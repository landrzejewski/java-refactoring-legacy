# Moduł 5. Refaktoryzacje hierarchii klas - warsztat praktyczny

## Zadania dla uczestników

### Jak pracować

- Każde ćwiczenie zaczynasz od zielonych testów i kończysz krótką retrospektywą (zadanie 5).
- Pracuj małymi krokami: jeden ruch pola, metody albo relacji typu, potem kompilacja i testy. Nie łącz w jednym kroku przeniesienia członka, zmiany zachowania i usunięcia publicznego API.
- Przed pracą utwórz własną gałąź, np. `git switch -c modul5-<imie>`. Zmieniaj wyłącznie pliki wskazane jako punkt wyjścia.
- Nie zaglądaj do pakietów oznaczonych w danym ćwiczeniu jako „nie otwieraj”. To gotowe wersje docelowe, które omówimy wspólnie.
- Testy uruchamiasz z IDE albo poleceniem:

```shell
mvn test -Dtest='pl.training.module5.**'
```

Pojedynczy test, np.:

```shell
mvn test -Dtest='NotificationCharacterizationTest'
```

Odpowiedniki w innych językach (jeśli pracujesz w C# lub TypeScript): `csharp/src/Training.Module5` z testami w `csharp/tests/Training.Module5.Tests` oraz `typescript/src/module5` z testami w `typescript/test/module5`. Nazwy klas i etapów są takie same.

### Przegląd zadań

| Nr | Nazwa | Punkt startowy |
| --- | --- | --- |
| Ćwiczenie 1 | Extract Superclass i Pull Up | `pl.training.module5.stage0` |
| Ćwiczenie 2 | Push Down i Extract Interface | `pl.training.module5.stage1` |
| Ćwiczenie 3 | Extract Subclass i Collapse Hierarchy (część A i B) | `pl.training.module5.extractsubclass.before`, `pl.training.module5.collapse.before` |
| Ćwiczenie 4 | Replace Inheritance with Composition | `pl.training.module5.composition.before` |
| Zadanie 5 | Retrospektywa po ćwiczeniu | wynik dowolnego ćwiczenia |

---

## Ćwiczenie 1: Extract Superclass i Pull Up

**Cel:** utworzyć wspólną klasę `Notification` dla powiadomień e-mail i SMS, zachowując dokładne wyniki i walidację obu kanałów.

**Czas:** ok. 45 minut (orientacyjnie).

### Kontekst

System obsługuje powiadomienia e-mail i SMS. Oba kanały:

- wymagają niepustego identyfikatora wiadomości, nadawcy i treści,
- usuwają białe znaki z początku i końca wartości,
- normalizują identyfikator nadawcy do wielkich liter niezależnie od ustawień regionalnych,
- budują stabilne podsumowanie tekstowe `messageId|SENDER|body|KANAŁ`,
- zwracają status `SENT` lub `FAILED`.

Tylko udana wysyłka SMS może zawierać znacznik `RECEIPT`. Klasa e-mail ma jednak skopiowane pole `deliveryReceipt` i prywatną metodę `appendReceipt`.

Granica kontraktu: publiczne konstruktory klas konkretnych, wyniki metod, typy wyjątków walidacji i kolejność elementów tekstu. Kontrakt nie obejmuje miejsca deklaracji prywatnych pól, refleksji ani serializacji. Zewnętrzne podklasy nie są dopuszczone.

### Pliki wejściowe

- `src/main/java/pl/training/module5/stage0/EmailNotification.java`
- `src/main/java/pl/training/module5/stage0/SmsNotification.java`
- `src/test/java/pl/training/module5/stage0/NotificationCharacterizationTest.java`
- `src/test/java/pl/training/module5/NotificationHierarchyEquivalenceTest.java` (test różnicowy uruchamiany na końcu)

Nie otwieraj: `stage1`, `stage2`, `stage3`.

Fragment wejściowy (`SmsNotification`, skrót):

```java
public final class SmsNotification {
    private final String messageId;
    private final String senderId;
    private final String body;
    private final boolean deliveryReceipt;
    // konstruktor: normalized(...) dla trzech pól, senderId.toUpperCase(Locale.ROOT)
    public String summary() {
        return messageId + "|" + senderId + "|" + body + "|SMS";
    }
    public String dispatch(boolean successful) {
        String result = summary() + (successful ? "|SENT" : "|FAILED");
        return successful ? appendReceipt(result) : result;
    }
    private String appendReceipt(String result) { ... }
    private static String normalized(String value, String fieldName) { ... }
}
```

### Polecenia

1. Uruchom `NotificationCharacterizationTest` i upewnij się, że przechodzi.
2. Wypisz pola i metody o wspólnej nazwie w obu klasach.
3. Dla każdego elementu odpowiedz, czy ma wspólne znaczenie, cykl życia i kontrakt (tabela: element / znaczenie / cykl życia / kontrakt / decyzja).
4. Utwórz pustą abstrakcyjną klasę `Notification` w pakiecie `stage0`.
5. Dołącz najpierw jedną klasę (`extends Notification`) i ponownie uruchom testy.
6. Przenieś wspólny stan przez konstruktor bazowy.
7. Przenieś normalizację, `messageId()`, budowę podsumowania i wspólną część wyniku wysyłki.
8. Wprowadź minimalny punkt polimorficzny dla nazwy kanału.
9. Dołącz drugą klasę i usuń zduplikowany kod.
10. Uruchom test różnicowy wszystkich etapów `NotificationHierarchyEquivalenceTest`.

### Pytania kontrolne

- Dlaczego pola bazowe pozostają prywatne?
- Czy `deliveryReceipt` naprawdę należy do wspólnego kontraktu?
- Co zmieniłoby wywołanie `channel()` z konstruktora?
- Czy dodanie `final` do `summary()` byłoby bezpieczne dla zewnętrznych podklas?
- Jak zmieniłoby się zachowanie, gdyby normalizacja zależała od domyślnych ustawień regionalnych?

### Kryteria akceptacji

- publiczne konstruktory klas konkretnych zachowują sygnatury,
- testy charakterystyki nadal przechodzą,
- w każdej klasie istnieje jedno źródło wspólnej reguły,
- nadklasa nie ujawnia surowych pól `protected`,
- potrafisz wskazać celowo niedoskonały stan `deliveryReceipt` w bazie.

**Produkt:** skompilowana hierarchia `Notification` / `EmailNotification` / `SmsNotification`, zielone testy oraz tabela decyzji z kroku 3.

---

## Ćwiczenie 2: Push Down i Extract Interface

**Cel:** usunąć cechę potwierdzenia z kontraktu bazowego i utworzyć rolę potrzebną klientowi wysyłającemu partię powiadomień.

**Czas:** ok. 45 minut (orientacyjnie).

### Kontekst

Po ćwiczeniu 1 klasa bazowa zawiera pole `deliveryReceipt` i metodę `appendReceipt`, choć mają znaczenie tylko dla SMS. Konstruktor e-mail przyjmuje flagę, która nic nie zmienia. Dodatkowo potrzebny jest klient `NotificationBatch`, który wysyła listę powiadomień i zwraca listę wyników. Klient ten potrzebuje wyłącznie operacji wysyłki.

Wymagania dla klienta partii:

- metoda `dispatchAll(lista powiadomień, boolean successful)` zwraca wyniki w kolejności wejścia,
- `null` jako lista lub jako element listy jest odrzucany `NullPointerException` zanim nastąpi jakakolwiek wysyłka,
- zwracana lista jest niemodyfikowalna.

### Pliki wejściowe

- `src/main/java/pl/training/module5/stage1/Notification.java`
- `src/main/java/pl/training/module5/stage1/EmailNotification.java`
- `src/main/java/pl/training/module5/stage1/SmsNotification.java`
- `src/test/java/pl/training/module5/NotificationHierarchyEquivalenceTest.java`

Nie otwieraj: `stage2`, `stage3` oraz testów z katalogu `src/test/java/pl/training/module5/stage3`.

### Polecenia

1. Znajdź wszystkie odczyty pola `deliveryReceipt` i wywołania `appendReceipt`.
2. Potwierdź testem, że e-mail ignoruje flagę dla sukcesu i błędu (wszystkie cztery kombinacje).
3. Przenieś pole oraz metodę do `SmsNotification`.
4. Usuń parametr z konstruktora nadklasy.
5. Zachowaj przejściowy czteroargumentowy konstruktor e-mail i dodaj konstruktor docelowy.
6. Zdefiniuj minimalny interfejs dla `NotificationBatch`.
7. Dodaj `implements` do nadklasy bez zmiany dotychczasowych sygnatur metod konkretnych.
8. Napisz klienta partii `NotificationBatch` tak, aby zależał od interfejsu.
9. Napisz i uruchom test kontraktowy dla e-maila i SMS wykonywany przez typ interfejsu. Dodaj test odrzucania `null` z fałszywą implementacją (lambda) zliczającą wywołania.
10. Zaprojektuj ścieżkę wycofania starego konstruktora e-mail dla publicznej biblioteki (kilka punktów, bez kodu).

### Kryteria akceptacji

- `Notification` nie zawiera stanu specyficznego dla SMS,
- SMS zachowuje potwierdzenie tylko po sukcesie,
- e-mail zachowuje dotychczasowy wynik dla obu wartości dawnej flagi,
- `NotificationBatch` nie zależy od klasy abstrakcyjnej,
- interfejs nie zawiera metod potrzebnych wyłącznie implementacji,
- test kontraktowy jest wykonywany przez typ interfejsu,
- plan wycofania starego konstruktora jest zapisany.

**Produkt:** zmieniona hierarchia, interfejs, `NotificationBatch`, test kontraktowy, plan wycofania.

---

## Ćwiczenie 3: Extract Subclass i Collapse Hierarchy

**Cel:** w części A wydzielić stabilny wariant zadania wysyłki do podklasy, w części B usunąć pusty poziom hierarchii formattera.

**Czas:** ok. 45 minut łącznie (orientacyjnie).

### Część A: Extract Subclass

#### Kontekst

`DeliveryJob` reprezentuje dwa warianty przez pole `Optional<Instant> scheduledAt`: zadanie natychmiastowe i zaplanowane. Wariant jest wybierany przez fabryki `immediate()` i `scheduled(Instant)` i nie zmienia się w czasie życia obiektu. `dispatchAt(now)` zwraca `SENT` albo `WAITING_UNTIL <termin>`.

#### Pliki wejściowe

- `src/main/java/pl/training/module5/extractsubclass/before/DeliveryJob.java`
- `src/test/java/pl/training/module5/extractsubclass/DeliveryJobEquivalenceTest.java` (test różnicowy; porównuje wersję `before` z wersją docelową)

Nie otwieraj: `extractsubclass/after`.

```java
public static DeliveryJob scheduled(Instant scheduledAt) {
    return new DeliveryJob(Optional.of(Objects.requireNonNull(
            scheduledAt, "scheduledAt must not be null")));
}

public String dispatchAt(Instant now) {
    Objects.requireNonNull(now, "now must not be null");
    return scheduledAt
            .filter(now::isBefore)
            .map(instant -> WAITING_UNTIL + instant)
            .orElse(SENT);
}
```

#### Polecenia

1. Zapisz tabelę wyników zadania natychmiastowego oraz zaplanowanego przed, dokładnie w i po terminie.
2. Wprowadź fabryki, jeśli eksperyment zaczyna się od publicznych konstruktorów (sprawdź, czy w tym kodzie to potrzebne).
3. Utwórz `ScheduledDeliveryJob`.
4. Zmień wyłącznie fabrykę wariantu zaplanowanego.
5. Przenieś `scheduledAt` oraz warunek do podklasy.
6. Usuń `Optional` i warunek z klasy bazowej.
7. Uruchom test różnicowy.
8. Wypisz kontrakty, których technika celowo nie zachowuje.

### Część B: Collapse Hierarchy

#### Kontekst

`LegacyNotificationFormatter` zawiera całe zachowanie formatowania, a `NotificationFormatter` jest pustą podklasą, której używają klienci. Format wyniku: `To: <recipient>\nMessage: <message>`, `null` w dowolnym argumencie daje `NullPointerException`.

#### Pliki wejściowe

- `src/main/java/pl/training/module5/collapse/before/LegacyNotificationFormatter.java`
- `src/main/java/pl/training/module5/collapse/before/NotificationFormatter.java`
- `src/test/java/pl/training/module5/collapse/NotificationFormatterEquivalenceTest.java`

Nie otwieraj: `collapse/after`.

#### Polecenia

1. Ustal, którą publiczną nazwę zachowują kontrolowani klienci.
2. Znajdź użycia obu klas, konstruktorów i relacji `extends`.
3. Przenieś zachowanie do typu docelowego.
4. Usuń zbędny poziom.
5. Uruchom test dokładnego formatu i walidacji (także dla polskich znaków).
6. Zaproponuj kompatybilny etap przejściowy, gdyby oba typy były publiczne.

### Kryteria akceptacji (część A i B)

- fabryki `DeliveryJob` zachowują publiczne typy wyników,
- termin graniczny ma jawny test,
- specjalny stan istnieje wyłącznie w specjalnym typie,
- rozpoznajesz zmianę dokładnej klasy runtime,
- formatter po scaleniu ma jedno źródło zachowania,
- usunięcie publicznej nazwy nie jest błędnie nazwane zgodną refaktoryzacją biblioteki.

**Produkt:** tabela wyników z kroku A1, lista niezachowanych kontraktów z kroku A8, scalony formatter, propozycja etapu przejściowego z kroku B6.

---

## Ćwiczenie 4: Replace Inheritance with Composition

**Cel:** zastąpić dziedziczenie po `ArrayList` wąską fasadą kolekcji bez przypadkowej obietnicy zgodności całego API `List`.

**Czas:** ok. 45 minut (orientacyjnie).

### Kontekst

`RecipientList extends ArrayList<String>` dziedziczy po liście wyłącznie po to, aby ponownie użyć kolekcji, i dodaje jedną metodę `snapshot()`. Przez dziedziczenie klienci mają dostęp do całego API `ArrayList`, w tym operacji, których domena nigdy nie zatwierdziła. Kontrolowani klienci używają: `add`, `remove(Object)`, `contains`, `size`, iteracji i `snapshot()`.

### Pliki wejściowe

- `src/main/java/pl/training/module5/composition/before/RecipientList.java`
- `src/test/java/pl/training/module5/composition/RecipientListEquivalenceTest.java`

Nie otwieraj: `composition/after`.

```java
public final class RecipientList extends ArrayList<String> {
    @Serial
    private static final long serialVersionUID = 1L;

    public List<String> snapshot() {
        return Collections.unmodifiableList(new ArrayList<>(this));
    }
}
```

### Polecenia

1. Wyszukaj wszystkie metody wywoływane przez kontrolowanych klientów.
2. Zapisz skrypt zachowania obejmujący duplikaty, kolejność, usuwanie i migawkę.
3. Dodaj prywatny `List<String>` jako delegate.
4. Wprowadź po jednej metodzie przekazującej scharakteryzowane operacje.
5. Zaimplementuj `Iterable<String>`, jeśli iteracja należy do kontraktu.
6. Usuń `extends ArrayList<String>`.
7. Uruchom skrypt różnicowy.
8. Sprawdź, które odziedziczone operacje przestały się kompilować.
9. Oceń `equals`, `hashCode`, serializację, iterator i własność delegata.
10. Przygotuj wariant planu dla publicznej klasy, której klienci wymagają pełnego `List`.

### Kryteria akceptacji

- wersje przed i po zachowują zatwierdzoną sekwencję klienta,
- wrapper nie dziedziczy niezatwierdzonego API,
- każdy wrapper posiada własny delegate,
- wcześniejsza migawka nie zmienia się po późniejszej mutacji właściciela,
- potrafisz wskazać różnicę między `remove(Object)` i `remove(int)`,
- utrata przypisywalności do `List` jest nazwana zmianą kontraktu, jeżeli była obserwowalna.

**Produkt:** klasa z kompozycją, skrypt zachowania, lista utraconych operacji i właściwości, plan dla wariantu publicznego.

---

## Zadanie 5: Retrospektywa po ćwiczeniu

**Cel:** nazwać, co naprawdę chroniły testy i które decyzje były projektowe, a nie mechaniczne.

**Czas:** ok. 10 minut po każdym ćwiczeniu albo 20 minut na koniec warsztatu.

Zespół odpowiada na pięć pytań:

1. Jaki kontrakt był chroniony?
2. Który krok był mechaniczny, a który wymagał decyzji projektowej?
3. Jaki test wykryłby najgroźniejszą regresję?
4. Której warstwy zgodności nie sprawdziły testy jednostkowe?
5. Czy końcowa hierarchia opisuje domenę, czy tylko ponownie używa kodu?

**Produkt:** krótka notatka zespołu (po jednym zdaniu na pytanie), przedstawiona grupie.

---

## Sprawdzenie wiedzy

1. Dlaczego podobny kod w dwóch klasach nie wystarcza do Extract Superclass?
2. Co oznacza zastępowalność podtypu w praktyce?
3. Czym overriding różni się od overloading?
4. Czy rzutowanie odbiorcy na nadtyp wyłącza dynamiczną dyspozycję metody?
5. Dlaczego dwa pola instancyjne o tej samej nazwie mogą przechowywać różne wartości w jednym obiekcie?
6. Co może zmienić przeniesienie inicjalizatora pola do nadklasy?
7. Dlaczego konstruktor podklasy nie może przypisać odziedziczonego pola instancyjnego `final`, które nie ma inicjalizatora w deklaracji?
8. Jaki monitor blokuje metoda `static synchronized`?
9. Co trzeba sprawdzić przed Pull Up Method zawierającej `super.method()`?
10. Dlaczego Push Down Method publicznej metody bazowej zwykle łamie stare binaria?
11. Dlaczego nie można emulować Push Down Field przez dwa pola o tej samej nazwie?
12. Kiedy Extract Subclass jest gorsze od State lub Strategy?
13. Jaką obserwowalną właściwość celowo zmienia Extract Subclass?
14. Dlaczego interfejs powinien wynikać z potrzeb klienta?
15. Czy dodanie metody `default` gwarantuje zgodność behawioralną?
16. Dlaczego zmiana parametru publicznej metody z klasy na interfejs może złamać stare `.class`?
17. Kiedy pusta podklasa nadal może mieć znaczenie?
18. Jakie zachowanie może zmienić delegowanie metody `synchronized`?
19. Dlaczego jawny `serialVersionUID` nie wystarcza po Pull Up Field?
20. Jak wiarygodnie przetestować zgodność binarną?
21. Co należy sprawdzić dla hierarchii `sealed` po dodaniu podtypu?
22. Dlaczego `RecipientList` po kompozycji nie jest zgodnym zamiennikiem dowolnego `List`?
