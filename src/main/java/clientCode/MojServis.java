package clientCode;

import anotacije.definicije.Autowired;
import anotacije.definicije.Service;

@Service
public class MojServis {

    @Autowired(verbose = true)
    private MojBean mojBean3;

    public MojServis(){
        System.out.println("Pozvao se konstruktor ---------------------> MojServis");
    }

    public void test(){
        System.out.println("-----------test za servis prosao-----------------");
    }


}
