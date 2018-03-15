package jmind.pigg.mongodb;


import org.bson.BsonReader;

/**
 * Created by weibo.xwb on 2017/12/19.
 */
public interface EntityDecoder<T> {
    T decode(BsonReader reader);
}
