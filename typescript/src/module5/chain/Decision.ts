import { requireNonNull } from '../../shared/requireNonNull.js';

// Odpowiednik zagnieżdżonego enuma Decision.Approver.
export enum Approver {
  AUTOMATIC = 'AUTOMATIC',
  TEAM_LEAD = 'TEAM_LEAD',
  RELEASE_MANAGER = 'RELEASE_MANAGER',
  CHANGE_ADVISORY_BOARD = 'CHANGE_ADVISORY_BOARD',
  REJECTED = 'REJECTED',
}

export class Decision {
  constructor(
    readonly approver: Approver,
    readonly reason: string,
  ) {
    requireNonNull(approver, 'approver');
    requireNonNull(reason, 'reason');
  }

  static of(approver: Approver, reason: string): Decision {
    return new Decision(approver, reason);
  }

  // Odpowiednik equals() rekordu Javy.
  equals(other: unknown): boolean {
    return other instanceof Decision && this.approver === other.approver && this.reason === other.reason;
  }

  toString(): string {
    return `Decision[approver=${this.approver}, reason=${this.reason}]`;
  }
}
