package controllers.disposal_of_vehicle

import play.api.mvc._
import play.api.Logger
import mappings.disposal_of_vehicle.Dispose._
import mappings.disposal_of_vehicle.VehicleLookup._
import mappings.disposal_of_vehicle.SetupTradeDetails._
import mappings.disposal_of_vehicle.TraderDetails._
import mappings.disposal_of_vehicle.BusinessChooseYourAddress._
import utils.helpers.{CookieNameHashing, CookieEncryption, CryptoHelper}
import com.google.inject.Inject

class BeforeYouStart @Inject()(implicit encryption: CookieEncryption, cookieNameHashing: CookieNameHashing) extends Controller {

  def present = Action { implicit request =>

    Ok(views.html.disposal_of_vehicle.before_you_start()).
      withNewSession.
      discardingCookies(getCookiesToDiscard: _*)
  }

  private def getCookiesToDiscard(implicit request: Request[_]): Seq[DiscardingCookie] = {
    val salt = CryptoHelper.getSessionSecretKeyFromRequest(request).getOrElse("")
    val cookieNames = Seq(SetupTradeDetailsCacheKey,
      traderDetailsCacheKey,
      businessChooseYourAddressCacheKey,
      vehicleLookupDetailsCacheKey,
      vehicleLookupResponseCodeCacheKey,
      vehicleLookupFormModelCacheKey,
      disposeFormModelCacheKey,
      disposeFormTransactionIdCacheKey,
      disposeFormTimestampIdCacheKey,
      disposeFormRegistrationNumberCacheKey,
      disposeModelCacheKey)
    cookieNames.map(cookieName => DiscardingCookie(name = cookieNameHashing.hash(salt + cookieName)))
  }

  def submit = Action { implicit request =>
    val modelId = request.session.get("modelId")
    Logger.debug(s"BeforeYouStart - reading modelId from session: $modelId")
    Redirect(routes.SetUpTradeDetails.present())
  }
}
