/**
 * Krok 3: wąski parametr zbierający - można tylko dopisać ostrzeżenie. Metody pomocnicze
 * nie mogą niczego usunąć, wyczyścić ani przestawić.
 */
export class Warnings {
  private readonly items: string[] = [];

  add(warning: string): void {
    this.items.push(warning);
  }

  summary(): string {
    return this.items.length === 0 ? 'OK' : this.items.join('; ');
  }
}
