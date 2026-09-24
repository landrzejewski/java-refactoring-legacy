import { Screening } from '../../../../src/workshop/m8/s01_branchbyabstraction/Screening.js';
import { LocalDateTime } from '../../../../src/workshop/shared/time.js';

// Seanse wspólne dla S01EquivalenceTest i S01SolutionTest (w Javie: stałe S01EquivalenceTest).
export const DIUNA = new Screening('Diuna', 3, LocalDateTime.of(2026, 3, 13, 20, 0), 10);
export const KRAINA_LODU = new Screening('Kraina Lodu', 2, LocalDateTime.of(2026, 3, 14, 10, 30), 10);
export const AMATOR = new Screening('Amator', 1, LocalDateTime.of(2026, 3, 14, 18, 0), 10);
export const AMATOR_RANO = new Screening('Amator', 1, LocalDateTime.of(2026, 3, 15, 9, 0), 10);

export const GROUP_SEATS: readonly string[] = ['A1', 'B1', 'C1', 'D1', 'E1', 'F1', 'G1', 'H1', 'I1', 'J1'];
