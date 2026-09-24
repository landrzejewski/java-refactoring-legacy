import { describe, expect, it } from 'vitest';

import { Booking } from '../../../../src/workshop/m4/s00_characterization/Booking.js';
import * as start from '../../../../src/workshop/m4/s00_characterization/start/BookingConfirmation.js';
import * as step1 from '../../../../src/workshop/m4/s00_characterization/step1/BookingConfirmation.js';
import * as step2 from '../../../../src/workshop/m4/s00_characterization/step2/BookingConfirmation.js';
import { fixedClock, LocalDateTime, LocalTime } from '../../../../src/workshop/shared/time.js';
import { Scene } from '../../support/scene.js';

/**
 * Test charakterystyki generatora potwierdzeń - zapisuje, co kod ROBI dziś.
 * Powstaje w trzech ruchach (patrz przewodnik):
 * A) start: "scrubber" maskuje linię z bieżącym czasem, locale ustawiamy jawnie;
 * B) step1: wstrzyknięty Clock pozwala porównać cały dokument bez maskowania;
 * C) step2: ten sam test chroni pierwszą refaktoryzację.
 * Nazwy przypadków z "ZASTANE" opisują dziwne zachowanie, którego NIE poprawiamy w refaktoryzacji.
 */
const SERVER_LOCALE = 'pl-PL';
const FIXED = fixedClock(LocalDateTime.of(2026, 9, 23, 8, 15, 30));

const IMAX_ONLINE = new Booking('  anna@kino.pl ', 'Diuna', 3,
  LocalTime.of(20, 0), ['N', 'S'], true);
const MORNING_3D = new Booking('jan@kino.pl', 'Kraina Lodu', 2,
  LocalTime.of(11, 0), ['E', 'C'], false);
const TEN_TICKETS = new Booking('jan@kino.pl', 'Amator', 1,
  LocalTime.of(18, 30), Array<string>(10).fill('N'), true);
const ELEVEN_TICKETS = new Booking('jan@kino.pl', 'Amator', 1,
  LocalTime.of(18, 30), Array<string>(11).fill('N'), false);

function scrubbed(variant: (booking: Booking) => string, booking: Booking): string {
  const document = inLocale(SERVER_LOCALE, () => variant(booking));
  return document.replace(/Wygenerowano: .*\n/g, 'Wygenerowano: <czas>\n');
}

/**
 * Ustawia domyślne locale tylko na czas wywołania - test nie zależy od maszyny.
 * JS nie ma Locale.setDefault, więc podmieniamy domyślny argument Number.prototype.toLocaleString
 * i przywracamy oryginał w finally.
 */
function inLocale(locale: string, action: () => string): string {
  const original = Number.prototype.toLocaleString;
  Number.prototype.toLocaleString = function (this: number, locales?: Intl.LocalesArgument,
    options?: Intl.NumberFormatOptions): string {
    return original.call(this, locales ?? locale, options);
  };
  try {
    return action();
  } finally {
    Number.prototype.toLocaleString = original;
  }
}

describe('S00CharacterizationTest', () => {
  describe('everyVariantPrintsTheApprovedDocument', () => {
    Scene.variants<Booking, string>()
      .variant('start', (b) => scrubbed((x) => new start.BookingConfirmation().confirm(x), b))
      .variant('step1', (b) => scrubbed((x) => new step1.BookingConfirmation(FIXED).confirm(x), b))
      .variant('step2', (b) => scrubbed((x) => new step2.BookingConfirmation(FIXED).confirm(x), b))
      .expect('IMAX wieczorem, online, klient z odstępami', IMAX_ONLINE, `POTWIERDZENIE REZERWACJI
Klient: ANNA@KINO.PL
Film: Diuna, IMAX, 20:00
Bilety: 2 [N, S]
Bilety razem: 70,00
Oplata rezerwacyjna: 4,00
Do zaplaty: 74,00
Wygenerowano: <czas>
`)
      .expect('3D rano, senior i dziecko, kasa', MORNING_3D, `POTWIERDZENIE REZERWACJI
Klient: JAN@KINO.PL
Film: Kraina Lodu, 3D, 11:00
Bilety: 2 [E, C]
Bilety razem: 31,60
Oplata rezerwacyjna: 0,00
Do zaplaty: 31,60
Wygenerowano: <czas>
`)
      .expect('ZASTANE: 10 biletów bez rabatu grupowego (reguła mówi 10+)', TEN_TICKETS, `POTWIERDZENIE REZERWACJI
Klient: JAN@KINO.PL
Film: Amator, 2D, 18:30
Bilety: 10 [N, N, N, N, N, N, N, N, N, N]
Bilety razem: 250,00
Oplata rezerwacyjna: 20,00
Do zaplaty: 270,00
Wygenerowano: <czas>
`)
      .expect('11 biletów - rabat grupowy 10%', ELEVEN_TICKETS, `POTWIERDZENIE REZERWACJI
Klient: JAN@KINO.PL
Film: Amator, 2D, 18:30
Bilety: 11 [N, N, N, N, N, N, N, N, N, N, N]
Bilety razem: 247,50
Oplata rezerwacyjna: 0,00
Do zaplaty: 247,50
Wygenerowano: <czas>
`)
      .tests();
  });

  it('startPrintsCurrentTimeSoTheTestMustScrubIt', () => {
    const document = inLocale(SERVER_LOCALE, () => new start.BookingConfirmation().confirm(IMAX_ONLINE));
    expect(document).toMatch(/\nWygenerowano: \d{4}-\d{2}-\d{2}T\d{2}:\d{2}(:\d{2})?\n$/);
  });

  it('fromStep1TheWholeDocumentIsDeterministic', () => {
    const variants: Array<(booking: Booking) => string> = [
      (b) => new step1.BookingConfirmation(FIXED).confirm(b),
      (b) => new step2.BookingConfirmation(FIXED).confirm(b),
    ];
    for (const variant of variants) {
      const document = inLocale(SERVER_LOCALE, () => variant(IMAX_ONLINE));
      expect(document.endsWith('Do zaplaty: 74,00\nWygenerowano: 2026-09-23T08:15:30\n'), document).toBe(true);
    }
  });

  it('foundDuringCharacterizationAmountsDependOnServerLocale', () => {
    const document = inLocale('en-US', () => new step2.BookingConfirmation(FIXED).confirm(IMAX_ONLINE));
    expect(document).toContain('Do zaplaty: 74.00\n');
  });
});
