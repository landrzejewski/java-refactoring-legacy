/** Port bramki SMS - istniejąca integracja, której nie zmieniamy. */
export interface SmsGateway {
  send(phone: string, text: string): void;
}
