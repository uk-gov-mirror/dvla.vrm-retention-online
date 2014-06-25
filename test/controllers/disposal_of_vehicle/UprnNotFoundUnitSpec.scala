package controllers.disposal_of_vehicle

import helpers.UnitSpec
import helpers.common.CookieHelper
import play.api.test.Helpers._
import play.api.test.FakeRequest
import helpers.WithApplication
import pages.disposal_of_vehicle.UprnNotFoundPage
import CookieHelper._
import scala.Some
import play.api.Play

final class UprnNotFoundUnitSpec extends UnitSpec {
  "present" should {
    "display the page" in new WithApplication {
      val request = FakeRequest()
      val result = uprnNotFound.present(request)
      whenReady(result) {
        r => r.header.status should equal(OK)
      }
    }
  }

  val uprnNotFound = injector.getInstance(classOf[UprnNotFound])
}
