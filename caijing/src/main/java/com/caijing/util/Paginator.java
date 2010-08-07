package com.caijing.util;

import java.io.Serializable;
import java.util.List;

/**
 * 分页数据包装器，将数据列表，当前页，页数，页面大小和记录数都包装在内的辅助类
 * 
 * 此代码可以通用
 * @author Raymond,Herbert
 * 
 * @param <T> 存放的对象类型
 */
public class Paginator<T> implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * 
   */
  public Paginator() {
  };

  /**
   * 当前页面的数据列表
   */
  private List<T> pageContentList;
  /**
   * 页面的记录条数
   */
  private int pageSize;
  /**
   * 总记录数
   */
  private int totalRecordNumber;
  /**
   * 当前的页码
   */
  private int currentPageNumber;

  /**
   * 用来构造分页列表的url模式串
   */
  private String url;

  /**
   * 获取该页的数据内容列表
   * @return 列表，内容是实际要用的对象
   */
  public List<T> getPageContentList() {
    return pageContentList;
  }

  /**
   * 设置列表对象
   * @param pageContentList
   */
  public void setPageContentList(final List<T> pageContentList) {
    this.pageContentList = pageContentList;
  }

  /**
   * 设置当前页码（返回前必须调用）
   * @param currentPageNumber 当前页码
   */
  public void setCurrentPageNumber(final int currentPageNumber) {
    if (currentPageNumber == 0) {
      this.currentPageNumber = 1;
    } else {
      this.currentPageNumber = currentPageNumber;
    }
  }

  /**
   * 设置页面大小，返回去必须调用
   * @param pageSize 页面大小
   */
  public void setPageSize(final int pageSize) {
    this.pageSize = pageSize;
  }

  // 必须调用
  /**
   * 设置总记录数（各页总和），返回前必须调用
   * @param totalRecordNumber
   */
  public void setTotalRecordNumber(final int totalRecordNumber) {
    this.totalRecordNumber = totalRecordNumber;
  }

  /**
   * 获取页面大小
   * @return 页面大小
   */
  public int getPageSize() {
    return pageSize;
  }

  /**
   * 获取总记录数
   * @return 总记录数
   */
  public int getTotalRecordNumber() {
    return totalRecordNumber;
  }

  /**
   * 获取总页数
   * @return 总页数
   */
  public int getTotalPageNumber() {
    final double x = totalRecordNumber;
    final double y = pageSize;
    final int z = (int) Math.ceil(x / y);
    return z;
  }

  /**
   * 获取每页都是满的总页数
   * @return 总页数
   */
  public int getTotalPageNumberIsFull() {
    final double x = totalRecordNumber;
    final double y = pageSize;
    final int z = (int) Math.floor(x / y);
    return z;
  }

  /**
   * 获取当前页码
   * @return 当前页码
   */
  public int getCurrentPageNumber() {
    if ((currentPageNumber > this.getTotalPageNumber()) || (currentPageNumber < 1)) {
      return 1;
    } else {
      return currentPageNumber;
    }
  }

  /**
   * 获取起始记录位置
   * @return 起始记录位置
   */
  public int getStartOffset() {
    final int temp = (this.getCurrentPageNumber() - 1) * pageSize;
    return temp;
  }

  /**
   * 设置构造分页列表用的URL模式，其中页码的值的位置用字符串$number$标记
   * @param url URL模式
   */
  public void setUrl(final String url) {
    this.url = url;
  }

  /**
   * 获取分页列表的HTML代码。如果url未曾设置过，返回空串。
   * @author Herbert
   * @return 分页的整个列表的HTML代码
   */
  public String getPageNumberList() {
    if (url == null) {
      return "";
    }
    String a = "<span class=\"current-page\">$number$</span>\r\n";
    String b = "<a href=\"$url$\">$number$</a>\r\n";
    a = a.replace("$url$", url);
    b = b.replace("$url$", url);
    int startNum = 1;
    int endNum = 1;
    final StringBuffer sb = new StringBuffer();
    final int totalPageNumber = this.getTotalPageNumber();
    if (totalPageNumber <= 1) {
      return sb.toString();
    } else {
      //上一页
      if (currentPageNumber > 1) {
        sb.append("<a class=\"prev-page\" href=\"$url$\">上一页</a>\r\n".replace("$url$", url).replace("$number$",
            (currentPageNumber - 1) + ""));
      }
      //得到页码起始
      if (currentPageNumber - 7 <= 1) {
        startNum = 1;
      } else {
        startNum = currentPageNumber - 6;
      }
      //得到页码结束
      if (currentPageNumber + 7 >= totalPageNumber) {
        endNum = totalPageNumber;
      } else {
        endNum = currentPageNumber + 6;
      }
      //循环中间的页码
      for (int i = startNum; i <= endNum; i++) {
        if (i == currentPageNumber) {
          sb.append(a.replace("$number$", i + ""));
        } else {
          sb.append(b.replace("$number$", i + ""));
        }
      }
      //下一页
      if (currentPageNumber < totalPageNumber) {
        sb.append("<a class=\"next-page\" href=\"$url$\">下一页</a>\r\n".replace("$url$", url).replace("$number$",
            (currentPageNumber + 1) + ""));
      }
      return sb.toString();
    }
  }

  /**
   * 专供博客组件使用的分页逻辑
   * 当前页的前面有两个页面，后面有两个页码。如果第一页没有显示出来就显示“首页”，如果最后一页没有显示出来就显示“末页”
   * @author Herbert
   * @return
   */
  public String getPageNumberListForBlog() {
    if (url == null) {
      return "";
    }
    String a = "<a href=\"$url$\" class=\"on active\">$number$</a>\r\n";
    String b = "<a href=\"$url$\">$number$</a>\r\n";
    a = a.replace("$url$", url);
    b = b.replace("$url$", url);
    int startNum = 1;
    int endNum = 1;
    final StringBuffer sb = new StringBuffer();
    final int totalPageNumber = this.getTotalPageNumber();
    if (totalPageNumber <= 1) {
      return sb.toString();
    } else {
      if (currentPageNumber >= 4) {
        sb.append("<a href=\"$url$\">首页</a>\r\n".replace("$url$", url).replace("$number$", "1"));
      }
      //上一页
      if (currentPageNumber > 1) {
        sb.append("<a class=\"pre\" href=\"$url$\">上一页</a>\r\n".replace("$url$", url).replace(
            "$number$", (currentPageNumber - 1) + ""));
      }
      //得到页码起始
      if (currentPageNumber - 3 < 1) {
        startNum = 1;
      } else {
        startNum = currentPageNumber - 2;
      }
      //得到页码结束
      if (currentPageNumber + 3 > totalPageNumber) {
        endNum = totalPageNumber;
      } else {
        endNum = currentPageNumber + 2;
      }
      //循环中间的页码
      for (int i = startNum; i <= endNum; i++) {
        if (i == currentPageNumber) {
          sb.append(a.replace("$number$", i + ""));
        } else {
          sb.append(b.replace("$number$", i + ""));
        }
      }
      //下一页
      if (currentPageNumber < totalPageNumber) {
        sb.append("<a class=\"next\" href=\"$url$\">下一页</a>\r\n".replace("$url$", url).replace(
            "$number$", (currentPageNumber + 1) + ""));
      }
      //此条件和下一页的显示条件一样，没有下一页就没有末页显示
      if (endNum < totalPageNumber) {
        sb.append("<a href=\"$url$\">末页</a>\r\n".replace("$url$", url).replace("$number$",
            this.getTotalPageNumber() + ""));
      }
      return sb.toString();
    }
  }
}