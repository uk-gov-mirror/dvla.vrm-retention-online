package models.domain.common

import play.api.libs.json.Json

final case class BruteForcePreventionResponse(attempts: Int, maxAttempts: Int)

object BruteForcePreventionResponse {
  implicit val JsonFormat = Json.format[BruteForcePreventionResponse]
}