# Refaktoryzacja i wzorce projektowe - przykłady w C#

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
dotnet run --project src/Training.Module5
```

Wzorce projektowe (moduł 4, `module4`) mają jeden punkt wejścia z wyborem przykładu:

```bash
dotnet run --project src/Training.Module4            # lista dostępnych przykładów
dotnet run --project src/Training.Module4 -- builder # jeden przykład
dotnet run --project src/Training.Module4 -- all     # wszystkie po kolei
```

Dostępne klucze:

| Grupa | Klucze |
|---|---|
| Kreacyjne | `abstract-factory`, `builder`, `factory-method`, `prototype`, `singleton` |
| Strukturalne | `adapter`, `bridge`, `composite`, `decorator`, `facade`, `flyweight`, `proxy` |
| Behawioralne | `chain-of-responsibility`, `command`, `interpreter`, `iterator`, `mediator`, `memento`, `observer`, `state`, `strategy`, `template-method`, `visitor` |
| Styl funkcyjny | `fn-chain`, `fn-command`, `fn-decorator`, `fn-factory`, `fn-iterator`, `fn-memento`, `fn-observer`, `fn-singleton`, `fn-state`, `fn-strategy`, `fn-template` |

Przykład `decorator` czyta linię ze standardowego wejścia; jeśli wejście jest puste (lub przy `all`), używa tekstu domyślnego „Hello Decorator Pattern”.

## Struktura

| Java (pakiet) | C# (projekt / namespace) | Testy |
|---|---|---|
| `pl.training.module1` … `module5` | `src/Training.Module1` … `Training.Module5` (`Training.ModuleN.*`) | `tests/Training.ModuleN.Tests` |

Foldery odpowiadają podpakietom (`before`/`after`, `stage0`…`stage3` itd.), np. `pl.training.module5.strategy.after` → `Training.Module5.Strategy.After`. Moduł 4 (`pl.training.module4`, wzorce projektowe) to `src/Training.Module4` (`Training.Module4.Creational.*`, `...Structural.*`, `...Behavioral.*`, `...Fn`).

## Konwencje tłumaczenia Java → C#

- Nazwy klas, metod i testów zachowane (PascalCase); interfejsy z prefiksem `I` (np. `OrderRepository` → `IOrderRepository`), wartości enumów w PascalCase (w wydrukach zamieniane na wielkie litery, aby tekst był taki sam jak w Javie).
- `BigDecimal` → `decimal` z jawnym `MidpointRounding` (`HALF_UP` → `AwayFromZero`, `HALF_EVEN` → `ToEven`); formatowanie zawsze z `CultureInfo.InvariantCulture`.
- `Clock` → `TimeProvider` (w testach `FakeTimeProvider`), `LocalDate` → `DateOnly`, `Instant` → `DateTimeOffset`, `Duration` → `TimeSpan`.
- `Optional<T>` → `T?`; `Objects.requireNonNull` → `ArgumentNullException.ThrowIfNull`; `IllegalArgumentException`/`IllegalStateException`/`UnsupportedOperationException` → `ArgumentException`/`InvalidOperationException`/`NotSupportedException`.
- Tam, gdzie Java przekazuje lambdę jako implementację interfejsu, w C# użyto delegatów (`Action`/`Func`) albo małych klas prywatnych.
- `final` → `sealed` / metody niewirtualne; testy refleksyjne sprawdzają `IsSealed`/`IsVirtual`.
- `module2/tooling`: `InMemoryJavaCompiler` (javax.tools) → `InMemoryCSharpCompiler` oparty o Roslyn; odpowiednikiem ostrzeżenia `-Xlint:rawtypes` jest ostrzeżenie nullable `CS8602`.
- `module4` (wzorce): logowanie `java.util.logging` naśladowane przez `JulLogger` (ten sam format na stderr); enumy z zachowaniem (`OrderStatus`, `MovieType`) → klasy z instancjami statycznymi.

## Testy (JUnit → xUnit)

- `@Test` → `[Fact]`, `@ParameterizedTest` + `@MethodSource` → `[Theory]` + `[MemberData]`.
- Testy przechwytujące konsolę lub zmieniające kulturę (`ar-EG`, `tr-TR`) działają w kolekcji `Console` z wyłączonym zrównolegleniem (odpowiednik `@ResourceLock`).

## Warsztat CineLegacy (moduły 2, 3 i 5)

Odpowiednik warsztatu Java z `src/main/java/pl/training/workshop/**`: stary system kina (`Legacy/CinemaManager`) i krótkie sceny do pokazu na żywo: `m2` (s01-s10), `m3` (s01-s05) i `m5` (s01-s25).

| Java | C# | Testy |
|---|---|---|
| `pl.training.workshop.legacy`, `shared` | `src/Training.Workshop/Legacy`, `Shared` (`Training.Workshop.Legacy`, `...Shared`) | `tests/Training.Workshop.Tests/Legacy` (golden master wspólny z Javą: `src/test/resources/workshop/cinema-manager.approved.txt`) |
| `pl.training.workshop.m5.s08_state.start` | `src/Training.Workshop/M5/S08State/Start` (`Training.Workshop.M5.S08State.Start`) | `tests/Training.Workshop.Tests/M5/S08State/S08EquivalenceTest.cs` |
| `support/Scene` (JUnit `@TestFactory`) | `tests/Training.Workshop.Tests/Support/Scene.cs` (`[Theory]` + `[MemberData]`, jeden przypadek na parę "wariant: przypadek") | |

Każda scena ma katalog `Start` (kod wyjściowy) i `Step1..N` (snapshoty po kolejnych krokach, różni się tylko namespace). Pokaz prowadzi się tym samym skryptem co w Javie, z opcją języka:

```bash
scripts/warsztat.sh --lang cs list m5
scripts/warsztat.sh --lang cs test m5/s08      # dotnet test z filtrem na scenę
scripts/warsztat.sh --lang cs next m5/s08      # kolejny krok do Start (namespace przepisywany automatycznie)
scripts/warsztat.sh --lang cs reset m5/s08
```

Przewodnik prowadzącego i zadania dla uczestników: `src/main/resources/html/warsztat-csharp/` (źródła md w `src/main/resources/md/warsztat-csharp/`).

Różnice względem Javy (szczegóły w przewodniku, akapity "Różnica względem Javy"):

- Wartości enumów w PascalCase także w oczekiwanych tekstach testów (np. `cannot pay in New`); tam, gdzie Java wypisuje nazwę wyjątku, test oczekuje nazwy wyjątku .NET.
- Testy dotykające statycznego stanu starego systemu działają w kolekcji `Legacy` (bez zrównoleglenia).
- Sceny zależne od mechanizmów Javy mają odpowiedniki .NET: `sealed`/`permits` -> konstruktor `private protected`, bramka kompilatora `-Xlint` (m2/s03) -> Roslyn z ostrzeżeniami jako błędami, bramka jakości (m2/s04) -> kompilacja i uruchomienie testów w pamięci przez Roslyn.
- Próbki kodu dla bramki jakości (m2/s04) są wyłączone z kompilacji projektu (`Compile Remove`), a scena m2/s03 ma własny `.editorconfig` wyciszający ostrzeżenia, które bramka ma wykrywać.
