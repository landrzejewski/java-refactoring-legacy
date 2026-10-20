import type { Decimal } from 'decimal.js';

import { IllegalArgumentError } from '../../../../shared/errors.js';
import type { Deal } from '../Deal.js';
import { FestivalTariffClient } from '../FestivalTariffClient.js';
import { FestivalFeeAdapter } from './FestivalFeeAdapter.js';
import { PercentageModel } from './PercentageModel.js';
import type { SettlementModel } from './SettlementModel.js';

/**
 * Krok 1: Replace Conditional with Strategy. Rozliczenie tylko wybiera model;
 * każdy wariant zmienia się we własnej klasie. Uzasadnienie: dwa ISTNIEJĄCE warianty
 * z różnymi właścicielami i obcy interfejs festiwalu - nie przyszłe pluginy.
 */
export class DistributorSettlement {
  private readonly models: ReadonlyMap<string, SettlementModel> = new Map<string, SettlementModel>([
    ['PERCENT', new PercentageModel()],
    ['FESTIVAL', new FestivalFeeAdapter(new FestivalTariffClient())],
  ]);

  payout(deal: Deal, week: number, ticketRevenue: Decimal): Decimal {
    const model = this.models.get(deal.model);
    if (model === undefined) {
      throw new IllegalArgumentError(`nieznany model: ${deal.model}`);
    }
    return model.payout(deal, week, ticketRevenue);
  }
}
