package jp.iwmat.sawtter.services

import java.time.ZonedDateTime

import scala.concurrent.ExecutionContext

import org.scalatest._
import scalaz.\/

import jp.iwmat.sawtter.base._
import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.models.configurations._
import jp.iwmat.sawtter.models.mails.MailData
import jp.iwmat.sawtter.models.types._
import jp.iwmat.sawtter.repositories._
import jp.iwmat.sawtter.utils.Mailer

class AuthServiceSpec extends WordSpec with MustMatchers with Mock {

  val mockConf = SawtterConf(
    "MOCK_DOMAIN",
    SawtterHosts(
      "MOCK_FRONTEND_URL",
      "MOCK_BACKEND_URL"
    )
  )

  "signup" must {

    val payload = SignUp(Email("foo@bar"), Password("password"))

    val userToken = UserToken(ID(1L), Token(""), null)

    def createUser(status: UserStatus) = User(
      ID(1L),
      Email(""),
      status,
      Version(0L),
      null,
      null
    )

    def createService(
      execHook: Errors \/ _ => Unit,
      mockFindBy: Email[SignUp] => Transaction[Option[User]],
      mockAdd: SignUp => Transaction[UserToken] = _ => ???,
      mockMailer: Mailer = mock[Mailer]
    ) = new AuthService(
      new UserRepository {
        def findBy(userId: ID[User]): Transaction[Option[User]] = ???
        def findBy(email: Email[SignUp]): Transaction[Option[User]] = mockFindBy(email)
        def findBy(login: Login): Transaction[Option[User]] = ???
        def findToken(token: String): Transaction[Option[UserToken]] = ???
        def add(signup: SignUp): Transaction[UserToken] = mockAdd(signup)
        def enable(user: User): Transaction[Unit] = ???
      },
      mock[SessionRepository],
      mockMailer,
      mockConf
    )(
      mock[ExecutionContext],
      SpecDB(execHook)
    )

    "pass with new user" in {
      val service = createService(
        result => result mustBe \/.right(()),
        _ => SpecTransaction.right(None),
        _ => SpecTransaction.right(userToken),
        new Mailer {
          def send(mailData: MailData): Unit = {
            mailData.to mustBe payload.email
            mailData.from mustBe Email(s"info@${mockConf.domain}")
          }
        }
      )
      service.signup(payload)
    }

    "pass with disabled user" in {
      val disabledUser = createUser(UserStatus.Disabled)
      val service = createService(
        result => result mustBe \/.right(()),
        _ => SpecTransaction.right(Some(disabledUser)),
        _ => SpecTransaction.right(userToken),
        new Mailer {
          def send(mailData: MailData): Unit = {
            mailData.to mustBe payload.email
            mailData.from mustBe Email(s"info@${mockConf.domain}")
          }
        }
      )
      service.signup(payload)
    }

    "failed with enabled user" in {
      val enabledUser = createUser(UserStatus.Enabled)
      val service = createService(
        result => result mustBe \/.left(AuthServiceErrors.userNotExists(payload)),
        _ => SpecTransaction.right(Some(enabledUser))
      )
      service.signup(payload)
    }

    "failed with registered user" in {
      val registeredUser = createUser(UserStatus.Registered)
      val service = createService(
        result => result mustBe \/.left(AuthServiceErrors.userNotExists(payload)),
        _ => SpecTransaction.right(Some(registeredUser))
      )
      service.signup(payload)
    }
  }

  "verify" must {

    def createService(
      execHook: Errors \/ _ => Unit,
      mockFindToken: String => Transaction[Option[UserToken]],
      mockFindBy: ID[User] => Transaction[Option[User]] = _ => ???,
      mockEnable: User => Transaction[Unit] = _ => ???,
      mockAdd: User => String = _ => ???
    ) = new AuthService(
      new UserRepository {
        def findBy(userId: ID[User]): Transaction[Option[User]] = mockFindBy(userId)
        def findBy(email: Email[SignUp]): Transaction[Option[User]] = ???
        def findBy(login: Login): Transaction[Option[User]] = ???
        def findToken(token: String): Transaction[Option[UserToken]] = mockFindToken(token)
        def add(signup: SignUp): Transaction[UserToken] = ???
        def enable(user: User): Transaction[Unit] = mockEnable(user)
      },
      new SessionRepository {
        def fetch(key: String): Option[User] = ???
        def add(user: User): String = mockAdd(user)
        def delete(user: User): Unit = ???
      },
      mock[Mailer],
      mockConf
    )(
      mock[ExecutionContext],
      SpecDB(execHook)
    )

    val payload = "MOCK_USER_TOKEN"

    val userToken = UserToken(ID(1L), Token("MOCK_TOKEN"), null)

    val user = User(
      ID(1L),
      Email(""),
      UserStatus.Registered,
      Version(0L),
      null,
      null
    )

    "pass with normal user" in {
      val serssionKey = "MOCK_SESSION_KEY"
      val service = createService(
        result => result mustBe \/.right(serssionKey),
        _ => SpecTransaction.right(Some(userToken)),
        _ => SpecTransaction.right(Some(user)),
        _ => SpecTransaction.right(()),
        _ => serssionKey
      )
      service.verify(payload)
    }

    "failed with missing token" in {
      val service = createService(
        result => result mustBe \/.left(AuthServiceErrors.tokenNotFound(payload)),
        _ => SpecTransaction.right(None)
      )
      service.verify(payload)
    }

    "failed with missing user" in {
      val service = createService(
        result => result mustBe \/.left(AuthServiceErrors.userMustExists),
        _ => SpecTransaction.right(Some(userToken)),
        _ => SpecTransaction.right(None)
      )
      service.verify(payload)
    }
  }

  "login" must {

    def createService(
      execHook: Errors \/ _ => Unit,
      mockFindBy: Login => Transaction[Option[User]],
      mockAdd: User => String = _ => ???
    ) = new AuthService(
      new UserRepository {
        def findBy(userId: ID[User]): Transaction[Option[User]] = ???
        def findBy(email: Email[SignUp]): Transaction[Option[User]] = ???
        def findBy(login: Login): Transaction[Option[User]] = mockFindBy(login)
        def findToken(token: String): Transaction[Option[UserToken]] = ???
        def add(signup: SignUp): Transaction[UserToken] = ???
        def enable(user: User): Transaction[Unit] = ???
      },
      new SessionRepository {
        def fetch(key: String): Option[User] = ???
        def add(user: User): String = mockAdd(user)
        def delete(user: User): Unit = ???
      },
      mock[Mailer],
      mockConf
    )(
      mock[ExecutionContext],
      SpecDB(execHook)
    )

    val payload = Login(Email("foo@bar"), Password("password"))

    val user = User(
      ID(1L),
      Email(""),
      UserStatus.Registered,
      Version(0L),
      null,
      null
    )

    "pass with exists user" in {
      val serssionKey = "MOCK_SESSION_KEY"
      val service = createService(
        result => result mustBe \/.right(serssionKey),
        _ => SpecTransaction.right(Some(user)),
        _ => serssionKey
      )
      service.login(payload)
    }

    "failed with missing user" in {
      val service = createService(
        result => result mustBe \/.left(AuthServiceErrors.userNotFound(payload)),
        _ => SpecTransaction.right(None)
      )
      service.login(payload)
    }

  }
}
