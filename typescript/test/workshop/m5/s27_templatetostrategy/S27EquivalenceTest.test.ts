import { describe } from 'vitest';

import { IllegalArgumentError } from '../../../../src/shared/errors.js';
import { Money } from '../../../../src/workshop/shared/Money.js';
import { LocalDate, LocalDateTime } from '../../../../src/workshop/shared/time.js';
import { ScreeningSale } from '../../../../src/workshop/m5/s27_templatetostrategy/ScreeningSale.js';
import * as start from '../../../../src/workshop/m5/s27_templatetostrategy/start/Reports.js';
import * as step1 from '../../../../src/workshop/m5/s27_templatetostrategy/step1/Reports.js';
import * as step2 from '../../../../src/workshop/m5/s27_templatetostrategy/step2/Reports.js';
import * as step3 from '../../../../src/workshop/m5/s27_templatetostrategy/step3/Reports.js';
import { Scene } from '../../support/scene.js';

interface Request {
  period: string;
  format: string;
  day: LocalDate;
}

interface Renderer {
  render(day: LocalDate, sales: readonly ScreeningSale[]): string;
}

type ReportFactory = (period: string, format: string) => Renderer;

// Odpowiednik LocalDate.parse("2026-10-20").
function date(text: string): LocalDate {
  return LocalDateTime.parse(`${text}T00:00`).toLocalDate();
}

function sale(day: string, title: string, tickets: number, revenue: string): ScreeningSale {
  return new ScreeningSale(date(day), title, tickets, Money.of(revenue));
}

const SALES: readonly ScreeningSale[] = [
  sale('2026-10-18', 'Amator', 4, '100.00'),
  sale('2026-10-19', 'Diuna', 10, '400.00'),
  sale('2026-10-20', 'Diuna', 5, '200.00'),
  sale('2026-10-20', 'Kraina Lodu', 3, '81.00'),
  sale('2026-10-20', 'Szybcy; reedycja', 1, '25.00'),
  sale('2026-10-25', 'Amator', 2, '50.00'),
  sale('2026-10-26', 'Diuna', 7, '280.00'),
];

function render(reports: ReportFactory): (request: Request) => string {
  return (request) => {
    try {
      return reports(request.period, request.format).render(request.day, SALES);
    } catch (exception) {
      if (exception instanceof IllegalArgumentError) {
        return `ERROR ${exception.message}`;
      }
      throw exception;
    }
  };
}

function request(period: string, format: string, day: string): Request {
  return { period, format, day: date(day) };
}

/**
 * Wszystkie cztery kombinacje okres x format na tych samych danych, granice tygodnia
 * (niedziela przed, poniedziałek po), dzień bez sprzedaży i kolejność błędów wyboru.
 */
describe('S27EquivalenceTest', () => {
  describe('everyStepRendersEveryCombinationTheSame', () => {
    Scene.variants<Request, string>()
      .variant('start', render((p, f) => start.Reports.of(p, f)))
      .variant('step1', render((p, f) => step1.Reports.of(p, f)))
      .variant('step2', render((p, f) => step2.Reports.of(p, f)))
      .variant('step3', render((p, f) => step3.Reports.of(p, f)))
      .expect('dzień CSV (escapowanie średnika)', request('DAILY', 'CSV', '2026-10-20'), [
        '# raport dzien 2026-10-20',
        'film;bilety;przychod',
        'Diuna;5;200.00',
        'Kraina Lodu;3;81.00',
        '"Szybcy; reedycja";1;25.00',
        'SUMA;9;306.00',
        '',
      ].join('\n'))
      .expect('dzień tekst', request('DAILY', 'TEXT', '2026-10-20'), [
        'RAPORT DZIEN 2026-10-20',
        '-------------------------------------',
        'Diuna                    5     200.00',
        'Kraina Lodu              3      81.00',
        'Szybcy; reedycja         1      25.00',
        '-------------------------------------',
        'RAZEM                    9     306.00',
        '',
      ].join('\n'))
      .expect('tydzień CSV ze środy (sumy po tytule)', request('WEEKLY', 'CSV', '2026-10-22'), [
        '# raport tydzien 2026-10-19..2026-10-25',
        'film;bilety;przychod',
        'Amator;2;50.00',
        'Diuna;15;600.00',
        'Kraina Lodu;3;81.00',
        '"Szybcy; reedycja";1;25.00',
        'SUMA;21;756.00',
        '',
      ].join('\n'))
      .expect('tydzień tekst z niedzieli (ten sam tydzień)', request('WEEKLY', 'TEXT', '2026-10-25'), [
        'RAPORT TYDZIEN 2026-10-19..2026-10-25',
        '-------------------------------------',
        'Amator                   2      50.00',
        'Diuna                   15     600.00',
        'Kraina Lodu              3      81.00',
        'Szybcy; reedycja         1      25.00',
        '-------------------------------------',
        'RAZEM                   21     756.00',
        '',
      ].join('\n'))
      .expect('tydzień od poniedziałku: następny tydzień', request('WEEKLY', 'CSV', '2026-10-26'), [
        '# raport tydzien 2026-10-26..2026-11-01',
        'film;bilety;przychod',
        'Diuna;7;280.00',
        'SUMA;7;280.00',
        '',
      ].join('\n'))
      .expect('dzień bez sprzedaży', request('DAILY', 'CSV', '2026-10-21'), [
        '# raport dzien 2026-10-21',
        'film;bilety;przychod',
        'SUMA;0;0.00',
        '',
      ].join('\n'))
      .expect('nieznany okres', request('MONTHLY', 'CSV', '2026-10-20'), 'ERROR unknown period: MONTHLY')
      .expect('nieznany format', request('DAILY', 'PDF', '2026-10-20'), 'ERROR unknown format: PDF')
      .expect('najpierw okres, potem format', request('MONTHLY', 'PDF', '2026-10-20'), 'ERROR unknown period: MONTHLY')
      .tests();
  });
});
