package pl.training.workshop.m4.s08_movefield.start;

/**
 * Start: próg VIP to cecha SALI, a jest polem SEANSU. Każdy seans w tej samej sali niesie
 * własną kopię i nic nie pilnuje, żeby kopie były zgodne. SeatPricer czyta pole bezpośrednio.
 */
public final class Screening {
    private final Hall hall;
    private final int format;
    final int vipFromRow;

    public Screening(Hall hall, int format, int vipFromRow) {
        this.hall = hall;
        this.format = format;
        this.vipFromRow = vipFromRow;
    }

    public Hall hall() {
        return hall;
    }

    public int format() {
        return format;
    }

    public boolean isVip(int row) {
        return row >= vipFromRow;
    }
}
