// Odpowiednik java.time.Clock: źródło bieżącego czasu wstrzykiwane jako zależność.
export interface Clock {
  now(): Date;
}

export const systemClock: Clock = { now: () => new Date() };

// Odpowiednik Clock.fixed(Instant.parse(...), ZoneOffset.UTC).
export function fixedClock(instant: string): Clock {
  const fixed = new Date(instant);
  return { now: () => new Date(fixed.getTime()) };
}
