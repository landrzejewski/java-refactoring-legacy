# Refaktoryzacja do wzorców - materiały

Szkolenie składa się z dwóch części:

| Część | Zawartość |
| --- | --- |
| [Slajdy](slajdy/refaktoryzacja-do-wzorcow-slajdy.html) | jedna prezentacja Marp (źródło: `md/slides/refaktoryzacja-do-wzorcow-slajdy.md`), którą da się przeczytać samodzielnie: refaktoryzacja i zachowanie obserwowalne, prewencja, siatka bezpieczeństwa, a potem refaktoryzacje do wzorców i usuwanie zbędnych wzorców, z przykładami z CineLegacy |
| Warsztat CineLegacy | krótkie sceny do pokazu na żywo i do samodzielnej pracy na kodzie systemu kina, w trzech językach: Java, C# i TypeScript |

Slajdy pokazują kod w Javie. Dopisek „Warsztat: m5/sNN” na slajdzie wskazuje scenę z pełnym kodem, testami i kolejnymi krokami.

## Slajdy

| Część | Sekcje | Treść |
| --- | --- | --- |
| I. Refaktoryzacja | 1 | definicja, zachowanie obserwowalne, zapachy kodu |
| II. Zanim wprowadzisz wzorzec | 2 | małe kroki, bramki jakości, YAGNI, ADR, SOLID, reguła zależności, zasada harcerza |
| III. Siatka bezpieczeństwa | 3 | testy charakteryzujące, wynik wzorcowy, test równoważności, szwy, refaktoryzacje automatyczne w IDE, weryfikacja w cieniu |
| IV. Wzorce i decyzja | 4-5 | rodzaj zmienności, koszt wzorca, opłacalność, zmiana kontraktu, słowniczek wzorców |
| V. Refaktoryzacje do wzorców | 6-16 | Strategia, Stan, Polecenie, Metoda szablonowa, Dekorator, Łańcuch zobowiązań, Adapter, obiekt graniczny, Fabryka, Fasada, warstwa antykorupcyjna, Budowniczy, Obserwator, Mediator, Odwiedzający, Kompozyt, parametr zbierający, Singleton |
| VI. Usuwanie wzorców i podsumowanie | 17-18 | usunięcie pośrednika, wchłonięcie Singletona, wzorzec jako decyzja odwracalna, mapa decyzji, lista kontrolna |

## Warsztat CineLegacy

Pakiet praktyczny na kodzie systemu kina CineLegacy: krótkie sceny, po jednej na temat ze slajdów. Każda scena ma kod wyjściowy (`start`), gotowe snapshoty kolejnych kroków (`stepN`) i testy równoważności. Pokaz prowadzi się narzędziem `scripts/warsztat.sh` (`list`, `test`, `diff`, `next`, `prev`, `status`, `jump`, `reset`, `html`). Warsztat jest dostępny w trzech językach: Java, C# (`--lang cs`) i TypeScript (`--lang ts`).

Start (wprowadzenie, reguły domeny, macierz pokrycia tematów): [Java](warsztat/00-cinelegacy.html), [C#](warsztat-csharp/00-cinelegacy.html), [TypeScript](warsztat-typescript/00-cinelegacy.html).

| Moduł warsztatu | Sekcje slajdów | Przewodnik prowadzącego | Pakiet / namespace / katalog |
| --- | --- | --- | --- |
| 2. Prewencja | 2 | [Java](warsztat/przewodnik/02-prewencja.html), [C#](warsztat-csharp/przewodnik/02-prewencja.html), [TS](warsztat-typescript/przewodnik/02-prewencja.html) | `pl.training.workshop.m2`, `Training.Workshop.M2`, `typescript/src/workshop/m2` |
| 3. Testowanie a bezpieczeństwo | 1.2, 3 | [Java](warsztat/przewodnik/03-testowanie-a-bezpieczenstwo.html), [C#](warsztat-csharp/przewodnik/03-testowanie-a-bezpieczenstwo.html), [TS](warsztat-typescript/przewodnik/03-testowanie-a-bezpieczenstwo.html) | `pl.training.workshop.m3`, `Training.Workshop.M3`, `typescript/src/workshop/m3` |
| 5. Refaktoryzacja do wzorców | 3-17 | [Java](warsztat/przewodnik/05-refaktoryzacja-do-wzorcow.html), [C#](warsztat-csharp/przewodnik/05-refaktoryzacja-do-wzorcow.html), [TS](warsztat-typescript/przewodnik/05-refaktoryzacja-do-wzorcow.html) | `pl.training.workshop.m5`, `Training.Workshop.M5`, `typescript/src/workshop/m5` |
| 6. Ćwiczenia: gdzie i jaki wzorzec (23 wzorce GoF w 10 zadaniach) | 4, 6-18 | [Java](warsztat/przewodnik/06-cwiczenia.html) (rozwiązania; treść zadań bez nazw wzorców: `md/warsztat/zadania/06-cwiczenia.md`) | `pl.training.workshop.exercises` (tylko Java) |

Sceny to menu pokazów, z którego prowadzący wybiera tematy pasujące do grupy. Proponowany rdzeń warsztatu m5 jest opisany w przewodniku prowadzącego.

## Uruchamianie warsztatu

### Java (główny projekt, Java 25, Maven)

```bash
mvn -q compile
mvn test                                        # wszystkie testy warsztatu
scripts/warsztat.sh list m5                     # sceny i kroki
scripts/warsztat.sh test m5/s08                 # testy jednej sceny
scripts/warsztat.sh test exercises/e03          # testy jednego ćwiczenia utrwalającego
mvn verify                                      # testy + raport JaCoCo (target/site/jacoco)
```

Kod: `src/main/java/pl/training/workshop`, testy: `src/test/java/pl/training/workshop`.

Przykłady wzorców GoF (tylko Java, 23 wzorce i warianty funkcyjne) są w pakiecie `pl.training.examples` (`creational`, `structural`, `behavioral`, `fn`); każdy wzorzec ma własną klasę `Application` z metodą `main`. Testy: `src/test/java/pl/training/examples`.

### C# (.NET 10)

```bash
cd csharp
dotnet build
dotnet test
```

W skrypcie: `scripts/warsztat.sh --lang cs test m5/s08`.

### TypeScript (Node.js 22+)

```bash
cd typescript
npm ci
npm run build
npm test
```

W skrypcie: `scripts/warsztat.sh --lang ts test m5/s08`.

## Generowanie stron

Strony HTML (także ta) powstają z plików `md/` poleceniem `scripts/warsztat.sh html` (wymaga Node.js; slajdy generuje Marp CLI).
