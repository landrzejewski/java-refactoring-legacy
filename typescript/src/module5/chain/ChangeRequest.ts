import { IllegalArgumentError } from '../../shared/errors.js';
import { requireNonNull } from '../../shared/requireNonNull.js';
import { isBlank } from '../support.js';

// Odpowiednik zagnieżdżonego enuma ChangeRequest.Risk.
export enum Risk {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
}

export class ChangeRequest {
  constructor(
    readonly id: string,
    readonly linesChanged: number,
    readonly risk: Risk,
    readonly production: boolean,
  ) {
    if (isBlank(id)) {
      throw new IllegalArgumentError('id must not be blank');
    }
    if (linesChanged < 0) {
      throw new IllegalArgumentError('linesChanged must not be negative');
    }
    requireNonNull(risk, 'risk');
  }

  // Odpowiednik toString() rekordu Javy.
  toString(): string {
    return `ChangeRequest[id=${this.id}, linesChanged=${this.linesChanged}, risk=${this.risk}, production=${this.production}]`;
  }
}
