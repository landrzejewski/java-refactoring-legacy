import type { LocalTime } from '../../shared/time.js';

/**
 * Stabilny kontrakt sceny - wspólny dla start i wszystkich kroków.
 *
 * @param format 2D, 3D albo IMAX
 * @param rows   numery rzędów kupionych miejsc (rząd 10 i dalej to VIP)
 */
export class Order {
  constructor(
    readonly title: string,
    readonly format: string,
    readonly start: LocalTime,
    readonly rows: readonly number[],
  ) {}
}
