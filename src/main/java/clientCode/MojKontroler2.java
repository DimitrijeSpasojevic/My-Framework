package clientCode;

import anotacije.definicije.*;

@Controller
public class MojKontroler2 {


    @Autowired(verbose = true)
    private MojBean mojBean2;

    @Autowired(verbose = true)
    @Qualifier("jedan")
    private MojInterfejs mojInterfejs22;


    @Autowired(verbose = true)
    private MojServis serviceKojiJeSingleton;

    private String neAnotiranoPolje = "moje neanotirano polje";


    public MojKontroler2(){
        System.out.println("Pozvan konstruktor -----------> MojKontroler2");
    }

    @Get
    @Path("/M")
    public void ruta33(){
        mojInterfejs22.metodaUinterfejsu();
    }

}
