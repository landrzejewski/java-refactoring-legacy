package pl.training.workshop.m4.s08_movefield.step3;

/** Sala kinowa - właściciel progu VIP i pytania "czy ten rząd jest VIP". */
public final class Hall {
    private final String name;
    private final int vipFromRow;

    public Hall(String name, int vipFromRow) {
        this.name = name;
        this.vipFromRow = vipFromRow;
    }

    public String name() {
        return name;
    }

    public boolean isVip(int row) {
        return row >= vipFromRow;
    }
}
