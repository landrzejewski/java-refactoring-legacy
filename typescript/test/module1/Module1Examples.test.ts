import { describe, expect, it } from 'vitest';
import { Module1Examples } from '../../src/module1/Module1Examples.js';

describe('Module1ExamplesTest', () => {
  it('runsAllModuleExamples', () => {
    expect(() => Module1Examples.main([])).not.toThrow();
  });
});
