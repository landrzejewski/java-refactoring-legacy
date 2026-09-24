import type { Status } from './Status.js';

// Stabilny kontrakt rezerwacji - wspólny dla start i kroków, używany przez test tabeli przejść.
export interface ReservationActions {
  pay(): void;

  use(): void;

  expire(): void;

  cancel(): void;

  status(): Status;

  // Efekty uboczne w kolejności wystąpienia.
  effects(): readonly string[];
}
