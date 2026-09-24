import { describe, expect, it } from 'vitest';

import * as step1 from '../../../../src/workshop/m7/s15_behaviourvector/step1/TicketCheckout.js';
import * as step2 from '../../../../src/workshop/m7/s15_behaviourvector/step2/TicketCheckout.js';
import * as step3 from '../../../../src/workshop/m7/s15_behaviourvector/step3/TicketCheckout.js';
import { Scene } from '../../support/scene.js';
import { Payment } from './Payment.js';

/**
 * Wektor obserwowalnego zachowania rośnie razem z seamami:
 * krok 1 - wynik + maile (regresja widoczna), krok 2 - naprawa,
 * krok 3 - wynik, wyjątek, maile i obciążenia w jednej kolejności, stan rezerwacji.
 */
class MailLog {
  readonly entries: string[] = [];

  readonly add = (to: string, text: string): void => {
    this.entries.push(`${to}: ${text}`);
  };
}

/** Wynik + lista maili (dostępne od kroku 1). */
function withMails(payment: Payment, pay: (mails: MailLog, payment: Payment) => string): string {
  const mails = new MailLog();
  const result = pay(mails, payment);
  return `${result} [${mails.entries.join(', ')}]`;
}

/** Pełny wektor: wynik albo wyjątek, efekty w kolejności, stan po operacji. */
function fullVector(payment: Payment): string {
  const events: string[] = [];
  const checkout = new step3.TicketCheckout(
    (to, text) => events.push(`MAIL ${to}: ${text}`),
    (card, amount) => {
      events.push(`CHARGE ${card} ${amount.toString()}`);
      return !card.endsWith('0000');
    });
  const booking = payment.booking();
  const vector = (outcome: string): string => `${outcome}; zdarzenia=[${events.join(', ')}]; status=${booking.status}`;
  try {
    return vector(`wynik=${checkout.pay(booking, payment.card)}`);
  } catch (error) {
    return vector(`wyjatek=${(error as Error).name}: ${(error as Error).message}`);
  }
}

describe('S15BehaviourVectorTest', () => {
  it('step1SeesTheRegressionThatResultOnlyTestMissed', () => {
    expect(withMails(Payment.DECLINED, (mails, p) => new step1.TicketCheckout(mails.add).pay(p.booking(), p.card)))
      .toBe('DECLINED [anna@kino.pl: Platnosc odrzucona B1, anna@kino.pl: Bilety B1 oplacone: 114.00]');
  });

  describe('fromStep2MailsAreCorrect', () => {
    Scene.variants<Payment, string>()
      .variant('step2', (p) => withMails(p, (mails, payment) =>
        new step2.TicketCheckout(mails.add).pay(payment.booking(), payment.card)))
      .variant('step3', (p) => withMails(p, (mails, payment) =>
        new step3.TicketCheckout(mails.add, (card) => !card.endsWith('0000'))
          .pay(payment.booking(), payment.card)))
      .expect('sukces', Payment.SUCCESS, 'OK [anna@kino.pl: Bilety B1 oplacone: 114.00]')
      .expect('karta odrzucona', Payment.DECLINED, 'DECLINED [anna@kino.pl: Platnosc odrzucona B1]')
      .expect('juz oplacona', Payment.ALREADY_PAID, 'ERROR: status PAID []')
      .tests();
  });

  describe('step3ObservesTheFullVector', () => {
    Scene.variants<Payment, string>()
      .variant('step3', fullVector)
      .expect('sukces: najpierw obciazenie, potem mail, status PAID', Payment.SUCCESS,
        'wynik=OK; zdarzenia=[CHARGE 4111111111111111 114.00, '
          + 'MAIL anna@kino.pl: Bilety B1 oplacone: 114.00]; status=PAID')
      .expect('odrzucenie: status bez zmian', Payment.DECLINED,
        'wynik=DECLINED; zdarzenia=[CHARGE 4111111111110000 114.00, '
          + 'MAIL anna@kino.pl: Platnosc odrzucona B1]; status=NEW')
      .expect('juz oplacona: zadnych efektow', Payment.ALREADY_PAID,
        'wynik=ERROR: status PAID; zdarzenia=[]; status=PAID')
      .expect('brak karty: typ i komunikat wyjatku, zadnych efektow', Payment.NO_CARD,
        'wyjatek=NullPointerError: card; zdarzenia=[]; status=NEW')
      .tests();
  });
});
