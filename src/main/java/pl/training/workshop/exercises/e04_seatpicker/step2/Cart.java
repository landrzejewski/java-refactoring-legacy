package pl.training.workshop.exercises.e04_seatpicker.step2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pl.training.workshop.exercises.e04_seatpicker.HallPlan;
import pl.training.workshop.exercises.e04_seatpicker.Seat;
import pl.training.workshop.shared.Money;

/**
 * Krok 2: koszyk sam robi migawkę całego swojego stanu i sam ją przywraca. Migawka jest nieprzezroczysta
 * dla innych klas (prywatne pola, brak metod), więc nikt poza koszykiem nie zna ani nie kopiuje jego pól.
 * Nowe pole koszyka wystarczy dopisać w jednym miejscu: tu.
 */
public final class Cart {
    private static final Money BASE = Money.of("32.00");
    private static final Money VIP = Money.of("10.00");
    private static final Money GLASSES = Money.of("3.00");
    private static final int LIMIT = 4;

    private final HallPlan plan;
    private final List<Seat> seats = new ArrayList<>();
    private boolean ownGlasses;
    private String voucher;

    /** Migawka koszyka: tylko koszyk ją tworzy i tylko koszyk ją czyta. */
    public static final class Memento {
        private final List<Seat> seats;
        private final boolean ownGlasses;
        private final String voucher;

        private Memento(List<Seat> seats, boolean ownGlasses, String voucher) {
            this.seats = List.copyOf(seats);
            this.ownGlasses = ownGlasses;
            this.voucher = voucher;
        }
    }

    public Cart(HallPlan plan) {
        this.plan = plan;
    }

    public Memento snapshot() {
        return new Memento(seats, ownGlasses, voucher);
    }

    public void restore(Memento memento) {
        seats.clear();
        seats.addAll(memento.seats);
        ownGlasses = memento.ownGlasses;
        voucher = memento.voucher;
    }

    public HallPlan plan() {
        return plan;
    }

    public List<Seat> seats() {
        return List.copyOf(seats);
    }

    public void add(Seat seat) {
        checkFree(seat);
        if (seats.size() >= LIMIT) {
            throw new IllegalStateException("limit " + LIMIT + " miejsc");
        }
        seats.add(seat);
    }

    /** Usuwa miejsce i zwraca jego pozycję, żeby cofnięcie mogło ją przywrócić. */
    public int remove(Seat seat) {
        int index = seats.indexOf(seat);
        if (index < 0) {
            throw new IllegalArgumentException("miejsca " + seat + " nie ma w koszyku");
        }
        seats.remove(index);
        return index;
    }

    public void insert(int index, Seat seat) {
        seats.add(index, seat);
    }

    /** Zamienia miejsce na inne na tej samej pozycji i zwraca tę pozycję. */
    public int replace(Seat from, Seat to) {
        int index = seats.indexOf(from);
        if (index < 0) {
            throw new IllegalArgumentException("miejsca " + from + " nie ma w koszyku");
        }
        checkFree(to);
        seats.set(index, to);
        return index;
    }

    public void replaceAt(int index, Seat seat) {
        seats.set(index, seat);
    }

    public void clear() {
        seats.clear();
    }

    public boolean ownGlasses() {
        return ownGlasses;
    }

    public void setOwnGlasses(boolean ownGlasses) {
        this.ownGlasses = ownGlasses;
    }

    public String voucher() {
        return voucher;
    }

    public void setVoucher(String voucher) {
        this.voucher = voucher;
    }

    private void checkFree(Seat seat) {
        if (!plan.exists(seat)) {
            throw new IllegalArgumentException("nie ma miejsca " + seat);
        }
        if (plan.isTaken(seat)) {
            throw new IllegalStateException("miejsce " + seat + " jest zajete");
        }
        if (seats.contains(seat)) {
            throw new IllegalStateException("miejsce " + seat + " jest juz w koszyku");
        }
    }

    public String state() {
        String list = seats.isEmpty() ? "-" : seats.stream().map(Seat::toString).collect(Collectors.joining(" "));
        return "miejsca: " + list
                + " | okulary: " + (ownGlasses ? "wlasne" : "wypozyczone")
                + " | voucher: " + (voucher == null ? "-" : voucher)
                + " | suma: " + total();
    }

    private Money total() {
        Money sum = Money.ZERO;
        for (Seat seat : seats) {
            Money price = BASE;
            if (plan.isVip(seat)) {
                price = price.plus(VIP);
            }
            if (!ownGlasses) {
                price = price.plus(GLASSES);
            }
            sum = sum.plus(price);
        }
        if ("KINO10".equals(voucher)) {
            sum = sum.minus(sum.percent(10));
        }
        return sum;
    }
}
