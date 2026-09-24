import { describe, expect, it } from 'vitest';

import { TicketQuery } from '../../../../src/workshop/m8/s06_reviewableseries/TicketQuery.js';
import * as step2 from '../../../../src/workshop/m8/s06_reviewableseries/step2/PriceList.js';
import * as step3 from '../../../../src/workshop/m8/s06_reviewableseries/step3/PriceList.js';
import { Money } from '../../../../src/workshop/shared/Money.js';
import { LocalDateTime } from '../../../../src/workshop/shared/time.js';
import { MONDAY_EVENING, TUESDAY_EVENING, TUESDAY_MORNING } from './S06Fixtures.js';

const before = (query: TicketQuery): Money => new step2.PriceList().price(query);
const after = (query: TicketQuery): Money => new step3.PriceList().price(query);

/**
 * Commit 3 zmienia zachowanie - test pokazuje nową regułę i to, że zmienia ona WYŁĄCZNIE
 * bilety NORMAL we wtorek (różnica zachowania między commitem 2 a 3 na siatce przypadków).
 */
describe('S06SolutionTest', () => {
  it('cheapTuesdayGivesNormalTicketTwentyPercentOff', () => {
    expect(after(new TicketQuery('2D', 'NORMAL', TUESDAY_EVENING, 5))).toEqual(Money.of('20.00'));
    // IMAX: 40 - 20% = 32, rano -5, VIP +10
    expect(after(new TicketQuery('IMAX', 'NORMAL', TUESDAY_MORNING, 12))).toEqual(Money.of('37.00'));
    expect(after(new TicketQuery('2D', 'NORMAL', MONDAY_EVENING, 5))).toEqual(Money.of('25.00'));
    expect(after(new TicketQuery('2D', 'STUDENT', TUESDAY_EVENING, 5))).toEqual(Money.of('18.75'));
  });

  it('behaviourChangeIsLimitedToNormalTicketsOnTuesday', () => {
    const changed: TicketQuery[] = [];
    let checked = 0;
    for (const format of ['2D', '3D', 'IMAX']) {
      for (const type of ['NORMAL', 'STUDENT', 'SENIOR', 'CHILD']) {
        for (let day = 9; day <= 15; day++) {
          for (const hour of [10, 18]) {
            for (const row of [1, 10]) {
              const query = new TicketQuery(format, type, LocalDateTime.of(2026, 3, day, hour, 0), row);
              checked++;
              if (!before(query).equals(after(query))) {
                changed.push(query);
              }
            }
          }
        }
      }
    }
    expect(checked).toBe(336);
    expect(changed, '3 formaty x 2 pory x 2 rzędy').toHaveLength(3 * 2 * 2);
    expect(changed.every((q) => q.type === 'NORMAL' && q.start.dayOfWeek === 'TUESDAY'), changed.join(', ')).toBe(true);
  });
});
