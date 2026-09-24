/** Port poczty - istniejąca integracja, której nie zmieniamy. */
export interface Mailer {
  send(to: string, text: string): void;
}
