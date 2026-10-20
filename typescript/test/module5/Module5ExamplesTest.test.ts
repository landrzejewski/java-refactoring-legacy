import { afterEach, describe, expect, it, vi } from 'vitest';
import { main } from '../../src/module5/main.js';

describe('Module5ExamplesTest', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('runsAllModuleExamples', () => {
    vi.spyOn(console, 'log').mockImplementation(() => {});

    expect(() => main()).not.toThrow();
  });
});
