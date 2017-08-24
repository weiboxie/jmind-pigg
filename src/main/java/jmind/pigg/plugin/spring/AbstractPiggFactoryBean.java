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

package jmind.pigg.plugin.spring;

import org.springframework.beans.factory.FactoryBean;

import jmind.pigg.operator.Pigg;

/**
 * @author xieweibo
 */
public abstract class AbstractPiggFactoryBean implements FactoryBean {

  private Class<?> daoClass;

  private static volatile Pigg pigg;

  public abstract Pigg createPigg();

  public Object createDao(Pigg pigg, Class<?> daoClass) {
    return pigg.create(daoClass);
  }

  @Override
  public Object getObject() throws Exception {
    Pigg pigg = getPigg();
    return pigg.create(daoClass);
  }

  @Override
  public Class<?> getObjectType() {
    return daoClass;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }

  public Pigg getPigg() {
    if (pigg == null) {
      synchronized (AbstractPiggFactoryBean.class) {
        if (pigg == null) {
          pigg = createPigg();
        }
      }
    }
    return pigg;
  }

  public void setDaoClass(Class<?> daoClass) {
    this.daoClass = daoClass;
  }

}
