import { describe, expect, it } from 'vitest';
import { ArithmeticError, IllegalArgumentError, IllegalStateError } from '../../src/shared/errors.js';
import { DeploymentGroup } from '../../src/module6/composite/after/DeploymentGroup.js';
import { DeploymentTask } from '../../src/module6/composite/after/DeploymentTask.js';
import { LegacyPathPlanMapper } from '../../src/module6/composite/after/LegacyPathPlanMapper.js';
import { PlanBuilder } from '../../src/module6/composite/after/PlanBuilder.js';
import type { PlanComponent } from '../../src/module6/composite/after/PlanComponent.js';
import { PlanExecutor } from '../../src/module6/composite/after/PlanExecutor.js';
import { TotalMinutesVisitor } from '../../src/module6/composite/after/TotalMinutesVisitor.js';
import { Entry, LegacyPathPlan } from '../../src/module6/composite/before/LegacyPathPlan.js';
import { LegacyPlanExecutor, Task } from '../../src/module6/composite/before/LegacyPlanExecutor.js';

function samplePlan(): DeploymentGroup {
  return PlanBuilder.group('release')
    .group('database', group => group.task('backup', 5).task('migrate', 8))
    .task('deploy', 3)
    .build();
}

describe('CompositeRefactoringsTest', () => {
  it('explicitCompositePreservesTheImplicitPathTree', () => {
    const entries = [
      new Entry('release/database/backup', 5),
      new Entry('release/database/migrate', 8),
      new Entry('release/deploy', 3),
    ];
    const legacy = new LegacyPathPlan(entries);
    const plan = new LegacyPathPlanMapper().map('release', entries);
    const tasks: DeploymentTask[] = [];
    plan.collectTasks(tasks);

    expect(legacy.totalMinutes()).toBe(16);
    expect(plan.totalMinutes()).toBe(16);
    expect(tasks.map(task => task.name)).toEqual(['backup', 'migrate', 'deploy']);
    expect(plan.totalMinutes()).toBe(legacy.totalMinutes());
    expect(tasks.map(task => task.name)).toEqual(legacy.taskNamesBelow('release'));
    expect(plan.components.map(component => component.name)).toEqual(['database', 'deploy']);
    const database = plan.components[0];
    expect(database).toBeInstanceOf(DeploymentGroup);
    if (!(database instanceof DeploymentGroup)) {
      throw new Error('unreachable');
    }
    expect(database.components.map(component => component.name)).toEqual(['backup', 'migrate']);
    const databaseTasks: DeploymentTask[] = [];
    database.collectTasks(databaseTasks);
    expect(databaseTasks.map(task => task.name)).toEqual(legacy.taskNamesBelow('release/database'));
  });

  it('pathMapperRejectsAmbiguousOrOrderChangingInput', () => {
    const mapper = new LegacyPathPlanMapper();

    expect(() => new Entry('release/ /task', 1)).toThrow(IllegalArgumentError);
    expect(() =>
      mapper.map('release', [new Entry('release/database', 1), new Entry('release/database/migrate', 8)]),
    ).toThrow(IllegalArgumentError);
    expect(() =>
      mapper.map('release', [
        new Entry('release/a/first', 1),
        new Entry('release/b/second', 1),
        new Entry('release/a/third', 1),
      ]),
    ).toThrow(IllegalArgumentError);
    expect(() => mapper.map('release', [new Entry('other/deploy', 1)])).toThrow(IllegalArgumentError);
  });

  it('collectingParameterAndVisitorAccumulateTheSameTree', () => {
    const plan = samplePlan();
    const tasks: DeploymentTask[] = [new DeploymentTask('already-collected', 1)];

    plan.collectTasks(tasks);

    expect(tasks.map(task => task.name)).toEqual(['already-collected', 'backup', 'migrate', 'deploy']);
    expect(plan.accept(new TotalMinutesVisitor())).toBe(plan.totalMinutes());
  });

  it('oneContractHandlesOneTaskAndAGroup', () => {
    const before = new LegacyPlanExecutor();
    const after = new PlanExecutor();
    const firstBefore = new Task('backup');
    const secondBefore = new Task('migrate');
    const firstAfter = new DeploymentTask('backup', 5);
    const secondAfter = new DeploymentTask('migrate', 8);

    expect(after.execute(firstAfter)).toEqual(before.execute(firstBefore));
    expect(after.execute(new DeploymentGroup('database', [firstAfter, secondAfter]))).toEqual(
      before.executeAll([firstBefore, secondBefore]),
    );
  });

  it('builderIsSingleUseAndBuiltCompositeIsImmutable', () => {
    const builder = PlanBuilder.group('release').task('deploy', 3);
    const plan = builder.build();

    expect(() => builder.task('verify', 2)).toThrow(IllegalStateError);
    expect(() => builder.task(' ', -1)).toThrow(IllegalStateError);
    // Zamrożona tablica w trybie strict ESM rzuca TypeError (odpowiednik UnsupportedOperationException).
    expect(() => (plan.components as PlanComponent[]).push(new DeploymentTask('verify', 2))).toThrow(
      TypeError,
    );
  });

  it('bothAccumulationImplementationsDetectOverflow', () => {
    // Long.MAX_VALUE → Number.MAX_SAFE_INTEGER (granica dokładnej arytmetyki całkowitej w JS).
    const overflowing: PlanComponent = new DeploymentGroup('release', [
      new DeploymentTask('first', Number.MAX_SAFE_INTEGER),
      new DeploymentTask('second', 1),
    ]);

    expect(() => overflowing.totalMinutes()).toThrow(ArithmeticError);
    expect(() => overflowing.accept(new TotalMinutesVisitor())).toThrow(ArithmeticError);
  });

  it('emptyCompositeHasNeutralBehavior', () => {
    const empty = PlanBuilder.group('empty').build();
    const mappedEmpty = new LegacyPathPlanMapper().map('empty', []);

    expect(empty.totalMinutes()).toBe(0);
    expect(mappedEmpty).toEqual(empty);
    expect(empty.accept(new TotalMinutesVisitor())).toBe(0);
    expect(new PlanExecutor().execute(empty)).toEqual([]);
  });
});
