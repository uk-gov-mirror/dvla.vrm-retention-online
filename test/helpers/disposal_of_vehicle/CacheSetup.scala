package helpers.disposal_of_vehicle

import helpers.disposal_of_vehicle.Helper._
import models.domain.disposal_of_vehicle._
import models.domain.disposal_of_vehicle.VehicleDetailsModel
import models.domain.disposal_of_vehicle.TraderDetailsModel
import models.domain.disposal_of_vehicle.SetupTradeDetailsModel
import models.DayMonthYear
import services.fakes.FakeVehicleLookupWebService._
import services.fakes.FakeDisposeWebServiceImpl._
import services.fakes.{FakeDisposeWebServiceImpl, FakeVehicleLookupWebService}
import services.fakes.FakeAddressLookupService._
import services.session.SessionState
import org.openqa.selenium.{WebDriver, Cookie}
import play.api.libs.json.{Writes, Json}
import services.fakes.FakeWebServiceImpl._
import services.fakes.FakeAddressLookupService.postcodeValid
import mappings.disposal_of_vehicle.BusinessChooseYourAddress.businessChooseYourAddressCacheKey
import mappings.disposal_of_vehicle.TraderDetails.traderDetailsCacheKey
import mappings.disposal_of_vehicle.SetupTradeDetails.SetupTradeDetailsCacheKey

class CacheSetup() { // TODO change from class to an object.
  private def addCookie[A](key: String, value: A)(implicit tjs: Writes[A], webDriver: WebDriver): Unit = {
    val valueAsString = Json.toJson(value).toString()
    val manage = webDriver.manage()
    val cookie = new Cookie(key, valueAsString)
    manage.addCookie(cookie)
  }

  def setupTradeDetailsIntegration(traderPostcode: String = postcodeValid)(implicit webDriver: WebDriver) = {
    val key = SetupTradeDetailsCacheKey
    val value = SetupTradeDetailsModel(traderBusinessName = traderBusinessNameValid,
      traderPostcode = traderPostcode)
    addCookie(key, value)
    this
  }

  def businessChooseYourAddressIntegration(uprn: Long = traderUprnValid)(implicit webDriver: WebDriver) = {
    val key = businessChooseYourAddressCacheKey
    val value = BusinessChooseYourAddressModel(uprnSelected = uprn)
    addCookie(key, value)
    this
  }

  def dealerDetailsIntegration(address: AddressViewModel = addressWithoutUprn)(implicit webDriver: WebDriver) = {
    val key = traderDetailsCacheKey
    val value = TraderDetailsModel(traderName = "", // TODO [SKW] why are we caching an empty string?
      traderAddress = address)
    addCookie(key, value)
    this
  }

  def vehicleLookupFormModelIntegration(referenceNumber: String = referenceNumberValid,
                                        registrationNumber: String = registrationNumberValid)(implicit webDriver: WebDriver) = {
    val key = mappings.disposal_of_vehicle.VehicleLookup.vehicleLookupFormModelCacheKey
    val value = VehicleLookupFormModel(referenceNumber = referenceNumber,
      registrationNumber = registrationNumber)
    addCookie(key, value)
    this
  }

  def vehicleDetailsModelIntegration(registrationNumber: String = registrationNumberValid,
                                     vehicleMake: String = FakeVehicleLookupWebService.vehicleMakeValid,
                                     vehicleModel: String = vehicleModelValid,
                                     keeperName: String = keeperNameValid)(implicit webDriver: WebDriver) = {
    val key = mappings.disposal_of_vehicle.VehicleLookup.vehicleLookupDetailsCacheKey
    val value = VehicleDetailsModel(registrationNumber = registrationNumber,
      vehicleMake = vehicleMake,
      vehicleModel = vehicleModel)
    addCookie(key, value)
    this
  }

  def disposeFormModelIntegration()(implicit webDriver: WebDriver) = {
    val key = mappings.disposal_of_vehicle.Dispose.disposeFormModelCacheKey
    val value = DisposeFormModel(mileage = None,
      dateOfDisposal = DayMonthYear.today,
      consent = FakeDisposeWebServiceImpl.consentValid,
      lossOfRegistrationConsent = FakeDisposeWebServiceImpl.consentValid)
    addCookie(key, value)
    this
  }

  def disposeModelIntegration(referenceNumber: String = referenceNumberValid,
                              registrationNumber: String = registrationNumberValid,
                              dateOfDisposal: DayMonthYear = DayMonthYear.today,
                              mileage: Option[Int] = None)(implicit webDriver: WebDriver) = {
    val key = mappings.disposal_of_vehicle.Dispose.disposeModelCacheKey
    val value = DisposeModel(referenceNumber = referenceNumber,
      registrationNumber = registrationNumber,
      dateOfDisposal = dateOfDisposal,
      mileage = mileage)
    addCookie(key, value)
    this
  }

  def disposeTransactionIdIntegration(transactionId: String = transactionIdValid)(implicit webDriver: WebDriver) = {
    val key = mappings.disposal_of_vehicle.Dispose.disposeFormTransactionIdCacheKey
    val value = transactionId
    addCookie(key, value)
    this
  }

  def vehicleRegistrationNumberIntegration()(implicit webDriver: WebDriver) = {
    val key = mappings.disposal_of_vehicle.Dispose.disposeFormRegistrationNumberCacheKey
    val value = registrationNumberValid
    addCookie(key, value)
    this
  }
}
