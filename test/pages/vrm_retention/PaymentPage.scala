package pages.vrm_retention

import helpers.webbrowser.Page
import org.openqa.selenium.{By, WebDriver}
import org.scalatest.selenium.WebBrowser._
import pages.ApplicationContext.applicationContext
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebDriverFactory
import views.vrm_retention.Payment._

object PaymentPage extends Page {

  def address = s"$applicationContext/payment/begin"

  override lazy val url = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Payment"

  def cancel(implicit driver: WebDriver) = find(id(CancelId)).get

  def cardName(implicit driver: WebDriver) = {
    driver.switchTo().frame(driver.findElement(By.cssSelector(IFrame)))
    textField(id(CardName))
  }

  def cardNumber(implicit driver: WebDriver) = textField(id(CardNumber))

  def securityCode(implicit driver: WebDriver) = textField(id(SecurityCode))

  def payNow(implicit driver: WebDriver) = find(id(PayNow)).get

  def maximize(implicit driver: WebDriver) = driver.manage().window().maximize()

  def theLogicaGroupLogo(implicit driver: WebDriver) = driver.findElement(By.xpath("//*[@id=\"CompanyLogo\"]")).click()

  def expiryMonth() = ExpiryMonth

  def expiryYear() = ExpiryYear
}
