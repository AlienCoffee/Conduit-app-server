@file:Suppress ("UNCHECKED_CAST")
package ru.shemplo.conduit.appserver;

import ru.shemplo.conduit.appserver.entities.wrappers.*;
import ru.shemplo.conduit.appserver.entities.data.*;
import ru.shemplo.conduit.appserver.entities.*;
import java.time.*;
import java.util.*;

private fun <T> i (obj : Any, method : String) : T = obj.javaClass.getMethod (method).invoke (obj) as T;

public fun AbsAuditableEntity.getAuthor () = i <UserEntity> (this, "getAuthor");
public fun AbsAuditableEntity.getAvailable () = i <Boolean> (this, "getAvailable");
public fun AbsAuditableEntity.getChanged () = i <LocalDateTime> (this, "getChanged");
public fun AbsAuditableEntity.getComment () = i <String> (this, "getComment");
public fun AbsAuditableEntity.getCommitter () = i <UserEntity> (this, "getCommitter");
public fun AbsAuditableEntity.getIssued () = i <LocalDateTime> (this, "getIssued");
public fun BlogPostEntity.getAttachments () = i <List <FileEntity>> (this, "getAttachments");
public fun BlogPostEntity.getChannels () = i <Set <String>> (this, "getChannels");
public fun BlogPostEntity.getContent () = i <String> (this, "getContent");
public fun BlogPostEntity.getPublished () = i <LocalDateTime> (this, "getPublished");
public fun BlogPostEntity.getTitle () = i <String> (this, "getTitle");
public fun OptionEntity.getName () = i <String> (this, "getName");
public fun PeriodEntity.getDescription () = i <String> (this, "getDescription");
public fun PeriodEntity.getName () = i <String> (this, "getName");
public fun PeriodEntity.getSince () = i <LocalDateTime> (this, "getSince");
public fun PeriodEntity.getStatus () = i <PeriodStatus> (this, "getStatus");
public fun PeriodEntity.getUntil () = i <LocalDateTime> (this, "getUntil");
public fun RoleEntity.getName () = i <String> (this, "getName");
public fun RoleEntity.getOptions () = i <Set <OptionEntity>> (this, "getOptions");
public fun RoleEntity.getTemplate () = i <PersonalDataTemplate> (this, "getTemplate");
public fun UserEntity.getIsAdmin () = i <Boolean> (this, "getIsAdmin");
public fun UserEntity.getLogin () = i <String> (this, "getLogin");
public fun UserEntity.getPassword () = i <String> (this, "getPassword");
public fun UserEntity.getPhone () = i <String> (this, "getPhone");
public fun WUser.getEntity () = i <UserEntity> (this, "getEntity");
