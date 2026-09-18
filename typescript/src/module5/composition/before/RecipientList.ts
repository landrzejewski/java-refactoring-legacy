// Dziedziczenie po kolekcji: klient dostaje całe API tablicy (push, splice, sort, length = 0, ...),
// choć potrzebuje tylko kilku operacji. Odpowiednik "extends ArrayList<String>".
export class RecipientList extends Array<string> {
  snapshot(): readonly string[] {
    return Object.freeze([...this]);
  }
}
