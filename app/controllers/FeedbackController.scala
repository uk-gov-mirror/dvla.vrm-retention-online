package controllers

import com.google.inject.Inject
import play.api.i18n.Messages
import play.api.mvc.{Action, AnyContent, Call, Controller}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.RichCookies
import common.controllers.FeedbackBase
import common.services.DateService
import common.webserviceclients.emailservice.EmailService
import common.webserviceclients.healthstats.HealthStats
import utils.helpers.Config

class FeedbackController @Inject()(val emailService: EmailService,
                                   val dateService: DateService,
                                   val healthStats: HealthStats)
                                  (implicit clientSideSessionFactory: ClientSideSessionFactory,
                                   config: Config) extends Controller with FeedbackBase {

  override val emailConfiguration = config.emailConfiguration

  implicit val implicitDateService = implicitly[DateService](dateService)

  implicit val controls: Map[String, Call] = Map(
    "submit" -> controllers.routes.FeedbackController.submit()
  )

  def present() = Action { implicit request =>
    Ok(views.html.vrm_retention.feedback(form))
  }

  def submit: Action[AnyContent] = Action { implicit request =>
    form.bindFromRequest.fold(
      invalidForm => BadRequest(views.html.vrm_retention.feedback(formWithReplacedErrors(invalidForm))),
      validForm => {
        val trackingId = request.cookies.trackingId
        sendFeedback(validForm, Messages("common_feedback.subject"), trackingId)
        Ok(views.html.vrm_retention.feedbackSuccess())
      }
    )
  }
}
