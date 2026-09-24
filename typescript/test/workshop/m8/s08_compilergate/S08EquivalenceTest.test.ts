import { describe } from 'vitest';

import * as startReport from '../../../../src/workshop/m8/s08_compilergate/start/OccupancyReport.js';
import * as startMap from '../../../../src/workshop/m8/s08_compilergate/start/SeatMap.js';
import * as step1Report from '../../../../src/workshop/m8/s08_compilergate/step1/OccupancyReport.js';
import * as step1Map from '../../../../src/workshop/m8/s08_compilergate/step1/SeatMap.js';
import * as step2Report from '../../../../src/workshop/m8/s08_compilergate/step2/OccupancyReport.js';
import * as step2Map from '../../../../src/workshop/m8/s08_compilergate/step2/SeatMap.js';
import * as step3Report from '../../../../src/workshop/m8/s08_compilergate/step3/OccupancyReport.js';
import * as step3Map from '../../../../src/workshop/m8/s08_compilergate/step3/SeatMap.js';
import { Scene } from '../../support/scene.js';

interface Input {
  readonly format: number;
  readonly seats: readonly string[];
}

/** Test równoważności: usuwanie ostrzeżeń kompilatora nie zmienia raportu obłożenia. */
describe('S08EquivalenceTest', () => {
  describe('everyStepDescribesOccupancyTheSameWay', () => {
    Scene.variants<Input, string>()
      .variant('start', (input) => {
        const map = new startMap.SeatMap();
        input.seats.forEach((seat) => map.take(seat));
        return new startReport.OccupancyReport().describe(map, input.format);
      })
      .variant('step1', (input) => {
        const map = new step1Map.SeatMap();
        input.seats.forEach((seat) => map.take(seat));
        return new step1Report.OccupancyReport().describe(map, input.format);
      })
      .variant('step2', (input) => {
        const map = new step2Map.SeatMap();
        input.seats.forEach((seat) => map.take(seat));
        return new step2Report.OccupancyReport().describe(map, input.format);
      })
      .variant('step3', (input) => {
        const map = new step3Map.SeatMap();
        input.seats.forEach((seat) => map.take(seat));
        return new step3Report.OccupancyReport().describe(map, input.format);
      })
      .expect('IMAX - przelot do Dolby', { format: 3, seats: ['A1', 'B1', 'C10'] },
        'IMAX [duzy ekran, dzwiek Dolby], cena 40 zl, zajete: {1=2, 10=1}')
      .expect('3D', { format: 2, seats: ['D5'] }, '3D [dzwiek Dolby], cena 32 zl, zajete: {5=1}')
      .expect('2D, pusta sala', { format: 1, seats: [] }, '2D [standard], cena 25 zl, zajete: {}')
      .tests();
  });
});
