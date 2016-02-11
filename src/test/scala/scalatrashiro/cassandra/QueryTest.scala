package scalatrashiro.cassandra

import java.util

import org.scalatest._
/**
  * Created by sam on 03/02/16.
  */
class QueryTest extends FlatSpec with Matchers {


  "A user " should  "be created " in {
    val model: UserModel = UserModel("ben", "sesame", "s@a.it",Set("admin","dev"))
    Queries.saveUser(model)
    Queries.usersById(model.id) should not be None
    Queries.deleteUser(model)

  }

  "A user " should  "be updated " in {
    val model: UserModel = UserModel("ben", "sesame", "s@a.it",Set())
    Queries.saveUser(model)

    val updated = model.copy(email = "b@uu.it")

    Queries.updateUser(updated)

    Queries.usersById(model.id) should not be None

   val myUser = Queries.usersById(model.id).get

   myUser.email shouldEqual  "b@uu.it"

    Queries.deleteUser(model)

  }

  "A user " should  "be initialy disabled " in {
    val model: UserModel = UserModel("ben", "sesame", "s@a.it",Set())
    Queries.saveUser(model)
    model.enabled shouldBe false
    Queries.deleteUser(model)

  }

  "A user " should  "be enabled " in {
    val model: UserModel = UserModel("ben", "sesame", "s@a.it",Set())
    Queries.saveUser(model)
    val newModel = Queries.enableUser(model)
    newModel.enabled shouldBe true
    Queries.deleteUser(model)

  }

}
