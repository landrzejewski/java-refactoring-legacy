import { describe } from 'vitest';

import { SampleProject } from '../../../../src/workshop/m8/s09_codemod/SampleProject.js';
import * as start from '../../../../src/workshop/m8/s09_codemod/start/BookCallCodemod.js';
import * as step1 from '../../../../src/workshop/m8/s09_codemod/step1/BookCallCodemod.js';
import * as step2 from '../../../../src/workshop/m8/s09_codemod/step2/BookCallCodemod.js';
import * as step3 from '../../../../src/workshop/m8/s09_codemod/step3/BookCallCodemod.js';
import { Scene } from '../../support/scene.js';

const SIMPLE = `// desk/Kiosk.ts

import { BookingService } from '../cinema/BookingService.js';

export class Kiosk {
  sell(bookings: BookingService, seats: string[], types: string[]): string {
    return bookings.book('S3', 'anna@kino.pl', seats, types, true, false);
  }
}
`;

interface Codemod {
  findLines(source: string): number[];
  rewrite(source: string): string;
}

function describeRun(codemod: Codemod, source: string): string {
  return 'linie: [' + codemod.findLines(source).join(', ') + ']\n' + codemod.rewrite(source);
}

/**
 * Na prostym przypadku (jedno wywołanie w jednej linii) wszystkie wersje codemodu się zgadzają.
 * Różnice wychodzą dopiero na trudnych przypadkach - patrz S09SolutionTest.
 */
describe('S09EquivalenceTest', () => {
  describe('everyVersionHandlesTheSimpleCase', () => {
    Scene.variants<string, string>()
      .variant('start', (s) => describeRun(new start.BookCallCodemod(SampleProject.API), s))
      .variant('step1', (s) => describeRun(new step1.BookCallCodemod(SampleProject.API), s))
      .variant('step2', (s) => describeRun(new step2.BookCallCodemod(SampleProject.API), s))
      .variant('step3', (s) => describeRun(new step3.BookCallCodemod(SampleProject.API), s))
      .expect('jedno wywołanie z literałami', SIMPLE, `linie: [7]
// desk/Kiosk.ts

import { BookingService } from '../cinema/BookingService.js';
import { Channel } from '../cinema/Channel.js';
import { Glasses } from '../cinema/Glasses.js';

export class Kiosk {
  sell(bookings: BookingService, seats: string[], types: string[]): string {
    return bookings.book('S3', 'anna@kino.pl', seats, types, Channel.WEB, Glasses.RENTED);
  }
}
`)
      .tests();
  });
}, 60_000);
