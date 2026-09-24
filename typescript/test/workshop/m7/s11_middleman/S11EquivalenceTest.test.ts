import { describe, expect, it } from 'vitest';

import { Screening } from '../../../../src/workshop/m7/s11_middleman/Screening.js';
import { NoSuchElementError, ScreeningCatalog } from '../../../../src/workshop/m7/s11_middleman/ScreeningCatalog.js';
import * as startFacade from '../../../../src/workshop/m7/s11_middleman/start/CinemaFacade.js';
import * as startBoard from '../../../../src/workshop/m7/s11_middleman/start/DailyBoard.js';
import * as startBadge from '../../../../src/workshop/m7/s11_middleman/start/SeatBadge.js';
import * as step1Facade from '../../../../src/workshop/m7/s11_middleman/step1/CinemaFacade.js';
import * as step1Board from '../../../../src/workshop/m7/s11_middleman/step1/DailyBoard.js';
import * as step1Badge from '../../../../src/workshop/m7/s11_middleman/step1/SeatBadge.js';
import * as step2Facade from '../../../../src/workshop/m7/s11_middleman/step2/CinemaFacade.js';
import * as step2Board from '../../../../src/workshop/m7/s11_middleman/step2/DailyBoard.js';
import * as step2Badge from '../../../../src/workshop/m7/s11_middleman/step2/SeatBadge.js';
import * as step3Board from '../../../../src/workshop/m7/s11_middleman/step3/DailyBoard.js';
import * as step3Badge from '../../../../src/workshop/m7/s11_middleman/step3/SeatBadge.js';
import { Scene } from '../../support/scene.js';

/** Test równoważności obu klientów pośrednika - także dla nieznanego seansu. */
const CATALOG = new ScreeningCatalog([
  new Screening('S1', 'Diuna', 'IMAX', 120),
  new Screening('S2', 'Kraina Lodu', '3D', 0),
  new Screening('S3', 'Amator', '2D', 7),
]);

const BOARD = 'S1 Diuna IMAX\nS2 Kraina Lodu 3D\nS3 Amator 2D';

describe('S11EquivalenceTest', () => {
  describe('clientsSeeTheSameThing', () => {
    const startCinema = new startFacade.CinemaFacade(CATALOG);
    const step1Cinema = new step1Facade.CinemaFacade(CATALOG);
    const step2Cinema = new step2Facade.CinemaFacade(CATALOG);
    Scene.variants<string, string>()
      .variant('start', (id) => `${new startBadge.SeatBadge(startCinema).badge(id)}`
        + ` | ${new startBoard.DailyBoard(startCinema).render()}`)
      .variant('step1', (id) => `${new step1Badge.SeatBadge(step1Cinema).badge(id)}`
        + ` | ${new step1Board.DailyBoard(step1Cinema).render()}`)
      .variant('step2', (id) => `${new step2Badge.SeatBadge(CATALOG).badge(id)}`
        + ` | ${new step2Board.DailyBoard(step2Cinema).render()}`)
      .variant('step3', (id) => `${new step3Badge.SeatBadge(CATALOG).badge(id)}`
        + ` | ${new step3Board.DailyBoard(CATALOG).render()}`)
      .expect('wolne miejsca', 'S1', `Diuna (IMAX): 120 wolnych | ${BOARD}`)
      .expect('wyprzedane', 'S2', `S2: WYPRZEDANE | ${BOARD}`)
      .expect('nieznany seans wyglada jak wyprzedany (historyczne zachowanie)', 'S9',
        `S9: WYPRZEDANE | ${BOARD}`)
      .tests();
  });

  it('catalogItselfRejectsUnknownScreening', () => {
    expect(() => CATALOG.freeSeats('S9')).toThrow(NoSuchElementError);
    expect(() => CATALOG.freeSeats('S9')).toThrow('brak seansu S9');
  });
});
