# Moduł 8. Strategie i dobre praktyki - warsztat CineLegacy: przewodnik prowadzącego

Trzynaście małych scen pokazuje strategie z modułu 8 na kodzie kina CineLegacy: przyrostową wymianę implementacji (Branch by Abstraction, Strangler Fig, shadow), Boy Scout Rule, serię zmian gotowych do przeglądu, ADR i dokumentację żywą, bramki kompilatora i jakości, codemod na AST, wdrożenie etapowe oraz migrację danych expand and contract. Każda scena to pakiet `start` (stan wyjściowy) i kompletne snapshoty `stepN` po każdym ruchu; test sceny uruchamiasz po każdym kroku.

## Jak prowadzić pokaz

```bash
scripts/warsztat.sh list m8                 # sceny i kroki modułu 8
scripts/warsztat.sh test m8/s01             # testy jednej sceny (albo całego modułu: m8)
scripts/warsztat.sh diff m8/s03 1 2         # co zmienia krok 2 względem kroku 1 (0 = start)
scripts/warsztat.sh diff m8/s03 1 2 --word  # to samo, zmiany podświetlone w obrębie linii
scripts/warsztat.sh jump m8/s03 2           # start = step2, gdy trzeba przeskoczyć krok
scripts/warsztat.sh next m8/s03             # następny krok do start + podsumowanie zmian
scripts/warsztat.sh next                    # kolejny krok tej samej sceny (scena zapamiętana)
scripts/warsztat.sh prev                    # krok wstecz
scripts/warsztat.sh status                  # który krok jest teraz w start
scripts/warsztat.sh reset m8/s03            # przywraca start z repozytorium
```

- Zasada: **jeden krok - jeden test - jedno zdanie komentarza**. Zanim ruszysz kod, powiedz, jaki dowód da test po kroku.
- **Krok po kroku bez numerów:** `scripts/warsztat.sh next` wstawia do `start` gotowy następny krok i wypisuje, co się zmieniło. `prev` cofa o krok, `status` mówi, który krok jest teraz w `start`. Skrypt pamięta ostatnią scenę, więc po pierwszym `next m8/sNN` wystarczy samo `next`. Ręczne zmiany w `start` zostają nadpisane - o to chodzi, gdy krok na żywo się rozjechał.
- W tym module część testów to nie tylko równoważność (`SNNEquivalenceTest`), ale też dowody strategii (`SNNSolutionTest`): raport rozbieżności, dziennik efektów ubocznych, wynik bramki, lista naruszeń ADR. Testy, które **dokumentują stan startowy** (np. "start wysyła dwa maile", "start narusza ADR"), zmienią kolor na czerwony, gdy wykonasz kroki na pakiecie `start` - to znak, że start jest już naprawiony. Po pokazie: `scripts/warsztat.sh reset`.
- Sceny s07, s08, s10, s12 i s13 czytają pliki źródłowe sceny. Testy uruchamiaj z katalogu głównego repozytorium (tak działa `warsztat.sh` i `mvn test`).
- Sceny s08 i s10 celowo zawierają kod z ostrzeżeniami kompilatora (`start` i `sample/dirty`) - to materiał dla bramek, nie niedoróbka.

## Mapa scen

| Scena | Temat ze slajdów | Kroki | Pakiet | Czas |
| --- | --- | --- | --- | --- |
| s01 | 1.3 Branch by Abstraction | 4 | `m8.s01_branchbyabstraction` | ~15 min |
| s02 | 1.3-1.4 Strangler Fig, architektura przejściowa | 4 | `m8.s02_stranglerfig` | ~12 min |
| s03 | 1.5 Równoległa weryfikacja (shadow) | 4 | `m8.s03_parallelrun` | ~15 min |
| s04 | 1.6-1.7 Granice trybu shadow | 3 | `m8.s04_shadowlimits` | ~12 min |
| s05 | 2.1-2.4 Boy Scout Rule i nadużycia | 2 | `m8.s05_boyscout` | ~8 min |
| s06 | 3.2 Małe zestawy zmian - czego nie łączyć | 3 | `m8.s06_reviewableseries` | ~10 min |
| s07 | 4.3-4.4 ADR i wykonywalny model decyzji | 2 | `m8.s07_adr` | ~10 min |
| s08 | 5.2-5.3, 5.6 Bramka kompilatora | 3 | `m8.s08_compilergate` | ~10 min |
| s09 | 5.4-5.5 Automatyczna transformacja (codemod) | 3 | `m8.s09_codemod` | ~15 min |
| s10 | 5.6-5.7 Minimalna bramka jakości | 4 | `m8.s10_qualitygate` | ~12 min |
| s11 | 6.2-6.4 Jawna polityka wdrożenia etapowego | 3 | `m8.s11_stagedrollout` | ~10 min |
| s12 | 6.5-6.6 Dane, wycofanie, zamknięcie migracji | 4 | `m8.s12_expandcontract` | ~15 min |
| s13 | 4.1, 4.5 Dokumentacja żywa i historyczna | 2 | `m8.s13_livingdocs` | ~8 min |

## Scena s01. Branch by Abstraction na cenniku z CinemaManager

**Temat ze slajdów:** 1.3-1.4 Branch by Abstraction, Strangler Fig, architektura przejściowa
**Pakiet:** `pl.training.workshop.m8.s01_branchbyabstraction` · **Test:** `scripts/warsztat.sh test m8/s01`
**Czas:** ~15 min

### W skrócie

**Co robimy:** `BookingService.confirm` ma wklejony stary cennik z `CinemaManager` (`double`, kody formatu, typy "S"/"E"/"C") i nie ma szwu, przez który dałoby się go wymienić. Izolujemy stary cennik w osobnej klasie, chowamy go za abstrakcją `TicketPricing` zwracającą `Money`, dokładamy obok `ModernTicketPricing` z przełącznikiem, a na końcu usuwamy starą ścieżkę.

**Zasada:** Branch by Abstraction to "gałąź" zrobiona w kodzie, a nie w Git: stabilny kontrakt wewnątrz aplikacji, za nim stara i nowa implementacja, a każdy krok trafia od razu do głównej gałęzi. Wydzielenie abstrakcji i adaptera to jeszcze refaktoryzacja, a nowa implementacja i przełączenie to już migracja. Nie mylić ze Strangler Fig, który działa na granicy systemu, a nie wewnątrz aplikacji.

**Efekt:** `BookingService` zależy tylko od `TicketPricing` i liczy w `Money`, a starego cennika i przełącznika `PricingMode` już nie ma - migracja jest zamknięta. Do pilnowania zostaje moment zaokrąglenia: nowy kod zaokrągla każdy procent osobno, a legacy dopiero sumę.

### Co widzimy

`start.BookingService.confirm` to kopia cennika z `CinemaManager.book()`: `double`, kody formatu `1/2/3`, typy biletu `"S"/"E"/"C"` i formatowanie potwierdzenia w jednej metodzie. Chcemy wymienić cennik na nową implementację, ale nie ma szwu, a długa gałąź w Git na czas przepisywania to antywzorzec.

```java
double sum = 0;
for (int i = 0; i < request.seats().size(); i++) {
    double p = 0;
    int f = s.format();
    if (f == 1) {
        p = 25.00;
    } ...
}
if (request.seats().size() >= 10) {
    sum = sum - sum * 0.10;
}
```

### Krok 1: Extract Method + Move - stary cennik w osobnej klasie

**W IDE:** zaznacz blok od `Screening s = ...` do obliczenia `total`, ⌥⌘M, nazwa `total`, zwraca `double`. Następnie F6 (Move) do nowej klasy `LegacyTicketPricing`. W `BookingService` pole `pricing`.
**Po:**

```java
double total = pricing.total(request);
return request.screening().title() + ": " + String.join(",", request.seats())
        + " - do zaplaty " + String.format(Locale.ROOT, "%.2f", total);
```

**Uruchom:** `scripts/warsztat.sh test m8/s01` - równoważność zielona dla wszystkich wariantów.
**Co powiedzieć:** starego kodu nie poprawiamy, tylko go izolujemy. Brzydki, ale ma jedno wejście i jedno wyjście.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m8/s01 0 1`

### Krok 2: abstrakcja TicketPricing, stary cennik za nią

**W IDE:** na `LegacyTicketPricing` Refactor → Extract Interface, nazwa `TicketPricing`. Zmień typ zwracany kontraktu na `Money` (⌘F6) i w implementacji zamień `double` na `Money` dopiero na granicy (`new Money(BigDecimal.valueOf(total))`). `BookingService` dostaje konstruktor z `TicketPricing`.
**Po:**

```java
public interface TicketPricing {
    Money total(BookingRequest request);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** abstrakcję projektujemy pod przyszłość (Money), a stary kod dostaje adapter. Do tego momentu to czysta refaktoryzacja - można ją zintegrować z główną gałęzią od razu.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m8/s01 1 2`

### Krok 3: nowa implementacja obok i przełącznik

**W IDE:** nowa klasa `ModernTicketPricing implements TicketPricing` (Money, `switch` na formacie i typie, nazwane stałe). Enum `PricingMode { LEGACY, MODERN }` i konstruktor `BookingService(PricingMode)`; domyślnie `LEGACY`.
**Po:**

```java
public BookingService(PricingMode mode) {
    this(switch (mode) {
        case LEGACY -> new LegacyTicketPricing();
        case MODERN -> new ModernTicketPricing();
    });
}
```

**Uruchom:** test zielony; `S01SolutionTest.legacyAndModernPricingFulfilTheSameContract` porównuje obie implementacje na tych samych żądaniach.
**Co powiedzieć:** wdrożenie nowego kodu nie zmienia zachowania - zmienia je dopiero konfiguracja. To już migracja, a nie refaktoryzacja, więc przełącznik ma mieć właściciela i termin usunięcia.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m8/s01 2 3`

### Krok 4: usunięcie starej implementacji i przełącznika

**W IDE:** Safe Delete (⌘⌦) na `LegacyTicketPricing` i `PricingMode`; `BookingService()` tworzy `ModernTicketPricing`.
**Po:**

```java
public BookingService() {
    this(new ModernTicketPricing());
}
```

**Uruchom:** test zielony; `migrationIsClosedOnlyWhenTheOldPathIsGone` sprawdza, że klas już nie ma.
**Co powiedzieć:** migracja kończy się po usunięciu starej ścieżki. Abstrakcja może zostać jako szew dla testów.
**Snapshot:** `step4/` · **Różnica:** `scripts/warsztat.sh diff m8/s01 3 4`

### Rozwiązanie i uzasadnienie

Cztery kroki, każdy z zielonym buildem i możliwy do zintegrowania osobno. Gałąź istnieje w kodzie (abstrakcja + przełącznik), a nie w systemie kontroli wersji. Test równoważności obejmuje obie gałęzie przełącznika w kroku 3.

### Pułapki

- Moment zaokrąglenia: legacy sumuje `double` i zaokrągla na końcu, nowy kod zaokrągla każdy procent. Dla kwot z "połową grosza" (np. 10% z 245.25) wyniki mogą się różnić - dopisz taki przypadek do testu kontraktu, zanim przełączysz.
- Abstrakcja skopiowana 1:1 ze starej sygnatury (`double total(...)`) utrwala stary model.
- Przełącznik bez terminu usunięcia staje się nowym legacy.

### Pytanie do sali

Który z czterech kroków jest refaktoryzacją, a który migracją? Jakie dowody są potrzebne dla każdego z nich?

## Scena s02. Strangler Fig - fasada, która przejmuje ścieżki

**Temat ze slajdów:** 1.3-1.4 Branch by Abstraction, Strangler Fig, architektura przejściowa
**Pakiet:** `pl.training.workshop.m8.s02_stranglerfig` · **Test:** `scripts/warsztat.sh test m8/s02`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `LegacyCinema` jest wystawiony klientom wprost jako `CinemaApi`, więc nie da się przenieść rezerwacji bez raportu. Stawiamy przed nim `CinemaFacade`, która najpierw deleguje 1:1, potem kieruje rezerwację do `BookingModule`, raport do `ReportModule`, a na końcu legacy znika.

**Zasada:** Strangler Fig to przejmowanie systemu operacja po operacji: brama, proxy lub fasada na granicy systemu kieruje część ruchu do nowego komponentu, a reszta dalej trafia do starego. Każdy element przejściowy, tu fasada i jej routing, potrzebuje właściciela i kryterium usunięcia. Fasada tylko kieruje ruch - nie powinna mieć własnej logiki biznesowej.

**Efekt:** Klienci rozmawiają z fasadą, która opisuje routing w `routes()`, a `LegacyCinema` zostało usunięte po przejęciu obu ścieżek. Przez okres hybrydy obie strony musiały pisać do `BookingLedger` w tym samym formacie - zgodność danych to osobny kontrakt migracji.

### Co widzimy

`start.LegacyCinema` implementuje publiczne API `CinemaApi` (rezerwacja i raport) i jest wystawiony klientom wprost. Obie operacje korzystają ze wspólnej bazy `BookingLedger`. Nie da się przenieść jednej operacji bez drugiej.

```java
public final class LegacyCinema implements CinemaApi {
    public String book(String email, String title, int format, int tickets, boolean web) { ... }
    public String report() { ... }
}
```

### Krok 1: fasada 1:1

**W IDE:** nowa klasa `CinemaFacade implements CinemaApi`; Refactor → Delegate (albo ręcznie ⌘N → Delegate Methods) do `LegacyCinema`. Metoda `routes()` opisuje, kto obsługuje którą operację.
**Po:**

```java
public String book(String email, String title, int format, int tickets, boolean web) {
    return legacy.book(email, title, format, tickets, web);
}
```

**Uruchom:** `scripts/warsztat.sh test m8/s02` - scenariusz klienta (rezerwacje + raport) daje identyczny zapis.
**Co powiedzieć:** fasada nic nie zmienia, ale daje jedno miejsce przekierowania. Klienci od teraz rozmawiają z fasadą.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m8/s02 0 1`

### Krok 2: przejęcie pierwszej ścieżki - rezerwacja

**W IDE:** nowa klasa `BookingModule` (Money, nazwane reguły), w fasadzie `book` deleguje do niej; `routes()` zwraca `book=new`.
**Po:**

```java
public String book(String email, String title, int format, int tickets, boolean web) {
    return bookings.book(email, title, format, tickets, web);
}

public String report() {
    return legacy.report();
}
```

**Uruchom:** test zielony; `legacyReportSeesBookingsTakenByTheNewModule` - raport legacy widzi rezerwacje nowego modułu, bo baza jest wspólna.
**Co powiedzieć:** przez chwilę system jest hybrydą. To działa tylko dlatego, że obie strony piszą w tym samym formacie danych - zgodność danych to osobny kontrakt.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m8/s02 1 2`

### Krok 3: przejęcie kolejnej ścieżki - raport

**W IDE:** nowa klasa `ReportModule` z tym samym formatem wyjścia (sumy w Money); fasada kieruje `report` do niej. `LegacyCinema` zostaje w kodzie, ale bez ruchu.
**Po:**

```java
public Map<String, String> routes() {
    return Map.of("book", "new", "report", "new");
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** stary kod zostaje na czas okna wycofania - powrót to jedna linia w fasadzie.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m8/s02 2 3`

### Krok 4: usunięcie legacy

**W IDE:** Safe Delete (⌘⌦) na `LegacyCinema` - IDE potwierdzi brak użyć.
**Po:** fasada ma tylko `BookingModule` i `ReportModule`.
**Uruchom:** test zielony; `legacyCinemaIsDeletedInTheLastStep`.
**Co powiedzieć:** kryterium usunięcia było znane od kroku 1 (brak ruchu do legacy + zamknięte okno wycofania).
**Snapshot:** `step4/` · **Różnica:** `scripts/warsztat.sh diff m8/s02 3 4`

### Rozwiązanie i uzasadnienie

Strangler Fig działa na granicy systemu (publiczne API), Branch by Abstraction (s01) wewnątrz aplikacji. Test równoważności uruchamia ten sam scenariusz klienta na każdym etapie hybrydy.

### Pułapki

- Nowy moduł z innym formatem danych niż legacy - raport legacy przestaje się zgadzać w kroku 2.
- Fasada, która zaczyna mieć własną logikę biznesową.
- Brak `routes()` albo innego jawnego opisu routingu - po miesiącu nikt nie wie, co gdzie działa (patrz s13).

### Pytanie do sali

Którą operację przenieślibyście jako pierwszą w prawdziwym CinemaManager i dlaczego: najprostszą, najczęściej zmienianą czy najbardziej ryzykowną?

## Scena s03. Równoległa weryfikacja (shadow) kalkulatora cen

**Temat ze slajdów:** 1.5 Przykład: równoległa weryfikacja kalkulatora
**Pakiet:** `pl.training.workshop.m8.s03_parallelrun` · **Test:** `scripts/warsztat.sh test m8/s03`
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
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m8/s03 0 1`

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
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m8/s03 1 2`

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
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m8/s03 2 3`

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
**Snapshot:** `step4/` · **Różnica:** `scripts/warsztat.sh diff m8/s03 3 4`

### Rozwiązanie i uzasadnienie

Shadow daje dowód na prawdziwym ruchu, zanim nowa implementacja stanie się autorytatywna. Typowane zdarzenia raportu pozwalają podjąć decyzję bez parsowania logów.

### Pułapki

- Brak izolacji awarii kandydata - wyjątek w cieniu zatrzymuje sprzedaż.
- Zgodność obu implementacji nie dowodzi poprawności: obie mogą mieć ten sam błąd (np. wycena 4DX na 0.00 w legacy).
- Produkcyjny cień kosztuje: latencja, timeout, limit ruchu.

### Pytanie do sali

Ile dni ruchu i ile zgodnych porównań wystarczy, żeby przełączyć tryb na CANDIDATE? Kto o tym decyduje?

## Scena s04. Granice trybu shadow - efekty uboczne

**Temat ze slajdów:** 1.6-1.7 Granice trybu shadow i antywzorce migracji
**Pakiet:** `pl.training.workshop.m8.s04_shadowlimits` · **Test:** `scripts/warsztat.sh test m8/s04`
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
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m8/s04 0 1`

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
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m8/s04 1 2`

### Krok 3: Separate Query from Modifier - czysty plan

**W IDE:** z `NewBookingFlow.book` wydziel (⌥⌘M, potem Move F6) klasę `BookingPlanner` z metodą `plan`, która zwraca `BookingPlan(result, effects)`. `NewBookingFlow` wykonuje plan na porcie; `ShadowBooking` woła tylko `BookingPlanner`. `RecordingEffects` usuń (Safe Delete).
**Po:**

```java
BookingPlan plan = candidate.plan(request);
compare("wynik", List.of(result), List.of(plan.result()));
```

**Uruchom:** test zielony; `step3ComparesAPurePlanAndNeverTouchesInfrastructure`.
**Co powiedzieć:** typ gwarantuje bezpieczeństwo: `BookingPlanner` nie ma dostępu do portu efektów, więc w cieniu nie da się go użyć "za mocno".
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m8/s04 2 3`

### Rozwiązanie i uzasadnienie

Podwójne wykonanie tylko dla czystych obliczeń. Efekty (płatność, mail, zapis, zdarzenia) w cieniu są danymi do porównania, a nie akcjami.

### Pułapki

- Nagrywarka podpięta tylko w części miejsc - jeden zapomniany efekt i klient dostaje drugi SMS.
- Efekty ukryte w obliczeniu: sekwencja numerów rezerwacji, cache, licznik - też są efektami.
- Zgodność odpowiedzi API to nie dowód zgodności danych i transakcji.

### Pytanie do sali

Jakie efekty uboczne ma wasz "czysty" kod, o których zwykle nie myślicie (logi audytowe, metryki, sekwencje)?

## Scena s05. Boy Scout Rule - mała poprawa i jej nadużycie

**Temat ze slajdów:** 2.1-2.4 Heurystyka, nie mandat; bezpieczna sekwencja i typowe nadużycia
**Pakiet:** `pl.training.workshop.m8.s05_boyscout` · **Test:** `scripts/warsztat.sh test m8/s05`
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

**W IDE:** pokaż antyprzykład: `scripts/warsztat.sh jump m8/s05 1`. Obok dobrych ruchów przemycono sortowanie miejsc, e-mail małymi literami i pominięcie linii "Tel" bez telefonu.
**Po:**

```java
List<String> sortedSeats = ticket.seats().stream().sorted().toList();
text.append("Klient: ").append(ticket.email().trim().toLowerCase(Locale.ROOT)).append('\n');
```

**Uruchom:** `scripts/warsztat.sh test m8/s05` - równoważność czerwona w trzech przypadkach (m.in. `A9, A10` staje się `A10, A9`). `S05SolutionTest.abusiveCleanupChangesBehaviourInThreeCases` dokumentuje, które.
**Co powiedzieć:** każda z tych zmian może być dobrym pomysłem, ale to decyzje biznesowe, nie sprzątanie. Cofamy: `scripts/warsztat.sh reset m8/s05`.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m8/s05 0 1`

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
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m8/s05 0 2`

### Rozwiązanie i uzasadnienie

Boy Scout Rule to heurystyka: mała, lokalna, weryfikowalna poprawa w kodzie bieżącej zmiany. Test różnicowy (start kontra krok) jest bezstronnym arbitrem, czy "sprzątanie" nie zmieniło zachowania.

### Pułapki

- Zmiana zachowania pod etykietą refaktoryzacji.
- Masowe formatowanie całego pliku razem z poprawką - recenzent nie znajdzie zmiany merytorycznej.
- "Sprzątanie" cudzego modułu bez uzgodnienia.

### Pytanie do sali

Posortowane miejsca są czytelniejsze. Jak wprowadzić tę zmianę uczciwie?

## Scena s06. Seria małych zmian gotowych do przeglądu

**Temat ze slajdów:** 3.2 Małe zestawy zmian - czego nie łączyć; 3.3-3.4 Przegląd kodu
**Pakiet:** `pl.training.workshop.m8.s06_reviewableseries` · **Test:** `scripts/warsztat.sh test m8/s06`
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
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m8/s06 0 1`

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
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m8/s06 1 2`

### Krok 3: commit 3 - zmiana zachowania

**W IDE:** w gałęzi `default` warunek `isCheapTuesday(query) ? 20 : 0`, metoda pomocnicza, stała `CHEAP_TUESDAY_PERCENT`.
**Po:**

```java
default -> isCheapTuesday(query) ? CHEAP_TUESDAY_PERCENT : 0;
```

**Uruchom:** test zielony; `S06SolutionTest.cheapTuesdayGivesNormalTicketTwentyPercentOff` (2D wtorek 20.00) i `behaviourChangeIsLimitedToNormalTicketsOnTuesday` - na siatce 336 przypadków zmieniło się dokładnie 12, wszystkie NORMAL we wtorek.
**Co powiedzieć:** diff to kilka linii. Test różnicowy commitu 2 kontra 3 mówi recenzentowi dokładnie, gdzie zmieniło się zachowanie.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m8/s06 2 3`

### Rozwiązanie i uzasadnienie

Trzy zestawy zmian, każdy z jedną intencją i zielonym buildem. Refaktoryzacje dowodzi test równoważności, zmianę zachowania - test nowej reguły i test różnicowy ograniczający jej zasięg.

### Pułapki

- Łączenie refaktoryzacji ze zmianą zachowania: recenzent nie odróżni ruchu mechanicznego od nowej reguły.
- Łączenie formatowania z logiką, diffu automatycznego z ręcznym.
- Commit przygotowawczy, który "przy okazji" poprawia regułę.

### Pytanie do sali

Jak opisalibyście commit 3 według pięciu pytań ze slajdu 3.3 (dlaczego, co i czego nie, kontrakt, dowody, wdrożenie i wycofanie)?

## Scena s07. ADR i wykonywalny model decyzji

**Temat ze slajdów:** 4.3-4.4 Architecture Decision Record
**Pakiet:** `pl.training.workshop.m8.s07_adr` · **Test:** `scripts/warsztat.sh test m8/s07`
**Czas:** ~10 min

### W skrócie

**Co robimy:** ADR-0007 mówi, że cennik nie zależy od `notification` i nie używa `double`, a cennik w `start` sam wysyła mail o rabacie grupowym i liczy w `double`. Cennik zaczyna zwracać `Quote` z informacją o rabacie, mail wysyła `BookingService`, a kwoty przechodzą na `Money`.

**Zasada:** ADR zapisuje decyzję o trwałym wpływie: kontekst, opcje, decyzję i konsekwencje - to dokument historyczny, którego się nie edytuje, tylko zastępuje nowym. Regułę z ADR warto uczynić wykonywalną, żeby build pilnował jej na co dzień. Nie pisze się ADR dla każdego Rename.

**Efekt:** `ArchitectureRules` zwraca pustą listę naruszeń, a maile i odpowiedzi są takie same jak przed zmianą. Reguła tekstowa ma swoje granice (pełna nazwa klasy bez importu albo refleksja ją ominą) i ADR opisuje to w konsekwencjach.

### Co widzimy

Obok kodu sceny leży `ADR-0007-cennik-jako-czysty-modul.md` (status: Zaakceptowana) z dwiema regułami: **R1** pakiet `pricing` nie zależy od `notification`, **R2** `pricing` nie używa `double`. `ArchitectureRules` to wykonywalny model tych reguł. W `start` cennik sam wysyła mail o rabacie grupowym i liczy w `double`:

```java
import pl.training.workshop.m8.s07_adr.start.notification.GroupMailer;
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
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m8/s07 0 1`

### Krok 2: spełnienie R2 - Money

**W IDE:** Type Migration (⇧⌘F6) `double` → `Money` w `Quote` i `TicketPricing`; nazwane stałe dla progu i procentu grupy; `BookingService` podaje `Money.of("25.00")`.
**Po:**

```java
public Quote total(int tickets, Money unitPrice) {
    Money sum = unitPrice.times(tickets);
```

**Uruchom:** test zielony; `step2CompliesWithTheDecision` - pusta lista naruszeń.
**Co powiedzieć:** moduł jest zgodny z decyzją, a test pilnuje, żeby tak zostało.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m8/s07 1 2`

### Rozwiązanie i uzasadnienie

ADR opisuje kontekst, opcje i konsekwencje (historia), a `ArchitectureRules` sprawdza regułę w każdym buildzie. `adrDocumentsEveryExecutableRule` pilnuje, żeby każda reguła w kodzie miała opis w ADR.

### Pułapki

- ADR dla każdego Rename - dokumentuje się decyzje o trwałym wpływie.
- Edycja zaakceptowanego ADR zamiast nowego zapisu, który go zastępuje.
- Reguła tekstowa ma granice: pełna nazwa klasy bez importu albo refleksja ją ominą (opisane w "Konsekwencjach").

### Pytanie do sali

Co zrobić, gdy zespół chce złamać R2 dla jednego przypadku (np. statystyk)? Wyjątek w teście czy nowy ADR?

## Scena s08. Bramka kompilatora -Xlint:all -Werror

**Temat ze slajdów:** 5.2-5.3 Refaktoryzacje IDE i kompilator Javy 25; 5.6 Bramka kompilatora
**Pakiet:** `pl.training.workshop.m8.s08_compilergate` · **Test:** `scripts/warsztat.sh test m8/s08`
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

**Uruchom:** `scripts/warsztat.sh test m8/s08` - zostają kategorie `deprecation`, `fallthrough`.
**Co powiedzieć:** jedna kategoria ostrzeżeń na krok - łatwy przegląd, łatwe wycofanie.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m8/s08 0 1`

### Krok 2: nowe API zamiast przestarzałego

**W IDE:** w `return` zamień argument `format` na istniejącą już zmienną `name` - wywołanie trafia do `PriceTable.basePrice(String)`. Ctrl+klik na metodzie potwierdza, że to nowe przeciążenie.
**Po:**

```java
return name + " [" + features + "], cena " + PriceTable.basePrice(name)
        + " zl, zajete: " + map.takenPerRow();
```

**Uruchom:** test zielony; zostaje tylko `[fallthrough] OccupancyReport.java:13`.
**Co powiedzieć:** przestarzałą metodę usuniemy (Safe Delete) dopiero, gdy nikt jej nie woła - także poza repozytorium.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m8/s08 1 2`

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
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m8/s08 2 3`

### Rozwiązanie i uzasadnienie

Bramka zwraca strukturalne wyniki, więc test może wymagać "zero ostrzeżeń" albo "nie więcej niż stan bazowy". Ciekawostka z implementacji: z `-Werror` javac przerywa po fazie z pierwszym ostrzeżeniem (fallthrough z analizy przepływu by się nie pokazał), więc pełną listę zbieramy drugim przebiegiem bez `-Werror`.

### Pułapki

- Nagłe włączenie `-Werror` w dużym legacy blokuje zespół - najpierw stan bazowy i "brak nowych naruszeń".
- `@SuppressWarnings("all")` na klasie zamiast wąskiego wyłączenia z uzasadnieniem.
- `--release 25` nie kontroluje, którym JDK uruchamiany jest Maven w CI.

### Pytanie do sali

Jak wprowadzić tę bramkę w projekcie z 3000 ostrzeżeń, nie blokując nikogo od jutra?

## Scena s09. Codemod na Compiler Tree API

**Temat ze slajdów:** 5.4-5.5 Analiza statyczna, formatowanie i automatyzacja (receptury)
**Pakiet:** `pl.training.workshop.m8.s09_codemod` · **Test:** `scripts/warsztat.sh test m8/s09`
**Czas:** ~15 min

### W skrócie

**Co robimy:** `BookCallCodemod` migruje wywołania przestarzałego `book(..., boolean, boolean)` wyrażeniem regularnym, które trafia w komentarz i w `HotelService`, a gubi wywołanie rozbite na trzy linie. Zastępujemy regex wyszukiwaniem po AST, przepisujemy argumenty według pozycji z drzewa, a na końcu dopasowujemy wywołania po typach.

**Zasada:** Codemod (receptura automatyczna) to powtarzalna transformacja wielu miejsc naraz; oparty na składni i typach jest bezpieczniejszy od regexa, ale automatyzacja zwiększa też zasięg błędu receptury. Recepturę sprawdzają testy `before`/`after`, kompilacja wyniku i test idempotencji.

**Efekt:** Codemod migruje tylko wywołania rozwiązane do przestarzałej `cinema.BookingService.book`, wynik kompiluje się bez ostrzeżeń, a drugie uruchomienie niczego nie zmienia. Kosztem jest sporo kodu narzędziowego, więc przy kilku miejscach szybsze bywa Change Signature w IDE.

### Co widzimy

`SampleProject` zawiera API kina z przestarzałym `book(..., boolean web, boolean ownGlasses)` i nowym `book(..., Channel, Glasses)`, łudząco podobne `HotelService.book(..., boolean, boolean)` oraz klienta `TicketDesk` z trzema wywołaniami: w jednej linii (11), rozbite na trzy linie (16-18) i hotelowe (22), plus komentarz ze starym przykładem (15). `start.BookCallCodemod` to "grep i zamień":

```java
private static final Pattern OLD_CALL =
        Pattern.compile("\\bbook\\(.*,\\s*(true|false)\\s*,\\s*\\w+\\s*\\)");
```

Test pokazuje: regex znajduje linie 11, 15, 22 - trafia w komentarz i hotel, a gubi wywołanie wielolinijkowe.

### Krok 1: wyszukiwanie przez AST

**W IDE:** ręcznie - `JavacTask.parse()` i `TreeScanner.visitMethodInvocation`; dopasowanie: `MemberSelectTree` o nazwie `book` z sześcioma argumentami; linia z `SourcePositions` i `LineMap`.
**Po:**

```java
if (looksLikeOldBook(call)) {
    long start = positions.getStartPosition(unit, call);
    lines.add((int) unit.getLineMap().getLineNumber(start));
}
```

**Uruchom:** test zielony; `step1AstSearchFindsRealCallsIncludingMultilineOne` - linie 11, 16, 22.
**Co powiedzieć:** parser widzi wywołania, nie tekst. Ale 22 to nadal hotel - sama składnia nie zna typów.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m8/s09 0 1`

### Krok 2: przepisanie według pozycji z AST

**W IDE:** `rewrite` podmienia dokładnie dwa ostatnie argumenty (literał → stała enum, wyrażenie → operator warunkowy) i dopisuje importy; edycje stosowane od końca pliku.
**Po:**

```java
if (arg instanceof LiteralTree literal && literal.getValue() instanceof Boolean value) {
    return new Edit(start, end, value ? ifTrue : ifFalse);
}
```

**Uruchom:** test zielony; `step2SyntacticRewriteBreaksTheHotelCallAndIsNotIdempotent` - wywołanie hotelowe zostało "zmigrowane" i kod się nie kompiluje, a drugie uruchomienie psuje już zmigrowane wywołania.
**Co powiedzieć:** automatyzacja zwiększa zasięg - także zasięg błędu receptury.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m8/s09 1 2`

### Krok 3: dopasowanie po typach

**W IDE:** analiza razem ze źródłami projektu (`task.analyze()`), `TreePathScanner` i `Trees.getElement`; migrujemy tylko wywołania rozwiązane do przestarzałej metody `cinema.BookingService.book`.
**Po:**

```java
return method.getSimpleName().contentEquals("book")
        && elements.isDeprecated(method)
        && method.getEnclosingElement() instanceof TypeElement owner
        && owner.getQualifiedName().contentEquals(OLD_API_OWNER);
```

**Uruchom:** test zielony; wynik identyczny z oczekiwanym `MIGRATED`, kompiluje się bez ostrzeżeń (`-Xlint:all`), a drugie uruchomienie niczego nie zmienia.
**Co powiedzieć:** receptura oparta na typach, test `before/after` na próbce, test idempotencji. Tak działają narzędzia typu OpenRewrite.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m8/s09 2 3`

### Rozwiązanie i uzasadnienie

Codemod ma trzy dowody: znajduje dokładnie właściwe miejsca, wynik się kompiluje bez użycia przestarzałego API, a ponowne uruchomienie jest bezpieczne. Diff z codemodu idzie do osobnego commita niż zmiany ręczne.

### Pułapki

- Regex na kodzie źródłowym: komentarze, stringi, wywołania wielolinijkowe, przeciążenia.
- Brak testu idempotencji - receptura uruchomiona drugi raz w CI psuje kod.
- Importy dopisane "gdzieś" - kolejność poprawi formatter, ale w osobnym commicie.

### Pytanie do sali

Kiedy codemod się opłaca, a kiedy szybciej jest poprawić 12 miejsc ręcznie z pomocą Change Signature w IDE?

## Scena s10. Minimalna bramka jakości jako kod

**Temat ze slajdów:** 5.6-5.7 Bramka kompilatora i minimalna bramka jakości
**Pakiet:** `pl.training.workshop.m8.s10_qualitygate` · **Test:** `scripts/warsztat.sh test m8/s10`
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

**Uruchom:** `scripts/warsztat.sh test m8/s10` - `TODO PriceTable.java:14`, `System.out PriceTable.java:23`.
**Co powiedzieć:** tanie, deterministyczne, z pozycją - od tego zaczynamy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m8/s10 0 1`

### Krok 2: ostrzeżenia kompilatora

**W IDE:** metoda `compileSources` (javax.tools, `-Xlint:all`), każda diagnostyka poza NOTE to wynik.
**Po:**

```java
findings.add("kompilator " + Path.of(d.getSource().toUri()).getFileName() + ":"
        + d.getLineNumber() + " " + d.getCode());
```

**Uruchom:** test zielony; dochodzą dwa wyniki `compiler.warn.raw.class.use`.
**Co powiedzieć:** to ta sama idea co w s08, tu jako jeden z kilku sygnałów bramki.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m8/s10 1 2`

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
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m8/s10 2 3`

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
**Snapshot:** `step4/` · **Różnica:** `scripts/warsztat.sh diff m8/s10 3 4`

### Rozwiązanie i uzasadnienie

Bramka łączy kilka niezależnych sygnałów. `S10EquivalenceTest` pilnuje drugiej strony: czysta próbka przechodzi każdą wersję bramki (brak fałszywych alarmów).

### Pułapki

- Bramka, która nic nie sprawdza, a daje zielony znaczek.
- Przybliżenie pokrycia potraktowane jak pomiar.
- Bramka bez właściciela - każdy fałszywy alarm kończy się jej wyłączeniem.

### Pytanie do sali

Które z tych czterech sprawdzeń uruchomilibyście lokalnie przed commitem, a które tylko w CI?

## Scena s11. Jawna polityka wdrożenia etapowego

**Temat ze slajdów:** 6.2 Warstwy kontroli; 6.3-6.4 Kryteria i jawna polityka wdrożenia etapowego
**Pakiet:** `pl.training.workshop.m8.s11_stagedrollout` · **Test:** `scripts/warsztat.sh test m8/s11`
**Czas:** ~10 min

### W skrócie

**Co robimy:** `CheckoutRouter` wdraża nowy proces płatności przez stałą w kodzie i testerów wpisanych w `if`, bez etapów i bez wyłącznika. Zamieniamy to na rekord `RolloutPolicy` z procentem ruchu, deterministycznym koszykiem klienta i wyłącznikiem awaryjnym.

**Zasada:** Wdrożenie etapowe to jawna, testowana polityka: populacja, deterministyczny podział klientów, wyjątki i wyłącznik, a o zwiększeniu ekspozycji decydują z góry ustalone kryteria. Podział ma być stabilny - ten sam klient zawsze na tej samej ścieżce - więc bez losowania i bez `hashCode` obiektu.

**Efekt:** Z dotychczasowymi ustawieniami router kieruje klientów tak samo jak `start`, procent da się zwiększać bez wyrzucania nikogo, a kill switch wycofuje zmianę bez nowego wydania. Świadomie zmienia się jedno zachowanie: e-mail jest normalizowany, a start porównywał go dosłownie.

### Co widzimy

`start.CheckoutRouter` wdraża nowy proces płatności "na flagę": stała w kodzie (zmiana = nowe wydanie), testerzy wpisani w `if`, brak etapów i wyłącznika awaryjnego.

```java
static final boolean NEW_CHECKOUT = false;

public boolean useNewCheckout(String email) {
    if (NEW_CHECKOUT) {
        return true;
    }
    return email.equals("anna@kino.pl") || email.equals("jan@kino.pl");
}
```

### Krok 1: polityka jako wartość

**W IDE:** Introduce Parameter Object dla flagi i listy testerów → record `RolloutPolicy(boolean enabled, Set<String> allowList)`, fabryka `current()` z dotychczasowymi wartościami; router dostaje politykę w konstruktorze.
**Po:**

```java
public boolean useNewCheckout(String email) {
    return policy.allows(email);
}
```

**Uruchom:** `scripts/warsztat.sh test m8/s11` - równoważność dla dotychczasowych ustawień.
**Co powiedzieć:** konfigurację da się podać z zewnątrz, przetestować i przejrzeć.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m8/s11 0 1`

### Krok 2: deterministyczny podział klientów

**W IDE:** ⌘F6 na rekordzie: `int percent` zamiast `boolean enabled` (0 = dawne `false`); metoda `bucket` - CRC32 znormalizowanego e-maila modulo 100.
**Po:**

```java
public boolean allows(String email) {
    return allowList.contains(normalize(email)) || bucket(email) < percent;
}
```

**Uruchom:** test zielony; `sameCustomerAlwaysGetsTheSamePath`, `percentOfCustomersIsRoughlyRespected`, `increasingPercentNeverRemovesAnyone`.
**Co powiedzieć:** stabilny skrót, nie `hashCode` obiektu i nie losowanie. Ten sam klient zawsze widzi tę samą ścieżkę, a zwiększenie procentu nikogo nie wyrzuca. Uwaga: normalizacja e-maila to świadoma zmiana - start porównywał dosłownie (`startComparesEmailLiterally`).
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m8/s11 1 2`

### Krok 3: wyłącznik awaryjny

**W IDE:** pole `killSwitch` w rekordzie, sprawdzane jako pierwsze w `allows`.
**Po:**

```java
if (killSwitch) {
    return false;
}
```

**Uruchom:** test zielony; `killSwitchOverridesPercentAndAllowList`.
**Co powiedzieć:** kill switch ma pierwszeństwo nawet przed testerami - służy do natychmiastowego wycofania bez wydania.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m8/s11 2 3`

### Rozwiązanie i uzasadnienie

Polityka wdrożenia to kod z testami: populacja, deterministyczny podział, wyjątki i wyłącznik. Decyzję o zwiększeniu procentu (ADVANCE / HOLD / ROLLBACK) podejmuje się na podstawie metryk - to temat przykładu `RolloutPolicy` z pakietu `pl.training.module8.risk`.

### Pułapki

- Losowanie przy każdym żądaniu - klient przeskakuje między ścieżkami.
- `String.hashCode()` z ujemnym wynikiem i `%` - ujemne koszyki.
- Flaga bez właściciela i terminu usunięcia.

### Pytanie do sali

Kill switch wyłącza nowy proces płatności. Co z zamówieniami, które są w trakcie płatności w nowym procesie?

## Scena s12. Expand and contract - format danych rezerwacji

**Temat ze slajdów:** 6.5-6.6 Dane, wycofanie i zamknięcie migracji
**Pakiet:** `pl.training.workshop.m8.s12_expandcontract` · **Test:** `scripts/warsztat.sh test m8/s12`
**Czas:** ~15 min

### W skrócie

**Co robimy:** Repozytorium rezerwacji zna tylko format csv, a zapis nowego formatu w tej samej kolumnie uniemożliwiłby wycofanie wydania. Przechodzimy na kolumnę `payload` w czterech krokach: podwójny zapis, odczyt z fallbackiem, backfill starych wierszy i contract.

**Zasada:** Expand and contract migruje dane tak, żeby w oknie wycofania stara wersja kodu czytała to, co zapisała nowa: najpierw rozszerzamy (nowe pole, podwójny zapis), potem przełączamy odczyt i uzupełniamy stare dane, a stary format usuwamy na końcu. Flaga ani wycofanie `.jar` nie cofną danych zapisanych w niezgodnym formacie.

**Efekt:** Repozytorium pisze i czyta tylko wersjonowany `payload`, a w kodzie nie ma już odwołań do starego formatu - migracja jest zamknięta. Ceną jest zamknięcie okna wycofania: po contract stara wersja nie widzi nowych danych, więc ten krok robi się dopiero, gdy powrót nie będzie potrzebny.

### Co widzimy

`BookingTable` ma starą kolumnę `csv` i nową, pustą kolumnę `payload` (migracja schematu "expand" już zrobiona). `start.BookingRepository` zna tylko csv: `B1;anna@kino.pl;A5,A10;84.00`. Zapisanie nowego formatu "w miejscu" do tej samej kolumny uniemożliwiłoby wycofanie wydania.

```java
public void save(Booking booking) {
    table.put(booking.id(), new BookingTable.Row(CsvBookingFormat.write(booking), null));
}
```

### Krok 1: expand + dual write

**W IDE:** nowa klasa `BookingPayloadFormat` (wersjonowany format `v2|id=...|email=...`); `save` pisze obie kolumny, `find` nadal czyta csv.
**Po:**

```java
table.put(booking.id(), new BookingTable.Row(CsvBookingFormat.write(booking),
        BookingPayloadFormat.write(booking)));
```

**Uruchom:** `scripts/warsztat.sh test m8/s12` - `step1DualWriteKeepsRollbackToTheOldVersionSafe`: repozytorium ze startu czyta dane zapisane przez krok 1.
**Co powiedzieć:** wycofanie kodu jest bezpieczne, bo stary format wciąż powstaje.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m8/s12 0 1`

### Krok 2: odczyt nowego formatu z fallbackiem

**W IDE:** Extract Method `read(Row)`: payload, a gdy go brak - csv.
**Po:**

```java
return row.payload() != null
        ? BookingPayloadFormat.read(row.payload())
        : CsvBookingFormat.read(row.csv());
```

**Uruchom:** test zielony; `step2ReadsNewFormatAndFallsBackForOldRows`.
**Co powiedzieć:** stare wiersze (sprzed kroku 1) nadal są czytelne. Zapis dalej podwójny.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m8/s12 1 2`

### Krok 3: backfill

**W IDE:** metoda `migrateAll` uzupełnia payload w wierszach, które go nie mają; zwraca licznik.
**Po:**

```java
if (row.payload() == null) {
    table.put(id, new BookingTable.Row(row.csv(),
            BookingPayloadFormat.write(CsvBookingFormat.read(row.csv()))));
    migrated++;
}
```

**Uruchom:** test zielony; `step3BackfillIsIdempotentAndPreparesTheContract` - przed backfillem wersja po contract gubi stary wiersz, po backfillu go widzi; drugie uruchomienie migruje 0 wierszy.
**Co powiedzieć:** kolejność ma znaczenie: backfill przed contract. Licznik to dowód w logu wdrożenia.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m8/s12 2 3`

### Krok 4: contract - usunięcie starego formatu

**W IDE:** Safe Delete (⌘⌦) na `CsvBookingFormat`; `save` pisze tylko payload (`Row.withPayload`), `find` czyta tylko payload; `migrateAll` znika razem z fallbackiem.
**Po:**

```java
public Optional<Booking> find(String id) {
    return table.get(id).map(BookingTable.Row::payload).map(BookingPayloadFormat::read);
}
```

**Uruchom:** test zielony; `step4HasNoReferenceToTheOldFormat` (żaden plik kroku nie wspomina starego formatu) i `step4ClosesTheRollbackWindow` (stara wersja nie widzi nowych danych).
**Co powiedzieć:** migracja kończy się po usunięciu kosztu legacy. Od tego momentu wycofanie kodu już nie wystarczy - dlatego contract robimy dopiero po zamknięciu okna wycofania. Usunięcie kolumny to kolejna, osobna migracja schematu.
**Snapshot:** `step4/` · **Różnica:** `scripts/warsztat.sh diff m8/s12 3 4`

### Rozwiązanie i uzasadnienie

Każdy krok jest wdrażalny i odwracalny do poprzedniego, aż do contract. Test "brak referencji do starego kodu" zamienia "zamknięcie migracji" z deklaracji w sprawdzalny warunek.

### Pułapki

- Contract przed backfillem - utrata starych wierszy.
- Dual write bez strategii rekoncyliacji (co, jeśli zapis jednej kolumny się nie uda?).
- Flaga "czytaj nowy format" przełączona bez sprawdzenia, że stara wersja nadal czyta dane.

### Pytanie do sali

Jak długo trzymać okno wycofania dla danych i kto decyduje o jego zamknięciu?

## Scena s13. Dokumentacja żywa kontra historyczna

**Temat ze slajdów:** 4.1 Dokumentacja według trwałości; 4.5 Dokumentacja żywa i historyczna
**Pakiet:** `pl.training.workshop.m8.s13_livingdocs` · **Test:** `scripts/warsztat.sh test m8/s13`
**Czas:** ~8 min

### W skrócie

**Co robimy:** Ręcznie pisany `ROUTING.md` rozjechał się z `Routing.routes()`: twierdzi, że raport obsługuje legacy, i nie zna operacji `cancel`. Generujemy dokument z kodu przez `RoutingDoc`, pilnujemy go testem i dopisujemy właściciela oraz kryterium usunięcia każdej trasy.

**Zasada:** Dokumentacja żywa opisuje stan obecny, więc powinna powstawać z systemu albo być z nim sprawdzana, a historyczna (ADR, zamknięty opis zmian) jest niezmienna. Mieszanie tych ról kończy się dokumentem, który opisuje nieistniejący system albo gubi historię.

**Efekt:** Test porównuje zapisany `ROUTING.md` z wygenerowanym przy każdym buildzie, a każda trasa pokazuje właściciela i warunek usunięcia. Uzasadnienia decyzji nie wpisujemy do tego pliku, bo zniknęłyby przy regeneracji - ich miejsce jest w ADR.

### Co widzimy

`start.Routing.routes()` to routing fasady Strangler Fig jako kod - źródło prawdy. Obok leży ręcznie pisany `start/ROUTING.md`, który twierdzi, że raport obsługuje legacy, i nie zna operacji `cancel`. Test `startDocumentationHasDriftedFromCode` wypisuje rozjazd: `report: dokument mowi legacy, kod mowi new` i `cancel: brak w dokumencie`.

```text
| Operacja | Obsługuje |
| --- | --- |
| book | new |
| report | legacy |
```

### Krok 1: dokument generowany z kodu

**W IDE:** nowa klasa `RoutingDoc` z `render(routes)` i `main`, który zapisuje `ROUTING.md` obok kodu (ścieżka liczona z pakietu). Uruchom `main` (⌃⇧R) z katalogu repozytorium.
**Po:**

```java
for (Routing.Route route : routes) {
    md.append("| " + route.operation() + " | " + route.target() + " |").append('\n');
}
```

**Uruchom:** `scripts/warsztat.sh test m8/s13` - `step1DocumentIsGeneratedFromCode`: zapisany plik równa się wygenerowanemu.
**Co powiedzieć:** dokument żywy nie może się rozjechać, bo test porównuje go z kodem przy każdym buildzie.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m8/s13 0 1`

### Krok 2: architektura przejściowa w dokumencie

**W IDE:** ⌘F6 na rekordzie `Route`: pola `owner` i `removeWhen`; nowe kolumny w `render`; ponownie uruchom `main`.
**Po:**

```java
new Route("cancel", "legacy", "zespol Sprzedaz",
        "CancelModule w trybie CANDIDATE przez 14 dni"));
```

**Uruchom:** test zielony; `step2EveryTransitionalRouteHasOwnerAndRemovalCriterion`.
**Co powiedzieć:** żywa dokumentacja operacyjna mówi, co działa dziś i kiedy to zniknie. Dlaczego tak zdecydowaliśmy - to ADR (historia, scena s07).
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m8/s13 1 2`

### Rozwiązanie i uzasadnienie

Dokument żywy powstaje z kodu i jest sprawdzany testem, historyczny (ADR, zamknięty opis zestawu zmian) jest niezmienny. Mieszanie ról kończy się dokumentem, który opisuje nieistniejący system albo gubi historię.

### Pułapki

- Ręczna edycja wygenerowanego pliku.
- Generator bez testu zgodności - plik i tak się zestarzeje.
- Uzasadnienia decyzji wpisywane do dokumentu żywego (znikną przy następnej regeneracji).

### Pytanie do sali

Które dokumenty w waszym projekcie powinny być generowane z kodu, a które świadomie zamrożone?

## Proponowana kolejność pokazu

**Ścieżka krótka (~75 min):** s01 (Branch by Abstraction, 15) → s03 (shadow, 15) → s04 (granice shadow, 12) → s06 (seria zmian, 10) → s08 (bramka kompilatora, 10) → s12 (expand and contract, 15). Pokazuje pełny łuk: szew, weryfikacja, bezpieczeństwo efektów, przegląd, narzędzia i dane.

**Ścieżka pełna (~150 min):** s01 → s02 → s03 → s04 (strategie migracji) → s05 → s06 (Boy Scout i przegląd) → s07 → s13 (dokumentacja) → s08 → s09 → s10 (narzędzia i bramki) → s11 → s12 (ryzyko, wdrożenie i dane). Między blokami krótkie pytanie do sali ze scen.
