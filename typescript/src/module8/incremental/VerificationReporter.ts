import type { VerificationEvent } from './VerificationEvent.js';

export interface VerificationReporter {
  report(event: VerificationEvent): void;
}

// Odpowiednik statycznej metody interfejsu VerificationReporter.ignoring().
export const VerificationReporter = {
  ignoring(): VerificationReporter {
    return { report: () => {} };
  },
};
