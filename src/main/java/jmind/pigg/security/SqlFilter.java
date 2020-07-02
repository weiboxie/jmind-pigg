package jmind.pigg.security;

/**
 * description:
 *
 * @author weibo.xie
 * @date : create in 2:21 下午 2020/7/2
 */
public interface SqlFilter {

    /**
     * SQL注入过滤函数，当程序需要将用户输入作为sql语句的一部分插入
     * sql语句中进行查询时，用户输入需要经过本函数过滤。原则上，实现
     * 参考 php的 mysql_real_escape_string，将\x00, \n, \r, \, ', "
     * and \x1a 加反斜杠转义。
     *
     * @param unescapedString
     * @return
     */
    String mysqlRealEscapeString(String unescapedString);

    /**
     * 严格限制用户输入只能包含<code>a-zA-Z0-9_-.</code>字符
     *
     * @param sql
     * @return
     */
    String trimSql(String sql);

    /**
     * 严格限制用户输入只能包含<code>a-zA-Z0-9_-. ,</code>字符
     *  比 trimSql 包含 空格和逗号
     * @param sql
     * @return
     */
    String safeSql(String sql);

}
