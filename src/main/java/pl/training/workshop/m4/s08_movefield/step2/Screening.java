package pl.training.workshop.m4.s08_movefield.step2;

/**
 * Krok 2: Move Field - {@code vipFromRow} przeniesione do Hall. Screening nie ma już pola,
 * a jego akcesor deleguje do sali. Bez okresu przejściowego z dwiema kopiami (dual write).
 * Konstruktor stracił parametr - to zmiana dla wszystkich miejsc tworzących seanse.
 */
public final class Screening {
    private final Hall hall;
    private final int format;

    public Screening(Hall hall, int format) {
        this.hall = hall;
        this.format = format;
    }

    public Hall hall() {
        return hall;
    }

    public int format() {
        return format;
    }

    public int vipFromRow() {
        return hall.vipFromRow();
    }

    public boolean isVip(int row) {
        return row >= vipFromRow();
    }
}
