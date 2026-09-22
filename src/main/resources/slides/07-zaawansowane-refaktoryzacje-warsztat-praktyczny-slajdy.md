---
marp: true
theme: default
paginate: true
size: 16:9
style: |
  section { font-size: 24px; }
  section.lead h1 { font-size: 48px; }
  pre, code { font-size: 18px; }
---

<!-- _class: lead -->
# Moduł 7. Zaawansowane refaktoryzacje - warsztat praktyczny
Trzynaście refaktoryzacji wykonywanych małymi krokami, z pełną kontrolą obserwowalnego zachowania

---

## Agenda

- **Model bezpieczeństwa** - czym refaktoryzacja różni się od zmiany kontraktu, pętla pracy, pułapki Javy 25.
- **Zależności i struktura:** Break Dependencies, Extract Method Object, Break Responsibilities, Remove Duplication, Break Method.
- **Sygnatury i warunki:** Introduce Parameter Object, Remove Arrowhead Antipattern, Introduce Design by Contract Checks, Remove Double Negative.
- **Klasy i API:** Remove God Classes, Remove Boolean Method Parameters, Remove Middle Man, Return ASAP.
- **Warsztaty praktyczne** - seam przed zmianą integracji, dekompozycja długiej metody, stopniowe rozbijanie God Class.
- **Lista kontrolna przeglądu** i podsumowanie najważniejszych rozróżnień.

---

## Refaktoryzacja a zmiana kontraktu

- Refaktoryzacja zachowuje obserwowalne zachowanie w przyjętej granicy - nie musi zachowywać struktury klas ani prywatnych metod.
- Kryterium jest proste: klient nie powinien dostrzec żadnej nieuzgodnionej różnicy.
- Odrzucenie niepoprawnego argumentu, zmiana typu wyjątku, kopia defensywna czy `ArithmeticException` zamiast przepełnienia to już zmiany zachowania lub kontraktu.
- Usunięcie publicznej metody również jest zmianą kontraktu, a nie „porządkami”.
- Takie kroki bywają potrzebne, ale należy je wykonywać i zatwierdzać **osobno** od ruchów strukturalnych.

---

## Wektor obserwowalnego zachowania

Dla każdej transformacji ustal, co jest częścią kontraktu:

| Obszar | Co sprawdzić |
| --- | --- |
| Wynik | wartość zwracana i tożsamość obiektu |
| Wyjątki | typ, komunikat, moment zgłoszenia |
| Stan | stan odbiorcy po sukcesie i po błędzie |
| Współpracownicy | kolejność i liczba wywołań, short-circuit warunków |
| Czas i aliasy | moment pobrania zależności/konfiguracji/zegara, aliasowanie mutowalnych obiektów |
| Granice | transakcje, blokady, widoczność między wątkami, API, dane trwałe, serializacja |

Granica kontraktu ma być świadomą decyzją, a nie przypadkowym skutkiem zbyt słabego testu.

---

## Pętla pracy

1. Nazwij konkretną przeszkodę dla planowanej zmiany i zapisz przykłady charakteryzujące ścieżkę poprawną, granice oraz awarie.
2. Wybierz najmniejszy ruch strukturalny, który usuwa przeszkodę - zachowując kolejność obliczeń, efektów i wyjątków.
3. Po każdym ruchu skompiluj kod i uruchom zawężony zestaw testów.
4. Migruj jedno wywołanie albo jedną odpowiedzialność naraz; element przejściowy usuwaj dopiero po migracji wszystkich klientów.
5. Zmiany kontraktu i optymalizacje zatwierdzaj w osobnych krokach, żeby dało się je niezależnie ocenić i wycofać.

---

## Java 25 w tym module (1/2)

- **Interfejs funkcyjny** to wygodny seam dla pojedynczej operacji - w teście można podstawić lambdę jako fake albo spy.
- **Interfejs `sealed`** nie jest interfejsem funkcyjnym i zamyka zbiór implementacji, więc nie pasuje do otwartego punktu podstawiania zależności.
- **Rekord** dobrze reprezentuje Parameter Object złożony z wartości, ale jest tylko płytko niemutowalny - `final` chroni referencję, nie stan wskazanego obiektu.
- Kopia defensywna kolekcji w rekordzie może być właściwa, lecz w istniejącym API zmienia semantykę aliasowania i wymaga osobnej decyzji.

---

## Java 25 w tym module (2/2)

- **`assert`** jest domyślnie wyłączony - nie może wymuszać warunków publicznego API; nadaje się tylko do wewnętrznych założeń bez skutków ubocznych.
- **Kolejność obliczeń:** wyrażenia liczone są od lewej do prawej, a `&&`/`||` stosują short-circuit evaluation - przestawienie warunków może zmienić liczbę wywołań i pierwszy wyjątek.
- **Zgodność binarna:** po migracji kod źródłowy się kompiluje, ale stary plik `.class` może oczekiwać starego deskryptora i zakończyć się błędem linkowania.
- Dla publicznej biblioteki potrzebny jest więc często okres przejściowy z delegującym przeciążeniem oznaczonym jako przestarzałe.

---

## Organizacja warsztatu (360 minut)

| Część | Czas |
| --- | ---: |
| model zachowania i Break Dependencies | 40 min |
| Method Object, odpowiedzialności i duplikacja | 60 min |
| Break Method i Parameter Object | 45 min |
| Arrowhead, kontrakty i podwójne zaprzeczenia | 60 min |
| God Class, flagi i Middle Man | 55 min |
| Return ASAP i migracja API | 25 min |
| ćwiczenia warsztatowe | 60 min |
| przegląd rozwiązań i podsumowanie | 15 min |

---

## 1. Break Dependencies - intencja

- Break Dependencies tworzy **seam** - miejsce, w którym kod może współpracować z inną implementacją bez edycji własnego algorytmu.
- **Separation:** pozwala uruchomić badany kod bez kosztownego lub niedostępnego otoczenia.
- **Sensing:** pozwala obserwować komunikację, której wynik nie jest bezpośrednio zwracany.
- Nie chodzi o interfejs przed każdą klasą - najwęższy skuteczny seam może być parametrem metody, zależnością konstruktora, `Clock`, funkcją albo chronioną metodą przejściową.

---

## Break Dependencies - przed i po

```java
// przed: serwis sam tworzy implementację kalendarza
private final StandardMaintenanceWindows maintenanceWindows =
        new StandardMaintenanceWindows();

// po: najwęższy kontrakt potrzebny klientowi
@FunctionalInterface
public interface MaintenanceWindows {
    boolean allows(String service, int hourUtc);
}

public DeploymentWindowService(MaintenanceWindows maintenanceWindows) {
    this.maintenanceWindows = Objects.requireNonNull(
            maintenanceWindows, "maintenanceWindows");
}
```

- Przed zmianą test nie mógł podstawić kontrolowanego wyniku; po zmianie produkcja nadal składa serwis z dotychczasową implementacją - zmienia się miejsce tworzenia zależności, nie reguła.

---

## Break Dependencies - sekwencja i ryzyka

- **Sekwencja:** scharakteryzuj wynik i wywołania zależności, wprowadź najwęższy kontrakt, przekaż przez niego dotychczasową implementację.
- Następnie potwierdź równoważność produkcyjnego składania, dodaj test z fake albo spy i dopiero potem rozwijaj logikę biznesową.
- **Ryzyko:** przeniesienie tworzenia zależności może zmienić jej czas życia, współdzielenie, inicjalizację statyczną, blokadę lub granicę transakcji.
- **Ryzyko:** interfejs nie powinien ujawniać całego API implementacji - ma opisywać dokładnie potrzebę klienta.

---

## 2. Extract Method Object - kiedy zwykła ekstrakcja nie wystarcza

- Extract Method Object (Replace Function with Command) przenosi pojedyncze wykonanie złożonego algorytmu do nowego obiektu.
- Parametry i lokalne wartości robocze stają się polami, więc kolejne kroki można wydzielać bez długich list argumentów i wyników pośrednich.
- Obiekt metody **nie jest usługą współdzieloną** - powinien powstawać dla każdego wywołania.
- Współdzielenie mutowalnego stanu obliczenia prowadziłoby do błędów przy ponownym wejściu i wykonaniu współbieżnym.

---

## Extract Method Object - przed

```java
public RiskAssessment calculate(DeploymentRiskInput input) {
    Objects.requireNonNull(input, "input");
    long score = input.changedFiles();
    score += (long) input.criticalServices() * 20;
    score += (long) input.failedChecks() * 10;
    if (input.rollbackTested()) {
        score -= 15;
    }
    score = Math.max(0, Math.min(100, score));
    RiskLevel level;
    if (score < 30) level = RiskLevel.LOW;
    else if (score < 70) level = RiskLevel.MEDIUM;
    else level = RiskLevel.HIGH;
    return new RiskAssessment((int) score, level);
}
```

Jedna metoda miesza sumowanie punktów, redukcję, ograniczenie zakresu i klasyfikację na wspólnej zmiennej `score`.

---

## Extract Method Object - po

```java
public RiskAssessment calculate(DeploymentRiskInput input) {   // fasada
    return new DeploymentRiskCalculation(input).calculate();
}

final class DeploymentRiskCalculation {
    private final DeploymentRiskInput input;
    private long score;                        // wartość robocza jako pole
    RiskAssessment calculate() {
        addChangedFilesRisk();
        addCriticalServicesRisk();
        addFailedChecksRisk();
        applyRollbackReduction();
        limitScore();
        return new RiskAssessment((int) score, classify());
    }
}
```

Fasada zachowuje dotychczasowy punkt wejścia, a nowa klasa reprezentuje jedno obliczenie z prywatnymi krokami operującymi na polach.

---

## Extract Method Object - sekwencja i ryzyka

- **Sekwencja:** utwórz klasę nazwaną zgodnie z zadaniem, przenieś parametry do konstruktora i skopiuj ciało metody **bez równoczesnego upraszczania**.
- Zastąp starą metodę delegacją, porównaj zachowanie obu wariantów, a dopiero potem zamieniaj zmienne lokalne na pola i wydzielaj kroki.
- **Ryzyko:** trzeba zachować moment odczytu danych, kolejność wyjątków, efekty uboczne i zakres synchronizacji.
- **Ryzyko:** niestatyczna klasa wewnętrzna przechwytuje instancję klasy zewnętrznej i ukrywa zależności - osobna klasa lub statyczna klasa zagnieżdżona daje czytelniejszą granicę.

---

## 3. Break Responsibilities - odpowiedzialność jako powód zmiany

- Odpowiedzialności nie mierzy się liczbą metod - dwa fragmenty są różnymi odpowiedzialnościami, gdy zmieniają się z innych powodów, wymagają innej wiedzy lub mają innych odbiorców.
- W przykładzie `LegacyDeploymentReport.generate` jednocześnie liczy metryki wdrożeń (liczba, porażki, średni lead time) i formatuje raport tekstowy.
- Obliczanie metryk i format raportu podlegają innym regułom, więc powinny mieć osobnych właścicieli.

---

## Break Responsibilities - po zmianie

```java
public final class DeploymentReportService {
    private final DeploymentMetricsCalculator calculator;
    private final DeploymentReportFormatter formatter;

    public String generate(List<DeploymentSample> samples) {
        return formatter.format(calculator.calculate(samples));
    }
}
// DeploymentMetricsCalculator.calculate(...) zwraca
// new DeploymentMetrics(deployments, failures, averageLeadTimeMinutes)
```

- Serwis aplikacyjny składa dwa kroki, ale nie przejmuje ich szczegółów.
- Dane mają jednego właściciela, a kierunek zależności pozostaje czytelny.

---

## Break Responsibilities - sekwencja i ryzyka

- **Sekwencja:** zidentyfikuj klaster pól i metod o wspólnym powodzie zmiany, utwórz klasę docelową i przenieś najmniejszy spójny fragment.
- Zachowaj starą klasę jako delegującą fasadę i przenoś stan do jednego właściciela - nie utrzymuj dwóch kopii.
- Migruj klientów pojedynczo, a delegację usuń dopiero po zakończeniu migracji.
- **Ryzyko:** ekstrakcja może rozdzielić operację wcześniej objętą jedną transakcją albo blokadą.
- **Ryzyko:** przy ORM, serializacji i refleksji przeniesienie pola może zmienić kontrakt mimo identycznych wyników metod.

---

## 4. Remove Duplication - duplikacja wiedzy

- Podobny tekst nie zawsze jest duplikacją - łączymy fragmenty, które reprezentują **tę samą regułę** i powinny zmieniać się razem.
- Dwie podobne reguły z niezależnych kontekstów mogą celowo ewoluować w różnych kierunkach i nie powinny być sklejane.
- W przykładzie `publishSnapshot` i `publishRelease` powtarzają walidację, normalizację i składanie współrzędnych artefaktu.
- Różnica (sufiks `-SNAPSHOT`) pozostaje jawna, a abstrakcja wymagająca wielu flag i wyjątków sygnalizuje połączenie przypadkowo podobnych procesów.

---

## Remove Duplication - po zmianie

```java
public String publishSnapshot(String artifactName, int buildNumber) {
    return publish(artifactName, buildNumber, "-SNAPSHOT");
}

public String publishRelease(String artifactName, int buildNumber) {
    return publish(artifactName, buildNumber, "");
}

private String publish(String artifactName, int buildNumber,
                       String qualifier) {
    String normalizedName = validateAndNormalize(artifactName, buildNumber);
    return normalizedName + ":" + buildNumber + qualifier;
}
```

Wspólny fragment ma jednego właściciela, a publiczne metody nadal komunikują dwa zamiary biznesowe.

---

## Remove Duplication - bezpieczna sekwencja

1. Niezależnie scharakteryzuj obie implementacje - zanim je połączysz, musisz wiedzieć, czy naprawdę zachowują się tak samo.
2. Porównaj przypadki brzegowe, zaokrąglenia, wyjątki i efekty uboczne, bo tam najczęściej kryją się różnice.
3. Nadaj fragmentom spójne nazwy i strukturę, jeszcze bez współdzielenia kodu.
4. Wydziel identyczną regułę w jednym miejscu.
5. Skieruj do niej jedną ścieżkę i uruchom testy; potem drugą i ponownie porównaj zachowanie.

---

## 5. Break Method - intencja

- Break Method to seria małych ekstrakcji, po których metoda nadrzędna opisuje algorytm **na jednym poziomie abstrakcji**.
- Długość wierszy nie jest samodzielnym kryterium - liczy się możliwość nazwania spójnych kroków i ograniczenie kontekstów utrzymywanych jednocześnie w głowie.

```java
public String build(List<ManifestEntry> entries) {
    List<ManifestEntry> validatedEntries = validateAndCopy(entries);
    List<ManifestEntry> orderedEntries = order(validatedEntries);
    return render(orderedEntries);
}
```

- Metoda publiczna opisuje proces: walidacja i kopia, uporządkowanie, renderowanie; dane łatwo przechodzą między krokami, więc Method Object byłby zbędny.

---

## Break Method - bezpieczna sekwencja

- Zacznij od fragmentu z małą liczbą wejść i najwyżej jednym wynikiem, przenieś go dosłownie z zachowaniem kolejności instrukcji.
- Skompiluj i uruchom testy, dopiero potem nadaj metodzie nazwę opisującą intencję; powtarzaj dla kolejnych fragmentów.
- Użyj **Split Phase**, gdy metoda zawiera następujące po sobie fazy o osobnych modelach danych.
- Sięgnij po **Method Object** dopiero wtedy, gdy przepływ lokalnego stanu blokuje dalsze ekstrakcje.
- Szczególnej uwagi wymagają `return`, `break`, `continue`, mutowalne kolekcje, aliasy i kod wykonywany wewnątrz blokady.

---

## 6. Introduce Parameter Object - data clump jako pojęcie

- Parameter Object nie służy do ukrywania dowolnie długiej listy argumentów - grupa ma opisywać **jedno pojęcie** i regularnie występować razem.
- Nowy typ nazywa pojęcie, ogranicza pomyłki kolejności i pozwala później przenieść do niego zachowanie operujące na całej grupie.

```java
// przed: estimateSeconds(service, region, instances, batchSize, pauseSeconds)
//        describe(service, region, instances, batchSize, pauseSeconds)
public record RolloutSpec(String service, String region,
        int instances, int batchSize, int pauseSeconds) { ... }

public long estimateSeconds(RolloutSpec spec) { ... }
public String describe(RolloutSpec spec) { ... }
```

---

## Parameter Object - uwaga na moment walidacji

- Stan `after` przenosi walidację do kompaktowego konstruktora rekordu `RolloutSpec`.
- Niepoprawny `RolloutSpec` jest więc odrzucany **wcześniej** niż wywołanie planera - to obserwowalna zmiana momentu błędu.
- Z tego powodu nie należy łączyć jej w jednym kroku z samym grupowaniem parametrów.
- W stanie przejściowym zachowującym zachowanie rekord przechowuje wartości bez walidacji, a dotychczasowe kontrole wykonuje nadal planer.

---

## Parameter Object - migracja API i ryzyka

- **Migracja przez przeciążenie:** utwórz typ z dokładnie dotychczasowymi wartościami, dodaj metodę przyjmującą nowy typ, a starą sygnaturę pozostaw jako delegującą.
- Migruj wywołania pojedynczo, walidację i zachowanie przenoś do obiektu później, starą metodę usuń po okresie zgodności.
- `List.copyOf` daje niemodyfikowalny wynik i odrzuca `null`, ale nie gwarantuje nowej instancji - może zmienić semantykę mutowalności i aliasowania.
- Obiekt `Parameters` z luźnym zestawem pól i kolejnymi flagami zwykle tylko maskuje problem.
- Komponenty publicznego rekordu są częścią API - zmiana ich liczby, kolejności lub typów wymaga ostrożności jak zmiana sygnatury.

---

## 7. Remove Arrowhead Antipattern - problem

- Kod strzałkowy powstaje, gdy główna ścieżka zostaje przesunięta przez kolejne poziomy zagnieżdżenia `if`/`else`.
- **Guard clauses** oddzielają przypadki kończące przetwarzanie i pozostawiają ścieżkę pozytywną na poziomie metody.
- Spłaszczenie jest zachowawcze tylko wtedy, gdy **zachowuje priorytet warunków**.
- Jeśli kandydat jednocześnie nie ma akceptacji i ma nieudane testy, wynik musi nadal odpowiadać pierwszemu sprawdzanemu warunkowi (`NOT_APPROVED`).

---

## Remove Arrowhead - po zmianie

```java
public Eligibility evaluate(DeploymentCandidate candidate) {
    if (candidate == null) {
        return Eligibility.MISSING_CANDIDATE;
    }
    if (candidate.releaseId() == null || candidate.releaseId().isBlank()) {
        return Eligibility.INVALID_RELEASE_ID;
    }
    if (!candidate.approved())    return Eligibility.NOT_APPROVED;
    if (!candidate.testsPassed()) return Eligibility.TESTS_FAILED;
    if (!candidate.windowOpen())  return Eligibility.WINDOW_CLOSED;
    return Eligibility.ELIGIBLE;
}
```

Wersja legacy miała pięć poziomów zagnieżdżenia i zmienną `result`; kolejność warunków pozostała identyczna.

---

## Remove Arrowhead - bezpieczna sekwencja

1. Zbuduj macierz gałęzi, łącznie z kombinacjami kilku jednocześnie niespełnionych warunków.
2. Odwróć wyłącznie najbardziej zewnętrzny warunek i zwróć albo zgłoś dokładnie ten sam rezultat.
3. Usuń odpowiadający mu `else`, uruchom testy i powtarzaj po jednym poziomie.
4. Łącz warunki dopiero po potwierdzeniu braku efektów ubocznych.
5. Nie wolno pominąć logowania, aktualizacji stanu ani zwalniania zasobów - zasoby zabezpiecz wcześniej przez try-with-resources lub `finally`.

---

## 8. Introduce Design by Contract Checks - trzy części kontraktu

- **Precondition** - obowiązek klienta przed wywołaniem operacji.
- **Postcondition** - gwarancja operacji po poprawnym zakończeniu.
- **Invariant** - właściwość poprawnego obiektu zachowana pomiędzy operacjami publicznymi.
- Podtyp nie powinien wzmacniać warunków wstępnych ani osłabiać warunków końcowych względem nadtypu.
- W przeciwnym razie klient legalnie korzystający z kontraktu bazowego nie może bezpiecznie użyć podtypu.

---

## Design by Contract - jawne kontrole

```java
public void reserve(int slots) {
    Contracts.require(slots > 0, "slots must be positive");
    Contracts.require(slots <= remaining,
            "cannot reserve more slots than remain");
    int previousRemaining = remaining;
    int nextRemaining = previousRemaining - slots;
    checkInvariant(nextRemaining);          // sprawdzenie przed przypisaniem
    remaining = nextRemaining;
    Contracts.ensure(remaining == previousRemaining - slots,
            "reserve must reduce remaining capacity by slots");
    checkInvariant();
}
```

- `require` → `IllegalArgumentException`, `ensure` i `invariant` → `IllegalStateException`.
- Preconditions sprawdzane są przed pierwszą mutacją, więc ich naruszenie nie zmienia obiektu; naruszenie postcondition/invariant oznacza błąd implementacji.

---

## Design by Contract - to nie zawsze refaktoryzacja

- Wariant legacy może akceptować wartości spoza domeny, przechodzić w niespójny stan albo zgłaszać przypadkowy wyjątek później - dodanie odrzucenia **zmienia zachowanie**.
- Testy przykładu rozdzielają równoważność dla poprawnych sekwencji operacji od nowego kontraktu dla niepoprawnych danych.
- `assert` działa dopiero z flagą `-ea` i zgłasza `AssertionError` - nie służy do sprawdzania argumentów metod publicznych.
- Typowo: `NullPointerException` dla wymaganego `null`, `IllegalArgumentException` dla złej wartości, `IllegalStateException` dla operacji niedozwolonej w bieżącym stanie.
- W istniejącym systemie historyczny typ wyjątku może być ważniejszy - nie zastępuj go bez decyzji o zmianie kontraktu.

---

## 9. Remove Double Negative

- Negowanie negatywnego predykatu zwiększa obciążenie poznawcze i sprzyja pomyłkom przy rozbudowie warunku.
- Pozytywna nazwa musi oznaczać **dokładne logiczne dopełnienie** starej właściwości.

```java
// przed: record LegacyReleaseReadiness(boolean notApproved,
//                 boolean testsNotPassed, boolean windowNotOpen)
return !readiness.notApproved()
        && !readiness.testsNotPassed()
        && !readiness.windowNotOpen();

// po: record ReleaseReadiness(boolean approved,
//                 boolean testsPassed, boolean windowOpen)
return readiness.approved()
        && readiness.testsPassed()
        && readiness.windowOpen();
```

---

## Remove Double Negative - sekwencja i pułapki

- **Sekwencja:** dodaj predykat pozytywny delegujący do negatywnego, migruj po jednym użyciu i kompiluj po każdym kroku.
- Następnie przenieś implementację do predykatu pozytywnego; negatywny pozostaw jako delegujący, dopóki należy do publicznego API.
- **`Boolean` z `null`** nie jest modelem dwustanowym: `!disabled` może rzucić `NullPointerException`, a `!Boolean.TRUE.equals(disabled)` traktuje `null` jako „nie true” - to różne kontrakty.
- Jeśli negatywna nazwa jest polem JSON, kolumną bazy, kluczem konfiguracji lub elementem API, zmiana wymaga migracji granicy systemu - odwrócenie wartości w aplikacji nie migruje danych ani klientów.

---

## 10. Remove God Classes - kampania, nie pojedynczy ruch

- God Class skupia niezależne reguły, dane i integracje, przez co większość zmian przechodzi przez jedno miejsce.
- Rozpoznaje się ją nie po liczbie wierszy, lecz po niskiej spójności, wielu powodach zmiany, szerokim dostępie do stanu i roli centralnego węzła zależności.
- Pełne przepisanie klasy w jednym kroku ma wysokie ryzyko.
- Bezpieczniejsza jest kampania małych Extract Class, Move Function i Move Field prowadzona w obszarze aktualnie wymaganej zmiany.
- W przykładzie `LegacyReleaseManager` sam waliduje, przechowuje wydania, prowadzi audyt, wysyła powiadomienia i rejestruje zdarzenia.

---

## Remove God Classes - wydzielony pionowy fragment

```java
public PublishedRelease publish(String releaseId, String service,
                                String version) {
    PublishedRelease release = validator.validate(releaseId, service, version);
    if (repository.existsById(release.releaseId())) {
        throw new IllegalStateException(
                "release already published: " + release.releaseId());
    }
    repository.save(release);
    auditTrail.recordPublished(release);
    notifier.notifyPublished(release);
    return release;
}
```

- `ReleaseApplicationService` zachowuje koordynację procesu, a walidator, repozytorium, audyt i powiadomienia mają osobne kontrakty.
- Nie każdy jednolinijkowy krok wymaga klasy - granice wynikają z odpowiedzialności i kierunku zmian.

---

## Remove God Classes - atomowość i ryzyka

- Przykład świadomie **nie zapewnia transakcji** obejmującej `save`, `audit` i `notify`.
- Testy definiują stan po awarii każdego kroku: awaria zatrzymuje sekwencję, ale nie wycofuje wcześniej zakończonych efektów.
- Wymóg atomowości może prowadzić do transakcji, transactional outbox, idempotentnych ponowień lub kompensacji - tej decyzji nie ukrywa się w ruchu klas.
- Podział może zmienić transakcję, kolejność efektów, blokady, tożsamość encji, lazy loading, serializację lub konfigurację DI.
- Sieć małych klas o niskiej spójności nie jest automatycznie lepsza od jednej większej klasy.

---

## Remove God Classes - bezpieczna sekwencja

1. Zbuduj mapę metod, pól i zewnętrznych efektów, a następnie wybierz jedną odpowiedzialność związaną z aktualną zmianą.
2. Wyodrębnij najmniejszy pionowy fragment, zachowując dotychczasową klasę jako zgodną fasadę.
3. Przenoś najpierw metody położone na końcu grafu wywołań - mają najmniej zależności.
4. Utrzymuj jednego właściciela stanu i jednokierunkowe zależności; kompiluj i testuj po każdym ruchu.
5. Powtarzaj tylko tak długo, jak przynosi to wartość dla planowanych zmian.

---

## 11. Remove Boolean Method Parameters

- Flag argument pozwala wywołującemu wybrać wariant algorytmu literałem `true`/`false`, który w miejscu wywołania nie komunikuje intencji.
- Nie każdy `boolean` jest flagą - wartość z formularza lub komunikatu może być zwykłą daną domenową.

```java
// przed: execute(String deploymentId, boolean dryRun)
public String preview(String deploymentId) {
    return execute(deploymentId, ExecutionMode.PREVIEW);
}
public String deploy(String deploymentId) {
    return execute(deploymentId, ExecutionMode.DEPLOY);
}
private String execute(String deploymentId, ExecutionMode mode) { ... }
```

- Metody `preview` i `deploy` ujawniają zamiar klienta, a wspólna implementacja przyjmuje nazwany tryb - bez dublowania algorytmu.

---

## Remove Boolean Method Parameters - migracja

- Dodaj jawne metody dla znaczących wariantów i skieruj je do istniejącej implementacji.
- Migruj klientów przekazujących literały; w publicznej bibliotece pozostaw czasowo starą metodę delegującą.
- Prywatny algorytm rozplątuj dopiero w osobnych krokach, a starą sygnaturę usuń po zakończeniu migracji.
- Wiele flag tworzy potencjalnie wykładniczą liczbę kombinacji - zamiast generować metodę dla każdej, zbadaj ponownie odpowiedzialności i rozważ obiekt polityki.

---

## 12. Remove Middle Man

- Middle Man przekazuje wywołania bez dodawania polityki, stabilnej granicy ani ukrycia szczegółów.
- Jeśli większość API klasy jest przezroczystą kopią API współpracownika, klient może zależeć bezpośrednio od właściwego kontraktu.

```java
// przed: ReleaseDashboard -> ReleaseService.statusOf -> registry.statusOf
// po: dashboard zależy bezpośrednio od DeploymentRegistry
public ReleaseDashboard(DeploymentRegistry registry) {
    this.registry = Objects.requireNonNull(registry, "registry must not be null");
}
public String render(String deploymentId) {
    return deploymentId + " -> " + registry.statusOf(deploymentId);
}
```

---

## Remove Middle Man - sekwencja i granice

- Najpierw potwierdź, że delegacja nie realizuje autoryzacji, transakcji, telemetryki, retry ani translacji błędów.
- Udostępnij klientowi właściwy kontrakt lub wstrzyknij go bezpośrednio, migrując jednego klienta naraz.
- Stare metody pozostaw jako forwardery w okresie zgodności; pośrednika usuń po potwierdzeniu braku wywołań.
- Fasada modułu, adapter biblioteki zewnętrznej i seam testowy mogą być wartościowe nawet przy jednej linii implementacji.
- Remove Middle Man jest odwrotnością Hide Delegate - wybór zależy od tego, która granica lepiej chroni klientów przed zmianą.

---

## 13. Return ASAP

- Wcześniejszy zwrot usuwa zbędną zmienną wyniku albo kończy przetwarzanie, gdy wynik jest już ostateczny - nie jest celem samym w sobie.

```java
// przed: while (result == null && index < artifacts.size()) { ... }
for (int index = 0; index < artifacts.size(); index++) {
    Artifact artifact = Objects.requireNonNull(
            artifacts.get(index), "artifact must not be null");
    if (checksum.equals(artifact.checksum())) {
        return Optional.of(artifact);
    }
}
return Optional.empty();
```

- Oba warianty kończą na pierwszym dopasowaniu i zwracają tę samą instancję; `after` celowo zachowuje `size()`/`get(index)`, bo iterator mógłby zmienić liczbę wywołań na niestandardowej `List`.

---

## Return ASAP - bezpieczna sekwencja

- Scharakteryzuj wynik i efekty każdej ścieżki, a zwalnianie zasobów zabezpiecz przez try-with-resources albo `finally`.
- Wprowadź wcześniejszy zwrot dokładnie tam, gdzie wynik staje się ostateczny, zachowując wcześniejsze mutacje należące do kontraktu.
- Usuń odpowiadający `else` lub zmienną sterującą; zmienną objaśniającą usuwaj tylko, gdy jej nazwa nie wnosi istotnego pojęcia.
- `return` wewnątrz `try` nadal uruchamia `finally`, a nagłe zakończenie samego `finally` może zastąpić wcześniejszy wynik albo wyjątek.
- Przenoszenie instrukcji między tymi blokami wymaga osobnych testów.

---

## Ćwiczenie: Warsztat 1 - seam przed zmianą integracji

**Cel:** przygotować usługę, która sama tworzy klienta zewnętrznego, do testowanej zmiany reguły biznesowej.

1. Dodaj testy charakterystyki wyniku oraz kolejności wywołań.
2. Zidentyfikuj najmniejszą operację potrzebną od klienta i wprowadź punkt podstawienia bez zmiany produkcyjnego składania.
3. Dodaj spy rejestrujący argumenty, a dopiero potem zaimplementuj nową regułę.

**Kryteria akceptacji:** implementacja produkcyjna nadal wybierana w composition root; test nie uruchamia infrastruktury; kontrakt nie ma nieużywanych metod; liczba i kolejność wywołań są jawnie sprawdzone.

---

## Ćwiczenie: Warsztat 2 - dekompozycja długiej metody

**Cel:** wybrać między Extract Method, Split Phase i Extract Method Object na podstawie przepływu danych.

1. Narysuj wejścia, wyjścia i mutowane lokalne wartości kolejnych fragmentów.
2. Wydziel najpierw krok z najmniejszą liczbą zależności i utrzymaj metodę nadrzędną na jednym poziomie abstrakcji.
3. Gdy listy argumentów zaczynają rosnąć, oceń Method Object; porównaj wynik, wyjątki i brak modyfikacji wejściowych kolekcji.

**Kryteria akceptacji:** nazwy opisują intencję, nie mechanikę; kolejność kroków jest jawna; obiekt metody nie jest współdzielony; refaktoryzacja nie dodaje nowej walidacji.

---

## Ćwiczenie: Warsztat 3 - stopniowe rozbijanie God Class

**Cel:** wydzielić jedną odpowiedzialność bez przepisywania centralnej klasy i bez zmiany kolejności efektów.

1. Zbuduj mapę metod do pól i integracji, wskaż klaster o samodzielnym powodzie zmiany.
2. Dodaj test śladu operacji dla sukcesu i awarii, wydziel najmniejszy pionowy fragment, stare API pozostaw jako delegujące.
3. Oceń granicę transakcji i własność stanu.

**Kryteria akceptacji:** stan ma jednego właściciela; brak cyklu zależności; walidacja zachowuje kolejność i komunikaty; po błędzie późniejsze kroki się nie wykonują, a wcześniejsze efekty zgadzają się z kontraktem; kolejne kroki kampanii można wdrażać niezależnie.

---

## Lista kontrolna przeglądu - zachowanie i projekt

**Zachowanie**
- Czy testy obejmują wszystkie gałęzie i priorytet nakładających się warunków oraz typ, komunikat i moment wyjątków?
- Czy stan po nieudanej operacji jest identyczny, a liczba, kolejność i argumenty efektów ubocznych są chronione?
- Czy wcześniejszy `return` nie omija zwalniania zasobów ani wymaganej mutacji?

**Projekt**
- Czy seam opisuje potrzebę klienta, a Parameter Object pojęcie, a nie worek ustawień?
- Czy usunięto duplikację wiedzy, a nie przypadkowe podobieństwo, i czy każdy stan ma jednego właściciela?
- Czy usuwany pośrednik rzeczywiście nie chronił wartościowej granicy?

---

## Lista kontrolna przeglądu - migracja

- Czy nowe i stare API mogą czasowo współistnieć, żeby klienci migrowali we własnym tempie?
- Czy istnieją klienci dostarczani jako już skompilowane pliki `.class`, dla których liczy się zgodność binarna?
- Czy zmiana rekordu, serializacji albo trwałych nazw wymaga migracji danych?
- Czy zaostrzenie kontraktu jest oddzielone od ruchu strukturalnego?
- Czy usunięcie elementów przejściowych ma jawne kryterium zakończenia?

---

## Podsumowanie - najważniejsze wnioski

- Zaawansowana refaktoryzacja to nie większe skoki, lecz rozkładanie trudnego problemu na mniejsze ruchy z bogatszą obserwacją zachowania.
- Seam umożliwia zmianę zależności, ale nie wymaga interfejsu dla każdej klasy; Extract Method jest pierwszym wyborem, Method Object - odpowiedzią na trudny przepływ lokalnego stanu.
- Duplikacja i duża klasa to diagnozy wymagające analizy wiedzy i powodów zmiany; guard clauses i wczesne zwroty są bezpieczne tylko przy zachowaniu kolejności i efektów.
- Publiczne kontrakty wymagają jawnych kontroli niezależnych od `assert`; flagę, pośrednika i negatywną nazwę usuwa się na podstawie semantyki, nie mechanicznej reguły.
- Zgodność strukturalna, źródłowa, binarna i behawioralna to różne własności - im bardziej publiczne API, tym ważniejsze małe kroki, testy różnicowe i świadoma migracja.
