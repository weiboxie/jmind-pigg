package jmind.pigg.mongodb;

import org.bson.conversions.Bson;

import java.util.List;
import java.util.Optional;


/**
 * https://www.programcreek.com/java-api-examples/index.php?source_dir=core-ng-project-master/core-ng/src/main/java/core/framework/impl/validate/type/TypeValidator.java
 https://www.programcreek.com/java-api-examples/index.php?source_dir=core-ng-project-master/core-ng/src/main/java/core/framework/api/mongo/Mongo.java
 * @author weibo.xwb
 */
public interface Mongo {
    <T> void insert(T entity);

    <T> Optional<T> get(Class<T> entityClass, Object id);

    <T> Optional<T> findOne(Class<T> entityClass, Bson filter);

    <T> List<T> find(Class<T> entityClass, Query query);

    <T> List<T> find(Class<T> entityClass, Bson filter);

    <T, V> List<V> aggregate(Class<T> entityClass, Class<V> resultClass, Bson... pipeline);

    <T> void update(T entity);

    <T> long update(Class<T> entityClass, Bson filter, Bson update);

    <T> void delete(Class<T> entityClass, Object id);

    <T> long delete(Class<T> entityClass, Bson filter);
}
