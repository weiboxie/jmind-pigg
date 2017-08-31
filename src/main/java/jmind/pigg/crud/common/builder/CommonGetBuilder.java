/*
 *  
 *
 * The jmind-pigg Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package jmind.pigg.crud.common.builder;

import jmind.base.util.DataUtil;
import jmind.base.util.GlobalConstants;
import jmind.pigg.crud.Builder;

import java.util.List;

/**
 * @author xieweibo
 */
public class CommonGetBuilder implements Builder {

    private final static String SQL_TEMPLATE = "select %s from #table where %s = :1 limit 1";
    private final static String BATCH_SQL_TEMPLATE = "select %s from #table where %s in (:1)";

    private final String SQL;

    public CommonGetBuilder(String colId, List<String> cols, boolean isBatch) {
        int index = cols.indexOf(colId);
        if (index < 0) {
            throw new IllegalArgumentException("error column id [" + colId + "]");
        }
        String s1 = DataUtil.join(cols, GlobalConstants.COMMA);
        SQL = isBatch ? String.format(BATCH_SQL_TEMPLATE, s1, colId) : String.format(SQL_TEMPLATE, s1, colId);
    }

    @Override
    public String buildSql() {
        return SQL;
    }
}
