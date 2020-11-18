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
import tech.tablesaw.plotly.components.Figure;

import java.util.Objects;

/**
 * This class provides a wrapper to include figures from Tablesaw into the HTML file.
 *
 * @author Javier Pérez Abad
 */
public class HtmlFigure implements HtmlComponent {

  private final Figure figure;

  public HtmlFigure(Figure figure) {
    this.figure = figure;
  }

  public String getHtml() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder
        .append("<div id='")
        .append(hashCode())
        .append("' class='js-plotly-plot'>")
        .append("</div>");
    stringBuilder.append(figure.asJavascript(Integer.toString(hashCode())));

    return stringBuilder.toString();
  }

  public String getCSS() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(".svg-container { width: 80%, margin: auto} ");
    return stringBuilder.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HtmlFigure that = (HtmlFigure) o;
    return Objects.equals(figure, that.figure);
  }

  @Override
  public int hashCode() {
    return Objects.hash(figure);
  }
}
