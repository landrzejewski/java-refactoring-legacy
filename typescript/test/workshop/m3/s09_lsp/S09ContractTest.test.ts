import { describe, expect, it } from 'vitest';

import { IllegalStateError, UnsupportedOperationError } from '../../../../src/shared/errors.js';
import * as start from '../../../../src/workshop/m3/s09_lsp/start/Hall.js';
import * as step1 from '../../../../src/workshop/m3/s09_lsp/step1/Hall.js';
import * as step1ReadOnly from '../../../../src/workshop/m3/s09_lsp/step1/ReadOnlyHall.js';
import * as step2 from '../../../../src/workshop/m3/s09_lsp/step2/Hall.js';
import * as step2ReadOnly from '../../../../src/workshop/m3/s09_lsp/step2/ReadOnlyHall.js';

/**
 * Testy kontraktowe: ten sam zestaw sprawdzeń dla KAŻDEJ implementacji danego typu.
 * Dają dowody zgodności (LSP) dla sprawdzonych stanów - nie formalny dowód.
 */

/**
 * Widok testu na salę z dowolnego wariantu. Typy w TypeScript są strukturalne,
 * więc sala z każdego kroku pasuje do tego widoku bez adaptera.
 */
interface HallUnderTest {
  reserve(seat: number): void;

  isFree(seat: number): boolean;

  freeSeats(): number;

  capacity(): number;
}

/** Kontrakt Hall.reserve: wolne miejsce zostaje zajęte, licznik maleje, druga rezerwacja - błąd. */
function obeysReserveContract(hall: HallUnderTest): void {
  const before = hall.freeSeats();
  expect(hall.isFree(3)).toBe(true);
  hall.reserve(3);
  expect(hall.isFree(3)).toBe(false);
  expect(hall.freeSeats()).toBe(before - 1);
  expect(() => hall.reserve(3)).toThrow(IllegalStateError);
}

/** Kontrakt odczytu (SeatMap): freeSeats zgodne z isFree dla wszystkich miejsc. */
function obeysReadContract(seats: HallUnderTest): void {
  let free = 0;
  for (let seat = 1; seat <= seats.capacity(); seat++) {
    if (seats.isFree(seat)) {
      free++;
    }
  }
  expect(seats.freeSeats()).toBe(free);
}

/** Sala archiwalna z kroku 2 ma tylko rolę odczytu - reserve w widoku testu jest niedostępne. */
function readOnly(seats: step2ReadOnly.ReadOnlyHall): HallUnderTest {
  return {
    reserve: () => {
      throw new Error('SeatMap nie ma reserve');
    },
    isFree: (seat) => seats.isFree(seat),
    freeSeats: () => seats.freeSeats(),
    capacity: () => seats.capacity(),
  };
}

describe('S09ContractTest', () => {
  describe('everyHallObeysTheReserveContract', () => {
    const halls: Array<[string, () => HallUnderTest]> = [
      ['start: Hall', () => new start.Hall(5)],
      ['step1: Hall', () => new step1.Hall(5)],
      ['step2: Hall', () => new step2.Hall(5)],
      // step2.ReadOnlyHall nie ma reserve - kompilator nie pozwala dodać go do tej listy.
    ];
    for (const [name, hall] of halls) {
      it(name, () => obeysReserveContract(hall()));
    }
  });

  describe('everySeatMapObeysTheReadContract', () => {
    const maps: Array<[string, () => HallUnderTest]> = [
      ['step1: ReadOnlyHall', () => new step1ReadOnly.ReadOnlyHall(5, new Set([1, 4]))],
      ['step2: ReadOnlyHall', () => readOnly(new step2ReadOnly.ReadOnlyHall(5, new Set([1, 4])))],
      ['step2: Hall', () => new step2.Hall(5)],
    ];
    for (const [name, seats] of maps) {
      it(name, () => obeysReadContract(seats()));
    }
  });

  it('readOnlyHallAsSubclassBreaksTheReserveContract', () => {
    // krok 1 = stan z start dla ReadOnlyHall (start jest edytowany na żywo, więc sprawdzamy kopię)
    const archived = new step1ReadOnly.ReadOnlyHall(5, new Set());
    expect(() => obeysReserveContract(archived)).toThrow(UnsupportedOperationError);
  });
});
