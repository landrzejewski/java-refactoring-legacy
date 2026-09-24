import type { Decimal } from 'decimal.js';

/**
 * Krok 1: Remove Setting Method - pola readonly ustawiane w konstruktorze
 * (właściwości parametrów). Obiekt jest niezmienny i powstaje w całości w jednym
 * wywołaniu - ale konstruktor wciąż przyjmie wszystko.
 */
export class Reservation {
  constructor(readonly email: string, readonly seats: number, readonly total: Decimal) {}
}
