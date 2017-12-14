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

package jmind.pigg.support.model4table;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import jmind.pigg.annotation.Id;
import jmind.pigg.support.Randoms;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xieweibo
 */
public class Msg {

  @Id
  private Integer id; // 自增id
  private int uid;
  private String content;
  private String userName;
  private int pid ;

  
  

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final Msg other = (Msg) obj;
    return Objects.equal(this.id, other.id)
        && Objects.equal(this.uid, other.uid)
        && Objects.equal(this.content, other.content);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("id", id).add("uid", uid).add("content", content).add("userNamre",userName).toString();
  }

  public static List<Msg> createRandomMsgs(int num) {
    List<Msg> msgs = new ArrayList<Msg>();
    for (int i = 0; i < num; i++) {
      msgs.add(createRandomMsg());
    }
    return msgs;
  }

  public static Msg createRandomMsg() {
    Msg msg = new Msg();
    msg.setUid(Randoms.randomInt(10000));
    msg.setContent(Randoms.randomString(20));
    return msg;
  }



  public int getUid() {
    return uid;
  }

  public void setUid(int uid) {
    this.uid = uid;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

public String getUserName() {
    return userName;
}

public void setUserName(String userName) {
    this.userName = userName;
}

public int getPid() {
    return pid;
}

public void setPid(int pid) {
    this.pid = pid;
}


  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }
}
