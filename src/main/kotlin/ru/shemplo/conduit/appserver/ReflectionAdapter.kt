@file:Suppress ("UNCHECKED_CAST")
package ru.shemplo.conduit.appserver;

import ru.shemplo.conduit.appserver.entities.wrappers.WUser
import ru.shemplo.conduit.appserver.entities.UserEntity

private fun <T> i (obj : Any, method : String) : T = obj.javaClass.getMethod (method).invoke (obj) as T;

public fun WUser.getEntity () = i <UserEntity> (this, "getEntity");
public fun UserEntity.getLogin () = i <String> (this, "getLogin");