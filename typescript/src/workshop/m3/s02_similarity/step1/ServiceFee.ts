/**
 * Krok 1: po Inline Method z ServiceFee zostało tylko wyliczenie rodzajów.
 * Usuniemy je w kroku 2, gdy wywołujący przestaną z niego korzystać.
 */
export enum Kind {
  ONLINE_BOOKING = 'ONLINE_BOOKING',
  REFUND = 'REFUND',
}
