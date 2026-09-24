import { describe } from 'vitest';

import { Money } from '../../../../src/workshop/shared/Money.js';
import { LocalDateTime, LocalTime } from '../../../../src/workshop/shared/time.js';
import * as start from '../../../../src/workshop/m6/s12_onemany/start/CancellationDesk.js';
import * as step1 from '../../../../src/workshop/m6/s12_onemany/step1/CancellationDesk.js';
import * as step2 from '../../../../src/workshop/m6/s12_onemany/step2/CancellationDesk.js';
import * as step3 from '../../../../src/workshop/m6/s12_onemany/step3/CancellationDesk.js';
import { TicketData } from '../../../../src/workshop/m6/s12_onemany/TicketData.js';
import { Scene } from '../../support/scene.js';

const NOW = LocalDateTime.of(2026, 10, 2, 12, 0);

function ticket(price: string, daysAfter: number, hour: number, minute: number): TicketData {
  return new TicketData(Money.of(price), NOW.toLocalDate().plusDays(daysAfter).atTime(LocalTime.of(hour, minute)));
}

/**
 * Klient (CancellationDesk) zwraca jeden bilet albo listę. W start rozróżnia refund/refundAll,
 * w kroku 3 zawsze buduje TicketGroup (także z jednego biletu) - wynik jest ten sam.
 */
describe('S12EquivalenceTest', () => {
  describe('everyStepRefundsTheSame', () => {
    Scene.variants<readonly TicketData[], string>()
      .variant('start', (tickets) => new start.CancellationDesk().refund(tickets, NOW).toString())
      .variant('step1', (tickets) => new step1.CancellationDesk().refund(tickets, NOW).toString())
      .variant('step2', (tickets) => new step2.CancellationDesk().refund(tickets, NOW).toString())
      .variant('step3', (tickets) => new step3.CancellationDesk().refund(tickets, NOW).toString())
      .expect('jeden, ponad 24h', [ticket('40.00', 2, 18, 0)], '37.00')
      .expect('jeden, dokładnie 24h', [ticket('25.00', 1, 12, 0)], '22.00')
      .expect('jeden, 23h59m - 50%', [ticket('25.00', 1, 11, 59)], '9.50')
      .expect('jeden, po starcie - potrącenie nie schodzi poniżej 0', [ticket('25.00', 0, 11, 0)], '0.00')
      .expect('wiele - potrącenie raz',
        [ticket('40.00', 2, 18, 0), ticket('32.00', 0, 20, 0), ticket('25.00', 0, 11, 0)], '53.00')
      .expect('pusta lista', [], '0.00')
      .tests();
  });
});
