package pages.vrm_retention

import helpers.webbrowser.{Element, Page, WebBrowserDSL, WebDriverFactory}
import views.vrm_retention.Payment
import Payment._
import org.openqa.selenium.WebDriver

object SummaryPage extends Page with WebBrowserDSL {

  final val address = "/vrm-retention/success"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Summary"
}