import { describe } from 'vitest';

import { LocalTime } from '../../../../src/workshop/shared/time.js';
import * as startDesk from '../../../../src/workshop/m3/s10_isp/start/CashDesk.js';
import * as startOffice from '../../../../src/workshop/m3/s10_isp/start/InMemoryBackOffice.js';
import * as startReport from '../../../../src/workshop/m3/s10_isp/start/RevenueReport.js';
import * as startBoard from '../../../../src/workshop/m3/s10_isp/start/ScheduleBoard.js';
import * as step1Desk from '../../../../src/workshop/m3/s10_isp/step1/CashDesk.js';
import * as step1Office from '../../../../src/workshop/m3/s10_isp/step1/InMemoryBackOffice.js';
import * as step1Report from '../../../../src/workshop/m3/s10_isp/step1/RevenueReport.js';
import * as step1Board from '../../../../src/workshop/m3/s10_isp/step1/ScheduleBoard.js';
import * as step2Desk from '../../../../src/workshop/m3/s10_isp/step2/CashDesk.js';
import * as step2Office from '../../../../src/workshop/m3/s10_isp/step2/InMemoryBackOffice.js';
import * as step2Report from '../../../../src/workshop/m3/s10_isp/step2/RevenueReport.js';
import * as step2Board from '../../../../src/workshop/m3/s10_isp/step2/ScheduleBoard.js';
import { Scene } from '../../support/scene.js';

interface Clients {
  readonly board: { plan(title: string, start: LocalTime): void; cancel(title: string): void; board(): string };
  readonly desk: { sell(title: string, seat: number): string; refund(ticketId: string): string };
  readonly report: { summary(title: string): string };
}

function day(clients: Clients, dune: number): string {
  const { board, desk, report } = clients;
  board.plan('Diuna', LocalTime.of(20, 0));
  board.plan('Amator', LocalTime.of(18, 0));
  board.plan('Kraina Lodu', LocalTime.of(10, 0));
  board.cancel('Kraina Lodu');
  let log = '';
  for (let seat = 1; seat <= dune; seat++) {
    log += `${desk.sell('Diuna', seat)}; `;
  }
  log += `${desk.sell('Amator', 7)}; `;
  if (dune > 0) {
    log += `${desk.refund('T-1')}; `;
  }
  return `${log}${board.board()} | ${report.summary('Diuna')}`;
}

/** Ten sam dzień pracy kasy, raportu i tablicy seansów w każdym wariancie. */
describe('S10EquivalenceTest', () => {
  describe('clientsBehaveTheSame', () => {
    Scene.variants<number, string>()
      .variant('start', (dune) => {
        const office = new startOffice.InMemoryBackOffice();
        return day({
          board: new startBoard.ScheduleBoard(office),
          desk: new startDesk.CashDesk(office),
          report: new startReport.RevenueReport(office),
        }, dune);
      })
      .variant('step1', (dune) => {
        const office = new step1Office.InMemoryBackOffice();
        return day({
          board: new step1Board.ScheduleBoard(office),
          desk: new step1Desk.CashDesk(office),
          report: new step1Report.RevenueReport(office),
        }, dune);
      })
      .variant('step2', (dune) => {
        const office = new step2Office.InMemoryBackOffice();
        return day({
          board: new step2Board.ScheduleBoard(office),
          desk: new step2Desk.CashDesk(office),
          report: new step2Report.RevenueReport(office),
        }, dune);
      })
      .expect('trzy bilety na Diune, jeden zwrocony', 3,
        'bilet T-1: Diuna, miejsce 1; bilet T-2: Diuna, miejsce 2; bilet T-3: Diuna, miejsce 3; '
          + 'bilet T-4: Amator, miejsce 7; zwrot T-1; '
          + '18:00 Amator, 20:00 Diuna | Diuna: 2 biletow, dzien: 75.00')
      .expect('bez biletow na Diune', 0,
        'bilet T-1: Amator, miejsce 7; 18:00 Amator, 20:00 Diuna | Diuna: 0 biletow, dzien: 25.00')
      .tests();
  });
});
