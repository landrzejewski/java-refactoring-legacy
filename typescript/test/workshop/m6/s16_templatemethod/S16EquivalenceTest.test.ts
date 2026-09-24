import { describe } from 'vitest';

import { Money } from '../../../../src/workshop/shared/Money.js';
import { LocalTime } from '../../../../src/workshop/shared/time.js';
import { Sale } from '../../../../src/workshop/m6/s16_templatemethod/Sale.js';
import * as startCsv from '../../../../src/workshop/m6/s16_templatemethod/start/CsvSalesReport.js';
import * as startHtml from '../../../../src/workshop/m6/s16_templatemethod/start/HtmlSalesReport.js';
import * as step1Csv from '../../../../src/workshop/m6/s16_templatemethod/step1/CsvSalesReport.js';
import * as step1Html from '../../../../src/workshop/m6/s16_templatemethod/step1/HtmlSalesReport.js';
import * as step2Csv from '../../../../src/workshop/m6/s16_templatemethod/step2/CsvSalesReport.js';
import * as step2Html from '../../../../src/workshop/m6/s16_templatemethod/step2/HtmlSalesReport.js';
import { Scene } from '../../support/scene.js';

/** Oba raporty (CSV i HTML) identyczne w każdym kroku - także sortowanie i escapowanie. */
describe('S16EquivalenceTest', () => {
  describe('everyStepRendersBothReportsTheSame', () => {
    Scene.variants<readonly Sale[], string>()
      .variant('start', (s) => new startCsv.CsvSalesReport().render(s) + new startHtml.HtmlSalesReport().render(s))
      .variant('step1', (s) => new step1Csv.CsvSalesReport().render(s) + new step1Html.HtmlSalesReport().render(s))
      .variant('step2', (s) => new step2Csv.CsvSalesReport().render(s) + new step2Html.HtmlSalesReport().render(s))
      .expect('sprzedaż dnia (nieposortowana na wejściu)', [
        new Sale(LocalTime.of(18, 0), 'Diuna', 3, Money.of('120.00')),
        new Sale(LocalTime.of(10, 0), 'Kraina Lodu', 2, Money.of('54.00')),
        new Sale(LocalTime.of(20, 30), 'Szybcy & Wsciekli; reedycja', 1, Money.of('25.00')),
      ], `godzina;film;bilety;kwota
10:00;Kraina Lodu;2;54.00
18:00;Diuna;3;120.00
20:30;"Szybcy & Wsciekli; reedycja";1;25.00
SUMA;;6;199.00
<table>
<tr><th>Godzina</th><th>Film</th><th>Bilety</th><th>Kwota</th></tr>
<tr><td>10:00</td><td>Kraina Lodu</td><td>2</td><td>54.00</td></tr>
<tr><td>18:00</td><td>Diuna</td><td>3</td><td>120.00</td></tr>
<tr><td>20:30</td><td>Szybcy &amp; Wsciekli; reedycja</td><td>1</td><td>25.00</td></tr>
<tr><td colspan="2">Suma</td><td>6</td><td>199.00</td></tr>
</table>
`)
      .expect('brak sprzedaży', [], `godzina;film;bilety;kwota
SUMA;;0;0.00
<table>
<tr><th>Godzina</th><th>Film</th><th>Bilety</th><th>Kwota</th></tr>
<tr><td colspan="2">Suma</td><td>0</td><td>0.00</td></tr>
</table>
`)
      .tests();
  });
});
