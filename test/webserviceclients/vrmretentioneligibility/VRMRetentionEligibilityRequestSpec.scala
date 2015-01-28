package webserviceclients.vrmretentioneligibility

import helpers.UnitSpec
import org.joda.time.DateTime
import play.api.libs.json.Json
import webserviceclients.fakes.DateServiceConstants.{DayValid, MonthValid, YearValid}
import webserviceclients.fakes.VrmRetentionRetainWebServiceConstants.ReplacementRegistrationNumberValid

final class VRMRetentionEligibilityRequestSpec extends UnitSpec {

  "format" should {
    "write json with currentVRM" in {
      toJson.toString() should include(ReplacementRegistrationNumberValid)
    }

    "write json with ISO formatted data" in {
      toJson.toString() should include("1970-11-25T00:00:00.000")
    }
  }

  private def dateTime = new DateTime(
    YearValid.toInt,
    MonthValid.toInt,
    DayValid.toInt,
    0,
    0)
  private def toJson = {
    val request = VRMRetentionEligibilityRequest(currentVRM = ReplacementRegistrationNumberValid, transactionTimestamp = dateTime)
    Json.toJson(request)
  }
}
