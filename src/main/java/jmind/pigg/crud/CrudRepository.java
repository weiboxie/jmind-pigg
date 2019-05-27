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
 * save  相当于 insert
 * saveAndGeneratedId 相当于 insert，返回主键id
 * update 和updateAllField 区别 。updateAllField 修改所有字段 ，update 修改非 null 属性字段
 * 支持 countBy ，deleteBy，removeBy，getBy，findBy
 * 一般 getBy返回单个对象，findBy返回list 这种命名规则
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
     * update，updateNotNull，updateAllField 区别 。
     * updateAllField 修改所有字段 ，
     * updateNotNull 修改非 null 属性字段
     * update  修改 非null ，非空字段属性
     */
    int update(T entity);
    int[] update(Collection<T> entities);
    /**
     * update，updateNotNull，updateAllField 区别 。
     * updateAllField 修改所有字段 ，
     * updateNotNull 修改非 null 属性字段
     * update  修改 非null ，非空字段属性
     */
    int updateAllField(T entity);
    int[] updateAllField(Collection<T> entities);

    /**
     *  修改非null属性
     * @param entity
     * @return
     */
    int updateNotNull(T entity);
    int[] updateNotNull(Collection<T> entities);
    int delete(ID id);
    // 目前此接口只支持mysql
    int replace(T entity);

}
