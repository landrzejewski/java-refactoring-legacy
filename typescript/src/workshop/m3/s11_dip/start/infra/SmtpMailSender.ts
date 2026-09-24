/**
 * Szczegół techniczny: klient SMTP (symulowany). API mówi językiem protokołu:
 * surowa wiadomość MIME na wejściu, kod odpowiedzi SMTP na wyjściu.
 */
export class SmtpMailSender {
  private readonly transcriptLines: string[] = [];

  constructor(private readonly host: string, private readonly port: number) {}

  /** Symulacja: adres bez '@' daje 550, poprawny 250. */
  send(to: string, mimeMessage: string): string {
    if (!to.includes('@')) {
      return '550 mailbox unavailable';
    }
    this.transcriptLines.push(`${this.host}:${this.port} ${mimeMessage}`);
    return '250 OK';
  }

  transcript(): readonly string[] {
    return Object.freeze([...this.transcriptLines]);
  }
}
