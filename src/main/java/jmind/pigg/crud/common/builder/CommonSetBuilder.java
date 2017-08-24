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

import java.util.ArrayList;
import java.util.List;

import jmind.pigg.crud.Builder;

/**
 * @author xieweibo
 */
public class CommonSetBuilder implements Builder {

    private final static String SQL_TEMPLATE = "update #table set %s where %s";

    private final String propertyId;

    private final String columnId;

    private final List<String> properties;

    private final List<String> columns;

    public CommonSetBuilder(String propId, List<String> props, List<String> cols) {
        int index = props.indexOf(propId);
        if (index < 0) {
            throw new IllegalArgumentException("error property id [" + propId + "]");
        }
        propertyId = propId;
        properties = new ArrayList<String>(props);
        columns = new ArrayList<String>(cols);
        columnId = columns.remove(index);
        properties.remove(index);
    }

    @Override
    public String buildSql() {
        String keyCol = columnId + " = :" + propertyId;
        StringBuilder exps = new StringBuilder(keyCol);
        for (int i = 0; i < properties.size(); i++) {
            exps.append(" #if(:" + properties.get(i) + ") ," + columns.get(i) + " = :" + properties.get(i) + " #end ");
        }
        return String.format(SQL_TEMPLATE, exps.toString(), keyCol);
    }

}
