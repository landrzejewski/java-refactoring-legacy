# Refaktoryzacja do wzorców - ćwiczenia utrwalające CineLegacy w PHP

Odpowiednik ćwiczeń Java z `src/main/java/pl/training/workshop/exercises/**` (oraz testów z `src/test/java/pl/training/workshop/exercises/**`) napisany w PHP.

## Wymagania

- PHP 8.4 lub nowszy z rozszerzeniem `bcmath` (`php -m | grep bcmath`) i Composer

## Instalacja i testy

```bash
cd php
composer install
vendor/bin/phpunit
```

## Struktura

Przestrzenie nazw zgodne z PSR-4 (`Training\Workshop\` → `src/`, `Training\Workshop\Tests\` → `tests/`), nazwy katalogów w PascalCase jak w wersji C#:

| Java | PHP | Testy |
|---|---|---|
| `pl.training.workshop.shared.Money` | `src/Shared/Money.php` (`Training\Workshop\Shared\Money`) | |
| `pl.training.workshop.exercises.e03_notifications.start` | `src/Exercises/E03Notifications/Start` (`Training\Workshop\Exercises\E03Notifications\Start`) | `tests/Exercises/E03Notifications/E03EquivalenceTest.php` |
| `support/Scene` (JUnit `@TestFactory`) | `tests/Support/Scene.php` (dane dla `#[DataProvider]`, jeden przypadek na parę "wariant: przypadek") | |
| `src/main/resources/exercises` | `resources/exercises` | |

Każde ćwiczenie ma katalog `Start` (kod wyjściowy) i `Step1..N` (snapshoty po kolejnych krokach, różni się tylko namespace). Klasy wspólne dla wszystkich wariantów (stabilny kontrakt ćwiczenia, np. `ImportRequest`) leżą w przestrzeni nazw ćwiczenia, poza `Start`/`StepN`.

## Konwencje tłumaczenia Java → PHP

- Każdy plik zaczyna się od `declare(strict_types=1);`; jedna klasa na plik, nazwy klas, metod i testów bez zmian (camelCase), komentarze z tą samą treścią.
- `record` → `final readonly class` z promocją właściwości w konstruktorze; akcesor `request.fileName()` → właściwość `$request->fileName`; `toString()` → `__toString()` (+ `Stringable`); `equals()` zwykle niepotrzebne, bo `assertEquals` porównuje obiekty po polach.
- `final class` → `final class`; metoda `final` → `final public function`; `sealed`/`permits` → zwykła hierarchia (PHP nie ma klas zapieczętowanych).
- `enum` → `enum` z przypadkami nazwanymi jak w Javie (UPPER_CASE), więc wydruki `name()` → `->name` są identyczne.
- `List`/`Map` → tablice (`list<T>`, `array<K, V>` w PHPDoc); tablice są kopiowane przy przekazaniu, więc `List.copyOf` nie jest potrzebne. Mapy z kluczem obiektowym → `SplObjectStorage`/`WeakMap`, kolejki → `SplQueue`/`SplStack` albo tablica.
- Przeciążone metody → osobne nazwy albo parametry opcjonalne; interfejs funkcyjny implementowany lambdą → `Closure` (z typem w PHPDoc) albo klasa anonimowa.
- Stałe obiektowe (`Money.ZERO`, `X.INSTANCE`, `PrintOptions.NONE`) → metody statyczne (`Money::zero()`, `X::instance()`), bo stała klasy w PHP nie może trzymać obiektu.
- Klasy zagnieżdżone → osobne klasy w tej samej przestrzeni nazw (`Cart.Memento` → `Memento`, `GroupDiscount.Allocation` → `GroupDiscountAllocation`); prywatne składowe dostępne tylko dla klasy zewnętrznej - przez `Closure::bind`.
- Widoczność pakietowa → klasa publiczna z tagiem `@internal` w PHPDoc; testy ukrycia klas (e07, e10) sprawdzają ten tag.
- Słowa zastrzeżone jako nazwy klas: `And`/`Or` → `AndRule`/`OrRule` (e05).
- `BigDecimal` → `BcMath\Number` (PHP 8.4); `Money` trzyma kwotę jako `Number` ze skalą 2 i zaokrągla `RoundingMode::HalfAwayFromZero` (= `HALF_UP`). Dzielenie z zaokrągleniem: `$x->div($y, 10)` i `new Money(...)` (HALF_UP), a `RoundingMode.DOWN` → `$x->div($y, 2)` (bcmath obcina).
- `LocalTime`/`LocalDate`/`LocalDateTime` → `DateTimeImmutable` (strefa UTC ustawiona w `phpunit.xml`), wydruki przez `format()` tak, by tekst był identyczny jak w Javie; `DayOfWeek` → `format('N')` albo enum w kontrakcie ćwiczenia.
- Wyjątki: `IllegalArgumentException` → `InvalidArgumentException`, `IllegalStateException` → `LogicException`, `UnsupportedOperationException` → `BadMethodCallException`, `NoSuchElementException` → `OutOfBoundsException`, `ArithmeticException` → `ArithmeticError`. Komunikaty bez zmian; tam, gdzie Java wypisuje nazwę wyjątku, test oczekuje nazwy klasy PHP.

## Testy (JUnit → PHPUnit 13)

- `@Test` → `#[Test]` (nazwy metod jak w Javie), `@TestFactory` + `Scene` → `#[DataProvider]` + `Scene::cases()`, `assertThrows` → `expectException` + `expectExceptionMessage` (albo `try`/`catch`, gdy po wyjątku są dalsze asercje), `assumeTrue` → `markTestSkipped`.
- Testy równoważności używają tylko publicznego API wspólnego dla wszystkich wariantów, więc działają także po skopiowaniu dowolnego kroku do `Start`. Testy sprawdzające "pułapkę" w `Start` są pomijane (`markTestSkipped`), gdy w `Start` jest już rozwiązanie; w testach, które obok pułapki sprawdzają też rozwiązanie, sama asercja na `Start` jest warunkowa.
- Testy refleksyjne (`final`, zadeklarowane metody, pola) używają `ReflectionClass`/`ReflectionMethod`.

## Pokaz

Ten sam skrypt co w Javie, z opcją języka:

```bash
scripts/warsztat.sh --lang php list exercises
scripts/warsztat.sh --lang php test exercises/e03
scripts/warsztat.sh --lang php next exercises/e03
scripts/warsztat.sh --lang php diff exercises/e03 0 1
scripts/warsztat.sh --lang php reset exercises/e03
```
