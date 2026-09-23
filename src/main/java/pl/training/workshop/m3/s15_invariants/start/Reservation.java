package pl.training.workshop.m3.s15_invariants.start;

import java.math.BigDecimal;

/**
 * Start: anemiczny JavaBean z setterami. Model nie pilnuje żadnego inwariantu -
 * poprawność zależy od tego, czy KAŻDY, kto go tworzy, pamięta o walidacji.
 */
public class Reservation {
    private String email;
    private int seats;
    private BigDecimal total;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
