// Deklaracje zbudowanej wtyczki (jak plik .d.ts dołączony do pakietu npm).
export interface PluginApi {
  readonly Money: unknown;
  readonly StudentTicket: unknown;
  readonly BoxOfficeApi: unknown;
}

export function run(api: PluginApi): string;
