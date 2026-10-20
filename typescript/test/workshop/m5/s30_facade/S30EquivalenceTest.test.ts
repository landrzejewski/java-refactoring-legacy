import { describe } from 'vitest';

import * as start from '../../../../src/workshop/m5/s30_facade/start/Cinema.js';
import * as step1 from '../../../../src/workshop/m5/s30_facade/step1/Cinema.js';
import * as step2 from '../../../../src/workshop/m5/s30_facade/step2/Cinema.js';
import * as step3 from '../../../../src/workshop/m5/s30_facade/step3/Cinema.js';
import { Scene } from '../../support/scene.js';
import { type AnyCinema, channels } from './S30Fixtures.js';

function session(cinema: () => AnyCinema): (lines: readonly string[]) => string {
  return (lines) => {
    const handle = channels(cinema());
    return lines.map(handle).join('\n');
  };
}

/**
 * Ścieżki, na których obie kopie orkiestracji zgadzają się ze sobą: zakup w obu kanałach,
 * konflikt miejsc między kanałami, odmowa w sklepie, nieznany typ biletu. Odmowa w kasie
 * celowo jest poza tą tabelą - tam start ma błąd, a krok 2 go naprawia (S30BehaviourChangeTest).
 */
describe('S30EquivalenceTest', () => {
  describe('everyStepSellsTheSameOnTheCorrectPaths', () => {
    Scene.variants<readonly string[], string>()
      .variant('start', session(() => new start.Cinema()))
      .variant('step1', session(() => new step1.Cinema()))
      .variant('step2', session(() => new step2.Cinema()))
      .variant('step3', session(() => new step3.Cinema()))
      .expect('sklep: zakup z opłatą rezerwacyjną', ['online S1 A1,A2 N 4111 anna@example.com', 'state'], [
        'OK 54.00',
        'miejsca={S1=[A1, A2]} platnosci=[CHARGED 4111 54.00] maile=[anna@example.com: Bilety S1 A1,A2 = 54.00]',
      ].join('\n'))
      .expect('kasa: zakup bez opłaty', ['desk S1 B1 S 4111 jan@example.com', 'state'], [
        'OK 18.75',
        'miejsca={S1=[B1]} platnosci=[CHARGED 4111 18.75] maile=[jan@example.com: Bilety S1 B1 = 18.75]',
      ].join('\n'))
      .expect('miejsce zajęte przez drugi kanał: nic się nie zmienia', [
        'online S1 A1 N 4111 anna@example.com', 'desk S1 A2,A1 C 4111 jan@example.com', 'state'], [
        'OK 27.00',
        'ERROR seat taken A1',
        'miejsca={S1=[A1]} platnosci=[CHARGED 4111 27.00] maile=[anna@example.com: Bilety S1 A1 = 27.00]',
      ].join('\n'))
      .expect('sklep: odmowa płatności zwalnia miejsca', [
        'online S1 A1,A2 E 40000 anna@example.com', 'state', 'desk S1 A1 N 4111 jan@example.com'], [
        'ERROR payment declined',
        'miejsca={S1=[]} platnosci=[DECLINED 40000 39.00] maile=[]',
        'OK 25.00',
      ].join('\n'))
      .expect('nieznany typ biletu: przed rezerwacją', [
        'online S1 A1 X 4111 anna@example.com', 'desk S1 A1 X 4111 jan@example.com', 'state'], [
        'ERROR unknown ticket type: X',
        'ERROR unknown ticket type: X',
        'miejsca={} platnosci=[] maile=[]',
      ].join('\n'))
      .expect('różne seanse się nie blokują', [
        'online S2 A1 C 4111 anna@example.com', 'desk S1 A1 N 4111 jan@example.com', 'state'], [
        'OK 17.00',
        'OK 25.00',
        'miejsca={S1=[A1], S2=[A1]} platnosci=[CHARGED 4111 17.00, CHARGED 4111 25.00] '
          + 'maile=[anna@example.com: Bilety S2 A1 = 17.00, jan@example.com: Bilety S1 A1 = 25.00]',
      ].join('\n'))
      .tests();
  });
});
