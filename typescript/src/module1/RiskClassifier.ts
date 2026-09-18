import { Decimal } from 'decimal.js';

export class RiskClassifier {
  private constructor() {}

  static riskLevel(order: OrderSummary): number {
    let score = 0;

    if (order.total.comparedTo(new Decimal('1000.00')) > 0) {
      score++;
    }

    if (order.international) {
      score++;
    }

    for (const item of order.items) {
      if (item.fragile) {
        score++;
      }
    }

    return score;
  }
}

export class OrderSummary {
  constructor(
    readonly total: Decimal,
    readonly international: boolean,
    readonly items: readonly Item[],
  ) {}
}

export class Item {
  constructor(readonly fragile: boolean) {}
}
