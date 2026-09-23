package pl.training.workshop.m4.s08_movefield.step3;

/**
 * Krok 3 (rozwiązanie): aktualizacja odczytów. Reguła "rząd >= próg" trafiła do Hall.isVip,
 * SeatPricer pyta o VIP-owość zamiast czytać surowy próg, a przejściowy akcesor
 * {@code Screening.vipFromRow()} został usunięty (Safe Delete). Jedno źródło prawdy.
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

    public boolean isVip(int row) {
        return hall.isVip(row);
    }
}
