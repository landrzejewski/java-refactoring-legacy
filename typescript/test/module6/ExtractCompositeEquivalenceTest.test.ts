import { describe, expect, it } from 'vitest';
import * as PlanNodes from '../../src/module6/extractcomposite/after/PlanNodes.js';
import * as LegacyPlanNodes from '../../src/module6/extractcomposite/before/LegacyPlanNodes.js';

describe('ExtractCompositeEquivalenceTest', () => {
  it('extractedSuperclassCentralizesChildStorageAndAccumulation', () => {
    const beforeRelease = new LegacyPlanNodes.ReleaseGroup();
    beforeRelease.add(new LegacyPlanNodes.TaskNode(5));
    beforeRelease.add(new LegacyPlanNodes.TaskNode(8));
    const afterRelease = new PlanNodes.ReleaseGroup();
    afterRelease.add(new PlanNodes.TaskNode(5));
    afterRelease.add(new PlanNodes.TaskNode(8));

    expect(afterRelease.totalMinutes()).toBe(beforeRelease.totalMinutes());
    expect(afterRelease.children().length).toBe(beforeRelease.children().length);
  });

  it('extractedCompositeDefensivelyExposesChildren', () => {
    const group = new PlanNodes.RollbackGroup();
    group.add(new PlanNodes.TaskNode(3));

    // children().clear() → splice(0) na zamrożonej kopii rzuca TypeError.
    expect(() => (group.children() as PlanNodes.PlanNode[]).splice(0)).toThrow(TypeError);
  });
});
