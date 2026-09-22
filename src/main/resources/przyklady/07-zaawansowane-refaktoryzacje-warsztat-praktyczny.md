# Moduł 7. Zaawansowane refaktoryzacje - przewodnik prowadzącego po przykładach

Dokument opisuje kod przykładów modułu 7: gdzie leży, co ilustruje, na co zwrócić uwagę uczestników i jak go zademonstrować na żywo. Każdy przykład ma parę `before` (wersja wyjściowa, zwykle klasa `Legacy*`) i `after` (wersja po refaktoryzacji) oraz test równoważności, który porównuje obie wersje.

Wszystkie ścieżki Java są względne wobec katalogu głównego repozytorium. Kod produkcyjny: `src/main/java/pl/training/module7/`, testy: `src/test/java/pl/training/module7/`.

## Uruchamianie przykładów i testów

### Java

```bash
mvn -q compile && java -cp target/classes pl.training.module7.Module7Examples
mvn test -Dtest='pl.training.module7.**'
```

Pojedynczy test, np. `mvn test -Dtest=GodClassEquivalenceTest`. Z IDE: uruchom `Module7Examples.main` albo cały pakiet testów `pl.training.module7`.

Oczekiwany wynik `Module7Examples` (weryfikowany przez `Module7ExamplesTest`):

```text
Break Dependencies: ALLOWED
Extract Method Object: 45/MEDIUM
Break Responsibilities: deployments=3;failures=1;avgLeadTimeMinutes=18
Remove Duplication: image:42
Break Method: 10|api|sha-api,20|worker|sha-worker
Introduce Parameter Object: 165s
Remove Arrowhead: ELIGIBLE
Design by Contract: remaining=7
Remove Double Negative: true
Remove God Class: rel-42/1/1/1
Remove Boolean Parameter: deployed:rel-42
Remove Middle Man: dep-42 -> RUNNING
Return ASAP: api.jar
```

`Module7Examples` pełni też rolę composition root: to tam składane są zależności wersji `after` (np. `new DeploymentWindowService(new StandardMaintenanceWindows())`). Warto to pokazać przy Break Dependencies i God Class.

### C#

```bash
cd csharp && dotnet run --project src/Training.Module7
cd csharp && dotnet test
```

Kod: `csharp/src/Training.Module7/<Temat>/Before|After`, testy: `csharp/tests/Training.Module7.Tests/` (te same nazwy klas testowych co w Javie).

### TypeScript

```bash
cd typescript && npm ci && npm run build && npm run module7
cd typescript && npm test
```

Kod: `typescript/src/module7/<temat>/before|after`, testy: `typescript/test/module7/*.test.ts`.

## Mapa przykładów

| Przykład (pakiet) | Sekcja teorii | Pliki before | Pliki after | Test |
| --- | --- | --- | --- | --- |
| `breakdependencies` | 1. Break Dependencies | `LegacyDeploymentWindowService`, `StandardMaintenanceWindows` | `MaintenanceWindows`, `DeploymentWindowService`, `StandardMaintenanceWindows` | `BreakDependenciesEquivalenceTest` |
| `methodobject` | 2. Extract Method Object | `LegacyDeploymentRiskCalculator` | `DeploymentRiskCalculator`, `DeploymentRiskCalculation` | `ExtractMethodObjectEquivalenceTest` |
| `breakresponsibilities` | 3. Break Responsibilities | `LegacyDeploymentReport` | `DeploymentMetricsCalculator`, `DeploymentReportFormatter`, `DeploymentReportService` | `BreakResponsibilitiesEquivalenceTest` |
| `duplication` | 4. Remove Duplication | `LegacyArtifactPublisher` | `ArtifactPublisher` | `DuplicationEquivalenceTest` |
| `breakmethod` | 5. Break Method | `LegacyReleaseManifestBuilder` | `ReleaseManifestBuilder` | `BreakMethodEquivalenceTest` |
| `parameterobject` | 6. Introduce Parameter Object | `LegacyRolloutPlanner` | `RolloutSpec`, `RolloutPlanner` | `ParameterObjectEquivalenceTest` |
| `arrowhead` | 7. Remove Arrowhead Antipattern | `LegacyDeploymentEligibility` | `DeploymentEligibility` | `ArrowheadEquivalenceTest` |
| `contract` | 8. Introduce Design by Contract Checks | `LegacyDeploymentCapacity` | `Contracts`, `DeploymentCapacity` | `DesignByContractTest` |
| `doublenegative` | 9. Remove Double Negative | `LegacyReleaseReadiness`, `LegacyReleaseGate` | `ReleaseReadiness`, `ReleaseGate` | `DoubleNegativeEquivalenceTest` |
| `godclass` | 10. Remove God Classes | `LegacyReleaseManager` | `ReleaseApplicationService`, `ReleaseValidator`, `ReleaseRepository`, `InMemoryReleaseRepository`, `AuditTrail`, `ReleaseNotifier` | `GodClassEquivalenceTest` |
| `booleanparameter` | 11. Remove Boolean Method Parameters | `LegacyDeploymentExecutor` | `DeploymentExecutor` | `BooleanParameterEquivalenceTest` |
| `middleman` | 12. Remove Middle Man | `ReleaseService`, `ReleaseDashboard`, `DeploymentRegistry` | `ReleaseDashboard`, `DeploymentRegistry` | `MiddleManEquivalenceTest` |
| `returnasap` | 13. Return ASAP | `LegacyArtifactFinder` | `ArtifactFinder` | `ReturnAsapEquivalenceTest` |
| (wszystkie) | cały moduł | - | `Module7Examples` | `Module7ExamplesTest` |

Pliki `before` leżą w `src/main/java/pl/training/module7/<pakiet>/before/`, pliki `after` w `src/main/java/pl/training/module7/<pakiet>/after/`, testy w `src/test/java/pl/training/module7/`. Typy wspólne dla obu wersji (rekordy, enumy) leżą bezpośrednio w `src/main/java/pl/training/module7/<pakiet>/`.

## Wspólny wzorzec testów

Prawie każdy test równoważności korzysta z tych samych technik. Warto je nazwać na początku modułu, bo uczestnicy użyją ich w warsztatach:

- **test różnicowy**: ta sama dana wejściowa trafia do wersji `before` i `after`, asercja porównuje wyniki,
- **`assertSameFailure`**: porównuje klasę i komunikat wyjątku obu wersji (nie tylko "czy rzucono wyjątek"),
- **wiele błędnych pól naraz**: np. `new ManifestEntry(null, null, -1)` sprawdza, który błąd zostanie zgłoszony pierwszy, czyli kolejność walidacji,
- **ślad zdarzeń**: `List<String> events` wypełniana przez `Consumer<String>` albo spy (`AtomicInteger`, `AtomicReference`) rejestruje liczbę, kolejność i argumenty wywołań,
- **pełna macierz lub tabela prawdy**: pętle po `boolean[] {false, true}`.

Przykład wzorca (z `BreakMethodEquivalenceTest`):

```java
private static void assertSameFailure(
        Supplier<String> legacyCall,
        Supplier<String> refactoredCall) {
    RuntimeException legacyFailure = assertThrows(
            RuntimeException.class, legacyCall::get);
    RuntimeException refactoredFailure = assertThrows(
            RuntimeException.class, refactoredCall::get);
    assertEquals(legacyFailure.getClass(), refactoredFailure.getClass());
    assertEquals(legacyFailure.getMessage(), refactoredFailure.getMessage());
}
```

---

## 1. Break Dependencies (`breakdependencies`)

**Sekcja teorii:** 1. Break Dependencies (intencja seam: separation i sensing).

**Co ilustruje:** serwis sam tworzy kalendarz okien serwisowych w inicjalizatorze pola. Po zmianie zależność jest wstrzykiwana przez konstruktor jako najwęższy kontrakt `MaintenanceWindows` (interfejs funkcyjny z jedną metodą `allows`). Reguła biznesowa i walidacja nie zmieniają się, zmienia się tylko miejsce tworzenia zależności.

**Pliki:**

- before: `src/main/java/pl/training/module7/breakdependencies/before/LegacyDeploymentWindowService.java`, `src/main/java/pl/training/module7/breakdependencies/before/StandardMaintenanceWindows.java`
- after: `src/main/java/pl/training/module7/breakdependencies/after/MaintenanceWindows.java`, `src/main/java/pl/training/module7/breakdependencies/after/DeploymentWindowService.java`, `src/main/java/pl/training/module7/breakdependencies/after/StandardMaintenanceWindows.java`
- wspólne: `src/main/java/pl/training/module7/breakdependencies/DeploymentDecision.java`

Kluczowa różnica:

```java
// before
private final StandardMaintenanceWindows maintenanceWindows =
        new StandardMaintenanceWindows();

// after
public DeploymentWindowService(MaintenanceWindows maintenanceWindows) {
    this.maintenanceWindows = Objects.requireNonNull(
            maintenanceWindows, "maintenanceWindows");
}
```

**Na co zwrócić uwagę:**

- Interfejs jest `@FunctionalInterface`, więc test podstawia lambdę jako fake albo spy. Teoria zaznacza, że interfejs `sealed` nie nadaje się na otwarty punkt podstawienia.
- Kontrakt zawiera tylko to, czego potrzebuje klient (jedna metoda). Nie kopiujemy całego API implementacji.
- Walidacja odbywa się przed wywołaniem zależności. Po zmianie niepoprawne żądanie nadal nie dociera do kalendarza (test `invalidRequestDoesNotReachInjectedDependency`).
- Konstruktor `after` dodaje `requireNonNull` dla zależności. To nowy kontrakt konstrukcji, a nie zmiana zachowania `schedule`.
- Wersja `after` nie ma konstruktora bezargumentowego, więc produkcyjne składanie przenosi się do composition root (`Module7Examples`). Alternatywą przejściową jest konstruktor domyślny delegujący do `this(new StandardMaintenanceWindows())`.
- Ryzyka z teorii: przeniesienie tworzenia może zmienić czas życia, współdzielenie, inicjalizację statyczną, blokady i granicę transakcji zależności.

**Testy (`BreakDependenciesEquivalenceTest`):**

- `standardDependencyPreservesDecisionsForEveryHour` - decyzje dla godzin 0-23 identyczne w obu wersjach,
- `preservesMaintenanceWindowBoundaries` - granice okna 0, 5 (dozwolone), 6, 23 (poza oknem),
- `injectedSeamControlsTheDecisionAndReceivesTheArgumentsOnce` - spy z lambdą: jedno wywołanie, poprawne argumenty, wynik sterowany przez test,
- `preservesValidationFailuresAndTheirOrder` - typ i komunikat wyjątków, także `(" ", -1)` (pierwszy błąd wygrywa),
- `invalidRequestDoesNotReachInjectedDependency` - zero wywołań zależności przy błędnych danych,
- `rejectsMissingInjectedDependency` - `NullPointerException` z komunikatem `maintenanceWindows`.

**Przebieg demonstracji:**

1. Pokaż `LegacyDeploymentWindowService` i zapytaj: jak przetestować decyzję dla godziny 14 bez zmiany kalendarza?
2. Wyodrębnij interfejs z jedną metodą, dodaj `implements` w implementacji produkcyjnej.
3. Zmień typ pola na interfejs, dodaj konstruktor. Skompiluj, pokaż błąd kompilacji w miejscach tworzenia serwisu i przenieś składanie do `Module7Examples`.
4. Uruchom `BreakDependenciesEquivalenceTest`, omów test ze spy.

---

## 2. Extract Method Object (`methodobject`)

**Sekcja teorii:** 2. Extract Method Object (Replace Function with Command).

**Co ilustruje:** obliczenie ryzyka z jedną mutowaną zmienną `score` i klasyfikacją poziomu zostaje przeniesione do obiektu `DeploymentRiskCalculation` tworzonego dla każdego wywołania. Zmienna lokalna staje się polem, kroki stają się prywatnymi metodami bez parametrów. Publiczna klasa `DeploymentRiskCalculator` zostaje fasadą.

**Pliki:**

- before: `src/main/java/pl/training/module7/methodobject/before/LegacyDeploymentRiskCalculator.java`
- after: `src/main/java/pl/training/module7/methodobject/after/DeploymentRiskCalculator.java`, `src/main/java/pl/training/module7/methodobject/after/DeploymentRiskCalculation.java`
- wspólne: `DeploymentRiskInput.java`, `RiskAssessment.java`, `RiskLevel.java` w `src/main/java/pl/training/module7/methodobject/`

```java
public RiskAssessment calculate(DeploymentRiskInput input) {
    return new DeploymentRiskCalculation(input).calculate();
}
```

**Na co zwrócić uwagę:**

- Nowy obiekt na każde wywołanie. Gdyby fasada trzymała jedną instancję w polu, `score` kumulowałby się między wywołaniami (test `facadeCreatesAnIndependentCalculationForEveryInvocation` wywołuje `low`, `high`, `low`).
- `DeploymentRiskCalculation` jest klasą pakietową (brak `public`), a nie niestatyczną klasą wewnętrzną. Teoria: klasa wewnętrzna przechwytuje instancję zewnętrzną i ukrywa zależności.
- Rzutowanie `(long)` przed mnożeniem chroni przed przepełnieniem `int`. Dla `Integer.MAX_VALUE` wynik musi wynosić 100 (clamp). Usunięcie rzutowania zmieniłoby wynik.
- Kolejność: suma, redukcja za rollback, dopiero potem ograniczenie do 0-100. Przestawienie clamp przed redukcją zmienia wynik: dla `(120, 0, 0, true)` poprawnie wychodzi 100, a po przestawieniu 85; dla `(5, 0, 0, true)` wynik byłby ujemny i konstruktor `RiskAssessment` zgłosiłby wyjątek.
- Magiczne liczby wagi stały się stałymi, ale progi 30 i 70 zostały w `classify()`.
- `requireNonNull(input, "input")` przeniesiono do konstruktora obiektu metody. Komunikat pozostał ten sam (test `preservesMissingInputFailure`).
- Teoria: Method Object to rozwiązanie dla trudnego przepływu lokalnego stanu. Tutaj stan jest prosty, przykład ma charakter dydaktyczny. Warto o to zapytać uczestników.

**Testy (`ExtractMethodObjectEquivalenceTest`):**

- `preservesRiskAssessmentsAcrossRepresentativeInputs` - różnicowo dla reprezentatywnych wejść, w tym `Integer.MAX_VALUE`,
- `preservesScoreClampingAndClassificationBoundaries` - granice 0, 29/30, 69/70, 100,
- `preservesEachRiskWeightAndRollbackReduction` - wagi 20, 10 i redukcja 15,
- `facadeCreatesAnIndependentCalculationForEveryInvocation` - brak wycieku stanu,
- `preservesMissingInputFailure`, `sharedInputModelRejectsNegativeCounts`, `assessmentProtectsItsRangeAndRequiredLevel` - kontrakty wejścia i wyniku.

**Przebieg demonstracji:**

1. Pokaż wersję `before`, zaznacz zmienną `score` używaną przez wszystkie fragmenty i zapytaj, dlaczego zwykły Extract Method wymagałby przekazywania i zwracania `score`.
2. Utwórz klasę, przenieś parametr do konstruktora, skopiuj ciało metody bez zmian (krok 3 sekwencji z teorii).
3. Zastąp starą metodę delegacją, uruchom testy.
4. Zamień `score` na pole i wydzielaj kroki po jednym, po każdym uruchamiając testy.
5. Na koniec pokaż błąd: przenieś `new DeploymentRiskCalculation` do pola fasady i uruchom test niezależności wywołań.

---

## 3. Break Responsibilities (`breakresponsibilities`)

**Sekcja teorii:** 3. Break Responsibilities (odpowiedzialność jako powód zmiany).

**Co ilustruje:** metoda `generate` liczy metryki i formatuje raport. Po zmianie obliczenie trafia do `DeploymentMetricsCalculator` (zwraca rekord `DeploymentMetrics`), formatowanie do `DeploymentReportFormatter`, a `DeploymentReportService` składa oba kroki.

**Pliki:**

- before: `src/main/java/pl/training/module7/breakresponsibilities/before/LegacyDeploymentReport.java`
- after: `src/main/java/pl/training/module7/breakresponsibilities/after/DeploymentMetricsCalculator.java`, `.../after/DeploymentReportFormatter.java`, `.../after/DeploymentReportService.java`
- wspólne: `src/main/java/pl/training/module7/breakresponsibilities/DeploymentSample.java`, `src/main/java/pl/training/module7/breakresponsibilities/DeploymentMetrics.java`

**Na co zwrócić uwagę:**

- `DeploymentMetrics` to nowy model pośredni z niezmiennikami w kompaktowym konstruktorze (np. `failures` w zakresie 0..deployments, pusta lista ma średnią 0). Walidacja ta nie jest osiągalna z poprawnego obliczenia, ale stanowi kontrakt nowego typu.
- `Math.addExact` przy sumowaniu czasu: przepełnienie daje `ArithmeticException` w obu wersjach (test przepełnienia).
- Średnia jest całkowitoliczbowa (`/` na `long`), dla 1 i 2 wynosi 1.
- Serwis nie przejmuje szczegółów obliczeń ani formatu. Kierunek zależności: serwis -> kalkulator, serwis -> formatter.
- Ryzyka z teorii: rozdzielenie operacji objętej jedną transakcją lub blokadą, wpływ ORM, serializacji i refleksji.

**Testy (`BreakResponsibilitiesEquivalenceTest`):**

- `preservesGeneratedReport`, `preservesEmptyReportAndIntegerAverage` - identyczny tekst,
- `neitherImplementationMutatesTheInputList` - lista wejściowa bez zmian,
- `preservesFailuresForMissingListAndElement`, `preservesOverflowFailureWhileAccumulatingLeadTime` - te same wyjątki,
- `extractedComponentsHaveFocusedContracts` - kalkulator i formatter testowane osobno,
- `domainValuesAndCollaboratorsRejectInvalidState` - niezmienniki rekordów i `requireNonNull` współpracowników.

**Przebieg demonstracji:** pokaż dwa powody zmiany (nowa metryka vs nowy format raportu), wydziel formatter jako pierwszy (koniec grafu wywołań), potem kalkulator, na końcu serwis składający. Po każdym kroku testy.

---

## 4. Remove Duplication (`duplication`)

**Sekcja teorii:** 4. Remove Duplication (duplikacja wiedzy, nie tekstu).

**Co ilustruje:** `publishSnapshot` i `publishRelease` powtarzają walidację, normalizację i składanie współrzędnych. Po zmianie wspólna reguła ma jednego właściciela (`publish` i `validateAndNormalize`), a publiczne metody nadal wyrażają dwa zamiary biznesowe. Różnica (sufiks `-SNAPSHOT` lub pusty) jest jawnym argumentem.

**Pliki:** before `src/main/java/pl/training/module7/duplication/before/LegacyArtifactPublisher.java`, after `src/main/java/pl/training/module7/duplication/after/ArtifactPublisher.java`.

**Na co zwrócić uwagę:**

- `toLowerCase(Locale.ROOT)`: test uruchamia oba warianty z domyślnym `Locale` `tr-TR`. Bez `Locale.ROOT` wynik zależałby od domyślnego `Locale`: w `tr-TR` `"IMAGE".toLowerCase()` daje `ımage` z tureckim bezkropkowym `ı`. To klasyczna pułapka przy "porządkowaniu" normalizacji.
- Kolejność walidacji (null, blank, buildNumber) musi być identyczna. Test używa `(null, 0)`, więc gdyby sprawdzano najpierw `buildNumber`, zmieniłby się pierwszy błąd.
- Teoria: abstrakcja wymagająca wielu flag to sygnał połączenia przypadkowo podobnych procesów. Tu różnica to jedna wartość, więc połączenie jest uzasadnione.
- Sekwencja z teorii: najpierw niezależnie scharakteryzować obie implementacje, potem ujednolicić strukturę bez współdzielenia, dopiero potem wydzielić wspólny fragment i przepinać ścieżki po jednej.

**Testy (`DuplicationEquivalenceTest`):** `preservesBothPublicationVariantsIndependentlyOfDefaultLocale` (wynik w `tr-TR`, `@ResourceLock(Resources.LOCALE)` przy zmianie globalnego `Locale`), `preservesValidationTypeMessageAndOrderForBothVariants`.

**Przebieg demonstracji:** najpierw uruchom test `Locale` na wersji `before`, potem przepnij `publishSnapshot` na wspólną metodę, uruchom testy, przepnij `publishRelease`. Dla efektu pokaż, co się stanie po usunięciu `Locale.ROOT`.

---

## 5. Break Method (`breakmethod`)

**Sekcja teorii:** 5. Break Method (seria małych ekstrakcji, jeden poziom abstrakcji, Split Phase).

**Co ilustruje:** `build` waliduje i kopiuje wpisy, sortuje je i renderuje. Po zmianie metoda publiczna czyta się jak opis procesu:

```java
public String build(List<ManifestEntry> entries) {
    List<ManifestEntry> validatedEntries = validateAndCopy(entries);
    List<ManifestEntry> orderedEntries = order(validatedEntries);
    return render(orderedEntries);
}
```

**Pliki:** before `src/main/java/pl/training/module7/breakmethod/before/LegacyReleaseManifestBuilder.java`, after `src/main/java/pl/training/module7/breakmethod/after/ReleaseManifestBuilder.java`, wspólny rekord `src/main/java/pl/training/module7/breakmethod/ManifestEntry.java`.

**Na co zwrócić uwagę:**

- Lista wywołującego nie może być sortowana w miejscu. Wersja `before` sortuje własną kopię, `after` kopiuje w `validateAndCopy` i sortuje strumieniem.
- `Stream.sorted` jest stabilny, podobnie jak `List.sort`. Przy równych kluczach (`deploymentOrder`, `artifact`) zachowana jest kolejność wejściowa (test `preservesInputOrderWhenAllSortKeysAreEqual`).
- `.toList()` zwraca listę niemodyfikowalną. Tu nie wycieka na zewnątrz, więc nie zmienia kontraktu, ale warto o tym powiedzieć.
- Walidacja pozostaje w jednej pętli w tej samej kolejności pól. Rozbicie na osobne przebiegi (np. najpierw wszystkie `null`, potem wszystkie puste) zmieniłoby pierwszy zgłaszany błąd dla list z wieloma wadliwymi wpisami.
- Teoria: lokalne dane łatwo przechodzą między krokami, więc Method Object byłby tu zbędny. Porównaj z przykładem `methodobject`.

**Testy (`BreakMethodEquivalenceTest`):** `preservesOrderingRenderingAndTheCallersCollection` (wynik, pusta lista, lista wywołującego bez zmian), `preservesValidationTypeMessageAndEncounterOrder` (7 przypadków z wieloma błędnymi polami), `preservesInputOrderWhenAllSortKeysAreEqual` (stabilność).

**Przebieg demonstracji:** wydziel najpierw `render` (jedno wejście, jeden wynik), potem `order`, na końcu `validateAndCopy`. Po każdym kroku testy. Zapytaj: czy to już Split Phase? (Trzy fazy z osobnymi danymi pośrednimi.)

---

## 6. Introduce Parameter Object (`parameterobject`)

**Sekcja teorii:** 6. Introduce Parameter Object (data clump jako pojęcie, migracja API).

**Co ilustruje:** pięć parametrów (`service`, `region`, `instances`, `batchSize`, `pauseSeconds`) powtarzanych w `estimateSeconds`, `describe` i `validate` zostaje zastąpionych rekordem `RolloutSpec` z walidacją w kompaktowym konstruktorze.

**Pliki:** before `src/main/java/pl/training/module7/parameterobject/before/LegacyRolloutPlanner.java`, after `src/main/java/pl/training/module7/parameterobject/after/RolloutSpec.java`, `src/main/java/pl/training/module7/parameterobject/after/RolloutPlanner.java`.

**Na co zwrócić uwagę (najważniejsza pułapka modułu):**

- Kod `after` to stan docelowy po świadomej zmianie momentu błędu: niepoprawny `RolloutSpec` jest odrzucany przy konstrukcji, a nie przy wywołaniu planera. To obserwowalna zmiana i nie jest czystą refaktoryzacją. Test nazywa ją wprost: `movesValidationFromEveryOperationToParameterObjectConstruction`.
- Stan przejściowy zachowujący zachowanie (według teorii): rekord bez walidacji, dotychczasowe kontrole nadal w planerze, stara sygnatura jako delegujące przeciążenie.
- `Math.ceilDiv((long) instances, batchSize)` oraz `multiplyExact`, `addExact`: rzutowanie na `long` i dokładna arytmetyka. Dla `Integer.MAX_VALUE` instancji oczekiwany wynik to `32_212_254_720`.
- Zmiana sygnatury publicznej jest źródłowo poprawna po migracji, ale skompilowane pliki `.class` klientów oczekują starego deskryptora (błąd linkowania). Dla biblioteki potrzebne jest przeciążenie oznaczone `@Deprecated`.
- Komponenty publicznego rekordu są jego API (liczba, kolejność, typy).
- `List.copyOf` (przywołane w teorii) nie gwarantuje nowej instancji i zmienia semantykę aliasowania. Tu nie występuje, ale warto wspomnieć przy rekordach z kolekcjami.

**Testy (`ParameterObjectEquivalenceTest`):** `preservesEstimationAndDescription` (165 s, identyczny opis), `usesOverflowSafeCeilingDivision`, `movesValidationFromEveryOperationToParameterObjectConstruction`, `preservesValidationTypeMessageAndOrder` (porównanie `legacy.describe(...)` z `new RolloutSpec(...)`), `rejectsNullParameterObjectAtTheNewApiBoundary` (komunikat `spec`).

**Przebieg demonstracji:** pokaż migrację w krokach z sekcji 6.4 teorii. Dodaj rekord bez walidacji, dodaj przeciążenie `estimateSeconds(RolloutSpec)`, niech stara metoda deleguje. Dopiero na końcu pokaż wersję `after` i zapytaj, gdzie zmienił się moment wyjątku.

---

## 7. Remove Arrowhead Antipattern (`arrowhead`)

**Sekcja teorii:** 7. Remove Arrowhead Antipattern (guard clauses, priorytet warunków).

**Co ilustruje:** pięć poziomów zagnieżdżonych `if` ze zmienną `result` zamienia się na sekwencję guard clauses z wczesnym `return`.

**Pliki:** before `src/main/java/pl/training/module7/arrowhead/before/LegacyDeploymentEligibility.java`, after `src/main/java/pl/training/module7/arrowhead/after/DeploymentEligibility.java`, wspólne `src/main/java/pl/training/module7/arrowhead/DeploymentCandidate.java`, `src/main/java/pl/training/module7/arrowhead/Eligibility.java`.

**Na co zwrócić uwagę:**

- Priorytet: kandydat bez akceptacji i z nieudanymi testami musi dać `NOT_APPROVED`, bo ten warunek sprawdzany jest pierwszy. Przestawienie guard clauses zmienia wynik.
- Warunek złożony `releaseId != null && !releaseId.isBlank()` po odwróceniu staje się `releaseId == null || releaseId.isBlank()` (prawo de Morgana, short-circuit nadal chroni przed NPE).
- Sekwencja z teorii: odwracaj po jednym poziomie od zewnątrz, usuwaj odpowiadający `else`, uruchamiaj testy. Nie pomijaj logowania, mutacji ani zwalniania zasobów.

**Testy (`ArrowheadEquivalenceTest`):** `guardClausesPreserveTheCompleteDecisionMatrix` (8 kombinacji flag), `guardClausesPreserveValidationAndFailurePriority` (brak kandydata, `releaseId` równy `null` i złożony z białych znaków przy wszystkich flagach `false`, oraz kolejne poziomy priorytetu).

**Przebieg demonstracji:** najpierw zbuduj z uczestnikami macierz decyzji na tablicy, potem odwracaj poziomy po jednym. Na koniec celowo zamień kolejność `approved` i `testsPassed` i uruchom test.

---

## 8. Introduce Design by Contract Checks (`contract`)

**Sekcja teorii:** 8. Introduce Design by Contract Checks (precondition, postcondition, invariant; `assert` a publiczne API).

**Co ilustruje:** `LegacyDeploymentCapacity` przyjmuje dowolne wartości i może przejść w stan niespójny (ujemna pojemność, więcej wolnych slotów niż całość). Wersja `after` dodaje pole `totalSlots` i jawne kontrole przez `Contracts.require` (`IllegalArgumentException`), `Contracts.ensure` i `Contracts.invariant` (`IllegalStateException`).

**Pliki:** before `src/main/java/pl/training/module7/contract/before/LegacyDeploymentCapacity.java`, after `src/main/java/pl/training/module7/contract/after/Contracts.java`, `src/main/java/pl/training/module7/contract/after/DeploymentCapacity.java`.

```java
public void reserve(int slots) {
    Contracts.require(slots > 0, "slots must be positive");
    Contracts.require(slots <= remaining, "cannot reserve more slots than remain");
    int previousRemaining = remaining;
    int nextRemaining = previousRemaining - slots;
    checkInvariant(nextRemaining);
    remaining = nextRemaining;
    Contracts.ensure(remaining == previousRemaining - slots,
            "reserve must reduce remaining capacity by slots");
    checkInvariant();
}
```

**Na co zwrócić uwagę:**

- To nie jest czysta refaktoryzacja. Dodanie odrzucenia niepoprawnych danych zmienia zachowanie. Test celowo rozdziela równoważność dla poprawnych sekwencji od nowego kontraktu dla niepoprawnych.
- Preconditions przed pierwszą mutacją; nowy stan najpierw liczony i sprawdzany względem niezmiennika, dopiero potem przypisywany. Naruszenie precondition nie zmienia obiektu.
- `assert` jest domyślnie wyłączony (flaga `-ea`) i zgłasza `AssertionError`, więc nie nadaje się do publicznych preconditions.
- W istniejącym systemie historyczny typ wyjątku może być ważniejszy niż "podręcznikowy". Nie zmieniać go mechanicznie.
- Zasada LSP: podtyp nie wzmacnia preconditions ani nie osłabia postconditions.

**Testy (`DesignByContractTest`):** `explicitContractsPreserveEveryValidStateTransition` (różnicowo dla poprawnej sekwencji), `preconditionsRejectInvalidOperationsBeforeMutation` (komunikaty i niezmieniony `remaining`), `contractHelpersUseRuntimeExceptionsWithoutJavaAssertions`.

**Przebieg demonstracji:** pokaż, że `new LegacyDeploymentCapacity(5).reserve(6)` daje `remaining() == -1`. Sformułuj z grupą trzy rodzaje warunków, dodaj je po kolei, uruchamiaj oba testy osobno.

---

## 9. Remove Double Negative (`doublenegative`)

**Sekcja teorii:** 9. Remove Double Negative.

**Co ilustruje:** rekord z polami `notApproved`, `testsNotPassed`, `windowNotOpen` i warunek `!a && !b && !c` zastąpione rekordem z polami pozytywnymi i warunkiem `a && b && c`.

**Pliki:** before `src/main/java/pl/training/module7/doublenegative/before/LegacyReleaseReadiness.java`, `.../before/LegacyReleaseGate.java`; after `src/main/java/pl/training/module7/doublenegative/after/ReleaseReadiness.java`, `.../after/ReleaseGate.java`.

**Na co zwrócić uwagę:**

- Pozytywna nazwa musi być dokładnym dopełnieniem logicznym starej właściwości. Test mapuje `approved = !notApproved` itd.
- `Boolean` z wartością `null` nie jest dwustanowy: `!disabled` może zgłosić NPE przy unboxingu, a `!Boolean.TRUE.equals(disabled)` traktuje `null` jak `false`. To różne kontrakty.
- Zmiana nazw komponentów rekordu to zmiana API. Jeśli nazwa jest polem JSON, kolumną lub kluczem konfiguracji, potrzebna jest migracja granicy systemu.
- Sekwencja z teorii: najpierw predykat pozytywny delegujący do negatywnego, migracja po jednym użyciu, potem odwrócenie implementacji.

**Testy (`DoubleNegativeEquivalenceTest`):** `positiveNamesPreserveAllEightTruthTableRows`, `bothGatesRejectMissingReadinessWithTheSameContract`.

**Przebieg demonstracji:** krótki (5 minut). Pokaż tabelę prawdy w teście i zapytaj o konsekwencje zmiany nazw dla serializacji.

---

## 10. Remove God Classes (`godclass`)

**Sekcja teorii:** 10. Remove God Classes (kampania małych ruchów).

**Co ilustruje:** `LegacyReleaseManager` łączy walidację, przechowywanie, audyt, powiadomienia i ślad zdarzeń (`events`). Wersja `after` to stan po wydzieleniu pionowego fragmentu: `ReleaseApplicationService` koordynuje, a `ReleaseValidator`, `ReleaseRepository` (+ `InMemoryReleaseRepository`), `AuditTrail` i `ReleaseNotifier` mają osobne kontrakty. Ślad zdarzeń jest zbierany przez wstrzykiwany `Consumer<String> eventSink`.

**Pliki:**

- before: `src/main/java/pl/training/module7/godclass/before/LegacyReleaseManager.java`
- after: `src/main/java/pl/training/module7/godclass/after/ReleaseApplicationService.java`, `ReleaseValidator.java`, `ReleaseRepository.java`, `InMemoryReleaseRepository.java`, `AuditTrail.java`, `ReleaseNotifier.java` (wszystkie w `src/main/java/pl/training/module7/godclass/after/`)
- wspólne: `src/main/java/pl/training/module7/godclass/PublishedRelease.java`

**Na co zwrócić uwagę:**

- Kolejność efektów `save`, `audit`, `notify` jest częścią kontraktu i jest sprawdzana przez wspólną listę `events`.
- Brak transakcji: awaria kroku zatrzymuje sekwencję, ale nie wycofuje wcześniejszych efektów. Teoria mówi, że to świadoma decyzja zapisana w testach. W produkcji atomowość mogłaby wymagać transakcji, transactional outbox, idempotentnych ponowień lub kompensacji, ale tej decyzji nie ukrywa się w ruchu klas.
- Walidacja w `ReleaseValidator` zachowuje kolejność pól i komunikaty (`releaseId`, `service`, `version`), a sprawdzenie duplikatu następuje po walidacji, jak w wersji `before`.
- `InMemoryReleaseRepository.save` sprawdza duplikat ponownie. Ścieżka przez serwis nigdy do niego nie dochodzi (serwis sprawdza `existsById` wcześniej), ale repozytorium chroni własny niezmiennik. Dobry temat do dyskusji.
- Kolekcje udostępniane są jako `List.copyOf` w obu wersjach (test `exposedCollectionsAreDefensiveSnapshots`).
- Stan ma jednego właściciela: wydania należą do repozytorium, wpisy audytu do `AuditTrail`, powiadomienia do `ReleaseNotifier`. Zależności są jednokierunkowe (serwis -> współpracownicy).
- Wersja `after` pokazuje stan końcowy kilku kroków kampanii. W praktyce (i w Warsztacie 3) pierwszy krok zostawia `LegacyReleaseManager` jako delegującą fasadę.
- Ryzyka z teorii: zmiana transakcji, kolejności efektów, blokad, tożsamości encji, lazy loading, serializacji, konfiguracji DI. Sieć małych klas o niskiej spójności nie jest automatycznie lepsza.

**Testy (`GodClassEquivalenceTest`):**

- `collaboratorsPreserveResultEffectsAndTheirOrder` - wynik, stan i ślad `save:rel-42`, `audit:rel-42`, `notify:rel-42` identyczne jak w `before`,
- `validationAndDuplicateFailuresDoNotCreateFurtherEffects` - brak efektów przy błędzie walidacji i przy duplikacie,
- `exposedCollectionsAreDefensiveSnapshots` - niemodyfikowalne kopie,
- `repositoryFailureStopsLaterEffectsAndKeepsTheCompletedSave`, `auditFailureStopsNotificationAndKeepsEarlierEffects`, `notificationFailureKeepsAllEarlierEffects` - awaria każdego kroku przez `eventSink` rzucający wyjątek.

**Przebieg demonstracji:**

1. Narysuj mapę metod do pól (4 pola, 1 metoda z logiką, 4 akcesory).
2. Wydziel `ReleaseNotifier` (koniec grafu wywołań) jako pierwszy krok, zostaw `LegacyReleaseManager.notifications()` jako delegację, zasil `eventSink` przez `events::add`.
3. Pokaż test awarii z `eventSink` rzucającym wyjątek i omów brak wycofania.
4. Pokaż wersję `after` jako docelowy stan kampanii i `Module7Examples` jako miejsce składania.

---

## 11. Remove Boolean Method Parameters (`booleanparameter`)

**Sekcja teorii:** 11. Remove Boolean Method Parameters.

**Co ilustruje:** `execute(String deploymentId, boolean dryRun)` zastąpione przez `preview(id)` i `deploy(id)`. Wspólna prywatna implementacja przyjmuje nazwany tryb `ExecutionMode` (prywatny enum z prefiksem wyniku).

**Pliki:** before `src/main/java/pl/training/module7/booleanparameter/before/LegacyDeploymentExecutor.java`, after `src/main/java/pl/training/module7/booleanparameter/after/DeploymentExecutor.java`.

**Na co zwrócić uwagę:**

- Nie każdy `boolean` jest flagą. Wartość z formularza albo komunikatu może być daną domenową.
- Celem nie jest zdublowanie algorytmu w dwóch metodach, tylko ujawnienie zamiaru w miejscu wywołania.
- Dla publicznej biblioteki stara metoda powinna czasowo pozostać jako delegująca (wersja `after` jej nie ma, bo pokazuje stan końcowy).
- Wiele flag daje wykładniczo wiele kombinacji. Wtedy zamiast mnożyć metody należy rozważyć obiekt polityki.

**Testy (`BooleanParameterEquivalenceTest`):** `namedOperationsPreserveBothLegacyBranches`, `validationIsIdenticalForBothNamedOperations` (null, pusty, blank), `publicApiContainsNoBooleanParameterAndExecutorHasNoMutableState` (refleksja po publicznych metodach).

**Przebieg demonstracji:** dodaj `preview` i `deploy` delegujące do `execute(id, true/false)`, migruj klienta, na końcu zamień prywatną implementację na enum. Pokaż test oparty na refleksji jako "strażnika" API.

---

## 12. Remove Middle Man (`middleman`)

**Sekcja teorii:** 12. Remove Middle Man.

**Co ilustruje:** `ReleaseService` tylko przekazuje wywołania do `DeploymentRegistry`. Po zmianie `ReleaseDashboard` zależy bezpośrednio od rejestru, a `ReleaseService` znika. `DeploymentRegistry` jest identyczny w obu wersjach.

**Pliki:** before `src/main/java/pl/training/module7/middleman/before/ReleaseService.java`, `.../before/ReleaseDashboard.java`, `.../before/DeploymentRegistry.java`; after `src/main/java/pl/training/module7/middleman/after/ReleaseDashboard.java`, `.../after/DeploymentRegistry.java`; wspólny enum `src/main/java/pl/training/module7/middleman/DeploymentStatus.java`.

**Na co zwrócić uwagę:**

- Przed usunięciem potwierdź, że pośrednik nie realizuje autoryzacji, transakcji, telemetrii, retry ani translacji błędów.
- Remove Middle Man jest odwrotnością Hide Delegate. Fasada modułu, adapter biblioteki zewnętrznej czy seam testowy mogą mieć wartość nawet przy jednej linii.
- Walidacja pozostaje na granicy rejestru, więc komunikaty są te same.
- Komunikat `requireNonNull` w konstruktorze dashboardu zmienia się z `releaseService must not be null` na `registry must not be null` (inny parametr, nowy kontrakt konstrukcji).

**Testy (`MiddleManEquivalenceTest`):** `directCollaborationPreservesLookupUpdatesOverwriteAndRendering` (UNKNOWN, aktualizacja, nadpisanie, render), `validationRemainsAtTheRegistryBoundary` (null, pusty, blank, `null` status nie zmienia stanu).

**Przebieg demonstracji:** krótki. Zapytaj grupę, jakie powody mogłyby uzasadnić pozostawienie `ReleaseService`, potem przepnij dashboard.

---

## 13. Return ASAP (`returnasap`)

**Sekcja teorii:** 13. Return ASAP.

**Co ilustruje:** pętla `while` ze zmienną wyniku i warunkiem `result == null` zastąpiona pętlą `for` z `return Optional.of(artifact)` w miejscu dopasowania.

**Pliki:** before `src/main/java/pl/training/module7/returnasap/before/LegacyArtifactFinder.java`, after `src/main/java/pl/training/module7/returnasap/after/ArtifactFinder.java`, wspólny rekord `src/main/java/pl/training/module7/returnasap/Artifact.java`.

**Na co zwrócić uwagę:**

- Obie wersje zatrzymują się na pierwszym dopasowaniu i zwracają tę samą instancję (`assertSame`).
- Element `null` przed dopasowaniem zgłasza NPE, element `null` po dopasowaniu nie jest odczytywany.
- Wersja `after` celowo zachowuje dostęp indeksowy `size()` i `get(index)`. Zamiana na iterator lub strumień to osobny krok, bo niestandardowa, leniwa lub instrumentowana lista może ujawnić inną liczbę wywołań i inne wyjątki. Test sprawdza ślad `["size", "get:0"]`.
- `return` w `try` nadal uruchamia `finally`, a nagłe zakończenie `finally` może zastąpić wynik lub wyjątek. Zasoby zabezpieczyć przed wprowadzeniem wczesnych zwrotów.

**Testy (`ReturnAsapEquivalenceTest`):** `earlyReturnPreservesEmptyMissingAndPresentResults`, `bothVersionsReturnTheFirstMatchingInstance`, `nullElementIsReadBeforeAMatchButNotAfterIt`, `inputValidationHasTheSameOrderAndMessages`, `earlyReturnPreservesIndexedListAccesses` (lista śledząca oparta o `AbstractList`).

**Przebieg demonstracji:** najpierw uruchom test ze śledzącą listą na obu wersjach i omów ślad: w wersji `before` po dopasowaniu warunek `result == null` jest fałszywy, więc `size()` nie jest wołane ponownie (short-circuit), a wersja `after` kończy się `return` przed kolejnym sprawdzeniem. Potem wprowadź wczesny zwrót na żywo w krokach z sekcji 13.4 teorii. Na koniec zapytaj, czy wolno od razu przejść na `for (Artifact a : artifacts)`. Uwaga dla prowadzącego: iterator `AbstractList` sam korzysta z `size()` i `get(index)`, więc dla tej konkretnej listy śledzącej test może nadal przejść. To dobry argument w dyskusji: przechodzący test nie dowodzi równoważności dla każdej implementacji `List` (lista leniwa, instrumentowana, `LinkedList` z liniowym `get`), dlatego zmiana sposobu iteracji jest osobnym, świadomym krokiem.

---

## `Module7Examples` i `Module7ExamplesTest`

- Plik: `src/main/java/pl/training/module7/Module7Examples.java`, test: `src/test/java/pl/training/module7/Module7ExamplesTest.java`.
- `runExamples()` uruchamia po jednym deterministycznym przykładzie wersji `after` dla każdego z 13 tematów i zwraca niemodyfikowalną listę.
- Testy: `runsOneDeterministicExampleForEveryTopic` (dokładna lista wyników), `returnedResultsCannotBeModified`, `mainPrintsEveryResultOnItsOwnLine` (przechwycenie `System.out`).
- Dobry punkt startowy zajęć: uruchom `main`, pokaż wynik, następnie przechodź do kolejnych pakietów zgodnie z harmonogramem.

## Proponowana kolejność pokazów według harmonogramu

| Blok (czas) | Przykłady |
| --- | --- |
| model zachowania i Break Dependencies (40 min) | wspólny wzorzec testów, `breakdependencies` |
| Method Object, odpowiedzialności i duplikacja (60 min) | `methodobject`, `breakresponsibilities`, `duplication` |
| Break Method i Parameter Object (45 min) | `breakmethod`, `parameterobject` |
| Arrowhead, kontrakty i podwójne zaprzeczenia (60 min) | `arrowhead`, `contract`, `doublenegative` |
| God Class, flagi i Middle Man (55 min) | `godclass`, `booleanparameter`, `middleman` |
| Return ASAP i migracja API (25 min) | `returnasap`, powrót do migracji z `parameterobject` i `booleanparameter` (przeciążenia, pliki `.class`) |
| ćwiczenia warsztatowe (60 min) | Warsztaty 1-3 (plik zadań) |
| przegląd rozwiązań i podsumowanie (15 min) | lista kontrolna przeglądu (plik rozwiązań) |
