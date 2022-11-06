package clientCode;


import anotacije.definicije.Autowired;
import anotacije.definicije.Bean;
import enums.Scope;

@Bean(scope = Scope.SINGLETON)
public class MojBean {


    public MojBean(){
        System.out.println("Pozvao se konstruktor ------------------> MojBean");
    }

    public void mojTest(){
        System.out.println("--------------------------------PROSAO TEST ------------------------------------------");
    }
}
