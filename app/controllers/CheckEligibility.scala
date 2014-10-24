package controllers

import audit._
import com.google.inject.Inject
import models.{BusinessDetailsModel, EligibilityModel, VehicleAndKeeperDetailsModel, VehicleAndKeeperLookupFormModel}
import play.api.Logger
import play.api.mvc.{Result, _}
import uk.gov.dvla.vehicles.presentation.common.LogFormats
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CookieImplicits.{RichCookies, RichResult}
import uk.gov.dvla.vehicles.presentation.common.services.DateService
import utils.helpers.Config
import views.vrm_retention.ConfirmBusiness.StoreBusinessDetailsCacheKey
import views.vrm_retention.VehicleLookup.{TransactionIdCacheKey, UserType_Keeper, VehicleAndKeeperLookupResponseCodeCacheKey}
import webserviceclients.vrmretentioneligibility.{VRMRetentionEligibilityRequest, VRMRetentionEligibilityService}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NonFatal
import uk.gov.dvla.vehicles.presentation.common.views.constraints.RegistrationNumber.formatVrm

final class CheckEligibility @Inject()(eligibilityService: VRMRetentionEligibilityService,
                                       dateService: DateService,
                                       auditService: AuditService)
                                      (implicit clientSideSessionFactory: ClientSideSessionFactory,
                                       config: Config) extends Controller {

  def present = Action.async {
    implicit request =>
      (request.cookies.getModel[VehicleAndKeeperLookupFormModel], request.cookies.getModel[VehicleAndKeeperDetailsModel],
        request.cookies.getString(StoreBusinessDetailsCacheKey).exists(_.toBoolean),
        request.cookies.getString(TransactionIdCacheKey)) match {
        case (Some(form), Some(vehicleAndKeeperDetailsModel), storeBusinessDetails, Some(transactionId)) =>
          checkVrmEligibility(form, vehicleAndKeeperDetailsModel, storeBusinessDetails, transactionId)
        case _ => Future.successful {
          Redirect(routes.Error.present("user went to CheckEligibility present without required cookies"))
        }
      }
  }

  /**
   * Call the eligibility service to determine if the VRM is valid for retention and a replacement mark can
   * be found.
   */
  private def checkVrmEligibility(vehicleAndKeeperLookupFormModel: VehicleAndKeeperLookupFormModel,
                                  vehicleAndKeeperDetailsModel: VehicleAndKeeperDetailsModel,
                                  storeBusinessDetails: Boolean, transactionId: String)
                                 (implicit request: Request[_]): Future[Result] = {

    def microServiceErrorResult(message: String) = {
      Logger.error(message)
      auditService.send(AuditMessage.from(
        pageMovement = AuditMessage.VehicleLookupToMicroServiceError,
        transactionId = transactionId))
      Redirect(routes.MicroServiceError.present())
    }

    def eligibilitySuccess(currentVRM: String, replacementVRM: String) = {
      val redirectLocation = {
        if (vehicleAndKeeperLookupFormModel.userType == UserType_Keeper) {
          auditService.send(AuditMessage.from(
            pageMovement = AuditMessage.VehicleLookupToConfirm,
            transactionId = transactionId,
            vehicleAndKeeperDetailsModel = Some(vehicleAndKeeperDetailsModel),
            replacementVrm = Some(replacementVRM)))
          routes.Confirm.present()
        } else {
          if (storeBusinessDetails) {
            val businessDetailsModel = request.cookies.getModel[BusinessDetailsModel]
            auditService.send(AuditMessage.from(
              pageMovement = AuditMessage.VehicleLookupToConfirmBusiness,
              transactionId = transactionId,
              vehicleAndKeeperDetailsModel = Some(vehicleAndKeeperDetailsModel),
              replacementVrm = Some(replacementVRM),
              businessDetailsModel = businessDetailsModel))
            routes.ConfirmBusiness.present()
          } else {
            auditService.send(AuditMessage.from(
              pageMovement = AuditMessage.VehicleLookupToCaptureActor,
              transactionId = transactionId,
              vehicleAndKeeperDetailsModel = Some(vehicleAndKeeperDetailsModel),
              replacementVrm = Some(replacementVRM)))
            routes.SetUpBusinessDetails.present()
          }
        }
      }
      Redirect(redirectLocation).withCookie(EligibilityModel.from(replacementVRM))
    }

    def eligibilityFailure(responseCode: String) = {
      Logger.debug(s"VRMRetentionEligibility encountered a problem with request" +
        s" ${LogFormats.anonymize(vehicleAndKeeperLookupFormModel.referenceNumber)}" +
        s" ${LogFormats.anonymize(vehicleAndKeeperLookupFormModel.registrationNumber)}, redirect to VehicleLookupFailure")

      auditService.send(AuditMessage.from(
        pageMovement = AuditMessage.VehicleLookupToVehicleLookupFailure,
        transactionId = transactionId,
        vehicleAndKeeperDetailsModel = Some(vehicleAndKeeperDetailsModel),
        rejectionCode = Some(responseCode)))
      Redirect(routes.VehicleLookupFailure.present()).
        withCookie(key = VehicleAndKeeperLookupResponseCodeCacheKey, value = responseCode.split(" - ")(1))
    }

    val eligibilityRequest = VRMRetentionEligibilityRequest(
      currentVRM = vehicleAndKeeperLookupFormModel.registrationNumber,
      transactionTimestamp = dateService.now.toDateTime
    )
    val trackingId = request.cookies.trackingId()

    eligibilityService.invoke(eligibilityRequest, trackingId).map { response =>
      response.responseCode match {
        case Some(responseCode) => eligibilityFailure(responseCode) // There is only a response code when there is a problem.
        case None =>
          // Happy path when there is no response code therefore no problem.
          (response.currentVRM, response.replacementVRM) match {
            case (Some(currentVRM), Some(replacementVRM)) => eligibilitySuccess(currentVRM, formatVrm(replacementVRM))
            case (None, None) => microServiceErrorResult(message = "Current VRM and replacement VRM not found in response")
            case (_, None) => microServiceErrorResult(message = "No replacement VRM found")
            case (None, _) => microServiceErrorResult(message = "No current VRM found")
          }
      }
    }.recover {
      case NonFatal(e) =>
        microServiceErrorResult(s"VRM Retention Eligibility web service call failed. Exception " + e.toString)
    }
  }
}
