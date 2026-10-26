# Refaktoryzacja do wzorców - warsztat CineLegacy w TypeScript

Odpowiednik warsztatu Java z `src/main/java/pl/training/workshop/**` (oraz testów z `src/test/java/pl/training/workshop/**`) napisany w TypeScript (Node.js, ESM).

## Wymagania

- Node.js 22+ i npm

## Budowanie i testy

```bash
cd typescript
npm ci
npm run build      # kompilacja TypeScript 7 (tsc) do dist/
npm run typecheck  # sprawdzenie typów łącznie z testami
npm test           # Vitest
```

## Struktura

| Java (pakiet) | TypeScript | Testy |
|---|---|---|
| — | `src/shared` (wyjątki, `requireNonNull`, `assertNever`) | `test/shared` |

Foldery odpowiadają podpakietom (`start`, `step1`…`stepN`), pliki mają nazwy klas Javy (`CinemaManager.ts`). Testy: `XxxTest.java` → `Xxx.test.ts`, `describe` = nazwa klasy testowej Javy, `it` = nazwa metody testowej.

## Konwencje tłumaczenia Java → TypeScript

- Nazwy klas i metod bez zmian (camelCase), wartości enumów UPPER_CASE — wydruki są identyczne jak w Javie.
- `BigDecimal` → `Decimal` z `decimal.js` (`import { Decimal } from 'decimal.js'`); `decimal.js` nie przechowuje skali, więc wydruki używają `toFixed(n)`, a testy skali sprawdzają `decimalPlaces()`.
- `int`/`long` → `number`; `Math.addExact` itp. → jawne sprawdzenie zakresu rzucające `ArithmeticError`.
- Wyjątki Javy → klasy z `src/shared/errors.ts`: `IllegalArgumentError`, `IllegalStateError`, `UnsupportedOperationError`, `NullPointerError`, `ArithmeticError`.
- `record` → klasy z polami `readonly` (+ `equals()`/`toString()` tam, gdzie są używane); `sealed interface` → unie dyskryminowane z polem `kind`.
- Niemodyfikowalne kolekcje (`List.copyOf`) → `Object.freeze([...])` (modyfikacja rzuca `TypeError`).
- `Clock`/`LocalDate`/`Instant` → małe helpery (`Clock { now(): Date }`, `LocalDate`, formatowanie jak `Instant.toString()`).
- Testy „locale” (`ar-EG`, `tr-TR`) pokazują, że kod nie używa API zależnych od locale (`toLocaleString`, `toLocaleLowerCase`).
- `synchronized`/atomiki pominięte (JavaScript jest jednowątkowy).

## Warsztat CineLegacy (sceny m2, m3 i m5)

Stary system kina (`src/workshop/legacy/CinemaManager.ts`) i 47 krótkich scen do pokazu na żywo: `m2` (s01-s10), `m3` (s01-s05) i `m5` (s01-s32).

| Java | TypeScript | Testy |
|---|---|---|
| `pl.training.workshop.legacy`, `shared` | `src/workshop/legacy`, `src/workshop/shared` (`Money`, `time.ts` - odpowiedniki `LocalDateTime`, `LocalDate`, `LocalTime`, `Duration`, `Clock`, liczone w UTC) | `test/workshop/legacy` (golden master wspólny z Javą: `src/test/resources/workshop/cinema-manager.approved.txt`) |
| `pl.training.workshop.m5.s08_state.start` | `src/workshop/m5/s08_state/start` | `test/workshop/m5/s08_state/S08EquivalenceTest.test.ts` |
| `support/Scene` (JUnit `@TestFactory`) | `test/workshop/support/scene.ts` (jeden `it` na parę "wariant: przypadek") | |

Każda scena ma katalog `start` (kod wyjściowy) i `step1..N` (snapshoty po kolejnych krokach). Kroki importują typy sceny ścieżką względną (`../Typ.js`), więc skopiowanie kroku do `start` nie wymaga zmian. Pokaz prowadzi się tym samym skryptem co w Javie, z opcją języka:

```bash
scripts/warsztat.sh --lang ts list m5
scripts/warsztat.sh --lang ts test m5/s08      # vitest na katalogu testów sceny
scripts/warsztat.sh --lang ts next m5/s08      # kolejny krok do start
scripts/warsztat.sh --lang ts reset m5/s08
```

Przewodnik prowadzącego i zadania dla uczestników: `src/main/resources/html/warsztat-typescript/` (źródła md w `src/main/resources/md/warsztat-typescript/`).

Różnice względem Javy (szczegóły w przewodniku, akapity "Różnica względem Javy"):

- Tam, gdzie Java wypisuje nazwę wyjątku, test oczekuje nazwy klasy błędu TS (np. `IllegalArgumentError`).
- Brak przeciążeń, `final`, pakietowej widoczności i `sealed`: osobne nazwy metod lub sygnatury przeciążeń, unie dyskryminowane z `assertNever`, moduły bez eksportu.
- Sceny zależne od mechanizmów Javy mają odpowiedniki TS: bramka kompilatora `-Xlint` -> `typescript-api` (TS 5.9) z ostrzejszymi opcjami.
- Próbki kodu dla bramki jakości (m2/s04) są wyłączone z `tsconfig.json` i `tsconfig.build.json`.
