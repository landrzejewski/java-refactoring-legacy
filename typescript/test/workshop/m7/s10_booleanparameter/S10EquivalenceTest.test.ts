import { describe, expect, it } from 'vitest';

import * as startTerminal from '../../../../src/workshop/m7/s10_booleanparameter/start/BoxOfficeTerminal.js';
import * as startApp from '../../../../src/workshop/m7/s10_booleanparameter/start/MobileApp.js';
import * as start from '../../../../src/workshop/m7/s10_booleanparameter/start/TicketService.js';
import * as step1Terminal from '../../../../src/workshop/m7/s10_booleanparameter/step1/BoxOfficeTerminal.js';
import * as step1App from '../../../../src/workshop/m7/s10_booleanparameter/step1/MobileApp.js';
import * as step1 from '../../../../src/workshop/m7/s10_booleanparameter/step1/TicketService.js';
import * as step2Terminal from '../../../../src/workshop/m7/s10_booleanparameter/step2/BoxOfficeTerminal.js';
import * as step2App from '../../../../src/workshop/m7/s10_booleanparameter/step2/MobileApp.js';
import * as step2 from '../../../../src/workshop/m7/s10_booleanparameter/step2/TicketService.js';
import * as step3Terminal from '../../../../src/workshop/m7/s10_booleanparameter/step3/BoxOfficeTerminal.js';
import * as step3App from '../../../../src/workshop/m7/s10_booleanparameter/step3/MobileApp.js';
import * as step3 from '../../../../src/workshop/m7/s10_booleanparameter/step3/TicketService.js';
import * as step4Terminal from '../../../../src/workshop/m7/s10_booleanparameter/step4/BoxOfficeTerminal.js';
import * as step4App from '../../../../src/workshop/m7/s10_booleanparameter/step4/MobileApp.js';
import * as step4 from '../../../../src/workshop/m7/s10_booleanparameter/step4/TicketService.js';
import { Scene } from '../../support/scene.js';

/**
 * Równoważność obserwowana przez klientów (wszystkie warianty) oraz przez stare API
 * (dopóki istnieje, czyli start..step3). Wejście: tytuł, format, liczba miejsc, okulary klienta.
 */
class Sale {
  constructor(
    readonly title: string,
    readonly format: string,
    readonly seats: number,
    readonly flagA: boolean,
    readonly flagB: boolean,
  ) {}
}

/**
 * Klient skompilowany wcześniej do JS (np. inny pakiet npm): nie przechodzi już przez
 * sprawdzanie typów i woła API po nazwie i pozycji argumentów. Odpowiednik starego .class
 * z Javy, który oczekuje konkretnej sygnatury.
 */
function precompiledJsClient(service: object): string {
  const legacy = service as { book(...args: unknown[]): string };
  return legacy.book('Kraina Lodu', '3D', 2, true, false);
}

describe('S10EquivalenceTest', () => {
  describe('clientsSeeTheSamePrices', () => {
    Scene.variants<Sale, string>()
      .variant('start', (s) => {
        const service = new start.TicketService();
        return `${new startApp.MobileApp(service).buy(s.title, s.format, s.seats)}`
          + ` | ${new startTerminal.BoxOfficeTerminal(service).sell(s.title, s.format, s.seats, s.flagA)}`;
      })
      .variant('step1', (s) => {
        const service = new step1.TicketService();
        return `${new step1App.MobileApp(service).buy(s.title, s.format, s.seats)}`
          + ` | ${new step1Terminal.BoxOfficeTerminal(service).sell(s.title, s.format, s.seats, s.flagA)}`;
      })
      .variant('step2', (s) => {
        const service = new step2.TicketService();
        return `${new step2App.MobileApp(service).buy(s.title, s.format, s.seats)}`
          + ` | ${new step2Terminal.BoxOfficeTerminal(service).sell(s.title, s.format, s.seats, s.flagA)}`;
      })
      .variant('step3', (s) => {
        const service = new step3.TicketService();
        return `${new step3App.MobileApp(service).buy(s.title, s.format, s.seats)}`
          + ` | ${new step3Terminal.BoxOfficeTerminal(service).sell(s.title, s.format, s.seats, s.flagA)}`;
      })
      .variant('step4', (s) => {
        const service = new step4.TicketService();
        return `${new step4App.MobileApp(service).buy(s.title, s.format, s.seats)}`
          + ` | ${new step4Terminal.BoxOfficeTerminal(service).sell(s.title, s.format, s.seats, s.flagA)}`;
      })
      .expect('IMAX x2', new Sale('Diuna', 'IMAX', 2, false, false),
        'Diuna IMAX x2 online: 84.00 | Diuna IMAX x2 kasa: 80.00')
      .expect('3D x2, klient w kasie ma okulary', new Sale('Kraina Lodu', '3D', 2, true, false),
        'Kraina Lodu 3D x2 online: 74.00 | Kraina Lodu 3D x2 kasa: 64.00')
      .expect('3D x1, klient w kasie bez okularow', new Sale('Kraina Lodu', '3D', 1, false, false),
        'Kraina Lodu 3D x1 online: 37.00 | Kraina Lodu 3D x1 kasa: 35.00')
      .tests();
  });

  // Wywołania przestarzałego book() (@deprecated) - edytor je przekreśla, ale działają.
  describe('oldFlagApiStillWorksDuringMigration', () => {
    const services = {
      start: new start.TicketService(),
      step1: new step1.TicketService(),
      step2: new step2.TicketService(),
      step3: new step3.TicketService(),
    };
    const scene = Scene.variants<Sale, string>()
      .variant('start', (s) => services.start.book(s.title, s.format, s.seats, s.flagA, s.flagB))
      .variant('step1', (s) => services.step1.book(s.title, s.format, s.seats, s.flagA, s.flagB))
      .variant('step2', (s) => services.step2.book(s.title, s.format, s.seats, s.flagA, s.flagB))
      .variant('step3', (s) => services.step3.book(s.title, s.format, s.seats, s.flagA, s.flagB));
    for (const online of [true, false]) {
      for (const ownGlasses of [true, false]) {
        const total = 64 + (ownGlasses ? 0 : 6) + (online ? 4 : 0);
        scene.expect(`online=${online}, ownGlasses=${ownGlasses}`,
          new Sale('Kraina Lodu', '3D', 2, online, ownGlasses),
          `Kraina Lodu 3D x2 ${online ? 'online' : 'kasa'}: ${total}.00`);
      }
    }
    scene.tests();
  });

  // Tylko w TS: odpowiednik zgodności binarnej. Kompilator TS nie widzi klientów spoza
  // projektu, więc Safe Delete w kroku 4 wychodzi dopiero w czasie działania.
  it('precompiledJsClientBreaksOnlyAfterSafeDelete', () => {
    for (const service of [new start.TicketService(), new step1.TicketService(),
      new step2.TicketService(), new step3.TicketService()]) {
      expect(precompiledJsClient(service)).toBe('Kraina Lodu 3D x2 online: 74.00');
    }
    expect(() => precompiledJsClient(new step4.TicketService())).toThrow(TypeError);
  });
});
