import { afterEach, describe, expect, it, vi } from 'vitest';
import { Module8Examples } from '../../src/module8/Module8Examples.js';

describe('Module8ExamplesTest', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('runsOneDeterministicExampleForEveryTopic', () => {
    expect(Module8Examples.runExamples()).toEqual([
      'Stopniowa migracja: 53.97/Agreement',
      'Boy Scout: Successful: 1/2',
      'Code review: ready=true',
      'Dokumentowanie: # ADR-0042: Use Branch by Abstraction',
      'Narzędzia: compiled=true',
      'Zarządzanie ryzykiem: ADVANCE',
    ]);
  });

  it('returnedResultsCannotBeModified', () => {
    const results = Module8Examples.runExamples();

    expect(() => (results as string[]).push('unexpected')).toThrow(TypeError);
  });

  it('mainPrintsEveryResultOnItsOwnLine', () => {
    const lines: string[] = [];
    vi.spyOn(console, 'log').mockImplementation((...args: unknown[]) => {
      lines.push(args.map(String).join(' '));
    });

    expect(() => Module8Examples.main([])).not.toThrow();

    const expected = Module8Examples.runExamples().join('\n') + '\n';
    expect(lines.map((line) => line + '\n').join('')).toBe(expected);
  });
});
