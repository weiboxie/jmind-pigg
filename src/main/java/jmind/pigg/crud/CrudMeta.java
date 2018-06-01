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

import jmind.base.annotation.Ignore;
import jmind.base.util.DataUtil;
import jmind.base.util.bean.BeanUtil;
import jmind.base.util.bean.PropertyMeta;
import jmind.pigg.annotation.Column;
import jmind.pigg.annotation.Id;
import jmind.pigg.operator.Pigg;
import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author xieweibo
 */
public class CrudMeta {

  private final List<String> properties;

  private final List<String> columns;

  private final Map<String, String> propertyToColumnMap;

  private final Map<String, Type> propertyToTypeMap;

  private final String propertyId;

  private final String columnId;

  private final boolean isAutoGenerateId;

  public CrudMeta(Class<?> clazz) {
    List<String> props = new ArrayList<String>();
    List<String> cols = new ArrayList<String>();
    Map<String, String> propToColMap = new HashMap<String, String>();
    HashMap<String, Type> propToTypeMap = new HashMap<String, Type>();
    String propId = null;
    String colId = null;
    Boolean autoGenerateId = null;
    for (PropertyMeta propertyMeta : BeanUtil.fetchPropertyMetas(clazz)) {
      Ignore igAnno = propertyMeta.getPropertyAnno(Ignore.class);
      if (igAnno != null) {
        continue;
      }
      String prop = propertyMeta.getName();
      Column colAnno = propertyMeta.getPropertyAnno(Column.class);
      String col = prop;
      if(colAnno!=null){
        col=colAnno.value();
      }else if(Pigg.getInstance().isUnderscoreName()){
         col=DataUtil.underscoreName(prop);
      }
      props.add(prop);
      cols.add(col);
      propToColMap.put(prop, col);
      propToTypeMap.put(prop, propertyMeta.getType());

      Id idAnno = propertyMeta.getPropertyAnno(Id.class);
      if (idAnno != null) {
        if (propId != null || colId != null) {
          throw new IllegalStateException("duplicate Id annotation");
        }
        propId = prop;
        colId = col;
        autoGenerateId = idAnno.autoGenerateId();
      }
    }
    if (autoGenerateId == null) {
      throw new IllegalStateException("need Id annotation on field to indicate primary key");
    }

    properties = Collections.unmodifiableList(props);
    columns = Collections.unmodifiableList(cols);
    propertyToColumnMap = Collections.unmodifiableMap(propToColMap);
    propertyToTypeMap = Collections.unmodifiableMap(propToTypeMap);
    propertyId = propId;
    columnId = colId;
    isAutoGenerateId = autoGenerateId;
  }

  public List<String> getProperties() {
    return properties;
  }

  public List<String> getColumns() {
    return columns;
  }

  @Nullable
  public String getColumnByProperty(String property) {
    return propertyToColumnMap.get(property);
  }

  @Nullable
  public Type getTypeByProperty(String property) {
    return propertyToTypeMap.get(property);
  }

  public String getPropertyId() {
    return propertyId;
  }

  public String getColumnId() {
    return columnId;
  }

  public boolean isAutoGenerateId() {
    return isAutoGenerateId;
  }

}
