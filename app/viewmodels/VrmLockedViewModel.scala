package viewmodels

import uk.gov.dvla.vehicles.presentation.common.views.constraints.RegistrationNumber.formatVrm

final case class VrmLockedViewModel(registrationNumber: String,
                                    vehicleMake: Option[String],
                                    vehicleModel: Option[String])

object VrmLockedViewModel {

  def apply(vehicleAndKeeperDetails: VehicleAndKeeperDetailsModel): VrmLockedViewModel =
    VrmLockedViewModel(
      registrationNumber = vehicleAndKeeperDetails.registrationNumber,
      vehicleMake = vehicleAndKeeperDetails.make,
      vehicleModel = vehicleAndKeeperDetails.model)

  def apply(vehicleAndKeeperLookupFormModel: VehicleAndKeeperLookupFormModel): VrmLockedViewModel =
    VrmLockedViewModel(
      registrationNumber = formatVrm(vehicleAndKeeperLookupFormModel.registrationNumber),
      vehicleMake = None,
      vehicleModel = None)
}