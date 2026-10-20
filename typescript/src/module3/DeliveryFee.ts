export class DeliveryFee {
  private constructor() {}

  static fee(premium: boolean): number {
    let fee = 100;

    if (premium) {
      fee = 0;
    }

    return fee;
  }
}
