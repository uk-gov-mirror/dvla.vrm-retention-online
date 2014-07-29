package services.vehicle_and_keeper_lookup

import com.google.inject.Inject
import common.LogFormats
import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.ws.{Response, WS}
import services.HttpHeaders
import utils.helpers.Config
import scala.concurrent.Future
import models.domain.vrm_retention.VehicleAndKeeperDetailsRequest

final class VehicleAndKeeperLookupWebServiceImpl @Inject()(config: Config) extends VehicleAndKeeperLookupWebService {
  private val endPoint: String = s"${config.vehicleAndKeeperLookupMicroServiceBaseUrl}/vehicleandkeeper/lookup/v1"

  override def callVehicleAndKeeperLookupService(request: VehicleAndKeeperDetailsRequest, trackingId: String): Future[Response] = {
    val vrm = LogFormats.anonymize(request.registrationNumber)
    val refNo = LogFormats.anonymize(request.referenceNumber)

    Logger.debug(s"Calling vehicle and keeper lookup micro-service with request $refNo $vrm")
    Logger.debug(s"Calling vehicle and keeper lookup micro-service with tracking id: $trackingId")
    WS.url(endPoint)
      .withHeaders(HttpHeaders.TrackingId -> trackingId)
      .post(Json.toJson(request))
  }
}