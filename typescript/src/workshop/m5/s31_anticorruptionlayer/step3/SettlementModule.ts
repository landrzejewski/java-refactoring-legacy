import type { DistributorFeed } from '../DistributorFeed.js';
import { DistributorAcl } from './DistributorAcl.js';
import { SettlementService } from './SettlementService.js';

/** Stabilny punkt wejścia sceny (korzeń kompozycji). Krok 3: domena dostaje licencje przez ACL. */
export class SettlementModule {
  private constructor() {}

  static settlementService(feed: DistributorFeed): SettlementService {
    return new SettlementService(new DistributorAcl(feed));
  }
}
