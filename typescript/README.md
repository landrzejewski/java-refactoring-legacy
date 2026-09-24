# Refaktoryzacja kodu legacy – przykłady w TypeScript

Odpowiedniki przykładów Java z `src/main/java/pl/training/**` (oraz testów z `src/test/java/pl/training/**`) napisane w TypeScript (Node.js, ESM).

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

## Uruchamianie przykładów

Po `npm run build` każdy moduł uruchamia się osobno (punkt wejścia: `src/moduleN/main.ts`):

```bash
npm run module1
npm run module6
```

Wzorce projektowe (`patterns`) mają jeden punkt wejścia z wyborem przykładu:

```bash
npm run patterns              # lista dostępnych przykładów
npm run patterns -- builder   # jeden przykład
npm run patterns -- all       # wszystkie po kolei
```

Dostępne klucze:

| Grupa | Klucze |
|---|---|
| Kreacyjne | `abstract-factory`, `builder`, `factory-method`, `prototype`, `singleton` |
| Strukturalne | `adapter`, `composite`, `decorator`, `facade`, `flyweight`, `proxy` |
| Behawioralne | `chain-of-responsibility`, `command`, `interpreter`, `iterator`, `memento`, `observer`, `state`, `strategy`, `template-method`, `visitor` |
| Styl funkcyjny | `fn-chain`, `fn-command`, `fn-decorator`, `fn-factory`, `fn-iterator`, `fn-memento`, `fn-observer`, `fn-singleton`, `fn-state`, `fn-strategy`, `fn-template` |

Przykład `decorator` czyta linię ze standardowego wejścia (`echo "ala ma kota" | npm run patterns -- decorator`); gdy wejście jest terminalem lub puste (oraz przy `all`), używa tekstu domyślnego „Hello Decorator Pattern”.

## Struktura

| Java (pakiet) | TypeScript | Testy |
|---|---|---|
| `pl.training.module1` … `module8` | `src/module1` … `src/module8` | `test/module1` … `test/module8` |
| `pl.training.patterns` | `src/patterns` | `test/patterns` |
| — | `src/shared` (wyjątki, `requireNonNull`, `assertNever`) | `test/shared` |

Foldery odpowiadają podpakietom (`before`/`after`, `stage0`…`stage3` itd.), pliki mają nazwy klas Javy (`LegacyOrderService.ts`). Testy: `XxxTest.java` → `Xxx.test.ts`, `describe` = nazwa klasy testowej Javy, `it` = nazwa metody testowej.

## Konwencje tłumaczenia Java → TypeScript

- Nazwy klas i metod bez zmian (camelCase), wartości enumów UPPER_CASE — wydruki są identyczne jak w Javie.
- `BigDecimal` → `Decimal` z `decimal.js` (`import { Decimal } from 'decimal.js'`); `decimal.js` nie przechowuje skali, więc wydruki używają `toFixed(n)`, a testy skali sprawdzają `decimalPlaces()`.
- `int`/`long` → `number`; `Math.addExact` itp. → jawne sprawdzenie zakresu rzucające `ArithmeticError`; w testach przepełnienia `Number.MAX_SAFE_INTEGER` zamiast `Long.MAX_VALUE`.
- Wyjątki Javy → klasy z `src/shared/errors.ts`: `IllegalArgumentError`, `IllegalStateError`, `UnsupportedOperationError`, `NullPointerError`, `ArithmeticError`.
- `record` → klasy z polami `readonly` (+ `equals()`/`toString()` tam, gdzie są używane); `sealed interface` → unie dyskryminowane z polem `kind`.
- Niemodyfikowalne kolekcje (`List.copyOf`) → `Object.freeze([...])` (modyfikacja rzuca `TypeError`).
- `Clock`/`LocalDate`/`Instant` → małe helpery (`Clock { now(): Date }`, `LocalDate`, formatowanie jak `Instant.toString()`).
- Testy „locale” (`ar-EG`, `tr-TR`) pokazują, że kod nie używa API zależnych od locale (`toLocaleString`, `toLocaleLowerCase`).
- `synchronized`/atomiki pominięte (JavaScript jest jednowątkowy).
- `module8/tooling`: `InMemoryJavaCompiler` (javax.tools) → `InMemoryTypeScriptCompiler`. TypeScript 7 (natywny kompilator używany w `npm run build`) nie udostępnia API dla JS, dlatego ten przykład korzysta z API TypeScript 5.9 zainstalowanego pod aliasem `typescript-api`. Odpowiednikiem ostrzeżenia `-Xlint:rawtypes` jest niejawne `any` (TS7006).
- `patterns`: logowanie `java.util.logging` naśladowane przez `JulLogger` (ten sam format na stderr); enumy z zachowaniem (`OrderStatus`, `MovieType`) → klasy z instancjami statycznymi; przeciążone `visit(...)` w wizytorze → `visitDepartment`/`visitEmployee`/….

## Warsztat CineLegacy (moduły 3-8)

Odpowiednik warsztatu Java z `src/main/java/pl/training/workshop/**`: stary system kina (`src/workshop/legacy/CinemaManager.ts`) i ok. 100 krótkich scen do pokazu na żywo.

| Java | TypeScript | Testy |
|---|---|---|
| `pl.training.workshop.legacy`, `shared` | `src/workshop/legacy`, `src/workshop/shared` (`Money`, `time.ts` - odpowiedniki `LocalDateTime`, `LocalDate`, `LocalTime`, `Duration`, `Clock`, liczone w UTC) | `test/workshop/legacy` (golden master wspólny z Javą: `src/test/resources/workshop/cinema-manager.approved.txt`) |
| `pl.training.workshop.m6.s08_state.start` | `src/workshop/m6/s08_state/start` | `test/workshop/m6/s08_state/S08EquivalenceTest.test.ts` |
| `support/Scene` (JUnit `@TestFactory`) | `test/workshop/support/scene.ts` (jeden `it` na parę "wariant: przypadek") | |

Każda scena ma katalog `start` (kod wyjściowy) i `step1..N` (snapshoty po kolejnych krokach). Kroki importują typy sceny ścieżką względną (`../Typ.js`), więc skopiowanie kroku do `start` nie wymaga zmian. Pokaz prowadzi się tym samym skryptem co w Javie, z opcją języka:

```bash
scripts/warsztat.sh --lang ts list m6
scripts/warsztat.sh --lang ts test m6/s08      # vitest na katalogu testów sceny
scripts/warsztat.sh --lang ts next m6/s08      # kolejny krok do start
scripts/warsztat.sh --lang ts reset m6/s08
```

Przewodnik prowadzącego i zadania dla uczestników: `src/main/resources/html/warsztat-typescript/` (źródła md w `src/main/resources/md/warsztat-typescript/`).

Różnice względem Javy (szczegóły w przewodniku, akapity "Różnica względem Javy"):

- Tam, gdzie Java wypisuje nazwę wyjątku, test oczekuje nazwy klasy błędu TS (np. `IllegalArgumentError`).
- Brak przeciążeń, `final`, pakietowej widoczności i `sealed`: osobne nazwy metod lub sygnatury przeciążeń, unie dyskryminowane z `assertNever`, moduły bez eksportu.
- Sceny zależne od mechanizmów Javy mają odpowiedniki TS: zgodność binarna -> wcześniej skompilowany klient JS, serializacja -> kształt JSON i DTO, bramka kompilatora `-Xlint` i codemod na Compiler Tree API -> `typescript-api` (TS 5.9) z ostrzejszymi opcjami i transformerem.
- Próbki kodu dla bramki jakości (m8/s10) są wyłączone z `tsconfig.json` i `tsconfig.build.json`.
