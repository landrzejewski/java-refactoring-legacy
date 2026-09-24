import { describe, expect, it } from 'vitest';

import * as start from '../../../../src/workshop/m8/s10_qualitygate/start/QualityGate.js';
import * as step1 from '../../../../src/workshop/m8/s10_qualitygate/step1/QualityGate.js';
import * as step2 from '../../../../src/workshop/m8/s10_qualitygate/step2/QualityGate.js';
import * as step3 from '../../../../src/workshop/m8/s10_qualitygate/step3/QualityGate.js';
import * as step4 from '../../../../src/workshop/m8/s10_qualitygate/step4/QualityGate.js';
import { CLEAN, DIRTY, FAILING_TESTS } from './S10Fixtures.js';

const SCAN = ['TODO PriceTable.ts:10', 'console PriceTable.ts:20'];
const COMPILER = [
  'kompilator PriceTable.ts:28 TS7006',
  'kompilator PriceTable.ts:28 TS7006'];
const COVERAGE = [
  'pokrycie PriceTable.vipSurcharge bez testu', 'pokrycie PriceTable.lookupCount bez testu'];

/** Bramka budowana krok po kroku na próbkach domeny. */
describe('S10SolutionTest', () => {
  it('startPassesEverythingBecauseItChecksNothing', () => {
    expect(new start.QualityGate().passes(DIRTY)).toBe(true);
  });

  it('step1FindsTodoAndConsoleOutput', () => {
    expect(new step1.QualityGate().evaluate(DIRTY)).toEqual(SCAN);
  });

  it('step2AddsCompilerWarnings', () => {
    expect(new step2.QualityGate().evaluate(DIRTY)).toEqual([...COMPILER, ...SCAN]);
  });

  it('step3AddsCoverageOfTheKeyClass', () => {
    expect(new step3.QualityGate().evaluate(DIRTY)).toEqual([...COMPILER, ...SCAN, ...COVERAGE]);
  });

  it('step4AddsGreenTestsAndCatchesAFailingOne', () => {
    const gate = new step4.QualityGate();
    expect(gate.evaluate(DIRTY), 'testy próbki brudnej są zielone').toEqual([...COMPILER, ...SCAN, ...COVERAGE]);
    expect(gate.evaluate(FAILING_TESTS)).toEqual(['test PriceTableTest.vipSurchargeStartsAtRowNine nie przechodzi: AssertionError']);
    expect(gate.passes(FAILING_TESTS)).toBe(false);
    expect(gate.passes(CLEAN)).toBe(true);
  });

  it('earlierGatesDoNotSeeFailingTests', () => {
    expect(new step3.QualityGate().passes(FAILING_TESTS)).toBe(true);
  });
}, 60_000);
