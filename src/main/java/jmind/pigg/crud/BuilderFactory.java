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

package jmind.pigg.crud;

import javax.annotation.Nullable;

import jmind.pigg.descriptor.Generic;
import jmind.pigg.descriptor.MethodDescriptor;
import jmind.pigg.descriptor.ParameterDescriptor;
import jmind.pigg.util.reflect.TokenTuple;
import jmind.pigg.util.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xieweibo
 */
public abstract class BuilderFactory {

  @Nullable
  public Builder tryGetBuilder(MethodDescriptor md) {
    String name = md.getName();
    Class<?> daoClass = md.getDaoClass();
    if (TypeToken.of(Generic.class).isAssignableFrom(daoClass)) {
      TypeToken<?> daoToken = TypeToken.of(daoClass);
      TokenTuple tuple = daoToken.resolveFatherClassTuple(Generic.class);
      Class<?> entityClass = tuple.getFirst().getRawType();
      Class<?> idClass = tuple.getSecond().getRawType();
      List<Type> types = new ArrayList<Type>();
      for (ParameterDescriptor pd : md.getParameterDescriptors()) {
        types.add(pd.getType());
      }
      return doTryGetBuilder(name, md.getReturnType(), types, entityClass, idClass);
    }
    return null;
  }

  @Nullable
  public abstract Builder doTryGetBuilder(
      String name, Type returnType, List<Type> parameterTypes, Class<?> entityClass, Class<?> idClass);

}

