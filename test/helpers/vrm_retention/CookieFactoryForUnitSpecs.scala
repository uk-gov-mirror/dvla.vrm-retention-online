package helpers.vrm_retention

import common.{ClearTextClientSideSession, ClientSideSessionFactory, CookieFlags}
import composition.TestComposition
import mappings.vrm_retention.BusinessChooseYourAddress.BusinessChooseYourAddressCacheKey
import mappings.vrm_retention.BusinessDetails.BusinessDetailsCacheKey
import mappings.vrm_retention.CheckEligibility.CheckEligibilityCacheKey
import mappings.vrm_retention.EnterAddressManually.EnterAddressManuallyCacheKey
import mappings.vrm_retention.SetupBusinessDetails.SetupBusinessDetailsCacheKey
import mappings.vrm_retention.Retain.RetainCacheKey
import mappings.vrm_retention.VehicleLookup.{VehicleAndKeeperLookupDetailsCacheKey, VehicleAndKeeperLookupFormModelCacheKey}
import models.domain.common.BruteForcePreventionViewModel._
import models.domain.common._
import models.domain.vrm_retention._
import play.api.libs.json.{Json, Writes}
import play.api.mvc.Cookie
import services.fakes.FakeAddressLookupService._
import services.fakes.FakeAddressLookupWebServiceImpl.traderUprnValid
import services.fakes.FakeVRMRetentionEligibilityWebServiceImpl.ReplacementRegistrationNumberValid
import services.fakes.FakeVRMRetentionRetainWebServiceImpl.{TransactionTimestampValid, TransactionIdValid, CertificateNumberValid}
import services.fakes.FakeVehicleAndKeeperLookupWebService._
import services.fakes.brute_force_protection.FakeBruteForcePreventionWebServiceImpl._

object CookieFactoryForUnitSpecs extends TestComposition {

  private final val TrackingIdValue = "trackingId"
  implicit private val cookieFlags = injector.getInstance(classOf[CookieFlags])
  private val session = new ClearTextClientSideSession(TrackingIdValue)

  def setupBusinessDetails(businessName: String = TraderBusinessNameValid,
                           businessContact: String = TraderBusinessContactValid,
                           businessPostcode: String = PostcodeValid): Cookie = {
    val key = SetupBusinessDetailsCacheKey
    val value = SetupBusinessDetailsFormModel(businessName = businessName,
      businessContact = businessContact,
      businessPostcode = businessPostcode)
    createCookie(key, value)
  }

  def vehicleAndKeeperDetailsModel(registrationNumber: String = RegistrationNumberValid,
                          vehicleMake: Option[String] = VehicleMakeValid,
                          vehicleModel: Option[String] = VehicleModelValid,
                          title: Option[String] = KeeperTitleValid,
                          firstName: Option[String] = KeeperFirstNameValid,
                          lastName: Option[String] = KeeperLastNameValid,
                          addressLine1: Option[String] = KeeperAddressLine1Valid,
                          addressLine2: Option[String] = KeeperAddressLine2Valid,
                          postTown: Option[String] = KeeperPostTownValid,
                          postCode: Option[String] = KeeperPostCodeValid): Cookie = {
    val key = VehicleAndKeeperLookupDetailsCacheKey
    val addressAndPostcodeModel = AddressAndPostcodeModel(
      addressLinesModel = AddressLinesModel(
        buildingNameOrNumber = addressLine1.get,
        line2 = addressLine2,
        line3 = None,
        postTown = PostTownValid
      )
    )
    val addressViewModel = AddressViewModel.from(addressAndPostcodeModel, postCode.get)
    val value = VehicleAndKeeperDetailsModel(registrationNumber = registrationNumber,
      vehicleMake = vehicleMake,
      vehicleModel = vehicleModel,
      keeperTitle = title,
      keeperFirstName = firstName,
      keeperLastName = lastName,
      keeperAddress = Some(addressViewModel))
    createCookie(key, value)
  }

  private def createCookie[A](key: String, value: A)(implicit tjs: Writes[A]): Cookie = {
    val json = Json.toJson(value).toString()
    val cookieName = session.nameCookie(key)
    session.newCookie(cookieName, json)
  }

  def vehicleAndKeeperLookupFormModel(referenceNumber: String = ReferenceNumberValid,
                             registrationNumber: String = RegistrationNumberValid,
                             postcode: String = KeeperPostcodeValid,
                             keeperConsent: String = KeeperConsentValid): Cookie = {
    val key = VehicleAndKeeperLookupFormModelCacheKey
    val value = VehicleAndKeeperLookupFormModel(
      referenceNumber = referenceNumber,
      registrationNumber = registrationNumber,
      postcode = postcode,
      keeperConsent = keeperConsent
    )
    createCookie(key, value)
  }

  def businessChooseYourAddress(): Cookie = {
    val key = BusinessChooseYourAddressCacheKey
    val value = BusinessChooseYourAddressFormModel(uprnSelected = traderUprnValid.toString)
    createCookie(key, value)
  }

  def enterAddressManually(): Cookie = {
    val key = EnterAddressManuallyCacheKey
    val value = EnterAddressManuallyModel(
      addressAndPostcodeModel = AddressAndPostcodeModel(
        addressLinesModel = AddressLinesModel(
          buildingNameOrNumber = BuildingNameOrNumberValid,
          line2 = Some(Line2Valid),
          line3 = Some(Line3Valid),
          postTown = PostTownValid
        )
      )
    )
    createCookie(key, value)
  }

  def bruteForcePreventionViewModel(permitted: Boolean = true,
                                    attempts: Int = 0,
                                    maxAttempts: Int = MaxAttempts,
                                    dateTimeISOChronology: String = org.joda.time.DateTime.now().toString): Cookie = {
    val key = BruteForcePreventionViewModelCacheKey
    val value = BruteForcePreventionViewModel(
      permitted,
      attempts,
      maxAttempts,
      dateTimeISOChronology = dateTimeISOChronology
    )
    createCookie(key, value)
  }

  def trackingIdModel(value: String = TrackingIdValue): Cookie = {
    createCookie(ClientSideSessionFactory.TrackingIdCookieName, value)
  }

  private def createCookie[A](key: String, value: String): Cookie = {
    val cookieName = session.nameCookie(key)
    session.newCookie(cookieName, value)
  }

//  def keeperDetailsModel(title: String = "title",
//                         firstName: String = "firstName",
//                         lastName: String = "lastName",
//                         address: AddressViewModel = addressWithUprn): Cookie = {
//    val key = KeeperLookupDetailsCacheKey
//    val value = KeeperDetailsModel(
//      title = title,
//      firstName = firstName,
//      lastName = lastName,
//      address = address
//    )
//    createCookie(key, value)
//  }

  def eligibilityModel(replacementVRM: String = ReplacementRegistrationNumberValid): Cookie = {
    val key = CheckEligibilityCacheKey
    val value = EligibilityModel(replacementVRM = replacementVRM)
    createCookie(key, value)
  }

  def businessDetailsModel(businessName: String = TraderBusinessNameValid,
                           businessContact: String = TraderBusinessContactValid,
                           businessAddress: AddressViewModel = addressWithUprn): Cookie = {
    val key = BusinessDetailsCacheKey
    val value = BusinessDetailsModel(businessName = businessName,
      businessContact = businessContact,
      businessAddress = businessAddress)
    createCookie(key, value)
  }

  def retainModel(certificateNumber: String = CertificateNumberValid,
                  transactionId: String = TransactionIdValid,
                  transactionTimestamp: String = TransactionTimestampValid): Cookie = {
    val key = RetainCacheKey
    val value = RetainModel(certificateNumber = certificateNumber,
      transactionId = transactionId,
      transactionTimestamp = transactionTimestamp)
    createCookie(key, value)
  }
}