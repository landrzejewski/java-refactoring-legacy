import { describe, expect, it } from 'vitest';

import { NullPointerError } from '../../../../src/shared/errors.js';
import type { CompositeProgramItem } from '../../../../src/workshop/m6/s13_extractcomposite/step2/CompositeProgramItem.js';
import { Marathon } from '../../../../src/workshop/m6/s13_extractcomposite/step2/Marathon.js';
import type { ProgramItem } from '../../../../src/workshop/m6/s13_extractcomposite/step2/ProgramItem.js';
import { ShortsBlock } from '../../../../src/workshop/m6/s13_extractcomposite/step2/ShortsBlock.js';

/** Wspólna nadklasa pilnuje kontraktu dzieci raz dla wszystkich kontenerów. */
describe('S13SolutionTest', () => {
  it('everyContainerRejectsNullChild', () => {
    const containers: CompositeProgramItem[] = [new Marathon('M'), new ShortsBlock('B')];
    for (const container of containers) {
      expect(() => container.add(null as unknown as ProgramItem)).toThrow(NullPointerError);
    }
  });

  it('childrenViewIsACopy', () => {
    const marathon = new Marathon('M');
    expect(() => (marathon.children() as ProgramItem[]).push(marathon)).toThrow(TypeError);
    expect(marathon.minutes()).toBe(0);
  });
});
