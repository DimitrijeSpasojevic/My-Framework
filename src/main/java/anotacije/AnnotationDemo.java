package anotacije;

import anotacije.definicije.AttributeInfo;
import anotacije.definicije.ClassInfo;
import anotacije.definicije.MethodInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

@ClassInfo(author = "Pera", version = "1.2.3")
public class AnnotationDemo {

	@AttributeInfo("Ovo je integer atribut")
	int counter = 1;

	@MethodInfo(version = "1.0", deprecated = false)
	public void f() {
		System.out.println("Ovo je metoda f().");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) {
		try {
			Class annotationDemoClass = Class.forName("anotacije.AnnotationDemo");
//			Class annotationDemoClass = AnnotationDemo.class;

			System.out.println("Anotacije AnnotationDemo klase: " + Arrays.toString(annotationDemoClass.getAnnotations()));
			if (annotationDemoClass.isAnnotationPresent(ClassInfo.class)) {
				ClassInfo ci = (ClassInfo) annotationDemoClass.getAnnotation(ClassInfo.class);
				System.out.println("Autor: " + ci.author());
				System.out.println("Verzija: " + ci.version());
			}
			System.out.println("-------------------------\n");


			Method methodF = annotationDemoClass.getDeclaredMethod("f");
			System.out.println("Anotacije metode f(): " + Arrays.toString(methodF.getAnnotations()));
			if (methodF.isAnnotationPresent(MethodInfo.class)) {
				MethodInfo mi = methodF.getAnnotation(MethodInfo.class);
				System.out.println("Deprecated: " + mi.deprecated());
				System.out.println("Version: " + mi.version());
			}
			System.out.println("-------------------------\n");


			Field fieldCounter = annotationDemoClass.getDeclaredField("counter");
			System.out.println("Anotacije atributa counter: " + Arrays.toString(fieldCounter.getAnnotations()));
			if (fieldCounter.isAnnotationPresent(AttributeInfo.class)) {
				AttributeInfo ai = fieldCounter.getAnnotation(AttributeInfo.class);
				System.out.println("value: " + ai.value());
			}
			System.out.println("-------------------------\n");


			Class childClass = Class.forName("anotacije.Child");
			System.out.println("Anotacije Child klase: " + Arrays.toString(childClass.getAnnotations()));
			if (childClass.isAnnotationPresent(ClassInfo.class)) {
				ClassInfo ci = (ClassInfo) childClass.getAnnotation(ClassInfo.class);
				System.out.println("Autor Child klase: " + ci.author());
				System.out.println("Verzija Child klase: " + ci.version());
			}
			System.out.println("-------------------------\n");

		} catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException ex) {
			ex.printStackTrace();
		}
	}
}
