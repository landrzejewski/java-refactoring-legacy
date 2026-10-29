package pl.training.workshop.exercises.e04_seatpicker;

/** Stabilny kontrakt sceny: miejsce w sali zapisywane jako "rzad-numer", np. 6-4. */
public record Seat(int row, int number) {
    public static Seat parse(String text) {
        String[] parts = text.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("zly format miejsca: " + text);
        }
        try {
            return new Seat(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("zly format miejsca: " + text);
        }
    }

    @Override
    public String toString() {
        return row + "-" + number;
    }
}
