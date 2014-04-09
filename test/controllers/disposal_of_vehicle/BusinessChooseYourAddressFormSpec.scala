package controllers.disposal_of_vehicle

import mappings.disposal_of_vehicle.BusinessChooseYourAddress._
import services.fakes.{FakeAddressLookupService, FakeWebServiceImpl}
import org.mockito.Mockito._
import org.mockito.Matchers._
import helpers.UnitSpec
import services.address_lookup.AddressLookupService
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import play.api.libs.ws.Response
import services.fakes.FakeWebServiceImpl._
import play.api.libs.ws.Response

class BusinessChooseYourAddressFormSpec extends UnitSpec {
  "BusinesssChooseYourAddress Form" should {
    def businessChooseYourAddressWithFakeWebService(uprnFound: Boolean = true) = {
      val response = if(uprnFound) responseValidForOrdnanceSurvey else responseValidForOrdnanceSurveyNotFound
      val fakeWebService = new FakeWebServiceImpl(response, response)
      val addressLookupService = new services.address_lookup.ordnance_survey.AddressLookupServiceImpl(fakeWebService)

      new BusinessChooseYourAddress(addressLookupService)
    }

    def formWithValidDefaults(addressSelected: String = traderUprnValid.toString) = {
      businessChooseYourAddressWithFakeWebService().form.bind(
        Map(addressSelectId -> addressSelected)
      )
    }

    "accept if form is valid" in {
      formWithValidDefaults().get.uprnSelected should equal(traderUprnValid)
    }

    "reject if addressSelect is empty" in {
      val errors = formWithValidDefaults(addressSelected = "").errors

      errors.length should equal(1)
      errors(0).key should equal(addressSelectId)
      errors(0).message should equal("error.number")
    }
  }
}
