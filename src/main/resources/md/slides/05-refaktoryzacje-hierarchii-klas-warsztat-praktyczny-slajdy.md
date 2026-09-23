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
# Moduł 5. Refaktoryzacje hierarchii klas - warsztat praktyczny
Jak bezpiecznie przesuwać stan, zachowanie i relacje typów, nie łamiąc kontraktów, starych binariów ani integracji

---

## Agenda

1. Kontrakt hierarchii i model ryzyka
2. Semantyka dziedziczenia w Javie 25
3. Pull Up / Push Down (Method, Field)
4. Extract Superclass, Extract Subclass, Extract Interface
5. Collapse Hierarchy, Replace Inheritance with Composition
6. Zgodność, serializacja i integracje frameworkowe
7. Studium przypadku: hierarchia powiadomień + dodatkowe przypadki przed/po
8. Warsztat praktyczny, sprawdzenie wiedzy, listy kontrolne

---

## 1.1-1.2. Hierarchia jest częścią zachowania

- Podklasa deklaruje, że może wystąpić **wszędzie tam, gdzie klient oczekuje nadklasy**.
- Poprawny podtyp nie wzmacnia warunków wstępnych, nie osłabia gwarancji, nie dodaje wyjątków ani efektów.
- **Zgodna sygnatura nie dowodzi zastępowalności**: `ReadOnlyAccount.withdraw` zawsze rzucające wyjątek to zły podtyp.
- Dwa osobne pytania: czy klasy mają **ten sam kod**, czy są **wariantami jednego pojęcia**?
- Wspólny kod uzasadnia współpracownika lub funkcję; dziedziczenie - dopiero **wspólny kontrakt**.

---

## 1.3. Warstwy zgodności

| Warstwa | Pytanie kontrolne |
| --- | --- |
| zachowanie wykonania | Czy wyniki, wyjątki, stan i efekty pozostają zgodne? |
| zgodność źródłowa | Czy kod klienta nadal się kompiluje? |
| zgodność binarna | Czy stary skompilowany klient nadal się linkuje? |
| kontrakt refleksyjny | Czy nazwy, typ deklarujący i adnotacje są znajdowane? |
| zgodność serializacyjna | Czy nowy kod czyta dane zapisane przez starą wersję? |
| integracje | Czy ORM, DI, proxy i konfiguracja rozumieją model? |

Zgodność binarna nie dowodzi zgodności źródłowej ani zachowania.

---

## 1.4-1.5. Mapa hierarchii i pętla transformacji

- Zinwentaryzuj podtypy (także zewnętrzne), konstruktory, fabryki, DI, `super`, `instanceof`, rzutowania.
- **Samo wyszukanie `extends` nie wystarczy** - hierarchia bywa w konfiguracji, ORM, `ServiceLoader`, proxy.
- Pętla: nazwij kontrakt i test → **jeden** ruch członka lub relacji → kompilacja całej hierarchii.
- Testy wszystkich podtypów przez typ bazowy; stary członek znika dopiero po migracji.
- IDE **nie potwierdza** zgodności starych binariów, danych historycznych ani zewnętrznych podklas.

---

## 2.1. Overriding i dynamiczna dyspozycja

- JVM wybiera implementację według klasy odbiorcy - **rzutowanie na nadtyp tego nie wyłącza**.
- Metoda `static` jest ukrywana, `private` nie jest dziedziczona, `final` nie może być nadpisana.
- `super.method()` omija zwykłą dyspozycję i startuje od bezpośredniego nadtypu.
- **Wniosek:** Pull Up Method może zrobić z metody zewnętrznej podklasy nieplanowany override.

---

## 2.2-2.3. Overloading i pola - wybór statyczny

- **Przeciążenie** wybiera kompilator według typów statycznych, nie klasy runtime argumentu.
- Po Pull Up ten sam tekst kodu może wywołać inne przeciążenie; stary `.class` woła dawną sygnaturę.
- **Pola nie są polimorficzne**: ukryte pole instancyjne to dwa niezależne sloty w jednym obiekcie.
- Przenoś pola według **znaczenia**, nie zgodności nazwy i typu.

---

## 2.4. Konstruktory i inicjalizacja

- Konstruktory nie są dziedziczone; pierwszy jawny konstruktor usuwa domyślny.
- Java 25 dopuszcza ograniczony **prolog** przed `super(...)`; inicjalizatory nadklasy i tak kończą się pierwsze.
- Konstruktor bazy może wywołać override **przed** inicjalizacją pól podklasy.
- Podklasa nie może przypisać odziedziczonego pola `final`.

---

## 2.5-2.6. Monitory, `static` i granice pakietów

- Instancyjne `synchronized` blokuje `this` - delegowanie może przenieść blokadę na delegata.
- `static synchronized` blokuje `Class` typu deklarującego - Pull Up **zmienia monitor**.
- Przeniesienie pola `static` może zmienić moment inicjalizacji klasy.
- Członek pakietowy nie jest dziedziczony poza pakietem - ta sama sygnatura to nie override.
- `protected` poza pakietem działa tylko przez odbiorcę typu podklasy.

---

## 2.7-2.8. Generyki, metody bridge i hierarchie `sealed`

- Sygnaturę analizuj **po erasure** - dwie deklaracje mogą mieć ten sam deskryptor.
- Kowariantny override tworzy metody bridge - porównaj wynik `javap -p -s -v`.
- `permits` obejmuje tylko bezpośrednie podtypy - pośrednia nadklasa zmienia oba poziomy.
- Nowy wariant `sealed` bywa zgodny binarnie, ale stary wyczerpujący `switch` rzuci `MatchException`.
- Rekord nie dostanie nowej nadklasy - zawsze rozszerza `Record`.

---

## 3.1. Pull Up Method

- Kontrakt trafia na **najniższy poziom, na którym jest prawdziwy dla wszystkich potomków**.
- Identyczne ciało nie oznacza tego samego zachowania - porównuj **kontrakty**, nie tekst.
- Warunki: tylko stan dostępny w bazie, brak kolizji po erasure i przypadkowego override.
- Procedura: ujednolić sygnatury → punkt rozszerzenia → jedna implementacja w bazie → testy przez typ bazowy.
- Nie wywołuj metody overridable z konstruktora tylko po to, by umożliwić Pull Up.

---

## 3.2. Pull Up Field - warunki i procedura

- Łącz pola tylko przy tym samym **znaczeniu**, typie, cyklu życia, walidacji i momencie inicjalizacji.
- Pole w bazie zwykle **`private`**, ustawiane przez `super(...)` - nie surowe `protected`.
- Znajdź wszystkie deklaracje nazwy, odczyty, zapisy, rzutowania i `super.field`.
- Dodaj pole i parametr konstruktora bazy, przekieruj metody do wspólnego stanu.
- Pola podklas usuń po zniknięciu odwołań; sprawdź inicjalizację, serializację, stare binaria.

---

## 3.3. Ryzyka Pull Up

- Pola `static` mogą scalić dwa stany; inicjalizatory z efektami wykonają się wcześniej.
- Pole `final` przypisuje tylko klasa deklarująca; metody z `super` zmieniają bezpośredni nadtyp.
- `static synchronized` zmienia monitor, a metody generyczne - deskryptor i bridge.
- Refleksja (`getDeclaredField`) i haki serializacji zobaczą inny typ deklarujący.
- Zgodność binarna Pull Up **nie gwarantuje zgodności zachowania**.

---

## 3.4. Push Down Method

- Przenosi członka do gałęzi, która **rzeczywiście go potrzebuje** - zawęża zbyt szeroki kontrakt.
- Sygnały: `null` i flagi bez znaczenia, `UnsupportedOperationException`, użycie tylko po `instanceof`.
- Kilka podklas gałęzi → **najbliższy wspólny przodek**, nie kopie w liściach.
- Sekwencja: klienci na podtyp → metoda w podklasie → testy → usunięcie metody bazowej.
- W bibliotece łamie API: stary `.class` szuka metody w nadklasie, nie w podklasach.

---

## 3.5. Push Down Field i asymetria przesunięć

- Najpierw przenieś **zachowanie** korzystające z pola, potem samo pole.
- Deklaracje w bazie i podklasie naraz to dwa niezależne sloty - nie da się tak emulować przeniesienia.
- **Pull Up i Push Down nie są symetryczne**: stary odnośnik do metody podklasy może trafić do nadklasy, nie odwrotnie.
- Ruch w dół publicznego członka wymaga jawnej migracji API i planu dla danych.

---

## 4.1. Extract Superclass

- Właściwy sygnał to **wspólny kontrakt** wariantów jednego pojęcia - podobne linie kodu to za mało.
- Nazwa z `Base`, `Common`, `Abstract` często zdradza ekstrakcję motywowaną tylko duplikacją.
- Procedura: pusta abstrakcyjna nadklasa → dołączaj klasy **pojedynczo** → Pull Up Field → Pull Up Method.
- Konstruktory nie przechodzą - każdą publiczną sygnaturę świadomie zachowaj lub zmigruj.
- Nowy poziom zmienia `getSuperclass`, typ deklarujący członków i założenia mapperów.

---

## 4.2. Extract Subclass

- Podklasa dla **stabilnego podzbioru instancji** z dodatkowym stanem lub zachowaniem.
- Sygnały: pole opcjonalne dla części obiektów, powtarzane sprawdzenie, tworzenie przez fabryki.
- Rola zmienna w czasie życia → **State lub Strategy**; wiele osi zmienności → kompozycja.
- Procedura: warianty w testach → punkty tworzenia → podklasa → Push Down → usunięcie warunku.
- Celowo **zmienia klasę runtime**: `getClass()`, `equals`, ORM, JSON, serializacja, `switch`.

---

## 4.3. Extract Interface

- Interfejs to **rola określonej grupy klientów** - nie kopia całego publicznego API klasy.
- `implements` wymusza sygnatury, **nie zachowanie** - wszystkie implementacje przechodzą jeden test kontraktowy.
- Procedura: podzbiór API klientów → interfejs + `implements` bez zmiany sygnatur → migracja klientów pojedynczo.
- Parametr z klasy na interfejs **zmienia deskryptor JVM** - w bibliotece zostaw stare przeciążenie delegujące.

---

## 4.4. Metody domyślne

- `default` musi być poprawna dla każdej implementacji i używać tylko operacji kontraktu.
- Metoda klasy wygrywa z `default`; konflikt niespokrewnionych interfejsów trzeba rozstrzygnąć jawnie.
- `default` nie zastąpi `equals`/`hashCode`, a sekwencja wywołań nie staje się atomowa.
- Nowa metoda abstrakcyjna w opublikowanym interfejsie może dać `AbstractMethodError` w starych implementacjach.

---

## 5.1. Collapse Hierarchy

- Scala poziomy, gdy rozróżnienie nie jest już kontraktem, wariantem ani punktem rozszerzenia.
- Sygnały: podklasa bez stanu i zachowania, override tylko woła `super`, jeden potomek.
- Pusty typ może być **markerem**, punktem rozszerzenia, typem protokołu lub kontraktem DI.
- Usunięcie publicznej klasy łamie klientów - przejściowo cienki, przestarzały typ zgodności.

---

## 5.2. Replace Inheritance with Composition - kiedy?

- Podklasa **nie jest zastępowalnym wariantem**, a `extends` służy do ponownego użycia kodu.
- Odziedziczone API jest szersze niż potrzeby klientów lub trzeba blokować odziedziczone operacje.
- Zachowanie ma być wymienne w czasie życia lub łączone z innymi.
- Prawdziwe, stabilne podtypowanie nadal uzasadnia dziedziczenie.

---

## 5.3. Procedura i pułapki delegowania

- Zinwentaryzuj **całe** odziedziczone API, dodaj delegata, przekazuj tylko potrzebne operacje, usuń `extends`.
- Bez `extends ArrayList<String>` obiekt przestaje być `List` - to **nie jest zgodny zamiennik** publicznego `List`.
- Hook woła się na delegacie, nie na `this`; metoda fluent może zwrócić **delegata zamiast wrappera**.
- `synchronized` blokuje inny monitor; znikają odziedziczone `equals`/`hashCode`.
- Test kontraktowy nie wykryje liczby wywołań, tożsamości `this` ani monitora - potrzebne spy i callback.

---

## 6.1. Macierz typowych skutków

| Transformacja | Typowe ryzyko dla publicznego kontraktu |
| --- | --- |
| Pull Up Method/Field | przypadkowy override, monitor `static`, scalenie slotów |
| Push Down Method/Field | klient bazowy traci członka; kopia pola = dwa stany |
| Extract Superclass/Subclass | nowy nadtyp lub typ runtime, konstruktory, ORM, serializacja |
| Extract Interface | deskryptory parametrów/wyników, metody domyślne |
| Collapse Hierarchy | usunięta nazwa klasy, utrata nadtypu i konfiguracji |
| Replace Inh. with Composition | utrata przypisywalności, API, hooków, monitora |

---

## 6.2. Zgodność binarna, refleksja i adnotacje

- Test biblioteki: klient skompilowany przeciw `v1` uruchomiony z `v2` oraz ponownie skompilowany.
- Sam pełny build sprawdza głównie **zgodność źródłową**, nie starą wtyczkę.
- Przeniesienie członka zmienia `getDeclaredMethods()`, `getDeclaringClass()` i metody bridge.
- `@Inherited` działa tylko dla adnotacji klas z nadklasy - nie z interfejsów i nie dla metod.

---

## 6.3. Serializacja Javy

- Postać serializowana jest **osobna dla każdego poziomu** - przeniesienie pola zmienia segment strumienia.
- Bez migracji historyczna wartość może zostać odrzucona, a nowe pole dostanie wartość domyślną.
- Stały `serialVersionUID` nie przenosi stanu między segmentami.
- Testuj plik `.ser` z `v1` czytany przez `v2`; format utrwal `serialPersistentFields` lub `writeObject`/`readObject`.
- Deserializacja uruchamia konstruktor pierwszej **nieserializowalnej** nadklasy.

---

## 6.4. ORM, DI i testy wysokiego ryzyka

- Hierarchia encji to migracja modelu: dyskryminator, tabele, zapytania polimorficzne, proxy.
- Test ORM z prawdziwym dostawcą: zapis, `flush`, czyszczenie kontekstu, ponowny odczyt.
- DI zależy od nazw, konstruktorów, adnotacji i proxy - uruchom kontekst aplikacji, nie tylko `new`.
- Minimum: test kontraktowy każdego podtypu i test różnicowy z niezależnym oczekiwaniem.
- Do tego `javap`, stary `.class` z nową wersją, historyczny plik `.ser`, `switch` dla `sealed`.

---

## 7.1. Studium przypadku: etap 0

```java
public final class SmsNotification {        // EmailNotification - prawie identyczna
    private final String messageId, senderId, body;
    private final boolean deliveryReceipt;   // w e-mailu też jest, choć bez znaczenia
    public String summary() {
        return messageId + "|" + senderId + "|" + body + "|SMS";
    }
    public String dispatch(boolean successful) {
        String result = summary() + (successful ? "|SENT" : "|FAILED");
        return successful ? appendReceipt(result) : result;
    }
    private String appendReceipt(String r) { return deliveryReceipt ? r + "|RECEIPT" : r; }
}
```

- E-mail i SMS: walidacja, trim, nadawca wielkimi literami; `RECEIPT` tylko w udanym SMS z potwierdzeniem.
- Najpierw `NotificationCharacterizationTest` - oddziela **ruch struktury** od zmian funkcjonalnych.

---

## 7.2. Etap 1: Extract Superclass oraz Pull Up

```java
abstract class Notification {
    private final String messageId, senderId, body;
    private final boolean deliveryReceipt;   // celowo niedoskonały stan przejściowy

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

`channel()` to świadomy punkt polimorficzny; `final` na `summary()` jest bezpieczne, bo baza jest pakietowa, a podklasy finalne.

---

## 7.3. Etap 2: Push Down cechy SMS

```java
public final class SmsNotification extends Notification {
    private final boolean deliveryReceipt;           // stan tylko tam, gdzie ma sens

    public SmsNotification(String id, String sender, String body, boolean receipt) {
        super(id, sender, body);
        this.deliveryReceipt = receipt;
    }
    @Override public String dispatch(boolean successful) {
        String result = dispatchResult(successful);
        return successful ? appendReceipt(result) : result;
    }
    private String appendReceipt(String r) { return deliveryReceipt ? r + "|RECEIPT" : r; }
}
```

- Baza odzyskuje kontrakt prawdziwy dla wszystkich podtypów.
- E-mail zachowuje przejściowy konstruktor 4-argumentowy i dostaje docelowy 3-argumentowy.

---

## 7.4. Etap 3: Extract Interface i test równoważności

```java
@FunctionalInterface
public interface OutboundNotification {
    String dispatch(boolean successful);
}
```

- `NotificationBatch` potrzebuje tylko `dispatch` - bez identyfikatora, podsumowania i `channel()`.
- Walidacja całej listy **przed** pierwszą wysyłką zapobiega częściowemu efektowi.
- Test równoważności porównuje każdy etap z **niezależnym oczekiwaniem**, nie „przed” z „po”.
- Samo `before.equals(after)` przepuści błąd obecny w obu wersjach.

---

## 7.5. Extract Subclass: zadanie zaplanowane

```java
public class DeliveryJob {
    public static DeliveryJob immediate() { return new DeliveryJob(); }
    public static DeliveryJob scheduled(Instant at) { return new ScheduledDeliveryJob(at); }
    public String dispatchAt(Instant now) { Objects.requireNonNull(now); return "SENT"; }
}
public final class ScheduledDeliveryJob extends DeliveryJob {
    private final Instant scheduledAt;
    @Override public String dispatchAt(Instant now) {
        Objects.requireNonNull(now, "now must not be null");
        if (now.isBefore(scheduledAt)) return "WAITING_UNTIL " + scheduledAt;
        return super.dispatchAt(now);
    }
}
```

- Przed: jedna klasa z polem `Optional<Instant>`; po: fabryka nadal zwraca `DeliveryJob`.
- Termin niezmienny - wariant **stały w czasie życia**; `now == scheduledAt` daje `SENT`.

---

## 7.6. Collapse Hierarchy: zbędny formatter

```java
// przed
public class LegacyNotificationFormatter {
    public String format(String recipient, String message) { ... }
}
public final class NotificationFormatter extends LegacyNotificationFormatter { }

// po
public final class NotificationFormatter {
    public String format(String recipient, String message) {
        Objects.requireNonNull(recipient, "recipient must not be null");
        Objects.requireNonNull(message, "message must not be null");
        return "To: " + recipient + "\nMessage: " + message;
    }
}
```

Zostaje nazwa używana przez klientów; w bibliotece usunięcie `LegacyNotificationFormatter` łamie API.

---

## 7.7. Replace Inheritance with Composition: lista odbiorców

```java
// przed: public final class RecipientList extends ArrayList<String> { snapshot() }
public final class RecipientList implements Iterable<String> {
    private final List<String> recipients = new ArrayList<>();

    public boolean add(String r)         { return recipients.add(r); }
    public boolean remove(Object r)      { return recipients.remove(r); }
    @Override public Iterator<String> iterator() {
        return Collections.unmodifiableList(recipients).iterator();
    }
    public List<String> snapshot() {
        return Collections.unmodifiableList(new ArrayList<>(recipients));
    }
}
```

- Test sprawdza duplikaty, kolejność, `remove(Object)` i izolację migawek.
- Świadomie tracone: przypisywalność do `List`, `set`, `sort`, `remove(int)`, `equals`/`hashCode`.

---

## Ćwiczenie 1: Extract Superclass i Pull Up (45 min)

- Cel: w `stage0` utworzyć wspólną `Notification`, zachowując wyniki i walidację obu kanałów.
- Dołączaj klasy pojedynczo, testy po każdym kroku.
- Te same sygnatury konstruktorów; brak surowych pól `protected`.

Szczegóły: zadania modułu 5, Ćwiczenie 1.

---

## Ćwiczenie 2: Push Down i Extract Interface (45 min)

- Cel: w `stage1` usunąć potwierdzenie z bazy i wydzielić rolę dla `NotificationBatch`.
- Najpierw zachowanie `appendReceipt`, potem pole; przejściowy konstruktor e-mail zostaje.
- `NotificationBatch` zależy tylko od `OutboundNotification`.

Szczegóły: zadania modułu 5, Ćwiczenie 2.

---

## Ćwiczenie 3: Extract Subclass i Collapse Hierarchy (45 min)

- **A:** `DeliveryJob` → `ScheduledDeliveryJob`; jawny test terminu granicznego, stan specjalny tylko w podtypie.
- **B:** usuń zbędny poziom formattera, zachowując nazwę używaną przez klientów.
- Nazwij zmianę klasy runtime i etap przejściowy dla typów publicznych.

Szczegóły: zadania modułu 5, Ćwiczenie 3.

---

## Ćwiczenie 4: Replace Inheritance with Composition (45 min)

- Cel: zastąpić `extends ArrayList` wąską fasadą bez obietnicy pełnego `List`.
- Każdy wrapper ma własnego delegata; migawki są izolowane.
- Wypisz utracone operacje i plan dla klientów wymagających `List`.

Szczegóły: zadania modułu 5, Ćwiczenie 4.

---

## Zadanie 5: retrospektywa (10 min po ćwiczeniu lub 20 min na koniec)

Zespół odpowiada po jednym zdaniu: jaki kontrakt chroniliśmy, który krok wymagał decyzji projektowej, jaki test wykryłby najgroźniejszą regresję, której warstwy zgodności nie sprawdziliśmy?

**Zasada:** końcowa hierarchia ma opisywać domenę, a nie tylko ponownie używać kodu.

Szczegóły: zadania modułu 5, Zadanie 5.

---

## Kluczowe wnioski z omówienia

- `channel()` to jedyna różnica potrzebna wspólnemu algorytmowi - bez pól `protected` i getterów „na zapas”.
- `deliveryReceipt` w bazie zachowuje wyniki, ale jest sygnałem złego modelu.
- W Push Down najpierw przenieś zachowanie, potem stan; w Extract Subclass - najpierw punkt tworzenia.
- Bez testu granicznego zamiana `isBefore` na inny operator przeszłaby niezauważona.
- Ujawnienie `remove(int)` w `RecipientList` sprawiłoby, że literał liczbowy usuwałby po indeksie.

Pełne omówienie: rozwiązania modułu 5.

---

## Sprawdzenie wiedzy

1. Dlaczego podobny kod nie wystarcza do Extract Superclass?
2. Czym overriding różni się od overloading?
3. Jaki monitor blokuje `static synchronized`?
4. Dlaczego Push Down publicznej metody łamie stare binaria?
5. Kiedy Extract Subclass przegrywa ze State lub Strategy?
6. Dlaczego zmiana parametru z klasy na interfejs łamie stare `.class`?
7. Dlaczego `serialVersionUID` nie wystarcza po Pull Up Field?

---

## Lista kontrolna

- **Przed:** znany kontrakt, klienci i wszystkie podtypy; sprawdzone `super`, `static`, erasure, refleksja, ORM, DI, serializacja.
- **Pull Up / Push Down:** ten sam kontrakt i znaczenie pól, pole bazowe prywatne, członek w najbliższej wspólnej gałęzi.
- **Ekstrakcje:** nazwa opisuje pojęcie lub rolę klienta, konstruktory i deskryptory świadomie zachowane.
- **Collapse / Composition:** usuwany poziom nie jest markerem; znane całe odziedziczone API, hooki i monitor.
- **Na koniec:** testy przechodzą, jedno źródło stanu, zmiana łamiąca nazwana i zaplanowana osobno.

---

## Podsumowanie - najważniejsze wnioski

- Refaktoryzacja hierarchii jest bezpieczna, gdy **struktura typów odpowiada kontraktom**.
- Pull Up centralizuje prawdziwie wspólne elementy; Push Down usuwa z bazy cechy części obiektów.
- Extract Superclass/Subclass zmieniają model typów; Extract Interface wydziela rolę klienta.
- Collapse usuwa zbędne rozróżnienie; kompozycja ogranicza fałszywe podtypowanie.
- Metody, przeciążenia i pola mają różne reguły wyboru - składnia nie dowodzi zgodności binarnej ani danych.
- Rozdzielaj decyzje: **gdzie stan i zachowanie, jaki typ widzi klient, jaka zgodność zostaje**.
