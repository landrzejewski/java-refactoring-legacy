# Moduł 5. Refaktoryzacja do wzorców - przewodnik prowadzącego po przykładach

Dokument opisuje kod modułu 5 w projekcie: co ilustruje każdy przykład, jak go pokazać na żywo i na co zwrócić uwagę grupy. Slajdy: `src/main/resources/md/slides/05-refaktoryzacja-do-wzorcow-slajdy.md`.

Kod produkcyjny: `src/main/java/pl/training/module5/`, testy: `src/test/java/pl/training/module5/`. Każdy przykład ma wersję `before` (kod wyjściowy, zwykle z przedrostkiem `Legacy`) i `after` (wersja referencyjna) oraz test równoważności, który uruchamia obie wersje na tych samych danych. Uzupełnieniem są sceny warsztatu CineLegacy (`pl.training.workshop.m5`), opisane w przewodniku warsztatu: `src/main/resources/md/warsztat/przewodnik/05-refaktoryzacja-do-wzorcow.md`.

## Uruchamianie przykładów i testów

Wszystkie polecenia uruchamiamy z katalogu głównego repozytorium.

### Java

```bash
mvn -q compile && java -cp target/classes pl.training.module5.Module5Examples
mvn test -Dtest='pl.training.module5.**'
```

Pojedynczy test, np. `mvn test -Dtest=ChainEquivalenceTest`. Z IDE: uruchom `Module5Examples.main` albo cały katalog `src/test/java/pl/training/module5`.

Oczekiwane wyjście `Module5Examples`:

```text
Strategy cost: 12500
Polymorphic steps: [executed:deploy.sh, approved-by:anna]
Type object requires approval: true
Composite total: 16
Visitor total: 16
Factory probe: http-ok:/health
Decorated deployment: deployed:rel-42, audit=[start:rel-42, success:rel-42]
State: DEPLOYED
Observer events: [audit:rel-42, metric:rel-42]
Adapter result: ops|release:rel-42
Command result: rolled-back:rel-42
Template result: ReleaseDraft[releaseId=rel-42, service=payments]
Singleton timeout: PT30S
Extract Composite equivalent: true
Remove Middle Man: dep-42 -> RUNNING
Command/Strategy/State: cost=1300, log=[scale-up -> instances=3, maintenance=off], status=DEPLOYING
Template Method -> Strategy: # Highlights 1.4.0 | - [FEATURE] Blue-green deployments
Chain of Responsibility: CHANGE_ADVISORY_BOARD (high-risk production change)
Boundary Object: build-17 [a1b2c3d] SUCCESS coverage=82.5% failed=0, releasable=true
Facade: published rel-42 to prod, effects=[stored rel-42.jar, rendered rel-42/prod.yaml, queued rel-42.jar with rel-42/prod.yaml, announced rel-42 on prod]
Anti-Corruption Layer: canary targets=[test-a]
Mediator: published 1.4.0, versionEditable=false
Inline Singleton: rel-42: 10% -> 50% -> 100%
```

`Module5Examples` używa wyłącznie wersji `after` (oraz obu wersji Extract Composite). Porównanie przed i po odbywa się w testach.

### C#

```bash
cd csharp && dotnet run --project src/Training.Module5
cd csharp && dotnet test tests/Training.Module5.Tests
```

Odpowiedniki: `csharp/src/Training.Module5/<Temat>/Before|After` (katalogi `Strategy`, `TypeCode`, `Polymorphism`, `Factory`, `Decorator`, `State`, `Observer`, `Composite`, `ExtractComposite`, `Adapter`, `Command`, `TemplateMethod`, `Singleton`, `MiddleMan` oraz `Variability`, `TemplateToStrategy`, `Chain`, `BoundaryObject`, `Facade`, `AntiCorruptionLayer`, `Mediator`, `InlineSingleton`), testy w `csharp/tests/Training.Module5.Tests` (te same nazwy klas testowych co w Javie).

### TypeScript

```bash
cd typescript && npm ci && npm run build && npm run module5
cd typescript && npx vitest run test/module5
```

Odpowiedniki: `typescript/src/module5/<temat>/before|after` (te same nazwy pakietów co w Javie, małymi literami, np. `variability`, `templatetostrategy`, `anticorruptionlayer`), punkt wejścia `typescript/src/module5/main.ts`, testy w `typescript/test/module5`.

---

## Uwaga organizacyjna: gotowe rozwiązania w repozytorium

Pakiety `after` są rozwiązaniami ćwiczeń modułu. Przed ćwiczeniami poproś uczestników, aby nie otwierali pakietów `after` i pracowali we własnym pakiecie (np. `pl.training.module5.workshop`) albo na osobnej gałęzi. Testy równoważności z repozytorium można skopiować i przepiąć na własne klasy: wtedy stają się gotową siecią bezpieczeństwa.

---

## Mapa przykładów

Wszystkie ścieżki Javy są względne względem `src/main/java/pl/training/module5/`, testy względem `src/test/java/pl/training/module5/`.

| # | Przykład | Sekcja teorii | Przed | Po | Testy |
| --- | --- | --- | --- | --- | --- |
| 1 | Remove Middle Man | 2.3 | `middleman/before/ReleaseService.java`, `ReleaseDashboard.java`, `DeploymentRegistry.java` | `middleman/after/ReleaseDashboard.java`, `DeploymentRegistry.java` | `MiddleManEquivalenceTest` |
| 2 | Inline Singleton | 2.4-2.5 | `inlinesingleton/before/LegacyFeatureFlags.java`, `LegacyRolloutService.java` | `inlinesingleton/after/FeatureFlags.java`, `RolloutService.java`, `RolloutCompositionRoot.java` | `InlineSingletonEquivalenceTest` |
| 3 | Command, Strategy czy State | 6.1-6.3, 6.8 | `variability/before/LegacyReleaseConsole.java` | `variability/after/ReleaseConsole.java`, `SupportCostPolicy.java`, `OperatorCommand.java`, `CommandConsole.java`, `Cluster.java`, `ReleaseLifecycle.java` | `VariabilityEquivalenceTest` |
| 4 | Strategy | 6.4 | `strategy/before/LegacyDeploymentCostCalculator.java` | `strategy/after/DeploymentCostPolicy.java`, `DeploymentCostCalculator.java`, `StandardCostPolicy.java`, `ExpeditedCostPolicy.java` | `StrategyEquivalenceTest` |
| 5 | State | 6.5-6.6 | `state/before/LegacyRelease.java` | `state/after/Release.java` | `StateEquivalenceTest` |
| 6 | Command | 6.7 | `command/before/LegacyDeploymentDispatcher.java` | `command/after/DeploymentAction.java`, `DeploymentCommand.java`, `PauseDeployment.java`, `RollbackDeployment.java`, `DeploymentCommandDispatcher.java` | `CommandEquivalenceTest` |
| 7 | Template Method | 7.1-7.2 | `templatemethod/before/LegacyKeyValueReleaseImporter.java`, `LegacyPipeReleaseImporter.java` | `templatemethod/after/ReleaseImporter.java`, `KeyValueReleaseImporter.java`, `PipeReleaseImporter.java` | `TemplateMethodEquivalenceTest` |
| 8 | Template Method → Strategy | 7.3-7.6 | `templatetostrategy/before/LegacyReleaseNotesExporter.java` i cztery podklasy | `templatetostrategy/after/ReleaseNotesExporter.java`, `NotesFormat.java`, `MarkdownFormat.java`, `HtmlFormat.java`, `ContentSelection.java` | `TemplateToStrategyEquivalenceTest` |
| 9 | Decorator | 8.1-8.2 | `decorator/before/LegacyDeploymentRunner.java` | `decorator/after/DeploymentRunner.java`, `BasicDeploymentRunner.java`, `AuditedDeploymentRunner.java` | `DecoratorEquivalenceTest` |
| 10 | Chain of Responsibility | 8.3-8.6 | `chain/before/LegacyApprovalRouter.java` | `chain/after/ApprovalHandler.java`, `ApprovalRouter.java` i pięć ogniw | `ChainEquivalenceTest` |
| 11 | Adapter | 9.1-9.2 | `adapter/before/LegacyNotificationClient.java` | `adapter/after/ReleaseNotifier.java`, `ReleaseMessage.java`, `LegacyMessageGateway.java`, `LegacyGatewayAdapter.java`, `NotificationService.java` | `AdapterEquivalenceTest` |
| 12 | Boundary Object | 9.3-9.6 | `boundaryobject/before/LegacyReleaseGate.java` | `boundaryobject/after/BuildReport.java`, `BuildReportMapper.java`, `BuildReports.java`, `CiBuildReportsAdapter.java`, `ReleaseGate.java` | `BoundaryObjectEquivalenceTest` |
| 13 | Factory | 10.1-10.3 | `factory/before/LegacyProbeService.java`, `HttpProbe.java`, `QueueProbe.java`, `DeploymentProbe.java` | `factory/after/DeploymentProbeFactory.java`, `ProbeService.java`, `DeploymentProbe.java` | `FactoryEquivalenceTest` |
| 14 | Facade | 11.1-11.3 | `facade/before/LegacyReleaseCli.java`, `LegacyReleaseScheduler.java` i cztery podsystemy | `facade/after/ReleasePublishingFacade.java`, `ReleaseCli.java`, `ReleaseScheduler.java` i cztery podsystemy | `FacadeEquivalenceTest` |
| 15 | Anti-Corruption Layer | 12.1-12.4 | `anticorruptionlayer/before/LegacyDeploymentPlanner.java` | `anticorruptionlayer/after/DeploymentPlanner.java`, `Environment.java`, `EnvironmentDirectory.java`, `anticorruptionlayer/after/acl/CmdbEnvironmentDirectory.java`, `anticorruptionlayer/after/acl/CmdbTranslator.java` | `AntiCorruptionLayerEquivalenceTest` |
| 16a | Builder | 13.1-13.2 | brak osobnej wersji (ręczne `new DeploymentGroup(...)`) | `composite/after/PlanBuilder.java` | `CompositeRefactoringsTest` |
| 16b | Composite (ukryte drzewo) | 17.3 | `composite/before/LegacyPathPlan.java` | `composite/after/PlanComponent.java`, `DeploymentTask.java`, `DeploymentGroup.java`, `LegacyPathPlanMapper.java` | `CompositeRefactoringsTest` |
| 16c | Jeden lub wiele | 17.4 | `composite/before/LegacyPlanExecutor.java` | `composite/after/PlanExecutor.java` | `CompositeRefactoringsTest` |
| 16d | Collecting Parameter i Visitor | 16.1-16.2, 17.5 | pętle w `LegacyPathPlan` | `PlanComponent.collectTasks`, `composite/after/PlanVisitor.java`, `TotalMinutesVisitor.java` | `CompositeRefactoringsTest` |
| 17 | Observer | 14.1-14.2 | `observer/before/LegacyReleasePublisher.java` | `observer/after/ReleasePublisher.java`, `ReleaseListener.java`, `ReleasePublished.java`, `Subscription.java` | `ObserverContractTest` |
| 18 | Mediator | 15.1-15.4 | `mediator/before/LegacyReleaseForm.java` i cztery komponenty | `mediator/after/ReleaseFormMediator.java`, `FormMediator.java` i cztery komponenty | `MediatorEquivalenceTest` |
| 19 | Polimorfizm | 17.1 | `polymorphism/before/LegacyDeploymentStep.java` | `polymorphism/after/DeploymentStep.java`, `ScriptStep.java`, `ApprovalStep.java` | `PolymorphismEquivalenceTest` |
| 20 | Obiekt typu | 17.2 | `typecode/before/LegacyDeploymentRequest.java` | `typecode/after/DeploymentZone.java`, `DeploymentRequest.java` | `TypeCodeEquivalenceTest` |
| 21 | Extract Composite | 17.4 | `extractcomposite/before/LegacyPlanNodes.java` | `extractcomposite/after/PlanNodes.java` | `ExtractCompositeEquivalenceTest` |
| 22 | Singleton | 17.6 | `singleton/before/LegacyDeploymentDefaults.java` | `singleton/DeploymentDefaults.java` | `SingletonContractTest` |
| - | Demonstracja całości | wszystkie | - | `Module5Examples.java` | `Module5ExamplesTest` (uruchamia `main` bez wyjątku) |

Sekcje 1, 3, 4 i 5 teorii (decyzja, niebezpieczeństwa, wsparcie IDE, testowanie) nie mają osobnego kodu. Ilustruj je na przykładach i scenach warsztatu:

- sekcja 1 (czy warto) - przykład 3 i scena s20 (`pl.training.workshop.m5.s20_decisionmap`),
- sekcja 3 (niebezpieczeństwa) - tabela przesunięć z slajdu 3.3 na przykładach 4, 6, 12, 15, 2 i 14 oraz scena s23 (`s23_contractchange`, `double` → `BigDecimal`),
- sekcja 4 (IDE) - sceny s24 (`s24_rename`, nazwy żyjące w konfiguracji i nagłówku CSV) i s25 (`s25_extractmethod`),
- sekcja 5 (testowanie) - dowolny test równoważności, najlepiej `VariabilityEquivalenceTest` (pomocnik `assertSameFailure`) i `ChainEquivalenceTest` (siatka 54 przypadków).

Sugerowana kolejność na zajęciach: przykłady 1-2 (blok 1, przed Ćwiczeniem 1), 3-6 (przed Ćwiczeniem 2), 7-10 (przed Ćwiczeniem 3), 11-15 (przed Ćwiczeniem 4), na końcu krótko 16-18 i przegląd 19-22. Przykłady 19-22 są przeglądem znanych technik - wystarczy 2-3 minuty na każdy albo wskazanie ich jako materiału do samodzielnego przejrzenia.

---

## Przykład 1. Remove Middle Man (`middleman`)

**Sekcja teorii:** 2.1-2.3. Refaktoryzacja od wzorca, Remove Middle Man.

**Co ilustruje:** `ReleaseService` tylko przekazuje wywołania do `DeploymentRegistry`. Po zmianie `ReleaseDashboard` zależy bezpośrednio od rejestru, a `ReleaseService` znika. `DeploymentRegistry` jest identyczny w obu wersjach.

**Klasy:**

- przed: `middleman/before/ReleaseService.java` (`update`, `statusOf` delegujące 1:1), `middleman/before/ReleaseDashboard.java` (zależy od `ReleaseService`), `middleman/before/DeploymentRegistry.java`,
- po: `middleman/after/ReleaseDashboard.java` (zależy od `DeploymentRegistry`), `middleman/after/DeploymentRegistry.java`,
- wspólny enum: `middleman/DeploymentStatus.java`.

**Na co zwrócić uwagę:**

- Przed usunięciem potwierdź, że pośrednik nie realizuje autoryzacji, transakcji, telemetrii, retry ani translacji błędów. Tu nie realizuje - ale to trzeba sprawdzić, a nie założyć.
- Remove Middle Man jest odwrotnością Hide Delegate. Fasada modułu, adapter biblioteki zewnętrznej czy seam testowy mogą mieć wartość nawet przy jednej linii.
- Walidacja pozostaje na granicy rejestru, więc komunikaty są te same.
- Komunikat `requireNonNull` w konstruktorze dashboardu zmienia się z `releaseService must not be null` na `registry must not be null` (inny parametr, nowy kontrakt konstrukcji).
- Scena s22 warsztatu pokazuje pośrednika, który "prawie" tylko deleguje: `CinemaFacade.freeSeats` tłumaczy brak seansu na `0`. Usunięcie go wymaga przeniesienia tej reguły do klienta albo do katalogu.

**Testy:** `MiddleManEquivalenceTest`

- `directCollaborationPreservesLookupUpdatesOverwriteAndRendering` - `UNKNOWN`, aktualizacja, nadpisanie, render,
- `validationRemainsAtTheRegistryBoundary` - `null`, pusty, biały identyfikator; `null` jako status nie zmienia stanu.

**Demonstracja na żywo:**

1. Pokaż `ReleaseService` i zapytaj, jakie powody mogłyby uzasadnić jego pozostawienie.
2. W IntelliJ: na polu `releaseService` w dashboardzie użyj Inline Method na `statusOf`, potem Change Signature konstruktora. Alternatywnie Refactor → Remove Middle Man na `ReleaseService`.
3. Uruchom test. Pytanie: "Co by się stało, gdyby `ReleaseService` logował każde wywołanie do audytu?"

---

## Przykład 2. Inline Singleton (`inlinesingleton`)

**Sekcja teorii:** 2.4-2.5. Inline Singleton - przed i po, procedura i ryzyka.

**Co ilustruje:** globalny, mutowalny singleton flag funkcji zastąpiony niezmiennym obiektem wartości, tworzonym w korzeniu kompozycji i przekazywanym przez konstruktor. Zależność staje się widoczna w sygnaturze, a testy przestają dzielić globalny stan.

**Klasy:**

- przed: `inlinesingleton/before/LegacyFeatureFlags.java` (`getInstance()`, `enable`, `disable`, `isEnabled`, `reset` "dla testów", metody `synchronized`), `inlinesingleton/before/LegacyRolloutService.java` (woła `getInstance()` w środku `plan`),
- po: `inlinesingleton/after/FeatureFlags.java` (rekord z `Set.copyOf`, fabryki `of` i `parse`), `inlinesingleton/after/RolloutService.java` (flagi w konstruktorze), `inlinesingleton/after/RolloutCompositionRoot.java` (`create(flagsConfig)`).

**Na co zwrócić uwagę:**

- Sygnatura `LegacyRolloutService.plan(String)` nie zdradza, że wynik zależy od globalnych flag. Po zmianie zależność jest w konstruktorze.
- To nie jest czysta refaktoryzacja: w wersji legacy można włączyć flagę w trakcie działania (`enable`), w wersji `after` flagi są niezmienne po starcie. Jeżeli ktoś przełączał flagi w locie (np. konsola administracyjna), potrzebny jest jawny port źródła flag, a nie powrót do singletonu.
- `FeatureFlags.parse` przycina spacje i pomija puste wpisy (`" canary , dark-launch "`); `null` i pusty tekst oznaczają brak flag. To nowa funkcja tworzenia, nie zmiana starego API.
- `Set.copyOf` odcina obiekt od zbioru źródłowego; `enabled().add(...)` rzuca `UnsupportedOperationException`.
- Testy legacy wymagają `reset()` w `finally` i `@ResourceLock("LegacyFeatureFlags")`. To dobry argument na sali: koszt globalnego stanu widać w testach.
- "Jedna instancja" przestaje być własnością klasy, a staje się decyzją korzenia kompozycji (lub zakresu DI).

**Testy:** `InlineSingletonEquivalenceTest`

- `planIsTheSameForEveryFlagCombination` - pięć kombinacji flag (w tym nieznana flaga) daje ten sam plan,
- `servicesWithDifferentFlagsCanCoexistWithoutSharedState` - dwa serwisy z różnymi flagami działają obok siebie,
- `legacySingletonLeaksStateBetweenUsers` - utrwala zapach: flaga włączona "przez kogoś" zmienia wynik drugiego serwisu,
- `compositionRootBuildsTheServiceFromConfiguration` - parsowanie konfiguracji z białymi znakami i pustej,
- `flagsAreImmutableValues` - kopia defensywna, niemutowalność, identyczny komunikat dla pustego `releaseId`.

**Demonstracja na żywo:**

1. Pokaż `LegacyRolloutService.plan` i zapytaj: "Skąd wiadomo, od czego zależy wynik?"
2. Pokaż test `legacySingletonLeaksStateBetweenUsers` i `@ResourceLock`.
3. Pokaż wersję `after` w kolejności: `RolloutService`, `FeatureFlags`, `RolloutCompositionRoot`.
4. Pytanie: "Czy to jeszcze refaktoryzacja?" (dla przypadku przełączania w locie - nie, zmiana kontraktu).

---

## Przykład 3. Command, Strategy czy State (`variability`)

**Sekcja teorii:** 6.1-6.3 i 6.8. Kto wybiera wariant i kiedy, punkt wyjścia, jeden mechanizm na każdą oś, typowe pomyłki.

**Co ilustruje:** jedna klasa z trzema `switch`-ami, które wyglądają podobnie, ale odpowiadają trzem różnym osiom zmienności. Każda oś dostaje inny mechanizm, a kryterium wyboru jest jedno: kto wybiera wariant i kiedy. Kryterium jest zapisane w `variability/after/package-info.java`.

**Klasy:**

- przed: `variability/before/LegacyReleaseConsole.java`:
  - (a) `supportCost(hours)` - `switch` po `CustomerTier`, chociaż taryfa jest stała dla klienta,
  - (b) `submit`, `runQueued`, `undoLast` - polecenia operatora jako napisy; wykonanie i cofnięcie to dwa osobne `switch`,
  - (c) `advance`, `rollback`, `banner` - `switch` i `if` po `ReleaseStatus`,
- po:
  - `variability/after/SupportCostPolicy.java` - Strategy (`@FunctionalInterface`, stałe `STANDARD`, `PREMIUM`, `ENTERPRISE`, fabryka `forTier`),
  - `variability/after/OperatorCommand.java` - Command (`sealed interface` z rekordami `ScaleUp`, `ScaleDown`, `ToggleMaintenance`; `execute` i `undo` obok siebie; fabryka `byName`),
  - `variability/after/CommandConsole.java` - invoker (kolejka, historia, log), `variability/after/Cluster.java` - odbiorca poleceń,
  - `variability/after/ReleaseLifecycle.java` - State (prywatny enum `State` z nadpisanymi metodami `advance`, `rollback`),
  - `variability/after/ReleaseConsole.java` - kontekst składający trzy mechanizmy,
- wspólne typy: `variability/CustomerTier.java`, `variability/ReleaseStatus.java`.

**Na co zwrócić uwagę:**

- Strategy: wariant wybiera klient **raz** (`ReleaseConsole.forTier` albo konstruktor z dowolną polityką, np. lambdą). Walidacja `hours must not be negative` zostaje w kontekście, nie w strategiach.
- Command: żądanie jest obiektem, bo trzeba je kolejkować, logować i cofać. W legacy dodanie polecenia wymaga zmian w `ACTIONS`, w `runQueued` i w `undoLast`; po zmianie - jednej klasy w `OperatorCommand` i wpisu w `byName`.
- Moment walidacji nieznanej akcji jest zachowany: `submit("reboot")` rzuca `IllegalArgumentException("unknown action: reboot")` od razu w obu wersjach (w `after` przez `OperatorCommand.byName`).
- Awaria w środku kolejki: `scale-down` przy jednej instancji rzuca `cannot scale below one instance`; log i stan po błędzie są takie same w obu wersjach (test `commandAxisPreservesFailures`). Polecenie, które rzuciło, nie trafia do historii.
- State: klient nie wybiera wariantu - obiekt sam zmienia zachowanie. Domyślne `advance` i `rollback` w enumie rzucają wyjątek z tym samym komunikatem co legacy (`cannot advance release in state LIVE`).
- Kontrprzykład na sali: "Czy taryfa klienta mogłaby być State?" (nie, bo nie zmienia się w wyniku operacji obiektu) oraz "Czy cykl życia mogłaby być Strategy?" (nie, bo nikt z zewnątrz go nie wybiera).
- Nie powstała kombinatoryczna hierarchia: osie są złożone w `ReleaseConsole`, nie dziedziczone.

**Testy:** `VariabilityEquivalenceTest`

- `strategyAxisPreservesEveryTierCost` - wszystkie taryfy dla 0, 1, 10, 33, 100 godzin oraz komunikat dla wartości ujemnej,
- `strategyCanBeSuppliedByTheClientWithoutTouchingTheConsole` - lambda jako strategia,
- `commandAxisPreservesQueueLogAndUndo` - kolejka czterech poleceń, dwa cofnięcia, dosłowny log jako niezależne oczekiwanie,
- `commandAxisPreservesFailures` - nieznana akcja, cofnięcie bez historii, `scale-down` poniżej jednej instancji (pomocnik `assertSameFailure` porównuje klasę, komunikat, status i log),
- `stateAxisPreservesLifecycleAndBanners`, `plannedReleaseCannotBeRolledBackInBothVersions` - pełna ścieżka cyklu życia i niedozwolone przejścia.

**Demonstracja na żywo:**

1. Pokaż `LegacyReleaseConsole` z komentarzami (a), (b), (c). Zapytaj dla każdego `switch`: "Kto wybiera wariant i kiedy?"
2. Wypełnij z grupą tabelę ze slajdu 6.1.
3. Pokaż `package-info.java`, potem trzy klasy `after` w kolejności Strategy, Command, State.
4. Uruchom `VariabilityEquivalenceTest`; pokaż pomocnik `assertSameFailure`.
5. Pytanie: "Co trzeba zmienić, aby dodać polecenie `restart` z cofnięciem?" Porównaj liczbę miejsc przed i po.

---

## Przykład 4. Strategy (`strategy`)

**Sekcja teorii:** 6.4. Strategy - intencja, sekwencja, ryzyka.

**Co ilustruje:** zastąpienie `switch` wybierającego wariant algorytmu wstrzykniętą polityką. Kontekst (`DeploymentCostCalculator`) zachowuje wspólną walidację, a samo obliczenie deleguje.

**Klasy:**

- przed: `strategy/before/LegacyDeploymentCostCalculator.java` - metoda `calculate(long, DeploymentMode)`, walidacja `baseCostInCents < 0`, `switch` po `STANDARD`/`EXPEDITED`, `Objects.requireNonNull(mode, "mode")`,
- po: `strategy/after/DeploymentCostPolicy.java` (`@FunctionalInterface`), `DeploymentCostCalculator.java` (polityka w konstruktorze, walidacja w `calculate`), `StandardCostPolicy.java`, `ExpeditedCostPolicy.java`.

**Na co zwrócić uwagę:**

- Walidacja wartości ujemnej zostaje w kontekście, nie w strategiach. Test `keepsInputValidationInTheContext` wstrzykuje lambdę `ignored -> 0` i nadal oczekuje wyjątku.
- `Math.addExact` w `ExpeditedCostPolicy` zachowuje politykę przepełnienia (`ArithmeticException`).
- Zmiana semantyki: wybór wariantu przeniósł się z parametru wywołania do konstruktora. Kalkulator ma "zamrożoną" politykę na cały cykl życia. Jeżeli stary kod odczytywał konfigurację przy każdym wywołaniu, to nie jest równoważne.
- `null` dla trybu: przed - `NullPointerException` przy wywołaniu; po - `NullPointerException("policy")` przy konstrukcji. Moment błędu się przesuwa (slajd 3.3).
- Interfejs funkcyjny nie może być `sealed`; strategia jest punktem otwartym na rozszerzenie.
- Dla jednego stabilnego warunku Strategy może być przerostem formy.

**Testy:** `StrategyEquivalenceTest`

- `preservesEveryLegacyCalculationVariant` - obie polityki dla wartości 0, 1, 4, 10 001 (zaokrąglenie dzielenia całkowitego `/ 4`),
- `keepsInputValidationInTheContext` - komunikat `base cost must not be negative`,
- `preservesOverflowPolicyOfTheExpeditedVariant` - `Long.MAX_VALUE` rzuca `ArithmeticException` w obu wersjach.

**Demonstracja na żywo:**

1. Pokaż `LegacyDeploymentCostCalculator`. Pytanie: "Czy ten `switch` to problem?" (lokalnie nie, uzasadnieniem jest niezależny rozwój polityk).
2. Pokaż `DeploymentCostPolicy` i `DeploymentCostCalculator`. Zapytaj, gdzie powinna zostać walidacja i dlaczego.
3. Uruchom `StrategyEquivalenceTest`. Zwróć uwagę na dobór wartości 1 i 4 (reszta z dzielenia).
4. Pytanie do grupy: "Co się stanie, jeśli tryb był czytany z konfiguracji przy każdym wywołaniu?" (moment wyboru).

---

## Przykład 5. State (`state`)

**Sekcja teorii:** 6.5-6.6. State - tabela przejść, kolejność przejścia i efektów.

**Co ilustruje:** warunki sprawdzające pole `status` w każdej operacji zastąpione obiektami stanu, które zwracają następny stan albo rzucają wyjątek. Wariant z `sealed interface`; przykład 3 pokazuje wariant z enumem z nadpisanymi metodami.

**Klasy:**

- przed: `state/before/LegacyRelease.java` - `if (status == ...)` w `approve`, `deploy`, `cancel`, wspólna metoda `invalid(action)`,
- po: `state/after/Release.java` - prywatny `sealed interface ReleaseState` z domyślnymi metodami rzucającymi wyjątek i prywatną metodą `invalid`, cztery enumy jednoelementowe (`DraftState`, `ApprovedState`, `DeployedState`, `CancelledState`), publiczny `enum Status`.

Tabela przejść (specyfikacja testu):

| Stan | approve | deploy | cancel |
| --- | --- | --- | --- |
| DRAFT | APPROVED | wyjątek | CANCELLED |
| APPROVED | wyjątek | DEPLOYED | CANCELLED |
| DEPLOYED | wyjątek | wyjątek | wyjątek |
| CANCELLED | wyjątek | wyjątek | wyjątek |

**Na co zwrócić uwagę:**

- Zacznij od tabeli przejść; to ona jest specyfikacją testu.
- Domyślna implementacja w interfejsie rzuca wyjątek, a nie jest pusta. Pusta implementacja domyślna zmieniłaby zachowanie.
- Stan po niedozwolonej operacji zostaje niezmieniony: `state = state.deploy()` nie wykona przypisania, bo wyjątek poleci wcześniej.
- Obiekty stanu są bezstanowymi stałymi enumu. Dane konkretnego wydania muszą zostać w `Release`; pole w enumie byłoby współdzielone przez wszystkie wydania.
- Publiczne `Status` w wersji `after` to inny typ niż `LegacyRelease.Status`; test porównuje `name()`.
- W przykładzie nie ma efektów zewnętrznych. W prawdziwym systemie trzeba wybrać kolejność stanu i efektu (efekt potem stan, stan potem efekt, outbox, rozdzielenie stanów). `volatile` nie daje atomowości, a funkcja w `AtomicReference.updateAndGet` może wykonać się kilka razy.
- Mały `switch` po dwóch stanach bywa czytelniejszy niż State.

**Testy:** `StateEquivalenceTest`

- `preservesApprovalAndDeploymentTransitions` - ścieżka DRAFT, APPROVED, DEPLOYED,
- `preservesBothAllowedCancellationPaths` - anulowanie z DRAFT i z APPROVED,
- `everyInvalidTransitionKeepsStateAndExceptionMessage` - osiem niedozwolonych kombinacji; identyczny komunikat `IllegalStateException` i niezmieniony status w obu wersjach (pomocnik `assertInvalid`).

**Demonstracja na żywo:**

1. Narysuj z grupą tabelę przejść na podstawie `LegacyRelease`.
2. Pokaż `Release`: najpierw metody publiczne (jednolinijkowe), potem `ReleaseState` z domyślnymi wyjątkami, potem enumy.
3. Uruchom `StateEquivalenceTest`; pokaż, jak `assertInvalid` pokrywa wszystkie komórki "wyjątek" z tabeli.
4. Pytanie: "Gdzie umieścić wysłanie powiadomienia po `deploy`?" (kolejność stanu i efektu, slajd 6.6).

---

## Przykład 6. Command (`command`)

**Sekcja teorii:** 6.7. Command - rejestr i ograniczenia.

**Co ilustruje:** `switch` po `DeploymentAction` zastąpiony niemodyfikowalnym rejestrem komend ze sprawdzeniem kompletności. W odróżnieniu od przykładu 3 komendy nie mają tu cofania ani kolejki - pokazują wyłącznie zamianę dyspozytora warunkowego na rejestr.

**Klasy:**

- przed: `command/before/LegacyDeploymentDispatcher.java` (walidacja `action` i `releaseId`, `switch` PAUSE/ROLLBACK, zagnieżdżony enum),
- po: `command/after/DeploymentAction.java` (enum najwyższego poziomu), `DeploymentCommand.java`, `PauseDeployment.java`, `RollbackDeployment.java`, `DeploymentCommandDispatcher.java`.

**Na co zwrócić uwagę:**

- Mapa jest równoważna tylko dla rozłącznego klucza. Łańcuch nakładających się predykatów ("wygrywa pierwszy") nie jest mapą - to przypadek z przykładu 10 (Chain of Responsibility).
- `Map.copyOf` odcina rejestr od późniejszych zmian mapy wejściowej i odrzuca `null`.
- Sprawdzenie kompletności przez `EnumSet.allOf` przenosi błąd konfiguracji na moment składania aplikacji (`missing commands: [ROLLBACK]`). To nowe zachowanie konstruktora; stary `switch` był kompletny z definicji.
- Walidacja `action` i `releaseId` zostaje w dispatcherze, przed wyszukaniem komendy.
- Command nie daje automatycznie asynchroniczności, kolejki, retry ani undo. Współdzielone komendy muszą być bezstanowe.

**Testy:** `CommandEquivalenceTest`

- `lookupPreservesEveryLegacyDispatchBranch` - PAUSE i ROLLBACK dają te same wyniki,
- `registryIsDefensivelyCopiedAndMustBeComplete` - modyfikacja źródłowej `EnumMap` po konstrukcji nie wpływa na dispatcher; niepełna mapa daje komunikat `missing commands: [ROLLBACK]`.

**Demonstracja na żywo:** pokaż `LegacyDeploymentDispatcher`, następnie `DeploymentCommandDispatcher`; uruchom test. Pytanie: "Gdyby selektorem był łańcuch `if (priority > 5) ... else if (region == EU) ...`, czy mapa by wystarczyła?" (nie - przejście do przykładu 10).

---

## Przykład 7. Template Method (`templatemethod`)

**Sekcja teorii:** 7.1-7.2. Apply Template Method, sekwencja i ryzyka.

**Co ilustruje:** dwa importery z identycznym szkieletem (walidacja wejścia, parsowanie, walidacja pól, utworzenie `ReleaseDraft`) i różnym krokiem parsowania. Szkielet przeniesiony do `final` metody w klasie bazowej. To przypadek, w którym Template Method jest dobrym wyborem: jedna oś zmienności, stabilny szkielet.

**Klasy:**

- przed: `templatemethod/before/LegacyKeyValueReleaseImporter.java` (`id=...;service=...`), `LegacyPipeReleaseImporter.java` (`rel|service`), `ReleaseDraft.java`,
- po: `templatemethod/after/ReleaseImporter.java` (`final importRelease`, `abstract parse`, chroniona fabryka `fields`, chroniony rekord `Fields`), `KeyValueReleaseImporter.java`, `PipeReleaseImporter.java`, `ReleaseDraft.java`.

**Na co zwrócić uwagę:**

- W wersji legacy Pipe przycinał pola przed walidacją, a KeyValue przycinał wartości w pętli. Po zmianie oba robią trim w `parse`, a wspólna walidacja `releaseId and service are required` jest w szkielecie. Komunikaty są zachowane.
- `final` na `importRelease` chroni kolejność. Dodanie `final` do opublikowanej metody, którą ktoś nadpisuje, byłoby zmianą łamiącą.
- Chroniona fabryka `fields(...)` jest potrzebna, bo podklasa `ReleaseImporter` spoza pakietu nie mogłaby wywołać chronionego konstruktora `Fields`. Test sprawdza to podklasą zagnieżdżoną w innym pakiecie.
- Nowa metoda abstrakcyjna wymusza zmiany we wszystkich podklasach; hooki wołane z konstruktora bazy widzą niezainicjalizowane pola; `synchronized` szablon trzyma monitor podczas hooków.

**Testy:** `TemplateMethodEquivalenceTest`

- `commonSkeletonPreservesBothImportFormats` - oba formaty dają te same wartości co legacy,
- `templateMethodProtectsTheRequiredOrder` - refleksyjnie sprawdza `Modifier.isFinal`,
- `commonValidationPreservesTheLegacyFailure` - `" |payments"`: ta sama klasa i komunikat wyjątku,
- `supportsSubclassOutsideTheImplementationPackage` - `CommaReleaseImporter` w pakiecie testowym.

**Demonstracja na żywo:** pokaż obie klasy legacy obok siebie i poproś grupę o wypisanie kroków; potem pokaż `ReleaseImporter`. Pytanie: "Co by się stało, gdyby oprócz formatu zmieniała się też polityka walidacji?" (przejście do przykładu 8).

---

## Przykład 8. Template Method → Strategy (`templatetostrategy`)

**Sekcja teorii:** 7.3-7.6. Gdy haki niosą dwie osie, szkielet w klasie finalnej, Replace Inheritance with Delegation, Strategy czy Template Method.

**Co ilustruje:** Template Method, którego haki niosą dwie niezależne osie zmienności (format: Markdown/HTML, zakres treści: Changelog/Highlights). Każda kombinacja to osobna podklasa: 2 × 2 = 4 klasy, kolejny format dokłada następne dwie, a kod formatu (np. `escape`) jest kopiowany. Po zmianie szkielet zostaje w jednej klasie finalnej, a osie stają się dwiema strategiami: N + M klas zamiast N × M.

**Klasy:**

- przed: `templatetostrategy/before/LegacyReleaseNotesExporter.java` (`final export`, cztery haki: `header`, `include`, `formatEntry`, `footer`), podklasy `LegacyMarkdownChangelogExporter.java`, `LegacyMarkdownHighlightsExporter.java`, `LegacyHtmlChangelogExporter.java`, `LegacyHtmlHighlightsExporter.java`,
- po: `templatetostrategy/after/ReleaseNotesExporter.java` (klasa `final`, szkielet `export`), `NotesFormat.java` (strategia formatu: `header(title, version)`, `entry`, `footer`), `MarkdownFormat.java`, `HtmlFormat.java`, `ContentSelection.java` (strategia zakresu: rekord z tytułem i `Predicate<ChangeEntry>`, stałe `CHANGELOG`, `HIGHLIGHTS`),
- wspólny typ: `templatetostrategy/ChangeEntry.java` (rodzaje `FEATURE`, `FIX`, `BREAKING`).

**Na co zwrócić uwagę:**

- Haki trzeba najpierw **pogrupować według osi**: `header`, `formatEntry`, `footer` należą do formatu, `include` do zakresu. Tytuł (`Changelog`/`Highlights`) był zaszyty w `header` każdej podklasy, a po zmianie należy do zakresu treści - stąd sygnatura `header(String title, String version)`.
- `HtmlFormat` ucieka wersję i opis, ale nie tytuł - dokładnie tak jak legacy (tytuł był stałą w kodzie). Gdyby tytuł mógł pochodzić od użytkownika (`new ContentSelection("<b>", ...)`), byłaby to luka do osobnej decyzji.
- Nowa kombinacja nie wymaga nowej klasy (`newCombinationNeedsNoNewSubclass` tworzy zakres "Breaking changes" lambdą).
- Kolejność kroków nadal chroni jedna metoda; zamiast `final` na metodzie szablonowej mamy `final` na klasie (`exporterIsClosedForInheritance`).
- Znika typ podklasy: kod, który robił `instanceof LegacyHtmlHighlightsExporter` albo wstrzykiwał eksporter po typie, wymaga migracji. W scenie s27 warsztatu tę rolę pełni stabilny punkt wejścia `Reports`.
- Pusta lista zmian daje sam nagłówek i stopkę w obu wersjach (test na wersji `1.4.1`).

**Testy:** `TemplateToStrategyEquivalenceTest`

- `everySubclassCombinationHasAnEquivalentComposition` - test parametryzowany: każda z czterech podklas ma równoważne złożenie strategii, także dla pustej listy,
- `htmlHighlightsKeepEscapingAndFiltering` - dosłowne oczekiwanie: `&amp;` w opisie, `FIX` odfiltrowany,
- `newCombinationNeedsNoNewSubclass` - nowy zakres bez nowej klasy,
- `exporterIsClosedForInheritance` - refleksyjnie `Modifier.isFinal`.

**Demonstracja na żywo:**

1. Pokaż `LegacyReleaseNotesExporter` i dwie podklasy HTML obok siebie (diff w IDE). Zapytaj: "Co trzeba zrobić, aby dodać format AsciiDoc?" (dwie nowe klasy i trzecia kopia formatu).
2. Pokoloruj z grupą haki według osi (format, zakres).
3. Pokaż `NotesFormat`, `ContentSelection` i `ReleaseNotesExporter`.
4. Uruchom test parametryzowany. Pytanie: "Kiedy zostawilibyście Template Method?" (jedna oś, stabilny szkielet - przykład 7).

---

## Przykład 9. Decorator (`decorator`)

**Sekcja teorii:** 8.1-8.2. Move Embellishment to Decorator.

**Co ilustruje:** audyt wpleciony w metodę `run` wydzielony do dekoratora opakowującego rdzeń o tym samym kontrakcie.

**Klasy:**

- przed: `decorator/before/LegacyDeploymentRunner.java` - `start:` przed walidacją, `success:` po wyniku, `failure:<id>:<SimpleName>` w `catch`, ponowne rzucenie wyjątku,
- po: `decorator/after/DeploymentRunner.java` (`@FunctionalInterface`), `BasicDeploymentRunner.java` (walidacja i wynik `deployed:<id>`), `AuditedDeploymentRunner.java` (dekorator).

**Na co zwrócić uwagę:**

- W wersji legacy walidacja była wewnątrz `try`, więc `start:` jest zapisywany także dla błędnego wejścia. Po zmianie walidacja jest w rdzeniu (delegacie), dzięki czemu kolejność audytu dla błędnego wejścia się nie zmienia.
- Dekorator rzuca dokładnie tę samą instancję wyjątku (`assertSame`).
- Jeżeli `audit.accept("failure...")` sam rzuci wyjątek, zasłoni błąd rdzenia. To wymaga jawnej decyzji (przerwanie, `suppressed`, kanał awaryjny).
- `audit.accept("success...")` jest wewnątrz `try`, więc błąd audytu sukcesu zostanie zarejestrowany jako `failure` (zachowane z wersji legacy, warto pokazać jako ciekawostkę).
- Kolejność dekoratorów jest częścią kontraktu: `audit(retry(core))` a `retry(audit(core))`.
- Dekorator nie jest przezroczysty dla `getClass`, tożsamości, serializacji, monitora `synchronized`, metod fluent zwracających `this` i samowywołań w delegacie.
- W porównaniu z przykładem 10: dekorator **zawsze** deleguje do rdzenia i dokłada swoje zachowanie; ogniwo łańcucha może przerwać przetwarzanie.

**Testy:** `DecoratorEquivalenceTest`

- `preservesResultAndAuditOrderOnSuccess` - wynik i lista audytu identyczne,
- `preservesAuditOrderAndPropagatesTheSameFailure` - delegat rzuca wyjątek; audyt `start`, `failure:rel-42:IllegalStateException`; ta sama instancja wyjątku,
- `invalidInputPreservesFailureAndAuditTrace` - pusty `releaseId`: ta sama klasa i komunikat wyjątku oraz ten sam ślad audytu.

**Demonstracja na żywo:**

1. Pokaż `LegacyDeploymentRunner`; poproś grupę o wypisanie śladu audytu dla `" "` (odpowiedź: `start: `, `failure: :IllegalArgumentException`).
2. Pokaż trzy klasy `after`; omów, dlaczego walidacja trafiła do rdzenia.
3. Uruchom test. Pytanie: "Gdzie postawilibyście retry i czy audyt ma liczyć próby, czy operacje?"

---

## Przykład 10. Chain of Responsibility (`chain`)

**Sekcja teorii:** 8.3-8.6. Łańcuch `if/else` z ukrytą kolejnością, ogniwo i złożenie, sekwencja i ryzyka, Chain czy Decorator.

**Co ilustruje:** długi łańcuch `if/else` wybierający zatwierdzającego zmianę. Kolejność reguł jest istotna, ale ukryta w jednej metodzie; dodanie lub przestawienie poziomu wymaga edycji tej metody. Po zmianie każda reguła jest ogniwem, które obsługuje żądanie albo przekazuje je dalej, a łańcuch jest złożony jawnie w jednym miejscu.

**Klasy:**

- przed: `chain/before/LegacyApprovalRouter.java` - pięć gałęzi i domyślne odrzucenie `no approver for <id>`,
- po: `chain/after/ApprovalHandler.java` (abstrakcyjne ogniwo: `then(next)` do składania, `handle`, chronione `passOn` z końcem łańcucha), `chain/after/ApprovalRouter.java` (złożenie `standard()`), ogniwa `SizeLimitHandler.java`, `AutomaticApprovalHandler.java`, `TeamLeadHandler.java`, `ReleaseManagerHandler.java`, `ChangeAdvisoryBoardHandler.java`,
- wspólne typy: `chain/ChangeRequest.java` (`linesChanged`, `Risk`, `production`, walidacja w rekordzie), `chain/Decision.java` (`Approver`, `reason`).

**Na co zwrócić uwagę:**

- Ogniwa **nie są niezależne**. `ReleaseManagerHandler` ma warunek `risk != HIGH || !production` - poprawny tylko dlatego, że wcześniejsze ogniwa obsłużyły małe i umiarkowane zmiany. Przestawienie `TeamLeadHandler` za `ReleaseManagerHandler` zmieni wynik dla zmian umiarkowanych. Kolejność jest regułą biznesową i musi być widoczna w złożeniu.
- Koniec łańcucha to decyzja (`REJECTED`, `no approver for <id>`), nie `null` ani wyjątek. Uwaga: `ChangeAdvisoryBoardHandler` przekazuje dalej zmiany powyżej 2000 linii, więc odrzucenie "spada" z końca łańcucha - tak jak `else` w legacy.
- Dokładnie jedno ogniwo obsługuje żądanie (test z ogniwami liczącymi wywołania).
- `then(next)` zwraca następne ogniwo, aby pisać `first.then(second).then(third)`. Pole `next` jest mutowalne: łańcuch składamy raz, przy starcie, i nie współdzielimy ogniw między łańcuchami.
- `ApprovalRouter.standard()` to fabryka spójnego łańcucha (sekcja 10). Inny proces zatwierdzania (np. dla zmian awaryjnych) to inne złożenie tych samych ogniw.
- Kiedy nie warto: pięć gałęzi w jednej stabilnej metodzie z testem jest czytelne. Łańcuch opłaca się, gdy procesy zatwierdzania różnią się zestawem lub kolejnością ogniw.
- Audyt decyzji (kto, dlaczego) to Decorator wokół `ApprovalRouter`, nie kolejne ogniwo - musi działać dla każdej decyzji.

**Testy:** `ChainEquivalenceTest`

- `chainPreservesEveryLegacyRoutingDecision` - siatka 9 rozmiarów (na granicach progów 50/51, 500/501, 2000/2001, 5000/5001) × 3 ryzyka × 2 środowiska = 54 przypadki; test liczy porównania,
- `everyApproverIsReachable` - po jednym żądaniu na każdego zatwierdzającego, jako niezależne oczekiwanie,
- `exactlyOneHandlerHandlesTheRequest` - ogniwo za obsługującym nie jest wywoływane,
- `endOfTheChainRejectsUnhandledRequests` - koniec łańcucha zwraca `REJECTED`.

**Demonstracja na żywo:**

1. Pokaż `LegacyApprovalRouter`. Zapytaj: "Czy możemy przestawić gałąź TEAM_LEAD pod RELEASE_MANAGER?" Poproś o przykład żądania, dla którego wynik się zmieni (np. 300 linii, MEDIUM, produkcja).
2. Pokaż `ApprovalHandler` i `ApprovalRouter.standard()`.
3. Kontrolna mutacja: w `standard()` zamień kolejność `TeamLeadHandler` i `ReleaseManagerHandler`, uruchom test, pokaż czerwony przypadek, cofnij.
4. Pytanie: "Audyt decyzji - ogniwo czy dekorator?"

---

## Przykład 11. Adapter (`adapter`)

**Sekcja teorii:** 9.1-9.2. Unify Interfaces with Adapter.

**Co ilustruje:** klient z dwiema ścieżkami (preferowany interfejs i stara brama) ujednolicony do jednego kontraktu `ReleaseNotifier`, a stara brama podpięta przez adapter obiektowy.

**Klasy:**

- przed: `adapter/before/LegacyNotificationClient.java` - `notifyUsingPreferred(notifier, recipient, releaseId)` i `notifyUsingLegacy(gateway, recipient, releaseId)` z budową payloadu `"release:" + releaseId`, wspólna walidacja,
- po: `adapter/after/ReleaseNotifier.java` (`send(ReleaseMessage)`), `ReleaseMessage.java` (rekord z walidacją), `LegacyMessageGateway.java` (`transmit(destination, body)`), `LegacyGatewayAdapter.java`, `NotificationService.java`.

**Na co zwrócić uwagę:**

- Walidacja przeszła z klienta do rekordu `ReleaseMessage` z identycznymi komunikatami.
- Adapter tłumaczy nie tylko nazwy, ale i format danych (`recipient` na `destination`, `releaseId` na `release:<id>`).
- W prawdziwym adapterze trzeba jawnie rozstrzygnąć jednostki, strefy czasu, kodowanie, `null`, własność zasobów (`AutoCloseable`), idempotencję, mapowanie wyjątków i model wątków.
- Obiekt opakowujący zmienia `getClass`, tożsamość, monitor i serializację. Jeśli oryginał można po prostu przemianować (Rename/Move Method), adapter może być zbędny.

**Testy:** `AdapterEquivalenceTest`

- `adapterMapsThePreferredContractToTheLegacyProtocol` - wynik `ops|release:rel-42` identyczny ze starą ścieżką,
- `clientCanUseNativeAndAdaptedImplementationsThroughOneInterface` - natywna lambda i adapter dają ten sam wynik przez `NotificationService`,
- `messageObjectPreservesLegacyValidation` - klasa i komunikat wyjątku dla pustego odbiorcy.

**Demonstracja na żywo:** pokaż dwie metody `LegacyNotificationClient`, zapytaj, co różni te ścieżki; pokaż `LegacyGatewayAdapter` i `NotificationService`; uruchom test. Pytanie: "Kto zamyka połączenie, gdy adapter jest `AutoCloseable`, a brama współdzielona?"

---

## Przykład 12. Boundary Object (`boundaryobject`)

**Sekcja teorii:** 9.3-9.6. Surowy ładunek w domenie, Boundary Object, dane na granicy, Adapter czy Boundary Object.

**Co ilustruje:** usługa domenowa pracuje bezpośrednio na surowym ładunku z systemu CI (`Map<String, String>`): nazwy kluczy, parsowanie i normalizacja są rozsiane po metodach, a błędny ładunek wybucha głęboko w domenie. Po zmianie adapter na granicy od razu zamienia ładunek na zwalidowany, niezmienny `BuildReport`, a domena widzi tylko port `BuildReports`.

**Klasy:**

- wspólny klient: `boundaryobject/LegacyCiClient.java` (`fetchBuild(buildId)` zwraca mapę z kluczami `id`, `status`, `coverage`, `failed`, `commit`),
- przed: `boundaryobject/before/LegacyReleaseGate.java` - `canRelease` i `summary` same parsują ładunek,
- po: `boundaryobject/after/BuildReport.java` (rekord z walidacją: pokrycie 0-100, `failedTests >= 0`, `commit` jako heksadecymalny sha 7-40 znaków; metoda `succeeded()`), `BuildReportMapper.java` (pakietowo-prywatny, jedyne miejsce znające format), `BuildReports.java` (port domenowy), `CiBuildReportsAdapter.java` (adapter implementujący port), `ReleaseGate.java` (domena operująca na `BuildReport`).

**Na co zwrócić uwagę:**

- Adapter i Boundary Object występują razem, ale robią co innego: adapter tłumaczy **wywołanie** (port → klient CI), obiekt graniczny tłumaczy **dane** (mapa → typy z niezmiennikami).
- Dla poprawnych ładunków decyzje i podsumowania są identyczne (w tym normalizacja `" success "`, wielkie litery w `commit`, pokrycie `80` jako `80.0`).
- Dla błędnych ładunków zachowanie **celowo się zmienia**: legacy rzuca `NumberFormatException` albo `NullPointerException` w środku reguły, wersja `after` rzuca `IllegalArgumentException("invalid CI payload: ...")` na granicy, z zachowaną przyczyną. To zmiana kontraktu dla danych błędnych - na sali nazwij ją wprost (slajd 3.3).
- `BuildReport` waliduje się sam, niezależnie od mappera (`boundaryObjectValidatesItselfIndependentlyOfThePayload`) - nie da się utworzyć niepoprawnego obiektu także z kodu domeny.
- `BuildReportMapper` używa `Locale.ROOT` przy zmianie wielkości liter; legacy używało domyślnego locale. Dla danych ASCII bez znaczenia, ale to kolejna drobna różnica do nazwania.
- Domena przestała zależeć od `java.util.Map` i nazw kluczy - zmiana formatu CI dotyka jednej klasy.

**Testy:** `BoundaryObjectEquivalenceTest`

- `domainDecisionsAreTheSameForEveryValidPayload` - pięć ładunków (sukces, wartości z białymi znakami, pokrycie 79.9, porażka, build w toku), porównanie `canRelease` i `summary` oraz niezależne oczekiwania,
- `payloadWithMissingFieldIsRejectedAtTheBoundary` - test parametryzowany dla każdego brakującego klucza: `invalid CI payload: missing <klucz>`,
- `malformedValuesAreRejectedAtTheBoundaryInsteadOfInsideTheDomain` - `eighty` jako pokrycie: legacy `NumberFormatException`, po zmianie czytelny błąd granicy; pokrycie 120 odrzucone przez niezmiennik rekordu,
- `boundaryObjectValidatesItselfIndependentlyOfThePayload` - ujemna liczba błędów, niepoprawny sha.

**Demonstracja na żywo:**

1. Pokaż `LegacyReleaseGate` i zapytaj: "Gdzie wybuchnie ładunek bez klucza `failed`?" (w środku reguły, `NullPointerException`).
2. Pokaż `BuildReport`, potem `BuildReportMapper` i `CiBuildReportsAdapter`, na końcu `ReleaseGate` (sama reguła biznesowa).
3. Uruchom test; zatrzymaj się przy `malformedValuesAreRejectedAtTheBoundaryInsteadOfInsideTheDomain` i zapytaj: "Czy to jeszcze refaktoryzacja?"
4. Odeślij do sceny s29 (`OK;AUTH-R1;4500` z terminala płatniczego) jako wariantu CineLegacy.

---

## Przykład 13. Factory (`factory`)

**Sekcja teorii:** 10.1-10.3. Factory - publiczna granica tworzenia, procedura, fabryka jako strażnik spójności.

**Co ilustruje:** dwie transformacje naraz. Encapsulate Classes with Factory ukrywa `HttpProbe`/`QueueProbe` jako prywatne rekordy fabryki; Extract Factory Class zabiera wiedzę o konstrukcji z serwisu do osobnej klasy wstrzykiwanej do `ProbeService`.

**Klasy:**

- przed: `factory/before/DeploymentProbe.java`, `HttpProbe.java`, `QueueProbe.java` (publiczne rekordy z walidacją w konstruktorze), `LegacyProbeService.java` (`switch` tworzący sondę i od razu ją uruchamiający),
- po: `factory/after/DeploymentProbe.java`, `DeploymentProbeFactory.java` (walidacja celu, `switch`, prywatne rekordy), `ProbeService.java` (dostaje fabrykę w konstruktorze).

**Na co zwrócić uwagę:**

- Walidacja przeniosła się z konstruktorów produktów do fabryki, ale komunikaty (`endpoint must not be blank`, `queueName must not be blank`) są zachowane. Prywatne rekordy po zmianie nie walidują same, bo jedyną drogą ich utworzenia jest fabryka - to właśnie kontrola spójności przez enkapsulację.
- `switch` zostaje w fabryce i to jest w porządku: fabryka jest właściwą granicą wiedzy o konstrukcji. Celem nie jest usuwanie każdego warunku.
- Fabryka tworzy nową instancję przy każdym wywołaniu (test `factoryPreservesFreshInstanceSemantics`). Tworzenie wszystkich wariantów z wyprzedzeniem lub cache byłoby zmianą zachowania.
- Ograniczenie widoczności publicznych konstruktorów jest zmianą łamiącą dla klientów zewnętrznych (w bibliotece: etap przejściowy, `@Deprecated`).
- Fabryka nie powinna stać się globalnym rejestrem usług.
- Na slajdzie 10.3 warto zebrać inne fabryki modułu: `DeploymentZone.fromCode` (przykład 20), `SupportCostPolicy.forTier` i `OperatorCommand.byName` (przykład 3), `ApprovalRouter.standard()` (przykład 10).

**Testy:** `FactoryEquivalenceTest`

- `factoryHidesConcreteClassesWithoutChangingBehavior` - wyniki `check()` identyczne jak dla starych rekordów,
- `rejectsAnInvalidTargetAtTheCreationBoundary` - identyczne komunikaty dla pustego celu HTTP i QUEUE,
- `factoryPreservesFreshInstanceSemantics` - `assertNotSame` dla dwóch wywołań,
- `extractedFactoryRemovesCreationKnowledgeFromTheService` - `ProbeService` daje te same wyniki co `LegacyProbeService`.

**Demonstracja na żywo:**

1. Pokaż `HttpProbe` i `LegacyProbeService`. Zapytaj, ile klas zna konstruktor `HttpProbe`.
2. Pokaż `DeploymentProbeFactory` (prywatne rekordy) i `ProbeService`.
3. Uruchom test; omów `factoryPreservesFreshInstanceSemantics`. Pytanie: "Czy możemy dodać cache sond?" (to już zmiana zachowania).

---

## Przykład 14. Facade (`facade`)

**Sekcja teorii:** 11.1-11.3. Klient orkiestruje podsystemy, fasada jako strażnik kolejności, procedura i ryzyka.

**Co ilustruje:** dwóch klientów (CLI i nocny harmonogram) samodzielnie orkiestruje cztery podsystemy publikacji wydania: zapis artefaktu, render konfiguracji, kolejka wdrożeń, ogłoszenie. Ta sama wiedza o kolejności i walidacji jest skopiowana i zapisana nieco inaczej. Po zmianie jedna operacja fasady wymusza kolejność i walidację, a podsystemy są pakietowo-prywatne, więc klient spoza pakietu nie może ich złożyć w złej kolejności ani pominąć kroku.

**Klasy:**

- przed: `facade/before/LegacyReleaseCli.java` (walidacja `releaseId` i środowiska, cztery kroki), `facade/before/LegacyReleaseScheduler.java` (ta sama orkiestracja w pętli, środowisko na sztywno `staging`), publiczne podsystemy `facade/before/ArtifactStore.java`, `ConfigRenderer.java`, `DeploymentQueue.java`, `ReleaseNotifier.java`,
- po: `facade/after/ReleasePublishingFacade.java` (jedyna publiczna operacja `publish`), `facade/after/ReleaseCli.java`, `facade/after/ReleaseScheduler.java` (klienci zależą tylko od fasady), pakietowo-prywatne podsystemy `facade/after/ArtifactStore.java`, `ConfigRenderer.java`, `DeploymentQueue.java`, `ReleaseNotifier.java`.

Efekty podsystemów są zapisywane do wstrzykniętego `Consumer<String>`, dzięki czemu test widzi ich listę i kolejność.

**Na co zwrócić uwagę:**

- Test porównuje **listę efektów i ich kolejność**, nie tylko wynik: `stored`, `rendered`, `queued`, `announced`.
- Przy błędnym żądaniu (nieznane środowisko `qa`, pusty `releaseId`) nie powstaje żaden efekt - walidacja jest przed pierwszym krokiem w obu wersjach.
- Harmonogram w legacy nie walidował środowiska (stałe `staging`), a po zmianie przechodzi przez walidację fasady. Dla stałej `staging` wynik jest ten sam, ale warto zauważyć, że dwa "podobne" klienty miały różne kontrakty.
- Różnica do nazwania: `null` jako środowisko dawało w legacy `NullPointerException` bez komunikatu (z `trim()`), w fasadzie - `NullPointerException("environment")` z `requireNonNull`.
- Zawężenie widoczności podsystemów to zmiana API dla klientów spoza pakietu. W prawdziwym projekcie robimy je jako osobny krok, po migracji wszystkich klientów (można to wykryć testem refleksyjnym albo regułą architektury).
- Fasada nie daje transakcji: awaria `enqueue` zostawia zapisany artefakt. Kompensacja to osobna decyzja. Fasada nie powinna też zbierać wszystkich operacji podsystemów (God Class) - jedna spójna operacja.
- Fasada, która tylko przekazuje wywołania 1:1, byłaby Middle Manem (przykład 1).

**Testy:** `FacadeEquivalenceTest`

- `cliProducesTheSameEffectsInTheSameOrder` - wynik i lista efektów dla `" PROD "` (normalizacja),
- `schedulerProducesTheSameEffectsInTheSameOrder` - dwa wydania w pętli,
- `invalidRequestsAreRejectedBeforeAnySideEffect` - te same komunikaty i brak efektów w obu wersjach,
- `subsystemsAreHiddenBehindTheFacade` - refleksyjnie: podsystemy w pakiecie `after` nie są publiczne.

**Demonstracja na żywo:**

1. Pokaż obok siebie `LegacyReleaseCli.publish` i `LegacyReleaseScheduler.publishNightly`. Zapytaj: "Co się stanie, gdy dział bezpieczeństwa każe skanować artefakt przed renderem konfiguracji?"
2. Pokaż `ReleasePublishingFacade` i obu klientów `after`.
3. Uruchom test; omów `invalidRequestsAreRejectedBeforeAnySideEffect`.
4. Pytanie: "Czy fasada powinna wycofać zapisany artefakt, gdy kolejka odrzuci wdrożenie?" (zmiana zachowania, osobny krok).

---

## Przykład 15. Anti-Corruption Layer (`anticorruptionlayer`)

**Sekcja teorii:** 12.1-12.4. Obcy model w regułach domeny, port i tłumacz, Facade a ACL, ryzyka i Strangler Fig.

**Co ilustruje:** reguły planowania wdrożeń mówią językiem starego CMDB: kody `P`/`T`/`D`, flagi statusu `1`/`0`/`9`, daty jako tekst `yyyyMMdd`. Po zmianie domena ma własny model (`Environment` z `Tier`, `active`, `LocalDate`) i port `EnvironmentDirectory`, a pakiet `acl` zawiera adapter implementujący port i tłumacza modeli. Typy CMDB nie wychodzą poza pakiet `acl`.

**Klasy:**

- obcy system (wspólny): `anticorruptionlayer/LegacyCmdbClient.java`, `anticorruptionlayer/LegacyCmdbRecord.java` (`nm`, `envCd`, `statFlg`, `lstDplyDt`),
- przed: `anticorruptionlayer/before/LegacyDeploymentPlanner.java` - `canaryTargets`, `staleEnvironments(today)`, `isProduction(name)` na rekordach CMDB,
- po: `anticorruptionlayer/after/Environment.java`, `EnvironmentDirectory.java` (port), `DeploymentPlanner.java` (reguły w języku domeny), `anticorruptionlayer/after/acl/CmdbEnvironmentDirectory.java` (adapter), `anticorruptionlayer/after/acl/CmdbTranslator.java` (pakietowo-prywatny tłumacz, jedyne miejsce znające kody i formaty).

**Na co zwrócić uwagę:**

- Reguła "canary tylko poza produkcją" była zapisana jako `"T".equals(...) || "D".equals(...)`, a po zmianie jako `tier() != PRODUCTION`. Dla znanych kodów to to samo. Dla nieznanego kodu (np. `Q`) - nie: legacy cicho go pomijało, a tłumacz rzuca `IllegalArgumentException("unknown CMDB environment code: Q")`.
- Tłumaczenie jest **zachłanne**: `all()` tłumaczy wszystkie rekordy, także nieaktywne. Zła data w nieaktywnym rekordzie w legacy nie miała znaczenia dla `canaryTargets` (data nie była parsowana), a po zmianie zatrzymuje planowanie (`invalid CMDB date: ...`). Test różnicowy obejmuje tylko poprawne dane - dla danych błędnych potrzebna jest decyzja (odrzuć, pomiń z logiem, kwarantanna) i osobny test.
- Flaga `9` (wycofane) i `0` (wyłączone) obie tłumaczą się na `active = false`. Jeśli domena kiedyś będzie musiała je rozróżnić, zmieni się model domeny, nie reguły CMDB.
- Domenę można testować bez żadnego typu CMDB (`domainCanBeTestedWithoutAnyLegacyType` sprawdza też refleksyjnie, że komponenty `Environment` nie pochodzą z pakietu obcego systemu).
- Facade a ACL: fasada upraszcza interfejs, ale zostaje w tym samym modelu; ACL tłumaczy między dwoma modelami i często używa fasady lub adaptera w środku (komentarz w `CmdbEnvironmentDirectory`).
- ACL jest naturalnym elementem Strangler Fig i Branch by Abstraction: gdy CMDB zostanie zastąpione nowym rejestrem, wymienia się tylko implementacja `EnvironmentDirectory`.

**Testy:** `AntiCorruptionLayerEquivalenceTest`

- `planningRulesGiveTheSameAnswersThroughTheAcl` - `canaryTargets`, `staleEnvironments`, `isProduction` dla sześciu rekordów (aktywne, wyłączone, wycofane) i niezależne oczekiwania,
- `translatorMapsLegacyCodesToTheDomainModel` - pierwszy i ostatni rekord przetłumaczone na `Environment`,
- `unknownLegacyDataIsRejectedInsideTheAcl` - nieznany kod i data w złym formacie,
- `domainCanBeTestedWithoutAnyLegacyType` - port zastąpiony lambdą z modelem domenowym.

**Demonstracja na żywo:**

1. Pokaż `LegacyDeploymentPlanner` i poproś grupę o przeczytanie reguły `canaryTargets` na głos w języku biznesu.
2. Pokaż `Environment`, `EnvironmentDirectory` i `DeploymentPlanner`; potem pakiet `acl`.
3. Uruchom test; zatrzymaj się przy `unknownLegacyDataIsRejectedInsideTheAcl` i zapytaj: "Co robiła wersja legacy z kodem `Q`?" (pomijała go po cichu).
4. Pytanie: "Kto powinien zdecydować, co robić z niepoprawnym rekordem CMDB?" i "Czym to różni się od fasady z przykładu 14?"

---

## Przykład 16. Rodzina Composite i Builder (`composite`)

Pakiet `composite` obsługuje kilka sekcji teorii jednocześnie: Builder (13), Visitor (16) oraz przegląd technik Composite i Collecting Parameter (17). Warto pokazywać w kolejności: 16b, 16a, 16d, a 16c tylko wspomnieć.

### 16a. Encapsulate Composite with Builder

**Sekcja teorii:** 13.1-13.2.

**Co ilustruje:** `PlanBuilder` ukrywa szczegóły budowy drzewa, pilnuje jednorazowości i oddaje niemutowalny wynik.

**Klasy:** `composite/after/PlanBuilder.java` (brak osobnej wersji "przed"; punktem odniesienia jest ręczne `new DeploymentGroup(name, List.of(...))`).

```java
DeploymentGroup plan = PlanBuilder.group("release")
        .group("database", group -> group
                .task("backup", 5)
                .task("migrate", 8))
        .task("deploy", 3)
        .build();
```

**Na co zwrócić uwagę:**

- Builder jest jednorazowy: każde wywołanie po `build()` rzuca `IllegalStateException("builder has already been used")`, i to przed walidacją argumentów (test z `task(" ", -1)`).
- `group(name, definition)` wywołuje lambdę synchronicznie i od razu buduje dziecko; zachowanie lambdy do późniejszego wywołania byłoby zmianą kontraktu.
- Publiczny konstruktor `DeploymentGroup` pozwala współdzielić poddrzewo (DAG); akumulacja policzy je dla każdej ścieżki.
- Scena s06 warsztatu pokazuje ten sam ruch na drzewie repertuaru (dzień - sala - seans).

### 16b. Replace Implicit Tree with Composite

**Sekcja teorii:** 17.3.

**Co ilustruje:** hierarchia zakodowana w ścieżkach tekstowych (`release/database/backup`) zastąpiona jawnym drzewem `PlanComponent` oraz mapper migracyjny ze starego formatu.

**Klasy:**

- przed: `composite/before/LegacyPathPlan.java` - `Entry(path, minutes)`, walidacja ścieżki (`invalid path: ...`, `task path must contain a parent`), `totalMinutes()` z `Math.addExact`, `taskNamesBelow(path)` przez `startsWith(prefix + "/")`,
- po: `composite/after/PlanComponent.java` (`sealed`, `name`, `totalMinutes`, `collectTasks`, `accept`), `DeploymentTask.java` (liść), `DeploymentGroup.java` (grupa, `List.copyOf`), `LegacyPathPlanMapper.java` (mapper).

**Na co zwrócić uwagę:**

- Mapper przyjmuje jawne `rootName`, dzięki czemu można odwzorować pusty plan.
- Mapper odrzuca: wpis spoza korzenia, ścieżkę będącą jednocześnie zadaniem i grupą, oraz dane niezgodne z przejściem w głąb (`release/a/first`, `release/b/second`, `release/a/third`). Ta ostatnia reguła chroni kolejność: grupowanie w drzewo zmieniłoby kolejność zadań. To świadomy wybór "błąd zamiast cichej zmiany".
- Powtórzone zadania są zachowane.
- Ryzyka rekurencji: `StackOverflowError` dla bardzo głębokich drzew, cykle, współdzielone poddrzewa liczone wielokrotnie, przepełnienie sumy (tu `Math.addExact`).
- `List.copyOf` chroni listę, nie elementy; tutaj elementy są rekordami, więc graf jest niemutowalny.

### 16c. Replace One/Many Distinctions with Composite

**Sekcja teorii:** 17.4.

**Klasy:** przed `composite/before/LegacyPlanExecutor.java` (`execute(Task)` i `executeAll(List<Task>)`), po `composite/after/PlanExecutor.java` (`execute(PlanComponent)`).

**Na co zwrócić uwagę:** kolejność preorder zgodna z kolejnością dzieci, pusta grupa daje pustą listę, pierwszy wyjątek przerwałby wykonanie. Przykład jest bliski Safe Composite: `components()` jest tylko w `DeploymentGroup`, liść nie ma `add`.

### 16d. Collecting Parameter i Visitor

**Sekcja teorii:** 16.1-16.2, 17.5.

**Klasy:** `PlanComponent.collectTasks(Collection<? super DeploymentTask>)` w `DeploymentTask` i `DeploymentGroup`; `composite/after/PlanVisitor.java`, `TotalMinutesVisitor.java`.

**Na co zwrócić uwagę:**

- `collectTasks` tylko dopisuje do kolekcji wywołującego (nie czyści jej) i może zostawić wynik częściowy po wyjątku.
- Visitor preferuje dodawanie nowych operacji przy stabilnych typach węzłów; `sealed PlanComponent` do tego pasuje. Nowy rodzaj węzła wymaga zmiany każdego Visitora.
- Alternatywa w nowoczesnej Javie: `switch` z dopasowaniem wzorców po `sealed PlanComponent` daje wyczerpujące sprawdzenie bez `accept`. Warto zapytać grupę, którą wersję wybraliby dla jednej operacji.
- Wynik `Long` jest opakowany (koszt w gorącej ścieżce).
- Obie ścieżki (metoda domenowa `totalMinutes()` i Visitor) używają `Math.addExact`. W kodzie produkcyjnym po decyzji usuwamy zbędną równoległą ścieżkę akumulacji - to też refaktoryzacja od wzorca.

### Testy pakietu: `CompositeRefactoringsTest`

- `explicitCompositePreservesTheImplicitPathTree` - test różnicowy: 16 minut, kolejność `backup`, `migrate`, `deploy`, topologia (`database`, `deploy`), zapytanie dla korzenia i podgrupy, niezależne oczekiwania liczbowe,
- `pathMapperRejectsAmbiguousOrOrderChangingInput` - biały segment, konflikt zadanie/grupa, niezgodność z przejściem w głąb, wpis spoza korzenia,
- `collectingParameterAndVisitorAccumulateTheSameTree` - dopisywanie do niepustej kolekcji, zgodność Visitora z `totalMinutes()`,
- `oneContractHandlesOneTaskAndAGroup` - `PlanExecutor` równoważny `execute` i `executeAll`,
- `builderIsSingleUseAndBuiltCompositeIsImmutable` - jednorazowość Buildera, `UnsupportedOperationException` przy modyfikacji wyniku,
- `bothAccumulationImplementationsDetectOverflow` - `ArithmeticException` dla obu akumulacji,
- `emptyCompositeHasNeutralBehavior` - pusty Builder i pusty mapper dają równe grupy, suma 0, pusta lista wykonania.

**Demonstracja na żywo (cały pakiet, ok. 10 minut):**

1. Pokaż `LegacyPathPlan.taskNamesBelow` i zapytaj, co się stanie przy zmianie separatora albo gdy nazwa zawiera `/`.
2. Pokaż `PlanComponent`, `DeploymentTask`, `DeploymentGroup`, potem `PlanBuilder` z przykładowym drzewem.
3. Pokaż `TotalMinutesVisitor` i zapytaj: "Visitor czy metoda w węźle, jeśli co sprint dochodzi nowy rodzaj węzła?"
4. Uruchom `CompositeRefactoringsTest`.

---

## Przykład 17. Observer (`observer`)

**Sekcja teorii:** 14.1-14.2. Replace Hard-coded Notifications with Observer.

**Co ilustruje:** publisher z dwoma na stałe wpisanymi odbiorcami (`auditLog`, `metrics`) zastąpiony rejestracją listenerów z uchwytem `Subscription`.

**Klasy:**

- przed: `observer/before/LegacyReleasePublisher.java` - dwa `Consumer` w konstruktorze, `publish` woła je w stałej kolejności,
- po: `observer/after/ReleaseListener.java`, `ReleasePublished.java`, `Subscription.java` (`AutoCloseable` z `close()` bez wyjątku kontrolowanego), `ReleasePublisher.java` (`CopyOnWriteArrayList<Registration>`, `AtomicBoolean` w uchwycie).

**Na co zwrócić uwagę:**

- Przejście z dokładnie dwóch odbiorców na dowolną liczbę jest rozszerzeniem zachowania, nie czystą refaktoryzacją. Najpierw relacja jeden do jednego przez interfejs, kolekcja dopiero w osobnym kroku.
- Kontrakt przykładu: publikacja synchroniczna na wątku wywołującym, kolejność rejestracji, duplikaty dozwolone, fail-fast (pierwszy wyjątek przerywa publikację).
- Iterator `CopyOnWriteArrayList` działa na migawce: listener usunięty podczas publikacji zostanie jeszcze wywołany w tej publikacji, ale nie w następnej.
- Każde `subscribe` tworzy osobny `Registration` z tożsamościowym `equals`, więc uchwyt usuwa dokładnie swoją rejestrację, nawet gdy ten sam listener dodano dwa razy.
- `AtomicBoolean` czyni `close()` idempotentnym; nie służy do atomowości publikacji.
- Subject trzyma silne referencje do listenerów aż do `close()` (ryzyko wycieku). Lambda nie ma stabilnej tożsamości: wyrejestrowujemy przez uchwyt, nie "taką samą" lambdą.
- W porównaniu z przykładem 18: Observer rozsyła zdarzenie do niezależnych odbiorców, a reguły reakcji są w odbiorcach; Mediator skupia reguły współpracy w jednym miejscu.

**Testy:** `ObserverContractTest`

- `preservesSynchronousRegistrationOrderForExistingRecipients` - `audit:`, potem `metric:`, jak w legacy,
- `unsubscriptionDuringPublicationAffectsTheNextSnapshot` - wynik `first:one`, `later:one`, `first:two`,
- `usesFailFastExceptionPolicy` - ta sama instancja wyjątku, drugi listener niewywołany,
- `duplicateRegistrationsRemainIndependentSubscriptions` - zamknięcie jednej z dwóch rejestracji tego samego listenera.

**Demonstracja na żywo:**

1. Pokaż `LegacyReleasePublisher`. Zapytaj, co trzeba zmienić, by dodać trzeciego odbiorcę.
2. Pokaż `ReleasePublisher` i `Subscription`.
3. Przed uruchomieniem `unsubscriptionDuringPublicationAffectsTheNextSnapshot` poproś grupę o przewidzenie wyniku. Potem uruchom.
4. Pytanie: "Czy zmienilibyście politykę na kontynuację po błędzie? Czy to refaktoryzacja?" (nie, zmiana kontraktu).

---

## Przykład 18. Mediator (`mediator`)

**Sekcja teorii:** 15.1-15.4. Komponenty połączone każdy z każdym, reguły współpracy w jednym miejscu, procedura i ryzyka, Observer czy Mediator.

**Co ilustruje:** formularz wydania z czterema komponentami (pole wersji, wybór zatwierdzającego, przycisk publikacji, etykieta statusu), w którym komponenty znają się nawzajem i same wyliczają stan sąsiadów. Reguła etykiety jest skopiowana w dwóch komponentach. Po zmianie komponenty zgłaszają mediatorowi tylko "coś się we mnie zmieniło", a wszystkie reguły współpracy są w `ReleaseFormMediator`.

**Klasy:**

- wspólna migawka: `mediator/FormState.java` (`version`, `versionEditable`, `approverEnabled`, `approver`, `publishEnabled`, `status`),
- przed: `mediator/before/LegacyReleaseForm.java` (łączy komponenty metodami `connect`), `mediator/before/VersionField.java` (zna trzy inne komponenty), `ApproverPicker.java` (powtarza logikę etykiety), `PublishButton.java`, `StatusLabel.java`,
- po: `mediator/after/FormMediator.java` (interfejs: `versionChanged`, `approverChosen`, `publishClicked`), `mediator/after/ReleaseFormMediator.java` (tworzy komponenty i trzyma reguły), `VersionField.java`, `ApproverPicker.java`, `PublishButton.java` (znają tylko `FormMediator`), `StatusLabel.java` (pasywna, nie zna nikogo).

**Na co zwrócić uwagę:**

- Test porównuje migawkę `FormState` **po każdym** z 12 zdarzeń, a nie tylko stan końcowy. Sekwencja zawiera zdarzenia ignorowane (wybór zatwierdzającego przy wyłączonym pickerze, kliknięcie nieaktywnego przycisku, wpisywanie do zablokowanego pola) i zdarzenia cofające stan (`type:oops` czyści wybór).
- Kolejność aktualizacji bywa zachowaniem i rozstrzyga to test, nie intuicja. Ciekawostka na salę: przeniesienie `approverPicker.clear()` za `refresh()` w `versionChanged` jest **nieobserwowalne**, bo `clear()` wywołujemy tylko dla niepoprawnej wersji, a wtedy wynik `refresh()` nie zależy od wyboru. Natomiast usunięcie `clear()` test wykrywa od razu (wybór `anna` przetrwałby `type:oops`).
- Komponenty ignorują zdarzenia, gdy są wyłączone, zanim powiadomią mediator - ta odpowiedzialność została w komponentach, tak jak w legacy.
- Reentrancja: metody ustawiające (`setEnabled`, `clear`, `lock`) są pakietowo-prywatne i nie wołają mediatora. Gdyby wołały, łatwo o nieskończoną pętlę powiadomień.
- Ryzyko God Mediator: mediator dla całego ekranu lub aplikacji szybko staje się God Class. Tu mediator dotyczy jednego formularza i jednej spójnej grupy reguł.
- Mediator ma sens przy wielu wzajemnych regułach; dla dwóch komponentów z jedną zależnością zwykłe wywołanie jest prostsze.

**Testy:** `MediatorEquivalenceTest`

- `sameEventsLeadToTheSameFormStates` - 13 migawek (stan początkowy i po każdym zdarzeniu) identycznych w obu wersjach oraz niezależne oczekiwania dla pierwszej i ostatniej migawki,
- `componentsKnowOnlyTheMediator` - refleksyjnie: pola komponentów są typu `FormMediator`, `String` albo prymitywne.

**Demonstracja na żywo:**

1. Narysuj na tablicy zależności między komponentami `before` (każdy z każdym), potem `after` (gwiazda wokół mediatora).
2. Pokaż `VersionField.type` i `ApproverPicker.choose` z wersji `before` obok siebie - skopiowana logika etykiety.
3. Pokaż `ReleaseFormMediator.versionChanged` i `refresh`.
4. Kontrolna mutacja: najpierw przenieś `approverPicker.clear()` za `refresh()` i zapytaj grupę, czy test zrobi się czerwony (nie - zmiana jest nieobserwowalna). Potem usuń `clear()` całkiem, uruchom test, pokaż czerwoną migawkę po `type:oops`, cofnij.
5. Pytanie: "Gdzie dopiszecie regułę: wersja `0.x` wymaga dwóch zatwierdzających?" i "Czym to się różni od Observera?"

---

## Przykład 19. Polimorfizm (`polymorphism`)

**Sekcja teorii:** 17.1. Replace Conditional with Polymorphism (przegląd).

**Co ilustruje:** kod typu `Kind` i pole `value` o znaczeniu zależnym od rodzaju zastąpione hierarchią `sealed` z podtypami posiadającymi własne, poprawnie nazwane dane.

**Klasy:**

- przed: `polymorphism/before/LegacyDeploymentStep.java` - rekord `(Kind kind, String value)`, walidacja z komunikatem zależnym od rodzaju (`command must not be blank` / `approver must not be blank`), `execute()` ze `switch`,
- po: `polymorphism/after/DeploymentStep.java` (`sealed interface ... permits ScriptStep, ApprovalStep`), `ScriptStep.java` (`command`), `ApprovalStep.java` (`approver`).

**Na co zwrócić uwagę:**

- Model "przed" dopuszcza reprezentację, której poprawność trzeba odtwarzać warunkami; "po" każdy podtyp ma własne pole z właściwą nazwą.
- Komunikaty wyjątków są zachowane (test to sprawdza).
- `sealed` jest świadomą decyzją o zamkniętym zbiorze rodzajów. W systemie wtyczek byłoby nieodpowiednie.
- Znika jedna klasa, pojawiają się trzy typy; zmienia się API tworzenia. Trzeba znaleźć wszystkie miejsca tworzenia, deserializacji i mapowania ORM.
- Pytanie z sekcji 6: "Kiedy wybralibyście State zamiast polimorfizmu?" (gdy rodzaj zmienia się w czasie życia obiektu).

**Testy:** `PolymorphismEquivalenceTest`

- `dispatchesEachStableVariantThroughItsOwnType` - wyniki `executed:deploy.sh` i `approved-by:anna`,
- `preservesInvalidValueContract` - identyczny komunikat dla pustego polecenia skryptu.

**Demonstracja na żywo:** krótko (3 minuty). Pokaż `LegacyDeploymentStep` i zapytaj, co oznacza `value`; potem `DeploymentStep` i podtypy.

---

## Przykład 20. Obiekt typu (`typecode`)

**Sekcja teorii:** 17.2. Replace Type Code with Class (przegląd), także 10.3 (fabryka kanonizująca).

**Co ilustruje:** surowy `String zoneCode` z rozproszoną normalizacją i regułą zatwierdzenia zastąpiony klasą `DeploymentZone` z kanonicznymi instancjami i fabryką `fromCode`.

**Klasy:**

- przed: `typecode/before/LegacyDeploymentRequest.java` - walidacja pustego kodu, `toUpperCase(Locale.ROOT)`, lista dozwolonych kodów `TEST`/`PROD`/`DR`, `requiresApproval()` porównujący napisy,
- po: `typecode/after/DeploymentZone.java` (prywatny konstruktor, stałe `TEST`, `PRODUCTION`, `DISASTER_RECOVERY`, mapa `BY_CODE`, `fromCode`, `code()`, `requiresApproval()`), `DeploymentRequest.java` (rekord z `DeploymentZone zone`).

**Na co zwrócić uwagę:**

- Komunikaty `zoneCode must not be blank` i `unknown zone code: <KOD>` są zachowane w `fromCode`, a nie w rekordzie. `DeploymentRequest` sprawdza już tylko `Objects.requireNonNull(zone, "zone")`.
- Nazwy stałych (`PRODUCTION`) różnią się od kodu trwałego (`PROD`). Do bazy i komunikatów zapisujemy `code()`, nie nazwę pola ani nazwę klasy.
- Kanonizacja: `fromCode("prod")` zwraca tę samą instancję co `DeploymentZone.PRODUCTION`. Tożsamość jest tu świadomą częścią modelu.
- `Locale.ROOT` w normalizacji (pułapka: `toUpperCase()` bez locale, np. tureckie "i").

**Testy:** `TypeCodeEquivalenceTest`

- `preservesMeaningOfEveryKnownCode` - dla `TEST`, `PROD`, `DR`, `prod` ta sama reguła zatwierdzenia i ten sam znormalizowany kod,
- `canonicalizesKnownInstancesAndRejectsUnknownCodes` - `assertSame` dla `prod`, identyczne komunikaty dla `null` i `unknown`.

**Demonstracja na żywo:** krótko. Pytanie: "Klasa wartości czy podtypy?" (tu tylko wartość z regułą, podtypy byłyby nadmiarowe).

---

## Przykład 21. Extract Composite (`extractcomposite`)

**Sekcja teorii:** 17.4. Jeden lub wiele i Extract Composite (przegląd).

**Co ilustruje:** dwie klasy grup (`ReleaseGroup`, `RollbackGroup`) z identycznym przechowywaniem dzieci i sumowaniem; wspólna odpowiedzialność wydzielona do abstrakcyjnej `CompositePlanNode`.

**Klasy:**

- przed: `extractcomposite/before/LegacyPlanNodes.java` (typy zagnieżdżone: `PlanNode`, `TaskNode`, `ReleaseGroup`, `RollbackGroup`),
- po: `extractcomposite/after/PlanNodes.java` (`CompositePlanNode` z prywatną listą i metodami `final`; `ReleaseGroup` i `RollbackGroup` jako puste `final` podklasy).

**Na co zwrócić uwagę:**

- Różnica względem przykładu 16b: tu hierarchia już istnieje, a my usuwamy duplikację obsługi dzieci; w 16b zastępujemy prymitywną reprezentację drzewem.
- Podobne pętle mogą mieć różne kontrakty (pomijanie nieaktywnych, kontynuacja po błędzie, równoległość). Wydzielenie jest poprawne tylko przy potwierdzonej zgodności.
- Model jest celowo mutowalny i można utworzyć cykl. Wykrywanie cykli to osobna zmiana zachowania.
- `children()` zwraca kopię (`List.copyOf`), więc modyfikacja widoku rzuca `UnsupportedOperationException`.

**Testy:** `ExtractCompositeEquivalenceTest`

- `extractedSuperclassCentralizesChildStorageAndAccumulation` - ta sama suma i liczba dzieci,
- `extractedCompositeDefensivelyExposesChildren` - `children().clear()` rzuca `UnsupportedOperationException`.

**Demonstracja na żywo:** pokaż obok siebie `ReleaseGroup` i `RollbackGroup` z `LegacyPlanNodes` (diff w IDE), potem `CompositePlanNode`. Pytanie: "Co musielibyście sprawdzić, zanim uznacie te dwie pętle za identyczne?"

---

## Przykład 22. Singleton (`singleton`)

**Sekcja teorii:** 17.6. Limit Instantiation with Singleton (przegląd); kontrapunkt do przykładu 2.

**Co ilustruje:** klasa z niemutowalnym ustawieniem (`Duration.ofSeconds(30)`), którą każdy klient może instancjonować, zamieniona na enum singleton.

**Klasy:** przed `singleton/before/LegacyDeploymentDefaults.java`, po `singleton/DeploymentDefaults.java` (uwaga: wersja "po" leży bezpośrednio w pakiecie `singleton`, nie w `after`).

**Na co zwrócić uwagę:**

- Enum daje jedną stałą na loader klas, bezpieczną publikację, poprawną serializację i blokadę refleksyjnego tworzenia. Nie gwarantuje jednej instancji w całym procesie (kilka loaderów).
- Przykład jest niemutowalny - i dlatego Singleton jest tu nieszkodliwy. Porównaj z przykładem 2: globalny **mutowalny** Singleton łączy testy, wymaga synchronizacji, utrudnia wielu tenantów i zamykanie zasobów, więc tam idziemy w przeciwnym kierunku (Inline Singleton).
- Usunięcie publicznego konstruktora jest zmianą API. Jeśli instancje miały osobny cache, licznik lub blokadę, przejście na Singleton nie jest refaktoryzacją.

**Testy:** `SingletonContractTest`

- `enumLimitsInstantiationWithoutChangingTheDefaultValue` - legacy tworzy różne obiekty, enum zawsze tę samą stałą; wartość 30 s zachowana,
- `allParallelAccessesObserveTheSameEnumConstant` - 1000 równoległych odczytów daje jedną tożsamość.

**Demonstracja na żywo:** krótko, w parze z przykładem 2. Pytanie otwierające: "Czy macie dowód (pomiar), że wiele instancji to problem?"

---

## Test demonstracyjny całego modułu

`Module5ExamplesTest.runsAllModuleExamples` uruchamia `Module5Examples.main` i sprawdza jedynie brak wyjątku. Warto uruchomić go na początku zajęć jako "smoke test" środowiska.

## Wskazówki ogólne dla prowadzącego

- Przy każdym przykładzie zadawaj to samo pytanie: "Jaka jest granica obserwowalnego zachowania i który test ją chroni?"
- Przy parach wzorców (Command/Strategy/State, Strategy/Template Method, Chain/Decorator, Adapter/Boundary Object, Facade/ACL, Observer/Mediator) najpierw pokaż oba przykłady, potem tabelę porównawczą ze slajdów. Uczestnicy sami formułują kryterium.
- Zwracaj uwagę na miejsca, gdzie przykład świadomie zmienia moment lub typ błędu (Strategy: konstruktor; Command: kompletność rejestru; Boundary Object i ACL: walidacja na granicy; Inline Singleton: niezmienne flagi; Facade: komunikat dla `null`). To dobre punkty do dyskusji "refaktoryzacja czy zmiana kontraktu" (slajd 3.3).
- Przykłady 1 i 2 pokazują kierunek "od wzorca". Wracaj do nich, gdy grupa zaczyna wprowadzać wzorce bez potrzeby.
- Mapa decyzji (3 slajdy) nadaje się na podsumowanie po przykładach i przed Ćwiczeniem 4.
