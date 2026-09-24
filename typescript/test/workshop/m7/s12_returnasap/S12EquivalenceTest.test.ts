import { describe } from 'vitest';

import { Seat } from '../../../../src/workshop/m7/s12_returnasap/Seat.js';
import * as start from '../../../../src/workshop/m7/s12_returnasap/start/SeatFinder.js';
import * as step1 from '../../../../src/workshop/m7/s12_returnasap/step1/SeatFinder.js';
import * as step2 from '../../../../src/workshop/m7/s12_returnasap/step2/SeatFinder.js';
import { Scene } from '../../support/scene.js';

/**
 * Test równoważności: wynik firstFree, licznik inspected (efekt uboczny) oraz klasy miejsc.
 * Gdyby return trafił przed inspectedCount++, licznik różniłby się o jeden - test to wykryje.
 */
const HALL: readonly Seat[] = [
  new Seat('A1', 1, false),
  new Seat('A10', 10, true),
  new Seat('B10', 10, false),
  new Seat('C11', 11, false),
];

class Query {
  constructor(readonly seats: readonly Seat[] | null, readonly minRow: number) {}
}

interface Finder {
  seatClass(seat: Seat | null, vipFromRow: number): string;
  firstFree(seats: readonly Seat[] | null, minRow: number): string | undefined;
  inspected(): number;
}

// Wynik opisany jak Optional.toString() w Javie, żeby oczekiwania były te same.
function optional(value: string | undefined): string {
  return value === undefined ? 'Optional.empty' : `Optional[${value}]`;
}

function classes(seatClass: (seat: Seat | null) => string): string {
  const probes: (Seat | null)[] = [...HALL, null];
  return `[${probes.map(seatClass).join(', ')}]`;
}

function observe(finders: () => Finder): (query: Query) => string {
  return (q) => {
    const finder = finders();
    return `${optional(finder.firstFree(q.seats, q.minRow))} sprawdzono=${finder.inspected()}`
      + ` klasy=${classes((seat) => finder.seatClass(seat, 10))}`;
  };
}

describe('S12EquivalenceTest', () => {
  describe('everyStepFindsAndCountsTheSame', () => {
    Scene.variants<Query, string>()
      .variant('start', observe(() => new start.SeatFinder()))
      .variant('step1', observe(() => new step1.SeatFinder()))
      .variant('step2', observe(() => new step2.SeatFinder()))
      .expect('pierwsze wolne VIP po zajetym', new Query(HALL, 10),
        'Optional[B10] sprawdzono=3 klasy=[STANDARD, ZAJETE, VIP, VIP, BRAK]')
      .expect('pierwsze miejsce od razu', new Query(HALL, 1),
        'Optional[A1] sprawdzono=1 klasy=[STANDARD, ZAJETE, VIP, VIP, BRAK]')
      .expect('brak pasujacego - przejrzane wszystkie', new Query(HALL, 12),
        'Optional.empty sprawdzono=4 klasy=[STANDARD, ZAJETE, VIP, VIP, BRAK]')
      .expect('lista null', new Query(null, 1),
        'Optional.empty sprawdzono=0 klasy=[STANDARD, ZAJETE, VIP, VIP, BRAK]')
      .tests();
  });
});
