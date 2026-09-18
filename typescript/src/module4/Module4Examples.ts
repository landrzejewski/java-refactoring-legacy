import { Decimal } from 'decimal.js';
import { IllegalStateError } from '../shared/errors.js';
import { EquipmentCatalog } from './encapsulation/EquipmentCatalog.js';
import { EquipmentType } from './model/EquipmentType.js';
import { RentalRequest } from './model/RentalRequest.js';
import { RentalPricing } from './pricing/RentalPricing.js';
import { RentalQuoteService as Stage0RentalQuoteService } from './stage0/RentalQuoteService.js';
import { RentalQuoteService as Stage3RentalQuoteService } from './stage3/RentalQuoteService.js';

export class Module4Examples {
  private constructor() {}

  static main(_args: readonly string[] = []): void {
    const request = new RentalRequest(' Acme ', EquipmentType.GENERATOR, 8, true, true);

    const legacyQuote = new Stage0RentalQuoteService().createQuote(request);
    const refactoredQuote = new Stage3RentalQuoteService(RentalPricing.standard())
      .createQuote(request);

    if (legacyQuote !== refactoredQuote) {
      throw new IllegalStateError('Refactoring changed the quote');
    }

    const sourceRates = new Map<EquipmentType, Decimal>();
    sourceRates.set(EquipmentType.DRILL, new Decimal('39.99'));
    const catalog = new EquipmentCatalog('Summer rental', sourceRates);
    const snapshot = catalog.dailyRates();
    catalog.changeDailyRate(EquipmentType.DRILL, new Decimal('42.00'));

    process.stdout.write(refactoredQuote);
    console.log('Quote stages equivalent: true');
    console.log(
      'Catalog snapshot isolated: '
        + (snapshot.get(EquipmentType.DRILL)?.equals(new Decimal('39.99')) ?? false),
    );
  }
}
