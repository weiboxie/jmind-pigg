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

package jmind.pigg.crud.custom.factory;

import jmind.pigg.crud.CrudMeta;
import jmind.pigg.crud.custom.builder.AbstractCustomBuilder;
import jmind.pigg.crud.custom.builder.CustomDeleteBuilder;
import jmind.pigg.crud.custom.builder.CustomExistBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xieweibo
 */
public class CustomExistBuilderFactory extends AbstractCustomBuilderFactory {

  private final static List<String> PREFIXS = new ArrayList<String>();
  static {
    PREFIXS.add("existBy");
  }

  @Override
  public List<String> prefixs() {
    return PREFIXS;
  }

  @Override
  AbstractCustomBuilder createCustomBuilder(CrudMeta cm, String tailOfSql) {
    return new CustomExistBuilder(tailOfSql);
  }

}
