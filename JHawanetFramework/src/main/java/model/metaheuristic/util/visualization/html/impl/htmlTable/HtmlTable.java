/*
 * Code took from https://github.com/jMetal/jMetal
 *
 * Copyright <2017> <Antonio J. Nebro, Juan J. Durillo>
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to
 * whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. © 2019
 * GitHub, Inc.
 */
package model.metaheuristic.util.visualization.html.impl.htmlTable;


import model.metaheuristic.util.visualization.html.HtmlComponent;

/**
 * This class creates a table in HTML
 *
 * @author Javier Pérez Abad
 */
public class HtmlTable<T> implements HtmlComponent {

  protected String title;
  protected String[] headersColumn;
  protected String[] headersRow;
  protected T[][] data;

  public HtmlTable<T> setTitle(String title) {
    this.title = title;
    return this;
  }

  public HtmlTable<T> setColumnHeaders(String[] headers) {
    if (data[0].length != headers.length) return this;
    this.headersColumn = headers;
    return this;
  }

  public HtmlTable<T> setRowHeaders(String[] headers) {
    if (data.length != headers.length) return this;
    this.headersRow = headers;
    return this;
  }

  public HtmlTable<T> setData(T[][] data) {
    this.data = data;
    return this;
  }

  public String getHtml() {
    StringBuilder html = new StringBuilder("<div>\n");
    html.append("<table>\n");
    html.append(appendTitle());
    html.append(appendColumnHeaders());
    html.append(appendData());
    html.append("</table>\n</div>\n");
    return html.toString();
  }

  private StringBuilder appendTitle() {
    StringBuilder stringBuilder = new StringBuilder();
    if (title != null) {
      stringBuilder.append("<caption>").append(title).append("</caption>\n");
    }
    return stringBuilder;
  }

  private StringBuilder appendColumnHeaders() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("<tr>");
    // FIRST CELL EMPTY IF THERE ARE ROW HEADERS
    if (headersRow != null) {
      stringBuilder.append("<th>").append("</th>");
    }
    if (headersColumn != null) {
      for (String elem : headersColumn) {
        stringBuilder.append("<th>").append(elem).append("</th>");
      }
    }
    stringBuilder.append("</tr>\n");
    return stringBuilder;
  }

  private StringBuilder appendData() {
    StringBuilder html = new StringBuilder();
    for (int i = 0; i < data.length; i++) {
      html.append("<tr>");
      if (headersRow != null) {
        html.append("<th>").append(headersRow[i]).append("</th>");
      }
      html.append(createRowOfData(i));
      html.append("</tr>\n");
    }
    return html;
  }

  protected StringBuilder createRowOfData(int index) {
    StringBuilder html = new StringBuilder();
    for (T elem : data[index]) {
      html.append("<td>").append(elem.toString()).append("</td>");
    }
    return html;
  }

  public String getCSS() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("table { margin: auto; }");
    stringBuilder.append("th,td { border:1px solid black; text-align: center; padding: 15px }");
    stringBuilder.append(
        "caption { display: table-caption; text-align: center; margin: 10px; font-size: 1.5em; }");
    return stringBuilder.toString();
  }
}
