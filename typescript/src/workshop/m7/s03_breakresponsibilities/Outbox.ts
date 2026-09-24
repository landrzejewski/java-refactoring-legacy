/** Stabilny kontrakt sceny: skrzynka nadawcza - efekt uboczny, który test obserwuje. */
export class Outbox {
  private readonly sentList: string[] = [];

  send(to: string | null, text: string): void {
    this.sentList.push(`${to}: ${text}`);
  }

  sent(): readonly string[] {
    return Object.freeze([...this.sentList]);
  }
}
