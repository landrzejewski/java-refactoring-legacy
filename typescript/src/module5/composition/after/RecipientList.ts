// Replace Inheritance with Delegation: lista jest prywatna, na zewnątrz tylko potrzebne operacje.
export class RecipientList implements Iterable<string> {
  readonly #recipients: string[] = [];

  add(recipient: string): boolean {
    this.#recipients.push(recipient);
    return true;
  }

  remove(recipient: string): boolean {
    const index = this.#recipients.indexOf(recipient);
    if (index < 0) {
      return false;
    }
    this.#recipients.splice(index, 1);
    return true;
  }

  contains(recipient: string): boolean {
    return this.#recipients.includes(recipient);
  }

  size(): number {
    return this.#recipients.length;
  }

  // Iterator nie daje dostępu do tablicy, więc nie pozwala jej zmienić.
  [Symbol.iterator](): Iterator<string> {
    return this.#recipients.values();
  }

  snapshot(): readonly string[] {
    return Object.freeze([...this.#recipients]);
  }
}
