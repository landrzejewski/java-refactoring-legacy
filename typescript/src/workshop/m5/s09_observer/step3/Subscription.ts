/** Krok 3: uchwyt subskrypcji - close() wyrejestrowuje, idempotentnie. */
export interface Subscription {
  close(): void;
}
