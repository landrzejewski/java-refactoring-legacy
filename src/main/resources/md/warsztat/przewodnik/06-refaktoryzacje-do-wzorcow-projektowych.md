# Moduł 6. Refaktoryzacje do wzorców projektowych - warsztat CineLegacy: przewodnik prowadzącego

Dwadzieścia małych scen w domenie kina CineLegacy. Każda pokazuje jedną refaktoryzację do wzorca (albo od wzorca) ze slajdów modułu 6: od Strategy i State, przez Factory, Builder, Decorator i Observer, po rodzinę Composite, Visitor i mapę decyzji. Każda scena ma kod wyjściowy `start` z zapachem lub pułapką i gotowe snapshoty `stepN` po każdym ruchu, więc w dowolnym momencie można przeskoczyć do kolejnego kroku. Test równoważności pilnuje, że wzorzec wprowadzamy bez zmiany obserwowalnego zachowania, a tam, gdzie zachowanie celowo się zmienia (moment wyboru strategii, nowy niezmiennik buildera), osobny test to dokumentuje.

## Jak prowadzić pokaz

```bash
scripts/warsztat.sh list m6              # sceny i kroki modułu 6
scripts/warsztat.sh test m6/s08          # testy jednej sceny
scripts/warsztat.sh test m6              # wszystkie sceny modułu (473 testy)
scripts/warsztat.sh diff m6/s08 1 2      # co zmienia krok 2 względem kroku 1 (0 = start)
scripts/warsztat.sh jump m6/s08 2        # kopiuje step2 do start (przeskok, gdy brakuje czasu)
scripts/warsztat.sh reset m6/s08         # przywraca start z repozytorium
```

- Zasada: **jeden krok - jeden test - jedno zdanie komentarza**. Po każdym ruchu w IDE uruchamiamy test sceny; zielony pasek jest dowodem, że wzorzec nie zmienił kontraktu.
- Pracujemy na pakiecie `start`. Gdy coś pójdzie nie tak, `jump` do właściwego kroku, a po scenie `reset`.
- Test równoważności przechodzi przez `start` i wszystkie kroki. Jeśli uczestnicy pracują równolegle, to ten sam test jest ich kryterium ukończenia.
- Testy wołają sceny przez stabilne punkty wejścia (klient w rodzaju `PriceBoard`, `CancellationDesk`, `PaymentServices` albo zachowany konstruktor), dzięki czemu `jump` do dowolnego kroku kompiluje się i przechodzi. Testy dokumentujące pułapkę samego `start` (s11, s17) po `jump` są pomijane (JUnit pokazuje je jako "aborted") - to zamierzone.
- Motyw przewodni modułu: **najpierw kontrakt, potem diagram klas**. W każdej scenie zaczynamy od pytania "co jest obserwowalne?" (wyjątek, kolejność efektów, moment wyboru, format trwały).

## Mapa scen

| Scena | Temat ze slajdów | Kroki | Pakiet | Czas |
| --- | --- | --- | --- | --- |
| s01 | Strategy - przed i po; intencja, sekwencja, ryzyka | 3 | `m6.s01_strategy` | ~12 min |
| s02 | Polimorfizm - przed i po; kiedy i pułapki | 3 | `m6.s02_polymorphism` | ~12 min |
| s03 | Replace Type Code with Class; granica trwałości | 3 | `m6.s03_typecode` | ~12 min |
| s04 | Factory - Encapsulate Classes with Factory | 3 | `m6.s04_encapsulatefactory` | ~10 min |
| s05 | Factory - Extract Factory Class | 3 | `m6.s05_extractfactory` | ~10 min |
| s06 | Encapsulate Composite with Builder | 3 | `m6.s06_builder` | ~12 min |
| s07 | Move Embellishment to Decorator; przezroczystość | 3 | `m6.s07_decorator` | ~12 min |
| s08 | Replace State-Altering Conditionals with State | 3 | `m6.s08_state` | ~15 min |
| s09 | Replace Hard-coded Notifications with Observer | 3 | `m6.s09_observer` | ~15 min |
| s10 | Replace Implicit Tree with Composite; mapper | 3 | `m6.s10_implicittree` | ~15 min |
| s11 | Safe vs Transparent Composite | 2 | `m6.s11_safecomposite` | ~8 min |
| s12 | Replace One/Many Distinctions with Composite | 3 | `m6.s12_onemany` | ~10 min |
| s13 | Extract Composite | 2 | `m6.s13_extractcomposite` | ~8 min |
| s14 | Unify Interfaces with Adapter | 3 | `m6.s14_adapter` | ~12 min |
| s15 | Replace Conditional Dispatcher with Command | 3 | `m6.s15_command` | ~12 min |
| s16 | Form Template Method | 2 | `m6.s16_templatemethod` | ~8 min |
| s17 | Limit Instantiation with Singleton | 3 | `m6.s17_singleton` | ~10 min |
| s18 | Move Accumulation to Collecting Parameter | 3 | `m6.s18_collectingparameter` | ~8 min |
| s19 | Visitor i macierz zmian | 3 | `m6.s19_visitor` | ~12 min |
| s20 | Mapa decyzji, "najpierw rodzaj zmienności" | 3 | `m6.s20_decisionmap` | ~12 min |

Liczby domeny (ceny formatów, zniżki, VIP, okulary, opłata online, punkty lojalnościowe, zwroty, VAT) są wspólne dla całego warsztatu. Wartości spoza wspólnej listy (program "tydzień studenta" 50%, ubezpieczenie biletu 4.00, "tani wtorek" -30%, weekend +2.00, ceny premier i maratonów) są przykładowe i opisane w Javadoc sceny.

## Scena s01. Replace Conditional Logic with Strategy - polityka zniżek

**Temat ze slajdów:** Strategy - przed i po; Strategy - intencja, sekwencja, ryzyka; Istotne mechanizmy Javy 25
**Pakiet:** `pl.training.workshop.m6.s01_strategy` · **Test:** `scripts/warsztat.sh test m6/s01`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `TicketPricer` wybiera zniżkę łańcuchem `if` po nazwie programu kina, wymieszanym z walidacją ceny. Wprowadzamy interfejs `DiscountPolicy` najpierw jako strategię przejściową, przenosimy każdy program do osobnej klasy, a wybór programu trafia do `DiscountPrograms` i konstruktora.

**Zasada:** Strategy zamyka wymienne warianty tego samego obliczenia za wspólnym interfejsem, gdy wariant wybiera się niezależnie od klasy obiektu. Wspólna walidacja zostaje w kontekście, a strategie są bezstanowe. Samo `if` nie uzasadnia wzorca - przy prostym, stabilnym warunku interfejs pogarsza czytelność.

**Efekt:** Nowy program zniżek to nowa strategia (nawet lambda) i jeden wpis w `switch`, bez zmiany `TicketPricer`. Zachowanie świadomie się zmienia: wybór w konstruktorze zamraża decyzję, więc nieznany program zgłasza się teraz przed ujemną ceną.

### Co widzimy

`TicketPricer.price(base, ticketType, program)` wybiera algorytm zniżki łańcuchem `if` po nazwie programu skonfigurowanego w kinie (STANDARD, STUDENT_WEEK, PREMIERE). W tej samej metodzie jest walidacja ceny i programu. Algorytm jest wybierany niezależnie od klasy obiektu - to klasyczny kandydat na Strategy.

```java
if (program.equals("PREMIERE")) {
    discount = Money.ZERO;
} else if (program.equals("STUDENT_WEEK") && ticketType.equals("S")) {
    discount = base.percent(50);
} else if (program.equals("STANDARD") || program.equals("STUDENT_WEEK")) {
    int percent = switch (ticketType) { case "N" -> 0; case "S" -> 25; ... };
    discount = base.percent(percent);
} else {
    throw new IllegalArgumentException("unknown program: " + program);
}
```

Klientem jest `PriceBoard.priceFor(request)` - przez niego woła test. Test równoważności to **tabela decyzji**: każda gałąź, typ nieznany, program nieznany i `null`. Zwróć uwagę na przypadek "PREMIERE nie sprawdza typu" - start przyjmuje typ `X` bez błędu i refaktoryzacja musi to zachować.

### Krok 1: Extract Interface + strategia przejściowa

**W IDE:** utwórz interfejs funkcyjny `DiscountPolicy` z metodą `Money discount(Money base, String ticketType)`. Zaznacz łańcuch `if`, ⌥⌘M, nazwa `legacyDiscount` (parametry `base`, `ticketType`, `program`, zwracane `Money`). W `price` utwórz lambdę `DiscountPolicy policy = (b, type) -> legacyDiscount(b, type, program);` i wywołaj ją.
**Po:**

```java
DiscountPolicy policy = (b, type) -> legacyDiscount(b, type, program);
return base.minus(policy.discount(base, ticketType));
```

**Uruchom:** `scripts/warsztat.sh test m6/s01` - zielone.
**Co powiedzieć:** pierwszy commit jest mały i odwracalny: kontrakt strategii istnieje, ale deleguje do starego kodu. Nie zaczynamy od tworzenia wszystkich klas wzorca.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m6/s01 0 1`

### Krok 2: przeniesienie gałęzi do strategii

**W IDE:** dla każdej gałęzi osobno: nowa klasa implementująca `DiscountPolicy` (⌥⏎ na interfejsie - Implement interface), ciało skopiowane z gałęzi, test. Kolejno `StandardDiscount`, `PremiereDiscount`, `StudentWeekDiscount` (ta ostatnia deleguje nie-studentów do `StandardDiscount`). Na koniec `legacyDiscount` zastąp metodą `policyFor(program)` ze `switch`.
**Po:**

```java
return switch (program) {
    case "STANDARD" -> new StandardDiscount();
    case "STUDENT_WEEK" -> new StudentWeekDiscount(new StandardDiscount());
    case "PREMIERE" -> new PremiereDiscount();
    default -> throw new IllegalArgumentException("unknown program: " + program);
};
```

**Uruchom:** test po każdej przeniesionej gałęzi.
**Co powiedzieć:** walidacja zostaje w kontekście, strategie są bezstanowe. `switch` nie zniknął - przeniósł się w jedno miejsce wyboru, a to jest w porządku.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m6/s01 1 2`

### Krok 3: wybór strategii w korzeniu kompozycji

**W IDE:** Extract Class (lub Move Static Member F6) dla `policyFor` do `DiscountPrograms.forName`, instancje jako stałe (współdzielone). Change Signature (⌘F6) na `TicketPricer`: strategia w konstruktorze, `price(base, ticketType)` bez programu. Popraw klienta `PriceBoard`.
**Po:**

```java
return new TicketPricer(DiscountPrograms.forName(request.program()))
        .price(request.base(), request.ticketType());
```

**Uruchom:** test zielony; `S01SolutionTest` pokazuje nowy program zniżek jako lambdę bez zmiany kontekstu.
**Co powiedzieć:** wybór w konstruktorze **zamraża decyzję** i przesuwa moment błędu: w start ujemna cena zgłaszała się przed nieznanym programem, teraz jest odwrotnie. To dokumentuje test `choosingInConstructorMovesTheUnknownProgramErrorEarlier` - taka zmiana wymaga świadomej zgody, nie jest "za darmo".
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m6/s01 2 3`

### Rozwiązanie i uzasadnienie

`step3`: `TicketPricer` zna tylko `DiscountPolicy`, a nazwy programów zna `DiscountPrograms`. Nowy program zniżek to nowa strategia i wpis w jednym `switch`. Kontekst trzyma wspólną walidację.

### Pułapki

- `Function<Money, Money>` zamiast własnego interfejsu - działa, ale gubi nazwę pojęcia i nie może deklarować wyjątków kontrolowanych.
- Strategia z polami zmienianymi podczas liczenia, a potem współdzielona między wątkami.
- Porównywanie lambd przez `==` - lambda nie ma stabilnej tożsamości.
- Interfejs strategii `sealed` - interfejs funkcyjny nie może być `sealed`, a punkt rozszerzeń ma być otwarty.

### Pytanie do sali

Czy przy dwóch programach i jednym `if` Strategy nadal byłaby uzasadniona? Co musiałoby się zmieniać, żeby tak?

## Scena s02. Replace Conditional with Polymorphism - rodzaj seansu

**Temat ze slajdów:** Polimorfizm - przed i po; Polimorfizm - kiedy i pułapki
**Pakiet:** `pl.training.workshop.m6.s02_polymorphism` · **Test:** `scripts/warsztat.sh test m6/s02`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `Screening` ma pole `kind` i ten sam `switch` w trzech metodach, a pole `value` znaczy raz minuty filmu, raz liczbę filmów. Wydzielamy podtyp dla każdego rodzaju seansu po kolei, aż zostaje zamknięta hierarchia rekordów z nazwanymi danymi.

**Zasada:** Replace Conditional with Polymorphism przenosi zachowanie zależne od rodzaju do podtypów, gdy selektor opisuje trwały rodzaj obiektu. Jeśli rodzaj zmienia się w czasie życia obiektu, to State, a jeśli algorytm wybiera klient - Strategy. Zaczynamy od znalezienia wszystkich miejsc tworzenia i deserializacji.

**Efekt:** Switche w zachowaniu znikają, zostaje jeden w `fromRow`, gdzie należy wiedza o konstrukcji, a dane mają jednoznaczne nazwy. Kosztem zamkniętej hierarchii `sealed` jest to, że nowy rodzaj psuje kompilację każdego wyczerpującego `switch`, także poza modułem.

### Co widzimy

`Screening` ma pole `kind` (REGULAR, PREMIERE, MARATHON) i ten sam `switch` w trzech metodach: `label()`, `durationMinutes()`, `price()`. Pole `value` znaczy raz "minuty filmu", raz "liczba filmów". Rodzaj seansu jest **trwałą cechą obiektu** - nie zmienia się w czasie życia, więc to nie State, a klient go nie wybiera, więc to nie Strategy.

```java
public int durationMinutes() {
    return switch (kind) {
        case REGULAR -> 20 + value;
        case PREMIERE -> 30 + value;
        case MARATHON -> value * 120 + (value - 1) * 15;
    };
}
```

Tworzenie jest w jednym miejscu: `Screening.fromRow(ScreeningRow)` (mapowanie wiersza z bazy). Zawsze najpierw szukamy miejsc tworzenia i deserializacji.

### Krok 1: Extract Subclass dla jednej gałęzi

**W IDE:** usuń `final`, dodaj `sealed ... permits MarathonScreening`. Utwórz `MarathonScreening extends Screening` z polem `films`, nadpisz trzy metody (⌃O - Override Methods) i skopiuj do nich gałęzie MARATHON. W `fromRow` twórz podklasę dla "MARATHON". W bazie gałęzie MARATHON rzucają `IllegalStateException`.
**Po:**

```java
case "MARATHON" -> new MarathonScreening(row.title(), row.value());
```

**Uruchom:** `scripts/warsztat.sh test m6/s02` - zielone.
**Co powiedzieć:** jedna gałąź naraz, jeden test kontraktowy dla wszystkich rodzajów. Martwa gałąź rzuca wyjątek zamiast po cichu liczyć bzdury.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m6/s02 0 1`

### Krok 2: pozostałe podklasy, baza abstrakcyjna

**W IDE:** powtórz ruch dla REGULAR i PREMIERE. Potem metody bazy jako `abstract`, usuń pole `kind` i enum `Kind` (Safe Delete ⌘⌦). Jedynym `switch` zostaje `fromRow`.
**Po:**

```java
public abstract sealed class Screening permits RegularScreening, PremiereScreening, MarathonScreening {
    public abstract int durationMinutes();
```

**Uruchom:** test zielony.
**Co powiedzieć:** `switch` w miejscu tworzenia jest właściwy - to jest wiedza o konstrukcji. Znikają za to switche w zachowaniu.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m6/s02 1 2`

### Krok 3: forma Java 25 - sealed interface i rekordy

**W IDE:** zamień klasę bazową na `sealed interface` ze statycznym `fromRow`, a podklasy na rekordy (⌥⏎ Convert to record). `value` znika - każdy rekord ma nazwane dane (`runtime`, `films`).
**Po:**

```java
public record MarathonScreening(String title, int films) implements Screening {
    public int durationMinutes() { return films * 120 + (films - 1) * 15; }
```

**Uruchom:** test zielony; `S02SolutionTest` zawiera `switch` klienta bez `default`.
**Co powiedzieć:** zamknięty zestaw rodzajów daje kontrolę kompilatora, ale też koszt: nowy rodzaj psuje kompilację każdego wyczerpującego `switch`, także poza modułem.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m6/s02 2 3`

### Rozwiązanie i uzasadnienie

Trzy rekordy z własnymi danymi i zachowaniem. Znaczenie pól jest jednoznaczne, a test kontraktowy przechodzi przez wszystkie rodzaje, łącznie z nieznanym rodzajem w danych.

### Pułapki

- Pominięcie miejsc tworzenia (ORM, deserializator JSON, fabryka w innym module) - obiekt powstanie jako zły podtyp albo wcale.
- Wywołanie metody nadpisywanej z konstruktora bazy - podklasa widzi niezainicjalizowane pola.
- Pola i metody `static` nie są polimorficzne - "nadpisanie" stałej w podklasie nic nie daje.

### Pytanie do sali

Seans może zmienić się z "premiery" w "zwykły" po tygodniu. Czy to nadal polimorfizm, czy już State?

## Scena s03. Replace Type Code with Class - format jako typ

**Temat ze slajdów:** Replace Type Code with Class; Type Code - decyzje i granica trwałości
**Pakiet:** `pl.training.workshop.m6.s03_typecode` · **Test:** `scripts/warsztat.sh test m6/s03`
**Czas:** ~12 min

### W skrócie

**Co robimy:** Format seansu jest surowym `int` z CSV, a wiedza o kodach jest rozsiana po trzech metodach. Zamieniamy liczbę na enum `Format` tuż po odczycie, przenosimy do niego etykietę, cenę i okulary, a tłumaczenie kodu trwałego zamykamy w mapperze `FormatCodes`.

**Zasada:** Replace Type Code with Class zastępuje prymitywny kod typem, który przejmuje walidację, normalizację i operacje pojęcia - co nie oznacza automatycznie hierarchii podklas. Enum wystarcza dla małego, zamkniętego zestawu, klasa jest lepsza przy aliasach i kodach zewnętrznych. Na granicy trwałości zostaje stabilny kod, nigdy `ordinal()` ani `toString()`.

**Efekt:** Poza mapperem żaden kod nie operuje na liczbach 1, 2, 3, a zapis CSV jest bez zmian. Zmiana formatu trwałego (np. na kody tekstowe) dotknie tylko mappera, ale to osobna decyzja, nie część tej refaktoryzacji.

### Co widzimy

Format seansu jest surowym `int` (1=2D, 2=3D, 3=IMAX) czytanym z CSV `Diuna;3`. Wiedza o kodzie jest w trzech metodach, a `needsGlasses` w ogóle nie waliduje kodu. Plik CSV musi nadal przechowywać `int` - to jest **granica trwałości**.

```java
private Money basePrice(int formatCode) {
    return switch (formatCode) {
        case FORMAT_2D -> Money.of("25.00");
        ...
        default -> throw new IllegalArgumentException("unknown format code: " + formatCode);
    };
}
```

### Krok 1: nowy typ i konwersja na granicy

**W IDE:** utwórz `enum Format { TWO_D(1), THREE_D(2), IMAX(3) }` z polem `code` i `fromCode(int)` rzucającym ten sam wyjątek co start. W `describe` zamień `int` na `Format` zaraz po odczycie; Change Signature (⌘F6) metod prywatnych na `Format`. Do CSV zapisuj `format.code()`.
**Po:**

```java
Format format = Format.fromCode(Integer.parseInt(parts[1].trim()));
```

**Uruchom:** `scripts/warsztat.sh test m6/s03` - zielone, łącznie z "nieznany kod" i "kod nie jest liczbą".
**Co powiedzieć:** enum wystarcza, bo zestaw jest mały i zamknięty. Trwały kod to jawne pole, nigdy `ordinal()` ani `name()`.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m6/s03 0 1`

### Krok 2: Move Method do typu

**W IDE:** przenieś `label`, `basePrice`, `needsGlasses` do enuma (F6 Move albo ręcznie jako pola stałych). Klient pyta obiekt zamiast wykonywać `switch`.
**Po:**

```java
TWO_D(1, "2D", "25.00", false),
THREE_D(2, "3D", "32.00", true),
IMAX(3, "IMAX", "40.00", false);
```

**Uruchom:** test zielony.
**Co powiedzieć:** nowy typ przejmuje walidację, normalizację i operacje pojęcia. Nie oznacza to automatycznie hierarchii klas.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m6/s03 1 2`

### Krok 3: mapper migracyjny na granicy trwałości

**W IDE:** Extract Class `FormatCodes` z `fromCode`/`toCode` (mapa `EnumMap`), usuń `code` z enuma. `ScreeningCsv` rozmawia z mapperem, reszta kodu wyłącznie z `Format`.
**Po:**

```java
Format format = FormatCodes.fromCode(Integer.parseInt(parts[1].trim()));
... + "|csv=" + title + ";" + FormatCodes.toCode(format);
```

**Uruchom:** test zielony; `S03SolutionTest` sprawdza, że `ordinal()` IMAX to 2, a kod trwały to 3.
**Co powiedzieć:** gdy kiedyś przejdziemy na kody tekstowe ("IMAX" w bazie), zmieni się tylko mapper - może przez pewien czas czytać oba formaty. Zmiana formatu trwałego to osobna decyzja, nie część tej refaktoryzacji.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m6/s03 2 3`

### Rozwiązanie i uzasadnienie

Typ domenowy bez wiedzy o bazie plus mapper na granicy. Enum, bo zestaw jest zamknięty. Klasę z prywatnym konstruktorem i fabryką (jak `DeploymentZone` ze slajdów) wybralibyśmy przy aliasach, kodach zewnętrznych albo gdy lista wartości pochodzi z konfiguracji.

### Pułapki

- Zapis `ordinal()` do bazy - dodanie stałej w środku enuma przestawia dane.
- Zapis `toString()` - ktoś go "upiększy" i CSV przestanie się wczytywać.
- Zmiana komunikatu wyjątku przy nieznanym kodzie - to też obserwowalne zachowanie (logi, monitoring).

### Pytanie do sali

Kiedy wybralibyście klasę zamiast enuma dla formatu? Co w domenie kina mogłoby to wymusić?

## Scena s04. Encapsulate Classes with Factory - bilety

**Temat ze slajdów:** Factory - publiczna granica tworzenia; Factory - intencja, procedura, pułapki
**Pakiet:** `pl.training.workshop.m6.s04_encapsulatefactory` · **Test:** `scripts/warsztat.sh test m6/s04`
**Czas:** ~10 min

### W skrócie

**Co robimy:** `BoxOffice` tworzy `StandardTicket` i `VipTicket` przez `new` i sam zna regułę VIP (rząd 10+). Przekierowujemy tworzenie do metod fabryki `Tickets`, przenosimy tam regułę wyboru biletu, a na końcu odbieramy klasom konkretnym `public`.

**Zasada:** Encapsulate Classes with Factory ukrywa klasy konkretne za publiczną granicą tworzenia, gdy klient potrzebuje tylko wspólnego interfejsu. Tworzenie przekierowujemy pojedynczo, a widoczność ograniczamy dopiero na końcu. Nie mylić z Extract Factory Class, która wydziela tworzenie z klasy o innej odpowiedzialności.

**Efekt:** Publiczne zostają tylko `Ticket` i `Tickets`, więc nowy rodzaj biletu nie zmienia żadnego klienta. Ryzykiem zostają miejsca tworzenia, których kompilator nie widzi (refleksja, DI, deserializacja), a w bibliotece publicznej potrzebny byłby etap `@Deprecated`.

### Co widzimy

`BoxOffice` (inny pakiet niż bilety) tworzy `StandardTicket` i `VipTicket` przez `new` w dwóch miejscach i zna regułę VIP (rząd 10+). Klasy biletów są publiczne, choć klient potrzebuje tylko interfejsu `Ticket`.

```java
tickets.add(row >= 10
        ? new VipTicket(title, base, row)
        : new StandardTicket(title, base, row));
```

### Krok 1: metody tworzące

**W IDE:** w pakiecie `tickets` utwórz `Tickets` ze statycznymi `standard(...)` i `vip(...)` zwracającymi `Ticket`. Zastąp każde `new` w `BoxOffice` wywołaniem metody tworzącej (IntelliJ: Replace Constructor with Factory Method z menu Refactor robi to automatycznie).
**Po:**

```java
return Tickets.vip(title, base, row);
```

**Uruchom:** `scripts/warsztat.sh test m6/s04` - zielone.
**Co powiedzieć:** przekierowujemy tworzenie pojedynczo; klient nie importuje już klas konkretnych.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m6/s04 0 1`

### Krok 2: decyzja do fabryki

**W IDE:** Move Method - warunek `row >= 10` przenieś do `Tickets.forSeat(title, base, row)`, stałą nazwij (⌥⌘C `VIP_FROM_ROW`). `sellAll` woła `sell`.
**Po:**

```java
public Ticket sell(String title, Money base, int row) {
    return Tickets.forSeat(title, base, row);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** reguła "który bilet" jest teraz w jednym miejscu - to właściwy dom dla `if` o konstrukcji.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m6/s04 1 2`

### Krok 3: ograniczenie widoczności

**W IDE:** usuń `public` z `StandardTicket` i `VipTicket` (przy okazji ⌥⏎ Convert to record). IntelliJ podkreśli każde użycie spoza pakietu - to inwentaryzacja, którą robi za nas kompilator.
**Po:**

```java
record VipTicket(String title, Money base, int row) implements Ticket { ... }
```

**Uruchom:** test zielony; `S04SolutionTest` sprawdza widoczność refleksją.
**Co powiedzieć:** widoczność ograniczamy na końcu, gdy wszyscy klienci już przeszli. W bibliotece publicznej potrzebny byłby etap `@Deprecated`.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m6/s04 2 3`

### Rozwiązanie i uzasadnienie

Publiczne są tylko `Ticket` i `Tickets`. Nowy rodzaj biletu (np. miejsce dla osoby na wózku) nie zmienia żadnego klienta.

### Pułapki

- Zapomniane miejsca tworzenia: refleksja, DI, deserializacja JSON wymagająca publicznego konstruktora.
- Fabryka rosnąca w globalny rejestr wszystkiego ("ServiceLocator").
- Fabryka tworząca warianty z wyprzedzeniem - kosztowne efekty uruchamiane wcześniej niż w starej gałęzi.

### Pytanie do sali

Co się zmienia, jeśli klasy biletów są używane przez framework ORM?

## Scena s05. Extract Factory Class - tworzenie rezerwacji

**Temat ze slajdów:** Factory - intencja, procedura, pułapki (Extract Factory Class)
**Pakiet:** `pl.training.workshop.m6.s05_extractfactory` · **Test:** `scripts/warsztat.sh test m6/s05`
**Czas:** ~10 min

### W skrócie

**Co robimy:** `ReservationService` pilnuje zajętości miejsc, ale też buduje rezerwacje (numer, opłata online, termin ważności), i to w dwóch kopiach. Wyciągamy tworzenie do metody, potem do klasy `ReservationFactory`, a na końcu przekazujemy fabrykę do serwisu przez konstruktor.

**Zasada:** Extract Factory Class wydziela wiedzę o tworzeniu obiektów z klasy, która ma inną główną odpowiedzialność. Fabryka to zwykła zależność wstrzykiwana konstruktorem, a nie globalny rejestr ani zestaw metod statycznych. Kolejność efektów przy tworzeniu (tu: numer pobierany przed walidacją kanału) jest częścią kontraktu.

**Efekt:** Fabryka wie, jak powstaje rezerwacja, serwis wie, kiedy wolno ją utworzyć, i każdą da się testować osobno. "Spalanie" numeru przy błędnym kanale zostaje celowo - jego naprawa byłaby zmianą zachowania.

### Co widzimy

`ReservationService` pilnuje zajętości miejsc, ale też buduje rezerwację: numer z licznika, opłata online 2.00 za bilet, termin ważności 15 minut. Ta wiedza jest skopiowana w `reserve` i `reserveGroup`. Subtelność: numer jest pobierany **przed** walidacją kanału - błędny kanał "spala" numer.

```java
sequence++;
String id = "R" + sequence;
if (!channel.equals("ONLINE") && !channel.equals("BOX_OFFICE")) {
    throw new IllegalArgumentException("unknown channel: " + channel);
}
```

Test to scenariusz kilku rezerwacji na jednym serwisie: zajęte miejsce nie zużywa numeru, nieznany kanał zużywa.

### Krok 1: Extract Method dla tworzenia

**W IDE:** w `reserve` zaznacz blok od `sequence++` do `new Reservation(...)`, ⌥⌘M, nazwa `newReservation`. W `reserveGroup` zastąp duplikat wywołaniem `newReservation("ONLINE", email, seats)`. Pętlę zajętości wyciągnij do `requireFree`.
**Po:**

```java
requireFree(seats);
Reservation reservation = newReservation(channel, email, seats);
takenSeats.addAll(seats);
```

**Uruchom:** `scripts/warsztat.sh test m6/s05` - zielone.
**Co powiedzieć:** kolejność efektów zostaje: najpierw sprawdzenie miejsc, potem numer, potem walidacja kanału. Gdybyśmy przy okazji "poprawili" kolejność, test to wychwyci.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m6/s05 0 1`

### Krok 2: Extract Class - ReservationFactory

**W IDE:** Refactor > Extract Delegate (albo Move Method F6 do nowej klasy) dla `newReservation` razem z polami `clock` i `sequence`. Serwis tworzy fabrykę w swoim konstruktorze - sygnatura konstruktora bez zmian.
**Po:**

```java
public ReservationService(Clock clock) {
    this.factory = new ReservationFactory(clock);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** klienci serwisu niczego nie zauważyli. Serwis ma jedną odpowiedzialność: dostępność miejsc.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m6/s05 1 2`

### Krok 3: fabryka jako zależność

**W IDE:** dodaj konstruktor `ReservationService(ReservationFactory factory)`; dotychczasowy `ReservationService(Clock)` zamień na delegację `this(new ReservationFactory(clock))`, żeby nie ruszać klientów.
**Po:**

```java
public ReservationService(Clock clock) {
    this(new ReservationFactory(clock));
}

public ReservationService(ReservationFactory factory) { ... }
```

**Uruchom:** test zielony; `S05SolutionTest` testuje fabrykę osobno i pokazuje współdzieloną numerację.
**Co powiedzieć:** fabryka to zwykła zależność wstrzykiwana konstruktorem, nie globalny rejestr. Dwie usługi z jedną fabryką dzielą numerację - to decyzja korzenia kompozycji.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m6/s05 2 3`

### Rozwiązanie i uzasadnienie

`ReservationFactory` wie, jak powstaje rezerwacja (numer, opłata, termin), `ReservationService` wie, kiedy wolno ją utworzyć. Każdą da się testować osobno.

### Pułapki

- "Naprawienie" spalania numerów w ramach refaktoryzacji - to zmiana zachowania (luki w numeracji mogą być wymagane przez księgowość albo przez nią zabronione).
- Zegar pobierany przez `LocalDateTime.now()` bez `Clock` - fabryka staje się nietestowalna.
- Fabryka ze statycznymi metodami i statycznym licznikiem - wraca globalny stan.

### Pytanie do sali

Czy numeracja rezerwacji powinna należeć do fabryki, czy do repozytorium? Co przemawia za każdą opcją?

## Scena s06. Encapsulate Composite with Builder - repertuar dnia

**Temat ze slajdów:** Encapsulate Composite with Builder
**Pakiet:** `pl.training.workshop.m6.s06_builder` · **Test:** `scripts/warsztat.sh test m6/s06`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `WeekendPlanner` składa drzewo dzień - sala - seans ręcznie z `new` i `add`, a węzły są mutowalne. Wprowadzamy `ScheduleBuilder`: najpierw z bieżącą salą, potem z niemutowalnymi rekordami i jednorazowym `build()`, na końcu z zagnieżdżoną lambdą dla każdej sali.

**Zasada:** Encapsulate Composite with Builder ukrywa budowę drzewa za API, które mówi "co" zbudować, a nie "jak". Jest uzasadniony, gdy budowa jest wieloetapowa i ma własne niezmienniki (kolejność, pusta grupa, unikalność nazw). Builder jest jednorazowy, a lambda gałęzi wykonuje się synchronicznie dokładnie raz.

**Efekt:** Wcięcia kodu planera odpowiadają poziomom repertuaru, a gotowego drzewa nie da się zmodyfikować. Unikalność nazw sal to świadomie dodany nowy niezmiennik - start pozwalał na duplikaty, więc dla błędnych danych zachowanie się zmienia.

### Co widzimy

`WeekendPlanner.plan(date)` buduje drzewo dzień - sala - seans ręcznie: dużo `new` i `add`, a o `day.add(hall2)` łatwo zapomnieć. Kod nie przypomina kształtu repertuaru, a węzły są mutowalne.

```java
Hall hall2 = new Hall("Sala 2");
if (weekend) {
    hall2.add(new Screening("Kraina Lodu", LocalTime.of(10, 0)));
}
hall2.add(new Screening("Amator", LocalTime.of(17, 30)));
day.add(hall2);
```

### Krok 1: klasyczny Builder z bieżącym węzłem

**W IDE:** utwórz `ScheduleBuilder` z metodami `hall(name)`, `screening(title, hour, minute)` i `build()`; builder pamięta bieżącą salę. Przepisz planera na wywołania buildera. Drzewo bez zmian.
**Po:**

```java
new ScheduleBuilder(date)
        .hall("Sala 1")
        .screening("Diuna", 18, 0)
        .screening("Diuna", 21, 0)
        .hall("Sala 2");
```

**Uruchom:** `scripts/warsztat.sh test m6/s06` - zielone (sobota i piątek z pustą salą VIP).
**Co powiedzieć:** klient mówi "co" zbudować. Seans przed salą to teraz jawny błąd (`screening without hall`).
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m6/s06 0 1`

### Krok 2: niemutowalne drzewo, builder jednorazowy

**W IDE:** węzły zamień na rekordy z `List.copyOf` (⌥⏎ Convert to record, usuń `add`). Builder zbiera dane i tworzy rekordy w `build()`; drugi `build()` rzuca `IllegalStateException`.
**Po:**

```java
public record Hall(String name, List<Screening> screenings) {
    public Hall { screenings = List.copyOf(screenings); }
```

**Uruchom:** test zielony; `S06SolutionTest` sprawdza jednorazowość i niemutowalność.
**Co powiedzieć:** gdy builder jest jedyną drogą budowy, drzewo może stać się niemutowalne. Unikalność nazw sal to **nowy niezmiennik** - start pozwalał na duplikaty. Taką regułę zatwierdzamy świadomie, bo zmienia zachowanie dla błędnych danych.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m6/s06 1 2`

### Krok 3: zagnieżdżony builder z lambdą

**W IDE:** `hall(name, Consumer<HallBuilder>)` zamiast "bieżącej sali"; `HallBuilder` z `screening(...)`. Przepisz planera.
**Po:**

```java
return ScheduleBuilder.day(date)
        .hall("Sala 1", hall -> hall
                .screening("Diuna", 18, 0)
                .screening("Diuna", 21, 0))
        .hall("Sala 2", hall -> { ... })
        .build();
```

**Uruchom:** test zielony.
**Co powiedzieć:** wcięcia kodu odpowiadają poziomom drzewa, a ukryty stan "bieżąca sala" zniknął. Kontrakt lambdy: synchroniczna, wywołana dokładnie raz.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m6/s06 2 3`

### Rozwiązanie i uzasadnienie

Builder jest uzasadniony, bo budowa drzewa jest wieloetapowa i ma niezmienniki (kolejność sal, pusta sala dozwolona, unikalne nazwy). Wynik jest niemutowalny.

### Pułapki

- Builder wielokrotnego użytku, którego `build()` zwraca wewnętrzną listę - kolejne wywołania zmieniają już zwrócone drzewo.
- Lambda zapamiętana i wywołana później (odroczenie) - zmienia kolejność i wynik.
- Współdzielone poddrzewo (DAG) - liczy się tyle razy, ile ścieżek do niego prowadzi.

### Pytanie do sali

Czy pusta sala w repertuarze to poprawny stan, czy błąd? Kto powinien o tym zdecydować - builder czy model?

## Scena s07. Move Embellishment to Decorator - dodatki do biletu

**Temat ze slajdów:** Move Embellishment to Decorator; Decorator - wyjątki, migracja, przezroczystość
**Pakiet:** `pl.training.workshop.m6.s07_decorator` · **Test:** `scripts/warsztat.sh test m6/s07`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `Ticket` ma flagi `vip`, `glasses`, `insurance` i te same `if` w `price()` i `description()`. Wydzielamy interfejs `PricedTicket` i przenosimy każdy dodatek do dekoratora, zaczynając od ubezpieczenia, a łańcuch składamy w `TicketAssembler`.

**Zasada:** Move Embellishment to Decorator przenosi opcjonalny dodatek wokół rdzenia do obiektu, który ma ten sam kontrakt i deleguje do środka. Kolejność owijania jest zachowaniem, więc składa się ją w jednym miejscu. Dekorator nie jest przezroczysty dla `instanceof`, `getClass`, tożsamości i `equals`.

**Efekt:** Rdzeń biletu nie ma flag, a nowy dodatek to nowa klasa i linia w assemblerze. Kosztem jest utrata pytania o typ: kto chce wiedzieć, czy bilet jest VIP, potrzebuje osobnego API zamiast `instanceof VipSeat`.

### Co widzimy

`Ticket` ma flagi `vip`, `glasses`, `insurance` i te same `if` w `price()` i `description()`. Większość biletów nie ma żadnego dodatku, a każdy nowy dodatek to kolejne pole. Opis ma ustaloną kolejność: VIP, okulary, ubezpieczenie.

```java
return title + " " + format
        + (vip ? " +VIP" : "")
        + (glasses ? " +okulary 3D" : "")
        + (insurance ? " +ubezpieczenie" : "");
```

### Krok 1: Extract Interface

**W IDE:** Refactor > Extract Interface na `Ticket` - `PricedTicket` z `price()` i `description()`. `TicketAssembler.assemble` zwraca interfejs.
**Po:**

```java
public PricedTicket assemble(TicketOrder order) {
    return new Ticket(order);
}
```

**Uruchom:** `scripts/warsztat.sh test m6/s07` - zielone.
**Co powiedzieć:** wąski kontrakt, który spełnią i rdzeń, i dekoratory. Składanie w jednym miejscu (assembler).
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m6/s07 0 1`

### Krok 2: pierwszy dodatek jako dekorator

**W IDE:** rekord `Insurance(PricedTicket inner)` dodający 4.00 i " +ubezpieczenie". Usuń flagę z `Ticket` (Safe Delete), w assemblerze owiń, gdy `order.insurance()`.
**Po:**

```java
PricedTicket ticket = new Ticket(order);
if (order.insurance()) {
    ticket = new Insurance(ticket);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** zaczynamy od ubezpieczenia, bo w opisie jest **ostatnie** - dekorator dopisuje się na końcu. Gdybyśmy zaczęli od VIP, opis zmieniłby kolejność i test "wszystkie dodatki" by to złapał. Kolejność dekoratorów jest kontraktem.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m6/s07 1 2`

### Krok 3: pozostałe dodatki, czysty rdzeń

**W IDE:** `VipSeat` i `Glasses3D` jako dekoratory, rdzeń jako rekord `Ticket(title, format, base)`. Decyzja "czy okulary potrzebne" (3D i brak własnych) zostaje w assemblerze.
**Po:**

```java
if (order.vip()) ticket = new VipSeat(ticket);
if (order.format().equals("3D") && !order.ownGlasses()) ticket = new Glasses3D(ticket);
if (order.insurance()) ticket = new Insurance(ticket);
```

**Uruchom:** test zielony; `S07SolutionTest` dokumentuje granice przezroczystości.
**Co powiedzieć:** `ticket instanceof VipSeat` zwraca `false`, gdy VIP jest owinięty ubezpieczeniem. `equals` rdzenia i udekorowanego biletu też się różni. Kto pyta o typ albo tożsamość, potrzebuje innego API (np. `features()`).
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m6/s07 2 3`

### Rozwiązanie i uzasadnienie

Rdzeń bez flag, trzy dekoratory z tym samym kontraktem, łańcuch składany w jednej fabryce w ustalonej kolejności.

### Pułapki

- Ślepe delegowanie `equals` do `inner` - łamie symetrię (`core.equals(vip)` różne od `vip.equals(core)`).
- Kod sprawdzający `getClass()`, `instanceof`, synchronizujący na obiekcie albo serializujący go - dekorator nie jest przezroczysty.
- Dekorator z efektem ubocznym (audyt), którego błąd zasłania błąd rdzenia.

### Pytanie do sali

Kasa chce wydrukować "Miejsce VIP" na bilecie. Jak to zrobić, nie używając `instanceof VipSeat`?

## Scena s08. Replace State-Altering Conditionals with State - status rezerwacji

**Temat ze slajdów:** Replace State-Altering Conditionals with State; State - kontekst delegujący
**Pakiet:** `pl.training.workshop.m6.s08_state` · **Test:** `scripts/warsztat.sh test m6/s08`
**Czas:** ~15 min

### W skrócie

**Co robimy:** `Reservation` w każdej operacji sprawdza i zmienia pole `status`, więc reguły przejść są rozsiane po metodach. Zaczynamy od tabeli przejść w teście, zastępujemy pole obiektem stanu i przenosimy `pay`, `use`, `expire`, `cancel` do stanów po jednej akcji.

**Zasada:** State pasuje, gdy zachowanie operacji zależy od bieżącego stanu obiektu i zmienia się razem z przejściami. Kontekst deleguje do obiektu stanu, a niedozwolone przejście domyślnie rzuca wyjątek - pusta metoda po cichu zmieniłaby zachowanie. Od Strategy różni się tym, że stan zmienia się w czasie życia obiektu, zwykle na skutek jego własnych operacji.

**Efekt:** Tabelę przejść da się przeczytać z kodu stanów, a `Reservation` tylko deleguje i przechowuje dane. Kolejność "obciążenie - zmiana stanu - efekt" zostaje jak w start, więc awaria bramki nadal zostawia rezerwację w stanie NEW.

### Co widzimy

`Reservation` ma pole `status` i w każdej operacji (`pay`, `use`, `expire`, `cancel`) warunki sprawdzające i zmieniające status. Reguły przejść NEW -> PAID -> USED, NEW -> EXPIRED, NEW/PAID -> CANCELLED są rozsiane po metodach. `pay` najpierw obciąża bramkę, potem zmienia stan: gdy bramka rzuci wyjątek, rezerwacja zostaje NEW.

```java
public void cancel() {
    if (status == Status.NEW) {
        status = Status.CANCELLED;
        effects.add("seats released");
    } else if (status == Status.PAID) {
        status = Status.CANCELLED;
        effects.add("refund");
        effects.add("seats released");
    } else {
        throw new IllegalStateException("cannot cancel in " + status);
    }
}
```

**Najpierw tabela przejść.** `S08EquivalenceTest` zawiera tabelę 5 stanów x 4 akcje (20 wierszy) plus przypadek awarii bramki. Każdy wiersz sprawdza status końcowy, efekty w kolejności i komunikat wyjątku. Pokaż ją na rzutniku przed pierwszym ruchem.

### Krok 1: obiekt stanu zamiast pola status

**W IDE:** utwórz prywatny `sealed interface ReservationState` z `status()` i stany jako enumy (`NewState.INSTANCE`, `PaidState.INSTANCE`, `ClosedState.USED/EXPIRED/CANCELLED`). Pole `status` zastąp polem `state`; warunki porównują `state.status()`. Efekty i zmianę stanu wyciągnij do `charge()`, `moveTo(...)`, `record(...)` (⌥⌘M).
**Po:**

```java
private ReservationState state = NewState.INSTANCE;
...
charge();
moveTo(PaidState.INSTANCE);
record("charged");
```

**Uruchom:** `scripts/warsztat.sh test m6/s08` - 84 testy zielone.
**Co powiedzieć:** stany końcowe to jeden enum z trzema stałymi - wszystkie zachowują się tak samo (każde przejście jest błędem).
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m6/s08 0 1`

### Krok 2: pay i use do stanów

**W IDE:** w interfejsie stanu dodaj `default void pay(Reservation r) { throw invalid("pay"); }` (i `use`). W `NewState` nadpisz `pay`, w `PaidState` - `use`, przenosząc ciała gałęzi. Kontekst deleguje: `state.pay(this)`.
**Po:**

```java
public void pay(Reservation reservation) {
    reservation.charge();
    reservation.moveTo(PaidState.INSTANCE);
    reservation.record("charged");
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** domyślna metoda **rzuca wyjątek**, nie jest pusta - pusta metoda po cichu zmieniłaby zachowanie niedozwolonych przejść. Kolejność "obciążenie - zmiana stanu - efekt" zostaje dokładnie jak w start; test awarii bramki to pilnuje.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m6/s08 1 2`

### Krok 3: expire i cancel do stanów

**W IDE:** ten sam ruch dla `expire` i `cancel`. Warunek `if/else if` w `cancel` rozkłada się na dwa stany: NEW (zwolnienie miejsc) i PAID (zwrot i zwolnienie).
**Po:**

```java
public void cancel() {
    state.cancel(this);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** tabelę przejść da się teraz przeczytać z kodu stanów. Stany są bezstanowymi stałymi, dane zostają w `Reservation`.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m6/s08 2 3`

### Rozwiązanie i uzasadnienie

Kontekst tylko deleguje. Każdy stan nadpisuje wyłącznie dozwolone przejścia, reszta rzuca wyjątek z tym samym komunikatem co start, a stan po błędzie się nie zmienia.

### Pułapki

- Zmiana stanu przed efektem zewnętrznym (albo odwrotnie) "przy okazji" - to inna semantyka przy awarii.
- `volatile` na polu stanu nie daje atomowości przejścia przy współbieżnych `pay` i `cancel`.
- Przy dwóch stanach i jednej akcji `switch` bywa czytelniejszy niż hierarchia.
- Kilka niezależnych osi stanu (płatność, obecność) w jednej hierarchii - eksplozja klas.

### Pytanie do sali

Czym różni się State od Strategy, skoro oba to "obiekt, któremu delegujemy"?

## Scena s09. Replace Hard-coded Notifications with Observer - po opłaceniu

**Temat ze slajdów:** Replace Hard-coded Notifications with Observer; Observer - kontrakt i migracja
**Pakiet:** `pl.training.workshop.m6.s09_observer` · **Test:** `scripts/warsztat.sh test m6/s09`
**Czas:** ~15 min

### W skrócie

**Co robimy:** `PaymentService.confirm` po zapisaniu opłaty na sztywno wysyła mail, SMS i nalicza punkty lojalnościowe. Wprowadzamy zdarzenie `ReservationPaid`, zamieniamy trzech odbiorców na implementacje `PaymentListener`, a subskrypcje przenosimy do korzenia kompozycji `PaymentServices`.

**Zasada:** Observer odwraca zależność: publikujący zna tylko interfejs odbiorcy, a odbiorcy są do niego rejestrowani z zewnątrz. Kontrakt musi być jawny - synchroniczność, kolejność, polityka błędów (tu fail-fast) i wyrejestrowanie. Dodanie nowych odbiorców to rozszerzenie zachowania, a nie część refaktoryzacji.

**Efekt:** Serwis nie importuje `Mailer`, `SmsGateway` ani `LoyaltyProgram`, a odbiorcy są wołani w kolejności subskrypcji jak w start. Zostaje ta sama semantyka błędów: awaria SMS przerywa naliczanie punktów, a jej zmiana byłaby osobną decyzją.

### Co widzimy

Serwis powstaje w korzeniu kompozycji `PaymentServices.standard(mailer, sms, loyalty)`. `PaymentService.confirm` zapisuje opłatę, a potem na sztywno wysyła mail, SMS i nalicza punkty lojalnościowe (1 pkt za pełne 10.00). Semantyka do zachowania: **kolejność** mail - SMS - punkty i **fail-fast**: wyjątek w SMS przerywa, punkty nie są naliczone, ale opłata jest już zapisana.

```java
paid.add(payment.reservationId());
mailer.send(payment.email(), "Potwierdzenie platnosci " + ...);
sms.send(payment.phone(), "Oplacono " + payment.reservationId());
loyalty.addPoints(payment.email(), payment.amount().amount().intValue() / 10);
```

Test używa fake'ów zapisujących do wspólnego logu i sprawdza kolejność oraz awarie w mailu i SMS.

### Krok 1: zdarzenie i Extract Method

**W IDE:** utwórz rekord `ReservationPaid`. Zaznacz trzy powiadomienia, ⌥⌘M `notifyPaid(event)`.
**Po:**

```java
paid.add(payment.reservationId());
notifyPaid(new ReservationPaid(payment.reservationId(), payment.email(), payment.phone(), payment.amount()));
```

**Uruchom:** `scripts/warsztat.sh test m6/s09` - zielone.
**Co powiedzieć:** zdarzenie to fakt z danymi potrzebnymi odbiorcom. Jeszcze nic nie jest dynamiczne.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m6/s09 0 1`

### Krok 2: odbiorcy jako obserwatorzy

**W IDE:** interfejs `PaymentListener.onPaid(event)` i trzy rekordy: `MailConfirmation`, `SmsConfirmation`, `LoyaltyPoints` (każdy z ciałem jednej linii z `notifyPaid`). Serwis tworzy `List.of(...)` w konstruktorze w starej kolejności; `notifyPaid` to pętla **bez** `try/catch`.
**Po:**

```java
for (PaymentListener listener : listeners) {
    listener.onPaid(event);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** konstruktor bez zmian, relacja wciąż "ci sami trzej odbiorcy" - to nadal czysta refaktoryzacja.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m6/s09 1 2`

### Krok 3: subscribe i korzeń kompozycji

**W IDE:** `subscribe(listener)` zwracające `Subscription` (idempotentne `close()`), lista `CopyOnWriteArrayList` rejestracji. Konstruktor serwisu staje się bezargumentowy, a subskrypcje przenosimy do istniejącego `PaymentServices.standard(...)`.
**Po:**

```java
PaymentService service = new PaymentService();
service.subscribe(new MailConfirmation(mailer));
service.subscribe(new SmsConfirmation(sms));
service.subscribe(new LoyaltyPoints(loyalty));
```

**Uruchom:** test zielony; `S09SolutionTest` pokazuje podwójną subskrypcję, idempotentne wyrejestrowanie i nowego odbiorcę.
**Co powiedzieć:** `Registration` jest klasą, nie rekordem - rekord z tą samą lambdą byłby `equals` i `remove` usunąłby cudzą rejestrację. Dodanie czwartego odbiorcy (push) to już **rozszerzenie** zachowania, zatwierdzane osobno.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m6/s09 2 3`

### Rozwiązanie i uzasadnienie

Serwis zna tylko `PaymentListener`. Kontrakt jest jawny: synchronicznie, w kolejności subskrypcji, fail-fast, iteracja po migawce.

### Pułapki

- "Przy okazji" dodany `try/catch` wokół każdego odbiorcy - zmienia politykę błędów (punkty naliczone mimo awarii SMS).
- Asynchroniczne powiadamianie (Executor) - inny wątek, inna transakcja, inna kolejność.
- Odbiorca usunięty w trakcie publikacji może być jeszcze wywołany w bieżącej (migawka).
- Niezamknięta subskrypcja trzyma przy życiu graf obiektów.

### Pytanie do sali

Czy błąd wysyłki SMS powinien przerywać naliczanie punktów? Kto o tym decyduje i gdzie to zapisać?

## Scena s10. Replace Implicit Tree with Composite - zestawy baru

**Temat ze slajdów:** Replace Implicit Tree with Composite; Implicit Tree - mapper, procedura, ryzyka
**Pakiet:** `pl.training.workshop.m6.s10_implicittree` · **Test:** `scripts/warsztat.sh test m6/s10`
**Czas:** ~15 min

### W skrócie

**Co robimy:** Zestaw combo to zagnieżdżona lista, w której pierwszy element jest nazwą, a `BarMenu` powtarza `instanceof` i rzutowania. Budujemy obok jawny Composite (`Product`, `Combo`) z mapperem ze starego formatu, a potem przenosimy na niego `price` i `render` po jednej operacji.

**Zasada:** Replace Implicit Tree with Composite zamienia drzewo ukryte w konwencji danych na jawne typy liścia i węzła. Mapper tłumaczy stary format i zgłasza błąd dla danych, których nie da się wiernie odwzorować. Stara i nowa reprezentacja żyją obok siebie, a test różnicowy potwierdza zgodność, ale nie zastępuje niezależnych oczekiwań.

**Efekt:** `BarMenu` nie ma `instanceof` ani rzutowań, a komunikaty błędów zostają jak w start. Format trwały (zagnieżdżone listy) się nie zmienia - jego zmiana to osobna decyzja z osobnym testem.

### Co widzimy

Zestaw combo to zagnieżdżona lista: pierwszy element to nazwa, kolejne to `"nazwa=cena"` albo podlisty. `BarMenu.price` i `BarMenu.render` powtarzają `instanceof` i rzutowania, a `render` dla każdego węzła liczy cenę od nowa.

```java
List.of("Zestaw Duo", "Popcorn L=18.00",
        List.of("Napoje", "Cola 0.5=9.00", "Cola 0.5=9.00"), "Nachos=14.00")
```

Testy: `S10EquivalenceTest` (niezależne oczekiwania policzone ręcznie, także błędy) i `S10DifferentialTest` (500 losowych drzew ze stałym ziarnem - stara reprezentacja kontra nowa).

### Krok 1: Composite i mapper obok starego kodu

**W IDE:** utwórz `sealed interface MenuItem` z rekordami `Product` i `Combo` oraz `MenuMapper.fromNested(List<?>)`. `BarMenu` bez zmian. Uruchom test różnicowy - porównuje stary kod z drzewem z mappera.
**Po:**

```java
items.add(switch (element) {
    case String product -> product(product);
    case List<?> nested -> fromNested(nested);
    default -> throw new IllegalArgumentException("unsupported element: " + element);
});
```

**Uruchom:** `scripts/warsztat.sh test m6/s10` - zielone, w tym `compositeFromMapperMatchesLegacyAlready`.
**Co powiedzieć:** obie reprezentacje żyją obok siebie. Mapper zachowuje komunikaty błędów starego kodu - to też obserwowalne zachowanie.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m6/s10 0 1`

### Krok 2: price na Composite

**W IDE:** ciało `BarMenu.price` zastąp `MenuMapper.fromNested(combo).price()`. Usuń nieużywane `requireName`.
**Po:**

```java
public Money price(List<?> combo) {
    return MenuMapper.fromNested(combo).price();
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** jedna operacja naraz; `render` wciąż jest stary i działa, bo woła nowe `price`.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m6/s10 1 2`

### Krok 3: render na Composite

**W IDE:** `render` deleguje do `Combo.render(0, text)`; stara rekurencja z `instanceof` do usunięcia (Safe Delete).
**Po:**

```java
StringBuilder text = new StringBuilder();
MenuMapper.fromNested(combo).render(0, text);
return text.toString();
```

**Uruchom:** test zielony, test różnicowy `finalBarMenuMatchesLegacy` zielony.
**Co powiedzieć:** format trwały (zagnieżdżone listy) zostaje. Jego zmiana na JSON czy tabelę w bazie to osobna decyzja z osobnym testem.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m6/s10 2 3`

### Rozwiązanie i uzasadnienie

Jawne drzewo z typami zamiast konwencji "pierwszy element to nazwa". Test różnicowy daje szerokie pokrycie, a niezależne oczekiwania chronią przed sytuacją, w której oba warianty mylą się tak samo.

### Pułapki

- Test różnicowy jako jedyny test - jeśli stary kod ma błąd, nowy go wiernie powtórzy.
- Brak limitu głębokości dla danych niezaufanych - `StackOverflowError`.
- Cykl w danych (lista zawierająca samą siebie) - nieskończona rekurencja w obu wersjach.

### Pytanie do sali

Co zrobić, gdy mapper natrafi na dane, których nie da się wiernie odwzorować w drzewie?

## Scena s11. Transparent vs Safe Composite - add() na liściu

**Temat ze slajdów:** Replace One/Many Distinctions with Composite (Safe i Transparent Composite)
**Pakiet:** `pl.training.workshop.m6.s11_safecomposite` · **Test:** `scripts/warsztat.sh test m6/s11`
**Czas:** ~8 min

### W skrócie

**Co robimy:** W Transparent Composite `add()` jest we wspólnym `MenuComponent`, więc wywołanie go na produkcie kompiluje się i wybucha dopiero w runtime. Przesuwamy `add` i `children` w dół do `Combo` (Safe Composite), a potem pokazujemy niemutowalne drzewo z rekordów bez `add`.

**Zasada:** Transparent Composite trzyma zarządzanie dziećmi we wspólnym interfejsie, więc liść musi rzucić wyjątek albo nic nie robić. Safe Composite trzyma je tylko w węźle, dzięki czemu błąd przenosi się do kompilacji, ale klient musi wiedzieć, czy ma w ręku węzeł. Gdy drzewo powstaje raz, dylemat znika, bo `add` nie jest potrzebne nigdzie.

**Efekt:** Dodanie dziecka do produktu przestaje się kompilować, a `MenuComponent` nie ma już `add` ani `children`. Kosztem jest dokładniejsze typowanie zmiennych w katalogu - rzutowanie `(Combo)` w kliencie oznaczałoby powrót do problemu.

### Co widzimy

Transparent Composite: `add()` i `children()` są we wspólnym `MenuComponent`, liść `Product` dziedziczy `add()`, które rzuca `UnsupportedOperationException`. Klient typuje wszystko jako `MenuComponent` i nic go nie chroni przed `nachos.add(sos)`.

```java
public void add(MenuComponent child) {
    throw new UnsupportedOperationException("cannot add to " + name());
}
```

### Krok 1: Safe Composite - Push Members Down

**W IDE:** Refactor > Push Members Down na `add` i `children` z `MenuComponent` do `Combo`. IntelliJ pokaże konflikty w `ComboCatalog` - zmień typ zmiennych z `MenuComponent` na `Combo` tam, gdzie dodajemy dzieci. `describe()` w `Combo` rozszerza opis o dzieci.
**Po:**

```java
Combo combo = new Combo("Zestaw Rodzinny");
combo.add(new Product("Popcorn XL", "24.00"));
```

**Uruchom:** `scripts/warsztat.sh test m6/s11` - zielone; `S11SolutionTest` pokazuje, że `Product` nie ma już `add`.
**Co powiedzieć:** błąd przeniósł się z runtime do kompilacji. Ceną jest to, że klient musi wiedzieć, czy ma w ręku węzeł.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m6/s11 0 1`

### Krok 2: niemutowalny Composite w Javie 25

**W IDE:** `sealed interface MenuComponent`, rekordy `Product` i `Combo(name, children)` z `Combo.of(...)`. Katalog zapisany deklaratywnie.
**Po:**

```java
case "duo" -> Combo.of("Zestaw Duo",
        new Product("Popcorn L", "18.00"),
        new Product("Cola", "9.00"),
        new Product("Cola", "9.00"));
```

**Uruchom:** test zielony.
**Co powiedzieć:** gdy drzewo powstaje raz, `add()` nie jest potrzebne nigdzie - dylemat Safe/Transparent znika.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m6/s11 1 2`

### Rozwiązanie i uzasadnienie

Safe Composite, gdy drzewo jest modyfikowane; niemutowalny Composite, gdy jest budowane raz (wtedy budowę przejmuje Builder albo fabryka - scena s06).

### Pułapki

- Transparent Composite z domyślnym pustym `add()` (bez wyjątku) - dziecko po cichu ginie.
- Rzutowanie `(Combo) component` w kliencie zamiast poprawienia typów - Safe Composite w przebraniu Transparent.

### Pytanie do sali

Kiedy Transparent Composite jest lepszym wyborem mimo ryzyka błędu w runtime?

## Scena s12. Replace One/Many Distinctions with Composite - zwroty

**Temat ze slajdów:** Replace One/Many Distinctions with Composite
**Pakiet:** `pl.training.workshop.m6.s12_onemany` · **Test:** `scripts/warsztat.sh test m6/s12`
**Czas:** ~10 min

### W skrócie

**Co robimy:** `RefundService` ma osobne `refund` i `refundAll` z regułą zwrotu zapisaną dwa razy, a `CancellationDesk` sam wybiera między nimi. Najpierw usuwamy duplikację reguły, potem wprowadzamy wspólny kontrakt `Refundable` z liściem i grupą, a stare metody usuwamy po etapie `@Deprecated`.

**Zasada:** Replace One/Many Distinctions with Composite zastępuje dwie ścieżki API - dla jednego elementu i dla wielu - jednym kontraktem, w którym grupa też jest elementem. Nie wolno przy tym zmienić kolejności przetwarzania, wyniku dla pustej grupy ani liczby efektów naliczanych raz na wywołanie.

**Efekt:** Jest jedna metoda `refund(Refundable)`, reguła biletu żyje w liściu, suma w węźle, a klient nie ma już `if`. Potrącenie 3.00 zostaje w serwisie i nadal jest naliczane raz na zwrot, a nie raz na bilet.

### Co widzimy

`RefundService` ma `refund(ticket, now)` i `refundAll(tickets, now)`, a klient `CancellationDesk` sam wybiera jedną z nich (`if (tickets.size() == 1)`). Reguła zwrotu (>= 24h 100%, < 24h 50%, po starcie 0) jest napisana dwa razy, trochę inaczej. Potrącenie 3.00 jest naliczane raz na zwrot i nie schodzi poniżej 0.

```java
for (TicketData ticket : tickets) {
    if (now.isBefore(ticket.showStart())) {
        long hours = Duration.between(now, ticket.showStart()).toHours();
        total = total.plus(hours >= 24 ? ticket.price() : ticket.price().percent(50));
    }
}
```

Test obejmuje granicę dokładnie 24h, 23h59m, bilet po starcie, listę i pustą listę.

### Krok 1: Extract Method na wspólnej regule

**W IDE:** w `refund` zaznacz wyliczenie kwoty, ⌥⌘M `ticketShare(ticket, now)`. W `refundAll` zastąp ciało pętli wywołaniem `ticketShare`.
**Po:**

```java
total = total.plus(ticketShare(ticket, now));
```

**Uruchom:** `scripts/warsztat.sh test m6/s12` - zielone.
**Co powiedzieć:** zanim wprowadzimy wzorzec, usuwamy duplikację reguły. Test granicy 24h potwierdza, że obie wersje były równoważne.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m6/s12 0 1`

### Krok 2: Composite i jeden kontrakt

**W IDE:** `sealed interface Refundable` z `refundableAmount(now)`, liść `SingleTicket` (reguła z `ticketShare`), węzeł `TicketGroup` (suma). Nowa metoda `refund(Refundable, now)`; stare metody jako delegacje z `@Deprecated`. `CancellationDesk` przechodzi na nowy kontrakt (na razie wciąż wybiera `SingleTicket` albo `TicketGroup`).
**Po:**

```java
public Money refund(Refundable refundable, LocalDateTime now) {
    return refundable.refundableAmount(now).minus(FEE).max(Money.ZERO);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** potrącenie jest w jednym miejscu i naliczane raz na wywołanie - tak jak w obu starych metodach.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m6/s12 1 2`

### Krok 3: usunięcie starych metod

**W IDE:** po migracji klientów Safe Delete (⌘⌦) na `refund(TicketData, ...)` i `refundAll`. W `CancellationDesk` usuń `if` - klient zawsze buduje `TicketGroup.of(tickets)`, także z jednego biletu.
**Po:**

```java
service.refund(TicketGroup.of(tickets), now);
```

**Uruchom:** test zielony; `S12SolutionTest` pokazuje zagnieżdżone grupy.
**Co powiedzieć:** rozróżnienie "jeden/wiele" zniknęło z API. Grupa jednego biletu daje ten sam wynik co bilet.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m6/s12 2 3`

### Rozwiązanie i uzasadnienie

Jeden kontrakt `refund(Refundable)`, reguła biletu w liściu, suma w węźle. Stare API usunięte dopiero po etapie `@Deprecated`.

### Pułapki

- Potrącenie przeniesione do liścia - wtedy lista biletów płaci 3.00 za każdy bilet (zmiana zachowania).
- Zmiana kolejności przetwarzania albo liczby transakcji przy przejściu na Composite.
- Pusta grupa - trzeba jawnie ustalić wynik (tu 0.00, jak w start).

### Pytanie do sali

Co, jeśli biznes chce potrącenia za każdą rezerwację, a jedna grupa obejmuje kilka rezerwacji?

## Scena s13. Extract Composite - kontenery programu

**Temat ze slajdów:** Extract Composite
**Pakiet:** `pl.training.workshop.m6.s13_extractcomposite` · **Test:** `scripts/warsztat.sh test m6/s13`
**Czas:** ~8 min

### W skrócie

**Co robimy:** `Marathon` i `ShortsBlock` powielają obsługę dzieci - listę, `add`, kopię `children()`, sumę minut i opis. Wydzielamy nadklasę `CompositeProgramItem` i podciągamy do niej to, co naprawdę wspólne, a regułę czasu zostawiamy w podklasach.

**Zasada:** Extract Composite wydziela wspólną nadklasę dla kilku kontenerów, które powielają zarządzanie dziećmi. Podobne pętle mogą znaczyć co innego, więc przed podciągnięciem porównujemy kontrakty, a nie tekst. Nowe reguły, np. wykrywanie cykli, to osobna zmiana zachowania.

**Efekt:** Lista dzieci, `add` i szkielet opisu są w jednym miejscu, a podklasy mają po kilkanaście linii. `minutes()` zostaje w podklasach, bo tylko maraton dolicza przerwy - podciągnięcie go do bazy zmieniłoby wynik.

### Co widzimy

`Marathon` i `ShortsBlock` powielają obsługę dzieci: lista, `add` z kontrolą `null`, `children()` jako kopia, suma minut, opis. Różnią się tylko regułą czasu: maraton dodaje 15 minut przerwy między pozycjami.

```java
return children.isEmpty() ? 0 : total + 15 * (children.size() - 1);
```

### Krok 1: Extract Superclass

**W IDE:** na `Marathon` Refactor > Extract Superclass: `CompositeProgramItem`, zaznacz pole `children`, `add`, `children()`. Potem `ShortsBlock extends CompositeProgramItem` i usuń jego kopie (Pull Members Up już nie trzeba - są w bazie).
**Po:**

```java
public abstract class CompositeProgramItem implements ProgramItem {
    private final List<ProgramItem> children = new ArrayList<>();
    public final void add(ProgramItem child) { ... }
```

**Uruchom:** `scripts/warsztat.sh test m6/s13` - zielone.
**Co powiedzieć:** kontrakt dzieci (brak `null`, kopia listy) jest teraz w jednym miejscu dla wszystkich kontenerów.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m6/s13 0 1`

### Krok 2: Pull Up sumy i opisu

**W IDE:** Pull Members Up dla `childrenMinutes()` (`protected final`) i `describe()` jako szablon z hookiem `label()`. Podklasy zostają z regułą czasu i etykietą.
**Po:**

```java
public int minutes() {
    int count = children().size();
    return count == 0 ? 0 : childrenMinutes() + 15 * (count - 1);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** podobne pętle mogą znaczyć co innego. Tu suma jest wspólna, ale przerwy nie - dlatego `minutes()` zostaje w podklasach.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m6/s13 1 2`

### Rozwiązanie i uzasadnienie

`CompositeProgramItem` z pełną obsługą dzieci i szkieletem opisu; `Marathon` i `ShortsBlock` po kilkanaście linii.

### Pułapki

- Podciągnięcie całego `minutes()` do bazy - maraton traci przerwy.
- Wykrywanie cykli (maraton zawierający sam siebie) to osobna zmiana zachowania, nie część Extract Composite.

### Pytanie do sali

Trzeci kontener "blok z przerwą na reklamy co drugi film" - czy nadal pasuje do tej nadklasy?

## Scena s14. Unify Interfaces with Adapter - dwie bramki płatności

**Temat ze slajdów:** Unify Interfaces with Adapter; Adapter - co naprawdę trzeba przetłumaczyć
**Pakiet:** `pl.training.workshop.m6.s14_adapter` · **Test:** `scripts/warsztat.sh test m6/s14`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `CheckoutService.pay` mówi dwoma językami: dla starej bramki składa XML w groszach, dla nowej woła REST w złotych i łapie wyjątek odmowy. Wydzielamy obie gałęzie do metod o jednej sygnaturze, przenosimy je do adapterów interfejsu `PaymentGateway`, a serwis wybiera bramkę z mapy.

**Zasada:** Unify Interfaces with Adapter tłumaczy obcy interfejs na preferowany kontrakt klienta, tak żeby klient zależał tylko od niego. Adapter tłumaczy nazwy, jednostki, format i sposób zgłaszania błędów, ale nie udaje, że semantyka jest identyczna. Jeśli wystarczy Rename Method, adapter jest zbędny.

**Efekt:** Logika serwisu zna tylko `PaymentGateway` i da się ją testować fake'iem, a trzeci dostawca to nowy adapter i wpis w mapie. Stary konstruktor zostaje jako jedyne miejsce znające biblioteki bramek - docelowo do przeniesienia do korzenia kompozycji.

### Co widzimy

`CheckoutService.pay(provider, reservationId, amount)` mówi dwoma językami: dla starej bramki składa XML z kwotą w **groszach** i parsuje atrybuty odpowiedzi, dla nowej woła API REST w **złotych** i łapie wyjątek odmowy. "Biblioteki" bramek (`XmlPayGateway`, `RestPayClient`) są w pakiecie sceny i ich nie zmieniamy.

```java
long grosze = amount.amount().movePointRight(2).longValueExact();
String request = "<charge ref='" + reservationId + "' amount='" + grosze + "'/>";
```

### Krok 1: Extract Method z jednolitą sygnaturą

**W IDE:** każdą gałąź ⌥⌘M: `payWithXml(reservationId, amount)` i `payWithRest(reservationId, amount)`, obie zwracają `PaymentResult`.
**Po:**

```java
if (provider.equals("XML")) {
    return payWithXml(reservationId, amount);
} else if (provider.equals("REST")) {
    return payWithRest(reservationId, amount);
}
```

**Uruchom:** `scripts/warsztat.sh test m6/s14` - zielone.
**Co powiedzieć:** identyczna sygnatura dwóch metod to gotowy kontrakt adaptera.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m6/s14 0 1`

### Krok 2: interfejs i dwa adaptery

**W IDE:** interfejs `PaymentGateway.pay(reservationId, amount)`. Move Method (F6) `payWithXml` do `XmlPayAdapter` i `payWithRest` do `RestPayAdapter`. Konstruktor serwisu bez zmian - tworzy adaptery.
**Po:**

```java
public CheckoutService(XmlPayGateway xml, RestPayClient rest) {
    this.xml = new XmlPayAdapter(xml);
    this.rest = new RestPayAdapter(rest);
}
```

**Uruchom:** test zielony; `S14SolutionTest` sprawdza, że do starej bramki idzie `amount='4000'`.
**Co powiedzieć:** adapter tłumaczy jednostki (złote - grosze), format i sposób zgłaszania odmowy (atrybut kontra wyjątek). Nie udaje, że semantyka jest taka sama.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m6/s14 1 2`

### Krok 3: serwis zależy tylko od interfejsu

**W IDE:** nowy konstruktor `CheckoutService(Map<String, PaymentGateway> gateways)` (kopia `Map.copyOf`); stary konstruktor deleguje do niego ze standardowym zestawem adapterów. `if` zastąp wyszukaniem w mapie; nieznany dostawca - ten sam wyjątek.
**Po:**

```java
PaymentGateway gateway = gateways.get(provider);
if (gateway == null) {
    throw new IllegalArgumentException("unknown provider: " + provider);
}
return gateway.pay(reservationId, amount);
```

**Uruchom:** test zielony.
**Co powiedzieć:** logika serwisu zna tylko `PaymentGateway` i da się ją testować fake'iem. Stary konstruktor to jedyne miejsce, które jeszcze zna biblioteki bramek - docelowo przeniesiemy go do korzenia kompozycji. Trzeci dostawca to nowy adapter i wpis w mapie.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m6/s14 2 3`

### Rozwiązanie i uzasadnienie

Preferowany kontrakt kina (`PaymentGateway`, `PaymentResult`) i dwa adaptery. Granica 500.00 włącznie działa tak samo w obu bramkach - test to sprawdza.

### Pułapki

- Podobna sygnatura, inny kontrakt: jednostki, zaokrąglenia, strefa czasowa, kodowanie znaków.
- Mapowanie wyjątku bez zachowania przyczyny (`cause`) - trudna diagnoza w produkcji.
- Adapter zamykający zasób, którego nie jest właścicielem.
- Adapter tam, gdzie wystarczyłby Rename Method.

### Pytanie do sali

Stara bramka zwraca `status='ERROR'` przy błędnym XML. Jak to zamapować - na odmowę czy wyjątek? Kto decyduje?

## Scena s15. Replace Conditional Dispatcher with Command - konsola kasjera

**Temat ze slajdów:** Replace Conditional Dispatcher with Command; Command - sekwencja i ograniczenia
**Pakiet:** `pl.training.workshop.m6.s15_command` · **Test:** `scripts/warsztat.sh test m6/s15`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `CashierConsole.handle` to łańcuch `if` po nazwie komendy z pełną logiką i zmianą stanu kasy w każdej gałęzi. Wydzielamy gałęzie do metod o wspólnej sygnaturze, zamieniamy je na bezstanowe komendy działające na `Till`, a łańcuch `if` zastępujemy mapą komend.

**Zasada:** Replace Conditional Dispatcher with Command zamienia każdą gałąź dyspozytora w obiekt komendy, a wybór gałęzi w wyszukanie w rejestrze. Mapa jest równoważna warunkom tylko wtedy, gdy klucze są rozłączne i zachowana jest normalizacja. Command nie daje automatycznie asynchroniczności, retry ani undo.

**Efekt:** Dodanie komendy to nowa klasa i wpis w mapie, bez zmiany `handle`. Komendy nie trzymają danych żądania, więc można je bezpiecznie współdzielić, a `sell` i `SELL` nadal działają tak samo.

### Co widzimy

`CashierConsole.handle(line)` to łańcuch `if (command.equals("SELL"))` z pełną logiką i modyfikacją stanu kasy w każdej gałęzi. Nazwa komendy jest normalizowana `toUpperCase(Locale.ROOT)` - `sell` i `SELL` działają tak samo.

```java
if (command.equals("SELL")) {
    String[] sell = args.split(" ", 2);
    ...
} else if (command.equals("REFUND")) {
    ...
} else if (command.equals("REPORT")) {
```

Test to sesja poleceń: sprzedaż, zwrot, raport oraz błędne polecenia, które nie zmieniają stanu.

### Krok 1: Extract Method dla gałęzi

**W IDE:** ciało każdej gałęzi ⌥⌘M: `sell(args)`, `refund(args)`, `report(args)` - ta sama sygnatura, także gdy `args` nie jest używane.
**Po:**

```java
if (command.equals("SELL")) {
    return sell(args);
}
```

**Uruchom:** `scripts/warsztat.sh test m6/s15` - zielone.
**Co powiedzieć:** jednolita sygnatura przygotowuje interfejs komendy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m6/s15 0 1`

### Krok 2: komendy jako obiekty, stan w Till

**W IDE:** Extract Class `Till` (pola `cash`, `tickets` i cennik). Interfejs `ConsoleCommand.execute(args, till)`, klasy `SellCommand`, `RefundCommand`, `ReportCommand` z ciałami metod. Dyspozytor `if` jeszcze zostaje.
**Po:**

```java
if (command.equals("SELL")) {
    return sell.execute(args, till);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** komendy są bezstanowe - dane żądania to argument, stan kasy to argument. Współdzielona komenda ze stanem poprzedniego żądania to błąd czekający na drugi wątek.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m6/s15 1 2`

### Krok 3: rejestr zamiast warunku

**W IDE:** `Map<String, ConsoleCommand>` w konstruktorze (domyślnym i wstrzykiwanym), `handle` wyszukuje komendę po znormalizowanym kluczu.
**Po:**

```java
ConsoleCommand command = commands.get(parts[0].toUpperCase(Locale.ROOT));
if (command == null) {
    return "Nieznana komenda: " + parts[0];
}
```

**Uruchom:** test zielony; `S15SolutionTest` dodaje komendę bez zmiany dyspozytora.
**Co powiedzieć:** mapa jest równoważna warunkom, bo klucze są **rozłączne** i normalizacja została zachowana. Przy nakładających się predykatach (`startsWith`) kolejność `if` byłaby częścią kontraktu i mapa by go zgubiła.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m6/s15 2 3`

### Rozwiązanie i uzasadnienie

Dyspozytor bez wiedzy o komendach, komendy bezstanowe, stan w `Till`. Nowa komenda to nowa klasa i wpis w mapie.

### Pułapki

- Zgubiona normalizacja klucza - `sell 1 Diuna` nagle "nieznana komenda".
- Command traktowany jako darmowa asynchroniczność, retry albo undo - to osobne decyzje (idempotencja, kompensacja).
- Brak sprawdzenia kompletności rejestru przy starcie aplikacji.

### Pytanie do sali

Jak dodalibyście `UNDO` dla ostatniej sprzedaży? Czego wymaga to od komend?

## Scena s16. Form Template Method - raporty CSV i HTML

**Temat ze slajdów:** Apply Template Method; Template Method - sekwencja i ryzyka
**Pakiet:** `pl.training.workshop.m6.s16_templatemethod` · **Test:** `scripts/warsztat.sh test m6/s16`
**Czas:** ~8 min

### W skrócie

**Co robimy:** `CsvSalesReport` i `HtmlSalesReport` mają ten sam szkielet (sortowanie, nagłówek, wiersze z sumą, stopka), ale napisany trochę inaczej. Wydzielamy różnice do metod `header`, `row`, `footer`, doprowadzamy `render` do identycznej postaci i podciągamy go do nadklasy `SalesReport` jako `final`.

**Zasada:** Form Template Method umieszcza wspólną sekwencję kroków w metodzie bazowej, a zmienne kroki w metodach nadpisywanych przez podklasy. Stosuje się ją, gdy kolejność kroków jest stała i różnią się tylko szczegóły. Przy wielu hookach albo zmiennej kolejności lepsza jest kompozycja lub Strategy.

**Efekt:** Sortowanie i sumowanie są w jednym miejscu, a nowy format (np. Markdown) to trzy metody. `final` chroni kolejność kroków, ale w opublikowanej bibliotece dodanie go do nadpisywanej metody złamałoby klientów.

### Co widzimy

`CsvSalesReport` i `HtmlSalesReport` mają ten sam szkielet: kopia listy, sortowanie po godzinie, nagłówek, wiersze z sumowaniem, stopka. Napisane trochę inaczej (stream kontra `sort`, inne nazwy zmiennych), więc duplikacja nie rzuca się w oczy. Różnice to formatowanie i escapowanie (`;` w CSV, `&` w HTML).

### Krok 1: Extract Method na różnicach

**W IDE:** w obu klasach ⌥⌘M dla nagłówka, wiersza i stopki: `header()`, `row(Sale)`, `footer(int tickets, Money total)`. Przepisz `render` HTML na ten sam kształt co CSV (nazwy zmiennych, sortowanie).
**Po:**

```java
StringBuilder text = new StringBuilder(header());
for (Sale sale : sorted) {
    text.append(row(sale));
    ...
}
return text.append(footer(tickets, total)).toString();
```

**Uruchom:** `scripts/warsztat.sh test m6/s16` - zielone.
**Co powiedzieć:** ujednolicamy sygnatury po kroku, aż `render` obu klas będzie identyczny znak w znak.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m6/s16 0 1`

### Krok 2: Extract Superclass + Pull Up render

**W IDE:** Extract Superclass `SalesReport` z CSV, zaznacz `render` i trzy metody jako abstrakcyjne. W HTML `extends SalesReport`, usuń kopię `render`. Dodaj `final` do `render`.
**Po:**

```java
public final String render(List<Sale> sales) { ... }
protected abstract String header();
protected abstract String row(Sale sale);
protected abstract String footer(int tickets, Money total);
```

**Uruchom:** test zielony; `S16SolutionTest` sprawdza `final` i dodaje format Markdown trzema metodami.
**Co powiedzieć:** `final` chroni kolejność kroków. W opublikowanej bibliotece dodanie `final` do nadpisywanej metody łamie klientów.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m6/s16 1 2`

### Rozwiązanie i uzasadnienie

Szkielet raz, w bazie; podklasy to czyste formatowanie. Pusta sprzedaż daje nagłówek i sumę 0.00 w obu formatach.

### Pułapki

- Hook opcjonalny z domyślną implementacją, która nie pasuje do wszystkich podklas.
- Wywołanie hooka z konstruktora bazy - podklasa widzi niezainicjalizowane pola.
- Wiele hooków i zmienna kolejność - wtedy lepsza kompozycja lub Strategy.

### Pytanie do sali

Raport PDF potrzebuje stronicowania co 30 wierszy. Czy to jeszcze Template Method?

## Scena s17. Limit Instantiation with Singleton - cennik

**Temat ze slajdów:** Limit Instantiation with Singleton
**Pakiet:** `pl.training.workshop.m6.s17_singleton` · **Test:** `scripts/warsztat.sh test m6/s17`
**Czas:** ~10 min

### W skrócie

**Co robimy:** `TicketDesk` przy każdej wycenie tworzy nowy `PriceList` i parsuje taryfę, choć cennik jest niemutowalny. Po pomiarze ograniczamy go do jednej instancji (najpierw `getInstance()`, potem enum), a na końcu ukrywamy za interfejsem `Tariff` wstrzykiwanym do `TicketDesk`.

**Zasada:** Limit Instantiation with Singleton to decyzja o cyklu życia: jedna instancja jest bezpieczna tylko wtedy, gdy obiekt jest niemutowalny, a instancje równoważne. Enum daje bezpieczną publikację, ale oznacza jedną instancję na loader klas, nie na JVM. Odwrotny ruch, Inline Singleton, jest równie ważny, gdy globalny dostęp szkodzi testom.

**Efekt:** Cennik powstaje raz, a `TicketDesk` zależy od kontraktu i da się go przetestować z innym cennikiem bez globalnego stanu. Globalna instancja zostaje tylko w domyślnym konstruktorze, a dodanie do niej mutowalnego stanu byłoby najgorszym wariantem.

### Co widzimy

`TicketDesk.quote` przy każdym wywołaniu robi `new PriceList()`, a konstruktor parsuje taryfę. Cennik jest niemutowalny, więc instancje są równoważne. `S17SolutionTest` najpierw **mierzy**: trzy wyceny to trzy utworzone cenniki.

```java
Money price = new PriceList().basePrice(format);
```

### Krok 1: klasyczny Singleton

**W IDE:** prywatny konstruktor, `private static final PriceList INSTANCE = new PriceList()`, `getInstance()`. Licznik z pomiaru usuń. Klient woła `PriceList.getInstance()`.
**Po:**

```java
Money price = PriceList.getInstance().basePrice(format);
```

**Uruchom:** `scripts/warsztat.sh test m6/s17` - zielone.
**Co powiedzieć:** Singleton to decyzja o cyklu życia - bezpieczna tylko dlatego, że obiekt jest niemutowalny i udowodniliśmy równoważność instancji.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m6/s17 0 1`

### Krok 2: Singleton jako enum

**W IDE:** zamień klasę na `enum PriceList { INSTANCE; ... }`, mapa przez `Map.copyOf`.
**Po:**

```java
Money price = PriceList.INSTANCE.basePrice(format);
```

**Uruchom:** test zielony.
**Co powiedzieć:** enum daje bezpieczną publikację i odporność na serializację oraz refleksję. Ale to **jedna instancja na loader klas**, nie "jedna na JVM".
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m6/s17 1 2`

### Krok 3: wstrzyknięcie zamiast globalnego dostępu

**W IDE:** Extract Interface `Tariff` z enuma; `TicketDesk(Tariff)` plus konstruktor bezargumentowy `this(PriceList.INSTANCE)`.
**Po:**

```java
public TicketDesk() {
    this(PriceList.INSTANCE);
}
```

**Uruchom:** test zielony; `injectedTariffNeedsNoGlobalState` używa cennika promocyjnego jako lambdy.
**Co powiedzieć:** o jednej instancji decyduje korzeń kompozycji, a klient zależy od kontraktu. Gdyby ktoś dodał do singletona `setPromo(...)`, testy zaczęłyby wpływać na siebie nawzajem - mutowalny globalny stan to najgorszy wariant.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m6/s17 2 3`

### Rozwiązanie i uzasadnienie

Jedna instancja cennika (enum), ale ukryta za interfejsem i wstrzykiwana. Odwrotna transformacja (Inline Singleton) jest równie ważna - gdy globalny dostęp szkodzi testom, wracamy do jawnej zależności.

### Pułapki

- Singleton z mutowalnym stanem (promocje, cache) - testy zależne od kolejności.
- Leniwy singleton z podwójnym sprawdzaniem bez `volatile`.
- Singleton "bo tak wygodniej" bez pomiaru kosztu tworzenia.

### Pytanie do sali

Cennik ma się zmieniać o północy bez restartu. Co wtedy z singletonem?

## Scena s18. Move Accumulation to Collecting Parameter - ostrzeżenia walidacji

**Temat ze slajdów:** Collecting Parameter
**Pakiet:** `pl.training.workshop.m6.s18_collectingparameter` · **Test:** `scripts/warsztat.sh test m6/s18`
**Czas:** ~8 min

### W skrócie

**Co robimy:** `ReservationValidator` skleja ostrzeżenia w `String`, a każda metoda pomocnicza dopisuje własny separator. Zamieniamy sklejanie na listę, potem przekazujemy akumulator do metod `check...`, a na końcu zawężamy go do klasy `Warnings`.

**Zasada:** Collecting Parameter polega na tym, że metody dopisują wyniki do przekazanego akumulatora, zamiast zwracać fragmenty do sklejenia. Właścicielem kolekcji jest wywołujący, trzeba ustalić, czy metoda czyści, czy dopisuje, a wynik częściowy po wyjątku jest częścią kontraktu. Dobry akumulator ma wąski typ, a nie ogólną mutowalną kolekcję.

**Efekt:** Separator i format wyniku są w jednym miejscu, a metody pomocnicze mogą tylko dopisać ostrzeżenie. Treść, kolejność i separator ostrzeżeń są dokładnie takie jak w start.

### Co widzimy

`ReservationValidator.validate` skleja ostrzeżenia w `String`: każda metoda pomocnicza zwraca fragment zakończony `"; "`, a na końcu obcinamy dwa znaki. Łatwo zgubić separator.

```java
warnings += checkEmail(draft.email());
warnings += checkSeats(draft.seats());
...
return warnings.isEmpty() ? "OK" : warnings.substring(0, warnings.length() - 2);
```

Test sprawdza treść, kolejność i separator, w tym duplikaty miejsc zgłaszane jeden raz.

### Krok 1: lista zamiast sklejania

**W IDE:** metody pomocnicze zwracają `List<String>` bez separatora, `validate` robi `addAll` i `String.join("; ", ...)`.
**Po:**

```java
warnings.addAll(checkEmail(draft.email()));
warnings.addAll(checkSeats(draft.seats()));
```

**Uruchom:** `scripts/warsztat.sh test m6/s18` - zielone.
**Co powiedzieć:** separator dokłada teraz jedno miejsce. Metody wciąż tworzą własne listy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m6/s18 0 1`

### Krok 2: Collecting Parameter

**W IDE:** Change Signature (⌘F6) - dodaj parametr `List<String> warnings`, zmień zwracany typ na `void`, zamień `return List.of(x)` na `warnings.add(x)`. Warunek czasu seansu wyciągnij do `checkShowTime(draft, warnings)`.
**Po:**

```java
checkEmail(draft.email(), warnings);
checkSeats(draft.seats(), warnings);
checkShowTime(draft, warnings);
```

**Uruchom:** test zielony.
**Co powiedzieć:** metody dopisują do przekazanego akumulatora. Właścicielem kolekcji jest `validate` - tworzy ją i decyduje o formacie.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m6/s18 1 2`

### Krok 3: wąski typ parametru

**W IDE:** klasa `Warnings` z `add` i `summary()`; Type Migration (⇧⌘F6) z `List<String>` na `Warnings`.
**Po:**

```java
Warnings warnings = new Warnings();
...
return warnings.summary();
```

**Uruchom:** test zielony.
**Co powiedzieć:** metoda pomocnicza może tylko dopisać - nie wyczyści ani nie przestawi cudzych ostrzeżeń.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m6/s18 2 3`

### Rozwiązanie i uzasadnienie

Każda reguła to metoda `check...(..., Warnings)`, format wyniku w jednym miejscu, akumulator o wąskim API.

### Pułapki

- Ogólna mutowalna mapa jako parametr zbierający - ukrywa, kto co zapisuje.
- Niejasne, czy metoda czyści akumulator, czy dopisuje.
- Wyjątek w połowie zbierania zostawia wynik częściowy - to część kontraktu, trzeba go ustalić.

### Pytanie do sali

Czy `Warnings` powinno pozwalać na poziomy (błąd kontra ostrzeżenie)? Jak to zmieni kontrakt?

## Scena s19. Visitor i macierz zmian - pozycje zamówienia

**Temat ze slajdów:** Visitor i macierz zmian
**Pakiet:** `pl.training.workshop.m6.s19_visitor` · **Test:** `scripts/warsztat.sh test m6/s19`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `ReceiptPrinter` ma trzy operacje na pozycjach zamówienia, każda z łańcuchem `instanceof` zakończonym wyjątkiem w runtime. Zamieniamy je na klasyczne Visitory, a potem pokazujemy alternatywę z Javy 25: zamkniętą hierarchię `sealed` i `switch` po typach bez `default`.

**Zasada:** Visitor przenosi operacje na strukturze do osobnych klas, a element wybiera właściwą metodę przez double dispatch. Macierz zmian mówi, kiedy go stosować: nowa operacja jest tania, nowy rodzaj elementu wymaga zmiany wszystkich Visitorów. W Javie 25 tę samą kontrolę kompilatora daje wyczerpujący `switch` po `sealed`, a Visitor ma sens, gdy hierarchii nie da się zamknąć.

**Efekt:** W kodzie nie ma `instanceof`, a pozycja bez obsługi to błąd kompilacji zamiast awarii na kasie. Obie formy są poprawne - wybór zależy od tego, czy częściej dochodzą operacje, czy rodzaje pozycji.

### Co widzimy

Pozycje zamówienia: bilet (VAT 8%), produkt baru (VAT 23%), voucher (pomniejsza kwotę, bez VAT). `ReceiptPrinter` ma trzy operacje - linia paragonu, kwota, VAT - każda z łańcuchem `instanceof` zakończonym wyjątkiem w runtime. Nowy rodzaj pozycji kompiluje się bez błędu i wybucha na kasie.

```java
if (item instanceof TicketItem ticket) {
    return vatOf(ticket.price(), 8);
} else if (item instanceof SnackItem snack) {
    return vatOf(snack.price(), 23);
} else if (item instanceof VoucherItem) {
    return Money.ZERO;
}
throw new IllegalArgumentException("unknown item: " + item);
```

### Krok 1: accept + pierwszy Visitor

**W IDE:** interfejs `OrderItemVisitor<R>` z `visitTicket/visitSnack/visitVoucher`, metoda `accept` w `OrderItem` i rekordach. Operację "linia paragonu" przenieś do `ReceiptLineVisitor`.
**Po:**

```java
text.append(item.accept(lines)).append('\n');
```

**Uruchom:** `scripts/warsztat.sh test m6/s19` - zielone.
**Co powiedzieć:** double dispatch - element wybiera metodę odwiedzającego. Brak metody dla typu to błąd kompilacji.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m6/s19 0 1`

### Krok 2: pozostałe operacje jako Visitory

**W IDE:** `AmountVisitor` i `VatVisitor`, usuń łańcuchy `instanceof`.
**Po:**

```java
total = total.plus(item.accept(amounts));
vat = vat.plus(item.accept(vats));
```

**Uruchom:** test zielony; `S19SolutionTest` dodaje operację "grupa VAT" jako nowy Visitor.
**Co powiedzieć:** macierz zmian: nowa operacja - nowa klasa, tanio; nowy rodzaj pozycji - zmiana interfejsu i **wszystkich** Visitorów.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m6/s19 1 2`

### Krok 3: alternatywa Java 25 - sealed + switch

**W IDE:** `sealed interface OrderItem permits ...`, usuń `accept` i Visitory, operacje jako `switch` po typach bez `default` (z record patterns w linii paragonu).
**Po:**

```java
return switch (item) {
    case TicketItem ticket -> vatOf(ticket.price(), 8);
    case SnackItem snack -> vatOf(snack.price(), 23);
    case VoucherItem voucher -> Money.ZERO;
};
```

**Uruchom:** test zielony.
**Co powiedzieć:** ta sama kontrola kompilatora co w Visitorze, bez ceremonii `accept/visit`. Visitor ma sens, gdy hierarchia nie może być `sealed` (inny moduł, biblioteka) albo gdy odwiedzający ma stan i logikę przejścia struktury.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m6/s19 2 3`

### Rozwiązanie i uzasadnienie

Dwie poprawne formy docelowe: klasyczny Visitor (`step2`) i wyczerpujący `switch` po zamkniętej hierarchii (`step3`). Macierz zmian:

| Częsta zmiana | Naturalniejszy kierunek |
| --- | --- |
| nowe operacje, stabilne rodzaje pozycji | Visitor albo switch po sealed |
| nowe rodzaje pozycji, stabilne operacje | metody polimorficzne w pozycjach |
| jedna prosta akumulacja | Collecting Parameter (scena s18) |

### Pułapki

- `default` w `switch` po sealed - wyłącza kontrolę kompilatora dla nowych typów.
- VAT zaokrąglany per pozycja kontra od sumy - różne wyniki, trzeba zachować sposób ze start.
- Visitor dla hierarchii, do której ciągle dochodzą typy - każdy nowy typ dotyka wszystkich Visitorów.

### Pytanie do sali

Dochodzi pozycja "karta podarunkowa" i operacja "eksport do księgowości". Która forma przyjmie te zmiany taniej?

## Scena s20. Mapa decyzji - jedna tabela, dwie struktury docelowe

**Temat ze slajdów:** Najpierw rodzaj zmienności; Mapa decyzji (1/2 i 2/2); Lista kontrolna
**Pakiet:** `pl.training.workshop.m6.s20_decisionmap` · **Test:** `scripts/warsztat.sh test m6/s20`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `ShowPricing` ma tabelę cen 3x3 wpisaną w zagnieżdżone warunki, w których splecione są format i reguła dnia. Najpierw rozplatamy dwie osie, a potem z tego samego punktu budujemy dwie alternatywy: Strategy dla reguły dnia albo typ formatu z zachowaniem.

**Zasada:** Wzorzec wybieramy według rodzaju zmienności, a nie kształtu kodu - ten sam `switch` może sygnalizować różne problemy. Pytamy, która oś zmienia się częściej, i tam budujemy punkt rozszerzenia. Gdy tego nie wiemy, zostajemy przy prostej strukturze bez nowych typów.

**Efekt:** Obie ścieżki są behawioralnie równoważne, a różnią się kosztem przyszłej zmiany: A tanio przyjmuje nowe akcje dniowe, B nowe formaty. Reguła zależna od obu osi naraz łamie założenie niezależności i wtedy tabela może być lepsza niż wzorzec.

### Co widzimy

`ShowPricing.price(day, format)` ma tabelę cen 3x3 wpisaną w zagnieżdżone warunki. Dwie osie zmienności - format i reguła dnia ("tani wtorek" -30%, weekend +2.00) - są splecione.

```java
if (format.equals("2D")) {
    return Money.of(day == DayOfWeek.TUESDAY ? "17.50" : weekend ? "27.00" : "25.00");
} else if (format.equals("3D")) {
    return Money.of(day == DayOfWeek.TUESDAY ? "22.40" : weekend ? "34.00" : "32.00");
} ...
```

Ta scena ma **dwie ścieżki**: krok 1 jest wspólny, krok 2 to ścieżka A (Strategy dla reguły dnia), krok 3 to ścieżka B (typ formatu z zachowaniem) - budowana od kroku 1, nie od kroku 2. Test równoważności sprawdza pełną tabelę dla obu ścieżek.

### Krok 1: rozplecenie osi (wspólny)

**W IDE:** zauważ, że tabela to dwie niezależne reguły. ⌥⌘M `basePrice(format)` ze `switch` po formacie i `adjustForDay(day, base)` ze `switch` po dniu. `price` = złożenie.
**Po:**

```java
public Money price(DayOfWeek day, String format) {
    return adjustForDay(day, basePrice(format));
}
```

**Uruchom:** `scripts/warsztat.sh test m6/s20` - zielone (9 cen i nieznany format).
**Co powiedzieć:** to jest moment decyzji. Ten sam `switch` może sygnalizować różne problemy - pytamy, **która oś zmienia się częściej**.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m6/s20 0 1`

### Krok 2: ścieżka A - zmienia się reguła dnia (Strategy)

**W IDE:** interfejs `DayPolicy.apply(base)`, stałe `CHEAP_TUESDAY`, `WEEKEND`, `REGULAR` i kalendarz `DayPolicies.standard(day)`. `ShowPricing` dostaje kalendarz w konstruktorze (domyślnie standardowy). Format zostaje prostym `switch`.
**Po:**

```java
return calendar.apply(day).apply(basePrice(format));
```

**Uruchom:** test zielony; `pathAMakesANewDayCampaignCheap` dodaje "środę seniora" bez dotykania formatów.
**Co powiedzieć:** wybierz tę ścieżkę, gdy marketing co miesiąc zmienia akcje dniowe, a formaty są stabilne. Zestaw polityk jest otwarty i wstrzykiwany.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m6/s20 1 2`

### Krok 3: ścieżka B - zmienia się zestaw formatów (typ z zachowaniem)

**W IDE:** pokaż alternatywę od kroku 1: `enum Format` z ceną bazową i metodą `priceOn(day)`, `Format.of(code)` z tym samym wyjątkiem. `ShowPricing` deleguje. Reguła dnia zostaje zwykłym `switch` w typie.
**Po:**

```java
public Money price(DayOfWeek day, String format) {
    return Format.of(format).priceOn(day);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** wybierz tę ścieżkę, gdy dochodzą formaty (4DX, ScreenX) z własnymi wyjątkami od reguł, a kalendarz jest stabilny. Porównaj `scripts/warsztat.sh diff m6/s20 1 3` z `diff m6/s20 1 2`.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m6/s20 1 3` (alternatywa dla kroku 2; `diff 2 3` pokazuje zamianę ścieżki)

### Rozwiązanie i uzasadnienie

Kryterium wyboru: **rodzaj zmienności, nie kształt kodu**.

| Co zmienia się często | Struktura | Co jest tanie |
| --- | --- | --- |
| reguły dnia, kampanie, kalendarz | Strategy (ścieżka A) | nowa polityka, wymiana kalendarza w teście |
| formaty i ich wyjątki | typ z zachowaniem (ścieżka B) | nowy format w jednym miejscu |
| nic, tabela stabilna | krok 1 wystarczy | czytelność bez nowych typów |

Obie ścieżki są równoważne behawioralnie - test to potwierdza. Różnią się kosztem **przyszłej** zmiany. Gdy nie wiemy, która oś będzie się zmieniać, zostajemy na kroku 1.

### Pułapki

- Wybór wzorca "bo ładniej" bez historii zmian (git log, backlog) - zgadujemy przyszłość.
- Obie ścieżki naraz (Strategy dnia w typie formatu) - podwójna ceremonia dla dwóch stabilnych osi.
- Reguła zależna od obu osi naraz ("IMAX we wtorek bez zniżki") - łamie założenie niezależności z kroku 1; wtedy tabela może być lepsza niż wzorzec.

### Pytanie do sali

Biznes zapowiada "IMAX we wtorek bez zniżki". Która ścieżka przyjmie to łatwiej i dlaczego?

## Proponowana kolejność pokazu

**Ścieżka krótka (~75 min)** - po jednej scenie z każdej grupy slajdów, z naciskiem na kontrakt:

1. s20 mapa decyzji (12 min) - ustawia pytanie "jaki rodzaj zmienności?" na cały moduł.
2. s01 Strategy (12 min) - pułapka momentu wyboru.
3. s08 State (15 min) - tabela przejść przed hierarchią, kolejność efektów.
4. s07 Decorator (12 min) - kolejność jest kontraktem, granice przezroczystości.
5. s09 Observer (12 min, kroki 1-2, krok 3 omówić z `diff`) - fail-fast i rozszerzenie kontra refaktoryzacja.
6. s10 Implicit Tree (12 min) - test różnicowy i mapper.

**Ścieżka pełna (~4 h z przerwami)** - kolejność zgodna z agendą slajdów:

1. Strategy, polimorfizm, typ: s20 (wprowadzenie), s01, s02, s03.
2. Builder, Factory, Decorator: s04, s05, s06, s07.
3. State, Observer, Composite: s08, s09, s10.
4. Adapter, Command, Template Method: s14, s15, s16.
5. Jeden/wiele, Singleton: s12, s11, s17.
6. Collecting Parameter, Visitor, Extract Composite: s18, s19, s13.
7. Podsumowanie: wróć do s20 i poproś salę o przypisanie każdej sceny do wiersza mapy decyzji.

Sceny s11 i s13 (po 2 kroki) dobrze sprawdzają się jako samodzielna praca uczestników w parach, a s04 i s05 jako jeden blok "Factory" z porównaniem obu refaktoryzacji.
