package pl.training.workshop.m6.s07_decorator;

import pl.training.workshop.shared.Money;

/**
 * Stabilny kontrakt sceny: zamówienie biletu z dodatkami. VIP +10.00, okulary +3.00 dla 3D
 * (chyba że klient ma własne), ubezpieczenie biletu +4.00 (wartość przykładowa).
 */
public record TicketOrder(String title, String format, Money base,
                          boolean vip, boolean ownGlasses, boolean insurance) {
}
