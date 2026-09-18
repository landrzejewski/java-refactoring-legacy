import { IllegalArgumentError } from '../../shared/errors.js';
import { requireNonNull } from '../../shared/requireNonNull.js';
import type { EquipmentType } from './EquipmentType.js';

// Odpowiednik rekordu Javy: niemutowalne pola + walidacja w konstruktorze.
export class RentalRequest {
  readonly customerName: string;
  readonly equipmentType: EquipmentType;

  constructor(
    customerName: string,
    equipmentType: EquipmentType,
    readonly days: number,
    readonly insurance: boolean,
    readonly delivery: boolean,
  ) {
    this.customerName = requireNonNull(customerName, 'customerName');
    this.equipmentType = requireNonNull(equipmentType, 'equipmentType');

    if (customerName.trim().length === 0) {
      throw new IllegalArgumentError('Customer name must not be blank');
    }
    if (days <= 0) {
      throw new IllegalArgumentError('Rental days must be positive');
    }
  }
}
