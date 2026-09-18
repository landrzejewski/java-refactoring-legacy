import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import { addExact, isBlank } from '../../support.js';
import type { PlanComponent, PlanComponentContract, TaskCollector } from './PlanComponent.js';
import type { PlanVisitor } from './PlanVisitor.js';

export class DeploymentGroup implements PlanComponentContract {
  readonly kind = 'group';
  readonly components: readonly PlanComponent[];

  constructor(
    readonly name: string,
    components: readonly PlanComponent[],
  ) {
    if (isBlank(name)) {
      throw new IllegalArgumentError('name must not be blank');
    }
    // List.copyOf — niemodyfikowalna kopia.
    this.components = Object.freeze([...requireNonNull(components, 'components')]);
  }

  totalMinutes(): number {
    let total = 0;
    for (const component of this.components) {
      total = addExact(total, component.totalMinutes());
    }
    return total;
  }

  collectTasks(target: TaskCollector): void {
    requireNonNull(target, 'target');
    for (const component of this.components) {
      component.collectTasks(target);
    }
  }

  accept<R>(visitor: PlanVisitor<R>): R {
    return requireNonNull(visitor, 'visitor').visitGroup(this);
  }
}
