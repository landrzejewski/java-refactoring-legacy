import { describe } from 'vitest';

import * as start from '../../../../src/workshop/m5/s07_collapsehierarchy/start/HallCatalog.js';
import * as step1 from '../../../../src/workshop/m5/s07_collapsehierarchy/step1/HallCatalog.js';
import * as step2 from '../../../../src/workshop/m5/s07_collapsehierarchy/step2/HallCatalog.js';
import * as step3 from '../../../../src/workshop/m5/s07_collapsehierarchy/step3/HallCatalog.js';
import { Scene } from '../../support/scene.js';

/** Test równoważności: opis sal i reguła VIP identyczne w start i każdym kroku. */
describe('S07EquivalenceTest', () => {
  describe('everyStepDescribesHallsTheSameWay', () => {
    Scene.variants<string, string>()
      .variant('start', (name) => new start.HallCatalog().describe(name))
      .variant('step1', (name) => new step1.HallCatalog().describe(name))
      .variant('step2', (name) => new step2.HallCatalog().describe(name))
      .variant('step3', (name) => new step3.HallCatalog().describe(name))
      .expect('zwykła sala', 'Sala 1', 'Sala 1: 180 miejsc, VIP od rzędu 10')
      .expect('sala IMAX - VIP w dwóch ostatnich rzędach', 'Sala IMAX',
        'Sala IMAX: 308 miejsc, VIP od rzędu 13')
      .expect('nieznana sala', 'Sala 9', 'brak sali: Sala 9')
      .tests();
  });

  describe('everyStepAppliesTheSameVipRule', () => {
    Scene.variants<number, boolean>()
      .variant('start', (row) => new start.HallCatalog().isVip('Sala IMAX', row))
      .variant('step1', (row) => new step1.HallCatalog().isVip('Sala IMAX', row))
      .variant('step2', (row) => new step2.HallCatalog().isVip('Sala IMAX', row))
      .variant('step3', (row) => new step3.HallCatalog().isVip('Sala IMAX', row))
      .expect('rząd 12 w IMAX - zwykły', 12, false)
      .expect('rząd 13 w IMAX - VIP', 13, true)
      .tests();
  });
});
