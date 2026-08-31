# Moduł 6. Refaktoryzacje do wzorców projektowych - warsztat praktyczny

## Zakres

1. Wzorzec jako rezultat refaktoryzacji
2. Replace Conditional Logic with Strategy
3. Replace Conditional with Polymorphism
4. Replace Type Code with Class
5. Encapsulate Composite with Builder
6. Encapsulate Classes with Factory i Extract Factory Class
7. Move Embellishment to Decorator
8. Replace State-Altering Conditionals with State
9. Replace Hard-coded Notifications with Observer
10. Replace Implicit Tree with Composite
11. Unify Interfaces with Adapter
12. Replace Conditional Dispatcher with Command
13. Apply Template Method
14. Replace Distinctions with Composite
15. Limit Instantiation with Singleton
16. Move Accumulation to Collecting Parameter i Visitor
17. Extract Composite
18. Warsztat praktyczny

## 1. Wzorzec jako rezultat refaktoryzacji

### 1.1. Najpierw rodzaj zmienności

Ta sama instrukcja `switch` może sygnalizować różne problemy:

| Pytanie | Typowa technika |
| --- | --- |
| Czy zmienia się algorytm wybierany dla operacji? | Strategy |
| Czy zachowanie należy do trwałego rodzaju obiektu? | polimorfizm podtypów |
| Czy zachowanie zależy od bieżącego stanu i przejść? | State |
| Czy selektor reprezentuje żądanie kierowane do wykonawcy? | Command |
| Czy kod jedynie tworzy właściwy obiekt? | Factory |

Obecność warunku nie wystarcza do wyboru wzorca. Najpierw należy ustalić, co rzeczywiście jest zmienne, kto powinien znać wariant oraz czy zestaw wariantów jest otwarty czy zamknięty.

### 1.2. Refaktoryzacja a zmiana projektu

Refaktoryzacja zachowuje obserwowalne zachowanie w przyjętej granicy. Wprowadzenie wzorca często kusi, aby jednocześnie:

- dodać nowe warianty,
- zmienić obsługę nieznanego kodu,
- przestawić operację z synchronicznej na asynchroniczną,
- zacząć kontynuować po błędzie listenera,
- dopuścić dowolną liczbę odbiorców,
- wprowadzić cache albo współdzielenie instancji,
- zmienić format trwałych danych.

Każdy taki krok może być wartościowy, ale jest rozszerzeniem zachowania. Należy oddzielić go od transformacji strukturalnej i zatwierdzić osobno.

### 1.3. Co trzeba zachować

Test porównujący tylko wartość zwracaną jest często niewystarczający. Dla wzorców z tego modułu należy rozważyć:

- dokładny typ wyjątku i, jeśli jest kontraktem, jego komunikat,
- stan obiektu po sukcesie i po błędzie,
- liczbę i kolejność efektów ubocznych,
- moment wyboru wariantu i odczytu konfiguracji,
- wątek wykonania callbacku,
- kolejność odbiorców i politykę błędów,
- tożsamość obiektu przekazanego do callbacku,
- zachowanie dla `null`, wartości nieznanej i gałęzi domyślnej,
- atomowość przejścia oraz używany monitor,
- zgodność danych, refleksji i publicznego API.

### 1.4. Bezpieczna pętla pracy

1. Nazwij konkretny problem w aktualnym kodzie.
2. Zapisz granicę obserwowalnego zachowania.
3. Dodaj test charakterystyki dla każdej gałęzi i awarii.
4. Wprowadź minimalny kontrakt, początkowo delegujący do starego kodu.
5. Przenoś jedną gałąź, odpowiedzialność albo efekt naraz.
6. Uruchamiaj mały zestaw testów po każdym ruchu.
7. Usuń stary warunek lub klasę dopiero po migracji wszystkich klientów.
8. W osobnym kroku oceń uproszczenie i usuń niepotrzebne elementy wzorca.

### 1.5. Istotne mechanizmy Javy 25

Interfejs funkcyjny umożliwia użycie lambdy, ale nie może być zadeklarowany jako `sealed`. Typ `sealed` jest dobry dla świadomie zamkniętego modelu domenowego, nie dla otwartego punktu wstrzykiwania strategii.

Lambda nie zapewnia stabilnej tożsamości. Nie należy porównywać jej przez `==`, używać jako monitora ani usuwać listenera przez utworzenie pozornie takiej samej lambdy. Wymóg efektywnej finalności dotyczy przechwytywanej zmiennej lokalnej, a nie niemutowalności wskazanego obiektu.

Rekord jest płytko niemutowalny. Komponent referencyjny rekordu może nadal wskazywać na mutowalną listę. Konstruktor `DeploymentGroup` używa dlatego `List.copyOf`.

`List.copyOf` i `Map.copyOf` tworzą niemodyfikowalne reprezentacje oraz odrzucają elementy lub wpisy `null`. Nie gwarantują nowej tożsamości obiektu kolekcji. Nie należy używać ich jako bariery przed mutacją samych elementów.

Typy `sealed` nie czynią obiektów niemutowalnymi ani bezpiecznymi wątkowo. `volatile` zapewnia widoczność pojedynczej referencji, ale nie atomowość całej sekwencji przejścia stanu.

## 2. Replace Conditional Logic with Strategy

### 2.1. Intencja

Strategy zastępuje warunek wybierający jeden z kilku wariantów algorytmu. Kontekst zachowuje odpowiedzialność za proces i walidację wspólną, a obliczenie deleguje do wstrzykniętej polityki.

Dobry kandydat:

- zawiera kilka wariantów tego samego obliczenia,
- wariant może być wybrany niezależnie od klasy obiektu domenowego,
- nowe warianty pojawiają się częściej niż zmienia się proces otaczający,
- test algorytmu nie wymaga całego kontekstu,
- wybór może zostać wykonany na granicy składania aplikacji.

Strategy nie jest uzasadniona wyłącznie dlatego, że metoda zawiera `if`. Dla jednego prostego i stabilnego warunku dodatkowy interfejs może zmniejszyć czytelność.

### 2.2. Stan przed zmianą

Plik `pl/training/module6/strategy/before/LegacyDeploymentCostCalculator.java`:

```java
package pl.training.module6.strategy.before;

import java.util.Objects;

public final class LegacyDeploymentCostCalculator {
    public long calculate(long baseCostInCents, DeploymentMode mode) {
        if (baseCostInCents < 0) {
            throw new IllegalArgumentException("base cost must not be negative");
        }

        return switch (Objects.requireNonNull(mode, "mode")) {
            case STANDARD -> baseCostInCents;
            case EXPEDITED -> Math.addExact(baseCostInCents, baseCostInCents / 4);
        };
    }

    public enum DeploymentMode {
        STANDARD,
        EXPEDITED
    }
}
```

Warunek jest lokalny, więc na tym etapie nie jest jeszcze problemem sam w sobie. Przyjmujemy jednak, że warianty kosztu są niezależnie rozwijanymi politykami. Walidacja nieujemnej wartości pozostaje wspólnym kontraktem kalkulatora.

### 2.3. Stan po zmianie

Plik `pl/training/module6/strategy/after/DeploymentCostPolicy.java`:

```java
package pl.training.module6.strategy.after;

@FunctionalInterface
public interface DeploymentCostPolicy {
    long calculate(long baseCostInCents);
}
```

Plik `pl/training/module6/strategy/after/DeploymentCostCalculator.java`:

```java
package pl.training.module6.strategy.after;

import java.util.Objects;

public final class DeploymentCostCalculator {
    private final DeploymentCostPolicy policy;

    public DeploymentCostCalculator(DeploymentCostPolicy policy) {
        this.policy = Objects.requireNonNull(policy, "policy");
    }

    public long calculate(long baseCostInCents) {
        if (baseCostInCents < 0) {
            throw new IllegalArgumentException("base cost must not be negative");
        }
        return policy.calculate(baseCostInCents);
    }
}
```

Konkretne strategie są bezstanowe. Można je bezpiecznie współdzielić:

```java
package pl.training.module6.strategy.after;

public final class ExpeditedCostPolicy implements DeploymentCostPolicy {
    @Override
    public long calculate(long baseCostInCents) {
        return Math.addExact(baseCostInCents, baseCostInCents / 4);
    }
}
```

### 2.4. Bezpieczna sekwencja

1. Zapisz tabelę decyzji, w tym `null`, przypadek nieznany i przepełnienie.
2. Wydziel całe obliczenie do metody bez zmiany warunku.
3. Wprowadź interfejs strategii o identycznym kontrakcie wyniku i wyjątków.
4. Dodaj strategię przejściową zawierającą jeszcze stary warunek.
5. Przenoś poszczególne gałęzie do osobnych strategii.
6. Przenieś wybór polityki do fabryki lub korzenia kompozycji.
7. Usuń stary selektor dopiero po migracji wszystkich wywołań.

Wybór strategii w konstruktorze zamraża go na czas życia kalkulatora. Nie jest to równoważne ze starym kodem, jeżeli warunek odczytywał zmienną konfigurację przy każdym wywołaniu. W takim przypadku trzeba zachować moment wyboru albo jawnie zatwierdzić nowy cykl życia.

Standardowe `Function` i `ToLongFunction` nie deklarują wyjątków kontrolowanych. Jeżeli stary algorytm je zgłasza, własny interfejs strategii powinien zachować odpowiednią klauzulę `throws`.

## 3. Replace Conditional with Polymorphism

### 3.1. Kiedy zachowanie należy do rodzaju obiektu

Polimorfizm jest właściwy, gdy selektor opisuje stabilny rodzaj obiektu, a zachowanie naturalnie należy do tego rodzaju. Klasa istniejącej instancji nie zmienia się podczas jej życia. Jeżeli zmienność jest przejściowym trybem, należy rozważyć State. Jeżeli klient wybiera algorytm niezależnie od rodzaju obiektu, lepsza może być Strategy.

Przed zmianą trzeba znaleźć wszystkie miejsca tworzenia, deserializacji, mapowania ORM i odtwarzania historycznego kodu typu. Samo usunięcie centralnego `switch` nie wystarczy, jeżeli podobne rozgałęzienia pozostają w innych usługach.

### 3.2. Stan przed zmianą

Plik `pl/training/module6/polymorphism/before/LegacyDeploymentStep.java`:

```java
package pl.training.module6.polymorphism.before;

import java.util.Objects;

public record LegacyDeploymentStep(Kind kind, String value) {
    public LegacyDeploymentStep {
        Objects.requireNonNull(kind, "kind");
        if (value == null || value.isBlank()) {
            String field = kind == Kind.SCRIPT ? "command" : "approver";
            throw new IllegalArgumentException(field + " must not be blank");
        }
    }

    public static LegacyDeploymentStep script(String command) {
        return new LegacyDeploymentStep(Kind.SCRIPT, command);
    }

    public static LegacyDeploymentStep approval(String approver) {
        return new LegacyDeploymentStep(Kind.APPROVAL, approver);
    }

    public String execute() {
        return switch (kind) {
            case SCRIPT -> "executed:" + value;
            case APPROVAL -> "approved-by:" + value;
        };
    }

    public enum Kind {
        SCRIPT,
        APPROVAL
    }
}
```

Pole `value` ma znaczenie zależne od `kind`. Taki model dopuszcza konstrukcję reprezentacji, której poprawność trzeba odtwarzać warunkami.

### 3.3. Stan po zmianie

Plik `pl/training/module6/polymorphism/after/DeploymentStep.java`:

```java
package pl.training.module6.polymorphism.after;

public sealed interface DeploymentStep permits ScriptStep, ApprovalStep {
    String execute();
}
```

Plik `pl/training/module6/polymorphism/after/ScriptStep.java`:

```java
package pl.training.module6.polymorphism.after;

public record ScriptStep(String command) implements DeploymentStep {
    public ScriptStep {
        if (command == null || command.isBlank()) {
            throw new IllegalArgumentException("command must not be blank");
        }
    }

    @Override
    public String execute() {
        return "executed:" + command;
    }
}
```

`ApprovalStep` realizuje ten sam kontrakt i posiada własne poprawne dane. Interfejs jest `sealed`, ponieważ przykład świadomie modeluje zamknięty zestaw rodzajów. W systemie wtyczek takie zamknięcie byłoby nieodpowiednie.

### 3.4. Sekwencja migracji

1. Scharakteryzuj każdą gałąź, w tym zachowanie domyślne.
2. Zamknij tworzenie obiektów za kontrolowaną granicą.
3. Wprowadź wspólną operację w nadtypie.
4. Utwórz pierwszy podtyp i skieruj do niego tylko odpowiedni punkt tworzenia.
5. Przenieś jedną gałąź do metody podtypu.
6. Powtórz dla pozostałych wariantów.
7. Usuń kod typu dopiero wtedy, gdy nie steruje już inną osią zachowania.
8. Uruchom jeden test kontraktowy dla wszystkich implementacji.

Dynamiczna dyspozycja dotyczy zwykłych metod instancyjnych. Pola nie są polimorficzne, metody `static` są ukrywane, `private` nie są nadpisywane, a `final` nie mogą być nadpisane. Nowy hook nie powinien być wywoływany z konstruktora klasy bazowej, ponieważ może trafić do częściowo zainicjalizowanego podtypu.

Dodanie nowego dozwolonego podtypu do hierarchii `sealed` może ujawnić stare, wyczerpujące instrukcje `switch`. Trzeba znaleźć je także poza bieżącym modułem i przetestować z nowym wariantem.

## 4. Replace Type Code with Class

### 4.1. Intencja

Surowy `String`, liczba albo znak reprezentujący pojęcie domenowe nie może sam pilnować poprawnych wartości, normalizacji i operacji związanych z tym pojęciem. Replace Type Code with Class tworzy typ, który przejmuje ten kontrakt.

Technika nie oznacza automatycznie tworzenia podklas. Jeżeli kod służy jedynie jako wartość z nazwanymi wariantami, wystarczy klasa wartości albo enum. Gdy kod steruje zachowaniem trwałych rodzajów, kolejnym krokiem może być polimorfizm. Gdy steruje przejściowym trybem, kandydatem jest State.

### 4.2. Stan przed zmianą

Plik `pl/training/module6/typecode/before/LegacyDeploymentRequest.java`:

```java
package pl.training.module6.typecode.before;

import java.util.Locale;

public record LegacyDeploymentRequest(String releaseId, String zoneCode) {
    public LegacyDeploymentRequest {
        if (releaseId == null || releaseId.isBlank()) {
            throw new IllegalArgumentException("releaseId must not be blank");
        }
        if (zoneCode == null || zoneCode.isBlank()) {
            throw new IllegalArgumentException("zoneCode must not be blank");
        }
        zoneCode = zoneCode.toUpperCase(Locale.ROOT);
        if (!zoneCode.equals("TEST") && !zoneCode.equals("PROD") && !zoneCode.equals("DR")) {
            throw new IllegalArgumentException("unknown zone code: " + zoneCode);
        }
    }

    public boolean requiresApproval() {
        return zoneCode.equals("PROD") || zoneCode.equals("DR");
    }
}
```

Znaczenie wartości i reguła zatwierdzenia są rozproszone wokół `String`. Każdy klient może ponownie zaimplementować inne reguły wielkości liter, nieznanych kodów i `null`.

### 4.3. Obiekt typu

Plik `pl/training/module6/typecode/after/DeploymentZone.java`:

```java
package pl.training.module6.typecode.after;

import java.util.Locale;
import java.util.Map;

public final class DeploymentZone {
    public static final DeploymentZone TEST = new DeploymentZone("TEST", false);
    public static final DeploymentZone PRODUCTION = new DeploymentZone("PROD", true);
    public static final DeploymentZone DISASTER_RECOVERY = new DeploymentZone("DR", true);

    private static final Map<String, DeploymentZone> BY_CODE = Map.of(
            TEST.code, TEST,
            PRODUCTION.code, PRODUCTION,
            DISASTER_RECOVERY.code, DISASTER_RECOVERY);

    private final String code;
    private final boolean approvalRequired;

    private DeploymentZone(String code, boolean approvalRequired) {
        this.code = code;
        this.approvalRequired = approvalRequired;
    }

    public static DeploymentZone fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("zoneCode must not be blank");
        }
        String normalized = code.toUpperCase(Locale.ROOT);
        DeploymentZone zone = BY_CODE.get(normalized);
        if (zone == null) {
            throw new IllegalArgumentException("unknown zone code: " + normalized);
        }
        return zone;
    }

    public String code() {
        return code;
    }

    public boolean requiresApproval() {
        return approvalRequired;
    }

    @Override
    public String toString() {
        return code;
    }
}
```

Konstruktor prywatny i fabryka kanonizują trzy znane instancje. W tym przykładzie tożsamość referencyjna stałych jest świadomą częścią modelu. Jeżeli instancje mogą powstawać niezależnie, klasa wartości musi również zdefiniować `equals` i `hashCode`, albo zostać rekordem.

Enum byłby dobrym rozwiązaniem dla zamkniętego, prostego zestawu. Osobna klasa jest przydatna, gdy potrzebna jest kontrolowana migracja kodów zewnętrznych, aliasy, własna polityka tworzenia albo możliwość późniejszego rozdzielenia kodu trwałego od implementacji.

### 4.4. Granica trwałości

W bazie danych i komunikacie może pozostać stabilny kod `PROD`. Wewnątrz aplikacji należy mapować go przez `DeploymentZone.fromCode`. Nie należy zapisywać nazwy klasy implementacyjnej ani wyniku `toString`, jeśli metoda nie jest jawnie częścią formatu.

Bezpieczna migracja zachowuje:

- normalizację regionalnie niezależną przez `Locale.ROOT`,
- obsługę `null` i pustego kodu,
- zachowanie kodu nieznanego,
- dokładny kod zapisywany na granicy,
- reguły równości używane w mapach i zbiorach,
- zgodność historycznych danych.

## 5. Encapsulate Composite with Builder

### 5.1. Problem konstrukcji drzewa

Composite upraszcza używanie pojedynczego elementu i grupy, ale jego ręczne tworzenie może nadal być powtarzalne oraz podatne na błędy. Klient musi znać kolejność tworzenia węzłów, zasady dodawania dzieci i moment zamknięcia struktury.

Builder jest uzasadniony, gdy budowanie Composite jest skomplikowane, wieloetapowe albo ma własne niezmienniki. Nie powinien powielać całej logiki drzewa ani udostępniać częściowo zbudowanego wyniku.

### 5.2. Builder planu wdrożenia

Plik `pl/training/module6/composite/after/PlanBuilder.java`:

```java
package pl.training.module6.composite.after;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class PlanBuilder {
    private final String name;
    private final List<PlanComponent> components = new ArrayList<>();
    private boolean built;

    private PlanBuilder(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        this.name = name;
    }

    public static PlanBuilder group(String name) {
        return new PlanBuilder(name);
    }

    public PlanBuilder task(String name, long minutes) {
        ensureOpen();
        components.add(new DeploymentTask(name, minutes));
        return this;
    }

    public PlanBuilder group(String name, Consumer<PlanBuilder> definition) {
        ensureOpen();
        PlanBuilder child = group(name);
        Objects.requireNonNull(definition, "definition").accept(child);
        DeploymentGroup builtChild = child.build();
        ensureOpen();
        components.add(builtChild);
        return this;
    }

    public PlanBuilder add(PlanComponent component) {
        ensureOpen();
        components.add(Objects.requireNonNull(component, "component"));
        return this;
    }

    public DeploymentGroup build() {
        ensureOpen();
        built = true;
        return new DeploymentGroup(name, components);
    }

    private void ensureOpen() {
        if (built) {
            throw new IllegalStateException("builder has already been used");
        }
    }
}
```

Użycie:

```java
DeploymentGroup plan = PlanBuilder.group("release")
        .group("database", group -> group
                .task("backup", 5)
                .task("migrate", 8))
        .task("deploy", 3)
        .build();
```

Builder jest jednorazowy. `build()` tworzy `DeploymentGroup`, którego konstruktor wykonuje kopię listy. Późniejsza mutacja wewnętrznego bufora Buildera nie może zmienić gotowego planu.

Przekazanie lambdy definiującej grupę jest wygodne, ale ma kontrakt synchroniczny. Zachowanie lambdy do późniejszego wykonania, uruchomienie jej w innym wątku albo ponowne wywołanie byłoby zmianą zachowania.

### 5.3. Warunki bezpieczeństwa

Przed wprowadzeniem Buildera trzeba zdecydować:

- czy pusty Composite jest poprawny,
- czy nazwy dzieci muszą być unikalne,
- czy kolejność dzieci jest obserwowalna,
- czy węzeł może należeć do kilku rodziców,
- czy struktura ma być drzewem, czy dopuszcza graf skierowany,
- jak wykrywane są cykle,
- czy Builder można wykorzystać ponownie,
- czy częściowy wynik może opuścić Builder.

W przykładzie typy są niemutowalne, a Builder konstruuje nowe dzieci od dołu, dlatego przez publiczny scenariusz Buildera nie powstaje cykl. Publiczny konstruktor `DeploymentGroup` pozwala jednak współdzielić już istniejący podplan. Wtedy model może być skierowanym grafem acyklicznym, a akumulacja policzy współdzielony węzeł dla każdej ścieżki. Jeśli domena wymaga ścisłego drzewa, własność rodzica trzeba egzekwować jawnie.

## 6. Encapsulate Classes with Factory i Extract Factory Class

### 6.1. Dwie powiązane transformacje

Encapsulate Classes with Factory ukrywa klasy konkretne należące do wspólnej rodziny. Klient otrzymuje obiekt przez fabrykę i zależy od interfejsu.

Extract Factory Class wydziela tworzenie z klasy, która miesza konstrukcję z inną odpowiedzialnością. Sama fabryka nie powinna przejmować późniejszego cyklu życia, logiki biznesowej i używania produktu, jeżeli nie są częścią kontraktu tworzenia.

### 6.2. Stan przed zmianą

Klienci mogą bezpośrednio utworzyć `HttpProbe` albo `QueueProbe`. Znają klasy konkretne, ich konstruktory i szczegóły pakietu:

```java
package pl.training.module6.factory.before;

public record HttpProbe(String endpoint) implements DeploymentProbe {
    public HttpProbe {
        if (endpoint == null || endpoint.isBlank()) {
            throw new IllegalArgumentException("endpoint must not be blank");
        }
    }

    @Override
    public String check() {
        return "http-ok:" + endpoint;
    }
}
```

`LegacyProbeService` zawiera dodatkowo wiedzę o wyborze i konstrukcji sondy, mimo że jego właściwym zadaniem jest jej uruchomienie:

```java
package pl.training.module6.factory.before;

import java.util.Objects;

public final class LegacyProbeService {
    public String check(ProbeKind kind, String target) {
        DeploymentProbe probe = switch (Objects.requireNonNull(kind, "kind")) {
            case HTTP -> new HttpProbe(target);
            case QUEUE -> new QueueProbe(target);
        };
        return probe.check();
    }

    public enum ProbeKind {
        HTTP,
        QUEUE
    }
}
```

### 6.3. Fabryka jako publiczna granica

Plik `pl/training/module6/factory/after/DeploymentProbeFactory.java`:

```java
package pl.training.module6.factory.after;

import java.util.Objects;

public final class DeploymentProbeFactory {
    public DeploymentProbe create(ProbeKind kind, String target) {
        Objects.requireNonNull(kind, "kind");
        if (target == null || target.isBlank()) {
            String field = kind == ProbeKind.HTTP ? "endpoint" : "queueName";
            throw new IllegalArgumentException(field + " must not be blank");
        }

        return switch (kind) {
            case HTTP -> new HttpProbe(target);
            case QUEUE -> new QueueProbe(target);
        };
    }

    public enum ProbeKind {
        HTTP,
        QUEUE
    }

    private record HttpProbe(String endpoint) implements DeploymentProbe {
        @Override
        public String check() {
            return "http-ok:" + endpoint;
        }
    }

    private record QueueProbe(String queueName) implements DeploymentProbe {
        @Override
        public String check() {
            return "queue-ok:" + queueName;
        }
    }
}
```

Implementacje są prywatne dla fabryki, więc klient może polegać wyłącznie na `DeploymentProbe`. Fabryka zawiera jeszcze `switch`, ale jest to właściwa granica wiedzy o konstrukcji. Usuwanie każdego warunku nie jest celem.

Po Extract Factory Class serwis otrzymuje fabrykę jako zależność i nie zna konstruktorów produktów:

```java
package pl.training.module6.factory.after;

import java.util.Objects;

import pl.training.module6.factory.after.DeploymentProbeFactory.ProbeKind;

public final class ProbeService {
    private final DeploymentProbeFactory factory;

    public ProbeService(DeploymentProbeFactory factory) {
        this.factory = Objects.requireNonNull(factory, "factory");
    }

    public String check(ProbeKind kind, String target) {
        return factory.create(kind, target).check();
    }
}
```

### 6.4. Procedura i pułapki

1. Zinwentaryzuj wszystkie konstruktory, refleksję, DI i deserializatory.
2. Wprowadź interfejs produktu bez zmiany istniejących klas.
3. Dodaj fabrykę zwracającą interfejs, początkowo używając tych samych konstruktorów.
4. Przekierowuj miejsca tworzenia pojedynczo.
5. Ogranicz widoczność klas i konstruktorów dopiero po migracji klientów.
6. Wydziel fabrykę do osobnej klasy, jeżeli tworzenie obciąża inną odpowiedzialność.
7. Dodaj test kontraktowy dla każdego produktu i test mapowania wejścia na typ.

Utworzenie wszystkich wariantów z wyprzedzeniem może wykonać kosztowne konstruktory i efekty wcześniej niż stara, wybrana gałąź. Fabryka nie powinna również zamieniać jawnej zależności na globalny rejestr usług.

Zmiana publicznego konstruktora na niedostępny jest zmianą łamiącą dla klientów zewnętrznych. W bibliotece potrzebny jest etap przejściowy, oznaczenie konstruktora jako przestarzałego i plan usunięcia w wersji głównej.

## 7. Move Embellishment to Decorator

### 7.1. Intencja

Embellishment to opcjonalne zachowanie wykonywane wokół odpowiedzialności podstawowej, na przykład audyt, pomiar czasu, retry albo cache. Decorator implementuje ten sam kontrakt co rdzeń i przechowuje delegata.

Kolejność dekoratorów jest częścią zachowania. `audit(retry(core))` może zapisać jedną operację obejmującą wszystkie próby, a `retry(audit(core))` może zapisać każdą próbę osobno.

### 7.2. Kod po zmianie

Plik `pl/training/module6/decorator/after/AuditedDeploymentRunner.java`:

```java
package pl.training.module6.decorator.after;

import java.util.Objects;
import java.util.function.Consumer;

public final class AuditedDeploymentRunner implements DeploymentRunner {
    private final DeploymentRunner delegate;
    private final Consumer<String> audit;

    public AuditedDeploymentRunner(DeploymentRunner delegate, Consumer<String> audit) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
        this.audit = Objects.requireNonNull(audit, "audit");
    }

    @Override
    public String run(String releaseId) {
        audit.accept("start:" + releaseId);
        try {
            String result = delegate.run(releaseId);
            audit.accept("success:" + releaseId);
            return result;
        } catch (RuntimeException exception) {
            audit.accept("failure:" + releaseId + ":" + exception.getClass().getSimpleName());
            throw exception;
        }
    }
}
```

Kod zachowuje dokładną instancję wyjątku delegata. Jeżeli sam zapis audytu rzuci wyjątek, może zasłonić błąd rdzenia. Produkcyjna polityka audytu musi rozstrzygnąć, czy błąd dodatku ma przerwać operację, zostać dołączony jako suppressed, czy trafić do awaryjnego kanału.

### 7.3. Sekwencja migracji

1. Wyodrębnij wąski interfejs komponentu.
2. Zapisz kolejność rdzenia i dodatku dla sukcesu oraz awarii.
3. Wprowadź dekorator przekazujący operację bez zmian.
4. Przenieś jeden dodatek, zachowując jego pozycję przed lub po rdzeniu.
5. Przetestuj propagację wyniku i dokładnej instancji wyjątku.
6. Przetestuj istotne kolejności składania kilku dekoratorów.
7. Przenieś składanie łańcucha do kontrolowanej fabryki.
8. Usuń dawną flagę po migracji wszystkich punktów tworzenia.

### 7.4. Granice przezroczystości

Decorator nie jest przezroczysty dla:

- `getClass` i refleksji,
- tożsamości referencyjnej,
- automatycznej serializacji,
- monitora używanego przez `synchronized`,
- metod fluent zwracających `this`,
- samowywołań wewnątrz delegata,
- nowych metod `default` dodanych później do interfejsu.

Metoda `synchronized` dekoratora blokuje monitor dekoratora, a metoda delegata monitor innego obiektu. Mechaniczne przeniesienie synchronizacji może więc naruszyć wykluczanie. Ślepe delegowanie `equals` może natomiast złamać symetrię między rdzeniem i obiektem opakowującym.

## 8. Replace State-Altering Conditionals with State

### 8.1. Tabela przejść przed klasami

State stosujemy, gdy warunki opisują zachowanie i przejścia bieżącego stanu obiektu. Zanim powstanie hierarchia, należy zapisać tabelę:

| Stan | approve | deploy | cancel |
| --- | --- | --- | --- |
| DRAFT | APPROVED | wyjątek | CANCELLED |
| APPROVED | wyjątek | DEPLOYED | CANCELLED |
| DEPLOYED | wyjątek | wyjątek | wyjątek |
| CANCELLED | wyjątek | wyjątek | wyjątek |

Pusta implementacja domyślna nie może zastąpić dotychczasowego wyjątku. Stan po niedozwolonej operacji musi pozostać niezmieniony.

### 8.2. Kontekst delegujący do State

Plik `pl/training/module6/state/after/Release.java`:

```java
package pl.training.module6.state.after;

public final class Release {
    private ReleaseState state = DraftState.INSTANCE;

    public Status status() {
        return state.status();
    }

    public void approve() {
        state = state.approve();
    }

    public void deploy() {
        state = state.deploy();
    }

    public void cancel() {
        state = state.cancel();
    }

    public enum Status {
        DRAFT,
        APPROVED,
        DEPLOYED,
        CANCELLED
    }

    private sealed interface ReleaseState
            permits DraftState, ApprovedState, DeployedState, CancelledState {
        Status status();

        default ReleaseState approve() {
            throw invalid("approve");
        }

        default ReleaseState deploy() {
            throw invalid("deploy");
        }

        default ReleaseState cancel() {
            throw invalid("cancel");
        }

        private IllegalStateException invalid(String action) {
            return new IllegalStateException(
                    "cannot " + action + " release in state " + status());
        }
    }

    private enum DraftState implements ReleaseState {
        INSTANCE;

        @Override
        public Status status() {
            return Status.DRAFT;
        }

        @Override
        public ReleaseState approve() {
            return ApprovedState.INSTANCE;
        }

        @Override
        public ReleaseState cancel() {
            return CancelledState.INSTANCE;
        }
    }

    private enum ApprovedState implements ReleaseState {
        INSTANCE;

        @Override
        public Status status() {
            return Status.APPROVED;
        }

        @Override
        public ReleaseState deploy() {
            return DeployedState.INSTANCE;
        }

        @Override
        public ReleaseState cancel() {
            return CancelledState.INSTANCE;
        }
    }

    private enum DeployedState implements ReleaseState {
        INSTANCE;

        @Override
        public Status status() {
            return Status.DEPLOYED;
        }
    }

    private enum CancelledState implements ReleaseState {
        INSTANCE;

        @Override
        public Status status() {
            return Status.CANCELLED;
        }
    }
}
```

Obiekty State są bezstanowymi stałymi enumu. Dane konkretnego wydania pozostają w kontekście `Release`. Umieszczenie danych jednego wydania w polu enumu współdzieliłoby je między wszystkimi kontekstami i byłoby błędem.

### 8.3. Kolejność przejścia i efektów

W przykładzie nie ma efektu zewnętrznego. W rzeczywistym systemie trzeba jawnie wybrać jeden z kontraktów:

1. wykonaj efekt, a stan zmień po sukcesie,
2. zmień stan, a następnie wykonaj efekt,
3. zapisz przejście i komunikat atomowo, na przykład przez outbox,
4. rozdziel stan procesu od stanu próby efektu.

Żaden wariant nie jest uniwersalnie poprawny. Refaktoryzacja musi zachować dotychczasowy moment zmiany, dopóki zespół nie zatwierdzi innego modelu niezawodności.

Pole `volatile` ze stanem zapewnia widoczność referencji, ale nie czyni sekwencji sprawdzenia, efektu i zapisu atomową. Funkcja przekazana do `AtomicReference.updateAndGet` może zostać wykonana więcej niż raz przy sporze, więc nie wolno umieszczać w niej nieidempotentnych efektów zewnętrznych.

### 8.4. Kiedy nie stosować State

Mały, zamknięty `switch` po dwóch stanach może być czytelniejszy. State staje się wartościowy, gdy tabela przejść rośnie, zachowania stanów są samodzielne albo zmiany jednego stanu nie powinny dotykać centralnej metody.

Kilka niezależnych osi stanu może spowodować eksplozję klas. Wtedy lepsze bywa rozdzielenie kontekstów, użycie kilku polityk albo jawny model procesu.

## 9. Replace Hard-coded Notifications with Observer

### 9.1. Intencja i granica zmiany

Observer usuwa zależność od konkretnych odbiorców powiadomienia. Subject zna minimalny interfejs listenera, a skład aplikacji rejestruje odbiorców.

Pierwszym bezpiecznym krokiem jest zachowanie relacji jeden do jednego przez nowy interfejs. Dopiero później można przejść do kolekcji obserwatorów. Możliwość powiadamiania dowolnej liczby odbiorców może być rozszerzeniem zachowania, a nie czystą refaktoryzacją.

### 9.2. Jawny kontrakt publikacji

Przed implementacją należy odpowiedzieć:

- czy publikacja jest synchroniczna,
- na jakim wątku wykonuje się listener,
- czy kolejność rejestracji jest gwarantowana,
- czy duplikaty są dozwolone,
- co dzieje się po pierwszym wyjątku,
- czy usunięcie podczas callbacku wpływa na bieżącą publikację,
- kto i kiedy kończy subskrypcję,
- czy subject przechowuje referencje silne,
- czy listener widzi stan przed, czy po zatwierdzeniu zmiany.

Przykład przyjmuje synchroniczną publikację na wątku wywołującym, kolejność rejestracji, dozwolone duplikaty i politykę fail-fast. Pierwszy wyjątek zatrzymuje dalsze powiadomienia.

### 9.3. Implementacja

Plik `pl/training/module6/observer/after/ReleasePublisher.java`:

```java
package pl.training.module6.observer.after;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ReleasePublisher {
    private final CopyOnWriteArrayList<Registration> listeners =
            new CopyOnWriteArrayList<>();

    public Subscription subscribe(ReleaseListener listener) {
        Registration registration = new Registration(
                Objects.requireNonNull(listener, "listener"));
        listeners.add(registration);
        AtomicBoolean active = new AtomicBoolean(true);
        return () -> {
            if (active.compareAndSet(true, false)) {
                listeners.remove(registration);
            }
        };
    }

    public void publish(ReleasePublished event) {
        Objects.requireNonNull(event, "event");
        for (Registration registration : listeners) {
            registration.notifyListener(event);
        }
    }

    private static final class Registration {
        private final ReleaseListener listener;

        private Registration(ReleaseListener listener) {
            this.listener = listener;
        }

        private void notifyListener(ReleasePublished event) {
            listener.onReleasePublished(event);
        }
    }
}
```

Plik `pl/training/module6/observer/after/Subscription.java`:

```java
package pl.training.module6.observer.after;

@FunctionalInterface
public interface Subscription extends AutoCloseable {
    @Override
    void close();
}
```

`CopyOnWriteArrayList` tworzy nową tablicę przy każdej operacji modyfikującej. Jest sensowna, gdy publikacji jest znacznie więcej niż rejestracji i usunięć, a liczba listenerów pozostaje umiarkowana. Iterator pracuje na migawce. Listener usunięty podczas publikacji może więc zostać jeszcze wywołany w tej samej publikacji, ale nie w następnej.

`AtomicBoolean` czyni zamknięcie konkretnego uchwytu idempotentnym. Nie służy do atomowości publikacji. Subject nadal przechowuje silną referencję do listenera aż do `close`, więc brak zamknięcia może wydłużyć życie całego grafu obiektów.

Każde wywołanie `subscribe` tworzy osobny obiekt `Registration`, który zachowuje tożsamościową implementację `equals` odziedziczoną z `Object`. Uchwyt usuwa więc dokładnie własną rejestrację, również gdy ten sam listener został dodany kilka razy albo dwa listenery są sobie równe według własnego `equals`.

### 9.4. Sekwencja migracji

1. Oddziel pracę odbiorcy od kodu wywołania powiadomienia.
2. Wyodrębnij minimalny interfejs listenera.
3. Zarejestruj dotychczasowego odbiorcę dokładnie raz.
4. Przenieś wywołanie za interfejs bez tworzenia kolekcji.
5. Usuń podklasę lub bezpośrednią zależność istniejącą tylko dla powiadomienia.
6. Jeżeli wymaganie to uzasadnia, w osobnym kroku dodaj wielu odbiorców.
7. Ustal politykę wyjątków, kolejności i wyrejestrowania.
8. Przetestuj rejestrację i usunięcie podczas callbacku.

Globalna szyna zdarzeń może jedynie ukryć zależności i utrudnić analizę kolejności. Dla jednego stabilnego odbiorcy jawna zależność bez Observera bywa prostsza.

## 10. Replace Implicit Tree with Composite

### 10.1. Ukryte drzewo

Drzewo bywa zakodowane w ścieżkach tekstowych, poziomach wcięcia, identyfikatorach rodzica, indeksach albo równoległych kolekcjach. Operacje drzewa są wtedy wykonywane przez parsowanie reprezentacji prymitywnej.

Plik `pl/training/module6/composite/before/LegacyPathPlan.java` przechowuje zadania jako ścieżki:

```java
package pl.training.module6.composite.before;

import java.util.List;
import java.util.Objects;

public final class LegacyPathPlan {
    private final List<Entry> entries;

    public LegacyPathPlan(List<Entry> entries) {
        this.entries = List.copyOf(Objects.requireNonNull(entries, "entries"));
    }

    public long totalMinutes() {
        long total = 0;
        for (Entry entry : entries) {
            total = Math.addExact(total, entry.minutes());
        }
        return total;
    }

    public List<String> taskNamesBelow(String path) {
        String prefix = requirePath(path);
        return entries.stream()
                .filter(entry -> entry.path().startsWith(prefix + "/"))
                .map(Entry::taskName)
                .toList();
    }

    public record Entry(String path, long minutes) {
        public Entry {
            path = requirePath(path);
            if (!path.contains("/")) {
                throw new IllegalArgumentException("task path must contain a parent");
            }
            if (minutes < 0) {
                throw new IllegalArgumentException("minutes must not be negative");
            }
        }

        private String taskName() {
            return path.substring(path.lastIndexOf('/') + 1);
        }
    }

    private static String requirePath(String path) {
        Objects.requireNonNull(path, "path");
        if (path.isBlank() || path.startsWith("/") || path.endsWith("/")
                || path.contains("//")) {
            throw new IllegalArgumentException("invalid path: " + path);
        }
        for (String segment : path.split("/", -1)) {
            if (segment.isBlank()) {
                throw new IllegalArgumentException("invalid path: " + path);
            }
        }
        return path;
    }
}
```

Legalna ścieżka ma co najmniej segment rodzica i zadania, a każdy segment zawiera znak inny niż biały. Hierarchia `release/database/backup` istnieje jednak jedynie w konwencji tekstu. Zmiana separatora, reguły jego maskowania, ścieżka pusta i wyszukiwanie prefiksu stają się częścią algorytmu każdego klienta.

### 10.2. Jawny model Composite

Plik `pl/training/module6/composite/after/PlanComponent.java`:

```java
package pl.training.module6.composite.after;

import java.util.Collection;

public sealed interface PlanComponent permits DeploymentTask, DeploymentGroup {
    String name();

    long totalMinutes();

    void collectTasks(Collection<? super DeploymentTask> target);

    <R> R accept(PlanVisitor<R> visitor);
}
```

Liść implementuje ten sam kontrakt:

```java
package pl.training.module6.composite.after;

import java.util.Collection;
import java.util.Objects;

public record DeploymentTask(String name, long minutes) implements PlanComponent {
    public DeploymentTask {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (minutes < 0) {
            throw new IllegalArgumentException("minutes must not be negative");
        }
    }

    @Override
    public long totalMinutes() {
        return minutes;
    }

    @Override
    public void collectTasks(Collection<? super DeploymentTask> target) {
        Objects.requireNonNull(target, "target").add(this);
    }

    @Override
    public <R> R accept(PlanVisitor<R> visitor) {
        return Objects.requireNonNull(visitor, "visitor").visitTask(this);
    }
}
```

Composite przechowuje dzieci i wykonuje operacje rekurencyjnie:

```java
package pl.training.module6.composite.after;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public record DeploymentGroup(
        String name,
        List<PlanComponent> components) implements PlanComponent {
    public DeploymentGroup {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        components = List.copyOf(Objects.requireNonNull(components, "components"));
    }

    @Override
    public long totalMinutes() {
        long total = 0;
        for (PlanComponent component : components) {
            total = Math.addExact(total, component.totalMinutes());
        }
        return total;
    }

    @Override
    public void collectTasks(Collection<? super DeploymentTask> target) {
        Objects.requireNonNull(target, "target");
        for (PlanComponent component : components) {
            component.collectTasks(target);
        }
    }

    @Override
    public <R> R accept(PlanVisitor<R> visitor) {
        return Objects.requireNonNull(visitor, "visitor").visitGroup(this);
    }
}
```

### 10.3. Mapper migracyjny

Samo ręczne zbudowanie podobnego drzewa nie sprawdza transformacji starego formatu. `LegacyPathPlanMapper` przyjmuje oczekiwany korzeń oraz wpisy legacy, a następnie tworzy strukturę Composite:

```java
package pl.training.module6.composite.after;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import pl.training.module6.composite.before.LegacyPathPlan;

public final class LegacyPathPlanMapper {
    public DeploymentGroup map(
            String rootName,
            List<LegacyPathPlan.Entry> entries) {
        requireRootName(rootName);
        List<LegacyPathPlan.Entry> source = List.copyOf(
                Objects.requireNonNull(entries, "entries"));
        validateDepthFirstOrder(rootName, source);

        GroupNode root = new GroupNode(rootName);
        for (LegacyPathPlan.Entry entry : source) {
            String[] segments = segmentsBelow(rootName, entry);
            GroupNode parent = root;
            for (int index = 1; index < segments.length - 1; index++) {
                parent = parent.group(segments[index], entry.path());
            }
            parent.task(
                    segments[segments.length - 1],
                    entry.minutes(),
                    entry.path());
        }
        return root.freeze();
    }

    private static void validateDepthFirstOrder(
            String rootName,
            List<LegacyPathPlan.Entry> entries) {
        List<String> previousParents = List.of();
        Set<String> closedGroups = new HashSet<>();
        for (LegacyPathPlan.Entry entry : entries) {
            String[] segments = segmentsBelow(rootName, entry);
            List<String> currentParents = parentPaths(segments);
            int common = commonPrefixLength(previousParents, currentParents);
            closedGroups.addAll(
                    previousParents.subList(common, previousParents.size()));
            for (String parent : currentParents) {
                if (closedGroups.contains(parent)) {
                    throw new IllegalArgumentException(
                            "entries are not in depth-first order: "
                                    + entry.path());
                }
            }
            previousParents = currentParents;
        }
    }

    private static String[] segmentsBelow(
            String rootName,
            LegacyPathPlan.Entry entry) {
        Objects.requireNonNull(entry, "entries must not contain null");
        String[] segments = entry.path().split("/", -1);
        if (!segments[0].equals(rootName)) {
            throw new IllegalArgumentException(
                    "entry is outside root " + rootName + ": " + entry.path());
        }
        for (String segment : segments) {
            if (segment.isBlank()) {
                throw new IllegalArgumentException(
                        "path segment must not be blank: " + entry.path());
            }
        }
        return segments;
    }

    private static List<String> parentPaths(String[] segments) {
        List<String> parents = new ArrayList<>();
        StringBuilder path = new StringBuilder(segments[0]);
        for (int index = 1; index < segments.length - 1; index++) {
            path.append('/').append(segments[index]);
            parents.add(path.toString());
        }
        return List.copyOf(parents);
    }

    private static int commonPrefixLength(
            List<String> first,
            List<String> second) {
        int length = Math.min(first.size(), second.size());
        int index = 0;
        while (index < length && first.get(index).equals(second.get(index))) {
            index++;
        }
        return index;
    }

    private static void requireRootName(String rootName) {
        if (rootName == null || rootName.isBlank() || rootName.contains("/")) {
            throw new IllegalArgumentException(
                    "rootName must be one non-blank path segment");
        }
    }

    private sealed interface Node permits GroupNode, TaskNode {
        String name();

        PlanComponent freeze();
    }

    private static final class GroupNode implements Node {
        private final String name;
        private final List<Node> children = new ArrayList<>();
        private final Map<String, GroupNode> groups = new HashMap<>();
        private final Set<String> taskNames = new HashSet<>();

        private GroupNode(String name) {
            this.name = name;
        }

        private GroupNode group(String childName, String sourcePath) {
            if (taskNames.contains(childName)) {
                throw pathConflict(sourcePath, childName);
            }
            GroupNode existing = groups.get(childName);
            if (existing != null) {
                return existing;
            }
            GroupNode created = new GroupNode(childName);
            groups.put(childName, created);
            children.add(created);
            return created;
        }

        private void task(String taskName, long minutes, String sourcePath) {
            if (groups.containsKey(taskName)) {
                throw pathConflict(sourcePath, taskName);
            }
            taskNames.add(taskName);
            children.add(new TaskNode(taskName, minutes));
        }

        private IllegalArgumentException pathConflict(
                String sourcePath,
                String childName) {
            return new IllegalArgumentException(
                    "path is both a task and a group at "
                            + childName + ": " + sourcePath);
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public DeploymentGroup freeze() {
            return new DeploymentGroup(
                    name, children.stream().map(Node::freeze).toList());
        }
    }

    private record TaskNode(String name, long minutes) implements Node {
        @Override
        public DeploymentTask freeze() {
            return new DeploymentTask(name, minutes);
        }
    }
}
```

Jawne `rootName` pozwala odwzorować także pusty plan. Mapper zachowuje powtórzone zadania, ale odrzuca ścieżkę używaną jednocześnie jako zadanie i grupa oraz wpis spoza wskazanego korzenia.

Kolejność płaskiej listy nie zawsze daje się zachować po zgrupowaniu w drzewo. Wpisy `release/a/first`, `release/b/second`, `release/a/third` wymagałyby ponownego wejścia do zamkniętej grupy `a`; zwykłe przejście Composite zwróciłoby inną kolejność. Mapper wymaga więc kolejności zgodnej z przejściem w głąb i zgłasza błąd zamiast po cichu zmienić zachowanie. W systemie produkcyjnym można zamiast tego jawnie posortować dane, dodać pole kolejności albo przyjąć zmianę kontraktu.

### 10.4. Procedura

1. Scharakteryzuj reguły starej reprezentacji i wszystkie niepoprawne wartości.
2. Wprowadź typ liścia bez usuwania reprezentacji tekstowej.
3. Wprowadź typ Composite przechowujący wspólny kontrakt.
4. Napisz parser lub mapper tworzący nowe drzewo ze starych danych.
5. Przenieś jedną operację, na przykład sumowanie.
6. Porównaj wynik i kolejność dla starej i nowej reprezentacji.
7. Przenoś kolejne operacje pojedynczo.
8. Zmień granicę trwałości dopiero po przygotowaniu migracji danych.

### 10.5. Ryzyka struktur rekurencyjnych

Należy określić zachowanie dla bardzo głębokiego drzewa, cykli, współdzielonych poddrzew i przepełnienia sumy. Rekurencja może zakończyć się `StackOverflowError`, a cykl nieskończoną wędrówką. W danych niezaufanych potrzebny jest limit głębokości, wykrywanie cykli albo iteracyjne przechodzenie po strukturze.

Przykład używa `Math.addExact`, więc przepełnienie `long` jest jawnym błędem zamiast cichego zawinięcia wartości. `List.copyOf` chroni strukturę listy, lecz nie nadaje dodatkowej niemutowalności elementom. Tutaj elementy są rekordami lub rekordem z niemodyfikowalną listą, więc cały graf utworzony przez Builder pozostaje niemutowalny.

## 11. Unify Interfaces with Adapter

### 11.1. Intencja

Adapter pozwala klientowi używać preferowanego kontraktu, mimo że istniejąca biblioteka albo komponent legacy udostępnia inny interfejs. Adapter tłumaczy nazwy, parametry, wyniki i błędy, ale nie powinien udawać zgodności semantycznej, której nie ma.

W przykładzie preferowany kontrakt przyjmuje `ReleaseMessage`, a stara brama wymaga oddzielnego celu i tekstowego payloadu.

### 11.2. Adapter obiektowy

Plik `pl/training/module6/adapter/after/LegacyGatewayAdapter.java`:

```java
package pl.training.module6.adapter.after;

import java.util.Objects;

public final class LegacyGatewayAdapter implements ReleaseNotifier {
    private final LegacyMessageGateway gateway;

    public LegacyGatewayAdapter(LegacyMessageGateway gateway) {
        this.gateway = Objects.requireNonNull(gateway, "gateway");
    }

    @Override
    public String send(ReleaseMessage message) {
        Objects.requireNonNull(message, "message");
        return gateway.transmit(
                message.recipient(),
                "release:" + message.releaseId());
    }
}
```

Klient zależy wyłącznie od interfejsu docelowego:

```java
package pl.training.module6.adapter.after;

import java.util.Objects;

public final class NotificationService {
    private final ReleaseNotifier notifier;

    public NotificationService(ReleaseNotifier notifier) {
        this.notifier = Objects.requireNonNull(notifier, "notifier");
    }

    public String notify(ReleaseMessage message) {
        return notifier.send(Objects.requireNonNull(message, "message"));
    }
}
```

### 11.3. Co naprawdę trzeba przetłumaczyć

Podobne sygnatury nie gwarantują podobnego kontraktu. Adapter powinien jawnie rozstrzygać:

- jednostki i zakresy liczb,
- strefę czasową i precyzję czasu,
- kodowanie znaków,
- znaczenie `null` i pustej wartości,
- kolejność i indeksowanie,
- mutowalność wejścia i wyniku,
- własność oraz zamykanie zasobów,
- idempotencję operacji,
- mapowanie wyjątków i zachowanie przyczyny,
- synchroniczność i kontekst wątku.

Adapter implementujący `AutoCloseable` musi określić, czy jest właścicielem adaptowanego obiektu. Zamknięcie adaptera nie zawsze powinno zamykać współdzielone połączenie.

### 11.4. Sekwencja migracji

1. Wyodrębnij preferowany interfejs z potrzeb klienta.
2. Utwórz najprostszy adapter przechowujący adaptowany obiekt.
3. Przenieś mapowanie jednej operacji do adaptera.
4. Dodaj test porównujący stary protokół i interfejs docelowy.
5. Przenoś pozostałe operacje pojedynczo.
6. Zmień zależność klienta z klasy konkretnej na interfejs.
7. Ukryj bezpośredni dostęp do adaptowanego obiektu dopiero po migracji.

Obiekt opakowujący zmienia `getClass`, tożsamość, monitor i zwykle serializowaną postać. Jeżeli oryginalny typ można bezpiecznie zmienić przez Rename Method lub Move Method, adapter może być zbędny.

## 12. Replace Conditional Dispatcher with Command

### 12.1. Intencja

Dispatcher odczytuje selektor i uruchamia przypisaną akcję. Command zamienia każdą akcję w obiekt o wspólnej operacji. Rejestr mapuje selektor na komendę.

Mapa jest równoważna tylko wtedy, gdy wybór opiera się na rozłącznym kluczu. Łańcuch nakładających się predykatów, w którym wygrywa pierwsza spełniona gałąź, nie może zostać mechanicznie zastąpiony mapą.

### 12.2. Rejestr komend

Plik `pl/training/module6/command/after/DeploymentCommandDispatcher.java`:

```java
package pl.training.module6.command.after;

import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;

public final class DeploymentCommandDispatcher {
    private final Map<DeploymentAction, DeploymentCommand> commands;

    public DeploymentCommandDispatcher(Map<DeploymentAction, DeploymentCommand> commands) {
        Map<DeploymentAction, DeploymentCommand> copy =
                Map.copyOf(Objects.requireNonNull(commands, "commands"));
        EnumSet<DeploymentAction> missing =
                EnumSet.allOf(DeploymentAction.class);
        missing.removeAll(copy.keySet());
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("missing commands: " + missing);
        }
        this.commands = copy;
    }

    public String dispatch(DeploymentAction action, String releaseId) {
        Objects.requireNonNull(action, "action");
        if (releaseId == null || releaseId.isBlank()) {
            throw new IllegalArgumentException("releaseId must not be blank");
        }

        return commands.get(action).execute(releaseId);
    }
}
```

Kontrakt komendy jest minimalny:

```java
package pl.training.module6.command.after;

@FunctionalInterface
public interface DeploymentCommand {
    String execute(String releaseId);
}
```

`Map.copyOf` odłącza rejestr od późniejszych modyfikacji mapy wejściowej i odrzuca `null`. Ponieważ dawny `switch` obsługiwał wszystkie stałe `DeploymentAction`, konstruktor sprawdza kompletność mapy przez `EnumSet.allOf`. Niepełna konfiguracja kończy się błędem podczas składania aplikacji, a nie dopiero dla rzadziej używanej akcji. Gdy częściowy rejestr jest rzeczywistym wymaganiem, powinien mieć osobny kontrakt opisujący zachowanie dla braku komendy.

### 12.3. Bezpieczna sekwencja

1. Scharakteryzuj klucz, priorytet gałęzi, `default` i `null`.
2. Wydziel ciało każdej gałęzi do metody, pozostawiając `switch`.
3. Przenieś każdą metodę do konkretnej komendy.
4. Ustal najmniejszą wspólną sygnaturę `execute`.
5. Pozwól staremu dispatcherowi warunkowo uruchamiać gotowe komendy.
6. Zbuduj rejestr z jawną polityką duplikatów i kompletności.
7. Zastąp warunek wyszukaniem komendy w rejestrze.
8. Usuń kod przejściowy po pokryciu wszystkich kluczy.

### 12.4. Czego Command nie daje automatycznie

Command nie oznacza asynchroniczności, kolejki, retry ani undo. Przeniesienie wykonania do `Executor` zmienia wątek, propagację wyjątków, transakcję i kontekst diagnostyczny. Retry może powielić nieidempotentne efekty, a undo wymaga osobnego modelu kompensacji.

Utworzenie wszystkich komend z wyprzedzeniem może wcześniej pobrać zależności lub wykonać kosztowne konstruktory. Współdzielona instancja komendy musi być bezstanowa albo bezpieczna wątkowo. Dane pojedynczego żądania powinny pozostać argumentem lub należeć do jednorazowej instancji.

## 13. Apply Template Method

### 13.1. Formowanie wspólnego szkieletu

Klasyczna nazwa refaktoryzacji to Form Template Method. Podklasy wykonują podobne kroki w tej samej kolejności, ale różnią się implementacją wybranych kroków. Po ujednoliceniu nazw i sygnatur wspólny szkielet można przenieść do nadklasy.

W przykładzie dwa importery wykonują:

1. walidację całego wejścia,
2. parsowanie właściwe dla formatu,
3. walidację wymaganych pól,
4. utworzenie `ReleaseDraft`.

Tylko krok parsowania jest zmienny.

### 13.2. Metoda szablonowa

Plik `pl/training/module6/templatemethod/after/ReleaseImporter.java`:

```java
package pl.training.module6.templatemethod.after;

public abstract class ReleaseImporter {
    public final ReleaseDraft importRelease(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("input must not be blank");
        }

        Fields fields = parse(raw);
        String releaseId = fields.releaseId();
        String service = fields.service();
        if (releaseId == null || releaseId.isBlank()
                || service == null || service.isBlank()) {
            throw new IllegalArgumentException("releaseId and service are required");
        }
        return new ReleaseDraft(releaseId, service);
    }

    protected abstract Fields parse(String raw);

    protected final Fields fields(String releaseId, String service) {
        return new Fields(releaseId, service);
    }

    protected record Fields(String releaseId, String service) {
    }
}
```

Plik `pl/training/module6/templatemethod/after/KeyValueReleaseImporter.java`:

```java
package pl.training.module6.templatemethod.after;

public final class KeyValueReleaseImporter extends ReleaseImporter {
    @Override
    protected Fields parse(String raw) {
        String releaseId = null;
        String service = null;
        for (String field : raw.split(";", -1)) {
            String[] pair = field.split("=", 2);
            if (pair.length != 2) {
                throw new IllegalArgumentException("expected key=value");
            }
            switch (pair[0].trim()) {
                case "id" -> releaseId = pair[1].trim();
                case "service" -> service = pair[1].trim();
                default -> throw new IllegalArgumentException("unknown field: " + pair[0]);
            }
        }
        return fields(releaseId, service);
    }
}
```

Metoda `importRelease` jest `final`, ponieważ kolejność jest niezmiennikiem przykładu. Dodanie `final` do opublikowanej metody, którą zewnętrzni klienci już nadpisują, byłoby jednak zmianą łamiącą.

Chroniony typ `Fields` ma chroniony konstruktor kanoniczny. Podklasa klasy `ReleaseImporter` nie jest podklasą `Fields`, dlatego poza pakietem nie mogłaby bezpośrednio wykonać `new Fields(...)`. Chroniona fabryka `fields` zachowuje możliwość rozszerzenia klasy bez upubliczniania pomocniczego typu.

### 13.3. Sekwencja

1. Zapisz dokładną kolejność kroków w każdej klasie.
2. Użyj Extract Method, aby kroki były na tym samym poziomie abstrakcji.
3. Ujednolicaj nazwy i sygnatury po jednym kroku.
4. Przenieś identyczne kroki do bazy.
5. Doprowadź zewnętrzne metody do identycznej postaci.
6. Użyj Pull Up Method dla szkieletu.
7. Oznacz wymagane kroki jako abstrakcyjne.
8. Dodawaj opcjonalny hook tylko wtedy, gdy domyślne zachowanie jest poprawne dla wszystkich podtypów.
9. Uruchom test kontraktowy dla każdego importera.

### 13.4. Ryzyka dziedziczenia algorytmu

Dodanie nowej metody abstrakcyjnej wymusza zmianę każdej ponownie kompilowanej podklasy konkretnej, która nie dziedziczy zgodnej implementacji. Podklasa abstrakcyjna może nadal pozostawić implementację swoim potomkom. Stara implementacja binarna może ujawnić brak dopiero podczas wywołania. Hook wywołany przez konstruktor bazy może zobaczyć domyślne wartości pól podklasy.

Metoda szablonowa `synchronized` utrzymuje monitor odbiorcy podczas hooków. Jeżeli hook wywołuje kod zewnętrzny, rośnie ryzyko reentrancji i zakleszczenia. Duża liczba hooków albo potrzeba zmiany ich kolejności sygnalizuje, że kompozycja lub Strategy może być elastyczniejsza.

## 14. Replace Distinctions with Composite

### 14.1. Rozróżnienie jeden lub wiele

Klient często posiada dwie ścieżki dla pojedynczego elementu i kolekcji. Powstają pary metod `execute` i `executeAll`, osobne warunki oraz powielone reguły błędów. Replace One/Many Distinctions with Composite pozwala przekazać liść albo grupę przez jeden kontrakt.

Stan przed zmianą:

```java
package pl.training.module6.composite.before;

import java.util.List;

public final class LegacyPlanExecutor {
    public List<String> execute(Task task) {
        return List.of("executed:" + task.name());
    }

    public List<String> executeAll(List<Task> tasks) {
        return tasks.stream()
                .map(task -> "executed:" + task.name())
                .toList();
    }

    public record Task(String name) {
        public Task {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("name must not be blank");
            }
        }
    }
}
```

Stan po zmianie:

```java
package pl.training.module6.composite.after;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class PlanExecutor {
    public List<String> execute(PlanComponent component) {
        Objects.requireNonNull(component, "component");
        List<DeploymentTask> tasks = new ArrayList<>();
        component.collectTasks(tasks);
        return tasks.stream()
                .map(task -> "executed:" + task.name())
                .toList();
    }
}
```

### 14.2. Warunki równoważności

Ujednolicenie API nie może przypadkowo zmienić:

- kolejności przetwarzania elementów,
- zachowania pustej grupy,
- polityki przerwania po błędzie,
- sposobu raportowania częściowego sukcesu,
- liczby transakcji,
- semantyki duplikatów,
- poziomu równoległości,
- granicy retry i idempotencji.

W przykładzie kolejność jest preorderem zgodnym z kolejnością list dzieci. Pierwszy wyjątek przerwałby wykonanie. Wersja produkcyjna musi zdecydować, czy wynik częściowy jest zwracany, kompensowany, czy odrzucany.

### 14.3. Transparentność i bezpieczeństwo Composite

Transparent Composite umieszcza operacje zarządzania dziećmi we wspólnym kontrakcie. Klient może wtedy wywołać `add` także na liściu, co wymaga wyjątku albo pustego zachowania.

Safe Composite udostępnia zarządzanie dziećmi tylko typowi grupy. Przykład jest bliższy temu wariantowi: wspólny interfejs opisuje operacje biznesowe, a `components()` należy do `DeploymentGroup`. Klient czasem musi znać rodzaj węzła, ale niemożliwa operacja nie zanieczyszcza kontraktu liścia.

## 15. Limit Instantiation with Singleton

### 15.1. Singleton jest decyzją o cyklu życia

Technika ogranicza tworzenie do jednej instancji dostępnej przez daną definicję klasy. Może być uzasadniona, gdy wiele równoważnych, kosztownych instancji nie daje wartości albo domena rzeczywiście wymaga jednej tożsamości.

Nie należy wprowadzać Singletona na podstawie przypuszczenia o pamięci lub wydajności. Najpierw trzeba zmierzyć koszt i sprawdzić, czy współdzielenie zachowuje semantykę. Zastąpienie kilku obiektów jednym może połączyć ich wcześniej niezależny stan, blokady i cykle życia. Katalog Kerievsky'ego zawiera też transformację odwrotną, Inline Singleton, która usuwa niepotrzebny Singleton i przywraca jawne przekazywanie zależności.

### 15.2. Niemutowalny enum singleton

Stan początkowy pozwala każdemu klientowi tworzyć kolejną instancję:

```java
package pl.training.module6.singleton.before;

import java.time.Duration;

public final class LegacyDeploymentDefaults {
    private final Duration healthCheckTimeout = Duration.ofSeconds(30);

    public Duration healthCheckTimeout() {
        return healthCheckTimeout;
    }
}
```

Plik `pl/training/module6/singleton/DeploymentDefaults.java`:

```java
package pl.training.module6.singleton;

import java.time.Duration;

public enum DeploymentDefaults {
    INSTANCE;

    private final Duration healthCheckTimeout = Duration.ofSeconds(30);

    public Duration healthCheckTimeout() {
        return healthCheckTimeout;
    }
}
```

Enum zapewnia jedną instancję każdej stałej w obrębie definiującego loadera klas. Mechanizm inicjalizacji klasy zapewnia bezpieczną publikację stałej. Standardowa serializacja enumów odtwarza kanoniczną stałą, a refleksyjne tworzenie instancji enumu jest zabronione.

Nie oznacza to jednej instancji w całym procesie w każdych warunkach. Dwa niezależne loadery klas mogą załadować dwie definicje `DeploymentDefaults`. Systemy aplikacyjne, kontenery, wtyczki i testy izolowane mogą celowo używać takich granic.

### 15.3. Dlaczego przykład jest niemutowalny

Globalny mutowalny Singleton:

- tworzy ukrytą zależność każdego klienta,
- łączy testy przez stan pozostawiony przez poprzedni przypadek,
- wymaga synchronizacji,
- utrudnia konfigurację kilku tenantów,
- wydłuża życie referencji,
- utrudnia kontrolowane zamykanie zasobów,
- może stać się wąskim gardłem.

Przykład przechowuje wyłącznie niemutowalne `Duration`. Nawet wtedy jawne wstrzyknięcie ustawień jest zwykle lepsze dla kodu domenowego. Kontener DI może ograniczyć liczbę instancji do jednej w określonym zakresie, zachowując możliwość podmiany zależności w teście.

### 15.4. Bezpieczna sekwencja

1. Zmierz rzeczywisty koszt wielu instancji.
2. Udowodnij, że instancje są semantycznie równoważne i mogą współdzielić stan.
3. Scharakteryzuj kolejność inicjalizacji, awarie i zamykanie.
4. Przekieruj tworzenie przez jedną fabrykę lub jednego dostawcę instancji.
5. Wprowadź współdzielenie za tą granicą.
6. Przetestuj współbieżny dostęp i izolację klientów.
7. Dopiero później ogranicz konstruktor lub wybierz enum.

Jeżeli klienci bezpośrednio wywoływali publiczny konstruktor, jego usunięcie jest zmianą API. Jeżeli każda instancja miała odrębny cache, licznik albo blokadę, przejście na Singleton nie jest refaktoryzacją bez zmiany zachowania.

## 16. Move Accumulation to Collecting Parameter i Visitor

### 16.1. Dwie techniki, różne koszty

Akumulacja przechodzi po strukturze i buduje wynik. Gdy jedna długa metoda rozpoznaje wiele elementów oraz mutuje lokalny wynik, można rozważyć:

- Collecting Parameter, gdy elementy same mogą dopisywać wynik do przekazanego akumulatora,
- Visitor, gdy chcemy dodawać wiele operacji do stabilnej hierarchii elementów bez umieszczania każdej operacji w tych elementach.

Collecting Parameter jest prostszy. Visitor wprowadza podwójną dyspozycję i silne powiązanie z zestawem typów elementów.

Obie techniki współistnieją w kodzie szkoleniowym, aby można je było porównać na tym samym drzewie. W kodzie produkcyjnym po podjęciu decyzji należy usunąć zbędną, równoległą ścieżkę akumulacji, jeżeli nie stanowi osobnego kontraktu.

### 16.2. Parametr zbierający

Kontrakt `PlanComponent` zawiera:

```java
void collectTasks(Collection<? super DeploymentTask> target);
```

Liść dodaje siebie, a grupa przekazuje ten sam akumulator dzieciom. Wywołujący zachowuje własność kolekcji:

```java
List<DeploymentTask> tasks = new ArrayList<>();
plan.collectTasks(tasks);
```

Trzeba zdefiniować, czy metoda może wyczyścić kolekcję, czy dopisuje do istniejącej zawartości oraz co pozostaje po wyjątku. W przykładzie metoda wyłącznie dopisuje i może pozostawić wynik częściowy, jeżeli późniejsze dziecko zgłosi błąd.

Przekazanie ogólnej mutowalnej mapy lub kontekstu może ukryć zbyt wiele odpowiedzialności. Dobry collecting parameter ma wąski typ i jasną własność.

### 16.3. Visitor

Plik `pl/training/module6/composite/after/PlanVisitor.java`:

```java
package pl.training.module6.composite.after;

public interface PlanVisitor<R> {
    R visitTask(DeploymentTask task);

    R visitGroup(DeploymentGroup group);
}
```

Plik `pl/training/module6/composite/after/TotalMinutesVisitor.java`:

```java
package pl.training.module6.composite.after;

public final class TotalMinutesVisitor implements PlanVisitor<Long> {
    @Override
    public Long visitTask(DeploymentTask task) {
        return task.minutes();
    }

    @Override
    public Long visitGroup(DeploymentGroup group) {
        long total = 0;
        for (PlanComponent component : group.components()) {
            total = Math.addExact(total, component.accept(this));
        }
        return total;
    }
}
```

Każdy element implementuje `accept`, który wybiera właściwe przeciążenie `visit`. Dzięki temu Visitor nie potrzebuje łańcucha `instanceof`.

### 16.4. Macierz zmian

| Częsta zmiana | Naturalniejszy kierunek |
| --- | --- |
| nowe operacje, stabilne rodzaje węzłów | Visitor |
| nowe rodzaje węzłów, stabilne operacje | metody polimorficzne w elementach |
| jedna prosta akumulacja | Collecting Parameter |
| operacja nie powinna należeć do domeny elementu | Visitor albo zewnętrzne przejście po strukturze |
| zbiór typów jest otwarty dla wtyczek | klasyczny Visitor staje się kosztowny |

Dodanie nowego rodzaju węzła wymaga zmiany wszystkich Visitorów. Z tego powodu zamknięty `sealed PlanComponent` pasuje do klasycznego Visitora. Dodanie kolejnej operacji wymaga natomiast nowej implementacji `PlanVisitor`, bez zmiany istniejących węzłów.

Wynik `Long` jest opakowany ze względu na generyczny parametr `R`. W gorącej ścieżce należy uwzględnić koszt opakowywania wartości prymitywnej. Zarówno metoda domenowa, jak i Visitor używają `Math.addExact`, aby zachować tę samą politykę przepełnienia.

## 17. Extract Composite

### 17.1. Dokładny problem techniki

Extract Composite nie jest ogólnym poleceniem utworzenia drzewa. Punktem wyjścia jest hierarchia, w której kilka klas powiela pola i logikę przechowywania oraz przetwarzania dzieci z tej samej hierarchii. Wspólną odpowiedzialność wydziela się do klasy Composite.

W przykładzie `ReleaseGroup` i `RollbackGroup` osobno przechowują listę dzieci, tworzą kopię przy odczycie i sumują czas.

### 17.2. Stan przed zmianą

Plik `pl/training/module6/extractcomposite/before/LegacyPlanNodes.java`:

```java
package pl.training.module6.extractcomposite.before;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class LegacyPlanNodes {
    private LegacyPlanNodes() {
    }

    public interface PlanNode {
        long totalMinutes();
    }

    public record TaskNode(long minutes) implements PlanNode {
        public TaskNode {
            if (minutes < 0) {
                throw new IllegalArgumentException("minutes must not be negative");
            }
        }

        @Override
        public long totalMinutes() {
            return minutes;
        }
    }

    public static final class ReleaseGroup implements PlanNode {
        private final List<PlanNode> children = new ArrayList<>();

        public void add(PlanNode child) {
            children.add(Objects.requireNonNull(child, "child"));
        }

        public List<PlanNode> children() {
            return List.copyOf(children);
        }

        @Override
        public long totalMinutes() {
            long total = 0;
            for (PlanNode child : children) {
                total = Math.addExact(total, child.totalMinutes());
            }
            return total;
        }
    }

    public static final class RollbackGroup implements PlanNode {
        private final List<PlanNode> children = new ArrayList<>();

        public void add(PlanNode child) {
            children.add(Objects.requireNonNull(child, "child"));
        }

        public List<PlanNode> children() {
            return List.copyOf(children);
        }

        @Override
        public long totalMinutes() {
            long total = 0;
            for (PlanNode child : children) {
                total = Math.addExact(total, child.totalMinutes());
            }
            return total;
        }
    }
}
```

Zagnieżdżone typy utrzymują przykład w jednym pliku. W kodzie produkcyjnym publiczne typy zwykle znajdowałyby się w osobnych plikach.

### 17.3. Stan po zmianie

Plik `pl/training/module6/extractcomposite/after/PlanNodes.java`:

```java
package pl.training.module6.extractcomposite.after;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class PlanNodes {
    private PlanNodes() {
    }

    public interface PlanNode {
        long totalMinutes();
    }

    public record TaskNode(long minutes) implements PlanNode {
        public TaskNode {
            if (minutes < 0) {
                throw new IllegalArgumentException("minutes must not be negative");
            }
        }

        @Override
        public long totalMinutes() {
            return minutes;
        }
    }

    public abstract static class CompositePlanNode implements PlanNode {
        private final List<PlanNode> children = new ArrayList<>();

        public final void add(PlanNode child) {
            children.add(Objects.requireNonNull(child, "child"));
        }

        public final List<PlanNode> children() {
            return List.copyOf(children);
        }

        @Override
        public final long totalMinutes() {
            long total = 0;
            for (PlanNode child : children) {
                total = Math.addExact(total, child.totalMinutes());
            }
            return total;
        }
    }

    public static final class ReleaseGroup extends CompositePlanNode {
    }

    public static final class RollbackGroup extends CompositePlanNode {
    }
}
```

Prywatne pole ukrywa wspólną reprezentację dzieci, a metody `final` uniemożliwiają zastąpienie wspólnego dostępu i polityki sumowania. Nie zabrania to podklasie zadeklarowania drugiego pola z dziećmi. W przykładzie konkretne grupy są `final` i nie dodają własnego stanu, ale podczas refaktoryzacji taką duplikację nadal trzeba wykryć w przeglądzie kodu i testach.

### 17.4. Sekwencja

1. Znajdź wszystkie klasy przechowujące dzieci z tej samej hierarchii.
2. Porównaj rzeczywiste kontrakty, nie tylko podobieństwo kodu.
3. Ujednolij nazwy i typy kolekcji bez przenoszenia.
4. Wyodrębnij wspólną klasę bazową implementującą kontrakt węzła.
5. Przenieś pole dzieci z jednej klasy i uruchom testy.
6. Przenieś wspólne operacje pojedynczo.
7. Skieruj drugą klasę do wspólnego Composite.
8. Usuń duplikaty dopiero po sprawdzeniu kolejności, mutowalności i wyjątków.

Podobne pętle mogą mieć różne znaczenie. Jedna grupa może pomijać nieaktywne dzieci, inna kontynuować po błędzie, a jeszcze inna wykonywać równolegle. Wydzielenie wspólnej implementacji jest poprawne tylko wtedy, gdy te różnice nie należą do kontraktu albo zostały jawnie zamodelowane.

Przykład celowo zachowuje mutowalny model wejściowy. Nadal można w nim dodać grupę jako własne dziecko i utworzyć cykl. Wykrywanie takiej sytuacji byłoby osobną zmianą zachowania, którą należy dodać po refaktoryzacji, jeżeli wymaga tego domena lub źródło danych.

## 18. Warsztat praktyczny

### 18.1. Zasada pracy

Każde ćwiczenie wykonujemy w krótkiej pętli:

1. uruchom testy bazowe,
2. nazwij zachowanie chronione w bieżącym kroku,
3. wykonaj jedną transformację,
4. uruchom test celowany,
5. uruchom test różnicowy,
6. zapisz decyzję projektową,
7. przejdź do następnego kroku dopiero po zielonym wyniku.

Nie rozpoczynamy od tworzenia wszystkich klas wzorca. Pierwszy commit powinien być mały i odwracalny.

### 18.2. Ćwiczenie 1: wybór właściwego mechanizmu

Punkt wyjścia zawiera jeden serwis z czterema warunkami:

- wariant kalkulacji kosztu,
- rodzaj kroku wdrożenia,
- bieżący stan wydania,
- akcję operatora.

Zadanie:

1. Przypisz każdą oś do Strategy, polimorfizmu, State albo Command.
2. Dla każdej osi zapisz uzasadnienie w jednym zdaniu.
3. Dodaj test charakterystyki dla gałęzi domyślnej i `null`.
4. Wprowadź jeden kontrakt naraz.
5. Nie usuwaj warunku, dopóki wszystkie punkty tworzenia nie używają nowego modelu.

Kryteria akceptacji:

- nie powstała kombinatoryczna hierarchia podtypów,
- stan przejściowy nie jest strategią,
- komendy nie przechowują danych poprzedniego żądania,
- fabryka lub korzeń kompozycji jest jedynym miejscem mapowania konfiguracji na Strategy,
- testy porównują wyniki i wyjątki wersji przed i po.

### 18.3. Ćwiczenie 2: State, Observer i Decorator

Rozbuduj przykład wydania o zapis audytu i powiadomienie odbiorców po udanym wdrożeniu.

Zadanie:

1. Ustal, czy audyt jest częścią przejścia State, dekoracją operacji, czy osobnym zdarzeniem.
2. Zapisz kolejność stanu, audytu i publikacji.
3. Dodaj listener, który usuwa inną subskrypcję podczas callbacku.
4. Dodaj listener rzucający wyjątek.
5. Udokumentuj politykę fail-fast albo kontynuacji.
6. Przetestuj stan po awarii audytu oraz listenera.

Kryteria akceptacji:

- kod nie wywołuje listenerów pod nieudokumentowaną blokadą,
- uchwyt subskrypcji jest zamykany idempotentnie,
- test ustala, czy usunięty listener jest jeszcze wywoływany podczas bieżącej publikacji,
- nie nastąpiła niejawna zmiana na wykonanie asynchroniczne,
- błąd dodatku nie zasłania pierwotnej przyczyny bez jawnej decyzji.

### 18.4. Ćwiczenie 3: rodzina refaktoryzacji Composite

Punkt wyjścia to lista zadań ze ścieżkami tekstowymi oraz osobne metody dla jednego zadania i listy.

Zadanie:

1. Scharakteryzuj separator, kolejność i niepoprawne ścieżki.
2. Wprowadź `PlanComponent`, liść i grupę.
3. Zbuduj mapper starego formatu do Composite.
4. Zastąp rozróżnienie jeden lub wiele jednym kontraktem.
5. Enkapsuluj konstrukcję drzewa w Builderze.
6. Przenieś zbieranie liści do parametru zbierającego.
7. Dodaj osobny Visitor liczący czas.
8. Przetestuj puste drzewo, głęboką strukturę, przepełnienie i współdzielone poddrzewo.

Kryteria akceptacji:

- gotowy wynik Buildera jest niemutowalny,
- kolejność przechodzenia po strukturze jest jawna,
- przepełnienie nie zawija wartości,
- każdy Visitor obsługuje wszystkie dozwolone rodzaje węzłów,
- zmiana formatu trwałego jest oddzielona od refaktoryzacji modelu.

### 18.5. Mapa testów w projekcie

| Test | Chroniony kontrakt |
| --- | --- |
| `StrategyEquivalenceTest` | wszystkie warianty obliczenia i wspólna walidacja |
| `PolymorphismEquivalenceTest` | wyniki rodzajów i walidacja danych |
| `TypeCodeEquivalenceTest` | kody, normalizacja, kanonizacja i błędy |
| `FactoryEquivalenceTest` | wybór produktu oraz granica walidacji |
| `DecoratorEquivalenceTest` | wynik, kolejność audytu i tożsamość wyjątku |
| `StateEquivalenceTest` | dozwolone i niedozwolone przejścia |
| `ObserverContractTest` | kolejność, migawka, usuwanie i fail-fast |
| `CompositeRefactoringsTest` | drzewo, Builder, jeden lub wiele i akumulacja |
| `AdapterEquivalenceTest` | mapowanie interfejsu docelowego na protokół legacy |
| `CommandEquivalenceTest` | wszystkie klucze i niezmienność rejestru |
| `TemplateMethodEquivalenceTest` | oba formaty i finalny szkielet |
| `SingletonContractTest` | kanoniczna instancja i niemutowalne ustawienie |
| `ExtractCompositeEquivalenceTest` | wspólne dzieci, suma i bezpieczny widok |
| `Module6ExamplesTest` | uruchomienie demonstracji całego modułu |

### 18.6. Test różnicowy Composite

Fragment testu z projektu pokazuje niezależne oczekiwanie dla starego i nowego modelu:

```java
@Test
void explicitCompositePreservesTheImplicitPathTree() {
    List<LegacyPathPlan.Entry> entries = List.of(
            new LegacyPathPlan.Entry("release/database/backup", 5),
            new LegacyPathPlan.Entry("release/database/migrate", 8),
            new LegacyPathPlan.Entry("release/deploy", 3));
    var legacy = new LegacyPathPlan(entries);
    DeploymentGroup plan = new LegacyPathPlanMapper()
            .map("release", entries);
    List<DeploymentTask> tasks = new ArrayList<>();
    plan.collectTasks(tasks);

    assertEquals(16, legacy.totalMinutes());
    assertEquals(16, plan.totalMinutes());
    assertEquals(
            List.of("backup", "migrate", "deploy"),
            tasks.stream().map(DeploymentTask::name).toList());
    assertEquals(legacy.totalMinutes(), plan.totalMinutes());
    assertEquals(
            legacy.taskNamesBelow("release"),
            tasks.stream().map(DeploymentTask::name).toList());
    assertEquals(
            List.of("database", "deploy"),
            plan.components().stream().map(PlanComponent::name).toList());
    DeploymentGroup database = assertInstanceOf(
            DeploymentGroup.class, plan.components().get(0));
    assertEquals(
            List.of("backup", "migrate"),
            database.components().stream().map(PlanComponent::name).toList());
    List<DeploymentTask> databaseTasks = new ArrayList<>();
    database.collectTasks(databaseTasks);
    assertEquals(
            legacy.taskNamesBelow("release/database"),
            databaseTasks.stream().map(DeploymentTask::name).toList());
}
```

Test wywołuje rzeczywisty mapper starego formatu, porównuje zapytanie dla korzenia i podgrupy oraz sprawdza topologię. Samo porównanie wersji przed i po może jednak utrwalić błąd obu implementacji. Dlatego test zawiera również niezależne oczekiwania: `16` minut oraz konkretne nazwy i kolejność zadań.

## 19. Mapa decyzji

| Sytuacja | Rozważ | Uważaj na |
| --- | --- | --- |
| wymienny algorytm | Strategy | moment wyboru i stan strategii |
| trwały rodzaj obiektu | polimorfizm | fabryki, serializację i zgodność podtypów |
| surowy kod domenowy | obiekt typu | format trwały, równość i nieznane kody |
| skomplikowane tworzenie drzewa | Builder | częściowy wynik, cykle i ponowne użycie |
| wiedza o tworzeniu rozproszona | Factory | globalny rejestr i zbyt szeroka odpowiedzialność |
| opcjonalne zachowanie wokół rdzenia | Decorator | kolejność, wyjątki i tożsamość opakowania |
| zachowanie zależne od stanu | State | tabela przejść, atomowość i efekty |
| zmienni odbiorcy zdarzenia | Observer | kolejność, błędy, wątki i cykl życia |
| ukryta struktura drzewa | Composite | cykle, głębokość i format migracji |
| niezgodne API istniejącej klasy | Adapter | semantykę, jednostki i własność zasobów |
| dispatcher według rozłącznego klucza | Command | kompletność kluczy, duplikaty i stan komendy |
| stała sekwencja zmiennych kroków | Template Method | kruche hooki i ewolucję podklas |
| osobny kod dla jednego i wielu | Composite | kolejność i częściowe błędy |
| rzeczywiście jedna wspólna instancja | Singleton | globalny stan, class loadery i testowalność |
| prosta rekurencyjna agregacja | Collecting Parameter | wynik częściowy i mutowalność |
| wiele operacji na stabilnych typach | Visitor | koszt dodawania nowego rodzaju węzła |
| duplikacja obsługi dzieci w hierarchii | Extract Composite | różnice ukryte w podobnych pętlach |

## 20. Lista kontrolna przed zatwierdzeniem zmiany

### Kontrakt

- Czy nazwano zachowanie, które pozostaje niezmienione?
- Czy test obejmuje każdą dawną gałąź, `null` i wartość nieznaną?
- Czy zachowano typy wyjątków i stan po błędzie?
- Czy kolejność i liczba efektów są sprawdzane?
- Czy moment wyboru strategii albo komendy pozostał ten sam?

### Tworzenie i cykl życia

- Czy wszystkie punkty tworzenia zostały znalezione?
- Czy fabryka nie stała się globalnym rejestrem usług?
- Czy współdzielone strategie, komendy i State są bezstanowe albo bezpieczne wątkowo?
- Czy Singleton jest jeden w wymaganym zakresie, a nie tylko w jednym loaderze klas?
- Czy zasoby mają jawnego właściciela i moment zamknięcia?

### Kolekcje i struktury

- Czy gotowy Composite jest niemutowalny zgodnie z kontraktem?
- Czy kolejność dzieci jest jawna?
- Czy wykrywane są cykle i nadmierna głębokość, jeśli dane są niezaufane?
- Czy akumulacja ma politykę przepełnienia i błędu częściowego?
- Czy Builder ma zdefiniowane zachowanie po `build()`?

### Integracje

- Czy format trwałego kodu pozostał stabilny?
- Czy adapter zachowuje jednostki, strefy czasu, kodowanie i idempotencję?
- Czy obiekt opakowujący nie zmienił wymaganej tożsamości, monitora albo serializacji?
- Czy dodanie podtypu `sealed` zostało sprawdzone we wszystkich `switch`?
- Czy klienci binarni i refleksyjni są częścią wymaganej zgodności?

## 21. Pytania sprawdzające

1. Dlaczego usunięcie `switch` nie wystarcza do uzasadnienia Strategy?
2. Kiedy kod typu powinien stać się klasą wartości, a kiedy podtypem?
3. Dlaczego wybór strategii w konstruktorze może zmienić zachowanie?
4. Co odróżnia State od Strategy?
5. Dlaczego kolejność Decoratorów jest częścią kontraktu?
6. Jak zachowuje się iterator `CopyOnWriteArrayList` po usunięciu listenera podczas callbacku?
7. Kiedy przejście od jednego odbiorcy do kolekcji Observerów jest rozszerzeniem?
8. Co musi tłumaczyć Adapter oprócz nazw metod?
9. Kiedy mapy komend nie można uznać za równoważną łańcuchowi warunków?
10. Dlaczego rekord z komponentem `List` nie jest automatycznie głęboko niemutowalny?
11. Jaki wymiar zmian preferuje klasyczny Visitor?
12. Czym Extract Composite różni się od Replace Implicit Tree with Composite?
13. Dlaczego enum Singleton nie oznacza bezwzględnie jednej instancji w całym procesie?
14. Jakie zachowanie trzeba ustalić dla częściowego błędu podczas akumulacji?
15. Kiedy prosty `switch` jest lepszy od wzorca?

## 22. Odpowiedzi skrócone

1. Strategy dotyczy wymiennego algorytmu, a warunek może reprezentować stan, typ, komendę albo zwykłą lokalną regułę.
2. Klasa wartości pasuje do samego pojęcia i jego reguł. Podtyp pasuje do stabilnego rodzaju posiadającego własne zachowanie.
3. Może zamrozić decyzję wcześniej niż dawny warunek odczytywany przy każdym wywołaniu.
4. Strategy wykonuje wybrany algorytm, a State reprezentuje bieżący stan i wyznacza dozwolone przejścia.
5. Warstwy zmieniają zasięg retry, audytu, transakcji i obsługi wyjątków.
6. Iterator używa migawki, więc usunięcie wpływa na kolejną iterację, nie na już utworzoną.
7. Gdy stary kontrakt gwarantował dokładnie jeden efekt, a nowy dopuszcza dodatkowe efekty i nowe błędy.
8. Semantykę danych, jednostki, `null`, błędy, własność zasobów, idempotencję i model wykonania.
9. Gdy predykaty się nakładają, liczy się ich kolejność albo wybór nie jest pojedynczym kluczem.
10. Referencja rekordu jest finalna, ale wskazany obiekt może być mutowalny. Potrzebna jest kopia lub niemutowalny typ.
11. Częste dodawanie nowych operacji przy stabilnym zestawie rodzajów elementów.
12. Pierwsza wydziela powieloną obsługę dzieci z istniejącej hierarchii. Druga zastępuje prymitywną, ukrytą reprezentację jawnym drzewem obiektów.
13. Każdy niezależny loader klas może mieć własną definicję i własną stałą enumu.
14. Czy wynik częściowy zostaje, jest wycofywany, czy błąd jest agregowany i przetwarzanie trwa.
15. Gdy warunek jest mały, lokalny, stabilny i czytelniejszy niż dodatkowe typy.

## Podsumowanie

Refaktoryzacja do wzorca zaczyna się od kontraktu, a nie od diagramu klas. Strategy, polimorfizm, State i Command mogą usuwać podobnie wyglądające warunki, lecz modelują inne przyczyny zmiany. Factory i Builder kontrolują tworzenie. Decorator, Observer i Adapter kontrolują współpracę. Composite oraz Visitor organizują struktury i operacje na nich.

Najbezpieczniejsza transformacja wprowadza jeden kontrakt, przenosi jedną odpowiedzialność i po każdym kroku porównuje zachowanie. Wzorzec pozostaje tylko wtedy, gdy upraszcza realny problem. Jeżeli po refaktoryzacji jest więcej pośrednictwa niż potrzeb, ostatnim krokiem powinno być uproszczenie.
