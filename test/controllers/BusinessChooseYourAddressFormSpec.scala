package controllers

import helpers.UnitSpec
import views.vrm_retention.BusinessChooseYourAddress.AddressSelectId
import services.fakes.AddressLookupWebServiceConstants.traderUprnValid
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.AddressLookupService
import utils.helpers.Config

final class BusinessChooseYourAddressFormSpec extends UnitSpec {

  "form" should {

    "accept when all fields contain valid responses" in {
      formWithValidDefaults().get.uprnSelected should equal(traderUprnValid.toString)
    }
  }

  "addressSelect" should {

    "reject if empty" in {
      val errors = formWithValidDefaults(addressSelected = "").errors
      errors.length should equal(1)
      errors(0).key should equal(AddressSelectId)
      errors(0).message should equal("error.required")
    }
  }

  private def formWithValidDefaults(addressSelected: String = traderUprnValid.toString) = {
    businessChooseYourAddressWithFakeWebService().form.bind(
      Map(AddressSelectId -> addressSelected)
    )
  }

  private def businessChooseYourAddressWithFakeWebService(uprnFound: Boolean = true) = {
    val addressLookupService = injector.getInstance(classOf[AddressLookupService])
    implicit val clientSideSessionFactory = injector.getInstance(classOf[ClientSideSessionFactory])
    implicit val config: Config = mock[Config]
    new BusinessChooseYourAddress(addressLookupService)
  }
}