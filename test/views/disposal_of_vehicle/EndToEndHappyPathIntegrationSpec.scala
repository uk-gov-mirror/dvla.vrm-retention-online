package views.disposal_of_vehicle

import helpers.UiSpec
import helpers.webbrowser.TestHarness
import pages.disposal_of_vehicle._
import services.fakes.FakeAddressLookupService

class EndToEndHappyPathIntegrationSpec extends UiSpec with TestHarness {
  "The happy end to end case" should {
    "follow the happy path trough all the pages" in new WebBrowser {

      info("Going Before You Start page and click start")
      go to BeforeYouStartPage
      click on BeforeYouStartPage.startNow
      page.title should equal(SetupTradeDetailsPage.title)

      info("Enter valid trader details and find the trader address")

      SetupTradeDetailsPage.happyPath()
      page.source.contains(FakeAddressLookupService.PostcodeValid) should equal(true)
      page.title should equal(BusinessChooseYourAddressPage.title)

      info("Select the business address")
      BusinessChooseYourAddressPage.happyPath
      page.title should equal(VehicleLookupPage.title)

      info("enter the vehicle details")
      VehicleLookupPage.happyPath()
      page.title should equal(DisposePage.title)

      info("dispose the vehicle")
      DisposePage.happyPath
      page.title should equal(DisposeSuccessPage.title)
    }
  }
}