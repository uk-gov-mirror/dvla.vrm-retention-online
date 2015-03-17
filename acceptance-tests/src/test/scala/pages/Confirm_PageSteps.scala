package pages

import cucumber.api.scala.EN
import cucumber.api.scala.ScalaDsl
import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually.PatienceConfig
import org.scalatest.concurrent.Eventually.eventually
import org.scalatest.selenium.WebBrowser._
import pages.vrm_retention.ConfirmPage._
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebBrowserDriver

final class Confirm_PageSteps(implicit webDriver: WebBrowserDriver, timeout: PatienceConfig) extends ScalaDsl with EN with Matchers {

  def `happy path` = {
    `is displayed`.
      `customer does not want an email`.
      `proceed to confirm`
    this
  }

  def `is displayed` = {
    eventually {
      currentUrl should equal(url)
    }
    this
  }

  def `proceed to confirm` = {
    click on confirm
    this
  }

  def `customer does not want an email` = {
    click on `don't supply keeper email`
    this
  }

  def `form is not filled`() = {
    `supply keeper email`.isSelected should equal(false)
    `don't supply keeper email`.isSelected should equal(false)
    this
  }
}