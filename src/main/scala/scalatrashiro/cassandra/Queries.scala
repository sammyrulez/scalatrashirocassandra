package scalatrashiro.cassandra

import java.util.UUID


import com.datastax.driver.core.{BoundStatement, Row}
import io.getquill._
import io.getquill.naming.SnakeCase
import io.getquill.source.{Encoder, Decoder}
import io.getquill.source.cassandra.CassandraSyncSource
import org.slf4j.{LoggerFactory, Logger}
import io.getquill.source.cassandra.ops._
import collection.JavaConversions._


/**
  * Created by sam on 03/02/16.
  */

object Queries {

  implicit val decodeCustomValue = mappedEncoding[UUID, String](_.toString)
  implicit val encodeCustomValue = mappedEncoding[String, UUID](UUID.fromString(_))


  implicit val stringSetDecoder: Decoder[Row,Set[String]] =
    new Decoder[Row,Set[String]] {
      def apply(index: Int, row: Row) =
        row.getSet(index,classOf[String]).toSet
    }

  implicit val stringSetEncoder: Encoder[BoundStatement,Set[String]] =
    new Encoder[BoundStatement,Set[String]] {
      override def apply(index: Int, value: Set[String], row: BoundStatement) = {
        row.setSet(index,value.toSet)
      }
    }




  object db extends CassandraSyncSource[SnakeCase]

  val logger:Logger = LoggerFactory.getLogger(Queries.getClass)
  object schema {

    val users = quote {
      query[UserModel]("users_logins")
    }



  }



  def usersById(id:UUID):Option[UserModel] = {

    val usersById = quote {
      (uid:UUID) =>
      schema.users.filter(s => s.id == uid)
    }

    val run= db.run(usersById)(id)

    run.size match {
      case 0 => None
      case 1 => Some(run.head)
      case _ => {
        logger.error(" more than one user with id " + id.toString)
        return None
      }
    }

  }

  def usersByUsername(username:String):Option[UserModel] = {

    val usersByUsername = quote {
      (usr:String) =>
        schema.users.filter(s => s.userName == usr)
    }

    val run: List[UserModel] = db.run(usersByUsername)(username)

    run.size match {
      case 0 => None
      case 1 => Some(run.head)
      case _ => {
        logger.error(" more than one user with username " + username.toString)
        return None
      }
    }

  }

  def saveUser(user:UserModel ): Unit ={


    val a = quote(schema.users.insert)
    val b = db.run(a)(List(user))
    logger.info("Save result of " +  user + " " + b.mkString(" :: "))


  }
  def updateUser(user:UserModel ): Unit ={
    val usersToUpdate = quote {
      (uid:UUID,username:String,email:String,enabled:Boolean) =>
        schema.users.filter(s => s.id == uid ).filter(s => s.userName == username).update(_.email -> email,_.enabled -> enabled)
    }
    val b = db.run(usersToUpdate)(List((user.id,user.userName,user.email,user.enabled)))
  //  logger.info("Updated result of " +  user + " " + b.mkString(" :: "))
  }

   def deleteUser(user: UserModel):Unit = {
   val usersById = quote {
      (uid:UUID) =>
        schema.users.filter(s => s.id == uid).delete
    }
    val b = db.run(usersById)(List(user.id))
    logger.info("Delete result of " +  user + " " + b.mkString(" :: "))
  }

  def disableUser(user:UserModel ): UserModel ={
    val disabledUser = user.copy(enabled = false)
    updateUser(disabledUser)
    return disabledUser
  }

  def enableUser(user:UserModel ): UserModel ={
    val disabledUser = user.copy(enabled = true)
    updateUser(disabledUser)
    return disabledUser
  }







}
