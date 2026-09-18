export interface PaymentsService {
  pay(properties: ReadonlyMap<string, string>): void;
}
