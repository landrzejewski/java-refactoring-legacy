import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { DeploymentTask } from './DeploymentTask.js';
import type { PlanComponent } from './PlanComponent.js';

export class PlanExecutor {
  execute(component: PlanComponent): readonly string[] {
    requireNonNull(component, 'component');
    const tasks: DeploymentTask[] = [];
    component.collectTasks(tasks);
    return tasks.map(task => 'executed:' + task.name);
  }
}
