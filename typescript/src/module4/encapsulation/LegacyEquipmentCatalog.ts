import type { Decimal } from 'decimal.js';
import type { EquipmentType } from '../model/EquipmentType.js';

// Publiczne, mutowalne pola i współdzielona (aliasowana) mapa wywołującego.
export class LegacyEquipmentCatalog {
  constructor(
    public name: string,
    public readonly dailyRates: Map<EquipmentType, Decimal>,
  ) {}
}
