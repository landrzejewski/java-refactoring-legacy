# Moduł 2. Prewencja - warsztat CineLegacy: przewodnik prowadzącego

Dziesięć małych scen w domenie kina CineLegacy pokazuje prewencję z modułu 2 jako serie bezpiecznych zmian: bramki kompilatora i jakości jako najszybsze pętle informacji zwrotnej, odraczanie decyzji (YAGNI) i ich zapis (ADR z wykonywalną regułą), projekt jako hamulec degradacji (SRP, DIP, granice Clean Architecture, automatyczna kontrola granicy) oraz prewencję w codziennej pracy (Boy Scout Rule, seria zmian gotowych do przeglądu). Każda scena ma kod wyjściowy (`start`) z zapachem lub pułapką i kompletne snapshoty po każdym kroku (`step1`, `step2`, ...). Prowadzący pracuje na żywo w `start`, po każdym kroku uruchamia test sceny i porównuje wynik ze snapshotem.

Sceny ilustrują sekcję 2 slajdów ("Zanim wprowadzisz wzorzec"): każdy temat ma własną, kilkuminutową scenę. Sceny są pokazami prowadzącego do wyboru - nie trzeba pokazywać wszystkich. Numery scen odpowiadają pakietom w kodzie; poniżej są ułożone w kolejności tematów modułu.

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
- Testy dotykają `start` wyłącznie przez API wspólne dla wszystkich kroków, więc kompilują się w każdym stanie pośrednim. Tam, gdzie refaktoryzacja zmienia konstruktory, scena ma punkt wejścia pełniący rolę composition root: `Main` (s08), `CinemaApplication` (s09), `CinemaApp` (s10). Refaktoryzacje IntelliJ (Introduce Parameter, Move Class) same aktualizują te miejsca.
- Część testów to nie tylko równoważność (`SNNEquivalenceTest`), ale też dowody strategii (`SNNSolutionTest`, `S08DependencyDirectionTest`, `S09UseCaseTest`, `S10ArchitectureTest`): wynik bramki, lista naruszeń ADR, kierunek zależności. Testy, które **dokumentują stan startowy** (`startViolatesBothRules` w s02, `startFailsTheGateWithFourKindsOfWarnings` w s03, `startPassesEverythingBecauseItChecksNothing` w s04, `startDomainHasNoViolationsBeyondTheFrozenOnes` w s10), zmienią kolor na czerwony, gdy wykonasz kroki na pakiecie `start` - to znak, że start jest już naprawiony. Pułapki, które znikają po naprawie, testy dokumentują na kopii w kroku, który jeszcze je ma (np. s05 krok 1 - antyprzykład nadużycia Boy Scout).
- Sceny s02, s03, s04 i s10 czytają pliki źródłowe sceny. Testy uruchamiaj z katalogu głównego repozytorium (tak działa `warsztat.sh` i `mvn test`).
- Sceny s03 i s04 celowo zawierają kod z ostrzeżeniami kompilatora (`start` i `sample/dirty`) - to materiał dla bramek, nie niedoróbka.

## Mapa scen

| Scena | Temat ze slajdów | Kroki | Pakiet | Czas |
| --- | --- | --- | --- | --- |
| s03 | 2.1. Małe kroki i szybka informacja zwrotna; 2.2. Bramki jakości w starym kodzie: stan bazowy i zapadka | 3 | `m2.s03_compilergate` | ~10 min |
| s04 | 2.2. Bramki jakości w starym kodzie: stan bazowy i zapadka | 4 | `m2.s04_qualitygate` | ~12 min |
| s01 | 2.3. YAGNI: buduj dla dzisiejszej potrzeby; 2.4. Pięć pytań przed dodaniem abstrakcji | 3 | `m2.s01_yagni` | ~12 min |
| s02 | 2.5. ADR: zapisz decyzję, a najlepiej ją sprawdzaj | 2 | `m2.s02_adr` | ~10 min |
| s07 | 2.6. Zasada pojedynczej odpowiedzialności: jeden aktor, jeden powód zmiany | 3 | `m2.s07_srp` | ~12 min |
| s08 | 2.7. Zasada odwrócenia zależności: liczy się kierunek importów; 2.8. Reguła zależności, porty i korzeń kompozycji | 3 | `m2.s08_dip` | ~12 min |
| s09 | 2.8. Reguła zależności, porty i korzeń kompozycji | 4 | `m2.s09_cleanarchitecture` | ~18 min |
| s10 | 2.8. Reguła zależności, porty i korzeń kompozycji (sprawdzana automatycznie) | 2 | `m2.s10_boundarycheck` | ~12 min |
| s05 | 2.9. Zasada harcerza i małe porcje zmian | 2 | `m2.s05_boyscout` | ~8 min |
| s06 | 2.9. Zasada harcerza i małe porcje zmian | 3 | `m2.s06_reviewableseries` | ~10 min |

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

**W IDE:** na polu `seatsByRow` ⌥⏎ → Add type arguments; `computeIfAbsent` zamiast `get`/`put`; usuń rzutowania.
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

**W IDE:** w `return` zamień argument `format` na istniejącą już zmienną `name` - wywołanie trafia do `PriceTable.basePrice(String)`. Ctrl+klik na metodzie potwierdza, że to nowe przeciążenie.
**Po:**

```java
return name + " [" + features + "], cena " + PriceTable.basePrice(name)
        + " zl, zajete: " + map.takenPerRow();
```

**Uruchom:** test zielony; zostaje tylko `[fallthrough] OccupancyReport.java:13`.
**Co powiedzieć:** przestarzałą metodę usuniemy (Safe Delete) dopiero, gdy nikt jej nie woła - także poza repozytorium.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m2/s03 1 2`

### Krok 3: switch expression

**W IDE:** ⌥⏎ na `switch` → Replace with enhanced 'switch' statement, potem ręcznie zamień na wyrażenie; przelot zapisany wprost: `case 3 -> "duzy ekran, dzwiek Dolby"`.
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

**W IDE:** metoda `scanSources`: każda linia z `TODO`/`FIXME` albo `System.out`/`System.err` to wynik z plikiem i linią.
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

**W IDE:** metoda `compileSources` (javax.tools, `-Xlint:all`), każda diagnostyka poza NOTE to wynik.
**Po:**

```java
findings.add("kompilator " + Path.of(d.getSource().toUri()).getFileName() + ":"
        + d.getLineNumber() + " " + d.getCode());
```

**Uruchom:** test zielony; dochodzą dwa wyniki `compiler.warn.raw.class.use`.
**Co powiedzieć:** to ta sama idea co w s03, tu jako jeden z kilku sygnałów bramki.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m2/s04 1 2`

### Krok 3: pokrycie kluczowej klasy (przybliżenie)

**W IDE:** metoda `checkCoverage`: każda publiczna metoda `PriceTable` musi być wywołana w `PriceTableTest`.
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

**W IDE:** metoda `runTests`: refleksja uruchamia metody z adnotacją o nazwie `Test` i zgłasza porażki.
**Po:**

```java
try {
    test.invoke(constructor.newInstance());
} catch (InvocationTargetException failure) {
    findings.add("test " + type.getSimpleName() + "." + test.getName() + " nie przechodzi: " ...);
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

**W IDE:** w `TicketPricer` zastąp pola `registry` i `activeRules` listą `List.of(new MorningRule(), new VipRule())` (ręcznie, konstruktory usuń). Safe Delete (⌘⌦) na `RuleRegistry` i na `priority()` w interfejsie (był potrzebny tylko do sortowania).
**Po:**

```java
private final List<PricingRule> rules = List.of(new MorningRule(), new VipRule());
```

**Uruchom:** `scripts/warsztat.sh test m2/s01` - 16 testów zielonych.
**Co powiedzieć:** konfiguracja napisem nie miała żadnego klienta. Teraz literówka w nazwie reguły nie skompiluje się.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m2/s01 0 1`

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
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m2/s01 1 2`

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
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m2/s01 2 3`

### Rozwiązanie i uzasadnienie

Przejdź z salą filtr ze slajdu 2.4: aktualne wymaganie (2 reguły), nazwa w języku problemu (brak: "plugin", "registry"), czy upraszcza aktualny przypadek (nie), koszt usunięcia (niski - pokazaliśmy). Z pięciu klas została jedna.

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

**W IDE:** nowy record `Quote(double total, boolean groupDiscount)` w `pricing`; ⌘F6 na `total` - bez `organizer`, zwraca `Quote`. Wywołanie `mailer` przenieś do `BookingService` (warstwa aplikacji). Usuń import `notification` z cennika.
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

**W IDE:** Type Migration (⇧⌘F6) `double` → `Money` w `Quote` i `TicketPricing`; nazwane stałe dla progu i procentu grupy; `BookingService` podaje `Money.of("25.00")`.
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

**W IDE:** zaznacz blok pod `// księgowość`, ⌥⌘M `accountingSection` (zwraca `String`, własny `StringBuilder`); to samo dla `// marketing` jako `marketingSection`.
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

**W IDE:** skopiuj `revenue` (⌘D na metodzie), nazwij kopię `popularity` (⇧F6), w `marketingSection` użyj `popularity`. Dodaj Javadoc z właścicielem każdej metody.
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

**W IDE:** na inicjalizatorze pola `mail` ⌥⌘P (Introduce Parameter do konstruktora). IntelliJ przeniesie `new SmtpMailSender("smtp.kino.pl", 25)` do `Main`.
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

**W IDE:** zaznacz składanie MIME, `send` i sprawdzenie kodu, ⌥⌘M, nazwa `notifyCustomer(String email, String message)`.
**Po:**

```java
notifyCustomer(reservation.email(), message);
return "potwierdzono: " + reservation.email();
```

**Uruchom:** test zielony.
**Co powiedzieć:** tak wygląda port, zanim stanie się interfejsem: potrzeba nazwana w języku problemu ("powiadom klienta"), a cała technologia w jednym miejscu. Nie robimy Extract Interface z `SmtpMailSender` - dostalibyśmy port `send(to, mime)`, czyli kształt technologii, nie potrzeby.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m2/s08 1 2`

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

**Uruchom:** `scripts/warsztat.sh test m2/s09` - 22 testy zielone.
**Co powiedzieć:** przypadek użycia czyta się jak scenariusz: wyceń, zapisz, powiadom. Granicę przekraczają proste rekordy, nie `Map` z HTTP. Wciąż zna jednak kolumny tabeli i temat komunikatu.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m2/s09 0 1`

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
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m2/s09 1 2`

### Krok 3: Move Class - granica widoczna w pakietach

**W IDE:** F6 na `BookSeats`, `BookSeatsCommand`, `Booking`, `NewReservation`, `ReservationStore`, `BookingNotifier` -> pakiet `app`; F6 na kontrolerze i obu adapterach -> `adapter`. IntelliJ poprawi importy (także w `CinemaApplication`).
**Po:**

```text
step3/app/       BookSeats, BookSeatsCommand, Booking, NewReservation, ReservationStore, BookingNotifier
step3/adapter/   ReservationController, RowStoreReservationStore, OutboxBookingNotifier
```

**Uruchom:** test zielony.
**Co powiedzieć:** `app` nie importuje niczego z `adapter`; adaptery importują `app`. Nazwy pakietów są drugorzędne - liczy się kierunek importów.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m2/s09 2 3`

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

**Uruchom:** test zielony; pokaż `S09UseCaseTest`: przypadek użycia z lambdami zamiast adapterów, w tym `doesNotNotifyWhenSavingFails`.
**Co powiedzieć:** composition root zna wszystkie konkrety i nie zawiera reguł biznesowych. Podmiana bazy to zmiana tylko tutaj. Kontener DI nie jest potrzebny.
**Snapshot:** `step4/` · **Różnica:** `scripts/warsztat.sh diff m2/s09 3 4`

### Rozwiązanie i uzasadnienie

Przypadek użycia testowany bez HTTP i bazy, adaptery wymienialne w jednym miejscu, dane na granicy jako rekordy. Koszt: siedem typów więcej. Uzasadnia go to, że dwa efekty zewnętrzne i protokół ich kolejności już istnieją (slajd 2.4, pytanie o koszt abstrakcji).

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

**W IDE:** Refactor > Extract > Delegate na `toRow`, `fromRow` i polu `table`, klasa `ScreeningRowMapper` w tym samym pakiecie `domain`. Popraw `CinemaApp` (composition root), żeby tworzył obie klasy.
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

**W IDE:** F6 na `ScreeningRowMapper`, pakiet `adapter`. IntelliJ doda import `domain.Screening` w maperze i poprawi `CinemaApp`.
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

**W IDE:** pokaż antyprzykład: `scripts/warsztat.sh jump m2/s05 1`. Obok dobrych ruchów przemycono sortowanie miejsc, e-mail małymi literami i pominięcie linii "Tel" bez telefonu.
**Po:**

```java
List<String> sortedSeats = ticket.seats().stream().sorted().toList();
text.append("Klient: ").append(ticket.email().trim().toLowerCase(Locale.ROOT)).append('\n');
```

**Uruchom:** `scripts/warsztat.sh test m2/s05` - równoważność czerwona w trzech przypadkach (m.in. `A9, A10` staje się `A10, A9`). `S05SolutionTest.abusiveCleanupChangesBehaviourInThreeCases` dokumentuje, które.
**Co powiedzieć:** każda z tych zmian może być dobrym pomysłem, ale to decyzje biznesowe, nie sprzątanie. Cofamy: `scripts/warsztat.sh reset m2/s05`.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m2/s05 0 1`

### Krok 2: poprawna, mała poprawa

**W IDE:** Rename (⇧F6) `t` → `ticket`; Replace loop with `String.join` (⌥⏎ na pętli albo ręcznie); `StringBuilder` zamiast konkatenacji; Extract Method (⌥⌘M) `phoneLine`.
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

**W IDE:** ⌥⌘M na wyborze ceny bazowej → `basePrice(String format)`; ⌥⌘M na wyborze zniżki → `discountPercent(String type)` zwracające procent; Money zamiast BigDecimal.
**Po:**

```java
Money base = basePrice(query.format());
Money price = base.minus(base.percent(discountPercent(query.type())));
```

**Uruchom:** test zielony - równoważność na przypadkach, w tym wtorkowych.
**Co powiedzieć:** opis commita: "refaktoryzacja, bez zmiany zachowania, dowód: S06EquivalenceTest". Recenzent sprawdza tylko mechanikę.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m2/s06 0 1`

### Krok 2: commit 2 - refaktoryzacja przygotowawcza (Change Signature)

**W IDE:** ⌘F6 na `discountPercent`: parametr `TicketQuery query` zamiast `String type`, w ciele `switch (query.type())`.
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

**W IDE:** w gałęzi `default` warunek `isCheapTuesday(query) ? 20 : 0`, metoda pomocnicza, stała `CHEAP_TUESDAY_PERCENT`.
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

## Proponowana kolejność pokazu

**Ścieżka krótka (~55 min):** s03 (bramka kompilatora, 10) -> s01 (YAGNI, 12) -> s08 (DIP, 12) -> s05 (Boy Scout, 8) -> s06 (seria zmian, 10). Pokazuje pełny łuk prewencji: najszybsza pętla, odłożona decyzja, kierunek zależności, mała poprawa i zestaw zmian gotowy do przeglądu. Jeśli zostanie czas, z s09 pokaż tylko `diff m2/s09 3 4` i `S09UseCaseTest` (5 min).

**Ścieżka pełna (~115 min, z przerwą):**

1. Fast feedback: s03, s04 (~22 min) - na koniec pytanie, które sprawdzenia uruchamiać lokalnie, a które w CI.
2. Decyzje: s01, s02 (~22 min) - filtr decyzyjny ze slajdu 2.4 na przykładzie s01, potem decyzja trwała zapisana w ADR i pilnowana testem.
3. Projekt: s07, s08, s09, s10 (~54 min) - od jednego aktora zmiany, przez kierunek zależności, do granicy sprawdzanej automatycznie.
4. Prewencja codzienna: s05, s06 (~18 min) jako podsumowanie - mała poprawa w dotykanym kodzie i seria zmian z osobnymi dowodami.

Sceny s09 i s10 dobrze łączyć: po s09 zapytaj, jak upewnić się, że `app` nigdy nie zaimportuje `adapter`, i przejdź do s10. Podobnie s05 i s06: po s05 zapytaj, jak uczciwie wprowadzić posortowane miejsca, i pokaż, jak s06 oddziela refaktoryzację od zmiany zachowania.
