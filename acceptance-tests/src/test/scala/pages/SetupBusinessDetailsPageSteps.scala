package pages

import cucumber.api.scala.{EN, ScalaDsl}
import org.openqa.selenium.support.events.EventFiringWebDriver
import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually.{eventually, PatienceConfig}
import org.scalatest.selenium.WebBrowser._
import pages.vrm_retention.SetupBusinessDetailsPage._

class SetupBusinessDetailsPageSteps(implicit webDriver: EventFiringWebDriver, timeout: PatienceConfig) extends ScalaDsl with EN with Matchers {

  def `is displayed` = {
    eventually {
      currentUrl should equal(url)
      pageSource contains title
    }
    this
  }

  def `enter business details` = {
    traderName.value = "Test Test1"
    traderContact.value = "Valtech"
    traderEmail.value = "business.example@test.com"
    traderPostcode.value = "SA11AA"
    click on lookup
    this
  }
}
