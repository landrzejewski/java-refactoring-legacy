# Moduł 7. Zaawansowane refaktoryzacje - warsztat praktyczny

## Zakres

1. Break Dependencies
2. Extract Method Object
3. Break Responsibilities
4. Remove Duplication
5. Break Method
6. Introduce Parameter Object
7. Remove Arrowhead Antipattern
8. Introduce Design by Contract Checks
9. Remove Double Negative
10. Remove God Classes
11. Remove Boolean Method Parameters
12. Remove Middle Man
13. Return ASAP

## Model bezpieczeństwa

### Refaktoryzacja a zmiana kontraktu

Refaktoryzacja zachowuje obserwowalne zachowanie w przyjętej granicy. Nie oznacza to zachowania identycznej struktury klas ani prywatnych metod. Oznacza natomiast, że klient nie powinien dostrzec nieuzgodnionej różnicy.

Dodanie odrzucenia niepoprawnego argumentu, zmiana typu wyjątku, wykonanie kopii defensywnej, zastąpienie przepełnienia przez `ArithmeticException` albo usunięcie publicznej metody jest zmianą zachowania lub kontraktu. Taki krok może być potrzebny, ale powinien zostać wykonany i zatwierdzony osobno.

### Wektor obserwowalnego zachowania

Dla każdej transformacji należy ustalić, które elementy są częścią kontraktu:

- wartość zwracana i tożsamość zwracanego obiektu,
- typ, komunikat i moment zgłoszenia wyjątku,
- stan odbiorcy po sukcesie i po błędzie,
- kolejność oraz liczba wywołań współpracowników,
- kolejność sprawdzania warunków i skróconego obliczania wyrażeń,
- czas pobrania zależności, konfiguracji albo zegara,
- aliasowanie mutowalnych obiektów,
- granica transakcji, blokady i widoczność między wątkami,
- zgodność publicznego API, danych trwałych i serializacji.

Nie każdy element musi być zamrożony na zawsze. Granica powinna być świadomą decyzją, a nie przypadkowym skutkiem zbyt słabego testu.

### Pętla pracy

1. Nazwij konkretną przeszkodę dla planowanej zmiany.
2. Zapisz przykłady charakteryzujące ścieżkę poprawną, granice i awarie.
3. Wybierz najmniejszy ruch strukturalny usuwający przeszkodę.
4. Zachowaj kolejność obliczeń, efektów i wyjątków.
5. Skompiluj oraz uruchom zawężony zestaw testów.
6. Migruj jedno wywołanie albo jedną odpowiedzialność naraz.
7. Usuń element przejściowy dopiero po migracji wszystkich klientów.
8. Zmiany kontraktu i optymalizacje zatwierdź w osobnych krokach.

### Java 25 w tym module

Interfejs funkcyjny jest wygodnym seam dla pojedynczej operacji i pozwala użyć lambdy jako obiektu testowego typu fake albo spy. Interfejs `sealed` nie jest interfejsem funkcyjnym w rozumieniu specyfikacji Javy. Uszczelnienie zamyka też zbiór implementacji, dlatego nie pasuje do otwartego punktu podstawiania zależności.

Rekord dobrze reprezentuje Parameter Object złożony z wartości. Jest jednak tylko płytko niemutowalny. Pole `final` chroni referencję, nie stan wskazanego obiektu. Kopia defensywna kolekcji może być właściwa, ale w istniejącym API zmienia semantykę aliasowania i wymaga osobnej decyzji.

Instrukcja `assert` jest domyślnie wyłączona i nie może wymuszać warunków publicznego API. Publiczne preconditions wymagają jawnych wyjątków. `assert` można pozostawić dla wewnętrznego założenia, którego naruszenie oznacza błąd implementacji i którego sprawdzenie nie ma skutków ubocznych.

Wyrażenia w Javie są obliczane od lewej do prawej, a `&&` oraz `||` stosują skrócone obliczanie, nazywane short-circuit evaluation. Mechaniczne przestawienie warunków może zmienić liczbę wywołań, pierwszy wyjątek i skutki uboczne.

Zmiana sygnatury metody może być poprawna źródłowo po migracji kodu, ale istniejący plik `.class` nadal może oczekiwać starego deskryptora i zakończyć wykonanie błędem linkowania. Dla publicznej biblioteki często potrzebny jest okres z delegującym przeciążeniem oznaczonym jako przestarzałe.

## Organizacja warsztatu

Sugerowany czas pracy synchronicznej wynosi 360 minut:

| Część | Czas |
| --- | ---: |
| model zachowania i Break Dependencies | 40 minut |
| Method Object, odpowiedzialności i duplikacja | 60 minut |
| Break Method i Parameter Object | 45 minut |
| Arrowhead, kontrakty i podwójne zaprzeczenia | 60 minut |
| God Class, flagi i Middle Man | 55 minut |
| Return ASAP i migracja API | 25 minut |
| ćwiczenia warsztatowe | 60 minut |
| przegląd rozwiązań i podsumowanie | 15 minut |

## 1. Break Dependencies

### 1.1. Intencja

Break Dependencies tworzy miejsce, w którym kod może współpracować z inną implementacją bez edycji własnego algorytmu. Taki punkt podstawienia bywa nazywany seam. W pracy z legacy code służy zwykle do dwóch celów:

- separation, czyli uruchomienia badanego kodu bez kosztownego lub niedostępnego otoczenia,
- sensing, czyli obserwowania komunikacji, której wynik nie jest bezpośrednio zwracany.

Nie chodzi o wprowadzenie interfejsu przed każdą klasą. Najwęższy skuteczny seam może być parametrem metody, zależnością konstruktora, obiektem `Clock`, funkcją albo chronioną metodą przejściową.

### 1.2. Stan przed zmianą

Plik `pl/training/module7/breakdependencies/before/LegacyDeploymentWindowService.java`:

```java
package pl.training.module7.breakdependencies.before;

import java.util.Objects;

import pl.training.module7.breakdependencies.DeploymentDecision;

public final class LegacyDeploymentWindowService {
    private final StandardMaintenanceWindows maintenanceWindows =
            new StandardMaintenanceWindows();

    public DeploymentDecision schedule(String service, int hourUtc) {
        validate(service, hourUtc);
        return maintenanceWindows.allows(service, hourUtc)
                ? DeploymentDecision.ALLOWED
                : DeploymentDecision.OUTSIDE_MAINTENANCE_WINDOW;
    }

    private static void validate(String service, int hourUtc) {
        Objects.requireNonNull(service, "service");
        if (service.isBlank()) {
            throw new IllegalArgumentException("service must not be blank");
        }
        if (hourUtc < 0 || hourUtc > 23) {
            throw new IllegalArgumentException(
                    "hourUtc must be between 0 and 23");
        }
    }
}
```

Serwis sam wybiera i tworzy implementację kalendarza. Test nie może podstawić kontrolowanego wyniku bez zmiany kodu produkcyjnego.

### 1.3. Stan po zmianie

Plik `pl/training/module7/breakdependencies/after/MaintenanceWindows.java`:

```java
package pl.training.module7.breakdependencies.after;

@FunctionalInterface
public interface MaintenanceWindows {
    boolean allows(String service, int hourUtc);
}
```

Plik `pl/training/module7/breakdependencies/after/DeploymentWindowService.java`:

```java
package pl.training.module7.breakdependencies.after;

import java.util.Objects;

import pl.training.module7.breakdependencies.DeploymentDecision;

public final class DeploymentWindowService {
    private final MaintenanceWindows maintenanceWindows;

    public DeploymentWindowService(MaintenanceWindows maintenanceWindows) {
        this.maintenanceWindows = Objects.requireNonNull(
                maintenanceWindows, "maintenanceWindows");
    }

    public DeploymentDecision schedule(String service, int hourUtc) {
        validate(service, hourUtc);
        return maintenanceWindows.allows(service, hourUtc)
                ? DeploymentDecision.ALLOWED
                : DeploymentDecision.OUTSIDE_MAINTENANCE_WINDOW;
    }

    private static void validate(String service, int hourUtc) {
        Objects.requireNonNull(service, "service");
        if (service.isBlank()) {
            throw new IllegalArgumentException("service must not be blank");
        }
        if (hourUtc < 0 || hourUtc > 23) {
            throw new IllegalArgumentException(
                    "hourUtc must be between 0 and 23");
        }
    }
}
```

Produkcja nadal składa serwis z dotychczasową implementacją. Zmienia się miejsce tworzenia zależności, nie reguła biznesowa.

### 1.4. Bezpieczna sekwencja

1. Scharakteryzuj wynik i wywołania istniejącej zależności.
2. Wprowadź najwęższy kontrakt potrzebny przez klienta.
3. Przekaż dotychczasową implementację przez nowy punkt podstawienia.
4. Potwierdź równoważność produkcyjnego składania.
5. Dodaj test z obiektem typu fake albo spy.
6. Dopiero potem rozwijaj logikę biznesową.

### 1.5. Ryzyka

Przeniesienie tworzenia zależności może nieświadomie zmienić jej czas życia, współdzielenie, inicjalizację statyczną, blokadę lub granicę transakcji. Interfejs nie powinien ujawniać całego API implementacji. Powinien opisywać dokładnie potrzebę klienta.

## 2. Extract Method Object

### 2.1. Kiedy zwykła ekstrakcja nie wystarcza

Extract Method Object, nazywane także Replace Function with Command, przenosi pojedyncze wykonanie złożonego algorytmu do nowego obiektu. Parametry i lokalne wartości robocze stają się jego polami. Dzięki temu kolejne kroki można wydzielać bez przekazywania długich list argumentów i wyników pośrednich.

Obiekt metody nie jest usługą współdzieloną przez aplikację. Powinien powstawać dla każdego wywołania. Współdzielenie mutowalnego stanu obliczenia prowadziłoby do błędów przy ponownym wejściu i wykonaniu współbieżnym.

### 2.2. Stan przed zmianą

Plik `pl/training/module7/methodobject/before/LegacyDeploymentRiskCalculator.java`:

```java
package pl.training.module7.methodobject.before;

import java.util.Objects;

import pl.training.module7.methodobject.DeploymentRiskInput;
import pl.training.module7.methodobject.RiskAssessment;
import pl.training.module7.methodobject.RiskLevel;

public final class LegacyDeploymentRiskCalculator {
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
        if (score < 30) {
            level = RiskLevel.LOW;
        } else if (score < 70) {
            level = RiskLevel.MEDIUM;
        } else {
            level = RiskLevel.HIGH;
        }

        return new RiskAssessment((int) score, level);
    }
}
```

### 2.3. Stan po zmianie

Plik `pl/training/module7/methodobject/after/DeploymentRiskCalculator.java`:

```java
package pl.training.module7.methodobject.after;

import pl.training.module7.methodobject.DeploymentRiskInput;
import pl.training.module7.methodobject.RiskAssessment;

public final class DeploymentRiskCalculator {
    public RiskAssessment calculate(DeploymentRiskInput input) {
        return new DeploymentRiskCalculation(input).calculate();
    }
}
```

Plik `pl/training/module7/methodobject/after/DeploymentRiskCalculation.java`:

```java
package pl.training.module7.methodobject.after;

import java.util.Objects;

import pl.training.module7.methodobject.DeploymentRiskInput;
import pl.training.module7.methodobject.RiskAssessment;
import pl.training.module7.methodobject.RiskLevel;

final class DeploymentRiskCalculation {
    private static final long CRITICAL_SERVICE_POINTS = 20;
    private static final long FAILED_CHECK_POINTS = 10;
    private static final long TESTED_ROLLBACK_REDUCTION = 15;

    private final DeploymentRiskInput input;
    private long score;

    DeploymentRiskCalculation(DeploymentRiskInput input) {
        this.input = Objects.requireNonNull(input, "input");
    }

    RiskAssessment calculate() {
        addChangedFilesRisk();
        addCriticalServicesRisk();
        addFailedChecksRisk();
        applyRollbackReduction();
        limitScore();
        return new RiskAssessment((int) score, classify());
    }

    private void addChangedFilesRisk() {
        score += input.changedFiles();
    }

    private void addCriticalServicesRisk() {
        score += (long) input.criticalServices() * CRITICAL_SERVICE_POINTS;
    }

    private void addFailedChecksRisk() {
        score += (long) input.failedChecks() * FAILED_CHECK_POINTS;
    }

    private void applyRollbackReduction() {
        if (input.rollbackTested()) {
            score -= TESTED_ROLLBACK_REDUCTION;
        }
    }

    private void limitScore() {
        score = Math.max(0, Math.min(100, score));
    }

    private RiskLevel classify() {
        if (score < 30) {
            return RiskLevel.LOW;
        }
        if (score < 70) {
            return RiskLevel.MEDIUM;
        }
        return RiskLevel.HIGH;
    }
}
```

Fasada zachowuje dotychczasowy punkt wejścia. Nowa klasa reprezentuje jedno obliczenie, a jej prywatne metody operują na polach roboczych.

### 2.4. Bezpieczna sekwencja

1. Utwórz klasę nazwaną zgodnie z wykonywanym zadaniem.
2. Przenieś parametry do konstruktora.
3. Skopiuj ciało starej metody bez równoczesnego upraszczania.
4. Zastąp starą metodę delegacją do nowego obiektu.
5. Porównaj zachowanie obu wariantów.
6. Zamień potrzebne zmienne lokalne na pola.
7. Wydzielaj kolejne kroki dopiero po uzyskaniu równoważności.

### 2.5. Ryzyka

Należy zachować moment odczytu danych, kolejność wyjątków, efekty uboczne oraz zakres synchronizacji. Niestatyczna klasa wewnętrzna przechwytuje instancję klasy zewnętrznej, dlatego łatwo ukrywa rzeczywiste zależności. Zwykła osobna klasa albo statyczna klasa zagnieżdżona daje czytelniejszą granicę.

## 3. Break Responsibilities

### 3.1. Odpowiedzialność jako powód zmiany

Odpowiedzialności nie mierzy się liczbą metod. Dwa fragmenty należą do różnych odpowiedzialności, gdy zmieniają się z innych powodów, wymagają innej wiedzy albo mają innych odbiorców. W przykładzie obliczanie metryk wdrożeń i format tekstowego raportu podlegają innym regułom.

### 3.2. Stan przed zmianą

Plik `pl/training/module7/breakresponsibilities/before/LegacyDeploymentReport.java`:

```java
package pl.training.module7.breakresponsibilities.before;

import java.util.List;
import java.util.Objects;

import pl.training.module7.breakresponsibilities.DeploymentSample;

public final class LegacyDeploymentReport {
    public String generate(List<DeploymentSample> samples) {
        Objects.requireNonNull(samples, "samples");

        int deployments = 0;
        int failures = 0;
        long totalLeadTimeMinutes = 0;

        for (DeploymentSample sample : samples) {
            Objects.requireNonNull(sample, "sample");
            deployments++;
            if (!sample.successful()) {
                failures++;
            }
            totalLeadTimeMinutes = Math.addExact(
                    totalLeadTimeMinutes, sample.leadTimeMinutes());
        }

        long averageLeadTimeMinutes = deployments == 0
                ? 0
                : totalLeadTimeMinutes / deployments;

        return "deployments=" + deployments
                + ";failures=" + failures
                + ";avgLeadTimeMinutes=" + averageLeadTimeMinutes;
    }
}
```

### 3.3. Stan po zmianie

Plik `pl/training/module7/breakresponsibilities/after/DeploymentMetricsCalculator.java`:

```java
package pl.training.module7.breakresponsibilities.after;

import java.util.List;
import java.util.Objects;

import pl.training.module7.breakresponsibilities.DeploymentMetrics;
import pl.training.module7.breakresponsibilities.DeploymentSample;

public final class DeploymentMetricsCalculator {
    public DeploymentMetrics calculate(List<DeploymentSample> samples) {
        Objects.requireNonNull(samples, "samples");

        int deployments = 0;
        int failures = 0;
        long totalLeadTimeMinutes = 0;

        for (DeploymentSample sample : samples) {
            Objects.requireNonNull(sample, "sample");
            deployments++;
            if (!sample.successful()) {
                failures++;
            }
            totalLeadTimeMinutes = Math.addExact(
                    totalLeadTimeMinutes, sample.leadTimeMinutes());
        }

        long averageLeadTimeMinutes = deployments == 0
                ? 0
                : totalLeadTimeMinutes / deployments;

        return new DeploymentMetrics(
                deployments, failures, averageLeadTimeMinutes);
    }
}
```

Plik `pl/training/module7/breakresponsibilities/after/DeploymentReportService.java`:

```java
package pl.training.module7.breakresponsibilities.after;

import java.util.List;
import java.util.Objects;

import pl.training.module7.breakresponsibilities.DeploymentSample;

public final class DeploymentReportService {
    private final DeploymentMetricsCalculator calculator;
    private final DeploymentReportFormatter formatter;

    public DeploymentReportService(
            DeploymentMetricsCalculator calculator,
            DeploymentReportFormatter formatter) {
        this.calculator = Objects.requireNonNull(calculator, "calculator");
        this.formatter = Objects.requireNonNull(formatter, "formatter");
    }

    public String generate(List<DeploymentSample> samples) {
        return formatter.format(calculator.calculate(samples));
    }
}
```

Serwis aplikacyjny składa dwa kroki, ale nie przejmuje ich szczegółów. Dane mają jednego właściciela, a kierunek zależności pozostaje czytelny.

### 3.4. Bezpieczna sekwencja

1. Zidentyfikuj klaster pól i metod o wspólnym powodzie zmiany.
2. Utwórz klasę docelową i przenieś najmniejszy spójny fragment.
3. Zachowaj starą klasę jako delegującą fasadę.
4. Przenoś stan do jednego właściciela, nie utrzymuj dwóch kopii.
5. Migruj klientów pojedynczo.
6. Usuń delegację dopiero po zakończeniu migracji.

### 3.5. Ryzyka

Ekstrakcja może rozdzielić operację, która wcześniej była objęta jedną transakcją albo blokadą. W kodzie zależnym od ORM, serializacji i refleksji przeniesienie pola może zmienić kontrakt mimo identycznego wyniku metod.

## 4. Remove Duplication

### 4.1. Duplikacja wiedzy

Podobny tekst nie zawsze jest duplikacją. Fragmenty powinny zostać połączone wtedy, gdy reprezentują tę samą regułę i powinny zmieniać się razem. Dwie podobne reguły pochodzące z niezależnych kontekstów mogą celowo ewoluować w różnych kierunkach.

W przykładzie publikacja snapshotu i wydania używa tej samej walidacji, normalizacji oraz składania współrzędnych artefaktu. Różnica repozytorium i sufiksu wersji pozostaje jawna.

### 4.2. Stan przed zmianą

Plik `pl/training/module7/duplication/before/LegacyArtifactPublisher.java`:

```java
package pl.training.module7.duplication.before;

import java.util.Locale;
import java.util.Objects;

public final class LegacyArtifactPublisher {
    public String publishSnapshot(String artifactName, int buildNumber) {
        Objects.requireNonNull(artifactName, "artifactName");
        if (artifactName.isBlank()) {
            throw new IllegalArgumentException("artifactName must not be blank");
        }
        if (buildNumber <= 0) {
            throw new IllegalArgumentException(
                    "buildNumber must be greater than zero");
        }

        String normalizedName = artifactName.strip().toLowerCase(Locale.ROOT);
        return normalizedName + ":" + buildNumber + "-SNAPSHOT";
    }

    public String publishRelease(String artifactName, int buildNumber) {
        Objects.requireNonNull(artifactName, "artifactName");
        if (artifactName.isBlank()) {
            throw new IllegalArgumentException("artifactName must not be blank");
        }
        if (buildNumber <= 0) {
            throw new IllegalArgumentException(
                    "buildNumber must be greater than zero");
        }

        String normalizedName = artifactName.strip().toLowerCase(Locale.ROOT);
        return normalizedName + ":" + buildNumber;
    }
}
```

### 4.3. Stan po zmianie

Plik `pl/training/module7/duplication/after/ArtifactPublisher.java`:

```java
package pl.training.module7.duplication.after;

import java.util.Locale;
import java.util.Objects;

public final class ArtifactPublisher {
    public String publishSnapshot(String artifactName, int buildNumber) {
        return publish(artifactName, buildNumber, "-SNAPSHOT");
    }

    public String publishRelease(String artifactName, int buildNumber) {
        return publish(artifactName, buildNumber, "");
    }

    private String publish(
            String artifactName,
            int buildNumber,
            String qualifier) {

        String normalizedName = validateAndNormalize(
                artifactName,
                buildNumber);
        return normalizedName + ":" + buildNumber + qualifier;
    }

    private String validateAndNormalize(
            String artifactName,
            int buildNumber) {

        Objects.requireNonNull(artifactName, "artifactName");
        if (artifactName.isBlank()) {
            throw new IllegalArgumentException("artifactName must not be blank");
        }
        if (buildNumber <= 0) {
            throw new IllegalArgumentException(
                    "buildNumber must be greater than zero");
        }

        return artifactName.strip().toLowerCase(Locale.ROOT);
    }
}
```

Wspólny fragment ma jednego właściciela, ale publiczne metody nadal komunikują dwa zamiary biznesowe.

### 4.4. Bezpieczna sekwencja

1. Niezależnie scharakteryzuj obie implementacje.
2. Porównaj przypadki brzegowe, zaokrąglenia, wyjątki i efekty uboczne.
3. Nadaj fragmentom spójne nazwy i strukturę bez współdzielenia kodu.
4. Wydziel identyczną regułę w jednym miejscu.
5. Skieruj do niej jedną ścieżkę i uruchom testy.
6. Skieruj drugą ścieżkę i ponownie porównaj zachowanie.

Abstrakcja wymagająca wielu flag i wyjątków jest sygnałem, że połączono przypadkowo podobne procesy.

## 5. Break Method

### 5.1. Intencja

Break Method oznacza serię małych ekstrakcji, po których metoda nadrzędna opisuje algorytm na jednym poziomie abstrakcji. Długość wierszy nie jest samodzielnym kryterium. Ważniejsza jest możliwość nazwania spójnych kroków i ograniczenia liczby kontekstów, które czytelnik musi utrzymywać jednocześnie.

### 5.2. Stan przed zmianą

Plik `pl/training/module7/breakmethod/before/LegacyReleaseManifestBuilder.java`:

```java
package pl.training.module7.breakmethod.before;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import pl.training.module7.breakmethod.ManifestEntry;

public final class LegacyReleaseManifestBuilder {
    public String build(List<ManifestEntry> entries) {
        Objects.requireNonNull(entries, "entries");

        var validatedEntries = new ArrayList<ManifestEntry>(entries.size());
        for (ManifestEntry entry : entries) {
            Objects.requireNonNull(entry, "entries must not contain null");
            Objects.requireNonNull(entry.artifact(), "artifact");
            if (entry.artifact().isBlank()) {
                throw new IllegalArgumentException("artifact must not be blank");
            }
            Objects.requireNonNull(entry.checksum(), "checksum");
            if (entry.checksum().isBlank()) {
                throw new IllegalArgumentException("checksum must not be blank");
            }
            if (entry.deploymentOrder() < 0) {
                throw new IllegalArgumentException(
                        "deploymentOrder must not be negative");
            }
            validatedEntries.add(entry);
        }

        validatedEntries.sort(
                Comparator.comparingInt(ManifestEntry::deploymentOrder)
                        .thenComparing(ManifestEntry::artifact));

        var manifest = new StringJoiner("\n");
        for (ManifestEntry entry : validatedEntries) {
            manifest.add(
                    entry.deploymentOrder()
                            + "|"
                            + entry.artifact()
                            + "|"
                            + entry.checksum());
        }
        return manifest.toString();
    }
}
```

### 5.3. Stan po zmianie

Plik `pl/training/module7/breakmethod/after/ReleaseManifestBuilder.java`:

```java
package pl.training.module7.breakmethod.after;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import pl.training.module7.breakmethod.ManifestEntry;

public final class ReleaseManifestBuilder {
    public String build(List<ManifestEntry> entries) {
        List<ManifestEntry> validatedEntries = validateAndCopy(entries);
        List<ManifestEntry> orderedEntries = order(validatedEntries);
        return render(orderedEntries);
    }

    private List<ManifestEntry> validateAndCopy(List<ManifestEntry> entries) {
        Objects.requireNonNull(entries, "entries");

        var copy = new ArrayList<ManifestEntry>(entries.size());
        for (ManifestEntry entry : entries) {
            Objects.requireNonNull(entry, "entries must not contain null");
            Objects.requireNonNull(entry.artifact(), "artifact");
            if (entry.artifact().isBlank()) {
                throw new IllegalArgumentException("artifact must not be blank");
            }
            Objects.requireNonNull(entry.checksum(), "checksum");
            if (entry.checksum().isBlank()) {
                throw new IllegalArgumentException("checksum must not be blank");
            }
            if (entry.deploymentOrder() < 0) {
                throw new IllegalArgumentException(
                        "deploymentOrder must not be negative");
            }
            copy.add(entry);
        }
        return copy;
    }

    private List<ManifestEntry> order(List<ManifestEntry> entries) {
        return entries.stream()
                .sorted(
                        Comparator.comparingInt(ManifestEntry::deploymentOrder)
                                .thenComparing(ManifestEntry::artifact))
                .toList();
    }

    private String render(List<ManifestEntry> entries) {
        var manifest = new StringJoiner("\n");
        for (ManifestEntry entry : entries) {
            manifest.add(
                    entry.deploymentOrder()
                            + "|"
                            + entry.artifact()
                            + "|"
                            + entry.checksum());
        }
        return manifest.toString();
    }
}
```

Metoda publiczna opisuje proces: walidacja i kopia, uporządkowanie, renderowanie. Lokalne dane łatwo przechodzą między krokami, więc Method Object byłby zbędny.

### 5.4. Bezpieczna sekwencja

1. Zacznij od fragmentu z małą liczbą wejść i najwyżej jednym wynikiem.
2. Przenieś go dosłownie, zachowując kolejność instrukcji.
3. Skompiluj i uruchom testy.
4. Nadaj metodzie nazwę opisującą intencję.
5. Powtarzaj dla kolejnych fragmentów.
6. Użyj Split Phase, gdy metoda zawiera następujące po sobie fazy o osobnych modelach danych.
7. Sięgnij po Method Object dopiero wtedy, gdy przepływ lokalnego stanu blokuje dalsze ekstrakcje.

Szczególnej uwagi wymagają `return`, `break`, `continue`, mutowalne kolekcje, aliasy i kod wykonywany wewnątrz blokady.

## 6. Introduce Parameter Object

### 6.1. Data clump jako pojęcie

Parameter Object nie służy do ukrywania dowolnie długiej listy argumentów. Grupa powinna opisywać jedno pojęcie i regularnie występować razem. Nowy typ pozwala nazwać to pojęcie, ograniczyć pomyłki kolejności oraz w dalszym kroku przenieść zachowanie operujące na całej grupie.

### 6.2. Stan przed zmianą

Plik `pl/training/module7/parameterobject/before/LegacyRolloutPlanner.java`:

```java
package pl.training.module7.parameterobject.before;

import java.util.Objects;

public final class LegacyRolloutPlanner {
    private static final long DEPLOYMENT_SECONDS_PER_BATCH = 30;

    public long estimateSeconds(
            String service,
            String region,
            int instances,
            int batchSize,
            int pauseSeconds) {

        validate(service, region, instances, batchSize, pauseSeconds);

        long batches = Math.ceilDiv((long) instances, batchSize);
        long deploymentSeconds = Math.multiplyExact(
                batches,
                DEPLOYMENT_SECONDS_PER_BATCH);
        long pauseTime = Math.multiplyExact(
                batches - 1,
                (long) pauseSeconds);
        return Math.addExact(deploymentSeconds, pauseTime);
    }

    public String describe(
            String service,
            String region,
            int instances,
            int batchSize,
            int pauseSeconds) {

        validate(service, region, instances, batchSize, pauseSeconds);

        return "service=" + service
                + ";region=" + region
                + ";instances=" + instances
                + ";batchSize=" + batchSize
                + ";pauseSeconds=" + pauseSeconds;
    }

    private void validate(
            String service,
            String region,
            int instances,
            int batchSize,
            int pauseSeconds) {

        Objects.requireNonNull(service, "service");
        if (service.isBlank()) {
            throw new IllegalArgumentException("service must not be blank");
        }
        Objects.requireNonNull(region, "region");
        if (region.isBlank()) {
            throw new IllegalArgumentException("region must not be blank");
        }
        if (instances <= 0) {
            throw new IllegalArgumentException(
                    "instances must be greater than zero");
        }
        if (batchSize <= 0) {
            throw new IllegalArgumentException(
                    "batchSize must be greater than zero");
        }
        if (pauseSeconds < 0) {
            throw new IllegalArgumentException(
                    "pauseSeconds must not be negative");
        }
    }
}
```

### 6.3. Stan po zmianie

Plik `pl/training/module7/parameterobject/after/RolloutSpec.java`:

```java
package pl.training.module7.parameterobject.after;

import java.util.Objects;

public record RolloutSpec(
        String service,
        String region,
        int instances,
        int batchSize,
        int pauseSeconds) {

    public RolloutSpec {
        Objects.requireNonNull(service, "service");
        if (service.isBlank()) {
            throw new IllegalArgumentException("service must not be blank");
        }
        Objects.requireNonNull(region, "region");
        if (region.isBlank()) {
            throw new IllegalArgumentException("region must not be blank");
        }
        if (instances <= 0) {
            throw new IllegalArgumentException(
                    "instances must be greater than zero");
        }
        if (batchSize <= 0) {
            throw new IllegalArgumentException(
                    "batchSize must be greater than zero");
        }
        if (pauseSeconds < 0) {
            throw new IllegalArgumentException(
                    "pauseSeconds must not be negative");
        }
    }
}
```

Plik `pl/training/module7/parameterobject/after/RolloutPlanner.java`:

```java
package pl.training.module7.parameterobject.after;

import java.util.Objects;

public final class RolloutPlanner {
    private static final long DEPLOYMENT_SECONDS_PER_BATCH = 30;

    public long estimateSeconds(RolloutSpec spec) {
        Objects.requireNonNull(spec, "spec");

        long batches = Math.ceilDiv(
                (long) spec.instances(),
                spec.batchSize());
        long deploymentSeconds = Math.multiplyExact(
                batches,
                DEPLOYMENT_SECONDS_PER_BATCH);
        long pauseTime = Math.multiplyExact(
                batches - 1,
                (long) spec.pauseSeconds());
        return Math.addExact(deploymentSeconds, pauseTime);
    }

    public String describe(RolloutSpec spec) {
        Objects.requireNonNull(spec, "spec");

        return "service=" + spec.service()
                + ";region=" + spec.region()
                + ";instances=" + spec.instances()
                + ";batchSize=" + spec.batchSize()
                + ";pauseSeconds=" + spec.pauseSeconds();
    }
}
```

Kod `after` pokazuje stan docelowy po świadomym przeniesieniu walidacji do konstruktora rekordu. Powstanie niepoprawnego `RolloutSpec` jest odrzucane wcześniej niż wywołanie planera. Jest to obserwowalna zmiana momentu błędu i dlatego nie należy łączyć jej w jednym kroku z samym grupowaniem parametrów. W przejściowym stanie zachowującym zachowanie rekord przechowywałby wartości bez walidacji, a dotychczasowe kontrole nadal wykonywałby planer.

### 6.4. Migracja API

Bezpieczna migracja publicznego API zwykle przebiega przez przeciążenie:

1. Utwórz typ przechowujący dokładnie dotychczasowe wartości.
2. Dodaj metodę przyjmującą nowy typ.
3. Pozostaw starą sygnaturę jako delegującą.
4. Migruj wywołania pojedynczo.
5. Dopiero później przenoś walidację i zachowanie do obiektu.
6. Usuń starą metodę po zakończeniu okresu zgodności.

Przeniesienie walidacji z wywołania metody do konstruktora zmienia moment wystąpienia wyjątku. `List.copyOf` gwarantuje niemodyfikowalny wynik i odrzuca elementy `null`, ale nie gwarantuje utworzenia nowej instancji. Jego użycie może więc zmienić semantykę mutowalności i aliasowania. Takich modyfikacji nie należy ukrywać w kroku przedstawianym jako czysta refaktoryzacja.

### 6.5. Ryzyka

Obiekt o nazwie `Parameters`, zawierający luźny zestaw pól i kolejne flagi, zwykle tylko maskuje problem. Komponenty publicznego rekordu są częścią jego API. Zmiana ich liczby, kolejności albo typów wymaga takiej samej ostrożności jak zmiana sygnatury metody.

## 7. Remove Arrowhead Antipattern

### 7.1. Problem kodu strzałkowego

Kod strzałkowy powstaje, gdy główna ścieżka zostaje przesunięta przez kolejne poziomy zagnieżdżenia. Guard clauses oddzielają przypadki kończące przetwarzanie i pozostawiają ścieżkę pozytywną na poziomie metody.

Spłaszczenie jest zachowawcze tylko wtedy, gdy zachowuje priorytet warunków. Jeżeli kandydat jednocześnie nie ma akceptacji i ma nieudane testy, wynik musi nadal odpowiadać pierwszemu sprawdzanemu warunkowi.

### 7.2. Stan przed zmianą

Plik `pl/training/module7/arrowhead/before/LegacyDeploymentEligibility.java`:

```java
package pl.training.module7.arrowhead.before;

import pl.training.module7.arrowhead.DeploymentCandidate;
import pl.training.module7.arrowhead.Eligibility;

public final class LegacyDeploymentEligibility {
    public Eligibility evaluate(DeploymentCandidate candidate) {
        Eligibility result;
        if (candidate != null) {
            if (candidate.releaseId() != null
                    && !candidate.releaseId().isBlank()) {
                if (candidate.approved()) {
                    if (candidate.testsPassed()) {
                        if (candidate.windowOpen()) {
                            result = Eligibility.ELIGIBLE;
                        } else {
                            result = Eligibility.WINDOW_CLOSED;
                        }
                    } else {
                        result = Eligibility.TESTS_FAILED;
                    }
                } else {
                    result = Eligibility.NOT_APPROVED;
                }
            } else {
                result = Eligibility.INVALID_RELEASE_ID;
            }
        } else {
            result = Eligibility.MISSING_CANDIDATE;
        }
        return result;
    }
}
```

### 7.3. Stan po zmianie

Plik `pl/training/module7/arrowhead/after/DeploymentEligibility.java`:

```java
package pl.training.module7.arrowhead.after;

import pl.training.module7.arrowhead.DeploymentCandidate;
import pl.training.module7.arrowhead.Eligibility;

public final class DeploymentEligibility {
    public Eligibility evaluate(DeploymentCandidate candidate) {
        if (candidate == null) {
            return Eligibility.MISSING_CANDIDATE;
        }
        if (candidate.releaseId() == null
                || candidate.releaseId().isBlank()) {
            return Eligibility.INVALID_RELEASE_ID;
        }
        if (!candidate.approved()) {
            return Eligibility.NOT_APPROVED;
        }
        if (!candidate.testsPassed()) {
            return Eligibility.TESTS_FAILED;
        }
        if (!candidate.windowOpen()) {
            return Eligibility.WINDOW_CLOSED;
        }
        return Eligibility.ELIGIBLE;
    }
}
```

### 7.4. Bezpieczna sekwencja

1. Zbuduj macierz gałęzi, w tym kombinacje kilku niespełnionych warunków.
2. Odwróć wyłącznie najbardziej zewnętrzny warunek.
3. Zwróć albo zgłoś dokładnie ten sam rezultat.
4. Usuń odpowiadający mu `else`.
5. Uruchom testy i powtarzaj po jednym poziomie.
6. Łącz warunki dopiero po potwierdzeniu braku efektów ubocznych.

Nie wolno pominąć logowania, aktualizacji stanu ani zwalniania zasobów. Zasoby należy wcześniej zabezpieczyć przez try-with-resources lub `finally`.

## 8. Introduce Design by Contract Checks

### 8.1. Trzy części kontraktu

Design by Contract rozróżnia:

- precondition, czyli obowiązek klienta przed wywołaniem,
- postcondition, czyli gwarancję operacji po poprawnym zakończeniu,
- invariant, czyli właściwość poprawnego obiektu zachowaną pomiędzy operacjami publicznymi.

Podtyp nie powinien wzmacniać warunków wstępnych ani osłabiać warunków końcowych względem nadtypu. W przeciwnym razie klient legalnie korzystający z kontraktu bazowego nie może bezpiecznie użyć podtypu.

### 8.2. Jawne kontrole

Plik `pl/training/module7/contract/after/Contracts.java`:

```java
package pl.training.module7.contract.after;

public final class Contracts {
    private Contracts() {
    }

    public static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void ensure(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    public static void invariant(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
```

Plik `pl/training/module7/contract/after/DeploymentCapacity.java`:

```java
package pl.training.module7.contract.after;

public final class DeploymentCapacity {
    private final int totalSlots;
    private int remaining;

    public DeploymentCapacity(int totalSlots) {
        Contracts.require(
                totalSlots >= 0,
                "totalSlots must not be negative");
        this.totalSlots = totalSlots;
        remaining = totalSlots;
        checkInvariant();
        Contracts.ensure(
                remaining == totalSlots,
                "initial capacity must equal totalSlots");
    }

    public void reserve(int slots) {
        Contracts.require(slots > 0, "slots must be positive");
        Contracts.require(
                slots <= remaining,
                "cannot reserve more slots than remain");

        int previousRemaining = remaining;
        int nextRemaining = previousRemaining - slots;

        checkInvariant(nextRemaining);
        remaining = nextRemaining;
        Contracts.ensure(
                remaining == previousRemaining - slots,
                "reserve must reduce remaining capacity by slots");
        checkInvariant();
    }

    public void release(int slots) {
        Contracts.require(slots > 0, "slots must be positive");
        Contracts.require(
                slots <= totalSlots - remaining,
                "cannot release more slots than are reserved");

        int previousRemaining = remaining;
        int nextRemaining = previousRemaining + slots;

        checkInvariant(nextRemaining);
        remaining = nextRemaining;
        Contracts.ensure(
                remaining == previousRemaining + slots,
                "release must increase remaining capacity by slots");
        checkInvariant();
    }

    public int remaining() {
        checkInvariant();
        return remaining;
    }

    private void checkInvariant() {
        checkInvariant(remaining);
    }

    private void checkInvariant(int candidateRemaining) {
        Contracts.invariant(
                candidateRemaining >= 0 && candidateRemaining <= totalSlots,
                "remaining capacity must be between zero and totalSlots");
    }
}
```

Warunki wstępne są sprawdzane przed pierwszą mutacją. Następny stan jest najpierw obliczany i sprawdzany względem niezmiennika, a dopiero potem przypisywany. Warunek końcowy odwołuje się już do opublikowanego pola `remaining`, więc rzeczywiście opisuje stan po operacji. Naruszenie precondition nie zmienia obiektu. Naruszenie postcondition albo invariant oznacza błąd implementacji i wymaga świadomej strategii odzyskania stanu, jeżeli operacja obejmuje bardziej złożone mutacje.

### 8.3. To nie zawsze jest refaktoryzacja

Wariant legacy może akceptować wartości spoza zamierzonej domeny, przechodzić w niespójny stan albo zgłaszać przypadkowy wyjątek później. Dodanie jawnego odrzucenia zmienia zachowanie. Testy przykładu celowo rozdzielają:

- równoważność dla poprawnych sekwencji operacji,
- nowy kontrakt dla niepoprawnych danych.

### 8.4. `assert` a publiczne API

Instrukcja `assert` jest domyślnie wyłączona, działa dopiero po uruchomieniu JVM z flagą `-ea`, a jej naruszenie zgłasza `AssertionError`. Nie należy używać jej do sprawdzania argumentów metod publicznych. Typowe rozróżnienie w API Javy to:

- `NullPointerException` dla wymaganego argumentu referencyjnego o wartości `null`,
- `IllegalArgumentException` dla niedozwolonej wartości argumentu,
- `IllegalStateException` dla operacji niedozwolonej w bieżącym stanie odbiorcy.

W istniejącym systemie ważniejszy może być historyczny typ wyjątku. Nie należy go mechanicznie zastępować bez decyzji o zmianie kontraktu.

## 9. Remove Double Negative

### 9.1. Intencja

Negowanie negatywnego predykatu zwiększa obciążenie poznawcze i sprzyja pomyłkom podczas rozbudowy warunku. Pozytywna nazwa musi jednak oznaczać dokładne logiczne dopełnienie starej właściwości.

### 9.2. Stan przed zmianą

Plik `pl/training/module7/doublenegative/before/LegacyReleaseReadiness.java`:

```java
package pl.training.module7.doublenegative.before;

public record LegacyReleaseReadiness(
        boolean notApproved,
        boolean testsNotPassed,
        boolean windowNotOpen) {
}
```

Plik `pl/training/module7/doublenegative/before/LegacyReleaseGate.java`:

```java
package pl.training.module7.doublenegative.before;

import java.util.Objects;

public final class LegacyReleaseGate {
    public boolean canRelease(LegacyReleaseReadiness readiness) {
        Objects.requireNonNull(readiness, "readiness");
        return !readiness.notApproved()
                && !readiness.testsNotPassed()
                && !readiness.windowNotOpen();
    }
}
```

### 9.3. Stan po zmianie

Plik `pl/training/module7/doublenegative/after/ReleaseReadiness.java`:

```java
package pl.training.module7.doublenegative.after;

public record ReleaseReadiness(
        boolean approved,
        boolean testsPassed,
        boolean windowOpen) {
}
```

Plik `pl/training/module7/doublenegative/after/ReleaseGate.java`:

```java
package pl.training.module7.doublenegative.after;

import java.util.Objects;

public final class ReleaseGate {
    public boolean canRelease(ReleaseReadiness readiness) {
        Objects.requireNonNull(readiness, "readiness");
        return readiness.approved()
                && readiness.testsPassed()
                && readiness.windowOpen();
    }
}
```

### 9.4. Bezpieczna sekwencja

1. Dodaj predykat pozytywny delegujący do negatywnego.
2. Migruj po jednym użyciu i kompiluj po każdym kroku.
3. Przenieś właściwą implementację do pozytywnego predykatu.
4. Pozostaw negatywny predykat jako delegujący, jeżeli nadal należy do publicznego API.
5. Usuń go dopiero po migracji klientów.

`Boolean` dopuszczający `null` nie jest modelem dwustanowym. `!disabled` może zgłosić `NullPointerException` podczas unboxingu, natomiast `!Boolean.TRUE.equals(disabled)` interpretuje `null` jako wartość inną niż `true`. Są to różne kontrakty.

Jeżeli negatywna właściwość jest nazwą pola JSON, kolumny bazy danych, klucza konfiguracji albo elementem publicznego API, zmiana modelu wymaga migracji granicy systemu. Samo odwrócenie wartości wewnątrz aplikacji nie migruje istniejących danych ani klientów.

## 10. Remove God Classes

### 10.1. Kampania, nie pojedynczy ruch

God Class skupia niezależne reguły, dane i integracje, przez co większość zmian przechodzi przez jedno miejsce. Nie rozpoznaje się jej wyłącznie po liczbie wierszy. Ważniejsze są niska spójność, wiele powodów zmiany, szeroki dostęp do stanu i rola centralnego węzła zależności.

Pełne przepisanie klasy w jednym kroku ma wysokie ryzyko. Bezpieczniejsza jest kampania małych Extract Class, Move Function i Move Field prowadzona w obszarze aktualnie wymaganej zmiany.

### 10.2. Stan przed zmianą

Plik `pl/training/module7/godclass/before/LegacyReleaseManager.java`:

```java
package pl.training.module7.godclass.before;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pl.training.module7.godclass.PublishedRelease;

public final class LegacyReleaseManager {
    private final Map<String, PublishedRelease> releases =
            new LinkedHashMap<>();
    private final List<String> auditEntries = new ArrayList<>();
    private final List<String> notifications = new ArrayList<>();
    private final List<String> events = new ArrayList<>();

    public PublishedRelease publish(
            String releaseId,
            String service,
            String version) {
        requireText(releaseId, "releaseId");
        requireText(service, "service");
        requireText(version, "version");
        if (releases.containsKey(releaseId)) {
            throw new IllegalStateException(
                    "release already published: " + releaseId);
        }

        var release = new PublishedRelease(releaseId, service, version);
        releases.put(releaseId, release);
        events.add("save:" + releaseId);
        auditEntries.add("published:" + releaseId);
        events.add("audit:" + releaseId);
        notifications.add("release-published:" + releaseId);
        events.add("notify:" + releaseId);
        return release;
    }

    public List<PublishedRelease> releases() {
        return List.copyOf(releases.values());
    }

    public List<String> auditEntries() {
        return List.copyOf(auditEntries);
    }

    public List<String> notifications() {
        return List.copyOf(notifications);
    }

    public List<String> events() {
        return List.copyOf(events);
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
    }
}
```

### 10.3. Stan po wydzieleniu pionowego fragmentu

Plik `pl/training/module7/godclass/after/ReleaseApplicationService.java`:

```java
package pl.training.module7.godclass.after;

import java.util.Objects;

import pl.training.module7.godclass.PublishedRelease;

public final class ReleaseApplicationService {
    private final ReleaseValidator validator;
    private final ReleaseRepository repository;
    private final AuditTrail auditTrail;
    private final ReleaseNotifier notifier;

    public ReleaseApplicationService(
            ReleaseValidator validator,
            ReleaseRepository repository,
            AuditTrail auditTrail,
            ReleaseNotifier notifier) {
        this.validator = Objects.requireNonNull(validator, "validator");
        this.repository = Objects.requireNonNull(repository, "repository");
        this.auditTrail = Objects.requireNonNull(auditTrail, "auditTrail");
        this.notifier = Objects.requireNonNull(notifier, "notifier");
    }

    public PublishedRelease publish(
            String releaseId,
            String service,
            String version) {
        PublishedRelease release = validator.validate(
                releaseId, service, version);
        if (repository.existsById(release.releaseId())) {
            throw new IllegalStateException(
                    "release already published: " + release.releaseId());
        }

        repository.save(release);
        auditTrail.recordPublished(release);
        notifier.notifyPublished(release);
        return release;
    }
}
```

Plik `pl/training/module7/godclass/after/ReleaseRepository.java`:

```java
package pl.training.module7.godclass.after;

import java.util.List;

import pl.training.module7.godclass.PublishedRelease;

public interface ReleaseRepository {
    boolean existsById(String releaseId);

    void save(PublishedRelease release);

    List<PublishedRelease> findAll();
}
```

Plik `pl/training/module7/godclass/after/InMemoryReleaseRepository.java`:

```java
package pl.training.module7.godclass.after;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import pl.training.module7.godclass.PublishedRelease;

public final class InMemoryReleaseRepository implements ReleaseRepository {
    private final Map<String, PublishedRelease> releases =
            new LinkedHashMap<>();
    private final Consumer<String> eventSink;

    public InMemoryReleaseRepository() {
        this(ignored -> { });
    }

    public InMemoryReleaseRepository(Consumer<String> eventSink) {
        this.eventSink = Objects.requireNonNull(eventSink, "eventSink");
    }

    @Override
    public boolean existsById(String releaseId) {
        return releases.containsKey(releaseId);
    }

    @Override
    public void save(PublishedRelease release) {
        Objects.requireNonNull(release, "release");
        if (releases.containsKey(release.releaseId())) {
            throw new IllegalStateException(
                    "release already published: " + release.releaseId());
        }
        releases.put(release.releaseId(), release);
        eventSink.accept("save:" + release.releaseId());
    }

    @Override
    public List<PublishedRelease> findAll() {
        return List.copyOf(releases.values());
    }
}
```

Plik `pl/training/module7/godclass/after/ReleaseValidator.java`:

```java
package pl.training.module7.godclass.after;

import pl.training.module7.godclass.PublishedRelease;

public final class ReleaseValidator {
    public PublishedRelease validate(
            String releaseId,
            String service,
            String version) {
        requireText(releaseId, "releaseId");
        requireText(service, "service");
        requireText(version, "version");
        return new PublishedRelease(releaseId, service, version);
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
    }
}
```

Serwis aplikacyjny zachowuje koordynację procesu. Walidator, repozytorium, audyt i powiadomienia mają osobne kontrakty. Nie oznacza to, że każdy jednolinijkowy krok wymaga osobnej klasy. Granice wynikają z odpowiedzialności i kierunku zmian.

Przykład świadomie nie zapewnia transakcji obejmującej `save`, `audit` i `notify`. Testy definiują stan po awarii każdego kroku: awaria zatrzymuje sekwencję, ale nie wycofuje zakończonych wcześniej efektów. W systemie produkcyjnym wymóg atomowości może prowadzić do transakcji, wzorca transactional outbox, idempotentnych ponowień albo jawnej kompensacji. Takiej decyzji nie należy ukrywać wewnątrz ruchu klas.

### 10.4. Bezpieczna sekwencja

1. Zbuduj mapę metod, pól i zewnętrznych efektów.
2. Wybierz jedną odpowiedzialność związaną z aktualną zmianą.
3. Wyodrębnij najmniejszy pionowy fragment.
4. Zachowaj dotychczasową klasę jako zgodną fasadę.
5. Przenoś najpierw metody położone na końcu grafu wywołań.
6. Utrzymuj jednego właściciela stanu i jednokierunkowe zależności.
7. Kompiluj i testuj po każdym ruchu.
8. Powtarzaj tylko tak długo, jak przynosi to wartość dla planowanych zmian.

### 10.5. Ryzyka

Podział może zmienić transakcję, kolejność `save`, `audit` i `notify`, semantykę blokad, tożsamość encji, lazy loading, serializację lub konfigurację kontenera DI. Sieć małych klas o niskiej spójności nie jest automatycznie lepsza od jednej większej klasy.

## 11. Remove Boolean Method Parameters

### 11.1. Flaga sterująca a dane

Flag argument pozwala wywołującemu wybrać wariant algorytmu, często za pomocą literału `true` lub `false`. Taki zapis nie komunikuje intencji w miejscu wywołania. Nie każdy parametr `boolean` jest jednak flagą. Wartość odczytana z formularza albo komunikatu może być zwykłą daną domenową.

### 11.2. Stan przed zmianą

Plik `pl/training/module7/booleanparameter/before/LegacyDeploymentExecutor.java`:

```java
package pl.training.module7.booleanparameter.before;

public final class LegacyDeploymentExecutor {
    public String execute(String deploymentId, boolean dryRun) {
        validate(deploymentId);

        if (dryRun) {
            return "preview:" + deploymentId;
        }
        return "deployed:" + deploymentId;
    }

    private static void validate(String deploymentId) {
        if (deploymentId == null || deploymentId.isBlank()) {
            throw new IllegalArgumentException("deploymentId must not be blank");
        }
    }
}
```

### 11.3. Stan po zmianie

Plik `pl/training/module7/booleanparameter/after/DeploymentExecutor.java`:

```java
package pl.training.module7.booleanparameter.after;

public final class DeploymentExecutor {
    public String preview(String deploymentId) {
        return execute(deploymentId, ExecutionMode.PREVIEW);
    }

    public String deploy(String deploymentId) {
        return execute(deploymentId, ExecutionMode.DEPLOY);
    }

    private String execute(String deploymentId, ExecutionMode mode) {
        validate(deploymentId);
        return mode.resultPrefix() + deploymentId;
    }

    private static void validate(String deploymentId) {
        if (deploymentId == null || deploymentId.isBlank()) {
            throw new IllegalArgumentException("deploymentId must not be blank");
        }
    }

    private enum ExecutionMode {
        PREVIEW("preview:"),
        DEPLOY("deployed:");

        private final String resultPrefix;

        ExecutionMode(String resultPrefix) {
            this.resultPrefix = resultPrefix;
        }

        String resultPrefix() {
            return resultPrefix;
        }
    }
}
```

Metody `preview` i `deploy` ujawniają zamiar klienta. Wspólna prywatna implementacja może początkowo przyjmować nazwany tryb. Celem nie jest mechaniczne zdublowanie całego algorytmu.

### 11.4. Migracja

1. Dodaj jawne metody dla znaczących wariantów.
2. Skieruj je do istniejącej implementacji.
3. Migruj klientów przekazujących literały.
4. Dla publicznej biblioteki pozostaw czasowo starą metodę delegującą.
5. Rozplącz prywatny algorytm dopiero w osobnych krokach.
6. Usuń starą sygnaturę po zakończeniu migracji.

Wiele flag tworzy potencjalnie wykładniczą liczbę kombinacji. Zamiast generować zestaw metod dla każdej kombinacji należy ponownie zbadać odpowiedzialności i rozważyć obiekt polityki.

## 12. Remove Middle Man

### 12.1. Kiedy delegacja jest zbędna

Middle Man przekazuje wywołania bez dodawania polityki, stabilnej granicy ani ukrycia szczegółów. Jeśli większość API klasy jest przezroczystą kopią API współpracownika, klient może zależeć bezpośrednio od właściwego kontraktu.

### 12.2. Stan przed zmianą

Plik `pl/training/module7/middleman/before/ReleaseService.java`:

```java
package pl.training.module7.middleman.before;

import java.util.Objects;

import pl.training.module7.middleman.DeploymentStatus;

public final class ReleaseService {
    private final DeploymentRegistry registry;

    public ReleaseService(DeploymentRegistry registry) {
        this.registry = Objects.requireNonNull(
                registry, "registry must not be null");
    }

    public void update(String deploymentId, DeploymentStatus status) {
        registry.update(deploymentId, status);
    }

    public DeploymentStatus statusOf(String deploymentId) {
        return registry.statusOf(deploymentId);
    }
}
```

Plik `pl/training/module7/middleman/before/ReleaseDashboard.java`:

```java
package pl.training.module7.middleman.before;

import java.util.Objects;

public final class ReleaseDashboard {
    private final ReleaseService releaseService;

    public ReleaseDashboard(ReleaseService releaseService) {
        this.releaseService = Objects.requireNonNull(
                releaseService, "releaseService must not be null");
    }

    public String render(String deploymentId) {
        return deploymentId + " -> " + releaseService.statusOf(deploymentId);
    }
}
```

### 12.3. Stan po zmianie

Plik `pl/training/module7/middleman/after/ReleaseDashboard.java`:

```java
package pl.training.module7.middleman.after;

import java.util.Objects;

public final class ReleaseDashboard {
    private final DeploymentRegistry registry;

    public ReleaseDashboard(DeploymentRegistry registry) {
        this.registry = Objects.requireNonNull(
                registry, "registry must not be null");
    }

    public String render(String deploymentId) {
        return deploymentId + " -> " + registry.statusOf(deploymentId);
    }
}
```

### 12.4. Bezpieczna sekwencja

1. Potwierdź, że delegacja nie realizuje autoryzacji, transakcji, telemetryki, retry ani translacji błędów.
2. Udostępnij klientowi właściwy kontrakt albo wstrzyknij go bezpośrednio.
3. Migruj jednego klienta naraz.
4. Pozostaw stare metody jako forwardery podczas okresu zgodności.
5. Usuń pośrednika po potwierdzeniu braku wywołań.

Fasada modułu, adapter biblioteki zewnętrznej i seam testowy mogą być wartościowe nawet wtedy, gdy ich bieżąca implementacja zawiera jedną linię. Remove Middle Man jest odwrotnością Hide Delegate. Wybór zależy od tego, która granica lepiej chroni klientów przed zmianą.

## 13. Return ASAP

### 13.1. Intencja

Wcześniejszy zwrot usuwa zbędną zmienną wyniku albo kończy przetwarzanie, gdy wynik jest już ostateczny. Nie jest celem samym w sobie i nie należy optymalizować kodu pod minimalną liczbę instrukcji `return`.

### 13.2. Stan przed zmianą

Plik `pl/training/module7/returnasap/before/LegacyArtifactFinder.java`:

```java
package pl.training.module7.returnasap.before;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import pl.training.module7.returnasap.Artifact;

public final class LegacyArtifactFinder {
    public Optional<Artifact> findByChecksum(
            List<Artifact> artifacts, String checksum) {
        Objects.requireNonNull(artifacts, "artifacts must not be null");
        Objects.requireNonNull(checksum, "checksum must not be null");

        Artifact result = null;
        int index = 0;
        while (result == null && index < artifacts.size()) {
            Artifact artifact = Objects.requireNonNull(
                    artifacts.get(index), "artifact must not be null");
            if (checksum.equals(artifact.checksum())) {
                result = artifact;
            }
            index++;
        }

        return Optional.ofNullable(result);
    }
}
```

### 13.3. Stan po zmianie

Plik `pl/training/module7/returnasap/after/ArtifactFinder.java`:

```java
package pl.training.module7.returnasap.after;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import pl.training.module7.returnasap.Artifact;

public final class ArtifactFinder {
    public Optional<Artifact> findByChecksum(
            List<Artifact> artifacts, String checksum) {
        Objects.requireNonNull(artifacts, "artifacts must not be null");
        Objects.requireNonNull(checksum, "checksum must not be null");

        for (int index = 0; index < artifacts.size(); index++) {
            Artifact artifact = Objects.requireNonNull(
                    artifacts.get(index), "artifact must not be null");
            if (checksum.equals(artifact.checksum())) {
                return Optional.of(artifact);
            }
        }

        return Optional.empty();
    }
}
```

Oba warianty zatrzymują iterację po pierwszym dopasowaniu i zwracają tę samą instancję. Wariant `after` celowo zachowuje indeksowy dostęp przez `size()` i `get(index)`. Zamiana na iterator byłaby osobnym krokiem, ponieważ niestandardowa, leniwa albo instrumentowana implementacja `List` może ujawnić inną liczbę wywołań i inne wyjątki. Test chroni ślad dostępu oraz zachowanie elementu `null` występującego przed i po znalezionym artefakcie.

### 13.4. Bezpieczna sekwencja

1. Scharakteryzuj wynik i efekty każdej ścieżki.
2. Zabezpiecz zwalnianie zasobów za pomocą try-with-resources albo `finally`.
3. Wprowadź wcześniejszy zwrot dokładnie tam, gdzie wynik staje się ostateczny.
4. Zachowaj wszystkie wcześniejsze mutacje należące do kontraktu.
5. Usuń odpowiadający `else` lub zmienną sterującą.
6. Usuń zmienną objaśniającą tylko wtedy, gdy jej nazwa nie wnosi istotnego pojęcia.

`return` wewnątrz `try` nadal uruchamia `finally`. Nagłe zakończenie samego `finally` może zastąpić wcześniejszy wynik albo wyjątek. Przenoszenie instrukcji między tymi blokami wymaga osobnych testów.

## Warsztat 1. Utworzenie seam przed zmianą integracji

### Cel

Przygotować usługę, która sama tworzy klienta zewnętrznego, do testowanej zmiany reguły biznesowej.

### Zadanie

1. Dodaj testy charakterystyki wyniku oraz kolejności wywołań.
2. Zidentyfikuj najmniejszą operację potrzebną od klienta.
3. Wprowadź punkt podstawienia bez zmiany produkcyjnego składania.
4. Dodaj obiekt typu spy rejestrujący argumenty.
5. Dopiero potem zaimplementuj nową regułę.

### Kryteria akceptacji

- produkcyjna implementacja jest nadal wybierana w głównym miejscu składania zależności, czyli composition root,
- test nie uruchamia infrastruktury,
- nowy kontrakt nie zawiera metod nieużywanych przez usługę,
- liczba i kolejność wywołań są jawnie sprawdzone.

## Warsztat 2. Dekompozycja długiej metody

### Cel

Wybrać między Extract Method, Split Phase i Extract Method Object na podstawie przepływu danych.

### Zadanie

1. Narysuj wejścia, wyjścia i mutowane lokalne wartości kolejnych fragmentów.
2. Wydziel najpierw krok z najmniejszą liczbą zależności.
3. Utrzymaj metodę nadrzędną na jednym poziomie abstrakcji.
4. Jeżeli listy argumentów zaczynają rosnąć, oceń Method Object.
5. Porównaj wynik, wyjątki i brak modyfikacji wejściowych kolekcji.

### Kryteria akceptacji

- nazwy metod opisują intencję, nie mechanikę,
- kolejność kroków pozostaje jawna,
- obiekt metody, jeśli powstał, nie jest współdzielony między wywołaniami,
- refaktoryzacja nie dodaje nowej walidacji.

## Warsztat 3. Stopniowe rozbijanie God Class

### Cel

Wydzielić jedną odpowiedzialność bez przepisywania centralnej klasy i bez zmiany kolejności efektów.

### Zadanie

1. Zbuduj mapę metod do pól i integracji.
2. Wskaż jeden klaster o samodzielnym powodzie zmiany.
3. Dodaj test śladu operacji dla sukcesu i awarii.
4. Wydziel najmniejszy pionowy fragment.
5. Pozostaw stare API jako delegujące.
6. Oceń granicę transakcji i własność stanu.

### Kryteria akceptacji

- stan ma jednego właściciela,
- nie powstał cykl zależności,
- walidacja zachowuje kolejność oraz komunikaty,
- po błędzie nie wykonują się późniejsze kroki, a wcześniejsze efekty odpowiadają scharakteryzowanemu kontraktowi,
- kolejne kroki kampanii można wdrażać niezależnie.

## Lista kontrolna przeglądu

### Zachowanie

- Czy testy obejmują wszystkie gałęzie i priorytet nakładających się warunków?
- Czy zachowano typ, komunikat i moment wyjątków należących do kontraktu?
- Czy stan po nieudanej operacji jest identyczny?
- Czy liczba, kolejność i argumenty efektów ubocznych są chronione?
- Czy wcześniejszy `return` nie omija zwalniania zasobów ani wymaganej mutacji?

### Projekt

- Czy nowy seam opisuje potrzebę klienta, a nie całe API dostawcy?
- Czy Parameter Object reprezentuje pojęcie, a nie worek ustawień?
- Czy usunięto duplikację wiedzy, a nie przypadkowe podobieństwo?
- Czy po ekstrakcji istnieje jeden właściciel każdego stanu?
- Czy pośrednik rzeczywiście nie chronił wartościowej granicy?

### Migracja

- Czy nowe i stare API mogą czasowo współistnieć?
- Czy istnieją klienci dostarczani jako już skompilowane pliki `.class`?
- Czy zmiana rekordu, serializacji albo trwałych nazw wymaga migracji danych?
- Czy zaostrzenie kontraktu jest oddzielone od ruchu strukturalnego?
- Czy usunięcie elementów przejściowych ma jawne kryterium zakończenia?

## Podsumowanie

Zaawansowana refaktoryzacja nie polega na wykonywaniu większych skoków. Polega na rozkładaniu trudnego problemu na mniejsze ruchy z bogatszą obserwacją zachowania. Im szersza odpowiedzialność klasy, bardziej ukryta zależność albo bardziej publiczne API, tym ważniejsze są małe kroki, testy różnicowe i świadoma migracja.

Najważniejsze rozróżnienia z modułu:

- seam umożliwia zmianę zależności, ale nie wymaga interfejsu dla każdej klasy,
- Extract Method jest pierwszym wyborem, a Method Object rozwiązaniem dla trudnego przepływu lokalnego stanu,
- duplikacja i duża klasa są diagnozami wymagającymi analizy wiedzy oraz powodów zmiany,
- guard clauses i wcześniejsze zwroty są bezpieczne tylko przy zachowaniu kolejności oraz efektów,
- publiczne kontrakty wymagają jawnych kontroli niezależnych od `assert`,
- flaga, pośrednik i negatywna nazwa powinny być usuwane na podstawie semantyki, nie mechanicznej reguły,
- zgodność strukturalna, źródłowa, binarna i behawioralna są różnymi własnościami.
