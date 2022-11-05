package clientCode;

import anotacije.definicije.*;

@Controller
public class MojaKlasa {

    @Autowired(verbose = false)
    private String mojePolje = "moje polje";

    @Autowired(verbose = false)
    private MojServis mojServis;

    @Autowired(verbose = false)
    private MojBean mojBean;

    private String neAnotiranoPolje = "moje neanotirano polje";


    public MojaKlasa(){
        System.out.println("Pozvan konstruktor -----------> mojaKlasa Controler");
    }

    @Get
    @Path("/")
    public void ruta1(){
        mojBean.mojTest();
    }

    @Get
    @Path("/NNN")
    public void gsdgsdgsdgssdgsd(){
        mojServis.test();
    }

}
