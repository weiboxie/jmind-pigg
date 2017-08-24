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

import java.lang.reflect.Type;
import java.util.List;

import javax.annotation.Nullable;

import jmind.pigg.crud.Builder;
import jmind.pigg.crud.BuilderFactory;
import jmind.pigg.crud.CrudMeta;

/**
 * @author xieweibo
 */
public abstract class AbstractCommonBuilderFactory extends BuilderFactory {

  @Nullable
  @Override
  public Builder doTryGetBuilder(String name, Type returnType, List<Type> parameterTypes, Class<?> entityClass, Class<?> idClass) {
    return (nameMatched(name) &&
        returnTypeMatched(returnType, entityClass) &&
        parameterTypesMatched(parameterTypes, entityClass, idClass)) ?
        createCommonBuilder(new CrudMeta(entityClass)) :
        null;
  }

  private boolean nameMatched(String actualName) {
    return expectedMethodName().equals(actualName);
  }

  private boolean returnTypeMatched(Type returnType, Class<?> entityClass) {
    return expectedReturnType(entityClass).equals(returnType);
  }

  private boolean parameterTypesMatched(List<Type> parameterTypes, Class<?> entityClass, Class<?> idClass) {
    return expectedParameterType(entityClass, idClass).equals(parameterTypes);
  }

  abstract String expectedMethodName();

  abstract Type expectedReturnType(Class<?> entityClass);

  abstract List<Type> expectedParameterType(Class<?> entityClass, Class<?> idClass);

  abstract Builder createCommonBuilder(CrudMeta cm);

}
