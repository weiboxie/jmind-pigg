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

package jmind.pigg.plugin.page;

/**
 * @author xieweibo
 */
public class Page {

  private boolean isFetchTotal = true;
  /**
   * 页码，从1开始
   */
  private int pageNum;
  /**
   * 页面大小
   */
  private int pageSize;

  private int total;
  private String groupBy ;
  private String OderBy ;

  public String getGroupBy() {
    return groupBy;
}

public void setGroupBy(String groupBy) {
    this.groupBy = groupBy;
}

public String getOderBy() {
    return OderBy;
}

public void setOderBy(String oderBy) {
    OderBy = oderBy;
}

  public Page() {
  }


  public static Page create(int pageNum, int pageSize) {
    Page page= new Page();
    page.setPageNum(pageNum);
    page.setPageSize(pageSize);
    return page;
  }

  public boolean isFetchTotal() {
    return isFetchTotal;
  }

  public void setFetchTotal(boolean fetchTotal) {
    isFetchTotal = fetchTotal;
  }

  public int getPageNum() {
    return pageNum;
  }

  public void setPageNum(int pageNum) {
    this.pageNum = pageNum;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }
}
