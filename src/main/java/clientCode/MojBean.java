package clientCode;


import anotacije.definicije.Autowired;
import anotacije.definicije.Bean;

@Bean
public class MojBean {

    @Autowired(verbose = true)
    private DrugiServis drugiServis;

    public MojBean(){
        System.out.println("Pozvao se konstruktor ------------------> MojBean");
    }

    public void mojTest(){
        System.out.println("--------------------------------PROSAO TEST ------------------------------------------");
    }
}
