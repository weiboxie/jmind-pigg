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

package jmind.pigg.crud.common.factory;

import jmind.base.util.reflect.DynamicTokens;
import jmind.base.util.reflect.TypeToken;
import jmind.pigg.crud.Builder;
import jmind.pigg.crud.CrudMeta;
import jmind.pigg.crud.common.builder.CommonUpdateBuilder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * @author xieweibo
 */
public class CommonBatchUpdateBuilderFactory extends AbstractCommonBuilderFactory {

    @Override
    String expectedMethodName() {
        return "update";
    }

    @Override
    Type expectedReturnType(Class<?> entityClass) {
        return int[].class;
    }

    @Override
    List<Type> expectedParameterType(Class<?> entityClass, Class<?> idClass) {
        List<Type> types = new ArrayList<Type>();
        types.add(DynamicTokens.collectionToken(TypeToken.of(entityClass)).getType());
        return types;
    }

    @Override
    Builder createCommonBuilder(CrudMeta cm) {
        return new CommonUpdateBuilder(cm.getPropertyId(), cm.getProperties(), cm.getColumns());
    }

}
