# Refaktoryzacja i wzorce projektowe - materiały dla prowadzącego

Szkolenie trwa 3 dni i ma 6 modułów. Materiały każdego modułu są podzielone na cztery rodzaje:

| Katalog | Dla kogo | Zawartość |
| --- | --- | --- |
| `slajdy/` | prowadzący, uczestnicy | prezentacje Marp do części wykładowej (źródła w `md/slides/`) |
| `zadania/` | uczestnicy (do rozdania) | aktywności i ćwiczenia: cel, czas, dane wejściowe, polecenia, kryteria akceptacji, pytania sprawdzające - bez odpowiedzi |
| `przyklady/` | prowadzący | przewodnik po kodzie: co ilustruje każdy przykład, pliki przed/po, testy chroniące zachowanie, uruchamianie, demonstracja na żywo |
| `rozwiazania/` | prowadzący | rozwiązania wzorcowe, odniesienia do kodu referencyjnego, typowe błędy, pytania do dyskusji, odpowiedzi do pytań sprawdzających |

Kod przykładów i warsztatu jest dostępny w trzech wersjach językowych o tej samej strukturze: Java (główna), C# i TypeScript. Slajdy, zadania i rozwiązania pokazują kod w Javie i wskazują odpowiedniki w C# i TypeScript.

## Moduły

| Moduł | Czas | Slajdy | Zadania | Przykłady | Rozwiązania |
| --- | --- | --- | --- | --- | --- |
| 1. Wprowadzenie | ok. 2,5 h | [slajdy](slajdy/01-wprowadzenie-slajdy.html) | [zadania](zadania/01-wprowadzenie.html) | [przykłady](przyklady/01-wprowadzenie.html) | [rozwiązania](rozwiazania/01-wprowadzenie.html) |
| 2. Prewencja | ok. 3 h | [slajdy](slajdy/02-prewencja-slajdy.html) | [zadania](zadania/02-prewencja.html) | [przykłady](przyklady/02-prewencja.html) | [rozwiązania](rozwiazania/02-prewencja.html) |
| 3. Testowanie a bezpieczeństwo | ok. 3 h | [slajdy](slajdy/03-testowanie-a-bezpieczenstwo-slajdy.html) | [zadania](zadania/03-testowanie-a-bezpieczenstwo.html) | [przykłady](przyklady/03-testowanie-a-bezpieczenstwo.html) | [rozwiązania](rozwiazania/03-testowanie-a-bezpieczenstwo.html) |
| 4. Wzorce projektowe | ok. 4 h | [slajdy](slajdy/04-wzorce-projektowe-slajdy.html) | [zadania](zadania/04-wzorce-projektowe.html) | [przykłady](przyklady/04-wzorce-projektowe.html) | [rozwiązania](rozwiazania/04-wzorce-projektowe.html) |
| 5. Refaktoryzacja do wzorców | ok. 6 h | [slajdy](slajdy/05-refaktoryzacja-do-wzorcow-slajdy.html) | [zadania](zadania/05-refaktoryzacja-do-wzorcow.html) | [przykłady](przyklady/05-refaktoryzacja-do-wzorcow.html) | [rozwiązania](rozwiazania/05-refaktoryzacja-do-wzorcow.html) |
| 6. Podsumowanie | ok. 1 h | [slajdy](slajdy/06-podsumowanie-slajdy.html) | [zadania](zadania/06-podsumowanie.html) | - | [rozwiązania](rozwiazania/06-podsumowanie.html) |

Tematy modułów:

1. **Wprowadzenie:** czym jest refaktoryzacja, kiedy warto refaktoryzować, code smells, metryki jakości kodu.
2. **Prewencja:** fast feedback, podejmowanie decyzji i odsuwanie ich w czasie, akceptacja problemów, których nie rozwiążesz, degradacja jakości i jej kontrola przez projektowanie, prewencja ważniejsza niż leczenie.
3. **Testowanie a bezpieczeństwo:** piramida testów, Test Double Patterns, code coverage, praca bez testów.
4. **Wzorce projektowe:** definicja wzorca, wzorce kreacyjne, strukturalne i behawioralne (katalog GoF z wariantami funkcyjnymi).
5. **Refaktoryzacja do wzorców:** czy zawsze warto, kiedy pozbyć się wzorca, niebezpieczeństwa, wsparcie IDE, refaktoryzacja a testowanie, Command/Strategy/State, Strategy/Template Method, Chain of Responsibility/Decorator, Adapter i Boundary Object, Factory, Facade, Facade a Anti-Corruption Layer, Builder, Observer, Mediator, Visitor.
6. **Podsumowanie:** mapa decyzji, lista kontrolna, ćwiczenie końcowe na `CinemaManager`.

## Proponowany plan 3 dni

| Dzień | Przed południem | Po południu |
| --- | --- | --- |
| 1 | Moduł 1. Wprowadzenie | Moduł 2. Prewencja |
| 2 | Moduł 3. Testowanie a bezpieczeństwo | Moduł 4. Wzorce projektowe |
| 3 | Moduł 5. Refaktoryzacja do wzorców (tematy 1-8) | Moduł 5 (tematy 9-16), Moduł 6. Podsumowanie |

Czasy modułów są orientacyjne. Sceny warsztatu to menu pokazów, z którego prowadzący wybiera tematy pasujące do grupy.

## Warsztat CineLegacy (moduły 2, 3 i 5)

Osobny pakiet praktyczny na kodzie systemu kina CineLegacy: krótkie sceny do pokazu na żywo, po jednej na temat ze slajdów. Każda scena ma kod wyjściowy (`start`), gotowe snapshoty kolejnych kroków (`stepN`) i testy równoważności. Pokaz prowadzi się narzędziem `scripts/warsztat.sh` (`list`, `test`, `diff`, `next`, `prev`, `status`, `jump`, `reset`, `html`). Warsztat jest dostępny w trzech językach: Java, C# (`--lang cs`) i TypeScript (`--lang ts`).

Start (wprowadzenie, reguły domeny, macierz pokrycia tematów): [Java](warsztat/00-cinelegacy.html), [C#](warsztat-csharp/00-cinelegacy.html), [TypeScript](warsztat-typescript/00-cinelegacy.html).

| Moduł | Przewodnik prowadzącego | Zadania dla uczestników | Pakiet / namespace / katalog |
| --- | --- | --- | --- |
| 2. Prewencja | [Java](warsztat/przewodnik/02-prewencja.html), [C#](warsztat-csharp/przewodnik/02-prewencja.html), [TS](warsztat-typescript/przewodnik/02-prewencja.html) | [Java](warsztat/zadania/02-prewencja.html), [C#](warsztat-csharp/zadania/02-prewencja.html), [TS](warsztat-typescript/zadania/02-prewencja.html) | `pl.training.workshop.m2`, `Training.Workshop.M2`, `typescript/src/workshop/m2` |
| 3. Testowanie a bezpieczeństwo | [Java](warsztat/przewodnik/03-testowanie-a-bezpieczenstwo.html), [C#](warsztat-csharp/przewodnik/03-testowanie-a-bezpieczenstwo.html), [TS](warsztat-typescript/przewodnik/03-testowanie-a-bezpieczenstwo.html) | [Java](warsztat/zadania/03-testowanie-a-bezpieczenstwo.html), [C#](warsztat-csharp/zadania/03-testowanie-a-bezpieczenstwo.html), [TS](warsztat-typescript/zadania/03-testowanie-a-bezpieczenstwo.html) | `pl.training.workshop.m3`, `Training.Workshop.M3`, `typescript/src/workshop/m3` |
| 5. Refaktoryzacja do wzorców | [Java](warsztat/przewodnik/05-refaktoryzacja-do-wzorcow.html), [C#](warsztat-csharp/przewodnik/05-refaktoryzacja-do-wzorcow.html), [TS](warsztat-typescript/przewodnik/05-refaktoryzacja-do-wzorcow.html) | [Java](warsztat/zadania/05-refaktoryzacja-do-wzorcow.html), [C#](warsztat-csharp/zadania/05-refaktoryzacja-do-wzorcow.html), [TS](warsztat-typescript/zadania/05-refaktoryzacja-do-wzorcow.html) | `pl.training.workshop.m5`, `Training.Workshop.M5`, `typescript/src/workshop/m5` |

## Uruchamianie przykładów

Kod przykładów jest dostępny w trzech językach o tej samej strukturze modułów (`module1` ... `module5`).

### Java (główny projekt, Java 25, Maven)

```bash
mvn -q compile
java -cp target/classes pl.training.module1.Module1Examples   # dowolny moduł: moduleN.ModuleNExamples
java -cp target/classes pl.training.module4.Module4Examples all   # katalog wzorców: klucz albo all
mvn test                                                      # wszystkie testy
mvn test -Dtest='pl.training.module5.**'                      # testy jednego modułu
mvn verify                                                    # testy + raport JaCoCo (target/site/jacoco)
```

Kod: `src/main/java/pl/training/moduleN`, testy: `src/test/java/pl/training/moduleN`.

### C# (.NET 10)

```bash
cd csharp
dotnet build
dotnet test
dotnet run --project src/Training.Module1
dotnet run --project src/Training.Module4 -- all
```

### TypeScript (Node.js 22+)

```bash
cd typescript
npm ci
npm run build
npm test
npm run module1
npm run module4 -- all
```

## Generowanie stron

Strony HTML (także ta) powstają z plików `md/` poleceniem `scripts/warsztat.sh html` (wymaga Node.js; slajdy generuje Marp CLI).

## Sugerowany przebieg modułu

1. Teoria na podstawie slajdów (`slajdy/`).
2. Demonstracja przykładów z kodu według `przyklady/` (najpierw wersja wyjściowa i test, potem kolejne kroki) albo scen warsztatu CineLegacy.
3. Rozdanie `zadania/` i praca w parach lub małych grupach.
4. Omówienie według `rozwiazania/`, z porównaniem prac uczestników z kodem referencyjnym.
5. Pytania sprawdzające i lista kontrolna na zakończenie.
