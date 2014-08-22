package pages.vrm_retention

import helpers.webbrowser._
import views.vrm_retention.Confirm
import Confirm.{ExitId, ConfirmId, StoreDetailsConsentId}
import org.openqa.selenium.WebDriver

object ConfirmPage extends Page with WebBrowserDSL {

  final val address = "/vrm-retention/confirm"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Check details"

  def confirm(implicit driver: WebDriver): Element = find(id(ConfirmId)).get

  def exit(implicit driver: WebDriver): Element = find(id(ExitId)).get

  def happyPath(implicit driver: WebDriver) = {
    go to ConfirmPage
    click on confirm
  }

  def exitPath(implicit driver: WebDriver) = {
    go to ConfirmPage
    click on exit
  }
}