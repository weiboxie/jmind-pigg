package jmind.pigg.mongodb;

import org.bson.conversions.Bson;

/**
 * Created by weibo.xwb on 2017/12/19.
 */
public final class Query {
    public Bson filter;
    public Bson projection;
    public Bson sort;
    public Integer skip;
    public Integer limit;
}