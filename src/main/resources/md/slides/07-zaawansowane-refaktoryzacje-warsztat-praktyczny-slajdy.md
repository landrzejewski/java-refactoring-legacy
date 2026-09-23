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

- **Model bezpieczeństwa** - refaktoryzacja a zmiana kontraktu, pętla pracy, pułapki Javy 25.
- **Zależności i struktura:** Break Dependencies, Extract Method Object, Break Responsibilities, Remove Duplication, Break Method.
- **Sygnatury i warunki:** Introduce Parameter Object, Remove Arrowhead Antipattern, Introduce Design by Contract Checks, Remove Double Negative.
- **Klasy i API:** Remove God Classes, Remove Boolean Method Parameters, Remove Middle Man, Return ASAP.
- **Warsztaty 1-3**, lista kontrolna i podsumowanie.

---

## Refaktoryzacja a zmiana kontraktu

- Refaktoryzacja zachowuje **obserwowalne zachowanie** w przyjętej granicy - nie strukturę klas ani prywatne metody.
- Kryterium: klient nie dostrzega żadnej nieuzgodnionej różnicy.
- Nowa walidacja, inny typ wyjątku, kopia defensywna, usunięcie publicznej metody - to **zmiany kontraktu**.
- Takie kroki wykonuj i zatwierdzaj **osobno** od ruchów strukturalnych.

---

## Wektor obserwowalnego zachowania

| Obszar | Co sprawdzić |
| --- | --- |
| Wynik | wartość zwracana i tożsamość obiektu |
| Wyjątki | typ, komunikat, moment zgłoszenia |
| Stan | stan odbiorcy po sukcesie i po błędzie |
| Współpracownicy | kolejność i liczba wywołań, short-circuit warunków |
| Czas i aliasy | moment pobrania zależności/zegara, aliasowanie mutowalnych obiektów |
| Granice | transakcje, blokady, wątki, API, dane trwałe, serializacja |

Granica kontraktu ma być **świadomą decyzją**, nie skutkiem zbyt słabego testu.

---

## Pętla pracy

1. Nazwij przeszkodę i zapisz przykłady: ścieżka poprawna, granice, awarie.
2. Wybierz najmniejszy ruch, zachowując kolejność obliczeń, efektów i wyjątków.
3. Po każdym ruchu: kompilacja i zawężony zestaw testów.
4. Migruj jedno wywołanie naraz; element przejściowy usuń po migracji wszystkich klientów.
5. Zmiany kontraktu i optymalizacje - w osobnych krokach.

---

## Java 25 w tym module

- **Interfejs funkcyjny** to wygodny seam - w teście lambda jako fake albo spy; interfejs `sealed` zamyka zbiór implementacji, więc się nie nadaje.
- **Rekord** dobrze modeluje Parameter Object, ale jest tylko **płytko** niemutowalny.
- **`assert`** jest domyślnie wyłączony - nie wymusza warunków publicznego API.
- `&&`/`||` stosują short-circuit - przestawienie warunków zmienia liczbę wywołań i pierwszy wyjątek.
- **Zgodność binarna:** stary `.class` może oczekiwać starej sygnatury - okres przejściowy z przestarzałym przeciążeniem.

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
| ćwiczenia warsztatowe (Warsztaty 1-3) | 60 min |
| przegląd rozwiązań i podsumowanie | 15 min |

---

## 1. Break Dependencies - intencja i ryzyka

- Tworzy **seam** - miejsce podstawienia innej implementacji bez edycji algorytmu.
- **Separation** - uruchomienie bez kosztownego otoczenia; **sensing** - obserwacja komunikacji.
- Najwęższy skuteczny seam: parametr, zależność konstruktora, `Clock`, funkcja - nie interfejs przed każdą klasą.
- **Sekwencja:** charakterystyka → najwęższy kontrakt → dotychczasowa implementacja przez kontrakt → test z fake/spy → nowa logika.
- **Ryzyko:** zmiana czasu życia, współdzielenia, inicjalizacji, blokady lub transakcji zależności.

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

Produkcja nadal składa serwis z dotychczasową implementacją - zmienia się **miejsce tworzenia zależności**, nie reguła.

---

## 2. Extract Method Object - przed

- Przenosi jedno wykonanie złożonego algorytmu do nowego obiektu; lokalne wartości robocze stają się **polami**.
- Obiekt metody **nie jest usługą współdzieloną** - powstaje dla każdego wywołania.

```java
public RiskAssessment calculate(DeploymentRiskInput input) {
    Objects.requireNonNull(input, "input");
    long score = input.changedFiles();
    score += (long) input.criticalServices() * 20;
    score += (long) input.failedChecks() * 10;
    if (input.rollbackTested()) score -= 15;
    score = Math.max(0, Math.min(100, score));
    RiskLevel level;
    if (score < 30) level = RiskLevel.LOW;
    else if (score < 70) level = RiskLevel.MEDIUM;
    else level = RiskLevel.HIGH;
    return new RiskAssessment((int) score, level);
}
```

---

## Extract Method Object - po, sekwencja i ryzyka

```java
public RiskAssessment calculate(DeploymentRiskInput input) {   // fasada
    return new DeploymentRiskCalculation(input).calculate();
}
final class DeploymentRiskCalculation {
    private long score;                        // wartość robocza jako pole
    RiskAssessment calculate() {
        addChangedFilesRisk(); addCriticalServicesRisk(); addFailedChecksRisk();
        applyRollbackReduction(); limitScore();
        return new RiskAssessment((int) score, classify());
    }
}
```

- **Sekwencja:** skopiuj ciało **bez upraszczania**, deleguj, porównaj, dopiero potem pola i ekstrakcje.
- **Ryzyko:** moment odczytu danych, kolejność wyjątków, zakres synchronizacji.
- Niestatyczna klasa wewnętrzna ukrywa referencję do klasy zewnętrznej.

---

## 3. Break Responsibilities - intencja i ryzyka

- Odpowiedzialność to **powód zmiany**, nie liczba metod.
- `LegacyDeploymentReport.generate` liczy metryki wdrożeń i formatuje raport - dwa różne powody zmiany.
- **Sekwencja:** wydziel spójny klaster, stara klasa jako delegująca fasada, migruj klientów pojedynczo.
- Stan przenoś do **jednego właściciela** - bez dwóch kopii.
- **Ryzyko:** rozdzielenie transakcji lub blokady; przy ORM i serializacji przeniesienie pola zmienia kontrakt.

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
// calculator zwraca new DeploymentMetrics(deployments, failures,
//                                          averageLeadTimeMinutes)
```

Serwis składa dwa kroki, ale nie przejmuje ich szczegółów; kierunek zależności jest czytelny.

---

## 4. Remove Duplication - duplikacja wiedzy

- Łączymy fragmenty reprezentujące **tę samą regułę**, które zmieniają się razem - nie podobny tekst.
- Podobne reguły z niezależnych kontekstów mogą celowo ewoluować osobno.
- Abstrakcja wymagająca wielu flag i wyjątków sygnalizuje sklejenie przypadkowo podobnych procesów.
- **Sekwencja:** scharakteryzuj obie wersje osobno → porównaj przypadki brzegowe → ujednolij nazwy → wydziel regułę → przełączaj po jednej ścieżce.

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

Wspólna reguła ma jednego właściciela, różnica (`-SNAPSHOT`) pozostaje jawna, a publiczne metody komunikują dwa zamiary.

---

## 5. Break Method - intencja

- Seria małych ekstrakcji, po której metoda opisuje algorytm **na jednym poziomie abstrakcji**.
- Kryterium to nazwane spójne kroki, nie długość w wierszach.

```java
public String build(List<ManifestEntry> entries) {
    List<ManifestEntry> validatedEntries = validateAndCopy(entries);
    List<ManifestEntry> orderedEntries = order(validatedEntries);
    return render(orderedEntries);
}
```

Dane łatwo przechodzą między krokami - Method Object byłby tu zbędny.

---

## Break Method - bezpieczna sekwencja

- Zacznij od fragmentu z małą liczbą wejść i najwyżej jednym wynikiem; przenieś go **dosłownie**.
- Testy, potem nazwa opisująca intencję; powtarzaj.
- **Split Phase** - gdy kolejne fazy mają osobne modele danych.
- **Method Object** - dopiero gdy przepływ lokalnego stanu blokuje ekstrakcje.
- Uwaga na `return`, `break`, `continue`, aliasy i kod wewnątrz blokady.

---

## 6. Introduce Parameter Object - data clump jako pojęcie

- Grupa parametrów ma opisywać **jedno pojęcie**, nie ukrywać długiej listy argumentów.
- Typ nazywa pojęcie, ogranicza pomyłki kolejności i przyciąga zachowanie.

```java
// przed: estimateSeconds(service, region, instances, batchSize, pauseSeconds)
//        describe(service, region, instances, batchSize, pauseSeconds)
public record RolloutSpec(String service, String region,
        int instances, int batchSize, int pauseSeconds) { ... }

public long estimateSeconds(RolloutSpec spec) { ... }
public String describe(RolloutSpec spec) { ... }
```

---

## Parameter Object - walidacja, migracja i ryzyka

- Walidacja w konstruktorze rekordu odrzuca dane **wcześniej** - to zmiana momentu błędu, osobny krok.
- Stan przejściowy: rekord bez walidacji, kontrole nadal w planerze.
- **Migracja:** nowa metoda z typem, stara sygnatura delegująca, wywołania pojedynczo.
- `List.copyOf` nie gwarantuje nowej instancji - może zmienić semantykę mutowalności i aliasowania.
- Komponenty publicznego rekordu są częścią API; worek `Parameters` z flagami maskuje problem.

---

## 7. Remove Arrowhead Antipattern

- Główna ścieżka zostaje przesunięta przez kolejne poziomy `if`/`else`.
- **Guard clauses** wydzielają przypadki kończące przetwarzanie.
- Spłaszczenie jest bezpieczne tylko przy **zachowanym priorytecie warunków** - brak akceptacji i nieudane testy nadal dają `NOT_APPROVED`.
- **Sekwencja:** macierz gałęzi z kombinacjami → odwróć najbardziej zewnętrzny warunek → usuń `else` → testy → kolejny poziom.
- Nie pomijaj logowania, zmian stanu ani zwalniania zasobów (try-with-resources, `finally`).

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

Legacy: pięć poziomów zagnieżdżenia i zmienna `result`; **kolejność warunków identyczna**.

---

## 8. Introduce Design by Contract Checks

- **Precondition** - obowiązek klienta; **postcondition** - gwarancja operacji; **invariant** - właściwość zachowana między operacjami.
- Podtyp nie powinien wzmacniać warunków wstępnych ani osłabiać końcowych.
- Dodanie odrzucenia wartości to **zmiana zachowania**, nie refaktoryzacja.
- `assert` działa tylko z `-ea` - nie do argumentów metod publicznych.
- Historyczny typ wyjątku może być kontraktem - nie zastępuj go bez decyzji.

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

- `require` → `IllegalArgumentException`; `ensure` i invariant → `IllegalStateException`.
- Preconditions przed pierwszą mutacją - naruszenie nie zmienia obiektu.

---

## 9. Remove Double Negative

```java
// przed: record LegacyReleaseReadiness(boolean notApproved, ...)
return !readiness.notApproved() && !readiness.testsNotPassed()
        && !readiness.windowNotOpen();
// po: record ReleaseReadiness(boolean approved, ...)
return readiness.approved() && readiness.testsPassed()
        && readiness.windowOpen();
```

- Pozytywna nazwa = **dokładne logiczne dopełnienie** starej właściwości.
- **Sekwencja:** predykat pozytywny delegujący → migracja po jednym użyciu → odwrócenie delegacji.
- `Boolean` z `null` nie jest dwustanowy: `!disabled` przy `null` rzuca NPE, `!Boolean.TRUE.equals(disabled)` - nie.
- Negatywna nazwa w JSON, bazie lub konfiguracji wymaga **migracji granicy**.

---

## 10. Remove God Classes - kampania, nie pojedynczy ruch

- God Class: niska spójność, wiele powodów zmiany, centralny węzeł zależności - nie liczba wierszy.
- `LegacyReleaseManager` waliduje, przechowuje, audytuje, powiadamia i rejestruje zdarzenia.
- Zamiast przepisania - kampania małych **Extract Class, Move Function, Move Field** w obszarze bieżącej zmiany.
- **Sekwencja:** mapa metod, pól i efektów → najmniejszy pionowy fragment → stara klasa jako fasada → jeden właściciel stanu.
- Kontynuuj, dopóki przynosi to wartość dla planowanych zmian.

---

## Remove God Classes - wydzielony fragment i ryzyka

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

- Przykład **nie zapewnia transakcji** - awaria zatrzymuje sekwencję, ale nie wycofuje wcześniejszych efektów.
- Atomowość (transakcja, outbox, kompensacja) to osobna decyzja, nie skutek ruchu klas.

---

## 11. Remove Boolean Method Parameters

```java
// przed: execute(String deploymentId, boolean dryRun)
public String preview(String deploymentId) {
    return execute(deploymentId, ExecutionMode.PREVIEW);
}
public String deploy(String deploymentId) {
    return execute(deploymentId, ExecutionMode.DEPLOY);
}
```

- Literał `true`/`false` nie komunikuje intencji; nie każdy `boolean` jest jednak flagą.
- **Migracja:** jawne metody → klienci pojedynczo → stara metoda delegująca do końca migracji.
- Wiele flag: zamiast metody na kombinację - przegląd odpowiedzialności i **obiekt polityki**.

---

## 12. Remove Middle Man

```java
// przed: ReleaseDashboard -> ReleaseService.statusOf -> registry.statusOf
public ReleaseDashboard(DeploymentRegistry registry) {
    this.registry = Objects.requireNonNull(registry, "registry must not be null");
}
public String render(String deploymentId) {
    return deploymentId + " -> " + registry.statusOf(deploymentId);
}
```

- Najpierw potwierdź, że pośrednik nie realizuje autoryzacji, transakcji, telemetrii, retry ani translacji błędów.
- Migruj jednego klienta naraz; stare metody jako forwardery w okresie zgodności.
- Fasada modułu, adapter czy seam testowy mogą być wartościowe; odwrotność: **Hide Delegate**.

---

## 13. Return ASAP

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

- Zwrot tam, gdzie wynik jest **ostateczny**, z zachowaniem wcześniejszych mutacji.
- `size()`/`get(index)` celowo zachowane - iterator zmieniłby liczbę wywołań.
- `return` w `try` uruchamia `finally`, a nagłe zakończenie `finally` może zastąpić wynik.

---

## Warsztat 1 - seam przed zmianą integracji (20 min)

**Cel:** przygotować usługę, która sama tworzy klienta zewnętrznego, do testowanej zmiany reguły biznesowej.

- Najpierw testy charakterystyki wyniku i kolejności wywołań, potem seam i spy.
- Implementacja produkcyjna nadal wybierana w composition root; kontrakt bez nieużywanych metod.
- Nowa reguła dopiero po refaktoryzacji, w osobnym kroku.

Szczegóły: zadania modułu 7, Warsztat 1.

---

## Warsztat 2 - dekompozycja długiej metody (20 min)

**Cel:** wybrać między Extract Method, Split Phase i Extract Method Object na podstawie przepływu danych.

- Rozrysuj wejścia, wyjścia i mutowane wartości lokalne; zacznij od kroku z najmniejszą liczbą zależności.
- Obiekt metody nie jest współdzielony; refaktoryzacja nie dodaje walidacji.
- Lista wejściowa pozostaje niezmieniona; wybór techniki uzasadniony.

Szczegóły: zadania modułu 7, Warsztat 2.

---

## Warsztat 3 - stopniowe rozbijanie God Class (20 min)

**Cel:** wydzielić jedną odpowiedzialność bez przepisywania centralnej klasy i bez zmiany kolejności efektów.

- Mapa metod do pól i integracji; test śladu operacji dla sukcesu i awarii.
- Stare API delegujące; stan ma jednego właściciela, brak cyklu zależności.
- Oceń granicę transakcji i stan po wyjątku wydzielonego kroku.

Szczegóły: zadania modułu 7, Warsztat 3.

---

## Lista kontrolna przeglądu

- **Zachowanie:** gałęzie i priorytet warunków; typ, komunikat i moment wyjątków; stan po błędzie; liczba i kolejność efektów.
- **Zasoby:** wcześniejszy `return` nie omija zwalniania zasobów ani wymaganej mutacji.
- **Projekt:** seam opisuje potrzebę klienta, Parameter Object - pojęcie; duplikacja wiedzy, nie tekstu; jeden właściciel stanu.
- **Migracja:** stare i nowe API współistnieją; zgodność binarna; migracja danych przy zmianie rekordu lub nazw trwałych.
- **Kontrakt:** zaostrzenie oddzielone od ruchu strukturalnego; jawne kryterium usunięcia elementów przejściowych.

---

## Podsumowanie - najważniejsze wnioski

- Zaawansowana refaktoryzacja to **mniejsze ruchy** z bogatszą obserwacją zachowania, nie większe skoki.
- Seam nie wymaga interfejsu dla każdej klasy; Method Object - dopiero gdy przepływ lokalnego stanu blokuje ekstrakcje.
- Duplikacja i God Class to diagnozy oparte na **wiedzy i powodach zmiany**.
- Guard clauses i wczesne zwroty są bezpieczne tylko przy zachowaniu kolejności i efektów.
- Kontrakty publiczne wymagają jawnych kontroli niezależnych od `assert`.
- Zgodność strukturalna, źródłowa, binarna i behawioralna to **różne własności**.
