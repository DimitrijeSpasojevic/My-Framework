package framework;

import anotacije.definicije.*;
import framework.request.Request;
import lombok.Synchronized;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class DIEngine {

    private Collection<String> controllersNamesList = Collections.synchronizedCollection(new ArrayList<>());
    private Collection<Class> controllers = Collections.synchronizedCollection(new ArrayList<>());
    private HashMap<Class,Object> mapClasObjectController= new HashMap<>();
    public DIEngine(){

    }

    public Set<Class> findAllClassesUsingClassLoader(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toSet());
    }

    private Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            // handle the exception
        }
        return null;
    }

    @Synchronized
    public void initializeControllers(Set<Class> classSet) {
        for(Class c : classSet){
            if (c.isAnnotationPresent(Controller.class)) {
                try {
                    if(!controllersNamesList.contains(c.getName())){
                        Constructor constructor = c.getDeclaredConstructor();
                        controllers.add(c);
                        mapClasObjectController.put(c, constructor.newInstance());
                        controllersNamesList.add(c.getName());
                    }else {
                        //todo
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
//                System.out.println("Nasao jedan conrtroler");
            }else{
//                System.out.println("klasa nadjena ali nije kontroler, nego klasa" + c.getName() + "i ima anotacije -> "+c.getAnnotations());
            }
        }
    }

    public Collection<Class> getControllers() {
        return controllers;
    }

    public void mapRequestToMethodOfController(Request request){
        List<Method> methodsFiltered = new ArrayList<>();
        for(Class controllerClass : controllers){
            if(request.getMethod().equals(framework.request.enums.Method.GET)){
                methodsFiltered = Arrays.stream(controllerClass.getMethods()).filter(m -> m.isAnnotationPresent(Path.class) && m.isAnnotationPresent(Get.class)).collect(Collectors.toList());;
            }else if(request.getMethod().equals(framework.request.enums.Method.POST)){
                methodsFiltered = Arrays.stream(controllerClass.getMethods()).filter(m -> m.isAnnotationPresent(Path.class) && m.isAnnotationPresent(Post.class)).collect(Collectors.toList());;
            }
            if(!methodsFiltered.isEmpty())
                System.out.println(methodsFiltered.get(0).getName());
                for(Method m : methodsFiltered){
                    Path path = m.getAnnotation(Path.class);
                    if(path.value().equals(request.getLocation())){
                        try {
                            m.invoke(mapClasObjectController.get(controllerClass));
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
    }

}
