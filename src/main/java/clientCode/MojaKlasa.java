package clientCode;

import anotacije.definicije.*;

@Controller
public class MojaKlasa {


    @Autowired(verbose = true)
    private MojServis verzija2ServicaKojiTrebaIstiDaBude;

    @Autowired(verbose = true)
    @Qualifier("dva")
    private MojInterfejs mojInterfejs11;


    @Autowired(verbose = true)
    private MojBean mojBean;

    private String neAnotiranoPolje = "moje neanotirano polje";


    public MojaKlasa(){
        System.out.println("Pozvan konstruktor -----------> mojaKlasa Controler");
    }

    @Get
    @Path("/")
    public void ruta1(){
        mojBean.mojTest();
        mojInterfejs11.metodaUinterfejsu();
    }

    @Get
    @Path("/NNN")
    public void gsdgsdgsdgssdgsd(){
        verzija2ServicaKojiTrebaIstiDaBude.test();
    }

}
