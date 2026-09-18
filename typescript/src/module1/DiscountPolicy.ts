export class DiscountPolicy {
  private constructor() {}

  static discountPercent(orderValue: number, vip: boolean): number {
    let discount = 0;

    if (orderValue >= 100) {
      discount += 10;
    }

    if (vip) {
      discount += 5;
    }

    return discount;
  }
}
