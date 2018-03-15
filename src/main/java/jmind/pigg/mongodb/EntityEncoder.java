package jmind.pigg.mongodb;

import org.bson.BsonWriter;

/**
 * @author neo
 */
public interface EntityEncoder<T> {
    void encode(BsonWriter writer, T entity);
}
