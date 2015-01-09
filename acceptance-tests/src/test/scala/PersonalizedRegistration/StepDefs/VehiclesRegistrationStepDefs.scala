package PersonalizedRegistration.StepDefs

import _root_.common._
import cucumber.api.java.After
import cucumber.api.java.en.{Given, Then, When}
import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually.PatienceConfig
import pages._
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebBrowserDriver

import scala.concurrent.duration.DurationInt

final class VehiclesRegistrationStepDefs(implicit webDriver: WebBrowserDriver) extends ScalaDsl with EN with Matchers {

  //  private implicit val webDriver: EventFiringWebDriver = {
  //    import com.typesafe.config.ConfigFactory
  //    val conf = ConfigFactory.load()
  //    conf.getString("browser.type") match {
  //      case "firefox" => new WebBrowserFirefoxDriver
  //      case _ => new WebBrowserDriver
  //    }
  //  }
  implicit val timeout = PatienceConfig(timeout = 30.seconds)
  val beforeYouStart = new BeforeYouStartPageSteps
  val vehicleLookup = new VehicleLookupPageSteps
  val payment = new PaymentPageSteps
  val success = new SuccessPaymentPageSteps
  val paymentFailure = new PaymentFailurePageSteps
  val paymentCallBack = new PaymentCallbackPageSteps
  val vehicleNotFound = new VehicleNotFoundPageSteps
  val vrmLocked = new VrmLockedPageSteps
  val vehicleLookupFailure = new VehicleLookupFailurePageSteps
  val setupBusinessDetails = new SetupBusinessDetailsPageSteps
  val businessChooseYourAddress = new BusinessChooseYourAddressPageSteps
  val confirmBusiness = new ConfirmBusinessPageSteps
  lazy val user = new CommonStepDefs(
    beforeYouStart,
    vehicleLookup,
    vehicleNotFound,
    vrmLocked,
    confirmBusiness,
    setupBusinessDetails,
    businessChooseYourAddress
  )

  @Given("^that I have started the PR Retention Service$")
  def `that I have started the PR Retention Service`() {
    user.`start the PR service`
  }

  @When( """^I enter data in the "(.*?)", "(.*?)" and "(.*?)" for a vehicle that is eligible for retention$""")
  def `I enter data in the <vehicle-registration-number>, <document-reference-number> and <postcode> for a vehicle that is eligible for retention`(vehicleRegistrationNumber: String, documentReferenceNumber: String, postcode: String) =
    vehicleLookup.enter(vehicleRegistrationNumber, documentReferenceNumber, postcode)

  @When("^I indicate that the keeper is acting$")
  def `I indicate that the keeper is acting`() {
    vehicleLookup.
      `keeper is acting`.
      `find vehicle`
  }

  @Then("^the confirm keeper details page is displayed$")
  def `the confirm keeper details page is displayed`() {
    println("***** timeout 1: " + timeout.timeout)
    user.confirmDetails
  }

  //Scenario 2 -
  @When( """^I enter invalid data in the "(.*?)", "(.*?)" and "(.*?)" fields$""")
  def `I enter invalid data in the <vehicle-registration-number> <document-reference-number> and <postcode> fields`(registrationNumber: String, vehicleRegistrationNumber: String, postcode: String) {
    vehicleLookup.
      enter(registrationNumber, vehicleRegistrationNumber, postcode).
      `keeper is not acting`.
      `find vehicle`
  }

  @Then("^the error messages for invalid data in the Vehicle Registration Number, Doc Ref ID and postcode fields are displayed$")
  def `the error messages for invalid data in the Vehicle Registration Number, Doc Ref ID and postcode fields are displayed`() {
    vehicleLookup.`has error messages`
  }

  @When( """^I enter data in the "(.*?)", "(.*?)" and "(.*?)" that does not match a valid vehicle record three times in a row$""")
  def `I enter data in the <vehicle-registration-number>, <document-reference-number> and <postcode> that does not match a valid vehicle record three times in a row`(vehicleRegistrationNumber: String, documentReferenceNumber: String, postcode: String) {
    user.
      vehicleLookupDoesNotMatchRecord(vehicleRegistrationNumber, documentReferenceNumber, postcode). // 1st
      goToVehicleLookupPage.

      vehicleLookupDoesNotMatchRecord(vehicleRegistrationNumber, documentReferenceNumber, postcode). // 2nd
      goToVehicleLookupPage.

      vehicleLookupDoesNotMatchRecord(vehicleRegistrationNumber, documentReferenceNumber, postcode) // Locked
  }

  @When( """^I enter data in the "(.*?)", "(.*?)" and "(.*?)" that does not match a valid vehicle record$""")
  def `I enter data in the <Vehicle-Registration-Number>, <document-reference-number> and <postcode> that does not match a valid vehicle record`(vehicleRegistrationNumber: String, documentReferenceNumber: String, postcode: String) = {
    vehicleLookup.
      enter(vehicleRegistrationNumber, documentReferenceNumber, postcode).
      `keeper is acting`.
      `find vehicle`
  }

  @When( """^I enter data in the "(.*?)", "(.*?)" and "(.*?)" for a vehicle that is not eligible for retention$""")
  def `I enter data in the <vehicle-registration-number>, <document-reference-number> and <postcode> for a vehicle that is not eligible for retention`(vehicleRegistrationNumber: String, documentReferenceNumber: String, postcode: String) = {
    vehicleLookup.
      enter(vehicleRegistrationNumber, documentReferenceNumber, postcode).
      `keeper is acting`.
      `find vehicle`
  }

  @Then( """^the vehicle not found page is displayed$""")
  def `the vehicle not found page is displayed`() =
    vehicleNotFound.
      `is displayed`.
      `has 'not found' message`

  @Then("^the brute force lock out page is displayed$")
  def `the brute force lock out page is displayed`() = vrmLocked

  @Then("^the direct to paper channel page is displayed$")
  def `the direct to paper channel page is displayed`() =
    vehicleNotFound.
      `is displayed`.
      `has 'direct to paper' message`

  @Then("^the vehicle not eligible page is displayed$")
  def `the vehicle not eligible page is displayed`() =
    vehicleNotFound.
      `is displayed`.
      `has 'not found' message`

  //Scenario 7
  @When("^I enter data in the \"(.*?)\", \"(.*?)\" and \"(.*?)\" for a vehicle that is eligible for retention and I indicate that the keeper is not acting and I have not previously chosen to store my details$")
  def `I enter data in the <Vehicle-Registration-Number>, <Doc-Ref-ID> and <postcode> for a vehicle that is eligible for retention and I indicate that the keeper is not acting and I have not previously chosen to store my details`(vehicleRegistrationNumber: String, documentReferenceNumber: String, postcode: String) = {
    vehicleLookup.enter(vehicleRegistrationNumber, documentReferenceNumber, postcode).
      `keeper is not acting`.
      `find vehicle`
  }

  @Then("^the supply business details page is displayed$")
  def `the supply business details page is displayed`() = {
    setupBusinessDetails.`is displayed`
  }

  //Scenario 8
  @When("^I enter data in the \"(.*?)\", \"(.*?)\" and \"(.*?)\" for a vehicle that and I indicate that the keeper is not acting and I have previously chosen to store my details and the cookie is still fresh less than seven days old$")
  def `I enter data in the <vehicle-registration-number>, <document-reference-number> and <postcode> for a vehicle that and I indicate that the keeper is not acting and I have previously chosen to store my details and the cookie is still fresh less than seven days old`(vehicleRegistrationNumber: String, documentReferenceNumber: String, postcode: String) = {
    //1st Store the details
    user.
      goToVehicleLookupPageWithNonKeeper(vehicleRegistrationNumber, documentReferenceNumber, postcode).
      provideBusinessDetails.
      chooseBusinessAddress.
      confirmBusinessDetailsIsDisplayed.
      storeBusinessDetails.
      exitBusiness.
      validateCookieIsFresh.

      //2nd validate details are stored
      goToVehicleLookupPage.
      goToVehicleLookupPageWithNonKeeper(vehicleRegistrationNumber, documentReferenceNumber, postcode)
  }

  @Then("^the confirm business details page is displayed$")
  def `the confirm business details page is displayed`() = {
    user.confirmBusinessDetailsIsDisplayed
  }

  /** DO NOT REMOVE **/
  @After()
  def teardown() = webDriver.quit()
}
