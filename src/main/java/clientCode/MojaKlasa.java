package clientCode;

import anotacije.definicije.Controller;
import anotacije.definicije.Get;
import anotacije.definicije.Path;
import anotacije.definicije.Post;

@Controller
public class MojaKlasa {

    public MojaKlasa(){
        System.out.println("Pozvan konstruktor MojaKlasa");
    }

    @Get
    @Path("/")
    public void ruta1(){
        System.out.println("pozvana metoda");
    }

    @Get
    @Path("/NNN")
    public void gsdgsdgsdgssdgsd(){
        System.out.println("NOVA METODA SA NNN pozvanan");
    }

}
