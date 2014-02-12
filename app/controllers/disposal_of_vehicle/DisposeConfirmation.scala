package controllers.disposal_of_vehicle

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import models.domain.disposal_of_vehicle.{DisposeConfirmationFormModel, DisposeModel}
import models.domain.common.Address
import app.DisposalOfVehicle.DisposeConfirmation._
import controllers.disposal_of_vehicle.Helpers._

object DisposeConfirmation extends Controller {

  val disposeConfirmationForm = Form(
    mapping(
      emailAddressId -> text
    )(DisposeConfirmationFormModel.apply)(DisposeConfirmationFormModel.unapply)
  )

  def present = Action {
    implicit request => {
      fetchDealerNameFromCache match {
        case Some(traderBusinessName) => Ok(views.html.disposal_of_vehicle.dispose_confirmation(fetchData, disposeConfirmationForm))
        case None => Redirect(routes.SetUpTradeDetails.present) // TODO write controller and integration tests for re-routing when not logged in.
      }
    }
  }

  def submit = Action {
    implicit request => {
      println("Submitted dispose confirmation form ")
      disposeConfirmationForm.bindFromRequest.fold(
        formWithErrors => {
          fetchDealerNameFromCache match {
            case Some(traderBusinessName) => BadRequest(views.html.disposal_of_vehicle.dispose_confirmation(fetchData, formWithErrors))
            case None => Redirect(routes.SetUpTradeDetails.present) // TODO write controller and integration tests for re-routing when not logged in.
          }
        },
        f => {println(s"Form submitted email address = <<${f.emailAddress}>>"); Ok("success")} //Redirect(routes.VehicleLookup.present)
      )
    }
  }

  private def fetchData: DisposeModel  = {
    DisposeModel(vehicleMake = "PEUGEOT",
      vehicleModel = "307 CC",
      keeperName = "Mrs Anne Shaw",
      keeperAddress = Address("1 The Avenue", Some("Earley"), Some("Reading"), None, "RG12 6HT"),
      dealerName = "Dealer name",
      dealerAddress = Address("Address line 1", Some("Address line 2"), Some("Address line 3"), None, "Postcode"))
  }
}