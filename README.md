# Refaktoryzacja do wzorców

Materiały szkoleniowe: prezentacja, warsztat CineLegacy (Java, C#, TypeScript), ćwiczenia utrwalające (Java, Kotlin, PHP) i przykłady wzorców projektowych GoF (Java).

| Co | Gdzie |
| --- | --- |
| strona główna materiałów | `src/main/resources/html/index.html` (otwórz w przeglądarce) |
| prezentacja | `src/main/resources/html/slajdy/refaktoryzacja-do-wzorcow-slajdy.html`, źródło: `src/main/resources/md/slides/` |
| przewodnik i zadania warsztatu | `src/main/resources/html/warsztat*/` |
| warsztat w Javie | `src/main/java/pl/training/workshop`, testy: `src/test/java/pl/training/workshop` |
| warsztat w C# | `csharp/` |
| warsztat w TypeScripcie | `typescript/` |
| ćwiczenia utrwalające (10 zadań, 22 wzorce GoF, Java) | `src/main/java/pl/training/workshop/exercises`, treść: `src/main/resources/md/warsztat/zadania/06-cwiczenia.md`, rozwiązania: `src/main/resources/md/warsztat/przewodnik/06-cwiczenia.md` |
| opis wzorców w ćwiczeniach: które, dlaczego i w jakich klasach (dla prowadzącego) | `CWICZENIA-WZORCE.md` |
| ćwiczenia utrwalające w Kotlinie | `kotlin/` |
| ćwiczenia utrwalające w PHP | `php/` |
| przykłady wzorców GoF | `src/main/java/pl/training/examples` |

## Wymagania

- Java 25 i Maven (wersja główna),
- .NET SDK 10 (wersja C#),
- Node.js 22 lub nowszy (wersja TypeScript i generowanie stron HTML),
- Kotlin: ten sam JDK i Maven co Java (Kotlin pobiera się jako plugin Mavena),
- PHP 8.4 lub nowszy z rozszerzeniem `bcmath` i Composer (wersja PHP).

## Warsztat CineLegacy

Każda scena ma kod wyjściowy (`start`), gotowe kolejne kroki (`step1`, `step2`, ...) i testy, które sprawdzają, że każdy krok zachowuje się tak samo. Pokaz prowadzi się skryptem `scripts/warsztat.sh` z katalogu głównego repozytorium:

```bash
scripts/warsztat.sh list m5            # lista scen i kroków modułu (m2, m3, m5)
scripts/warsztat.sh test m5/s08        # testy jednej sceny
scripts/warsztat.sh next m5/s08        # następny krok wgrany do start
scripts/warsztat.sh prev m5/s08        # poprzedni krok
scripts/warsztat.sh jump m5/s08 2      # przejście od razu do kroku 2
scripts/warsztat.sh diff m5/s08 1 2    # co zmienia krok 2 względem kroku 1 (0 = start)
scripts/warsztat.sh status m5/s08      # który krok jest teraz w start
scripts/warsztat.sh reset m5/s08       # przywrócenie start do wersji z repozytorium
```

Ćwiczenia utrwalające mają ten sam układ (`start`, `step1`, ...) i te same komendy, ale moduł nazywa się `exercises`, a sceny `e01`..`e10` (np. `scripts/warsztat.sh test exercises/e03`). Treść zadania nie zdradza wzorca: uczestnik rozpoznaje go sam, a kroki `stepN` i przewodnik są rozwiązaniem.

Ćwiczenia utrwalające mają też wersje w Kotlinie (`kotlin/`) i PHP (`php/`), np. `scripts/warsztat.sh --lang kt test exercises/e03`, `scripts/warsztat.sh --lang php next exercises/e03`. Moduły m2, m3 i m5 są tylko w Javie, C# i TypeScripcie.

Język wybiera się opcją `--lang java|cs|ts|kt|php` przed komendą, np. `scripts/warsztat.sh --lang cs test m5/s08`. Bez niej skrypt używa ostatnio wybranego języka (na początku Javy).

Wszystkie testy naraz:

```bash
mvn test                                     # Java
cd csharp && dotnet test                     # C#
cd typescript && npm ci && npm test          # TypeScript
cd kotlin && mvn test                        # Kotlin (ćwiczenia)
cd php && composer install && vendor/bin/phpunit   # PHP (ćwiczenia)
```

## Przykłady wzorców GoF

Każdy wzorzec ma klasę `Application` z metodą `main`, a warianty funkcyjne są w pakiecie `fn`:

```bash
mvn -q compile
java -cp target/classes pl.training.examples.creational.builder.Application
java -cp target/classes pl.training.examples.behavioral.strategy.Application
java -cp target/classes pl.training.examples.fn.FunctionalStyleStrategy
```

Pakiety: `creational` (wzorce konstrukcyjne), `structural` (strukturalne), `behavioral` (operacyjne), `fn` (warianty funkcyjne). Przykład można też uruchomić bezpośrednio z IDE.

## Generowanie stron HTML

Strony HTML i prezentacja powstają z plików `src/main/resources/md`:

```bash
scripts/warsztat.sh html </dev/null
```

Przekierowanie `</dev/null` jest potrzebne, gdy skrypt nie działa w zwykłym terminalu (np. w tle albo z innego skryptu), bo generator slajdów Marp czeka wtedy na dane ze standardowego wejścia.
