import type { DeploymentGroup } from './DeploymentGroup.js';
import type { DeploymentTask } from './DeploymentTask.js';
import type { PlanVisitor } from './PlanVisitor.js';

// Kontrakt wspólny dla liścia i kompozytu.
export interface PlanComponentContract {
  readonly name: string;

  totalMinutes(): number;

  // Collecting Parameter — odpowiednik Collection<? super DeploymentTask>.
  collectTasks(target: TaskCollector): void;

  accept<R>(visitor: PlanVisitor<R>): R;
}

export interface TaskCollector {
  push(task: DeploymentTask): unknown;
}

// Odpowiednik `sealed interface PlanComponent permits DeploymentTask, DeploymentGroup`:
// zamknięta unia dyskryminowana polem `kind`.
export type PlanComponent = DeploymentTask | DeploymentGroup;
