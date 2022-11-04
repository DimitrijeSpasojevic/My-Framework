package anotacije.definicije;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * Anotacija je dostupna u runtime-u
 */
@Retention(RetentionPolicy.RUNTIME)
/*
 * Samo metode se mogu anotirati ovom anotacijom.
 */
@Target(ElementType.METHOD)
public @interface MethodInfo {
	/**
	 * Atribut anotacije moze imati podrazumevanu vrednost
	 */
	boolean deprecated() default true;

	String version();
}
