import { IllegalArgumentError, IllegalStateError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import { isBlank } from '../../support.js';
import { DeploymentGroup } from './DeploymentGroup.js';
import { DeploymentTask } from './DeploymentTask.js';
import type { PlanComponent } from './PlanComponent.js';

export class PlanBuilder {
  private readonly name: string;
  private readonly components: PlanComponent[] = [];
  private built = false;

  private constructor(name: string) {
    if (isBlank(name)) {
      throw new IllegalArgumentError('name must not be blank');
    }
    this.name = name;
  }

  static group(name: string): PlanBuilder {
    return new PlanBuilder(name);
  }

  task(name: string, minutes: number): this {
    this.ensureOpen();
    this.components.push(new DeploymentTask(name, minutes));
    return this;
  }

  group(name: string, definition: (builder: PlanBuilder) => unknown): this {
    this.ensureOpen();
    const child = PlanBuilder.group(name);
    requireNonNull(definition, 'definition')(child);
    const builtChild = child.build();
    this.ensureOpen();
    this.components.push(builtChild);
    return this;
  }

  add(component: PlanComponent): this {
    this.ensureOpen();
    this.components.push(requireNonNull(component, 'component'));
    return this;
  }

  build(): DeploymentGroup {
    this.ensureOpen();
    this.built = true;
    return new DeploymentGroup(this.name, this.components);
  }

  private ensureOpen(): void {
    if (this.built) {
      throw new IllegalStateError('builder has already been used');
    }
  }
}
