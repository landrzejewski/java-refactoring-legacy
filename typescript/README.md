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
