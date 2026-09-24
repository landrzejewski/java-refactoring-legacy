# Moduł 3. Zasady dobrego projektowania - warsztat CineLegacy (C#): przewodnik prowadzącego

Szesnaście małych scen w domenie kina CineLegacy pokazuje zasady z modułu 3 jako serie bezpiecznych zmian: DRY, KISS, YAGNI, SOLID, spójność i sprzężenie, granice Clean Architecture, wzorzec jako decyzja odwracalna oraz inwarianty modelu. Każda scena ma kod wyjściowy (`Start`) z zapachem lub pułapką i kompletne snapshoty po każdym kroku (`Step1`, `Step2`, ...). Prowadzący pracuje na żywo w `Start`, po każdym kroku uruchamia test sceny i porównuje wynik ze snapshotem.

Sceny nie powielają ćwiczeń z `md/zadania/03-zasady-dobrego-projektowania.md` (studium wyceny dostawy w `csharp/src/Training.Module3`): tamte ćwiczą jeden duży przykład, tu każda zasada ma własną, kilkuminutową scenę.

Port C#: kod scen leży w `csharp/src/Training.Workshop/M3/SNN.../{Start,Step1,...}`, testy xUnit w `csharp/tests/Training.Workshop.Tests/M3/SNN.../`. Kwoty to `decimal` (odpowiednik `BigDecimal`), zaokrąglanie `Math.Round(x, 2, MidpointRounding.AwayFromZero)`. `decimal` nie zawsze pamięta skalę 2, a konkatenacja z napisem używa bieżącej kultury, dlatego kwoty w wynikach są formatowane jawnie (`ToString("0.00", CultureInfo.InvariantCulture)` albo interpolacja z `CultureInfo.InvariantCulture`) - oczekiwane teksty są takie same jak w wersji Java. Wyjątki: `IllegalArgumentException` -> `ArgumentException`, `IllegalStateException` -> `InvalidOperationException`, `UnsupportedOperationException` -> `NotSupportedException`. Interfejsy mają prefiks `I` (`IPricingRule`, `ISeatMap`, `ITicketSales`, `ICustomerNotifier`, ...), a podpakiety `app`/`infra`/`adapter`/`domain` to przestrzenie nazw `App`/`Infra`/`Adapter`/`Domain`.

## Jak prowadzić pokaz

```bash
scripts/warsztat.sh --lang cs list m3              # lista scen i kroków modułu 3
scripts/warsztat.sh --lang cs test m3/s01          # testy jednej sceny (krótka nazwa wystarczy)
scripts/warsztat.sh --lang cs test m3              # wszystkie sceny modułu (238 testów)
scripts/warsztat.sh --lang cs diff m3/s01 0 1      # co zmienia krok 1 względem Start (0 = Start)
scripts/warsztat.sh --lang cs diff m3/s01 0 1 --word  # to samo, zmiany podświetlone w obrębie linii
scripts/warsztat.sh --lang cs jump m3/s12 3        # przeskok: Start = snapshot kroku 3
scripts/warsztat.sh --lang cs next m3/s12          # następny krok do Start + podsumowanie zmian
scripts/warsztat.sh next                           # kolejny krok tej samej sceny (język i scena zapamiętane)
scripts/warsztat.sh prev                           # krok wstecz
scripts/warsztat.sh status                         # który krok jest teraz w Start
scripts/warsztat.sh --lang cs reset m3/s12         # przywrócenie Start z repozytorium
```

- Zasada pokazu: **jeden krok - jeden test - jedno zdanie komentarza**. Nie łącz kroków, nawet jeśli IDE pozwala.
- **Krok po kroku bez numerów:** `scripts/warsztat.sh next` wstawia do `Start` gotowy następny krok i wypisuje, co się zmieniło. `prev` cofa o krok, `status` mówi, który krok jest teraz w `Start`. Skrypt pamięta język i ostatnią scenę, więc po pierwszym `--lang cs next m3/sNN` wystarczy samo `next`. Ręczne zmiany w `Start` zostają nadpisane - o to chodzi, gdy krok na żywo się rozjechał.
- Jeśli krok na żywo się nie uda albo brakuje czasu: `next` (albo `jump` do konkretnego snapshotu) i kontynuuj od następnego kroku. Po pokazie zawsze `reset`.
- Testy dotykają `Start` wyłącznie przez API wspólne dla wszystkich kroków, więc kompilują się w każdym stanie pośrednim. Tam, gdzie refaktoryzacja zmienia konstruktory, scena ma punkt wejścia pełniący rolę composition root: statyczna klasa `Main` (s11), `CinemaApplication` (s12), `CinemaApp` (s13), `TicketDesk` (s16). Refaktoryzacje Rider (Introduce Parameter, Move to Folder/Namespace) same aktualizują te miejsca.
- IDE: JetBrains Rider z keymapą macOS (skróty jak w IntelliJ): ⌥⌘M Extract Method, ⇧F6 Rename, ⌥⌘V Introduce Variable, ⌥⌘C Introduce Constant, ⌥⌘P Introduce Parameter, ⌥⌘N Inline, ⌘F6 Change Signature, F6 Move, ⌘⌦ Safe Delete, ⌃T Refactor This, ⌥⏎ akcje kontekstowe, ⌥F7 Find Usages. Test sceny uruchamiasz skryptem albo z ikony w marginesie (gutter) klasy testowej lub z okna Unit Tests. Refaktoryzacji, których Rider nie ma (np. Inline Class, Replace Inheritance with Delegation), dokonujemy ręcznie - przy każdej takiej scenie jest opis.
- Pułapki, których nie da się sprawdzić na edytowanym `Start` (bo znikają po naprawie), testy dokumentują na kopii w kroku, który jeszcze ją ma (np. s04 krok 1, s09 krok 1, s15 krok 1).
- Test s13 czyta pliki źródłowe sceny. Katalog `csharp/` znajduje sam - szuka `RefactoringLegacy.slnx` w górę od katalogu wyjściowego testów - więc działa ze skryptu, z Rider i z `dotnet test`.

## Mapa scen

| Scena | Temat ze slajdów | Kroki | Namespace | Czas |
| --- | --- | --- | --- | --- |
| s01 | 2.1 DRY dotyczy wiedzy | 4 | `M3.S01DryKnowledge` | ~12 min |
| s02 | 2.2 Podobieństwo nie wystarcza | 3 | `M3.S02Similarity` | ~10 min |
| s03 | 2.3 Fałszywa abstrakcja | 2 | `M3.S03FalseAbstraction` | ~8 min |
| s04 | 2.3 DRY w testach, niezależna wyrocznia | 2 | `M3.S04DryTests` | ~10 min |
| s05 | 2.4-2.5 KISS: złożoność wprowadzona i istotna | 2 | `M3.S05Kiss` | ~8 min |
| s06 | 2.6-2.7 YAGNI, 2.8 napięcia, 2.9 filtr decyzyjny | 3 | `M3.S06Yagni` | ~12 min |
| s07 | 3.2 SRP: jeden aktor zmiany | 3 | `M3.S07Srp` | ~12 min |
| s08 | 3.3 OCP: zamknięcie dla wybranej osi | 3 | `M3.S08Ocp` | ~10 min |
| s09 | 3.4 LSP i test kontraktowy | 2 | `M3.S09Lsp` | ~12 min |
| s10 | 3.5 ISP z perspektywy klienta | 2 | `M3.S10Isp` | ~8 min |
| s11 | 3.6 DIP, 5.1-5.2 reguła zależności a przepływ sterowania | 3 | `M3.S11Dip` | ~12 min |
| s12 | 5.3-5.6 przypadek użycia, dane na granicy, composition root | 4 | `M3.S12CleanArchitecture` | ~18 min |
| s13 | 4.4 diagnostyka spójności, lista kontrolna "granica sprawdzana automatycznie" | 2 | `M3.S13BoundaryCheck` | ~12 min |
| s14 | 6.1-6.4 Strategy i Adapter, wzorzec można usunąć | 3 | `M3.S14ReversiblePattern` | ~12 min |
| s15 | 7.4 model domeny i inwarianty | 2 | `M3.S15Invariants` | ~8 min |
| s16 | 4.4 sprzężenie protokołu i czasu | 2 | `M3.S16TemporalCoupling` | ~6 min |

Przestrzenie nazw są względne wobec `Training.Workshop` (katalogi `csharp/src/Training.Workshop/M3/...`), testy leżą w `Training.Workshop.Tests.M3...` (`csharp/tests/Training.Workshop.Tests/M3/...`).

## Scena s01. DRY - jedna wiedza, dwie reprezentacje

**Temat ze slajdów:** 2.1 DRY dotyczy wiedzy; 2.8 Napięcia między zasadami (nazwanie potwierdzonej reguły)
**Namespace:** `Training.Workshop.M3.S01DryKnowledge` · **Test:** `scripts/warsztat.sh --lang cs test m3/s01` (`S01EquivalenceTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** W `BoxOffice` reguła ceny biletu jest zapisana dwa razy - raz przy sprzedaży, raz przy zwrocie - różnym kodem. Nazywamy obie kopie, sprawdzamy testem, że liczą to samo, zostawiamy jedną i przenosimy ją do klasy `TicketPrice`.

**Zasada:** DRY mówi, że każda wiedza w systemie powinna mieć jedną autorytatywną reprezentację. Chodzi o wiedzę (regułę biznesową), a nie o podobny tekst: dwa różne fragmenty mogą kodować tę samą regułę, a dwa identyczne mogą być przypadkowo podobne.

**Efekt:** Zmiana zniżki studenckiej to teraz edycja jednego miejsca, a zwrot automatycznie używa aktualnego cennika. Reguły samego zwrotu zostają w `BoxOffice`, bo mają innego właściciela.

### Co widzimy

`BoxOffice` sprzedaje bilety i przyjmuje zwroty. Reguła ceny biletu (cena formatu, zniżka typu, seans poranny) jest zapisana dwa razy: w `Sell` jako dwa wyrażenia `switch` i procent, w `Refund` jako łańcuch `if` i mnożniki. Tekst jest inny, więc detektor duplikatów IDE nic nie znajdzie, ale wiedza jest ta sama. Zmiana zniżki studenckiej wymaga zgodnej edycji dwóch miejsc.

```csharp
// ile kosztował bilet
var paid = 25.00m;
if (ticket.Format == "3D")
{
    paid = 32.00m;
}
else if (ticket.Format == "IMAX")
{
    paid = 40.00m;
}
if (ticket.Type == "STUDENT")
{
    paid *= 0.75m;
} // ...SENIOR 0.70, CHILD 0.60, poranek -5.00
```

### Krok 1: Extract Method - nazwij drugą kopię wiedzy

**W IDE:** w `Refund` zaznacz blok od `// ile kosztował bilet` do końca `if` z porankiem, ⌥⌘M, nazwa `PaidFor`. Usuń komentarz.
**Po:**

```csharp
public decimal Refund(Ticket ticket, long hoursBeforeStart)
{
    var paid = PaidFor(ticket);
    // zwrot: >= 24h 100%, < 24h 50%, po starcie 0%; potrącenie 3.00
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m3/s01` - 25 testów zielonych (5 przypadków x 5 wariantów).
**Co powiedzieć:** zanim scalimy wiedzę, musimy ją zobaczyć. Nazwa `PaidFor` odpowiada na to samo pytanie co `Sell`: ile kosztuje ten bilet?
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s01 0 1`

### Krok 2: Extract Method po stronie sprzedaży

**W IDE:** w `Sell` zaznacz całe ciało metody, ⌥⌘M, nazwa `TicketPrice`.
**Po:**

```csharp
public decimal Sell(Ticket ticket)
{
    return TicketPrice(ticket);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** teraz obie kopie reguły to dwie prywatne metody o identycznej sygnaturze. Widać, że jedna jest zbędna.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s01 1 2`

### Krok 3: Substitute Algorithm - zwrot korzysta z jednej reguły

**W IDE:** w `Refund` ręcznie zamień `PaidFor(ticket)` na `TicketPrice(ticket)`, uruchom test, potem Safe Delete (⌘⌦) na `PaidFor`.
**Po:**

```csharp
var paid = TicketPrice(ticket);
```

**Uruchom:** test zielony - to dowód, że obie kopie liczyły to samo dla wszystkich przypadków (w tym zniżki, poranek i zwrot po starcie).
**Co powiedzieć:** Substitute Algorithm jest bezpieczny tylko pod testem równoważności. Gdyby kopie się rozjechały, test pokazałby, która reguła jest "prawdziwa" - i to byłaby rozmowa z biznesem, nie decyzja programisty.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s01 2 3`

### Krok 4: Extract Class - autorytatywne źródło wiedzy

**W IDE:** na `TicketPrice` ⌃T > Extract Class, klasa `TicketPrice`, metodę po przeniesieniu nazwij `Of` (⇧F6). W nowej klasie ⌥⌘M dla `BasePrice` i `DiscountPercent`, ⌥⌘C dla `MorningDiscount`; w `BoxOffice` ⌥⌘C dla `RefundDeduction`.
**Po:**

```csharp
public sealed class BoxOffice
{
    private const decimal RefundDeduction = 3.00m;

    private readonly TicketPrice _ticketPrice = new();

    public decimal Sell(Ticket ticket)
    {
        return _ticketPrice.Of(ticket);
    }
```

**Uruchom:** test zielony.
**Co powiedzieć:** reguła ceny ma nazwę w języku problemu i jednego właściciela (cennik). `BoxOffice` zna już tylko własną wiedzę: regulamin zwrotów.
**Snapshot:** `Step4/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s01 3 4`

### Rozwiązanie i uzasadnienie

`Step4/TicketPrice` to jedyna reprezentacja wiedzy "ile kosztuje bilet". Scalenie było uzasadnione od razu, bez czekania na trzecią kopię: dwie kopie tej samej reguły to już ryzyko niezgodnej zmiany (slajd 2.8: reguła trzech pomaga czekać na kształt abstrakcji, ale nie definiuje DRY).

### Pułapki

- Szukanie duplikacji po tekście: IDE nie znalazło tej kopii, bo była inaczej napisana.
- Scalanie bez testu równoważności - jeśli kopie już się różniły, "cicho" zmieniamy zachowanie.
- Przeniesienie do `TicketPrice` także reguł zwrotu - to inna wiedza z innym właścicielem.

### Pytanie do sali

Gdzie w waszym systemie ta sama reguła żyje w SQL, w kodzie i w dokumentacji jednocześnie? Co jest jej autorytatywnym źródłem?

## Scena s02. Podobieństwo to nie duplikacja

**Temat ze slajdów:** 2.2 Podobieństwo nie wystarcza; 2.3 Tymczasowe powtórzenie kodu bywa bezpiecznym etapem
**Namespace:** `Training.Workshop.M3.S02Similarity` · **Test:** `scripts/warsztat.sh --lang cs test m3/s02` (`S02EquivalenceTest`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** Statyczna klasa `ServiceFee` scala opłatę rezerwacyjną online i potrącenie przy zwrocie tylko dlatego, że obie wyglądają jak "kwota razy liczba sztuk". Wklejamy wspólny kod z powrotem do obu wywołujących, usuwamy martwe gałęzie i dajemy każdej kwocie stałą u jej właściciela.

**Zasada:** Podobny kod to jeszcze nie ta sama wiedza - rozstrzyga pytanie, czy dwie reguły zmieniają się razem i z tego samego powodu. Wspólna metoda dla niezależnych reguł tworzy fałszywą zależność, a chwilowe powtórzenie kodu jest bezpiecznym etapem ich rozdzielania.

**Efekt:** Marketing może zmienić opłatę rezerwacyjną bez ryzyka dla regulaminu zwrotów, a parametr `units = 1` znika. Płacimy za to dwiema podobnie wyglądającymi linijkami, co jest w porządku, bo kodują różną wiedzę.

**Różnica względem Javy:** po Inline Method w kroku 1 porównanie `ServiceFee.Kind.OnlineBooking == ServiceFee.Kind.OnlineBooking` to w C# ostrzeżenie CS1718 ("porównanie z tą samą zmienną"), a przy ostrzeżeniach traktowanych jak błędy - błąd kompilacji. Snapshot kroku 1 otacza tę linię `#pragma warning disable/restore CS1718` z komentarzem, że ślad znika w kroku 2. Warto to pokazać jako zaletę: kompilator C# sam wskazuje martwą gałąź do uproszczenia.

### Co widzimy

Ktoś "zDRYował" dwie reguły, bo wyglądały tak samo ("stała kwota razy liczba sztuk"): opłatę rezerwacyjną online (2.00 za bilet, właściciel: sprzedaż online i marketing) i potrącenie przy zwrocie (3.00 za zwrot, właściciel: regulamin zwrotów). Powstała wspólna metoda z przełącznikiem, a `RefundDesk` musi podać `units = 1`, choć ten parametr pasuje tylko drugiej regule.

```csharp
public static decimal Of(Kind kind, int units)
{
    var perUnit = kind == Kind.OnlineBooking ? 2.00m : 3.00m;
    return Math.Round(perUnit * units, 2, MidpointRounding.AwayFromZero);
}
```

### Krok 1: Inline Method - wspólny kod wraca do wywołujących

**W IDE:** na `ServiceFee.Of` ⌥⌘N (Inline Method), zaznacz inline wszystkich użyć i usunięcie deklaracji. Zostaje samo `enum Kind` w statycznej klasie `ServiceFee`.
**Po:**

```csharp
var perUnit = ServiceFee.Kind.Refund == ServiceFee.Kind.OnlineBooking
    ? 2.00m : 3.00m;
var fee = Math.Round(perUnit * 1, 2, MidpointRounding.AwayFromZero);
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m3/s02` - 16 testów zielonych.
**Co powiedzieć:** chwilowo mamy więcej powtórzonego kodu - i to jest w porządku. Powtórzenie kodu to bezpieczny etap rozdzielania fałszywie scalonej wiedzy. W `OnlineCheckout` kompilator od razu krzyczy CS1718 - sam pokazuje, że warunek jest martwy.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s02 0 1`

### Krok 2: Simplify - usuń martwe gałęzie

**W IDE:** na warunkach porównujących stałe enuma ⌥⏎ (Rider podświetla porównanie jako zawsze prawdziwe lub fałszywe) albo ręcznie zostaw właściwą gałąź; usuń `#pragma` i mnożenie przez 1. Safe Delete (⌘⌦) na `ServiceFee`.
**Po:**

```csharp
return Math.Max(share - 3.00m, 0.00m);
```

**Uruchom:** test zielony.
**Co powiedzieć:** po uproszczeniu widać, że obie reguły nie miały ze sobą nic wspólnego poza kształtem wyrażenia.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s02 1 2`

### Krok 3: Extract Constant u właściciela reguły

**W IDE:** ⌥⌘C na `2.00m` w `OnlineCheckout` (nazwa `BookingFeePerTicket`) i na `3.00m` w `RefundDesk` (nazwa `RefundDeduction`).
**Po:**

```csharp
private const decimal BookingFeePerTicket = 2.00m;  // OnlineCheckout
private const decimal RefundDeduction = 3.00m;      // RefundDesk
```

**Uruchom:** test zielony.
**Co powiedzieć:** każda stała ma nazwę w języku domeny i mieszka u swojego właściciela. Marketing może znieść opłatę rezerwacyjną w promocji, nie dotykając regulaminu zwrotów.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s02 2 3`

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
**Namespace:** `Training.Workshop.M3.S03FalseAbstraction` · **Test:** `scripts/warsztat.sh --lang cs test m3/s03` (`S03EquivalenceTest`)
**Czas:** ~8 min

### W skrócie

**Co robimy:** `Pricing.Price` obsługuje bilety i karnety przez pięć parametrów, w tym flagi `bool`, z których każdy wywołujący potrzebuje tylko części. Cofamy abstrakcję do obu kas, upraszczamy martwe gałęzie i sprawdzamy, czy zostało coś wspólnego.

**Zasada:** Fałszywa abstrakcja to wspólny kod, który łączy konteksty bez wspólnej wiedzy i rośnie przez kolejne flagi. Kolejność naprawy: zabezpiecz testami, przenieś kod z powrotem do kontekstów, a wydziel tylko tę wiedzę, która się potwierdzi.

**Efekt:** Zostają dwie proste klasy, a `PassCounter` nie wie nic o formatach i okularach. Wspólnej wiedzy nie było wcale, więc świadomie niczego nowego nie wydzielamy.

### Co widzimy

`Pricing.Price` wycenia bilety i karnety (reguła sceny: karnet to 20.00 za wejście na seans 2D), sterowana flagami `bool`. Kasa biletowa podaje `quantity = 1` i `pass = false`, kasa karnetów podaje format, poranek i okulary "na wszelki wypadek".

```csharp
public decimal Price(string format, int quantity, bool pass,
                     bool morning, bool ownGlasses)
{
    decimal unit;
    if (pass)
    {
        unit = 20.00m;
    }
    else { /* format, poranek */ }
    if (format == "3D" && !ownGlasses && !pass) { /* okulary */ }
```

```csharp
return _pricing.Price("2D", entries, true, false, true);   // PassCounter
```

### Krok 1: Inline Method do obu wywołujących

**W IDE:** na `Pricing.Price` ⌥⌘N, inline wszystkich użyć i usunięcie metody. Jeśli Rider wstawi stałe argumenty wprost do wyrażeń, wprowadź dla nich zmienne lokalne (⌥⌘V) - snapshot ma je jako `var pass = false;`, żeby następny krok był czytelny. Usuń pustą klasę `Pricing` i pole `_pricing`.
**Po:**

```csharp
public decimal Ticket(string format, bool morning, bool ownGlasses)
{
    var pass = false;
    decimal unit;
    if (pass)
    {
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m3/s03` - 18 testów zielonych.
**Co powiedzieć:** najpierw cofamy abstrakcję, dopiero potem myślimy, czy jest w tym kodzie coś naprawdę wspólnego.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s03 0 1`

### Krok 2: Inline Variable i Simplify - martwe gałęzie znikają

**W IDE:** ⌥⌘N na `pass`, `morning`, `ownGlasses`, `format` w `PassCounter` (i na `pass` w `TicketCounter`), potem ⌥⏎ na warunkach ze stałymi (Rider podpowiada uproszczenie wyrażenia zawsze prawdziwego lub fałszywego i usunięcie martwego kodu). Na koniec ⌥⌘C dla stałych kwot.
**Po:**

```csharp
public decimal Pass(int entries)
{
    return PricePerEntry * entries;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** po rozdzieleniu okazuje się, że wspólnej wiedzy nie ma wcale - karnet nie wie nic o formatach i okularach. Nie wydzielamy nic nowego.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s03 1 2`

### Rozwiązanie i uzasadnienie

Dwie proste klasy zamiast jednej metody z pięcioma parametrami. Kolejność ze slajdu 2.3: testy (tu równoważności) - kod z powrotem do kontekstów - wydzielenie tylko potwierdzonej wiedzy (tu: żadnej).

### Pułapki

- Dokładanie kolejnej flagi do fałszywej abstrakcji "bo już jest wspólna metoda".
- Rozdzielanie bez testów: przy flagach łatwo pomylić kolejność argumentów `bool` (argumenty nazwane, np. `ownGlasses: true`, łagodzą problem, ale go nie usuwają).

### Pytanie do sali

Ile flag `bool` ma najdłuższa metoda w waszym projekcie? Ilu wywołujących używa każdej kombinacji?

## Scena s04. DRY w testach i niezależna wyrocznia

**Temat ze slajdów:** 2.3 DRY w testach: wspólne fabryki danych tak, liczenie oczekiwanej wartości algorytmem produkcyjnym nie
**Namespace:** `Training.Workshop.M3.S04DryTests` · **Test:** `scripts/warsztat.sh --lang cs test m3/s04` (`S04SpecsTest`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** Specyfikacja cen `TicketPriceSpecs` ma przypadki zaszyfrowane w napisach i liczy oczekiwaną cenę tym samym wzorem co produkcja. Najpierw nadajemy przypadkom czytelne nazwy, potem zastępujemy wyliczanie oczekiwania kwotami policzonymi ręcznie z regulaminu.

**Zasada:** DRY w testach obejmuje fabryki danych i wspólne helpery, ale nie oczekiwaną wartość: test liczący ją algorytmem produkcyjnym traci niezależną wyrocznię. DAMP oznacza, że każdy przypadek czyta się jak zdanie z regulaminu, nawet kosztem pewnego powtórzenia.

**Efekt:** Specyfikacja łapie błędną zniżkę studencką i mówi, który przypadek, jaka kwota oczekiwana i jaka faktyczna. Koszt: przy zmianie cennika kwoty w przykładach trzeba przeliczyć ręcznie - i to jest zamierzone.

**Różnica względem Javy:** `Tariff` to `sealed record` ze słownikami `IReadOnlyDictionary`, kopiowanymi w inicjatorach właściwości (odpowiednik `Map.copyOf`), a `Run` zwraca `IReadOnlyList<string>`.

### Co widzimy

Kod produkcyjny sceny (`Tariff`, `TicketPrice`) jest stabilny. Refaktoryzujemy specyfikację cen `TicketPriceSpecs` - w projekcie byłaby to klasa testowa xUnit, tu leży w projekcie `Training.Workshop`, żeby działał mechanizm Start/StepN. Metoda `Run` zwraca listę niespełnionych przypadków. Start jest "maksymalnie DRY": przypadki zaszyfrowane w stringach, a oczekiwana cena liczona z tej samej taryfy i tym samym wzorem co produkcja.

```csharp
private static readonly IReadOnlyList<string> Specs = ["IMAX N 20", "3D S 11", "2D E 18", "2D C 10"];
...
var @base = price.Tariff.BasePrices[p[0]];
var discount = Math.Round(@base * price.Tariff.DiscountPercents[type] / 100, 2, MidpointRounding.AwayFromZero);
var expected = @base - discount - (start.Hour < 12 ? 5.00m : 0m);
```

### Krok 1: DAMP - nazwane przykłady zamiast szyfru

**W IDE:** ręcznie: zamień pętlę po `Specs` na cztery wywołania `Check(price, "student na porannym 3D", "3D", "STUDENT", new TimeOnly(11, 0), failures)`. Usuń parsowanie stringa. Wyliczenie oczekiwania wydziel ⌥⌘M jako `ExpectedFromTariff`. Komunikat błędu: nazwa przykładu, oczekiwana i faktyczna cena (formatowana `"0.00"` w kulturze niezmiennej).
**Po:**

```csharp
Check(price, "student na porannym 3D", "3D", "STUDENT", new TimeOnly(11, 0), failures);
...
var expected = ExpectedFromTariff(price.Tariff, format, type, start);
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m3/s04` - 5 testów zielonych; test `Step1IsReadableButStillBlindToTheBug` pokazuje, że z błędną taryfą (zniżka studencka 20% zamiast 25%) krok 1 nadal nic nie zgłasza.
**Co powiedzieć:** czytelność poprawiona, ale test wciąż jest kopią algorytmu. Jeśli algorytm jest zły, test jest zły tak samo.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s04 0 1`

### Krok 2: Jawna oczekiwana wartość - niezależna wyrocznia

**W IDE:** Change Signature (⌘F6) na `Check`: nowy parametr `string expected`, wpisz ręcznie policzone kwoty (40.00, 19.00, 17.50, 10.00). Safe Delete (⌘⌦) na `ExpectedFromTariff`.
**Po:**

```csharp
Check(price, "student na porannym 3D", "3D", "STUDENT", new TimeOnly(11, 0), "19.00", failures);
```

**Uruchom:** test zielony; `Step2CatchesBrokenStudentDiscount` pokazuje komunikat `student na porannym 3D: oczekiwano 19.00, jest 20.60`.
**Co powiedzieć:** wspólny helper `Check` zostaje - współdzielenie fabryki przypadku i formatu komunikatu to dobre DRY w testach. Wyrocznia musi być niezależna od kodu, który sprawdza.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s04 1 2`

### Rozwiązanie i uzasadnienie

DAMP (Descriptive And Meaningful Phrases): każdy przypadek czyta się jak zdanie z regulaminu, oczekiwanie pochodzi z regulaminu, nie z taryfy. Kwoty policz na sali: 32.00 - 25% = 24.00, rano -5.00 = 19.00. W prawdziwym projekcie xUnit te same przykłady byłyby wierszami `[InlineData("student na porannym 3D", "3D", "STUDENT", 11, "19.00")]` - nazwa przypadku i jawna kwota w jednym wierszu.

### Pułapki

- "Test sprawdza, czy kod robi to, co robi" - oczekiwanie liczone kodem produkcyjnym lub jego kopią.
- Przesadne DRY w testach: jeden parametryzowany helper z szyfrem zamiast czytelnych przykładów.
- Odwrotna skrajność: kopiowanie całego przygotowania danych w każdym teście zamiast fabryki.

### Pytanie do sali

W którym waszym teście oczekiwana wartość jest wyliczana, a nie wpisana? Co by się stało, gdyby wzór był zły?

## Scena s05. KISS - złożoność wprowadzona kontra istotna

**Temat ze slajdów:** 2.4-2.5 KISS: prostota po poprawności; ukryty przepływ przez refleksję
**Namespace:** `Training.Workshop.M3.S05Kiss` · **Test:** `scripts/warsztat.sh --lang cs test m3/s05` (`S05EquivalenceTest`)
**Czas:** ~8 min

### W skrócie

**Co robimy:** `SeatCounter` wybiera rodzaj miejsc napisem i refleksją, a wolne miejsca liczy wyrażeniem regularnym. Zastępujemy to dwiema jawnymi metodami i zwykłą pętlą po znakach.

**Zasada:** KISS to najmniej złożone rozwiązanie, które poprawnie realizuje aktualne wymagania. Celuje w złożoność wprowadzoną (refleksja, sprytne mechanizmy), a złożoność istotną zostawia, tylko ją nazywa - nie myl go z najmniejszą liczbą linii.

**Efekt:** Literówka w rodzaju miejsc nie przejdzie kompilacji, a Find Usages znów pokazuje przepływ. Reguły istotne, jak miejsce zablokowane czy początek strefy VIP, zostają i pilnuje ich test.

**Różnica względem Javy:** refleksja szuka prywatnej metody instancji `kind + "Rows"` z wielką literą (`"all"` -> `AllRows`, `"vip"` -> `VipRows`) przez `GetMethod(..., BindingFlags.NonPublic | BindingFlags.Instance)`; brak metody daje `InvalidOperationException("nieznany rodzaj miejsc: ...")`. Strumienie Javy to LINQ (`Enumerable.Range`, `SelectMany`, `Regex.Matches`), a `subList` to `Skip(...).ToList()`.

### Co widzimy

`SeatCounter` liczy wolne miejsca w sali (`'.'` wolne, `'X'` zajęte, `'B'` zablokowane, `' '` przejście) oraz wolne miejsca VIP. Rodzaj miejsc wybierany jest stringiem i refleksją, a liczenie idzie przez wyrażenie regularne z nazwaną grupą. Analizator zgłasza `AllRows` i `VipRows` jako nieużywane (IDE0051), więc Start musi je uciszać atrybutem `[SuppressMessage(..., Justification = "wołane refleksją")]` - to już sygnał, że przepływ jest ukryty.

```csharp
var name = char.ToUpperInvariant(kind[0]) + kind[1..] + "Rows";
var rows = GetType().GetMethod(name, BindingFlags.NonPublic | BindingFlags.Instance)
    ?? throw new InvalidOperationException("nieznany rodzaj miejsc: " + kind);
var selected = (IEnumerable<string>)rows.Invoke(this, [hall])!;
return selected.SelectMany(row => Free.Matches(row).Select(m => m.Groups["seat"].Value)).LongCount();
```

### Krok 1: Replace Parameter with Explicit Methods

**W IDE:** ręcznie: zamiast `FreeSeats(hall, "all")` i `FreeSeats(hall, "vip")` utwórz `FreeSeats(hall)` i `FreeVipSeats(hall)`, które wołają wspólne `CountFree(IEnumerable<string>)`. Usuń refleksję, `using System.Reflection` i atrybuty `[SuppressMessage]`.
**Po:**

```csharp
public string Summary(Hall hall)
{
    return "wolne: " + FreeSeats(hall) + ", wolne VIP: " + FreeVipSeats(hall);
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m3/s05` - 12 testów zielonych.
**Co powiedzieć:** literówka w `"vip"` wybuchała dopiero w runtime; teraz nie przejdzie kompilacji. Przepływ widać w IDE (⌥F7 Find Usages znów działa), a atrybut uciszający analizator nie jest już potrzebny.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s05 0 1`

### Krok 2: Substitute Algorithm - zwykłe pętle

**W IDE:** ręcznie zastąp sekwencję indeksów przez `Skip` od pierwszego rzędu VIP (z `Math.Clamp` do zakresu), a regex przez pętlę po znakach porównującą ze stałą `Free = '.'`.
**Po:**

```csharp
private static int FreeIn(IReadOnlyList<string> rows)
{
    var free = 0;
    foreach (var row in rows)
    {
        foreach (var seat in row)
        {
            if (seat == Free)
            {
                free++;
            }
        }
    }
    return free;
}
```

**Uruchom:** test zielony, także dla rzędu z `'B'` i przejściem.
**Co powiedzieć:** złożoność istotna zostaje (co to jest wolne miejsce, od którego rzędu VIP, obcięcie `VipFromRow` do zakresu), ale jest nazwana. Zniknęła tylko złożoność wprowadzona.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s05 1 2`

### Rozwiązanie i uzasadnienie

KISS to najmniej złożone rozwiązanie, które **poprawnie** realizuje wymagania. Test z zablokowanym miejscem i przejściem pilnuje, że uproszczenie nie zgubiło przypadku.

### Pułapki

- Uproszczenie, które pomija przypadek brzegowy (np. `'B'` liczone jako wolne) - "prostsze, ale błędne".
- Mylenie KISS z najmniejszą liczbą linii: jednolinijkowy regex (albo łańcuch LINQ) jest krótszy, ale trudniejszy.

### Pytanie do sali

Jaki "sprytny" mechanizm (refleksja, atrybuty, konwencje nazw) w waszym kodzie ukrywa przepływ sterowania? Czy daje coś, czego nie da zwykłe wywołanie?

## Scena s06. YAGNI - silnik reguł dla dwóch reguł

**Temat ze slajdów:** 2.6-2.7 YAGNI i czego nie zabrania; 2.8 Napięcia (ogólny silnik hipotetycznych taryf); 2.9 Filtr decyzyjny
**Namespace:** `Training.Workshop.M3.S06Yagni` · **Test:** `scripts/warsztat.sh --lang cs test m3/s06` (`S06EquivalenceTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `TicketPricer` to silnik z rejestrem pluginów, konfiguracją napisem i kontekstem `IDictionary` - dla dokładnie dwóch reguł. W trzech krokach usuwamy rejestr, zastępujemy słownik typowanymi danymi i wklejamy reguły do jednej klasy.

**Zasada:** YAGNI mówi, żeby nie budować zdolności potrzebnej tylko dla przewidywanego wymagania. Nie zabrania testów, refaktoryzacji ani nazwanych metod - to one pozwalają bezpiecznie odłożyć abstrakcję do chwili, gdy pojawi się realna potrzeba.

**Efekt:** Z pięciu typów zostaje jedna klasa z dwiema nazwanymi regułami, a literówka w nazwie reguły nie skompiluje się. Gdy przyjdzie trzecia reguła z innym właścicielem, abstrakcję trzeba będzie wprowadzić ponownie.

**Różnica względem Javy:** kontekst to `IDictionary<string, object>` z rzutowaniami `(TimeOnly)` i `(int)`, fabryki pluginów to `Func<IPricingRule>`, a priorytet reguły to właściwość `Priority`.

### Co widzimy

`TicketPricer` to spekulatywny silnik: rejestr pluginów, konfiguracja napisem `"morning,vip"`, priorytety i kontekst `IDictionary<string, object>`. Obsługuje dokładnie dwie reguły (poranek -5.00, VIP +10.00), które nie zależą od kolejności.

```csharp
var context = new Dictionary<string, object>
{
    ["format"] = quote.Format,
    ["start"] = quote.Start,
    ["row"] = quote.Row,
    ["vipFromRow"] = quote.VipFromRow,
};
var price = BasePrice(quote.Format);
foreach (var rule in _registry.Resolve(_activeRules))
{
    if (rule.AppliesTo(context))
    {
        price = rule.Apply(context, price);
    }
}
```

Na żywo: zmień w konstruktorze `"morning,vip"` na `"morning,vlp"` i uruchom test - błąd konfiguracji wychodzi dopiero w runtime (`ArgumentException: brak reguly: vlp`). Cofnij zmianę.

### Krok 1: Inline Class - rejestr pluginów znika

**W IDE:** w `TicketPricer` zastąp pola `_registry` i `_activeRules` listą `[new MorningRule(), new VipRule()]` (ręcznie - Rider nie ma Inline Class; oba konstruktory usuń). Safe Delete (⌘⌦) na `RuleRegistry` i na właściwości `Priority` w interfejsie (była potrzebna tylko do sortowania).
**Po:**

```csharp
private readonly IReadOnlyList<IPricingRule> _rules = [new MorningRule(), new VipRule()];
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m3/s06` - 16 testów zielonych.
**Co powiedzieć:** konfiguracja napisem nie miała żadnego klienta. Teraz literówka w nazwie reguły nie skompiluje się.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s06 0 1`

### Krok 2: Change Signature - typowane dane zamiast słownika

**W IDE:** ⌘F6 na `IPricingRule.AppliesTo`: typ parametru `IDictionary<string, object>` -> `TicketQuote`; na `Apply` usuń parametr kontekstu. Popraw implementacje (rzutowania znikają), usuń budowanie słownika w `TicketPricer`.
**Po:**

```csharp
public bool AppliesTo(TicketQuote quote)
{
    return quote.Row >= quote.VipFromRow;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** generyczny kontekst to typowa spekulacja "reguła może potrzebować czegokolwiek". Kompilator znów pilnuje nazw pól.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s06 1 2`

### Krok 3: Inline Class dla reguł, Safe Delete interfejsu

**W IDE:** ręcznie (Rider nie ma Inline Class): dwa `if` w `Price` z metodami `IsMorning` i `IsVip` - ciała wklej z `AppliesTo` i `Apply` obu reguł. Safe Delete na `MorningRule`, `VipRule` i `IPricingRule`. Stałe przez ⌥⌘C.
**Po:**

```csharp
public decimal Price(TicketQuote quote)
{
    var price = BasePrice(quote.Format);
    if (IsMorning(quote))
    {
        price -= MorningDiscount;
    }
    if (IsVip(quote))
    {
        price += VipSurcharge;
    }
    return price;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** YAGNI nie zabrania testów, nazwanych metod ani szwu testowego - cena liczona jest z danych wejściowych, bez zegara i stanu statycznego, więc każdą regułę łatwo sprawdzić. Gdy pojawi się trzecia reguła z innym właścicielem, wydzielimy abstrakcję wtedy.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s06 2 3`

### Rozwiązanie i uzasadnienie

Przejdź z salą filtr ze slajdu 2.9: aktualne wymaganie (2 reguły), nazwa w języku problemu (brak: "plugin", "registry"), czy upraszcza aktualny przypadek (nie), koszt usunięcia (niski - pokazaliśmy). Z pięciu typów została jedna klasa.

### Pułapki

- "YAGNI, więc bez testów" - odwrotnie: testy i małe kroki pozwalają bezpiecznie odraczać decyzje.
- Usunięcie abstrakcji, która chroni aktualną, realną granicę (np. port do bazy) - to nie spekulacja.

### Pytanie do sali

Który mechanizm rozszerzeń w waszym systemie ma dziś jedną albo dwie implementacje? Jaki sygnał powiedziałby, że czas go wprowadzić ponownie?

## Scena s07. SRP - raport dla dwóch aktorów

**Temat ze slajdów:** 3.2 SRP: jeden aktor zmiany; 3.7 Błędne uproszczenia SOLID ("klasa robi jedną rzecz")
**Namespace:** `Training.Workshop.M3.S07Srp` · **Test:** `scripts/warsztat.sh --lang cs test m3/s07` (`S07EquivalenceTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `DailyReport` składa raport dla księgowości i marketingu, a wspólny helper `Revenue` sprawia, że zmiana dla jednego aktora cicho zmienia kwoty drugiego. Wydzielamy sekcje, rozdzielamy helper i tworzymy klasę na każdego aktora.

**Zasada:** SRP mówi, że moduł powinien odpowiadać przed jednym aktorem, czyli mieć jeden powód zmiany. To nie jest "klasa robi jedną rzecz" ani jedna metoda na klasę - klasa może mieć kilka metod, jeśli wszystkie zmieniają się dla tego samego aktora.

**Efekt:** Zmiana definicji hitu dotyka tylko `MarketingSection`, a zmiana stawki VAT tylko `AccountingSection`. Koszt: dwie identyczne dziś formuły, powtórzone świadomie, bo to kod, a nie wspólna wiedza.

**Różnica względem Javy:** `TreeMap` to `SortedDictionary<string, decimal>(StringComparer.Ordinal)`, a `merge` to `GetValueOrDefault(...) + ...`. Klasy sekcji w kroku 3 są `internal` (odpowiednik widoczności pakietowej).

### Co widzimy

`DailyReport.Render` składa raport dla księgowości (przychód brutto i netto wg VAT 8% i 23%) i dla marketingu (hit dnia, liczba biletów). Obie części korzystają ze wspólnego helpera `Revenue`. Gdy marketing poprosi "hit dnia licz bez baru", poprawka helpera po cichu zmieni też "Razem brutto" dla księgowości.

```csharp
private static decimal Revenue(Sale sale)
{
    return sale.TicketRevenue + sale.BarRevenue;
}
...
total += Revenue(sale);                                                     // księgowość
byTitle[sale.Title] = byTitle.GetValueOrDefault(sale.Title) + Revenue(sale); // marketing
```

### Krok 1: Extract Method według aktora

**W IDE:** zaznacz blok pod `// księgowość`, ⌥⌘M `AccountingSection` (zwraca `string`, własny `StringBuilder`); to samo dla `// marketing` jako `MarketingSection`.
**Po:**

```csharp
public string Render(IReadOnlyList<Sale> sales)
{
    return AccountingSection(sales) + MarketingSection(sales);
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m3/s07` - 12 testów zielonych (w tym remis tytułów i dzień bez sprzedaży).
**Co powiedzieć:** komentarze nazywały aktorów - teraz nazywają je metody. Helper `Revenue` nadal wiąże oba światy.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s07 0 1`

### Krok 2: Rozdziel wspólny helper według aktora

**W IDE:** zaznacz metodę `Revenue` i zduplikuj ją (⌘D), nazwij kopię `Popularity` (⇧F6), w `MarketingSection` użyj `Popularity`. Dodaj komentarz XML (`/// <summary>`) z właścicielem każdej metody.
**Po:**

```csharp
/// <summary>Księgowość: przychód brutto seansu.</summary>
private static decimal Revenue(Sale sale) { ... }

/// <summary>Marketing: miara popularności filmu (dziś: bilety + bar).</summary>
private static decimal Popularity(Sale sale) { ... }
```

**Uruchom:** test zielony.
**Co powiedzieć:** świadomie powtarzamy kod, nie wiedzę - dziś formuły są równe przypadkiem. To lekcja z s02 w kontekście SRP.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s07 1 2`

### Krok 3: Extract Class - klasa na aktora

**W IDE:** ⌃T > Extract Class dla `AccountingSection`, `Revenue`, `Net` -> klasa `AccountingSection`; to samo dla `MarketingSection`, `Popularity` -> `MarketingSection`. Metody nazwij `Render`, klasy oznacz `internal`.
**Po:**

```csharp
public sealed class DailyReport
{
    private readonly AccountingSection _accounting = new();
    private readonly MarketingSection _marketing = new();

    public string Render(IReadOnlyList<Sale> sales)
    {
        return _accounting.Render(sales) + _marketing.Render(sales);
    }
}
```

**Uruchom:** test zielony - dokument identyczny co do znaku.
**Co powiedzieć:** `DailyReport` koordynuje, ale nie zna polityk. Zmiana definicji hitu dotyka tylko `MarketingSection`, zmiana stawki VAT tylko `AccountingSection`.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s07 2 3`

### Rozwiązanie i uzasadnienie

Odpowiedzialność = aktor i jego powód zmiany, nie "jedna rzecz". `AccountingSection` ma trzy metody i to jest w porządku: wszystkie zmieniają się dla księgowości.

### Pułapki

- Dzielenie na klasy "po jednej metodzie" zamiast według aktora.
- Zostawienie wspólnego helpera między nowymi klasami (np. w statycznej klasie `ReportUtils` albo metodzie rozszerzającej) - powrót do problemu.

### Pytanie do sali

Kto w waszej organizacji zgłasza zmiany do największej klasy w systemie? Ilu to różnych aktorów?

## Scena s08. OCP na wybranej osi - formaty seansu

**Temat ze slajdów:** 3.3 OCP: zamknięcie dla wybranej osi; nie każdy `switch` narusza OCP
**Namespace:** `Training.Workshop.M3.S08Ocp` · **Test:** `scripts/warsztat.sh --lang cs test m3/s08` (`S08EquivalenceTest`, `S08NewFormatTest`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** Wiedza o formatach seansu jest rozsiana po trzech `switch` na napisach, więc nowy format wymaga edycji wielu miejsc. Zamieniamy napisy na enum, przenosimy dane formatów do typu `Format` i dodajemy 4DX jedną linią.

**Zasada:** OCP to możliwość rozszerzenia wybranego zachowania bez modyfikowania stabilnej części. Zamyka się kod na jedną, realnie rosnącą oś zmian, nie na wszystkie naraz, a nie każdy `switch` łamie OCP - wyrażenie `switch` na enumie bez gałęzi `_` bywa dobrym modelem zamkniętego zbioru.

**Efekt:** Dodanie formatu to zmiana tylko w `Format`, a `ScreeningOffer` nie zmienia się ani o znak. Na inne osie, na przykład nową dopłatę, klasa nadal nie jest zamknięta - i nie musi.

**Różnica względem Javy:** enum C# nie niesie danych (nie ma pól ani konstruktora jak enum Javy). Krok 1 to zwykły `enum Format { TwoD, ThreeD, Imax }`, a parsowanie kodu jest statycznym członkiem rozszerzenia C# 14 (`extension(Format)` w klasie `FormatCodes`), więc wywołanie `Format.Parse(code)` wygląda jak w Javie. Żeby brak obsługi nowej stałej był błędem kompilacji, plik ma `#pragma warning disable CS8524` (ostrzeżenie o wartościach spoza enuma), a niepełny `switch` daje CS8509, traktowane jako błąd. W kroku 2 `Format` staje się `sealed class` ze statycznymi instancjami (`TwoD`, `ThreeD`, `Imax`), prywatnym konstruktorem i właściwościami `BasePrice`, `NeedsGlasses`, `Label` - diff kroku 2 zmienia więc rodzaj typu (enum -> klasa), co warto skomentować przy pokazie.

### Co widzimy

Wiedza o formatach (2D, 3D, IMAX) jest rozsiana po trzech `switch` na stringu: cena bazowa, okulary 3D, etykieta. Kino kupuje salę 4DX - trzeba edytować każdy `switch`, a zapomniany trafi do gałęzi `_`.

```csharp
var glasses = format switch
{
    "3D" => ownGlasses ? 0m : 3.00m,
    _ => 0m,
};
```

### Krok 1: Replace Type Code with Enum

**W IDE:** utwórz `enum Format { TwoD, ThreeD, Imax }` i statyczne `Format.Parse(string)` (blok `extension(Format)` w `FormatCodes`); w `ScreeningOffer` przełącz wyrażenia `switch` na enum, usuń gałęzie `_` i dodaj na początku pliku `#pragma warning disable CS8524` z komentarzem.
**Po:**

```csharp
var glasses = format switch
{
    Format.ThreeD => ownGlasses ? 0m : 3.00m,
    Format.TwoD or Format.Imax => 0m,
};
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m3/s08` - 18 testów zielonych.
**Co powiedzieć:** to jeszcze nie OCP, ale już bezpieczny, zamknięty zbiór: nowa stała enuma to błąd kompilacji (CS8509) w każdym `switch` bez gałęzi `_`. Dla małego, stabilnego zbioru to często wystarcza.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s08 0 1`

### Krok 2: Dane zamiast gałęzi - wiedza przeniesiona do typu formatu

**W IDE:** ręcznie zamień `enum Format` na `sealed class Format` ze statycznymi instancjami `TwoD`, `ThreeD`, `Imax`, prywatnym konstruktorem i właściwościami `BasePrice`, `NeedsGlasses`, `Label` (każda instancja dopisuje się w konstruktorze do listy `Values`, z której korzysta `Parse`). W `ScreeningOffer` zastąp każdy `switch` odczytem właściwości (Replace Conditional with Polymorphism w wersji "dane") i usuń `#pragma`.
**Po:**

```csharp
public decimal Price(string code, bool ownGlasses)
{
    var format = Format.Parse(code);
    var glasses = format.NeedsGlasses && !ownGlasses ? Glasses : 0m;
    return format.BasePrice + glasses;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** `ScreeningOffer` jest zamknięta na oś "format seansu". Nie jest zamknięta na inne osie (nowa dopłata za fotel premium ją zmieni) - i nie musi.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s08 1 2`

### Krok 3: Nowy format 4DX - jedna linia

**W IDE:** w `Format` dopisz `public static readonly Format FourDx = new("4DX", 45.00m, true, "4DX - ruchome fotele");`.
**Po:**

```csharp
public static readonly Format Imax = new("IMAX", 40.00m, false, "IMAX - ekran laserowy");
public static readonly Format FourDx = new("4DX", 45.00m, true, "4DX - ruchome fotele");
```

**Uruchom:** test zielony; `S08NewFormatTest` sprawdza 4DX: 48.00 bez własnych okularów, 45.00 z własnymi (a `Step2DoesNotKnow4dxYet` - że krok 2 jeszcze go nie zna).
**Co powiedzieć:** `scripts/warsztat.sh --lang cs diff m3/s08 2 3` pokazuje zmianę tylko w `Format` - `ScreeningOffer` nie zmieniła się ani o znak. To jest OCP: rozszerzenie wybranego zachowania bez modyfikacji stabilnej części.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s08 2 3`

### Rozwiązanie i uzasadnienie

Formaty to dane, więc typ ze stałymi instancjami i właściwościami jest prostszy niż hierarchia klas (w C# to najbliższy odpowiednik enuma Javy z polami). Polimorfizm klas opłaciłby się, gdyby formaty miały różne algorytmy (np. dynamiczną cenę 4DX), a nie różne liczby.

### Pułapki

- "Każdy `switch` łamie OCP" - `switch` na enumie bez gałęzi `_` (i z CS8509 jako błędem) to często dobry model zamkniętego zbioru.
- Próba zamknięcia klasy na wszystkie osie naraz: interfejsy dla dopłat, formatów, typów biletów "na zapas".
- Klasa ze stałymi instancjami, która dopisuje się do statycznej listy, zależy od kolejności inicjalizacji pól statycznych - lista `All` musi być zadeklarowana przed instancjami (w kodzie jest o tym komentarz).

### Pytanie do sali

Która oś zmian w waszym systemie rośnie najszybciej? Czy kod jest zamknięty właśnie na nią?

## Scena s09. LSP i test kontraktowy

**Temat ze slajdów:** 3.4 LSP: substytucja behawioralna; `NotSupportedException` nie zawsze łamie LSP - ocena zaczyna się od kontraktu
**Namespace:** `Training.Workshop.M3.S09Lsp` · **Test:** `scripts/warsztat.sh --lang cs test m3/s09` (`S09EquivalenceTest`, `S09ContractTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `ReadOnlyHall` dziedziczy po `Hall`, ale na `Reserve` rzuca wyjątek, więc łamie kontrakt sali bazowej. Wydzielamy interfejs odczytu `ISeatMap` i zastępujemy dziedziczenie delegacją.

**Zasada:** LSP wymaga, by podtyp dało się podstawić za typ bazowy bez zmiany zachowania: nie może wzmacniać warunków wstępnych ani osłabiać końcowych. Kompilator tego nie sprawdzi, dlatego ten sam test kontraktowy uruchamia się dla każdej implementacji, a `NotSupportedException` ocenia się względem kontraktu typu bazowego.

**Efekt:** Sala archiwalna spełnia tylko kontrakt odczytu, który naprawdę obiecuje, a przekazanie jej do kasy kończy się błędem kompilacji zamiast wyjątkiem w runtime. Test daje dowody zgodności dla sprawdzonych stanów, nie formalny dowód.

**Różnica względem Javy:** w C# metody nie są domyślnie wirtualne, więc w Start (i w kroku 1) `Hall.Reserve` jest `virtual`, żeby `ReadOnlyHall` mógł go nadpisać (`override`) - już samo słowo `virtual` na metodzie z twardym kontraktem to sygnał do rozmowy. W kroku 2 `Hall` jest `sealed`, a `Reserve` niewirtualne. `freeSeats()`/`capacity()` to właściwości `FreeSeats`/`Capacity`, a `@TestFactory` z mapą nazwanych przypadków to `[Theory]` z `[MemberData]` po kluczach słownika fabryk.

### Co widzimy

`Hall` ma udokumentowany kontrakt `Reserve`: wolne miejsce zostaje zajęte, `FreeSeats` maleje o 1, zajęte miejsce daje `InvalidOperationException`. Kontrakt nie przewiduje odmowy. `ReadOnlyHall : Hall` (plan zamkniętego seansu dla raportów) nadpisuje `Reserve` rzucając `NotSupportedException` - wzmacnia warunek wstępny do "nigdy".

```csharp
public class ReadOnlyHall : Hall
{
    public override void Reserve(int seat)
    {
        throw new NotSupportedException("sala archiwalna - tylko do odczytu");
    }
}
```

Pokaż `S09ContractTest`: ten sam zestaw sprawdzeń (`ObeysReserveContract`) uruchamiany dla każdej implementacji (`EveryHallObeysTheReserveContract`). `ReadOnlyHallAsSubclassBreaksTheReserveContract` dokumentuje, że sala archiwalna go łamie.

### Krok 1: Extract Interface - rola odczytu

**W IDE:** na `Hall` ⌃T > Extract Interface, nazwa `ISeatMap`, członkowie `IsFree`, `FreeSeats`, `Capacity`. Potem na parametrze `OccupancyReport.Describe` ⌃T > Use Base Type where Possible (albo ręcznie zmień typ na `ISeatMap`, nazwa parametru `seats`).
**Po:**

```csharp
public interface ISeatMap
{
    bool IsFree(int seat);

    int FreeSeats { get; }

    int Capacity { get; }
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m3/s09` - 13 testów zielonych; kontrakt odczytu (`EverySeatMapObeysTheReadContract`) spełniają obie sale.
**Co powiedzieć:** sala archiwalna jest doskonałym `ISeatMap` - problemem nie jest ona, tylko dziedziczenie po typie, którego kontraktu nie spełnia.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s09 0 1`

### Krok 2: Replace Inheritance with Delegation

**W IDE:** Rider nie ma tej refaktoryzacji, robimy ją ręcznie: w `ReadOnlyHall` dodaj pole `private readonly Hall _snapshot`, zmień `: Hall` na `: ISeatMap`, wygeneruj członków delegujących (⌘N > Delegating Members, tylko członkowie `ISeatMap`), usuń `Reserve`. Konstruktor tworzy prywatną kopię i rezerwuje w niej zajęte miejsca. W `Hall` usuń chroniony konstruktor, `virtual` z `Reserve` i dodaj `sealed`.
**Po:**

```csharp
public sealed class ReadOnlyHall : ISeatMap
{
    private readonly Hall _snapshot;

    public ReadOnlyHall(int capacity, IReadOnlySet<int> taken)
    {
        _snapshot = new Hall(capacity);
        foreach (var seat in taken)
        {
            _snapshot.Reserve(seat);
        }
    }
```

**Uruchom:** test zielony. Spróbuj na żywo przekazać `ReadOnlyHall` do `BoxOffice.Sell` - kompilator odmówi.
**Co powiedzieć:** zamiast wyjątku w runtime mamy błąd kompilacji. Test kontraktowy `Hall` nie ma już komu się nie udać, bo sala archiwalna nie obiecuje rezerwacji.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s09 1 2`

### Rozwiązanie i uzasadnienie

Dwie role, dwa kontrakty, dwa testy kontraktowe. Test daje dowody zgodności dla sprawdzonych stanów, nie formalny dowód LSP. Alternatywa równie poprawna: osłabić kontrakt bazowy (np. `bool CanReserve` w kontrakcie - tak robi `ICollection<T>.IsReadOnly` w .NET) - wtedy `NotSupportedException` przestaje łamać LSP, ale każdy klient musi obsłużyć odmowę.

### Pułapki

- "Kompiluje się, więc jest podstawialne" - kompilator nie sprawdza kontraktów domenowych.
- Test kontraktowy uruchamiany tylko dla jednej implementacji.
- `if (hall is ReadOnlyHall)` w kasie jako "naprawa".

### Pytanie do sali

Które wasze klasy nadpisują metodę bazową tylko po to, żeby rzucić wyjątek? Jaki jest kontrakt typu bazowego i gdzie jest zapisany?

## Scena s10. ISP z perspektywy klienta

**Temat ze slajdów:** 3.5 ISP: interfejs według ról klientów; 4.2 Interfejs nie usuwa sprzężenia
**Namespace:** `Training.Workshop.M3.S10Isp` · **Test:** `scripts/warsztat.sh --lang cs test m3/s10` (`S10EquivalenceTest`, `S10ClientFakeTest`)
**Czas:** ~8 min

### W skrócie

**Co robimy:** Gruby `ICinemaAdminService` ma osiem metod, a każdy klient używa dwóch lub trzech - fake kasy musi implementować wszystkie. Wydzielamy interfejsy ról według klientów i usuwamy gruby interfejs.

**Zasada:** ISP mówi, że klient nie powinien zależeć od metod, których nie używa, więc interfejsy projektuje się według ról klientów. To nie znaczy "jedna metoda na interfejs" - rola może mieć kilka metod, jeśli jest spójna.

**Efekt:** Kasa zależy tylko od `ITicketSales`, a jej fake ma dwie metody zamiast ośmiu. Sprzężenie z zapleczem nie znika - staje się zależnością od węższego, stabilniejszego kontraktu.

**Różnica względem Javy:** fake'i w `S10ClientFakeTest` to prywatne klasy w teście (`FakeBackOffice` z sześcioma metodami rzucającymi `NotSupportedException`, `FakeTicketSales`) zamiast klas anonimowych.

### Co widzimy

Gruby `ICinemaAdminService` ma osiem metod. Kasa używa dwóch (`SellTicket`, `RefundTicket`), raport dwóch, tablica seansów trzech, zmianę ceny woła tylko konfiguracja. Fake do testu kasy musi implementować wszystkie osiem.

```csharp
public interface ICinemaAdminService
{
    string SellTicket(string title, int seat);

    string RefundTicket(string ticketId);

    decimal DailyRevenue();

    int TicketsSold(string title);

    void ScheduleScreening(string title, TimeOnly start);

    void CancelScreening(string title);

    IReadOnlyList<string> Screenings();

    void UpdateTicketPrice(decimal price);
}
```

### Krok 1: Extract Interface - po jednej roli na klienta

**W IDE:** na `ICinemaAdminService` trzy razy ⌃T > Extract Interface: `ITicketSales`, `ISalesFigures`, `IScreeningSchedule` (interfejs gruby dziedziczy po wszystkich trzech). W każdym kliencie zmień typ parametru konstruktora i pola na jego rolę (⌘F6 na konstruktorze albo ⌃T > Use Base Type where Possible).
**Po:**

```csharp
public interface ICinemaAdminService : ITicketSales, ISalesFigures, IScreeningSchedule
{
    void UpdateTicketPrice(decimal price);
}
```

```csharp
public sealed class CashDesk
{
    private readonly ITicketSales _backOffice;
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m3/s10` - 8 testów zielonych; `S10ClientFakeTest` porównuje fake grubego interfejsu (8 metod) z fake roli (2 metody).
**Co powiedzieć:** role nazwaliśmy z perspektywy klienta, nie implementacji. Zmiana harmonogramu nie dotyka już kasy.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s10 0 1`

### Krok 2: Safe Delete grubego interfejsu

**W IDE:** w `InMemoryBackOffice` zmień `: ICinemaAdminService` na `: ITicketSales, ISalesFigures, IScreeningSchedule` (`UpdateTicketPrice` zostaje zwykłą metodą publiczną klasy), Safe Delete (⌘⌦) na `ICinemaAdminService`.
**Po:**

```csharp
public sealed class InMemoryBackOffice : ITicketSales, ISalesFigures, IScreeningSchedule
{
```

**Uruchom:** test zielony.
**Co powiedzieć:** zmiana ceny nie dostała interfejsu - jej jedynym klientem jest konfiguracja, więc interfejs niczego by nie chronił.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s10 1 2`

### Rozwiązanie i uzasadnienie

Trzy spójne role, jedna implementacja. Interfejs nie usunął sprzężenia kasy z zapleczem - zastąpił je sprzężeniem z węższym, stabilniejszym kontraktem.

### Pułapki

- ISP jako "jedna metoda na interfejs" - `ITicketSales` ma dwie metody, bo to jedna spójna rola.
- Interfejs dla każdej klasy "bo mock" (Moq, NSubstitute) - łatwość mockowania nie dowodzi dobrego projektu.

### Pytanie do sali

Który wasz przypadek użycia dostaje generyczne `IRepository<T>` albo cały `DbContext` (albo podobny gruby typ), a używa jednej metody?

## Scena s11. DIP - kierunek zależności kontra przepływ sterowania

**Temat ze slajdów:** 3.6 DIP i DIP to nie dependency injection; 5.1-5.2 Reguła zależności a przepływ sterowania
**Namespace:** `Training.Workshop.M3.S11Dip` · **Test:** `scripts/warsztat.sh --lang cs test m3/s11` (`S11EquivalenceTest`, `S11DependencyDirectionTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** Przypadek użycia `ConfirmReservation` sam tworzy klienta SMTP i zna protokół, więc `using` i wywołania biegną `App -> Infra`. Wstrzykujemy zależność, nazywamy potrzebę polityki i wprowadzamy port `ICustomerNotifier` z adapterem w `Infra`.

**Zasada:** DIP mówi, że zależności źródłowe mają wskazywać od szczegółów ku polityce i abstrakcjom, a port nazywa potrzebę klienta, nie kształt technologii. DIP to nie dependency injection: wstrzyknięcie konkretnej klasy przez konstruktor wciąż wiąże politykę ze szczegółem.

**Efekt:** Sterowanie nadal płynie do `Infra`, ale zależność źródłowa odwróciła się na `Infra -> App`, a politykę da się przetestować ręcznym fake'iem. Wystarczyła ręczna statyczna klasa `Main` jako composition root, bez kontenera DI.

**Różnica względem Javy:** test kierunku zależności sprawdza refleksją przestrzeń nazw typów pól (`Namespace.EndsWith(".Infra")`) zamiast nazwy pakietu, a fake portu w `Step3PolicyIsTestableWithAHandWrittenFake` to prywatna klasa `FakeNotifier(Action<string, string>)` zamiast lambdy.

### Co widzimy

Przypadek użycia `App.ConfirmReservation` sam tworzy klienta `Infra.SmtpMailSender`, składa nagłówki MIME i interpretuje kody SMTP. `using` i przepływ sterowania biegną w tę samą stronę: `App -> Infra`. `Main` to composition root wariantu - test woła tylko `Main.ConfirmReservation()`.

```csharp
private readonly SmtpMailSender _mail = new("smtp.kino.pl", 25);
...
var mime = "To: " + reservation.Email + "\r\nSubject: Rezerwacja\r\n\r\n" + message;
var reply = _mail.Send(reservation.Email, mime);
if (!reply.StartsWith("250", StringComparison.Ordinal))
{
    throw new InvalidOperationException("SMTP odrzucil: " + reply);
}
```

### Krok 1: Introduce Parameter - dependency injection

**W IDE:** inicjalizator pola przenieś do konstruktora (⌥⏎ na polu, akcja przeniesienia inicjalizacji do konstruktora, albo ręcznie dopisz konstruktor), zaznacz w nim `new SmtpMailSender("smtp.kino.pl", 25)` i ⌥⌘P (Introduce Parameter). Rider przeniesie tworzenie obiektu do wywołania w `Main`.
**Po:**

```csharp
public ConfirmReservation(SmtpMailSender mail)
{
    _mail = mail;
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m3/s11` - 18 testów zielonych.
**Co powiedzieć:** to jest DI, ale jeszcze nie DIP - `using ...Infra;` nadal stoi w polityce. Test `Step1AndStep2PolicyDependsOnInfrastructure` to potwierdza.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s11 0 1`

### Krok 2: Extract Method - nazwij potrzebę polityki

**W IDE:** zaznacz składanie MIME, `Send` i sprawdzenie kodu, ⌥⌘M, nazwa `NotifyCustomer(string email, string message)`.
**Po:**

```csharp
NotifyCustomer(reservation.Email, message);
return "potwierdzono: " + reservation.Email;
```

**Uruchom:** test zielony.
**Co powiedzieć:** tak wygląda port, zanim stanie się interfejsem: potrzeba nazwana w języku problemu ("powiadom klienta"), a cała technologia w jednym miejscu. Nie robimy Extract Interface z `SmtpMailSender` - dostalibyśmy port `Send(to, mime)`, czyli kształt technologii, nie potrzeby.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s11 1 2`

### Krok 3: Port po stronie polityki, adapter w Infra

**W IDE:** ⌃T > Extract Class na `NotifyCustomer` i polu `_mail` -> klasa `SmtpCustomerNotifier`, potem F6 (Move to Folder) do `Infra`. Na nowej klasie ⌃T > Extract Interface `ICustomerNotifier` z metodą `NotifyCustomer` i przenieś interfejs (F6) do `App`. W `ConfirmReservation` typ pola i parametru -> `ICustomerNotifier`. W `Main` złóż `new ConfirmReservation(new SmtpCustomerNotifier(new SmtpMailSender(...)))`.
**Po:**

```csharp
public interface ICustomerNotifier                         // App
{
    void NotifyCustomer(string email, string message);
}

public sealed class SmtpCustomerNotifier : ICustomerNotifier   // Infra
{
```

**Uruchom:** test zielony; `StepsSendTheSameMimeMessage` potwierdza identyczny MIME, `Step3PolicyDependsOnlyOnItsOwnPort` - że polityka nie ma już pól z `Infra`, a `Step3PolicyIsTestableWithAHandWrittenFake` testuje politykę ręcznym fake'iem.
**Co powiedzieć:** sterowanie nadal płynie `App -> Infra` (`Confirm` woła notifier), ale zależność źródłowa odwróciła się: `Infra -> App`. To jest DIP.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s11 2 3`

### Rozwiązanie i uzasadnienie

Port należy do strony formułującej potrzebę. Adapter tłumaczy kody SMTP na błąd kontraktu (`InvalidOperationException`), więc polityka nie zna protokołu. Kontener DI (`Microsoft.Extensions.DependencyInjection`) nie był potrzebny - wystarczyła ręczna klasa `Main`.

### Pułapki

- "Rejestrujemy wszystko w `IServiceCollection`, więc mamy DIP" - krok 1 pokazuje, że nie.
- Port odbijający API technologii (`Send(host, port, mime)`) - wymiana kanału na SMS i tak zmieni politykę.
- Wyjątek technologii (np. `SmtpException` czy `DbException`) w kontrakcie portu.

### Pytanie do sali

Narysujcie strzałki `using` (zależności) i strzałki wywołań dla jednego waszego przypadku użycia. W którym miejscu biegną w tę samą stronę przez granicę?

## Scena s12. Clean Architecture - use case, dane na granicy, composition root

**Temat ze slajdów:** 5.3 Elementy praktyczne; 5.4 Dane na granicy i composition root; 5.5-5.6 Kręgi to nie szablon; 7.8 Protokół efektów
**Namespace:** `Training.Workshop.M3.S12CleanArchitecture` · **Test:** `scripts/warsztat.sh --lang cs test m3/s12` (`S12EquivalenceTest`, `S12UseCaseTest`)
**Czas:** ~18 min

### W skrócie

**Co robimy:** `ReservationController` parsuje żądanie, liczy cenę, zapisuje wiersz i publikuje komunikat w jednej metodzie. W czterech krokach wydzielamy przypadek użycia, porty z adapterami, przestrzenie nazw `App` i `Adapter` oraz composition root.

**Zasada:** Clean Architecture to zasada, że polityka (przypadki użycia) nie zależy od mechanizmów: porty należą do strony formułującej potrzebę, granicę przekraczają proste rekordy, a composition root składa graf bez reguł biznesowych. Liczy się kierunek zależności (`using`), a nie nazwy czterech folderów.

**Efekt:** Przypadek użycia testujemy bez HTTP i bazy, a kolejność zapis-powiadomienie jest jawną decyzją protokołu. Koszt to siedem typów więcej, uzasadniony dwoma realnymi efektami zewnętrznymi.

**Różnica względem Javy:** parametry żądania to `IReadOnlyDictionary<string, string> parameters` (`params` to w C# słowo kluczowe), `getOrDefault` to `GetValueOrDefault`, a wiersz bazy to `object[]` budowany wyrażeniem kolekcji `[...]`. Porty w `S12UseCaseTest` to prywatne klasy `FakeStore`/`FakeNotifier` na delegatach zamiast lambd.

### Co widzimy

`ReservationController` obsługuje żądanie "HTTP" (`IReadOnlyDictionary<string, string>`) i robi wszystko: parsuje parametry, liczy cenę (format + VIP), zapisuje wiersz `object[]` w `RowStore`, publikuje komunikat w `Outbox`, buduje odpowiedź. `RowStore` i `Outbox` to stabilny "świat zewnętrzny" sceny. Test wchodzi przez `CinemaApplication.ReservationController(db, outbox)` i porównuje odpowiedź, zapisane wiersze i komunikaty.

```csharp
string id;
try
{
    id = _db.Insert([email, format, rows.Count, total]);
}
catch (InvalidOperationException e)
{
    return "503 " + e.Message;
}
_outbox.Publish("reservation-created",
    id + ";" + email + ";" + total.ToString(CultureInfo.InvariantCulture));
return "201 " + id + " " + total.ToString(CultureInfo.InvariantCulture);
```

### Krok 1: Extract Class - przypadek użycia jako jawna orkiestracja

**W IDE:** utwórz rekordy `BookSeatsCommand(Email, Format, Rows)` i `Booking(Id, Total)`. Zaznacz w kontrolerze wycenę, zapis i publikację, ⌥⌘M `Execute`, potem ⌃T > Extract Class (albo Move Instance Method, F6) do nowej klasy `BookSeats`. Kontroler mapuje wyjątki: `ArgumentException` -> 400, `InvalidOperationException` -> 503.
**Po:**

```csharp
public Booking Execute(BookSeatsCommand command)
{
    if (command.Rows.Count == 0)
    {
        throw new ArgumentException("brak miejsc");
    }
    var total = Price(command);
    var id = _db.Insert(
        [command.Email, command.Format, command.Rows.Count, total]);
    _outbox.Publish("reservation-created",
        id + ";" + command.Email + ";" + total.ToString(CultureInfo.InvariantCulture));
    return new Booking(id, total);
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m3/s12` - 22 testy zielone.
**Co powiedzieć:** przypadek użycia czyta się jak scenariusz: wyceń, zapisz, powiadom. Granicę przekraczają proste rekordy, nie słownik z HTTP. Wciąż zna jednak kolumny tabeli i temat komunikatu.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s12 0 1`

### Krok 2: Porty zdefiniowane przez potrzebę, adaptery

**W IDE:** utwórz `NewReservation` (rekord), porty `IReservationStore.Save(NewReservation)` i `IBookingNotifier.ReservationCreated(id, reservation)`. Przenieś (ręcznie lub ⌃T > Extract Class) mapowanie na `object[]` do `RowStoreReservationStore`, publikację do `OutboxBookingNotifier`. `BookSeats` przyjmuje porty.
**Po:**

```csharp
var id = _store.Save(reservation);
_notifier.ReservationCreated(id, reservation);
return new Booking(id, reservation.Total);
```

**Uruchom:** test zielony, także przypadek "baza niedostępna - brak powiadomienia".
**Co powiedzieć:** kolejność zapis -> powiadomienie to decyzja protokołu: błąd zapisu oznacza brak powiadomienia. Kolejność nie daje atomowości - w systemie rozproszonym potrzebny byłby outbox transakcyjny.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s12 1 2`

### Krok 3: Move to Namespace - granica widoczna w przestrzeniach nazw

**W IDE:** F6 (Move to Folder, z aktualizacją przestrzeni nazw) na `BookSeats`, `BookSeatsCommand`, `Booking`, `NewReservation`, `IReservationStore`, `IBookingNotifier` -> folder `App`; F6 na kontrolerze i obu adapterach -> `Adapter`. Rider poprawi dyrektywy `using` (także w `CinemaApplication`).
**Po:**

```text
Step3/App/       BookSeats, BookSeatsCommand, Booking, NewReservation, IReservationStore, IBookingNotifier
Step3/Adapter/   ReservationController, RowStoreReservationStore, OutboxBookingNotifier
```

**Uruchom:** test zielony.
**Co powiedzieć:** `App` nie ma żadnego `using ...Adapter`; adaptery używają `App`. Nazwy przestrzeni nazw są drugorzędne - liczy się kierunek zależności. Zauważ, że w jednym projekcie (csproj) kompilator tego kierunku nie pilnuje - wrócimy do tego w s13.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s12 2 3`

### Krok 4: Composition root

**W IDE:** w konstruktorze kontrolera zaznacz `new BookSeats(...)` i ⌥⌘P (Introduce Parameter) - tworzenie grafu przeniesie się do `CinemaApplication`. Usuń nieużywane parametry `db`, `outbox` z kontrolera (⌘F6).
**Po:**

```csharp
public static ReservationController ReservationController(RowStore db, Outbox outbox)
{
    var bookSeats = new BookSeats(
        new RowStoreReservationStore(db), new OutboxBookingNotifier(outbox));
    return new ReservationController(bookSeats);
}
```

**Uruchom:** test zielony; pokaż `S12UseCaseTest`: przypadek użycia z ręcznymi fake'ami portów zamiast adapterów, w tym `DoesNotNotifyWhenSavingFails`.
**Co powiedzieć:** composition root zna wszystkie konkrety i nie zawiera reguł biznesowych. Podmiana bazy to zmiana tylko tutaj. Kontener DI nie jest potrzebny (a jeśli jest, to rejestracje w `Program.cs` pełnią tę samą rolę).
**Snapshot:** `Step4/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s12 3 4`

### Rozwiązanie i uzasadnienie

Przypadek użycia testowany bez HTTP i bazy, adaptery wymienialne w jednym miejscu, dane na granicy jako rekordy. Koszt: siedem typów więcej. Uzasadnia go to, że dwa efekty zewnętrzne i protokół ich kolejności już istnieją (slajd 7.10).

### Pułapki

- Kopiowanie szablonu czterech folderów (albo czterech projektów) bez sprawdzenia kierunku zależności.
- Osobny model na każdej granicy "bo tak trzeba" - `NewReservation` ma sens, bo oddziela semantykę od `object[]`.
- Reguły biznesowe w composition root albo w kontrolerze (np. walidacja miejsc w kontrolerze).

### Pytanie do sali

Czy wasz przypadek użycia da się uruchomić w teście bez ASP.NET Core i bazy? Co trzeba by przenieść, żeby się dało?

## Scena s13. Automatyczna ochrona granicy i diagnostyka spójności

**Temat ze slajdów:** 4.4 Diagnostyka spójności i sprzężenia; 4.1-4.3 Spójność, sprzężenie, koszt wydzielenia; lista kontrolna "reguła sprawdzana automatycznie"
**Namespace:** `Training.Workshop.M3.S13BoundaryCheck` · **Test:** `scripts/warsztat.sh --lang cs test m3/s13` (`S13ArchitectureTest`, `S13EquivalenceTest`, `S13ToolsTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** Klasa domeny `ScreeningService` miesza politykę cenową z mapowaniem na wiersz bazy i używa typów adaptera oraz `System.Data.SqlTypes`. Wydzielamy maper, przenosimy go do adaptera, a narzędzia `BoundaryRule` i `CohesionProbe` mierzą efekt po każdym kroku.

**Zasada:** Spójny moduł zmienia się z jednego powodu, a reguła zależności mówi, że domena nie zależy od technologii - to dwa niezależne wymiary. Regułę architektury warto sprawdzać automatycznie przy każdym buildzie, a metryki takie jak LCOM4 traktować jako sygnał do rozmowy, nie wyrocznię.

**Efekt:** Domena jest wolna od `System.Data` i typów adaptera, a każda klasa ma LCOM4 równe 1. Skan dyrektyw `using` jest słabą bramką - pełna nazwa typu w kodzie przejdzie, więc silniejsze są NetArchTest / ArchUnitNET, osobne projekty z kontrolowanymi referencjami albo analizatory Roslyn.

**Różnica względem Javy:** technologią bazy jest `System.Data.SqlTypes.SqlDateTime` (w BCL, bez pakietów) zamiast `java.sql.Timestamp`. `BoundaryRule` skanuje pliki `.cs` i dyrektywy `using` (także `using static`, `global using` i aliasy; instrukcje `using var`/`using (...)` pomija), a zakazane fragmenty to `"System.Data."` i `".Adapter"`. `CohesionProbe` rozpoznaje styl C# tego repozytorium (pola `private [readonly] Typ _pole;`, metody wcięte o 4 spacje), więc grupy metod są w PascalCase. Wynik porównywany w `S13EquivalenceTest` ma format rekordu C#: `ScreeningRow { Table = screenings, Title = Amator, Start = 2026-10-02 20:00:00 } | True`.

### Co widzimy

Dwa narzędzia bez bibliotek w przestrzeni nazw sceny: `BoundaryRule` (skanuje pliki `.cs` w katalogu i zgłasza zakazane dyrektywy `using`) oraz `CohesionProbe` (LCOM4: liczba grup metod połączonych wspólnym polem lub wywołaniem). Klasa domeny `ScreeningService` miesza politykę cenową z mapowaniem na wiersz bazy - używa adaptera i `System.Data.SqlTypes`.

```csharp
using System.Data.SqlTypes;
using Training.Workshop.M3.S13BoundaryCheck.Start.Adapter;

namespace Training.Workshop.M3.S13BoundaryCheck.Start.Domain;

public sealed class ScreeningService
{
    private readonly decimal _basePrice;
    private readonly decimal _morningDiscount;
    private readonly string _table;
```

Uruchom `scripts/warsztat.sh --lang cs test m3/s13` (skrypt wypisuje wyjście testów, które xUnit zbiera przez `ITestOutputHelper`) albo test z Rider (okno Unit Tests) i pokaż wyjście `S13ArchitectureTest`: `s13 start - naruszenia granicy: [ScreeningService.cs: System.Data.SqlTypes, ScreeningService.cs: Training.Workshop.M3.S13BoundaryCheck.Start.Adapter]` i `s13 start - LCOM4 ScreeningService: Result { Lcom4 = 2, Groups = [FromRow, ToRow; IsMorning, Price] }`. Test startu działa jak "zamrożone naruszenia" (FreezingArchRule w ArchUnit, podobny mechanizm ma ArchUnitNET): znane są tolerowane, każde nowe jest czerwone.

### Krok 1: Extract Class - spójność

**W IDE:** ⌃T > Extract Class na `ToRow`, `FromRow` i polu `_table`, klasa `ScreeningRowMapper` w tym samym folderze `Domain`. Popraw `CinemaApp` (composition root), żeby tworzył obie klasy.
**Po:**

```csharp
public sealed class ScreeningRowMapper    // wciąż w Domain
{
    private readonly string _table;
    public ScreeningRow ToRow(Screening screening) { ... }
    public Screening FromRow(ScreeningRow row) { ... }
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m3/s13` - 13 testów zielonych; LCOM4 obu klas = 1 (`Step1ClassesAreCohesive`), ale `Step1StillViolatesTheBoundaryButOnlyInTheMapper` pokazuje, że naruszenie granicy tylko się przeniosło.
**Co powiedzieć:** spójność i kierunek zależności to dwa różne wymiary. Wydzielenie klasy nie naprawia granicy.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s13 0 1`

### Krok 2: Move to Namespace - maper do adaptera

**W IDE:** F6 na `ScreeningRowMapper`, folder `Adapter` (z aktualizacją przestrzeni nazw). Rider doda `using ...Domain;` w maperze i poprawi `CinemaApp`.
**Po:**

```csharp
using System.Data.SqlTypes;
using Training.Workshop.M3.S13BoundaryCheck.Step2.Domain;

namespace Training.Workshop.M3.S13BoundaryCheck.Step2.Adapter;
```

**Uruchom:** test zielony; `Step2DomainIsFreeOfTechnology` - lista naruszeń pusta.
**Co powiedzieć:** zależność biegnie teraz `Adapter -> Domain`. Reguła jest sprawdzana automatycznie przy każdym buildzie, a nie tylko na code review.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s13 1 2`

### Rozwiązanie i uzasadnienie

Domena bez `System.Data` i bez typów adaptera, każda klasa opisuje jedno pojęcie. `S13ToolsTest` sprawdza same narzędzia na stałych próbkach kodu (w tym `using static` do adaptera).

### Pułapki

- `BoundaryRule` widzi tylko dyrektywy `using` - pełna nazwa typu w kodzie (albo `global using` w innym pliku) przejdzie. Silniejsze bramki: NetArchTest / ArchUnitNET, osobne projekty (csproj) z kontrolowanymi referencjami (domena bez referencji do projektu adaptera - wtedy pilnuje kompilator), analizatory Roslyn, reguły w CI. Porównajcie ich siłę i koszt.
- LCOM4 to heurystyka na źródle (pola `private`, typy bez spacji, wcięcie 4 spacje) - sygnał do rozmowy, nie wyrocznia. Mała klasa nie musi być spójna, duża może być nierozdzielnym pojęciem.
- Wydzielenie klasy dodaje współpracownika i kontrakt - porównuj koszt zmiany przed i po (slajd 4.3).

### Pytanie do sali

Która reguła architektury w waszym projekcie istnieje tylko w głowach lub na wiki? Jak najtaniej zamienić ją w test?

## Scena s14. Wzorzec jako decyzja odwracalna

**Temat ze slajdów:** 6.1-6.2 Wzorzec i refaktoryzacja w jego kierunku; 6.3 Strategy i Adapter; 6.4 Wzorzec można usunąć
**Namespace:** `Training.Workshop.M3.S14ReversiblePattern` · **Test:** `scripts/warsztat.sh --lang cs test m3/s14` (`S14EquivalenceTest`, `S14FestivalVariantTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** Rozliczenie z dystrybutorem ma dwa modele w jednym `switch`, w tym festiwalowy z obcego systemu w groszach. Wprowadzamy Strategy z Adapterem, a gdy umowy festiwalowe wygasają, usuwamy wariant i cały wzorzec.

**Zasada:** Wzorzec to odpowiedź na konkretne siły (tu: dwa istniejące warianty i obcy interfejs), a nie kod do skopiowania. Strategy opłaca się dla rodziny wymiennych algorytmów, Adapter dla realnego tłumaczenia obcego modelu, a refaktoryzacja od wzorca jest równie poprawna jak do niego.

**Efekt:** Zachowanie świadomie się zmienia: umowy FESTIVAL są teraz odrzucane, a model procentowy liczy tak samo na każdym etapie. Zostaje prosta metoda bez interfejsu i słownika, którą w razie potrzeby rozbudujemy tymi samymi krokami w przód.

**Różnica względem Javy:** mapa modeli to `IReadOnlyDictionary<string, ISettlementModel>` z inicjatorem, a brak modelu wykrywa `TryGetValue`. W Start `switch` z blokami to instrukcja `switch` z `case ...: { ... return ...; }`.

### Co widzimy

Rozliczenie z dystrybutorem ma dwa istniejące modele: procentowy (tydzień 1: 50%, 2: 40%, dalej 35%, minimalna gwarancja 500.00) i festiwalowy (stawka z obcego systemu `FestivalTariffClient`, w groszach jako `long`). Oba siedzą w jednym `switch`, konwersja jednostek jest wpleciona w logikę rozliczeń.

```csharp
case "FESTIVAL":
{
    var cents = _festival.WeeklyFeeInCents(deal.Title, week);
    return cents / 100m;
}
```

### Krok 1: Replace Conditional with Strategy (i Adapter dla obcego klienta)

**W IDE:** utwórz interfejs `ISettlementModel.Payout(deal, week, ticketRevenue)`. Każdą gałąź `switch` przenieś (⌥⌘M, potem ⌃T > Extract Class) do klasy: `PercentageModel` i `FestivalFeeAdapter` (opakowuje `FestivalTariffClient`, konwertuje grosze). `DistributorSettlement` wybiera model ze słownika.
**Po:**

```csharp
private readonly IReadOnlyDictionary<string, ISettlementModel> _models = new Dictionary<string, ISettlementModel>
{
    ["PERCENT"] = new PercentageModel(),
    ["FESTIVAL"] = new FestivalFeeAdapter(new FestivalTariffClient()),
};
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m3/s14` - 18 testów zielonych; `Step1AdapterPaysTheFestivalFeeConvertedFromCents` sprawdza 300.00 i 150.00.
**Co powiedzieć:** uzasadnienie to dwa **istniejące** warianty z różnymi właścicielami i obcy interfejs. Adapter ma realną pracę (konwersja jednostek), nie jest pustym przekazaniem 1:1.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s14 0 1`

### Krok 2: Wariant znika - Safe Delete adaptera

**W IDE:** umowy festiwalowe wygasły. Usuń wpis `"FESTIVAL"` ze słownika, Safe Delete (⌘⌦) na `FestivalFeeAdapter`.
**Po:**

```csharp
private readonly IReadOnlyDictionary<string, ISettlementModel> _models = new Dictionary<string, ISettlementModel>
{
    ["PERCENT"] = new PercentageModel(),
};
```

**Uruchom:** test zielony; `AfterTheVariantIsGoneFestivalDealsAreRejected` - to świadoma zmiana zachowania, dlatego sprawdzana osobno, a nie testem równoważności.
**Co powiedzieć:** zostaje Strategy z jedną implementacją i słownikiem z jednym wpisem - klasyczny sygnał nadmiaru wzorca ze slajdu 6.4.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s14 1 2`

### Krok 3: Refaktoryzacja od wzorca

**W IDE:** ⌥⌘N (Inline Method) na `PercentageModel.Payout` w miejscu wywołania (albo ręcznie wklej ciało), usuń słownik, Safe Delete na `PercentageModel` i `ISettlementModel`. Przenieś stałą `MinimumGuarantee` do `DistributorSettlement` (F6 na stałej albo ręcznie).
**Po:**

```csharp
public decimal Payout(Deal deal, int week, decimal ticketRevenue)
{
    if (deal.Model != "PERCENT")
    {
        throw new ArgumentException("nieznany model: " + deal.Model);
    }
    var percent = week == 1 ? 50 : week == 2 ? 40 : 35;
```

**Uruchom:** test zielony - model procentowy rozlicza się tak samo przed wzorcem, ze wzorcem i po jego usunięciu.
**Co powiedzieć:** refaktoryzacja od wzorca jest równie poprawna jak do wzorca. Gdy wróci drugi model, przywrócimy Strategy tymi samymi krokami w przód.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s14 2 3`

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
**Namespace:** `Training.Workshop.M3.S15Invariants` · **Test:** `scripts/warsztat.sh --lang cs test m3/s15` (`S15InvariantsTest`)
**Czas:** ~8 min

### W skrócie

**Co robimy:** `Reservation` to anemiczna klasa z publicznymi setterami, walidację robi tylko `BookingService`, więc import pliku partnera tworzy niepoprawne rezerwacje. Zamieniamy klasę na niezmienny rekord i przenosimy strażników do jego konstruktora.

**Zasada:** Inwariant to warunek, który obiekt spełnia przez całe życie, a jego naturalnym właścicielem jest sam model domeny. Pilnują go konstruktor i niezmienność, a nie każdy serwis z osobna; reguły wymagające danych spoza obiektu należą do przypadku użycia.

**Efekt:** Złej rezerwacji nie da się utworzyć żadną ścieżką - import stał się chroniony, choć go nie zmienialiśmy. Zachowanie importu świadomie się zmienia: błędny wiersz kończy się teraz wyjątkiem.

**Różnica względem Javy:** Start to klasa z auto-właściwościami `{ get; set; }` (odpowiednik JavaBeana z setterami). C# nie ma kompaktowego konstruktora rekordu, więc w kroku 2 rekord dostaje jawny konstruktor ze strażnikami i właściwości tylko `{ get; }` (bez `init`). Warto o tym powiedzieć: pozycyjny rekord z kroku 1 ma właściwości `init`, więc wyrażenie `with { Seats = 0 }` ominęłoby każdego strażnika w konstruktorze - dopiero właściwości bez `init` zamykają tę furtkę.

### Co widzimy

`Reservation` to anemiczna klasa z publicznymi setterami. Walidację (e-mail z `@`, co najmniej jedno miejsce, kwota nieujemna) robi tylko `BookingService`. `ReservationImport` (plik partnera `email;miejsca;kwota`) tworzy model z pominięciem walidacji - i przepuszcza `jan-kino.pl;0;-5.00`.

```csharp
var reservation = new Reservation();
reservation.Email = columns[0];
reservation.Seats = int.Parse(columns[1], CultureInfo.InvariantCulture);
reservation.Total = decimal.Parse(columns[2], CultureInfo.InvariantCulture);
```

### Krok 1: Remove Setting Method i konwersja na rekord

**W IDE:** na `Reservation` ⌥⏎ i akcja konwersji klasy do rekordu (jeśli Twoja wersja Rider jej nie oferuje, przepisz ręcznie na `sealed record Reservation(string Email, int Seats, decimal Total)`), w obu serwisach zastąp settery wywołaniem konstruktora rekordu.
**Po:**

```csharp
public sealed record Reservation(string Email, int Seats, decimal Total);
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m3/s15` - 17 testów zielonych; `ImportWithoutInvariantsCreatesInvalidReservation` pokazuje, że import wciąż tworzy złą rezerwację.
**Co powiedzieć:** obiekt jest niezmienny i powstaje w całości w jednym wywołaniu - to warunek, żeby w ogóle dało się pilnować inwariantów.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s15 0 1`

### Krok 2: Move - strażnicy do konstruktora rekordu

**W IDE:** zamień rekord pozycyjny na rekord z jawnym konstruktorem i właściwościami `{ get; }` (bez `init`), wytnij trzy `if` z `BookingService.Book` i wklej je na początek konstruktora.
**Po:**

```csharp
public sealed record Reservation
{
    public Reservation(string email, int seats, decimal total)
    {
        if (email is null || !email.Contains('@'))
        {
            throw new ArgumentException("niepoprawny email: " + email);
        }
        // ... miejsca, kwota
        Email = email;
        Seats = seats;
        Total = total;
    }

    public string Email { get; }
```

**Uruchom:** test zielony; ścieżka przez serwis daje te same komunikaty, a `Step2ImportCannotCreateInvalidReservation` pokazuje, że import jest chroniony, choć go nie zmienialiśmy.
**Co powiedzieć:** inwariant ma jednego właściciela - model. To też DRY: reguła poprawności rezerwacji istnieje w jednym miejscu.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s15 1 2`

### Rozwiązanie i uzasadnienie

Nie da się utworzyć rezerwacji w złym stanie, niezależnie od ścieżki (kasa, online, import), także przez `with`. Serwis tylko koordynuje.

### Pułapki

- Walidacja w atrybutach na DTO (`[Required]`, `[EmailAddress]`, `[Range]`) zamiast w modelu - omija ją każda ścieżka bez frameworka (model binding ASP.NET Core).
- Inwarianty, które wymagają danych spoza obiektu (np. dostępność miejsca) - to reguła przypadku użycia, nie konstruktora.
- Rekord pozycyjny z właściwościami `init` i walidacją tylko w konstruktorze - `with` i inicjator obiektu ją omijają.

### Pytanie do sali

Ile ścieżek tworzy wasz najważniejszy obiekt domeny? Czy wszystkie przechodzą przez tę samą walidację?

## Scena s16. Sprzężenie protokołu - ukryta kolejność wywołań

**Temat ze slajdów:** 4.4 "Czy wywołania wymagają ukrytej kolejności?" - sprzężenie protokołu lub czasu
**Namespace:** `Training.Workshop.M3.S16TemporalCoupling` · **Test:** `scripts/warsztat.sh --lang cs test m3/s16` (`S16EquivalenceTest`)
**Czas:** ~6 min

### W skrócie

**Co robimy:** `TicketPrinter` wymaga wywołania trzech setterów przed `Print`, czego nie widać w typach - pominięcie kończy się wyjątkiem w `Print`. Przenosimy dane do parametrów `Print`, a potem do rekordu `TicketRequest` z walidacją.

**Zasada:** Sprzężenie czasowe (protokołu) to ukryte wymaganie kolejności wywołań, o którym klient musi wiedzieć poza typami. Naprawia się je, zamieniając stan i kolejność na jawne dane wejściowe, które kompilator i konstruktor mogą sprawdzić.

**Efekt:** Drukarka jest bezstanowa i bezpieczna współbieżnie, a niekompletne dane są odrzucane w chwili tworzenia żądania. Klient zamiast trzech wywołań buduje jeden obiekt.

**Różnica względem Javy:** w Start pola są nullowalne (`Screening?`, `int?`, `string?`), a `Print` używa operatora `!` - on tylko ucisza ostrzeżenia nullable, więc zapomniane wywołanie to `NullReferenceException` (brak seansu lub kupującego) albo `InvalidOperationException` (brak miejsca w `int?`) w `Print`. `TicketRequest` w kroku 2 to rekord z jawnym konstruktorem i `ArgumentNullException.ThrowIfNull`.

### Co widzimy

`TicketPrinter` wymaga wywołania `SelectScreening`, `SelectSeat` i `ForBuyer` przed `Print`. Nic w typach tego nie mówi: zapomniane wywołanie to wyjątek w `Print`, a drukarka współdzielona przez dwie kasy miesza dane klientów. `TicketDesk` (klient) zna ten protokół.

```csharp
_printer.SelectScreening(screening);
_printer.SelectSeat(seat);
_printer.ForBuyer(buyer);
return _printer.Print();
```

Na żywo: w `TicketDesk` zakomentuj `SelectSeat` i uruchom test - `InvalidOperationException: Nullable object must have a value` z wnętrza `Print` (a bez `SelectScreening` - `NullReferenceException`). Kompilator nic nie zgłosił, bo `!` go uciszył. Cofnij.

### Krok 1: Change Signature - wszystko, czego potrzeba, w parametrach

**W IDE:** ⌘F6 na `Print`: dodaj parametry `Screening screening`, `int seat`, `string buyer` (wartość w wywołaniach: odpowiednie pola). W `Print` użyj parametrów, Safe Delete (⌘⌦) na polach i setterach; w `TicketDesk` usuń trzy wywołania.
**Po:**

```csharp
public string Print(Screening screening, int seat, string buyer)
{
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m3/s16` - 7 testów zielonych.
**Co powiedzieć:** protokół "najpierw ustaw, potem drukuj" stał się kontraktem sprawdzanym przez kompilator (także przez analizę nullable - operatory `!` zniknęły). Drukarka jest bezstanowa, więc bezpieczna współbieżnie.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s16 0 1`

### Krok 2: Introduce Parameter Object

**W IDE:** na `Print` ⌃T > Extract Class from Parameters (odpowiednik Introduce Parameter Object), zaznacz trzy parametry, klasa `TicketRequest`; zamień ją na `sealed record` z jawnym konstruktorem i dodaj `ArgumentNullException.ThrowIfNull` dla seansu i kupującego.
**Po:**

```csharp
public sealed record TicketRequest
{
    public TicketRequest(Screening screening, int seat, string buyer)
    {
        ArgumentNullException.ThrowIfNull(screening);
        ArgumentNullException.ThrowIfNull(buyer);
        Screening = screening;
        Seat = seat;
        Buyer = buyer;
    }
```

**Uruchom:** test zielony; `Step2RejectsIncompleteRequestAtCreation` - niekompletne żądanie (`null!`) jest odrzucane `ArgumentNullException` przy tworzeniu, a nie w `Print`.
**Co powiedzieć:** błąd wychodzi tam, gdzie powstał, a nie trzy wywołania dalej. Adnotacje nullable ostrzegają w czasie kompilacji, strażnik w konstruktorze chroni w runtime (np. dane z deserializacji).
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s16 1 2`

### Rozwiązanie i uzasadnienie

Sprzężenie czasowe zamienione na zależność od danych. To samo dotyczy protokołów efektów (s12): kolejność, która ma znaczenie, powinna być jawna i przetestowana.

### Pułapki

- Metoda `Init()` wymagana przed użyciem obiektu - ten sam zapach w innej formie.
- Builder bez walidacji w `Build()` - przenosi problem, nie usuwa go.
- Operator `!` jako "naprawa" ostrzeżenia nullable - ukrywa protokół zamiast go usunąć.

### Pytanie do sali

Które API w waszym systemie wymaga "najpierw X, potem Y"? Jak klient ma się o tym dowiedzieć?

## Proponowana kolejność pokazu

**Ścieżka krótka (~85 min):** s01 (DRY) -> s02 (podobieństwo) -> s06 (YAGNI) -> s07 (SRP) -> s09 (LSP) -> s11 (DIP) -> s14 (wzorzec odwracalny). Z s12 pokaż tylko `scripts/warsztat.sh --lang cs diff m3/s12 3 4` i `S12UseCaseTest` (5 min), jeśli zostanie czas.

**Ścieżka pełna (~170 min, z przerwą):**

1. DRY, KISS, YAGNI: s01, s02, s03, s04, s05, s06 (~60 min) - na koniec filtr decyzyjny ze slajdu 2.9 na przykładzie s06.
2. SOLID: s07, s08, s09, s10, s11 (~55 min).
3. Spójność, sprzężenie, granice: s15, s16, s13, s12 (~45 min).
4. Wzorce: s14 (~12 min) jako podsumowanie - wzorzec to kierunek serii refaktoryzacji, który można odwrócić.

Sceny s12 i s13 dobrze łączyć: po s12 zapytaj, jak upewnić się, że `App` nigdy nie użyje `Adapter` (w jednym projekcie kompilator tego nie pilnuje), i przejdź do s13.
