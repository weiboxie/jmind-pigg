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

  // 是否自动查询页面总数，默认true
  private boolean isFetchTotal = true;
  /**
   * 页码，从1开始
   */
  private int page=1;
  /**
   * 页面大小
   */
  private int pageSize=20;

  private long totalNum;
  private String groupBy ;
  private String orderBy;
  // 查询结果集
  private Object result ;


  public String getGroupBy() {
    return groupBy;
}

public void setGroupBy(String groupBy) {
    this.groupBy = groupBy;
}

  public String getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(String orderBy) {
    this.orderBy = orderBy;
  }

  public Page() {
  }


  public static Page create(int pageNum, int pageSize) {
    Page page= new Page();
    page.setPage(pageNum);
    page.setPageSize(pageSize);
    return page;
  }

  public boolean isFetchTotal() {
    return isFetchTotal;
  }

  public void setFetchTotal(boolean fetchTotal) {
    isFetchTotal = fetchTotal;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public long getTotalNum() {
    return totalNum;
  }

  public void setTotalNum(long totalNum) {
    this.totalNum = totalNum;
  }

  public Object getResult() {
    return result;
  }

  public void setResult(Object result) {
    this.result = result;
  }

  public int getStart(){
    return (page-1)*pageSize;
  }
}
