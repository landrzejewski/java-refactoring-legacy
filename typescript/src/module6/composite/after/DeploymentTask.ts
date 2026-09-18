import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import { isBlank } from '../../support.js';
import type { PlanComponentContract, TaskCollector } from './PlanComponent.js';
import type { PlanVisitor } from './PlanVisitor.js';

export class DeploymentTask implements PlanComponentContract {
  readonly kind = 'task';

  constructor(
    readonly name: string,
    readonly minutes: number,
  ) {
    if (isBlank(name)) {
      throw new IllegalArgumentError('name must not be blank');
    }
    if (minutes < 0) {
      throw new IllegalArgumentError('minutes must not be negative');
    }
  }

  totalMinutes(): number {
    return this.minutes;
  }

  collectTasks(target: TaskCollector): void {
    requireNonNull(target, 'target').push(this);
  }

  accept<R>(visitor: PlanVisitor<R>): R {
    return requireNonNull(visitor, 'visitor').visitTask(this);
  }
}
