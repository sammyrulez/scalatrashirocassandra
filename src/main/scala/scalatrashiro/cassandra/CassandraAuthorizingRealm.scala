package scalatrashiro.cassandra

import org.apache.shiro.authc.{AuthenticationException, UsernamePasswordToken, AuthenticationInfo, AuthenticationToken}
import org.apache.shiro.authz.{SimpleAuthorizationInfo, AuthorizationInfo}
import org.apache.shiro.realm.AuthorizingRealm
import org.apache.shiro.subject.{SimplePrincipalCollection, PrincipalCollection}
import scala.collection.JavaConversions._

/**
  * Created by sam on 03/02/16.
  */
class CassandraAuthorizingRealm extends AuthorizingRealm{

  override def doGetAuthorizationInfo(principals: PrincipalCollection): AuthorizationInfo = {

    val  roles:Set[String] = principals.fromRealm(this.getName).toList.map(principal => {
      Queries.usersByUsername(principal.toString) match {
        case Some(usermodel) => usermodel.roles
      }
    }).flatten.map(x => x.toString).toSet

    new  SimpleAuthorizationInfo(roles)
  }


  override def doGetAuthenticationInfo(token: AuthenticationToken): AuthenticationInfo = {
    val realmName = this.getName
    val userAndPassword = token.asInstanceOf[UsernamePasswordToken]
     Queries.usersByUsername(userAndPassword.getUsername) match {
       case None => throw new AuthenticationException()
       case Some(userModel) => {
         if (UserModel.checkPassword(userModel,new String(userAndPassword.getPassword))){
           new AuthenticationInfo {override def getPrincipals: PrincipalCollection = new SimplePrincipalCollection(userModel.userName,realmName)

             override def getCredentials: AnyRef = userModel
           }
         }else
           throw new AuthenticationException()
       }
     }



  }


}
