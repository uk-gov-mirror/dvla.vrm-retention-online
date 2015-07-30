package webserviceclients.vrmretentionretain

import com.google.inject.Inject
import play.api.Logger
import play.api.Play.current
import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.api.libs.ws.WSResponse
import uk.gov.dvla.vehicles.presentation.common.LogFormats
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.HttpHeaders
import utils.helpers.Config

import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common.LogFormats._

final class VRMRetentionRetainWebServiceImpl @Inject()(
                                                        config: Config
                                                        ) extends VRMRetentionRetainWebService {

  private val endPoint: String = s"${config.vrmRetentionRetainMicroServiceUrlBase}/vrm/retention/retain"

  override def invoke(request: VRMRetentionRetainRequest, trackingId: TrackingId): Future[WSResponse] = {
    val vrm = LogFormats.anonymize(request.currentVRM)

    Logger.debug(logMessage(s"Calling vrm retention retain micro-service with request $vrm",trackingId))
    WS.url(endPoint).
      withHeaders(HttpHeaders.TrackingId -> trackingId.value).
      withRequestTimeout(config.vrmRetentionRetainMsRequestTimeout). // Timeout is in milliseconds
      post(Json.toJson(request))
  }
}