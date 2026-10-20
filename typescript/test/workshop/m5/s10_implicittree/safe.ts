import { IllegalArgumentError } from '../../../../src/shared/errors.js';

// Wspólny adapter testów sceny S10 (w Javie: statyczna metoda S10EquivalenceTest.safe, używana
// też przez S10DifferentialTest; tu osobny moduł, bo import pliku .test.ts zdublowałby jego testy).
export function safe(
  price: (definition: readonly unknown[]) => { toString(): string },
  render: (definition: readonly unknown[]) => string,
): (definition: readonly unknown[]) => string {
  return (definition) => {
    try {
      return `${price(definition).toString()}\n${render(definition)}`;
    } catch (error) {
      if (error instanceof IllegalArgumentError) {
        return `ERROR ${error.message}`;
      }
      throw error;
    }
  };
}
