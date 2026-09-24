import type { Screening } from '../Screening.js';

/**
 * Krok 1: Change Signature - print przyjmuje wszystko, czego potrzebuje,
 * pola i settery usunięte (Safe Delete). Protokół "najpierw ustaw, potem drukuj"
 * zamienił się w kontrakt sprawdzany przez kompilator, a obiekt jest bezstanowy
 * i bezpieczny do współdzielenia.
 */
export class TicketPrinter {
  print(screening: Screening, seat: number, buyer: string): string {
    return `BILET ${screening.title} (${screening.format}) ${screening.start.toString()}`
      + `, miejsce ${seat}, dla ${buyer.toLowerCase()}`;
  }
}
