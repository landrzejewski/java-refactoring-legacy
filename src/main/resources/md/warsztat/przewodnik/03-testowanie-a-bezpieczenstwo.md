# Moduł 3. Testowanie a bezpieczeństwo - warsztat CineLegacy: przewodnik prowadzącego

Pięć scen w domenie kina pokazuje na żywo, jak zbudować sieć bezpieczeństwa wokół kodu, który jej nie ma: test charakteryzujący przed pierwszą zmianą, szew (ang. seam) w klasie, której nie da się uruchomić w teście, wektor obserwowalnego zachowania, weryfikację w cieniu i jej granice przy efektach ubocznych. Każda scena ma kod wyjściowy (`start`), kompletne snapshoty po każdym kroku (`stepN`) i test, który po każdym kroku ma być zielony. Tam, gdzie krok świadomie zmienia zachowanie (s03 krok 2, s04 krok 4), test ma osobne oczekiwania i pokazuje różnicę wprost.

Punktem odniesienia dla całego modułu jest wynik wzorcowy (ang. golden master) starego systemu: `CinemaManagerGoldenMasterTest` odtwarza "jeden dzień kina" na nietkniętym `legacy/CinemaManager` i porównuje pełny wektor zachowania (wyniki wywołań, maile, SMS-y, obciążenia i zwroty, raport dzienny, rozliczenia) z zatwierdzonym plikiem `src/test/resources/workshop/cinema-manager.approved.txt`. To odpowiedź na pytanie "jak poradzić sobie bez testów?" w skali całego systemu: jeden szeroki, deterministyczny test, zanim powstanie jakikolwiek test jednostkowy. Sceny poniżej pokazują te same techniki w mniejszej skali. Pokaż golden master na początku modułu (3 min) i wracaj do niego przy s01 (charakterystyka) i s03 (wektor zachowania).

Sceny ilustrują sekcję 3 slajdów ("Siatka bezpieczeństwa") i służą do pokazu; uczestnicy powtarzają je samodzielnie według zakładki "Zadania".

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

- Zasada: **jeden krok - jeden test - jedno zdanie komentarza.** Zmiana w kodzie, uruchomienie testu sceny, zdanie z sekcji "Co powiedzieć". Zanim ruszysz kod, powiedz, jaki dowód da test po kroku.
- **Krok po kroku bez numerów:** `scripts/warsztat.sh next` wstawia do `start` gotowy następny krok i wypisuje, co się zmieniło. `prev` cofa o krok, `status` mówi, który krok jest teraz w `start`. Skrypt pamięta ostatnią scenę, więc po pierwszym `next m3/sNN` wystarczy samo `next`. Ręczne zmiany w `start` zostają nadpisane - o to chodzi, gdy krok na żywo się rozjechał.
- Pracujesz zawsze w pakiecie `start`. Snapshoty `stepN` służą do `diff` albo do przeskoku (`next`, `jump`), gdy coś się rozjedzie.
- Zanim zaczniesz, pokaż slajd 1.2 "Zachowanie obserwowalne to więcej niż wynik metody" i wracaj do niego przy każdej scenie: *co tu obserwujemy - wynik, wyjątek, stan, efekty, kolejność?*
- W s02 start **nie ma testów** i to jest puenta: testy pojawiają się dopiero wtedy, gdy kod ma szew. Pierwsze ruchy to refaktoryzacje automatyczne IDE o niskim ryzyku (slajd 3.6).
- W s01 i s03 warto najpierw **zrobić naiwny ruch na żywo** (np. "poprawić" znalezisko z charakterystyki, sprawdzić tylko wynik), pokazać, czego test nie widzi, cofnąć zmianę i dopiero wtedy wykonać krok z przewodnika.
- W s04 i s05 część testów to nie tylko równoważność (`SNNEquivalenceTest`), ale też dowody strategii (`SNNSolutionTest`): raport rozbieżności, dziennik efektów ubocznych. Testy, które **dokumentują stan startowy** (np. "start obciąża kartę dwa razy"), zmienią kolor na czerwony, gdy wykonasz kroki na pakiecie `start` - to znak, że start jest już naprawiony. Po pokazie: `scripts/warsztat.sh reset`.
- Kroki, które zmieniają zachowanie (s03 krok 2, s04 krok 4), zapowiadaj na głos: "to jest osobny commit, nie refaktoryzacja".

## Mapa scen

| Scena | Temat ze slajdów | Kroki | Pakiet | Czas |
|---|---|---|---|---|
| s01 | 3.1. Test charakteryzujący: zapisz, co kod robi dziś; 3.2. Wynik wzorcowy i dobór przypadków; 3.3. Test równoważności przed zmianą i po niej | 2 | `m3.s01_characterization` | ~15 min |
| s02 | 3.4. Szew: miejsce, w którym podstawiamy zależność; 3.5. Rozrywanie zależności: po zmianie; 3.6. Refaktoryzacje automatyczne w IDE: co dają, a czego nie widzą | 4 | `m3.s02_breakdependencies` | ~20 min |
| s03 | 1.2. Zachowanie obserwowalne to więcej niż wynik metody; 3.2. Wynik wzorcowy i dobór przypadków | 3 | `m3.s03_behaviourvector` | ~12 min |
| s04 | 3.8. Weryfikacja w cieniu | 4 | `m3.s04_parallelrun` | ~15 min |
| s05 | 3.9. Granice weryfikacji w cieniu: efekty uboczne | 3 | `m3.s05_shadowlimits` | ~12 min |

## Scena s01. Test charakterystyki przed pierwszą zmianą

**Temat ze slajdów:** 3.1. Test charakteryzujący: zapisz, co kod robi dziś; 3.2. Wynik wzorcowy i dobór przypadków; 3.3. Test równoważności przed zmianą i po niej
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

**Zmiany:**

1. W klasie `BookingConfirmation` dodajemy prywatne finalne pole `Clock clock` (dochodzi import `java.time.Clock`).
2. Dodajemy konstruktor `BookingConfirmation(Clock clock)`, który przypisuje pole, oraz konstruktor bezargumentowy delegujący do `this(Clock.systemDefaultZone())` - dotychczasowi klienci niczego nie zmieniają (Parameterize Constructor).
3. W `confirm` zamieniamy `LocalDateTime.now()` na `LocalDateTime.now(clock)`.

**Przed:**

```java
public final class BookingConfirmation {
    public String confirm(Booking b) {
        ...
        return "POTWIERDZENIE REZERWACJI\n"
                ...
                + "Wygenerowano: " + LocalDateTime.now().withNano(0) + "\n";
    }
}
```

**Po:**

```java
public final class BookingConfirmation {
    private final Clock clock;

    public BookingConfirmation() {
        this(Clock.systemDefaultZone());
    }

    public BookingConfirmation(Clock clock) {
        this.clock = clock;
    }

    public String confirm(Booking b) {
        ...
                + "Wygenerowano: " + LocalDateTime.now(clock).withNano(0) + "\n";
    }
}
```

**Uruchom:** `scripts/warsztat.sh test m3/s01` - 15 testów zielonych, w tym `fromStep1TheWholeDocumentIsDeterministic` (z `Clock.fixed` porównujemy także linię z czasem, bez scrubbera).
**Co powiedzieć:** to minimalna zmiana, która robi szew dla testu. Stary konstruktor zostaje, więc produkcyjni klienci nie widzą różnicy. Scrubber był rusztowaniem i od tego kroku jest potrzebny tylko dla `start`.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m3/s01 0 1`

### Krok 2: pierwsza refaktoryzacja pod ochroną testu

**Zmiany:**

1. Z ciała pętli w `confirm` wydzielamy nową prywatną metodę `double ticketPrice(Booking b, String t)` - trafia do niej cały blok od `double p;` po rabat poranny; metoda ma dwa wejścia (`b`, `t`) i jedno wyjście (`p`) (Extract Method).
2. W pętli zostaje jedno wywołanie: `sum = sum + ticketPrice(b, t);`.

**Przed:**

```java
double sum = 0;
for (String t : b.ticketTypes()) {
    double p;
    if (b.format() == 3) {
        p = 40.00;
    } else if (b.format() == 2) {
        p = 32.00;
    } else {
        p = 25.00;
    }
    if (t.equals("S")) {
        p = p * 0.75;
    } else if (t.equals("E")) {
        p = p * 0.70;
    } else if (t.equals("C")) {
        p = p * 0.60;
    }
    if (b.start().getHour() < 12) {
        p = p - 5.00;
    }
    sum = sum + p;
}
```

**Po:**

```java
double sum = 0;
for (String t : b.ticketTypes()) {
    sum = sum + ticketPrice(b, t);
}
...
private double ticketPrice(Booking b, String t) {
    double p;
    if (b.format() == 3) {
        p = 40.00;
    } else if (b.format() == 2) {
        p = 32.00;
    } else {
        p = 25.00;
    }
    if (t.equals("S")) {
        p = p * 0.75;
    } else if (t.equals("E")) {
        p = p * 0.70;
    } else if (t.equals("C")) {
        p = p * 0.60;
    }
    if (b.start().getHour() < 12) {
        p = p - 5.00;
    }
    return p;
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

**Temat ze slajdów:** 3.4. Szew: miejsce, w którym podstawiamy zależność; 3.5. Rozrywanie zależności: po zmianie; 3.6. Refaktoryzacje automatyczne w IDE: co dają, a czego nie widzą
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

Sekwencja rozrywania zależności: nazwij zachowanie i punkt obserwacji, wprowadź najmniejszy szew bez zmiany logiki, zabezpiecz go testem z fake/spy, przenieś wybór do jawnego punktu aktywacji, usuń konstrukcję przejściową. Charakterystyki nie da się napisać przed pierwszym ruchem, więc pierwsze ruchy muszą być mechaniczne i wspierane przez IDE (slajd 3.6).

### Krok 1: Extract Interface + Parameterize Constructor

**Zmiany:**

1. Z klasy `LegacyDatabase` wydzielamy nowy interfejs `BookingStore` z dwiema metodami: `paidBookings()` i `markReminded(String bookingId)`; `LegacyDatabase` zaczyna go implementować, a obie jej metody dostają `@Override` (Extract Interface).
2. W `ShowtimeReminderJob` dodajemy pole `Supplier<BookingStore> stores` i konstruktor z tym parametrem (dochodzą importy `java.util.Objects` i `java.util.function.Supplier`).
3. Dodajemy konstruktor bezargumentowy delegujący do `this(LegacyDatabase::new)` - produkcja składa to samo co wcześniej (Parameterize Constructor).
4. W `run()` zamieniamy `new LegacyDatabase()` na `stores.get()`; zmienna lokalna `database` ma teraz typ `BookingStore`. Reszta metody `run()` bez zmian.

**Po (`BookingStore.java`, nowy plik):**

```java
import java.util.List;

import pl.training.workshop.m3.s02_breakdependencies.PaidBooking;

/** Najwęższy kontrakt, którego potrzebuje ShowtimeReminderJob (wydzielony z LegacyDatabase). */
public interface BookingStore {
    List<PaidBooking> paidBookings();

    void markReminded(String bookingId);
}
```

**Po (`LegacyDatabase.java`):**

```java
public final class LegacyDatabase implements BookingStore {
    public LegacyDatabase() {
        throw new IllegalStateException("brak polaczenia z jdbc:oracle:thin:@prod-db:1521/CINEMA");
    }

    @Override
    public List<PaidBooking> paidBookings() {
        return List.of();
    }

    @Override
    public void markReminded(String bookingId) {
        // UPDATE bookings SET reminded = 1 WHERE id = ?
    }
}
```

**Przed (`ShowtimeReminderJob.java`):**

```java
public final class ShowtimeReminderJob {
    public int run() {
        LegacyDatabase database = new LegacyDatabase();
        LocalDateTime now = LocalDateTime.now();
        int sent = 0;
        ...
    }
}
```

**Po (`ShowtimeReminderJob.java`):**

```java
public final class ShowtimeReminderJob {
    private final Supplier<BookingStore> stores;

    public ShowtimeReminderJob() {
        this(LegacyDatabase::new);
    }

    public ShowtimeReminderJob(Supplier<BookingStore> stores) {
        this.stores = Objects.requireNonNull(stores, "stores");
    }

    public int run() {
        BookingStore database = stores.get();
        LocalDateTime now = LocalDateTime.now();
        int sent = 0;
        ...
    }
}
```

**Uruchom:** `scripts/warsztat.sh test m3/s02` - testy `step1...` zielone: z fałszywą bazą zadanie działa, gdy nic nie jest do wysłania.
**Co powiedzieć:** przekazujemy fabrykę, a nie gotową bazę, bo stary kod otwierał połączenie przy każdym `run()`. Gdybyśmy zrobili `this(new LegacyDatabase())`, wyjątek poleciałby już w konstruktorze - zmienilibyśmy czas życia zależności i moment błędu.
**Snapshot:** `step1/`  ·  **Różnica:** `scripts/warsztat.sh diff m3/s02 0 1`

### Krok 2: Clock jako zależność

**Zmiany:**

1. W `ShowtimeReminderJob` dodajemy pole `Clock clock` i drugi parametr konstruktora (import `java.time.Clock`) - celowo zależność konstruktora, a nie dodatkowy parametr metody `run()`.
2. Konstruktor bezargumentowy przekazuje `Clock.systemDefaultZone()`.
3. W `run()` zamieniamy `LocalDateTime.now()` na `LocalDateTime.now(clock)` - zegar czytamy w tym samym miejscu co wcześniej. Reszta metody `run()` bez zmian.

**Przed:**

```java
private final Supplier<BookingStore> stores;

public ShowtimeReminderJob() {
    this(LegacyDatabase::new);
}

public ShowtimeReminderJob(Supplier<BookingStore> stores) {
    this.stores = Objects.requireNonNull(stores, "stores");
}

public int run() {
    BookingStore database = stores.get();
    LocalDateTime now = LocalDateTime.now();
```

**Po:**

```java
private final Supplier<BookingStore> stores;
private final Clock clock;

public ShowtimeReminderJob() {
    this(LegacyDatabase::new, Clock.systemDefaultZone());
}

public ShowtimeReminderJob(Supplier<BookingStore> stores, Clock clock) {
    this.stores = Objects.requireNonNull(stores, "stores");
    this.clock = Objects.requireNonNull(clock, "clock");
}

public int run() {
    BookingStore database = stores.get();
    LocalDateTime now = LocalDateTime.now(clock);
```

**Uruchom:** test zielony. Test `step2...` pokazuje dwie rzeczy: przypadki bez wysyłki działają przy stałym zegarze, a przypadek "do wysłania" wciąż kończy się wyjątkiem SMTP.
**Co powiedzieć:** czas był ukrytym wejściem. Zegar czytamy w tym samym miejscu co wcześniej (po utworzeniu bazy), więc kolejność się nie zmienia. Następna przeszkoda jest widoczna w teście - statyczny mailer.
**Snapshot:** `step2/`  ·  **Różnica:** `scripts/warsztat.sh diff m3/s02 1 2`

### Krok 3: Subclass and Override Method i pierwszy test

**Zmiany:**

1. Wywołanie `ReminderMailer.send(...)` z `run()` wydzielamy do nowej metody `protected void sendReminder(String to, String subject, String body)` (Extract Method).
2. Usuwamy `final` z klasy, żeby dało się ją rozszerzyć.
3. W teście powstaje podklasa anonimowa, która nadpisuje `sendReminder` i zapisuje wiadomości do listy - to jest **pierwszy prawdziwy test** tej klasy (Subclass and Override Method).

**Przed:**

```java
public final class ShowtimeReminderJob {
    ...
    if (!booking.reminded() && minutes > 0 && minutes <= 120) {
        ReminderMailer.send(booking.email(),
                "Przypomnienie: " + booking.title(),
                "Seans zaczyna sie o " + booking.start().toLocalTime());
        database.markReminded(booking.id());
```

**Po:**

```java
public class ShowtimeReminderJob {
    ...
    if (!booking.reminded() && minutes > 0 && minutes <= 120) {
        sendReminder(booking.email(),
                "Przypomnienie: " + booking.title(),
                "Seans zaczyna sie o " + booking.start().toLocalTime());
        database.markReminded(booking.id());
    ...
    protected void sendReminder(String to, String subject, String body) {
        ReminderMailer.send(to, subject, body);
    }
}
```

**Uruchom:** test zielony: 3 przypadki z granicą 120/121 minut, już przypomniane i już rozpoczęte.
**Co powiedzieć:** to najtańszy seam, gdy nie możemy jeszcze zmienić konstruktora - ale ma cenę: klasa przestała być `final`, a test zależy od szczegółu implementacji (nazwy metody chronionej).
**Snapshot:** `step3/`  ·  **Różnica:** `scripts/warsztat.sh diff m3/s02 2 3`

### Krok 4: seam z dziedziczenia na interfejs funkcyjny

**Zmiany:**

1. Powstaje nowy plik `ReminderSender.java`: interfejs z adnotacją `@FunctionalInterface` i jedną metodą `void send(String to, String subject, String body)`.
2. W `ShowtimeReminderJob` dodajemy pole `ReminderSender sender` i trzeci parametr konstruktora; konstruktor bezargumentowy przekazuje `ReminderMailer::send`.
3. Ciało `sendReminder` zmieniamy na `sender.send(...)`, wstawiamy je w miejsce wywołania w `run()` i usuwamy metodę `sendReminder` (Inline Method).
4. Przywracamy `final` na klasie. W teście podklasę zastępuje lambda.

**Po (`ReminderSender.java`, nowy plik):**

```java
/** Najwęższy seam dla powiadomień: interfejs funkcyjny, w teście wystarczy lambda. */
@FunctionalInterface
public interface ReminderSender {
    void send(String to, String subject, String body);
}
```

**Przed (`ShowtimeReminderJob.java`):**

```java
public class ShowtimeReminderJob {
    private final Supplier<BookingStore> stores;
    private final Clock clock;

    public ShowtimeReminderJob() {
        this(LegacyDatabase::new, Clock.systemDefaultZone());
    }

    public ShowtimeReminderJob(Supplier<BookingStore> stores, Clock clock) {
        this.stores = Objects.requireNonNull(stores, "stores");
        this.clock = Objects.requireNonNull(clock, "clock");
    }
    ...
    sendReminder(booking.email(),
            "Przypomnienie: " + booking.title(),
            "Seans zaczyna sie o " + booking.start().toLocalTime());
    ...
    protected void sendReminder(String to, String subject, String body) {
        ReminderMailer.send(to, subject, body);
    }
}
```

**Po (`ShowtimeReminderJob.java`):**

```java
public final class ShowtimeReminderJob {
    private final Supplier<BookingStore> stores;
    private final Clock clock;
    private final ReminderSender sender;

    public ShowtimeReminderJob() {
        this(LegacyDatabase::new, Clock.systemDefaultZone(), ReminderMailer::send);
    }

    public ShowtimeReminderJob(Supplier<BookingStore> stores, Clock clock, ReminderSender sender) {
        this.stores = Objects.requireNonNull(stores, "stores");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.sender = Objects.requireNonNull(sender, "sender");
    }
    ...
    sender.send(booking.email(),
            "Przypomnienie: " + booking.title(),
            "Seans zaczyna sie o " + booking.start().toLocalTime());
    ...
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

**Temat ze slajdów:** 1.2. Zachowanie obserwowalne to więcej niż wynik metody; 3.2. Wynik wzorcowy i dobór przypadków
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

Pokaż tabelę ze slajdu 1.2: wynik, wyjątki, stan, współpracownicy, czas, granice. Które z nich ten test widzi?

### Krok 1: seam dla maili - regresja staje się widoczna

**Zmiany:**

1. Powstaje nowy plik `Mailer.java`: interfejs z adnotacją `@FunctionalInterface` i metodą `void send(String to, String text)`.
2. W `TicketCheckout` dodajemy pole `Mailer mailer`, konstruktor z tym parametrem i konstruktor bezargumentowy delegujący do `this(CinemaMailer::send)` (Parameterize Constructor).
3. Oba statyczne wywołania `CinemaMailer.send(...)` w `pay` zamieniamy na `mailer.send(...)`. Logika bez zmian.

**Po (`Mailer.java`, nowy plik):**

```java
/** Seam dla efektu ubocznego "wysłany mail" - w teście lambda zapisująca do dziennika. */
@FunctionalInterface
public interface Mailer {
    void send(String to, String text);
}
```

**Przed (`TicketCheckout.java`):**

```java
public final class TicketCheckout {
    public String pay(Booking booking, String card) {
        ...
        boolean charged = CardTerminal.charge(card, booking.amount());
        if (charged) {
            booking.markPaid();
        } else {
            CinemaMailer.send(booking.email(), "Platnosc odrzucona " + booking.id());
        }
        CinemaMailer.send(booking.email(), "Bilety " + booking.id() + " oplacone: " + booking.amount());
        return charged ? "OK" : "DECLINED";
    }
}
```

**Po (`TicketCheckout.java`):**

```java
public final class TicketCheckout {
    private final Mailer mailer;

    public TicketCheckout() {
        this(CinemaMailer::send);
    }

    public TicketCheckout(Mailer mailer) {
        this.mailer = Objects.requireNonNull(mailer, "mailer");
    }

    public String pay(Booking booking, String card) {
        ...
        boolean charged = CardTerminal.charge(card, booking.amount());
        if (charged) {
            booking.markPaid();
        } else {
            mailer.send(booking.email(), "Platnosc odrzucona " + booking.id());
        }
        mailer.send(booking.email(), "Bilety " + booking.id() + " oplacone: " + booking.amount());
        return charged ? "OK" : "DECLINED";
    }
}
```

**Uruchom:** `scripts/warsztat.sh test m3/s03` - zielony. `step1SeesTheRegressionThatResultOnlyTestMissed` dokumentuje, że po odrzuceniu karty idą dwa maile: "Platnosc odrzucona" i "Bilety B1 oplacone".
**Co powiedzieć:** kod się nie zmienił, zmieniło się to, co widzimy. Test z samym wynikiem nie mógł tego wykryć.
**Snapshot:** `step1/`  ·  **Różnica:** `scripts/warsztat.sh diff m3/s03 0 1`

### Krok 2: naprawa regresji pod ochroną testu

**Zmiany:**

1. Wysyłkę maila potwierdzającego przenosimy do gałęzi sukcesu, za `booking.markPaid()`.
2. Zmienna `charged` znika: wywołanie `CardTerminal.charge(...)` ląduje wprost w warunku `if`, a `return charged ? "OK" : "DECLINED"` rozbijamy na `return "OK"` w gałęzi sukcesu i `return "DECLINED"` po wysyłce maila o odrzuceniu.

**Przed:**

```java
boolean charged = CardTerminal.charge(card, booking.amount());
if (charged) {
    booking.markPaid();
} else {
    mailer.send(booking.email(), "Platnosc odrzucona " + booking.id());
}
mailer.send(booking.email(), "Bilety " + booking.id() + " oplacone: " + booking.amount());
return charged ? "OK" : "DECLINED";
```

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

**Zmiany:**

1. Powstaje nowy plik `PaymentGateway.java`: interfejs z adnotacją `@FunctionalInterface` i metodą `boolean charge(String card, Money amount)`.
2. W `TicketCheckout` dodajemy pole `PaymentGateway gateway` i drugi parametr konstruktora; konstruktor bezargumentowy przekazuje `CardTerminal::charge`.
3. W `pay` zamieniamy `CardTerminal.charge(...)` na `gateway.charge(...)`.
4. W teście oba fałszywe obiekty (mailer i bramka płatności) zapisują do **jednego** dziennika zdarzeń.

**Po (`PaymentGateway.java`, nowy plik):**

```java
import pl.training.workshop.shared.Money;

/** Seam dla efektu ubocznego "obciążenie karty". */
@FunctionalInterface
public interface PaymentGateway {
    boolean charge(String card, Money amount);
}
```

**Przed (`TicketCheckout.java`):**

```java
private final Mailer mailer;

public TicketCheckout() {
    this(CinemaMailer::send);
}

public TicketCheckout(Mailer mailer) {
    this.mailer = Objects.requireNonNull(mailer, "mailer");
}
...
if (CardTerminal.charge(card, booking.amount())) {
    booking.markPaid();
```

**Po (`TicketCheckout.java`):**

```java
private final Mailer mailer;
private final PaymentGateway gateway;

public TicketCheckout() {
    this(CinemaMailer::send, CardTerminal::charge);
}

public TicketCheckout(Mailer mailer, PaymentGateway gateway) {
    this.mailer = Objects.requireNonNull(mailer, "mailer");
    this.gateway = Objects.requireNonNull(gateway, "gateway");
}
...
if (gateway.charge(card, booking.amount())) {
    booking.markPaid();
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

**Temat ze slajdów:** 3.8. Weryfikacja w cieniu
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

**Zmiany:**

1. W `PriceService` dodajemy pole `CandidatePriceCalculator candidate` oraz licznik `int mismatches` z metodą odczytu `mismatches()`.
2. W `price` po wyliczeniu wyniku legacy wywołujemy kandydata w bloku `try/catch (RuntimeException)`: inny wynik albo wyjątek zwiększa licznik, a awaria kandydata nie przerywa sprzedaży.
3. Klient nadal dostaje wynik legacy (`return result`).

**Przed:**

```java
public final class PriceService {
    private final LegacyPriceCalculator legacy = new LegacyPriceCalculator();

    public Money price(TicketQuery query) {
        return legacy.price(query);
    }
}
```

**Po:**

```java
public final class PriceService {
    private final LegacyPriceCalculator legacy = new LegacyPriceCalculator();
    private final CandidatePriceCalculator candidate = new CandidatePriceCalculator();
    private int mismatches;

    public Money price(TicketQuery query) {
        Money result = legacy.price(query);
        try {
            if (!candidate.price(query).equals(result)) {
                mismatches++;
            }
        } catch (RuntimeException candidateFailure) {
            mismatches++;
        }
        return result;
    }

    /** Wiemy, ŻE coś się różni, ale nie wiemy CO - licznik to za mało do decyzji. */
    public int mismatches() {
        return mismatches;
    }
}
```

**Uruchom:** test zielony; `step1ShadowSurvivesCandidateFailureButOnlyCountsMismatches` - wyjątek kandydata dla formatu 4DX nie przerywa sprzedaży.
**Co powiedzieć:** klient zawsze dostaje wynik legacy. Ale licznik mówi tylko, ŻE coś się różni - nie wiemy co.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m3/s04 0 1`

### Krok 2: raport rozbieżności

**Zmiany:**

1. Powstaje nowy plik `VerificationReport.java`: `sealed interface Verification` z rekordami `Agreement`, `Divergence` i `CandidateFailure`, lista zdarzeń z metodami `record` i `entries` oraz metoda `problems()` zwracająca czytelne wiersze.
2. W `PriceService` licznik `mismatches` i jego metoda odczytu znikają; w ich miejsce pole `VerificationReport report` - konstruktor przyjmuje raport, a bezargumentowy tworzy `new VerificationReport()`.
3. Porównanie wydzielamy z `price` do prywatnej metody `verify(TicketQuery, Money)`, która zwraca typowane zdarzenie; `price` tylko je zapisuje przez `report.record(verify(query, result))` (Extract Method).

**Po (`VerificationReport.java`, nowy plik):**

```java
import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.m3.s04_parallelrun.TicketQuery;
import pl.training.workshop.shared.Money;

public final class VerificationReport {
    public sealed interface Verification {
    }

    public record Agreement(TicketQuery query, Money price) implements Verification {
    }

    public record Divergence(TicketQuery query, Money legacy, Money candidate) implements Verification {
    }

    public record CandidateFailure(TicketQuery query, Money legacy, String error) implements Verification {
    }

    private final List<Verification> entries = new ArrayList<>();

    public void record(Verification verification) {
        entries.add(verification);
    }

    public List<Verification> entries() {
        return List.copyOf(entries);
    }

    /** Czytelne wiersze raportu - tylko to, co wymaga uwagi zespołu. */
    public List<String> problems() {
        List<String> lines = new ArrayList<>();
        for (Verification entry : entries) {
            switch (entry) {
                case Agreement agreement -> { }
                case Divergence d -> lines.add("ROZBIEZNOSC " + d.query()
                        + ": legacy " + d.legacy() + ", kandydat " + d.candidate());
                case CandidateFailure f -> lines.add("BLAD KANDYDATA " + f.query() + ": " + f.error());
            }
        }
        return lines;
    }
}
```

**Przed (`PriceService.java`):**

```java
private final LegacyPriceCalculator legacy = new LegacyPriceCalculator();
private final CandidatePriceCalculator candidate = new CandidatePriceCalculator();
private int mismatches;

public Money price(TicketQuery query) {
    Money result = legacy.price(query);
    try {
        if (!candidate.price(query).equals(result)) {
            mismatches++;
        }
    } catch (RuntimeException candidateFailure) {
        mismatches++;
    }
    return result;
}

public int mismatches() {
    return mismatches;
}
```

**Po (`PriceService.java`):**

```java
private final LegacyPriceCalculator legacy = new LegacyPriceCalculator();
private final CandidatePriceCalculator candidate = new CandidatePriceCalculator();
private final VerificationReport report;

public PriceService() {
    this(new VerificationReport());
}

public PriceService(VerificationReport report) {
    this.report = report;
}

public Money price(TicketQuery query) {
    Money result = legacy.price(query);
    report.record(verify(query, result));
    return result;
}

private VerificationReport.Verification verify(TicketQuery query, Money legacyPrice) {
    try {
        Money candidatePrice = candidate.price(query);
        return legacyPrice.equals(candidatePrice)
                ? new VerificationReport.Agreement(query, legacyPrice)
                : new VerificationReport.Divergence(query, legacyPrice, candidatePrice);
    } catch (RuntimeException failure) {
        return new VerificationReport.CandidateFailure(query, legacyPrice,
                failure.getClass().getSimpleName() + ": " + failure.getMessage());
    }
}
```

**Uruchom:** test zielony; `step2ReportShowsWhatDivergedAndWhy` - raport pokazuje dwie rozbieżności (3D dziecko rano: 17.20 vs 19.20, 2D student rano: 13.75 vs 15.00) i awarię dla 4DX.
**Co powiedzieć:** z raportu widać wzorzec: rozbieżności tylko rano i tylko przy zniżce procentowej. Kandydat odejmuje rabat poranny przed procentem.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m3/s04 1 2`

### Krok 3: poprawka znaleziona przez raport

**Zmiany:**

1. W `CandidatePriceCalculator.price` przenosimy blok rabatu porannego (`if (query.start().getHour() < 12) ...`) za wyliczenie zniżki procentowej: rabat odejmujemy od `price`, a nie od `base` - tak jak liczy legacy.
2. `PriceService` i raport bez zmian - poprawność ruchu potwierdza znikająca rozbieżność w raporcie.

**Przed:**

```java
public Money price(TicketQuery query) {
    Money base = basePrice(query.format());
    if (query.start().getHour() < 12) {
        base = base.minus(MORNING_DISCOUNT);
    }
    Money price = base.minus(base.percent(discountPercent(query.type())));
    if (query.row() >= 10) {
        price = price.plus(VIP_SURCHARGE);
    }
    if (query.format().equals("3D")) {
        price = price.plus(GLASSES_3D);
    }
    return price;
}
```

**Po:**

```java
public Money price(TicketQuery query) {
    Money base = basePrice(query.format());
    Money price = base.minus(base.percent(discountPercent(query.type())));
    if (query.start().getHour() < 12) {
        price = price.minus(MORNING_DISCOUNT);
    }
    if (query.row() >= 10) {
        price = price.plus(VIP_SURCHARGE);
    }
    if (query.format().equals("3D")) {
        price = price.plus(GLASSES_3D);
    }
    return price;
}
```

**Uruchom:** test zielony; w raporcie zostaje tylko awaria 4DX.
**Co powiedzieć:** odrzucenie nieznanego formatu to świadoma decyzja (legacy wyceniało 4DX na 0.00!), a nie błąd. Zapisujemy ją jako zaakceptowaną różnicę.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m3/s04 2 3`

### Krok 4: przełączenie

**Zmiany:**

1. Powstaje nowy plik `MigrationMode.java`: enum z wartościami `LEGACY`, `SHADOW` i `CANDIDATE`.
2. W `PriceService` dodajemy pole `MigrationMode mode`; konstruktor przyjmuje teraz `(MigrationMode, VerificationReport)`, a bezargumentowy przekazuje `MigrationMode.SHADOW`.
3. Dotychczasową ścieżkę porównania przenosimy do prywatnej metody `shadow(TicketQuery)`; metoda `verify` znika, a zapis do raportu dzieje się wprost w `shadow`.
4. `price` wybiera ścieżkę przez `switch` po trybie: `LEGACY` woła stary kalkulator, `SHADOW` metodę `shadow`, `CANDIDATE` kandydata.

**Po (`MigrationMode.java`, nowy plik):**

```java
/** Jawny tryb migracji zamiast flagi boolean. */
public enum MigrationMode {
    LEGACY,
    SHADOW,
    CANDIDATE
}
```

**Przed (`PriceService.java`):**

```java
public PriceService() {
    this(new VerificationReport());
}
...
public Money price(TicketQuery query) {
    Money result = legacy.price(query);
    report.record(verify(query, result));
    return result;
}

private VerificationReport.Verification verify(TicketQuery query, Money legacyPrice) { ... }
```

**Po (`PriceService.java`):**

```java
public final class PriceService {
    private final LegacyPriceCalculator legacy = new LegacyPriceCalculator();
    private final CandidatePriceCalculator candidate = new CandidatePriceCalculator();
    private final MigrationMode mode;
    private final VerificationReport report;

    public PriceService() {
        this(MigrationMode.SHADOW, new VerificationReport());
    }

    public PriceService(MigrationMode mode, VerificationReport report) {
        this.mode = mode;
        this.report = report;
    }

    public Money price(TicketQuery query) {
        return switch (mode) {
            case LEGACY -> legacy.price(query);
            case SHADOW -> shadow(query);
            case CANDIDATE -> candidate.price(query);
        };
    }

    private Money shadow(TicketQuery query) {
        Money result = legacy.price(query);
        try {
            Money candidatePrice = candidate.price(query);
            report.record(result.equals(candidatePrice)
                    ? new VerificationReport.Agreement(query, result)
                    : new VerificationReport.Divergence(query, result, candidatePrice));
        } catch (RuntimeException failure) {
            report.record(new VerificationReport.CandidateFailure(query, result,
                    failure.getClass().getSimpleName() + ": " + failure.getMessage()));
        }
        return result;
    }
}
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

**Temat ze slajdów:** 3.9. Granice weryfikacji w cieniu: efekty uboczne
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

**Zmiany:**

1. Powstaje nowy plik `Effects.java`: interfejs z trzema operacjami `Infrastructure`, których używa nowy przepływ - `sendMail`, `charge` i `save` (Extract Interface).
2. Powstaje nowy plik `RealEffects.java`: adapter implementujący `Effects` i delegujący każdą operację do `Infrastructure`.
3. W konstruktorze `NewBookingFlow` zmieniamy typ parametru z `Infrastructure` na `Effects` (pole `infra` staje się polem `effects`); w `book` wszystkie trzy wywołania `infra.*` stają się `effects.*`. Import `...s05_shadowlimits.Infrastructure` znika z tego pliku.
4. W `ShadowBooking` kandydat powstaje teraz przez `new NewBookingFlow(new RealEffects(infra))` - zachowanie (także błędne) bez zmian.

**Po (`Effects.java`, nowy plik):**

```java
import pl.training.workshop.shared.Money;

/** Port efektów ubocznych - nowa ścieżka nie sięga już bezpośrednio do infrastruktury. */
public interface Effects {
    void sendMail(String to, String text);

    void charge(String card, Money amount);

    void save(String row);
}
```

**Po (`RealEffects.java`, nowy plik):**

```java
import pl.training.workshop.m3.s05_shadowlimits.Infrastructure;
import pl.training.workshop.shared.Money;

/** Adapter portu na prawdziwą infrastrukturę (poczta, płatności, baza). */
public final class RealEffects implements Effects {
    private final Infrastructure infra;

    public RealEffects(Infrastructure infra) {
        this.infra = infra;
    }

    @Override
    public void sendMail(String to, String text) {
        infra.sendMail(to, text);
    }

    @Override
    public void charge(String card, Money amount) {
        infra.charge(card, amount);
    }

    @Override
    public void save(String row) {
        infra.save(row);
    }
}
```

**Przed (`NewBookingFlow.java`):**

```java
private final Infrastructure infra;

public NewBookingFlow(Infrastructure infra) {
    this.infra = infra;
}

public String book(BookingRequest request) {
    Money tickets = TICKET_2D.times(request.tickets());
    Money total = tickets.plus(ONLINE_FEE.times(request.tickets()));
    infra.charge(request.card(), total);
    infra.save(request.title() + ";" + request.email() + ";" + request.tickets() + ";" + total);
    infra.sendMail(request.email(),
            "Bilety " + request.title() + " x" + request.tickets() + ", zaplacono " + tickets);
    return "OK " + total;
}
```

**Po (`NewBookingFlow.java`):**

```java
private final Effects effects;

public NewBookingFlow(Effects effects) {
    this.effects = effects;
}

public String book(BookingRequest request) {
    Money tickets = TICKET_2D.times(request.tickets());
    Money total = tickets.plus(ONLINE_FEE.times(request.tickets()));
    effects.charge(request.card(), total);
    effects.save(request.title() + ";" + request.email() + ";" + request.tickets() + ";" + total);
    effects.sendMail(request.email(),
            "Bilety " + request.title() + " x" + request.tickets() + ", zaplacono " + tickets);
    return "OK " + total;
}
```

**Przed (`ShadowBooking.java`):**

```java
public ShadowBooking(Infrastructure infra) {
    this.legacy = new LegacyBookingFlow(infra);
    this.candidate = new NewBookingFlow(infra);
}
```

**Po (`ShadowBooking.java`):**

```java
public ShadowBooking(Infrastructure infra) {
    this.legacy = new LegacyBookingFlow(infra);
    this.candidate = new NewBookingFlow(new RealEffects(infra));
}
```

**Uruchom:** test zielony; `step1PreparatoryRefactoringKeepsTheBugOnPurpose` - nadal dwa maile.
**Co powiedzieć:** refaktoryzacja przygotowawcza zachowuje zachowanie, także błędne. Mamy szew, który w następnym kroku zmieni wszystko.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m3/s05 0 1`

### Krok 2: przechwycenie efektów w cieniu

**Zmiany:**

1. Powstaje nowy plik `RecordingEffects.java`: implementacja `Effects`, która zapisuje zamiary w formacie dziennika infrastruktury (`MAIL ...`, `CHARGE ...`, `SAVE ...`), niczego nie wykonuje, a nagrania udostępnia przez `recorded()`.
2. W `ShadowBooking` pole `candidate` znika; dochodzi pole `Infrastructure infra`. Kandydata tworzymy w `book` z nagrywarką: `new NewBookingFlow(recorder)`.
3. W `book` przed wywołaniem legacy zapamiętujemy rozmiar dziennika, a po nim wycinamy z dziennika efekty wykonane przez legacy (`legacyEffects`).
4. Porównanie przenosimy do pomocniczej metody `compare(String, List, List)` i wołamy ją dwa razy: dla wyniku i dla efektów; różnice trafiają do `divergences`. Metoda `divergences()` bez zmian.

**Po (`RecordingEffects.java`, nowy plik):**

```java
import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.shared.Money;

/** Nagrywa zamiary kandydata w formacie dziennika infrastruktury; niczego nie wykonuje. */
public final class RecordingEffects implements Effects {
    private final List<String> recorded = new ArrayList<>();

    @Override
    public void sendMail(String to, String text) {
        recorded.add("MAIL " + to + ": " + text);
    }

    @Override
    public void charge(String card, Money amount) {
        recorded.add("CHARGE " + card + ": " + amount);
    }

    @Override
    public void save(String row) {
        recorded.add("SAVE " + row);
    }

    public List<String> recorded() {
        return List.copyOf(recorded);
    }
}
```

**Przed (`ShadowBooking.java`):**

```java
private final LegacyBookingFlow legacy;
private final NewBookingFlow candidate;
private final List<String> divergences = new ArrayList<>();

public ShadowBooking(Infrastructure infra) {
    this.legacy = new LegacyBookingFlow(infra);
    this.candidate = new NewBookingFlow(new RealEffects(infra));
}

public String book(BookingRequest request) {
    String result = legacy.book(request);
    try {
        String shadow = candidate.book(request);
        if (!shadow.equals(result)) {
            divergences.add("legacy: " + result + " | kandydat: " + shadow);
        }
    } catch (RuntimeException failure) {
        divergences.add("kandydat: " + failure.getMessage());
    }
    return result;
}
```

**Po (`ShadowBooking.java`):**

```java
private final Infrastructure infra;
private final LegacyBookingFlow legacy;
private final List<String> divergences = new ArrayList<>();

public ShadowBooking(Infrastructure infra) {
    this.infra = infra;
    this.legacy = new LegacyBookingFlow(infra);
}

public String book(BookingRequest request) {
    int before = infra.log().size();
    String result = legacy.book(request);
    List<String> legacyEffects = infra.log().subList(before, infra.log().size());
    try {
        RecordingEffects recorder = new RecordingEffects();
        String shadow = new NewBookingFlow(recorder).book(request);
        compare("wynik", List.of(result), List.of(shadow));
        compare("efekty", legacyEffects, recorder.recorded());
    } catch (RuntimeException failure) {
        divergences.add("kandydat: " + failure.getMessage());
    }
    return result;
}

private void compare(String what, List<String> legacyValues, List<String> candidateValues) {
    for (int i = 0; i < Math.max(legacyValues.size(), candidateValues.size()); i++) {
        String legacyValue = i < legacyValues.size() ? legacyValues.get(i) : "-";
        String candidateValue = i < candidateValues.size() ? candidateValues.get(i) : "-";
        if (!legacyValue.equals(candidateValue)) {
            divergences.add(what + " legacy: " + legacyValue + " | kandydat: " + candidateValue);
        }
    }
}
```

**Uruchom:** test zielony; `step2RecordsCandidateEffectsInsteadOfExecutingThem` - jeden mail, jedno obciążenie, a raport pokazuje rozbieżność w treści maila (kandydat pisze 50.00 zamiast 54.00).
**Co powiedzieć:** porównanie efektów znalazło błąd, którego porównanie wyniku nie widziało.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m3/s05 1 2`

### Krok 3: Separate Query from Modifier - czysty plan

**Zmiany:**

1. Powstaje nowy plik `BookingPlan.java`: rekord `BookingPlan(String result, List<Effect> effects)` z zapieczętowanym interfejsem `Effect` i rekordami `Charge`, `Save`, `SendMail`; każdy efekt umie się opisać (`describe`) i wykonać na porcie (`applyTo`).
2. Powstaje nowy plik `BookingPlanner.java`: czysta część wyliczeń wydzielona z `NewBookingFlow.book` - metoda `plan(BookingRequest)` liczy kwoty i zwraca plan jako dane; klasa nie ma dostępu do portu efektów (Separate Query from Modifier). Stałe `TICKET_2D` i `ONLINE_FEE` przenoszą się z `NewBookingFlow` do `BookingPlanner`.
3. `NewBookingFlow.book` chudnie: pobiera plan z `BookingPlanner` (nowe pole `planner`), wykonuje `plan.effects()` na porcie `Effects` i zwraca `plan.result()`.
4. W `ShadowBooking` kandydatem jest teraz pole `BookingPlanner candidate`: cień porównuje wynik planu oraz opisy zaplanowanych efektów z efektami legacy. Konstruktor, metody `compare` i `divergences()` bez zmian.
5. Plik `RecordingEffects.java` znika - nagrywarka nie jest już potrzebna.

**Po (`BookingPlan.java`, nowy plik):**

```java
import java.util.List;

import pl.training.workshop.shared.Money;

/**
 * Wynik czystego obliczenia - odpowiedź dla klienta i lista efektów DO wykonania.
 * Efekty to dane: można je porównać, zalogować albo wykonać, ale plan sam niczego nie robi.
 */
public record BookingPlan(String result, List<Effect> effects) {
    public sealed interface Effect {
        String describe();

        void applyTo(Effects port);
    }

    public record Charge(String card, Money amount) implements Effect {
        public String describe() {
            return "CHARGE " + card + ": " + amount;
        }

        public void applyTo(Effects port) {
            port.charge(card, amount);
        }
    }

    public record Save(String row) implements Effect {
        public String describe() {
            return "SAVE " + row;
        }

        public void applyTo(Effects port) {
            port.save(row);
        }
    }

    public record SendMail(String to, String text) implements Effect {
        public String describe() {
            return "MAIL " + to + ": " + text;
        }

        public void applyTo(Effects port) {
            port.sendMail(to, text);
        }
    }
}
```

**Po (`BookingPlanner.java`, nowy plik):**

```java
import java.util.List;

import pl.training.workshop.m3.s05_shadowlimits.BookingRequest;
import pl.training.workshop.shared.Money;

/** Czysta część nowej ścieżki - nie ma dostępu do żadnego portu efektów. */
public final class BookingPlanner {
    private static final Money TICKET_2D = Money.of("25.00");
    private static final Money ONLINE_FEE = Money.of("2.00");

    public BookingPlan plan(BookingRequest request) {
        Money tickets = TICKET_2D.times(request.tickets());
        Money total = tickets.plus(ONLINE_FEE.times(request.tickets()));
        return new BookingPlan("OK " + total, List.of(
                new BookingPlan.Charge(request.card(), total),
                new BookingPlan.Save(request.title() + ";" + request.email() + ";"
                        + request.tickets() + ";" + total),
                new BookingPlan.SendMail(request.email(),
                        "Bilety " + request.title() + " x" + request.tickets() + ", zaplacono " + tickets)));
    }
}
```

**Przed (`NewBookingFlow.java`):**

```java
public final class NewBookingFlow {
    private static final Money TICKET_2D = Money.of("25.00");
    private static final Money ONLINE_FEE = Money.of("2.00");

    private final Effects effects;

    public NewBookingFlow(Effects effects) {
        this.effects = effects;
    }

    public String book(BookingRequest request) {
        Money tickets = TICKET_2D.times(request.tickets());
        Money total = tickets.plus(ONLINE_FEE.times(request.tickets()));
        effects.charge(request.card(), total);
        effects.save(request.title() + ";" + request.email() + ";" + request.tickets() + ";" + total);
        effects.sendMail(request.email(),
                "Bilety " + request.title() + " x" + request.tickets() + ", zaplacono " + tickets);
        return "OK " + total;
    }
}
```

**Po (`NewBookingFlow.java`):**

```java
public final class NewBookingFlow {
    private final BookingPlanner planner = new BookingPlanner();
    private final Effects effects;

    public NewBookingFlow(Effects effects) {
        this.effects = effects;
    }

    public String book(BookingRequest request) {
        BookingPlan plan = planner.plan(request);
        plan.effects().forEach(effect -> effect.applyTo(effects));
        return plan.result();
    }
}
```

**Przed (`ShadowBooking.java`):**

```java
private final Infrastructure infra;
private final LegacyBookingFlow legacy;
private final List<String> divergences = new ArrayList<>();
...
try {
    RecordingEffects recorder = new RecordingEffects();
    String shadow = new NewBookingFlow(recorder).book(request);
    compare("wynik", List.of(result), List.of(shadow));
    compare("efekty", legacyEffects, recorder.recorded());
} catch (RuntimeException failure) {
    divergences.add("kandydat: " + failure.getMessage());
}
```

**Po (`ShadowBooking.java`):**

```java
private final Infrastructure infra;
private final LegacyBookingFlow legacy;
private final BookingPlanner candidate = new BookingPlanner();
private final List<String> divergences = new ArrayList<>();
...
try {
    BookingPlan plan = candidate.plan(request);
    compare("wynik", List.of(result), List.of(plan.result()));
    List<String> planned = plan.effects().stream().map(BookingPlan.Effect::describe).toList();
    compare("efekty", legacyEffects, planned);
} catch (RuntimeException failure) {
    divergences.add("kandydat: " + failure.getMessage());
}
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

**Ścieżka pełna (~80 min):** golden master → s01 → s02 → s03 → s04 → s05, czyli kolejność sekcji 3 slajdów. Jeśli chcesz wcześniej ustawić język wektora zachowania, pokaż s03 przed s02 (scena sama wprowadza swoje seamy). Po s03 zrób przerwę na pytania. Sceny s03 i s05 dobrze nadają się na samodzielną pracę uczestników, jeśli brakuje czasu na pokaz (patrz plik zadań modułu 3).
