import { Decimal } from 'decimal.js';
import { ConsoleQuoteNotifier } from './adapter/ConsoleQuoteNotifier.js';
import { InMemoryQuoteRepository } from './adapter/InMemoryQuoteRepository.js';
import { Command, CreateDeliveryQuote } from './application/CreateDeliveryQuote.js';
import { DeliveryPriceCalculator } from './domain/DeliveryPriceCalculator.js';
import { ExpressDeliveryPricePolicy } from './domain/ExpressDeliveryPricePolicy.js';
import { FuelSurcharge } from './domain/FuelSurcharge.js';
import { Parcel } from './domain/Parcel.js';
import { ShippingMethod } from './domain/ShippingMethod.js';
import { StandardDeliveryPricePolicy } from './domain/StandardDeliveryPricePolicy.js';
import { LegacyDeliveryQuoteService } from './legacy/LegacyDeliveryQuoteService.js';

export class Module3Examples {
  private constructor() {}

  static main(_args: readonly string[] = []): void {
    const parcel = new Parcel(new Decimal('3.00'));
    const legacy = new LegacyDeliveryQuoteService();
    const legacyQuote = legacy.createQuote(
      'developer@example.com',
      ShippingMethod.STANDARD,
      parcel);

    const fuelSurcharge = new FuelSurcharge(new Decimal('0.08'));
    const calculator = new DeliveryPriceCalculator([
      new StandardDeliveryPricePolicy(fuelSurcharge),
      new ExpressDeliveryPricePolicy(fuelSurcharge),
    ]);
    const repository = new InMemoryQuoteRepository();
    const useCase = new CreateDeliveryQuote(
      calculator,
      repository,
      new ConsoleQuoteNotifier());

    const refactoredQuote = useCase.execute(new Command(
      'developer@example.com',
      ShippingMethod.STANDARD,
      parcel));

    console.log(
      'Legacy and refactored prices equal: '
        + legacyQuote.price.equals(refactoredQuote.price));
    console.log('Stored quotes: ' + repository.quotes().length);
  }
}
