package framework;

import anotacije.definicije.*;
import enums.Scope;
import framework.request.Request;
import framework.request.exceptions.MyException;
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
import java.util.stream.Collectors;

public class DIEngine {

    private Collection<String> controllersNamesList = new ArrayList<>();
    private Collection<Class> controllers = new ArrayList<>();
    private Map<Class,Object> mapClasObjectController= new HashMap<>();
    private Map<Class,Object> mapClasObjectService= new HashMap<>();
    private Collection<Object> objects = new ArrayList<>();
    private DependencyContainer container = new DependencyContainer();
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
    public void initializeControllers(Set<Class> classSet) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {

        for(Class c : classSet){
            if (c.isAnnotationPresent(Controller.class)) {
                try {
                    if(!controllersNamesList.contains(c.getName())){
                        Constructor constructor = c.getDeclaredConstructor();
                        controllers.add(c);
                        Object o = constructor.newInstance();
                        objects.add(o);
                        mapClasObjectController.put(c, o);
                        controllersNamesList.add(c.getName());
                        initAutoWiredFields(c);
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }else{
                if(c.isAnnotationPresent(Qualifier.class)){
                    Qualifier qualifier = (Qualifier) c.getDeclaredAnnotation(Qualifier.class);
                    if(container.qualifierAlreadyExist(qualifier.value())) throw new MyException("QualifierAlreadyExistException -> Vec postoji qualifier sa \"" + qualifier.value() + "\" imenom");
                    else this.container.addImplementationInMap(qualifier.value(), c.getDeclaredConstructor().newInstance());
                }
            }
        }
//        List<Object> objs = new ArrayList<>();
//        objs.addAll(objects);
        for (Object objectToInitializeFields : objects){
            List<Field> fields = List.of(objectToInitializeFields.getClass().getDeclaredFields());
            List<Field> autoWiredFields = fields.stream().filter(fi -> fi.isAnnotationPresent(Autowired.class)).collect(Collectors.toList());
            for (Field f : autoWiredFields){
                f.setAccessible(true);
                Object objToSet = null;
                if(f.isAnnotationPresent(Qualifier.class)){
                    Qualifier qualifier = f.getAnnotation(Qualifier.class);
                    objToSet = container.getImplementation(qualifier.value());
                    if(objToSet.getClass().isAnnotationPresent(Component.class)){
                        objToSet = objToSet.getClass().getDeclaredConstructor().newInstance();
                    }else if(objToSet.getClass().isAnnotationPresent(Bean.class)){
                        Bean bean = objToSet.getClass().getDeclaredAnnotation(Bean.class);
                        if(bean.scope().equals(Scope.PROTOTYPE)){
                            objToSet = objToSet.getClass().getDeclaredConstructor().newInstance();
                        }
                    }
                }else {
                    objToSet = findObjectOfClass(f.getType());
                }
                f.set(objectToInitializeFields,objToSet);
                Autowired autowired = f.getAnnotation(Autowired.class);
                if(autowired.verbose()) {
                    System.out.println("Initialized " + f.getType() + " " + f.getName() + " in " + objectToInitializeFields.getClass().getName() + " on " + LocalDateTime.now() + " with " + objToSet.hashCode());
                }
            }
        }
    }

    private Object findObjectOfClass(Class cl){
        boolean singletonObject = true;
        for(Object o : objects){
            if(o.getClass().equals(cl)){
                if(o.getClass().isAnnotationPresent(Component.class)){
                    singletonObject = false;
                }else if(o.getClass().isAnnotationPresent(Bean.class)){
                    Bean bean = o.getClass().getDeclaredAnnotation(Bean.class);
                    if(bean.scope().equals(Scope.PROTOTYPE)){
                        singletonObject = false;
                    }
                }
                if(!singletonObject){
                    objects.remove(o);
                }
                return o;
            }
        }
        return null;
    }

    private void initAutoWiredFields(Class klasaZaKojuSePraviObjekat) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if(!klasaZaKojuSePraviObjekat.isAnnotationPresent(Controller.class) && !klasaZaKojuSePraviObjekat.isInterface()){

           if(klasaZaKojuSePraviObjekat.isAnnotationPresent(Service.class) && mapClasObjectService.get(klasaZaKojuSePraviObjekat)== null ){
                Object objectService = klasaZaKojuSePraviObjekat.getDeclaredConstructor().newInstance();
                mapClasObjectService.put(klasaZaKojuSePraviObjekat,objectService);
                objects.add(objectService);
            }
            if(klasaZaKojuSePraviObjekat.isAnnotationPresent(Component.class)){
                Object objectComponent = klasaZaKojuSePraviObjekat.getDeclaredConstructor().newInstance();
                objects.add(objectComponent);
            }
            if(klasaZaKojuSePraviObjekat.isAnnotationPresent(Bean.class)){
                Bean bean = (Bean) klasaZaKojuSePraviObjekat.getAnnotation(Bean.class);
                if(bean.scope().equals(Scope.PROTOTYPE)){
                    Object objectBean = klasaZaKojuSePraviObjekat.getDeclaredConstructor().newInstance();
                    objects.add(objectBean);
                }else{ // anotacija je sington mora se proveriti dal smo vec napravili objekat
                    boolean foundObjectOfSingletonClass = false;
                    for (Object o : objects){
                        if(o.getClass().equals(klasaZaKojuSePraviObjekat)){
                            foundObjectOfSingletonClass = true;
                            break;
                        }
                    }
                    if(!foundObjectOfSingletonClass){
                        Object objectBean = klasaZaKojuSePraviObjekat.getDeclaredConstructor().newInstance();
                        objects.add(objectBean);
                    }
                }
            }

        }
        List<Field> fieldsAnnotatedWithAutowired = Arrays.stream(klasaZaKojuSePraviObjekat.getDeclaredFields()).filter(field -> field.isAnnotationPresent(Autowired.class)).collect(Collectors.toList());
        for(Field field : fieldsAnnotatedWithAutowired){
            if(field.getType().isInterface() && !field.isAnnotationPresent(Qualifier.class)){
                throw new MyException("Atribut " + field.getName() + " je anotiran sa @Autowired a nije sa @Qualifier\n");
            }
            if(!field.getType().isAnnotationPresent(Service.class) && !field.getType().isAnnotationPresent(Component.class) && !field.getType().isAnnotationPresent(Bean.class) ){
                throw new MyException("@Autowired na atributu " + field.getName() + " koji nije @Bean (ili @Service ili @Component)\n");
            }
            initAutoWiredFields(field.getType());
        }
    }

    public void mapRequestToMethodOfController(Request request) throws IllegalAccessException {

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
