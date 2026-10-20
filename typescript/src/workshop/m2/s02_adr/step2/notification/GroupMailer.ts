/** Krok 2 (bez zmian): powiadomienia - wołane tylko przez warstwę aplikacji. */
export class GroupMailer {
  private readonly sentList: string[] = [];

  groupDiscountGranted(organizer: string, tickets: number): void {
    this.sentList.push(organizer + ': rabat grupowy dla ' + tickets + ' biletow');
  }

  sent(): readonly string[] {
    return Object.freeze([...this.sentList]);
  }
}
