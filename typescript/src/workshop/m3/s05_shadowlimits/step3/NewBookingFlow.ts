import type { BookingRequest } from '../BookingRequest.js';
import { BookingPlanner } from './BookingPlanner.js';
import type { Effects } from './Effects.js';

/**
 * Krok 3: nowa ścieżka = plan (czysty) + wykonanie planu na porcie efektów.
 * Tylko ta klasa wykonuje efekty i używa jej wyłącznie ścieżka autorytatywna.
 */
export class NewBookingFlow {
  private readonly planner = new BookingPlanner();

  constructor(private readonly effects: Effects) {}

  book(request: BookingRequest): string {
    const plan = this.planner.plan(request);
    plan.effects.forEach((effect) => effect.applyTo(this.effects));
    return plan.result;
  }
}
