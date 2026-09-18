import { requireNonNull } from '../../shared/requireNonNull.js';
import type { DeliveryPriceCalculator } from '../domain/DeliveryPriceCalculator.js';
import { DeliveryQuote } from '../domain/DeliveryQuote.js';
import type { Parcel } from '../domain/Parcel.js';
import type { ShippingMethod } from '../domain/ShippingMethod.js';
import type { QuoteNotifier } from './QuoteNotifier.js';
import type { QuoteRepository } from './QuoteRepository.js';

export class CreateDeliveryQuote {
  private readonly priceCalculator: DeliveryPriceCalculator;
  private readonly repository: QuoteRepository;
  private readonly notifier: QuoteNotifier;

  constructor(
    priceCalculator: DeliveryPriceCalculator,
    repository: QuoteRepository,
    notifier: QuoteNotifier,
  ) {
    this.priceCalculator = requireNonNull(priceCalculator);
    this.repository = requireNonNull(repository);
    this.notifier = requireNonNull(notifier);
  }

  execute(command: Command): DeliveryQuote {
    requireNonNull(command, 'command');

    const quote = new DeliveryQuote(
      command.customerEmail,
      command.method,
      command.parcel,
      this.priceCalculator.priceFor(command.method, command.parcel));

    this.repository.save(quote);
    this.notifier.quoteCreated(quote);
    return quote;
  }
}

// W Javie: CreateDeliveryQuote.Command.
export class Command {
  readonly customerEmail: string;
  readonly method: ShippingMethod;
  readonly parcel: Parcel;

  constructor(customerEmail: string, method: ShippingMethod, parcel: Parcel) {
    this.customerEmail = requireNonNull(customerEmail, 'customerEmail');
    this.method = requireNonNull(method, 'method');
    this.parcel = requireNonNull(parcel, 'parcel');
  }
}
