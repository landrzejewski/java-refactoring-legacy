export class LegacyDeploymentCapacity {
  #remaining: number;

  constructor(totalSlots: number) {
    this.#remaining = totalSlots;
  }

  reserve(slots: number): void {
    this.#remaining -= slots;
  }

  release(slots: number): void {
    this.#remaining += slots;
  }

  remaining(): number {
    return this.#remaining;
  }
}
