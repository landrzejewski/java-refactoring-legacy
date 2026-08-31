# Moduł 8. Strategie i dobre praktyki

## Zakres

1. Stopniowa refaktoryzacja kontra przepisanie systemu
2. Zasada Boy Scout
3. Praca zespołowa w refaktoryzacji
4. Dokumentowanie zmian
5. Narzędzia wspierające refaktoryzację
6. Zarządzanie ryzykiem

### Obserwowalne zachowanie

Zgodność nie kończy się na wartości zwracanej. W zależności od kontraktu obejmuje także:

- typ, komunikat i moment wyjątku,
- skutki uboczne oraz ich kolejność,
- stan trwały i właściwości transakcyjne,
- idempotencję i kolejność komunikatów,
- zgodność źródłową, binarną i protokołową,
- opóźnienie, przepustowość i zużycie zasobów, jeśli są częścią oczekiwań klientów,
- telemetrię, audyt i wymagania bezpieczeństwa.

Nie każdy przypadkowy szczegół starego systemu powinien zostać utrwalony. Zespół powinien jednak świadomie zdecydować, co zachowuje, co naprawia, a co usuwa.

### Krótka pętla informacji zwrotnej

Podstawowy cykl pracy wygląda następująco:

1. Określ cel oraz granicę zmiany.
2. Zapisz istotny kontrakt i stan bazowy.
3. Dodaj brakujące testy charakterystyki lub kontraktowe.
4. Wykonaj jeden mały, odwracalny krok.
5. Skompiluj, uruchom zawężone testy i obejrzyj różnicę zmian, czyli diff.
6. Włącz szerszą weryfikację oraz analizę statyczną.
7. Zintegruj zmianę z główną linią kodu.
8. Obserwuj zachowanie po wdrożeniu.
9. Usuń elementy przejściowe po spełnieniu jawnych kryteriów.

Mały krok nie oznacza arbitralnego limitu linii. Powinien reprezentować jedną spójną intencję, zawierać potrzebne testy i pozostawiać system w działającym stanie.

## 1. Stopniowa refaktoryzacja kontra przepisanie systemu

### 1.1. Domyślny kierunek

Dla dużego systemu obsługującego rzeczywisty ruch bezpieczniejszym punktem wyjścia jest zwykle praca przyrostowa. Dostarcza wcześniejszych informacji o nieznanych regułach, integracjach i charakterystyce operacyjnej. Pozwala też przerwać lub zmienić kierunek przed wydaniem całego budżetu.

Pełne przepisanie nie usuwa złożoności domeny. Przenosi ją do nowego kodu, często bez dostępu do wiedzy zakodowanej w wyjątkach, danych i wieloletnich integracjach. W czasie budowy stary system nadal się zmienia, więc cel migracji pozostaje ruchomy.

Nie wynika z tego, że każde przepisanie jest błędem. Ograniczone przepisanie komponentu jest racjonalnym kandydatem, gdy zakres jest mały, kontrakt znany, integracje nieliczne, stan nie występuje albo ma zaprojektowaną migrację, a starą i nową wersję można niezależnie zweryfikować.

### 1.2. Kryteria decyzji

| Pytanie | Sygnał za zmianą przyrostową | Sygnał za ograniczonym przepisaniem komponentu |
| --- | --- | --- |
| Czy zachowanie jest dobrze poznane? | liczne reguły niejawne i słabe testy | kompletny, mierzalny kontrakt |
| Czy zakres jest izolowany? | wiele integracji i wspólnych danych | wąska granica oraz niewielu klientów |
| Czy komponent przechowuje stan? | skomplikowana migracja i silna spójność | brak stanu albo sprawdzony plan migracji |
| Czy system musi działać stale? | ruch krytyczny i brak okna serwisowego | proste, odwracalne przełączenie |
| Czy cel się zmienia? | aktywny rozwój starego systemu | stabilny zakres i kryteria akceptacji |
| Czy można współistnieć? | dostępny router, adapter albo punkt podstawienia, czyli seam | współistnienie zbędne ze względu na mały zakres |
| Jaka jest motywacja? | redukcja konkretnego ryzyka krok po kroku | mierzalne ograniczenie, którego stara podstawa nie może spełnić |

Nowa technologia sama w sobie nie jest wynikiem biznesowym. Decyzję powinny uzasadniać mierzalne ograniczenia, na przykład brak wsparcia, wymaganie bezpieczeństwa, koszt operacyjny, niezawodność albo czas wprowadzania konkretnych zmian.

### 1.3. Branch by Abstraction i Strangler Fig

Branch by Abstraction wprowadza wewnątrz aplikacji stabilny kontrakt pomiędzy klientem a wymienianym dostawcą. Nazwa nie oznacza gałęzi w systemie kontroli wersji. Typowa sekwencja obejmuje:

1. opisanie potrzeb klienta,
2. wprowadzenie małej abstrakcji,
3. umieszczenie starej implementacji za adapterem,
4. skierowanie istniejących wywołań przez abstrakcję,
5. dodanie wspólnych testów kontraktowych,
6. zbudowanie implementacji kandydującej,
7. porównanie wyników,
8. stopniowe przełączenie klientów,
9. usunięcie starej ścieżki i zbędnego rusztowania.

Kroki wprowadzające abstrakcję i adapter mogą być refaktoryzacją, jeśli zachowują zachowanie. Budowa nowego dostawcy i przełączenie na niego są migracją.

Strangler Fig działa zwykle na granicy systemu albo funkcji biznesowej. Brama, fasada, proxy lub router kieruje część operacji do systemu legacy, a część do nowego komponentu. Branch by Abstraction wymienia dostawcę za kontraktem wewnątrz aplikacji, natomiast Strangler Fig przenosi funkcje między większymi granicami wykonawczymi.

### 1.4. Architektura przejściowa

Adapter, router, translator, flaga i dodatkowa telemetria mogą być uzasadnionym kosztem zmniejszenia ryzyka. Każdy taki element powinien mieć:

- właściciela,
- cel i zakres,
- testy oraz monitoring,
- kryterium zakończenia,
- warunki bezpiecznego usunięcia,
- termin ponownej oceny.

Bez tych informacji rusztowanie migracyjne łatwo staje się kolejną trwałą warstwą legacy. Sam procent ruchu przeniesionego do nowej ścieżki nie kończy migracji. Potrzebne jest jeszcze usunięcie starych zależności, danych, kosztów operacyjnych i przełączników.

### 1.5. Przykład: równoległa weryfikacja kalkulatora

Przykład dotyczy czystego obliczenia ceny. `PriceRequest` normalizuje pieniądze i stopę rabatu zgodnie z jawną polityką `HALF_EVEN`. Walidacja następuje przed zaokrągleniem, więc niewielka wartość ujemna nie może zostać przypadkowo zamieniona na zero.

Plik `pl/training/module8/incremental/PriceRequest.java`:

```java
package pl.training.module8.incremental;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record PriceRequest(
        BigDecimal unitPrice,
        int quantity,
        BigDecimal discountRate) {
    private static final int MONEY_SCALE = 2;
    private static final int RATE_SCALE = 4;
    private static final BigDecimal MAXIMUM_DISCOUNT_RATE = BigDecimal.ONE;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN;

    public PriceRequest {
        Objects.requireNonNull(unitPrice, "unitPrice");
        Objects.requireNonNull(discountRate, "discountRate");
        if (unitPrice.signum() < 0) {
            throw new IllegalArgumentException(
                    "unitPrice must not be negative");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        if (discountRate.signum() < 0
                || discountRate.compareTo(MAXIMUM_DISCOUNT_RATE) > 0) {
            throw new IllegalArgumentException(
                    "discountRate must be between 0 and 1");
        }

        unitPrice = unitPrice.setScale(MONEY_SCALE, ROUNDING);
        discountRate = discountRate.setScale(RATE_SCALE, ROUNDING);
    }
}
```

Plik `pl/training/module8/incremental/PriceQuote.java`:

```java
package pl.training.module8.incremental;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record PriceQuote(BigDecimal netAmount) {
    private static final int MONEY_SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN;

    public PriceQuote {
        Objects.requireNonNull(netAmount, "netAmount");
        if (netAmount.signum() < 0) {
            throw new IllegalArgumentException(
                    "netAmount must not be negative");
        }
        netAmount = netAmount.setScale(MONEY_SCALE, ROUNDING);
    }
}
```

Abstrakcja wyraża jedną potrzebę klienta. Komentarz określa ważny warunek użycia trybu shadow: implementacje tej granicy są wolne od efektów zewnętrznych.

Plik `pl/training/module8/incremental/PricingEngine.java`:

```java
package pl.training.module8.incremental;

/**
 * Czysta granica obliczeniowa używana podczas migracji. Implementacje nie
 * wykonują operacji wejścia-wyjścia ani nie modyfikują zewnętrznego stanu.
 */
@FunctionalInterface
public interface PricingEngine {
    PriceQuote quote(PriceRequest request);
}
```

Stary kalkulator oczekuje rabatu jako wartości procentowej od `0` do `100`.

Plik `pl/training/module8/incremental/LegacyPriceCalculator.java`:

```java
package pl.training.module8.incremental;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class LegacyPriceCalculator {
    private static final int MONEY_SCALE = 2;
    private static final int PERCENT_SCALE = 2;
    private static final BigDecimal MAXIMUM_DISCOUNT_PERCENT =
            new BigDecimal("100");
    private static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN;

    public BigDecimal calculate(
            BigDecimal unitPrice,
            int quantity,
            BigDecimal discountPercent) {
        Objects.requireNonNull(unitPrice, "unitPrice");
        Objects.requireNonNull(discountPercent, "discountPercent");
        if (unitPrice.signum() < 0) {
            throw new IllegalArgumentException(
                    "unitPrice must not be negative");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        if (discountPercent.signum() < 0
                || discountPercent.compareTo(
                        MAXIMUM_DISCOUNT_PERCENT) > 0) {
            throw new IllegalArgumentException(
                    "discountPercent must be between 0 and 100");
        }

        BigDecimal normalizedUnitPrice = unitPrice.setScale(
                MONEY_SCALE, ROUNDING);
        BigDecimal normalizedDiscountPercent = discountPercent.setScale(
                PERCENT_SCALE, ROUNDING);
        BigDecimal grossAmount = normalizedUnitPrice.multiply(
                BigDecimal.valueOf(quantity));
        BigDecimal discountAmount = grossAmount
                .multiply(normalizedDiscountPercent)
                .movePointLeft(2)
                .setScale(MONEY_SCALE, ROUNDING);

        return grossAmount.subtract(discountAmount)
                .setScale(MONEY_SCALE, ROUNDING);
    }
}
```

Adapter tłumaczy kontrakt klienta na stare API. Kod domenowy nie musi znać jednostki procentowej legacy.

Plik `pl/training/module8/incremental/LegacyPricingEngineAdapter.java`:

```java
package pl.training.module8.incremental;

import java.util.Objects;

public final class LegacyPricingEngineAdapter implements PricingEngine {
    private final LegacyPriceCalculator calculator;

    public LegacyPricingEngineAdapter() {
        this(new LegacyPriceCalculator());
    }

    public LegacyPricingEngineAdapter(LegacyPriceCalculator calculator) {
        this.calculator = Objects.requireNonNull(calculator, "calculator");
    }

    @Override
    public PriceQuote quote(PriceRequest request) {
        Objects.requireNonNull(request, "request");
        return new PriceQuote(calculator.calculate(
                request.unitPrice(),
                request.quantity(),
                request.discountRate().movePointRight(2)));
    }
}
```

Kandydat zachowuje moment zaokrąglenia rabatu. Przy `HALF_EVEN` zaokrąglenie rabatu przed odejmowaniem nie zawsze jest równoważne zaokrągleniu dopiero końcowej kwoty.

Plik `pl/training/module8/incremental/CandidatePricingEngine.java`:

```java
package pl.training.module8.incremental;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class CandidatePricingEngine implements PricingEngine {
    private static final int MONEY_SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN;

    @Override
    public PriceQuote quote(PriceRequest request) {
        Objects.requireNonNull(request, "request");

        BigDecimal grossAmount = request.unitPrice().multiply(
                BigDecimal.valueOf(request.quantity()));
        BigDecimal discountAmount = grossAmount.multiply(
                request.discountRate()).setScale(MONEY_SCALE, ROUNDING);

        return new PriceQuote(grossAmount.subtract(discountAmount));
    }
}
```

Trzy stany migracji są nazwane enumem. Flaga boolean nie potrafiłaby opisać osobno kontroli legacy, weryfikacji i produkcyjnego użycia kandydata.

Plik `pl/training/module8/incremental/MigrationMode.java`:

```java
package pl.training.module8.incremental;

public enum MigrationMode {
    LEGACY,
    VERIFY,
    CANDIDATE
}
```

Wynik porównania jest zamkniętą hierarchią zdarzeń. Dla awarii kandydata zapisywany jest typ wyjątku i komunikat, lecz sam wyjątek nie przechodzi do klienta trybu `VERIFY`.

Plik `pl/training/module8/incremental/VerificationEvent.java`:

```java
package pl.training.module8.incremental;

import java.util.Objects;

public sealed interface VerificationEvent {
    record Agreement(
            PriceRequest request,
            PriceQuote quote) implements VerificationEvent {
        public Agreement {
            Objects.requireNonNull(request, "request");
            Objects.requireNonNull(quote, "quote");
        }
    }

    record Divergence(
            PriceRequest request,
            PriceQuote legacyQuote,
            PriceQuote candidateQuote) implements VerificationEvent {
        public Divergence {
            Objects.requireNonNull(request, "request");
            Objects.requireNonNull(legacyQuote, "legacyQuote");
            Objects.requireNonNull(candidateQuote, "candidateQuote");
            if (legacyQuote.equals(candidateQuote)) {
                throw new IllegalArgumentException(
                        "divergent quotes must be different");
            }
        }
    }

    record CandidateFailure(
            PriceRequest request,
            PriceQuote legacyQuote,
            String exceptionType,
            String message) implements VerificationEvent {
        public CandidateFailure {
            Objects.requireNonNull(request, "request");
            Objects.requireNonNull(legacyQuote, "legacyQuote");
            Objects.requireNonNull(exceptionType, "exceptionType");
            Objects.requireNonNull(message, "message");
            if (exceptionType.isBlank()) {
                throw new IllegalArgumentException(
                        "exceptionType must not be blank");
            }
        }
    }
}
```

Plik `pl/training/module8/incremental/VerificationReporter.java`:

```java
package pl.training.module8.incremental;

@FunctionalInterface
public interface VerificationReporter {
    void report(VerificationEvent event);

    static VerificationReporter ignoring() {
        return event -> {
        };
    }
}
```

`MigratingPricingEngine` centralizuje wybór ścieżki. W `VERIFY` najpierw uzyskuje autorytatywny wynik legacy, a następnie synchronicznie uruchamia kandydata. Po sukcesie albo przechwyconym `RuntimeException` zwraca wartość kontrolną. Jest to deterministyczny wariant warsztatowy dla szybkiego, czystego obliczenia, a nie pełna izolacja operacyjna.

Plik `pl/training/module8/incremental/MigratingPricingEngine.java`:

```java
package pl.training.module8.incremental;

import java.util.Objects;

public final class MigratingPricingEngine implements PricingEngine {
    private final PricingEngine legacy;
    private final PricingEngine candidate;
    private final VerificationReporter reporter;
    private final MigrationMode mode;

    public MigratingPricingEngine(
            PricingEngine legacy,
            PricingEngine candidate,
            VerificationReporter reporter,
            MigrationMode mode) {
        this.legacy = Objects.requireNonNull(legacy, "legacy");
        this.candidate = Objects.requireNonNull(candidate, "candidate");
        this.reporter = Objects.requireNonNull(reporter, "reporter");
        this.mode = Objects.requireNonNull(mode, "mode");
    }

    @Override
    public PriceQuote quote(PriceRequest request) {
        Objects.requireNonNull(request, "request");
        return switch (mode) {
            case LEGACY -> requireQuote(legacy.quote(request), "legacy quote");
            case VERIFY -> verify(request);
            case CANDIDATE -> requireQuote(
                    candidate.quote(request), "candidate quote");
        };
    }

    private PriceQuote verify(PriceRequest request) {
        PriceQuote legacyQuote = requireQuote(
                legacy.quote(request), "legacy quote");
        try {
            PriceQuote candidateQuote = requireQuote(
                    candidate.quote(request), "candidate quote");
            VerificationEvent event = legacyQuote.equals(candidateQuote)
                    ? new VerificationEvent.Agreement(request, legacyQuote)
                    : new VerificationEvent.Divergence(
                            request, legacyQuote, candidateQuote);
            tryToReport(event);
        } catch (RuntimeException failure) {
            tryToReport(
                    new VerificationEvent.CandidateFailure(
                            request,
                            legacyQuote,
                            failure.getClass().getName(),
                            Objects.toString(failure.getMessage(), "")));
        }
        return legacyQuote;
    }

    private void tryToReport(VerificationEvent event) {
        try {
            reporter.report(event);
        } catch (RuntimeException ignored) {
            // Awaria reportera nie może zastąpić wyniku legacy.
        }
    }

    private static PriceQuote requireQuote(
            PriceQuote quote,
            String message) {
        return Objects.requireNonNull(quote, message);
    }
}
```

Wspólny test kontraktowy jest uruchamiany dla adaptera legacy i implementacji kandydującej. Przypadek rabatu równego połowie centa chroni dokładny moment zaokrąglenia.

Plik `pl/training/module8/incremental/PricingEngineContractTest.java`:

```java
package pl.training.module8.incremental;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

final class PricingEngineContractTest {
    @ParameterizedTest(name = "{0}")
    @MethodSource("engines")
    void everyProductionImplementationCalculatesCanonicalExamples(
            String description,
            PricingEngine engine) {
        assertAll(
                () -> assertQuote(engine, "10.00", 3, "0", "30.00"),
                () -> assertQuote(engine, "10.00", 3, "1", "0.00"),
                () -> assertQuote(engine, "0.01", 1, "0.5", "0.01"),
                () -> assertQuote(
                        engine, "19.995", 2, "0.12555", "34.98"),
                () -> assertQuote(
                        engine, "0.05", 3, "0.3333", "0.10"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("engines")
    void everyProductionImplementationIsDeterministicAndReturnsMoney(
            String description,
            PricingEngine engine) {
        var request = new PriceRequest(
                new BigDecimal("17.49"),
                7,
                new BigDecimal("0.075"));

        PriceQuote first = engine.quote(request);
        PriceQuote second = engine.quote(request);

        assertAll(
                () -> assertEquals(first, second),
                () -> assertEquals(2, first.netAmount().scale()),
                () -> assertEquals(new BigDecimal("113.25"),
                        first.netAmount()));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("engines")
    void everyProductionImplementationRejectsMissingRequest(
            String description,
            PricingEngine engine) {
        NullPointerException failure = assertThrows(
                NullPointerException.class,
                () -> engine.quote(null));

        assertEquals("request", failure.getMessage());
    }

    private static void assertQuote(
            PricingEngine engine,
            String unitPrice,
            int quantity,
            String discountRate,
            String expected) {
        PriceQuote quote = engine.quote(new PriceRequest(
                new BigDecimal(unitPrice),
                quantity,
                new BigDecimal(discountRate)));

        assertEquals(new BigDecimal(expected), quote.netAmount());
        assertEquals(2, quote.netAmount().scale());
    }

    private static Stream<Arguments> engines() {
        return Stream.of(
                Arguments.of(
                        "legacy implementation behind an adapter",
                        new LegacyPricingEngineAdapter()),
                Arguments.of(
                        "candidate implementation",
                        new CandidatePricingEngine()));
    }
}
```

Testy `MigratingPricingEngineTest` dodatkowo sprawdzają liczbę wywołań, brak uruchomienia nieaktywnej implementacji, zgodność, rozbieżność, `null`, wyjątek kandydata, awarię reportera, autorytatywną awarię legacy i brak maskowania błędu JVM. Każdy z tych przypadków znajduje się w projekcie Maven i jest uruchamiany przez pełny build.

### 1.6. Granice trybu shadow

Tryb `VERIFY` powinien zachować wynik kontrolnej implementacji legacy dla przewidzianych awarii kandydata. Przykład przechwytuje `RuntimeException`, tworzy zdarzenie i wykonuje próbę przekazania go reporterowi. Nie maskuje błędów JVM reprezentowanych przez `Error` i nie gwarantuje dostarczenia zdarzenia po awarii reportera.

Kandydat i reporter są w przykładzie synchroniczni. Mogą więc zwiększyć latencję, wyczerpać zasoby albo zablokować wywołanie. Produkcyjny tryb shadow powinien izolować tę pracę poza ścieżką odpowiedzi albo stosować jawny limit czasu, ograniczenie współbieżności typu bulkhead, limit kopiowanego ruchu i osobny sygnał sprawności kanału telemetrycznego. Sam timeout nie przerywa automatycznie dowolnego obliczenia i także wymaga polityki anulowania oraz kontroli zasobów.

Podwójne wykonanie jest bezpieczne przede wszystkim dla czystego obliczenia albo odpowiednio izolowanego odczytu. Nie należy w ten sposób bezpośrednio powielać płatności, wysyłki wiadomości, zapisu do współdzielonej bazy ani publikacji zdarzenia. Dla operacji ze skutkami ubocznymi należy porównywać decyzję przed wykonaniem efektu albo skierować kandydata do odizolowanego środowiska.

Zgodność dwóch implementacji również nie dowodzi automatycznie poprawności. Obie mogą współdzielić ten sam błąd albo tę samą błędną interpretację wymagania. Potrzebne są niezależne przykłady domenowe, testy kontraktowe i kryteria operacyjne.

### 1.7. Antywzorce

- wieloletnia gałąź bez częstej integracji,
- jednorazowa migracja typu big bang bez wcześniejszych sygnałów produkcyjnych,
- odtwarzanie każdej starej funkcji bez sprawdzenia, czy nadal jest potrzebna,
- abstrakcja kopiująca całe API wymienianego frameworka,
- logika przełączania rozrzucona po kodzie domenowym,
- flaga bez właściciela i terminu usunięcia,
- uznanie zgodności odpowiedzi API za dowód zgodności danych i transakcji,
- utrzymywanie starej ścieżki bez planu jej rzeczywistego wyłączenia.

## 2. Zasada Boy Scout

### 2.1. Heurystyka, nie mandat do dowolnych zmian

Boy Scout Rule zachęca do pozostawiania dotykanego kodu w nieco lepszym stanie. Jest praktyczną heurystyką ciągłego ograniczania lokalnej entropii, a nie miernikiem jakości ani pozwoleniem na przebudowę dowolnej części systemu.

Poprawa jest właściwa w ramach bieżącego zadania, gdy:

- dotyczy kodu potrzebnego do obecnej zmiany,
- jest mała i lokalna,
- zachowuje obserwowalne zachowanie,
- daje się łatwo zweryfikować,
- nie zwiększa istotnie ryzyka konfliktu z pracą innej osoby,
- nie utrudnia zrozumienia głównej intencji zestawu zmian.

Przykładami są lepsza nazwa, małe Extract Method, usunięcie lokalnej duplikacji i uproszczenie warunku. Zmiana publicznego API, aktualizacja zależności, migracja danych albo naprawa reguły biznesowej nie są drobnym sprzątaniem.

### 2.2. Przykład lokalnej poprawy

Wspólny model danych normalizuje wartości raz, przed przekazaniem do formatera.

Plik `pl/training/module8/boyscout/DeploymentStatus.java`:

```java
package pl.training.module8.boyscout;

public enum DeploymentStatus {
    SUCCESS,
    FAILURE
}
```

Plik `pl/training/module8/boyscout/DeploymentResult.java`:

```java
package pl.training.module8.boyscout;

import java.util.Objects;

public record DeploymentResult(
        DeploymentStatus status,
        String environment,
        String description) {
    public DeploymentResult {
        Objects.requireNonNull(status, "status");
        environment = normalized(environment, "environment");
        description = normalized(description, "description");
    }

    private static String normalized(String value, String name) {
        Objects.requireNonNull(value, name);
        String normalized = value.strip();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return normalized;
    }
}
```

Wersja początkowa ma nieczytelne nazwy, wielokrotną konkatenację i powielony format wiersza. Jej publiczny kontrakt jest jednak jednoznaczny i nie powinien zostać przypadkowo zaostrzony.

Plik `pl/training/module8/boyscout/before/ReleaseSummaryFormatter.java`:

```java
package pl.training.module8.boyscout.before;

import java.util.List;
import java.util.Objects;

import pl.training.module8.boyscout.DeploymentResult;
import pl.training.module8.boyscout.DeploymentStatus;

public final class ReleaseSummaryFormatter {
    public String format(
            String releaseId,
            List<DeploymentResult> results) {
        Objects.requireNonNull(releaseId, "releaseId");
        Objects.requireNonNull(results, "results");
        if (releaseId.isBlank()) {
            throw new IllegalArgumentException(
                    "releaseId must not be blank");
        }
        if (results.isEmpty()) {
            throw new IllegalArgumentException(
                    "results must not be empty");
        }

        String s = "Release " + releaseId.strip() + "\n";
        int n = 0;
        for (DeploymentResult r : results) {
            Objects.requireNonNull(r, "result");
            if (r.status() == DeploymentStatus.SUCCESS) {
                s = s + "[OK] " + r.environment() + ": "
                        + r.description() + "\n";
                n++;
            } else {
                s = s + "[ERROR] " + r.environment() + ": "
                        + r.description() + "\n";
            }
        }
        return s + "Successful: " + n + "/" + results.size();
    }
}
```

Lokalna poprawa wydziela walidację i formatowanie wiersza, nadaje nazwy danym oraz zastępuje wielokrotną konkatenację przez `StringBuilder`. Nie dodaje nowej walidacji, nie zmienia sygnatury i zachowuje kolejność kontroli.

Plik `pl/training/module8/boyscout/after/ReleaseSummaryFormatter.java`:

```java
package pl.training.module8.boyscout.after;

import java.util.List;
import java.util.Objects;

import pl.training.module8.boyscout.DeploymentResult;
import pl.training.module8.boyscout.DeploymentStatus;

public final class ReleaseSummaryFormatter {
    public String format(
            String releaseId,
            List<DeploymentResult> results) {
        validate(releaseId, results);

        StringBuilder summary = new StringBuilder()
                .append("Release ")
                .append(releaseId.strip())
                .append('\n');
        int successfulDeployments = 0;
        for (DeploymentResult result : results) {
            Objects.requireNonNull(result, "result");
            summary.append(formatResult(result)).append('\n');
            if (result.status() == DeploymentStatus.SUCCESS) {
                successfulDeployments++;
            }
        }
        return summary.append("Successful: ")
                .append(successfulDeployments)
                .append('/')
                .append(results.size())
                .toString();
    }

    private static void validate(
            String releaseId,
            List<DeploymentResult> results) {
        Objects.requireNonNull(releaseId, "releaseId");
        Objects.requireNonNull(results, "results");
        if (releaseId.isBlank()) {
            throw new IllegalArgumentException(
                    "releaseId must not be blank");
        }
        if (results.isEmpty()) {
            throw new IllegalArgumentException(
                    "results must not be empty");
        }
    }

    private static String formatResult(DeploymentResult result) {
        String label = result.status() == DeploymentStatus.SUCCESS
                ? "[OK]"
                : "[ERROR]";
        return label + " " + result.environment() + ": "
                + result.description();
    }
}
```

Test różnicowy porównuje oba warianty, a niezależny orakl zapobiega sytuacji, w której dwie zgodne implementacje są jednocześnie błędne.

Plik `pl/training/module8/boyscout/BoyScoutEquivalenceTest.java`:

```java
package pl.training.module8.boyscout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

final class BoyScoutEquivalenceTest {
    private final pl.training.module8.boyscout.before.ReleaseSummaryFormatter
            before = new pl.training.module8.boyscout.before
                    .ReleaseSummaryFormatter();
    private final pl.training.module8.boyscout.after.ReleaseSummaryFormatter
            after = new pl.training.module8.boyscout.after
                    .ReleaseSummaryFormatter();

    @ParameterizedTest(name = "{0}")
    @MethodSource("representativeResults")
    void localCleanupPreservesRepresentativeOutputs(
            String description,
            String releaseId,
            List<DeploymentResult> results) {
        assertEquals(
                before.format(releaseId, results),
                after.format(releaseId, results));
    }

    @Test
    void refactoredCodeProducesTheIndependentlySpecifiedSummary() {
        List<DeploymentResult> results = List.of(
                success("test", "deployed"),
                failure("production", "timeout"));

        assertEquals(
                "Release release-42\n"
                        + "[OK] test: deployed\n"
                        + "[ERROR] production: timeout\n"
                        + "Successful: 1/2",
                after.format(" release-42 ", results));
    }

    @Test
    void localCleanupPreservesValidationFailuresAndTheirOrder() {
        assertSameFailure(
                () -> before.format(null, List.of(success("test", "ok"))),
                () -> after.format(null, List.of(success("test", "ok"))));
        assertSameFailure(
                () -> before.format("release-1", null),
                () -> after.format("release-1", null));
        assertSameFailure(
                () -> before.format("   ", List.of(success("test", "ok"))),
                () -> after.format("   ", List.of(success("test", "ok"))));
        assertSameFailure(
                () -> before.format("release-1", List.of()),
                () -> after.format("release-1", List.of()));
        assertSameFailure(
                () -> before.format(
                        "release-1", Collections.singletonList(null)),
                () -> after.format(
                        "release-1", Collections.singletonList(null)));
        assertSameFailure(
                () -> before.format(" ", null),
                () -> after.format(" ", null));
    }

    @Test
    void bothVersionsExposeTheSameCallableContract() {
        SummaryContract oldContract = before::format;
        SummaryContract cleanedContract = after::format;
        List<DeploymentResult> results = List.of(success("test", "ok"));

        assertEquals(
                oldContract.format("release-1", results),
                cleanedContract.format("release-1", results));
    }

    @Test
    void sharedInputModelRejectsIncompleteResults() {
        assertFailure(
                NullPointerException.class,
                "status",
                () -> new DeploymentResult(null, "test", "ok"));
        assertFailure(
                NullPointerException.class,
                "environment",
                () -> new DeploymentResult(
                        DeploymentStatus.SUCCESS, null, "ok"));
        assertFailure(
                IllegalArgumentException.class,
                "environment must not be blank",
                () -> new DeploymentResult(
                        DeploymentStatus.SUCCESS, " ", "ok"));
        assertFailure(
                NullPointerException.class,
                "description",
                () -> new DeploymentResult(
                        DeploymentStatus.SUCCESS, "test", null));
        assertFailure(
                IllegalArgumentException.class,
                "description must not be blank",
                () -> new DeploymentResult(
                        DeploymentStatus.SUCCESS, "test", " "));
    }

    private static Stream<Arguments> representativeResults() {
        return Stream.of(
                Arguments.of(
                        "single success",
                        "release-1",
                        List.of(success("test", "deployed"))),
                Arguments.of(
                        "single failure",
                        "release-2",
                        List.of(failure("production", "timeout"))),
                Arguments.of(
                        "mixed result in stable order",
                        " release-3 ",
                        List.of(
                                success("test", "deployed"),
                                failure("staging", "health check failed"),
                                success("production", "deployed"))));
    }

    private static DeploymentResult success(
            String environment,
            String description) {
        return new DeploymentResult(
                DeploymentStatus.SUCCESS, environment, description);
    }

    private static DeploymentResult failure(
            String environment,
            String description) {
        return new DeploymentResult(
                DeploymentStatus.FAILURE, environment, description);
    }

    private static void assertSameFailure(
            Runnable beforeAction,
            Runnable afterAction) {
        RuntimeException beforeFailure = assertThrows(
                RuntimeException.class, beforeAction::run);
        RuntimeException afterFailure = assertThrows(
                RuntimeException.class, afterAction::run);

        assertEquals(beforeFailure.getClass(), afterFailure.getClass());
        assertEquals(beforeFailure.getMessage(), afterFailure.getMessage());
    }

    private static void assertFailure(
            Class<? extends RuntimeException> expectedType,
            String expectedMessage,
            Runnable action) {
        RuntimeException failure = assertThrows(expectedType, action::run);
        assertEquals(expectedMessage, failure.getMessage());
    }

    @FunctionalInterface
    private interface SummaryContract {
        String format(String releaseId, List<DeploymentResult> results);
    }
}
```

### 2.3. Bezpieczna sekwencja

1. Zacznij od zielonego zestawu testów.
2. Nazwij lokalną przeszkodę utrudniającą bieżące zadanie.
3. Wykonaj jeden mały ruch strukturalny.
4. Skompiluj i uruchom zawężone testy.
5. Obejrzyj diff pod kątem niezamierzonych zmian.
6. Dopiero w osobnym kroku zmień zachowanie.
7. Gdy zakres rośnie, odłóż pozostałe porządki do osobnego zadania lub zestawu zmian.

Boy Scout Rule nie wymaga osobnego commitu dla każdej nazwy. Wymaga natomiast czytelności intencji. Niewielka zmiana nazwy może pozostać przy bieżącej pracy, lecz większe przeniesienie, formatowanie lub przebudowa powinny zostać oddzielone, aby osoba przeglądająca mogła ocenić zachowanie bez szumu strukturalnego.

### 2.4. Typowe nadużycia

- nieograniczone poprawianie kodu przy okazji małego zadania,
- ukrywanie zmiany zachowania pod etykietą refaktoryzacji,
- masowe formatowanie pliku razem z naprawą,
- poprawianie modułu należącego do innego zespołu bez komunikacji,
- dodawanie abstrakcji na podstawie hipotetycznej przyszłości,
- używanie liczby zmienionych linii jako miary pozostawienia kodu w lepszym stanie.

## 3. Praca zespołowa w refaktoryzacji

### 3.1. Wspólny obraz zmiany

Refaktoryzacja dotykająca współdzielonego kodu wymaga krótkiego uzgodnienia przed rozpoczęciem pracy. Zespół powinien znać:

- problem, który zmiana ma usunąć,
- granicę obserwowalnego zachowania,
- elementy znajdujące się poza zakresem,
- spodziewaną serię małych kroków,
- właścicieli obszarów i wymagane osoby przeglądające,
- sposób weryfikacji,
- plan wdrożenia, obserwacji i wycofania,
- kryterium usunięcia elementów przejściowych.

Dla małej lokalnej zmiany wystarczy opis zestawu zmian. Dla migracji przekraczającej granice zespołów potrzebne mogą być krótka propozycja techniczna, zapis decyzji oraz uzgodnienie odpowiedzialności operacyjnej. Dokument ma zmniejszać niepewność, a nie spełniać rytuał.

Programowanie w parze albo praca grupowa są szczególnie użyteczne przy odkrywaniu zachowania, wyznaczaniu punktu podstawienia, pierwszej transformacji ryzykownego fragmentu i analizie awarii. Nie zastępują testów. Mogą realizować ciągły przegląd merytoryczny, natomiast formalna niezależna akceptacja pozostaje potrzebna tylko wtedy, gdy wymaga jej polityka zespołu, model uprawnień albo regulacja.

### 3.2. Małe zestawy zmian

Dobry zestaw zmian reprezentuje jedną samodzielną intencję. Zawiera powiązane testy i po integracji pozostawia repozytorium w działającym stanie. Mała zmiana jest zwykle:

- szybsza do zrozumienia,
- dokładniej przeglądana,
- łatwiejsza do niezależnego przetestowania,
- mniej podatna na konflikty,
- prostsza do cofnięcia,
- lepszym źródłem informacji o rzeczywistej przyczynie regresji.

Liczba linii jest jedynie przybliżeniem. Automatyczna zmiana nazwy może objąć setki miejsc, ale mieć jedną mechaniczną intencję. Kilkanaście linii łączących zmianę schematu, zachowania i uprawnień może być trudniejsze do oceny.

Przykładowa seria dla zmiany legacy:

1. testy charakteryzujące dotychczasowe zachowanie,
2. refaktoryzacja tworząca punkt podstawienia,
3. implementacja nowego zachowania za punktem podstawienia,
4. konfiguracja kontrolowanego wdrożenia etapowego,
5. usunięcie starej ścieżki po zakończonej walidacji.

Testy dodane w pierwszym kroku także wymagają przeglądu. Mogą bowiem utrwalić przypadkowe zachowanie albo pominąć istotny efekt uboczny.

### 3.3. Przygotowanie do przeglądu kodu

Opis zmiany powinien pozwolić osobie przeglądającej odpowiedzieć na pięć pytań:

1. Dlaczego zmiana jest potrzebna?
2. Co dokładnie zmienia, a czego nie zmienia?
3. Jaki kontrakt ma pozostać niezmieniony?
4. Jakie dowody potwierdzają wynik?
5. Jak wdrożyć, obserwować i w razie potrzeby wycofać zmianę?

Warto wskazać zalecaną kolejność czytania. Przy refaktoryzacji dobrym punktem wejścia są często testy kontraktowe, następnie nowa granica, implementacje, główne miejsce składania zależności i konfiguracja wdrożenia etapowego.

Osoba przeglądająca powinna ocenić co najmniej:

- poprawność i kompletność zachowania,
- projekt oraz poziom przypadkowej złożoności,
- testy i jakość ich orakli,
- nazwy, komentarze i dokumentację,
- skutki dla danych, współbieżności, bezpieczeństwa i wydajności,
- kompatybilność klientów,
- obserwowalność i możliwość wycofania,
- plan usunięcia kodu przejściowego.

Przegląd kodu nie powinien dążyć do abstrakcyjnej perfekcji. Zmiana jest gotowa, gdy poprawia ogólny stan kodu i spełnia uzgodniony standard zespołu. Osoba przeglądająca nie powinna jednak akceptować nowej złożoności tylko dlatego, że podobny problem już istnieje w kodzie legacy.

### 3.4. Informacja zwrotna

Komentarz powinien dotyczyć kodu i ryzyka, nie osoby. Najbardziej użyteczna forma zawiera:

- obserwację,
- możliwy skutek,
- uzasadnienie,
- konkretną propozycję albo pytanie.

Zespół może oznaczać komentarze jako `BLOCKER`, `SUGGESTION` i `NIT`, o ile znaczenie poziomów jest wspólnie ustalone. Taka etykieta nie zastępuje argumentu. Przy sporze o kierunek należy szybko przejść do rozmowy synchronicznej, a końcowy wniosek zapisać w zestawie zmian lub zapisie decyzji.

### 3.5. Przykład polityki gotowości

Polityka rozróżnia intencję zmiany od dowodu jej weryfikacji. Intencje są zamkniętym zbiorem zrozumiałym dla autora i osoby przeglądającej.

Plik `pl/training/module8/collaboration/ChangeIntent.java`:

```java
package pl.training.module8.collaboration;

public enum ChangeIntent {
    CHARACTERIZATION_TESTS,
    REFACTORING,
    BEHAVIOR_CHANGE,
    ROLLOUT
}
```

Plik `pl/training/module8/collaboration/EvidenceKind.java`:

```java
package pl.training.module8.collaboration;

public enum EvidenceKind {
    AUTOMATED_TEST,
    STATIC_ANALYSIS,
    MANUAL_CHECK,
    BENCHMARK,
    STAGING_OBSERVATION
}
```

Plik `pl/training/module8/collaboration/VerificationEvidence.java`:

```java
package pl.training.module8.collaboration;

import java.util.Objects;

public record VerificationEvidence(EvidenceKind kind, String observation) {
    public VerificationEvidence {
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(observation, "observation");
        if (observation.isBlank()) {
            throw new IllegalArgumentException("observation must not be blank");
        }
    }
}
```

`ChangeSet` tworzy niemutowalne migawki wejściowych kolekcji. Późniejsza modyfikacja danych autora nie może zmienić obiektu już poddanego ocenie.

Plik `pl/training/module8/collaboration/ChangeSet.java`:

```java
package pl.training.module8.collaboration;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public record ChangeSet(
        String title,
        Set<ChangeIntent> intents,
        List<VerificationEvidence> verificationEvidence,
        boolean independentlyGreenBuild) {

    public ChangeSet {
        Objects.requireNonNull(title, "title");
        if (title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        intents = Set.copyOf(Objects.requireNonNull(intents, "intents"));
        verificationEvidence = List.copyOf(Objects.requireNonNull(
                verificationEvidence,
                "verificationEvidence"));
    }
}
```

Problemy gotowości są typowane. Klient nie musi analizować komunikatu przeznaczonego dla człowieka.

Plik `pl/training/module8/collaboration/ReadinessProblem.java`:

```java
package pl.training.module8.collaboration;

public enum ReadinessProblem {
    MISSING_INTENT,
    MIXED_PRIMARY_INTENTS,
    MISSING_VERIFICATION_EVIDENCE,
    BUILD_NOT_INDEPENDENTLY_GREEN
}
```

Plik `pl/training/module8/collaboration/ReviewReadiness.java`:

```java
package pl.training.module8.collaboration;

import java.util.List;
import java.util.Objects;

public record ReviewReadiness(List<ReadinessProblem> problems) {
    public ReviewReadiness {
        problems = List.copyOf(Objects.requireNonNull(problems, "problems"));
    }

    public boolean ready() {
        return problems.isEmpty();
    }
}
```

Plik `pl/training/module8/collaboration/ExampleTeamReviewPolicy.java`:

```java
package pl.training.module8.collaboration;

import java.util.ArrayList;
import java.util.Objects;

/**
 * An example team policy, not a universal definition of review readiness.
 */
public final class ExampleTeamReviewPolicy {
    public ReviewReadiness assess(ChangeSet changeSet) {
        Objects.requireNonNull(changeSet, "changeSet");

        var problems = new ArrayList<ReadinessProblem>();
        if (changeSet.intents().isEmpty()) {
            problems.add(ReadinessProblem.MISSING_INTENT);
        } else if (changeSet.intents().size() > 1) {
            problems.add(ReadinessProblem.MIXED_PRIMARY_INTENTS);
        }
        if (changeSet.verificationEvidence().isEmpty()) {
            problems.add(ReadinessProblem.MISSING_VERIFICATION_EVIDENCE);
        }
        if (!changeSet.independentlyGreenBuild()) {
            problems.add(ReadinessProblem.BUILD_NOT_INDEPENDENTLY_GREEN);
        }
        return new ReviewReadiness(problems);
    }
}
```

Test pokazuje gotową zmianę oraz stabilną kolejność wszystkich problemów. Sprawdza też, że zewnętrzne kolekcje nie pozostają aliasami stanu `ChangeSet`.

Plik `pl/training/module8/collaboration/ExampleTeamReviewPolicyTest.java`:

```java
package pl.training.module8.collaboration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

final class ExampleTeamReviewPolicyTest {
    private final ExampleTeamReviewPolicy policy = new ExampleTeamReviewPolicy();

    @Test
    void acceptsAFocusedChangeWithEvidenceAndAGreenBuild() {
        var changeSet = new ChangeSet(
                "Extract deployment clock",
                EnumSet.of(ChangeIntent.REFACTORING),
                List.of(new VerificationEvidence(
                        EvidenceKind.AUTOMATED_TEST,
                        "mvn test: 42 tests passed")),
                true);

        ReviewReadiness readiness = policy.assess(changeSet);

        assertTrue(readiness.ready());
        assertEquals(List.of(), readiness.problems());
    }

    @Test
    void reportsMixedIntentMissingEvidenceAndNonGreenBuildInStableOrder() {
        var changeSet = new ChangeSet(
                "Move validator and change its rules",
                EnumSet.of(
                        ChangeIntent.REFACTORING,
                        ChangeIntent.BEHAVIOR_CHANGE),
                List.of(),
                false);

        ReviewReadiness readiness = policy.assess(changeSet);

        assertFalse(readiness.ready());
        assertEquals(
                List.of(
                        ReadinessProblem.MIXED_PRIMARY_INTENTS,
                        ReadinessProblem.MISSING_VERIFICATION_EVIDENCE,
                        ReadinessProblem.BUILD_NOT_INDEPENDENTLY_GREEN),
                readiness.problems());
    }

    @Test
    void reportsAnUnspecifiedIntentAsATypedProblem() {
        var changeSet = new ChangeSet(
                "Unclassified change",
                EnumSet.noneOf(ChangeIntent.class),
                List.of(new VerificationEvidence(
                        EvidenceKind.STATIC_ANALYSIS,
                        "No new findings")),
                true);

        assertEquals(
                List.of(ReadinessProblem.MISSING_INTENT),
                policy.assess(changeSet).problems());
    }

    @Test
    void snapshotsMutableInputCollections() {
        var intents = EnumSet.of(ChangeIntent.CHARACTERIZATION_TESTS);
        var evidence = new ArrayList<>(List.of(new VerificationEvidence(
                EvidenceKind.AUTOMATED_TEST,
                "Characterization suite passed")));

        var changeSet = new ChangeSet("Capture behavior", intents, evidence, true);
        intents.add(ChangeIntent.ROLLOUT);
        evidence.clear();

        assertEquals(
                Set.of(ChangeIntent.CHARACTERIZATION_TESTS),
                changeSet.intents());
        assertEquals(1, changeSet.verificationEvidence().size());
        assertThrows(
                UnsupportedOperationException.class,
                () -> changeSet.intents().add(ChangeIntent.ROLLOUT));
        assertThrows(
                UnsupportedOperationException.class,
                () -> changeSet.verificationEvidence().clear());
    }
}
```

Polityka w przykładzie jest lokalną umową zespołu. Nie należy przedstawiać jej jako uniwersalnego algorytmu oceny przeglądu. Jej wartością jest jawność: autor i osoba przeglądająca widzą te same, typowane powody zatrzymania zmiany.

### 3.6. Czego nie należy łączyć

Duża refaktoryzacja i zmiana funkcjonalna powinny trafiać do osobnych zestawów zmian. Pozwala to niezależnie ocenić równoważność strukturalną i poprawność nowej reguły. Wyjątkiem może być bardzo małe lokalne uporządkowanie, którego wydzielenie utrudniłoby zrozumienie zamiast je ułatwić.

Szczególnie warto oddzielać:

- masowe formatowanie od zmian logicznych,
- rename i przeniesienia od naprawy błędu,
- automatycznie wygenerowaną transformację od ręcznych poprawek,
- zmianę schematu danych od przełączenia odczytów,
- wprowadzenie flagi od zwiększania ekspozycji,
- usunięcie starego kodu od wcześniejszego przełączenia ruchu.

## 4. Dokumentowanie zmian

### 4.1. Dokumentacja według trwałości

Nie każda informacja należy do komentarza w kodzie. Właściwe miejsce zależy od odbiorcy i przewidywanej trwałości:

| Artefakt | Co powinien zawierać |
| --- | --- |
| nazwy i struktura kodu | bieżący model i intencję implementacji |
| komentarz | nieoczywiste uzasadnienie, ograniczenie albo kontrakt zewnętrzny |
| test | wykonywalny przykład zachowania i granic |
| opis zestawu zmian | cel, zakres, dowody, ryzyko, wdrożenie etapowe i wycofanie konkretnej zmiany |
| commit | jeden logiczny krok i powód jego wykonania |
| ADR | trwałą, istotną decyzję architektoniczną, jej kontekst i konsekwencje |
| instrukcja operacyjna | procedurę wdrożenia, diagnozy i odzyskania |

Kod powinien opisywać stan obecny. Komentarz typu „przeniesiono tę metodę podczas refaktoryzacji” szybko traci wartość, ponieważ historię ruchu przechowuje system kontroli wersji. Komentarz wyjaśniający wymaganie regulatora, nietypową kolejność integracji albo przyczynę pozornie zbędnej walidacji może pozostać istotny.

### 4.2. Opis zestawu zmian

Profesjonalny opis nie powtarza diffu. Powinien zawierać:

- problem i oczekiwany rezultat,
- zakres oraz elementy świadomie pominięte,
- kontrakt zachowywany przez refaktoryzację,
- zmianę zachowania, jeśli występuje, opisaną oddzielnie,
- dowody z testów i analizy,
- wpływ na dane, integracje i operacje,
- sposób wdrożenia oraz obserwacji,
- warunki zatrzymania i wycofania,
- plan usunięcia flag, adapterów i starego kodu.

Stwierdzenie „testy przeszły” jest zbyt słabe dla ryzykownej migracji. Należy wskazać, jakie testy wykonano, jakie scenariusze chronią i które ryzyka pozostają poza ich zakresem.

### 4.3. Architecture Decision Record

ADR jest właściwy dla decyzji o trwałym wpływie na architekturę, na przykład wyboru granicy migracji, modelu własności danych albo strategii współistnienia implementacji. Nie jest potrzebny dla każdego Extract Method ani Rename.

Klasyczny lekki rdzeń ADR obejmuje:

- tytuł,
- status,
- kontekst,
- decyzję,
- konsekwencje.

Lokalny szablon tego modułu rozszerza rdzeń o identyfikator, jawnie rozważone opcje i metodę weryfikacji. Są to wartościowe elementy procesu, ale nie uniwersalne wymagania formatu ADR. Model wymaga przynajmniej jednej konsekwencji dowolnego rodzaju. W praktyce warto uczciwie opisać zarówno korzyści, jak i koszty, jeżeli oba występują.

Po zmianie kierunku stary zapis warto oznaczyć jako zastąpiony i utworzyć nowy dokument. Zachowuje to historię rozumowania dostępną razem z kodem.

### 4.4. Przykład wykonywalnego modelu decyzji

Przykład stosuje lokalną konwencję identyfikatora `ADR-NNNN`. Nie jest to wymóg formatu ADR, lecz jawna reguła tego projektu.

Plik `pl/training/module8/documentation/DecisionId.java`:

```java
package pl.training.module8.documentation;

import java.util.Objects;
import java.util.regex.Pattern;

public record DecisionId(String value) {
    private static final Pattern FORMAT = Pattern.compile("ADR-[0-9]{4}");

    public DecisionId {
        Objects.requireNonNull(value, "value");
        if (!FORMAT.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "value must use the format ADR-NNNN");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
```

Plik `pl/training/module8/documentation/DecisionStatus.java`:

```java
package pl.training.module8.documentation;

public enum DecisionStatus {
    PROPOSED,
    ACCEPTED,
    REJECTED,
    DEPRECATED,
    SUPERSEDED
}
```

Plik `pl/training/module8/documentation/DecisionOption.java`:

```java
package pl.training.module8.documentation;

import java.util.Objects;

public record DecisionOption(String name, String rationale) {
    public DecisionOption {
        name = requireNonBlank(name, "name");
        rationale = requireNonBlank(rationale, "rationale");
    }

    private static String requireNonBlank(String value, String name) {
        Objects.requireNonNull(value, name);
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }
}
```

Plik `pl/training/module8/documentation/ConsequenceKind.java`:

```java
package pl.training.module8.documentation;

public enum ConsequenceKind {
    POSITIVE,
    NEGATIVE,
    NEUTRAL
}
```

Plik `pl/training/module8/documentation/DecisionConsequence.java`:

```java
package pl.training.module8.documentation;

import java.util.Objects;

public record DecisionConsequence(ConsequenceKind kind, String description) {
    public DecisionConsequence {
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(description, "description");
        if (description.isBlank()) {
            throw new IllegalArgumentException("description must not be blank");
        }
    }
}
```

`DecisionRecord` wymaga przynajmniej jednej rozważonej opcji i jednej konsekwencji. Kopie defensywne chronią dokument przed zmianą list należących do klienta.

Plik `pl/training/module8/documentation/DecisionRecord.java`:

```java
package pl.training.module8.documentation;

import java.util.List;
import java.util.Objects;

public record DecisionRecord(
        DecisionId id,
        String title,
        DecisionStatus status,
        String context,
        String decision,
        List<DecisionOption> consideredOptions,
        List<DecisionConsequence> consequences,
        String verificationMethod) {

    public DecisionRecord {
        Objects.requireNonNull(id, "id");
        title = requireNonBlank(title, "title");
        Objects.requireNonNull(status, "status");
        context = requireNonBlank(context, "context");
        decision = requireNonBlank(decision, "decision");
        consideredOptions = nonEmptyCopy(consideredOptions, "consideredOptions");
        consequences = nonEmptyCopy(consequences, "consequences");
        verificationMethod = requireNonBlank(
                verificationMethod,
                "verificationMethod");
    }

    private static String requireNonBlank(String value, String name) {
        Objects.requireNonNull(value, name);
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }

    private static <T> List<T> nonEmptyCopy(List<T> values, String name) {
        List<T> copy = List.copyOf(Objects.requireNonNull(values, name));
        if (copy.isEmpty()) {
            throw new IllegalArgumentException(name + " must not be empty");
        }
        return copy;
    }
}
```

Plik `pl/training/module8/documentation/DecisionRecordMarkdownRenderer.java`:

```java
package pl.training.module8.documentation;

import java.util.Objects;

public final class DecisionRecordMarkdownRenderer {
    public String render(DecisionRecord record) {
        Objects.requireNonNull(record, "record");

        var markdown = new StringBuilder();
        markdown.append("# ")
                .append(record.id())
                .append(": ")
                .append(record.title())
                .append("\n\n");
        section(markdown, "Status", statusLabel(record.status()));
        section(markdown, "Kontekst", record.context());
        section(markdown, "Decyzja", record.decision());

        markdown.append("## Rozważone opcje\n\n");
        for (int index = 0; index < record.consideredOptions().size(); index++) {
            DecisionOption option = record.consideredOptions().get(index);
            markdown.append(index + 1)
                    .append(". **")
                    .append(option.name())
                    .append("**: ")
                    .append(option.rationale())
                    .append('\n');
        }
        markdown.append('\n');

        markdown.append("## Konsekwencje\n\n");
        for (DecisionConsequence consequence : record.consequences()) {
            markdown.append("- **")
                    .append(consequenceLabel(consequence.kind()))
                    .append("**: ")
                    .append(consequence.description())
                    .append('\n');
        }
        markdown.append('\n');
        section(markdown, "Metoda weryfikacji", record.verificationMethod());
        return markdown.toString();
    }

    private static void section(
            StringBuilder markdown,
            String heading,
            String content) {

        markdown.append("## ")
                .append(heading)
                .append("\n\n")
                .append(content)
                .append("\n\n");
    }

    private static String statusLabel(DecisionStatus status) {
        return switch (status) {
            case PROPOSED -> "Proponowana";
            case ACCEPTED -> "Zaakceptowana";
            case REJECTED -> "Odrzucona";
            case DEPRECATED -> "Wycofana";
            case SUPERSEDED -> "Zastąpiona";
        };
    }

    private static String consequenceLabel(ConsequenceKind kind) {
        return switch (kind) {
            case POSITIVE -> "Pozytywna";
            case NEGATIVE -> "Negatywna";
            case NEUTRAL -> "Neutralna";
        };
    }
}
```

Test zapisuje oczekiwany dokument jako text block. Chroni kolejność sekcji, etykiety i końcowe znaki nowej linii.

Plik `pl/training/module8/documentation/DecisionRecordMarkdownRendererTest.java`:

```java
package pl.training.module8.documentation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

final class DecisionRecordMarkdownRendererTest {
    private final DecisionRecordMarkdownRenderer renderer =
            new DecisionRecordMarkdownRenderer();

    @Test
    void rendersEveryFieldInDeterministicOrder() {
        var record = new DecisionRecord(
                new DecisionId("ADR-0042"),
                "Wyodrębnij port zegara",
                DecisionStatus.ACCEPTED,
                "Logika domenowa odczytuje czas systemowy bezpośrednio.",
                "Wprowadzamy port zegara na granicy aplikacji.",
                List.of(
                        new DecisionOption(
                                "Port zegara",
                                "Pozwala kontrolować czas w testach."),
                        new DecisionOption(
                                "Mockowanie statyczne",
                                "Nie zmienia sygnatur, ale zwiększa sprzężenie testów.")),
                List.of(
                        new DecisionConsequence(
                                ConsequenceKind.POSITIVE,
                                "Testy stają się deterministyczne."),
                        new DecisionConsequence(
                                ConsequenceKind.NEGATIVE,
                                "Konstruktor otrzymuje dodatkową zależność.")),
                "Uruchom testy charakteryzujące przed i po zmianie.");

        String expected = """
                # ADR-0042: Wyodrębnij port zegara

                ## Status

                Zaakceptowana

                ## Kontekst

                Logika domenowa odczytuje czas systemowy bezpośrednio.

                ## Decyzja

                Wprowadzamy port zegara na granicy aplikacji.

                ## Rozważone opcje

                1. **Port zegara**: Pozwala kontrolować czas w testach.
                2. **Mockowanie statyczne**: Nie zmienia sygnatur, ale zwiększa sprzężenie testów.

                ## Konsekwencje

                - **Pozytywna**: Testy stają się deterministyczne.
                - **Negatywna**: Konstruktor otrzymuje dodatkową zależność.

                ## Metoda weryfikacji

                Uruchom testy charakteryzujące przed i po zmianie.

                """;

        assertEquals(expected, renderer.render(record));
        assertEquals(expected, renderer.render(record));
    }

    @Test
    void rejectsANullRecord() {
        assertThrows(NullPointerException.class, () -> renderer.render(null));
    }
}
```

Renderer jest deterministyczny, dlatego wynik można sprawdzić testem i przechowywać w systemie kontroli wersji. Sam generator nie gwarantuje jakości decyzji. Wartość pochodzi z konkretnego kontekstu, uczciwie opisanych alternatyw i konsekwencji oraz późniejszej weryfikacji.

### 4.5. Dokumentacja żywa i historyczna

Instrukcja operacyjna, diagram stanu obecnego i opis wdrożenia powinny być aktualizowane wraz ze zmianą systemu. ADR i zamknięty opis zestawu zmian są zapisem historycznym. Mieszanie tych ról prowadzi albo do utraty historii, albo do instrukcji operacyjnej opisującej nieistniejący system.

Właściciel architektury przejściowej powinien regularnie sprawdzać:

- czy opis routingu odpowiada konfiguracji produkcyjnej,
- czy kryteria usunięcia starej ścieżki zostały spełnione,
- czy procedura wycofania nadal działa,
- czy dashboardy i alerty pokrywają obie implementacje,
- czy termin usunięcia flagi nadal jest realny.

## 5. Narzędzia wspierające refaktoryzację

### 5.1. Różne narzędzia, różne dowody

| Narzędzie | Główna rola | Czego samo nie dowodzi |
| --- | --- | --- |
| refaktoryzacja IDE | transformacja oparta na symbolach i analiza konfliktów | zgodność klientów poza projektem i użyć dynamicznych |
| kompilator | poprawność typów, składni i wybrane ostrzeżenia | poprawność reguł biznesowych |
| narzędzie formatujące | jednolity zapis kodu | dobry projekt i poprawne zachowanie |
| linter stylu | zgodność z jawnymi regułami źródła | semantyka całego systemu |
| analiza statyczna | wykrywanie określonych wzorców ryzyka | brak wyników fałszywie dodatnich i fałszywie ujemnych |
| testy | zgodność dla wykonanych scenariuszy i orakli | zachowanie poza zakresem testu |
| automatyczna receptura | powtarzalna transformacja wielu miejsc | poprawność każdej domenowej konsekwencji |
| przegląd kodu | ocena intencji, projektu i ryzyka | matematyczny dowód braku regresji |

Bezpieczna bramka łączy kilka niezależnych sygnałów. Zielony wynik pojedynczego narzędzia nie powinien być traktowany jako certyfikat poprawności.

### 5.2. Refaktoryzacje IDE

Rename, Move, Change Signature, Extract, Inline i Safe Delete korzystają z modelu symboli projektu. Są bezpieczniejsze niż ręczne wyszukiwanie i zamiana, ponieważ rozróżniają deklaracje oraz użycia i potrafią sygnalizować konflikty.

Granica analizy IDE nadal istnieje. Narzędzie może nie znać:

- konsumentów skompilowanych poza repozytorium,
- nazw klas i metod zapisanych w konfiguracji,
- użyć przez refleksję albo mechanizm pluginów,
- szablonów, skryptów i zapytań zależnych od nazw,
- nazw serializowanych do danych trwałych,
- kontraktów wykorzystywanych przez inne języki.

Przed zatwierdzeniem należy obejrzeć podgląd transformacji, przeszukać użycia tekstowe, skompilować pełny projekt i uruchomić testy integracyjne odpowiednie dla granicy.

### 5.3. Kompilator Javy 25

Opcja `--release 25` ustala poziom języka, format klas i dostępne udokumentowane API platformy. Nie gwarantuje, że sam Maven został uruchomiony na JDK 25. W środowisku CI należy kontrolować faktyczny JDK, na przykład przez mechanizm toolchains Mavena lub jawnie skonfigurowane środowisko wykonania.

`-Xlint` włącza zalecane ostrzeżenia kompilatora. `-Werror` powoduje niepowodzenie kompilacji, gdy wystąpi ostrzeżenie. Jest to silna bramka dla nowego lub oczyszczonego kodu, lecz gwałtowne włączenie jej w dużym systemie legacy może zablokować wszystkie zmiany z powodu wcześniejszych problemów.

Rozsądne wdrożenie obejmuje:

1. zapisanie stanu bazowego,
2. ocenę każdej kategorii ostrzeżeń,
3. naprawę problemów wysokiego ryzyka,
4. ograniczone i uzasadnione wyłączenia,
5. niedopuszczanie nowych naruszeń,
6. stopniowe zmniejszanie stanu bazowego naruszeń.

Tłumienie powinno wskazywać konkretną regułę i obejmować najmniejszy możliwy zakres. Należy rozróżnić wynik fałszywie dodatni, regułę niedopasowaną do polityki projektu i prawdziwe ryzyko świadomie zaakceptowane na określony czas.

### 5.4. Analiza statyczna i formatowanie

Checkstyle koncentruje się na konwencjach źródła, składni, nazwach i regułach możliwych do oceny w jego modelu. PMD analizuje źródło, AST oraz wybrane zależności i przepływy. SpotBugs analizuje skompilowany kod bajtowy pod kątem wzorców prawdopodobnych błędów. Narzędzia te pokrywają inne klasy problemów i nie są zamienne.

Przed użyciem trzeba potwierdzić, że konkretna wersja analizatora obsługuje kod bajtowy i składnię używanej wersji Javy. Niepełna ścieżka klas, czyli `classpath`, może pogorszyć dokładność analizy. Wyniki należy wersjonować przez konfigurację, a wyłączenia dokumentować blisko kodu lub w precyzyjnym filtrze.

Narzędzie formatujące usuwa dyskusje o zapisie i ogranicza szum w diffie. Formatowanie całego starego repozytorium w jednym zestawie zmian z naprawą logiczną utrudnia jednak przegląd i analizę historii. Lepsze są oddzielna zmiana mechaniczna albo zasada blokowania nowych naruszeń, nazywana ratchet, obejmująca nowe i dotykane pliki.

### 5.5. Automatyzacja transformacji

IDE dobrze obsługuje zmianę interaktywną w jednym repozytorium. Dla powtarzalnej migracji wielu miejsc lub repozytoriów warto użyć strukturalnej receptury, na przykład OpenRewrite. Transformacja oparta na modelu składni i typów jest bezpieczniejsza od wyrażenia regularnego, ale nadal wymaga:

- jawnych warunków wejściowych,
- przykładów `before` i `after`,
- przypadków, których nie wolno zmienić,
- kompletnej ścieżki klas,
- przebiegu próbnego i przeglądu wygenerowanej łatki,
- kompilacji oraz testów po zastosowaniu,
- oddzielenia wygenerowanego diffu od ręcznych poprawek.

Automatyzacja zwiększa spójność i zasięg. Zwiększa także zasięg błędu receptury, dlatego jej testy są częścią produktu.

### 5.6. Przykład bramki kompilatora

Polityka ostrzeżeń jest jawnym parametrem wywołania, a nie ukrytym stanem globalnym.

Plik `pl/training/module8/tooling/WarningPolicy.java`:

```java
package pl.training.module8.tooling;

public enum WarningPolicy {
    ALLOW_WARNINGS,
    TREAT_WARNINGS_AS_ERRORS
}
```

Wynik przechowuje strukturalne dane diagnostyczne. Lokalizowany komunikat kompilatora nie staje się częścią kontraktu testów.

Plik `pl/training/module8/tooling/CompilationDiagnostic.java`:

```java
package pl.training.module8.tooling;

import java.util.Objects;
import java.util.Optional;

import javax.tools.Diagnostic;

public record CompilationDiagnostic(
        Diagnostic.Kind kind,
        Optional<String> code,
        long lineNumber,
        long columnNumber) {

    public CompilationDiagnostic {
        Objects.requireNonNull(kind, "kind");
        code = Objects.requireNonNull(code, "code");
        if (code.filter(String::isBlank).isPresent()) {
            throw new IllegalArgumentException("code must not be blank");
        }
        if (lineNumber != Diagnostic.NOPOS && lineNumber < 1) {
            throw new IllegalArgumentException(
                    "lineNumber must be Diagnostic.NOPOS or positive");
        }
        if (columnNumber != Diagnostic.NOPOS && columnNumber < 1) {
            throw new IllegalArgumentException(
                    "columnNumber must be Diagnostic.NOPOS or positive");
        }
    }
}
```

Plik `pl/training/module8/tooling/CompilationResult.java`:

```java
package pl.training.module8.tooling;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import javax.tools.Diagnostic;

public record CompilationResult(
        boolean successful,
        List<CompilationDiagnostic> diagnostics,
        Set<String> generatedClassNames) {

    public CompilationResult {
        diagnostics = List.copyOf(Objects.requireNonNull(
                diagnostics, "diagnostics"));
        generatedClassNames = Collections.unmodifiableSet(new TreeSet<>(
                Objects.requireNonNull(
                        generatedClassNames, "generatedClassNames")));
    }

    public boolean hasDiagnostic(Diagnostic.Kind kind, String code) {
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(code, "code");

        return diagnostics.stream().anyMatch(diagnostic ->
                diagnostic.kind() == kind
                        && diagnostic.code().filter(code::equals).isPresent());
    }
}
```

`InMemoryJavaCompiler` używa `--release 25`, wyłącza procesory adnotacji w tym kontrolowanym przykładzie i włącza kategorię `rawtypes`. Własny file manager przechwytuje wynikowe klasy.

Plik `pl/training/module8/tooling/InMemoryJavaCompiler.java`:

```java
package pl.training.module8.tooling;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.lang.model.SourceVersion;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public final class InMemoryJavaCompiler {
    private static final List<String> BASE_OPTIONS = List.of(
            "--release", "25",
            "-proc:none",
            "-Xlint:rawtypes");

    public CompilationResult compile(
            String binaryName,
            String source,
            WarningPolicy warningPolicy) {

        validateBinaryName(binaryName);
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(warningPolicy, "warningPolicy");
        if (source.isBlank()) {
            throw new IllegalArgumentException("source must not be blank");
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException(
                    "System Java compiler is unavailable; run on a JDK");
        }

        var diagnostics = new DiagnosticCollector<JavaFileObject>();
        var sourceFile = new SourceFile(binaryName, source);
        List<String> options = compilerOptions(warningPolicy);

        StandardJavaFileManager standardFileManager =
                compiler.getStandardFileManager(
                        diagnostics, Locale.ROOT, UTF_8);
        try (var memoryFileManager =
                new MemoryFileManager(standardFileManager)) {

            boolean successful = Boolean.TRUE.equals(compiler.getTask(
                    null,
                    memoryFileManager,
                    diagnostics,
                    options,
                    null,
                    List.of(sourceFile)).call());

            List<CompilationDiagnostic> stableDiagnostics = diagnostics
                    .getDiagnostics()
                    .stream()
                    .map(diagnostic -> new CompilationDiagnostic(
                            diagnostic.getKind(),
                            Optional.ofNullable(diagnostic.getCode()),
                            diagnostic.getLineNumber(),
                            diagnostic.getColumnNumber()))
                    .toList();

            return new CompilationResult(
                    successful,
                    stableDiagnostics,
                    memoryFileManager.generatedClassNames());
        } catch (IOException failure) {
            throw new IllegalStateException(
                    "Could not close the in-memory compiler", failure);
        }
    }

    private static List<String> compilerOptions(WarningPolicy warningPolicy) {
        var options = new ArrayList<>(BASE_OPTIONS);
        if (warningPolicy == WarningPolicy.TREAT_WARNINGS_AS_ERRORS) {
            options.add("-Werror");
        }
        return List.copyOf(options);
    }

    private static void validateBinaryName(String binaryName) {
        Objects.requireNonNull(binaryName, "binaryName");
        if (!SourceVersion.isName(binaryName)) {
            throw new IllegalArgumentException(
                    "binaryName must be a valid Java binary name");
        }
    }

    private static final class SourceFile extends SimpleJavaFileObject {
        private final String source;

        private SourceFile(String binaryName, String source) {
            super(uriFor(binaryName, Kind.SOURCE), Kind.SOURCE);
            this.source = source;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return source;
        }
    }

    private static final class ClassFile extends SimpleJavaFileObject {
        private final ByteArrayOutputStream bytecode =
                new ByteArrayOutputStream();

        private ClassFile(String binaryName) {
            super(uriFor(binaryName, Kind.CLASS), Kind.CLASS);
        }

        @Override
        public OutputStream openOutputStream() {
            bytecode.reset();
            return bytecode;
        }
    }

    private static final class MemoryFileManager
            extends ForwardingJavaFileManager<StandardJavaFileManager> {
        private final Map<String, ClassFile> generatedClasses =
                new LinkedHashMap<>();

        private MemoryFileManager(StandardJavaFileManager fileManager) {
            super(fileManager);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(
                JavaFileManager.Location location,
                String className,
                JavaFileObject.Kind kind,
                FileObject sibling) {

            if (kind != JavaFileObject.Kind.CLASS) {
                throw new IllegalArgumentException(
                        "Only in-memory class output is supported");
            }

            var classFile = new ClassFile(className);
            generatedClasses.put(className, classFile);
            return classFile;
        }

        private Set<String> generatedClassNames() {
            return Set.copyOf(generatedClasses.keySet());
        }
    }

    private static URI uriFor(
            String binaryName,
            JavaFileObject.Kind kind) {
        return URI.create("memory:///"
                + binaryName.replace('.', '/')
                + kind.extension);
    }
}
```

Test kompiluje to samo użycie surowego typu z dwiema politykami ostrzeżeń.

Plik `pl/training/module8/tooling/InMemoryJavaCompilerTest.java`:

```java
package pl.training.module8.tooling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.Set;

import javax.tools.Diagnostic;

import org.junit.jupiter.api.Test;

final class InMemoryJavaCompilerTest {
    private static final String CLEAN_SOURCE = """
            package example;

            import java.util.List;

            public final class TypedNames {
                public int count(List<?> names) {
                    return names.size();
                }
            }
            """;

    private static final String RAW_SOURCE = """
            package example;

            import java.util.List;

            public final class RawNames {
                public int count(List names) {
                    return names.size();
                }
            }
            """;

    private final InMemoryJavaCompiler compiler =
            new InMemoryJavaCompiler();

    @Test
    void compilesTypedSourceAndKeepsGeneratedBytecodeInMemory() {
        CompilationResult result = compiler.compile(
                "example.TypedNames",
                CLEAN_SOURCE,
                WarningPolicy.TREAT_WARNINGS_AS_ERRORS);

        assertTrue(result.successful());
        assertTrue(result.diagnostics().isEmpty());
        assertEquals(Set.of("example.TypedNames"),
                result.generatedClassNames());
    }

    @Test
    void reportsRawTypeByKindAndCompilerCodeWithoutRejectingSource() {
        CompilationResult result = compiler.compile(
                "example.RawNames",
                RAW_SOURCE,
                WarningPolicy.ALLOW_WARNINGS);

        assertTrue(result.successful());
        assertTrue(result.hasDiagnostic(
                Diagnostic.Kind.WARNING,
                "compiler.warn.raw.class.use"));
        assertEquals(Set.of("example.RawNames"),
                result.generatedClassNames());
    }

    @Test
    void rejectsTheSameRawTypeWhenWarningsAreErrors() {
        CompilationResult result = compiler.compile(
                "example.RawNames",
                RAW_SOURCE,
                WarningPolicy.TREAT_WARNINGS_AS_ERRORS);

        assertFalse(result.successful());
        assertTrue(result.hasDiagnostic(
                Diagnostic.Kind.WARNING,
                "compiler.warn.raw.class.use"));
        assertTrue(result.hasDiagnostic(
                Diagnostic.Kind.ERROR,
                "compiler.err.warnings.and.werror"));
    }

    @Test
    void validatesCompilationRequest() {
        assertThrows(NullPointerException.class,
                () -> compiler.compile(
                        null, CLEAN_SOURCE, WarningPolicy.ALLOW_WARNINGS));
        assertThrows(IllegalArgumentException.class,
                () -> compiler.compile(
                        "not a name", CLEAN_SOURCE,
                        WarningPolicy.ALLOW_WARNINGS));
        assertThrows(NullPointerException.class,
                () -> compiler.compile(
                        "example.TypedNames", null,
                        WarningPolicy.ALLOW_WARNINGS));
        assertThrows(IllegalArgumentException.class,
                () -> compiler.compile(
                        "example.TypedNames", "  \n",
                        WarningPolicy.ALLOW_WARNINGS));
        assertThrows(NullPointerException.class,
                () -> compiler.compile(
                        "example.TypedNames", CLEAN_SOURCE, null));
    }

    @Test
    void diagnosticsCanRepresentAnAbsentImplementationSpecificCode() {
        var diagnostic = new CompilationDiagnostic(
                Diagnostic.Kind.NOTE,
                Optional.empty(),
                Diagnostic.NOPOS,
                Diagnostic.NOPOS);

        assertEquals(Optional.empty(), diagnostic.code());
        assertThrows(
                NullPointerException.class,
                () -> new CompilationDiagnostic(
                        Diagnostic.Kind.NOTE,
                        null,
                        Diagnostic.NOPOS,
                        Diagnostic.NOPOS));
        assertThrows(
                IllegalArgumentException.class,
                () -> new CompilationDiagnostic(
                        Diagnostic.Kind.NOTE,
                        Optional.of(" "),
                        Diagnostic.NOPOS,
                        Diagnostic.NOPOS));
        assertThrows(
                IllegalArgumentException.class,
                () -> new CompilationDiagnostic(
                        Diagnostic.Kind.NOTE,
                        Optional.empty(),
                        0,
                        1));
        assertThrows(
                IllegalArgumentException.class,
                () -> new CompilationDiagnostic(
                        Diagnostic.Kind.NOTE,
                        Optional.empty(),
                        Diagnostic.NOPOS - 1,
                        1));
        assertThrows(
                IllegalArgumentException.class,
                () -> new CompilationDiagnostic(
                        Diagnostic.Kind.NOTE,
                        Optional.empty(),
                        1,
                        0));
        assertThrows(
                IllegalArgumentException.class,
                () -> new CompilationDiagnostic(
                        Diagnostic.Kind.NOTE,
                        Optional.empty(),
                        1,
                        Diagnostic.NOPOS - 1));
    }
}
```

Przykład korzysta wyłącznie z publicznego API `javax.tools`. Źródło i wynikowy kod bajtowy pozostają w pamięci. Publiczne API dopuszcza diagnostykę bez kodu, dlatego model używa `Optional<String>`. Testy sprawdzające konkretne kody są świadomie związane z systemowym `javac` z używanego JDK 25 i po zmianie kompilatora wymagają ponownej weryfikacji.

Ta bramka demonstruje konkretną własność: ostrzeżenie `rawtypes` może zostać zarejestrowane albo potraktowane jako błąd. Nie zastępuje Maven, pełnej ścieżki klas projektu, testów ani innych analizatorów.

### 5.7. Minimalna bramka jakości

Dla refaktoryzacji w Javie praktyczny ciąg kontroli obejmuje:

1. podgląd transformacji w IDE albo przebieg próbny receptury,
2. przegląd diffu,
3. czystą kompilację na docelowym JDK,
4. ostrzeżenia kompilatora,
5. testy zawężone do zmienionej granicy,
6. pełny zestaw testów,
7. skonfigurowane analizatory statyczne,
8. przegląd wykonany przez człowieka,
9. obserwację kontrolowanego wdrożenia.

Kolejność daje szybki feedback najpierw i kosztowniejszy później. W CI wszystkie wymagane bramki pozostają obowiązkowe niezależnie od wyniku lokalnego.

## 6. Zarządzanie ryzykiem

### 6.1. Ryzyko jest właściwością zmiany i kontekstu

Ta sama transformacja może być małym ryzykiem w narzędziu wewnętrznym i wysokim ryzykiem w ścieżce autoryzacji płatności. Ocena powinna uwzględniać:

- krytyczność funkcji i zasięg skutków,
- znajomość zachowania oraz jakość orakli testowych,
- zgodność danych i możliwość ich odtworzenia,
- transakcje, kolejność, współbieżność i idempotencję,
- bezpieczeństwo i wymagania regulacyjne,
- kompatybilność integracji,
- wydajność i wykorzystanie zasobów,
- obserwowalność oraz czas detekcji,
- odwracalność kodu, konfiguracji i danych,
- kompetencje, własność i dostępność zespołu.

Prosta macierz prawdopodobieństwa i wpływu pomaga uporządkować rozmowę, lecz nie tworzy obiektywnej precyzji. Najważniejsze są założenia, dowody, właściciel i konkretne mechanizmy kontroli.

### 6.2. Warstwy kontroli

| Ryzyko | Przykładowe zabezpieczenia |
| --- | --- |
| nieznane zachowanie | testy charakterystyki, kontraktowe i różnicowe |
| szeroki zasięg skutków | mała kohorta, deterministyczny routing, wdrożenie etapowe |
| niezgodność wyników | shadow dla czystej operacji, domenowa normalizacja, rekoncyliacja |
| awaria kandydata | izolacja, timeout, wynik kontrolny z legacy |
| regresja operacyjna | stan bazowy, SLI, SLO, panel metryk i alert |
| trudne wycofanie | zgodność wsteczna, przetestowany przełącznik, automatyzacja procedury |
| migracja danych | expand and contract, kopia, walidacja, jednoznaczne źródło prawdy |
| kod przejściowy | właściciel, termin i kryterium usunięcia |

Zabezpieczenie powinno odpowiadać mechanizmowi awarii. Więcej testów jednostkowych nie rozwiąże braku kompatybilności schematu, a flaga nie cofnie danych zapisanych w formacie niezrozumiałym dla starej wersji.

### 6.3. Kryteria przed wdrożeniem etapowym

Przed ekspozycją ruchu należy zdefiniować:

- populację kontrolną i kandydującą,
- minimalną reprezentatywną próbkę,
- metryki zgodności funkcjonalnej,
- bezwzględne SLO i porównanie ze stanem bazowym,
- czas obserwacji,
- kryteria przejścia do kolejnego etapu,
- warunki zatrzymania,
- automatyczne i ręczne warunki wycofania,
- właściciela decyzji i kanał eskalacji.

Nie istnieją uniwersalne progi. Dla obliczeń finansowych dopuszczalna różnica po ustalonej normalizacji może wynosić zero. Dla opóźnienia, zużycia zasobów albo systemu probabilistycznego potrzebny jest uzgodniony przedział. Progu nie należy dopasowywać dopiero po zobaczeniu niekorzystnych wyników.

### 6.4. Przykład jawnej polityki wdrożenia etapowego

Poniższa polityka dotyczy scenariusza z dokładnym kontraktem odpowiedzi, dlatego dopuszczalna liczba rozbieżności wynosi zero. Progi błędów, opóźnienia i liczebności próbki są przekazywane jako dane konfiguracyjne.

Plik `pl/training/module8/risk/RolloutDecision.java`:

```java
package pl.training.module8.risk;

public enum RolloutDecision {
    ADVANCE,
    HOLD,
    ROLLBACK
}
```

Plik `pl/training/module8/risk/RolloutThresholds.java`:

```java
package pl.training.module8.risk;

public record RolloutThresholds(
        long minimumSampleSize,
        double maximumErrorRate,
        double maximumP95LatencyMillis) {

    public RolloutThresholds {
        if (minimumSampleSize < 1) {
            throw new IllegalArgumentException(
                    "minimumSampleSize must be positive");
        }
        requireFiniteInRange(
                maximumErrorRate, 0.0, 1.0, "maximumErrorRate");
        requireFiniteInRange(
                maximumP95LatencyMillis,
                0.0,
                Double.MAX_VALUE,
                "maximumP95LatencyMillis");
    }

    private static void requireFiniteInRange(
            double value,
            double minimum,
            double maximum,
            String name) {
        if (!Double.isFinite(value) || value < minimum || value > maximum) {
            throw new IllegalArgumentException(
                    name + " must be finite and between "
                            + minimum + " and " + maximum);
        }
    }
}
```

Migawka nie pozwala utworzyć niemożliwych liczników ani pomiaru `NaN`. Dla pustej próbki definiuje wskaźnik błędów jako zero, ale polityka nadal zwróci `HOLD`.

Plik `pl/training/module8/risk/RolloutSnapshot.java`:

```java
package pl.training.module8.risk;

public record RolloutSnapshot(
        long sampleSize,
        long failedRequests,
        long mismatchedResponses,
        double p95LatencyMillis) {

    public RolloutSnapshot {
        if (sampleSize < 0) {
            throw new IllegalArgumentException(
                    "sampleSize must not be negative");
        }
        requireCountWithinSample(
                failedRequests, sampleSize, "failedRequests");
        requireCountWithinSample(
                mismatchedResponses, sampleSize, "mismatchedResponses");
        if (!Double.isFinite(p95LatencyMillis)
                || p95LatencyMillis < 0.0) {
            throw new IllegalArgumentException(
                    "p95LatencyMillis must be finite and not negative");
        }
        if (sampleSize == 0 && p95LatencyMillis != 0.0) {
            throw new IllegalArgumentException(
                    "an empty sample must have zero p95LatencyMillis");
        }
    }

    public double errorRate() {
        return sampleSize == 0
                ? 0.0
                : (double) failedRequests / sampleSize;
    }

    private static void requireCountWithinSample(
            long count,
            long sampleSize,
            String name) {
        if (count < 0 || count > sampleSize) {
            throw new IllegalArgumentException(
                    name + " must be between 0 and sampleSize");
        }
    }
}
```

Kontrole bezpieczeństwa są wykonywane przed oceną rozmiaru próbki. Mała próbka z potwierdzonym naruszeniem nie jest zdrową próbką czekającą na więcej danych.

Plik `pl/training/module8/risk/RolloutPolicy.java`:

```java
package pl.training.module8.risk;

import java.util.Objects;

public final class RolloutPolicy {
    private final RolloutThresholds thresholds;

    public RolloutPolicy(RolloutThresholds thresholds) {
        this.thresholds = Objects.requireNonNull(thresholds, "thresholds");
    }

    public RolloutDecision decide(RolloutSnapshot snapshot) {
        Objects.requireNonNull(snapshot, "snapshot");

        if (snapshot.mismatchedResponses() > 0
                || snapshot.errorRate() > thresholds.maximumErrorRate()
                || snapshot.p95LatencyMillis()
                        > thresholds.maximumP95LatencyMillis()) {
            return RolloutDecision.ROLLBACK;
        }
        if (snapshot.sampleSize() < thresholds.minimumSampleSize()) {
            return RolloutDecision.HOLD;
        }
        return RolloutDecision.ADVANCE;
    }
}
```

Plik `pl/training/module8/risk/RolloutPolicyTest.java`:

```java
package pl.training.module8.risk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

final class RolloutPolicyTest {
    private final RolloutPolicy policy = new RolloutPolicy(
            new RolloutThresholds(100, 0.05, 250.0));

    @Test
    void advancesWhenTheSampleAndMetricsMeetAllThresholds() {
        assertEquals(
                RolloutDecision.ADVANCE,
                policy.decide(new RolloutSnapshot(200, 4, 0, 180.0)));
    }

    @Test
    void advancesAtInclusiveMetricAndSampleBoundaries() {
        assertEquals(
                RolloutDecision.ADVANCE,
                policy.decide(new RolloutSnapshot(100, 5, 0, 250.0)));
    }

    @Test
    void holdsWhenAHealthySampleIsStillTooSmall() {
        assertEquals(
                RolloutDecision.HOLD,
                policy.decide(new RolloutSnapshot(99, 0, 0, 120.0)));
        assertEquals(
                RolloutDecision.HOLD,
                policy.decide(new RolloutSnapshot(0, 0, 0, 0.0)));
    }

    @Test
    void rollsBackAfterAnyBehaviorMismatch() {
        assertEquals(
                RolloutDecision.ROLLBACK,
                policy.decide(new RolloutSnapshot(200, 0, 1, 120.0)));
    }

    @Test
    void rollsBackAfterAnAbsoluteErrorOrLatencySloViolation() {
        assertEquals(
                RolloutDecision.ROLLBACK,
                policy.decide(new RolloutSnapshot(100, 6, 0, 200.0)));
        assertEquals(
                RolloutDecision.ROLLBACK,
                policy.decide(new RolloutSnapshot(100, 0, 0, 250.01)));
    }

    @Test
    void safetyViolationsTakePriorityOverAnInsufficientSample() {
        assertEquals(
                RolloutDecision.ROLLBACK,
                policy.decide(new RolloutSnapshot(10, 0, 1, 100.0)));
        assertEquals(
                RolloutDecision.ROLLBACK,
                policy.decide(new RolloutSnapshot(10, 1, 0, 100.0)));
        assertEquals(
                RolloutDecision.ROLLBACK,
                policy.decide(new RolloutSnapshot(10, 0, 0, 251.0)));
    }

    @Test
    void rejectsMissingPolicyInputs() {
        assertThrows(NullPointerException.class,
                () -> new RolloutPolicy(null));
        assertThrows(NullPointerException.class,
                () -> policy.decide(null));
    }
}
```

`HOLD` nie oznacza sukcesu ani porażki. Oznacza brak wystarczających danych do zwiększenia ekspozycji. `ROLLBACK` ma pierwszeństwo, gdy naruszono uzgodnione bezpieczeństwo, zgodność albo bezwzględne SLO. Podobnie złe wyniki legacy i kandydata nie usprawiedliwiają przejścia dalej, jeśli oba naruszają wymaganie użytkownika.

### 6.5. Dane i wycofanie

Wycofanie pliku `.jar` jest proste tylko wtedy, gdy nowa wersja nie pozostawiła niezgodnego stanu. Migracja danych powinna preferować sekwencję rozszerz i zwęź, znaną jako expand and contract:

1. dodaj strukturę zgodną ze starą i nową wersją,
2. wdróż kod potrafiący pracować w okresie przejściowym,
3. przenieś lub uzupełnij dane z kontrolą postępu,
4. przełącz odczyty i zweryfikuj zgodność,
5. zatrzymaj stare zapisy,
6. usuń stare pola lub tabele dopiero po zamknięciu okna wycofania.

Dual write wymaga jawnej strategii dla częściowego niepowodzenia, ponowienia, kolejności i rekoncyliacji. Samo zapisanie do dwóch systemów nie zapewnia spójności.

Próba wycofania powinna potwierdzać nie tylko działanie przełącznika, lecz także:

- czas powrotu,
- zgodność danych po powrocie,
- zachowanie żądań będących w toku,
- brak powtórzenia nieidempotentnych efektów,
- dostępność dashboardów i procedury eskalacji.

### 6.6. Zamknięcie migracji

Migracja kończy się po usunięciu kosztu legacy, nie po pierwszym sukcesie nowej ścieżki. Kryteria zamknięcia mogą obejmować:

- pełny ruch na nowej implementacji przez uzgodniony okres,
- brak niewyjaśnionych rozbieżności,
- dotrzymanie SLO i budżetu kosztowego,
- zakończoną migrację oraz rekoncyliację danych,
- potwierdzoną gotowość operacyjną,
- usunięcie starego kodu, flag, adapterów i dashboardów przejściowych,
- aktualizację dokumentacji stanu obecnego,
- zapisanie wniosków z incydentów i wdrożeń etapowych.

Procent przepisanych linii nie mierzy wartości ani redukcji ryzyka. Lepszymi sygnałami są przeniesione funkcje biznesowe, usunięte zależności, wyłączone koszty oraz skrócony czas bezpiecznej zmiany.

## Warsztat 1. Plan przyrostowej wymiany implementacji

### Cel

Zaplanować migrację kalkulatora używanego przez krytyczną ścieżkę bez jednorazowego przełączenia.

### Zadanie

1. Zapisz funkcjonalny i operacyjny kontrakt starej implementacji.
2. Wskaż najmniejszą abstrakcję potrzebną klientowi.
3. Zaprojektuj adapter oraz testy kontraktowe.
4. Określ, czy tryb shadow jest bezpieczny.
5. Zdefiniuj kohorty i etapy przełączenia.
6. Zapisz kryteria `ADVANCE`, `HOLD` i `ROLLBACK`.
7. Zdefiniuj warunki usunięcia starej ścieżki.

### Kryteria akceptacji

- plan rozróżnia refaktoryzację od migracji i zmiany zachowania,
- kandydat nie powiela nieidempotentnych efektów,
- obie implementacje przechodzą wspólny test kontraktowy,
- routing jest deterministyczny i obserwowalny,
- wycofanie uwzględnia kod, konfigurację i dane,
- każdy element przejściowy ma właściciela oraz kryterium usunięcia.

## Warsztat 2. Seria zmian gotowych do przeglądu

### Cel

Podzielić szeroką zmianę legacy na samodzielne jednostki o czytelnej intencji.

### Zadanie

1. Oddziel odkrycie zachowania od przebudowy struktury.
2. Oddziel zmianę zachowania od refaktoryzacji.
3. Umieść powiązane testy w odpowiednich zestawach zmian.
4. Zapewnij zielony build po każdym kroku.
5. Przygotuj opis zakresu, dowodów i ryzyka.
6. Wskaż kolejność czytania dla osoby przeglądającej.

### Kryteria akceptacji

- każdy zestaw zmian ma jedną główną intencję,
- system działa po integracji każdego kroku,
- testy nie utrwalają bezkrytycznie przypadkowego zachowania,
- mechaniczne i ręczne zmiany są rozdzielone,
- opis pozwala odtworzyć sposób weryfikacji,
- komentarze z przeglądu rozróżniają blokadę od sugestii.

## Warsztat 3. Wprowadzenie bramek do projektu legacy

### Cel

Zwiększyć jakość automatycznej informacji zwrotnej bez blokowania zespołu historycznymi naruszeniami.

### Zadanie

1. Uruchom ostrzeżenia kompilatora i analizę statyczną bez modyfikowania kodu.
2. Sklasyfikuj wyniki według ryzyka i pewności.
3. Zapisz stan bazowy istniejących naruszeń.
4. Włącz bramkę dla nowego oraz dotykanego kodu.
5. Ustal format precyzyjnego tłumienia i termin przeglądu wyjątków.
6. Zaplanuj stopniowe zmniejszanie stanu bazowego naruszeń.

### Kryteria akceptacji

- użyte wersje narzędzi obsługują Javę 25,
- analiza otrzymuje kompletną ścieżkę klas,
- build nie generuje niezwiązanych automatycznych poprawek,
- wynik fałszywie dodatni jest odróżniony od zaakceptowanego ryzyka,
- nowe naruszenia nie powiększają stanu bazowego,
- raport jest dostępny w CI i ma właściciela.

## Lista kontrolna

### Strategia

- Czy cel modernizacji jest mierzalny?
- Czy rodzaj zmiany został nazwany poprawnie?
- Czy można zmniejszyć granicę pierwszego przyrostu?
- Czy stara i nowa ścieżka mogą bezpiecznie współistnieć?
- Czy architektura przejściowa ma kryterium usunięcia?

### Zespół i przegląd

- Czy zestaw zmian ma jedną główną intencję?
- Czy zachowuje działający build?
- Czy testy stanowią wiarygodny dowód, a nie tylko pokrycie linii?
- Czy opis zawiera zakres, elementy poza zakresem, wdrożenie etapowe i wycofanie?
- Czy właściwi właściciele zostali włączeni odpowiednio wcześnie?

### Dokumentacja

- Czy informacja znajduje się w artefakcie o właściwej trwałości?
- Czy komentarze wyjaśniają przyczynę, a nie historię edycji?
- Czy istotna decyzja zawiera alternatywy i konsekwencje?
- Czy dokumentacja operacyjna opisuje stan obecny?
- Czy decyzja zastąpiona pozostaje dostępna jako historia?

### Narzędzia

- Czy IDE widzi wszystkie statyczne użycia i czy sprawdzono użycia dynamiczne?
- Czy kompilacja faktycznie używa JDK 25?
- Czy analizator obsługuje używaną składnię i kod bajtowy?
- Czy wyłączenia reguł są wąskie i uzasadnione?
- Czy automatyczny diff jest oddzielony od zmian ręcznych?

### Ryzyko

- Czy ustalono stan bazowy, SLI i bezwzględne SLO?
- Czy próbka jest reprezentatywna?
- Czy kryteria zatrzymania powstały przed wdrożeniem etapowym?
- Czy wycofanie zostało przećwiczone z uwzględnieniem danych?
- Czy zespół wie, kto podejmuje decyzję podczas incydentu?

## Podsumowanie

Skuteczna refaktoryzacja legacy jest procesem zdobywania informacji w kontrolowanych krokach. Kod, testy, narzędzia, przegląd, dokumentacja i wdrożenie etapowe tworzą jeden system bezpieczeństwa. Żaden z tych elementów samodzielnie nie gwarantuje poprawności.

Najważniejsze zasady modułu:

- refaktoryzacja, migracja i zmiana zachowania wymagają innych dowodów,
- sposób dostarczenia jest niezależny od rodzaju zmiany,
- małe przepisanie komponentu może być rozsądne, ale szeroka migracja typu big bang zwykle usuwa możliwość wczesnej nauki,
- Boy Scout Rule dotyczy małych, lokalnych i weryfikowalnych ulepszeń,
- jedna główna intencja ułatwia przegląd, wycofanie i diagnozę,
- dokument należy dobrać do odbiorcy oraz trwałości informacji,
- narzędzia semantyczne zmniejszają ryzyko, lecz mają granice widoczności,
- progi wdrożenia etapowego wynikają z konkretnego kontraktu i SLO, a nie z uniwersalnej recepty,
- migracja kończy się dopiero po usunięciu starego kosztu i architektury przejściowej.
