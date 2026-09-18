import { describe, expect, it } from 'vitest';
import { IllegalArgumentError } from '../../../src/shared/errors.js';
import { EquipmentType } from '../../../src/module4/model/EquipmentType.js';
import { RentalRequest } from '../../../src/module4/model/RentalRequest.js';

describe('RentalRequestTest', () => {
  it('rejectsInvalidInput', () => {
    expect.soft(() => new RentalRequest(' ', EquipmentType.DRILL, 1, false, false))
      .toThrow(IllegalArgumentError);
    expect.soft(() => new RentalRequest('Acme', EquipmentType.DRILL, 0, false, false))
      .toThrow(IllegalArgumentError);
  });
});
