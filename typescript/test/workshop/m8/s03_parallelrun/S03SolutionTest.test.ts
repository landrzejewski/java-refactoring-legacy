import { describe, expect, it } from 'vitest';

import { IllegalArgumentError } from '../../../../src/shared/errors.js';
import type { TicketQuery } from '../../../../src/workshop/m8/s03_parallelrun/TicketQuery.js';
import * as step1 from '../../../../src/workshop/m8/s03_parallelrun/step1/PriceService.js';
import * as step2 from '../../../../src/workshop/m8/s03_parallelrun/step2/PriceService.js';
import * as step2report from '../../../../src/workshop/m8/s03_parallelrun/step2/VerificationReport.js';
import * as step3 from '../../../../src/workshop/m8/s03_parallelrun/step3/PriceService.js';
import * as step3report from '../../../../src/workshop/m8/s03_parallelrun/step3/VerificationReport.js';
import { MigrationMode } from '../../../../src/workshop/m8/s03_parallelrun/step4/MigrationMode.js';
import * as step4 from '../../../../src/workshop/m8/s03_parallelrun/step4/PriceService.js';
import * as step4report from '../../../../src/workshop/m8/s03_parallelrun/step4/VerificationReport.js';
import { Money } from '../../../../src/workshop/shared/Money.js';
import {
  EVENING_2D_SENIOR, IMAX_NORMAL, IMAX_STUDENT_VIP, MORNING_2D_STUDENT, MORNING_3D_CHILD, MORNING_3D_NORMAL_VIP,
  UNKNOWN_4DX,
} from './S03Fixtures.js';

const TRAFFIC: readonly TicketQuery[] = [IMAX_NORMAL, IMAX_STUDENT_VIP, MORNING_3D_CHILD,
  MORNING_3D_NORMAL_VIP, EVENING_2D_SENIOR, MORNING_2D_STUDENT, UNKNOWN_4DX];

function replay(service: (query: TicketQuery) => unknown): void {
  TRAFFIC.forEach((query) => service(query));
}

/** Co wiemy po każdym kroku trybu shadow i co zmienia przełączenie. */
describe('S03SolutionTest', () => {
  it('step1ShadowSurvivesCandidateFailureButOnlyCountsMismatches', () => {
    const service = new step1.PriceService();
    replay((query) => service.price(query));
    expect(service.price(UNKNOWN_4DX), 'klient dostaje wynik legacy').toEqual(Money.of('0.00'));
    expect(service.mismatches(), '3 różnice w ruchu + powtórzony 4DX, ale nie wiemy które').toBe(4);
  });

  it('step2ReportShowsWhatDivergedAndWhy', () => {
    const report = new step2report.VerificationReport();
    const service = new step2.PriceService(report);
    replay((query) => service.price(query));
    expect(report.problems()).toEqual([
      'ROZBIEZNOSC 3D CHILD 10:30 rzad 3: legacy 17.20, kandydat 19.20',
      'ROZBIEZNOSC 2D STUDENT 09:00 rzad 2: legacy 13.75, kandydat 15.00',
      'BLAD KANDYDATA 4DX NORMAL 20:00 rzad 1: IllegalArgumentError: Nieznany format: 4DX']);
    expect(report.entries()).toHaveLength(TRAFFIC.length);
  });

  it('step3FixedCandidateLeavesOnlyTheAcceptedDifference', () => {
    const report = new step3report.VerificationReport();
    const service = new step3.PriceService(report);
    replay((query) => service.price(query));
    expect(report.problems()).toEqual([
      'BLAD KANDYDATA 4DX NORMAL 20:00 rzad 1: IllegalArgumentError: Nieznany format: 4DX']);
  });

  it('step4CandidateModeIsAuthoritativeAndRejectsUnknownFormat', () => {
    const report = new step4report.VerificationReport();
    const service = new step4.PriceService(MigrationMode.CANDIDATE, report);
    expect(service.price(MORNING_3D_CHILD)).toEqual(Money.of('17.20'));
    expect(() => service.price(UNKNOWN_4DX)).toThrow(IllegalArgumentError);
    expect(report.entries(), 'po przełączeniu cień już nie działa').toHaveLength(0);
  });
});
