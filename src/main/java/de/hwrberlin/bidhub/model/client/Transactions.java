package de.hwrberlin.bidhub.model.client;

import javafx.beans.property.SimpleStringProperty;

public class Transactions {
    private final SimpleStringProperty produkt;
    private final SimpleStringProperty preis;
    private final SimpleStringProperty person;

    public Transactions(String produkt, String preis, String person) {
        this.produkt = new SimpleStringProperty(produkt);
        this.preis = new SimpleStringProperty(preis);
        this.person = new SimpleStringProperty(person);
    }

    public String getProdukt() {
        return produkt.get();
    }

    public void setProdukt(String produkt) {
        this.produkt.set(produkt);
    }

    public String getPreis() {
        return preis.get();
    }

    public void setPreis(String preis) {
        this.preis.set(preis);
    }

    public String getPerson() {
        return person.get();
    }

    public void setPerson(String person) {
        this.person.set(person);
    }
}
