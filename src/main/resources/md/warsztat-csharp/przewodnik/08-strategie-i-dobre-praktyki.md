# Moduł 8. Strategie i dobre praktyki - warsztat CineLegacy (C#): przewodnik prowadzącego

Trzynaście małych scen pokazuje strategie z modułu 8 na kodzie kina CineLegacy: przyrostową wymianę implementacji (Branch by Abstraction, Strangler Fig, shadow), Boy Scout Rule, serię zmian gotowych do przeglądu, ADR i dokumentację żywą, bramki kompilatora i jakości, codemod na drzewie składni, wdrożenie etapowe oraz migrację danych expand and contract. Każda scena to katalog (namespace) `Start` (stan wyjściowy) i kompletne snapshoty `StepN` po każdym ruchu; test sceny uruchamiasz po każdym kroku.

Kod portu C# leży w `csharp/src/Training.Workshop/M8/SNN.../{Start,Step1,...}`, testy (xUnit) w `csharp/tests/Training.Workshop.Tests/M8/SNN.../`. Kwoty to `Money` (`decimal`, skala 2, zaokrąglenie `AwayFromZero`), `Optional<T>` to typ dopuszczający null (`Booking?`), a `sealed interface` z rekordami to interfejs z rekordami i `switch` z gałęzią `default` (C# nie sprawdza wyczerpania dla hierarchii interfejsów). Największe adaptacje dotyczą scen narzędziowych: bramka kompilatora (s08) kompiluje kod sceny w pamięci przez Roslyn, codemod (s09) korzysta z drzewa składni i modelu semantycznego Roslyn (`CSharpSyntaxRewriter`, `SemanticModel`), a bramka jakości (s10) kompiluje próbki w pamięci i sama uruchamia ich testy. Tam, gdzie scena odbiega od Javy, akapit **Różnica względem Javy** mówi, co i dlaczego wygląda inaczej.

## Jak prowadzić pokaz

```bash
scripts/warsztat.sh --lang cs list m8              # sceny i kroki modułu 8
scripts/warsztat.sh --lang cs test m8/s01          # testy jednej sceny
scripts/warsztat.sh --lang cs test m8              # wszystkie sceny modułu (237 testów)
scripts/warsztat.sh --lang cs diff m8/s03 1 2      # co zmienia krok 2 względem kroku 1 (0 = Start)
scripts/warsztat.sh --lang cs diff m8/s03 1 2 --word  # to samo, zmiany podświetlone w obrębie linii
scripts/warsztat.sh --lang cs jump m8/s03 2        # Start = Step2, gdy trzeba przeskoczyć krok
scripts/warsztat.sh --lang cs next m8/s03          # następny krok do Start + podsumowanie zmian
scripts/warsztat.sh next                           # kolejny krok tej samej sceny (język i scena zapamiętane)
scripts/warsztat.sh prev                           # krok wstecz
scripts/warsztat.sh status                         # który krok jest teraz w Start
scripts/warsztat.sh --lang cs reset m8/s03         # przywraca Start z repozytorium
```

- Zasada: **jeden krok - jeden test - jedno zdanie komentarza**. Zanim ruszysz kod, powiedz, jaki dowód da test po kroku.
- **Krok po kroku bez numerów:** `scripts/warsztat.sh --lang cs next m8/sNN` wstawia do `Start` gotowy następny krok i wypisuje, co się zmieniło. `prev` cofa o krok, `status` mówi, który krok jest teraz w `Start`. Skrypt pamięta język i ostatnią scenę, więc potem wystarczy samo `next`. Ręczne zmiany w `Start` zostają nadpisane - o to chodzi, gdy krok na żywo się rozjechał.
- W tym module część testów to nie tylko równoważność (`SNNEquivalenceTest`), ale też dowody strategii (`SNNSolutionTest`): raport rozbieżności, dziennik efektów ubocznych, wynik bramki, lista naruszeń ADR. Testy, które **dokumentują stan startowy** (np. "start wysyła dwa maile", "start narusza ADR"), zmienią kolor na czerwony, gdy wykonasz kroki na katalogu `Start` - to znak, że start jest już naprawiony. Po pokazie: `scripts/warsztat.sh --lang cs reset m8/sNN`.
- Sceny s07, s08, s10, s12 i s13 czytają pliki źródłowe sceny. Ścieżki wyznacza pomocnik testów `SourceFiles` (szuka `RefactoringLegacy.slnx` w górę od katalogu wyjściowego testów), więc testy działają niezależnie od katalogu, z którego je uruchamiasz - z `warsztat.sh`, z `dotnet test` i z Rider.
- Sceny s08 i s10 celowo zawierają kod z ostrzeżeniami kompilatora (`Start`/`Step1..2` w s08 i `Sample/Dirty` w s10) - to materiał dla bramek, nie niedoróbka. Build projektu ma `TreatWarningsAsErrors`, więc w s08 plik `M8/S08CompilerGate/.editorconfig` wyłącza te cztery identyfikatory ostrzeżeń tylko dla wariantów sceny, a próbki s10 są wyłączone z kompilacji projektu (`<Compile Remove>` w obu plikach `.csproj`) - bramki kompilują je same, w pamięci.
- Skróty w Rider (keymap macOS w stylu IntelliJ): ⇧F6 Rename, ⌥⌘M Extract Method, ⌥⌘V Introduce Variable, F6 Move, ⌘F6 Change Signature, ⌘⌦ Safe Delete, ⌥⌘T Surround With, ⌘N Generate (m.in. Delegating Members), ⌥⏎ akcje kontekstowe, ⌃T Refactor This (menu wszystkich refaktoryzacji dostępnych w miejscu kursora). Rider nie ma odpowiednika Type Migration z IntelliJ - zmianę typu (np. `double` na `Money`) robimy ręcznie, a listę miejsc do poprawy daje build.

## Mapa scen

| Scena | Temat ze slajdów | Kroki | Namespace | Czas |
| --- | --- | --- | --- | --- |
| s01 | 1.3 Branch by Abstraction | 4 | `M8.S01BranchByAbstraction` | ~15 min |
| s02 | 1.3-1.4 Strangler Fig, architektura przejściowa | 4 | `M8.S02StranglerFig` | ~12 min |
| s03 | 1.5 Równoległa weryfikacja (shadow) | 4 | `M8.S03ParallelRun` | ~15 min |
| s04 | 1.6-1.7 Granice trybu shadow | 3 | `M8.S04ShadowLimits` | ~12 min |
| s05 | 2.1-2.4 Boy Scout Rule i nadużycia | 2 | `M8.S05BoyScout` | ~8 min |
| s06 | 3.2 Małe zestawy zmian - czego nie łączyć | 3 | `M8.S06ReviewableSeries` | ~10 min |
| s07 | 4.3-4.4 ADR i wykonywalny model decyzji | 2 | `M8.S07Adr` | ~10 min |
| s08 | 5.2-5.3, 5.6 Bramka kompilatora | 3 | `M8.S08CompilerGate` | ~10 min |
| s09 | 5.4-5.5 Automatyczna transformacja (codemod) | 3 | `M8.S09Codemod` | ~15 min |
| s10 | 5.6-5.7 Minimalna bramka jakości | 4 | `M8.S10QualityGate` | ~12 min |
| s11 | 6.2-6.4 Jawna polityka wdrożenia etapowego | 3 | `M8.S11StagedRollout` | ~10 min |
| s12 | 6.5-6.6 Dane, wycofanie, zamknięcie migracji | 4 | `M8.S12ExpandContract` | ~15 min |
| s13 | 4.1, 4.5 Dokumentacja żywa i historyczna | 2 | `M8.S13LivingDocs` | ~8 min |

Pełne nazwy namespace zaczynają się od `Training.Workshop.`, katalogi to `csharp/src/Training.Workshop/M8/S01BranchByAbstraction` itd.

## Scena s01. Branch by Abstraction na cenniku z CinemaManager

**Temat ze slajdów:** 1.3-1.4 Branch by Abstraction, Strangler Fig, architektura przejściowa
**Namespace:** `Training.Workshop.M8.S01BranchByAbstraction` · **Test:** `scripts/warsztat.sh --lang cs test m8/s01`
**Czas:** ~15 min

### W skrócie

**Co robimy:** `BookingService.Confirm` ma wklejony stary cennik z `CinemaManager` (`double`, kody formatu, typy "S"/"E"/"C") i nie ma szwu, przez który dałoby się go wymienić. Izolujemy stary cennik w osobnej klasie, chowamy go za abstrakcją `ITicketPricing` zwracającą `Money`, dokładamy obok `ModernTicketPricing` z przełącznikiem, a na końcu usuwamy starą ścieżkę.

**Zasada:** Branch by Abstraction to "gałąź" zrobiona w kodzie, a nie w Git: stabilny kontrakt wewnątrz aplikacji, za nim stara i nowa implementacja, a każdy krok trafia od razu do głównej gałęzi. Wydzielenie abstrakcji i adaptera to jeszcze refaktoryzacja, a nowa implementacja i przełączenie to już migracja. Nie mylić ze Strangler Fig, który działa na granicy systemu, a nie wewnątrz aplikacji.

**Efekt:** `BookingService` zależy tylko od `ITicketPricing` i liczy w `Money`, a starego cennika i przełącznika `PricingMode` już nie ma - migracja jest zamknięta. Do pilnowania zostaje moment zaokrąglenia: nowy kod zaokrągla każdy procent osobno, a legacy dopiero sumę.

### Co widzimy

`Start.BookingService.Confirm` to kopia cennika z `CinemaManager.Book()`: `double`, kody formatu `1/2/3`, typy biletu `"S"/"E"/"C"` i formatowanie potwierdzenia w jednej metodzie. Chcemy wymienić cennik na nową implementację, ale nie ma szwu, a długa gałąź w Git na czas przepisywania to antywzorzec.

```csharp
double sum = 0;
for (int i = 0; i < request.Seats.Count; i++)
{
    double p = 0;
    int f = s.Format;
    if (f == 1)
    {
        p = 25.00;
    } ...
}
if (request.Seats.Count >= 10)
{
    sum = sum - sum * 0.10;
}
```

### Krok 1: Extract Method + Move - stary cennik w osobnej klasie

**W IDE:** zaznacz blok od `Screening s = ...` do obliczenia `total`, ⌥⌘M, nazwa `Total`, zwraca `double`. Następnie F6 (Move) do nowej klasy `LegacyTicketPricing`. W `BookingService` pole `_pricing`.
**Po:**

```csharp
double total = _pricing.Total(request);
return request.Screening.Title + ": " + string.Join(",", request.Seats)
    + " - do zaplaty " + total.ToString("F2", CultureInfo.InvariantCulture);
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m8/s01` - równoważność zielona dla wszystkich wariantów.
**Co powiedzieć:** starego kodu nie poprawiamy, tylko go izolujemy. Brzydki, ale ma jedno wejście i jedno wyjście.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s01 0 1`

### Krok 2: abstrakcja ITicketPricing, stary cennik za nią

**W IDE:** na `LegacyTicketPricing` ⌃T → Extract Interface, nazwa `ITicketPricing`. Zmień typ zwracany kontraktu na `Money` (⌘F6) i w implementacji zamień `double` na `Money` dopiero na granicy (`new Money((decimal)total)`). `BookingService` dostaje konstruktor z `ITicketPricing`.
**Po:**

```csharp
public interface ITicketPricing
{
    Money Total(BookingRequest request);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** abstrakcję projektujemy pod przyszłość (Money), a stary kod dostaje adapter. Do tego momentu to czysta refaktoryzacja - można ją zintegrować z główną gałęzią od razu.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s01 1 2`

### Krok 3: nowa implementacja obok i przełącznik

**W IDE:** nowa klasa `ModernTicketPricing : ITicketPricing` (Money, `switch` na formacie i typie, nazwane stałe). Enum `PricingMode { Legacy, Modern }` i konstruktor `BookingService(PricingMode)`; domyślnie `Legacy`.
**Po:**

```csharp
public BookingService(PricingMode mode)
    : this(mode switch
    {
        PricingMode.Legacy => new LegacyTicketPricing(),
        PricingMode.Modern => new ModernTicketPricing(),
        _ => throw new ArgumentOutOfRangeException(nameof(mode), mode, null),
    })
{
}
```

**Uruchom:** test zielony; `S01SolutionTest.LegacyAndModernPricingFulfilTheSameContract` porównuje obie implementacje na tych samych żądaniach.
**Co powiedzieć:** wdrożenie nowego kodu nie zmienia zachowania - zmienia je dopiero konfiguracja. To już migracja, a nie refaktoryzacja, więc przełącznik ma mieć właściciela i termin usunięcia.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s01 2 3`

**Różnica względem Javy:** `switch` po enumie w C# musi mieć ramię `_` (enum może przyjąć dowolną wartość `int`, a brak ramienia daje ostrzeżenie, które przy `TreatWarningsAsErrors` jest błędem). W Javie wyrażenie `switch` po enumie jest wyczerpujące bez `default`.

### Krok 4: usunięcie starej implementacji i przełącznika

**W IDE:** Safe Delete (⌘⌦) na `LegacyTicketPricing` i `PricingMode`; `BookingService()` tworzy `ModernTicketPricing`.
**Po:**

```csharp
public BookingService()
    : this(new ModernTicketPricing())
{
}
```

**Uruchom:** test zielony; `MigrationIsClosedOnlyWhenTheOldPathIsGone` sprawdza, że typów już nie ma (`typeof(...).Assembly.GetType(...)` zwraca `null`).
**Co powiedzieć:** migracja kończy się po usunięciu starej ścieżki. Abstrakcja może zostać jako szew dla testów.
**Snapshot:** `Step4/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s01 3 4`

### Rozwiązanie i uzasadnienie

Cztery kroki, każdy z zielonym buildem i możliwy do zintegrowania osobno. Gałąź istnieje w kodzie (abstrakcja + przełącznik), a nie w systemie kontroli wersji. Test równoważności obejmuje obie gałęzie przełącznika w kroku 3 (warianty `step3 LEGACY` i `step3 MODERN`).

### Pułapki

- Moment zaokrąglenia: legacy sumuje `double` i zaokrągla na końcu, nowy kod zaokrągla każdy procent. Dla kwot z "połową grosza" (np. 10% z 245.25) wyniki mogą się różnić - dopisz taki przypadek do testu kontraktu, zanim przełączysz.
- Abstrakcja skopiowana 1:1 ze starej sygnatury (`double Total(...)`) utrwala stary model.
- Przełącznik bez terminu usunięcia staje się nowym legacy.

### Pytanie do sali

Który z czterech kroków jest refaktoryzacją, a który migracją? Jakie dowody są potrzebne dla każdego z nich?

## Scena s02. Strangler Fig - fasada, która przejmuje ścieżki

**Temat ze slajdów:** 1.3-1.4 Branch by Abstraction, Strangler Fig, architektura przejściowa
**Namespace:** `Training.Workshop.M8.S02StranglerFig` · **Test:** `scripts/warsztat.sh --lang cs test m8/s02`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `LegacyCinema` jest wystawiony klientom wprost jako `ICinemaApi`, więc nie da się przenieść rezerwacji bez raportu. Stawiamy przed nim `CinemaFacade`, która najpierw deleguje 1:1, potem kieruje rezerwację do `BookingModule`, raport do `ReportModule`, a na końcu legacy znika.

**Zasada:** Strangler Fig to przejmowanie systemu operacja po operacji: brama, proxy lub fasada na granicy systemu kieruje część ruchu do nowego komponentu, a reszta dalej trafia do starego. Każdy element przejściowy, tu fasada i jej routing, potrzebuje właściciela i kryterium usunięcia. Fasada tylko kieruje ruch - nie powinna mieć własnej logiki biznesowej.

**Efekt:** Klienci rozmawiają z fasadą, która opisuje routing w `Routes()`, a `LegacyCinema` zostało usunięte po przejęciu obu ścieżek. Przez okres hybrydy obie strony musiały pisać do `BookingLedger` w tym samym formacie - zgodność danych to osobny kontrakt migracji.

### Co widzimy

`Start.LegacyCinema` implementuje publiczne API `ICinemaApi` (rezerwacja i raport) i jest wystawiony klientom wprost. Obie operacje korzystają ze wspólnej bazy `BookingLedger`. Nie da się przenieść jednej operacji bez drugiej.

```csharp
public sealed class LegacyCinema : ICinemaApi
{
    public string Book(string email, string title, int format, int tickets, bool web) { ... }
    public string Report() { ... }
}
```

### Krok 1: fasada 1:1

**W IDE:** nowa klasa `CinemaFacade : ICinemaApi` z polem `_legacy`; ⌘N → Delegating Members do `LegacyCinema` (albo ręcznie). Metoda `Routes()` opisuje, kto obsługuje którą operację.
**Po:**

```csharp
public string Book(string email, string title, int format, int tickets, bool web)
{
    return _legacy.Book(email, title, format, tickets, web);
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m8/s02` - scenariusz klienta (rezerwacje + raport) daje identyczny zapis.
**Co powiedzieć:** fasada nic nie zmienia, ale daje jedno miejsce przekierowania. Klienci od teraz rozmawiają z fasadą.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s02 0 1`

### Krok 2: przejęcie pierwszej ścieżki - rezerwacja

**W IDE:** nowa klasa `BookingModule` (Money, nazwane reguły), w fasadzie `Book` deleguje do niej; `Routes()` zwraca `book=new`.
**Po:**

```csharp
public string Book(string email, string title, int format, int tickets, bool web)
{
    return _bookings.Book(email, title, format, tickets, web);
}

public string Report()
{
    return _legacy.Report();
}
```

**Uruchom:** test zielony; `LegacyReportSeesBookingsTakenByTheNewModule` - raport legacy widzi rezerwacje nowego modułu, bo baza jest wspólna.
**Co powiedzieć:** przez chwilę system jest hybrydą. To działa tylko dlatego, że obie strony piszą w tym samym formacie danych - zgodność danych to osobny kontrakt.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s02 1 2`

### Krok 3: przejęcie kolejnej ścieżki - raport

**W IDE:** nowa klasa `ReportModule` z tym samym formatem wyjścia (sumy w Money); fasada kieruje `Report` do niej. `LegacyCinema` zostaje w kodzie, ale bez ruchu.
**Po:**

```csharp
public IReadOnlyDictionary<string, string> Routes()
{
    return new Dictionary<string, string> { ["book"] = "new", ["report"] = "new" };
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** stary kod zostaje na czas okna wycofania - powrót to jedna linia w fasadzie.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s02 2 3`

### Krok 4: usunięcie legacy

**W IDE:** Safe Delete (⌘⌦) na `LegacyCinema` - IDE potwierdzi brak użyć.
**Po:** fasada ma tylko `BookingModule` i `ReportModule`.
**Uruchom:** test zielony; `LegacyCinemaIsDeletedInTheLastStep`.
**Co powiedzieć:** kryterium usunięcia było znane od kroku 1 (brak ruchu do legacy + zamknięte okno wycofania).
**Snapshot:** `Step4/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s02 3 4`

### Rozwiązanie i uzasadnienie

Strangler Fig działa na granicy systemu (publiczne API), Branch by Abstraction (s01) wewnątrz aplikacji. Test równoważności uruchamia ten sam scenariusz klienta na każdym etapie hybrydy.

### Pułapki

- Nowy moduł z innym formatem danych niż legacy - raport legacy przestaje się zgadzać w kroku 2.
- Fasada, która zaczyna mieć własną logikę biznesową.
- Brak `Routes()` albo innego jawnego opisu routingu - po miesiącu nikt nie wie, co gdzie działa (patrz s13).

### Pytanie do sali

Którą operację przenieślibyście jako pierwszą w prawdziwym CinemaManager i dlaczego: najprostszą, najczęściej zmienianą czy najbardziej ryzykowną?

## Scena s03. Równoległa weryfikacja (shadow) kalkulatora cen

**Temat ze slajdów:** 1.5 Przykład: równoległa weryfikacja kalkulatora
**Namespace:** `Training.Workshop.M8.S03ParallelRun` · **Test:** `scripts/warsztat.sh --lang cs test m8/s03`
**Czas:** ~15 min

### W skrócie

**Co robimy:** `PriceService` woła tylko stary kalkulator w `double`, a gotowy `CandidatePriceCalculator` nigdy nie był porównany z produkcją. Uruchamiamy kandydata w cieniu, zbieramy typowany raport rozbieżności, poprawiamy znaleziony błąd i wprowadzamy jawny tryb `MigrationMode`.

**Zasada:** Równoległa weryfikacja (shadow) liczy wynik obiema implementacjami na prawdziwym ruchu, ale klient dostaje wynik legacy, a awaria kandydata jest izolowana. Nadaje się dla czystych obliczeń bez efektów ubocznych. Zgodność obu implementacji nie dowodzi poprawności - obie mogą mieć ten sam błąd.

**Efekt:** Raport wskazał złą kolejność rabatu porannego i zniżki procentowej, a po poprawce zostaje tylko awaria dla 4DX. W trybie `Candidate` zachowanie dla 4DX świadomie się zmienia (odrzucenie zamiast wyceny 0.00), a `Legacy` służy za natychmiastowe wycofanie, dopóki stary kod istnieje.

**Różnica względem Javy:** `sealed interface Verification` to w porcie interfejs `VerificationReport.IVerification` z trzema rekordami, a `switch` w `Problems()` ma gałąź `default` (C# nie sprawdza wyczerpania dla hierarchii interfejsów). Godzina seansu to `TimeOnly`, wypisywana jako `HH:mm`, a w raporcie awarii kandydata jest nazwa wyjątku .NET: `ArgumentException` zamiast `IllegalArgumentException`.

### Co widzimy

`Start.PriceService` woła tylko `LegacyPriceCalculator` (double). Obok leży `CandidatePriceCalculator` (Money), napisany przez zespół, ale nigdy nieporównany z produkcją. Jedyne opcje: "włączyć i zobaczyć" albo wieczne testy ręczne.

```csharp
public Money Price(TicketQuery query)
{
    return _legacy.Price(query);
}
```

### Krok 1: dodanie cienia

**W IDE:** w `Price` po wyliczeniu wyniku legacy wywołaj kandydata w `try/catch` (Surround With ⌥⌘T) i licz niezgodności.
**Po:**

```csharp
Money result = _legacy.Price(query);
try
{
    if (!_candidate.Price(query).Equals(result))
    {
        _mismatches++;
    }
}
catch (Exception)
{
    _mismatches++;
}
return result;
```

**Uruchom:** test zielony; `Step1ShadowSurvivesCandidateFailureButOnlyCountsMismatches` - wyjątek kandydata dla formatu 4DX nie przerywa sprzedaży.
**Co powiedzieć:** klient zawsze dostaje wynik legacy. Ale licznik mówi tylko, ŻE coś się różni - nie wiemy co.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s03 0 1`

### Krok 2: raport rozbieżności

**W IDE:** nowa klasa `VerificationReport` z interfejsem `IVerification` i rekordami `Agreement`, `Divergence`, `CandidateFailure`. Extract Method (⌥⌘M) `Verify` w `PriceService`.
**Po:**

```csharp
return legacyPrice.Equals(candidatePrice)
    ? new VerificationReport.Agreement(query, legacyPrice)
    : new VerificationReport.Divergence(query, legacyPrice, candidatePrice);
```

**Uruchom:** test zielony; `Step2ReportShowsWhatDivergedAndWhy` - raport pokazuje dwie rozbieżności (3D dziecko rano: 17.20 vs 19.20, 2D student rano: 13.75 vs 15.00) i awarię dla 4DX (`BLAD KANDYDATA 4DX NORMAL 20:00 rzad 1: ArgumentException: Nieznany format: 4DX`).
**Co powiedzieć:** z raportu widać wzorzec: rozbieżności tylko rano i tylko przy zniżce procentowej. Kandydat odejmuje rabat poranny przed procentem.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s03 1 2`

### Krok 3: poprawka znaleziona przez raport

**W IDE:** w `CandidatePriceCalculator` przenieś blok rabatu porannego (⇧⌘↓, Move Statement Down) za wyliczenie zniżki procentowej.
**Po:**

```csharp
Money price = @base.Minus(@base.Percent(DiscountPercent(query.Type)));
if (query.Start.Hour < 12)
{
    price = price.Minus(MorningDiscount);
}
```

**Uruchom:** test zielony; w raporcie zostaje tylko awaria 4DX.
**Co powiedzieć:** odrzucenie nieznanego formatu to świadoma decyzja (legacy wyceniało 4DX na 0.00!), a nie błąd. Zapisujemy ją jako zaakceptowaną różnicę.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s03 2 3`

### Krok 4: przełączenie

**W IDE:** enum `MigrationMode { Legacy, Shadow, Candidate }`, konstruktor `PriceService(MigrationMode, VerificationReport)`, wyrażenie `switch` w `Price`.
**Po:**

```csharp
return _mode switch
{
    MigrationMode.Legacy => _legacy.Price(query),
    MigrationMode.Shadow => Shadow(query),
    MigrationMode.Candidate => _candidate.Price(query),
    _ => throw new InvalidOperationException("nieznany tryb: " + _mode),
};
```

**Uruchom:** test zielony; równoważność obejmuje wszystkie trzy tryby, `Step4CandidateModeIsAuthoritativeAndRejectsUnknownFormat` pokazuje świadomą zmianę dla 4DX.
**Co powiedzieć:** tryb zamiast flagi `bool`, wybór w jednym miejscu. `Legacy` to natychmiastowe wycofanie, dopóki nie usuniemy starego kodu.
**Snapshot:** `Step4/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s03 3 4`

### Rozwiązanie i uzasadnienie

Shadow daje dowód na prawdziwym ruchu, zanim nowa implementacja stanie się autorytatywna. Typowane zdarzenia raportu pozwalają podjąć decyzję bez parsowania logów.

### Pułapki

- Brak izolacji awarii kandydata - wyjątek w cieniu zatrzymuje sprzedaż.
- Zgodność obu implementacji nie dowodzi poprawności: obie mogą mieć ten sam błąd (np. wycena 4DX na 0.00 w legacy).
- Produkcyjny cień kosztuje: latencja, timeout, limit ruchu.

### Pytanie do sali

Ile dni ruchu i ile zgodnych porównań wystarczy, żeby przełączyć tryb na `Candidate`? Kto o tym decyduje?

## Scena s04. Granice trybu shadow - efekty uboczne

**Temat ze slajdów:** 1.6-1.7 Granice trybu shadow i antywzorce migracji
**Namespace:** `Training.Workshop.M8.S04ShadowLimits` · **Test:** `scripts/warsztat.sh --lang cs test m8/s04`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `ShadowBooking` stosuje shadow do rezerwacji z efektami ubocznymi, więc klient dostaje dwa maile i dwa obciążenia karty, a cień niczego nie zgłasza. Wprowadzamy port `IEffects`, w cieniu nagrywamy efekty zamiast je wykonywać, a na końcu wydzielamy czysty `BookingPlanner`, który zwraca plan jako dane.

**Zasada:** Podwójne wykonanie w trybie shadow jest bezpieczne tylko dla czystych obliczeń - płatności, maili, zapisów ani zdarzeń nie wolno powielać. Efekty zamieniamy w dane (Separate Query from Modifier): opis "co zrobić" można porównać, a wykonuje się go tylko raz. Zgodność zwracanego wyniku nie dowodzi zgodności efektów.

**Efekt:** Po rezerwacji klient dostaje jeden mail i jedno obciążenie, a cień porównuje zaplanowane efekty z efektami legacy i wyłapuje błędną kwotę w mailu kandydata. Bezpieczeństwo gwarantuje typ, bo `BookingPlanner` nie ma dostępu do portu efektów; kosztem jest rozdzielenie planu od wykonania i dodatkowy typ `BookingPlan`.

### Co widzimy

`Start.ShadowBooking` stosuje shadow "jak dla kalkulatora" do rezerwacji z efektami ubocznymi. `NewBookingFlow` sam obciąża kartę, zapisuje wiersz i wysyła mail. Test pokazuje: klient dostaje **dwa maile i dwa obciążenia**, a cień niczego nie zgłasza, bo porównuje tylko zwracany tekst.

```csharp
string result = _legacy.Book(request);
try
{
    string shadow = _candidate.Book(request); // też obciąża kartę i wysyła mail
```

### Krok 1: port efektów (Parameterize Constructor)

**W IDE:** ⌃T → Extract Interface na operacjach `Infrastructure` używanych przez nowy przepływ → `IEffects`; adapter `RealEffects`. ⌘F6 na konstruktorze `NewBookingFlow`: parametr `IEffects` zamiast `Infrastructure`.
**Po:**

```csharp
_candidate = new NewBookingFlow(new RealEffects(infra));
```

**Uruchom:** test zielony; `Step1PreparatoryRefactoringKeepsTheBugOnPurpose` - nadal dwa maile.
**Co powiedzieć:** refaktoryzacja przygotowawcza zachowuje zachowanie, także błędne. Mamy szew, który w następnym kroku zmieni wszystko.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s04 0 1`

### Krok 2: przechwycenie efektów w cieniu

**W IDE:** nowa klasa `RecordingEffects : IEffects` zapisuje zamiary w formacie dziennika. `ShadowBooking` przekazuje kandydatowi nagrywarkę i porównuje nagrane efekty z efektami, które faktycznie wykonało legacy.
**Po:**

```csharp
var recorder = new RecordingEffects();
string shadow = new NewBookingFlow(recorder).Book(request);
Compare("wynik", [result], [shadow]);
Compare("efekty", legacyEffects, recorder.Recorded());
```

**Uruchom:** test zielony; `Step2RecordsCandidateEffectsInsteadOfExecutingThem` - jeden mail, jedno obciążenie, a raport pokazuje rozbieżność w treści maila (kandydat pisze 50.00 zamiast 54.00).
**Co powiedzieć:** porównanie efektów znalazło błąd, którego porównanie wyniku nie widziało.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s04 1 2`

### Krok 3: Separate Query from Modifier - czysty plan

**W IDE:** z `NewBookingFlow.Book` wydziel (⌥⌘M, potem Move F6) klasę `BookingPlanner` z metodą `Plan`, która zwraca `BookingPlan(Result, Effects)`. `NewBookingFlow` wykonuje plan na porcie; `ShadowBooking` woła tylko `BookingPlanner`. `RecordingEffects` usuń (Safe Delete).
**Po:**

```csharp
BookingPlan plan = _candidate.Plan(request);
Compare("wynik", [result], [plan.Result]);
```

**Uruchom:** test zielony; `Step3ComparesAPurePlanAndNeverTouchesInfrastructure`.
**Co powiedzieć:** typ gwarantuje bezpieczeństwo: `BookingPlanner` nie ma dostępu do portu efektów, więc w cieniu nie da się go użyć "za mocno".
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s04 2 3`

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
**Namespace:** `Training.Workshop.M8.S05BoyScout` · **Test:** `scripts/warsztat.sh --lang cs test m8/s05`
**Czas:** ~8 min

### W skrócie

**Co robimy:** `TicketPrinter.Print` i tak otwieramy dla nowej linii "Sala", a kod ma nazwy `s`, `d`, `x` i ręczne sklejanie listy miejsc. Najpierw pokazujemy antyprzykład "skoro już tu jestem", który po cichu zmienia wydruk, a potem robimy małą poprawę tylko w tej metodzie.

**Zasada:** Boy Scout Rule mówi: dotykany kod zostaw w nieco lepszym stanie - to heurystyka, nie licencja na przebudowę. Poprawa jest mała, lokalna, dotyczy kodu bieżącej zmiany i nie zmienia zachowania; zmiana reguły biznesowej pod etykietą sprzątania to nadużycie.

**Efekt:** Metoda ma czytelne nazwy, `string.Join`, `StringBuilder` i `PhoneLine`, a test równoważności potwierdza identyczny wydruk. Sortowanie miejsc czy małe litery w e-mailu mogą być dobrymi pomysłami, ale trafiają do osobnego commita jako świadoma zmiana zachowania.

**Różnica względem Javy:** data i godzina seansu są w wydruku formatowane jawnie (`yyyy-MM-dd` i `HH:mm` z `CultureInfo.InvariantCulture`) - to odpowiednik `LocalDate.toString()`/`LocalTime.toString()`, który w C# nie istnieje bez podania formatu. Antyprzykład sortuje miejsca porządkiem ordinalnym (`Order(StringComparer.Ordinal)`), tak jak `sorted()` w Javie, więc `A9, A10` staje się `A10, A9`.

### Co widzimy

`Start.TicketPrinter.Print` trzeba i tak otworzyć, bo w sprincie dochodzi linia "Sala". Kod ma nazwy `s`, `d`, `x`, konkatenację w pętli i ręczne sklejanie listy miejsc. Kusi, żeby "posprzątać wszystko".

```csharp
string x = "";
for (int i = 0; i < t.Seats.Count; i++)
{
    if (i > 0)
    {
        x = x + ", ";
    }
    x = x + t.Seats[i];
}
```

### Krok 1: nadużycie - "skoro już tu jestem"

**W IDE:** pokaż antyprzykład: `scripts/warsztat.sh --lang cs jump m8/s05 1`. Obok dobrych ruchów przemycono sortowanie miejsc, e-mail małymi literami i pominięcie linii "Tel" bez telefonu.
**Po:**

```csharp
IReadOnlyList<string> sortedSeats = ticket.Seats.Order(StringComparer.Ordinal).ToList();
text.Append("Klient: ").Append(ticket.Email.Trim().ToLowerInvariant()).Append('\n');
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m8/s05` - równoważność czerwona w trzech przypadkach (m.in. `A9, A10` staje się `A10, A9`). `S05SolutionTest.AbusiveCleanupChangesBehaviourInThreeCases` dokumentuje, które.
**Co powiedzieć:** każda z tych zmian może być dobrym pomysłem, ale to decyzje biznesowe, nie sprzątanie. Cofamy: `scripts/warsztat.sh --lang cs reset m8/s05`.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s05 0 1`

### Krok 2: poprawna, mała poprawa

**W IDE:** Rename (⇧F6) `t` → `ticket`; pętlę zamień ręcznie na `string.Join`; `StringBuilder` zamiast konkatenacji; Extract Method (⌥⌘M) `PhoneLine`.
**Po:**

```csharp
.Append("Miejsca: ").Append(string.Join(", ", ticket.Seats)).Append('\n')
.Append("Klient: ").Append(ticket.Email.Trim()).Append('\n')
.Append(PhoneLine(ticket)).Append('\n')
```

**Uruchom:** test zielony.
**Co powiedzieć:** tylko dotykana metoda, bez zmiany kontraktu, diff do przejrzenia w minutę. Nowa linia "Sala" to osobny commit (patrz s06).
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s05 0 2`

### Rozwiązanie i uzasadnienie

Boy Scout Rule to heurystyka: mała, lokalna, weryfikowalna poprawa w kodzie bieżącej zmiany. Test różnicowy (start kontra krok) jest bezstronnym arbitrem, czy "sprzątanie" nie zmieniło zachowania.

### Pułapki

- Zmiana zachowania pod etykietą refaktoryzacji.
- Masowe formatowanie całego pliku (np. Reformat and Cleanup Code na całym projekcie) razem z poprawką - recenzent nie znajdzie zmiany merytorycznej.
- "Sprzątanie" cudzego modułu bez uzgodnienia.

### Pytanie do sali

Posortowane miejsca są czytelniejsze. Jak wprowadzić tę zmianę uczciwie?

## Scena s06. Seria małych zmian gotowych do przeglądu

**Temat ze slajdów:** 3.2 Małe zestawy zmian - czego nie łączyć; 3.3-3.4 Przegląd kodu
**Namespace:** `Training.Workshop.M8.S06ReviewableSeries` · **Test:** `scripts/warsztat.sh --lang cs test m8/s06`
**Czas:** ~10 min

### W skrócie

**Co robimy:** `PriceList.Price` ma dostać "Tani wtorek" (NORMAL -20% we wtorek), a zniżki są wplecione w jedną metodę i nie widzą daty. Zamiast jednego commita "porządki + tani wtorek" robimy serię: Extract Method, Change Signature, a dopiero potem nowa reguła.

**Zasada:** Dobry zestaw zmian ma jedną intencję, własny dowód i zielony build po integracji. Refaktoryzację oddziela się od zmiany funkcjonalnej, formatowanie od logiki, a diff automatyczny od ręcznego - najpierw ułatw zmianę, potem zrób łatwą zmianę.

**Efekt:** Dwa pierwsze commity dowodzi test równoważności, a trzeci świadomie zmienia zachowanie: test różnicowy pokazuje, że zmieniło się dokładnie 12 przypadków, wszystkie NORMAL we wtorek. Kosztem jest więcej commitów, ale recenzent sprawdza w każdym tylko jedną rzecz.

### Co widzimy

`Start.PriceList.Price` ma dostać promocję "Tani wtorek": bilet NORMAL -20% ceny bazowej we wtorek. Zniżki są wplecione w jedną metodę i zależą tylko od typu biletu, a nowa reguła potrzebuje daty. Pokusa: jeden commit "porządki + tani wtorek".

```csharp
decimal d;
if (q.Type.Equals("STUDENT"))
{
    d = p * 0.25m;
}
else if ...
```

### Krok 1: commit 1 - refaktoryzacja (Extract Method)

**W IDE:** ⌥⌘M na wyborze ceny bazowej → `BasePrice(string format)`; ⌥⌘M na wyborze zniżki → `DiscountPercent(string type)` zwracające procent; Money zamiast `decimal`.
**Po:**

```csharp
Money @base = BasePrice(query.Format);
Money price = @base.Minus(@base.Percent(DiscountPercent(query.Type)));
```

**Uruchom:** test zielony - równoważność na przypadkach, w tym wtorkowych.
**Co powiedzieć:** opis commita: "refaktoryzacja, bez zmiany zachowania, dowód: S06EquivalenceTest". Recenzent sprawdza tylko mechanikę.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s06 0 1`

### Krok 2: commit 2 - refaktoryzacja przygotowawcza (Change Signature)

**W IDE:** ⌘F6 na `DiscountPercent`: parametr `TicketQuery query` zamiast `string type`, w ciele `query.Type switch`.
**Po:**

```csharp
private static int DiscountPercent(TicketQuery query) => query.Type switch
{
    ...
};
```

**Uruchom:** test zielony.
**Co powiedzieć:** "make the change easy, then make the easy change". Ten commit nadal nie zmienia zachowania.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s06 1 2`

### Krok 3: commit 3 - zmiana zachowania

**W IDE:** w ramieniu `_` warunek `IsCheapTuesday(query) ? CheapTuesdayPercent : 0`, metoda pomocnicza, stała `CheapTuesdayPercent`.
**Po:**

```csharp
_ => IsCheapTuesday(query) ? CheapTuesdayPercent : 0,
```

**Uruchom:** test zielony; `S06SolutionTest.CheapTuesdayGivesNormalTicketTwentyPercentOff` (2D wtorek 20.00) i `BehaviourChangeIsLimitedToNormalTicketsOnTuesday` - na siatce 336 przypadków zmieniło się dokładnie 12, wszystkie NORMAL we wtorek.
**Co powiedzieć:** diff to kilka linii. Test różnicowy commitu 2 kontra 3 mówi recenzentowi dokładnie, gdzie zmieniło się zachowanie.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s06 2 3`

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
**Namespace:** `Training.Workshop.M8.S07Adr` · **Test:** `scripts/warsztat.sh --lang cs test m8/s07`
**Czas:** ~10 min

### W skrócie

**Co robimy:** ADR-0007 mówi, że cennik nie zależy od `Notification` i nie używa `double`, a cennik w `Start` sam wysyła mail o rabacie grupowym i liczy w `double`. Cennik zaczyna zwracać `Quote` z informacją o rabacie, mail wysyła `BookingService`, a kwoty przechodzą na `Money`.

**Zasada:** ADR zapisuje decyzję o trwałym wpływie: kontekst, opcje, decyzję i konsekwencje - to dokument historyczny, którego się nie edytuje, tylko zastępuje nowym. Regułę z ADR warto uczynić wykonywalną, żeby build pilnował jej na co dzień. Nie pisze się ADR dla każdego Rename.

**Efekt:** `ArchitectureRules` zwraca pustą listę naruszeń, a maile i odpowiedzi są takie same jak przed zmianą. Reguła tekstowa ma swoje granice (pełna nazwa typu bez dyrektywy `using` albo refleksja ją ominą) i ADR opisuje to w konsekwencjach.

**Różnica względem Javy:** pakiety `pricing`/`notification` to w porcie podprzestrzenie nazw i katalogi `Pricing`/`Notification`. Reguła R1 szuka dyrektywy `using` (regex `^using .*\.Notification\b` zamiast `import ...notification.`), a ponieważ w C# dyrektywa `using` stoi w pierwszej linii pliku, naruszenie startu to `ADR-0007/R1 TicketPricing.cs:1`. Enum `Rule` z polami to klasa `ArchitectureRules.Rule` z instancjami statycznymi i listą `Values`. ADR mówi o przestrzeni nazw zamiast pakietu i o osobnym projekcie (assembly) zamiast modułu Maven.

### Co widzimy

Obok kodu sceny leży `ADR-0007-cennik-jako-czysty-modul.md` (status: Zaakceptowana) z dwiema regułami: **R1** przestrzeń nazw `Pricing` nie zależy od `Notification`, **R2** `Pricing` nie używa `double`. `ArchitectureRules` to wykonywalny model tych reguł. W `Start` cennik sam wysyła mail o rabacie grupowym i liczy w `double`:

```csharp
using Training.Workshop.M8.S07Adr.Start.Notification;
...
public double Total(string organizer, int tickets, double unitPrice)
{
    ...
    _mailer.GroupDiscountGranted(organizer, tickets);
```

### Krok 1: spełnienie R1 - wynik zamiast efektu

**W IDE:** nowy rekord `Quote(double Total, bool GroupDiscount)` w `Pricing`; ⌘F6 na `Total` - bez `organizer`, zwraca `Quote`. Wywołanie `_mailer` przenieś do `BookingService` (warstwa aplikacji). Usuń dyrektywę `using ...Notification` z cennika.
**Po:**

```csharp
Quote quote = _pricing.Total(tickets, unitPrice);
if (quote.GroupDiscount)
{
    _mailer.GroupDiscountGranted(organizer, tickets);
}
```

**Uruchom:** test zielony; `Step1RemovesDependencyOnNotification` - zostało tylko naruszenie `ADR-0007/R2`.
**Co powiedzieć:** naruszenie w teście prowadzi wprost do identyfikatora decyzji i jej uzasadnienia.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s07 0 1`

### Krok 2: spełnienie R2 - Money

**W IDE:** ręcznie zmień typ `double` → `Money` w `Quote` i `TicketPricing` (Rider nie ma Type Migration - build wskaże resztę miejsc); nazwane stałe dla progu i procentu grupy; `BookingService` wybiera cenę jednostkową jako `Money` (`Money.Of("25.00")` itd.).
**Po:**

```csharp
public Quote Total(int tickets, Money unitPrice)
{
    Money sum = unitPrice.Times(tickets);
```

**Uruchom:** test zielony; `Step2CompliesWithTheDecision` - pusta lista naruszeń.
**Co powiedzieć:** moduł jest zgodny z decyzją, a test pilnuje, żeby tak zostało.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s07 1 2`

### Rozwiązanie i uzasadnienie

ADR opisuje kontekst, opcje i konsekwencje (historia), a `ArchitectureRules` sprawdza regułę w każdym buildzie. `AdrDocumentsEveryExecutableRule` pilnuje, żeby każda reguła w kodzie miała opis w ADR.

### Pułapki

- ADR dla każdego Rename - dokumentuje się decyzje o trwałym wpływie.
- Edycja zaakceptowanego ADR zamiast nowego zapisu, który go zastępuje.
- Reguła tekstowa ma granice: pełna nazwa typu bez dyrektywy `using` (`Training.Workshop...Notification.GroupMailer`) albo refleksja ją ominą (opisane w "Konsekwencjach").

### Pytanie do sali

Co zrobić, gdy zespół chce złamać R2 dla jednego przypadku (np. statystyk)? Wyjątek w teście czy nowy ADR?

## Scena s08. Bramka kompilatora - nullable i ostrzeżenia jako błędy

**Temat ze slajdów:** 5.2-5.3 Refaktoryzacje IDE i kompilator (w Javie: kompilator Javy 25); 5.6 Bramka kompilatora
**Namespace:** `Training.Workshop.M8.S08CompilerGate` · **Test:** `scripts/warsztat.sh --lang cs test m8/s08`
**Czas:** ~10 min

### W skrócie

**Co robimy:** Kod `Start` nie przechodzi bramki Roslyn z włączonym nullable, wszystkimi falami ostrzeżeń i ostrzeżeniami jako błędami: kolekcje niegeneryczne z rzutowaniami w `SeatMap`, przestarzałe `PriceTable.BasePrice(int)` i celowy przelot w `switch` zapisany przez `goto case`. Usuwamy ostrzeżenia po jednej przyczynie na krok: typy generyczne, nowe przeciążenie, switch expression.

**Zasada:** Bramka kompilatora traktuje ostrzeżenia jak błędy i zwraca strukturalną diagnostykę, więc test może wymagać zera ostrzeżeń albo braku nowych. W dużym legacy nie włącza się jej nagle: najpierw stan bazowy, potem "brak nowych naruszeń" i stopniowa redukcja, a tłumienie tylko wąskie i uzasadnione.

**Efekt:** Bramka przechodzi z zerem ostrzeżeń, a raport działa tak samo, z intencją "IMAX ma też Dolby" zapisaną wprost w `switch`. Przestarzała metoda zostaje w `PriceTable`, dopóki ktoś może ją wołać spoza repozytorium.

**Różnica względem Javy:** zamiast `javax.tools` z `--release 25 -Xlint:all -Werror` bramka kompiluje pliki wariantu w pamięci przez Roslyn (`CSharpCompilation`): `NullableContextOptions.Enable`, `warningLevel: 9999` (wszystkie fale ostrzeżeń, odpowiednik `-Xlint:all`) i `ReportDiagnostic.Error` (ostrzeżenia jako błędy, odpowiednik `-Werror`), z tymi samymi globalnymi `using` co `ImplicitUsings` w projekcie. Kategorią ostrzeżenia jest identyfikator Roslyn (`CS8602`), a nie nazwa kategorii `-Xlint`. Zapachy są przełożone na C#: surowe typy i `unchecked` to niegeneryczne `Hashtable`/`ArrayList` z rzutowaniami (CS8600, CS8602), `deprecation` to `[Obsolete]` (CS0618), a ponieważ C# zabrania niejawnego przelotu między `case`, zamierzony przelot jest zapisany jako `goto case 2;`, za którym został martwy `break;` (CS0162, kod nieosiągalny).

### Co widzimy

`CompilerGate.Check` kompiluje wszystkie pliki wariantu i zwraca `Result(Passed, Warnings)` ze strukturalną diagnostyką (identyfikator, plik, linia). Kod `Start` nie przechodzi: `SeatMap` na `Hashtable` i `ArrayList` z rzutowaniami (CS8600, CS8602), `OccupancyReport` woła przestarzałe `PriceTable.BasePrice(int)` (CS0618) i celowo przelatuje z IMAX do 3D przez `goto case`, zostawiając nieosiągalny `break` (CS0162). Test startu: kategorie `["CS0162", "CS0618", "CS8600", "CS8602"]`.

```csharp
private readonly Hashtable _seatsByRow = new();
...
ArrayList seats = (ArrayList)_seatsByRow[row];
...
case 3:
    features = features + "duzy ekran, ";
    goto case 2;
    break;
case 2:
    features = features + "dzwiek Dolby";
    break;
```

Build całego projektu ma `TreatWarningsAsErrors`, więc plik `M8/S08CompilerGate/.editorconfig` wyłącza te cztery identyfikatory tylko dla `Start/` i `Step1..3/` (odpowiednik tego, że Maven nie kompiluje tej sceny z `-Werror`). Plik leży na poziomie sceny, bo `jump`/`reset` kasują i odtwarzają katalog `Start`. `CompilerGate` go nie czyta - bramką jest test sceny.

### Krok 1: typy generyczne

**W IDE:** ręcznie zmień pole `_seatsByRow` na `SortedDictionary<int, List<string>>`, `TryGetValue` zamiast indeksu z rzutowaniem, usuń rzutowania i `using System.Collections`.
**Po:**

```csharp
private readonly SortedDictionary<int, List<string>> _seatsByRow = new();
...
if (!_seatsByRow.TryGetValue(row, out List<string>? seats))
{
    seats = [];
    _seatsByRow[row] = seats;
}
seats.Add(seat);
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m8/s08` - zostają kategorie `CS0162`, `CS0618`.
**Co powiedzieć:** jedna przyczyna ostrzeżeń na krok - łatwy przegląd, łatwe wycofanie. Znane typy elementów to także koniec rzutowań, na których analiza nullable nie mogła nic udowodnić.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s08 0 1`

### Krok 2: nowe API zamiast przestarzałego

**W IDE:** w `return` zamień argument `format` na istniejącą już zmienną `name` - wywołanie trafia do `PriceTable.BasePrice(string)`. ⌘klik (Go to Declaration) na metodzie potwierdza, że to nowe przeciążenie.
**Po:**

```csharp
return name + " [" + features + "], cena " + PriceTable.BasePrice(name)
    + " zl, zajete: " + Show(map.TakenPerRow());
```

**Uruchom:** test zielony; zostaje tylko `[CS0162] OccupancyReport.cs:17` (`WarningsPointToFileAndLine`).
**Co powiedzieć:** przestarzałą metodę usuniemy (Safe Delete) dopiero, gdy nikt jej nie woła - także poza repozytorium.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s08 1 2`

### Krok 3: switch expression

**W IDE:** zamień instrukcję `switch` na wyrażenie `switch` (⌥⏎ na `switch` może zaproponować konwersję; `goto case` zwykle ją blokuje, wtedy ręcznie); przelot zapisany wprost: `3 => "duzy ekran, dzwiek Dolby"`.
**Po:**

```csharp
string features = format switch
{
    3 => "duzy ekran, dzwiek Dolby",
    2 => "dzwiek Dolby",
    _ => "standard",
};
```

**Uruchom:** test zielony; `Step3PassesTheGate` - zero ostrzeżeń, bramka z ostrzeżeniami jako błędami przechodzi.
**Co powiedzieć:** intencja "IMAX ma też Dolby" jest teraz w kodzie, a nie w skoku `goto case` i kolejności `case`.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s08 2 3`

### Rozwiązanie i uzasadnienie

Bramka zwraca strukturalne wyniki, więc test może wymagać "zero ostrzeżeń" albo "nie więcej niż stan bazowy". Ciekawostka z implementacji: javac z `-Werror` przerywa po fazie z pierwszym ostrzeżeniem i wersja Java potrzebuje drugiego przebiegu, żeby zebrać pełną listę. Roslyn z ostrzeżeniami jako błędami raportuje wszystko w jednym przebiegu (`Emit` daje i werdykt, i pełną diagnostykę), więc `CompilerGate` kompiluje raz.

### Pułapki

- Nagłe włączenie `TreatWarningsAsErrors` w dużym legacy blokuje zespół - najpierw stan bazowy i "brak nowych naruszeń".
- `<NoWarn>` w `.csproj` albo `#pragma warning disable` bez numeru na całym pliku zamiast wąskiego wyłączenia z uzasadnieniem (tu: `.editorconfig` tylko dla wariantów sceny, z komentarzem dlaczego).
- `TargetFramework` i `LangVersion` w `.csproj` nie kontrolują, którym SDK buduje CI - wersję SDK przypina `global.json`.

### Pytanie do sali

Jak wprowadzić tę bramkę w projekcie z 3000 ostrzeżeń, nie blokując nikogo od jutra?

## Scena s09. Codemod na API kompilatora Roslyn

**Temat ze slajdów:** 5.4-5.5 Analiza statyczna, formatowanie i automatyzacja (receptury)
**Namespace:** `Training.Workshop.M8.S09Codemod` · **Test:** `scripts/warsztat.sh --lang cs test m8/s09`
**Czas:** ~15 min

### W skrócie

**Co robimy:** `BookCallCodemod` migruje wywołania przestarzałego `Book(..., bool, bool)` wyrażeniem regularnym, które trafia w komentarz i w `HotelService`, a gubi wywołanie rozbite na trzy linie. Zastępujemy regex wyszukiwaniem po drzewie składni Roslyn, przepisujemy argumenty na drzewie (`CSharpSyntaxRewriter`), a na końcu dopasowujemy wywołania po typach (`SemanticModel`).

**Zasada:** Codemod (receptura automatyczna) to powtarzalna transformacja wielu miejsc naraz; oparty na składni i typach jest bezpieczniejszy od regexa, ale automatyzacja zwiększa też zasięg błędu receptury. Recepturę sprawdzają testy `before`/`after`, kompilacja wyniku i test idempotencji.

**Efekt:** Codemod migruje tylko wywołania rozwiązane do przestarzałej `Cinema.BookingService.Book`, wynik kompiluje się bez ostrzeżeń, a drugie uruchomienie niczego nie zmienia. Kosztem jest sporo kodu narzędziowego, więc przy kilku miejscach szybsze bywa Change Signature w IDE.

**Różnica względem Javy:** zamiast Compiler Tree API (`JavacTask`, `TreeScanner`, `Trees`) codemod używa Roslyn: `CSharpSyntaxTree` i `InvocationExpressionSyntax` do wyszukiwania, `CSharpSyntaxRewriter` do przepisania (podmiana węzłów argumentów z zachowaniem formatowania, czyli trivia, zamiast edycji tekstu po pozycjach od końca pliku) i `SemanticModel.GetSymbolInfo` do dopasowania po typach. Próbka projektu jest przepisana na C#; nowe typy `Channel` i `Glasses` leżą w przestrzeni `Cinema.Options`, żeby codemod nadal musiał dopisać dyrektywę `using` (sam `using Cinema;` nie obejmuje podprzestrzeni). Nawiasy klamrowe w osobnych liniach zmieniają numery linii: wywołania to 13, 19-21 i 26, komentarz 18 (w Javie 11, 16-18, 22 i 15).

### Co widzimy

`SampleProject.Api` zawiera API kina z przestarzałym (`[Obsolete]`) `Book(..., bool web, bool ownGlasses)` i nowym `Book(..., Channel, Glasses)`, łudząco podobne `HotelService.Book(..., bool, bool)`, a `SampleProject.TicketDesk` to klient z trzema wywołaniami: w jednej linii (13), rozbite na trzy linie (19-21) i hotelowe (26), plus komentarz ze starym przykładem (18). `Start.BookCallCodemod` to "grep i zamień":

```csharp
private static readonly Regex OldCall = new(@"\bBook\(.*,\s*(true|false)\s*,\s*\w+\s*\)");
```

Test pokazuje: regex znajduje linie 13, 18, 26 - trafia w komentarz i hotel, a gubi wywołanie wielolinijkowe.

### Krok 1: wyszukiwanie przez AST

**W IDE:** ręcznie - `CSharpSyntaxTree.ParseText(source).GetCompilationUnitRoot()`, potem `DescendantNodes().OfType<InvocationExpressionSyntax>()`; dopasowanie: `MemberAccessExpressionSyntax` o nazwie `Book` z sześcioma argumentami; linia z `GetLocation().GetLineSpan()`.
**Po:**

```csharp
return unit.DescendantNodes()
    .OfType<InvocationExpressionSyntax>()
    .Where(LooksLikeOldBook)
    .Select(call => call.GetLocation().GetLineSpan().StartLinePosition.Line + 1)
    .ToList();
```

**Uruchom:** test zielony; `Step1AstSearchFindsRealCallsIncludingMultilineOne` - linie 13, 19, 26.
**Co powiedzieć:** parser widzi wywołania, nie tekst. Ale 26 to nadal hotel - sama składnia nie zna typów.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s09 0 1`

### Krok 2: przepisanie na drzewie składni

**W IDE:** `Rewrite` przepuszcza drzewo przez `BookCallRewriter : CSharpSyntaxRewriter`, który podmienia dokładnie dwa ostatnie argumenty znalezionych wywołań (literał → stała enum, wyrażenie → operator warunkowy) z zachowaniem trivia, a potem dopisuje `using Cinema.Options;`, jeśli go brakuje.
**Po:**

```csharp
if (expression.IsKind(SyntaxKind.TrueLiteralExpression) || expression.IsKind(SyntaxKind.FalseLiteralExpression))
{
    text = expression.IsKind(SyntaxKind.TrueLiteralExpression) ? ifTrue : ifFalse;
}
```

**Uruchom:** test zielony; `Step2SyntacticRewriteBreaksTheHotelCallAndIsNotIdempotent` - wywołanie hotelowe zostało "zmigrowane" i kod się nie kompiluje, a drugie uruchomienie psuje już zmigrowane wywołania (dalej mają nazwę `Book` i sześć argumentów).
**Co powiedzieć:** automatyzacja zwiększa zasięg - także zasięg błędu receptury.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s09 1 2`

### Krok 3: dopasowanie po typach

**W IDE:** analiza razem ze źródłami projektu (`CSharpCompilation.Create` z `SampleProject.Api` i klientem), `compilation.GetSemanticModel(tree)` i `model.GetSymbolInfo(call).Symbol`; migrujemy tylko wywołania rozwiązane do przestarzałej metody `Cinema.BookingService.Book`.
**Po:**

```csharp
return method.Name == "Book"
    && method.GetAttributes().Any(a => a.AttributeClass?.ToDisplayString() == "System.ObsoleteAttribute")
    && method.ContainingType.ToDisplayString() == OldApiOwner;
```

**Uruchom:** test zielony; wynik identyczny z oczekiwanym `Migrated`, kompiluje się bez ostrzeżeń (oryginał daje dwa razy `WARNING CS0618`), a drugie uruchomienie niczego nie zmienia.
**Co powiedzieć:** receptura oparta na typach, test `before/after` na próbce, test idempotencji. Tak działają poprawki (code fix) analizatorów Roslyn uruchamiane hurtowo w IDE albo przez `dotnet format`, a w świecie Javy - OpenRewrite.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s09 2 3`

### Rozwiązanie i uzasadnienie

Codemod ma trzy dowody: znajduje dokładnie właściwe miejsca, wynik się kompiluje bez użycia przestarzałego API, a ponowne uruchomienie jest bezpieczne. Diff z codemodu idzie do osobnego commita niż zmiany ręczne.

### Pułapki

- Regex na kodzie źródłowym: komentarze, stringi, wywołania wielolinijkowe, przeciążenia.
- Brak testu idempotencji - receptura uruchomiona drugi raz w CI psuje kod.
- Dyrektywy `using` dopisane "gdzieś" (w wyniku `using Cinema.Options;` stoi za `using Hotel;`) - kolejność poprawi Code Cleanup albo `dotnet format`, ale w osobnym commicie.

### Pytanie do sali

Kiedy codemod się opłaca, a kiedy szybciej jest poprawić 12 miejsc ręcznie z pomocą Change Signature w IDE?

## Scena s10. Minimalna bramka jakości jako kod

**Temat ze slajdów:** 5.6-5.7 Bramka kompilatora i minimalna bramka jakości
**Namespace:** `Training.Workshop.M8.S10QualityGate` · **Test:** `scripts/warsztat.sh --lang cs test m8/s10`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `QualityGate` w `Start` to lista kontrolna w komentarzu dokumentacyjnym i metoda, która zawsze zwraca pustą listę, więc przepuszcza brudną próbkę. Zamieniamy listę na cztery wykonywalne sprawdzenia: skan TODO i wydruków na konsolę, ostrzeżenia kompilatora, przybliżenie pokrycia i uruchomienie testów.

**Zasada:** Minimalna bramka jakości łączy kilka niezależnych sygnałów, bo każde narzędzie dowodzi czegoś innego. Wynik ma być deterministyczny i wskazywać miejsce, a bramka potrzebuje właściciela i nie może dawać fałszywych alarmów - inaczej zespół ją wyłączy.

**Efekt:** Bramka zgłasza problemy `Sample/Dirty`, zatrzymuje nieprzechodzący test z `Sample/Broken` i przepuszcza `Sample/Clean`. Sprawdzenie pokrycia to tylko przybliżenie ("czy jakiś test w ogóle woła metodę") i nie zastępuje pomiaru coverlet.

**Różnica względem Javy:** próbki `Sample/Clean`, `Sample/Dirty` i ich testy (`Sample/{Clean,Dirty,Broken}/PriceTableTest.cs` w projekcie testów) nie wchodzą do kompilacji projektu (`<Compile Remove>` w obu plikach `.csproj`) - bramka czyta je przez `SourceFiles` i kompiluje sama, Roslynem w pamięci. Skutek: testy próbek nie są uruchamiane przez xUnit (w Javie to zwykłe testy JUnit), uruchamia je tylko bramka z kroku 4. Skan szuka `Console.Write`/`Console.Error` zamiast `System.out`/`System.err`, a "surowy typ" z Javy to niegeneryczna `ArrayList` w polu, którego nikt nie przypisuje - kompilator zgłasza dla niego CS0649 i CS8618 zamiast dwóch `compiler.warn.raw.class.use`. Fikstura broken ma własny atrybut `Test` i wyjątek `AssertionException` (w Javie `AssertionError`).

### Co widzimy

`Start.QualityGate` to lista kontrolna w komentarzu `<summary>` i metoda `Evaluate`, która zawsze zwraca pustą listę - przepuszcza próbkę `Sample/Dirty` (TODO, `Console.WriteLine`, niegeneryczna kolekcja, metody bez testu). Bramka działa na `GateInput`: katalog źródeł domeny, plik testu, kluczowa klasa i pełna nazwa klasy testowej.

```csharp
public IReadOnlyList<string> Evaluate(GateInput input)
{
    return [];
}
```

### Krok 1: skan źródeł - TODO i Console

**W IDE:** metoda `ScanSources`: każda linia z `TODO`/`FIXME` albo `Console.Write`/`Console.Error` to wynik z plikiem i linią.
**Po:**

```csharp
if (line.Contains("TODO") || line.Contains("FIXME"))
{
    findings.Add("TODO " + where);
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m8/s10` - `TODO PriceTable.cs:15`, `Console PriceTable.cs:26`.
**Co powiedzieć:** tanie, deterministyczne, z pozycją - od tego zaczynamy.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s10 0 1`

### Krok 2: ostrzeżenia kompilatora

**W IDE:** metoda `CompileSources` (Roslyn w pamięci: nullable włączone, `warningLevel: 9999`, te same globalne `using` co w projekcie), każde ostrzeżenie i błąd w źródłach to wynik.
**Po:**

```csharp
findings.Add("kompilator " + Path.GetFileName(d.Location.SourceTree!.FilePath) + ":"
    + (d.Location.GetLineSpan().StartLinePosition.Line + 1) + " " + d.Id);
```

**Uruchom:** test zielony; dochodzą dwa wyniki: `kompilator PriceTable.cs:11 CS0649` (pole nigdy nie jest przypisane) i `kompilator PriceTable.cs:11 CS8618` (pole nie dopuszcza null, a konstruktor go nie ustawia).
**Co powiedzieć:** to ta sama idea co w s08, tu jako jeden z kilku sygnałów bramki.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s10 1 2`

### Krok 3: pokrycie kluczowej klasy (przybliżenie)

**W IDE:** metoda `CheckCoverage`: każda publiczna metoda `PriceTable` musi być wywołana w `PriceTableTest`.
**Po:**

```csharp
if (!test.Contains("." + method.Groups[1].Value + "("))
{
    findings.Add("pokrycie " + input.KeyClass + "." + method.Groups[1].Value + " bez testu");
}
```

**Uruchom:** test zielony; `VipSurcharge` i `LookupCount` bez testu.
**Co powiedzieć:** to nie zastępuje pomiaru pokrycia (`dotnet test --collect:"XPlat Code Coverage"` - pakiet `coverlet.collector` jest w projekcie testów), ale łapie metodę, której żaden test nawet nie woła.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s10 2 3`

### Krok 4: testy zielone

**W IDE:** metoda `RunTests`: Roslyn kompiluje domenę razem z plikiem testu, zestaw trafia do zwalnianego `AssemblyLoadContext`, a refleksja uruchamia metody z atrybutem o nazwie `Fact` albo `Test` i zgłasza porażki.
**Po:**

```csharp
try
{
    test.Invoke(Activator.CreateInstance(type, nonPublic: true), null);
}
catch (TargetInvocationException failure)
{
    findings.Add("test " + type.Name + "." + test.Name + " nie przechodzi: "
        + failure.InnerException?.GetType().Name);
}
```

**Uruchom:** test zielony; `Step4AddsGreenTestsAndCatchesAFailingOne` - fikstura `Sample/Broken` (celowo nie xUnit, żeby nie psuć buildu) daje wynik `test PriceTableTest.VipSurchargeStartsAtRowNine nie przechodzi: AssertionException`.
**Co powiedzieć:** lista kontrolna jest w całości wykonywalna i ma ten sam wynik u każdego.
**Snapshot:** `Step4/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s10 3 4`

### Rozwiązanie i uzasadnienie

Bramka łączy kilka niezależnych sygnałów. `S10EquivalenceTest` pilnuje drugiej strony: czysta próbka przechodzi każdą wersję bramki (wynik `[]`, brak fałszywych alarmów).

### Pułapki

- Bramka, która nic nie sprawdza, a daje zielony znaczek.
- Przybliżenie pokrycia potraktowane jak pomiar.
- Bramka bez właściciela - każdy fałszywy alarm kończy się jej wyłączeniem.

### Pytanie do sali

Które z tych czterech sprawdzeń uruchomilibyście lokalnie przed commitem, a które tylko w CI?

## Scena s11. Jawna polityka wdrożenia etapowego

**Temat ze slajdów:** 6.2 Warstwy kontroli; 6.3-6.4 Kryteria i jawna polityka wdrożenia etapowego
**Namespace:** `Training.Workshop.M8.S11StagedRollout` · **Test:** `scripts/warsztat.sh --lang cs test m8/s11`
**Czas:** ~10 min

### W skrócie

**Co robimy:** `CheckoutRouter` wdraża nowy proces płatności przez stałą w kodzie i testerów wpisanych w `if`, bez etapów i bez wyłącznika. Zamieniamy to na rekord `RolloutPolicy` z procentem ruchu, deterministycznym koszykiem klienta i wyłącznikiem awaryjnym.

**Zasada:** Wdrożenie etapowe to jawna, testowana polityka: populacja, deterministyczny podział klientów, wyjątki i wyłącznik, a o zwiększeniu ekspozycji decydują z góry ustalone kryteria. Podział ma być stabilny - ten sam klient zawsze na tej samej ścieżce - więc bez losowania i bez `GetHashCode` obiektu.

**Efekt:** Z dotychczasowymi ustawieniami router kieruje klientów tak samo jak `Start`, procent da się zwiększać bez wyrzucania nikogo, a kill switch wycofuje zmianę bez nowego wydania. Świadomie zmienia się jedno zachowanie: e-mail jest normalizowany, a start porównywał go dosłownie.

**Różnica względem Javy:** CRC32 nie ma w bibliotece standardowej .NET (wymaga pakietu `System.IO.Hashing`), więc koszyk liczymy ze SHA-256 (`SHA256.HashData`, pierwsze 4 bajty big-endian modulo 100) - to nadal stabilny skrót. `string.GetHashCode()` w .NET jest losowany przy każdym starcie procesu, więc tu byłby błędem nawet bardziej oczywistym niż w Javie. Stała `NEW_CHECKOUT` to `internal static readonly bool NewCheckout` (`const` dałby ostrzeżenie o nieosiągalnym kodzie, a przy `TreatWarningsAsErrors` błąd). Walidacja procentu siedzi w inicjalizatorze właściwości rekordu pozycyjnego, a `Set.copyOf` to `ToHashSet()` wystawione jako `IReadOnlySet<string>`.

### Co widzimy

`Start.CheckoutRouter` wdraża nowy proces płatności "na flagę": stała w kodzie (zmiana = nowe wydanie), testerzy wpisani w `if`, brak etapów i wyłącznika awaryjnego.

```csharp
internal static readonly bool NewCheckout = false;

public bool UseNewCheckout(string email)
{
    if (NewCheckout)
    {
        return true;
    }
    return email.Equals("anna@kino.pl") || email.Equals("jan@kino.pl");
}
```

### Krok 1: polityka jako wartość

**W IDE:** Introduce Parameter Object ręcznie (flaga i lista testerów nie są parametrami, więc Extract Class from Parameters w Rider tu nie pomoże) → rekord `RolloutPolicy(bool Enabled, IReadOnlySet<string> AllowList)`, fabryka `Current()` z dotychczasowymi wartościami; router dostaje politykę w konstruktorze.
**Po:**

```csharp
public bool UseNewCheckout(string email)
{
    return _policy.Allows(email);
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m8/s11` - równoważność dla dotychczasowych ustawień.
**Co powiedzieć:** konfigurację da się podać z zewnątrz, przetestować i przejrzeć.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s11 0 1`

### Krok 2: deterministyczny podział klientów

**W IDE:** ⌘F6 na konstruktorze głównym rekordu (albo ręcznie): `int Percent` zamiast `bool Enabled` (0 = dawne `false`); metoda `Bucket` - SHA-256 znormalizowanego e-maila modulo 100.
**Po:**

```csharp
public bool Allows(string email)
{
    return AllowList.Contains(Normalize(email)) || Bucket(email) < Percent;
}
```

**Uruchom:** test zielony; `SameCustomerAlwaysGetsTheSamePath`, `PercentOfCustomersIsRoughlyRespected`, `IncreasingPercentNeverRemovesAnyone`.
**Co powiedzieć:** stabilny skrót, nie `GetHashCode` i nie losowanie. Ten sam klient zawsze widzi tę samą ścieżkę, a zwiększenie procentu nikogo nie wyrzuca. Uwaga: normalizacja e-maila to świadoma zmiana - start porównywał dosłownie (`StartComparesEmailLiterally`).
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s11 1 2`

### Krok 3: wyłącznik awaryjny

**W IDE:** parametr `KillSwitch` w rekordzie, sprawdzany jako pierwszy w `Allows`.
**Po:**

```csharp
if (KillSwitch)
{
    return false;
}
```

**Uruchom:** test zielony; `KillSwitchOverridesPercentAndAllowList`.
**Co powiedzieć:** kill switch ma pierwszeństwo nawet przed testerami - służy do natychmiastowego wycofania bez wydania.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s11 2 3`

### Rozwiązanie i uzasadnienie

Polityka wdrożenia to kod z testami: populacja, deterministyczny podział, wyjątki i wyłącznik. Decyzję o zwiększeniu procentu (ADVANCE / HOLD / ROLLBACK) podejmuje się na podstawie metryk - to temat przykładu `RolloutPolicy` z namespace `Training.Module8.Risk` (`csharp/src/Training.Module8/Risk`).

### Pułapki

- Losowanie przy każdym żądaniu - klient przeskakuje między ścieżkami.
- `string.GetHashCode()` - w .NET inny po każdym restarcie procesu, a do tego ujemny wynik z `%` daje ujemne koszyki.
- Flaga bez właściciela i terminu usunięcia.

### Pytanie do sali

Kill switch wyłącza nowy proces płatności. Co z zamówieniami, które są w trakcie płatności w nowym procesie?

## Scena s12. Expand and contract - format danych rezerwacji

**Temat ze slajdów:** 6.5-6.6 Dane, wycofanie i zamknięcie migracji
**Namespace:** `Training.Workshop.M8.S12ExpandContract` · **Test:** `scripts/warsztat.sh --lang cs test m8/s12`
**Czas:** ~15 min

### W skrócie

**Co robimy:** Repozytorium rezerwacji zna tylko format csv, a zapis nowego formatu w tej samej kolumnie uniemożliwiłby wycofanie wydania. Przechodzimy na kolumnę `Payload` w czterech krokach: podwójny zapis, odczyt z fallbackiem, backfill starych wierszy i contract.

**Zasada:** Expand and contract migruje dane tak, żeby w oknie wycofania stara wersja kodu czytała to, co zapisała nowa: najpierw rozszerzamy (nowe pole, podwójny zapis), potem przełączamy odczyt i uzupełniamy stare dane, a stary format usuwamy na końcu. Flaga ani wycofanie wdrożonej wersji aplikacji nie cofną danych zapisanych w niezgodnym formacie.

**Efekt:** Repozytorium pisze i czyta tylko wersjonowany `Payload`, a w kodzie nie ma już odwołań do starego formatu - migracja jest zamknięta. Ceną jest zamknięcie okna wycofania: po contract stara wersja nie widzi nowych danych, więc ten krok robi się dopiero, gdy powrót nie będzie potrzebny.

**Różnica względem Javy:** `Optional<Booking>` to `Booking?` (brak wiersza to `null`, w testach `Assert.Null`). Rekord `Booking` ma własne `Equals`/`GetHashCode`, bo rekord C# porównywałby listę miejsc po referencji, a klasy formatów z prywatnym konstruktorem to `static class`.

### Co widzimy

`BookingTable` ma starą kolumnę `Csv` i nową, pustą kolumnę `Payload` (migracja schematu "expand" już zrobiona). `Start.BookingRepository` zna tylko csv: `B1;anna@kino.pl;A5,A10;84.00`. Zapisanie nowego formatu "w miejscu" do tej samej kolumny uniemożliwiłoby wycofanie wydania.

```csharp
public void Save(Booking booking)
{
    _table.Put(booking.Id, new BookingTable.Row(CsvBookingFormat.Write(booking), null));
}
```

### Krok 1: expand + dual write

**W IDE:** nowa klasa `BookingPayloadFormat` (wersjonowany format `v2|id=...|email=...`); `Save` pisze obie kolumny, `Find` nadal czyta csv.
**Po:**

```csharp
_table.Put(booking.Id, new BookingTable.Row(CsvBookingFormat.Write(booking),
    BookingPayloadFormat.Write(booking)));
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m8/s12` - `Step1DualWriteKeepsRollbackToTheOldVersionSafe`: repozytorium ze startu czyta dane zapisane przez krok 1.
**Co powiedzieć:** wycofanie kodu jest bezpieczne, bo stary format wciąż powstaje.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s12 0 1`

### Krok 2: odczyt nowego formatu z fallbackiem

**W IDE:** Extract Method (⌥⌘M) `Read(Row)`: payload, a gdy go brak - csv.
**Po:**

```csharp
return row.Payload != null
    ? BookingPayloadFormat.Read(row.Payload)
    : CsvBookingFormat.Read(row.Csv!);
```

**Uruchom:** test zielony; `Step2ReadsNewFormatAndFallsBackForOldRows`.
**Co powiedzieć:** stare wiersze (sprzed kroku 1) nadal są czytelne. Zapis dalej podwójny.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s12 1 2`

### Krok 3: backfill

**W IDE:** metoda `MigrateAll` uzupełnia payload w wierszach, które go nie mają; zwraca licznik.
**Po:**

```csharp
if (row.Payload == null)
{
    _table.Put(id, new BookingTable.Row(row.Csv,
        BookingPayloadFormat.Write(CsvBookingFormat.Read(row.Csv!))));
    migrated++;
}
```

**Uruchom:** test zielony; `Step3BackfillIsIdempotentAndPreparesTheContract` - przed backfillem wersja po contract gubi stary wiersz, po backfillu go widzi; drugie uruchomienie migruje 0 wierszy.
**Co powiedzieć:** kolejność ma znaczenie: backfill przed contract. Licznik to dowód w logu wdrożenia.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s12 2 3`

### Krok 4: contract - usunięcie starego formatu

**W IDE:** Safe Delete (⌘⌦) na `CsvBookingFormat`; `Save` pisze tylko payload (`Row.WithPayload`), `Find` czyta tylko payload; `MigrateAll` znika razem z fallbackiem.
**Po:**

```csharp
public Booking? Find(string id)
{
    string? payload = _table.Get(id)?.Payload;
    return payload is null ? null : BookingPayloadFormat.Read(payload);
}
```

**Uruchom:** test zielony; `Step4HasNoReferenceToTheOldFormat` (żaden plik kroku nie wspomina starego formatu) i `Step4ClosesTheRollbackWindow` (stara wersja nie widzi nowych danych).
**Co powiedzieć:** migracja kończy się po usunięciu kosztu legacy. Od tego momentu wycofanie kodu już nie wystarczy - dlatego contract robimy dopiero po zamknięciu okna wycofania. Usunięcie kolumny to kolejna, osobna migracja schematu.
**Snapshot:** `Step4/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s12 3 4`

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
**Namespace:** `Training.Workshop.M8.S13LivingDocs` · **Test:** `scripts/warsztat.sh --lang cs test m8/s13`
**Czas:** ~8 min

### W skrócie

**Co robimy:** Ręcznie pisany `ROUTING.md` rozjechał się z `Routing.Routes()`: twierdzi, że raport obsługuje legacy, i nie zna operacji `cancel`. Generujemy dokument z kodu przez `RoutingDoc`, pilnujemy go testem i dopisujemy właściciela oraz kryterium usunięcia każdej trasy.

**Zasada:** Dokumentacja żywa opisuje stan obecny, więc powinna powstawać z systemu albo być z nim sprawdzana, a historyczna (ADR, zamknięty opis zmian) jest niezmienna. Mieszanie tych ról kończy się dokumentem, który opisuje nieistniejący system albo gubi historię.

**Efekt:** Test porównuje zapisany `ROUTING.md` z wygenerowanym przy każdym buildzie, a każda trasa pokazuje właściciela i warunek usunięcia. Uzasadnienia decyzji nie wpisujemy do tego pliku, bo zniknęłyby przy regeneracji - ich miejsce jest w ADR.

**Różnica względem Javy:** zamiast `RoutingDoc.main` jest statyczna metoda `RoutingDoc.Regenerate()` - biblioteka `Training.Workshop` nie ma punktu wejścia, a dodatkowe `Main` kolidowałoby z punktem wejścia projektu testowego. Wywołuje się ją z katalogiem roboczym `csharp/`, bo `Location()` liczy ścieżkę z przestrzeni nazw względem tego katalogu: `src/Training.Workshop/M8/S13LivingDocs/Step2/ROUTING.md`.

### Co widzimy

`Start.Routing.Routes()` to routing fasady Strangler Fig jako kod - źródło prawdy. Obok leży ręcznie pisany `Start/ROUTING.md`, który twierdzi, że raport obsługuje legacy, i nie zna operacji `cancel`. Test `StartDocumentationHasDriftedFromCode` wypisuje rozjazd: `report: dokument mowi legacy, kod mowi new` i `cancel: brak w dokumencie`.

```text
| Operacja | Obsługuje |
| --- | --- |
| book | new |
| report | legacy |
```

### Krok 1: dokument generowany z kodu

**W IDE:** nowa klasa `RoutingDoc` z `Render(routes)`, `Location()` (ścieżka liczona z przestrzeni nazw) i `Regenerate()`, która zapisuje `ROUTING.md` obok kodu. Wywołaj `RoutingDoc.Regenerate()` z katalogiem roboczym `csharp/` - np. w oknie C# Interactive w Rider (po załadowaniu projektu i `Directory.SetCurrentDirectory(...)`) albo z jednorazowego testu.
**Po:**

```csharp
foreach (Routing.Route route in routes)
{
    md.Append("| " + route.Operation + " | " + route.Target + " |").Append('\n');
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m8/s13` - `Step1DocumentIsGeneratedFromCode`: zapisany plik równa się wygenerowanemu.
**Co powiedzieć:** dokument żywy nie może się rozjechać, bo test porównuje go z kodem przy każdym buildzie.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s13 0 1`

### Krok 2: architektura przejściowa w dokumencie

**W IDE:** ⌘F6 na konstruktorze głównym rekordu `Route` (albo ręcznie): parametry `Owner` i `RemoveWhen`; nowe kolumny w `Render`; ponownie wywołaj `Regenerate()`.
**Po:**

```csharp
new Route("cancel", "legacy", "zespol Sprzedaz",
    "CancelModule w trybie CANDIDATE przez 14 dni"),
```

**Uruchom:** test zielony; `Step2EveryTransitionalRouteHasOwnerAndRemovalCriterion`.
**Co powiedzieć:** żywa dokumentacja operacyjna mówi, co działa dziś i kiedy to zniknie. Dlaczego tak zdecydowaliśmy - to ADR (historia, scena s07).
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m8/s13 1 2`

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
