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
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DIEngine {

    private Collection<String> controllersNamesList = Collections.synchronizedCollection(new ArrayList<>());
    private Collection<Class> controllers = Collections.synchronizedCollection(new ArrayList<>());
    private Map<Class,Object> mapClasObjectController= new ConcurrentHashMap<>();
    private Map<Class,Object> mapClasObjectService= new ConcurrentHashMap<>();
    private Map<Object,List<Class>> map= new ConcurrentHashMap<>();
    private Collection<Object> objects = Collections.synchronizedCollection(new ArrayList<>());
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
    public void initializeControllers(Set<Class> classSet) throws IllegalAccessException {
        for(Class c : classSet){
            if (c.isAnnotationPresent(Controller.class)) {
                try {
                    if(!controllersNamesList.contains(c.getName())){
                        Constructor constructor = c.getDeclaredConstructor();
                        controllers.add(c);
                        Object o = constructor.newInstance();
                        mapClasObjectController.put(c, o);
                        controllersNamesList.add(c.getName());
                        initAutowiredFields( null , c, false, "Controller");
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }else{
//                System.out.println("klasa nadjena ali nije kontroler, nego klasa" + c.getName() + "i ima anotacije -> "+c.getAnnotations());
            }
        }
    }

    private Object findObjectOfClass(Class cl){
        for(Object o : objects){
            if(o.getClass().equals(cl)){
                return o;
            }
        }
        return null;
    }

//cFireld controler na pocetku
    private void initAutowiredFields(Class parentClass, Class cField, boolean verbose, String fieldName) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if(!cField.isAnnotationPresent(Controller.class) && !parentClass.isAnnotationPresent(Controller.class)) {
            Object whichObjectToSet = null;

            whichObjectToSet = parentClass.getDeclaredConstructor().newInstance();
            objects.add(whichObjectToSet);
//                if(c.isAnnotationPresent(Service.class)){
//                    if(!mapClasObjectService.containsKey(c)){
//                        Object service = c.getConstructor().newInstance();
//                        mapClasObjectService.put(c,service);
//                        Field field = parentClass.getDeclaredField(fieldName);
//                    }else{
////                        c. = mapClasObjectService.get(c)??
//                    }
//                }
//                else if(c.isAnnotationPresent(Component.class)) {
//                    Object obj = c.getConstructor().newInstance();
//                }
            // pamtimo komObjektuSEtujem
            if(map.get(whichObjectToSet) == null){
                List<Class> klase = new ArrayList<>();
                klase.add(cField);
                map.put(whichObjectToSet,klase);
            }else {
                List<Class> klase = map.get(whichObjectToSet);
                klase.add(cField);
                map.put(whichObjectToSet,klase);
            }

            if (verbose) {
//                  “Initialized <param.object.type> <param.name> in <param.parentClass.name> on <localDateTime.now()> with <param.instance.hashCode>”
                System.out.println("Initialized " + cField.getTypeName() + " " + cField.getName() + " in " + parentClass.getName() + " on " + LocalDateTime.now() + " with ");
            }
        }else if(parentClass != null){
            if(map.get(mapClasObjectController.get(parentClass)) == null){
                List<Class> klase = new ArrayList<>();
                klase.add(cField);
                map.put(mapClasObjectController.get(parentClass),klase);
            }else {
                List<Class> klase = map.get(mapClasObjectController.get(parentClass));
                klase.add(cField);
                map.put(mapClasObjectController.get(parentClass),klase);
            }
        }
        List<Field> fields = Arrays.stream(cField.getDeclaredFields()).filter(field -> field.isAnnotationPresent(Autowired.class)).collect(Collectors.toList());
        if(fields.isEmpty()){

            objects.add(cField.getDeclaredConstructor().newInstance());

        }
        for(Field field : fields){
            Autowired autowired = field.getAnnotation(Autowired.class);
            initAutowiredFields(cField, field.getType(), autowired.verbose(), field.getName());
        }

    }

    public Collection<Class> getControllers() {
        return controllers;
    }

    public void mapRequestToMethodOfController(Request request) throws IllegalAccessException {

        for (Map.Entry<Object,List<Class>> entry : map.entrySet()){
            Object objectToInitializeFields = entry.getKey();
            List<Field> fields = List.of(objectToInitializeFields.getClass().getDeclaredFields());
            for (Field f : fields){
                f.setAccessible(true);
                f.set(objectToInitializeFields,findObjectOfClass(f.getType()));
            }
        }

        List<Method> methodsFiltered = new ArrayList<>();
        for(Class controllerClass : controllers){
            if(request.getMethod().equals(framework.request.enums.Method.GET)){
                methodsFiltered = Arrays.stream(controllerClass.getMethods()).filter(m -> m.isAnnotationPresent(Path.class) && m.isAnnotationPresent(Get.class)).collect(Collectors.toList());
            }else if(request.getMethod().equals(framework.request.enums.Method.POST)){
                methodsFiltered = Arrays.stream(controllerClass.getMethods()).filter(m -> m.isAnnotationPresent(Path.class) && m.isAnnotationPresent(Post.class)).collect(Collectors.toList());
            }
            if(!methodsFiltered.isEmpty())
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
