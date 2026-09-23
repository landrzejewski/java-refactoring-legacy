# Moduł 3. Zasady dobrego projektowania - warsztat CineLegacy: przewodnik prowadzącego

Szesnaście małych scen w domenie kina CineLegacy pokazuje zasady z modułu 3 jako serie bezpiecznych zmian: DRY, KISS, YAGNI, SOLID, spójność i sprzężenie, granice Clean Architecture, wzorzec jako decyzja odwracalna oraz inwarianty modelu. Każda scena ma kod wyjściowy (`start`) z zapachem lub pułapką i kompletne snapshoty po każdym kroku (`step1`, `step2`, ...). Prowadzący pracuje na żywo w `start`, po każdym kroku uruchamia test sceny i porównuje wynik ze snapshotem.

Sceny nie powielają ćwiczeń z `md/zadania/03-zasady-dobrego-projektowania.md` (studium wyceny dostawy w `pl.training.module3`): tamte ćwiczą jeden duży przykład, tu każda zasada ma własną, kilkuminutową scenę.

## Jak prowadzić pokaz

```bash
scripts/warsztat.sh list m3              # lista scen i kroków modułu 3
scripts/warsztat.sh test m3/s01          # testy jednej sceny (krótka nazwa wystarczy)
scripts/warsztat.sh test m3              # wszystkie sceny modułu (238 testów)
scripts/warsztat.sh diff m3/s01 0 1      # co zmienia krok 1 względem start (0 = start)
scripts/warsztat.sh jump m3/s12 3        # przeskok: start = snapshot kroku 3
scripts/warsztat.sh reset m3/s12         # przywrócenie start z repozytorium
```

- Zasada pokazu: **jeden krok - jeden test - jedno zdanie komentarza**. Nie łącz kroków, nawet jeśli IDE pozwala.
- Jeśli krok na żywo się nie uda albo brakuje czasu: `jump` do snapshotu i kontynuuj od następnego kroku. Po pokazie zawsze `reset`.
- Testy dotykają `start` wyłącznie przez API wspólne dla wszystkich kroków, więc kompilują się w każdym stanie pośrednim. Tam, gdzie refaktoryzacja zmienia konstruktory, scena ma punkt wejścia pełniący rolę composition root: `Main` (s11), `CinemaApplication` (s12), `CinemaApp` (s13), `TicketDesk` (s16). Refaktoryzacje IntelliJ (Introduce Parameter, Move Class) same aktualizują te miejsca.
- Pułapki, których nie da się sprawdzić na edytowanym `start` (bo znikają po naprawie), testy dokumentują na kopii w kroku, który jeszcze ją ma (np. s04 krok 1, s09 krok 1, s15 krok 1).
- Test s13 czyta pliki źródłowe względem katalogu projektu - uruchamiaj go z katalogu głównego repozytorium (tak działa Maven i skrypt).

## Mapa scen

| Scena | Temat ze slajdów | Kroki | Pakiet | Czas |
| --- | --- | --- | --- | --- |
| s01 | 2.1 DRY dotyczy wiedzy | 4 | `m3.s01_dryknowledge` | ~12 min |
| s02 | 2.2 Podobieństwo nie wystarcza | 3 | `m3.s02_similarity` | ~10 min |
| s03 | 2.3 Fałszywa abstrakcja | 2 | `m3.s03_falseabstraction` | ~8 min |
| s04 | 2.3 DRY w testach, niezależna wyrocznia | 2 | `m3.s04_drytests` | ~10 min |
| s05 | 2.4-2.5 KISS: złożoność wprowadzona i istotna | 2 | `m3.s05_kiss` | ~8 min |
| s06 | 2.6-2.7 YAGNI, 2.8 napięcia, 2.9 filtr decyzyjny | 3 | `m3.s06_yagni` | ~12 min |
| s07 | 3.2 SRP: jeden aktor zmiany | 3 | `m3.s07_srp` | ~12 min |
| s08 | 3.3 OCP: zamknięcie dla wybranej osi | 3 | `m3.s08_ocp` | ~10 min |
| s09 | 3.4 LSP i test kontraktowy | 2 | `m3.s09_lsp` | ~12 min |
| s10 | 3.5 ISP z perspektywy klienta | 2 | `m3.s10_isp` | ~8 min |
| s11 | 3.6 DIP, 5.1-5.2 reguła zależności a przepływ sterowania | 3 | `m3.s11_dip` | ~12 min |
| s12 | 5.3-5.6 przypadek użycia, dane na granicy, composition root | 4 | `m3.s12_cleanarchitecture` | ~18 min |
| s13 | 4.4 diagnostyka spójności, lista kontrolna "granica sprawdzana automatycznie" | 2 | `m3.s13_boundarycheck` | ~12 min |
| s14 | 6.1-6.4 Strategy i Adapter, wzorzec można usunąć | 3 | `m3.s14_reversiblepattern` | ~12 min |
| s15 | 7.4 model domeny i inwarianty | 2 | `m3.s15_invariants` | ~8 min |
| s16 | 4.4 sprzężenie protokołu i czasu | 2 | `m3.s16_temporalcoupling` | ~6 min |

## Scena s01. DRY - jedna wiedza, dwie reprezentacje

**Temat ze slajdów:** 2.1 DRY dotyczy wiedzy; 2.8 Napięcia między zasadami (nazwanie potwierdzonej reguły)
**Pakiet:** `pl.training.workshop.m3.s01_dryknowledge` · **Test:** `scripts/warsztat.sh test m3/s01`
**Czas:** ~12 min

### W skrócie

**Co robimy:** W `BoxOffice` reguła ceny biletu jest zapisana dwa razy - raz przy sprzedaży, raz przy zwrocie - różnym kodem. Nazywamy obie kopie, sprawdzamy testem, że liczą to samo, zostawiamy jedną i przenosimy ją do klasy `TicketPrice`.

**Zasada:** DRY mówi, że każda wiedza w systemie powinna mieć jedną autorytatywną reprezentację. Chodzi o wiedzę (regułę biznesową), a nie o podobny tekst: dwa różne fragmenty mogą kodować tę samą regułę, a dwa identyczne mogą być przypadkowo podobne.

**Efekt:** Zmiana zniżki studenckiej to teraz edycja jednego miejsca, a zwrot automatycznie używa aktualnego cennika. Reguły samego zwrotu zostają w `BoxOffice`, bo mają innego właściciela.

### Co widzimy

`BoxOffice` sprzedaje bilety i przyjmuje zwroty. Reguła ceny biletu (cena formatu, zniżka typu, seans poranny) jest zapisana dwa razy: w `sell` jako dwa `switch` i procent, w `refund` jako łańcuch `if` i mnożniki. Tekst jest inny, więc detektor duplikatów IDE nic nie znajdzie, ale wiedza jest ta sama. Zmiana zniżki studenckiej wymaga zgodnej edycji dwóch miejsc.

```java
// ile kosztował bilet
BigDecimal paid = new BigDecimal("25.00");
if (ticket.format().equals("3D")) {
    paid = new BigDecimal("32.00");
} else if (ticket.format().equals("IMAX")) {
    paid = new BigDecimal("40.00");
}
if (ticket.type().equals("STUDENT")) {
    paid = paid.multiply(new BigDecimal("0.75"));
} // ...SENIOR 0.70, CHILD 0.60, poranek -5.00
```

### Krok 1: Extract Method - nazwij drugą kopię wiedzy

**W IDE:** w `refund` zaznacz blok od `// ile kosztował bilet` do końca `if` z porankiem, ⌥⌘M, nazwa `paidFor`. Usuń komentarz.
**Po:**

```java
public BigDecimal refund(Ticket ticket, long hoursBeforeStart) {
    BigDecimal paid = paidFor(ticket);
    // zwrot: >= 24h 100%, < 24h 50%, po starcie 0%; potrącenie 3.00
```

**Uruchom:** `scripts/warsztat.sh test m3/s01` - 25 testów zielonych (5 przypadków x 5 wariantów).
**Co powiedzieć:** zanim scalimy wiedzę, musimy ją zobaczyć. Nazwa `paidFor` odpowiada na to samo pytanie co `sell`: ile kosztuje ten bilet?
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m3/s01 0 1`

### Krok 2: Extract Method po stronie sprzedaży

**W IDE:** w `sell` zaznacz całe ciało metody, ⌥⌘M, nazwa `ticketPrice`.
**Po:**

```java
public BigDecimal sell(Ticket ticket) {
    return ticketPrice(ticket);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** teraz obie kopie reguły to dwie prywatne metody o identycznej sygnaturze. Widać, że jedna jest zbędna.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m3/s01 1 2`

### Krok 3: Substitute Algorithm - zwrot korzysta z jednej reguły

**W IDE:** w `refund` ręcznie zamień `paidFor(ticket)` na `ticketPrice(ticket)`, uruchom test, potem Safe Delete (⌘⌦) na `paidFor`.
**Po:**

```java
BigDecimal paid = ticketPrice(ticket);
```

**Uruchom:** test zielony - to dowód, że obie kopie liczyły to samo dla wszystkich przypadków (w tym zniżki, poranek i zwrot po starcie).
**Co powiedzieć:** Substitute Algorithm jest bezpieczny tylko pod testem równoważności. Gdyby kopie się rozjechały, test pokazałby, która reguła jest "prawdziwa" - i to byłaby rozmowa z biznesem, nie decyzja programisty.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m3/s01 2 3`

### Krok 4: Extract Class - autorytatywne źródło wiedzy

**W IDE:** na `ticketPrice` Refactor > Extract > Delegate (Extract Class), klasa `TicketPrice`, metoda `of`. W nowej klasie ⌥⌘M dla `basePrice` i `discountPercent`, ⌥⌘C dla `MORNING_DISCOUNT`; w `BoxOffice` ⌥⌘C dla `REFUND_DEDUCTION`.
**Po:**

```java
public final class BoxOffice {
    private static final BigDecimal REFUND_DEDUCTION = new BigDecimal("3.00");
    private final TicketPrice ticketPrice = new TicketPrice();

    public BigDecimal sell(Ticket ticket) {
        return ticketPrice.of(ticket);
    }
```

**Uruchom:** test zielony.
**Co powiedzieć:** reguła ceny ma nazwę w języku problemu i jednego właściciela (cennik). `BoxOffice` zna już tylko własną wiedzę: regulamin zwrotów.
**Snapshot:** `step4/` · **Różnica:** `scripts/warsztat.sh diff m3/s01 3 4`

### Rozwiązanie i uzasadnienie

`step4/TicketPrice` to jedyna reprezentacja wiedzy "ile kosztuje bilet". Scalenie było uzasadnione od razu, bez czekania na trzecią kopię: dwie kopie tej samej reguły to już ryzyko niezgodnej zmiany (slajd 2.8: reguła trzech pomaga czekać na kształt abstrakcji, ale nie definiuje DRY).

### Pułapki

- Szukanie duplikacji po tekście: IDE nie znalazło tej kopii, bo była inaczej napisana.
- Scalanie bez testu równoważności - jeśli kopie już się różniły, "cicho" zmieniamy zachowanie.
- Przeniesienie do `TicketPrice` także reguł zwrotu - to inna wiedza z innym właścicielem.

### Pytanie do sali

Gdzie w waszym systemie ta sama reguła żyje w SQL, w kodzie i w dokumentacji jednocześnie? Co jest jej autorytatywnym źródłem?

## Scena s02. Podobieństwo to nie duplikacja

**Temat ze slajdów:** 2.2 Podobieństwo nie wystarcza; 2.3 Tymczasowe powtórzenie kodu bywa bezpiecznym etapem
**Pakiet:** `pl.training.workshop.m3.s02_similarity` · **Test:** `scripts/warsztat.sh test m3/s02`
**Czas:** ~10 min

### W skrócie

**Co robimy:** Klasa `ServiceFee` scala opłatę rezerwacyjną online i potrącenie przy zwrocie tylko dlatego, że obie wyglądają jak "kwota razy liczba sztuk". Wklejamy wspólny kod z powrotem do obu wywołujących, usuwamy martwe gałęzie i dajemy każdej kwocie stałą u jej właściciela.

**Zasada:** Podobny kod to jeszcze nie ta sama wiedza - rozstrzyga pytanie, czy dwie reguły zmieniają się razem i z tego samego powodu. Wspólna metoda dla niezależnych reguł tworzy fałszywą zależność, a chwilowe powtórzenie kodu jest bezpiecznym etapem ich rozdzielania.

**Efekt:** Marketing może zmienić opłatę rezerwacyjną bez ryzyka dla regulaminu zwrotów, a parametr `units = 1` znika. Płacimy za to dwiema podobnie wyglądającymi linijkami, co jest w porządku, bo kodują różną wiedzę.

### Co widzimy

Ktoś "zDRYował" dwie reguły, bo wyglądały tak samo ("stała kwota razy liczba sztuk"): opłatę rezerwacyjną online (2.00 za bilet, właściciel: sprzedaż online i marketing) i potrącenie przy zwrocie (3.00 za zwrot, właściciel: regulamin zwrotów). Powstała wspólna metoda z przełącznikiem, a `RefundDesk` musi podać `units = 1`, choć ten parametr pasuje tylko drugiej regule.

```java
public static BigDecimal of(Kind kind, int units) {
    BigDecimal perUnit = kind == Kind.ONLINE_BOOKING ? new BigDecimal("2.00") : new BigDecimal("3.00");
    return perUnit.multiply(BigDecimal.valueOf(units)).setScale(2, RoundingMode.HALF_UP);
}
```

### Krok 1: Inline Method - wspólny kod wraca do wywołujących

**W IDE:** na `ServiceFee.of` ⌥⌘N, opcja "Inline all and remove the method". Zostaje samo `enum Kind`.
**Po:**

```java
BigDecimal perUnit = ServiceFee.Kind.REFUND == ServiceFee.Kind.ONLINE_BOOKING
        ? new BigDecimal("2.00") : new BigDecimal("3.00");
BigDecimal fee = perUnit.multiply(BigDecimal.valueOf(1)).setScale(2, RoundingMode.HALF_UP);
```

**Uruchom:** `scripts/warsztat.sh test m3/s02` - 16 testów zielonych.
**Co powiedzieć:** chwilowo mamy więcej powtórzonego kodu - i to jest w porządku. Powtórzenie kodu to bezpieczny etap rozdzielania fałszywie scalonej wiedzy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m3/s02 0 1`

### Krok 2: Simplify - usuń martwe gałęzie

**W IDE:** na warunkach porównujących stałe enuma ⌥⏎ "Simplify" (IntelliJ widzi warunek zawsze prawdziwy lub fałszywy), usuń mnożenie przez 1. Safe Delete (⌘⌦) na `ServiceFee`.
**Po:**

```java
return share.subtract(new BigDecimal("3.00")).max(BigDecimal.ZERO.setScale(2));
```

**Uruchom:** test zielony.
**Co powiedzieć:** po uproszczeniu widać, że obie reguły nie miały ze sobą nic wspólnego poza kształtem wyrażenia.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m3/s02 1 2`

### Krok 3: Extract Constant u właściciela reguły

**W IDE:** ⌥⌘C na `"2.00"` w `OnlineCheckout` (nazwa `BOOKING_FEE_PER_TICKET`) i na `"3.00"` w `RefundDesk` (nazwa `REFUND_DEDUCTION`).
**Po:**

```java
private static final BigDecimal BOOKING_FEE_PER_TICKET = new BigDecimal("2.00");  // OnlineCheckout
private static final BigDecimal REFUND_DEDUCTION = new BigDecimal("3.00");        // RefundDesk
```

**Uruchom:** test zielony.
**Co powiedzieć:** każda stała ma nazwę w języku domeny i mieszka u swojego właściciela. Marketing może znieść opłatę rezerwacyjną w promocji, nie dotykając regulaminu zwrotów.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m3/s02 2 3`

### Rozwiązanie i uzasadnienie

Dwie reguły, dwie stałe, dwóch właścicieli. Kod jest podobny, wiedza niezależna - zgodnie z tabelą ze slajdu 2.2 ("dwa konteksty mają dziś podobną regułę: może być przypadkowe; ustal relację reguł").

### Pułapki

- Wspólna metoda dla niezależnych reguł tworzy fałszywą zależność: zmiana jednej grozi zmianą drugiej.
- Parametr, który ma sens tylko dla jednego wywołującego (`units = 1`), to sygnał fałszywej abstrakcji.
- Odwrotny błąd: rozdzielenie reguły, która naprawdę jest jedna (s01).

### Pytanie do sali

Jakie pytanie zadać biznesowi, żeby rozstrzygnąć, czy dwie podobne reguły to jedna wiedza? (Podpowiedź: "czy zmieniają się razem i z tego samego powodu?")

## Scena s03. Fałszywa abstrakcja z flagami

**Temat ze slajdów:** 2.3 Fałszywa abstrakcja: zabezpiecz testami, przenieś kod z powrotem, wydziel tylko potwierdzoną wiedzę
**Pakiet:** `pl.training.workshop.m3.s03_falseabstraction` · **Test:** `scripts/warsztat.sh test m3/s03`
**Czas:** ~8 min

### W skrócie

**Co robimy:** `Pricing.price` obsługuje bilety i karnety przez pięć parametrów, w tym flagi `boolean`, z których każdy wywołujący potrzebuje tylko części. Cofamy abstrakcję do obu kas, upraszczamy martwe gałęzie i sprawdzamy, czy zostało coś wspólnego.

**Zasada:** Fałszywa abstrakcja to wspólny kod, który łączy konteksty bez wspólnej wiedzy i rośnie przez kolejne flagi. Kolejność naprawy: zabezpiecz testami, przenieś kod z powrotem do kontekstów, a wydziel tylko tę wiedzę, która się potwierdzi.

**Efekt:** Zostają dwie proste klasy, a `PassCounter` nie wie nic o formatach i okularach. Wspólnej wiedzy nie było wcale, więc świadomie niczego nowego nie wydzielamy.

### Co widzimy

`Pricing.price` wycenia bilety i karnety (reguła sceny: karnet to 20.00 za wejście na seans 2D), sterowana flagami `boolean`. Kasa biletowa podaje `quantity = 1` i `pass = false`, kasa karnetów podaje format, poranek i okulary "na wszelki wypadek".

```java
public BigDecimal price(String format, int quantity, boolean pass,
                        boolean morning, boolean ownGlasses) {
    BigDecimal unit;
    if (pass) {
        unit = new BigDecimal("20.00");
    } else { /* format, poranek */ }
    if (format.equals("3D") && !ownGlasses && !pass) { /* okulary */ }
```

```java
return pricing.price("2D", entries, true, false, true);   // PassCounter
```

### Krok 1: Inline Method do obu wywołujących

**W IDE:** na `Pricing.price` ⌥⌘N, "Inline all and remove". IntelliJ wstawi kod z lokalnymi zmiennymi dla stałych argumentów (`boolean pass = false;`). Usuń pustą klasę `Pricing` i pole `pricing`.
**Po:**

```java
public BigDecimal ticket(String format, boolean morning, boolean ownGlasses) {
    boolean pass = false;
    BigDecimal unit;
    if (pass) {
```

**Uruchom:** `scripts/warsztat.sh test m3/s03` - 18 testów zielonych.
**Co powiedzieć:** najpierw cofamy abstrakcję, dopiero potem myślimy, czy jest w tym kodzie coś naprawdę wspólnego.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m3/s03 0 1`

### Krok 2: Inline Variable i Simplify - martwe gałęzie znikają

**W IDE:** ⌥⌘N na `pass`, `morning`, `ownGlasses`, `format` w `PassCounter` (i na `pass` w `TicketCounter`), potem ⌥⏎ "Simplify" na warunkach ze stałymi. Na koniec ⌥⌘C dla stałych kwot.
**Po:**

```java
public BigDecimal pass(int entries) {
    return PRICE_PER_ENTRY.multiply(BigDecimal.valueOf(entries));
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** po rozdzieleniu okazuje się, że wspólnej wiedzy nie ma wcale - karnet nie wie nic o formatach i okularach. Nie wydzielamy nic nowego.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m3/s03 1 2`

### Rozwiązanie i uzasadnienie

Dwie proste klasy zamiast jednej metody z pięcioma parametrami. Kolejność z slajdu 2.3: testy (tu równoważności) - kod z powrotem do kontekstów - wydzielenie tylko potwierdzonej wiedzy (tu: żadnej).

### Pułapki

- Dokładanie kolejnej flagi do fałszywej abstrakcji "bo już jest wspólna metoda".
- Rozdzielanie bez testów: przy flagach łatwo pomylić kolejność argumentów `boolean`.

### Pytanie do sali

Ile flag `boolean` ma najdłuższa metoda w waszym projekcie? Ilu wywołujących używa każdej kombinacji?

## Scena s04. DRY w testach i niezależna wyrocznia

**Temat ze slajdów:** 2.3 DRY w testach: wspólne fabryki danych tak, liczenie oczekiwanej wartości algorytmem produkcyjnym nie
**Pakiet:** `pl.training.workshop.m3.s04_drytests` · **Test:** `scripts/warsztat.sh test m3/s04`
**Czas:** ~10 min

### W skrócie

**Co robimy:** Specyfikacja cen `TicketPriceSpecs` ma przypadki zaszyfrowane w napisach i liczy oczekiwaną cenę tym samym wzorem co produkcja. Najpierw nadajemy przypadkom czytelne nazwy, potem zastępujemy wyliczanie oczekiwania kwotami policzonymi ręcznie z regulaminu.

**Zasada:** DRY w testach obejmuje fabryki danych i wspólne helpery, ale nie oczekiwaną wartość: test liczący ją algorytmem produkcyjnym traci niezależną wyrocznię. DAMP oznacza, że każdy przypadek czyta się jak zdanie z regulaminu, nawet kosztem pewnego powtórzenia.

**Efekt:** Specyfikacja łapie błędną zniżkę studencką i mówi, który przypadek, jaka kwota oczekiwana i jaka faktyczna. Koszt: przy zmianie cennika kwoty w przykładach trzeba przeliczyć ręcznie - i to jest zamierzone.

### Co widzimy

Kod produkcyjny sceny (`Tariff`, `TicketPrice`) jest stabilny. Refaktoryzujemy specyfikację cen `TicketPriceSpecs` - w projekcie byłaby to klasa testowa JUnit, tu leży w `main`, żeby działał mechanizm start/stepN. Metoda `run` zwraca listę niespełnionych przypadków. Start jest "maksymalnie DRY": przypadki zaszyfrowane w stringach, a oczekiwana cena liczona z tej samej taryfy i tym samym wzorem co produkcja.

```java
private static final List<String> SPECS = List.of("IMAX N 20", "3D S 11", "2D E 18", "2D C 10");
...
BigDecimal base = price.tariff().basePrices().get(p[0]);
BigDecimal expected = base.subtract(discount)
        .subtract(start.getHour() < 12 ? new BigDecimal("5.00") : BigDecimal.ZERO);
```

### Krok 1: DAMP - nazwane przykłady zamiast szyfru

**W IDE:** ręcznie: zamień pętlę po `SPECS` na cztery wywołania `check(price, "student na porannym 3D", "3D", "STUDENT", LocalTime.of(11, 0), failures)`. Usuń parsowanie stringa. Wyliczenie oczekiwania wydziel ⌥⌘M jako `expectedFromTariff`. Komunikat błędu: nazwa przykładu, oczekiwana i faktyczna cena.
**Po:**

```java
check(price, "student na porannym 3D", "3D", "STUDENT", LocalTime.of(11, 0), failures);
...
BigDecimal expected = expectedFromTariff(price.tariff(), format, type, start);
```

**Uruchom:** `scripts/warsztat.sh test m3/s04` - zielono; test `step1IsReadableButStillBlindToTheBug` pokazuje, że z błędną taryfą (zniżka studencka 20% zamiast 25%) krok 1 nadal nic nie zgłasza.
**Co powiedzieć:** czytelność poprawiona, ale test wciąż jest kopią algorytmu. Jeśli algorytm jest zły, test jest zły tak samo.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m3/s04 0 1`

### Krok 2: Jawna oczekiwana wartość - niezależna wyrocznia

**W IDE:** Change Signature (⌘F6) na `check`: nowy parametr `String expected`, wpisz ręcznie policzone kwoty (40.00, 19.00, 17.50, 10.00). Safe Delete (⌘⌦) na `expectedFromTariff`.
**Po:**

```java
check(price, "student na porannym 3D", "3D", "STUDENT", LocalTime.of(11, 0), "19.00", failures);
```

**Uruchom:** test zielony; `step2CatchesBrokenStudentDiscount` pokazuje komunikat `student na porannym 3D: oczekiwano 19.00, jest 20.60`.
**Co powiedzieć:** wspólny helper `check` zostaje - współdzielenie fabryki przypadku i formatu komunikatu to dobre DRY w testach. Wyrocznia musi być niezależna od kodu, który sprawdza.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m3/s04 1 2`

### Rozwiązanie i uzasadnienie

DAMP (Descriptive And Meaningful Phrases): każdy przypadek czyta się jak zdanie z regulaminu, oczekiwanie pochodzi z regulaminu, nie z taryfy. Kwoty policz na sali: 32.00 - 25% = 24.00, rano -5.00 = 19.00.

### Pułapki

- "Test sprawdza, czy kod robi to, co robi" - oczekiwanie liczone kodem produkcyjnym lub jego kopią.
- Przesadne DRY w testach: jeden parametryzowany helper z szyfrem zamiast czytelnych przykładów.
- Odwrotna skrajność: kopiowanie całego przygotowania danych w każdym teście zamiast fabryki.

### Pytanie do sali

W którym waszym teście oczekiwana wartość jest wyliczana, a nie wpisana? Co by się stało, gdyby wzór był zły?

## Scena s05. KISS - złożoność wprowadzona kontra istotna

**Temat ze slajdów:** 2.4-2.5 KISS: prostota po poprawności; ukryty przepływ przez refleksję
**Pakiet:** `pl.training.workshop.m3.s05_kiss` · **Test:** `scripts/warsztat.sh test m3/s05`
**Czas:** ~8 min

### W skrócie

**Co robimy:** `SeatCounter` wybiera rodzaj miejsc napisem i refleksją, a wolne miejsca liczy wyrażeniem regularnym. Zastępujemy to dwiema jawnymi metodami i zwykłą pętlą po znakach.

**Zasada:** KISS to najmniej złożone rozwiązanie, które poprawnie realizuje aktualne wymagania. Celuje w złożoność wprowadzoną (refleksja, sprytne mechanizmy), a złożoność istotną zostawia, tylko ją nazywa - nie myl go z najmniejszą liczbą linii.

**Efekt:** Literówka w rodzaju miejsc nie przejdzie kompilacji, a Find Usages znów pokazuje przepływ. Reguły istotne, jak miejsce zablokowane czy początek strefy VIP, zostają i pilnuje ich test.

### Co widzimy

`SeatCounter` liczy wolne miejsca w sali (`'.'` wolne, `'X'` zajęte, `'B'` zablokowane, `' '` przejście) oraz wolne miejsca VIP. Rodzaj miejsc wybierany jest stringiem i refleksją, a liczenie idzie przez wyrażenie regularne z nazwaną grupą. IntelliJ pokazuje `allRows` i `vipRows` jako nieużywane.

```java
Method rows = getClass().getDeclaredMethod(kind + "Rows", Hall.class);
@SuppressWarnings("unchecked")
Stream<String> selected = (Stream<String>) rows.invoke(this, hall);
return selected.flatMap(row -> FREE.matcher(row).results().map(m -> m.group("seat"))).count();
```

### Krok 1: Replace Parameter with Explicit Methods

**W IDE:** ręcznie: zamiast `free(hall, "all")` i `free(hall, "vip")` utwórz `freeSeats(hall)` i `freeVipSeats(hall)`, które wołają wspólne `countFree(Stream<String>)`. Usuń refleksję i `@SuppressWarnings`.
**Po:**

```java
public String summary(Hall hall) {
    return "wolne: " + freeSeats(hall) + ", wolne VIP: " + freeVipSeats(hall);
}
```

**Uruchom:** `scripts/warsztat.sh test m3/s05` - 12 testów zielonych.
**Co powiedzieć:** literówka w `"vip"` wybuchała dopiero w runtime; teraz nie przejdzie kompilacji. Przepływ widać w IDE (⌥F7 Find Usages znów działa).
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m3/s05 0 1`

### Krok 2: Substitute Algorithm - zwykłe pętle

**W IDE:** ręcznie zastąp strumień indeksów przez `subList` od pierwszego rzędu VIP, a regex przez pętlę po znakach porównującą ze stałą `FREE = '.'`.
**Po:**

```java
private int freeIn(List<String> rows) {
    int free = 0;
    for (String row : rows) {
        for (char seat : row.toCharArray()) {
            if (seat == FREE) {
                free++;
            }
        }
    }
    return free;
}
```

**Uruchom:** test zielony, także dla rzędu z `'B'` i przejściem.
**Co powiedzieć:** złożoność istotna zostaje (co to jest wolne miejsce, od którego rzędu VIP, obcięcie `vipFromRow` do zakresu), ale jest nazwana. Zniknęła tylko złożoność wprowadzona.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m3/s05 1 2`

### Rozwiązanie i uzasadnienie

KISS to najmniej złożone rozwiązanie, które **poprawnie** realizuje wymagania. Test z zablokowanym miejscem i przejściem pilnuje, że uproszczenie nie zgubiło przypadku.

### Pułapki

- Uproszczenie, które pomija przypadek brzegowy (np. `'B'` liczone jako wolne) - "prostsze, ale błędne".
- Mylenie KISS z najmniejszą liczbą linii: jednolinijkowy regex jest krótszy, ale trudniejszy.

### Pytanie do sali

Jaki "sprytny" mechanizm (refleksja, adnotacje, konwencje nazw) w waszym kodzie ukrywa przepływ sterowania? Czy daje coś, czego nie da zwykłe wywołanie?

## Scena s06. YAGNI - silnik reguł dla dwóch reguł

**Temat ze slajdów:** 2.6-2.7 YAGNI i czego nie zabrania; 2.8 Napięcia (ogólny silnik hipotetycznych taryf); 2.9 Filtr decyzyjny
**Pakiet:** `pl.training.workshop.m3.s06_yagni` · **Test:** `scripts/warsztat.sh test m3/s06`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `TicketPricer` to silnik z rejestrem pluginów, konfiguracją napisem i kontekstem `Map` - dla dokładnie dwóch reguł. W trzech krokach usuwamy rejestr, zastępujemy mapę typowanymi danymi i wklejamy reguły do jednej klasy.

**Zasada:** YAGNI mówi, żeby nie budować zdolności potrzebnej tylko dla przewidywanego wymagania. Nie zabrania testów, refaktoryzacji ani nazwanych metod - to one pozwalają bezpiecznie odłożyć abstrakcję do chwili, gdy pojawi się realna potrzeba.

**Efekt:** Z pięciu klas zostaje jedna z dwiema nazwanymi regułami, a literówka w nazwie reguły nie skompiluje się. Gdy przyjdzie trzecia reguła z innym właścicielem, abstrakcję trzeba będzie wprowadzić ponownie.

### Co widzimy

`TicketPricer` to spekulatywny silnik: rejestr pluginów, konfiguracja napisem `"morning,vip"`, priorytety i kontekst `Map<String, Object>`. Obsługuje dokładnie dwie reguły (poranek -5.00, VIP +10.00), które nie zależą od kolejności.

```java
Map<String, Object> context = new HashMap<>();
context.put("start", quote.start());
context.put("row", quote.row());
...
for (PricingRule rule : registry.resolve(activeRules)) {
    if (rule.appliesTo(context)) {
        price = rule.apply(context, price);
    }
}
```

Na żywo: zmień w konstruktorze `"morning,vip"` na `"morning,vlp"` i uruchom test - błąd konfiguracji wychodzi dopiero w runtime (`IllegalArgumentException: brak reguly: vlp`). Cofnij zmianę.

### Krok 1: Inline Class - rejestr pluginów znika

**W IDE:** w `TicketPricer` zastąp pola `registry` i `activeRules` listą `List.of(new MorningRule(), new VipRule())` (ręcznie, konstruktory usuń). Safe Delete (⌘⌦) na `RuleRegistry` i na `priority()` w interfejsie (był potrzebny tylko do sortowania).
**Po:**

```java
private final List<PricingRule> rules = List.of(new MorningRule(), new VipRule());
```

**Uruchom:** `scripts/warsztat.sh test m3/s06` - 16 testów zielonych.
**Co powiedzieć:** konfiguracja napisem nie miała żadnego klienta. Teraz literówka w nazwie reguły nie skompiluje się.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m3/s06 0 1`

### Krok 2: Change Signature - typowane dane zamiast mapy

**W IDE:** ⌘F6 na `PricingRule.appliesTo`: typ parametru `Map<String, Object>` -> `TicketQuote`; na `apply` usuń parametr kontekstu. Popraw implementacje (rzutowania znikają), usuń budowanie mapy w `TicketPricer`.
**Po:**

```java
public boolean appliesTo(TicketQuote quote) {
    return quote.row() >= quote.vipFromRow();
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** generyczny kontekst to typowa spekulacja "reguła może potrzebować czegokolwiek". Kompilator znów pilnuje nazw pól.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m3/s06 1 2`

### Krok 3: Inline Class dla reguł, Safe Delete interfejsu

**W IDE:** ⌥⌘N na `MorningRule` i `VipRule` (Inline Class) albo ręcznie: dwa `if` w `price` z metodami `isMorning` i `isVip`. Safe Delete na `PricingRule`. Stałe przez ⌥⌘C.
**Po:**

```java
public BigDecimal price(TicketQuote quote) {
    BigDecimal price = basePrice(quote.format());
    if (isMorning(quote)) {
        price = price.subtract(MORNING_DISCOUNT);
    }
    if (isVip(quote)) {
        price = price.add(VIP_SURCHARGE);
    }
    return price;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** YAGNI nie zabrania testów, nazwanych metod ani szwu testowego - cena liczona jest z danych wejściowych, bez zegara i stanu statycznego, więc każdą regułę łatwo sprawdzić. Gdy pojawi się trzecia reguła z innym właścicielem, wydzielimy abstrakcję wtedy.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m3/s06 2 3`

### Rozwiązanie i uzasadnienie

Przejdź z salą filtr ze slajdu 2.9: aktualne wymaganie (2 reguły), nazwa w języku problemu (brak: "plugin", "registry"), czy upraszcza aktualny przypadek (nie), koszt usunięcia (niski - pokazaliśmy). Z pięciu klas została jedna.

### Pułapki

- "YAGNI, więc bez testów" - odwrotnie: testy i małe kroki pozwalają bezpiecznie odraczać decyzje.
- Usunięcie abstrakcji, która chroni aktualną, realną granicę (np. port do bazy) - to nie spekulacja.

### Pytanie do sali

Który mechanizm rozszerzeń w waszym systemie ma dziś jedną albo dwie implementacje? Jaki sygnał powiedziałby, że czas go wprowadzić ponownie?

## Scena s07. SRP - raport dla dwóch aktorów

**Temat ze slajdów:** 3.2 SRP: jeden aktor zmiany; 3.7 Błędne uproszczenia SOLID ("klasa robi jedną rzecz")
**Pakiet:** `pl.training.workshop.m3.s07_srp` · **Test:** `scripts/warsztat.sh test m3/s07`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `DailyReport` składa raport dla księgowości i marketingu, a wspólny helper `revenue` sprawia, że zmiana dla jednego aktora cicho zmienia kwoty drugiego. Wydzielamy sekcje, rozdzielamy helper i tworzymy klasę na każdego aktora.

**Zasada:** SRP mówi, że moduł powinien odpowiadać przed jednym aktorem, czyli mieć jeden powód zmiany. To nie jest "klasa robi jedną rzecz" ani jedna metoda na klasę - klasa może mieć kilka metod, jeśli wszystkie zmieniają się dla tego samego aktora.

**Efekt:** Zmiana definicji hitu dotyka tylko `MarketingSection`, a zmiana stawki VAT tylko `AccountingSection`. Koszt: dwie identyczne dziś formuły, powtórzone świadomie, bo to kod, a nie wspólna wiedza.

### Co widzimy

`DailyReport.render` składa raport dla księgowości (przychód brutto i netto wg VAT 8% i 23%) i dla marketingu (hit dnia, liczba biletów). Obie części korzystają ze wspólnego helpera `revenue`. Gdy marketing poprosi "hit dnia licz bez baru", poprawka helpera po cichu zmieni też "Razem brutto" dla księgowości.

```java
private BigDecimal revenue(Sale sale) {
    return sale.ticketRevenue().add(sale.barRevenue());
}
...
total = total.add(revenue(sale));                              // księgowość
byTitle.merge(sale.title(), revenue(sale), BigDecimal::add);   // marketing
```

### Krok 1: Extract Method według aktora

**W IDE:** zaznacz blok pod `// księgowość`, ⌥⌘M `accountingSection` (zwraca `String`, własny `StringBuilder`); to samo dla `// marketing` jako `marketingSection`.
**Po:**

```java
public String render(List<Sale> sales) {
    return accountingSection(sales) + marketingSection(sales);
}
```

**Uruchom:** `scripts/warsztat.sh test m3/s07` - 12 testów zielonych (w tym remis tytułów i dzień bez sprzedaży).
**Co powiedzieć:** komentarze nazywały aktorów - teraz nazywają je metody. Helper `revenue` nadal wiąże oba światy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m3/s07 0 1`

### Krok 2: Rozdziel wspólny helper według aktora

**W IDE:** skopiuj `revenue` (⌘D na metodzie), nazwij kopię `popularity` (⇧F6), w `marketingSection` użyj `popularity`. Dodaj Javadoc z właścicielem każdej metody.
**Po:**

```java
/** Księgowość: przychód brutto seansu. */
private BigDecimal revenue(Sale sale) { ... }

/** Marketing: miara popularności filmu (dziś: bilety + bar). */
private BigDecimal popularity(Sale sale) { ... }
```

**Uruchom:** test zielony.
**Co powiedzieć:** świadomie powtarzamy kod, nie wiedzę - dziś formuły są równe przypadkiem. To lekcja z s02 w kontekście SRP.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m3/s07 1 2`

### Krok 3: Extract Class - klasa na aktora

**W IDE:** Refactor > Extract > Delegate dla `accountingSection`, `revenue`, `net` -> `AccountingSection`; to samo dla `marketingSection`, `popularity` -> `MarketingSection`. Metody nazwij `render`.
**Po:**

```java
public final class DailyReport {
    private final AccountingSection accounting = new AccountingSection();
    private final MarketingSection marketing = new MarketingSection();

    public String render(List<Sale> sales) {
        return accounting.render(sales) + marketing.render(sales);
    }
}
```

**Uruchom:** test zielony - dokument identyczny co do znaku.
**Co powiedzieć:** `DailyReport` koordynuje, ale nie zna polityk. Zmiana definicji hitu dotyka tylko `MarketingSection`, zmiana stawki VAT tylko `AccountingSection`.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m3/s07 2 3`

### Rozwiązanie i uzasadnienie

Odpowiedzialność = aktor i jego powód zmiany, nie "jedna rzecz". `AccountingSection` ma trzy metody i to jest w porządku: wszystkie zmieniają się dla księgowości.

### Pułapki

- Dzielenie na klasy "po jednej metodzie" zamiast według aktora.
- Zostawienie wspólnego helpera między nowymi klasami (np. w klasie `ReportUtils`) - powrót do problemu.

### Pytanie do sali

Kto w waszej organizacji zgłasza zmiany do największej klasy w systemie? Ilu to różnych aktorów?

## Scena s08. OCP na wybranej osi - formaty seansu

**Temat ze slajdów:** 3.3 OCP: zamknięcie dla wybranej osi; nie każdy `switch` narusza OCP
**Pakiet:** `pl.training.workshop.m3.s08_ocp` · **Test:** `scripts/warsztat.sh test m3/s08`
**Czas:** ~10 min

### W skrócie

**Co robimy:** Wiedza o formatach seansu jest rozsiana po trzech `switch` na napisach, więc nowy format wymaga edycji wielu miejsc. Zamieniamy napisy na enum, przenosimy do niego dane formatów i dodajemy 4DX jedną linią.

**Zasada:** OCP to możliwość rozszerzenia wybranego zachowania bez modyfikowania stabilnej części. Zamyka się kod na jedną, realnie rosnącą oś zmian, nie na wszystkie naraz, a nie każdy `switch` łamie OCP - na enumie bez `default` bywa dobrym modelem zamkniętego zbioru.

**Efekt:** Dodanie formatu to zmiana tylko w `Format`, a `ScreeningOffer` nie zmienia się ani o znak. Na inne osie, na przykład nową dopłatę, klasa nadal nie jest zamknięta - i nie musi.

### Co widzimy

Wiedza o formatach (2D, 3D, IMAX) jest rozsiana po trzech `switch` na stringu: cena bazowa, okulary 3D, etykieta. Kino kupuje salę 4DX - trzeba edytować każdy `switch`, a zapomniany trafi do `default`.

```java
BigDecimal glasses = switch (format) {
    case "3D" -> ownGlasses ? BigDecimal.ZERO : new BigDecimal("3.00");
    default -> BigDecimal.ZERO;
};
```

### Krok 1: Replace Type Code with Enum

**W IDE:** utwórz `enum Format { TWO_D("2D"), THREE_D("3D"), IMAX("IMAX") }` z `parse(String)`; w `ScreeningOffer` przełącz `switch` na enum i usuń `default`.
**Po:**

```java
BigDecimal glasses = switch (format) {
    case THREE_D -> ownGlasses ? BigDecimal.ZERO : new BigDecimal("3.00");
    case TWO_D, IMAX -> BigDecimal.ZERO;
};
```

**Uruchom:** `scripts/warsztat.sh test m3/s08` - zielono.
**Co powiedzieć:** to jeszcze nie OCP, ale już bezpieczny, zamknięty zbiór: nowa stała enuma to błąd kompilacji w każdym `switch` bez `default`. Dla małego, stabilnego zbioru to często wystarcza.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m3/s08 0 1`

### Krok 2: Dane zamiast gałęzi - wiedza przeniesiona do enuma

**W IDE:** dodaj do `Format` pola `basePrice`, `needsGlasses`, `label` (Introduce Field w konstruktorze enuma); w `ScreeningOffer` zastąp każdy `switch` wywołaniem akcesora (Replace Conditional with Polymorphism w wersji "dane").
**Po:**

```java
public BigDecimal price(String code, boolean ownGlasses) {
    Format format = Format.parse(code);
    BigDecimal glasses = format.needsGlasses() && !ownGlasses ? GLASSES : BigDecimal.ZERO;
    return format.basePrice().add(glasses);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** `ScreeningOffer` jest zamknięta na oś "format seansu". Nie jest zamknięta na inne osie (nowa dopłata za fotel premium ją zmieni) - i nie musi.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m3/s08 1 2`

### Krok 3: Nowy format 4DX - jedna linia

**W IDE:** w `Format` dopisz `FOUR_DX("4DX", new BigDecimal("45.00"), true, "4DX - ruchome fotele")`.
**Po:**

```java
IMAX("IMAX", new BigDecimal("40.00"), false, "IMAX - ekran laserowy"),
FOUR_DX("4DX", new BigDecimal("45.00"), true, "4DX - ruchome fotele");
```

**Uruchom:** test zielony; `S08NewFormatTest` sprawdza 4DX: 48.00 bez własnych okularów, 45.00 z własnymi.
**Co powiedzieć:** `scripts/warsztat.sh diff m3/s08 2 3` pokazuje zmianę tylko w enumie - `ScreeningOffer` nie zmieniła się ani o znak. To jest OCP: rozszerzenie wybranego zachowania bez modyfikacji stabilnej części.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m3/s08 2 3`

### Rozwiązanie i uzasadnienie

Formaty to dane, więc enum z polami jest prostszy niż hierarchia klas. Polimorfizm klas opłaciłby się, gdyby formaty miały różne algorytmy (np. dynamiczną cenę 4DX), a nie różne liczby.

### Pułapki

- "Każdy `switch` łamie OCP" - `switch` na enumie bez `default` to często dobry model zamkniętego zbioru.
- Próba zamknięcia klasy na wszystkie osie naraz: interfejsy dla dopłat, formatów, typów biletów "na zapas".

### Pytanie do sali

Która oś zmian w waszym systemie rośnie najszybciej? Czy kod jest zamknięty właśnie na nią?

## Scena s09. LSP i test kontraktowy

**Temat ze slajdów:** 3.4 LSP: substytucja behawioralna; `UnsupportedOperationException` nie zawsze łamie LSP - ocena zaczyna się od kontraktu
**Pakiet:** `pl.training.workshop.m3.s09_lsp` · **Test:** `scripts/warsztat.sh test m3/s09`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `ReadOnlyHall` dziedziczy po `Hall`, ale na `reserve` rzuca wyjątek, więc łamie kontrakt sali bazowej. Wydzielamy interfejs odczytu `SeatMap` i zastępujemy dziedziczenie delegacją.

**Zasada:** LSP wymaga, by podtyp dało się podstawić za typ bazowy bez zmiany zachowania: nie może wzmacniać warunków wstępnych ani osłabiać końcowych. Kompilator tego nie sprawdzi, dlatego ten sam test kontraktowy uruchamia się dla każdej implementacji, a `UnsupportedOperationException` ocenia się względem kontraktu typu bazowego.

**Efekt:** Sala archiwalna spełnia tylko kontrakt odczytu, który naprawdę obiecuje, a przekazanie jej do kasy kończy się błędem kompilacji zamiast wyjątkiem w runtime. Test daje dowody zgodności dla sprawdzonych stanów, nie formalny dowód.

### Co widzimy

`Hall` ma udokumentowany kontrakt `reserve`: wolne miejsce zostaje zajęte, `freeSeats()` maleje o 1, zajęte miejsce daje `IllegalStateException`. Kontrakt nie przewiduje odmowy. `ReadOnlyHall extends Hall` (plan zamkniętego seansu dla raportów) nadpisuje `reserve` rzucając `UnsupportedOperationException` - wzmacnia warunek wstępny do "nigdy".

```java
public class ReadOnlyHall extends Hall {
    @Override
    public void reserve(int seat) {
        throw new UnsupportedOperationException("sala archiwalna - tylko do odczytu");
    }
}
```

Pokaż `S09ContractTest`: ten sam zestaw sprawdzeń (`obeysReserveContract`) uruchamiany dla każdej implementacji. `readOnlyHallAsSubclassBreaksTheReserveContract` dokumentuje, że sala archiwalna go łamie.

### Krok 1: Extract Interface - rola odczytu

**W IDE:** na `Hall` Refactor > Extract Interface, nazwa `SeatMap`, metody `isFree`, `freeSeats`, `capacity`, zaznacz "Use interface where possible" - `OccupancyReport.describe` przyjmie `SeatMap`.
**Po:**

```java
public interface SeatMap {
    boolean isFree(int seat);
    int freeSeats();
    int capacity();
}
```

**Uruchom:** `scripts/warsztat.sh test m3/s09` - zielono; kontrakt odczytu (`everySeatMapObeysTheReadContract`) spełniają obie sale.
**Co powiedzieć:** sala archiwalna jest doskonałym `SeatMap` - problemem nie jest ona, tylko dziedziczenie po typie, którego kontraktu nie spełnia.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m3/s09 0 1`

### Krok 2: Replace Inheritance with Delegation

**W IDE:** na `ReadOnlyHall` Refactor > Replace Inheritance with Delegation (pole `snapshot`, delegowane tylko metody `SeatMap`), zmień `extends Hall` na `implements SeatMap`. Konstruktor rezerwuje zajęte miejsca w prywatnej kopii. W `Hall` usuń chroniony konstruktor i dodaj `final`.
**Po:**

```java
public final class ReadOnlyHall implements SeatMap {
    private final Hall snapshot;

    public ReadOnlyHall(int capacity, Set<Integer> taken) {
        this.snapshot = new Hall(capacity);
        taken.forEach(snapshot::reserve);
    }
```

**Uruchom:** test zielony. Spróbuj na żywo przekazać `ReadOnlyHall` do `BoxOffice.sell` - kompilator odmówi.
**Co powiedzieć:** zamiast wyjątku w runtime mamy błąd kompilacji. Test kontraktowy `Hall` nie ma już komu się nie udać, bo sala archiwalna nie obiecuje rezerwacji.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m3/s09 1 2`

### Rozwiązanie i uzasadnienie

Dwie role, dwa kontrakty, dwa testy kontraktowe. Test daje dowody zgodności dla sprawdzonych stanów, nie formalny dowód LSP. Alternatywa równie poprawna: osłabić kontrakt bazowy (np. `boolean canReserve()` w kontrakcie) - wtedy `UnsupportedOperationException` przestaje łamać LSP, ale każdy klient musi obsłużyć odmowę.

### Pułapki

- "Kompiluje się, więc jest podstawialne" - kompilator nie sprawdza kontraktów domenowych.
- Test kontraktowy uruchamiany tylko dla jednej implementacji.
- `instanceof ReadOnlyHall` w kasie jako "naprawa".

### Pytanie do sali

Które wasze klasy nadpisują metodę bazową tylko po to, żeby rzucić wyjątek? Jaki jest kontrakt typu bazowego i gdzie jest zapisany?

## Scena s10. ISP z perspektywy klienta

**Temat ze slajdów:** 3.5 ISP: interfejs według ról klientów; 4.2 Interfejs nie usuwa sprzężenia
**Pakiet:** `pl.training.workshop.m3.s10_isp` · **Test:** `scripts/warsztat.sh test m3/s10`
**Czas:** ~8 min

### W skrócie

**Co robimy:** Gruby `CinemaAdminService` ma osiem metod, a każdy klient używa dwóch lub trzech - fake kasy musi implementować wszystkie. Wydzielamy interfejsy ról według klientów i usuwamy gruby interfejs.

**Zasada:** ISP mówi, że klient nie powinien zależeć od metod, których nie używa, więc interfejsy projektuje się według ról klientów. To nie znaczy "jedna metoda na interfejs" - rola może mieć kilka metod, jeśli jest spójna.

**Efekt:** Kasa zależy tylko od `TicketSales`, a jej fake ma dwie metody zamiast ośmiu. Sprzężenie z zapleczem nie znika - staje się zależnością od węższego, stabilniejszego kontraktu.

### Co widzimy

Gruby `CinemaAdminService` ma osiem metod. Kasa używa dwóch (`sellTicket`, `refundTicket`), raport dwóch, tablica seansów trzech, zmianę ceny woła tylko konfiguracja. Fake do testu kasy musi implementować wszystkie osiem.

```java
public interface CinemaAdminService {
    String sellTicket(String title, int seat);
    String refundTicket(String ticketId);
    BigDecimal dailyRevenue();
    int ticketsSold(String title);
    void scheduleScreening(String title, LocalTime start);
    void cancelScreening(String title);
    List<String> screenings();
    void updateTicketPrice(BigDecimal price);
}
```

### Krok 1: Extract Interface - po jednej roli na klienta

**W IDE:** na `CinemaAdminService` trzy razy Refactor > Extract Interface: `TicketSales`, `SalesFigures`, `ScreeningSchedule` (interfejs gruby rozszerza wszystkie trzy). W każdym kliencie zmień typ pola (⌘F6 w konstruktorze) na jego rolę.
**Po:**

```java
public interface CinemaAdminService extends TicketSales, SalesFigures, ScreeningSchedule {
    void updateTicketPrice(BigDecimal price);
}
```

```java
public final class CashDesk {
    private final TicketSales backOffice;
```

**Uruchom:** `scripts/warsztat.sh test m3/s10` - zielono; `S10ClientFakeTest` porównuje fake grubego interfejsu (8 metod) z fake roli (2 metody).
**Co powiedzieć:** role nazwaliśmy z perspektywy klienta, nie implementacji. Zmiana harmonogramu nie wymusza już rekompilacji kasy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m3/s10 0 1`

### Krok 2: Safe Delete grubego interfejsu

**W IDE:** w `InMemoryBackOffice` zmień `implements CinemaAdminService` na `implements TicketSales, SalesFigures, ScreeningSchedule`, usuń `@Override` z `updateTicketPrice`, Safe Delete (⌘⌦) na `CinemaAdminService`.
**Po:**

```java
public final class InMemoryBackOffice implements TicketSales, SalesFigures, ScreeningSchedule {
```

**Uruchom:** test zielony.
**Co powiedzieć:** zmiana ceny nie dostała interfejsu - jej jedynym klientem jest konfiguracja, więc interfejs niczego by nie chronił.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m3/s10 1 2`

### Rozwiązanie i uzasadnienie

Trzy spójne role, jedna implementacja. Interfejs nie usunął sprzężenia kasy z zapleczem - zastąpił je sprzężeniem z węższym, stabilniejszym kontraktem.

### Pułapki

- ISP jako "jedna metoda na interfejs" - `TicketSales` ma dwie metody, bo to jedna spójna rola.
- Interfejs dla każdej klasy "bo mock" - łatwość mockowania nie dowodzi dobrego projektu.

### Pytanie do sali

Który wasz przypadek użycia dostaje `CrudRepository` (albo podobny gruby typ), a używa jednej metody?

## Scena s11. DIP - kierunek zależności kontra przepływ sterowania

**Temat ze slajdów:** 3.6 DIP i DIP to nie dependency injection; 5.1-5.2 Reguła zależności a przepływ sterowania
**Pakiet:** `pl.training.workshop.m3.s11_dip` · **Test:** `scripts/warsztat.sh test m3/s11`
**Czas:** ~12 min

### W skrócie

**Co robimy:** Przypadek użycia `ConfirmReservation` sam tworzy klienta SMTP i zna protokół, więc import i wywołania biegną `app -> infra`. Wstrzykujemy zależność, nazywamy potrzebę polityki i wprowadzamy port `CustomerNotifier` z adapterem w `infra`.

**Zasada:** DIP mówi, że zależności źródłowe mają wskazywać od szczegółów ku polityce i abstrakcjom, a port nazywa potrzebę klienta, nie kształt technologii. DIP to nie dependency injection: wstrzyknięcie konkretnej klasy przez konstruktor wciąż wiąże politykę ze szczegółem.

**Efekt:** Sterowanie nadal płynie do `infra`, ale import odwrócił się na `infra -> app`, a politykę da się przetestować lambdą. Wystarczył ręczny `Main` jako composition root, bez kontenera DI.

### Co widzimy

Przypadek użycia `app.ConfirmReservation` sam tworzy klienta `infra.SmtpMailSender`, składa nagłówki MIME i interpretuje kody SMTP. Import i przepływ sterowania biegną w tę samą stronę: `app -> infra`. `Main` to composition root wariantu - test woła tylko `Main.confirmReservation()`.

```java
private final SmtpMailSender mail = new SmtpMailSender("smtp.kino.pl", 25);
...
String mime = "To: " + reservation.email() + "\r\nSubject: Rezerwacja\r\n\r\n" + message;
String reply = mail.send(reservation.email(), mime);
if (!reply.startsWith("250")) {
    throw new IllegalStateException("SMTP odrzucil: " + reply);
}
```

### Krok 1: Introduce Parameter - dependency injection

**W IDE:** na inicjalizatorze pola `mail` ⌥⌘P (Introduce Parameter do konstruktora). IntelliJ przeniesie `new SmtpMailSender("smtp.kino.pl", 25)` do `Main`.
**Po:**

```java
public ConfirmReservation(SmtpMailSender mail) {
    this.mail = mail;
}
```

**Uruchom:** `scripts/warsztat.sh test m3/s11` - zielono.
**Co powiedzieć:** to jest DI, ale jeszcze nie DIP - `import ...infra.SmtpMailSender` nadal stoi w polityce. Test `step1AndStep2PolicyDependsOnInfrastructure` to potwierdza.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m3/s11 0 1`

### Krok 2: Extract Method - nazwij potrzebę polityki

**W IDE:** zaznacz składanie MIME, `send` i sprawdzenie kodu, ⌥⌘M, nazwa `notifyCustomer(String email, String message)`.
**Po:**

```java
notifyCustomer(reservation.email(), message);
return "potwierdzono: " + reservation.email();
```

**Uruchom:** test zielony.
**Co powiedzieć:** tak wygląda port, zanim stanie się interfejsem: potrzeba nazwana w języku problemu ("powiadom klienta"), a cała technologia w jednym miejscu. Nie robimy Extract Interface z `SmtpMailSender` - dostalibyśmy port `send(to, mime)`, czyli kształt technologii, nie potrzeby.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m3/s11 1 2`

### Krok 3: Port po stronie polityki, adapter w infra

**W IDE:** utwórz w `app` interfejs `CustomerNotifier` z metodą `notifyCustomer`. Na `notifyCustomer` Refactor > Move (F6) do nowej klasy `infra.SmtpCustomerNotifier implements CustomerNotifier` (albo Extract Delegate i przeniesienie). W `ConfirmReservation` typ pola -> `CustomerNotifier`. W `Main` złóż `new ConfirmReservation(new SmtpCustomerNotifier(new SmtpMailSender(...)))`.
**Po:**

```java
public interface CustomerNotifier {           // app
    void notifyCustomer(String email, String message);
}

public final class SmtpCustomerNotifier implements CustomerNotifier {   // infra
```

**Uruchom:** test zielony; `stepsSendTheSameMimeMessage` potwierdza identyczny MIME, `step3PolicyIsTestableWithAHandWrittenFake` testuje politykę lambdą.
**Co powiedzieć:** sterowanie nadal płynie `app -> infra` (confirm woła notifier), ale zależność źródłowa odwróciła się: `infra -> app`. To jest DIP.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m3/s11 2 3`

### Rozwiązanie i uzasadnienie

Port należy do strony formułującej potrzebę. Adapter tłumaczy kody SMTP na błąd kontraktu (`IllegalStateException`), więc polityka nie zna protokołu. Kontener DI nie był potrzebny - wystarczył ręczny `Main`.

### Pułapki

- "Mamy `@Autowired`, więc mamy DIP" - krok 1 pokazuje, że nie.
- Port odbijający API technologii (`send(host, port, mime)`) - wymiana kanału na SMS i tak zmieni politykę.
- Wyjątek technologii (np. `SQLException`) w sygnaturze portu.

### Pytanie do sali

Narysujcie strzałki importów i strzałki wywołań dla jednego waszego przypadku użycia. W którym miejscu biegną w tę samą stronę przez granicę?

## Scena s12. Clean Architecture - use case, dane na granicy, composition root

**Temat ze slajdów:** 5.3 Elementy praktyczne; 5.4 Dane na granicy i composition root; 5.5-5.6 Kręgi to nie szablon; 7.8 Protokół efektów
**Pakiet:** `pl.training.workshop.m3.s12_cleanarchitecture` · **Test:** `scripts/warsztat.sh test m3/s12`
**Czas:** ~18 min

### W skrócie

**Co robimy:** `ReservationController` parsuje żądanie, liczy cenę, zapisuje wiersz i publikuje komunikat w jednej metodzie. W czterech krokach wydzielamy przypadek użycia, porty z adapterami, pakiety `app` i `adapter` oraz composition root.

**Zasada:** Clean Architecture to zasada, że polityka (przypadki użycia) nie zależy od mechanizmów: porty należą do strony formułującej potrzebę, granicę przekraczają proste rekordy, a composition root składa graf bez reguł biznesowych. Liczy się kierunek importów, a nie nazwy czterech folderów.

**Efekt:** Przypadek użycia testujemy bez HTTP i bazy, a kolejność zapis-powiadomienie jest jawną decyzją protokołu. Koszt to siedem typów więcej, uzasadniony dwoma realnymi efektami zewnętrznymi.

### Co widzimy

`ReservationController` obsługuje żądanie "HTTP" (`Map<String, String>`) i robi wszystko: parsuje parametry, liczy cenę (format + VIP), zapisuje wiersz `Object[]` w `RowStore`, publikuje komunikat w `Outbox`, buduje odpowiedź. `RowStore` i `Outbox` to stabilny "świat zewnętrzny" sceny. Test wchodzi przez `CinemaApplication.reservationController(db, outbox)` i porównuje odpowiedź, zapisane wiersze i komunikaty.

```java
String id;
try {
    id = db.insert(new Object[] {email, format, rows.size(), total});
} catch (IllegalStateException e) {
    return "503 " + e.getMessage();
}
outbox.publish("reservation-created", id + ";" + email + ";" + total);
return "201 " + id + " " + total;
```

### Krok 1: Extract Class - przypadek użycia jako jawna orkiestracja

**W IDE:** utwórz rekordy `BookSeatsCommand(email, format, rows)` i `Booking(id, total)`. Zaznacz w kontrolerze wycenę, zapis i publikację, ⌥⌘M `execute`, potem Move (F6) do nowej klasy `BookSeats`. Kontroler mapuje wyjątki: `IllegalArgumentException` -> 400, `IllegalStateException` -> 503.
**Po:**

```java
public Booking execute(BookSeatsCommand command) {
    if (command.rows().isEmpty()) {
        throw new IllegalArgumentException("brak miejsc");
    }
    BigDecimal total = price(command);
    String id = db.insert(
            new Object[] {command.email(), command.format(), command.rows().size(), total});
    outbox.publish("reservation-created", id + ";" + command.email() + ";" + total);
    return new Booking(id, total);
}
```

**Uruchom:** `scripts/warsztat.sh test m3/s12` - 22 testy zielone.
**Co powiedzieć:** przypadek użycia czyta się jak scenariusz: wyceń, zapisz, powiadom. Granicę przekraczają proste rekordy, nie `Map` z HTTP. Wciąż zna jednak kolumny tabeli i temat komunikatu.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m3/s12 0 1`

### Krok 2: Porty zdefiniowane przez potrzebę, adaptery

**W IDE:** utwórz `NewReservation` (rekord), porty `ReservationStore.save(NewReservation)` i `BookingNotifier.reservationCreated(id, reservation)`. Przenieś (ręcznie lub Extract Delegate) mapowanie na `Object[]` do `RowStoreReservationStore`, publikację do `OutboxBookingNotifier`. `BookSeats` przyjmuje porty.
**Po:**

```java
String id = store.save(reservation);
notifier.reservationCreated(id, reservation);
return new Booking(id, reservation.total());
```

**Uruchom:** test zielony, także przypadek "baza niedostępna - brak powiadomienia".
**Co powiedzieć:** kolejność zapis -> powiadomienie to decyzja protokołu: błąd zapisu oznacza brak powiadomienia. Kolejność nie daje atomowości - w systemie rozproszonym potrzebny byłby outbox transakcyjny.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m3/s12 1 2`

### Krok 3: Move Class - granica widoczna w pakietach

**W IDE:** F6 na `BookSeats`, `BookSeatsCommand`, `Booking`, `NewReservation`, `ReservationStore`, `BookingNotifier` -> pakiet `app`; F6 na kontrolerze i obu adapterach -> `adapter`. IntelliJ poprawi importy (także w `CinemaApplication`).
**Po:**

```text
step3/app/       BookSeats, BookSeatsCommand, Booking, NewReservation, ReservationStore, BookingNotifier
step3/adapter/   ReservationController, RowStoreReservationStore, OutboxBookingNotifier
```

**Uruchom:** test zielony.
**Co powiedzieć:** `app` nie importuje niczego z `adapter`; adaptery importują `app`. Nazwy pakietów są drugorzędne - liczy się kierunek importów.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m3/s12 2 3`

### Krok 4: Composition root

**W IDE:** w konstruktorze kontrolera zaznacz `new BookSeats(...)` i ⌥⌘P (Introduce Parameter) - tworzenie grafu przeniesie się do `CinemaApplication`. Usuń nieużywane parametry `db`, `outbox` z kontrolera (⌘F6).
**Po:**

```java
public static ReservationController reservationController(RowStore db, Outbox outbox) {
    BookSeats bookSeats = new BookSeats(
            new RowStoreReservationStore(db), new OutboxBookingNotifier(outbox));
    return new ReservationController(bookSeats);
}
```

**Uruchom:** test zielony; pokaż `S12UseCaseTest`: przypadek użycia z lambdami zamiast adapterów, w tym `doesNotNotifyWhenSavingFails`.
**Co powiedzieć:** composition root zna wszystkie konkrety i nie zawiera reguł biznesowych. Podmiana bazy to zmiana tylko tutaj. Kontener DI nie jest potrzebny.
**Snapshot:** `step4/` · **Różnica:** `scripts/warsztat.sh diff m3/s12 3 4`

### Rozwiązanie i uzasadnienie

Przypadek użycia testowany bez HTTP i bazy, adaptery wymienialne w jednym miejscu, dane na granicy jako rekordy. Koszt: siedem typów więcej. Uzasadnia go to, że dwa efekty zewnętrzne i protokół ich kolejności już istnieją (slajd 7.10).

### Pułapki

- Kopiowanie szablonu czterech folderów bez sprawdzenia kierunku importów.
- Osobny model na każdej granicy "bo tak trzeba" - `NewReservation` ma sens, bo oddziela semantykę od `Object[]`.
- Reguły biznesowe w composition root albo w kontrolerze (np. walidacja miejsc w kontrolerze).

### Pytanie do sali

Czy wasz przypadek użycia da się uruchomić w teście bez frameworka webowego i bazy? Co trzeba by przenieść, żeby się dało?

## Scena s13. Automatyczna ochrona granicy i diagnostyka spójności

**Temat ze slajdów:** 4.4 Diagnostyka spójności i sprzężenia; 4.1-4.3 Spójność, sprzężenie, koszt wydzielenia; lista kontrolna "reguła sprawdzana automatycznie"
**Pakiet:** `pl.training.workshop.m3.s13_boundarycheck` · **Test:** `scripts/warsztat.sh test m3/s13`
**Czas:** ~12 min

### W skrócie

**Co robimy:** Klasa domeny `ScreeningService` miesza politykę cenową z mapowaniem na wiersz bazy i importuje adapter oraz `java.sql`. Wydzielamy maper, przenosimy go do adaptera, a narzędzia `BoundaryRule` i `CohesionProbe` mierzą efekt po każdym kroku.

**Zasada:** Spójny moduł zmienia się z jednego powodu, a reguła zależności mówi, że domena nie importuje technologii - to dwa niezależne wymiary. Regułę architektury warto sprawdzać automatycznie przy każdym buildzie, a metryki takie jak LCOM4 traktować jako sygnał do rozmowy, nie wyrocznię.

**Efekt:** Domena jest wolna od `java.sql` i typów adaptera, a każda klasa ma LCOM4 równe 1. Skan importów jest słabą bramką - pełna nazwa klasy w kodzie przejdzie, więc silniejsze są ArchUnit, moduły Maven lub JPMS.

### Co widzimy

Dwa narzędzia bez bibliotek w pakiecie sceny: `BoundaryRule` (skanuje pliki `.java` w katalogu i zgłasza zakazane importy) oraz `CohesionProbe` (LCOM4: liczba grup metod połączonych wspólnym polem lub wywołaniem). Klasa domeny `ScreeningService` miesza politykę cenową z mapowaniem na wiersz bazy - importuje adapter i `java.sql`.

```java
import java.sql.Timestamp;

import pl.training.workshop.m3.s13_boundarycheck.start.adapter.ScreeningRow;

public final class ScreeningService {
    private final BigDecimal basePrice;
    private final BigDecimal morningDiscount;
    private final String table;
```

Uruchom test i pokaż wyjście konsoli: `s13 start - naruszenia granicy: [...]` i `LCOM4 ScreeningService: Result[lcom4=2, groups=[fromRow, toRow, isMorning, price]]`. Test startu działa jak "zamrożone naruszenia" (FreezingArchRule w ArchUnit): znane są tolerowane, każde nowe jest czerwone.

### Krok 1: Extract Class - spójność

**W IDE:** Refactor > Extract > Delegate na `toRow`, `fromRow` i polu `table`, klasa `ScreeningRowMapper` w tym samym pakiecie `domain`. Popraw `CinemaApp` (composition root), żeby tworzył obie klasy.
**Po:**

```java
public final class ScreeningRowMapper {    // wciąż w domain
    private final String table;
    public ScreeningRow toRow(Screening screening) { ... }
    public Screening fromRow(ScreeningRow row) { ... }
}
```

**Uruchom:** `scripts/warsztat.sh test m3/s13` - zielono; LCOM4 obu klas = 1, ale `step1StillViolatesTheBoundaryButOnlyInTheMapper` pokazuje, że naruszenie granicy tylko się przeniosło.
**Co powiedzieć:** spójność i kierunek zależności to dwa różne wymiary. Wydzielenie klasy nie naprawia granicy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m3/s13 0 1`

### Krok 2: Move Class do adaptera

**W IDE:** F6 na `ScreeningRowMapper`, pakiet `adapter`. IntelliJ doda import `domain.Screening` w maperze i poprawi `CinemaApp`.
**Po:**

```java
package pl.training.workshop.m3.s13_boundarycheck.step2.adapter;

import java.sql.Timestamp;

import pl.training.workshop.m3.s13_boundarycheck.step2.domain.Screening;
```

**Uruchom:** test zielony; `step2DomainIsFreeOfTechnology` - lista naruszeń pusta.
**Co powiedzieć:** zależność biegnie teraz `adapter -> domain`. Reguła jest sprawdzana automatycznie przy każdym buildzie, a nie tylko na code review.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m3/s13 1 2`

### Rozwiązanie i uzasadnienie

Domena bez `java.sql` i bez typów adaptera, każda klasa opisuje jedno pojęcie. `S13ToolsTest` sprawdza same narzędzia na stałych próbkach kodu.

### Pułapki

- `BoundaryRule` widzi tylko importy - pełna nazwa klasy w kodzie przejdzie. Silniejsze bramki: ArchUnit, osobne moduły Maven, JPMS (`module-info`), reguły w CI. Porównajcie ich siłę.
- LCOM4 to heurystyka na źródle (pola `private`, typy bez spacji, wcięcie 4 spacje) - sygnał do rozmowy, nie wyrocznia. Mała klasa nie musi być spójna, duża może być nierozdzielnym pojęciem.
- Wydzielenie klasy dodaje współpracownika i kontrakt - porównuj koszt zmiany przed i po (slajd 4.3).

### Pytanie do sali

Która reguła architektury w waszym projekcie istnieje tylko w głowach lub na wiki? Jak najtaniej zamienić ją w test?

## Scena s14. Wzorzec jako decyzja odwracalna

**Temat ze slajdów:** 6.1-6.2 Wzorzec i refaktoryzacja w jego kierunku; 6.3 Strategy i Adapter; 6.4 Wzorzec można usunąć
**Pakiet:** `pl.training.workshop.m3.s14_reversiblepattern` · **Test:** `scripts/warsztat.sh test m3/s14`
**Czas:** ~12 min

### W skrócie

**Co robimy:** Rozliczenie z dystrybutorem ma dwa modele w jednym `switch`, w tym festiwalowy z obcego systemu w groszach. Wprowadzamy Strategy z Adapterem, a gdy umowy festiwalowe wygasają, usuwamy wariant i cały wzorzec.

**Zasada:** Wzorzec to odpowiedź na konkretne siły (tu: dwa istniejące warianty i obcy interfejs), a nie kod do skopiowania. Strategy opłaca się dla rodziny wymiennych algorytmów, Adapter dla realnego tłumaczenia obcego modelu, a refaktoryzacja od wzorca jest równie poprawna jak do niego.

**Efekt:** Zachowanie świadomie się zmienia: umowy FESTIVAL są teraz odrzucane, a model procentowy liczy tak samo na każdym etapie. Zostaje prosta metoda bez interfejsu i mapy, którą w razie potrzeby rozbudujemy tymi samymi krokami w przód.

### Co widzimy

Rozliczenie z dystrybutorem ma dwa istniejące modele: procentowy (tydzień 1: 50%, 2: 40%, dalej 35%, minimalna gwarancja 500.00) i festiwalowy (stawka z obcego systemu `FestivalTariffClient`, w groszach jako `long`). Oba siedzą w jednym `switch`, konwersja jednostek jest wpleciona w logikę rozliczeń.

```java
case "FESTIVAL" -> {
    long cents = festival.weeklyFeeInCents(deal.title(), week);
    return BigDecimal.valueOf(cents, 2);
}
```

### Krok 1: Replace Conditional with Strategy (i Adapter dla obcego klienta)

**W IDE:** utwórz interfejs `SettlementModel.payout(deal, week, ticketRevenue)`. Każdą gałąź `switch` przenieś (⌥⌘M, potem F6) do klasy: `PercentageModel` i `FestivalFeeAdapter` (opakowuje `FestivalTariffClient`, konwertuje grosze). `DistributorSettlement` wybiera model z mapy.
**Po:**

```java
private final Map<String, SettlementModel> models = Map.of(
        "PERCENT", new PercentageModel(),
        "FESTIVAL", new FestivalFeeAdapter(new FestivalTariffClient()));
```

**Uruchom:** `scripts/warsztat.sh test m3/s14` - zielono; `step1AdapterPaysTheFestivalFeeConvertedFromCents` sprawdza 300.00 i 150.00.
**Co powiedzieć:** uzasadnienie to dwa **istniejące** warianty z różnymi właścicielami i obcy interfejs. Adapter ma realną pracę (konwersja jednostek), nie jest pustym przekazaniem 1:1.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m3/s14 0 1`

### Krok 2: Wariant znika - Safe Delete adaptera

**W IDE:** umowy festiwalowe wygasły. Usuń wpis `"FESTIVAL"` z mapy, Safe Delete (⌘⌦) na `FestivalFeeAdapter`.
**Po:**

```java
private final Map<String, SettlementModel> models = Map.of(
        "PERCENT", new PercentageModel());
```

**Uruchom:** test zielony; `afterTheVariantIsGoneFestivalDealsAreRejected` - to świadoma zmiana zachowania, dlatego sprawdzana osobno, a nie testem równoważności.
**Co powiedzieć:** zostaje Strategy z jedną implementacją i mapą z jednym wpisem - klasyczny sygnał nadmiaru wzorca ze slajdu 6.4.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m3/s14 1 2`

### Krok 3: Refaktoryzacja od wzorca

**W IDE:** ⌥⌘N (Inline) na `PercentageModel.payout` w miejscu wywołania, usuń mapę, Safe Delete na `PercentageModel` i `SettlementModel`. Przenieś `MINIMUM_GUARANTEE` (F6 na stałej).
**Po:**

```java
public BigDecimal payout(Deal deal, int week, BigDecimal ticketRevenue) {
    if (!deal.model().equals("PERCENT")) {
        throw new IllegalArgumentException("nieznany model: " + deal.model());
    }
    int percent = week == 1 ? 50 : week == 2 ? 40 : 35;
```

**Uruchom:** test zielony - model procentowy rozlicza się tak samo przed wzorcem, ze wzorcem i po jego usunięciu.
**Co powiedzieć:** refaktoryzacja od wzorca jest równie poprawna jak do wzorca. Gdy wróci drugi model, przywrócimy Strategy tymi samymi krokami w przód.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m3/s14 2 3`

### Rozwiązanie i uzasadnienie

Wzorzec był odpowiedzią na konkretne siły (dwa warianty, obcy interfejs). Gdy siły zniknęły, zniknął też wzorzec. Test równoważności chroni oba kierunki.

### Pułapki

- Zostawienie interfejsu "bo może wróci" - to YAGNI w drugą stronę (s06).
- Adapter, który tylko przekazuje wywołania 1:1 bez tłumaczenia modelu, błędów czy jednostek.
- Dopasowanie obcego klienta na siłę do kontraktu, którego obietnic nie spełnia (np. determinizm przy wywołaniu sieciowym).

### Pytanie do sali

Który wzorzec w waszym kodzie przeżył powód, dla którego go wprowadzono? Co by kosztowało jego usunięcie?

## Scena s15. Inwarianty w modelu domeny

**Temat ze slajdów:** 7.4 Model domeny i DRY (obiekty pilnują inwariantów); 4.3 Dobry moduł ukrywa decyzję
**Pakiet:** `pl.training.workshop.m3.s15_invariants` · **Test:** `scripts/warsztat.sh test m3/s15`
**Czas:** ~8 min

### W skrócie

**Co robimy:** `Reservation` to anemiczny JavaBean, walidację robi tylko `BookingService`, więc import pliku partnera tworzy niepoprawne rezerwacje. Zamieniamy klasę na niezmienny rekord i przenosimy strażników do jego kompaktowego konstruktora.

**Zasada:** Inwariant to warunek, który obiekt spełnia przez całe życie, a jego naturalnym właścicielem jest sam model domeny. Pilnują go konstruktor i niezmienność, a nie każdy serwis z osobna; reguły wymagające danych spoza obiektu należą do przypadku użycia.

**Efekt:** Złej rezerwacji nie da się utworzyć żadną ścieżką - import stał się chroniony, choć go nie zmienialiśmy. Zachowanie importu świadomie się zmienia: błędny wiersz kończy się teraz wyjątkiem.

### Co widzimy

`Reservation` to anemiczny JavaBean z setterami. Walidację (e-mail z `@`, co najmniej jedno miejsce, kwota nieujemna) robi tylko `BookingService`. `ReservationImport` (plik partnera `email;miejsca;kwota`) tworzy model z pominięciem walidacji - i przepuszcza `jan-kino.pl;0;-5.00`.

```java
Reservation reservation = new Reservation();
reservation.setEmail(columns[0]);
reservation.setSeats(Integer.parseInt(columns[1]));
reservation.setTotal(new BigDecimal(columns[2]));
```

### Krok 1: Remove Setting Method i konwersja na record

**W IDE:** na `Reservation` ⌥⏎ "Convert to record class" (IntelliJ zamieni gettery na akcesory w użyciach), w obu serwisach zastąp settery wywołaniem konstruktora rekordu.
**Po:**

```java
public record Reservation(String email, int seats, BigDecimal total) {
}
```

**Uruchom:** `scripts/warsztat.sh test m3/s15` - zielono; `importWithoutInvariantsCreatesInvalidReservation` pokazuje, że import wciąż tworzy złą rezerwację.
**Co powiedzieć:** obiekt jest niezmienny i powstaje w całości w jednym wywołaniu - to warunek, żeby w ogóle dało się pilnować inwariantów.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m3/s15 0 1`

### Krok 2: Move - strażnicy do kompaktowego konstruktora

**W IDE:** wytnij trzy `if` z `BookingService.book` i wklej do kompaktowego konstruktora rekordu (IntelliJ: ⌥⏎ "Insert compact canonical constructor").
**Po:**

```java
public record Reservation(String email, int seats, BigDecimal total) {
    public Reservation {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("niepoprawny email: " + email);
        }
        // ... miejsca, kwota
    }
}
```

**Uruchom:** test zielony; ścieżka przez serwis daje te same komunikaty, a `step2ImportCannotCreateInvalidReservation` pokazuje, że import jest chroniony, choć go nie zmienialiśmy.
**Co powiedzieć:** inwariant ma jednego właściciela - model. To też DRY: reguła poprawności rezerwacji istnieje w jednym miejscu.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m3/s15 1 2`

### Rozwiązanie i uzasadnienie

Nie da się utworzyć rezerwacji w złym stanie, niezależnie od ścieżki (kasa, online, import). Serwis tylko koordynuje.

### Pułapki

- Walidacja w adnotacjach frameworka na DTO zamiast w modelu - omija ją każda ścieżka bez frameworka.
- Inwarianty, które wymagają danych spoza obiektu (np. dostępność miejsca) - to reguła przypadku użycia, nie konstruktora.

### Pytanie do sali

Ile ścieżek tworzy wasz najważniejszy obiekt domeny? Czy wszystkie przechodzą przez tę samą walidację?

## Scena s16. Sprzężenie protokołu - ukryta kolejność wywołań

**Temat ze slajdów:** 4.4 "Czy wywołania wymagają ukrytej kolejności?" - sprzężenie protokołu lub czasu
**Pakiet:** `pl.training.workshop.m3.s16_temporalcoupling` · **Test:** `scripts/warsztat.sh test m3/s16`
**Czas:** ~6 min

### W skrócie

**Co robimy:** `TicketPrinter` wymaga wywołania trzech setterów przed `print`, czego nie widać w typach - pominięcie kończy się `NullPointerException`. Przenosimy dane do parametrów `print`, a potem do rekordu `TicketRequest` z walidacją.

**Zasada:** Sprzężenie czasowe (protokołu) to ukryte wymaganie kolejności wywołań, o którym klient musi wiedzieć poza typami. Naprawia się je, zamieniając stan i kolejność na jawne dane wejściowe, które kompilator i konstruktor mogą sprawdzić.

**Efekt:** Drukarka jest bezstanowa i bezpieczna współbieżnie, a niekompletne dane są odrzucane w chwili tworzenia żądania. Klient zamiast trzech wywołań buduje jeden obiekt.

### Co widzimy

`TicketPrinter` wymaga wywołania `selectScreening`, `selectSeat` i `forBuyer` przed `print`. Nic w typach tego nie mówi: zapomniane wywołanie to `NullPointerException` w `print`, a drukarka współdzielona przez dwie kasy miesza dane klientów. `TicketDesk` (klient) zna ten protokół.

```java
printer.selectScreening(screening);
printer.selectSeat(seat);
printer.forBuyer(buyer);
return printer.print();
```

Na żywo: w `TicketDesk` zakomentuj `selectSeat` i uruchom test - `NullPointerException`. Cofnij.

### Krok 1: Change Signature - wszystko, czego potrzeba, w parametrach

**W IDE:** ⌘F6 na `print`: dodaj parametry `Screening screening`, `int seat`, `String buyer` (wartości domyślne w wywołaniu: pola). W `print` użyj parametrów, Safe Delete (⌘⌦) na polach i setterach; w `TicketDesk` usuń trzy wywołania.
**Po:**

```java
public String print(Screening screening, int seat, String buyer) {
```

**Uruchom:** `scripts/warsztat.sh test m3/s16` - zielono.
**Co powiedzieć:** protokół "najpierw ustaw, potem drukuj" stał się kontraktem sprawdzanym przez kompilator. Drukarka jest bezstanowa, więc bezpieczna współbieżnie.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m3/s16 0 1`

### Krok 2: Introduce Parameter Object

**W IDE:** zaznacz trzy parametry `print`, Refactor > Introduce Parameter Object, rekord `TicketRequest`; w kompaktowym konstruktorze `Objects.requireNonNull` dla seansu i kupującego.
**Po:**

```java
public record TicketRequest(Screening screening, int seat, String buyer) {
    public TicketRequest {
        Objects.requireNonNull(screening, "screening");
        Objects.requireNonNull(buyer, "buyer");
    }
}
```

**Uruchom:** test zielony; `step2RejectsIncompleteRequestAtCreation` - niekompletne żądanie jest odrzucane przy tworzeniu, a nie w `print`.
**Co powiedzieć:** błąd wychodzi tam, gdzie powstał, a nie trzy wywołania dalej.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m3/s16 1 2`

### Rozwiązanie i uzasadnienie

Sprzężenie czasowe zamienione na zależność od danych. To samo dotyczy protokołów efektów (s12): kolejność, która ma znaczenie, powinna być jawna i przetestowana.

### Pułapki

- Metoda `init()` wymagana przed użyciem obiektu - ten sam zapach w innej formie.
- Builder bez walidacji w `build()` - przenosi problem, nie usuwa go.

### Pytanie do sali

Które API w waszym systemie wymaga "najpierw X, potem Y"? Jak klient ma się o tym dowiedzieć?

## Proponowana kolejność pokazu

**Ścieżka krótka (~85 min):** s01 (DRY) -> s02 (podobieństwo) -> s06 (YAGNI) -> s07 (SRP) -> s09 (LSP) -> s11 (DIP) -> s14 (wzorzec odwracalny). Z s12 pokaż tylko `diff m3/s12 3 4` i `S12UseCaseTest` (5 min), jeśli zostanie czas.

**Ścieżka pełna (~170 min, z przerwą):**

1. DRY, KISS, YAGNI: s01, s02, s03, s04, s05, s06 (~60 min) - na koniec filtr decyzyjny ze slajdu 2.9 na przykładzie s06.
2. SOLID: s07, s08, s09, s10, s11 (~55 min).
3. Spójność, sprzężenie, granice: s15, s16, s13, s12 (~45 min).
4. Wzorce: s14 (~12 min) jako podsumowanie - wzorzec to kierunek serii refaktoryzacji, który można odwrócić.

Sceny s12 i s13 dobrze łączyć: po s12 zapytaj, jak upewnić się, że `app` nigdy nie zaimportuje `adapter`, i przejdź do s13.
