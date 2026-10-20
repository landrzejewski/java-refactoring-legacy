import type { DistributorFeed } from '../DistributorFeed.js';
import { SettlementService } from './SettlementService.js';

/** Stabilny punkt wejścia sceny (korzeń kompozycji). Krok 1: bez zmian. */
export class SettlementModule {
  private constructor() {}

  static settlementService(feed: DistributorFeed): SettlementService {
    return new SettlementService(feed);
  }
}
