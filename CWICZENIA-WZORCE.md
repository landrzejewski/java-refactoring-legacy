# Ćwiczenia utrwalające – które wzorce, dlaczego i gdzie

Ten plik jest dla prowadzącego i **zdradza rozwiązania** ćwiczeń `e01`–`e10`.

Każde ćwiczenie leży w `src/main/java/pl/training/workshop/exercises/eNN_nazwa/` i ma:

- `start/` – kod do refaktoryzacji (od tego zaczyna uczestnik),
- `step1/`, `step2/`, ... – gotowy stan po kolejnych krokach (rozwiązanie),
- klasy bezpośrednio w pakiecie `eNN_nazwa` – wspólny kontrakt (dane wejściowe/wyjściowe); tych klas się nie zmienia.

Ścieżki klas w tym pliku są podane względem pakietu ćwiczenia, np. `start/RepertoireImporter` to
`exercises/e01_import/start/RepertoireImporter.java`.

Przydatne komendy (więcej w `README.md`):

```bash
scripts/warsztat.sh test exercises/e01      # testy ćwiczenia
scripts/warsztat.sh next exercises/e01      # wgraj następny krok do start
scripts/warsztat.sh diff exercises/e01 0 1  # co zmienia krok 1 względem start
scripts/warsztat.sh reset exercises/e01     # przywróć start
```

## Spis

| Ćwiczenie | Wzorce (w kolejności kroków) | Od których klas w `start` zacząć |
|---|---|---|
| [e01 Import repertuaru](#e01-import-repertuaru-e01_import) | Metoda szablonowa → Metoda wytwórcza | `RepertoireImporter`, `RepertoireImport` |
| [e02 Mapa sali](#e02-mapa-sali-e02_hallmap) | Kompozyt → Iterator → Pyłek | `HallMaps`, `Hall`, `Section`, `Row`, `Seat` |
| [e03 Powiadomienia](#e03-powiadomienia-o-zmianach-seansu-e03_notifications) | Obserwator → Łańcuch zobowiązań | `ScreeningScheduler`, `*Notice` (6 klas) |
| [e04 Wybór miejsc](#e04-wybór-miejsc-z-cofaniem-e04_seatpicker) | Polecenie → Pamiątka | `SeatPicker` |
| [e05 Karta podarunkowa](#e05-karta-podarunkowa-i-vouchery-e05_giftcard) | Stan → Interpreter | `GiftCard`, `Vouchers` |
| [e06 Informacje o filmie](#e06-informacje-o-filmie-e06_movieinfo) | Adapter → Pełnomocnik → Singleton | `MovieInfoService`, `AgeRatingTable` |
| [e07 Bilety i paragony](#e07-bilety-i-paragony-e07_documents) | Fabryka abstrakcyjna → Dekorator | `SalesDocuments`, `DocumentComposer` |
| [e08 Cykl seansów](#e08-cykl-seansów-e08_series) | Prototyp → Budowniczy | `Screening`, `SeriesPlanner` |
| [e09 Szuflada kasowa](#e09-raport-szuflady-kasowej-e09_cashdrawer) | Odwiedzający → Strategia | `CashDrawerReport`, `DrawerEntry` |
| [e10 Przebieg seansu](#e10-przebieg-seansu-e10_showtime) | Mediator → Fasada | `HallController`, `Doors`, `AdPlayer`, `Projector`, `BoxOfficePanel`, `AutoScheduler` |

Razem 22 wzorce GoF, każdy w jednym ćwiczeniu (Mostu – Bridge – w ćwiczeniach nie ma).

---

## e01 Import repertuaru (`e01_import`)

**Problem w jednym zdaniu:** import CSV i JSONL to ta sama metoda skopiowana dwa razy, a format pliku jest rozpoznawany w dwóch miejscach.

**Wzorce:** Metoda szablonowa → Metoda wytwórcza

### Co jest nie tak w `start`
- `RepertoireImporter.importCsv()` i `importJsonLines()` to ta sama pętla (numer wiersza, pomijanie pustych linii, wspólna walidacja, sortowanie) skopiowana dwa razy. Różnią się tylko znakiem komentarza (`#` / `//`) i podziałem wiersza na pola.
- Trzeci format (np. XML) oznaczałby trzecią kopię tej pętli.
- `RepertoireImporter.importFile()` wybiera format po rozszerzeniu pliku, a `RepertoireImport.load()` sprawdza rozszerzenie drugi raz, tylko żeby wypisać etykietę `[CSV]` / `[JSONL]`.

### Krok 1: Metoda szablonowa (Template Method)
> Wspólny przebieg importu jest raz, w klasie bazowej; podklasy dopisują tylko to, co się różni.

**Przed** (`start/RepertoireImporter`):
```java
public List<ScreeningEntry> importCsv(List<String> lines) {
    for (String raw : lines) {
        String line = raw.strip();
        if (line.isEmpty() || line.startsWith("#")) {
            continue;
        }
        String[] fields = line.split(";", -1);
        entries.add(toEntry(number, fields[0].strip(), fields[1].strip(), ...));
    }
    entries.sort(...);
}
// importJsonLines(): ta sama pętla, tylko "//" i rozbiór obiektu JSON
```
**Po** (`step1/RepertoireImporter`):
```java
public final List<ScreeningEntry> importLines(List<String> lines) {
    for (String raw : lines) {
        ...
        if (line.isEmpty() || isComment(line)) {
            continue;
        }
        entries.add(toEntry(number, parseLine(line, number)));
    }
    ...
}
protected abstract boolean isComment(String line);
protected abstract RawScreening parseLine(String line, int number);
```
**Jak to zrobić:**
1. `RepertoireImporter` → klasa abstrakcyjna z abstrakcyjnymi `isComment(String)` i `parseLine(String, int)`.
2. Pętlę z `importCsv()` przenieś do `public final importLines()`; zamiast `startsWith("#")` wołaj `isComment()`, zamiast `split` – `parseLine()`. `final` pilnuje, żeby żaden format nie zmienił kolejności kroków.
3. Dodaj rekord `RawScreening(title, format, hall, start)` – surowe pola wiersza; `toEntry()` przyjmuje teraz `RawScreening` i nadal robi wspólną walidację.
4. Utwórz `CsvRepertoireImporter` i `JsonLinesRepertoireImporter`: tylko `isComment()` i `parseLine()`, treść wzięta z dawnych `importCsv()` / `importJsonLines()`.
5. `importFile()` staje się statyczne i tworzy właściwą podklasę; `RepertoireImport.load()` woła `RepertoireImporter.importFile(...)`. Podwójne sprawdzanie rozszerzenia jeszcze zostaje – to krok 2.

**Efekt:** pętla importu jest w jednym miejscu; nowy format = 1 podklasa z 2 metodami.

### Krok 2: Metoda wytwórcza (Factory Method)
> Importer nie czyta wiersza sam, tylko tworzy swój parser – a podklasa decyduje, jaki.

**Przed** (`step1/RepertoireImport`):
```java
public String load(ImportRequest request) {
    String label;
    if (request.fileName().endsWith(".csv")) {
        label = "CSV";
    } else if (request.fileName().endsWith(".jsonl")) {
        label = "JSONL";
    } else { ... }
    List<ScreeningEntry> entries = RepertoireImporter.importFile(request.fileName(), request.lines());
    ...
}
// RepertoireImporter.importFile() sprawdza te same rozszerzenia drugi raz
```
**Po** (`step2/RepertoireImporter`, `step2/CsvRepertoireImporter`, `step2/RepertoireImport`):
```java
public final List<ScreeningEntry> importLines(List<String> lines) {
    LineParser parser = createLineParser();
    ...
        if (line.isEmpty() || parser.isComment(line)) { ... }
        entries.add(toEntry(number, parser.parse(line, number)));
    ...
}
// CsvRepertoireImporter
public String label() { return "CSV"; }
protected LineParser createLineParser() { return new CsvLineParser(); }
// RepertoireImport.load()
RepertoireImporter importer = Importers.forFile(request.fileName());
```
**Jak to zrobić:**
1. Utwórz interfejs `LineParser` z `isComment(String)` i `parse(String, int)`.
2. Przenieś treść `isComment()` / `parseLine()` z importerów do nowych `CsvLineParser` i `JsonLineParser`.
3. W `RepertoireImporter` dodaj `protected abstract LineParser createLineParser()` i `public abstract String label()`; `importLines()` na początku woła `createLineParser()` i dalej używa parsera (to jest metoda wytwórcza).
4. W `CsvRepertoireImporter` i `JsonLinesRepertoireImporter` zostają tylko `createLineParser()` i `label()`.
5. Utwórz `Importers.forFile()` – jedyne miejsce, które zna rozszerzenia (prosta fabryka, to **nie** jest wzorzec GoF); usuń `importFile()`.
6. `RepertoireImport.load()`: `Importers.forFile()` i `importer.label()` zamiast własnego `if`.

**Efekt:** rozszerzenia są sprawdzane w jednym miejscu, a parser da się przetestować i użyć bez importera (np. podgląd wiersza w edytorze).

### Dlaczego nie inne wzorce
- **Strategia?** Wygląda podobnie, bo parser to wymienny sposób czytania wiersza. Ale tutaj nikt z zewnątrz go nie wybiera ani nie wstrzykuje – parser jest na stałe związany z podklasą importera (CSV zawsze czyta CSV), więc wystarczy, że podklasa go tworzy.
- **Fabryka abstrakcyjna?** Wygląda podobnie, bo importer tworzy obiekty zależne od formatu. Ale tutaj powstaje jeden rodzaj obiektu (parser), a nie rodzina kilku pasujących do siebie obiektów, więc wystarczy jedna metoda wytwórcza.
- **Sama Metoda szablonowa (stop po kroku 1)?** Wygląda na wystarczającą, bo skopiowana pętla już znika. Ale tutaj zostaje podwójne sprawdzanie rozszerzenia, a parsera nie da się użyć bez importera, więc robimy krok 2.
- **Budowniczy?** Wygląda podobnie, bo z surowych pól powstaje `ScreeningEntry`. Ale tutaj zamiana `RawScreening` na `ScreeningEntry` to jedna konwersja z walidacją, a nie składanie obiektu krok po kroku, więc wystarczy `toEntry()`.

### Nie ruszamy
`ImportRequest`, `ScreeningEntry`.

---

## e02 Mapa sali (`e02_hallmap`)

**Problem w jednym zdaniu:** klient cztery razy ręcznie chodzi po drzewie sala → sektor → rząd → miejsce, a każdy fotel nosi własną kopię danych swojej kategorii.

**Wzorce:** Kompozyt → Iterator → Pyłek

### Co jest nie tak w `start`
- `HallMaps.describe()` ma cztery potrójne pętle (sala → sektory → rzędy → miejsca), każda z `if (seat == null) continue;`. Liczą pojemność, sumę dopłat, liczbę miejsc w kategoriach i listę etykiet. Nowy poziom (np. balkon z sektorami) albo nowe pytanie o salę = kolejna kopia pętli.
- `Hall`, `Section` i `Row` nie mają wspólnego typu, więc klient musi znać każdy poziom.
- `Row` trzyma miejsca w tablicy `Seat[]`, w której `null` oznacza przejście.
- `Row.seatFor()` kopiuje wszystkie dane kategorii (kod, nazwa, dopłata, ikona, opis) do każdego `Seat` – w sali na 400 miejsc to 400 kopii tych samych danych.

### Krok 1: Kompozyt (Composite)
> Fotel, rząd, sektor i sala odpowiadają na te same pytania, a grupa po prostu sumuje odpowiedzi swoich dzieci.

**Przed** (`start/HallMaps`):
```java
int capacity = 0;
for (Section section : hall.sections()) {
    for (Row row : section.rows()) {
        for (Seat seat : row.places()) {
            if (seat == null) {
                continue;
            }
            capacity++;
        }
    }
}
// ... ta sama potrójna pętla jeszcze raz dla dopłat (surcharges)
```
**Po** (`step1/HallGroup`, `step1/HallMaps`):
```java
public abstract class HallGroup implements HallElement {
    private final List<? extends HallElement> children;
    public final int capacity() {
        int capacity = 0;
        for (HallElement child : children) {
            capacity += child.capacity();
        }
        return capacity;
    }
    // surcharges() tak samo: sumuje child.surcharges()
}
// HallMaps: "pojemnosc " + hall.capacity() + ", doplaty " + hall.surcharges()
```
**Jak to zrobić:**
1. Utwórz interfejs `HallElement` z `capacity()` i `surcharges()`.
2. Utwórz klasę abstrakcyjną `HallGroup`: niemutowalna lista dzieci podana w konstruktorze, `capacity()` i `surcharges()` sumują dzieci. Bez `add`/`remove` – drzewo powstaje raz.
3. `Hall`, `Section` i `Row` dziedziczą po `HallGroup` (w konstruktorze `super(sections)` / `super(rows)` / `super(seats)`).
4. `Row` trzyma `List<Seat>` zamiast `Seat[]`: przejście `_` po prostu nie tworzy miejsca (numer miejsca nadal wynika z pozycji w układzie).
5. `Seat implements HallElement`: `capacity()` zwraca 1, `surcharges()` zwraca własną dopłatę (to jest liść drzewa).
6. `HallMaps`: pętle pojemności i dopłat zastąp `hall.capacity()` i `hall.surcharges()`.

**Efekt:** dwie z czterech pętli znikają, a nowy poziom w drzewie nie wymaga zmian w kliencie.

### Krok 2: Iterator
> Osobny obiekt podaje miejsca po kolei, więc klient nie musi wiedzieć, ile poziomów ma sala.

**Przed** (`step1/HallMaps`):
```java
Map<String, Integer> counts = new LinkedHashMap<>();
for (Section section : hall.sections()) {
    for (Row row : section.rows()) {
        for (Seat seat : row.seats()) {
            counts.merge(seat.categoryName(), 1, Integer::sum);
        }
    }
}
StringJoiner seats = new StringJoiner(", ");
// ... ta sama potrójna pętla jeszcze raz dla listy etykiet
```
**Po** (`step2/SeatIterator`, `step2/HallMaps`):
```java
// SeatIterator.advance(): grupę wkłada na stos, miejsce oddaje klientowi
HallElement element = level.next();
if (element instanceof HallGroup group) {
    path.push(group.children().iterator());
} else {
    next = (Seat) element;
}
// HallMaps.describe(): Hall implements Iterable<Seat>, więc wystarczy for-each
for (Seat seat : hall) {
    counts.merge(seat.categoryName(), 1, Integer::sum);
    seats.add(seat.label() + " " + seat.icon());
}
```
**Jak to zrobić:**
1. Utwórz `SeatIterator implements Iterator<Seat>`: trzyma stos (`Deque`) iteratorów po `children()`, schodzi w głąb i zwraca tylko miejsca.
2. `Hall implements Iterable<Seat>`; `iterator()` zwraca `new SeatIterator(this)`.
3. Usuń `Hall.sections()`, `Section.rows()` i `Row.seats()` razem z polami, które je trzymały. Listę dzieci czyta już tylko `SeatIterator` (przez `HallGroup.children()`).
4. `HallMaps`: dwie potrójne pętle zastąp jedną `for (Seat seat : hall)`.

**Efekt:** klient nie zna już sektorów ani rzędów; kolejność miejsc i pomijanie przejść należą do sali.

### Krok 3: Pyłek (Flyweight)
> Fotele tej samej kategorii wskazują na jeden wspólny, niezmienny obiekt z danymi kategorii zamiast trzymać własne kopie.

**Przed** (`step2/Row`, `step2/Seat`):
```java
// Row.seatFor(): każdy fotel dostaje własną kopię danych kategorii
return switch (code) {
    case 'N' -> new Seat(label, 'N', "Normalne", Money.ZERO, "o", "Miejsce standardowe");
    case 'V' -> new Seat(label, 'V', "VIP", Money.of("10.00"), "*", "Fotel skorzany z szerszym rozstawem");
    ...
};
// Seat
private final String label;
private final char code;
private final String categoryName;
private final Money surcharge;
... // icon, description
```
**Po** (`step3/SeatCategories`, `step3/Seat`, `step3/Row`):
```java
// SeatCategories: jedna instancja na kod kategorii
private static final Map<Character, SeatCategory> SHARED = new ConcurrentHashMap<>();
public static SeatCategory of(char code) {
    ...
    category = SHARED.computeIfAbsent(code, SeatCategories::create);
    ...
}
// Seat: tylko własna etykieta i odwołanie do wspólnej kategorii
private final String label;
private final SeatCategory category;
// Row.of()
seats.add(new Seat(spec.label() + (i + 1), SeatCategories.of(code)));
```
**Jak to zrobić:**
1. Utwórz niezmienną klasę `SeatCategory` (kod, nazwa, dopłata, ikona, opis). Nie wie, w którym rzędzie stoi fotel (to jest pyłek – stan wspólny).
2. Utwórz `SeatCategories` z mapą kod → kategoria i metodą `of(char)` (`computeIfAbsent`); przenieś tu `switch` z `Row.seatFor()` jako prywatne `create()`.
3. `Seat`: zostaw tylko `label` (stan własny fotela) i `category`; `surcharges()` zwraca `category.surcharge()`.
4. `Row.of()`: `new Seat(etykieta, SeatCategories.of(code))`; usuń `seatFor()`.
5. `HallMaps`: czytaj `seat.category().name()` i `seat.category().icon()`.

**Efekt:** 400 foteli VIP wskazuje na jeden obiekt kategorii, a wynik `describe()` jest taki sam jak w `start`.

### Dlaczego nie inne wzorce
- **Odwiedzający?** Wygląda podobnie, bo na drzewie wykonujemy kilka różnych operacji. Ale tutaj żadna operacja nie potrzebuje innej obsługi sali, sektora i rzędu – sumy daje Kompozyt, a miejsca po kolei daje Iterator, więc Odwiedzający nic by nie dodał.
- **Dekorator?** Wygląda podobnie, bo `HallGroup` trzyma elementy tego samego typu co on sam. Ale tutaj grupa ma **wiele** dzieci i buduje drzewo, a nie owija jednego obiektu, żeby dodać mu zachowanie, więc to Kompozyt.
- **Singleton?** Wygląda podobnie, bo kategoria jest współdzielona. Ale tutaj jest po jednej instancji **na kod kategorii**, a nie jedna w całym programie, więc to Pyłek.
- **Prototyp?** Wygląda podobnie, bo fotele tej samej kategorii są prawie identyczne i kusi klonowanie wzorcowego fotela. Ale tutaj klonowanie nadal dałoby 400 kopii, a problem rozwiązuje współdzielenie, nie tańsze kopiowanie, więc Prototyp nie pomaga.

### Nie ruszamy
`HallSpec`, `SectionSpec`, `RowSpec`.

---

## e03 Powiadomienia o zmianach seansu (`e03_notifications`)

**Problem w jednym zdaniu:** harmonogram sam powiadamia wszystkich zainteresowanych, wybór kanału jest skopiowany, a wiadomości to siatka 6 klas (kanał × rodzaj).

**Wzorce:** Obserwator → Łańcuch zobowiązań

### Co jest nie tak w `start`
- `ScreeningScheduler` ma na sztywno pola `Waitlist` i `ChangeStats`. `move()` i `cancel()` po kolei walidują, piszą do dziennika, powiadamiają klientów, wołają listę oczekujących i statystyki. Nowy odbiorca zmian (np. kasa biletowa) = edycja tej klasy w dwóch miejscach.
- Wybór kanału (ważny token push → SMS, gdy jest telefon → e-mail → `UNDELIVERED`) to ten sam łańcuch `if` skopiowany w `move()` i `cancel()`.
- Sześć klas `EmailMovedNotice`, `SmsMovedNotice`, `PushMovedNotice`, `EmailCancelledNotice`, `SmsCancelledNotice`, `PushCancelledNotice`: treść tej samej wiadomości jest w 3 klasach, skracanie SMS do 70 znaków w 2 klasach, a nowy rodzaj wiadomości to 3 nowe klasy.

### Krok 1: Obserwator (Observer)
> Harmonogram tylko ogłasza „seans przesunięty / odwołany”, a zainteresowani sami się na to zapisują.

**Przed** (`start/ScreeningScheduler`):
```java
private final Waitlist waitlist;
private final ChangeStats stats;
public void move(String screeningId, String newStart, String newHall, List<Customer> ticketHolders) {
    journal.note("SCHEDULE " + before.title() + " " + before.start() + " -> " + newStart + ...);
    for (Customer customer : ticketHolders) {
        if (customer.pushToken() != null && ...) {
            new PushMovedNotice(journal, before, after).send(customer);
        } else if ... // SMS, e-mail, UNDELIVERED
    }
    waitlist.screeningMoved(after);
    stats.moved();
}
```
**Po** (`step1/ScreeningScheduler`, `step1/ScreeningDesk`):
```java
private final List<ScreeningListener> listeners = new ArrayList<>();
public void subscribe(ScreeningListener listener) {
    listeners.add(listener);
}
public void move(String screeningId, String newStart, String newHall, List<Customer> ticketHolders) {
    ...
    ScreeningMoved event = new ScreeningMoved(before, after, List.copyOf(ticketHolders));
    for (ScreeningListener listener : List.copyOf(listeners)) {
        listener.screeningMoved(event);
    }
}
// ScreeningDesk: subscribe(new CustomerNotifier(journal)), potem Waitlist, potem ChangeStats
```
**Jak to zrobić:**
1. Utwórz interfejs `ScreeningListener` z `screeningMoved(ScreeningMoved)` i `screeningCancelled(ScreeningCancelled)` oraz rekordy zdarzeń `ScreeningMoved(before, after, ticketHolders)` i `ScreeningCancelled(screening, reason, ticketHolders)`.
2. `ScreeningScheduler`: usuń pola `Waitlist` i `ChangeStats`, dodaj listę słuchaczy z `subscribe()` / `unsubscribe()`. `move()` i `cancel()` po wpisie `SCHEDULE` tworzą zdarzenie i wołają wszystkich słuchaczy.
3. Utwórz `CustomerNotifier implements ScreeningListener` i przenieś do niego pętle po klientach razem z łańcuchem `if` i tworzeniem `*Notice` (na razie bez zmian).
4. `Waitlist` i `ChangeStats` implementują `ScreeningListener` – ta sama logika, nowe nazwy metod (`screeningMoved(event)` zamiast `screeningMoved(after)` / `moved()`).
5. `ScreeningDesk` rejestruje słuchaczy w kolejności ze `start`: `CustomerNotifier`, `Waitlist`, `ChangeStats`. Kolejność wpisów w dzienniku jest częścią kontraktu.

**Efekt:** nowy odbiorca zmian = nowa klasa i jedno `subscribe()`, bez edycji harmonogramu.

### Krok 2: Łańcuch zobowiązań (Chain of Responsibility)
> Klient przechodzi przez kolejne kanały; pierwszy, który umie go obsłużyć, wysyła, a pozostałe nic nie robią.

**Przed** (`step1/CustomerNotifier`, `step1/SmsMovedNotice`):
```java
for (Customer customer : event.ticketHolders()) {
    if (customer.pushToken() != null && !customer.pushToken().isBlank()
            && !customer.pushToken().startsWith("expired-")) {
        new PushMovedNotice(journal, event.before(), event.after()).send(customer);
    } else if (customer.phone() != null && !customer.phone().isBlank()) {
        new SmsMovedNotice(journal, event.before(), event.after()).send(customer);
    } else if ... // e-mail, na końcu UNDELIVERED
}
// screeningCancelled(): ten sam łańcuch if, tylko klasy *CancelledNotice
// SmsMovedNotice.send(): treść wiadomości + skracanie do 70 znaków (kopia jest też w SmsCancelledNotice)
```

**Po** (`step2/SmsLink`, `step2/ScreeningDesk`, `step2/CustomerNotifier`):
```java
public void deliver(Customer customer, String subject, String body) {   // SmsLink
    if (customer.phone() != null && !customer.phone().isBlank()) {
        sms.deliver(customer, subject, body);
    } else {
        next.deliver(customer, subject, body);
    }
}
return new PushLink(new PushChannel(journal),                          // ScreeningDesk.standardDelivery()
        new SmsLink(new SmsChannel(journal),
                new EmailLink(new EmailChannel(journal), new UndeliveredLink(journal))));
String subject = "Zmiana seansu: " + event.after().title();             // CustomerNotifier.screeningMoved()
for (Customer customer : event.ticketHolders()) {
    delivery.deliver(customer, subject, body);
}
```

**Jak to zrobić:**
1. Utwórz interfejs `Channel` z jedną metodą `deliver(customer, subject, body)` – to wspólny typ kanału i ogniwa łańcucha.
2. Utwórz `PushChannel`, `SmsChannel`, `EmailChannel`: sam sposób wysyłki wzięty z klas `*Notice`. Skracanie SMS do 70 znaków jest teraz tylko w `SmsChannel`.
3. Utwórz `PushLink`, `SmsLink`, `EmailLink`: każdy dostaje swój kanał i `next`. Jeśli klient ma ten kontakt – wysyła, inaczej woła `next.deliver(...)`. Warunki przenieś z łańcucha `if` w `CustomerNotifier`.
4. Utwórz `UndeliveredLink`: zapisuje `UNDELIVERED ... (brak kanalu)` i nie ma następnika – to jawny koniec łańcucha, który można podmienić.
5. `ScreeningDesk`: dodaj `standardDelivery()`, który składa Push → SMS → E-mail → Undelivered, i przekaż wynik do `new CustomerNotifier(...)`.
6. `CustomerNotifier`: konstruktor przyjmuje `Channel delivery`; w `screeningMoved()` i `screeningCancelled()` zbuduj tytuł i treść raz, a w pętli wołaj `delivery.deliver(customer, subject, body)`. Usuń 6 klas od `EmailMovedNotice` do `PushCancelledNotice`.

**Efekt:** w `CustomerNotifier` nie ma żadnego `if`, treść każdej wiadomości i skracanie SMS są w jednym miejscu, a inna kolejność kanałów (np. festiwal: tylko e-mail) to inny łańcuch w `ScreeningDesk`.

### Dlaczego nie inne wzorce
- **Mediator zamiast Obserwatora?** Wygląda podobnie, bo harmonogram stoi „pośrodku” kilku obiektów. Ale tutaj odbiorcy nie rozmawiają ze sobą – to jednokierunkowe ogłoszenie, a nie koordynacja, więc wystarczy Obserwator.
- **Most?** Wygląda podobnie, bo wiadomości tworzą siatkę na dwóch osiach (rodzaj × kanał). Ale tutaj rodzaje wiadomości różnią się tylko tekstem – wystarczy zbudować tytuł i treść raz w `CustomerNotifier` i przekazać je do kanału, więc osobna hierarchia wiadomości byłaby nadmiarowa.
- **Fabryka abstrakcyjna?** Wygląda podobnie, bo można zrobić fabrykę na kanał, która tworzy wiadomość „przesunięty” i „odwołany”. Ale tutaj to tylko uporządkowałoby 6 klas, a nie je usunęło, więc problem zostaje.
- **Dekorator zamiast Łańcucha?** Wygląda podobnie, bo ogniwa owijają `Channel` tak jak dekoratory. Ale dekorator zawsze przekazuje dalej i coś dodaje, a tutaj działa **dokładnie jedno** ogniwo – obsłużone żądanie nie idzie dalej, więc to Łańcuch zobowiązań.

### Nie ruszamy
`Customer`, `Screening`, `Scenario`, `Change`, `Move`, `Cancel`; `Journal` celowo zostaje bez zmian.

---

## e04 Wybór miejsc z cofaniem (`e04_seatpicker`)

**Problem w jednym zdaniu:** operacje kasjera i ich cofanie są rozrzucone po dwóch wielkich `switch`/`if`, a cofanie AUTO gubi voucher.

**Wzorce:** Polecenie → Pamiątka

### Co jest nie tak w `start`
- `SeatPicker.apply()` – jeden duży `switch` na osiem operacji (SELECT, DESELECT, SWAP, GLASSES, VOUCHER, CLEAR, AUTO, UNDO). Każda gałąź zmienia koszyk i sama dopisuje wpis do historii.
- `SeatPicker.undo()` – drugi, osobny łańcuch `if`, który ręcznie odwraca każdą operację. Nowa operacja to zmiana w dwóch odległych miejscach.
- Historia to rekord `Change` z siedmioma polami „do wszystkiego”. Które pola coś znaczą, zależy od operacji.
- **Pułapka:** `autoArrange()` przed zmianą kopiuje tylko miejsca i okulary, a voucher zeruje bez zapisania. Nieudane AUTO i UNDO po AUTO gubią voucher.

### Krok 1: Polecenie (Command)
> Każda operacja kasjera staje się obiektem, który umie się wykonać i cofnąć.

**Przed** (`start/SeatPicker`):
```java
switch (op) {
    case "SELECT" -> {
        Seat seat = Seat.parse(arg(parts, 1));
        select(seat);
        history.push(new Change(op, seat, null, 0, false, null, null));
    }
    ... // DESELECT, SWAP, GLASSES, VOUCHER, CLEAR, AUTO
    case "UNDO" -> undo();
}
Change change = history.pop();   // undo(): drugi łańcuch, odwraca każdą gałąź
if (change.op().equals("SELECT")) { seats.remove(change.seat()); } else if ...
```
**Po** (`step1/Command`, `step1/SelectSeat`, `step1/SeatPicker`):
```java
public interface Command { void execute(); void undo(); }
public final class SelectSeat implements Command {
    public void execute() { cart.add(seat); }
    public void undo() { cart.remove(seat); }
}
// SeatPicker.apply(): parse() tylko tworzy polecenie, np. new SelectSeat(cart, ...)
Command command = parse(op, parts);
command.execute();
history.push(command);   // koniec apply()
history.pop().undo();    // całe SeatPicker.undo()
```
**Jak to zrobić:**
1. Wydziel z `SeatPicker` klasę `Cart`: pola `seats`, `ownGlasses`, `voucher`, walidację `checkFree()`, `state()` i `total()`. Dodaj proste operacje: `add()`, `remove()` (zwraca pozycję), `insert()`, `replace()`, `replaceAt()`, `clear()`, `replaceAll()` i settery okularów oraz vouchera (to jest odbiorca poleceń).
2. Dodaj interfejs `Command` z metodami `execute()` i `undo()`.
3. Każdą gałąź `switch` przenieś do osobnej klasy: `SelectSeat`, `DeselectSeat`, `SwapSeats`, `SetGlasses`, `SetVoucher`, `ClearSeats`, `AutoArrange`. W `execute()` polecenie zapisuje w swoim polu to, czego potrzebuje do cofnięcia (np. `index`, `before`). `undo()` to kod z odpowiedniej gałęzi starego `undo()`.
4. W `SeatPicker` zamień `switch` na `parse()`, które tylko tworzy polecenie. `apply()` woła `execute()` i odkłada polecenie na `Deque<Command> history`.
5. `SeatPicker.undo()` skróć do `history.pop().undo()` i usuń rekord `Change`.
6. `AutoArrange` przenieś bez zmian zachowania: nadal sam kopiuje miejsca i okulary. Pułapka z voucherem celowo zostaje na krok 2.

**Efekt:** nowa operacja to jedna nowa klasa i jedna linia w `parse()`, a jej wykonanie i cofnięcie leżą obok siebie.

### Krok 2: Pamiątka (Memento)
> Koszyk sam robi migawkę całego swojego stanu i sam ją przywraca; nikt inny nie zagląda do środka.

**Przed** (`step1/AutoArrange`):
```java
private List<Seat> seatsBefore = List.of();
private boolean glassesBefore;
public void execute() {
    ...
    seatsBefore = cart.seats();
    glassesBefore = cart.ownGlasses();
    cart.clear();
    cart.setOwnGlasses(false);
    cart.setVoucher(null);   // voucher znika, a nikt go nie zapisał
    ...
}
public void undo() { cart.replaceAll(seatsBefore); cart.setOwnGlasses(glassesBefore); }
```
**Po** (`step2/Cart`, `step2/AutoArrange`):
```java
public static final class Memento {          // w Cart
    private final List<Seat> seats;
    private final boolean ownGlasses;
    private final String voucher;
    private Memento(List<Seat> seats, boolean ownGlasses, String voucher) { ... }
}
public Memento snapshot() { return new Memento(seats, ownGlasses, voucher); }
public void restore(Memento memento) { ... } // przepisuje wszystkie trzy pola
private Cart.Memento before;                 // w AutoArrange
before = cart.snapshot();                    // w execute()
cart.restore(before);                        // w undo(), wołanym też przy błędzie
```
**Jak to zrobić:**
1. W `Cart` dodaj zagnieżdżoną klasę `Memento`: prywatne pola `seats`, `ownGlasses`, `voucher`, prywatny konstruktor i żadnych metod (to jest pamiątka).
2. W `Cart` dodaj `snapshot()`, które tworzy migawkę, i `restore(Memento)`, które przepisuje wszystkie pola z powrotem.
3. W `AutoArrange` zamień pola `seatsBefore` i `glassesBefore` na jedno pole `Cart.Memento before`.
4. W `AutoArrange.execute()` zamiast kopiowania pól zapisz `before = cart.snapshot()`. `undo()` (wołane też w `catch` przy błędzie) to teraz `cart.restore(before)`.
5. Usuń z `Cart` nieużywane już `replaceAll()`. Pozostałe polecenia zostaw – cofają się prostą odwrotnością.

**Efekt:** `AutoArrange` nie zna już pól koszyka, a nowe pole koszyka dopisujesz w jednym miejscu – w `Cart`.

**Świadoma zmiana zachowania:** po tym kroku voucher nie ginie ani po nieudanym AUTO, ani po UNDO po AUTO.

### Dlaczego nie inne wzorce
- **Strategia zamiast Polecenia?** Wygląda podobnie, bo każda klasa operacji „robi coś z koszykiem”. Ale tutaj obiekt niesie swoje argumenty i dane do cofnięcia, a potem trafia do historii, więc to Polecenie. Strategia to wymienny algorytm bez stanu i bez cofania.
- **Prototyp zamiast Pamiątki?** Wygląda podobnie, bo `snapshot()` robi kopię koszyka. Ale tutaj nikt nie dostaje używalnego klonu: migawka nie ma metod i czyta ją tylko `Cart`, więc to Pamiątka.
- **Interpreter dla skryptu (`"SELECT 6-4"`)?** Wygląda podobnie, bo skrypt to tekst z komendami. Ale tutaj składnia jest płaska (komenda i argumenty, bez zagnieżdżeń), więc `switch` w `parse()` wystarczy.
- **Migawka przed każdą operacją?** Wygląda kusząco, bo cofanie byłoby wszędzie takie samo. Ale tutaj proste operacje cofa się zwykłą odwrotnością, a wiele pól naraz zmienia tylko AUTO, więc migawka jest potrzebna tylko tam.

### Nie ruszamy
`HallPlan`, `PickerRun`, `Seat`, `SeatPickerSession`.

---

## e05 Karta podarunkowa i vouchery (`e05_giftcard`)

**Problem w jednym zdaniu:** status karty to tekst sprawdzany w każdej metodzie, a regułę vouchera rozbierają osobno dwie metody.

**Wzorce:** Stan → Interpreter

### Co jest nie tak w `start`
- `GiftCard` trzyma status jako `String`. Każda z metod `activate()`, `charge()`, `block()`, `expire()` zaczyna się własnym `switch (status)`, więc tabela przejść jest rozsypana po czterech metodach.
- W `GiftCard` komunikat „card is blocked” powtarza się w trzech miejscach. Nowy status albo nowa operacja to zmiana w każdym `switch`.
- `Vouchers` – reguła, np. `"format=3D AND day IN MON,TUE AND NOT premiere"`, jest rozbierana dwa razy niezależnie: w `applies()` i w `describe()`. Nowe pojęcie w regule (np. godzinę) trzeba dopisać w obu metodach, a oba rozbiory muszą się zgadzać.

### Krok 1: Stan (State)
> Każdy status karty to osobna klasa, a karta przekazuje każdą operację do bieżącego statusu.

**Przed** (`start/GiftCard`):
```java
private String status = "ISSUED";
public void charge(Money amount) {
    ... // kwota musi być dodatnia
    switch (status) {
        case "ISSUED" -> throw new IllegalStateException("card not activated");
        case "ACTIVE" -> { ... balance = balance.minus(amount); ... status = "EMPTY"; ... }
        case "EMPTY" -> throw new IllegalStateException("card is empty");
        case "BLOCKED" -> throw new IllegalStateException("card is blocked");
        case "EXPIRED" -> throw new IllegalStateException("card has expired");
        ...
    }
}   // activate(), block(), expire() – każda z własnym switch (status)
```
**Po** (`step1/CardState`, `step1/ActiveState`, `step1/GiftCard`):
```java
public interface CardState {
    String name();
    CardState activate();
    CardState charge(GiftCard card, Money amount);
    CardState block();
    CardState expire();
}
card.debit(amount);   // ActiveState.charge()
return card.balance().compareTo(Money.ZERO) == 0 ? EmptyState.INSTANCE : this;

private CardState state = IssuedState.INSTANCE;   // GiftCard
public void block() { state = state.block(); }
```
**Jak to zrobić:**
1. Dodaj interfejs `CardState` z metodami `name()`, `activate()`, `charge(GiftCard, Money)`, `block()`, `expire()`. Każda zwraca następny stan albo rzuca wyjątek.
2. Dla każdego statusu dodaj klasę: `IssuedState`, `ActiveState`, `EmptyState`, `BlockedState`, `ExpiredState`. Do każdej przenieś jej `case` ze wszystkich czterech `switch`, z tymi samymi komunikatami błędów.
3. Stany nie mają pól, więc każdy ma jedną wspólną instancję `INSTANCE` i prywatny konstruktor.
4. W `GiftCard` zamień `String status` na `CardState state = IssuedState.INSTANCE`. Każda operacja to `state = state.op(...)`, a `status()` zwraca `state.name()`.
5. Sprawdzenie dodatniej kwoty zostaw w `GiftCard.charge()` – nie zależy od stanu. Dodaj w `GiftCard` metodę pakietową `debit(Money)`; woła ją tylko `ActiveState.charge()`, które przy zerowym saldzie zwraca `EmptyState.INSTANCE`.

**Efekt:** jeden status = jedna klasa = jeden wiersz tabeli przejść; nowy status to nowa klasa.

### Krok 2: Interpreter
> Tekst reguły raz zamieniamy w drzewo obiektów, a każdy węzeł umie się ocenić i opisać.

**Przed** (`start/Vouchers`, bez zmian w kroku 1):
```java
public static boolean applies(String rule, ScreeningFacts facts) {
    for (String term : rule.split(" AND ")) {
        ... if (body.startsWith("format=")) {
            value = facts.format().equals(body.substring("format=".length()).trim());
        } else if (body.startsWith("day IN ")) { ... }
    }
}
public static String describe(String rule) {
    for (String term : rule.split(" AND ")) {   // ten sam rozbiór jeszcze raz
        ...
        if (body.startsWith("format=")) {
            text = "format " + body.substring("format=".length()).trim();
```
**Po** (`step2/VoucherRule`, `step2/FormatIs`, `step2/And`, `step2/VoucherRuleParser`, `step2/Vouchers`):
```java
public sealed interface VoucherRule permits And, Or, Not, FormatIs, DayIn, IsPremiere {
    boolean applies(ScreeningFacts facts);
    String describe();
}
public record FormatIs(String format) implements VoucherRule { ... }
public record And(VoucherRule left, VoucherRule right) implements VoucherRule { ... }
for (String term : rule.split(" AND ")) {   // VoucherRuleParser.parse()
    VoucherRule parsed = term(term.trim());
    result = result == null ? parsed : new And(result, parsed);
}
return VoucherRuleParser.parse(rule).applies(facts);   // Vouchers.applies()
```
**Jak to zrobić:**
1. Dodaj interfejs `sealed VoucherRule` z metodami `applies(ScreeningFacts)` i `describe()`.
2. Dla każdego pojęcia reguły dodaj rekord: `FormatIs`, `DayIn`, `IsPremiere`. Do każdego przenieś obie gałęzie – tę z `applies()` i tę z `describe()`.
3. Dodaj rekordy, które łączą inne reguły: `And` i `Not`. `Or` też możesz dodać – działa w kodzie, ale tekstowa składnia jeszcze go nie zna.
4. Dodaj `VoucherRuleParser.parse()` – jedyne miejsce, które zna tekst (`format=`, `day IN`, `premiere`, `NOT `, ` AND `). Kolejne pojęcia łączy w `And`, a `NOT ` zamienia na `Not`. Tu przenieś też `days()`.
5. W `Vouchers` zostaw dwie jednolinijkowe metody: `VoucherRuleParser.parse(rule).applies(facts)` i `VoucherRuleParser.parse(rule).describe()`.

**Efekt:** nowe pojęcie w regule to nowy rekord i jedna gałąź w parserze; `Vouchers` się nie zmienia.

### Dlaczego nie inne wzorce
- **Strategia zamiast Stanu?** Wygląda podobnie, bo diagram klas jest taki sam: interfejs i kilka wymiennych klas. Ale tutaj stan sam wybiera następny stan (np. `ActiveState` zwraca `EmptyState`), więc to Stan. Strategię wybiera ktoś z zewnątrz i ona sama się nie podmienia.
- **Sam Kompozyt?** Wygląda podobnie, bo `And` i `Not` zawierają inne reguły jak w drzewie. Ale tutaj drzewo powstaje z tekstowej gramatyki i jest oceniane dla konkretnych faktów (`ScreeningFacts`), więc to Interpreter.
- **Odwiedzający?** Wygląda podobnie, bo mamy dwie operacje (`applies`, `describe`) na zamkniętym drzewie. Ale tutaj będą przybywać rodzaje węzłów (nowe pojęcia), a nie operacje, więc Odwiedzający tylko by przeszkadzał – opłaca się w odwrotnej sytuacji.
- **Singleton albo Pyłek?** Wygląda podobnie, bo stany mają współdzielone `INSTANCE`. Ale tutaj to tylko skutek tego, że stany nie mają pól, więc uczymy Stanu, a współdzielenie przychodzi przy okazji.

### Nie ruszamy
`ScreeningFacts`, `GiftCards`.

---

## e06 Informacje o filmie (`e06_movieinfo`)

**Problem w jednym zdaniu:** jeden serwis zna dwa obce formaty danych, regułę dostępu i pamięć podręczną, a tabelę kategorii wiekowych czyta z pliku przy każdym pytaniu.

**Wzorce:** Adapter → Pełnomocnik → Singleton

### Co jest nie tak w `start`
- `MovieInfoService.fetch()` – jedno `if (source == MovieSource.OMDB)`, a w nim surowy format OMDb („155 min”, „8.0/10”, `Response=False`). Poniżej konwencje starej tabeli (ocena × 10, `null` dla nieznanego filmu).
- `MovieInfoService.lookup()` i `runtime()` – obie powtarzają sprawdzenie „tylko personel” i obsługę pamięci podręcznej.
- `AgeRatingTable.load()` czyta i parsuje plik CSV przy każdym wywołaniu, a `fetch()` woła je przy każdym filmie. Tabela jest przecież niezmienna i jedna dla całego kina.

### Krok 1: Adapter
> Każde obce źródło dostaje klasę, która tłumaczy jego dane na interfejs w języku kina.

**Przed** (`start/MovieInfoService`):
```java
private MovieDetails fetch(MovieSource source, String id) {
    if (source == MovieSource.OMDB) {
        Map<String, String> raw = omdb.byId(id);
        if ("False".equals(raw.get("Response"))) { throw new IllegalArgumentException("unknown movie: " + id); }
        ...
                Integer.parseInt(raw.get("Runtime").replace(" min", "")),
        ...
    }
    LegacyMovieRow row = legacy.find(id);
    if (row == null) { throw new IllegalArgumentException("unknown movie: " + id); }
    ...
            BigDecimal.valueOf(row.ratingTimesTen(), 1),
```
**Po** (`step1/MovieCatalog`, `step1/OmdbCatalogAdapter`, `step1/MovieInfo`):
```java
public interface MovieCatalog {
    MovieDetails find(String id);
}
public final class OmdbCatalogAdapter implements MovieCatalog {
    public MovieDetails find(String id) {
        Map<String, String> raw = client.byId(id);   // dawna gałąź OMDB z fetch()
        ...
    }
}
this.service = new MovieInfoService(Map.of(          // MovieInfo
        MovieSource.OMDB, new OmdbCatalogAdapter(omdb),
        MovieSource.LEGACY, new LegacyCatalogAdapter(legacy)));
```
**Jak to zrobić:**
1. Dodaj interfejs `MovieCatalog` z jedną metodą `find(String id)`. Zwraca `MovieDetails` albo rzuca `IllegalArgumentException("unknown movie: " + id)` – tak samo dla każdego źródła.
2. Dodaj `OmdbCatalogAdapter`, który owija `OmdbLikeClient`. Przenieś do niego gałąź OMDB z `fetch()`: „155 min” → minuty, „8.0/10” → ocena, `Response=False` → wyjątek.
3. Dodaj `LegacyCatalogAdapter`, który owija `LegacyMovieRows`. Przenieś do niego resztę `fetch()`: ocena ÷ 10, `null` → wyjątek.
4. `MovieInfoService` dostaje w konstruktorze `Map<MovieSource, MovieCatalog>` i woła `catalogs.get(source).find(id)`. Usuń `fetch()`.
5. W `MovieInfo` (tu składamy obiekty) utwórz oba adaptery i przekaż je serwisowi. `OmdbLikeClient` i `LegacyMovieRows` zostają bez zmian.

**Efekt:** serwis nie importuje już klienta OMDb ani starej bazy; nowe źródło to nowy adapter.

### Krok 2: Pełnomocnik (Proxy) – dwa razy
> Obiekt z tym samym interfejsem staje przed oryginałem i decyduje, czy w ogóle go wołać.

**Przed** (`step1/MovieInfoService`):
```java
public MovieDetails lookup(User user, MovieSource source, String id) {
    if (!user.staff()) {
        throw new IllegalStateException("brak uprawnien: " + user.name());
    }
    String key = source + ":" + id;
    if (cache.containsKey(key)) {
        return cache.get(key);
    }
    MovieDetails details = catalogs.get(source).find(id);
    cache.put(key, details);
    return details;
}   // runtime(): to samo sprawdzenie i ta sama pamięć jeszcze raz
```
**Po** (`step2/AuthorizingLookup`, `step2/CachingCatalog`, `step2/MovieInfo`):
```java
public MovieDetails lookup(User user, MovieSource source, String id) {   // AuthorizingLookup
    requireStaff(user);
    return target.lookup(user, source, id);
}
public MovieDetails find(String id) {                                   // CachingCatalog
    MovieDetails cached = cache.get(id);
    if (cached != null) { return cached; }
    ... // target.find(id), potem cache.put(id, details)
}
this.lookup = new AuthorizingLookup(new MovieInfoService(Map.of(        // MovieInfo
        MovieSource.OMDB, new CachingCatalog(new OmdbCatalogAdapter(omdb)),
        MovieSource.LEGACY, new CachingCatalog(new LegacyCatalogAdapter(legacy)))));
```
**Jak to zrobić:**
1. Wydziel z `MovieInfoService` interfejs `MovieLookup` z metodami `lookup(...)` i `runtime(...)`; serwis go implementuje.
2. Dodaj `AuthorizingLookup implements MovieLookup`. Owija `MovieLookup`, w obu metodach najpierw woła `requireStaff(user)`, a potem przekazuje dalej (to jest pełnomocnik ochronny).
3. Dodaj `CachingCatalog implements MovieCatalog`. Owija jeden katalog i pamięta jego wyniki. Wyjątków nie pamięta – tak jak w `start`.
4. Z `MovieInfoService` usuń sprawdzenie uprawnień i pole `cache`. `lookup()` to teraz `catalogs.get(source).find(id)`, a `runtime()` to `lookup(...).runtimeMinutes()`.
5. W `MovieInfo` ustaw kolejność: `AuthorizingLookup` na zewnątrz (gość nie dociera nawet do pamięci), w środku serwis, a `CachingCatalog` tuż przed każdym adapterem (pamięta już przetłumaczone dane).

**Efekt:** reguła dostępu i pamięć podręczna są każda w jednym miejscu, a serwis tylko wybiera katalog.

**Świadoma zmiana zachowania:** `runtime()` teraz też zapisuje wynik w pamięci podręcznej (w `start` tylko z niej czytał), więc `runtime()`, a potem `lookup()` tego samego filmu pyta źródło raz, a nie dwa razy.

### Krok 3: Singleton
> Tabela kategorii wiekowych istnieje w jednym egzemplarzu i jest wczytywana raz.

**Przed** (`start/AgeRatingTable` – bez zmian do kroku 2, `step2/OmdbCatalogAdapter`):
```java
public final class AgeRatingTable {
    public static AgeRatingTable load() {
        Map<String, String> parsed = new HashMap<>();
        for (String line : AgeRatingResource.readLines()) { ... }   // plik czytany za każdym razem
        return new AgeRatingTable(Map.copyOf(parsed));
    }
    ...
}
AgeRatingTable table = AgeRatingTable.load();   // OmdbCatalogAdapter.find(), przy każdym filmie
...
        table.categoryFor("OMDB", raw.get("Rated")));
```
**Po** (`step3/AgeRatings`, `step3/AgeRatingTable`, `step3/OmdbCatalogAdapter`, `step3/MovieInfo`):
```java
public interface AgeRatings {
    String categoryFor(String system, String code);
}
public enum AgeRatingTable implements AgeRatings {
    INSTANCE;
    AgeRatingTable() { ... }   // plik czytany raz, tutaj
}
public OmdbCatalogAdapter(OmdbLikeClient client, AgeRatings ratings) { ... }
        ratings.categoryFor("OMDB", raw.get("Rated")));   // w find()
AgeRatings ratings = AgeRatingTable.INSTANCE;               // MovieInfo
```
**Jak to zrobić:**
1. Wydziel interfejs `AgeRatings` z metodą `categoryFor(String system, String code)`.
2. Zamień `AgeRatingTable` na `enum AgeRatingTable implements AgeRatings { INSTANCE }`. Kod z `load()` przenieś do konstruktora – plik czyta się raz. Java tworzy `enum` leniwie i bezpiecznie dla wątków, bez `synchronized`.
3. W `OmdbCatalogAdapter` i `LegacyCatalogAdapter` dodaj pole `AgeRatings ratings` i konstruktor, który je przyjmuje. Zamiast `AgeRatingTable.load()` wołaj `ratings.categoryFor(...)`. Stary konstruktor z jednym argumentem niech podaje `AgeRatingTable.INSTANCE`.
4. W `MovieInfo` weź `AgeRatingTable.INSTANCE` i jawnie przekaż go obu adapterom.

**Efekt:** tabela jest wczytywana raz na całą JVM, a test może podać adapterowi własną tabelę.

### Dlaczego nie inne wzorce
- **Dekorator zamiast Pełnomocnika?** Wygląda podobnie, bo budowa jest ta sama: ten sam interfejs i jeden owijany obiekt. Ale tutaj opakowania kontrolują dostęp – blokują gościa albo w ogóle nie wołają źródła – a nie dokładają nic do wyniku, więc to Pełnomocnik. Porównaj z e07, gdzie opakowania dodają treść.
- **Łańcuch zobowiązań?** Wygląda podobnie, bo javadoc mówi o „łańcuchu zastępców”. Ale tutaj każde opakowanie zawsze przekazuje dalej do jednego stałego celu i nikt nie szuka obsługującego, więc to nie Łańcuch.
- **Fasada zamiast Adaptera?** Wygląda podobnie, bo obie chowają obcy kod za nowym interfejsem. Ale tutaj tłumaczymy jeden obcy obiekt 1:1 na interfejs, który klient już zna, a nie upraszczamy całego podsystemu, więc to Adapter.
- **Pyłek zamiast Singletona?** Wygląda podobnie, bo chodzi o współdzielenie obiektu. Ale tutaj jest jedna tabela, a celem jest „wczytaj raz”; pyłki to wiele drobnych współdzielonych obiektów, więc to Singleton.

### Nie ruszamy
`OmdbLikeClient`, `LegacyMovieRows`, `LegacyMovieRow`, `MovieDetails`, `MovieSource`, `User`, `AgeRatingResource`.

---

## e07 Bilety i paragony (`e07_documents`)

**Problem w jednym zdaniu:** części dokumentu dla kanału wybierają trzy osobne `switch`, a opcjonalne sekcje to `if`-y z obliczeniami w jednej metodzie.

**Wzorce:** Fabryka abstrakcyjna → Dekorator

### Co jest nie tak w `start`
- `SalesDocuments` ma trzy równoległe `switch` po kanale: `ticketLayoutFor()`, `codeRendererFor()`, `receiptLayoutFor()`. Nowy kanał to trzy nowe gałęzie, a pomyłka w jednej kompiluje się bez ostrzeżenia.
- `SalesDocuments.print()` – opcjonalne sekcje (VAT, promocja, stopka lojalnościowa) to kolejne `if (options.withX())` z obliczeniami w środku.
- `DocumentComposer` przyjmuje trzy osobne części, więc nic nie broni złożyć biletu z kiosku z kodem QR aplikacji i paragonem A4.

### Krok 1: Fabryka abstrakcyjna (Abstract Factory)
> Jeden obiekt na kanał tworzy cały komplet pasujących do siebie części.

**Przed** (`start/SalesDocuments`):
```java
TicketLayout ticketLayout = ticketLayoutFor(channel);
CodeRenderer codeRenderer = codeRendererFor(channel);
ReceiptLayout receiptLayout = receiptLayoutFor(channel);
... new DocumentComposer(ticketLayout, codeRenderer, receiptLayout).compose(sale) ...

private static TicketLayout ticketLayoutFor(Channel channel) {
    return switch (channel) {
        case KIOSK -> new KioskTicketLayout();
        ...
    };
}   // codeRendererFor(), receiptLayoutFor() – ten sam switch jeszcze dwa razy
```
**Po** (`step1/DocumentFactory`, `step1/KioskDocuments`, `step1/DocumentFactories`, `step1/DocumentComposer`):
```java
public interface DocumentFactory {
    TicketLayout ticketLayout();
    CodeRenderer codeRenderer();
    ReceiptLayout receiptLayout();
}
public final class KioskDocuments implements DocumentFactory {
    public TicketLayout ticketLayout() { return new KioskTicketLayout(); }   ...
}
public static DocumentFactory forChannel(Channel channel) {   // DocumentFactories
    return switch (channel) { case KIOSK -> KIOSK; case MOBILE -> MOBILE; case BOX_OFFICE -> BOX_OFFICE; };
}
public DocumentComposer(DocumentFactory family) { this.family = family; }
```
**Jak to zrobić:**
1. Dodaj interfejs `DocumentFactory` z metodami `ticketLayout()`, `codeRenderer()`, `receiptLayout()`.
2. Dodaj po jednej fabryce na kanał: `KioskDocuments`, `MobileDocuments`, `BoxOfficeDocuments`. Każda tworzy komplet trzech części swojego kanału.
3. Klasom `Kiosk*`, `Mobile*`, `BoxOffice*` (układy i kody) zdejmij `public` – spoza pakietu widać tylko fabryki. Interfejsy `TicketLayout`, `CodeRenderer`, `ReceiptLayout` zostają bez zmian.
4. Dodaj `DocumentFactories.forChannel()` – jedyny `switch` po kanale. Fabryki nie mają stanu, więc trzymaj po jednej instancji w polach statycznych.
5. `DocumentComposer` przyjmuje w konstruktorze `DocumentFactory` zamiast trzech części i bierze z niej wszystkie trzy.
6. W `SalesDocuments.print()` weź fabrykę z `DocumentFactories.forChannel(channel)` i usuń trzy metody `...For()`. Opcjonalne sekcje zostaw na krok 2.

**Efekt:** nowy kanał to jedna nowa klasa fabryki i jeden wpis w `forChannel()`; części różnych kanałów nie da się już pomieszać.

### Krok 2: Dekorator (Decorator)
> Każda opcjonalna sekcja owija dokument i dopisuje pod nim swoją część.

**Przed** (`step1/SalesDocuments`):
```java
DocumentFactory family = DocumentFactories.forChannel(channel);
ReceiptLayout receiptLayout = family.receiptLayout();
StringBuilder document = new StringBuilder(new DocumentComposer(family).compose(sale));
if (options.withVat()) {
    List<String> lines = new ArrayList<>();
    lines.add("VAT 8% bilet: " + vat(sale.ticketPrice(), 8));
    ...
    document.append("\n").append(receiptLayout.section(lines));
}
if (options.withPromo()) { ... }
if (options.withLoyaltyFooter()) { ... }   // punkty liczone tutaj
return document.toString();
```
**Po** (`step2/Document`, `step2/VatSection`, `step2/DocumentComposer`, `step2/SalesDocuments`):
```java
public interface Document {
    String render();
}
public record VatSection(Document inner, ReceiptLayout layout, Sale sale) implements Document {
    public String render() { ... return inner.render() + "\n" + layout.section(lines); }
}
Document document = new ChannelDocument(sale, family);   // DocumentComposer.compose(sale, options)
if (options.withVat()) {
    document = new VatSection(document, layout, sale);
}
... // PromoBanner, potem LoyaltyFooter
return new DocumentComposer(DocumentFactories.forChannel(channel)).compose(sale, options).render();
```
**Jak to zrobić:**
1. Dodaj interfejs `Document` z jedną metodą `render()`.
2. Dodaj `ChannelDocument(sale, family)` – dokument bez żadnej opcji. Przenieś do niego ciało dawnego `DocumentComposer.compose(sale)`.
3. Dla każdej opcji dodaj rekord, który owija `Document`: `VatSection`, `PromoBanner`, `LoyaltyFooter`. Do każdego przenieś obliczenia z jego `if` w `print()` (do `VatSection` także `vat()`). `render()` zwraca `inner.render() + "\n" + layout.section(lines)`.
4. W `DocumentComposer` zamień `compose(sale)` na `compose(sale, options)`, które zwraca `Document`. Zaczyna od `ChannelDocument` i owija go w stałej kolejności: VAT, promocja, stopka.
5. W `SalesDocuments.print()` zostaw walidację i jedną linię `compose(sale, options).render()`.

**Efekt:** `print()` nie ma żadnego `if` po opcjach, obliczenia siedzą w swoich sekcjach, a kolejność sekcji jest zapisana w jednym miejscu.

### Dlaczego nie inne wzorce
- **Metoda wytwórcza?** Wygląda podobnie, bo fabryka ma metody, które tworzą obiekty. Ale tutaj potrzebny jest spójny komplet trzech produktów naraz, a Metoda wytwórcza tworzy jeden produkt, więc to Fabryka abstrakcyjna.
- **Budowniczy?** Wygląda podobnie, bo `DocumentComposer` składa dokument z opcji. Ale tutaj każda opcja to osobny obiekt z tym samym interfejsem, który sam się renderuje, a nie krok budowy jednego produktu, więc to Dekorator.
- **Kompozyt albo Łańcuch zobowiązań?** Wygląda podobnie, bo obiekty wskazują na inne obiekty tego samego typu. Ale tutaj każda sekcja owija dokładnie jeden dokument (to nie drzewo) i zawsze renderuje (nikt nie przerywa łańcucha), więc to Dekorator.
- **Pełnomocnik?** Wygląda podobnie, bo opakowania są zbudowane tak samo jak w e06. Ale tutaj dokładają treść, a nie kontrolują dostępu, więc to Dekorator.

### Nie ruszamy
`Channel`, `PrintOptions`, `PrintRequest`, `Sale`.

---

## e08 Cykl seansów (`e08_series`)

**Problem w jednym zdaniu:** każdy tydzień cyklu to ręczna kopia 9 pól seansu, a wszystkie kopie dzielą jedną mapę dopłat, więc dopłata premierowa trafia do każdego tygodnia.

**Wzorce:** Prototyp → Budowniczy

### Co jest nie tak w `start`
- `Screening` ma publiczny konstruktor z 9 argumentami; `null` znaczy „użyj wartości domyślnej”.
- Wartości domyślne są w trzech miejscach: konstruktor `Screening` (pusta mapa dopłat), `Screening.describe()` (sala „A”, cena z formatu) i `SeriesPlanner.plan()` (cena z formatu, VIP, okulary dla 3D, wiek 12).
- `SeriesPlanner.plan()` tworzy każdy tydzień ręczną kopią pole po polu; ostatni tydzień w innej sali to kolejna taka kopia.
- `Screening.surcharges()` zwraca wewnętrzną mapę, a konstruktor jej nie kopiuje. Wszystkie tygodnie dzielą jedną mapę, więc `PREMIERA` dodana do tygodnia 1 pojawia się w każdym tygodniu.

### Krok 1: Prototyp (Prototype)
> Nowy seans powstaje z kopii istniejącego (z jedną zmianą), a nie przez przepisywanie 9 pól.

**Przed** (`start/SeriesPlanner`):
```java
for (int week = 0; week < request.weeks(); week++) {
    Screening copy = new Screening(template.title(), template.format(),
            template.hall() != null ? template.hall() : "A",
            template.start().plusWeeks(week), template.basePrice(), template.surcharges(),
            template.subtitles(), template.dubbing(), template.ageLimit());
    if (week == 0 && request.premiereWeek()) {
        copy.surcharges().put("PREMIERA", PREMIERE); // ta sama mapa we wszystkich tygodniach
    }
    series.add(copy);
}
```
**Po** (`step1/Screening`, `step1/SeriesPlanner`):
```java
// Screening
this.surcharges = surcharges != null ? new LinkedHashMap<>(surcharges) : new LinkedHashMap<>();
public Screening withStart(LocalDateTime newStart) {
    return new Screening(title, format, hall, newStart, basePrice, surcharges, subtitles, dubbing, ageLimit);
}
// SeriesPlanner.plan()
Screening copy = template.withStart(template.start().plusWeeks(week));
if (week == 0 && request.premiereWeek()) {
    copy = copy.withSurcharge("PREMIERA", PREMIERE);
}
...
series.set(series.size() - 1, series.getLast().withHall(request.lastWeekHall()));
```
**Jak to zrobić:**
1. `Screening`: w konstruktorze kopiuj mapę dopłat (`new LinkedHashMap<>(surcharges)`), a `surcharges()` niech zwraca `Collections.unmodifiableMap(surcharges)`.
2. `Screening`: dodaj `copy()` (pełna kopia) oraz `withStart()`, `withHall()`, `withSurcharge()` – kopia z jedną zmianą (to jest prototyp).
3. `SeriesPlanner.plan()`: domyślną salę „A” ustaw już przy tworzeniu szablonu, bo kopia jej nie uzupełnia.
4. `SeriesPlanner.plan()`: kolejny tydzień to `template.withStart(...)`, premiera to `copy.withSurcharge("PREMIERA", PREMIERE)`.
5. `SeriesPlanner.plan()`: ostatni tydzień w innej sali to `series.getLast().withHall(...)`.

**Efekt:** każdy tydzień ma własną mapę dopłat, a seans tworzy się z szablonu jedną metodą.

**Świadoma zmiana zachowania:** dopłata `PREMIERA` zostaje tylko w tygodniu 1 (w `start` wyciekała do wszystkich tygodni).

### Krok 2: Budowniczy (Builder)
> Osobny obiekt zbiera parametry seansu krok po kroku, a na końcu uzupełnia wartości domyślne, sprawdza je i tworzy gotowy seans.

**Przed** (`step1/SeriesPlanner`):
```java
if (request.title() == null || request.title().isBlank()) { throw ... }
if (!List.of("2D", "3D", "IMAX").contains(request.format())) { throw ... }
Money base = request.basePrice() != null ? request.basePrice() : priceFor(request.format());
Map<String, Money> surcharges = new LinkedHashMap<>();
surcharges.put("VIP", VIP);
...
boolean dubbing = request.dubbing() != null && request.dubbing();
int ageLimit = request.ageLimit() != null ? request.ageLimit() : 12;
Screening template = new Screening(request.title(), request.format(),
        request.hall() != null ? request.hall() : "A",
        request.firstStart(), base, surcharges, !dubbing, dubbing, ageLimit);
```
**Po** (`step2/SeriesPlanner`, `step2/Screening.Builder`):
```java
// SeriesPlanner.plan()
Screening.Builder builder = Screening.builder()
        .title(request.title()).format(request.format()).start(request.firstStart());
if (request.hall() != null) {
    builder.hall(request.hall());
}
... // tak samo basePrice, dubbing, ageLimit
Screening template = builder.build();
// Screening.Builder.build()
Money price = basePrice != null ? basePrice : defaultPrice(format); // + walidacja, VIP, okulary
return new Screening(title, format, hall != null ? hall : DEFAULT_HALL, start, price, surcharges,
        !dubbing, dubbing, ageLimit != null ? ageLimit : DEFAULT_AGE_LIMIT);
```
**Jak to zrobić:**
1. `Screening`: dodaj zagnieżdżoną klasę `Builder` z metodami `title()`, `format()`, `hall()`, `start()`, `basePrice()`, `surcharge()`, `dubbing()`, `ageLimit()`; każda zwraca `this`.
2. `Screening.Builder.build()`: przenieś tu walidację (tytuł, format, start, cena nieujemna) i wszystkie wartości domyślne: sala „A”, cena z formatu, VIP, okulary dla 3D, napisy, wiek 12.
3. `Screening`: konstruktor zrób prywatny i dodaj `Screening.builder()`; z `describe()` usuń uzupełnianie sali i ceny.
4. `SeriesPlanner.plan()`: ustawiaj w budowniczym tylko pola, które przyszły w `SeriesRequest`, i wołaj `build()`. Zostaje tylko sprawdzenie `weeks < 1`; usuń `priceFor()` oraz stałe `VIP` i `GLASSES_3D`.
5. Kopie tygodni (`withStart()`, `withSurcharge()`, `withHall()`) zostają z kroku 1.

**Efekt:** wartości domyślne i walidacja seansu są w jednym miejscu (`build()`), a planer zna tylko regułę cyklu.

### Dlaczego nie inne wzorce
- **Metoda wytwórcza / Fabryka abstrakcyjna?** Wyglądają podobnie, bo też chodzi o tworzenie obiektów. Ale tutaj jest jeden rodzaj seansu, bez wariantów do wyboru, więc nie ma czego tworzyć w podklasach. Problemem jest liczba parametrów, wartości domyślne i kopiowanie.
- **Pyłek?** Wygląda podobnie, bo seanse cyklu „współdzielą” dane. Ale tutaj błędem jest właśnie współdzielona, modyfikowalna mapa dopłat, więc potrzebujemy niezależnych kopii – czyli odwrotności Pyłku.
- **Pamiątka?** Wygląda podobnie, bo kopia przypomina migawkę stanu. Ale tutaj niczego nie przywracamy – kopie to nowe, różne seanse, więc wystarczy Prototyp.
- **Metoda szablonowa?** Wygląda podobnie, bo każdy tydzień przechodzi te same kroki. Ale tutaj algorytm cyklu jest jeden, bez wariantów, a powtórzenia są w tworzeniu obiektów, więc nie ma czego wydzielać do podklas.

### Nie ruszamy
`SeriesRequest`.

---

## e09 Raport szuflady kasowej (`e09_cashdrawer`)

**Problem w jednym zdaniu:** raport rozpoznaje rodzaj wpisu przez `instanceof` w trzech miejscach, a podział rabatu grupowego to łańcuch `if` po nazwie trybu.

**Wzorce:** Odwiedzający → Strategia

### Co jest nie tak w `start`
- `DrawerEntry` to pusty interfejs, więc `CashDrawerReport` rozpoznaje rodzaj wpisu przez `instanceof`.
- `CashDrawerReport` ma trzy równoległe łańcuchy `instanceof`: `cashEffect()`, `fiscalLine()`, `auditLine()`. Każdy kończy się wyjątkiem, więc nowy rodzaj wpisu bez obsługi wychodzi dopiero przy zamknięciu dnia.
- `CashDrawerReport.groupDiscount(tickets, mode)` miesza regułę „10+ biletów = -10%” z łańcuchem `if` po nazwie trybu (`PROPORTIONAL`, `LAST`, `EQUAL`) i z korektą zaokrągleń.
- Tryb jest sprawdzany dopiero dla grupy 10+, więc nieznany tryb przy małej grupie przechodzi bez błędu.

### Krok 1: Odwiedzający (Visitor)
> Każda operacja na wpisach to osobna klasa z jedną metodą na rodzaj wpisu, a wpis sam wybiera, którą metodę wywołać.

**Przed** (`start/CashDrawerReport`):
```java
private String fiscalLine(DrawerEntry entry) {
    if (entry instanceof CashSale sale) {
        return "S;" + sale.reference() + ";" + sale.amount() + ";8";
    } else if (entry instanceof CardSale card) {
        return "K;" + card.reference() + ";" + card.amount() + ";8;" + card.cardLast4();
    } else if (entry instanceof Refund refund) {
        ...
    } else if (entry instanceof Deposit) {
        return "";
    }
    throw new IllegalArgumentException("unknown entry: " + entry);
}
```
**Po** (`step1/DrawerEntryVisitor`, `step1/CashSale`, `step1/CashDrawerReport`):
```java
public interface DrawerEntryVisitor<R> {
    R visitCashSale(CashSale sale);
    R visitCardSale(CardSale sale);
    R visitRefund(Refund refund);
    R visitDeposit(Deposit deposit);
}
public record CashSale(String reference, Money amount) implements DrawerEntry {
    public <R> R accept(DrawerEntryVisitor<R> visitor) { return visitor.visitCashSale(this); }
}
// CashDrawerReport.render()
String line = entry.accept(fiscalLine); // fiscalLine = new FiscalLineVisitor()
```
**Jak to zrobić:**
1. Dodaj interfejs `DrawerEntryVisitor<R>` z metodami `visitCashSale`, `visitCardSale`, `visitRefund`, `visitDeposit` (to jest odwiedzający).
2. `DrawerEntry`: dodaj metodę `<R> R accept(DrawerEntryVisitor<R> visitor)`.
3. `CashSale`, `CardSale`, `Refund`, `Deposit`: w `accept()` wołaj swoją metodę, np. `visitor.visitCashSale(this)`.
4. Treść łańcuchów `cashEffect()`, `fiscalLine()`, `auditLine()` przenieś do nowych klas `CashEffectVisitor`, `FiscalLineVisitor`, `AuditLineVisitor` – jedna gałąź `if` = jedna metoda `visit...`.
5. `CashDrawerReport`: trzymaj trzy obiekty odwiedzających i wołaj `entry.accept(...)`; usuń prywatne metody z `instanceof`.

**Efekt:** nowa operacja na wpisach = jedna nowa klasa, a brak obsługi nowego rodzaju wpisu to błąd kompilacji, nie wyjątek przy zamknięciu dnia.

### Krok 2: Strategia (Strategy)
> Każdy sposób podziału rabatu to osobna klasa, a reguła rabatu dostaje wybrany sposób z zewnątrz.

**Przed** (`step1/CashDrawerReport`):
```java
private String groupDiscount(List<Money> tickets, String mode) {
    if (tickets.size() < GROUP_SIZE) {
        return "Rabat grupowy: brak (" + tickets.size() + " biletow)";
    }
    ...
    Money discount = sum.percent(GROUP_PERCENT);
    if (mode.equals("PROPORTIONAL")) { ... }
    else if (mode.equals("LAST")) { ... }
    else if (mode.equals("EQUAL")) { ... }
    else { throw new IllegalArgumentException("unknown split mode: " + mode); }
    ...
}
```
**Po** (`step2/DiscountSplit`, `step2/GroupDiscount`, `step2/CashDrawerReport`):
```java
public interface DiscountSplit {
    List<Money> split(Money discount, List<Money> tickets);
}
// GroupDiscount.allocate()
if (tickets.size() < GROUP_SIZE) {
    return Optional.empty();
}
...
return Optional.of(new Allocation(discount, split.split(discount, tickets)));
// CashDrawerReport.groupDiscount()
GroupDiscount rule = new GroupDiscount(DiscountSplits.forMode(mode));
return rule.allocate(tickets).map(...).orElse("Rabat grupowy: brak (" + tickets.size() + " biletow)");
```
**Jak to zrobić:**
1. Dodaj interfejs `DiscountSplit` z metodą `split(discount, tickets)` (to jest strategia).
2. Każdą gałąź dawnego `if` przenieś do osobnej klasy: `ProportionalSplit`, `LastTicketSplit`, `EqualSplit`.
3. Dodaj klasę `GroupDiscount`: reguła 10+ biletów i -10%, a podział oddaje strategii podanej w konstruktorze. `allocate()` zwraca `Optional<Allocation>` (pusty dla małej grupy). Klasa nie zna nazw trybów.
4. Dodaj `DiscountSplits.forMode(mode)` – jedyne miejsce, gdzie nazwa trybu zamienia się w strategię; nieznana nazwa rzuca wyjątek.
5. `CashDrawerReport.groupDiscount()`: tworzy `new GroupDiscount(DiscountSplits.forMode(mode))` i tylko formatuje wynik.

**Efekt:** nowy tryb podziału = jedna klasa i jedna linia w `DiscountSplits`, bez zmian w regule rabatu.

**Świadoma zmiana zachowania:** tryb jest wybierany przed sprawdzeniem wielkości grupy, więc nieznany tryb rzuca błąd także dla grupy poniżej 10 biletów.

### Dlaczego nie inne wzorce
- **Łańcuch zobowiązań?** Wygląda podobnie, bo w `start` jest „łańcuch `instanceof`”. Ale tutaj każda gałąź obsługuje dokładnie jeden typ i nie ma decyzji „obsłuż albo przekaż dalej”, a łańcuch zostawiłby błąd w czasie działania, więc wybieramy Odwiedzającego, który przenosi ten błąd do kompilacji.
- **Stan?** Wygląda podobnie, bo zachowanie podziału zależy od trybu. Ale tutaj tryb to konfiguracja wybierana raz i nic nie przechodzi z trybu do trybu, więc wystarczy Strategia.
- **Polecenie?** Wygląda podobnie, bo każda z trzech operacji staje się osobnym obiektem. Ale tutaj operacji nie kolejkujemy, nie zapisujemy ani nie cofamy, więc problemem jest tylko wybór zachowania po rodzaju wpisu.
- **Kompozyt?** Wygląda podobnie, bo Odwiedzający często chodzi w parze z Kompozytem. Ale tutaj wpisy to płaska lista, a nie drzewo, więc nie ma czego składać.

### Nie ruszamy
`DrawerDay`, `DrawerLine`, `DrawerEntries`.

---

## e10 Przebieg seansu (`e10_showtime`)

**Problem w jednym zdaniu:** urządzenia sali sterują sobą nawzajem, a dwaj klienci powielają kroki startu seansu i jeden z nich pomija raport frekwencji.

**Wzorce:** Mediator → Fasada

### Co jest nie tak w `start`
- `HallController` tylko spina urządzenia „każde z każdym” przez metody `wire(...)`, więc kolejność reakcji jest rozrzucona po `Doors`, `AdPlayer` i `Projector`.
- `Doors.close(title)` ustawia światło i uruchamia reklamy, a przy przeszkodzie pamięta tytuł filmu, który nie jest sprawą drzwi. `AdPlayer.tick()` po ostatniej reklamie ustawia światło, dźwięk i uruchamia projektor.
- `Projector.stop()` sam wyłącza dźwięk, zapala światło i otwiera drzwi; `cut()` powstał po to, żeby w awarii ominąć tę kaskadę.
- `BoxOfficePanel` i `AutoScheduler` powtarzają te same kroki (bramka → sala → raport), ale `AutoScheduler.startShow()` pomija zapis frekwencji.

### Krok 1: Mediator
> Urządzenie tylko zgłasza, co się stało, a jeden sterownik sali decyduje, kto i w jakiej kolejności reaguje.

**Przed** (`start/AdPlayer`):
```java
private Lights lights;
private Sound sound;
private Projector projector;
...
public void tick() {
    ...
    log.add("ads: finished");
    lights.set(0);
    sound.set(70);
    projector.play(title);
    title = null;
}
```
**Po** (`step1/AdPlayer`, `step1/HallController`):
```java
// AdPlayer.tick()
log.add("ads: finished");
hall.adsFinished();

// HallController implements HallMediator
@Override
public void adsFinished() {
    lights.set(0);
    sound.set(70);
    projector.play(pendingTitle);
    pendingTitle = null;
}
```
**Jak to zrobić:**
1. Dodaj interfejs `HallMediator` z metodami `doorsClosed()`, `doorsBlocked()`, `doorsCleared()`, `adsFinished()` (to jest mediator).
2. `Doors`, `AdPlayer`: zamiast pól z innymi urządzeniami i `wire(...)` dostają `HallMediator` w konstruktorze i tylko zgłaszają zdarzenie, np. `hall.adsFinished()`. Usuń z nich tytuł filmu: zostają `close()` i `start(ads)` bez tytułu.
3. `Projector`: usuń `wire(...)` i kaskadę ze `stop()`; zostaje tylko stan projektora (`play()`, `stop()`, `cut()`).
4. `HallController`: implementuje `HallMediator`. Tu trafia kolejność reakcji, tytuł filmu (`pendingTitle`), ponowne zamknięcie drzwi po usunięciu przeszkody (`closeWhenCleared`) i kaskada końca seansu w `endShow()`.
5. `Lights`, `Sound`: bez zmian – tylko wykonują polecenia, a limit głośności zostaje w `Sound`.

**Efekt:** cała kolejność reakcji sali jest w jednej klasie i żadne urządzenie nie zna innego urządzenia. `AutoScheduler` nadal pomija raport – to temat kroku 2.

### Krok 2: Fasada (Facade)
> Jedna klasa z metodami „start seansu” i „koniec seansu” ukrywa przed klientami bramkę, salę i raport.

**Przed** (`step1/BoxOfficePanel`, `step1/AutoScheduler`):
```java
// BoxOfficePanel.startShow()
int attendance = scanner.close(request.screeningId());
hall.startShow(request.title());
report.record(request.screeningId(), attendance);

// AutoScheduler.startShow() – kopia bez raportu
scanner.close(request.screeningId());
hall.startShow(request.title());
```
**Po** (`step2/Showtime`, `step2/AutoScheduler`):
```java
public final class Showtime {
    ...
    public void start(ShowRequest request) {
        int attendance = scanner.close(request.screeningId());
        hall.startShow(request.title());
        report.record(request.screeningId(), attendance);
    }
}
// AutoScheduler (tak samo BoxOfficePanel)
public void startShow(ShowRequest request) {
    showtime.start(request);
}
```
**Jak to zrobić:**
1. Dodaj klasę `Showtime` z metodami `start(ShowRequest)` i `end(screeningId)`; przenieś do nich kroki z `BoxOfficePanel`, czyli wersję z raportem (to jest fasada).
2. `TicketScanner`, `HallController`, `AttendanceReport`: zmień na widoczność pakietową, żeby klienci nie mogli ich używać bezpośrednio.
3. `BoxOfficePanel`, `AutoScheduler`: jedyną zależnością jest `Showtime`; `startShow()` i `endShow()` tylko ją wołają.
4. `ShowtimeRunner`: składa `Showtime` z bramki, sali i raportu i przekazuje ją obu klientom; zdarzenia sprzętu (`tick`, `obstacle`, `emergency` itd.) nadal idą prosto do `HallController`.

**Efekt:** kroki startu i końca seansu są w jednym miejscu i żaden klient nie może żadnego pominąć.

**Świadoma zmiana zachowania:** `AutoScheduler` zapisuje teraz frekwencję, tak jak `BoxOfficePanel`.

### Dlaczego nie inne wzorce
- **Łańcuch zobowiązań?** Wygląda podobnie, bo javadoc w `start` mówi o „łańcuchu urządzeń”. Ale tutaj każde ogniwo zawsze działa i na sztywno woła następne, bez decyzji „obsłuż albo przekaż dalej”, więc to splątana kaskada, którą trzeba zebrać w jednym miejscu.
- **Obserwator?** Wygląda podobnie, bo urządzenia „powiadamiają” o zdarzeniach. Ale tutaj jest jeden znany odbiorca, który steruje innymi w ściśle określonej kolejności, a Obserwator rozsyła zdarzenia do niezależnych słuchaczy, więc kolejność by się rozmyła.
- **Polecenie?** Wygląda podobnie, bo skrypt składa się z komend (`desk start ...`, `tick`). Ale tutaj to tylko symulator w `ShowtimeRunner`, więc nie ma czego kolejkować ani cofać.
- **Metoda szablonowa dla `BoxOfficePanel` / `AutoScheduler`?** Wygląda podobnie, bo oba robią te same kroki. Ale tutaj podklasa mogłaby nadpisać i pominąć krok, a klienci nadal znaliby trzy podsystemy, więc wybieramy Fasadę, w której kroku nie da się pominąć.
- **Tylko Mediator albo tylko Fasada?** Wyglądają podobnie, bo oba są „jednym miejscem pośrodku”. Ale tutaj mediator koordynuje reakcje urządzeń między sobą, a fasada tylko upraszcza wejście dla klientów, więc potrzebne są oba.

### Nie ruszamy
`ShowRequest`.
