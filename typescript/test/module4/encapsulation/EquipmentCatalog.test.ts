import { Decimal } from 'decimal.js';
import { describe, expect, it } from 'vitest';
import { IllegalArgumentError, UnsupportedOperationError } from '../../../src/shared/errors.js';
import { AccessorBasedEquipmentCatalog } from '../../../src/module4/encapsulation/AccessorBasedEquipmentCatalog.js';
import { EquipmentCatalog } from '../../../src/module4/encapsulation/EquipmentCatalog.js';
import { LegacyEquipmentCatalog } from '../../../src/module4/encapsulation/LegacyEquipmentCatalog.js';
import { EquipmentType } from '../../../src/module4/model/EquipmentType.js';

function rates(): Map<EquipmentType, Decimal> {
  return new Map([[EquipmentType.DRILL, new Decimal('39.99')]]);
}

// Odpowiednik assertEquals(new BigDecimal("x.yy"), actual): równa wartość i skala 2.
function expectMoney(actual: Decimal | undefined, expected: string): void {
  expect(actual?.toString()).toBe(new Decimal(expected).toString());
  expect(actual?.decimalPlaces()).toBeLessThanOrEqual(2);
}

describe('EquipmentCatalogTest', () => {
  it('legacyCatalogSharesItsMutableMapWithTheCaller', () => {
    const source = rates();
    const catalog = new LegacyEquipmentCatalog('Summer rental', source);

    source.set(EquipmentType.DRILL, new Decimal('1.00'));

    expectMoney(catalog.dailyRates.get(EquipmentType.DRILL), '1.00');
  });

  it('accessorBasedCatalogPreservesAliasesDuringControlledMigration', () => {
    const source = rates();
    const catalog = new AccessorBasedEquipmentCatalog('Summer rental', source);

    source.set(EquipmentType.DRILL, new Decimal('1.00'));
    catalog.setName(' ');

    expect(catalog.dailyRates()).toBe(source);
    expectMoney(catalog.dailyRates().get(EquipmentType.DRILL), '1.00');
    expect(catalog.name()).toBe(' ');
  });

  it('encapsulatedCatalogOwnsRatesAndReturnsUnmodifiableSnapshots', () => {
    const source = rates();
    const catalog = new EquipmentCatalog('Summer rental', source);
    const snapshot = catalog.dailyRates();

    source.set(EquipmentType.DRILL, new Decimal('1.00'));
    expectMoney(catalog.dailyRateFor(EquipmentType.DRILL), '39.99');

    catalog.changeDailyRate(EquipmentType.DRILL, new Decimal('42.00'));

    expectMoney(snapshot.get(EquipmentType.DRILL), '39.99');
    expectMoney(catalog.dailyRateFor(EquipmentType.DRILL), '42.00');
    expect(() =>
      (snapshot as Map<EquipmentType, Decimal>).set(EquipmentType.GENERATOR, new Decimal('120.00')),
    ).toThrow(UnsupportedOperationError);
  });

  it('changesNameOnlyThroughValidatedOperation', () => {
    const catalog = new EquipmentCatalog('Summer rental', rates());

    catalog.renameTo('Winter rental');

    expect(catalog.name()).toBe('Winter rental');
    expect(() => catalog.renameTo(' ')).toThrow(IllegalArgumentError);
  });

  it('normalizesRateBeforeCheckingItsInvariant', () => {
    const catalog = new EquipmentCatalog('Summer rental', rates());

    catalog.changeDailyRate(EquipmentType.DRILL, new Decimal('42.005'));

    expectMoney(catalog.dailyRateFor(EquipmentType.DRILL), '42.01');
    expect(() => catalog.changeDailyRate(EquipmentType.DRILL, new Decimal('0.004')))
      .toThrow(IllegalArgumentError);
  });
});
