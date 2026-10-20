import { addExact } from '../../support.js';
import type { DeploymentGroup } from './DeploymentGroup.js';
import type { DeploymentTask } from './DeploymentTask.js';
import type { PlanVisitor } from './PlanVisitor.js';

export class TotalMinutesVisitor implements PlanVisitor<number> {
  visitTask(task: DeploymentTask): number {
    return task.minutes;
  }

  visitGroup(group: DeploymentGroup): number {
    let total = 0;
    for (const component of group.components) {
      total = addExact(total, component.accept(this));
    }
    return total;
  }
}
