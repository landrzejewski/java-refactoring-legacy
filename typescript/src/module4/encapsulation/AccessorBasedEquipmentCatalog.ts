import type { Decimal } from 'decimal.js';
import type { EquipmentType } from '../model/EquipmentType.js';

// Etap przejściowy: pola schowane za akcesorami, ale bez walidacji i nadal z aliasem mapy.
export class AccessorBasedEquipmentCatalog {
  #name: string;
  readonly #dailyRates: Map<EquipmentType, Decimal>;

  constructor(name: string, dailyRates: Map<EquipmentType, Decimal>) {
    this.#name = name;
    this.#dailyRates = dailyRates;
  }

  name(): string {
    return this.#name;
  }

  setName(newName: string): void {
    this.#name = newName;
  }

  dailyRates(): Map<EquipmentType, Decimal> {
    return this.#dailyRates;
  }
}
