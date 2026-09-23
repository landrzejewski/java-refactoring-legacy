package pl.training.workshop.m3.s15_invariants.step1;

import java.math.BigDecimal;

/**
 * Krok 1: Remove Setting Method + konwersja na record (⌥⏎ "Convert to record class").
 * Obiekt jest niezmienny i powstaje w całości w jednym wywołaniu - ale konstruktor
 * wciąż przyjmie wszystko.
 */
public record Reservation(String email, int seats, BigDecimal total) {
}
