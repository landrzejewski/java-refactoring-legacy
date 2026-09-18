import { realpathSync } from 'node:fs';
import { pathToFileURL } from 'node:url';
import * as chainOfResponsibility from './behavioral/chainofresponsibility/Application.js';
import * as command from './behavioral/command/Application.js';
import * as interpreter from './behavioral/interpreter/Application.js';
import * as iterator from './behavioral/iterator/Application.js';
import * as memento from './behavioral/memento/Application.js';
import * as observer from './behavioral/observer/Application.js';
import * as state from './behavioral/state/Application.js';
import * as strategy from './behavioral/strategy/Application.js';
import * as templateMethod from './behavioral/templatemethod/Application.js';
import * as visitor from './behavioral/visitor/Application.js';
import * as abstractFactory from './creational/abstractfactory/Application.js';
import * as builder from './creational/builder/Application.js';
import * as factoryMethod from './creational/factorymethod/Application.js';
import * as prototype from './creational/prototype/Application.js';
import * as singleton from './creational/singleton/Application.js';
import * as fnChain from './fn/FunctionalStyleChain.js';
import * as fnCommand from './fn/FunctionalStyleCommand.js';
import * as fnDecorator from './fn/FunctionalStyleDecorator.js';
import * as fnFactory from './fn/FunctionalStyleFactory.js';
import * as fnIterator from './fn/FunctionalStyleIterator.js';
import * as fnMemento from './fn/FunctionalStyleMemento.js';
import * as fnObserver from './fn/FunctionalStyleObserver.js';
import * as fnSingleton from './fn/FunctionalStyleSingleton.js';
import * as fnState from './fn/FunctionalStyleState.js';
import * as fnStrategy from './fn/FunctionalStyleStrategy.js';
import * as fnTemplate from './fn/FunctionalStyleTemplate.js';
import * as adapter from './structural/adapter/Application.js';
import * as composite from './structural/composite/Application.js';
import * as decorator from './structural/decorator/Application.js';
import { SystemInReader } from './structural/decorator/SystemInReader.js';
import * as facade from './structural/facade/Application.js';
import * as flyweight from './structural/flyweight/Application.js';
import * as proxy from './structural/proxy/Application.js';

/**
 * Single entry point replacing the many Java main methods.
 *   npm run patterns                 - lists available example keys
 *   npm run patterns -- builder      - runs one example
 *   npm run patterns -- all          - runs every example in order
 * The decorator example reads one line from standard input (like Java's Scanner). When stdin is
 * a TTY or empty it falls back to a default text; in "all" mode stdin is never read, so the run does not block.
 */
export interface Example {
  readonly key: string;
  readonly javaClass: string;
  readonly run: () => void;
}

export const examples: readonly Example[] = Object.freeze([
  { key: 'abstract-factory', javaClass: 'creational.abstractfactory.Application', run: abstractFactory.run },
  { key: 'builder', javaClass: 'creational.builder.Application', run: builder.run },
  { key: 'factory-method', javaClass: 'creational.factorymethod.Application', run: factoryMethod.run },
  { key: 'prototype', javaClass: 'creational.prototype.Application', run: prototype.run },
  { key: 'singleton', javaClass: 'creational.singleton.Application', run: singleton.run },
  { key: 'adapter', javaClass: 'structural.adapter.Application', run: adapter.run },
  { key: 'composite', javaClass: 'structural.composite.Application', run: composite.run },
  { key: 'decorator', javaClass: 'structural.decorator.Application (reads a line from stdin)', run: decorator.run },
  { key: 'facade', javaClass: 'structural.facade.Application', run: facade.run },
  { key: 'flyweight', javaClass: 'structural.flyweight.Application', run: flyweight.run },
  { key: 'proxy', javaClass: 'structural.proxy.Application', run: proxy.run },
  { key: 'chain-of-responsibility', javaClass: 'behavioral.chainofresponsibility.Application', run: chainOfResponsibility.run },
  { key: 'command', javaClass: 'behavioral.command.Application', run: command.run },
  { key: 'interpreter', javaClass: 'behavioral.interpreter.Application', run: interpreter.run },
  { key: 'iterator', javaClass: 'behavioral.iterator.Application', run: iterator.run },
  { key: 'memento', javaClass: 'behavioral.memento.Application', run: memento.run },
  { key: 'observer', javaClass: 'behavioral.observer.Application', run: observer.run },
  { key: 'state', javaClass: 'behavioral.state.Application', run: state.run },
  { key: 'strategy', javaClass: 'behavioral.strategy.Application', run: strategy.run },
  { key: 'template-method', javaClass: 'behavioral.templatemethod.Application', run: templateMethod.run },
  { key: 'visitor', javaClass: 'behavioral.visitor.Application', run: visitor.run },
  { key: 'fn-chain', javaClass: 'fn.FunctionalStyleChain', run: fnChain.run },
  { key: 'fn-command', javaClass: 'fn.FunctionalStyleCommand', run: fnCommand.run },
  { key: 'fn-decorator', javaClass: 'fn.FunctionalStyleDecorator', run: fnDecorator.run },
  { key: 'fn-factory', javaClass: 'fn.FunctionalStyleFactory', run: fnFactory.run },
  { key: 'fn-iterator', javaClass: 'fn.FunctionalStyleIterator', run: fnIterator.run },
  { key: 'fn-memento', javaClass: 'fn.FunctionalStyleMemento', run: fnMemento.run },
  { key: 'fn-observer', javaClass: 'fn.FunctionalStyleObserver', run: fnObserver.run },
  { key: 'fn-singleton', javaClass: 'fn.FunctionalStyleSingleton', run: fnSingleton.run },
  { key: 'fn-state', javaClass: 'fn.FunctionalStyleState', run: fnState.run },
  { key: 'fn-strategy', javaClass: 'fn.FunctionalStyleStrategy', run: fnStrategy.run },
  { key: 'fn-template', javaClass: 'fn.FunctionalStyleTemplate', run: fnTemplate.run },
]);

export function tryRun(key: string): boolean {
  const example = examples.find((candidate) => candidate.key === key);
  if (example === undefined) {
    return false;
  }
  example.run();
  return true;
}

export function runAll(): void {
  // never wait for input when running everything - decorator uses its default text
  const previous = SystemInReader.stdinEnabled;
  SystemInReader.stdinEnabled = false;
  try {
    for (const example of examples) {
      console.log(`=== ${example.key} ===`);
      example.run();
    }
  } finally {
    SystemInReader.stdinEnabled = previous;
  }
}

function printUsage(write: (line: string) => void): void {
  write('Usage: npm run patterns -- <example|all>');
  write('Available examples (Java class in pl.training.patterns):');
  for (const example of examples) {
    write(`  ${example.key.padEnd(24)} ${example.javaClass}`);
  }
  write(`  ${'all'.padEnd(24)} runs every example in the order above`);
}

/** Returns the process exit code. */
export function main(args: readonly string[]): number {
  const [first] = args;
  if (first === undefined) {
    printUsage((line) => console.log(line));
    return 0;
  }
  const key = first.trim().toLowerCase();
  if (key === 'all') {
    runAll();
    return 0;
  }
  if (!tryRun(key)) {
    console.error(`Unknown example: ${first}`);
    printUsage((line) => console.error(line));
    return 1;
  }
  return 0;
}

function isEntryPoint(): boolean {
  const script = process.argv[1];
  if (script === undefined) {
    return false;
  }
  try {
    return import.meta.url === pathToFileURL(realpathSync(script)).href;
  } catch {
    return false;
  }
}

if (isEntryPoint()) {
  process.exitCode = main(process.argv.slice(2));
}
