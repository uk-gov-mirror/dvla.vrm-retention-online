package controllers

import com.google.inject.Inject
import play.api.Logger
import play.api.mvc.{Result, _}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CookieImplicits.RichCookies
import utils.helpers.Config
import views.vrm_retention.Payment._
import views.vrm_retention.VehicleLookup._
import webserviceclients.paymentsolve.{PaymentSolveCancelRequest, PaymentSolveService}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NonFatal

final class RetainFailure @Inject()(paymentSolveService: PaymentSolveService)
                                   (implicit clientSideSessionFactory: ClientSideSessionFactory,
                                    config: Config) extends Controller {

  def present = Action.async {
    implicit request =>
      (request.cookies.getString(TransactionIdCacheKey),
        request.cookies.getString(TransactionReferenceCacheKey)) match {

        case (Some(transactionId), Some(trxRef)) =>
          callCancelWebPaymentService(transactionId, trxRef)
        case _ =>
          Future.successful(Redirect(routes.MicroServiceError.present()))
      }
  }

  private def callCancelWebPaymentService(transactionId: String, trxRef: String)
                                         (implicit request: Request[_]): Future[Result] = {

    val paymentSolveCancelRequest = PaymentSolveCancelRequest(
      transNo = transactionId.replaceAll("[^0-9]", ""), // TODO find a suitable trans no
      trxRef = trxRef
    )
    val trackingId = request.cookies.trackingId()

    paymentSolveService.invoke(paymentSolveCancelRequest, trackingId).map {
      response =>
        Ok(views.html.vrm_retention.retention_failure())
    }.recover {
      case NonFatal(e) =>
        Logger.error(s"RetainFailure Payment Solve web service call with paymentSolveCancelRequest failed. Exception " + e.toString)
        Ok(views.html.vrm_retention.retention_failure())
    }
  }
}