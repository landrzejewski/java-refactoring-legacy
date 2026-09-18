import { afterEach, describe, expect, it, vi } from 'vitest';
import { examples, main } from '../../src/patterns/main.js';
import { SystemInReader } from '../../src/patterns/structural/decorator/SystemInReader.js';

const EXPECTED_KEYS = [
  'abstract-factory', 'builder', 'factory-method', 'prototype', 'singleton',
  'adapter', 'composite', 'decorator', 'facade', 'flyweight', 'proxy',
  'chain-of-responsibility', 'command', 'interpreter', 'iterator', 'memento', 'observer',
  'state', 'strategy', 'template-method', 'visitor',
  'fn-chain', 'fn-command', 'fn-decorator', 'fn-factory', 'fn-iterator', 'fn-memento',
  'fn-observer', 'fn-singleton', 'fn-state', 'fn-strategy', 'fn-template',
];

interface Captured {
  readonly exitCode: number;
  readonly out: string;
  readonly err: string;
}

function capture(args: readonly string[]): Captured {
  const out: string[] = [];
  const err: string[] = [];
  const logSpy = vi.spyOn(console, 'log').mockImplementation((...parts: unknown[]) => {
    out.push(parts.map(String).join(' '));
  });
  const errorSpy = vi.spyOn(console, 'error').mockImplementation((...parts: unknown[]) => {
    err.push(parts.map(String).join(' '));
  });
  try {
    return { exitCode: main(args), out: out.join('\n'), err: err.join('\n') };
  } finally {
    logSpy.mockRestore();
    errorSpy.mockRestore();
  }
}

describe('PatternsSmoke', () => {
  const previousStdinEnabled = SystemInReader.stdinEnabled;

  afterEach(() => {
    SystemInReader.stdinEnabled = previousStdinEnabled;
    vi.restoreAllMocks();
  });

  it('registersTheSameKeysAsTheCSharpDispatcher', () => {
    expect(examples.map((example) => example.key)).toEqual(EXPECTED_KEYS);
  });

  it.each(EXPECTED_KEYS)('runsExample %s', (key) => {
    SystemInReader.stdinEnabled = false; // never read stdin in tests
    const result = capture([key]);
    expect(result.exitCode).toBe(0);
    expect(`${result.out}${result.err}`.length).toBeGreaterThan(0);
  });

  it('decoratorUsesTheDefaultTextWithoutStdin', () => {
    SystemInReader.stdinEnabled = false;
    expect(capture(['decorator']).err).toContain('INFO: hello_decorator_pattern');
  });

  it('runsAllExamplesWithHeaders', () => {
    const result = capture(['all']);
    expect(result.exitCode).toBe(0);
    for (const key of EXPECTED_KEYS) {
      expect(result.out).toContain(`=== ${key} ===`);
    }
  });

  it('printsTheListWithoutArguments', () => {
    const result = capture([]);
    expect(result.exitCode).toBe(0);
    expect(result.out).toContain('fn-template');
  });

  it('rejectsAnUnknownKey', () => {
    const result = capture(['no-such-pattern']);
    expect(result.exitCode).toBe(1);
    expect(result.err).toContain('Unknown example: no-such-pattern');
    expect(result.err).toContain('builder');
  });
});
