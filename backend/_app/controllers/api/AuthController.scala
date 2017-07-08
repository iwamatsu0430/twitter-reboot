package controllers.api

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.mvc.{Action, Controller}

import actions.AuthAction
import controllers.ResponseCode._
import models.{MemberConfirmHashModel, MemberModel}
import utils.JsonUtil._
import utils.MailUtil

case class SignUp(mail: String, password: String, passwordConfirm: String)
case class SignIn(mail: String, password: String)

/**
 * @author SAW
 */
class AuthController extends Controller {

  implicit val signUpReads: Reads[SignUp] = (
    (JsPath \ "mail").read[String](Reads.email) and
    (JsPath \ "password").read[String](pattern("""^\w+$""".r) keepAnd minLength[String](6) keepAnd maxLength[String](32)) and
    (JsPath \ "passwordConfirm").read[String](pattern("""^\w+$""".r) keepAnd minLength[String](6) keepAnd maxLength[String](32))
  )(SignUp.apply _)

  implicit val signInReads: Reads[SignIn] = (
    (JsPath \ "mail").read[String](Reads.email) and
    (JsPath \ "password").read[String](pattern("""^\w+$""".r) keepAnd minLength[String](6) keepAnd maxLength[String](32))
  )(SignIn.apply _)

  def signUp = Action.async(parse.json) { request =>
    request.body.validate[SignUp] match {
      case JsSuccess(value, path) => value.password == value.passwordConfirm match {
        case true => {
          MemberModel.findByMail(value.mail).flatMap {
            case Some(e) => Future.successful(BadRequest(createJson(MailIsUsed(value.mail))))
            case None => {
              val memberFuture = MemberModel.create(value.mail, value.password)
              memberFuture.map{ member =>
                val hash = MemberConfirmHashModel.create(member)
                val url = s"http://${request.domain}/confirm/${member.memberId}?hash=$hash"
                MailUtil.createSignUpMessage(url).sendTo(value.mail)
                Ok(createJson(NoReason, Json.toJson(url)))
              }
            }
          }
        }
        case _ => Future.successful(BadRequest(createJson(PasswordsNotMatch)))
      }
      case e: JsError => Future.successful(BadRequest(createJson(ValidationError, JsError.toJson(e))))
    }
  }

  def confirm(memberId: String) = Action.async { request =>
    MemberModel.findById(memberId).flatMap {
      case None => Future.successful(NotFound(createJson(MemberNotFound)))
      case Some(member) => {
        MemberConfirmHashModel.findHashValueByMemberId(member.memberId).map { confirmHashOpt =>
          confirmHashOpt match {
            case None => BadRequest(createJson(HashValuesNotMatch))
            case Some(confirmHash) => request.getQueryString("hash") match {
              case None => BadRequest (createJson (HashValuesNotMatch) )
              case Some (inputHash) if inputHash != confirmHash.hashValue => {
                println(s"$inputHash == ${confirmHash.hashValue}")
                BadRequest (createJson (HashValuesNotMatch) )
              }
              case _ => {
                confirmHash.complete
                member.confirm
                Ok(successJson)
              }
            }
          }
        }
      }
    }
  }

  def signIn = Action.async(parse.json) { request =>
    request.body.validate[SignIn] match {
      case JsSuccess(value, path) => MemberModel.findByMail(value.mail).map {
        case None => NotFound(createJson(MemberNotFound))
        case Some(member) if !member.isMatch(value.password) => BadRequest(createJson(SignInFailed))
        case Some(member) if !member.confirmed => BadRequest(createJson(NotConfirmed))
        case Some(member) => Ok(successJson).withSession(request.session + ("memberId" -> member.memberId))
      }
      case e: JsError => Future.successful(BadRequest(createJson(ValidationError, JsError.toJson(e))))
    }
  }

  def signOut = AuthAction {
    request =>
      Ok(successJson).withSession(request.session - "memberId")
  }
}
