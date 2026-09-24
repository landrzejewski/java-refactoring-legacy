import type { Money } from '../../../shared/Money.js';

/** Krok 2 (ścieżka A): korekta ceny za dzień jako wymienny algorytm - Strategy. */
export type DayPolicy = (base: Money) => Money;
