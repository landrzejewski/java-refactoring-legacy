import { describe } from 'vitest';

import { LocalDateTime } from '../../../../src/workshop/shared/time.js';
import { Reservation } from '../../../../src/workshop/m3/s11_dip/Reservation.js';
import * as startMain from '../../../../src/workshop/m3/s11_dip/start/Main.js';
import * as step1Main from '../../../../src/workshop/m3/s11_dip/step1/Main.js';
import * as step1App from '../../../../src/workshop/m3/s11_dip/step1/app/ConfirmReservation.js';
import * as step1Infra from '../../../../src/workshop/m3/s11_dip/step1/infra/SmtpMailSender.js';
import * as step2Main from '../../../../src/workshop/m3/s11_dip/step2/Main.js';
import * as step2App from '../../../../src/workshop/m3/s11_dip/step2/app/ConfirmReservation.js';
import * as step2Infra from '../../../../src/workshop/m3/s11_dip/step2/infra/SmtpMailSender.js';
import * as step3Main from '../../../../src/workshop/m3/s11_dip/step3/Main.js';
import * as step3App from '../../../../src/workshop/m3/s11_dip/step3/app/ConfirmReservation.js';
import * as step3Notifier from '../../../../src/workshop/m3/s11_dip/step3/infra/SmtpCustomerNotifier.js';
import * as step3Infra from '../../../../src/workshop/m3/s11_dip/step3/infra/SmtpMailSender.js';
import { Scene } from '../../support/scene.js';

const DUNE = new Reservation('anna@kino.pl', 'Diuna', LocalDateTime.of(2026, 10, 2, 20, 0), 2);
const BAD_EMAIL = new Reservation('jan-kino.pl', 'Amator', LocalDateTime.of(2026, 10, 3, 18, 30), 1);
const NO_SEATS = new Reservation('jan@kino.pl', 'Amator', LocalDateTime.of(2026, 10, 3, 18, 30), 0);

function safe(useCase: { confirm(reservation: Reservation): string }): (r: Reservation) => string {
  return (r) => {
    try {
      return useCase.confirm(r);
    } catch (error) {
      return `blad: ${(error as Error).message}`;
    }
  };
}

/** Potwierdzenie działa tak samo; od kroku 1 sprawdzamy też, co faktycznie poszło przez SMTP. */
describe('S11EquivalenceTest', () => {
  describe('everyStepConfirmsTheSame', () => {
    Scene.variants<Reservation, string>()
      .variant('start', safe(startMain.confirmReservation()))
      .variant('step1', safe(step1Main.confirmReservation()))
      .variant('step2', safe(step2Main.confirmReservation()))
      .variant('step3', safe(step3Main.confirmReservation()))
      .expect('poprawna rezerwacja', DUNE, 'potwierdzono: anna@kino.pl')
      .expect('serwer odrzuca adres', BAD_EMAIL, 'blad: SMTP odrzucil: 550 mailbox unavailable')
      .expect('rezerwacja bez miejsc', NO_SEATS, 'blad: rezerwacja bez miejsc')
      .tests();
  });

  describe('stepsSendTheSameMimeMessage', () => {
    Scene.variants<Reservation, string>()
      .variant('step1', (r) => {
        const smtp = new step1Infra.SmtpMailSender('smtp.kino.pl', 25);
        new step1App.ConfirmReservation(smtp).confirm(r);
        return smtp.transcript().join('|');
      })
      .variant('step2', (r) => {
        const smtp = new step2Infra.SmtpMailSender('smtp.kino.pl', 25);
        new step2App.ConfirmReservation(smtp).confirm(r);
        return smtp.transcript().join('|');
      })
      .variant('step3', (r) => {
        const smtp = new step3Infra.SmtpMailSender('smtp.kino.pl', 25);
        new step3App.ConfirmReservation(new step3Notifier.SmtpCustomerNotifier(smtp)).confirm(r);
        return smtp.transcript().join('|');
      })
      .expect('MIME dla Diuny', DUNE, 'smtp.kino.pl:25 To: anna@kino.pl\r\nSubject: Rezerwacja\r\n\r\n'
        + 'Rezerwacja: Diuna, 2026-10-02T20:00, miejsc: 2. Zaplac w ciagu 15 minut.')
      .tests();
  });
});
