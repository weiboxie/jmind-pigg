package jmind.pigg.mongodb;

/**
 * Created by weibo.xwb on 2017/12/19.
 */
public interface EntityIdHandler<T> {
    Object get(T entity);

    void set(T entity, Object id);

    boolean generateIdIfAbsent();
}
