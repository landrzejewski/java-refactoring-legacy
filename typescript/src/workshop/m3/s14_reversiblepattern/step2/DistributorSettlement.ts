import type { Decimal } from 'decimal.js';

import { IllegalArgumentError } from '../../../../shared/errors.js';
import type { Deal } from '../Deal.js';
import { PercentageModel } from './PercentageModel.js';
import type { SettlementModel } from './SettlementModel.js';

/**
 * Krok 2: wariant znika - umowy festiwalowe wygasły. Safe Delete FestivalFeeAdapter
 * i wpisu w mapie. Zostaje Strategy z JEDNĄ implementacją: sygnał nadmiaru wzorca.
 * (To zmiana zachowania: model FESTIVAL jest teraz odrzucany.)
 */
export class DistributorSettlement {
  private readonly models: ReadonlyMap<string, SettlementModel> = new Map<string, SettlementModel>([
    ['PERCENT', new PercentageModel()],
  ]);

  payout(deal: Deal, week: number, ticketRevenue: Decimal): Decimal {
    const model = this.models.get(deal.model);
    if (model === undefined) {
      throw new IllegalArgumentError(`nieznany model: ${deal.model}`);
    }
    return model.payout(deal, week, ticketRevenue);
  }
}
