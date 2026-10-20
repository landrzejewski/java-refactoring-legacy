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
# Moduł 4. Wzorce projektowe

Katalog 23 wzorców GoF: intencja, struktura, zastosowanie, koszt i wariant w nowoczesnej Javie

---

## Agenda

1. Definicja wzorca projektowego
2. Wzorce kreacyjne
3. Wzorce strukturalne
4. Wzorce behawioralne (z wariantem funkcyjnym)
5. Ćwiczenia, mapa decyzji, sprawdzenie wiedzy, lista kontrolna

Każdy wzorzec: **intencja, struktura, kiedy, koszt, powiązane wzorce, wariant w Javie 25, kod** (`Module4Examples <klucz>`). Ten moduł to katalog - jak dojść do wzorca bezpiecznymi krokami (i jak go usunąć), pokazuje moduł 5.

---

## 1.1. Wzorzec projektowy

- Odkrywany na bazie doświadczenia **szablon sprawdzonego rozwiązania** powszechnie występującego problemu.
- Koncepcja architekta Christophera Alexandra (język wzorców), przeniesiona m.in. do inżynierii oprogramowania.
- Katalog obiektowy: „Design Patterns: Elements of Reusable Object-Oriented Software” - Gamma, Helm, Johnson, Vlissides, tzw. banda czworga (GoF).
- Opis wzorca: **nazwa, problem i kontekst, rozwiązanie (role i współpraca), konsekwencje**.
- Wzorzec opisuje siły i kompromis - **nie jest kodem do skopiowania** ani biblioteką.
- Wspólny słownik skraca rozmowę („tu jest Strategy”), o ile obie strony mają na myśli tę samą intencję.

---

## 1.2. Klasyfikacja

| Kryterium | Grupa | Co opisuje |
| --- | --- | --- |
| rodzaj i rola | kreacyjne | proces tworzenia i konfigurowania nowych obiektów |
| | strukturalne | struktury obiektów i klas |
| | behawioralne (czynnościowe) | zachowanie i odpowiedzialność współpracujących obiektów |
| zakres | klasowe | statyczne związki między klasami (dziedziczenie, ustalane w kompilacji) |
| | obiektowe | dynamiczne zależności między obiektami (kompozycja, zmienne w czasie działania) |

Większość wzorców GoF jest obiektowa. Klasowe: **Factory Method, Interpreter, Template Method** oraz klasowy wariant Adaptera.

---

## 1.3. Katalog GoF - 23 wzorce

| Rodzaj | Klasowe | Obiektowe |
| --- | --- | --- |
| kreacyjne (5) | Factory Method | Abstract Factory, Builder, Prototype, Singleton |
| strukturalne (7) | Adapter (klasowy) | Adapter, Bridge, Composite, Decorator, Facade, Flyweight, Proxy |
| behawioralne (11) | Interpreter, Template Method | Chain of Responsibility, Command, Iterator, Mediator, Memento, Observer, State, Strategy, Visitor |

- Każdy wzorzec ma przykład w `pl.training.module4` (pakiety `creational`, `structural`, `behavioral`) i klucz w `Module4Examples`.
- Wybrane wzorce mają wariant funkcyjny w pakiecie `fn` (klucze `fn-*`).

---

## 1.4. Najpierw rodzaj zmienności

| Pytanie | Kierunek |
| --- | --- |
| Czy zmienia się algorytm wybierany dla operacji? | Strategy |
| Czy zachowanie zależy od bieżącego stanu i przejść? | State |
| Czy żądanie trzeba przechować, zakolejkować albo cofnąć? | Command |
| Czy kod jedynie wybiera i tworzy właściwy obiekt? | Factory Method, Abstract Factory |
| Czy dodatki owijają rdzeń w dowolnych kombinacjach? | Decorator |
| Czy wiele obiektów reaguje na zmianę jednego? | Observer |
| Czy mamy drzewo części i całości? | Composite |

Ten sam `switch` może sygnalizować różne problemy - najpierw ustal, **co naprawdę się zmienia** i czy zestaw wariantów jest otwarty, czy zamknięty.

---

## 1.5. Wzorzec ma koszt - i można go usunąć

- Przed użyciem: znany problem, **istniejące dziś** warianty, prostsza alternatywa, warunek, w którym wzorzec przestanie być potrzebny.
- Koszt: dodatkowe typy i pośredniość, trudniejsza nawigacja, kontrakt do utrzymania i testowania.
- Kierunek: zabezpiecz zachowanie → nazwij oś zmienności → odsłoń role → najwęższy kontrakt; **zatrzymaj się**, gdy problem jest rozwiązany.
- Sygnały nadmiaru: jedna implementacja od lat, fabryka z jednym `new`, implementacje rzucające `UnsupportedOperationException`, nowy wariant nadal wymaga zmian w wielu miejscach.
- Refaktoryzacja **od** wzorca jest równie poprawna jak **do** wzorca - sekwencje kroków w module 5.

---

## 1.6. Wzorce a język: Java 25

| Mechanizm | Co upraszcza |
| --- | --- |
| lambda, referencja do metody | jednometodowe role: Strategy, Command, fabryka (`Supplier`), krok szablonu, odbiorca zdarzeń |
| `Function.andThen` | łańcuch przekształceń: Decorator, potok |
| `enum` z ciałami stałych | zamknięty zestaw stanów lub strategii; Singleton (`INSTANCE`) |
| `record` | niemutowalne produkty, migawki Memento, klucze Flyweight |
| `sealed` + `switch` z dopasowaniem | zamknięta hierarchia: alternatywa dla Visitora i Interpretera |
| `Iterable`, `Stream` | iterator zewnętrzny i wewnętrzny |

Wzorzec nie znika - zmienia się **zapis roli**. Intencja i konsekwencje (kolejność, stan, tożsamość, błędy) zostają.

---

## Aktywność 1.7: nazwij problem, nie wzorzec (3 min)

```java
byte[] export(Report report, String type) {
    if (type.equals("PDF"))  { /* 40 linii */ }
    if (type.equals("CSV"))  { /* 25 linii */ }
    if (type.equals("XLSX")) { /* 60 linii */ }
    throw new IllegalArgumentException("unknown type: " + type);
}
```

1. Opisz problem jednym zdaniem **bez nazwy wzorca**.
2. Co się zmienia, kto wybiera wariant i jak często dochodzi nowy?
3. Dopiero teraz: jaki wzorzec, a może żaden?

---

## 2.1. Factory Method (klasowy, kreacyjny)

- **Intencja:** interfejs tworzenia obiektu bez wskazywania konkretnej klasy - typ produktu określa podklasa implementująca metodę wytwórczą.

```text
Creator.anOperation()            → product = factoryMethod()
ConcreteCreator.factoryMethod()  → return new ConcreteProduct()
Client zna Creator i Product, nie zna ConcreteProduct
```

- **Kiedy:** nie wiadomo, która implementacja będzie używana, i potrzebna jest jej elastyczna podmiana; łączenie równoległych hierarchii klas.
- **Konsekwencje:** niezależność od konkretnej implementacji; nowa podklasa twórcy na każdy nowy produkt.
- **Powiązane:** Abstract Factory (zbiór metod wytwórczych), Template Method (metoda wytwórcza jako krok), Prototype.
- **Java 25:** `Supplier<T>`, referencja do konstruktora `Square::new` (`fn-factory`).
- **Kod:** `creational.factorymethod` (`factory-method`).

---

## 2.2. Abstract Factory (obiektowy, kreacyjny)

- **Intencja:** interfejs tworzenia **rodzin** zależnych i powiązanych obiektów bez określania ich konkretnych klas.

```text
ConnectionAbstractFactory: createConnection(), createSecuredConnection()
FtpConnectionFactory  → FtpConnection (21),  SftpConnection (22)
HttpConnectionFactory → HttpConnection (80), HttpsConnection (443)
Service zna tylko fabrykę abstrakcyjną i interfejsy produktów
```

- **Kiedy:** system ma działać z jedną z kilku rodzin produktów; rodzina ma być używana **w całości** (nie mieszamy FTP z HTTPS).
- **Konsekwencje:** izolacja klientów, prosta podmiana rodziny, łatwa nowa rodzina - **trudny nowy rodzaj produktu** (zmiana interfejsu i wszystkich fabryk).
- **Powiązane:** Factory Method (realizacja metod), Singleton (jedna fabryka na rodzinę), Prototype.
- **Kod:** `creational.abstractfactory` (`abstract-factory`), test `usesTheRegisteredFtpControlPort`.

---

## 2.3. Fabryki w kodzie i ich koszt

```java
public interface IdGeneratorFactory { IdGenerator create(); }

public class ProductionIdGeneratorFactory implements IdGeneratorFactory {
    private final UuidGenerator uuidGenerator = new UuidGenerator();
    public IdGenerator create() { return uuidGenerator; }          // współdzielona
}
public class TestIdGeneratorFactory implements IdGeneratorFactory {
    public IdGenerator create() { return new IncrementalIdGenerator(); } // nowa
}
// Service.run(): idGeneratorFactory.create().getNext()
```

- Twórca jest tu **wstrzykiwanym obiektem**, nie podklasą `Service` - kompozycyjny wariant Factory Method.
- Dwie fabryki, dwa kontrakty: instancja współdzielona czy nowa przy każdym `create()`? Licznik testowy startuje od 1 przy każdym `run()`.
- Ryzyka: fabryka jako globalny rejestr usług; fabryka z jednym `new` (YAGNI); tworzenie kosztownych obiektów „na zapas”.

---

## 2.4. Builder - intencja i struktura

- **Intencja:** oddziela sposób tworzenia złożonego obiektu od jego reprezentacji - jeden proces konstrukcji może dać różne reprezentacje.

```text
Director ──▶ Builder: buildPart() ...    ConcreteBuilder.getResult() → Product
ConnectionUrlBuilder: host, port, protocol, database, encoding, build()
Generic | MySql | PostgresSql ConnectionUrlBuilder → ConnectionUrl
```

- **Kiedy:** algorytm tworzenia niezależny od składników i sposobu ich łączenia; obiekty w różnych konfiguracjach; wiele parametrów opcjonalnych.
- **Konsekwencje:** elastyczność wewnętrznej konfiguracji, kontrola nad procesem wytwórczym i odizolowanie go od reprezentacji; dodatkowy typ do utrzymania.
- **Powiązane:** Abstract Factory (rodzina naraz vs krok po kroku), Composite (budowa drzewa).
- **Java 25:** dla kilku pól wystarczy `record` z walidacją w konstruktorze kanonicznym albo statyczne fabryki z nazwami.

---

## 2.5. Builder - kod i pułapki

```java
var builder = new PostgresSqlConnectionUrlBuilder()
        .host("localhost")
        .database("test");
new Director(builder).run();
// INFO: Connection url: jdbc:postgres://localhost:5432/test?encoding=UTF-8
```

- Wartości domyślne ustawia konkretny builder: `MySql...` przez **dziedziczenie**, `PostgresSql...` przez **delegację** do `Generic...`.
- `build()` zwraca **tę samą, mutowalną** instancję `ConnectionUrl` - `host(...)` po `build()` zmienia już zbudowany produkt.
- Brak walidacji: bez hosta powstanie `jdbc:postgres://null:5432/...`. Builder to dobre miejsce na niezmienniki całości.
- `Director` tylko wywołuje `build()` - w GoF to on decyduje o kolejności kroków.
- Kierunek poprawy: niemutowalny produkt (`record`), kopia w `build()`, walidacja, builder jednorazowy.

---

## 2.6. Prototype (obiektowy, kreacyjny)

- **Intencja:** rodzaj obiektu określa instancja prototypu; nowe obiekty powstają przez jego **klonowanie**.

```text
Client ── p = prototype.clone() ──▶ Prototype.clone()
                                      ▲ ConcretePrototype1, ConcretePrototype2
```

- **Kiedy:** klasy dostarczane dynamicznie w czasie działania; instancja ma kilka typowych kombinacji stanu; unikamy hierarchii fabryk równoległej do hierarchii produktów.
- **Konsekwencje:** dodawanie i usuwanie produktów w czasie działania, nowe obiekty przez zmianę wartości, mniej klas niż przy Factory Method; decyzja: **kopia płytka czy głęboka**.
- **W kodzie:** `Window implements Cloneable`; pola są `final` i niemutowalne, więc klon niczego nie zyskuje względem współdzielenia instancji.
- **Java 25:** kontrakt `Cloneable` jest wadliwy (wyjątek kontrolowany, płytkie `super.clone()`) - wolimy konstruktor kopiujący, `copy()` albo rekord z metodami `withX(...)`.
- **Kod:** `creational.prototype` (`prototype`).

---

## 2.7. Singleton (obiektowy, kreacyjny)

- **Intencja:** tylko jedna instancja klasy i globalny punkt dostępu do niej.

```java
public synchronized static Logger getInstance() {
    if (LOGGER == null) { LOGGER = new Logger(); }
    return LOGGER;
}
```

- **Kiedy:** musi istnieć dokładnie jedna instancja, dostępna klientom w dobrze znany sposób.
- **Konsekwencje (GoF):** kontrola dostępu do jedynego egzemplarza, mniej zmiennych globalnych, więcej elastyczności niż operacje statyczne.
- **Koszt:** **ukryta zależność i globalny stan** - testy wpływają na siebie, trudna podmiana; `synchronized` przy każdym odczycie.
- **Alternatywy:** `enum INSTANCE` (jedna instancja na loader klas), idiom holder, `memoize(Supplier)` (`fn-singleton`), a najczęściej **jedna instancja tworzona w composition root i wstrzykiwana**.
- **Kod:** `creational.singleton` (`singleton`).

---

## Aktywność 2.8: konstruktor, fabryka czy builder? (3 min)

1. `Money(BigDecimal amount, Currency currency)` - dwa wymagane pola, jedna reguła: kwota nieujemna.
2. `HttpClientConfig` - 9 pól, 6 opcjonalnych; `proxyHost` wymaga `proxyPort`.
3. `Notifier` - zależnie od konfiguracji e-mail, SMS albo push; klient nie powinien znać klas.

**Zadanie:** dla każdego przypadku wskaż najprostszy mechanizm tworzenia i uzasadnij go jednym zdaniem.

---

## 3.1. Adapter - intencja i struktura

- **Intencja:** konwersja interfejsu klasy na taki, jakiego spodziewa się klient; współpraca klas niekompatybilnych pod względem interfejsów.

```text
Client ──▶ Target: temperatureUp(°C), temperatureDown(°C)
             ▲
           Adapter ──adaptee──▶ Adaptee: changeTemperature(delta °F)
```

- **Kiedy:** istniejąca klasa ma niepasujący interfejs; reużywalna klasa ma współpracować z niepowiązanymi klasami; dopasowanie przez podklasy jest niepraktyczne.
- **Warianty:** obiektowy (kompozycja, jak w kodzie) i klasowy (dziedziczenie po klasie adaptowanej).
- **Konsekwencje:** użycie niekompatybilnych komponentów, opakowanie istniejącego interfejsu w nowy; utrudnione przesłanianie zachowań klasy adaptowanej.
- **Powiązane:** Bridge (projektowany z góry, Adapter po fakcie), Decorator (ten sam interfejs), Facade (nowy, prostszy interfejs), Proxy.

---

## 3.2. Adapter - kod i semantyka

```java
public class TemperatureControllerAdapter implements TemperatureController {
    private final AirConditioningController controller;
    public void temperatureUp(double value) {
        controller.changeTemperature(celsiusDeltaToFahrenheit(value));
    }
    public void temperatureDown(double value) {
        controller.changeTemperature(-celsiusDeltaToFahrenheit(value));
    }
    private double celsiusDeltaToFahrenheit(double value) { return value * 1.8; }
}
```

- Przelicza **różnicę** temperatur: `× 1.8` bez `+ 32` (test `convertsTemperatureDifferencesWithoutAnAbsoluteScaleOffset`: 10 °C → 18.0 °F).
- Podobna sygnatura to nie podobny kontrakt: jednostki, znak, `null`, błędy, własność zasobów.
- Adapter tłumaczy, ale **nie dodaje logiki domenowej**. Adapter na granicy systemu a Anti-Corruption Layer - moduł 5.
- **Kod:** `structural.adapter` (`adapter`).

---

## 3.3. Bridge (obiektowy, strukturalny)

- **Intencja:** oddziela abstrakcję od implementacji tak, aby mogły być **niezależnie** modyfikowane i rozszerzane.

```text
Message (abstrakcja) ──sender──▶ MessageSender (implementacja)
  ▲ TextMessage, UrgentMessage      ▲ EmailSender, SmsSender
2 rodzaje + 2 kanały = 4 klasy dla 4 kombinacji; przy 3 × 3: 6 klas zamiast 9
```

- **Kiedy:** obie hierarchie rozwijają się niezależnie; implementację wybieramy w czasie działania; zmiana implementacji nie może dotykać klientów.
- **Konsekwencje:** rozdzielenie interfejsu abstrakcji od implementacji, rozszerzalność, izolacja klientów; koszt - dwie hierarchie i pośredniość od początku.
- **Powiązane:** Adapter (po fakcie), Strategy (implementacja wstrzykiwana jak strategia), Abstract Factory (tworzy pasujący most).
- **Java 25:** `MessageSender` ma jedną metodę - kanał może być lambdą.
- **Kod:** `structural.bridge` (`bridge`), test `bridgeCombinesEveryMessageKindWithEverySender`.

---

## 3.4. Composite - intencja i struktura

- **Intencja:** kompozycja obiektów w strukturę drzewiastą; obiekty indywidualne i złożone traktowane **w jednakowy sposób**.

```text
Client ──▶ Component: operation(), add(), remove(), getChild()
             ▲                    ▲
           Leaf              Composite ──children──▶ Component*
                             operation(): for (c : children) c.operation()
```

- **Kiedy:** hierarchia wzajemnie zagnieżdżonych elementów (liści i kompozytów), które mają być obsługiwane tym samym kodem.
- **Konsekwencje:** definiowanie i zarządzanie hierarchiami obiektów pierwotnych i złożonych, łatwe operacje na fragmentach, prostszy kod klienta.
- **Koszt:** ogólny `Component` utrudnia ograniczenie, kto może być dzieckiem; cykle, głębokość rekurencji, kolejność dzieci.
- **Powiązane:** Iterator (przejście), Visitor (operacje), Decorator (jedno dziecko), Builder (budowa), Flyweight (współdzielone liście).

---

## 3.5. Composite - wariant przezroczysty i bezpieczny

```java
public abstract class Node {
    protected final String name;
    protected List<Node> children = new ArrayList<>();
    public void addChild(Node node) { children.add(node); }
    public abstract void printInfo();
}
public class Employee extends Node {
    @Override public void addChild(Node node) { throw new UnsupportedOperationException(); }
    @Override public void printInfo() { System.out.println(" - Employee " + name); }
}
```

- **Przezroczysty** (jak tu): zarządzanie dziećmi we wspólnym typie - jednolitość kosztem wyjątku na liściu (napięcie z LSP).
- **Bezpieczny:** `addChild` tylko w kompozycie - pilnuje kompilator, ale klient czasem musi rozróżniać typy.
- Publiczny `setChildren` i chroniona, mutowalna lista osłabiają hermetyzację drzewa.
- **Java 25:** `sealed interface` + rekordy z `List.copyOf`. **Kod:** `structural.composite` (`composite`).

---

## 3.6. Decorator - intencja i struktura

- **Intencja:** dynamiczne dodawanie odpowiedzialności do obiektu; elastyczna alternatywa dla dziedziczenia.

```text
Component.operation() ◀──component── Decorator.operation(): component.operation()
  ▲ ConcreteComponent                   ▲ ConcreteDecoratorA, B: dodatek + delegacja
Reader ◀── ReaderDecorator ◀── LowerCaseReaderDecorator, UnderscoreReaderDecorator
```

- **Kiedy:** nowe odpowiedzialności mają być dodawane dynamicznie i przezroczyście, bez zmian w pozostałym kodzie; dziedziczenie dałoby ogromną liczbę podklas.
- **Konsekwencje:** większa elastyczność niż dziedziczenie, dynamiczna modyfikacja zachowań, brak przeładowanych klas; wiele małych obiektów, trudniejsze debugowanie.
- **Powiązane:** Proxy (ta sama struktura, inna intencja), Adapter (zmienia interfejs), Composite (wiele dzieci), Chain of Responsibility, Strategy (zmienia „wnętrze”, dekorator - „skórę”).

---

## 3.7. Decorator - kod i kolejność

```java
Reader reader = new LowerCaseReaderDecorator(
        new UnderscoreReaderDecorator(new SystemInReader()));
reader.getText();  // "Hello Decorator Pattern" → "hello_decorator_pattern"

public String getText() {                    // UnderscoreReaderDecorator
    return super.getText().replaceAll("\\s", "_");
}
```

- Najpierw działa dekorator **najbliżej rdzenia** (podkreślenia), potem zewnętrzny (małe litery).
- **Kolejność to zachowanie:** `audit(retry(core))` zapisze jedną operację, `retry(audit(core))` każdą próbę.
- Dekorator nie jest przezroczysty dla tożsamości, `getClass()` i `equals`.
- **Funkcyjnie:** `base.andThen(trim).andThen(upper)` - kolejność **od lewej do prawej** (`fn-decorator`).
- **Kod:** `structural.decorator` (`decorator`; czyta linię ze standardowego wejścia, przy pustym używa tekstu domyślnego).

---

## 3.8. Facade (obiektowy, strukturalny)

- **Intencja:** jeden, zunifikowany interfejs wyższego poziomu dla zbioru interfejsów podsystemów, ułatwiający korzystanie z systemu.

```text
Client ──▶ Facade.run() ──▶ FirstService.run()    INFO: Step 1
                        └─▶ SecondService.run()   INFO: Step 2
```

- **Kiedy:** prosty interfejs dla złożonego podsystemu; wiele zależności między klientami a podsystemami; centralny punkt wejścia.
- **Konsekwencje:** izolacja klientów od podsystemów i szczegółów niskiego poziomu, niskie sprzężenie; podsystem nadal dostępny bezpośrednio.
- **Ryzyka:** fasada-bóg rosnąca o każdą operację; fasada przekazująca wywołania 1:1 niczego nie upraszcza.
- **Powiązane:** Mediator (koordynuje w obie strony), Adapter, Abstract Factory, Singleton. Facade a Anti-Corruption Layer - moduł 5.
- **Kod:** `structural.facade` (`facade`).

---

## 3.9. Flyweight (obiektowy, strukturalny)

- **Intencja:** wydajna obsługa dużej liczby małych obiektów przez **współdzielenie** stanu wewnętrznego.

```java
public static TreeType getTreeType(String name, String color) {
    return TYPES.computeIfAbsent(name + ":" + color, key -> new TreeType(name, color));
}
// Tree(x, y, type): x, y - stan zewnętrzny; TreeType - wewnętrzny, współdzielony
```

- **Kiedy:** bardzo dużo obiektów, wysoki koszt ich przechowywania, większość stanu da się wydzielić; aplikacja **nie zależy od tożsamości** obiektów.
- **Konsekwencje:** oszczędność zależna od liczby obiektów i rozmiaru współdzielonego stanu; stan współdzielony musi być niemutowalny.
- **W kodzie:** statyczna `HashMap` bez synchronizacji i bez usuwania; klucz z konkatenacji - `("a:b", "c")` i `("a", "b:c")` dają ten sam klucz. Lepszy klucz: `record`.
- **JDK:** `Integer.valueOf` (pamięć podręczna -128..127). **Kod:** `structural.flyweight` (`flyweight`).

---

## 3.10. Proxy (obiektowy, strukturalny)

- **Intencja:** pośrednik dla obiektu, który **kontroluje dostęp** do niego.

```text
Client ──▶ Subject.pay() ◀── RealSubject (RemotePaymentsService)
               ▲
             Proxy ──realSubject──▶ ...; realSubject.pay(); ...
PaymentLoggerProxy(PaymentSecurityProxy(RemotePaymentsService))
```

- **Kiedy:** dostęp zdalny, wirtualny (leniwe tworzenie), ochronny (uprawnienia), inteligentny; izolacja klienta od zbędnej złożoności.
- **Konsekwencje:** optymalizacja, ukrycie złożoności mechanizmów, kontrola dostępu, dynamiczne wzbogacanie funkcjonalności.
- **W kodzie:** `PaymentSecurityProxy` tylko loguje - prawdziwy proxy ochronny może **odmówić** delegacji; `PaymentLoggerProxy` loguje tylko po sukcesie.
- **Java:** `java.lang.reflect.Proxy`, proxy frameworków (transakcje, leniwe encje) - wywołanie wewnątrz tej samej klasy omija proxy.
- **Kod:** `structural.proxy` (`proxy`).

---

## 3.11. Rodzina opakowań: ta sama struktura, różne intencje

| Wzorzec | Interfejs wobec opakowanego obiektu | Intencja |
| --- | --- | --- |
| Adapter | **inny** | dopasować do oczekiwań klienta |
| Decorator | **ten sam** | dodać odpowiedzialność, składać warstwy |
| Proxy | **ten sam** | kontrolować dostęp, opóźnić, wywołać zdalnie |
| Facade | **nowy, prostszy**, nad wieloma obiektami | uprościć użycie podsystemu |
| Bridge | abstrakcja deleguje do implementacji | rozwijać dwie hierarchie niezależnie |

Strukturalnie to często „klasa z polem i delegacją” - **o wzorcu decyduje intencja**, nie diagram ani nazwa klasy.

---

## Aktywność 3.12: rozpoznaj opakowanie (4 min)

```java
class A implements PaymentsService {
    public void pay(Map<String, String> p) {
        if (!user.isAdmin()) throw new SecurityException("forbidden");
        target.pay(p);
    }
}
class B implements PaymentsService {
    public void pay(Map<String, String> p) { metrics.time("pay", () -> target.pay(p)); }
}
class C implements PaymentsService {
    public void pay(Map<String, String> p) { gateway.charge(p.get("amount"), "PLN"); }
}
```

1. Adapter, Decorator czy Proxy? Co rozstrzyga - kod czy intencja?
2. Czym staje się B, jeśli zacznie odrzucać płatności w oknie serwisowym?

---

## 4.1. Chain of Responsibility (obiektowy, behawioralny)

- **Intencja:** odseparowanie nadawcy od odbiorcy; żądanie przechodzi przez łańcuch obiektów, z których każdy może je obsłużyć lub przekazać dalej.

```java
var chain = new Validator(new Logger(new Processor()));
chain.handleRequest("Test");      // INFO: Test, INFO: Processing: Test

public void handleRequest(String request) {   // Validator
    if (request.length() < 3) { return; }      // przerwanie łańcucha
    nextHandler.handleRequest(request);
}
```

- **Kiedy:** wielu możliwych odbiorców, zbiór ustalany dynamicznie. **Warianty:** „pierwszy obsługujący kończy” albo potok filtrów.
- **Konsekwencje:** niskie sprzężenie nadawcy i odbiorcy; **brak gwarancji odbioru** - `"Te"` znika bez śladu.
- **Powiązane:** Decorator, Composite, Command. **Kod:** `behavioral.chainofresponsibility` (`chain-of-responsibility`).

---

## 4.2. Command - intencja i struktura

- **Intencja:** hermetyzuje żądanie w formie obiektu; różne operacje wykonywane w taki sam sposób; możliwość cofania kroków.

```text
Client ──tworzy──▶ ConcreteCommand(receiver, state)
Invoker ──▶ Command.execute()     ConcreteCommand.execute(): receiver.action()
Invoker: register(cmd), invoke(cmd), invokeAll() nad Queue<Command>
```

- **Kiedy:** potrzebny mechanizm typu callback; zadania trzeba kolejkować i wywoływać w odpowiednim momencie; rejestrowanie, powtarzanie i cofanie zmian.
- **Konsekwencje:** separacja obiektu wywołującego od obiektu, który wie, jak wykonać operację; przekazywanie komend, łatwe dodawanie nowych, makra.
- **Powiązane:** Memento (stan do cofnięcia), Strategy (jak liczyć vs co i kiedy wykonać), Composite (makro), Chain of Responsibility.

---

## 4.3. Command - kod, kolejka i undo

```java
public interface Command { void execute(); }

var invoker = new Invoker();
invoker.invoke(new PrintTime());        // INFO: 2026-10-20T13:47:34.438274
invoker.invoke(new ConnectToServer());  // INFO: Connecting... / INFO: Connected...
```

- Komendy same wykonują pracę - brak roli **Receiver**; `register` i `invokeAll` istnieją, ale demo ich nie używa.
- Undo wymaga drugiej operacji (`undo()`) albo migawki (Memento) - samo `execute()` go nie daje.
- Command nie daje automatycznie asynchroniczności ani retry; komenda współdzielona powinna być bezstanowa, dane żądania to argument.
- **Java 25:** `Runnable`, `Consumer<T>` (`fn-command`); klasa wraca, gdy komenda ma nazwę, metadane, `undo` lub jest utrwalana.
- **Kod:** `behavioral.command` (`command`).

---

## 4.4. Interpreter (klasowy, behawioralny)

- **Intencja:** reprezentacja gramatyki języka oraz interpreter, który na jej podstawie interpretuje zdania tego języka.

```java
// 2a * 3 + 1, gdzie a = 4
var expression = new Operation(
        new Operation(new Operation(new Literal(2), new Variable("a"), op("*")),
                new Literal(3), op("*")),
        new Literal(1), op("+"));
expression.evaluate(Map.of("a", 4.0));   // 25.0
```

- **Struktura:** `Expression.evaluate(context)`; terminalne `Literal`, `Variable`; nieterminalne `Operation(left, right, BiOperator)`.
- **Kiedy:** gramatyka jest prosta, a wydajność nie jest najważniejsza. **Konsekwencje:** łatwe rozszerzanie gramatyki; trudne zarządzanie złożoną gramatyką.
- Brak parsera - drzewo budowane ręcznie; brak zmiennej w kontekście kończy się `NullPointerException`.
- **Powiązane:** Composite (drzewo składni), Visitor, Flyweight (`op(...)` współdzieli operatory). **Kod:** `behavioral.interpreter` (`interpreter`).

---

## 4.5. Iterator (obiektowy, behawioralny)

- **Intencja:** sekwencyjny dostęp do elementów złożonego obiektu niezależnie od jego implementacji i wewnętrznej struktury.

```java
public Iterator<Song> iterator() { return new PlaylistIterator(0, 1); }
public Iterable<Song> reversed()  { return () -> new PlaylistIterator(size - 1, -1); }

for (var song : playlist.reversed()) { System.out.println("Reversed: " + song.title()); }
```

- **Kiedy:** dostęp bez ujawniania struktury (`Playlist` trzyma tablicę); jednolity interfejs dla różnych agregatów; kilka sposobów przejścia.
- **Konsekwencje:** spójny sposób poruszania się po agregatach; wiele niezależnych kursorów naraz.
- Brak wykrywania modyfikacji w trakcie przejścia (kolekcje JDK są fail-fast dzięki `modCount`).
- Iterator **zewnętrzny** (klient steruje, może przerwać) vs **wewnętrzny** (`forEach`, jednorazowy `Stream` - `fn-iterator`).
- **Kod:** `behavioral.iterator` (`iterator`), testy `iteratorDemoWalksThePlaylistForwardAndBackward`, `playlistIteratorIsExhaustedAfterTheLastSong`.

---

## 4.6. Mediator (obiektowy, behawioralny)

- **Intencja:** luźne powiązanie współpracujących obiektów - mediator bierze na siebie komunikację między nimi; interakcje można zmieniać niezależnie.

```text
Participant (colleague) ──mediator──▶ ChatMediator: join(), broadcast()
ChatRoom (ConcreteMediator) ──▶ wszyscy uczestnicy poza nadawcą
alice.send("Hello everyone") → Bob, Carol: received from Alice
```

- **Kiedy:** obiekty komunikują się w dobrze zdefiniowany, ale skomplikowany sposób; powiązania „każdy z każdym” utrudniają modyfikację.
- **Konsekwencje:** rozdzielenie współpracowników, prostszy protokół (n powiązań zamiast n²), centralizacja sterowania - ryzyko **mediatora-boga**.
- **W kodzie:** `Participant` rejestruje się w konstruktorze (wyciek `this`), brak wyjścia z pokoju.
- **Powiązane:** Observer (mediator często opiera się na zdarzeniach), Facade (jednokierunkowa).
- **Kod:** `behavioral.mediator` (`mediator`), test `mediatorDeliversMessagesToEveryoneExceptTheSender`.

---

## 4.7. Memento (obiektowy, behawioralny)

- **Intencja:** rejestruje i zapisuje wewnętrzny stan obiektu na zewnątrz **bez naruszenia kapsułkowania**; umożliwia przywrócenie tego stanu.

```java
var account = new Account(UUID.randomUUID());
var caretaker = List.of(account.createMemento());   // migawka: balance = 0
account.deposit(BigDecimal.TEN);                     // Deposited balance: 10
account.restoreMemento(caretaker.getFirst());        // Restored balance: 0
```

- **Role:** Originator (`Account`), Memento, Caretaker (lista w `Application`).
- Konstruktor i `balance()` w `Memento` są **pakietowe** (`memento.accounts`) - opiekun przechowuje migawkę, ale jej nie odczyta.
- **Konsekwencje:** zachowanie hermetyzacji, przywracanie stanu; potencjalnie duże zużycie zasobów; mutowalne pola wymagają kopii głębokiej.
- **Powiązane:** Command (undo), Iterator, Prototype. **Java 25:** rekord jako migawka, domknięcie `Supplier` (`fn-memento`).
- **Kod:** `behavioral.memento` (`memento`), test `restoresTheBalanceRecordedInTheMemento`.

---

## 4.8. Observer - intencja i struktura

- **Intencja:** zależność jeden do wielu - gdy zmienia się stan jednego obiektu, wszystkie obiekty zależne są informowane w ustandaryzowany sposób.

```text
Subject: attach(o), detach(o), notify() { for (o : observers) o.update(); }
ConcreteObserver.update(): stan z ConcreteSubject (pull) albo z danych zdarzenia (push)
EventsBus: addConsumer(Consumer<ServerEvent>), publish(ServerEvent)
```

- **Kiedy:** zmiana stanu jednego obiektu ma wywołać działanie w innych, **nie wiadomo ilu i jakich**; dwa aspekty abstrakcji łatwiej zmieniać w oddzielnych obiektach.
- **Konsekwencje:** grupowe rozsyłanie komunikatów, luźne powiązanie obiektów; przepływ sterowania mniej widoczny w kodzie.
- **Powiązane:** Mediator, Command; szyna zdarzeń i strumienie reaktywne jako warianty.

---

## 4.9. Observer - kod i kontrakt powiadomień

```java
class EventsBus {
    private final Set<Consumer<ServerEvent>> consumers = synchronizedSet(new HashSet<>());
    void addConsumer(Consumer<ServerEvent> consumer) { consumers.add(consumer); }
    void publish(ServerEvent event) {
        consumers.forEach(consumer -> consumer.accept(event));
    }
}
```

- `HashSet`: **kolejność powiadomień nieokreślona**; drugi `add` tego samego obiektu nic nie zmienia.
- `forEach` na `synchronizedSet` wywołuje odbiorców **pod blokadą** - wolny odbiorca blokuje publikację i rejestrację.
- Brak `detach`: odbiorca żyje tak długo jak szyna; lambda nie ma stabilnej tożsamości do wyrejestrowania.
- Do ustalenia: wątek, kolejność, polityka wyjątków (tu pierwszy wyjątek przerywa pętlę), wyrejestrowanie w trakcie publikacji.
- **Kod:** `behavioral.observer` (`observer`), `fn-observer`.

---

## 4.10. State - intencja i struktura

- **Intencja:** zmiana zachowania obiektu w zależności od jego aktualnego stanu - wygląda to tak, jakby obiekt zmienił swoją klasę.

```text
Context (Order) ──state──▶ State: pay(), ship(), cancel()
Order.pay(): state = state.pay()     ConcreteState zwraca następny stan
NEW --pay--> PAID --ship--> SHIPPED;  NEW, PAID --cancel--> CANCELLED
```

- **Kiedy:** zachowanie obiektu zależy od jego stanu; operacje są długie i składają się z wielu instrukcji warunkowych po polu statusu.
- **Konsekwencje:** separacja zachowania i stanu, współużytkowanie obiektów stanu, **jawna zmiana stanu**; więcej typów, tabela przejść rozproszona po klasach.
- **Powiązane:** Strategy (ta sama struktura - kto zmienia wariant?), Flyweight i Singleton (bezstanowe, współdzielone stany).

---

## 4.11. State - kod: stany jako enum

```java
public interface OrderState {
    default OrderState pay() {
        throw new IllegalStateException("Payment is not allowed in state " + this);
    }   // ship(), cancel() - analogicznie
}
public enum OrderStatus implements OrderState {
    NEW  { public OrderState pay()  { return PAID; }    /* cancel() → CANCELLED */ },
    PAID { public OrderState ship() { return SHIPPED; } /* cancel() → CANCELLED */ },
    SHIPPED, CANCELLED
}
```

- Domyślnie **wyjątek**, nie pusta metoda - niedozwolona operacja nie zmienia stanu.
- Stany to bezstanowe stałe; dane zamówienia zostają w `Order`. Dla dwóch stanów `switch` bywa czytelniejszy.
- **Kod:** `behavioral.state` (`state`), test `orderStateMovesThroughItsLifecycleAndRejectsInvalidTransitions`; funkcyjnie `fn-state`.

---

## 4.12. Strategy - intencja i struktura

- **Intencja:** rodzina algorytmów rozwiązujących ten sam problem, wymiennych w zależności od potrzeby; różne algorytmy dla różnych klientów.

```text
Context ──strategy──▶ Strategy.algorithmInterface()
                        ▲ ConcreteStrategyA, B, C
Service(IdGenerator) ──▶ UuidGenerator | IncrementalIdGenerator
```

- **Kiedy:** powiązane klasy różnią się tylko zachowaniem; potrzebne różne implementacje jednego algorytmu; ukrycie złożoności algorytmu i jego danych; wiele zachowań w złożonych warunkach.
- **Konsekwencje:** algorytmy zgrupowane w rodziny, łatwa podmiana, niskie sprzężenie, eliminacja instrukcji warunkowych; klient musi znać różnice, by wybrać.
- **Powiązane:** State (kto i kiedy zmienia wariant), Template Method (dziedziczenie vs kompozycja), Bridge, Flyweight (bezstanowe strategie).

---

## 4.13. Strategy - kod: obiekt, enum, lambda

```java
new Service(new UuidGenerator()).run();           // Id: 6eae08aa-784f-...
new Service(new IncrementalIdGenerator()).run();  // Id: 00000000000000000001

public enum MovieType {                           // strategia jako dane
    REGULAR(2, 2, 1.5), CHILDREN(3, 1.5, 1.5), NEW_RELEASE(0, 0, 3);
    // getValueFor(days) = initialCost + płatne dni × costPerDay
}
Function<String, String> reverse = s -> new StringBuilder(s).reverse().toString();
```

- Wybór strategii w konstruktorze **zamraża** decyzję na czas życia kontekstu.
- `IncrementalIdGenerator` ma stan (licznik bez synchronizacji) - nie współdziel go między wątkami.
- Enum z parametrami: zamknięty zestaw wariantów różniących się **danymi** (`movies`, test `doesNotSubtractAChargeDuringTheFreeRentalPeriod`).
- Lambda (`fn-strategy`) wystarcza dla jednej metody bez stanu. **Kod:** `behavioral.strategy` (`strategy`).

---

## 4.14. Template Method (klasowy, behawioralny)

- **Intencja:** szkielet algorytmu w klasie bazowej, kroki delegowane do podklas - zmienne kroki, stała sekwencja.

```java
public abstract class Processor<V, PV> {
    public final void run() {      // final: sekwencji nie da się nadpisać
        var data = read();
        var result = process(data);
        write(result);
    }
    protected abstract V read();
    protected abstract PV process(V data);
    protected abstract void write(PV data);
}
```

- **Kiedy:** niezmienna część algorytmu raz, zmienne kroki w podklasach; kontrola rozszerzalności klas.
- **Konsekwencje:** redukcja duplikacji, **odwrócenie sterowania**, niezmienność kroków; kruche hooki.
- **Kod:** `behavioral.templatemethod` (`template-method`); funkcyjnie: `fn-template`.

---

## 4.15. Visitor - intencja i struktura

- **Intencja:** reprezentuje operacje wykonywane na elementach struktury obiektów; nowe operacje **bez zmian w klasach elementów**.

```text
Element.accept(Visitor v)                  ConcreteElementA.accept(v): v.visit(this)
Visitor: visit(ElementA), visit(ElementB)   nowa operacja = nowa klasa gościa
Node ▲ Department, SuperDepartment, Employee      goście: NameValidator, Reporter
```

- **Kiedy:** struktura obejmuje wiele klas, operacje zależą od typu elementu; wiele niepowiązanych operacji bez zaśmiecania klas; klasy struktury **rzadko** się zmieniają.
- **Konsekwencje:** łatwe dodawanie i grupowanie operacji, odwiedzanie różnych hierarchii jednym gościem; **trudne dodawanie nowych elementów**.
- **Mechanizm:** podwójna dyspozycja - typ elementu wybiera `accept`, przeciążenie `visit(this)` wybiera metodę gościa.
- **Powiązane:** Composite (struktura), Interpreter, Iterator (przejście).

---

## 4.16. Visitor - kod i pułapka metod domyślnych

```java
public interface Visitor {
    default void visit(Department department) { }
    default void visit(SuperDepartment department) { }
    default void visit(Employee employee) { }
}
public class Department extends Node {
    public void accept(Visitor visitor) {
        visitor.visit(this);     // przeciążenie wybrane w kompilacji
        super.accept(visitor);   // potem dzieci
    }
}
```

- Puste metody domyślne: gość nadpisuje tylko to, czego potrzebuje, ale kompilator **nie wymusi** obsługi nowego typu.
- `Reporter` nie nadpisuje `visit(SuperDepartment)` - taki dział **znika z raportu** (pracownicy zostają).
- **Java 25:** `sealed` + `switch` z dopasowaniem sprawdza wyczerpująco. **Kod:** `behavioral.visitor` (`visitor`).

---

## 4.17. Podobna struktura, różna intencja

| Para | Co je odróżnia |
| --- | --- |
| Strategy / State | strategię wybiera klient; stan zmienia sam obiekt w wyniku operacji |
| Strategy / Command | jak policzyć vs co wykonać, kiedy i czy cofnąć |
| Strategy / Template Method | kompozycja (w czasie działania) vs dziedziczenie (w kompilacji) |
| Chain of Responsibility / Decorator | ogniwo może przerwać; dekorator zawsze deleguje |
| Visitor / metoda polimorficzna | łatwe nowe operacje vs łatwe nowe typy |
| Facade / Mediator | fasada upraszcza dostęp; mediator koordynuje współpracowników |

Szczegółowe porównania oraz refaktoryzacje do tych wzorców i od nich - moduł 5.

---

## Aktywność 4.18: Strategy czy State? (3 min)

1. Sposób zaokrąglania ceny wybierany w konfiguracji sklepu.
2. Bilet: ZAREZERWOWANY → OPŁACONY → WYKORZYSTANY; zwrot tylko przed wykorzystaniem.
3. Algorytm kompresji wybierany przez użytkownika przy każdym eksporcie.
4. Abonament: próbny → płatny → zawieszony; w każdym inny limit pobrań.

**Zadanie:** kto zmienia wariant i kiedy? Czy istnieje tabela przejść?

---

## 4.19. Wariant funkcyjny (1/2): rola jako funkcja

| Wzorzec | Wariant obiektowy | Wariant funkcyjny (`fn`) |
| --- | --- | --- |
| Strategy | `IdGenerator`, `UuidGenerator` | `Function<String, String>` |
| Command | `Command`, `Invoker` | `Runnable` |
| Factory | `IdGeneratorFactory` | `Supplier<Shape>`, `Square::new` |
| Decorator | `ReaderDecorator` i podklasy | `base.andThen(trim).andThen(upper)` |
| Template Method | `Processor.run()` + podklasa | `process(Consumer<String> step)` |
| Observer | `EventsBus` | `List<Consumer<String>>` |
| State | `OrderStatus` | lambdy `LOCKED`, `UNLOCKED` |
| Memento, Singleton | `Memento`, `Logger.getInstance()` | domknięcie `Supplier`, `memoize(Supplier)` |

Każdy klucz `fn-*` w `Module4Examples` ma obiektowy odpowiednik w katalogu.

---

## 4.20. Wariant funkcyjny (2/2): co tracimy

- **Semantyka:** `fn-chain` (`step1.andThen(step2)`) wykonuje **każdy** krok - nie przerwie jak `Validator`; to potok, nie Chain of Responsibility.
- **Tożsamość:** lambda nie ma stabilnej tożsamości - nie wyrejestrujesz „tej samej” lambdy z listy odbiorców.
- **Nazwa i kontrakt:** `Function<String, String>` nie mówi, co obiecuje; własny interfejs funkcyjny niesie nazwę domenową.
- **Stan i wyjątki:** stanowa strategia potrzebuje domknięcia nad zmiennym obiektem; `Function` nie deklaruje wyjątków kontrolowanych.
- **Rozszerzalność:** druga operacja (`undo`, `describe`) wymusza powrót do klasy.
- **Diagnostyka:** ślady stosu z `lambda$main$0` są mniej czytelne niż nazwa klasy.

Funkcja dla **jednej, bezstanowej** roli; klasa, gdy rola ma nazwę, stan, kilka operacji lub cykl życia.

---

## Ćwiczenie 1: rozpoznawanie wzorców w kodzie (20 + 10 min)

- Cel: rozpoznać wzorzec po **intencji**, nie po nazwie pakietu - w kodzie `pl.training.module4` i w JDK.
- Dla 12 fragmentów: wzorzec (lub kilka), role z katalogu, intencja jednym zdaniem, koszt lub ryzyko widoczne w kodzie.
- Szukaj wzorców „ukrytych”: builder w `ServerEvent`, Flyweight w interpreterze, proxy, które jest dekoratorem.

Szczegóły: zadania modułu 4, Ćwiczenie 1.

---

## Ćwiczenie 2: nowy wariant bez modyfikacji (30 + 10 min)

- Cel: dodać kanał `PushSender` i rodzaj `SignedMessage` w Bridge oraz operację `EmployeeCounter` w Visitor - **bez zmiany** istniejących klas.
- Testy z własnym obiektem zastępczym `MessageSender`; drzewo z `SuperDepartment`.
- Na koniec: które pliki trzeba by zmienić, dodając nowy **rodzaj węzła**?

Szczegóły: zadania modułu 4, Ćwiczenie 2.

---

## Ćwiczenie 3: wariant obiektowy czy funkcyjny? (20 + 10 min)

- Cel: porównać trzy pary z katalogu i pakietu `fn` (nazwa, stan, tożsamość, rozszerzalność, testowalność, semantyka).
- Usuń różnicę semantyczną `fn-chain`: łańcuch funkcji, który potrafi przerwać przetwarzanie jak `Validator`.
- Decyzja: kiedy w waszym kodzie zostaje klasa, a kiedy wystarcza lambda?

Szczegóły: zadania modułu 4, Ćwiczenie 3.

---

## Ćwiczenie 4: dobór wzorca - karty problemów (15 + 10 min)

- Cel: dla 10 opisanych problemów wskazać wzorzec, prostszą alternatywę i główny koszt.
- Uzasadnienie zaczyna się od **rodzaju zmienności**, nie od nazwy wzorca.
- Zawsze rozważ odpowiedź „bez wzorca”.

Szczegóły: zadania modułu 4, Ćwiczenie 4.

---

## Kluczowe wnioski z omówienia

- `PaymentLoggerProxy` jest strukturalnie dekoratorem - nazwa klasy nie przesądza o wzorcu.
- Nowy kanał w Bridge i nowa operacja w Visitor nie wymagają zmian istniejących klas; nowy rodzaj węzła wymaga zmiany `Visitor`.
- Puste metody domyślne w `Visitor` ukrywają brak obsługi `SuperDepartment` - kompilator nie pomoże.
- `fn-chain` nie jest Chain of Responsibility - `andThen` nie przerywa; potrzebny `Optional` lub jawny warunek.
- Command z `undo` i Observer z wyrejestrowaniem wracają do klas; bezstanowa Strategy zostaje lambdą.
- Część kart problemów najlepiej rozwiązuje zwykły `if`, `enum` lub `record`.

Pełne omówienie: rozwiązania modułu 4.

---

## Mapa decyzji (1/2)

| Sytuacja | Rozważ | Uważaj na |
| --- | --- | --- |
| tworzenie ukryte za abstrakcją | Factory Method | fabryka z jednym `new` |
| spójne rodziny produktów | Abstract Factory | koszt nowego rodzaju produktu |
| wiele parametrów, niezmienniki całości | Builder | mutowalny produkt, ponowne użycie |
| obcy interfejs | Adapter | jednostki, `null`, błędy |
| dwie niezależne osie hierarchii | Bridge | pośredniość od początku |
| drzewo część-całość | Composite | cykle, liść z `add` |
| dodatki składane wokół rdzenia | Decorator | kolejność, tożsamość |
| kontrola dostępu, leniwe tworzenie | Proxy | wywołania omijające proxy |

---

## Mapa decyzji (2/2)

| Sytuacja | Rozważ | Uważaj na |
| --- | --- | --- |
| wymienny algorytm wybierany przez klienta | Strategy | moment wyboru, stan strategii |
| zachowanie zależne od stanu i przejść | State | tabela przejść, domyślny wyjątek |
| żądanie do kolejkowania lub cofania | Command | model undo, stan komendy |
| wielu odbiorców zmiany | Observer | kolejność, wyjątki, wyrejestrowanie |
| sieć wzajemnych powiązań | Mediator | mediator-bóg |
| stała sekwencja zmiennych kroków | Template Method | kruche hooki |
| nowe operacje na stabilnych typach | Visitor | koszt nowego typu, metody domyślne |
| prosty, stabilny warunek | bez wzorca | „wzorzec na zapas” |

---

## Sprawdzenie wiedzy

1. Z jakich elementów składa się opis wzorca i dlaczego wzorzec to nie gotowy kod?
2. Czym różnią się wzorce klasowe od obiektowych? Podaj przykłady.
3. Co jest trudne do dodania w Abstract Factory?
4. Co odróżnia Adapter, Decorator i Proxy, skoro wszystkie opakowują obiekt?
5. Co odróżnia State od Strategy?
6. Jaki wymiar zmian preferuje Visitor i co go psuje?
7. Kiedy wariant funkcyjny wzorca zmienia jego semantykę?
8. Jakie sygnały mówią, że wzorzec jest zbędny?

---

## Lista kontrolna

- **Problem:** opisany bez nazwy wzorca; nazwany rodzaj zmienności; warianty istnieją dziś, nie „kiedyś”.
- **Wybór:** sprawdzona prostsza alternatywa (`if`, `enum`, `record`, lambda); znane konsekwencje z katalogu, także negatywne.
- **Kontrakt:** jawna kolejność (dekoratory, odbiorcy), polityka błędów, stan po wyjątku, tożsamość i cykl życia instancji.
- **Rozszerzenie:** wiadomo, która oś jest otwarta (nowa operacja czy nowy typ); test kontraktowy dla implementacji.
- **Wyjście:** wiadomo, kiedy wzorzec uprościć lub usunąć (moduł 5).

---

## Podsumowanie - najważniejsze wnioski

- Wzorzec to nazwane rozwiązanie problemu w kontekście - liczy się **intencja i konsekwencje**, nie diagram.
- Klasyfikacja GoF: kreacyjne, strukturalne, behawioralne; klasowe (dziedziczenie) i obiektowe (kompozycja).
- Te same struktury (opakowanie, delegacja, kontekst z polimorficznym polem) realizują różne intencje - rozróżnia je pytanie **co się zmienia i kto o tym decyduje**.
- Każdy wzorzec ma cenę: typy, pośredniość, kontrakt do utrzymania.
- Java 25 skraca zapis wielu ról (lambda, `enum`, `record`, `sealed`) - intencja zostaje, ale część semantyki trzeba zapewnić jawnie.
- Moduł 5: jak dojść do wzorca (i od niego odejść) bezpiecznymi krokami.
