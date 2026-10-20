# Moduł 4. Wzorce projektowe - zadania dla uczestników

## Jak pracować

- Wzorzec rozpoznajemy po **intencji** (jaki problem rozwiązuje i co się zmienia), a nie po nazwie klasy, pakietu czy kształcie diagramu.
- Zanim nazwiesz wzorzec, opisz problem jednym zdaniem bez jego nazwy. Zanim go zastosujesz, sprawdź prostszą alternatywę (`if`, `enum`, `record`, lambda).
- Przy każdym wzorcu wskaż koszt: dodatkowe typy, pośredniość, kontrakt do utrzymania i testowania.
- W ćwiczeniach z kodem nie zmieniaj istniejących klas, chyba że polecenie mówi inaczej. Nowe klasy dodawaj obok, a testy umieszczaj w tym samym pakiecie co testowany kod.
- Każdy nowy test sprawdź **kontrolną mutacją**: zepsuj na chwilę regułę, zobacz czerwony test, przywróć kod.
- Refaktoryzacje prowadzące do wzorców (i od nich) są tematem modułu 5. Tutaj budujemy słownik i umiejętność oceny.

### Przygotowanie środowiska

1. Sprawdź `mvn -version` i potwierdź, że Maven korzysta z Javy 25.
2. Przejdź do katalogu głównego repozytorium.
3. Uruchom `mvn clean verify`. Wszystkie testy powinny być zielone.
4. Uruchom katalog przykładów bez argumentów, aby zobaczyć listę kluczy:

```bash
mvn -q compile && java -cp target/classes pl.training.module4.Module4Examples
```

5. Pracuj w pakiecie `pl.training.module4` (`src/main/java/pl/training/module4` i `src/test/java/pl/training/module4`).

Uruchomienie jednego przykładu i samych testów modułu:

```bash
mvn -q compile && java -cp target/classes pl.training.module4.Module4Examples bridge
mvn test -Dtest='pl.training.module4.**'
```

Odpowiedniki w innych językach (jeśli pracujesz w C# lub TypeScript): `csharp/src/Training.Module4` (katalogi `Creational`, `Structural`, `Behavioral`, `Fn`; interfejsy z prefiksem `I`, np. `IMessageSender`, `IVisitor`) z testami w `csharp/tests/Training.Module4.Tests` (`cd csharp && dotnet run --project src/Training.Module4 -- <klucz|all>`, `cd csharp && dotnet test tests/Training.Module4.Tests`) oraz `typescript/src/module4` (katalogi `creational`, `structural`, `behavioral`, `fn`) z testami w `typescript/test/module4` (`cd typescript && npm ci && npm run build && npm run module4 -- <klucz|all>`, `cd typescript && npx vitest run test/module4`). Klucze przykładów i nazwy klas są takie same jak w Javie.

### Przegląd zadań

| Nr | Nazwa | Czas | Forma |
| --- | --- | --- | --- |
| Aktywność 1.7 | Nazwij problem, nie wzorzec | 3 min | analiza, dyskusja |
| Aktywność 2.8 | Konstruktor, fabryka czy builder? | 3 min | analiza |
| Aktywność 3.12 | Rozpoznaj opakowanie | 4 min | analiza kodu |
| Aktywność 4.18 | Strategy czy State? | 3 min | analiza, dyskusja |
| Ćwiczenie 1 | Rozpoznawanie wzorców w kodzie | 20 min + 10 min omówienia | analiza kodu |
| Ćwiczenie 2 | Nowy wariant bez modyfikacji | 30 min + 10 min omówienia | kod + test |
| Ćwiczenie 3 | Wariant obiektowy czy funkcyjny? | 20 min + 10 min omówienia | analiza + kod |
| Ćwiczenie 4 | Dobór wzorca - karty problemów | 15 min + 10 min omówienia | praca w parach, dyskusja |

---

## Krótkie aktywności

### Aktywność 1.7: Nazwij problem, nie wzorzec

**Czas:** 3 minuty.

**Cel:** oddzielić opis problemu od nazwy rozwiązania i zacząć od rodzaju zmienności.

**Kontekst:**

```java
byte[] export(Report report, String type) {
    if (type.equals("PDF"))  { /* 40 linii */ }
    if (type.equals("CSV"))  { /* 25 linii */ }
    if (type.equals("XLSX")) { /* 60 linii */ }
    throw new IllegalArgumentException("unknown type: " + type);
}
```

**Polecenia:**

1. Opisz problem tego kodu jednym zdaniem, nie używając nazwy żadnego wzorca.
2. Odpowiedz: co się zmienia, kto wybiera wariant (i kiedy), jak często dochodzi nowy format?
3. Dopiero teraz zaproponuj wzorzec albo uzasadnij, że żaden nie jest potrzebny.

**Oczekiwany produkt:** jedno zdanie opisu problemu, trzy krótkie odpowiedzi i decyzja.

---

### Aktywność 2.8: Konstruktor, fabryka czy builder?

**Czas:** 3 minuty.

**Cel:** dobrać najprostszy mechanizm tworzenia do liczby parametrów, niezmienników i wiedzy klienta.

**Polecenia:** dla każdego przypadku wskaż mechanizm (konstruktor lub `record`, statyczna metoda fabrykująca, Factory Method lub Abstract Factory, Builder) i uzasadnij jednym zdaniem.

1. `Money(BigDecimal amount, Currency currency)` - dwa wymagane pola, reguła: kwota nieujemna.
2. `HttpClientConfig` - 9 pól, z czego 6 opcjonalnych; `proxyHost` wymaga `proxyPort`.
3. `Notifier` - zależnie od konfiguracji powiadamia e-mailem, SMS-em albo push; klient nie powinien znać konkretnych klas.

**Oczekiwany produkt:** trzy decyzje z uzasadnieniem.

---

### Aktywność 3.12: Rozpoznaj opakowanie

**Czas:** 4 minuty.

**Cel:** zobaczyć, że Adapter, Decorator i Proxy mogą mieć identyczną strukturę, a rozróżnia je intencja.

**Kontekst:** trzy klasy implementują `PaymentsService` z `pl.training.module4.structural.proxy`:

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

**Polecenia:**

1. Przypisz każdej klasie wzorzec: Adapter, Decorator albo Proxy. Co rozstrzyga - kod czy intencja?
2. Czym staje się klasa B, jeśli zacznie odrzucać płatności w oknie serwisowym?
3. Do którego z trzech wzorców pasuje `PaymentLoggerProxy` z repozytorium?

**Oczekiwany produkt:** trzy przypisania z jednym zdaniem uzasadnienia oraz odpowiedzi na pytania 2 i 3.

---

### Aktywność 4.18: Strategy czy State?

**Czas:** 3 minuty.

**Cel:** rozróżnić dwa wzorce o tej samej strukturze po tym, kto i kiedy zmienia wariant.

**Polecenia:** dla każdego przypadku zdecyduj: Strategy, State czy żaden. Odpowiedz, kto zmienia wariant i kiedy, oraz czy istnieje tabela przejść.

1. Sposób zaokrąglania ceny wybierany w konfiguracji sklepu.
2. Bilet: ZAREZERWOWANY → OPŁACONY → WYKORZYSTANY; zwrot możliwy tylko przed wykorzystaniem.
3. Algorytm kompresji wybierany przez użytkownika przy każdym eksporcie.
4. Abonament: próbny → płatny → zawieszony; w każdym stanie inny limit pobrań.

**Oczekiwany produkt:** cztery decyzje z odpowiedzią „kto i kiedy”.

---

## Ćwiczenie 1: Rozpoznawanie wzorców w kodzie

**Cel:** rozpoznawać wzorce po intencji, także tam, gdzie nazwa pakietu lub klasy nic nie mówi albo wprowadza w błąd, oraz nazywać role, koszt i ryzyko widoczne w kodzie.

**Czas:** 20 minut pracy (w parach) i 10 minut omówienia.

### Kontekst

Pakiety `pl.training.module4.creational`, `structural` i `behavioral` mają nazwy wzorców, więc „rozpoznanie” po nazwie pakietu jest zbyt łatwe. W tym ćwiczeniu patrzysz na fragmenty, w których wzorzec jest ukryty, nazwany inaczej, niż działa, albo połączony z innym. Część fragmentów pochodzi z JDK - wzorce GoF są tam na każdym kroku.

### Pliki wejściowe

| Nr | Fragment | Plik lub wyrażenie |
| --- | --- | --- |
| F1 | `ServerEvent.builder()` i klasa `ServerEventBuilder` | `src/main/java/pl/training/module4/behavioral/observer/ServerEvent.java` |
| F2 | metody `op(...)` i `operatorFactory(...)` oraz interfejs `BiOperator` | `src/main/java/pl/training/module4/behavioral/interpreter/Application.java`, `src/main/java/pl/training/module4/behavioral/interpreter/BiOperator.java` |
| F3 | `PaymentLoggerProxy` | `src/main/java/pl/training/module4/structural/proxy/PaymentLoggerProxy.java` |
| F4 | pole `builder` w `PostgresSqlConnectionUrlBuilder` (porównaj z `MySqlConnectionUrlBuilder`) | `src/main/java/pl/training/module4/creational/builder/PostgresSqlConnectionUrlBuilder.java`, `src/main/java/pl/training/module4/creational/builder/MySqlConnectionUrlBuilder.java` |
| F5 | `MovieType` i `movies.Order` | `src/main/java/pl/training/module4/behavioral/strategy/movies/MovieType.java`, `src/main/java/pl/training/module4/behavioral/strategy/movies/Order.java` |
| F6 | `Playlist.iterator()` i `Playlist.reversed()` | `src/main/java/pl/training/module4/behavioral/iterator/Playlist.java` |
| F7 | `Department.accept(...)` i `Node.accept(...)` | `src/main/java/pl/training/module4/behavioral/visitor/Department.java`, `src/main/java/pl/training/module4/behavioral/visitor/Node.java` |
| F8 | metoda `memoize(...)` | `src/main/java/pl/training/module4/fn/FunctionalStyleSingleton.java` |
| F9 | `new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))` | JDK (`java.io`) |
| F10 | `Collections.unmodifiableList(list)` | JDK (`java.util`) |
| F11 | `Arrays.asList(array)` | JDK (`java.util`) |
| F12 | `Integer.valueOf(100) == Integer.valueOf(100)` | JDK (`java.lang`) |

### Polecenia

1. Dla każdego fragmentu wypełnij wiersz tabeli:

| Nr | Wzorzec (jeden lub kilka) | Role z katalogu (np. Context, Strategy) | Intencja jednym zdaniem | Koszt lub ryzyko widoczne w kodzie |
| --- | --- | --- | --- | --- |

2. Zaznacz fragmenty, w których nazwa klasy sugeruje inny wzorzec niż ten, który realizuje kod.
3. Zaznacz fragmenty, w których jeden wzorzec jest zbudowany z pomocą innego (np. fabryka wewnątrz Flyweight).
4. Wskaż co najmniej jeden fragment, który według ciebie **nie jest** wzorcem GoF, tylko zwykłą techniką (delegacja, statyczna metoda fabrykująca) - i uzasadnij.
5. Dla F3 i F7 zaproponuj jedną zmianę, która usunęłaby wskazane ryzyko, ale **nie wprowadzaj jej**.

### Ograniczenia

- Nie modyfikuj kodu. To ćwiczenie analityczne.
- Nie używaj nazwy pakietu jako jedynego argumentu.

### Kryteria akceptacji

- Każdy fragment ma wskazaną intencję jednym zdaniem, a nie tylko nazwę wzorca.
- Co najmniej trzy fragmenty mają wskazany więcej niż jeden wzorzec albo technikę pomocniczą.
- Dla F3 odpowiedź odróżnia strukturę (opakowanie z tym samym interfejsem) od intencji (kontrola dostępu vs dodanie odpowiedzialności).
- Dla F7 odpowiedź wskazuje, kto odpowiada za przejście po strukturze.
- Każde wskazane ryzyko jest konkretne (np. „mutowalny produkt po `build()`”), a nie ogólne („może być trudne w utrzymaniu”).

### Oczekiwany produkt

Wypełniona tabela 12 fragmentów, lista fragmentów o mylącej nazwie, lista połączeń wzorców oraz dwie propozycje zmian (F3, F7).

---

## Ćwiczenie 2: Nowy wariant bez modyfikacji

**Cel:** sprawdzić w praktyce, wzdłuż której osi wzorzec pozwala rozszerzać kod bez modyfikacji istniejących klas, i policzyć koszt rozszerzenia wzdłuż osi przeciwnej.

**Czas:** 30 minut pracy i 10 minut omówienia.

### Kontekst

Most (`structural.bridge`) rozdziela rodzaj wiadomości od kanału wysyłki. Gość (`behavioral.visitor`) oddziela operacje na drzewie działów od klas węzłów. Zespół chce dodać kanał push, wiadomość z podpisem i raport liczby pracowników. Wymaganie zespołu: istniejące klasy obu pakietów mają pozostać nietknięte - zmiany mają być widoczne w `git status` wyłącznie jako nowe pliki.

### Pliki wejściowe

- `src/main/java/pl/training/module4/structural/bridge/Message.java`, `src/main/java/pl/training/module4/structural/bridge/TextMessage.java`, `src/main/java/pl/training/module4/structural/bridge/UrgentMessage.java` - abstrakcja i jej rozszerzenia,
- `src/main/java/pl/training/module4/structural/bridge/MessageSender.java`, `src/main/java/pl/training/module4/structural/bridge/EmailSender.java`, `src/main/java/pl/training/module4/structural/bridge/SmsSender.java` - implementacja i kanały,
- `src/main/java/pl/training/module4/behavioral/visitor/Visitor.java`, `src/main/java/pl/training/module4/behavioral/visitor/Node.java`, `src/main/java/pl/training/module4/behavioral/visitor/Department.java`, `src/main/java/pl/training/module4/behavioral/visitor/SuperDepartment.java`, `src/main/java/pl/training/module4/behavioral/visitor/Employee.java`, `src/main/java/pl/training/module4/behavioral/visitor/Reporter.java`,
- `src/test/java/pl/training/module4/AdditionalPatternsExamplesTest.java` - wzór przechwytywania `System.out` (metoda `outputOf`) i adnotacji `@ResourceLock`.

### Polecenia

**Część A: Bridge**

1. Narysuj dwie hierarchie mostu i zapisz, ile klas potrzeba dziś dla 2 rodzajów i 2 kanałów, a ile byłoby potrzebnych bez mostu dla 3 rodzajów i 3 kanałów.
2. Dodaj klasę `PushSender implements MessageSender` w pakiecie `pl.training.module4.structural.bridge`, wypisującą `Push to <recipient>: <content>`.
3. Dodaj klasę `SignedMessage extends Message` przyjmującą w konstruktorze kanał i podpis; wysyła treść w postaci `<text> (<signature>)`. Podpis `null` ma być odrzucony.
4. Napisz test `BridgeExtensionTest` w `src/test/java/pl/training/module4/structural/bridge/`:
   - własny obiekt zastępczy `RecordingSender implements MessageSender` zapisujący wywołania,
   - każdy z trzech rodzajów wiadomości wysłany przez ten sam `RecordingSender` (dowód, że abstrakcja nie zależy od konkretnego kanału),
   - `UrgentMessage` wysłana przez `PushSender` - sprawdź wyjście konsoli (`Push to anna: [URGENT] DISK FULL`).
5. Sprawdź `git status`: żaden istniejący plik nie może być zmieniony.

**Część B: Visitor**

6. Dodaj klasę `EmployeeCounter implements Visitor` w pakiecie `pl.training.module4.behavioral.visitor`, z metodą `int count()`.
7. Napisz test `EmployeeCounterTest` w `src/test/java/pl/training/module4/behavioral/visitor/`: drzewo z `Department`, `SuperDepartment` i trzema `Employee` (w tym jeden wewnątrz `SuperDepartment`) daje 3; pusty dział daje 0.
8. Uruchom `Reporter` na drzewie zawierającym `SuperDepartment` z jednym pracownikiem (np. w teście albo tymczasowo w `main`, którego potem nie zatwierdzasz) i opisz, co widać w wyniku. Wyjaśnij przyczynę.
9. Bez pisania kodu: wypisz wszystkie pliki, które trzeba utworzyć lub zmienić, aby dodać nowy rodzaj węzła `Contractor` (zewnętrzny wykonawca w dziale). Co się stanie z istniejącymi gośćmi, jeśli nowa metoda w `Visitor` będzie miała pustą implementację domyślną, a co, jeśli będzie abstrakcyjna?

### Ograniczenia

- Nie modyfikuj istniejących klas w `structural.bridge` i `behavioral.visitor` ani `Module4Examples`.
- Nie używaj `instanceof` ani rzutowania w gościu.
- Nie zmieniaj formatu wyjścia istniejących kanałów i wiadomości.

### Kryteria akceptacji

- `mvn test -Dtest='pl.training.module4.**'` jest zielone, łącznie z nowymi testami.
- `git status` pokazuje wyłącznie nowe pliki.
- Test mostu używa własnego obiektu zastępczego, a nie tylko przechwytywania konsoli.
- `EmployeeCounter` liczy także pracowników działu `SuperDepartment`.
- Każdy nowy test zawodzi po kontrolnej mutacji (np. `SignedMessage` pomija podpis, `EmployeeCounter` liczy działy).
- Odpowiedź na polecenie 8 wskazuje przyczynę (pusta metoda domyślna `visit(SuperDepartment)`), a odpowiedź na polecenie 9 zawiera pełną listę plików i oba warianty interfejsu.

### Oczekiwany produkt

Klasy `PushSender`, `SignedMessage`, `EmployeeCounter`, testy `BridgeExtensionTest` i `EmployeeCounterTest`, rachunek klas z polecenia 1 oraz pisemne odpowiedzi na polecenia 8 i 9.

---

## Ćwiczenie 3: Wariant obiektowy czy funkcyjny?

**Cel:** ocenić, kiedy lambda lub kompozycja funkcji wystarczy w miejsce klas wzorca, a kiedy zmienia jego semantykę albo odbiera ważne możliwości.

**Czas:** 20 minut pracy i 10 minut omówienia.

### Kontekst

Pakiet `pl.training.module4.fn` zawiera warianty funkcyjne jedenastu wzorców. Zespół zastanawia się, czy w nowym kodzie pisać wzorce wyłącznie lambdami. Zanim przyjmie regułę, chce porównać warianty na konkretnych kryteriach i sprawdzić, czy zachowanie jest naprawdę takie samo.

### Pliki wejściowe

- `src/main/java/pl/training/module4/fn/FunctionalStyleChain.java` oraz `src/main/java/pl/training/module4/behavioral/chainofresponsibility/Validator.java`, `src/main/java/pl/training/module4/behavioral/chainofresponsibility/Logger.java`, `src/main/java/pl/training/module4/behavioral/chainofresponsibility/Processor.java` (para obowiązkowa),
- do wyboru dwie dalsze pary:
  - `src/main/java/pl/training/module4/fn/FunctionalStyleStrategy.java` i `src/main/java/pl/training/module4/behavioral/strategy/`,
  - `src/main/java/pl/training/module4/fn/FunctionalStyleDecorator.java` i `src/main/java/pl/training/module4/structural/decorator/`,
  - `src/main/java/pl/training/module4/fn/FunctionalStyleObserver.java` i `src/main/java/pl/training/module4/behavioral/observer/`,
  - `src/main/java/pl/training/module4/fn/FunctionalStyleState.java` i `src/main/java/pl/training/module4/behavioral/state/`,
  - `src/main/java/pl/training/module4/fn/FunctionalStyleCommand.java` i `src/main/java/pl/training/module4/behavioral/command/`,
  - `src/main/java/pl/training/module4/fn/FunctionalStyleSingleton.java` i `src/main/java/pl/training/module4/creational/singleton/`.

### Polecenia

1. Uruchom obie wersje każdej z trzech par (np. `chain-of-responsibility` i `fn-chain`).
2. Dla każdej pary wypełnij tabelę:

| Kryterium | Wariant obiektowy | Wariant funkcyjny |
| --- | --- | --- |
| liczba typów do napisania | | |
| nazwa domenowa roli | | |
| stan (gdzie i czy bezpieczny) | | |
| tożsamość (wyrejestrowanie, porównanie) | | |
| dodanie drugiej operacji (np. `undo`) | | |
| testowalność | | |
| czy semantyka jest identyczna? | | |

3. Dla pary łańcucha znajdź różnicę semantyczną: co dzieje się z żądaniem `"Te"` w obu wersjach?
4. Usuń tę różnicę w wariancie funkcyjnym. Dodaj klasę `FunctionalRequestChain` w pakiecie `pl.training.module4.fn`:
   - konstruktor przyjmuje `List<Function<String, Optional<String>>>`,
   - metoda `Optional<String> handle(String request)` przekazuje żądanie przez kolejne kroki; krok zwracający `Optional.empty()` przerywa łańcuch.
5. Napisz test `FunctionalRequestChainTest` w `src/test/java/pl/training/module4/fn/` z trzema krokami odpowiadającymi `Validator`, `Logger` i `Processor` (kroki zapisują swoje wywołania na listach):
   - `"Test"` przechodzi przez wszystkie kroki i daje `Processing: Test`,
   - `"Te"` zostaje zatrzymane na walidacji; log i przetwarzanie pozostają puste.
6. Zapisz regułę zespołu (2-3 zdania): kiedy rola wzorca zostaje lambdą, a kiedy klasą.

### Ograniczenia

- Nie modyfikuj istniejących klas w `fn` ani w pakietach wzorców.
- Nie używaj wyjątków do sterowania przepływem w `FunctionalRequestChain`.

### Kryteria akceptacji

- Tabela porównania jest wypełniona dla trzech par, a ostatni wiersz każdej z nich zawiera uzasadnienie.
- Zachowanie `FunctionalRequestChain` dla `"Te"` jest identyczne z obiektowym łańcuchem (brak logu i przetwarzania).
- Test zawodzi po kontrolnej mutacji (np. krok walidacji zawsze zwracający wartość albo pętla, która po pustym wyniku wraca do pierwotnego żądania).
- Reguła zespołu odwołuje się do kryteriów z tabeli, a nie do gustu („lambdy są nowocześniejsze”).

### Oczekiwany produkt

Trzy tabele porównania, klasa `FunctionalRequestChain` z testem i reguła zespołu.

---

## Ćwiczenie 4: Dobór wzorca - karty problemów

**Cel:** dobierać wzorzec do problemu, zaczynając od rodzaju zmienności, i zawsze rozważać prostszą alternatywę, w tym brak wzorca.

**Czas:** 15 minut pracy w parach i 10 minut omówienia.

### Kontekst

Każda karta opisuje sytuację z projektu. Nie ma jednej poprawnej odpowiedzi, ale są odpowiedzi lepiej i gorzej uzasadnione. Liczy się uzasadnienie oparte na tym, co się zmienia, kto o tym decyduje i jak często.

### Karty

| Karta | Sytuacja |
| --- | --- |
| K1 | Raport eksportujemy do PDF, CSV i XLSX; format wybiera użytkownik przy każdym eksporcie, co kwartał dochodzi nowy format. |
| K2 | Bilet przechodzi przez stany zarezerwowany, opłacony, wykorzystany, anulowany; dziś pięć metod klasy `Ticket` ma `switch` po polu `status`, a każda nowa reguła psuje inną gałąź. |
| K3 | Zewnętrzna biblioteka płatności przyjmuje kwoty w groszach (`long`), rzuca własne wyjątki kontrolowane; nasz kod używa `BigDecimal` i własnego interfejsu `PaymentGateway`. |
| K4 | Do klienta HTTP chcemy dodawać ponowienia, logowanie i pamięć podręczną - w różnych kombinacjach dla różnych usług. |
| K5 | Kategorie produktów tworzą drzewo dowolnej głębokości; dla każdej kategorii liczymy sumę cen i liczbę produktów w całym poddrzewie. |
| K6 | Edytor diagramów ma mieć cofanie i ponawianie operacji użytkownika (dodaj, przesuń, usuń element). |
| K7 | Obiekt konfiguracji połączenia ma 9 parametrów, 6 opcjonalnych; niektóre kombinacje są niedozwolone (np. TLS bez certyfikatu). |
| K8 | Po złożeniu zamówienia trzeba powiadomić magazyn, fakturowanie i wysyłkę e-maila; lista odbiorców rośnie z każdym wydaniem. |
| K9 | Formularz: zmiana kraju zmienia listę województw, regułę walidacji kodu pocztowego i widoczność pola NIP; komponenty odwołują się dziś do siebie nawzajem. |
| K10 | Dwa rodzaje rabatu (standardowy i studencki), niezmienne od pięciu lat, używane w jednym miejscu kodu. |
| K11 (dodatkowa) | Mapa gry ma milion kafelków, ale tylko 12 rodzajów tekstur; pamięć jest problemem. |
| K12 (dodatkowa) | Komponenty interfejsu (przycisk, pole, okno) występują w motywie jasnym i ciemnym; nie wolno mieszać komponentów z różnych motywów. |

### Polecenia

1. Dla każdej karty K1-K10 wypełnij wiersz:

| Karta | Rodzaj zmienności (co się zmienia, kto decyduje) | Wzorzec | Prostsza alternatywa | Główny koszt lub ryzyko |
| --- | --- | --- | --- | --- |

2. Wskaż karty, dla których najlepszą odpowiedzią jest „bez wzorca” lub najprostszy mechanizm języka.
3. Wskaż karty, w których sensowne jest połączenie dwóch wzorców.
4. Jeśli zostanie czas, rozwiąż karty K11 i K12.

### Kryteria akceptacji

- Każda decyzja zaczyna się od rodzaju zmienności, a nie od nazwy wzorca.
- Każdy wiersz ma prostszą alternatywę (także wtedy, gdy ją odrzucasz).
- Co najmniej jedna karta ma odpowiedź „bez wzorca” z uzasadnieniem.
- Koszt jest konkretny (np. „kolejność dekoratorów zmienia liczbę wpisów audytu”), a nie ogólny.

### Oczekiwany produkt

Tabela dziesięciu (lub dwunastu) kart oraz krótka lista kart „bez wzorca” i kart z połączeniem wzorców.

---

## Sprawdzenie wiedzy

1. Czym jest wzorzec projektowy i z jakich elementów składa się jego opis?
2. Na czym polega podział wzorców ze względu na rodzaj i ze względu na zakres? Które wzorce GoF są klasowe?
3. Czym różni się Factory Method od Abstract Factory? Co jest trudne do dodania w Abstract Factory?
4. Jakie problemy rozwiązuje Builder i jakie ryzyko niesie builder zwracający mutowalny produkt?
5. Dlaczego Singleton bywa uważany za antywzorzec i jakie są alternatywy w Javie?
6. Co odróżnia Adapter, Decorator, Proxy i Facade?
7. Czym różni się przezroczysty i bezpieczny wariant Composite?
8. Dlaczego kolejność dekoratorów jest częścią kontraktu?
9. Co odróżnia State od Strategy, a Strategy od Template Method?
10. Jaki wymiar zmian preferuje Visitor? Czym grożą puste metody domyślne w interfejsie gościa?
11. Jakie decyzje trzeba podjąć, projektując Observer?
12. Kiedy lambda wystarcza w miejsce klasy wzorca, a kiedy zmienia jego semantykę?
13. Jakie sygnały wskazują, że wzorzec jest zbędny?
