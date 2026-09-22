# Moduł 5. Refaktoryzacje hierarchii klas - przewodnik prowadzącego po przykładach

## 1. Uruchamianie przykładów i testów

### Java (wersja referencyjna)

```shell
mvn -q compile && java -cp target/classes pl.training.module5.Module5Examples
mvn test -Dtest='pl.training.module5.**'
```

Testy można też uruchamiać z IDE (pakiet `pl.training.module5` w `src/test/java`).

Oczekiwany wynik programu demonstracyjnego:

```text
Hierarchy stages equivalent: true
Extract subclass equivalent: true
Collapse hierarchy equivalent: true
Composition client behavior equivalent: true
Batch results: [mail-1|OPS|Ready|EMAIL|SENT, sms-1|OPS|Ready|SMS|SENT|RECEIPT]
```

Przy omawianiu deskryptorów i metod bridge (sekcja 2.7 teorii) można pokazać:

```shell
javap -classpath target/classes -p -s -v pl.training.module5.stage3.EmailNotification
```

### C#

```shell
cd csharp && dotnet run --project src/Training.Module5
dotnet test
```

Kod: `csharp/src/Training.Module5` (katalogi `Stage0..Stage3`, `ExtractSubclass`, `Collapse`, `Composition`), testy: `csharp/tests/Training.Module5.Tests`. Interfejs roli nazywa się tam `IOutboundNotification`.

### TypeScript

```shell
cd typescript && npm ci && npm run build && npm run module5
npm test
```

Kod: `typescript/src/module5`, testy: `typescript/test/module5` (np. `NotificationHierarchyEquivalence.test.ts`).

## 2. Struktura modułu w skrócie

Moduł ma jedno studium przypadku prowadzone przez cztery etapy (`stage0` do `stage3`, hierarchia powiadomień) oraz trzy niezależne przypadki przed/po (`extractsubclass`, `collapse`, `composition`). Wszystkie ścieżki poniżej są względne od katalogu repozytorium.

Kluczowa myśl do powtarzania przez cały moduł: dziedziczenie deklaruje zastępowalność (kontrakt), a nie tylko współdzielenie kodu. Każda transformacja ma inny profil ryzyka dla zgodności źródłowej, binarnej, refleksyjnej, serializacyjnej i integracyjnej (tabela warstw w sekcji 1.3 i macierz w sekcji 10.1 teorii).

## 3. Tabela mapująca

| Przykład | Sekcja teorii | Pliki (main) | Testy |
| --- | --- | --- | --- |
| Etap 0: niezależne klasy | 11.2, 1.4, 1.5 | `src/main/java/pl/training/module5/stage0/EmailNotification.java`, `.../stage0/SmsNotification.java` | `src/test/java/pl/training/module5/stage0/NotificationCharacterizationTest.java` |
| Etap 1: Extract Superclass + Pull Up | 11.3, 3, 5 | `src/main/java/pl/training/module5/stage1/Notification.java`, `EmailNotification.java`, `SmsNotification.java` | `src/test/java/pl/training/module5/NotificationHierarchyEquivalenceTest.java` |
| Etap 2: Push Down cechy SMS | 11.4, 4 | `src/main/java/pl/training/module5/stage2/Notification.java`, `EmailNotification.java`, `SmsNotification.java` | `NotificationHierarchyEquivalenceTest` |
| Etap 3: Extract Interface | 11.5, 7 | `src/main/java/pl/training/module5/stage3/OutboundNotification.java`, `Notification.java`, `EmailNotification.java`, `SmsNotification.java`, `NotificationBatch.java` | `src/test/java/pl/training/module5/stage3/OutboundNotificationContractTest.java`, `NotificationHierarchyEquivalenceTest` |
| Test równoważności etapów | 11.6, 10.6 | wszystkie `stage0..stage3` | `src/test/java/pl/training/module5/NotificationHierarchyEquivalenceTest.java` |
| Extract Subclass: `DeliveryJob` | 12.1, 6 | `src/main/java/pl/training/module5/extractsubclass/before/DeliveryJob.java` -> `.../extractsubclass/after/DeliveryJob.java`, `.../extractsubclass/after/ScheduledDeliveryJob.java` | `src/test/java/pl/training/module5/extractsubclass/DeliveryJobEquivalenceTest.java` |
| Collapse Hierarchy: formatter | 12.2, 8 | `src/main/java/pl/training/module5/collapse/before/LegacyNotificationFormatter.java`, `.../collapse/before/NotificationFormatter.java` -> `.../collapse/after/NotificationFormatter.java` | `src/test/java/pl/training/module5/collapse/NotificationFormatterEquivalenceTest.java` |
| Replace Inheritance with Composition: `RecipientList` | 12.3, 9 | `src/main/java/pl/training/module5/composition/before/RecipientList.java` -> `.../composition/after/RecipientList.java` | `src/test/java/pl/training/module5/composition/RecipientListEquivalenceTest.java` |
| Program demonstracyjny | 12.4 | `src/main/java/pl/training/module5/Module5Examples.java` | `src/test/java/pl/training/module5/Module5ExamplesTest.java` |

Powiązanie z ćwiczeniami: etap 0 -> 1 to Ćwiczenie 1, etap 1 -> 2 -> 3 to Ćwiczenie 2, `extractsubclass` i `collapse` to Ćwiczenie 3, `composition` to Ćwiczenie 4. Pokazując przykłady przed ćwiczeniem, pokazuj tylko wersje wyjściowe; wersje docelowe odsłaniaj przy omówieniu.

---

## 4. Studium przypadku: hierarchia powiadomień

### 4.1. Etap 0: niezależne klasy i duplikacja

**Sekcja teorii:** 11.1 (kontrakt szkoleniowy), 11.2.

**Co ilustruje:** dwie finalne, niezależne klasy `EmailNotification` i `SmsNotification` z niemal identycznym kodem: te same pola `messageId`, `senderId`, `body`, `deliveryReceipt`, ta sama `normalized(...)`, różnica w kanale w `summary()` i w tym, że tylko SMS dokleja `|RECEIPT`. W e-mailu `appendReceipt` jest martwym kodem (`@SuppressWarnings("unused")`), a pole `deliveryReceipt` nie ma znaczenia.

**Pliki:** `src/main/java/pl/training/module5/stage0/EmailNotification.java`, `src/main/java/pl/training/module5/stage0/SmsNotification.java`.

**Na co zwrócić uwagę:**

- Duplikacja wskazuje kierunek, ale nie dowodzi poprawnej nadklasy (sekcja 1.2: „czy mają ten sam kod” to inne pytanie niż „czy są wariantami tego samego pojęcia”).
- Martwa metoda `appendReceipt` w e-mailu i pole bez znaczenia to sygnał, że nie wszystko, co jest skopiowane, należy do wspólnego kontraktu.
- `toUpperCase(Locale.ROOT)`: normalizacja niezależna od locale (np. turecka litera i). To jest element kontraktu, nie detal.
- Walidacja: `null` daje `NullPointerException`, pusty lub biały tekst daje `IllegalArgumentException`. Typ wyjątku jest częścią kontraktu.

**Testy:** `NotificationCharacterizationTest`:

- `capturesEmailBehavior` - trim, wielkie litery nadawcy, `summary()`, `dispatch(true/false)` dla e-maila z flagą `true` (flaga nic nie zmienia),
- `capturesSmsReceiptBehavior` - `|RECEIPT` tylko przy sukcesie i fladze `true`,
- `capturesValidation` - `IllegalArgumentException` dla pustego `messageId`, `NullPointerException` dla `null` nadawcy.

Test nie ocenia, czy zachowanie jest idealne. Tworzy punkt odniesienia, żeby oddzielić ruch struktury od późniejszych zmian funkcjonalnych.

**Przebieg demonstracji:**

1. Otwórz obie klasy obok siebie (widok diff w IDE). Zapytaj: „Co tu jest wspólne? Czy wszystko, co jest wspólne tekstowo, jest wspólne znaczeniowo?”.
2. Wskaż `@SuppressWarnings("unused")` w e-mailu. Zapytaj: „Skąd się to wzięło i czy ta flaga ma sens dla e-maila?”.
3. Uruchom `NotificationCharacterizationTest`. Zapytaj: „Czego ten test nie sprawdza?” (np. SMS bez flagi przy błędzie, e-mail z flagą `false`). To prowadzi do testu różnicowego z macierzą.
4. Wróć do sekcji 1.4 teorii (mapa hierarchii): co byśmy sprawdzili w prawdziwym systemie poza `extends` (refleksja, serializacja, DI, ORM).

### 4.2. Etap 1: Extract Superclass oraz Pull Up

**Sekcja teorii:** 11.3; techniki z sekcji 3 (Pull Up Method/Field) i 5 (Extract Superclass).

**Co ilustruje:** nowa abstrakcyjna klasa `Notification` (dostęp pakietowy) z prywatnymi polami, konstruktorem `protected`, finalnymi `messageId()` i `summary()`, chronioną finalną `dispatchResult(boolean)`, abstrakcyjnym `channel()` i abstrakcyjnym `dispatch(boolean)`. Na tym etapie do bazy trafiają też `deliveryReceipt` i `appendReceipt`, co jest celowo niedoskonałym stanem przejściowym.

**Pliki:** `src/main/java/pl/training/module5/stage1/Notification.java`, `.../stage1/EmailNotification.java`, `.../stage1/SmsNotification.java`.

Kluczowy fragment:

```java
abstract class Notification {
    private final String messageId;
    private final String senderId;
    private final String body;
    private final boolean deliveryReceipt;   // cecha tylko SMS w bazie

    public final String summary() {
        return messageId + "|" + senderId + "|" + body + "|" + channel();
    }
    protected final String dispatchResult(boolean successful) {
        return summary() + (successful ? "|SENT" : "|FAILED");
    }
    protected final String appendReceipt(String result) { ... }
    protected abstract String channel();
    public abstract String dispatch(boolean successful);
}
```

**Na co zwrócić uwagę:**

- Pola pozostają `private` i są ustawiane przez `super(...)`; nie ma surowych pól `protected` (sekcja 3.4).
- `channel()` jest jedynym punktem polimorficznym potrzebnym algorytmowi `summary()`. Nie jest wywoływane z konstruktora (sekcja 3.3: overridable wywołanie w konstruktorze działa na częściowo zainicjalizowanym obiekcie).
- `final` na `summary()` dokumentuje wspólny algorytm. W publicznej bibliotece dodanie `final` do istniejącej metody łamie zewnętrzne override, tu jest bezpieczne, bo klasa bazowa jest pakietowa, a konkretne są `final`.
- Publiczne konstruktory czteroargumentowe zachowują sygnatury (sekcja 5.3).
- `deliveryReceipt` w bazie: zachowanie jest zgodne, ale model jest zły. E-mail przyjmuje wartość bez znaczenia, baza opisuje cechę nieprawdziwą dla wszystkich wariantów.

**Testy:** `NotificationHierarchyEquivalenceTest` (opis w 4.5) obejmuje `stage1`.

**Przebieg demonstracji:**

1. Pokaż `stage1/Notification.java`, potem obie podklasy. Zapytaj: „Ile linii zostało w podklasach i co one teraz wyrażają?”.
2. Zapytaj: „Dlaczego `channel()` jest `protected abstract`, a `summary()` `public final`?”.
3. Wskaż `deliveryReceipt` w bazie. Zapytaj: „Czy to jest prawda o każdej `Notification`?”. Zapowiedz etap 2.
4. Zapytaj o scenariusz: co by się stało, gdyby konstruktor bazowy budował `summary` (a więc wołał `channel()`), a podklasa trzymała kanał w polu inicjalizowanym w swoim konstruktorze (sekcja 2.4: wartość `null`).

### 4.3. Etap 2: Push Down cechy SMS

**Sekcja teorii:** 11.4; technika z sekcji 4 (Push Down Method/Field).

**Co ilustruje:** `deliveryReceipt` i `appendReceipt` (teraz prywatna) przeniesione do `SmsNotification`. Konstruktor bazowy ma trzy parametry. `EmailNotification` ma dwa konstruktory: przejściowy czteroargumentowy (parametr `ignoredDeliveryReceipt`) delegujący przez `this(...)` do docelowego trzyargumentowego.

**Pliki:** `src/main/java/pl/training/module5/stage2/Notification.java`, `.../stage2/EmailNotification.java`, `.../stage2/SmsNotification.java`.

```java
public EmailNotification(String messageId, String senderId,
        String body, boolean ignoredDeliveryReceipt) {
    this(messageId, senderId, body);
}

public EmailNotification(String messageId, String senderId, String body) {
    super(messageId, senderId, body);
}
```

**Na co zwrócić uwagę:**

- Kolejność: najpierw zachowanie (`appendReceipt`), potem stan. Dzięki temu baza przestaje zależeć od pola przed jego usunięciem.
- Push Down jest tu refaktoryzacją tylko dlatego, że granica kontraktu wyklucza zewnętrzne podklasy i obserwację układu pól (sekcja 11.1). W publicznej bibliotece usunięcie członka z bazy łamie stare binaria (sekcja 4.2, 4.4: przesuwanie nie jest symetryczne).
- Nazwa parametru `ignoredDeliveryReceipt` komunikuje, że wartość jest ignorowana zgodnie z zapisanym zachowaniem.
- Nie wolno zostawić pola w bazie i dodać drugiego w podklasie: powstałyby dwa niezależne sloty (sekcja 2.3).

**Testy:** `NotificationHierarchyEquivalenceTest`, w szczególności `allStagesPreserveEmailContractForEveryLegacyFlagAndStatus` (e-mail z obiema wartościami flagi).

**Przebieg demonstracji:**

1. Pokaż diff `stage1` vs `stage2` dla `Notification.java` i `SmsNotification.java`.
2. Zapytaj: „Czy stary kod klienta `new EmailNotification(a, b, c, true)` nadal się kompiluje? A stary plik `.class`?”. (Tak, sygnatura została zachowana).
3. Zapytaj: „Jak wycofalibyśmy czteroargumentowy konstruktor w publicznej bibliotece?” (`@Deprecated`, okres przejściowy, usunięcie w wydaniu łamiącym).

### 4.4. Etap 3: Extract Interface

**Sekcja teorii:** 11.5; technika z sekcji 7.

**Co ilustruje:** interfejs roli `OutboundNotification` z jedną metodą `dispatch(boolean)` oznaczony `@FunctionalInterface`. `Notification implements OutboundNotification` (abstrakcyjne `dispatch` przestaje być deklarowane w bazie, pochodzi z interfejsu). Nowy klient `NotificationBatch` zależy wyłącznie od interfejsu.

**Pliki:** `src/main/java/pl/training/module5/stage3/OutboundNotification.java`, `.../stage3/Notification.java`, `.../stage3/EmailNotification.java`, `.../stage3/SmsNotification.java`, `.../stage3/NotificationBatch.java`.

```java
public List<String> dispatchAll(
        List<? extends OutboundNotification> notifications,
        boolean successful) {
    Objects.requireNonNull(notifications, "notifications must not be null");
    var validatedNotifications = notifications.stream()
            .map(n -> Objects.requireNonNull(n, "notification must not be null"))
            .toList();
    return validatedNotifications.stream()
            .map(n -> n.dispatch(successful))
            .toList();
}
```

**Na co zwrócić uwagę:**

- Interfejs wynika z potrzeb klienta (tylko `dispatch`), nie z całego API klasy. Nie ma w nim `messageId()`, `summary()`, `channel()` ani niczego o potwierdzeniu.
- `@FunctionalInterface` chroni rolę jako interfejs z jedną metodą abstrakcyjną; w teście pozwala użyć lambdy jako fałszywej implementacji.
- `List<? extends OutboundNotification>` pozwala przekazać np. `List<SmsNotification>` (metoda tylko czyta).
- Dwa przejścia strumienia: najpierw walidacja całej kolekcji, potem wysyłka. Błędny późniejszy element nie powoduje częściowego efektu.
- Niemodyfikowalny wynik (`Stream.toList()`) to kontrakt nowej klasy, nie skutek Extract Interface.
- Pułapka binarna (sekcja 7.3): gdyby istniejąca publiczna metoda zmieniła typ parametru z klasy na interfejs, zmieniłby się deskryptor JVM. Tu `NotificationBatch` jest nowa, więc problem nie występuje.

**Testy:** `OutboundNotificationContractTest`:

- `everyImplementationSatisfiesTheExtractedContract` - e-mail i SMS wywoływane przez `List<OutboundNotification>`,
- `batchDependsOnlyOnTheClientRole` - wyniki dla `false` w kolejności wejścia oraz `UnsupportedOperationException` przy próbie modyfikacji wyniku,
- `batchRejectsInvalidInputs` - `null` jako lista i jako element; recording fake (lambda z `AtomicInteger`) potwierdza zero wywołań `dispatch` przed wykryciem błędu.

**Przebieg demonstracji:**

1. Pokaż `OutboundNotification.java` (4 linie). Zapytaj: „Dlaczego tylko jedna metoda?”.
2. Pokaż `NotificationBatch.java`. Zapytaj: „Po co dwa strumienie zamiast jednego?”. Następnie pokaż `batchRejectsInvalidInputs` jako dowód.
3. Uruchom `javap -p -s` dla `stage3.EmailNotification` i pokaż deskryptor `dispatch:(Z)Ljava/lang/String;`. Zapytaj: „Co by się zmieniło w deskryptorze, gdyby parametr metody publicznej zmienił typ z klasy na interfejs?”.

### 4.5. Test równoważności wszystkich etapów

**Sekcja teorii:** 11.6, 10.6.

**Plik:** `src/test/java/pl/training/module5/NotificationHierarchyEquivalenceTest.java`.

**Co sprawdza:**

- `allStagesPreserveEmailContractForEveryLegacyFlagAndStatus` - macierz 2x2 (flaga x status) dla e-maila we wszystkich czterech etapach, oczekiwanie niezależne: `msg-1|OPS|Deployment ready|EMAIL|SENT/FAILED`,
- `allStagesPreserveSmsContractForEveryReceiptAndStatus` - macierz 2x2 dla SMS, `|RECEIPT` tylko przy sukcesie i fladze,
- `allStagesPreserveValidationTypes` - dokładny typ wyjątku (`IllegalArgumentException`, `NullPointerException`) we wszystkich etapach; porównanie `getClass()`, więc podklasa wyjątku też nie przejdzie.

**Na co zwrócić uwagę:** oczekiwanie jest wyliczane niezależnie, a nie przez `before.equals(after)`. Obie wersje mogłyby mieć ten sam błąd. Test sprawdza zachowanie, nie klasę runtime, pola ani refleksję, zgodnie z granicą kontraktu z 11.1.

**Pytanie do grupy:** „Której warstwy zgodności z tabeli 1.3 ten test nie dotyka?” (binarnej, refleksyjnej, serializacyjnej, integracyjnej).

---

## 5. Extract Subclass: `DeliveryJob`

**Sekcja teorii:** 12.1; technika z sekcji 6.

**Co ilustruje:** wariant zaplanowany reprezentowany przez `Optional<Instant>` zostaje wydzielony do `ScheduledDeliveryJob`. Fabryka `scheduled(...)` zwraca nowy typ runtime, ale typ deklarowany wyniku pozostaje `DeliveryJob`.

**Pliki:** przed: `src/main/java/pl/training/module5/extractsubclass/before/DeliveryJob.java` (finalna klasa, prywatny konstruktor, fabryki `immediate()` i `scheduled(Instant)`). Po: `src/main/java/pl/training/module5/extractsubclass/after/DeliveryJob.java` (nie-finalna, konstruktor pakietowy, `dispatchAt` zwraca `SENT`) i `src/main/java/pl/training/module5/extractsubclass/after/ScheduledDeliveryJob.java`.

```java
public final class ScheduledDeliveryJob extends DeliveryJob {
    private final Instant scheduledAt;

    @Override
    public String dispatchAt(Instant now) {
        Objects.requireNonNull(now, "now must not be null");
        if (now.isBefore(scheduledAt)) {
            return "WAITING_UNTIL " + scheduledAt;
        }
        return super.dispatchAt(now);
    }
}
```

**Na co zwrócić uwagę:**

- Klasa bazowa musiała przestać być `final`. Konstruktor pakietowy ogranicza, kto może tworzyć podklasy i instancje.
- Wersja przed już ma fabryki, więc krok „wprowadź fabryki” z procedury (6.2) jest tu spełniony od początku. Krytyczne jest zmienienie punktu tworzenia (`scheduled`) przed Push Down.
- Granica: `now == scheduledAt` daje `SENT` (warunek `isBefore`). Zamiana na `!isAfter` byłaby regresją.
- Zmienia się dokładna klasa runtime obiektu zaplanowanego: `getClass()`, `equals` oparte na `getClass()`, ORM, JSON z metadanymi typu, serializacja, `switch`/`instanceof`. To celowa zmiana (sekcja 6.3).
- `super.dispatchAt(now)` świadomie używa zachowania bazowego po terminie (w tym walidacji `now`).
- Model działa, bo wariant jest stabilny. Przy planowaniu, wstrzymywaniu i wznawianiu lepszy byłby State lub osobna polityka.

**Testy:** `src/test/java/pl/training/module5/extractsubclass/DeliveryJobEquivalenceTest.java`:

- `immediateJobIsSentAtTheRequestedTime` - `SENT` dla zadania natychmiastowego w obu wersjach,
- `scheduledJobPreservesItsObservableResult` - parametryzowany: sekundę przed terminem (`WAITING_UNTIL 2030-06-15T10:15:30Z`), dokładnie w terminie (`SENT`), sekundę po (`SENT`); porównuje obie wersje z niezależnym oczekiwaniem i ze sobą.

Test celowo nie sprawdza klasy runtime.

**Przebieg demonstracji:**

1. Pokaż wersję `before`. Zapytaj: „Czy to `Optional` jest problemem? Kiedy pole opcjonalne jest w porządku?”.
2. Poproś grupę o tabelę wyników (natychmiastowe, przed, w, po terminie), zanim otworzysz test.
3. Pokaż `after/DeliveryJob.java` i `after/ScheduledDeliveryJob.java`.
4. Zapytaj: „Co zobaczy kod, który robi `job.getClass() == DeliveryJob.class`?”, „Co zrobi ORM z dyskryminatorem?”.
5. Uruchom `DeliveryJobEquivalenceTest`. Tymczasowo zmień `isBefore` na `!isAfter` w `ScheduledDeliveryJob` i pokaż, że test graniczny się czerwieni (potem cofnij).

---

## 6. Collapse Hierarchy: formatter

**Sekcja teorii:** 12.2; technika z sekcji 8.

**Co ilustruje:** `LegacyNotificationFormatter` (publiczna, nie-finalna) zawiera całe zachowanie, `NotificationFormatter` jest pustą finalną podklasą używaną przez klientów. Po scaleniu zostaje jedna finalna klasa `NotificationFormatter` z metodą `format`.

**Pliki:** przed: `src/main/java/pl/training/module5/collapse/before/LegacyNotificationFormatter.java`, `src/main/java/pl/training/module5/collapse/before/NotificationFormatter.java`. Po: `src/main/java/pl/training/module5/collapse/after/NotificationFormatter.java`.

**Na co zwrócić uwagę:**

- Zostaje nazwa, której używają kontrolowani klienci (`NotificationFormatter`), a nie ta, która ma kod.
- Scalenie jest zachowujące tylko dla klientów `NotificationFormatter`. Usunięcie publicznej `LegacyNotificationFormatter` łamie niezależnych klientów tej nazwy (błąd ładowania klasy, rzutowania, konfiguracja DI). W bibliotece: cienki typ zgodności `@Deprecated` i zaplanowane wydanie łamiące.
- Pusta klasa nie zawsze jest bezwartościowa: marker, punkt rozszerzenia, typ w protokole, cel DI (sekcja 8.1).
- `final` na klasie wynikowej jest bezpieczne w zamkniętym przykładzie; w bibliotece pozwalającej na podklasy byłoby osobną zmianą łamiącą.

**Testy:** `src/test/java/pl/training/module5/collapse/NotificationFormatterEquivalenceTest.java`:

- `collapsedFormatterPreservesValidation` - `NullPointerException` dla `null` odbiorcy i `null` treści w obu wersjach,
- `collapsedFormatterPreservesTheFormattedNotification` - dokładny tekst `To: ...\nMessage: ...` dla danych operacyjnych i dla polskich znaków (`zespół@example.pl`, `Zażółć gęślą jaźń`).

**Przebieg demonstracji:**

1. Pokaż obie klasy `before`. Zapytaj: „Którą klasę usuniemy i dlaczego nie tę pustą?”.
2. Pokaż `after/NotificationFormatter.java`.
3. Zapytaj: „Co się stanie z klientem zewnętrznym, który ma w konfiguracji nazwę `LegacyNotificationFormatter`?”.
4. Uruchom test.

---

## 7. Replace Inheritance with Composition: `RecipientList`

**Sekcja teorii:** 12.3; technika z sekcji 9.

**Co ilustruje:** `RecipientList extends ArrayList<String>` z metodą `snapshot()` zamieniona na `RecipientList implements Iterable<String>` z prywatnym delegatem `List<String>` i jawnymi metodami `add`, `remove(Object)`, `contains`, `size`, `iterator`, `snapshot`.

**Pliki:** przed: `src/main/java/pl/training/module5/composition/before/RecipientList.java`. Po: `src/main/java/pl/training/module5/composition/after/RecipientList.java`.

```java
public final class RecipientList implements Iterable<String> {
    private final List<String> recipients = new ArrayList<>();

    public boolean remove(Object recipient) {
        return recipients.remove(recipient);
    }

    @Override
    public Iterator<String> iterator() {
        return Collections.unmodifiableList(recipients).iterator();
    }

    public List<String> snapshot() {
        return Collections.unmodifiableList(new ArrayList<>(recipients));
    }
}
```

**Na co zwrócić uwagę:**

- Po zmianie obiekt nie jest `List` ani `ArrayList`: tracimy przypisywalność, `add(int, E)`, `set`, `clear`, `sort`, `replaceAll`, `remove(int)`, `equals`/`hashCode` kolekcji, serializowalność po `ArrayList`, dokładne charakterystyki spliteratora. To zmiana kontraktu, jeżeli była obserwowalna.
- `Iterable` dodaje domyślne `forEach` i `spliterator`, więc też należą do publicznego kontraktu.
- Iterator wersji po zmianie blokuje `Iterator.remove()` (celowo); klient używający tej drogi musi przejść na `remove(Object)`.
- `remove(Object)` vs `remove(int)`: gdyby wrapper ujawnił oba przeciążenia, literał liczbowy wybrałby usunięcie po indeksie (overloading jest statyczny, sekcja 2.2).
- Delegat jest tworzony w polu, więc każdy wrapper ma własny stan (brak przypadkowego współdzielenia).
- `snapshot()` to płytka kopia w niemodyfikowalnym widoku; `String` jest niemutowalny, więc płytkość nie szkodzi.
- Pułapki delegowania z sekcji 9.4 (hooki na `this`, `super`, fluent `this`, callbacki, `synchronized`) tu nie występują, ale warto je wymienić.

**Testy:** `src/test/java/pl/training/module5/composition/RecipientListEquivalenceTest.java` (jedna metoda `compositionPreservesTheIntendedRecipientListContract`) wykonuje tę samą sekwencję na obu wersjach: dodanie z duplikatem, rozmiar, `contains`, kolejność iteracji, `remove` pierwszego wystąpienia, porównanie niemodyfikowalnych migawek, izolacja migawki po późniejszym `add`, `UnsupportedOperationException` z `Iterator.remove()` w wersji po zmianie, `false` przy usuwaniu nieistniejącego elementu, niezależność stanu nowych instancji.

**Przebieg demonstracji:**

1. Pokaż wersję `before`. Poproś grupę, by w IDE wpisała `new RecipientList().` i przejrzała podpowiedzi. Zapytaj: „Które z tych operacji domena zatwierdziła?”.
2. Pokaż wersję `after`. Zapytaj: „Czego już nie da się zrobić? Czy to jest zgodna zmiana API?”.
3. Zapytaj o `remove(int)` vs `remove(Object)` na `List<Integer>`.
4. Uruchom test. Zapytaj: „Czego ten test nie sprawdza?” (np. `equals`, serializacja, przypisywalność).
5. Zapytaj o wariant publiczny: zachować dziedziczenie, zaimplementować pełny `List` z delegowaniem, albo nowy wąski typ i migracja z wydaniem łamiącym.

---

## 8. Program demonstracyjny

**Sekcja teorii:** 12.4.

**Plik:** `src/main/java/pl/training/module5/Module5Examples.java`; test dymny `src/test/java/pl/training/module5/Module5ExamplesTest.java` (sprawdza tylko, że `main` nie rzuca wyjątku).

Program porównuje `stage0` z `stage3` dla SMS, wersje `before`/`after` dla `DeliveryJob` (sekundę przed terminem), formattera i `RecipientList` (migawki), a na końcu uruchamia `NotificationBatch` z e-mailem i SMS. Dobry na otwarcie modułu (pokazać, że wszystkie przykłady działają) i na zamknięcie (podsumowanie).

**Uwaga dla prowadzącego:** porównania w programie to pojedyncze przypadki, nie dowód równoważności. Warto to podkreślić i odesłać do testów różnicowych z macierzą scenariuszy.

---

## 9. Sugerowany plan zajęć

| Blok | Treść | Materiał |
| --- | --- | --- |
| 1 | Kontrakt hierarchii, warstwy zgodności, pętla bezpiecznej transformacji | teoria 1, program demonstracyjny |
| 2 | Semantyka dziedziczenia w Javie (dyspozycja, overloading, pola, konstruktory, monitory, `protected`, bridge, `sealed`) | teoria 2, `javap` na `stage3` |
| 3 | Etap 0 i Ćwiczenie 1 | 4.1, potem omówienie 4.2 |
| 4 | Ćwiczenie 2 | omówienie 4.3, 4.4, 4.5 |
| 5 | Ćwiczenie 3 | omówienie rozdziałów 5 i 6 |
| 6 | Ćwiczenie 4 | omówienie rozdziału 7 |
| 7 | Zgodność, serializacja, ORM/DI (teoria 10), retrospektywa, sprawdzenie wiedzy | zadanie 5 |
