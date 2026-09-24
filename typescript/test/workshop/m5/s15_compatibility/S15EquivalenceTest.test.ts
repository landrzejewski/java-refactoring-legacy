import { describe } from 'vitest';

import { Money } from '../../../../src/workshop/shared/Money.js';
import * as startApi from '../../../../src/workshop/m5/s15_compatibility/start/BoxOfficeApi.js';
import * as startExporter from '../../../../src/workshop/m5/s15_compatibility/start/TicketExporter.js';
import * as startStandard from '../../../../src/workshop/m5/s15_compatibility/start/StandardTicket.js';
import * as startStudent from '../../../../src/workshop/m5/s15_compatibility/start/StudentTicket.js';
import * as step1Api from '../../../../src/workshop/m5/s15_compatibility/step1/BoxOfficeApi.js';
import * as step1Exporter from '../../../../src/workshop/m5/s15_compatibility/step1/TicketExporter.js';
import * as step1Standard from '../../../../src/workshop/m5/s15_compatibility/step1/StandardTicket.js';
import * as step1Student from '../../../../src/workshop/m5/s15_compatibility/step1/StudentTicket.js';
import * as step2Api from '../../../../src/workshop/m5/s15_compatibility/step2/BoxOfficeApi.js';
import * as step2Exporter from '../../../../src/workshop/m5/s15_compatibility/step2/TicketExporter.js';
import * as step2Standard from '../../../../src/workshop/m5/s15_compatibility/step2/StandardTicket.js';
import * as step2Student from '../../../../src/workshop/m5/s15_compatibility/step2/StudentTicket.js';
import * as step3Api from '../../../../src/workshop/m5/s15_compatibility/step3/BoxOfficeApi.js';
import * as step3Exporter from '../../../../src/workshop/m5/s15_compatibility/step3/TicketExporter.js';
import * as step3Standard from '../../../../src/workshop/m5/s15_compatibility/step3/StandardTicket.js';
import * as step3Student from '../../../../src/workshop/m5/s15_compatibility/step3/StudentTicket.js';
import * as step4Api from '../../../../src/workshop/m5/s15_compatibility/step4/BoxOfficeApi.js';
import * as step4Exporter from '../../../../src/workshop/m5/s15_compatibility/step4/TicketExporter.js';
import * as step4Standard from '../../../../src/workshop/m5/s15_compatibility/step4/StandardTicket.js';
import * as step4Student from '../../../../src/workshop/m5/s15_compatibility/step4/StudentTicket.js';
import { Scene } from '../../support/scene.js';

/**
 * Test równoważności na poziomie ŹRÓDŁA: ten sam kod klienta, sprawdzony typami z każdym wariantem,
 * daje ten sam eksport i tę samą wycenę. Zgodność zbudowanych wtyczek sprawdza S15SolutionTest.
 */
describe('S15EquivalenceTest', () => {
  describe('everyStepExportsTheSameRow', () => {
    Scene.variants<string, string>()
      .variant('start', (kind) => new startExporter.TicketExporter().export(kind === 'STUDENT'
        ? new startStudent.StudentTicket('Amator', Money.of('25.00'))
        : new startStandard.StandardTicket('Diuna', Money.of('40.00'))))
      .variant('step1', (kind) => new step1Exporter.TicketExporter().export(kind === 'STUDENT'
        ? new step1Student.StudentTicket('Amator', Money.of('25.00'))
        : new step1Standard.StandardTicket('Diuna', Money.of('40.00'))))
      .variant('step2', (kind) => new step2Exporter.TicketExporter().export(kind === 'STUDENT'
        ? new step2Student.StudentTicket('Amator', Money.of('25.00'))
        : new step2Standard.StandardTicket('Diuna', Money.of('40.00'))))
      .variant('step3', (kind) => new step3Exporter.TicketExporter().export(kind === 'STUDENT'
        ? new step3Student.StudentTicket('Amator', Money.of('25.00'))
        : new step3Standard.StandardTicket('Diuna', Money.of('40.00'))))
      .variant('step4', (kind) => new step4Exporter.TicketExporter().export(kind === 'STUDENT'
        ? new step4Student.StudentTicket('Amator', Money.of('25.00'))
        : new step4Standard.StandardTicket('Diuna', Money.of('40.00'))))
      .expect('studencki', 'STUDENT', 'Amator;cena=18.75')
      .expect('normalny', 'NORMAL', 'Diuna;cena=40.00')
      .tests();
  });

  /**
   * Start woła metodę po nazwie: po `jump 3` w start jest już tylko `quote` (TS nie ma przeciążeń),
   * a test ma się nadal typować.
   */
  function quoteStudentOf(api: object, ticket: unknown): string {
    const methods = api as Record<string, ((ticket: unknown) => unknown) | undefined>;
    const quote = methods['quoteStudent'] ?? methods['quote'];
    return String(quote!.call(api, ticket));
  }

  // Start..step2 i step4 (przestarzała metoda) wołają quoteStudent, step3 ma już tylko quote.
  describe('everyStepQuotesStudentTicketTheSameWay', () => {
    Scene.variants<string, string>()
      .variant('start', (p) => quoteStudentOf(new startApi.BoxOfficeApi(),
        new startStudent.StudentTicket('Amator', Money.of(p))))
      .variant('step1', (p) => new step1Api.BoxOfficeApi()
        .quoteStudent(new step1Student.StudentTicket('Amator', Money.of(p))).toString())
      .variant('step2', (p) => new step2Api.BoxOfficeApi()
        .quoteStudent(new step2Student.StudentTicket('Amator', Money.of(p))).toString())
      .variant('step3', (p) => new step3Api.BoxOfficeApi()
        .quote(new step3Student.StudentTicket('Amator', Money.of(p))).toString())
      .variant('step4', (p) => new step4Api.BoxOfficeApi()
        .quoteStudent(new step4Student.StudentTicket('Amator', Money.of(p))).toString())
      .expect('studencki 2D', '25.00', '18.75')
      .tests();
  });
});
