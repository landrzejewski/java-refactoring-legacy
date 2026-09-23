package pl.training.workshop.m4.s09_extractclass;

import java.math.BigDecimal;
import java.util.List;

/**
 * Stabilne wejście testu.
 *
 * @param cards kolejne próby płatności (każda próba to jedna karta)
 */
public record BookingInput(String id, String name, String email, String phone, BigDecimal amount,
                           List<String> cards) {
}
