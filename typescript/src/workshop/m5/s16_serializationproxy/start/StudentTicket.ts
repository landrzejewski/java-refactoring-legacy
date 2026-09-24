/**
 * Start: bilet zapisywany przez SessionStore (np. sesja HTTP, cache, kolejka). Postać JSON to nazwy
 * WŁASNYCH pól obiektu - struktura klasy (nazwy i miejsce pól) jest częścią formatu danych.
 * Pola są prywatne tylko dla TS (nie #), bo pola # nie trafiłyby do JSON.
 */
export class StudentTicket {
  private readonly title: string;
  private readonly seat: string;
  private readonly studentId: string;

  constructor(title: string, seat: string, studentId: string) {
    this.title = title;
    this.seat = seat;
    this.studentId = studentId;
  }

  describe(): string {
    return this.title + ' ' + this.seat + ' (legitymacja ' + this.studentId + ')';
  }
}
