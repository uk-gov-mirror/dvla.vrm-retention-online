package composition

import com.google.inject.name.Names
import com.tzavellas.sse.guice.ScalaModule
import composition.TestModule.DateServiceConstants.{DateOfDisposalDayValid, DateOfDisposalMonthValid, DateOfDisposalYearValid}
import org.joda.time.{DateTime, Instant}
import org.mockito.Matchers.{any, _}
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import pdf.{PdfService, PdfServiceImpl}
import play.api.http.Status.{FORBIDDEN, OK}
import play.api.i18n.Lang
import play.api.{Logger, LoggerLike}
import services.brute_force_prevention.{BruteForcePreventionService, BruteForcePreventionServiceImpl, BruteForcePreventionWebService}
import services.fakes.AddressLookupServiceConstants.PostcodeInvalid
import services.fakes._
import services.fakes.brute_force_protection.BruteForcePreventionWebServiceConstants._
import services.vehicle_and_keeper_lookup.{VehicleAndKeeperLookupService, VehicleAndKeeperLookupServiceImpl, VehicleAndKeeperLookupWebService}
import services.vrm_retention_eligibility.{VRMRetentionEligibilityService, VRMRetentionEligibilityServiceImpl, VRMRetentionEligibilityWebService}
import services.vrm_retention_retain.{VRMRetentionRetainService, VRMRetentionRetainServiceImpl, VRMRetentionRetainWebService}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.{ClearTextClientSideSessionFactory, ClientSideSessionFactory, CookieFlags, NoCookieFlags}
import uk.gov.dvla.vehicles.presentation.common.filters.AccessLoggingFilter.AccessLoggerName
import uk.gov.dvla.vehicles.presentation.common.services.DateService
import uk.gov.dvla.vehicles.presentation.common.views.models.DayMonthYear
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.{AddressLookupService, AddressLookupWebService}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TestModule() extends ScalaModule with MockitoSugar {

  /**
   * Bind the fake implementations the traits
   */
  def configure() {
    Logger.debug("Guice is loading TestModule")

    stubOrdnanceSurveyAddressLookup()
    stubDateService()
    stubBruteForcePreventionWebService()

    bind[VehicleAndKeeperLookupWebService].to[FakeVehicleAndKeeperLookupWebService].asEagerSingleton()
    bind[VehicleAndKeeperLookupService].to[VehicleAndKeeperLookupServiceImpl].asEagerSingleton()
    bind[CookieFlags].to[NoCookieFlags].asEagerSingleton()
    bind[ClientSideSessionFactory].to[ClearTextClientSideSessionFactory].asEagerSingleton()

    bind[BruteForcePreventionService].to[BruteForcePreventionServiceImpl].asEagerSingleton()
    bind[LoggerLike].annotatedWith(Names.named(AccessLoggerName)).toInstance(Logger("dvla.common.AccessLogger"))

    bind[VRMRetentionEligibilityWebService].to[FakeVRMRetentionEligibilityWebServiceImpl].asEagerSingleton()
    bind[VRMRetentionEligibilityService].to[VRMRetentionEligibilityServiceImpl].asEagerSingleton()

    bind[VRMRetentionRetainWebService].to[FakeVRMRetentionRetainWebServiceImpl].asEagerSingleton()
    bind[VRMRetentionRetainService].to[VRMRetentionRetainServiceImpl].asEagerSingleton()
    bind[PdfService].to[PdfServiceImpl].asEagerSingleton()
  }

  private def stubOrdnanceSurveyAddressLookup() = {
    bind[AddressLookupService].to[uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey.AddressLookupServiceImpl]

    val stubbedWebServiceImpl = mock[AddressLookupWebService]
    when(stubbedWebServiceImpl.callPostcodeWebService(postcode = any[String], trackingId = any[String])(any[Lang])).thenReturn(AddressLookupWebServiceConstants.responseValidForPostcodeToAddress)
    when(stubbedWebServiceImpl.callPostcodeWebService(matches(PostcodeInvalid.toUpperCase), any[String])(any[Lang])).thenReturn(AddressLookupWebServiceConstants.responseWhenPostcodeInvalid)
    when(stubbedWebServiceImpl.callUprnWebService(uprn = matches(AddressLookupWebServiceConstants.traderUprnValid.toString), trackingId = any[String])(any[Lang])).thenReturn(AddressLookupWebServiceConstants.responseValidForUprnToAddress)
    when(stubbedWebServiceImpl.callUprnWebService(uprn = matches(AddressLookupWebServiceConstants.traderUprnInvalid.toString), trackingId = any[String])(any[Lang])).thenReturn(AddressLookupWebServiceConstants.responseValidForUprnToAddressNotFound)

    bind[AddressLookupWebService].toInstance(stubbedWebServiceImpl)
  }

  private def stubDateService() = {
    val dateTimeISOChronology: String = new DateTime(
      DateOfDisposalYearValid.toInt,
      DateOfDisposalMonthValid.toInt,
      DateOfDisposalDayValid.toInt,
      0,
      0).toString
    val today = DayMonthYear(
      DateOfDisposalDayValid.toInt,
      DateOfDisposalMonthValid.toInt,
      DateOfDisposalYearValid.toInt
    )
    val now = Instant.now()

    val dateService = mock[DateService]
    when(dateService.dateTimeISOChronology).thenReturn(dateTimeISOChronology)
    when(dateService.today).thenReturn(today)
    when(dateService.now).thenReturn(now)
    bind[DateService].toInstance(dateService)
  }

  private def stubBruteForcePreventionWebService() = {
    val bruteForcePreventionWebService = mock[BruteForcePreventionWebService]
    when(bruteForcePreventionWebService.callBruteForce(any[String])).thenReturn(Future {
      new FakeResponse(status = OK, fakeJson = responseFirstAttempt)
    })
    when(bruteForcePreventionWebService.callBruteForce(matches(VrmLocked))).thenReturn(Future {
      new FakeResponse(status = FORBIDDEN)
    })
    bind[BruteForcePreventionWebService].toInstance(bruteForcePreventionWebService)
  }
}

object TestModule {

  object DateServiceConstants {

    final val DateOfDisposalDayValid = "25"
    final val DateOfDisposalMonthValid = "11"
    final val DateOfDisposalYearValid = "1970"
  }

}