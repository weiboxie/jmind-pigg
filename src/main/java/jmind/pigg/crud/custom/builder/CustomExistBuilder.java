package jmind.pigg.crud.custom.builder;

/**
 * description:
 *
 * @author weibo.xie
 * @date : create in 11:20 上午 2021/3/23
 */
public class CustomExistBuilder extends AbstractCustomBuilder {

    private final static String SQL_TEMPLATE = "select 1 from #table %s limit 1";

    private final String tailOfSql;

    public CustomExistBuilder(String tailOfSql) {
        this.tailOfSql = tailOfSql;
    }

    @Override
    public String buildSql() {
        return String.format(SQL_TEMPLATE, tailOfSql);
    }

}