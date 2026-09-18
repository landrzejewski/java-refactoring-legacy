import { Decimal } from 'decimal.js';
import { IllegalArgumentError } from '../../shared/errors.js';
import { requireNonNull } from '../../shared/requireNonNull.js';
import { EquipmentType } from '../model/EquipmentType.js';
import type { RentalRequest } from '../model/RentalRequest.js';
import { PriceBreakdown } from './PriceBreakdown.js';

export class RentalPricing {
  private static readonly LONG_RENTAL_DAYS = 7;
  private static readonly INSURANCE_DAILY_RATE = new Decimal('8.00');
  private static readonly DELIVERY_FEE = new Decimal('25.00');
  private static readonly VAT_RATE = new Decimal('0.23');
  private static readonly ZERO_MONEY = new Decimal('0.00');

  private readonly dailyRates: ReadonlyMap<EquipmentType, Decimal>;
  private readonly longRentalDiscountRate: Decimal;

  constructor(
    dailyRates: ReadonlyMap<EquipmentType, Decimal>,
    longRentalDiscountRate: Decimal,
  ) {
    requireNonNull(dailyRates, 'dailyRates');
    requireNonNull(longRentalDiscountRate, 'longRentalDiscountRate');

    if (longRentalDiscountRate.lt(0) || longRentalDiscountRate.gt(1)) {
      throw new IllegalArgumentError('Discount rate must be between zero and one');
    }

    const rates = new Map<EquipmentType, Decimal>();
    for (const type of Object.values(EquipmentType)) {
      const rate = dailyRates.get(type);
      if (rate === undefined) {
        throw new IllegalArgumentError(`Missing daily rate for ${type}`);
      }
      const normalizedRate = RentalPricing.money(rate);
      if (normalizedRate.lte(0)) {
        throw new IllegalArgumentError(`Daily rate must be positive for ${type}`);
      }
      rates.set(type, normalizedRate);
    }

    // Kopia obronna - zmiany mapy wywołującego nie wpływają na cennik.
    this.dailyRates = rates;
    this.longRentalDiscountRate = longRentalDiscountRate;
  }

  static standard(): RentalPricing {
    return new RentalPricing(
      new Map([
        [EquipmentType.DRILL, new Decimal('39.99')],
        [EquipmentType.GENERATOR, new Decimal('120.00')],
      ]),
      new Decimal('0.10'),
    );
  }

  calculate(request: RentalRequest): PriceBreakdown {
    requireNonNull(request, 'request');

    const baseRentalCost = this.calculateBaseRentalCost(request);
    const discount = this.calculateDiscount(request, baseRentalCost);
    const insuranceCost = RentalPricing.calculateInsuranceCost(request);
    const deliveryCost = RentalPricing.calculateDeliveryCost(request);
    const netAmount = RentalPricing.money(
      baseRentalCost.minus(discount).plus(insuranceCost).plus(deliveryCost),
    );
    const vat = RentalPricing.money(netAmount.times(RentalPricing.VAT_RATE));
    const total = RentalPricing.money(netAmount.plus(vat));

    return new PriceBreakdown(
      baseRentalCost,
      discount,
      insuranceCost,
      deliveryCost,
      netAmount,
      vat,
      total,
    );
  }

  private calculateBaseRentalCost(request: RentalRequest): Decimal {
    // Konstruktor gwarantuje stawkę dla każdego typu sprzętu.
    const dailyRate = requireNonNull(this.dailyRates.get(request.equipmentType));
    return RentalPricing.money(dailyRate.times(request.days));
  }

  private calculateDiscount(request: RentalRequest, baseRentalCost: Decimal): Decimal {
    if (!RentalPricing.qualifiesForLongRentalDiscount(request)) {
      return RentalPricing.ZERO_MONEY;
    }
    return RentalPricing.money(baseRentalCost.times(this.longRentalDiscountRate));
  }

  private static qualifiesForLongRentalDiscount(request: RentalRequest): boolean {
    return request.days >= RentalPricing.LONG_RENTAL_DAYS;
  }

  private static calculateInsuranceCost(request: RentalRequest): Decimal {
    if (!request.insurance) {
      return RentalPricing.ZERO_MONEY;
    }
    return RentalPricing.money(RentalPricing.INSURANCE_DAILY_RATE.times(request.days));
  }

  private static calculateDeliveryCost(request: RentalRequest): Decimal {
    return request.delivery ? RentalPricing.DELIVERY_FEE : RentalPricing.ZERO_MONEY;
  }

  private static money(amount: Decimal): Decimal {
    return amount.toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }
}
