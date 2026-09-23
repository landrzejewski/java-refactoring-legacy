# Moduł 8. Strategie i dobre praktyki - rozwiązania wzorcowe dla prowadzącego

Materiał wyłącznie dla prowadzącego. Nie rozdawać uczestnikom przed zakończeniem warsztatów.

Warsztaty tego modułu mają charakter projektowy: nie istnieje jedno poprawne rozwiązanie. Poniższe rozwiązania pokazują produkt, który spełnia wszystkie kryteria akceptacji, oraz wskazują kod referencyjny w projekcie, na którym można oprzeć omówienie. Prace uczestników należy oceniać według kryteriów akceptacji i listy kontrolnej (na końcu dokumentu), a nie według zgodności z tym wzorcem.

---

## Warsztat 1. Plan przyrostowej wymiany implementacji

> Rozwiązanie opracowane na podstawie kodu referencyjnego, nie pochodzi wprost z materiału teoretycznego.

### Kod referencyjny i testy

- Abstrakcja i obiekty wartości: `src/main/java/pl/training/module8/incremental/PricingEngine.java`, `PriceRequest.java`, `PriceQuote.java`
- Adapter: `src/main/java/pl/training/module8/incremental/LegacyPricingEngineAdapter.java`
- Kandydat: `src/main/java/pl/training/module8/incremental/CandidatePricingEngine.java`
- Router migracji i telemetria: `MigratingPricingEngine.java`, `MigrationMode.java`, `VerificationEvent.java`, `VerificationReporter.java` (ten sam pakiet)
- Kryteria etapów: `src/main/java/pl/training/module8/risk/RolloutPolicy.java`, `RolloutThresholds.java`, `RolloutSnapshot.java`, `RolloutDecision.java`
- Testy: `src/test/java/pl/training/module8/incremental/PricingEngineContractTest.java`, `MigratingPricingEngineTest.java`, `PricingValueObjectsTest.java`, `src/test/java/pl/training/module8/risk/RolloutPolicyTest.java`

### Przykładowy produkt: plan migracji

#### 1. Kontrakt funkcjonalny `LegacyPriceCalculator.calculate`

| Element | Zachowanie |
| --- | --- |
| Walidacja, kolejność | `unitPrice` null -> `NullPointerException("unitPrice")`; `discountPercent` null -> `NullPointerException("discountPercent")`; `unitPrice < 0` -> `IllegalArgumentException("unitPrice must not be negative")`; `quantity <= 0` -> `IllegalArgumentException("quantity must be positive")`; rabat poza `0..100` -> `IllegalArgumentException("discountPercent must be between 0 and 100")` |
| Moment walidacji | przed zaokrągleniem (np. `-0.001` jest odrzucane, a nie zamieniane na `0.00`) |
| Normalizacja | cena do skali 2, procent rabatu do skali 2, `HALF_EVEN` |
| Obliczenie | brutto = cena x ilość; rabat = brutto x procent / 100, **zaokrąglony do skali 2 przed odejmowaniem**; wynik = brutto - rabat w skali 2 |
| Wynik | `BigDecimal` w skali 2 |

Wyniki dla danych z tabeli w zadaniu:

| unitPrice | quantity | discountPercent | Wynik legacy | Uwagi |
| --- | --- | --- | --- | --- |
| `10.00` | 3 | `0` | `30.00` | |
| `10.00` | 3 | `100` | `0.00` | |
| `0.01` | 1 | `50` | `0.01` | rabat `0.005` -> `0.00` (HALF_EVEN), chroni moment zaokrąglenia |
| `19.995` | 2 | `12.555` | `34.98` | cena `20.00`, procent `12.56`, rabat `5.024` -> `5.02` |
| `0.05` | 3 | `33.33` | `0.10` | rabat `0.049995` -> `0.05` |
| `-0.001` | 1 | `0` | wyjątek | `unitPrice must not be negative` |
| `10.00` | 0 | `0` | wyjątek | `quantity must be positive` |
| `10.00` | 1 | `100.01` | wyjątek | `discountPercent must be between 0 and 100` |

Te same przypadki (po przeliczeniu procentu na stopę) są w `PricingEngineContractTest.everyProductionImplementationCalculatesCanonicalExamples`.

#### 2. Kontrakt operacyjny

- czyste obliczenie: brak I/O, brak stanu, deterministyczne (`everyProductionImplementationIsDeterministicAndReturnsMoney`),
- krytyczna ścieżka, wywołanie przy każdym zamówieniu, brak okna serwisowego,
- oczekiwanie co do opóźnienia: stan bazowy p95 zmierzony przed migracją (np. z istniejącego monitoringu); nowa ścieżka nie może go przekroczyć ponad uzgodniony próg,
- błąd obliczenia jest błędem autorytatywnym (klient ma dostać wyjątek, a nie cenę zastępczą).

#### 3. Najmniejsza abstrakcja

```java
@FunctionalInterface
public interface PricingEngine {
    PriceQuote quote(PriceRequest request);
}
```

- jedna potrzeba klienta: "podaj cenę netto dla żądania",
- `PriceRequest` przenosi rabat jako stopę `0..1` (skala 4), więc klient nie zna jednostki procentowej legacy,
- `PriceQuote` gwarantuje nieujemną kwotę w skali 2,
- abstrakcja **nie** kopiuje API starego kalkulatora (antywzorzec z 1.7).

#### 4. Adapter i test kontraktowy

- `LegacyPricingEngineAdapter` przelicza stopę na procent (`movePointRight(2)`) i opakowuje wynik w `PriceQuote`.
- Wspólny test kontraktowy uruchamiany dla każdej implementacji (sparametryzowany dostawcą implementacji): przykłady kanoniczne z tabeli, determinizm i skala wyniku, odrzucenie `null` z komunikatem `request`.
- Dodatkowo testy obiektów wartości (walidacja przed zaokrągleniem, `PricingValueObjectsTest`).

#### 5. Czy shadow jest bezpieczny?

Tak, pod warunkami:

- obie implementacje są czystym obliczeniem (zapisane w kontrakcie `PricingEngine`), więc podwójne wykonanie nie powiela efektów,
- legacy jest autorytatywne: liczone pierwsze; jego wyjątek trafia do klienta, a kandydat nie jest wywoływany,
- wyjątek kandydata albo `null` od kandydata zamienia się w zdarzenie `CandidateFailure`, odpowiedź pozostaje z legacy,
- awaria reportera nie zmienia odpowiedzi (brak gwarancji dostarczenia zdarzenia, więc potrzebny osobny sygnał sprawności kanału telemetrii),
- błędy JVM (`Error`) nie są maskowane,
- koszt: dodatkowa latencja i CPU. W produkcji kandydat poza ścieżką odpowiedzi albo z timeoutem, bulkheadem i limitem kopiowanego ruchu (np. próbkowanie 10% żądań w trybie weryfikacji).

Zastrzeżenie do zapisania w planie: zgodność nie dowodzi poprawności, bo obie wersje mogą mieć ten sam błąd. Dlatego test kontraktowy ma niezależnie wyliczone wartości.

#### 6. Tryby, etapy i kohorty

Tryby (`MigrationMode`): `LEGACY` -> `VERIFY` -> `CANDIDATE`. Enum, nie boolean, bo są trzy stany.

| Etap | Tryb | Kohorta | Minimalny czas obserwacji |
| --- | --- | --- | --- |
| 0 | `LEGACY` | 100% (stan bazowy metryk) | do zebrania stanu bazowego |
| 1 | `VERIFY` | ruch wewnętrzny / testowy | np. 1 dzień roboczy |
| 2 | `VERIFY` | próbka ruchu produkcyjnego | np. pełny cykl tygodniowy (w tym szczyty) |
| 3 | `CANDIDATE` | 1% klientów | np. 2 dni |
| 4 | `CANDIDATE` | 10%, potem 50% | np. 2-3 dni na etap |
| 5 | `CANDIDATE` | 100% | uzgodniony okres przed usunięciem legacy |

Determinizm i obserwowalność routingu:

- przypisanie do kohorty przez stabilny klucz (np. hash identyfikatora klienta modulo 100), a nie losowanie per żądanie,
- decyzja o trybie w jednym miejscu (router `MigratingPricingEngine` składany w jednym miejscu kompozycji), nie rozrzucona po kodzie domenowym,
- każda odpowiedź oznaczona metryką / logiem "która ścieżka odpowiedziała" i "jaka kohorta",
- zdarzenia `Agreement` / `Divergence` / `CandidateFailure` zliczane jako metryki, rozbieżności logowane z żądaniem (dane do analizy).

#### 7. Kryteria `ADVANCE`, `HOLD`, `ROLLBACK`

Progi ustalone **przed** etapem (przykład zgodny z `RolloutPolicyTest`): minimalna próbka 100, maksymalny wskaźnik błędów 5%, p95 do 250 ms, liczba rozbieżności 0 (obliczenie finansowe po ustalonej normalizacji).

Reguła, w tej kolejności:

1. `ROLLBACK`, gdy: jakakolwiek rozbieżność, wskaźnik błędów powyżej progu albo p95 powyżej progu (bez względu na wielkość próbki),
2. `HOLD`, gdy próbka mniejsza niż minimum,
3. `ADVANCE` w pozostałych przypadkach (progi są włącznie).

Właściciel decyzji: wskazana osoba z zespołu właściciela kalkulatora; kanał eskalacji: dyżur zespołu. W `VERIFY` "rollback" oznacza zatrzymanie etapu i analizę rozbieżności (odpowiedzi nadal z legacy); w `CANDIDATE` oznacza powrót konfiguracji do `LEGACY` lub `VERIFY`.

#### 8. Wycofanie

- kod: stara ścieżka pozostaje wdrożona do końca okresu z etapu 5,
- konfiguracja: zmiana trybu bez nowego wdrożenia, przećwiczona na etapie 1 (czas powrotu zmierzony),
- dane: obliczenie nie zapisuje stanu, ale ceny trafiają do zamówień; plan musi opisać, jak zidentyfikować zamówienia wycenione przez kandydata (znacznik ścieżki w logu lub zamówieniu) na wypadek korekt,
- żądania w toku: przełączenie trybu nie przerywa już rozpoczętych obliczeń.

#### 9. Elementy przejściowe

| Element | Właściciel | Kryterium usunięcia | Termin przeglądu |
| --- | --- | --- | --- |
| `LegacyPricingEngineAdapter` + `LegacyPriceCalculator` | zespół cennika | 100% w `CANDIDATE` przez uzgodniony okres, brak niewyjaśnionych rozbieżności, brak bezpośrednich wywołań starego API | koniec etapu 5 |
| router `MigratingPricingEngine` i konfiguracja trybu | zespół cennika | jak wyżej; klient używa bezpośrednio docelowej implementacji | razem z adapterem |
| reporter i metryki weryfikacji, dashboard | zespół cennika + operacje | usunięcie trybu `VERIFY` | razem z routerem |
| przypisanie do kohort | zespół cennika | 100% ruchu na nowej ścieżce | razem z routerem |

Migracja jest zakończona dopiero po usunięciu tych elementów i aktualizacji dokumentacji stanu obecnego (6.6).

#### 10. Klasyfikacja kroków

| Krok | Rodzaj |
| --- | --- |
| testy charakteryzujące starego kalkulatora | brak zmiany kodu produkcyjnego |
| `PricingEngine`, `PriceRequest`, `PriceQuote`, adapter, przekierowanie wywołań | refaktoryzacja |
| test kontraktowy | test |
| `CandidatePricingEngine`, router, tryby, telemetria | migracja (nowy dostawca, bez zmiany zachowania dla klienta) |
| przełączenie kohort | migracja (wdrożenie etapowe) |
| usunięcie legacy | refaktoryzacja porządkująca (usunięcie martwej ścieżki) |
| ewentualna zmiana reguły zaokrąglania | zmiana zachowania, osobny zestaw zmian po zakończeniu migracji |

### Jak spełnić kryteria akceptacji

| Kryterium | Co musi być w planie |
| --- | --- |
| rozróżnia refaktoryzację od migracji i zmiany zachowania | tabela klasyfikacji kroków (punkt 10); kandydat odtwarza moment zaokrąglenia, a ewentualna "naprawa" jest osobną zmianą |
| kandydat nie powiela nieidempotentnych efektów | uzasadnienie w punkcie 5 (czyste obliczenie); informacja, że dla operacji z efektami porównuje się decyzję przed efektem albo używa środowiska izolowanego |
| obie implementacje przechodzą wspólny test kontraktowy | jeden zestaw przypadków sparametryzowany implementacją (wzór: `PricingEngineContractTest`) |
| routing deterministyczny i obserwowalny | stabilny klucz kohorty, jedno miejsce decyzji, metryki ścieżki i zdarzeń weryfikacji |
| wycofanie uwzględnia kod, konfigurację i dane | punkt 8 |
| każdy element przejściowy ma właściciela i kryterium usunięcia | tabela w punkcie 9 |

Opcjonalna część kodowa: poprawne rozwiązanie odpowiada klasom `PricingEngine`, `LegacyPricingEngineAdapter` i testowi `PricingEngineContractTest` z pakietu referencyjnego.

### Typowe błędy uczestników

- abstrakcja kopiująca API legacy (`calculate(BigDecimal, int, BigDecimal discountPercent)`), więc jednostka procentowa "przecieka" do nowego kodu,
- kandydat zaokrągla dopiero wynik końcowy; przypadek `0.01, 1, 50%` daje wtedy `0.00` zamiast `0.01` (jeśli uczestnicy nie mieli takiego przypadku w teście, nie zauważą różnicy),
- walidacja po zaokrągleniu (`-0.001` staje się `0.00` i przechodzi),
- boolean `useNewCalculator` zamiast trzech trybów; brak etapu weryfikacji,
- w trybie weryfikacji odpowiedź z kandydata albo wyjątek kandydata propagowany do klienta,
- losowy wybór ścieżki per żądanie (brak powtarzalności, trudna diagnoza),
- progi "ustalimy po pierwszym tygodniu" albo tolerancja różnic w cenie,
- `HOLD` traktowany jako sukces albo brak `ROLLBACK` dla małej próbki z rozbieżnością,
- plan kończy się na "100% ruchu", bez usunięcia starej ścieżki i właścicieli,
- wycofanie opisane tylko jako "revert commita".

### Pytania do dyskusji

1. Kiedy ograniczone przepisanie tego kalkulatora byłoby rozsądniejsze niż Branch by Abstraction? (tabela 1.2: mały zakres, znany kontrakt, brak stanu)
2. Czym różni się Branch by Abstraction od Strangler Fig i kiedy wybralibyście drugie?
3. Co zmienia się w planie, jeśli zamiast obliczenia migrujemy autoryzację płatności?
4. Obie implementacje zgodnie zwracają ten sam wynik. Czy to dowód poprawności?
5. Kandydat jest 3 razy wolniejszy, ale w granicy SLO. Czy przechodzimy dalej?
6. Kto i kiedy faktycznie usunie adapter? Jak to wymusić?

---

## Warsztat 2. Seria zmian gotowych do przeglądu

> Rozwiązanie opracowane na podstawie kodu referencyjnego, nie pochodzi wprost z materiału teoretycznego.

### Kod referencyjny i testy

- Wejście: `src/main/java/pl/training/module8/boyscout/before/ReleaseSummaryFormatter.java`
- Wzór refaktoryzacji bez zmiany zachowania: `src/main/java/pl/training/module8/boyscout/after/ReleaseSummaryFormatter.java`
- Wzór testów równoważności i orakla: `src/test/java/pl/training/module8/boyscout/BoyScoutEquivalenceTest.java`
- Słownik intencji i polityka gotowości: `src/main/java/pl/training/module8/collaboration/ChangeIntent.java`, `ExampleTeamReviewPolicy.java`, test `src/test/java/pl/training/module8/collaboration/ExampleTeamReviewPolicyTest.java`

Uwaga: nowa reguła raportu (`Failed: n/m`) jest wymaganiem wymyślonym na potrzeby warsztatu i nie ma odpowiednika w kodzie referencyjnym. Kod referencyjny obejmuje tylko kroki 1-2.

### Przykładowy produkt: seria zestawów zmian

#### Zestaw 1. Testy charakteryzujące (intencja: `CHARACTERIZATION_TESTS`)

Commit: `Characterize ReleaseSummaryFormatter output and validation order`

Zawartość: wyłącznie testy, bez zmian w kodzie produkcyjnym.

- wynik dla sukcesu, porażki i przypadku mieszanego, w tym `releaseId` ze spacjami (`" release-3 "` -> `Release release-3`),
- niezależny orakl tekstu, np.:

```text
Release release-42
[OK] test: deployed
[ERROR] production: timeout
Successful: 1/2
```

- brak końcowego znaku nowej linii po ostatnim wierszu,
- walidacja i jej kolejność (typ i komunikat):

| Wejście | Wyjątek |
| --- | --- |
| `releaseId = null` | `NullPointerException("releaseId")` |
| `results = null` | `NullPointerException("results")` |
| `releaseId = "   "` | `IllegalArgumentException("releaseId must not be blank")` |
| `results = List.of()` | `IllegalArgumentException("results must not be empty")` |
| lista z elementem `null` | `NullPointerException("result")` |
| `(" ", null)` | `NullPointerException("results")` - kontrole null przed kontrolą pustego napisu |

Decyzje o testach (do zapisania w opisie):

- zachowanie zamierzone: format wierszy, licznik sukcesów, `strip` identyfikatora, komunikaty walidacji (klienci mogą na nich polegać),
- zachowanie potencjalnie przypadkowe: dokładna kolejność kontroli dla kombinacji błędów. Utrwalamy ją, bo refaktoryzacja ma być czysta, ale zaznaczamy w opisie, że test `(" ", null)` chroni kolejność, a nie wymaganie biznesowe. Zespół może później świadomie ją zmienić.
- `null` element listy w środku: pierwsze elementy są już przetworzone, zanim poleci wyjątek; nie ma efektów ubocznych, więc nie ma to znaczenia dla klienta.

Dowód: `mvn test -Dtest='pl.training.module8.workshop2.**'` zielone na niezmienionym kodzie.

#### Zestaw 2. Refaktoryzacja (intencja: `REFACTORING`)

Seria małych commitów w jednym zestawie (lub kilka zestawów, jeśli zespół woli):

1. `Rename local variables in ReleaseSummaryFormatter` (`s` -> `summary`, `n` -> `successfulDeployments`, `r` -> `result`) - zmiana IDE,
2. `Extract validation into validate()` - bez zmiany kolejności kontroli,
3. `Extract formatResult() to remove duplicated line format`,
4. `Build summary with StringBuilder`.

Wynik odpowiada `boyscout/after/ReleaseSummaryFormatter.java`. Po każdym commicie: kompilacja, testy z zestawu 1, przegląd diffu. Sygnatura publiczna bez zmian, brak nowej walidacji.

Opcjonalny zestaw mechaniczny (jeśli zespół formatował plik): `Apply formatter to ReleaseSummaryFormatter` jako osobny commit bez żadnych ręcznych zmian, najlepiej przed zestawem 2.

#### Zestaw 3. Zmiana zachowania (intencja: `BEHAVIOR_CHANGE`)

Commit: `Show failed deployment count in release summary`

- zmiana ostatniego wiersza na `Successful: 1/2, Failed: 1/2`,
- aktualizacja orakla w teście z zestawu 1 (świadoma zmiana oczekiwania, widoczna w diffie jako jedyna zmiana testu),
- nowy test dla przypadków brzegowych (same sukcesy: `Failed: 0/n`, same porażki),
- testy walidacji bez zmian (dowód, że kontrakt wejścia nie został naruszony).

W zależności od konsumentów raportu (parsery, alerty) może być potrzebny zestaw 4 `ROLLOUT` (np. włączenie nowego formatu dla wybranych odbiorców). Jeśli raport czyta tylko człowiek, wystarczy notatka w opisie.

#### Przykładowy opis zestawu 2

```markdown
## Dlaczego
Klasa ReleaseSummaryFormatter jest trudna do zmiany: nazwy s/n/r, powielony
format wiersza, konkatenacja w pętli. Przygotowuje grunt pod zmianę formatu
podsumowania (osobny PR).

## Co zmienia / czego nie zmienia
Zmienia: nazwy lokalne, wydzielone validate() i formatResult(), StringBuilder.
Nie zmienia: sygnatury format(...), tekstu wyniku, walidacji, kolejności kontroli.

## Zachowywany kontrakt
Tekst raportu i typy/komunikaty wyjątków, w tym kolejność kontroli
(test dla wejścia (" ", null)).

## Dowody
- mvn test -Dtest='pl.training.module8.workshop2.**': wszystkie testy zielone
  (testy charakteryzujące z PR #1 bez zmian)
- diff testów: brak zmian

## Wdrożenie i wycofanie
Brak zmiany zachowania; zwykłe wdrożenie. Wycofanie: revert PR.

## Kolejność czytania
1. testy (niezmienione) 2. validate() 3. formatResult() 4. format()
```

Kolejność czytania dla recenzenta całej serii: zestaw 1 (testy, czyli kontrakt), zestaw 2 (struktura, testy bez zmian), zestaw 3 (zmiana orakla i nowa reguła).

#### Przykładowe komentarze z przeglądu

- `BLOCKER`: "W `validate()` sprawdzenie `releaseId.isBlank()` jest przed `requireNonNull(results)`. Dla wejścia `(" ", null)` klient dostanie teraz `IllegalArgumentException` zamiast `NullPointerException`. To zmiana kontraktu w PR oznaczonym jako refaktoryzacja. Proszę przywrócić kolejność albo przenieść zmianę do osobnego PR z uzasadnieniem."
- `BLOCKER`: "Ten PR zmienia jednocześnie strukturę i format ostatniego wiersza. Nie da się ocenić, czy refaktoryzacja zachowała zachowanie. Proponuję rozdzielić na dwa PR."
- `SUGGESTION`: "`formatResult` mogłoby przyjmować etykietę jako parametr, ale przy dwóch statusach obecna wersja jest czytelniejsza. Zostawiam do decyzji autora."
- `NIT`: "Nazwa testu `test1` nie mówi, co chroni. Może `preservesValidationOrderForBlankIdAndNullResults`?"

### Jak spełnić kryteria akceptacji

| Kryterium | Jak sprawdzić |
| --- | --- |
| każdy zestaw zmian ma jedną główną intencję | każdy zestaw da się opisać jedną wartością `ChangeIntent`; polityka `ExampleTeamReviewPolicy` nie zgłasza `MIXED_PRIMARY_INTENTS` |
| system działa po integracji każdego kroku | w opisie każdego zestawu jest polecenie testów i wynik; `git checkout` dowolnego commita kompiluje się i przechodzi testy |
| testy nie utrwalają bezkrytycznie przypadkowego zachowania | w zestawie 1 jest decyzja: zamierzone vs przypadkowe (np. kolejność kontroli dla kombinacji błędów) |
| mechaniczne i ręczne zmiany są rozdzielone | formatowanie i rename z IDE w osobnych commitach, bez ręcznych poprawek |
| opis pozwala odtworzyć sposób weryfikacji | konkretne polecenia, a nie "testy przeszły" |
| komentarze rozróżniają blokadę od sugestii | etykiety `BLOCKER` / `SUGGESTION` / `NIT` z obserwacją, skutkiem, uzasadnieniem i propozycją |

### Typowe błędy uczestników

- zestaw 1 zawiera już pierwsze zmiany w kodzie produkcyjnym ("tylko nazwy"),
- refaktoryzacja "przy okazji" dodaje walidację (np. odrzucanie pustego `environment` w formaterze, choć robi to już `DeploymentResult`) albo zmienia kolejność kontroli,
- zmiana orakla testu i zmiana formatu w zestawie oznaczonym jako refaktoryzacja,
- testy porównujące tylko "nie rzuca wyjątku" albo tylko `contains("Successful")`, bez pełnego tekstu,
- test różnicowy starej i nowej wersji bez niezależnego orakla,
- opis "refactor + feature, testy zielone",
- przeformatowanie całego pliku razem z logiką,
- komentarze z przeglądu dotyczące osoby ("znowu...") albo bez propozycji.

### Pytania do dyskusji

1. Kiedy mała zmiana nazwy może zostać w zestawie ze zmianą zachowania? (3.6: wyjątek dla bardzo małego lokalnego uporządkowania)
2. Czy kolejność kontroli walidacji dla kombinacji błędów jest kontraktem? Kto o tym decyduje?
3. Czy polityka typu `ExampleTeamReviewPolicy` powinna blokować merge automatycznie? Czego nie sprawdza?
4. Pięćset linii automatycznego rename kontra trzydzieści linii zmieniających schemat, regułę i uprawnienia: co jest "mniejszą" zmianą?
5. Kto przegląda testy charakteryzujące i na co patrzy?

---

## Warsztat 3. Wprowadzenie bramek do projektu legacy

> Rozwiązanie opracowane na podstawie kodu referencyjnego, nie pochodzi wprost z materiału teoretycznego.

### Kod referencyjny i testy

- `pom.xml` (stan wyjściowy: `maven.compiler.release` = `25`, `maven-compiler-plugin` bez dodatkowej konfiguracji, brak analizatorów statycznych, JaCoCo)
- `src/main/java/pl/training/module8/tooling/InMemoryJavaCompiler.java`, `WarningPolicy.java`, `CompilationResult.java`, `CompilationDiagnostic.java`
- `src/test/java/pl/training/module8/tooling/InMemoryJavaCompilerTest.java`

### Przykładowy produkt

#### 1. Stan środowiska

- `pom.xml` ustawia `--release 25` (poziom języka, format klas, API platformy). To nie gwarantuje JDK 25: na maszynie, na której przygotowano ten materiał, `mvn -v` pokazywał JDK 26. Build działa, ale CI powinno jawnie kontrolować JDK (Maven Toolchains lub obraz/środowisko CI z przypiętym JDK 25) i np. `maven-enforcer-plugin` z regułą wersji Javy.
- Przykład `InMemoryJavaCompiler` wymaga JDK (nie JRE) i używa systemowego `javac`.

#### 2. Pomiar (bez zmian w kodzie)

Polecenia:

```bash
javac --release 25 -Xlint:all -d <tmp>/main $(find src/main/java -name '*.java')
mvn -q dependency:build-classpath -Dmdep.outputFile=<tmp>/cp.txt
javac --release 25 -Xlint:all -cp "$(cat <tmp>/cp.txt):<tmp>/main" \
      -d <tmp>/test $(find src/test/java -name '*.java')
```

Wynik zmierzony przy przygotowaniu materiału (JDK 26 z `--release 25`; na innym JDK liczby mogą się różnić, uczestnicy muszą zmierzyć sami):

| Zakres | Kategoria | Liczba | Pliki |
| --- | --- | --- | --- |
| `src/main/java` | `auxiliaryclass` | 10 | `pl/training/module1/Module1Examples.java` (klasy pomocnicze z `LegacyOrderService.java` i `RiskClassifier.java` używane spoza pliku) |
| `src/test/java` | `auxiliaryclass` | 10 | `pl/training/module1/LegacyOrderServiceTest.java` (6), `pl/training/module1/RiskClassifierTest.java` (4) |
| pozostałe kategorie `-Xlint:all` | - | 0 | - |

Wniosek: przy obecnym `pom.xml` włączenie `-Xlint:all -Werror` zablokowałoby build z powodu 20 historycznych ostrzeżeń w module 1, choć żadne z nich nie jest związane z bieżącą pracą.

Analiza statyczna (Checkstyle, PMD, SpotBugs): nie ma jej w projekcie, więc uczestnicy dodają wybrane narzędzie w trybie raportowania (bez `failOnViolation`), w wersji, której dokumentacja potwierdza obsługę składni i kodu bajtowego Javy 25. SpotBugs uruchamiany po kompilacji (analizuje bytecode), z pełną ścieżką klas zależności; PMD z ustawioną wersją języka i ścieżką klas (aux classpath).

#### 3. Eksperyment z polityką (`InMemoryJavaCompiler`)

Dla źródła z `List names` (surowy typ):

| Polityka | `successful()` | Diagnostyki |
| --- | --- | --- |
| `ALLOW_WARNINGS` | `true` | `WARNING compiler.warn.raw.class.use` |
| `TREAT_WARNINGS_AS_ERRORS` | `false` | `WARNING compiler.warn.raw.class.use` oraz `ERROR compiler.err.warnings.and.werror` |

Dokładnie to sprawdzają `reportsRawTypeByKindAndCompilerCodeWithoutRejectingSource` i `rejectsTheSameRawTypeWhenWarningsAreErrors`. Wniosek: ostrzeżenie jest tym samym sygnałem, polityka decyduje, czy blokuje. Kody diagnostyk są związane z `javac` danej wersji.

#### 4. Klasyfikacja

| Kategoria | Ryzyko | Pewność | Decyzja |
| --- | --- | --- | --- |
| `auxiliaryclass` (module1) | niskie (dotyczy organizacji plików; może utrudnić kompilację przyrostową i nawigację) | prawdziwy problem, ale świadomie zaakceptowany w materiale szkoleniowym (klasy legacy celowo w jednym pliku) | stan bazowy, tłumienie wąskie z terminem przeglądu |
| `rawtypes`, `unchecked` (jeśli się pojawią w nowym kodzie) | średnie/wysokie | zwykle prawdziwy problem | blokujące od razu |
| wyniki analizatora dotyczące np. nazw w kodzie testów | niskie | często reguła niedopasowana do polityki projektu | zmiana konfiguracji reguły, nie tłumienie w kodzie |
| wynik SpotBugs typu "możliwy null" przy polu ustawianym przez framework | zależnie | wynik fałszywie dodatni | tłumienie konkretnej reguły z uzasadnieniem |

Rozróżnienie wymagane w kryteriach: **wynik fałszywie dodatni** (narzędzie się myli), **reguła niedopasowana** (zmienić konfigurację), **zaakceptowane ryzyko** (prawdziwy problem, świadomie pozostawiony na określony czas, z właścicielem).

#### 5. Stan bazowy

- plik w repozytorium, np. `quality/baseline/javac-warnings.txt` (lista `plik:kategoria:liczba`) oraz pliki bazowe analizatorów (np. filtr wykluczeń SpotBugs, plik tłumień Checkstyle),
- zmiana pliku bazowego tylko przez przegląd właściciela jakości (CODEOWNERS),
- zasada: stan bazowy może tylko maleć.

#### 6. Bramka

Dla kompilatora: blokujące ostrzeżenia dla wszystkich kategorii poza historyczną, która ma plan usunięcia:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.15.0</version>
    <configuration>
        <showWarnings>true</showWarnings>
        <compilerArgs>
            <!-- auxiliaryclass: stan bazowy module1, wlasciciel: zespol szkolenia, przeglad: <data> -->
            <arg>-Xlint:all,-auxiliaryclass</arg>
            <arg>-Werror</arg>
        </compilerArgs>
    </configuration>
</plugin>
```

- nowe ostrzeżenia każdej innej kategorii od razu blokują build (także w nowym i dotykanym kodzie),
- wyłączenie kategorii jest tymczasowe i udokumentowane; alternatywą bez wyłączania kategorii jest usunięcie przyczyny w osobnym zestawie zmian (przeniesienie klas pomocniczych modułu 1 do własnych plików), ale to refaktoryzacja materiału legacy, którą trzeba uzgodnić,
- dla analizatorów: tryb ratchet, czyli porównanie z plikiem bazowym (nowe naruszenia blokują, istniejące raportowane) lub blokada tylko dla plików zmienionych w danym PR,
- build nie uruchamia automatycznych poprawek (formatowanie w trybie `check`, a nie `apply`); ewentualne automatyczne formatowanie jako osobna zmiana mechaniczna.

#### 7. Format tłumienia

```java
// Zgloszenie: QUAL-123, wlasciciel: zespol szkolenia, przeglad: 2026-12-31
// Powod: klasy pomocnicze legacy celowo w jednym pliku (material modulu 1)
@SuppressWarnings("auxiliaryclass")
```

Zasady: konkretna reguła (nigdy `"all"`), najmniejszy zakres (metoda lub klasa, nie pakiet), uzasadnienie, właściciel, termin przeglądu. Analogicznie w filtrach SpotBugs i plikach tłumień Checkstyle: konkretna klasa i konkretny wzorzec.

Uwaga dla prowadzącego: `javac` pozwala tłumić przez `@SuppressWarnings` tylko wybrane kategorie; czy dana kategoria jest tłumiona adnotacją, trzeba sprawdzić dla używanego JDK. Jeżeli nie, pozostaje wyłączenie kategorii w konfiguracji kompilacji (jak wyżej) z tym samym opisem.

#### 8. Plan redukcji

1. Sprint 1: bramka `-Werror` z wyłączeniem `auxiliaryclass`; analizatory w trybie raportu, zebranie stanu bazowego.
2. Sprint 2: ratchet dla analizatorów (nowe naruszenia blokują).
3. Sprint 3: usunięcie `auxiliaryclass` z modułu 1 (osobny zestaw zmian refaktoryzacyjnych, testy modułu 1 jako dowód) i usunięcie wyłączenia z `pom.xml`.
4. Kolejne sprinty: redukcja stanu bazowego analizatorów od kategorii wysokiego ryzyka; przegląd tłumień przy każdym terminie.

#### 9. CI i właściciel

- raporty (`target/` analizatorów, log ostrzeżeń kompilatora) publikowane jako artefakty builda i komentarz w PR,
- bramka wymagana w CI niezależnie od wyniku lokalnego,
- właściciel: wskazana osoba lub zespół odpowiadający za jakość builda; przegląd stanu bazowego co sprint.

### Jak spełnić kryteria akceptacji

| Kryterium | Dowód w pracy |
| --- | --- |
| wersje narzędzi obsługują Javę 25 | wskazane wersje i odniesienie do ich dokumentacji; kontrola faktycznego JDK w CI (toolchains / enforcer) |
| analiza otrzymuje kompletną ścieżkę klas | analizator uruchamiany przez Maven po kompilacji albo z jawnie przekazaną ścieżką klas zależności (jak w poleceniu z `dependency:build-classpath`) |
| build nie generuje niezwiązanych automatycznych poprawek | narzędzia w trybie sprawdzania; brak `apply`/`fix` w buildzie |
| fałszywie dodatni odróżniony od zaakceptowanego ryzyka | tabela klasyfikacji z trzema kategoriami |
| nowe naruszenia nie powiększają stanu bazowego | `-Werror` dla nowych kategorii, ratchet dla analizatorów, plik bazowy tylko maleje |
| raport dostępny w CI i ma właściciela | punkt 9 |

### Typowe błędy uczestników

- "włączamy `-Werror` od jutra" bez pomiaru, co blokuje cały zespół,
- `@SuppressWarnings("all")` albo wyłączenie reguły dla całego pakietu,
- uznanie `maven.compiler.release=25` za dowód, że build działa na JDK 25,
- SpotBugs lub PMD bez pełnej ścieżki klas (dużo fałszywych wyników albo pominięte problemy),
- formatowanie całego repozytorium w tym samym zestawie co konfiguracja bramek,
- brak rozróżnienia "narzędzie się myli" od "wiemy, ale akceptujemy",
- stan bazowy, który może rosnąć ("dopiszemy nowe naruszenie do baseline"),
- raport tylko lokalny, bez właściciela.

### Pytania do dyskusji

1. Co jest lepsze dla tego projektu: wyłączyć kategorię `auxiliaryclass` czy od razu przenieść klasy do osobnych plików? Kto o tym decyduje?
2. Czy testy sprawdzające kody `javac` (`compiler.warn.raw.class.use`) to dobra praktyka? Co się stanie przy zmianie JDK?
3. Które narzędzie złapie jaki problem (tabela 5.1)? Czy potrzebujemy wszystkich trzech analizatorów?
4. Jak odróżnić ratchet od "wiecznego baseline"?
5. Czego nie widzi refaktoryzacja IDE w tym projekcie? (refleksja, konfiguracja, klienci spoza repozytorium)

---

## Aktywność dodatkowa A. ADR

> Rozwiązanie opracowane na podstawie kodu referencyjnego, nie pochodzi wprost z materiału teoretycznego.

Wzór struktury: `src/test/java/pl/training/module8/documentation/DecisionRecordMarkdownRendererTest.java` (pełny oczekiwany dokument), model: `src/main/java/pl/training/module8/documentation/DecisionRecord.java`.

Przykład:

```markdown
# ADR-0042: Wymiana kalkulatora ceny przez Branch by Abstraction

## Status

Zaakceptowana

## Kontekst

Kalkulator ceny jest na krytycznej ścieżce zamówienia, nie ma okna serwisowego,
a reguły zaokrąglania nie są w pełni udokumentowane.

## Decyzja

Wprowadzamy interfejs PricingEngine, stary kalkulator umieszczamy za adapterem,
a nową implementację weryfikujemy w trybie VERIFY przed etapowym przełączeniem.

## Rozważone opcje

1. **Branch by Abstraction**: pozwala weryfikować przyrostowo i zatrzymać się po każdym etapie.
2. **Big bang**: usuwa współistnienie, ale opóźnia informację zwrotną do dnia przełączenia.

## Konsekwencje

- **Pozytywna**: wdrożenie można zatrzymać po każdym etapie.
- **Negatywna**: dwie implementacje współistnieją tymczasowo; wymagany właściciel i termin usunięcia.

## Metoda weryfikacji

Wspólny test kontraktowy dla obu implementacji, porównanie wyników w trybie VERIFY
przed przełączeniem ruchu, kryteria ADVANCE/HOLD/ROLLBACK dla każdego etapu.
```

Na co patrzeć: konkretny kontekst (a nie "chcemy lepszy kod"), co najmniej dwie realne opcje, uczciwe koszty, weryfikowalna metoda. Odpowiedź na pytanie 3: przy zmianie kierunku ten ADR dostaje status "Zastąpiona" z odwołaniem do nowego ADR; nie edytuje się go wstecz.

Typowe błędy: ADR dla drobnej refaktoryzacji; tylko pozytywne konsekwencje; opcja "nic nie robić" pominięta bez uzasadnienia; ADR jako instrukcja operacyjna (mieszanie dokumentacji historycznej i żywej, 4.5).

---

## Aktywność dodatkowa B. Decyzja o kolejnym etapie

> Rozwiązanie opracowane na podstawie kodu referencyjnego, nie pochodzi wprost z materiału teoretycznego.

Kod: `src/main/java/pl/training/module8/risk/RolloutPolicy.java`; wszystkie przypadki są w `src/test/java/pl/training/module8/risk/RolloutPolicyTest.java`.

| Etap | Decyzja | Uzasadnienie |
| --- | --- | --- |
| a (200, 4, 0, 180.0) | `ADVANCE` | 2% błędów, p95 w progu, próbka wystarczająca |
| b (100, 5, 0, 250.0) | `ADVANCE` | progi włącznie: 5% i 250 ms są dopuszczalne, próbka równa minimum |
| c (99, 0, 0, 120.0) | `HOLD` | zdrowa, ale za mała próbka |
| d (0, 0, 0, 0.0) | `HOLD` | brak danych; wskaźnik błędów pustej próbki zdefiniowany jako 0 |
| e (200, 0, 1, 120.0) | `ROLLBACK` | jakakolwiek rozbieżność przy dokładnym kontrakcie |
| f (100, 6, 0, 200.0) | `ROLLBACK` | 6% błędów powyżej 5% |
| g (10, 0, 0, 251.0) | `ROLLBACK` | p95 powyżej progu; naruszenie ma pierwszeństwo przed małą próbką |
| h (10, 1, 0, 100.0) | `ROLLBACK` | 10% błędów; mała próbka z naruszeniem to nie "zdrowa próbka" |

Reguła kolejności: najpierw warunki bezpieczeństwa (rozbieżności, błędy, latencja) -> `ROLLBACK`; potem wielkość próbki -> `HOLD`; w przeciwnym razie `ADVANCE`.

Pytanie o system rekomendacji: progi nie są uniwersalne. Tam zero rozbieżności jest nierealne (system probabilistyczny); potrzebny jest uzgodniony przedział i inne metryki (np. jakość biznesowa), ustalone przed wdrożeniem.

Typowe błędy: `HOLD` dla etapów g i h ("za mało danych"), `ROLLBACK` dla b (progi rozumiane jako ostre), `ADVANCE` dla d.

---

## Lista kontrolna do oceny prac uczestników

Lista pochodzi z teorii modułu. Przy ocenie warsztatu zaznacz, które pytania praca adresuje; brak odpowiedzi na pytanie powiązane z danym warsztatem to temat do omówienia.

### Strategia (W1, aktywność A)

- [ ] Czy cel modernizacji jest mierzalny?
- [ ] Czy rodzaj zmiany został nazwany poprawnie?
- [ ] Czy można zmniejszyć granicę pierwszego przyrostu?
- [ ] Czy stara i nowa ścieżka mogą bezpiecznie współistnieć?
- [ ] Czy architektura przejściowa ma kryterium usunięcia?

### Zespół i przegląd (W2)

- [ ] Czy zestaw zmian ma jedną główną intencję?
- [ ] Czy zachowuje działający build?
- [ ] Czy testy stanowią wiarygodny dowód, a nie tylko pokrycie linii?
- [ ] Czy opis zawiera zakres, elementy poza zakresem, wdrożenie etapowe i wycofanie?
- [ ] Czy właściwi właściciele zostali włączeni odpowiednio wcześnie?

### Dokumentacja (W2, aktywność A)

- [ ] Czy informacja znajduje się w artefakcie o właściwej trwałości?
- [ ] Czy komentarze wyjaśniają przyczynę, a nie historię edycji?
- [ ] Czy istotna decyzja zawiera alternatywy i konsekwencje?
- [ ] Czy dokumentacja operacyjna opisuje stan obecny?
- [ ] Czy decyzja zastąpiona pozostaje dostępna jako historia?

### Narzędzia (W3)

- [ ] Czy IDE widzi wszystkie statyczne użycia i czy sprawdzono użycia dynamiczne?
- [ ] Czy kompilacja faktycznie używa JDK 25?
- [ ] Czy analizator obsługuje używaną składnię i kod bajtowy?
- [ ] Czy wyłączenia reguł są wąskie i uzasadnione?
- [ ] Czy automatyczny diff jest oddzielony od zmian ręcznych?

### Ryzyko (W1, aktywność B)

- [ ] Czy ustalono stan bazowy, SLI i bezwzględne SLO?
- [ ] Czy próbka jest reprezentatywna?
- [ ] Czy kryteria zatrzymania powstały przed wdrożeniem etapowym?
- [ ] Czy wycofanie zostało przećwiczone z uwzględnieniem danych?
- [ ] Czy zespół wie, kto podejmuje decyzję podczas incydentu?

### Sugerowana skala oceny warsztatu

| Poziom | Opis |
| --- | --- |
| spełnione | wszystkie kryteria akceptacji warsztatu i większość pytań z powiązanych sekcji listy kontrolnej |
| częściowo | kryteria akceptacji spełnione, ale z lukami w uzasadnieniu (np. brak właściciela, progi bez uzasadnienia) |
| niespełnione | brak rozróżnienia rodzaju zmian, brak planu wycofania lub zestawy zmian z mieszanymi intencjami |

Zasada końcowa do przypomnienia uczestnikom (z podsumowania teorii): kod, testy, narzędzia, przegląd, dokumentacja i wdrożenie etapowe tworzą jeden system bezpieczeństwa, ale żaden z tych elementów samodzielnie nie gwarantuje poprawności.
