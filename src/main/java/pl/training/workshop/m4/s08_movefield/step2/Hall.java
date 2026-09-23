package pl.training.workshop.m4.s08_movefield.step2;

/** Sala kinowa - od kroku 2 jedyny właściciel progu VIP. */
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

    public int vipFromRow() {
        return vipFromRow;
    }
}
