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

import com.google.common.collect.Lists;

import jmind.pigg.plugin.spring.PiggDaoScanner;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author xieweibo
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/plugin/spring/applicationContext.xml")
@Ignore
public class PiggDaoScannerTest {

  @Autowired
  TestDao testDao;

  @Test
  public void testSetPackages() throws Exception {
    PiggDaoScanner mc = new PiggDaoScanner();
    List<String> packages = Lists.newArrayList("jmind.pigg", "", "org.jfaster");
    mc.setPackages(packages);
    assertThat(mc.locationPatterns.get(0), is("classpath*:org/jfaster/pigg/**/*Dao.class"));
    assertThat(mc.locationPatterns.get(1), is("classpath*:org/jfaster/pigg/**/*DAO.class"));
    assertThat(mc.locationPatterns.get(2), is("classpath*:/**/*Dao.class"));
    assertThat(mc.locationPatterns.get(3), is("classpath*:/**/*DAO.class"));
    assertThat(mc.locationPatterns.get(4), is("classpath*:org/jfaster/**/*Dao.class"));
    assertThat(mc.locationPatterns.get(5), is("classpath*:org/jfaster/**/*DAO.class"));
  }

  @Test
  public void testNotNull() {
    assertThat(testDao, notNullValue());
  }

}
