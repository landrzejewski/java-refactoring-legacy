# Moduł 5. Refaktoryzacja do wzorców - warsztat CineLegacy (C#): przewodnik prowadzącego

Trzydzieści dwie małe sceny w domenie kina CineLegacy. Każda pokazuje jedną refaktoryzację do wzorca (albo usunięcie wzorca) ze slajdów: od pytania "czy zawsze warto?" i usuwania zbędnych wzorców, przez ryzyka zmiany kontraktu i refaktoryzacje automatyczne w IDE, po wybór między podobnymi wzorcami (Polecenie, Strategia, Stan; Strategia i Metoda szablonowa; Łańcuch zobowiązań i Dekorator), granice systemu (Adapter, obiekt graniczny, Fasada, warstwa antykorupcyjna) oraz Budowniczego, Obserwatora, Mediatora i Odwiedzającego. Polskie nazwy wzorców i refaktoryzacji są takie jak na slajdach; tytuły scen zostają w wersji angielskiej, bo tak nazywają się refaktoryzacje w katalogu Kerievsky’ego i w IDE. Każda scena ma kod wyjściowy `Start` z zapachem lub pułapką i gotowe snapshoty `StepN` po każdym ruchu, więc w dowolnym momencie można przeskoczyć do kolejnego kroku. Test równoważności pilnuje, że wzorzec wprowadzamy bez zmiany obserwowalnego zachowania, a tam, gdzie zachowanie celowo się zmienia (moment wyboru strategii, nowy niezmiennik buildera, wygaszony wariant, naprawiony błąd skopiowanej orkiestracji, zaostrzona walidacja na granicy), osobny test to dokumentuje.

Port C# żyje w `csharp/src/Training.Workshop/M5/SNN.../{Start,Step1,...}` (testy xUnit w `csharp/tests/Training.Workshop.Tests/M5/SNN.../`). Każdy krok opisujemy jako konkretne zmiany w kodzie; nazwy refaktoryzacji z katalogu Kerievsky'ego i Fowlera podajemy w nawiasach. Projekt ma `TreatWarningsAsErrors` (`csharp/Directory.Build.props`), więc każde ostrzeżenie kompilatora (np. niewyczerpujący `switch`, wywołanie metody `[Obsolete]`, pole nieustawione w konstruktorze przy włączonym nullable) zatrzymuje build - kilka scen korzysta z tego świadomie.

## Jak prowadzić pokaz

```bash
scripts/warsztat.sh --lang cs list m5              # sceny i kroki modułu 5
scripts/warsztat.sh --lang cs test m5/s08          # testy jednej sceny
scripts/warsztat.sh --lang cs test m5              # wszystkie sceny modułu (845 testów)
scripts/warsztat.sh --lang cs diff m5/s08 1 2      # co zmienia krok 2 względem kroku 1 (0 = Start)
scripts/warsztat.sh --lang cs diff m5/s08 1 2 --word  # to samo, zmiany podświetlone w obrębie linii
scripts/warsztat.sh --lang cs jump m5/s08 2        # kopiuje Step2 do Start (przeskok, gdy brakuje czasu)
scripts/warsztat.sh --lang cs next m5/s08          # następny krok do Start + podsumowanie zmian
scripts/warsztat.sh next                           # kolejny krok tej samej sceny (język i scena zapamiętane)
scripts/warsztat.sh prev                           # krok wstecz
scripts/warsztat.sh status                         # który krok jest teraz w Start
scripts/warsztat.sh --lang cs reset m5/s08         # przywraca Start z repozytorium
```

- Zasada: **jeden krok - jeden test - jedno zdanie komentarza**. Po każdym ruchu uruchamiamy test sceny (najprościej skryptem `scripts/warsztat.sh --lang cs test m5/sNN`); zielony pasek jest dowodem, że wzorzec nie zmienił kontraktu.
- **Krok po kroku bez numerów:** `scripts/warsztat.sh --lang cs next m5/sNN` wstawia do `Start` gotowy następny krok i wypisuje, co się zmieniło. `prev` cofa o krok, `status` mówi, który krok jest teraz w `Start`. Skrypt pamięta język i ostatnią scenę, więc potem wystarczy samo `next`. Ręczne zmiany w `Start` zostają nadpisane - o to chodzi, gdy krok na żywo się rozjechał.
- Pracujemy na namespace `Start` sceny (np. `Training.Workshop.M5.S08State.Start`). Gdy coś pójdzie nie tak, `next` (albo `jump` do właściwego kroku), a po scenie `reset`.
- Test równoważności przechodzi przez `Start` i wszystkie kroki (xUnit `[Theory]` z wariantami `start`, `step1`...). Jeśli uczestnicy pracują równolegle, to ten sam test jest ich kryterium ukończenia.
- Testy wołają sceny przez stabilne punkty wejścia (klient w rodzaju `PriceBoard`, `CancellationDesk`, `PaymentServices`, `Reports`, `Cinema`, `PaymentModule`, `SettlementModule` albo zachowany konstruktor), dzięki czemu `jump` do dowolnego kroku kompiluje się i przechodzi. Testy dokumentujące pułapkę samego `Start` (s11, s17, s30) po `jump` kończą się bez sprawdzeń i są zielone - xUnit 2 nie ma odpowiednika `assumeTrue` z JUnit (w Javie te testy są "aborted"), więc test sam wykrywa refleksją, że `Start` nie ma już pułapki, i wychodzi wcześniej. To zamierzone.
- Motyw przewodni: **najpierw kontrakt, potem diagram klas**. W każdej scenie zaczynamy od pytania "co jest obserwowalne?" (wyjątek, kolejność efektów, moment wyboru, format trwały, typ i moment błędu na granicy). Siatkę bezpieczeństwa z sekcji 3 slajdów ćwiczy każda scena: test równoważności przed pierwszym ruchem, test różnicowy w s10, testy struktury w s26-s32.

## Mapa scen

Sceny są ułożone według numerów, tak jak przestrzenie nazw i skrypt. Kolejność pokazu, zgodną z kolejnością slajdów, podaje sekcja „Wybór scen”.

| Scena | Temat ze slajdów | Kroki | Namespace | Czas |
| --- | --- | --- | --- | --- |
| s01 | 6.1. Strategia, Polecenie czy Stan? Kto wybiera wariant i kiedy; 6.2. Strategia: sygnał w kodzie; 6.3. Strategia: kroki i wynik | 3 | `M5.S01Strategy` | ~12 min |
| s02 | 16.1. Zastąpienie instrukcji warunkowej polimorfizmem; 6.8. Typowe pomyłki przy wyborze | 3 | `M5.S02Polymorphism` | ~12 min |
| s03 | 16.2. Zastąpienie kodu typu klasą | 3 | `M5.S03TypeCode` | ~12 min |
| s04 | 10.1. Ukrycie klas za Fabryką; 10.3. Fabryka jako strażnik spójności | 3 | `M5.S04EncapsulateFactory` | ~10 min |
| s05 | 10.2. Wyodrębnienie klasy fabryki; 10.3. Fabryka jako strażnik spójności | 3 | `M5.S05ExtractFactory` | ~10 min |
| s06 | 12.1. Ukrycie budowy Kompozytu za Budowniczym; 12.2. Budowniczy: kiedy i na co uważać | 3 | `M5.S06Builder` | ~12 min |
| s07 | 8.1. Przeniesienie dodatków do Dekoratora; 8.2. Dekorator: na co uważać | 3 | `M5.S07Decorator` | ~12 min |
| s08 | 6.4. Stan: sygnał i tabela przejść; 6.5. Stan: wynik | 3 | `M5.S08State` | ~15 min |
| s09 | 13.1. Zastąpienie zakodowanych powiadomień Obserwatorem; 13.2. Obserwator: zasady powiadamiania muszą być jawne | 3 | `M5.S09Observer` | ~15 min |
| s10 | 16.3. Zastąpienie ukrytego drzewa Kompozytem; 3.3. Test równoważności przed zmianą i po niej | 3 | `M5.S10ImplicitTree` | ~15 min |
| s11 | 16.4. Kompozyt przezroczysty czy bezpieczny | 2 | `M5.S11SafeComposite` | ~8 min |
| s12 | 16.5. Jeden element lub wiele oraz wyodrębnienie Kompozytu (zastąpienie rozróżnienia „jeden lub wiele” Kompozytem) | 3 | `M5.S12OneMany` | ~10 min |
| s13 | 16.5. Jeden element lub wiele oraz wyodrębnienie Kompozytu (wyodrębnienie Kompozytu) | 2 | `M5.S13ExtractComposite` | ~8 min |
| s14 | 9.1. Ujednolicenie interfejsów Adapterem; 9.2. Co adapter naprawdę musi przetłumaczyć | 3 | `M5.S14Adapter` | ~12 min |
| s15 | 6.6. Polecenie: łańcuch warunków zamieniony na rejestr poleceń | 3 | `M5.S15Command` | ~12 min |
| s16 | 7.1. Utworzenie Metody szablonowej: wspólny szkielet, różne kroki | 2 | `M5.S16TemplateMethod` | ~8 min |
| s17 | 16.7. Ograniczenie liczby instancji Singletonem; 17.4. Wchłonięcie Singletona | 3 | `M5.S17Singleton` | ~10 min |
| s18 | 16.6. Przeniesienie zbierania wyników do parametru zbierającego | 3 | `M5.S18CollectingParameter` | ~8 min |
| s19 | 15.1. Odwiedzający i macierz zmian; 15.2. Odwiedzający albo hierarchia `sealed` i `switch` | 3 | `M5.S19Visitor` | ~12 min |
| s20 | 4.2. Najpierw rodzaj zmienności, potem wzorzec; 18.1. Mapa decyzji (1/2); 18.2. Mapa decyzji (2/2); 18.3. Lista kontrolna | 3 | `M5.S20DecisionMap` | ~12 min |
| s21 | 17.5. Wzorzec jako decyzja odwracalna; 17.2. Refaktoryzacje, które usuwają wzorzec; 5.2. Kiedy wzorzec się nie opłaca | 3 | `M5.S21ReversiblePattern` | ~12 min |
| s22 | 17.3. Usunięcie pośrednika; 17.2. Refaktoryzacje, które usuwają wzorzec | 3 | `M5.S22MiddleMan` | ~8 min |
| s23 | 5.3. Refaktoryzacja a zmiana kontraktu; 5.4. Co trzeba zachować przy wprowadzaniu wzorca; 3.10. Bezpieczna pętla refaktoryzacji do wzorca | 3 | `M5.S23ContractChange` | ~10 min |
| s24 | 3.6. Refaktoryzacje automatyczne w IDE: co dają, a czego nie widzą; 3.7. Zmiana nazwy i wyodrębnienie metody w drodze do wzorca | 3 | `M5.S24Rename` | ~12 min |
| s25 | 3.7. Zmiana nazwy i wyodrębnienie metody w drodze do wzorca | 3 | `M5.S25ExtractMethod` | ~15 min |
| s26 | 6.1. Strategia, Polecenie czy Stan? Kto wybiera wariant i kiedy; 6.7. Trzy osie w jednej klasie: każda dostaje własny mechanizm; 6.8. Typowe pomyłki przy wyborze | 3 | `M5.S26Variability` | ~15 min |
| s27 | 7.2. Gdy punkty zaczepienia obejmują dwie niezależne osie; 7.3. Zastąpienie dziedziczenia delegacją: osie jako strategie; 7.4. Strategia czy Metoda szablonowa? | 3 | `M5.S27TemplateToStrategy` | ~12 min |
| s28 | 8.3. Łańcuch `if/else`, w którym kolejność jest regułą; 8.4. Łańcuch zobowiązań: ogniwo i złożenie łańcucha; 8.5. Łańcuch zobowiązań czy Dekorator? | 3 | `M5.S28Chain` | ~12 min |
| s29 | 9.3. Surowe dane z zewnątrz w kodzie domeny; 9.4. Obiekt graniczny: sprawdzone dane na granicy systemu; 9.5. Adapter czy obiekt graniczny? | 3 | `M5.S29BoundaryObject` | ~12 min |
| s30 | 11.1. Klienci sami koordynują podsystemy; 11.2. Wyodrębnienie Fasady: jedna operacja zamiast znajomości kroków | 3 | `M5.S30Facade` | ~12 min |
| s31 | 11.3. Obcy model w regułach domeny; 11.4. Warstwa antykorupcyjna: port, tłumacz i model domenowy; 11.5. Fasada a warstwa antykorupcyjna | 3 | `M5.S31AntiCorruptionLayer` | ~12 min |
| s32 | 14.1. Komponenty połączone każdy z każdym; 14.2. Wprowadzenie Mediatora: reguły współpracy w jednym miejscu; 14.3. Obserwator czy Mediator? | 3 | `M5.S32Mediator` | ~12 min |

Pełne namespace mają prefiks `Training.Workshop.` (np. `Training.Workshop.M5.S01Strategy`), katalogi to `csharp/src/Training.Workshop/M5/S01Strategy` itd. Liczby domeny (ceny formatów, zniżki, VIP, okulary, opłata online, punkty lojalnościowe, zwroty, VAT) są wspólne dla całego warsztatu. Wartości spoza wspólnej listy (program "tydzień studenta" 50%, ubezpieczenie biletu 4.00, "tani wtorek" -30%, weekend +2.00, ceny premier i maratonów, limity zatwierdzania zwrotów 100.00 i 1000.00, autoryzacja częściowa terminala do 300.00, voucher KINO10) są przykładowe i opisane w komentarzach XML (`/// <summary>`) sceny.

Konwencje portu, które wracają w wielu scenach: interfejsy mają prefiks `I` (`IDiscountPolicy`, `IPricedTicket`, `IPaymentListener`, `IDeskCommand`, `IReportFormat`, `IRefundHandler`, `IPaymentTerminal`, `IFilmLicenses`, `ISelectionMediator`...), gettery z Javy są właściwościami (`Name`, `Price`, `Minutes`), wyjątki to `ArgumentException` (Java `IllegalArgumentException`), `InvalidOperationException` (`IllegalStateException`) i `NotSupportedException` (`UnsupportedOperationException`), `Optional<T>` to typ dopuszczający `null` (`RefundDecision?`, `FilmLicense?`), a parametr `base` nazywa się `basePrice`, bo `base` to słowo kluczowe C#. Tam, gdzie Java przekazuje lambdę jako implementację interfejsu, C# potrzebuje klasy (lambda pasuje tylko do typu delegata) - w porcie i w testach są to małe prywatne klasy. Widoczność pakietowa to `internal`, ale warsztat jest jednym projektem, więc tam, gdzie scena ukrywa typy (s30, s31), granicę pilnuje test refleksji. Wyniki tekstowe (kwoty, daty, statusy) są takie same jak w wersji Java. Tam, gdzie scena odbiega od Javy, akapit **Różnica względem Javy** mówi, co i dlaczego wygląda inaczej.

## Wybór scen

Wszystkie sceny razem to ponad 6 h samego pokazu (około 370 min), więc na szkoleniu pokazujemy **rdzeń: 11 scen, około 2 h 20 min**, po jednej z kluczowych sekcji slajdów, w kolejności prezentacji:

1. s25 podsumowanie zamówienia biletów (15 min): wyodrębnienie metody, przepływ danych i moment, w którym IDE odmawia (slajd 3.7).
2. s20 mapa decyzji (12 min): pytanie „jaki rodzaj zmienności?” na cały warsztat (slajd 4.2).
3. s23 zmiana kontraktu (10 min): krok „przy okazji” zmienia dwie rzeczy naraz (slajdy 5.3-5.4).
4. s26 pulpit kasjera (15 min): Polecenie, Strategia i Stan w jednej klasie (slajdy 6.1, 6.7).
5. s27 raporty sprzedaży (12 min): Metoda szablonowa z dwiema osiami zamieniona na Strategie (slajdy 7.2-7.4).
6. s28 zatwierdzanie zwrotów (12 min): Łańcuch zobowiązań a Dekorator (slajdy 8.3-8.5).
7. s29 odpowiedź terminala (12 min): Adapter i obiekt graniczny (slajdy 9.3-9.5).
8. s30 zakup w sklepie i w kasie (12 min): Fasada i świadoma naprawa błędu w kopii (slajdy 11.1-11.2).
9. s31 licencje dystrybutora (12 min): Fasada a warstwa antykorupcyjna (slajdy 11.3-11.5).
10. s32 ekran wyboru miejsc (12 min): Mediator i ryzyko mediatora, który robi wszystko (slajdy 14.1-14.3).
11. s21 rozliczenie z dystrybutorem (12 min): usunięcie wzorca, który przestał być potrzebny (slajd 17.5).

**Sceny opcjonalne**, gdy grupa pracuje szybciej albo pyta o konkretny wzorzec: s22 i s17 (usuwanie wzorców: usunięcie pośrednika, Singleton i jego wchłonięcie), s24 (zmiana nazwy i nazwy poza C#), s01, s08 i s15 (każdy z trzech wzorców sekcji 6 osobno, w szczegółach), s16 (klasyczna Metoda szablonowa), s07 (Dekorator), s14 (Adapter), s04 i s05 (jeden blok o Fabryce z porównaniem obu refaktoryzacji), s06 (Budowniczy), s09 (Obserwator, dobry kontrast do s32), s19 (Odwiedzający). Sceny z sekcji 16 (s02, s03, s10-s13, s18) zostawiamy do samodzielnej pracy w parach albo jako zadanie domowe; s11 i s13 mają po 2 kroki i dobrze sprawdzają się jako krótkie ćwiczenie. Na podsumowanie wróć do s20 i poproś salę o przypisanie każdej pokazanej sceny do wiersza mapy decyzji (slajdy 18.1-18.2).

## Scena s01. Replace Conditional Logic with Strategy - polityka zniżek

**Temat ze slajdów:** 6.1. Strategia, Polecenie czy Stan? Kto wybiera wariant i kiedy; 6.2. Strategia: sygnał w kodzie; 6.3. Strategia: kroki i wynik
**Namespace:** `Training.Workshop.M5.S01Strategy` · **Test:** `scripts/warsztat.sh --lang cs test m5/s01` (`S01EquivalenceTest`, `S01SolutionTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `TicketPricer` wybiera zniżkę łańcuchem `if` po nazwie programu kina, wymieszanym z walidacją ceny. Wprowadzamy interfejs `IDiscountPolicy` najpierw jako strategię przejściową, przenosimy każdy program do osobnej klasy, a wybór programu trafia do `DiscountPrograms` i konstruktora.

**Zasada:** Strategy zamyka wymienne warianty tego samego obliczenia za wspólnym interfejsem, gdy wariant wybiera się niezależnie od klasy obiektu. Wspólna walidacja zostaje w kontekście, a strategie są bezstanowe. Samo `if` nie uzasadnia wzorca - przy prostym, stabilnym warunku interfejs pogarsza czytelność.

**Efekt:** Nowy program zniżek to nowa strategia (mała klasa) i jeden wpis w `switch`, bez zmiany `TicketPricer`. Zachowanie świadomie się zmienia: wybór w konstruktorze zamraża decyzję, więc nieznany program zgłasza się teraz przed ujemną ceną.

**Różnica względem Javy:** w Javie strategia przejściowa z kroku 1 to lambda `(b, type) -> legacyDiscount(b, type, program)`. Interfejs C# nie przyjmuje lambdy, więc strategią przejściową jest prywatna klasa zagnieżdżona `LegacyPolicy(program)` (primary constructor). Z tego samego powodu nowy program w `S01SolutionTest` to prywatna klasa `BlackFridayDiscount`, a nie lambda. Ruch i wniosek ("kontekst bez zmian") są te same.

### Co widzimy

`TicketPricer.Price(basePrice, ticketType, program)` wybiera algorytm zniżki łańcuchem `if` po nazwie programu skonfigurowanego w kinie (STANDARD, STUDENT_WEEK, PREMIERE). W tej samej metodzie jest walidacja ceny i programu. Algorytm jest wybierany niezależnie od klasy obiektu - to klasyczny kandydat na Strategy.

```csharp
if (program == "PREMIERE")
{
    discount = Money.Zero;
}
else if (program == "STUDENT_WEEK" && ticketType == "S")
{
    discount = basePrice.Percent(50);
}
else if (program == "STANDARD" || program == "STUDENT_WEEK")
{
    var percent = ticketType switch { "N" => 0, "S" => 25, ... };
    discount = basePrice.Percent(percent);
}
else
{
    throw new ArgumentException("unknown program: " + program);
}
```

Klientem jest `PriceBoard.PriceFor(request)` - przez niego woła test. Test równoważności to **tabela decyzji**: każda gałąź, typ nieznany, program nieznany i `null`. Zwróć uwagę na przypadek "PREMIERE nie sprawdza typu" - Start przyjmuje typ `X` bez błędu i refaktoryzacja musi to zachować.

### Krok 1: Extract Interface + strategia przejściowa

**Zmiany:**

1. Utwórz nowy plik `IDiscountPolicy.cs` z interfejsem `IDiscountPolicy` i jedną metodą `Money Discount(Money basePrice, string ticketType)`.
2. W `TicketPricer` wydziel cały łańcuch `if` z metody `Price` do nowej prywatnej metody statycznej `LegacyDiscount(Money basePrice, string ticketType, string program)` zwracającej `Money` (wyodrębnienie metody). Przypisania do `discount` zamień na `return`, a końcowe `basePrice.Minus(discount)` zostaw w `Price`.
3. Dopisz prywatną klasę zagnieżdżoną `LegacyPolicy(string program) : IDiscountPolicy` (primary constructor), której `Discount` deleguje do `LegacyDiscount`.
4. W `Price`, po walidacji, utwórz `IDiscountPolicy policy = new LegacyPolicy(program)` i zwróć `basePrice.Minus(policy.Discount(basePrice, ticketType))`.

**Przed (`TicketPricer.cs`):**

```csharp
public Money Price(Money basePrice, string ticketType, string? program)
{
    if (basePrice.CompareTo(Money.Zero) < 0)
    {
        throw new ArgumentException("base price must not be negative");
    }
    if (program == null)
    {
        throw new ArgumentException("program must not be null");
    }
    Money discount;
    if (program == "PREMIERE")
    {
        discount = Money.Zero;
    }
    else if (program == "STUDENT_WEEK" && ticketType == "S")
    {
        discount = basePrice.Percent(50);
    }
    else if (program == "STANDARD" || program == "STUDENT_WEEK")
    {
        var percent = ticketType switch
        {
            "N" => 0,
            "S" => 25,
            "E" => 30,
            "C" => 40,
            _ => throw new ArgumentException("unknown ticket type: " + ticketType),
        };
        discount = basePrice.Percent(percent);
    }
    else
    {
        throw new ArgumentException("unknown program: " + program);
    }
    return basePrice.Minus(discount);
}
```

**Po (`IDiscountPolicy.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public interface IDiscountPolicy
{
    Money Discount(Money basePrice, string ticketType);
}
```

**Po (`TicketPricer.cs`):**

```csharp
public Money Price(Money basePrice, string ticketType, string? program)
{
    if (basePrice.CompareTo(Money.Zero) < 0)
    {
        throw new ArgumentException("base price must not be negative");
    }
    if (program == null)
    {
        throw new ArgumentException("program must not be null");
    }
    IDiscountPolicy policy = new LegacyPolicy(program);
    return basePrice.Minus(policy.Discount(basePrice, ticketType));
}

private static Money LegacyDiscount(Money basePrice, string ticketType, string program)
{
    if (program == "PREMIERE")
    {
        return Money.Zero;
    }
    else if (program == "STUDENT_WEEK" && ticketType == "S")
    {
        return basePrice.Percent(50);
    }
    else if (program == "STANDARD" || program == "STUDENT_WEEK")
    {
        var percent = ticketType switch
        {
            "N" => 0,
            "S" => 25,
            "E" => 30,
            "C" => 40,
            _ => throw new ArgumentException("unknown ticket type: " + ticketType),
        };
        return basePrice.Percent(percent);
    }
    throw new ArgumentException("unknown program: " + program);
}

private sealed class LegacyPolicy(string program) : IDiscountPolicy
{
    public Money Discount(Money basePrice, string ticketType) => LegacyDiscount(basePrice, ticketType, program);
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s01` - zielone.
**Co powiedzieć:** pierwszy commit jest mały i odwracalny: kontrakt strategii istnieje, ale deleguje do starego kodu. Nie zaczynamy od tworzenia wszystkich klas wzorca.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s01 0 1`

### Krok 2: przeniesienie gałęzi do strategii

**Zmiany:**

1. Dla każdej gałęzi osobno: utwórz nową klasę implementującą `IDiscountPolicy`, przenieś do jej metody `Discount` ciało gałęzi i uruchom test. Kolejno: `StandardDiscount` (switch po `ticketType`), `PremiereDiscount` (zawsze `Money.Zero`), `StudentWeekDiscount` (student 50%, nie-studentów deleguje do przekazanego w konstruktorze `StandardDiscount`).
2. W `TicketPricer` zastąp całą metodę `LegacyDiscount` (usuń ją) prywatną metodą statyczną `PolicyFor(string program)` zwracającą strategię ze switch expression; nieznany program nadal rzuca `ArgumentException`.
3. Usuń klasę `LegacyPolicy`, a w `Price` wywołaj `PolicyFor(program).Discount(basePrice, ticketType)`.

**Przed (`TicketPricer.cs`):**

```csharp
public Money Price(Money basePrice, string ticketType, string? program)
{
    if (basePrice.CompareTo(Money.Zero) < 0)
    {
        throw new ArgumentException("base price must not be negative");
    }
    if (program == null)
    {
        throw new ArgumentException("program must not be null");
    }
    IDiscountPolicy policy = new LegacyPolicy(program);
    return basePrice.Minus(policy.Discount(basePrice, ticketType));
}
```

(plus usuwane w tym kroku `LegacyDiscount` i `LegacyPolicy` - pełna treść w Po kroku 1)

**Po (`StandardDiscount.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public sealed class StandardDiscount : IDiscountPolicy
{
    public Money Discount(Money basePrice, string ticketType)
    {
        var percent = ticketType switch
        {
            "N" => 0,
            "S" => 25,
            "E" => 30,
            "C" => 40,
            _ => throw new ArgumentException("unknown ticket type: " + ticketType),
        };
        return basePrice.Percent(percent);
    }
}
```

**Po (`PremiereDiscount.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public sealed class PremiereDiscount : IDiscountPolicy
{
    public Money Discount(Money basePrice, string ticketType)
    {
        return Money.Zero;
    }
}
```

**Po (`StudentWeekDiscount.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public sealed class StudentWeekDiscount : IDiscountPolicy
{
    private readonly IDiscountPolicy _fallback;

    public StudentWeekDiscount(IDiscountPolicy fallback)
    {
        _fallback = fallback;
    }

    public Money Discount(Money basePrice, string ticketType)
    {
        return ticketType == "S" ? basePrice.Percent(50) : _fallback.Discount(basePrice, ticketType);
    }
}
```

**Po (`TicketPricer.cs`):**

```csharp
public Money Price(Money basePrice, string ticketType, string? program)
{
    if (basePrice.CompareTo(Money.Zero) < 0)
    {
        throw new ArgumentException("base price must not be negative");
    }
    if (program == null)
    {
        throw new ArgumentException("program must not be null");
    }
    return basePrice.Minus(PolicyFor(program).Discount(basePrice, ticketType));
}

private static IDiscountPolicy PolicyFor(string program)
{
    return program switch
    {
        "STANDARD" => new StandardDiscount(),
        "STUDENT_WEEK" => new StudentWeekDiscount(new StandardDiscount()),
        "PREMIERE" => new PremiereDiscount(),
        _ => throw new ArgumentException("unknown program: " + program),
    };
}
```

**Uruchom:** test po każdej przeniesionej gałęzi.
**Co powiedzieć:** walidacja zostaje w kontekście, strategie są bezstanowe. `switch` nie zniknął - przeniósł się w jedno miejsce wyboru, a to jest w porządku.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s01 1 2`

### Krok 3: wybór strategii w korzeniu kompozycji

**Zmiany:**

1. Utwórz statyczną klasę `DiscountPrograms` i przenieś do niej `PolicyFor` pod nazwą `ForName(string? program)` (przeniesienie metody). Instancje strategii trzymaj jako pola `static readonly` (bezstanowe, więc współdzielone). Walidacja `program == null` przenosi się tutaj.
2. Zmień sygnatury `TicketPricer` (zmiana sygnatury): konstruktor przyjmuje `IDiscountPolicy` (z `ArgumentNullException.ThrowIfNull`), a `Price(basePrice, ticketType)` traci parametr `program`. Walidacja ujemnej ceny zostaje w `Price`.
3. Popraw klienta `PriceBoard`: zamiast pola z gotowym `TicketPricer` składa kontekst przy każdym żądaniu - `new TicketPricer(DiscountPrograms.ForName(request.Program))`.

**Przed (`TicketPricer.cs`):**

```csharp
public Money Price(Money basePrice, string ticketType, string? program)
{
    if (basePrice.CompareTo(Money.Zero) < 0)
    {
        throw new ArgumentException("base price must not be negative");
    }
    if (program == null)
    {
        throw new ArgumentException("program must not be null");
    }
    return basePrice.Minus(PolicyFor(program).Discount(basePrice, ticketType));
}

private static IDiscountPolicy PolicyFor(string program)
{
    return program switch
    {
        "STANDARD" => new StandardDiscount(),
        "STUDENT_WEEK" => new StudentWeekDiscount(new StandardDiscount()),
        "PREMIERE" => new PremiereDiscount(),
        _ => throw new ArgumentException("unknown program: " + program),
    };
}
```

**Przed (`PriceBoard.cs`):**

```csharp
private readonly TicketPricer _pricer = new();

public Money PriceFor(PriceRequest request)
{
    return _pricer.Price(request.Base, request.TicketType, request.Program);
}
```

**Po (`DiscountPrograms.cs`, nowy plik):**

```csharp
public static class DiscountPrograms
{
    private static readonly IDiscountPolicy Standard = new StandardDiscount();
    private static readonly IDiscountPolicy StudentWeek = new StudentWeekDiscount(Standard);
    private static readonly IDiscountPolicy Premiere = new PremiereDiscount();

    public static IDiscountPolicy ForName(string? program)
    {
        if (program == null)
        {
            throw new ArgumentException("program must not be null");
        }
        return program switch
        {
            "STANDARD" => Standard,
            "STUDENT_WEEK" => StudentWeek,
            "PREMIERE" => Premiere,
            _ => throw new ArgumentException("unknown program: " + program),
        };
    }
}
```

**Po (`TicketPricer.cs`):** usuń `PolicyFor`, cała klasa wygląda teraz tak:

```csharp
public sealed class TicketPricer
{
    private readonly IDiscountPolicy _policy;

    public TicketPricer(IDiscountPolicy policy)
    {
        ArgumentNullException.ThrowIfNull(policy);
        _policy = policy;
    }

    public Money Price(Money basePrice, string ticketType)
    {
        if (basePrice.CompareTo(Money.Zero) < 0)
        {
            throw new ArgumentException("base price must not be negative");
        }
        return basePrice.Minus(_policy.Discount(basePrice, ticketType));
    }
}
```

**Po (`PriceBoard.cs`):** pole `_pricer` znika:

```csharp
public Money PriceFor(PriceRequest request)
{
    return new TicketPricer(DiscountPrograms.ForName(request.Program))
        .Price(request.Base, request.TicketType);
}
```

**Uruchom:** test zielony; `S01SolutionTest` pokazuje nowy program zniżek (`BlackFridayDiscount`) bez zmiany kontekstu.
**Co powiedzieć:** wybór w konstruktorze **zamraża decyzję** i przesuwa moment błędu: w Start ujemna cena zgłaszała się przed nieznanym programem, teraz jest odwrotnie. To dokumentuje test `ChoosingInConstructorMovesTheUnknownProgramErrorEarlier` - taka zmiana wymaga świadomej zgody, nie jest "za darmo".
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s01 2 3`

### Rozwiązanie i uzasadnienie

`Step3`: `TicketPricer` zna tylko `IDiscountPolicy`, a nazwy programów zna `DiscountPrograms`. Nowy program zniżek to nowa strategia i wpis w jednym `switch`. Kontekst trzyma wspólną walidację.

### Pułapki

- `Func<Money, string, Money>` zamiast własnego interfejsu - działa, ale gubi nazwę pojęcia i nazwy parametrów w kontrakcie.
- Strategia z polami zmienianymi podczas liczenia, a potem współdzielona między wątkami.
- Porównywanie strategii-delegatów przez `==` - lambda nie ma stabilnej tożsamości, każde wyrażenie może dać nowy obiekt delegata.
- Zamknięcie punktu rozszerzeń (np. klasa bazowa z konstruktorem `private protected` zamiast interfejsu) - punkt rozszerzeń ma być otwarty.

### Pytanie do sali

Czy przy dwóch programach i jednym `if` Strategy nadal byłaby uzasadniona? Co musiałoby się zmieniać, żeby tak?

## Scena s02. Replace Conditional with Polymorphism - rodzaj seansu

**Temat ze slajdów:** 16.1. Zastąpienie instrukcji warunkowej polimorfizmem; 6.8. Typowe pomyłki przy wyborze
**Namespace:** `Training.Workshop.M5.S02Polymorphism` · **Test:** `scripts/warsztat.sh --lang cs test m5/s02` (`S02EquivalenceTest`, `S02SolutionTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `Screening` ma pole `_kind` i ten sam `switch` w trzech metodach, a pole `_value` znaczy raz minuty filmu, raz liczbę filmów. Wydzielamy podtyp dla każdego rodzaju seansu po kolei, aż zostaje zamknięta hierarchia rekordów z nazwanymi danymi.

**Zasada:** Replace Conditional with Polymorphism przenosi zachowanie zależne od rodzaju do podtypów, gdy selektor opisuje trwały rodzaj obiektu. Jeśli rodzaj zmienia się w czasie życia obiektu, to State, a jeśli algorytm wybiera klient - Strategy. Zaczynamy od znalezienia wszystkich miejsc tworzenia i deserializacji.

**Efekt:** Switche w zachowaniu znikają, zostaje jeden w `FromRow`, gdzie należy wiedza o konstrukcji, a dane mają jednoznaczne nazwy. Hierarchia jest zamknięta w assembly, ale kompilator C# nie sprawdza wyczerpania `switch` po typach - listę rodzajów pilnuje test.

**Różnica względem Javy:** C# nie ma `sealed ... permits` ani `sealed interface`. W krokach 1-2 hierarchię zamyka konstruktor bazy `private protected` (podklasy tylko w tym assembly), a podklasy są `sealed` z konstruktorami `internal`. Krok 3 to `abstract record Screening` z konstruktorem `private protected` i trzy `sealed record`. Switch po typach bez ramienia `_` daje ostrzeżenie CS8509 (przy `TreatWarningsAsErrors` - błąd), więc w `S02SolutionTest.ClientSwitchIsExhaustiveWithoutDefault` klient ma ramię `_ => throw new UnreachableException(...)`, a sygnał "nowy rodzaj psuje kompilację" zastępuje asercja refleksyjna: nieabstrakcyjne podtypy `Screening` to dokładnie Marathon, Premiere, Regular. Z tego samego powodu (CS8524) switche po enumie `Kind` w Start i kroku 1 mają ramię `_ => throw new ArgumentOutOfRangeException(...)`.

### Co widzimy

`Screening` ma pole `_kind` (Regular, Premiere, Marathon) i ten sam `switch` w trzech metodach: `Label()`, `DurationMinutes()`, `Price()`. Pole `_value` znaczy raz "minuty filmu", raz "liczba filmów". Rodzaj seansu jest **trwałą cechą obiektu** - nie zmienia się w czasie życia, więc to nie State, a klient go nie wybiera, więc to nie Strategy.

```csharp
public int DurationMinutes()
{
    return _kind switch
    {
        Kind.Regular => 20 + _value,
        Kind.Premiere => 30 + _value,
        Kind.Marathon => _value * 120 + (_value - 1) * 15,
        _ => throw new ArgumentOutOfRangeException(nameof(_kind)),
    };
}
```

Tworzenie jest w jednym miejscu: `Screening.FromRow(ScreeningRow)` (mapowanie wiersza z bazy). Zawsze najpierw szukamy miejsc tworzenia i deserializacji (wszystkich użyć konstruktora).

### Krok 1: Extract Subclass dla jednej gałęzi

**Zmiany:**

1. W `Screening` usuń `sealed`, konstruktor zmień z `private` na `private protected` (podklasy tylko w tym assembly), a `Label`, `DurationMinutes` i `Price` oznacz `virtual`. Dodaj `protected string Title => _title;`, żeby podklasa widziała tytuł.
2. Utwórz nowy plik `MarathonScreening.cs`: `public sealed class MarathonScreening : Screening` z konstruktorem `internal` i polem `_films`. Nadpisz (`override`) trzy metody i skopiuj do nich ciała gałęzi `Kind.Marathon`.
3. W `FromRow` gałąź "MARATHON" tworzy `new MarathonScreening(row.Title, row.Value)`; pozostałe gałęzie tworzą bazę wprost (`new Screening(Kind.Regular, ...)`).
4. W bazie martwe już gałęzie `Kind.Marathon` zamień na `throw new InvalidOperationException("handled by MarathonScreening")` - w `Label`, `DurationMinutes` i `Price` tak samo.

**Przed (`Screening.cs`):**

```csharp
public static Screening FromRow(ScreeningRow row)
{
    var kind = row.Kind switch
    {
        "REGULAR" => Kind.Regular,
        "PREMIERE" => Kind.Premiere,
        "MARATHON" => Kind.Marathon,
        _ => throw new ArgumentException("unknown screening kind: " + row.Kind),
    };
    return new Screening(kind, row.Title, row.Value);
}

public int DurationMinutes()
{
    return _kind switch
    {
        Kind.Regular => 20 + _value,
        Kind.Premiere => 30 + _value,
        Kind.Marathon => _value * 120 + (_value - 1) * 15,
        _ => throw new ArgumentOutOfRangeException(nameof(_kind)),
    };
}
```

**Po (`MarathonScreening.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public sealed class MarathonScreening : Screening
{
    private readonly int _films;

    internal MarathonScreening(string title, int films)
        : base(Kind.Marathon, title, films)
    {
        _films = films;
    }

    public override string Label()
    {
        return "Maraton: " + Title + " (" + _films + " filmy)";
    }

    public override int DurationMinutes()
    {
        return _films * 120 + (_films - 1) * 15;
    }

    public override Money Price()
    {
        return Money.Of("20.00").Times(_films);
    }
}
```

**Po (`Screening.cs`):**

```csharp
public class Screening
{
    public enum Kind { Regular, Premiere, Marathon }

    private readonly Kind _kind;
    private readonly string _title;
    private readonly int _value;

    private protected Screening(Kind kind, string title, int value)
    {
        _kind = kind;
        _title = title;
        _value = value;
    }

    public static Screening FromRow(ScreeningRow row)
    {
        return row.Kind switch
        {
            "REGULAR" => new Screening(Kind.Regular, row.Title, row.Value),
            "PREMIERE" => new Screening(Kind.Premiere, row.Title, row.Value),
            "MARATHON" => new MarathonScreening(row.Title, row.Value),
            _ => throw new ArgumentException("unknown screening kind: " + row.Kind),
        };
    }

    protected string Title => _title;

    public virtual int DurationMinutes()
    {
        return _kind switch
        {
            Kind.Regular => 20 + _value,
            Kind.Premiere => 30 + _value,
            Kind.Marathon => throw new InvalidOperationException("handled by MarathonScreening"),
            _ => throw new ArgumentOutOfRangeException(nameof(_kind)),
        };
    }
    // Label() i Price() analogicznie: virtual, gałąź Kind.Marathon rzuca InvalidOperationException,
    // pozostałe gałęzie bez zmian.
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s02` - zielone.
**Co powiedzieć:** jedna gałąź naraz, jeden test kontraktowy dla wszystkich rodzajów. Martwa gałąź rzuca wyjątek zamiast po cichu liczyć bzdury.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s02 0 1`

### Krok 2: pozostałe podklasy, baza abstrakcyjna

**Zmiany:**

1. Powtórz ruch z kroku 1 dla pozostałych rodzajów: nowe pliki `RegularScreening.cs` i `PremiereScreening.cs` z ciałami swoich gałęzi; `FromRow` tworzy już tylko podklasy.
2. Oznacz klasę `Screening` jako `abstract`, a `Label`, `DurationMinutes` i `Price` jako `abstract` (bez ciał).
3. Usuń pole `_kind`, pole `_value` i enum `Kind` (bezpieczne usunięcie - najpierw upewnij się, że nie mają użyć). Konstruktor bazy zostaje z samym `title`. Jedynym `switch` zostaje `FromRow`.
4. W `MarathonScreening` popraw wywołanie konstruktora bazy: `base(Kind.Marathon, title, films)` zamienia się na `base(title)`.

**Przed:**

```csharp
public virtual int DurationMinutes()
{
    return _kind switch
    {
        Kind.Regular => 20 + _value,
        Kind.Premiere => 30 + _value,
        Kind.Marathon => throw new InvalidOperationException("handled by MarathonScreening"),
        _ => throw new ArgumentOutOfRangeException(nameof(_kind)),
    };
}
```

**Po (`RegularScreening.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public sealed class RegularScreening : Screening
{
    private readonly int _runtime;

    internal RegularScreening(string title, int runtime)
        : base(title)
    {
        _runtime = runtime;
    }

    public override string Label()
    {
        return Title;
    }

    public override int DurationMinutes()
    {
        return 20 + _runtime;
    }

    public override Money Price()
    {
        return Money.Of("25.00");
    }
}
```

**Po (`PremiereScreening.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public sealed class PremiereScreening : Screening
{
    private readonly int _runtime;

    internal PremiereScreening(string title, int runtime)
        : base(title)
    {
        _runtime = runtime;
    }

    public override string Label()
    {
        return "Premiera: " + Title;
    }

    public override int DurationMinutes()
    {
        return 30 + _runtime;
    }

    public override Money Price()
    {
        return Money.Of("35.00");
    }
}
```

**Po (`Screening.cs`, cały plik):**

```csharp
using Training.Workshop.Shared;

public abstract class Screening
{
    private readonly string _title;

    private protected Screening(string title)
    {
        _title = title;
    }

    public static Screening FromRow(ScreeningRow row)
    {
        return row.Kind switch
        {
            "REGULAR" => new RegularScreening(row.Title, row.Value),
            "PREMIERE" => new PremiereScreening(row.Title, row.Value),
            "MARATHON" => new MarathonScreening(row.Title, row.Value),
            _ => throw new ArgumentException("unknown screening kind: " + row.Kind),
        };
    }

    protected string Title => _title;

    public abstract string Label();

    public abstract int DurationMinutes();

    public abstract Money Price();
}
```

**Po (`MarathonScreening.cs`):**

```csharp
internal MarathonScreening(string title, int films)
    : base(title)
{
    _films = films;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** `switch` w miejscu tworzenia jest właściwy - to jest wiedza o konstrukcji. Znikają za to switche w zachowaniu.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s02 1 2`

### Krok 3: forma C# - abstrakcyjny rekord i zapieczętowane rekordy

**Zmiany:**

1. Zamień klasę bazową na `abstract record Screening` z bezparametrowym konstruktorem `private protected`, statycznym `FromRow` i abstrakcyjną właściwością `Title` (pole `_title` znika).
2. Zamień każdą podklasę na pozycyjny `sealed record` w miejscu klasy z polami: `RegularScreening(string Title, int Runtime)`, `PremiereScreening(string Title, int Runtime)`, `MarathonScreening(string Title, int Films)`. Każdy rekord nadpisuje `Title` i trzy metody.
3. Dane mają teraz jednoznaczne nazwy (`Runtime`, `Films`) - dwuznaczne `_value` znika z kodu.

**Przed:** wszystkie cztery pliki w wersji z kroku 2 (klasy z polami `_title`, `_runtime`, `_films` i konstruktorami `internal`).

**Po (`Screening.cs`, cały plik):**

```csharp
using Training.Workshop.Shared;

public abstract record Screening
{
    private protected Screening()
    {
    }

    public static Screening FromRow(ScreeningRow row)
    {
        return row.Kind switch
        {
            "REGULAR" => new RegularScreening(row.Title, row.Value),
            "PREMIERE" => new PremiereScreening(row.Title, row.Value),
            "MARATHON" => new MarathonScreening(row.Title, row.Value),
            _ => throw new ArgumentException("unknown screening kind: " + row.Kind),
        };
    }

    public abstract string Title { get; }

    public abstract string Label();

    public abstract int DurationMinutes();

    public abstract Money Price();
}
```

**Po (`RegularScreening.cs`, cały plik):**

```csharp
using Training.Workshop.Shared;

public sealed record RegularScreening(string Title, int Runtime) : Screening
{
    public override string Title { get; } = Title;

    public override string Label()
    {
        return Title;
    }

    public override int DurationMinutes()
    {
        return 20 + Runtime;
    }

    public override Money Price()
    {
        return Money.Of("25.00");
    }
}
```

**Po (`PremiereScreening.cs`, cały plik):**

```csharp
using Training.Workshop.Shared;

public sealed record PremiereScreening(string Title, int Runtime) : Screening
{
    public override string Title { get; } = Title;

    public override string Label()
    {
        return "Premiera: " + Title;
    }

    public override int DurationMinutes()
    {
        return 30 + Runtime;
    }

    public override Money Price()
    {
        return Money.Of("35.00");
    }
}
```

**Po (`MarathonScreening.cs`, cały plik):**

```csharp
using Training.Workshop.Shared;

public sealed record MarathonScreening(string Title, int Films) : Screening
{
    public override string Title { get; } = Title;

    public override string Label()
    {
        return "Maraton: " + Title + " (" + Films + " filmy)";
    }

    public override int DurationMinutes()
    {
        return Films * 120 + (Films - 1) * 15;
    }

    public override Money Price()
    {
        return Money.Of("20.00").Times(Films);
    }
}
```

**Uruchom:** test zielony; `S02SolutionTest` zawiera `switch` klienta z ramieniem `_ => throw new UnreachableException(...)` i asercję listy podtypów.
**Co powiedzieć:** zamknięty zestaw rodzajów daje w Javie kontrolę kompilatora, a w C# tylko częściową: konstruktor `private protected` nie wpuści podtypów spoza assembly, ale nowy rodzaj w tym assembly nie zepsuje kompilacji żadnego `switch` - wyjdzie jako `UnreachableException` w runtime albo jako czerwony test listy rodzajów. Koszt jest więc inny niż w Javie: mniej pilnowania przez kompilator, za to nowy rodzaj nie psuje kodu poza modułem.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s02 2 3`

### Rozwiązanie i uzasadnienie

Trzy rekordy z własnymi danymi i zachowaniem. Znaczenie pól jest jednoznaczne, a test kontraktowy przechodzi przez wszystkie rodzaje, łącznie z nieznanym rodzajem w danych.

### Pułapki

- Pominięcie miejsc tworzenia (ORM, deserializator JSON, fabryka w innym projekcie) - obiekt powstanie jako zły podtyp albo wcale.
- Wywołanie metody wirtualnej z konstruktora bazy - podklasa widzi pola, których jej konstruktor jeszcze nie ustawił.
- Składowe `static` nie są polimorficzne - "nadpisanie" stałej w podklasie (`new`) nic nie daje przy wywołaniu przez typ bazowy.

### Pytanie do sali

Seans może zmienić się z "premiery" w "zwykły" po tygodniu. Czy to nadal polimorfizm, czy już State?

## Scena s03. Replace Type Code with Class - format jako typ

**Temat ze slajdów:** 16.2. Zastąpienie kodu typu klasą
**Namespace:** `Training.Workshop.M5.S03TypeCode` · **Test:** `scripts/warsztat.sh --lang cs test m5/s03` (`S03EquivalenceTest`, `S03SolutionTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** Format seansu jest surowym `int` z CSV, a wiedza o kodach jest rozsiana po trzech metodach. Zamieniamy liczbę na enum `Format` tuż po odczycie, przenosimy do jego rozszerzeń etykietę, cenę i okulary, a tłumaczenie kodu trwałego zamykamy w mapperze `FormatCodes`.

**Zasada:** Replace Type Code with Class zastępuje prymitywny kod typem, który przejmuje walidację, normalizację i operacje pojęcia - co nie oznacza automatycznie hierarchii podklas. Enum wystarcza dla małego, zamkniętego zestawu, klasa jest lepsza przy aliasach i kodach zewnętrznych. Na granicy trwałości zostaje stabilny kod, nigdy `(int)format` ani `ToString()`.

**Efekt:** Poza mapperem żaden kod nie operuje na liczbach 1, 2, 3, a zapis CSV jest bez zmian. Zmiana formatu trwałego (np. na kody tekstowe) dotknie tylko mappera, ale to osobna decyzja, nie część tej refaktoryzacji.

**Różnica względem Javy:** enum C# nie ma pól ani metod. `Format` to zwykły `enum Format { TwoD, ThreeD, Imax }`, a operacje typu są w bloku członków rozszerzeń C# 14 (`extension(Format format)` w statycznej klasie `FormatExtensions`): w kroku 1 `format.Code` i statyczne `Format.FromCode(int)`, w kroku 2 właściwości `Label`, `BasePrice`, `RequiresGlasses` (jeden `switch` na operację zamiast danych w konstruktorach stałych), w kroku 3 mapowanie w `FormatCodes` na `Dictionary<Format, int>` zamiast `EnumMap`. Odpowiednikiem pułapki `ordinal()` jest `(int)format`. Przypadek "kod nie jest liczbą": `int.Parse` rzuca `FormatException`, które nie dziedziczy po `ArgumentException`; oczekiwany tekst to komunikat .NET `ERROR: The input string 'x' was not in a correct format.`.

### Co widzimy

Format seansu jest surowym `int` (1=2D, 2=3D, 3=IMAX) czytanym z CSV `Diuna;3`. Wiedza o kodzie jest w trzech metodach, a `NeedsGlasses` w ogóle nie waliduje kodu. Plik CSV musi nadal przechowywać `int` - to jest **granica trwałości**.

```csharp
private static Money BasePrice(int formatCode)
{
    return formatCode switch
    {
        Format2D => Money.Of("25.00"),
        ...
        _ => throw new ArgumentException("unknown format code: " + formatCode),
    };
}
```

### Krok 1: nowy typ i konwersja na granicy

**Zmiany:**

1. Utwórz nowy plik `Format.cs`: `enum Format { TwoD, ThreeD, Imax }` i obok statyczna klasa `FormatExtensions` z blokiem `extension(Format format)`. W bloku: właściwość `Code` (switch z jawnym przypisaniem 1, 2, 3) i statyczna metoda `FromCode(int)`, która szuka stałej o danym `Code`, a dla nieznanego kodu rzuca ten sam `ArgumentException` co Start.
2. W `Describe` zamień `int` na `Format` zaraz po odczycie: `var format = Format.FromCode(int.Parse(...))`.
3. Zmień sygnatury metod prywatnych `Label`, `BasePrice`, `NeedsGlasses`, `ToCsv` z `int` na `Format` (zmiana sygnatury); switche przechodzą na stałe enuma.
4. Usuń stałe `Format2D`, `Format3D`, `FormatImax`. Do CSV zapisuj `format.Code`.

**Przed (`ScreeningCsv.cs`, cały plik):**

```csharp
using System.Globalization;
using Training.Workshop.Shared;

public sealed class ScreeningCsv
{
    public const int Format2D = 1;
    public const int Format3D = 2;
    public const int FormatImax = 3;

    /// <summary>Wiersz "tytuł;kod" - np. "Diuna;3".</summary>
    public string Describe(string line)
    {
        var parts = line.Split(';');
        var title = parts[0];
        var formatCode = int.Parse(parts[1].Trim(), CultureInfo.InvariantCulture);
        return title + "|" + Label(formatCode) + "|" + BasePrice(formatCode)
            + "|okulary:" + (NeedsGlasses(formatCode) ? "tak" : "nie")
            + "|csv=" + ToCsv(title, formatCode);
    }

    private static string Label(int formatCode)
    {
        return formatCode switch
        {
            Format2D => "2D",
            Format3D => "3D",
            FormatImax => "IMAX",
            _ => throw new ArgumentException("unknown format code: " + formatCode),
        };
    }

    private static Money BasePrice(int formatCode)
    {
        return formatCode switch
        {
            Format2D => Money.Of("25.00"),
            Format3D => Money.Of("32.00"),
            FormatImax => Money.Of("40.00"),
            _ => throw new ArgumentException("unknown format code: " + formatCode),
        };
    }

    private static bool NeedsGlasses(int formatCode)
    {
        return formatCode == Format3D;
    }

    private static string ToCsv(string title, int formatCode)
    {
        return title + ";" + formatCode;
    }
}
```

**Po (`Format.cs`, nowy plik):**

```csharp
public enum Format
{
    TwoD,
    ThreeD,
    Imax,
}

/// <summary>Operacje typu Format (C# 14: członkowie rozszerzeń - enum nie ma własnych metod).</summary>
public static class FormatExtensions
{
    extension(Format format)
    {
        public int Code => format switch
        {
            Format.TwoD => 1,
            Format.ThreeD => 2,
            Format.Imax => 3,
            _ => throw new ArgumentOutOfRangeException(nameof(format)),
        };

        public static Format FromCode(int code)
        {
            foreach (var candidate in Enum.GetValues<Format>())
            {
                if (candidate.Code == code)
                {
                    return candidate;
                }
            }
            throw new ArgumentException("unknown format code: " + code);
        }
    }
}
```

**Po (`ScreeningCsv.cs`, cały plik):**

```csharp
using System.Globalization;
using Training.Workshop.Shared;

public sealed class ScreeningCsv
{
    /// <summary>Wiersz "tytuł;kod" - np. "Diuna;3".</summary>
    public string Describe(string line)
    {
        var parts = line.Split(';');
        var title = parts[0];
        var format = Format.FromCode(int.Parse(parts[1].Trim(), CultureInfo.InvariantCulture));
        return title + "|" + Label(format) + "|" + BasePrice(format)
            + "|okulary:" + (NeedsGlasses(format) ? "tak" : "nie")
            + "|csv=" + ToCsv(title, format);
    }

    private static string Label(Format format)
    {
        return format switch
        {
            Format.TwoD => "2D",
            Format.ThreeD => "3D",
            Format.Imax => "IMAX",
            _ => throw new ArgumentOutOfRangeException(nameof(format)),
        };
    }

    private static Money BasePrice(Format format)
    {
        return format switch
        {
            Format.TwoD => Money.Of("25.00"),
            Format.ThreeD => Money.Of("32.00"),
            Format.Imax => Money.Of("40.00"),
            _ => throw new ArgumentOutOfRangeException(nameof(format)),
        };
    }

    private static bool NeedsGlasses(Format format)
    {
        return format == Format.ThreeD;
    }

    private static string ToCsv(string title, Format format)
    {
        return title + ";" + format.Code;
    }
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s03` - zielone, łącznie z "nieznany kod" i "kod nie jest liczbą".
**Co powiedzieć:** enum wystarcza, bo zestaw jest mały i zamknięty. Trwały kod to jawne przypisanie, nigdy `(int)format` ani `ToString()`.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s03 0 1`

### Krok 2: Move Method do typu

**Zmiany:**

1. Przenieś `Label`, `BasePrice` i `NeedsGlasses` z `ScreeningCsv` do bloku rozszerzeń jako właściwości `Label`, `BasePrice`, `RequiresGlasses` (przeniesienie metody): wytnij każdy `switch` i wklej jako ciało właściwości w `extension(Format format)`.
2. Kwoty wyciągnij do pól `static readonly` w `FormatExtensions` (`TwoDPrice`, `ThreeDPrice`, `ImaxPrice`).
3. W `Describe` zastąp wywołania metod odczytem właściwości (`format.Label`, `format.BasePrice`, `format.RequiresGlasses`), a `ToCsv` wpisz wprost w wynik: `title + ";" + format.Code`. Prywatne metody znikają z `ScreeningCsv`.

**Przed:**

```csharp
return title + "|" + Label(format) + "|" + BasePrice(format)
    + "|okulary:" + (NeedsGlasses(format) ? "tak" : "nie")
    + "|csv=" + ToCsv(title, format);
...
private static string Label(Format format)
{
    return format switch
    {
        Format.TwoD => "2D",
        Format.ThreeD => "3D",
        Format.Imax => "IMAX",
        _ => throw new ArgumentOutOfRangeException(nameof(format)),
    };
}
```

**Po (`Format.cs`):** enum bez zmian, klasa `FormatExtensions` wygląda teraz tak (dochodzi `using Training.Workshop.Shared;` na górze pliku):

```csharp
public static class FormatExtensions
{
    private static readonly Money TwoDPrice = Money.Of("25.00");
    private static readonly Money ThreeDPrice = Money.Of("32.00");
    private static readonly Money ImaxPrice = Money.Of("40.00");

    extension(Format format)
    {
        public int Code => format switch
        {
            Format.TwoD => 1,
            Format.ThreeD => 2,
            Format.Imax => 3,
            _ => throw new ArgumentOutOfRangeException(nameof(format)),
        };

        public string Label => format switch
        {
            Format.TwoD => "2D",
            Format.ThreeD => "3D",
            Format.Imax => "IMAX",
            _ => throw new ArgumentOutOfRangeException(nameof(format)),
        };

        public Money BasePrice => format switch
        {
            Format.TwoD => TwoDPrice,
            Format.ThreeD => ThreeDPrice,
            Format.Imax => ImaxPrice,
            _ => throw new ArgumentOutOfRangeException(nameof(format)),
        };

        public bool RequiresGlasses => format == Format.ThreeD;

        public static Format FromCode(int code)
        {
            foreach (var candidate in Enum.GetValues<Format>())
            {
                if (candidate.Code == code)
                {
                    return candidate;
                }
            }
            throw new ArgumentException("unknown format code: " + code);
        }
    }
}
```

**Po (`ScreeningCsv.cs`, cały plik):** metody prywatne i `using Training.Workshop.Shared;` znikają:

```csharp
using System.Globalization;

public sealed class ScreeningCsv
{
    /// <summary>Wiersz "tytuł;kod" - np. "Diuna;3".</summary>
    public string Describe(string line)
    {
        var parts = line.Split(';');
        var title = parts[0];
        var format = Format.FromCode(int.Parse(parts[1].Trim(), CultureInfo.InvariantCulture));
        return title + "|" + format.Label + "|" + format.BasePrice
            + "|okulary:" + (format.RequiresGlasses ? "tak" : "nie")
            + "|csv=" + title + ";" + format.Code;
    }
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** nowy typ przejmuje walidację, normalizację i operacje pojęcia. Nie oznacza to automatycznie hierarchii klas. W C# "typ z operacjami" to enum plus członkowie rozszerzeń - wywołanie `format.Label` wygląda jak właściwość typu.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s03 1 2`

### Krok 3: mapper migracyjny na granicy trwałości

**Zmiany:**

1. Utwórz nowy plik `FormatCodes.cs`: statyczna klasa ze słownikiem `Dictionary<Format, int>` (trzymanym jako `IReadOnlyDictionary`) i metodami `FromCode(int)` (szuka wpisu po wartości; nieznany kod rzuca ten sam `ArgumentException`) oraz `ToCode(Format)`.
2. Usuń `Code` i `FromCode` z bloku rozszerzeń - typ domenowy traci wiedzę o kodach trwałych.
3. W `ScreeningCsv.Describe` czytaj format przez `FormatCodes.FromCode(...)`, a do CSV zapisuj `FormatCodes.ToCode(format)`. Reszta kodu rozmawia wyłącznie z `Format`.

**Przed:**

```csharp
extension(Format format)
{
    public int Code => format switch
    {
        Format.TwoD => 1,
        Format.ThreeD => 2,
        Format.Imax => 3,
        _ => throw new ArgumentOutOfRangeException(nameof(format)),
    };
    ...
    public static Format FromCode(int code) { ... }
}
...
var format = Format.FromCode(int.Parse(parts[1].Trim(), CultureInfo.InvariantCulture));
... + "|csv=" + title + ";" + format.Code;
```

**Po (`FormatCodes.cs`, nowy plik):**

```csharp
public static class FormatCodes
{
    private static readonly IReadOnlyDictionary<Format, int> Codes = new Dictionary<Format, int>
    {
        [Format.TwoD] = 1,
        [Format.ThreeD] = 2,
        [Format.Imax] = 3,
    };

    public static Format FromCode(int code)
    {
        foreach (var entry in Codes)
        {
            if (entry.Value == code)
            {
                return entry.Key;
            }
        }
        throw new ArgumentException("unknown format code: " + code);
    }

    public static int ToCode(Format format)
    {
        return Codes[format];
    }
}
```

**Po (`ScreeningCsv.cs`):**

```csharp
public string Describe(string line)
{
    var parts = line.Split(';');
    var title = parts[0];
    var format = FormatCodes.FromCode(int.Parse(parts[1].Trim(), CultureInfo.InvariantCulture));
    return title + "|" + format.Label + "|" + format.BasePrice
        + "|okulary:" + (format.RequiresGlasses ? "tak" : "nie")
        + "|csv=" + title + ";" + FormatCodes.ToCode(format);
}
```

**Uruchom:** test zielony; `S03SolutionTest.PersistentCodeIsNotTheOrdinal` sprawdza, że `(int)Format.Imax` to 2, a kod trwały to 3.
**Co powiedzieć:** gdy kiedyś przejdziemy na kody tekstowe ("IMAX" w bazie), zmieni się tylko mapper - może przez pewien czas czytać oba formaty. Zmiana formatu trwałego to osobna decyzja, nie część tej refaktoryzacji.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s03 2 3`

### Rozwiązanie i uzasadnienie

Typ domenowy bez wiedzy o bazie plus mapper na granicy. Enum, bo zestaw jest zamknięty. Klasę z prywatnym konstruktorem i instancjami statycznymi (w tym porcie `Format` ze sceny s20) wybralibyśmy przy aliasach, kodach zewnętrznych albo gdy lista wartości pochodzi z konfiguracji.

### Pułapki

- Zapis `(int)format` do bazy - dodanie wartości w środku enuma przestawia dane.
- Zapis `ToString()` (nazwy stałej) - ktoś ją "upiększy" przy Rename i CSV przestanie się wczytywać.
- Zmiana komunikatu wyjątku przy nieznanym kodzie - to też obserwowalne zachowanie (logi, monitoring).

### Pytanie do sali

Kiedy wybralibyście klasę zamiast enuma dla formatu? Co w domenie kina mogłoby to wymusić?

## Scena s04. Encapsulate Classes with Factory - bilety

**Temat ze slajdów:** 10.1. Ukrycie klas za Fabryką; 10.3. Fabryka jako strażnik spójności
**Namespace:** `Training.Workshop.M5.S04EncapsulateFactory` · **Test:** `scripts/warsztat.sh --lang cs test m5/s04` (`S04EquivalenceTest`, `S04SolutionTest`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** `BoxOffice` tworzy `StandardTicket` i `VipTicket` przez `new` i sam zna regułę VIP (rząd 10+). Przekierowujemy tworzenie do metod fabryki `Tickets`, przenosimy tam regułę wyboru biletu, a na końcu chowamy klasy konkretne w fabryce jako typy prywatne.

**Zasada:** Encapsulate Classes with Factory ukrywa klasy konkretne za publiczną granicą tworzenia, gdy klient potrzebuje tylko wspólnego interfejsu. Tworzenie przekierowujemy pojedynczo, a widoczność ograniczamy dopiero na końcu. Nie mylić z Extract Factory Class, która wydziela tworzenie z klasy o innej odpowiedzialności.

**Efekt:** Publiczne zostają tylko `ITicket` i `Tickets`, więc nowy rodzaj biletu nie zmienia żadnego klienta. Ryzykiem zostają miejsca tworzenia, których kompilator nie widzi (refleksja, DI, deserializacja), a w bibliotece publicznej potrzebny byłby etap `[Obsolete]`.

**Różnica względem Javy:** podpakiet `tickets` to namespace `Ticketing` (namespace `...Tickets` kolidowałby z klasą fabryki `Tickets`). W kroku 3 Java odbiera klasom `public` i zostają package-private. W C# `internal` nie ukryłby ich przed `BoxOffice` (to samo assembly), więc klasy konkretne stają się prywatnymi rekordami zagnieżdżonymi w `public static partial class Tickets` - każdy nadal we własnym pliku (`StandardTicket.cs`, `VipTicket.cs`). `S04SolutionTest` sprawdza refleksją `IsNestedPrivate` dla biletów i `IsPublic` dla `Tickets` i `ITicket`.

### Co widzimy

`BoxOffice` (inny namespace niż bilety) tworzy `StandardTicket` i `VipTicket` przez `new` w dwóch miejscach i zna regułę VIP (rząd 10+). Klasy biletów są publiczne, choć klient potrzebuje tylko interfejsu `ITicket`.

```csharp
tickets.Add(row >= 10
    ? new VipTicket(title, basePrice, row)
    : new StandardTicket(title, basePrice, row));
```

### Krok 1: metody tworzące

**Zmiany:**

1. W namespace `Ticketing` utwórz nowy plik `Tickets.cs`: statyczna klasa `Tickets` z metodami tworzącymi `Standard(title, basePrice, row)` i `Vip(title, basePrice, row)`, obie zwracają `ITicket` i wołają `new` na klasie konkretnej.
2. Znajdź wszystkie użycia konstruktorów `StandardTicket` i `VipTicket` - to dwa miejsca w `BoxOffice` (`Sell` i pętla w `SellAll`) - i zastąp każde `new` wywołaniem metody tworzącej.

**Przed (`BoxOffice.cs`):**

```csharp
public ITicket Sell(string title, Money basePrice, int row)
{
    if (row >= 10)
    {
        return new VipTicket(title, basePrice, row);
    }
    return new StandardTicket(title, basePrice, row);
}

public List<ITicket> SellAll(string title, Money basePrice, IReadOnlyList<int> rows)
{
    var tickets = new List<ITicket>();
    foreach (var row in rows)
    {
        tickets.Add(row >= 10
            ? new VipTicket(title, basePrice, row)
            : new StandardTicket(title, basePrice, row));
    }
    return tickets;
}
```

**Po (`Tickets.cs`, nowy plik w namespace `Ticketing`):**

```csharp
using Training.Workshop.Shared;

public static class Tickets
{
    public static ITicket Standard(string title, Money basePrice, int row)
    {
        return new StandardTicket(title, basePrice, row);
    }

    public static ITicket Vip(string title, Money basePrice, int row)
    {
        return new VipTicket(title, basePrice, row);
    }
}
```

**Po (`BoxOffice.cs`):**

```csharp
public ITicket Sell(string title, Money basePrice, int row)
{
    if (row >= 10)
    {
        return Tickets.Vip(title, basePrice, row);
    }
    return Tickets.Standard(title, basePrice, row);
}

public List<ITicket> SellAll(string title, Money basePrice, IReadOnlyList<int> rows)
{
    var tickets = new List<ITicket>();
    foreach (var row in rows)
    {
        tickets.Add(row >= 10 ? Tickets.Vip(title, basePrice, row) : Tickets.Standard(title, basePrice, row));
    }
    return tickets;
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s04` - zielone.
**Co powiedzieć:** przekierowujemy tworzenie pojedynczo; klient nie używa już klas konkretnych.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s04 0 1`

### Krok 2: decyzja do fabryki

**Zmiany:**

1. W `Tickets` dodaj metodę `ForSeat(title, basePrice, row)` i przenieś do niej warunek `row >= 10` z `BoxOffice` (przeniesienie metody). Próg wyciągnij do stałej `private const int VipFromRow = 10` (wprowadzenie stałej).
2. Usuń z fabryki metody `Standard` i `Vip` - `ForSeat` zostaje jedynym wejściem.
3. W `BoxOffice.Sell` zostaw delegację do `Tickets.ForSeat(...)`, a pętla w `SellAll` woła `Sell`.

**Przed:**

```csharp
public ITicket Sell(string title, Money basePrice, int row)
{
    if (row >= 10)
    {
        return Tickets.Vip(title, basePrice, row);
    }
    return Tickets.Standard(title, basePrice, row);
}
```

**Po (`Tickets.cs`, cały plik):**

```csharp
using Training.Workshop.Shared;

public static class Tickets
{
    private const int VipFromRow = 10;

    public static ITicket ForSeat(string title, Money basePrice, int row)
    {
        if (row >= VipFromRow)
        {
            return new VipTicket(title, basePrice, row);
        }
        return new StandardTicket(title, basePrice, row);
    }
}
```

**Po (`BoxOffice.cs`):**

```csharp
public ITicket Sell(string title, Money basePrice, int row)
{
    return Tickets.ForSeat(title, basePrice, row);
}

public List<ITicket> SellAll(string title, Money basePrice, IReadOnlyList<int> rows)
{
    var tickets = new List<ITicket>();
    foreach (var row in rows)
    {
        tickets.Add(Sell(title, basePrice, row));
    }
    return tickets;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** reguła "który bilet" jest teraz w jednym miejscu - to właściwy dom dla `if` o konstrukcji.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s04 1 2`

### Krok 3: ograniczenie widoczności

**Zmiany:**

1. Oznacz `Tickets` jako `public static partial class`.
2. W plikach `StandardTicket.cs` i `VipTicket.cs` owiń każdy bilet tą samą klasą częściową `Tickets` i zamień klasę na `private sealed record` pozycyjny (np. `VipTicket(string Title, Money Base, int Row)`); pola i konstruktor zastępują parametry rekordu.
3. Skompiluj: każde użycie biletów spoza fabryki podkreśli teraz kompilator - to inwentaryzacja klientów, których trzeba by zmigrować (tu nie ma już żadnego).

**Przed (`VipTicket.cs`; `StandardTicket.cs` analogicznie, z `Price()` zwracającym `_base` i opisem bez "VIP"):**

```csharp
public sealed class VipTicket : ITicket
{
    private readonly string _title;
    private readonly Money _base;
    private readonly int _row;

    public VipTicket(string title, Money basePrice, int row)
    {
        _title = title;
        _base = basePrice;
        _row = row;
    }

    public Money Price()
    {
        return _base.Plus(Money.Of("10.00"));
    }

    public string Describe()
    {
        return _title + " r" + _row + " VIP " + Price();
    }
}
```

**Po (`Tickets.cs`):** tylko nagłówek klasy:

```csharp
public static partial class Tickets
```

**Po (`VipTicket.cs`, cały plik):**

```csharp
using Training.Workshop.Shared;

public static partial class Tickets
{
    private sealed record VipTicket(string Title, Money Base, int Row) : ITicket
    {
        public Money Price()
        {
            return Base.Plus(Money.Of("10.00"));
        }

        public string Describe()
        {
            return Title + " r" + Row + " VIP " + Price();
        }
    }
}
```

**Po (`StandardTicket.cs`, cały plik):**

```csharp
using Training.Workshop.Shared;

public static partial class Tickets
{
    private sealed record StandardTicket(string Title, Money Base, int Row) : ITicket
    {
        public Money Price()
        {
            return Base;
        }

        public string Describe()
        {
            return Title + " r" + Row + " " + Price();
        }
    }
}
```

**Uruchom:** test zielony; `S04SolutionTest` sprawdza widoczność refleksją.
**Co powiedzieć:** widoczność ograniczamy na końcu, gdy wszyscy klienci już przeszli. W bibliotece publicznej potrzebny byłby etap `[Obsolete]`. W C# `internal` to granica assembly, nie namespace - dlatego ukrywamy typy w samej fabryce.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s04 2 3`

### Rozwiązanie i uzasadnienie

Publiczne są tylko `ITicket` i `Tickets`. Nowy rodzaj biletu (np. miejsce dla osoby na wózku) nie zmienia żadnego klienta.

### Pułapki

- Zapomniane miejsca tworzenia: refleksja, kontener DI, deserializacja JSON wymagająca publicznego konstruktora.
- Fabryka rosnąca w globalny rejestr wszystkiego ("ServiceLocator").
- Fabryka tworząca warianty z wyprzedzeniem - kosztowne efekty uruchamiane wcześniej niż w starej gałęzi.

### Pytanie do sali

Co się zmienia, jeśli klasy biletów są używane przez ORM (np. Entity Framework)?

## Scena s05. Extract Factory Class - tworzenie rezerwacji

**Temat ze slajdów:** 10.2. Wyodrębnienie klasy fabryki; 10.3. Fabryka jako strażnik spójności
**Namespace:** `Training.Workshop.M5.S05ExtractFactory` · **Test:** `scripts/warsztat.sh --lang cs test m5/s05` (`S05EquivalenceTest`, `S05SolutionTest`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** `ReservationService` pilnuje zajętości miejsc, ale też buduje rezerwacje (numer, opłata online, termin ważności), i to w dwóch kopiach. Wyciągamy tworzenie do metody, potem do klasy `ReservationFactory`, a na końcu przekazujemy fabrykę do serwisu przez konstruktor.

**Zasada:** Extract Factory Class wydziela wiedzę o tworzeniu obiektów z klasy, która ma inną główną odpowiedzialność. Fabryka to zwykła zależność wstrzykiwana konstruktorem, a nie globalny rejestr ani zestaw metod statycznych. Kolejność efektów przy tworzeniu (tu: numer pobierany przed walidacją kanału) jest częścią kontraktu.

**Efekt:** Fabryka wie, jak powstaje rezerwacja, serwis wie, kiedy wolno ją utworzyć, i każdą da się testować osobno. "Spalanie" numeru przy błędnym kanale zostaje celowo - jego naprawa byłaby zmianą zachowania.

**Różnica względem Javy:** `Clock` to w porcie `TimeProvider`, czas lokalny `_clock.GetLocalNow().DateTime` (w testach `FakeTimeProvider` w strefie UTC). W oczekiwanym wyniku scenariusza nazwy wyjątków są z .NET: `InvalidOperationException: seat taken: A1` i `ArgumentException: unknown channel: ...` zamiast `IllegalStateException`/`IllegalArgumentException`.

### Co widzimy

`ReservationService` pilnuje zajętości miejsc, ale też buduje rezerwację: numer z licznika, opłata online 2.00 za bilet, termin ważności 15 minut. Ta wiedza jest skopiowana w `Reserve` i `ReserveGroup`. Subtelność: numer jest pobierany **przed** walidacją kanału - błędny kanał "spala" numer.

```csharp
_sequence++;
var id = "R" + _sequence;
if (channel != "ONLINE" && channel != "BOX_OFFICE")
{
    throw new ArgumentException("unknown channel: " + channel);
}
```

Test to scenariusz kilku rezerwacji na jednym serwisie: zajęte miejsce nie zużywa numeru, nieznany kanał zużywa.

### Krok 1: Extract Method dla tworzenia

**Zmiany:**

1. W `Reserve` wydziel blok od `_sequence++` do `new Reservation(...)` do prywatnej metody `NewReservation(channel, email, seats)` (wyodrębnienie metody). Linia `_takenSeats.UnionWith(seats)` zostaje w `Reserve`, po wywołaniu.
2. W `ReserveGroup` zastąp skopiowany blok wywołaniem `NewReservation("ONLINE", email, seats)`.
3. Pętlę sprawdzającą zajętość miejsc wydziel do prywatnej metody `RequireFree(seats)` i wywołaj ją w obu metodach publicznych.

**Przed (`ReservationService.cs`):**

```csharp
public Reservation Reserve(string channel, string email, IReadOnlyList<string> seats)
{
    foreach (var seat in seats)
    {
        if (_takenSeats.Contains(seat))
        {
            throw new InvalidOperationException("seat taken: " + seat);
        }
    }
    _sequence++;
    var id = "R" + _sequence;
    if (channel != "ONLINE" && channel != "BOX_OFFICE")
    {
        throw new ArgumentException("unknown channel: " + channel);
    }
    var fee = channel == "ONLINE" ? Money.Of("2.00").Times(seats.Count) : Money.Zero;
    DateTime? expiresAt = channel == "ONLINE"
        ? _clock.GetLocalNow().DateTime.AddMinutes(15)
        : null;
    _takenSeats.UnionWith(seats);
    return new Reservation(id, channel, email, seats.ToList(), fee, expiresAt);
}

public Reservation ReserveGroup(string email, IReadOnlyList<string> seats)
{
    if (seats.Count < 10)
    {
        throw new ArgumentException("group needs 10+ seats");
    }
    foreach (var seat in seats)
    {
        if (_takenSeats.Contains(seat))
        {
            throw new InvalidOperationException("seat taken: " + seat);
        }
    }
    _sequence++;
    var id = "R" + _sequence;
    var fee = Money.Of("2.00").Times(seats.Count);
    var expiresAt = _clock.GetLocalNow().DateTime.AddMinutes(15);
    _takenSeats.UnionWith(seats);
    return new Reservation(id, "ONLINE", email, seats.ToList(), fee, expiresAt);
}
```

**Po (`ReservationService.cs`):** pola i konstruktor bez zmian, reszta klasy wygląda tak:

```csharp
public Reservation Reserve(string channel, string email, IReadOnlyList<string> seats)
{
    RequireFree(seats);
    var reservation = NewReservation(channel, email, seats);
    _takenSeats.UnionWith(seats);
    return reservation;
}

public Reservation ReserveGroup(string email, IReadOnlyList<string> seats)
{
    if (seats.Count < 10)
    {
        throw new ArgumentException("group needs 10+ seats");
    }
    RequireFree(seats);
    var reservation = NewReservation("ONLINE", email, seats);
    _takenSeats.UnionWith(seats);
    return reservation;
}

private void RequireFree(IReadOnlyList<string> seats)
{
    foreach (var seat in seats)
    {
        if (_takenSeats.Contains(seat))
        {
            throw new InvalidOperationException("seat taken: " + seat);
        }
    }
}

private Reservation NewReservation(string channel, string email, IReadOnlyList<string> seats)
{
    _sequence++;
    var id = "R" + _sequence;
    if (channel != "ONLINE" && channel != "BOX_OFFICE")
    {
        throw new ArgumentException("unknown channel: " + channel);
    }
    var fee = channel == "ONLINE" ? Money.Of("2.00").Times(seats.Count) : Money.Zero;
    DateTime? expiresAt = channel == "ONLINE"
        ? _clock.GetLocalNow().DateTime.AddMinutes(15)
        : null;
    return new Reservation(id, channel, email, seats.ToList(), fee, expiresAt);
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s05` - zielone.
**Co powiedzieć:** kolejność efektów zostaje: najpierw sprawdzenie miejsc, potem numer, potem walidacja kanału. Gdybyśmy przy okazji "poprawili" kolejność, test to wychwyci.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s05 0 1`

### Krok 2: Extract Class - ReservationFactory

**Zmiany:**

1. Utwórz nowy plik `ReservationFactory.cs` i przenieś do niego `NewReservation` razem z polami `_clock` i `_sequence` (wyodrębnienie klasy). W fabryce metoda nazywa się `Create`, a konstruktor przyjmuje `TimeProvider`.
2. W serwisie zamień pola `_clock` i `_sequence` na pole `_factory`; konstruktor `ReservationService(TimeProvider clock)` tworzy `new ReservationFactory(clock)` - jego sygnatura się nie zmienia. Usuń `NewReservation` oraz zbędny już `using Training.Workshop.Shared;`.
3. `Reserve` i `ReserveGroup` wołają teraz `_factory.Create(...)` zamiast `NewReservation(...)` (reszta ich ciał bez zmian).

**Przed (`ReservationService.cs`):**

```csharp
private readonly TimeProvider _clock;
private readonly HashSet<string> _takenSeats = [];
private int _sequence;

public ReservationService(TimeProvider clock)
{
    _clock = clock;
}
```

(oraz metoda `NewReservation` z kroku 1, przenoszona teraz w całości do fabryki)

**Po (`ReservationFactory.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public sealed class ReservationFactory
{
    private readonly TimeProvider _clock;
    private int _sequence;

    public ReservationFactory(TimeProvider clock)
    {
        _clock = clock;
    }

    public Reservation Create(string channel, string email, IReadOnlyList<string> seats)
    {
        _sequence++;
        var id = "R" + _sequence;
        if (channel != "ONLINE" && channel != "BOX_OFFICE")
        {
            throw new ArgumentException("unknown channel: " + channel);
        }
        var fee = channel == "ONLINE" ? Money.Of("2.00").Times(seats.Count) : Money.Zero;
        DateTime? expiresAt = channel == "ONLINE"
            ? _clock.GetLocalNow().DateTime.AddMinutes(15)
            : null;
        return new Reservation(id, channel, email, seats.ToList(), fee, expiresAt);
    }
}
```

**Po (`ReservationService.cs`):**

```csharp
private readonly ReservationFactory _factory;
private readonly HashSet<string> _takenSeats = [];

public ReservationService(TimeProvider clock)
{
    _factory = new ReservationFactory(clock);
}
```

oraz w obu metodach publicznych:

```csharp
var reservation = _factory.Create(channel, email, seats);          // Reserve
var reservation = _factory.Create("ONLINE", email, seats);         // ReserveGroup
```

**Uruchom:** test zielony.
**Co powiedzieć:** klienci serwisu niczego nie zauważyli. Serwis ma jedną odpowiedzialność: dostępność miejsc.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s05 1 2`

### Krok 3: fabryka jako zależność

**Zmiany:**

1. Dodaj konstruktor `ReservationService(ReservationFactory factory)` z `ArgumentNullException.ThrowIfNull(factory)`; to on ustawia pole `_factory`.
2. Dotychczasowy konstruktor `ReservationService(TimeProvider clock)` zamień na delegację `: this(new ReservationFactory(clock))` - klienci nie muszą się zmieniać.

**Przed:**

```csharp
public ReservationService(TimeProvider clock)
{
    _factory = new ReservationFactory(clock);
}
```

**Po (`ReservationService.cs`):**

```csharp
/// <summary>Dotychczasowy konstruktor zostaje jako wygodny skrót - klienci nie muszą się zmieniać.</summary>
public ReservationService(TimeProvider clock)
    : this(new ReservationFactory(clock))
{
}

public ReservationService(ReservationFactory factory)
{
    ArgumentNullException.ThrowIfNull(factory);
    _factory = factory;
}
```

**Uruchom:** test zielony; `S05SolutionTest` testuje fabrykę osobno i pokazuje współdzieloną numerację.
**Co powiedzieć:** fabryka to zwykła zależność wstrzykiwana konstruktorem, nie globalny rejestr. Dwie usługi z jedną fabryką dzielą numerację - to decyzja korzenia kompozycji.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s05 2 3`

### Rozwiązanie i uzasadnienie

`ReservationFactory` wie, jak powstaje rezerwacja (numer, opłata, termin), `ReservationService` wie, kiedy wolno ją utworzyć. Każdą da się testować osobno.

### Pułapki

- "Naprawienie" spalania numerów w ramach refaktoryzacji - to zmiana zachowania (luki w numeracji mogą być wymagane przez księgowość albo przez nią zabronione).
- Zegar pobierany przez `DateTime.Now` bez `TimeProvider` - fabryka staje się nietestowalna.
- Fabryka ze statycznymi metodami i statycznym licznikiem - wraca globalny stan.

### Pytanie do sali

Czy numeracja rezerwacji powinna należeć do fabryki, czy do repozytorium? Co przemawia za każdą opcją?

## Scena s06. Encapsulate Composite with Builder - repertuar dnia

**Temat ze slajdów:** 12.1. Ukrycie budowy Kompozytu za Budowniczym; 12.2. Budowniczy: kiedy i na co uważać
**Namespace:** `Training.Workshop.M5.S06Builder` · **Test:** `scripts/warsztat.sh --lang cs test m5/s06` (`S06EquivalenceTest`, `S06SolutionTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `WeekendPlanner` składa drzewo dzień - sala - seans ręcznie z `new` i `Add`, a węzły są mutowalne. Wprowadzamy `ScheduleBuilder`: najpierw z bieżącą salą, potem z niemutowalnymi rekordami i jednorazowym `Build()`, na końcu z zagnieżdżoną lambdą dla każdej sali.

**Zasada:** Encapsulate Composite with Builder ukrywa budowę drzewa za API, które mówi "co" zbudować, a nie "jak". Jest uzasadniony, gdy budowa jest wieloetapowa i ma własne niezmienniki (kolejność, pusta grupa, unikalność nazw). Builder jest jednorazowy, a lambda gałęzi wykonuje się synchronicznie dokładnie raz.

**Efekt:** Wcięcia kodu planera odpowiadają poziomom repertuaru, a gotowego drzewa nie da się zmodyfikować. Unikalność nazw sal to świadomie dodany nowy niezmiennik - Start pozwalał na duplikaty, więc dla błędnych danych zachowanie się zmienia.

**Różnica względem Javy:** `LocalDate`/`LocalTime` to `DateOnly`/`TimeOnly`, a dzień tygodnia w wydruku to nazwa enuma .NET (`2026-10-03 Saturday` zamiast `SATURDAY`). W kroku 2 `LinkedHashMap` to `OrderedDictionary<string, List<Screening>>` (.NET 9+), a niemutowalność rekordów daje kopia `ToList().AsReadOnly()` w inicjatorze właściwości (`S06SolutionTest.BuiltTreeIsImmutable` rzutuje listy na `ICollection<T>` i oczekuje `NotSupportedException` przy `Clear()`). W kroku 3 `Consumer<HallBuilder>` to `Action<HallBuilder>`, a konstruktor `HallBuilder` jest `internal`, bo klasa zewnętrzna C# nie widzi prywatnych składowych klasy zagnieżdżonej.

### Co widzimy

`WeekendPlanner.Plan(date)` buduje drzewo dzień - sala - seans ręcznie: dużo `new` i `Add`, a o `day.Add(hall2)` łatwo zapomnieć. Kod nie przypomina kształtu repertuaru, a węzły są mutowalne.

```csharp
var hall2 = new Hall("Sala 2");
if (weekend)
{
    hall2.Add(new Screening("Kraina Lodu", new TimeOnly(10, 0)));
}
hall2.Add(new Screening("Amator", new TimeOnly(17, 30)));
day.Add(hall2);
```

### Krok 1: klasyczny Builder z bieżącym węzłem

**Zmiany:**

1. Utwórz nowy plik `ScheduleBuilder.cs`: konstruktor `ScheduleBuilder(DateOnly date)` tworzy `DaySchedule`; metoda `Hall(name)` tworzy salę, dodaje ją do dnia, zapamiętuje jako bieżącą i zwraca `this`; `Screening(title, hour, minute)` dodaje seans do bieżącej sali, a bez sali rzuca `InvalidOperationException("screening without hall")`; `Build()` zwraca dzień.
2. Przepisz `WeekendPlanner.Plan` na łańcuch wywołań buildera; seanse weekendowe dodawaj warunkowo przez `builder.Screening(...)`. Węzły drzewa zostają bez zmian.

**Przed (`WeekendPlanner.cs`):**

```csharp
public DaySchedule Plan(DateOnly date)
{
    var dayOfWeek = date.DayOfWeek;
    var weekend = dayOfWeek == DayOfWeek.Saturday || dayOfWeek == DayOfWeek.Sunday;
    var day = new DaySchedule(date);
    var hall1 = new Hall("Sala 1");
    hall1.Add(new Screening("Diuna", new TimeOnly(18, 0)));
    hall1.Add(new Screening("Diuna", new TimeOnly(21, 0)));
    day.Add(hall1);
    var hall2 = new Hall("Sala 2");
    if (weekend)
    {
        hall2.Add(new Screening("Kraina Lodu", new TimeOnly(10, 0)));
    }
    hall2.Add(new Screening("Amator", new TimeOnly(17, 30)));
    day.Add(hall2);
    var hall3 = new Hall("Sala 3 VIP");
    if (weekend)
    {
        hall3.Add(new Screening("Amator", new TimeOnly(20, 0)));
    }
    day.Add(hall3);
    return day;
}
```

**Po (`ScheduleBuilder.cs`, nowy plik):**

```csharp
public sealed class ScheduleBuilder
{
    private readonly DaySchedule _day;
    private Hall? _current;

    public ScheduleBuilder(DateOnly date)
    {
        _day = new DaySchedule(date);
    }

    public ScheduleBuilder Hall(string name)
    {
        _current = new Hall(name);
        _day.Add(_current);
        return this;
    }

    public ScheduleBuilder Screening(string title, int hour, int minute)
    {
        if (_current == null)
        {
            throw new InvalidOperationException("screening without hall");
        }
        _current.Add(new Screening(title, new TimeOnly(hour, minute)));
        return this;
    }

    public DaySchedule Build()
    {
        return _day;
    }
}
```

**Po (`WeekendPlanner.cs`):**

```csharp
public DaySchedule Plan(DateOnly date)
{
    var dayOfWeek = date.DayOfWeek;
    var weekend = dayOfWeek == DayOfWeek.Saturday || dayOfWeek == DayOfWeek.Sunday;
    var builder = new ScheduleBuilder(date)
        .Hall("Sala 1")
        .Screening("Diuna", 18, 0)
        .Screening("Diuna", 21, 0)
        .Hall("Sala 2");
    if (weekend)
    {
        builder.Screening("Kraina Lodu", 10, 0);
    }
    builder.Screening("Amator", 17, 30)
        .Hall("Sala 3 VIP");
    if (weekend)
    {
        builder.Screening("Amator", 20, 0);
    }
    return builder.Build();
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s06` - zielone (sobota i piątek z pustą salą VIP).
**Co powiedzieć:** klient mówi "co" zbudować. Seans przed salą to teraz jawny błąd (`screening without hall`).
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s06 0 1`

### Krok 2: niemutowalne drzewo, builder jednorazowy

**Zmiany:**

1. Zamień węzły `DaySchedule` i `Hall` na rekordy z kopią listy w inicjatorze właściwości (`Screenings.ToList().AsReadOnly()`); metody `Add` i `Size` znikają, a `Render` czyta `Halls`/`Screenings`. `Screening` też staje się rekordem.
2. Builder zbiera dane w `OrderedDictionary<string, List<Screening>>` z polem `_current` wskazującym listę bieżącej sali; rekordy drzewa powstają dopiero w `Build()`.
3. Dodaj niezmienniki: duplikat nazwy sali rzuca `InvalidOperationException("duplicate hall: ...")`, a pole `_built` i metoda `RequireNotBuilt()` sprawiają, że każde wywołanie po `Build()` (w tym drugie `Build()`) rzuca `InvalidOperationException("builder already used")`.

**Przed:** mutowalne klasy węzłów z `Add` (jak w Start) oraz builder z kroku 1.

**Po (`DaySchedule.cs`, cały plik):**

```csharp
using System.Globalization;
using System.Text;

public sealed record DaySchedule(DateOnly Date, IReadOnlyList<Hall> Halls)
{
    public IReadOnlyList<Hall> Halls { get; } = Halls.ToList().AsReadOnly();

    public string Render()
    {
        var text = new StringBuilder()
            .Append(Date.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture)).Append(' ').Append(Date.DayOfWeek).Append('\n');
        var count = 0;
        foreach (var hall in Halls)
        {
            text.Append(hall.Render());
            count += hall.Screenings.Count;
        }
        return text.Append("Seansow: ").Append(count).Append('\n').ToString();
    }
}
```

**Po (`Hall.cs`, cały plik):**

```csharp
using System.Text;

public sealed record Hall(string Name, IReadOnlyList<Screening> Screenings)
{
    public IReadOnlyList<Screening> Screenings { get; } = Screenings.ToList().AsReadOnly();

    public string Render()
    {
        var text = new StringBuilder(Name).Append('\n');
        if (Screenings.Count == 0)
        {
            text.Append("  (brak seansow)\n");
        }
        foreach (var screening in Screenings)
        {
            text.Append(screening.Render());
        }
        return text.ToString();
    }
}
```

**Po (`Screening.cs`, cały plik):**

```csharp
using System.Globalization;

public sealed record Screening(string Title, TimeOnly Start)
{
    public string Render()
    {
        return "  " + Start.ToString("HH:mm", CultureInfo.InvariantCulture) + " " + Title + "\n";
    }
}
```

**Po (`ScheduleBuilder.cs`, cały plik):**

```csharp
public sealed class ScheduleBuilder
{
    private readonly DateOnly _date;
    private readonly OrderedDictionary<string, List<Screening>> _halls = [];
    private List<Screening>? _current;
    private bool _built;

    public ScheduleBuilder(DateOnly date)
    {
        _date = date;
    }

    public ScheduleBuilder Hall(string name)
    {
        RequireNotBuilt();
        if (_halls.ContainsKey(name))
        {
            throw new InvalidOperationException("duplicate hall: " + name);
        }
        _current = [];
        _halls.Add(name, _current);
        return this;
    }

    public ScheduleBuilder Screening(string title, int hour, int minute)
    {
        RequireNotBuilt();
        if (_current == null)
        {
            throw new InvalidOperationException("screening without hall");
        }
        _current.Add(new Screening(title, new TimeOnly(hour, minute)));
        return this;
    }

    public DaySchedule Build()
    {
        RequireNotBuilt();
        _built = true;
        var result = new List<Hall>();
        foreach (var (name, screenings) in _halls)
        {
            result.Add(new Hall(name, screenings));
        }
        return new DaySchedule(_date, result);
    }

    private void RequireNotBuilt()
    {
        if (_built)
        {
            throw new InvalidOperationException("builder already used");
        }
    }
}
```

**Uruchom:** test zielony; `S06SolutionTest` sprawdza jednorazowość i niemutowalność.
**Co powiedzieć:** gdy builder jest jedyną drogą budowy, drzewo może stać się niemutowalne. Unikalność nazw sal to **nowy niezmiennik** - Start pozwalał na duplikaty. Taką regułę zatwierdzamy świadomie, bo zmienia zachowanie dla błędnych danych.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s06 1 2`

### Krok 3: zagnieżdżony builder z lambdą

**Zmiany:**

1. Zamień `Hall(name)` na `Hall(string name, Action<HallBuilder> content)`: metoda tworzy `HallBuilder`, synchronicznie wywołuje lambdę i od razu dodaje gotową salę - pole "bieżąca sala" znika.
2. Dodaj zagnieżdżoną klasę `HallBuilder` z metodą `Screening(title, hour, minute)`; jej konstruktor jest `internal`.
3. Konstruktor `ScheduleBuilder` zmień na prywatny; wejściem jest statyczna metoda `ScheduleBuilder.Day(date)`.
4. Przepisz planera na zagnieżdżone lambdy (sale warunkowe dostają lambdę z blokiem `if`).

**Przed:**

```csharp
public ScheduleBuilder Hall(string name)
{
    RequireNotBuilt();
    if (_halls.ContainsKey(name))
    {
        throw new InvalidOperationException("duplicate hall: " + name);
    }
    _current = [];
    _halls.Add(name, _current);
    return this;
}
```

**Po (`ScheduleBuilder.cs`, cały plik):**

```csharp
public sealed class ScheduleBuilder
{
    private readonly DateOnly _date;
    private readonly List<Hall> _halls = [];
    private bool _built;

    private ScheduleBuilder(DateOnly date)
    {
        _date = date;
    }

    public static ScheduleBuilder Day(DateOnly date)
    {
        return new ScheduleBuilder(date);
    }

    public ScheduleBuilder Hall(string name, Action<HallBuilder> content)
    {
        RequireNotBuilt();
        if (_halls.Any(hall => hall.Name == name))
        {
            throw new InvalidOperationException("duplicate hall: " + name);
        }
        var hall = new HallBuilder();
        content(hall);
        _halls.Add(new Hall(name, hall.Screenings));
        return this;
    }

    public DaySchedule Build()
    {
        RequireNotBuilt();
        _built = true;
        return new DaySchedule(_date, _halls);
    }

    private void RequireNotBuilt()
    {
        if (_built)
        {
            throw new InvalidOperationException("builder already used");
        }
    }

    public sealed class HallBuilder
    {
        private readonly List<Screening> _screenings = [];

        internal HallBuilder()
        {
        }

        internal IReadOnlyList<Screening> Screenings => _screenings;

        public HallBuilder Screening(string title, int hour, int minute)
        {
            _screenings.Add(new Screening(title, new TimeOnly(hour, minute)));
            return this;
        }
    }
}
```

**Po (`WeekendPlanner.cs`):**

```csharp
public DaySchedule Plan(DateOnly date)
{
    var dayOfWeek = date.DayOfWeek;
    var weekend = dayOfWeek == DayOfWeek.Saturday || dayOfWeek == DayOfWeek.Sunday;
    return ScheduleBuilder.Day(date)
        .Hall("Sala 1", hall => hall
            .Screening("Diuna", 18, 0)
            .Screening("Diuna", 21, 0))
        .Hall("Sala 2", hall =>
        {
            if (weekend)
            {
                hall.Screening("Kraina Lodu", 10, 0);
            }
            hall.Screening("Amator", 17, 30);
        })
        .Hall("Sala 3 VIP", hall =>
        {
            if (weekend)
            {
                hall.Screening("Amator", 20, 0);
            }
        })
        .Build();
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** wcięcia kodu odpowiadają poziomom drzewa, a ukryty stan "bieżąca sala" zniknął. Kontrakt lambdy: synchroniczna, wywołana dokładnie raz.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s06 2 3`

### Rozwiązanie i uzasadnienie

Builder jest uzasadniony, bo budowa drzewa jest wieloetapowa i ma niezmienniki (kolejność sal, pusta sala dozwolona, unikalne nazwy). Wynik jest niemutowalny.

### Pułapki

- Builder wielokrotnego użytku, którego `Build()` zwraca wewnętrzną listę - kolejne wywołania zmieniają już zwrócone drzewo.
- Lambda zapamiętana i wywołana później (odroczenie) - zmienia kolejność i wynik.
- Współdzielone poddrzewo (DAG) - liczy się tyle razy, ile ścieżek do niego prowadzi.

### Pytanie do sali

Czy pusta sala w repertuarze to poprawny stan, czy błąd? Kto powinien o tym zdecydować - builder czy model?

## Scena s07. Move Embellishment to Decorator - dodatki do biletu

**Temat ze slajdów:** 8.1. Przeniesienie dodatków do Dekoratora; 8.2. Dekorator: na co uważać
**Namespace:** `Training.Workshop.M5.S07Decorator` · **Test:** `scripts/warsztat.sh --lang cs test m5/s07` (`S07EquivalenceTest`, `S07SolutionTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `Ticket` ma flagi `_vip`, `_glasses`, `_insurance` i te same `if` w `Price()` i `Description()`. Wydzielamy interfejs `IPricedTicket` i przenosimy każdy dodatek do dekoratora, zaczynając od ubezpieczenia, a łańcuch składamy w `TicketAssembler`.

**Zasada:** Move Embellishment to Decorator przenosi opcjonalny dodatek wokół rdzenia do obiektu, który ma ten sam kontrakt i deleguje do środka. Kolejność owijania jest zachowaniem, więc składa się ją w jednym miejscu. Dekorator nie jest przezroczysty dla `is`, `GetType()`, tożsamości i `Equals`.

**Efekt:** Rdzeń biletu nie ma flag, a nowy dodatek to nowa klasa i linia w assemblerze. Kosztem jest utrata pytania o typ: kto chce wiedzieć, czy bilet jest VIP, potrzebuje osobnego API zamiast `is VipSeat`.

**Różnica względem Javy:** merytorycznie brak. Interfejs to `IPricedTicket`, dekoratory i rdzeń z kroku 3 to `sealed record`, `instanceof` to `is` (nazwa testu `InstanceofSeesOnlyTheOutermostDecorator` zachowana z Javy).

### Co widzimy

`Ticket` ma flagi `_vip`, `_glasses`, `_insurance` i te same `if` w `Price()` i `Description()`. Większość biletów nie ma żadnego dodatku, a każdy nowy dodatek to kolejne pole. Opis ma ustaloną kolejność: VIP, okulary, ubezpieczenie.

```csharp
return _title + " " + _format
    + (_vip ? " +VIP" : "")
    + (_glasses ? " +okulary 3D" : "")
    + (_insurance ? " +ubezpieczenie" : "");
```

### Krok 1: Extract Interface

**Zmiany:**

1. Utwórz nowy plik `IPricedTicket.cs` i wydziel z `Ticket` interfejs `IPricedTicket` z metodami `Money Price()` i `string Description()` (wyodrębnienie interfejsu). `Ticket` implementuje interfejs, flagi na razie zostają.
2. Zmień typ zwracany `TicketAssembler.Assemble` z `Ticket` na `IPricedTicket`.

**Przed (`TicketAssembler.cs`):**

```csharp
public Ticket Assemble(TicketOrder order)
{
    return new Ticket(order);
}
```

**Po (`IPricedTicket.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public interface IPricedTicket
{
    Money Price();

    string Description();
}
```

**Po (`Ticket.cs`):** tylko nagłówek klasy:

```csharp
public sealed class Ticket : IPricedTicket
```

**Po (`TicketAssembler.cs`):**

```csharp
public IPricedTicket Assemble(TicketOrder order)
{
    return new Ticket(order);
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s07` - zielone.
**Co powiedzieć:** wąski kontrakt, który spełnią i rdzeń, i dekoratory. Składanie w jednym miejscu (assembler).
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s07 0 1`

### Krok 2: pierwszy dodatek jako dekorator

**Zmiany:**

1. Utwórz nowy plik `Insurance.cs`: `sealed record Insurance(IPricedTicket Inner) : IPricedTicket`; `Price()` zwraca `Inner.Price().Plus(Money.Of("4.00"))`, a `Description()` dokleja `" +ubezpieczenie"` do `Inner.Description()`.
2. Usuń z `Ticket` pole `_insurance`, jego przypisanie w konstruktorze i obie gałęzie `if (_insurance)` / człon opisu.
3. W `TicketAssembler.Assemble` owiń rdzeń dekoratorem, gdy `order.Insurance`.

**Przed (`Ticket.cs`):**

```csharp
private readonly bool _insurance;
...
_insurance = order.Insurance;      // w konstruktorze
...
if (_insurance)
{
    price = price.Plus(Money.Of("4.00"));
}
...
return _title + " " + _format
    + (_vip ? " +VIP" : "")
    + (_glasses ? " +okulary 3D" : "")
    + (_insurance ? " +ubezpieczenie" : "");
```

**Po (`Insurance.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public sealed record Insurance(IPricedTicket Inner) : IPricedTicket
{
    public Money Price()
    {
        return Inner.Price().Plus(Money.Of("4.00"));
    }

    public string Description()
    {
        return Inner.Description() + " +ubezpieczenie";
    }
}
```

**Po (`Ticket.cs`):** bez pola `_insurance` i jego przypisania w konstruktorze; metody wyglądają teraz tak:

```csharp
public Money Price()
{
    var price = _base;
    if (_vip)
    {
        price = price.Plus(Money.Of("10.00"));
    }
    if (_glasses)
    {
        price = price.Plus(Money.Of("3.00"));
    }
    return price;
}

public string Description()
{
    return _title + " " + _format
        + (_vip ? " +VIP" : "")
        + (_glasses ? " +okulary 3D" : "");
}
```

**Po (`TicketAssembler.cs`):**

```csharp
public IPricedTicket Assemble(TicketOrder order)
{
    IPricedTicket ticket = new Ticket(order);
    if (order.Insurance)
    {
        ticket = new Insurance(ticket);
    }
    return ticket;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** zaczynamy od ubezpieczenia, bo w opisie jest **ostatnie** - dekorator dopisuje się na końcu. Gdybyśmy zaczęli od VIP, opis zmieniłby kolejność i test "wszystkie dodatki" by to złapał. Kolejność dekoratorów jest kontraktem.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s07 1 2`

### Krok 3: pozostałe dodatki, czysty rdzeń

**Zmiany:**

1. Utwórz nowe pliki `VipSeat.cs` i `Glasses3D.cs`: dekoratory na wzór `Insurance` - VIP dodaje 10.00 i `" +VIP"`, okulary 3.00 i `" +okulary 3D"`.
2. Rdzeń zamień na `sealed record Ticket(string Title, string Format, Money Base) : IPricedTicket`: `Price()` zwraca `Base`, `Description()` to `Title + " " + Format`. Flagi `_vip` i `_glasses` znikają.
3. W `TicketAssembler.Assemble` złóż łańcuch w kolejności: rdzeń, VIP, okulary, ubezpieczenie. Decyzja "czy okulary potrzebne" (`order.Format == "3D" && !order.OwnGlasses`) zostaje w assemblerze.

**Przed:**

```csharp
public sealed class Ticket : IPricedTicket
{
    private readonly string _title;
    private readonly string _format;
    private readonly Money _base;
    private readonly bool _vip;
    private readonly bool _glasses;
    ...
    public string Description()
    {
        return _title + " " + _format
            + (_vip ? " +VIP" : "")
            + (_glasses ? " +okulary 3D" : "");
    }
}
```

**Po (`VipSeat.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public sealed record VipSeat(IPricedTicket Inner) : IPricedTicket
{
    public Money Price()
    {
        return Inner.Price().Plus(Money.Of("10.00"));
    }

    public string Description()
    {
        return Inner.Description() + " +VIP";
    }
}
```

**Po (`Glasses3D.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public sealed record Glasses3D(IPricedTicket Inner) : IPricedTicket
{
    public Money Price()
    {
        return Inner.Price().Plus(Money.Of("3.00"));
    }

    public string Description()
    {
        return Inner.Description() + " +okulary 3D";
    }
}
```

**Po (`Ticket.cs`, cały plik):**

```csharp
using Training.Workshop.Shared;

public sealed record Ticket(string Title, string Format, Money Base) : IPricedTicket
{
    public Money Price()
    {
        return Base;
    }

    public string Description()
    {
        return Title + " " + Format;
    }
}
```

**Po (`TicketAssembler.cs`):**

```csharp
public IPricedTicket Assemble(TicketOrder order)
{
    IPricedTicket ticket = new Ticket(order.Title, order.Format, order.Base);
    if (order.Vip)
    {
        ticket = new VipSeat(ticket);
    }
    if (order.Format == "3D" && !order.OwnGlasses)
    {
        ticket = new Glasses3D(ticket);
    }
    if (order.Insurance)
    {
        ticket = new Insurance(ticket);
    }
    return ticket;
}
```

**Uruchom:** test zielony; `S07SolutionTest` dokumentuje granice przezroczystości.
**Co powiedzieć:** `ticket is VipSeat` zwraca `false`, gdy VIP jest owinięty ubezpieczeniem. `Equals` rdzenia i udekorowanego biletu też się różni (rekordy porównują typ i pola). Kto pyta o typ albo tożsamość, potrzebuje innego API (np. `Features`).
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s07 2 3`

### Rozwiązanie i uzasadnienie

Rdzeń bez flag, trzy dekoratory z tym samym kontraktem, łańcuch składany w jednej fabryce w ustalonej kolejności.

### Pułapki

- Ślepe delegowanie `Equals` do `Inner` - łamie symetrię (`core.Equals(vip)` różne od `vip.Equals(core)`).
- Kod sprawdzający `GetType()`, `is`, robiący `lock` na obiekcie albo serializujący go - dekorator nie jest przezroczysty.
- Dekorator z efektem ubocznym (audyt), którego błąd zasłania błąd rdzenia.

### Pytanie do sali

Kasa chce wydrukować "Miejsce VIP" na bilecie. Jak to zrobić, nie używając `is VipSeat`?

## Scena s08. Replace State-Altering Conditionals with State - status rezerwacji

**Temat ze slajdów:** 6.4. Stan: sygnał i tabela przejść; 6.5. Stan: wynik
**Namespace:** `Training.Workshop.M5.S08State` · **Test:** `scripts/warsztat.sh --lang cs test m5/s08` (`S08EquivalenceTest`)
**Czas:** ~15 min

### W skrócie

**Co robimy:** `Reservation` w każdej operacji sprawdza i zmienia pole `_status`, więc reguły przejść są rozsiane po metodach. Zaczynamy od tabeli przejść w teście, zastępujemy pole obiektem stanu i przenosimy `Pay`, `Use`, `Expire`, `Cancel` do stanów po jednej akcji.

**Zasada:** State pasuje, gdy zachowanie operacji zależy od bieżącego stanu obiektu i zmienia się razem z przejściami. Kontekst deleguje do obiektu stanu, a niedozwolone przejście domyślnie rzuca wyjątek - pusta metoda po cichu zmieniłaby zachowanie. Od Strategy różni się tym, że stan zmienia się w czasie życia obiektu, zwykle na skutek jego własnych operacji.

**Efekt:** Tabelę przejść da się przeczytać z kodu stanów, a `Reservation` tylko deleguje i przechowuje dane. Kolejność "obciążenie - zmiana stanu - efekt" zostaje jak w Start, więc awaria bramki nadal zostawia rezerwację w stanie New.

**Różnica względem Javy:** Java używa prywatnego `sealed interface ReservationState` i stanów jako enumów (`NewState.INSTANCE`, `ClosedState.USED/EXPIRED/CANCELLED`). C# nie ma `sealed interface`, a enum nie ma metod, więc stan to prywatny interfejs zagnieżdżony `IReservationState`, a stany to prywatne klasy `sealed` z instancjami `static readonly` (`NewState.Instance`, `PaidState.Instance`, jedna klasa `ClosedState(Status)` z trzema instancjami `Used`, `Expired`, `Cancelled`). Domyślne metody Javy to domyślne implementacje metod interfejsu (C# 8+) - są widoczne tylko przez typ interfejsu, dlatego pole `_state` ma typ `IReservationState`. Statusy są w PascalCase, więc komunikaty brzmią np. `cannot pay in New`.

### Co widzimy

`Reservation` ma pole `_status` i w każdej operacji (`Pay`, `Use`, `Expire`, `Cancel`) warunki sprawdzające i zmieniające status. Reguły przejść New -> Paid -> Used, New -> Expired, New/Paid -> Cancelled są rozsiane po metodach. `Pay` najpierw obciąża bramkę, potem zmienia stan: gdy bramka rzuci wyjątek, rezerwacja zostaje New.

```csharp
public void Cancel()
{
    if (_status == Status.New)
    {
        _status = Status.Cancelled;
        _effects.Add("seats released");
    }
    else if (_status == Status.Paid)
    {
        _status = Status.Cancelled;
        _effects.Add("refund");
        _effects.Add("seats released");
    }
    else
    {
        throw new InvalidOperationException("cannot cancel in " + _status);
    }
}
```

**Najpierw tabela przejść.** `S08EquivalenceTest` zawiera tabelę 5 stanów x 4 akcje (20 wierszy) plus przypadek awarii bramki. Każdy wiersz sprawdza status końcowy, efekty w kolejności i komunikat wyjątku. Pokaż ją na rzutniku przed pierwszym ruchem.

### Krok 1: obiekt stanu zamiast pola status

**Zmiany:**

1. Wewnątrz `Reservation` utwórz prywatny interfejs zagnieżdżony `IReservationState` z właściwością `Status` oraz stany jako prywatne klasy `sealed` z instancjami `static readonly`: `NewState.Instance`, `PaidState.Instance` i jedna klasa `ClosedState(Status)` z instancjami `Used`, `Expired`, `Cancelled`.
2. Pole `private Status _status` zastąp polem `private IReservationState _state = NewState.Instance`; warunki w metodach porównują teraz `_state.Status`, a właściwość `Status` zwraca `_state.Status`.
3. Wydziel prywatne metody pomocnicze (wyodrębnienie metody): `Charge()` (obciążenie bramki), `MoveTo(next)` (przypisanie `_state`) i `Record(effect)` (dopisanie efektu); metody publiczne wołają je zamiast przypisań.

**Przed:**

```csharp
private Status _status = Status.New;
...
public void Pay()
{
    if (_status != Status.New)
    {
        throw new InvalidOperationException("cannot pay in " + _status);
    }
    _payments.Charge(_id);
    _status = Status.Paid;
    _effects.Add("charged");
}
```

**Po (`Reservation.cs`, cały plik; `Use`, `Expire` i `Cancel` zmieniają się tak samo jak `Pay`):**

```csharp
public sealed class Reservation : IReservationActions
{
    private readonly string _id;
    private readonly IPayments _payments;
    private readonly List<string> _effects = [];
    private IReservationState _state = NewState.Instance;

    public Reservation(string id, IPayments payments)
    {
        _id = id;
        _payments = payments;
    }

    public void Pay()
    {
        if (_state.Status != Status.New)
        {
            throw new InvalidOperationException("cannot pay in " + _state.Status);
        }
        Charge();
        MoveTo(PaidState.Instance);
        Record("charged");
    }

    public void Use()
    {
        if (_state.Status != Status.Paid)
        {
            throw new InvalidOperationException("cannot use in " + _state.Status);
        }
        MoveTo(ClosedState.Used);
        Record("gate opened");
    }

    public void Expire()
    {
        if (_state.Status != Status.New)
        {
            throw new InvalidOperationException("cannot expire in " + _state.Status);
        }
        MoveTo(ClosedState.Expired);
        Record("seats released");
    }

    public void Cancel()
    {
        if (_state.Status == Status.New)
        {
            MoveTo(ClosedState.Cancelled);
            Record("seats released");
        }
        else if (_state.Status == Status.Paid)
        {
            MoveTo(ClosedState.Cancelled);
            Record("refund");
            Record("seats released");
        }
        else
        {
            throw new InvalidOperationException("cannot cancel in " + _state.Status);
        }
    }

    public Status Status => _state.Status;

    public IReadOnlyList<string> Effects => _effects.ToList();

    private void Charge()
    {
        _payments.Charge(_id);
    }

    private void MoveTo(IReservationState next)
    {
        _state = next;
    }

    private void Record(string effect)
    {
        _effects.Add(effect);
    }

    private interface IReservationState
    {
        Status Status { get; }
    }

    private sealed class NewState : IReservationState
    {
        public static readonly NewState Instance = new();

        public Status Status => Status.New;
    }

    private sealed class PaidState : IReservationState
    {
        public static readonly PaidState Instance = new();

        public Status Status => Status.Paid;
    }

    /// <summary>Stany końcowe - żadne przejście nie jest dozwolone.</summary>
    private sealed class ClosedState(Status status) : IReservationState
    {
        public static readonly ClosedState Used = new(Status.Used);
        public static readonly ClosedState Expired = new(Status.Expired);
        public static readonly ClosedState Cancelled = new(Status.Cancelled);

        public Status Status => status;
    }
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s08` - 84 testy zielone.
**Co powiedzieć:** stany końcowe to jedna klasa `ClosedState` z trzema instancjami - wszystkie zachowują się tak samo (każde przejście jest błędem).
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s08 0 1`

### Krok 2: Pay i Use do stanów

**Zmiany:**

1. W interfejsie stanu dodaj metody z domyślnymi implementacjami: `void Pay(Reservation reservation) => throw Invalid("pay");` i analogicznie `Use`, oraz prywatną metodę interfejsu `Invalid(action)` budującą `InvalidOperationException("cannot " + action + " in " + Status)`.
2. W `NewState` zaimplementuj `Pay` (ciało gałęzi: `reservation.Charge()`, `reservation.MoveTo(PaidState.Instance)`, `reservation.Record("charged")`), a w `PaidState` - `Use` (przejście do `ClosedState.Used` i efekt "gate opened").
3. W kontekście metody `Pay` i `Use` zamień na delegacje `_state.Pay(this)` i `_state.Use(this)`; ich warunki znikają.

**Przed (`Reservation.cs`):**

```csharp
public void Pay()
{
    if (_state.Status != Status.New)
    {
        throw new InvalidOperationException("cannot pay in " + _state.Status);
    }
    Charge();
    MoveTo(PaidState.Instance);
    Record("charged");
}

public void Use()
{
    if (_state.Status != Status.Paid)
    {
        throw new InvalidOperationException("cannot use in " + _state.Status);
    }
    MoveTo(ClosedState.Used);
    Record("gate opened");
}
```

**Po (`Reservation.cs`, zmienione fragmenty):**

```csharp
public void Pay()
{
    _state.Pay(this);
}

public void Use()
{
    _state.Use(this);
}

private interface IReservationState
{
    Status Status { get; }

    void Pay(Reservation reservation) => throw Invalid("pay");

    void Use(Reservation reservation) => throw Invalid("use");

    private InvalidOperationException Invalid(string action) =>
        new("cannot " + action + " in " + Status);
}

private sealed class NewState : IReservationState
{
    public static readonly NewState Instance = new();

    public Status Status => Status.New;

    public void Pay(Reservation reservation)
    {
        reservation.Charge();
        reservation.MoveTo(PaidState.Instance);
        reservation.Record("charged");
    }
}

private sealed class PaidState : IReservationState
{
    public static readonly PaidState Instance = new();

    public Status Status => Status.Paid;

    public void Use(Reservation reservation)
    {
        reservation.MoveTo(ClosedState.Used);
        reservation.Record("gate opened");
    }
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** domyślna metoda **rzuca wyjątek**, nie jest pusta - pusta metoda po cichu zmieniłaby zachowanie niedozwolonych przejść. Kolejność "obciążenie - zmiana stanu - efekt" zostaje dokładnie jak w Start; test awarii bramki to pilnuje. Stany są zagnieżdżone w `Reservation`, więc widzą jej prywatne `Charge`, `MoveTo`, `Record` - interfejs publiczny kontekstu się nie zmienia.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s08 1 2`

### Krok 3: Expire i Cancel do stanów

**Zmiany:**

1. Powtórz ruch z kroku 2 dla `Expire` i `Cancel`: dodaj w interfejsie domyślne implementacje rzucające `Invalid("expire")` i `Invalid("cancel")`.
2. Warunek `if/else if` z `Cancel` rozłóż na dwa stany: `NewState.Cancel` (zwolnienie miejsc) i `PaidState.Cancel` (zwrot i zwolnienie miejsc). `Expire` trafia do `NewState`.
3. W kontekście `Expire` i `Cancel` zamień na delegacje `_state.Expire(this)` i `_state.Cancel(this)`.

**Przed (`Reservation.cs`):**

```csharp
public void Expire()
{
    if (_state.Status != Status.New)
    {
        throw new InvalidOperationException("cannot expire in " + _state.Status);
    }
    MoveTo(ClosedState.Expired);
    Record("seats released");
}

public void Cancel()
{
    if (_state.Status == Status.New)
    {
        MoveTo(ClosedState.Cancelled);
        Record("seats released");
    }
    else if (_state.Status == Status.Paid)
    {
        MoveTo(ClosedState.Cancelled);
        Record("refund");
        Record("seats released");
    }
    else
    {
        throw new InvalidOperationException("cannot cancel in " + _state.Status);
    }
}
```

**Po (`Reservation.cs`, zmienione fragmenty):**

```csharp
public void Expire()
{
    _state.Expire(this);
}

public void Cancel()
{
    _state.Cancel(this);
}
```

w interfejsie `IReservationState` (obok `Pay` i `Use`):

```csharp
void Expire(Reservation reservation) => throw Invalid("expire");

void Cancel(Reservation reservation) => throw Invalid("cancel");
```

w `NewState`:

```csharp
public void Expire(Reservation reservation)
{
    reservation.MoveTo(ClosedState.Expired);
    reservation.Record("seats released");
}

public void Cancel(Reservation reservation)
{
    reservation.MoveTo(ClosedState.Cancelled);
    reservation.Record("seats released");
}
```

w `PaidState`:

```csharp
public void Cancel(Reservation reservation)
{
    reservation.MoveTo(ClosedState.Cancelled);
    reservation.Record("refund");
    reservation.Record("seats released");
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** tabelę przejść da się teraz przeczytać z kodu stanów. Stany są bezstanowymi instancjami współdzielonymi, dane zostają w `Reservation`.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s08 2 3`

### Rozwiązanie i uzasadnienie

Kontekst tylko deleguje. Każdy stan implementuje wyłącznie dozwolone przejścia, reszta rzuca wyjątek z tym samym komunikatem co Start, a stan po błędzie się nie zmienia.

### Pułapki

- Zmiana stanu przed efektem zewnętrznym (albo odwrotnie) "przy okazji" - to inna semantyka przy awarii.
- `volatile` na polu stanu nie daje atomowości przejścia przy współbieżnych `Pay` i `Cancel`.
- Przy dwóch stanach i jednej akcji `switch` bywa czytelniejszy niż hierarchia.
- Kilka niezależnych osi stanu (płatność, obecność) w jednej hierarchii - eksplozja klas.

### Pytanie do sali

Czym różni się State od Strategy, skoro oba to "obiekt, któremu delegujemy"?

## Scena s09. Replace Hard-coded Notifications with Observer - po opłaceniu

**Temat ze slajdów:** 13.1. Zastąpienie zakodowanych powiadomień Obserwatorem; 13.2. Obserwator: zasady powiadamiania muszą być jawne
**Namespace:** `Training.Workshop.M5.S09Observer` · **Test:** `scripts/warsztat.sh --lang cs test m5/s09` (`S09EquivalenceTest`, `S09SolutionTest`)
**Czas:** ~15 min

### W skrócie

**Co robimy:** `PaymentService.Confirm` po zapisaniu opłaty na sztywno wysyła mail, SMS i nalicza punkty lojalnościowe. Wprowadzamy zdarzenie `ReservationPaid`, zamieniamy trzech odbiorców na implementacje `IPaymentListener`, a subskrypcje przenosimy do korzenia kompozycji `PaymentServices`.

**Zasada:** Observer odwraca zależność: publikujący zna tylko interfejs odbiorcy, a odbiorcy są do niego rejestrowani z zewnątrz. Kontrakt musi być jawny - synchroniczność, kolejność, polityka błędów (tu fail-fast) i wyrejestrowanie. Dodanie nowych odbiorców to rozszerzenie zachowania, a nie część refaktoryzacji.

**Efekt:** Serwis nie zna `IMailer`, `ISmsGateway` ani `ILoyaltyProgram`, a odbiorcy są wołani w kolejności subskrypcji jak w Start. Zostaje ta sama semantyka błędów: awaria SMS przerywa naliczanie punktów, a jej zmiana byłaby osobną decyzją.

**Różnica względem Javy:** w kroku 3 `Subscription extends AutoCloseable` to `ISubscription : IDisposable` (idempotentne `Dispose()` przez `Interlocked.CompareExchange`), a `CopyOnWriteArrayList` to pole `ImmutableList<Registration>` aktualizowane `ImmutableInterlocked.Update` - iteracja idzie po migawce, jak w Javie. Porty mają prefiks `I` (`IMailer`, `ISmsGateway`, `ILoyaltyProgram`). W testach lambdy Javy zastąpiły małe klasy: jedna klasa `Ports` implementuje trzy porty, a odbiorcy w `S09SolutionTest` to klasa `Listener(Action<ReservationPaid>)`. Parametr `event` nazywa się `paidEvent` (`event` to słowo kluczowe C#).

### Co widzimy

Serwis powstaje w korzeniu kompozycji `PaymentServices.Standard(mailer, sms, loyalty)`. `PaymentService.Confirm` zapisuje opłatę, a potem na sztywno wysyła mail, SMS i nalicza punkty lojalnościowe (1 pkt za pełne 10.00). Semantyka do zachowania: **kolejność** mail - SMS - punkty i **fail-fast**: wyjątek w SMS przerywa, punkty nie są naliczone, ale opłata jest już zapisana.

```csharp
_paid.Add(payment.ReservationId);
_mailer.Send(payment.Email, "Potwierdzenie platnosci " + ...);
_sms.Send(payment.Phone, "Oplacono " + payment.ReservationId);
_loyalty.AddPoints(payment.Email, (int)payment.Amount.Amount / 10);
```

Test używa fake'ów zapisujących do wspólnego logu i sprawdza kolejność oraz awarie w mailu i SMS.

### Krok 1: zdarzenie i Extract Method

**Zmiany:**

1. Utwórz nowy plik `ReservationPaid.cs`: rekord zdarzenia z danymi `ReservationId`, `Email`, `Phone`, `Amount`.
2. Wydziel trzy powiadomienia z `Confirm` do prywatnej metody `NotifyPaid(ReservationPaid paidEvent)` (wyodrębnienie metody); `Confirm` tworzy zdarzenie i przekazuje je dalej.

**Przed:**

```csharp
_paid.Add(payment.ReservationId);
_mailer.Send(payment.Email,
    "Potwierdzenie platnosci " + payment.ReservationId + ": " + payment.Amount);
_sms.Send(payment.Phone, "Oplacono " + payment.ReservationId);
_loyalty.AddPoints(payment.Email, (int)payment.Amount.Amount / 10);
```

**Po (`ReservationPaid.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

/// <summary>Zdarzenie - fakt "rezerwacja opłacona" z danymi potrzebnymi odbiorcom.</summary>
public sealed record ReservationPaid(string ReservationId, string Email, string Phone, Money Amount);
```

**Po (`PaymentService.cs`):** w `Confirm` po `_paid.Add(...)` zostaje tylko publikacja, a trzy wywołania lądują w nowej metodzie:

```csharp
_paid.Add(payment.ReservationId);
NotifyPaid(new ReservationPaid(
    payment.ReservationId, payment.Email, payment.Phone, payment.Amount));
```

```csharp
private void NotifyPaid(ReservationPaid paidEvent)
{
    _mailer.Send(paidEvent.Email,
        "Potwierdzenie platnosci " + paidEvent.ReservationId + ": " + paidEvent.Amount);
    _sms.Send(paidEvent.Phone, "Oplacono " + paidEvent.ReservationId);
    _loyalty.AddPoints(paidEvent.Email, (int)paidEvent.Amount.Amount / 10);
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s09` - zielone.
**Co powiedzieć:** zdarzenie to fakt z danymi potrzebnymi odbiorcom. Jeszcze nic nie jest dynamiczne.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s09 0 1`

### Krok 2: odbiorcy jako obserwatorzy

**Zmiany:**

1. Utwórz nowy plik `IPaymentListener.cs`: interfejs z metodą `OnPaid(ReservationPaid paidEvent)`.
2. Utwórz trzy rekordy-obserwatorów: `MailConfirmation(IMailer)`, `SmsConfirmation(ISmsGateway)`, `LoyaltyPoints(ILoyaltyProgram)` - każdy przejmuje jedną linię z `NotifyPaid`.
3. W serwisie zamień trzy pola portów na `IReadOnlyList<IPaymentListener> _listeners`; konstruktor (ta sama sygnatura) buduje listę wyrażeniem kolekcji w starej kolejności: mail, SMS, punkty.
4. `NotifyPaid` zamień na pętlę po `_listeners` - celowo **bez** `try/catch` (fail-fast jak w Start).

**Przed:**

```csharp
private void NotifyPaid(ReservationPaid paidEvent)
{
    _mailer.Send(paidEvent.Email,
        "Potwierdzenie platnosci " + paidEvent.ReservationId + ": " + paidEvent.Amount);
    _sms.Send(paidEvent.Phone, "Oplacono " + paidEvent.ReservationId);
    _loyalty.AddPoints(paidEvent.Email, (int)paidEvent.Amount.Amount / 10);
}
```

**Po (`IPaymentListener.cs`, nowy plik):**

```csharp
public interface IPaymentListener
{
    void OnPaid(ReservationPaid paidEvent);
}
```

**Po (`MailConfirmation.cs`, nowy plik):**

```csharp
public sealed record MailConfirmation(IMailer Mailer) : IPaymentListener
{
    public void OnPaid(ReservationPaid paidEvent)
    {
        Mailer.Send(paidEvent.Email,
            "Potwierdzenie platnosci " + paidEvent.ReservationId + ": " + paidEvent.Amount);
    }
}
```

**Po (`SmsConfirmation.cs`, nowy plik):**

```csharp
public sealed record SmsConfirmation(ISmsGateway Sms) : IPaymentListener
{
    public void OnPaid(ReservationPaid paidEvent)
    {
        Sms.Send(paidEvent.Phone, "Oplacono " + paidEvent.ReservationId);
    }
}
```

**Po (`LoyaltyPoints.cs`, nowy plik):**

```csharp
public sealed record LoyaltyPoints(ILoyaltyProgram Loyalty) : IPaymentListener
{
    public void OnPaid(ReservationPaid paidEvent)
    {
        Loyalty.AddPoints(paidEvent.Email, (int)paidEvent.Amount.Amount / 10);
    }
}
```

**Po (`PaymentService.cs`):** pola, konstruktor i `NotifyPaid` wyglądają teraz tak (`Confirm` bez zmian):

```csharp
private readonly IReadOnlyList<IPaymentListener> _listeners;
private readonly List<string> _paid = [];

public PaymentService(IMailer mailer, ISmsGateway sms, ILoyaltyProgram loyalty)
{
    _listeners =
    [
        new MailConfirmation(mailer), new SmsConfirmation(sms), new LoyaltyPoints(loyalty),
    ];
}

private void NotifyPaid(ReservationPaid paidEvent)
{
    foreach (var listener in _listeners)
    {
        listener.OnPaid(paidEvent);
    }
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** konstruktor bez zmian, relacja wciąż "ci sami trzej odbiorcy" - to nadal czysta refaktoryzacja.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s09 1 2`

### Krok 3: Subscribe i korzeń kompozycji

**Zmiany:**

1. Utwórz nowy plik `ISubscription.cs`: interfejs `ISubscription : IDisposable`.
2. W serwisie dodaj metodę `Subscribe(IPaymentListener listener)` zwracającą `ISubscription` oraz pole `ImmutableList<Registration> _listeners` aktualizowane przez `ImmutableInterlocked.Update`. `Registration` to prywatna klasa (celowo nie rekord), a prywatna klasa `Unsubscribe` ma idempotentne `Dispose()` (`Interlocked.CompareExchange`).
3. Usuń konstruktor z portami - serwis ma teraz konstruktor bezargumentowy. `Confirm` iteruje po migawce `_listeners` i woła `registration.Listener.OnPaid(paidEvent)`.
4. Subskrypcje trzech odbiorców przenieś do istniejącego korzenia kompozycji `PaymentServices.Standard(...)`.

**Przed:**

```csharp
public PaymentService(IMailer mailer, ISmsGateway sms, ILoyaltyProgram loyalty)
{
    _listeners =
    [
        new MailConfirmation(mailer), new SmsConfirmation(sms), new LoyaltyPoints(loyalty),
    ];
}
```

**Po (`ISubscription.cs`, nowy plik):**

```csharp
/// <summary>Uchwyt subskrypcji - Dispose() wyrejestrowuje, idempotentnie.</summary>
public interface ISubscription : IDisposable
{
}
```

**Po (`PaymentService.cs`, cały plik):**

```csharp
using System.Collections.Immutable;

public sealed class PaymentService
{
    private readonly List<string> _paid = [];
    private ImmutableList<Registration> _listeners = [];

    public ISubscription Subscribe(IPaymentListener listener)
    {
        ArgumentNullException.ThrowIfNull(listener);
        var registration = new Registration(listener);
        ImmutableInterlocked.Update(ref _listeners, listeners => listeners.Add(registration));
        return new Unsubscribe(this, registration);
    }

    public void Confirm(Payment payment)
    {
        if (payment.Amount.Amount <= 0)
        {
            throw new ArgumentException("amount must be positive");
        }
        _paid.Add(payment.ReservationId);
        var paidEvent = new ReservationPaid(
            payment.ReservationId, payment.Email, payment.Phone, payment.Amount);
        foreach (var registration in _listeners)
        {
            registration.Listener.OnPaid(paidEvent);
        }
    }

    public IReadOnlyList<string> Paid => _paid.ToList();

    /// <summary>Osobny obiekt na każde Subscribe - ten sam odbiorca zapisany dwa razy to dwie rejestracje.</summary>
    private sealed class Registration(IPaymentListener listener)
    {
        public IPaymentListener Listener => listener;
    }

    private sealed class Unsubscribe(PaymentService service, Registration registration) : ISubscription
    {
        private int _active = 1;

        public void Dispose()
        {
            if (Interlocked.CompareExchange(ref _active, 0, 1) == 1)
            {
                ImmutableInterlocked.Update(ref service._listeners, listeners => listeners.Remove(registration));
            }
        }
    }
}
```

**Po (`PaymentServices.cs`):**

```csharp
public static PaymentService Standard(IMailer mailer, ISmsGateway sms, ILoyaltyProgram loyalty)
{
    var service = new PaymentService();
    service.Subscribe(new MailConfirmation(mailer));
    service.Subscribe(new SmsConfirmation(sms));
    service.Subscribe(new LoyaltyPoints(loyalty));
    return service;
}
```

**Uruchom:** test zielony; `S09SolutionTest` pokazuje podwójną subskrypcję, idempotentne wyrejestrowanie i nowego odbiorcę.
**Co powiedzieć:** `Registration` jest klasą, nie rekordem - rekord z tym samym odbiorcą byłby równy (`Equals`) i `Remove` usunąłby cudzą rejestrację. Dodanie czwartego odbiorcy (push) to już **rozszerzenie** zachowania, zatwierdzane osobno.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s09 2 3`

### Rozwiązanie i uzasadnienie

Serwis zna tylko `IPaymentListener`. Kontrakt jest jawny: synchronicznie, w kolejności subskrypcji, fail-fast, iteracja po migawce.

### Pułapki

- "Przy okazji" dodany `try/catch` wokół każdego odbiorcy - zmienia politykę błędów (punkty naliczone mimo awarii SMS).
- Asynchroniczne powiadamianie (`Task.Run`, kolejka) - inny wątek, inna transakcja, inna kolejność.
- Odbiorca usunięty w trakcie publikacji może być jeszcze wywołany w bieżącej (migawka).
- Subskrypcja bez `Dispose()` trzyma przy życiu graf obiektów (klasyczny wyciek przez zdarzenia w .NET).

### Pytanie do sali

Czy błąd wysyłki SMS powinien przerywać naliczanie punktów? Kto o tym decyduje i gdzie to zapisać?

## Scena s10. Replace Implicit Tree with Composite - zestawy baru

**Temat ze slajdów:** 16.3. Zastąpienie ukrytego drzewa Kompozytem; 3.3. Test równoważności przed zmianą i po niej
**Namespace:** `Training.Workshop.M5.S10ImplicitTree` · **Test:** `scripts/warsztat.sh --lang cs test m5/s10` (`S10DifferentialTest`, `S10EquivalenceTest`)
**Czas:** ~15 min

### W skrócie

**Co robimy:** Zestaw combo to zagnieżdżona lista, w której pierwszy element jest nazwą, a `BarMenu` powtarza testy `is` i rzutowania. Budujemy obok jawny Composite (`Product`, `Combo`) z mapperem ze starego formatu, a potem przenosimy na niego `Price` i `Render` po jednej operacji.

**Zasada:** Replace Implicit Tree with Composite zamienia drzewo ukryte w konwencji danych na jawne typy liścia i węzła. Mapper tłumaczy stary format i zgłasza błąd dla danych, których nie da się wiernie odwzorować. Stara i nowa reprezentacja żyją obok siebie, a test różnicowy potwierdza zgodność, ale nie zastępuje niezależnych oczekiwań.

**Efekt:** `BarMenu` nie ma testów `is` ani rzutowań, a komunikaty błędów zostają jak w Start. Format trwały (zagnieżdżone listy) się nie zmienia - jego zmiana to osobna decyzja z osobnym testem.

**Różnica względem Javy:** stary format `List<?>` to `IReadOnlyList<object>` (w testach tablice `object[]` i wyrażenia kolekcji), `instanceof` i `switch` po typie to `is` i switch expression. `MenuItem` to interfejs `IMenuItem` z właściwościami `Name` i `Price` - rekord `Product(string Name, Money Price)` spełnia go wprost, a `Combo.Price` to właściwość licząca sumę. Interfejs nie jest zamknięty (C# nie ma `sealed interface`). `S10DifferentialTest` używa `new Random(42)` z .NET, które daje inne drzewa niż `java.util.Random(42)` - test jest różnicowy (Start kontra nowy kod), więc to bez znaczenia.

### Co widzimy

Zestaw combo to zagnieżdżona lista: pierwszy element to nazwa, kolejne to `"nazwa=cena"` albo podlisty. `BarMenu.Price` i `BarMenu.Render` powtarzają `is` i rzutowania, a `Render` dla każdego węzła liczy cenę od nowa.

```csharp
["Zestaw Duo", "Popcorn L=18.00",
    new object[] { "Napoje", "Cola 0.5=9.00", "Cola 0.5=9.00" }, "Nachos=14.00"]
```

Testy: `S10EquivalenceTest` (niezależne oczekiwania policzone ręcznie, także błędy) i `S10DifferentialTest` (500 losowych drzew ze stałym ziarnem - stara reprezentacja kontra nowa).

### Krok 1: Composite i mapper obok starego kodu

**Zmiany:**

1. Utwórz nowe pliki Composite: `IMenuItem.cs` (interfejs z `Name`, `Price`, `Render(depth, text)`), `Product.cs` (`sealed record Product(string Name, Money Price)` - liść) i `Combo.cs` (`sealed record Combo(string Name, IReadOnlyList<IMenuItem> Items)` z kopią listy; `Price` sumuje elementy, `Render` wypisuje węzeł i dzieci).
2. Utwórz nowy plik `MenuMapper.cs`: statyczna metoda `FromNested(IReadOnlyList<object>)` tłumaczy rekurencyjnie stary format na drzewo i zachowuje komunikaty błędów Start ("combo needs a name", "product needs a price: ...", "unsupported element: ...").
3. `BarMenu` zostaje bez zmian - obie reprezentacje żyją obok siebie.

**Przed:** brak - nowe pliki `IMenuItem.cs`, `Product.cs`, `Combo.cs`, `MenuMapper.cs` (`BarMenu.cs` bez zmian).

**Po (`IMenuItem.cs`, nowy plik):**

```csharp
using System.Text;
using Training.Workshop.Shared;

public interface IMenuItem
{
    string Name { get; }

    Money Price { get; }

    void Render(int depth, StringBuilder text);
}
```

**Po (`Product.cs`, nowy plik):**

```csharp
using System.Text;
using Training.Workshop.Shared;

public sealed record Product(string Name, Money Price) : IMenuItem
{
    public void Render(int depth, StringBuilder text)
    {
        text.Append(new string(' ', 2 * depth)).Append(Name).Append(' ').Append(Price).Append('\n');
    }
}
```

**Po (`Combo.cs`, nowy plik):**

```csharp
using System.Text;
using Training.Workshop.Shared;

public sealed record Combo(string Name, IReadOnlyList<IMenuItem> Items) : IMenuItem
{
    public IReadOnlyList<IMenuItem> Items { get; } = Items.ToList().AsReadOnly();

    public Money Price
    {
        get
        {
            var total = Money.Zero;
            foreach (var item in Items)
            {
                total = total.Plus(item.Price);
            }
            return total;
        }
    }

    public void Render(int depth, StringBuilder text)
    {
        text.Append(new string(' ', 2 * depth)).Append(Name).Append(' ').Append(Price).Append('\n');
        foreach (var item in Items)
        {
            item.Render(depth + 1, text);
        }
    }
}
```

**Po (`MenuMapper.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public static class MenuMapper
{
    public static Combo FromNested(IReadOnlyList<object> combo)
    {
        if (combo.Count == 0 || combo[0] is not string name)
        {
            throw new ArgumentException("combo needs a name");
        }
        var items = new List<IMenuItem>();
        foreach (var element in combo.Skip(1))
        {
            items.Add(element switch
            {
                string product => Product(product),
                IReadOnlyList<object> nested => FromNested(nested),
                _ => throw new ArgumentException("unsupported element: " + element),
            });
        }
        return new Combo(name, items);
    }

    private static Product Product(string text)
    {
        if (!text.Contains('='))
        {
            throw new ArgumentException("product needs a price: " + text);
        }
        var separator = text.IndexOf('=');
        return new Product(text[..separator], Money.Of(text[(separator + 1)..]));
    }
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s10` - zielone, w tym `CompositeFromMapperMatchesLegacyAlready`.
**Co powiedzieć:** obie reprezentacje żyją obok siebie. Mapper zachowuje komunikaty błędów starego kodu - to też obserwowalne zachowanie.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s10 0 1`

### Krok 2: Price na Composite

**Zmiany:**

1. Ciało `BarMenu.Price` zastąp jedną linią: `return MenuMapper.FromNested(combo).Price;`.
2. Usuń nieużywaną już metodę `RequireName` (bezpieczne usunięcie). Stary `Render` zostaje i dalej działa, bo woła nowe `Price`.

**Przed:**

```csharp
public Money Price(IReadOnlyList<object> combo)
{
    RequireName(combo);
    var total = Money.Zero;
    foreach (var element in combo.Skip(1))
    {
        if (element is string product)
        {
            total = total.Plus(ProductPrice(product));
        }
        else if (element is IReadOnlyList<object> nested)
        {
            total = total.Plus(Price(nested));
        }
        else
        {
            throw new ArgumentException("unsupported element: " + element);
        }
    }
    return total;
}
```

**Po:**

```csharp
public Money Price(IReadOnlyList<object> combo)
{
    return MenuMapper.FromNested(combo).Price;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** jedna operacja naraz; `Render` wciąż jest stary i działa, bo woła nowe `Price`.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s10 1 2`

### Krok 3: Render na Composite

**Zmiany:**

1. W publicznym `Render` zbuduj drzewo mapperem i deleguj: `MenuMapper.FromNested(combo).Render(0, text)`.
2. Usuń starą prywatną rekurencję `Render(combo, depth, text)` z testami `is` oraz nieużywane już `ProductPrice` i `Indent` (bezpieczne usunięcie).

**Przed (`BarMenu.cs`):**

```csharp
private void Render(IReadOnlyList<object> combo, int depth, StringBuilder text)
{
    text.Append(Indent(depth)).Append(combo[0])
        .Append(' ').Append(Price(combo)).Append('\n');
    foreach (var element in combo.Skip(1))
    {
        if (element is string product)
        {
            text.Append(Indent(depth + 1)).Append(product, 0, product.IndexOf('='))
                .Append(' ').Append(ProductPrice(product)).Append('\n');
        }
        else if (element is IReadOnlyList<object> nested)
        {
            Render(nested, depth + 1, text);
        }
        else
        {
            throw new ArgumentException("unsupported element: " + element);
        }
    }
}

private static Money ProductPrice(string product)
{
    if (!product.Contains('='))
    {
        throw new ArgumentException("product needs a price: " + product);
    }
    return Money.Of(product[(product.IndexOf('=') + 1)..]);
}

private static string Indent(int depth) => new(' ', 2 * depth);
```

**Po (`BarMenu.cs`):** po usunięciach w klasie zostają już tylko dwie metody:

```csharp
public Money Price(IReadOnlyList<object> combo)
{
    return MenuMapper.FromNested(combo).Price;
}

public string Render(IReadOnlyList<object> combo)
{
    var text = new StringBuilder();
    MenuMapper.FromNested(combo).Render(0, text);
    return text.ToString();
}
```

**Uruchom:** test zielony, test różnicowy `FinalBarMenuMatchesLegacy` zielony.
**Co powiedzieć:** format trwały (zagnieżdżone listy) zostaje. Jego zmiana na JSON czy tabelę w bazie to osobna decyzja z osobnym testem.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s10 2 3`

### Rozwiązanie i uzasadnienie

Jawne drzewo z typami zamiast konwencji "pierwszy element to nazwa". Test różnicowy daje szerokie pokrycie, a niezależne oczekiwania chronią przed sytuacją, w której oba warianty mylą się tak samo.

### Pułapki

- Test różnicowy jako jedyny test - jeśli stary kod ma błąd, nowy go wiernie powtórzy.
- Brak limitu głębokości dla danych niezaufanych - `StackOverflowException`, którego w .NET nie da się złapać (proces kończy działanie).
- Cykl w danych (lista zawierająca samą siebie) - nieskończona rekurencja w obu wersjach.

### Pytanie do sali

Co zrobić, gdy mapper natrafi na dane, których nie da się wiernie odwzorować w drzewie?

## Scena s11. Transparent vs Safe Composite - add() na liściu

**Temat ze slajdów:** 16.4. Kompozyt przezroczysty czy bezpieczny
**Namespace:** `Training.Workshop.M5.S11SafeComposite` · **Test:** `scripts/warsztat.sh --lang cs test m5/s11` (`S11EquivalenceTest`, `S11SolutionTest`)
**Czas:** ~8 min

### W skrócie

**Co robimy:** W Transparent Composite `Add()` jest we wspólnym `MenuComponent`, więc wywołanie go na produkcie kompiluje się i wybucha dopiero w runtime. Przesuwamy `Add` i `Children` w dół do `Combo` (Safe Composite), a potem pokazujemy niemutowalne drzewo z rekordów bez `Add`.

**Zasada:** Transparent Composite trzyma zarządzanie dziećmi we wspólnym typie, więc liść musi rzucić wyjątek albo nic nie robić. Safe Composite trzyma je tylko w węźle, dzięki czemu błąd przenosi się do kompilacji, ale klient musi wiedzieć, czy ma w ręku węzeł. Gdy drzewo powstaje raz, dylemat znika, bo `Add` nie jest potrzebne nigdzie.

**Efekt:** Dodanie dziecka do produktu przestaje się kompilować, a `MenuComponent` nie ma już `Add` ani `Children`. Kosztem jest dokładniejsze typowanie zmiennych w katalogu - rzutowanie `(Combo)` w kliencie oznaczałoby powrót do problemu.

**Różnica względem Javy:** krok 2 w Javie to `sealed interface MenuComponent` + rekordy. W C# `MenuComponent` jest `abstract record` z konstruktorem `private protected` (podtypy tylko w tym projekcie), a `Product` i `Combo` to `sealed record`; nazwa typu zostaje `MenuComponent` jak w Start. `S11SolutionTest` sprawdza refleksją brak `Add(MenuComponent)` na `Product` i brak właściwości `Children` na `MenuComponent`. Test pułapki Start (`TransparentCompositeFailsAtRuntime`) po `jump` kończy się bez sprawdzeń (zamiast `assumeTrue`).

### Co widzimy

Transparent Composite: `Add()` i `Children` są we wspólnej klasie abstrakcyjnej `MenuComponent`, liść `Product` dziedziczy `Add()`, które rzuca `NotSupportedException`. Klient typuje wszystko jako `MenuComponent` i nic go nie chroni przed `nachos.Add(sauce)`.

```csharp
public virtual void Add(MenuComponent child)
{
    throw new NotSupportedException("cannot add to " + Name);
}
```

### Krok 1: Safe Composite - Push Members Down

**Zmiany:**

1. Przenieś `Add` i `Children` z `MenuComponent` w dół do `Combo` (przesunięcie składowych do podklasy): w bazie je usuń, w `Combo` zostają jako zwykłe składowe bez `override`.
2. Kompilacja wskaże konflikty w `ComboCatalog` - zmień typ zmiennych z `MenuComponent` na `Combo` (tu: `var`) wszędzie tam, gdzie dodajemy dzieci.
3. `Describe()` w bazie staje się `virtual` i zwraca sam `Name + " " + Price`; `Combo` nadpisuje go i rozszerza opis o dzieci.

**Przed (`MenuComponent.cs`, cały plik):**

```csharp
using Training.Workshop.Shared;

public abstract class MenuComponent
{
    public abstract string Name { get; }

    public abstract Money Price { get; }

    public virtual void Add(MenuComponent child)
    {
        throw new NotSupportedException("cannot add to " + Name);
    }

    public virtual IReadOnlyList<MenuComponent> Children => [];

    public string Describe()
    {
        var text = Name + " " + Price;
        if (Children.Count == 0)
        {
            return text;
        }
        return text + " [" + string.Join(", ", Children.Select(child => child.Describe())) + "]";
    }
}
```

**Przed (`ComboCatalog.cs`):**

```csharp
MenuComponent combo = new Combo("Zestaw Rodzinny");
combo.Add(new Product("Popcorn XL", "24.00"));
```

**Po (`MenuComponent.cs`, cały plik):**

```csharp
using Training.Workshop.Shared;

public abstract class MenuComponent
{
    public abstract string Name { get; }

    public abstract Money Price { get; }

    public virtual string Describe()
    {
        return Name + " " + Price;
    }
}
```

**Po (`Combo.cs`):** `Add` i `Children` tracą `override`, dochodzi nadpisanie `Describe` (reszta klasy bez zmian):

```csharp
public void Add(MenuComponent child)
{
    _children.Add(child);
}

public IReadOnlyList<MenuComponent> Children => _children.ToList().AsReadOnly();

public override string Describe()
{
    if (_children.Count == 0)
    {
        return base.Describe();
    }
    return base.Describe() + " [" + string.Join(", ", _children.Select(child => child.Describe())) + "]";
}
```

**Po (`ComboCatalog.cs`):** w `Family()` i `Duo()` zmienne `combo` i `drinks` mają typ `var` (czyli `Combo`):

```csharp
var combo = new Combo("Zestaw Rodzinny");
combo.Add(new Product("Popcorn XL", "24.00"));
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s11` - zielone; `S11SolutionTest` pokazuje, że `Product` nie ma już `Add`.
**Co powiedzieć:** błąd przeniósł się z runtime do kompilacji. Ceną jest to, że klient musi wiedzieć, czy ma w ręku węzeł.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s11 0 1`

### Krok 2: niemutowalny Composite w C# - abstrakcyjny rekord

**Zmiany:**

1. Zamień bazę na `abstract record MenuComponent` z konstruktorem `private protected` i abstrakcyjnymi `Name`, `Price`, `Describe()`.
2. Zamień `Product` i `Combo` na `sealed record`. `Combo` dostaje dzieci w konstruktorze (`Combo(string name, IEnumerable<MenuComponent> children)`, kopia `ToList().AsReadOnly()`) i statyczne wejście `Combo.Of(string name, params MenuComponent[] children)`; metoda `Add` znika.
3. Przepisz `ComboCatalog` deklaratywnie - zagnieżdżone wywołania `Combo.Of(...)` wprost w `Find` zamiast sekwencji `Add`; metody pomocnicze `Family()` i `Duo()` znikają.

**Przed (`ComboCatalog.cs`):**

```csharp
private static MenuComponent Duo()
{
    var combo = new Combo("Zestaw Duo");
    combo.Add(new Product("Popcorn L", "18.00"));
    combo.Add(new Product("Cola", "9.00"));
    combo.Add(new Product("Cola", "9.00"));
    return combo;
}
```

**Po (`MenuComponent.cs`, cały plik):**

```csharp
using Training.Workshop.Shared;

public abstract record MenuComponent
{
    private protected MenuComponent()
    {
    }

    public abstract string Name { get; }

    public abstract Money Price { get; }

    public abstract string Describe();
}
```

**Po (`Product.cs`, cały plik):**

```csharp
using Training.Workshop.Shared;

public sealed record Product(string Name, Money Price) : MenuComponent
{
    public override string Name { get; } = Name;

    public override Money Price { get; } = Price;

    public Product(string name, string price)
        : this(name, Money.Of(price))
    {
    }

    public override string Describe()
    {
        return Name + " " + Price;
    }
}
```

**Po (`Combo.cs`, cały plik):**

```csharp
using Training.Workshop.Shared;

public sealed record Combo : MenuComponent
{
    public Combo(string name, IEnumerable<MenuComponent> children)
    {
        Name = name;
        Children = children.ToList().AsReadOnly();
    }

    public override string Name { get; }

    public IReadOnlyList<MenuComponent> Children { get; }

    public static Combo Of(string name, params MenuComponent[] children)
    {
        return new Combo(name, children);
    }

    public override Money Price
    {
        get
        {
            var total = Money.Zero;
            foreach (var child in Children)
            {
                total = total.Plus(child.Price);
            }
            return total;
        }
    }

    public override string Describe()
    {
        var text = Name + " " + Price;
        if (Children.Count == 0)
        {
            return text;
        }
        return text + " [" + string.Join(", ", Children.Select(child => child.Describe())) + "]";
    }
}
```

**Po (`ComboCatalog.cs`, cały plik):**

```csharp
public sealed class ComboCatalog
{
    public MenuComponent Find(string code) => code switch
    {
        "family" => Combo.Of("Zestaw Rodzinny",
            new Product("Popcorn XL", "24.00"),
            Combo.Of("Napoje",
                new Product("Cola", "9.00"),
                new Product("Cola", "9.00"),
                new Product("Woda", "7.00"))),
        "duo" => Combo.Of("Zestaw Duo",
            new Product("Popcorn L", "18.00"),
            new Product("Cola", "9.00"),
            new Product("Cola", "9.00")),
        "nachos" => new Product("Nachos", "14.00"),
        _ => throw new ArgumentException("unknown combo: " + code),
    };
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** gdy drzewo powstaje raz, `Add()` nie jest potrzebne nigdzie - dylemat Safe/Transparent znika. Lista dzieci to kopia `ToList().AsReadOnly()`, więc nawet rzutowanie na `ICollection<T>` nie pozwoli jej zmienić.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s11 1 2`

### Rozwiązanie i uzasadnienie

Safe Composite, gdy drzewo jest modyfikowane; niemutowalny Composite, gdy jest budowane raz (wtedy budowę przejmuje Builder albo fabryka - scena s06).

### Pułapki

- Transparent Composite z domyślnym pustym `Add()` (bez wyjątku) - dziecko po cichu ginie.
- Rzutowanie `(Combo)component` w kliencie zamiast poprawienia typów - Safe Composite w przebraniu Transparent.

### Pytanie do sali

Kiedy Transparent Composite jest lepszym wyborem mimo ryzyka błędu w runtime?

## Scena s12. Replace One/Many Distinctions with Composite - zwroty

**Temat ze slajdów:** 16.5. Jeden element lub wiele oraz wyodrębnienie Kompozytu (zastąpienie rozróżnienia „jeden lub wiele” Kompozytem)
**Namespace:** `Training.Workshop.M5.S12OneMany` · **Test:** `scripts/warsztat.sh --lang cs test m5/s12` (`S12EquivalenceTest`, `S12SolutionTest`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** `RefundService` ma osobne `Refund` i `RefundAll` z regułą zwrotu zapisaną dwa razy, a `CancellationDesk` sam wybiera między nimi. Najpierw usuwamy duplikację reguły, potem wprowadzamy wspólny kontrakt `IRefundable` z liściem i grupą, a stare metody usuwamy po etapie `[Obsolete]`.

**Zasada:** Replace One/Many Distinctions with Composite zastępuje dwie ścieżki API - dla jednego elementu i dla wielu - jednym kontraktem, w którym grupa też jest elementem. Nie wolno przy tym zmienić kolejności przetwarzania, wyniku dla pustej grupy ani liczby efektów naliczanych raz na wywołanie.

**Efekt:** Jest jedna metoda `Refund(IRefundable)`, reguła biletu żyje w liściu, suma w węźle, a klient nie ma już `if`. Potrącenie 3.00 zostaje w serwisie i nadal jest naliczane raz na zwrot, a nie raz na bilet.

**Różnica względem Javy:** `sealed interface Refundable` to zwykły interfejs `IRefundable` (bez zamknięcia hierarchii). `@Deprecated` to `[Obsolete]` - przy `TreatWarningsAsErrors` każde wywołanie przestarzałej metody (ostrzeżenie CS0618) zatrzymuje build, dlatego w kroku 2 klient przechodzi na nowy kontrakt od razu, a test delegacji (`DeprecatedWrappersInStep2DelegateToTheNewContract`) wyłącza CS0618 przez `#pragma`. `Duration.toHours() >= 24` to `TimeSpan.TotalHours >= 24`; w `RefundAll` w Start jest rzutowanie na `long`, żeby zachować "trochę inny" zapis reguły.

### Co widzimy

`RefundService` ma `Refund(ticket, now)` i `RefundAll(tickets, now)`, a klient `CancellationDesk` sam wybiera jedną z nich (`if (tickets.Count == 1)`). Reguła zwrotu (>= 24h 100%, < 24h 50%, po starcie 0) jest napisana dwa razy, trochę inaczej. Potrącenie 3.00 jest naliczane raz na zwrot i nie schodzi poniżej 0.

```csharp
foreach (var ticket in tickets)
{
    if (now < ticket.ShowStart)
    {
        var hours = (long)(ticket.ShowStart - now).TotalHours;
        total = total.Plus(hours >= 24 ? ticket.Price : ticket.Price.Percent(50));
    }
}
```

Test obejmuje granicę dokładnie 24h, 23h59m, bilet po starcie, listę i pustą listę.

### Krok 1: Extract Method na wspólnej regule

**Zmiany:**

1. W `Refund` wydziel wyliczenie kwoty za bilet do prywatnej statycznej metody `TicketShare(ticket, now)` (wyodrębnienie metody); gałęzie `if/else` zamieniają się we wczesne `return`.
2. W `RefundAll` zastąp ciało pętli (łącznie z inaczej zapisaną regułą i rzutowaniem na `long`) wywołaniem `total = total.Plus(TicketShare(ticket, now));`.

**Przed (`RefundService.cs`):**

```csharp
public Money Refund(TicketData ticket, DateTime now)
{
    Money amount;
    if (now >= ticket.ShowStart)
    {
        amount = Money.Zero;
    }
    else if ((ticket.ShowStart - now).TotalHours >= 24)
    {
        amount = ticket.Price;
    }
    else
    {
        amount = ticket.Price.Percent(50);
    }
    return amount.Minus(Fee).Max(Money.Zero);
}

public Money RefundAll(IReadOnlyList<TicketData> tickets, DateTime now)
{
    var total = Money.Zero;
    foreach (var ticket in tickets)
    {
        if (now < ticket.ShowStart)
        {
            var hours = (long)(ticket.ShowStart - now).TotalHours;
            total = total.Plus(hours >= 24 ? ticket.Price : ticket.Price.Percent(50));
        }
    }
    return total.Minus(Fee).Max(Money.Zero);
}
```

**Po (`RefundService.cs`):**

```csharp
public Money Refund(TicketData ticket, DateTime now)
{
    return TicketShare(ticket, now).Minus(Fee).Max(Money.Zero);
}

public Money RefundAll(IReadOnlyList<TicketData> tickets, DateTime now)
{
    var total = Money.Zero;
    foreach (var ticket in tickets)
    {
        total = total.Plus(TicketShare(ticket, now));
    }
    return total.Minus(Fee).Max(Money.Zero);
}

private static Money TicketShare(TicketData ticket, DateTime now)
{
    if (now >= ticket.ShowStart)
    {
        return Money.Zero;
    }
    if ((ticket.ShowStart - now).TotalHours >= 24)
    {
        return ticket.Price;
    }
    return ticket.Price.Percent(50);
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s12` - zielone.
**Co powiedzieć:** zanim wprowadzimy wzorzec, usuwamy duplikację reguły. Test granicy 24h potwierdza, że obie wersje były równoważne.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s12 0 1`

### Krok 2: Composite i jeden kontrakt

**Zmiany:**

1. Utwórz nowe pliki: `IRefundable.cs` (interfejs z metodą `RefundableAmount(now)`), `SingleTicket.cs` (liść z regułą przeniesioną z `TicketShare`) i `TicketGroup.cs` (węzeł sumujący elementy, ze statycznym `TicketGroup.Of(tickets)`).
2. W serwisie dodaj nową metodę `Refund(IRefundable refundable, DateTime now)` z potrąceniem `Fee`; `TicketShare` znika.
3. Stare metody `Refund(TicketData, ...)` i `RefundAll(...)` zostają jako jednolinijkowe delegacje do nowego kontraktu i dostają atrybut `[Obsolete]` z podpowiedzią.
4. Przeprowadź `CancellationDesk` na nowy kontrakt: na razie wciąż wybiera `SingleTicket` albo `TicketGroup.Of(tickets)` warunkiem po liczbie biletów.

**Przed:**

```csharp
public Money Refund(TicketData ticket, DateTime now)
{
    return TicketShare(ticket, now).Minus(Fee).Max(Money.Zero);
}

public Money RefundAll(IReadOnlyList<TicketData> tickets, DateTime now)
{
    var total = Money.Zero;
    foreach (var ticket in tickets)
    {
        total = total.Plus(TicketShare(ticket, now));
    }
    return total.Minus(Fee).Max(Money.Zero);
}
```

**Po (`IRefundable.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public interface IRefundable
{
    Money RefundableAmount(DateTime now);
}
```

**Po (`SingleTicket.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public sealed record SingleTicket(TicketData Ticket) : IRefundable
{
    public Money RefundableAmount(DateTime now)
    {
        if (now >= Ticket.ShowStart)
        {
            return Money.Zero;
        }
        if ((Ticket.ShowStart - now).TotalHours >= 24)
        {
            return Ticket.Price;
        }
        return Ticket.Price.Percent(50);
    }
}
```

**Po (`TicketGroup.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public sealed record TicketGroup : IRefundable
{
    public TicketGroup(IEnumerable<IRefundable> items)
    {
        Items = items.ToList().AsReadOnly();
    }

    public IReadOnlyList<IRefundable> Items { get; }

    public static TicketGroup Of(IEnumerable<TicketData> tickets)
    {
        return new TicketGroup(tickets.Select(ticket => (IRefundable)new SingleTicket(ticket)));
    }

    public Money RefundableAmount(DateTime now)
    {
        var total = Money.Zero;
        foreach (var item in Items)
        {
            total = total.Plus(item.RefundableAmount(now));
        }
        return total;
    }
}
```

**Po (`RefundService.cs`, cały plik):**

```csharp
using Training.Workshop.Shared;

public sealed class RefundService
{
    private static readonly Money Fee = Money.Of("3.00");

    public Money Refund(IRefundable refundable, DateTime now)
    {
        return refundable.RefundableAmount(now).Minus(Fee).Max(Money.Zero);
    }

    /// <summary>Przestarzałe: użyj <see cref="Refund(IRefundable, DateTime)"/> z <see cref="SingleTicket"/>.</summary>
    [Obsolete("użyj Refund(IRefundable, DateTime) z SingleTicket")]
    public Money Refund(TicketData ticket, DateTime now)
    {
        return Refund(new SingleTicket(ticket), now);
    }

    /// <summary>Przestarzałe: użyj <see cref="Refund(IRefundable, DateTime)"/> z <see cref="TicketGroup"/>.</summary>
    [Obsolete("użyj Refund(IRefundable, DateTime) z TicketGroup")]
    public Money RefundAll(IReadOnlyList<TicketData> tickets, DateTime now)
    {
        return Refund(TicketGroup.Of(tickets), now);
    }
}
```

**Po (`CancellationDesk.cs`):**

```csharp
public Money Refund(IReadOnlyList<TicketData> tickets, DateTime now)
{
    IRefundable refundable = tickets.Count == 1
        ? new SingleTicket(tickets[0])
        : TicketGroup.Of(tickets);
    return _service.Refund(refundable, now);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** potrącenie jest w jednym miejscu i naliczane raz na wywołanie - tak jak w obu starych metodach. `[Obsolete]` przy ostrzeżeniach traktowanych jak błędy działa jak twarda bramka: nikt nowy nie wywoła starego API.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s12 1 2`

### Krok 3: usunięcie starych metod

**Zmiany:**

1. Po migracji klientów usuń z serwisu przestarzałe metody `Refund(TicketData, ...)` i `RefundAll(...)` (bezpieczne usunięcie - nie mają już użyć).
2. W `CancellationDesk` usuń `if` po liczbie biletów - klient zawsze buduje `TicketGroup.Of(tickets)`, także z jednego biletu.

**Przed (`RefundService.cs` i `CancellationDesk.cs`):**

```csharp
[Obsolete("użyj Refund(IRefundable, DateTime) z SingleTicket")]
public Money Refund(TicketData ticket, DateTime now)
{
    return Refund(new SingleTicket(ticket), now);
}

[Obsolete("użyj Refund(IRefundable, DateTime) z TicketGroup")]
public Money RefundAll(IReadOnlyList<TicketData> tickets, DateTime now)
{
    return Refund(TicketGroup.Of(tickets), now);
}
```

```csharp
IRefundable refundable = tickets.Count == 1
    ? new SingleTicket(tickets[0])
    : TicketGroup.Of(tickets);
return _service.Refund(refundable, now);
```

**Po (`CancellationDesk.cs`):** w `RefundService` zostaje tylko `Refund(IRefundable, DateTime)`, a klient wygląda tak:

```csharp
public Money Refund(IReadOnlyList<TicketData> tickets, DateTime now)
{
    return _service.Refund(TicketGroup.Of(tickets), now);
}
```

**Uruchom:** test zielony; `S12SolutionTest` pokazuje zagnieżdżone grupy.
**Co powiedzieć:** rozróżnienie "jeden/wiele" zniknęło z API. Grupa jednego biletu daje ten sam wynik co bilet.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s12 2 3`

### Rozwiązanie i uzasadnienie

Jeden kontrakt `Refund(IRefundable)`, reguła biletu w liściu, suma w węźle. Stare API usunięte dopiero po etapie `[Obsolete]`.

### Pułapki

- Potrącenie przeniesione do liścia - wtedy lista biletów płaci 3.00 za każdy bilet (zmiana zachowania).
- Zmiana kolejności przetwarzania albo liczby transakcji przy przejściu na Composite.
- Pusta grupa - trzeba jawnie ustalić wynik (tu 0.00, jak w Start).

### Pytanie do sali

Co, jeśli biznes chce potrącenia za każdą rezerwację, a jedna grupa obejmuje kilka rezerwacji?

## Scena s13. Extract Composite - kontenery programu

**Temat ze slajdów:** 16.5. Jeden element lub wiele oraz wyodrębnienie Kompozytu (wyodrębnienie Kompozytu)
**Namespace:** `Training.Workshop.M5.S13ExtractComposite` · **Test:** `scripts/warsztat.sh --lang cs test m5/s13` (`S13EquivalenceTest`, `S13SolutionTest`)
**Czas:** ~8 min

### W skrócie

**Co robimy:** `Marathon` i `ShortsBlock` powielają obsługę dzieci - listę, `Add`, kopię `Children`, sumę minut i opis. Wydzielamy nadklasę `CompositeProgramItem` i podciągamy do niej to, co naprawdę wspólne, a regułę czasu zostawiamy w podklasach.

**Zasada:** Extract Composite wydziela wspólną nadklasę dla kilku kontenerów, które powielają zarządzanie dziećmi. Podobne pętle mogą znaczyć co innego, więc przed podciągnięciem porównujemy kontrakty, a nie tekst. Nowe reguły, np. wykrywanie cykli, to osobna zmiana zachowania.

**Efekt:** Lista dzieci, `Add` i szkielet opisu są w jednym miejscu, a podklasy mają po kilkanaście linii. `Minutes` zostaje w podklasach, bo tylko maraton dolicza przerwy - podciągnięcie go do bazy zmieniłoby wynik.

**Różnica względem Javy:** metody `final` z Javy to w C# zwykłe metody niewirtualne (w C# metody są domyślnie niewirtualne, więc podklasa i tak nie może ich nadpisać). `Objects.requireNonNull` to `ArgumentNullException.ThrowIfNull` (test oczekuje `ArgumentNullException`). `Minutes` jest abstrakcyjną właściwością bazy, a interfejs to `IProgramItem`.

### Co widzimy

`Marathon` i `ShortsBlock` powielają obsługę dzieci: lista, `Add` z kontrolą `null`, `Children` jako kopia, suma minut, opis. Różnią się tylko regułą czasu: maraton dodaje 15 minut przerwy między pozycjami.

```csharp
return _children.Count == 0 ? 0 : total + 15 * (_children.Count - 1);
```

### Krok 1: Extract Superclass

**Zmiany:**

1. Utwórz nowy plik `CompositeProgramItem.cs` i wydziel z `Marathon` nadklasę (wyodrębnienie nadklasy): przenieś do niej pole `_children`, metodę `Add` (z `ArgumentNullException.ThrowIfNull`) i właściwość `Children` (kopia listy); `Minutes` i `Describe()` zostają w bazie jako abstrakcyjne.
2. `Marathon : CompositeProgramItem` korzysta z `Children` bazy; `Minutes` i `Describe()` dostają `override`.
3. Zmień `ShortsBlock` na `ShortsBlock : CompositeProgramItem` i usuń jego kopie pola `_children`, `Add` i `Children` - są już w bazie.

**Przed (`Marathon.cs`, cały plik; `ShortsBlock.cs` analogicznie - bez przerw w `Minutes` i z etykietą "Blok"):**

```csharp
public sealed class Marathon : IProgramItem
{
    private readonly string _name;
    private readonly List<IProgramItem> _children = [];

    public Marathon(string name)
    {
        _name = name;
    }

    public void Add(IProgramItem child)
    {
        ArgumentNullException.ThrowIfNull(child);
        _children.Add(child);
    }

    public IReadOnlyList<IProgramItem> Children => _children.ToList().AsReadOnly();

    public int Minutes
    {
        get
        {
            var total = 0;
            foreach (var child in _children)
            {
                total += child.Minutes;
            }
            return _children.Count == 0 ? 0 : total + 15 * (_children.Count - 1);
        }
    }

    public string Describe()
    {
        return "Maraton " + _name + " (" + Minutes + " min) "
            + "[" + string.Join(", ", _children.Select(child => child.Describe())) + "]";
    }
}
```

**Po (`CompositeProgramItem.cs`, nowy plik):**

```csharp
public abstract class CompositeProgramItem : IProgramItem
{
    private readonly List<IProgramItem> _children = [];

    public void Add(IProgramItem child)
    {
        ArgumentNullException.ThrowIfNull(child);
        _children.Add(child);
    }

    public IReadOnlyList<IProgramItem> Children => _children.ToList().AsReadOnly();

    public abstract int Minutes { get; }

    public abstract string Describe();
}
```

**Po (`Marathon.cs`, cały plik):**

```csharp
public sealed class Marathon : CompositeProgramItem
{
    private readonly string _name;

    public Marathon(string name)
    {
        _name = name;
    }

    public override int Minutes
    {
        get
        {
            var children = Children;
            var total = 0;
            foreach (var child in children)
            {
                total += child.Minutes;
            }
            return children.Count == 0 ? 0 : total + 15 * (children.Count - 1);
        }
    }

    public override string Describe()
    {
        return "Maraton " + _name + " (" + Minutes + " min) "
            + "[" + string.Join(", ", Children.Select(child => child.Describe())) + "]";
    }
}
```

**Po (`ShortsBlock.cs`, cały plik):**

```csharp
public sealed class ShortsBlock : CompositeProgramItem
{
    private readonly string _name;

    public ShortsBlock(string name)
    {
        _name = name;
    }

    public override int Minutes
    {
        get
        {
            var total = 0;
            foreach (var child in Children)
            {
                total += child.Minutes;
            }
            return total;
        }
    }

    public override string Describe()
    {
        return "Blok " + _name + " (" + Minutes + " min) "
            + "[" + string.Join(", ", Children.Select(child => child.Describe())) + "]";
    }
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s13` - zielone.
**Co powiedzieć:** kontrakt dzieci (brak `null`, kopia listy) jest teraz w jednym miejscu dla wszystkich kontenerów.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s13 0 1`

### Krok 2: Pull Up sumy i opisu

**Zmiany:**

1. Podciągnij sumowanie czasu dzieci do bazy (przeniesienie metody do nadklasy) jako `protected int ChildrenMinutes()` - metoda niewirtualna, podklasy jej nie nadpisują.
2. `Describe()` w bazie zamień na niewirtualny szablon `Label() + " " + _name + " (" + Minutes + " min) [...]"` z hookiem `protected abstract string Label()`; podklasy zwracają w nim "Maraton" i "Blok".
3. Nazwę kontenera przenieś do konstruktora bazy (`protected CompositeProgramItem(string name)`). W podklasach zostają tylko reguła czasu (`Minutes`) i etykieta (`Label()`).

**Przed (`Marathon.cs`):**

```csharp
public override int Minutes
{
    get
    {
        var children = Children;
        var total = 0;
        foreach (var child in children)
        {
            total += child.Minutes;
        }
        return children.Count == 0 ? 0 : total + 15 * (children.Count - 1);
    }
}

public override string Describe()
{
    return "Maraton " + _name + " (" + Minutes + " min) "
        + "[" + string.Join(", ", Children.Select(child => child.Describe())) + "]";
}
```

**Po (`CompositeProgramItem.cs`, cały plik):**

```csharp
public abstract class CompositeProgramItem : IProgramItem
{
    private readonly string _name;
    private readonly List<IProgramItem> _children = [];

    protected CompositeProgramItem(string name)
    {
        _name = name;
    }

    public void Add(IProgramItem child)
    {
        ArgumentNullException.ThrowIfNull(child);
        _children.Add(child);
    }

    public IReadOnlyList<IProgramItem> Children => _children.ToList().AsReadOnly();

    public abstract int Minutes { get; }

    protected int ChildrenMinutes()
    {
        var total = 0;
        foreach (var child in _children)
        {
            total += child.Minutes;
        }
        return total;
    }

    public string Describe()
    {
        return Label() + " " + _name + " (" + Minutes + " min) "
            + "[" + string.Join(", ", _children.Select(child => child.Describe())) + "]";
    }

    protected abstract string Label();
}
```

**Po (`Marathon.cs`, cały plik):**

```csharp
public sealed class Marathon : CompositeProgramItem
{
    public Marathon(string name)
        : base(name)
    {
    }

    public override int Minutes
    {
        get
        {
            var count = Children.Count;
            return count == 0 ? 0 : ChildrenMinutes() + 15 * (count - 1);
        }
    }

    protected override string Label()
    {
        return "Maraton";
    }
}
```

**Po (`ShortsBlock.cs`, cały plik):**

```csharp
public sealed class ShortsBlock : CompositeProgramItem
{
    public ShortsBlock(string name)
        : base(name)
    {
    }

    public override int Minutes => ChildrenMinutes();

    protected override string Label()
    {
        return "Blok";
    }
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** podobne pętle mogą znaczyć co innego. Tu suma jest wspólna, ale przerwy nie - dlatego `Minutes` zostaje w podklasach.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s13 1 2`

### Rozwiązanie i uzasadnienie

`CompositeProgramItem` z pełną obsługą dzieci i szkieletem opisu; `Marathon` i `ShortsBlock` po kilkanaście linii.

### Pułapki

- Podciągnięcie całego `Minutes` do bazy - maraton traci przerwy.
- Wykrywanie cykli (maraton zawierający sam siebie) to osobna zmiana zachowania, nie część Extract Composite.

### Pytanie do sali

Trzeci kontener "blok z przerwą na reklamy co drugi film" - czy nadal pasuje do tej nadklasy?

## Scena s14. Unify Interfaces with Adapter - dwie bramki płatności

**Temat ze slajdów:** 9.1. Ujednolicenie interfejsów Adapterem; 9.2. Co adapter naprawdę musi przetłumaczyć
**Namespace:** `Training.Workshop.M5.S14Adapter` · **Test:** `scripts/warsztat.sh --lang cs test m5/s14` (`S14EquivalenceTest`, `S14SolutionTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `CheckoutService.Pay` mówi dwoma językami: dla starej bramki składa XML w groszach, dla nowej woła REST w złotych i łapie wyjątek odmowy. Wydzielamy obie gałęzie do metod o jednej sygnaturze, przenosimy je do adapterów interfejsu `IPaymentGateway`, a serwis wybiera bramkę ze słownika.

**Zasada:** Unify Interfaces with Adapter tłumaczy obcy interfejs na preferowany kontrakt klienta, tak żeby klient zależał tylko od niego. Adapter tłumaczy nazwy, jednostki, format i sposób zgłaszania błędów, ale nie udaje, że semantyka jest identyczna. Jeśli wystarczy Rename Method, adapter jest zbędny.

**Efekt:** Logika serwisu zna tylko `IPaymentGateway` i da się ją testować fake'iem, a trzeci dostawca to nowy adapter i wpis w słowniku. Stary konstruktor zostaje jako jedyne miejsce znające biblioteki bramek - docelowo do przeniesienia do korzenia kompozycji.

**Różnica względem Javy:** komponent rekordu `PaymentResult.accepted()` kolidowałby w C# z fabryką `Accepted(...)`, więc właściwość nazywa się `IsAccepted` (fabryki `Accepted`/`Declined` bez zmian). Metody "bibliotek" `XmlPayGateway.Submit` i `RestPayClient.Charge` są `virtual`, bo `S14SolutionTest` nadpisuje `Submit` w podklasie nagrywającej (w Javie metody są domyślnie wirtualne). `Map.copyOf` to kopia `Dictionary` wystawiona jako `IReadOnlyDictionary`, a grosze liczy `decimal.ToInt64(amount.Amount * 100)`.

### Co widzimy

`CheckoutService.Pay(provider, reservationId, amount)` mówi dwoma językami: dla starej bramki składa XML z kwotą w **groszach** i parsuje atrybuty odpowiedzi, dla nowej woła API REST w **złotych** i łapie wyjątek odmowy. "Biblioteki" bramek (`XmlPayGateway`, `RestPayClient`) są w namespace sceny i ich nie zmieniamy.

```csharp
var grosze = decimal.ToInt64(amount.Amount * 100);
var request = "<charge ref='" + reservationId + "' amount='" + grosze + "'/>";
```

### Krok 1: Extract Method z jednolitą sygnaturą

**Zmiany:**

1. Wydziel ciało gałęzi XML do prywatnej metody `PayWithXml(reservationId, amount)` (wyodrębnienie metody); budowę żądania można przy tym złożyć w jedno wywołanie `_xml.Submit(...)`.
2. Wydziel ciało gałęzi REST do `PayWithRest(reservationId, amount)`.
3. Obie metody mają identyczną sygnaturę i zwracają `PaymentResult`; pomocnicze `Attribute` zostaje na razie w serwisie.

**Przed:**

```csharp
if (provider == "XML")
{
    var grosze = decimal.ToInt64(amount.Amount * 100);
    var request = "<charge ref='" + reservationId + "' amount='" + grosze + "'/>";
    var response = _xml.Submit(request);
    if (response.Contains("status='OK'", StringComparison.Ordinal))
    {
        return PaymentResult.Accepted(Attribute(response, "id"));
    }
    return PaymentResult.Declined(Attribute(response, "code"));
}
else if (provider == "REST")
{
    try
    {
        return PaymentResult.Accepted(
            _rest.Charge(new RestPayClient.ChargeRequest(amount.Amount, "PLN", reservationId))
                .TransactionId);
    }
    catch (RestPayClient.RestPayException exception)
    {
        return PaymentResult.Declined(exception.Code);
    }
}
```

**Po (`CheckoutService.cs`):**

```csharp
public PaymentResult Pay(string provider, string reservationId, Money amount)
{
    if (provider == "XML")
    {
        return PayWithXml(reservationId, amount);
    }
    else if (provider == "REST")
    {
        return PayWithRest(reservationId, amount);
    }
    throw new ArgumentException("unknown provider: " + provider);
}

private PaymentResult PayWithXml(string reservationId, Money amount)
{
    var grosze = decimal.ToInt64(amount.Amount * 100);
    var response = _xml.Submit("<charge ref='" + reservationId + "' amount='" + grosze + "'/>");
    if (response.Contains("status='OK'", StringComparison.Ordinal))
    {
        return PaymentResult.Accepted(Attribute(response, "id"));
    }
    return PaymentResult.Declined(Attribute(response, "code"));
}

private PaymentResult PayWithRest(string reservationId, Money amount)
{
    try
    {
        return PaymentResult.Accepted(
            _rest.Charge(new RestPayClient.ChargeRequest(amount.Amount, "PLN", reservationId))
                .TransactionId);
    }
    catch (RestPayClient.RestPayException exception)
    {
        return PaymentResult.Declined(exception.Code);
    }
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s14` - zielone.
**Co powiedzieć:** identyczna sygnatura dwóch metod to gotowy kontrakt adaptera.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s14 0 1`

### Krok 2: interfejs i dwa adaptery

**Zmiany:**

1. Utwórz nowy plik `IPaymentGateway.cs`: interfejs z metodą `PaymentResult Pay(string reservationId, Money amount)`.
2. Przenieś `PayWithXml` do nowej klasy `XmlPayAdapter : IPaymentGateway` (razem z pomocniczym `Attribute`), a `PayWithRest` do `RestPayAdapter : IPaymentGateway` (przeniesienie metody); każdy adapter dostaje swoją bramkę w konstruktorze.
3. W serwisie zamień typy pól na `IPaymentGateway`; konstruktor bez zmiany sygnatury tworzy adaptery, a gałęzie `if` wołają `_xml.Pay(...)` i `_rest.Pay(...)`.

**Przed:**

```csharp
private PaymentResult PayWithXml(string reservationId, Money amount)
{
    var grosze = decimal.ToInt64(amount.Amount * 100);
    var response = _xml.Submit("<charge ref='" + reservationId + "' amount='" + grosze + "'/>");
    if (response.Contains("status='OK'", StringComparison.Ordinal))
    {
        return PaymentResult.Accepted(Attribute(response, "id"));
    }
    return PaymentResult.Declined(Attribute(response, "code"));
}
```

**Po (`IPaymentGateway.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

/// <summary>Preferowany interfejs kina - kwota jako Money, odmowa jako wynik, nie wyjątek.</summary>
public interface IPaymentGateway
{
    PaymentResult Pay(string reservationId, Money amount);
}
```

**Po (`XmlPayAdapter.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public sealed class XmlPayAdapter : IPaymentGateway
{
    private readonly XmlPayGateway _xml;

    public XmlPayAdapter(XmlPayGateway xml)
    {
        _xml = xml;
    }

    public PaymentResult Pay(string reservationId, Money amount)
    {
        var grosze = decimal.ToInt64(amount.Amount * 100);
        var response = _xml.Submit("<charge ref='" + reservationId + "' amount='" + grosze + "'/>");
        if (response.Contains("status='OK'", StringComparison.Ordinal))
        {
            return PaymentResult.Accepted(Attribute(response, "id"));
        }
        return PaymentResult.Declined(Attribute(response, "code"));
    }

    private static string Attribute(string xml, string name)
    {
        var start = xml.IndexOf(name + "='", StringComparison.Ordinal) + name.Length + 2;
        return xml[start..xml.IndexOf('\'', start)];
    }
}
```

**Po (`RestPayAdapter.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public sealed class RestPayAdapter : IPaymentGateway
{
    private readonly RestPayClient _rest;

    public RestPayAdapter(RestPayClient rest)
    {
        _rest = rest;
    }

    public PaymentResult Pay(string reservationId, Money amount)
    {
        try
        {
            return PaymentResult.Accepted(
                _rest.Charge(new RestPayClient.ChargeRequest(amount.Amount, "PLN", reservationId))
                    .TransactionId);
        }
        catch (RestPayClient.RestPayException exception)
        {
            return PaymentResult.Declined(exception.Code);
        }
    }
}
```

**Po (`CheckoutService.cs`, cały plik):** metody `PayWithXml`, `PayWithRest` i `Attribute` znikają z serwisu:

```csharp
using Training.Workshop.Shared;

public sealed class CheckoutService
{
    private readonly IPaymentGateway _xml;
    private readonly IPaymentGateway _rest;

    public CheckoutService(XmlPayGateway xml, RestPayClient rest)
    {
        _xml = new XmlPayAdapter(xml);
        _rest = new RestPayAdapter(rest);
    }

    public PaymentResult Pay(string provider, string reservationId, Money amount)
    {
        if (provider == "XML")
        {
            return _xml.Pay(reservationId, amount);
        }
        else if (provider == "REST")
        {
            return _rest.Pay(reservationId, amount);
        }
        throw new ArgumentException("unknown provider: " + provider);
    }
}
```

**Uruchom:** test zielony; `S14SolutionTest.XmlAdapterSendsAmountInGrosze` sprawdza, że do starej bramki idzie `amount='4000'`.
**Co powiedzieć:** adapter tłumaczy jednostki (złote - grosze), format i sposób zgłaszania odmowy (atrybut kontra wyjątek). Nie udaje, że semantyka jest taka sama.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s14 1 2`

### Krok 3: serwis zależy tylko od interfejsu

**Zmiany:**

1. Dodaj konstruktor `CheckoutService(IReadOnlyDictionary<string, IPaymentGateway> gateways)`, który robi kopię do nowego `Dictionary`; dwa pola adapterów zastąp jednym polem słownika.
2. Stary konstruktor `CheckoutService(XmlPayGateway, RestPayClient)` zamień na delegację `: this(...)` ze standardowym zestawem adapterów pod kluczami "XML" i "REST".
3. W `Pay` zastąp `if/else if` wyszukaniem w słowniku (`TryGetValue`); nieznany dostawca rzuca ten sam `ArgumentException` co wcześniej.

**Przed:**

```csharp
public CheckoutService(XmlPayGateway xml, RestPayClient rest)
{
    _xml = new XmlPayAdapter(xml);
    _rest = new RestPayAdapter(rest);
}

public PaymentResult Pay(string provider, string reservationId, Money amount)
{
    if (provider == "XML")
    {
        return _xml.Pay(reservationId, amount);
    }
    else if (provider == "REST")
    {
        return _rest.Pay(reservationId, amount);
    }
    throw new ArgumentException("unknown provider: " + provider);
}
```

**Po (`CheckoutService.cs`, cały plik):**

```csharp
using Training.Workshop.Shared;

public sealed class CheckoutService
{
    private readonly IReadOnlyDictionary<string, IPaymentGateway> _gateways;

    /// <summary>Dotychczasowy konstruktor jako skrót: standardowy zestaw dwóch adapterów.</summary>
    public CheckoutService(XmlPayGateway xml, RestPayClient rest)
        : this(new Dictionary<string, IPaymentGateway>
        {
            ["XML"] = new XmlPayAdapter(xml),
            ["REST"] = new RestPayAdapter(rest),
        })
    {
    }

    public CheckoutService(IReadOnlyDictionary<string, IPaymentGateway> gateways)
    {
        _gateways = new Dictionary<string, IPaymentGateway>(gateways);
    }

    public PaymentResult Pay(string provider, string reservationId, Money amount)
    {
        if (!_gateways.TryGetValue(provider, out var gateway))
        {
            throw new ArgumentException("unknown provider: " + provider);
        }
        return gateway.Pay(reservationId, amount);
    }
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** logika serwisu zna tylko `IPaymentGateway` i da się ją testować fake'iem (`CheckoutWorksWithAnyGateway`). Stary konstruktor to jedyne miejsce, które jeszcze zna biblioteki bramek - docelowo przeniesiemy go do korzenia kompozycji. Trzeci dostawca to nowy adapter i wpis w słowniku.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s14 2 3`

### Rozwiązanie i uzasadnienie

Preferowany kontrakt kina (`IPaymentGateway`, `PaymentResult`) i dwa adaptery. Granica 500.00 włącznie działa tak samo w obu bramkach - test to sprawdza.

### Pułapki

- Podobna sygnatura, inny kontrakt: jednostki, zaokrąglenia, strefa czasowa, kodowanie znaków.
- Mapowanie wyjątku bez zachowania przyczyny (`InnerException`) - trudna diagnoza w produkcji.
- Adapter wołający `Dispose()` na zasobie, którego nie jest właścicielem.
- Adapter tam, gdzie wystarczyłby Rename Method.

### Pytanie do sali

Stara bramka zwraca `status='ERROR'` przy błędnym XML. Jak to zamapować - na odmowę czy wyjątek? Kto decyduje?

## Scena s15. Replace Conditional Dispatcher with Command - konsola kasjera

**Temat ze slajdów:** 6.6. Polecenie: łańcuch warunków zamieniony na rejestr poleceń
**Namespace:** `Training.Workshop.M5.S15Command` · **Test:** `scripts/warsztat.sh --lang cs test m5/s15` (`S15EquivalenceTest`, `S15SolutionTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `CashierConsole.Handle` to łańcuch `if` po nazwie komendy z pełną logiką i zmianą stanu kasy w każdej gałęzi. Wydzielamy gałęzie do metod o wspólnej sygnaturze, zamieniamy je na bezstanowe komendy działające na `Till`, a łańcuch `if` zastępujemy słownikiem komend.

**Zasada:** Replace Conditional Dispatcher with Command zamienia każdą gałąź dyspozytora w obiekt komendy, a wybór gałęzi w wyszukanie w rejestrze. Słownik jest równoważny warunkom tylko wtedy, gdy klucze są rozłączne i zachowana jest normalizacja. Command nie daje automatycznie asynchroniczności, retry ani undo.

**Efekt:** Dodanie komendy to nowa klasa i wpis w słowniku, bez zmiany `Handle`. Komendy nie trzymają danych żądania, więc można je bezpiecznie współdzielić, a `sell` i `SELL` nadal działają tak samo.

**Różnica względem Javy:** `@FunctionalInterface ConsoleCommand` to interfejs `IConsoleCommand` (nowa komenda HELLO w `S15SolutionTest` to prywatna klasa `HelloCommand` zamiast lambdy). `toUpperCase(Locale.ROOT)` to `ToUpperInvariant()`, `strip()` to `Trim()`, a `matches("\\d+")` to `Regex.IsMatch(..., "^[0-9]+$")` (tylko cyfry ASCII, jak w Javie). Metody `Till` package-private są `internal` (`Cash` i `Tickets` jako właściwości).

### Co widzimy

`CashierConsole.Handle(line)` to łańcuch `if (command == "SELL")` z pełną logiką i modyfikacją stanu kasy w każdej gałęzi. Nazwa komendy jest normalizowana `ToUpperInvariant()` - `sell` i `SELL` działają tak samo.

```csharp
if (command == "SELL")
{
    var sell = args.Split(' ', 2);
    ...
}
else if (command == "REFUND")
{
    ...
}
else if (command == "REPORT")
```

Test to sesja poleceń: sprzedaż, zwrot, raport oraz błędne polecenia, które nie zmieniają stanu.

### Krok 1: Extract Method dla gałęzi

**Zmiany:**

1. Wydziel ciało każdej gałęzi do prywatnej metody (wyodrębnienie metody): `Sell(args)`, `Refund(args)`, `Report(args)`.
2. Ujednolić sygnatury: wszystkie trzy przyjmują `string args` i zwracają `string`, także `Report`, które z `args` nie korzysta.

**Przed (`CashierConsole.cs`):**

```csharp
public string Handle(string line)
{
    var parts = line.Trim().Split(' ', 2);
    var command = parts[0].ToUpperInvariant();
    var args = parts.Length > 1 ? parts[1] : "";
    if (command == "SELL")
    {
        var sell = args.Split(' ', 2);
        if (sell.Length < 2 || !Regex.IsMatch(sell[0], "^[0-9]+$"))
        {
            return "Blad: SELL <liczba> <tytul>";
        }
        var quantity = int.Parse(sell[0], CultureInfo.InvariantCulture);
        if (!Prices.TryGetValue(sell[1], out var price))
        {
            return "Blad: nieznany film " + sell[1];
        }
        var total = price.Times(quantity);
        _cash = _cash.Plus(total);
        _tickets += quantity;
        return "Sprzedano " + quantity + " x " + sell[1] + " = " + total;
    }
    else if (command == "REFUND")
    {
        if (!Prices.TryGetValue(args, out var price))
        {
            return "Blad: nieznany film " + args;
        }
        if (_tickets == 0)
        {
            return "Blad: brak biletow do zwrotu";
        }
        _cash = _cash.Minus(price);
        _tickets--;
        return "Zwrot 1 x " + args + " = " + price;
    }
    else if (command == "REPORT")
    {
        return "Kasa: " + _cash + ", biletow: " + _tickets;
    }
    return "Nieznana komenda: " + parts[0];
}
```

**Po (`CashierConsole.cs`):**

```csharp
public string Handle(string line)
{
    var parts = line.Trim().Split(' ', 2);
    var command = parts[0].ToUpperInvariant();
    var args = parts.Length > 1 ? parts[1] : "";
    if (command == "SELL")
    {
        return Sell(args);
    }
    else if (command == "REFUND")
    {
        return Refund(args);
    }
    else if (command == "REPORT")
    {
        return Report(args);
    }
    return "Nieznana komenda: " + parts[0];
}

private string Sell(string args)
{
    var sell = args.Split(' ', 2);
    if (sell.Length < 2 || !Regex.IsMatch(sell[0], "^[0-9]+$"))
    {
        return "Blad: SELL <liczba> <tytul>";
    }
    var quantity = int.Parse(sell[0], CultureInfo.InvariantCulture);
    if (!Prices.TryGetValue(sell[1], out var price))
    {
        return "Blad: nieznany film " + sell[1];
    }
    var total = price.Times(quantity);
    _cash = _cash.Plus(total);
    _tickets += quantity;
    return "Sprzedano " + quantity + " x " + sell[1] + " = " + total;
}

private string Refund(string args)
{
    if (!Prices.TryGetValue(args, out var price))
    {
        return "Blad: nieznany film " + args;
    }
    if (_tickets == 0)
    {
        return "Blad: brak biletow do zwrotu";
    }
    _cash = _cash.Minus(price);
    _tickets--;
    return "Zwrot 1 x " + args + " = " + price;
}

private string Report(string args)
{
    return "Kasa: " + _cash + ", biletow: " + _tickets;
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s15` - zielone.
**Co powiedzieć:** jednolita sygnatura przygotowuje interfejs komendy.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s15 0 1`

### Krok 2: komendy jako obiekty, stan w Till

**Zmiany:**

1. Utwórz nowy plik `Till.cs` i przenieś do niego stan konsoli (wyodrębnienie klasy): pola `_cash` i `_tickets` jako właściwości `Cash` i `Tickets` (`internal`, prywatny setter), cennik `Prices` oraz metody `PriceOf(title)`, `Sold(quantity, total)`, `Refunded(price)`.
2. Utwórz nowy plik `IConsoleCommand.cs`: interfejs z metodą `string Execute(string args, Till till)`.
3. Przenieś ciała metod `Sell`, `Refund`, `Report` do nowych klas `SellCommand`, `RefundCommand`, `ReportCommand` implementujących interfejs; mutacje stanu idą przez metody `Till`.
4. W konsoli zostają pole `_till`, trzy pola komend i dyspozytor `if` - zmieniamy jedną rzecz naraz.

**Przed:**

```csharp
private Money _cash = Money.Zero;
private int _tickets;
...
if (command == "SELL")
{
    return Sell(args);
}
else if (command == "REFUND")
{
    return Refund(args);
}
else if (command == "REPORT")
{
    return Report(args);
}
```

**Po (`Till.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public sealed class Till
{
    private static readonly IReadOnlyDictionary<string, Money> Prices = new Dictionary<string, Money>
    {
        ["Diuna"] = Money.Of("40.00"),
        ["Kraina Lodu"] = Money.Of("32.00"),
        ["Amator"] = Money.Of("25.00"),
    };

    internal Money Cash { get; private set; } = Money.Zero;

    internal int Tickets { get; private set; }

    internal Money? PriceOf(string title)
    {
        return Prices.GetValueOrDefault(title);
    }

    internal void Sold(int quantity, Money total)
    {
        Cash = Cash.Plus(total);
        Tickets += quantity;
    }

    internal void Refunded(Money price)
    {
        Cash = Cash.Minus(price);
        Tickets--;
    }
}
```

**Po (`IConsoleCommand.cs`, nowy plik):**

```csharp
public interface IConsoleCommand
{
    string Execute(string args, Till till);
}
```

**Po (`SellCommand.cs`, nowy plik):**

```csharp
using System.Globalization;
using System.Text.RegularExpressions;

public sealed class SellCommand : IConsoleCommand
{
    public string Execute(string args, Till till)
    {
        var sell = args.Split(' ', 2);
        if (sell.Length < 2 || !Regex.IsMatch(sell[0], "^[0-9]+$"))
        {
            return "Blad: SELL <liczba> <tytul>";
        }
        var quantity = int.Parse(sell[0], CultureInfo.InvariantCulture);
        var price = till.PriceOf(sell[1]);
        if (price == null)
        {
            return "Blad: nieznany film " + sell[1];
        }
        var total = price.Times(quantity);
        till.Sold(quantity, total);
        return "Sprzedano " + quantity + " x " + sell[1] + " = " + total;
    }
}
```

**Po (`RefundCommand.cs`, nowy plik):**

```csharp
public sealed class RefundCommand : IConsoleCommand
{
    public string Execute(string args, Till till)
    {
        var price = till.PriceOf(args);
        if (price == null)
        {
            return "Blad: nieznany film " + args;
        }
        if (till.Tickets == 0)
        {
            return "Blad: brak biletow do zwrotu";
        }
        till.Refunded(price);
        return "Zwrot 1 x " + args + " = " + price;
    }
}
```

**Po (`ReportCommand.cs`, nowy plik):**

```csharp
public sealed class ReportCommand : IConsoleCommand
{
    public string Execute(string args, Till till)
    {
        return "Kasa: " + till.Cash + ", biletow: " + till.Tickets;
    }
}
```

**Po (`CashierConsole.cs`, cały plik):**

```csharp
public sealed class CashierConsole
{
    private readonly Till _till = new();
    private readonly IConsoleCommand _sell = new SellCommand();
    private readonly IConsoleCommand _refund = new RefundCommand();
    private readonly IConsoleCommand _report = new ReportCommand();

    public string Handle(string line)
    {
        var parts = line.Trim().Split(' ', 2);
        var command = parts[0].ToUpperInvariant();
        var args = parts.Length > 1 ? parts[1] : "";
        if (command == "SELL")
        {
            return _sell.Execute(args, _till);
        }
        else if (command == "REFUND")
        {
            return _refund.Execute(args, _till);
        }
        else if (command == "REPORT")
        {
            return _report.Execute(args, _till);
        }
        return "Nieznana komenda: " + parts[0];
    }
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** komendy są bezstanowe - dane żądania to argument, stan kasy to argument. Współdzielona komenda ze stanem poprzedniego żądania to błąd czekający na drugi wątek.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s15 1 2`

### Krok 3: rejestr zamiast warunku

**Zmiany:**

1. Zamień trzy pola komend na pole `IReadOnlyDictionary<string, IConsoleCommand> _commands`.
2. Dodaj konstruktor wstrzykiwany `CashierConsole(IReadOnlyDictionary<string, IConsoleCommand> commands)` (kopia do nowego `Dictionary`); konstruktor bezargumentowy deleguje do niego przez `: this(...)` ze słownikiem "SELL"/"REFUND"/"REPORT".
3. W `Handle` zastąp łańcuch `if` wyszukaniem komendy po znormalizowanym kluczu (`parts[0].ToUpperInvariant()`, `TryGetValue`); brak klucza zwraca ten sam komunikat "Nieznana komenda".

**Przed:**

```csharp
private readonly IConsoleCommand _sell = new SellCommand();
private readonly IConsoleCommand _refund = new RefundCommand();
private readonly IConsoleCommand _report = new ReportCommand();
...
if (command == "SELL")
{
    return _sell.Execute(args, _till);
}
else if (command == "REFUND")
{
    return _refund.Execute(args, _till);
}
```

**Po (`CashierConsole.cs`, cały plik):**

```csharp
public sealed class CashierConsole
{
    private readonly Till _till = new();
    private readonly IReadOnlyDictionary<string, IConsoleCommand> _commands;

    public CashierConsole()
        : this(new Dictionary<string, IConsoleCommand>
        {
            ["SELL"] = new SellCommand(),
            ["REFUND"] = new RefundCommand(),
            ["REPORT"] = new ReportCommand(),
        })
    {
    }

    public CashierConsole(IReadOnlyDictionary<string, IConsoleCommand> commands)
    {
        _commands = new Dictionary<string, IConsoleCommand>(commands);
    }

    public string Handle(string line)
    {
        var parts = line.Trim().Split(' ', 2);
        if (!_commands.TryGetValue(parts[0].ToUpperInvariant(), out var command))
        {
            return "Nieznana komenda: " + parts[0];
        }
        return command.Execute(parts.Length > 1 ? parts[1] : "", _till);
    }
}
```

**Uruchom:** test zielony; `S15SolutionTest` dodaje komendę bez zmiany dyspozytora.
**Co powiedzieć:** słownik jest równoważny warunkom, bo klucze są **rozłączne** i normalizacja została zachowana. Przy nakładających się predykatach (`StartsWith`) kolejność `if` byłaby częścią kontraktu i słownik by go zgubił.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s15 2 3`

### Rozwiązanie i uzasadnienie

Dyspozytor bez wiedzy o komendach, komendy bezstanowe, stan w `Till`. Nowa komenda to nowa klasa i wpis w słowniku.

### Pułapki

- Zgubiona normalizacja klucza - `sell 1 Diuna` nagle "nieznana komenda" (albo `ToUpper()` zależne od kultury, np. tureckie "i").
- Command traktowany jako darmowa asynchroniczność, retry albo undo - to osobne decyzje (idempotencja, kompensacja).
- Brak sprawdzenia kompletności rejestru przy starcie aplikacji.

### Pytanie do sali

Jak dodalibyście `UNDO` dla ostatniej sprzedaży? Czego wymaga to od komend?

## Scena s16. Form Template Method - raporty CSV i HTML

**Temat ze slajdów:** 7.1. Utworzenie Metody szablonowej: wspólny szkielet, różne kroki
**Namespace:** `Training.Workshop.M5.S16TemplateMethod` · **Test:** `scripts/warsztat.sh --lang cs test m5/s16` (`S16EquivalenceTest`, `S16SolutionTest`)
**Czas:** ~8 min

### W skrócie

**Co robimy:** `CsvSalesReport` i `HtmlSalesReport` mają ten sam szkielet (sortowanie, nagłówek, wiersze z sumą, stopka), ale napisany trochę inaczej. Wydzielamy różnice do metod `Header`, `Row`, `Footer`, doprowadzamy `Render` do identycznej postaci i podciągamy go do nadklasy `SalesReport` jako metodę niewirtualną.

**Zasada:** Form Template Method umieszcza wspólną sekwencję kroków w metodzie bazowej, a zmienne kroki w metodach nadpisywanych przez podklasy. Stosuje się ją, gdy kolejność kroków jest stała i różnią się tylko szczegóły. Przy wielu hookach albo zmiennej kolejności lepsza jest kompozycja lub Strategy.

**Efekt:** Sortowanie i sumowanie są w jednym miejscu, a nowy format (np. Markdown) to trzy metody. Niewirtualny `Render` chroni kolejność kroków, ale w opublikowanej bibliotece odebranie `virtual` nadpisywanej metodzie złamałoby klientów.

**Różnica względem Javy:** Java dodaje `final` do `render`. W C# metody są domyślnie niewirtualne, więc `Render` w bazie po prostu nie jest `virtual` - to odpowiednik `final`, a `S16SolutionTest.TemplateMethodIsFinal` sprawdza `MethodInfo.IsVirtual == false`. Sortowanie idzie przez `OrderBy` (stabilne, jak `List.sort` w Javie; `List<T>.Sort` w .NET nie jest stabilne), godzina to `TimeOnly.ToString("HH:mm", CultureInfo.InvariantCulture)`. W kroku 1 `Header`, `Row`, `Footer` są prywatne statyczne, a nowy format Markdown w teście to prywatna podklasa zamiast klasy anonimowej.

### Co widzimy

`CsvSalesReport` i `HtmlSalesReport` mają ten sam szkielet: sortowanie po godzinie, nagłówek, wiersze z sumowaniem, stopka. Napisane trochę inaczej (`OrderBy(...).ToList()` kontra `foreach` wprost po `OrderBy`, inne nazwy zmiennych), więc duplikacja nie rzuca się w oczy. Różnice to formatowanie i escapowanie (`;` w CSV, `&` w HTML).

### Krok 1: Extract Method na różnicach

**Zmiany:**

1. W obu klasach wydziel różniące się fragmenty do prywatnych statycznych metod o tych samych nazwach i sygnaturach (wyodrębnienie metody): `Header()`, `Row(Sale sale)`, `Footer(int tickets, Money total)`.
2. Przepisz `Render` raportu HTML na dokładnie ten sam kształt co w CSV: te same nazwy zmiennych (`sorted`, `text`, `tickets`, `total`), sortowanie `OrderBy(...).ToList()` i ta sama pętla.
3. Sprawdź, że `Render` obu klas jest identyczny znak w znak.

**Przed (`CsvSalesReport.cs`):**

```csharp
public string Render(IReadOnlyList<Sale> sales)
{
    var sorted = sales.OrderBy(sale => sale.Time).ToList();
    var csv = new StringBuilder("godzina;film;bilety;kwota\n");
    var tickets = 0;
    var total = Money.Zero;
    foreach (var sale in sorted)
    {
        var title = sale.Title.Contains(';') ? "\"" + sale.Title + "\"" : sale.Title;
        csv.Append(sale.Time.ToString("HH:mm", CultureInfo.InvariantCulture)).Append(';').Append(title).Append(';')
            .Append(sale.Tickets).Append(';').Append(sale.Amount).Append('\n');
        tickets += sale.Tickets;
        total = total.Plus(sale.Amount);
    }
    csv.Append("SUMA;;").Append(tickets).Append(';').Append(total).Append('\n');
    return csv.ToString();
}
```

**Przed (`HtmlSalesReport.cs`):**

```csharp
public string Render(IReadOnlyList<Sale> sales)
{
    var html = new StringBuilder();
    html.Append("<table>\n<tr><th>Godzina</th><th>Film</th><th>Bilety</th><th>Kwota</th></tr>\n");
    var sum = Money.Zero;
    var count = 0;
    foreach (var sale in sales.OrderBy(sale => sale.Time))
    {
        var title = sale.Title.Replace("&", "&amp;").Replace("<", "&lt;");
        html.Append("<tr><td>").Append(sale.Time.ToString("HH:mm", CultureInfo.InvariantCulture))
            .Append("</td><td>").Append(title)
            .Append("</td><td>").Append(sale.Tickets).Append("</td><td>").Append(sale.Amount)
            .Append("</td></tr>\n");
        sum = sum.Plus(sale.Amount);
        count += sale.Tickets;
    }
    html.Append("<tr><td colspan=\"2\">Suma</td><td>").Append(count).Append("</td><td>").Append(sum)
        .Append("</td></tr>\n</table>\n");
    return html.ToString();
}
```

**Po (`CsvSalesReport.cs`):**

```csharp
public string Render(IReadOnlyList<Sale> sales)
{
    var sorted = sales.OrderBy(sale => sale.Time).ToList();
    var text = new StringBuilder(Header());
    var tickets = 0;
    var total = Money.Zero;
    foreach (var sale in sorted)
    {
        text.Append(Row(sale));
        tickets += sale.Tickets;
        total = total.Plus(sale.Amount);
    }
    return text.Append(Footer(tickets, total)).ToString();
}

private static string Header()
{
    return "godzina;film;bilety;kwota\n";
}

private static string Row(Sale sale)
{
    var title = sale.Title.Contains(';') ? "\"" + sale.Title + "\"" : sale.Title;
    return sale.Time.ToString("HH:mm", CultureInfo.InvariantCulture) + ";" + title + ";" + sale.Tickets + ";" + sale.Amount + "\n";
}

private static string Footer(int tickets, Money total)
{
    return "SUMA;;" + tickets + ";" + total + "\n";
}
```

**Po (`HtmlSalesReport.cs`):** `Render` identyczny znak w znak jak w CSV powyżej, a trzy metody formatujące to:

```csharp
private static string Header()
{
    return "<table>\n<tr><th>Godzina</th><th>Film</th><th>Bilety</th><th>Kwota</th></tr>\n";
}

private static string Row(Sale sale)
{
    var title = sale.Title.Replace("&", "&amp;").Replace("<", "&lt;");
    return "<tr><td>" + sale.Time.ToString("HH:mm", CultureInfo.InvariantCulture) + "</td><td>" + title + "</td><td>" + sale.Tickets
        + "</td><td>" + sale.Amount + "</td></tr>\n";
}

private static string Footer(int tickets, Money total)
{
    return "<tr><td colspan=\"2\">Suma</td><td>" + tickets + "</td><td>" + total + "</td></tr>\n"
        + "</table>\n";
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s16` - zielone.
**Co powiedzieć:** ujednolicamy sygnatury po kroku, aż `Render` obu klas będzie identyczny znak w znak.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s16 0 1`

### Krok 2: Extract Superclass + Pull Up Render

**Zmiany:**

1. Utwórz nowy plik `SalesReport.cs` i wydziel z raportu CSV nadklasę `abstract class SalesReport` (wyodrębnienie nadklasy): przenieś do niej `Render`, a `Header`, `Row`, `Footer` zadeklaruj jako `protected abstract`.
2. W `CsvSalesReport : SalesReport` i `HtmlSalesReport : SalesReport` usuń kopie `Render`, a `Header/Row/Footer` zamień z prywatnych statycznych na `protected override`.
3. `Render` w bazie zostaw bez `virtual` - podklasy nie mogą go nadpisać (odpowiednik `final` z Javy).

**Przed:** obie klasy w wersji z kroku 1 - identyczny `Render` plus prywatne statyczne `Header`, `Row`, `Footer`.

**Po (`SalesReport.cs`, nowy plik):**

```csharp
using System.Text;
using Training.Workshop.Shared;

public abstract class SalesReport
{
    public string Render(IReadOnlyList<Sale> sales)
    {
        var sorted = sales.OrderBy(sale => sale.Time).ToList();
        var text = new StringBuilder(Header());
        var tickets = 0;
        var total = Money.Zero;
        foreach (var sale in sorted)
        {
            text.Append(Row(sale));
            tickets += sale.Tickets;
            total = total.Plus(sale.Amount);
        }
        return text.Append(Footer(tickets, total)).ToString();
    }

    protected abstract string Header();

    protected abstract string Row(Sale sale);

    protected abstract string Footer(int tickets, Money total);
}
```

**Po (`CsvSalesReport.cs`, cały plik):**

```csharp
using System.Globalization;
using Training.Workshop.Shared;

public sealed class CsvSalesReport : SalesReport
{
    protected override string Header()
    {
        return "godzina;film;bilety;kwota\n";
    }

    protected override string Row(Sale sale)
    {
        var title = sale.Title.Contains(';') ? "\"" + sale.Title + "\"" : sale.Title;
        return sale.Time.ToString("HH:mm", CultureInfo.InvariantCulture) + ";" + title + ";" + sale.Tickets + ";" + sale.Amount + "\n";
    }

    protected override string Footer(int tickets, Money total)
    {
        return "SUMA;;" + tickets + ";" + total + "\n";
    }
}
```

**Po (`HtmlSalesReport.cs`, cały plik):**

```csharp
using System.Globalization;
using Training.Workshop.Shared;

public sealed class HtmlSalesReport : SalesReport
{
    protected override string Header()
    {
        return "<table>\n<tr><th>Godzina</th><th>Film</th><th>Bilety</th><th>Kwota</th></tr>\n";
    }

    protected override string Row(Sale sale)
    {
        var title = sale.Title.Replace("&", "&amp;").Replace("<", "&lt;");
        return "<tr><td>" + sale.Time.ToString("HH:mm", CultureInfo.InvariantCulture) + "</td><td>" + title + "</td><td>" + sale.Tickets
            + "</td><td>" + sale.Amount + "</td></tr>\n";
    }

    protected override string Footer(int tickets, Money total)
    {
        return "<tr><td colspan=\"2\">Suma</td><td>" + tickets + "</td><td>" + total + "</td></tr>\n"
            + "</table>\n";
    }
}
```

**Uruchom:** test zielony; `S16SolutionTest` sprawdza, że `Render` nie jest wirtualny, i dodaje format Markdown trzema metodami.
**Co powiedzieć:** niewirtualny szablon chroni kolejność kroków. W opublikowanej bibliotece odebranie `virtual` (albo `sealed override`) nadpisywanej metodzie łamie klientów.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s16 1 2`

### Rozwiązanie i uzasadnienie

Szkielet raz, w bazie; podklasy to czyste formatowanie. Pusta sprzedaż daje nagłówek i sumę 0.00 w obu formatach.

### Pułapki

- Hook opcjonalny (`virtual`) z domyślną implementacją, która nie pasuje do wszystkich podklas.
- Wywołanie hooka z konstruktora bazy - podklasa widzi pola, których jej konstruktor jeszcze nie ustawił.
- Wiele hooków i zmienna kolejność - wtedy lepsza kompozycja lub Strategy.

### Pytanie do sali

Raport PDF potrzebuje stronicowania co 30 wierszy. Czy to jeszcze Template Method?

## Scena s17. Limit Instantiation with Singleton - cennik

**Temat ze slajdów:** 16.7. Ograniczenie liczby instancji Singletonem; 17.4. Wchłonięcie Singletona
**Namespace:** `Training.Workshop.M5.S17Singleton` · **Test:** `scripts/warsztat.sh --lang cs test m5/s17` (`S17EquivalenceTest`, `S17SolutionTest`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** `TicketDesk` przy każdej wycenie tworzy nowy `PriceList` i parsuje taryfę, choć cennik jest niemutowalny. Po pomiarze ograniczamy go do jednej instancji (najpierw `GetInstance()`, potem statyczna właściwość `Instance`), a na końcu ukrywamy za interfejsem `ITariff` wstrzykiwanym do `TicketDesk`.

**Zasada:** Limit Instantiation with Singleton to decyzja o cyklu życia: jedna instancja jest bezpieczna tylko wtedy, gdy obiekt jest niemutowalny, a instancje równoważne. Statyczna właściwość tylko do odczytu daje bezpieczną wątkowo, jednokrotną inicjalizację (gwarantuje ją CLR), ale oznacza jedną instancję na `AssemblyLoadContext`, nie na proces. Odwrotny ruch, Inline Singleton, jest równie ważny, gdy globalny dostęp szkodzi testom.

**Efekt:** Cennik powstaje raz, a `TicketDesk` zależy od kontraktu i da się go przetestować z innym cennikiem bez globalnego stanu. Globalna instancja zostaje tylko w domyślnym konstruktorze, a dodanie do niej mutowalnego stanu byłoby najgorszym wariantem.

**Różnica względem Javy:** krok 2 w Javie to singleton jako `enum` (język gwarantuje jedną instancję na loader klas). W C# odpowiednikiem jest idiomatyczna statyczna właściwość `public static PriceList Instance { get; } = new();` z prywatnym konstruktorem - CLR inicjalizuje typ raz i bezpiecznie wątkowo (jedna instancja na `AssemblyLoadContext`, analogicznie do "na loader klas"). Niemodyfikowalna mapa to `FrozenDictionary`, licznik w Start to `Interlocked.Increment` zamiast `AtomicInteger`. Oba testy sceny są w kolekcji xUnit `Legacy`, bo xUnit uruchamia klasy równolegle, a Start zmienia statyczny licznik mierzony w `StartCreatesAPriceListForEveryQuote`. Cennik promocyjny w teście to prywatna klasa `PromoTariff` zamiast lambdy, a test pomiaru po `jump` kończy się bez sprawdzeń.

### Co widzimy

`TicketDesk.Quote` przy każdym wywołaniu robi `new PriceList()`, a konstruktor parsuje taryfę. Cennik jest niemutowalny, więc instancje są równoważne. `S17SolutionTest` najpierw **mierzy**: trzy wyceny to trzy utworzone cenniki.

```csharp
var price = new PriceList().BasePrice(format);
```

### Krok 1: klasyczny Singleton

**Zmiany:**

1. W `PriceList` zmień konstruktor na prywatny i dodaj pole `private static readonly PriceList Instance = new();` oraz metodę `public static PriceList GetInstance()`, która je zwraca.
2. Usuń licznik z pomiaru: pole `_created`, `Interlocked.Increment` w konstruktorze i metodę `Created()`.
3. W `TicketDesk.Quote` zamień `new PriceList()` na `PriceList.GetInstance()`.

**Przed (`PriceList.cs`):**

```csharp
private const string Tariff = "2D=25.00;3D=32.00;IMAX=40.00";
private static int _created;

private readonly Dictionary<string, Money> _prices = [];

public PriceList()
{
    foreach (var entry in Tariff.Split(';'))
    {
        var pair = entry.Split('=');
        _prices[pair[0]] = Money.Of(pair[1]);
    }
    Interlocked.Increment(ref _created);
}

public static int Created()
{
    return Volatile.Read(ref _created);
}
```

**Po (`PriceList.cs`):**

```csharp
private const string Tariff = "2D=25.00;3D=32.00;IMAX=40.00";
private static readonly PriceList Instance = new();

private readonly Dictionary<string, Money> _prices = [];

private PriceList()
{
    foreach (var entry in Tariff.Split(';'))
    {
        var pair = entry.Split('=');
        _prices[pair[0]] = Money.Of(pair[1]);
    }
}

public static PriceList GetInstance()
{
    return Instance;
}
```

**Przed (`TicketDesk.cs`):**

```csharp
public Money Quote(string format, bool online)
{
    var price = new PriceList().BasePrice(format);
    return online ? price.Plus(Money.Of("2.00")) : price;
}
```

**Po (`TicketDesk.cs`, w `Quote`):**

```csharp
var price = PriceList.GetInstance().BasePrice(format);
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s17` - zielone.
**Co powiedzieć:** Singleton to decyzja o cyklu życia - bezpieczna tylko dlatego, że obiekt jest niemutowalny i udowodniliśmy równoważność instancji.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s17 0 1`

### Krok 2: Singleton jako statyczna właściwość tylko do odczytu

**Zmiany:**

1. W `PriceList` usuń pole `Instance` i metodę `GetInstance()`; w ich miejsce dodaj statyczną właściwość `public static PriceList Instance { get; } = new();`.
2. Słownik cen zbuduj w konstruktorze do lokalnego `Dictionary` i zamroź raz: pole zmienia typ na `FrozenDictionary<string, Money>`, przypisanie przez `parsed.ToFrozenDictionary()` (dojdzie `using System.Collections.Frozen;`).
3. W `TicketDesk.Quote` zamień `PriceList.GetInstance()` na `PriceList.Instance`.

**Przed (`PriceList.cs`):**

```csharp
private static readonly PriceList Instance = new();

private readonly Dictionary<string, Money> _prices = [];

private PriceList()
{
    foreach (var entry in Tariff.Split(';'))
    {
        var pair = entry.Split('=');
        _prices[pair[0]] = Money.Of(pair[1]);
    }
}

public static PriceList GetInstance()
{
    return Instance;
}
```

**Po (`PriceList.cs`):**

```csharp
private readonly FrozenDictionary<string, Money> _prices;

private PriceList()
{
    var parsed = new Dictionary<string, Money>();
    foreach (var entry in Tariff.Split(';'))
    {
        var pair = entry.Split('=');
        parsed[pair[0]] = Money.Of(pair[1]);
    }
    _prices = parsed.ToFrozenDictionary();
}

public static PriceList Instance { get; } = new();
```

**Po (`TicketDesk.cs`, w `Quote`):**

```csharp
var price = PriceList.Instance.BasePrice(format);
```

**Uruchom:** test zielony.
**Co powiedzieć:** CLR daje bezpieczną publikację i jednokrotną inicjalizację bez `lock`. Ale to **jedna instancja na `AssemblyLoadContext`**, nie "jedna na proces", a prywatny konstruktor nie chroni przed refleksją (`Activator.CreateInstance(type, nonPublic: true)`) - inaczej niż enum w Javie.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s17 1 2`

### Krok 3: wstrzyknięcie zamiast globalnego dostępu

**Zmiany:**

1. Utwórz nowy plik `ITariff.cs`: interfejs z metodą `Money BasePrice(string format)` (wyodrębnienie interfejsu z `PriceList`); `PriceList` deklaruje `: ITariff`.
2. W `TicketDesk` dodaj pole `ITariff _tariff` i konstruktor `TicketDesk(ITariff tariff)` ze strażnikiem `ArgumentNullException.ThrowIfNull`; `Quote` woła `_tariff.BasePrice(format)`.
3. Dodaj konstruktor bezargumentowy delegujący `: this(PriceList.Instance)` - dotychczasowi klienci działają bez zmian.

**Przed (`TicketDesk.cs`):**

```csharp
public sealed class TicketDesk
{
    public Money Quote(string format, bool online)
    {
        var price = PriceList.Instance.BasePrice(format);
        return online ? price.Plus(Money.Of("2.00")) : price;
    }
}
```

**Po (`ITariff.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

/// <summary>Extract Interface - klient zależy od kontraktu cennika, nie od singletona.</summary>
public interface ITariff
{
    Money BasePrice(string format);
}
```

**Po (`TicketDesk.cs`):**

```csharp
using Training.Workshop.Shared;

public sealed class TicketDesk
{
    private readonly ITariff _tariff;

    public TicketDesk()
        : this(PriceList.Instance)
    {
    }

    public TicketDesk(ITariff tariff)
    {
        ArgumentNullException.ThrowIfNull(tariff);
        _tariff = tariff;
    }

    public Money Quote(string format, bool online)
    {
        var price = _tariff.BasePrice(format);
        return online ? price.Plus(Money.Of("2.00")) : price;
    }
}
```

**Uruchom:** test zielony; `InjectedTariffNeedsNoGlobalState` używa cennika promocyjnego `PromoTariff`.
**Co powiedzieć:** o jednej instancji decyduje korzeń kompozycji, a klient zależy od kontraktu. Gdyby ktoś dodał do singletona `SetPromo(...)`, testy zaczęłyby wpływać na siebie nawzajem - mutowalny globalny stan to najgorszy wariant.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s17 2 3`

### Rozwiązanie i uzasadnienie

Jedna instancja cennika (statyczna właściwość `Instance`), ale ukryta za interfejsem i wstrzykiwana. Odwrotna transformacja (Inline Singleton) jest równie ważna - gdy globalny dostęp szkodzi testom, wracamy do jawnej zależności. W aplikacji z kontenerem DI ten sam efekt daje rejestracja `AddSingleton<ITariff, PriceList>()`.

### Pułapki

- Singleton z mutowalnym stanem (promocje, cache) - testy zależne od kolejności (a w xUnit także od równoległości klas testowych).
- Ręcznie pisany leniwy singleton z podwójnym sprawdzaniem zamiast `Lazy<T>` albo inicjalizacji statycznej.
- Singleton "bo tak wygodniej" bez pomiaru kosztu tworzenia.

### Pytanie do sali

Cennik ma się zmieniać o północy bez restartu. Co wtedy z singletonem?

## Scena s18. Move Accumulation to Collecting Parameter - ostrzeżenia walidacji

**Temat ze slajdów:** 16.6. Przeniesienie zbierania wyników do parametru zbierającego
**Namespace:** `Training.Workshop.M5.S18CollectingParameter` · **Test:** `scripts/warsztat.sh --lang cs test m5/s18` (`S18EquivalenceTest`)
**Czas:** ~8 min

### W skrócie

**Co robimy:** `ReservationValidator` skleja ostrzeżenia w `string`, a każda metoda pomocnicza dopisuje własny separator. Zamieniamy sklejanie na listę, potem przekazujemy akumulator do metod `Check...`, a na końcu zawężamy go do klasy `Warnings`.

**Zasada:** Collecting Parameter polega na tym, że metody dopisują wyniki do przekazanego akumulatora, zamiast zwracać fragmenty do sklejenia. Właścicielem kolekcji jest wywołujący, trzeba ustalić, czy metoda czyści, czy dopisuje, a wynik częściowy po wyjątku jest częścią kontraktu. Dobry akumulator ma wąski typ, a nie ogólną mutowalną kolekcję.

**Efekt:** Separator i format wyniku są w jednym miejscu, a metody pomocnicze mogą tylko dopisać ostrzeżenie. Treść, kolejność i separator ostrzeżeń są dokładnie takie jak w Start.

**Różnica względem Javy:** brak istotnych. E-mail jest `string?`, `isBlank()` to `string.IsNullOrWhiteSpace`, `addAll` w kroku 1 to `AddRange`, a obcięcie separatora w Start to zakres `warnings[..^2]`.

### Co widzimy

`ReservationValidator.Validate` skleja ostrzeżenia w `string`: każda metoda pomocnicza zwraca fragment zakończony `"; "`, a na końcu obcinamy dwa znaki. Łatwo zgubić separator.

```csharp
warnings += CheckEmail(draft.Email);
warnings += CheckSeats(draft.Seats);
...
return warnings.Length == 0 ? "OK" : warnings[..^2];
```

Test sprawdza treść, kolejność i separator, w tym duplikaty miejsc zgłaszane jeden raz.

### Krok 1: lista zamiast sklejania

**Zmiany:**

1. Zmień typ zwracany `CheckEmail` i `CheckSeats` na `IReadOnlyList<string>`: fragmenty bez separatora, puste przypadki zwracają `[]`, a `CheckSeats` zbiera wyniki do lokalnej `List<string>`.
2. W `Validate` zamień `string warnings` na `List<string>`, a sklejanie `+=` na `AddRange(...)`; warunek czasu seansu dopisuje przez `warnings.Add(...)` (już bez `"; "`).
3. Wynik złóż raz, na końcu: `warnings.Count == 0 ? "OK" : string.Join("; ", warnings)`.

**Przed:**

```csharp
public string Validate(ReservationDraft draft)
{
    var warnings = "";
    warnings += CheckEmail(draft.Email);
    warnings += CheckSeats(draft.Seats);
    if (draft.Now >= draft.ShowStart)
    {
        warnings += "seans juz sie rozpoczal; ";
    }
    return warnings.Length == 0 ? "OK" : warnings[..^2];
}

private static string CheckEmail(string? email)
{
    if (string.IsNullOrWhiteSpace(email))
    {
        return "brak e-maila; ";
    }
    if (!email.Contains('@'))
    {
        return "niepoprawny e-mail: " + email + "; ";
    }
    return "";
}

private static string CheckSeats(IReadOnlyList<string> seats)
{
    if (seats.Count == 0)
    {
        return "brak miejsc; ";
    }
    var result = "";
    var seen = new HashSet<string>();
    var reported = new HashSet<string>();
    foreach (var seat in seats)
    {
        if (!seen.Add(seat) && reported.Add(seat))
        {
            result += "miejsce " + seat + " zdublowane; ";
        }
    }
    if (seats.Count >= 10)
    {
        result += "grupa 10+: zastosuj rabat grupowy; ";
    }
    return result;
}
```

**Po:**

```csharp
public string Validate(ReservationDraft draft)
{
    var warnings = new List<string>();
    warnings.AddRange(CheckEmail(draft.Email));
    warnings.AddRange(CheckSeats(draft.Seats));
    if (draft.Now >= draft.ShowStart)
    {
        warnings.Add("seans juz sie rozpoczal");
    }
    return warnings.Count == 0 ? "OK" : string.Join("; ", warnings);
}

private static IReadOnlyList<string> CheckEmail(string? email)
{
    if (string.IsNullOrWhiteSpace(email))
    {
        return ["brak e-maila"];
    }
    if (!email.Contains('@'))
    {
        return ["niepoprawny e-mail: " + email];
    }
    return [];
}

private static IReadOnlyList<string> CheckSeats(IReadOnlyList<string> seats)
{
    if (seats.Count == 0)
    {
        return ["brak miejsc"];
    }
    var result = new List<string>();
    var seen = new HashSet<string>();
    var reported = new HashSet<string>();
    foreach (var seat in seats)
    {
        if (!seen.Add(seat) && reported.Add(seat))
        {
            result.Add("miejsce " + seat + " zdublowane");
        }
    }
    if (seats.Count >= 10)
    {
        result.Add("grupa 10+: zastosuj rabat grupowy");
    }
    return result;
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s18` - zielone.
**Co powiedzieć:** separator dokłada teraz jedno miejsce. Metody wciąż tworzą własne listy.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s18 0 1`

### Krok 2: Collecting Parameter

**Zmiany:**

1. Zmień sygnatury metod pomocniczych (zmiana sygnatury): dodaj parametr `List<string> warnings`, typ zwracany na `void`, a każde `return [...]` zamień na `warnings.Add(...)` (w `CheckEmail` drugi warunek jako `else if`, w `CheckSeats` po "brak miejsc" wczesny `return`).
2. Warunek czasu seansu wydziel do metody `CheckShowTime(draft, warnings)` (wyodrębnienie metody).
3. W `Validate` zostają: utworzenie listy, trzy wywołania `Check...` i `string.Join` na końcu.

**Przed:**

```csharp
var warnings = new List<string>();
warnings.AddRange(CheckEmail(draft.Email));
warnings.AddRange(CheckSeats(draft.Seats));
if (draft.Now >= draft.ShowStart)
{
    warnings.Add("seans juz sie rozpoczal");
}
return warnings.Count == 0 ? "OK" : string.Join("; ", warnings);
```

**Po:**

```csharp
public string Validate(ReservationDraft draft)
{
    var warnings = new List<string>();
    CheckEmail(draft.Email, warnings);
    CheckSeats(draft.Seats, warnings);
    CheckShowTime(draft, warnings);
    return warnings.Count == 0 ? "OK" : string.Join("; ", warnings);
}

private static void CheckEmail(string? email, List<string> warnings)
{
    if (string.IsNullOrWhiteSpace(email))
    {
        warnings.Add("brak e-maila");
    }
    else if (!email.Contains('@'))
    {
        warnings.Add("niepoprawny e-mail: " + email);
    }
}

private static void CheckSeats(IReadOnlyList<string> seats, List<string> warnings)
{
    if (seats.Count == 0)
    {
        warnings.Add("brak miejsc");
        return;
    }
    var seen = new HashSet<string>();
    var reported = new HashSet<string>();
    foreach (var seat in seats)
    {
        if (!seen.Add(seat) && reported.Add(seat))
        {
            warnings.Add("miejsce " + seat + " zdublowane");
        }
    }
    if (seats.Count >= 10)
    {
        warnings.Add("grupa 10+: zastosuj rabat grupowy");
    }
}

private static void CheckShowTime(ReservationDraft draft, List<string> warnings)
{
    if (draft.Now >= draft.ShowStart)
    {
        warnings.Add("seans juz sie rozpoczal");
    }
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** metody dopisują do przekazanego akumulatora. Właścicielem kolekcji jest `Validate` - tworzy ją i decyduje o formacie.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s18 1 2`

### Krok 3: wąski typ parametru

**Zmiany:**

1. Utwórz nowy plik `Warnings.cs`: klasa z prywatną listą `_items`, metodą `Add(string)` i metodą `Summary()` zwracającą `"OK"` albo `string.Join("; ", _items)`.
2. W `Validate` zamień `new List<string>()` na `new Warnings()`; kompilator wskaże wszystkie metody `Check...`, w których typ parametru trzeba zmienić z `List<string>` na `Warnings`.
3. Na końcu `Validate` zwróć `warnings.Summary()` - format wyniku przenosi się do `Warnings`.

**Przed:**

```csharp
public string Validate(ReservationDraft draft)
{
    var warnings = new List<string>();
    CheckEmail(draft.Email, warnings);
    CheckSeats(draft.Seats, warnings);
    CheckShowTime(draft, warnings);
    return warnings.Count == 0 ? "OK" : string.Join("; ", warnings);
}
```

**Po (`Warnings.cs`, nowy plik):**

```csharp
public sealed class Warnings
{
    private readonly List<string> _items = [];

    public void Add(string warning)
    {
        _items.Add(warning);
    }

    public string Summary()
    {
        return _items.Count == 0 ? "OK" : string.Join("; ", _items);
    }
}
```

**Po (`ReservationValidator.cs`, metoda `Validate`):**

```csharp
public string Validate(ReservationDraft draft)
{
    var warnings = new Warnings();
    CheckEmail(draft.Email, warnings);
    CheckSeats(draft.Seats, warnings);
    CheckShowTime(draft, warnings);
    return warnings.Summary();
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** metoda pomocnicza może tylko dopisać - nie wyczyści ani nie przestawi cudzych ostrzeżeń.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s18 2 3`

### Rozwiązanie i uzasadnienie

Każda reguła to metoda `Check...(..., Warnings)`, format wyniku w jednym miejscu, akumulator o wąskim API.

### Pułapki

- Ogólny mutowalny słownik jako parametr zbierający - ukrywa, kto co zapisuje.
- Niejasne, czy metoda czyści akumulator, czy dopisuje.
- Wyjątek w połowie zbierania zostawia wynik częściowy - to część kontraktu, trzeba go ustalić.

### Pytanie do sali

Czy `Warnings` powinno pozwalać na poziomy (błąd kontra ostrzeżenie)? Jak to zmieni kontrakt?

## Scena s19. Visitor i macierz zmian - pozycje zamówienia

**Temat ze slajdów:** 15.1. Odwiedzający i macierz zmian; 15.2. Odwiedzający albo hierarchia `sealed` i `switch`
**Namespace:** `Training.Workshop.M5.S19Visitor` · **Test:** `scripts/warsztat.sh --lang cs test m5/s19` (`S19EquivalenceTest`, `S19SolutionTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `ReceiptPrinter` ma trzy operacje na pozycjach zamówienia, każda z łańcuchem testów `is` zakończonym wyjątkiem w runtime. Zamieniamy je na klasyczne Visitory, a potem pokazujemy alternatywę C#: switch expression po typach z wzorcami pozycyjnymi - i jej granice.

**Zasada:** Visitor przenosi operacje na strukturze do osobnych klas, a element wybiera właściwą metodę przez double dispatch. Macierz zmian mówi, kiedy go stosować: nowa operacja jest tania, nowy rodzaj elementu wymaga zmiany wszystkich Visitorów. W Javie 25 tę samą kontrolę kompilatora daje wyczerpujący `switch` po `sealed`; C# nie ma zamkniętych hierarchii, więc switch po typach wymaga ramienia `_`, a nowy rodzaj pozycji wychodzi dopiero w runtime - w C# argument za Visitorem jest mocniejszy.

**Efekt:** W kodzie nie ma łańcuchów `is`. W wersji z Visitorem pozycja bez obsługi to błąd kompilacji zamiast awarii na kasie, w wersji ze switch expression - `UnreachableException` z nazwą typu. Obie formy są poprawne - wybór zależy od tego, czy częściej dochodzą operacje, czy rodzaje pozycji, i od tego, ile kontroli kompilatora potrzebujemy.

**Różnica względem Javy:** krok 3 w Javie to `sealed interface` + wyczerpujący `switch` bez `default`, w którym kompilator pilnuje nowych typów. C# 14 nie ma zamkniętych hierarchii ani sprawdzania wyczerpania dla wzorców typów - switch expression bez `_` daje ostrzeżenie CS8509 (przy `TreatWarningsAsErrors` błąd). Krok 3 w porcie to więc switch expression po typach (w linii paragonu wzorce pozycyjne, odpowiednik record patterns) z ramieniem `_ => throw new UnreachableException(...)`, a `IOrderItem` jest zwykłym interfejsem znacznikowym. Test Javy `newOperationIsANewSwitchInJava25` nazywa się `NewOperationIsANewSwitchExpression`. Generyczne `accept` to `TResult Accept<TResult>(IOrderItemVisitor<TResult> visitor)`, a interfejs Visitora jest kowariantny (`out TResult`).

### Co widzimy

Pozycje zamówienia: bilet (VAT 8%), produkt baru (VAT 23%), voucher (pomniejsza kwotę, bez VAT). `ReceiptPrinter` ma trzy operacje - linia paragonu, kwota, VAT - każda z łańcuchem `is` zakończonym wyjątkiem w runtime. Nowy rodzaj pozycji kompiluje się bez błędu i wybucha na kasie.

```csharp
if (item is TicketItem ticket)
{
    return VatOf(ticket.Price, 8);
}
else if (item is SnackItem snack)
{
    return VatOf(snack.Price, 23);
}
else if (item is VoucherItem)
{
    return Money.Zero;
}
throw new ArgumentException("unknown item: " + item);
```

### Krok 1: Accept + pierwszy Visitor

**Zmiany:**

1. Utwórz nowy plik `IOrderItemVisitor.cs`: kowariantny interfejs `IOrderItemVisitor<out TResult>` z metodami `VisitTicket`, `VisitSnack`, `VisitVoucher`.
2. W `IOrderItem` dodaj metodę `TResult Accept<TResult>(IOrderItemVisitor<TResult> visitor)`; każdy rekord pozycji implementuje ją jednym wywołaniem swojej metody, np. `visitor.VisitTicket(this)`.
3. Operację "linia paragonu" przenieś z `ReceiptPrinter.Line` do nowej klasy `ReceiptLineVisitor : IOrderItemVisitor<string>` - po jednej gałęzi `is` na metodę `Visit...`. W `Print` zamień `Line(item)` na `item.Accept(_lines)` (pole `private readonly ReceiptLineVisitor _lines = new();`), metodę `Line` usuń. Kwota i VAT zostają na `is`.

**Przed (`ReceiptPrinter.cs`):**

```csharp
private static string Line(IOrderItem item)
{
    if (item is TicketItem ticket)
    {
        return "Bilet " + ticket.Title + " " + ticket.Format + " " + ticket.Price;
    }
    else if (item is SnackItem snack)
    {
        return snack.Name + " " + snack.Price;
    }
    else if (item is VoucherItem voucher)
    {
        return "Voucher " + voucher.Code + " -" + voucher.Value;
    }
    throw new ArgumentException("unknown item: " + item);
}
```

**Po (`IOrderItemVisitor.cs`, nowy plik):**

```csharp
/// <summary>Klasyczny Visitor - jedna metoda na rodzaj pozycji; brak metody = błąd kompilacji.</summary>
public interface IOrderItemVisitor<out TResult>
{
    TResult VisitTicket(TicketItem ticket);

    TResult VisitSnack(SnackItem snack);

    TResult VisitVoucher(VoucherItem voucher);
}
```

**Po (`ReceiptLineVisitor.cs`, nowy plik):**

```csharp
public sealed class ReceiptLineVisitor : IOrderItemVisitor<string>
{
    public string VisitTicket(TicketItem ticket)
    {
        return "Bilet " + ticket.Title + " " + ticket.Format + " " + ticket.Price;
    }

    public string VisitSnack(SnackItem snack)
    {
        return snack.Name + " " + snack.Price;
    }

    public string VisitVoucher(VoucherItem voucher)
    {
        return "Voucher " + voucher.Code + " -" + voucher.Value;
    }
}
```

**Po (`TicketItem.cs`; analogicznie `SnackItem.cs` z `VisitSnack` i `VoucherItem.cs` z `VisitVoucher`):**

```csharp
public sealed record TicketItem(string Title, string Format, Money Price) : IOrderItem
{
    public TResult Accept<TResult>(IOrderItemVisitor<TResult> visitor)
    {
        return visitor.VisitTicket(this);
    }
}
```

**Po (`ReceiptPrinter.cs`, w pętli `Print`):**

```csharp
text.Append(item.Accept(_lines)).Append('\n');
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s19` - zielone.
**Co powiedzieć:** double dispatch - element wybiera metodę odwiedzającego. Brak metody dla typu to błąd kompilacji.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s19 0 1`

### Krok 2: pozostałe operacje jako Visitory

**Zmiany:**

1. Tak samo przenieś `Amount` do nowej klasy `AmountVisitor : IOrderItemVisitor<Money>`, a `Vat` (razem z pomocniczym `VatOf`) do `VatVisitor : IOrderItemVisitor<Money>`.
2. W `ReceiptPrinter` dodaj pola `_amounts` i `_vats`; w pętli `Print` zamień wywołania na `item.Accept(_amounts)` i `item.Accept(_vats)`.
3. Usuń z `ReceiptPrinter` metody `Amount`, `Vat` i `VatOf` - znikają ostatnie łańcuchy `is`.

**Przed (`ReceiptPrinter.cs`):**

```csharp
private static Money Amount(IOrderItem item)
{
    if (item is TicketItem ticket)
    {
        return ticket.Price;
    }
    else if (item is SnackItem snack)
    {
        return snack.Price;
    }
    else if (item is VoucherItem voucher)
    {
        return Money.Zero.Minus(voucher.Value);
    }
    throw new ArgumentException("unknown item: " + item);
}
```

**Po (`AmountVisitor.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public sealed class AmountVisitor : IOrderItemVisitor<Money>
{
    public Money VisitTicket(TicketItem ticket)
    {
        return ticket.Price;
    }

    public Money VisitSnack(SnackItem snack)
    {
        return snack.Price;
    }

    public Money VisitVoucher(VoucherItem voucher)
    {
        return Money.Zero.Minus(voucher.Value);
    }
}
```

**Po (`VatVisitor.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public sealed class VatVisitor : IOrderItemVisitor<Money>
{
    public Money VisitTicket(TicketItem ticket)
    {
        return VatOf(ticket.Price, 8);
    }

    public Money VisitSnack(SnackItem snack)
    {
        return VatOf(snack.Price, 23);
    }

    public Money VisitVoucher(VoucherItem voucher)
    {
        return Money.Zero;
    }

    /// <summary>VAT zawarty w cenie brutto: brutto * stawka / (100 + stawka).</summary>
    private static Money VatOf(Money gross, int rate)
    {
        return new Money(Math.Round(gross.Amount * rate / (100 + rate), 2, MidpointRounding.AwayFromZero));
    }
}
```

**Po (`ReceiptPrinter.cs`):**

```csharp
private readonly ReceiptLineVisitor _lines = new();
private readonly AmountVisitor _amounts = new();
private readonly VatVisitor _vats = new();
...
total = total.Plus(item.Accept(_amounts));
vat = vat.Plus(item.Accept(_vats));
```

**Uruchom:** test zielony; `S19SolutionTest.NewOperationIsANewVisitor` dodaje operację "grupa VAT" jako nowy Visitor.
**Co powiedzieć:** macierz zmian: nowa operacja - nowa klasa, tanio; nowy rodzaj pozycji - zmiana interfejsu i **wszystkich** Visitorów. W C# to jedyna z form tej sceny, w której kompilator pilnuje kompletności.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s19 1 2`

### Krok 3: alternatywa C# - switch expression po typach

**Zmiany:**

1. Usuń pliki `IOrderItemVisitor.cs`, `ReceiptLineVisitor.cs`, `AmountVisitor.cs` i `VatVisitor.cs`; z `IOrderItem` i rekordów pozycji usuń `Accept` - interfejs znów jest pustym znacznikiem.
2. W `ReceiptPrinter` zapisz operacje jako metody `internal static` `Line`, `Amount` i `Vat` ze switch expression po typach; w `Line` użyj wzorców pozycyjnych, np. `TicketItem(var title, var format, var price) => ...`. Pomocnicze `VatOf` wraca do `ReceiptPrinter` (bez zmian treści). Pętla `Print` woła znów `Line(item)`, `Amount(item)` i `Vat(item)`, a pola `_lines`, `_amounts`, `_vats` znikają.
3. Każdy switch kończy ramię `_ => throw new UnreachableException("unknown item: " + item)` (dojdzie `using System.Diagnostics;`).

**Przed (`ReceiptPrinter.cs`):**

```csharp
private readonly ReceiptLineVisitor _lines = new();
private readonly AmountVisitor _amounts = new();
private readonly VatVisitor _vats = new();
...
foreach (var item in items)
{
    text.Append(item.Accept(_lines)).Append('\n');
    total = total.Plus(item.Accept(_amounts));
    vat = vat.Plus(item.Accept(_vats));
}
```

**Po (`ReceiptPrinter.cs`):**

```csharp
foreach (var item in items)
{
    text.Append(Line(item)).Append('\n');
    total = total.Plus(Amount(item));
    vat = vat.Plus(Vat(item));
}
...
internal static string Line(IOrderItem item) => item switch
{
    TicketItem(var title, var format, var price) => "Bilet " + title + " " + format + " " + price,
    SnackItem(var name, var price) => name + " " + price,
    VoucherItem(var code, var value) => "Voucher " + code + " -" + value,
    _ => throw new UnreachableException("unknown item: " + item),
};

internal static Money Amount(IOrderItem item) => item switch
{
    TicketItem ticket => ticket.Price,
    SnackItem snack => snack.Price,
    VoucherItem voucher => Money.Zero.Minus(voucher.Value),
    _ => throw new UnreachableException("unknown item: " + item),
};

internal static Money Vat(IOrderItem item) => item switch
{
    TicketItem ticket => VatOf(ticket.Price, 8),
    SnackItem snack => VatOf(snack.Price, 23),
    VoucherItem => Money.Zero,
    _ => throw new UnreachableException("unknown item: " + item),
};
```

**Uruchom:** test zielony.
**Co powiedzieć:** każda operacja w jednym miejscu, bez ceremonii `Accept/Visit`, ale **bez** kontroli kompilatora: nowy rodzaj pozycji skompiluje się i wyjdzie jako `UnreachableException` na kasie. W Javie 25 ta forma daje tę samą kontrolę co Visitor, w C# - nie. Visitor ma w C# sens zawsze, gdy nowe rodzaje mają psuć kompilację, a także gdy odwiedzający ma stan i logikę przejścia struktury.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s19 2 3`

### Rozwiązanie i uzasadnienie

Dwie poprawne formy docelowe: klasyczny Visitor (`Step2`) i switch expression po typach (`Step3`). Macierz zmian:

| Częsta zmiana | Naturalniejszy kierunek |
| --- | --- |
| nowe operacje, stabilne rodzaje pozycji | Visitor (w C# jedyna forma z kontrolą kompilatora) albo switch expression po typach |
| nowe rodzaje pozycji, stabilne operacje | metody polimorficzne w pozycjach |
| jedna prosta akumulacja | Collecting Parameter (scena s18) |

### Pułapki

- Ramię `_`, które po cichu zwraca wartość domyślną zamiast rzucać `UnreachableException` - nowy typ pozycji przejdzie niezauważony.
- VAT zaokrąglany per pozycja kontra od sumy - różne wyniki, trzeba zachować sposób ze Start.
- Visitor dla hierarchii, do której ciągle dochodzą typy - każdy nowy typ dotyka wszystkich Visitorów.

### Pytanie do sali

Dochodzi pozycja "karta podarunkowa" i operacja "eksport do księgowości". Która forma przyjmie te zmiany taniej?

## Scena s20. Mapa decyzji - jedna tabela, dwie struktury docelowe

**Temat ze slajdów:** 4.2. Najpierw rodzaj zmienności, potem wzorzec; 18.1. Mapa decyzji (1/2); 18.2. Mapa decyzji (2/2); 18.3. Lista kontrolna
**Namespace:** `Training.Workshop.M5.S20DecisionMap` · **Test:** `scripts/warsztat.sh --lang cs test m5/s20` (`S20EquivalenceTest`, `S20SolutionTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `ShowPricing` ma tabelę cen 3x3 wpisaną w zagnieżdżone warunki, w których splecione są format i reguła dnia. Najpierw rozplatamy dwie osie, a potem z tego samego punktu budujemy dwie alternatywy: Strategy dla reguły dnia albo typ formatu z zachowaniem.

**Zasada:** Wzorzec wybieramy według rodzaju zmienności, a nie kształtu kodu - ten sam `switch` może sygnalizować różne problemy. Pytamy, która oś zmienia się częściej, i tam budujemy punkt rozszerzenia. Gdy tego nie wiemy, zostajemy przy prostej strukturze bez nowych typów.

**Efekt:** Obie ścieżki są behawioralnie równoważne, a różnią się kosztem przyszłej zmiany: A tanio przyjmuje nowe akcje dniowe, B nowe formaty. Reguła zależna od obu osi naraz łamie założenie niezależności i wtedy tabela może być lepsza niż wzorzec.

**Różnica względem Javy:** `@FunctionalInterface DayPolicy` to delegat `DayPolicy(Money basePrice)` - Strategy jako delegat; stałe `DayPolicies.CheapTuesday`, `Weekend`, `Regular` to lambdy jak w Javie, a kalendarz to `Func<DayOfWeek, DayPolicy>`. `enum Format` z polami i zachowaniem to `sealed class Format` z instancjami statycznymi `TwoD`, `ThreeD`, `Imax` i prywatnym konstruktorem (enum C# nie ma pól ani metod). `DayOfWeek` pochodzi z `System` (wartości PascalCase), a test parsuje wejście "MONDAY 2D" przez `Enum.Parse<DayOfWeek>(..., ignoreCase: true)`, więc oczekiwane teksty są identyczne jak w Javie.

### Co widzimy

`ShowPricing.Price(day, format)` ma tabelę cen 3x3 wpisaną w zagnieżdżone warunki. Dwie osie zmienności - format i reguła dnia ("tani wtorek" -30%, weekend +2.00) - są splecione.

```csharp
if (format == "2D")
{
    return Money.Of(day == DayOfWeek.Tuesday ? "17.50" : weekend ? "27.00" : "25.00");
}
else if (format == "3D")
{
    return Money.Of(day == DayOfWeek.Tuesday ? "22.40" : weekend ? "34.00" : "32.00");
} ...
```

Ta scena ma **dwie ścieżki**: krok 1 jest wspólny, krok 2 to ścieżka A (Strategy dla reguły dnia), krok 3 to ścieżka B (typ formatu z zachowaniem) - budowana od kroku 1, nie od kroku 2. Test równoważności sprawdza pełną tabelę dla obu ścieżek.

### Krok 1: rozplecenie osi (wspólny)

**Zmiany:**

1. Zauważ, że tabela to dwie niezależne reguły. Wydziel z `Price` metodę `BasePrice(format)` (wyodrębnienie metody) jako switch expression po formacie: trzy ceny bazowe i ten sam wyjątek dla nieznanego formatu.
2. Wydziel metodę `AdjustForDay(day, basePrice)` jako switch expression po dniu: wtorek to `basePrice.Minus(basePrice.Percent(30))`, sobota lub niedziela `basePrice.Plus(Money.Of("2.00"))`, reszta bez korekty.
3. `Price` staje się złożeniem obu metod; lokalna zmienna `weekend` znika.

**Przed:**

```csharp
var weekend = day == DayOfWeek.Saturday || day == DayOfWeek.Sunday;
if (format == "2D")
{
    return Money.Of(day == DayOfWeek.Tuesday ? "17.50" : weekend ? "27.00" : "25.00");
}
else if (format == "3D")
{
    return Money.Of(day == DayOfWeek.Tuesday ? "22.40" : weekend ? "34.00" : "32.00");
}
else if (format == "IMAX")
{
    return Money.Of(day == DayOfWeek.Tuesday ? "28.00" : weekend ? "42.00" : "40.00");
}
throw new ArgumentException("unknown format: " + format);
```

**Po:**

```csharp
public Money Price(DayOfWeek day, string format)
{
    return AdjustForDay(day, BasePrice(format));
}

private static Money BasePrice(string format) => format switch
{
    "2D" => Money.Of("25.00"),
    "3D" => Money.Of("32.00"),
    "IMAX" => Money.Of("40.00"),
    _ => throw new ArgumentException("unknown format: " + format),
};

private static Money AdjustForDay(DayOfWeek day, Money basePrice) => day switch
{
    DayOfWeek.Tuesday => basePrice.Minus(basePrice.Percent(30)),
    DayOfWeek.Saturday or DayOfWeek.Sunday => basePrice.Plus(Money.Of("2.00")),
    _ => basePrice,
};
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s20` - zielone (9 cen i nieznany format).
**Co powiedzieć:** to jest moment decyzji. Ten sam `switch` może sygnalizować różne problemy - pytamy, **która oś zmienia się częściej**.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s20 0 1`

### Krok 2: ścieżka A - zmienia się reguła dnia (Strategy)

**Zmiany:**

1. Utwórz nowy plik `DayPolicy.cs`: delegat `public delegate Money DayPolicy(Money basePrice)` - Strategy w formie delegata.
2. Utwórz nowy plik `DayPolicies.cs`: statyczna klasa ze stałymi `CheapTuesday`, `Weekend`, `Regular` (lambdy z logiką przeniesioną z `AdjustForDay`) i kalendarzem `Standard(DayOfWeek)`, który dla dnia zwraca politykę.
3. W `ShowPricing` dodaj pole `Func<DayOfWeek, DayPolicy> _calendar` i konstruktor przyjmujący kalendarz ze strażnikiem `ArgumentNullException.ThrowIfNull` (bezargumentowy deleguje `: this(DayPolicies.Standard)`); `Price` woła `_calendar(day)(BasePrice(format))`, metoda `AdjustForDay` znika. Format zostaje prostym `switch`.

**Przed:**

```csharp
public Money Price(DayOfWeek day, string format)
{
    return AdjustForDay(day, BasePrice(format));
}
...
private static Money AdjustForDay(DayOfWeek day, Money basePrice) => day switch
{
    DayOfWeek.Tuesday => basePrice.Minus(basePrice.Percent(30)),
    DayOfWeek.Saturday or DayOfWeek.Sunday => basePrice.Plus(Money.Of("2.00")),
    _ => basePrice,
};
```

**Po (`DayPolicy.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

/// <summary>Korekta ceny za dzień jako wymienny algorytm - Strategy (delegat).</summary>
public delegate Money DayPolicy(Money basePrice);
```

**Po (`DayPolicies.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public static class DayPolicies
{
    public static readonly DayPolicy CheapTuesday = basePrice => basePrice.Minus(basePrice.Percent(30));
    public static readonly DayPolicy Weekend = basePrice => basePrice.Plus(Money.Of("2.00"));
    public static readonly DayPolicy Regular = basePrice => basePrice;

    public static DayPolicy Standard(DayOfWeek day) => day switch
    {
        DayOfWeek.Tuesday => CheapTuesday,
        DayOfWeek.Saturday or DayOfWeek.Sunday => Weekend,
        _ => Regular,
    };
}
```

**Po (`ShowPricing.cs`):**

```csharp
private readonly Func<DayOfWeek, DayPolicy> _calendar;

public ShowPricing()
    : this(DayPolicies.Standard)
{
}

public ShowPricing(Func<DayOfWeek, DayPolicy> calendar)
{
    ArgumentNullException.ThrowIfNull(calendar);
    _calendar = calendar;
}

public Money Price(DayOfWeek day, string format)
{
    return _calendar(day)(BasePrice(format));
}
```

**Uruchom:** test zielony; `PathAMakesANewDayCampaignCheap` dodaje "środę seniora" bez dotykania formatów.
**Co powiedzieć:** wybierz tę ścieżkę, gdy marketing co miesiąc zmienia akcje dniowe, a formaty są stabilne. Zestaw polityk jest otwarty i wstrzykiwany. Tu delegat jest naturalniejszy niż interfejs z s01: polityka to jedna funkcja bez nazwanych wariantów w konfiguracji.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s20 1 2`

### Krok 3: ścieżka B - zmienia się zestaw formatów (typ z zachowaniem)

**Zmiany:**

1. Alternatywa budowana od kroku 1, nie od kroku 2. Utwórz nowy plik `Format.cs`: `sealed class Format` z prywatnym konstruktorem, instancjami statycznymi `TwoD`, `ThreeD`, `Imax` (kod i cena bazowa z `BasePrice`) oraz listą `Values`.
2. W `Format` dodaj `Of(code)` przeszukujące `Values` i rzucające ten sam `ArgumentException` dla nieznanego kodu oraz metodę `PriceOn(day)` ze switch expression z regułą dnia (logika z `AdjustForDay`).
3. `ShowPricing.Price` deleguje do `Format.Of(format).PriceOn(day)`; metody `BasePrice` i `AdjustForDay` znikają z `ShowPricing`.

**Przed:**

```csharp
private static Money BasePrice(string format) => format switch
{
    "2D" => Money.Of("25.00"),
    "3D" => Money.Of("32.00"),
    "IMAX" => Money.Of("40.00"),
    _ => throw new ArgumentException("unknown format: " + format),
};

private static Money AdjustForDay(DayOfWeek day, Money basePrice) => day switch
{
    DayOfWeek.Tuesday => basePrice.Minus(basePrice.Percent(30)),
    DayOfWeek.Saturday or DayOfWeek.Sunday => basePrice.Plus(Money.Of("2.00")),
    _ => basePrice,
};
```

**Po (`Format.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public sealed class Format
{
    public static readonly Format TwoD = new("2D", "25.00");
    public static readonly Format ThreeD = new("3D", "32.00");
    public static readonly Format Imax = new("IMAX", "40.00");

    private static readonly IReadOnlyList<Format> Values = [TwoD, ThreeD, Imax];

    private readonly string _code;
    private readonly Money _basePrice;

    private Format(string code, string basePrice)
    {
        _code = code;
        _basePrice = Money.Of(basePrice);
    }

    public static Format Of(string code)
    {
        foreach (var format in Values)
        {
            if (format._code == code)
            {
                return format;
            }
        }
        throw new ArgumentException("unknown format: " + code);
    }

    public Money PriceOn(DayOfWeek day) => day switch
    {
        DayOfWeek.Tuesday => _basePrice.Minus(_basePrice.Percent(30)),
        DayOfWeek.Saturday or DayOfWeek.Sunday => _basePrice.Plus(Money.Of("2.00")),
        _ => _basePrice,
    };
}
```

**Po (`ShowPricing.cs`):**

```csharp
public Money Price(DayOfWeek day, string format)
{
    return Format.Of(format).PriceOn(day);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** wybierz tę ścieżkę, gdy dochodzą formaty (4DX, ScreenX) z własnymi wyjątkami od reguł, a kalendarz jest stabilny. Porównaj `scripts/warsztat.sh --lang cs diff m5/s20 1 3` z `diff m5/s20 1 2`.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s20 1 3` (alternatywa dla kroku 2; `diff 2 3` pokazuje zamianę ścieżki)

### Rozwiązanie i uzasadnienie

Kryterium wyboru: **rodzaj zmienności, nie kształt kodu**.

| Co zmienia się często | Struktura | Co jest tanie |
| --- | --- | --- |
| reguły dnia, kampanie, kalendarz | Strategy (ścieżka A) | nowa polityka, wymiana kalendarza w teście |
| formaty i ich wyjątki | typ z zachowaniem (ścieżka B) | nowy format w jednym miejscu |
| nic, tabela stabilna | krok 1 wystarczy | czytelność bez nowych typów |

Obie ścieżki są równoważne behawioralnie - test to potwierdza. Różnią się kosztem **przyszłej** zmiany. Gdy nie wiemy, która oś będzie się zmieniać, zostajemy na kroku 1.

### Pułapki

- Wybór wzorca "bo ładniej" bez historii zmian (git log, backlog) - zgadujemy przyszłość.
- Obie ścieżki naraz (Strategy dnia w typie formatu) - podwójna ceremonia dla dwóch stabilnych osi.
- Reguła zależna od obu osi naraz ("IMAX we wtorek bez zniżki") - łamie założenie niezależności z kroku 1; wtedy tabela może być lepsza niż wzorzec.

### Pytanie do sali

Biznes zapowiada "IMAX we wtorek bez zniżki". Która ścieżka przyjmie to łatwiej i dlaczego?

## Scena s21. Wzorzec jako decyzja odwracalna - rozliczenie z dystrybutorem

**Temat ze slajdów:** 17.5. Wzorzec jako decyzja odwracalna; 17.2. Refaktoryzacje, które usuwają wzorzec; 5.2. Kiedy wzorzec się nie opłaca
**Namespace:** `Training.Workshop.M5.S21ReversiblePattern` · **Test:** `scripts/warsztat.sh --lang cs test m5/s21` (`S21EquivalenceTest`, `S21FestivalVariantTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** Rozliczenie z dystrybutorem ma dwa modele w jednym `switch`, w tym festiwalowy z obcego systemu w groszach. Wprowadzamy Strategy z Adapterem, a gdy umowy festiwalowe wygasają, usuwamy wariant i cały wzorzec.

**Zasada:** Wzorzec to odpowiedź na konkretne siły (tu: dwa istniejące warianty i obcy interfejs), a nie kod do skopiowania. Strategy opłaca się dla rodziny wymiennych algorytmów, Adapter dla realnego tłumaczenia obcego modelu, a usunięcie wzorca jest równie poprawną refaktoryzacją jak jego wprowadzenie.

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

**Zmiany:**

1. Utwórz nowy plik `ISettlementModel.cs`: interfejs z metodą `decimal Payout(Deal deal, int week, decimal ticketRevenue)`.
2. Gałąź `PERCENT` przenieś do nowej klasy `PercentageModel : ISettlementModel` (wyodrębnienie klasy); gwarancja ląduje w niej jako stała `MinimumGuarantee = 500.00m`. Gałąź `FESTIVAL` przenieś do `FestivalFeeAdapter : ISettlementModel`, który dostaje `FestivalTariffClient` w konstruktorze i konwertuje grosze (`/ 100m`).
3. W `DistributorSettlement` zamień `switch` na słownik `IReadOnlyDictionary<string, ISettlementModel>` z wpisami "PERCENT" i "FESTIVAL"; `Payout` wybiera model przez `TryGetValue`, a nieznany model rzuca ten sam `ArgumentException`. Pole `_festival` znika.

**Przed:**

```csharp
switch (deal.Model)
{
    case "PERCENT":
    {
        var percent = week == 1 ? 50 : week == 2 ? 40 : 35;
        var share = Math.Round(ticketRevenue * percent / 100, 2, MidpointRounding.AwayFromZero);
        return Math.Max(share, 500.00m);
    }
    case "FESTIVAL":
    {
        var cents = _festival.WeeklyFeeInCents(deal.Title, week);
        return cents / 100m;
    }
    default:
        throw new ArgumentException("nieznany model: " + deal.Model);
}
```

**Po (`ISettlementModel.cs`, nowy plik):**

```csharp
/// <summary>Strategy - wspólny kontrakt modeli rozliczeń.</summary>
public interface ISettlementModel
{
    decimal Payout(Deal deal, int week, decimal ticketRevenue);
}
```

**Po (`PercentageModel.cs`, nowy plik):**

```csharp
public sealed class PercentageModel : ISettlementModel
{
    private const decimal MinimumGuarantee = 500.00m;

    public decimal Payout(Deal deal, int week, decimal ticketRevenue)
    {
        var percent = week == 1 ? 50 : week == 2 ? 40 : 35;
        var share = Math.Round(ticketRevenue * percent / 100, 2, MidpointRounding.AwayFromZero);
        return Math.Max(share, MinimumGuarantee);
    }
}
```

**Po (`FestivalFeeAdapter.cs`, nowy plik):**

```csharp
public sealed class FestivalFeeAdapter : ISettlementModel
{
    private readonly FestivalTariffClient _client;

    public FestivalFeeAdapter(FestivalTariffClient client)
    {
        _client = client;
    }

    public decimal Payout(Deal deal, int week, decimal ticketRevenue)
    {
        return _client.WeeklyFeeInCents(deal.Title, week) / 100m;
    }
}
```

**Po (`DistributorSettlement.cs`):**

```csharp
private readonly IReadOnlyDictionary<string, ISettlementModel> _models = new Dictionary<string, ISettlementModel>
{
    ["PERCENT"] = new PercentageModel(),
    ["FESTIVAL"] = new FestivalFeeAdapter(new FestivalTariffClient()),
};

public decimal Payout(Deal deal, int week, decimal ticketRevenue)
{
    if (!_models.TryGetValue(deal.Model, out var model))
    {
        throw new ArgumentException("nieznany model: " + deal.Model);
    }
    return model.Payout(deal, week, ticketRevenue);
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s21` - 18 testów zielonych; `Step1AdapterPaysTheFestivalFeeConvertedFromCents` sprawdza 300.00 i 150.00.
**Co powiedzieć:** uzasadnienie to dwa **istniejące** warianty z różnymi właścicielami i obcy interfejs. Adapter ma realną pracę (konwersja jednostek), nie jest pustym przekazaniem 1:1.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s21 0 1`

### Krok 2: Wariant znika - Safe Delete adaptera

**Zmiany:**

1. Umowy festiwalowe wygasły: usuń wpis `"FESTIVAL"` ze słownika w `DistributorSettlement`.
2. Usuń plik `FestivalFeeAdapter.cs` (bezpieczne usunięcie - po punkcie 1 nikt już go nie używa).

**Przed:**

```csharp
private readonly IReadOnlyDictionary<string, ISettlementModel> _models = new Dictionary<string, ISettlementModel>
{
    ["PERCENT"] = new PercentageModel(),
    ["FESTIVAL"] = new FestivalFeeAdapter(new FestivalTariffClient()),
};
```

**Po:**

```csharp
private readonly IReadOnlyDictionary<string, ISettlementModel> _models = new Dictionary<string, ISettlementModel>
{
    ["PERCENT"] = new PercentageModel(),
};
```

**Uruchom:** test zielony; `AfterTheVariantIsGoneFestivalDealsAreRejected` - to świadoma zmiana zachowania, dlatego sprawdzana osobno, a nie testem równoważności.
**Co powiedzieć:** zostaje Strategy z jedną implementacją i słownikiem z jednym wpisem - klasyczny sygnał nadmiaru wzorca ze slajdu 17.1.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s21 1 2`

### Krok 3: Usunięcie wzorca

**Zmiany:**

1. Wstaw ciało `PercentageModel.Payout` do `DistributorSettlement.Payout` (włączenie metody w miejsce wywołania); zamiast `TryGetValue` warunek `deal.Model != "PERCENT"` rzuca ten sam wyjątek.
2. Przenieś stałą `MinimumGuarantee` do `DistributorSettlement`.
3. Usuń słownik `_models` oraz pliki `PercentageModel.cs` i `ISettlementModel.cs` - oba typy znikają.

**Przed:**

```csharp
public decimal Payout(Deal deal, int week, decimal ticketRevenue)
{
    if (!_models.TryGetValue(deal.Model, out var model))
    {
        throw new ArgumentException("nieznany model: " + deal.Model);
    }
    return model.Payout(deal, week, ticketRevenue);
}
```

**Po:**

```csharp
private const decimal MinimumGuarantee = 500.00m;

public decimal Payout(Deal deal, int week, decimal ticketRevenue)
{
    if (deal.Model != "PERCENT")
    {
        throw new ArgumentException("nieznany model: " + deal.Model);
    }
    var percent = week == 1 ? 50 : week == 2 ? 40 : 35;
    var share = Math.Round(ticketRevenue * percent / 100, 2, MidpointRounding.AwayFromZero);
    return Math.Max(share, MinimumGuarantee);
}
```

**Uruchom:** test zielony - model procentowy rozlicza się tak samo przed wzorcem, ze wzorcem i po jego usunięciu.
**Co powiedzieć:** usunięcie wzorca jest równie poprawną refaktoryzacją jak jego wprowadzenie. Gdy wróci drugi model, przywrócimy Strategy tymi samymi krokami w przód.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s21 2 3`

### Rozwiązanie i uzasadnienie

Wzorzec był odpowiedzią na konkretne siły (dwa warianty, obcy interfejs). Gdy siły zniknęły, zniknął też wzorzec. Test równoważności chroni oba kierunki.

### Pułapki

- Zostawienie interfejsu "bo może wróci" - to YAGNI w drugą stronę (moduł 2, scena s01).
- Adapter, który tylko przekazuje wywołania 1:1 bez tłumaczenia modelu, błędów czy jednostek.
- Dopasowanie obcego klienta na siłę do kontraktu, którego obietnic nie spełnia (np. determinizm przy wywołaniu sieciowym).

### Pytanie do sali

Który wzorzec w waszym kodzie przeżył powód, dla którego go wprowadzono? Co by kosztowało jego usunięcie?

## Scena s22. Remove Middle Man - fasada kina

**Temat ze slajdów:** 17.3. Usunięcie pośrednika; 17.2. Refaktoryzacje, które usuwają wzorzec
**Namespace:** `Training.Workshop.M5.S22MiddleMan` · **Test:** `scripts/warsztat.sh --lang cs test m5/s22` (`S22EquivalenceTest`)
**Czas:** ~8 min

### W skrócie

**Co robimy:** `CinemaFacade` prawie wszystko deleguje 1:1 do `ScreeningCatalog`, ale w `FreeSeats` po cichu zamienia nieznany seans na 0 wolnych miejsc. Przenosimy to tłumaczenie do `SeatBadge`, migrujemy obu klientów do katalogu po jednym i usuwamy fasadę.

**Zasada:** Remove Middle Man usuwa pośrednika, który tylko przekazuje wywołania dalej, i pozwala klientom rozmawiać bezpośrednio z właściwym obiektem - ruch odwrotny to Hide Delegate. Najpierw sprawdzamy, czy pośrednik nie robi czegoś więcej (autoryzacja, transakcja, telemetria, retry, translacja błędów), bo fasada modułu czy seam testowy mogą być wartościowe.

**Efekt:** Klienci zależą bezpośrednio od `ScreeningCatalog`, a zachowanie "nieznany seans = WYPRZEDANE" zostało zachowane w jedynym miejscu, które go potrzebuje. To dziwne zachowanie jest teraz widoczne i czeka na osobną decyzję.

**Różnica względem Javy:** `NoSuchElementException` to `KeyNotFoundException`, a `LinkedHashMap` w katalogu to `OrderedDictionary<string, Screening>` (.NET 9+), żeby kolejność tablicy seansów była gwarantowana.

### Co widzimy

`CinemaFacade` deleguje 1:1 do `ScreeningCatalog` - prawie. `FreeSeats` po cichu tłumaczy `KeyNotFoundException` na 0, więc `SeatBadge` pokazuje "WYPRZEDANE" dla nieznanego seansu. Dwóch klientów: `SeatBadge` i `DailyBoard`.

```csharp
public int FreeSeats(string id)
{
    try
    {
        return _catalog.FreeSeats(id);
    }
    catch (KeyNotFoundException)
    {
        return 0;
    }
}
```

### Krok 1: pośrednik staje się czystym forwarderem

**Zmiany:**

1. W `SeatBadge` dodaj prywatną metodę `FreeSeats(id)` z `try/catch` przeniesionym z fasady (`KeyNotFoundException` tłumaczony na 0); `Badge` woła teraz tę metodę. To jedyny klient, który polega na tłumaczeniu.
2. W `CinemaFacade.FreeSeats` usuń `try/catch` - zostaje zwykłe delegowanie `return _catalog.FreeSeats(id);`.

**Przed:**

```csharp
// CinemaFacade
public int FreeSeats(string id)
{
    try
    {
        return _catalog.FreeSeats(id);
    }
    catch (KeyNotFoundException)
    {
        return 0;
    }
}
```

**Po:**

```csharp
// SeatBadge
private int FreeSeats(string id)
{
    try
    {
        return _cinema.FreeSeats(id);
    }
    catch (KeyNotFoundException)
    {
        return 0;
    }
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s22` - przypadek "nieznany seans wygląda jak wyprzedany" nadal zielony.
**Co powiedzieć:** najpierw sprawdź, co pośrednik naprawdę robi: autoryzacja, transakcja, telemetria, retry, translacja błędów. Tu była translacja - przenosimy ją do klienta, zanim usuniemy pośrednika.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s22 0 1`

### Krok 2: pierwszy klient rozmawia z katalogiem

**Zmiany:**

1. W konstruktorze `SeatBadge` zmień typ parametru z `CinemaFacade` na `ScreeningCatalog` (zmiana sygnatury), a pole przemianuj z `_cinema` na `_catalog`.
2. W `Badge` i prywatnym `FreeSeats` wywołania idą teraz na `_catalog` - metody katalogu mają te same nazwy i sygnatury.

**Przed:**

```csharp
private readonly CinemaFacade _cinema;

public SeatBadge(CinemaFacade cinema)
{
    ArgumentNullException.ThrowIfNull(cinema);
    _cinema = cinema;
}
```

**Po:**

```csharp
private readonly ScreeningCatalog _catalog;

public SeatBadge(ScreeningCatalog catalog)
{
    ArgumentNullException.ThrowIfNull(catalog);
    _catalog = catalog;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** jeden klient naraz. `DailyBoard` wciąż używa fasady i działa.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s22 1 2`

### Krok 3: ostatni klient i Safe Delete fasady

**Zmiany:**

1. Ten sam ruch w `DailyBoard`: konstruktor przyjmuje `ScreeningCatalog`, pole `_cinema` staje się `_catalog`, a wywołanie `_cinema.Screenings()` zamień na `_catalog.All()`.
2. Usuń plik `CinemaFacade.cs` (bezpieczne usunięcie - fasada nie ma już klientów).

**Przed:**

```csharp
private readonly CinemaFacade _cinema;
...
public string Render()
{
    return string.Join("\n", _cinema.Screenings()
        .Select(screening => screening.Id + " " + screening.Title + " " + screening.Format));
}
```

**Po:**

```csharp
return string.Join("\n", _catalog.All()
    .Select(screening => screening.Id + " " + screening.Title + " " + screening.Format));
```

**Uruchom:** test zielony.
**Co powiedzieć:** odwrotny ruch to Hide Delegate. Fasada modułu albo seam testowy mogą być wartościowe - usuwamy pośrednika, który nic nie wnosi.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s22 2 3`

### Rozwiązanie i uzasadnienie

`Step3`: dwaj klienci zależą od `ScreeningCatalog`, zachowanie dla nieznanego seansu zachowane w jedynym miejscu, które go potrzebuje. Test `CatalogItselfRejectsUnknownScreening` pokazuje, dlaczego krok 1 był konieczny.

### Pułapki

- Wstawienie wszystkich metod fasady w miejsca wywołań naraz, bez sprawdzenia, że któraś tłumaczy błędy.
- Usunięcie fasady, która jest granicą modułu (klienci z innych przestrzeni nazw albo assembly zaczną zależeć od szczegółów).
- Utrwalenie dziwnego zachowania ("nieznany = wyprzedany") bez notatki - to kandydat na osobną decyzję.

### Pytanie do sali

Czy "nieznany seans = WYPRZEDANE" to błąd? Kiedy wolno go poprawić?

## Scena s23. Refaktoryzacja a zmiana kontraktu - zwrot na BigDecimal

**Temat ze slajdów:** 5.3. Refaktoryzacja a zmiana kontraktu; 5.4. Co trzeba zachować przy wprowadzaniu wzorca; 3.10. Bezpieczna pętla refaktoryzacji do wzorca
**Namespace:** `Training.Workshop.M5.S23ContractChange` · **Test:** `scripts/warsztat.sh --lang cs test m5/s23` (`S23ContractTest`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** `RefundCalculator.Refund` liczy zwrot na `double`, a kontraktem jest też format z dwoma miejscami po przecinku. Robimy jedną czystą refaktoryzację, potem pokazujemy naiwne przejście na `decimal` (w C# odpowiednik `BigDecimal` z tytułu sceny), które zmienia dwie rzeczy naraz, i na koniec zostawiamy tylko uzgodnioną zmianę.

**Zasada:** Refaktoryzacja zachowuje obserwowalne zachowanie w przyjętej granicy, a kryterium brzmi: klient nie dostrzega żadnej nieuzgodnionej różnicy. Zmiana typu, zaokrąglenia, formatu czy wyjątku to zmiana kontraktu, którą robimy i zatwierdzamy osobno od ruchów strukturalnych, a nie "przy okazji".

**Efekt:** Zachowanie świadomie się zmienia w jednym punkcie: 64.35 anulowane 2 godziny przed seansem daje 29.18 zamiast 29.17, zatwierdzone w osobnym commicie. Format "0.00" zostaje, a naiwny krok 2 nie powinien trafić do repozytorium.

**Różnica względem Javy:** `BigDecimal` to `decimal`. Pułapka formatu jest ta sama, ale mechanizm jest C#-owy: `decimal` niesie własną skalę, a `Math.Round` **nie dopisuje zer**, więc `Math.Max(..., 0m)` drukuje się jako `0`. Dlatego w kroku 3 poza przestawieniem zaokrąglenia za `Max()` format "0.00" przywraca jawne `ToString("F2")`. `(decimal)double` zastępuje `BigDecimal.valueOf(double)` (dla danych testu wynik identyczny), a komunikaty z `assertEquals(..., message)` są komentarzami nad `Assert.Equal`.

### Co widzimy

`RefundCalculator.Refund` liczy zwrot na `double` - dokładnie jak w legacy: 100%, 50% albo 0%, minus 3.00, nie poniżej zera, zaokrąglenie `Math.Floor(x * 100 + 0.5) / 100.0` (odpowiednik javowego `Math.round`, bo domyślne `Math.Round` w .NET zaokrągla bankiersko), format `"F2"`. Kontraktem jest także **format**: zawsze dwa miejsca po przecinku.

```csharp
refund = refund - 3.00;
if (refund < 0)
{
    refund = 0;
}
refund = Math.Floor(refund * 100 + 0.5) / 100.0;
return refund.ToString("F2", CultureInfo.InvariantCulture);
```

### Krok 1: czysta refaktoryzacja

**Zmiany:**

1. Z łańcucha `if/else` wyboru udziału wydziel metodę `Share(minutesBeforeStart)` (wyodrębnienie metody) zwracającą `double`: 0, 1.0 albo 0.5.
2. W `Refund` zapisz potrącenie jednym wyrażeniem `ticketsPaid * Share(minutesBeforeStart) - 3.00`; próg zera, zaokrąglenie i format zostają bez zmian.

**Przed:**

```csharp
double refund;
if (minutesBeforeStart <= 0)
{
    refund = 0;
}
else if (minutesBeforeStart >= 24 * 60)
{
    refund = ticketsPaid;
}
else
{
    refund = ticketsPaid * 0.5;
}
refund = refund - 3.00;
```

**Po:**

```csharp
public string Refund(double ticketsPaid, long minutesBeforeStart)
{
    double refund = ticketsPaid * Share(minutesBeforeStart) - 3.00;
    if (refund < 0)
    {
        refund = 0;
    }
    refund = Math.Floor(refund * 100 + 0.5) / 100.0;
    return refund.ToString("F2", CultureInfo.InvariantCulture);
}

private static double Share(long minutesBeforeStart)
{
    if (minutesBeforeStart <= 0)
    {
        return 0;
    }
    if (minutesBeforeStart >= 24 * 60)
    {
        return 1.0;
    }
    return 0.5;
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s23` - wszystko zielone, łącznie z przypadkami brzegowymi (29.17 i 0.00 w `RefactoringStepKeepsHistoricalRoundingAndFormat`).
**Co powiedzieć:** `x * 1.0 == x` w `double` - arytmetyka jest identyczna co do bitu. To jest refaktoryzacja.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s23 0 1`

### Krok 2: "przy okazji" double na decimal

**Zmiany:**

1. Pokaż naiwny ruch: zmień typ zwracany `Share` na `decimal` (`0m`, `1m`, `0.5m`) i dodaj stałą `Fee = 3.00m`.
2. W `Refund` policz wszystko jednym wyrażeniem: `Math.Max(Math.Round((decimal)ticketsPaid * Share(minutesBeforeStart) - Fee, 2, MidpointRounding.AwayFromZero), 0m)`.
3. Zwróć `refund.ToString(CultureInfo.InvariantCulture)` - bez jawnego formatu.

**Przed:**

```csharp
double refund = ticketsPaid * Share(minutesBeforeStart) - 3.00;
if (refund < 0)
{
    refund = 0;
}
refund = Math.Floor(refund * 100 + 0.5) / 100.0;
return refund.ToString("F2", CultureInfo.InvariantCulture);
```

**Po:**

```csharp
var refund = Math.Max(
    Math.Round((decimal)ticketsPaid * Share(minutesBeforeStart) - Fee, 2, MidpointRounding.AwayFromZero),
    0m);
return refund.ToString(CultureInfo.InvariantCulture);
```

**Uruchom:** test zwykłych zwrotów zielony (`OrdinaryRefundsNeverChange`), ale `ByTheWayStepChangedTwoThingsAtOnce` dokumentuje dwie zmiany: 64.35 zapłacone, anulowanie 2 h przed seansem daje 29.18 zamiast 29.17, a zwrot po starcie to "0" zamiast "0.00".
**Co powiedzieć:** krok wyglądał na porządki, a zmienił dwa elementy kontraktu. Pierwszy (grosz) można uzasadnić regułą domeny, drugi (format) to czysta regresja - paragon i mail pokażą "Zwrot: 0".
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s23 1 2`

### Krok 3: świadoma decyzja

**Zmiany:**

1. Przestaw kolejność operacji: `Math.Max(..., 0m)` do środka, a `Math.Round(..., 2, MidpointRounding.AwayFromZero)` na zewnątrz - zaokrąglamy po obcięciu do zera.
2. Przywróć jawny format wyniku: `refund.ToString("F2", CultureInfo.InvariantCulture)` - "0.00" wraca.
3. Różnicę groszową zatwierdź jako zmianę kontraktu: komentarz XML z decyzją, nowe oczekiwanie w teście, osobny commit.

**Przed:**

```csharp
var refund = Math.Max(
    Math.Round((decimal)ticketsPaid * Share(minutesBeforeStart) - Fee, 2, MidpointRounding.AwayFromZero),
    0m);
return refund.ToString(CultureInfo.InvariantCulture);
```

**Po:**

```csharp
var refund = Math.Round(
    Math.Max((decimal)ticketsPaid * Share(minutesBeforeStart) - Fee, 0m),
    2, MidpointRounding.AwayFromZero);
return refund.ToString("F2", CultureInfo.InvariantCulture);
```

**Uruchom:** test zielony: `DeliberateStepChangesOnlyTheApprovedRounding`.
**Co powiedzieć:** kryterium refaktoryzacji to "klient nie dostrzega żadnej **nieuzgodnionej** różnicy". Różnicę w groszach uzgodniliśmy, różnicy w formacie nie. W C# samo przestawienie `Max` nie wystarczy: `Math.Round(0m, 2)` to dalej `0`, bo zaokrąglenie nie podnosi skali - format trzeba napisać jawnie.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s23 2 3`

### Rozwiązanie i uzasadnienie

`Step3/RefundCalculator`: `decimal` i `AwayFromZero` (HALF_UP) zgodnie z regułą domeny, format bez zmian. Historia commitów: refaktoryzacja (krok 1), zmiana typu i reguły zaokrąglenia jako jawna zmiana kontraktu (krok 3). Krok 2 nie powinien trafić do repozytorium.

### Pułapki

- Test tylko na "okrągłych" kwotach - `double` i `decimal` dają ten sam wynik i zmiana przechodzi niezauważona.
- Liczenie dalej na `double` i konwersja na `decimal` dopiero na końcu (albo `Math.Round` na `double`) - binarne rozwinięcie (64.349999...) daje inny wynik zaokrąglenia. Na `decimal` przechodzimy na wejściu.
- `decimal` porównuje wartości (`0m == 0.00m` to `true`), ale `ToString()` drukuje skalę ("0" i "0.00"). Test porównujący liczby nie zobaczy zmiany formatu - test porównujący tekst tak.
- `Math.Round(x, 2)` bez trybu zaokrągla bankiersko (`ToEven`) - inny wynik niż HALF_UP np. dla 0.125 (0.12 zamiast 0.13), więc tryb podajemy zawsze jawnie.
- Zmiana typu wyjątku, kolejności efektów albo formatu "bo i tak ruszamy ten kod".

### Pytanie do sali

Kto zatwierdza zmianę kontraktu o 1 grosz: programista, księgowość czy klient API?

## Scena s24. Rename - nazwy, które żyją poza C#

**Temat ze slajdów:** 3.6. Refaktoryzacje automatyczne w IDE: co dają, a czego nie widzą; 3.7. Zmiana nazwy i wyodrębnienie metody w drodze do wzorca
**Namespace:** `Training.Workshop.M5.S24Rename` · **Test:** `scripts/warsztat.sh --lang cs test m5/s24` (`S24EquivalenceTest`, `S24RenameLimitsTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** W `SalesReport` nazwy `Calc2`, `s`, `m`, `t` nic nie mówią, ale nazwa metody żyje w konfiguracji, a nazwy właściwości rekordu trafiają refleksją do nagłówka CSV. Zmieniamy nazwy od najbezpieczniejszych lokalnych do tych, które są kontraktem zewnętrznym.

**Zasada:** Rename nadaje nazwę opisującą rolę w kontekście, a nie typ ani implementację. Dla zmiennych lokalnych IDE robi to bezpiecznie, ale nazwa użyta w konfiguracji, refleksji, JSON czy ORM jest częścią API i wymaga strategii migracji. IDE nie widzi takich użyć, więc nie mylić automatycznej zmiany nazwy z bezpieczną zmianą kontraktu.

**Efekt:** Kod czyta się po nazwach ról, a oba kontrakty zostają nietknięte: stara nazwa `Calc2` jako przestarzały delegat, nagłówek CSV jako jawna stała. Koszt to delegat, którego nie wolno usunąć, dopóki ktoś nie zmieni konfiguracji na serwerach.

**Różnica względem Javy:** metoda z konfiguracji nazywa się w C# `Calc2` (PascalCase), więc wpis to `report.method=Calc2`. Rekord `Line` ma w `Start`, `Step1` i `Step2` celowo właściwości `t`, `n`, `d` z małej litery (część zapachu), dzięki czemu nagłówek z refleksji (`GetProperties()`) jest taki sam jak w Javie: `t;n;d`. `@Deprecated` to `[Obsolete]`, a `Properties` to ręczne parsowanie `klucz=wartość`. `GetMethod` zwraca w C# `null` zamiast rzucać, więc `ReportJob` sam rzuca `MissingMethodException` i opakowuje go w `InvalidOperationException`.

### Co widzimy

`SalesReport.Calc2(IReadOnlyList<Sale> s, bool flag)` składa CSV dla dystrybutora: `m`, `x`, `l`, `b`, a wiersz to `Line(string t, int n, decimal d)`. Dwie nazwy żyją poza C#:

```csharp
// ReportJob - w produkcji plik report.properties na serwerze
internal const string Config = """
    report.method=Calc2
    report.onlineOnly=true
    """;
...
var method = typeof(SalesReport).GetMethod(name, [typeof(IReadOnlyList<Sale>), typeof(bool)])
    ?? throw new MissingMethodException(nameof(SalesReport), name);

// SalesReport - nagłówek CSV liczony refleksją z nazw właściwości rekordu
return string.Join(";", typeof(Line).GetProperties().Select(p => p.Name));
```

### Krok 1: Rename zmiennych lokalnych i parametrów

**Zmiany:**

1. Zmień nazwy parametrów `Calc2` (zmiana nazwy): `s` na `sales`, `flag` na `onlineOnly`.
2. Zmień nazwy zmiennych lokalnych: `m` na `linesByTitle`, `x` na `sale`, `l` w pętli agregującej na `previous`, a w pętli składającej CSV i w parametrze `Row` na `line`, `b` na `csv`; w `Header` i `Row`: `v` na `values`, `c` i `p` na `property`.

**Przed:**

```csharp
public string Calc2(IReadOnlyList<Sale> s, bool flag)
{
    var m = new SortedDictionary<string, Line>(StringComparer.Ordinal);
    foreach (var x in s)
    {
        if (flag && !x.Online)
        {
            continue;
        }
        if (!m.TryGetValue(x.Title, out var l))
        {
            m[x.Title] = new Line(x.Title, x.Tickets, x.Amount);
        }
        ...
    }
    ...
}
```

**Po:**

```csharp
public string Calc2(IReadOnlyList<Sale> sales, bool onlineOnly)
{
    var linesByTitle = new SortedDictionary<string, Line>(StringComparer.Ordinal);
    foreach (var sale in sales)
    {
        if (onlineOnly && !sale.Online)
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s24` - 14 testów zielonych.
**Co powiedzieć:** zasięg lokalny, zero użyć poza metodą, więc IDE robi to w stu procentach bezpiecznie. Od tego zaczynamy każde porządkowanie, bo tanie nazwy ułatwiają następne kroki.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s24 0 1`

### Krok 2: Rename metody z delegatem dla starej nazwy

**Zmiany:**

1. Najpierw na żywo naiwnie: zmień nazwę metody `Calc2` na `RevenueCsv` wszędzie w kodzie, bez ruszania tekstów. Uruchom test: `EveryStepRunsTheConfiguredJob` jest czerwony (`InvalidOperationException: Zadanie raportu nie działa: ...`, w środku `MissingMethodException` dla `Calc2`) - automat nie wie, że tekst w konfiguracji to nazwa metody. Cofnij zmianę.
2. Teraz poprawnie: skopiuj metodę i nazwij kopię `RevenueCsv`.
3. Ciało `Calc2` zamień na delegowanie `return RevenueCsv(sales, onlineOnly);`, dodaj atrybut `[Obsolete(...)]` i komentarz XML z warunkiem usunięcia.

**Przed:**

```csharp
/// <summary>Wywoływana także refleksyjnie przez ReportJob - nazwa metody jest w konfiguracji.</summary>
public string Calc2(IReadOnlyList<Sale> sales, bool onlineOnly)
{
    var linesByTitle = new SortedDictionary<string, Line>(StringComparer.Ordinal);
    ...
}
```

**Po:**

```csharp
/// <summary>
/// Stara nazwa z konfiguracji report.properties (report.method=Calc2).
/// Usunąć dopiero, gdy żaden serwer nie ma jej w konfiguracji.
/// </summary>
[Obsolete("Użyj RevenueCsv - Calc2 zostaje tylko dla konfiguracji report.properties")]
public string Calc2(IReadOnlyList<Sale> sales, bool onlineOnly)
{
    return RevenueCsv(sales, onlineOnly);
}

public string RevenueCsv(IReadOnlyList<Sale> sales, bool onlineOnly)
{
```

**Uruchom:** test zielony, zadanie z konfiguracji działa. Test, który wciąż woła `Calc2` wprost, wyłącza ostrzeżenie kompilatora przez `#pragma warning disable CS0618` (odpowiednik `@SuppressWarnings("deprecation")`).
**Co powiedzieć:** konfiguracja leży na serwerach, poza naszym repozytorium, więc nie zmienimy jej w tym samym commicie. Stara nazwa zostaje jako przestarzały delegat, dopóki ktoś nie zmieni konfiguracji wszędzie. To jest strategia migracji ze slajdu.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s24 1 2`

### Krok 3: Rename właściwości rekordu po odcięciu kontraktu CSV

**Zmiany:**

1. Najpierw naiwnie: zmień nazwę właściwości `t` rekordu `Line` na `Title` (zmiana parametru rekordu zmienia też wygenerowaną z niego właściwość). Test pokaże nagłówek `Title;n;d` zamiast `t;n;d`: plik dla dystrybutora właśnie się zmienił. Cofnij zmianę.
2. Teraz poprawnie: usuń refleksyjne `Header()` i wprowadź jawną stałą `CsvHeader = "t;n;d"` używaną w `RevenueCsv`, a `Row` zamień na jawne składanie wiersza z kwotą formatowaną przez `ToString(CultureInfo.InvariantCulture)`; test zielony.
3. Dopiero potem zmień nazwy właściwości rekordu: `t`, `n`, `d` na `Title`, `Tickets`, `Revenue` (zmiana obejmie też użycia `previous.n` i `previous.d` w `RevenueCsv`).

**Przed:**

```csharp
public sealed record Line(string t, int n, decimal d);
...
private static string Header()
{
    return string.Join(";", typeof(Line).GetProperties().Select(property => property.Name));
}

private static string Row(Line line)
{
    var values = new List<string>();
    foreach (var property in typeof(Line).GetProperties())
    {
        values.Add(Convert.ToString(property.GetValue(line), CultureInfo.InvariantCulture) ?? "null");
    }
    return string.Join(";", values);
}
```

**Po:**

```csharp
/// <summary>Nagłówek uzgodniony z dystrybutorem - kontrakt zewnętrzny, NIE nazwy właściwości w C#.</summary>
internal const string CsvHeader = "t;n;d";

public sealed record Line(string Title, int Tickets, decimal Revenue);

private static string Row(Line line)
{
    return line.Title + ";" + line.Tickets + ";" + line.Revenue.ToString(CultureInfo.InvariantCulture);
}
```

**Uruchom:** test zielony, także `S24RenameLimitsTest.ReflectionTurnsCSharpNamesIntoTheCsvHeader`, który pokazuje, że refleksja na nowym rekordzie dałaby `Title;Tickets;Revenue`.
**Co powiedzieć:** nazwa w C# i nazwa w kontrakcie zewnętrznym to dwie różne rzeczy. Dopóki są sklejone refleksją, każdy Rename jest zmianą API. Przy okazji: jawny wiersz formatuje kwotę z `InvariantCulture`, tak jak robiła to refleksja (`Convert.ToString(..., CultureInfo.InvariantCulture)`), inaczej na serwerze z `pl-PL` w pliku pojawiłby się przecinek.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s24 2 3`

### Rozwiązanie i uzasadnienie

`Step3`: wszystkie nazwy mówią o roli (`RevenueCsv`, `onlineOnly`, `linesByTitle`, `Line.Revenue`), a dwa kontrakty zewnętrzne (nazwa metody w konfiguracji i nagłówek CSV) są zachowane jawnie: przez delegat i przez stałą. Oba testy równoważności (wywołanie z C# i zadanie z konfiguracji) są zielone na każdym etapie.

### Pułapki

- Poleganie na automatycznej zmianie wystąpień nazwy także w tekstach. Czasem pomaga, ale nie obejmuje plików poza projektem (konfiguracja na serwerze, `appsettings.*.json` w innym repozytorium) ani nazw składanych dynamicznie (`"Calc" + version`).
- Inne miejsca, w których nazwa jest kontraktem: JSON (`System.Text.Json`, Newtonsoft), kolumny i tabele EF Core bez jawnego mapowania, wiązanie konfiguracji (`IConfiguration.Bind`, `IOptions<T>` wiążą po nazwach właściwości), widoki Razor, nazwy w DI rejestrowane po nazwie, logi parsowane przez monitoring. `nameof(...)` jest bezpieczne, bo Rename je zmienia, ale wtedy zmienia też tekst, który ktoś może czytać.
- Usunięcie delegatu `Calc2` "bo nikt go nie woła". Wyszukiwanie użyć w kodzie go nie znajdzie, bo wołają go tylko refleksja i konfiguracja.

### Pytanie do sali

Jak w waszym systemie sprawdzić, czy nazwa klasy albo metody nie jest użyta w konfiguracji, bazie danych lub innym repozytorium?

## Scena s25. Extract Method - przepływ danych i wiele wyjść

**Temat ze slajdów:** 3.7. Zmiana nazwy i wyodrębnienie metody w drodze do wzorca
**Namespace:** `Training.Workshop.M5.S25ExtractMethod` · **Test:** `scripts/warsztat.sh --lang cs test m5/s25` (`S25EquivalenceTest`)
**Czas:** ~15 min

### W skrócie

**Co robimy:** `TicketSummary.Describe` liczy cenę, sumę i liczbę miejsc VIP, a potem składa dokument, i to wszystko w jednej metodzie z komentarzami zamiast nazw. Wydzielamy bloki do metod, a pętlę z dwoma wyjściami najpierw rozdzielamy przez Split Loop.

**Zasada:** Extract Method przenosi spójny fragment do metody, gdy nazwa wyrazi intencję lepiej niż szczegóły, a sama długość nie jest powodem. Kluczowa jest analiza przepływu danych: co wchodzi jako parametr, co wychodzi jako wynik. Gdy fragment ma kilka wyjść, dzielimy go albo tworzymy obiekt wyniku, a nie zwracamy tablicy, parametrów `out` czy mutowalnego holdera.

**Efekt:** Metoda publiczna czyta się jak spis treści, a obliczenia są oddzielone od renderowania, bez zmiany sygnatury ani formatu. Kosztem są dwa przejścia po liście rzędów, pomijalne wobec czytelności.

**Różnica względem Javy:** `base` to w C# słowo kluczowe, więc zmienna ceny bazowej nazywa się `@base`. Suma startuje od `0.00m` (a nie od `0m`), żeby puste zamówienie drukowało `0.00` tak jak `BigDecimal.setScale(2)` w Javie - `decimal` pamięta skalę literału. Automatyczna ekstrakcja metody w C# nie odmawia pracy na fragmencie z dwoma wyjściami: proponuje parametr `out` (albo krotkę), co omawiamy w kroku 2.

### Co widzimy

`TicketSummary.Describe` liczy cenę bazową, sumę i liczbę miejsc VIP, a na końcu składa dokument. Komentarze `// cena bazowa formatu`, `// suma i liczba miejsc VIP`, `// dokument` to naturalne granice metod. Pętla ma jednak **dwa wyjścia**: `subtotal` i `vipSeats`.

```csharp
decimal subtotal = 0.00m;
int vipSeats = 0;
foreach (var row in order.Rows)
{
    decimal price = @base;
    if (row >= 10)
    {
        price = price + 10.00m;
        vipSeats++;
    }
    subtotal = subtotal + price;
}
subtotal = Math.Round(subtotal, 2, MidpointRounding.AwayFromZero);
```

### Krok 1: Extract Method dla bloku z jednym wyjściem

**Zmiany:**

1. Blok pod komentarzem `// cena bazowa formatu` (od deklaracji `decimal @base;` do końca `if` z porankiem) wydziel do metody `BasePrice(order)` (wyodrębnienie metody): jedno wejście (`order`), jedno wyjście (`@base` jako wartość zwracana).
2. W `Describe` zostaje `decimal @base = BasePrice(order);`; zbędny komentarz usuń.

**Przed:**

```csharp
// cena bazowa formatu
decimal @base;
if (order.Format == "IMAX")
{
    @base = 40.00m;
}
else if (order.Format == "3D")
{
    @base = 32.00m;
}
else
{
    @base = 25.00m;
}
if (order.Start.Hour < 12)
{
    @base = @base - 5.00m;
}
```

**Po:**

```csharp
decimal @base = BasePrice(order);
...
private decimal BasePrice(Order order)
{
    decimal @base;
    if (order.Format == "IMAX")
    {
        @base = 40.00m;
    }
    else if (order.Format == "3D")
    {
        @base = 32.00m;
    }
    else
    {
        @base = 25.00m;
    }
    if (order.Start.Hour < 12)
    {
        @base = @base - 5.00m;
    }
    return @base;
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s25` - 16 testów zielonych.
**Co powiedzieć:** blok z jednym wyjściem to najprostszy przypadek, IDE robi to bezpiecznie. Komentarz stał się nazwą metody.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s25 0 1`

### Krok 2: Split Loop, potem dwa razy Extract Method

**Zmiany:**

1. Spróbuj wydzielić całą pętlę (razem z `Math.Round`) w jedną metodę: fragment ma dwie wartości wyjściowe (`subtotal` i `vipSeats`), więc automat zaproponuje jedną jako wynik, a drugą jako parametr `out` albo krotkę. To jest moment dydaktyczny - nie korzystamy z tej propozycji.
2. Zamiast tego skopiuj pętlę (Split Loop): z pierwszej kopii usuń `vipSeats`, z drugiej `subtotal` i `price`. Uruchom test.
3. Każdą pętlę wydziel do metody (wyodrębnienie metody): `Subtotal(order, @base)` z `Math.Round` na końcu oraz `VipSeats(order)`.

**Przed:**

```csharp
decimal subtotal = 0.00m;
int vipSeats = 0;
foreach (var row in order.Rows)
{
    decimal price = @base;
    if (row >= 10)
    {
        price = price + 10.00m;
        vipSeats++;
    }
    subtotal = subtotal + price;
}
subtotal = Math.Round(subtotal, 2, MidpointRounding.AwayFromZero);
```

**Po:**

```csharp
decimal @base = BasePrice(order);
decimal subtotal = Subtotal(order, @base);
int vipSeats = VipSeats(order);
...
private decimal Subtotal(Order order, decimal @base)
{
    decimal subtotal = 0.00m;
    foreach (var row in order.Rows)
    {
        decimal price = @base;
        if (row >= 10)
        {
            price = price + 10.00m;
        }
        subtotal = subtotal + price;
    }
    return Math.Round(subtotal, 2, MidpointRounding.AwayFromZero);
}

private int VipSeats(Order order)
{
    int vipSeats = 0;
    foreach (var row in order.Rows)
    {
        if (row >= 10)
        {
            vipSeats++;
        }
    }
    return vipSeats;
}
```

**Uruchom:** test po Split Loop i po każdej ekstrakcji.
**Co powiedzieć:** zamiast zwracać parę (record z dwoma polami, krotkę albo `out`) rozdzielamy odpowiedzialności. Dwa przejścia po liście to koszt pomijalny wobec czytelności. Jeśli wydajność miałaby znaczenie, mierzymy (np. BenchmarkDotNet), a nie zgadujemy.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s25 1 2`

### Krok 3: Extract Method dla renderowania

**Zmiany:**

1. Blok pod komentarzem `// dokument` wydziel do metody `Render(order, subtotal, vipSeats)` (wyodrębnienie metody).
2. Wstaw zmienną `@base` w miejsce jej jedynego użycia (włączenie zmiennej): `Subtotal(order, BasePrice(order))`.

**Przed:**

```csharp
decimal @base = BasePrice(order);
decimal subtotal = Subtotal(order, @base);
int vipSeats = VipSeats(order);

// dokument
var text = new StringBuilder();
text.Append("BILETY: ").Append(order.Title).Append('\n');
...
return text.ToString();
```

**Po:**

```csharp
public string Describe(Order order)
{
    decimal subtotal = Subtotal(order, BasePrice(order));
    int vipSeats = VipSeats(order);
    return Render(order, subtotal, vipSeats);
}
...
private string Render(Order order, decimal subtotal, int vipSeats)
{
    var text = new StringBuilder();
    text.Append("BILETY: ").Append(order.Title).Append('\n');
    text.Append("Format: ").Append(order.Format)
        .Append(", start ").Append(order.Start.ToString("HH:mm", CultureInfo.InvariantCulture)).Append('\n');
    text.Append("Miejsc: ").Append(order.Rows.Count);
    if (vipSeats > 0)
    {
        text.Append(" (w tym VIP: ").Append(vipSeats).Append(')');
    }
    text.Append('\n');
    text.Append("Razem: ").Append(subtotal.ToString(CultureInfo.InvariantCulture)).Append('\n');
    return text.ToString();
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** metoda publiczna czyta się jak spis treści. Obliczenia są oddzielone od prezentacji, więc przygotowaliśmy grunt pod Extract Class.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s25 2 3`

### Rozwiązanie i uzasadnienie

`Step3/TicketSummary`: cztery prywatne metody, każda z jednym wyjściem. Nie zmieniliśmy sygnatury publicznej ani formatu dokumentu, co potwierdza test równoważności na czterech przypadkach (w tym zamówienie puste i same miejsca VIP).

### Pułapki

- Ekstrakcja bloku, który modyfikuje zmienną lokalną używaną dalej, bez zwrócenia jej wartości. IDE tego pilnuje, ręczne kopiowanie nie.
- Zwracanie tablicy `object[]`, mutowalnego "holdera" albo dorzucanie parametrów `out`/`ref` tylko po to, żeby wyciągnąć dwa wyniki. Automatyczna ekstrakcja zaproponuje `out` sama z siebie - to nie znaczy, że to dobry projekt.
- Nazwa metody opisująca "jak" (`LoopRows`) zamiast "co" (`VipSeats`).

### Pytanie do sali

Kiedy zamiast Split Loop lepiej zwrócić record (albo nazwaną krotkę) z dwoma polami?

## Scena s26. Strategy, Command i State w jednej klasie - pulpit kasjera

**Temat ze slajdów:** 6.1. Strategia, Polecenie czy Stan? Kto wybiera wariant i kiedy; 6.7. Trzy osie w jednej klasie: każda dostaje własny mechanizm; 6.8. Typowe pomyłki przy wyborze
**Namespace:** `Training.Workshop.M5.S26Variability` · **Test:** `scripts/warsztat.sh --lang cs test m5/s26` (`S26EquivalenceTest`, `S26SolutionTest`)
**Czas:** ~15 min

### W skrócie

**Co robimy:** `BoxOfficeDesk.Handle` obsługuje polecenia kasjera BOOK, PAY, USE, CANCEL, UNDO i REPORT jednym `switch` po nazwie operacji, w którym siedzi `switch` po statusie rezerwacji, a w rezerwacji `switch` po typie biletu. Rozplątujemy trzy osie po kolei: cenę biletu jako Strategy (delegat `TicketPricing`), operacje kasjera jako Command z cofaniem (`IDeskCommand` i odbiorca `BookingLedger`), cykl życia rezerwacji jako State (`Booking` z prywatnym typem `State`).

**Zasada:** O mechanizmie decyduje pytanie "kto wybiera wariant i kiedy?", a nie wygląd kodu - wszystkie trzy `switch` wyglądają tak samo. Strategy: wariant wybiera klient na podstawie danych (typ biletu), raz. Command: żądanie z zewnątrz, które trzeba zapisać w dzienniku i umieć cofnąć. State: wariant zmienia sam obiekt w wyniku własnych operacji (CANCEL w NEW zwalnia rezerwację, w PAID oddaje pieniądze). Wprowadzamy jedną oś naraz.

**Efekt:** Nowy typ biletu, nowa operacja i nowy status to zmiany w trzech rozłącznych miejscach. Odpowiedzi pulpitu, stan kasy i dziennik są identyczne w `Start` i w każdym kroku, łącznie z komunikatami błędów, kolejnością walidacji i tym, że nieudane polecenie nie trafia do dziennika.

**Różnica względem Javy:** `Status` to enum w PascalCase (`Status.New`), ale pulpit wypisuje go przez `ToString().ToUpperInvariant()`, więc odpowiedzi są takie same jak w Javie (`B1 NEW 122.00`, `B1=PAID`). Interfejs funkcyjny `TicketPricing` to delegat (jak `DayPolicy` w s20), `LinkedHashMap` to `OrderedDictionary<,>`, a metoda `status(id)` odbiorcy nazywa się `StatusOf(id)`. Prywatny enum Javy z metodami nadpisywanymi w stałych nie ma odpowiednika w C# (enum C# nie ma metod), więc w kroku 3 stan to prywatna klasa abstrakcyjna `State` z jednym obiektem na stan i fabryką `State.Of(Status)`; stany końcowe Used i Cancelled dzielą klasę `FinalState`. Metody odbiorcy i fabryki poleceń `From(args)` są `internal` (w Javie pakietowe).

### Co widzimy

`BoxOfficeDesk` trzyma statusy i kwoty rezerwacji w słownikach, kasę (`_till`) i dziennik rekordów `Entry`. Ta sama operacja CANCEL znaczy co innego w zależności od statusu, a cofanie (UNDO) to drugi `switch` po nazwie operacji zapisanej w dzienniku. Wspólny kontrakt sceny to enum `Status` (New, Paid, Used, Cancelled) - wartość zapisywana w bazie.

```csharp
Status? next = null;
switch (status)
{
    case Status.New:
        switch (operation)
        {
            case "PAY":
                _till = _till.Plus(amount);
                moved = amount;
                next = Status.Paid;
                break;
            case "CANCEL":
                next = Status.Cancelled;
                break;
        }
        break;
    case Status.Paid:
        switch (operation)
        {
            case "USE":
                next = Status.Used;
                break;
            case "CANCEL":
                _till = _till.Minus(amount);
                moved = amount;
                next = Status.Cancelled;
                break;
        }
        break;
    case Status.Used:
    case Status.Cancelled:
        break;
}
```

`S26EquivalenceTest` to sesje kasjera przechodzące przez wszystkie osie naraz: ceny typów N/S/E/C, cykl życia (CANCEL w NEW i w PAID), niedozwolone przejścia, cofanie w odwrotnej kolejności, cofnięcie USE i ponowny CANCEL, błędy, które nie trafiają do dziennika. Przed pierwszym ruchem pokaż na rzutniku wiersz `"BOOK B1 4DX N"`: najpierw walidacja formatu, potem typów biletów, dopiero na końcu "booking exists" - kolejność walidacji też jest kontraktem.

### Krok 1: Strategy dla ceny biletu

**Zmiany:**

1. Utwórz nowy plik `TicketPricing.cs`: delegat `public delegate Money TicketPricing(Money formatBase)`.
2. Utwórz nowy plik `TicketPricings.cs`: statyczna klasa ze słownikiem kod typu - strategia (lambdy, wspólna metoda pomocnicza `Discounted(percent)`) i metodą `ForType(code)`, która dla nieznanego kodu rzuca `unknown ticket type: X` jak `Start`.
3. W pętli `Book` zastąp wewnętrzny `switch` po typie biletu wywołaniem strategii.

**Przed (`BoxOfficeDesk.cs`, w `Book`):**

```csharp
var total = Money.Zero;
for (var i = 3; i < args.Length; i++)
{
    total = total.Plus(args[i] switch
    {
        "N" => basePrice,
        "S" => basePrice.Minus(basePrice.Percent(25)),
        "E" => basePrice.Minus(basePrice.Percent(30)),
        "C" => basePrice.Minus(basePrice.Percent(40)),
        _ => throw new ArgumentException("unknown ticket type: " + args[i]),
    });
}
```

**Po (`TicketPricing.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

/// <summary>Strategy - wymienny algorytm ceny jednego biletu (delegat).</summary>
public delegate Money TicketPricing(Money formatBase);
```

**Po (`TicketPricings.cs`, nowy plik):**

```csharp
public static class TicketPricings
{
    private static readonly IReadOnlyDictionary<string, TicketPricing> ByType = new Dictionary<string, TicketPricing>
    {
        ["N"] = formatBase => formatBase,
        ["S"] = Discounted(25),
        ["E"] = Discounted(30),
        ["C"] = Discounted(40),
    };

    public static TicketPricing ForType(string code)
    {
        if (!ByType.TryGetValue(code, out var pricing))
        {
            throw new ArgumentException("unknown ticket type: " + code);
        }
        return pricing;
    }

    private static TicketPricing Discounted(int percent)
    {
        return formatBase => formatBase.Minus(formatBase.Percent(percent));
    }
}
```

**Po (`BoxOfficeDesk.cs`, w `Book`):**

```csharp
for (var i = 3; i < args.Length; i++)
{
    total = total.Plus(TicketPricings.ForType(args[i]).Invoke(basePrice));
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s26` - zielone.
**Co powiedzieć:** cenę wybiera klient na podstawie danych wejściowych, bez historii i bez stanu - to Strategy. Strategia zna tylko cenę bazową formatu, nic o rezerwacji ani o operacji kasjera. Strategie są bezstanowe, więc `ForType("S")` zwraca zawsze tę samą instancję delegatu.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s26 0 1`

### Krok 2: Command dla operacji kasjera

**Zmiany:**

1. Wydziel z pulpitu pola `_statuses`, `_amounts`, `_till` do nowej klasy odbiorcy `BookingLedger` (wyodrębnienie klasy) z metodami `Open`, `Remove`, `StatusOf`, `Pay`, `Use`, `Cancel`, `Restore`, `Deposit`, `Withdraw`, `Summary`; warunki po statusie przechodzą do `Pay`, `Use` i `Cancel`.
2. Utwórz interfejs `IDeskCommand` (`Execute(ledger)`, `Undo(ledger)`, `JournalEntry()`) i po jednej klasie na operację: `BookCommand`, `PayCommand`, `UseCommand`, `CancelCommand`; każda ma fabrykę `From(args)` i pamięta tylko to, czego potrzebuje do cofnięcia.
3. Pulpit dostaje rejestr `IReadOnlyDictionary<string, Func<string[], IDeskCommand>>` i historię `List<IDeskCommand>` zamiast rekordów `Entry`; `Undo()` woła `last.Undo(_ledger)`, a `Report()` składa dziennik z `JournalEntry()`. UNDO i REPORT zostają w `switch` pulpitu.

**Przed (`BoxOfficeDesk.cs`, w `Undo`):**

```csharp
var last = _journal[^1];
_journal.RemoveAt(_journal.Count - 1);
switch (last.Operation)
{
    case "BOOK":
        _statuses.Remove(last.Id);
        _amounts.Remove(last.Id);
        break;
    case "PAY":
        _till = _till.Minus(last.Moved);
        _statuses[last.Id] = last.Previous!.Value;
        break;
    case "CANCEL":
        _till = _till.Plus(last.Moved);
        _statuses[last.Id] = last.Previous!.Value;
        break;
    default:
        _statuses[last.Id] = last.Previous!.Value;
        break;
}
```

**Po (`BookingLedger.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public sealed class BookingLedger
{
    private readonly OrderedDictionary<string, Status> _statuses = [];
    private readonly OrderedDictionary<string, Money> _amounts = [];
    private Money _till = Money.Zero;

    internal void Open(string id, Money amount)
    {
        if (_statuses.ContainsKey(id))
        {
            throw new ArgumentException("booking exists: " + id);
        }
        _statuses[id] = Status.New;
        _amounts[id] = amount;
    }

    internal void Remove(string id)
    {
        _statuses.Remove(id);
        _amounts.Remove(id);
    }

    internal Status StatusOf(string id)
    {
        if (!_statuses.TryGetValue(id, out var status))
        {
            throw new ArgumentException("no booking " + id);
        }
        return status;
    }

    internal Money Pay(string id)
    {
        var status = StatusOf(id);
        if (status != Status.New)
        {
            throw Invalid("pay", status);
        }
        _till = _till.Plus(_amounts[id]);
        _statuses[id] = Status.Paid;
        return _amounts[id];
    }

    internal void Use(string id)
    {
        var status = StatusOf(id);
        if (status != Status.Paid)
        {
            throw Invalid("use", status);
        }
        _statuses[id] = Status.Used;
    }

    internal Money Cancel(string id)
    {
        var status = StatusOf(id);
        if (status == Status.New)
        {
            _statuses[id] = Status.Cancelled;
            return Money.Zero;
        }
        else if (status == Status.Paid)
        {
            _till = _till.Minus(_amounts[id]);
            _statuses[id] = Status.Cancelled;
            return _amounts[id];
        }
        throw Invalid("cancel", status);
    }

    internal void Restore(string id, Status previous)
    {
        _statuses[id] = previous;
    }

    internal void Deposit(Money amount)
    {
        _till = _till.Plus(amount);
    }

    internal void Withdraw(Money amount)
    {
        _till = _till.Minus(amount);
    }

    internal string Summary()
    {
        var bookings = _statuses.Count == 0 ? "-" : string.Join(", ",
            _statuses.Select(entry => entry.Key + "=" + entry.Value.ToString().ToUpperInvariant()));
        return "Kasa: " + _till + "; " + bookings;
    }

    private static InvalidOperationException Invalid(string action, Status status)
    {
        return new InvalidOperationException("cannot " + action + " in " + status.ToString().ToUpperInvariant());
    }
}
```

**Po (`IDeskCommand.cs`, nowy plik):**

```csharp
public interface IDeskCommand
{
    string Execute(BookingLedger ledger);

    void Undo(BookingLedger ledger);

    string JournalEntry();

    /// <summary>Wspólna walidacja poleceń "OPERACJA &lt;id&gt;".</summary>
    static string IdFrom(string[] args, string operation)
    {
        if (args.Length < 2)
        {
            throw new ArgumentException(operation + " <id>");
        }
        return args[1];
    }
}
```

**Po (`BookCommand.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public sealed class BookCommand : IDeskCommand
{
    private static readonly IReadOnlyDictionary<string, Money> Formats = new Dictionary<string, Money>
    {
        ["2D"] = Money.Of("25.00"),
        ["3D"] = Money.Of("32.00"),
        ["IMAX"] = Money.Of("40.00"),
    };

    private readonly string _id;
    private readonly string _format;
    private readonly IReadOnlyList<string> _ticketTypes;

    private BookCommand(string id, string format, IEnumerable<string> ticketTypes)
    {
        _id = id;
        _format = format;
        _ticketTypes = ticketTypes.ToList();
    }

    internal static BookCommand From(string[] args)
    {
        if (args.Length < 4)
        {
            throw new ArgumentException("BOOK <id> <format> <typ>...");
        }
        return new BookCommand(args[1], args[2], args[3..]);
    }

    public string Execute(BookingLedger ledger)
    {
        if (!Formats.TryGetValue(_format, out var basePrice))
        {
            throw new ArgumentException("unknown format: " + _format);
        }
        var total = Money.Zero;
        foreach (var type in _ticketTypes)
        {
            total = total.Plus(TicketPricings.ForType(type).Invoke(basePrice));
        }
        ledger.Open(_id, total);
        return _id + " NEW " + total;
    }

    public void Undo(BookingLedger ledger)
    {
        ledger.Remove(_id);
    }

    public string JournalEntry()
    {
        return "BOOK " + _id;
    }
}
```

**Po (`PayCommand.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public sealed class PayCommand : IDeskCommand
{
    private readonly string _id;
    private Status _previous;
    private Money _paid = Money.Zero;

    private PayCommand(string id)
    {
        _id = id;
    }

    internal static PayCommand From(string[] args)
    {
        return new PayCommand(IDeskCommand.IdFrom(args, "PAY"));
    }

    public string Execute(BookingLedger ledger)
    {
        _previous = ledger.StatusOf(_id);
        _paid = ledger.Pay(_id);
        return _id + " PAID " + _paid;
    }

    public void Undo(BookingLedger ledger)
    {
        ledger.Withdraw(_paid);
        ledger.Restore(_id, _previous);
    }

    public string JournalEntry()
    {
        return "PAY " + _id;
    }
}
```

**Po (`UseCommand.cs`, nowy plik):**

```csharp
public sealed class UseCommand : IDeskCommand
{
    private readonly string _id;
    private Status _previous;

    private UseCommand(string id)
    {
        _id = id;
    }

    internal static UseCommand From(string[] args)
    {
        return new UseCommand(IDeskCommand.IdFrom(args, "USE"));
    }

    public string Execute(BookingLedger ledger)
    {
        _previous = ledger.StatusOf(_id);
        ledger.Use(_id);
        return _id + " USED";
    }

    public void Undo(BookingLedger ledger)
    {
        ledger.Restore(_id, _previous);
    }

    public string JournalEntry()
    {
        return "USE " + _id;
    }
}
```

**Po (`CancelCommand.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public sealed class CancelCommand : IDeskCommand
{
    private readonly string _id;
    private Status _previous;
    private Money _refunded = Money.Zero;

    private CancelCommand(string id)
    {
        _id = id;
    }

    internal static CancelCommand From(string[] args)
    {
        return new CancelCommand(IDeskCommand.IdFrom(args, "CANCEL"));
    }

    public string Execute(BookingLedger ledger)
    {
        _previous = ledger.StatusOf(_id);
        _refunded = ledger.Cancel(_id);
        return _id + " CANCELLED" + (_refunded.IsGreaterThan(Money.Zero) ? " zwrot " + _refunded : "");
    }

    public void Undo(BookingLedger ledger)
    {
        ledger.Deposit(_refunded);
        ledger.Restore(_id, _previous);
    }

    public string JournalEntry()
    {
        return "CANCEL " + _id;
    }
}
```

**Po (`BoxOfficeDesk.cs`, cały plik - pulpit został tylko dyspozytorem):**

```csharp
using System.Text.RegularExpressions;

public sealed class BoxOfficeDesk
{
    private static readonly IReadOnlyDictionary<string, Func<string[], IDeskCommand>> Commands =
        new Dictionary<string, Func<string[], IDeskCommand>>
        {
            ["BOOK"] = BookCommand.From,
            ["PAY"] = PayCommand.From,
            ["USE"] = UseCommand.From,
            ["CANCEL"] = CancelCommand.From,
        };

    private readonly BookingLedger _ledger = new();
    private readonly List<IDeskCommand> _history = [];

    public string Handle(string line)
    {
        var args = Regex.Split(line.Trim(), " +");
        var operation = args[0].ToUpperInvariant();
        try
        {
            return operation switch
            {
                "UNDO" => Undo(),
                "REPORT" => Report(),
                _ => Execute(operation, args),
            };
        }
        catch (Exception exception) when (exception is ArgumentException or InvalidOperationException)
        {
            return "Blad: " + exception.Message;
        }
    }

    private string Execute(string operation, string[] args)
    {
        if (!Commands.TryGetValue(operation, out var factory))
        {
            return "Nieznana operacja: " + args[0];
        }
        var command = factory(args);
        var result = command.Execute(_ledger);
        _history.Add(command);
        return result;
    }

    private string Undo()
    {
        if (_history.Count == 0)
        {
            throw new InvalidOperationException("nothing to undo");
        }
        var last = _history[^1];
        _history.RemoveAt(_history.Count - 1);
        last.Undo(_ledger);
        return "Cofnieto: " + last.JournalEntry();
    }

    private string Report()
    {
        var log = _history.Count == 0 ? "-" : string.Join(", ",
            _history.Select(command => command.JournalEntry()));
        return _ledger.Summary() + "; dziennik: " + log;
    }
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** wykonanie i cofnięcie są obok siebie w jednej klasie, a nie w dwóch `switch`. Polecenie pamięta tylko to, czego potrzebuje do cofnięcia (poprzedni status, przesuniętą kwotę) - to stan **tego** żądania, dlatego każde wywołanie tworzy nowy obiekt przez `From(args)`, a komenda nie jest współdzieloną stałą jak w s15. Do historii trafia dopiero polecenie wykonane bez wyjątku, jak w `Start`. UNDO i REPORT zostały w `switch` pulpitu, bo nie są operacjami na rezerwacji. Cykl życia nadal siedzi w warunkach `BookingLedger` - to zapach na krok 3.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s26 1 2`

### Krok 3: State dla cyklu życia rezerwacji

**Zmiany:**

1. Utwórz nowy plik `Booking.cs`: klasa z kwotą `_amount`, polem `private State _state` i metodami `Pay`, `Use`, `Cancel` oraz `Restore(status)` delegującymi do stanu; `Restore` celowo omija reguły przejść (kompensacja dla UNDO).
2. Wewnątrz `Booking` dodaj prywatną klasę abstrakcyjną `State`: obiekty `New`, `Paid`, `Used`, `Cancelled`, fabryka `State.Of(Status)`, metody bazowe rzucają `cannot <akcja> in <STATUS>`. Warunki z `BookingLedger.Pay/Use/Cancel` przenieś do podklas stanu: `NewState` nadpisuje `Pay` i `Cancel`, `PaidState` nadpisuje `Use` i `Cancel`, `FinalState` (Used, Cancelled) nie nadpisuje niczego.
3. `BookingLedger` trzyma teraz jeden `OrderedDictionary<string, Booking>` zamiast dwóch słowników i tylko przesuwa pieniądze w kasie; przejścia statusów wykonuje `Booking`.

**Przed (`BookingLedger.cs`, przykład warunków po statusie):**

```csharp
internal Money Cancel(string id)
{
    var status = StatusOf(id);
    if (status == Status.New)
    {
        _statuses[id] = Status.Cancelled;
        return Money.Zero;
    }
    else if (status == Status.Paid)
    {
        _till = _till.Minus(_amounts[id]);
        _statuses[id] = Status.Cancelled;
        return _amounts[id];
    }
    throw Invalid("cancel", status);
}
```

**Po (`Booking.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public sealed class Booking
{
    private readonly Money _amount;
    private State _state = State.New;

    public Booking(Money amount)
    {
        ArgumentNullException.ThrowIfNull(amount);
        _amount = amount;
    }

    public Status Status => _state.Status;

    /// <summary>Zwraca kwotę pobraną do kasy.</summary>
    public Money Pay()
    {
        return _state.Pay(this);
    }

    public void Use()
    {
        _state.Use(this);
    }

    /// <summary>Zwraca kwotę oddaną klientowi (zero, gdy rezerwacja nie była opłacona).</summary>
    public Money Cancel()
    {
        return _state.Cancel(this);
    }

    /// <summary>Dla cofania poleceń: powrót do zapamiętanego stanu z pominięciem reguł przejść.</summary>
    internal void Restore(Status status)
    {
        _state = State.Of(status);
    }

    private abstract class State
    {
        public static readonly State New = new NewState();
        public static readonly State Paid = new PaidState();
        public static readonly State Used = new FinalState(Status.Used);
        public static readonly State Cancelled = new FinalState(Status.Cancelled);

        public abstract Status Status { get; }

        public static State Of(Status status) => status switch
        {
            Status.New => New,
            Status.Paid => Paid,
            Status.Used => Used,
            Status.Cancelled => Cancelled,
            _ => throw new ArgumentOutOfRangeException(nameof(status), status, null),
        };

        public virtual Money Pay(Booking booking) => throw Invalid("pay");

        public virtual void Use(Booking booking) => throw Invalid("use");

        public virtual Money Cancel(Booking booking) => throw Invalid("cancel");

        private InvalidOperationException Invalid(string action) =>
            new("cannot " + action + " in " + Status.ToString().ToUpperInvariant());

        private sealed class NewState : State
        {
            public override Status Status => Status.New;

            public override Money Pay(Booking booking)
            {
                booking._state = Paid;
                return booking._amount;
            }

            public override Money Cancel(Booking booking)
            {
                booking._state = Cancelled;
                return Money.Zero;
            }
        }

        private sealed class PaidState : State
        {
            public override Status Status => Status.Paid;

            public override void Use(Booking booking)
            {
                booking._state = Used;
            }

            public override Money Cancel(Booking booking)
            {
                booking._state = Cancelled;
                return booking._amount;
            }
        }

        /// <summary>Stany końcowe (Used, Cancelled) - żadne przejście nie jest dozwolone.</summary>
        private sealed class FinalState(Status status) : State
        {
            public override Status Status => status;
        }
    }
}
```

**Po (`BookingLedger.cs`, cały plik - bez warunków po statusie):**

```csharp
using Training.Workshop.Shared;

public sealed class BookingLedger
{
    private readonly OrderedDictionary<string, Booking> _bookings = [];
    private Money _till = Money.Zero;

    internal void Open(string id, Money amount)
    {
        if (_bookings.ContainsKey(id))
        {
            throw new ArgumentException("booking exists: " + id);
        }
        _bookings[id] = new Booking(amount);
    }

    internal void Remove(string id)
    {
        _bookings.Remove(id);
    }

    internal Status StatusOf(string id)
    {
        return Booking(id).Status;
    }

    internal Money Pay(string id)
    {
        var paid = Booking(id).Pay();
        _till = _till.Plus(paid);
        return paid;
    }

    internal void Use(string id)
    {
        Booking(id).Use();
    }

    internal Money Cancel(string id)
    {
        var refunded = Booking(id).Cancel();
        _till = _till.Minus(refunded);
        return refunded;
    }

    internal void Restore(string id, Status previous)
    {
        Booking(id).Restore(previous);
    }

    internal void Deposit(Money amount)
    {
        _till = _till.Plus(amount);
    }

    internal void Withdraw(Money amount)
    {
        _till = _till.Minus(amount);
    }

    internal string Summary()
    {
        var list = _bookings.Count == 0 ? "-" : string.Join(", ",
            _bookings.Select(entry => entry.Key + "=" + entry.Value.Status.ToString().ToUpperInvariant()));
        return "Kasa: " + _till + "; " + list;
    }

    private Booking Booking(string id)
    {
        if (!_bookings.TryGetValue(id, out var booking))
        {
            throw new ArgumentException("no booking " + id);
        }
        return booking;
    }
}
```

**Uruchom:** test zielony; `S26SolutionTest` sprawdza, że cena to bezstanowa strategia (`TicketPriceIsAStatelessStrategyThatKnowsOnlyMoney`), każda operacja to polecenie z `Undo` (`EveryOperatorOperationIsACommandWithUndo`), a stan jest prywatnym typem rezerwacji (`StateLivesInsideBookingAsPrivateType`).
**Co powiedzieć:** CANCEL w NEW i w PAID to ta sama operacja z innym zachowaniem - tego nie wyrazi ani Strategy (nikt z zewnątrz nie wybiera), ani Command (polecenie jest to samo). `AxesAreNotMixed` pilnuje, że stan nie zna cennika ani nazw operacji, a polecenia nie sięgają do stanu. `Restore` celowo omija reguły przejść: cofnięcie to kompensacja, nie przejście biznesowe (USED -> PAID nie jest dozwolone, ale UNDO po USE musi je wykonać).
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s26 2 3`

### Rozwiązanie i uzasadnienie

`Step3`: pulpit zna tylko polecenia i ich historię, cena biletu to Strategy wybierana po typie, cykl życia to State w `Booking`. Trzy osie, trzy wzorce, zero mieszania. Publiczny `Status` zostaje kontraktem trwałym, a prywatny `State` jest szczegółem implementacji mapowanym przez `State.Of(status)`.

### Pułapki

- Jeden wzorzec dla wszystkich osi "bo to wszystko `switch`" - np. Strategy dla statusu: klient musiałby wiedzieć, w jakim stanie jest rezerwacja, i sam podmieniać strategię.
- Polecenie jako współdzielony singleton z polami `_previous` i `_paid` - drugie PAY nadpisze dane potrzebne do cofnięcia pierwszego.
- Cofanie przez publiczne przejścia (`Pay()`, `Cancel()`) zamiast `Restore` - cofnięcie USE wymaga przejścia, którego reguły zabraniają, a cofnięcie CANCEL po zwrocie musi wpłacić kwotę z powrotem do kasy (`Deposit`).
- Kombinatoryczna hierarchia (`PaidCancelCommand`) zamiast złożenia osi.
- Zapis prywatnego `State` (np. rzutowanie enuma na `int`) do bazy zamiast stabilnego `Status`.

### Pytanie do sali

Kierownik chce zwrotu z potrąceniem 10% dla rezerwacji opłaconej, ale tylko w weekend. Która oś się zmienia i w której klasie to dopiszecie?

## Scena s27. Replace Inheritance with Delegation - raporty sprzedaży

**Temat ze slajdów:** 7.2. Gdy punkty zaczepienia obejmują dwie niezależne osie; 7.3. Zastąpienie dziedziczenia delegacją: osie jako strategie; 7.4. Strategia czy Metoda szablonowa?
**Namespace:** `Training.Workshop.M5.S27TemplateToStrategy` · **Test:** `scripts/warsztat.sh --lang cs test m5/s27` (`S27EquivalenceTest`, `S27SolutionTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `ScreeningReport` to Template Method z pięcioma hakami, ale haki niosą dwie niezależne osie: okres (`Includes`, `Label`) i format (`Header`, `Row`, `Footer`). Każda kombinacja jest podklasą (`DailyCsvReport`, `DailyTextReport`, `WeeklyCsvReport`, `WeeklyTextReport`), a kod okresu i formatu jest między nimi skopiowany. Wyjmujemy oś formatu do strategii `IReportFormat`, potem oś okresu do `IReportPeriod`, a na końcu zwijamy cztery podklasy w jedną klasę `sealed` złożoną z dwóch strategii.

**Zasada:** Template Method jest dobry, gdy szkielet jest stabilny, a zmienia się **jeden** krok. Gdy haki grupują się w niezależne osie, dziedziczenie mnoży klasy (N × M), a kompozycja je dodaje (N + M). Replace Inheritance with Delegation robimy oś po osi: pogrupuj haki, Extract Interface dla grupy, delegat w konstruktorze, przeniesienie implementacji, usunięcie podklas po migracji klientów.

**Efekt:** Dwa okresy i dwa formaty to cztery klasy strategii, a trzeci format to jedna klasa zamiast dwóch podklas. Kolejność kroków raportu nadal pilnuje jedna metoda `Render` - ochrona przechodzi z niewirtualnej metody szkieletu na klasę `sealed`. Wynik każdego raportu i komunikaty błędów `Reports.Of` są bez zmian.

**Różnica względem Javy:** `final` na metodzie szkieletu to w C# zwykła metoda niewirtualna (metody C# domyślnie nie są wirtualne), a `final` na klasie to `sealed`. `S27SolutionTest` sprawdza to refleksją (`IsSealed`, `!IsVirtual`), a usunięte podklasy przez `Assembly.GetType(...) == null`. `TreeMap` to `SortedDictionary<string, ...>(StringComparer.Ordinal)`, `LocalDate` to `DateOnly` (drukowany jako `yyyy-MM-dd`), `previousOrSame(MONDAY)` to arytmetyka na `DayOfWeek`, a formatowanie `%-20s %5d %10s` to `string.Format(CultureInfo.InvariantCulture, "{0,-20} {1,5} {2,10}", ...)`. Format Markdown z testu to prywatna klasa `MarkdownFormat : IReportFormat`.

### Co widzimy

`Reports.Of(period, format)` (stabilny punkt wejścia) wybiera zagnieżdżonym `switch` jedną z czterech podklas - iloczyn osi zapisany w kodzie. Szkielet w bazie jest poprawny, ale haki mieszają dwie przyczyny zmiany:

```csharp
/// <summary>Szkielet raportu - niewirtualny (odpowiednik final).</summary>
public string Render(DateOnly day, IReadOnlyList<ScreeningSale> sales)
{
    ...
        if (Includes(day, sale.Day))                               // hak okresu
    ...
    var text = new StringBuilder(Header(Label(day)));              // format(okres)
    ...
        text.Append(Row(entry.Key, entry.Value.Tickets, entry.Value.Revenue));   // hak formatu
    return text.Append(Footer(total.Tickets, total.Revenue)).ToString();
}
```

`S27EquivalenceTest` sprawdza każdą kombinację: dzień CSV (z escapowaniem średnika w tytule), dzień tekst, tydzień CSV ze środy (sumy po tytule), tydzień tekst z niedzieli, następny tydzień od poniedziałku, dzień bez sprzedaży, nieznany okres, nieznany format i przypadek "najpierw okres, potem format".

### Krok 1: oś formatu jako strategia

**Zmiany:**

1. Utwórz interfejs `IReportFormat` z metodami `Header`, `Row`, `Footer` oraz klasy `CsvFormat` i `TextFormat` z ciałami skopiowanymi z `DailyCsvReport` i `DailyTextReport` (najpierw porównaj je z kopiami w `Weekly*`, czy się nie rozjechały).
2. Do `ScreeningReport` dodaj pole `_format` i konstruktor `protected ScreeningReport(IReportFormat format)`; w `Render` zamień wywołania haków na `_format.Header(...)`, `_format.Row(...)`, `_format.Footer(...)`.
3. Usuń trzy haki formatu z bazy i z podklas; każda podklasa woła `: base(new CsvFormat())` albo `: base(new TextFormat())`.

**Przed (`ScreeningReport.cs`):**

```csharp
var text = new StringBuilder(Header(Label(day)));
...
/// <summary>Hak formatu.</summary>
protected abstract string Header(string label);

/// <summary>Hak formatu.</summary>
protected abstract string Row(string title, int tickets, Money revenue);

/// <summary>Hak formatu.</summary>
protected abstract string Footer(int tickets, Money revenue);
```

**Po (`IReportFormat.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

/// <summary>Strategy dla osi "format" - trzy haki formatu wyjęte z hierarchii.</summary>
public interface IReportFormat
{
    string Header(string label);

    string Row(string title, int tickets, Money revenue);

    string Footer(int tickets, Money revenue);
}
```

**Po (`CsvFormat.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public sealed class CsvFormat : IReportFormat
{
    public string Header(string label)
    {
        return "# raport " + label + "\nfilm;bilety;przychod\n";
    }

    public string Row(string title, int tickets, Money revenue)
    {
        var cell = title.Contains(';') ? "\"" + title + "\"" : title;
        return cell + ";" + tickets + ";" + revenue + "\n";
    }

    public string Footer(int tickets, Money revenue)
    {
        return "SUMA;" + tickets + ";" + revenue + "\n";
    }
}
```

**Po (`TextFormat.cs`, nowy plik):**

```csharp
using System.Globalization;
using Training.Workshop.Shared;

public sealed class TextFormat : IReportFormat
{
    public string Header(string label)
    {
        return "RAPORT " + label.ToUpperInvariant() + "\n" + new string('-', 37) + "\n";
    }

    public string Row(string title, int tickets, Money revenue)
    {
        return string.Format(CultureInfo.InvariantCulture, "{0,-20} {1,5} {2,10}", title, tickets, revenue) + "\n";
    }

    public string Footer(int tickets, Money revenue)
    {
        return new string('-', 37) + "\n" + Row("RAZEM", tickets, revenue);
    }
}
```

**Po (`ScreeningReport.cs`):**

```csharp
private readonly IReportFormat _format;

protected ScreeningReport(IReportFormat format)
{
    ArgumentNullException.ThrowIfNull(format);
    _format = format;
}
...
var text = new StringBuilder(_format.Header(Label(day)));
var total = new Line(0, Money.Zero);
foreach (var entry in byTitle)
{
    text.Append(_format.Row(entry.Key, entry.Value.Tickets, entry.Value.Revenue));
    total = total.Plus(entry.Value);
}
return text.Append(_format.Footer(total.Tickets, total.Revenue)).ToString();
```

**Po (`DailyCsvReport.cs`; analogicznie pozostałe podklasy):**

```csharp
public DailyCsvReport()
    : base(new CsvFormat())
{
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s27` - zielone.
**Co powiedzieć:** najpierw grupujemy haki według osi, dopiero potem cokolwiek przenosimy. Hak wołamy teraz na delegacie, nie na `this` - strategia nie widzi stanu `protected` bazy i wszystko dostaje parametrami. `Reports` jest bez zmian, klienci niczego nie zauważyli.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s27 0 1`

### Krok 2: oś okresu jako strategia

**Zmiany:**

1. Ten sam ruch dla haków okresu `Includes` i `Label`: interfejs `IReportPeriod` oraz klasy `DailyPeriod` i `WeeklyPeriod`; powtórzone wyliczanie poniedziałku wydziel w `WeeklyPeriod` do prywatnej metody `Monday(day)` (wyodrębnienie metody).
2. Konstruktor bazy przyjmuje obie strategie: `ScreeningReport(IReportPeriod period, IReportFormat format)`; w `Render` wołaj `_period.Includes(...)` i `_period.Label(...)`.
3. Z podklas znikają ostatnie haki - każda to już tylko konfiguracja konstruktora.

**Przed (`DailyCsvReport.cs`):**

```csharp
public sealed class DailyCsvReport : ScreeningReport
{
    public DailyCsvReport()
        : base(new CsvFormat())
    {
    }

    protected override bool Includes(DateOnly day, DateOnly saleDay)
    {
        return saleDay == day;
    }

    protected override string Label(DateOnly day)
    {
        return "dzien " + day.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture);
    }
}
```

**Po (`IReportPeriod.cs`, nowy plik):**

```csharp
/// <summary>Strategy dla osi "okres" - dwa haki okresu wyjęte z hierarchii.</summary>
public interface IReportPeriod
{
    bool Includes(DateOnly day, DateOnly saleDay);

    string Label(DateOnly day);
}
```

**Po (`DailyPeriod.cs`, nowy plik):**

```csharp
using System.Globalization;

public sealed class DailyPeriod : IReportPeriod
{
    public bool Includes(DateOnly day, DateOnly saleDay)
    {
        return saleDay == day;
    }

    public string Label(DateOnly day)
    {
        return "dzien " + day.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture);
    }
}
```

**Po (`WeeklyPeriod.cs`, nowy plik):**

```csharp
using System.Globalization;

public sealed class WeeklyPeriod : IReportPeriod
{
    public bool Includes(DateOnly day, DateOnly saleDay)
    {
        var monday = Monday(day);
        return saleDay >= monday && saleDay <= monday.AddDays(6);
    }

    public string Label(DateOnly day)
    {
        var monday = Monday(day);
        return "tydzien " + monday.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture)
            + ".." + monday.AddDays(6).ToString("yyyy-MM-dd", CultureInfo.InvariantCulture);
    }

    /// <summary>Poniedziałek tego samego tygodnia (DayOfWeek liczy od niedzieli = 0).</summary>
    private static DateOnly Monday(DateOnly day)
    {
        return day.AddDays(-(((int)day.DayOfWeek + 6) % 7));
    }
}
```

**Po (`ScreeningReport.cs`):**

```csharp
private readonly IReportPeriod _period;
private readonly IReportFormat _format;

protected ScreeningReport(IReportPeriod period, IReportFormat format)
{
    ArgumentNullException.ThrowIfNull(period);
    ArgumentNullException.ThrowIfNull(format);
    _period = period;
    _format = format;
}
...
if (_period.Includes(day, sale.Day))
...
var text = new StringBuilder(_format.Header(_period.Label(day)));
```

**Po (`WeeklyTextReport.cs`; analogicznie pozostałe podklasy):**

```csharp
public WeeklyTextReport()
    : base(new WeeklyPeriod(), new TextFormat())
{
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** baza nie ma już żadnego haka, a podklasy są tylko konfiguracją konstruktora - kandydatami do Inline Class. `Render` jest wciąż niewirtualny (`BeforeTheCollapseTheSkeletonWasStillAProtectedTemplateMethod`), więc kolejność kroków jest chroniona tak jak w Template Method.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s27 1 2`

### Krok 3: zwinięcie podklas w jedną klasę sealed

**Zmiany:**

1. W `ScreeningReport` usuń `abstract`, oznacz klasę `sealed` i zrób konstruktor publiczny.
2. W `Reports` dodaj dwa słowniki `Periods` i `Formats`; `Of` sprawdza najpierw okres, potem format (te same komunikaty błędów) i zwraca `new ScreeningReport(chosenPeriod, chosenFormat)`; pomocnicze `UnknownFormat` znika.
3. Usuń cztery pliki podklas `Daily*Report` i `Weekly*Report` (bezpieczne usunięcie - wcześniej upewnij się, że nikt nie używa ich typów).

**Przed:**

```csharp
return period switch
{
    "DAILY" => format switch
    {
        "CSV" => new DailyCsvReport(),
        "TEXT" => new DailyTextReport(),
        _ => throw UnknownFormat(format),
    },
    "WEEKLY" => format switch
    {
        "CSV" => new WeeklyCsvReport(),
        "TEXT" => new WeeklyTextReport(),
        _ => throw UnknownFormat(format),
    },
    _ => throw new ArgumentException("unknown period: " + period),
};
```

**Po (`Reports.cs`):**

```csharp
private static readonly IReadOnlyDictionary<string, IReportPeriod> Periods = new Dictionary<string, IReportPeriod>
{
    ["DAILY"] = new DailyPeriod(),
    ["WEEKLY"] = new WeeklyPeriod(),
};

private static readonly IReadOnlyDictionary<string, IReportFormat> Formats = new Dictionary<string, IReportFormat>
{
    ["CSV"] = new CsvFormat(),
    ["TEXT"] = new TextFormat(),
};

public static ScreeningReport Of(string period, string format)
{
    if (!Periods.TryGetValue(period, out var chosenPeriod))
    {
        throw new ArgumentException("unknown period: " + period);
    }
    if (!Formats.TryGetValue(format, out var chosenFormat))
    {
        throw new ArgumentException("unknown format: " + format);
    }
    return new ScreeningReport(chosenPeriod, chosenFormat);
}
```

**Uruchom:** test zielony; `S27SolutionTest.NewFormatWorksWithEveryPeriodWithoutNewClasses` dodaje format Markdown jedną klasą testową i od razu używa go z oboma okresami, a `CombinationSubclassesAreGone` sprawdza, że podklas już nie ma.
**Co powiedzieć:** N + M zamiast N × M; nowa kombinacja to nowe złożenie, nie nowa klasa. Przypadek "najpierw okres, potem format" (MONTHLY i PDF daje `unknown period`) pilnuje, że zamiana zagnieżdżonego `switch` na dwa słowniki nie zmieniła kolejności walidacji.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s27 2 3`

### Rozwiązanie i uzasadnienie

`Step3`: jedna klasa `sealed ScreeningReport` złożona z `IReportPeriod` i `IReportFormat`, cztery małe strategie i fabryka `Reports` wybierająca osie niezależnie (`ReportIsOneFinalConcreteClassComposedOfTwoStrategies`). Template Method był dobry, dopóki zmieniała się jedna oś; przy dwóch niezależnych osiach kompozycja pozwala też wybrać kombinację w czasie działania.

### Pułapki

- Zwinięcie podklas przed wyjęciem obu osi - zostaje klasa z warunkiem `if (weekly)` zamiast strategii.
- Znika typ podklasy: kod z `is WeeklyCsvReport`, rejestracja w kontenerze DI po typie albo zapisana nazwa klasy przestają działać - poszukaj takich użyć, zanim usuniesz podklasy.
- Osie, które tylko wyglądają na niezależne: gdyby format tekstowy inaczej opisywał tydzień niż dzień, format zależałby od okresu i podział 2 + 2 byłby fałszywy.
- Kopie implementacji w podklasach mogły się już rozjechać (np. escapowanie tylko w jednym CSV) - porównaj je, zanim zostawisz jedną.

### Pytanie do sali

Kiedy zostawilibyście Template Method mimo dwóch osi w hakach? Co musiałoby być prawdą o tempie zmian każdej z nich?

## Scena s28. Replace Conditional with Chain of Responsibility - zatwierdzanie zwrotów

**Temat ze slajdów:** 8.3. Łańcuch `if/else`, w którym kolejność jest regułą; 8.4. Łańcuch zobowiązań: ogniwo i złożenie łańcucha; 8.5. Łańcuch zobowiązań czy Dekorator?
**Namespace:** `Training.Workshop.M5.S28Chain` · **Test:** `scripts/warsztat.sh --lang cs test m5/s28` (`S28EquivalenceTest`, `S28SolutionTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `RefundApprover.Decide` rozstrzyga łańcuchem `if/else`, kto zatwierdza zwrot: po rozpoczęciu seansu odmowa, co najmniej 24 h przed seansem zwrot automatyczny, do 100.00 kasjer, do 1000.00 kierownik, wyżej brak uprawnionego. Wyjmujemy pierwszą gałąź jako ogniwo `IRefundHandler`, potem wszystkie gałęzie jako ogniwa w liście, a na końcu budujemy jawny łańcuch z następnikiem w konstruktorze i ogniwem końcowym `NoApproverHandler`.

**Zasada:** Chain of Responsibility pasuje do uporządkowanych reguł, z których obsługuje jedna. Ogniwa nie są niezależne: zwrot automatyczny nie ma limitu kwoty tylko dlatego, że stoi przed kasjerem - kolejność jest regułą biznesową i ma być widoczna w jednym miejscu. Koniec łańcucha to decyzja, nie `null`. Kształt przypomina Decorator, ale intencja jest inna: dekorator zawsze woła wnętrze, ogniwo tylko wtedy, gdy samo nie obsłuży żądania.

**Efekt:** Kolejność poziomów widać w jednym wyrażeniu, a inną ścieżkę (np. festiwal bez zwrotów automatycznych) składa się bez edycji `Decide`. Walidacja kwoty zostaje przed łańcuchem, bo to warunek wstępny, a nie poziom zatwierdzania. Decyzje dla wszystkich granic progów są identyczne jak w `Start`.

**Różnica względem Javy:** przejściowy kontrakt `Optional<RefundDecision>` z kroków 1-2 to `RefundDecision?` (`null` = "pytaj następne ogniwo"); w kroku 3 typ wyniku traci `?` i kompilator (nullable) sam pilnuje, że ogniwo zawsze zwraca decyzję. `LocalDateTime` to `DateTime`, a `Duration.between(...).toHours()` to `(ScreeningStart - RequestedAt).TotalHours`. Ogniwa mają jawne pola `readonly` (`_limit`, `_next`) zamiast konstruktorów głównych, żeby test refleksji (`EveryLinkKnowsOnlyItsSuccessor`) liczył dokładnie jedno pole następnika. Ogniwo-szpieg w `HandlerThatDecidesStopsTheChainUnlikeADecorator` to prywatna klasa `SpyHandler` zamiast lambdy.

### Co widzimy

```csharp
if (!(request.RequestedAt < request.ScreeningStart))
{
    return RefundDecision.Rejected("seans juz sie rozpoczal");
}
else if ((request.ScreeningStart - request.RequestedAt).TotalHours >= 24)
{
    return RefundDecision.ApprovedBy("AUTO");
}
else if (!request.Amount.IsGreaterThan(CashierLimit))
{
    return RefundDecision.ApprovedBy("CASHIER");
}
else if (!request.Amount.IsGreaterThan(ManagerLimit))
{
    return RefundDecision.ApprovedBy("MANAGER");
}
return RefundDecision.Rejected("brak uprawnionego zatwierdzajacego");
```

Zmiana kolejności, nowy poziom albo inna ścieżka dla festiwalu oznacza edycję tej jednej metody. `S28EquivalenceTest` to tabela na granicach progów: dokładnie 24 h przed (AUTO) i 23 h 59 min (kasjer), 100.00 i 100.01, 1000.00 i 1000.01, 5000.00 trzy dni przed seansem (AUTO bez limitu - kolejność reguł), chwila rozpoczęcia seansu, kwota zero i kwota ujemna po seansie (walidacja przed łańcuchem).

### Krok 1: Extract Interface i pierwsze ogniwo

**Zmiany:**

1. Utwórz nowy plik `IRefundHandler.cs`: interfejs z metodą `RefundDecision? Handle(RefundRequest request)` - `null` znaczy "pytaj dalej".
2. Pierwszą gałąź przenieś do nowej klasy `AfterScreeningHandler : IRefundHandler` (wyodrębnienie klasy): po rozpoczęciu seansu zwraca odmowę, w przeciwnym razie `null`.
3. W `RefundApprover` dodaj pole `_afterScreening = new AfterScreeningHandler()`; w `Decide` pierwsze `if` zastąp wywołaniem `Handle` i sprawdzeniem `decision != null`. Reszta łańcucha `if/else` bez zmian.

**Przed:**

```csharp
if (!(request.RequestedAt < request.ScreeningStart))
{
    return RefundDecision.Rejected("seans juz sie rozpoczal");
}
else if ((request.ScreeningStart - request.RequestedAt).TotalHours >= 24)
{
    return RefundDecision.ApprovedBy("AUTO");
}
```

**Po (`IRefundHandler.cs`, nowy plik):**

```csharp
/// <summary>Ogniwo decyzji - pusta odpowiedź (null) znaczy "to nie moja sprawa, pytaj dalej".</summary>
public interface IRefundHandler
{
    RefundDecision? Handle(RefundRequest request);
}
```

**Po (`AfterScreeningHandler.cs`, nowy plik):**

```csharp
public sealed class AfterScreeningHandler : IRefundHandler
{
    public RefundDecision? Handle(RefundRequest request)
    {
        if (!(request.RequestedAt < request.ScreeningStart))
        {
            return RefundDecision.Rejected("seans juz sie rozpoczal");
        }
        return null;
    }
}
```

**Po (`RefundApprover.cs`):**

```csharp
private readonly IRefundHandler _afterScreening = new AfterScreeningHandler();
...
var decision = _afterScreening.Handle(request);
if (decision != null)
{
    return decision;
}
else if ((request.ScreeningStart - request.RequestedAt).TotalHours >= 24)
{
    return RefundDecision.ApprovedBy("AUTO");
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s28` - zielone.
**Co powiedzieć:** zaczynamy od pierwszej gałęzi, bo wszystkie pozostałe zakładają, że ona odmówiła. `RefundDecision?` to przejściowy kontrakt "obsłużyłem albo nie" - jeszcze bez następnika.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s28 0 1`

### Krok 2: wszystkie gałęzie jako ogniwa w liście

**Zmiany:**

1. Ten sam ruch dla pozostałych gałęzi: `AutoRefundHandler`, `CashierHandler` i `ManagerHandler`; limity kasjera i kierownika stają się parametrami konstruktorów (pole `readonly _limit`).
2. W `RefundApprover` zamień pole pojedynczego ogniwa na listę `IReadOnlyList<IRefundHandler> _handlers` w kolejności reguł.
3. `Decide` przegląda listę po kolei i zwraca pierwszą decyzję różną od `null`, a po liście - odmowę "brak uprawnionego zatwierdzajacego".

**Przed:**

```csharp
var decision = _afterScreening.Handle(request);
if (decision != null)
{
    return decision;
}
else if ((request.ScreeningStart - request.RequestedAt).TotalHours >= 24)
{
    return RefundDecision.ApprovedBy("AUTO");
}
else if (!request.Amount.IsGreaterThan(CashierLimit))
{
    return RefundDecision.ApprovedBy("CASHIER");
}
else if (!request.Amount.IsGreaterThan(ManagerLimit))
{
    return RefundDecision.ApprovedBy("MANAGER");
}
return RefundDecision.Rejected("brak uprawnionego zatwierdzajacego");
```

**Po (`AutoRefundHandler.cs`, nowy plik):**

```csharp
public sealed class AutoRefundHandler : IRefundHandler
{
    public RefundDecision? Handle(RefundRequest request)
    {
        if ((request.ScreeningStart - request.RequestedAt).TotalHours >= 24)
        {
            return RefundDecision.ApprovedBy("AUTO");
        }
        return null;
    }
}
```

**Po (`CashierHandler.cs`, nowy plik; `ManagerHandler.cs` analogicznie z `"MANAGER"`):**

```csharp
using Training.Workshop.Shared;

public sealed class CashierHandler : IRefundHandler
{
    private readonly Money _limit;

    public CashierHandler(Money limit)
    {
        ArgumentNullException.ThrowIfNull(limit);
        _limit = limit;
    }

    public RefundDecision? Handle(RefundRequest request)
    {
        if (!request.Amount.IsGreaterThan(_limit))
        {
            return RefundDecision.ApprovedBy("CASHIER");
        }
        return null;
    }
}
```

**Po (`RefundApprover.cs`):**

```csharp
private readonly IReadOnlyList<IRefundHandler> _handlers =
[
    new AfterScreeningHandler(),
    new AutoRefundHandler(),
    new CashierHandler(CashierLimit),
    new ManagerHandler(ManagerLimit),
];
...
foreach (var handler in _handlers)
{
    var decision = handler.Handle(request);
    if (decision != null)
    {
        return decision;
    }
}
return RefundDecision.Rejected("brak uprawnionego zatwierdzajacego");
```

**Uruchom:** test zielony.
**Co powiedzieć:** kolejność reguł ma nazwy i jest w jednym miejscu - to kolejność elementów listy. Lista bywa wystarczająca i prostsza niż klasyczny łańcuch; jeśli nikt nie musi składać innych ścieżek, można się tu zatrzymać.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s28 1 2`

### Krok 3: jawny łańcuch z następnikiem

**Zmiany:**

1. Zmień sygnaturę `IRefundHandler.Handle` (zmiana sygnatury): zwraca `RefundDecision` zamiast `RefundDecision?` - kompilator pilnuje, że ogniwo zawsze zwraca decyzję.
2. Każde ogniwo dostaje następnika w konstruktorze (pole `readonly _next`) i woła `_next.Handle(request)`, gdy samo nie obsłuży żądania. Odmowę z końca listy przenieś do nowego ogniwa `NoApproverHandler`.
3. W `RefundApprover` zastąp listę polem `_chain` i daj dwa konstruktory: domyślny składa standardową ścieżkę, drugi przyjmuje dowolną głowę łańcucha; `Decide` po walidacji kwoty woła `_chain.Handle(request)`.

**Przed:**

```csharp
private readonly IReadOnlyList<IRefundHandler> _handlers =
[
    new AfterScreeningHandler(),
    new AutoRefundHandler(),
    new CashierHandler(CashierLimit),
    new ManagerHandler(ManagerLimit),
];
...
foreach (var handler in _handlers)
{
    var decision = handler.Handle(request);
    if (decision != null)
    {
        return decision;
    }
}
return RefundDecision.Rejected("brak uprawnionego zatwierdzajacego");
```

**Po (`IRefundHandler.cs`):**

```csharp
/// <summary>Ogniwo zawsze zwraca decyzję: własną (i zatrzymuje łańcuch) albo następnika.</summary>
public interface IRefundHandler
{
    RefundDecision Handle(RefundRequest request);
}
```

**Po (`CashierHandler.cs`; `ManagerHandler.cs` analogicznie, a `AfterScreeningHandler.cs` i `AutoRefundHandler.cs` tak samo, tylko bez `_limit`):**

```csharp
using Training.Workshop.Shared;

public sealed class CashierHandler : IRefundHandler
{
    private readonly Money _limit;
    private readonly IRefundHandler _next;

    public CashierHandler(Money limit, IRefundHandler next)
    {
        ArgumentNullException.ThrowIfNull(limit);
        ArgumentNullException.ThrowIfNull(next);
        _limit = limit;
        _next = next;
    }

    public RefundDecision Handle(RefundRequest request)
    {
        if (!request.Amount.IsGreaterThan(_limit))
        {
            return RefundDecision.ApprovedBy("CASHIER");
        }
        return _next.Handle(request);
    }
}
```

**Po (`NoApproverHandler.cs`, nowy plik):**

```csharp
public sealed class NoApproverHandler : IRefundHandler
{
    public RefundDecision Handle(RefundRequest request)
    {
        return RefundDecision.Rejected("brak uprawnionego zatwierdzajacego");
    }
}
```

**Po (`RefundApprover.cs`):**

```csharp
private readonly IRefundHandler _chain;

public RefundApprover()
    : this(new AfterScreeningHandler(
        new AutoRefundHandler(
            new CashierHandler(CashierLimit,
                new ManagerHandler(ManagerLimit,
                    new NoApproverHandler())))))
{
}

public RefundApprover(IRefundHandler chain)
{
    ArgumentNullException.ThrowIfNull(chain);
    _chain = chain;
}

public RefundDecision Decide(RefundRequest request)
{
    if (!request.Amount.IsGreaterThan(Money.Zero))
    {
        throw new ArgumentException("amount must be positive");
    }
    return _chain.Handle(request);
}
```

**Uruchom:** test zielony; `S28SolutionTest.ChainIsAssembledExplicitlySoAFestivalCanSkipAutoRefunds` składa ścieżkę festiwalową bez zwrotów automatycznych, `HandlerThatDecidesStopsTheChainUnlikeADecorator` sprawdza, że ogniwo, które zdecydowało, nie woła następnika, a `EndOfTheChainHasNoSuccessor` - że koniec łańcucha jest ogniwem.
**Co powiedzieć:** koniec łańcucha jest ogniwem, więc żądanie nie może "spaść" bez odpowiedzi. Kontrast z Decoratorem: dekorator zawsze woła wnętrze i dokłada coś do wyniku, a tu odpowiedź pochodzi od dokładnie jednego ogniwa. Pytanie kontrolne: czy po obsłużeniu ktoś dalej musi pracować? Audyt decyzji byłby więc Decoratorem wokół `RefundApprover`, a nie kolejnym ogniwem.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s28 2 3`

### Rozwiązanie i uzasadnienie

`Step3`: pięć ogniw, każde zna tylko swój warunek i następnika, kolejność złożona w jednym konstruktorze, walidacja kwoty przed łańcuchem. Nowy poziom (np. dyrektor do 5000.00) to nowe ogniwo i jedna linia w złożeniu.

### Pułapki

- Przestawienie ogniw "dla porządku" (kasjer przed AUTO) - 5000.00 trzy dni przed seansem przestaje być zwrotem automatycznym; wychwyci to przypadek "AUTO nie ma limitu kwoty".
- Ogniwa z mutowalnym `next` (setter `Then(...)`) współdzielone między dwoma łańcuchami - drugie złożenie zmienia pierwsze. Tu `_next` jest `readonly`.
- Walidacja kwoty jako pierwsze ogniwo - łańcuch podany z zewnątrz mógłby ją pominąć.
- Granica 24 h: `TotalHours` to ułamek godzin (Java `toHours()` obcina minuty, przy progu całkowitym wynik jest ten sam). Przepisanie na `.Hours >= 24` (składowa godzin, zawsze 0-23) albo zaokrąglanie łatwo zmienia wynik dla 23 h 59 min albo 3 dni - wychwyci to tabela granic.

### Pytanie do sali

Biznes chce logować każdą decyzję o zwrocie razem z tym, kto ją podjął. Ogniwo czy dekorator - i dlaczego?

## Scena s29. Introduce Boundary Object - odpowiedź terminala płatniczego

**Temat ze slajdów:** 9.3. Surowe dane z zewnątrz w kodzie domeny; 9.4. Obiekt graniczny: sprawdzone dane na granicy systemu; 9.5. Adapter czy obiekt graniczny?
**Namespace:** `Training.Workshop.M5.S29BoundaryObject` · **Test:** `scripts/warsztat.sh --lang cs test m5/s29` (`S29EquivalenceTest`, `S29SolutionTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `PaymentService` dostaje od starego terminala tekst `OK;AUTH-R1;4500` (status, kod, kwota w groszach) i rozbiera go w każdej metodzie osobno: `Split`, `StartsWith`, `LastIndexOf`. Wprowadzamy niezmienny obiekt graniczny `PaymentConfirmation`, wydzielamy mapper `TerminalResponseMapper`, a na końcu chowamy klienta terminala za portem `IPaymentTerminal` z adapterem `LegacyTerminalAdapter`.

**Zasada:** Boundary Object to zwalidowany obraz danych z zewnątrz w języku domeny: jeśli obiekt istnieje, jest poprawny, więc domena niczego nie parsuje ani nie zgaduje. Adapter tłumaczy wywołanie (jak zawołać obcy system), Boundary Object - dane (co wpuszczamy do domeny); zwykle występują razem. Walidacja na granicy zmienia typ i moment błędu dla danych błędnych - to decyzja do zatwierdzenia, nie skutek uboczny.

**Efekt:** Format terminala zna tylko mapper, a serwis zależy wyłącznie od portu i widzi `PaymentConfirmation`. Dla poprawnych odpowiedzi wynik jest identyczny. Świadomie zmienia się zachowanie dla odpowiedzi zniekształconych: są odrzucane na granicy przez `ArgumentException("malformed terminal response: ...")`, zamiast wybuchać trzy metody dalej albo drukować pusty kod na paragonie.

**Różnica względem Javy:** zagnieżdżony enum `PaymentConfirmation.Outcome` to enum najwyższego poziomu `Outcome { Approved, Declined, Error }` (w C# typ zagnieżdżony o tej samej nazwie co właściwość `Outcome` dałby konflikt nazw), a odpowiedź serwisu wypisuje go przez `ToString().ToUpperInvariant()` (`DECLINED 51` jak w Javie). Rekord z konstruktorem kompaktowym to `sealed record` z jawnym konstruktorem i właściwościami tylko do odczytu (bez `init`), więc wyrażenie `with` nie ominie walidacji. `long.Parse`/`decimal.Parse` rzucają `FormatException` (nie `ArgumentException` jak `NumberFormatException` w Javie), dlatego mapper używa `decimal.TryParse` i sam rzuca `ArgumentException` z komunikatem granicy. Terminal podstawiony w `ServiceWorksWithAnyTerminalBehindThePort` to prywatna klasa `FakeTerminal : IPaymentTerminal` zamiast lambdy.

### Co widzimy

`LegacyTerminalClient` (nie zmieniamy go) przyjmuje grosze i odpowiada tekstem: kwota <= 0 daje `ERROR;AMOUNT;0`, powyżej 1000.00 `DECLINED;51;0`, powyżej 300.00 autoryzację częściową do 300.00. Serwis trzyma surowe odpowiedzi w słowniku i każda metoda sama je rozbiera:

```csharp
var grosze = decimal.ToInt64(amount.Amount * 100);
var raw = _terminal.Authorize(bookingId, grosze);
_responses[bookingId] = raw;
var parts = raw.Split(';');
if (parts[0] != "OK")
{
    return parts[0] + " " + parts[1];
}
var authorized = long.Parse(parts[2], CultureInfo.InvariantCulture);
if (authorized < grosze)
{
    return "PARTIAL " + parts[1] + " brakuje "
        + ((grosze - authorized) / 100m).ToString("0.00", CultureInfo.InvariantCulture);
}
```

Punkt wejścia testu to korzeń kompozycji `PaymentModule.PaymentService(terminal)`. `S29EquivalenceTest` gra sesje płatności: pełna autoryzacja, częściowa, granica 300.00 i grosz ponad, odmowa i błąd kwoty, brak płatności, ponowna płatność nadpisująca poprzednią - za każdym razem z potwierdzeniem i sumą autoryzacji.

### Krok 1: Introduce Boundary Object

**Zmiany:**

1. Utwórz nowy plik `PaymentConfirmation.cs`: enum `Outcome { Approved, Declined, Error }` i `sealed record PaymentConfirmation` z jawnym konstruktorem `(Outcome outcome, string code, Money authorized)`. W konstruktorze walidacja: kod niepusty, kwota nieujemna, tylko `Approved` może autoryzować pieniądze. Dodaj właściwość `Approved` i metodę `IsPartialFor(requested)`.
2. W `PaymentService` wydziel prywatną metodę `Parse(raw)` (wyodrębnienie metody) wołaną raz, zaraz po wywołaniu terminala: sprawdza liczbę pól i znany status, kwotę parsuje przez `decimal.TryParse`, a dla błędnych danych rzuca `ArgumentException("malformed terminal response: ...")`.
3. Słownik `_responses` zamień na `Dictionary<string, PaymentConfirmation> _confirmations`; `Receipt` i `TotalAuthorized` czytają właściwości obiektu zamiast parsować tekst.

**Przed:**

```csharp
var grosze = decimal.ToInt64(amount.Amount * 100);
var raw = _terminal.Authorize(bookingId, grosze);
_responses[bookingId] = raw;
var parts = raw.Split(';');
if (parts[0] != "OK")
{
    return parts[0] + " " + parts[1];
}
var authorized = long.Parse(parts[2], CultureInfo.InvariantCulture);
if (authorized < grosze)
{
    return "PARTIAL " + parts[1] + " brakuje "
        + ((grosze - authorized) / 100m).ToString("0.00", CultureInfo.InvariantCulture);
}
return "PAID " + parts[1];
```

**Po (`PaymentConfirmation.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

/// <summary>Wynik autoryzacji w języku kina (w Javie typ zagnieżdżony PaymentConfirmation.Outcome).</summary>
public enum Outcome { Approved, Declined, Error }

/// <summary>Boundary Object: obiekt, który istnieje, jest poprawny. Właściwości bez init - with nie ominie walidacji.</summary>
public sealed record PaymentConfirmation
{
    public PaymentConfirmation(Outcome outcome, string code, Money authorized)
    {
        ArgumentNullException.ThrowIfNull(authorized);
        if (string.IsNullOrWhiteSpace(code))
        {
            throw new ArgumentException("code must not be blank", nameof(code));
        }
        if (authorized.CompareTo(Money.Zero) < 0)
        {
            throw new ArgumentException("authorized amount must not be negative", nameof(authorized));
        }
        if (outcome != Outcome.Approved && authorized.IsGreaterThan(Money.Zero))
        {
            throw new ArgumentException("only approved payment can authorize money", nameof(authorized));
        }
        Outcome = outcome;
        Code = code;
        Authorized = authorized;
    }

    public Outcome Outcome { get; }

    public string Code { get; }

    public Money Authorized { get; }

    public bool Approved => Outcome == Outcome.Approved;

    /// <summary>Autoryzacja częściowa: terminal zgodził się na mniej, niż prosiliśmy.</summary>
    public bool IsPartialFor(Money requested)
    {
        return Approved && Authorized.CompareTo(requested) < 0;
    }
}
```

**Po (`PaymentService.cs`):**

```csharp
private readonly Dictionary<string, PaymentConfirmation> _confirmations = [];
...
public string Pay(string bookingId, Money amount)
{
    var grosze = decimal.ToInt64(amount.Amount * 100);
    var confirmation = Parse(_terminal.Authorize(bookingId, grosze));
    _confirmations[bookingId] = confirmation;
    if (!confirmation.Approved)
    {
        return confirmation.Outcome.ToString().ToUpperInvariant() + " " + confirmation.Code;
    }
    if (confirmation.IsPartialFor(amount))
    {
        return "PARTIAL " + confirmation.Code + " brakuje " + amount.Minus(confirmation.Authorized);
    }
    return "PAID " + confirmation.Code;
}

public string Receipt(string bookingId)
{
    if (!_confirmations.TryGetValue(bookingId, out var confirmation))
    {
        return "Brak platnosci " + bookingId;
    }
    if (!confirmation.Approved)
    {
        return "Platnosc " + bookingId + " nieudana: " + confirmation.Code;
    }
    return "Platnosc " + bookingId + ": " + confirmation.Authorized + " (" + confirmation.Code + ")";
}

public Money TotalAuthorized()
{
    var total = Money.Zero;
    foreach (var confirmation in _confirmations.Values)
    {
        total = total.Plus(confirmation.Authorized);
    }
    return total;
}

private static PaymentConfirmation Parse(string raw)
{
    var parts = raw.Split(';');
    if (parts.Length != 3)
    {
        throw new ArgumentException("malformed terminal response: " + raw);
    }
    var outcome = parts[0] switch
    {
        "OK" => Outcome.Approved,
        "DECLINED" => Outcome.Declined,
        "ERROR" => Outcome.Error,
        _ => throw new ArgumentException("malformed terminal response: " + raw),
    };
    if (!decimal.TryParse(parts[2], NumberStyles.AllowLeadingSign | NumberStyles.AllowDecimalPoint | NumberStyles.AllowExponent, CultureInfo.InvariantCulture, out var grosze))
    {
        throw new ArgumentException("malformed terminal response: " + raw);
    }
    return new PaymentConfirmation(outcome, parts[1], new Money(grosze / 100m));
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s29` - zielone.
**Co powiedzieć:** zamiast trzech parserów jest jeden, w miejscu wejścia danych. `TotalAuthorized` nie sprawdza już `StartsWith("OK")` i po prostu sumuje - niezmiennik obiektu gwarantuje zero dla odmowy. `Parse` sprawdza liczbę pól i znany status: to pierwszy moment, w którym zmienia się zachowanie dla danych błędnych (`Start` przyjąłby `OK;;4500` i wydrukował pusty kod).
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s29 0 1`

### Krok 2: mapper na granicy

**Zmiany:**

1. Przenieś statyczną metodę `Parse` do nowej klasy `TerminalResponseMapper` jako `ToConfirmation(raw)` (przeniesienie metody); przeliczenie złotych na grosze wydziel tam jako `ToGrosze(amount)`.
2. W serwisie dodaj pole `_mapper = new TerminalResponseMapper()`; `Pay` woła mapper po obu stronach wywołania terminala.

**Przed:**

```csharp
private static PaymentConfirmation Parse(string raw)
{
    var parts = raw.Split(';');
    if (parts.Length != 3)
    {
        throw new ArgumentException("malformed terminal response: " + raw);
    }
    var outcome = parts[0] switch
    {
        "OK" => Outcome.Approved,
        "DECLINED" => Outcome.Declined,
        "ERROR" => Outcome.Error,
        _ => throw new ArgumentException("malformed terminal response: " + raw),
    };
    ...
}
```

**Po (`TerminalResponseMapper.cs`, nowy plik):**

```csharp
using System.Globalization;
using Training.Workshop.Shared;

public sealed class TerminalResponseMapper
{
    public long ToGrosze(Money amount)
    {
        return decimal.ToInt64(amount.Amount * 100);
    }

    public PaymentConfirmation ToConfirmation(string raw)
    {
        var parts = raw.Split(';');
        if (parts.Length != 3)
        {
            throw Malformed(raw);
        }
        var outcome = parts[0] switch
        {
            "OK" => Outcome.Approved,
            "DECLINED" => Outcome.Declined,
            "ERROR" => Outcome.Error,
            _ => throw Malformed(raw),
        };
        if (!decimal.TryParse(parts[2], NumberStyles.AllowLeadingSign | NumberStyles.AllowDecimalPoint | NumberStyles.AllowExponent, CultureInfo.InvariantCulture, out var grosze))
        {
            throw Malformed(raw);
        }
        return new PaymentConfirmation(outcome, parts[1], new Money(grosze / 100m));
    }

    private static ArgumentException Malformed(string raw) => new("malformed terminal response: " + raw);
}
```

**Po (`PaymentService.cs`, w `Pay`):**

```csharp
private readonly TerminalResponseMapper _mapper = new();
...
var raw = _terminal.Authorize(bookingId, _mapper.ToGrosze(amount));
var confirmation = _mapper.ToConfirmation(raw);
```

**Uruchom:** test zielony; `S29SolutionTest.MalformedResponseIsRejectedAtTheBoundary` sprawdza `OK;AUTH-R1`, `MAYBE;X;0`, `OK;;4500` i `OK;AUTH-R1;abc`, a `MapperTranslatesUnitsBothWays` - grosze w obie strony.
**Co powiedzieć:** mapper to jedyne miejsce, które zna format `STATUS;KOD;KWOTA` i grosze w obie strony. Serwis nie zna już formatu, ale wciąż sam woła `LegacyTerminalClient` i składa wywołanie z mapperem.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s29 1 2`

### Krok 3: port domeny i adapter

**Zmiany:**

1. Utwórz interfejs `IPaymentTerminal` z metodą `PaymentConfirmation Authorize(string bookingId, Money amount)` oraz klasę `LegacyTerminalAdapter : IPaymentTerminal`, która łączy `LegacyTerminalClient` z mapperem.
2. W konstruktorze `PaymentService` zmień typ parametru z `LegacyTerminalClient` na `IPaymentTerminal` (zmiana sygnatury); `Pay` woła `_terminal.Authorize(bookingId, amount)` i nie widzi już groszy ani mappera.
3. W korzeniu kompozycji `PaymentModule` owiń klienta adapterem.

**Przed:**

```csharp
private readonly LegacyTerminalClient _terminal;
private readonly TerminalResponseMapper _mapper = new();
...
public string Pay(string bookingId, Money amount)
{
    var raw = _terminal.Authorize(bookingId, _mapper.ToGrosze(amount));
    var confirmation = _mapper.ToConfirmation(raw);
    ...
}
```

**Po (`IPaymentTerminal.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

/// <summary>Port domeny - terminal widziany oczami kina: Money na wejściu, obiekt graniczny na wyjściu.</summary>
public interface IPaymentTerminal
{
    PaymentConfirmation Authorize(string bookingId, Money amount);
}
```

**Po (`LegacyTerminalAdapter.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public sealed class LegacyTerminalAdapter : IPaymentTerminal
{
    private readonly LegacyTerminalClient _client;
    private readonly TerminalResponseMapper _mapper = new();

    public LegacyTerminalAdapter(LegacyTerminalClient client)
    {
        ArgumentNullException.ThrowIfNull(client);
        _client = client;
    }

    public PaymentConfirmation Authorize(string bookingId, Money amount)
    {
        return _mapper.ToConfirmation(_client.Authorize(bookingId, _mapper.ToGrosze(amount)));
    }
}
```

**Po (`PaymentService.cs`; dwie pierwsze linie `Pay` zastępuje jedna, reszta metody bez zmian):**

```csharp
private readonly IPaymentTerminal _terminal;
private readonly Dictionary<string, PaymentConfirmation> _confirmations = [];

public PaymentService(IPaymentTerminal terminal)
{
    ArgumentNullException.ThrowIfNull(terminal);
    _terminal = terminal;
}
...
var confirmation = _terminal.Authorize(bookingId, amount);
```

**Po (`PaymentModule.cs`):**

```csharp
public static PaymentService PaymentService(LegacyTerminalClient terminal)
{
    return new PaymentService(new LegacyTerminalAdapter(terminal));
}
```

**Uruchom:** test zielony; `ServiceWorksWithAnyTerminalBehindThePort` używa ręcznego `FakeTerminal` zamiast terminala, a `DomainServiceSeesNeitherTheLegacyClientNorTheMapper` sprawdza sygnatury serwisu refleksją.
**Co powiedzieć:** granica jest kompletna: Adapter (kształt wywołania), mapper (format danych), Boundary Object (poprawna wartość w języku domeny). Serwis da się testować bez terminala i bez tekstowych odpowiedzi.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s29 2 3`

### Rozwiązanie i uzasadnienie

`Step3`: `PaymentService` zna tylko `IPaymentTerminal` i `PaymentConfirmation`, `LegacyTerminalAdapter` implementuje port, `TerminalResponseMapper` jako jedyny zna grosze i tekst odpowiedzi. Zmiana zachowania dla odpowiedzi zniekształconych jest nazwana i opisana testem (`BoundaryObjectCannotBeInvalid`, `MalformedResponseIsRejectedAtTheBoundary`).

### Pułapki

- Obiekt graniczny bez walidacji (zwykłe DTO, rekord pozycyjny z `init`) - domena znowu musi sprawdzać i zgadywać, a `with` stworzy niepoprawną kopię.
- Zmiana typu i momentu błędu bez zapisania decyzji: w `Start` `OK;AUTH-R1` dawało `IndexOutOfRangeException` dopiero przy parsowaniu kwoty, a `OK;;4500` - paragon z pustym kodem.
- Adapter, który przepuszcza surowy tekst dalej "dla elastyczności" - port ma mówić językiem domeny.
- Kopiowanie każdego DTO obcego systemu bez potrzeby - osobny model ma sens, gdy granica oddziela różne semantyki lub tempo zmian.
- Przeliczanie na grosze jest kontraktem: `decimal.ToInt64` po cichu obcina ułamek grosza (Java `longValueExact` rzuca wyjątek). Przy `Money` ze skalą 2 ułamka nie ma, ale kwota spoza `Money` zostałaby zaokrąglona w dół bez śladu.

### Pytanie do sali

Terminal zaczyna zwracać nowy status `PENDING`. W którym miejscu kodu to zobaczycie i kto powinien zdecydować, co z nim zrobić?

## Scena s30. Extract Facade - zakup biletów w sklepie i w kasie

**Temat ze slajdów:** 11.1. Klienci sami koordynują podsystemy; 11.2. Wyodrębnienie Fasady: jedna operacja zamiast znajomości kroków
**Namespace:** `Training.Workshop.M5.S30Facade` · **Test:** `scripts/warsztat.sh --lang cs test m5/s30` (`S30BehaviourChangeTest`, `S30EquivalenceTest`, `S30SolutionTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `OnlineShop.Buy` i `BoxOffice.Sell` same orkiestrują cztery podsystemy: cennik, rejestr miejsc, bramkę płatności i wysyłkę maili. Kod kasy to kopia sklepu sprzed lat i ma celowo zostawiony błąd legacy: po odmowie płatności nie zwalnia miejsc. Wyjmujemy orkiestrację ze sklepu do `BookingFacade`, przepinamy na nią kasę (to świadomie naprawia błąd), a na końcu ukrywamy podsystemy i domykamy niezmiennik blokiem `finally`.

**Zasada:** Facade daje klientom jedną spójną operację zamiast znajomości kroków i ich kolejności. Procedura: test efektów obu klientów, Extract Method orkiestracji, Move Method do fasady, przepięcie drugiego klienta, zawężenie widoczności podsystemów. Gdy kopie orkiestracji już się rozjechały, przepięcie klienta zmienia jego zachowanie - to naprawa błędu, którą dokumentuje się osobnym testem, a nie "czysta" refaktoryzacja.

**Efekt:** Kanały sprzedaży znają tylko fasadę i własną opłatę (sklep 2.00 za miejsce, kasa zero), a kolejność cena - rezerwacja - płatność z kompensacją - mail jest w jednym miejscu. Zachowanie świadomie zmienia się dwa razy: w kroku 2 kasa zaczyna zwalniać miejsca po odmowie płatności, w kroku 3 miejsca są zwalniane także wtedy, gdy bramka rzuci wyjątkiem.

**Różnica względem Javy:** widoczność pakietowa to `internal`, ale warsztat jest jednym projektem (assembly), więc `internal` nie odgradza podsystemów od reszty kodu tak jak pakiet w Javie - granicę pilnuje test refleksji (`SubsystemIsHiddenBehindTheFacade`: typy podsystemów mają `IsPublic == false`, a fasada nie ma publicznych konstruktorów). Prawdziwą granicę dałby osobny projekt. `assumeTrue` z JUnit nie ma odpowiednika w xUnit 2, więc `StartBoxOfficeLeaksSeatsAfterDeclinedPayment` po `jump` do kroku 2 lub 3 wychodzi wcześniej (`return`) i jest zielony, a nie pominięty - jak testy pułapek w s11 i s17. `Cinema` buduje części w konstruktorze i wystawia kanały jako właściwości `OnlineShop` i `BoxOffice`; `State()` ręcznie odtwarza format `toString()` z Javy, więc migawki są identyczne.

### Co widzimy

Dwie metody wyglądają prawie tak samo. Pokaż je obok siebie na rzutniku i poproś salę o znalezienie różnic, zanim cokolwiek wydzielisz:

```csharp
// BoxOffice.Sell - kopia OnlineShop.Buy bez opłaty rezerwacyjnej
var total = _prices.Price(order.TicketType).Times(order.Seats.Count);
try
{
    _seats.Reserve(order.Screening, order.Seats);
}
catch (InvalidOperationException exception)
{
    return "ERROR " + exception.Message;
}
if (!_payments.Charge(order.Card, total))
{
    return "ERROR payment declined";          // sklep w tym miejscu woła _seats.Release(...)
}
```

`Cinema` to korzeń kompozycji i stabilny punkt wejścia: składa podsystemy i oba kanały, a `State()` pokazuje miejsca, dziennik płatności i wysłane maile naraz. `S30EquivalenceTest` gra sesje, w których zachowanie ma zostać bez zmian (zakup w sklepie z opłatą, w kasie bez opłaty, miejsce zajęte przez drugi kanał, odmowa w sklepie, nieznany typ biletu przed rezerwacją, różne seanse). Odmowa w kasie celowo jest w osobnym `S30BehaviourChangeTest`.

### Krok 1: Extract Facade ze sklepu

**Zmiany:**

1. W `OnlineShop.Buy` wydziel całą orkiestrację do metody `Book(order)` (wyodrębnienie metody), a potem zamień stałą opłaty na parametr (wprowadzenie parametru): `Book(order, feePerSeat)`.
2. Przenieś `Book` do nowej klasy `BookingFacade` (przeniesienie metody) z czterema podsystemami w konstruktorze.
3. W `OnlineShop` zostają pole `_booking` i stała `BookingFee`; `Cinema` tworzy fasadę i podaje ją sklepowi. Kasa bez zmian.

**Przed (`OnlineShop.cs`):**

```csharp
public string Buy(TicketOrder order)
{
    var total = _prices.Price(order.TicketType).Plus(BookingFee).Times(order.Seats.Count);
    try
    {
        _seats.Reserve(order.Screening, order.Seats);
    }
    catch (InvalidOperationException exception)
    {
        return "ERROR " + exception.Message;
    }
    if (!_payments.Charge(order.Card, total))
    {
        _seats.Release(order.Screening, order.Seats);
        return "ERROR payment declined";
    }
    _mailer.Send(order.Email, "Bilety " + order.Screening + " " + string.Join(",", order.Seats) + " = " + total);
    return "OK " + total;
}
```

**Po (`BookingFacade.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public sealed class BookingFacade
{
    private readonly SeatRegistry _seats;
    private readonly PriceList _prices;
    private readonly PaymentClient _payments;
    private readonly TicketMailer _mailer;

    public BookingFacade(SeatRegistry seats, PriceList prices, PaymentClient payments, TicketMailer mailer)
    {
        _seats = seats;
        _prices = prices;
        _payments = payments;
        _mailer = mailer;
    }

    public string Book(TicketOrder order, Money feePerSeat)
    {
        var total = _prices.Price(order.TicketType).Plus(feePerSeat).Times(order.Seats.Count);
        try
        {
            _seats.Reserve(order.Screening, order.Seats);
        }
        catch (InvalidOperationException exception)
        {
            return "ERROR " + exception.Message;
        }
        if (!_payments.Charge(order.Card, total))
        {
            _seats.Release(order.Screening, order.Seats);
            return "ERROR payment declined";
        }
        _mailer.Send(order.Email, "Bilety " + order.Screening + " " + string.Join(",", order.Seats) + " = " + total);
        return "OK " + total;
    }
}
```

**Po (`OnlineShop.cs`, cały plik):**

```csharp
using Training.Workshop.Shared;

public sealed class OnlineShop
{
    private static readonly Money BookingFee = Money.Of("2.00");

    private readonly BookingFacade _booking;

    public OnlineShop(BookingFacade booking)
    {
        ArgumentNullException.ThrowIfNull(booking);
        _booking = booking;
    }

    public string Buy(TicketOrder order)
    {
        return _booking.Book(order, BookingFee);
    }
}
```

**Po (`Cinema.cs`, pole `_booking` i konstruktor):**

```csharp
private readonly BookingFacade _booking;
...
public Cinema()
{
    _booking = new BookingFacade(_seats, _prices, _payments, _mailer);
    _onlineShop = new OnlineShop(_booking);
    _boxOffice = new BoxOffice(_seats, _prices, _payments, _mailer);
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s30` - zielone; `Step1StillLeaksBecauseOnlyTheShopUsesTheFacade` pokazuje, że błąd kasy wciąż żyje.
**Co powiedzieć:** zaczynamy od klienta, którego orkiestracja jest poprawna (kompensuje odmowę płatności). To, czym różnią się kanały, przychodzi parametrem, a nie flagą `if (online)`.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s30 0 1`

### Krok 2: kasa na fasadzie - świadoma zmiana zachowania

**Zmiany:**

1. W konstruktorze `BoxOffice` zamień cztery podsystemy na jedną zależność `BookingFacade` (zmiana sygnatury).
2. `Sell` woła `_booking.Book(order, Money.Zero)`; skopiowaną orkiestrację usuń.
3. Popraw składanie w `Cinema` - kasa też dostaje fasadę.

**Przed:**

```csharp
public string Sell(TicketOrder order)
{
    var total = _prices.Price(order.TicketType).Times(order.Seats.Count);
    try
    {
        _seats.Reserve(order.Screening, order.Seats);
    }
    catch (InvalidOperationException exception)
    {
        return "ERROR " + exception.Message;
    }
    if (!_payments.Charge(order.Card, total))
    {
        return "ERROR payment declined";
    }
    _mailer.Send(order.Email, "Bilety " + order.Screening + " " + string.Join(",", order.Seats) + " = " + total);
    return "OK " + total;
}
```

**Po (`BoxOffice.cs`):**

```csharp
private readonly BookingFacade _booking;

public BoxOffice(BookingFacade booking)
{
    ArgumentNullException.ThrowIfNull(booking);
    _booking = booking;
}

public string Sell(TicketOrder order)
{
    return _booking.Book(order, Money.Zero);
}
```

**Po (`Cinema.cs`, w konstruktorze):**

```csharp
_boxOffice = new BoxOffice(_booking);
```

**Uruchom:** test równoważności zielony, a `S30BehaviourChangeTest.Step2FacadeReleasesSeatsForBothChannels` pokazuje zmianę: po odmowie w kasie miejsce A1 jest wolne i sklep może je sprzedać (`OK 27.00` zamiast `ERROR seat taken A1`).
**Co powiedzieć:** usunięcie kopii usuwa także jej błąd, ale to zmiana zachowania kasy. Nazywamy ją, opisujemy testem i zatwierdzamy osobno (osobny commit "kasa zwalnia miejsca po odmowie płatności"). Test `StartBoxOfficeLeaksSeatsAfterDeclinedPayment` po `jump` do kroku 2 lub 3 kończy się wcześniej - `Start` kasy nie ma już własnej orkiestracji.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s30 1 2`

### Krok 3: ukryte podsystemy i niezmiennik w finally

**Zmiany:**

1. Zmień `public` na `internal` w `SeatRegistry`, `PriceList`, `PaymentClient` i `TicketMailer`, w ich metodach oraz w konstruktorze fasady - kanały sprzedaży dostają gotowy obiekt.
2. W `Book` otocz `Charge` blokiem `try/finally`: lokalna flaga `paid`, a `finally` zwalnia miejsca, gdy płatność się nie powiodła - także gdy bramka rzuciła wyjątkiem.

**Przed:**

```csharp
if (!_payments.Charge(order.Card, total))
{
    _seats.Release(order.Screening, order.Seats);
    return "ERROR payment declined";
}
```

**Po (`BookingFacade.cs`, w `Book`; konstruktor fasady dostaje `internal` zamiast `public`):**

```csharp
var paid = false;
try
{
    paid = _payments.Charge(order.Card, total);
}
finally
{
    if (!paid)
    {
        _seats.Release(order.Screening, order.Seats);
    }
}
if (!paid)
{
    return "ERROR payment declined";
}
```

**Uruchom:** test zielony; `S30SolutionTest` sprawdza, że podsystemy nie są publiczne (`SubsystemIsHiddenBehindTheFacade`), a kanały zależą tylko od fasady (`SalesChannelsKnowOnlyTheFacade`).
**Co powiedzieć:** zawężenie widoczności to zmiana API dla klientów spoza assembly - robimy je po migracji, jako osobny krok. `finally` to druga zmiana zachowania: karta `TIMEOUT` (wyjątek bramki) w kroku 2 zostawiała miejsce zajęte (`BeforeStep3AGatewayFailureLeakedSeats`), teraz nie (`GatewayFailureNoLongerLeavesSeatsTaken`). Fasada pilnuje niezmiennika "miejsce zajęte tylko po udanej płatności", ale nie jest transakcją: błąd wysyłki maila po płatności nadal niczego nie cofa.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s30 2 3`

### Rozwiązanie i uzasadnienie

`Step3`: `BookingFacade` to jedyne wejście do podsystemu i strażnik spójności, podsystemy są `internal`, a `Cinema` jest jedynym miejscem poza fasadą, które je składa. Obie zmiany zachowania (zwolnienie miejsc po odmowie w kasie i po awarii bramki) są nazwane i mają własne testy.

### Pułapki

- Przepięcie drugiego klienta jako "czysta refaktoryzacja" bez testu zmiany - ktoś mógł polegać na starym zachowaniu (np. raport zajętości kasy).
- Fasada jako kopia całego API podsystemów (God Class) zamiast jednej spójnej operacji.
- Parametr `bool online` zamiast opłaty - fasada zaczyna znać kanały sprzedaży.
- Ukrycie podsystemów przed migracją wszystkich klientów, także testów i rejestracji w kontenerze DI.
- `internal` w jednym projekcie to umowa, nie bariera - bez osobnego assembly albo testu architektury każdy w projekcie dalej może zawołać podsystem.
- Fasada przekazująca 1:1 bez żadnej reguły to Middle Man (scena s22).

### Pytanie do sali

Kasa chce sprzedawać bilety bez maila, gdy klient nie poda adresu. Parametr fasady, druga operacja fasady czy osobny klient podsystemów?

## Scena s31. Anti-Corruption Layer - licencje dystrybutora

**Temat ze slajdów:** 11.3. Obcy model w regułach domeny; 11.4. Warstwa antykorupcyjna: port, tłumacz i model domenowy; 11.5. Fasada a warstwa antykorupcyjna
**Namespace:** `Training.Workshop.M5.S31AntiCorruptionLayer` · **Test:** `scripts/warsztat.sh --lang cs test m5/s31` (`S31EquivalenceTest`, `S31SolutionTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `SettlementService` rozlicza się z dystrybutorem i decyduje, czy wolno grać film, myśląc w modelu dystrybutora: status `A`/`S`, daty `yyyyMMdd`, minimalna gwarancja w groszach jako tekst, udział `"45%"` albo `" 40 %"`, tytuł wielkimi literami. Wprowadzamy rekord domenowy `FilmLicense`, wydzielamy tłumacza `DistributorTranslator`, a na końcu port `IFilmLicenses` z implementacją `DistributorAcl`.

**Zasada:** Anti-Corruption Layer chroni język domeny przed obcym modelem: port mówi językiem kina, a tłumacz jako jedyny zna cudze konwencje. Fasada upraszcza dostęp do podsystemu, ale mówi jego językiem; ACL tłumaczy jeden model na drugi i często składa się z fasady, adaptera i tłumacza naraz. Tłumaczenie zachłanne zmienia zachowanie dla danych błędnych - polityka wobec złego rekordu to decyzja biznesowa.

**Efekt:** Reguły (czy licencja obejmuje dzień, ile się należy) są metodami `FilmLicense` i testuje się je bez typów dystrybutora, a `null` z obcego systemu nie wychodzi poza ACL. Dla poprawnych rekordów rozliczenia są identyczne. Walidacja jest świadomie zaostrzona: zła data to `ArgumentException("invalid legacy date: ...")`, a niespójna licencja (koniec przed początkiem, udział spoza 0-100) w ogóle nie powstanie.

**Różnica względem Javy:** `Optional<FilmLicense>` to `FilmLicense?`. Obcy `DistributorFeed` jest skompilowany z `#nullable disable` (jak cudza biblioteka sprzed adnotacji nullable), więc kompilator nie ostrzega, że `Fetch` może zwrócić `null` - port `IFilmLicenses.Find` musi jawnie zadeklarować brak wyniku typem `FilmLicense?`, żeby `null` z obcego systemu nie przeszedł niezauważony. `DateTimeParseException` to `FormatException` z `DateOnly.ParseExact(legacy, "yyyyMMdd", CultureInfo.InvariantCulture)`, a komunikat granicy jest ten sam. Daty są parsowane i wypisywane z jawnymi formatami invariant (`yyyy-MM-dd` w odpowiedziach, jak `LocalDate.toString()`). Tłumacz jest klasą `internal` z metodą instancji `ToLicense`, a źródło licencji podane w `ServiceWorksWithAnyLicenseSource` to prywatna klasa `FixedLicenses : IFilmLicenses` zamiast lambdy.

### Co widzimy

```csharp
if (record.StatusCode != "A")
{
    return "LICENCJA ZAWIESZONA " + filmCode;
}
if (!CanScreen(filmCode, day))
{
    return "LICENCJA NIEWAZNA " + filmCode + " w dniu " + day.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture);
}
var percent = int.Parse(record.Share.Replace("%", "").Trim(), CultureInfo.InvariantCulture);
var minimum = new Money(decimal.Parse(record.MinGuaranteeCents, CultureInfo.InvariantCulture) / 100);
var due = revenue.Percent(percent).Max(minimum);
var title = record.TitleUpper[0] + record.TitleUpper[1..].ToLowerInvariant();
```

Reguła "wolno grać, gdy licencja jest aktywna i obejmuje dzień" jest zapisana jako "kod `A` i dwie daty sparsowane z `yyyyMMdd`". Każda zmiana formatu u dystrybutora rozlewa się po regułach kina. Punkt wejścia to `SettlementModule.SettlementService(feed)`; `DistributorFeed` i `DistributorRecord` należą do obcego systemu i ich nie zmieniamy. `S31EquivalenceTest` sprawdza udział powyżej minimum, minimalną gwarancję, udział ze spacjami i zaokrąglenie, oba końce okresu licencji (włącznie) i dni tuż poza nimi, licencję zawieszoną i nieznany film - dla `Settle` i `CanScreen`.

### Krok 1: model domeny i tłumaczenie w jednym miejscu

**Zmiany:**

1. Utwórz nowy plik `FilmLicense.cs`: `sealed record` z właściwościami `FilmCode`, `Title`, `Active`, `ValidFrom`, `ValidTo`, `SharePercent`, `MinimumGuarantee`, niezmiennikami w konstruktorze (koniec nie przed początkiem, udział 0-100) i metodami `Covers(day)`, `AllowsScreeningOn(day)`, `DueFor(revenue)`.
2. W serwisie wydziel dekodowanie pól rekordu do prywatnej metody `ToLicense(record)` (wyodrębnienie metody): kod statusu, daty `yyyyMMdd`, grosze, procent i tytuł tłumaczone w jednym miejscu.
3. Reguły w `Settle` i `CanScreen` licz na licencji: `license.Active`, `license.Covers(day)`, `license.DueFor(revenue)`.

**Przed:**

```csharp
if (record.StatusCode != "A")
{
    return "LICENCJA ZAWIESZONA " + filmCode;
}
if (!CanScreen(filmCode, day))
{
    return "LICENCJA NIEWAZNA " + filmCode + " w dniu " + day.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture);
}
var percent = int.Parse(record.Share.Replace("%", "").Trim(), CultureInfo.InvariantCulture);
var minimum = new Money(decimal.Parse(record.MinGuaranteeCents, CultureInfo.InvariantCulture) / 100);
var due = revenue.Percent(percent).Max(minimum);
var title = record.TitleUpper[0] + record.TitleUpper[1..].ToLowerInvariant();
```

**Po (`FilmLicense.cs`, nowy plik):**

```csharp
using Training.Workshop.Shared;

public sealed record FilmLicense
{
    public FilmLicense(string filmCode, string title, bool active, DateOnly validFrom, DateOnly validTo,
        int sharePercent, Money minimumGuarantee)
    {
        ArgumentNullException.ThrowIfNull(filmCode);
        ArgumentNullException.ThrowIfNull(title);
        ArgumentNullException.ThrowIfNull(minimumGuarantee);
        if (validTo < validFrom)
        {
            throw new ArgumentException("license ends before it starts");
        }
        if (sharePercent < 0 || sharePercent > 100)
        {
            throw new ArgumentException("share out of range: " + sharePercent);
        }
        if (minimumGuarantee.CompareTo(Money.Zero) < 0)
        {
            throw new ArgumentException("minimum guarantee must not be negative");
        }
        FilmCode = filmCode;
        Title = title;
        Active = active;
        ValidFrom = validFrom;
        ValidTo = validTo;
        SharePercent = sharePercent;
        MinimumGuarantee = minimumGuarantee;
    }

    public string FilmCode { get; }

    public string Title { get; }

    public bool Active { get; }

    public DateOnly ValidFrom { get; }

    public DateOnly ValidTo { get; }

    public int SharePercent { get; }

    public Money MinimumGuarantee { get; }

    public bool Covers(DateOnly day)
    {
        return day >= ValidFrom && day <= ValidTo;
    }

    public bool AllowsScreeningOn(DateOnly day)
    {
        return Active && Covers(day);
    }

    /// <summary>Udział dystrybutora, ale nie mniej niż minimalna gwarancja.</summary>
    public Money DueFor(Money revenue)
    {
        return revenue.Percent(SharePercent).Max(MinimumGuarantee);
    }
}
```

**Po (`SettlementService.cs`):**

```csharp
public bool CanScreen(string filmCode, DateOnly day)
{
    var record = _feed.Fetch(filmCode);
    return record != null && ToLicense(record).AllowsScreeningOn(day);
}

public string Settle(string filmCode, DateOnly day, Money revenue)
{
    var record = _feed.Fetch(filmCode);
    if (record == null)
    {
        return "BRAK LICENCJI " + filmCode;
    }
    var license = ToLicense(record);
    if (!license.Active)
    {
        return "LICENCJA ZAWIESZONA " + filmCode;
    }
    if (!license.Covers(day))
    {
        return "LICENCJA NIEWAZNA " + filmCode + " w dniu " + day.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture);
    }
    return "ROZLICZENIE " + license.Title + " " + day.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture)
        + ": przychod " + revenue + ", udzial "
        + license.SharePercent + "%, minimum " + license.MinimumGuarantee
        + ", dla dystrybutora " + license.DueFor(revenue);
}

private static FilmLicense ToLicense(DistributorRecord record)
{
    var title = record.TitleUpper[0] + record.TitleUpper[1..].ToLowerInvariant();
    return new FilmLicense(
        record.FilmCode,
        title,
        record.StatusCode == "A",
        DateOnly.ParseExact(record.ValidFrom, "yyyyMMdd", CultureInfo.InvariantCulture),
        DateOnly.ParseExact(record.ValidTo, "yyyyMMdd", CultureInfo.InvariantCulture),
        int.Parse(record.Share.Replace("%", "").Trim(), CultureInfo.InvariantCulture),
        new Money(decimal.Parse(record.MinGuaranteeCents, CultureInfo.InvariantCulture) / 100));
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s31` - zielone.
**Co powiedzieć:** reguły brzmią teraz jak reguły kina, a nie jak `StatusCode != "A"`. Zauważ, że tłumaczenie jest zachłanne: rekord zawieszony z zepsutą datą dawał w `Start` po prostu "LICENCJA ZAWIESZONA", a teraz parsowanie dat wybuchnie wcześniej. Dla poprawnych danych z testu wynik jest ten sam - to przesunięcie trzeba nazwać.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s31 0 1`

### Krok 2: tłumacz jako osobna klasa

**Zmiany:**

1. Przenieś `ToLicense` do nowej klasy `DistributorTranslator` (przeniesienie metody); serwis dostaje pole `_translator = new DistributorTranslator()`.
2. W tłumaczu wydziel metody pomocnicze `Title(...)` i `Date(...)` (wyodrębnienie metody), a kod statusu zapisz jako stałą `Active` (wprowadzenie stałej).
3. W `Date` złap `FormatException` i rzuć `ArgumentException("invalid legacy date: ...")` z zachowaną przyczyną.

**Przed:**

```csharp
private static FilmLicense ToLicense(DistributorRecord record)
{
    var title = record.TitleUpper[0] + record.TitleUpper[1..].ToLowerInvariant();
    return new FilmLicense(
        record.FilmCode,
        title,
        record.StatusCode == "A",
        DateOnly.ParseExact(record.ValidFrom, "yyyyMMdd", CultureInfo.InvariantCulture),
        DateOnly.ParseExact(record.ValidTo, "yyyyMMdd", CultureInfo.InvariantCulture),
        int.Parse(record.Share.Replace("%", "").Trim(), CultureInfo.InvariantCulture),
        new Money(decimal.Parse(record.MinGuaranteeCents, CultureInfo.InvariantCulture) / 100));
}
```

**Po (`DistributorTranslator.cs`, nowy plik):**

```csharp
using System.Globalization;
using Training.Workshop.Shared;

public sealed class DistributorTranslator
{
    private const string Active = "A";

    public FilmLicense ToLicense(DistributorRecord record)
    {
        return new FilmLicense(
            record.FilmCode,
            Title(record.TitleUpper),
            record.StatusCode == Active,
            Date(record.ValidFrom),
            Date(record.ValidTo),
            int.Parse(record.Share.Replace("%", "").Trim(), CultureInfo.InvariantCulture),
            new Money(decimal.Parse(record.MinGuaranteeCents, CultureInfo.InvariantCulture) / 100));
    }

    private static string Title(string upper)
    {
        return upper[0] + upper[1..].ToLowerInvariant();
    }

    private static DateOnly Date(string legacy)
    {
        try
        {
            return DateOnly.ParseExact(legacy, "yyyyMMdd", CultureInfo.InvariantCulture);
        }
        catch (FormatException exception)
        {
            throw new ArgumentException("invalid legacy date: " + legacy, exception);
        }
    }
}
```

**Po (`SettlementService.cs`; prywatne `ToLicense` znika, oba wywołania idą na pole):**

```csharp
private readonly DistributorFeed _feed;
private readonly DistributorTranslator _translator = new();
...
return record != null && _translator.ToLicense(record).AllowsScreeningOn(day);
...
var license = _translator.ToLicense(record);
```

**Uruchom:** test zielony; `S31SolutionTest.AclRejectsMalformedLegacyDataAtTheBoundary` dokumentuje nowy komunikat dla daty `2026-10-01` w formacie ISO zamiast `yyyyMMdd`, a `AclTranslatesLegacyConventions` - tłumaczenie kodów, groszy i procentów.
**Co powiedzieć:** zepsuty rekord zgłasza się w języku granicy, z komunikatem, który mówi, co jest nie tak. To zmiana typu wyjątku i trzeba ją zatwierdzić. Serwis nadal jednak sam pobiera `DistributorRecord` i obsługuje jego `null`.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s31 1 2`

### Krok 3: port domenowy i warstwa antykorupcyjna

**Zmiany:**

1. Utwórz interfejs `IFilmLicenses` z metodą `FilmLicense? Find(string filmCode)` i klasę `DistributorAcl : IFilmLicenses`, która łączy `DistributorFeed` z tłumaczem i zamienia `null` obcego systemu na zadeklarowany typem `FilmLicense?`.
2. W konstruktorze serwisu zmień typ zależności z `DistributorFeed` na `IFilmLicenses` (zmiana sygnatury); `Settle` i `CanScreen` wołają `Find` i nie widzą już `DistributorRecord`.
3. Zmień widoczność tłumacza na `internal`, a w `SettlementModule` owiń klienta w `DistributorAcl`.

**Przed:**

```csharp
private readonly DistributorFeed _feed;
private readonly DistributorTranslator _translator = new();
...
public bool CanScreen(string filmCode, DateOnly day)
{
    var record = _feed.Fetch(filmCode);
    return record != null && _translator.ToLicense(record).AllowsScreeningOn(day);
}
```

**Po (`IFilmLicenses.cs`, nowy plik):**

```csharp
/// <summary>Port domeny - brak licencji to jawnie zadeklarowany null w typie FilmLicense?.</summary>
public interface IFilmLicenses
{
    FilmLicense? Find(string filmCode);
}
```

**Po (`DistributorAcl.cs`, nowy plik):**

```csharp
public sealed class DistributorAcl : IFilmLicenses
{
    private readonly DistributorFeed _feed;
    private readonly DistributorTranslator _translator = new();

    public DistributorAcl(DistributorFeed feed)
    {
        ArgumentNullException.ThrowIfNull(feed);
        _feed = feed;
    }

    public FilmLicense? Find(string filmCode)
    {
        var record = _feed.Fetch(filmCode);
        return record == null ? null : _translator.ToLicense(record);
    }
}
```

**Po (`SettlementService.cs`; `Settle` od `if (!license.Active)` w dół bez zmian):**

```csharp
private readonly IFilmLicenses _licenses;

public SettlementService(IFilmLicenses licenses)
{
    ArgumentNullException.ThrowIfNull(licenses);
    _licenses = licenses;
}

public bool CanScreen(string filmCode, DateOnly day)
{
    return _licenses.Find(filmCode)?.AllowsScreeningOn(day) ?? false;
}

public string Settle(string filmCode, DateOnly day, Money revenue)
{
    var license = _licenses.Find(filmCode);
    if (license == null)
    {
        return "BRAK LICENCJI " + filmCode;
    }
    ...
}
```

**Po (`SettlementModule.cs`):**

```csharp
public static SettlementService SettlementService(DistributorFeed feed)
{
    return new SettlementService(new DistributorAcl(feed));
}
```

**Uruchom:** test zielony; `DomainServiceUsesNoLegacyType` sprawdza sygnatury serwisu refleksją, `TranslatorIsAnImplementationDetailOfTheAcl` - że tłumacz nie jest publiczny, a `ServiceWorksWithAnyLicenseSource` rozlicza licencję festiwalową podaną ręcznym `FixedLicenses`, bez żadnego rekordu dystrybutora.
**Co powiedzieć:** żaden typ dystrybutora nie przekracza ACL, a "brak licencji" to zadeklarowany typem `FilmLicense?`, a nie przypadkowy `null` z biblioteki bez adnotacji. Fasada by tu nie wystarczyła - uprościłaby wywołania, ale domena dalej mówiłaby kodami `A`/`S`. ACL to też naturalny punkt dla Strangler Fig: za portem może stać stary i nowy dostawca licencji.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s31 2 3`

### Rozwiązanie i uzasadnienie

`Step3`: `SettlementService` zależy tylko od `IFilmLicenses` i `FilmLicense`, `DistributorAcl` jest jedyną klasą, która widzi `DistributorFeed` i `DistributorRecord`, a `internal` `DistributorTranslator` zna wszystkie konwencje dystrybutora. Zaostrzona walidacja jest opisana testami (`AclRejectsMalformedLegacyDataAtTheBoundary`, `LicenseProtectsItsInvariants`).

### Pułapki

- ACL bez różnicy modeli to drogi Middle Man - tu różnica jest realna (kody, grosze, daty i procenty jako tekst).
- Tłumaczenie zachłanne bez decyzji: zły rekord zawieszonej licencji zatrzymuje teraz rozliczenie. Odrzucić, pominąć z logiem czy przenieść do kwarantanny - to decyzja biznesowa.
- Przecieki modelu: `DistributorRecord` zwracany "tylko w jednym miejscu", nieoznaczony `null` z kodu `#nullable disable` przepuszczony dalej, kody `A`/`S` w enumie domeny.
- Normalizacja tytułu (`KRAINA LODU` daje `Kraina lodu`) też jest kontraktem (paragon, raport) - nie poprawiaj jej "przy okazji" na `Kraina Lodu` (np. przez `TextInfo.ToTitleCase`).

### Pytanie do sali

Dystrybutor dodaje status `T` (licencja tylko na pokazy prasowe). Gdzie pojawi się to pojęcie i jak nazwiecie je po stronie kina?

## Scena s32. Introduce Mediator - ekran wyboru miejsc

**Temat ze slajdów:** 14.1. Komponenty połączone każdy z każdym; 14.2. Wprowadzenie Mediatora: reguły współpracy w jednym miejscu; 14.3. Obserwator czy Mediator?
**Namespace:** `Training.Workshop.M5.S32Mediator` · **Test:** `scripts/warsztat.sh --lang cs test m5/s32` (`S32EquivalenceTest`, `S32SolutionTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** Ekran wyboru miejsc ma cztery komponenty: `SeatMap`, `Basket`, `PriceSummary` i `PayButton`, połączone "każdy z każdym" (11 referencji). Każdy po akcji użytkownika sam odświeża pozostałe, a kolejność odświeżania (suma, potem przycisk) jest powtórzona w czterech miejscach. Wprowadzamy `ISelectionMediator`: najpierw dla kliknięcia miejsca, potem dla wszystkich zdarzeń ze wspólnym `Refresh()`, a na końcu przycisk zgłasza tylko chęć płatności i komponenty znają wyłącznie mediatora.

**Zasada:** Mediator skupia reguły współpracy komponentów w jednym miejscu: komponent zgłasza, co się stało, a mediator decyduje, kto i w jakiej kolejności reaguje. Od Observera różni się miejscem reguł: obserwator rozgłasza zdarzenie nieznanym odbiorcom, mediator zna wszystkich uczestników i świadomie koordynuje ich reakcję. Ryzyko to God Mediator - w mediatorze zostaje koordynacja, a logika komponentów (cena, limit miejsc) zostaje w komponentach.

**Efekt:** Cztery referencje do interfejsu mediatora zamiast 11 między komponentami, a nowy komponent (np. licznik czasu rezerwacji) to zmiana w mediatorze, nie w czterech klasach. Migawka ekranu po każdej akcji jest identyczna jak w `Start`, także dla akcji ignorowanych.

**Różnica względem Javy:** C# nie pozwala użyć `this` w inicjalizatorze pola (CS0027), więc od kroku 1 komponenty, które dostają mediatora, są tworzone w konstruktorze `SeatSelectionScreen` (`_seatMap = new SeatMap(this);`), a nie w inicjalizatorze pola jak w Javie. Z tego samego powodu konstruktor ekranu nie znika w kroku 3. Pola komponentów wiązane późno przez `Connect(...)` wymagają w `Start` `= null!;` - kompilator nullable sam wskazuje zapach "każdy zna każdego" (obiekt istnieje w niepełnym stanie, dopóki ktoś nie zawoła `Connect`). `TreeSet` to `SortedSet<string>` z porządkiem ordinalnym, a format `toString()` kolekcji Javy jest odtworzony przez `"[" + string.Join(", ", ...) + "]"`. Test architektury (`EveryComponentKnowsOnlyTheMediator`) działa, bo komponenty mają jawne pola, a nie auto-właściwości; atrapa mediatora to prywatna klasa `RecordingMediator : ISelectionMediator`.

### Co widzimy

`SeatSelectionScreen` (stabilny punkt wejścia) tylko tworzy komponenty i łączy je metodami `Connect(...)`. Sala ma miejsca A1-C4, B2 jest sprzedane wcześniej, rząd C to VIP (35.00, pozostałe 25.00), voucher KINO10 daje -10%, przycisk jest aktywny dla 1-4 miejsc.

```csharp
// SeatMap.Click - mapa zna trzy inne komponenty i kolejność ich odświeżania
if (_selected.Remove(seat))
{
    _basket.Drop(seat);
}
else
{
    _selected.Add(seat);
    _basket.Add(seat);
}
_summary.Recalculate(_basket.Seats);
_payButton.Refresh(_basket.Seats.Count, _summary.Total);
```

Ta sama para "przelicz sumę, odśwież przycisk" jest w `Basket.Remove`, `Basket.Clear` i `PriceSummary.ApplyVoucher`, a `PayButton.Press` sam pyta koszyk i sumę, oznacza miejsca jako sprzedane i czyści koszyk, co uruchamia kaskadę odświeżeń. `S32EquivalenceTest` porównuje migawkę `Render()` po sekwencjach akcji: ekran startowy, kolejność kliknięć, ponowne kliknięcie, miejsca sprzedane i spoza sali, usuwanie z koszyka, limit 4 miejsc, voucher, płatność, czyszczenie koszyka.

### Krok 1: interfejs mediatora i pierwsze zdarzenie

**Zmiany:**

1. Utwórz nowy plik `ISelectionMediator.cs`: interfejs z metodą `void SeatToggled(string seat, bool selected)`; `SeatSelectionScreen` implementuje go (jak "dialog" w GoF).
2. Reakcję z `SeatMap.Click` (aktualizacja koszyka, przeliczenie sumy, odświeżenie przycisku) przenieś do metody `SeatToggled` w ekranie (wyodrębnienie i przeniesienie metody); `Click` wyznacza tylko `nowSelected` i zgłasza zdarzenie.
3. `SeatMap` dostaje mediatora w konstruktorze - ekran tworzy ją w swoim konstruktorze przez `_seatMap = new SeatMap(this);`; metoda `Connect(...)` mapy i jej trzy pola komponentów znikają.

**Przed (`SeatMap.cs`):**

```csharp
private Basket _basket = null!;
private PriceSummary _summary = null!;
private PayButton _payButton = null!;

internal void Connect(Basket basket, PriceSummary summary, PayButton payButton)
{
    _basket = basket;
    _summary = summary;
    _payButton = payButton;
}
...
if (_selected.Remove(seat))
{
    _basket.Drop(seat);
}
else
{
    _selected.Add(seat);
    _basket.Add(seat);
}
_summary.Recalculate(_basket.Seats);
_payButton.Refresh(_basket.Seats.Count, _summary.Total);
```

**Po (`ISelectionMediator.cs`, nowy plik):**

```csharp
/// <summary>Mediator - komponent zgłasza, CO się stało, a mediator decyduje, KTO ma zareagować.</summary>
public interface ISelectionMediator
{
    void SeatToggled(string seat, bool selected);
}
```

**Po (`SeatMap.cs`):**

```csharp
private readonly ISelectionMediator _mediator;

public SeatMap(ISelectionMediator mediator)
{
    ArgumentNullException.ThrowIfNull(mediator);
    _mediator = mediator;
}
...
var nowSelected = !_selected.Remove(seat);
if (nowSelected)
{
    _selected.Add(seat);
}
_mediator.SeatToggled(seat, nowSelected);
```

**Po (`SeatSelectionScreen.cs`):**

```csharp
public sealed class SeatSelectionScreen : ISelectionMediator
{
    private readonly SeatMap _seatMap;
    private readonly Basket _basket = new();
    private readonly PriceSummary _summary = new();
    private readonly PayButton _payButton = new();

    public SeatSelectionScreen()
    {
        _seatMap = new SeatMap(this);
        _basket.Connect(_seatMap, _summary, _payButton);
        _summary.Connect(_basket, _payButton);
        _payButton.Connect(_seatMap, _basket, _summary);
    }

    public void SeatToggled(string seat, bool selected)
    {
        if (selected)
        {
            _basket.Add(seat);
        }
        else
        {
            _basket.Drop(seat);
        }
        _summary.Recalculate(_basket.Seats);
        _payButton.Refresh(_basket.Seats.Count, _summary.Total);
    }
    ...
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s32` - zielone.
**Co powiedzieć:** komponent mówi "co się stało", a nie "kto ma zareagować". `new SeatMap(this)` w konstruktorze przekazuje `this` przed końcem konstrukcji ekranu - bezpieczne tylko dlatego, że mediator działa dopiero po akcji użytkownika (C# w ogóle nie pozwala na to w inicjalizatorze pola). Pozostałe komponenty są nadal połączone wprost: jeden komponent naraz.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s32 0 1`

### Krok 2: wszystkie zdarzenia i jedno Refresh()

**Zmiany:**

1. Dodaj do interfejsu zdarzenia `SeatRemoved`, `BasketCleared`, `VoucherApplied` i `SeatsPaid(IReadOnlyList<string>)`; ekran obsługuje je metodami (np. `SeatRemoved` woła `_seatMap.Deselect(seat)`).
2. `Basket`, `PriceSummary` i `PayButton` dostają mediatora w konstruktorze i zgłaszają zdarzenia zamiast odświeżać sąsiadów; przycisk zachowuje jeszcze `Connect(basket, summary)`, bo sam czyta dane do komunikatu.
3. Powtórzoną parę "przelicz sumę, odśwież przycisk" wydziel w ekranie do prywatnej metody `Refresh()` (wyodrębnienie metody) i użyj jej we wszystkich obsługach zdarzeń.

**Przed (`SeatSelectionScreen.cs`):**

```csharp
public void SeatToggled(string seat, bool selected)
{
    if (selected)
    {
        _basket.Add(seat);
    }
    else
    {
        _basket.Drop(seat);
    }
    _summary.Recalculate(_basket.Seats);
    _payButton.Refresh(_basket.Seats.Count, _summary.Total);
}
```

**Po (`ISelectionMediator.cs`):**

```csharp
public interface ISelectionMediator
{
    void SeatToggled(string seat, bool selected);

    void SeatRemoved(string seat);

    void BasketCleared();

    void VoucherApplied();

    void SeatsPaid(IReadOnlyList<string> seats);
}
```

**Po (`Basket.cs`):**

```csharp
private readonly List<string> _seats = [];
private readonly ISelectionMediator _mediator;

public Basket(ISelectionMediator mediator)
{
    ArgumentNullException.ThrowIfNull(mediator);
    _mediator = mediator;
}

/// <summary>Akcja użytkownika: usuwa miejsce z koszyka (i odznacza je na mapie).</summary>
public void Remove(string seat)
{
    if (!_seats.Remove(seat))
    {
        return;
    }
    _mediator.SeatRemoved(seat);
}

/// <summary>Akcja użytkownika: czyści koszyk. Woła ją też mediator po płatności.</summary>
public void Clear()
{
    _seats.Clear();
    _mediator.BasketCleared();
}
```

**Po (`PriceSummary.cs`):**

```csharp
private readonly ISelectionMediator _mediator;

public PriceSummary(ISelectionMediator mediator)
{
    ArgumentNullException.ThrowIfNull(mediator);
    _mediator = mediator;
}

/// <summary>Akcja użytkownika: voucher; nieznany kod usuwa zniżkę.</summary>
public void ApplyVoucher(string code)
{
    _discountPercent = code == "KINO10" ? 10 : 0;
    _mediator.VoucherApplied();
}
```

**Po (`PayButton.cs`):**

```csharp
private readonly ISelectionMediator _mediator;
private Basket _basket = null!;
private PriceSummary _summary = null!;

public PayButton(ISelectionMediator mediator)
{
    ArgumentNullException.ThrowIfNull(mediator);
    _mediator = mediator;
}

internal void Connect(Basket basket, PriceSummary summary)
{
    _basket = basket;
    _summary = summary;
}

/// <summary>Akcja użytkownika: płatność za zawartość koszyka.</summary>
public string Press()
{
    if (!_enabled)
    {
        return "Nieaktywny: " + _label;
    }
    var paid = _basket.Seats;
    var amount = _summary.Total;
    _mediator.SeatsPaid(paid);
    return "Zaplacono " + amount + " za " + string.Join(",", paid);
}
```

**Po (`SeatSelectionScreen.cs`):**

```csharp
public SeatSelectionScreen()
{
    _seatMap = new SeatMap(this);
    _basket = new Basket(this);
    _summary = new PriceSummary(this);
    _payButton = new PayButton(this);
    _payButton.Connect(_basket, _summary);
}
...
public void SeatRemoved(string seat)
{
    _seatMap.Deselect(seat);
    Refresh();
}

public void BasketCleared()
{
    _seatMap.DeselectAll();
    Refresh();
}

public void VoucherApplied()
{
    Refresh();
}

public void SeatsPaid(IReadOnlyList<string> seats)
{
    _seatMap.MarkSold(seats);
    _basket.Clear();
}

/// <summary>Jedno miejsce, które zna kolejność odświeżania: najpierw suma, potem przycisk.</summary>
private void Refresh()
{
    _summary.Recalculate(_basket.Seats);
    _payButton.Refresh(_basket.Seats.Count, _summary.Total);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** kolejność odświeżania jest zachowaniem - przycisk pokazuje kwotę, więc suma musi być przeliczona wcześniej. Teraz jest zapisana raz zamiast czterech razy. Uwaga na reentrancję: `SeatsPaid` woła `_basket.Clear()`, które znów woła mediatora (`BasketCleared`) - kaskada działa, ale jest ukryta.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s32 1 2`

### Krok 3: komponenty znają tylko mediatora

**Zmiany:**

1. Zamień w interfejsie `SeatsPaid(IReadOnlyList<string>)` na `string PayRequested()` (zmiana sygnatury).
2. Zbieranie miejsc i kwoty oraz składanie komunikatu "Zaplacono ..." przenieś z `PayButton.Press` do `PayRequested` w mediatorze; przycisk tylko zgłasza chęć płatności.
3. Usuń `Connect` i pola `_basket`/`_summary` z przycisku oraz ostatnie wywołanie `Connect` z konstruktora ekranu - zostaje w nim tylko tworzenie czterech komponentów z `this`.

**Przed:**

```csharp
public string Press()
{
    if (!_enabled)
    {
        return "Nieaktywny: " + _label;
    }
    var paid = _basket.Seats;
    var amount = _summary.Total;
    _mediator.SeatsPaid(paid);
    return "Zaplacono " + amount + " za " + string.Join(",", paid);
}
```

**Po (`ISelectionMediator.cs`, zmiana ostatniej metody):**

```csharp
/// <summary>Zwraca komunikat dla użytkownika.</summary>
string PayRequested();
```

**Po (`PayButton.cs`):**

```csharp
public string Press()
{
    if (!_enabled)
    {
        return "Nieaktywny: " + _label;
    }
    return _mediator.PayRequested();
}
```

**Po (`SeatSelectionScreen.cs`, w miejsce `SeatsPaid`):**

```csharp
public string PayRequested()
{
    var paid = _basket.Seats;
    var amount = _summary.Total;
    _seatMap.MarkSold(paid);
    _basket.Clear();
    return "Zaplacono " + amount + " za " + string.Join(",", paid);
}
```

**Uruchom:** test zielony; `S32SolutionTest.EveryComponentKnowsOnlyTheMediator` sprawdza pola komponentów refleksją, a `ComponentReportsWhatHappenedWithoutKnowingWhoReacts` testuje komponent z atrapą mediatora.
**Co powiedzieć:** przycisk pilnuje własnej reguły (aktywny dla 1-4 miejsc) i zgłasza "użytkownik chce zapłacić", a dane zbiera mediator. Ekran jest jedynym miejscem, które zna wszystkie cztery komponenty - i to jest zarazem ryzyko: pilnuj, żeby mediator nie zaczął liczyć cen.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s32 2 3`

### Rozwiązanie i uzasadnienie

`Step3`: `SeatSelectionScreen` implementuje `ISelectionMediator` i jako jedyny zna wszystkie komponenty; każdy komponent zna tylko interfejs mediatora. Reguły współpracy (kto reaguje na zdarzenie i w jakiej kolejności) są w jednym miejscu, a reguły komponentów (cena VIP, voucher, limit miejsc) zostały u siebie. Żadne pole nie potrzebuje już `= null!`.

### Pułapki

- God Mediator: mediator przejmuje logikę komponentów (cena VIP, limit 4 miejsc) albo obsługuje cały system zamiast jednego ekranu - dziel według spójnych grup reguł.
- Zmiana kolejności w `Refresh()` (przycisk przed sumą) - przycisk pokaże starą kwotę; wychwyci to przypadek z voucherem.
- Reentrancja: mediator woła komponent, który zgłasza zdarzenie z powrotem - podwójne odświeżenie albo nieskończona pętla.
- Mediator dla dwóch komponentów i jednej zależności - zwykłe wywołanie wystarczy.
- Zdarzenie domenowe dla wielu niezależnych odbiorców (mail, SMS, punkty po opłaceniu - scena s09) to Observer, a nie mediator ekranu.

### Pytanie do sali

Dochodzi licznik "rezerwacja wygasa za 10 minut", który po upływie czasu czyści koszyk. Ile klas zmienicie w `Start`, a ile w `Step3`?
