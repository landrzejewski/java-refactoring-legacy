import { Decimal } from 'decimal.js';
import { EquipmentType } from '../model/EquipmentType.js';
import type { RentalRequest } from '../model/RentalRequest.js';

// Etap 1: nazwane stałe i metody pomocnicze, nadal jedna klasa odpowiedzialna za wszystko.
export class RentalQuoteService {
  private static readonly LONG_RENTAL_DAYS = 7;
  private static readonly INSURANCE_DAILY_RATE = new Decimal('8.00');
  private static readonly DELIVERY_FEE = new Decimal('25.00');
  private static readonly VAT_RATE = new Decimal('0.23');
  private static readonly ZERO_MONEY = new Decimal('0.00');

  private readonly dailyRates: ReadonlyMap<EquipmentType, Decimal> = new Map([
    [EquipmentType.DRILL, new Decimal('39.99')],
    [EquipmentType.GENERATOR, new Decimal('120.00')],
  ]);
  private readonly longRentalDiscountRate = new Decimal('0.10');

  createQuote(request: RentalRequest): string {
    const dailyRate = this.dailyRates.get(request.equipmentType)!;
    const rentalDays = new Decimal(request.days);
    const baseRentalCost = RentalQuoteService.money(dailyRate.times(rentalDays));
    const discount = this.calculateDiscount(request, baseRentalCost);
    const insuranceCost = RentalQuoteService.calculateInsuranceCost(request);
    const deliveryCost = RentalQuoteService.calculateDeliveryCost(request);
    const netAmount = RentalQuoteService.money(
      baseRentalCost.minus(discount).plus(insuranceCost).plus(deliveryCost),
    );
    const vat = RentalQuoteService.money(netAmount.times(RentalQuoteService.VAT_RATE));
    const total = RentalQuoteService.money(netAmount.plus(vat));

    return RentalQuoteService.buildDocument(
      request,
      baseRentalCost,
      discount,
      insuranceCost,
      deliveryCost,
      netAmount,
      vat,
      total,
    );
  }

  private calculateDiscount(request: RentalRequest, baseRentalCost: Decimal): Decimal {
    if (!RentalQuoteService.qualifiesForLongRentalDiscount(request)) {
      return RentalQuoteService.ZERO_MONEY;
    }
    return RentalQuoteService.money(baseRentalCost.times(this.longRentalDiscountRate));
  }

  private static qualifiesForLongRentalDiscount(request: RentalRequest): boolean {
    return request.days >= RentalQuoteService.LONG_RENTAL_DAYS;
  }

  private static calculateInsuranceCost(request: RentalRequest): Decimal {
    if (!request.insurance) {
      return RentalQuoteService.ZERO_MONEY;
    }
    return RentalQuoteService.money(RentalQuoteService.INSURANCE_DAILY_RATE.times(request.days));
  }

  private static calculateDeliveryCost(request: RentalRequest): Decimal {
    return request.delivery ? RentalQuoteService.DELIVERY_FEE : RentalQuoteService.ZERO_MONEY;
  }

  // Odpowiednik String.format(Locale.ROOT, ...): szablon bez API zależnych od locale.
  private static buildDocument(
    request: RentalRequest,
    baseRentalCost: Decimal,
    discount: Decimal,
    insuranceCost: Decimal,
    deliveryCost: Decimal,
    netAmount: Decimal,
    vat: Decimal,
    total: Decimal,
  ): string {
    return `RENTAL QUOTE
Customer: ${request.customerName.trim().toUpperCase()}
Equipment: ${request.equipmentType}
Days: ${request.days}
Base: ${baseRentalCost.toFixed(2)}
Discount: ${discount.toFixed(2)}
Insurance: ${insuranceCost.toFixed(2)}
Delivery: ${deliveryCost.toFixed(2)}
Net: ${netAmount.toFixed(2)}
VAT: ${vat.toFixed(2)}
Total: ${total.toFixed(2)}
`;
  }

  private static money(amount: Decimal): Decimal {
    return amount.toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }
}
