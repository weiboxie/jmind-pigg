package jmind.pigg.security;

/**
 * SQL注入威胁过滤函数。
 * <p>
 * <p>
 * 当程序需要将用户输入作为sql语句的一部分插入sql语句中进行查询时，
 * 用户输入需要经过本函数过滤。
 * <p>
 *
 * @author weiboxie
 */
public class SqlFilterImpl implements SqlFilter {

    private static final int SIZE = 128;
    private static final CharMasks MASKS = new CharMasks(SIZE) {
        {
            addCharToMasks("\\\0\n\r'\"");
        }
    };
    private static final boolean[] ESCAPE_CMD_MASKS = MASKS.getMasks();
    private static final char BOUND = 0x1a;
    private static final String ESCAPED_BOUND = "0x1a";

    @Override
    public String mysqlRealEscapeString(String unescapedString) {
        return filterStatically(unescapedString);
    }

    private static final CharMasks FILTER_ORDER_BY_MASKS = new CharMasks(SIZE) {
        {

            // 0 ~ 9
            for (int i = 48; i < 58; i++) {
                addCharToMasks((char) i);
            }

            // A ~ Z
            for (int i = 65; i < 91; i++) {
                addCharToMasks((char) i);
            }

            // a ~ z
            for (int i = 97; i < 123; i++) {
                addCharToMasks((char) i);
            }

            addCharToMasks("_-.");

        }
    };

    private static final CharMasks FILTER_ORDER_BY_MASKS_KONG = new CharMasks(SIZE) {
        {

            // 0 ~ 9
            for (int i = 48; i < 58; i++) {
                addCharToMasks((char) i);
            }

            // A ~ Z
            for (int i = 65; i < 91; i++) {
                addCharToMasks((char) i);
            }

            // a ~ z
            for (int i = 97; i < 123; i++) {
                addCharToMasks((char) i);
            }

            addCharToMasks("_-. ,");

        }
    };

    @Override
    public String trimSql(String columnName) {
        return trimSqlStatically(columnName,FILTER_ORDER_BY_MASKS);
    }

    @Override
    public String safeSql(String sql) {
        return trimSqlStatically(sql,FILTER_ORDER_BY_MASKS_KONG);
    }

    // ***********************************************************************************
    // 静态化实现，保证可以线程安全地直接使用
    // ***********************************************************************************


    static String trimSqlStatically(String columnName,CharMasks charMasks) {

        if (columnName == null || columnName.isEmpty()) {
            return columnName;
        }

        StringBuilder sb = new StringBuilder();
        int lastPos = 0;
        for (int i = 0; i < columnName.length(); i++) {
            char c = columnName.charAt(i);
            if (c >= SIZE || !charMasks.getMasks()[c]) {
                sb.append(columnName.substring(lastPos, i));
                lastPos = i + 1;
            }
        }

        return sb.append(columnName.substring(lastPos)).toString();

    }

    static String filterStatically(String sql) {

        if (sql == null || sql.isEmpty()) {
            return sql;
        }

        StringBuilder sb = new StringBuilder();
        int lastPos = 0;
        for (int i = 0; i < sql.length(); i++) {
            char c = sql.charAt(i);
            if (c < SIZE && ESCAPE_CMD_MASKS[c]) {
                sb.append(sql.substring(lastPos, i));
                sb.append('\\').append(c);
                lastPos = i + 1;
            } else if (c == BOUND) {
                sb.append(sql.substring(lastPos, i)).append(ESCAPED_BOUND);
                lastPos = i + 1;
            }
        }
        return sb.append(sql.substring(lastPos)).toString();
    }



}
