import { IllegalArgumentError } from '../../../../src/shared/errors.js';
import * as start from '../../../../src/workshop/m7/s06_parameterobject/start/ScreeningPlanner.js';
import * as step1 from '../../../../src/workshop/m7/s06_parameterobject/step1/ScreeningPlanner.js';
import * as step1Slot from '../../../../src/workshop/m7/s06_parameterobject/step1/ScreeningSlot.js';
import * as step2 from '../../../../src/workshop/m7/s06_parameterobject/step2/ScreeningPlanner.js';
import * as step2Slot from '../../../../src/workshop/m7/s06_parameterobject/step2/ScreeningSlot.js';
import * as step3 from '../../../../src/workshop/m7/s06_parameterobject/step3/ScreeningPlanner.js';
import * as step3Slot from '../../../../src/workshop/m7/s06_parameterobject/step3/ScreeningSlot.js';
import { LocalDate } from '../../../../src/workshop/shared/time.js';

// Obserwacje wspólne dla S06EquivalenceTest i S06ValidationMomentTest
// (w Javie statyczne metody pakietowe klasy S06EquivalenceTest).

/** Surowe dane wejściowe - te same dla wszystkich wariantów (Parameter Object powstaje dopiero w krokach). */
export class Clump {
  constructor(
    readonly screeningId: string,
    readonly date: LocalDate,
    readonly hall: number,
    readonly format: string,
  ) {}
}

export const DAY = LocalDate.of(2026, 3, 10);

/**
 * Wynik albo komunikat wyjątku - wyjątek też jest częścią obserwowalnego zachowania.
 * Kwoty są zamieniane na tekst przez toFixed(2) (jak BigDecimal.toString() w Javie).
 */
export function attempt<T>(call: (input: T) => string, input: T): string {
  try {
    return call(input);
  } catch (error) {
    if (error instanceof IllegalArgumentError) {
      return `EXC ${error.message}`;
    }
    throw error;
  }
}

export function runStart(c: Clump): string {
  const planner = new start.ScreeningPlanner();
  return `${attempt(() => planner.describe(c.screeningId, c.date, c.hall, c.format), c)}`
    + ` | ${attempt(() => planner.ticketPrice(c.screeningId, c.date, c.hall, c.format).toFixed(2), c)}`;
}

export function runStep1(c: Clump): string {
  const planner = new step1.ScreeningPlanner();
  const slot = new step1Slot.ScreeningSlot(c.screeningId, c.date, c.hall, c.format);
  return `${attempt((s) => planner.describe(s), slot)} | ${attempt((s) => planner.ticketPrice(s).toFixed(2), slot)}`;
}

export function runStep2(c: Clump): string {
  const planner = new step2.ScreeningPlanner();
  const slot = new step2Slot.ScreeningSlot(c.screeningId, c.date, c.hall, c.format);
  return `${attempt((s) => planner.describe(s), slot)} | ${attempt((s) => planner.ticketPrice(s).toFixed(2), slot)}`;
}

export function runStep3(c: Clump): string {
  const planner = new step3.ScreeningPlanner();
  let slot: step3Slot.ScreeningSlot;
  try {
    slot = new step3Slot.ScreeningSlot(c.screeningId, c.date, c.hall, c.format);
  } catch (error) {
    if (error instanceof IllegalArgumentError) {
      return `new ScreeningSlot -> EXC ${error.message}`;
    }
    throw error;
  }
  return `${attempt((s) => planner.describe(s), slot)} | ${attempt((s) => planner.ticketPrice(s).toFixed(2), slot)}`;
}
