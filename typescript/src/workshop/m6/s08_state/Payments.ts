// Port bramki płatności - efekt zewnętrzny, który może się nie udać.
export interface Payments {
  charge(reservationId: string): void;
}
