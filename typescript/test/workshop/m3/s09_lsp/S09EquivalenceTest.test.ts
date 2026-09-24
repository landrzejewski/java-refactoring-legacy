import { describe } from 'vitest';

import * as startBox from '../../../../src/workshop/m3/s09_lsp/start/BoxOffice.js';
import * as start from '../../../../src/workshop/m3/s09_lsp/start/Hall.js';
import * as startReport from '../../../../src/workshop/m3/s09_lsp/start/OccupancyReport.js';
import * as startReadOnly from '../../../../src/workshop/m3/s09_lsp/start/ReadOnlyHall.js';
import * as step1Box from '../../../../src/workshop/m3/s09_lsp/step1/BoxOffice.js';
import * as step1 from '../../../../src/workshop/m3/s09_lsp/step1/Hall.js';
import * as step1Report from '../../../../src/workshop/m3/s09_lsp/step1/OccupancyReport.js';
import * as step1ReadOnly from '../../../../src/workshop/m3/s09_lsp/step1/ReadOnlyHall.js';
import * as step2Box from '../../../../src/workshop/m3/s09_lsp/step2/BoxOffice.js';
import * as step2 from '../../../../src/workshop/m3/s09_lsp/step2/Hall.js';
import * as step2Report from '../../../../src/workshop/m3/s09_lsp/step2/OccupancyReport.js';
import * as step2ReadOnly from '../../../../src/workshop/m3/s09_lsp/step2/ReadOnlyHall.js';
import { Scene } from '../../support/scene.js';

/** Kasa (na zwykłej sali) i raport (na sali archiwalnej) działają tak samo we wszystkich wariantach. */
describe('S09EquivalenceTest', () => {
  describe('sellingAndReportingBehaveTheSame', () => {
    Scene.variants<number, string>()
      .variant('start', (seat) => {
        const hall = new start.Hall(10);
        hall.reserve(1);
        return `${new startBox.BoxOffice().sell(hall, seat)} | ${new startReport.OccupancyReport().describe(
          new startReadOnly.ReadOnlyHall(10, new Set([1, 2, seat])))}`;
      })
      .variant('step1', (seat) => {
        const hall = new step1.Hall(10);
        hall.reserve(1);
        return `${new step1Box.BoxOffice().sell(hall, seat)} | ${new step1Report.OccupancyReport().describe(
          new step1ReadOnly.ReadOnlyHall(10, new Set([1, 2, seat])))}`;
      })
      .variant('step2', (seat) => {
        const hall = new step2.Hall(10);
        hall.reserve(1);
        return `${new step2Box.BoxOffice().sell(hall, seat)} | ${new step2Report.OccupancyReport().describe(
          new step2ReadOnly.ReadOnlyHall(10, new Set([1, 2, seat])))}`;
      })
      .expect('miejsce 5', 5, 'sprzedano miejsce 5, wolnych: 8 | zajete 3 z 10')
      .expect('ostatnie miejsce', 10, 'sprzedano miejsce 10, wolnych: 8 | zajete 3 z 10')
      .tests();
  });
});
