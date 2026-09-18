import { IllegalArgumentError } from '../../../../shared/errors.js';

/** Java: enum MovieType with fields and behaviour -> class with static instances. */
export class MovieType {
  static readonly REGULAR = new MovieType('REGULAR', 2, 2, 1.5);
  static readonly CHILDREN = new MovieType('CHILDREN', 3, 1.5, 1.5);
  static readonly NEW_RELEASE = new MovieType('NEW_RELEASE', 0, 0, 3);

  private constructor(
    readonly name: string,
    private readonly freeRentalPeriodInDays: number,
    private readonly initialCost: number,
    private readonly costPerDay: number,
  ) {}

  static values(): readonly MovieType[] {
    return [MovieType.REGULAR, MovieType.CHILDREN, MovieType.NEW_RELEASE];
  }

  // Java: package-private
  getValueFor(periodInDays: number): number {
    if (periodInDays < 0) {
      throw new IllegalArgumentError(
        'periodInDays must not be negative');
    }
    return this.getInitialCost() + this.getValueForPeriod(periodInDays);
  }

  private getValueForPeriod(periodInDays: number): number {
    const paidDays = Math.max(
      0, periodInDays - this.getFreeRentalPeriodInDays());
    return paidDays * this.getCostPerDay();
  }

  getFreeRentalPeriodInDays(): number {
    return this.freeRentalPeriodInDays;
  }

  getInitialCost(): number {
    return this.initialCost;
  }

  getCostPerDay(): number {
    return this.costPerDay;
  }

  toString(): string {
    return this.name;
  }
}
