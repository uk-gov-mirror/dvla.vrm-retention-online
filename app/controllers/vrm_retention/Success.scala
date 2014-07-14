package controllers.vrm_retention

import play.api.mvc._
import com.google.inject.Inject
import common.{ClientSideSessionFactory, CookieImplicits}
import CookieImplicits.RichSimpleResult
import CookieImplicits.RichCookies
import CookieImplicits.RichForm
import mappings.vrm_retention.RelatedCacheKeys
import play.api.Play.current
import utils.helpers.Config
import models.domain.vrm_retention.{EligibilityModel, BusinessDetailsModel, SuccessViewModel, KeeperDetailsModel}
import models.domain.common.VehicleDetailsModel

final class Success @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory, config: Config) extends Controller {

  def present = Action {
    implicit request =>
      (request.cookies.getModel[VehicleDetailsModel], request.cookies.getModel[KeeperDetailsModel],
        request.cookies.getModel[EligibilityModel], request.cookies.getModel[BusinessDetailsModel]) match {
        case (Some(vehicleDetails), Some(keeperDetails), Some(eligibilityModel), Some(businessDetailsModel)) =>
          val successViewModel = createViewModel(vehicleDetails, keeperDetails, eligibilityModel, businessDetailsModel)
          Ok(views.html.vrm_retention.success(successViewModel))
        case (Some(vehicleDetails), Some(keeperDetails), Some(eligibilityModel), None) =>
          val successViewModel = createViewModel(vehicleDetails, keeperDetails, eligibilityModel)
          Ok(views.html.vrm_retention.success(successViewModel))
        case _ => Redirect(routes.MicroServiceError.present())
      }
  }

  // TODO merge these two create methods together
  private def createViewModel(vehicleDetails: VehicleDetailsModel,
                              keeperDetails: KeeperDetailsModel,
                              eligibilityModel: EligibilityModel,
                              businessDetailsModel: BusinessDetailsModel): SuccessViewModel =
    SuccessViewModel(
      registrationNumber = vehicleDetails.registrationNumber,
      vehicleMake = vehicleDetails.vehicleMake,
      vehicleModel = vehicleDetails.vehicleModel,
      keeperTitle = keeperDetails.title,
      keeperFirstName = keeperDetails.firstName,
      keeperLastName = keeperDetails.lastName,
      keeperAddressLine1 = keeperDetails.addressLine1,
      keeperAddressLine2 = keeperDetails.addressLine2,
      keeperAddressLine3 = keeperDetails.addressLine3,
      keeperAddressLine4 = keeperDetails.addressLine4,
      keeperPostTown = keeperDetails.postTown,
      keeperPostCode = keeperDetails.postCode,
      businessName = Some(businessDetailsModel.businessName),
      businessAddressLine1 = Some(businessDetailsModel.businessAddress.address(0)),
      businessAddressLine2 = Some(businessDetailsModel.businessAddress.address(1)),
      businessAddressLine3 = Some(businessDetailsModel.businessAddress.address(2)),
      businessPostTown = Some(businessDetailsModel.businessAddress.address(4)),
      businessPostCode = Some(businessDetailsModel.businessAddress.address(5)),
      replacementRegistrationNumber = eligibilityModel.replacementVRM,
      "1234567890", "12-34-56-78-90", "10th August 2014" // TODO replacement mark, cert number and txn details
    )

  private def createViewModel(vehicleDetails: VehicleDetailsModel,
                              keeperDetails: KeeperDetailsModel,
                              eligibilityModel: EligibilityModel): SuccessViewModel =
    SuccessViewModel(
      registrationNumber = vehicleDetails.registrationNumber,
      vehicleMake = vehicleDetails.vehicleMake,
      vehicleModel = vehicleDetails.vehicleModel,
      keeperTitle = keeperDetails.title,
      keeperFirstName = keeperDetails.firstName,
      keeperLastName = keeperDetails.lastName,
      keeperAddressLine1 = keeperDetails.addressLine1,
      keeperAddressLine2 = keeperDetails.addressLine2,
      keeperAddressLine3 = keeperDetails.addressLine3,
      keeperAddressLine4 = keeperDetails.addressLine4,
      keeperPostTown = keeperDetails.postTown,
      keeperPostCode = keeperDetails.postCode,
      businessName = None,
      businessAddressLine1 = None,
      businessAddressLine2 = None,
      businessAddressLine3 = None,
      businessPostTown = None,
      businessPostCode = None,
      replacementRegistrationNumber = eligibilityModel.replacementVRM,
      "1234567890", "12-34-56-78-90", "10th August 2014" // TODO replacement mark, cert number and txn details
    )
}
