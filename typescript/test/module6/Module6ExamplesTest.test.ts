import { afterEach, describe, expect, it, vi } from 'vitest';
import { main } from '../../src/module6/main.js';

describe('Module6ExamplesTest', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('runsAllModuleExamples', () => {
    vi.spyOn(console, 'log').mockImplementation(() => {});

    expect(() => main()).not.toThrow();
  });
});
