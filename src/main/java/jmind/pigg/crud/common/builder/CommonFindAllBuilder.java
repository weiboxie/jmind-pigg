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

import java.util.List;

import jmind.base.util.DataUtil;
import jmind.base.util.GlobalConstants;
import jmind.pigg.crud.Builder;


/**
 * @author xieweibo
 */
public class CommonFindAllBuilder implements Builder {

    private final String SQL;

    public CommonFindAllBuilder(List<String> cols) {
        String s1 = DataUtil.join(cols, GlobalConstants.COMMA);
        SQL = String.format("select %s from #table", s1, s1);
    }

    @Override
    public String buildSql() {
        return SQL;
    }

}
