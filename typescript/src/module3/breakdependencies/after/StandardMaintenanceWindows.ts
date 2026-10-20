import type { MaintenanceWindows } from './MaintenanceWindows.js';

export class StandardMaintenanceWindows implements MaintenanceWindows {
  private static readonly WINDOW_START_HOUR_UTC = 0;
  private static readonly WINDOW_END_HOUR_UTC = 6;

  allows(_service: string, hourUtc: number): boolean {
    return hourUtc >= StandardMaintenanceWindows.WINDOW_START_HOUR_UTC
        && hourUtc < StandardMaintenanceWindows.WINDOW_END_HOUR_UTC;
  }
}
