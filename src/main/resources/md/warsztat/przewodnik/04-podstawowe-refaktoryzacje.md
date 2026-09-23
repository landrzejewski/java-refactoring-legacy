# Moduł 4. Podstawowe refaktoryzacje - warsztat CineLegacy: przewodnik prowadzącego

Trzynaście krótkich scen w domenie kina pokazuje na żywo każdą technikę z modułu 4: od testu charakterystyki, przez Rename, Extract i Inline, Move Method i Move Field, Extract Class, aż po hermetyzację pól, kolekcji i warunków. Każda scena ma kod wyjściowy (`start`), gotowe snapshoty po każdym kroku (`stepN`) i test, który po każdym ruchu w IDE ma być zielony. Tam, gdzie tematem jest pułapka semantyki, test ją dokumentuje: pokazuje, co by się zepsuło, i dlaczego bezpieczny krok wygląda inaczej.

Sceny nie powtarzają studium "generator oferty wynajmu" z zadań modułu 4 (`pl.training.module4`). Tamto studium uczestnicy robią sami, a sceny poniżej służą do pokazu.

## Jak prowadzić pokaz

```bash
scripts/warsztat.sh list m4            # sceny modułu i ich kroki
scripts/warsztat.sh test m4/s05        # testy jednej sceny
scripts/warsztat.sh test m4            # wszystkie sceny modułu (230 testów)
scripts/warsztat.sh diff m4/s05 2 3    # co zmienia krok 3 względem kroku 2 (0 = start)
scripts/warsztat.sh jump m4/s05 2      # skopiuj step2 do start, gdy brakuje czasu
scripts/warsztat.sh reset m4/s05       # przywróć start po pokazie
```

- Zasada: **jeden krok - jeden test - jedno zdanie komentarza.** Ruch w IDE, ⌃R na teście sceny, zdanie z sekcji "Co powiedzieć".
- Pracujesz zawsze w pakiecie `start`. Snapshoty `stepN` są po to, żeby pokazać `diff` albo przeskoczyć (`jump`), gdy coś się rozjedzie.
- W scenach o pułapkach (s01, s02, s05, s06, s07) warto najpierw **zrobić naiwny ruch na żywo**, pokazać czerwony test, cofnąć (⌘Z) i dopiero wtedy wykonać krok z przewodnika.
- Sceny s10 i s11 zawierają kroki, które **świadomie zmieniają zachowanie**. Test ma dla nich osobne oczekiwania. Powiedz to na głos: to osobny commit, a nie refaktoryzacja.

## Mapa scen

| Scena | Temat ze slajdów | Kroki | Pakiet | Czas |
|---|---|---|---|---|
| s00 | Test charakterystyki przed pierwszą zmianą; Test równoważności etapów | 2 | `m4.s00_characterization` | ~15 min |
| s01 | Rename - cel i mechanika (nazwy poza Javą) | 3 | `m4.s01_rename` | ~12 min |
| s02 | Extract Variable i moment ewaluacji | 3 | `m4.s02_extractvariable` | ~8 min |
| s03 | Extract Constant i `final`; Replace Magic Numbers | 3 | `m4.s03_magicnumbers` | ~10 min |
| s04 | Extract Method - przepływ danych, mechanika i trudne przypadki | 3 | `m4.s04_extractmethod` | ~15 min |
| s05 | Inline Variable i typ docelowy (liczba i moment ewaluacji) | 3 | `m4.s05_inlinevariable` | ~12 min |
| s06 | Inline Method i jego ryzyka | 2 | `m4.s06_inlinemethod` | ~8 min |
| s07 | Move Method - wybór właściciela, krok po kroku i ryzyka | 2 | `m4.s07_movemethod` | ~10 min |
| s08 | Move Field krok po kroku i ryzyka | 3 | `m4.s08_movefield` | ~10 min |
| s09 | Extract Class - cel, mechanika i zły wynik | 3 | `m4.s09_extractclass` | ~15 min |
| s10 | Encapsulate Field | 3 | `m4.s10_encapsulatefield` | ~10 min |
| s11 | Encapsulate Collection; Trzy różne kontrakty kolekcji | 3 | `m4.s11_encapsulatecollection` | ~12 min |
| s12 | Encapsulate Conditional | 3 | `m4.s12_encapsulateconditional` | ~8 min |

## Scena s00. Test charakterystyki przed pierwszą zmianą

**Temat ze slajdów:** Test charakterystyki przed pierwszą zmianą; Test równoważności etapów; Co ma pozostać niezmienione
**Pakiet:** `pl.training.workshop.m4.s00_characterization` · **Test:** `scripts/warsztat.sh test m4/s00`
**Czas:** ~15 min

### Co widzimy

`BookingConfirmation.confirm` drukuje potwierdzenie rezerwacji. Nie ma żadnego testu, a dokument czytają klienci i infolinia. Zanim cokolwiek zmienimy, zapisujemy, co kod **robi**. Na drodze stoją dwie rzeczy: bieżący czas w ostatniej linii i `String.format` bez `Locale`.

```java
if (b.ticketTypes().size() > 10) {
    sum = sum * 0.9;
}
...
+ "Do zaplaty: " + String.format("%.2f", sum + fee) + "\n"
+ "Wygenerowano: " + LocalDateTime.now().withNano(0) + "\n";
```

W tej scenie najważniejszy jest test `S00CharacterizationTest`. Powstaje w trzech ruchach (A, B, C), a kod `start/step1/step2` tylko za nim nadąża.

**Ruch A (na `start`, przed krokiem 1):** napisz `assertEquals("", new BookingConfirmation().confirm(booking))`, uruchom i skopiuj rzeczywisty wynik z komunikatu błędu do oczekiwań. Test dalej jest czerwony, bo zmienia się sekunda. Dodaj "scrubber", który maskuje linię z czasem (`replaceAll("Wygenerowano: .*\n", "Wygenerowano: <czas>\n")`), i ustaw `Locale` jawnie na czas wywołania (`inLocale(pl-PL, ...)`). Teraz test jest zielony i deterministyczny. Dopisz przypadki na obu bokach progów: 10 i 11 biletów, rano i wieczorem, kasa i online.

W trakcie wychodzą dwa znaleziska, które test zapisuje, a nie poprawia:

- rabat grupowy działa od **11** biletów (`> 10`), choć regulamin mówi "10+" - przypadek nazwany `ZASTANE: ...`,
- kwoty zależą od `Locale` serwera: na `pl-PL` jest `74,00`, na `en-US` `74.00` (test `foundDuringCharacterizationAmountsDependOnServerLocale`).

### Krok 1: Parameterize Constructor - wstrzyknięty Clock

**W IDE:** w `confirm` zamień `LocalDateTime.now()` na `LocalDateTime.now(Clock.systemDefaultZone())`, zaznacz `Clock.systemDefaultZone()` i wykonaj Extract Field (⌥⌘F, inicjalizacja w konstruktorze, nazwa `clock`). Potem w konstruktorze na tym samym wyrażeniu Extract Parameter (⌥⌘P) z opcją delegowania przez przeciążenie ("Delegate via overloading method"), żeby został konstruktor bezargumentowy delegujący do `this(Clock.systemDefaultZone())`.
**Po:**

```java
public BookingConfirmation() {
    this(Clock.systemDefaultZone());
}

public BookingConfirmation(Clock clock) {
    this.clock = clock;
}
```

**Uruchom:** `scripts/warsztat.sh test m4/s00` - 15 testów zielonych, w tym `fromStep1TheWholeDocumentIsDeterministic` (z `Clock.fixed` porównujemy także linię z czasem, bez scrubbera).
**Co powiedzieć:** to minimalna zmiana, która robi szew dla testu. Stary konstruktor zostaje, więc produkcyjni klienci nie widzą różnicy. Scrubber był rusztowaniem i od tego kroku jest potrzebny tylko dla `start`.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m4/s00 0 1`

### Krok 2: pierwsza refaktoryzacja pod ochroną testu

**W IDE:** zaznacz ciało pętli od `double p;` do `p = p - 5.00; }`, Extract Method (⌥⌘M), nazwa `ticketPrice`. IntelliJ wykryje dwa wejścia (`b`, `t`) i jedno wyjście. W pętli zostaje `sum = sum + ticketPrice(b, t);`.
**Po:**

```java
for (String t : b.ticketTypes()) {
    sum = sum + ticketPrice(b, t);
}
```

**Uruchom:** test zielony - ten sam dokument, znak w znak.
**Co powiedzieć:** kusi, żeby przy okazji zamienić `double` na `BigDecimal`, dodać `Locale.ROOT` i poprawić próg grupy. Każda z tych rzeczy zmienia wydruk, więc to trzy osobne decyzje z wymaganiem, a nie refaktoryzacja.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m4/s00 1 2`

### Rozwiązanie i uzasadnienie

Test charakterystyki porównuje **pełny dokument**, a nie wybrane liczby, dla przypadków po obu stronach progów. Ten sam zestaw oczekiwań uruchamiamy na `start`, `step1` i `step2`, co jest testem równoważności etapów. Oczekiwania zapisaliśmy raz, zatwierdzone przez człowieka, a nie liczone na nowo przez starą wersję kodu. Kod zmienia się minimalnie: szew na czas i jedna ekstrakcja.

### Pułapki

- Test, który wylicza oczekiwania tym samym algorytmem co kod ("drugi raz to samo"), niczego nie charakteryzuje.
- Test zależny od strefy czasowej, Locale albo bieżącej daty przejdzie na laptopie i padnie na CI. Tutaj `Locale` ustawiamy na czas wywołania i przywracamy w `finally`.
- "Poprawienie" znaleziska z charakterystyki w tym samym commicie co refaktoryzacja. Znalezisko zapisujemy (`ZASTANE: ...`), zgłaszamy i poprawiamy osobno.
- Brak sprawdzenia czułości testu. Zmień na chwilę `> 10` na `>= 10` w `start` i zobacz, że test padnie. Potem cofnij.

### Pytanie do sali

Znaleźliście w charakterystyce ewidentny błąd (rabat od 11 biletów). Kto decyduje, czy go poprawić, i jak powinien wyglądać commit z poprawką?

## Scena s01. Rename - nazwy, które żyją poza Javą

**Temat ze slajdów:** Rename - cel i mechanika; Co ma pozostać niezmienione (refleksja, konfiguracja)
**Pakiet:** `pl.training.workshop.m4.s01_rename` · **Test:** `scripts/warsztat.sh test m4/s01`
**Czas:** ~12 min

### Co widzimy

`SalesReport.calc2(List<Sale> s, boolean flag)` składa CSV dla dystrybutora: `m`, `x`, `l`, `b`, a wiersz to `Line(String t, int n, BigDecimal d)`. Dwie nazwy żyją poza Javą:

```java
// ReportJob - w produkcji plik report.properties na serwerze
static final String CONFIG = """
        report.method=calc2
        report.onlineOnly=true
        """;
...
Method method = SalesReport.class.getMethod(config.getProperty("report.method"), ...);

// SalesReport - nagłówek CSV liczony refleksją z nazw komponentów rekordu
Arrays.stream(Line.class.getRecordComponents()).map(RecordComponent::getName)...
```

### Krok 1: Rename zmiennych lokalnych i parametrów

**W IDE:** kursor na `s`, ⇧F6, `sales`. Tak samo `flag` -> `onlineOnly`, `m` -> `linesByTitle`, `x` -> `sale`, `l` -> `previous` / `line`, `b` -> `csv`, `v` -> `values`, `c` -> `component`.
**Po:**

```java
public String calc2(List<Sale> sales, boolean onlineOnly) {
    Map<String, Line> linesByTitle = new TreeMap<>();
    for (Sale sale : sales) {
        if (onlineOnly && !sale.online()) {
```

**Uruchom:** `scripts/warsztat.sh test m4/s01` - 14 testów zielonych.
**Co powiedzieć:** zasięg lokalny, zero użyć poza metodą, więc IDE robi to w stu procentach bezpiecznie. Od tego zaczynamy każde porządkowanie, bo tanie nazwy ułatwiają następne kroki.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m4/s01 0 1`

### Krok 2: Rename metody z delegatem dla starej nazwy

**W IDE:** najpierw na żywo naiwnie: ⇧F6 na `calc2`, nazwa `revenueCsv`, bez zaznaczania "Search in comments and strings". Uruchom test: `everyStepRunsTheConfiguredJob` jest czerwony (`NoSuchMethodException: calc2`). IDE nie wie, że tekst w konfiguracji to nazwa metody. Cofnij (⌘Z). Teraz poprawnie: skopiuj metodę, zmień nazwę kopii na `revenueCsv`, a ciało `calc2` zamień na delegowanie i oznacz `@Deprecated`.
**Po:**

```java
/** Stara nazwa z konfiguracji report.properties (report.method=calc2). */
@Deprecated
public String calc2(List<Sale> sales, boolean onlineOnly) {
    return revenueCsv(sales, onlineOnly);
}

public String revenueCsv(List<Sale> sales, boolean onlineOnly) {
```

**Uruchom:** test zielony, zadanie z konfiguracji działa.
**Co powiedzieć:** konfiguracja leży na serwerach, poza naszym repozytorium, więc nie zmienimy jej w tym samym commicie. Stara nazwa zostaje jako przestarzały delegat, dopóki ktoś nie zmieni konfiguracji wszędzie. To jest strategia migracji ze slajdu.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m4/s01 1 2`

### Krok 3: Rename komponentów rekordu po odcięciu kontraktu CSV

**W IDE:** najpierw naiwnie: ⇧F6 na `t` w `Line`, `title`. Test pokaże nagłówek `title;n;d` zamiast `t;n;d`: plik dla dystrybutora właśnie się zmienił. Cofnij. Teraz poprawnie: najpierw zamień refleksyjne `header()` i `row()` na jawny `CSV_HEADER = "t;n;d"` i jawne składanie wiersza (test zielony), dopiero potem ⇧F6 na `t`, `n`, `d` -> `title`, `tickets`, `revenue`.
**Po:**

```java
/** Nagłówek uzgodniony z dystrybutorem - kontrakt zewnętrzny, NIE nazwy pól w Javie. */
static final String CSV_HEADER = "t;n;d";

public record Line(String title, int tickets, BigDecimal revenue) {
}

private static String row(Line line) {
    return line.title() + ";" + line.tickets() + ";" + line.revenue();
}
```

**Uruchom:** test zielony, także `S01RenameLimitsTest.reflectionTurnsJavaNamesIntoTheCsvHeader`, który pokazuje, że refleksja na nowym rekordzie dałaby `title;tickets;revenue`.
**Co powiedzieć:** nazwa w Javie i nazwa w kontrakcie zewnętrznym to dwie różne rzeczy. Dopóki są sklejone refleksją, każdy Rename jest zmianą API.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m4/s01 2 3`

### Rozwiązanie i uzasadnienie

`step3`: wszystkie nazwy mówią o roli (`revenueCsv`, `onlineOnly`, `linesByTitle`, `Line.revenue`), a dwa kontrakty zewnętrzne (nazwa metody w konfiguracji i nagłówek CSV) są zachowane jawnie: przez delegat i przez stałą. Oba testy równoważności (wywołanie z Javy i zadanie z konfiguracji) są zielone na każdym etapie.

### Pułapki

- Poleganie na "Search in comments and strings" w IntelliJ. Czasem pomaga, ale nie widzi plików poza projektem ani nazw składanych dynamicznie (`"calc" + version`).
- Inne miejsca, w których nazwa jest kontraktem: JSON (Jackson), kolumny ORM, szablony, `Serializable`, nazwy beanów, logi parsowane przez monitoring.
- Usunięcie delegatu `calc2` "bo nikt go nie woła". Find Usages go nie znajdzie, bo wołają go tylko refleksja i konfiguracja.

### Pytanie do sali

Jak w waszym systemie sprawdzić, czy nazwa klasy albo metody nie jest użyta w konfiguracji, bazie danych lub innym repozytorium?

## Scena s02. Extract Variable dla złożonego wyrażenia ceny

**Temat ze slajdów:** Extract Variable i moment ewaluacji
**Pakiet:** `pl.training.workshop.m4.s02_extractvariable` · **Test:** `scripts/warsztat.sh test m4/s02`
**Czas:** ~8 min

### Co widzimy

Cała cena biletu to jedno wyrażenie z pięcioma ternary. Żeby odpowiedzieć na pytanie "skąd 32.00?", trzeba policzyć je w głowie. Uwaga na `row`: to `Integer`, a `null` oznacza wolną widownię.

```java
return (r.format() == 3 ? new BigDecimal("40.00")
                : r.format() == 2 ? new BigDecimal("32.00") : new BigDecimal("25.00"))
        .multiply(BigDecimal.valueOf(100 - (r.type().equals("S") ? 25
                : r.type().equals("E") ? 30 : r.type().equals("C") ? 40 : 0)))
        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
        .subtract(r.start().isBefore(LocalTime.NOON)
                ? new BigDecimal("5.00") : BigDecimal.ZERO)
        .add(r.row() != null && r.row() >= 10 ? new BigDecimal("10.00") : BigDecimal.ZERO)
        .add(r.format() == 2 && !r.ownGlasses() ? new BigDecimal("3.00") : BigDecimal.ZERO);
```

### Krok 1: Extract Variable dla ceny bazowej i zniżki

**W IDE:** zaznacz pierwszy nawias z ternary formatu, ⌥⌘V, `basePrice`. Potem zaznacz ternary typu biletu, ⌥⌘V, `discountPercent`. Na koniec zaznacz `basePrice.multiply(...).divide(...)`, ⌥⌘V, `discountedPrice`.
**Po:**

```java
BigDecimal basePrice = r.format() == 3 ? new BigDecimal("40.00")
        : r.format() == 2 ? new BigDecimal("32.00") : new BigDecimal("25.00");
int discountPercent = r.type().equals("S") ? 25
        : r.type().equals("E") ? 30 : r.type().equals("C") ? 40 : 0;
BigDecimal discountedPrice = basePrice
        .multiply(BigDecimal.valueOf(100 - discountPercent))
        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
```

**Uruchom:** `scripts/warsztat.sh test m4/s02` - 25 testów zielonych.
**Co powiedzieć:** zmienna nazywa pojęcie z cennika, a nie fragment składni. Wyrażenia są czyste, więc przesunięcie ich ewaluacji wcześniej niczego nie zmienia.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m4/s02 0 1`

### Krok 2: Extract Variable dla warunków

**W IDE:** zaznacz `r.start().isBefore(LocalTime.NOON)`, ⌥⌘V, `morning`. Dla VIP zaznacz **całe** `r.row() != null && r.row() >= 10`, ⌥⌘V, `vipSeat`. Potem `r.format() == 2 && !r.ownGlasses()` -> `needsGlasses`.
**Po:**

```java
boolean morning = r.start().isBefore(LocalTime.NOON);
boolean vipSeat = r.row() != null && r.row() >= 10;
boolean needsGlasses = r.format() == 2 && !r.ownGlasses();
```

**Uruchom:** test zielony, także przypadki "wolna widownia (row = null)".
**Co powiedzieć:** gdybyśmy wydzielili samo `r.row() >= 10`, zmienna policzyłaby się **przed** osłoną `!= null` i przy wolnej widowni poleciałby `NullPointerException` z unboxingu. Krótkie spięcie jest zachowaniem. Pokazuje to test `extractingTheComparisonWithoutTheNullGuardThrows`.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m4/s02 1 2`

### Krok 3: Extract Variable dla kwot

**W IDE:** zaznacz każde `warunek ? kwota : BigDecimal.ZERO`, ⌥⌘V: `morningReduction`, `vipSurcharge`, `glassesFee`.
**Po:**

```java
BigDecimal morningReduction = morning ? new BigDecimal("5.00") : BigDecimal.ZERO;
BigDecimal vipSurcharge = vipSeat ? new BigDecimal("10.00") : BigDecimal.ZERO;
BigDecimal glassesFee = needsGlasses ? new BigDecimal("3.00") : BigDecimal.ZERO;
return discountedPrice.subtract(morningReduction).add(vipSurcharge).add(glassesFee);
```

**Uruchom:** test zielony.
**Co powiedzieć:** ostatnia linia czyta się jak paragon. Następny naturalny krok to Replace Magic Numbers (scena s03) albo Extract Method dla `basePrice` (scena s04).
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m4/s02 2 3`

### Rozwiązanie i uzasadnienie

`step3/TicketPrice`: każda część ceny ma nazwę, kolejność operacji arytmetycznych i miejsce zaokrąglenia są takie same jak w `start`. Test obejmuje obie strony progów (11:59 i 12:00, rząd 9 i 10) oraz `row = null`.

### Pułapki

- Wydzielenie jednej strony `&&` / `||` przed osłoną (null, `instanceof`, `isEmpty()`).
- Scalenie dwóch identycznych wyrażeń w jedną zmienną, gdy wyrażenie ma efekt uboczny albo czyta czas. Wtedy zmienia się **liczba** ewaluacji (patrz s05).
- Nazwa opisująca składnię (`ternary1`, `tmp`) zamiast znaczenia.

### Pytanie do sali

Kiedy jedna zmienna `now = clock.instant()` zamiast dwóch wywołań zegara jest poprawką, a kiedy zmianą kontraktu?

## Scena s03. Stałe z nazwą zamiast magicznych liczb

**Temat ze slajdów:** Extract Constant i `final`; Replace Magic Numbers with Named Constants
**Pakiet:** `pl.training.workshop.m4.s03_magicnumbers` · **Test:** `scripts/warsztat.sh test m4/s03`
**Czas:** ~10 min

### Co widzimy

`OrderPricer.summary` liczy bilety, opłatę online i punkty lojalnościowe. Liczby: 25.00, 32.00, 40.00, 0.25, 0.30, 0.40, 5.00, 12, 10, 10.00, 10, 0.90, 2.00 i `BigDecimal.TEN`. Cztery "dziesiątki" oznaczają cztery różne decyzje biznesowe.

```java
if (t.row() >= 10) {                                   // od którego rzędu VIP
    p = p.add(new BigDecimal("10.00"));                // dopłata VIP
}
...
if (o.tickets().size() >= 10) {                        // próg grupy
    tickets = tickets.multiply(new BigDecimal("0.90"));
}
int points = tickets.divide(BigDecimal.TEN, 0, RoundingMode.DOWN).intValue(); // zł za punkt
```

### Krok 1: Extract Constant dla kwot z cennika

**W IDE:** zaznacz `new BigDecimal("25.00")`, ⌥⌘C, `BASE_PRICE_2D`. IntelliJ zapyta o zastąpienie wszystkich wystąpień, więc się zgódź, bo to ta sama wiedza. Tak samo `BASE_PRICE_3D`, `BASE_PRICE_IMAX`, `MORNING_REDUCTION`, `VIP_SURCHARGE`, `ONLINE_FEE_PER_TICKET`, `NO_FEE`.
**Po:**

```java
private static final BigDecimal BASE_PRICE_2D = new BigDecimal("25.00");
private static final BigDecimal MORNING_REDUCTION = new BigDecimal("5.00");
private static final BigDecimal VIP_SURCHARGE = new BigDecimal("10.00");
private static final BigDecimal ONLINE_FEE_PER_TICKET = new BigDecimal("2.00");
```

**Uruchom:** `scripts/warsztat.sh test m4/s03` - 17 testów zielonych.
**Co powiedzieć:** `BigDecimal` jest niezmienny, więc `static final` rzeczywiście jest stałą. Stała jest prywatna, bo ma jednego właściciela.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m4/s03 0 1`

### Krok 2: Replace Magic Numbers - progi reguł

**W IDE:** zaznacz `10` w `t.row() >= 10`, ⌥⌘C, `VIP_FROM_ROW`. **Odmów** zastąpienia wszystkich wystąpień, bo pozostałe dziesiątki to inna wiedza. Osobno: `GROUP_MIN_TICKETS`, `GROUP_PRICE_FACTOR`, `AMOUNT_PER_LOYALTY_POINT` (z `BigDecimal.TEN`), `MORNING_ENDS_AT_HOUR`.
**Po:**

```java
private static final int MORNING_ENDS_AT_HOUR = 12;
private static final int VIP_FROM_ROW = 10;
private static final int GROUP_MIN_TICKETS = 10;
private static final BigDecimal GROUP_PRICE_FACTOR = new BigDecimal("0.90");
private static final BigDecimal AMOUNT_PER_LOYALTY_POINT = BigDecimal.TEN;
```

**Uruchom:** test zielony.
**Co powiedzieć:** jeśli dyrekcja zmieni próg grupy na 8, rząd VIP nie może się przesunąć razem z nim. Nazwa opisuje rolę (`GROUP_MIN_TICKETS`), a nie wartość (`TEN`). Extract Constant to mechanika, a Replace Magic Number to powód.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m4/s03 1 2`

### Krok 3: Extract Constant dla stawek zniżek

**W IDE:** zaznacz `new BigDecimal("0.25")`, ⌥⌘C, `STUDENT_DISCOUNT`. Tak samo `SENIOR_DISCOUNT` i `CHILD_DISCOUNT`.
**Po:**

```java
if (t.type().equals("S")) {
    p = p.multiply(BigDecimal.ONE.subtract(STUDENT_DISCOUNT));
} else if (t.type().equals("E")) {
    p = p.multiply(BigDecimal.ONE.subtract(SENIOR_DISCOUNT));
```

**Uruchom:** test zielony.
**Co powiedzieć:** w metodzie nie ma już żadnej liczby bez nazwy. Następny ruch (już nie w tej scenie) to przeniesienie cennika do własnej klasy, bo stałe pokazują, że to osobna odpowiedzialność.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m4/s03 2 3`

### Rozwiązanie i uzasadnienie

`step3/OrderPricer`: czternaście prywatnych stałych, każda z nazwą roli. Trzy progi o wartości 10 mają trzy różne nazwy, bo zmieniają się z różnych powodów. Test obejmuje 9 i 10 biletów, 12:00 i rano, kasę i online.

### Pułapki

- `final` blokuje tylko przypisanie. `static final List` z `new ArrayList<>()` każdy może zmodyfikować. Test `finalDoesNotMakeACollectionConstant` pokazuje to na liście typów zniżkowych, a `List.of` rzuca `UnsupportedOperationException`.
- Publiczne `static final int` / `String` to stała czasu kompilacji. Kompilator wkleja wartość do klas klienta, więc po zmianie `VIP_FROM_ROW` klient bez rekompilacji widzi starą wartość. `static final BigDecimal` nie jest wklejany.
- Stała dla czegoś, co jest konfiguracją (ceny zmieniane bez wdrożenia). Wtedy miejsce jest w konfiguracji albo w bazie, a nie w kodzie.
- Jedna stała `TEN` dla czterech reguł.

### Pytanie do sali

Które z tych stałych w prawdziwym kinie powinny być konfiguracją, a nie kodem? Po czym to poznać?

## Scena s04. Extract Method - przepływ danych i wiele wyjść

**Temat ze slajdów:** Extract Method - cel i sygnały; Extract Method - analiza przepływu danych; Extract Method - mechanika i trudne przypadki
**Pakiet:** `pl.training.workshop.m4.s04_extractmethod` · **Test:** `scripts/warsztat.sh test m4/s04`
**Czas:** ~15 min

### Co widzimy

`TicketSummary.describe` liczy cenę bazową, sumę i liczbę miejsc VIP, a na końcu składa dokument. Komentarze `// cena bazowa`, `// suma i liczba miejsc VIP`, `// dokument` to naturalne granice metod. Pętla ma jednak **dwa wyjścia**: `subtotal` i `vipSeats`.

```java
BigDecimal subtotal = BigDecimal.ZERO;
int vipSeats = 0;
for (int row : order.rows()) {
    BigDecimal price = base;
    if (row >= 10) {
        price = price.add(new BigDecimal("10.00"));
        vipSeats++;
    }
    subtotal = subtotal.add(price);
}
```

### Krok 1: Extract Method dla bloku z jednym wyjściem

**W IDE:** zaznacz blok pod `// cena bazowa` (od `BigDecimal base;` do końca `if` z porankiem), ⌥⌘M, nazwa `basePrice`. IntelliJ sam wykryje jedno wejście (`order`) i jedno wyjście (`base`). Usuń zbędny komentarz.
**Po:**

```java
BigDecimal base = basePrice(order);
```

**Uruchom:** `scripts/warsztat.sh test m4/s04` - 16 testów zielonych.
**Co powiedzieć:** blok z jednym wyjściem to najprostszy przypadek, IDE robi to bezpiecznie. Komentarz stał się nazwą metody.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m4/s04 0 1`

### Krok 2: Split Loop, potem dwa razy Extract Method

**W IDE:** spróbuj ⌥⌘M na pętli. IntelliJ zgłosi, że fragment ma wiele wartości wyjściowych. To jest moment dydaktyczny. Ręcznie skopiuj pętlę (Split Loop) i z pierwszej kopii usuń `vipSeats`, a z drugiej `subtotal`. Uruchom test. Następnie na każdej pętli ⌥⌘M, nazwy `subtotal` i `vipSeats`.
**Po:**

```java
BigDecimal subtotal = subtotal(order, base);
int vipSeats = vipSeats(order);
```

**Uruchom:** test po Split Loop i po każdej ekstrakcji.
**Co powiedzieć:** zamiast zwracać parę (record z dwoma polami) rozdzielamy odpowiedzialności. Dwa przejścia po liście to koszt pomijalny wobec czytelności. Jeśli wydajność miałaby znaczenie, mierzymy, a nie zgadujemy.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m4/s04 1 2`

### Krok 3: Extract Method dla renderowania

**W IDE:** zaznacz blok `// dokument` i wykonaj ⌥⌘M, nazwa `render`. Parametry: `order`, `subtotal`, `vipSeats`. Potem Inline Variable (⌥⌘N) dla `base`.
**Po:**

```java
public String describe(Order order) {
    BigDecimal subtotal = subtotal(order, basePrice(order));
    int vipSeats = vipSeats(order);
    return render(order, subtotal, vipSeats);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** metoda publiczna czyta się jak spis treści. Obliczenia są oddzielone od prezentacji, więc przygotowaliśmy grunt pod Extract Class (scena s09).
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m4/s04 2 3`

### Rozwiązanie i uzasadnienie

`step3/TicketSummary`: cztery prywatne metody, każda z jednym wyjściem. Nie zmieniliśmy sygnatury publicznej ani formatu dokumentu, co potwierdza test równoważności na czterech przypadkach (w tym zamówienie puste i same miejsca VIP).

### Pułapki

- Ekstrakcja bloku, który modyfikuje zmienną lokalną używaną dalej, bez zwrócenia jej wartości. IDE tego pilnuje, ręczne kopiowanie nie.
- Zwracanie tablicy `Object[]` albo mutowalnego "holdera" tylko po to, żeby wyciągnąć dwa wyniki.
- Nazwa metody opisująca "jak" (`loopRows`) zamiast "co" (`vipSeats`).

### Pytanie do sali

Kiedy zamiast Split Loop lepiej zwrócić record z dwoma polami?

## Scena s05. Inline Variable - liczba i moment ewaluacji

**Temat ze slajdów:** Inline Variable i typ docelowy; Extract Variable i moment ewaluacji
**Pakiet:** `pl.training.workshop.m4.s05_inlinevariable` · **Test:** `scripts/warsztat.sh test m4/s05`
**Czas:** ~12 min

### Co widzimy

`TicketIssuer.issue` ma sześć zmiennych lokalnych. Wyglądają na zbędne, ale trzy z nich trzymają wynik, którego **nie wolno** obliczyć ponownie:

```java
int number = nextNumber();          // efekt uboczny: każde wywołanie zużywa numer
Instant issuedAt = clock.instant(); // odczyt czasu: każde wywołanie to inna chwila
String code = screeningCode + "-" + number;
double price = basePrice(format);   // int -> double: typ zmiennej wybiera przeciążenie money
String label = "Bilet " + code + ", cena " + money(price)
        + ", oplata " + money(ONLINE_FEE_GROSZE);
Instant holdUntil = issuedAt.plus(HOLD);
return new Ticket(code, label, issuedAt, holdUntil);
```

`money(double zloty)` i `money(int grosze)` to dwa przeciążenia o tej samej nazwie i różnych jednostkach.

**Pokaz pułapki (przed krokiem 1):** ⌥⌘N na `number`, na `issuedAt` i na `price`. Kod się kompiluje, IDE nie protestuje, bo nie wie, że `nextNumber()` ma efekt uboczny, a `clock.instant()` zależy od czasu. Test równoważności jest czerwony: etykieta ma numer 2, a kod numer 1, rezerwacja trzyma o sekundę za długo (fake zegar tyka przy każdym odczycie), a cena 40 zł to "0.40". Cofnij (⌘Z). Te same trzy objawy dokumentuje stale `S05InlineTrapTest`.

### Krok 1: Inline Variable dla czystego wyrażenia użytego raz

**W IDE:** kursor na `label`, ⌥⌘N.
**Po:**

```java
return new Ticket(code,
        "Bilet " + code + ", cena " + money(price)
                + ", oplata " + money(ONLINE_FEE_GROSZE),
        issuedAt, holdUntil);
```

**Uruchom:** `scripts/warsztat.sh test m4/s05` - 11 testów zielonych.
**Co powiedzieć:** wyrażenie jest czyste i użyte raz, a nazwa parametru rekordu (`label`) mówi to samo co zmienna. To podręcznikowy bezpieczny inline.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m4/s05 0 1`

### Krok 2: Inline Variable, który czyta zmienną, a nie zegar

**W IDE:** kursor na `holdUntil`, ⌥⌘N.
**Po:**

```java
return new Ticket(code, ..., issuedAt, issuedAt.plus(HOLD));
```

**Uruchom:** test zielony.
**Co powiedzieć:** ten inline jest bezpieczny, bo inicjalizator czyta **zmienną** `issuedAt`, która zamroziła jeden odczyt zegara. Gdybyśmy wcześniej zrobili inline `issuedAt`, ten sam ruch dałby dwa różne odczyty czasu.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m4/s05 1 2`

### Krok 3: najpierw Rename przeciążenia, potem Inline `price`

**W IDE:** ⇧F6 na `money(int grosze)`, nazwa `moneyFromGrosze`. Teraz ⌥⌘N na `price`: jedyny kandydat to `money(double)`, a `int` zostanie poszerzony do `double` tak jak wcześniej w zmiennej.
**Po:**

```java
"Bilet " + code + ", cena " + money(basePrice(format))
        + ", oplata " + moneyFromGrosze(ONLINE_FEE_GROSZE)
```

**Uruchom:** test zielony.
**Co powiedzieć:** typ zmiennej lokalnej to typ docelowy, który wybiera przeciążenie. Najpierw usuwamy niejednoznaczność nazwą, a dopiero potem inline. `number` i `issuedAt` zostają celowo i warto dopisać im komentarz albo nic nie ruszać.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m4/s05 2 3`

### Rozwiązanie i uzasadnienie

`step3/TicketIssuer`: zostały trzy zmienne (`number`, `issuedAt`, `code`), bo każda chroni pojedynczą ewaluację albo nazywa pojęcie. Zniknęły te, które nic nie wnosiły. Test równoważności używa ręcznego `TickingClock` (każdy odczyt +1 s), więc wykryłby każde podwójne czytanie zegara.

### Pułapki

- Inline zmiennej z inicjalizatorem, który ma efekt uboczny (`nextSequence()`, `iterator.next()`, `queue.poll()`), zmienia **liczbę** wywołań.
- Inline odczytu czasu, losowości albo I/O zmienia **moment** i liczbę odczytów, a test z `Clock.fixed` tego nie wykryje. Dlatego zegar w teście tyka.
- Inline zmiennej o jawnym typie (`double`, `long`, `Integer`) może wybrać inne przeciążenie albo dodać/usunąć unboxing.
- Lambda wymaga typu docelowego: `var f = () -> ...` się nie skompiluje, a inline zmiennej z lambdą do `Object` też nie.

### Pytanie do sali

Jak w teście wykryć, że kod czyta zegar dwa razy, skoro `Clock.fixed` zawsze zwraca tę samą chwilę?

## Scena s06. Inline Method i nadpisanie w podklasie

**Temat ze slajdów:** Inline Method i jego ryzyka
**Pakiet:** `pl.training.workshop.m4.s06_inlinemethod` · **Test:** `scripts/warsztat.sh test m4/s06`
**Czas:** ~8 min

### Co widzimy

`TicketPricing` ma trzy małe metody, które "wyglądają na trywialne": dwa prywatne pośredniki (`base`, `addFee`) i chroniony hak `bookingFee()`, nadpisany w `OnlineTicketPricing` (+2.00).

```java
public BigDecimal total(Ticket ticket) {
    return addFee(price(ticket));
}

protected BigDecimal bookingFee() {        // kasa: 0.00, OnlineTicketPricing: 2.00
    return new BigDecimal("0.00");
}

private BigDecimal base(Ticket ticket) {
    return basePrice(ticket.format());
}
```

### Krok 1: Inline Method trywialnego delegata

**W IDE:** kursor na `base`, ⌥⌘N, "Inline all and remove the method".
**Po:**

```java
BigDecimal price = basePrice(ticket.format());
```

**Uruchom:** `scripts/warsztat.sh test m4/s06` - 19 testów zielonych (kasa i internet).
**Co powiedzieć:** prywatny, niepolimorficzny delegat z jednym wywołaniem to najbezpieczniejszy możliwy inline. Nazwa `base` nic nie dodawała do `basePrice`.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m4/s06 0 1`

### Krok 2: Inline Method wywołującego hak, a nie samego haka

**W IDE:** najpierw pokaż pułapkę: kursor na `bookingFee()` w klasie bazowej, ⌥⌘N. IntelliJ ostrzeże, że metoda jest nadpisywana (albo odmówi). Ręczne wklejenie ciała dałoby `price(ticket).add(new BigDecimal("0.00"))` i internet sprzedawałby bez opłaty. Test `S06InlineOverriddenMethodTrapTest` pokazuje dokładnie ten wynik: `40.00` zamiast `42.00`. Zamknij dialog. Teraz poprawnie: kursor na `addFee`, ⌥⌘N.
**Po:**

```java
public BigDecimal total(Ticket ticket) {
    return price(ticket).add(bookingFee());
}
```

**Uruchom:** test zielony, `onlineTotalsStayTheSame` nadal daje 42.00.
**Co powiedzieć:** wklejamy **wywołanie** metody polimorficznej, więc dynamiczna dyspozycja zostaje. Wklejenie **ciała** metody nadpisywanej zabija nadpisanie po cichu, a podklasa kompiluje się dalej, z poprawnym `@Override`.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m4/s06 1 2`

### Rozwiązanie i uzasadnienie

`step2/TicketPricing`: bez pośredników, `total` wprost pokazuje "cena + opłata", a `bookingFee()` zostaje hakiem dla podklasy. Test równoważności uruchamia **obie** klasy, bo zachowanie podklasy jest częścią kontraktu klasy bazowej.

### Pułapki

- Inline metody nadpisywanej albo implementującej interfejs usuwa dynamiczną dyspozycję.
- Inline metody `synchronized` usuwa blokadę. Inline metody wywoływanej przez proxy (`@Transactional`, `@PreAuthorize`) usuwa transakcję albo autoryzację.
- Parametr użyty w ciele dwa razy, a w wywołaniu argument z efektem ubocznym: po inline efekt wykona się dwa razy (IntelliJ zwykle wprowadza wtedy zmienną, a ręczne wklejanie tego nie zrobi).
- Liczba linii nie przesądza. Metoda o nazwie z domeny (`qualifiesForGroupDiscount`) zostaje, nawet jeśli ma jedną linię.

### Pytanie do sali

Jak test klasy bazowej ma "wiedzieć" o podklasach, które jeszcze nie istnieją albo są w innym module?

## Scena s07. Move Method - Feature Envy i pułapka przeciążenia

**Temat ze slajdów:** Move Method i Move Field - wybór właściciela; Move Method krok po kroku i ryzyka
**Pakiet:** `pl.training.workshop.m4.s07_movemethod` · **Test:** `scripts/warsztat.sh test m4/s07`
**Czas:** ~10 min

### Co widzimy

`BookingPrinter` ma dwie metody, które używają **wyłącznie** danych `Screening`. `print` koordynuje `Booking` i `Screening`, więc zostaje.

```java
private String screeningLine(Screening s) {
    String format = switch (s.format()) { ... };
    return s.title() + " (" + format + "), sala " + s.hall() + ", "
            + s.start().toLocalDate() + " " + s.start().toLocalTime();
}

private List<Integer> remainingSeats(Screening s, Integer seat) {
    List<Integer> free = new ArrayList<>(s.freeSeats());
    free.remove(seat);          // Integer -> remove(Object): usuń MIEJSCE o tym numerze
    return free;
}
```

### Krok 1: Move Method - opis seansu

**W IDE:** kursor na `screeningLine`, F6, wybierz cel: parametr `s` (typ `Screening`). IntelliJ przeniesie metodę do rekordu i zamieni `s.title()` na `title`. Potem ⇧F6 na przeniesionej metodzie: `headline`.
**Po:**

```java
// Screening
public String headline() {
    String formatName = switch (format) { ... };
    return title + " (" + formatName + "), sala " + hall + ", " + ...;
}
// BookingPrinter
+ booking.screening().headline() + "\n"
```

**Uruchom:** `scripts/warsztat.sh test m4/s07` - 11 testów zielonych.
**Co powiedzieć:** metoda poszła tam, gdzie są jej dane. Wybór właściciela jest decyzją, a nie automatem: `print` też sięga do `Screening`, ale jej zadaniem jest złożenie wydruku z kilku obiektów.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m4/s07 0 1`

### Krok 2: Move Method - wolne miejsca, bez zmiany typu parametru

**W IDE:** najpierw pokaż pułapkę: przenieś `remainingSeats` (F6, cel `s`), a potem "posprzątaj" parametr `Integer seat` na `int seat`, bo w `Screening` wszystkie numery są prymitywami. Kompiluje się. Test jest czerwony: przy miejscu 1 zostało `[1, 3, 4, 5]`, a przy miejscu 8 poleciał `IndexOutOfBoundsException`, bo teraz wołane jest `remove(int index)`. Cofnij. Poprawnie: F6 bez zmiany typu, ⇧F6 na `freeSeatsWithout`, a w Javadoc zapisz, dlaczego parametr to `Integer`.
**Po:**

```java
/**
 * Wolne miejsca bez wskazanego. Parametr MUSI zostać {@code Integer}: z {@code int}
 * wywołanie {@code remove} wybrałoby {@code remove(int index)} zamiast {@code remove(Object)}.
 */
public List<Integer> freeSeatsWithout(Integer seat) {
    List<Integer> free = new ArrayList<>(freeSeats);
    free.remove(seat);
    return free;
}
```

**Uruchom:** test zielony. `S07OverloadTrapTest` dokumentuje oba wyniki pułapki.
**Co powiedzieć:** przeniesienie metody zmienia kontekst typów: inne pola, inne typy, inne przeciążenia w zasięgu. Zwróć uwagę na trzeci przypadek testu: miejsce 2 na pozycji 2 **nie** odróżnia `remove(int)` od `remove(Object)`. Jeden przypadek testowy to za mało.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m4/s07 1 2`

### Rozwiązanie i uzasadnienie

`step2`: `Screening` ma zachowanie o seansie (`headline`, `freeSeatsWithout`), a `BookingPrinter` tylko składa wydruk. Wydruk jest identyczny dla trzech rezerwacji, w tym takich, gdzie numer miejsca różni się od indeksu.

### Pułapki

- Zmiana typu parametru "przy okazji" przenosin (`Integer` -> `int`, `List` -> `Collection`, `long` -> `int`) wybiera inne przeciążenie.
- Metoda przenoszona z klasy, która ma podklasy nadpisujące tę metodę. Nadpisania zostają w starym miejscu i przestają działać.
- `synchronized` na metodzie po przeniesieniu blokuje **inny obiekt**.
- Publiczna metoda przeniesiona bez delegatu w starym miejscu psuje zewnętrznych klientów. Delegat może zostać jako fasada.

### Pytanie do sali

`print` też używa głównie danych `Booking` i `Screening`. Dlaczego nie przenosimy jej do `Booking`?

## Scena s08. Move Field - próg VIP należy do sali

**Temat ze slajdów:** Move Field krok po kroku i ryzyka; Move Method i Move Field - wybór właściciela
**Pakiet:** `pl.training.workshop.m4.s08_movefield` · **Test:** `scripts/warsztat.sh test m4/s08`
**Czas:** ~10 min

### Co widzimy

Od którego rzędu zaczynają się miejsca VIP, to cecha **sali**, a pole `vipFromRow` siedzi w **seansie**. Każdy seans w tej samej sali niesie własną kopię i nic nie pilnuje zgodności. `SeatPricer` czyta pole bezpośrednio (package-private).

```java
public final class Screening {
    private final Hall hall;
    private final int format;
    final int vipFromRow;
    ...
}
// SeatPricer
BigDecimal price = row >= screening.vipFromRow ? base.add(VIP_SURCHARGE) : base;
```

`S08SingleSourceOfTruthTest.startLetsScreeningsInOneHallDisagree` pokazuje, że w `start` ten sam fotel w tej samej sali bywa VIP albo nie, zależnie od seansu.

### Krok 1: Self-Encapsulate Field

**W IDE:** kursor na `vipFromRow`, Refactor > Encapsulate Fields, zaznacz tylko getter, "Use accessors even when field is accessible". IntelliJ podmieni także odczyt w `isVip` i w `SeatPricer`. Zmień widoczność pola na `private`.
**Po:**

```java
private final int vipFromRow;

public int vipFromRow() {
    return vipFromRow;
}

public boolean isVip(int row) {
    return row >= vipFromRow();
}
```

**Uruchom:** `scripts/warsztat.sh test m4/s08` - 14 testów zielonych.
**Co powiedzieć:** teraz jest dokładnie jedno miejsce, w którym zmienimy źródło wartości. Bez tego kroku przeniesienie oznaczałoby poprawianie każdego odczytu ręcznie.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m4/s08 0 1`

### Krok 2: Move Field do Hall

**W IDE:** dodaj pole i parametr konstruktora w `Hall` (Change Signature ⌘F6 na konstruktorze `Hall`, nowy parametr `int vipFromRow`), getter `vipFromRow()`. W `Screening` ciało gettera zamień na `return hall.vipFromRow();`, usuń pole i parametr konstruktora (⌘F6 na konstruktorze `Screening`, usuń `vipFromRow`). Miejsca tworzące seanse (tu: adaptery w teście) przekazują próg do sali.
**Po:**

```java
// Hall
public Hall(String name, int vipFromRow) { ... }
// Screening
public int vipFromRow() {
    return hall.vipFromRow();
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** przenosimy w jednym ruchu, bez okresu, w którym obie klasy mają zapisywalną kopię (dual write). Dwie kopie rozjadą się przy pierwszym wyjątku albo przy pierwszym zapomnianym zapisie.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m4/s08 1 2`

### Krok 3: aktualizacja odczytów i usunięcie akcesora przejściowego

**W IDE:** w `Hall` dodaj `isVip(int row)`. W `Screening.isVip` zamień ciało na `hall.isVip(row)`. W `SeatPricer` zamień `row >= screening.vipFromRow()` na `screening.isVip(row)` i wydziel zmienną `vip` (⌥⌘V). Na końcu Safe Delete (⌘⌦) na `Screening.vipFromRow()` i `Hall.vipFromRow()`.
**Po:**

```java
// Hall
public boolean isVip(int row) {
    return row >= vipFromRow;
}
// SeatPricer
boolean vip = screening.isVip(row);
BigDecimal price = vip ? base.add(VIP_SURCHARGE) : base;
```

**Uruchom:** test zielony, także `afterMoveFieldTheHallDecidesForEveryScreening`.
**Co powiedzieć:** najpierw migrujemy odczyty, a stary akcesor usuwamy dopiero, gdy nikt go nie używa. Przy okazji klienci przestali porównywać surowy próg i pytają o regułę.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m4/s08 2 3`

### Rozwiązanie i uzasadnienie

`step3`: próg VIP ma jednego właściciela (`Hall`) i jedną regułę (`Hall.isVip`). Wycena miejsca jest identyczna na każdym etapie. Zmienił się sposób tworzenia seansu, bo konstruktor stracił parametr. W systemie z bazą danych to osobna migracja danych.

### Pułapki

- Pole używane przez refleksję, ORM (kolumna w tabeli seansów), serializację albo `equals`/`hashCode`. Find Usages tego nie pokaże.
- Utrata modyfikatorów przy przenoszeniu: `final`, `volatile`, `transient`.
- Migracja zapisów przed odczytami albo dwie zapisywalne kopie "na chwilę".
- Seanse, które naprawdę miały różny próg w tej samej sali (np. inna konfiguracja foteli). Wtedy Move Field jest zmianą zachowania i trzeba najpierw sprawdzić dane.

### Pytanie do sali

Jak przeprowadzić ten Move Field, gdy `vipFromRow` jest kolumną w tabeli `screening` z milionem wierszy?

## Scena s09. Extract Class - klient, płatność i klasa-worek

**Temat ze slajdów:** Extract Class - cel, mechanika i zły wynik
**Pakiet:** `pl.training.workshop.m4.s09_extractclass` · **Test:** `scripts/warsztat.sh test m4/s09`
**Czas:** ~15 min

### Co widzimy

`Booking` ma siedem pól i trzy powody zmiany: dane klienta i ich formatowanie (e-mail, telefon), płatność (karta, status, maskowanie) oraz samą rezerwację (id, kwota).

```java
private final String customerName;
private final String customerEmail;
private final String customerPhone;
private final BigDecimal amount;
private String cardNumber;
private String paymentStatus = "NEW";

public String contact() {
    return customerName + " <" + customerEmail.trim().toLowerCase(Locale.ROOT) + ">, tel. "
            + formattedPhone();
}
```

### Krok 1: Extract Class - tylko dane (zły wynik pośredni)

**W IDE:** Refactor > Extract > Delegate na `Booking`, zaznacz trzy pola `customer*`, nazwa klasy `Customer`. Zamień klasę na `record Customer(String name, String email, String phone)` (⌥⏎ "Convert to record"). Konstruktor `Booking` tworzy `new Customer(...)`.
**Po:**

```java
public record Customer(String name, String email, String phone) {
}

public String contact() {
    return customer.name() + " <" + customer.email().trim().toLowerCase(Locale.ROOT) + ">, tel. "
            + formattedPhone();
}
```

**Uruchom:** `scripts/warsztat.sh test m4/s09` - 15 testów zielonych, także `step1CustomerIsADataBagWithoutBehaviour`.
**Co powiedzieć:** to jest **zły wynik**, jeśli się tu zatrzymamy. Mamy klasę-worek z getterami, a cała wiedza o kliencie nadal siedzi w `Booking`. Dane się przeniosły, odpowiedzialność nie. Test struktury to pokazuje: `Customer` nie ma ani jednej metody poza akcesorami.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m4/s09 0 1`

### Krok 2: Move Method - zachowanie idzie za danymi

**W IDE:** w `Booking.contact()` zaznacz treść `return`, Extract Method (⌥⌘M) z parametrem `customer`, nazwa `contactLine`. Potem F6 na `contactLine` i `formattedPhone`, cel `Customer`. `Booking.contact()` zostaje jako delegat.
**Po:**

```java
public record Customer(String name, String email, String phone) {
    public String contactLine() {
        return name + " <" + email.trim().toLowerCase(Locale.ROOT) + ">, tel. " + formattedPhone();
    }
}
// Booking
public String contact() {
    return customer.contactLine();
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** teraz `Customer` ma własną odpowiedzialność. Extract Class to decyzja projektowa, a Move Field i Move Method to mechanika jej realizacji.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m4/s09 1 2`

### Krok 3: Extract Class - płatność od razu z zachowaniem

**W IDE:** utwórz klasę `Payment`, przenieś do niej pola `cardNumber` i `paymentStatus` (F6 z `Booking`) oraz metody `pay`, `isPaid`, `maskedCard`. Metoda `Payment.payWith(card, bookingId)` dostaje id jako **wartość**, bez referencji do `Booking`. W `Booking` zostają delegaty `pay` i `isPaid`, a `summary` woła `payment.description()`.
**Po:**

```java
public final class Payment {
    private String cardNumber;
    private String status = "NEW";

    public void payWith(String card, String bookingId) { ... }
    public boolean isPaid() { ... }
    public String description() {
        return isPaid() ? "oplacona karta " + maskedCard() : "oczekuje";
    }
}
```

**Uruchom:** test zielony, także druga płatność odrzucona z tym samym komunikatem i `step3BookingComposesCustomerAndPayment`.
**Co powiedzieć:** tym razem od razu przenosimy dane **i** zachowanie. `Payment` nie zna `Booking`, więc nie powstał cykl zależności. Publiczne API `Booking` się nie zmieniło, bo działa jako fasada.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m4/s09 2 3`

### Rozwiązanie i uzasadnienie

`step3`: `Booking` ma cztery pola (`id`, `customer`, `amount`, `payment`) i składa rezerwację z dwóch spójnych części. Każda z nich ma jeden powód zmiany: formaty kontaktu, płatność i PCI. Podsumowanie i komunikaty błędów są identyczne na każdym etapie.

### Pułapki

- **Klasa-worek:** przeniesione dane, logika w źródle przez gettery (krok 1). Drugi rodzaj worka to jedna klasa `BookingDetails` na "wszystko, co nie pasuje", bez spójności.
- Referencja zwrotna (`new Payment(this)`) tworzy cykl. Lepiej przekazać wartość albo wąski interfejs.
- Dwa źródła prawdy: pole zostawione w `Booking` "na wszelki wypadek" obok kopii w nowej klasie.
- Zmiana publicznego API przy okazji ekstrakcji (np. `booking.payment().payWith(...)` u klientów). To osobny krok.

### Pytanie do sali

Po czym na przeglądzie kodu rozpoznać, że wydzielona klasa jest workiem, a nie pojęciem?

## Scena s10. Encapsulate Field - status, który każdy może nadpisać

**Temat ze slajdów:** Encapsulate Field
**Pakiet:** `pl.training.workshop.m4.s10_encapsulatefield` · **Test:** `scripts/warsztat.sh test m4/s10`
**Czas:** ~10 min

### Co widzimy

`Reservation.status` to publiczne pole. Reguły przejść (NEW -> PAID -> USED, NEW -> EXPIRED, NEW/PAID -> CANCELLED) pilnuje kasa, a `guestEntry` po prostu przypisuje `"USED"`, także na rezerwację anulowaną.

```java
public final class Reservation {
    public String status = "NEW";
}
// BoxOffice
public void guestEntry(Reservation r) {
    r.status = "USED";
}
```

Test równoważności zapisuje dziwne zachowanie jako `ZASTANE: ...`: płatność po wygaśnięciu, druga płatność i wejście bez płatności są cicho ignorowane, a gość wchodzi na anulowaną rezerwację.

### Krok 1: Encapsulate Field

**W IDE:** kursor na `status`, Refactor > Encapsulate Fields, getter i setter, widoczność pola `private`. IntelliJ przepisze `BoxOffice` na `getStatus()`/`setStatus(...)`.
**Po:**

```java
private String status = "NEW";

public String getStatus() {
    return status;
}

public void setStatus(String status) {
    this.status = status;
}
```

**Uruchom:** `scripts/warsztat.sh test m4/s10` - 29 testów zielonych, także `startFieldIsPublicFromStep1ItIsNot`.
**Co powiedzieć:** trywialne akcesory na tym samym polu to czysta refaktoryzacja, bo setter przyjmuje wszystko jak wcześniej. Zysk jest taki, że każdy zapis przechodzi teraz przez jedną metodę.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m4/s10 0 1`

### Krok 2: operacje domenowe zamiast settera

**W IDE:** w `BoxOffice.pay` zaznacz ciało, Extract Method (⌥⌘M), potem F6 do `Reservation` jako `pay()`. Powtórz dla `checkIn`, `cancel`, `expire`, a `guestEntry` przenieś jako `admitGuestWithoutPayment()`. Na końcu Safe Delete (⌘⌦) na `setStatus` i Rename `getStatus` -> `status`.
**Po:**

```java
public void pay() {
    if (status.equals("NEW")) {
        status = "PAID";
    }
}

public void admitGuestWithoutPayment() {
    status = "USED";
}
```

**Uruchom:** test zielony, bo warunki przepisaliśmy 1:1.
**Co powiedzieć:** `pay()` komunikuje dozwoloną zmianę lepiej niż `setStatus("PAID")`. Obejście z `guestEntry` ma teraz uczciwą nazwę i jest widoczne w API, a nie ukryte w przypisaniu.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m4/s10 1 2`

### Krok 3: niezmiennik w operacjach (świadoma zmiana zachowania)

**W IDE:** ręcznie: prywatna `moveTo(target, allowedFrom...)` rzuca `IllegalStateException`, gdy przejście jest niedozwolone. Każda operacja deleguje do `moveTo`.
**Po:**

```java
public void pay() {
    moveTo("PAID", "NEW");
}

public void admitGuestWithoutPayment() {
    moveTo("USED", "NEW", "PAID");
}
```

**Uruchom:** test zielony, ale dla `step3` z **innymi oczekiwaniami** (`step3RejectsIllegalTransitions`): dozwolone ścieżki bez zmian, a przypadki `ZASTANE` kończą się `BLAD`.
**Co powiedzieć:** to już nie jest refaktoryzacja. Zmieniają się akceptowane operacje i wyjątki, więc to osobny commit z wymaganiem od biznesu. Hermetyzacja z kroków 1-2 sprawiła, że ta zmiana dotyczy jednej klasy.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m4/s10 2 3`

### Rozwiązanie i uzasadnienie

`step2` to koniec refaktoryzacji: pole prywatne, operacje domenowe i identyczne zachowanie. `step3` to zatwierdzona zmiana kontraktu, którą test opisuje osobno. Kolejność ma znaczenie: najpierw hermetyzacja bez zmiany zachowania, potem reguła.

### Pułapki

- Walidacja dodana razem z getterem i setterem w jednym commicie. Nie da się wtedy odróżnić refaktoryzacji od zmiany zachowania.
- Setter, który zostaje "na wszelki wypadek". Wtedy niezmiennik da się obejść tak samo jak przez publiczne pole.
- Pole `volatile` albo chronione blokadą. Semantyka pamięci nie przechodzi sama na metodę.
- Frameworki, które zapisują pole refleksją (ORM, deserializacja) i omijają operacje domenowe.

### Pytanie do sali

Kto w waszej organizacji decyduje, że "gość na anulowaną rezerwację" to błąd, a nie funkcja? Jak test ma to zapisać, zanim decyzja zapadnie?

## Scena s11. Encapsulate Collection - trzy kontrakty listy miejsc

**Temat ze slajdów:** Encapsulate Collection; Trzy różne kontrakty kolekcji
**Pakiet:** `pl.training.workshop.m4.s11_encapsulatecollection` · **Test:** `scripts/warsztat.sh test m4/s11`
**Czas:** ~12 min

### Co widzimy

`Booking.seats` to publiczna, mutowalna lista. `final` blokuje tylko przypisanie, więc każdy może dodać, usunąć albo wyczyścić miejsca z pominięciem właściciela.

```java
public final List<Seat> seats = new ArrayList<>();
// SeatDesk
booking.seats.add(seat);
booking.seats.remove(seat);
```

### Krok 1: operacje w właścicielu, getter bez zmiany kontraktu

**W IDE:** Refactor > Encapsulate Fields na `seats` (tylko getter). Dodaj `addSeat` i `removeSeat` w `Booking` i przepisz `SeatDesk` na te operacje (w `SeatDesk` zaznacz `booking.seats().add(seat)`, ⌥⌘M, F6 do `Booking`). Getter zwraca **tę samą** listę.
**Po:**

```java
private final List<Seat> seats = new ArrayList<>();

public List<Seat> seats() {
    return seats;
}

public void addSeat(Seat seat) {
    seats.add(seat);
}
```

**Uruchom:** `scripts/warsztat.sh test m4/s11` - 16 testów zielonych, `step1GetterStillReturnsTheSameAlias`: klient zmienia tak, widzi zmiany tak.
**Co powiedzieć:** to faza przejściowa, która nadal jest czystą refaktoryzacją. Klient, który jeszcze modyfikuje listę przez getter, działa jak wcześniej, a my migrujemy klientów po kolei.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m4/s11 0 1`

### Krok 2: niemodyfikowalny widok

**W IDE:** w getterze `return Collections.unmodifiableList(seats);`.
**Po:**

```java
public List<Seat> seats() {
    return Collections.unmodifiableList(seats);
}
```

**Uruchom:** test równoważności zielony (kasa używa już `addSeat`/`removeSeat`). `S11CollectionContractTest`: klient zmienia nie, widzi zmiany tak.
**Co powiedzieć:** to pierwsza **zmiana kontraktu** gettera. Jest bezpieczna dopiero wtedy, gdy żaden klient nie modyfikuje listy przez getter. Widok jest żywy, więc wcześniej pobrana lista pokaże miejsca dodane później.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m4/s11 1 2`

### Krok 3: migawka

**W IDE:** w getterze `return List.copyOf(seats);`.
**Po:**

```java
public List<Seat> seats() {
    return List.copyOf(seats);
}
```

**Uruchom:** test równoważności zielony. `S11CollectionContractTest`: klient zmienia nie, widzi zmiany nie.
**Co powiedzieć:** widok czy migawka to decyzja o kontrakcie, a nie szczegół implementacji. Klient, który trzymał listę i liczył, że "sama się odświeży", po tym kroku widzi stare dane.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m4/s11 2 3`

### Rozwiązanie i uzasadnienie

Tabela ze slajdu jako test:

| Wariant | Klient zmienia | Widzi późniejsze zmiany |
|---|:---:|:---:|
| `start` (publiczne pole) | tak | tak |
| `step1` (getter na tym samym polu) | tak | tak |
| `step2` (`Collections.unmodifiableList`) | nie | tak |
| `step3` (`List.copyOf`) | nie | nie |

Wszystkie warianty dają ten sam wynik operacji kasy (`S11EquivalenceTest`), a różnią się kontraktem gettera (`S11CollectionContractTest`). Który wariant jest rozwiązaniem, zależy od klientów: widok, gdy ktoś potrzebuje bieżącego stanu, a migawka, gdy wynik idzie dalej (raport, inny wątek).

### Pułapki

- Niemodyfikowalność blokuje członkostwo, a nie wnętrze elementów. Tu `Seat` jest rekordem, więc to wystarcza. Przy mutowalnych elementach nie wystarczy.
- `List.copyOf` odrzuca `null` i może zwrócić ten sam obiekt dla już niemodyfikowalnej listy, więc nie opieraj kontraktu na tożsamości.
- Konstruktor przyjmujący listę z zewnątrz bez kopii zachowuje alias do listy klienta.
- Migawka nie daje bezpieczeństwa wątkowego samego właściciela.
- Zmiana na zbiór (`Set`) "przy okazji" zmienia zachowanie duplikatów. Test ma przypadek z tym samym miejscem dwa razy.

### Pytanie do sali

Który kontrakt wybierzecie dla listy miejsc, którą czyta ekran sali w kasie, a który dla listy wysyłanej w e-mailu z potwierdzeniem?

## Scena s12. Encapsulate Conditional - czy przysługuje zwrot

**Temat ze slajdów:** Encapsulate Conditional
**Pakiet:** `pl.training.workshop.m4.s12_encapsulateconditional` · **Test:** `scripts/warsztat.sh test m4/s12`
**Czas:** ~8 min

### Co widzimy

`RefundCalculator.refund` liczy zwrot: 100% przy anulowaniu co najmniej 24 h przed seansem, 50% później, 0 po starcie, potrącenie 3.00 (nie poniżej zera). Warunek pyta o implementację, a nie o regułę:

```java
if (b.status().equals("PAID") && now.isBefore(b.screeningStart())
        && !(b.promo() != null && b.promo().startsWith("FREE"))) {
    if (!now.plusHours(24).isAfter(b.screeningStart())) {
        amount = b.tickets();
    } else {
        amount = b.tickets().percent(50);
    }
}
```

### Krok 1: Extract Method dla najmniejszego fragmentu

**W IDE:** zaznacz `b.promo() != null && b.promo().startsWith("FREE")` (bez negacji), ⌥⌘M, nazwa `hasFreeTicketPromo`.
**Po:**

```java
if (b.status().equals("PAID") && now.isBefore(b.screeningStart()) && !hasFreeTicketPromo(b)) {

private static boolean hasFreeTicketPromo(Booking b) {
    return b.promo() != null && b.promo().startsWith("FREE");
}
```

**Uruchom:** `scripts/warsztat.sh test m4/s12` - 28 testów zielonych.
**Co powiedzieć:** osłona `!= null` idzie razem z testem prefiksu. Krótkie spięcie jest zachowaniem, co sprawdza przypadek z `promo = null`.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m4/s12 0 1`

### Krok 2: Encapsulate Conditional - cały warunek

**W IDE:** zaznacz cały warunek pierwszego `if`, ⌥⌘M, nazwa `isRefundable`.
**Po:**

```java
if (isRefundable(b, now)) {

private static boolean isRefundable(Booking b, LocalDateTime now) {
    return b.status().equals("PAID") && now.isBefore(b.screeningStart()) && !hasFreeTicketPromo(b);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** `if` pyta teraz o regułę z regulaminu, a szczegóły (status, czas, promocja) mieszkają w jednym miejscu. Kolejność warunków w `&&` jest taka sama, więc wyjątki i krótkie spięcie też są te same.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m4/s12 1 2`

### Krok 3: Encapsulate Conditional dla gałęzi

**W IDE:** zaznacz `!now.plusHours(24).isAfter(b.screeningStart())`, ⌥⌘M, nazwa `cancelledAtLeast24hBefore`.
**Po:**

```java
if (isRefundable(b, now)) {
    if (cancelledAtLeast24hBefore(b, now)) {
        amount = b.tickets();
    } else {
        amount = b.tickets().percent(50);
    }
}
```

**Uruchom:** test zielony, także przypadek "dokładnie 24 h przed" (100%) i "24 h minus minuta" (50%).
**Co powiedzieć:** podwójne zaprzeczenie zamknęliśmy w nazwie. Granicę (`>=` 24 h) opisuje test, więc nikt jej nie "poprawi" przy okazji uproszczenia wyrażenia.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m4/s12 2 3`

### Rozwiązanie i uzasadnienie

`step3/RefundCalculator`: metoda publiczna czyta się jak regulamin (czy przysługuje zwrot, czy anulowano co najmniej 24 h przed), a predykaty mają nazwy z domeny. Test obejmuje granice czasu, statusy, promocje, `null` i dolną granicę kwoty.

### Pułapki

- Uproszczenie `!now.plusHours(24).isAfter(start)` do `now.plusHours(24).isBefore(start)` gubi przypadek "dokładnie 24 h".
- Obliczenie prawej strony osłony wcześniej (np. `boolean free = b.promo().startsWith("FREE")` przed sprawdzeniem `null`).
- Nazwa `is`/`has` nie gwarantuje czystości. Predykat z efektem ubocznym (log, licznik, zapytanie do bazy) po ekstrakcji może zostać wywołany w innym miejscu albo inną liczbę razy.
- Przeniesienie predykatu do `Booking` jako następny krok ma sens, ale wymaga zegara albo `now` jako parametru, bo sam rekord nie zna bieżącej chwili.

### Pytanie do sali

Czy `isRefundable` powinno trafić do `Booking`? Co wtedy z parametrem `now`?

## Proponowana kolejność pokazu

**Ścieżka krótka (~75 min):** zasada "najpierw test, potem ruch" i pułapki semantyki.

1. s00 Test charakterystyki (ruch A i krok 1, ~12 min)
2. s01 Rename, tylko krok 2 i 3 z naiwnym ruchem na żywo (~8 min)
3. s02 Extract Variable, krok 2 z osłoną `null` (~5 min)
4. s04 Extract Method (~15 min)
5. s05 Inline Variable, pokaz pułapki i krok 3 (~8 min)
6. s06 Inline Method, krok 2 (~5 min)
7. s07 Move Method, krok 2 (~6 min)
8. s09 Extract Class, kroki 1 i 2 ze "złym wynikiem" (~10 min)
9. s11 Encapsulate Collection, tabela kontraktów (~6 min)

**Ścieżka pełna (~145 min):** wszystkie sceny w kolejności numerów, s00-s12. Po s04 i po s09 zrób przerwę na pytania. Sceny s03, s08, s10 i s12 dobrze nadają się do zadania dla uczestników, jeśli brakuje czasu na pokaz (patrz plik zadań modułu 4).
