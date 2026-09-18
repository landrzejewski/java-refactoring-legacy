import { afterEach, describe, expect, it, vi } from 'vitest';
import { main, runExamples } from '../../src/module7/Module7Examples.js';

describe('Module7ExamplesTest', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('runsOneDeterministicExampleForEveryTopic', () => {
    expect(runExamples()).toEqual([
      'Break Dependencies: ALLOWED',
      'Extract Method Object: 45/MEDIUM',
      'Break Responsibilities: deployments=3;failures=1;'
        + 'avgLeadTimeMinutes=18',
      'Remove Duplication: image:42',
      'Break Method: 10|api|sha-api,20|worker|sha-worker',
      'Introduce Parameter Object: 165s',
      'Remove Arrowhead: ELIGIBLE',
      'Design by Contract: remaining=7',
      'Remove Double Negative: true',
      'Remove God Class: rel-42/1/1/1',
      'Remove Boolean Parameter: deployed:rel-42',
      'Remove Middle Man: dep-42 -> RUNNING',
      'Return ASAP: api.jar']);
  });

  it('returnedResultsCannotBeModified', () => {
    const results = runExamples();

    expect(() => (results as string[]).push('unexpected')).toThrow(TypeError);
  });

  it('mainPrintsEveryResultOnItsOwnLine', () => {
    const lines: string[] = [];
    vi.spyOn(console, 'log').mockImplementation((line: string) => {
      lines.push(line + '\n');
    });

    expect(() => main()).not.toThrow();

    const expected = runExamples().join('\n') + '\n';
    expect(lines.join('')).toBe(expected);
  });
});
