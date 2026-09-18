import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import { addExact } from '../../support.js';

// Odpowiednik klasy-kontenera LegacyPlanNodes z typami zagnieżdżonymi:
// importuj jako `import * as LegacyPlanNodes from '.../LegacyPlanNodes.js'`.

export interface PlanNode {
  totalMinutes(): number;
}

export class TaskNode implements PlanNode {
  constructor(readonly minutes: number) {
    if (minutes < 0) {
      throw new IllegalArgumentError('minutes must not be negative');
    }
  }

  totalMinutes(): number {
    return this.minutes;
  }
}

// Zduplikowane przechowywanie dzieci i sumowanie — kandydat do Extract Composite.
export class ReleaseGroup implements PlanNode {
  private readonly nodes: PlanNode[] = [];

  add(child: PlanNode): void {
    this.nodes.push(requireNonNull(child, 'child'));
  }

  children(): readonly PlanNode[] {
    return Object.freeze([...this.nodes]);
  }

  totalMinutes(): number {
    let total = 0;
    for (const child of this.nodes) {
      total = addExact(total, child.totalMinutes());
    }
    return total;
  }
}

export class RollbackGroup implements PlanNode {
  private readonly nodes: PlanNode[] = [];

  add(child: PlanNode): void {
    this.nodes.push(requireNonNull(child, 'child'));
  }

  children(): readonly PlanNode[] {
    return Object.freeze([...this.nodes]);
  }

  totalMinutes(): number {
    let total = 0;
    for (const child of this.nodes) {
      total = addExact(total, child.totalMinutes());
    }
    return total;
  }
}
