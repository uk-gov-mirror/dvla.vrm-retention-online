/*
 * Copyright 2001-2013 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * TODO Check for licensing issues as the code below is based on code found in Scalatest
 */

package helpers.webbrowser

import org.openqa.selenium.WebElement
import org.openqa.selenium.Cookie
import org.openqa.selenium.WebDriver
import org.openqa.selenium.Alert
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.OutputType
import org.openqa.selenium.JavascriptExecutor
import java.io.File
import java.io.FileOutputStream
import java.io.FileInputStream
import java.util.Date
import java.util.concurrent.TimeUnit
import scala.collection.JavaConverters.{asScalaBufferConverter, asScalaSetConverter}
import scala.language.implicitConversions

trait WebBrowserDSL {

  class CookiesNoun

  val cookies = new CookiesNoun

  sealed abstract class SwitchTarget[T] {
    def switch(driver: WebDriver): T
  }

  final class ActiveElementTarget extends SwitchTarget[Element] {

    def switch(driver: WebDriver): Element = {
      createTypedElement(driver.switchTo.activeElement)
    }
  }

  private def createTypedElement(element: WebElement): Element = {
    if (Helper.isTextField(element))
      new TextField(element)
    else if (Helper.isTextArea(element))
      new TextArea(element)
    else if (Helper.isPasswordField(element))
      new PasswordField(element)
    else if (Helper.isEmailField(element))
      new EmailField(element)
    else if (Helper.isColorField(element))
      new ColorField(element)
    else if (Helper.isDateField(element))
      new DateField(element)
    else if (Helper.isDateTimeField(element))
      new DateTimeField(element)
    else if (Helper.isDateTimeLocalField(element))
      new DateTimeLocalField(element)
    else if (Helper.isMonthField(element))
      new MonthField(element)
    else if (Helper.isNumberField(element))
      new NumberField(element)
    else if (Helper.isRangeField(element))
      new RangeField(element)
    else if (Helper.isSearchField(element))
      new SearchField(element)
    else if (Helper.isTelField(element))
      new TelField(element)
    else if (Helper.isTimeField(element))
      new TimeField(element)
    else if (Helper.isUrlField(element))
      new UrlField(element)
    else if (Helper.isWeekField(element))
      new WeekField(element)
    else if (Helper.isCheckBox(element))
      new Checkbox(element)
    else if (Helper.isRadioButton(element))
      new RadioButton(element)
    else if (element.getTagName.toLowerCase == "select") {
      val select = new Select(element)
      if (select.isMultiple)
        new MultiSel(element)
      else
        new SingleSel(element)
    }
    else
      new Element() {
        val underlying = element
      }
  }

  object go {

    def to(url: String)(implicit driver: WebDriver) {
      driver.get(url)
    }

    def to(page: Page)(implicit driver: WebDriver) {
      driver.get(page.url)
    }
  }

  def close()(implicit driver: WebDriver) {
    driver.close()
  }

  sealed trait Query {

    val by: By

    val queryString: String

    def element(implicit driver: WebDriver): Element = {
      try {
        createTypedElement(driver.findElement(by))
      }
      catch {
        case e: org.openqa.selenium.NoSuchElementException =>
          throw new TestFailedException("Element '" + queryString + "' not found.")
      }
    }

    def findElement(implicit driver: WebDriver): Option[Element] =
      try {
        Some(createTypedElement(driver.findElement(by)))
      }
      catch {
        case e: org.openqa.selenium.NoSuchElementException => None
      }

    def findAllElements(implicit driver: WebDriver): Iterator[Element] =
      driver.findElements(by).asScala.toIterator.map { e => createTypedElement(e)}

    def webElement(implicit driver: WebDriver): WebElement = {
      try {
        driver.findElement(by)
      }
      catch {
        case e: org.openqa.selenium.NoSuchElementException =>
          throw new TestFailedException("WebElement '" + queryString + "' not found.")
      }
    }
  }

  case class IdQuery(queryString: String) extends Query {
    val by = By.id(queryString)
  }

  case class NameQuery(queryString: String) extends Query {
    val by = By.name(queryString)
  }

  case class XPathQuery(queryString: String) extends Query {
    val by = By.xpath(queryString)
  }

  // TODO: Are these case classes just to get at the val?

  case class ClassNameQuery(queryString: String) extends Query {
    val by = By.className(queryString)
  }

  case class CssSelectorQuery(queryString: String) extends Query {
    val by = By.cssSelector(queryString)
  }

  case class LinkTextQuery(queryString: String) extends Query {
    val by = By.linkText(queryString)
  }

  case class PartialLinkTextQuery(queryString: String) extends Query {
    val by = By.partialLinkText(queryString)
  }

  def id(elementId: String): IdQuery = new IdQuery(elementId)

  def name(elementName: String): NameQuery = new NameQuery(elementName)

  def xpath(xpath: String): XPathQuery = new XPathQuery(xpath)

  def className(className: String): ClassNameQuery = new ClassNameQuery(className)

  def cssSelector(cssSelector: String): CssSelectorQuery = new CssSelectorQuery(cssSelector)

  def linkText(linkText: String): LinkTextQuery = new LinkTextQuery(linkText)

  def partialLinkText(partialLinkText: String): PartialLinkTextQuery = new PartialLinkTextQuery(partialLinkText)

  // XXX

  def find(query: Query)(implicit driver: WebDriver): Option[Element] = query.findElement

  def find(queryString: String)(implicit driver: WebDriver): Option[Element] =
    new IdQuery(queryString).findElement match {
      case Some(element) => Some(element)
      case None => new NameQuery(queryString).findElement match {
        case Some(element) => Some(element)
        case None => None
      }
    }

  def findAll(query: Query)(implicit driver: WebDriver): Iterator[Element] = query.findAllElements

  def findAll(queryString: String)(implicit driver: WebDriver): Iterator[Element] = {
    val byIdItr = new IdQuery(queryString).findAllElements
    if (byIdItr.hasNext)
      byIdItr
    else
      new NameQuery(queryString).findAllElements
  }

  private def tryQueries[T](queryString: String)(f: Query => T)(implicit driver: WebDriver): T = {
    try {
      f(IdQuery(queryString))
    }
    catch {
      case _: Throwable => f(NameQuery(queryString))
    }
  }

  def textField(query: Query)(implicit driver: WebDriver): TextField = new TextField(query.webElement)

  def textField(queryString: String)(implicit driver: WebDriver): TextField =
    tryQueries(queryString)(q => new TextField(q.webElement))

  def textArea(query: Query)(implicit driver: WebDriver) = new TextArea(query.webElement)

  def textArea(queryString: String)(implicit driver: WebDriver): TextArea =
    tryQueries(queryString)(q => new TextArea(q.webElement))

  def pwdField(query: Query)(implicit driver: WebDriver): PasswordField = new PasswordField(query.webElement)

  def pwdField(queryString: String)(implicit driver: WebDriver): PasswordField =
    tryQueries(queryString)(q => new PasswordField(q.webElement))

  def emailField(query: Query)(implicit driver: WebDriver): EmailField = new EmailField(query.webElement)

  def emailField(queryString: String)(implicit driver: WebDriver): EmailField =
    tryQueries(queryString)(q => new EmailField(q.webElement))

  def colorField(query: Query)(implicit driver: WebDriver): ColorField = new ColorField(query.webElement)

  def colorField(queryString: String)(implicit driver: WebDriver): ColorField =
    tryQueries(queryString)(q => new ColorField(q.webElement))

  def dateField(query: Query)(implicit driver: WebDriver): DateField = new DateField(query.webElement)

  def dateField(queryString: String)(implicit driver: WebDriver): DateField =
    tryQueries(queryString)(q => new DateField(q.webElement))

  def dateTimeField(query: Query)(implicit driver: WebDriver): DateTimeField = new DateTimeField(query.webElement)

  def dateTimeField(queryString: String)(implicit driver: WebDriver): DateTimeField =
    tryQueries(queryString)(q => new DateTimeField(q.webElement))

  def dateTimeLocalField(query: Query)(implicit driver: WebDriver): DateTimeLocalField = new DateTimeLocalField(query.webElement)

  def dateTimeLocalField(queryString: String)(implicit driver: WebDriver): DateTimeLocalField =
    tryQueries(queryString)(q => new DateTimeLocalField(q.webElement))

  def monthField(query: Query)(implicit driver: WebDriver): MonthField = new MonthField(query.webElement)

  def monthField(queryString: String)(implicit driver: WebDriver): MonthField =
    tryQueries(queryString)(q => new MonthField(q.webElement))

  def numberField(query: Query)(implicit driver: WebDriver): NumberField = new NumberField(query.webElement)

  def numberField(queryString: String)(implicit driver: WebDriver): NumberField =
    tryQueries(queryString)(q => new NumberField(q.webElement))

  def rangeField(query: Query)(implicit driver: WebDriver): RangeField = new RangeField(query.webElement)

  def rangeField(queryString: String)(implicit driver: WebDriver): RangeField =
    tryQueries(queryString)(q => new RangeField(q.webElement))

  def searchField(query: Query)(implicit driver: WebDriver): SearchField = new SearchField(query.webElement)

  def searchField(queryString: String)(implicit driver: WebDriver): SearchField =
    tryQueries(queryString)(q => new SearchField(q.webElement))

  def telField(query: Query)(implicit driver: WebDriver): TelField = new TelField(query.webElement)

  def telField(queryString: String)(implicit driver: WebDriver): TelField =
    tryQueries(queryString)(q => new TelField(q.webElement))

  object click {
    def on(element: WebElement) {
      element.click()
    }

    def on(query: Query)(implicit driver: WebDriver) {
      query.webElement.click()
    }

    def on(queryString: String)(implicit driver: WebDriver) {
      // stack depth is not correct if just call the button("...") directly.
      val target = tryQueries(queryString)(q => q.webElement)
      on(target)
    }

    def on(element: Element) {
      element.underlying.click()
    }
  }

  object switch {
    def to[T](target: SwitchTarget[T])(implicit driver: WebDriver): T = {
      target.switch(driver)
    }
  }

  val activeElement = new ActiveElementTarget()

  def switchTo[T](target: SwitchTarget[T])(implicit driver: WebDriver): T = switch to target

  // Can get by with volatile, because the setting doesn't depend on the getting
  @volatile private var targetDir = new File(System.getProperty("java.io.tmpdir"))

  def page(implicit driver: WebDriver) = new {
    def title: String = {
      val t = driver.getTitle
      if (t != null) t else ""
    }

    def url: String = driver.getCurrentUrl

    def source: String = driver.getPageSource
  }

  implicit def in(element: Element) = new {
    def enter(value: String) = {
      element match {
        case tf: TextField => tf.value = value
        case pf: EmailField => pf.value = value
        case _ =>
          throw new TestFailedException("Currently selected element is neither a text field, text area, password field, email field, search field, tel field or url field")
      }
    }
  }
}