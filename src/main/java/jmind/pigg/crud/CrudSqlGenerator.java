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

import java.util.ArrayList;
import java.util.List;

import jmind.pigg.crud.common.factory.*;
import jmind.pigg.crud.custom.factory.CustomCountBuilderFactory;
import jmind.pigg.crud.custom.factory.CustomDeleteBuilderFactory;
import jmind.pigg.crud.custom.factory.CustomQueryBuilderFactory;
import jmind.pigg.descriptor.MethodDescriptor;
import jmind.pigg.descriptor.SqlGenerator;

/**
 * @author xieweibo
 */
public class CrudSqlGenerator implements SqlGenerator {

  private static final List<BuilderFactory> commonBuilderFactories = new ArrayList<BuilderFactory>();
  private static final List<BuilderFactory> customBuilderFactories = new ArrayList<BuilderFactory>();
  static {
    commonBuilderFactories.add(new CommonSaveBuilderFactory());
    commonBuilderFactories.add(new CommonSaveAndGeneratedIdBuilderFactory());
    commonBuilderFactories.add(new CommonBatchSaveBuilderFactory());
    commonBuilderFactories.add(new CommonFindOneBuilderFactory());
    commonBuilderFactories.add(new CommonGetMultiBuilderFactory());
    commonBuilderFactories.add(new CommonFindAllBuilderFactory());
    commonBuilderFactories.add(new CommonCountBuilderFactory());
    commonBuilderFactories.add(new CommonUpdateBuilderFactory());
    commonBuilderFactories.add(new CommonBatchUpdateBuilderFactory());
    commonBuilderFactories.add(new CommonDeleteBuilderFactory());
    
    commonBuilderFactories.add(new CommonSetBuilderFactory());
    commonBuilderFactories.add(new CommonBatchSetBuilderFactory());

    customBuilderFactories.add(new CustomQueryBuilderFactory());
    customBuilderFactories.add(new CustomCountBuilderFactory());
    customBuilderFactories.add(new CustomDeleteBuilderFactory());
  }

  @Override
  public String generateSql(MethodDescriptor md) {
    return getBuilder(md).buildSql();
  }

  private Builder getBuilder(MethodDescriptor md) {
    for (BuilderFactory commonBuilderFactory : commonBuilderFactories) {
      Builder builder = commonBuilderFactory.tryGetBuilder(md);
      if (builder != null) {
        return builder;
      }
    }
    for (BuilderFactory lookupBuilderFactory : customBuilderFactories) {
      Builder builder = lookupBuilderFactory.tryGetBuilder(md);
      if (builder != null) {
        return builder;
      }
    }
    throw new CrudException("can't convert method [" + md.getName() + "] to SQL");
  }

}
