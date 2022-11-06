package clientCode;

import anotacije.definicije.Qualifier;

@Qualifier("dva")
public class KlasaKojaImplementiraMojInterfejs implements MojInterfejs{
    @Override
    public void metodaUinterfejsu() {
        System.out.println("Iz interfejsa metoda klasa 1");
    }
}
