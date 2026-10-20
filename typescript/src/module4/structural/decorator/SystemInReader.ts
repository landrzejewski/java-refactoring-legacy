import { readFileSync } from 'node:fs';
import type { Reader } from './Reader.js';

/**
 * Java: new Scanner(System.in).nextLine(). Reads the first line of standard input.
 * Deviation: when stdin is a TTY, empty/closed or reading is disabled (dispatcher "all" mode, tests),
 * DEFAULT_TEXT is returned instead of blocking / throwing NoSuchElementException.
 */
export class SystemInReader implements Reader {
  static readonly DEFAULT_TEXT = 'Hello Decorator Pattern';
  /** Set to false by the dispatcher in "all" mode, so the run never waits for input. */
  static stdinEnabled = true;

  getText(): string {
    if (!SystemInReader.stdinEnabled || process.stdin.isTTY) {
      return SystemInReader.DEFAULT_TEXT;
    }
    let input: string;
    try {
      input = readFileSync(0, 'utf8');
    } catch {
      return SystemInReader.DEFAULT_TEXT;
    }
    if (input.length === 0) {
      return SystemInReader.DEFAULT_TEXT;
    }
    return input.split(/\r?\n/)[0] ?? SystemInReader.DEFAULT_TEXT;
  }
}
