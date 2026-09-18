import type { LocalDate } from './LocalDate.js';

export class Subscription {
  constructor(
    readonly email: string,
    readonly renewalDate: LocalDate,
  ) {}
}
