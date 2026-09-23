package pl.training.workshop.m6.s04_encapsulatefactory;

import java.util.List;

import pl.training.workshop.shared.Money;

/** Stabilny kontrakt sceny: sprzedaż miejsc na jeden seans (rząd 10+ to VIP). */
public record SeatSale(String title, Money base, List<Integer> rows) {
}
