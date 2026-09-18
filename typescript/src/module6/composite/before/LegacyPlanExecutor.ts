import { IllegalArgumentError } from '../../../shared/errors.js';
import { isBlank } from '../../support.js';

// Odpowiednik rekordu zagnieżdżonego LegacyPlanExecutor.Task.
export class Task {
  constructor(readonly name: string) {
    if (isBlank(name)) {
      throw new IllegalArgumentError('name must not be blank');
    }
  }
}

export class LegacyPlanExecutor {
  execute(task: Task): readonly string[] {
    return ['executed:' + task.name];
  }

  executeAll(tasks: readonly Task[]): readonly string[] {
    return tasks.map(task => 'executed:' + task.name);
  }
}
