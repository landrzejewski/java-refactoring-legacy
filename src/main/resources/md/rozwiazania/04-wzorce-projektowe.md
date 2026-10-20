# Moduł 4. Wzorce projektowe - rozwiązania dla prowadzącego

Numeracja i nazwy zadań są zgodne z plikiem zadań (`src/main/resources/md/zadania/04-wzorce-projektowe.md`). Ścieżki Javy są względne względem katalogu głównego repozytorium, pakiety w tabelach - względem `pl.training.module4`.

Odpowiedzi opierają się na kodzie modułu (`src/main/java/pl/training/module4`) oraz na treści katalogu wzorców ze slajdów (przeznaczenie, zastosowanie, konsekwencje). Kod referencyjny ćwiczeń 2 i 3 został skompilowany i uruchomiony na kopii repozytorium (Java 25, JUnit 6): wszystkie testy referencyjne przechodzą. Kod referencyjny **nie jest** częścią repozytorium - uczestnicy piszą go sami.

W ćwiczeniach analitycznych (1 i 4) nie ma jednej poprawnej odpowiedzi. Oceniaj jakość uzasadnienia: czy zaczyna się od rodzaju zmienności, czy nazywa koszt i czy rozważa prostszą alternatywę.

---

## Krótkie aktywności - odpowiedzi

### Aktywność 1.7: Nazwij problem, nie wzorzec

- **Opis problemu bez nazwy wzorca:** jedna metoda zawiera kilka niezależnych algorytmów formatowania wybieranych napisem, więc każdy nowy format wymaga edycji tej samej metody, a błąd w nazwie formatu wychodzi dopiero w czasie działania.
- **Co się zmienia:** algorytm formatowania. **Kto wybiera:** użytkownik, przy każdym eksporcie. **Jak często:** zestaw jest otwarty (nowe formaty pojawiają się regularnie).
- **Decyzja:** Strategy (`ReportExporter`) z rejestrem `Map<Format, ReportExporter>` i typem `Format` (enum) zamiast `String`. Gdyby formaty były dwa, stabilne i krótkie, `switch` po enumie byłby wystarczający - sama obecność `if` nie uzasadnia wzorca.

### Aktywność 2.8: Konstruktor, fabryka czy builder?

| Przypadek | Mechanizm | Uzasadnienie |
| --- | --- | --- |
| `Money` | `record Money(BigDecimal amount, Currency currency)` z walidacją w konstruktorze kompaktowym; opcjonalnie fabryka `Money.pln(...)` | dwa wymagane pola i jedna reguła - builder byłby nadmiarem |
| `HttpClientConfig` | Builder z walidacją kombinacji w `build()` | wiele opcjonalnych parametrów i niezmiennik całości (`proxyHost` wymaga `proxyPort`) |
| `Notifier` | fabryka (Factory Method lub wybór w composition root) zwracająca interfejs `Notifier` | klient ma znać tylko abstrakcję; wybór implementacji zależy od konfiguracji |

### Aktywność 3.12: Rozpoznaj opakowanie

- **A - Proxy (ochronny):** kontroluje dostęp i może **nie delegować**.
- **B - Decorator:** zawsze deleguje i dokłada odpowiedzialność (pomiar czasu).
- **C - Adapter:** tłumaczy wywołanie na inny interfejs (`gateway.charge(...)`), nie deleguje do obiektu tego samego typu.
- Rozstrzyga **intencja**: A i B mają identyczną strukturę.
- Jeśli B zacznie odrzucać płatności w oknie serwisowym, zmienia intencję na kontrolę dostępu - staje się proxy ochronnym, a dla klienta pojawia się nowy możliwy wynik (wyjątek), czyli zmiana kontraktu.
- `PaymentLoggerProxy` z repozytorium to strukturalnie i intencjonalnie **Decorator** (zawsze deleguje, dokłada log po sukcesie) - mimo nazwy.

### Aktywność 4.18: Strategy czy State?

| Przypadek | Decyzja | Kto i kiedy zmienia wariant | Tabela przejść |
| --- | --- | --- | --- |
| 1. zaokrąglanie ceny | Strategy, a często po prostu parametr (`RoundingMode`) | konfiguracja, przy starcie | brak |
| 2. bilet | State | sam obiekt, w wyniku operacji (opłać, wykorzystaj, zwróć) | tak |
| 3. kompresja | Strategy (lub zwykły parametr wywołania) | użytkownik, przy każdym eksporcie | brak |
| 4. abonament | State; jeśli stany różnią się tylko limitem - `enum` z polem `limit` | sam obiekt (płatność, zaległość) | tak |

Pytanie kontrolne, które rozstrzyga: „Czy wariant zmienia się w wyniku operacji na samym obiekcie?” Jeśli tak - State.

---

## Ćwiczenie 1: Rozpoznawanie wzorców w kodzie

> Rozwiązanie opracowane na podstawie kodu modułu i katalogu wzorców ze slajdów.

### Krok 1: tabela referencyjna

| Nr | Wzorzec (jeden lub kilka) | Role | Intencja | Koszt lub ryzyko w kodzie |
| --- | --- | --- | --- | --- |
| F1 | Builder (wewnętrzny, statyczny, w stylu generowanym przez Lombok) | Builder: `ServerEventBuilder`, Product: `ServerEvent`; brak Directora | tworzenie zdarzenia z nazwanymi parametrami | przy jednym polu builder jest nadmiarowy (wystarczy `record ServerEvent(String payload)`); nieużywany w demo; pole `payload` nie jest `final` |
| F2 | Flyweight (`op(...)` współdzieli operatory) + prosta fabryka (`operatorFactory`) + Strategy (`BiOperator` jako wymienne działanie w `Operation`) | FlyweightFactory: `op`; Flyweight: `Plus`, `Minus`, `Multiply`; Strategy: `BiOperator` | jeden egzemplarz operatora na symbol i wybór działania bez warunków w `Operation` | statyczna `HashMap` bez synchronizacji w klasie `Application`; nieznany operator wykryty dopiero przy budowie wyrażenia; dla bezstanowych operatorów prostszy byłby `enum` |
| F3 | Decorator (mimo nazwy Proxy) | Component: `PaymentsService`; ConcreteDecorator: `PaymentLoggerProxy` | dołożenie logowania po udanej płatności | nazwa myli; brak wpisu przy wyjątku - log dokumentuje tylko sukcesy |
| F4 | Builder; wewnątrz delegacja (przekazywanie wywołań) do `GenericConnectionUrlBuilder` - technika, nie osobny wzorzec | ConcreteBuilder: `PostgresSqlConnectionUrlBuilder` | ponowne użycie ogólnego buildera z innymi wartościami domyślnymi bez dziedziczenia | każda nowa metoda interfejsu wymaga ręcznego przekazania; `build()` zwraca mutowalny produkt; MySQL robi to samo dziedziczeniem - dwa style w jednym pakiecie |
| F5 | Strategy wyrażona danymi (`enum` z parametrami) | Context: `movies.Order`; Strategy: `MovieType` | wycena wypożyczenia zależna od rodzaju filmu bez hierarchii klas | wariant o innym **algorytmie** (nie tylko innych parametrach) wymaga ciała stałej lub powrotu do klas; `double` dla kwot pieniężnych |
| F6 | Iterator + Factory Method (`iterator()` i `reversed()` tworzą iteratory) | Aggregate: `Playlist` (`Iterable`); ConcreteIterator: `PlaylistIterator` | przejście w przód i wstecz bez ujawniania tablicy | brak wykrywania modyfikacji w trakcie przejścia (nie jest fail-fast) |
| F7 | Visitor na strukturze Composite | Element: `Node`, `Department`, `Employee`; ObjectStructure: drzewo `Node`; Visitor: `Visitor` | nowe operacje na drzewie bez zmian w węzłach | przejście (preorder) jest zaszyte w węzłach - gość nie może pominąć poddrzewa; `SuperDepartment` musi powielać `accept`; puste metody domyślne ukrywają brak obsługi typów |
| F8 | leniwa inicjalizacja przez wirtualny Proxy (`Supplier` opakowujący `Supplier`), efekt: jedna instancja na dostawcę | Proxy: anonimowy `Supplier`; RealSubject: przekazany `supplier` | utworzyć kosztowny obiekt raz, przy pierwszym użyciu | `synchronized` przy każdym `get()`; dostawca zwracający `null` będzie wołany ponownie; to nie jest Singleton w zakresie procesu |
| F9 | Decorator (`BufferedReader`) + Adapter (`InputStreamReader`: bajty → znaki) | Component: `Reader`; Adaptee: `InputStream` | buforowanie i `readLine()` nad strumieniem bajtów | kodowanie znaków to semantyka adaptera (jawny `Charset`); zamknięcie zewnętrznego obiektu zamyka wewnętrzny |
| F10 | Proxy ochronny (widok tylko do odczytu) | Subject: `List`; Proxy: lista niemodyfikowalna | zablokować modyfikację przez tę referencję | to **widok**, nie kopia - zmiany oryginału są widoczne; kopia to `List.copyOf` |
| F11 | Adapter (tablica → `List`) | Target: `List`; Adaptee: tablica | użyć tablicy tam, gdzie oczekiwana jest lista | adapter częściowy: `set` działa i zmienia tablicę, `add`/`remove` rzucają `UnsupportedOperationException` |
| F12 | Flyweight (pamięć podręczna `Integer` dla -128..127) + statyczna metoda fabrykująca | FlyweightFactory: `Integer.valueOf` | nie tworzyć wielu identycznych małych obiektów | `==` daje `true` dla 100, a zwykle `false` dla 1000 - nie wolno porównywać tożsamością |

### Krok 2-4: nazwy mylące, połączenia, techniki niebędące wzorcami GoF

- **Nazwa sugeruje inny wzorzec niż kod:** F3 (`...Proxy` realizuje Decorator), F8 (klasa `FunctionalStyleSingleton`, a mechanizm to leniwy proxy z pamięcią), F1 (builder w pakiecie `observer`), F2 (pula i fabryka ukryte w klasie `Application`).
- **Połączenia wzorców:** F2 (Flyweight + fabryka + Strategy), F6 (Iterator + Factory Method), F7 (Visitor + Composite), F8 (Proxy + Singleton), F9 (Decorator + Adapter), F12 (Flyweight + statyczna fabryka).
- **Techniki, nie wzorce GoF:** delegacja w F4 (to „kompozycja zamiast dziedziczenia”, strukturalnie podobna do dekoratora, ale bez intencji dokładania odpowiedzialności), `operatorFactory` w F2 i `Integer.valueOf` w F12 (statyczna metoda fabrykująca - idiom, nie Factory Method GoF).

### Krok 5: propozycje zmian (bez wprowadzania)

- **F3:** zmiana nazwy na `LoggingPaymentsService` (lub `PaymentsLoggingDecorator`) i jawna decyzja o logowaniu porażek (`try`/`catch` z ponownym rzuceniem tego samego wyjątku). Jeśli intencją ma być kontrola dostępu, prawdziwy proxy powinien mieć warunek, przy którym **nie deleguje**.
- **F7:** przenieść przejście po drzewie do gościa (węzeł tylko `visitor.visit(this)`, a `visit(Department)` decyduje o dzieciach) albo zastąpić metody domyślne abstrakcyjnymi, aby kompilator wymusił obsługę każdego rodzaju węzła. W Javie 25 alternatywą jest `sealed` + `switch` z dopasowaniem wzorców.

### Weryfikacja kryteriów akceptacji

- Intencja jednym zdaniem przy każdym fragmencie: kolumna „Intencja”.
- Co najmniej trzy fragmenty z więcej niż jednym wzorcem lub techniką: F2, F6, F7, F8, F9, F12.
- F3: struktura (opakowanie z tym samym interfejsem) odróżniona od intencji (dokładanie logowania, brak kontroli dostępu).
- F7: przejście po strukturze należy do węzłów (`Node.accept` iteruje dzieci), nie do gościa.
- Ryzyka konkretne: np. „widok, nie kopia” (F10), „`set` działa, `add` nie” (F11), „kolizja tożsamości `Integer`” (F12).

### Typowe błędy uczestników

- Rozpoznanie po nazwie pakietu lub klasy („to proxy, bo tak się nazywa”).
- Uznanie każdej delegacji za Decorator albo każdego `static ... of(...)`/`valueOf(...)` za Factory Method GoF.
- Traktowanie `Collections.unmodifiableList` jako kopii obronnej.
- Pominięcie Factory Method w `Iterable.iterator()` - to klasyczny przykład z katalogu.
- Wskazanie ryzyka ogólnikiem („trudne w utrzymaniu”) zamiast konkretnej konsekwencji.

### Pytania do dyskusji

- Czy nazwa klasy powinna zawierać nazwę wzorca? Kiedy pomaga, a kiedy utrwala błędną intencję (F3)?
- Czy builder dla klasy z jednym polem (F1) to błąd, czy konwencja zespołu? Kto płaci jego koszt?
- Dlaczego JDK ma tyle wzorców „w środku”, a tak rzadko w nazwach typów?

---

## Ćwiczenie 2: Nowy wariant bez modyfikacji

> Rozwiązanie opracowane na podstawie kodu referencyjnego przygotowanego dla tego ćwiczenia; kod został skompilowany i przetestowany (`BridgeExtensionTest`: 2 testy, `EmployeeCounterTest`: 2 testy, wszystkie zielone).

### Krok 1: rachunek klas

- Dziś: abstrakcja `Message` + 2 rozszerzenia oraz implementacja `MessageSender` + 2 kanały. Cztery kombinacje obsługują 4 klasy konkretne.
- Po dodaniu `SignedMessage` i `PushSender`: 3 + 3 = **6** klas konkretnych.
- Bez mostu (podklasa dla każdej kombinacji, np. `UrgentSmsMessage`): 3 × 3 = **9** klas, a każdy nowy kanał dokłada tyle klas, ile jest rodzajów wiadomości.

### Kod referencyjny

`src/main/java/pl/training/module4/structural/bridge/PushSender.java`:

```java
package pl.training.module4.structural.bridge;

public class PushSender implements MessageSender {

    @Override
    public void send(String recipient, String content) {
        System.out.println("Push to " + recipient + ": " + content);
    }

}
```

`src/main/java/pl/training/module4/structural/bridge/SignedMessage.java`:

```java
package pl.training.module4.structural.bridge;

import java.util.Objects;

public class SignedMessage extends Message {

    private final String signature;

    public SignedMessage(MessageSender sender, String signature) {
        super(sender);
        this.signature = Objects.requireNonNull(signature, "signature");
    }

    @Override
    public void send(String recipient, String text) {
        sender.send(recipient, text + " (" + signature + ")");
    }

}
```

`src/test/java/pl/training/module4/structural/bridge/BridgeExtensionTest.java`:

```java
@ResourceLock(Resources.SYSTEM_OUT)
final class BridgeExtensionTest {

    @Test
    void everyMessageKindWorksWithEverySender() {
        var sender = new RecordingSender();
        List<Message> messages = List.of(
                new TextMessage(sender),
                new UrgentMessage(sender),
                new SignedMessage(sender, "ops"));

        messages.forEach(message -> message.send("anna", "Server restarted"));

        assertEquals(List.of(
                "anna|Server restarted",
                "anna|[URGENT] SERVER RESTARTED",
                "anna|Server restarted (ops)"), sender.sent);
    }

    @Test
    void pushSenderPrintsThePushChannel() {
        PrintStream original = System.out;
        var output = new ByteArrayOutputStream();
        try (var replacement = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(replacement);
            new UrgentMessage(new PushSender()).send("anna", "disk full");
        } finally {
            System.setOut(original);
        }
        assertEquals(List.of("Push to anna: [URGENT] DISK FULL"),
                output.toString(StandardCharsets.UTF_8).lines().toList());
    }

    private static final class RecordingSender implements MessageSender {

        private final List<String> sent = new ArrayList<>();

        @Override
        public void send(String recipient, String content) {
            sent.add(recipient + "|" + content);
        }

    }

}
```

Importy testu: `org.junit.jupiter.api.Test`, `org.junit.jupiter.api.parallel.ResourceLock`, `org.junit.jupiter.api.parallel.Resources`, `java.io.ByteArrayOutputStream`, `java.io.PrintStream`, `java.nio.charset.StandardCharsets`, `java.util.ArrayList`, `java.util.List` oraz statyczny `org.junit.jupiter.api.Assertions.assertEquals`.

`src/main/java/pl/training/module4/behavioral/visitor/EmployeeCounter.java`:

```java
package pl.training.module4.behavioral.visitor;

public class EmployeeCounter implements Visitor {

    private int count;

    @Override
    public void visit(Employee employee) {
        count++;
    }

    public int count() {
        return count;
    }

}
```

`src/test/java/pl/training/module4/behavioral/visitor/EmployeeCounterTest.java`:

```java
final class EmployeeCounterTest {

    @Test
    void countsEmployeesInTheWholeTree() {
        var main = new Department("Main Department");
        var it = new Department("IT Department");
        var board = new SuperDepartment("Board");
        main.addChild(it);
        main.addChild(board);
        it.addChild(new Employee("Kowalski"));
        it.addChild(new Employee("Karlsson"));
        board.addChild(new Employee("Nowak"));

        var counter = new EmployeeCounter();
        main.accept(counter);

        assertEquals(3, counter.count());
    }

    @Test
    void emptyDepartmentHasNoEmployees() {
        var counter = new EmployeeCounter();
        new Department("Empty").accept(counter);
        assertEquals(0, counter.count());
    }

}
```

### Krok 2: dlaczego to działa bez zmian

- **Bridge:** `Message` zależy wyłącznie od interfejsu `MessageSender`, a `MessageSender` nie wie nic o rodzajach wiadomości. Nowy kanał i nowy rodzaj to nowe klasy na dwóch niezależnych osiach.
- **Visitor:** `Visitor` ma metody domyślne, więc nowy gość nadpisuje tylko `visit(Employee)`. Przejście po drzewie zapewniają węzły - także `SuperDepartment.accept`, który przekazuje gościa dzieciom - więc pracownik w dziale `SuperDepartment` jest policzony.

### Krok 3 (polecenie 8): `Reporter` i `SuperDepartment`

Dla drzewa `Main → Board (SuperDepartment) → Nowak` wynik `main.accept(new Reporter())` to (sprawdzone):

```text
Department: Main
 - Employee Nowak
```

Dział `Board` nie pojawia się w raporcie. Przyczyna: `SuperDepartment.accept` wywołuje przeciążenie `visit(SuperDepartment)`, a `Reporter` go nie nadpisuje - działa pusta metoda domyślna z interfejsu `Visitor`. Kompilator tego nie zgłosi. To ta sama mechanika, która w kroku 2 pozwoliła napisać `EmployeeCounter` jednym nadpisaniem - wygoda i ryzyko mają jedno źródło.

### Krok 4 (polecenie 9): koszt nowego rodzaju węzła `Contractor`

| Plik | Zmiana |
| --- | --- |
| `Contractor.java` (nowy) | `extends Node`, `accept(visitor)` wywołuje `visitor.visit(this)`, `addChild` rzuca wyjątek (liść) |
| `Visitor.java` | nowe przeciążenie `visit(Contractor)` - bez niego `visitor.visit(this)` w `Contractor` się nie skompiluje (żadne istniejące przeciążenie nie pasuje) |
| `Reporter.java` | decyzja: czy i jak raportować wykonawców |
| `EmployeeCounter.java` | decyzja: czy wykonawca jest pracownikiem w sensie licznika |
| `NameValidator.java` | przegląd - prawdopodobnie bez zmian (sprawdza tylko działy) |
| testy i odpowiedniki C#/TS | jak wyżej |

- **Pusta metoda domyślna** `visit(Contractor)`: wszystko się kompiluje, a wszyscy goście **po cichu ignorują** wykonawców (powtórka problemu `SuperDepartment`).
- **Metoda abstrakcyjna:** kompilator wskaże każdego gościa (`NameValidator`, `Reporter`, `EmployeeCounter`, klasy anonimowe), który musi podjąć decyzję. Koszt: każdy gość implementuje wszystkie metody, także te, których nie potrzebuje.
- Pułapka: gdyby `Contractor` dziedziczył po `Employee`, kod skompilowałby się bez zmian w `Visitor`, a wykonawcy byliby traktowani jak pracownicy - decyzja domenowa podjęta przez przypadek.
- Wniosek z macierzy zmian: Visitor ułatwia nowe operacje i utrudnia nowe typy. Gdy częściej dochodzą typy, naturalniejsze są metody polimorficzne w węzłach; w Javie 25 również `sealed` + `switch`, gdzie nowy typ psuje kompilację dokładnie w miejscach decyzji.

### Weryfikacja kryteriów akceptacji

- Testy zielone: `mvn test -Dtest='pl.training.module4.**'` (51 testów modułu + 4 nowe).
- `git status` pokazuje tylko nowe pliki: `PushSender.java`, `SignedMessage.java`, `EmployeeCounter.java` i dwa testy.
- Test mostu korzysta z `RecordingSender` (obiekt zastępczy roli implementacji); konsola sprawdzana tylko dla nowego kanału.
- `EmployeeCounter` liczy pracownika `Nowak` w `SuperDepartment`.
- Kontrolne mutacje: `SignedMessage` wysyłające samo `text` - pada `everyMessageKindWorksWithEverySender`; `EmployeeCounter` z dodatkowym `count++` w `visit(Department)` - pada `countsEmployeesInTheWholeTree`; `PushSender` z prefiksem `Notification to` - pada `pushSenderPrintsThePushChannel`.

### Typowe błędy uczestników

- Dodanie `visit(...)` lub nowej metody do `Visitor` „na wszelki wypadek” - łamie ograniczenie i niczego nie daje.
- `instanceof Employee` wewnątrz gościa zamiast nadpisania przeciążenia.
- Liczenie dzieci w `visit(Department)` zamiast liczenia liści - wynik zależy od struktury i pomija `SuperDepartment`.
- `SignedMessage extends UrgentMessage` albo `PushSender extends EmailSender` - dziedziczenie między elementami jednej osi przywraca kombinatorykę.
- Test oparty wyłącznie na przechwytywaniu konsoli, bez obiektu zastępczego.
- Brak przywrócenia `System.out` w `finally` albo brak `@ResourceLock` - niestabilne testy przy równoległym uruchamianiu.

### Pytania do dyskusji

- Czy `MessageSender` powinien być interfejsem funkcyjnym, skoro kanał to jedna metoda? Co by się zmieniło w teście?
- Czy puste metody domyślne w `Visitor` to dobra decyzja dla tego modelu? Jaką cenę płacimy za wygodę z kroku 2?
- Bridge a Strategy: czym różni się kanał wiadomości od strategii wysyłki?

---

## Ćwiczenie 3: Wariant obiektowy czy funkcyjny?

> Rozwiązanie opracowane na podstawie kodu referencyjnego przygotowanego dla tego ćwiczenia; `FunctionalRequestChainTest` (2 testy) przechodzi.

### Krok 1-2: tabele porównania (wzorcowe)

**Chain of Responsibility** (`behavioral.chainofresponsibility` i `fn/FunctionalStyleChain`):

| Kryterium | Obiektowy | Funkcyjny |
| --- | --- | --- |
| liczba typów | `Handler` + 3 ogniwa | 0 nowych typów, 3 lambdy |
| nazwa domenowa | `Validator`, `Logger`, `Processor` | `step1`, `step2`, `step3` |
| stan | `nextHandler` (chronione, mutowalne pole) | brak |
| tożsamość | nieistotna | nieistotna |
| druga operacja | możliwa w `Handler` | wymaga zmiany typu funkcji |
| testowalność | każde ogniwo osobno, z atrapą następnika | każda lambda osobno, łatwo |
| semantyka identyczna? | **nie** - ogniwo może przerwać | `andThen` zawsze wykonuje każdy krok |

**Strategy** (`behavioral.strategy` i `fn/FunctionalStyleStrategy`): semantyka identyczna dla strategii bezstanowych; `IncrementalIdGenerator` jest stanowy, więc wariant funkcyjny wymaga domknięcia nad licznikiem (`AtomicInteger`) - stan przestaje być widoczny w nazwie typu. Nazwa domenowa (`IdGenerator`) ginie przy `Function<String, String>`; własny `@FunctionalInterface` przywraca ją bez kosztu klasy.

**Decorator** (`structural.decorator` i `fn/FunctionalStyleDecorator`): semantyka zachowana, ale **kierunek zapisu** odwrotny (obiektowo od środka na zewnątrz, funkcyjnie od lewej do prawej). Dekorator obiektowy może mieć stan (np. licznik prób przy retry) i dodatkowe metody, kompozycja funkcji - nie.

**Observer** (`behavioral.observer` i `fn/FunctionalStyleObserver`): `List` daje kolejność rejestracji i duplikaty, `HashSet` - brak kolejności i brak duplikatów. Wariant z lambdami nie ma wyrejestrowania; lambda nie ma stabilnej tożsamości, więc potrzebny byłby uchwyt (`Subscription`) zwracany przez `subscribe`.

**State** (`behavioral.state` i `fn/FunctionalStyleState`): wariant funkcyjny ma jedno zdarzenie (`onEvent`) i stany z efektami ubocznymi; brak nazwanych operacji i domyślnego wyjątku dla niedozwolonego przejścia. Przy wielu operacjach enum z ciałami stałych jest czytelniejszy.

**Command** (`behavioral.command` i `fn/FunctionalStyleCommand`): `Runnable` wystarcza do kolejkowania i wywołania; `undo`, nazwa komendy do logu audytu czy utrwalanie wymagają klasy.

**Singleton** (`creational.singleton` i `fn/FunctionalStyleSingleton`): `memoize` daje jedną instancję na dostawcę i łatwo go podmienić w teście; klasyczny Singleton - jedna instancja na loader klas i trudna podmiana.

### Krok 3: różnica semantyczna łańcucha

- Obiektowo: `new Validator(...).handleRequest("Te")` - walidator zwraca bez delegacji, `Logger` i `Processor` nie działają.
- Funkcyjnie: `step1.andThen(step2).andThen(step3)` nie ma mechanizmu przerwania. Ogniwo może jedynie zwrócić inną wartość, a kolejne i tak się wykona. To potok (bliżej dekoratorów), nie łańcuch odpowiedzialności.

### Kod referencyjny

`src/main/java/pl/training/module4/fn/FunctionalRequestChain.java`:

```java
package pl.training.module4.fn;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public final class FunctionalRequestChain {

    private final List<Function<String, Optional<String>>> steps;

    public FunctionalRequestChain(List<Function<String, Optional<String>>> steps) {
        this.steps = List.copyOf(steps);
    }

    public Optional<String> handle(String request) {
        Optional<String> current = Optional.of(request);
        for (var step : steps) {
            current = current.flatMap(step);
        }
        return current;
    }

}
```

`src/test/java/pl/training/module4/fn/FunctionalRequestChainTest.java`:

```java
final class FunctionalRequestChainTest {

    private final List<String> log = new ArrayList<>();
    private final List<String> processed = new ArrayList<>();

    private final FunctionalRequestChain chain = new FunctionalRequestChain(List.of(
            request -> request.length() < 3 ? Optional.empty() : Optional.of(request),
            request -> { log.add(request); return Optional.of(request); },
            request -> { processed.add(request); return Optional.of("Processing: " + request); }));

    @Test
    void validRequestPassesThroughEveryStep() {
        assertEquals(Optional.of("Processing: Test"), chain.handle("Test"));
        assertEquals(List.of("Test"), log);
        assertEquals(List.of("Test"), processed);
    }

    @Test
    void shortRequestStopsTheChainLikeTheValidator() {
        assertEquals(Optional.empty(), chain.handle("Te"));
        assertEquals(List.of(), log);
        assertEquals(List.of(), processed);
    }

}
```

- `flatMap` na pustym `Optional` nie wywołuje kolejnych kroków - to właśnie przerwanie łańcucha bez wyjątków.
- `List.copyOf` chroni przed zmianą listy kroków po utworzeniu łańcucha (i odrzuca `null`).
- Alternatywy równie dobre: `Predicate<String>` jako warunek kontynuacji, pętla z `break`, własny interfejs `Step { Optional<String> handle(String request); }` z nazwą domenową.
- Wariant „pierwszy obsługujący kończy” (klasyczny GoF) wymagałby innej konwencji: krok zwracający wartość kończy łańcuch, a `Optional.empty()` przekazuje dalej. Warto o to zapytać grupę - kierunek konwencji to decyzja kontraktu.

### Krok 4: reguła zespołu (przykład)

> Rolę wzorca zapisujemy lambdą lub referencją do metody, gdy ma jedną operację, nie ma stanu, nie potrzebuje tożsamości i nie musi być rozpoznawalna w logach. Gdy rola potrzebuje nazwy domenowej, deklarujemy własny `@FunctionalInterface`. Klasę piszemy, gdy rola ma stan, więcej niż jedną operację (np. `undo`), cykl życia (rejestracja i wyrejestrowanie) albo gdy wariant funkcyjny zmieniłby semantykę (przerwanie łańcucha, kolejność).

### Weryfikacja kryteriów akceptacji

- Trzy tabele z uzasadnionym ostatnim wierszem: para łańcucha jest obowiązkowa i jako jedyna ma odpowiedź „nie” w wierszu semantyki.
- Dla `"Te"`: `Optional.empty()`, puste listy `log` i `processed` - jak w wersji obiektowej.
- Kontrolne mutacje: walidacja `request -> Optional.of(request)` - pada `shortRequestStopsTheChainLikeTheValidator`; pętla przywracająca `Optional.of(request)` po pustym wyniku - pada ten sam test.
- Reguła odwołuje się do kryteriów (stan, liczba operacji, tożsamość, semantyka).

### Typowe błędy uczestników

- Uznanie wersji z `andThen` za równoważną, bo „dla `Test` wynik jest taki sam” - testy tylko ścieżki pozytywnej nie wykryją różnicy.
- Przerywanie łańcucha wyjątkiem (sterowanie przepływem przez wyjątki, koszt i nieczytelność).
- Zwracanie `null` z kroku zamiast `Optional.empty()`.
- Ocena wyłącznie po liczbie linii kodu.
- Reguła „zawsze lambdy” lub „nigdy lambd” bez kryteriów.

### Pytania do dyskusji

- Czy `FunctionalRequestChain` to jeszcze Chain of Responsibility, czy już potok z warunkiem? Czy nazwa ma znaczenie?
- Jak zapewnić wyrejestrowanie odbiorcy w wariancie z lambdami?
- Dlaczego przy wariancie funkcyjnym tym bardziej potrzebny jest test zachowania brzegowego?

---

## Ćwiczenie 4: Dobór wzorca - karty problemów

> Rozwiązanie opracowane na podstawie katalogu wzorców ze slajdów (zastosowanie, konsekwencje) i mapy decyzji.

### Krok 1: tabela referencyjna

| Karta | Rodzaj zmienności | Wzorzec | Prostsza alternatywa | Główny koszt lub ryzyko |
| --- | --- | --- | --- | --- |
| K1 | algorytm eksportu; wybiera użytkownik w czasie działania; zestaw otwarty | Strategy + rejestr `Map<Format, ReportExporter>` | `switch` po enumie `Format` (dla 2-3 stabilnych formatów) | wspólny kontrakt eksportera (strumień czy bajty, kodowanie); jedno miejsce rejestracji |
| K2 | zachowanie zależne od bieżącego stanu i przejścia | State (np. `enum` stanów z domyślnym wyjątkiem) | `switch` po enumie w jednym miejscu albo tabela przejść w `EnumMap` | rozproszona tabela przejść; stan po wyjątku; trwały kod stanu w bazie |
| K3 | obcy interfejs i model (grosze, wyjątki kontrolowane) | Adapter implementujący `PaymentGateway` | brak - tłumaczenie musi gdzieś być; ważne, by było w jednym miejscu | zaokrąglenie i skala przy konwersji kwot, waluta, mapowanie wyjątków z zachowaniem przyczyny; na granicy systemu - Anti-Corruption Layer (moduł 5) |
| K4 | opcjonalne dodatki w różnych kombinacjach | Decorator (składany w fabryce lub konfiguracji) | gotowe interceptory biblioteki klienta HTTP | kolejność warstw (retry wewnątrz czy na zewnątrz pamięci podręcznej), idempotencja ponowień, maskowanie wyjątków |
| K5 | struktura część-całość dowolnej głębokości | Composite (+ Visitor lub `sealed` + `switch`, gdy operacji przybywa) | rekurencyjna funkcja nad `record Category(List<Category> children, List<Product> products)` | cykle i głębokość; koszt wielokrotnego liczenia sum w dużych drzewach |
| K6 | operacje użytkownika do cofnięcia i ponowienia | Command z `undo()` + Memento (stos historii) | migawka całego dokumentu po każdej operacji | poprawne `undo` dla każdej komendy; pamięć; spójność z zapisem dokumentu |
| K7 | wiele parametrów opcjonalnych i niezmienniki kombinacji | Builder z walidacją w `build()` | `record` z konstruktorem kanonicznym i kilka nazwanych fabryk | duplikacja pól; builder wielokrotnego użytku i mutowalny produkt |
| K8 | zmienny zbiór odbiorców zdarzenia | Observer (zdarzenie domenowe `OrderPlaced`) | jawne wywołania w serwisie, gdy odbiorców jest 2-3 i są stali | kolejność, polityka błędów (czy błąd e-maila cofa zamówienie?), transakcja; asynchroniczność i outbox to osobna decyzja |
| K9 | sieć wzajemnych zależności komponentów | Mediator (`AddressFormMediator`) | model formularza z regułami albo Observer między komponentami (ale sieć zostaje) | mediator-bóg skupiający logikę wszystkich pól |
| K10 | brak realnej zmienności | **bez wzorca**: `if` albo `enum DiscountType` z wartością | - | Strategy byłaby spekulacją (YAGNI): więcej typów bez korzyści |
| K11 | wiele obiektów, mało rodzajów stanu wewnętrznego | Flyweight (`TileType` współdzielony, pozycja jako stan zewnętrzny) | tablica indeksów rodzajów (`byte[]`) i tablica 12 tekstur | stan współdzielony musi być niemutowalny; kafelek traci tożsamość |
| K12 | spójne rodziny produktów | Abstract Factory (`UiFactory`: `createButton`, `createField`, `createWindow`) | motyw jako dane (tokeny stylu) zamiast klas | nowy rodzaj komponentu wymaga zmiany interfejsu i wszystkich fabryk |

### Krok 2-3: karty „bez wzorca” i połączenia

- **Bez wzorca lub najprostszy mechanizm:** K10 (zawsze); K1 i K2, jeśli zestaw jest mały i stabilny; K11 w wersji z tablicą indeksów bywa prostsza niż obiekty-pyłki.
- **Połączenia:** K1 (Strategy + rejestr lub fabryka), K4 (Decorator + fabryka składająca warstwy), K5 (Composite + Visitor), K6 (Command + Memento), K8 (Observer; przy wielu kanałach także Mediator lub szyna zdarzeń).

### Weryfikacja kryteriów akceptacji

- Każdy wiersz zaczyna się od kolumny „Rodzaj zmienności”.
- Każdy wiersz ma prostszą alternatywę - w K3 jest nią świadomy brak alternatywy (tłumaczenie jest konieczne).
- K10 ma odpowiedź „bez wzorca” z uzasadnieniem (brak zmienności od pięciu lat, jedno miejsce użycia).
- Koszty są konkretne (np. kolejność warstw w K4, polityka błędów w K8).

### Typowe błędy uczestników

- Start od nazwy wzorca („tu pasuje Strategy”) zamiast od pytania, co się zmienia i kto decyduje.
- Strategy dla K2 („bo jest `switch`”) - pytanie kontrolne: czy wariant zmienia się w wyniku operacji na obiekcie?
- Wzorzec dla K10 „na przyszłość”.
- Observer dla K9 bez zauważenia, że sieć wzajemnych subskrypcji to nadal sieć powiązań.
- Pominięcie polityki błędów w K8 i kolejności w K4.
- Builder dla każdego obiektu z więcej niż trzema polami, bez sprawdzenia `record`.

### Pytania do dyskusji

- K8: co zmienia się w kontrakcie, gdy z jednego odbiorcy robi się lista? Kiedy to już rozszerzenie zachowania (moduł 5)?
- K5: Visitor czy metody w węzłach - co częściej będzie się zmieniać w waszym katalogu produktów?
- K3: gdzie kończy się Adapter, a zaczyna Anti-Corruption Layer?

---

## Odpowiedzi do pytań sprawdzających

1. **Czym jest wzorzec projektowy i z jakich elementów składa się jego opis?**
   To odkrywany na bazie doświadczenia szablon sprawdzonego rozwiązania powszechnie występującego problemu (koncepcja Christophera Alexandra, katalog obiektowy GoF). Opis obejmuje nazwę, problem i kontekst, rozwiązanie (role i ich współpracę) oraz konsekwencje. Nie jest gotowym kodem ani biblioteką.

2. **Na czym polega podział ze względu na rodzaj i zakres? Które wzorce GoF są klasowe?**
   Rodzaj: kreacyjne (tworzenie i konfigurowanie obiektów), strukturalne (struktury obiektów i klas), behawioralne (zachowanie i odpowiedzialność współpracujących obiektów). Zakres: klasowe (statyczne związki między klasami, dziedziczenie) i obiektowe (dynamiczne zależności między obiektami, kompozycja). Klasowe: Factory Method, Interpreter, Template Method i klasowy wariant Adaptera.

3. **Czym różni się Factory Method od Abstract Factory? Co jest trudne w Abstract Factory?**
   Factory Method tworzy jeden produkt, a jego typ określa podklasa (lub wstrzyknięty twórca). Abstract Factory tworzy całe rodziny powiązanych produktów, które mają być używane razem. W Abstract Factory łatwo dodać nową rodzinę, a trudno nowy rodzaj produktu - wymaga zmiany interfejsu i wszystkich fabryk.

4. **Jakie problemy rozwiązuje Builder i jakie ryzyko niesie mutowalny produkt?**
   Oddziela proces konstrukcji złożonego obiektu od jego reprezentacji, porządkuje wiele parametrów opcjonalnych i daje miejsce na walidację niezmienników całości. Builder zwracający tę samą mutowalną instancję (jak `GenericConnectionUrlBuilder.build()`) pozwala zmienić już zbudowany i przekazany produkt dalszym wywołaniem metod buildera.

5. **Dlaczego Singleton bywa uważany za antywzorzec i jakie są alternatywy w Javie?**
   Wprowadza globalny stan i ukrytą zależność: testy wpływają na siebie, implementacji nie da się łatwo podmienić, a cykl życia jest sztywny. Alternatywy: jedna instancja tworzona w composition root i wstrzykiwana, `enum INSTANCE` (jedna instancja na loader klas), idiom holder, `memoize(Supplier)`.

6. **Co odróżnia Adapter, Decorator, Proxy i Facade?**
   Adapter zmienia interfejs na oczekiwany przez klienta; Decorator zachowuje interfejs i dokłada odpowiedzialność; Proxy zachowuje interfejs i kontroluje dostęp (może nie delegować, opóźnić, wywołać zdalnie); Facade daje nowy, uproszczony interfejs nad wieloma obiektami podsystemu. Struktura bywa identyczna - rozstrzyga intencja.

7. **Czym różni się przezroczysty i bezpieczny wariant Composite?**
   Przezroczysty umieszcza zarządzanie dziećmi we wspólnym typie komponentu (jednolitość, ale liść musi rzucać wyjątek lub ignorować `add`, jak `Employee.addChild`). Bezpieczny umieszcza je tylko w kompozycie (kontrola kompilatora, ale klient czasem rozróżnia typy).

8. **Dlaczego kolejność dekoratorów jest częścią kontraktu?**
   Każda warstwa zmienia zasięg działania pozostałych: `audit(retry(core))` zapisze jedną operację, `retry(audit(core))` każdą próbę; podobnie pamięć podręczna przed lub za ponowieniami czy transakcją. Zmiana kolejności zmienia obserwowalne zachowanie.

9. **Co odróżnia State od Strategy, a Strategy od Template Method?**
   Strategy wybiera klient z zewnątrz, State zmienia sam obiekt w wyniku operacji i wyznacza dozwolone przejścia. Strategy opiera się na kompozycji (wymiana w czasie działania), Template Method na dziedziczeniu (kroki ustalone przez podklasę, sekwencja chroniona `final`).

10. **Jaki wymiar zmian preferuje Visitor? Czym grożą puste metody domyślne?**
    Częste dodawanie nowych operacji przy stabilnym zestawie typów elementów; nowy typ elementu wymaga zmiany interfejsu gościa i przeglądu wszystkich gości. Puste metody domyślne wyłączają kontrolę kompilatora - gość po cichu pomija nieobsłużony typ (jak `Reporter` pomija `SuperDepartment`).

11. **Jakie decyzje trzeba podjąć, projektując Observer?**
    Kolejność powiadomień, dopuszczalność duplikatów, polityka wyjątków odbiorcy (przerwać czy kontynuować i zebrać błędy), wątek i synchroniczność, wyrejestrowanie (także w trakcie publikacji) oraz to, czy odbiorcy są wywoływani pod blokadą.

12. **Kiedy lambda wystarcza w miejsce klasy wzorca, a kiedy zmienia semantykę?**
    Wystarcza dla jednej, bezstanowej operacji bez potrzeby tożsamości i nazwy (Strategy, prosta komenda, fabryka, krok szablonu). Zmienia semantykę, gdy mechanizm funkcyjny działa inaczej niż wzorzec: `andThen` nie przerywa łańcucha, `Stream` jest iteratorem wewnętrznym i jednorazowym, lambdy nie da się niezawodnie wyrejestrować. Klasa wraca, gdy rola ma stan, kilka operacji lub cykl życia.

13. **Jakie sygnały wskazują, że wzorzec jest zbędny?**
    Jedna implementacja od dawna, interfejs powtarzający API jednej klasy, fabryka z jednym `new`, większość implementacji rzucająca `UnsupportedOperationException`, nowy wariant nadal wymagający zmian w wielu miejscach, konfiguracja trudniejsza od bezpośredniego `new`. Usuwanie wzorca to równie poprawna refaktoryzacja - moduł 5.
