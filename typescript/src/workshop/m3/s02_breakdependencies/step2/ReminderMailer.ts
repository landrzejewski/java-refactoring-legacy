import { IllegalStateError } from '../../../../shared/errors.js';

/** Statyczny klient SMTP. Poza produkcją serwer jest nieosiągalny. */
export class ReminderMailer {
  private constructor() {}

  static send(to: string, _subject: string, _body: string): void {
    throw new IllegalStateError(`SMTP smtp.kino.pl niedostepny (${to})`);
  }
}
