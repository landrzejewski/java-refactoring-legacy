// Odpowiednik @FunctionalInterface — w TS wystarczy typ funkcyjny,
// ale interfejs z metodą pozwala też przekazać obiekt/klasę.
export interface MaintenanceWindows {
  allows(service: string, hourUtc: number): boolean;
}
