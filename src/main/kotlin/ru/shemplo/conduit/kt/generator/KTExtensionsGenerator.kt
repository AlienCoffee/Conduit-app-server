package ru.shemplo.conduit.kt.generator;

import lombok.Getter
import ru.shemplo.conduit.ts.generator.Generator
import ru.shemplo.snowball.utils.ClasspathManager
import java.io.PrintWriter
import java.util.HashSet
import java.lang.reflect.Modifier.*
import java.lang.reflect.Method
import java.lang.reflect.Field
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import java.lang.reflect.ParameterizedType
import ru.shemplo.snowball.utils.MiscUtils
import java.util.LinkedHashSet
import java.lang.StringBuilder
import java.time.LocalDateTime
import java.io.ByteArrayOutputStream
import javax.persistence.Entity
import java.util.Objects
import java.nio.charset.StandardCharsets

public class KTExtensionsGenerator (val cm : ClasspathManager) : Generator {
	
	private val classes : List <Class <*>>;
	
	init {
		val annotation = KTEntity::class.java;
		classes = cm.findObjectsWithAnnotation (setOf (annotation)) [annotation]!!.map { obj ->
			        when (obj) {
						is Class <*> -> obj; is Field -> obj.getDeclaringClass ()
						else -> null
					}
				}.filter (Objects::nonNull).map {obj -> obj as Class <*>}.toList ();
	}
	
	public override fun print (pw : PrintWriter) {
	    pw.println ("@file:Suppress (\"UNCHECKED_CAST\")");
		pw.println ("package ru.shemplo.conduit.appserver;");
		pw.println ();
		
		val buffer = ByteArrayOutputStream ();
		val apw = PrintWriter (buffer);
		
		val toImport = classes.toMutableSet ();
		printInvocations (apw, toImport);
		printImports (toImport, pw);
		
		pw.println ();
		pw.println ("private fun <T> i (obj : Any, method : String) : T = "
				    + "obj.javaClass.getMethod (method).invoke (obj) as T;");
		pw.println ();
		
		apw.flush ();
		pw.print (buffer.toString (StandardCharsets.UTF_8));
	}
	
	private fun printImports (clss : Collection <Class <*>>, pw : PrintWriter) {
		clss.map { cls -> cls.getPackage() }.distinct ().sortedBy { -it.getName ().length }
		. forEach { pkg -> pw.println (String.format ("import %s.*;", pkg.name)); };
	}
	
	private fun printInvocations (pw : PrintWriter, toImport : MutableSet <Class <*>>) {
		fun m2f (name : String) : String { // method name to field name
			if (name.length < 4) { return ""; } // name is too short for getter
			return Character.toLowerCase (name [3]) + name.substring (4);
		}
		
		classes.sortedWith (Comparator.comparing (Class <*>::getName)).forEach { cls ->
			val fields = cls.getDeclaredFields ()
					   . filter { field ->
						   Character.isLowerCase (field.name [0])
						   && !isPublic (field.getModifiers ())
						   && !isStatic (field.getModifiers ())
					   }
				       . map (Field::getName).toSet ();
			
			cls.getMethods ().filter { method ->
				fields.contains (m2f (method.getName ())) && method.getName().startsWith ("get")
			    && !isStatic (method.getModifiers ()) && isPublic (method.getModifiers ())
			}.sortedWith (Comparator.comparing (Method::getName)).forEach { method ->
				val type = translateJavaType (method.getGenericReturnType (), toImport);
				pw.println (String.format ("public fun %s.%s () = i <%s> (this, \"%s\");",
					cls.getSimpleName (), method.getName (), type, method.getName ()));
			}
		};
	}
	
	private fun translateJavaType (type : Type, toImport : MutableSet <Class <*>>) : String {
	    val sb = StringBuilder ();
		
		fun translate (t : Type) {
		    when (t) {
		        is Class <*> -> {
					if (t.isArray ()) {
					    sb.append ("Array <");
						translate (t.getComponentType ());
					    sb.append (">");
					} else if (t.isPrimitive ()) {
						sb.append (t.name.capitalize ());
					} else {
						sb.append (t.getSimpleName ());
						if (!t.getPackageName ().equals ("java.lang")) {
							toImport.add (t);
						}
					}
		        }
		        
		        is ParameterizedType -> {
		            val ct = t.getRawType () as Class <*>;
					if (t !is Collection <*> && t !is Map <*, *>) {
					    toImport.add (ct);
					}
					
					sb.append (ct.getSimpleName ()).append (" <");
					t.getActualTypeArguments ().map { arg -> translateJavaType (arg, toImport) }
					 .joinToString (", ").run (sb::append);
					sb.append (">");
		        }
		        
		        else -> throw NotImplementedError (type.getTypeName ())
		    }
		}
		
		translate (type);
		return sb.toString ();
	}
	
}