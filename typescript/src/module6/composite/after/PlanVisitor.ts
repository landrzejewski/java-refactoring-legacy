import type { DeploymentGroup } from './DeploymentGroup.js';
import type { DeploymentTask } from './DeploymentTask.js';

export interface PlanVisitor<R> {
  visitTask(task: DeploymentTask): R;

  visitGroup(group: DeploymentGroup): R;
}
