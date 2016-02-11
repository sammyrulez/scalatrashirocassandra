package scalatrashiro.cassandra

import java.nio.charset.StandardCharsets
import java.security.MessageDigest


import java.util.{Base64, UUID}



/**
  * Created by sam on 03/02/16.
  */
case class UserModel(val id: UUID, val userName: String, val passwordData: String, val salt: String, val enabled: Boolean, val email: String,roles:Set[String])

object  UserModel {

  val digest = MessageDigest.getInstance("SHA-256");

  def apply(username:String,clearTextPassword:String,email:String,roles:Set[String]) = {

    val salt = UUID.randomUUID().toString
    val password = hashPassword(clearTextPassword, salt)

    new UserModel(UUID.randomUUID(), username, password, salt, false, email,roles)

  }

  def checkPassword(user:UserModel,password:String):Boolean = {
    user.passwordData.equals(hashPassword(password,user.salt))
  }

  private def hashPassword(clearTextPassword: String, salt: String): String = {
    new String(Base64.getEncoder.encode(digest.digest((clearTextPassword + salt).getBytes(StandardCharsets.UTF_8))))
  }
}



