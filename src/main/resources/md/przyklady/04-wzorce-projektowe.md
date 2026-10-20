# Moduł 4. Wzorce projektowe - przewodnik prowadzącego po przykładach

Dokument opisuje kod modułu 4 w projekcie: co ilustruje każdy przykład, jak go pokazać na żywo i na co zwrócić uwagę grupy. Slajdy: `src/main/resources/md/slides/04-wzorce-projektowe-slajdy.md`.

Wszystkie ścieżki Java są względne wobec katalogu głównego repozytorium. Kod produkcyjny: `src/main/java/pl/training/module4/` (pakiety `creational`, `structural`, `behavioral`, `fn`), testy: `src/test/java/pl/training/module4/`. W tabelach i opisach pakiety podajemy względem `pl.training.module4` (np. `creational.builder` oznacza `pl.training.module4.creational.builder`).

Moduł 4 jest katalogiem wzorców: pokazujemy intencję, strukturę, koszt i wariant w nowoczesnej Javie. Przykłady są celowo małe i mają po jednej klasie `Application` z metodą `main`. Refaktoryzacje prowadzące do wzorców (i od nich) oraz porównania wzorców w kontekście zmiany kodu legacy są tematem modułu 5 - w razie pytań o „jak tu dojść z istniejącego kodu” odsyłaj tam.

## Uruchamianie przykładów i testów

### Java

```bash
# z katalogu głównego repozytorium
mvn -q compile && java -cp target/classes pl.training.module4.Module4Examples            # lista kluczy
mvn -q compile && java -cp target/classes pl.training.module4.Module4Examples builder    # jeden przykład
mvn -q compile && java -cp target/classes pl.training.module4.Module4Examples all        # wszystkie po kolei

# testy modułu 4
mvn test -Dtest='pl.training.module4.**'
```

Pojedyncza klasa testowa, np. `mvn test -Dtest=AdditionalPatternsExamplesTest`. Z IDE: uruchom `Module4Examples.main` z argumentem (klucz albo `all`) albo bezpośrednio `main` wybranej klasy `Application`.

`Module4Examples` to jeden punkt wejścia zamiast 34 metod `main`:

- bez argumentów wypisuje dostępne klucze i klasy,
- z kluczem (np. `observer`) uruchamia jeden przykład; nieznany klucz kończy się kodem wyjścia 1 i komunikatem `Unknown example: ...`,
- `all` uruchamia wszystkie przykłady w kolejności z listy, poprzedzając każdy nagłówkiem `=== klucz ===`.

Klucze: `abstract-factory`, `builder`, `factory-method`, `prototype`, `singleton`, `adapter`, `bridge`, `composite`, `decorator`, `facade`, `flyweight`, `proxy`, `chain-of-responsibility`, `command`, `interpreter`, `iterator`, `mediator`, `memento`, `observer`, `state`, `strategy`, `template-method`, `visitor` oraz warianty funkcyjne `fn-chain`, `fn-command`, `fn-decorator`, `fn-factory`, `fn-iterator`, `fn-memento`, `fn-observer`, `fn-singleton`, `fn-state`, `fn-strategy`, `fn-template`.

Przykład `decorator` czyta jedną linię ze standardowego wejścia; gdy wejście jest puste lub zamknięte, używa tekstu `Hello Decorator Pattern`. W trybie `all` wejście nie jest czytane wcale, więc uruchomienie nigdy się nie blokuje. Do pokazu z własnym tekstem: `echo "Ala ma kota" | java -cp target/classes pl.training.module4.Module4Examples decorator`.

Część przykładów pisze przez `java.util.logging`. Wtedy przed każdym komunikatem `INFO:` pojawia się linia z datą i nazwą klasy (format zależy od ustawień regionalnych, np. `paź 20, 2026 1:47:34 PM pl.training.module4.creational.builder.Director run`). Poniżej pomijamy te linie.

Oczekiwane wyjście `Module4Examples all` (bez linii nagłówkowych `java.util.logging`):

```text
=== abstract-factory ===
INFO: Connection: 21
INFO: Secured connection: 22
=== builder ===
INFO: Connection url: jdbc:postgres://localhost:5432/test?encoding=UTF-8
=== factory-method ===
INFO: Id: 00000000000000000001
=== prototype ===
INFO: Window: Window(x=0, y=0, width=800, height=600)
=== singleton ===
INFO: Is same: true
Info: Success
=== adapter ===
INFO: Changing temperature: 18.0
=== bridge ===
Email to anna: Server restarted
SMS to anna: Server restarted
Email to anna: [URGENT] SERVER RESTARTED
SMS to anna: [URGENT] SERVER RESTARTED
=== composite ===
Department: IT Department
 - Employee Kowalski
 - Employee Karlsson
=== decorator ===
INFO: hello_decorator_pattern
=== facade ===
INFO: Step 1
INFO: Step 2
=== flyweight ===
Oak (green) at 1,2
Pine (dark green) at 5,3
Oak (green) at 9,8
true
=== proxy ===
INFO: Checking security
INFO: Payment started
INFO: Payment completed
=== chain-of-responsibility ===
INFO: Test
INFO: Processing: Test
=== command ===
INFO: 2026-10-20T13:47:34.438274
INFO: Connecting...
INFO: Connected...
=== interpreter ===
INFO: Result: 25.0
=== iterator ===
Playing: Intro (90s)
Playing: Main Theme (225s)
Playing: Outro (130s)
Reversed: Outro
Reversed: Main Theme
Reversed: Intro
=== mediator ===
Alice sends: Hello everyone
Bob received from Alice: Hello everyone
Carol received from Alice: Hello everyone
Bob sends: Hi Alice
Alice received from Bob: Hi Alice
Carol received from Bob: Hi Alice
=== memento ===
INFO: Deposited balance: 10
INFO: Restored balance: 0
=== observer ===
INFO: Started
=== state ===
INFO: State: NEW
INFO: State: PAID
INFO: State: SHIPPED
INFO: Cancellation is not allowed in state SHIPPED
=== strategy ===
INFO: Id: 6eae08aa-784f-4dfd-a8e2-7a8ad38731a2
INFO: Id: 00000000000000000001
=== template-method ===
TEMPLATE METHOD
=== visitor ===
Department name too short: Ma
=== fn-chain ===
Request -> validate -> authenticate -> log
=== fn-command ===
Opening file...
Saving file...
Closing file...
=== fn-decorator ===
[DECORATOR PATTERN]
=== fn-factory ===
Circle
Square
=== fn-iterator ===
ONE
TWO
THREE
=== fn-memento ===
Set state: A
Set state: B
Restored: A
=== fn-observer ===
Observer 1 received: Functional Observer pattern with lambdas!
Observer 2 received: FUNCTIONAL OBSERVER PATTERN WITH LAMBDAS!
=== fn-singleton ===
Connecting to DB...
true
=== fn-state ===
Locked → unlocking
Unlocked → locking
Locked → unlocking
=== fn-strategy ===
UPPER: FUNCTIONAL PATTERNS
LOWER: functional patterns
REVERSE: snrettaP lanoitcnuF
=== fn-template ===
Start processing...
Custom step with: data
Finish processing!
```

Wartości niedeterministyczne: znacznik czasu w `command` i UUID w `strategy` są inne przy każdym uruchomieniu.

### C#

```bash
cd csharp && dotnet run --project src/Training.Module4 -- <klucz|all>
cd csharp && dotnet test tests/Training.Module4.Tests
```

Kod: `csharp/src/Training.Module4` z katalogami `Creational`, `Structural`, `Behavioral` i `Fn` (np. `csharp/src/Training.Module4/Creational/Builder`, `csharp/src/Training.Module4/Structural/Bridge`, `csharp/src/Training.Module4/Behavioral/Visitor`). Interfejsy mają prefiks `I` (np. `IMessageSender`, `IVisitor`), pozostałe nazwy klas są takie same jak w Javie. Testy: `csharp/tests/Training.Module4.Tests` (`ExamplesSmokeTest.cs`, `AdditionalPatternsExamplesTest.cs`). Klucze przykładów są takie same jak w Javie.

### TypeScript

```bash
cd typescript && npm ci && npm run build && npm run module4 -- <klucz|all>
cd typescript && npx vitest run test/module4
```

Kod: `typescript/src/module4` z katalogami `creational`, `structural`, `behavioral` i `fn` (te same nazwy pakietów co w Javie, np. `typescript/src/module4/creational/builder`), punkt wejścia `typescript/src/module4/main.ts`. Testy: `typescript/test/module4` (`PatternsSmoke.test.ts`, `AdditionalPatternsExamplesTest.test.ts`).

## Mapa przykładów

| Nr | Przykład | Sekcja slajdów | Pakiet (`pl.training.module4.`) | Klucz | Testy | Zadania |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | Factory Method i Abstract Factory | 2.1-2.3 | `creational.factorymethod`, `creational.abstractfactory` | `factory-method`, `abstract-factory` | `usesTheRegisteredFtpControlPort` + smoke | Ćwiczenie 4 (K1, K12) |
| 2 | Builder | 2.4-2.5 | `creational.builder` | `builder` | smoke | Aktywność 2.8, Ćwiczenie 1 (F4), Ćwiczenie 4 (K7) |
| 3 | Prototype i Singleton | 2.6-2.7 | `creational.prototype`, `creational.singleton` | `prototype`, `singleton` | smoke | Ćwiczenie 3 (opcjonalnie) |
| 4 | Adapter | 3.1-3.2 | `structural.adapter` | `adapter` | `convertsTemperatureDifferencesWithoutAnAbsoluteScaleOffset` | Ćwiczenie 4 (K3) |
| 5 | Bridge | 3.3 | `structural.bridge` | `bridge` | `bridgeCombinesEveryMessageKindWithEverySender` | Ćwiczenie 2A |
| 6 | Composite | 3.4-3.5 | `structural.composite` | `composite` | smoke | Ćwiczenie 4 (K5) |
| 7 | Decorator i Proxy | 3.6-3.7, 3.10-3.12 | `structural.decorator`, `structural.proxy` | `decorator`, `proxy` | `decoratorUsesDefaultTextWhenStdinIsEmpty`, `decoratorTransformsTheLineReadFromStdin` | Aktywność 3.12, Ćwiczenie 1 (F3), Ćwiczenie 4 (K4) |
| 8 | Facade i Flyweight | 3.8-3.9 | `structural.facade`, `structural.flyweight` | `facade`, `flyweight` | `sharesOneFlyweightInstancePerIntrinsicState` | Ćwiczenie 4 (K11) |
| 9 | Chain of Responsibility i Command | 4.1-4.3 | `behavioral.chainofresponsibility`, `behavioral.command` | `chain-of-responsibility`, `command` | smoke | Ćwiczenie 3, Ćwiczenie 4 (K6) |
| 10 | Interpreter i Iterator | 4.4-4.5 | `behavioral.interpreter`, `behavioral.iterator` | `interpreter`, `iterator` | `iteratorDemoWalksThePlaylistForwardAndBackward`, `playlistIteratorIsExhaustedAfterTheLastSong` | Ćwiczenie 1 (F2, F6) |
| 11 | Mediator i Observer | 4.6, 4.8-4.9 | `behavioral.mediator`, `behavioral.observer` | `mediator`, `observer` | `mediatorDeliversMessagesToEveryoneExceptTheSender` | Ćwiczenie 1 (F1), Ćwiczenie 4 (K8, K9) |
| 12 | Memento | 4.7 | `behavioral.memento` | `memento` | `restoresTheBalanceRecordedInTheMemento` | Ćwiczenie 4 (K6) |
| 13 | State i Strategy | 4.10-4.13, 4.17-4.18 | `behavioral.state`, `behavioral.strategy` | `state`, `strategy` | `orderStateMovesThroughItsLifecycleAndRejectsInvalidTransitions`, `doesNotSubtractAChargeDuringTheFreeRentalPeriod` | Aktywność 4.18, Ćwiczenie 1 (F5), Ćwiczenie 4 (K1, K2) |
| 14 | Template Method | 4.14 | `behavioral.templatemethod` | `template-method` | smoke | Ćwiczenie 3 |
| 15 | Visitor | 4.15-4.16 | `behavioral.visitor` | `visitor` | smoke | Ćwiczenie 1 (F7), Ćwiczenie 2B |
| 16 | Warianty funkcyjne | 1.6, 4.19-4.20 | `fn` | `fn-*` | `functionalStateDemoAlternatesTransitions` + smoke | Ćwiczenie 3 |
| 17 | Punkt wejścia i testy modułu | przekrojowo | `Module4Examples` | `all` | `ExamplesSmokeTest`, `AdditionalPatternsExamplesTest` | - |

„Smoke” oznacza, że przykład jest objęty tylko testem `ExamplesSmokeTest.exampleRunsWithoutException` (uruchamia się bez wyjątku i coś wypisuje). To celowo słaba asercja - nie chroni żadnej reguły wzorca. Sekcja 1 slajdów (definicja, klasyfikacja, katalog, rodzaj zmienności, koszt wzorca) nie ma osobnego kodu; ilustruj ją uruchomieniem `Module4Examples` bez argumentów (lista 23 + 11 kluczy jako „mapa katalogu”) oraz przykładem 13 (ta sama struktura, różna intencja).

---

## Przykład 1. Factory Method i Abstract Factory (`creational.factorymethod`, `creational.abstractfactory`)

**Sekcja teorii:** 2.1 Factory Method, 2.2 Abstract Factory, 2.3 fabryki w kodzie i ich koszt; nawiązanie do 1.4 (kod, który jedynie wybiera i tworzy obiekt).

**Co ilustruje:** dwa poziomy ukrywania konkretnych klas. W Factory Method klient (`Service`) dostaje obiekt, który tworzy **jeden** produkt (`IdGenerator`). W Abstract Factory klient dostaje fabrykę **rodziny** produktów (`Connection` i `SecuredConnection`), które muszą do siebie pasować.

**Klasy:**

- `src/main/java/pl/training/module4/creational/factorymethod/IdGeneratorFactory.java` - interfejs z jedną metodą `create()`.
- `src/main/java/pl/training/module4/creational/factorymethod/ProductionIdGeneratorFactory.java` - zwraca zawsze tę samą instancję `UuidGenerator`.
- `src/main/java/pl/training/module4/creational/factorymethod/TestIdGeneratorFactory.java` - przy każdym `create()` zwraca nowy `IncrementalIdGenerator` (licznik od 1, format `%020d`).
- `src/main/java/pl/training/module4/creational/factorymethod/Service.java` - w `run()` woła `create()` i loguje `Id: ...`.
- `src/main/java/pl/training/module4/creational/abstractfactory/ConnectionAbstractFactory.java` - `createConnection()`, `createSecuredConnection()`.
- `src/main/java/pl/training/module4/creational/abstractfactory/ftp/FtpConnectionFactory.java` (porty 21 i 22, `SftpConnection` z `AES`), `src/main/java/pl/training/module4/creational/abstractfactory/http/HttpConnectionFactory.java` (porty 80 i 443).
- `src/main/java/pl/training/module4/creational/abstractfactory/Service.java` - używa wyłącznie interfejsów.

**Na co zwrócić uwagę:**

- Klasyczny Factory Method GoF jest **klasowy**: metoda wytwórcza jest abstrakcyjna w klasie twórcy i nadpisywana w podklasie. W repozytorium twórca jest wstrzykiwanym obiektem (`Service(IdGeneratorFactory)`), czyli wariant kompozycyjny - strukturalnie bliski jednoproduktowej fabryce abstrakcyjnej albo „strategii tworzenia”. Warto to powiedzieć wprost, bo uczestnicy często pytają o różnicę.
- Dwie fabryki mają **różne kontrakty**: produkcyjna współdzieli instancję, testowa tworzy nową. Ponieważ `Service.run()` woła `create()` za każdym razem, generator testowy przy każdym `run()` zaczyna od `00000000000000000001`. Pytanie do grupy: czy `create()` obiecuje nowy obiekt? Kontrakt fabryki trzeba nazwać.
- Abstract Factory chroni spójność rodziny (FTP + SFTP albo HTTP + HTTPS). Koszt widać przy pytaniu: „dodajmy `createProxyConnection()`” - zmiana interfejsu i obu fabryk. Nowa rodzina (np. `SmtpConnectionFactory`) to natomiast tylko nowe klasy.
- Wariant funkcyjny: `fn/FunctionalStyleFactory` (`Supplier<Shape>`, `Square::new`). Dla jednej metody bez stanu lambda wystarcza; rodzina produktów to zwykle rekord z kilkoma `Supplier`.

**Testy:** `AdditionalPatternsExamplesTest.usesTheRegisteredFtpControlPort` (port 21 dla `FtpConnection`); poza tym tylko smoke.

**Demonstracja na żywo (ok. 6 min):**

1. Uruchom `factory-method`, potem w `creational/factorymethod/Application.java` zamień `TestIdGeneratorFactory` na `ProductionIdGeneratorFactory` i uruchom ponownie (UUID zamiast licznika). `Service` się nie zmienia.
2. Uruchom `abstract-factory`, zamień `FtpConnectionFactory` na `HttpConnectionFactory` (porty 80 i 443).
3. Zapytaj: „Co trzeba zmienić, by dodać trzeci produkt do rodziny?” i pokaż listę plików. Przywróć kod.

---

## Przykład 2. Builder (`creational.builder`)

**Sekcja teorii:** 2.4 Builder - intencja i struktura, 2.5 kod i pułapki; Aktywność 2.8.

**Co ilustruje:** płynny interfejs budowy adresu JDBC z wartościami domyślnymi zależnymi od bazy oraz rolę `Director`. Wynik: `jdbc:postgres://localhost:5432/test?encoding=UTF-8`.

**Klasy:**

- `src/main/java/pl/training/module4/creational/builder/ConnectionUrlBuilder.java` - interfejs kroków `host`, `port`, `protocol`, `database`, `encoding`, `build`.
- `src/main/java/pl/training/module4/creational/builder/GenericConnectionUrlBuilder.java` - realizacja ogólna, domyślne kodowanie `UTF-8`.
- `src/main/java/pl/training/module4/creational/builder/MySqlConnectionUrlBuilder.java` - **dziedziczy** po `Generic...` i ustawia `mysql`/`3306`.
- `src/main/java/pl/training/module4/creational/builder/PostgresSqlConnectionUrlBuilder.java` - **deleguje** do wewnętrznego `Generic...` i ustawia `postgres`/`5432`.
- `src/main/java/pl/training/module4/creational/builder/ConnectionUrl.java` - produkt z polami pakietowymi i `toString()` w formacie JDBC.
- `src/main/java/pl/training/module4/creational/builder/Director.java` - w `run()` woła `build()` i loguje wynik.

**Na co zwrócić uwagę:**

- Dwa sposoby ponownego użycia ogólnego buildera obok siebie: dziedziczenie (MySQL) i kompozycja z delegacją (PostgreSQL). Delegacja wymaga powtórzenia wszystkich metod, ale zwraca właściwy typ (`PostgresSqlConnectionUrlBuilder`) bez rzutowania i nie wiąże z wnętrzem klasy bazowej.
- `build()` zwraca **tę samą mutowalną instancję** `ConnectionUrl`. Wywołanie `host("other")` po `build()` zmienia obiekt już przekazany dalej. To najważniejsza pułapka do pokazania (slajd 2.5).
- Brak walidacji niezmienników: bez `host` powstaje `jdbc:postgres://null:5432/...`. Builder jest naturalnym miejscem na sprawdzenie kompletności i spójności całości (np. port w zakresie, wymagany host).
- `Director` w GoF steruje kolejnością kroków budowy. Tu jedynie wywołuje `build()` - kroki wykonuje `Application`. Można zapytać, co musiałoby się znaleźć w `Director`, aby uzasadnić jego istnienie (np. „profil połączenia testowego”).
- W pakiecie `behavioral.observer` jest jeszcze jeden builder: `ServerEvent.builder()` - klasyczny wewnętrzny builder w stylu generowanym przez Lombok, nieużywany w demo. Wykorzystujemy go w Ćwiczeniu 1.
- Alternatywa w Javie 25: dla kilku pól wystarczy `record ConnectionUrl(...)` z walidacją w konstruktorze kanonicznym i statycznymi fabrykami `postgres(host, db)`, `mysql(host, db)`.

**Testy:** tylko smoke (`ExamplesSmokeTest`). Brak testu niezmienników jest świadomy - to dobre pytanie do grupy: jaki test wykryłby problem z mutowalnym produktem?

**Demonstracja na żywo (ok. 5 min):**

1. Uruchom `builder`.
2. W `creational/builder/Application.java` po `new Director(builder).run();` dopisz `builder.host("prod-db"); new Director(builder).run();` i pokaż, że „zbudowany” wcześniej obiekt to ten sam egzemplarz (np. zachowaj referencję z pierwszego `build()` i wypisz ją po zmianie).
3. Usuń `.host("localhost")` - pokaż `null` w adresie. Przywróć kod.

---

## Przykład 3. Prototype i Singleton (`creational.prototype`, `creational.singleton`)

**Sekcja teorii:** 2.6 Prototype, 2.7 Singleton; nawiązanie do 1.6 (enum i `memoize` jako zapis roli w Javie 25).

**Co ilustruje:** dwa wzorce kreacyjne, które w nowoczesnej Javie najczęściej zastępujemy prostszym idiomem. Prototype: nowe okno przez `clone()` istniejącego. Singleton: leniwie tworzony, synchronizowany `Logger.getInstance()`.

**Klasy:**

- `src/main/java/pl/training/module4/creational/prototype/Window.java` - `final class Window implements Cloneable` z czterema polami `final int`, `clone()` z kowariantnym typem zwracanym.
- `src/main/java/pl/training/module4/creational/prototype/Application.java` - klonuje `fullScreenWindow`; obiekt `dialog` jest tworzony, ale nieużywany.
- `src/main/java/pl/training/module4/creational/singleton/Logger.java` - prywatny konstruktor, `synchronized static getInstance()`, leniwa inicjalizacja.
- `src/main/java/pl/training/module4/fn/FunctionalStyleSingleton.java` - `memoize(Supplier)` jako wariant funkcyjny.

**Na co zwrócić uwagę:**

- `Window` jest niemutowalny, więc klonowanie niczego nie daje - ten sam efekt da współdzielenie instancji. Prototype ma sens dla obiektów kosztownych w konfiguracji i **mutowalnych** po skopiowaniu. Przy okazji: `Cloneable` ma wadliwy kontrakt (metoda `clone` w `Object`, wyjątek kontrolowany, płytka kopia); w Javie preferujemy konstruktor kopiujący lub metodę `copy()`.
- `Module4Examples` opakowuje `prototype` w `runPrototype()`, bo `main` deklaruje `CloneNotSupportedException` - ilustracja kosztu wyjątku kontrolowanego w kontrakcie `clone()`.
- `Logger.getInstance()` synchronizuje każde wywołanie. Alternatywy: idiom holder, `enum INSTANCE`, a najczęściej jedna instancja tworzona w composition root i przekazywana jawnie.
- Nazwa `Logger` koliduje z `java.util.logging.Logger` - dlatego `Application` używa pełnej nazwy kwalifikowanej dla loggera JUL. Drobiazg, ale dobrze pokazuje koszt globalnych nazw.
- `memoize` daje jedną instancję **na dostawcę**, nie na proces - to zaleta (można wstrzyknąć inny dostawca w teście), ale też zmiana semantyki względem klasycznego Singletona.

**Testy:** tylko smoke.

**Demonstracja na żywo (ok. 4 min):** uruchom `prototype`, `singleton` i `fn-singleton`. Przy `fn-singleton` zwróć uwagę, że `Connecting to DB...` pojawia się raz, a `c1 == c2` daje `true`. Zapytaj: „Jak w teście podmienić `Logger.getInstance()`?” (bez zmian w kodzie produkcyjnym - nie da się łatwo).

---

## Przykład 4. Adapter (`structural.adapter`)

**Sekcja teorii:** 3.1 Adapter - intencja i struktura, 3.2 kod i semantyka; 3.11 rodzina opakowań.

**Co ilustruje:** obiektowy Adapter tłumaczący interfejs `TemperatureController` (stopnie Celsjusza, dwie metody) na API `AirConditioningController.changeTemperature(deltaInFahrenheit)`.

**Klasy:**

- `src/main/java/pl/training/module4/structural/adapter/TemperatureController.java` - interfejs docelowy (Target).
- `src/main/java/pl/training/module4/structural/adapter/AirConditioningController.java` - klasa adaptowana (Adaptee).
- `src/main/java/pl/training/module4/structural/adapter/TemperatureControllerAdapter.java` - adapter z prywatną konwersją `celsiusDeltaToFahrenheit`.

**Na co zwrócić uwagę:**

- Adapter przelicza **różnicę** temperatur, więc mnoży przez 1.8 bez dodawania 32. To właśnie semantyka, której nie widać w sygnaturze `double -> double`. Test o długiej nazwie dokumentuje tę decyzję.
- `temperatureDown` przekazuje wartość ujemną - adapter tłumaczy także znak i kierunek, nie tylko nazwę metody.
- Adapter nie dodaje logiki domenowej (np. limitów temperatury) - to byłaby inna odpowiedzialność.
- Wariant klasowy (adapter dziedziczący po `AirConditioningController` i implementujący `TemperatureController`) też jest możliwy, ale wiąże z klasą adaptowaną i ujawnia jej metody.
- Adapter na granicy z systemem zewnętrznym, tłumaczący model i błędy, prowadzi do Anti-Corruption Layer - moduł 5.

**Testy:** `AdditionalPatternsExamplesTest.convertsTemperatureDifferencesWithoutAnAbsoluteScaleOffset` - podklasa `RecordingAirConditioningController` (obiekt szpiegujący) zapisuje przekazane delty; oczekiwane `[18.0, -18.0]`.

**Demonstracja na żywo (ok. 4 min):**

1. Uruchom `adapter` (`Changing temperature: 18.0`).
2. Kontrolna mutacja: w `celsiusDeltaToFahrenheit` zmień na `value * 1.8 + 32`. Uruchom `mvn test -Dtest=AdditionalPatternsExamplesTest` - test pada. Przywróć kod.
3. Pytanie: czy „poprawny” wzór na przeliczenie temperatury zawsze jest poprawny w adapterze? (Zależy od tego, czy tłumaczymy wartość bezwzględną, czy różnicę.)

---

## Przykład 5. Bridge (`structural.bridge`)

**Sekcja teorii:** 3.3 Bridge; 3.11 rodzina opakowań; Ćwiczenie 2A.

**Co ilustruje:** dwie niezależne hierarchie - rodzaj wiadomości (abstrakcja) i kanał wysyłki (implementacja). Abstrakcja trzyma referencję do implementacji zamiast po niej dziedziczyć, więc 2 rodzaje i 2 kanały dają 4 klasy obsługujące 4 kombinacje.

**Klasy:**

- `src/main/java/pl/training/module4/structural/bridge/Message.java` - abstrakcja z chronionym polem `sender`.
- `src/main/java/pl/training/module4/structural/bridge/TextMessage.java`, `src/main/java/pl/training/module4/structural/bridge/UrgentMessage.java` - rozszerzone abstrakcje (`UrgentMessage` dodaje `[URGENT] ` i zamienia tekst na wielkie litery).
- `src/main/java/pl/training/module4/structural/bridge/MessageSender.java` - interfejs implementacji (jedna metoda).
- `src/main/java/pl/training/module4/structural/bridge/EmailSender.java`, `src/main/java/pl/training/module4/structural/bridge/SmsSender.java` - konkretne implementacje.

**Na co zwrócić uwagę:**

- Bridge projektuje się **z góry**, gdy wiadomo, że obie osie będą rosły. Adapter dodaje się **po fakcie** do istniejącego API. Strukturalnie oba to „obiekt z polem innego typu”.
- `MessageSender` ma jedną metodę, więc kanał może być lambdą (`(recipient, content) -> ...`). Abstrakcja (`Message`) zostaje klasą, bo rodzaje wiadomości mają własne zachowanie.
- Różnica względem Strategy: implementacja w moście jest trwałą częścią abstrakcji (ustalana przy tworzeniu), a nie algorytmem wymienianym przez klienta dla pojedynczej operacji. Granica bywa płynna - dobre pytanie do grupy.
- Rachunek klas: przy 3 rodzajach i 3 kanałach most daje 6 klas, a hierarchia kombinacji - 9. Ćwiczenie 2A to sprawdza w praktyce.

**Testy:** `AdditionalPatternsExamplesTest.bridgeCombinesEveryMessageKindWithEverySender` - porównuje cztery linie wyjścia `Application.main`.

**Demonstracja na żywo (ok. 4 min):** uruchom `bridge`. Narysuj na tablicy dwie hierarchie i zapytaj, ile klas powstałoby bez mostu po dodaniu kanału push i wiadomości podpisanej. Nie pisz kodu - to treść Ćwiczenia 2.

---

## Przykład 6. Composite (`structural.composite`)

**Sekcja teorii:** 3.4 Composite - intencja i struktura, 3.5 wariant przezroczysty i bezpieczny.

**Co ilustruje:** drzewo działów (`Department`) i pracowników (`Employee`) obsługiwane jednym typem `Node`. `printInfo()` na dowolnym węźle drukuje całe poddrzewo.

**Klasy:**

- `src/main/java/pl/training/module4/structural/composite/Node.java` - abstrakcyjny komponent z `name`, listą `children`, `addChild` i `setChildren`.
- `src/main/java/pl/training/module4/structural/composite/Department.java` - kompozyt, drukuje siebie i dzieci.
- `src/main/java/pl/training/module4/structural/composite/Employee.java` - liść, `addChild` rzuca `UnsupportedOperationException`.

**Na co zwrócić uwagę:**

- To wariant **przezroczysty**: zarządzanie dziećmi jest we wspólnym typie, więc liść musi rzucać wyjątek. Klient nie musi rozróżniać typów, ale kompilator nie ochroni przed `employee.addChild(...)`. Wariant bezpieczny przenosi `addChild` do kompozytu.
- `children` jest chronione i mutowalne, a `setChildren` publiczne - każdy może podmienić listę dzieci. W nowoczesnej Javie: `sealed interface Node permits Department, Employee`, rekordy i `List.copyOf`.
- Brak ochrony przed cyklem (`a.addChild(b); b.addChild(a);` kończy się `StackOverflowError` przy `printInfo`).
- Demo wywołuje `printInfo()` na `itDepartment`, a nie na korzeniu - operacja na fragmencie hierarchii działa tak samo jak na całości (konsekwencja z katalogu).
- Ten sam model w wersji z gościem jest w przykładzie 15 (`behavioral.visitor`).

**Testy:** tylko smoke.

**Demonstracja na żywo (ok. 3 min):** uruchom `composite`, potem w `Application` zamień `itDepartment.printInfo()` na `mainDepartment.printInfo()`. Zapytaj, co się stanie przy `new Employee("X").addChild(...)`.

---

## Przykład 7. Decorator i Proxy (`structural.decorator`, `structural.proxy`)

**Sekcja teorii:** 3.6-3.7 Decorator, 3.10 Proxy, 3.11 rodzina opakowań; Aktywność 3.12.

**Co ilustruje:** dwa wzorce o **identycznej strukturze** (klasa implementuje ten sam interfejs i deleguje do opakowanego obiektu), różniące się intencją. Decorator dokłada odpowiedzialność (przekształcenie tekstu), Proxy kontroluje dostęp (bezpieczeństwo, logowanie płatności).

**Klasy:**

- `src/main/java/pl/training/module4/structural/decorator/Reader.java` - komponent.
- `src/main/java/pl/training/module4/structural/decorator/SystemInReader.java` - rdzeń; czyta linię ze standardowego wejścia albo zwraca `Hello Decorator Pattern`.
- `src/main/java/pl/training/module4/structural/decorator/ReaderDecorator.java` - abstrakcyjny dekorator delegujący.
- `src/main/java/pl/training/module4/structural/decorator/UnderscoreReaderDecorator.java`, `src/main/java/pl/training/module4/structural/decorator/LowerCaseReaderDecorator.java`.
- `src/main/java/pl/training/module4/structural/proxy/PaymentsService.java`, `src/main/java/pl/training/module4/structural/proxy/RemotePaymentsService.java` (obiekt rzeczywisty).
- `src/main/java/pl/training/module4/structural/proxy/PaymentSecurityProxy.java` (loguje przed delegacją), `src/main/java/pl/training/module4/structural/proxy/PaymentLoggerProxy.java` (loguje po delegacji).
- `src/main/java/pl/training/module4/fn/FunctionalStyleDecorator.java` - `Function.andThen`.

**Na co zwrócić uwagę:**

- Kolejność dekoratorów: `new LowerCase(new Underscore(reader))` - najpierw działa warstwa najbliżej rdzenia. Tu oba przekształcenia są przemienne, więc kolejności nie widać. Dla pary „przytnij” i „zamień spacje” wynik zależy od kolejności - dobry przykład do dopowiedzenia.
- Wariant funkcyjny składa od lewej do prawej (`base.andThen(trim).andThen(upper).andThen(addBrackets)`), obiektowy - od środka na zewnątrz. Uczestnicy często mylą te kierunki.
- `PaymentSecurityProxy` w obecnej postaci niczego nie blokuje - tylko loguje `Checking security`. Strukturalnie to dekorator. Prawdziwy proxy ochronny może **nie delegować** (rzucić wyjątek), proxy wirtualny tworzy obiekt rzeczywisty dopiero przy pierwszym użyciu, proxy zdalny ukrywa komunikację.
- `PaymentLoggerProxy` loguje `Payment completed` tylko wtedy, gdy delegat nie rzuci wyjątku - kolejność i obsługa błędów są częścią kontraktu opakowania.
- Dekorator i proxy nie są przezroczyste dla tożsamości (`==`), `getClass()` ani `equals`. Proxy frameworków (np. transakcyjne) nie działają dla wywołań wewnątrz tej samej klasy.

**Testy:** `ExamplesSmokeTest.decoratorUsesDefaultTextWhenStdinIsEmpty` (oczekuje `INFO: hello_decorator_pattern`) i `ExamplesSmokeTest.decoratorTransformsTheLineReadFromStdin` (wejście `Hello World Foo` daje `hello_world_foo`). Proxy - tylko smoke.

**Demonstracja na żywo (ok. 6 min):**

1. `echo "Ala Ma Kota" | java -cp target/classes pl.training.module4.Module4Examples decorator` - wynik `ala_ma_kota`.
2. Uruchom `fn-decorator` i porównaj kierunek składania.
3. Uruchom `proxy`; zwróć uwagę na kolejność trzech linii. Zamień kolejność opakowań w `structural/proxy/Application.java` (`new PaymentSecurityProxy(new PaymentLoggerProxy(...))`) i porównaj. Przywróć kod.
4. Przejdź do Aktywności 3.12.

---

## Przykład 8. Facade i Flyweight (`structural.facade`, `structural.flyweight`)

**Sekcja teorii:** 3.8 Facade, 3.9 Flyweight.

**Co ilustruje:** Facade - jeden punkt wejścia (`run()`) uruchamiający kroki dwóch podsystemów. Flyweight - współdzielenie stanu wewnętrznego drzew (`TreeType`: nazwa i kolor) między wieloma obiektami `Tree` różniącymi się tylko położeniem.

**Klasy:**

- `src/main/java/pl/training/module4/structural/facade/Facade.java`, `src/main/java/pl/training/module4/structural/facade/FirstService.java`, `src/main/java/pl/training/module4/structural/facade/SecondService.java`.
- `src/main/java/pl/training/module4/structural/flyweight/TreeType.java` - pyłek (stan wewnętrzny), konstruktor pakietowy.
- `src/main/java/pl/training/module4/structural/flyweight/TreeFactory.java` - fabryka pyłków ze statyczną `HashMap` i `computeIfAbsent`.
- `src/main/java/pl/training/module4/structural/flyweight/Tree.java` - obiekt kontekstu ze stanem zewnętrznym (`x`, `y`).

**Na co zwrócić uwagę:**

- Fasada w przykładzie jest minimalna. Jej wartość rośnie, gdy ukrywa kolejność kroków, konfigurację i obsługę błędów podsystemu. Fasada przekazująca wywołania 1:1 albo rosnąca o każdą nową operację to sygnał nadmiaru. Porównanie Facade z Anti-Corruption Layer - moduł 5.
- `TreeFactory` łączy dwa wzorce: fabrykę i pulę pyłków. Konstruktor `TreeType` jest pakietowy, więc klient spoza pakietu nie ominie puli.
- Klucz `name + ":" + color` jest niejednoznaczny: `("a:b", "c")` i `("a", "b:c")` dają `a:b:c`. Lepszy klucz to `record TreeKey(String name, String color)`.
- Statyczna `HashMap` nie jest bezpieczna wątkowo i nigdy nie zwalnia wpisów. Przy wielu wątkach - `ConcurrentHashMap`; przy dużej rotacji typów - polityka usuwania.
- Wymóg z katalogu: aplikacja **nie może zależeć od tożsamości** obiektów - tu wręcz test sprawdza, że tożsamość jest współdzielona (`assertSame`).
- Przykład z JDK: `Integer.valueOf(127) == Integer.valueOf(127)` daje `true`, a dla 128 zwykle `false` (pamięć podręczna -128..127).

**Testy:** `AdditionalPatternsExamplesTest.sharesOneFlyweightInstancePerIntrinsicState`. Facade - tylko smoke.

**Demonstracja na żywo (ok. 4 min):** uruchom `facade` i `flyweight` (ostatnia linia `true` to porównanie tożsamości). Zapytaj o klucz: jak wymusić kolizję dwóch różnych typów drzew?

---

## Przykład 9. Chain of Responsibility i Command (`behavioral.chainofresponsibility`, `behavioral.command`)

**Sekcja teorii:** 4.1 Chain of Responsibility, 4.2-4.3 Command, 4.17 (CoR a Decorator), 4.20 (semantyka `fn-chain`).

**Co ilustruje:** CoR - łańcuch `Validator → Logger → Processor`, w którym walidator może przerwać przetwarzanie. Command - żądania jako obiekty wykonywane przez `Invoker`.

**Klasy:**

- `src/main/java/pl/training/module4/behavioral/chainofresponsibility/Handler.java` - abstrakcja z chronionym polem `nextHandler`.
- `src/main/java/pl/training/module4/behavioral/chainofresponsibility/Validator.java` (przerywa dla żądań krótszych niż 3 znaki), `src/main/java/pl/training/module4/behavioral/chainofresponsibility/Logger.java`, `src/main/java/pl/training/module4/behavioral/chainofresponsibility/Processor.java` (ogniwo końcowe).
- `src/main/java/pl/training/module4/behavioral/command/Command.java`, `src/main/java/pl/training/module4/behavioral/command/PrintTime.java`, `src/main/java/pl/training/module4/behavioral/command/ConnectToServer.java`.
- `src/main/java/pl/training/module4/behavioral/command/Invoker.java` - `invoke(command)` oraz kolejka `register`/`invokeAll`.
- `src/main/java/pl/training/module4/fn/FunctionalStyleChain.java`, `src/main/java/pl/training/module4/fn/FunctionalStyleCommand.java`.

**Na co zwrócić uwagę:**

- Konsekwencja z katalogu „brak gwarancji odbioru żądania” jest widoczna wprost: `handleRequest("Te")` nie robi nic i nic nie zgłasza. Czy to poprawne zachowanie, zależy od kontraktu.
- `Validator` kończy łańcuch, `Logger` zawsze deleguje. Gdyby wszystkie ogniwa zawsze delegowały, mielibyśmy potok dekoratorów, a nie łańcuch odpowiedzialności.
- `FunctionalStyleChain` (`step1.andThen(step2).andThen(step3)`) **nie potrafi przerwać** - każde ogniwo zawsze działa. To potok przekształceń, nie CoR. Ćwiczenie 3 każe tę różnicę usunąć.
- Klasa łańcucha nazywa się `Logger` i koliduje z `java.util.logging.Logger` - stąd pełna nazwa w polu `log`.
- Komendy w przykładzie nie mają odbiorcy (Receiver) - same wykonują pracę. W GoF komenda zwykle przechowuje odbiorcę i parametry, a `execute()` deleguje do `receiver.action()`.
- `Invoker.register`/`invokeAll` nie są używane w demo, ale pokazują drugą intencję Command: kolejkowanie i późniejsze wykonanie. `invokeAll` nie czyści kolejki - ponowne wywołanie wykona komendy drugi raz.
- `PrintTime` używa `LocalDateTime.now()` - wynik niedeterministyczny, trudny do testu. Undo nie jest zaimplementowane; potrzebowałoby `undo()` albo migawki (przykład 12).

**Testy:** tylko smoke.

**Demonstracja na żywo (ok. 5 min):**

1. Uruchom `chain-of-responsibility`, potem w `Application` zmień żądanie na `"Te"` - brak wyjścia.
2. Uruchom `fn-chain` i zapytaj, gdzie w tej wersji umieścić walidację, która może zatrzymać przetwarzanie.
3. Uruchom `command` i `fn-command`. W `behavioral/command/Application.java` dopisz `invoker.register(new PrintTime()); invoker.invokeAll(); invoker.invokeAll();` - komenda wykona się dwa razy. Przywróć kod.

---

## Przykład 10. Interpreter i Iterator (`behavioral.interpreter`, `behavioral.iterator`)

**Sekcja teorii:** 4.4 Interpreter, 4.5 Iterator.

**Co ilustruje:** Interpreter - drzewo wyrażenia `2a * 3 + 1` (dla `a = 4` wynik `25.0`) zbudowane z wyrażeń terminalnych i nieterminalnych. Iterator - własny agregat `Playlist` z dwoma sposobami przejścia (w przód i wstecz) bez ujawniania tablicy.

**Klasy:**

- `src/main/java/pl/training/module4/behavioral/interpreter/Expression.java` - `evaluate(Map<String, Double> context)`.
- `src/main/java/pl/training/module4/behavioral/interpreter/Literal.java`, `src/main/java/pl/training/module4/behavioral/interpreter/Variable.java` (terminalne), `src/main/java/pl/training/module4/behavioral/interpreter/Operation.java` (nieterminalne, `left`, `right`, `BiOperator`).
- `src/main/java/pl/training/module4/behavioral/interpreter/BiOperator.java`, `src/main/java/pl/training/module4/behavioral/interpreter/Plus.java`, `src/main/java/pl/training/module4/behavioral/interpreter/Minus.java`, `src/main/java/pl/training/module4/behavioral/interpreter/Multiply.java`.
- `src/main/java/pl/training/module4/behavioral/interpreter/Application.java` - `op(name)` z pamięcią podręczną operatorów (`computeIfAbsent`) i `operatorFactory` (`switch`).
- `src/main/java/pl/training/module4/behavioral/iterator/Playlist.java` - tablica powiększana dwukrotnie, wewnętrzna klasa `PlaylistIterator(start, step)`, metoda `reversed()` zwracająca lambdę `Iterable`.
- `src/main/java/pl/training/module4/behavioral/iterator/Song.java` - rekord.
- `src/main/java/pl/training/module4/fn/FunctionalStyleIterator.java` - `Stream` jako iterator wewnętrzny.

**Na co zwrócić uwagę:**

- Interpreter nie ma parsera: drzewo składni buduje się ręcznie. W praktyce koszt Interpretera to głównie gramatyka i parser; dla złożonej gramatyki lepszy jest generator parserów albo gotowy silnik.
- `Variable.evaluate` zwraca `context.get(name)` jako `double` - brak zmiennej kończy się `NullPointerException` przy rozpakowaniu. Komunikat błędu dla użytkownika języka to część kontraktu interpretera.
- W `Application` ukryte są dwa dodatkowe wzorce: `op(...)` współdzieli instancje operatorów (Flyweight), a `operatorFactory` to prosta fabryka. `BiOperator` to z kolei strategia działania.
- Wariant w Javie 25: `sealed interface Expr permits Num, Var, Add, Mul` + rekordy + `switch` z dopasowaniem wzorców. Nowe operacje (np. wypisywanie, upraszczanie) dodaje się wtedy jako funkcje, nie metody w każdej klasie.
- `Playlist.iterator()` jest metodą wytwórczą (Factory Method) dla iteratora - klasyczne powiązanie z katalogu. `reversed()` daje drugi sposób przejścia bez kopiowania danych.
- `PlaylistIterator` nie wykrywa modyfikacji listy w trakcie przejścia (kolekcje JDK są fail-fast dzięki `modCount`). Rzuca natomiast `NoSuchElementException` po końcu - zgodnie z kontraktem `Iterator`.
- Iterator zewnętrzny pozwala przerwać i mieć dwa niezależne kursory; `Stream` (`fn-iterator`) jest wewnętrzny i jednorazowy.

**Testy:** `AdditionalPatternsExamplesTest.iteratorDemoWalksThePlaylistForwardAndBackward` (sześć linii wyjścia), `AdditionalPatternsExamplesTest.playlistIteratorIsExhaustedAfterTheLastSong` (wyczerpanie i pusta lista wstecz). Interpreter - tylko smoke.

**Demonstracja na żywo (ok. 5 min):**

1. Uruchom `interpreter`; zmień kontekst na `Map.of("a", 1.0)` (wynik `7.0`), potem na `Map.of()` - `NullPointerException`. Przywróć kod.
2. Uruchom `iterator` i `fn-iterator`. Zapytaj, jak przerwać przejście po pierwszym utworze dłuższym niż 200 s w każdej z wersji.

---

## Przykład 11. Mediator i Observer (`behavioral.mediator`, `behavioral.observer`)

**Sekcja teorii:** 4.6 Mediator, 4.8-4.9 Observer, 4.17 (Facade a Mediator).

**Co ilustruje:** dwa sposoby rozluźnienia komunikacji wielu obiektów. Mediator: uczestnicy czatu znają tylko pokój (`ChatRoom`), który rozsyła wiadomości wszystkim poza nadawcą. Observer: szyna zdarzeń (`EventsBus`) powiadamia zarejestrowanych odbiorców `Consumer<ServerEvent>`.

**Klasy:**

- `src/main/java/pl/training/module4/behavioral/mediator/ChatMediator.java` - `join`, `broadcast`.
- `src/main/java/pl/training/module4/behavioral/mediator/ChatRoom.java` - konkretny mediator, lista uczestników, porównanie tożsamości `participant != sender`.
- `src/main/java/pl/training/module4/behavioral/mediator/Participant.java` - współpracownik; w konstruktorze woła `mediator.join(this)`.
- `src/main/java/pl/training/module4/behavioral/observer/EventsBus.java` - `synchronizedSet(new HashSet<>())`, `addConsumer`, `publish`.
- `src/main/java/pl/training/module4/behavioral/observer/Logger.java` - odbiorca (`Consumer<ServerEvent>`).
- `src/main/java/pl/training/module4/behavioral/observer/ServerEvent.java` - zdarzenie z wewnętrznym builderem.
- `src/main/java/pl/training/module4/fn/FunctionalStyleObserver.java` - lista lambd.

**Na co zwrócić uwagę:**

- Mediator zastępuje sieć powiązań „każdy z każdym” powiązaniami z jednym obiektem, ale centralizuje sterowanie. Gdy w mediatorze zaczyna się logika biznesowa wszystkich uczestników, powstaje mediator-bóg.
- `Participant` rejestruje się w konstruktorze - `this` wycieka przed zakończeniem konstrukcji. Przy dziedziczeniu lub wielowątkowości to ryzyko; bezpieczniej rejestrować w fabryce. Brak operacji opuszczenia pokoju.
- `EventsBus` używa `HashSet`: **kolejność powiadomień nie jest określona**, a dodanie tego samego obiektu drugi raz niczego nie zmienia. Lista dałaby kolejność rejestracji i dopuszczała duplikaty - to decyzja kontraktu.
- `forEach` na zbiorze z `synchronizedSet` wykonuje się pod blokadą tego zbioru. Odbiorca, który długo działa, blokuje inne publikacje i rejestracje; odbiorca rejestrujący kolejnego w innym wątku może prowadzić do zakleszczenia. Alternatywa: `CopyOnWriteArrayList` (iteracja po migawce).
- Brak wyrejestrowania. Lambda nie ma stabilnej tożsamości, więc bez uchwytu (`Subscription`) nie da się jej niezawodnie usunąć. Pierwszy wyjątek odbiorcy przerywa pętlę - pozostali nie dostaną zdarzenia.
- `ServerEvent` ma builder, z którego nikt nie korzysta (`new ServerEvent("Started")` w demo) - materiał do Ćwiczenia 1.
- Observer a Mediator: obserwator nie wie o innych obserwatorach, mediator zna wszystkich i koordynuje ich współpracę.

**Testy:** `AdditionalPatternsExamplesTest.mediatorDeliversMessagesToEveryoneExceptTheSender` (sześć linii w ustalonej kolejności). Observer - tylko smoke.

**Demonstracja na żywo (ok. 5 min):**

1. Uruchom `mediator`. Zapytaj: ile referencji między uczestnikami byłoby bez pokoju przy 10 osobach?
2. Uruchom `observer` i `fn-observer`. W `behavioral/observer/Application.java` dodaj drugiego odbiorcę lambdą i uruchom kilka razy - kolejność nie jest gwarantowana kontraktem (w praktyce może się nie zmieniać, ale nic jej nie obiecuje).
3. Dodaj odbiorcę rzucającego wyjątek i pokaż, że przerywa publikację. Przywróć kod.

---

## Przykład 12. Memento (`behavioral.memento`)

**Sekcja teorii:** 4.7 Memento; nawiązanie do 4.3 (undo w Command).

**Co ilustruje:** zapis i przywrócenie salda konta bez ujawniania jego wnętrza opiekunowi migawek.

**Klasy:**

- `src/main/java/pl/training/module4/behavioral/memento/accounts/Account.java` - Originator: `createMemento()`, `restoreMemento(memento)`.
- `src/main/java/pl/training/module4/behavioral/memento/accounts/Memento.java` - publiczna klasa z **pakietowym** konstruktorem i pakietową metodą `balance()`.
- `src/main/java/pl/training/module4/behavioral/memento/Application.java` - Caretaker (lista migawek w innym pakiecie niż `Account`).
- `src/main/java/pl/training/module4/fn/FunctionalStyleMemento.java` - migawka jako domknięcie `Supplier<String>`.

**Na co zwrócić uwagę:**

- Umieszczenie `Account` i `Memento` w podpakiecie `accounts` realizuje „wąski interfejs” z katalogu: `Application` może przechować migawkę, ale nie może jej utworzyć ani odczytać salda. Pokaż, że `memento.balance()` w `Application` się nie skompiluje.
- Migawka przechowuje referencję do `BigDecimal`. To bezpieczne tylko dlatego, że `BigDecimal` jest niemutowalny. Dla mutowalnego stanu (lista, mapa) potrzebna jest kopia głęboka.
- Koszt pamięci: każda migawka to pełna kopia stanu. Dla dużych obiektów rozważ zapis różnic albo zdarzeń.
- W Javie 25 migawka to naturalnie rekord; wariant z domknięciem (`fn-memento`) jest zwięzły, ale przechwytuje referencję - przy mutowalnym stanie wymaga jawnej kopii.
- Memento z Command daje undo: komenda zapisuje migawkę przed wykonaniem, `undo()` ją przywraca.

**Testy:** `AdditionalPatternsExamplesTest.restoresTheBalanceRecordedInTheMemento`.

**Demonstracja na żywo (ok. 3 min):** uruchom `memento` i `fn-memento`. W `behavioral/memento/Application.java` spróbuj wywołać `caretaker.getFirst().balance()` - błąd kompilacji jest celem projektu, nie przeszkodą.

---

## Przykład 13. State i Strategy (`behavioral.state`, `behavioral.strategy`)

**Sekcja teorii:** 4.10-4.11 State, 4.12-4.13 Strategy, 4.17 podobna struktura i różna intencja; Aktywność 4.18. Nawiązanie do 1.4 (rodzaj zmienności).

**Co ilustruje:** dwa wzorce o tej samej strukturze (kontekst z polem typu interfejsowego i delegacją) i różnej intencji. W State obiekt sam zmienia stan w wyniku operacji (`state = state.pay()`). W Strategy algorytm wybiera klient z zewnątrz (`new Service(new UuidGenerator())`).

**Klasy:**

- `src/main/java/pl/training/module4/behavioral/state/OrderState.java` - domyślne metody `pay`, `ship`, `cancel` rzucają `IllegalStateException` z nazwą stanu.
- `src/main/java/pl/training/module4/behavioral/state/OrderStatus.java` - `enum` ze stałymi z ciałami (`NEW`, `PAID`), `SHIPPED` i `CANCELLED` bez nadpisań.
- `src/main/java/pl/training/module4/behavioral/state/Order.java` - kontekst.
- `src/main/java/pl/training/module4/behavioral/strategy/IdGenerator.java`, `src/main/java/pl/training/module4/behavioral/strategy/UuidGenerator.java`, `src/main/java/pl/training/module4/behavioral/strategy/IncrementalIdGenerator.java`, `src/main/java/pl/training/module4/behavioral/strategy/Service.java`.
- `src/main/java/pl/training/module4/behavioral/strategy/movies/MovieType.java`, `src/main/java/pl/training/module4/behavioral/strategy/movies/Order.java` - strategia wyrażona danymi enumu.
- `src/main/java/pl/training/module4/fn/FunctionalStyleState.java`, `src/main/java/pl/training/module4/fn/FunctionalStyleStrategy.java`.

**Na co zwrócić uwagę:**

- Tabela przejść jest zapisana w stałych enumu: `NEW --pay--> PAID`, `NEW --cancel--> CANCELLED`, `PAID --ship--> SHIPPED`, `PAID --cancel--> CANCELLED`; wszystko inne rzuca wyjątek. Domyślna metoda rzucająca wyjątek (a nie pusta) jest kluczowa - pusta zmieniłaby niedozwoloną operację w ciche zignorowanie.
- Komunikat używa `this`, czyli nazwy stałej enumu (`Cancellation is not allowed in state SHIPPED`).
- Stany są bezstanowymi singletonami (stałe enumu) - dlatego dane zamówienia muszą być w `Order`, a nie w stanie.
- `FunctionalStyleState` modeluje bramkę LOCKED/UNLOCKED lambdami, które same ustawiają następny stan. Jest zwięzły, ale ma jedno zdarzenie (`onEvent`) i efekty uboczne w stanach; brakuje nazwanych operacji i jawnej tabeli.
- `IncrementalIdGenerator` jest stanowy (licznik `int`, bez synchronizacji). Współdzielona strategia musi być bezstanowa albo bezpieczna wątkowo.
- `MovieType` to strategia jako dane: trzy warianty różnią się parametrami, nie kodem. To często lepsza forma niż hierarchia klas, gdy zestaw jest zamknięty. Test chroni regułę bezpłatnego okresu.
- `IdGenerator` z pakietu `strategy` i z pakietu `factorymethod` mają identyczny kształt - ta sama abstrakcja może grać rolę produktu fabryki albo strategii, zależnie od tego, kto ją wybiera i po co.

**Testy:** `AdditionalPatternsExamplesTest.orderStateMovesThroughItsLifecycleAndRejectsInvalidTransitions` (cykl życia, odrzucenie `cancel` po wysyłce bez zmiany stanu, `pay` po anulowaniu), `AdditionalPatternsExamplesTest.doesNotSubtractAChargeDuringTheFreeRentalPeriod` (koszt dla jednego dnia: 2.0, 1.5, 3.0; wyjątek dla wartości ujemnej), `AdditionalPatternsExamplesTest.functionalStateDemoAlternatesTransitions`.

**Demonstracja na żywo (ok. 6 min):**

1. Uruchom `state`; pokaż tabelę przejść na podstawie enumu.
2. Kontrolna mutacja: w `OrderState.cancel()` zamień `throw` na `return this;`. Uruchom `mvn test -Dtest=AdditionalPatternsExamplesTest` - test cyklu życia pada. Przywróć kod.
3. Uruchom `strategy` i `fn-strategy`. Zadaj pytanie z Aktywności 4.18: kto zmienia wariant i kiedy?

---

## Przykład 14. Template Method (`behavioral.templatemethod`)

**Sekcja teorii:** 4.14 Template Method, 4.17 (Strategy a Template Method).

**Co ilustruje:** szkielet przetwarzania `read → process → write` w metodzie `final run()` z krokami w podklasie `UpperCaseProcessor` (wynik `TEMPLATE METHOD`).

**Klasy:**

- `src/main/java/pl/training/module4/behavioral/templatemethod/Processor.java` - klasa abstrakcyjna z parametrami typu `V` (dane wejściowe) i `PV` (dane przetworzone).
- `src/main/java/pl/training/module4/behavioral/templatemethod/Application.java` - statyczna klasa zagnieżdżona `UpperCaseProcessor`.
- `src/main/java/pl/training/module4/fn/FunctionalStyleTemplate.java` - krok przekazany jako `Consumer<String>`.

**Na co zwrócić uwagę:**

- `final` na `run()` chroni sekwencję - podklasa może zmienić kroki, ale nie ich kolejność. To dokładnie „możliwa zmiana poszczególnych kroków, ale nie ich sekwencji” z katalogu.
- Wszystkie kroki są abstrakcyjne. Hook z implementacją domyślną (np. pusta `afterWrite()`) jest opcjonalny - dodawaj go tylko, gdy domyślne zachowanie jest poprawne dla wszystkich podklas.
- Odwrócenie sterowania: to klasa bazowa wywołuje kod podklasy. Podklasa nie powinna wołać kroków samodzielnie ani polegać na kolejności inicjalizacji pól (hook wywołany z konstruktora bazy widzi niezainicjalizowane pola podklasy).
- `FunctionalStyleTemplate` zamienia dziedziczenie na parametr - szkielet zostaje w `process`, krok przychodzi z zewnątrz. To przejście od Template Method do kompozycji, bliskiej Strategy.

**Testy:** tylko smoke.

**Demonstracja na żywo (ok. 3 min):** uruchom `template-method` i `fn-template`. Spróbuj w `UpperCaseProcessor` nadpisać `run()` - błąd kompilacji (`final`).

---

## Przykład 15. Visitor (`behavioral.visitor`)

**Sekcja teorii:** 4.15-4.16 Visitor, 4.17 (Visitor a metoda polimorficzna); Ćwiczenie 2B.

**Co ilustruje:** operacje na drzewie działów jako osobne klasy gości (`NameValidator`, `Reporter`) bez zmian w klasach węzłów; podwójna dyspozycja przez `accept`/`visit`.

**Klasy:**

- `src/main/java/pl/training/module4/behavioral/visitor/Visitor.java` - trzy przeciążenia `visit` z **pustymi metodami domyślnymi**.
- `src/main/java/pl/training/module4/behavioral/visitor/Node.java` - `accept` przekazuje gościa dzieciom.
- `src/main/java/pl/training/module4/behavioral/visitor/Department.java` - `visitor.visit(this)`, potem `super.accept(visitor)`.
- `src/main/java/pl/training/module4/behavioral/visitor/SuperDepartment.java` - własne `accept` wywołujące przeciążenie `visit(SuperDepartment)`.
- `src/main/java/pl/training/module4/behavioral/visitor/Employee.java` - liść.
- `src/main/java/pl/training/module4/behavioral/visitor/NameValidator.java` - zgłasza nazwy działów krótsze niż 3 znaki.
- `src/main/java/pl/training/module4/behavioral/visitor/Reporter.java` - drukuje działy i pracowników (nieużywany w demo).

**Na co zwrócić uwagę:**

- Przeciążenie `visit(this)` jest wybierane **w czasie kompilacji** na podstawie statycznego typu `this` w danej klasie. Dlatego `SuperDepartment` musi mieć własne `accept` - odziedziczone z `Department` wywołałoby `visit(Department)`.
- Puste metody domyślne pozwalają pisać gości obsługujących tylko część typów, ale **wyłączają kontrolę kompilatora**. `Reporter` nie nadpisuje `visit(SuperDepartment)`, więc dział typu `SuperDepartment` znika z raportu, a jego pracownicy zostają wypisani (sprawdzone: dla drzewa `Main → Board(SuperDepartment) → Nowak` wynik to `Department: Main` i ` - Employee Nowak`).
- Przejście po drzewie jest w węzłach (`accept` deleguje do dzieci). Gość nie wybiera kolejności ani nie może pominąć poddrzewa.
- Gość czyta pole `name` (chronione) - działa, bo jest w tym samym pakiecie. Gość spoza pakietu potrzebowałby publicznego akcesora.
- Macierz zmian: nowa operacja = nowa klasa gościa (łatwo); nowy rodzaj węzła = zmiana interfejsu `Visitor` i przegląd wszystkich gości (trudno). Ćwiczenie 2B każe to policzyć.
- Java 25: `sealed interface Node permits ...` i `switch (node) { case Department d -> ...; case Employee e -> ...; }` dają sprawdzenie wyczerpujące bez `accept` - nowy rodzaj węzła psuje kompilację w każdym `switch`, czyli tam, gdzie trzeba podjąć decyzję.

**Testy:** tylko smoke (`Department name too short: Ma`).

**Demonstracja na żywo (ok. 5 min):**

1. Uruchom `visitor`.
2. W `behavioral/visitor/Application.java` zamień `new NameValidator()` na `new Reporter()` i uruchom.
3. Dodaj `var board = new SuperDepartment("Board"); mainDepartment.addChild(board); board.addChild(new Employee("Nowak"));` i uruchom ponownie z `Reporter` - dział `Board` nie pojawia się w raporcie. Przywróć kod.

---

## Przykład 16. Warianty funkcyjne (`fn`)

**Sekcja teorii:** 1.6 wzorce a język, 4.19-4.20 wariant funkcyjny; Ćwiczenie 3.

**Co ilustruje:** jak wzorce z jedną, bezstanową rolą zamieniają się w interfejsy funkcyjne JDK i co przy tym tracimy.

**Klasy:**

| Klasa | Klucz | Odpowiednik obiektowy | Mechanizm |
| --- | --- | --- | --- |
| `src/main/java/pl/training/module4/fn/FunctionalStyleStrategy.java` | `fn-strategy` | `behavioral.strategy` | `Function<String, String>` |
| `src/main/java/pl/training/module4/fn/FunctionalStyleCommand.java` | `fn-command` | `behavioral.command` | `Runnable` |
| `src/main/java/pl/training/module4/fn/FunctionalStyleFactory.java` | `fn-factory` | `creational.factorymethod` | `Supplier<Shape>`, `Square::new` |
| `src/main/java/pl/training/module4/fn/FunctionalStyleDecorator.java` | `fn-decorator` | `structural.decorator` | `Function.andThen` |
| `src/main/java/pl/training/module4/fn/FunctionalStyleChain.java` | `fn-chain` | `behavioral.chainofresponsibility` | `Function.andThen` (bez przerwania) |
| `src/main/java/pl/training/module4/fn/FunctionalStyleTemplate.java` | `fn-template` | `behavioral.templatemethod` | `Consumer<String>` jako krok |
| `src/main/java/pl/training/module4/fn/FunctionalStyleObserver.java` | `fn-observer` | `behavioral.observer` | `List<Consumer<String>>` |
| `src/main/java/pl/training/module4/fn/FunctionalStyleState.java` | `fn-state` | `behavioral.state` | `State extends Consumer<FunctionalStyleState>` |
| `src/main/java/pl/training/module4/fn/FunctionalStyleMemento.java` | `fn-memento` | `behavioral.memento` | domknięcie `Supplier<String>` |
| `src/main/java/pl/training/module4/fn/FunctionalStyleSingleton.java` | `fn-singleton` | `creational.singleton` | `memoize(Supplier)` |
| `src/main/java/pl/training/module4/fn/FunctionalStyleIterator.java` | `fn-iterator` | `behavioral.iterator` | `Stream` |

**Na co zwrócić uwagę:**

- Dwa warianty mają **inną semantykę** niż odpowiednik obiektowy: `fn-chain` nie przerywa łańcucha, a `fn-iterator` jest iteratorem wewnętrznym i jednorazowym. Pozostałe zachowują intencję, ale tracą nazwę domenową i miejsce na drugą operację.
- `fn-observer` nie ma wyrejestrowania; lambdy nie mają stabilnej tożsamości, więc „usuń tę samą lambdę” nie działa niezawodnie.
- `fn-state`: stany `LOCKED` i `UNLOCKED` to statyczne pola odwołujące się do siebie nawzajem. Działa, bo lambda odczytuje pole dopiero przy wywołaniu, ale kolejność inicjalizacji pól statycznych to subtelna pułapka.
- `fn-singleton`: `memoize` synchronizuje każde `get()`; jedna instancja na dostawcę, nie na proces.
- `fn-memento`: domknięcie przechwytuje referencję - dla `String` bezpiecznie, dla mutowalnych obiektów potrzebna jawna kopia.
- Reguła do zapamiętania: funkcja dla jednej, bezstanowej roli; klasa, gdy rola ma nazwę, stan, kilka operacji lub cykl życia.

**Testy:** `AdditionalPatternsExamplesTest.functionalStateDemoAlternatesTransitions`; pozostałe - smoke.

**Demonstracja na żywo (ok. 5 min):** uruchom kolejno pary `strategy`/`fn-strategy`, `decorator`/`fn-decorator`, `chain-of-responsibility`/`fn-chain`. Przy ostatniej parze zapytaj o wynik dla żądania `"Te"` w obu wersjach - to wprowadzenie do Ćwiczenia 3.

---

## Przykład 17. Punkt wejścia i testy modułu (`pl.training.module4`)

**Sekcja teorii:** przekrojowo; 1.3 (katalog jako lista kluczy).

**Co ilustruje:** jeden punkt wejścia dla 34 przykładów oraz dwa rodzaje testów: dymne (każdy przykład się uruchamia) i dokumentujące reguły wybranych wzorców.

**Klasy:**

- `src/main/java/pl/training/module4/Module4Examples.java` - rekord `Example(key, javaClass, run)`, lista `EXAMPLES`, metody `execute` (zwraca kod wyjścia bez `System.exit`), `tryRun`, `runAll` (podmienia `System.in` na pusty strumień).
- `src/test/java/pl/training/module4/ExamplesSmokeTest.java` - test parametryzowany po wszystkich kluczach, przechwytuje `System.out`, `System.err` i `java.util.logging`; testy nieznanego klucza, listy kluczy, kolejności `all` i wejścia dla `decorator`.
- `src/test/java/pl/training/module4/AdditionalPatternsExamplesTest.java` - 11 testów reguł (adapter, strategia filmowa, port FTP, memento, flyweight, cykl stanów, stan funkcyjny, iterator x2, bridge, mediator).

**Na co zwrócić uwagę:**

- `ExamplesSmokeTest` daje szerokie pokrycie przy bardzo słabej asercji (brak wyjątku i niepuste wyjście). Dobry kontrprzykład: wysokie pokrycie nie oznacza ochrony reguł wzorca.
- Oba testy używają `@ResourceLock` na `System.out` - przechwytywanie globalnego wyjścia wymaga wyłączności przy równoległym uruchamianiu testów.
- `AdditionalPatternsExamplesTest.outputOf(...)` to wzór do skopiowania w Ćwiczeniu 2 (test kanału piszącego na konsolę).
- Łącznie moduł ma 51 testów (11 + 40 przypadków `ExamplesSmokeTest`).

**Testy:** `mvn test -Dtest='pl.training.module4.**'`.

**Demonstracja na żywo:** na początku modułu uruchom `Module4Examples` bez argumentów jako mapę katalogu (slajd 1.3). Pod koniec, przy omówieniu Ćwiczenia 2, pokaż, że nowe testy uczestników są w tych samych pakietach co kod (dostęp do elementów pakietowych).

---

## Proponowany plan modułu (ok. 240 min)

| Blok | Czas (orientacyjnie) | Materiał |
| --- | --- | --- |
| Wprowadzenie, `Module4Examples` jako mapa katalogu | 3 min | slajdy: Agenda, 1.3; przykład 17 |
| Sekcja 1: definicja, klasyfikacja, rodzaj zmienności, koszt, Java 25, Aktywność 1.7 | 15 min | slajdy 1.1-1.7 |
| Sekcja 2: wzorce kreacyjne, Aktywność 2.8 | 22 min | przykłady 1-3 |
| Sekcja 3: wzorce strukturalne, Aktywność 3.12 | 28 min | przykłady 4-8 |
| Sekcja 4: wzorce behawioralne, Aktywność 4.18 | 35 min | przykłady 9-15 |
| Sekcja 4: wariant funkcyjny | 7 min | slajdy 4.19-4.20; przykład 16 |
| Ćwiczenie 1 (rozpoznawanie wzorców) | 30 min | przykłady 2, 7, 10, 11, 15 jako omówienie |
| Ćwiczenie 2 (nowy wariant bez modyfikacji) | 40 min | przykłady 5 i 15 |
| Ćwiczenie 3 (obiektowy czy funkcyjny) | 30 min | przykłady 9 i 16 |
| Ćwiczenie 4 (karty problemów) | 25 min | mapa decyzji |
| Podsumowanie, lista kontrolna, sprawdzenie wiedzy | 5 min | slajdy końcowe |

Wykład z aktywnościami zajmuje ok. 110 minut, ćwiczenia z omówieniem ok. 125 minut. Przerwy nie są wliczone. Jeśli brakuje czasu, skróć demonstracje przykładów 3, 8 i 14 do samego uruchomienia klucza, a w Ćwiczeniu 4 omów tylko karty K1, K2, K8 i K10.
