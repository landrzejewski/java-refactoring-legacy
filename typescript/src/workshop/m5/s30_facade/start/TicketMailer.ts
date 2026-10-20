/** Podsystem: wysyłka biletów mailem. */
export class TicketMailer {
  private readonly outbox: string[] = [];

  send(email: string, text: string): void {
    this.outbox.push(`${email}: ${text}`);
  }

  sent(): readonly string[] {
    return [...this.outbox];
  }
}
