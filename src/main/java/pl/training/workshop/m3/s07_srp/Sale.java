package pl.training.workshop.m3.s07_srp;

import java.math.BigDecimal;

/**
 * Stabilny kontrakt sceny - sprzedaż na jeden seans (kwoty brutto).
 *
 * @param tickets       liczba sprzedanych biletów
 * @param ticketRevenue przychód z biletów (VAT 8%)
 * @param barRevenue    przychód z baru (VAT 23%)
 */
public record Sale(String title, int tickets, BigDecimal ticketRevenue, BigDecimal barRevenue) {
}
