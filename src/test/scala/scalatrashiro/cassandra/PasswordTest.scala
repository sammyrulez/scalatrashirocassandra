package scalatrashiro.cassandra

import org.scalatest.{Matchers, FlatSpec}

/**
  * Created by sam on 11/02/16.
  */
class PasswordTest extends FlatSpec with Matchers {


  "A user " should  "be authenticated " in {
    val sesame: String = "sesame"
    val model: UserModel = UserModel("ben", sesame, "s@a.it",Set("admin","dev"))
    Queries.saveUser(model)
    val resultOk = UserModel.checkPassword(model,sesame)
    resultOk shouldBe true
    Queries.deleteUser(model)
  }

  "A user " should  "be not  authenticated if wrong password is passed " in {
    val sesame: String = "sesame"
    val model: UserModel = UserModel("ben", sesame, "s@a.it",Set("admin","dev"))
    Queries.saveUser(model)
    val resultOk = UserModel.checkPassword(model,"wrong")
    resultOk shouldBe false
    Queries.deleteUser(model)
  }


}
