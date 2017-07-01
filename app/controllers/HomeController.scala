package controllers

import javax.inject._

import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc._
import play.api.routing.JavaScriptReverseRouter
import slick.driver.JdbcProfile
import slick.jdbc.GetResult

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class HotelPeop(id: Int, CtfId: String,
                     Name: String, Address: String, Mobile: String, Tel: String, Birthday: String,
                     CTel: String)

object HotelPeop {
  implicit val getSupplierResult = GetResult(r => HotelPeop(r.<<, r.<<, r.<<, r <<, r <<, r <<, r <<, r <<))
}

class HomeController @Inject()(dbConfigProvider: DatabaseConfigProvider)
  extends Controller with FormController {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import HotelPeop._
  import dbConfig.driver.api._

  var idx = 0
  var search: Option[Search] = None


  def javascriptRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(
        routes.javascript.HomeController.del
      )
    ).as("text/javascript")
  }

  def index = Action {
    Redirect(routes.HomeController.page(0));
  }

  val lineNumber = 20

  def page(idx: Int) = Action.async {
    this.idx = idx

    val offset = idx * lineNumber

    val sql = search match {
      case Some(search) =>
        if (search.address.length >= 1 && search.yyyydd.length >= 1) {
          val address = s"%${search.address}%"
          val yyyydd = s"%${search.yyyydd}"
          sql"select id, CtfId, Name, Address, Mobile, Tel, Birthday, CTel from hotel.filter_for_310 where Birthday like ${yyyydd} AND Address like ${address} limit ${lineNumber} offset ${offset}".as[HotelPeop]
        } else if (search.yyyydd.length >= 1) {
          val yyyydd = s"%${search.yyyydd}"
          sql"select id, CtfId, Name, Address, Mobile, Tel, Birthday, CTel from hotel.filter_for_310 where Birthday like ${yyyydd} limit ${lineNumber} offset ${offset}".as[HotelPeop]
        } else if (search.address.length >= 1) {
          val address = s"%${search.address}%"
          sql"select id, CtfId, Name, Address, Mobile, Tel, Birthday, CTel from hotel.filter_for_310 where Address like ${address} limit ${lineNumber} offset ${offset}".as[HotelPeop]
        } else {
          sql"select id, CtfId, Name, Address, Mobile, Tel, Birthday, CTel from hotel.filter_for_310 limit ${lineNumber} offset ${offset}".as[HotelPeop]
        }
      case None =>
        sql"select id, CtfId, Name, Address, Mobile, Tel, Birthday, CTel from hotel.filter_for_310 limit ${lineNumber} offset ${offset}".as[HotelPeop]
    }

    val rows: Future[Vector[HotelPeop]] = dbConfig.db.run(sql)
    rows.map(rs => Ok(views.html.index(rs.toList, this.search)))
  }

  def del(id: Int) = Action.async {
    dbConfig.db.run(sqlu"delete from hotel.filter_for_310 where id = ${id}")
      .map(_ => Redirect(routes.HomeController.page(this.idx)))
    // Future.successful(Redirect(routes.HomeController.page(this.idx)))
  }

  def searchPost() = Action(parse.form(searchForm)) { implicit request =>
    this.search = Some(request.body)
    Redirect(routes.HomeController.page(0));
  }
}

