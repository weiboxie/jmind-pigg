package jmind.pigg.jdbc;

/**
 * Created by weibo.xwb on 2017/8/28.
 */
public class JdbcOperationsFactory {

    private static final JdbcOperations instance=new JdbcTemplate();

    public static final JdbcOperations getJdbcOperations(){
           return instance;
    }
}
