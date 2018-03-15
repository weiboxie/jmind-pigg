package jmind.pigg.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import jmind.pigg.mongodb.anno.*;
import org.bson.BsonDocument;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;


/**
 * Created by weibo.xwb on 2017/12/19.
 */
public final class MongoImpl implements Mongo {
    private final Logger logger = LoggerFactory.getLogger(MongoImpl.class);
    private final MongoClientOptions.Builder builder = MongoClientOptions.builder().socketKeepAlive(true);
    private final Map<Class<?>, Codec<?>> codecs = new HashMap();
    private final Map<Class, EntityIdHandler> idHandlers = new HashMap();
    private final MongoEntityValidator validator = new MongoEntityValidator();
    public MongoClient mongoClient;
    public int tooManyRowsReturnedThreshold = 2000;
    private MongoDatabase database;
    private long slowQueryThresholdInMs = Duration.ofSeconds(5).toMillis();
    private MongoClientURI uri;

    public void uri(String uri) {
        logger.info("set mongo uri, uri={}", uri);
        this.uri = new MongoClientURI(uri, builder);
        if (this.uri.getDatabase() == null) throw new Error("uri must have database, uri={}"+uri);
    }

    public void poolSize(int minSize, int maxSize) {
        builder.minConnectionsPerHost(minSize)
                .connectionsPerHost(maxSize);
    }

    public void timeout(Duration timeout) {
        builder.connectTimeout((int) timeout.toMillis()) // default is 10s
                .socketTimeout((int) timeout.toMillis());
    }

    public void close() {
        if (mongoClient != null) {
            logger.info("close mongodb client, database={}", database.getName());
            mongoClient.close();
        }
    }

    public <T> void entityClass(Class<T> entityClass) {
        validator.register(entityClass);
        EntityIdHandler<T> entityIdHandler = new EntityIdHandlerBuilder<>(entityClass).build();
        idHandlers.put(entityClass, entityIdHandler);
        registerCodec(entityClass, entityIdHandler);
    }

    public <T> void viewClass(Class<T> viewClass) {
        new MongoClassValidator(viewClass).validateViewClass();
        registerCodec(viewClass, null);
    }

    private <T> void registerCodec(Class<T> entityClass, EntityIdHandler<T> entityIdHandler) {
        EntityEncoder<T> entityEncoder = new EntityEncoderBuilder<>(entityClass).build();
        EntityDecoder<T> entityDecoder = new EntityDecoderBuilder<>(entityClass).build();
        EntityCodec<T> codec = new EntityCodec<>(entityClass, entityIdHandler, entityEncoder, entityDecoder);
        Codec<?> previous = codecs.putIfAbsent(entityClass, codec);
        if (previous != null)
            throw new Error("entity or view class is registered, class={}"+ entityClass.getCanonicalName());
    }

    public void slowQueryThreshold(Duration slowQueryThreshold) {
        slowQueryThresholdInMs = slowQueryThreshold.toMillis();
    }

    private MongoDatabase database() {
        if (mongoClient == null) {
            if (uri == null) throw new Error("uri() must be called before initialize");
            mongoClient = new MongoClient(uri);
        }

        if (database == null) {
            CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(),
                    CodecRegistries.fromCodecs(new ArrayList<>(codecs.values())));
            database = mongoClient.getDatabase(uri.getDatabase()).withCodecRegistry(codecRegistry);
        }

        return database;
    }

    @Override
    public <T> void insert(T entity) {
        try {
            validator.validate(entity);
            @SuppressWarnings("unchecked")
            MongoCollection<T> collection = collection((Class<T>) entity.getClass());
            collection.insertOne(entity);
        } finally {

        }
    }

    @Override
    public <T> Optional<T> get(Class<T> entityClass, Object id) {
        try {
            T result = collection(entityClass).find(Filters.eq("_id", id)).first();
            return Optional.ofNullable(result);
        } finally {

        }
    }

    @Override
    public <T> Optional<T> findOne(Class<T> entityClass, Bson filter) {
        try {
            FindIterable<T> query = collection(entityClass)
                    .find(filter == null ? new BsonDocument() : filter)
                    .limit(2);
            List<T> results =new ArrayList<>();
            for (T document : query) {
                results.add(document);
            }
            if (results.isEmpty()) return Optional.empty();
            return Optional.of(results.get(0));
        } finally {

        }
    }

    @Override
    public <T> List<T> find(Class<T> entityClass, Query query) {

        List<T> results = new ArrayList();
        try {
            FindIterable<T> collection = collection(entityClass)
                    .find(query.filter == null ? new BsonDocument() : query.filter);
            if (query.projection != null) collection.projection(query.projection);
            if (query.sort != null) collection.sort(query.sort);
            if (query.skip != null) collection.skip(query.skip);
            if (query.limit != null) collection.limit(query.limit);

            for (T document : collection) {
                results.add(document);
            }
            return results;
        } finally {

        }
    }

    @Override
    public <T> List<T> find(Class<T> entityClass, Bson filter) {
        List<T> results = new ArrayList();
        try {
            FindIterable<T> collection = collection(entityClass).find(filter == null ? new BsonDocument() : filter);
            for (T document : collection) {
                results.add(document);
            }
            return results;
        } finally {

        }
    }

    @Override
    public <T, V> List<V> aggregate(Class<T> entityClass, Class<V> resultClass, Bson... pipeline) {
        List<V> results = new ArrayList();
        ArrayList<Bson> list = new ArrayList<>();
       list.addAll(Arrays.asList(pipeline));
        try {
            MongoCollection<T> collection = collection(entityClass);
            AggregateIterable<V> documents = collection.aggregate(list, resultClass);
            for (V document : documents) {
                results.add(document);
            }
            return results;
        } finally {


            if (results.size() > tooManyRowsReturnedThreshold)
                logger.warn("too many rows returned, returnedRows={}", results.size());
        }
    }

    @Override
    public <T> void update(T entity) {

        try {
            validator.validate(entity);
            @SuppressWarnings("unchecked")
            Class<T> entityClass = (Class<T>) entity.getClass();
            Bson filter = idEqualsFilter(entity);
            collection(entityClass).replaceOne(filter, entity);
        } finally {

        }
    }

    @Override
    public <T> long update(Class<T> entityClass, Bson filter, Bson update) {
        try {
            UpdateResult result = collection(entityClass).updateMany(filter, update);
            return result.getModifiedCount();
        } finally {

        }
    }

    @Override
    public <T> void delete(Class<T> entityClass, Object id) {
        try {
            collection(entityClass).deleteOne(Filters.eq("_id", id));
        } finally {

        }
    }

    @Override
    public <T> long delete(Class<T> entityClass, Bson filter) {
        try {
            DeleteResult result = collection(entityClass).deleteMany(filter);
            return result.getDeletedCount();
        } finally {

        }
    }

    private <T> Bson idEqualsFilter(T entity) {
        @SuppressWarnings("unchecked")
        EntityIdHandler<T> idHandler = idHandlers.get(entity.getClass());
        return Filters.eq("_id", idHandler.get(entity));
    }

    private void checkSlowQuery(long elapsedTime) {
        if (elapsedTime > slowQueryThresholdInMs)
            logger.warn("slow query detected");
    }

    private <T> void checkTooManyRowsReturned(int size) {
        if (size > tooManyRowsReturnedThreshold)
            logger.warn("too many rows returned, returnedRows={}", size);
    }

    private <T> MongoCollection<T> collection(Class<T> entityClass) {
        jmind.pigg.mongodb.anno.Collection collection = entityClass.getDeclaredAnnotation(jmind.pigg.mongodb.anno.Collection.class);
        return database().getCollection(collection.name(), entityClass);
    }
}

