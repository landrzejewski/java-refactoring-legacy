import { afterEach, describe, expect, it, vi } from 'vitest';
import { Module4Examples } from '../../src/module4/Module4Examples.js';

describe('Module4ExamplesTest', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('runsAllModuleExamples', () => {
    vi.spyOn(process.stdout, 'write').mockImplementation(() => true);
    vi.spyOn(console, 'log').mockImplementation(() => {});
    expect(() => Module4Examples.main([])).not.toThrow();
  });
});
