// Stabilny kontrakt: status rezerwacji zapisywany w bazie.
export enum Status {
  NEW = 'NEW',
  PAID = 'PAID',
  USED = 'USED',
  EXPIRED = 'EXPIRED',
  CANCELLED = 'CANCELLED',
}
