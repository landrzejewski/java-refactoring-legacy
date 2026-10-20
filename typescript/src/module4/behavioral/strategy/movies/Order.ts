import type { MovieType } from './MovieType.js';

export class Order {
  constructor(private readonly movieType: MovieType) {}

  getTotalValue(periodInDays: number): number {
    return this.movieType.getValueFor(periodInDays);
  }
}
