package pl.training.workshop.m4.s08_movefield.step1;

/**
 * Krok 1: Self-Encapsulate Field - pole prywatne, WSZYSTKIE odczyty (także wewnątrz klasy)
 * idą przez {@code vipFromRow()}. Teraz jest jedno miejsce, w którym zmienimy źródło wartości.
 */
public final class Screening {
    private final Hall hall;
    private final int format;
    private final int vipFromRow;

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

    public int vipFromRow() {
        return vipFromRow;
    }

    public boolean isVip(int row) {
        return row >= vipFromRow();
    }
}
