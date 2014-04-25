package controllers.disposal_of_vehicle

import mappings.disposal_of_vehicle.BusinessChooseYourAddress._
import services.fakes.FakeWebServiceImpl
import helpers.UnitSpec
import services.fakes.FakeWebServiceImpl._
import services.session.PlaySessionState

class BusinessChooseYourAddressFormSpec extends UnitSpec {
  "form" should {
    "accept when all fields contain valid responses" in {
      formWithValidDefaults().get.uprnSelected should equal(traderUprnValid)
    }
  }

  "addressSelect" should {
    "reject if empty" in {
      val errors = formWithValidDefaults(addressSelected = "").errors
      errors.length should equal(1)
      errors(0).key should equal(addressSelectId)
      errors(0).message should equal("error.number")
    }
  }

  private def businessChooseYourAddressWithFakeWebService(uprnFound: Boolean = true) = {
    val responsePostcode = if(uprnFound) responseValidForPostcodeToAddress else responseValidForPostcodeToAddressNotFound
    val responseUprn = if(uprnFound) responseValidForUprnToAddress else responseValidForUprnToAddressNotFound
    val fakeWebService = new FakeWebServiceImpl(responsePostcode, responseUprn)
    val addressLookupService = new services.address_lookup.ordnance_survey.AddressLookupServiceImpl(fakeWebService)
    val sessionState = new PlaySessionState()
    val sessionStateFacade = new DisposalOfVehicleSessionState(sessionState)
    new BusinessChooseYourAddress(sessionStateFacade, addressLookupService)
  }

  private def formWithValidDefaults(addressSelected: String = traderUprnValid.toString) = {
    businessChooseYourAddressWithFakeWebService().form.bind(
      Map(addressSelectId -> addressSelected)
    )
  }
}
