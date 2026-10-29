# Moduł 6. Ćwiczenia utrwalające: gdzie i jaki wzorzec - warsztat CineLegacy: rozwiązania

Dziesięć ćwiczeń w domenie kina CineLegacy, uzupełniających sceny modułu 5. Różnica jest jedna, ale zasadnicza: **tytuł ćwiczenia nie zdradza wzorca**. Uczestnik dostaje fragment systemu z dwoma albo trzema niezależnymi problemami i musi sam odpowiedzieć na pytania z mapy decyzji (co się zmienia, kto wybiera wariant i kiedy, ile osi zmienności), nazwać mechanizm i dopiero potem go wprowadzić. Razem ćwiczenia pokrywają wszystkie 23 wzorce GoF; kroki `stepN` wprowadzają po jednym wzorcu i są rozwiązaniem wzorcowym, a ten dokument wyjaśnia, **po czym rozpoznać** każdy z nich i dlaczego nie wybrano podobnego wzorca.

Treść zadań dla uczestników (bez nazw wzorców) jest w `src/main/resources/md/warsztat/zadania/06-cwiczenia.md`. Scenariusze celowo różnią się od scen modułu 5, nawet gdy wzorzec się powtarza: obserwator reaguje na zmianę seansu zamiast na płatność, fasada ukrywa start seansu zamiast zakupu, mediator koordynuje urządzenia sali zamiast ekranu wyboru miejsc.

## Jak prowadzić

```bash
scripts/warsztat.sh list exercises             # ćwiczenia i kroki
scripts/warsztat.sh test exercises/e03         # testy jednego ćwiczenia
scripts/warsztat.sh test exercises             # wszystkie ćwiczenia
scripts/warsztat.sh diff exercises/e03 0 1     # co zmienia krok 1 względem start
scripts/warsztat.sh jump exercises/e03 2       # start = krok 2 (omówienie gotowego kroku)
scripts/warsztat.sh reset exercises/e03        # przywrócenie start
```

- Ćwiczenia są do samodzielnej pracy w parach (20-30 min na ćwiczenie) albo jako zadanie domowe po module 5. Omówienie: najpierw sala nazywa problemy i proponuje mechanizmy, dopiero potem `diff` kolejnych kroków.
- Każdy krok ma test równoważności `EnnEquivalenceTest` (te same przypadki na `start` i każdym `stepN`) i test `EnnSolutionTest`, który sprawdza warunek projektowy rozwiązania (współdzielone instancje, brak referencji między komponentami, cofnięcie stanu, liczba wywołań). Tam, gdzie zachowanie świadomie się zmienia, jest osobny test z nazwą `BehaviourChange` i jedno zdanie uzasadnienia w tym dokumencie.
- W dyskusji wracaj do pytania "czy wzorzec jest tu potrzebny?". W kilku ćwiczeniach prostsza struktura (rekord, `sealed` + `switch`, lambda) jest równie dobrą odpowiedzią i przewodnik to zaznacza.

## Mapa ćwiczeń

| Ćwiczenie | Scenariusz | Wzorce (po jednym na krok) | Pakiet |
| --- | --- | --- | --- |
| e01 | Import repertuaru z CSV i JSON Lines | Template Method, Factory Method | `exercises.e01_import` |
| e02 | Mapa sali: sektory, rzędy, miejsca | Composite, Iterator, Flyweight | `exercises.e02_hallmap` |
| e03 | Zmiana seansu i powiadomienia | Observer, Bridge, Chain of Responsibility | `exercises.e03_notifications` |
| e04 | Wybór miejsc w koszyku z cofaniem | Command, Memento | `exercises.e04_seatpicker` |
| e05 | Karta podarunkowa i reguły voucherów | State, Interpreter | `exercises.e05_giftcard` |
| e06 | Informacje o filmie ze źródeł zewnętrznych | Adapter, Proxy, Singleton | `exercises.e06_movieinfo` |
| e07 | Dokumenty sprzedaży: bilet, paragon, kod | Abstract Factory, Decorator | `exercises.e07_documents` |
| e08 | Seanse cykliczne z szablonu | Prototype, Builder | `exercises.e08_series` |
| e09 | Zamknięcie kasy i podział rabatu | Visitor, Strategy | `exercises.e09_cashdrawer` |
| e10 | Start seansu: urządzenia sali i orkiestracja | Mediator, Facade | `exercises.e10_showtime` |

## Ćwiczenie e01. Import repertuaru z plików CSV i JSON Lines

**Pakiet:** `pl.training.workshop.exercises.e01_import` · **Test:** `scripts/warsztat.sh test exercises/e01` · **Kroki:** 2

| Fragment kodu | Sygnał | Wzorzec | Dlaczego nie ... |
| --- | --- | --- | --- |
| `RepertoireImporter.importCsv` i `importJsonLines` | dwie metody o tym samym przebiegu (pętla, numeracja, komentarze, walidacja, sortowanie), różni się jeden krok w środku | Template Method (Metoda szablonowa) | nie Strategy: przebieg jest stały i należy chronić go przed zmianą, a różnica to jeden krok, nie cały algorytm |
| rozbiór wiersza (`split(";")`, regex JSON) wewnątrz importera | zmienny krok jest obiektem o własnym sensie: ma dwa zachowania (`isComment`, `parse`), chcemy go testować bez importera i użyć poza nim | Factory Method (Metoda wytwórcza) `createLineParser()` | nie zwykły hak `parseLine`: hak nie da się przetestować osobno ani użyć w podglądzie wiersza; nie Abstract Factory: tworzymy jeden produkt, nie rodzinę |
| `if (fileName.endsWith(".csv"))` w `RepertoireImport` i w `RepertoireImporter.importFile` | ta sama decyzja w dwóch miejscach, klient zna formaty | prosta fabryka statyczna `Importers.forFile` (to nie wzorzec GoF, tylko jedno miejsce decyzji) | nie Factory Method: nie ma tu hierarchii twórców, tylko jeden punkt wyboru po danych |

Test równoważności to tabela: oba formaty, komentarz właściwy dla formatu (i błąd, gdy użyć cudzego), sortowanie po godzinie i tytule, zniżka poranna, numeracja wierszy liczona razem z komentarzami i każdy komunikat walidacji. Komunikaty z numerem wiersza są kontraktem: refaktoryzacja nie może przesunąć numeracji ani zmienić kolejności sprawdzeń.

### Krok 1: Template Method - wspólny przebieg importu w jednym miejscu

**Zmiany:**

1. Powstaje rekord `RawScreening(title, format, hall, start)` - surowe pola wiersza przed walidacją, wspólne dla formatów.
2. `RepertoireImporter` staje się klasą abstrakcyjną z finalną metodą `importLines(lines)`: pętla, numeracja, pomijanie pustych linii i komentarzy, `toEntry`, sortowanie. Dwa haki: `isComment(line)` i `parseLine(line, number)`.
3. Nowe klasy `CsvRepertoireImporter` i `JsonLinesRepertoireImporter` zawierają tylko te dwa haki (Extract Superclass + Pull Up Method).
4. `importFile(fileName, lines)` zostaje jako metoda statyczna z tym samym `if` po rozszerzeniu; klient zmienia tylko sposób wywołania.

**Przed (`RepertoireImporter.java`, fragment):**

```java
public List<ScreeningEntry> importCsv(List<String> lines) {
    List<ScreeningEntry> entries = new ArrayList<>();
    int number = 0;
    for (String raw : lines) {
        number++;
        String line = raw.strip();
        if (line.isEmpty() || line.startsWith("#")) {
            continue;
        }
        String[] fields = line.split(";", -1);
        if (fields.length != 4) {
            throw new IllegalArgumentException("line " + number + ": expected 4 fields, got " + fields.length);
        }
        entries.add(toEntry(number, fields[0].strip(), fields[1].strip(), fields[2].strip(), fields[3].strip()));
    }
    entries.sort(Comparator.comparing(ScreeningEntry::start).thenComparing(ScreeningEntry::title));
    return List.copyOf(entries);
}

public List<ScreeningEntry> importJsonLines(List<String> lines) {
    // ta sama pętla, ta sama numeracja, inny znak komentarza i inny rozbiór wiersza
}
```

**Po (`RepertoireImporter.java`):**

```java
public final List<ScreeningEntry> importLines(List<String> lines) {
    List<ScreeningEntry> entries = new ArrayList<>();
    int number = 0;
    for (String raw : lines) {
        number++;
        String line = raw.strip();
        if (line.isEmpty() || isComment(line)) {
            continue;
        }
        entries.add(toEntry(number, parseLine(line, number)));
    }
    entries.sort(Comparator.comparing(ScreeningEntry::start).thenComparing(ScreeningEntry::title));
    return List.copyOf(entries);
}

protected abstract boolean isComment(String line);

protected abstract RawScreening parseLine(String line, int number);
```

**Po (`CsvRepertoireImporter.java`):**

```java
public final class CsvRepertoireImporter extends RepertoireImporter {
    @Override
    protected boolean isComment(String line) {
        return line.startsWith("#");
    }

    @Override
    protected RawScreening parseLine(String line, int number) {
        String[] fields = line.split(";", -1);
        if (fields.length != 4) {
            throw new IllegalArgumentException("line " + number + ": expected 4 fields, got " + fields.length);
        }
        return new RawScreening(fields[0].strip(), fields[1].strip(), fields[2].strip(), fields[3].strip());
    }
}
```

**Pułapka:** przed podciągnięciem obie metody muszą być *identyczne*, nie podobne. Numer wiersza w komunikacie musi być liczony przed pominięciem komentarza (przypadek "numer wiersza liczy też komentarze"), a `isComment` dostaje wiersz już po `strip()`. Jeśli ktoś najpierw wyciągnie `parseLine`, a znak komentarza zostawi w szkielecie jako `startsWith("#") || startsWith("//")`, test "komentarz w stylu JSONL to błąd" robi się czerwony: zmieniło się zachowanie dla CSV.

`scripts/warsztat.sh diff exercises/e01 0 1`

### Krok 2: Factory Method - zmienny krok jako produkt, rozszerzenia w jednym miejscu

**Zmiany:**

1. Powstaje interfejs `LineParser` (`isComment`, `parse`) i dwa produkty: `CsvLineParser`, `JsonLineParser` - kod haków z kroku 1 przeniesiony bez zmian (Extract Class).
2. W `RepertoireImporter` dwa haki zastępuje jedna metoda wytwórcza `protected abstract LineParser createLineParser()`; szkielet tworzy parser raz na import i woła go w pętli. Dochodzi `public abstract String label()`.
3. `CsvRepertoireImporter` i `JsonLinesRepertoireImporter` to już tylko `label()` i `createLineParser()` (konkretni twórcy).
4. Statyczne `importFile` znika z importera; wybór po rozszerzeniu trafia do `Importers.forFile(fileName)`, a klient bierze podpis raportu z `importer.label()`. Oba `if` po rozszerzeniu zastępuje jeden.

**Przed (`RepertoireImport.java`, krok 1):**

```java
String label;
if (request.fileName().endsWith(".csv")) {
    label = "CSV";
} else if (request.fileName().endsWith(".jsonl")) {
    label = "JSONL";
} else {
    throw new IllegalArgumentException("unsupported file: " + request.fileName());
}
List<ScreeningEntry> entries = RepertoireImporter.importFile(request.fileName(), request.lines());
```

**Po (`RepertoireImport.java`):**

```java
RepertoireImporter importer = Importers.forFile(request.fileName());
List<ScreeningEntry> entries = importer.importLines(request.lines());
StringBuilder report = new StringBuilder("[" + importer.label() + "] pozycji: " + entries.size());
```

**Po (`RepertoireImporter.java`, fragment) i (`JsonLinesRepertoireImporter.java`):**

```java
public final List<ScreeningEntry> importLines(List<String> lines) {
    LineParser parser = createLineParser();
    ...
        if (line.isEmpty() || parser.isComment(line)) {
            continue;
        }
        entries.add(toEntry(number, parser.parse(line, number)));
    ...
}

public abstract String label();

protected abstract LineParser createLineParser();
```

```java
public final class JsonLinesRepertoireImporter extends RepertoireImporter {
    @Override
    public String label() {
        return "JSONL";
    }

    @Override
    protected LineParser createLineParser() {
        return new JsonLineParser();
    }
}
```

**Pułapka:** kolejność błędów. W `start` nieobsługiwane rozszerzenie zgłaszał klient, zanim importer cokolwiek zrobił; `Importers.forFile` woła się na początku `load`, więc moment i komunikat błędu zostają te same. Druga pułapka to pokusa, żeby `createLineParser()` zwracał nowy parser w każdym obrocie pętli - szkielet tworzy go raz, bo produkt może być kosztowny (np. kompilacja wyrażeń). Alternatywa warta nazwania na sali: zamiast podklas importera można wstrzyknąć parser przez konstruktor (kompozycja, jak w s27). Wybieramy Factory Method, bo rodzina importerów i tak jest wybierana po typie pliku, a podklasa jest naturalnym miejscem na "jaki parser dla tego formatu"; gdyby importery zaczęły różnić się czymś więcej niż parserem, kompozycja byłaby lepsza.

`scripts/warsztat.sh diff exercises/e01 1 2`

**Macierz zmian dla formatu XML:** w `start` kopiujemy 30 linii pętli do trzeciej metody i dopisujemy gałęzie w dwóch `if`; po kroku 2 dodajemy `XmlLineParser`, `XmlRepertoireImporter` (6 linii) i jeden wiersz w `Importers`. `E01SolutionTest` pokazuje to na formacie TSV złożonym z anonimowych klas: sortowanie i zniżka poranna działają bez dotykania szkieletu.

**Co powiedzieć:** Metoda szablonowa odpowiada na pytanie "co jest stałe, a co się różni", a metoda wytwórcza na pytanie "czy to, co się różni, jest obiektem, który chcemy tworzyć i testować osobno". Jeden `if` po rozszerzeniu w `Importers` to nie wzorzec, tylko jedyne miejsce decyzji - i o to chodzi.

## Ćwiczenie e02. Mapa sali: sektory, rzędy, miejsca

**Pakiet:** `pl.training.workshop.exercises.e02_hallmap` · **Test:** `scripts/warsztat.sh test exercises/e02` · **Kroki:** 3

**W skrócie:** jedna struktura sala, sektor, rząd, miejsce kryje trzy niezależne zapachy. Agregaty liczone pętlami na każdym poziomie to Kompozyt, ręczna wędrówka klienta po poziomach z pomijaniem luk to Iterator, a dane kategorii skopiowane do każdego z 400 foteli to Pyłek. Wynik `describe` nie zmienia się ani o znak, łącznie z komunikatami błędów dla złych danych.

| Fragment kodu | Sygnał | Wzorzec | Dlaczego nie ... |
| --- | --- | --- | --- |
| cztery potrójne pętle w `HallMaps.describe`, z których dwie liczą sumy (pojemność, dopłaty) | ta sama operacja na każdym poziomie drzewa, wynik węzła = suma wyników dzieci | Kompozyt (Composite): `HallElement`, węzeł `HallGroup`, liść `Seat` | nie Odwiedzający: operacji jest mało i są stabilne (sumy), za to poziomów może przybywać (balkon, loża); Odwiedzający opłaca się przy odwrotnej osi zmian |
| dwie pozostałe pętle (zliczanie kategorii, lista miejsc) i `if (seat == null) continue` w każdej | klient zna kształt struktury i reguły przejścia (kolejność, luki) | Iterator: `SeatIterator`, `Hall implements Iterable<Seat>` | nie kolejne metody Kompozytu (`countByCategory`, `labels`): te pytania nie są agregatami rekurencyjnymi, tylko przejściem po liściach; każde nowe pytanie klienta wymagałoby nowej metody w całym drzewie |
| `Row.seatFor` kopiuje nazwę, dopłatę, ikonę i opis do każdego `Seat` | setki obiektów o identycznej, niemutowalnej części stanu | Pyłek (Flyweight): `SeatCategory` + fabryka `SeatCategories.of(code)` | nie `enum`: tu też by zadziałał, ale ćwiczymy jawne rozdzielenie stanu wspólnego (kategoria) od własnego (etykieta) i fabrykę, która gwarantuje jedną instancję; nie Prototyp: nie chcemy kopii, tylko współdzielenia |

### Krok 1: Composite (Kompozyt)

**Zmiany:**

1. Nowy interfejs `HallElement` z `capacity()` i `surcharges()` oraz klasa abstrakcyjna `HallGroup`, która trzyma niemutowalną listę dzieci `List<? extends HallElement>` i sumuje ich odpowiedzi.
2. `Hall`, `Section` i `Row` rozszerzają `HallGroup` (wołają `super(children)`), `Seat` implementuje `HallElement` (pojemność 1, dopłata własna).
3. `Row` nie trzyma już tablicy z `null`-ami: przejście po prostu nie tworzy dziecka, a numer miejsca nadal wynika z pozycji w układzie (`spec.label() + (i + 1)`), więc `A4` po luce zostaje `A4`.
4. W `HallMaps` dwie pętle znikają: `hall.capacity()` i `hall.surcharges()`.

**Przed (`HallMaps.java`):**

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
```

**Po (`HallGroup.java`, nowy plik):**

```java
public abstract class HallGroup implements HallElement {
    private final List<? extends HallElement> children;

    protected HallGroup(List<? extends HallElement> children) {
        this.children = List.copyOf(children);
    }

    @Override
    public final int capacity() {
        int capacity = 0;
        for (HallElement child : children) {
            capacity += child.capacity();
        }
        return capacity;
    }
    ...
}
```

**Po (`HallMaps.java`):**

```java
return "Sala " + hall.name() + ": pojemnosc " + hall.capacity() + ", doplaty " + hall.surcharges() + "\n"
```

**Pułapka:** Kompozyt bezpieczny czy przezroczysty? Tu dzieci są ustalane raz w konstruktorze i nie ma `add`, więc pytanie o `add` na liściu w ogóle nie powstaje. Walidacja pustego rzędu i sektora zostaje w metodach `of`, bo komunikaty błędów są kontraktem.

### Krok 2: Iterator (Iterator)

**Zmiany:**

1. Nowa klasa `SeatIterator implements Iterator<Seat>`: przejście w głąb po `HallGroup.children()` ze stosem iteratorów poziomów (`Deque<Iterator<? extends HallElement>>`), zwraca kolejne liście.
2. `Hall implements Iterable<Seat>`, `iterator()` zwraca `new SeatIterator(this)`.
3. Typowane akcesory `Hall.sections()`, `Section.rows()`, `Row.seats()` znikają, bo nikt ich już nie potrzebuje. `HallMaps` importuje tylko `Hall` i `Seat`, a dwie pozostałe pętle zlewają się w jedną `for (Seat seat : hall)`.

**Przed (`HallMaps.java`):**

```java
for (Section section : hall.sections()) {
    for (Row row : section.rows()) {
        for (Seat seat : row.seats()) {
            counts.merge(seat.categoryName(), 1, Integer::sum);
        }
    }
}
```

**Po (`HallMaps.java`):**

```java
for (Seat seat : hall) {
    counts.merge(seat.categoryName(), 1, Integer::sum);
    seats.add(seat.label() + " " + seat.icon());
}
```

**Po (`SeatIterator.java`, nowy plik, fragment):**

```java
private void advance() {
    next = null;
    while (!path.isEmpty()) {
        Iterator<? extends HallElement> level = path.peek();
        if (!level.hasNext()) {
            path.pop();
            continue;
        }
        HallElement element = level.next();
        if (element instanceof HallGroup group) {
            path.push(group.children().iterator());
        } else {
            next = (Seat) element;
            return;
        }
    }
}
```

**Pułapka:** kolejność przejścia jest częścią kontraktu (test porównuje listę `A1 o, A2 o, A4 *`), więc iterator musi iść w głąb i od lewej, dokładnie jak potrójna pętla. Wersja funkcyjna to `flatMap` po dzieciach - prostsza, ale wtedy warto pokazać, że to ten sam wzorzec w innej składni.

### Krok 3: Flyweight (Pyłek)

**Zmiany:**

1. Nowa klasa `SeatCategory` (kod, nazwa, dopłata, ikona, opis) - wszystkie pola `final`, bez żadnej wiedzy o rzędzie czy numerze.
2. Nowa fabryka `SeatCategories.of(char)`: `computeIfAbsent` na mapie, więc pierwsze pytanie o kod tworzy kategorię, każde kolejne zwraca tę samą instancję; nieznany kod rzuca ten sam `unknown seat category: X` co `switch` w `start`.
3. `Seat` ma tylko `label` i `category`; `Row.of` woła `new Seat(label, SeatCategories.of(code))`. `HallMaps` czyta `seat.category().name()` i `seat.category().icon()`.

**Przed (`Row.java`):**

```java
private static Seat seatFor(char code, String label) {
    return switch (code) {
        case 'N' -> new Seat(label, 'N', "Normalne", Money.ZERO, "o", "Miejsce standardowe");
        case 'V' -> new Seat(label, 'V', "VIP", Money.of("10.00"), "*", "Fotel skorzany z szerszym rozstawem");
        ...
    };
}
```

**Po (`Row.java`):**

```java
seats.add(new Seat(spec.label() + (i + 1), SeatCategories.of(code)));
```

**Po (`Seat.java`):**

```java
public final class Seat implements HallElement {
    private final String label;
    private final SeatCategory category;

    @Override
    public Money surcharges() {
        return category.surcharge();
    }
    ...
}
```

**Pułapka:** współdzielić wolno tylko stan niemutowalny. Gdyby `SeatCategory` miała np. licznik sprzedanych miejsc, każda sala psułaby wynik innej. Test `E02SolutionTest.fourHundredSeatsShareAtMostFourCategoryInstances` buduje salę 20 x 20 i sprawdza przez `IdentityHashMap`, że 400 foteli wskazuje na dokładnie dwie instancje kategorii.

**Co powiedzieć:** jedna struktura danych, trzy różne osie zmian: poziomy drzewa (Kompozyt), sposób przejścia (Iterator) i liczba obiektów (Pyłek). Dopiero nazwanie każdej osi osobno mówi, który fragment kodu ma się zmienić i dlaczego test równoważności nie drgnął.

## Ćwiczenie e03. Zmiana seansu i powiadomienia klientów

**Pakiet:** `pl.training.workshop.exercises.e03_notifications` · **Test:** `scripts/warsztat.sh test exercises/e03` · **Kroki:** 3

### Rozpoznanie

| Fragment kodu | Sygnał | Wzorzec | Dlaczego nie ... |
| --- | --- | --- | --- |
| `ScreeningScheduler.move/cancel` wołają na sztywno powiadomienia, `Waitlist` i `ChangeStats` | źródło zdarzenia zna konkretnych odbiorców; nowy odbiorca = edycja źródła; odbiorcy nic od siebie nie chcą | Obserwator (`ScreeningListener`) | nie Mediator: odbiorcy nie współpracują ze sobą, tylko reagują na jedno zdarzenie; nie Polecenie: nic nie jest kolejkowane ani cofane |
| sześć klas `{Email,Sms,Push}{Moved,Cancelled}Notice` | dwie niezależne osie (rodzaj wiadomości x kanał) pomnożone w hierarchii; treść skopiowana trzy razy, skracanie SMS dwa razy | Most (`Notice` x `Channel`) | nie Fabryka abstrakcyjna: nie tworzymy rodzin spójnych obiektów, tylko składamy dwie osie w czasie wysyłki; nie Strategia: kanał i treść są obie zmienne, a Strategia obsługuje jedną oś |
| blok `if (push) ... else if (phone) ... else if (email) ... else UNDELIVERED` w `move` i `cancel` | uporządkowany ciąg prób, w którym kolejność jest regułą, a koniec "wypada" z ostatniego `else`; ten sam blok w dwóch miejscach | Łańcuch zobowiązań (`PushLink -> SmsLink -> EmailLink -> UndeliveredLink`) | nie Dekorator: ogniwo, które obsłużyło żądanie, zatrzymuje je, a dekorator zawsze deleguje dalej; nie Strategia wybierana mapą: warunki nie są rozłącznymi kluczami, tylko priorytetami |

Trzy struktury odpowiadają na trzy różne pytania: Obserwator "kto reaguje na zmianę" (konfiguruje kino przy starcie), Most "co i którędy wysłać" (rodzaj wynika ze zdarzenia, kanał z danych klienta), Łańcuch "kto pierwszy spróbuje dostarczyć" (kolejność prób per klient).

### Krok 1: Observer (Obserwator)

**Zmiany:**

1. Nowy interfejs `ScreeningListener` z metodami `screeningMoved(ScreeningMoved)` i `screeningCancelled(ScreeningCancelled)` oraz dwa rekordy zdarzeń niosące wszystko, czego odbiorcy potrzebują (seans przed i po, powód, posiadacze biletów).
2. `ScreeningScheduler` traci konstruktorowe zależności od `Waitlist` i `ChangeStats`; dostaje `subscribe`/`unsubscribe` i listę odbiorców, po której iteruje po zapisaniu zmiany.
3. Powiadamianie klientów przenosi się bez zmian do pierwszego odbiorcy `CustomerNotifier` (Extract Class); `Waitlist` i `ChangeStats` implementują interfejs.
4. `ScreeningDesk` rejestruje odbiorców w kolejności, która była zaszyta w schedulerze: powiadomienia, lista oczekujących, statystyki.

**Przed (`ScreeningScheduler.java`):**

```java
journal.note("SCHEDULE " + before.title() + " " + before.start() + " -> " + newStart + " sala " + newHall);
for (Customer customer : ticketHolders) {
    if (customer.pushToken() != null && !customer.pushToken().isBlank()
            && !customer.pushToken().startsWith("expired-")) {
        new PushMovedNotice(journal, before, after).send(customer);
    } else if (customer.phone() != null && !customer.phone().isBlank()) {
        new SmsMovedNotice(journal, before, after).send(customer);
    } else if (customer.email() != null && !customer.email().isBlank()) {
        new EmailMovedNotice(journal, before, after).send(customer);
    } else {
        journal.note("UNDELIVERED " + customer.name() + " (brak kanalu)");
    }
}
waitlist.screeningMoved(after);
stats.moved();
```

**Po (`ScreeningScheduler.java`):**

```java
journal.note("SCHEDULE " + before.title() + " " + before.start() + " -> " + newStart + " sala " + newHall);
ScreeningMoved event = new ScreeningMoved(before, after, List.copyOf(ticketHolders));
for (ScreeningListener listener : List.copyOf(listeners)) {
    listener.screeningMoved(event);
}
```

**Po (`ScreeningDesk.java`):**

```java
ScreeningScheduler scheduler = new ScreeningScheduler(journal);
scheduler.subscribe(new CustomerNotifier(journal));
scheduler.subscribe(new Waitlist(journal, scenario.waiting()));
scheduler.subscribe(new ChangeStats(journal));
```

**Pułapka:** kolejność powiadamiania to kolejność rejestracji i jest częścią kontraktu (test sprawdza, że `WAITLIST` jest po powiadomieniach, a `STATS` ostatnie). Polityka błędów też: wyjątek odbiorcy przerywa powiadamianie następnych, tak jak w `start`. Zdarzenie niesie dane, nie referencję do schedulera, więc odbiorca nie może "przy okazji" zmienić repertuaru.

### Krok 2: Bridge (Most)

**Zmiany:**

1. Interfejs `Channel.deliver(customer, subject, body)` i trzy implementacje: `EmailChannel`, `SmsChannel` (sklejanie i limit 70 znaków w jednym miejscu), `PushChannel`.
2. Abstrakcja `Notice` z finalną metodą `send(customer)`, która deleguje do kanału przekazanego w konstruktorze; podklasy `MovedNotice` i `CancelledNotice` dostarczają tylko `subject()` i `body()`.
3. Sześć klas `{Email,Sms,Push}{Moved,Cancelled}Notice` znika. `CustomerNotifier` ma jedną metodę `channelFor(customer)` zwracającą `Optional<Channel>`.

**Przed (`SmsMovedNotice.java`, jedna z sześciu):**

```java
public void send(Customer customer) {
    String subject = "Zmiana seansu: " + after.title();
    String body = "Seans " + after.title() + " z " + before.start() + " przeniesiony na " + after.start()
            + ", sala " + after.hall() + ". Bilety zachowuja waznosc.";
    String text = subject + ". " + body;
    if (text.length() > 70) {
        text = text.substring(0, 67) + "...";
    }
    journal.sms(customer.phone(), text);
}
```

**Po (`Notice.java`):**

```java
public abstract class Notice {
    private final Channel channel;

    protected Notice(Channel channel) {
        this.channel = Objects.requireNonNull(channel, "channel");
    }

    public final void send(Customer customer) {
        channel.deliver(customer, subject(), body());
    }

    protected abstract String subject();

    protected abstract String body();
}
```

**Po (`SmsChannel.java`):**

```java
@Override
public void deliver(Customer customer, String subject, String body) {
    String text = subject + ". " + body;
    if (text.length() > MAX_LENGTH) {
        text = text.substring(0, MAX_LENGTH - 3) + "...";
    }
    journal.sms(customer.phone(), text);
}
```

**Pułapka:** granica między osiami musi przebiegać tak, żeby obie strony były naprawdę niezależne. Jeśli kanał dostanie gotowy, sklejony tekst, SMS nie będzie mógł sam decydować o skracaniu; jeśli dostanie cały `Notice`, kanał zacznie zależeć od rodzaju wiadomości. Kontrakt `deliver(customer, subject, body)` jest minimum, które oba kanały i oba rodzaje spełniają. Test `newChannelIsOneClassWorkingWithEveryKindOfNotice` pokazuje, że nowy kanał to jedna lambda działająca z każdą wiadomością.

### Krok 3: Chain of Responsibility (Łańcuch zobowiązań)

**Zmiany:**

1. Cztery ogniwa implementujące ten sam interfejs `Channel`: `PushLink`, `SmsLink`, `EmailLink` (każde ma swój kanał i jednego następnika) oraz `UndeliveredLink` jako jawny koniec, który zapisuje wpis o niedostarczeniu.
2. `CustomerNotifier` dostaje początek łańcucha w konstruktorze jako zwykły `Channel` i nie zawiera żadnego `if` po danych kontaktowych; `MovedNotice`/`CancelledNotice` powstają raz na zdarzenie.
3. `ScreeningDesk.standardDelivery(journal)` składa ścieżkę push, SMS, e-mail, niedostarczone. Inna ścieżka (festiwal: tylko e-mail) to inny skład w korzeniu kompozycji.

**Przed (`CustomerNotifier.java`, krok 2):**

```java
private Optional<Channel> channelFor(Customer customer) {
    if (customer.pushToken() != null && !customer.pushToken().isBlank()
            && !customer.pushToken().startsWith("expired-")) {
        return Optional.of(push);
    }
    if (customer.phone() != null && !customer.phone().isBlank()) {
        return Optional.of(sms);
    }
    if (customer.email() != null && !customer.email().isBlank()) {
        return Optional.of(email);
    }
    return Optional.empty();
}
```

**Po (`PushLink.java`):**

```java
@Override
public void deliver(Customer customer, String subject, String body) {
    String token = customer.pushToken();
    if (token != null && !token.isBlank() && !token.startsWith("expired-")) {
        push.deliver(customer, subject, body);
    } else {
        next.deliver(customer, subject, body);
    }
}
```

**Po (`ScreeningDesk.java`):**

```java
static Channel standardDelivery(Journal journal) {
    return new PushLink(new PushChannel(journal),
            new SmsLink(new SmsChannel(journal),
                    new EmailLink(new EmailChannel(journal),
                            new UndeliveredLink(journal))));
}
```

**Pułapka:** ogniwo, które obsłużyło żądanie, zatrzymuje je (test `linkPassesOnOnlyWhatItCannotDeliver` liczy wywołania następnika), inaczej klient z tokenem dostałby też SMS. To właśnie odróżnia tę strukturę od Dekoratora, który zawsze deleguje dalej. Koniec łańcucha musi być jawny: bez `UndeliveredLink` klient bez kontaktu "wypadałby" po cichu, a test równoważności wymaga wpisu `UNDELIVERED`.

### Diff i pytania do sali

```bash
scripts/warsztat.sh diff exercises/e03 0 1   # scheduler traci konkretnych odbiorców
scripts/warsztat.sh diff exercises/e03 1 2   # sześć klas znika, powstają Notice i Channel
scripts/warsztat.sh diff exercises/e03 2 3   # channelFor zamienia się w cztery ogniwa
```

- Czy Obserwator byłby dobrym wyborem, gdyby lista oczekujących musiała po odwołaniu zapytać statystyki o liczbę wolnych miejsc? (Nie, wtedy odbiorcy współpracują i potrzebny jest Mediator albo prosta orkiestracja.)
- Czy Most opłacałby się przy jednym kanale? (Nie, przy jednej osi wystarczy polimorfizm po rodzaju wiadomości.)
- Czy audyt "kto został powiadomiony" to kolejne ogniwo, czy dekorator na `Channel`? (Dekorator: ma działać dla każdego kanału i zawsze przepuszczać dalej.)

**Co powiedzieć:** jedna klasa `ScreeningScheduler` kryła trzy różne rodzaje zmienności i każdy dostał inny mechanizm. Obserwator odpowiada na pytanie "kto reaguje", Most na "co i którędy wysłać", Łańcuch na "kto pierwszy spróbuje". Wszystkie trzy decyzje widać teraz w jednym miejscu, w `ScreeningDesk`, a test równoważności dowodzi, że dziennik efektów jest po każdym kroku identyczny.

## Ćwiczenie e04. Koszyk miejsc z cofaniem

**Pakiet:** `pl.training.workshop.exercises.e04_seatpicker` · **Test:** `scripts/warsztat.sh test exercises/e04` · **Kroki:** 2

**W skrócie:** `SeatPicker` ze `start` jest jednocześnie koszykiem, parserem skryptu, historią i mechanizmem cofania. Dwa zapachy prowadzą do dwóch różnych wzorców: łańcuch `if` w `undo` z rekordem `Change` „na wszystko” to Polecenie (Command) z `execute/undo`, a ręczny punkt kontrolny przed AUTO, który gubi pole dopisane później, to Pamiątka (Memento). Oba dotyczą cofania, ale odpowiadają na inne pytanie: „jak odwrócić tę jedną zmianę?” i „jak wrócić do zapisanego punktu, nie znając budowy obiektu?”.

| Fragment kodu | Sygnał | Wzorzec | Dlaczego nie ... |
| --- | --- | --- | --- |
| `apply`: `switch` po nazwie operacji; `undo`: drugi łańcuch `if` po `change.op()` z odwrotnością każdej operacji; rekord `Change(op, seat, other, index, flag, text, seatsBefore)` | każda nowa operacja to zmiana w dwóch metodach i w rekordzie; dane do cofnięcia leżą w strukturze wspólnej dla wszystkich operacji, z polami „na wszelki wypadek”; operacje trafiają na stos i są cofane w kolejności odwrotnej | Command: `Command.execute()/undo()`, po klasie na operację, stos `Deque<Command>` w wywołującym | nie Strategy: operacja nie jest wymiennym algorytmem tej samej czynności, tylko żądaniem zapisywanym w historii i odwracanym; nie State: zachowanie nie zależy od stanu koszyka, tylko od tego, co kasjer wpisał |
| `autoArrange`: `seatsBefore = new ArrayList<>(seats); glassesBefore = ownGlasses;` ... `catch { seats.clear(); seats.addAll(seatsBefore); ownGlasses = glassesBefore; }`; to samo w `undo` dla `"AUTO"` | ktoś spoza obiektu kopiuje jego pola i je przywraca; kopia jest niepełna (voucher doszedł później), więc enkapsulacja została złamana i to się zemściło | Memento: `Cart.snapshot()` zwraca nieprzezroczysty `Cart.Memento`, `Cart.restore(memento)` przywraca całość | nie „undo przez odwrotność”: AUTO zmienia trzy pola naraz i nie ma prostej odwrotności; nie Prototype: nie potrzebujemy drugiego działającego koszyka, tylko zapisu jego wnętrza, którego nikt poza koszykiem nie czyta |

### Krok 1: Command - operacja i jej odwrotność w jednym obiekcie

**Zmiany:**

1. Extract Class: `Cart` dostaje pola koszyka (`seats`, `ownGlasses`, `voucher`, `plan`), walidację (`checkFree`, limit 4), sumę i `state()`. Operacje elementarne zwracają to, co potrzebne do cofnięcia: `remove(seat)` i `replace(from, to)` zwracają pozycję.
2. Nowy interfejs `Command { void execute(); void undo(); }` i siedem klas: `SelectSeat`, `DeselectSeat`, `SwapSeats`, `SetGlasses`, `SetVoucher`, `ClearSeats`, `AutoArrange`. Każda trzyma odbiorcę (`Cart`), argumenty z linii skryptu i własne dane do cofnięcia (`index`, `before`).
3. `SeatPicker` zostaje wywołującym: `parse(op, parts)` tłumaczy linię na nowy obiekt polecenia, `apply` wykonuje je i odkłada na `Deque<Command>`, `UNDO` zdejmuje ostatnie i woła `undo()`. Rekord `Change` i łańcuch `if` w `undo` znikają.
4. Punkt kontrolny przed AUTO przenosi się do `AutoArrange` bez zmian - nadal kopiuje tylko miejsca i okulary. Krok 1 nie zmienia zachowania, więc test równoważności jest zielony, a voucher nadal ginie.

**Przed (`SeatPicker.java`, start):**

```java
private void undo() {
    if (history.isEmpty()) {
        throw new IllegalStateException("nie ma czego cofnac");
    }
    Change change = history.pop();
    if (change.op().equals("SELECT")) {
        seats.remove(change.seat());
    } else if (change.op().equals("DESELECT")) {
        seats.add(change.index(), change.seat());
    } else if (change.op().equals("SWAP")) {
        seats.set(change.index(), change.seat());
    } else if (change.op().equals("GLASSES")) {
        ownGlasses = change.flag();
    } else if (change.op().equals("VOUCHER")) {
        voucher = change.text();
    } else if (change.op().equals("CLEAR")) {
        seats.addAll(change.seatsBefore());
    } else if (change.op().equals("AUTO")) {
        seats.clear();
        seats.addAll(change.seatsBefore());
        ownGlasses = change.flag();
    }
}
```

**Po (`SeatPicker.java`, krok 1):**

```java
public void apply(String line) {
    String[] parts = line.trim().split("\\s+");
    String op = parts[0].toUpperCase();
    if (op.equals("UNDO")) {
        undo();
        return;
    }
    Command command = parse(op, parts);
    command.execute();
    history.push(command);
}

private Command parse(String op, String[] parts) {
    return switch (op) {
        case "SELECT" -> new SelectSeat(cart, Seat.parse(arg(parts, 1)));
        case "DESELECT" -> new DeselectSeat(cart, Seat.parse(arg(parts, 1)));
        case "SWAP" -> new SwapSeats(cart, Seat.parse(arg(parts, 1)), Seat.parse(arg(parts, 2)));
        case "GLASSES" -> new SetGlasses(cart, parseGlasses(arg(parts, 1)));
        case "VOUCHER" -> new SetVoucher(cart, parseVoucher(arg(parts, 1)));
        case "CLEAR" -> new ClearSeats(cart);
        case "AUTO" -> new AutoArrange(cart, parseCount(arg(parts, 1)));
        default -> throw new IllegalArgumentException("nieznana operacja: " + parts[0]);
    };
}

private void undo() {
    if (history.isEmpty()) {
        throw new IllegalStateException("nie ma czego cofnac");
    }
    history.pop().undo();
}
```

**Po (`DeselectSeat.java`, krok 1):**

```java
public final class DeselectSeat implements Command {
    private final Cart cart;
    private final Seat seat;
    private int index;

    @Override
    public void execute() {
        index = cart.remove(seat);
    }

    @Override
    public void undo() {
        cart.insert(index, seat);
    }
}
```

**Pułapka:** kolejność w `apply` jest częścią kontraktu: polecenie trafia na stos dopiero po udanym `execute()`. Jeśli odłożysz je wcześniej, nieudany SELECT (miejsce zajęte) zostawi na stosie coś, czego UNDO nie umie cofnąć, i test „nie ma czego cofnac” po błędzie się rozjedzie. Druga pułapka: `switch` po nazwie operacji w `parse` zostaje. To nie jest ten sam zapach co w `undo`: tłumaczenie tekstu na obiekt musi gdzieś być, ważne, że jest w jednym miejscu i nie wie nic o cofaniu.

**Co powiedzieć:** „Każda linia skryptu dostaje własny obiekt, który wie, jak się wykonać i jak się cofnąć. Historia nie musi już wiedzieć, czym był SELECT, trzyma tylko obiekty i woła `undo()`.”

### Krok 2: Memento - migawka koszyka zamiast ręcznej kopii pól

**Zmiany:**

1. `Cart` dostaje zagnieżdżoną klasę `Memento` z prywatnymi polami (`seats`, `ownGlasses`, `voucher`), prywatnym konstruktorem i bez metod. Tworzy ją tylko `Cart.snapshot()`, czyta tylko `Cart.restore(memento)`.
2. `AutoArrange` zamiast kopiować pola woła `before = cart.snapshot()` przed czyszczeniem, a w `catch` i w `undo()` woła `cart.restore(before)`. Pola `seatsBefore` i `glassesBefore` znikają, razem z metodą `Cart.replaceAll`, której potrzebowała tylko ręczna kopia.
3. Pozostałe polecenia zostają przy odwrotności: `SelectSeat` cofa się przez `remove`, `SetVoucher` przez poprzedni kod. Migawka całego koszyka dla zmiany jednego pola byłaby przesadą i kopiowałaby listę miejsc przy każdej linii skryptu.

**Przed (`AutoArrange.java`, krok 1):**

```java
private List<Seat> seatsBefore = List.of();
private boolean glassesBefore;

@Override
public void execute() {
    ...
    seatsBefore = cart.seats();
    glassesBefore = cart.ownGlasses();
    cart.clear();
    cart.setOwnGlasses(false);
    cart.setVoucher(null);
    try {
        ...
    } catch (IllegalStateException exception) {
        undo();
        throw exception;
    }
}

@Override
public void undo() {
    cart.replaceAll(seatsBefore);
    cart.setOwnGlasses(glassesBefore);
}
```

**Po (`Cart.java`, krok 2):**

```java
/** Migawka koszyka: tylko koszyk ją tworzy i tylko koszyk ją czyta. */
public static final class Memento {
    private final List<Seat> seats;
    private final boolean ownGlasses;
    private final String voucher;

    private Memento(List<Seat> seats, boolean ownGlasses, String voucher) {
        this.seats = List.copyOf(seats);
        this.ownGlasses = ownGlasses;
        this.voucher = voucher;
    }
}

public Memento snapshot() {
    return new Memento(seats, ownGlasses, voucher);
}

public void restore(Memento memento) {
    seats.clear();
    seats.addAll(memento.seats);
    ownGlasses = memento.ownGlasses;
    voucher = memento.voucher;
}
```

**Po (`AutoArrange.java`, krok 2):**

```java
private Cart.Memento before;

@Override
public void execute() {
    ...
    before = cart.snapshot();
    cart.clear();
    cart.setOwnGlasses(false);
    cart.setVoucher(null);
    try {
        ...
    } catch (IllegalStateException exception) {
        undo();
        throw exception;
    }
}

@Override
public void undo() {
    cart.restore(before);
}
```

**Pułapka:** ten krok zmienia zachowanie i trzeba to powiedzieć wprost. Pełna migawka przywraca też voucher, którego ręczna kopia nie obejmowała: skrypt `VOUCHER KINO10, SELECT 1-1, AUTO 2` w prawie pełnej sali kończył się w `start` wynikiem `voucher: - | suma: 35.00`, a w kroku 2 daje `voucher: KINO10 | suma: 31.50`. To samo dla `UNDO` po udanym AUTO. Test równoważności nie ma tych skryptów, a `E04SolutionTest` dokumentuje oba wyniki: stary jako błąd, nowy jako uzgodnioną naprawę. Gdyby zespół chciał zachować stary kontrakt, migawka musiałaby celowo pomijać voucher, co byłoby decyzją, a nie przypadkiem. Druga pułapka: `Memento` musi kopiować listę (`List.copyOf`), inaczej `restore` po `clear()` przywróci pustą listę, bo migawka trzymałaby referencję do tej samej `ArrayList`.

**Co powiedzieć:** „Cofnięcie jednej zmiany i powrót do zapisanego punktu to dwa różne problemy. Polecenie zna swoją odwrotność, ale AUTO zmienia trzy pola naraz, więc nie ma odwrotności - ma zapis. Zapis robi sam koszyk, bo tylko on zna wszystkie swoje pola; gdy dojdzie czwarte, dopiszemy je w `Memento` i nigdzie indziej.”

## Ćwiczenie e05. Karta podarunkowa i reguły voucherów

**Pakiet:** `pl.training.workshop.exercises.e05_giftcard` · **Test:** `scripts/warsztat.sh test exercises/e05` · **Kroki:** 2

| Fragment kodu | Sygnał | Wzorzec | Dlaczego nie ... |
| --- | --- | --- | --- |
| `GiftCard.activate/charge/block/expire`: `switch (status)` w każdej metodzie | gałąź wybiera **bieżący status obiektu**, a wynik operacji zmienia ten status; te same komunikaty błędów powtórzone w kilku metodach | **State** (Stan) | nie Strategy: wariantu nie wybiera klient z danych wejściowych, tylko sam obiekt w trakcie życia; nie Command: operacje nie są zapisywane ani cofane, zmienia się reakcja na tę samą operację |
| `Vouchers.applies` i `Vouchers.describe`: dwa rozbiory tego samego tekstu reguły | reguła ma **gramatykę** (pojęcia, `NOT`, `AND`), a na tym samym "zdaniu" wykonujemy już dwie operacje (ocena, opis); nowe pojęcie to zmiana w obu | **Interpreter** | nie Strategy: nie wymieniamy całego algorytmu, tylko składamy go z małych klocków; nie Chain of Responsibility: wszystkie pojęcia muszą być ocenione, żadne nie "obsługuje i kończy"; nie Visitor: operacji są dwie i stabilne, a pojęć będzie przybywać |
| `GiftCards.simulate`: `switch` po nazwie operacji | dyspozytor po tekście z zewnątrz | (poza zakresem) | to kandydat na Command / rejestr jak w m5/s15, ale test wymaga tylko kontraktu klienta; warto to nazwać na sali: nie każdy `switch` w pliku jest tą samą osią zmienności |

### Krok 1: State (Stan)

**Zmiany:**

1. Nowy interfejs `CardState` z metodami `name()`, `activate()`, `charge(GiftCard, Money)`, `block()`, `expire()`; każda metoda przejścia zwraca stan następny.
2. Pięć klas `IssuedState`, `ActiveState`, `EmptyState`, `BlockedState`, `ExpiredState`, każda z jedną współdzieloną instancją `INSTANCE` (stany są bezstanowe, saldo zostaje w karcie). Każda klasa to jeden wiersz tabeli przejść z testu.
3. `GiftCard` zamienia pole `String status` na `CardState state` i deleguje: `state = state.activate()`. Walidacja wspólna (saldo początkowe, dodatnia kwota) zostaje w karcie, bo nie zależy od stanu. `ActiveState.charge` zmniejsza saldo przez pakietowe `card.debit(amount)` i sam decyduje o przejściu do `EmptyState`.

**Przed (`GiftCard.java`, start):**

```java
public void charge(Money amount) {
    if (!amount.isGreaterThan(Money.ZERO)) {
        throw new IllegalArgumentException("amount must be positive");
    }
    switch (status) {
        case "ISSUED" -> throw new IllegalStateException("card not activated");
        case "ACTIVE" -> {
            if (amount.isGreaterThan(balance)) {
                throw new IllegalStateException("insufficient balance");
            }
            balance = balance.minus(amount);
            if (balance.compareTo(Money.ZERO) == 0) {
                status = "EMPTY";
            }
        }
        case "EMPTY" -> throw new IllegalStateException("card is empty");
        case "BLOCKED" -> throw new IllegalStateException("card is blocked");
        case "EXPIRED" -> throw new IllegalStateException("card has expired");
        default -> throw new IllegalStateException("unknown status: " + status);
    }
}
```

**Po (`GiftCard.java`, krok 1):**

```java
private CardState state = IssuedState.INSTANCE;

public void charge(Money amount) {
    if (!amount.isGreaterThan(Money.ZERO)) {
        throw new IllegalArgumentException("amount must be positive");
    }
    state = state.charge(this, amount);
}

void debit(Money amount) {
    balance = balance.minus(amount);
}
```

**Po (`ActiveState.java`, krok 1):**

```java
@Override
public CardState charge(GiftCard card, Money amount) {
    if (amount.isGreaterThan(card.balance())) {
        throw new IllegalStateException("insufficient balance");
    }
    card.debit(amount);
    return card.balance().compareTo(Money.ZERO) == 0 ? EmptyState.INSTANCE : this;
}
```

**Pułapka:** kolejność sprawdzeń jest kontraktem. W start ujemna kwota na karcie wygasłej daje `amount must be positive`, nie `card has expired`, bo walidacja kwoty stoi przed `switch`. Jeśli przeniesiesz ją do stanów, przypadek "kwota ujemna na wygasłej - walidacja przed statusem" zrobi się czerwony. Druga pułapka: `EmptyState` wyznacza stan **aktywny**, nie karta, inaczej logika przejścia zostaje w kontekście i `switch` wraca tylnymi drzwiami.

`scripts/warsztat.sh diff exercises/e05 0 1`

### Krok 2: Interpreter (Interpreter)

**Zmiany:**

1. Nowy `sealed interface VoucherRule` z dwiema operacjami: `applies(ScreeningFacts)` i `describe()`.
2. Wyrażenia terminalne jako rekordy: `FormatIs(format)`, `DayIn(days)`, `IsPremiere()`; nieterminalne: `Not(rule)`, `And(left, right)` oraz `Or(left, right)`, którego składnia tekstowa jeszcze nie zna.
3. `VoucherRuleParser.parse(String)` jako jedyne miejsce znające tekst: `split(" AND ")`, prefiks `NOT `, `format=`, `day IN `, `premiere`, mapowanie `MON..SUN`. Buduje lewostronną koniunkcję, więc `describe` daje ten sam tekst co `String.join(" oraz ", ...)` w start.
4. `Vouchers.applies` i `Vouchers.describe` to po jednej linii: `VoucherRuleParser.parse(rule).applies(facts)` / `.describe()`.

**Przed (`Vouchers.java`, start, fragment `describe`; `applies` ma bliźniaczy rozbiór):**

```java
for (String term : rule.split(" AND ")) {
    String trimmed = term.trim();
    boolean negated = trimmed.startsWith("NOT ");
    String body = negated ? trimmed.substring(4).trim() : trimmed;
    String text;
    if (body.startsWith("format=")) {
        text = "format " + body.substring("format=".length()).trim();
    } else if (body.startsWith("day IN ")) {
        ...
    } else if (body.equals("premiere")) {
        text = "premiera";
    } else {
        throw new IllegalArgumentException("unknown rule term: " + body);
    }
    parts.add(negated ? "nie " + text : text);
}
return String.join(" oraz ", parts);
```

**Po (`VoucherRuleParser.java` i `And.java`, krok 2):**

```java
public static VoucherRule parse(String rule) {
    if (rule == null || rule.isBlank()) {
        throw new IllegalArgumentException("rule must not be empty");
    }
    VoucherRule result = null;
    for (String term : rule.split(" AND ")) {
        VoucherRule parsed = term(term.trim());
        result = result == null ? parsed : new And(result, parsed);
    }
    return result;
}
```

```java
public record And(VoucherRule left, VoucherRule right) implements VoucherRule {
    public boolean applies(ScreeningFacts facts) {
        return left.applies(facts) && right.applies(facts);
    }

    public String describe() {
        return left.describe() + " oraz " + right.describe();
    }
}
```

**Pułapka:** moment błędu. Start ocenia **wszystkie** pojęcia (`result &= ...`), więc literówka w trzecim pojęciu wychodzi nawet, gdy pierwsze jest fałszywe. Po refaktoryzacji `And.applies` może skracać obliczenie, bo błędy składni padają już w `parse`, przed oceną - przypadek "nieznane pojęcie po fałszywym" zostaje zielony. Gdyby parser był leniwy (rozbiór podczas oceny), kontrakt by się zmienił. Druga pułapka: `NOT NOT premiere` w start to nieznane pojęcie, więc parser celowo nie rekurencyjnie obsługuje `NOT` (`new Not(atom(...))`), a nie `new Not(term(...))`. Rozszerzenie składni (podwójne NOT, `OR`, nawiasy) to osobna, świadoma zmiana kontraktu - `E05SolutionTest` pokazuje, że w kodzie `Or` już działa, a tekst `premiere OR format=IMAX` nadal jest odrzucany.

`scripts/warsztat.sh diff exercises/e05 1 2`

**Co powiedzieć:** Oba `switch`-e w tej scenie wyglądają podobnie, ale mają inną oś zmienności. W karcie o gałęzi decyduje bieżący status obiektu i to operacja go zmienia - stąd obiekty stanów. W voucherze zmienność jest w **języku** reguły: przybywa pojęć, a operacje na regule (ocena, opis) są stabilne - stąd drzewo wyrażeń i jeden parser. Test równoważności pilnuje, że kolejność walidacji i moment błędu nie zmieniły się po drodze.

## Ćwiczenie e06. Informacje o filmie z dwóch obcych źródeł

**Pakiet:** `pl.training.workshop.exercises.e06_movieinfo` · **Test:** `scripts/warsztat.sh test exercises/e06` · **Kroki:** 3 (Adapter, Proxy, Singleton)

Jedna klasa `MovieInfoService` kryje trzy różne rodzaje zmienności i każdy dostaje inny mechanizm. Wspólny motyw dwóch z nich: obiekt o **tym samym interfejsie** co to, co owija. Różni je cel - adapter zmienia interfejs obcego klienta na nasz, zastępca zachowuje interfejs i dokłada kontrolę dostępu do niego.

| Fragment kodu | Sygnał | Wzorzec | Dlaczego nie ... |
| --- | --- | --- | --- |
| `fetch`: `replace(" min", "")`, `substring(0, indexOf('/'))`, `BigDecimal.valueOf(ratingTimesTen, 1)`, `Response=False` kontra `null` | serwis zna słownictwo dwóch obcych systemów (jednostki, format, sposób zgłaszania błędu); nowe źródło = nowa gałąź `if` | **Adapter** (po jednym na źródło, wspólny port `MovieCatalog`) | nie Facade: nie upraszczamy wielu podsystemów, tłumaczymy jeden interfejs na drugi; nie Strategy: warianty nie są wymiennymi algorytmami tego samego obliczenia, tylko różnymi językami |
| `if (!user.staff())` i `cache.containsKey` w `lookup` i `runtime` | przekrojowe "przed wywołaniem": kontrola dostępu i pamięć podręczna, nie zmieniają wyniku, powtarzają się w każdej metodzie | **Proxy** (ochronny `AuthorizingLookup` na interfejsie serwisu, pamięć podręczna `CachingCatalog` na porcie katalogu) | nie Decorator: nie dokładamy nowej funkcji do wyniku, tylko kontrolujemy dostęp do podmiotu (ochrona, cache); strukturalnie podobne, różni je intencja i to, że zastępca zwykle sam decyduje, czy w ogóle zawołać cel |
| `AgeRatingTable.load()` przy każdym wyszukaniu | obiekt niemutowalny, równoważny w każdej kopii, kosztowny w tworzeniu, brak decyzji o cyklu życia | **Singleton** (enum za interfejsem `AgeRatings`, wstrzykiwany) | nie statyczna metoda narzędziowa: adapter ma zależeć od kontraktu, żeby test mógł podać inną tabelę; singleton bez interfejsu i wstrzyknięcia to ukryta zależność globalna (slajd 17.4) |

### Krok 1: Adapter (Adapter)

`scripts/warsztat.sh diff exercises/e06 0 1`

**Zmiany:**

1. Nowy port `MovieCatalog` w języku kina: `MovieDetails find(String id)`, nieznany film zawsze jako `IllegalArgumentException("unknown movie: ...")`.
2. Dwie gałęzie `fetch` stają się dwiema klasami: `OmdbCatalogAdapter` (zna `"155 min"`, `"8.0/10"`, `Response=False`, kody `PG-13`) i `LegacyCatalogAdapter` (zna ocenę x10, kod wieku jako tekst, `null`).
3. Serwis dostaje `Map<MovieSource, MovieCatalog>`; mapę składa korzeń kompozycji `MovieInfo`.

**Przed (`MovieInfoService.fetch`, start):**

```java
if (source == MovieSource.OMDB) {
    Map<String, String> raw = omdb.byId(id);
    if ("False".equals(raw.get("Response"))) {
        throw new IllegalArgumentException("unknown movie: " + id);
    }
    AgeRatingTable table = AgeRatingTable.load();
    String rating = raw.get("imdbRating");
    return new MovieDetails(raw.get("Title"), Integer.parseInt(raw.get("Year")),
            Integer.parseInt(raw.get("Runtime").replace(" min", "")),
            new BigDecimal(rating.substring(0, rating.indexOf('/'))),
            table.categoryFor("OMDB", raw.get("Rated")));
}
LegacyMovieRow row = legacy.find(id);
if (row == null) {
    throw new IllegalArgumentException("unknown movie: " + id);
}
...
```

**Po (`MovieInfoService`, krok 1):**

```java
private final Map<MovieSource, MovieCatalog> catalogs;

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
}
```

**Po (`MovieInfo`, krok 1):**

```java
this.service = new MovieInfoService(Map.of(
        MovieSource.OMDB, new OmdbCatalogAdapter(omdb),
        MovieSource.LEGACY, new LegacyCatalogAdapter(legacy)));
```

**Pułapka:** adapter tłumaczy nie tylko nazwy pól. Jednostki (minuty z `"155 min"`, ocena ze skali x10 na `8.0`), format (`"8.0/10"`) i **sposób zgłaszania błędu** (`Response=False` i `null` oba stają się tym samym wyjątkiem z tym samym komunikatem) to kontrakt portu. Test równoważności ma po jednym przypadku nieznanego filmu na źródło, bo tu najłatwiej zgubić zachowanie.

### Krok 2: Proxy (Pełnomocnik)

`scripts/warsztat.sh diff exercises/e06 1 2`

**Zmiany:**

1. Extract Interface `MovieLookup` z serwisu (`lookup`, `runtime`).
2. `AuthorizingLookup implements MovieLookup`: jedno `requireStaff`, potem delegacja. Serwis traci oba `if (!user.staff())`.
3. `CachingCatalog implements MovieCatalog`: mapa po `id`, delegacja przy chybieniu, nieudane wyszukanie nie jest zapamiętywane (jak w start). Serwis traci `cache`.
4. `MovieInfoService` zostaje prawdziwym podmiotem: `catalogs.get(source).find(id)`.
5. Korzeń kompozycji składa łańcuch: `new AuthorizingLookup(new MovieInfoService(Map.of(OMDB, new CachingCatalog(new OmdbCatalogAdapter(omdb)), ...)))`.

**Przed (`MovieInfoService.runtime`, krok 1):**

```java
public int runtime(User user, MovieSource source, String id) {
    if (!user.staff()) {
        throw new IllegalStateException("brak uprawnien: " + user.name());
    }
    String key = source + ":" + id;
    if (cache.containsKey(key)) {
        return cache.get(key).runtimeMinutes();
    }
    return catalogs.get(source).find(id).runtimeMinutes();
}
```

**Po (`MovieInfoService`, krok 2):**

```java
public final class MovieInfoService implements MovieLookup {
    private final Map<MovieSource, MovieCatalog> catalogs;

    @Override
    public MovieDetails lookup(User user, MovieSource source, String id) {
        return catalogs.get(source).find(id);
    }

    @Override
    public int runtime(User user, MovieSource source, String id) {
        return lookup(user, source, id).runtimeMinutes();
    }
}
```

**Po (`AuthorizingLookup`, krok 2):**

```java
public final class AuthorizingLookup implements MovieLookup {
    private final MovieLookup target;

    @Override
    public MovieDetails lookup(User user, MovieSource source, String id) {
        requireStaff(user);
        return target.lookup(user, source, id);
    }
    ...
}
```

**Pułapka:** kolejność owijania jest zachowaniem. Strażnik musi być **na zewnątrz** pamięci podręcznej: gdyby cache był pierwszy, gość dostałby dane filmu, o który wcześniej zapytał kasjer (albo wymusiłby zapytanie do sieci). `E06SolutionTest.guestNeverReachesTheSources` liczy wywołania obu klientów po odmowie: muszą być zerem. Drugi szczegół: w start `runtime` nie zapisywał do cache, tylko z niego czytał; po kroku 2 `runtime` idzie przez `lookup`, więc też zapełnia pamięć. Wynik jest ten sam, liczba zapytań mniejsza - to poprawa niewidoczna dla testu równoważności, więc nazywamy ją na głos.

### Krok 3: Singleton (Singleton)

`scripts/warsztat.sh diff exercises/e06 2 3`

**Zmiany:**

1. Extract Interface `AgeRatings` (`categoryFor(system, code)`).
2. `AgeRatingTable` staje się `enum ... implements AgeRatings { INSTANCE; }` - plik czytany w konstruktorze enuma, czyli raz, leniwie i bezpiecznie wątkowo.
3. Adaptery dostają `AgeRatings` przez konstruktor; konstruktor jednoargumentowy deleguje do `this(client, AgeRatingTable.INSTANCE)`, więc zachowanie domyślne się nie zmienia.
4. `MovieInfo` przekazuje `AgeRatingTable.INSTANCE` jawnie: o cyklu życia decyduje korzeń kompozycji, nie adapter.

**Przed (`OmdbCatalogAdapter.find`, krok 2):**

```java
AgeRatingTable table = AgeRatingTable.load();
...
        table.categoryFor("OMDB", raw.get("Rated")));
```

**Po (`AgeRatingTable`, krok 3):**

```java
public enum AgeRatingTable implements AgeRatings {
    INSTANCE;

    private final Map<String, String> categories;

    AgeRatingTable() {
        Map<String, String> parsed = new HashMap<>();
        for (String line : AgeRatingResource.readLines()) {
            ...
        }
        categories = Map.copyOf(parsed);
    }
}
```

**Po (`OmdbCatalogAdapter`, krok 3):**

```java
public OmdbCatalogAdapter(OmdbLikeClient client) {
    this(client, AgeRatingTable.INSTANCE);
}

public OmdbCatalogAdapter(OmdbLikeClient client, AgeRatings ratings) {
    this.client = client;
    this.ratings = Objects.requireNonNull(ratings, "ratings");
}
```

**Pułapka:** singleton jest bezpieczny tylko dlatego, że tabela jest niemutowalna i każda jej kopia byłaby równoważna (slajd 16.7). Gdyby tabela miała stan (np. licznik użyć albo możliwość przeładowania), jedna instancja na JVM oznaczałaby współdzielony stan między testami. Dlatego test `adapterAcceptsAnotherTableWithoutGlobalState` podaje lambdę zamiast `INSTANCE` i nie potrzebuje żadnego "resetu" - to kryterium, że singleton nie stał się globalną zmienną. Test `ageTableIsLoadedAtMostOnceForAnyNumberOfServices` mierzy `AgeRatingResource.reads()` i dopuszcza przyrost o najwyżej 1, bo enum mógł zostać zainicjowany już wcześniej w tej samej JVM.

**Co powiedzieć:** "Trzy zapachy, trzy różne pytania. Co tłumaczy obcy język? Adapter. Co stoi przed wywołaniem i decyduje, czy w ogóle je przepuścić, nie zmieniając interfejsu? Zastępca - ochronny i buforujący. Co jest niemutowalne, drogie i identyczne dla wszystkich? Jedna instancja, ale za interfejsem i podana przez konstruktor, żeby nie zamieniać kosztu tworzenia na koszt testowania."

## Ćwiczenie e07. Dokumenty sprzedaży: bilet, kod i paragon w trzech kanałach

**Pakiet:** `pl.training.workshop.exercises.e07_documents` · **Test:** `scripts/warsztat.sh test exercises/e07` · **Kroki:** 2 (`step1` Abstract Factory, `step2` Decorator)

### Gdzie i jaki wzorzec

| Fragment kodu | Sygnał | Wzorzec | Dlaczego nie ... |
| --- | --- | --- | --- |
| `SalesDocuments.ticketLayoutFor / codeRendererFor / receiptLayoutFor` - trzy równoległe `switch (channel)` | kilka produktów, które muszą do siebie pasować, wybieranych tym samym kluczem w kilku miejscach; `DocumentComposer` przyjmuje trzy luźne części i da się je pomieszać | **Abstract Factory** (`DocumentFactory` i `KioskDocuments`, `MobileDocuments`, `BoxOfficeDocuments`) | nie Factory Method: tu nie ma jednego produktu z wariantami, tylko spójna rodzina trzech produktów; nie Builder: nie budujemy krok po kroku jednego złożonego obiektu, tylko dobieramy gotowe, pasujące do siebie części; nie Strategy: strategie są niezależne od siebie, a tu właśnie zależność między częściami jest problemem |
| `if (options.withVat()) {...} if (options.withPromo()) {...} if (options.withLoyaltyFooter()) {...}` w `print` | opcjonalne, niezależne dodatki do jednego wyniku, każdy "dopisuje się pod" tym, co już jest; liczba kombinacji rośnie wykładniczo z liczbą flag | **Decorator** (`Document`, rdzeń `ChannelDocument`, owijki `VatSection`, `PromoBanner`, `LoyaltyFooter`) | nie Template Method z flagami: szkielet z haczykami `if` to jest właśnie stan wyjściowy; nie Chain of Responsibility: żadna sekcja nie "obsługuje i kończy", każda przepuszcza dalej i dokłada swoje; nie podklasy `KioskDocumentWithVatAndPromo`: 3 kanały x 8 kombinacji = 24 klasy |

Scena m5 z tym samym wzorcem: Decorator w s07 (dodatki do biletu). Abstract Factory nie ma sceny w m5; najbliżej są s04 i s05 (Fabryka jako strażnik spójności).

### Co widzimy w start

```java
TicketLayout ticketLayout = ticketLayoutFor(channel);
CodeRenderer codeRenderer = codeRendererFor(channel);
ReceiptLayout receiptLayout = receiptLayoutFor(channel);
StringBuilder document = new StringBuilder(
        new DocumentComposer(ticketLayout, codeRenderer, receiptLayout).compose(sale));
if (options.withVat()) {
    List<String> lines = new ArrayList<>();
    lines.add("VAT 8% bilet: " + vat(sale.ticketPrice(), 8));
    ...
    document.append("\n").append(receiptLayout.section(lines));
}
if (options.withPromo()) { ... }
if (options.withLoyaltyFooter()) { ... }
```

Pułapkę pokazuje `E07SolutionTest.startLetsYouMixTicketCodeAndReceiptOfDifferentChannels`: `new DocumentComposer(new KioskTicketLayout(), new MobileCodeRenderer(), new BoxOfficeReceiptLayout())` kompiluje się i drukuje dokument z gwiazdkami kiosku, `<qr>` aplikacji i paragonem A4. Po `jump` do kroku 1 ten test jest pomijany, bo konstruktor z trzema częściami znika.

Test równoważności porównuje dokument znak po znaku: każdy kanał bez opcji, każda sekcja osobno, wszystkie razem (kolejność VAT, promocja, stopka), paragon bez baru (brak wiersza "Bar" i brak VAT 23%), próg 100 punktów ("Odbierz darmowy bilet 2D") i pięć błędów walidacji.

### Krok 1: Abstract Factory - komplet dokumentów jednego kanału

**Zmiany:**

1. Nowy interfejs `DocumentFactory` z trzema metodami: `ticketLayout()`, `codeRenderer()`, `receiptLayout()`.
2. Trzy fabryki: `KioskDocuments`, `MobileDocuments`, `BoxOfficeDocuments` - każda zwraca wyłącznie klasy swojego kanału.
3. Dziewięć klas konkretnych traci `public` (zostają w pakiecie) - kompilator pokaże wszystkie miejsca, które jeszcze ich używają.
4. `DocumentFactories.forChannel(channel)` to jedyny `switch` po kanale; fabryki są bezstanowe, więc są współdzielone.
5. `DocumentComposer` przyjmuje `DocumentFactory` zamiast trzech części (Change Signature). `SalesDocuments` woła `DocumentFactories.forChannel(channel)` raz.

**Przed (`SalesDocuments.java`):**

```java
TicketLayout ticketLayout = ticketLayoutFor(channel);
CodeRenderer codeRenderer = codeRendererFor(channel);
ReceiptLayout receiptLayout = receiptLayoutFor(channel);
StringBuilder document = new StringBuilder(
        new DocumentComposer(ticketLayout, codeRenderer, receiptLayout).compose(sale));
```

**Po (`DocumentFactory.java`, nowy plik):**

```java
public interface DocumentFactory {
    TicketLayout ticketLayout();

    CodeRenderer codeRenderer();

    ReceiptLayout receiptLayout();
}
```

**Po (`KioskDocuments.java`, nowy plik):**

```java
public final class KioskDocuments implements DocumentFactory {
    @Override
    public TicketLayout ticketLayout() {
        return new KioskTicketLayout();
    }

    @Override
    public CodeRenderer codeRenderer() {
        return new KioskCodeRenderer();
    }

    @Override
    public ReceiptLayout receiptLayout() {
        return new KioskReceiptLayout();
    }
}
```

**Po (`SalesDocuments.java`):**

```java
DocumentFactory family = DocumentFactories.forChannel(channel);
ReceiptLayout receiptLayout = family.receiptLayout();
StringBuilder document = new StringBuilder(new DocumentComposer(family).compose(sale));
```

**Pułapka:** odebranie `public` klasom konkretnym to ostatni ruch kroku, nie pierwszy - dopiero gdy wszyscy klienci przeszli na fabrykę, kompilator nie zgłosi błędów poza pakietem. `E07SolutionTest.solutionHidesConcreteDocumentsBehindFamilies` sprawdza widoczność, a `everyFamilyDeliversPartsOfOneChannelOnly` pilnuje, że każda fabryka zwraca części jednego kanału.

**Co powiedzieć:** "Trzy switch-e po tym samym kluczu to jedna decyzja rozlana na trzy miejsca. Rodzina produktów, które muszą do siebie pasować, dostaje jedną fabrykę, a pomyłka przestaje się kompilować."

### Krok 2: Decorator - sekcje jako owijki dokumentu

**Zmiany:**

1. Nowy interfejs `Document` z jedną metodą `render()`.
2. Rdzeń `ChannelDocument(sale, family)` - bilet, kod i paragon z rodziny, bez żadnej opcji.
3. Trzy owijki o tym samym kontrakcie: `VatSection`, `PromoBanner`, `LoyaltyFooter`. Każda dostaje dokument wewnętrzny i `ReceiptLayout` kanału (żeby sformatować sekcję), liczy swoją sekcję i dopisuje ją pod `inner.render()`.
4. `DocumentComposer.compose(sale, options)` zwraca `Document` i jest jedynym miejscem, które zna kolejność owijania.
5. `SalesDocuments.print` to walidacja, wybór rodziny i `compose(...).render()`.

**Przed (`SalesDocuments.java`, krok 1):**

```java
if (options.withVat()) {
    List<String> lines = new ArrayList<>();
    lines.add("VAT 8% bilet: " + vat(sale.ticketPrice(), 8));
    if (sale.barPrice().isGreaterThan(Money.ZERO)) {
        lines.add("VAT 23% bar: " + vat(sale.barPrice(), 23));
    }
    document.append("\n").append(receiptLayout.section(lines));
}
```

**Po (`VatSection.java`, nowy plik):**

```java
public record VatSection(Document inner, ReceiptLayout layout, Sale sale) implements Document {
    @Override
    public String render() {
        List<String> lines = new ArrayList<>();
        lines.add("VAT 8% bilet: " + vat(sale.ticketPrice(), 8));
        if (sale.barPrice().isGreaterThan(Money.ZERO)) {
            lines.add("VAT 23% bar: " + vat(sale.barPrice(), 23));
        }
        return inner.render() + "\n" + layout.section(lines);
    }
}
```

**Po (`DocumentComposer.java`):**

```java
public Document compose(Sale sale, PrintOptions options) {
    ReceiptLayout layout = family.receiptLayout();
    Document document = new ChannelDocument(sale, family);
    if (options.withVat()) {
        document = new VatSection(document, layout, sale);
    }
    if (options.withPromo()) {
        document = new PromoBanner(document, layout);
    }
    if (options.withLoyaltyFooter()) {
        document = new LoyaltyFooter(document, layout, sale);
    }
    return document;
}
```

**Po (`SalesDocuments.java`):**

```java
return new DocumentComposer(DocumentFactories.forChannel(channel)).compose(sale, options).render();
```

**Pułapka:** kolejność owijania jest częścią zachowania. `E07SolutionTest.wrappingOrderIsBehaviourAndIsFixedInTheComposer` pokazuje, że `VatSection(LoyaltyFooter(core))` daje inny dokument niż `LoyaltyFooter(VatSection(core))`, i że composer owija w kolejności z testu równoważności (VAT, promocja, stopka). Owijki nie są przezroczyste: `document instanceof ChannelDocument` jest fałszem dla udekorowanego dokumentu - tak jak w s07. Test `newChannelIsOneFamilyWithoutTouchingTheComposer` składa kanał e-mail z trzech lambd i anonimowej fabryki: nowy kanał dostaje wszystkie sekcje bez zmiany composera.

**Co powiedzieć:** "Trzy flagi to osiem kombinacji w jednej metodzie. Każda sekcja staje się owijką o kontrakcie dokumentu: dopisuje swoje i deleguje resztę do środka. Kolejność owijania to reguła biznesowa, więc ma jedno miejsce i test."

### Porównanie wzorców w tym ćwiczeniu

- **Abstract Factory a Decorator:** fabryka odpowiada na pytanie "które części do siebie pasują" (decyzja na początku, jedna na kanał), owijka na pytanie "co jeszcze dołożyć do wyniku" (decyzja per dokument, dowolny zestaw). Owijki korzystają z rodziny tylko przez `ReceiptLayout.section`, więc nowy kanał automatycznie dostaje wszystkie sekcje, a nowa sekcja działa we wszystkich kanałach: 3 + 3 klasy zamiast 3 x 8.
- **Sygnał do zapamiętania:** równoległe `switch` po tym samym kluczu w kilku metodach tworzących obiekty = rodzina produktów; łańcuch niezależnych `if (opcja)` dopisujących coś do tego samego wyniku = owijki.

## Ćwiczenie e08. Seanse cykliczne - seria tygodniowa z szablonu

**Pakiet:** `pl.training.workshop.exercises.e08_series` · **Test:** `scripts/warsztat.sh test exercises/e08` · **Kroki:** 2 · **Czas:** ~12 min

### W skrócie

**Co robimy:** `SeriesPlanner` tworzy szablon seansu dziewięcioargumentowym konstruktorem i kopiuje go pole po polu dla każdego tygodnia. Najpierw uczymy `Screening` kopiować się samodzielnie (głęboka kopia mapy dopłat, kopie "z jedną zmianą"), potem zamykamy tworzenie seansu w budowniczym z polami wymaganymi, wartościami domyślnymi i walidacją.

**Zasada:** Dwa różne problemy tworzenia obiektu: "zrób mi taki sam, ale z inną datą" to Prototype (obiekt sam wie, co i jak głęboko skopiować), a "zbuduj mi od zera, część podam, resztę weź domyślną" to Builder (jedno miejsce na domyślne i walidację, bez `null`-i w konstruktorze). Prototype nie zastępuje Buildera: jeden kopiuje, drugi buduje.

**Efekt:** Planer ma kilkanaście linii, nie zna cen formatów ani domyślnej sali; nowa cecha seansu to zmiana w `Screening` i `Builder`, bez dotykania kopii. Zachowanie świadomie się zmienia w jednym miejscu: dopłata premierowa zostaje w pierwszym tygodniu, bo kopie już nie dzielą mapy.

### Fragment kodu, sygnał, wzorzec

| Fragment kodu | Sygnał | Wzorzec | Dlaczego nie ... |
| --- | --- | --- | --- |
| `new Screening(template.title(), template.format(), ..., template.surcharges(), ...)` w pętli tygodni i jeszcze raz przy zmianie sali | kopia pole po polu w dwóch miejscach, każde nowe pole trzeba dopisać w obu; współdzielona mapa dopłat (`copy.surcharges().put(...)` psuje szablon) | **Prototype** (`copy()`, `withStart`, `withHall`, `withSurcharge`) | nie Factory: nie tworzymy "nowego rodzaju" seansu, tylko wariant istniejącego; nie `clone()` z `Cloneable`: płytki, bez kontroli nad mapą |
| `new Screening(title, format, hall, start, base, surcharges, !dubbing, dubbing, ageLimit)` z `null`-ami i `?: "A"`, `?: 12`, `?: priceFor(format)` w trzech miejscach | konstruktor teleskopowy, domyślne wartości rozsiane po planerze, kopii i `describe()` | **Builder** (`Screening.builder()...build()`) | nie rekord z metodami `with`: rekord nie ma miejsca na walidację pól wymaganych i domyślne zależne od innych pól (okulary tylko dla 3D); nie przeciążone konstruktory: 2^n kombinacji pól opcjonalnych |

### Co widzimy

```java
Screening template = new Screening(request.title(), request.format(), request.hall(),
        request.firstStart(), base, surcharges, !dubbing, dubbing, ageLimit);

for (int week = 0; week < request.weeks(); week++) {
    Screening copy = new Screening(template.title(), template.format(),
            template.hall() != null ? template.hall() : "A",
            template.start().plusWeeks(week), template.basePrice(), template.surcharges(),
            template.subtitles(), template.dubbing(), template.ageLimit());
    if (week == 0 && request.premiereWeek()) {
        copy.surcharges().put("PREMIERA", PREMIERE);   // ta sama mapa co w szablonie
    }
    series.add(copy);
}
```

Test równoważności nie zawiera scenariusza "premiera przy wielu tygodniach", bo tam `start` ma błąd: wszystkie tygodnie dostają PREMIERA. `E08SolutionTest.startLeaksThePremiereSurcharge...` dokumentuje ten błąd, a `solutionKeepsThePremiereSurchargeInTheFirstWeekOnly` jego naprawę. To zmiana zachowania wprowadzona razem z krokiem 1, nazwana i zatwierdzona osobnym testem - jak w scenie s30.

### Krok 1: Prototype - seans kopiuje się sam

**Zmiany:**

1. Konstruktor `Screening` kopiuje przekazaną mapę (`new LinkedHashMap<>(surcharges)`), a `surcharges()` zwraca `Collections.unmodifiableMap(...)`.
2. Nowe metody: `copy()`, `withStart(LocalDateTime)`, `withHall(String)`, `withSurcharge(String, Money)` - każda zwraca nowy, niezależny obiekt.
3. `SeriesPlanner` używa `template.withStart(...)`, `copy.withSurcharge("PREMIERA", ...)` i `series.getLast().withHall(...)`. Domyślna sala przenosi się do tworzenia szablonu, bo kopia już jej nie uzupełnia.

**Po (`Screening.java`):**

```java
public Screening withStart(LocalDateTime newStart) {
    return new Screening(title, format, hall, newStart, basePrice, surcharges, subtitles, dubbing, ageLimit);
}

public Screening withSurcharge(String name, Money amount) {
    Map<String, Money> extended = new LinkedHashMap<>(surcharges);
    extended.put(name, amount);
    return new Screening(title, format, hall, start, basePrice, extended, subtitles, dubbing, ageLimit);
}
```

**Po (`SeriesPlanner.java`):**

```java
for (int week = 0; week < request.weeks(); week++) {
    Screening copy = template.withStart(template.start().plusWeeks(week));
    if (week == 0 && request.premiereWeek()) {
        copy = copy.withSurcharge("PREMIERA", PREMIERE);
    }
    series.add(copy);
}
if (request.lastWeekHall() != null) {
    series.set(series.size() - 1, series.getLast().withHall(request.lastWeekHall()));
}
```

**Pułapka:** głębokość kopii. Prototyp musi wiedzieć, które pola są mutowalne (tu mapa dopłat) i skopiować je, a resztę (`String`, `Money`, `LocalDateTime`) może współdzielić. Jeśli getter nadal oddaje mutowalną mapę, "kopia" jest niezależna tylko do pierwszego `put` z zewnątrz - dlatego widok tylko do odczytu jest częścią kroku.

`scripts/warsztat.sh diff exercises/e08 0 1`

### Krok 2: Builder - pola wymagane, domyślne w jednym miejscu

**Zmiany:**

1. Konstruktor `Screening` staje się prywatny; powstaje zagnieżdżony `Screening.Builder` z metodami `title`, `format`, `hall`, `start`, `basePrice`, `surcharge`, `dubbing`, `ageLimit`.
2. `build()` sprawdza pola wymagane (tytuł, format, start), ujemną cenę i ustala domyślne: sala A, cena z formatu, VIP, okulary dla 3D, napisy, limit 12. `describe()` już nic nie uzupełnia.
3. `SeriesPlanner` ustawia w budowniczym tylko to, co przyszło w zamówieniu (`if (request.hall() != null) builder.hall(...)`), sprawdza tylko liczbę tygodni i nie zna już cen formatów.

**Przed (`SeriesPlanner.java`, krok 1):**

```java
Money base = request.basePrice() != null ? request.basePrice() : priceFor(request.format());
Map<String, Money> surcharges = new LinkedHashMap<>();
surcharges.put("VIP", VIP);
if (request.format().equals("3D")) {
    surcharges.put("OKULARY", GLASSES_3D);
}
boolean dubbing = request.dubbing() != null && request.dubbing();
int ageLimit = request.ageLimit() != null ? request.ageLimit() : 12;
Screening template = new Screening(request.title(), request.format(),
        request.hall() != null ? request.hall() : "A",
        request.firstStart(), base, surcharges, !dubbing, dubbing, ageLimit);
```

**Po (`SeriesPlanner.java`):**

```java
Screening.Builder builder = Screening.builder()
        .title(request.title())
        .format(request.format())
        .start(request.firstStart());
if (request.hall() != null) {
    builder.hall(request.hall());
}
if (request.basePrice() != null) {
    builder.basePrice(request.basePrice());
}
// dubbing, ageLimit analogicznie
Screening template = builder.build();
```

**Po (`Screening.Builder.build()`):**

```java
public Screening build() {
    if (title == null || title.isBlank()) {
        throw new IllegalArgumentException("title must not be blank");
    }
    if (format == null || !FORMATS.contains(format)) {
        throw new IllegalArgumentException("unknown format: " + format);
    }
    if (start == null) {
        throw new IllegalArgumentException("start is required");
    }
    Money price = basePrice != null ? basePrice : defaultPrice(format);
    if (price.compareTo(Money.ZERO) < 0) {
        throw new IllegalArgumentException("base price must not be negative");
    }
    Map<String, Money> surcharges = new LinkedHashMap<>();
    surcharges.put("VIP", VIP);
    if (format.equals("3D")) {
        surcharges.put("OKULARY", GLASSES_3D);
    }
    surcharges.putAll(extraSurcharges);
    return new Screening(title, format, hall != null ? hall : DEFAULT_HALL, start, price, surcharges,
            !dubbing, dubbing, ageLimit != null ? ageLimit : DEFAULT_AGE_LIMIT);
}
```

**Pułapka:** kolejność błędów jest kontraktem. W `start` walidacja idzie: tygodnie, tytuł, format, cena. Po przeniesieniu sprawdzeń do `build()` liczba tygodni musi być sprawdzona w planerze **przed** `build()`, a w `build()` zachowana kolejność tytuł, format, cena - inaczej przypadek "zero tygodni i pusty tytuł" z testu zmienia komunikat. Nowy komunikat `start is required` jest możliwy tylko przez budowniczego (planer zawsze ma start), więc nie zmienia kontraktu sceny.

`scripts/warsztat.sh diff exercises/e08 1 2`

**Co powiedzieć:** "Kopiowanie i budowanie to dwa różne pytania. Jeśli masz gotowy obiekt i chcesz prawie taki sam - niech on sam się skopiuje i sam wie, co jest w nim mutowalne. Jeśli budujesz od zera z dziewięciu parametrów, z których sześć ma wartość domyślną - potrzebujesz jednego miejsca, które te domyślne zna i które odmówi, gdy zabraknie wymaganych. Wprowadzenie kopii przy okazji naprawiło błąd współdzielonej mapy; to zmiana zachowania, więc ma osobny test."

## Ćwiczenie e09. Zamknięcie kasy - wpisy szuflady i rabat grupowy

**Pakiet:** `pl.training.workshop.exercises.e09_cashdrawer` · **Test:** `scripts/warsztat.sh test exercises/e09` · **Kroki:** 2 · **Czas:** ~15 min

### Rozpoznanie: dwa zapachy, dwa wzorce

| Fragment kodu | Sygnał | Wzorzec | Dlaczego nie ... |
| --- | --- | --- | --- |
| `cashEffect`, `fiscalLine`, `auditLine` w `CashDrawerReport` | trzy operacje, każda z tym samym łańcuchem `instanceof` po tych samych czterech rekordach, zakończonym wyjątkiem w runtime; struktura wpisów stabilna, operacji przybywa (raport kierownika, eksport do księgowości) | **Visitor** (`DrawerEntryVisitor<R>` + `accept` w rekordach) | nie polimorfizm w samych wpisach: `CashSale.fiscalLine()` wciągnąłby do rekordu danych formaty trzech raportów i każda nowa operacja zmieniałaby cztery rekordy; nie Strategy: tu nie wybieramy wariantu obliczenia, tylko rozdzielamy obliczenie po rodzaju elementu |
| `groupDiscount(tickets, mode)` z `if (mode.equals("PROPORTIONAL")) ... else if ("LAST") ... else if ("EQUAL")` | wymienne algorytmy tego samego obliczenia (podział kwoty na N części), wariant wybiera konfiguracja kina, a nie stan obiektu; reguła 10+ i format wyniku wspólne | **Strategy** (`DiscountSplit`, trzy klasy, wybór w `DiscountSplits.forMode`) | nie State: tryb nie zmienia się w trakcie życia raportu i nie zależy od wcześniejszych wywołań; nie Template Method: warianty nie dzielą szkieletu, różnią się całym algorytmem; nie Visitor: tryby nie są strukturą danych |

Pytanie z zadania "co zmienia się częściej?" rozstrzyga pierwszy wybór. Macierz zmian dla wpisów szuflady:

| Zmiana | `start` (instanceof) | Visitor | `sealed` + `switch` |
| --- | --- | --- | --- |
| nowa operacja (np. metoda płatności) | 1 metoda z nowym łańcuchem w raporcie (+1 plik) | 1 nowa klasa odwiedzającego (+1 plik) | 1 nowa metoda ze `switch` (+0 lub 1 plik) |
| nowy rodzaj wpisu (np. voucher) | 1 rekord + 3 łańcuchy do ręcznego odnalezienia, brak błędu kompilacji, wyjątek przy zamknięciu dnia | 1 rekord + interfejs + każdy odwiedzający (3 klasy), kompilator wskazuje wszystkie | 1 rekord w `permits` + każdy `switch` (3 metody), kompilator wskazuje wszystkie |

Visitor i `sealed` + `switch` dają tę samą kontrolę kompilatora. Visitor wygrywa, gdy operacje żyją w innych modułach niż typy (np. eksport fiskalny w osobnym pakiecie bez dostępu do `permits`) albo gdy hierarchii nie da się zamknąć; `sealed` + `switch` jest krótszy, gdy całość siedzi w jednym pakiecie. Obie formy są poprawnym rozwiązaniem zadania.

### Krok 1: Visitor - trzy operacje, trzy klasy

**Zmiany:**

1. Nowy plik `DrawerEntryVisitor.java`: generyczny interfejs z metodami `visitCashSale`, `visitCardSale`, `visitRefund`, `visitDeposit`.
2. `DrawerEntry` przestaje być pustym znacznikiem: dostaje `<R> R accept(DrawerEntryVisitor<R> visitor)`; każdy rekord implementuje ją wywołaniem swojej metody `visit...` (double dispatch).
3. Trzy nowe klasy: `CashEffectVisitor` (`Money`), `FiscalLineVisitor` (`String`, pogotowie kasowe zwraca pusty tekst jak w `start`), `AuditLineVisitor` (`String`). Każda przejmuje gałęzie jednej metody raportu.
4. `CashDrawerReport` trzyma trzy pola odwiedzających i w pętli woła `entry.accept(...)`. Metody `cashEffect`, `fiscalLine`, `auditLine` znikają; `groupDiscount` zostaje bez zmian.

**Przed (`CashDrawerReport.java`, jedna z trzech metod - znika):**

```java
private String fiscalLine(DrawerEntry entry) {
    if (entry instanceof CashSale sale) {
        return "S;" + sale.reference() + ";" + sale.amount() + ";8";
    } else if (entry instanceof CardSale card) {
        return "K;" + card.reference() + ";" + card.amount() + ";8;" + card.cardLast4();
    } else if (entry instanceof Refund refund) {
        return "Z;" + refund.reference() + ";-" + refund.amount() + ";8";
    } else if (entry instanceof Deposit) {
        return "";
    }
    throw new IllegalArgumentException("unknown entry: " + entry);
}
```

**Po (`DrawerEntryVisitor.java`, nowy plik):**

```java
public interface DrawerEntryVisitor<R> {
    R visitCashSale(CashSale sale);

    R visitCardSale(CardSale sale);

    R visitRefund(Refund refund);

    R visitDeposit(Deposit deposit);
}
```

**Po (`DrawerEntry.java` i `Refund.java`):**

```java
public interface DrawerEntry {
    <R> R accept(DrawerEntryVisitor<R> visitor);
}

public record Refund(String reference, Money amount, String reason) implements DrawerEntry {
    @Override
    public <R> R accept(DrawerEntryVisitor<R> visitor) {
        return visitor.visitRefund(this);
    }
}
```

**Po (`FiscalLineVisitor.java`, nowy plik):**

```java
public final class FiscalLineVisitor implements DrawerEntryVisitor<String> {
    @Override
    public String visitCashSale(CashSale sale) {
        return "S;" + sale.reference() + ";" + sale.amount() + ";8";
    }

    @Override
    public String visitCardSale(CardSale sale) {
        return "K;" + sale.reference() + ";" + sale.amount() + ";8;" + sale.cardLast4();
    }

    @Override
    public String visitRefund(Refund refund) {
        return "Z;" + refund.reference() + ";-" + refund.amount() + ";8";
    }

    @Override
    public String visitDeposit(Deposit deposit) {
        return "";
    }
}
```

**Po (`CashDrawerReport.java`, pętla):**

```java
private final CashEffectVisitor cashEffect = new CashEffectVisitor();
private final FiscalLineVisitor fiscalLine = new FiscalLineVisitor();
private final AuditLineVisitor auditLine = new AuditLineVisitor();

for (DrawerEntry entry : entries) {
    cash = cash.plus(entry.accept(cashEffect));
    String line = entry.accept(fiscalLine);
    if (!line.isEmpty()) {
        fiscal.append(line).append('\n');
    }
    audit.append(entry.accept(auditLine)).append('\n');
}
```

**Pułapka:** pogotowie kasowe nie ma linii fiskalnej. W `start` sygnalizuje to pusty tekst i warunek `isEmpty()` w pętli - odwiedzający musi zachować dokładnie tę konwencję, inaczej w eksporcie pojawi się pusta linia i test równoważności to wyłapie. Druga pułapka: przenosić po jednej operacji (najpierw `fiscalLine`, test, potem kolejne), a nie wszystkie trzy naraz.

**Uruchom:** `scripts/warsztat.sh test exercises/e09` - zielone.
**Co powiedzieć:** element wybiera metodę odwiedzającego. Nowy rodzaj wpisu to teraz cztery czerwone podkreślenia w IDE zamiast wyjątku o 23:00 przy zamknięciu kasy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff exercises/e09 0 1`

### Krok 2: Strategy - trzy tryby podziału, wybór w korzeniu kompozycji

**Zmiany:**

1. Nowy plik `DiscountSplit.java`: interfejs funkcyjny `List<Money> split(Money discount, List<Money> tickets)`.
2. Trzy nowe klasy: `ProportionalSplit` (udział proporcjonalny HALF_UP, różnica zaokrągleń na ostatnim bilecie), `LastTicketSplit`, `EqualSplit` (części w dół, brakujące grosze po jednym dla pierwszych biletów). Każda bezstanowa.
3. Nowy plik `GroupDiscount.java`: kontekst z regułą "10+ biletów = -10%", który dostaje `DiscountSplit` w konstruktorze i zwraca `Optional<Allocation>` (rabat i udziały). Kontekst nie zna nazw trybów.
4. Nowy plik `DiscountSplits.java`: `forMode(String)` ze `switch` po nazwie i współdzielonymi instancjami (jak `DiscountPrograms` w scenie m5/s01).
5. `CashDrawerReport.groupDiscount` składa `new GroupDiscount(DiscountSplits.forMode(mode))` i formatuje wynik. Łańcuch `if` i stałe reguły znikają z raportu.

**Przed (`CashDrawerReport.java`, środek `groupDiscount`):**

```java
Money discount = sum.percent(GROUP_PERCENT);
List<Money> shares = new ArrayList<>();
if (mode.equals("PROPORTIONAL")) {
    Money given = Money.ZERO;
    for (int i = 0; i < tickets.size() - 1; i++) {
        Money share = new Money(discount.amount().multiply(tickets.get(i).amount())
                .divide(sum.amount(), 2, RoundingMode.HALF_UP));
        shares.add(share);
        given = given.plus(share);
    }
    shares.add(discount.minus(given));
} else if (mode.equals("LAST")) {
    ...
} else if (mode.equals("EQUAL")) {
    ...
} else {
    throw new IllegalArgumentException("unknown split mode: " + mode);
}
```

**Po (`DiscountSplit.java` i `EqualSplit.java`, nowe pliki):**

```java
@FunctionalInterface
public interface DiscountSplit {
    List<Money> split(Money discount, List<Money> tickets);
}

public final class EqualSplit implements DiscountSplit {
    private static final Money CENT = Money.of("0.01");

    @Override
    public List<Money> split(Money discount, List<Money> tickets) {
        Money base = new Money(discount.amount()
                .divide(BigDecimal.valueOf(tickets.size()), 2, RoundingMode.DOWN));
        int restCents = discount.minus(base.times(tickets.size())).amount().movePointRight(2).intValueExact();
        List<Money> shares = new ArrayList<>();
        for (int i = 0; i < tickets.size(); i++) {
            shares.add(i < restCents ? base.plus(CENT) : base);
        }
        return shares;
    }
}
```

**Po (`GroupDiscount.java`, nowy plik):**

```java
public final class GroupDiscount {
    private static final int GROUP_SIZE = 10;
    private static final int GROUP_PERCENT = 10;

    private final DiscountSplit split;

    public GroupDiscount(DiscountSplit split) {
        this.split = Objects.requireNonNull(split, "split");
    }

    public Optional<Allocation> allocate(List<Money> tickets) {
        if (tickets.size() < GROUP_SIZE) {
            return Optional.empty();
        }
        Money sum = Money.ZERO;
        for (Money ticket : tickets) {
            sum = sum.plus(ticket);
        }
        Money discount = sum.percent(GROUP_PERCENT);
        return Optional.of(new Allocation(discount, split.split(discount, tickets)));
    }

    public record Allocation(Money discount, List<Money> shares) {
    }
}
```

**Po (`CashDrawerReport.java`):**

```java
private String groupDiscount(List<Money> tickets, String mode) {
    GroupDiscount rule = new GroupDiscount(DiscountSplits.forMode(mode));
    return rule.allocate(tickets)
            .map(allocation -> "Rabat grupowy " + mode + ": " + allocation.discount() + " = "
                    + allocation.shares().stream().map(Money::toString).collect(Collectors.joining(" + ")))
            .orElse("Rabat grupowy: brak (" + tickets.size() + " biletow)");
}
```

**Pułapka:** zaokrąglenie. `Money` zaokrągla HALF_UP w konstruktorze, a tryb równy potrzebuje dzielenia w dół i rozdania reszty - dlatego liczy na `BigDecimal` i dopiero wynik opakowuje. Test `E09SolutionTest.everySplitSumsExactlyToTheDiscountOnRandomGroups` sprawdza na 200 losowych grupach, że każdy tryb oddaje dokładnie kwotę rabatu i nie daje ujemnych udziałów. Druga pułapka to **moment wyboru**: `DiscountSplits.forMode` jest wołane przed sprawdzeniem wielkości grupy, więc nieznany tryb zgłasza się teraz także dla grupy z 3 biletami, a w `start` dawał "brak". To świadoma zmiana (błąd konfiguracji wychodzi wcześniej) opisana w `choosingTheSplitUpFrontMovesTheUnknownModeErrorToSmallGroupsToo`; test równoważności celowo nie łączy małej grupy z nieznanym trybem. Kto chce zachować stary kontrakt, przenosi `forMode` do środka `Optional.map`.

**Uruchom:** `scripts/warsztat.sh test exercises/e09` - zielone (36 przypadków równoważności + 5 kontraktu rozwiązania).
**Co powiedzieć:** tryb wybiera konfiguracja, zanim zaczniemy liczyć - to Strategy. Gdyby tryb zmieniał się sam w trakcie dnia w zależności od tego, co już policzono, mówilibyśmy o State. Nowy tryb to lambda w teście `newSplitIsJustAnotherVariantWithoutTouchingTheRule`.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff exercises/e09 1 2`

### Alternatywa: `sealed` + `switch` zamiast Visitora

W Javie 25 ten sam efekt dla wpisów daje zamknięta hierarchia i wyczerpujący `switch` bez `default`:

```java
public sealed interface DrawerEntry permits CashSale, CardSale, Refund, Deposit {
}

static String fiscalLine(DrawerEntry entry) {
    return switch (entry) {
        case CashSale sale -> "S;" + sale.reference() + ";" + sale.amount() + ";8";
        case CardSale card -> "K;" + card.reference() + ";" + card.amount() + ";8;" + card.cardLast4();
        case Refund refund -> "Z;" + refund.reference() + ";-" + refund.amount() + ";8";
        case Deposit deposit -> "";
    };
}
```

Brak gałęzi dla nowego rekordu w `permits` to błąd kompilacji, dokładnie jak brakująca metoda `visit...`. Różnica: `switch` nie wymaga `accept` w rekordach ani interfejsu odwiedzającego, ale wszystkie operacje muszą widzieć zamkniętą hierarchię (ten sam moduł), a nową operację pisze się jako metodę, nie jako obiekt, który można wstrzyknąć albo podmienić w teście. Scena m5/s19 pokazuje obie drogi krok po kroku.

**Co powiedzieć:** najpierw pytanie "co przybywa: operacje czy rodzaje?", potem wybór między Visitorem a `sealed`; oba są lepsze od `instanceof`, bo przenoszą błąd z runtime do kompilacji.

## Ćwiczenie e10. Start seansu - urządzenia sali i dwa pulpity

**Wzorce:** Mediator (krok 1), Facade (krok 2) · **Pakiet:** `pl.training.workshop.exercises.e10_showtime` · **Test:** `scripts/warsztat.sh test exercises/e10` · **Powtórka z m5:** s32 (Mediator na ekranie wyboru miejsc), s30 (Facade zakupu biletów)

### W skrócie

**Co robimy:** Urządzenia sali (`Doors`, `AdPlayer`, `Projector`) sterują sobą nawzajem, a dwa klienty (`BoxOfficePanel`, `AutoScheduler`) mają własne, rozjechane kopie orkiestracji startu seansu. Najpierw przenosimy reguły współpracy urządzeń do sterownika sali, który jest jedynym miejscem znającym wszystkie urządzenia i kolejność ich reakcji (Mediator). Potem dajemy klientom jedną operację `Showtime.start/end` i chowamy podsystemy (Facade).

**Zasada:** Mediator skupia reguły współpracy komponentów w jednym miejscu: komponent zgłasza, co się stało, a mediator decyduje, kto i w jakiej kolejności reaguje. Facade daje klientom jedną spójną operację zamiast znajomości kroków, a ukrycie podsystemów sprawia, że żadnego kroku nie da się pominąć. To dwa różne problemy w jednym scenariuszu: mediator koordynuje reakcje wewnątrz podsystemu, fasada upraszcza wejście do kilku podsystemów.

**Efekt:** Nowe urządzenie to jedna klasa i jedna metoda w mediatorze, bez dotykania pozostałych urządzeń. Nowy klient (np. aplikacja kierownika) dostaje fasadę i nie może pominąć raportu frekwencji. Zachowanie zmienia się świadomie w kroku 2: automat zaczyna raportować frekwencję, co dokumentuje `E10BehaviourChangeTest`.

### Gdzie jaki wzorzec i dlaczego

| Fragment kodu | Sygnał | Wzorzec | Dlaczego nie ... |
| --- | --- | --- | --- |
| `Doors.close` woła `lights.set(30)` i `adPlayer.start(...)`, `AdPlayer.tick` woła światła, dźwięk i projektor, `Projector.stop` woła dźwięk, światła i drzwi; `HallController` spina je `wire(...)` | komponenty połączone każdy z każdym, kolejność reakcji rozsiana po trzech klasach, drzwi pamiętają tytuł filmu, projektor ma `stop()` i `cut()` tylko po to, by ominąć kaskadę | **Mediator** (`HallController implements HallMediator`) | nie Observer: tu nie chodzi o "powiadom wszystkich zainteresowanych", tylko o jedną, uporządkowaną sekwencję reakcji, która jest regułą sali (światła przed dźwiękiem, dźwięk przed projektorem). Obserwatorzy nie gwarantują ani kolejności, ani tego, że reakcja na jedno zdarzenie zależy od stanu innego urządzenia. Nie Chain of Responsibility: zdarzenie nie szuka "pierwszego, kto je obsłuży", tylko ma ustaloną listę reakcji. |
| `BoxOfficePanel.startShow` i `AutoScheduler.startShow` same wołają `scanner.close`, `hall.startShow`, `report.record`; kopia w automacie pomija raport | klienci sami koordynują podsystemy, skopiowana orkiestracja się rozjechała, każdy nowy klient powieliłby kroki | **Facade** (`Showtime.start/end`, podsystemy pakietowe) | nie Mediator: fasada nie reaguje na zdarzenia podsystemów i nie koordynuje ich wzajemnych reakcji, tylko udostępnia jedną operację wejściową i wykonuje kroki w ustalonej kolejności. Nie Template Method: kroki nie różnią się między klientami, więc nie ma czego parametryzować podklasami. Nie Command: start seansu nie jest kolejkowany ani cofany. |
| `Sound.set` z limitem 85, `Doors` z flagą `blocked`, `Projector` z kontrolą `playing` | logika samego urządzenia | zostaje w urządzeniu | mediator, który przejmuje limit głośności albo stan projektora, staje się boską klasą (pułapka z m5/s32). |

### Krok 1: Mediator - sterownik sali decyduje, kto reaguje

`scripts/warsztat.sh diff exercises/e10 0 1`

**Zmiany:**

1. Nowy interfejs `HallMediator` z czterema zdarzeniami: `doorsClosed()`, `doorsBlocked()`, `doorsCleared()`, `adsFinished()`. Są to zdarzenia, które w `start` uruchamiały kaskadę.
2. `Doors`, `AdPlayer` dostają w konstruktorze `HallMediator` zamiast `wire(...)`. W miejscu kaskady wołają zdarzenie. `Doors.close()` traci parametr `title`, znika pole `pendingTitle`. `AdPlayer.start(int)` traci tytuł. `Projector` nie zna nikogo; `stop()` tylko zmienia stan i loguje, `cut()` zostaje jako awaryjne odcięcie lampy (operacja urządzenia, już nie obejście kaskady).
3. `HallController implements HallMediator`: tworzy urządzenia z `this`, trzyma `pendingTitle` (to sala wie, jaki film ma pójść po reklamach) i flagę `closeWhenCleared`. Cała kolejność reakcji jest w metodach zdarzeń i w `startShow/endShow/emergency`.

**Przed (`Doors.java`):**

```java
public void close(String title) {
    if (closed) {
        throw new IllegalStateException("doors already closed");
    }
    if (blocked) {
        log.add("doors: blocked");
        lights.set(100);
        pendingTitle = title;
        return;
    }
    closed = true;
    log.add("doors: closed");
    lights.set(30);
    adPlayer.start(2, title);
}
```

**Po (`Doors.java`):**

```java
public void close() {
    if (closed) {
        throw new IllegalStateException("doors already closed");
    }
    if (blocked) {
        log.add("doors: blocked");
        hall.doorsBlocked();
        return;
    }
    closed = true;
    log.add("doors: closed");
    hall.doorsClosed();
}
```

**Po (`HallController.java`):**

```java
public void startShow(String title) {
    pendingTitle = title;
    doors.close();
}

public void endShow() {
    projector.stop();
    sound.set(0);
    lights.set(100);
    doors.open();
}

@Override
public void doorsClosed() {
    lights.set(30);
    adPlayer.start(ADS_BEFORE_FEATURE);
}

@Override
public void doorsBlocked() {
    lights.set(100);
    closeWhenCleared = true;
}

@Override
public void doorsCleared() {
    if (closeWhenCleared) {
        closeWhenCleared = false;
        doors.close();
    }
}

@Override
public void adsFinished() {
    lights.set(0);
    sound.set(70);
    projector.play(pendingTitle);
    pendingTitle = null;
}
```

**Pułapka:** kaskada. W `start` procedura awaryjna nie mogła wołać `projector.stop()`, bo ten sam wołałby dźwięk, światła i drzwi i dziennik miałby podwójne wpisy; stąd `cut()`. Po przeniesieniu reakcji do mediatora `stop()` jest "czyste", a procedura awaryjna sama ustala kolejność: światła, drzwi, dźwięk, reklamy, projektor. Druga pułapka: wznowienie zamknięcia po usunięciu przeszkody. W `start` drzwi wznawiały zamknięcie tylko wtedy, gdy wcześniej odmówiły (pole `pendingTitle` ustawiane w gałęzi `blocked`). Mediator musi to odtworzyć flagą `closeWhenCleared`, a nie samym `pendingTitle != null`, bo tytuł jest ustawiany przy każdym starcie. Test "przeszkoda po zamknięciu drzwi nie wznawia niczego" łapie tę różnicę. Trzecia pułapka: mediator-bóg. Limit głośności, flaga `blocked` i kontrola `playing` zostają w urządzeniach; `E10SolutionTest.noDeviceHoldsAnotherDevice` pilnuje tylko referencji, nie zabrania urządzeniom mieć własnej logiki.

**Co powiedzieć:** "Urządzenie mówi, co się stało. Sala mówi, co z tego wynika. Kolejność reakcji ma teraz jedno miejsce i jedną nazwę."

### Krok 2: Facade - jedna operacja zamiast kroków u każdego klienta

`scripts/warsztat.sh diff exercises/e10 1 2`

**Zmiany:**

1. Nowa klasa `Showtime` z pakietowym konstruktorem (`TicketScanner`, `HallController`, `AttendanceReport`) i dwiema operacjami: `start(ShowRequest)` (bramka, sala, raport) i `end(screeningId)` (sala, raport). Składa ją korzeń kompozycji `ShowtimeRunner`.
2. `BoxOfficePanel` i `AutoScheduler` mają po jednym polu `Showtime` i tylko delegują.
3. Klasy podsystemów (`TicketScanner`, `AttendanceReport`, `HallController`, `HallMediator`, urządzenia, `DeviceLog`) tracą `public`. Kompilator pokazuje, kto jeszcze ich używał: tylko korzeń kompozycji w tym samym pakiecie.

**Przed (`AutoScheduler.java`):**

```java
public void startShow(ShowRequest request) {
    scanner.close(request.screeningId());
    hall.startShow(request.title());
}
```

**Po (`Showtime.java`):**

```java
public void start(ShowRequest request) {
    int attendance = scanner.close(request.screeningId());
    hall.startShow(request.title());
    report.record(request.screeningId(), attendance);
}

public void end(String screeningId) {
    hall.endShow();
    report.close(screeningId);
}
```

**Po (`AutoScheduler.java`):**

```java
public void startShow(ShowRequest request) {
    showtime.start(request);
}
```

**Pułapka:** zmiana zachowania klienta. Przepięcie automatu na fasadę naprawia pominięty raport frekwencji; w dzienniku pojawia się wpis `report: S1 attendance 2`, którego w `start` i `step1` nie było. To nie jest refaktoryzacja w sensie zachowania obserwowalnego, dlatego `auto start` nie wchodzi do testu równoważności, a `E10BehaviourChangeTest` pokazuje trzy stany: `start` pomija raport (test z `assumeTrue`, pomijany po `jump`), `step1` nadal pomija (zmienił się tylko podsystem sali), `step2` raportuje dla obu klientów. Zmianę trzeba nazwać i uzgodnić, tak jak w m5/s30. Druga pułapka: fasada a mediator w jednej scenie. `Showtime` nie reaguje na zdarzenia urządzeń i nie zna ich; zna `HallController` jako jedną z trzech zależności. Zdarzenia sprzętu (`tick`, `obstacle`, `cleared`, `emergency`) trafiają do sterownika sali z korzenia kompozycji, nie przez fasadę, bo to symulacja czujników, a nie operacja klienta.

**Co powiedzieć:** "Fasada nie koordynuje reakcji, tylko upraszcza wejście. Mediator nie upraszcza wejścia, tylko koordynuje reakcje. Dlatego w tej sali są potrzebne oba i nie da się ich zastąpić jednym."

### Macierz zmian

| Zmiana | `start` | po kroku 2 |
| --- | --- | --- |
| nowe urządzenie (kurtyna) reagujące na koniec reklam | `AdPlayer` (referencja i wywołanie), `HallController` (wire), ewentualnie `Projector` | `HallController` (pole i jedna linia w `adsFinished`) |
| inna kolejność: dźwięk przed światłami | `AdPlayer.tick` i `Projector.stop` (dwie kopie reguły) | `HallController.adsFinished` i `endShow` |
| nowy klient startujący seanse (aplikacja kierownika) | kopia trzech kroków, ryzyko pominięcia jednego | jedno pole `Showtime` |
| nowy krok startu (blokada sprzedaży online) | `BoxOfficePanel` i `AutoScheduler` | `Showtime.start` |

## Mapa 23 wzorców: sygnał w kodzie, ćwiczenie, scena modułu 5

Tabela do podsumowania. Kolumna "Sygnał" to pytanie, które zadajemy kodowi, zanim padnie nazwa wzorca; kolumna "m5" wskazuje scenę, w której ten sam wzorzec był pokazywany krok po kroku (albo "-" dla wzorców, które pojawiają się tylko w ćwiczeniach).

### Wzorce konstrukcyjne

| Wzorzec | Ćwiczenie | Sygnał w kodzie | m5 |
| --- | --- | --- | --- |
| Factory Method | e01 | nadklasa zna algorytm, ale nie wie, jaki obiekt pomocniczy (parser) utworzyć; wybór zależy od podklasy | s04, s05 (fabryka jako klasa) |
| Abstract Factory | e07 | kilka `switch` po tym samym kluczu tworzy po jednym elemencie rodziny; elementy muszą do siebie pasować | - |
| Builder | e08 | konstruktor z wieloma parametrami i `null`-ami, wartości domyślne ustalane w kilku miejscach, walidacja dopiero po utworzeniu | s06 |
| Prototype | e08 | obiekt kopiowany pole po polu w kilku miejscach, kopia różni się jednym-dwoma polami, pułapka płytkiej kopii | - |
| Singleton | e06 | kosztowna, niemutowalna tabela wczytywana przy każdym użyciu; jedna instancja jest bezpieczna tylko dla obiektu bez stanu zmiennego i nadal wstrzykiwana | s17 |

### Wzorce strukturalne

| Wzorzec | Ćwiczenie | Sygnał w kodzie | m5 |
| --- | --- | --- | --- |
| Adapter | e06 | tłumaczenie obcego formatu (jednostki, nazwy pól, sposób zgłaszania błędów) wymieszane z logiką domeny; klient zna szczegóły zewnętrznego API | s14, s29 |
| Bridge | e03 | klasy nazwane `KanałRodzaj` (EmailMoved, SmsMoved, ...): iloczyn dwóch niezależnych osi rośnie z każdą nową wartością na którejkolwiek z nich | s27 (dwie osie w Template Method) |
| Composite | e02 | zagnieżdżone pętle po `List<List<...>>` i `instanceof` po elementach drzewa, operacja (pojemność, cena) liczona inaczej dla liścia i węzła | s10, s11, s12, s13 |
| Decorator | e07 | flagi `withX` rozmnażające `if` w jednej metodzie; dodatki są opcjonalne, składalne i nie zmieniają kontraktu rdzenia | s07 |
| Facade | e10 | kilku klientów powtarza tę samą sekwencję wywołań podsystemów i kopie się rozjeżdżają | s30, s22 (usunięcie pośrednika) |
| Flyweight | e02 | setki obiektów trzymają identyczne, niemutowalne dane (nazwa, dopłata, ikona kategorii); zmienia się tylko kilka pól zewnętrznych (rząd, numer) | - |
| Proxy | e06 | `cache.containsKey`, sprawdzanie uprawnień albo leniwe ładowanie rozsiane po metodach klienta; ten sam interfejs, inna polityka dostępu | - |

### Wzorce operacyjne

| Wzorzec | Ćwiczenie | Sygnał w kodzie | m5 |
| --- | --- | --- | --- |
| Chain of Responsibility | e03 | uporządkowany łańcuch `if/else if`, w którym kolejność jest regułą, a każda gałąź albo obsługuje, albo przekazuje dalej | s28 |
| Command | e04 | `switch` po nazwie operacji z ręcznym cofaniem; operacje trzeba zapisywać, odtwarzać i cofać | s15, s26 |
| Interpreter | e05 | reguła biznesowa trzymana jako tekst i parsowana `split`-em w kilku miejscach; gramatyka jest mała, ale rośnie | - |
| Iterator | e02 | klient zna wewnętrzną strukturę kolekcji (tablice, listy list) i sam pomija luki, powtarzając tę logikę | - |
| Mediator | e10 | komponenty trzymają referencje do siebie nawzajem, każdy zna kolejność reakcji, kaskady wywołań | s32 |
| Memento | e04 | "punkt kontrolny" robiony przez ręczne kopiowanie pól do zmiennych lokalnych, które gubi jedno pole; przywrócenie stanu bez wystawiania wnętrza | - |
| Observer | e03 | źródło zdarzenia woła na sztywno każdego odbiorcę po nazwie klasy; lista odbiorców zmienia się częściej niż zdarzenie | s09 |
| State | e05 | `switch` po polu statusu w każdej metodzie, tabela przejść rozproszona po klasie | s08, s26 |
| Strategy | e09 | algorytm (podział rabatu) wybierany z konfiguracji `if (mode.equals(...))`, warianty mają to samo wejście i wyjście | s01, s20, s21, s26, s27 |
| Template Method | e01 | dwie metody o identycznym szkielecie (czytanie, filtrowanie, walidacja, sortowanie) różniące się jednym krokiem | s16 |
| Visitor | e09 | kilka niezależnych operacji nad zamkniętą rodziną typów, każda z własnym łańcuchem `instanceof`; częściej przybywa operacji niż typów | s19 |

Na zakończenie wróć do pytania z modułu 5: nie "jaki to wzorzec?", tylko "co się tu zmienia i kto o tym decyduje?". Jeśli sala potrafi dla każdego z 23 wierszy wskazać sygnał bez patrzenia na nazwę, cel ćwiczeń jest osiągnięty.
