# Refaktoryzacja do wzorców - warsztat CineLegacy w C#

Odpowiednik warsztatu Java z `src/main/java/pl/training/workshop/**` (oraz testów z `src/test/java/pl/training/workshop/**`) napisany w C# / .NET 10.

## Wymagania

- .NET SDK 10 (`dotnet --list-sdks`)

## Budowanie i testy

```bash
cd csharp
dotnet build
dotnet test
```

## Konwencje tłumaczenia Java → C#

- Nazwy klas, metod i testów zachowane (PascalCase); interfejsy z prefiksem `I` (np. `OrderRepository` → `IOrderRepository`), wartości enumów w PascalCase (w wydrukach zamieniane na wielkie litery, aby tekst był taki sam jak w Javie).
- `BigDecimal` → `decimal` z jawnym `MidpointRounding` (`HALF_UP` → `AwayFromZero`, `HALF_EVEN` → `ToEven`); formatowanie zawsze z `CultureInfo.InvariantCulture`.
- `Clock` → `TimeProvider` (w testach `FakeTimeProvider`), `LocalDate` → `DateOnly`, `Instant` → `DateTimeOffset`, `Duration` → `TimeSpan`.
- `Optional<T>` → `T?`; `Objects.requireNonNull` → `ArgumentNullException.ThrowIfNull`; `IllegalArgumentException`/`IllegalStateException`/`UnsupportedOperationException` → `ArgumentException`/`InvalidOperationException`/`NotSupportedException`.
- Tam, gdzie Java przekazuje lambdę jako implementację interfejsu, w C# użyto delegatów (`Action`/`Func`) albo małych klas prywatnych.
- `final` → `sealed` / metody niewirtualne; testy refleksyjne sprawdzają `IsSealed`/`IsVirtual`.

## Testy (JUnit → xUnit)

- `@Test` → `[Fact]`, `@ParameterizedTest` + `@MethodSource` → `[Theory]` + `[MemberData]`.
- Testy przechwytujące konsolę lub zmieniające kulturę (`ar-EG`, `tr-TR`) działają w kolekcji `Console` z wyłączonym zrównolegleniem (odpowiednik `@ResourceLock`).

## Warsztat CineLegacy (sceny m2, m3 i m5)

Stary system kina (`Legacy/CinemaManager`) i krótkie sceny do pokazu na żywo: `m2` (s01-s10), `m3` (s01-s05) i `m5` (s01-s32).

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
