package pl.training.workshop.m5.s12_bridgemethods.step1;

import pl.training.workshop.shared.Money;
import pl.training.workshop.m5.s12_bridgemethods.Ticket;

/**
 * Krok 1: Extract Interface - generyczna rola reguły cenowej.
 * Po erasure {@code apply(T)} to {@code apply(Ticket)}, więc każda implementacja z konkretnym T
 * dostaje od kompilatora syntetyczną metodę bridge {@code apply(Ticket)} z rzutowaniem.
 */
public interface PriceRule<T extends Ticket> {
    Money apply(T ticket);
}
