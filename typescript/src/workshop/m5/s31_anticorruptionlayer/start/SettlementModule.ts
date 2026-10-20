import type { DistributorFeed } from '../DistributorFeed.js';
import { SettlementService } from './SettlementService.js';

/** Stabilny punkt wejścia sceny (korzeń kompozycji). Start: serwis dostaje klienta dystrybutora wprost. */
export class SettlementModule {
  private constructor() {}

  static settlementService(feed: DistributorFeed): SettlementService {
    return new SettlementService(feed);
  }
}
