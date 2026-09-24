import { describe, expect, it } from 'vitest';

import { CashierConsole } from '../../../../src/workshop/m6/s15_command/step3/CashierConsole.js';
import type { ConsoleCommand } from '../../../../src/workshop/m6/s15_command/step3/ConsoleCommand.js';
import { ReportCommand } from '../../../../src/workshop/m6/s15_command/step3/ReportCommand.js';

/** Rejestr komend jest otwarty na nowe komendy bez zmiany dyspozytora. */
describe('S15SolutionTest', () => {
  it('newCommandIsJustARegistryEntry', () => {
    const console = new CashierConsole(new Map<string, ConsoleCommand>([
      ['REPORT', new ReportCommand()],
      ['HELLO', { execute: (args) => `Dzien dobry ${args}` }],
    ]));
    expect(console.handle('hello Anna')).toBe('Dzien dobry Anna');
    expect(console.handle('SELL 1 Diuna')).toBe('Nieznana komenda: SELL');
  });
});
