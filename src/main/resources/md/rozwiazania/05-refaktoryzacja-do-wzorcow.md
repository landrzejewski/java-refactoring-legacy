# Moduł 5. Refaktoryzacja do wzorców - rozwiązania dla prowadzącego

Numeracja i nazwy zadań są zgodne z plikiem zadań. Ścieżki Javy są względne względem katalogu repozytorium. Kod referencyjny leży w pakietach `after` w `src/main/java/pl/training/module5/`, testy referencyjne w `src/test/java/pl/training/module5/`.

Rozwiązania ćwiczeń zostały opracowane na podstawie kodu referencyjnego i slajdów modułu. Kod referencyjny jest jednym poprawnym wynikiem - dopuszczalne są inne struktury, jeśli spełniają kryteria akceptacji, a zespół potrafi wskazać, który test chroni każdą decyzję.

---

## Ćwiczenie 1: usunięcie zbędnego wzorca

### Kod referencyjny

- `src/main/java/pl/training/module5/middleman/after/ReleaseDashboard.java`, `src/main/java/pl/training/module5/middleman/after/DeploymentRegistry.java`,
- `src/main/java/pl/training/module5/inlinesingleton/after/FeatureFlags.java`, `RolloutService.java`, `RolloutCompositionRoot.java`,
- testy: `MiddleManEquivalenceTest`, `InlineSingletonEquivalenceTest`.

### Część A: ocena pośrednika

| Punkt | Ocena dla `ReleaseService` | Uzasadnienie |
| --- | --- | --- |
| autoryzacja | nie | brak sprawdzeń uprawnień |
| transakcje | nie | brak granicy transakcji, rejestr to mapa w pamięci |
| telemetria, logowanie | nie | brak |
| retry, timeout | nie | brak |
| translacja błędów | nie | wyjątki rejestru przechodzą bez zmian |
| cache | nie | brak |
| granica modułu | nie wiadomo, do sprawdzenia | czy `ReleaseService` jest publicznym API modułu dla innych zespołów? |

Wniosek: pośrednik nie realizuje żadnej odpowiedzialności, więc można go usunąć. Jeśli odpowiedź na ostatni punkt brzmiałaby "tak", sensownym wynikiem jest pozostawienie go jako fasady modułu albo okres zgodności z forwarderami oznaczonymi `@Deprecated`.

Sekwencja:

1. Test charakterystyki (wzorzec: `MiddleManEquivalenceTest`): `UNKNOWN` dla nieznanego identyfikatora, aktualizacja, nadpisanie, render; identyfikator `null`, pusty, `"  \t"` (komunikat `deploymentId must not be blank`); `null` jako status (`status must not be null`, stan bez zmian).
2. Przepięcie `ReleaseDashboard` na `DeploymentRegistry` (zmiana parametru konstruktora i wywołania w `render`).
3. Migracja pozostałych klientów (tu tylko testy i `Module5Examples`).
4. Safe Delete `ReleaseService` w osobnym commicie.

Element kontraktu, który się zmienia: komunikat `requireNonNull` w konstruktorze dashboardu (`releaseService must not be null` → `registry must not be null`). Zmienia się też sama sygnatura konstruktora - dla klientów spoza repozytorium to zmiana łamiąca.

### Część B: Inline Singleton

Test pokazujący współdzielony stan (wzorzec: `legacySingletonLeaksStateBetweenUsers`):

```java
@Test
@ResourceLock("LegacyFeatureFlags")
void legacySingletonLeaksStateBetweenUsers() {
    var first = new LegacyRolloutService();
    var second = new LegacyRolloutService();
    try {
        LegacyFeatureFlags.getInstance().enable("canary");
        assertEquals("rel-1: 10% -> 50% -> 100%", second.plan("rel-1"));
        assertEquals(first.plan("rel-1"), second.plan("rel-1"));
    } finally {
        LegacyFeatureFlags.getInstance().reset();
    }
}
```

Sprzątanie wymaga `reset()` w `finally` i blokady zasobu przy testach równoległych. To jest właściwa obserwacja do zapisania: koszt globalnego stanu widać w testach.

Sekwencja:

1. `FeatureFlags` jako niezmienny rekord (`Set.copyOf` w konstruktorze kompaktowym), fabryki `of(...)` i `parse(config)`.
2. Nowa klasa `RolloutService(FeatureFlags flags)` obok `LegacyRolloutService`; klientów przepinamy pojedynczo, a stara klasa zostaje do końca migracji (`LegacyFeatureFlags` nie udostępnia listy flag, więc nie da się z niego zrobić migawki - stąd nowa klasa zamiast konstruktora przejściowego).
3. `RolloutCompositionRoot.create(flagsConfig)` jako jedyne miejsce tworzenia.
4. Usunięcie `LegacyRolloutService` po migracji klientów; `LegacyFeatureFlags` zostaje, dopóki inne moduły z niego korzystają.

Test równoważności: dla każdej kombinacji flag (pusta, `canary`, `dark-launch`, obie, nieznana) ustaw stan singletonu, porównaj plan legacy z planem serwisu z `new FeatureFlags(combination)` (`planIsTheSameForEveryFlagCombination`).

Odpowiedź na pytanie 5: nie w pełni. Wersja legacy pozwala zmienić flagi w trakcie działania (`enable`, `disable`) i serwis od razu to zobaczy; wersja `after` ma flagi niezmienne od startu. Pytanie do właściciela: "Czy ktoś przełącza flagi w locie (konsola administracyjna, skrypt, inny moduł)?" Jeśli tak - potrzebny jest jawny port źródła flag (np. interfejs `FeatureFlagSource` czytany przy każdym `plan`), a nie powrót do singletonu.

### Weryfikacja kryteriów akceptacji

- ocena pośrednika: tabela powyżej, przed pierwszym ruchem,
- identyczne renderowanie i walidacja: `validationRemainsAtTheRegistryBoundary`,
- migracja jednym klientem naraz i osobny commit na Safe Delete,
- jawna zależność: konstruktor `RolloutService(FeatureFlags)`,
- testy bez `reset()`: `servicesWithDifferentFlagsCanCoexistWithoutSharedState`,
- nazwana zmiana kontraktu: notatka o przełączaniu w locie.

### Typowe błędy uczestników

- Usunięcie pośrednika bez sprawdzenia, czy nie jest publicznym API modułu.
- Usunięcie `ReleaseService` w tym samym kroku co przepięcie klienta (brak punktu przywracania).
- Zastąpienie singletonu statyczną metodą fabrykującą, która nadal zwraca współdzieloną mutowalną instancję.
- `FeatureFlags` z mutowalnym `HashSet` przekazanym z zewnątrz (brak kopii defensywnej).
- Przeniesienie `getInstance()` do konstruktora serwisu i ogłoszenie sukcesu (zależność nadal ukryta, tylko w innym miejscu).
- Pominięcie pytania o przełączanie flag w locie.

### Pytania do dyskusji

- Kiedy pośrednik z jedną linią jest wartościowy? (fasada modułu, adapter biblioteki zewnętrznej, seam testowy, miejsce na przyszłą autoryzację - ale tylko z realnym wymaganiem).
- Czy kontener DI z zakresem singleton to nadal Singleton? (cykl życia tak, ale zależność jest jawna i wymienialna w testach).
- Scena s22: co zrobić z regułą `freeSeats` → `0` przed usunięciem `CinemaFacade`? (przenieść do klienta albo do katalogu, osobnym krokiem, z testem).

---

## Ćwiczenie 2: Command, Strategy czy State

### Kod referencyjny

- `src/main/java/pl/training/module5/variability/after/SupportCostPolicy.java` - Strategy,
- `src/main/java/pl/training/module5/variability/after/OperatorCommand.java`, `CommandConsole.java`, `Cluster.java` - Command,
- `src/main/java/pl/training/module5/variability/after/ReleaseLifecycle.java` - State,
- `src/main/java/pl/training/module5/variability/after/ReleaseConsole.java` - kontekst,
- `src/main/java/pl/training/module5/variability/after/package-info.java` - kryterium wyboru,
- test: `src/test/java/pl/training/module5/VariabilityEquivalenceTest.java`.

### Krok 1-2: przypisanie osi i uzasadnienie

| Oś | Kto wybiera wariant i kiedy | Mechanizm | Uzasadnienie |
| --- | --- | --- | --- |
| (a) koszt wsparcia | klient, raz, przy tworzeniu konsoli | Strategy | Stabilna rodzina wymiennych algorytmów; taryfa nie zmienia się w czasie życia obiektu, więc wybór przy konstrukcji jest równoważny wyborowi przy każdym wywołaniu. |
| (b) polecenia operatora | nadawca, przy każdym żądaniu | Command | Żądanie trzeba kolejkować, logować i cofać, więc musi być obiektem, który wie, jak się wykonać i cofnąć. |
| (c) cykl życia wydania | nikt z zewnątrz, obiekt sam po operacji | State | Zachowanie `advance`, `rollback`, `banner` zależy od bieżącego stanu, który zmienia się w wyniku operacji obiektu. |

Uwaga do osi (a): wybór taryfy w konstruktorze jest równoważny, bo `tier` w legacy jest polem `final`. Gdyby taryfa była czytana z konfiguracji przy każdym wywołaniu, przeniesienie wyboru do konstruktora zamroziłoby decyzję (slajd 6.4).

### Krok 3: test równoważności

Wzorzec: `VariabilityEquivalenceTest`. Najważniejsze elementy:

- taryfy: `@EnumSource(CustomerTier.class)`, godziny 0, 1, 10, 33, 100 (dla `ENTERPRISE` 33 × 60 = 1980 < 2000, a 100 × 60 = 6000 > 2000 - obie strony `Math.max`), komunikat `hours must not be negative`,
- polecenia: `scale-up`, `maintenance`, `scale-down`, `scale-down`, potem dwa cofnięcia; dosłowny log jako niezależne oczekiwanie:

```text
scale-up -> instances=3, maintenance=off
maintenance -> instances=3, maintenance=on
scale-down -> instances=2, maintenance=on
scale-down -> instances=1, maintenance=on
undo scale-down -> instances=2, maintenance=on
undo scale-down -> instances=3, maintenance=on
```

- awarie: `submit("reboot")` (`unknown action: reboot`), `undoLast()` bez historii (`nothing to undo`), dwa `scale-down` od dwóch instancji (`cannot scale below one instance`); po każdej awarii porównujemy klasę wyjątku, komunikat, status i log (pomocnik `assertSameFailure`),
- cykl życia: PLANNED → DEPLOYING → LIVE, `advance` w LIVE (wyjątek), `rollback` z LIVE, `rollback` i `advance` w ROLLED_BACK (wyjątki), `rollback` w PLANNED (wyjątek); po każdym kroku status i banner.

### Krok 4-6: sekwencja commitów

1. Test równoważności (bez zmian w kodzie produkcyjnym).
2. Strategy: interfejs `SupportCostPolicy` z jedną metodą `cost(int hours)`; stałe lub klasy dla trzech taryf; fabryka `forTier`; `ReleaseConsole` dostaje politykę w konstruktorze, walidacja `hours < 0` zostaje w kontekście.
3. Command, krok a: `OperatorCommand` z `name`, `execute(Cluster)`, `undo(Cluster)`; `Cluster` jako odbiorca (stan instancji i trybu serwisowego, reguła "nie mniej niż jedna instancja").
4. Command, krok b: kolejka i historia przechowują polecenia zamiast napisów; `runQueued` i `undoLast` wołają `execute` i `undo` bez `switch`. Fabryka `byName` jako jedyne miejsce mapowania nazwy na polecenie.
5. State: prywatny enum stanów z domyślnymi `advance` i `rollback` rzucającymi wyjątek z dotychczasowym komunikatem; banner jako dane stanu; kontekst przypisuje `state = state.advance()`.
6. Usunięcie starych pól i `switch` z kontekstu.

### Krok 7: przesunięcie momentu błędu

W obu wersjach nieznana akcja jest odrzucana przy `submit` (legacy: sprawdzenie `ACTIONS.contains`, `after`: `OperatorCommand.byName`). Gałąź `default` w `runQueued` i `undoLast` legacy jest nieosiągalna. Gdyby uczestnik przeniósł tworzenie polecenia do `runQueued`, błąd przesunąłby się w czasie (po zakolejkowaniu i wykonaniu wcześniejszych poleceń) - to byłaby zmiana zachowania wykryta przez `commandAxisPreservesFailures`.

### Odpowiedź: polecenie `restart`

- legacy: trzy miejsca w jednej klasie - zbiór `ACTIONS`, `switch` w `runQueued`, `switch` w `undoLast` (oraz ewentualnie nowe pole stanu),
- `after`: nowy rekord w `OperatorCommand` (wykonanie i cofnięcie obok siebie) i jeden wpis w `byName`; `CommandConsole` i `ReleaseConsole` bez zmian.

### Weryfikacja kryteriów akceptacji

- brak kombinatorycznej hierarchii: trzy niezależne mechanizmy złożone w `ReleaseConsole`,
- stan nie jest strategią: `ReleaseLifecycle` nie ma publicznego konstruktora z wariantem, stan zmienia sam obiekt,
- polecenia bezstanowe (rekordy bez pól); polecenie, które rzuciło, nie trafia do historii (`history.push` po `execute`),
- jedno miejsce mapowania: `SupportCostPolicy.forTier`, `OperatorCommand.byName`,
- test porównuje wynik, komunikaty, status i log oraz zawiera dosłowny log.

### Typowe błędy uczestników

- Strategy dla cyklu życia ("bo to też `switch`"). Pytanie kontrolne: kto zmienia wariant i kiedy?
- State dla taryfy (taryfa nie zmienia się w wyniku operacji).
- Polecenie przechowujące liczbę instancji sprzed wykonania jako pole współdzielonej instancji (stan poprzedniego żądania).
- Cofnięcie zaimplementowane jako osobny `switch` w konsoli (Command bez korzyści).
- Pusta domyślna implementacja `advance` w stanie zamiast wyjątku.
- Przeniesienie walidacji godzin do każdej strategii.
- Usunięcie `switch` przed migracją wszystkich miejsc tworzenia.
- Porównanie tylko wyniku, bez logu i stanu po błędzie.

### Rozszerzenie: cztery osie w osobnych klasach

| Klasa | Mechanizm | Kod referencyjny |
| --- | --- | --- |
| `LegacyDeploymentCostCalculator` (tryb kosztu) | Strategy | `src/main/java/pl/training/module5/strategy/after/` |
| `LegacyDeploymentStep` (rodzaj kroku) | polimorfizm podtypów | `src/main/java/pl/training/module5/polymorphism/after/` |
| `LegacyRelease` (stan wydania) | State | `src/main/java/pl/training/module5/state/after/Release.java` |
| `LegacyDeploymentDispatcher` (akcja operatora) | Command (rejestr) | `src/main/java/pl/training/module5/command/after/` |

Różnica względem `LegacyReleaseConsole`: rodzaj kroku jest trwały przez całe życie obiektu i niesie własne dane (`command`, `approver`), więc to polimorfizm, nie Strategy. Komendy w `command/after` nie mają cofania - rejestr zastępuje dyspozytor, ale korzyść jest mniejsza niż w `variability`.

### Pytania do dyskusji

- Czy dla trzech taryf Strategy się opłaca? (tak, jeśli taryfy zmieniają się niezależnie lub klient dostarcza własną; nie, jeśli są stałe i lokalne - wtedy `switch` jest czytelny).
- Co się stanie, gdy dodamy nową wartość do `CustomerTier`? (błąd kompilacji w wyczerpującym `switch` w `forTier` - dobra wiadomość).
- Czy `undo` po nieudanym `scale-down` powinno coś cofać? (nie - polecenie nie trafiło do historii; zmiana tej reguły to zmiana zachowania).

---

## Ćwiczenie 3: Template Method → Strategy, Chain czy Decorator

### Kod referencyjny

- `src/main/java/pl/training/module5/templatetostrategy/after/ReleaseNotesExporter.java`, `NotesFormat.java`, `MarkdownFormat.java`, `HtmlFormat.java`, `ContentSelection.java`,
- `src/main/java/pl/training/module5/chain/after/ApprovalHandler.java`, `ApprovalRouter.java` i ogniwa `SizeLimitHandler.java`, `AutomaticApprovalHandler.java`, `TeamLeadHandler.java`, `ReleaseManagerHandler.java`, `ChangeAdvisoryBoardHandler.java`,
- testy: `TemplateToStrategyEquivalenceTest`, `ChainEquivalenceTest`.

### Część A: tabela haków

| Hak | Oś | Nowe miejsce |
| --- | --- | --- |
| `header(version)` | format (znaczniki) **i** zakres (tytuł `Changelog`/`Highlights`) | `NotesFormat.header(title, version)` + `ContentSelection.title()` |
| `include(entry)` | zakres treści | `ContentSelection.filter` / `includes(entry)` |
| `formatEntry(entry)` | format | `NotesFormat.entry(entry)` |
| `footer()` | format | `NotesFormat.footer()` |

Kluczowa obserwacja: hak `header` mieszał dwie osie, bo tytuł był stałą w kodzie podklasy. Stąd nowa sygnatura z parametrem `title`.

Sekwencja:

1. Test parametryzowany dla czterech podklas (wzorzec: `everySubclassCombinationHasAnEquivalentComposition`) z wpisami `Blue-green deployments`, `Retry <flaky> health check` (FIX), `Drop v1 API & legacy tokens` (BREAKING) oraz z pustą listą; dosłowne oczekiwanie dla HTML Highlights (`htmlHighlightsKeepEscapingAndFiltering`).
2. Extract Interface `NotesFormat` i rekord `ContentSelection`; implementacje `MarkdownFormat`, `HtmlFormat` (z jedną kopią `escape`), stałe `CHANGELOG` i `HIGHLIGHTS`.
3. Przejściowo: podklasy legacy delegują haki do strategii (test nadal zielony).
4. Klasa finalna `ReleaseNotesExporter(format, selection)` ze szkieletem `export`.
5. Migracja miejsc tworzenia, usunięcie podklas.
6. Nowa kombinacja: `new ContentSelection("Breaking changes", entry -> entry.kind() == Kind.BREAKING)` z `MarkdownFormat` (`newCombinationNeedsNoNewSubclass`).

Ucieczka znaków: `HtmlFormat` ucieka wersję i opis wpisu, ale nie tytuł - legacy też nie uciekało tytułu (był literałem). Uciekanie tytułu "na wszelki wypadek" byłoby zmianą wyniku dla tytułów z `&` - poprawną, ale osobną decyzją.

### Część B: łańcuch zatwierdzania

Liczba przypadków: 9 rozmiarów × 3 ryzyka × 2 środowiska = 54 (`chainPreservesEveryLegacyRoutingDecision` liczy porównania i sprawdza tę liczbę).

Gałąź zależna od wcześniejszych: `RELEASE_MANAGER` z warunkiem `risk != HIGH || !production`. Sama w sobie przyjęłaby także małe zmiany o niskim ryzyku - jest poprawna tylko dlatego, że wcześniej obsłużono `AUTOMATIC` i `TEAM_LEAD`. Podobnie `CHANGE_ADVISORY_BOARD` (`linesChanged <= 2000`) przyjąłby każdą małą zmianę.

Żądanie zmieniające wynik po przestawieniu `TEAM_LEAD` i `RELEASE_MANAGER`: `new ChangeRequest("CR-1", 300, Risk.MEDIUM, true)` - przed: `TEAM_LEAD` (`moderate change`), po przestawieniu: `RELEASE_MANAGER`. Kontrolna mutacja w `ApprovalRouter.standard()` daje czerwony test na siatce.

Sekwencja:

1. Test na siatce i test osiągalności każdego zatwierdzającego (`everyApproverIsReachable`).
2. Extract Method dla każdej gałęzi w `LegacyApprovalRouter` (warunek i decyzja).
3. Abstrakcyjne ogniwo `ApprovalHandler`: `handle`, chronione `passOn`, koniec łańcucha w `passOn` (`no approver for <id>`), składanie przez `then(next)`.
4. Pierwsze ogniwo `SizeLimitHandler`; router: ogniwo + reszta starej metody jako ogniwo przejściowe. Test.
5. Kolejne ogniwa po jednym, test po każdym.
6. Złożenie w `ApprovalRouter.standard()`; usunięcie `if/else`.

Uwaga o końcu łańcucha: zmiany wysokiego ryzyka na produkcji powyżej 2000 linii przechodzą przez `ChangeAdvisoryBoardHandler` i "spadają" z końca łańcucha jako `REJECTED` - dokładnie jak `else` w legacy. Testy `endOfTheChainRejectsUnhandledRequests` i `exactlyOneHandlerHandlesTheRequest` używają ogniw liczących wywołania.

### Część C: audyt decyzji

Rekomendacja: **Decorator** wokół `ApprovalRouter` (albo wokół pierwszego ogniwa), np. `AuditedApprovalRouter`, który woła rdzeń i zapisuje każdą decyzję. Uzasadnienie:

- audyt musi pracować dla **każdego** żądania i każdej decyzji, niezależnie od tego, które ogniwo obsłużyło żądanie,
- w łańcuchu pracuje zwykle jedno ogniwo; ogniwo audytu na początku musiałoby zawsze przekazywać dalej i dopiero po powrocie zapisywać wynik - czyli zachowywałoby się jak dekorator przebrany za ogniwo,
- ogniwo audytu na końcu nie zobaczy decyzji podjętych wcześniej.

Do zapisania: polityka błędu audytu (czy awaria zapisu blokuje decyzję) - analogicznie do `AuditedDeploymentRunner`.

### Weryfikacja kryteriów akceptacji

- N + M klas: dwa formaty i dwa zakresy jako cztery obiekty, nie cztery podklasy; `ReleaseNotesExporter` jest `final` (`exporterIsClosedForInheritance`),
- ucieczka znaków jak w legacy: test HTML Highlights,
- dokładnie jedno ogniwo i decyzja na końcu łańcucha: testy z `CountingHandler`,
- kolejność w jednym wyrażeniu: `ApprovalRouter.standard()`,
- siatka granic i niezależne oczekiwania: 54 przypadki i `everyApproverIsReachable`.

### Typowe błędy uczestników

- Przeniesienie tytułu do `NotesFormat` (tytuł należy do zakresu - dla nowej kombinacji trzeba by tworzyć nowy format).
- Dwie kopie `escape` pozostawione po zmianie.
- Strategia formatu z metodą `export` zawierającą pętlę (szkielet rozjeżdża się między formatami).
- Ogniwa łańcucha napisane jako niezależne predykaty z pełnymi warunkami "na wszelki wypadek" - niby bezpieczniej, ale zmienia to semantykę przy przestawianiu i duplikuje progi.
- `null` zamiast decyzji na końcu łańcucha.
- Test łańcucha tylko dla "typowych" wartości, bez granic 50/51, 500/501, 2000/2001, 5000/5001.
- Audyt jako ogniwo łańcucha.

### Pytania do dyskusji

- Kiedy zostawilibyście Template Method? (jedna oś, stabilny szkielet, potrzebny dostęp do chronionego stanu - przykład `templatemethod`).
- Czy łańcuch na pięć reguł się opłaca? (gdy są różne procesy zatwierdzania lub reguły zmieniają się niezależnie; inaczej czytelna metoda z testem jest prostsza).
- Jak zapisać zależność od kolejności, żeby nikt jej przypadkiem nie złamał? (nazwy ogniw, komentarz przy złożeniu, test na siatce, ewentualnie ogniwa z jawnymi warunkami wstępnymi w testach).

---

## Ćwiczenie 4: granice - Adapter, Boundary Object, Facade, ACL

### Kod referencyjny

- `src/main/java/pl/training/module5/boundaryobject/after/BuildReport.java`, `BuildReportMapper.java`, `BuildReports.java`, `CiBuildReportsAdapter.java`, `ReleaseGate.java`,
- `src/main/java/pl/training/module5/facade/after/ReleasePublishingFacade.java`, `ReleaseCli.java`, `ReleaseScheduler.java` (podsystemy pakietowo-prywatne),
- `src/main/java/pl/training/module5/anticorruptionlayer/after/Environment.java`, `EnvironmentDirectory.java`, `DeploymentPlanner.java`, `src/main/java/pl/training/module5/anticorruptionlayer/after/acl/CmdbEnvironmentDirectory.java`, `src/main/java/pl/training/module5/anticorruptionlayer/after/acl/CmdbTranslator.java`,
- testy: `BoundaryObjectEquivalenceTest`, `FacadeEquivalenceTest`, `AntiCorruptionLayerEquivalenceTest`.

### Tabela granic (oczekiwany produkt)

| Granica | Mechanizm | Uzasadnienie | Przesunięcia błędów |
| --- | --- | --- | --- |
| ładunek CI w regule wydania | Adapter + Boundary Object | obcy format danych wchodzi do domeny; trzeba go zwalidować raz, na granicy | brak klucza: `NullPointerException` w domenie → `invalid CI payload: missing <klucz>`; wartość nieliczbowa: `NumberFormatException` → `invalid CI payload: ...`; pokrycie 120: dawniej akceptowane, teraz odrzucane |
| dwóch klientów orkiestruje podsystemy | Facade | ta sama wiedza o kolejności i walidacji w dwóch miejscach | `null` środowiska: `NullPointerException` bez komunikatu → `NullPointerException("environment")` |
| kody CMDB w regułach planowania | Anti-Corruption Layer | dwa różne modele; domena ma mówić własnym językiem | nieznany kod: dawniej pomijany lub nieistotny → `unknown CMDB environment code`; zła data w rekordzie nieaktywnym: dawniej bez znaczenia → `invalid CMDB date` |

### Część A: Boundary Object

- Poprawne ładunki (wzorzec: `domainDecisionsAreTheSameForEveryValidPayload`): `SUCCESS`/82.5/0, `" success "`/80/0 z sha wielkimi i małymi literami, 79.9 (granica pokrycia), `FAILED`/3, `running`/0. Wynik `canRelease` w kolejności identyfikatorów: `true, true, false, false, false`; `summary` np. `build-1 [a1b2c3d] SUCCESS coverage=82.5% failed=0`.
- Dziś: brak klucza `failed` → `NullPointerException` z `payload.get("failed").trim()` w środku reguły; `coverage = "eighty"` → `NumberFormatException` w domenie.
- Po zmianie: `BuildReportMapper.fromPayload` łapie `IllegalArgumentException` (także `NumberFormatException`, która jest jej podklasą) i rzuca `IllegalArgumentException("invalid CI payload: " + przyczyna, przyczyna)`.
- Decyzja: dla danych niepoprawnych to **zmiana kontraktu**. Akceptowalna i zwykle pożądana, ale trzeba ją nazwać, sprawdzić, czy ktoś łapał `NumberFormatException` wyżej, i zatwierdzić osobno. Przypadek pokrycia 120 jest najciekawszy: legacy pozwalało wydać build z nielogicznym raportem, nowa wersja go odrzuca - to zmiana reguły biznesowej, a nie tylko komunikatu.

### Część B: Facade

- Test efektów (wzorzec: `cliProducesTheSameEffectsInTheSameOrder`): dla `publish("rel-42", " PROD ")` lista `stored rel-42.jar`, `rendered rel-42/prod.yaml`, `queued rel-42.jar with rel-42/prod.yaml`, `announced rel-42 on prod`.
- Brak efektów przy błędnym żądaniu: `qa` (`unknown environment: qa`) i `" "` (`releaseId must not be blank`) - `invalidRequestsAreRejectedBeforeAnySideEffect`.
- Różnice kontraktów klientów: scheduler nie walidował środowiska (stałe `staging`), a CLI tak; po zmianie oba przechodzą przez walidację fasady - dla `staging` bez różnicy wyniku. `null` środowiska: różny komunikat (patrz tabela).
- Pusty identyfikator w środku listy schedulera: w obu wersjach wcześniejsze wydania są już opublikowane (efekty zostają), a pętla przerywa się wyjątkiem. Warto to utrwalić testem.
- Zawężenie widoczności podsystemów (osobny commit, `subsystemsAreHiddenBehindTheFacade`) może zepsuć: klientów spoza pakietu, testy w innych pakietach, konfigurację DI tworzącą podsystemy refleksyjnie.
- Czego fasada nie daje: atomowości (awaria `enqueue` zostawia zapisany artefakt i wyrenderowaną konfigurację), retry, transakcji rozproszonej. Kompensacja to nowe zachowanie.

### Część C: Anti-Corruption Layer

- Reguły w języku biznesu: "cele canary to aktywne środowiska poza produkcją, alfabetycznie", "nieświeże to aktywne środowiska bez wdrożenia od 30 dni", "środowisko jest produkcyjne, jeśli ma poziom produkcja". Potrzebny model: `Environment(name, tier, active, lastDeployment)` z `Tier { PRODUCTION, TEST, DEVELOPMENT }`.
- Test równoważności (wzorzec: `planningRulesGiveTheSameAnswersThroughTheAcl`): sześć rekordów, w tym wyłączony (`0`) i wycofany (`9`); oczekiwania `[dev-1, test-a, test-b]` dla canary i `[dev-1, test-b]` dla nieświeżych przy dacie 2026-10-20.
- Nieznany kod środowiska (np. `Q`), rekord aktywny:
  - legacy: `canaryTargets` pomija go po cichu, `isProduction` zwraca `false`, `staleEnvironments` może go zwrócić (kod nie ma znaczenia),
  - po zmianie: każda metoda planera rzuca `IllegalArgumentException("unknown CMDB environment code: Q")`, bo tłumaczenie jest zachłanne (`all()` tłumaczy wszystkie rekordy).
- Niepoprawna data w rekordzie nieaktywnym:
  - legacy: bez znaczenia (filtr `statFlg == 1` przed parsowaniem, `canaryTargets` i `isProduction` w ogóle nie parsują daty),
  - po zmianie: `IllegalArgumentException("invalid CMDB date: ...")` dla każdej metody.
- Decyzja do zapisania (przykładowe opcje): (1) akceptujemy - dane niepoprawne zatrzymują planowanie, bo lepiej nie wdrażać na podstawie śmieci; (2) tłumacz pomija i loguje niepoprawne rekordy - bliżej legacy, ale ukrywa problem; (3) kwarantanna rekordów z raportem. Opcja (2) jest najbliższa czystej refaktoryzacji, ale nawet ona zmienia `staleEnvironments` dla nieznanego kodu. Kod referencyjny wybiera (1) i utrwala ją testem `unknownLegacyDataIsRejectedInsideTheAcl`.

### Weryfikacja kryteriów akceptacji

- domena bez szczegółów obcych systemów: `ReleaseGate` operuje na `BuildReport`, `DeploymentPlanner` na `Environment`, klienci publikacji znają tylko fasadę (`domainCanBeTestedWithoutAnyLegacyType`, `subsystemsAreHiddenBehindTheFacade`),
- równoważność dla danych poprawnych: trzy testy równoważności,
- nazwane przesunięcia błędów: tabela granic,
- kolejność i walidacja przed pierwszym efektem: `invalidRequestsAreRejectedBeforeAnySideEffect`,
- domena testowalna bez typów CMDB: port zastąpiony lambdą.

### Typowe błędy uczestników

- `BuildReport` z polami `String` "żeby było prościej" (obiekt graniczny bez typów i niezmienników to DTO z inną nazwą).
- Mapper publiczny i używany w domenie (wiedza o formacie znowu wycieka).
- Opakowanie wyjątku bez przyczyny (`new IllegalArgumentException(message)` bez `cause`).
- Fasada z osobnymi metodami `upload`, `render`, `enqueue`, `announce` (to Middle Man, nie strażnik kolejności).
- Zawężenie widoczności podsystemów w tym samym kroku co wydzielenie fasady.
- ACL, w którym `LegacyCmdbRecord` jest polem `Environment` "na wszelki wypadek".
- Tłumaczenie kodów w planerze (`"P".equals(...)`) mimo wprowadzenia modelu - tłumacz istnieje, ale reguły go omijają.
- Brak decyzji dla danych niepoprawnych - test równoważności na poprawnych danych daje fałszywe poczucie bezpieczeństwa.

### Pytania do dyskusji

- Kto powinien zdecydować, co robić z niepoprawnym rekordem CMDB? (właściciel procesu planowania, nie programista w trakcie refaktoryzacji).
- Czy fasada powinna należeć do zespołu podsystemów, czy do zespołu klientów? (zależy, czyją wiedzę chroni; ACL zwykle należy do zespołu domeny).
- Jak ACL pomaga w Strangler Fig? (nowy rejestr środowisk to nowa implementacja `EnvironmentDirectory`; domena i testy domeny bez zmian).

---

## Ćwiczenia dodatkowe

### D1: State, Observer i Decorator

Kod referencyjny pokazuje trzy wzorce osobno: `src/main/java/pl/training/module5/state/after/Release.java`, `src/main/java/pl/training/module5/decorator/after/AuditedDeploymentRunner.java`, `src/main/java/pl/training/module5/observer/after/ReleasePublisher.java`; testy `StateEquivalenceTest`, `DecoratorEquivalenceTest`, `ObserverContractTest`. Połączenie (w tym wybór kolejności) jest właściwym produktem ćwiczenia.

- Audyt jako dekoracja operacji (`AuditedDeploymentRunner` wokół rdzenia wdrożenia), nie część obiektu stanu: stany są bezstanowymi stałymi enumu i nie powinny wykonywać efektów. Powiadomienie po udanym wdrożeniu to osobne zdarzenie publikowane przez Observer. Audyt jako listener traci wpisy `start` i `failure`, więc nie jest równoważny z legacy.
- Przykładowa kolejność dla sukcesu: `audit: start:<id>` → rdzeń → `APPROVED -> DEPLOYED` → `audit: success:<id>` → publikacja do listenerów w kolejności rejestracji. Dla awarii rdzenia: `start`, wyjątek, stan bez zmian, `failure:<id>:<Typ>`, ten sam wyjątek dalej, brak publikacji. Inne kolejności (stan przed efektem, outbox) są dopuszczalne, jeśli jawnie zatwierdzone.
- Listener usuwający inną subskrypcję: przy `CopyOnWriteArrayList` usunięty listener jest jeszcze wywołany w bieżącej publikacji, ale nie w następnej (wzorzec: `unsubscriptionDuringPublicationAffectsTheNextSnapshot`, wynik `first:one`, `later:one`, `first:two`).
- Fail-fast: pierwszy wyjątek przerywa publikację, propagowany bez opakowania (`usesFailFastExceptionPolicy`). W chwili wyjątku listenera stan jest już `DEPLOYED` - do udokumentowania.
- Awaria audytu `failure` zasłania wyjątek rdzenia - kod referencyjny tego nie rozwiązuje; decyzja (`addSuppressed`, kanał awaryjny) musi być jawna. Podwójne `close()` uchwytu: brak błędu i brak usunięcia cudzej rejestracji (`duplicateRegistrationsRemainIndependentSubscriptions`).
- Typowe błędy: publikacja pod `synchronized`, `ExecutorService` "dla wydajności", wyrejestrowanie przez `remove(listener)` zamiast uchwytu, dane wydania w polu enumu stanu.

### D2: rodzina refaktoryzacji Composite

Kod referencyjny: `src/main/java/pl/training/module5/composite/after/` (`PlanComponent`, `DeploymentTask`, `DeploymentGroup`, `LegacyPathPlanMapper`, `PlanExecutor`, `PlanBuilder`, `PlanVisitor`, `TotalMinutesVisitor`), test `CompositeRefactoringsTest`.

- Charakterystyka `LegacyPathPlan`: separator `/` bez maskowania; ścieżka pusta, biała, z `/` na początku lub końcu, z `//` albo białym segmentem jest niepoprawna (`invalid path: ...`); zadanie musi mieć rodzica; minuty nieujemne; suma przez `Math.addExact`; `taskNamesBelow` w kolejności listy wejściowej.
- Mapper z jawnym `rootName`: wpis spoza korzenia, konflikt zadanie/grupa i dane niezgodne z przejściem w głąb (`a/first`, `b/second`, `a/third`) to błędy - grupowanie zmieniłoby kolejność. Alternatywy (sortowanie, pole kolejności) to zmiany zachowania.
- `PlanExecutor.execute(PlanComponent)` zastępuje `execute` i `executeAll` (preorder, pusta grupa daje pustą listę).
- `PlanBuilder`: jednorazowy (`builder has already been used`, sprawdzane przed walidacją argumentów), dziecko budowane synchronicznie, wynik niemutowalny.
- `collectTasks` tylko dopisuje i może zostawić wynik częściowy; `TotalMinutesVisitor` sumuje przez `Math.addExact` i podwójną dyspozycję, bez `instanceof`.
- Przypadki brzegowe: `emptyCompositeHasNeutralBehavior`, `bothAccumulationImplementationsDetectOverflow`; głęboka struktura i współdzielone poddrzewo nie mają testu w kodzie referencyjnym - uczestnicy powinni zapisać decyzję (limit głębokości, DAG liczony dla każdej ścieżki).
- Typowe błędy: drzewo budowane ręcznie w teście zamiast przez mapper, ciche sortowanie, rekord grupy bez `List.copyOf`, Transparent Composite z `add` na liściu, zwykłe `+` zamiast `Math.addExact`.

### D3: Mediator

Kod referencyjny: `src/main/java/pl/training/module5/mediator/after/ReleaseFormMediator.java`, `FormMediator.java` i komponenty; test `MediatorEquivalenceTest`.

- Zależności przed: `VersionField` zna `ApproverPicker`, `PublishButton`, `StatusLabel`; `ApproverPicker` zna `VersionField`, `PublishButton`, `StatusLabel`; `PublishButton` zna `VersionField`, `ApproverPicker`, `StatusLabel`. Logika etykiety (`enter version`, `choose approver`, `ready to publish`) jest skopiowana w `VersionField.type` i `ApproverPicker.choose`.
- Sekwencja zdarzeń w teście referencyjnym: `choose:early` (ignorowane - picker wyłączony), `type:1.2` (niepoprawna wersja), `type:1.2.0`, `choose:anna`, `type:oops` (czyści wybór), `click` (ignorowane), `type:1.3.0`, `choose:bob`, `click` (publikacja i blokada), `type:2.0.0`, `choose:carol`, `click` (wszystkie ignorowane). Ostatnia migawka: `FormState("1.3.0", false, false, "bob", false, "published 1.3.0")`.
- Kontrolna mutacja: usunięcie `approverPicker.clear()` w `versionChanged` zostawia wybór `anna` po `type:oops`, a po `type:1.3.0` przycisk staje się aktywny bez nowego wyboru - test migawek po każdym zdarzeniu to wykrywa. Uwaga: samo przeniesienie `clear()` za `refresh()` jest nieobserwowalne (dla niepoprawnej wersji `refresh()` nie zależy od wyboru) - jeśli zespół zgłosi, że "mutacja przeżyła", to poprawna obserwacja, a nie dziura w teście.
- Metody `setEnabled`, `clear`, `lock`, `setText` są pakietowo-prywatne i nie wołają mediatora (brak reentrancji).
- `componentsKnowOnlyTheMediator` sprawdza refleksyjnie typy pól komponentów.

---

## Odpowiedzi do pytań sprawdzających

1. **Jakie sygnały wskazują, że wzorzec w kodzie należy usunąć, a nie rozbudować?**
   Jedna implementacja od dawna, interfejs powtarzający API jednej klasy, fabryka z jednym konstruktorem, implementacje rzucające `UnsupportedOperationException`, pośrednik przekazujący wywołania 1:1, globalna instancja ukrywająca zależność, a także sytuacja, w której nowy wariant nadal wymaga zmian w wielu miejscach.

2. **Co trzeba sprawdzić przed usunięciem pośrednika (Remove Middle Man)?**
   Czy pośrednik nie realizuje autoryzacji, transakcji, telemetrii, retry, translacji błędów ani cache, i czy nie jest publiczną granicą modułu. Potem migracja jednego klienta naraz i osobne usunięcie klasy.

3. **Dlaczego Inline Singleton może być zmianą kontraktu, a nie czystą refaktoryzacją?**
   Mutowalny singleton pozwala zmieniać stan w trakcie działania i natychmiast widzą to wszyscy klienci. Niezmienna wartość przekazana przez konstruktor tego nie pozwala. Zmienia się też cykl życia i liczba instancji.

4. **Kto wybiera wariant i kiedy - w Strategy, w Command i w State?**
   Strategy: klient, zwykle raz (przy konstrukcji). Command: nadawca żądania, przy każdym wywołaniu; żądanie jest obiektem, bo trzeba je kolejkować, logować lub cofać. State: nikt z zewnątrz - obiekt sam zmienia wariant w wyniku przejść.

5. **Dlaczego wybór strategii w konstruktorze może zmienić zachowanie?**
   Zamraża decyzję wcześniej niż dawny warunek odczytywany przy każdym wywołaniu (np. zmienna konfiguracja) i przesuwa moment błędu dla `null` z wywołania do konstrukcji.

6. **Kiedy mapy komend nie można uznać za równoważną łańcuchowi warunków?**
   Gdy predykaty się nakładają, liczy się ich kolejność albo wybór nie jest pojedynczym, rozłącznym kluczem. Wtedy właściwym kierunkiem jest raczej Chain of Responsibility.

7. **Po czym poznać, że haki Template Method niosą więcej niż jedną oś zmienności?**
   Liczba podklas rośnie jak iloczyn wariantów (N × M), kod haków jest kopiowany między podklasami, a haki dają się pogrupować według niezależnych powodów zmiany (np. format i zakres treści).

8. **Co traci kod klienta po zastąpieniu podklas delegacją?**
   Typ podklasy (`instanceof`, wstrzykiwanie po typie), dostęp strategii do chronionego stanu bazy, odziedziczone metody i monitor `synchronized`. Zyskuje wymienność i składanie osi.

9. **Dlaczego ogniwa łańcucha zatwierdzania nie są od siebie niezależne?**
   Warunek ogniwa zakłada, że wcześniejsze ogniwa odmówiły obsługi (np. `RELEASE_MANAGER` przyjąłby też małe zmiany). Przestawienie ogniw zmienia wynik, więc kolejność jest regułą biznesową.

10. **Czym różni się Chain of Responsibility od Decoratora? Gdzie umieścisz audyt decyzji?**
    W Decoratorze pracują wszystkie warstwy i każda zawsze deleguje do opakowanego obiektu; w łańcuchu zwykle pracuje jedno ogniwo, a przekazanie dalej następuje tylko, gdy ogniwo nie obsłuży żądania. Audyt decyzji musi działać dla każdego żądania, więc to Decorator wokół routera.

11. **Co tłumaczy Adapter, a co Boundary Object?**
    Adapter tłumaczy interfejs (wywołania, nazwy, protokół, błędy) na kontrakt klienta. Boundary Object tłumaczy dane: zamienia surowy format na niezmienny, zwalidowany obiekt z niezmiennikami. Zwykle adapter zwraca obiekt graniczny.

12. **Jak walidacja na granicy zmienia zachowanie dla danych niepoprawnych?**
    Zmienia typ i moment błędu (np. `NumberFormatException` w domenie → `IllegalArgumentException` na granicy) i może odrzucać dane, które dawniej przechodziły (pokrycie 120, nieznany kod CMDB). To zmiana kontraktu do nazwania i zatwierdzenia.

13. **Czym różni się Facade od Anti-Corruption Layer?**
    Fasada upraszcza interfejs podsystemu, zostając w tym samym modelu. ACL tłumaczy między dwoma różnymi modelami, aby obcy model nie przeciekał do języka domeny; często używa fasady lub adaptera w środku i należy do zespołu domeny.

14. **Kiedy fasada staje się Middle Manem, a kiedy God Class?**
    Middle Manem - gdy tylko przekazuje wywołania 1:1, bez reguł kolejności i walidacji. God Class - gdy zbiera wszystkie operacje wszystkich podsystemów zamiast jednej spójnej operacji.

15. **Czym różni się Observer od Mediatora?**
    Observer rozsyła zdarzenie do niezależnych odbiorców, a reguły reakcji są rozproszone w odbiorcach. Mediator skupia reguły współpracy wielu komponentów w jednym koordynatorze; komponenty znają tylko mediator. Mediator często używa Observera jako mechanizmu powiadomień.

16. **Kiedy `switch` z dopasowaniem wzorców po typie `sealed` jest lepszy od Visitora?**
    Gdy operacji jest niewiele, zestaw typów jest zamknięty (`sealed`) i zależy nam na wyczerpującym sprawdzeniu przez kompilator bez podwójnej dyspozycji i metod `accept`. Visitor zostaje dla wielu operacji na stabilnych typach, zwłaszcza gdy operacje dodają różne zespoły.

17. **Czego nie widzi automatyczny Rename w IDE?**
    Użyć dynamicznych i tekstowych: refleksji, konfiguracji, szablonów, nazw serializowanych (JSON, kolumny, nagłówki CSV), skryptów i konsumentów spoza repozytorium.

18. **Kiedy prosty `switch` jest lepszy od wzorca?**
    Gdy warunek jest mały, lokalny, stabilny, z zamkniętym zestawem wariantów, i czytelniejszy niż dodatkowe typy. Wzorzec ma sens, gdy jego zysk widać w najbliższych zmianach.
