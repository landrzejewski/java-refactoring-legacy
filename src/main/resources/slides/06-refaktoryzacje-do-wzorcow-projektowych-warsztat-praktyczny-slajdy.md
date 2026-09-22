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

## Najpierw rodzaj zmienności

| Pytanie | Typowa technika |
| --- | --- |
| Czy zmienia się algorytm wybierany dla operacji? | Strategy |
| Czy zachowanie należy do trwałego rodzaju obiektu? | polimorfizm podtypów |
| Czy zachowanie zależy od bieżącego stanu i przejść? | State |
| Czy selektor reprezentuje żądanie kierowane do wykonawcy? | Command |
| Czy kod jedynie tworzy właściwy obiekt? | Factory |

- Ten sam `switch` może sygnalizować różne problemy, więc sama obecność warunku nie wystarcza do wyboru wzorca.
- Najpierw ustal, co naprawdę jest zmienne, kto powinien znać wariant i czy zestaw wariantów jest otwarty, czy zamknięty.

---

## Refaktoryzacja a zmiana projektu

- Refaktoryzacja zachowuje obserwowalne zachowanie w przyjętej granicy - wprowadzenie wzorca tego nie zmienia.
- Kusi, by przy okazji dodać warianty, zmienić obsługę nieznanego kodu, przejść na asynchroniczność lub kontynuować po błędzie listenera.
- Podobnie kuszą: dowolna liczba odbiorców, cache i współdzielenie instancji, zmiana formatu trwałych danych.
- Każdy taki krok może być wartościowy, ale jest **rozszerzeniem zachowania** - należy go oddzielić od transformacji strukturalnej i zatwierdzić osobno.

---

## Co trzeba zachować

- Sama wartość zwracana to za mało: liczy się dokładny typ wyjątku (i komunikat, jeśli jest kontraktem) oraz stan obiektu po sukcesie i po błędzie.
- Liczba i kolejność efektów ubocznych, moment wyboru wariantu i odczytu konfiguracji muszą pozostać takie same.
- W callbackach ważne są: wątek wykonania, kolejność odbiorców, polityka błędów i tożsamość przekazanego obiektu.
- Trzeba też zachować zachowanie dla `null`, wartości nieznanej i gałęzi domyślnej, atomowość przejść i używany monitor.
- Nie wolno zapominać o zgodności danych, refleksji i publicznego API.

---

## Bezpieczna pętla pracy

1. Nazwij konkretny problem w aktualnym kodzie i zapisz granicę obserwowalnego zachowania.
2. Dodaj test charakterystyki dla każdej gałęzi i każdej awarii.
3. Wprowadź minimalny kontrakt, początkowo delegujący do starego kodu.
4. Przenoś jedną gałąź, odpowiedzialność albo efekt naraz i po każdym ruchu uruchamiaj mały zestaw testów.
5. Stary warunek lub klasę usuń dopiero po migracji wszystkich klientów.
6. W osobnym kroku oceń uproszczenie i usuń niepotrzebne elementy wzorca.

---

## Istotne mechanizmy Javy 25

- Interfejs funkcyjny pozwala na lambdę, ale nie może być `sealed`; `sealed` służy zamkniętemu modelowi domenowemu, a nie otwartemu punktowi wstrzykiwania strategii.
- Lambda nie ma stabilnej tożsamości - nie porównuj jej przez `==`, nie używaj jako monitora i nie wyrejestrowuj listenera „taką samą” nową lambdą.
- Rekord jest płytko niemutowalny, dlatego `DeploymentGroup` kopiuje listę przez `List.copyOf`.
- `List.copyOf`/`Map.copyOf` dają niemodyfikowalne widoki i odrzucają `null`, ale nie gwarantują nowej tożsamości ani nie chronią samych elementów.
- `sealed` nie czyni obiektów niemutowalnymi ani bezpiecznymi wątkowo, a `volatile` daje widoczność referencji, nie atomowość sekwencji przejścia.

---

## Replace Conditional Logic with Strategy - intencja

- Strategy zastępuje warunek wybierający jeden z kilku wariantów algorytmu; kontekst zachowuje proces i wspólną walidację, a obliczenie deleguje do polityki.
- Dobry kandydat ma kilka wariantów tego samego obliczenia, wybieranych niezależnie od klasy obiektu domenowego.
- Nowe warianty pojawiają się częściej niż zmienia się otaczający proces, a test algorytmu nie wymaga całego kontekstu.
- Wybór wariantu może zostać wykonany na granicy składania aplikacji.
- Samo `if` w metodzie nie uzasadnia Strategy - dla prostego, stabilnego warunku dodatkowy interfejs pogarsza czytelność.

---

## Strategy - przed i po

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

- Walidacja nieujemnej wartości pozostaje wspólnym kontraktem kalkulatora; konkretne strategie są bezstanowe i można je współdzielić.

---

## Strategy - bezpieczna sekwencja

1. Zapisz tabelę decyzji, w tym `null`, przypadek nieznany i przepełnienie; wydziel obliczenie do metody bez zmiany warunku.
2. Wprowadź interfejs strategii o identycznym kontrakcie wyniku i wyjątków oraz strategię przejściową ze starym warunkiem.
3. Przenoś gałęzie do osobnych strategii, a wybór polityki do fabryki lub korzenia kompozycji; stary selektor usuń na końcu.

- Wybór strategii w konstruktorze **zamraża** go na czas życia obiektu - to nie jest równoważne kodowi, który czytał konfigurację przy każdym wywołaniu.
- `Function` i `ToLongFunction` nie deklarują wyjątków kontrolowanych, więc przy takich wyjątkach potrzebny jest własny interfejs z `throws`.

---

## Replace Conditional with Polymorphism - kiedy?

- Polimorfizm pasuje, gdy selektor opisuje **stabilny rodzaj** obiektu, a zachowanie naturalnie do tego rodzaju należy - klasa instancji nie zmienia się w trakcie życia.
- Jeśli zmienność to przejściowy tryb, rozważ State; jeśli klient wybiera algorytm niezależnie od rodzaju - Strategy.
- Przed zmianą znajdź wszystkie miejsca tworzenia, deserializacji, mapowania ORM i odtwarzania historycznego kodu typu.
- Usunięcie centralnego `switch` nic nie da, jeśli podobne rozgałęzienia zostają w innych usługach.

---

## Polimorfizm - przed i po

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

- Stary model dopuszczał niepoprawne kombinacje danych; `sealed` jest tu świadomym wyborem, w systemie wtyczek byłby nieodpowiedni.

---

## Polimorfizm - sekwencja migracji i pułapki

- Scharakteryzuj każdą gałąź (z domyślną), zamknij tworzenie obiektów za kontrolowaną granicą i wprowadź wspólną operację w nadtypie.
- Twórz podtypy pojedynczo, przenosząc po jednej gałęzi; kod typu usuń dopiero, gdy nie steruje inną osią zachowania, i uruchom jeden test kontraktowy dla wszystkich implementacji.
- Dyspozycja dynamiczna dotyczy tylko zwykłych metod instancyjnych: pola nie są polimorficzne, `static` są ukrywane, `private` i `final` nie są nadpisywane.
- Nie wywołuj nowego hooka z konstruktora bazy - trafi do częściowo zainicjalizowanego podtypu.
- Nowy podtyp w hierarchii `sealed` może ujawnić stare wyczerpujące `switch`, także poza bieżącym modułem.

---

## Replace Type Code with Class - intencja

- Surowy `String` czy liczba nie pilnuje poprawnych wartości, normalizacji ani operacji pojęcia domenowego - nowy typ przejmuje ten kontrakt.
- Technika nie oznacza automatycznie podklas: do nazwanych wariantów wystarczy klasa wartości albo enum.
- Gdy kod steruje zachowaniem trwałych rodzajów, kolejnym krokiem może być polimorfizm; gdy przejściowym trybem - State.
- W przykładzie `LegacyDeploymentRequest` trzyma `zoneCode` jako `String`, a reguła zatwierdzenia (`PROD`, `DR`) jest rozproszona - każdy klient może inaczej traktować wielkość liter, nieznane kody i `null`.

---

## Obiekt typu `DeploymentZone`

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

- Prywatny konstruktor i fabryka kanonizują trzy instancje; gdyby mogły powstawać niezależnie, potrzebne byłyby `equals`/`hashCode` lub rekord.

---

## Type Code - enum czy klasa i granica trwałości

- Enum wystarcza dla zamkniętego, prostego zestawu; osobna klasa przydaje się przy migracji kodów zewnętrznych, aliasach i własnej polityce tworzenia.
- W bazie i komunikatach może zostać stabilny kod `PROD`, mapowany wewnątrz aplikacji przez `DeploymentZone.fromCode`.
- Nie zapisuj nazwy klasy implementacyjnej ani wyniku `toString`, jeśli nie jest jawnie częścią formatu.
- Migracja musi zachować normalizację przez `Locale.ROOT`, obsługę `null`, pustego i nieznanego kodu oraz dokładny kod zapisywany na granicy.
- Równie ważne są reguły równości w mapach i zbiorach oraz zgodność historycznych danych.

---

## Encapsulate Composite with Builder - problem

- Composite upraszcza używanie elementu i grupy, ale ręczne budowanie drzewa bywa powtarzalne i podatne na błędy.
- Klient musi znać kolejność tworzenia węzłów, zasady dodawania dzieci i moment zamknięcia struktury.
- Builder jest uzasadniony, gdy budowa jest skomplikowana, wieloetapowa albo ma własne niezmienniki.
- Nie powinien powielać logiki drzewa ani udostępniać częściowo zbudowanego wyniku.

---

## Builder planu wdrożenia

```java
DeploymentGroup plan = PlanBuilder.group("release")
        .group("database", group -> group
                .task("backup", 5)
                .task("migrate", 8))
        .task("deploy", 3)
        .build();
```

- `PlanBuilder` jest **jednorazowy**: `ensureOpen()` rzuca `IllegalStateException` po `build()`.
- `build()` tworzy `DeploymentGroup`, którego konstruktor kopiuje listę, więc późniejsza mutacja bufora Buildera nie zmieni gotowego planu.
- Lambda definiująca grupę ma kontrakt synchroniczny - zachowanie jej na później, w innym wątku lub ponowne wywołanie byłoby zmianą zachowania.

---

## Builder - warunki bezpieczeństwa

- Przed wprowadzeniem Buildera zdecyduj: czy pusty Composite jest poprawny, czy nazwy dzieci muszą być unikalne i czy kolejność dzieci jest obserwowalna.
- Ustal, czy węzeł może mieć kilku rodziców, czy struktura to drzewo, czy graf skierowany, i jak wykrywane są cykle.
- Określ, czy Builder można użyć ponownie i czy częściowy wynik może go opuścić.
- W przykładzie Builder buduje dzieci od dołu z typów niemutowalnych, więc cykl nie powstanie.
- Publiczny konstruktor `DeploymentGroup` pozwala jednak współdzielić podplan (DAG) - akumulacja policzy go dla każdej ścieżki; ścisłe drzewo wymaga jawnej własności rodzica.

---

## Encapsulate Classes with Factory i Extract Factory Class

- **Encapsulate Classes with Factory** ukrywa klasy konkretne wspólnej rodziny - klient dostaje obiekt przez fabrykę i zależy tylko od interfejsu.
- **Extract Factory Class** wydziela tworzenie z klasy, która miesza konstrukcję z inną odpowiedzialnością.
- Fabryka nie powinna przejmować cyklu życia, logiki biznesowej ani używania produktu, jeśli nie należą do kontraktu tworzenia.
- Przed zmianą klienci znają `HttpProbe`/`QueueProbe` i ich konstruktory, a `LegacyProbeService` sam wybiera i konstruuje sondę, choć ma ją tylko uruchomić.

---

## Fabryka jako publiczna granica

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

- Implementacje są prywatne, więc klient polega wyłącznie na `DeploymentProbe`; `switch` zostaje, bo to właściwa granica wiedzy o konstrukcji - usuwanie każdego warunku nie jest celem.

---

## Factory - procedura i pułapki

- Zinwentaryzuj konstruktory, refleksję, DI i deserializatory; wprowadź interfejs produktu i fabrykę używającą początkowo tych samych konstruktorów.
- Przekierowuj miejsca tworzenia pojedynczo, a widoczność klas ogranicz dopiero po migracji klientów; dodaj testy kontraktowe produktów i mapowania wejścia na typ.
- Tworzenie wszystkich wariantów z wyprzedzeniem może uruchomić kosztowne konstruktory i efekty wcześniej niż stara gałąź.
- Fabryka nie powinna zamieniać jawnej zależności w globalny rejestr usług.
- Ukrycie publicznego konstruktora łamie klientów zewnętrznych - w bibliotece potrzebny jest etap przejściowy z `@Deprecated` i usunięciem w wersji głównej.

---

## Move Embellishment to Decorator

- Embellishment to opcjonalne zachowanie wokół rdzenia, np. audyt, pomiar czasu, retry albo cache.
- Decorator implementuje ten sam kontrakt co rdzeń i przechowuje delegata.
- Kolejność dekoratorów jest częścią zachowania: `audit(retry(core))` zapisze jedną operację obejmującą wszystkie próby, a `retry(audit(core))` - każdą próbę osobno.

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

---

## Decorator - wyjątki i sekwencja migracji

- Kod zachowuje dokładną instancję wyjątku delegata, ale wyjątek z samego audytu może zasłonić błąd rdzenia.
- Polityka produkcyjna musi zdecydować: przerwać operację, dołączyć błąd jako suppressed czy skierować go do kanału awaryjnego.
- Migracja: wąski interfejs komponentu, zapis kolejności rdzenia i dodatku dla sukcesu i awarii, dekorator przekazujący bez zmian.
- Następnie przenoś po jednym dodatku z zachowaniem pozycji, testuj propagację wyniku i instancji wyjątku oraz istotne kolejności składania.
- Składanie łańcucha przenieś do kontrolowanej fabryki, a dawną flagę usuń po migracji wszystkich punktów tworzenia.

---

## Decorator - granice przezroczystości

- Decorator **nie jest przezroczysty** dla `getClass` i refleksji, tożsamości referencyjnej oraz automatycznej serializacji.
- Nie przenosi też monitora `synchronized`, metod fluent zwracających `this`, samowywołań wewnątrz delegata ani później dodanych metod `default`.
- `synchronized` w dekoratorze blokuje inny monitor niż w delegacie, więc mechaniczne przeniesienie synchronizacji może naruszyć wykluczanie.
- Ślepe delegowanie `equals` może złamać symetrię między rdzeniem a obiektem opakowującym.

---

## Replace State-Altering Conditionals with State - tabela przejść

| Stan | approve | deploy | cancel |
| --- | --- | --- | --- |
| DRAFT | APPROVED | wyjątek | CANCELLED |
| APPROVED | wyjątek | DEPLOYED | CANCELLED |
| DEPLOYED | wyjątek | wyjątek | wyjątek |
| CANCELLED | wyjątek | wyjątek | wyjątek |

- State stosujemy, gdy warunki opisują zachowanie i przejścia bieżącego stanu - tabelę zapisujemy **przed** tworzeniem hierarchii.
- Pusta implementacja domyślna nie może zastąpić dotychczasowego wyjątku, a stan po niedozwolonej operacji musi pozostać niezmieniony.

---

## Kontekst delegujący do State

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

- Stany to bezstanowe stałe enumu, a dane konkretnego wydania zostają w `Release` - dane w polu enumu byłyby współdzielone przez wszystkie konteksty.

---

## State - kolejność przejścia i efektów

- Przy efektach zewnętrznych trzeba wybrać kontrakt: efekt, potem zmiana stanu; zmiana stanu, potem efekt; atomowy zapis przez outbox; albo rozdzielenie stanu procesu i próby efektu.
- Żaden wariant nie jest uniwersalnie poprawny - refaktoryzacja zachowuje dotychczasowy moment zmiany, dopóki zespół nie zatwierdzi innego modelu.
- `volatile` daje widoczność referencji, ale nie czyni sekwencji sprawdzenie-efekt-zapis atomową.
- Funkcja w `AtomicReference.updateAndGet` może wykonać się wielokrotnie przy sporze, więc nie umieszczaj w niej nieidempotentnych efektów.
- Mały `switch` po dwóch stanach bywa czytelniejszy; State zyskuje wartość przy rosnącej tabeli przejść, a kilka niezależnych osi stanu grozi eksplozją klas.

---

## Replace Hard-coded Notifications with Observer

- Observer usuwa zależność od konkretnych odbiorców: subject zna minimalny interfejs listenera, a skład aplikacji rejestruje odbiorców.
- Pierwszy bezpieczny krok zachowuje relację jeden do jednego przez nowy interfejs; kolekcja obserwatorów to późniejszy, osobny krok.
- Powiadamianie dowolnej liczby odbiorców może być **rozszerzeniem** zachowania, a nie czystą refaktoryzacją.
- Kontrakt publikacji musi określać: synchroniczność i wątek listenera, kolejność, duplikaty, reakcję na wyjątek, usuwanie w trakcie callbacku, koniec subskrypcji, siłę referencji i widoczność stanu.
- Przykład: publikacja synchroniczna na wątku wywołującym, kolejność rejestracji, duplikaty dozwolone, polityka fail-fast.

---

## Observer - implementacja

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

- `Subscription` rozszerza `AutoCloseable`; każde `subscribe` tworzy osobny `Registration` z tożsamościowym `equals`, więc uchwyt usuwa dokładnie własną rejestrację.

---

## Observer - semantyka i migracja

- `CopyOnWriteArrayList` kopiuje tablicę przy każdej modyfikacji - sensowna, gdy publikacji jest dużo więcej niż rejestracji, a listenerów umiarkowanie.
- Iterator pracuje na migawce: listener usunięty podczas publikacji może zostać wywołany jeszcze w tej publikacji, ale nie w następnej.
- `AtomicBoolean` czyni zamknięcie uchwytu idempotentnym; subject trzyma silną referencję do `close`, więc brak zamknięcia wydłuża życie grafu obiektów.
- Migracja: oddziel pracę odbiorcy, wyodrębnij interfejs, zarejestruj dotychczasowego odbiorcę raz i przenieś wywołanie bez kolekcji; wielu odbiorców dodaj osobno.
- Globalna szyna zdarzeń może ukryć zależności; dla jednego stabilnego odbiorcy jawna zależność bywa prostsza.

---

## Replace Implicit Tree with Composite - ukryte drzewo

- Drzewo bywa zakodowane w ścieżkach tekstowych, wcięciach, identyfikatorach rodzica, indeksach albo równoległych kolekcjach.
- Operacje drzewa wykonuje się wtedy przez parsowanie reprezentacji prymitywnej.
- `LegacyPathPlan` przechowuje zadania jako ścieżki typu `release/database/backup` i wyszukuje je po prefiksie `path + "/"`.
- Hierarchia istnieje tylko w konwencji tekstu, więc separator, reguły maskowania, pusta ścieżka i wyszukiwanie prefiksu stają się częścią algorytmu każdego klienta.

---

## Jawny model Composite

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

- Liść `DeploymentTask` realizuje ten sam kontrakt (zwraca `minutes`, dodaje siebie do kolekcji), a grupa wykonuje operacje rekurencyjnie i kopiuje listę dzieci.

---

## Mapper migracyjny `LegacyPathPlanMapper`

- Ręczne zbudowanie podobnego drzewa nie sprawdza transformacji starego formatu - potrzebny jest mapper starych wpisów na Composite.
- Jawne `rootName` pozwala odwzorować także pusty plan; wpisy spoza korzenia są odrzucane.
- Mapper zachowuje powtórzone zadania, ale odrzuca ścieżkę używaną jednocześnie jako zadanie i grupa.
- Kolejności płaskiej listy nie zawsze da się zachować: `release/a/first`, `release/b/second`, `release/a/third` wymagałoby ponownego wejścia do zamkniętej grupy `a`.
- Dlatego mapper wymaga kolejności zgodnej z przejściem w głąb i zgłasza błąd zamiast po cichu zmienić zachowanie; alternatywy to sortowanie, pole kolejności lub zmiana kontraktu.

---

## Implicit Tree - procedura i ryzyka

- Scharakteryzuj reguły starej reprezentacji, wprowadź liść i Composite bez usuwania tekstu, napisz mapper i przenieś jedną operację, np. sumowanie.
- Porównuj wynik i kolejność obu reprezentacji, przenoś kolejne operacje pojedynczo, a granicę trwałości zmień dopiero po przygotowaniu migracji danych.
- Określ zachowanie dla głębokich drzew, cykli, współdzielonych poddrzew i przepełnienia - rekurencja grozi `StackOverflowError`, a cykl nieskończoną wędrówką.
- Dla danych niezaufanych potrzebny jest limit głębokości, wykrywanie cykli albo iteracyjne przechodzenie.
- `Math.addExact` czyni przepełnienie jawnym błędem; `List.copyOf` chroni strukturę listy, ale nie elementy.

---

## Unify Interfaces with Adapter

- Adapter pozwala klientowi używać preferowanego kontraktu mimo innego interfejsu biblioteki lub komponentu legacy.
- Tłumaczy nazwy, parametry, wyniki i błędy, ale nie powinien udawać zgodności semantycznej, której nie ma.

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

- `NotificationService` zależy wyłącznie od interfejsu docelowego `ReleaseNotifier`.

---

## Adapter - co naprawdę trzeba przetłumaczyć

- Podobne sygnatury nie gwarantują podobnego kontraktu - adapter jawnie rozstrzyga jednostki, zakresy, strefę czasową, precyzję i kodowanie znaków.
- Musi określić znaczenie `null` i pustej wartości, kolejność i indeksowanie oraz mutowalność wejścia i wyniku.
- Ważne są też własność i zamykanie zasobów, idempotencja, mapowanie wyjątków z zachowaniem przyczyny oraz synchroniczność i kontekst wątku.
- Adapter `AutoCloseable` musi wiedzieć, czy jest właścicielem obiektu - nie zawsze powinien zamykać współdzielone połączenie.
- Migracja: interfejs z potrzeb klienta, najprostszy adapter, mapowanie operacji pojedynczo z testem porównawczym; jeśli wystarczy Rename Method lub Move Method, adapter może być zbędny.

---

## Replace Conditional Dispatcher with Command

- Dispatcher odczytuje selektor i uruchamia akcję; Command zamienia każdą akcję w obiekt o wspólnej operacji, a rejestr mapuje selektor na komendę.
- Mapa jest równoważna tylko przy **rozłącznym kluczu** - łańcucha nakładających się predykatów („wygrywa pierwszy”) nie da się mechanicznie zastąpić mapą.

```java
public DeploymentCommandDispatcher(Map<DeploymentAction, DeploymentCommand> commands) {
    Map<DeploymentAction, DeploymentCommand> copy = Map.copyOf(commands);
    EnumSet<DeploymentAction> missing = EnumSet.allOf(DeploymentAction.class);
    missing.removeAll(copy.keySet());
    if (!missing.isEmpty()) throw new IllegalArgumentException("missing commands: " + missing);
    this.commands = copy;
}
```

- `Map.copyOf` odłącza rejestr od mapy wejściowej, a sprawdzenie kompletności przenosi błąd konfiguracji na moment składania aplikacji.

---

## Command - sekwencja i ograniczenia

- Scharakteryzuj klucz, priorytet gałęzi, `default` i `null`; wydziel ciało gałęzi do metod, pozostawiając `switch`, i przenieś je do komend.
- Ustal najmniejszą wspólną sygnaturę `execute`, pozwól staremu dispatcherowi uruchamiać gotowe komendy, a potem zbuduj rejestr z polityką duplikatów i kompletności.
- Command **nie daje automatycznie** asynchroniczności, kolejki, retry ani undo - `Executor` zmienia wątek, wyjątki, transakcję i kontekst diagnostyczny.
- Retry może powielić nieidempotentne efekty, a undo wymaga osobnego modelu kompensacji.
- Współdzielone komendy muszą być bezstanowe lub bezpieczne wątkowo; dane żądania powinny być argumentem.

---

## Apply Template Method

- Klasyczna nazwa to Form Template Method: podklasy wykonują podobne kroki w tej samej kolejności, różniąc się implementacją wybranych kroków.
- W przykładzie importery: walidują wejście, parsują format, walidują pola i tworzą `ReleaseDraft` - zmienne jest tylko parsowanie.

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

- `final` chroni kolejność, ale dodanie go do opublikowanej, nadpisywanej metody łamie klientów; chroniona fabryka `fields` pozwala podklasom spoza pakietu tworzyć `Fields`.

---

## Template Method - sekwencja i ryzyka

- Zapisz kolejność kroków w każdej klasie, użyj Extract Method, ujednolicaj nazwy i sygnatury po jednym kroku i przenieś identyczne kroki do bazy.
- Szkielet przenieś przez Pull Up Method, wymagane kroki oznacz jako abstrakcyjne, a opcjonalny hook dodawaj tylko przy poprawnym domyślnym zachowaniu dla wszystkich.
- Nowa metoda abstrakcyjna wymusza zmianę podklas konkretnych, a stara implementacja binarna ujawni brak dopiero podczas wywołania.
- Hook wywołany z konstruktora bazy zobaczy domyślne wartości pól podklasy; `synchronized` w szablonie trzyma monitor podczas hooków, co grozi reentrancją i zakleszczeniem.
- Dużo hooków lub potrzeba zmiany ich kolejności sygnalizuje, że kompozycja lub Strategy będą elastyczniejsze.

---

## Replace Distinctions with Composite

- Klient często ma dwie ścieżki: `execute` dla jednego elementu i `executeAll` dla kolekcji, z powielonymi warunkami i regułami błędów.
- Replace One/Many Distinctions with Composite pozwala przekazać liść albo grupę przez jeden kontrakt.

```java
public List<String> execute(PlanComponent component) {
    Objects.requireNonNull(component, "component");
    List<DeploymentTask> tasks = new ArrayList<>();
    component.collectTasks(tasks);
    return tasks.stream().map(task -> "executed:" + task.name()).toList();
}
```

- Ujednolicenie nie może zmienić kolejności, zachowania pustej grupy, polityki przerwania po błędzie, raportowania częściowego sukcesu, liczby transakcji, duplikatów, równoległości ani granicy retry.
- W przykładzie kolejność to preorder zgodny z listami dzieci, a pierwszy wyjątek przerywa wykonanie.

---

## Transparent vs Safe Composite

- **Transparent Composite** umieszcza zarządzanie dziećmi we wspólnym kontrakcie - wywołanie `add` na liściu wymaga wtedy wyjątku albo pustego zachowania.
- **Safe Composite** udostępnia zarządzanie dziećmi tylko typowi grupy.
- Przykład jest bliższy wariantowi Safe: wspólny interfejs opisuje operacje biznesowe, a `components()` należy do `DeploymentGroup`.
- Klient czasem musi znać rodzaj węzła, ale niemożliwa operacja nie zanieczyszcza kontraktu liścia.

---

## Limit Instantiation with Singleton

- Singleton to **decyzja o cyklu życia**: uzasadniony, gdy wiele równoważnych, kosztownych instancji nie daje wartości albo domena wymaga jednej tożsamości.
- Nie wprowadzaj go na podstawie przypuszczeń o pamięci - zmierz koszt i sprawdź, czy współdzielenie zachowuje semantykę stanu, blokad i cyklu życia.
- Katalog Kerievsky'ego zawiera transformację odwrotną - Inline Singleton, przywracającą jawne przekazywanie zależności.

```java
public enum DeploymentDefaults {
    INSTANCE;
    private final Duration healthCheckTimeout = Duration.ofSeconds(30);
    public Duration healthCheckTimeout() { return healthCheckTimeout; }
}
```

- Enum daje jedną instancję w obrębie loadera klas, bezpieczną publikację, kanoniczną deserializację i zakaz tworzenia refleksją - ale dwa loadery mogą mieć dwie definicje.

---

## Singleton - mutowalność i sekwencja

- Globalny mutowalny Singleton tworzy ukryte zależności, łączy testy przez pozostawiony stan i wymaga synchronizacji.
- Utrudnia też konfigurację wielu tenantów i zamykanie zasobów, wydłuża życie referencji i może stać się wąskim gardłem.
- Nawet dla niemutowalnego `Duration` jawne wstrzyknięcie ustawień jest zwykle lepsze; kontener DI ograniczy liczbę instancji w zakresie, zachowując podmianę w testach.
- Sekwencja: zmierz koszt, udowodnij równoważność instancji, scharakteryzuj inicjalizację i zamykanie, przekieruj tworzenie przez jednego dostawcę, przetestuj współbieżność - konstruktor ogranicz na końcu.
- Usunięcie publicznego konstruktora zmienia API, a odrębne cache, liczniki lub blokady per instancja wykluczają refaktoryzację bez zmiany zachowania.

---

## Collecting Parameter i Visitor - dwie techniki

- Gdy długa metoda rozpoznaje wiele elementów i mutuje lokalny wynik, rozważ **Collecting Parameter** - elementy same dopisują wynik do przekazanego akumulatora.
- **Visitor** pozwala dodawać wiele operacji do stabilnej hierarchii bez umieszczania ich w elementach, kosztem podwójnej dyspozycji i silnego powiązania z typami.
- Kontrakt `collectTasks(Collection<? super DeploymentTask>)`: liść dodaje siebie, grupa przekazuje akumulator dzieciom, a wywołujący zachowuje własność kolekcji.
- Trzeba zdefiniować, czy metoda czyści kolekcję, czy dopisuje, i co zostaje po wyjątku - w przykładzie może zostać wynik częściowy.
- Ogólna mutowalna mapa lub kontekst ukrywa zbyt wiele; dobry parametr zbierający ma wąski typ i jasną własność.

---

## Visitor i macierz zmian (1/2)

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

---

## Visitor i macierz zmian (2/2)

| Częsta zmiana | Naturalniejszy kierunek |
| --- | --- |
| nowe operacje, stabilne rodzaje węzłów | Visitor |
| nowe rodzaje węzłów, stabilne operacje | metody polimorficzne |
| jedna prosta akumulacja | Collecting Parameter |
| zbiór typów otwarty dla wtyczek | klasyczny Visitor staje się kosztowny |

- `accept` wybiera właściwe `visit` bez `instanceof`; `Long` oznacza koszt opakowania w gorącej ścieżce.

---

## Extract Composite

- To nie ogólne „zrób drzewo”: punktem wyjścia jest hierarchia, w której kilka klas powiela przechowywanie i przetwarzanie dzieci - wspólną część wydziela się do klasy Composite.
- W przykładzie `ReleaseGroup` i `RollbackGroup` osobno trzymają listę dzieci, kopiują ją przy odczycie i sumują czas.

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

- Prywatne pole i metody `final` chronią wspólną reprezentację, ale nie zabraniają podklasie drugiego pola z dziećmi - to trzeba wykryć w przeglądzie i testach.

---

## Extract Composite - sekwencja

- Znajdź wszystkie klasy przechowujące dzieci z tej samej hierarchii i porównaj rzeczywiste kontrakty, nie tylko podobieństwo kodu.
- Ujednolij nazwy i typy kolekcji bez przenoszenia, wyodrębnij bazę implementującą kontrakt węzła i przenoś pole dzieci oraz operacje pojedynczo.
- Duplikaty usuń dopiero po sprawdzeniu kolejności, mutowalności i wyjątków.
- Podobne pętle mogą znaczyć co innego: pomijanie nieaktywnych dzieci, kontynuacja po błędzie, wykonanie równoległe - wspólna implementacja jest poprawna tylko, gdy te różnice nie należą do kontraktu.
- Mutowalny model nadal pozwala utworzyć cykl; jego wykrywanie to osobna zmiana zachowania po refaktoryzacji.

---

## Warsztat - zasada pracy

1. Uruchom testy bazowe i nazwij zachowanie chronione w bieżącym kroku.
2. Wykonaj jedną transformację.
3. Uruchom test celowany, a potem test różnicowy.
4. Zapisz decyzję projektową.
5. Przejdź dalej dopiero po zielonym wyniku.

- Nie zaczynamy od tworzenia wszystkich klas wzorca - pierwszy commit ma być mały i odwracalny.

---

## Ćwiczenie 1: wybór właściwego mechanizmu

Serwis zawiera cztery warunki: wariant kalkulacji kosztu, rodzaj kroku wdrożenia, bieżący stan wydania i akcję operatora.

- Przypisz każdą oś do Strategy, polimorfizmu, State albo Command i uzasadnij wybór jednym zdaniem.
- Dodaj test charakterystyki dla gałęzi domyślnej i `null`, wprowadzaj jeden kontrakt naraz i nie usuwaj warunku przed migracją wszystkich punktów tworzenia.
- **Kryteria akceptacji:** brak kombinatorycznej hierarchii podtypów, stan przejściowy nie jest strategią, komendy nie przechowują danych poprzedniego żądania.
- Fabryka lub korzeń kompozycji jest jedynym miejscem mapowania konfiguracji na Strategy, a testy porównują wyniki i wyjątki przed i po.

---

## Ćwiczenie 2: State, Observer i Decorator

Rozbuduj przykład wydania o zapis audytu i powiadomienie odbiorców po udanym wdrożeniu.

- Ustal, czy audyt jest częścią przejścia State, dekoracją operacji, czy osobnym zdarzeniem, i zapisz kolejność stanu, audytu i publikacji.
- Dodaj listener usuwający inną subskrypcję podczas callbacku oraz listener rzucający wyjątek; udokumentuj politykę fail-fast albo kontynuacji.
- Przetestuj stan po awarii audytu i listenera.
- **Kryteria akceptacji:** brak wywołań listenerów pod nieudokumentowaną blokadą, idempotentne zamykanie subskrypcji, test ustala los usuniętego listenera w bieżącej publikacji.
- Nie ma niejawnego przejścia na asynchroniczność, a błąd dodatku nie zasłania pierwotnej przyczyny bez jawnej decyzji.

---

## Ćwiczenie 3: rodzina refaktoryzacji Composite

Punkt wyjścia: lista zadań ze ścieżkami tekstowymi oraz osobne metody dla jednego zadania i listy.

- Scharakteryzuj separator, kolejność i niepoprawne ścieżki; wprowadź `PlanComponent`, liść i grupę oraz mapper starego formatu.
- Zastąp rozróżnienie jeden/wiele jednym kontraktem, enkapsuluj budowę w Builderze, przenieś zbieranie liści do parametru zbierającego i dodaj Visitor liczący czas.
- Przetestuj puste drzewo, głęboką strukturę, przepełnienie i współdzielone poddrzewo.
- **Kryteria akceptacji:** wynik Buildera niemutowalny, jawna kolejność przechodzenia, przepełnienie nie zawija wartości.
- Każdy Visitor obsługuje wszystkie rodzaje węzłów, a zmiana formatu trwałego jest oddzielona od refaktoryzacji modelu.

---

## Mapa testów w projekcie (1/2)

| Test | Chroniony kontrakt |
| --- | --- |
| `StrategyEquivalenceTest` | warianty obliczenia i wspólna walidacja |
| `TypeCodeEquivalenceTest` | kody, normalizacja, kanonizacja, błędy |
| `DecoratorEquivalenceTest` | wynik, kolejność audytu, tożsamość wyjątku |
| `StateEquivalenceTest` | dozwolone i niedozwolone przejścia |

---

## Mapa testów w projekcie (2/2)

| Test | Chroniony kontrakt |
| --- | --- |
| `ObserverContractTest` | kolejność, migawka, usuwanie, fail-fast |
| `CompositeRefactoringsTest` | drzewo, Builder, jeden/wiele, akumulacja |
| `CommandEquivalenceTest` | wszystkie klucze, niezmienność rejestru |
| `SingletonContractTest` | kanoniczna instancja, niemutowalne ustawienie |

- Projekt zawiera też testy dla polimorfizmu, Factory, Adaptera, Template Method, Extract Composite oraz `Module6ExamplesTest` dla całego modułu.

---

## Test różnicowy Composite

```java
var legacy = new LegacyPathPlan(entries);
DeploymentGroup plan = new LegacyPathPlanMapper().map("release", entries);
List<DeploymentTask> tasks = new ArrayList<>();
plan.collectTasks(tasks);

assertEquals(16, legacy.totalMinutes());
assertEquals(16, plan.totalMinutes());
assertEquals(List.of("backup", "migrate", "deploy"),
        tasks.stream().map(DeploymentTask::name).toList());
assertEquals(legacy.taskNamesBelow("release"),
        tasks.stream().map(DeploymentTask::name).toList());
```

- Test wywołuje rzeczywisty mapper, porównuje zapytania dla korzenia i podgrupy oraz sprawdza topologię.
- Samo porównanie przed/po może utrwalić błąd obu wersji, dlatego test ma też **niezależne oczekiwania**: 16 minut oraz konkretne nazwy i kolejność.

---

## Mapa decyzji (1/3)

| Sytuacja | Rozważ | Uważaj na |
| --- | --- | --- |
| wymienny algorytm | Strategy | moment wyboru, stan strategii |
| trwały rodzaj obiektu | polimorfizm | fabryki, serializacja, podtypy |
| surowy kod domenowy | obiekt typu | format trwały, równość, nieznane kody |
| skomplikowane tworzenie drzewa | Builder | częściowy wynik, cykle, ponowne użycie |
| wiedza o tworzeniu rozproszona | Factory | globalny rejestr, zbyt szeroka odpowiedzialność |
| opcjonalne zachowanie wokół rdzenia | Decorator | kolejność, wyjątki, tożsamość |
| zachowanie zależne od stanu | State | tabela przejść, atomowość, efekty |
| zmienni odbiorcy zdarzenia | Observer | kolejność, błędy, wątki, cykl życia |
| ukryta struktura drzewa | Composite | cykle, głębokość, format migracji |

---

## Mapa decyzji (2/3)

| Sytuacja | Rozważ | Uważaj na |
| --- | --- | --- |
| niezgodne API istniejącej klasy | Adapter | semantyka, jednostki, własność zasobów |
| dispatcher według rozłącznego klucza | Command | kompletność kluczy, duplikaty, stan komendy |
| stała sekwencja zmiennych kroków | Template Method | kruche hooki, ewolucja podklas |
| osobny kod dla jednego i wielu | Composite | kolejność, częściowe błędy |

---

## Mapa decyzji (3/3)

| Sytuacja | Rozważ | Uważaj na |
| --- | --- | --- |
| rzeczywiście jedna wspólna instancja | Singleton | globalny stan, class loadery, testowalność |
| prosta rekurencyjna agregacja | Collecting Parameter | wynik częściowy, mutowalność |
| wiele operacji na stabilnych typach | Visitor | koszt nowego rodzaju węzła |
| duplikacja obsługi dzieci | Extract Composite | różnice ukryte w podobnych pętlach |

---

## Lista kontrolna (1/2): kontrakt, tworzenie i cykl życia

- **Kontrakt:** czy nazwano zachowanie niezmienione i czy testy obejmują każdą gałąź, `null` i wartość nieznaną?
- Czy zachowano typy wyjątków, stan po błędzie, kolejność i liczbę efektów oraz moment wyboru strategii lub komendy?
- **Tworzenie:** czy znaleziono wszystkie punkty tworzenia, a fabryka nie stała się globalnym rejestrem usług?
- Czy współdzielone strategie, komendy i State są bezstanowe lub bezpieczne wątkowo?
- Czy Singleton jest jeden w wymaganym zakresie (nie tylko w jednym loaderze), a zasoby mają jawnego właściciela i moment zamknięcia?

---

## Lista kontrolna (2/2): struktury i integracje

- **Struktury:** czy gotowy Composite jest niemutowalny zgodnie z kontraktem, a kolejność dzieci jawna?
- Czy przy danych niezaufanych wykrywane są cykle i nadmierna głębokość; czy akumulacja ma politykę przepełnienia i błędu częściowego; czy zachowanie Buildera po `build()` jest zdefiniowane?
- **Integracje:** czy format trwałego kodu pozostał stabilny, a adapter zachowuje jednostki, strefy czasu, kodowanie i idempotencję?
- Czy obiekt opakowujący nie zmienił wymaganej tożsamości, monitora ani serializacji?
- Czy nowy podtyp `sealed` sprawdzono we wszystkich `switch`, a klienci binarni i refleksyjni są uwzględnieni w zgodności?

---

## Pytania sprawdzające (1/3)

1. Dlaczego usunięcie `switch` nie wystarcza do uzasadnienia Strategy? - Warunek może oznaczać stan, typ, komendę albo zwykłą lokalną regułę.
2. Kiedy kod typu staje się klasą wartości, a kiedy podtypem? - Klasa wartości dla pojęcia i jego reguł, podtyp dla stabilnego rodzaju z własnym zachowaniem.
3. Dlaczego wybór strategii w konstruktorze może zmienić zachowanie? - Zamraża decyzję wcześniej niż warunek odczytywany przy każdym wywołaniu.
4. Co odróżnia State od Strategy? - Strategy wykonuje wybrany algorytm, State reprezentuje bieżący stan i dozwolone przejścia.
5. Dlaczego kolejność Decoratorów jest kontraktem? - Warstwy zmieniają zasięg retry, audytu, transakcji i obsługi wyjątków.
6. Jak działa iterator `CopyOnWriteArrayList` po usunięciu listenera? - Używa migawki, więc usunięcie działa od następnej iteracji.
7. Kiedy kolekcja Observerów jest rozszerzeniem? - Gdy stary kontrakt gwarantował dokładnie jeden efekt.

---

## Pytania sprawdzające (2/3)

8. Co tłumaczy Adapter poza nazwami? - Semantykę danych, jednostki, `null`, błędy, własność zasobów, idempotencję i model wykonania.
9. Kiedy mapa komend nie jest równoważna warunkom? - Gdy predykaty się nakładają, liczy się kolejność lub wybór nie jest pojedynczym kluczem.
10. Dlaczego rekord z `List` nie jest głęboko niemutowalny? - Referencja jest finalna, ale lista może być mutowalna; potrzebna kopia.
11. Jaki wymiar zmian preferuje Visitor? - Częste nowe operacje przy stabilnym zestawie rodzajów elementów.

---

## Pytania sprawdzające (3/3)

12. Extract Composite vs Replace Implicit Tree? - Pierwsza wydziela powieloną obsługę dzieci, druga zastępuje ukrytą reprezentację jawnym drzewem.
13. Dlaczego enum Singleton to nie zawsze jedna instancja? - Każdy loader klas może mieć własną definicję.
14. Częściowy błąd akumulacji? - Ustal, czy wynik zostaje, jest wycofywany, czy błędy są agregowane.
15. Kiedy prosty `switch` jest lepszy? - Gdy warunek jest mały, lokalny, stabilny i czytelniejszy niż dodatkowe typy.

---

## Podsumowanie - najważniejsze wnioski

- Refaktoryzacja do wzorca zaczyna się od **kontraktu**, a nie od diagramu klas - najpierw nazwij zachowanie, które ma pozostać niezmienione.
- Strategy, polimorfizm, State i Command usuwają podobnie wyglądające warunki, ale modelują różne przyczyny zmiany.
- Factory i Builder kontrolują tworzenie; Decorator, Observer i Adapter - współpracę; Composite i Visitor - struktury i operacje na nich.
- Najbezpieczniejsza transformacja wprowadza jeden kontrakt, przenosi jedną odpowiedzialność i po każdym kroku porównuje zachowanie.
- Rozszerzenia zachowania (asynchroniczność, wielu odbiorców, cache) zatwierdzaj osobno od zmian strukturalnych.
- Wzorzec zostaje tylko, gdy upraszcza realny problem - nadmiar pośrednictwa usuń w ostatnim kroku.
