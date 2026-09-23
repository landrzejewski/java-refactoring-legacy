package pl.training.workshop.m7.s03_breakresponsibilities;

import java.util.List;

/**
 * Stabilny kontrakt sceny: prośba o rezerwację.
 *
 * @param seats miejsca w postaci litera + rząd, np. C10 (rząd 10 i dalej to VIP)
 */
public record BookingRequest(String email, String format, List<String> seats) {
}
