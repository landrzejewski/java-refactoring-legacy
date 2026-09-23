package pl.training.workshop.m5.s14_reuse.step2;

/**
 * Krok 2: Extract Interface - rola dla raportu. Wspólny KONTRAKT (właściciel i saldo) jest prawdziwy
 * dla obu kont, więc tu podtypowanie jest uczciwe. Wymiany na bilet w nim nie ma.
 */
public interface PointsHolder {
    String owner();

    int points();
}
