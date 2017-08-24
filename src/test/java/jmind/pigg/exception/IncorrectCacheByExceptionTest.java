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

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import jmind.pigg.annotation.Cache;
import jmind.pigg.annotation.CacheBy;
import jmind.pigg.annotation.DB;
import jmind.pigg.annotation.SQL;
import jmind.pigg.operator.Pigg;
import jmind.pigg.operator.cache.IncorrectCacheByException;
import jmind.pigg.operator.cache.LocalCacheHandler;
import jmind.pigg.support.DataSourceConfig;

/**
 * 测试{@link jmind.pigg.operator.cache.IncorrectCacheByException}
 *
 * @author xieweibo
 */
public class IncorrectCacheByExceptionTest {

  private final static Pigg pigg = Pigg.newInstance(DataSourceConfig.getDataSource());

  static { 
    pigg.setLazyInit(true);
    pigg.setCacheHandler(new LocalCacheHandler());
  }

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void test() {
    thrown.expect(IncorrectCacheByException.class);
    thrown.expectMessage("CacheBy :2 can't match any db parameter");
    Dao dao = pigg.create(Dao.class);
    dao.add(1, 2);
  }

  @Test
  public void test2() {
    thrown.expect(IncorrectCacheByException.class);
    thrown.expectMessage("CacheBy :1 can't match any db parameter");
    Dao dao = pigg.create(Dao.class);
    dao.batchAdd(new ArrayList<Integer>());
  }

  @DB
  @Cache(prefix = "dao_", expire = 100)
  static interface Dao {
    @SQL("insert into ${1 + :1} ...")
    public int add(int a, @CacheBy int b);

    @SQL("insert into ...")
    public int[] batchAdd(@CacheBy List<Integer> ids);
  }

}
