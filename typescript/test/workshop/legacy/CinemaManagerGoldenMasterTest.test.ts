import { readFileSync } from 'node:fs';
import { fileURLToPath } from 'node:url';

import { describe, expect, it } from 'vitest';

import { runCinemaManagerScript } from './CinemaManagerScript.js';

// Golden master całego starego systemu. Każda zmiana w legacy, która
// zmienia choćby jedną wiadomość, kwotę czy kolejność, zostanie wykryta.
// Zatwierdzony wynik jest wspólny z Javą: src/test/resources/workshop/cinema-manager.approved.txt
const APPROVED = fileURLToPath(
  new URL('../../../../src/test/resources/workshop/cinema-manager.approved.txt', import.meta.url),
);

describe('CinemaManagerGoldenMasterTest', () => {
  it('oneDayOfCinemaProducesApprovedOutput', () => {
    expect(runCinemaManagerScript()).toBe(readFileSync(APPROVED, 'utf8'));
  });
});
