package com.caijing.util;

import java.io.Serializable;
import java.util.List;

/**
 * ��ҳ���ݰ�װ�����������б���ǰҳ��ҳ����ҳ���С�ͼ�¼������װ���ڵĸ�����
 * 
 * �˴������ͨ��
 * @author Raymond,Herbert
 * 
 * @param <T> ��ŵĶ�������
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
   * ��ǰҳ��������б�
   */
  private List<T> pageContentList;
  /**
   * ҳ��ļ�¼����
   */
  private int pageSize;
  /**
   * �ܼ�¼��
   */
  private int totalRecordNumber;
  /**
   * ��ǰ��ҳ��
   */
  private int currentPageNumber;

  /**
   * ���������ҳ�б��urlģʽ��
   */
  private String url;

  /**
   * ��ȡ��ҳ�����������б�
   * @return �б�������ʵ��Ҫ�õĶ���
   */
  public List<T> getPageContentList() {
    return pageContentList;
  }

  /**
   * �����б����
   * @param pageContentList
   */
  public void setPageContentList(final List<T> pageContentList) {
    this.pageContentList = pageContentList;
  }

  /**
   * ���õ�ǰҳ�루����ǰ������ã�
   * @param currentPageNumber ��ǰҳ��
   */
  public void setCurrentPageNumber(final int currentPageNumber) {
    if (currentPageNumber == 0) {
      this.currentPageNumber = 1;
    } else {
      this.currentPageNumber = currentPageNumber;
    }
  }

  /**
   * ����ҳ���С������ȥ�������
   * @param pageSize ҳ���С
   */
  public void setPageSize(final int pageSize) {
    this.pageSize = pageSize;
  }

  // �������
  /**
   * �����ܼ�¼������ҳ�ܺͣ�������ǰ�������
   * @param totalRecordNumber
   */
  public void setTotalRecordNumber(final int totalRecordNumber) {
    this.totalRecordNumber = totalRecordNumber;
  }

  /**
   * ��ȡҳ���С
   * @return ҳ���С
   */
  public int getPageSize() {
    return pageSize;
  }

  /**
   * ��ȡ�ܼ�¼��
   * @return �ܼ�¼��
   */
  public int getTotalRecordNumber() {
    return totalRecordNumber;
  }

  /**
   * ��ȡ��ҳ��
   * @return ��ҳ��
   */
  public int getTotalPageNumber() {
    final double x = totalRecordNumber;
    final double y = pageSize;
    final int z = (int) Math.ceil(x / y);
    return z;
  }

  /**
   * ��ȡÿҳ����������ҳ��
   * @return ��ҳ��
   */
  public int getTotalPageNumberIsFull() {
    final double x = totalRecordNumber;
    final double y = pageSize;
    final int z = (int) Math.floor(x / y);
    return z;
  }

  /**
   * ��ȡ��ǰҳ��
   * @return ��ǰҳ��
   */
  public int getCurrentPageNumber() {
    if ((currentPageNumber > this.getTotalPageNumber()) || (currentPageNumber < 1)) {
      return 1;
    } else {
      return currentPageNumber;
    }
  }

  /**
   * ��ȡ��ʼ��¼λ��
   * @return ��ʼ��¼λ��
   */
  public int getStartOffset() {
    final int temp = (this.getCurrentPageNumber() - 1) * pageSize;
    return temp;
  }

  /**
   * ���ù����ҳ�б��õ�URLģʽ������ҳ���ֵ��λ�����ַ���$number$���
   * @param url URLģʽ
   */
  public void setUrl(final String url) {
    this.url = url;
  }

  /**
   * ��ȡ��ҳ�б��HTML���롣���urlδ�����ù������ؿմ���
   * @author Herbert
   * @return ��ҳ�������б��HTML����
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
      //��һҳ
      if (currentPageNumber > 1) {
        sb.append("<a class=\"prev-page\" href=\"$url$\">��һҳ</a>\r\n".replace("$url$", url).replace("$number$",
            (currentPageNumber - 1) + ""));
      }
      //�õ�ҳ����ʼ
      if (currentPageNumber - 7 <= 1) {
        startNum = 1;
      } else {
        startNum = currentPageNumber - 6;
      }
      //�õ�ҳ�����
      if (currentPageNumber + 7 >= totalPageNumber) {
        endNum = totalPageNumber;
      } else {
        endNum = currentPageNumber + 6;
      }
      //ѭ���м��ҳ��
      for (int i = startNum; i <= endNum; i++) {
        if (i == currentPageNumber) {
          sb.append(a.replace("$number$", i + ""));
        } else {
          sb.append(b.replace("$number$", i + ""));
        }
      }
      //��һҳ
      if (currentPageNumber < totalPageNumber) {
        sb.append("<a class=\"next-page\" href=\"$url$\">��һҳ</a>\r\n".replace("$url$", url).replace("$number$",
            (currentPageNumber + 1) + ""));
      }
      return sb.toString();
    }
  }

  /**
   * ר���������ʹ�õķ�ҳ�߼�
   * ��ǰҳ��ǰ��������ҳ�棬����������ҳ�롣�����һҳû����ʾ��������ʾ����ҳ����������һҳû����ʾ��������ʾ��ĩҳ��
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
        sb.append("<a href=\"$url$\">��ҳ</a>\r\n".replace("$url$", url).replace("$number$", "1"));
      }
      //��һҳ
      if (currentPageNumber > 1) {
        sb.append("<a class=\"pre\" href=\"$url$\">��һҳ</a>\r\n".replace("$url$", url).replace(
            "$number$", (currentPageNumber - 1) + ""));
      }
      //�õ�ҳ����ʼ
      if (currentPageNumber - 3 < 1) {
        startNum = 1;
      } else {
        startNum = currentPageNumber - 2;
      }
      //�õ�ҳ�����
      if (currentPageNumber + 3 > totalPageNumber) {
        endNum = totalPageNumber;
      } else {
        endNum = currentPageNumber + 2;
      }
      //ѭ���м��ҳ��
      for (int i = startNum; i <= endNum; i++) {
        if (i == currentPageNumber) {
          sb.append(a.replace("$number$", i + ""));
        } else {
          sb.append(b.replace("$number$", i + ""));
        }
      }
      //��һҳ
      if (currentPageNumber < totalPageNumber) {
        sb.append("<a class=\"next\" href=\"$url$\">��һҳ</a>\r\n".replace("$url$", url).replace(
            "$number$", (currentPageNumber + 1) + ""));
      }
      //����������һҳ����ʾ����һ����û����һҳ��û��ĩҳ��ʾ
      if (endNum < totalPageNumber) {
        sb.append("<a href=\"$url$\">ĩҳ</a>\r\n".replace("$url$", url).replace("$number$",
            this.getTotalPageNumber() + ""));
      }
      return sb.toString();
    }
  }
}