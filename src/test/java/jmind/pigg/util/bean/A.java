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

package jmind.pigg.util.bean;

import javax.annotation.Nullable;

import jmind.pigg.annotation.Column;
import jmind.pigg.annotation.Getter;
import jmind.pigg.annotation.ID;
import jmind.pigg.annotation.Setter;
import jmind.pigg.invoker.function.IntArrayToStringFunction;
import jmind.pigg.invoker.function.StringToIntArrayFunction;

/**
 * @author xieweibo
 */
public class A {

  @ID
  private int id;

  @Column("user_id")
  @Nullable
  private int uid;

  private int age;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Getter(IntArrayToStringFunction.class)
  public int getUid() {
    return uid;
  }

  @Setter(StringToIntArrayFunction.class)
  public void setUid(int uid) {
    this.uid = uid;
  }

  public String getName() {
    return "";
  }

  public void setName(String name) {
  }

  public int getAge() {
    return age;
  }

}
