import { afterEach, describe, expect, it, vi } from 'vitest';
import { Module5Examples } from '../../src/module5/Module5Examples.js';

describe('Module5ExamplesTest', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('runsAllModuleExamples', () => {
    vi.spyOn(console, 'log').mockImplementation(() => {});
    expect(() => Module5Examples.main([])).not.toThrow();
  });
});
