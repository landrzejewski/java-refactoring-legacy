import { describe } from 'vitest';

import { IllegalArgumentError } from '../../../../src/shared/errors.js';
import * as start from '../../../../src/workshop/m5/s32_mediator/start/SeatSelectionScreen.js';
import * as step1 from '../../../../src/workshop/m5/s32_mediator/step1/SeatSelectionScreen.js';
import * as step2 from '../../../../src/workshop/m5/s32_mediator/step2/SeatSelectionScreen.js';
import * as step3 from '../../../../src/workshop/m5/s32_mediator/step3/SeatSelectionScreen.js';
import { Scene } from '../../support/scene.js';

/** Akcje użytkownika na komponentach ekranu (wspólny kształt ekranów wszystkich wariantów). */
interface Screen {
  readonly seatMap: { click(seat: string): void };
  readonly basket: { remove(seat: string): void; clear(): void };
  readonly priceSummary: { applyVoucher(code: string): void };
  readonly payButton: { press(): string };
  render(): string;
}

/** Zwraca komunikat dla "pay" i "render", null dla akcji bez komunikatu. */
function handle(screen: Screen, line: string): string | null {
  const [action, argument = ''] = line.split(' ');
  switch (action) {
    case 'click':
      screen.seatMap.click(argument);
      return null;
    case 'remove':
      screen.basket.remove(argument);
      return null;
    case 'clear':
      screen.basket.clear();
      return null;
    case 'voucher':
      screen.priceSummary.applyVoucher(argument);
      return null;
    case 'pay':
      return screen.payButton.press();
    case 'render':
      return screen.render();
    default:
      throw new IllegalArgumentError(line);
  }
}

function session(screen: () => Screen): (lines: readonly string[]) => string {
  return (lines) => {
    const ui = screen();
    return lines.map((line) => handle(ui, line)).filter((message) => message !== null).join('\n');
  };
}

/**
 * Scenariusze klikania w ekranie wyboru miejsc: każda akcja użytkownika musi odświeżyć te same
 * komponenty w tej samej kolejności. Wynik to komunikaty przycisku i stan ekranu (render).
 */
describe('S32EquivalenceTest', () => {
  describe('everyStepCoordinatesTheScreenTheSame', () => {
    Scene.variants<readonly string[], string>()
      .variant('start', session(() => new start.SeatSelectionScreen()))
      .variant('step1', session(() => new step1.SeatSelectionScreen()))
      .variant('step2', session(() => new step2.SeatSelectionScreen()))
      .variant('step3', session(() => new step3.SeatSelectionScreen()))
      .expect('ekran startowy', ['render', 'pay'], [
        'wybrane=[] sprzedane=[B2] koszyk=[] suma=0.00 przycisk=Wybierz miejsca',
        'Nieaktywny: Wybierz miejsca',
      ].join('\n'))
      .expect('wybór miejsc: koszyk w kolejności kliknięć, VIP w rzędzie C', ['click C2', 'click A1', 'render'],
        'wybrane=[A1, C2] sprzedane=[B2] koszyk=[C2, A1] suma=60.00 przycisk=Zaplac 60.00')
      .expect('drugie kliknięcie odznacza', ['click A1', 'click A2', 'click A1', 'render'],
        'wybrane=[A2] sprzedane=[B2] koszyk=[A2] suma=25.00 przycisk=Zaplac 25.00')
      .expect('sprzedane i spoza sali są ignorowane', ['click B2', 'click Z9', 'render'],
        'wybrane=[] sprzedane=[B2] koszyk=[] suma=0.00 przycisk=Wybierz miejsca')
      .expect('usunięcie z koszyka odznacza na mapie', ['click A1', 'click C1', 'remove A1', 'remove A3', 'render'],
        'wybrane=[C1] sprzedane=[B2] koszyk=[C1] suma=35.00 przycisk=Zaplac 35.00')
      .expect('limit 4 miejsc wyłącza przycisk', [
        'click A1', 'click A2', 'click A3', 'click A4', 'click C1', 'render', 'pay', 'remove C1', 'render',
      ], [
        'wybrane=[A1, A2, A3, A4, C1] sprzedane=[B2] koszyk=[A1, A2, A3, A4, C1] suma=135.00 '
          + 'przycisk=Maksymalnie 4 miejsca',
        'Nieaktywny: Maksymalnie 4 miejsca',
        'wybrane=[A1, A2, A3, A4] sprzedane=[B2] koszyk=[A1, A2, A3, A4] suma=100.00 przycisk=Zaplac 100.00',
      ].join('\n'))
      .expect('voucher przelicza sumę i przycisk', [
        'click C1', 'click C2', 'voucher KINO10', 'render', 'voucher XYZ', 'render',
      ], [
        'wybrane=[C1, C2] sprzedane=[B2] koszyk=[C1, C2] suma=63.00 przycisk=Zaplac 63.00',
        'wybrane=[C1, C2] sprzedane=[B2] koszyk=[C1, C2] suma=70.00 przycisk=Zaplac 70.00',
      ].join('\n'))
      .expect('płatność sprzedaje miejsca i czyści ekran', [
        'click A1', 'click C4', 'voucher KINO10', 'pay', 'render', 'click A1', 'click A2', 'render',
      ], [
        'Zaplacono 54.00 za A1,C4',
        'wybrane=[] sprzedane=[A1, B2, C4] koszyk=[] suma=0.00 przycisk=Wybierz miejsca',
        'wybrane=[A2] sprzedane=[A1, B2, C4] koszyk=[A2] suma=22.50 przycisk=Zaplac 22.50',
      ].join('\n'))
      .expect('wyczyszczenie koszyka', ['click A1', 'click C3', 'clear', 'render'],
        'wybrane=[] sprzedane=[B2] koszyk=[] suma=0.00 przycisk=Wybierz miejsca')
      .tests();
  });
});
