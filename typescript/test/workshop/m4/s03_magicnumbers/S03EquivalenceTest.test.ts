import { describe, expect, it } from 'vitest';

import { Order } from '../../../../src/workshop/m4/s03_magicnumbers/Order.js';
import * as start from '../../../../src/workshop/m4/s03_magicnumbers/start/OrderPricer.js';
import * as step1 from '../../../../src/workshop/m4/s03_magicnumbers/step1/OrderPricer.js';
import * as step2 from '../../../../src/workshop/m4/s03_magicnumbers/step2/OrderPricer.js';
import * as step3 from '../../../../src/workshop/m4/s03_magicnumbers/step3/OrderPricer.js';
import { Ticket } from '../../../../src/workshop/m4/s03_magicnumbers/Ticket.js';
import { LocalTime } from '../../../../src/workshop/shared/time.js';
import { Scene } from '../../support/scene.js';

/** Pułapka "stałej" kolekcji: const chroni referencję, nie zawartość. */
const discountedTypes: string[] = ['S', 'E', 'C'];
const discountedTypesImmutable: readonly string[] = Object.freeze(['S', 'E', 'C']);

/** Test równoważności: nazwanie liczb nie zmienia ani grosza w podsumowaniu. */
describe('S03EquivalenceTest', () => {
  describe('everyStepSummarizesOrdersTheSameWay', () => {
    Scene.variants<Order, string>()
      .variant('start', (o) => new start.OrderPricer().summary(o))
      .variant('step1', (o) => new step1.OrderPricer().summary(o))
      .variant('step2', (o) => new step2.OrderPricer().summary(o))
      .variant('step3', (o) => new step3.OrderPricer().summary(o))
      .expect('2D wieczorem online: normalny + student na VIP',
        new Order(1, LocalTime.of(18, 0), true,
          [new Ticket('N', 5), new Ticket('S', 10)]),
        'Bilety: 53.75, oplata: 4.00, razem: 57.75, punkty: 5')
      .expect('3D rano w kasie: senior + dziecko na VIP',
        new Order(2, LocalTime.of(10, 30), false,
          [new Ticket('E', 3), new Ticket('C', 11)]),
        'Bilety: 41.60, oplata: 0.00, razem: 41.60, punkty: 4')
      .expect('IMAX online, grupa 10 biletów',
        new Order(3, LocalTime.of(20, 0), true, Array<Ticket>(10).fill(new Ticket('N', 1))),
        'Bilety: 360.00, oplata: 20.00, razem: 380.00, punkty: 36')
      .expect('2D 12:00 w kasie, 9 biletów VIP - jeszcze nie grupa',
        new Order(1, LocalTime.of(12, 0), false, Array<Ticket>(9).fill(new Ticket('N', 10))),
        'Bilety: 315.00, oplata: 0.00, razem: 315.00, punkty: 31')
      .tests();
  });

  it('finalDoesNotMakeACollectionConstant', () => {
    discountedTypes.push('N');
    // Ktoś właśnie dał zniżkę biletom normalnym.
    expect(discountedTypes).toEqual(['S', 'E', 'C', 'N']);
    expect(() => (discountedTypesImmutable as string[]).push('N')).toThrow(TypeError);
  });
});
