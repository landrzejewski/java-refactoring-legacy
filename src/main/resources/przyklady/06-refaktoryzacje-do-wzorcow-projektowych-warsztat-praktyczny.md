# Moduł 6. Refaktoryzacje do wzorców projektowych - przewodnik prowadzącego po przykładach

## Uruchamianie przykładów i testów

Wszystkie polecenia uruchamiamy z katalogu głównego repozytorium.

### Java

```bash
mvn -q compile && java -cp target/classes pl.training.module6.Module6Examples
mvn test -Dtest='pl.training.module6.**'
```

Testy można też uruchamiać z IDE (pojedyncza klasa testowa albo cały pakiet `pl.training.module6`).

Oczekiwane wyjście `Module6Examples`:

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
```

`Module6Examples` używa wyłącznie wersji `after` (oraz obu wersji Extract Composite). Porównanie przed/po odbywa się w testach.

### C#

```bash
cd csharp && dotnet run --project src/Training.Module6
dotnet test
```

Odpowiedniki: `csharp/src/Training.Module6/<Wzorzec>/Before|After` (katalogi `Strategy`, `TypeCode`, `Polymorphism`, `Factory`, `Decorator`, `State`, `Observer`, `Composite`, `ExtractComposite`, `Adapter`, `Command`, `TemplateMethod`, `Singleton`), testy w `csharp/tests/Training.Module6.Tests`.

### TypeScript

```bash
cd typescript && npm ci && npm run build && npm run module6
npm test
```

Odpowiedniki: `typescript/src/module6/<wzorzec>/before|after` (te same nazwy pakietów co w Javie, małymi literami), punkt wejścia `typescript/src/module6/main.ts`.

---

## Mapa przykładów

Wszystkie ścieżki Javy są względne względem `src/main/java/pl/training/module6/`, testy względem `src/test/java/pl/training/module6/`.

| # | Przykład | Sekcja teorii | Przed | Po | Testy |
| --- | --- | --- | --- | --- | --- |
| 1 | Strategy | 2. Replace Conditional Logic with Strategy | `strategy/before/LegacyDeploymentCostCalculator.java` | `strategy/after/DeploymentCostPolicy.java`, `DeploymentCostCalculator.java`, `StandardCostPolicy.java`, `ExpeditedCostPolicy.java` | `StrategyEquivalenceTest` |
| 2 | Polimorfizm | 3. Replace Conditional with Polymorphism | `polymorphism/before/LegacyDeploymentStep.java` | `polymorphism/after/DeploymentStep.java`, `ScriptStep.java`, `ApprovalStep.java` | `PolymorphismEquivalenceTest` |
| 3 | Obiekt typu | 4. Replace Type Code with Class | `typecode/before/LegacyDeploymentRequest.java` | `typecode/after/DeploymentZone.java`, `DeploymentRequest.java` | `TypeCodeEquivalenceTest` |
| 4 | Factory | 6. Encapsulate Classes with Factory i Extract Factory Class | `factory/before/LegacyProbeService.java`, `HttpProbe.java`, `QueueProbe.java`, `DeploymentProbe.java` | `factory/after/DeploymentProbeFactory.java`, `ProbeService.java`, `DeploymentProbe.java` | `FactoryEquivalenceTest` |
| 5 | Decorator | 7. Move Embellishment to Decorator | `decorator/before/LegacyDeploymentRunner.java` | `decorator/after/DeploymentRunner.java`, `BasicDeploymentRunner.java`, `AuditedDeploymentRunner.java` | `DecoratorEquivalenceTest` |
| 6 | State | 8. Replace State-Altering Conditionals with State | `state/before/LegacyRelease.java` | `state/after/Release.java` | `StateEquivalenceTest` |
| 7 | Observer | 9. Replace Hard-coded Notifications with Observer | `observer/before/LegacyReleasePublisher.java` | `observer/after/ReleasePublisher.java`, `ReleaseListener.java`, `ReleasePublished.java`, `Subscription.java` | `ObserverContractTest` |
| 8a | Composite (ukryte drzewo) | 10. Replace Implicit Tree with Composite | `composite/before/LegacyPathPlan.java` | `composite/after/PlanComponent.java`, `DeploymentTask.java`, `DeploymentGroup.java`, `LegacyPathPlanMapper.java` | `CompositeRefactoringsTest` |
| 8b | Builder | 5. Encapsulate Composite with Builder | brak osobnej wersji (ręczne `new DeploymentGroup(...)`) | `composite/after/PlanBuilder.java` | `CompositeRefactoringsTest` |
| 8c | Jeden lub wiele | 14. Replace Distinctions with Composite | `composite/before/LegacyPlanExecutor.java` | `composite/after/PlanExecutor.java` | `CompositeRefactoringsTest` |
| 8d | Collecting Parameter i Visitor | 16. Move Accumulation to Collecting Parameter i Visitor | pętle w `LegacyPathPlan` | `composite/after/PlanComponent.collectTasks`, `PlanVisitor.java`, `TotalMinutesVisitor.java` | `CompositeRefactoringsTest` |
| 9 | Extract Composite | 17. Extract Composite | `extractcomposite/before/LegacyPlanNodes.java` | `extractcomposite/after/PlanNodes.java` | `ExtractCompositeEquivalenceTest` |
| 10 | Adapter | 11. Unify Interfaces with Adapter | `adapter/before/LegacyNotificationClient.java` | `adapter/after/ReleaseNotifier.java`, `ReleaseMessage.java`, `LegacyMessageGateway.java`, `LegacyGatewayAdapter.java`, `NotificationService.java` | `AdapterEquivalenceTest` |
| 11 | Command | 12. Replace Conditional Dispatcher with Command | `command/before/LegacyDeploymentDispatcher.java` | `command/after/DeploymentAction.java`, `DeploymentCommand.java`, `PauseDeployment.java`, `RollbackDeployment.java`, `DeploymentCommandDispatcher.java` | `CommandEquivalenceTest` |
| 12 | Template Method | 13. Apply Template Method | `templatemethod/before/LegacyKeyValueReleaseImporter.java`, `LegacyPipeReleaseImporter.java` | `templatemethod/after/ReleaseImporter.java`, `KeyValueReleaseImporter.java`, `PipeReleaseImporter.java` | `TemplateMethodEquivalenceTest` |
| 13 | Singleton | 15. Limit Instantiation with Singleton | `singleton/before/LegacyDeploymentDefaults.java` | `singleton/DeploymentDefaults.java` | `SingletonContractTest` |
| - | Demonstracja całości | wszystkie | - | `Module6Examples.java` | `Module6ExamplesTest` (uruchamia `main` bez wyjątku) |

Sugerowana kolejność na zajęciach: sekcja 1 teorii (tabela "najpierw rodzaj zmienności"), następnie przykłady 1-3 i 6 (cztery twarze tego samego `switch`: Strategy, polimorfizm, obiekt typu, State), 11 (Command), 4 (Factory), 5, 7, 10 (wzorce współpracy), 8a-8d i 9 (struktury), 12, 13. Przykłady 1, 2, 6, 11 przygotowują do Ćwiczenia 1, przykłady 5, 6, 7 do Ćwiczenia 2, przykłady 8a-8d do Ćwiczenia 3.

---

## Przykład 1. Strategy (`strategy`)

**Sekcja teorii:** 2. Replace Conditional Logic with Strategy.

**Co ilustruje:** zastąpienie `switch` wybierającego wariant algorytmu wstrzykniętą polityką. Kontekst (`DeploymentCostCalculator`) zachowuje wspólną walidację, a samo obliczenie deleguje.

**Klasy:**

- przed: `strategy/before/LegacyDeploymentCostCalculator.java` - metoda `calculate(long, DeploymentMode)`, walidacja `baseCostInCents < 0`, `switch` po `STANDARD`/`EXPEDITED`, `Objects.requireNonNull(mode, "mode")`,
- po: `strategy/after/DeploymentCostPolicy.java` (`@FunctionalInterface`), `DeploymentCostCalculator.java` (polityka w konstruktorze, walidacja w `calculate`), `StandardCostPolicy.java`, `ExpeditedCostPolicy.java`.

**Na co zwrócić uwagę:**

- Walidacja wartości ujemnej zostaje w kontekście, nie w strategiach. Test `keepsInputValidationInTheContext` wstrzykuje lambdę `ignored -> 0` i nadal oczekuje wyjątku.
- `Math.addExact` w `ExpeditedCostPolicy` zachowuje politykę przepełnienia (`ArithmeticException`).
- Zmiana semantyki: wybór wariantu przeniósł się z parametru wywołania do konstruktora. Kalkulator ma "zamrożoną" politykę na cały cykl życia. Jeżeli stary kod odczytywał konfigurację przy każdym wywołaniu, to nie jest równoważne.
- `null` dla trybu: przed - `NullPointerException` przy wywołaniu; po - `NullPointerException("policy")` przy konstrukcji. Moment błędu się przesuwa.
- Interfejs funkcyjny nie może być `sealed`; strategia jest punktem otwartym na rozszerzenie.
- Dla jednego stabilnego warunku Strategy może być przerostem formy.

**Testy:** `StrategyEquivalenceTest`

- `preservesEveryLegacyCalculationVariant` - obie polityki dla wartości 0, 1, 4, 10 001 (zaokrąglenie dzielenia całkowitego `/ 4`),
- `keepsInputValidationInTheContext` - komunikat `base cost must not be negative`,
- `preservesOverflowPolicyOfTheExpeditedVariant` - `Long.MAX_VALUE` rzuca `ArithmeticException` w obu wersjach.

**Demonstracja na żywo:**

1. Pokaż `LegacyDeploymentCostCalculator`. Pytanie: "Czy ten `switch` to problem?" (odpowiedź teorii: lokalnie nie, uzasadnieniem jest niezależny rozwój polityk).
2. Pokaż `DeploymentCostPolicy` i `DeploymentCostCalculator`. Zapytaj, gdzie powinna zostać walidacja i dlaczego.
3. Uruchom `StrategyEquivalenceTest`. Zwróć uwagę na dobór wartości 1 i 4 (reszta z dzielenia).
4. Pytanie do grupy: "Co się stanie, jeśli tryb był czytany z konfiguracji przy każdym wywołaniu?" (moment wyboru).

---

## Przykład 2. Polimorfizm (`polymorphism`)

**Sekcja teorii:** 3. Replace Conditional with Polymorphism.

**Co ilustruje:** kod typu `Kind` i pole `value` o znaczeniu zależnym od rodzaju zastąpione hierarchią `sealed` z podtypami posiadającymi własne, poprawnie nazwane dane.

**Klasy:**

- przed: `polymorphism/before/LegacyDeploymentStep.java` - rekord `(Kind kind, String value)`, walidacja z komunikatem zależnym od rodzaju (`command must not be blank` / `approver must not be blank`), `execute()` ze `switch`,
- po: `polymorphism/after/DeploymentStep.java` (`sealed interface ... permits ScriptStep, ApprovalStep`), `ScriptStep.java` (`command`), `ApprovalStep.java` (`approver`).

**Na co zwrócić uwagę:**

- Model "przed" dopuszcza reprezentację, której poprawność trzeba odtwarzać warunkami; "po" każdy podtyp ma własne pole z właściwą nazwą.
- Komunikaty wyjątków są zachowane (test to sprawdza).
- `sealed` jest świadomą decyzją o zamkniętym zbiorze rodzajów. W systemie wtyczek byłoby nieodpowiednie.
- Dodanie nowego podtypu `sealed` może ujawnić wyczerpujące `switch` w innych modułach.
- Znika jedna klasa, pojawiają się trzy typy; zmienia się API tworzenia (fabryki statyczne `script`/`approval` zastępują konstruktory podtypów). Trzeba znaleźć wszystkie miejsca tworzenia, deserializacji i mapowania ORM.
- Polimorfizm dotyczy tylko zwykłych metod instancyjnych; hook nie powinien być wołany z konstruktora bazy.

**Testy:** `PolymorphismEquivalenceTest`

- `dispatchesEachStableVariantThroughItsOwnType` - wyniki `executed:deploy.sh` i `approved-by:anna`,
- `preservesInvalidValueContract` - identyczny komunikat dla pustego polecenia skryptu.

**Demonstracja na żywo:**

1. Pokaż `LegacyDeploymentStep` i zapytaj, co oznacza `value` (odpowiedź zależy od `kind`, to zapach).
2. Pokaż `DeploymentStep`, `ScriptStep`, `ApprovalStep`.
3. Uruchom test. Zapytaj: "Kiedy wybralibyście State zamiast polimorfizmu?" (gdy rodzaj zmienia się w czasie życia obiektu).

---

## Przykład 3. Obiekt typu (`typecode`)

**Sekcja teorii:** 4. Replace Type Code with Class.

**Co ilustruje:** surowy `String zoneCode` z rozproszoną normalizacją i regułą zatwierdzenia zastąpiony klasą `DeploymentZone` z kanonicznymi instancjami i fabryką `fromCode`.

**Klasy:**

- przed: `typecode/before/LegacyDeploymentRequest.java` - walidacja pustego kodu, `toUpperCase(Locale.ROOT)`, lista dozwolonych kodów `TEST`/`PROD`/`DR`, `requiresApproval()` porównujący napisy,
- po: `typecode/after/DeploymentZone.java` (prywatny konstruktor, stałe `TEST`, `PRODUCTION`, `DISASTER_RECOVERY`, mapa `BY_CODE`, `fromCode`, `code()`, `requiresApproval()`), `DeploymentRequest.java` (rekord z `DeploymentZone zone`).

**Na co zwrócić uwagę:**

- Komunikaty `zoneCode must not be blank` i `unknown zone code: <KOD>` są zachowane w `fromCode`, a nie w rekordzie. `DeploymentRequest` sprawdza już tylko `Objects.requireNonNull(zone, "zone")`.
- Nazwy stałych (`PRODUCTION`) różnią się od kodu trwałego (`PROD`). Do bazy i komunikatów zapisujemy `code()`, nie nazwę pola ani nazwę klasy.
- Kanonizacja: `fromCode("prod")` zwraca tę samą instancję co `DeploymentZone.PRODUCTION`. Tożsamość jest tu świadomą częścią modelu; klasa nie definiuje `equals`/`hashCode`.
- `Locale.ROOT` w normalizacji (pułapka: `toUpperCase()` bez locale, np. tureckie "i").
- Enum byłby dobry dla prostego zamkniętego zbioru; klasa daje miejsce na aliasy i migrację kodów.

**Testy:** `TypeCodeEquivalenceTest`

- `preservesMeaningOfEveryKnownCode` - dla `TEST`, `PROD`, `DR`, `prod` ta sama reguła zatwierdzenia i ten sam znormalizowany kod,
- `canonicalizesKnownInstancesAndRejectsUnknownCodes` - `assertSame` dla `prod`, identyczne komunikaty dla `null` i `unknown`.

**Demonstracja na żywo:**

1. Pokaż `LegacyDeploymentRequest`. Zapytaj: "Ile miejsc w systemie mogło powtórzyć tę normalizację inaczej?"
2. Pokaż `DeploymentZone` i `DeploymentRequest`.
3. Uruchom test. Zapytaj: "Czy możemy zapisać do bazy `zone.toString()`?" (teoria: tylko jeśli jest jawnie częścią formatu; tu bezpieczniej `code()`).
4. Zapytaj: "Klasa wartości czy podtypy?" (tu tylko wartość z regułą, podtypy byłyby nadmiarowe).

---

## Przykład 4. Factory (`factory`)

**Sekcja teorii:** 6. Encapsulate Classes with Factory i Extract Factory Class.

**Co ilustruje:** dwie transformacje naraz. Encapsulate Classes with Factory ukrywa `HttpProbe`/`QueueProbe` jako prywatne rekordy fabryki; Extract Factory Class zabiera wiedzę o konstrukcji z serwisu do osobnej klasy wstrzykiwanej do `ProbeService`.

**Klasy:**

- przed: `factory/before/DeploymentProbe.java`, `HttpProbe.java`, `QueueProbe.java` (publiczne rekordy z walidacją w konstruktorze), `LegacyProbeService.java` (`switch` tworzący sondę i od razu ją uruchamiający),
- po: `factory/after/DeploymentProbe.java`, `DeploymentProbeFactory.java` (walidacja celu, `switch`, prywatne rekordy), `ProbeService.java` (dostaje fabrykę w konstruktorze).

**Na co zwrócić uwagę:**

- Walidacja przeniosła się z konstruktorów produktów do fabryki, ale komunikaty (`endpoint must not be blank`, `queueName must not be blank`) są zachowane. Prywatne rekordy po zmianie nie walidują same, bo jedyną drogą ich utworzenia jest fabryka.
- `switch` zostaje w fabryce i to jest w porządku: fabryka jest właściwą granicą wiedzy o konstrukcji. Celem nie jest usuwanie każdego warunku.
- Fabryka tworzy nową instancję przy każdym wywołaniu (test `factoryPreservesFreshInstanceSemantics`). Tworzenie wszystkich wariantów z wyprzedzeniem lub cache byłoby zmianą zachowania.
- Ograniczenie widoczności publicznych konstruktorów jest zmianą łamiącą dla klientów zewnętrznych (w bibliotece: etap przejściowy, `@Deprecated`).
- Fabryka nie powinna stać się globalnym rejestrem usług.

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

## Przykład 5. Decorator (`decorator`)

**Sekcja teorii:** 7. Move Embellishment to Decorator.

**Co ilustruje:** audyt wpleciony w metodę `run` wydzielony do dekoratora opakowującego rdzeń o tym samym kontrakcie.

**Klasy:**

- przed: `decorator/before/LegacyDeploymentRunner.java` - `start:` przed walidacją, `success:` po wyniku, `failure:<id>:<SimpleName>` w `catch`, ponowne rzucenie wyjątku,
- po: `decorator/after/DeploymentRunner.java` (`@FunctionalInterface`), `BasicDeploymentRunner.java` (walidacja i wynik `deployed:<id>`), `AuditedDeploymentRunner.java` (dekorator).

Kluczowy fragment dekoratora:

```java
audit.accept("start:" + releaseId);
try {
    String result = delegate.run(releaseId);
    audit.accept("success:" + releaseId);
    return result;
} catch (RuntimeException exception) {
    audit.accept("failure:" + releaseId + ":" + exception.getClass().getSimpleName());
    throw exception;
}
```

**Na co zwrócić uwagę:**

- W wersji legacy walidacja była wewnątrz `try`, więc `start:` jest zapisywany także dla błędnego wejścia. Po zmianie walidacja jest w rdzeniu (delegacie), dzięki czemu kolejność audytu dla błędnego wejścia się nie zmienia.
- Dekorator rzuca dokładnie tę samą instancję wyjątku (`assertSame`).
- Jeżeli `audit.accept("failure...")` sam rzuci wyjątek, zasłoni błąd rdzenia. To wymaga jawnej decyzji (przerwanie, `suppressed`, kanał awaryjny).
- Uwaga: `audit.accept("success...")` jest wewnątrz `try`, więc błąd audytu sukcesu zostanie zarejestrowany jako `failure` (zachowane z wersji legacy, warto pokazać jako ciekawostkę).
- Kolejność dekoratorów jest częścią kontraktu: `audit(retry(core))` a `retry(audit(core))`.
- Dekorator nie jest przezroczysty dla `getClass`, tożsamości, serializacji, monitora `synchronized`, metod fluent zwracających `this` i samowywołań w delegacie.

**Testy:** `DecoratorEquivalenceTest`

- `preservesResultAndAuditOrderOnSuccess` - wynik i lista audytu identyczne,
- `preservesAuditOrderAndPropagatesTheSameFailure` - delegat rzuca wyjątek; audyt `start`, `failure:rel-42:IllegalStateException`; ta sama instancja wyjątku,
- `invalidInputPreservesFailureAndAuditTrace` - pusty `releaseId`: ta sama klasa i komunikat wyjątku oraz ten sam ślad audytu.

**Demonstracja na żywo:**

1. Pokaż `LegacyDeploymentRunner`; poproś grupę o wypisanie śladu audytu dla `" "` (odpowiedź: `start: `, `failure: :IllegalArgumentException`).
2. Pokaż trzy klasy `after`; omów, dlaczego walidacja trafiła do rdzenia.
3. Uruchom test. Pytanie: "Gdzie postawilibyście retry i czy audyt ma liczyć próby, czy operacje?"

---

## Przykład 6. State (`state`)

**Sekcja teorii:** 8. Replace State-Altering Conditionals with State.

**Co ilustruje:** warunki sprawdzające pole `status` w każdej operacji zastąpione obiektami stanu, które zwracają następny stan albo rzucają wyjątek.

**Klasy:**

- przed: `state/before/LegacyRelease.java` - `if (status == ...)` w `approve`, `deploy`, `cancel`, wspólna metoda `invalid(action)`,
- po: `state/after/Release.java` - prywatny `sealed interface ReleaseState` z domyślnymi metodami rzucającymi wyjątek i prywatną metodą `invalid`, cztery enumy jednoelementowe (`DraftState`, `ApprovedState`, `DeployedState`, `CancelledState`), publiczny `enum Status`.

**Na co zwrócić uwagę:**

- Zacznij od tabeli przejść (sekcja 8.1 teorii); to ona jest specyfikacją testu.
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
4. Pytanie: "Czym różni się State od Strategy?" oraz "Gdzie umieścić wysłanie powiadomienia po `deploy`?" (wstęp do Ćwiczenia 2).

---

## Przykład 7. Observer (`observer`)

**Sekcja teorii:** 9. Replace Hard-coded Notifications with Observer.

**Co ilustruje:** publisher z dwoma na stałe wpisanymi odbiorcami (`auditLog`, `metrics`) zastąpiony rejestracją listenerów z uchwytem `Subscription`.

**Klasy:**

- przed: `observer/before/LegacyReleasePublisher.java` - dwa `Consumer` w konstruktorze, `publish` woła je w stałej kolejności,
- po: `observer/after/ReleaseListener.java`, `ReleasePublished.java`, `Subscription.java` (`AutoCloseable` z `close()` bez wyjątku kontrolowanego), `ReleasePublisher.java` (`CopyOnWriteArrayList<Registration>`, `AtomicBoolean` w uchwycie).

**Na co zwrócić uwagę:**

- Przejście z dokładnie dwóch odbiorców na dowolną liczbę jest rozszerzeniem zachowania, nie czystą refaktoryzacją. Teoria zaleca najpierw relację jeden do jednego przez interfejs, kolekcję dopiero w osobnym kroku.
- Kontrakt przykładu: publikacja synchroniczna na wątku wywołującym, kolejność rejestracji, duplikaty dozwolone, fail-fast (pierwszy wyjątek przerywa publikację).
- Iterator `CopyOnWriteArrayList` działa na migawce: listener usunięty podczas publikacji zostanie jeszcze wywołany w tej publikacji, ale nie w następnej.
- Każde `subscribe` tworzy osobny `Registration` z tożsamościowym `equals`, więc uchwyt usuwa dokładnie swoją rejestrację, nawet gdy ten sam listener dodano dwa razy.
- `AtomicBoolean` czyni `close()` idempotentnym; nie służy do atomowości publikacji.
- Subject trzyma silne referencje do listenerów aż do `close()` (ryzyko wycieku).
- Lambda nie ma stabilnej tożsamości: nie wyrejestrowujemy "takiej samej" lambdy, tylko przez uchwyt.

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

## Przykład 8. Rodzina Composite (`composite`)

Pakiet `composite` obsługuje cztery sekcje teorii jednocześnie. Warto pokazywać je w kolejności: 8a, 8c, 8b, 8d.

### 8a. Replace Implicit Tree with Composite

**Sekcja teorii:** 10.

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

### 8b. Encapsulate Composite with Builder

**Sekcja teorii:** 5.

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

### 8c. Replace Distinctions with Composite (jeden lub wiele)

**Sekcja teorii:** 14.

**Klasy:** przed `composite/before/LegacyPlanExecutor.java` (`execute(Task)` i `executeAll(List<Task>)`), po `composite/after/PlanExecutor.java` (`execute(PlanComponent)`).

**Na co zwrócić uwagę:** kolejność preorder zgodna z kolejnością dzieci, pusta grupa daje pustą listę, pierwszy wyjątek przerwałby wykonanie. Przykład jest bliski Safe Composite: `components()` jest tylko w `DeploymentGroup`, liść nie ma `add`.

### 8d. Collecting Parameter i Visitor

**Sekcja teorii:** 16.

**Klasy:** `PlanComponent.collectTasks(Collection<? super DeploymentTask>)` w `DeploymentTask` i `DeploymentGroup`; `composite/after/PlanVisitor.java`, `TotalMinutesVisitor.java`.

**Na co zwrócić uwagę:**

- `collectTasks` tylko dopisuje do kolekcji wywołującego (nie czyści jej) i może zostawić wynik częściowy po wyjątku.
- Visitor preferuje dodawanie nowych operacji przy stabilnych typach węzłów; `sealed PlanComponent` do tego pasuje. Nowy rodzaj węzła wymaga zmiany każdego Visitora.
- Wynik `Long` jest opakowany (koszt w gorącej ścieżce).
- Obie ścieżki (metoda domenowa i Visitor) używają `Math.addExact`. W kodzie produkcyjnym po decyzji usuwamy zbędną równoległą ścieżkę akumulacji.

### Testy pakietu: `CompositeRefactoringsTest`

- `explicitCompositePreservesTheImplicitPathTree` - test różnicowy: 16 minut, kolejność `backup`, `migrate`, `deploy`, topologia (`database`, `deploy`), zapytanie dla korzenia i podgrupy, niezależne oczekiwania liczbowe,
- `pathMapperRejectsAmbiguousOrOrderChangingInput` - biały segment, konflikt zadanie/grupa, niezgodność z przejściem w głąb, wpis spoza korzenia,
- `collectingParameterAndVisitorAccumulateTheSameTree` - dopisywanie do niepustej kolekcji, zgodność Visitora z `totalMinutes()`,
- `oneContractHandlesOneTaskAndAGroup` - `PlanExecutor` równoważny `execute` i `executeAll`,
- `builderIsSingleUseAndBuiltCompositeIsImmutable` - jednorazowość Buildera, `UnsupportedOperationException` przy modyfikacji wyniku,
- `bothAccumulationImplementationsDetectOverflow` - `ArithmeticException` dla obu akumulacji,
- `emptyCompositeHasNeutralBehavior` - pusty Builder i pusty mapper dają równe grupy, suma 0, pusta lista wykonania.

**Demonstracja na żywo (cały pakiet):**

1. Pokaż `LegacyPathPlan.taskNamesBelow` i zapytaj, co się stanie przy zmianie separatora albo gdy nazwa zawiera `/`.
2. Pokaż `PlanComponent`, `DeploymentTask`, `DeploymentGroup`.
3. Pokaż `LegacyPathPlanMapper.map` (bez wchodzenia w szczegóły `validateDepthFirstOrder`); omów przypadek `a/b/a`.
4. Uruchom `explicitCompositePreservesTheImplicitPathTree` i `pathMapperRejectsAmbiguousOrOrderChangingInput`.
5. Pokaż `LegacyPlanExecutor` vs `PlanExecutor`, potem `PlanBuilder`, na końcu `TotalMinutesVisitor`.
6. Pytania: "Które przypadki brzegowe traktujecie jako błąd, a które jako zmianę kontraktu?", "Visitor czy metoda w węźle, jeśli co sprint dochodzi nowy rodzaj węzła?"

---

## Przykład 9. Extract Composite (`extractcomposite`)

**Sekcja teorii:** 17. Extract Composite.

**Co ilustruje:** dwie klasy grup (`ReleaseGroup`, `RollbackGroup`) z identycznym przechowywaniem dzieci i sumowaniem; wspólna odpowiedzialność wydzielona do abstrakcyjnej `CompositePlanNode`.

**Klasy:**

- przed: `extractcomposite/before/LegacyPlanNodes.java` (typy zagnieżdżone: `PlanNode`, `TaskNode`, `ReleaseGroup`, `RollbackGroup`),
- po: `extractcomposite/after/PlanNodes.java` (`CompositePlanNode` z prywatną listą i metodami `final`; `ReleaseGroup` i `RollbackGroup` jako puste `final` podklasy).

**Na co zwrócić uwagę:**

- Różnica względem przykładu 8a: tu hierarchia już istnieje, a my usuwamy duplikację obsługi dzieci; w 8a zastępujemy prymitywną reprezentację drzewem.
- Podobne pętle mogą mieć różne kontrakty (pomijanie nieaktywnych, kontynuacja po błędzie, równoległość). Wydzielenie jest poprawne tylko przy potwierdzonej zgodności.
- `final` na metodach nie zabrania podklasie dodania drugiego pola z dziećmi.
- Model jest celowo mutowalny i można utworzyć cykl (grupa jako własne dziecko). Wykrywanie cykli to osobna zmiana zachowania.
- `children()` zwraca kopię (`List.copyOf`), więc modyfikacja widoku rzuca `UnsupportedOperationException`.

**Testy:** `ExtractCompositeEquivalenceTest`

- `extractedSuperclassCentralizesChildStorageAndAccumulation` - ta sama suma i liczba dzieci,
- `extractedCompositeDefensivelyExposesChildren` - `children().clear()` rzuca `UnsupportedOperationException`.

**Demonstracja na żywo:** pokaż obok siebie `ReleaseGroup` i `RollbackGroup` z `LegacyPlanNodes` (diff w IDE), potem `CompositePlanNode`. Pytanie: "Co musielibyście sprawdzić, zanim uznacie te dwie pętle za identyczne?"

---

## Przykład 10. Adapter (`adapter`)

**Sekcja teorii:** 11. Unify Interfaces with Adapter.

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

## Przykład 11. Command (`command`)

**Sekcja teorii:** 12. Replace Conditional Dispatcher with Command.

**Co ilustruje:** `switch` po `DeploymentAction` zastąpiony niemodyfikowalnym rejestrem komend ze sprawdzeniem kompletności.

**Klasy:**

- przed: `command/before/LegacyDeploymentDispatcher.java` (walidacja `action` i `releaseId`, `switch` PAUSE/ROLLBACK, zagnieżdżony enum),
- po: `command/after/DeploymentAction.java` (enum najwyższego poziomu), `DeploymentCommand.java`, `PauseDeployment.java`, `RollbackDeployment.java`, `DeploymentCommandDispatcher.java`.

**Na co zwrócić uwagę:**

- Mapa jest równoważna tylko dla rozłącznego klucza. Łańcuch nakładających się predykatów ("wygrywa pierwszy") nie jest mapą.
- `Map.copyOf` odcina rejestr od późniejszych zmian mapy wejściowej i odrzuca `null`.
- Sprawdzenie kompletności przez `EnumSet.allOf` przenosi błąd konfiguracji na moment składania aplikacji (`missing commands: [ROLLBACK]`). To nowe zachowanie konstruktora; stary `switch` był kompletny z definicji.
- Walidacja `action` i `releaseId` zostaje w dispatcherze, przed wyszukaniem komendy.
- Command nie daje automatycznie asynchroniczności, kolejki, retry ani undo. Współdzielone komendy muszą być bezstanowe.

**Testy:** `CommandEquivalenceTest`

- `lookupPreservesEveryLegacyDispatchBranch` - PAUSE i ROLLBACK dają te same wyniki,
- `registryIsDefensivelyCopiedAndMustBeComplete` - modyfikacja źródłowej `EnumMap` po konstrukcji nie wpływa na dispatcher; niepełna mapa daje komunikat `missing commands: [ROLLBACK]`.

**Demonstracja na żywo:** pokaż `LegacyDeploymentDispatcher`, następnie `DeploymentCommandDispatcher`; uruchom test. Pytanie: "Gdyby selektorem był łańcuch `if (priority > 5) ... else if (region == EU) ...`, czy mapa by wystarczyła?"

---

## Przykład 12. Template Method (`templatemethod`)

**Sekcja teorii:** 13. Apply Template Method.

**Co ilustruje:** dwa importery z identycznym szkieletem (walidacja wejścia, parsowanie, walidacja pól, utworzenie `ReleaseDraft`) i różnym krokiem parsowania. Szkielet przeniesiony do `final` metody w klasie bazowej.

**Klasy:**

- przed: `templatemethod/before/LegacyKeyValueReleaseImporter.java` (`id=...;service=...`), `LegacyPipeReleaseImporter.java` (`rel|service`), `ReleaseDraft.java`,
- po: `templatemethod/after/ReleaseImporter.java` (`final importRelease`, `abstract parse`, chroniona fabryka `fields`, chroniony rekord `Fields`), `KeyValueReleaseImporter.java`, `PipeReleaseImporter.java`, `ReleaseDraft.java`.

**Na co zwrócić uwagę:**

- W wersji legacy Pipe przycinał pola przed walidacją, a KeyValue przycinał wartości w pętli. Po zmianie oba robią trim w `parse`, a wspólna walidacja `releaseId and service are required` jest w szkielecie. Komunikaty są zachowane.
- `final` na `importRelease` chroni kolejność. Dodanie `final` do opublikowanej metody, którą ktoś nadpisuje, byłoby zmianą łamiącą.
- Chroniona fabryka `fields(...)` jest potrzebna, bo podklasa `ReleaseImporter` spoza pakietu nie mogłaby wywołać chronionego konstruktora `Fields` (nie jest podklasą `Fields`). Test sprawdza to podklasą zagnieżdżoną w innym pakiecie.
- Nowa metoda abstrakcyjna wymusza zmiany we wszystkich podklasach; hooki wołane z konstruktora bazy widzą niezainicjalizowane pola; `synchronized` szablon trzyma monitor podczas hooków.

**Testy:** `TemplateMethodEquivalenceTest`

- `commonSkeletonPreservesBothImportFormats` - oba formaty dają te same wartości co legacy,
- `templateMethodProtectsTheRequiredOrder` - refleksyjnie sprawdza `Modifier.isFinal`,
- `commonValidationPreservesTheLegacyFailure` - `" |payments"`: ta sama klasa i komunikat wyjątku,
- `supportsSubclassOutsideTheImplementationPackage` - `CommaReleaseImporter` w pakiecie testowym.

**Demonstracja na żywo:** pokaż obie klasy legacy obok siebie i poproś grupę o wypisanie kroków; potem pokaż `ReleaseImporter`. Pytanie: "Kiedy lepsza byłaby Strategy parsowania zamiast dziedziczenia?"

---

## Przykład 13. Singleton (`singleton`)

**Sekcja teorii:** 15. Limit Instantiation with Singleton.

**Co ilustruje:** klasa z niemutowalnym ustawieniem (`Duration.ofSeconds(30)`), którą każdy klient może instancjonować, zamieniona na enum singleton.

**Klasy:** przed `singleton/before/LegacyDeploymentDefaults.java`, po `singleton/DeploymentDefaults.java` (uwaga: wersja "po" leży bezpośrednio w pakiecie `singleton`, nie w `after`).

**Na co zwrócić uwagę:**

- Enum daje jedną stałą na loader klas, bezpieczną publikację, poprawną serializację i blokadę refleksyjnego tworzenia. Nie gwarantuje jednej instancji w całym procesie (kilka loaderów).
- Przykład jest niemutowalny. Globalny mutowalny Singleton łączy testy, wymaga synchronizacji, utrudnia wielu tenantów i zamykanie zasobów.
- Usunięcie publicznego konstruktora jest zmianą API. Jeśli instancje miały osobny cache/licznik/blokadę, przejście na Singleton nie jest refaktoryzacją.
- Teoria przypomina transformację odwrotną Inline Singleton i zaleca raczej wstrzykiwanie ustawień (DI z zakresem singleton).

**Testy:** `SingletonContractTest`

- `enumLimitsInstantiationWithoutChangingTheDefaultValue` - legacy tworzy różne obiekty, enum zawsze tę samą stałą; wartość 30 s zachowana,
- `allParallelAccessesObserveTheSameEnumConstant` - 1000 równoległych odczytów daje jedną tożsamość.

**Demonstracja na żywo:** krótko (5 minut). Pytanie otwierające: "Czy macie dowód (pomiar), że wiele instancji to problem?" Potem pytanie 13 z listy sprawdzającej.

---

## Test demonstracyjny całego modułu

`Module6ExamplesTest.runsAllModuleExamples` uruchamia `Module6Examples.main` i sprawdza jedynie brak wyjątku. Warto uruchomić go na początku zajęć jako "smoke test" środowiska.

## Wskazówki ogólne dla prowadzącego

- Przy każdym przykładzie zadawaj to samo pytanie: "Jaka jest granica obserwowalnego zachowania i który test ją chroni?"
- Zwracaj uwagę na miejsca, gdzie przykład świadomie zmienia moment błędu (Strategy: konstruktor; Command: kompletność rejestru) albo przenosi walidację (Factory, Adapter, TypeCode). To dobre punkty do dyskusji "refaktoryzacja czy zmiana kontraktu".
- Tabela z sekcji 19 teorii (mapa decyzji) nadaje się na podsumowanie po przykładach.
