# Refaktoryzacja kodu legacy – przykłady w C#

Odpowiedniki przykładów Java z `src/main/java/pl/training/**` (oraz testów z `src/test/java/pl/training/**`) napisane w C# / .NET 10.

## Wymagania

- .NET SDK 10 (`dotnet --list-sdks`)

## Budowanie i testy

```bash
cd csharp
dotnet build
dotnet test
```

## Uruchamianie przykładów

Każdy moduł to osobna aplikacja konsolowa (punkt wejścia: klasa `ModuleNExamples`):

```bash
dotnet run --project src/Training.Module1
dotnet run --project src/Training.Module6
```

Wzorce projektowe (`patterns`) mają jeden punkt wejścia z wyborem przykładu:

```bash
dotnet run --project src/Training.Patterns            # lista dostępnych przykładów
dotnet run --project src/Training.Patterns -- builder # jeden przykład
dotnet run --project src/Training.Patterns -- all     # wszystkie po kolei
```

Dostępne klucze:

| Grupa | Klucze |
|---|---|
| Kreacyjne | `abstract-factory`, `builder`, `factory-method`, `prototype`, `singleton` |
| Strukturalne | `adapter`, `composite`, `decorator`, `facade`, `flyweight`, `proxy` |
| Behawioralne | `chain-of-responsibility`, `command`, `interpreter`, `iterator`, `memento`, `observer`, `state`, `strategy`, `template-method`, `visitor` |
| Styl funkcyjny | `fn-chain`, `fn-command`, `fn-decorator`, `fn-factory`, `fn-iterator`, `fn-memento`, `fn-observer`, `fn-singleton`, `fn-state`, `fn-strategy`, `fn-template` |

Przykład `decorator` czyta linię ze standardowego wejścia; jeśli wejście jest puste (lub przy `all`), używa tekstu domyślnego „Hello Decorator Pattern”.

## Struktura

| Java (pakiet) | C# (projekt / namespace) | Testy |
|---|---|---|
| `pl.training.module1` … `module8` | `src/Training.Module1` … `Training.Module8` (`Training.ModuleN.*`) | `tests/Training.ModuleN.Tests` |
| `pl.training.patterns` | `src/Training.Patterns` (`Training.Patterns.*`) | `tests/Training.Patterns.Tests` |

Foldery odpowiadają podpakietom (`before`/`after`, `stage0`…`stage3` itd.), np. `pl.training.module6.strategy.after` → `Training.Module6.Strategy.After`.

## Konwencje tłumaczenia Java → C#

- Nazwy klas, metod i testów zachowane (PascalCase); interfejsy z prefiksem `I` (np. `OrderRepository` → `IOrderRepository`), wartości enumów w PascalCase (w wydrukach zamieniane na wielkie litery, aby tekst był taki sam jak w Javie).
- `BigDecimal` → `decimal` z jawnym `MidpointRounding` (`HALF_UP` → `AwayFromZero`, `HALF_EVEN` → `ToEven`); formatowanie zawsze z `CultureInfo.InvariantCulture`.
- `Clock` → `TimeProvider` (w testach `FakeTimeProvider`), `LocalDate` → `DateOnly`, `Instant` → `DateTimeOffset`, `Duration` → `TimeSpan`.
- `Optional<T>` → `T?`; `Objects.requireNonNull` → `ArgumentNullException.ThrowIfNull`; `IllegalArgumentException`/`IllegalStateException`/`UnsupportedOperationException` → `ArgumentException`/`InvalidOperationException`/`NotSupportedException`.
- Tam, gdzie Java przekazuje lambdę jako implementację interfejsu, w C# użyto delegatów (`Action`/`Func`) albo małych klas prywatnych.
- `final` → `sealed` / metody niewirtualne; testy refleksyjne sprawdzają `IsSealed`/`IsVirtual`.
- `module8/tooling`: `InMemoryJavaCompiler` (javax.tools) → `InMemoryCSharpCompiler` oparty o Roslyn; odpowiednikiem ostrzeżenia `-Xlint:rawtypes` jest ostrzeżenie nullable `CS8602`.
- `patterns`: logowanie `java.util.logging` naśladowane przez `JulLogger` (ten sam format na stderr); enumy z zachowaniem (`OrderStatus`, `MovieType`) → klasy z instancjami statycznymi.

## Testy (JUnit → xUnit)

- `@Test` → `[Fact]`, `@ParameterizedTest` + `@MethodSource` → `[Theory]` + `[MemberData]`.
- Testy przechwytujące konsolę lub zmieniające kulturę (`ar-EG`, `tr-TR`) działają w kolekcji `Console` z wyłączonym zrównolegleniem (odpowiednik `@ResourceLock`).
