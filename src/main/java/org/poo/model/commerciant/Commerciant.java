package org.poo.model.commerciant;

public final class Commerciant {
    private String commerciant;
    private int id;
    private String account;
    private String type;
    private String cashbackStrategy;

    public Commerciant(final String commerciant, final int id, final String account,
                       final String type, final String cashbackStrategy) {
        this.commerciant = commerciant;
        this.id = id;
        this.account = account;
        this.type = type;
        this.cashbackStrategy = cashbackStrategy;
    }

    public String getCommerciant() {
        return commerciant;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getAccount() {
        return account;
    }

    public String getCashbackStrategy() {
        return cashbackStrategy;
    }
}
