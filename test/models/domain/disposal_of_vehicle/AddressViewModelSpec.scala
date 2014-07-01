package models.domain.disposal_of_vehicle

import AddressViewModel.JsonFormat
import helpers.UnitSpec
import models.domain.common.{AddressLinesModel, AddressAndPostcodeModel}
import play.api.libs.json.Json
import services.fakes.FakeVehicleLookupWebService._
import services.fakes.FakeAddressLookupService._

final class AddressViewModelSpec extends UnitSpec {
  "from" should {
    "translate correctly" in {
      val addressAndPostcodeModel = AddressAndPostcodeModel(addressLinesModel = AddressLinesModel(
        buildingNameOrNumber = BuildingNameOrNumberValid,
        line2 = Some(Line2Valid),
        line3 = Some(Line3Valid),
        postTown = PostTownValid))

      val result = AddressViewModel.from(addressAndPostcodeModel, PostcodeValid)

      result.uprn should equal(None)
      result.address should equal(Seq(
        BuildingNameOrNumberValid.toUpperCase,
        Line2Valid.toUpperCase,
        Line3Valid.toUpperCase,
        PostTownValid.toUpperCase,
        PostcodeValid.toUpperCase))
    }
  }

  "format" should {
    "serialize to json" in {
      val address = AddressViewModel(
        uprn = Some(KeeperUprnValid),
        address = Seq(BuildingNameOrNumberValid, Line2Valid, Line3Valid, PostTownValid, PostcodeValid))

      Json.toJson(address) should equal(asJson)
    }

    "deserialize from json" in {
      val fromJson =  Json.fromJson[AddressViewModel](asJson)
      val expected = AddressViewModel(
        uprn = Some(KeeperUprnValid),
        address = Seq(BuildingNameOrNumberValid, Line2Valid, Line3Valid, PostTownValid, PostcodeValid))

      fromJson.asOpt should equal(Some(expected))
    }
  }

  private val asJson = Json.parse(s"""{"uprn":10123456789,"address":["1234","line2 stub","line3 stub","postTown stub","$PostcodeValid"]}""")
}