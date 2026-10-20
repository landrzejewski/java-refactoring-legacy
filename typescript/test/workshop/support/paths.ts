import { fileURLToPath } from 'node:url';

// Ścieżki do plików źródłowych warsztatu - dla testów, które czytają kod sceny
// (odpowiednik Path.of("src/main/java/pl/training/workshop/...") w Javie).

// Katalog typescript/src/workshop, np. workshopDir('m2', 's02_adr').
export function workshopDir(...parts: string[]): string {
  return fileURLToPath(new URL(['../../../src/workshop', ...parts].join('/'), import.meta.url));
}

// Katalog typescript/test/workshop, np. testDir('m2', 's04_qualitygate').
export function testDir(...parts: string[]): string {
  return fileURLToPath(new URL(['..', ...parts].join('/'), import.meta.url));
}
