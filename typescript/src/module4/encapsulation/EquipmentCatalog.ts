import { Decimal } from 'decimal.js';
import { IllegalArgumentError, UnsupportedOperationError } from '../../shared/errors.js';
import { requireNonNull } from '../../shared/requireNonNull.js';
import type { EquipmentType } from '../model/EquipmentType.js';

export class EquipmentCatalog {
  #name: string;
  readonly #dailyRates = new Map<EquipmentType, Decimal>();

  constructor(name: string, dailyRates: ReadonlyMap<EquipmentType, Decimal>) {
    this.#name = EquipmentCatalog.validName(name);
    requireNonNull(dailyRates, 'dailyRates');

    dailyRates.forEach((rate, type) => this.changeDailyRate(type, rate));
  }

  name(): string {
    return this.#name;
  }

  renameTo(newName: string): void {
    this.#name = EquipmentCatalog.validName(newName);
  }

  dailyRateFor(type: EquipmentType): Decimal {
    requireNonNull(type, 'type');
    const rate = this.#dailyRates.get(type);
    if (rate === undefined) {
      throw new IllegalArgumentError(`Missing daily rate for ${type}`);
    }
    return rate;
  }

  changeDailyRate(type: EquipmentType, newRate: Decimal): void {
    requireNonNull(type, 'type');
    requireNonNull(newRate, 'newRate');
    const normalizedRate = newRate.toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
    if (normalizedRate.lte(0)) {
      throw new IllegalArgumentError('Daily rate must be positive');
    }
    this.#dailyRates.set(type, normalizedRate);
  }

  // Odpowiednik Map.copyOf: niezależna kopia, której nie da się zmienić.
  dailyRates(): ReadonlyMap<EquipmentType, Decimal> {
    return unmodifiableCopy(this.#dailyRates);
  }

  private static validName(value: string): string {
    requireNonNull(value, 'name');
    if (value.trim().length === 0) {
      throw new IllegalArgumentError('Catalog name must not be blank');
    }
    return value;
  }
}

// Zamrożony Map nadal pozwala na set(), dlatego operacje modyfikujące rzucają jawnie.
function unmodifiableCopy<K, V>(source: ReadonlyMap<K, V>): ReadonlyMap<K, V> {
  const copy = new Map(source);
  const reject = (): never => {
    throw new UnsupportedOperationError('Map is unmodifiable');
  };
  Object.defineProperties(copy, {
    set: { value: reject },
    delete: { value: reject },
    clear: { value: reject },
  });
  return Object.freeze(copy);
}
