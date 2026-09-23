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
# Moduł 6. Refaktoryzacje do wzorców projektowych
Warsztat praktyczny: wzorzec jako rezultat bezpiecznej, krokowej transformacji kodu legacy

---

## Agenda

1. Wzorzec jako rezultat refaktoryzacji
2. Strategy, polimorfizm, Replace Type Code with Class
3. Builder, Factory, Decorator
4. State, Observer, Composite (Implicit Tree)
5. Adapter, Command, Template Method
6. Replace Distinctions with Composite, Singleton
7. Collecting Parameter i Visitor, Extract Composite
8. Warsztat praktyczny, mapa decyzji, lista kontrolna

---

## 1.1. Najpierw rodzaj zmienności

| Pytanie | Typowa technika |
| --- | --- |
| Czy zmienia się algorytm wybierany dla operacji? | Strategy |
| Czy zachowanie należy do trwałego rodzaju obiektu? | polimorfizm podtypów |
| Czy zachowanie zależy od bieżącego stanu i przejść? | State |
| Czy selektor reprezentuje żądanie kierowane do wykonawcy? | Command |
| Czy kod jedynie tworzy właściwy obiekt? | Factory |

Ten sam `switch` może sygnalizować różne problemy - najpierw ustal, **co naprawdę jest zmienne** i czy zestaw wariantów jest otwarty, czy zamknięty.

---

## 1.2. Refaktoryzacja a zmiana projektu

- Refaktoryzacja zachowuje obserwowalne zachowanie w przyjętej granicy - wprowadzenie wzorca tego nie zmienia.
- Kusi: nowe warianty, inna obsługa nieznanego kodu, asynchroniczność, kontynuacja po błędzie listenera.
- Podobnie: dowolna liczba odbiorców, cache i współdzielenie instancji, nowy format trwałych danych.
- Każdy taki krok to **rozszerzenie zachowania** - oddziel go od transformacji strukturalnej i zatwierdź osobno.

---

## 1.3. Co trzeba zachować

- Nie tylko wynik: typ wyjątku (i komunikat, jeśli jest kontraktem), stan po sukcesie i po błędzie.
- Liczba i kolejność efektów ubocznych, moment wyboru wariantu i odczytu konfiguracji.
- W callbackach: wątek, kolejność odbiorców, polityka błędów, tożsamość przekazanego obiektu.
- `null`, wartość nieznana, gałąź domyślna; atomowość przejść i używany monitor.
- Zgodność danych, refleksji i publicznego API.

---

## 1.4. Bezpieczna pętla pracy

1. Nazwij problem i granicę obserwowalnego zachowania.
2. Dodaj test charakterystyki dla każdej gałęzi i każdej awarii.
3. Wprowadź minimalny kontrakt, początkowo delegujący do starego kodu.
4. Przenoś jedną gałąź lub efekt naraz, testy po każdym ruchu.
5. Stary warunek usuń po migracji wszystkich klientów; nadmiar wzorca usuń w osobnym kroku.

Pierwszy commit ma być **mały i odwracalny** - nie zaczynamy od tworzenia wszystkich klas wzorca.

---

## 1.5. Istotne mechanizmy Javy 25

- Interfejs funkcyjny nie może być `sealed`; `sealed` służy zamkniętemu modelowi, nie otwartemu punktowi wstrzykiwania.
- Lambda **nie ma stabilnej tożsamości** - nie porównuj przez `==`, nie wyrejestrowuj „taką samą” lambdą.
- Rekord jest płytko niemutowalny - stąd `List.copyOf` w `DeploymentGroup` (chroni listę, nie elementy).
- `sealed` nie daje niemutowalności; `volatile` daje widoczność referencji, nie atomowość przejścia.

---

## 2.1. Strategy - przed i po

```java
// przed: LegacyDeploymentCostCalculator
return switch (Objects.requireNonNull(mode, "mode")) {
    case STANDARD -> baseCostInCents;
    case EXPEDITED -> Math.addExact(baseCostInCents, baseCostInCents / 4);
};
```

```java
// po: kontekst z wstrzykniętą polityką
@FunctionalInterface
public interface DeploymentCostPolicy { long calculate(long baseCostInCents); }

public long calculate(long baseCostInCents) {
    if (baseCostInCents < 0) throw new IllegalArgumentException("base cost must not be negative");
    return policy.calculate(baseCostInCents);
}
```

Wspólna walidacja zostaje w kontekście; strategie są bezstanowe i współdzielone.

---

## 2.2. Strategy - intencja, sekwencja, ryzyka

- Kandydat: kilka wariantów tego samego obliczenia, wybieranych **niezależnie od klasy obiektu**.
- Samo `if` nie uzasadnia Strategy - dla prostego, stabilnego warunku interfejs pogarsza czytelność.
- Sekwencja: tabela decyzji (`null`, nieznany, przepełnienie) → interfejs ze strategią przejściową → gałęzie do strategii → wybór w korzeniu kompozycji.
- Wybór w konstruktorze **zamraża** decyzję - nie jest równoważny odczytowi konfiguracji przy każdym wywołaniu.
- `Function`/`ToLongFunction` nie deklarują wyjątków kontrolowanych - wtedy własny interfejs z `throws`.

---

## 2.3. Polimorfizm - przed i po

```java
// przed: pole value ma znaczenie zależne od kind
public record LegacyDeploymentStep(Kind kind, String value) {
    public String execute() {
        return switch (kind) {
            case SCRIPT -> "executed:" + value;
            case APPROVAL -> "approved-by:" + value;
        };
    }
}
```

```java
// po: zamknięty zestaw rodzajów, każdy z własnymi danymi
public sealed interface DeploymentStep permits ScriptStep, ApprovalStep { String execute(); }
public record ScriptStep(String command) implements DeploymentStep {
    public String execute() { return "executed:" + command; }
}
```

---

## 2.4. Polimorfizm - kiedy i pułapki

- Pasuje, gdy selektor opisuje **stabilny rodzaj** obiektu; tryb przejściowy → State, algorytm wybierany przez klienta → Strategy.
- Najpierw znajdź tworzenie, deserializację, mapowanie ORM i podobne `switch` w innych usługach.
- Twórz podtypy pojedynczo, po jednej gałęzi; jeden test kontraktowy dla wszystkich implementacji.
- Pola i metody `static`/`private`/`final` nie są polimorficzne; nie wywołuj hooka z konstruktora bazy.
- Nowy podtyp `sealed` może ujawnić wyczerpujące `switch` także poza modułem.

---

## 2.5. Replace Type Code with Class - `DeploymentZone`

```java
public final class DeploymentZone {
    public static final DeploymentZone TEST = new DeploymentZone("TEST", false);
    public static final DeploymentZone PRODUCTION = new DeploymentZone("PROD", true);
    public static final DeploymentZone DISASTER_RECOVERY = new DeploymentZone("DR", true);

    public static DeploymentZone fromCode(String code) {
        if (code == null || code.isBlank()) throw new IllegalArgumentException("zoneCode must not be blank");
        String normalized = code.toUpperCase(Locale.ROOT);
        DeploymentZone zone = BY_CODE.get(normalized);
        if (zone == null) throw new IllegalArgumentException("unknown zone code: " + normalized);
        return zone;
    }
    public boolean requiresApproval() { return approvalRequired; }
}
```

Zamiast surowego `String zoneCode` z rozproszoną regułą zatwierdzenia (`PROD`, `DR`).

---

## 2.6. Type Code - decyzje i granica trwałości

- Nowy typ przejmuje **walidację, normalizację i operacje** pojęcia; nie oznacza automatycznie podklas.
- Enum dla prostego, zamkniętego zestawu; klasa przy aliasach, kodach zewnętrznych i własnej polityce tworzenia.
- Prywatny konstruktor i fabryka kanonizują instancje - inaczej potrzebne `equals`/`hashCode`.
- W bazie zostaje stabilny kod `PROD`, nie nazwa klasy ani `toString`.
- Zachowaj normalizację `Locale.ROOT`, obsługę `null`, pustego i nieznanego kodu.

---

## 3.1. Encapsulate Composite with Builder

```java
DeploymentGroup plan = PlanBuilder.group("release")
        .group("database", group -> group
                .task("backup", 5)
                .task("migrate", 8))
        .task("deploy", 3)
        .build();
```

- Uzasadniony, gdy budowa drzewa jest wieloetapowa i ma **własne niezmienniki**.
- `PlanBuilder` jest jednorazowy: każde wywołanie po `build()` rzuca `IllegalStateException`; wynik kopiuje listę.
- Lambda grupy ma kontrakt synchroniczny - odroczenie lub ponowne wywołanie zmienia zachowanie.
- Ustal: pusta grupa, unikalność nazw, kolejność dzieci; współdzielony podplan (DAG) liczy się dla każdej ścieżki.

---

## 3.2. Factory - publiczna granica tworzenia

```java
public final class DeploymentProbeFactory {
    public DeploymentProbe create(ProbeKind kind, String target) {
        Objects.requireNonNull(kind, "kind");
        // walidacja target ...
        return switch (kind) {
            case HTTP -> new HttpProbe(target);
            case QUEUE -> new QueueProbe(target);
        };
    }
    private record HttpProbe(String endpoint) implements DeploymentProbe { ... }
    private record QueueProbe(String queueName) implements DeploymentProbe { ... }
}
// ProbeService: return factory.create(kind, target).check();
```

`switch` zostaje - to właściwe miejsce wiedzy o konstrukcji.

---

## 3.3. Factory - intencja, procedura, pułapki

- **Encapsulate Classes with Factory** ukrywa klasy konkretne; **Extract Factory Class** wydziela tworzenie z klasy o innej odpowiedzialności.
- Zinwentaryzuj konstruktory, refleksję, DI, deserializatory; przekierowuj tworzenie pojedynczo, widoczność ogranicz na końcu.
- Tworzenie wariantów z wyprzedzeniem może uruchomić kosztowne efekty wcześniej niż stara gałąź.
- Fabryka nie może stać się **globalnym rejestrem usług**.
- Ukrycie publicznego konstruktora w bibliotece wymaga etapu `@Deprecated`.

---

## 3.4. Move Embellishment to Decorator

```java
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
```

- Opcjonalny dodatek wokół rdzenia (audyt, retry, cache) z **tym samym kontraktem** co rdzeń.
- **Kolejność jest zachowaniem:** `audit(retry(core))` to jeden wpis, `retry(audit(core))` - wpis na próbę.

---

## 3.5. Decorator - wyjątki, migracja, przezroczystość

- Zachowana jest instancja wyjątku delegata, ale błąd samego audytu może **zasłonić** błąd rdzenia - potrzebna jawna polityka.
- Migracja: wąski interfejs, zapis kolejności dla sukcesu i awarii, dekorator przekazujący, potem po jednym dodatku.
- Składanie łańcucha w kontrolowanej fabryce; dawną flagę usuń po migracji.
- Nieprzezroczysty dla `getClass`, tożsamości, serializacji i monitora `synchronized`.
- Ślepe delegowanie `equals` łamie symetrię.

---

## 4.1. Replace State-Altering Conditionals with State

| Stan | approve | deploy | cancel |
| --- | --- | --- | --- |
| DRAFT | APPROVED | wyjątek | CANCELLED |
| APPROVED | wyjątek | DEPLOYED | CANCELLED |
| DEPLOYED | wyjątek | wyjątek | wyjątek |
| CANCELLED | wyjątek | wyjątek | wyjątek |

- State, gdy warunki opisują zachowanie i przejścia bieżącego stanu; tabelę zapisz **przed** hierarchią.
- Pusta metoda domyślna nie zastąpi dotychczasowego wyjątku; stan po niedozwolonej operacji się nie zmienia.

---

## 4.2. State - kontekst delegujący

```java
public final class Release {
    private ReleaseState state = DraftState.INSTANCE;
    public void approve() { state = state.approve(); }
    public void deploy()  { state = state.deploy(); }

    private sealed interface ReleaseState permits DraftState, ApprovedState, ... {
        Status status();
        default ReleaseState approve() { throw invalid("approve"); }
        default ReleaseState deploy()  { throw invalid("deploy"); }
    }
    private enum DraftState implements ReleaseState {
        INSTANCE;
        public Status status() { return Status.DRAFT; }
        public ReleaseState approve() { return ApprovedState.INSTANCE; }
    }
}
```

Stany to bezstanowe stałe enumu; dane wydania zostają w `Release`.

---

## 4.3. State - kolejność przejścia i efektów

- Kontrakt efektów: efekt, potem stan; stan, potem efekt; outbox; albo rozdzielenie stanu i próby efektu.
- Żaden wariant nie jest uniwersalny - refaktoryzacja zachowuje dotychczasowy moment zmiany stanu.
- `volatile` daje widoczność referencji, nie atomowość sekwencji sprawdzenie-efekt-zapis.
- Funkcja w `updateAndGet` może wykonać się wielokrotnie - bez nieidempotentnych efektów.
- Przy dwóch stanach `switch` bywa czytelniejszy; kilka osi stanu grozi eksplozją klas.

---

## 4.4. Replace Hard-coded Notifications with Observer

```java
private final CopyOnWriteArrayList<Registration> listeners = new CopyOnWriteArrayList<>();

public Subscription subscribe(ReleaseListener listener) {
    Registration registration = new Registration(Objects.requireNonNull(listener, "listener"));
    listeners.add(registration);
    AtomicBoolean active = new AtomicBoolean(true);
    return () -> {
        if (active.compareAndSet(true, false)) listeners.remove(registration);
    };
}

public void publish(ReleasePublished event) {
    for (Registration registration : listeners) registration.notifyListener(event);
}
```

Osobny `Registration` na `subscribe` - uchwyt usuwa dokładnie własną rejestrację, idempotentnie.

---

## 4.5. Observer - kontrakt i migracja

- Subject zna tylko interfejs listenera; pierwszy krok zachowuje relację **jeden do jednego**.
- Wielu odbiorców to często **rozszerzenie** zachowania, nie refaktoryzacja.
- Kontrakt: synchroniczność i wątek, kolejność, duplikaty, wyjątki (tu fail-fast), wyrejestrowanie.
- Iterator na migawce: listener usunięty w trakcie publikacji może być jeszcze wywołany w bieżącej.
- Niezamknięty uchwyt wydłuża życie grafu obiektów; globalna szyna zdarzeń ukrywa zależności.

---

## 4.6. Replace Implicit Tree with Composite

```java
public sealed interface PlanComponent permits DeploymentTask, DeploymentGroup {
    String name();
    long totalMinutes();
    void collectTasks(Collection<? super DeploymentTask> target);
    <R> R accept(PlanVisitor<R> visitor);
}

public record DeploymentGroup(String name, List<PlanComponent> components)
        implements PlanComponent {
    public long totalMinutes() {
        long total = 0;
        for (PlanComponent c : components) total = Math.addExact(total, c.totalMinutes());
        return total;
    }
}
```

Zamiast ścieżek `release/database/backup` i wyszukiwania po prefiksie `path + "/"`.

---

## 4.7. Implicit Tree - mapper, procedura, ryzyka

- `LegacyPathPlanMapper` mapuje stary format; odrzuca ścieżkę będącą jednocześnie zadaniem i grupą.
- Kolejność `a/first`, `b/second`, `a/third` jest nieodwzorowalna - mapper **zgłasza błąd** zamiast po cichu zmienić wynik.
- Przenoś po jednej operacji (np. sumowanie) i porównuj obie reprezentacje; format trwały zmieniaj osobno.
- Głębokie drzewa i cykle: `StackOverflowError` lub nieskończona pętla - limit głębokości dla danych niezaufanych.
- `Math.addExact` czyni przepełnienie jawnym błędem.

---

## 5.1. Unify Interfaces with Adapter

```java
public final class LegacyGatewayAdapter implements ReleaseNotifier {
    private final LegacyMessageGateway gateway;

    @Override
    public String send(ReleaseMessage message) {
        Objects.requireNonNull(message, "message");
        return gateway.transmit(message.recipient(), "release:" + message.releaseId());
    }
}
```

- Klient (`NotificationService`) zależy tylko od **preferowanego kontraktu** `ReleaseNotifier`.
- Adapter tłumaczy nazwy, parametry, wyniki i błędy, ale nie udaje zgodności semantycznej.

---

## 5.2. Adapter - co naprawdę trzeba przetłumaczyć

- Podobna sygnatura ≠ podobny kontrakt: jednostki, strefa czasowa, precyzja, kodowanie.
- Znaczenie `null` i pustej wartości, indeksowanie, mutowalność wejścia i wyniku.
- Wyjątki mapowane z zachowaniem przyczyny; idempotencja, wątek wykonania.
- Adapter `AutoCloseable` musi wiedzieć, czy jest **właścicielem** zasobu.
- Jeśli wystarczy Rename Method lub Move Method, adapter jest zbędny.

---

## 5.3. Replace Conditional Dispatcher with Command

```java
public DeploymentCommandDispatcher(Map<DeploymentAction, DeploymentCommand> commands) {
    Map<DeploymentAction, DeploymentCommand> copy = Map.copyOf(commands);
    EnumSet<DeploymentAction> missing = EnumSet.allOf(DeploymentAction.class);
    missing.removeAll(copy.keySet());
    if (!missing.isEmpty()) throw new IllegalArgumentException("missing commands: " + missing);
    this.commands = copy;
}
```

- Każda akcja staje się obiektem, rejestr mapuje selektor na komendę.
- Mapa jest równoważna tylko przy **rozłącznym kluczu** - nie przy nakładających się predykatach.
- Sprawdzenie kompletności przenosi błąd konfiguracji na moment składania aplikacji.

---

## 5.4. Command - sekwencja i ograniczenia

- Scharakteryzuj klucz, priorytet gałęzi, `default` i `null`.
- Wydziel ciała gałęzi do metod przy zachowanym `switch`, przenieś do komend, na końcu rejestr.
- Command **nie daje automatycznie** asynchroniczności, retry ani undo - `Executor` zmienia wątek, wyjątki i transakcję.
- Retry może powielić nieidempotentne efekty; undo wymaga modelu kompensacji.
- Komendy współdzielone są bezstanowe; dane żądania to argument.

---

## 5.5. Apply Template Method

```java
public abstract class ReleaseImporter {
    public final ReleaseDraft importRelease(String raw) {
        if (raw == null || raw.isBlank()) throw new IllegalArgumentException("input must not be blank");
        Fields fields = parse(raw);
        // walidacja releaseId i service
        return new ReleaseDraft(fields.releaseId(), fields.service());
    }
    protected abstract Fields parse(String raw);
    protected final Fields fields(String releaseId, String service) { ... }
}
```

- Importery wykonują te same kroki w tej samej kolejności - zmienne jest tylko **parsowanie**.
- `final` chroni kolejność, ale dodany do opublikowanej, nadpisywanej metody łamie klientów.

---

## 5.6. Template Method - sekwencja i ryzyka

- Extract Method w każdej klasie, ujednolicaj sygnatury po jednym kroku, Pull Up Method dla szkieletu.
- Hook opcjonalny tylko przy poprawnym domyślnym zachowaniu dla wszystkich podklas.
- Nowa metoda abstrakcyjna psuje podklasy; hook z konstruktora bazy widzi domyślne pola podklasy.
- `synchronized` w szablonie trzyma monitor podczas hooków - ryzyko zakleszczenia.
- Wiele hooków lub zmienna kolejność → lepsza kompozycja lub Strategy.

---

## 6.1. Replace One/Many Distinctions with Composite

```java
public List<String> execute(PlanComponent component) {
    Objects.requireNonNull(component, "component");
    List<DeploymentTask> tasks = new ArrayList<>();
    component.collectTasks(tasks);
    return tasks.stream().map(task -> "executed:" + task.name()).toList();
}
```

- Jeden kontrakt zamiast `execute` i `executeAll` z powielonymi regułami.
- Nie zmieniaj: kolejności (tu preorder), pustej grupy, przerwania po pierwszym błędzie, liczby transakcji.
- **Safe Composite:** zarządzanie dziećmi tylko w grupie; **Transparent:** we wspólnym kontrakcie, więc `add` na liściu wymaga wyjątku albo pustego zachowania.

---

## 6.2. Limit Instantiation with Singleton

```java
public enum DeploymentDefaults {
    INSTANCE;
    private final Duration healthCheckTimeout = Duration.ofSeconds(30);
    public Duration healthCheckTimeout() { return healthCheckTimeout; }
}
```

- To **decyzja o cyklu życia** - najpierw zmierz koszt i udowodnij równoważność instancji.
- Enum: bezpieczna publikacja i serializacja, ale **jedna instancja na loader klas**.
- Globalny mutowalny stan łączy testy i ukrywa zależności - zwykle lepsze jawne wstrzyknięcie lub zakres DI.
- Odwrotna transformacja: **Inline Singleton**.

---

## 7.1. Collecting Parameter

- Metoda mutująca lokalny wynik → elementy same dopisują wynik do **przekazanego akumulatora**.
- `collectTasks(Collection<? super DeploymentTask>)`: liść dodaje siebie, grupa przekazuje akumulator dzieciom.
- Wywołujący zachowuje własność kolekcji; ustal, czy metoda czyści, czy dopisuje.
- Po wyjątku może zostać **wynik częściowy** - to część kontraktu.
- Dobry parametr zbierający ma wąski typ; ogólna mutowalna mapa ukrywa zbyt wiele.

---

## 7.2. Visitor i macierz zmian

```java
public final class TotalMinutesVisitor implements PlanVisitor<Long> {
    public Long visitTask(DeploymentTask task) { return task.minutes(); }
    public Long visitGroup(DeploymentGroup group) {
        long total = 0;
        for (PlanComponent c : group.components()) total = Math.addExact(total, c.accept(this));
        return total;
    }
}
```

| Częsta zmiana | Naturalniejszy kierunek |
| --- | --- |
| nowe operacje, stabilne rodzaje węzłów | Visitor |
| nowe rodzaje węzłów, stabilne operacje | metody polimorficzne |
| jedna prosta akumulacja | Collecting Parameter |

---

## 7.3. Extract Composite

```java
public abstract static class CompositePlanNode implements PlanNode {
    private final List<PlanNode> children = new ArrayList<>();
    public final void add(PlanNode child) { children.add(Objects.requireNonNull(child, "child")); }
    public final List<PlanNode> children() { return List.copyOf(children); }
    @Override public final long totalMinutes() { /* suma przez Math.addExact */ }
}
public static final class ReleaseGroup extends CompositePlanNode { }
public static final class RollbackGroup extends CompositePlanNode { }
```

- Punkt wyjścia: kilka klas hierarchii **powiela obsługę dzieci** (`ReleaseGroup`, `RollbackGroup`).
- Podobne pętle mogą znaczyć co innego (pomijanie, kontynuacja po błędzie, równoległość) - porównaj kontrakty.
- Wykrywanie cykli to osobna zmiana zachowania.

---

## Ćwiczenie 1: wybór właściwego mechanizmu (45-60 min)

- Cel: cztery osie w serwisie (koszt, rodzaj kroku, stan wydania, akcja operatora) → Strategy, polimorfizm, State albo Command.
- Testy charakterystyki dla gałęzi domyślnej i `null`; jeden kontrakt naraz.
- Stan przejściowy nie jest strategią; komendy nie przechowują danych poprzedniego żądania.

Szczegóły: zadania modułu 6, Ćwiczenie 1.

---

## Ćwiczenie 2: State, Observer i Decorator (60 min)

- Cel: dodać do wydania audyt i powiadomienie odbiorców, jawnie zapisując kolejność stanu, audytu i publikacji.
- Przetestuj listener usuwający inną subskrypcję i listener rzucający wyjątek.
- Bez niejawnej asynchroniczności; błąd dodatku nie zasłania pierwotnej przyczyny.

Szczegóły: zadania modułu 6, Ćwiczenie 2.

---

## Ćwiczenie 3: rodzina refaktoryzacji Composite (60-90 min)

- Cel: zastąpić ścieżki tekstowe jawnym Composite z mapperem, dodać jeden/wiele, Builder, Collecting Parameter i Visitor.
- Przetestuj puste drzewo, głęboką strukturę, przepełnienie i współdzielone poddrzewo.
- Test różnicowy z **niezależnymi oczekiwaniami**; zmiana formatu trwałego oddzielona od refaktoryzacji.

Szczegóły: zadania modułu 6, Ćwiczenie 3.

---

## Mapa decyzji (1/3)

| Sytuacja | Rozważ | Uważaj na |
| --- | --- | --- |
| wymienny algorytm | Strategy | moment wyboru, stan strategii |
| trwały rodzaj obiektu | polimorfizm | fabryki, serializacja |
| surowy kod domenowy | obiekt typu | format trwały, nieznane kody |
| skomplikowane tworzenie drzewa | Builder | częściowy wynik, ponowne użycie |
| rozproszona wiedza o tworzeniu | Factory | globalny rejestr |
| opcjonalne zachowanie wokół rdzenia | Decorator | kolejność, wyjątki, tożsamość |

---

## Mapa decyzji (2/3)

| Sytuacja | Rozważ | Uważaj na |
| --- | --- | --- |
| zachowanie zależne od stanu | State | tabela przejść, atomowość |
| zmienni odbiorcy zdarzenia | Observer | kolejność, błędy, wątki |
| ukryte drzewo | Composite | cykle, głębokość, migracja |
| niezgodne API | Adapter | semantyka, własność zasobów |
| dispatcher po rozłącznym kluczu | Command | kompletność, stan komendy |
| stała sekwencja zmiennych kroków | Template Method | kruche hooki |

---

## Mapa decyzji (3/3)

| Sytuacja | Rozważ | Uważaj na |
| --- | --- | --- |
| osobny kod dla jednego i wielu | Composite | kolejność, częściowe błędy |
| rzeczywiście jedna wspólna instancja | Singleton | globalny stan, testowalność |
| prosta rekurencyjna agregacja | Collecting Parameter | wynik częściowy, mutowalność |
| wiele operacji na stabilnych typach | Visitor | koszt nowego rodzaju węzła |
| duplikacja obsługi dzieci | Extract Composite | różnice ukryte w podobnych pętlach |

---

## Lista kontrolna

- **Kontrakt:** testy obejmują każdą gałąź, `null` i wartość nieznaną; zachowane wyjątki, stan po błędzie, kolejność efektów.
- **Tworzenie:** znalezione wszystkie punkty tworzenia; współdzielone strategie, komendy i stany bezstanowe.
- **Struktury:** gotowy Composite niemutowalny, kolejność jawna, polityka przepełnienia i błędu częściowego.
- **Integracje:** stabilny format trwały; adapter zachowuje jednostki i idempotencję; opakowanie nie zmienia wymaganej tożsamości.
- **Zgodność:** nowy podtyp `sealed` sprawdzony we wszystkich `switch`; klienci binarni i refleksyjni uwzględnieni.

---

## Pytania sprawdzające

1. Dlaczego usunięcie `switch` nie wystarcza do uzasadnienia Strategy?
2. Kiedy kod typu staje się klasą wartości, a kiedy podtypem?
3. Co odróżnia State od Strategy?
4. Dlaczego kolejność Decoratorów jest kontraktem?
5. Kiedy kolekcja Observerów jest rozszerzeniem zachowania?
6. Kiedy mapa komend nie jest równoważna warunkom?
7. Dlaczego enum Singleton to nie zawsze jedna instancja?

---

## Podsumowanie - najważniejsze wnioski

- Refaktoryzacja do wzorca zaczyna się od **kontraktu**, nie od diagramu klas.
- Strategy, polimorfizm, State i Command usuwają podobne warunki, ale modelują **różne przyczyny zmiany**.
- Factory i Builder kontrolują tworzenie; Decorator, Observer i Adapter - współpracę; Composite i Visitor - struktury.
- Jeden kontrakt, jedna przeniesiona odpowiedzialność, porównanie zachowania po każdym kroku.
- Rozszerzenia (asynchroniczność, wielu odbiorców, cache) zatwierdzaj osobno.
- Wzorzec zostaje tylko, gdy upraszcza realny problem.
