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

import jmind.pigg.annotation.GeneratedId;
import jmind.pigg.annotation.UseSqlGenerator;
import jmind.pigg.descriptor.Generic;
import jmind.pigg.plugin.page.Page;

import java.util.Collection;
import java.util.List;

/**
 * @author xieweibo
 */
@UseSqlGenerator(CrudSqlGenerator.class)
public interface CrudRepository<T, ID> extends Generic<T, ID> {
    T findOne(ID id);
    List<T> findAll();
    List<T> findAll(Collection<ID> ids);


    List<T> findAll(Page page);
    long count();

  int save(T entity);

  @GeneratedId
  int saveAndGeneratedId(T entity);

  void save(Collection<T> entities);

    /**
     *   update 和set 区别 。update 修改所有字段 ，set 修改非 null 属性字段
      * @param entity
     * @return
     */
  int update(T entity);
  int update(T entity,T wheres);
  int[] update(Collection<T> entities);

    /**
     * update 和set 区别 。update 修改所有字段 ，set 修改非 null 属性字段
     * @param entity
     * @return
     */
  int set(T entity); 

  int[] set(Collection<T> entities);
  
  int delete(ID id);
  // 目前此接口只支持mysql
  int replace(T entity);

}
