package composition.vehicleandkeeperlookup

import com.tzavellas.sse.guice.ScalaModule
import composition.vehicleandkeeperlookup.TestVehicleAndKeeperLookupWebService.createResponse
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperDetailsRequest
import webserviceclients.fakes.VehicleAndKeeperLookupWebServiceConstants.vehicleAndKeeperDetailsResponseDocRefNumberNotLatest
import webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperLookupWebService

import scala.concurrent.Future

final class VehicleAndKeeperDetailsCallDocRefNumberNotLatest extends ScalaModule with MockitoSugar {

  def configure() = {
    val vehicleAndKeeperLookupWebService = mock[VehicleAndKeeperLookupWebService]
    when(vehicleAndKeeperLookupWebService.invoke(any[VehicleAndKeeperDetailsRequest], any[String])).
      thenReturn(Future.successful(createResponse(vehicleAndKeeperDetailsResponseDocRefNumberNotLatest)))
    bind[VehicleAndKeeperLookupWebService].toInstance(vehicleAndKeeperLookupWebService)
  }
}
