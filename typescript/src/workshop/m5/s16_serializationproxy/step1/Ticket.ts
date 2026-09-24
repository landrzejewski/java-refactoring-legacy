/**
 * Krok 1: Extract Superclass + Pull Up Field (title, seat) - poprawne dla kodu, groźne dla danych.
 * Baza ma pola prywatne i akcesory title()/seat(); TS nie pozwala, by pole i metoda miały tę samą
 * nazwę, więc pola nazywają się teraz _title i _seat - a nazwy pól to klucze w JSON.
 */
export abstract class Ticket {
  private readonly _title: string;
  private readonly _seat: string;

  protected constructor(title: string, seat: string) {
    this._title = title;
    this._seat = seat;
  }

  title(): string {
    return this._title;
  }

  seat(): string {
    return this._seat;
  }
}
