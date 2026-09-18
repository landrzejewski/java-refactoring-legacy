import { describe, expect, it } from 'vitest';
import { NullPointerError } from '../../../src/shared/errors.js';
import { ConsequenceKind } from '../../../src/module8/documentation/ConsequenceKind.js';
import { DecisionConsequence } from '../../../src/module8/documentation/DecisionConsequence.js';
import { DecisionId } from '../../../src/module8/documentation/DecisionId.js';
import { DecisionOption } from '../../../src/module8/documentation/DecisionOption.js';
import { DecisionRecord } from '../../../src/module8/documentation/DecisionRecord.js';
import { DecisionRecordMarkdownRenderer } from '../../../src/module8/documentation/DecisionRecordMarkdownRenderer.js';
import { DecisionStatus } from '../../../src/module8/documentation/DecisionStatus.js';

describe('DecisionRecordMarkdownRendererTest', () => {
  const renderer = new DecisionRecordMarkdownRenderer();

  it('rendersEveryFieldInDeterministicOrder', () => {
    const record = new DecisionRecord(
      new DecisionId('ADR-0042'),
      'Wyodrębnij port zegara',
      DecisionStatus.ACCEPTED,
      'Logika domenowa odczytuje czas systemowy bezpośrednio.',
      'Wprowadzamy port zegara na granicy aplikacji.',
      [
        new DecisionOption('Port zegara', 'Pozwala kontrolować czas w testach.'),
        new DecisionOption(
          'Mockowanie statyczne',
          'Nie zmienia sygnatur, ale zwiększa sprzężenie testów.',
        ),
      ],
      [
        new DecisionConsequence(ConsequenceKind.POSITIVE, 'Testy stają się deterministyczne.'),
        new DecisionConsequence(
          ConsequenceKind.NEGATIVE,
          'Konstruktor otrzymuje dodatkową zależność.',
        ),
      ],
      'Uruchom testy charakteryzujące przed i po zmianie.',
    );

    const expected = `# ADR-0042: Wyodrębnij port zegara

## Status

Zaakceptowana

## Kontekst

Logika domenowa odczytuje czas systemowy bezpośrednio.

## Decyzja

Wprowadzamy port zegara na granicy aplikacji.

## Rozważone opcje

1. **Port zegara**: Pozwala kontrolować czas w testach.
2. **Mockowanie statyczne**: Nie zmienia sygnatur, ale zwiększa sprzężenie testów.

## Konsekwencje

- **Pozytywna**: Testy stają się deterministyczne.
- **Negatywna**: Konstruktor otrzymuje dodatkową zależność.

## Metoda weryfikacji

Uruchom testy charakteryzujące przed i po zmianie.

`;

    expect(renderer.render(record)).toBe(expected);
    expect(renderer.render(record)).toBe(expected);
  });

  it('rejectsANullRecord', () => {
    expect(() => renderer.render(null as unknown as DecisionRecord)).toThrow(NullPointerError);
  });
});
