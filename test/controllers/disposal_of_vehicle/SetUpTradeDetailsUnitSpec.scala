package controllers.disposal_of_vehicle

import common.ClientSideSessionFactory
import helpers.UnitSpec
import helpers.common.CookieHelper
import mappings.disposal_of_vehicle.SetupTradeDetails._
import org.mockito.Mockito._
import pages.disposal_of_vehicle._
import play.api.test.Helpers._
import services.fakes.FakeAddressLookupService._
import helpers.disposal_of_vehicle.CookieFactoryForUnitSpecs
import CookieHelper._
import helpers.WithApplication
import play.api.test.FakeRequest
import play.api.Play
import models.domain.disposal_of_vehicle.SetupTradeDetailsModel
import utils.helpers.Config
import scala.Some
import helpers.JsonUtils.deserializeJsonToModel

final class SetUpTradeDetailsUnitSpec extends UnitSpec {

  "present" should {
    "display the page" in new WithApplication {
      whenReady(present) {
        r => r.header.status should equal(OK)
      }
    }

    "display populated fields when cookie exists" in new WithApplication {
      val request = FakeRequest().
        withCookies(CookieFactoryForUnitSpecs.setupTradeDetails())
      val result = setUpTradeDetails.present(request)
      val content = contentAsString(result)
      content should include(TraderBusinessNameValid)
      content should include(PostcodeValid)
    }

    "display empty fields when cookie does not exist" in new WithApplication {
      val content = contentAsString(present)
      content should not include TraderBusinessNameValid
      content should not include PostcodeValid
    }

    "display expected progress bar" in new WithApplication {
      contentAsString(present) should include("Step 2 of 6")
    }

    "display prototype message when config set to true" in new WithApplication {
      contentAsString(present) should include("""<div class="prototype">""")
    }

    "not display prototype message when config set to false" in new WithApplication {
      val request = FakeRequest()
      implicit val clientSideSessionFactory = injector.getInstance(classOf[ClientSideSessionFactory])
      implicit val config: Config = mock[Config]
      when(config.isPrototypeBannerVisible).thenReturn(false)
      val setUpTradeDetailsPrototypeNotVisible = new SetUpTradeDetails()

      val result = setUpTradeDetailsPrototypeNotVisible.present(request)
      contentAsString(result) should not include """<div class="prototype">"""
    }
  }

  "submit" should {
    "redirect to next page when the form is completed successfully" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest()
      val result = setUpTradeDetails.submit(request)
      whenReady(result) {
        r =>
          r.header.headers.get(LOCATION) should equal(Some(BusinessChooseYourAddressPage.address))
          val cookies = fetchCookiesFromHeaders(r)
          val cookieName = "setupTraderDetails"
          cookies.find(_.name == cookieName) match {
            case Some(cookie) =>
              val json = cookie.value
              val model = deserializeJsonToModel[SetupTradeDetailsModel](json)
              model.traderBusinessName should equal(TraderBusinessNameValid.toUpperCase)
              model.traderPostcode should equal(PostcodeValid.toUpperCase)
            case None => fail(s"$cookieName cookie not found")
          }
      }
    }

    "return a bad request if no details are entered" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(dealerName = "", dealerPostcode = "")
      val result = setUpTradeDetails.submit(request)
      whenReady(result) {
        r => r.header.status should equal(BAD_REQUEST)
      }
    }

    "replace max length error message for traderBusinessName with standard error message (US158)" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(dealerName = "a" * (TraderNameMaxLength + 1))
      val result = setUpTradeDetails.submit(request)
      val count = "Must be between 2 and 58 characters and only contain valid characters".r.findAllIn(contentAsString(result)).length
      count should equal(2)
    }

    "replace required and min length error messages for traderBusinessName with standard error message (US158)" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(dealerName = "")
      val result = setUpTradeDetails.submit(request)
      val count = "Must be between 2 and 58 characters and only contain valid characters".r.findAllIn(contentAsString(result)).length
      count should equal(2) // The same message is displayed in 2 places - once in the validation-summary at the top of
      // the page and once above the field.
    }

    "write cookie when the form is completed successfully" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest()
      val result = setUpTradeDetails.submit(request)
      whenReady(result) {
        r =>
          val cookies = fetchCookiesFromHeaders(r)
          cookies.map(_.name) should contain (SetupTradeDetailsCacheKey)
      }
    }
  }

  private def buildCorrectlyPopulatedRequest(dealerName: String = TraderBusinessNameValid, dealerPostcode: String = PostcodeValid) = {
    FakeRequest().withFormUrlEncodedBody(
      TraderNameId -> dealerName,
      TraderPostcodeId -> dealerPostcode)
  }

  private val setUpTradeDetails = {
    injector.getInstance(classOf[SetUpTradeDetails])
  }

  private lazy val present = {
    val request = FakeRequest()
    setUpTradeDetails.present(request)
  }
}
