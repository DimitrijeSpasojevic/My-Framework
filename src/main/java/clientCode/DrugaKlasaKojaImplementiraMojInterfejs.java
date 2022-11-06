package clientCode;

import anotacije.definicije.*;
import enums.Scope;

@Qualifier("jedan")
@Bean(scope = Scope.SINGLETON)
public class DrugaKlasaKojaImplementiraMojInterfejs implements MojInterfejs{
    @Override
    public void metodaUinterfejsu() {
        System.out.println("Iz interfejsa metoda klasa 2");
    }
}
