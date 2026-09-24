import { describe } from 'vitest';

import { Scene } from '../../support/scene.js';
import { runS13Script } from './S13Script.js';
import * as start from './start/Driver.js';
import * as step1 from './step1/Driver.js';
import * as step2 from './step2/Driver.js';
import * as step3 from './step3/Driver.js';
import * as step4 from './step4/Driver.js';

/**
 * Golden master kampanii Remove God Class: start (kopia legacy) i każdy krok muszą dać
 * DOKŁADNIE ten sam wektor zachowania - wyniki, maile, SMS-y, operacje bramki, raporty.
 * Oczekiwany tekst jest kopią src/test/resources/workshop/cinema-manager.approved.txt.
 */
const APPROVED = ''
  + 'book: B1 B2 B3 B4 B5\n'
  + 'book taken: ERROR: seat taken A5\n'
  + 'book no seat: ERROR: no such seat Z99\n'
  + 'book mismatch: ERROR: types do not match seats\n'
  + 'book no screening: ERROR: no screening S9\n'
  + 'pay b1: OK\n'
  + 'pay b1 again: ERROR: already paid\n'
  + 'pay b2 declined: ERROR: payment declined\n'
  + 'pay b2: OK\n'
  + 'pay b4: OK\n'
  + 'pay b3 expired: ERROR: expired\n'
  + 'cancel b1 early: REFUND 105.00\n'
  + 'cancel b2 late: REFUND 49.20\n'
  + 'cancel b2 again: ERROR: cannot cancel\n'
  + 'use b4: OK\n'
  + 'use b5 unpaid: ERROR: not paid\n'
  + 'points anna: 0\n'
  + 'points szkola: 15\n'
  + 'free S1: 120\n'
  + 'RAPORT DZIENNY 2026-03-10\n'
  + 'Amator: 10 bil., 153.00\n'
  + 'Biletow: 10\n'
  + 'Przychod z biletow: 153.00\n'
  + 'Oplaty rezerwacyjne: 20.00\n'
  + 'Netto (bez VAT 8%): 141.67\n'
  + 'ROZLICZENIE Amator tydzien 1: przychod 153.00, dla dystrybutora 500.00\n'
  + 'ROZLICZENIE Diuna tydzien 2: przychod 0.00, dla dystrybutora 500.00\n'
  + 'MAIL to=anna@kino.pl subject=Rezerwacja B1 body=Film: Diuna, miejsca: A5,B5,C10, do zaplaty: 114.00\n'
  + 'MAIL to=jan@kino.pl subject=Rezerwacja B2 body=Film: Kraina Lodu, miejsca: A1,B1,C1,D7, do zaplaty: 104.40\n'
  + 'MAIL to=ola@kino.pl subject=Rezerwacja B3 body=Film: Kraina Lodu, miejsca: E2,F2, do zaplaty: 42.00\n'
  + 'MAIL to=szkola@kino.pl subject=Rezerwacja B4 body=Film: Amator, miejsca: A1,B1,C1,D1,E1,F1,G1,H1,I1,J1, do zaplaty: 173.00\n'
  + 'MAIL to=piotr@kino.pl subject=Rezerwacja B5 body=Film: Amator, miejsca: K9, do zaplaty: 35.00\n'
  + 'MAIL to=anna@kino.pl subject=Bilety B1 body=Oplacono 114.00, punkty: +10\n'
  + 'SMS to=600100200 text=CineLegacy: bilety B1 oplacone\n'
  + 'MAIL to=jan@kino.pl subject=Platnosc odrzucona body=Rezerwacja B2\n'
  + 'MAIL to=jan@kino.pl subject=Bilety B2 body=Oplacono 104.40, punkty: +10\n'
  + 'MAIL to=szkola@kino.pl subject=Bilety B4 body=Oplacono 173.00, punkty: +15\n'
  + 'MAIL to=ola@kino.pl subject=Rezerwacja wygasla body=B3\n'
  + 'MAIL to=piotr@kino.pl subject=Rezerwacja wygasla body=B5\n'
  + 'MAIL to=anna@kino.pl subject=Anulowano B1 body=Zwrot: 105.00\n'
  + 'MAIL to=jan@kino.pl subject=Anulowano B2 body=Zwrot: 49.20\n'
  + 'CHARGED 4111111111111111 114.00\n'
  + 'DECLINED 4111111111110000 104.40\n'
  + 'CHARGED 5555444433331111 104.40\n'
  + 'CHARGED 4000123412341234 173.00\n'
  + 'REFUND 4111111111111111 105.00\n'
  + 'REFUND 5555444433331111 49.20\n';

describe('S13GoldenMasterTest', () => {
  describe('everyStepOfTheCampaignKeepsTheApprovedBehaviour', () => {
    Scene.variants<string, string>()
      .variant('start', () => runS13Script(new start.Driver()))
      .variant('step1', () => runS13Script(new step1.Driver()))
      .variant('step2', () => runS13Script(new step2.Driver()))
      .variant('step3', () => runS13Script(new step3.Driver()))
      .variant('step4', () => runS13Script(new step4.Driver()))
      .expect('jeden dzien kina (S13Script)', '2026-03-09/10', APPROVED)
      .tests();
  });
});
