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

package jmind.pigg.exception;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import jmind.pigg.annotation.Cache;
import jmind.pigg.annotation.DB;
import jmind.pigg.annotation.SQL;
import jmind.pigg.operator.Pigg;
import jmind.pigg.operator.cache.LocalCacheHandler;
import jmind.pigg.support.DataSourceConfig;

/**
 * 测试{@link IncorrectAnnotationException}
 *
 * @author xieweibo
 */
public class IllegalStateExceptionTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void test() {
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("dao interface expected one @DB annotation but not found");
    Pigg pigg = Pigg.newInstance(DataSourceConfig.getDataSource());
    pigg.setLazyInit(true);
    pigg.create(Dao.class);
  }

  @Test
  public void test2() {
    thrown.expect(DescriptionException.class);
    thrown.expectMessage("each method expected one of @SQL or @UseSqlGenerator annotation but not found");
    Pigg pigg = Pigg.newInstance(DataSourceConfig.getDataSource());
    pigg.setLazyInit(true);
    pigg.setCacheHandler(new LocalCacheHandler());
    Dao2 dao = pigg.create(Dao2.class);
    dao.add();
  }

  @Test
  public void test3() {
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("if use cache, each method expected one or more " +
        "@CacheBy annotation on parameter but found 0");
    Pigg pigg = Pigg.newInstance(DataSourceConfig.getDataSource());
    pigg.setLazyInit(true);
    pigg.setCacheHandler(new LocalCacheHandler());
    Dao3 dao = pigg.create(Dao3.class);
    dao.add();
  }

  static interface Dao {
  }

  @DB
  static interface Dao2 {
    public int add();
  }

  @DB
  @Cache(prefix = "dao3_", expire = 100)
  static interface Dao3 {
    @SQL("insert into ...")
    public int add();
  }

}
