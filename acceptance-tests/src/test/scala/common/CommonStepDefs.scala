package common

import composition.TestHarness
import cucumber.api.scala.EN
import cucumber.api.scala.ScalaDsl
import org.openqa.selenium.support.events.EventFiringWebDriver
import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually.PatienceConfig
import org.scalatest.concurrent.Eventually.eventually
import org.scalatest.selenium.WebBrowser._
import pages._
import pages.vrm_retention._
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory.TrackingIdCookieName

class CommonStepDefs(
                      beforeYouStart: BeforeYouStartPageSteps,
                      vehicleLookup: VehicleLookupPageSteps,
                      vehicleNotFound: VehicleNotFoundPageSteps,
                      vrmLocked: VrmLockedPageSteps,
                      confirmBusiness: ConfirmBusinessPageSteps,
                      setupBusinessDetails: SetupBusinessDetailsPageSteps,
                      businessChooseYourAddress: BusinessChooseYourAddressPageSteps
                      )(implicit webDriver: EventFiringWebDriver, timeout: PatienceConfig) extends ScalaDsl with EN with Matchers with TestHarness {

  def `start the PR service` = {
    //    import com.typesafe.config.ConfigFactory
    //    val conf = ConfigFactory.load()
    //    val testEnvValue = conf.getString("test.env")
    //    val testUrlKey = s"test.url"
    //    val testUrlValue = conf.getString(s"$testUrlKey.$testEnvValue")
    //    sys.props += ((testUrlKey, testUrlValue))

    beforeYouStart.`go to BeforeYouStart page`.
      `is displayed`
    delete all cookies
    beforeYouStart.`click 'Start now' button`
    vehicleLookup.`is displayed`
    this
  }

  def validateCookieIsFresh = {
    val c = cookie(TrackingIdCookieName)
    try {
      c.underlying.validate() // The java method returns void or throws, so to make it testable you should wrap it in a try-catch.
    } catch {
      case e: Throwable => fail(s"Cookie should be valid and not have thrown exception: $e")
    }
    //    val timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime)
    //    cookie("tracking_id").value should include(timeStamp) // This is not possible to test as the cookie content is encrypted and the test framework will not the decryption key.
    c.expiry should not be None // It is not a session cookie.
    this
  }

  def confirmDetails = {
    eventually {
      pageTitle should equal(ConfirmPage.title)
    }
    click on ConfirmPage.confirm
    this
  }

  def goToVehicleLookupPageWithNonKeeper(RegistrationNumber: String, DocRefNumber: String, Postcode: String) = {
    vehicleLookup.
      enter(RegistrationNumber, DocRefNumber, Postcode).
      `keeper is not acting`.
      `find vehicle`
    //confirmBusiness.`is displayed`
    this
  }

  def provideBusinessDetails = {
    setupBusinessDetails.`is displayed`
    setupBusinessDetails.`enter business details`
    this
  }

  def chooseBusinessAddress = {
    businessChooseYourAddress.`proceed to next page`
    this
  }

  def storeBusinessDetails = {
    click on ConfirmBusinessPage.rememberDetails
    click on ConfirmBusinessPage.confirm
    this
  }

  def confirmBusinessDetailsIsDisplayed = {
    eventually {
      pageTitle should equal(ConfirmBusinessPage.title)
    }
    this
  }

  def exitBusiness = {
    click on ConfirmBusinessPage.exit
    this
  }

  def goToVehicleLookupPage = {
    go to VehicleLookupPage
    this
  }

  def vehicleLookupDoesNotMatchRecord(registrationNumber: String, docRefNumber: String, postcode: String) = {
    vehicleLookup.
      enter(registrationNumber, docRefNumber, postcode).
      `keeper is not acting`.
      `find vehicle`
    vrmLocked.`is displayed`
    this
  }
}
