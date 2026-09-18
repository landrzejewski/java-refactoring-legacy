import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import { addExact } from '../../support.js';

// Odpowiednik klasy-kontenera PlanNodes z typami zagnieżdżonymi:
// importuj jako `import * as PlanNodes from '.../PlanNodes.js'`.

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

// Wyodrębniona nadklasa kompozytu. W Javie metody są `final`;
// TS nie ma `final`, więc podklasy po prostu ich nie nadpisują.
export abstract class CompositePlanNode implements PlanNode {
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

export class ReleaseGroup extends CompositePlanNode {}

export class RollbackGroup extends CompositePlanNode {}
