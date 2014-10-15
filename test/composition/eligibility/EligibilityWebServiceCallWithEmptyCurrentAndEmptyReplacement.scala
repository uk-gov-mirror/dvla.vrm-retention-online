package composition.eligibility

import com.tzavellas.sse.guice.ScalaModule
import composition.eligibility.Helper.createResponse
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import play.api.http.Status.OK
import webserviceclients.vrmretentioneligibility.{VRMRetentionEligibilityRequest, VRMRetentionEligibilityResponse, VRMRetentionEligibilityWebService}
import scala.concurrent.Future

final class EligibilityWebServiceCallWithEmptyCurrentAndEmptyReplacement() extends ScalaModule with MockitoSugar {

  val withEmptyCurrentAndEmptyReplacement: (Int, VRMRetentionEligibilityResponse) = {
    (OK, VRMRetentionEligibilityResponse(currentVRM = None, replacementVRM = None, responseCode = None))
  }

  def configure() = {
    val webService = mock[VRMRetentionEligibilityWebService]
    when(webService.invoke(any[VRMRetentionEligibilityRequest], any[String])).
      thenReturn(Future.successful(createResponse(withEmptyCurrentAndEmptyReplacement)))
    bind[VRMRetentionEligibilityWebService].toInstance(webService)
  }
}