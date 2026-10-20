/** Port programu lojalnościowego - istniejąca integracja, której nie zmieniamy. */
export interface LoyaltyProgram {
  addPoints(email: string, points: number): void;
}
