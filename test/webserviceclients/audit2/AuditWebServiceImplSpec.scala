package webserviceclients.audit2

import com.github.tomakehurst.wiremock.client.WireMock.{equalTo, postRequestedFor, urlEqualTo}
import composition.WithApplication
import helpers.{UnitSpec, WireMockFixture}
import play.api.libs.json.Json
import utils.helpers.Config

final class AuditWebServiceImplSpec extends UnitSpec with WireMockFixture {

  "invoke" should {

    "send the serialised json request" in new WithApplication {
      val resultFuture = auditService.invoke(request)
      whenReady(resultFuture, timeout) { result =>
        wireMock.verifyThat(1, postRequestedFor(
          urlEqualTo(s"/audit/v1")
        ).
          withRequestBody(equalTo(Json.toJson(request).toString())))
      }
    }
  }

  private lazy val auditService = new AuditMicroServiceImpl(new Config() {
    override val auditMicroServiceUrlBase = s"http://localhost:$wireMockPort"
  })

  private val request = {
    val data: Seq[(String, Any)] = Seq(("stub-key", "stub-value"))
    AuditRequest(
      name = "transaction id",
      serviceType = "trans no",
      data = data
    )
  }
}