package controllers.vrm_retention

import com.google.inject.Inject
import common.ClientSideSessionFactory
import play.api.mvc.{Action, Controller}
import utils.helpers.Config

final class SoapEndpointError @Inject()()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                                          config: Config) extends Controller {

  def present = Action { implicit request =>
    Ok(views.html.vrm_retention.soap_endpoint_error())
  }
}