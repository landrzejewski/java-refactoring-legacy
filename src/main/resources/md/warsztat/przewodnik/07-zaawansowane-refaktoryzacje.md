# Moduł 7. Zaawansowane refaktoryzacje - warsztat CineLegacy: przewodnik prowadzącego

Piętnaście scen w domenie kina pokazuje na żywo wszystkie trzynaście refaktoryzacji z modułu 7 oraz dwa tematy przekrojowe: granicę między refaktoryzacją a zmianą kontraktu i wektor obserwowalnego zachowania. Każda scena ma kod wyjściowy (`start`), kompletne snapshoty po każdym kroku (`stepN`) i test, który po każdym ruchu w IDE ma być zielony. Tam, gdzie krok świadomie zmienia zachowanie (s04, s06, s08, s14, s15), test ma osobne oczekiwania dla wariantów i pokazuje różnicę wprost.

Sceny nie powtarzają przykładów z domeny wdrożeń (`pl.training.module7`) ani Warsztatów 1-3 z zadań modułu 7. Tamte ćwiczenia uczestnicy robią sami, a sceny poniżej służą do pokazu. Scena s13 pracuje na **kopii** starego `CinemaManager`, więc oryginał w `pl.training.workshop.legacy` i jego golden master pozostają nietknięte.

## Jak prowadzić pokaz

```bash
scripts/warsztat.sh list m7            # sceny modułu i ich kroki
scripts/warsztat.sh test m7/s02        # testy jednej sceny
scripts/warsztat.sh test m7            # wszystkie sceny modułu (286 testów)
scripts/warsztat.sh diff m7/s13 2 3    # co zmienia krok 3 względem kroku 2 (0 = start)
scripts/warsztat.sh jump m7/s13 2      # skopiuj step2 do start, gdy brakuje czasu
scripts/warsztat.sh reset m7/s13       # przywróć start po pokazie
```

- Zasada: **jeden krok - jeden test - jedno zdanie komentarza.** Ruch w IDE, ⌃R na teście sceny, zdanie z sekcji "Co powiedzieć".
- Pracujesz zawsze w pakiecie `start`. Snapshoty `stepN` służą do `diff` albo do przeskoku (`jump`), gdy coś się rozjedzie.
- Zanim zaczniesz, pokaż slajd "Wektor obserwowalnego zachowania" i wracaj do niego przy każdej scenie: *co tu obserwujemy - wynik, wyjątek, stan, efekty, kolejność?* Testy scen celowo obserwują więcej niż sam wynik (audyt w s07, licznik w s12, lista wejściowa w s05, skrzynka nadawcza w s03).
- W s01 start **nie ma testów** i to jest puenta: testy pojawiają się dopiero wtedy, gdy kod ma seam.
- W s10 kompilator zgłasza 4 ostrzeżenia `[deprecation]` (kroki 1 i 2). To celowe: lista ostrzeżeń jest listą klientów do migracji. Zniknie w kroku 3.
- Kroki, które zmieniają kontrakt (s04 krok 2, s06 krok 3, s08 kroki 1-2, s14 kroki 2-3, s15 krok 2), zapowiadaj na głos: "to jest osobny commit, nie refaktoryzacja".

## Mapa scen

| Scena | Temat ze slajdów | Kroki | Pakiet | Czas |
|---|---|---|---|---|
| s01 | 1. Break Dependencies - intencja, ryzyka, przed i po | 4 | `m7.s01_breakdependencies` | ~20 min |
| s02 | 2. Extract Method Object - przed, po, sekwencja i ryzyka | 3 | `m7.s02_methodobject` | ~15 min |
| s03 | 3. Break Responsibilities - intencja, ryzyka, po zmianie | 3 | `m7.s03_breakresponsibilities` | ~12 min |
| s04 | 4. Remove Duplication - duplikacja wiedzy, po zmianie | 3 | `m7.s04_removeduplication` | ~12 min |
| s05 | 5. Break Method - intencja i bezpieczna sekwencja | 3 | `m7.s05_breakmethod` | ~10 min |
| s06 | 6. Introduce Parameter Object - data clump, walidacja, migracja | 3 | `m7.s06_parameterobject` | ~12 min |
| s07 | 7. Remove Arrowhead Antipattern | 3 | `m7.s07_arrowhead` | ~10 min |
| s08 | 8. Introduce Design by Contract Checks | 2 | `m7.s08_designbycontract` | ~10 min |
| s09 | 9. Remove Double Negative | 3 | `m7.s09_doublenegative` | ~8 min |
| s10 | 11. Remove Boolean Method Parameters; Zgodność binarna | 4 | `m7.s10_booleanparameter` | ~12 min |
| s11 | 12. Remove Middle Man | 3 | `m7.s11_middleman` | ~8 min |
| s12 | 13. Return ASAP | 2 | `m7.s12_returnasap` | ~8 min |
| s13 | 10. Remove God Classes - kampania, wydzielony fragment i ryzyka | 4 | `m7.s13_godclass` | ~30 min |
| s14 | Refaktoryzacja a zmiana kontraktu; Pętla pracy (pkt 5) | 3 | `m7.s14_contractchange` | ~10 min |
| s15 | Wektor obserwowalnego zachowania | 3 | `m7.s15_behaviourvector` | ~12 min |

## Scena s01. Break Dependencies - seam dla zadania przypomnień

**Temat ze slajdów:** 1. Break Dependencies - intencja i ryzyka; Break Dependencies - przed i po; Java 25 w tym module (interfejs funkcyjny jako seam)
**Pakiet:** `pl.training.workshop.m7.s01_breakdependencies` · **Test:** `scripts/warsztat.sh test m7/s01`
**Czas:** ~20 min

### Co widzimy

`ShowtimeReminderJob.run` wysyła przypomnienia na 2 godziny przed seansem. Wszystkie zależności są zaszyte w środku: produkcyjna baza tworzona przez `new`, bieżący czas i statyczny mailer. Konstruktor `LegacyDatabase` rzuca wyjątek poza serwerownią, a `ReminderMailer.send` - poza produkcją. **Tej klasy nie da się uruchomić w teście** - test sceny dla `start` tylko to dokumentuje.

```java
public int run() {
    LegacyDatabase database = new LegacyDatabase();
    LocalDateTime now = LocalDateTime.now();
    ...
            ReminderMailer.send(booking.email(), "Przypomnienie: " + booking.title(), ...);
            database.markReminded(booking.id());
```

Sekwencja ze slajdu: charakterystyka, najwęższy kontrakt, dotychczasowa implementacja przez kontrakt, test z fake/spy. Charakterystyki nie da się napisać przed pierwszym ruchem, więc pierwsze ruchy muszą być mechaniczne i wspierane przez IDE.

### Krok 1: Extract Interface + Parameterize Constructor

**W IDE:** na `LegacyDatabase` Refactor → Extract Interface, nazwa `BookingStore`, zaznacz `paidBookings` i `markReminded`. Potem w `ShowtimeReminderJob` ręcznie: pole `Supplier<BookingStore> stores`, konstruktor z parametrem i konstruktor bezargumentowy `this(LegacyDatabase::new)`. W `run()` zamień `new LegacyDatabase()` na `stores.get()`.
**Po:**

```java
public ShowtimeReminderJob() {
    this(LegacyDatabase::new);
}

public int run() {
    BookingStore database = stores.get();
```

**Uruchom:** `scripts/warsztat.sh test m7/s01` - testy `step1...` zielone: z fałszywą bazą zadanie działa, gdy nic nie jest do wysłania.
**Co powiedzieć:** przekazujemy fabrykę, a nie gotową bazę, bo stary kod otwierał połączenie przy każdym `run()`. Gdybyśmy zrobili `this(new LegacyDatabase())`, wyjątek poleciałby już w konstruktorze - zmienilibyśmy czas życia zależności i moment błędu.
**Snapshot:** `step1/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s01 0 1`

### Krok 2: Clock jako zależność

**W IDE:** ręcznie (Extract Parameter ⌥⌘P dodałby parametr do `run()`, a potrzebujemy zależności konstruktora): pole `Clock clock`, drugi parametr konstruktora, w domyślnym konstruktorze `Clock.systemDefaultZone()`, w `run()` `LocalDateTime.now(clock)`.
**Po:**

```java
LocalDateTime now = LocalDateTime.now(clock);
```

**Uruchom:** test zielony. Test `step2...` pokazuje dwie rzeczy: przypadki bez wysyłki działają przy stałym zegarze, a przypadek "do wysłania" wciąż kończy się wyjątkiem SMTP.
**Co powiedzieć:** czas był ukrytym wejściem. Zegar czytamy w tym samym miejscu co wcześniej (po utworzeniu bazy), więc kolejność się nie zmienia. Następna przeszkoda jest widoczna w teście - statyczny mailer.
**Snapshot:** `step2/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s01 1 2`

### Krok 3: Subclass and Override Method i pierwszy test

**W IDE:** zaznacz wywołanie `ReminderMailer.send(...)`, ⌥⌘M, nazwa `sendReminder`, widoczność `protected`. Usuń `final` z klasy. W teście podklasa anonimowa nadpisuje `sendReminder` i zapisuje wiadomości do listy - to jest **pierwszy prawdziwy test** tej klasy.
**Po:**

```java
protected void sendReminder(String to, String subject, String body) {
    ReminderMailer.send(to, subject, body);
}
```

**Uruchom:** test zielony: 3 przypadki z granicą 120/121 minut, już przypomniane i już rozpoczęte.
**Co powiedzieć:** to najtańszy seam, gdy nie możemy jeszcze zmienić konstruktora - ale ma cenę: klasa przestała być `final`, a test zależy od szczegółu implementacji (nazwy metody chronionej).
**Snapshot:** `step3/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s01 2 3`

### Krok 4: seam z dziedziczenia na interfejs funkcyjny

**W IDE:** utwórz `@FunctionalInterface ReminderSender`, dodaj trzeci parametr konstruktora, w domyślnym `ReminderMailer::send`. Inline Method (⌥⌘N) na `sendReminder`, przywróć `final`. W teście podklasę zastępuje lambda.
**Po:**

```java
public ShowtimeReminderJob() {
    this(LegacyDatabase::new, Clock.systemDefaultZone(), ReminderMailer::send);
}
```

**Uruchom:** test zielony - te same 3 przypadki dla `step3` (podklasa) i `step4` (lambda).
**Co powiedzieć:** pod ochroną testu z kroku 3 wymieniliśmy seam na najwęższy możliwy. Produkcja w domyślnym konstruktorze składa dokładnie te same implementacje co wcześniej - zmieniło się **miejsce tworzenia zależności**, nie reguła.
**Snapshot:** `step4/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s01 3 4`

### Rozwiązanie i uzasadnienie

`step4/ShowtimeReminderJob`: trzy zależności w konstruktorze, każda o najwęższym kontrakcie potrzebnym klientowi (`BookingStore` z dwiema metodami, `Clock`, `ReminderSender` z jedną). Reguła przypomnień nie zmieniła się ani o znak. Dopiero teraz można bezpiecznie dodać nową regułę biznesową (np. przypomnienie SMS), bo test widzi i czas, i wysyłki.

### Pułapki

- `this(new LegacyDatabase())` w domyślnym konstruktorze: zmiana czasu życia i momentu wyjątku (test `step1KeepsConnectionLifetimeOfProductionConstructor`).
- Interfejs "na zapas" z wszystkimi metodami `LegacyDatabase` zamiast dwóch potrzebnych.
- `sealed` na interfejsie seamu - test nie podstawi wtedy własnej implementacji.
- Zostawienie Subclass and Override na stałe: klasa otwarta do dziedziczenia tylko dla testu.

### Pytanie do sali

Kiedy Subclass and Override jest lepszym pierwszym ruchem niż Parameterize Constructor?

## Scena s02. Extract Method Object - wycena zamówienia grupowego

**Temat ze slajdów:** 2. Extract Method Object - przed; Extract Method Object - po, sekwencja i ryzyka
**Pakiet:** `pl.training.workshop.m7.s02_methodobject` · **Test:** `scripts/warsztat.sh test m7/s02`
**Czas:** ~15 min

### Co widzimy

`GroupPricing.quote` liczy cenę zamówienia (także grupowego): cena formatu, zniżki, poranek, okulary 3D, VIP, rabat 10% od 10 biletów, opłaty online i punkty. Osiem zmiennych lokalnych karmi się nawzajem. Pętla ma dwa wyjścia (`tickets`, `count`) i trzy wejścia robocze.

```java
Money tickets = Money.ZERO;
int count = 0;
for (String type : order.ticketTypes()) {
    ...
    Money price = base.minus(base.percent(discount));
    if (morning) { price = price.minus(Money.of("5.00")); }
    if (glasses) { price = price.plus(Money.of("3.00")); }
    tickets = tickets.plus(price);
    count++;
}
```

Spróbuj ⌥⌘M na pętli: IntelliJ odmówi (wiele wartości wyjściowych). To jest moment na Method Object.

### Krok 1: Extract Method Object - kopia bez upraszczania

**W IDE:** utwórz klasę `GroupQuoteCalculation` z polem `order` i metodą `calculate()`. Skopiuj ciało `quote` **dosłownie**. W `GroupPricing.quote` zostaw jedną linię delegacji. (IntelliJ ma też Refactor → Replace Method with Method Object, ale tworzy klasę wewnętrzną - patrz Pułapki.)
**Po:**

```java
public Quote quote(GroupOrder order) {
    return new GroupQuoteCalculation(order).calculate();
}
```

**Uruchom:** `scripts/warsztat.sh test m7/s02` - 24 testy zielone.
**Co powiedzieć:** skopiuj, deleguj, porównaj - dopiero potem sprzątaj. Obiekt metody powstaje na każde wywołanie, więc nie ma współdzielonego stanu między wycenami.
**Snapshot:** `step1/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s02 0 1`

### Krok 2: zmienne lokalne jako pola

**W IDE:** na każdej zmiennej roboczej (`base`, `morning`, `glasses`, `tickets`, `count`) Refactor This (⌃T) → Convert Local to Field (albo ⌥⌘F), inicjalizacja w miejscu deklaracji w metodzie.
**Po:**

```java
private Money base;
private boolean morning;
private boolean glasses;
private Money tickets = Money.ZERO;
private int count;
```

**Uruchom:** test zielony.
**Co powiedzieć:** teraz każdy blok może zostać metodą bez parametrów i bez wielu wyjść - stan niosą pola. To działa tylko dlatego, że obiekt żyje jedno wywołanie.
**Snapshot:** `step2/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s02 1 2`

### Krok 3: Extract Method wewnątrz obiektu

**W IDE:** kolejno ⌥⌘M: `readConditions`, `addTickets` (a w niej `ticketPrice` i statyczne `discountPercent`), `addVipSeats`, `applyGroupDiscount`, `bookingFees`, `loyaltyPoints`. Inline Variable (⌥⌘N) dla `total`.
**Po:**

```java
Quote calculate() {
    readConditions();
    addTickets();
    addVipSeats();
    applyGroupDiscount();
    Money fees = bookingFees();
    return new Quote(tickets, fees, tickets.plus(fees), loyaltyPoints());
}
```

**Uruchom:** test zielony - w tym przypadek z rabatem 23.125 zaokrąglonym do 23.13.
**Co powiedzieć:** kolejność wywołań jest kontraktem: rabat grupowy liczony po dodaniu VIP, punkty po rabacie. Metody z efektem na polach czytają się jak kroki algorytmu.
**Snapshot:** `step3/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s02 2 3`

### Rozwiązanie i uzasadnienie

`step3/GroupQuoteCalculation`: jedno wykonanie wyceny jako obiekt, sześć nazwanych kroków. Publiczne API `GroupPricing` i kontrakt `Quote` bez zmian. Porównaj ze sceną s05: tam dane łatwo przechodzą między etapami, więc Method Object byłby zbędny.

### Pułapki

- Obiekt metody jako pole usługi (`private final GroupQuoteCalculation calc`) - pola przeżyją wywołanie i wyceny zaczną się mieszać.
- Upraszczanie w trakcie kopiowania (np. `count` zastąpione `ticketTypes().size()` przed testem).
- Niestatyczna klasa wewnętrzna ukrywa referencję do obiektu zewnętrznego.
- Zmiana momentu odczytu danych (np. `order.start()` czytane w konstruktorze zamiast w `calculate()`).

### Pytanie do sali

Po co w ogóle Method Object, skoro można zwrócić record `(tickets, count)` z wydzielonej pętli?

## Scena s03. Break Responsibilities - walidacja, wycena, powiadomienie

**Temat ze slajdów:** 3. Break Responsibilities - intencja i ryzyka; Break Responsibilities - po zmianie
**Pakiet:** `pl.training.workshop.m7.s03_breakresponsibilities` · **Test:** `scripts/warsztat.sh test m7/s03`
**Czas:** ~12 min

### Co widzimy

`BookingDesk.book` waliduje prośbę, wycenia miejsca i wysyła powiadomienie do `Outbox`. Trzy powody zmiany: reguły walidacji (obsługa klienta), cennik (finanse), treść wiadomości (marketing). Komentarze `// walidacja`, `// wycena`, `// powiadomienie` wyznaczają klastry. Test obserwuje wynik i zawartość skrzynki nadawczej.

```java
BigDecimal total = BigDecimal.ZERO;
int vipSeats = 0;
for (String seat : request.seats()) { ... }
// powiadomienie
String text = "Rezerwacja " + request.seats().size() + " miejsc";
if (vipSeats > 0) { ... }
```

### Krok 1: Extract Class - walidacja

**W IDE:** zaznacz blok walidacji, ⌥⌘M `firstError` zwracająca `Optional<String>` (ręcznie zamień `return "..."` na `return Optional.of("...")`), potem F6 (Move) do nowej klasy `BookingValidator`.
**Po:**

```java
Optional<String> error = validator.firstError(request);
if (error.isPresent()) {
    return error.get();
}
```

**Uruchom:** `scripts/warsztat.sh test m7/s03` - 20 testów zielonych (w tym priorytet: zły e-mail przed brakiem miejsc).
**Co powiedzieć:** kolejność kontroli jest częścią kontraktu - pierwszy błąd wygrywa.
**Snapshot:** `step1/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s03 0 1`

### Krok 2: Extract Class - wycena z wynikiem jako rekord

**W IDE:** utwórz rekord `Pricing(total, vipSeats)`, wydziel blok wyceny do `TicketPricer.price`. Dwa wyjścia pętli wracają jako jeden obiekt z nazwą z domeny.
**Po:**

```java
Pricing pricing = pricer.price(request);
```

**Uruchom:** test zielony.
**Co powiedzieć:** `vipSeats` potrzebuje powiadomienie, a liczy cennik - rekord jawnie opisuje ten przepływ zamiast ukrytej zmiennej lokalnej.
**Snapshot:** `step2/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s03 1 2`

### Krok 3: Extract Class - powiadomienie, BookingDesk jako orkiestrator

**W IDE:** wydziel blok powiadomienia do `BookingNotifier.bookingConfirmed(request, pricing)`. `Outbox` przechodzi do notifiera. Dodaj konstruktor pakietowy przyjmujący trzech współpracowników, publiczny zostaje `BookingDesk(Outbox)`.
**Po:**

```java
public String book(BookingRequest request) {
    Optional<String> error = validator.firstError(request);
    if (error.isPresent()) {
        return error.get();
    }
    Pricing pricing = pricer.price(request);
    notifier.bookingConfirmed(request, pricing);
    return "OK " + pricing.total();
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** klasa składa kroki, ale nie zna ich szczegółów. Stan (`Outbox`) ma jednego właściciela.
**Snapshot:** `step3/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s03 2 3`

### Rozwiązanie i uzasadnienie

`step3/BookingDesk` z trzema współpracownikami. Publiczne API bez zmian, więc klienci niczego nie zauważają. Kierunek zależności jest czytelny: orkiestrator zna usługi, usługi nie znają orkiestratora.

### Pułapki

- Podział według liczby linii, a nie powodu zmiany.
- Dwie kopie stanu (np. `Outbox` w `BookingDesk` i w `BookingNotifier`).
- Przeniesienie powiadomienia przed wycenę "bo tak ładniej" - zmiana kolejności efektów.
- W prawdziwym systemie: rozdzielenie transakcji (zapis i wysyłka w innych granicach).

### Pytanie do sali

Czy `TicketPricer` powinien dostać `BookingRequest`, czy tylko format i listę miejsc?

## Scena s04. Remove Duplication - rabat grupowy w kasie i w sklepie

**Temat ze slajdów:** 4. Remove Duplication - duplikacja wiedzy; Remove Duplication - po zmianie
**Pakiet:** `pl.training.workshop.m7.s04_removeduplication` · **Test:** `scripts/warsztat.sh test m7/s04`
**Czas:** ~12 min

### Co widzimy

Reguła "10+ biletów = -10%" żyje w dwóch miejscach, zapisana inaczej: `BoxOffice` (pętla, `> 9`, `multiply(0.10)`, HALF_UP) i `WebShop` (stream, `>= 10`, `x * 10 / 100`, **HALF_EVEN**). Tekstowo niepodobne, ale to ta sama wiedza. Czy różnica w zaokrągleniu to decyzja, czy przypadek?

```java
// BoxOffice
BigDecimal discount = sum.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
// WebShop
tickets = tickets.subtract(tickets.multiply(BigDecimal.TEN)
        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_EVEN));
```

Test `S04RoundingDecisionTest` pokazuje przypadek brzegowy: 3 x student 2D (18.75) + 7 x normalny = 231.25, rabat 23.125. Kasa odejmuje 23.13, sklep 23.12.

### Krok 1: ujednolicenie zapisu (bez zmiany zachowania)

**W IDE:** w obu klasach Extract Constant (⌥⌘C) `GROUP_SIZE` i `GROUP_DISCOUNT`, Rename (⇧F6) `sum` na `tickets`, warunek `> 9` na `>= GROUP_SIZE`, w `WebShop` stream na pętlę i `x * 10 / 100` na `multiply(GROUP_DISCOUNT).setScale(2, HALF_EVEN)`.
**Po:**

```java
if (ticketPrices.size() >= GROUP_SIZE) {
    BigDecimal discount = tickets.multiply(GROUP_DISCOUNT).setScale(2, RoundingMode.HALF_EVEN);
    tickets = tickets.subtract(discount);
}
```

**Uruchom:** `scripts/warsztat.sh test m7/s04` - wszystko zielone, sklep nadal daje 228.13 w przypadku brzegowym.
**Co powiedzieć:** po ujednoliceniu obie wersje różnią się jedną linią. Różnica przestała być ukryta w formie zapisu i można o niej rozmawiać z biznesem.
**Snapshot:** `step1/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s04 0 1`

### Krok 2: decyzja - HALF_UP także w sklepie (zmiana kontraktu)

**W IDE:** ręcznie `HALF_EVEN` na `HALF_UP` w `WebShop`. Osobny commit z opisem decyzji.
**Po:**

```java
BigDecimal discount = tickets.multiply(GROUP_DISCOUNT).setScale(2, RoundingMode.HALF_UP);
```

**Uruchom:** test zielony: `step2DeliberatelyAlignsWebWithBoxOffice` oczekuje 228.12, zwykłe koszyki bez zmian.
**Co powiedzieć:** to NIE jest refaktoryzacja - klient sklepu w przypadku brzegowym zapłaci grosz mniej. Robimy to świadomie, osobno i z testem, który mówi to wprost.
**Snapshot:** `step2/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s04 1 2`

### Krok 3: wydzielenie reguły

**W IDE:** w `BoxOffice` ⌥⌘M na pętli i rabacie, nazwa `ticketsTotal`, statyczna. F6 do nowej klasy `GroupDiscount`. W `WebShop` zastąp identyczny fragment wywołaniem (IntelliJ po ekstrakcji sam proponuje zastąpienie duplikatów w tej samej klasie - tu robimy to ręcznie).
**Po:**

```java
// WebShop
BigDecimal fees = FEE.multiply(BigDecimal.valueOf(ticketPrices.size()));
return GroupDiscount.ticketsTotal(ticketPrices).add(fees).setScale(2, RoundingMode.HALF_UP);
```

**Uruchom:** test zielony.
**Co powiedzieć:** wspólna reguła ma jednego właściciela, a różnica (opłata online) zostaje jawna w `WebShop`. Przełączaj po jednej ścieżce: najpierw kasa, test, potem sklep.
**Snapshot:** `step3/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s04 2 3`

### Rozwiązanie i uzasadnienie

`step3/GroupDiscount`: reguła 10+/-10%/HALF_UP w jednym miejscu. Sekwencja ze slajdu zachowana: scharakteryzuj obie wersje osobno, porównaj brzegi, ujednolić, zdecyduj o różnicy, wydziel, przełącz po jednej ścieżce.

### Pułapki

- Wydzielenie wspólnej metody z parametrem `RoundingMode mode` "żeby nic nie zmieniać" - flaga utrwala przypadkową różnicę.
- Sklejanie podobnego tekstu, który jest inną wiedzą (np. opłata online wygląda jak rabat, ale ma innego właściciela).
- Test tylko na "ładnych" kwotach (250.00) - różnica w zaokrągleniu przechodzi niezauważona.

### Pytanie do sali

Kto w Waszej firmie może zdecydować, że HALF_EVEN w sklepie był błędem, a nie wymaganiem?

## Scena s05. Break Method - repertuar dnia

**Temat ze slajdów:** 5. Break Method - intencja; Break Method - bezpieczna sekwencja
**Pakiet:** `pl.training.workshop.m7.s05_breakmethod` · **Test:** `scripts/warsztat.sh test m7/s05`
**Czas:** ~10 min

### Co widzimy

`RepertoireBuilder.build` miesza trzy poziomy abstrakcji: kontrolę wejścia (null w liście), porządkowanie (odwołane seanse, sortowanie po godzinie i tytule) i renderowanie tekstu. Test obserwuje tekst albo wyjątek (typ i komunikat) oraz to, czy lista klienta pozostała nietknięta.

```java
List<Screening> active = new ArrayList<>();
for (Screening screening : copy) { if (!screening.cancelled()) { active.add(screening); } }
active.sort(Comparator.comparing(Screening::start).thenComparing(Screening::title));
StringBuilder text = new StringBuilder("REPERTUAR\n");
```

### Krok 1: Extract Method - fragment z jednym wejściem i jednym wynikiem

**W IDE:** zaznacz pierwszą pętlę (kopia z kontrolą null), ⌥⌘M, nazwa `validateAndCopy`.
**Po:**

```java
List<Screening> copy = validateAndCopy(screenings);
```

**Uruchom:** `scripts/warsztat.sh test m7/s05` - 12 testów zielonych, w tym wyjątek dla `null`.
**Co powiedzieć:** zaczynamy od fragmentu z najmniejszą liczbą zależności i przenosimy go dosłownie. Nazwa przychodzi po teście.
**Snapshot:** `step1/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s05 0 1`

### Krok 2: Extract Method - porządkowanie

**W IDE:** zaznacz filtr i sortowanie, ⌥⌘M, nazwa `order`.
**Po:**

```java
List<Screening> active = order(copy);
```

**Uruchom:** test zielony - "wejście bez zmian: true".
**Co powiedzieć:** sortujemy kopię. Gdyby ktoś "uprościł" to do `screenings.sort(...)`, lista klienta zostałaby przestawiona - test to wykryje.
**Snapshot:** `step2/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s05 1 2`

### Krok 3: Extract Method - renderowanie i Inline Variable

**W IDE:** zaznacz budowę tekstu, ⌥⌘M `render`. Rename (⇧F6) zmiennych na `validated` i `ordered`.
**Po:**

```java
public String build(List<Screening> screenings) {
    List<Screening> validated = validateAndCopy(screenings);
    List<Screening> ordered = order(validated);
    return render(ordered);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** metoda opisuje algorytm na jednym poziomie abstrakcji. Dane łatwo przechodzą między krokami (lista, lista, tekst), więc Method Object ze sceny s02 byłby tu przerostem formy.
**Snapshot:** `step3/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s05 2 3`

### Rozwiązanie i uzasadnienie

`step3/RepertoireBuilder`: trzy etapy, każdy z jednym wejściem i jednym wyjściem. Gdyby etapy miały osobne modele danych (np. wiersz raportu zamiast `Screening`), następnym ruchem byłby Split Phase.

### Pułapki

- Ekstrakcja pętli z `return`/`break`/`continue` w środku bez sprawdzenia, dokąd skacze sterowanie.
- Zastąpienie `validateAndCopy` przez `List.copyOf(screenings)` - dla `null` rzuca NPE zamiast `IllegalArgumentException` (inny typ i komunikat wyjątku).
- Sortowanie listy wejściowej zamiast kopii (aliasowanie).

### Pytanie do sali

Kiedy `order` i `render` zasługują na osobne klasy, a kiedy wystarczą prywatne metody?

## Scena s06. Introduce Parameter Object - termin seansu

**Temat ze slajdów:** 6. Introduce Parameter Object - data clump jako pojęcie; Parameter Object - walidacja, migracja i ryzyka
**Pakiet:** `pl.training.workshop.m7.s06_parameterobject` · **Test:** `scripts/warsztat.sh test m7/s06`
**Czas:** ~12 min

### Co widzimy

`ScreeningPlanner` ma dwie metody z tą samą czwórką parametrów: `screeningId, date, hall, format`. To pojęcie "termin seansu w sali", którego nikt nie nazwał. Walidacja sali i formatu siedzi tylko w `ticketPrice`, a `describe` przyjmuje wszystko.

```java
public String describe(String screeningId, LocalDate date, int hall, String format)
public BigDecimal ticketPrice(String screeningId, LocalDate date, int hall, String format)
```

### Krok 1: Introduce Parameter Object (stan przejściowy, bez walidacji)

**W IDE:** na `describe` Refactor → Introduce Parameter Object, rekord `ScreeningSlot`, zaznacz "keep method as delegate" (zostaw starą sygnaturę). Powtórz dla `ticketPrice`, wskazując istniejący rekord. Dodaj `@Deprecated` na starych sygnaturach.
**Po:**

```java
public record ScreeningSlot(String screeningId, LocalDate date, int hall, String format) {
}

@Deprecated
public String describe(String screeningId, LocalDate date, int hall, String format) {
    return describe(new ScreeningSlot(screeningId, date, hall, format));
}
```

**Uruchom:** `scripts/warsztat.sh test m7/s06` - nowe API i stare sygnatury dają te same wyniki.
**Co powiedzieć:** rekord bez walidacji to celowy stan przejściowy - kontrole zostają tam, gdzie były. Stare sygnatury pozwalają migrować wywołania po jednym.
**Snapshot:** `step1/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s06 0 1`

### Krok 2: Move Method - typ przyciąga zachowanie

**W IDE:** w `describe(ScreeningSlot)` F6 (Move) do `ScreeningSlot`, nazwa `label()`. `describe` deleguje.
**Po:**

```java
public String label() {
    return screeningId + " " + date + " sala " + hall + " (" + format + ")";
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** po nazwaniu pojęcia pojawia się miejsce na zachowanie, które do niego należy.
**Snapshot:** `step2/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s06 1 2`

### Krok 3: walidacja w konstruktorze rekordu (zmiana kontraktu)

**W IDE:** przenieś kontrolę sali i formatu z `ticketPrice` do kanonicznego konstruktora kompaktowego `ScreeningSlot`. W `ticketPrice` gałąź `default -> throw` staje się zbędna.
**Po:**

```java
public ScreeningSlot {
    if (hall < 1 || hall > 8) {
        throw new IllegalArgumentException("nie ma sali " + hall + " (" + screeningId + ")");
    }
    if (!FORMATS.contains(format)) { ... }
}
```

**Uruchom:** test zielony. `S06ValidationMomentTest` pokazuje różnicę: do kroku 2 `describe` dla sali 12 zwraca tekst, a `ticketPrice` rzuca. W kroku 3 wyjątek leci już przy `new ScreeningSlot(...)`.
**Co powiedzieć:** ten sam komunikat, inny moment. Klient, który logował opis przed wyceną, przestanie dostawać log. To zmiana kontraktu - osobny krok i osobny commit.
**Snapshot:** `step3/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s06 2 3`

### Rozwiązanie i uzasadnienie

`step3/ScreeningSlot` jest poprawny z definicji, planner nie musi niczego sprawdzać. Stare sygnatury zostają do końca migracji klientów, potem Safe Delete (⌘⌦).

### Pułapki

- Walidacja w rekordzie w tym samym kroku co wprowadzenie rekordu - nie wiadomo, który ruch zmienił zachowanie.
- Worek `Parameters` z flagami zamiast pojęcia z domeny.
- Rekord jest tylko płytko niemutowalny: komponent `List` bez kopii defensywnej. `List.copyOf` zmienia semantykę aliasowania i odrzuca `null`.
- Komponenty publicznego rekordu to API - zmiana nazwy komponentu łamie klientów i serializację.

### Pytanie do sali

Czy `date` naprawdę należy do tego pojęcia, skoro `ticketPrice` jej nie używa?

## Scena s07. Remove Arrowhead Antipattern - bramka rezerwacji

**Temat ze slajdów:** 7. Remove Arrowhead Antipattern; Remove Arrowhead - po zmianie
**Pakiet:** `pl.training.workshop.m7.s07_arrowhead` · **Test:** `scripts/warsztat.sh test m7/s07`
**Czas:** ~10 min

### Co widzimy

`BookingGate.book` ma pięć poziomów zagnieżdżenia, a główna ścieżka (`BOOKED`) siedzi na samym dnie. Wynik zbiera zmienna `result`. Na końcu metoda zapisuje wpis do audytu - **dla każdej ścieżki**.

```java
if (attempt.screeningFound()) {
    if (attempt.salesOpen()) {
        if (!attempt.customerBlocked()) {
            if (attempt.requestedSeats() > 0) {
                if (attempt.requestedSeats() <= attempt.freeSeats()) {
...
audit.add(attempt.email() + " -> " + result);
return result;
```

Test to macierz gałęzi z kombinacjami sprawdzającymi priorytet (np. brak seansu i jednocześnie zamknięta sprzedaż) plus zawartość audytu.

### Krok 1: Extract Method - oddziel decyzję od efektu

**W IDE:** zaznacz cały grot (od `String result;` do końca `if`), ⌥⌘M, nazwa `decide`.
**Po:**

```java
public String book(BookingAttempt attempt) {
    String result = decide(attempt);
    audit.add(attempt.email() + " -> " + result);
    return result;
}
```

**Uruchom:** `scripts/warsztat.sh test m7/s07` - 28 testów zielonych.
**Co powiedzieć:** najpierw chronimy efekt uboczny. Gdybyśmy wstawili guard clauses od razu w `book`, każde wczesne `return` ominęłoby audyt - test pokazałby puste listy.
**Snapshot:** `step1/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s07 0 1`

### Krok 2: odwróć najbardziej zewnętrzny warunek

**W IDE:** kursor na `if (attempt.screeningFound())`, ⌥⏎ → Invert 'if' condition, potem usuń `else` (⌥⏎ → Remove redundant 'else' albo ręcznie) i zamień przypisanie na `return "NO_SCREENING"`.
**Po:**

```java
if (!attempt.screeningFound()) {
    return "NO_SCREENING";
}
String result;
if (attempt.salesOpen()) { ...
```

**Uruchom:** test zielony.
**Co powiedzieć:** jeden poziom naraz, test po każdym. Kolejność warunków się nie zmienia - to ona definiuje priorytet błędów.
**Snapshot:** `step2/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s07 1 2`

### Krok 3: spłaszcz pozostałe poziomy

**W IDE:** powtórz ruch z kroku 2 dla każdego kolejnego poziomu. Na końcu zmienna `result` znika (Inline ⌥⌘N).
**Po:**

```java
if (!attempt.screeningFound()) { return "NO_SCREENING"; }
if (!attempt.salesOpen()) { return "SALES_CLOSED"; }
if (attempt.customerBlocked()) { return "CUSTOMER_BLOCKED"; }
if (attempt.requestedSeats() <= 0) { return "NO_SEATS_REQUESTED"; }
if (attempt.requestedSeats() > attempt.freeSeats()) { return "SOLD_OUT"; }
return "BOOKED";
```

**Uruchom:** test zielony.
**Co powiedzieć:** zaprzeczenie `requestedSeats() <= freeSeats()` to `>`, nie `>=` - przypadek "dokładnie tyle wolnych" w teście tego pilnuje.
**Snapshot:** `step3/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s07 2 3`

### Rozwiązanie i uzasadnienie

`step3/BookingGate`: czysta funkcja decyzji z guard clauses i jedno miejsce efektu ubocznego. Priorytet warunków identyczny jak w start.

### Pułapki

- Guard clause w metodzie z efektem na końcu (audyt, log, zwolnienie zasobu) - wczesny `return` go omija. Zasoby: try-with-resources albo `finally`.
- Przestawienie warunków "od najczęstszego" - zmienia priorytet błędów.
- Błędne zaprzeczenie przy odwracaniu (`<=` na `>=`).

### Pytanie do sali

Gdyby `customerBlocked()` robił zapytanie do bazy, czy wolno przenieść go na początek?

## Scena s08. Introduce Design by Contract Checks - pula miejsc

**Temat ze slajdów:** 8. Introduce Design by Contract Checks; Design by Contract - jawne kontrole
**Pakiet:** `pl.training.workshop.m7.s08_designbycontract` · **Test:** `scripts/warsztat.sh test m7/s08`
**Czas:** ~10 min

### Co widzimy

`SeatPool` pilnuje liczby wolnych miejsc seansu. Nie ma żadnych kontraktów: `reserve(-2)` po cichu dodaje dwa miejsca, a `release(15)` po rezerwacji 10 daje 105 wolnych w sali na 100. Test `startSilentlyCorruptsStateForInvalidInput` dokumentuje te niemożliwe stany.

```java
public boolean reserve(int seats) {
    if (seats > remaining) {
        return false;
    }
    remaining = remaining - seats;
    return true;
}
```

Uwaga: `false` przy braku miejsc to **poprawna, udokumentowana odpowiedź**, nie naruszenie kontraktu.

### Krok 1: warunki wstępne (to nie jest refaktoryzacja)

**W IDE:** utwórz klasę `Contracts` z `require` (IllegalArgumentException) i `ensure` (IllegalStateException). Na początku `reserve`, `release` i konstruktora dodaj `Contracts.require(...)` - **przed pierwszą mutacją**.
**Po:**

```java
public void release(int seats) {
    Contracts.require(seats > 0, "seats must be positive");
    Contracts.require(seats <= capacity - remaining, "cannot release more seats than reserved");
    remaining = remaining + seats;
}
```

**Uruchom:** `scripts/warsztat.sh test m7/s08` - poprawne użycia bez zmian, niepoprawne rzucają wyjątek i nie zmieniają stanu ("zostalo 100", "zostalo 90").
**Co powiedzieć:** dla niepoprawnych wejść zachowanie się zmieniło - dlatego test ma dla nich osobne oczekiwania. Nie zamieniamy `false` na wyjątek: to zmieniłoby kontrakt także dla poprawnych klientów.
**Snapshot:** `step1/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s08 0 1`

### Krok 2: warunki końcowe i niezmiennik

**W IDE:** ręcznie: zapamiętaj `previous`, policz `next`, `checkInvariant(next)` przed przypisaniem, po przypisaniu `Contracts.ensure(...)`.
**Po:**

```java
int previous = remaining;
int next = previous - seats;
checkInvariant(next);
remaining = next;
Contracts.ensure(remaining == previous - seats, "reserve must reduce remaining by seats");
```

**Uruchom:** test zielony - dla poprawnych wejść nic się nie zmienia.
**Co powiedzieć:** przy obecnym kodzie te kontrole nie mogą się nie udać. Chronią przyszłe zmiany: ktoś dopisze rabat "2 za 1" i niezmiennik złapie błąd, zanim stan się zepsuje.
**Snapshot:** `step2/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s08 1 2`

### Rozwiązanie i uzasadnienie

`step2/SeatPool`: `require` przed mutacją, niezmiennik sprawdzany dla kandydata przed przypisaniem, `ensure` po operacji. Jawne kontrole zamiast `assert`.

### Pułapki

- `assert seats > 0` - domyślnie wyłączony (bez `-ea` nic nie sprawdza), a Maven Surefire włącza go w testach. Test zielony, produkcja bez ochrony.
- Kontrola po mutacji - wyjątek leci, ale stan już jest zepsuty.
- Zamiana historycznego `IllegalArgumentException` na własny wyjątek "przy okazji" - klienci mogą go łapać.
- Podklasa wzmacniająca warunek wstępny (np. "maks. 6 miejsc naraz") łamie LSP.

### Pytanie do sali

Czy "nie więcej miejsc, niż zostało" to warunek wstępny, czy zwykła odpowiedź `false`? Kto o tym decyduje?

## Scena s09. Remove Double Negative - wstęp do strefy VIP

**Temat ze slajdów:** 9. Remove Double Negative
**Pakiet:** `pl.training.workshop.m7.s09_doublenegative` · **Test:** `scripts/warsztat.sh test m7/s09`
**Czas:** ~8 min

### Co widzimy

`LoungeAccess` decyduje, czy klient wejdzie do strefy VIP. `Customer` ma komponent `notVip`, a `Voucher` predykat `isNotExpired`. Każdy warunek trzeba odwracać w głowie.

```java
if (!customer.notVip()) {
    return true;
}
...
if (!voucher.isNotExpired(today)) {
    return false;
}
```

### Krok 1: pozytywne predykaty delegujące

**W IDE:** w `Customer` dodaj `vip()` zwracające `!notVip`, w `Voucher` `isExpired(today)` zwracające `!isNotExpired(today)`. `LoungeAccess` bez zmian.
**Po:**

```java
public boolean isExpired(LocalDate today) {
    return !isNotExpired(today);
}
```

**Uruchom:** `scripts/warsztat.sh test m7/s09` - 20 testów zielonych.
**Co powiedzieć:** pozytywna nazwa musi być dokładnym dopełnieniem starej. Voucher ważny "do dziś włącznie" - test ma przypadek granicy.
**Snapshot:** `step1/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s09 0 1`

### Krok 2: migracja użyć po jednym

**W IDE:** w `LoungeAccess` zamień `!customer.notVip()` na `customer.vip()` (w obu metodach) i `!voucher.isNotExpired(today)` na `voucher.isExpired(today)`. Test po każdej zamianie.
**Po:**

```java
if (customer.vip()) {
    return true;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** mechaniczna zamiana `!negatyw()` na `pozytyw()` - bez wymyślania logiki od nowa.
**Snapshot:** `step2/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s09 1 2`

### Krok 3: odwrócenie delegacji

**W IDE:** w `Voucher` Inline (⌥⌘N) `isNotExpired` do `isExpired` i uprość `!!`. W `Customer` Rename (⇧F6) komponentu `notVip` na `vip` wymaga odwrócenia wartości u wszystkich, którzy tworzą rekord - zrób to ręcznie i usuń metodę `vip()` (teraz akcesor).
**Po:**

```java
public record Customer(String email, boolean vip) {
}
```

**Uruchom:** test zielony. Zwróć uwagę na adapter w teście: dla `step3` tworzy `new Customer(email, !notVip)`.
**Co powiedzieć:** to jest migracja granicy. Gdyby rekord trafiał do JSON-a albo bazy, pole `notVip` w danych trzeba by migrować osobno.
**Snapshot:** `step3/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s09 2 3`

### Rozwiązanie i uzasadnienie

`step3`: `customer.vip()` i `voucher.isExpired(today)`. Sekwencja ze slajdu: predykat pozytywny delegujący, migracja po jednym użyciu, odwrócenie delegacji.

### Pułapki

- `Boolean` z `null` nie jest dwustanowy: `!notVip` rzuca NPE, `!Boolean.TRUE.equals(notVip)` nie. Pozytywna wersja musi zachować, co znaczy `null`.
- Rename komponentu rekordu przez ⇧F6 bez odwrócenia wartości - kompiluje się, a znaczenie jest odwrotne.
- "Dopełnienie" z przesuniętą granicą (`isBefore` zamiast `!isAfter`).

### Pytanie do sali

Gdzie w Waszym kodzie negatywna nazwa żyje w kolumnie bazy albo w pliku konfiguracyjnym?

## Scena s10. Remove Boolean Method Parameters - migracja API

**Temat ze slajdów:** 11. Remove Boolean Method Parameters; Java 25 w tym module (zgodność binarna)
**Pakiet:** `pl.training.workshop.m7.s10_booleanparameter` · **Test:** `scripts/warsztat.sh test m7/s10`
**Czas:** ~12 min

### Co widzimy

`TicketService.book(title, format, seats, online, ownGlasses)` ma dwie flagi. Klienci: `MobileApp` woła `book(..., true, false)`, `BoxOfficeTerminal` woła `book(..., false, customerHasGlasses)`. Z miejsca wywołania nie widać, co znaczy `true`.

```java
return tickets.book(title, format, seats, true, false);
```

`customerHasGlasses` w terminalu to dana z formularza, a nie flaga sterująca. Nie każdy `boolean` jest zapachem.

### Krok 1: jawne metody dla kanału, stara metoda @Deprecated

**W IDE:** dodaj `bookOnline(...)` i `bookAtBoxOffice(...)`, prywatne `book(..., Channel, ...)` z wewnętrznym enumem. Stara publiczna metoda dostaje `@Deprecated` i deleguje.
**Po:**

```java
@Deprecated
public String book(String title, String format, int seats, boolean online, boolean ownGlasses) {
    return online
            ? bookOnline(title, format, seats, ownGlasses)
            : bookAtBoxOffice(title, format, seats, ownGlasses);
}
```

**Uruchom:** `scripts/warsztat.sh test m7/s10` - 31 testów zielonych. Kompilator zgłasza ostrzeżenia `[deprecation]` w obu klientach.
**Co powiedzieć:** ostrzeżenia kompilatora to lista klientów do migracji. Stara metoda zostaje, bo stary `.class` innego modułu oczekuje tej sygnatury (zgodność binarna).
**Snapshot:** `step1/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s10 0 1`

### Krok 2: druga flaga jako typ

**W IDE:** utwórz `enum Glasses { OWN, RENTED }`. Na `bookOnline` Change Signature (⌘F6): typ parametru `boolean` na `Glasses`. To samo dla `bookAtBoxOffice`. Stara metoda tłumaczy `boolean` na `Glasses`.
**Po:**

```java
public String bookOnline(String title, String format, int seats, Glasses glasses)
```

**Uruchom:** test zielony.
**Co powiedzieć:** nie robimy czterech metod na każdą kombinację (`bookOnlineWithOwnGlasses`...). Kanał jest metodą, okulary są wartością. Przy trzech i więcej flagach - obiekt polityki i przegląd odpowiedzialności.
**Snapshot:** `step2/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s10 1 2`

### Krok 3: migracja klientów

**W IDE:** w `MobileApp` zamień wywołanie na `bookOnline(title, format, seats, Glasses.RENTED)`. Test. W `BoxOfficeTerminal` - `bookAtBoxOffice(...)` z tłumaczeniem `customerHasGlasses` na `Glasses`. Test.
**Po:**

```java
return tickets.bookOnline(title, format, seats, Glasses.RENTED);
```

**Uruchom:** test zielony, ostrzeżeń `[deprecation]` już nie ma.
**Co powiedzieć:** jeden klient naraz, test po każdym. Wywołanie czyta się bez zaglądania do sygnatury.
**Snapshot:** `step3/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s10 2 3`

### Krok 4: Safe Delete starej metody

**W IDE:** na starym `book(...)` Safe Delete (⌘⌦) - IntelliJ potwierdza brak użyć w kodzie produkcyjnym.
**Po:**

```java
public String bookOnline(String title, String format, int seats, Glasses glasses) { ... }
public String bookAtBoxOffice(String title, String format, int seats, Glasses glasses) { ... }
```

**Uruchom:** test zielony (testy starego API obejmują tylko start..step3).
**Co powiedzieć:** koniec okresu przejściowego to decyzja z jawnym kryterium ("wszyscy klienci zmigrowani, wydanie X"). W bibliotece publicznej to zmiana łamiąca zgodność binarną.
**Snapshot:** `step4/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s10 3 4`

### Rozwiązanie i uzasadnienie

`step4/TicketService`: dwie jawne metody i enum. Migracja wg slajdu: jawne metody, klienci pojedynczo, stara metoda delegująca do końca migracji, usunięcie.

### Pułapki

- Usunięcie starej metody w tym samym commicie co dodanie nowych.
- Zamiana każdego `boolean` na enum, także danych z formularza.
- Metoda na każdą kombinację flag zamiast przeglądu odpowiedzialności.

### Pytanie do sali

Jak długo trzymać `@Deprecated` w module używanym przez inne zespoły? Kto mierzy, czy ktoś go jeszcze woła?

## Scena s11. Remove Middle Man - fasada kina

**Temat ze slajdów:** 12. Remove Middle Man
**Pakiet:** `pl.training.workshop.m7.s11_middleman` · **Test:** `scripts/warsztat.sh test m7/s11`
**Czas:** ~8 min

### Co widzimy

`CinemaFacade` deleguje 1:1 do `ScreeningCatalog` - prawie. `freeSeats` po cichu tłumaczy `NoSuchElementException` na 0, więc `SeatBadge` pokazuje "WYPRZEDANE" dla nieznanego seansu. Dwóch klientów: `SeatBadge` i `DailyBoard`.

```java
public int freeSeats(String id) {
    try {
        return catalog.freeSeats(id);
    } catch (NoSuchElementException e) {
        return 0;
    }
}
```

### Krok 1: pośrednik staje się czystym forwarderem

**W IDE:** przenieś `try/catch` do prywatnej metody `freeSeats` w `SeatBadge` (jedyny klient, który polega na tłumaczeniu). W fasadzie zostaje zwykłe delegowanie.
**Po:**

```java
// SeatBadge
private int freeSeats(String id) {
    try {
        return cinema.freeSeats(id);
    } catch (NoSuchElementException e) {
        return 0;
    }
}
```

**Uruchom:** `scripts/warsztat.sh test m7/s11` - przypadek "nieznany seans wygląda jak wyprzedany" nadal zielony.
**Co powiedzieć:** najpierw sprawdź, co pośrednik naprawdę robi: autoryzacja, transakcja, telemetria, retry, translacja błędów. Tu była translacja - przenosimy ją do klienta, zanim usuniemy pośrednika.
**Snapshot:** `step1/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s11 0 1`

### Krok 2: pierwszy klient rozmawia z katalogiem

**W IDE:** w `SeatBadge` Change Signature (⌘F6) konstruktora: `CinemaFacade` na `ScreeningCatalog`, pole przez ⇧F6 na `catalog`.
**Po:**

```java
public SeatBadge(ScreeningCatalog catalog) {
    this.catalog = Objects.requireNonNull(catalog, "catalog");
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** jeden klient naraz. `DailyBoard` wciąż używa fasady i działa.
**Snapshot:** `step2/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s11 1 2`

### Krok 3: ostatni klient i Safe Delete fasady

**W IDE:** ten sam ruch w `DailyBoard` (`cinema.screenings()` na `catalog.all()`), potem Safe Delete (⌘⌦) na `CinemaFacade`.
**Po:**

```java
return catalog.all().stream()
        .map(screening -> screening.id() + " " + screening.title() + " " + screening.format())
        .collect(Collectors.joining("\n"));
```

**Uruchom:** test zielony.
**Co powiedzieć:** odwrotny ruch to Hide Delegate. Fasada modułu albo seam testowy mogą być wartościowe - usuwamy pośrednika, który nic nie wnosi.
**Snapshot:** `step3/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s11 2 3`

### Rozwiązanie i uzasadnienie

`step3`: dwaj klienci zależą od `ScreeningCatalog`, zachowanie dla nieznanego seansu zachowane w jedynym miejscu, które go potrzebuje. Test `catalogItselfRejectsUnknownScreening` pokazuje, dlaczego krok 1 był konieczny.

### Pułapki

- Inline wszystkich metod fasady naraz (IntelliJ: Refactor → Remove Middleman) bez sprawdzenia, że któraś tłumaczy błędy.
- Usunięcie fasady, która jest granicą modułu (klienci z innych pakietów zaczną zależeć od szczegółów).
- Utrwalenie dziwnego zachowania ("nieznany = wyprzedany") bez notatki - to kandydat na osobną decyzję.

### Pytanie do sali

Czy "nieznany seans = WYPRZEDANE" to błąd? Kiedy wolno go poprawić?

## Scena s12. Return ASAP - wyszukiwanie wolnego miejsca

**Temat ze slajdów:** 13. Return ASAP
**Pakiet:** `pl.training.workshop.m7.s12_returnasap` · **Test:** `scripts/warsztat.sh test m7/s12`
**Czas:** ~8 min

### Co widzimy

`SeatFinder` ma dwie metody w stylu "jeden punkt wyjścia": `seatClass` z zagnieżdżonymi `if` i zmienną `result`, oraz `firstFree` z pętlą `while (!found && index < size)`. Licznik `inspected` (metryka dla działu IT) jest efektem ubocznym, który musi przetrwać zmianę.

```java
while (!found && index < seats.size()) {
    Seat seat = seats.get(index);
    inspected++;
    if (seat.row() >= minRow && !seat.taken()) {
        result = seat.label();
        found = true;
    }
    index++;
}
```

### Krok 1: guard clauses

**W IDE:** w `seatClass` odwróć zewnętrzne `if` (⌥⏎ → Invert 'if' condition), zamień przypisania `result = ...` na `return ...`, na końcu Inline (⌥⌘N) `result`. W `firstFree` guard `if (seats == null) return Optional.empty();` zamiast otaczającego `if`.
**Po:**

```java
if (seat == null) { return "BRAK"; }
if (seat.taken()) { return "ZAJETE"; }
if (seat.row() >= vipFromRow) { return "VIP"; }
return "STANDARD";
```

**Uruchom:** `scripts/warsztat.sh test m7/s12` - 12 testów zielonych.
**Co powiedzieć:** kolejność sprawdzeń zachowana: miejsce zajęte w rzędzie VIP to nadal "ZAJETE".
**Snapshot:** `step1/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s12 0 1`

### Krok 2: Return ASAP w pętli

**W IDE:** zamień `result = seat.label(); found = true;` na `return Optional.of(seat.label());`, usuń `found` i `result`, ręcznie zamień `while` na `for` z licznikiem `index`. Ostatnia linia `return Optional.empty();`.
**Po:**

```java
for (int index = 0; index < seats.size(); index++) {
    Seat seat = seats.get(index);
    inspected++;
    if (seat.row() >= minRow && !seat.taken()) {
        return Optional.of(seat.label());
    }
}
return Optional.empty();
```

**Uruchom:** test zielony - "sprawdzono=3" dla pierwszego wolnego VIP.
**Co powiedzieć:** zwracamy tam, gdzie wynik jest ostateczny, ale **po** `inspected++`. Gdyby `return` trafił przed inkrementację, licznik różniłby się o jeden i test by to złapał.
**Snapshot:** `step2/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s12 1 2`

### Rozwiązanie i uzasadnienie

`step2/SeatFinder`: bez flag i zmiennych wyniku, `size()`/`get(index)` zachowane (iterator zmieniłby sposób dostępu do listy, np. dla listy leniwej albo z licznikiem wywołań).

### Pułapki

- `return` w `try` uruchamia `finally`, a nagłe zakończenie `finally` może zastąpić wynik.
- Wczesny `return` przed wymaganą mutacją (licznik, log, zwolnienie blokady).
- "Przy okazji" zamiana na stream z `findFirst` - inny sposób iteracji i inne zachowanie dla `null` w liście.

### Pytanie do sali

Czy reguła "jeden return na metodę" ma jeszcze sens w Javie z try-with-resources?

## Scena s13. Remove God Class - kampania na kopii CinemaManager

**Temat ze slajdów:** 10. Remove God Classes - kampania, nie pojedynczy ruch; Remove God Classes - wydzielony fragment i ryzyka; Warsztat 3 (kontekst)
**Pakiet:** `pl.training.workshop.m7.s13_godclass` · **Test:** `scripts/warsztat.sh test m7/s13`
**Czas:** ~30 min

### Co widzimy

`start/CinemaManager` to kopia legacy (razem z `LegacyDb`, `LegacyMailer`, `LegacyPaymentGateway`) - 300 linii, dane w `Object[]` z magicznymi indeksami, cennik, płatności, zwroty, lojalność, powiadomienia, raporty i rozliczenia w jednej klasie. Zanim cokolwiek ruszysz, pokaż mapę: metody, pola `LegacyDb`, efekty (mail, SMS, bramka).

```java
LegacyDb.BOOKINGS.put(id, new Object[] {screeningId, email, phone,
        seats, types, web, total, 0, clock.get(), null, sum});
...
int points = (int) (((Double) b[10]) / 10);
```

Ochrona: `S13GoldenMasterTest` puszcza ten sam scenariusz "jednego dnia kina" co golden master legacy (`S13Script`) i porównuje **pełny wektor**: wyniki wywołań, maile, SMS-y, operacje bramki, raport dzienny i rozliczenia. Oczekiwany tekst to kopia `cinema-manager.approved.txt`. Każdy wariant ma adapter `Driver` w pakiecie testowym (dostęp do haka `clock`).

### Krok 1: wycinek "cennik" - PricingService

**W IDE:** w `book` zaznacz pętlę cen i rabat grupowy razem z zaokrągleniem, ⌥⌘M `ticketsSum`. Zamień parametr `Object[] s` na trzy wartości (Change Signature ⌘F6: `format`, `start`, `vipFromRow`). Wydziel `bookingFee`. Move (F6) obu metod do nowej klasy `PricingService`, pole w `CinemaManager`.
**Po:**

```java
double sum = pricing.ticketsSum((Integer) s[1], (LocalDateTime) s[2], (Integer) s[5],
        seats, types, ownGlasses);
double total = sum + pricing.bookingFee(web, seats.length);
```

**Uruchom:** `scripts/warsztat.sh test m7/s13` - golden master zielony dla start i step1.
**Co powiedzieć:** cennik ma jednego właściciela i nie zna układu `Object[]`. Arytmetyka `double` przeniesiona dosłownie - naprawa typu pieniędzy to osobna decyzja (scena s14).
**Snapshot:** `step1/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s13 0 1`

### Krok 2: wycinek "powiadomienia" - NotificationService

**W IDE:** każde wywołanie `LegacyMailer.send/sms` wydziel (⌥⌘M) do metody o nazwie zdarzenia: `bookingCreated`, `paymentDeclined`, `ticketsPaid` (mail i SMS razem), `bookingExpired`, `bookingCancelled`. Move (F6) do `NotificationService`. `fmt` przenieś do `Formats.amount`, w `CinemaManager` zostaw delegację.
**Po:**

```java
notifications.ticketsPaid(email, (String) b[2], bookingId, (Double) b[6], points);
```

**Uruchom:** test zielony.
**Co powiedzieć:** `CinemaManager` decyduje KIEDY powiadomić, `NotificationService` - CO i JAK. Kolejność efektów (mail przed SMS, powiadomienie po zapisie rezerwacji) jest częścią wektora - golden master pilnuje jej co do linii.
**Snapshot:** `step2/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s13 1 2`

### Krok 3: wycinek "rezerwacje" - Booking i BookingRepository zamiast Object[]

**W IDE:** utwórz klasę `Booking` z nazwanymi polami (status zostaje kodem `int`), `BookingRepository` z `nextId`, `save`, `find`, `all`. Zmień typ mapy `LegacyDb.BOOKINGS` na `Map<String, Booking>` i napraw błędy kompilacji jeden po drugim: `b[7]` na `b.status()`, `b[10]` na `b.ticketsSum()`...
**Po:**

```java
Booking b = bookings.find(bookingId);
...
b.markPaid(card);
int points = (int) (b.ticketsSum() / 10);
```

**Uruchom:** test zielony.
**Co powiedzieć:** kompilator prowadzi tę migrację - każdy magiczny indeks staje się błędem do naprawy. Magazyn zostaje globalny (`LegacyDb`), więc współdzielenie stanu między instancjami się nie zmienia. Przeniesienie mapy do instancji repozytorium zmieniłoby semantykę - to osobny krok.
**Snapshot:** `step3/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s13 2 3`

### Krok 4: wycinek "raporty" - ReportService

**W IDE:** na `dailyReport` i `settlement` Move (F6) do nowej klasy `ReportService` z zależnością `BookingRepository`. W `CinemaManager` zostają metody delegujące (stare API).
**Po:**

```java
public String dailyReport(LocalDate day) {
    return reports.dailyReport(day);
}

public String settlement(String title, int week) {
    return reports.settlement(title, week);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** raporty czytają rezerwacje przez repozytorium, więc zmiana magazynu nie dotknie raportów. Kampanię kontynuujemy (zwroty, lojalność, repertuar), dopóki przynosi wartość dla planowanych zmian - nie przepisujemy wszystkiego.
**Snapshot:** `step4/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s13 3 4`

### Rozwiązanie i uzasadnienie

`step4`: `CinemaManager` jest fasadą o niezmienionym publicznym API, cztery wydzielone klasy mają po jednym powodzie zmiany, a dane rezerwacji mają nazwany typ i jednego właściciela dostępu. Cztery kroki, cztery commity, po każdym identyczny wektor zachowania.

### Pułapki

- "Przepiszmy to od nowa" zamiast pionowych wycinków - brak punktu, w którym system jest zielony.
- Zmiana kolejności efektów przy wydzielaniu (np. SMS przed mailem, mail przed zapisem rezerwacji) - golden master pokazuje różnicę w jednej linii.
- Poprawianie błędów "przy okazji" (np. `double` na `BigDecimal`) w kroku strukturalnym.
- Przeniesienie stanu do instancji usługi: zmiana współdzielenia i czasu życia.
- Transakcje: przykład nie zapewnia atomowości - wyjątek w `NotificationService` po zapisie rezerwacji zostawia rezerwację bez maila. To było tak samo przed zmianą, ale po wydzieleniu łatwiej to przeoczyć. Atomowość (transakcja, outbox, kompensacja) to osobna decyzja.

### Pytanie do sali

Który wycinek zrobilibyście jako następny, jeśli za tydzień dochodzi nowa zasada zwrotów?

## Scena s14. Refaktoryzacja a zmiana kontraktu - zwrot na BigDecimal

**Temat ze slajdów:** Refaktoryzacja a zmiana kontraktu; Pętla pracy (zmiany kontraktu w osobnych krokach)
**Pakiet:** `pl.training.workshop.m7.s14_contractchange` · **Test:** `scripts/warsztat.sh test m7/s14`
**Czas:** ~10 min

### Co widzimy

`RefundCalculator.refund` liczy zwrot na `double` - dokładnie jak w legacy: 100%, 50% albo 0%, minus 3.00, nie poniżej zera, zaokrąglenie `Math.round(x * 100) / 100.0`, format `%.2f`. Kontraktem jest także **format**: zawsze dwa miejsca po przecinku.

```java
refund = refund - 3.00;
if (refund < 0) {
    refund = 0;
}
refund = Math.round(refund * 100) / 100.0;
return String.format(Locale.ROOT, "%.2f", refund);
```

### Krok 1: czysta refaktoryzacja

**W IDE:** zaznacz łańcuch `if/else` wyboru udziału, ⌥⌘M, nazwa `share`, zwraca `double` (0, 1.0, 0.5). W `refund` jedno wyrażenie `ticketsPaid * share(minutes) - 3.00`.
**Po:**

```java
double refund = ticketsPaid * share(minutesBeforeStart) - 3.00;
```

**Uruchom:** `scripts/warsztat.sh test m7/s14` - wszystko zielone, łącznie z przypadkami brzegowymi (29.17 i 0.00).
**Co powiedzieć:** `x * 1.0 == x` w `double` - arytmetyka jest identyczna co do bitu. To jest refaktoryzacja.
**Snapshot:** `step1/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s14 0 1`

### Krok 2: "przy okazji" double na BigDecimal

**W IDE:** pokaż naiwny ruch: `share` zwraca `BigDecimal`, liczymy `BigDecimal.valueOf(ticketsPaid).multiply(...).subtract(FEE).setScale(2, HALF_UP).max(BigDecimal.ZERO)`, zwracamy `toPlainString()`.
**Po:**

```java
BigDecimal refund = BigDecimal.valueOf(ticketsPaid)
        .multiply(share(minutesBeforeStart))
        .subtract(FEE)
        .setScale(2, RoundingMode.HALF_UP)
        .max(BigDecimal.ZERO);
return refund.toPlainString();
```

**Uruchom:** test zwykłych zwrotów zielony, ale `byTheWayStepChangedTwoThingsAtOnce` dokumentuje dwie zmiany: 64.35 zapłacone, anulowanie 2 h przed seansem daje 29.18 zamiast 29.17, a zwrot po starcie to "0" zamiast "0.00".
**Co powiedzieć:** krok wyglądał na porządki, a zmienił dwa elementy kontraktu. Pierwszy (grosz) można uzasadnić regułą domeny, drugi (format) to czysta regresja - paragon i mail pokażą "Zwrot: 0".
**Snapshot:** `step2/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s14 1 2`

### Krok 3: świadoma decyzja

**W IDE:** przestaw `max` przed `setScale(2, HALF_UP)` - format "0.00" wraca. Różnicę groszową zatwierdzamy jako zmianę kontraktu: Javadoc z decyzją, nowe oczekiwanie w teście, osobny commit.
**Po:**

```java
BigDecimal refund = BigDecimal.valueOf(ticketsPaid)
        .multiply(share(minutesBeforeStart))
        .subtract(FEE)
        .max(BigDecimal.ZERO)
        .setScale(2, RoundingMode.HALF_UP);
```

**Uruchom:** test zielony: `deliberateStepChangesOnlyTheApprovedRounding`.
**Co powiedzieć:** kryterium refaktoryzacji to "klient nie dostrzega żadnej **nieuzgodnionej** różnicy". Różnicę w groszach uzgodniliśmy, różnicy w formacie nie.
**Snapshot:** `step3/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s14 2 3`

### Rozwiązanie i uzasadnienie

`step3/RefundCalculator`: `BigDecimal` i HALF_UP zgodnie z regułą domeny, format bez zmian. Historia commitów: refaktoryzacja (krok 1), zmiana typu i reguły zaokrąglenia jako jawna zmiana kontraktu (krok 3). Krok 2 nie powinien trafić do repozytorium.

### Pułapki

- Test tylko na "okrągłych" kwotach - `double` i `BigDecimal` dają ten sam wynik i zmiana przechodzi niezauważona.
- `new BigDecimal(double)` zamiast `BigDecimal.valueOf(double)` - binarne rozwinięcie (64.349999...) i inny wynik zaokrąglenia.
- `BigDecimal.equals` porównuje skalę ("0" różni się od "0.00"), `compareTo` nie.
- Zmiana typu wyjątku, kolejności efektów albo formatu "bo i tak ruszamy ten kod".

### Pytanie do sali

Kto zatwierdza zmianę kontraktu o 1 grosz: programista, księgowość czy klient API?

## Scena s15. Wektor obserwowalnego zachowania - płatność za bilety

**Temat ze slajdów:** Wektor obserwowalnego zachowania; Lista kontrolna przeglądu (zachowanie)
**Pakiet:** `pl.training.workshop.m7.s15_behaviourvector` · **Test:** `scripts/warsztat.sh test m7/s15`
**Czas:** ~12 min

### Co widzimy

`TicketCheckout.pay` to kod **po** "porządkach" kolegi (Consolidate Duplicate Conditional Fragments): mail potwierdzający wysunięto za `if`, więc dostaje go także klient z odrzuconą kartą. Mailer i terminal są statyczne - z testu widać tylko wynik. `S15ResultOnlyTest` jest zielony dla każdego wariantu. Regresja przeszła.

```java
if (charged) {
    booking.markPaid();
} else {
    CinemaMailer.send(booking.email(), "Platnosc odrzucona " + booking.id());
}
CinemaMailer.send(booking.email(), "Bilety " + booking.id() + " oplacone: " + booking.amount());
return charged ? "OK" : "DECLINED";
```

Pokaż tabelę ze slajdu: wynik, wyjątki, stan, współpracownicy, czas, granice. Które z nich ten test widzi?

### Krok 1: seam dla maili - regresja staje się widoczna

**W IDE:** utwórz `@FunctionalInterface Mailer`, Parameterize Constructor, domyślny konstruktor `this(CinemaMailer::send)`, zamień statyczne wywołania na `mailer.send(...)`. Logika bez zmian.
**Po:**

```java
public TicketCheckout() {
    this(CinemaMailer::send);
}
```

**Uruchom:** `scripts/warsztat.sh test m7/s15` - zielony. `step1SeesTheRegressionThatResultOnlyTestMissed` dokumentuje, że po odrzuceniu karty idą dwa maile: "Platnosc odrzucona" i "Bilety B1 oplacone".
**Co powiedzieć:** kod się nie zmienił, zmieniło się to, co widzimy. Test z samym wynikiem nie mógł tego wykryć.
**Snapshot:** `step1/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s15 0 1`

### Krok 2: naprawa regresji pod ochroną testu

**W IDE:** przenieś wysyłkę potwierdzenia do gałęzi sukcesu, zamień `charged ? "OK" : "DECLINED"` na dwa `return`.
**Po:**

```java
if (CardTerminal.charge(card, booking.amount())) {
    booking.markPaid();
    mailer.send(booking.email(), "Bilety " + booking.id() + " oplacone: " + booking.amount());
    return "OK";
}
mailer.send(booking.email(), "Platnosc odrzucona " + booking.id());
return "DECLINED";
```

**Uruchom:** test zielony: `fromStep2MailsAreCorrect`.
**Co powiedzieć:** to naprawa błędu, nie refaktoryzacja - przywracamy kontrakt sprzed "porządków". Wynik metody się nie zmienia, zmienia się efekt uboczny.
**Snapshot:** `step2/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s15 1 2`

### Krok 3: seam dla płatności i pełny wektor

**W IDE:** utwórz `@FunctionalInterface PaymentGateway`, drugi parametr konstruktora, domyślnie `CardTerminal::charge`. W teście oba fałszywe obiekty zapisują do **jednego** dziennika zdarzeń.
**Po:**

```java
public TicketCheckout() {
    this(CinemaMailer::send, CardTerminal::charge);
}
```

**Uruchom:** test zielony: `step3ObservesTheFullVector` sprawdza wynik albo wyjątek (typ i komunikat), obciążenia i maile w jednej kolejności oraz status rezerwacji po operacji.
**Co powiedzieć:** jeden dziennik dla wszystkich współpracowników pokazuje kolejność: najpierw obciążenie, potem mail. Stan po błędzie (`NEW` po odrzuceniu) i brak efektów przy wyjątku też są częścią kontraktu.
**Snapshot:** `step3/`  ·  **Różnica:** `scripts/warsztat.sh diff m7/s15 2 3`

### Rozwiązanie i uzasadnienie

`step3/TicketCheckout` z dwoma seamami i test, który obserwuje pięć wymiarów wektora: wynik, wyjątek, efekty, kolejność efektów i stan. Granica kontraktu jest świadomą decyzją, a nie skutkiem zbyt słabego testu.

### Pułapki

- Test "zielony, bo nic nie sprawdza" - pokrycie linii 100%, a efekty uboczne niewidoczne.
- Osobne listy dla maili i obciążeń - kolejność między współpracownikami ginie.
- Sprawdzanie komunikatu wyjątku bez sprawdzenia, że nie było efektów przed wyjątkiem.
- Logi: jeśli ktoś je parsuje (alerty, audyt), też należą do wektora - wtedy potrzebny kolejny seam.

### Pytanie do sali

Które elementy wektora sprawdzają testy w Waszym najważniejszym module? Czego nie widzą?

## Proponowana kolejność pokazu

**Ścieżka krótka (~85 min):** model bezpieczeństwa i najczęstsze ruchy.

1. s15 Wektor obserwowalnego zachowania, kroki 1 i 3 (~10 min) - ustawia język na cały moduł
2. s01 Break Dependencies, kroki 1, 3 i 4 (~15 min)
3. s02 Extract Method Object (~12 min)
4. s07 Remove Arrowhead, krok 1 i 3 z naiwnym ruchem na żywo (~8 min)
5. s06 Introduce Parameter Object, kroki 1 i 3 (~8 min)
6. s14 Refaktoryzacja a zmiana kontraktu (~10 min)
7. s13 Remove God Class, kroki 1 i 3 (~20 min)

**Ścieżka pełna (~200 min):** kolejność slajdów modułu.

1. Model bezpieczeństwa: s15, s14 (~22 min)
2. Zależności i struktura: s01, s02, s03, s04, s05 (~69 min)
3. Sygnatury i warunki: s06, s07, s08, s09 (~40 min)
4. Klasy i API: s13, s10, s11, s12 (~58 min)

Po s05 i po s09 zrób przerwę na pytania. Sceny s03, s05, s09, s11 i s12 dobrze nadają się na samodzielną pracę uczestników, jeśli brakuje czasu na pokaz (patrz plik zadań modułu 7).
