/**
 * Krok 2: Extract Interface - rola dla raportu. Wspólny KONTRAKT (właściciel i saldo) jest prawdziwy
 * dla obu kont, więc tu podtypowanie jest uczciwe. Wymiany na bilet w nim nie ma.
 */
export interface PointsHolder {
  owner(): string;

  points(): number;
}
