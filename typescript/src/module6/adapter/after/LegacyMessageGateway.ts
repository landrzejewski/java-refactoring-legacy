export interface LegacyMessageGateway {
  transmit(destination: string, body: string): string;
}
