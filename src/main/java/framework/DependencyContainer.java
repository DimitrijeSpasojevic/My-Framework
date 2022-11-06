package framework;

import java.util.HashMap;
import java.util.Map;

public class DependencyContainer {

    private Map<String,Object> map = new HashMap<>();

    public void addImplementationInMap(String qualifier, Object concreteImplementation){
        map.put(qualifier,concreteImplementation);
    }

    public Object getImplementation(String qualifier){
        return map.get(qualifier);
    }

    public boolean qualifierAlreadyExist(String qualifier){
        for (Map.Entry<String,Object> entry : map.entrySet()){
            if(entry.getKey().equals(qualifier)) return true;
        }
        return false;
    }
}
