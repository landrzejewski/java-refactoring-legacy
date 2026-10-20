# Moduł 3. Testowanie a bezpieczeństwo - warsztat CineLegacy: przewodnik prowadzącego

Pięć scen w domenie kina pokazuje na żywo, jak zbudować sieć bezpieczeństwa wokół kodu, który jej nie ma: test charakterystyki przed pierwszą zmianą, seam w klasie, której nie da się uruchomić w teście, wektor obserwowalnego zachowania, równoległą weryfikację (shadow) i jej granice przy efektach ubocznych. Każda scena ma kod wyjściowy (`start`), kompletne snapshoty po każdym kroku (`stepN`) i test, który po każdym ruchu w IDE ma być zielony. Tam, gdzie krok świadomie zmienia zachowanie (s03 krok 2, s04 krok 4), test ma osobne oczekiwania i pokazuje różnicę wprost.

Punktem odniesienia dla całego modułu jest golden master starego systemu: `CinemaManagerGoldenMasterTest` odtwarza "jeden dzień kina" na nietkniętym `legacy/CinemaManager` i porównuje pełny wektor zachowania (wyniki wywołań, maile, SMS-y, obciążenia i zwroty, raport dzienny, rozliczenia) z zatwierdzonym plikiem `src/test/resources/workshop/cinema-manager.approved.txt`. To odpowiedź na pytanie "jak poradzić sobie bez testów?" w skali całego systemu: jeden szeroki, deterministyczny test, zanim powstanie jakikolwiek test jednostkowy. Sceny poniżej pokazują te same techniki w mniejszej skali. Pokaż golden master na początku modułu (3 min) i wracaj do niego przy s01 (charakterystyka) i s03 (wektor zachowania).

Sceny nie powtarzają przykładów z `pl.training.module3` ani ćwiczeń z zadań modułu 3. Tamte ćwiczenia uczestnicy robią sami, a sceny poniżej służą do pokazu.

## Jak prowadzić pokaz

```bash
mvn -q test -Dtest=CinemaManagerGoldenMasterTest   # golden master starego systemu
scripts/warsztat.sh list m3                 # sceny modułu i ich kroki
scripts/warsztat.sh test m3/s02             # testy jednej sceny
scripts/warsztat.sh test m3                 # wszystkie sceny modułu (107 testów)
scripts/warsztat.sh diff m3/s04 1 2         # co zmienia krok 2 względem kroku 1 (0 = start)
scripts/warsztat.sh diff m3/s04 1 2 --word  # to samo, zmiany podświetlone w obrębie linii
scripts/warsztat.sh jump m3/s04 2           # skopiuj step2 do start, gdy brakuje czasu
scripts/warsztat.sh next m3/s04             # następny krok do start + podsumowanie zmian
scripts/warsztat.sh next                    # kolejny krok tej samej sceny (scena zapamiętana)
scripts/warsztat.sh prev                    # krok wstecz
scripts/warsztat.sh status                  # który krok jest teraz w start
scripts/warsztat.sh reset m3/s04            # przywróć start po pokazie
```

- Zasada: **jeden krok - jeden test - jedno zdanie komentarza.** Ruch w IDE, ⌃R na teście sceny, zdanie z sekcji "Co powiedzieć". Zanim ruszysz kod, powiedz, jaki dowód da test po kroku.
- **Krok po kroku bez numerów:** `scripts/warsztat.sh next` wstawia do `start` gotowy następny krok i wypisuje, co się zmieniło. `prev` cofa o krok, `status` mówi, który krok jest teraz w `start`. Skrypt pamięta ostatnią scenę, więc po pierwszym `next m3/sNN` wystarczy samo `next`. Ręczne zmiany w `start` zostają nadpisane - o to chodzi, gdy krok na żywo się rozjechał.
- Pracujesz zawsze w pakiecie `start`. Snapshoty `stepN` służą do `diff` albo do przeskoku (`next`, `jump`), gdy coś się rozjedzie.
- Zanim zaczniesz, pokaż slajd 4.6 "Wektor obserwowalnego zachowania" i wracaj do niego przy każdej scenie: *co tu obserwujemy - wynik, wyjątek, stan, efekty, kolejność?*
- W s02 start **nie ma testów** i to jest puenta: testy pojawiają się dopiero wtedy, gdy kod ma seam. Pierwsze ruchy to automaty IDE o niskim ryzyku (slajd 4.16).
- W s01 i s03 warto najpierw **zrobić naiwny ruch na żywo** (np. "poprawić" znalezisko z charakterystyki, sprawdzić tylko wynik), pokazać, czego test nie widzi, cofnąć (⌘Z) i dopiero wtedy wykonać krok z przewodnika.
- W s04 i s05 część testów to nie tylko równoważność (`SNNEquivalenceTest`), ale też dowody strategii (`SNNSolutionTest`): raport rozbieżności, dziennik efektów ubocznych. Testy, które **dokumentują stan startowy** (np. "start obciąża kartę dwa razy"), zmienią kolor na czerwony, gdy wykonasz kroki na pakiecie `start` - to znak, że start jest już naprawiony. Po pokazie: `scripts/warsztat.sh reset`.
- Kroki, które zmieniają zachowanie (s03 krok 2, s04 krok 4), zapowiadaj na głos: "to jest osobny commit, nie refaktoryzacja".

## Mapa scen

| Scena | Temat ze slajdów | Kroki | Pakiet | Czas |
|---|---|---|---|---|
| s01 | 4.2 Testy charakteryzujące; 4.4 Dobór przypadków; 4.8 Porównanie implementacji | 2 | `m3.s01_characterization` | ~15 min |
| s02 | 4.10 Seam; 4.12 Szew przejściowy; 4.14 Break Dependencies; 4.16 Ruchy IDE bez testów | 4 | `m3.s02_breakdependencies` | ~20 min |
| s03 | 4.6 Wektor obserwowalnego zachowania; 2.2 Weryfikacja stanu a zachowania | 3 | `m3.s03_behaviourvector` | ~12 min |
| s04 | 4.18 Równoległa weryfikacja (parallel run) | 4 | `m3.s04_parallelrun` | ~15 min |
| s05 | 4.19 Granice trybu shadow | 3 | `m3.s05_shadowlimits` | ~12 min |

## Scena s01. Test charakterystyki przed pierwszą zmianą

**Temat ze slajdów:** 4.2 Testy charakteryzujące; 4.4 Dobór przypadków; 4.8 Porównanie implementacji (równoważność etapów); 4.6 Wektor obserwowalnego zachowania
**Pakiet:** `pl.training.workshop.m3.s01_characterization` · **Test:** `scripts/warsztat.sh test m3/s01`
**Czas:** ~15 min

### W skrócie

**Co robimy:** `BookingConfirmation.confirm` drukuje potwierdzenie bez żadnego testu, a w wydruku jest bieżący czas i kwoty zależne od `Locale`. Najpierw zapisujemy w teście pełny dokument, potem robimy szew na zegar i dopiero wtedy pierwszą ekstrakcję.

**Zasada:** Test charakterystyki zapisuje, co kod faktycznie robi, a nie co powinien robić - razem z dziwnymi regułami. Stosujemy go przed pierwszą zmianą kodu bez testów, a ten sam zestaw oczekiwań uruchamiamy na każdym etapie jako test równoważności. Nie mylić z testem, który liczy oczekiwania tym samym algorytmem co kod.

**Efekt:** Mamy deterministyczny test całego dokumentu, wstrzykiwany `Clock` i wydzieloną metodę `ticketPrice`, a stary konstruktor dalej działa. Znaleziska (rabat od 11 biletów, kwoty zależne od `Locale`) zostają zapisane, ale niepoprawione, bo to osobne decyzje.

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

W tej scenie najważniejszy jest test `S01CharacterizationTest`. Powstaje w trzech ruchach (A, B, C), a kod `start/step1/step2` tylko za nim nadąża.

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

**Uruchom:** `scripts/warsztat.sh test m3/s01` - 15 testów zielonych, w tym `fromStep1TheWholeDocumentIsDeterministic` (z `Clock.fixed` porównujemy także linię z czasem, bez scrubbera).
**Co powiedzieć:** to minimalna zmiana, która robi szew dla testu. Stary konstruktor zostaje, więc produkcyjni klienci nie widzą różnicy. Scrubber był rusztowaniem i od tego kroku jest potrzebny tylko dla `start`.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m3/s01 0 1`

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
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m3/s01 1 2`

### Rozwiązanie i uzasadnienie

Test charakterystyki porównuje **pełny dokument**, a nie wybrane liczby, dla przypadków po obu stronach progów. Ten sam zestaw oczekiwań uruchamiamy na `start`, `step1` i `step2`, co jest testem równoważności etapów. Oczekiwania zapisaliśmy raz, zatwierdzone przez człowieka, a nie liczone na nowo przez starą wersję kodu. Kod zmienia się minimalnie: szew na czas i jedna ekstrakcja.

### Pułapki

- Test, który wylicza oczekiwania tym samym algorytmem co kod ("drugi raz to samo"), niczego nie charakteryzuje.
- Test zależny od strefy czasowej, Locale albo bieżącej daty przejdzie na laptopie i padnie na CI. Tutaj `Locale` ustawiamy na czas wywołania i przywracamy w `finally`.
- "Poprawienie" znaleziska z charakterystyki w tym samym commicie co refaktoryzacja. Znalezisko zapisujemy (`ZASTANE: ...`), zgłaszamy i poprawiamy osobno.
- Brak sprawdzenia czułości testu. Zmień na chwilę `> 10` na `>= 10` w `start` i zobacz, że test padnie. Potem cofnij.

### Pytanie do sali

Znaleźliście w charakterystyce ewidentny błąd (rabat od 11 biletów). Kto decyduje, czy go poprawić, i jak powinien wyglądać commit z poprawką?

## Scena s02. Break Dependencies - seam dla zadania przypomnień

**Temat ze slajdów:** 4.10 Seam, punkt aktywacji i cel rozrywania; 4.12 Minimalny szew przejściowy; 4.14 Break Dependencies - przed i po; 4.16 Bezpieczne ruchy automatami IDE, gdy brak testów
**Pakiet:** `pl.training.workshop.m3.s02_breakdependencies` · **Test:** `scripts/warsztat.sh test m3/s02`
**Czas:** ~20 min

### W skrócie

**Co robimy:** `ShowtimeReminderJob` sam tworzy bazę, czyta zegar systemowy i woła statyczny mailer, więc nie da się go uruchomić w teście. Kolejnymi małymi ruchami wyciągamy trzy zależności do konstruktora, aż test może podstawić fałszywą bazę, stały zegar i lambdę zamiast maila.

**Zasada:** Break Dependencies tworzy seam - miejsce, w którym można podstawić inną implementację bez edycji algorytmu, żeby uruchomić kod bez kosztownego otoczenia i obserwować jego komunikację. Wybieramy najwęższy skuteczny seam: parametr, zależność konstruktora, `Clock` albo interfejs funkcyjny, a nie interfejs przed każdą klasą. To nie jest zmiana reguły biznesowej, tylko zmiana miejsca tworzenia zależności.

**Efekt:** Klasa ma pierwszy prawdziwy test z granicą 120/121 minut, a produkcyjny konstruktor składa te same implementacje co wcześniej, łącznie z bazą otwieraną przy każdym `run()`. Kosztem są trzy nowe małe typy i konstruktor z trzema parametrami.

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

Sekwencja ze slajdu 4.15: nazwij zachowanie i punkt obserwacji, wprowadź najmniejszy szew bez zmiany logiki, zabezpiecz go testem z fake/spy, przenieś wybór do jawnego punktu aktywacji, usuń konstrukcję przejściową. Charakterystyki nie da się napisać przed pierwszym ruchem, więc pierwsze ruchy muszą być mechaniczne i wspierane przez IDE (slajd 4.16).

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

**Uruchom:** `scripts/warsztat.sh test m3/s02` - testy `step1...` zielone: z fałszywą bazą zadanie działa, gdy nic nie jest do wysłania.
**Co powiedzieć:** przekazujemy fabrykę, a nie gotową bazę, bo stary kod otwierał połączenie przy każdym `run()`. Gdybyśmy zrobili `this(new LegacyDatabase())`, wyjątek poleciałby już w konstruktorze - zmienilibyśmy czas życia zależności i moment błędu.
**Snapshot:** `step1/`  ·  **Różnica:** `scripts/warsztat.sh diff m3/s02 0 1`

### Krok 2: Clock jako zależność

**W IDE:** ręcznie (Extract Parameter ⌥⌘P dodałby parametr do `run()`, a potrzebujemy zależności konstruktora): pole `Clock clock`, drugi parametr konstruktora, w domyślnym konstruktorze `Clock.systemDefaultZone()`, w `run()` `LocalDateTime.now(clock)`.
**Po:**

```java
LocalDateTime now = LocalDateTime.now(clock);
```

**Uruchom:** test zielony. Test `step2...` pokazuje dwie rzeczy: przypadki bez wysyłki działają przy stałym zegarze, a przypadek "do wysłania" wciąż kończy się wyjątkiem SMTP.
**Co powiedzieć:** czas był ukrytym wejściem. Zegar czytamy w tym samym miejscu co wcześniej (po utworzeniu bazy), więc kolejność się nie zmienia. Następna przeszkoda jest widoczna w teście - statyczny mailer.
**Snapshot:** `step2/`  ·  **Różnica:** `scripts/warsztat.sh diff m3/s02 1 2`

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
**Snapshot:** `step3/`  ·  **Różnica:** `scripts/warsztat.sh diff m3/s02 2 3`

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
**Snapshot:** `step4/`  ·  **Różnica:** `scripts/warsztat.sh diff m3/s02 3 4`

### Rozwiązanie i uzasadnienie

`step4/ShowtimeReminderJob`: trzy zależności w konstruktorze, każda o najwęższym kontrakcie potrzebnym klientowi (`BookingStore` z dwiema metodami, `Clock`, `ReminderSender` z jedną). Reguła przypomnień nie zmieniła się ani o znak. Dopiero teraz można bezpiecznie dodać nową regułę biznesową (np. przypomnienie SMS), bo test widzi i czas, i wysyłki.

### Pułapki

- `this(new LegacyDatabase())` w domyślnym konstruktorze: zmiana czasu życia i momentu wyjątku (test `step1KeepsConnectionLifetimeOfProductionConstructor`).
- Interfejs "na zapas" z wszystkimi metodami `LegacyDatabase` zamiast dwóch potrzebnych.
- `sealed` na interfejsie seamu - test nie podstawi wtedy własnej implementacji.
- Zostawienie Subclass and Override na stałe: klasa otwarta do dziedziczenia tylko dla testu.

### Pytanie do sali

Kiedy Subclass and Override jest lepszym pierwszym ruchem niż Parameterize Constructor?

## Scena s03. Wektor obserwowalnego zachowania - płatność za bilety

**Temat ze slajdów:** 4.6 Wektor obserwowalnego zachowania; 2.2 Weryfikacja stanu a weryfikacja zachowania; 3.4-3.5 Pokrycie: zalety, ograniczenia, progi, nadużycia
**Pakiet:** `pl.training.workshop.m3.s03_behaviourvector` · **Test:** `scripts/warsztat.sh test m3/s03`
**Czas:** ~12 min

### W skrócie

**Co robimy:** Po "porządkach" kolegi `TicketCheckout.pay` wysyła mail z potwierdzeniem także klientowi z odrzuconą kartą, a test sprawdzający tylko wynik tego nie widzi. Wprowadzamy seam dla maili, naprawiamy regresję i dodajemy seam dla płatności, żeby test widział pełny wektor.

**Zasada:** Wektor obserwowalnego zachowania to wszystko, co klient może zauważyć: wynik, wyjątki (typ, komunikat, moment), stan po sukcesie i po błędzie, wywołania współpracowników z ich kolejnością, czas i granice. Test sprawdzający jeden wymiar przepuści regresję w pozostałych, dlatego granica kontraktu ma być świadomą decyzją, a nie skutkiem słabego testu.

**Efekt:** Test widzi wynik, wyjątek, maile i obciążenia karty w jednym dzienniku oraz status rezerwacji. Zachowanie świadomie się zmienia w kroku 2: przy odrzuconej karcie znika błędny mail o opłaceniu - to naprawa błędu, a nie refaktoryzacja, choć wynik metody pozostaje ten sam.

### Co widzimy

`TicketCheckout.pay` to kod **po** "porządkach" kolegi (Consolidate Duplicate Conditional Fragments): mail potwierdzający wysunięto za `if`, więc dostaje go także klient z odrzuconą kartą. Mailer i terminal są statyczne - z testu widać tylko wynik. `S03ResultOnlyTest` jest zielony dla każdego wariantu. Regresja przeszła.

```java
if (charged) {
    booking.markPaid();
} else {
    CinemaMailer.send(booking.email(), "Platnosc odrzucona " + booking.id());
}
CinemaMailer.send(booking.email(), "Bilety " + booking.id() + " oplacone: " + booking.amount());
return charged ? "OK" : "DECLINED";
```

Pokaż tabelę ze slajdu 4.6: wynik, wyjątki, stan, współpracownicy, czas, granice. Które z nich ten test widzi?

### Krok 1: seam dla maili - regresja staje się widoczna

**W IDE:** utwórz `@FunctionalInterface Mailer`, Parameterize Constructor, domyślny konstruktor `this(CinemaMailer::send)`, zamień statyczne wywołania na `mailer.send(...)`. Logika bez zmian.
**Po:**

```java
public TicketCheckout() {
    this(CinemaMailer::send);
}
```

**Uruchom:** `scripts/warsztat.sh test m3/s03` - zielony. `step1SeesTheRegressionThatResultOnlyTestMissed` dokumentuje, że po odrzuceniu karty idą dwa maile: "Platnosc odrzucona" i "Bilety B1 oplacone".
**Co powiedzieć:** kod się nie zmienił, zmieniło się to, co widzimy. Test z samym wynikiem nie mógł tego wykryć.
**Snapshot:** `step1/`  ·  **Różnica:** `scripts/warsztat.sh diff m3/s03 0 1`

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
**Snapshot:** `step2/`  ·  **Różnica:** `scripts/warsztat.sh diff m3/s03 1 2`

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
**Snapshot:** `step3/`  ·  **Różnica:** `scripts/warsztat.sh diff m3/s03 2 3`

### Rozwiązanie i uzasadnienie

`step3/TicketCheckout` z dwoma seamami i test, który obserwuje pięć wymiarów wektora: wynik, wyjątek, efekty, kolejność efektów i stan. Granica kontraktu jest świadomą decyzją, a nie skutkiem zbyt słabego testu.

### Pułapki

- Test "zielony, bo nic nie sprawdza" - pokrycie linii 100%, a efekty uboczne niewidoczne.
- Osobne listy dla maili i obciążeń - kolejność między współpracownikami ginie.
- Sprawdzanie komunikatu wyjątku bez sprawdzenia, że nie było efektów przed wyjątkiem.
- Logi: jeśli ktoś je parsuje (alerty, audyt), też należą do wektora - wtedy potrzebny kolejny seam.

### Pytanie do sali

Które elementy wektora sprawdzają testy w Waszym najważniejszym module? Czego nie widzą?

## Scena s04. Równoległa weryfikacja (shadow) kalkulatora cen

**Temat ze slajdów:** 4.18 Równoległa weryfikacja (parallel run)
**Pakiet:** `pl.training.workshop.m3.s04_parallelrun` · **Test:** `scripts/warsztat.sh test m3/s04`
**Czas:** ~15 min

### W skrócie

**Co robimy:** `PriceService` woła tylko stary kalkulator w `double`, a gotowy `CandidatePriceCalculator` nigdy nie był porównany z produkcją. Uruchamiamy kandydata w cieniu, zbieramy typowany raport rozbieżności, poprawiamy znaleziony błąd i wprowadzamy jawny tryb `MigrationMode`.

**Zasada:** Równoległa weryfikacja (shadow) liczy wynik obiema implementacjami na prawdziwym ruchu, ale klient dostaje wynik legacy, a awaria kandydata jest izolowana. Nadaje się dla czystych obliczeń bez efektów ubocznych. Zgodność obu implementacji nie dowodzi poprawności - obie mogą mieć ten sam błąd.

**Efekt:** Raport wskazał złą kolejność rabatu porannego i zniżki procentowej, a po poprawce zostaje tylko awaria dla 4DX. W trybie `CANDIDATE` zachowanie dla 4DX świadomie się zmienia (odrzucenie zamiast wyceny 0.00), a `LEGACY` służy za natychmiastowe wycofanie, dopóki stary kod istnieje.

### Co widzimy

`start.PriceService` woła tylko `LegacyPriceCalculator` (double). Obok leży `CandidatePriceCalculator` (Money), napisany przez zespół, ale nigdy nieporównany z produkcją. Jedyne opcje: "włączyć i zobaczyć" albo wieczne testy ręczne.

```java
public Money price(TicketQuery query) {
    return legacy.price(query);
}
```

### Krok 1: dodanie cienia

**W IDE:** w `price` po wyliczeniu wyniku legacy wywołaj kandydata w `try/catch` (Surround With ⌥⌘T) i licz niezgodności.
**Po:**

```java
Money result = legacy.price(query);
try {
    if (!candidate.price(query).equals(result)) {
        mismatches++;
    }
} catch (RuntimeException candidateFailure) {
    mismatches++;
}
return result;
```

**Uruchom:** test zielony; `step1ShadowSurvivesCandidateFailureButOnlyCountsMismatches` - wyjątek kandydata dla formatu 4DX nie przerywa sprzedaży.
**Co powiedzieć:** klient zawsze dostaje wynik legacy. Ale licznik mówi tylko, ŻE coś się różni - nie wiemy co.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m3/s04 0 1`

### Krok 2: raport rozbieżności

**W IDE:** nowa klasa `VerificationReport` z `sealed interface Verification` i rekordami `Agreement`, `Divergence`, `CandidateFailure`. Extract Method `verify` w `PriceService`.
**Po:**

```java
return legacyPrice.equals(candidatePrice)
        ? new VerificationReport.Agreement(query, legacyPrice)
        : new VerificationReport.Divergence(query, legacyPrice, candidatePrice);
```

**Uruchom:** test zielony; `step2ReportShowsWhatDivergedAndWhy` - raport pokazuje dwie rozbieżności (3D dziecko rano: 17.20 vs 19.20, 2D student rano: 13.75 vs 15.00) i awarię dla 4DX.
**Co powiedzieć:** z raportu widać wzorzec: rozbieżności tylko rano i tylko przy zniżce procentowej. Kandydat odejmuje rabat poranny przed procentem.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m3/s04 1 2`

### Krok 3: poprawka znaleziona przez raport

**W IDE:** w `CandidatePriceCalculator` przenieś blok rabatu porannego (⇧⌘↓, Move Statement Down) za wyliczenie zniżki procentowej.
**Po:**

```java
Money price = base.minus(base.percent(discountPercent(query.type())));
if (query.start().getHour() < 12) {
    price = price.minus(MORNING_DISCOUNT);
}
```

**Uruchom:** test zielony; w raporcie zostaje tylko awaria 4DX.
**Co powiedzieć:** odrzucenie nieznanego formatu to świadoma decyzja (legacy wyceniało 4DX na 0.00!), a nie błąd. Zapisujemy ją jako zaakceptowaną różnicę.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m3/s04 2 3`

### Krok 4: przełączenie

**W IDE:** enum `MigrationMode { LEGACY, SHADOW, CANDIDATE }`, konstruktor `PriceService(MigrationMode, VerificationReport)`, `switch` w `price`.
**Po:**

```java
return switch (mode) {
    case LEGACY -> legacy.price(query);
    case SHADOW -> shadow(query);
    case CANDIDATE -> candidate.price(query);
};
```

**Uruchom:** test zielony; równoważność obejmuje wszystkie trzy tryby, `step4CandidateModeIsAuthoritativeAndRejectsUnknownFormat` pokazuje świadomą zmianę dla 4DX.
**Co powiedzieć:** tryb zamiast flagi boolean, wybór w jednym miejscu. `LEGACY` to natychmiastowe wycofanie, dopóki nie usuniemy starego kodu.
**Snapshot:** `step4/` · **Różnica:** `scripts/warsztat.sh diff m3/s04 3 4`

### Rozwiązanie i uzasadnienie

Shadow daje dowód na prawdziwym ruchu, zanim nowa implementacja stanie się autorytatywna. Typowane zdarzenia raportu pozwalają podjąć decyzję bez parsowania logów.

### Pułapki

- Brak izolacji awarii kandydata - wyjątek w cieniu zatrzymuje sprzedaż.
- Zgodność obu implementacji nie dowodzi poprawności: obie mogą mieć ten sam błąd (np. wycena 4DX na 0.00 w legacy).
- Produkcyjny cień kosztuje: latencja, timeout, limit ruchu.

### Pytanie do sali

Ile dni ruchu i ile zgodnych porównań wystarczy, żeby przełączyć tryb na CANDIDATE? Kto o tym decyduje?

## Scena s05. Granice trybu shadow - efekty uboczne

**Temat ze slajdów:** 4.19 Granice trybu shadow
**Pakiet:** `pl.training.workshop.m3.s05_shadowlimits` · **Test:** `scripts/warsztat.sh test m3/s05`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `ShadowBooking` stosuje shadow do rezerwacji z efektami ubocznymi, więc klient dostaje dwa maile i dwa obciążenia karty, a cień niczego nie zgłasza. Wprowadzamy port `Effects`, w cieniu nagrywamy efekty zamiast je wykonywać, a na końcu wydzielamy czysty `BookingPlanner`, który zwraca plan jako dane.

**Zasada:** Podwójne wykonanie w trybie shadow jest bezpieczne tylko dla czystych obliczeń - płatności, maili, zapisów ani zdarzeń nie wolno powielać. Efekty zamieniamy w dane (Separate Query from Modifier): opis "co zrobić" można porównać, a wykonuje się go tylko raz. Zgodność zwracanego wyniku nie dowodzi zgodności efektów.

**Efekt:** Po rezerwacji klient dostaje jeden mail i jedno obciążenie, a cień porównuje zaplanowane efekty z efektami legacy i wyłapuje błędną kwotę w mailu kandydata. Bezpieczeństwo gwarantuje typ, bo `BookingPlanner` nie ma dostępu do portu efektów; kosztem jest rozdzielenie planu od wykonania i dodatkowy typ `BookingPlan`.

### Co widzimy

`start.ShadowBooking` stosuje shadow "jak dla kalkulatora" do rezerwacji z efektami ubocznymi. `NewBookingFlow` sam obciąża kartę, zapisuje wiersz i wysyła mail. Test pokazuje: klient dostaje **dwa maile i dwa obciążenia**, a cień niczego nie zgłasza, bo porównuje tylko zwracany tekst.

```java
String result = legacy.book(request);
try {
    String shadow = candidate.book(request); // też obciąża kartę i wysyła mail
```

### Krok 1: port efektów (Parameterize Constructor)

**W IDE:** Extract Interface na operacjach `Infrastructure` używanych przez nowy przepływ → `Effects`; adapter `RealEffects`. ⌘F6 na konstruktorze `NewBookingFlow`: parametr `Effects` zamiast `Infrastructure`.
**Po:**

```java
this.candidate = new NewBookingFlow(new RealEffects(infra));
```

**Uruchom:** test zielony; `step1PreparatoryRefactoringKeepsTheBugOnPurpose` - nadal dwa maile.
**Co powiedzieć:** refaktoryzacja przygotowawcza zachowuje zachowanie, także błędne. Mamy szew, który w następnym kroku zmieni wszystko.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m3/s05 0 1`

### Krok 2: przechwycenie efektów w cieniu

**W IDE:** nowa klasa `RecordingEffects implements Effects` zapisuje zamiary w formacie dziennika. `ShadowBooking` przekazuje kandydatowi nagrywarkę i porównuje nagrane efekty z efektami, które faktycznie wykonało legacy.
**Po:**

```java
RecordingEffects recorder = new RecordingEffects();
String shadow = new NewBookingFlow(recorder).book(request);
compare("wynik", List.of(result), List.of(shadow));
compare("efekty", legacyEffects, recorder.recorded());
```

**Uruchom:** test zielony; `step2RecordsCandidateEffectsInsteadOfExecutingThem` - jeden mail, jedno obciążenie, a raport pokazuje rozbieżność w treści maila (kandydat pisze 50.00 zamiast 54.00).
**Co powiedzieć:** porównanie efektów znalazło błąd, którego porównanie wyniku nie widziało.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m3/s05 1 2`

### Krok 3: Separate Query from Modifier - czysty plan

**W IDE:** z `NewBookingFlow.book` wydziel (⌥⌘M, potem Move F6) klasę `BookingPlanner` z metodą `plan`, która zwraca `BookingPlan(result, effects)`. `NewBookingFlow` wykonuje plan na porcie; `ShadowBooking` woła tylko `BookingPlanner`. `RecordingEffects` usuń (Safe Delete).
**Po:**

```java
BookingPlan plan = candidate.plan(request);
compare("wynik", List.of(result), List.of(plan.result()));
```

**Uruchom:** test zielony; `step3ComparesAPurePlanAndNeverTouchesInfrastructure`.
**Co powiedzieć:** typ gwarantuje bezpieczeństwo: `BookingPlanner` nie ma dostępu do portu efektów, więc w cieniu nie da się go użyć "za mocno".
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m3/s05 2 3`

### Rozwiązanie i uzasadnienie

Podwójne wykonanie tylko dla czystych obliczeń. Efekty (płatność, mail, zapis, zdarzenia) w cieniu są danymi do porównania, a nie akcjami.

### Pułapki

- Nagrywarka podpięta tylko w części miejsc - jeden zapomniany efekt i klient dostaje drugi SMS.
- Efekty ukryte w obliczeniu: sekwencja numerów rezerwacji, cache, licznik - też są efektami.
- Zgodność odpowiedzi API to nie dowód zgodności danych i transakcji.

### Pytanie do sali

Jakie efekty uboczne ma wasz "czysty" kod, o których zwykle nie myślicie (logi audytowe, metryki, sekwencje)?

## Proponowana kolejność pokazu

**Ścieżka krótka (~50 min):** jak zdobyć pierwszy dowód, gdy testów nie ma.

1. Golden master `CinemaManager` (~3 min) - jeden szeroki test na cały stary system
2. s01 Test charakterystyki (ruch A i krok 1, ~12 min)
3. s02 Break Dependencies, kroki 1, 3 i 4 (~15 min)
4. s03 Wektor obserwowalnego zachowania, kroki 1 i 3 (~10 min)
5. s05 Granice trybu shadow, kroki 1 i 2 (~10 min)

**Ścieżka pełna (~80 min):** golden master → s01 → s02 → s03 → s04 → s05, czyli kolejność sekcji 4 slajdów. Jeśli chcesz wcześniej ustawić język wektora zachowania, pokaż s03 przed s02 (scena sama wprowadza swoje seamy). Po s03 zrób przerwę na pytania. Sceny s03 i s05 dobrze nadają się na samodzielną pracę uczestników, jeśli brakuje czasu na pokaz (patrz plik zadań modułu 3).
