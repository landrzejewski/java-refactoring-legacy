/** Statyczny klient SMTP. Z testu nie widać, co i do kogo wysłał. */
export class CinemaMailer {
  private constructor() {}

  static send(_to: string, _text: string): void {
    // produkcyjnie: SMTP smtp.kino.pl
  }
}
