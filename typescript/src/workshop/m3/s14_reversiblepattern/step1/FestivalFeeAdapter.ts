import { Decimal } from 'decimal.js';

import type { Deal } from '../Deal.js';
import type { FestivalTariffClient } from '../FestivalTariffClient.js';
import type { SettlementModel } from './SettlementModel.js';

/**
 * Krok 1: Adapter - tłumaczy obcy interfejs (grosze jako liczba całkowita) na kontrakt
 * `SettlementModel`. Ma realną pracę: konwersję jednostek. Przychód z biletów
 * ignoruje - w tym modelu opłata nie zależy od sprzedaży.
 */
export class FestivalFeeAdapter implements SettlementModel {
  constructor(private readonly client: FestivalTariffClient) {}

  payout(deal: Deal, week: number, _ticketRevenue: Decimal): Decimal {
    return new Decimal(this.client.weeklyFeeInCents(deal.title, week)).dividedBy(100);
  }
}
