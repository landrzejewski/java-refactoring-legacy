import { Contracts } from './Contracts.js';

export class DeploymentCapacity {
  private readonly totalSlots: number;
  #remaining: number;

  constructor(totalSlots: number) {
    Contracts.require(
      totalSlots >= 0,
      'totalSlots must not be negative');
    this.totalSlots = totalSlots;
    this.#remaining = totalSlots;
    this.checkInvariant();
    Contracts.ensure(
      this.#remaining === totalSlots,
      'initial capacity must equal totalSlots');
  }

  reserve(slots: number): void {
    Contracts.require(slots > 0, 'slots must be positive');
    Contracts.require(
      slots <= this.#remaining,
      'cannot reserve more slots than remain');

    const previousRemaining = this.#remaining;
    const nextRemaining = previousRemaining - slots;

    this.checkInvariant(nextRemaining);
    this.#remaining = nextRemaining;
    Contracts.ensure(
      this.#remaining === previousRemaining - slots,
      'reserve must reduce remaining capacity by slots');
    this.checkInvariant();
  }

  release(slots: number): void {
    Contracts.require(slots > 0, 'slots must be positive');
    Contracts.require(
      slots <= this.totalSlots - this.#remaining,
      'cannot release more slots than are reserved');

    const previousRemaining = this.#remaining;
    const nextRemaining = previousRemaining + slots;

    this.checkInvariant(nextRemaining);
    this.#remaining = nextRemaining;
    Contracts.ensure(
      this.#remaining === previousRemaining + slots,
      'release must increase remaining capacity by slots');
    this.checkInvariant();
  }

  remaining(): number {
    this.checkInvariant();
    return this.#remaining;
  }

  private checkInvariant(candidateRemaining: number = this.#remaining): void {
    Contracts.invariant(
      candidateRemaining >= 0 && candidateRemaining <= this.totalSlots,
      'remaining capacity must be between zero and totalSlots');
  }
}
