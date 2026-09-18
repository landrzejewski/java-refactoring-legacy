// Formatuje Date jak Instant.toString() w Javie (ISO-8601 UTC, bez ".000" przy pełnych sekundach),
// np. 2030-06-15T10:15:30Z.
export function instantToString(instant: Date): string {
  return instant.toISOString().replace('.000Z', 'Z');
}
