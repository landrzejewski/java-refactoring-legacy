# Moduł 7. Zaawansowane refaktoryzacje - rozwiązania dla prowadzącego

Dokument zawiera rozwiązania wzorcowe Warsztatów 1-3 i ćwiczeń uzupełniających A-I z pliku zadań, typowe błędy uczestników, pytania do dyskusji oraz listę kontrolną przeglądu jako narzędzie oceny.

Materiał teoretyczny modułu nie zawiera rozwiązań warsztatów. Poniższe rozwiązania opierają się na kodzie referencyjnym (pakiety `after` i testy równoważności w `src/test/java/pl/training/module7/`) oraz na sekwencjach bezpiecznych kroków i ryzykach opisanych w teorii.

Przypomnienie organizacyjne: na trzy warsztaty przypada 60 minut, na przegląd i podsumowanie 15 minut. W przeglądzie warto wybrać po jednym rozwiązaniu na warsztat i przejść je z listą kontrolną z końca tego dokumentu.

---

## Warsztat 1. Utworzenie seam przed zmianą integracji

> Rozwiązanie opracowane na podstawie kodu referencyjnego, nie pochodzi wprost z materiału teoretycznego.

### Kod wejściowy i referencyjny

- wejście: `src/main/java/pl/training/module7/breakdependencies/before/LegacyDeploymentWindowService.java`, `src/main/java/pl/training/module7/breakdependencies/before/StandardMaintenanceWindows.java`
- referencja: `src/main/java/pl/training/module7/breakdependencies/after/MaintenanceWindows.java`, `src/main/java/pl/training/module7/breakdependencies/after/DeploymentWindowService.java`, `src/main/java/pl/training/module7/breakdependencies/after/StandardMaintenanceWindows.java`
- composition root w referencji: `src/main/java/pl/training/module7/Module7Examples.java`
- testy referencyjne: `src/test/java/pl/training/module7/BreakDependenciesEquivalenceTest.java`

Reguła "zamrożonych usług" jest propozycją z pliku zadań i nie występuje w kodzie referencyjnym. Poniższy wariant jej implementacji jest przykładowy.

### Sekwencja kroków

1. **Charakterystyka (commit 1).** Testy na kopii i na `LegacyDeploymentWindowService` jako wyroczni:
   - pętla po godzinach 0-23 porównująca decyzje obu wersji,
   - granice okna: 0 i 5 dają `ALLOWED`, 6 i 23 dają `OUTSIDE_MAINTENANCE_WINDOW`,
   - wyjątki: `(null, 2)` daje `NullPointerException("service")`, `("   ", 2)` daje `IllegalArgumentException("service must not be blank")`, `("payments", -1)` i `("payments", 24)` dają `IllegalArgumentException("hourUtc must be between 0 and 23")`, `(" ", -1)` daje błąd usługi (pierwszy sprawdzany warunek wygrywa).
2. **Najmniejsza operacja.** Serwis używa wyłącznie `allows(String service, int hourUtc)`. To jedyna metoda nowego kontraktu.
3. **Seam (commit 2).** Wydziel interfejs funkcyjny `MaintenanceWindows` z jedną metodą. `StandardMaintenanceWindows implements MaintenanceWindows`. Pole serwisu zmienia typ na interfejs i jest ustawiane w konstruktorze z `Objects.requireNonNull(maintenanceWindows, "maintenanceWindows")`. Algorytm `schedule` i `validate` pozostają bez zmian.
4. **Produkcyjne składanie.** Dwie akceptowalne drogi:
   - jak w referencji: brak konstruktora bezargumentowego, a produkcyjna implementacja jest przekazywana w composition root (`new DeploymentWindowService(new StandardMaintenanceWindows())` w `Module7Examples`),
   - wariant przejściowy dla wielu klientów: konstruktor domyślny `this(new StandardMaintenanceWindows())`, usuwany po migracji wywołań.
   Po tym kroku uruchom test różnicowy dla godzin 0-23, aby potwierdzić równoważność produkcyjnego składania.
5. **Spy (commit 3).** Test z lambdą rejestrującą wywołania:

   ```java
   List<String> calls = new ArrayList<>();
   var service = new DeploymentWindowService((svc, hour) -> {
       calls.add("allows:" + svc + ":" + hour);
       return svc.equals("emergency") && hour == 14;
   });

   assertEquals(DeploymentDecision.ALLOWED, service.schedule("emergency", 14));
   assertEquals(List.of("allows:emergency:14"), calls);
   ```

   Drugi test: dla każdego niepoprawnego wejścia lista wywołań pozostaje pusta (odpowiednik `invalidRequestDoesNotReachInjectedDependency`).
6. **Nowa reguła (commit 4, zmiana zachowania).** Przykład: po walidacji, a przed zapytaniem kalendarza, serwis sprawdza listę zamrożonych usług i dla `billing` zwraca `OUTSIDE_MAINTENANCE_WINDOW` bez wywołania zależności. Testy: `billing` o godzinie 2 daje `OUTSIDE_MAINTENANCE_WINDOW`, lista wywołań spy jest pusta; `payments` o godzinie 2 nadal woła kalendarz dokładnie raz. Jeżeli lista zamrożonych usług ma być konfigurowalna, jest to drugi, osobny seam (np. parametr konstruktora typu `Set<String>` lub `Predicate<String>`).

### Jak spełnić kryteria akceptacji

| Kryterium | Jak sprawdzić |
| --- | --- |
| produkcyjna implementacja wybierana w composition root | jedyne `new StandardMaintenanceWindows()` w kodzie produkcyjnym jest w miejscu składania (lub w przejściowym konstruktorze domyślnym z planem usunięcia) |
| test nie uruchamia infrastruktury | testy reguły i interakcji używają lambdy, nie `StandardMaintenanceWindows` |
| kontrakt bez nieużywanych metod | interfejs ma dokładnie jedną metodę `allows`, najlepiej z `@FunctionalInterface` |
| liczba i kolejność wywołań sprawdzone | asercja na całej liście wywołań (`assertEquals(List.of(...), calls)`), nie tylko `calls.size() > 0` |
| testy charakterystyki bez zmian asercji | commit 1 i commit 3 nie modyfikują wcześniejszych asercji |
| reguła w osobnym kroku | osobny commit po zielonych testach refaktoryzacji |

### Typowe błędy uczestników

- Interfejs kopiuje całe API implementacji albo dodaje "na zapas" metody (np. `nextWindow`, `listWindows`).
- Interfejs `sealed` z jedną implementacją. Zamyka zbiór implementacji i uniemożliwia lambdę w teście (teoria: `sealed` nie jest interfejsem funkcyjnym).
- Tworzenie `StandardMaintenanceWindows` przeniesione do statycznej fabryki lub singletona, co zmienia czas życia i współdzielenie zależności bez decyzji.
- Walidacja przeniesiona za wywołanie zależności albo do implementacji kalendarza: zmienia się moment błędu, a spy odnotowuje wywołania dla niepoprawnych danych.
- Nowa reguła dodana w tym samym commicie co seam, bez oddzielnych testów.
- Weryfikacja spy tylko przez `calls > 0` zamiast dokładnej liczby i argumentów.
- Mockowanie przez framework z `verify(...)` bez sprawdzenia braku innych interakcji (odpowiednik "liczby" wywołań).

### Pytania do dyskusji

- Kiedy wystarczy parametr metody lub `Clock`, a kiedy potrzebna jest zależność konstruktora?
- Co zmienia przeniesienie `new` do composition root, jeśli implementacja trzyma połączenie, cache albo blokadę?
- Czy `requireNonNull` w nowym konstruktorze to refaktoryzacja, czy nowy kontrakt?
- Jak przeprowadzić migrację, jeśli serwis tworzy kilkadziesiąt klas w różnych modułach?

---

## Warsztat 2. Dekompozycja długiej metody

> Rozwiązanie opracowane na podstawie kodu referencyjnego, nie pochodzi wprost z materiału teoretycznego.

### Kod wejściowy i referencyjny

- część A, wejście: `src/main/java/pl/training/module7/breakmethod/before/LegacyReleaseManifestBuilder.java`
- część A, referencja: `src/main/java/pl/training/module7/breakmethod/after/ReleaseManifestBuilder.java`, test `src/test/java/pl/training/module7/BreakMethodEquivalenceTest.java`
- część B, wejście: `src/main/java/pl/training/module7/methodobject/before/LegacyDeploymentRiskCalculator.java`
- część B, referencja: `src/main/java/pl/training/module7/methodobject/after/DeploymentRiskCalculator.java`, `src/main/java/pl/training/module7/methodobject/after/DeploymentRiskCalculation.java`, test `src/test/java/pl/training/module7/ExtractMethodObjectEquivalenceTest.java`

### Część A: przepływ danych i wybór techniki

| Fragment | Wejście | Wyjście | Mutowany stan lokalny |
| --- | --- | --- | --- |
| walidacja i kopia | `entries` | nowa lista `validatedEntries` | budowana kopia |
| sortowanie | `validatedEntries` | lista uporządkowana | sortowana kopia |
| renderowanie | lista uporządkowana | `String` | `StringJoiner` |

Każdy fragment ma jedno wejście i jeden wynik, a stan lokalny nie przecina granic kroków. Wniosek: wystarczy Extract Method, a trzy kolejne fazy z osobnymi danymi pośrednimi to w praktyce Split Phase. Method Object byłby zbędny (zgodnie z komentarzem w teorii do przykładu).

### Część A: sekwencja kroków

1. Testy różnicowe (odpowiedniki `BreakMethodEquivalenceTest`):
   - wynik dla listy nieposortowanej, np. wpisy `web/20`, `worker/10`, `api/10` dają `10|api|sha-api\n10|worker|sha-worker\n20|web|sha-web`,
   - pusta lista daje pusty tekst,
   - równe klucze sortowania zachowują kolejność wejściową (stabilność),
   - lista wywołującego po `build` jest równa migawce sprzed wywołania,
   - wyjątki z wieloma błędnymi polami naraz: `null` lista, lista z `null`, `ManifestEntry(null, null, -1)`, `(" ", null, -1)`, `("api", null, -1)`, `("api", " ", -1)`, `("api", "sha", -1)`.
2. Wydziel `render(List<ManifestEntry>)`: przenieś pętlę z `StringJoiner` dosłownie. Testy.
3. Wydziel `order(List<ManifestEntry>)`. Dopuszczalne jest zarówno sortowanie kopii przez `List.sort`, jak i `stream().sorted(...).toList()` z referencji (oba są stabilne). Testy.
4. Wydziel `validateAndCopy(List<ManifestEntry>)` razem z `requireNonNull(entries, "entries")`, zachowując jedną pętlę i kolejność sprawdzeń pól. Testy.
5. Metoda publiczna zawiera trzy wywołania na jednym poziomie abstrakcji.

### Część B: przepływ danych i wybór techniki

Wszystkie fragmenty czytają i modyfikują tę samą zmienną `score`, a klasyfikacja zależy od jej końcowej wartości. Zwykły Extract Method wymagałby przekazywania i zwracania `score` w każdym kroku. To sygnał do Method Object: `score` staje się polem obiektu tworzonego dla jednego obliczenia.

Uczestnik może też obronić wariant z Extract Method zwracającym nową wartość (`long addCriticalServicesRisk(long score)`), bo stan jest jedną liczbą. Ocenia się uzasadnienie, nie wybór sam w sobie.

### Część B: sekwencja kroków (według sekcji 2.4 teorii)

1. Testy różnicowe: wejścia `(0,0,0,false)`, `(5,0,0,true)` daje 0/LOW, `(29,0,0,false)`, `(30,...)`, `(69,...)`, `(70,...)`, `(50,0,0,true)` daje 35/MEDIUM, wagi `(0,1,0,false)` daje 20, `(0,0,1,false)` daje 10, `Integer.MAX_VALUE` we wszystkich licznikach daje 100/HIGH, `calculate(null)` z komunikatem `input`.
2. Utwórz klasę `DeploymentRiskCalculation` (pakietową, nie wewnętrzną) z konstruktorem przyjmującym `DeploymentRiskInput`.
3. Skopiuj ciało metody do `calculate()` bez upraszczania.
4. Stara metoda: `return new DeploymentRiskCalculation(input).calculate();`. Testy.
5. Zamień `score` na pole.
6. Wydzielaj kroki: `addChangedFilesRisk`, `addCriticalServicesRisk`, `addFailedChecksRisk`, `applyRollbackReduction`, `limitScore`, `classify`. Testy po każdym.
7. Opcjonalnie stałe dla wag 20, 10 i 15.

### Jak spełnić kryteria akceptacji

| Kryterium | Jak sprawdzić |
| --- | --- |
| nazwy opisują intencję | `order`, `render`, `applyRollbackReduction`, a nie `loop1`, `sortList`, `subtract15` |
| kolejność kroków jawna | metoda nadrzędna to sekwencja wywołań w kolejności obliczeń; w B `limitScore` po `applyRollbackReduction` |
| obiekt metody nie jest współdzielony | `new` wewnątrz metody fasady; test `low`, `high`, `low` daje 10, 80, 10 |
| brak nowej walidacji | brak nowych `require...`/`if throw`; `requireNonNull(input, "input")` tylko przeniesiony, z tym samym komunikatem |
| testy różnicowe i wartości skrajne | przypadek `Integer.MAX_VALUE` i granice 29/30, 69/70 zielone |
| lista wejściowa bez zmian | test z migawką `List.copyOf(entries)` |
| uzasadnienie techniki | pisemna notatka odwołująca się do tabeli przepływu danych |

### Typowe błędy uczestników

- Sortowanie listy wejściowej w miejscu (`entries.sort(...)`) po "uproszczeniu" kopii. Test migawki to wykrywa. Przy `List.of(...)` na wejściu kończy się `UnsupportedOperationException`.
- Rozbicie walidacji na kilka przebiegów lub strumień z `filter`, co zmienia pierwszy zgłaszany błąd przy wielu wadliwych wpisach.
- Dodanie "przy okazji" nowej walidacji, np. odrzucanie duplikatów artefaktów albo `requireNonNull` na `ManifestEntry` w innym miejscu.
- Zamiana `Comparator.comparingInt(...).thenComparing(...)` na własny komparator bez drugiego klucza.
- W części B: usunięcie rzutowania `(long)` przed mnożeniem (przepełnienie `int` dla `Integer.MAX_VALUE`), przestawienie `limitScore` przed redukcją za rollback (dla `(120,0,0,true)` wynik 85 zamiast 100), zmiana `<` na `<=` w progach.
- Obiekt metody przechowywany w polu fasady lub jako singleton: stan `score` kumuluje się między wywołaniami.
- Niestatyczna klasa wewnętrzna jako obiekt metody (ukryta referencja do klasy zewnętrznej).

### Pytania do dyskusji

- Dlaczego dla manifestu Method Object byłby nadmiarowy, a dla ryzyka nie?
- Czy `stream().sorted().toList()` zmienia kontrakt metody? (Wynik prywatny, niemodyfikowalny, stabilny.)
- Kiedy Split Phase wymaga nowego typu danych pośrednich?
- Jak obsłużyć `return`, `break` i `continue` wewnątrz fragmentu przeznaczonego do ekstrakcji?

---

## Warsztat 3. Stopniowe rozbijanie God Class

> Rozwiązanie opracowane na podstawie kodu referencyjnego, nie pochodzi wprost z materiału teoretycznego.

### Kod wejściowy i referencyjny

- wejście: `src/main/java/pl/training/module7/godclass/before/LegacyReleaseManager.java`
- referencja (stan docelowy kampanii): `src/main/java/pl/training/module7/godclass/after/ReleaseApplicationService.java`, `ReleaseValidator.java`, `ReleaseRepository.java`, `InMemoryReleaseRepository.java`, `AuditTrail.java`, `ReleaseNotifier.java` w `src/main/java/pl/training/module7/godclass/after/`
- testy referencyjne: `src/test/java/pl/training/module7/GodClassEquivalenceTest.java`

Referencja pokazuje stan po kilku krokach kampanii (wszystkie odpowiedzialności wydzielone, stara klasa zastąpiona serwisem). Warsztat wymaga jednego kroku, w którym klasa wyjściowa zostaje jako delegująca fasada. Poniżej wzorcowy pierwszy krok: wydzielenie powiadomień do `ReleaseNotifier`.

### Mapa metod do pól i integracji

| Metoda | `releases` | `auditEntries` | `notifications` | `events` | Uwagi |
| --- | --- | --- | --- | --- | --- |
| `publish` | czyta, zapisuje | zapisuje | zapisuje | zapisuje | walidacja 3 pól, duplikat, potem save, audit, notify |
| `releases()` | czyta | | | | `List.copyOf` |
| `auditEntries()` | | czyta | | | `List.copyOf` |
| `notifications()` | | | czyta | | `List.copyOf` |
| `events()` | | | | czyta | `List.copyOf` |
| `requireText` | | | | | statyczna walidacja |

Ślad efektów udanej publikacji: `save:rel-42`, `audit:rel-42`, `notify:rel-42`. W każdym kroku wpis do własnej kolekcji poprzedza wpis do `events`.

### Wybór klastra

Powiadomienia (`notifications` + fragment `publish` + akcesor) mają samodzielny powód zmiany (kanał i format powiadomień), leżą na końcu grafu wywołań i nie są czytane przez inne kroki. Równie dobrym wyborem jest audyt. Repozytorium jest trudniejsze na pierwszy krok, bo czyta je sprawdzenie duplikatu.

### Sekwencja kroków

1. **Testy śladu (commit 1)** na kopii klasy, z oryginałem jako wyrocznią:
   - sukces: wynik `PublishedRelease`, `releases()`, `auditEntries()`, `notifications()` i `events()` równe wersji wyjściowej,
   - walidacja: `(" ", "payments", "2.1.0")` daje `IllegalArgumentException("releaseId must not be blank")`, wszystkie kolekcje puste; analogicznie dla `service` i `version`, także z kilkoma błędnymi polami naraz (pierwszy błąd: `releaseId`),
   - duplikat: po udanej publikacji `rel-42` ponowna publikacja daje `IllegalStateException("release already published: rel-42")`, ślad i kolekcje bez zmian,
   - kopie defensywne: `clear()` na zwróconych listach zgłasza `UnsupportedOperationException`.
2. **Nowa klasa (commit 2).** `ReleaseNotifier` jak w referencji: własna lista `notifications`, `Consumer<String> eventSink`, metoda `notifyPublished(PublishedRelease)` najpierw dodaje wpis `release-published:<id>`, potem wywołuje `eventSink.accept("notify:<id>")`, akcesor zwraca `List.copyOf`.
3. **Delegacja w klasie wyjściowej (commit 3).** Pole `notifications` znika z menedżera, a stare API deleguje:

   ```java
   private final List<String> events = new ArrayList<>();
   private final ReleaseNotifier notifier;

   public ReleaseManager() {
       this(ReleaseNotifier::new);
   }

   ReleaseManager(Function<Consumer<String>, ReleaseNotifier> notifierFactory) {
       this.notifier = notifierFactory.apply(events::add);
   }

   public List<String> notifications() {
       return notifier.notifications();
   }
   ```

   W `publish` dwie linie powiadomienia zastępuje `notifier.notifyPublished(release);`. Walidacja, sprawdzenie duplikatu, zapis i audyt pozostają bez zmian i w tej samej kolejności.
4. **Test awarii (commit 4).** Pakietowy konstruktor pozwala podstawić powiadamiacz, którego ujście rzuca wyjątek po zarejestrowaniu zdarzenia:

   ```java
   var manager = new ReleaseManager(sink -> new ReleaseNotifier(event -> {
       sink.accept(event);
       throw new IllegalStateException("notification event failure");
   }));
   ```

   Oczekiwane (zgodnie z `notificationFailureKeepsAllEarlierEffects`): wyjątek propaguje się, `events()` zawiera `save`, `audit`, `notify`, wydanie jest zapisane, wpis audytu istnieje. Nic nie jest wycofywane.
5. **Ocena transakcji i własności stanu (notatka).** Wersja wyjściowa nie miała transakcji ani kompensacji, więc wydzielenie jej nie wprowadza i nie usuwa. Awaria kroku zatrzymuje późniejsze kroki i zostawia wcześniejsze efekty. Jeśli biznes wymaga atomowości, to osobna decyzja (transakcja, transactional outbox, idempotentne ponowienia, kompensacja), a nie część ruchu klas.
6. **Kolejne kroki kampanii (propozycja).** Wydzielenie `AuditTrail` tym samym wzorcem; wydzielenie `ReleaseValidator`; wydzielenie repozytorium z interfejsem `ReleaseRepository`; na końcu `ReleaseApplicationService` jako koordynator i migracja klientów z fasady. Każdy krok jest samodzielnie wdrażalny, bo publiczne API fasady się nie zmienia.

### Jak spełnić kryteria akceptacji

| Kryterium | Jak sprawdzić |
| --- | --- |
| stan ma jednego właściciela | lista powiadomień istnieje tylko w `ReleaseNotifier`; menedżer nie trzyma kopii, `notifications()` deleguje |
| brak cyklu zależności | `ReleaseNotifier` nie zna menedżera; komunikacja zwrotna tylko przez `Consumer<String>` |
| walidacja zachowuje kolejność i komunikaty | testy z kroku 1 z wieloma błędnymi polami zielone bez zmian asercji |
| po błędzie brak późniejszych kroków, wcześniejsze efekty zgodne z kontraktem | test awarii z kroku 4 oraz test duplikatu (brak nowych zdarzeń) |
| kroki kampanii niezależnie wdrażalne | publiczne sygnatury fasady bez zmian; każdy krok ma własne testy i osobny commit |
| te same wartości publicznych metod | testy różnicowe akcesorów i `clear()` rzucające `UnsupportedOperationException` |

### Typowe błędy uczestników

- Przepisanie całej klasy od razu na docelowy serwis z czterema współpracownikami ("big bang") bez fasady.
- Dwie kopie stanu: menedżer nadal trzyma `notifications`, a `ReleaseNotifier` ma własną listę. Rozjazd przy awarii lub przyszłej zmianie.
- Przestawienie kolejności `save`, `audit`, `notify` albo zapis do `events` przed wpisem do własnej kolekcji kroku (inny stan po awarii).
- Wstrzyknięcie do `ReleaseNotifier` referencji do menedżera, aby dopisywał zdarzenia (cykl zależności).
- Zmiana akcesora na zwracanie mutowalnej listy (utrata kopii defensywnej) albo `Collections.unmodifiableList` (widok zamiast migawki, inna semantyka aliasowania).
- Dodanie `try/catch` z wycofaniem wcześniejszych efektów bez decyzji biznesowej (zmiana zachowania ukryta w refaktoryzacji).
- Wydzielenie klas dla każdej linii kodu, co daje sieć małych klas o niskiej spójności.

### Pytania do dyskusji

- Dlaczego `InMemoryReleaseRepository.save` w referencji ponownie sprawdza duplikat, skoro serwis robi to wcześniej? Czy to zmienia kontrakt?
- Czy lista `events` współdzielona przez `Consumer` narusza zasadę jednego właściciela stanu?
- Jak zmieni się analiza, jeśli `save` jest w transakcji bazy danych, a `notify` wysyła wiadomość do brokera?
- Kiedy przestać rozbijać klasę? (Teoria: tylko tak długo, jak przynosi to wartość dla planowanych zmian.)

---

## Ćwiczenia uzupełniające A-I

> Rozwiązania opracowane na podstawie kodu referencyjnego, nie pochodzą wprost z materiału teoretycznego.

### A. Break Responsibilities

- Referencja: `src/main/java/pl/training/module7/breakresponsibilities/after/DeploymentMetricsCalculator.java`, `DeploymentReportFormatter.java`, `DeploymentReportService.java`; rekord `src/main/java/pl/training/module7/breakresponsibilities/DeploymentMetrics.java`; test `BreakResponsibilitiesEquivalenceTest`.
- Kroki: testy różnicowe (raport, pusta lista, średnia całkowita, `null` lista i element, przepełnienie `Math.addExact`, brak mutacji wejścia), wydzielenie formattera, wydzielenie kalkulatora zwracającego `DeploymentMetrics`, serwis składający oba kroki, stara klasa jako fasada do czasu migracji.
- Błędy: zamiana `Math.addExact` na `+` (utrata `ArithmeticException`), średnia jako `double`, formatter liczący coś samodzielnie.

### B. Remove Duplication

- Referencja: `src/main/java/pl/training/module7/duplication/after/ArtifactPublisher.java`; test `DuplicationEquivalenceTest`.
- Kroki: osobne testy obu metod (także w `tr-TR`), ujednolicenie struktury, wydzielenie `validateAndNormalize`, przepięcie `publishSnapshot`, testy, przepięcie `publishRelease`, testy. Różnica jako jawny argument `qualifier`.
- Błędy: usunięcie `Locale.ROOT`, połączenie w jedną metodę publiczną z parametrem `boolean snapshot` (zamiana duplikacji na flagę), zmiana kolejności walidacji.

### C. Introduce Parameter Object

- Referencja: `src/main/java/pl/training/module7/parameterobject/after/RolloutSpec.java`, `RolloutPlanner.java`; test `ParameterObjectEquivalenceTest`.
- Kroki (sekcja 6.4 teorii): rekord przechowujący dokładnie dotychczasowe wartości bez walidacji, nowe przeciążenia przyjmujące rekord, stare sygnatury delegujące (ewentualnie `@Deprecated`), migracja wywołań, dopiero w osobnym kroku przeniesienie walidacji do konstruktora rekordu (zmiana momentu błędu, test `movesValidationFromEveryOperationToParameterObjectConstruction`), usunięcie starych sygnatur po okresie zgodności.
- Oczekiwane wyniki: `estimateSeconds` dla `(10, 3, 15)` daje 165, dla `Integer.MAX_VALUE` i `batchSize` 2 daje `32_212_254_720`.
- Błędy: przeniesienie walidacji w tym samym kroku co grupowanie, usunięcie starej sygnatury bez okresu zgodności (klienci w plikach `.class`), nazwa typu `RolloutParameters` jako worek ustawień.

### D. Remove Arrowhead Antipattern

- Referencja: `src/main/java/pl/training/module7/arrowhead/after/DeploymentEligibility.java`; test `ArrowheadEquivalenceTest`.
- Kroki: macierz 8 kombinacji flag plus przypadki `null` kandydata i niepoprawnego `releaseId`, odwracanie po jednym poziomie od zewnątrz, usuwanie `else`, testy po każdym poziomie.
- Błędy: zmiana priorytetu warunków, błędne zastosowanie de Morgana (`&&` zamiast `||` w odwróconym warunku `releaseId`).

### E. Introduce Design by Contract Checks

- Referencja: `src/main/java/pl/training/module7/contract/after/Contracts.java`, `DeploymentCapacity.java`; test `DesignByContractTest`.
- Kontrakt: precondition `totalSlots >= 0`, `slots > 0`, `slots <= remaining` (reserve), `slots <= totalSlots - remaining` (release); postcondition zmiany `remaining` o `slots`; invariant `0 <= remaining <= totalSlots`.
- Kroki: test równoważności poprawnych sekwencji, nowe pole `totalSlots`, preconditions przed mutacją, wyliczenie i sprawdzenie następnego stanu przed przypisaniem, postcondition po przypisaniu. Osobny test nowego kontraktu (komunikaty, brak mutacji po błędzie).
- Błędy: użycie `assert`, sprawdzanie niezmiennika dopiero po przypisaniu (obiekt zostaje w złym stanie), przedstawianie zmiany jako czystej refaktoryzacji.

### F. Remove Double Negative

- Referencja: `src/main/java/pl/training/module7/doublenegative/after/ReleaseReadiness.java`, `ReleaseGate.java`; test `DoubleNegativeEquivalenceTest`.
- Kroki (sekcja 9.4 teorii): predykaty pozytywne delegujące do negatywnych, migracja użyć, odwrócenie implementacji, negatywne akcesory jako delegujące do czasu migracji klientów, usunięcie.
- Błędy: pozytywna nazwa niebędąca dokładnym dopełnieniem, pominięcie wpływu na JSON, kolumny i klucze konfiguracji.

### G. Remove Boolean Method Parameters

- Referencja: `src/main/java/pl/training/module7/booleanparameter/after/DeploymentExecutor.java`; test `BooleanParameterEquivalenceTest`.
- Kroki (sekcja 11.4 teorii): `preview` i `deploy` delegujące do `execute(id, true/false)`, migracja klientów, stara metoda czasowo delegująca (dla biblioteki), prywatna implementacja z nazwanym trybem (enum), usunięcie starej sygnatury.
- Błędy: skopiowanie algorytmu do dwóch metod, walidacja tylko w jednej z nich, usunięcie starej metody bez okresu zgodności.

### H. Remove Middle Man

- Referencja: `src/main/java/pl/training/module7/middleman/after/ReleaseDashboard.java`, `DeploymentRegistry.java`; test `MiddleManEquivalenceTest`.
- Ocena: `ReleaseService` nie realizuje autoryzacji, transakcji, telemetrii, retry ani translacji błędów, więc może zostać usunięty. Kroki: dashboard przyjmuje `DeploymentRegistry`, migracja po jednym kliencie, `ReleaseService` jako forwarder w okresie zgodności, usunięcie po potwierdzeniu braku wywołań.
- Błędy: usunięcie pośrednika, który jest fasadą modułu lub adapterem; brak odnotowania nowego komunikatu `requireNonNull` w konstruktorze dashboardu.

### I. Return ASAP

- Referencja: `src/main/java/pl/training/module7/returnasap/after/ArtifactFinder.java`; test `ReturnAsapEquivalenceTest`.
- Kroki: testy (pusta lista, brak dopasowania, pierwsze dopasowanie przez `assertSame`, `null` przed i po dopasowaniu, ślad `size`/`get`), `return Optional.of(artifact)` w miejscu dopasowania, usunięcie zmiennej `result`, zamiana `while` na `for` z indeksem, `return Optional.empty()` na końcu.
- Błędy: zamiana na strumień lub iterator w tym samym kroku, zwracanie ostatniego zamiast pierwszego dopasowania, `Optional.ofNullable` pozostawione bez potrzeby (drobne), pominięcie testu elementu `null` po dopasowaniu.

---

## Lista kontrolna przeglądu (narzędzie oceny)

Lista pochodzi z materiału teoretycznego modułu. Podczas przeglądu zaznacz dla każdego rozwiązania: T (tak), N (nie), n/d (nie dotyczy). Każde "N" wymaga komentarza i wskazania brakującego testu lub kroku.

### Zachowanie

| Pytanie | W1 | W2 | W3 |
| --- | --- | --- | --- |
| Czy testy obejmują wszystkie gałęzie i priorytet nakładających się warunków? | | | |
| Czy zachowano typ, komunikat i moment wyjątków należących do kontraktu? | | | |
| Czy stan po nieudanej operacji jest identyczny? | | | |
| Czy liczba, kolejność i argumenty efektów ubocznych są chronione? | | | |
| Czy wcześniejszy `return` nie omija zwalniania zasobów ani wymaganej mutacji? | | | |

### Projekt

| Pytanie | W1 | W2 | W3 |
| --- | --- | --- | --- |
| Czy nowy seam opisuje potrzebę klienta, a nie całe API dostawcy? | | | |
| Czy Parameter Object reprezentuje pojęcie, a nie worek ustawień? | | | |
| Czy usunięto duplikację wiedzy, a nie przypadkowe podobieństwo? | | | |
| Czy po ekstrakcji istnieje jeden właściciel każdego stanu? | | | |
| Czy pośrednik rzeczywiście nie chronił wartościowej granicy? | | | |

### Migracja

| Pytanie | W1 | W2 | W3 |
| --- | --- | --- | --- |
| Czy nowe i stare API mogą czasowo współistnieć? | | | |
| Czy istnieją klienci dostarczani jako już skompilowane pliki `.class`? | | | |
| Czy zmiana rekordu, serializacji albo trwałych nazw wymaga migracji danych? | | | |
| Czy zaostrzenie kontraktu jest oddzielone od ruchu strukturalnego? | | | |
| Czy usunięcie elementów przejściowych ma jawne kryterium zakończenia? | | | |

### Sugerowana skala oceny (propozycja prowadzącego)

- **Zaliczone wzorcowo**: wszystkie kryteria akceptacji warsztatu spełnione, brak "N" w części Zachowanie, zmiany kontraktu w osobnych commitach z opisem.
- **Zaliczone**: kryteria akceptacji spełnione, pojedyncze "N" w częściach Projekt lub Migracja z trafnym komentarzem uczestnika.
- **Do poprawy**: jakiekolwiek "N" w części Zachowanie albo zmiana kontraktu ukryta w kroku opisanym jako refaktoryzacja.

## Podsumowanie do zamknięcia modułu (5 minut)

Najważniejsze rozróżnienia z teorii, do przypomnienia po przeglądzie:

- seam umożliwia zmianę zależności, ale nie wymaga interfejsu dla każdej klasy,
- Extract Method jest pierwszym wyborem, a Method Object rozwiązaniem dla trudnego przepływu lokalnego stanu,
- duplikacja i duża klasa są diagnozami wymagającymi analizy wiedzy oraz powodów zmiany,
- guard clauses i wcześniejsze zwroty są bezpieczne tylko przy zachowaniu kolejności oraz efektów,
- publiczne kontrakty wymagają jawnych kontroli niezależnych od `assert`,
- flaga, pośrednik i negatywna nazwa powinny być usuwane na podstawie semantyki, nie mechanicznej reguły,
- zgodność strukturalna, źródłowa, binarna i behawioralna są różnymi własnościami.
