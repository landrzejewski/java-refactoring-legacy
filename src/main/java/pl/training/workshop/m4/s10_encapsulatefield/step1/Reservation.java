package pl.training.workshop.m4.s10_encapsulatefield.step1;

/**
 * Krok 1: Encapsulate Field - pole prywatne, trywialne akcesory na TYM SAMYM polu.
 * Zachowanie bez zmian (setter przyjmuje wszystko), ale każdy zapis przechodzi teraz przez nas.
 */
public final class Reservation {
    private String status = "NEW";

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
