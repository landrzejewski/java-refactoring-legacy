import type { RentalRequest } from '../model/RentalRequest.js';
import type { PriceBreakdown } from '../pricing/PriceBreakdown.js';
import { RentalPricing } from '../pricing/RentalPricing.js';
import { requireNonNull } from '../../shared/requireNonNull.js';

// Etap 2: obliczenia wydzielone do RentalPricing, usługa jedynie deleguje i formatuje.
export class RentalQuoteService {
  private readonly pricing: RentalPricing;

  constructor(pricing: RentalPricing = RentalPricing.standard()) {
    this.pricing = requireNonNull(pricing);
  }

  createQuote(request: RentalRequest): string {
    requireNonNull(request, 'request');

    const price = this.calculatePrice(request);
    return RentalQuoteService.buildDocument(request, price);
  }

  private calculatePrice(request: RentalRequest): PriceBreakdown {
    return this.pricing.calculate(request);
  }

  // Odpowiednik String.format(Locale.ROOT, ...): szablon bez API zależnych od locale.
  private static buildDocument(request: RentalRequest, price: PriceBreakdown): string {
    return `RENTAL QUOTE
Customer: ${request.customerName.trim().toUpperCase()}
Equipment: ${request.equipmentType}
Days: ${request.days}
Base: ${price.baseRentalCost.toFixed(2)}
Discount: ${price.discount.toFixed(2)}
Insurance: ${price.insuranceCost.toFixed(2)}
Delivery: ${price.deliveryCost.toFixed(2)}
Net: ${price.netAmount.toFixed(2)}
VAT: ${price.vat.toFixed(2)}
Total: ${price.total.toFixed(2)}
`;
  }
}
