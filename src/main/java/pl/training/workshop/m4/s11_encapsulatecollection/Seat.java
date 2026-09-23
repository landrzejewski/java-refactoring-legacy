package pl.training.workshop.m4.s11_encapsulatecollection;

/** Stabilny kontrakt sceny: miejsce (niezmienny rekord - niemodyfikowalna lista wystarczy). */
public record Seat(int row, int number) {
    @Override
    public String toString() {
        return row + "/" + number;
    }
}
