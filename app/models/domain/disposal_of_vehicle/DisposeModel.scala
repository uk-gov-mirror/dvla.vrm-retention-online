package models.domain.disposal_of_vehicle

import models.DayMonthYear
import play.api.libs.json.{Format, Json}
import mappings.disposal_of_vehicle.Dispose._
import models.domain.common.CacheKey

final case class DisposeModel(referenceNumber: String,
                        registrationNumber: String,
                        dateOfDisposal: DayMonthYear,
                        consent: String,
                        lossOfRegistrationConsent: String,
                        mileage: Option[Int])

object DisposeModel {
  implicit val JsonFormat: Format[DisposeModel] = Json.format[DisposeModel]
  implicit val Key = CacheKey[DisposeModel](value = DisposeModelCacheKey)
}