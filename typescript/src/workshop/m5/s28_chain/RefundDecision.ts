/** Stabilny kontrakt sceny: decyzja o zwrocie - kto zatwierdził albo dlaczego odrzucono. */
export class RefundDecision {
  constructor(
    readonly approved: boolean,
    readonly approver: string | null,
    readonly reason: string | null,
  ) {}

  static approvedBy(approver: string): RefundDecision {
    return new RefundDecision(true, approver, null);
  }

  static rejected(reason: string): RefundDecision {
    return new RefundDecision(false, null, reason);
  }

  toString(): string {
    return this.approved ? `APPROVED ${String(this.approver)}` : `REJECTED ${String(this.reason)}`;
  }
}
