import { existsSync } from 'node:fs';

import { describe, expect, it } from 'vitest';

import { IllegalStateError } from '../../../../src/shared/errors.js';
import { Money } from '../../../../src/workshop/shared/Money.js';
import { LocalDate, LocalDateTime } from '../../../../src/workshop/shared/time.js';
import { ScreeningSale } from '../../../../src/workshop/m5/s27_templatetostrategy/ScreeningSale.js';
import { DailyCsvReport as Step2DailyCsvReport } from '../../../../src/workshop/m5/s27_templatetostrategy/step2/DailyCsvReport.js';
import * as step2 from '../../../../src/workshop/m5/s27_templatetostrategy/step2/ScreeningReport.js';
import { WeeklyTextReport as Step2WeeklyTextReport } from '../../../../src/workshop/m5/s27_templatetostrategy/step2/WeeklyTextReport.js';
import { CsvFormat } from '../../../../src/workshop/m5/s27_templatetostrategy/step3/CsvFormat.js';
import { DailyPeriod } from '../../../../src/workshop/m5/s27_templatetostrategy/step3/DailyPeriod.js';
import type { ReportFormat } from '../../../../src/workshop/m5/s27_templatetostrategy/step3/ReportFormat.js';
import { ScreeningReport } from '../../../../src/workshop/m5/s27_templatetostrategy/step3/ScreeningReport.js';
import { WeeklyPeriod } from '../../../../src/workshop/m5/s27_templatetostrategy/step3/WeeklyPeriod.js';
import { workshopDir } from '../../support/paths.js';

const STEP3 = (file: string) => workshopDir('m5', 's27_templatetostrategy', 'step3', file);

function date(text: string): LocalDate {
  return LocalDateTime.parse(`${text}T00:00`).toLocalDate();
}

/** Po kroku 3: jedna finalna klasa, osie dodają się zamiast mnożyć. */
describe('S27SolutionTest', () => {
  // TypeScript nie ma final ani refleksji typów parametrów: klasa daje się utworzyć wprost
  // z dwóch strategii (nie jest abstrakcyjna), a podklasa jest odrzucana przez konstruktor.
  it('reportIsOneFinalConcreteClassComposedOfTwoStrategies', () => {
    expect(ScreeningReport.length).toBe(2);
    expect(new ScreeningReport(new DailyPeriod(), new CsvFormat())).toBeInstanceOf(ScreeningReport);
    class Subclass extends ScreeningReport {}
    expect(() => new Subclass(new DailyPeriod(), new CsvFormat())).toThrow(IllegalStateError);
  });

  it('combinationSubclassesAreGone', () => {
    for (const name of ['DailyCsvReport', 'DailyTextReport', 'WeeklyCsvReport', 'WeeklyTextReport']) {
      expect(existsSync(STEP3(`${name}.ts`)), name).toBe(false);
    }
  });

  it('newFormatWorksWithEveryPeriodWithoutNewClasses', () => {
    const markdown: ReportFormat = {
      header: (label) => `## ${label}\n`,
      row: (title, tickets) => `- ${title}: ${tickets}\n`,
      footer: (_tickets, revenue) => `**${revenue.toString()}**\n`,
    };
    const sales = [
      new ScreeningSale(date('2026-10-19'), 'Diuna', 2, Money.of('80.00')),
      new ScreeningSale(date('2026-10-20'), 'Diuna', 1, Money.of('40.00')),
    ];
    expect(new ScreeningReport(new DailyPeriod(), markdown).render(date('2026-10-20'), sales))
      .toBe('## dzien 2026-10-20\n- Diuna: 1\n**40.00**\n');
    expect(new ScreeningReport(new WeeklyPeriod(), markdown).render(date('2026-10-20'), sales))
      .toBe('## tydzien 2026-10-19..2026-10-25\n- Diuna: 3\n**120.00**\n');
  });

  // Odpowiednik Modifier.isFinal(render): podklasy nie mają własnego render(),
  // a podklasa, która próbuje go nadpisać, jest odrzucana przez konstruktor bazy.
  it('beforeTheCollapseTheSkeletonWasStillAProtectedTemplateMethod', () => {
    expect(Object.hasOwn(step2.ScreeningReport.prototype, 'render')).toBe(true);
    expect(Object.hasOwn(Step2DailyCsvReport.prototype, 'render')).toBe(false);
    expect(Object.hasOwn(Step2WeeklyTextReport.prototype, 'render')).toBe(false);
    class Overriding extends Step2DailyCsvReport {
      override render(): string {
        return 'inny szkielet';
      }
    }
    expect(() => new Overriding()).toThrow(IllegalStateError);
  });
});
