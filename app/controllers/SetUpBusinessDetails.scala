package controllers

import com.google.inject.Inject
import models.CacheKeyPrefix
import models.EligibilityModel
import models.RetainModel
import models.SetupBusinessDetailsFormModel
import models.SetupBusinessDetailsViewModel
import play.api.data.Form
import play.api.data.FormError
import play.api.mvc.{Action, Controller, Request}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClearTextClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CookieImplicits.RichCookies
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CookieImplicits.RichForm
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CookieImplicits.RichResult
import uk.gov.dvla.vehicles.presentation.common.model.VehicleAndKeeperDetailsModel
import uk.gov.dvla.vehicles.presentation.common.views.helpers.FormExtensions.formBinding
import utils.helpers.Config
import views.vrm_retention.RelatedCacheKeys.removeCookiesOnExit
import views.vrm_retention.SetupBusinessDetails.{BusinessContactId, BusinessNameId, BusinessPostcodeId}
import views.vrm_retention.VehicleLookup.TransactionIdCacheKey
import webserviceclients.audit2
import webserviceclients.audit2.AuditRequest

final class SetUpBusinessDetails @Inject()(auditService2: audit2.AuditService)
                                          (implicit clientSideSessionFactory: ClientSideSessionFactory,
                                           config: Config,
                                           dateService: uk.gov.dvla.vehicles.presentation.common.services.DateService)
  extends Controller {

  private[controllers] val form = Form(
    SetupBusinessDetailsFormModel.Form.Mapping
  )

  def present = Action { implicit request =>
    (request.cookies.getModel[VehicleAndKeeperDetailsModel],
      request.cookies.getModel[RetainModel]) match {
      case (Some(vehicleAndKeeperDetails), None) =>
        val viewModel = SetupBusinessDetailsViewModel(vehicleAndKeeperDetails)
        Ok(views.html.vrm_retention.setup_business_details(form.fill(), viewModel))
      case _ => Redirect(routes.VehicleLookup.present())
    }
  }

  def submit = Action { implicit request =>
    form.bindFromRequest.fold(
      invalidForm => {
        request.cookies.getModel[VehicleAndKeeperDetailsModel] match {
          case Some(vehicleAndKeeperDetails) =>
            val setupBusinessDetailsViewModel = SetupBusinessDetailsViewModel(vehicleAndKeeperDetails)
            BadRequest(views.html.vrm_retention.setup_business_details(formWithReplacedErrors(invalidForm),
              setupBusinessDetailsViewModel))
          case _ =>
            Redirect(routes.VehicleLookup.present())
        }
      },
      validForm => Redirect(routes.BusinessChooseYourAddress.present()).withCookie(validForm)
    )
  }

  def exit = Action {
    implicit request =>
      auditService2.send(AuditRequest.from(
        pageMovement = AuditRequest.CaptureActorToExit,
        transactionId = request.cookies.getString(TransactionIdCacheKey)
          .getOrElse(ClearTextClientSideSessionFactory.DefaultTrackingId),
        timestamp = dateService.dateTimeISOChronology,
        vehicleAndKeeperDetailsModel = request.cookies.getModel[VehicleAndKeeperDetailsModel],
        replacementVrm = Some(request.cookies.getModel[EligibilityModel].get.replacementVRM)))

      Redirect(routes.LeaveFeedback.present()).
        discardingCookies(removeCookiesOnExit)
  }

  private def formWithReplacedErrors(form: Form[SetupBusinessDetailsFormModel])(implicit request: Request[_]) =
    (form /: List(
      (BusinessNameId, "error.validBusinessName"),
      (BusinessContactId, "error.validBusinessContact"),
      (BusinessPostcodeId, "error.restricted.validPostcode"))) { (form, error) =>
      form.replaceError(error._1, FormError(
        key = error._1,
        message = error._2,
        args = Seq.empty
      ))
    }.distinctErrors
}
