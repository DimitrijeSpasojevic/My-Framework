package anotacije.definicije;

import java.lang.annotation.*;

/*
 * Na kom nivou ce anotacija biti dostupna: source/class/runtime
 */
@Retention(RetentionPolicy.RUNTIME)
/*
 * Samo klase/interfejs/enum se mogu anotiarti ovom anotacijom
 */
@Target(ElementType.TYPE)
/*
 * Oznacava da se anotacija automatski naledjuje
 */
@Inherited
public @interface ClassInfo {
	String author();

	String version();
}
