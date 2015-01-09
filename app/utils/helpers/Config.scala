package utils.helpers

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{getDurationProperty, getProperty}

import scala.concurrent.duration.DurationInt

class Config {

  val isCsrfPreventionEnabled = getProperty("csrf.prevention", default = true)

  // Micro-service config
  val vehicleAndKeeperLookupMicroServiceBaseUrl: String = getProperty("vehicleAndKeeperLookupMicroServiceUrlBase", "NOT FOUND")
  val vrmRetentionEligibilityMicroServiceUrlBase: String = getProperty("vrmRetentionEligibilityMicroServiceUrlBase", "NOT FOUND")
  val vrmRetentionEligibilityMsRequestTimeout: Int = getProperty("vrmRetentionEligibility.requesttimeout", 30.seconds.toMillis.toInt)
  val vrmRetentionRetainMicroServiceUrlBase: String = getProperty("vrmRetentionRetainMicroServiceUrlBase", "NOT FOUND")
  val vrmRetentionRetainMsRequestTimeout: Int = getProperty("vrmRetentionRetain.requesttimeout", 30.seconds.toMillis.toInt)
  val paymentSolveMicroServiceUrlBase: String = getProperty("paymentSolveMicroServiceUrlBase", "NOT FOUND")
  val paymentSolveMsRequestTimeout: Int = getProperty("paymentSolve.ms.requesttimeout", 30.seconds.toMillis.toInt)
  val vehicleAndKeeperLookupRequestTimeout: Int = getProperty("vehicleAndKeeperLookup.requesttimeout", 30.seconds.toMillis.toInt)

  // Ordnance survey config
  val ordnanceSurveyMicroServiceUrl: String = getProperty("ordnancesurvey.ms.url", "NOT FOUND")
  val ordnanceSurveyRequestTimeout: Int = getProperty("ordnancesurvey.requesttimeout", 5.seconds.toMillis.toInt)
  val ordnanceSurveyUseUprn: Boolean = getProperty("ordnancesurvey.useUprn", default = false)

  // Brute force prevention config
  val bruteForcePreventionMicroServiceBaseUrl: String = getProperty("bruteForcePreventionMicroServiceBase", "NOT FOUND")
  val bruteForcePreventionTimeout: Int = getProperty("bruteForcePrevention.requesttimeout", 5.seconds.toMillis.toInt)
  val isBruteForcePreventionEnabled: Boolean = getProperty("bruteForcePrevention.enabled", default = true)
  val bruteForcePreventionServiceNameHeader: String = getProperty("bruteForcePrevention.headers.serviceName", "")
  val bruteForcePreventionMaxAttemptsHeader: Int = getProperty("bruteForcePrevention.headers.maxAttempts", 3)
  val bruteForcePreventionExpiryHeader: String = getProperty("bruteForcePrevention.headers.expiry", "")

  // Prototype message in html
  val isPrototypeBannerVisible: Boolean = getProperty("prototype.disclaimer", default = true)

  // Prototype survey URL
  val prototypeSurveyUrl: String = getProperty("survey.url", "")
  val prototypeSurveyPrepositionInterval: Long = getDurationProperty("survey.interval", 7.days.toMillis)

  // Google analytics
  val googleAnalyticsTrackingId: String = getProperty("googleAnalytics.id.retention", "NOT FOUND")

  // Progress step indicator
  val isProgressBarEnabled: Boolean = getProperty("progressBar.enabled", default = true)

  // Email Service
  val emailSmtpHost: String = getProperty("smtp.host", "")
  val emailSmtpPort: Int = getProperty("smtp.port", 25)
  val emailSmtpSsl: Boolean = getProperty("smtp.ssl", default = false)
  val emailSmtpTls: Boolean = getProperty("smtp.tls", default = true)
  val emailSmtpUser: String = getProperty("smtp.user", "")
  val emailSmtpPassword: String = getProperty("smtp.password", "")
  val emailWhitelist: Array[String] = getProperty("email.whitelist", "").split(",")
  val emailSenderAddress: String = getProperty("email.senderAddress", "")

  // Payment Service
  val purchaseAmount: String = getProperty("retention.purchaseAmountInPence", "NOT FOUND")

  // Rabbit-MQ
  val rabbitmqHost = getProperty("rabbitmq.host", "NOT FOUND")
  val rabbitmqPort = getProperty("rabbitmq.port", 0)
  val rabbitmqQueue = getProperty("rabbitmq.queue", "NOT FOUND")
  val rabbitmqUsername = getProperty("rabbitmq.username", "NOT FOUND")
  val rabbitmqPassword = getProperty("rabbitmq.password", "NOT FOUND")
  val rabbitmqVirtualHost = getProperty("rabbitmq.virtualHost", "NOT FOUND")

  // Cookie flags
  val secureCookies = getProperty("secureCookies", default = true)
  val cookieMaxAge = getProperty("application.cookieMaxAge", 30.minutes.toSeconds.toInt)
  val storeBusinessDetailsMaxAge = getProperty("storeBusinessDetails.cookieMaxAge", 7.days.toSeconds.toInt)
}