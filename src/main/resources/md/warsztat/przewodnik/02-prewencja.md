# Moduł 2. Prewencja - warsztat CineLegacy: przewodnik prowadzącego

Dziesięć małych scen w domenie kina CineLegacy pokazuje tematy z części II prezentacji („Zanim wprowadzisz wzorzec”) jako serie bezpiecznych zmian: bramki kompilatora i jakości jako najszybsze pętle informacji zwrotnej, odraczanie decyzji (YAGNI) i ich zapis (ADR z regułą sprawdzaną przez test), projekt, który hamuje degradację kodu (zasada pojedynczej odpowiedzialności, zasada odwrócenia zależności, granice czystej architektury, automatyczna kontrola granicy) oraz codzienną dbałość o kod (zasada harcerza, seria zmian gotowych do przeglądu). Każda scena ma kod wyjściowy (`start`) z zapachem lub pułapką i kompletne snapshoty po każdym kroku (`step1`, `step2`, ...). Prowadzący pracuje na żywo w `start`, po każdym kroku uruchamia test sceny i porównuje wynik ze snapshotem.

Sceny ilustrują sekcję 2 slajdów ("Zanim wprowadzisz wzorzec"): każdy temat ma własną, kilkuminutową scenę. Sceny są pokazami prowadzącego do wyboru - nie trzeba pokazywać wszystkich. Numery scen odpowiadają pakietom w kodzie i w tej kolejności sceny są opisane poniżej. Kolejność pokazu, zgodną z kolejnością slajdów, podaje sekcja „Proponowana kolejność pokazu” na końcu.

## Jak prowadzić pokaz

```bash
scripts/warsztat.sh list m2                 # sceny i kroki modułu 2
scripts/warsztat.sh test m2/s01             # testy jednej sceny (krótka nazwa wystarczy)
scripts/warsztat.sh test m2                 # wszystkie sceny modułu (153 testy)
scripts/warsztat.sh diff m2/s01 0 1         # co zmienia krok 1 względem start (0 = start)
scripts/warsztat.sh diff m2/s01 0 1 --word  # to samo, zmiany podświetlone w obrębie linii
scripts/warsztat.sh jump m2/s09 3           # przeskok: start = snapshot kroku 3
scripts/warsztat.sh next m2/s09             # następny krok do start + podsumowanie zmian
scripts/warsztat.sh next                    # kolejny krok tej samej sceny (scena zapamiętana)
scripts/warsztat.sh prev                    # krok wstecz
scripts/warsztat.sh status                  # który krok jest teraz w start
scripts/warsztat.sh reset m2/s09            # przywrócenie start z repozytorium
```

- Zasada pokazu: **jeden krok - jeden test - jedno zdanie komentarza**. Nie łącz kroków, nawet jeśli IDE pozwala. Zanim ruszysz kod, powiedz, jaki dowód da test po kroku - to w praktyce pętla informacji zwrotnej ze slajdu 2.1.
- **Krok po kroku bez numerów:** `scripts/warsztat.sh next` wstawia do `start` gotowy następny krok i wypisuje, co się zmieniło. `prev` cofa o krok, `status` mówi, który krok jest teraz w `start`. Skrypt pamięta ostatnią scenę, więc po pierwszym `next m2/sNN` wystarczy samo `next`. Ręczne zmiany w `start` zostają nadpisane - o to chodzi, gdy krok na żywo się rozjechał.
- Jeśli krok na żywo się nie uda albo brakuje czasu: `next` (albo `jump` do konkretnego snapshotu) i kontynuuj od następnego kroku. Po pokazie zawsze `reset`.
- Testy dotykają `start` wyłącznie przez API wspólne dla wszystkich kroków, więc kompilują się w każdym stanie pośrednim. Tam, gdzie refaktoryzacja zmienia konstruktory, scena ma punkt wejścia pełniący rolę composition root: `Main` (s08), `CinemaApplication` (s09), `CinemaApp` (s10). Kroki, które zmieniają konstruktor albo przenoszą klasę, aktualizują też te punkty wejścia - to część kroku.
- Część testów to nie tylko równoważność (`SNNEquivalenceTest`), ale też dowody strategii (`SNNSolutionTest`, `S08DependencyDirectionTest`, `S09UseCaseTest`, `S10ArchitectureTest`): wynik bramki, lista naruszeń ADR, kierunek zależności. Testy, które **dokumentują stan startowy** (`startViolatesBothRules` w s02, `startFailsTheGateWithFourKindsOfWarnings` w s03, `startPassesEverythingBecauseItChecksNothing` w s04, `startDomainHasNoViolationsBeyondTheFrozenOnes` w s10), zmienią kolor na czerwony, gdy wykonasz kroki na pakiecie `start` - to znak, że start jest już naprawiony. Pułapki, które znikają po naprawie, testy dokumentują na kopii w kroku, który jeszcze je ma (np. s05 krok 1 - antyprzykład nadużycia Boy Scout).
- Sceny s02, s03, s04 i s10 czytają pliki źródłowe sceny. Testy uruchamiaj z katalogu głównego repozytorium (tak działa `warsztat.sh` i `mvn test`).
- Sceny s03 i s04 celowo zawierają kod z ostrzeżeniami kompilatora (`start` i `sample/dirty`) - to materiał dla bramek, nie niedoróbka.

## Mapa scen

| Scena | Temat ze slajdów | Kroki | Pakiet | Czas |
| --- | --- | --- | --- | --- |
| s01 | 2.3. YAGNI: buduj dla dzisiejszej potrzeby; 2.4. Pięć pytań przed dodaniem abstrakcji | 3 | `m2.s01_yagni` | ~12 min |
| s02 | 2.5. ADR: zapisz decyzję, a najlepiej ją sprawdzaj | 2 | `m2.s02_adr` | ~10 min |
| s03 | 2.1. Małe kroki i szybka informacja zwrotna; 2.2. Bramki jakości w starym kodzie: stan bazowy i zapadka | 3 | `m2.s03_compilergate` | ~10 min |
| s04 | 2.2. Bramki jakości w starym kodzie: stan bazowy i zapadka | 4 | `m2.s04_qualitygate` | ~12 min |
| s05 | 2.9. Zasada harcerza i małe porcje zmian | 2 | `m2.s05_boyscout` | ~8 min |
| s06 | 2.9. Zasada harcerza i małe porcje zmian | 3 | `m2.s06_reviewableseries` | ~10 min |
| s07 | 2.6. Zasada pojedynczej odpowiedzialności: jeden aktor, jeden powód zmiany | 3 | `m2.s07_srp` | ~12 min |
| s08 | 2.7. Zasada odwrócenia zależności: liczy się kierunek importów; 2.8. Reguła zależności, porty i korzeń kompozycji | 3 | `m2.s08_dip` | ~12 min |
| s09 | 2.8. Reguła zależności, porty i korzeń kompozycji | 4 | `m2.s09_cleanarchitecture` | ~18 min |
| s10 | 2.8. Reguła zależności, porty i korzeń kompozycji (sprawdzana automatycznie) | 2 | `m2.s10_boundarycheck` | ~12 min |

## Scena s01. YAGNI - silnik reguł dla dwóch reguł

**Temat ze slajdów:** 2.3. YAGNI: buduj dla dzisiejszej potrzeby; 2.4. Pięć pytań przed dodaniem abstrakcji
**Pakiet:** `pl.training.workshop.m2.s01_yagni` · **Test:** `scripts/warsztat.sh test m2/s01`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `TicketPricer` to silnik z rejestrem pluginów, konfiguracją napisem i kontekstem `Map` - dla dokładnie dwóch reguł. W trzech krokach usuwamy rejestr, zastępujemy mapę typowanymi danymi i wklejamy reguły do jednej klasy.

**Zasada:** YAGNI mówi, żeby nie budować zdolności potrzebnej tylko dla przewidywanego wymagania. Nie zabrania testów, refaktoryzacji ani nazwanych metod - to one pozwalają bezpiecznie odłożyć abstrakcję do chwili, gdy pojawi się realna potrzeba. To praktyczna wersja ostatniego odpowiedzialnego momentu: decyzję o mechanizmie rozszerzeń odkładamy, dopóki nie znamy jego kształtu, a usunięcie spekulacji jest drzwiami dwukierunkowymi.

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

**Zmiany:**

1. W `TicketPricer` usuń pola `registry` i `activeRules` oraz oba konstruktory; w ich miejsce dodaj pole `rules` z listą `List.of(new MorningRule(), new VipRule())` i iteruj w pętli po `rules` (Inline Class).
2. Usuń klasę `RuleRegistry` - nikt jej już nie woła.
3. Usuń metodę `priority()` z interfejsu `PricingRule` i z obu reguł - była potrzebna tylko do sortowania w rejestrze.

**Przed:**

```java
private final RuleRegistry registry;
private final String activeRules;

public TicketPricer() {
    this(RuleRegistry.withDefaults(), "morning,vip");
}

public TicketPricer(RuleRegistry registry, String activeRules) {
    this.registry = registry;
    this.activeRules = activeRules;
}
...
for (PricingRule rule : registry.resolve(activeRules)) {
```

**Po:**

```java
private final List<PricingRule> rules = List.of(new MorningRule(), new VipRule());
```

**Uruchom:** `scripts/warsztat.sh test m2/s01` - 16 testów zielonych.
**Co powiedzieć:** konfiguracja napisem nie miała żadnego klienta. Teraz literówka w nazwie reguły nie skompiluje się.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m2/s01 0 1`

### Krok 2: Change Signature - typowane dane zamiast mapy

**Zmiany:**

1. W interfejsie `PricingRule` zmień typ parametru `appliesTo` z `Map<String, Object>` na `TicketQuote`, a z `apply` usuń parametr kontekstu - zostaje samo `apply(BigDecimal price)` (Change Signature).
2. Popraw obie implementacje: rzutowania z mapy zastąp odczytami pól rekordu (`quote.start().getHour() < 12`, `quote.row() >= quote.vipFromRow()`).
3. W `TicketPricer.price` usuń budowanie mapy kontekstu i przekazuj `quote` wprost do `appliesTo`.

**Przed:**

```java
@Override
public boolean appliesTo(Map<String, Object> context) {
    return (int) context.get("row") >= (int) context.get("vipFromRow");
}
```

**Po:**

```java
public boolean appliesTo(TicketQuote quote) {
    return quote.row() >= quote.vipFromRow();
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** generyczny kontekst to typowa spekulacja "reguła może potrzebować czegokolwiek". Kompilator znów pilnuje nazw pól.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m2/s01 1 2`

### Krok 3: Inline Class dla reguł, Safe Delete interfejsu

**Zmiany:**

1. Wklej ciała obu reguł do `TicketPricer`: pętlę po `rules` zastąp dwoma `if` z prywatnymi metodami `isMorning(quote)` i `isVip(quote)` (Inline Class).
2. Usuń klasy `MorningRule` i `VipRule` oraz interfejs `PricingRule` - nie mają już klientów; znika też pole `rules`.
3. Kwoty -5.00 i +10.00 wyciągnij do stałych `MORNING_DISCOUNT` i `VIP_SURCHARGE` (Extract Constant).
4. Usuń końcowe `setScale(2, RoundingMode.HALF_UP)` - wszystkie kwoty mają już dwa miejsca po przecinku.

**Przed:**

```java
private final List<PricingRule> rules = List.of(new MorningRule(), new VipRule());

public BigDecimal price(TicketQuote quote) {
    BigDecimal price = basePrice(quote.format());
    for (PricingRule rule : rules) {
        if (rule.appliesTo(quote)) {
            price = rule.apply(price);
        }
    }
    return price.setScale(2, RoundingMode.HALF_UP);
}
```

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
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m2/s01 2 3`

### Rozwiązanie i uzasadnienie

Przejdź z salą pięć pytań ze slajdu 2.4: aktualne wymaganie (2 reguły), nazwa w języku problemu (brak: "plugin", "registry"), czy upraszcza aktualny przypadek (nie), koszt usunięcia (niski - pokazaliśmy). Z pięciu klas została jedna.

### Pułapki

- "YAGNI, więc bez testów" - odwrotnie: testy i małe kroki pozwalają bezpiecznie odraczać decyzje.
- Usunięcie abstrakcji, która chroni aktualną, realną granicę (np. port do bazy) - to nie spekulacja.

### Pytanie do sali

Który mechanizm rozszerzeń w waszym systemie ma dziś jedną albo dwie implementacje? Jaki sygnał powiedziałby, że czas go wprowadzić ponownie?

## Scena s02. ADR i wykonywalny model decyzji

**Temat ze slajdów:** 2.5. ADR: zapisz decyzję, a najlepiej ją sprawdzaj
**Pakiet:** `pl.training.workshop.m2.s02_adr` · **Test:** `scripts/warsztat.sh test m2/s02`
**Czas:** ~10 min

### W skrócie

**Co robimy:** ADR-0007 mówi, że cennik nie zależy od `notification` i nie używa `double`, a cennik w `start` sam wysyła mail o rabacie grupowym i liczy w `double`. Cennik zaczyna zwracać `Quote` z informacją o rabacie, mail wysyła `BookingService`, a kwoty przechodzą na `Money`.

**Zasada:** ADR zapisuje decyzję o trwałym wpływie: kontekst, opcje, decyzję i konsekwencje - to dokument historyczny, którego się nie edytuje, tylko zastępuje nowym. Regułę z ADR warto uczynić wykonywalną, żeby build pilnował jej na co dzień. Nie pisze się ADR dla każdego Rename.

**Efekt:** `ArchitectureRules` zwraca pustą listę naruszeń, a maile i odpowiedzi są takie same jak przed zmianą. Reguła tekstowa ma swoje granice (pełna nazwa klasy bez importu albo refleksja ją ominą) i ADR opisuje to w konsekwencjach.

### Co widzimy

Obok kodu sceny leży `ADR-0007-cennik-jako-czysty-modul.md` (status: Zaakceptowana) z dwiema regułami: **R1** pakiet `pricing` nie zależy od `notification`, **R2** `pricing` nie używa `double`. `ArchitectureRules` to wykonywalny model tych reguł. W `start` cennik sam wysyła mail o rabacie grupowym i liczy w `double`:

```java
import pl.training.workshop.m2.s02_adr.start.notification.GroupMailer;
...
public double total(String organizer, int tickets, double unitPrice) {
    ...
    mailer.groupDiscountGranted(organizer, tickets);
```

### Krok 1: spełnienie R1 - wynik zamiast efektu

**Zmiany:**

1. W pakiecie `pricing` dodaj rekord `Quote(double total, boolean groupDiscount)` - wynik cennika niesie informację o rabacie zamiast efektu ubocznego.
2. Zmień sygnaturę `TicketPricing.total`: usuń parametr `organizer`, metoda zwraca `Quote` zamiast `double` (Change Signature).
3. Usuń z `TicketPricing` pole `mailer`, konstruktor i wywołanie `mailer.groupDiscountGranted(...)` - razem z importem pakietu `notification`.
4. W `BookingService` odbierz `Quote` i wyślij mail tam, gdy `quote.groupDiscount()` jest prawdą.

**Przed:**

```java
import pl.training.workshop.m2.s02_adr.start.notification.GroupMailer;

public final class TicketPricing {
    private final GroupMailer mailer;
    ...
    public double total(String organizer, int tickets, double unitPrice) {
        double sum = unitPrice * tickets;
        if (tickets >= 10) {
            sum = sum - sum * 0.10;
            mailer.groupDiscountGranted(organizer, tickets);
        }
        return Math.round(sum * 100) / 100.0;
    }
}
```

**Po:**

```java
Quote quote = pricing.total(tickets, unitPrice);
if (quote.groupDiscount()) {
    mailer.groupDiscountGranted(organizer, tickets);
}
```

**Uruchom:** test zielony; `step1RemovesDependencyOnNotification` - zostało tylko naruszenie `ADR-0007/R2`.
**Co powiedzieć:** naruszenie w teście prowadzi wprost do identyfikatora decyzji i jej uzasadnienia.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m2/s02 0 1`

### Krok 2: spełnienie R2 - Money

**Zmiany:**

1. W `Quote` i `TicketPricing` zamień typ kwot `double` na `Money`; arytmetyka przechodzi na `times`, `minus` i `percent`.
2. Próg i procent rabatu grupowego zapisz jako stałe `GROUP_SIZE` i `GROUP_DISCOUNT_PERCENT`.
3. W `BookingService` podawaj cenę jednostkową jako `Money.of("25.00")` itd. i wypisuj `quote.total()` bez `String.format`.

**Przed:**

```java
public Quote total(int tickets, double unitPrice) {
    double sum = unitPrice * tickets;
    boolean groupDiscount = tickets >= 10;
    if (groupDiscount) {
        sum = sum - sum * 0.10;
    }
    return new Quote(Math.round(sum * 100) / 100.0, groupDiscount);
}
```

**Po:**

```java
public Quote total(int tickets, Money unitPrice) {
    Money sum = unitPrice.times(tickets);
```

**Uruchom:** test zielony; `step2CompliesWithTheDecision` - pusta lista naruszeń.
**Co powiedzieć:** moduł jest zgodny z decyzją, a test pilnuje, żeby tak zostało.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m2/s02 1 2`

### Rozwiązanie i uzasadnienie

ADR opisuje kontekst, opcje i konsekwencje (historia), a `ArchitectureRules` sprawdza regułę w każdym buildzie. `adrDocumentsEveryExecutableRule` pilnuje, żeby każda reguła w kodzie miała opis w ADR.

### Pułapki

- ADR dla każdego Rename - dokumentuje się decyzje o trwałym wpływie.
- Edycja zaakceptowanego ADR zamiast nowego zapisu, który go zastępuje.
- Reguła tekstowa ma granice: pełna nazwa klasy bez importu albo refleksja ją ominą (opisane w "Konsekwencjach").

### Pytanie do sali

Co zrobić, gdy zespół chce złamać R2 dla jednego przypadku (np. statystyk)? Wyjątek w teście czy nowy ADR?

## Scena s03. Bramka kompilatora -Xlint:all -Werror

**Temat ze slajdów:** 2.1. Małe kroki i szybka informacja zwrotna; 2.2. Bramki jakości w starym kodzie: stan bazowy i zapadka
**Pakiet:** `pl.training.workshop.m2.s03_compilergate` · **Test:** `scripts/warsztat.sh test m2/s03`
**Czas:** ~10 min

### W skrócie

**Co robimy:** Kod `start` nie przechodzi bramki `--release 25 -Xlint:all -Werror`: surowe typy w `SeatMap`, przestarzałe `PriceTable.basePrice(int)` i celowy przelot w `switch`. Usuwamy ostrzeżenia po jednej kategorii na krok: typy generyczne, nowe przeciążenie, switch expression.

**Zasada:** Bramka kompilatora traktuje ostrzeżenia `-Xlint` jak błędy i zwraca strukturalną diagnostykę, więc test może wymagać zera ostrzeżeń albo braku nowych. W dużym legacy nie włącza się jej nagle: najpierw stan bazowy, potem "brak nowych naruszeń" i stopniowa redukcja, a tłumienie tylko wąskie i uzasadnione.

**Efekt:** Bramka przechodzi z zerem ostrzeżeń, a raport działa tak samo, z intencją "IMAX ma też Dolby" zapisaną wprost w `switch`. Przestarzała metoda zostaje w `PriceTable`, dopóki ktoś może ją wołać spoza repozytorium.

### Co widzimy

`CompilerGate` kompiluje wszystkie pliki wariantu przez `javax.tools` z `--release 25 -Xlint:all -Werror` i zwraca strukturalną diagnostykę (kategoria, plik, linia). Kod `start` nie przechodzi: `SeatMap` na surowych typach (rawtypes, unchecked), `OccupancyReport` woła przestarzałe `PriceTable.basePrice(int)` (deprecation) i celowo przelatuje z IMAX do 3D w `switch` (fallthrough).

```java
private final Map seatsByRow = new TreeMap();
...
case 3:
    features = features + "duzy ekran, ";
case 2:
    features = features + "dzwiek Dolby";
    break;
```

### Krok 1: typy generyczne

**Zmiany:**

1. W polu `seatsByRow` dodaj parametry typowe: `Map<Integer, List<String>>` i `new TreeMap<>()`.
2. W `take` zastąp parę `get`/`put` z rzutowaniem jednym wywołaniem `computeIfAbsent(row, r -> new ArrayList<>()).add(seat)`.
3. W `takenPerRow` usuń rzutowania - iteracja `seatsByRow.forEach((row, seats) -> result.put(row, seats.size()))`.

**Przed:**

```java
private final Map seatsByRow = new TreeMap();

public void take(String seat) {
    Integer row = Integer.valueOf(seat.substring(1));
    List seats = (List) seatsByRow.get(row);
    if (seats == null) {
        seats = new ArrayList();
        seatsByRow.put(row, seats);
    }
    seats.add(seat);
}
```

**Po:**

```java
private final Map<Integer, List<String>> seatsByRow = new TreeMap<>();
...
seatsByRow.computeIfAbsent(row, r -> new ArrayList<>()).add(seat);
```

**Uruchom:** `scripts/warsztat.sh test m2/s03` - zostają kategorie `deprecation`, `fallthrough`.
**Co powiedzieć:** jedna kategoria ostrzeżeń na krok - łatwy przegląd, łatwe wycofanie.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m2/s03 0 1`

### Krok 2: nowe API zamiast przestarzałego

**Zmiany:**

1. W `OccupancyReport.describe` w instrukcji `return` zamień argument `PriceTable.basePrice(format)` na istniejącą już zmienną `name` - wywołanie trafia do nowego przeciążenia `basePrice(String)`.
2. `PriceTable` zostaje bez zmian: przestarzałe `basePrice(int)` dalej istnieje, bo mogą je wołać klienci spoza repozytorium.

**Przed:**

```java
String name = format == 3 ? "IMAX" : format == 2 ? "3D" : "2D";
return name + " [" + features + "], cena " + PriceTable.basePrice(format)
        + " zl, zajete: " + map.takenPerRow();
```

**Po:**

```java
return name + " [" + features + "], cena " + PriceTable.basePrice(name)
        + " zl, zajete: " + map.takenPerRow();
```

**Uruchom:** test zielony; zostaje tylko `[fallthrough] OccupancyReport.java:13`.
**Co powiedzieć:** przestarzałą metodę usuniemy dopiero, gdy nikt jej nie woła - także poza repozytorium.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m2/s03 1 2`

### Krok 3: switch expression

**Zmiany:**

1. Zamień instrukcję `switch` z przelotem na wyrażenie `switch` z gałęziami strzałkowymi, przypisane do `features`; puste `features = ""` i `break` znikają.
2. Przelot zapisz wprost: gałąź `case 3 -> "duzy ekran, dzwiek Dolby"` zawiera pełny tekst dla IMAX.

**Przed:**

```java
String features = "";
switch (format) {
    case 3:
        features = features + "duzy ekran, ";
    case 2:
        features = features + "dzwiek Dolby";
        break;
    default:
        features = "standard";
}
```

**Po:**

```java
String features = switch (format) {
    case 3 -> "duzy ekran, dzwiek Dolby";
    case 2 -> "dzwiek Dolby";
    default -> "standard";
};
```

**Uruchom:** test zielony; `step3PassesTheGate` - zero ostrzeżeń, `-Werror` przechodzi.
**Co powiedzieć:** intencja "IMAX ma też Dolby" jest teraz w kodzie, a nie w kolejności `case`.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m2/s03 2 3`

### Rozwiązanie i uzasadnienie

Bramka zwraca strukturalne wyniki, więc test może wymagać "zero ostrzeżeń" albo "nie więcej niż stan bazowy". Ciekawostka z implementacji: z `-Werror` javac przerywa po fazie z pierwszym ostrzeżeniem (fallthrough z analizy przepływu by się nie pokazał), więc pełną listę zbieramy drugim przebiegiem bez `-Werror`.

### Pułapki

- Nagłe włączenie `-Werror` w dużym legacy blokuje zespół - najpierw stan bazowy i "brak nowych naruszeń".
- `@SuppressWarnings("all")` na klasie zamiast wąskiego wyłączenia z uzasadnieniem.
- `--release 25` nie kontroluje, którym JDK uruchamiany jest Maven w CI.

### Pytanie do sali

Jak wprowadzić tę bramkę w projekcie z 3000 ostrzeżeń, nie blokując nikogo od jutra?

## Scena s04. Minimalna bramka jakości jako kod

**Temat ze slajdów:** 2.2. Bramki jakości w starym kodzie: stan bazowy i zapadka
**Pakiet:** `pl.training.workshop.m2.s04_qualitygate` · **Test:** `scripts/warsztat.sh test m2/s04`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `QualityGate` w `start` to lista kontrolna w Javadocu i metoda, która zawsze zwraca pustą listę, więc przepuszcza brudną próbkę. Zamieniamy listę na cztery wykonywalne sprawdzenia: skan TODO i `System.out`, ostrzeżenia kompilatora, przybliżenie pokrycia i uruchomienie testów.

**Zasada:** Minimalna bramka jakości łączy kilka niezależnych sygnałów, bo każde narzędzie dowodzi czegoś innego. Wynik ma być deterministyczny i wskazywać miejsce, a bramka potrzebuje właściciela i nie może dawać fałszywych alarmów - inaczej zespół ją wyłączy.

**Efekt:** Bramka zgłasza problemy `sample/dirty`, zatrzymuje nieprzechodzący test z `sample/broken` i przepuszcza `sample/clean`. Sprawdzenie pokrycia to tylko przybliżenie ("czy jakiś test w ogóle woła metodę") i nie zastępuje pomiaru JaCoCo.

### Co widzimy

`start.QualityGate` to lista kontrolna w Javadocu i metoda `evaluate`, która zawsze zwraca pustą listę - przepuszcza próbkę `sample/dirty` (TODO, `System.out`, surowy typ, metody bez testu). Bramka działa na `GateInput`: katalog źródeł domeny, plik testu, kluczowa klasa i klasa testowa.

```java
public List<String> evaluate(GateInput input) {
    return List.of();
}
```

### Krok 1: skan źródeł - TODO i System.out

**Zmiany:**

1. W `QualityGate` dodaj prywatną metodę `scanSources(GateInput, List<String>)`: dla każdego pliku `.java` w katalogu źródeł czyta linie, a linię z `TODO`/`FIXME` albo `System.out`/`System.err` zamienia na wynik z nazwą pliku i numerem linii.
2. Dodaj pomocnicze metody `javaFiles(Path)` i `read(Path)` (lista plików `.java` i odczyt linii).
3. `evaluate` zbiera wyniki do listy `findings` i zwraca ją zamiast pustego `List.of()`.

**Przed:**

```java
public List<String> evaluate(GateInput input) {
    return List.of();
}
```

**Po:**

```java
if (line.contains("TODO") || line.contains("FIXME")) {
    findings.add("TODO " + where);
}
```

**Uruchom:** `scripts/warsztat.sh test m2/s04` - `TODO PriceTable.java:14`, `System.out PriceTable.java:23`.
**Co powiedzieć:** tanie, deterministyczne, z pozycją - od tego zaczynamy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m2/s04 0 1`

### Krok 2: ostrzeżenia kompilatora

**Zmiany:**

1. Dodaj metodę `compileSources(GateInput, List<String>)`: kompiluje źródła domeny przez `javax.tools` z opcjami `--release 25 -Xlint:all`, a każdą diagnostykę inną niż NOTE zamienia na wynik z plikiem, linią i kodem ostrzeżenia.
2. Dodaj pomocnicze `tempDir()` i `delete(Path)` - kompilacja pisze do katalogu tymczasowego, sprzątanego po zakończeniu.
3. W `evaluate` wywołaj `compileSources` przed `scanSources`.

**Przed:**

```java
public List<String> evaluate(GateInput input) {
    List<String> findings = new ArrayList<>();
    scanSources(input, findings);
    return findings;
}
```

**Po:**

```java
findings.add("kompilator " + Path.of(d.getSource().toUri()).getFileName() + ":"
        + d.getLineNumber() + " " + d.getCode());
```

**Uruchom:** test zielony; dochodzą dwa wyniki `compiler.warn.raw.class.use`.
**Co powiedzieć:** to ta sama idea co w s03, tu jako jeden z kilku sygnałów bramki.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m2/s04 1 2`

### Krok 3: pokrycie kluczowej klasy (przybliżenie)

**Zmiany:**

1. Dodaj stałą `PUBLIC_METHOD` - wyrażenie regularne wyłapujące nazwy publicznych metod.
2. Dodaj metodę `checkCoverage(GateInput, List<String>)`: dla każdej publicznej metody kluczowej klasy sprawdza, czy źródło testu zawiera wywołanie `.nazwa(`; brak wywołania to wynik "pokrycie ... bez testu".
3. W `evaluate` dołóż `checkCoverage` po `scanSources`.

**Przed:**

```java
public List<String> evaluate(GateInput input) {
    List<String> findings = new ArrayList<>();
    compileSources(input, findings);
    scanSources(input, findings);
    return findings;
}
```

**Po:**

```java
if (!test.contains("." + method.group(1) + "(")) {
    findings.add("pokrycie " + input.keyClass() + "." + method.group(1) + " bez testu");
}
```

**Uruchom:** test zielony; `vipSurcharge` i `lookupCount` bez testu.
**Co powiedzieć:** to nie zastępuje JaCoCo (`mvn verify` - plugin jest w pom.xml), ale łapie metodę, której żaden test nawet nie woła.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m2/s04 2 3`

### Krok 4: testy zielone

**Zmiany:**

1. Dodaj metodę `runTests(GateInput, List<String>)`: refleksją ładuje klasę testową, wybiera metody z adnotacją o prostej nazwie `Test` (JUnit albo dowolna inna), uruchamia każdą na świeżej instancji i porażkę (`InvocationTargetException`) zamienia na wynik z nazwą testu i typem wyjątku.
2. Gdy klasy testowej nie da się uruchomić, bramka zgłasza to jako wynik zamiast przepuszczać zmianę.
3. W `evaluate` wywołaj `runTests` jako pierwsze sprawdzenie.

**Przed:**

```java
public List<String> evaluate(GateInput input) {
    List<String> findings = new ArrayList<>();
    compileSources(input, findings);
    scanSources(input, findings);
    checkCoverage(input, findings);
    return findings;
}
```

**Po:**

```java
try {
    test.invoke(constructor.newInstance());
} catch (InvocationTargetException failure) {
    findings.add("test " + type.getSimpleName() + "." + test.getName() + " nie przechodzi: "
            + failure.getCause().getClass().getSimpleName());
}
```

**Uruchom:** test zielony; `step4AddsGreenTestsAndCatchesAFailingOne` - fikstura `sample/broken` (celowo nie JUnit, żeby nie psuć buildu) daje wynik `test PriceTableTest.vipSurchargeStartsAtRowNine nie przechodzi: AssertionError`.
**Co powiedzieć:** lista kontrolna jest w całości wykonywalna i ma ten sam wynik u każdego.
**Snapshot:** `step4/` · **Różnica:** `scripts/warsztat.sh diff m2/s04 3 4`

### Rozwiązanie i uzasadnienie

Bramka łączy kilka niezależnych sygnałów. `S04EquivalenceTest` pilnuje drugiej strony: czysta próbka przechodzi każdą wersję bramki (brak fałszywych alarmów).

### Pułapki

- Bramka, która nic nie sprawdza, a daje zielony znaczek.
- Przybliżenie pokrycia potraktowane jak pomiar.
- Bramka bez właściciela - każdy fałszywy alarm kończy się jej wyłączeniem.

### Pytanie do sali

Które z tych czterech sprawdzeń uruchomilibyście lokalnie przed commitem, a które tylko w CI?

## Scena s05. Boy Scout Rule - mała poprawa i jej nadużycie

**Temat ze slajdów:** 2.9. Zasada harcerza i małe porcje zmian
**Pakiet:** `pl.training.workshop.m2.s05_boyscout` · **Test:** `scripts/warsztat.sh test m2/s05`
**Czas:** ~8 min

### W skrócie

**Co robimy:** `TicketPrinter.print` i tak otwieramy dla nowej linii "Sala", a kod ma nazwy `s`, `d`, `x` i ręczne sklejanie listy miejsc. Najpierw pokazujemy antyprzykład "skoro już tu jestem", który po cichu zmienia wydruk, a potem robimy małą poprawę tylko w tej metodzie.

**Zasada:** Boy Scout Rule mówi: dotykany kod zostaw w nieco lepszym stanie - to heurystyka, nie licencja na przebudowę. Poprawa jest mała, lokalna, dotyczy kodu bieżącej zmiany i nie zmienia zachowania; zmiana reguły biznesowej pod etykietą sprzątania to nadużycie.

**Efekt:** Metoda ma czytelne nazwy, `String.join`, `StringBuilder` i `phoneLine`, a test równoważności potwierdza identyczny wydruk. Sortowanie miejsc czy małe litery w e-mailu mogą być dobrymi pomysłami, ale trafiają do osobnego commita jako świadoma zmiana zachowania.

### Co widzimy

`start.TicketPrinter.print` trzeba i tak otworzyć, bo w sprincie dochodzi linia "Sala". Kod ma nazwy `s`, `d`, `x`, konkatenację w pętli i ręczne sklejanie listy miejsc. Kusi, żeby "posprzątać wszystko".

```java
String x = "";
for (int i = 0; i < t.seats().size(); i++) {
    if (i > 0) {
        x = x + ", ";
    }
    x = x + t.seats().get(i);
}
```

### Krok 1: nadużycie - "skoro już tu jestem"

**Zmiany:**

1. Nie rób tego kroku ręcznie - wczytaj gotowy antyprzykład: `scripts/warsztat.sh jump m2/s05 1`.
2. W antyprzykładzie obok dobrych ruchów (nazwy, `StringBuilder`, `String.join`) przemycono trzy zmiany zachowania: miejsca są sortowane, e-mail przechodzi na małe litery, a linia "Tel" znika, gdy telefonu brak; dodatkowo kwotę formatuje `Money` zamiast `String.format`.

**Przed:**

```java
s = s + "Miejsca: " + x + "\n";
s = s + "Klient: " + t.email().trim() + "\n";
if (t.phone() != null) {
    s = s + "Tel: " + t.phone() + "\n";
} else {
    s = s + "Tel: -\n";
}
```

**Po:**

```java
List<String> sortedSeats = ticket.seats().stream().sorted().toList();
text.append("Klient: ").append(ticket.email().trim().toLowerCase(Locale.ROOT)).append('\n');
```

**Uruchom:** `scripts/warsztat.sh test m2/s05` - równoważność czerwona w trzech przypadkach (m.in. `A9, A10` staje się `A10, A9`). `S05SolutionTest.abusiveCleanupChangesBehaviourInThreeCases` dokumentuje, które.
**Co powiedzieć:** każda z tych zmian może być dobrym pomysłem, ale to decyzje biznesowe, nie sprzątanie. Cofamy: `scripts/warsztat.sh reset m2/s05`.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m2/s05 0 1`

### Krok 2: poprawna, mała poprawa

**Zmiany:**

1. Zacznij od stanu wyjściowego (`scripts/warsztat.sh reset m2/s05`) - krok 2 wychodzi ze `start`, nie z antyprzykładu.
2. Zmień nazwę parametru `t` na `ticket`; pomocnicze `s`, `d`, `x` znikają przy przebudowie (Rename).
3. Ręczną pętlę sklejającą miejsca zastąp `String.join(", ", ticket.seats())`.
4. Konkatenację w zmiennej `s` zastąp jednym łańcuchem wywołań `StringBuilder.append(...)`.
5. Warunek na telefon wydziel do metody `phoneLine(ticket)` zwracającej "Tel: ..." albo "Tel: -" (Extract Method).

**Przed:**

```java
public String print(Ticket t) {
    String s = "";
    s = s + "Film: " + t.title() + "\n";
    String d = t.start().toLocalDate() + " " + t.start().toLocalTime();
    s = s + "Seans: " + d + "\n";
    String x = "";
    for (int i = 0; i < t.seats().size(); i++) {
        if (i > 0) {
            x = x + ", ";
        }
        x = x + t.seats().get(i);
    }
    s = s + "Miejsca: " + x + "\n";
    ...
}
```

**Po:**

```java
.append("Miejsca: ").append(String.join(", ", ticket.seats())).append('\n')
.append("Klient: ").append(ticket.email().trim()).append('\n')
.append(phoneLine(ticket)).append('\n')
```

**Uruchom:** test zielony.
**Co powiedzieć:** tylko dotykana metoda, bez zmiany kontraktu, diff do przejrzenia w minutę. Nowa linia "Sala" to osobny commit (patrz s06).
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m2/s05 0 2`

### Rozwiązanie i uzasadnienie

Boy Scout Rule to heurystyka: mała, lokalna, weryfikowalna poprawa w kodzie bieżącej zmiany. Test różnicowy (start kontra krok) jest bezstronnym arbitrem, czy "sprzątanie" nie zmieniło zachowania.

### Pułapki

- Zmiana zachowania pod etykietą refaktoryzacji.
- Masowe formatowanie całego pliku razem z poprawką - recenzent nie znajdzie zmiany merytorycznej.
- "Sprzątanie" cudzego modułu bez uzgodnienia.

### Pytanie do sali

Posortowane miejsca są czytelniejsze. Jak wprowadzić tę zmianę uczciwie?

## Scena s06. Seria małych zmian gotowych do przeglądu

**Temat ze slajdów:** 2.9. Zasada harcerza i małe porcje zmian
**Pakiet:** `pl.training.workshop.m2.s06_reviewableseries` · **Test:** `scripts/warsztat.sh test m2/s06`
**Czas:** ~10 min

### W skrócie

**Co robimy:** `PriceList.price` ma dostać "Tani wtorek" (NORMAL -20% we wtorek), a zniżki są wplecione w jedną metodę i nie widzą daty. Zamiast jednego commita "porządki + tani wtorek" robimy serię: Extract Method, Change Signature, a dopiero potem nowa reguła.

**Zasada:** Dobry zestaw zmian ma jedną intencję, własny dowód i zielony build po integracji. Refaktoryzację oddziela się od zmiany funkcjonalnej, formatowanie od logiki, a diff automatyczny od ręcznego - najpierw ułatw zmianę, potem zrób łatwą zmianę.

**Efekt:** Dwa pierwsze commity dowodzi test równoważności, a trzeci świadomie zmienia zachowanie: test różnicowy pokazuje, że zmieniło się dokładnie 12 przypadków, wszystkie NORMAL we wtorek. Kosztem jest więcej commitów, ale recenzent sprawdza w każdym tylko jedną rzecz.

### Co widzimy

`start.PriceList.price` ma dostać promocję "Tani wtorek": bilet NORMAL -20% ceny bazowej we wtorek. Zniżki są wplecione w jedną metodę i zależą tylko od typu biletu, a nowa reguła potrzebuje daty. Pokusa: jeden commit "porządki + tani wtorek".

```java
BigDecimal d;
if (q.type().equals("STUDENT")) {
    d = p.multiply(new BigDecimal("0.25"));
} else if ...
```

### Krok 1: commit 1 - refaktoryzacja (Extract Method)

**Zmiany:**

1. Wybór ceny bazowej wydziel do metody `basePrice(String format)` (Extract Method).
2. Wybór zniżki wydziel do metody `discountPercent(String type)`, która zwraca procent zamiast kwoty (Extract Method).
3. Kwoty przejdź z `BigDecimal` na `Money`: `base.minus(base.percent(...))`, `Money.of("5.00")`, `Money.of("10.00")`; przy okazji zmień nazwę parametru `q` na `query` (Rename).

**Przed:**

```java
BigDecimal p;
if (q.format().equals("IMAX")) {
    p = new BigDecimal("40.00");
} else if (q.format().equals("3D")) {
    p = new BigDecimal("32.00");
} else {
    p = new BigDecimal("25.00");
}
BigDecimal d;
if (q.type().equals("STUDENT")) {
    d = p.multiply(new BigDecimal("0.25"));
} else if (q.type().equals("SENIOR")) {
    d = p.multiply(new BigDecimal("0.30"));
} else if (q.type().equals("CHILD")) {
    d = p.multiply(new BigDecimal("0.40"));
} else {
    d = BigDecimal.ZERO;
}
p = p.subtract(d);
```

**Po:**

```java
Money base = basePrice(query.format());
Money price = base.minus(base.percent(discountPercent(query.type())));
```

**Uruchom:** test zielony - równoważność na przypadkach, w tym wtorkowych.
**Co powiedzieć:** opis commita: "refaktoryzacja, bez zmiany zachowania, dowód: S06EquivalenceTest". Recenzent sprawdza tylko mechanikę.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m2/s06 0 1`

### Krok 2: commit 2 - refaktoryzacja przygotowawcza (Change Signature)

**Zmiany:**

1. Zmień sygnaturę `discountPercent`: zamiast `String type` metoda przyjmuje całe `TicketQuery query`, bo następna reguła potrzebuje daty (Change Signature).
2. W ciele metody dopasowuj `switch (query.type())`; wywołanie w `price` podaje `query` zamiast `query.type()`.

**Przed:**

```java
Money price = base.minus(base.percent(discountPercent(query.type())));
...
private static int discountPercent(String type) {
    return switch (type) {
        case "STUDENT" -> 25;
        case "SENIOR" -> 30;
        case "CHILD" -> 40;
        default -> 0;
    };
}
```

**Po:**

```java
private static int discountPercent(TicketQuery query) {
    return switch (query.type()) { ... };
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** "make the change easy, then make the easy change". Ten commit nadal nie zmienia zachowania.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m2/s06 1 2`

### Krok 3: commit 3 - zmiana zachowania

**Zmiany:**

1. Dodaj stałą `CHEAP_TUESDAY_PERCENT = 20` i metodę pomocniczą `isCheapTuesday(query)` sprawdzającą `DayOfWeek.TUESDAY`.
2. W gałęzi `default` metody `discountPercent` zwracaj `isCheapTuesday(query) ? CHEAP_TUESDAY_PERCENT : 0`.

**Przed:**

```java
private static int discountPercent(TicketQuery query) {
    return switch (query.type()) {
        case "STUDENT" -> 25;
        case "SENIOR" -> 30;
        case "CHILD" -> 40;
        default -> 0;
    };
}
```

**Po:**

```java
default -> isCheapTuesday(query) ? CHEAP_TUESDAY_PERCENT : 0;
```

**Uruchom:** test zielony; `S06SolutionTest.cheapTuesdayGivesNormalTicketTwentyPercentOff` (2D wtorek 20.00) i `behaviourChangeIsLimitedToNormalTicketsOnTuesday` - na siatce 336 przypadków zmieniło się dokładnie 12, wszystkie NORMAL we wtorek.
**Co powiedzieć:** diff to kilka linii. Test różnicowy commitu 2 kontra 3 mówi recenzentowi dokładnie, gdzie zmieniło się zachowanie.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m2/s06 2 3`

### Rozwiązanie i uzasadnienie

Trzy zestawy zmian, każdy z jedną intencją i zielonym buildem. Refaktoryzacje dowodzi test równoważności, zmianę zachowania - test nowej reguły i test różnicowy ograniczający jej zasięg.

### Pułapki

- Łączenie refaktoryzacji ze zmianą zachowania: recenzent nie odróżni ruchu mechanicznego od nowej reguły.
- Łączenie formatowania z logiką, diffu automatycznego z ręcznym.
- Commit przygotowawczy, który "przy okazji" poprawia regułę.

### Pytanie do sali

Jak opisalibyście commit 3 według pięciu pytań (dlaczego, co i czego nie, kontrakt, dowody, wdrożenie i wycofanie)?

## Scena s07. SRP - raport dla dwóch aktorów

**Temat ze slajdów:** 2.6. Zasada pojedynczej odpowiedzialności: jeden aktor, jeden powód zmiany
**Pakiet:** `pl.training.workshop.m2.s07_srp` · **Test:** `scripts/warsztat.sh test m2/s07`
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

**Zmiany:**

1. Blok oznaczony komentarzem `// księgowość` wydziel do metody `accountingSection(List<Sale> sales)` zwracającej `String`, z własnym `StringBuilder` (Extract Method).
2. To samo dla bloku `// marketing`: metoda `marketingSection(List<Sale> sales)`.
3. `render` skleja wyniki obu metod; komentarze znikają - zastąpiły je nazwy metod.

**Przed:**

```java
public String render(List<Sale> sales) {
    StringBuilder out = new StringBuilder();
    // księgowość
    BigDecimal tickets = new BigDecimal("0.00");
    BigDecimal bar = new BigDecimal("0.00");
    ...
    out.append("Razem brutto ").append(total).append('\n');
    // marketing
    out.append("MARKETING\n");
    ...
    return out.toString();
}
```

**Po:**

```java
public String render(List<Sale> sales) {
    return accountingSection(sales) + marketingSection(sales);
}
```

**Uruchom:** `scripts/warsztat.sh test m2/s07` - 12 testów zielonych (w tym remis tytułów i dzień bez sprzedaży).
**Co powiedzieć:** komentarze nazywały aktorów - teraz nazywają je metody. Helper `revenue` nadal wiąże oba światy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m2/s07 0 1`

### Krok 2: Rozdziel wspólny helper według aktora

**Zmiany:**

1. Skopiuj metodę `revenue` i nazwij kopię `popularity` - identyczne ciało, inny właściciel.
2. W `marketingSection` zamień wywołanie `revenue(sale)` na `popularity(sale)`.
3. Obu metodom dodaj Javadoc z nazwą aktora-właściciela.

**Przed:**

```java
byTitle.merge(sale.title(), revenue(sale), BigDecimal::add);
...
private BigDecimal revenue(Sale sale) {
    return sale.ticketRevenue().add(sale.barRevenue());
}
```

**Po:**

```java
/** Księgowość: przychód brutto seansu. */
private BigDecimal revenue(Sale sale) { ... }

/** Marketing: miara popularności filmu (dziś: bilety + bar). */
private BigDecimal popularity(Sale sale) { ... }
```

**Uruchom:** test zielony.
**Co powiedzieć:** świadomie powtarzamy kod, nie wiedzę - dziś formuły są równe przypadkiem. To lekcja z DRY (slajd 2.6: dwie identyczne dziś formuły to nie duplikacja wiedzy) w kontekście SRP.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m2/s07 1 2`

### Krok 3: Extract Class - klasa na aktora

**Zmiany:**

1. Utwórz klasę `AccountingSection` i przenieś do niej `accountingSection` (pod nazwą `render`) oraz `revenue` i `net` (Extract Class).
2. Utwórz klasę `MarketingSection` i przenieś do niej `marketingSection` (pod nazwą `render`) oraz `popularity`.
3. W `DailyReport` zostają tylko pola `accounting` i `marketing` oraz `render`, który skleja wyniki obu sekcji.

**Przed:**

```java
public final class DailyReport {
    public String render(List<Sale> sales) {
        return accountingSection(sales) + marketingSection(sales);
    }

    private String accountingSection(List<Sale> sales) { ... }

    private String marketingSection(List<Sale> sales) { ... }

    private BigDecimal revenue(Sale sale) { ... }

    private BigDecimal popularity(Sale sale) { ... }

    private BigDecimal net(BigDecimal gross, int vatPercent) { ... }
}
```

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
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m2/s07 2 3`

### Rozwiązanie i uzasadnienie

Odpowiedzialność = aktor i jego powód zmiany, nie "jedna rzecz". `AccountingSection` ma trzy metody i to jest w porządku: wszystkie zmieniają się dla księgowości.

### Pułapki

- Dzielenie na klasy "po jednej metodzie" zamiast według aktora.
- Zostawienie wspólnego helpera między nowymi klasami (np. w klasie `ReportUtils`) - powrót do problemu.

### Pytanie do sali

Kto w waszej organizacji zgłasza zmiany do największej klasy w systemie? Ilu to różnych aktorów?

## Scena s08. DIP - kierunek zależności kontra przepływ sterowania

**Temat ze slajdów:** 2.7. Zasada odwrócenia zależności: liczy się kierunek importów; 2.8. Reguła zależności, porty i korzeń kompozycji
**Pakiet:** `pl.training.workshop.m2.s08_dip` · **Test:** `scripts/warsztat.sh test m2/s08`
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

**Zmiany:**

1. W `ConfirmReservation` usuń inicjalizator pola `mail` - klasa dostaje `SmtpMailSender` przez nowy konstruktor (Introduce Parameter).
2. Tworzenie `new SmtpMailSender("smtp.kino.pl", 25)` przenieś do `Main`, który składa graf obiektów.

**Przed:**

```java
public final class ConfirmReservation {
    private final SmtpMailSender mail = new SmtpMailSender("smtp.kino.pl", 25);
...
public static ConfirmReservation confirmReservation() {    // Main
    return new ConfirmReservation();
}
```

**Po:**

```java
public ConfirmReservation(SmtpMailSender mail) {
    this.mail = mail;
}
```

**Uruchom:** `scripts/warsztat.sh test m2/s08` - zielono.
**Co powiedzieć:** to jest DI, ale jeszcze nie DIP - `import ...infra.SmtpMailSender` nadal stoi w polityce. Test `step1AndStep2PolicyDependsOnInfrastructure` to potwierdza.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m2/s08 0 1`

### Krok 2: Extract Method - nazwij potrzebę polityki

**Zmiany:**

1. Składanie wiadomości MIME, wywołanie `mail.send` i sprawdzenie kodu odpowiedzi wydziel do prywatnej metody `notifyCustomer(String email, String message)` (Extract Method).
2. `confirm` woła już tylko `notifyCustomer(reservation.email(), message)` - cała technologia siedzi w jednej metodzie.

**Przed:**

```java
String mime = "To: " + reservation.email() + "\r\nSubject: Rezerwacja\r\n\r\n" + message;
String reply = mail.send(reservation.email(), mime);
if (!reply.startsWith("250")) {
    throw new IllegalStateException("SMTP odrzucil: " + reply);
}
return "potwierdzono: " + reservation.email();
```

**Po:**

```java
notifyCustomer(reservation.email(), message);
return "potwierdzono: " + reservation.email();
```

**Uruchom:** test zielony.
**Co powiedzieć:** tak wygląda port, zanim stanie się interfejsem: potrzeba nazwana w języku problemu ("powiadom klienta"), a cała technologia w jednym miejscu. Nie robimy Extract Interface z `SmtpMailSender` - dostalibyśmy port `send(to, mime)`, czyli kształt technologii, nie potrzeby.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m2/s08 1 2`

### Krok 3: Port po stronie polityki, adapter w infra

**Zmiany:**

1. W pakiecie `app` utwórz interfejs `CustomerNotifier` z metodą `notifyCustomer(String email, String message)` (Extract Interface).
2. W `infra` utwórz klasę `SmtpCustomerNotifier implements CustomerNotifier` i przenieś do niej ciało `notifyCustomer` wraz z polem `mail` (Move Method).
3. W `ConfirmReservation` zmień typ pola i parametru konstruktora na `CustomerNotifier` - import `infra` znika z polityki.
4. W `Main` złóż graf: `new ConfirmReservation(new SmtpCustomerNotifier(new SmtpMailSender("smtp.kino.pl", 25)))`.

**Przed:**

```java
import pl.training.workshop.m2.s08_dip.step2.infra.SmtpMailSender;
...
private final SmtpMailSender mail;
...
private void notifyCustomer(String email, String message) {
    String mime = "To: " + email + "\r\nSubject: Rezerwacja\r\n\r\n" + message;
    String reply = mail.send(email, mime);
    if (!reply.startsWith("250")) {
        throw new IllegalStateException("SMTP odrzucil: " + reply);
    }
}
```

**Po:**

```java
public interface CustomerNotifier {           // app
    void notifyCustomer(String email, String message);
}

public final class SmtpCustomerNotifier implements CustomerNotifier {   // infra
```

**Uruchom:** test zielony; `stepsSendTheSameMimeMessage` potwierdza identyczny MIME, `step3PolicyIsTestableWithAHandWrittenFake` testuje politykę lambdą.
**Co powiedzieć:** sterowanie nadal płynie `app -> infra` (confirm woła notifier), ale zależność źródłowa odwróciła się: `infra -> app`. To jest DIP.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m2/s08 2 3`

### Rozwiązanie i uzasadnienie

Port należy do strony formułującej potrzebę. Adapter tłumaczy kody SMTP na błąd kontraktu (`IllegalStateException`), więc polityka nie zna protokołu. Kontener DI nie był potrzebny - wystarczył ręczny `Main`.

### Pułapki

- "Mamy `@Autowired`, więc mamy DIP" - krok 1 pokazuje, że nie.
- Port odbijający API technologii (`send(host, port, mime)`) - wymiana kanału na SMS i tak zmieni politykę.
- Wyjątek technologii (np. `SQLException`) w sygnaturze portu.

### Pytanie do sali

Narysujcie strzałki importów i strzałki wywołań dla jednego waszego przypadku użycia. W którym miejscu biegną w tę samą stronę przez granicę?

## Scena s09. Clean Architecture - use case, dane na granicy, composition root

**Temat ze slajdów:** 2.8. Reguła zależności, porty i korzeń kompozycji
**Pakiet:** `pl.training.workshop.m2.s09_cleanarchitecture` · **Test:** `scripts/warsztat.sh test m2/s09`
**Czas:** ~18 min

### W skrócie

**Co robimy:** `ReservationController` parsuje żądanie, liczy cenę, zapisuje wiersz i publikuje komunikat w jednej metodzie. W czterech krokach wydzielamy przypadek użycia, porty z adapterami, pakiety `app` i `adapter` oraz composition root.

**Zasada:** Clean Architecture to zasada, że polityka (przypadki użycia) nie zależy od mechanizmów: porty należą do strony formułującej potrzebę, granicę przekraczają proste rekordy, a composition root składa graf bez reguł biznesowych. Liczy się kierunek importów, a nie nazwy czterech folderów.

**Efekt:** Przypadek użycia testujemy bez HTTP i bazy, a kolejność zapis-powiadomienie jest jawną decyzją protokołu. Koszt to osiem typów więcej, uzasadniony dwoma realnymi efektami zewnętrznymi.

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

**Zmiany:**

1. Utwórz rekordy `BookSeatsCommand(email, format, rows)` i `Booking(id, total)` - proste dane przekraczające granicę.
2. Wycenę, zapis i publikację wydziel z kontrolera do metody `execute`, a metodę przenieś do nowej klasy `BookSeats` z polami `db` i `outbox` (Extract Method, Move Method).
3. Walidację "brak miejsc" przenieś do `BookSeats.execute` jako `IllegalArgumentException`.
4. Kontroler tylko tłumaczy: parametry na `BookSeatsCommand`, wynik na "201 ...", a wyjątki na kody: `IllegalArgumentException` -> 400, `IllegalStateException` -> 503.

**Przed:**

```java
BigDecimal total = new BigDecimal("0.00");
for (int row : rows) {
    total = total.add(base);
    if (row >= 10) {
        total = total.add(new BigDecimal("10.00"));
    }
}
String id;
try {
    id = db.insert(new Object[] {email, format, rows.size(), total});
} catch (IllegalStateException e) {
    return "503 " + e.getMessage();
}
outbox.publish("reservation-created", id + ";" + email + ";" + total);
return "201 " + id + " " + total;
```

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

**Uruchom:** `scripts/warsztat.sh test m2/s09` - 22 testy zielone.
**Co powiedzieć:** przypadek użycia czyta się jak scenariusz: wyceń, zapisz, powiadom. Granicę przekraczają proste rekordy, nie `Map` z HTTP. Wciąż zna jednak kolumny tabeli i temat komunikatu.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m2/s09 0 1`

### Krok 2: Porty zdefiniowane przez potrzebę, adaptery

**Zmiany:**

1. Dodaj rekord `NewReservation(email, format, seats, total)` - dane przekraczające granicę do zapisu.
2. Dodaj porty: interfejs `ReservationStore` z metodą `save(NewReservation)` i interfejs `BookingNotifier` z metodą `reservationCreated(id, reservation)` - nazwane potrzebą przypadku użycia.
3. Mapowanie na `Object[]` przenieś do adaptera `RowStoreReservationStore`, a publikację komunikatu do `OutboxBookingNotifier`.
4. `BookSeats` przyjmuje w konstruktorze porty zamiast `RowStore` i `Outbox`; adaptery składa na razie kontroler (do kroku 4).

**Przed:**

```java
BigDecimal total = price(command);
String id = db.insert(
        new Object[] {command.email(), command.format(), command.rows().size(), total});
outbox.publish("reservation-created", id + ";" + command.email() + ";" + total);
return new Booking(id, total);
```

**Po:**

```java
String id = store.save(reservation);
notifier.reservationCreated(id, reservation);
return new Booking(id, reservation.total());
```

**Uruchom:** test zielony, także przypadek "baza niedostępna - brak powiadomienia".
**Co powiedzieć:** kolejność zapis -> powiadomienie to decyzja protokołu: błąd zapisu oznacza brak powiadomienia. Kolejność nie daje atomowości - w systemie rozproszonym potrzebny byłby outbox transakcyjny.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m2/s09 1 2`

### Krok 3: Move Class - granica widoczna w pakietach

**Zmiany:**

1. Przenieś `BookSeats`, `BookSeatsCommand`, `Booking`, `NewReservation`, `ReservationStore` i `BookingNotifier` do nowego pakietu `app` (Move Class).
2. Przenieś `ReservationController`, `RowStoreReservationStore` i `OutboxBookingNotifier` do nowego pakietu `adapter` (Move Class).
3. Popraw importy u wszystkich klientów, także w `CinemaApplication` - adaptery importują `app`, nigdy odwrotnie.

**Przed:**

```text
step2/   BookSeats, BookSeatsCommand, Booking, NewReservation, ReservationStore,
         BookingNotifier, ReservationController, RowStoreReservationStore,
         OutboxBookingNotifier, CinemaApplication   (wszystko w jednym pakiecie)
```

**Po:**

```text
step3/app/       BookSeats, BookSeatsCommand, Booking, NewReservation, ReservationStore, BookingNotifier
step3/adapter/   ReservationController, RowStoreReservationStore, OutboxBookingNotifier
```

**Uruchom:** test zielony.
**Co powiedzieć:** `app` nie importuje niczego z `adapter`; adaptery importują `app`. Nazwy pakietów są drugorzędne - liczy się kierunek importów.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m2/s09 2 3`

### Krok 4: Composition root

**Zmiany:**

1. W konstruktorze kontrolera zastąp składanie `new BookSeats(...)` parametrem `BookSeats bookSeats` - tworzenie grafu przenosi się do `CinemaApplication` (Introduce Parameter).
2. Usuń z konstruktora kontrolera nieużywane już parametry `db` i `outbox` wraz z importami `RowStore` i `Outbox` (Change Signature).
3. `CinemaApplication.reservationController` składa cały graf: oba adaptery wyjściowe, przypadek użycia i kontroler.

**Przed:**

```java
public ReservationController(RowStore db, Outbox outbox) {
    this.bookSeats = new BookSeats(
            new RowStoreReservationStore(db), new OutboxBookingNotifier(outbox));
}
...
public static ReservationController reservationController(RowStore db, Outbox outbox) {
    return new ReservationController(db, outbox);                // CinemaApplication
}
```

**Po:**

```java
public static ReservationController reservationController(RowStore db, Outbox outbox) {
    BookSeats bookSeats = new BookSeats(
            new RowStoreReservationStore(db), new OutboxBookingNotifier(outbox));
    return new ReservationController(bookSeats);
}
```

**Uruchom:** test zielony; pokaż `S09UseCaseTest`: przypadek użycia z lambdami zamiast adapterów, w tym `doesNotNotifyWhenSavingFails`.
**Co powiedzieć:** composition root zna wszystkie konkrety i nie zawiera reguł biznesowych. Podmiana bazy to zmiana tylko tutaj. Kontener DI nie jest potrzebny.
**Snapshot:** `step4/` · **Różnica:** `scripts/warsztat.sh diff m2/s09 3 4`

### Rozwiązanie i uzasadnienie

Przypadek użycia testowany bez HTTP i bazy, adaptery wymienialne w jednym miejscu, dane na granicy jako rekordy. Koszt: osiem typów więcej. Uzasadnia go to, że dwa efekty zewnętrzne i protokół ich kolejności już istnieją (slajd 2.4, pytanie o koszt abstrakcji).

### Pułapki

- Kopiowanie szablonu czterech folderów bez sprawdzenia kierunku importów.
- Osobny model na każdej granicy "bo tak trzeba" - `NewReservation` ma sens, bo oddziela semantykę od `Object[]`.
- Reguły biznesowe w composition root albo w kontrolerze (np. walidacja miejsc w kontrolerze).

### Pytanie do sali

Czy wasz przypadek użycia da się uruchomić w teście bez frameworka webowego i bazy? Co trzeba by przenieść, żeby się dało?

## Scena s10. Automatyczna ochrona granicy i diagnostyka spójności

**Temat ze slajdów:** 2.8. Reguła zależności, porty i korzeń kompozycji (sprawdzana automatycznie)
**Pakiet:** `pl.training.workshop.m2.s10_boundarycheck` · **Test:** `scripts/warsztat.sh test m2/s10`
**Czas:** ~12 min

### W skrócie

**Co robimy:** Klasa domeny `ScreeningService` miesza politykę cenową z mapowaniem na wiersz bazy i importuje adapter oraz `java.sql`. Wydzielamy maper, przenosimy go do adaptera, a narzędzia `BoundaryRule` i `CohesionProbe` mierzą efekt po każdym kroku.

**Zasada:** Spójny moduł zmienia się z jednego powodu, a reguła zależności mówi, że domena nie importuje technologii - to dwa niezależne wymiary. Regułę architektury warto sprawdzać automatycznie przy każdym buildzie, a metryki takie jak LCOM4 traktować jako sygnał do rozmowy, nie wyrocznię.

**Efekt:** Domena jest wolna od `java.sql` i typów adaptera, a każda klasa ma LCOM4 równe 1. Skan importów jest słabą bramką - pełna nazwa klasy w kodzie przejdzie, więc silniejsze są ArchUnit, moduły Maven lub JPMS.

### Co widzimy

Dwa narzędzia bez bibliotek w pakiecie sceny: `BoundaryRule` (skanuje pliki `.java` w katalogu i zgłasza zakazane importy) oraz `CohesionProbe` (LCOM4: liczba grup metod połączonych wspólnym polem lub wywołaniem). Klasa domeny `ScreeningService` miesza politykę cenową z mapowaniem na wiersz bazy - importuje adapter i `java.sql`.

```java
import java.sql.Timestamp;

import pl.training.workshop.m2.s10_boundarycheck.start.adapter.ScreeningRow;

public final class ScreeningService {
    private final BigDecimal basePrice;
    private final BigDecimal morningDiscount;
    private final String table;
```

Uruchom test i pokaż wyjście konsoli: `s10 start - naruszenia granicy: [...]` i `s10 start - LCOM4 ScreeningService: Result[lcom4=2, groups=[fromRow, toRow, isMorning, price]]`. Test startu działa jak "zamrożone naruszenia" (FreezingArchRule w ArchUnit): znane są tolerowane, każde nowe jest czerwone.

### Krok 1: Extract Class - spójność

**Zmiany:**

1. Utwórz w pakiecie `domain` klasę `ScreeningRowMapper` i przenieś do niej pole `table` oraz metody `toRow` i `fromRow` z `ScreeningService` (Extract Class).
2. W `ScreeningService` zostaje sama polityka cenowa: pola `basePrice` i `morningDiscount`, metody `price` i `isMorning`; konstruktor traci parametr `table`.
3. Popraw composition root `CinemaApp`: tworzy `ScreeningService` i osobno `ScreeningRowMapper("screenings")`, a `toRow`/`fromRow` woła na maperze.

**Przed:**

```java
public final class ScreeningService {
    private final BigDecimal basePrice;
    private final BigDecimal morningDiscount;
    private final String table;
    ...
    public ScreeningRow toRow(Screening screening) {
        return new ScreeningRow(table, screening.title(), Timestamp.valueOf(screening.start()));
    }

    public Screening fromRow(ScreeningRow row) {
        if (!row.table().equals(table)) {
            throw new IllegalArgumentException("obca tabela: " + row.table());
        }
        return new Screening(row.title(), row.start().toLocalDateTime());
    }
}
```

**Po:**

```java
public final class ScreeningRowMapper {    // wciąż w domain
    private final String table;
    public ScreeningRow toRow(Screening screening) { ... }
    public Screening fromRow(ScreeningRow row) { ... }
}
```

**Uruchom:** `scripts/warsztat.sh test m2/s10` - zielono; LCOM4 obu klas = 1, ale `step1StillViolatesTheBoundaryButOnlyInTheMapper` pokazuje, że naruszenie granicy tylko się przeniosło.
**Co powiedzieć:** spójność i kierunek zależności to dwa różne wymiary. Wydzielenie klasy nie naprawia granicy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m2/s10 0 1`

### Krok 2: Move Class do adaptera

**Zmiany:**

1. Przenieś `ScreeningRowMapper` z pakietu `domain` do pakietu `adapter` (Move Class).
2. W maperze dochodzi import `domain.Screening`; popraw też import mapera w `CinemaApp`.

**Przed:**

```java
package pl.training.workshop.m2.s10_boundarycheck.step1.domain;

import java.sql.Timestamp;

import pl.training.workshop.m2.s10_boundarycheck.step1.adapter.ScreeningRow;

public final class ScreeningRowMapper {
```

**Po:**

```java
package pl.training.workshop.m2.s10_boundarycheck.step2.adapter;

import java.sql.Timestamp;

import pl.training.workshop.m2.s10_boundarycheck.step2.domain.Screening;
```

**Uruchom:** test zielony; `step2DomainIsFreeOfTechnology` - lista naruszeń pusta.
**Co powiedzieć:** zależność biegnie teraz `adapter -> domain`. Reguła jest sprawdzana automatycznie przy każdym buildzie, a nie tylko na code review.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m2/s10 1 2`

### Rozwiązanie i uzasadnienie

Domena bez `java.sql` i bez typów adaptera, każda klasa opisuje jedno pojęcie. `S10ToolsTest` sprawdza same narzędzia na stałych próbkach kodu.

### Pułapki

- `BoundaryRule` widzi tylko importy - pełna nazwa klasy w kodzie przejdzie. Silniejsze bramki: ArchUnit, osobne moduły Maven, JPMS (`module-info`), reguły w CI. Porównajcie ich siłę.
- LCOM4 to heurystyka na źródle (pola `private`, typy bez spacji, wcięcie 4 spacje) - sygnał do rozmowy, nie wyrocznia. Mała klasa nie musi być spójna, duża może być nierozdzielnym pojęciem.
- Wydzielenie klasy dodaje współpracownika i kontrakt - porównuj koszt zmiany przed i po.

### Pytanie do sali

Która reguła architektury w waszym projekcie istnieje tylko w głowach lub na wiki? Jak najtaniej zamienić ją w test?

## Proponowana kolejność pokazu

**Ścieżka krótka (~55 min):** s03 (bramka kompilatora, 10) → s01 (YAGNI, 12) → s08 (odwrócenie zależności, 12) → s05 (zasada harcerza, 8) → s06 (seria zmian, 10). Pokazuje pełny łuk prewencji: najszybsza pętla, odłożona decyzja, kierunek zależności, mała poprawa i zestaw zmian gotowy do przeglądu. Jeśli zostanie czas, z s09 pokaż tylko `diff m2/s09 3 4` i `S09UseCaseTest` (5 min).

**Ścieżka pełna (~115 min, z przerwą):**

1. Szybka informacja zwrotna: s03, s04 (~22 min) - na koniec pytanie, które sprawdzenia uruchamiać lokalnie, a które w CI.
2. Decyzje: s01, s02 (~22 min) - pięć pytań ze slajdu 2.4 na przykładzie s01, potem decyzja trwała zapisana w ADR i pilnowana testem.
3. Projekt: s07, s08, s09, s10 (~54 min) - od jednego aktora zmiany, przez kierunek zależności, do granicy sprawdzanej automatycznie.
4. Codzienna dbałość o kod: s05, s06 (~18 min) jako podsumowanie - mała poprawa w dotykanym kodzie i seria zmian z osobnymi dowodami.

Sceny s09 i s10 dobrze łączyć: po s09 zapytaj, jak upewnić się, że `app` nigdy nie zaimportuje `adapter`, i przejdź do s10. Podobnie s05 i s06: po s05 zapytaj, jak uczciwie wprowadzić posortowane miejsca, i pokaż, jak s06 oddziela refaktoryzację od zmiany zachowania.
