package clientCode;

import anotacije.definicije.Autowired;
import anotacije.definicije.Controller;
import anotacije.definicije.Get;
import anotacije.definicije.Path;

@Controller
public class MojKontroler2 {


    @Autowired(verbose = true)
    private MojServis mojServis;

    private String neAnotiranoPolje = "moje neanotirano polje";


    public MojKontroler2(){
        System.out.println("Pozvan konstruktor -----------> MojKontroler2");
    }

}
