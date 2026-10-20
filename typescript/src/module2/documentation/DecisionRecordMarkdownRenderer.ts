import { assertNever } from '../../shared/assertNever.js';
import { requireNonNull } from '../../shared/requireNonNull.js';
import { ConsequenceKind } from './ConsequenceKind.js';
import type { DecisionRecord } from './DecisionRecord.js';
import { DecisionStatus } from './DecisionStatus.js';

export class DecisionRecordMarkdownRenderer {
  render(record: DecisionRecord): string {
    requireNonNull(record, 'record');

    const markdown: string[] = [];
    markdown.push('# ', record.id.toString(), ': ', record.title, '\n\n');
    DecisionRecordMarkdownRenderer.section(
      markdown,
      'Status',
      DecisionRecordMarkdownRenderer.statusLabel(record.status),
    );
    DecisionRecordMarkdownRenderer.section(markdown, 'Kontekst', record.context);
    DecisionRecordMarkdownRenderer.section(markdown, 'Decyzja', record.decision);

    markdown.push('## Rozważone opcje\n\n');
    record.consideredOptions.forEach((option, index) => {
      markdown.push(String(index + 1), '. **', option.name, '**: ', option.rationale, '\n');
    });
    markdown.push('\n');

    markdown.push('## Konsekwencje\n\n');
    for (const consequence of record.consequences) {
      markdown.push(
        '- **',
        DecisionRecordMarkdownRenderer.consequenceLabel(consequence.kind),
        '**: ',
        consequence.description,
        '\n',
      );
    }
    markdown.push('\n');
    DecisionRecordMarkdownRenderer.section(
      markdown,
      'Metoda weryfikacji',
      record.verificationMethod,
    );
    return markdown.join('');
  }

  private static section(markdown: string[], heading: string, content: string): void {
    markdown.push('## ', heading, '\n\n', content, '\n\n');
  }

  private static statusLabel(status: DecisionStatus): string {
    switch (status) {
      case DecisionStatus.PROPOSED:
        return 'Proponowana';
      case DecisionStatus.ACCEPTED:
        return 'Zaakceptowana';
      case DecisionStatus.REJECTED:
        return 'Odrzucona';
      case DecisionStatus.DEPRECATED:
        return 'Wycofana';
      case DecisionStatus.SUPERSEDED:
        return 'Zastąpiona';
      default:
        return assertNever(status);
    }
  }

  private static consequenceLabel(kind: ConsequenceKind): string {
    switch (kind) {
      case ConsequenceKind.POSITIVE:
        return 'Pozytywna';
      case ConsequenceKind.NEGATIVE:
        return 'Negatywna';
      case ConsequenceKind.NEUTRAL:
        return 'Neutralna';
      default:
        return assertNever(kind);
    }
  }
}
