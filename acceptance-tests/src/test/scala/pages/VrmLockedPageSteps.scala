package pages

import cucumber.api.scala.{EN, ScalaDsl}
import helpers.webbrowser.{WebBrowserDSL, WebBrowserDriver}
import org.scalatest.Matchers
import pages.vrm_retention.VrmLockedPage._

class VrmLockedPageSteps(implicit webDriver: WebBrowserDriver) extends ScalaDsl with EN with WebBrowserDSL with Matchers {

  def `is displayed` = {
    page.url should equal(url)
    this
  }
}