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
package model.metaheuristic.util.visualization.html.impl;


import model.metaheuristic.util.visualization.html.HtmlComponent;

import java.util.LinkedList;
import java.util.List;

/**
 * This class makes possible to group elements inside the HTML file using the flexbox technology.
 *
 * <p>It is composed of {@link HtmlComponent}.
 *
 * @author Javier Pérez Abad
 */
public class HtmlGridView implements HtmlComponent {

  private final List<HtmlComponent> components = new LinkedList<>();
  private final String title;

  public HtmlGridView(String title) {
    this.title = title;
  }

  public HtmlGridView() {
    this(null);
  }

  public void addComponent(HtmlComponent component) {
    components.add(component);
  }

  @Override
  public String getHtml() {
    StringBuilder stringBuilder = new StringBuilder();
    if (title != null) {
      stringBuilder.append("<h2>").append(title).append("</h2>\n");
    }
    stringBuilder.append("<div class='grid-container'>\n");
    for (HtmlComponent component : components) {
      stringBuilder.append("<div class='grid-item'>\n");
      stringBuilder.append(component.getHtml());
      stringBuilder.append("</div>\n");
    }
    stringBuilder.append("</div>\n");
    return stringBuilder.toString();
  }

  @Override
  public String getCSS() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("h2 { text-align: center; }");
    stringBuilder.append(".grid-container")
        .append(
            " { display: flex; flex-direction: row; flex-wrap: wrap; flex-shrink: 0; justify-content: space-evenly; align-items: center; }");
    stringBuilder.append(".grid-container .grid-item ").append("{ margin: 15px; }");
    for (HtmlComponent component : components) {
      stringBuilder.append(component.getCSS());
    }
    return stringBuilder.toString();
  }
}
