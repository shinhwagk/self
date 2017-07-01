package controllers

import play.api.data.Form
import play.api.data.Forms._

trait FormController {

  val searchForm = Form(
    mapping("yyyydd" -> text, "address" -> text)(Search.apply)(Search.unapply)
  )

}

case class Search(yyyydd: String, address: String)
