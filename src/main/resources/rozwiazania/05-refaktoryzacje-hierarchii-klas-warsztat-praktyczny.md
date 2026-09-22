# Moduł 5. Refaktoryzacje hierarchii klas - rozwiązania dla prowadzącego

Numeracja i nazwy zadań odpowiadają plikowi zadań `src/main/resources/prowadzenie/zadania/05-refaktoryzacje-hierarchii-klas-warsztat-praktyczny.md`. Ścieżki są względne od katalogu repozytorium.

Uwaga: materiał teoretyczny modułu nie zawiera sekcji „Krótkie aktywności”. Zadania obejmują cztery ćwiczenia z sekcji 13, retrospektywę (13.5) i sprawdzenie wiedzy (15).

| Zadanie | Kod referencyjny (wersja docelowa) | Testy chroniące zachowanie |
| --- | --- | --- |
| Ćwiczenie 1 | `src/main/java/pl/training/module5/stage1/*` | `stage0/NotificationCharacterizationTest`, `NotificationHierarchyEquivalenceTest` |
| Ćwiczenie 2 | `src/main/java/pl/training/module5/stage2/*`, `src/main/java/pl/training/module5/stage3/*` | `NotificationHierarchyEquivalenceTest`, `stage3/OutboundNotificationContractTest` |
| Ćwiczenie 3A | `src/main/java/pl/training/module5/extractsubclass/after/*` | `extractsubclass/DeliveryJobEquivalenceTest` |
| Ćwiczenie 3B | `src/main/java/pl/training/module5/collapse/after/NotificationFormatter.java` | `collapse/NotificationFormatterEquivalenceTest` |
| Ćwiczenie 4 | `src/main/java/pl/training/module5/composition/after/RecipientList.java` | `composition/RecipientListEquivalenceTest` |

(Testy leżą w `src/test/java/pl/training/module5/`.)

---

## Ćwiczenie 1: Extract Superclass i Pull Up

### Rozwiązanie wzorcowe

Oczekiwany wynik odpowiada pakietowi `stage1` (`src/main/java/pl/training/module5/stage1/Notification.java`, `EmailNotification.java`, `SmsNotification.java`).

Zalecana kolejność kroków (z teorii, 14.1):

1. Utworzyć pustą `Notification` i dołączyć jedną klasę.
2. Przenieść wspólne prywatne pola i ustawiać je w konstruktorze nadklasy.
3. Przenieść `normalized`, ponieważ jej kontrakt jest identyczny.
4. Przenieść `messageId()`.
5. Wprowadzić `channel()` i przenieść `summary()`.
6. Wydzielić wspólny `dispatchResult`.
7. Dołączyć drugi podtyp i usunąć duplikację.
8. Uruchomić test po każdym kroku.

`channel()` jest jedyną różnicą potrzebną wspólnemu algorytmowi podsumowania. Nie ma potrzeby tworzenia getterów dla wszystkich pól ani oznaczania pól jako `protected`.

Kształt docelowy (skrót):

```java
abstract class Notification {                 // dostęp pakietowy
    private final String messageId, senderId, body;
    private final boolean deliveryReceipt;    // celowo niedoskonałe
    protected Notification(String messageId, String senderId,
            String body, boolean deliveryReceipt) { ... normalized(...) ... }
    public final String messageId() { ... }
    public final String summary() { ... + "|" + channel(); }
    protected final String dispatchResult(boolean successful) { ... }
    protected final String appendReceipt(String result) { ... }
    protected abstract String channel();
    public abstract String dispatch(boolean successful);
}
```

`EmailNotification.dispatch` zwraca `dispatchResult(successful)`, `SmsNotification.dispatch` dokleja `appendReceipt` tylko przy sukcesie. Obie klasy są `public final` i zachowują publiczny czteroargumentowy konstruktor.

Przykładowa tabela decyzji z kroku 3:

| Element | Wspólne znaczenie i kontrakt? | Decyzja |
| --- | --- | --- |
| `messageId`, `senderId`, `body` | tak, ta sama walidacja i cykl życia | Pull Up Field (prywatne) |
| `normalized` | tak, identyczny kontrakt | Pull Up Method |
| `messageId()` | tak | Pull Up Method |
| `summary()` | tak, różni się tylko kanał | Pull Up Method + `channel()` |
| wspólna część `dispatch` | tak (statusy SENT i FAILED) | `dispatchResult` |
| `deliveryReceipt`, `appendReceipt` | nie, znaczenie tylko dla SMS | w tym ćwiczeniu przeniesione w górę jako stan przejściowy; do naprawy w ćwiczeniu 2 |

### Odpowiedzi na pytania kontrolne

- **Dlaczego pola bazowe pozostają prywatne?** Konstruktor bazowy i nazwane operacje kontrolują niezmienniki (normalizacja, walidacja). Surowe pole `protected` pozwala podklasom omijać te reguły i rozszerza kontrakt klasy bazowej (teoria 3.4).
- **Czy `deliveryReceipt` naprawdę należy do wspólnego kontraktu?** Nie. Przeniesienie jest poprawne w przyjętym kontrakcie zachowania (wyniki się nie zmieniają), ale jest sygnałem złego modelu: konstruktor e-mail przyjmuje wartość bez znaczenia, a nadklasa opisuje cechę nieprawdziwą dla wszystkich wariantów (14.1).
- **Co zmieniłoby wywołanie `channel()` z konstruktora?** Dynamiczna dyspozycja działa podczas konstrukcji. Konstruktor nadklasy wywołałby override podklasy, zanim jej pola zostaną zainicjalizowane (2.4, 3.3). Przy obecnych literałach wynik byłby poprawny, ale każda implementacja oparta na polu podklasy zwróciłaby `null`. Tworzy to punkt rozszerzenia działający na częściowo zainicjalizowanym obiekcie.
- **Czy dodanie `final` do `summary()` byłoby bezpieczne dla zewnętrznych podklas?** Nie w publicznej bibliotece: zewnętrzny override przestałby się kompilować, a stare binaria mogą zawieść przy ładowaniu. Tutaj jest bezpieczne, bo `Notification` ma dostęp pakietowy, a klasy konkretne są `final` (11.1, komentarz pod 11.3).
- **Jak zmieniłoby się zachowanie przy normalizacji zależnej od domyślnych ustawień regionalnych?** `toUpperCase()` bez `Locale.ROOT` daje różne wyniki zależnie od locale JVM (np. turecka reguła dla litery i). Wynik `summary()` przestałby być stabilny, a testy mogłyby przechodzić lokalnie i padać na innym serwerze.

### Typowe błędy uczestników

- Pola bazowe jako `protected` albo gettery dla wszystkich pól „na zapas”.
- Wywoływanie `channel()` lub budowanie `summary` w konstruktorze bazowym.
- Przeniesienie `dispatch` w całości do bazy z `if (this instanceof SmsNotification)`.
- Zmiana sygnatury publicznego konstruktora (np. usunięcie flagi z e-maila już w tym ćwiczeniu, mieszając dwa ruchy).
- Dołączenie obu klas naraz i duży krok bez testów pośrednich.
- Nazwa `BaseNotification` / `AbstractNotification` zamiast nazwy pojęcia (5.1).

### Pytania do dyskusji

- Czym różni się pytanie „czy klasy mają ten sam kod” od „czy są wariantami tego samego pojęcia” (1.2)?
- Co należałoby sprawdzić w realnym systemie przed Extract Superclass (mapa hierarchii 1.4: refleksja, serializacja, DI, ORM)?
- Czy `dispatchResult` powinno być `protected final`, czy mogłoby być prywatne w innym układzie?

---

## Ćwiczenie 2: Push Down i Extract Interface

### Rozwiązanie wzorcowe

Oczekiwany wynik odpowiada pakietom `stage2` (po Push Down) i `stage3` (po Extract Interface).

- `src/main/java/pl/training/module5/stage2/Notification.java` - bez `deliveryReceipt` i `appendReceipt`, konstruktor trzyargumentowy.
- `src/main/java/pl/training/module5/stage2/SmsNotification.java` - prywatne pole `deliveryReceipt`, prywatna `appendReceipt`.
- `src/main/java/pl/training/module5/stage2/EmailNotification.java` - konstruktor przejściowy `(messageId, senderId, body, boolean ignoredDeliveryReceipt)` delegujący przez `this(...)` do docelowego `(messageId, senderId, body)`.
- `src/main/java/pl/training/module5/stage3/OutboundNotification.java` - `@FunctionalInterface` z jedną metodą `String dispatch(boolean successful)`.
- `src/main/java/pl/training/module5/stage3/Notification.java` - `abstract class Notification implements OutboundNotification`; abstrakcyjne `dispatch` pochodzi z interfejsu.
- `src/main/java/pl/training/module5/stage3/NotificationBatch.java` - `dispatchAll(List<? extends OutboundNotification>, boolean)`: najpierw walidacja `null` listy i każdego elementu, dopiero potem wysyłka; wynik z `Stream.toList()` (niemodyfikowalny).
- Test kontraktowy: `src/test/java/pl/training/module5/stage3/OutboundNotificationContractTest.java` (wywołanie przez `List<OutboundNotification>`, niemodyfikowalny wynik, recording fake z `AtomicInteger` potwierdzający zero wywołań przed odrzuceniem `null`).

Omówienie z teorii (14.2):

- Najpierw należy przenieść zachowanie `appendReceipt` do SMS, a dopiero potem stan. Dzięki temu nadklasa przestaje zależeć od pola przed jego usunięciem.
- Czteroargumentowy konstruktor e-mail deleguje do trzyargumentowego i jawnie komunikuje, że parametr jest ignorowany wyłącznie dla zgodności okresu przejściowego.
- Interfejs `OutboundNotification` wynika z jedynej operacji klienta partii: `dispatch`. Nie zawiera `messageId()`, `summary()` ani `channel()`, ponieważ klient ich nie potrzebuje. Nie zawiera także metod związanych z potwierdzeniem SMS. `@FunctionalInterface` zabezpiecza rolę jako interfejs z jedną metodą abstrakcyjną.
- Zmiana typu parametru klienta z `Notification` na `OutboundNotification` jest bezpieczna źródłowo dla kontrolowanych wywołań przy ponownej kompilacji. Jeżeli metoda klienta była częścią publicznej biblioteki, stary deskryptor powinien pozostać jako przeciążenie delegujące przez okres migracji.

Krok 2 (e-mail ignoruje flagę) jest pokryty przez `NotificationHierarchyEquivalenceTest.allStagesPreserveEmailContractForEveryLegacyFlagAndStatus` (macierz: obie wartości flagi x sukces/błąd).

Krok 8: w materiale wyjściowym `stage1` nie ma klasy `NotificationBatch`; w praktyce uczestnik tworzy klienta od razu zależnego od interfejsu. Wymagania (walidacja przed wysyłką, niemodyfikowalny wynik) są kontraktem nowej klasy, a nie skutkiem samego Extract Interface (11.5).

### Krok 10: ścieżka wycofania starego konstruktora

> Rozwiązanie opracowane na podstawie kodu referencyjnego, nie pochodzi wprost z materiału teoretycznego.

1. Wydanie N: dodać konstruktor trzyargumentowy, stary czteroargumentowy oznaczyć `@Deprecated(since = "N", forRemoval = true)` z dokumentacją, że flaga jest ignorowana, i delegować przez `this(...)` (tak jak w `stage2`).
2. Zaktualizować kontrolowanych klientów i przykłady do konstruktora trzyargumentowego; w buildzie włączyć ostrzeżenia o użyciu przestarzałego API.
3. Wydanie N + k (zgodnie z polityką wersjonowania): usunąć stary konstruktor w wydaniu łamiącym (major), z notą migracyjną. Usunięcie zmienia zbiór deskryptorów `<init>`, więc stare `.class` wywołujące `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V` przestaną się linkować.
4. Opcjonalnie: test zgodności binarnej (klient skompilowany przeciw N uruchomiony z N + 1) potwierdzający, że w okresie przejściowym nic się nie łamie (10.2).

### Typowe błędy uczestników

- Najpierw usunięcie pola z bazy, a potem naprawianie kompilacji w wielu miejscach naraz.
- Pozostawienie pola w bazie i dodanie drugiego w `SmsNotification` (dwa niezależne sloty, 2.3).
- Usunięcie czteroargumentowego konstruktora e-mail w tym samym kroku co Push Down (łamie klientów).
- Interfejs kopiujący całe publiczne API klasy (`messageId`, `summary`), albo zawierający `channel()`.
- Klient partii zależny od `Notification` zamiast od interfejsu (a `Notification` jest pakietowa, więc zewnętrzny klient i tak nie może jej użyć).
- Walidacja `null` w tym samym strumieniu co wysyłka, przez co częściowy efekt następuje przed wyjątkiem.
- Test kontraktowy tylko przez typy konkretne.

### Pytania do dyskusji

- Dlaczego Push Down jest tu refaktoryzacją, a w publicznej bibliotece byłby zmianą łamiącą (4.2, 4.4)?
- Po co `List<? extends OutboundNotification>`, a nie `List<OutboundNotification>`?
- Co zmieniłaby metoda `default` dodana do `OutboundNotification` (7.4)?
- Czy `@FunctionalInterface` jest częścią kontraktu? Co się stanie, gdy ktoś doda drugą metodę abstrakcyjną?

---

## Ćwiczenie 3: Extract Subclass i Collapse Hierarchy

### Część A: Extract Subclass - rozwiązanie

Kod referencyjny: `src/main/java/pl/training/module5/extractsubclass/after/DeliveryJob.java` i `src/main/java/pl/training/module5/extractsubclass/after/ScheduledDeliveryJob.java`.

- `DeliveryJob` przestaje być `final`, ma konstruktor pakietowy, `immediate()` zwraca `new DeliveryJob()`, `scheduled(Instant)` zwraca `new ScheduledDeliveryJob(scheduledAt)` z typem deklarowanym `DeliveryJob`, `dispatchAt` waliduje `now` i zwraca `"SENT"`.
- `ScheduledDeliveryJob` (`public final`, konstruktor pakietowy) ma pole `Instant scheduledAt` z walidacją `null`; `dispatchAt` zwraca `"WAITING_UNTIL " + scheduledAt`, gdy `now.isBefore(scheduledAt)`, w przeciwnym razie `super.dispatchAt(now)`.

Tabela z kroku A1 (odpowiada `DeliveryJobEquivalenceTest`, termin `2030-06-15T10:15:30Z`):

| Wariant | `now` | Wynik |
| --- | --- | --- |
| natychmiastowe | dowolny | `SENT` |
| zaplanowane | termin - 1 s | `WAITING_UNTIL 2030-06-15T10:15:30Z` |
| zaplanowane | dokładnie termin | `SENT` |
| zaplanowane | termin + 1 s | `SENT` |

Krok A2: wersja `before` już ma prywatny konstruktor i fabryki, więc ten krok jest spełniony od początku.

Omówienie z teorii (14.3):

- Krytyczna jest zmiana punktu tworzenia przed Push Down. Obiekt zaplanowany musi rzeczywiście być `ScheduledDeliveryJob`, zanim stan i zachowanie znikną z `DeliveryJob`.
- Warunek `now.isBefore(scheduledAt)` oznacza, że równość z terminem przechodzi do `SENT`. Pominięcie testu granicznego mogłoby pozwolić na nieświadomą zmianę operatora.
- Dokładna klasa runtime specjalnego zadania zmienia się celowo. Jeśli `getClass`, mapowanie typu albo serializacja są częścią kontraktu, potrzebna jest dodatkowa migracja. Nie można ukryć tego faktu testem sprawdzającym wyłącznie tekst.

Krok A8, kontrakty celowo niezachowane (6.3): dokładna klasa runtime i `getClass()`, `equals` oparte na porównaniu klas, dyskryminatory ORM, metadane typu w JSON, serializacja Javy, kontenery DI i proxy, wzorce `instanceof` i `switch`. Dodatkowo `DeliveryJob` przestała być `final`, co w bibliotece rozszerza kontrakt (możliwość podklas w pakiecie).

Decyzje projektowe (12.1): `scheduledAt` jest niezmienne (wariant stabilny), fabryka zachowuje typ wyniku `DeliveryJob`, podklasa nie jest publicznym punktem konstrukcji, baza zachowuje pełny kontrakt wspólny, `super.dispatchAt(now)` świadomie używa zachowania bazowego. Gdyby zadanie mogło być wielokrotnie planowane, wstrzymywane i wznawiane, lepszy byłby State lub osobny obiekt polityki.

### Część B: Collapse Hierarchy - rozwiązanie

Kod referencyjny: `src/main/java/pl/training/module5/collapse/after/NotificationFormatter.java` (`public final class NotificationFormatter` z metodą `format`, walidacja `null` przez `Objects.requireNonNull`, wynik `"To: " + recipient + "\nMessage: " + message`).

Omówienie z teorii (14.3, 12.2):

- Pozostaje `NotificationFormatter`, ponieważ tej nazwy używają kontrolowani klienci. Zachowanie `LegacyNotificationFormatter` zostaje przeniesione, a pusta klasa znika.
- Scalenie jest zachowujące dla kontrolowanych klientów `NotificationFormatter`, ale usunięcie publicznej nazwy `LegacyNotificationFormatter` nie byłoby zgodne dla niezależnego klienta tej klasy.
- `final` na klasie wynikowej jest bezpieczne w zamkniętym przykładzie; w bibliotece pozwalającej tworzyć podklasy byłoby osobną zmianą łamiącą.

Krok B6, kompatybilny etap przejściowy: w publicznym API, w którym istnieją klienci obu nazw, należy utrzymać cienką klasę zgodności oznaczoną jako przestarzała albo zaplanować wydanie łamiące (8.3, 14.3). Taki typ zachowa część miejsc konstrukcji i przypisań, ale nie wszystkie kontrakty refleksyjne, serializacyjne i frameworkowe. Przykładowy kształt (opracowany na podstawie teorii):

```java
@Deprecated(forRemoval = true)
public class LegacyNotificationFormatter {
    private final NotificationFormatter delegate = new NotificationFormatter();
    public String format(String recipient, String message) {
        return delegate.format(recipient, message);
    }
}
```

Uwaga do omówienia: przy takim etapie relacja `NotificationFormatter extends LegacyNotificationFormatter` znika, więc klient przypisujący `NotificationFormatter` do zmiennej typu `LegacyNotificationFormatter` przestanie się kompilować. Jeśli ta przypisywalność była obserwowalna, etap przejściowy musi ją zachować albo zmianę trzeba zaplanować jako wydanie łamiące.

### Typowe błędy uczestników

- Zmiana konstruktora/pola i fabryki w jednym kroku; przeniesienie stanu do podklasy, zanim fabryka zaczęła ją zwracać.
- Brak testu dla `now == scheduledAt` albo zamiana `isBefore` na `!isAfter`.
- Zmiana typu wyniku fabryki na `ScheduledDeliveryJob` (niepotrzebnie wiąże klientów z podklasą).
- Publiczny konstruktor podklasy.
- Twierdzenie, że technika „nic nie zmienia”, bo testy tekstowe przechodzą (pominięcie klasy runtime).
- W części B usunięcie `NotificationFormatter` (nazwy używanej przez klientów) i pozostawienie `Legacy...`.
- Nazywanie usunięcia publicznej klasy „zgodną refaktoryzacją”.

### Pytania do dyskusji

- Kiedy pole `Optional` jest w porządku, a kiedy sygnalizuje ukryty podtyp?
- Co zrobiłby ORM z istniejącymi rekordami zadań zaplanowanych po tej zmianie?
- Kiedy pusta podklasa nadal ma znaczenie (marker, punkt rozszerzenia, cel DI, typ w protokole)?

---

## Ćwiczenie 4: Replace Inheritance with Composition

### Rozwiązanie wzorcowe

Kod referencyjny: `src/main/java/pl/training/module5/composition/after/RecipientList.java`.

- `public final class RecipientList implements Iterable<String>`,
- prywatne pole `List<String> recipients = new ArrayList<>()` (własny delegate w każdym wrapperze),
- jawne `add(String)`, `remove(Object)`, `contains(Object)`, `size()`,
- `iterator()` zwraca iterator niemodyfikowalnego widoku (blokuje `Iterator.remove()`),
- `snapshot()` zwraca `Collections.unmodifiableList(new ArrayList<>(recipients))`.

Skrypt zachowania z kroku 2 odpowiada `RecipientListEquivalenceTest`: dodanie elementów z duplikatem; rozmiar, zawartość i kolejność iteracji; usunięcie pierwszego równego wystąpienia przez `remove(Object)`; porównanie niemodyfikowalnych migawek; mutacja właściciela i izolacja wcześniejszej migawki; iterator po zmianie nie pozwala ominąć jawnego API mutacji; wynik usuwania nieistniejącego elementu (`false`); niezależność stanu dwóch instancji.

Omówienie z teorii (14.4):

- Własny delegate uniemożliwia przypadkowe dzielenie stanu między wrapperami. Każda jawnie zadeklarowana metoda domenowa odpowiada operacji potwierdzonej przez klienta.
- `remove(Object)` jest deklarowane jawnie. Gdyby klasa ujawniła także `remove(int)`, wywołanie z literałem liczbowym wybierałoby usunięcie po indeksie, a nie usunięcie elementu o wartości.
- `snapshot()` tworzy nową `ArrayList`, a następnie niemodyfikowalny widok tej kopii. Późniejsze zmiany delegata nie są widoczne w wyniku. To płytka migawka, ale `String` jest niemutowalny.

Krok 8, operacje, które przestają się kompilować lub znikają z kontraktu (12.3): przypisywalność do `List<String>`/`ArrayList`; `add(int, E)`, `set`, `clear`, `sort`, `replaceAll`, `get`, `addAll` i pozostałe odziedziczone metody; `remove(int)`; mutacja przez `Iterator.remove()` (celowo zablokowana); `equals` i `hashCode` kolekcji; dokładne charakterystyki `Spliterator` (sama metoda jest dostępna przez `Iterable`); serializowalność po `ArrayList`; klasa runtime i wyniki refleksji. `forEach` i `spliterator` pozostają jako domyślne metody `Iterable`.

Krok 9, ocena:

- `equals`/`hashCode`: wracają do tożsamości `Object`; dwie listy o tej samej zawartości przestają być równe. Jeśli klient trzymał je w `Set` lub porównywał, to zmiana kontraktu.
- serializacja: klasa przestaje być `Serializable`; stare dane `.ser` nie zostaną odczytane.
- iterator: tylko do odczytu; klient korzystający z `Iterator.remove()` wymaga migracji do `remove(Object)`.
- własność delegata: tworzony wewnątrz, nie wyciekający, jeden na wrapper.

Krok 10, wariant dla publicznej klasy wymagającej pełnego `List` (14.4):

- zachować dziedziczenie, jeżeli podtypowanie jest prawdziwe,
- zaimplementować pełny `List` i delegować z precyzyjnymi testami kontraktu (`equals`, `hashCode`, iteratory, spliterator, mutacja podczas iteracji, metody domyślne kolekcji),
- wprowadzić nowy wąski typ, migrować klientów i usunąć stary typ w wydaniu łamiącym.

Nie należy wybierać automatycznej delegacji setek operacji bez rozumienia ich kontraktów.

### Typowe błędy uczestników

- Delegat przekazywany z zewnątrz w konstruktorze bez kopiowania (współdzielony stan wrapperów).
- `iterator()` zwracający iterator delegata (pozwala na `remove` z pominięciem API).
- `snapshot()` zwracający `Collections.unmodifiableList(recipients)` bez kopii: widok, który zmienia się po mutacji właściciela.
- Wygenerowanie w IDE delegacji wszystkich metod `List` („bo tak było”).
- Ujawnienie `remove(int)` obok `remove(Object)`.
- Uznanie zmiany za zgodną, bo „testy przechodzą”, bez nazwania utraty przypisywalności do `List`.

### Pytania do dyskusji

- Jakie pułapki delegowania z 9.4 (hooki na `this`, `super`, fluent `this`, callbacki, `synchronized`) wystąpiłyby, gdyby nadklasa była własną klasą z hookami?
- Czego test kontraktowy nie wykryje w wrapperze (liczba wywołań, kolejność, tożsamość `this`, monitor) i jak to przetestować (recording fake, spy)?

---

## Zadanie 5: Retrospektywa po ćwiczeniu

> Rozwiązanie opracowane na podstawie kodu referencyjnego, nie pochodzi wprost z materiału teoretycznego.

Teoria podaje tylko pytania. Przykładowe odpowiedzi, na które warto naprowadzić zespoły:

| Pytanie | Ćwiczenie 1-2 (powiadomienia) | Ćwiczenie 3 (`DeliveryJob`, formatter) | Ćwiczenie 4 (`RecipientList`) |
| --- | --- | --- | --- |
| 1. Chroniony kontrakt | publiczne konstruktory, format `summary`/`dispatch`, `RECEIPT` tylko dla udanego SMS, typy wyjątków walidacji | wyniki `dispatchAt` z granicą `now == scheduledAt`, typ wyniku fabryk; dokładny format i walidacja formattera | zatwierdzona sekwencja klienta: duplikaty, kolejność, `remove(Object)`, izolacja migawki |
| 2. Mechaniczne vs decyzja | mechaniczne: przeniesienie pól i `normalized`; decyzja: czy `deliveryReceipt` należy do bazy, zawartość interfejsu, konstruktor przejściowy | mechaniczne: przeniesienie warunku; decyzja: czy wariant jest stabilny, którą nazwę formattera zachować | mechaniczne: metody przekazujące; decyzja: które operacje zatwierdzić, blokada `Iterator.remove()` |
| 3. Test na najgroźniejszą regresję | macierz flaga x status (`NotificationHierarchyEquivalenceTest`), test typów wyjątków | test graniczny „dokładnie w terminie” | izolacja migawki, niezależność instancji, iterator tylko do odczytu |
| 4. Niesprawdzona warstwa zgodności | binarna, refleksyjna, serializacyjna | klasa runtime, ORM/JSON/serializacja, stare binaria korzystające z `LegacyNotificationFormatter` | przypisywalność do `List`, `equals`/`hashCode`, serializacja |
| 5. Domena czy reuse | `Notification` jest pojęciem domenowym, `OutboundNotification` rolą klienta | `ScheduledDeliveryJob` to stabilny wariant; formatter nie miał dwóch pojęć | dziedziczenie po `ArrayList` było tylko reuse, kompozycja opisuje domenę |

Wskazówka prowadzenia: w pytaniu 4 odsyłaj do tabeli warstw zgodności (teoria 1.3) i do testu dwóch wersji artefaktu (10.2).

---

## Sprawdzenie wiedzy - odpowiedzi

1. Nadklasa deklaruje wspólne pojęcie i kontrakt. Podobieństwo implementacji może być przypadkowe albo wynikać wyłącznie ze współdzielenia algorytmu technicznego.
2. Obiekt podtypu przyjmuje dane dopuszczone przez bazę i zachowuje jej gwarancje wyników, wyjątków, stanu, efektów oraz innych obserwowalnych właściwości.
3. Overriding wybiera implementację instancyjnej sygnatury według klasy runtime odbiorcy. Overloading wybiera sygnaturę w czasie kompilacji według typów kompilacyjnych.
4. Nie. Zwykłe wywołanie nadal wybierze override klasy runtime. Dynamiczną dyspozycję omija między innymi jawne `super`.
5. Pole podklasy ukrywa pole nadklasy, ale go nie zastępuje. Jeśli obie deklaracje są instancyjne, obiekt zawiera osobny slot dla każdej z nich, a wybór zależy od typu kompilacyjnego odwołania. Pola statyczne należą osobno do klas.
6. Moment wykonania, kolejność efektów ubocznych, widoczne wartości podczas konstrukcji, a dla pola statycznego także moment inicjalizacji klasy.
7. Niezainicjalizowane w deklaracji pole instancyjne `final` musi zostać przypisane w konstruktorze albo bloku inicjalizacyjnym klasy deklarującej i spełniać definite assignment dla każdego konstruktora. Podklasa nie może zapisać odziedziczonego pola finalnego.
8. Obiekt `Class` klasy deklarującej metodę. Przeniesienie metody statycznej do innej klasy zmienia monitor.
9. Czy wyszukiwanie rozpoczęte od bezpośredniego nadtypu nadal wybiera tę samą implementację i czy nie zmienia się semantyka wywołania.
10. Stary odnośnik wskazuje metodę nadklasy i mechanizm rozwiązywania nie szuka implementacji w podklasach.
11. Dla pól instancyjnych powstałyby dwa niezależne sloty wybierane statycznie. Dla pól `static` powstałyby dwie zmienne klasowe. W obu przypadkach zapis do jednej deklaracji nie aktualizuje drugiej.
12. Gdy specjalna rola zmienia się podczas życia obiektu albo istnieje kilka niezależnych osi zachowania powodujących mnożenie klas.
13. Dokładny typ runtime specjalnych obiektów, a wraz z nim potencjalnie refleksję, `equals`, ORM i serializację.
14. Dzięki temu opisuje minimalną spójną rolę i nie wiąże klientów z operacjami konkretnej implementacji.
15. Nie. Konflikty metod domyślnych, metoda klasy o innej semantyce, atomowość i stare implementacje nadal mogą zmienić zachowanie.
16. Typ parametru jest częścią deskryptora metody zapisanego w pliku `.class`. Stary klient szuka dokładnie dawnej sygnatury.
17. Może być markerem domenowym, punktem rozszerzenia, typem w protokole, elementem konfiguracji, celem DI albo częścią formatu danych.
18. Metoda odziedziczona blokowała monitor wrappera, a metoda delegata może blokować monitor innego obiektu. Zmienia to wzajemne wykluczanie i widoczność pamięci.
19. Format zapisuje stan osobno dla każdego poziomu hierarchii. Przeniesienie pola zmienia segment, a UID nie migruje wartości między segmentami.
20. Uruchomić niezmieniony klient skompilowany przeciw wersji starej z biblioteką nową, a osobno ponownie skompilować klienta i porównać oba wyniki.
21. Listę bezpośrednich `permits`, modyfikatory podtypów oraz wszystkie wyczerpujące `switch`, które mogą otrzymać nowy wariant.
22. Traci przypisywalność, wiele odziedziczonych operacji oraz kontrakty `equals`, `hashCode`, serializacji i implementacji iteracji, chyba że zostaną jawnie odtworzone.
