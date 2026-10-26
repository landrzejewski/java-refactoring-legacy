# Moduł 5. Refaktoryzacja do wzorców - warsztat CineLegacy: przewodnik prowadzącego

Trzydzieści dwie małe sceny w domenie kina CineLegacy. Każda pokazuje jedną refaktoryzację do wzorca (albo usunięcie wzorca) ze slajdów: od pytania "czy zawsze warto?" i usuwania zbędnych wzorców, przez ryzyka zmiany kontraktu i refaktoryzacje automatyczne w IDE, po wybór między podobnymi wzorcami (Polecenie, Strategia, Stan; Strategia i Metoda szablonowa; Łańcuch zobowiązań i Dekorator), granice systemu (Adapter, obiekt graniczny, Fasada, warstwa antykorupcyjna) oraz Budowniczego, Obserwatora, Mediatora i Odwiedzającego. Polskie nazwy wzorców i refaktoryzacji są takie jak na slajdach; tytuły scen zostają w wersji angielskiej, bo tak nazywają się refaktoryzacje w katalogu Kerievsky’ego i w IDE. Każda scena ma kod wyjściowy `start` z zapachem lub pułapką i gotowe snapshoty `stepN` po każdym ruchu, więc w dowolnym momencie można przeskoczyć do kolejnego kroku. Test równoważności pilnuje, że wzorzec wprowadzamy bez zmiany obserwowalnego zachowania, a tam, gdzie zachowanie celowo się zmienia (moment wyboru strategii, nowy niezmiennik buildera, wygaszony wariant, naprawiony błąd skopiowanej orkiestracji, zaostrzona walidacja na granicy), osobny test to dokumentuje.

## Jak prowadzić pokaz

```bash
scripts/warsztat.sh list m5              # sceny i kroki modułu 5
scripts/warsztat.sh test m5/s08          # testy jednej sceny
scripts/warsztat.sh test m5              # wszystkie sceny modułu (838 testów)
scripts/warsztat.sh diff m5/s08 1 2      # co zmienia krok 2 względem kroku 1 (0 = start)
scripts/warsztat.sh diff m5/s08 1 2 --word  # to samo, zmiany podświetlone w obrębie linii
scripts/warsztat.sh jump m5/s08 2        # kopiuje step2 do start (przeskok, gdy brakuje czasu)
scripts/warsztat.sh next m5/s08          # następny krok do start + podsumowanie zmian
scripts/warsztat.sh next                 # kolejny krok tej samej sceny (scena zapamiętana)
scripts/warsztat.sh prev                 # krok wstecz
scripts/warsztat.sh status               # który krok jest teraz w start
scripts/warsztat.sh reset m5/s08         # przywraca start z repozytorium
```

- Zasada: **jeden krok - jeden test - jedno zdanie komentarza**. Po każdym ruchu w IDE uruchamiamy test sceny; zielony pasek jest dowodem, że wzorzec nie zmienił kontraktu.
- **Krok po kroku bez numerów:** `scripts/warsztat.sh next` wstawia do `start` gotowy następny krok i wypisuje, co się zmieniło. `prev` cofa o krok, `status` mówi, który krok jest teraz w `start`. Skrypt pamięta ostatnią scenę, więc po pierwszym `next m5/sNN` wystarczy samo `next`. Ręczne zmiany w `start` zostają nadpisane - o to chodzi, gdy krok na żywo się rozjechał.
- Pracujemy na pakiecie `start`. Gdy coś pójdzie nie tak, `next` (albo `jump` do właściwego kroku), a po scenie `reset`.
- Test równoważności przechodzi przez `start` i wszystkie kroki. Jeśli uczestnicy pracują równolegle, to ten sam test jest ich kryterium ukończenia.
- Testy wołają sceny przez stabilne punkty wejścia (klient w rodzaju `PriceBoard`, `CancellationDesk`, `PaymentServices`, `Reports`, `Cinema`, `PaymentModule`, `SettlementModule` albo zachowany konstruktor), dzięki czemu `jump` do dowolnego kroku kompiluje się i przechodzi. Testy dokumentujące pułapkę samego `start` (s11, s17, s30) po `jump` są pomijane (JUnit pokazuje je jako "aborted") - to zamierzone.
- Motyw przewodni: **najpierw kontrakt, potem diagram klas**. W każdej scenie zaczynamy od pytania "co jest obserwowalne?" (wyjątek, kolejność efektów, moment wyboru, format trwały, typ i moment błędu na granicy). Siatkę bezpieczeństwa z sekcji 3 slajdów ćwiczy każda scena: test równoważności przed pierwszym ruchem, test różnicowy w s10, testy struktury w s26-s32.

## Mapa scen

Sceny są ułożone według numerów, tak jak pakiety i skrypt. Kolejność pokazu, zgodną z kolejnością slajdów, podaje sekcja „Wybór scen”.

| Scena | Temat ze slajdów | Kroki | Pakiet | Czas |
| --- | --- | --- | --- | --- |
| s01 | 6.1. Strategia, Polecenie czy Stan? Kto wybiera wariant i kiedy; 6.2. Strategia: sygnał w kodzie; 6.3. Strategia: kroki i wynik | 3 | `m5.s01_strategy` | ~12 min |
| s02 | 16.1. Zastąpienie instrukcji warunkowej polimorfizmem; 6.8. Typowe pomyłki przy wyborze | 3 | `m5.s02_polymorphism` | ~12 min |
| s03 | 16.2. Zastąpienie kodu typu klasą | 3 | `m5.s03_typecode` | ~12 min |
| s04 | 10.1. Ukrycie klas za Fabryką; 10.3. Fabryka jako strażnik spójności | 3 | `m5.s04_encapsulatefactory` | ~10 min |
| s05 | 10.2. Wyodrębnienie klasy fabryki; 10.3. Fabryka jako strażnik spójności | 3 | `m5.s05_extractfactory` | ~10 min |
| s06 | 12.1. Ukrycie budowy Kompozytu za Budowniczym; 12.2. Budowniczy: kiedy i na co uważać | 3 | `m5.s06_builder` | ~12 min |
| s07 | 8.1. Przeniesienie dodatków do Dekoratora; 8.2. Dekorator: na co uważać | 3 | `m5.s07_decorator` | ~12 min |
| s08 | 6.4. Stan: sygnał i tabela przejść; 6.5. Stan: wynik | 3 | `m5.s08_state` | ~15 min |
| s09 | 13.1. Zastąpienie zakodowanych powiadomień Obserwatorem; 13.2. Obserwator: zasady powiadamiania muszą być jawne | 3 | `m5.s09_observer` | ~15 min |
| s10 | 16.3. Zastąpienie ukrytego drzewa Kompozytem; 3.3. Test równoważności przed zmianą i po niej | 3 | `m5.s10_implicittree` | ~15 min |
| s11 | 16.4. Kompozyt przezroczysty czy bezpieczny | 2 | `m5.s11_safecomposite` | ~8 min |
| s12 | 16.5. Jeden element lub wiele oraz wyodrębnienie Kompozytu (zastąpienie rozróżnienia „jeden lub wiele” Kompozytem) | 3 | `m5.s12_onemany` | ~10 min |
| s13 | 16.5. Jeden element lub wiele oraz wyodrębnienie Kompozytu (wyodrębnienie Kompozytu) | 2 | `m5.s13_extractcomposite` | ~8 min |
| s14 | 9.1. Ujednolicenie interfejsów Adapterem; 9.2. Co adapter naprawdę musi przetłumaczyć | 3 | `m5.s14_adapter` | ~12 min |
| s15 | 6.6. Polecenie: łańcuch warunków zamieniony na rejestr poleceń | 3 | `m5.s15_command` | ~12 min |
| s16 | 7.1. Utworzenie Metody szablonowej: wspólny szkielet, różne kroki | 2 | `m5.s16_templatemethod` | ~8 min |
| s17 | 16.7. Ograniczenie liczby instancji Singletonem; 17.4. Wchłonięcie Singletona | 3 | `m5.s17_singleton` | ~10 min |
| s18 | 16.6. Przeniesienie zbierania wyników do parametru zbierającego | 3 | `m5.s18_collectingparameter` | ~8 min |
| s19 | 15.1. Odwiedzający i macierz zmian; 15.2. Odwiedzający albo hierarchia `sealed` i `switch` | 3 | `m5.s19_visitor` | ~12 min |
| s20 | 4.2. Najpierw rodzaj zmienności, potem wzorzec; 18.1. Mapa decyzji (1/2); 18.2. Mapa decyzji (2/2); 18.3. Lista kontrolna | 3 | `m5.s20_decisionmap` | ~12 min |
| s21 | 17.5. Wzorzec jako decyzja odwracalna; 17.2. Refaktoryzacje, które usuwają wzorzec; 5.2. Kiedy wzorzec się nie opłaca | 3 | `m5.s21_reversiblepattern` | ~12 min |
| s22 | 17.3. Usunięcie pośrednika; 17.2. Refaktoryzacje, które usuwają wzorzec | 3 | `m5.s22_middleman` | ~8 min |
| s23 | 5.3. Refaktoryzacja a zmiana kontraktu; 5.4. Co trzeba zachować przy wprowadzaniu wzorca; 3.10. Bezpieczna pętla refaktoryzacji do wzorca | 3 | `m5.s23_contractchange` | ~10 min |
| s24 | 3.6. Refaktoryzacje automatyczne w IDE: co dają, a czego nie widzą; 3.7. Zmiana nazwy i wyodrębnienie metody w drodze do wzorca | 3 | `m5.s24_rename` | ~12 min |
| s25 | 3.7. Zmiana nazwy i wyodrębnienie metody w drodze do wzorca | 3 | `m5.s25_extractmethod` | ~15 min |
| s26 | 6.1. Strategia, Polecenie czy Stan? Kto wybiera wariant i kiedy; 6.7. Trzy osie w jednej klasie: każda dostaje własny mechanizm; 6.8. Typowe pomyłki przy wyborze | 3 | `m5.s26_variability` | ~15 min |
| s27 | 7.2. Gdy punkty zaczepienia obejmują dwie niezależne osie; 7.3. Zastąpienie dziedziczenia delegacją: osie jako strategie; 7.4. Strategia czy Metoda szablonowa? | 3 | `m5.s27_templatetostrategy` | ~12 min |
| s28 | 8.3. Łańcuch `if/else`, w którym kolejność jest regułą; 8.4. Łańcuch zobowiązań: ogniwo i złożenie łańcucha; 8.5. Łańcuch zobowiązań czy Dekorator? | 3 | `m5.s28_chain` | ~12 min |
| s29 | 9.3. Surowe dane z zewnątrz w kodzie domeny; 9.4. Obiekt graniczny: sprawdzone dane na granicy systemu; 9.5. Adapter czy obiekt graniczny? | 3 | `m5.s29_boundaryobject` | ~12 min |
| s30 | 11.1. Klienci sami koordynują podsystemy; 11.2. Wyodrębnienie Fasady: jedna operacja zamiast znajomości kroków | 3 | `m5.s30_facade` | ~12 min |
| s31 | 11.3. Obcy model w regułach domeny; 11.4. Warstwa antykorupcyjna: port, tłumacz i model domenowy; 11.5. Fasada a warstwa antykorupcyjna | 3 | `m5.s31_anticorruptionlayer` | ~12 min |
| s32 | 14.1. Komponenty połączone każdy z każdym; 14.2. Wprowadzenie Mediatora: reguły współpracy w jednym miejscu; 14.3. Obserwator czy Mediator? | 3 | `m5.s32_mediator` | ~12 min |

Liczby domeny (ceny formatów, zniżki, VIP, okulary, opłata online, punkty lojalnościowe, zwroty, VAT) są wspólne dla całego warsztatu. Wartości spoza wspólnej listy (program "tydzień studenta" 50%, ubezpieczenie biletu 4.00, "tani wtorek" -30%, weekend +2.00, ceny premier i maratonów, limity zatwierdzania zwrotów 100.00 i 1000.00, autoryzacja częściowa terminala do 300.00, voucher KINO10) są przykładowe i opisane w Javadoc sceny.

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

**Sceny opcjonalne**, gdy grupa pracuje szybciej albo pyta o konkretny wzorzec: s22 i s17 (usuwanie wzorców: usunięcie pośrednika, Singleton i jego wchłonięcie), s24 (zmiana nazwy i nazwy poza Javą), s01, s08 i s15 (każdy z trzech wzorców sekcji 6 osobno, w szczegółach), s16 (klasyczna Metoda szablonowa), s07 (Dekorator), s14 (Adapter), s04 i s05 (jeden blok o Fabryce z porównaniem obu refaktoryzacji), s06 (Budowniczy), s09 (Obserwator, dobry kontrast do s32), s19 (Odwiedzający). Sceny z sekcji 16 (s02, s03, s10-s13, s18) zostawiamy do samodzielnej pracy w parach albo jako zadanie domowe; s11 i s13 mają po 2 kroki i dobrze sprawdzają się jako krótkie ćwiczenie. Na podsumowanie wróć do s20 i poproś salę o przypisanie każdej pokazanej sceny do wiersza mapy decyzji (slajdy 18.1-18.2).

## Scena s01. Replace Conditional Logic with Strategy - polityka zniżek

**Temat ze slajdów:** 6.1. Strategia, Polecenie czy Stan? Kto wybiera wariant i kiedy; 6.2. Strategia: sygnał w kodzie; 6.3. Strategia: kroki i wynik
**Pakiet:** `pl.training.workshop.m5.s01_strategy` · **Test:** `scripts/warsztat.sh test m5/s01`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `TicketPricer` wybiera zniżkę łańcuchem `if` po nazwie programu kina, wymieszanym z walidacją ceny. Wprowadzamy interfejs `DiscountPolicy` najpierw jako strategię przejściową, przenosimy każdy program do osobnej klasy, a wybór programu trafia do `DiscountPrograms` i konstruktora.

**Zasada:** Strategy zamyka wymienne warianty tego samego obliczenia za wspólnym interfejsem, gdy wariant wybiera się niezależnie od klasy obiektu. Wspólna walidacja zostaje w kontekście, a strategie są bezstanowe. Samo `if` nie uzasadnia wzorca - przy prostym, stabilnym warunku interfejs pogarsza czytelność.

**Efekt:** Nowy program zniżek to nowa strategia (nawet lambda) i jeden wpis w `switch`, bez zmiany `TicketPricer`. Zachowanie świadomie się zmienia: wybór w konstruktorze zamraża decyzję, więc nieznany program zgłasza się teraz przed ujemną ceną.

### Co widzimy

`TicketPricer.price(base, ticketType, program)` wybiera algorytm zniżki łańcuchem `if` po nazwie programu skonfigurowanego w kinie (STANDARD, STUDENT_WEEK, PREMIERE). W tej samej metodzie jest walidacja ceny i programu. Algorytm jest wybierany niezależnie od klasy obiektu - to klasyczny kandydat na Strategy.

```java
if (program.equals("PREMIERE")) {
    discount = Money.ZERO;
} else if (program.equals("STUDENT_WEEK") && ticketType.equals("S")) {
    discount = base.percent(50);
} else if (program.equals("STANDARD") || program.equals("STUDENT_WEEK")) {
    int percent = switch (ticketType) { case "N" -> 0; case "S" -> 25; ... };
    discount = base.percent(percent);
} else {
    throw new IllegalArgumentException("unknown program: " + program);
}
```

Klientem jest `PriceBoard.priceFor(request)` - przez niego woła test. Test równoważności to **tabela decyzji**: każda gałąź, typ nieznany, program nieznany i `null`. Zwróć uwagę na przypadek "PREMIERE nie sprawdza typu" - start przyjmuje typ `X` bez błędu i refaktoryzacja musi to zachować.

### Krok 1: Extract Interface + strategia przejściowa

**W IDE:** utwórz interfejs funkcyjny `DiscountPolicy` z metodą `Money discount(Money base, String ticketType)`. Zaznacz łańcuch `if`, ⌥⌘M, nazwa `legacyDiscount` (parametry `base`, `ticketType`, `program`, zwracane `Money`). W `price` utwórz lambdę `DiscountPolicy policy = (b, type) -> legacyDiscount(b, type, program);` i wywołaj ją.
**Po:**

```java
DiscountPolicy policy = (b, type) -> legacyDiscount(b, type, program);
return base.minus(policy.discount(base, ticketType));
```

**Uruchom:** `scripts/warsztat.sh test m5/s01` - zielone.
**Co powiedzieć:** pierwszy commit jest mały i odwracalny: kontrakt strategii istnieje, ale deleguje do starego kodu. Nie zaczynamy od tworzenia wszystkich klas wzorca.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s01 0 1`

### Krok 2: przeniesienie gałęzi do strategii

**W IDE:** dla każdej gałęzi osobno: nowa klasa implementująca `DiscountPolicy` (⌥⏎ na interfejsie - Implement interface), ciało skopiowane z gałęzi, test. Kolejno `StandardDiscount`, `PremiereDiscount`, `StudentWeekDiscount` (ta ostatnia deleguje nie-studentów do `StandardDiscount`). Na koniec `legacyDiscount` zastąp metodą `policyFor(program)` ze `switch`.
**Po:**

```java
return switch (program) {
    case "STANDARD" -> new StandardDiscount();
    case "STUDENT_WEEK" -> new StudentWeekDiscount(new StandardDiscount());
    case "PREMIERE" -> new PremiereDiscount();
    default -> throw new IllegalArgumentException("unknown program: " + program);
};
```

**Uruchom:** test po każdej przeniesionej gałęzi.
**Co powiedzieć:** walidacja zostaje w kontekście, strategie są bezstanowe. `switch` nie zniknął - przeniósł się w jedno miejsce wyboru, a to jest w porządku.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s01 1 2`

### Krok 3: wybór strategii w korzeniu kompozycji

**W IDE:** Extract Class (lub Move Static Member F6) dla `policyFor` do `DiscountPrograms.forName`, instancje jako stałe (współdzielone). Change Signature (⌘F6) na `TicketPricer`: strategia w konstruktorze, `price(base, ticketType)` bez programu. Popraw klienta `PriceBoard`.
**Po:**

```java
return new TicketPricer(DiscountPrograms.forName(request.program()))
        .price(request.base(), request.ticketType());
```

**Uruchom:** test zielony; `S01SolutionTest` pokazuje nowy program zniżek jako lambdę bez zmiany kontekstu.
**Co powiedzieć:** wybór w konstruktorze **zamraża decyzję** i przesuwa moment błędu: w start ujemna cena zgłaszała się przed nieznanym programem, teraz jest odwrotnie. To dokumentuje test `choosingInConstructorMovesTheUnknownProgramErrorEarlier` - taka zmiana wymaga świadomej zgody, nie jest "za darmo".
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s01 2 3`

### Rozwiązanie i uzasadnienie

`step3`: `TicketPricer` zna tylko `DiscountPolicy`, a nazwy programów zna `DiscountPrograms`. Nowy program zniżek to nowa strategia i wpis w jednym `switch`. Kontekst trzyma wspólną walidację.

### Pułapki

- `Function<Money, Money>` zamiast własnego interfejsu - działa, ale gubi nazwę pojęcia i nie może deklarować wyjątków kontrolowanych.
- Strategia z polami zmienianymi podczas liczenia, a potem współdzielona między wątkami.
- Porównywanie lambd przez `==` - lambda nie ma stabilnej tożsamości.
- Interfejs strategii `sealed` - interfejs funkcyjny nie może być `sealed`, a punkt rozszerzeń ma być otwarty.

### Pytanie do sali

Czy przy dwóch programach i jednym `if` Strategy nadal byłaby uzasadniona? Co musiałoby się zmieniać, żeby tak?

## Scena s02. Replace Conditional with Polymorphism - rodzaj seansu

**Temat ze slajdów:** 16.1. Zastąpienie instrukcji warunkowej polimorfizmem; 6.8. Typowe pomyłki przy wyborze
**Pakiet:** `pl.training.workshop.m5.s02_polymorphism` · **Test:** `scripts/warsztat.sh test m5/s02`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `Screening` ma pole `kind` i ten sam `switch` w trzech metodach, a pole `value` znaczy raz minuty filmu, raz liczbę filmów. Wydzielamy podtyp dla każdego rodzaju seansu po kolei, aż zostaje zamknięta hierarchia rekordów z nazwanymi danymi.

**Zasada:** Replace Conditional with Polymorphism przenosi zachowanie zależne od rodzaju do podtypów, gdy selektor opisuje trwały rodzaj obiektu. Jeśli rodzaj zmienia się w czasie życia obiektu, to State, a jeśli algorytm wybiera klient - Strategy. Zaczynamy od znalezienia wszystkich miejsc tworzenia i deserializacji.

**Efekt:** Switche w zachowaniu znikają, zostaje jeden w `fromRow`, gdzie należy wiedza o konstrukcji, a dane mają jednoznaczne nazwy. Kosztem zamkniętej hierarchii `sealed` jest to, że nowy rodzaj psuje kompilację każdego wyczerpującego `switch`, także poza modułem.

### Co widzimy

`Screening` ma pole `kind` (REGULAR, PREMIERE, MARATHON) i ten sam `switch` w trzech metodach: `label()`, `durationMinutes()`, `price()`. Pole `value` znaczy raz "minuty filmu", raz "liczba filmów". Rodzaj seansu jest **trwałą cechą obiektu** - nie zmienia się w czasie życia, więc to nie State, a klient go nie wybiera, więc to nie Strategy.

```java
public int durationMinutes() {
    return switch (kind) {
        case REGULAR -> 20 + value;
        case PREMIERE -> 30 + value;
        case MARATHON -> value * 120 + (value - 1) * 15;
    };
}
```

Tworzenie jest w jednym miejscu: `Screening.fromRow(ScreeningRow)` (mapowanie wiersza z bazy). Zawsze najpierw szukamy miejsc tworzenia i deserializacji.

### Krok 1: Extract Subclass dla jednej gałęzi

**W IDE:** usuń `final`, dodaj `sealed ... permits MarathonScreening`. Utwórz `MarathonScreening extends Screening` z polem `films`, nadpisz trzy metody (⌃O - Override Methods) i skopiuj do nich gałęzie MARATHON. W `fromRow` twórz podklasę dla "MARATHON". W bazie gałęzie MARATHON rzucają `IllegalStateException`.
**Po:**

```java
case "MARATHON" -> new MarathonScreening(row.title(), row.value());
```

**Uruchom:** `scripts/warsztat.sh test m5/s02` - zielone.
**Co powiedzieć:** jedna gałąź naraz, jeden test kontraktowy dla wszystkich rodzajów. Martwa gałąź rzuca wyjątek zamiast po cichu liczyć bzdury.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s02 0 1`

### Krok 2: pozostałe podklasy, baza abstrakcyjna

**W IDE:** powtórz ruch dla REGULAR i PREMIERE. Potem metody bazy jako `abstract`, usuń pole `kind` i enum `Kind` (Safe Delete ⌘⌦). Jedynym `switch` zostaje `fromRow`.
**Po:**

```java
public abstract sealed class Screening permits RegularScreening, PremiereScreening, MarathonScreening {
    public abstract int durationMinutes();
```

**Uruchom:** test zielony.
**Co powiedzieć:** `switch` w miejscu tworzenia jest właściwy - to jest wiedza o konstrukcji. Znikają za to switche w zachowaniu.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s02 1 2`

### Krok 3: forma Java 25 - sealed interface i rekordy

**W IDE:** zamień klasę bazową na `sealed interface` ze statycznym `fromRow`, a podklasy na rekordy (⌥⏎ Convert to record). `value` znika - każdy rekord ma nazwane dane (`runtime`, `films`).
**Po:**

```java
public record MarathonScreening(String title, int films) implements Screening {
    public int durationMinutes() { return films * 120 + (films - 1) * 15; }
```

**Uruchom:** test zielony; `S02SolutionTest` zawiera `switch` klienta bez `default`.
**Co powiedzieć:** zamknięty zestaw rodzajów daje kontrolę kompilatora, ale też koszt: nowy rodzaj psuje kompilację każdego wyczerpującego `switch`, także poza modułem.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s02 2 3`

### Rozwiązanie i uzasadnienie

Trzy rekordy z własnymi danymi i zachowaniem. Znaczenie pól jest jednoznaczne, a test kontraktowy przechodzi przez wszystkie rodzaje, łącznie z nieznanym rodzajem w danych.

### Pułapki

- Pominięcie miejsc tworzenia (ORM, deserializator JSON, fabryka w innym module) - obiekt powstanie jako zły podtyp albo wcale.
- Wywołanie metody nadpisywanej z konstruktora bazy - podklasa widzi niezainicjalizowane pola.
- Pola i metody `static` nie są polimorficzne - "nadpisanie" stałej w podklasie nic nie daje.

### Pytanie do sali

Seans może zmienić się z "premiery" w "zwykły" po tygodniu. Czy to nadal polimorfizm, czy już State?

## Scena s03. Replace Type Code with Class - format jako typ

**Temat ze slajdów:** 16.2. Zastąpienie kodu typu klasą
**Pakiet:** `pl.training.workshop.m5.s03_typecode` · **Test:** `scripts/warsztat.sh test m5/s03`
**Czas:** ~12 min

### W skrócie

**Co robimy:** Format seansu jest surowym `int` z CSV, a wiedza o kodach jest rozsiana po trzech metodach. Zamieniamy liczbę na enum `Format` tuż po odczycie, przenosimy do niego etykietę, cenę i okulary, a tłumaczenie kodu trwałego zamykamy w mapperze `FormatCodes`.

**Zasada:** Replace Type Code with Class zastępuje prymitywny kod typem, który przejmuje walidację, normalizację i operacje pojęcia - co nie oznacza automatycznie hierarchii podklas. Enum wystarcza dla małego, zamkniętego zestawu, klasa jest lepsza przy aliasach i kodach zewnętrznych. Na granicy trwałości zostaje stabilny kod, nigdy `ordinal()` ani `toString()`.

**Efekt:** Poza mapperem żaden kod nie operuje na liczbach 1, 2, 3, a zapis CSV jest bez zmian. Zmiana formatu trwałego (np. na kody tekstowe) dotknie tylko mappera, ale to osobna decyzja, nie część tej refaktoryzacji.

### Co widzimy

Format seansu jest surowym `int` (1=2D, 2=3D, 3=IMAX) czytanym z CSV `Diuna;3`. Wiedza o kodzie jest w trzech metodach, a `needsGlasses` w ogóle nie waliduje kodu. Plik CSV musi nadal przechowywać `int` - to jest **granica trwałości**.

```java
private Money basePrice(int formatCode) {
    return switch (formatCode) {
        case FORMAT_2D -> Money.of("25.00");
        ...
        default -> throw new IllegalArgumentException("unknown format code: " + formatCode);
    };
}
```

### Krok 1: nowy typ i konwersja na granicy

**W IDE:** utwórz `enum Format { TWO_D(1), THREE_D(2), IMAX(3) }` z polem `code` i `fromCode(int)` rzucającym ten sam wyjątek co start. W `describe` zamień `int` na `Format` zaraz po odczycie; Change Signature (⌘F6) metod prywatnych na `Format`. Do CSV zapisuj `format.code()`.
**Po:**

```java
Format format = Format.fromCode(Integer.parseInt(parts[1].trim()));
```

**Uruchom:** `scripts/warsztat.sh test m5/s03` - zielone, łącznie z "nieznany kod" i "kod nie jest liczbą".
**Co powiedzieć:** enum wystarcza, bo zestaw jest mały i zamknięty. Trwały kod to jawne pole, nigdy `ordinal()` ani `name()`.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s03 0 1`

### Krok 2: Move Method do typu

**W IDE:** przenieś `label`, `basePrice`, `needsGlasses` do enuma (F6 Move albo ręcznie jako pola stałych). Klient pyta obiekt zamiast wykonywać `switch`.
**Po:**

```java
TWO_D(1, "2D", "25.00", false),
THREE_D(2, "3D", "32.00", true),
IMAX(3, "IMAX", "40.00", false);
```

**Uruchom:** test zielony.
**Co powiedzieć:** nowy typ przejmuje walidację, normalizację i operacje pojęcia. Nie oznacza to automatycznie hierarchii klas.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s03 1 2`

### Krok 3: mapper migracyjny na granicy trwałości

**W IDE:** Extract Class `FormatCodes` z `fromCode`/`toCode` (mapa `EnumMap`), usuń `code` z enuma. `ScreeningCsv` rozmawia z mapperem, reszta kodu wyłącznie z `Format`.
**Po:**

```java
Format format = FormatCodes.fromCode(Integer.parseInt(parts[1].trim()));
... + "|csv=" + title + ";" + FormatCodes.toCode(format);
```

**Uruchom:** test zielony; `S03SolutionTest` sprawdza, że `ordinal()` IMAX to 2, a kod trwały to 3.
**Co powiedzieć:** gdy kiedyś przejdziemy na kody tekstowe ("IMAX" w bazie), zmieni się tylko mapper - może przez pewien czas czytać oba formaty. Zmiana formatu trwałego to osobna decyzja, nie część tej refaktoryzacji.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s03 2 3`

### Rozwiązanie i uzasadnienie

Typ domenowy bez wiedzy o bazie plus mapper na granicy. Enum, bo zestaw jest zamknięty. Klasę z prywatnym konstruktorem i fabryką wybralibyśmy przy aliasach, kodach zewnętrznych albo gdy lista wartości pochodzi z konfiguracji.

### Pułapki

- Zapis `ordinal()` do bazy - dodanie stałej w środku enuma przestawia dane.
- Zapis `toString()` - ktoś go "upiększy" i CSV przestanie się wczytywać.
- Zmiana komunikatu wyjątku przy nieznanym kodzie - to też obserwowalne zachowanie (logi, monitoring).

### Pytanie do sali

Kiedy wybralibyście klasę zamiast enuma dla formatu? Co w domenie kina mogłoby to wymusić?

## Scena s04. Encapsulate Classes with Factory - bilety

**Temat ze slajdów:** 10.1. Ukrycie klas za Fabryką; 10.3. Fabryka jako strażnik spójności
**Pakiet:** `pl.training.workshop.m5.s04_encapsulatefactory` · **Test:** `scripts/warsztat.sh test m5/s04`
**Czas:** ~10 min

### W skrócie

**Co robimy:** `BoxOffice` tworzy `StandardTicket` i `VipTicket` przez `new` i sam zna regułę VIP (rząd 10+). Przekierowujemy tworzenie do metod fabryki `Tickets`, przenosimy tam regułę wyboru biletu, a na końcu odbieramy klasom konkretnym `public`.

**Zasada:** Encapsulate Classes with Factory ukrywa klasy konkretne za publiczną granicą tworzenia, gdy klient potrzebuje tylko wspólnego interfejsu. Tworzenie przekierowujemy pojedynczo, a widoczność ograniczamy dopiero na końcu. Nie mylić z Extract Factory Class, która wydziela tworzenie z klasy o innej odpowiedzialności.

**Efekt:** Publiczne zostają tylko `Ticket` i `Tickets`, więc nowy rodzaj biletu nie zmienia żadnego klienta. Ryzykiem zostają miejsca tworzenia, których kompilator nie widzi (refleksja, DI, deserializacja), a w bibliotece publicznej potrzebny byłby etap `@Deprecated`.

### Co widzimy

`BoxOffice` (inny pakiet niż bilety) tworzy `StandardTicket` i `VipTicket` przez `new` w dwóch miejscach i zna regułę VIP (rząd 10+). Klasy biletów są publiczne, choć klient potrzebuje tylko interfejsu `Ticket`.

```java
tickets.add(row >= 10
        ? new VipTicket(title, base, row)
        : new StandardTicket(title, base, row));
```

### Krok 1: metody tworzące

**W IDE:** w pakiecie `tickets` utwórz `Tickets` ze statycznymi `standard(...)` i `vip(...)` zwracającymi `Ticket`. Zastąp każde `new` w `BoxOffice` wywołaniem metody tworzącej (IntelliJ: Replace Constructor with Factory Method z menu Refactor robi to automatycznie).
**Po:**

```java
return Tickets.vip(title, base, row);
```

**Uruchom:** `scripts/warsztat.sh test m5/s04` - zielone.
**Co powiedzieć:** przekierowujemy tworzenie pojedynczo; klient nie importuje już klas konkretnych.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s04 0 1`

### Krok 2: decyzja do fabryki

**W IDE:** Move Method - warunek `row >= 10` przenieś do `Tickets.forSeat(title, base, row)`, stałą nazwij (⌥⌘C `VIP_FROM_ROW`). `sellAll` woła `sell`.
**Po:**

```java
public Ticket sell(String title, Money base, int row) {
    return Tickets.forSeat(title, base, row);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** reguła "który bilet" jest teraz w jednym miejscu - to właściwy dom dla `if` o konstrukcji.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s04 1 2`

### Krok 3: ograniczenie widoczności

**W IDE:** usuń `public` z `StandardTicket` i `VipTicket` (przy okazji ⌥⏎ Convert to record). IntelliJ podkreśli każde użycie spoza pakietu - to inwentaryzacja, którą robi za nas kompilator.
**Po:**

```java
record VipTicket(String title, Money base, int row) implements Ticket { ... }
```

**Uruchom:** test zielony; `S04SolutionTest` sprawdza widoczność refleksją.
**Co powiedzieć:** widoczność ograniczamy na końcu, gdy wszyscy klienci już przeszli. W bibliotece publicznej potrzebny byłby etap `@Deprecated`.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s04 2 3`

### Rozwiązanie i uzasadnienie

Publiczne są tylko `Ticket` i `Tickets`. Nowy rodzaj biletu (np. miejsce dla osoby na wózku) nie zmienia żadnego klienta.

### Pułapki

- Zapomniane miejsca tworzenia: refleksja, DI, deserializacja JSON wymagająca publicznego konstruktora.
- Fabryka rosnąca w globalny rejestr wszystkiego ("ServiceLocator").
- Fabryka tworząca warianty z wyprzedzeniem - kosztowne efekty uruchamiane wcześniej niż w starej gałęzi.

### Pytanie do sali

Co się zmienia, jeśli klasy biletów są używane przez framework ORM?

## Scena s05. Extract Factory Class - tworzenie rezerwacji

**Temat ze slajdów:** 10.2. Wyodrębnienie klasy fabryki; 10.3. Fabryka jako strażnik spójności
**Pakiet:** `pl.training.workshop.m5.s05_extractfactory` · **Test:** `scripts/warsztat.sh test m5/s05`
**Czas:** ~10 min

### W skrócie

**Co robimy:** `ReservationService` pilnuje zajętości miejsc, ale też buduje rezerwacje (numer, opłata online, termin ważności), i to w dwóch kopiach. Wyciągamy tworzenie do metody, potem do klasy `ReservationFactory`, a na końcu przekazujemy fabrykę do serwisu przez konstruktor.

**Zasada:** Extract Factory Class wydziela wiedzę o tworzeniu obiektów z klasy, która ma inną główną odpowiedzialność. Fabryka to zwykła zależność wstrzykiwana konstruktorem, a nie globalny rejestr ani zestaw metod statycznych. Kolejność efektów przy tworzeniu (tu: numer pobierany przed walidacją kanału) jest częścią kontraktu.

**Efekt:** Fabryka wie, jak powstaje rezerwacja, serwis wie, kiedy wolno ją utworzyć, i każdą da się testować osobno. "Spalanie" numeru przy błędnym kanale zostaje celowo - jego naprawa byłaby zmianą zachowania.

### Co widzimy

`ReservationService` pilnuje zajętości miejsc, ale też buduje rezerwację: numer z licznika, opłata online 2.00 za bilet, termin ważności 15 minut. Ta wiedza jest skopiowana w `reserve` i `reserveGroup`. Subtelność: numer jest pobierany **przed** walidacją kanału - błędny kanał "spala" numer.

```java
sequence++;
String id = "R" + sequence;
if (!channel.equals("ONLINE") && !channel.equals("BOX_OFFICE")) {
    throw new IllegalArgumentException("unknown channel: " + channel);
}
```

Test to scenariusz kilku rezerwacji na jednym serwisie: zajęte miejsce nie zużywa numeru, nieznany kanał zużywa.

### Krok 1: Extract Method dla tworzenia

**W IDE:** w `reserve` zaznacz blok od `sequence++` do `new Reservation(...)`, ⌥⌘M, nazwa `newReservation`. W `reserveGroup` zastąp duplikat wywołaniem `newReservation("ONLINE", email, seats)`. Pętlę zajętości wyciągnij do `requireFree`.
**Po:**

```java
requireFree(seats);
Reservation reservation = newReservation(channel, email, seats);
takenSeats.addAll(seats);
```

**Uruchom:** `scripts/warsztat.sh test m5/s05` - zielone.
**Co powiedzieć:** kolejność efektów zostaje: najpierw sprawdzenie miejsc, potem numer, potem walidacja kanału. Gdybyśmy przy okazji "poprawili" kolejność, test to wychwyci.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s05 0 1`

### Krok 2: Extract Class - ReservationFactory

**W IDE:** Refactor > Extract Delegate (albo Move Method F6 do nowej klasy) dla `newReservation` razem z polami `clock` i `sequence`. Serwis tworzy fabrykę w swoim konstruktorze - sygnatura konstruktora bez zmian.
**Po:**

```java
public ReservationService(Clock clock) {
    this.factory = new ReservationFactory(clock);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** klienci serwisu niczego nie zauważyli. Serwis ma jedną odpowiedzialność: dostępność miejsc.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s05 1 2`

### Krok 3: fabryka jako zależność

**W IDE:** dodaj konstruktor `ReservationService(ReservationFactory factory)`; dotychczasowy `ReservationService(Clock)` zamień na delegację `this(new ReservationFactory(clock))`, żeby nie ruszać klientów.
**Po:**

```java
public ReservationService(Clock clock) {
    this(new ReservationFactory(clock));
}

public ReservationService(ReservationFactory factory) { ... }
```

**Uruchom:** test zielony; `S05SolutionTest` testuje fabrykę osobno i pokazuje współdzieloną numerację.
**Co powiedzieć:** fabryka to zwykła zależność wstrzykiwana konstruktorem, nie globalny rejestr. Dwie usługi z jedną fabryką dzielą numerację - to decyzja korzenia kompozycji.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s05 2 3`

### Rozwiązanie i uzasadnienie

`ReservationFactory` wie, jak powstaje rezerwacja (numer, opłata, termin), `ReservationService` wie, kiedy wolno ją utworzyć. Każdą da się testować osobno.

### Pułapki

- "Naprawienie" spalania numerów w ramach refaktoryzacji - to zmiana zachowania (luki w numeracji mogą być wymagane przez księgowość albo przez nią zabronione).
- Zegar pobierany przez `LocalDateTime.now()` bez `Clock` - fabryka staje się nietestowalna.
- Fabryka ze statycznymi metodami i statycznym licznikiem - wraca globalny stan.

### Pytanie do sali

Czy numeracja rezerwacji powinna należeć do fabryki, czy do repozytorium? Co przemawia za każdą opcją?

## Scena s06. Encapsulate Composite with Builder - repertuar dnia

**Temat ze slajdów:** 12.1. Ukrycie budowy Kompozytu za Budowniczym; 12.2. Budowniczy: kiedy i na co uważać
**Pakiet:** `pl.training.workshop.m5.s06_builder` · **Test:** `scripts/warsztat.sh test m5/s06`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `WeekendPlanner` składa drzewo dzień - sala - seans ręcznie z `new` i `add`, a węzły są mutowalne. Wprowadzamy `ScheduleBuilder`: najpierw z bieżącą salą, potem z niemutowalnymi rekordami i jednorazowym `build()`, na końcu z zagnieżdżoną lambdą dla każdej sali.

**Zasada:** Encapsulate Composite with Builder ukrywa budowę drzewa za API, które mówi "co" zbudować, a nie "jak". Jest uzasadniony, gdy budowa jest wieloetapowa i ma własne niezmienniki (kolejność, pusta grupa, unikalność nazw). Builder jest jednorazowy, a lambda gałęzi wykonuje się synchronicznie dokładnie raz.

**Efekt:** Wcięcia kodu planera odpowiadają poziomom repertuaru, a gotowego drzewa nie da się zmodyfikować. Unikalność nazw sal to świadomie dodany nowy niezmiennik - start pozwalał na duplikaty, więc dla błędnych danych zachowanie się zmienia.

### Co widzimy

`WeekendPlanner.plan(date)` buduje drzewo dzień - sala - seans ręcznie: dużo `new` i `add`, a o `day.add(hall2)` łatwo zapomnieć. Kod nie przypomina kształtu repertuaru, a węzły są mutowalne.

```java
Hall hall2 = new Hall("Sala 2");
if (weekend) {
    hall2.add(new Screening("Kraina Lodu", LocalTime.of(10, 0)));
}
hall2.add(new Screening("Amator", LocalTime.of(17, 30)));
day.add(hall2);
```

### Krok 1: klasyczny Builder z bieżącym węzłem

**W IDE:** utwórz `ScheduleBuilder` z metodami `hall(name)`, `screening(title, hour, minute)` i `build()`; builder pamięta bieżącą salę. Przepisz planera na wywołania buildera. Drzewo bez zmian.
**Po:**

```java
new ScheduleBuilder(date)
        .hall("Sala 1")
        .screening("Diuna", 18, 0)
        .screening("Diuna", 21, 0)
        .hall("Sala 2");
```

**Uruchom:** `scripts/warsztat.sh test m5/s06` - zielone (sobota i piątek z pustą salą VIP).
**Co powiedzieć:** klient mówi "co" zbudować. Seans przed salą to teraz jawny błąd (`screening without hall`).
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s06 0 1`

### Krok 2: niemutowalne drzewo, builder jednorazowy

**W IDE:** węzły zamień na rekordy z `List.copyOf` (⌥⏎ Convert to record, usuń `add`). Builder zbiera dane i tworzy rekordy w `build()`; drugi `build()` rzuca `IllegalStateException`.
**Po:**

```java
public record Hall(String name, List<Screening> screenings) {
    public Hall { screenings = List.copyOf(screenings); }
```

**Uruchom:** test zielony; `S06SolutionTest` sprawdza jednorazowość i niemutowalność.
**Co powiedzieć:** gdy builder jest jedyną drogą budowy, drzewo może stać się niemutowalne. Unikalność nazw sal to **nowy niezmiennik** - start pozwalał na duplikaty. Taką regułę zatwierdzamy świadomie, bo zmienia zachowanie dla błędnych danych.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s06 1 2`

### Krok 3: zagnieżdżony builder z lambdą

**W IDE:** `hall(name, Consumer<HallBuilder>)` zamiast "bieżącej sali"; `HallBuilder` z `screening(...)`. Przepisz planera.
**Po:**

```java
return ScheduleBuilder.day(date)
        .hall("Sala 1", hall -> hall
                .screening("Diuna", 18, 0)
                .screening("Diuna", 21, 0))
        .hall("Sala 2", hall -> { ... })
        .build();
```

**Uruchom:** test zielony.
**Co powiedzieć:** wcięcia kodu odpowiadają poziomom drzewa, a ukryty stan "bieżąca sala" zniknął. Kontrakt lambdy: synchroniczna, wywołana dokładnie raz.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s06 2 3`

### Rozwiązanie i uzasadnienie

Builder jest uzasadniony, bo budowa drzewa jest wieloetapowa i ma niezmienniki (kolejność sal, pusta sala dozwolona, unikalne nazwy). Wynik jest niemutowalny.

### Pułapki

- Builder wielokrotnego użytku, którego `build()` zwraca wewnętrzną listę - kolejne wywołania zmieniają już zwrócone drzewo.
- Lambda zapamiętana i wywołana później (odroczenie) - zmienia kolejność i wynik.
- Współdzielone poddrzewo (DAG) - liczy się tyle razy, ile ścieżek do niego prowadzi.

### Pytanie do sali

Czy pusta sala w repertuarze to poprawny stan, czy błąd? Kto powinien o tym zdecydować - builder czy model?

## Scena s07. Move Embellishment to Decorator - dodatki do biletu

**Temat ze slajdów:** 8.1. Przeniesienie dodatków do Dekoratora; 8.2. Dekorator: na co uważać
**Pakiet:** `pl.training.workshop.m5.s07_decorator` · **Test:** `scripts/warsztat.sh test m5/s07`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `Ticket` ma flagi `vip`, `glasses`, `insurance` i te same `if` w `price()` i `description()`. Wydzielamy interfejs `PricedTicket` i przenosimy każdy dodatek do dekoratora, zaczynając od ubezpieczenia, a łańcuch składamy w `TicketAssembler`.

**Zasada:** Move Embellishment to Decorator przenosi opcjonalny dodatek wokół rdzenia do obiektu, który ma ten sam kontrakt i deleguje do środka. Kolejność owijania jest zachowaniem, więc składa się ją w jednym miejscu. Dekorator nie jest przezroczysty dla `instanceof`, `getClass`, tożsamości i `equals`.

**Efekt:** Rdzeń biletu nie ma flag, a nowy dodatek to nowa klasa i linia w assemblerze. Kosztem jest utrata pytania o typ: kto chce wiedzieć, czy bilet jest VIP, potrzebuje osobnego API zamiast `instanceof VipSeat`.

### Co widzimy

`Ticket` ma flagi `vip`, `glasses`, `insurance` i te same `if` w `price()` i `description()`. Większość biletów nie ma żadnego dodatku, a każdy nowy dodatek to kolejne pole. Opis ma ustaloną kolejność: VIP, okulary, ubezpieczenie.

```java
return title + " " + format
        + (vip ? " +VIP" : "")
        + (glasses ? " +okulary 3D" : "")
        + (insurance ? " +ubezpieczenie" : "");
```

### Krok 1: Extract Interface

**W IDE:** Refactor > Extract Interface na `Ticket` - `PricedTicket` z `price()` i `description()`. `TicketAssembler.assemble` zwraca interfejs.
**Po:**

```java
public PricedTicket assemble(TicketOrder order) {
    return new Ticket(order);
}
```

**Uruchom:** `scripts/warsztat.sh test m5/s07` - zielone.
**Co powiedzieć:** wąski kontrakt, który spełnią i rdzeń, i dekoratory. Składanie w jednym miejscu (assembler).
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s07 0 1`

### Krok 2: pierwszy dodatek jako dekorator

**W IDE:** rekord `Insurance(PricedTicket inner)` dodający 4.00 i " +ubezpieczenie". Usuń flagę z `Ticket` (Safe Delete), w assemblerze owiń, gdy `order.insurance()`.
**Po:**

```java
PricedTicket ticket = new Ticket(order);
if (order.insurance()) {
    ticket = new Insurance(ticket);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** zaczynamy od ubezpieczenia, bo w opisie jest **ostatnie** - dekorator dopisuje się na końcu. Gdybyśmy zaczęli od VIP, opis zmieniłby kolejność i test "wszystkie dodatki" by to złapał. Kolejność dekoratorów jest kontraktem.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s07 1 2`

### Krok 3: pozostałe dodatki, czysty rdzeń

**W IDE:** `VipSeat` i `Glasses3D` jako dekoratory, rdzeń jako rekord `Ticket(title, format, base)`. Decyzja "czy okulary potrzebne" (3D i brak własnych) zostaje w assemblerze.
**Po:**

```java
if (order.vip()) ticket = new VipSeat(ticket);
if (order.format().equals("3D") && !order.ownGlasses()) ticket = new Glasses3D(ticket);
if (order.insurance()) ticket = new Insurance(ticket);
```

**Uruchom:** test zielony; `S07SolutionTest` dokumentuje granice przezroczystości.
**Co powiedzieć:** `ticket instanceof VipSeat` zwraca `false`, gdy VIP jest owinięty ubezpieczeniem. `equals` rdzenia i udekorowanego biletu też się różni. Kto pyta o typ albo tożsamość, potrzebuje innego API (np. `features()`).
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s07 2 3`

### Rozwiązanie i uzasadnienie

Rdzeń bez flag, trzy dekoratory z tym samym kontraktem, łańcuch składany w jednej fabryce w ustalonej kolejności.

### Pułapki

- Ślepe delegowanie `equals` do `inner` - łamie symetrię (`core.equals(vip)` różne od `vip.equals(core)`).
- Kod sprawdzający `getClass()`, `instanceof`, synchronizujący na obiekcie albo serializujący go - dekorator nie jest przezroczysty.
- Dekorator z efektem ubocznym (audyt), którego błąd zasłania błąd rdzenia.

### Pytanie do sali

Kasa chce wydrukować "Miejsce VIP" na bilecie. Jak to zrobić, nie używając `instanceof VipSeat`?

## Scena s08. Replace State-Altering Conditionals with State - status rezerwacji

**Temat ze slajdów:** 6.4. Stan: sygnał i tabela przejść; 6.5. Stan: wynik
**Pakiet:** `pl.training.workshop.m5.s08_state` · **Test:** `scripts/warsztat.sh test m5/s08`
**Czas:** ~15 min

### W skrócie

**Co robimy:** `Reservation` w każdej operacji sprawdza i zmienia pole `status`, więc reguły przejść są rozsiane po metodach. Zaczynamy od tabeli przejść w teście, zastępujemy pole obiektem stanu i przenosimy `pay`, `use`, `expire`, `cancel` do stanów po jednej akcji.

**Zasada:** State pasuje, gdy zachowanie operacji zależy od bieżącego stanu obiektu i zmienia się razem z przejściami. Kontekst deleguje do obiektu stanu, a niedozwolone przejście domyślnie rzuca wyjątek - pusta metoda po cichu zmieniłaby zachowanie. Od Strategy różni się tym, że stan zmienia się w czasie życia obiektu, zwykle na skutek jego własnych operacji.

**Efekt:** Tabelę przejść da się przeczytać z kodu stanów, a `Reservation` tylko deleguje i przechowuje dane. Kolejność "obciążenie - zmiana stanu - efekt" zostaje jak w start, więc awaria bramki nadal zostawia rezerwację w stanie NEW.

### Co widzimy

`Reservation` ma pole `status` i w każdej operacji (`pay`, `use`, `expire`, `cancel`) warunki sprawdzające i zmieniające status. Reguły przejść NEW -> PAID -> USED, NEW -> EXPIRED, NEW/PAID -> CANCELLED są rozsiane po metodach. `pay` najpierw obciąża bramkę, potem zmienia stan: gdy bramka rzuci wyjątek, rezerwacja zostaje NEW.

```java
public void cancel() {
    if (status == Status.NEW) {
        status = Status.CANCELLED;
        effects.add("seats released");
    } else if (status == Status.PAID) {
        status = Status.CANCELLED;
        effects.add("refund");
        effects.add("seats released");
    } else {
        throw new IllegalStateException("cannot cancel in " + status);
    }
}
```

**Najpierw tabela przejść.** `S08EquivalenceTest` zawiera tabelę 5 stanów x 4 akcje (20 wierszy) plus przypadek awarii bramki. Każdy wiersz sprawdza status końcowy, efekty w kolejności i komunikat wyjątku. Pokaż ją na rzutniku przed pierwszym ruchem.

### Krok 1: obiekt stanu zamiast pola status

**W IDE:** utwórz prywatny `sealed interface ReservationState` z `status()` i stany jako enumy (`NewState.INSTANCE`, `PaidState.INSTANCE`, `ClosedState.USED/EXPIRED/CANCELLED`). Pole `status` zastąp polem `state`; warunki porównują `state.status()`. Efekty i zmianę stanu wyciągnij do `charge()`, `moveTo(...)`, `record(...)` (⌥⌘M).
**Po:**

```java
private ReservationState state = NewState.INSTANCE;
...
charge();
moveTo(PaidState.INSTANCE);
record("charged");
```

**Uruchom:** `scripts/warsztat.sh test m5/s08` - 84 testy zielone.
**Co powiedzieć:** stany końcowe to jeden enum z trzema stałymi - wszystkie zachowują się tak samo (każde przejście jest błędem).
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s08 0 1`

### Krok 2: pay i use do stanów

**W IDE:** w interfejsie stanu dodaj `default void pay(Reservation r) { throw invalid("pay"); }` (i `use`). W `NewState` nadpisz `pay`, w `PaidState` - `use`, przenosząc ciała gałęzi. Kontekst deleguje: `state.pay(this)`.
**Po:**

```java
public void pay(Reservation reservation) {
    reservation.charge();
    reservation.moveTo(PaidState.INSTANCE);
    reservation.record("charged");
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** domyślna metoda **rzuca wyjątek**, nie jest pusta - pusta metoda po cichu zmieniłaby zachowanie niedozwolonych przejść. Kolejność "obciążenie - zmiana stanu - efekt" zostaje dokładnie jak w start; test awarii bramki to pilnuje.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s08 1 2`

### Krok 3: expire i cancel do stanów

**W IDE:** ten sam ruch dla `expire` i `cancel`. Warunek `if/else if` w `cancel` rozkłada się na dwa stany: NEW (zwolnienie miejsc) i PAID (zwrot i zwolnienie).
**Po:**

```java
public void cancel() {
    state.cancel(this);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** tabelę przejść da się teraz przeczytać z kodu stanów. Stany są bezstanowymi stałymi, dane zostają w `Reservation`.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s08 2 3`

### Rozwiązanie i uzasadnienie

Kontekst tylko deleguje. Każdy stan nadpisuje wyłącznie dozwolone przejścia, reszta rzuca wyjątek z tym samym komunikatem co start, a stan po błędzie się nie zmienia.

### Pułapki

- Zmiana stanu przed efektem zewnętrznym (albo odwrotnie) "przy okazji" - to inna semantyka przy awarii.
- `volatile` na polu stanu nie daje atomowości przejścia przy współbieżnych `pay` i `cancel`.
- Przy dwóch stanach i jednej akcji `switch` bywa czytelniejszy niż hierarchia.
- Kilka niezależnych osi stanu (płatność, obecność) w jednej hierarchii - eksplozja klas.

### Pytanie do sali

Czym różni się State od Strategy, skoro oba to "obiekt, któremu delegujemy"?

## Scena s09. Replace Hard-coded Notifications with Observer - po opłaceniu

**Temat ze slajdów:** 13.1. Zastąpienie zakodowanych powiadomień Obserwatorem; 13.2. Obserwator: zasady powiadamiania muszą być jawne
**Pakiet:** `pl.training.workshop.m5.s09_observer` · **Test:** `scripts/warsztat.sh test m5/s09`
**Czas:** ~15 min

### W skrócie

**Co robimy:** `PaymentService.confirm` po zapisaniu opłaty na sztywno wysyła mail, SMS i nalicza punkty lojalnościowe. Wprowadzamy zdarzenie `ReservationPaid`, zamieniamy trzech odbiorców na implementacje `PaymentListener`, a subskrypcje przenosimy do korzenia kompozycji `PaymentServices`.

**Zasada:** Observer odwraca zależność: publikujący zna tylko interfejs odbiorcy, a odbiorcy są do niego rejestrowani z zewnątrz. Kontrakt musi być jawny - synchroniczność, kolejność, polityka błędów (tu fail-fast) i wyrejestrowanie. Dodanie nowych odbiorców to rozszerzenie zachowania, a nie część refaktoryzacji.

**Efekt:** Serwis nie importuje `Mailer`, `SmsGateway` ani `LoyaltyProgram`, a odbiorcy są wołani w kolejności subskrypcji jak w start. Zostaje ta sama semantyka błędów: awaria SMS przerywa naliczanie punktów, a jej zmiana byłaby osobną decyzją.

### Co widzimy

Serwis powstaje w korzeniu kompozycji `PaymentServices.standard(mailer, sms, loyalty)`. `PaymentService.confirm` zapisuje opłatę, a potem na sztywno wysyła mail, SMS i nalicza punkty lojalnościowe (1 pkt za pełne 10.00). Semantyka do zachowania: **kolejność** mail - SMS - punkty i **fail-fast**: wyjątek w SMS przerywa, punkty nie są naliczone, ale opłata jest już zapisana.

```java
paid.add(payment.reservationId());
mailer.send(payment.email(), "Potwierdzenie platnosci " + ...);
sms.send(payment.phone(), "Oplacono " + payment.reservationId());
loyalty.addPoints(payment.email(), payment.amount().amount().intValue() / 10);
```

Test używa fake'ów zapisujących do wspólnego logu i sprawdza kolejność oraz awarie w mailu i SMS.

### Krok 1: zdarzenie i Extract Method

**W IDE:** utwórz rekord `ReservationPaid`. Zaznacz trzy powiadomienia, ⌥⌘M `notifyPaid(event)`.
**Po:**

```java
paid.add(payment.reservationId());
notifyPaid(new ReservationPaid(payment.reservationId(), payment.email(), payment.phone(), payment.amount()));
```

**Uruchom:** `scripts/warsztat.sh test m5/s09` - zielone.
**Co powiedzieć:** zdarzenie to fakt z danymi potrzebnymi odbiorcom. Jeszcze nic nie jest dynamiczne.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s09 0 1`

### Krok 2: odbiorcy jako obserwatorzy

**W IDE:** interfejs `PaymentListener.onPaid(event)` i trzy rekordy: `MailConfirmation`, `SmsConfirmation`, `LoyaltyPoints` (każdy z ciałem jednej linii z `notifyPaid`). Serwis tworzy `List.of(...)` w konstruktorze w starej kolejności; `notifyPaid` to pętla **bez** `try/catch`.
**Po:**

```java
for (PaymentListener listener : listeners) {
    listener.onPaid(event);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** konstruktor bez zmian, relacja wciąż "ci sami trzej odbiorcy" - to nadal czysta refaktoryzacja.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s09 1 2`

### Krok 3: subscribe i korzeń kompozycji

**W IDE:** `subscribe(listener)` zwracające `Subscription` (idempotentne `close()`), lista `CopyOnWriteArrayList` rejestracji. Konstruktor serwisu staje się bezargumentowy, a subskrypcje przenosimy do istniejącego `PaymentServices.standard(...)`.
**Po:**

```java
PaymentService service = new PaymentService();
service.subscribe(new MailConfirmation(mailer));
service.subscribe(new SmsConfirmation(sms));
service.subscribe(new LoyaltyPoints(loyalty));
```

**Uruchom:** test zielony; `S09SolutionTest` pokazuje podwójną subskrypcję, idempotentne wyrejestrowanie i nowego odbiorcę.
**Co powiedzieć:** `Registration` jest klasą, nie rekordem - rekord z tą samą lambdą byłby `equals` i `remove` usunąłby cudzą rejestrację. Dodanie czwartego odbiorcy (push) to już **rozszerzenie** zachowania, zatwierdzane osobno.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s09 2 3`

### Rozwiązanie i uzasadnienie

Serwis zna tylko `PaymentListener`. Kontrakt jest jawny: synchronicznie, w kolejności subskrypcji, fail-fast, iteracja po migawce.

### Pułapki

- "Przy okazji" dodany `try/catch` wokół każdego odbiorcy - zmienia politykę błędów (punkty naliczone mimo awarii SMS).
- Asynchroniczne powiadamianie (Executor) - inny wątek, inna transakcja, inna kolejność.
- Odbiorca usunięty w trakcie publikacji może być jeszcze wywołany w bieżącej (migawka).
- Niezamknięta subskrypcja trzyma przy życiu graf obiektów.

### Pytanie do sali

Czy błąd wysyłki SMS powinien przerywać naliczanie punktów? Kto o tym decyduje i gdzie to zapisać?

## Scena s10. Replace Implicit Tree with Composite - zestawy baru

**Temat ze slajdów:** 16.3. Zastąpienie ukrytego drzewa Kompozytem; 3.3. Test równoważności przed zmianą i po niej
**Pakiet:** `pl.training.workshop.m5.s10_implicittree` · **Test:** `scripts/warsztat.sh test m5/s10`
**Czas:** ~15 min

### W skrócie

**Co robimy:** Zestaw combo to zagnieżdżona lista, w której pierwszy element jest nazwą, a `BarMenu` powtarza `instanceof` i rzutowania. Budujemy obok jawny Composite (`Product`, `Combo`) z mapperem ze starego formatu, a potem przenosimy na niego `price` i `render` po jednej operacji.

**Zasada:** Replace Implicit Tree with Composite zamienia drzewo ukryte w konwencji danych na jawne typy liścia i węzła. Mapper tłumaczy stary format i zgłasza błąd dla danych, których nie da się wiernie odwzorować. Stara i nowa reprezentacja żyją obok siebie, a test różnicowy potwierdza zgodność, ale nie zastępuje niezależnych oczekiwań.

**Efekt:** `BarMenu` nie ma `instanceof` ani rzutowań, a komunikaty błędów zostają jak w start. Format trwały (zagnieżdżone listy) się nie zmienia - jego zmiana to osobna decyzja z osobnym testem.

### Co widzimy

Zestaw combo to zagnieżdżona lista: pierwszy element to nazwa, kolejne to `"nazwa=cena"` albo podlisty. `BarMenu.price` i `BarMenu.render` powtarzają `instanceof` i rzutowania, a `render` dla każdego węzła liczy cenę od nowa.

```java
List.of("Zestaw Duo", "Popcorn L=18.00",
        List.of("Napoje", "Cola 0.5=9.00", "Cola 0.5=9.00"), "Nachos=14.00")
```

Testy: `S10EquivalenceTest` (niezależne oczekiwania policzone ręcznie, także błędy) i `S10DifferentialTest` (500 losowych drzew ze stałym ziarnem - stara reprezentacja kontra nowa).

### Krok 1: Composite i mapper obok starego kodu

**W IDE:** utwórz `sealed interface MenuItem` z rekordami `Product` i `Combo` oraz `MenuMapper.fromNested(List<?>)`. `BarMenu` bez zmian. Uruchom test różnicowy - porównuje stary kod z drzewem z mappera.
**Po:**

```java
items.add(switch (element) {
    case String product -> product(product);
    case List<?> nested -> fromNested(nested);
    default -> throw new IllegalArgumentException("unsupported element: " + element);
});
```

**Uruchom:** `scripts/warsztat.sh test m5/s10` - zielone, w tym `compositeFromMapperMatchesLegacyAlready`.
**Co powiedzieć:** obie reprezentacje żyją obok siebie. Mapper zachowuje komunikaty błędów starego kodu - to też obserwowalne zachowanie.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s10 0 1`

### Krok 2: price na Composite

**W IDE:** ciało `BarMenu.price` zastąp `MenuMapper.fromNested(combo).price()`. Usuń nieużywane `requireName`.
**Po:**

```java
public Money price(List<?> combo) {
    return MenuMapper.fromNested(combo).price();
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** jedna operacja naraz; `render` wciąż jest stary i działa, bo woła nowe `price`.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s10 1 2`

### Krok 3: render na Composite

**W IDE:** `render` deleguje do `Combo.render(0, text)`; stara rekurencja z `instanceof` do usunięcia (Safe Delete).
**Po:**

```java
StringBuilder text = new StringBuilder();
MenuMapper.fromNested(combo).render(0, text);
return text.toString();
```

**Uruchom:** test zielony, test różnicowy `finalBarMenuMatchesLegacy` zielony.
**Co powiedzieć:** format trwały (zagnieżdżone listy) zostaje. Jego zmiana na JSON czy tabelę w bazie to osobna decyzja z osobnym testem.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s10 2 3`

### Rozwiązanie i uzasadnienie

Jawne drzewo z typami zamiast konwencji "pierwszy element to nazwa". Test różnicowy daje szerokie pokrycie, a niezależne oczekiwania chronią przed sytuacją, w której oba warianty mylą się tak samo.

### Pułapki

- Test różnicowy jako jedyny test - jeśli stary kod ma błąd, nowy go wiernie powtórzy.
- Brak limitu głębokości dla danych niezaufanych - `StackOverflowError`.
- Cykl w danych (lista zawierająca samą siebie) - nieskończona rekurencja w obu wersjach.

### Pytanie do sali

Co zrobić, gdy mapper natrafi na dane, których nie da się wiernie odwzorować w drzewie?

## Scena s11. Transparent vs Safe Composite - add() na liściu

**Temat ze slajdów:** 16.4. Kompozyt przezroczysty czy bezpieczny
**Pakiet:** `pl.training.workshop.m5.s11_safecomposite` · **Test:** `scripts/warsztat.sh test m5/s11`
**Czas:** ~8 min

### W skrócie

**Co robimy:** W Transparent Composite `add()` jest we wspólnym `MenuComponent`, więc wywołanie go na produkcie kompiluje się i wybucha dopiero w runtime. Przesuwamy `add` i `children` w dół do `Combo` (Safe Composite), a potem pokazujemy niemutowalne drzewo z rekordów bez `add`.

**Zasada:** Transparent Composite trzyma zarządzanie dziećmi we wspólnym interfejsie, więc liść musi rzucić wyjątek albo nic nie robić. Safe Composite trzyma je tylko w węźle, dzięki czemu błąd przenosi się do kompilacji, ale klient musi wiedzieć, czy ma w ręku węzeł. Gdy drzewo powstaje raz, dylemat znika, bo `add` nie jest potrzebne nigdzie.

**Efekt:** Dodanie dziecka do produktu przestaje się kompilować, a `MenuComponent` nie ma już `add` ani `children`. Kosztem jest dokładniejsze typowanie zmiennych w katalogu - rzutowanie `(Combo)` w kliencie oznaczałoby powrót do problemu.

### Co widzimy

Transparent Composite: `add()` i `children()` są we wspólnym `MenuComponent`, liść `Product` dziedziczy `add()`, które rzuca `UnsupportedOperationException`. Klient typuje wszystko jako `MenuComponent` i nic go nie chroni przed `nachos.add(sos)`.

```java
public void add(MenuComponent child) {
    throw new UnsupportedOperationException("cannot add to " + name());
}
```

### Krok 1: Safe Composite - Push Members Down

**W IDE:** Refactor > Push Members Down na `add` i `children` z `MenuComponent` do `Combo`. IntelliJ pokaże konflikty w `ComboCatalog` - zmień typ zmiennych z `MenuComponent` na `Combo` tam, gdzie dodajemy dzieci. `describe()` w `Combo` rozszerza opis o dzieci.
**Po:**

```java
Combo combo = new Combo("Zestaw Rodzinny");
combo.add(new Product("Popcorn XL", "24.00"));
```

**Uruchom:** `scripts/warsztat.sh test m5/s11` - zielone; `S11SolutionTest` pokazuje, że `Product` nie ma już `add`.
**Co powiedzieć:** błąd przeniósł się z runtime do kompilacji. Ceną jest to, że klient musi wiedzieć, czy ma w ręku węzeł.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s11 0 1`

### Krok 2: niemutowalny Composite w Javie 25

**W IDE:** `sealed interface MenuComponent`, rekordy `Product` i `Combo(name, children)` z `Combo.of(...)`. Katalog zapisany deklaratywnie.
**Po:**

```java
case "duo" -> Combo.of("Zestaw Duo",
        new Product("Popcorn L", "18.00"),
        new Product("Cola", "9.00"),
        new Product("Cola", "9.00"));
```

**Uruchom:** test zielony.
**Co powiedzieć:** gdy drzewo powstaje raz, `add()` nie jest potrzebne nigdzie - dylemat Safe/Transparent znika.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s11 1 2`

### Rozwiązanie i uzasadnienie

Safe Composite, gdy drzewo jest modyfikowane; niemutowalny Composite, gdy jest budowane raz (wtedy budowę przejmuje Builder albo fabryka - scena s06).

### Pułapki

- Transparent Composite z domyślnym pustym `add()` (bez wyjątku) - dziecko po cichu ginie.
- Rzutowanie `(Combo) component` w kliencie zamiast poprawienia typów - Safe Composite w przebraniu Transparent.

### Pytanie do sali

Kiedy Transparent Composite jest lepszym wyborem mimo ryzyka błędu w runtime?

## Scena s12. Replace One/Many Distinctions with Composite - zwroty

**Temat ze slajdów:** 16.5. Jeden element lub wiele oraz wyodrębnienie Kompozytu (zastąpienie rozróżnienia „jeden lub wiele” Kompozytem)
**Pakiet:** `pl.training.workshop.m5.s12_onemany` · **Test:** `scripts/warsztat.sh test m5/s12`
**Czas:** ~10 min

### W skrócie

**Co robimy:** `RefundService` ma osobne `refund` i `refundAll` z regułą zwrotu zapisaną dwa razy, a `CancellationDesk` sam wybiera między nimi. Najpierw usuwamy duplikację reguły, potem wprowadzamy wspólny kontrakt `Refundable` z liściem i grupą, a stare metody usuwamy po etapie `@Deprecated`.

**Zasada:** Replace One/Many Distinctions with Composite zastępuje dwie ścieżki API - dla jednego elementu i dla wielu - jednym kontraktem, w którym grupa też jest elementem. Nie wolno przy tym zmienić kolejności przetwarzania, wyniku dla pustej grupy ani liczby efektów naliczanych raz na wywołanie.

**Efekt:** Jest jedna metoda `refund(Refundable)`, reguła biletu żyje w liściu, suma w węźle, a klient nie ma już `if`. Potrącenie 3.00 zostaje w serwisie i nadal jest naliczane raz na zwrot, a nie raz na bilet.

### Co widzimy

`RefundService` ma `refund(ticket, now)` i `refundAll(tickets, now)`, a klient `CancellationDesk` sam wybiera jedną z nich (`if (tickets.size() == 1)`). Reguła zwrotu (>= 24h 100%, < 24h 50%, po starcie 0) jest napisana dwa razy, trochę inaczej. Potrącenie 3.00 jest naliczane raz na zwrot i nie schodzi poniżej 0.

```java
for (TicketData ticket : tickets) {
    if (now.isBefore(ticket.showStart())) {
        long hours = Duration.between(now, ticket.showStart()).toHours();
        total = total.plus(hours >= 24 ? ticket.price() : ticket.price().percent(50));
    }
}
```

Test obejmuje granicę dokładnie 24h, 23h59m, bilet po starcie, listę i pustą listę.

### Krok 1: Extract Method na wspólnej regule

**W IDE:** w `refund` zaznacz wyliczenie kwoty, ⌥⌘M `ticketShare(ticket, now)`. W `refundAll` zastąp ciało pętli wywołaniem `ticketShare`.
**Po:**

```java
total = total.plus(ticketShare(ticket, now));
```

**Uruchom:** `scripts/warsztat.sh test m5/s12` - zielone.
**Co powiedzieć:** zanim wprowadzimy wzorzec, usuwamy duplikację reguły. Test granicy 24h potwierdza, że obie wersje były równoważne.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s12 0 1`

### Krok 2: Composite i jeden kontrakt

**W IDE:** `sealed interface Refundable` z `refundableAmount(now)`, liść `SingleTicket` (reguła z `ticketShare`), węzeł `TicketGroup` (suma). Nowa metoda `refund(Refundable, now)`; stare metody jako delegacje z `@Deprecated`. `CancellationDesk` przechodzi na nowy kontrakt (na razie wciąż wybiera `SingleTicket` albo `TicketGroup`).
**Po:**

```java
public Money refund(Refundable refundable, LocalDateTime now) {
    return refundable.refundableAmount(now).minus(FEE).max(Money.ZERO);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** potrącenie jest w jednym miejscu i naliczane raz na wywołanie - tak jak w obu starych metodach.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s12 1 2`

### Krok 3: usunięcie starych metod

**W IDE:** po migracji klientów Safe Delete (⌘⌦) na `refund(TicketData, ...)` i `refundAll`. W `CancellationDesk` usuń `if` - klient zawsze buduje `TicketGroup.of(tickets)`, także z jednego biletu.
**Po:**

```java
service.refund(TicketGroup.of(tickets), now);
```

**Uruchom:** test zielony; `S12SolutionTest` pokazuje zagnieżdżone grupy.
**Co powiedzieć:** rozróżnienie "jeden/wiele" zniknęło z API. Grupa jednego biletu daje ten sam wynik co bilet.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s12 2 3`

### Rozwiązanie i uzasadnienie

Jeden kontrakt `refund(Refundable)`, reguła biletu w liściu, suma w węźle. Stare API usunięte dopiero po etapie `@Deprecated`.

### Pułapki

- Potrącenie przeniesione do liścia - wtedy lista biletów płaci 3.00 za każdy bilet (zmiana zachowania).
- Zmiana kolejności przetwarzania albo liczby transakcji przy przejściu na Composite.
- Pusta grupa - trzeba jawnie ustalić wynik (tu 0.00, jak w start).

### Pytanie do sali

Co, jeśli biznes chce potrącenia za każdą rezerwację, a jedna grupa obejmuje kilka rezerwacji?

## Scena s13. Extract Composite - kontenery programu

**Temat ze slajdów:** 16.5. Jeden element lub wiele oraz wyodrębnienie Kompozytu (wyodrębnienie Kompozytu)
**Pakiet:** `pl.training.workshop.m5.s13_extractcomposite` · **Test:** `scripts/warsztat.sh test m5/s13`
**Czas:** ~8 min

### W skrócie

**Co robimy:** `Marathon` i `ShortsBlock` powielają obsługę dzieci - listę, `add`, kopię `children()`, sumę minut i opis. Wydzielamy nadklasę `CompositeProgramItem` i podciągamy do niej to, co naprawdę wspólne, a regułę czasu zostawiamy w podklasach.

**Zasada:** Extract Composite wydziela wspólną nadklasę dla kilku kontenerów, które powielają zarządzanie dziećmi. Podobne pętle mogą znaczyć co innego, więc przed podciągnięciem porównujemy kontrakty, a nie tekst. Nowe reguły, np. wykrywanie cykli, to osobna zmiana zachowania.

**Efekt:** Lista dzieci, `add` i szkielet opisu są w jednym miejscu, a podklasy mają po kilkanaście linii. `minutes()` zostaje w podklasach, bo tylko maraton dolicza przerwy - podciągnięcie go do bazy zmieniłoby wynik.

### Co widzimy

`Marathon` i `ShortsBlock` powielają obsługę dzieci: lista, `add` z kontrolą `null`, `children()` jako kopia, suma minut, opis. Różnią się tylko regułą czasu: maraton dodaje 15 minut przerwy między pozycjami.

```java
return children.isEmpty() ? 0 : total + 15 * (children.size() - 1);
```

### Krok 1: Extract Superclass

**W IDE:** na `Marathon` Refactor > Extract Superclass: `CompositeProgramItem`, zaznacz pole `children`, `add`, `children()`. Potem `ShortsBlock extends CompositeProgramItem` i usuń jego kopie (Pull Members Up już nie trzeba - są w bazie).
**Po:**

```java
public abstract class CompositeProgramItem implements ProgramItem {
    private final List<ProgramItem> children = new ArrayList<>();
    public final void add(ProgramItem child) { ... }
```

**Uruchom:** `scripts/warsztat.sh test m5/s13` - zielone.
**Co powiedzieć:** kontrakt dzieci (brak `null`, kopia listy) jest teraz w jednym miejscu dla wszystkich kontenerów.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s13 0 1`

### Krok 2: Pull Up sumy i opisu

**W IDE:** Pull Members Up dla `childrenMinutes()` (`protected final`) i `describe()` jako szablon z hookiem `label()`. Podklasy zostają z regułą czasu i etykietą.
**Po:**

```java
public int minutes() {
    int count = children().size();
    return count == 0 ? 0 : childrenMinutes() + 15 * (count - 1);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** podobne pętle mogą znaczyć co innego. Tu suma jest wspólna, ale przerwy nie - dlatego `minutes()` zostaje w podklasach.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s13 1 2`

### Rozwiązanie i uzasadnienie

`CompositeProgramItem` z pełną obsługą dzieci i szkieletem opisu; `Marathon` i `ShortsBlock` po kilkanaście linii.

### Pułapki

- Podciągnięcie całego `minutes()` do bazy - maraton traci przerwy.
- Wykrywanie cykli (maraton zawierający sam siebie) to osobna zmiana zachowania, nie część Extract Composite.

### Pytanie do sali

Trzeci kontener "blok z przerwą na reklamy co drugi film" - czy nadal pasuje do tej nadklasy?

## Scena s14. Unify Interfaces with Adapter - dwie bramki płatności

**Temat ze slajdów:** 9.1. Ujednolicenie interfejsów Adapterem; 9.2. Co adapter naprawdę musi przetłumaczyć
**Pakiet:** `pl.training.workshop.m5.s14_adapter` · **Test:** `scripts/warsztat.sh test m5/s14`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `CheckoutService.pay` mówi dwoma językami: dla starej bramki składa XML w groszach, dla nowej woła REST w złotych i łapie wyjątek odmowy. Wydzielamy obie gałęzie do metod o jednej sygnaturze, przenosimy je do adapterów interfejsu `PaymentGateway`, a serwis wybiera bramkę z mapy.

**Zasada:** Unify Interfaces with Adapter tłumaczy obcy interfejs na preferowany kontrakt klienta, tak żeby klient zależał tylko od niego. Adapter tłumaczy nazwy, jednostki, format i sposób zgłaszania błędów, ale nie udaje, że semantyka jest identyczna. Jeśli wystarczy Rename Method, adapter jest zbędny.

**Efekt:** Logika serwisu zna tylko `PaymentGateway` i da się ją testować fake'iem, a trzeci dostawca to nowy adapter i wpis w mapie. Stary konstruktor zostaje jako jedyne miejsce znające biblioteki bramek - docelowo do przeniesienia do korzenia kompozycji.

### Co widzimy

`CheckoutService.pay(provider, reservationId, amount)` mówi dwoma językami: dla starej bramki składa XML z kwotą w **groszach** i parsuje atrybuty odpowiedzi, dla nowej woła API REST w **złotych** i łapie wyjątek odmowy. "Biblioteki" bramek (`XmlPayGateway`, `RestPayClient`) są w pakiecie sceny i ich nie zmieniamy.

```java
long grosze = amount.amount().movePointRight(2).longValueExact();
String request = "<charge ref='" + reservationId + "' amount='" + grosze + "'/>";
```

### Krok 1: Extract Method z jednolitą sygnaturą

**W IDE:** każdą gałąź ⌥⌘M: `payWithXml(reservationId, amount)` i `payWithRest(reservationId, amount)`, obie zwracają `PaymentResult`.
**Po:**

```java
if (provider.equals("XML")) {
    return payWithXml(reservationId, amount);
} else if (provider.equals("REST")) {
    return payWithRest(reservationId, amount);
}
```

**Uruchom:** `scripts/warsztat.sh test m5/s14` - zielone.
**Co powiedzieć:** identyczna sygnatura dwóch metod to gotowy kontrakt adaptera.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s14 0 1`

### Krok 2: interfejs i dwa adaptery

**W IDE:** interfejs `PaymentGateway.pay(reservationId, amount)`. Move Method (F6) `payWithXml` do `XmlPayAdapter` i `payWithRest` do `RestPayAdapter`. Konstruktor serwisu bez zmian - tworzy adaptery.
**Po:**

```java
public CheckoutService(XmlPayGateway xml, RestPayClient rest) {
    this.xml = new XmlPayAdapter(xml);
    this.rest = new RestPayAdapter(rest);
}
```

**Uruchom:** test zielony; `S14SolutionTest` sprawdza, że do starej bramki idzie `amount='4000'`.
**Co powiedzieć:** adapter tłumaczy jednostki (złote - grosze), format i sposób zgłaszania odmowy (atrybut kontra wyjątek). Nie udaje, że semantyka jest taka sama.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s14 1 2`

### Krok 3: serwis zależy tylko od interfejsu

**W IDE:** nowy konstruktor `CheckoutService(Map<String, PaymentGateway> gateways)` (kopia `Map.copyOf`); stary konstruktor deleguje do niego ze standardowym zestawem adapterów. `if` zastąp wyszukaniem w mapie; nieznany dostawca - ten sam wyjątek.
**Po:**

```java
PaymentGateway gateway = gateways.get(provider);
if (gateway == null) {
    throw new IllegalArgumentException("unknown provider: " + provider);
}
return gateway.pay(reservationId, amount);
```

**Uruchom:** test zielony.
**Co powiedzieć:** logika serwisu zna tylko `PaymentGateway` i da się ją testować fake'iem. Stary konstruktor to jedyne miejsce, które jeszcze zna biblioteki bramek - docelowo przeniesiemy go do korzenia kompozycji. Trzeci dostawca to nowy adapter i wpis w mapie.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s14 2 3`

### Rozwiązanie i uzasadnienie

Preferowany kontrakt kina (`PaymentGateway`, `PaymentResult`) i dwa adaptery. Granica 500.00 włącznie działa tak samo w obu bramkach - test to sprawdza.

### Pułapki

- Podobna sygnatura, inny kontrakt: jednostki, zaokrąglenia, strefa czasowa, kodowanie znaków.
- Mapowanie wyjątku bez zachowania przyczyny (`cause`) - trudna diagnoza w produkcji.
- Adapter zamykający zasób, którego nie jest właścicielem.
- Adapter tam, gdzie wystarczyłby Rename Method.

### Pytanie do sali

Stara bramka zwraca `status='ERROR'` przy błędnym XML. Jak to zamapować - na odmowę czy wyjątek? Kto decyduje?

## Scena s15. Replace Conditional Dispatcher with Command - konsola kasjera

**Temat ze slajdów:** 6.6. Polecenie: łańcuch warunków zamieniony na rejestr poleceń
**Pakiet:** `pl.training.workshop.m5.s15_command` · **Test:** `scripts/warsztat.sh test m5/s15`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `CashierConsole.handle` to łańcuch `if` po nazwie komendy z pełną logiką i zmianą stanu kasy w każdej gałęzi. Wydzielamy gałęzie do metod o wspólnej sygnaturze, zamieniamy je na bezstanowe komendy działające na `Till`, a łańcuch `if` zastępujemy mapą komend.

**Zasada:** Replace Conditional Dispatcher with Command zamienia każdą gałąź dyspozytora w obiekt komendy, a wybór gałęzi w wyszukanie w rejestrze. Mapa jest równoważna warunkom tylko wtedy, gdy klucze są rozłączne i zachowana jest normalizacja. Command nie daje automatycznie asynchroniczności, retry ani undo.

**Efekt:** Dodanie komendy to nowa klasa i wpis w mapie, bez zmiany `handle`. Komendy nie trzymają danych żądania, więc można je bezpiecznie współdzielić, a `sell` i `SELL` nadal działają tak samo.

### Co widzimy

`CashierConsole.handle(line)` to łańcuch `if (command.equals("SELL"))` z pełną logiką i modyfikacją stanu kasy w każdej gałęzi. Nazwa komendy jest normalizowana `toUpperCase(Locale.ROOT)` - `sell` i `SELL` działają tak samo.

```java
if (command.equals("SELL")) {
    String[] sell = args.split(" ", 2);
    ...
} else if (command.equals("REFUND")) {
    ...
} else if (command.equals("REPORT")) {
```

Test to sesja poleceń: sprzedaż, zwrot, raport oraz błędne polecenia, które nie zmieniają stanu.

### Krok 1: Extract Method dla gałęzi

**W IDE:** ciało każdej gałęzi ⌥⌘M: `sell(args)`, `refund(args)`, `report(args)` - ta sama sygnatura, także gdy `args` nie jest używane.
**Po:**

```java
if (command.equals("SELL")) {
    return sell(args);
}
```

**Uruchom:** `scripts/warsztat.sh test m5/s15` - zielone.
**Co powiedzieć:** jednolita sygnatura przygotowuje interfejs komendy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s15 0 1`

### Krok 2: komendy jako obiekty, stan w Till

**W IDE:** Extract Class `Till` (pola `cash`, `tickets` i cennik). Interfejs `ConsoleCommand.execute(args, till)`, klasy `SellCommand`, `RefundCommand`, `ReportCommand` z ciałami metod. Dyspozytor `if` jeszcze zostaje.
**Po:**

```java
if (command.equals("SELL")) {
    return sell.execute(args, till);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** komendy są bezstanowe - dane żądania to argument, stan kasy to argument. Współdzielona komenda ze stanem poprzedniego żądania to błąd czekający na drugi wątek.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s15 1 2`

### Krok 3: rejestr zamiast warunku

**W IDE:** `Map<String, ConsoleCommand>` w konstruktorze (domyślnym i wstrzykiwanym), `handle` wyszukuje komendę po znormalizowanym kluczu.
**Po:**

```java
ConsoleCommand command = commands.get(parts[0].toUpperCase(Locale.ROOT));
if (command == null) {
    return "Nieznana komenda: " + parts[0];
}
```

**Uruchom:** test zielony; `S15SolutionTest` dodaje komendę bez zmiany dyspozytora.
**Co powiedzieć:** mapa jest równoważna warunkom, bo klucze są **rozłączne** i normalizacja została zachowana. Przy nakładających się predykatach (`startsWith`) kolejność `if` byłaby częścią kontraktu i mapa by go zgubiła.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s15 2 3`

### Rozwiązanie i uzasadnienie

Dyspozytor bez wiedzy o komendach, komendy bezstanowe, stan w `Till`. Nowa komenda to nowa klasa i wpis w mapie.

### Pułapki

- Zgubiona normalizacja klucza - `sell 1 Diuna` nagle "nieznana komenda".
- Command traktowany jako darmowa asynchroniczność, retry albo undo - to osobne decyzje (idempotencja, kompensacja).
- Brak sprawdzenia kompletności rejestru przy starcie aplikacji.

### Pytanie do sali

Jak dodalibyście `UNDO` dla ostatniej sprzedaży? Czego wymaga to od komend?

## Scena s16. Form Template Method - raporty CSV i HTML

**Temat ze slajdów:** 7.1. Utworzenie Metody szablonowej: wspólny szkielet, różne kroki
**Pakiet:** `pl.training.workshop.m5.s16_templatemethod` · **Test:** `scripts/warsztat.sh test m5/s16`
**Czas:** ~8 min

### W skrócie

**Co robimy:** `CsvSalesReport` i `HtmlSalesReport` mają ten sam szkielet (sortowanie, nagłówek, wiersze z sumą, stopka), ale napisany trochę inaczej. Wydzielamy różnice do metod `header`, `row`, `footer`, doprowadzamy `render` do identycznej postaci i podciągamy go do nadklasy `SalesReport` jako `final`.

**Zasada:** Form Template Method umieszcza wspólną sekwencję kroków w metodzie bazowej, a zmienne kroki w metodach nadpisywanych przez podklasy. Stosuje się ją, gdy kolejność kroków jest stała i różnią się tylko szczegóły. Przy wielu hookach albo zmiennej kolejności lepsza jest kompozycja lub Strategy.

**Efekt:** Sortowanie i sumowanie są w jednym miejscu, a nowy format (np. Markdown) to trzy metody. `final` chroni kolejność kroków, ale w opublikowanej bibliotece dodanie go do nadpisywanej metody złamałoby klientów.

### Co widzimy

`CsvSalesReport` i `HtmlSalesReport` mają ten sam szkielet: kopia listy, sortowanie po godzinie, nagłówek, wiersze z sumowaniem, stopka. Napisane trochę inaczej (stream kontra `sort`, inne nazwy zmiennych), więc duplikacja nie rzuca się w oczy. Różnice to formatowanie i escapowanie (`;` w CSV, `&` w HTML).

### Krok 1: Extract Method na różnicach

**W IDE:** w obu klasach ⌥⌘M dla nagłówka, wiersza i stopki: `header()`, `row(Sale)`, `footer(int tickets, Money total)`. Przepisz `render` HTML na ten sam kształt co CSV (nazwy zmiennych, sortowanie).
**Po:**

```java
StringBuilder text = new StringBuilder(header());
for (Sale sale : sorted) {
    text.append(row(sale));
    ...
}
return text.append(footer(tickets, total)).toString();
```

**Uruchom:** `scripts/warsztat.sh test m5/s16` - zielone.
**Co powiedzieć:** ujednolicamy sygnatury po kroku, aż `render` obu klas będzie identyczny znak w znak.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s16 0 1`

### Krok 2: Extract Superclass + Pull Up render

**W IDE:** Extract Superclass `SalesReport` z CSV, zaznacz `render` i trzy metody jako abstrakcyjne. W HTML `extends SalesReport`, usuń kopię `render`. Dodaj `final` do `render`.
**Po:**

```java
public final String render(List<Sale> sales) { ... }
protected abstract String header();
protected abstract String row(Sale sale);
protected abstract String footer(int tickets, Money total);
```

**Uruchom:** test zielony; `S16SolutionTest` sprawdza `final` i dodaje format Markdown trzema metodami.
**Co powiedzieć:** `final` chroni kolejność kroków. W opublikowanej bibliotece dodanie `final` do nadpisywanej metody łamie klientów.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s16 1 2`

### Rozwiązanie i uzasadnienie

Szkielet raz, w bazie; podklasy to czyste formatowanie. Pusta sprzedaż daje nagłówek i sumę 0.00 w obu formatach.

### Pułapki

- Hook opcjonalny z domyślną implementacją, która nie pasuje do wszystkich podklas.
- Wywołanie hooka z konstruktora bazy - podklasa widzi niezainicjalizowane pola.
- Wiele hooków i zmienna kolejność - wtedy lepsza kompozycja lub Strategy.

### Pytanie do sali

Raport PDF potrzebuje stronicowania co 30 wierszy. Czy to jeszcze Template Method?

## Scena s17. Limit Instantiation with Singleton - cennik

**Temat ze slajdów:** 16.7. Ograniczenie liczby instancji Singletonem; 17.4. Wchłonięcie Singletona
**Pakiet:** `pl.training.workshop.m5.s17_singleton` · **Test:** `scripts/warsztat.sh test m5/s17`
**Czas:** ~10 min

### W skrócie

**Co robimy:** `TicketDesk` przy każdej wycenie tworzy nowy `PriceList` i parsuje taryfę, choć cennik jest niemutowalny. Po pomiarze ograniczamy go do jednej instancji (najpierw `getInstance()`, potem enum), a na końcu ukrywamy za interfejsem `Tariff` wstrzykiwanym do `TicketDesk`.

**Zasada:** Limit Instantiation with Singleton to decyzja o cyklu życia: jedna instancja jest bezpieczna tylko wtedy, gdy obiekt jest niemutowalny, a instancje równoważne. Enum daje bezpieczną publikację, ale oznacza jedną instancję na loader klas, nie na JVM. Odwrotny ruch, Inline Singleton, jest równie ważny, gdy globalny dostęp szkodzi testom.

**Efekt:** Cennik powstaje raz, a `TicketDesk` zależy od kontraktu i da się go przetestować z innym cennikiem bez globalnego stanu. Globalna instancja zostaje tylko w domyślnym konstruktorze, a dodanie do niej mutowalnego stanu byłoby najgorszym wariantem.

### Co widzimy

`TicketDesk.quote` przy każdym wywołaniu robi `new PriceList()`, a konstruktor parsuje taryfę. Cennik jest niemutowalny, więc instancje są równoważne. `S17SolutionTest` najpierw **mierzy**: trzy wyceny to trzy utworzone cenniki.

```java
Money price = new PriceList().basePrice(format);
```

### Krok 1: klasyczny Singleton

**W IDE:** prywatny konstruktor, `private static final PriceList INSTANCE = new PriceList()`, `getInstance()`. Licznik z pomiaru usuń. Klient woła `PriceList.getInstance()`.
**Po:**

```java
Money price = PriceList.getInstance().basePrice(format);
```

**Uruchom:** `scripts/warsztat.sh test m5/s17` - zielone.
**Co powiedzieć:** Singleton to decyzja o cyklu życia - bezpieczna tylko dlatego, że obiekt jest niemutowalny i udowodniliśmy równoważność instancji.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s17 0 1`

### Krok 2: Singleton jako enum

**W IDE:** zamień klasę na `enum PriceList { INSTANCE; ... }`, mapa przez `Map.copyOf`.
**Po:**

```java
Money price = PriceList.INSTANCE.basePrice(format);
```

**Uruchom:** test zielony.
**Co powiedzieć:** enum daje bezpieczną publikację i odporność na serializację oraz refleksję. Ale to **jedna instancja na loader klas**, nie "jedna na JVM".
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s17 1 2`

### Krok 3: wstrzyknięcie zamiast globalnego dostępu

**W IDE:** Extract Interface `Tariff` z enuma; `TicketDesk(Tariff)` plus konstruktor bezargumentowy `this(PriceList.INSTANCE)`.
**Po:**

```java
public TicketDesk() {
    this(PriceList.INSTANCE);
}
```

**Uruchom:** test zielony; `injectedTariffNeedsNoGlobalState` używa cennika promocyjnego jako lambdy.
**Co powiedzieć:** o jednej instancji decyduje korzeń kompozycji, a klient zależy od kontraktu. Gdyby ktoś dodał do singletona `setPromo(...)`, testy zaczęłyby wpływać na siebie nawzajem - mutowalny globalny stan to najgorszy wariant.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s17 2 3`

### Rozwiązanie i uzasadnienie

Jedna instancja cennika (enum), ale ukryta za interfejsem i wstrzykiwana. Odwrotna transformacja (Inline Singleton) jest równie ważna - gdy globalny dostęp szkodzi testom, wracamy do jawnej zależności.

### Pułapki

- Singleton z mutowalnym stanem (promocje, cache) - testy zależne od kolejności.
- Leniwy singleton z podwójnym sprawdzaniem bez `volatile`.
- Singleton "bo tak wygodniej" bez pomiaru kosztu tworzenia.

### Pytanie do sali

Cennik ma się zmieniać o północy bez restartu. Co wtedy z singletonem?

## Scena s18. Move Accumulation to Collecting Parameter - ostrzeżenia walidacji

**Temat ze slajdów:** 16.6. Przeniesienie zbierania wyników do parametru zbierającego
**Pakiet:** `pl.training.workshop.m5.s18_collectingparameter` · **Test:** `scripts/warsztat.sh test m5/s18`
**Czas:** ~8 min

### W skrócie

**Co robimy:** `ReservationValidator` skleja ostrzeżenia w `String`, a każda metoda pomocnicza dopisuje własny separator. Zamieniamy sklejanie na listę, potem przekazujemy akumulator do metod `check...`, a na końcu zawężamy go do klasy `Warnings`.

**Zasada:** Collecting Parameter polega na tym, że metody dopisują wyniki do przekazanego akumulatora, zamiast zwracać fragmenty do sklejenia. Właścicielem kolekcji jest wywołujący, trzeba ustalić, czy metoda czyści, czy dopisuje, a wynik częściowy po wyjątku jest częścią kontraktu. Dobry akumulator ma wąski typ, a nie ogólną mutowalną kolekcję.

**Efekt:** Separator i format wyniku są w jednym miejscu, a metody pomocnicze mogą tylko dopisać ostrzeżenie. Treść, kolejność i separator ostrzeżeń są dokładnie takie jak w start.

### Co widzimy

`ReservationValidator.validate` skleja ostrzeżenia w `String`: każda metoda pomocnicza zwraca fragment zakończony `"; "`, a na końcu obcinamy dwa znaki. Łatwo zgubić separator.

```java
warnings += checkEmail(draft.email());
warnings += checkSeats(draft.seats());
...
return warnings.isEmpty() ? "OK" : warnings.substring(0, warnings.length() - 2);
```

Test sprawdza treść, kolejność i separator, w tym duplikaty miejsc zgłaszane jeden raz.

### Krok 1: lista zamiast sklejania

**W IDE:** metody pomocnicze zwracają `List<String>` bez separatora, `validate` robi `addAll` i `String.join("; ", ...)`.
**Po:**

```java
warnings.addAll(checkEmail(draft.email()));
warnings.addAll(checkSeats(draft.seats()));
```

**Uruchom:** `scripts/warsztat.sh test m5/s18` - zielone.
**Co powiedzieć:** separator dokłada teraz jedno miejsce. Metody wciąż tworzą własne listy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s18 0 1`

### Krok 2: Collecting Parameter

**W IDE:** Change Signature (⌘F6) - dodaj parametr `List<String> warnings`, zmień zwracany typ na `void`, zamień `return List.of(x)` na `warnings.add(x)`. Warunek czasu seansu wyciągnij do `checkShowTime(draft, warnings)`.
**Po:**

```java
checkEmail(draft.email(), warnings);
checkSeats(draft.seats(), warnings);
checkShowTime(draft, warnings);
```

**Uruchom:** test zielony.
**Co powiedzieć:** metody dopisują do przekazanego akumulatora. Właścicielem kolekcji jest `validate` - tworzy ją i decyduje o formacie.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s18 1 2`

### Krok 3: wąski typ parametru

**W IDE:** klasa `Warnings` z `add` i `summary()`; Type Migration (⇧⌘F6) z `List<String>` na `Warnings`.
**Po:**

```java
Warnings warnings = new Warnings();
...
return warnings.summary();
```

**Uruchom:** test zielony.
**Co powiedzieć:** metoda pomocnicza może tylko dopisać - nie wyczyści ani nie przestawi cudzych ostrzeżeń.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s18 2 3`

### Rozwiązanie i uzasadnienie

Każda reguła to metoda `check...(..., Warnings)`, format wyniku w jednym miejscu, akumulator o wąskim API.

### Pułapki

- Ogólna mutowalna mapa jako parametr zbierający - ukrywa, kto co zapisuje.
- Niejasne, czy metoda czyści akumulator, czy dopisuje.
- Wyjątek w połowie zbierania zostawia wynik częściowy - to część kontraktu, trzeba go ustalić.

### Pytanie do sali

Czy `Warnings` powinno pozwalać na poziomy (błąd kontra ostrzeżenie)? Jak to zmieni kontrakt?

## Scena s19. Visitor i macierz zmian - pozycje zamówienia

**Temat ze slajdów:** 15.1. Odwiedzający i macierz zmian; 15.2. Odwiedzający albo hierarchia `sealed` i `switch`
**Pakiet:** `pl.training.workshop.m5.s19_visitor` · **Test:** `scripts/warsztat.sh test m5/s19`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `ReceiptPrinter` ma trzy operacje na pozycjach zamówienia, każda z łańcuchem `instanceof` zakończonym wyjątkiem w runtime. Zamieniamy je na klasyczne Visitory, a potem pokazujemy alternatywę z Javy 25: zamkniętą hierarchię `sealed` i `switch` po typach bez `default`.

**Zasada:** Visitor przenosi operacje na strukturze do osobnych klas, a element wybiera właściwą metodę przez double dispatch. Macierz zmian mówi, kiedy go stosować: nowa operacja jest tania, nowy rodzaj elementu wymaga zmiany wszystkich Visitorów. W Javie 25 tę samą kontrolę kompilatora daje wyczerpujący `switch` po `sealed`, a Visitor ma sens, gdy hierarchii nie da się zamknąć.

**Efekt:** W kodzie nie ma `instanceof`, a pozycja bez obsługi to błąd kompilacji zamiast awarii na kasie. Obie formy są poprawne - wybór zależy od tego, czy częściej dochodzą operacje, czy rodzaje pozycji.

### Co widzimy

Pozycje zamówienia: bilet (VAT 8%), produkt baru (VAT 23%), voucher (pomniejsza kwotę, bez VAT). `ReceiptPrinter` ma trzy operacje - linia paragonu, kwota, VAT - każda z łańcuchem `instanceof` zakończonym wyjątkiem w runtime. Nowy rodzaj pozycji kompiluje się bez błędu i wybucha na kasie.

```java
if (item instanceof TicketItem ticket) {
    return vatOf(ticket.price(), 8);
} else if (item instanceof SnackItem snack) {
    return vatOf(snack.price(), 23);
} else if (item instanceof VoucherItem) {
    return Money.ZERO;
}
throw new IllegalArgumentException("unknown item: " + item);
```

### Krok 1: accept + pierwszy Visitor

**W IDE:** interfejs `OrderItemVisitor<R>` z `visitTicket/visitSnack/visitVoucher`, metoda `accept` w `OrderItem` i rekordach. Operację "linia paragonu" przenieś do `ReceiptLineVisitor`.
**Po:**

```java
text.append(item.accept(lines)).append('\n');
```

**Uruchom:** `scripts/warsztat.sh test m5/s19` - zielone.
**Co powiedzieć:** double dispatch - element wybiera metodę odwiedzającego. Brak metody dla typu to błąd kompilacji.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s19 0 1`

### Krok 2: pozostałe operacje jako Visitory

**W IDE:** `AmountVisitor` i `VatVisitor`, usuń łańcuchy `instanceof`.
**Po:**

```java
total = total.plus(item.accept(amounts));
vat = vat.plus(item.accept(vats));
```

**Uruchom:** test zielony; `S19SolutionTest` dodaje operację "grupa VAT" jako nowy Visitor.
**Co powiedzieć:** macierz zmian: nowa operacja - nowa klasa, tanio; nowy rodzaj pozycji - zmiana interfejsu i **wszystkich** Visitorów.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s19 1 2`

### Krok 3: alternatywa Java 25 - sealed + switch

**W IDE:** `sealed interface OrderItem permits ...`, usuń `accept` i Visitory, operacje jako `switch` po typach bez `default` (z record patterns w linii paragonu).
**Po:**

```java
return switch (item) {
    case TicketItem ticket -> vatOf(ticket.price(), 8);
    case SnackItem snack -> vatOf(snack.price(), 23);
    case VoucherItem voucher -> Money.ZERO;
};
```

**Uruchom:** test zielony.
**Co powiedzieć:** ta sama kontrola kompilatora co w Visitorze, bez ceremonii `accept/visit`. Visitor ma sens, gdy hierarchia nie może być `sealed` (inny moduł, biblioteka) albo gdy odwiedzający ma stan i logikę przejścia struktury.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s19 2 3`

### Rozwiązanie i uzasadnienie

Dwie poprawne formy docelowe: klasyczny Visitor (`step2`) i wyczerpujący `switch` po zamkniętej hierarchii (`step3`). Macierz zmian:

| Częsta zmiana | Naturalniejszy kierunek |
| --- | --- |
| nowe operacje, stabilne rodzaje pozycji | Visitor albo switch po sealed |
| nowe rodzaje pozycji, stabilne operacje | metody polimorficzne w pozycjach |
| jedna prosta akumulacja | Collecting Parameter (scena s18) |

### Pułapki

- `default` w `switch` po sealed - wyłącza kontrolę kompilatora dla nowych typów.
- VAT zaokrąglany per pozycja kontra od sumy - różne wyniki, trzeba zachować sposób ze start.
- Visitor dla hierarchii, do której ciągle dochodzą typy - każdy nowy typ dotyka wszystkich Visitorów.

### Pytanie do sali

Dochodzi pozycja "karta podarunkowa" i operacja "eksport do księgowości". Która forma przyjmie te zmiany taniej?

## Scena s20. Mapa decyzji - jedna tabela, dwie struktury docelowe

**Temat ze slajdów:** 4.2. Najpierw rodzaj zmienności, potem wzorzec; 18.1. Mapa decyzji (1/2); 18.2. Mapa decyzji (2/2); 18.3. Lista kontrolna
**Pakiet:** `pl.training.workshop.m5.s20_decisionmap` · **Test:** `scripts/warsztat.sh test m5/s20`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `ShowPricing` ma tabelę cen 3x3 wpisaną w zagnieżdżone warunki, w których splecione są format i reguła dnia. Najpierw rozplatamy dwie osie, a potem z tego samego punktu budujemy dwie alternatywy: Strategy dla reguły dnia albo typ formatu z zachowaniem.

**Zasada:** Wzorzec wybieramy według rodzaju zmienności, a nie kształtu kodu - ten sam `switch` może sygnalizować różne problemy. Pytamy, która oś zmienia się częściej, i tam budujemy punkt rozszerzenia. Gdy tego nie wiemy, zostajemy przy prostej strukturze bez nowych typów.

**Efekt:** Obie ścieżki są behawioralnie równoważne, a różnią się kosztem przyszłej zmiany: A tanio przyjmuje nowe akcje dniowe, B nowe formaty. Reguła zależna od obu osi naraz łamie założenie niezależności i wtedy tabela może być lepsza niż wzorzec.

### Co widzimy

`ShowPricing.price(day, format)` ma tabelę cen 3x3 wpisaną w zagnieżdżone warunki. Dwie osie zmienności - format i reguła dnia ("tani wtorek" -30%, weekend +2.00) - są splecione.

```java
if (format.equals("2D")) {
    return Money.of(day == DayOfWeek.TUESDAY ? "17.50" : weekend ? "27.00" : "25.00");
} else if (format.equals("3D")) {
    return Money.of(day == DayOfWeek.TUESDAY ? "22.40" : weekend ? "34.00" : "32.00");
} ...
```

Ta scena ma **dwie ścieżki**: krok 1 jest wspólny, krok 2 to ścieżka A (Strategy dla reguły dnia), krok 3 to ścieżka B (typ formatu z zachowaniem) - budowana od kroku 1, nie od kroku 2. Test równoważności sprawdza pełną tabelę dla obu ścieżek.

### Krok 1: rozplecenie osi (wspólny)

**W IDE:** zauważ, że tabela to dwie niezależne reguły. ⌥⌘M `basePrice(format)` ze `switch` po formacie i `adjustForDay(day, base)` ze `switch` po dniu. `price` = złożenie.
**Po:**

```java
public Money price(DayOfWeek day, String format) {
    return adjustForDay(day, basePrice(format));
}
```

**Uruchom:** `scripts/warsztat.sh test m5/s20` - zielone (9 cen i nieznany format).
**Co powiedzieć:** to jest moment decyzji. Ten sam `switch` może sygnalizować różne problemy - pytamy, **która oś zmienia się częściej**.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s20 0 1`

### Krok 2: ścieżka A - zmienia się reguła dnia (Strategy)

**W IDE:** interfejs `DayPolicy.apply(base)`, stałe `CHEAP_TUESDAY`, `WEEKEND`, `REGULAR` i kalendarz `DayPolicies.standard(day)`. `ShowPricing` dostaje kalendarz w konstruktorze (domyślnie standardowy). Format zostaje prostym `switch`.
**Po:**

```java
return calendar.apply(day).apply(basePrice(format));
```

**Uruchom:** test zielony; `pathAMakesANewDayCampaignCheap` dodaje "środę seniora" bez dotykania formatów.
**Co powiedzieć:** wybierz tę ścieżkę, gdy marketing co miesiąc zmienia akcje dniowe, a formaty są stabilne. Zestaw polityk jest otwarty i wstrzykiwany.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s20 1 2`

### Krok 3: ścieżka B - zmienia się zestaw formatów (typ z zachowaniem)

**W IDE:** pokaż alternatywę od kroku 1: `enum Format` z ceną bazową i metodą `priceOn(day)`, `Format.of(code)` z tym samym wyjątkiem. `ShowPricing` deleguje. Reguła dnia zostaje zwykłym `switch` w typie.
**Po:**

```java
public Money price(DayOfWeek day, String format) {
    return Format.of(format).priceOn(day);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** wybierz tę ścieżkę, gdy dochodzą formaty (4DX, ScreenX) z własnymi wyjątkami od reguł, a kalendarz jest stabilny. Porównaj `scripts/warsztat.sh diff m5/s20 1 3` z `diff m5/s20 1 2`.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s20 1 3` (alternatywa dla kroku 2; `diff 2 3` pokazuje zamianę ścieżki)

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
**Pakiet:** `pl.training.workshop.m5.s21_reversiblepattern` · **Test:** `scripts/warsztat.sh test m5/s21`
**Czas:** ~12 min

### W skrócie

**Co robimy:** Rozliczenie z dystrybutorem ma dwa modele w jednym `switch`, w tym festiwalowy z obcego systemu w groszach. Wprowadzamy Strategy z Adapterem, a gdy umowy festiwalowe wygasają, usuwamy wariant i cały wzorzec.

**Zasada:** Wzorzec to odpowiedź na konkretne siły (tu: dwa istniejące warianty i obcy interfejs), a nie kod do skopiowania. Strategy opłaca się dla rodziny wymiennych algorytmów, Adapter dla realnego tłumaczenia obcego modelu, a usunięcie wzorca jest równie poprawną refaktoryzacją jak jego wprowadzenie.

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

**Uruchom:** `scripts/warsztat.sh test m5/s21` - zielono; `step1AdapterPaysTheFestivalFeeConvertedFromCents` sprawdza 300.00 i 150.00.
**Co powiedzieć:** uzasadnienie to dwa **istniejące** warianty z różnymi właścicielami i obcy interfejs. Adapter ma realną pracę (konwersja jednostek), nie jest pustym przekazaniem 1:1.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s21 0 1`

### Krok 2: Wariant znika - Safe Delete adaptera

**W IDE:** umowy festiwalowe wygasły. Usuń wpis `"FESTIVAL"` z mapy, Safe Delete (⌘⌦) na `FestivalFeeAdapter`.
**Po:**

```java
private final Map<String, SettlementModel> models = Map.of(
        "PERCENT", new PercentageModel());
```

**Uruchom:** test zielony; `afterTheVariantIsGoneFestivalDealsAreRejected` - to świadoma zmiana zachowania, dlatego sprawdzana osobno, a nie testem równoważności.
**Co powiedzieć:** zostaje Strategy z jedną implementacją i mapą z jednym wpisem - klasyczny sygnał nadmiaru wzorca ze slajdu 17.1.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s21 1 2`

### Krok 3: Usunięcie wzorca

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
**Co powiedzieć:** usunięcie wzorca jest równie poprawną refaktoryzacją jak jego wprowadzenie. Gdy wróci drugi model, przywrócimy Strategy tymi samymi krokami w przód.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s21 2 3`

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
**Pakiet:** `pl.training.workshop.m5.s22_middleman` · **Test:** `scripts/warsztat.sh test m5/s22`
**Czas:** ~8 min

### W skrócie

**Co robimy:** `CinemaFacade` prawie wszystko deleguje 1:1 do `ScreeningCatalog`, ale w `freeSeats` po cichu zamienia nieznany seans na 0 wolnych miejsc. Przenosimy to tłumaczenie do `SeatBadge`, migrujemy obu klientów do katalogu po jednym i usuwamy fasadę.

**Zasada:** Remove Middle Man usuwa pośrednika, który tylko przekazuje wywołania dalej, i pozwala klientom rozmawiać bezpośrednio z właściwym obiektem - ruch odwrotny to Hide Delegate. Najpierw sprawdzamy, czy pośrednik nie robi czegoś więcej (autoryzacja, transakcja, telemetria, retry, translacja błędów), bo fasada modułu czy seam testowy mogą być wartościowe.

**Efekt:** Klienci zależą bezpośrednio od `ScreeningCatalog`, a zachowanie "nieznany seans = WYPRZEDANE" zostało zachowane w jedynym miejscu, które go potrzebuje. To dziwne zachowanie jest teraz widoczne i czeka na osobną decyzję.

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

**Uruchom:** `scripts/warsztat.sh test m5/s22` - przypadek "nieznany seans wygląda jak wyprzedany" nadal zielony.
**Co powiedzieć:** najpierw sprawdź, co pośrednik naprawdę robi: autoryzacja, transakcja, telemetria, retry, translacja błędów. Tu była translacja - przenosimy ją do klienta, zanim usuniemy pośrednika.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s22 0 1`

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
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s22 1 2`

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
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s22 2 3`

### Rozwiązanie i uzasadnienie

`step3`: dwaj klienci zależą od `ScreeningCatalog`, zachowanie dla nieznanego seansu zachowane w jedynym miejscu, które go potrzebuje. Test `catalogItselfRejectsUnknownScreening` pokazuje, dlaczego krok 1 był konieczny.

### Pułapki

- Inline wszystkich metod fasady naraz (IntelliJ: Refactor → Remove Middleman) bez sprawdzenia, że któraś tłumaczy błędy.
- Usunięcie fasady, która jest granicą modułu (klienci z innych pakietów zaczną zależeć od szczegółów).
- Utrwalenie dziwnego zachowania ("nieznany = wyprzedany") bez notatki - to kandydat na osobną decyzję.

### Pytanie do sali

Czy "nieznany seans = WYPRZEDANE" to błąd? Kiedy wolno go poprawić?

## Scena s23. Refaktoryzacja a zmiana kontraktu - zwrot na BigDecimal

**Temat ze slajdów:** 5.3. Refaktoryzacja a zmiana kontraktu; 5.4. Co trzeba zachować przy wprowadzaniu wzorca; 3.10. Bezpieczna pętla refaktoryzacji do wzorca
**Pakiet:** `pl.training.workshop.m5.s23_contractchange` · **Test:** `scripts/warsztat.sh test m5/s23`
**Czas:** ~10 min

### W skrócie

**Co robimy:** `RefundCalculator.refund` liczy zwrot na `double`, a kontraktem jest też format z dwoma miejscami po przecinku. Robimy jedną czystą refaktoryzację, potem pokazujemy naiwne przejście na `BigDecimal`, które zmienia dwie rzeczy naraz, i na koniec zostawiamy tylko uzgodnioną zmianę.

**Zasada:** Refaktoryzacja zachowuje obserwowalne zachowanie w przyjętej granicy, a kryterium brzmi: klient nie dostrzega żadnej nieuzgodnionej różnicy. Zmiana typu, zaokrąglenia, formatu czy wyjątku to zmiana kontraktu, którą robimy i zatwierdzamy osobno od ruchów strukturalnych, a nie "przy okazji".

**Efekt:** Zachowanie świadomie się zmienia w jednym punkcie: 64.35 anulowane 2 godziny przed seansem daje 29.18 zamiast 29.17, zatwierdzone w osobnym commicie. Format "0.00" zostaje, a naiwny krok 2 nie powinien trafić do repozytorium.

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

**Uruchom:** `scripts/warsztat.sh test m5/s23` - wszystko zielone, łącznie z przypadkami brzegowymi (29.17 i 0.00).
**Co powiedzieć:** `x * 1.0 == x` w `double` - arytmetyka jest identyczna co do bitu. To jest refaktoryzacja.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s23 0 1`

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
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s23 1 2`

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
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s23 2 3`

### Rozwiązanie i uzasadnienie

`step3/RefundCalculator`: `BigDecimal` i HALF_UP zgodnie z regułą domeny, format bez zmian. Historia commitów: refaktoryzacja (krok 1), zmiana typu i reguły zaokrąglenia jako jawna zmiana kontraktu (krok 3). Krok 2 nie powinien trafić do repozytorium.

### Pułapki

- Test tylko na "okrągłych" kwotach - `double` i `BigDecimal` dają ten sam wynik i zmiana przechodzi niezauważona.
- `new BigDecimal(double)` zamiast `BigDecimal.valueOf(double)` - binarne rozwinięcie (64.349999...) i inny wynik zaokrąglenia.
- `BigDecimal.equals` porównuje skalę ("0" różni się od "0.00"), `compareTo` nie.
- Zmiana typu wyjątku, kolejności efektów albo formatu "bo i tak ruszamy ten kod".

### Pytanie do sali

Kto zatwierdza zmianę kontraktu o 1 grosz: programista, księgowość czy klient API?

## Scena s24. Rename - nazwy, które żyją poza Javą

**Temat ze slajdów:** 3.6. Refaktoryzacje automatyczne w IDE: co dają, a czego nie widzą; 3.7. Zmiana nazwy i wyodrębnienie metody w drodze do wzorca
**Pakiet:** `pl.training.workshop.m5.s24_rename` · **Test:** `scripts/warsztat.sh test m5/s24`
**Czas:** ~12 min

### W skrócie

**Co robimy:** W `SalesReport` nazwy `calc2`, `s`, `m`, `t` nic nie mówią, ale nazwa metody żyje w konfiguracji, a nazwy komponentów rekordu trafiają refleksją do nagłówka CSV. Zmieniamy nazwy od najbezpieczniejszych lokalnych do tych, które są kontraktem zewnętrznym.

**Zasada:** Rename nadaje nazwę opisującą rolę w kontekście, a nie typ ani implementację. Dla zmiennych lokalnych IDE robi to bezpiecznie, ale nazwa użyta w konfiguracji, refleksji, JSON czy ORM jest częścią API i wymaga strategii migracji. IDE nie widzi takich użyć, więc nie mylić operacji ⇧F6 z bezpieczną zmianą kontraktu.

**Efekt:** Kod czyta się po nazwach ról, a oba kontrakty zostają nietknięte: stara nazwa `calc2` jako przestarzały delegat, nagłówek CSV jako jawna stała. Koszt to delegat, którego nie wolno usunąć, dopóki ktoś nie zmieni konfiguracji na serwerach.

### Co widzimy

`SalesReport.calc2(List<Sale> s, boolean flag)` składa CSV dla dystrybutora: `m`, `x`, `l`, `b`, a wiersz to `Line(String t, int n, BigDecimal d)`. Dwie nazwy żyją poza Javą:

```java
// ReportJob - w produkcji plik report.properties na serwerze
static final String CONFIG = """
        report.method=calc2
        report.onlineOnly=true
        """;
...
Method method = SalesReport.class.getMethod(config.getProperty("report.method"), ...);

// SalesReport - nagłówek CSV liczony refleksją z nazw komponentów rekordu
Arrays.stream(Line.class.getRecordComponents()).map(RecordComponent::getName)...
```

### Krok 1: Rename zmiennych lokalnych i parametrów

**W IDE:** kursor na `s`, ⇧F6, `sales`. Tak samo `flag` -> `onlineOnly`, `m` -> `linesByTitle`, `x` -> `sale`, `l` -> `previous` / `line`, `b` -> `csv`, `v` -> `values`, `c` -> `component`.
**Po:**

```java
public String calc2(List<Sale> sales, boolean onlineOnly) {
    Map<String, Line> linesByTitle = new TreeMap<>();
    for (Sale sale : sales) {
        if (onlineOnly && !sale.online()) {
```

**Uruchom:** `scripts/warsztat.sh test m5/s24` - 14 testów zielonych.
**Co powiedzieć:** zasięg lokalny, zero użyć poza metodą, więc IDE robi to w stu procentach bezpiecznie. Od tego zaczynamy każde porządkowanie, bo tanie nazwy ułatwiają następne kroki.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s24 0 1`

### Krok 2: Rename metody z delegatem dla starej nazwy

**W IDE:** najpierw na żywo naiwnie: ⇧F6 na `calc2`, nazwa `revenueCsv`, bez zaznaczania "Search in comments and strings". Uruchom test: `everyStepRunsTheConfiguredJob` jest czerwony (`NoSuchMethodException: calc2`). IDE nie wie, że tekst w konfiguracji to nazwa metody. Cofnij (⌘Z). Teraz poprawnie: skopiuj metodę, zmień nazwę kopii na `revenueCsv`, a ciało `calc2` zamień na delegowanie i oznacz `@Deprecated`.
**Po:**

```java
/** Stara nazwa z konfiguracji report.properties (report.method=calc2). */
@Deprecated
public String calc2(List<Sale> sales, boolean onlineOnly) {
    return revenueCsv(sales, onlineOnly);
}

public String revenueCsv(List<Sale> sales, boolean onlineOnly) {
```

**Uruchom:** test zielony, zadanie z konfiguracji działa.
**Co powiedzieć:** konfiguracja leży na serwerach, poza naszym repozytorium, więc nie zmienimy jej w tym samym commicie. Stara nazwa zostaje jako przestarzały delegat, dopóki ktoś nie zmieni konfiguracji wszędzie. To jest strategia migracji ze slajdu.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s24 1 2`

### Krok 3: Rename komponentów rekordu po odcięciu kontraktu CSV

**W IDE:** najpierw naiwnie: ⇧F6 na `t` w `Line`, `title`. Test pokaże nagłówek `title;n;d` zamiast `t;n;d`: plik dla dystrybutora właśnie się zmienił. Cofnij. Teraz poprawnie: najpierw zamień refleksyjne `header()` i `row()` na jawny `CSV_HEADER = "t;n;d"` i jawne składanie wiersza (test zielony), dopiero potem ⇧F6 na `t`, `n`, `d` -> `title`, `tickets`, `revenue`.
**Po:**

```java
/** Nagłówek uzgodniony z dystrybutorem - kontrakt zewnętrzny, NIE nazwy pól w Javie. */
static final String CSV_HEADER = "t;n;d";

public record Line(String title, int tickets, BigDecimal revenue) {
}

private static String row(Line line) {
    return line.title() + ";" + line.tickets() + ";" + line.revenue();
}
```

**Uruchom:** test zielony, także `S24RenameLimitsTest.reflectionTurnsJavaNamesIntoTheCsvHeader`, który pokazuje, że refleksja na nowym rekordzie dałaby `title;tickets;revenue`.
**Co powiedzieć:** nazwa w Javie i nazwa w kontrakcie zewnętrznym to dwie różne rzeczy. Dopóki są sklejone refleksją, każdy Rename jest zmianą API.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s24 2 3`

### Rozwiązanie i uzasadnienie

`step3`: wszystkie nazwy mówią o roli (`revenueCsv`, `onlineOnly`, `linesByTitle`, `Line.revenue`), a dwa kontrakty zewnętrzne (nazwa metody w konfiguracji i nagłówek CSV) są zachowane jawnie: przez delegat i przez stałą. Oba testy równoważności (wywołanie z Javy i zadanie z konfiguracji) są zielone na każdym etapie.

### Pułapki

- Poleganie na "Search in comments and strings" w IntelliJ. Czasem pomaga, ale nie widzi plików poza projektem ani nazw składanych dynamicznie (`"calc" + version`).
- Inne miejsca, w których nazwa jest kontraktem: JSON (Jackson), kolumny ORM, szablony, `Serializable`, nazwy beanów, logi parsowane przez monitoring.
- Usunięcie delegatu `calc2` "bo nikt go nie woła". Find Usages go nie znajdzie, bo wołają go tylko refleksja i konfiguracja.

### Pytanie do sali

Jak w waszym systemie sprawdzić, czy nazwa klasy albo metody nie jest użyta w konfiguracji, bazie danych lub innym repozytorium?

## Scena s25. Extract Method - przepływ danych i wiele wyjść

**Temat ze slajdów:** 3.7. Zmiana nazwy i wyodrębnienie metody w drodze do wzorca
**Pakiet:** `pl.training.workshop.m5.s25_extractmethod` · **Test:** `scripts/warsztat.sh test m5/s25`
**Czas:** ~15 min

### W skrócie

**Co robimy:** `TicketSummary.describe` liczy cenę, sumę i liczbę miejsc VIP, a potem składa dokument, i to wszystko w jednej metodzie z komentarzami zamiast nazw. Wydzielamy bloki do metod, a pętlę z dwoma wyjściami najpierw rozdzielamy przez Split Loop.

**Zasada:** Extract Method przenosi spójny fragment do metody, gdy nazwa wyrazi intencję lepiej niż szczegóły, a sama długość nie jest powodem. Kluczowa jest analiza przepływu danych: co wchodzi jako parametr, co wychodzi jako wynik. Gdy fragment ma kilka wyjść, dzielimy go albo tworzymy obiekt wyniku, a nie zwracamy tablicy czy mutowalnego holdera.

**Efekt:** Metoda publiczna czyta się jak spis treści, a obliczenia są oddzielone od renderowania, bez zmiany sygnatury ani formatu. Kosztem są dwa przejścia po liście rzędów, pomijalne wobec czytelności.

### Co widzimy

`TicketSummary.describe` liczy cenę bazową, sumę i liczbę miejsc VIP, a na końcu składa dokument. Komentarze `// cena bazowa`, `// suma i liczba miejsc VIP`, `// dokument` to naturalne granice metod. Pętla ma jednak **dwa wyjścia**: `subtotal` i `vipSeats`.

```java
BigDecimal subtotal = BigDecimal.ZERO;
int vipSeats = 0;
for (int row : order.rows()) {
    BigDecimal price = base;
    if (row >= 10) {
        price = price.add(new BigDecimal("10.00"));
        vipSeats++;
    }
    subtotal = subtotal.add(price);
}
```

### Krok 1: Extract Method dla bloku z jednym wyjściem

**W IDE:** zaznacz blok pod `// cena bazowa` (od `BigDecimal base;` do końca `if` z porankiem), ⌥⌘M, nazwa `basePrice`. IntelliJ sam wykryje jedno wejście (`order`) i jedno wyjście (`base`). Usuń zbędny komentarz.
**Po:**

```java
BigDecimal base = basePrice(order);
```

**Uruchom:** `scripts/warsztat.sh test m5/s25` - 16 testów zielonych.
**Co powiedzieć:** blok z jednym wyjściem to najprostszy przypadek, IDE robi to bezpiecznie. Komentarz stał się nazwą metody.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s25 0 1`

### Krok 2: Split Loop, potem dwa razy Extract Method

**W IDE:** spróbuj ⌥⌘M na pętli. IntelliJ zgłosi, że fragment ma wiele wartości wyjściowych. To jest moment dydaktyczny. Ręcznie skopiuj pętlę (Split Loop) i z pierwszej kopii usuń `vipSeats`, a z drugiej `subtotal`. Uruchom test. Następnie na każdej pętli ⌥⌘M, nazwy `subtotal` i `vipSeats`.
**Po:**

```java
BigDecimal subtotal = subtotal(order, base);
int vipSeats = vipSeats(order);
```

**Uruchom:** test po Split Loop i po każdej ekstrakcji.
**Co powiedzieć:** zamiast zwracać parę (record z dwoma polami) rozdzielamy odpowiedzialności. Dwa przejścia po liście to koszt pomijalny wobec czytelności. Jeśli wydajność miałaby znaczenie, mierzymy, a nie zgadujemy.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s25 1 2`

### Krok 3: Extract Method dla renderowania

**W IDE:** zaznacz blok `// dokument` i wykonaj ⌥⌘M, nazwa `render`. Parametry: `order`, `subtotal`, `vipSeats`. Potem Inline Variable (⌥⌘N) dla `base`.
**Po:**

```java
public String describe(Order order) {
    BigDecimal subtotal = subtotal(order, basePrice(order));
    int vipSeats = vipSeats(order);
    return render(order, subtotal, vipSeats);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** metoda publiczna czyta się jak spis treści. Obliczenia są oddzielone od prezentacji, więc przygotowaliśmy grunt pod Extract Class.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s25 2 3`

### Rozwiązanie i uzasadnienie

`step3/TicketSummary`: cztery prywatne metody, każda z jednym wyjściem. Nie zmieniliśmy sygnatury publicznej ani formatu dokumentu, co potwierdza test równoważności na czterech przypadkach (w tym zamówienie puste i same miejsca VIP).

### Pułapki

- Ekstrakcja bloku, który modyfikuje zmienną lokalną używaną dalej, bez zwrócenia jej wartości. IDE tego pilnuje, ręczne kopiowanie nie.
- Zwracanie tablicy `Object[]` albo mutowalnego "holdera" tylko po to, żeby wyciągnąć dwa wyniki.
- Nazwa metody opisująca "jak" (`loopRows`) zamiast "co" (`vipSeats`).

### Pytanie do sali

Kiedy zamiast Split Loop lepiej zwrócić record z dwoma polami?

## Scena s26. Strategy, Command i State w jednej klasie - pulpit kasjera

**Temat ze slajdów:** 6.1. Strategia, Polecenie czy Stan? Kto wybiera wariant i kiedy; 6.7. Trzy osie w jednej klasie: każda dostaje własny mechanizm; 6.8. Typowe pomyłki przy wyborze
**Pakiet:** `pl.training.workshop.m5.s26_variability` · **Test:** `scripts/warsztat.sh test m5/s26`
**Czas:** ~15 min

### W skrócie

**Co robimy:** `BoxOfficeDesk.handle` obsługuje polecenia kasjera BOOK, PAY, USE, CANCEL, UNDO i REPORT jednym `switch` po nazwie operacji, w którym siedzi `switch` po statusie rezerwacji, a w rezerwacji `switch` po typie biletu. Rozplątujemy trzy osie po kolei: cenę biletu jako Strategy (`TicketPricing`), operacje kasjera jako Command z cofaniem (`DeskCommand` i odbiorca `BookingLedger`), cykl życia rezerwacji jako State (`Booking` z prywatnym enumem `State`).

**Zasada:** O mechanizmie decyduje pytanie "kto wybiera wariant i kiedy?", a nie wygląd kodu - wszystkie trzy `switch` wyglądają tak samo. Strategy: wariant wybiera klient na podstawie danych (typ biletu), raz. Command: żądanie z zewnątrz, które trzeba zapisać w dzienniku i umieć cofnąć. State: wariant zmienia sam obiekt w wyniku własnych operacji (CANCEL w NEW zwalnia rezerwację, w PAID oddaje pieniądze). Wprowadzamy jedną oś naraz.

**Efekt:** Nowy typ biletu, nowa operacja i nowy status to zmiany w trzech rozłącznych miejscach. Odpowiedzi pulpitu, stan kasy i dziennik są identyczne w start i w każdym kroku, łącznie z komunikatami błędów, kolejnością walidacji i tym, że nieudane polecenie nie trafia do dziennika.

### Co widzimy

`BoxOfficeDesk` trzyma statusy i kwoty rezerwacji w mapach, kasę (`till`) i dziennik rekordów `Entry`. Ta sama operacja CANCEL znaczy co innego w zależności od statusu, a cofanie (UNDO) to drugi `switch` po nazwie operacji zapisanej w dzienniku. Wspólny kontrakt sceny to enum `Status` (NEW, PAID, USED, CANCELLED) - wartość zapisywana w bazie.

```java
Status next = switch (status) {
    case NEW -> switch (operation) {
        case "PAY" -> { till = till.plus(amount); moved = amount; yield Status.PAID; }
        case "CANCEL" -> Status.CANCELLED;
        default -> null;
    };
    case PAID -> switch (operation) {
        case "USE" -> Status.USED;
        case "CANCEL" -> { till = till.minus(amount); moved = amount; yield Status.CANCELLED; }
        default -> null;
    };
    case USED, CANCELLED -> null;
};
```

`S26EquivalenceTest` to sesje kasjera przechodzące przez wszystkie osie naraz: ceny typów N/S/E/C, cykl życia (CANCEL w NEW i w PAID), niedozwolone przejścia, cofanie w odwrotnej kolejności, cofnięcie USE i ponowny CANCEL, błędy, które nie trafiają do dziennika. Przed pierwszym ruchem pokaż na rzutniku wiersz `"BOOK B1 4DX N"`: najpierw walidacja formatu, potem typów biletów, dopiero na końcu "booking exists" - kolejność walidacji też jest kontraktem.

### Krok 1: Strategy dla ceny biletu

**W IDE:** utwórz interfejs funkcyjny `TicketPricing` z metodą `Money price(Money formatBase)`. Utwórz `TicketPricings` z mapą kod typu - strategia (lambdy, wspólna metoda `discounted(percent)`) i `forType(code)`, które dla nieznanego kodu rzuca `unknown ticket type: X` jak start. W pętli `book` zastąp wewnętrzny `switch` wywołaniem strategii.
**Po:**

```java
for (int i = 3; i < args.length; i++) {
    total = total.plus(TicketPricings.forType(args[i]).price(base));
}
```

**Uruchom:** `scripts/warsztat.sh test m5/s26` - zielone.
**Co powiedzieć:** cenę wybiera klient na podstawie danych wejściowych, bez historii i bez stanu - to Strategy. Strategia zna tylko cenę bazową formatu, nic o rezerwacji ani o operacji kasjera. Strategie są bezstanowe, więc `forType("S")` zwraca zawsze tę samą instancję.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s26 0 1`

### Krok 2: Command dla operacji kasjera

**W IDE:** Refactor > Extract Delegate dla pól `statuses`, `amounts`, `till` - powstaje odbiorca `BookingLedger` z metodami `open`, `remove`, `status`, `pay`, `use`, `cancel`, `restore`, `deposit`, `withdraw`, `summary`. Utwórz interfejs `DeskCommand` (`execute(ledger)`, `undo(ledger)`, `journalEntry()`) i po jednej klasie na operację: `BookCommand`, `PayCommand`, `UseCommand`, `CancelCommand`, każda z fabryką `from(args)`. Pulpit dostaje rejestr `Map<String, Function<String[], DeskCommand>>` i historię `List<DeskCommand>` zamiast rekordów `Entry`; `undo()` woła `last.undo(ledger)`.
**Po:**

```java
DeskCommand command = factory.apply(args);
String result = command.execute(ledger);
history.add(command);
return result;
```

**Uruchom:** test zielony.
**Co powiedzieć:** wykonanie i cofnięcie są obok siebie w jednej klasie, a nie w dwóch `switch`. Polecenie pamięta tylko to, czego potrzebuje do cofnięcia (poprzedni status, przesuniętą kwotę) - to stan **tego** żądania, dlatego każde wywołanie tworzy nowy obiekt przez `from(args)`, a komenda nie jest współdzieloną stałą jak w s15. Do historii trafia dopiero polecenie wykonane bez wyjątku, jak w start. UNDO i REPORT zostały w `switch` pulpitu, bo nie są operacjami na rezerwacji. Cykl życia nadal siedzi w warunkach `BookingLedger` - to zapach na krok 3.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s26 1 2`

### Krok 3: State dla cyklu życia rezerwacji

**W IDE:** utwórz klasę `Booking` (kwota i pole `private State state`) z prywatnym enumem `State`. Warunki z `BookingLedger.pay/use/cancel` przenieś do stałych enumu: NEW nadpisuje `pay` i `cancel`, PAID nadpisuje `use` i `cancel`, USED i CANCELLED nie nadpisują niczego. Domyślne metody enumu rzucają `cannot <akcja> in <STATUS>`. `BookingLedger` trzyma `Map<String, Booking>` i tylko przesuwa pieniądze w kasie.
**Po:**

```java
NEW {
    Money pay(Booking booking) { booking.state = PAID; return booking.amount; }
    Money cancel(Booking booking) { booking.state = CANCELLED; return Money.ZERO; }
},
PAID {
    void use(Booking booking) { booking.state = USED; }
    Money cancel(Booking booking) { booking.state = CANCELLED; return booking.amount; }
},
USED,
CANCELLED;
```

**Uruchom:** test zielony; `S26SolutionTest` sprawdza, że cena to bezstanowa strategia, każda operacja to polecenie z `undo`, a stan jest prywatnym typem rezerwacji.
**Co powiedzieć:** CANCEL w NEW i w PAID to ta sama operacja z innym zachowaniem - tego nie wyrazi ani Strategy (nikt z zewnątrz nie wybiera), ani Command (polecenie jest to samo). `axesAreNotMixed` pilnuje, że stan nie zna cennika ani nazw operacji, a polecenia nie sięgają do stanu. `restore` celowo omija reguły przejść: cofnięcie to kompensacja, nie przejście biznesowe (USED -> PAID nie jest dozwolone, ale UNDO po USE musi je wykonać).
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s26 2 3`

### Rozwiązanie i uzasadnienie

`step3`: pulpit zna tylko polecenia i ich historię, cena biletu to Strategy wybierana po typie, cykl życia to State w `Booking`. Trzy osie, trzy wzorce, zero mieszania. Publiczny `Status` zostaje kontraktem trwałym, a prywatny `State` jest szczegółem implementacji mapowanym przez nazwę.

### Pułapki

- Jeden wzorzec dla wszystkich osi "bo to wszystko `switch`" - np. Strategy dla statusu: klient musiałby wiedzieć, w jakim stanie jest rezerwacja, i sam podmieniać strategię.
- Polecenie jako współdzielony singleton z polami `previous` i `paid` - drugie PAY nadpisze dane potrzebne do cofnięcia pierwszego.
- Cofanie przez publiczne przejścia (`pay()`, `cancel()`) zamiast `restore` - cofnięcie USE wymaga przejścia, którego reguły zabraniają, a cofnięcie CANCEL po zwrocie musi wpłacić kwotę z powrotem do kasy (`deposit`).
- Kombinatoryczna hierarchia (`PaidCancelCommand`) zamiast złożenia osi.
- Zapis prywatnego `State` (np. `ordinal()`) do bazy zamiast stabilnego `Status`.

### Pytanie do sali

Kierownik chce zwrotu z potrąceniem 10% dla rezerwacji opłaconej, ale tylko w weekend. Która oś się zmienia i w której klasie to dopiszecie?

## Scena s27. Replace Inheritance with Delegation - raporty sprzedaży

**Temat ze slajdów:** 7.2. Gdy punkty zaczepienia obejmują dwie niezależne osie; 7.3. Zastąpienie dziedziczenia delegacją: osie jako strategie; 7.4. Strategia czy Metoda szablonowa?
**Pakiet:** `pl.training.workshop.m5.s27_templatetostrategy` · **Test:** `scripts/warsztat.sh test m5/s27`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `ScreeningReport` to Template Method z pięcioma hakami, ale haki niosą dwie niezależne osie: okres (`includes`, `label`) i format (`header`, `row`, `footer`). Każda kombinacja jest podklasą (`DailyCsvReport`, `DailyTextReport`, `WeeklyCsvReport`, `WeeklyTextReport`), a kod okresu i formatu jest między nimi skopiowany. Wyjmujemy oś formatu do strategii `ReportFormat`, potem oś okresu do `ReportPeriod`, a na końcu zwijamy cztery podklasy w jedną finalną klasę złożoną z dwóch strategii.

**Zasada:** Template Method jest dobry, gdy szkielet jest stabilny, a zmienia się **jeden** krok. Gdy haki grupują się w niezależne osie, dziedziczenie mnoży klasy (N × M), a kompozycja je dodaje (N + M). Replace Inheritance with Delegation robimy oś po osi: pogrupuj haki, Extract Interface dla grupy, delegat w konstruktorze, przeniesienie implementacji, usunięcie podklas po migracji klientów.

**Efekt:** Dwa okresy i dwa formaty to cztery klasy strategii, a trzeci format to jedna klasa zamiast dwóch podklas. Kolejność kroków raportu nadal pilnuje jedna metoda `render` - `final` przechodzi z metody na klasę. Wynik każdego raportu i komunikaty błędów `Reports.of` są bez zmian.

### Co widzimy

`Reports.of(period, format)` (stabilny punkt wejścia) wybiera zagnieżdżonym `switch` jedną z czterech podklas - iloczyn osi zapisany w kodzie. Szkielet w bazie jest poprawny, ale haki mieszają dwie przyczyny zmiany:

```java
public final String render(LocalDate day, List<ScreeningSale> sales) {
    ...
        if (includes(day, sale.day())) {                    // hak okresu
    ...
    StringBuilder text = new StringBuilder(header(label(day)));   // format(okres)
    ...
        text.append(row(entry.getKey(), ...));              // hak formatu
    return text.append(footer(total.tickets(), total.revenue())).toString();
}
```

`S27EquivalenceTest` sprawdza każdą kombinację: dzień CSV (z escapowaniem średnika w tytule), dzień tekst, tydzień CSV ze środy (sumy po tytule), tydzień tekst z niedzieli, następny tydzień od poniedziałku, dzień bez sprzedaży, nieznany okres, nieznany format i przypadek "najpierw okres, potem format".

### Krok 1: oś formatu jako strategia

**W IDE:** utwórz interfejs `ReportFormat` z `header`, `row`, `footer` oraz klasy `CsvFormat` i `TextFormat` z ciałami skopiowanymi z `DailyCsvReport` i `DailyTextReport` (najpierw porównaj je z kopiami w `Weekly*`, ⌘D na dwóch plikach). Do `ScreeningReport` dodaj pole `format` i konstruktor `protected ScreeningReport(ReportFormat format)`, w `render` wołaj `format.header(...)`, `format.row(...)`, `format.footer(...)`. Usuń trzy haki formatu z bazy i podklas (Safe Delete ⌘⌦), podklasy wołają `super(new CsvFormat())` albo `super(new TextFormat())`.
**Po:**

```java
public DailyCsvReport() {
    super(new CsvFormat());
}
```

**Uruchom:** `scripts/warsztat.sh test m5/s27` - zielone.
**Co powiedzieć:** najpierw grupujemy haki według osi, dopiero potem cokolwiek przenosimy. Hak wołamy teraz na delegacie, nie na `this` - strategia nie widzi stanu `protected` bazy i wszystko dostaje parametrami. `Reports` jest bez zmian, klienci niczego nie zauważyli.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s27 0 1`

### Krok 2: oś okresu jako strategia

**W IDE:** ten sam ruch dla `includes` i `label`: interfejs `ReportPeriod`, klasy `DailyPeriod` i `WeeklyPeriod` (powtórzone wyliczanie poniedziałku wydziel ⌥⌘M do `monday(day)`), konstruktor bazy `ScreeningReport(ReportPeriod period, ReportFormat format)`.
**Po:**

```java
public WeeklyTextReport() {
    super(new WeeklyPeriod(), new TextFormat());
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** baza nie ma już żadnego haka, a podklasy są tylko konfiguracją konstruktora - kandydatami do Inline Class. `render` jest wciąż `final` (`beforeTheCollapseTheSkeletonWasStillAProtectedTemplateMethod`), więc kolejność kroków jest chroniona tak jak w Template Method.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s27 1 2`

### Krok 3: zwinięcie podklas w jedną finalną klasę

**W IDE:** w `ScreeningReport` usuń `abstract`, oznacz klasę `final`, konstruktor zrób publiczny, z `render` zdejmij zbędne już `final`. `Reports.of` składa `new ScreeningReport(period, format)` z dwóch map `PERIODS` i `FORMATS`, sprawdzając najpierw okres, potem format. Safe Delete (⌘⌦) czterech podklas.
**Po:**

```java
ReportPeriod chosenPeriod = PERIODS.get(period);
if (chosenPeriod == null) {
    throw new IllegalArgumentException("unknown period: " + period);
}
ReportFormat chosenFormat = FORMATS.get(format);
if (chosenFormat == null) {
    throw new IllegalArgumentException("unknown format: " + format);
}
return new ScreeningReport(chosenPeriod, chosenFormat);
```

**Uruchom:** test zielony; `S27SolutionTest.newFormatWorksWithEveryPeriodWithoutNewClasses` dodaje format Markdown jedną klasą testową i od razu używa go z oboma okresami.
**Co powiedzieć:** N + M zamiast N × M; nowa kombinacja to nowe złożenie, nie nowa klasa. Przypadek "najpierw okres, potem format" (MONTHLY i PDF daje `unknown period`) pilnuje, że zamiana zagnieżdżonego `switch` na dwie mapy nie zmieniła kolejności walidacji.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s27 2 3`

### Rozwiązanie i uzasadnienie

`step3`: jedna finalna klasa `ScreeningReport` złożona z `ReportPeriod` i `ReportFormat`, cztery małe strategie i fabryka `Reports` wybierająca osie niezależnie. Template Method był dobry, dopóki zmieniała się jedna oś; przy dwóch niezależnych osiach kompozycja pozwala też wybrać kombinację w czasie działania.

### Pułapki

- Zwinięcie podklas przed wyjęciem obu osi - zostaje klasa z warunkiem `if (weekly)` zamiast strategii.
- Znika typ podklasy: kod z `instanceof WeeklyCsvReport`, rejestracja w DI po typie albo zapisana nazwa klasy przestają działać - poszukaj takich użyć przed Safe Delete.
- Osie, które tylko wyglądają na niezależne: gdyby format tekstowy inaczej opisywał tydzień niż dzień, format zależałby od okresu i podział 2 + 2 byłby fałszywy.
- Kopie implementacji w podklasach mogły się już rozjechać (np. escapowanie tylko w jednym CSV) - porównaj je, zanim zostawisz jedną.

### Pytanie do sali

Kiedy zostawilibyście Template Method mimo dwóch osi w hakach? Co musiałoby być prawdą o tempie zmian każdej z nich?

## Scena s28. Replace Conditional with Chain of Responsibility - zatwierdzanie zwrotów

**Temat ze slajdów:** 8.3. Łańcuch `if/else`, w którym kolejność jest regułą; 8.4. Łańcuch zobowiązań: ogniwo i złożenie łańcucha; 8.5. Łańcuch zobowiązań czy Dekorator?
**Pakiet:** `pl.training.workshop.m5.s28_chain` · **Test:** `scripts/warsztat.sh test m5/s28`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `RefundApprover.decide` rozstrzyga łańcuchem `if/else`, kto zatwierdza zwrot: po rozpoczęciu seansu odmowa, co najmniej 24 h przed seansem zwrot automatyczny, do 100.00 kasjer, do 1000.00 kierownik, wyżej brak uprawnionego. Wyjmujemy pierwszą gałąź jako ogniwo `RefundHandler`, potem wszystkie gałęzie jako ogniwa w liście, a na końcu budujemy jawny łańcuch z następnikiem w konstruktorze i ogniwem końcowym `NoApproverHandler`.

**Zasada:** Chain of Responsibility pasuje do uporządkowanych reguł, z których obsługuje jedna. Ogniwa nie są niezależne: zwrot automatyczny nie ma limitu kwoty tylko dlatego, że stoi przed kasjerem - kolejność jest regułą biznesową i ma być widoczna w jednym miejscu. Koniec łańcucha to decyzja, nie `null`. Kształt przypomina Decorator, ale intencja jest inna: dekorator zawsze woła wnętrze, ogniwo tylko wtedy, gdy samo nie obsłuży żądania.

**Efekt:** Kolejność poziomów widać w jednym wyrażeniu, a inną ścieżkę (np. festiwal bez zwrotów automatycznych) składa się bez edycji `decide`. Walidacja kwoty zostaje przed łańcuchem, bo to warunek wstępny, a nie poziom zatwierdzania. Decyzje dla wszystkich granic progów są identyczne jak w start.

### Co widzimy

```java
if (!request.requestedAt().isBefore(request.screeningStart())) {
    return RefundDecision.rejected("seans juz sie rozpoczal");
} else if (Duration.between(request.requestedAt(), request.screeningStart()).toHours() >= 24) {
    return RefundDecision.approvedBy("AUTO");
} else if (!request.amount().isGreaterThan(CASHIER_LIMIT)) {
    return RefundDecision.approvedBy("CASHIER");
} else if (!request.amount().isGreaterThan(MANAGER_LIMIT)) {
    return RefundDecision.approvedBy("MANAGER");
}
return RefundDecision.rejected("brak uprawnionego zatwierdzajacego");
```

Zmiana kolejności, nowy poziom albo inna ścieżka dla festiwalu oznacza edycję tej jednej metody. `S28EquivalenceTest` to tabela na granicach progów: dokładnie 24 h przed (AUTO) i 23 h 59 min (kasjer), 100.00 i 100.01, 1000.00 i 1000.01, 5000.00 trzy dni przed seansem (AUTO bez limitu - kolejność reguł), chwila rozpoczęcia seansu, kwota zero i kwota ujemna po seansie (walidacja przed łańcuchem).

### Krok 1: Extract Interface i pierwsze ogniwo

**W IDE:** utwórz interfejs funkcyjny `RefundHandler` z metodą `Optional<RefundDecision> handle(RefundRequest request)` - pusty wynik znaczy "pytaj dalej". Pierwszą gałąź wydziel (⌥⌘M) i przenieś (F6) do klasy `AfterScreeningHandler`. W `decide` zostaje reszta `if/else`.
**Po:**

```java
Optional<RefundDecision> decision = afterScreening.handle(request);
if (decision.isPresent()) {
    return decision.get();
} else if (Duration.between(request.requestedAt(), request.screeningStart()).toHours() >= 24) {
    return RefundDecision.approvedBy("AUTO");
```

**Uruchom:** `scripts/warsztat.sh test m5/s28` - zielone.
**Co powiedzieć:** zaczynamy od pierwszej gałęzi, bo wszystkie pozostałe zakładają, że ona odmówiła. `Optional` to przejściowy kontrakt "obsłużyłem albo nie" - jeszcze bez następnika.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s28 0 1`

### Krok 2: wszystkie gałęzie jako ogniwa w liście

**W IDE:** ten sam ruch dla `AutoRefundHandler`, `CashierHandler` i `ManagerHandler`; limity trafiają do konstruktorów (Introduce Parameter ⌥⌘P). `decide` przegląda `List.of(...)` po kolei i zwraca pierwszą decyzję, a po liście - odmowę "brak uprawnionego zatwierdzajacego".
**Po:**

```java
for (RefundHandler handler : handlers) {
    Optional<RefundDecision> decision = handler.handle(request);
    if (decision.isPresent()) {
        return decision.get();
    }
}
return RefundDecision.rejected("brak uprawnionego zatwierdzajacego");
```

**Uruchom:** test zielony.
**Co powiedzieć:** kolejność reguł ma nazwy i jest w jednym miejscu - to kolejność elementów listy. Lista bywa wystarczająca i prostsza niż klasyczny łańcuch; jeśli nikt nie musi składać innych ścieżek, można się tu zatrzymać.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s28 1 2`

### Krok 3: jawny łańcuch z następnikiem

**W IDE:** Change Signature (⌘F6) na `RefundHandler.handle`: zwraca `RefundDecision` zamiast `Optional`. Każde ogniwo dostaje `next` w konstruktorze i woła `next.handle(request)`, gdy nie obsłuży żądania. Odmowę z końca listy przenieś do `NoApproverHandler`. `RefundApprover` dostaje dwa konstruktory: domyślny składa standardową ścieżkę, drugi przyjmuje dowolną głowę łańcucha.
**Po:**

```java
public RefundApprover() {
    this(new AfterScreeningHandler(
            new AutoRefundHandler(
                    new CashierHandler(CASHIER_LIMIT,
                            new ManagerHandler(MANAGER_LIMIT,
                                    new NoApproverHandler())))));
}
```

**Uruchom:** test zielony; `S28SolutionTest` składa ścieżkę festiwalową bez zwrotów automatycznych i sprawdza, że ogniwo, które zdecydowało, nie woła następnika.
**Co powiedzieć:** koniec łańcucha jest ogniwem, więc żądanie nie może "spaść" bez odpowiedzi. Kontrast z Decoratorem: dekorator zawsze woła wnętrze i dokłada coś do wyniku, a tu odpowiedź pochodzi od dokładnie jednego ogniwa. Pytanie kontrolne: czy po obsłużeniu ktoś dalej musi pracować? Audyt decyzji byłby więc Decoratorem wokół `RefundApprover`, a nie kolejnym ogniwem.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s28 2 3`

### Rozwiązanie i uzasadnienie

`step3`: pięć ogniw, każde zna tylko swój warunek i następnika, kolejność złożona w jednym konstruktorze, walidacja kwoty przed łańcuchem. Nowy poziom (np. dyrektor do 5000.00) to nowe ogniwo i jedna linia w złożeniu.

### Pułapki

- Przestawienie ogniw "dla porządku" (kasjer przed AUTO) - 5000.00 trzy dni przed seansem przestaje być zwrotem automatycznym; wychwyci to przypadek "AUTO nie ma limitu kwoty".
- Ogniwa z mutowalnym `next` (setter `then(...)`) współdzielone między dwoma łańcuchami - drugie złożenie zmienia pierwsze. Tu `next` jest `final`.
- Walidacja kwoty jako pierwsze ogniwo - łańcuch podany z zewnątrz mógłby ją pominąć.
- Granica `toHours() >= 24` obcina minuty; przepisanie na porównanie `Duration` łatwo zmienia wynik dla 23 h 59 min.

### Pytanie do sali

Biznes chce logować każdą decyzję o zwrocie razem z tym, kto ją podjął. Ogniwo czy dekorator - i dlaczego?

## Scena s29. Introduce Boundary Object - odpowiedź terminala płatniczego

**Temat ze slajdów:** 9.3. Surowe dane z zewnątrz w kodzie domeny; 9.4. Obiekt graniczny: sprawdzone dane na granicy systemu; 9.5. Adapter czy obiekt graniczny?
**Pakiet:** `pl.training.workshop.m5.s29_boundaryobject` · **Test:** `scripts/warsztat.sh test m5/s29`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `PaymentService` dostaje od starego terminala tekst `OK;AUTH-R1;4500` (status, kod, kwota w groszach) i rozbiera go w każdej metodzie osobno: `split`, `startsWith`, `lastIndexOf`. Wprowadzamy niezmienny obiekt graniczny `PaymentConfirmation`, wydzielamy mapper `TerminalResponseMapper`, a na końcu chowamy klienta terminala za portem `PaymentTerminal` z adapterem `LegacyTerminalAdapter`.

**Zasada:** Boundary Object to zwalidowany obraz danych z zewnątrz w języku domeny: jeśli obiekt istnieje, jest poprawny, więc domena niczego nie parsuje ani nie zgaduje. Adapter tłumaczy wywołanie (jak zawołać obcy system), Boundary Object - dane (co wpuszczamy do domeny); zwykle występują razem. Walidacja na granicy zmienia typ i moment błędu dla danych błędnych - to decyzja do zatwierdzenia, nie skutek uboczny.

**Efekt:** Format terminala zna tylko mapper, a serwis zależy wyłącznie od portu i widzi `PaymentConfirmation`. Dla poprawnych odpowiedzi wynik jest identyczny. Świadomie zmienia się zachowanie dla odpowiedzi zniekształconych: są odrzucane na granicy przez `IllegalArgumentException("malformed terminal response: ...")`, zamiast wybuchać trzy metody dalej albo drukować pusty kod na paragonie.

### Co widzimy

`LegacyTerminalClient` (nie zmieniamy go) przyjmuje grosze i odpowiada tekstem: kwota <= 0 daje `ERROR;AMOUNT;0`, powyżej 1000.00 `DECLINED;51;0`, powyżej 300.00 autoryzację częściową do 300.00. Serwis trzyma surowe odpowiedzi w mapie i każda metoda sama je rozbiera:

```java
String raw = terminal.authorize(bookingId, grosze);
responses.put(bookingId, raw);
String[] parts = raw.split(";");
if (!parts[0].equals("OK")) {
    return parts[0] + " " + parts[1];
}
long authorized = Long.parseLong(parts[2]);
if (authorized < grosze) {
    return "PARTIAL " + parts[1] + " brakuje " + BigDecimal.valueOf(grosze - authorized).movePointLeft(2);
}
```

Punkt wejścia testu to korzeń kompozycji `PaymentModule.paymentService(terminal)`. `S29EquivalenceTest` gra sesje płatności: pełna autoryzacja, częściowa, granica 300.00 i grosz ponad, odmowa i błąd kwoty, brak płatności, ponowna płatność nadpisująca poprzednią - za każdym razem z potwierdzeniem i sumą autoryzacji.

### Krok 1: Introduce Boundary Object

**W IDE:** utwórz rekord `PaymentConfirmation(Outcome outcome, String code, Money authorized)` z enumem `Outcome { APPROVED, DECLINED, ERROR }` i walidacją w konstruktorze kompaktowym: kod niepusty, kwota nieujemna, tylko APPROVED może autoryzować pieniądze. W `pay` rozbierz odpowiedź raz, zaraz po wywołaniu terminala (⌥⌘M `parse(raw)`); mapa przechowuje `PaymentConfirmation`, a `receipt` i `totalAuthorized` czytają pola obiektu.
**Po:**

```java
PaymentConfirmation confirmation = parse(terminal.authorize(bookingId, grosze));
confirmations.put(bookingId, confirmation);
if (!confirmation.approved()) {
    return confirmation.outcome() + " " + confirmation.code();
}
if (confirmation.isPartialFor(amount)) {
    return "PARTIAL " + confirmation.code() + " brakuje " + amount.minus(confirmation.authorized());
}
```

**Uruchom:** `scripts/warsztat.sh test m5/s29` - zielone.
**Co powiedzieć:** zamiast trzech parserów jest jeden, w miejscu wejścia danych. `totalAuthorized` nie sprawdza już `startsWith("OK")` i po prostu sumuje - niezmiennik obiektu gwarantuje zero dla odmowy. `parse` sprawdza liczbę pól i znany status: to pierwszy moment, w którym zmienia się zachowanie dla danych błędnych (start przyjąłby `OK;;4500` i wydrukował pusty kod).
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s29 0 1`

### Krok 2: mapper na granicy

**W IDE:** Move Static Member (F6) dla `parse` do nowej klasy `TerminalResponseMapper` jako `toConfirmation(raw)`; przeliczenie złotych na grosze też tam (`toGrosze(amount)`). Serwis tworzy mapper i woła go po obu stronach wywołania terminala.
**Po:**

```java
String raw = terminal.authorize(bookingId, mapper.toGrosze(amount));
PaymentConfirmation confirmation = mapper.toConfirmation(raw);
```

**Uruchom:** test zielony; `S29SolutionTest.malformedResponseIsRejectedAtTheBoundary` sprawdza `OK;AUTH-R1`, `MAYBE;X;0`, `OK;;4500` i `OK;AUTH-R1;abc`.
**Co powiedzieć:** mapper to jedyne miejsce, które zna format `STATUS;KOD;KWOTA` i grosze w obie strony. Serwis nie zna już formatu, ale wciąż sam woła `LegacyTerminalClient` i składa wywołanie z mapperem.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s29 1 2`

### Krok 3: port domeny i adapter

**W IDE:** utwórz interfejs `PaymentTerminal` z `PaymentConfirmation authorize(String bookingId, Money amount)` i klasę `LegacyTerminalAdapter implements PaymentTerminal`, która łączy klienta z mapperem. Change Signature (⌘F6) konstruktora `PaymentService`: `LegacyTerminalClient` na `PaymentTerminal`. W korzeniu kompozycji owiń klienta adapterem.
**Po:**

```java
public static PaymentService paymentService(LegacyTerminalClient terminal) {
    return new PaymentService(new LegacyTerminalAdapter(terminal));
}
```

**Uruchom:** test zielony; `serviceWorksWithAnyTerminalBehindThePort` używa lambdy zamiast terminala, a `domainServiceSeesNeitherTheLegacyClientNorTheMapper` sprawdza sygnatury serwisu refleksją.
**Co powiedzieć:** granica jest kompletna: Adapter (kształt wywołania), mapper (format danych), Boundary Object (poprawna wartość w języku domeny). Serwis da się testować bez terminala i bez tekstowych odpowiedzi.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s29 2 3`

### Rozwiązanie i uzasadnienie

`step3`: `PaymentService` zna tylko `PaymentTerminal` i `PaymentConfirmation`, `LegacyTerminalAdapter` implementuje port, `TerminalResponseMapper` jako jedyny zna grosze i tekst odpowiedzi. Zmiana zachowania dla odpowiedzi zniekształconych jest nazwana i opisana testem.

### Pułapki

- Obiekt graniczny bez walidacji (zwykłe DTO) - domena znowu musi sprawdzać i zgadywać.
- Zmiana typu i momentu błędu bez zapisania decyzji: w start `OK;AUTH-R1` dawało `ArrayIndexOutOfBoundsException` dopiero przy parsowaniu kwoty, a `OK;;4500` - paragon z pustym kodem.
- Adapter, który przepuszcza surowy tekst dalej "dla elastyczności" - port ma mówić językiem domeny.
- Kopiowanie każdego DTO obcego systemu bez potrzeby - osobny model ma sens, gdy granica oddziela różne semantyki lub tempo zmian.
- `longValueExact` przy przeliczaniu na grosze rzuca dla ułamka grosza - to też część kontraktu, nie szczegół do "uproszczenia" przez `longValue`.

### Pytanie do sali

Terminal zaczyna zwracać nowy status `PENDING`. W którym miejscu kodu to zobaczycie i kto powinien zdecydować, co z nim zrobić?

## Scena s30. Extract Facade - zakup biletów w sklepie i w kasie

**Temat ze slajdów:** 11.1. Klienci sami koordynują podsystemy; 11.2. Wyodrębnienie Fasady: jedna operacja zamiast znajomości kroków
**Pakiet:** `pl.training.workshop.m5.s30_facade` · **Test:** `scripts/warsztat.sh test m5/s30`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `OnlineShop.buy` i `BoxOffice.sell` same orkiestrują cztery podsystemy: cennik, rejestr miejsc, bramkę płatności i wysyłkę maili. Kod kasy to kopia sklepu sprzed lat i ma celowo zostawiony błąd legacy: po odmowie płatności nie zwalnia miejsc. Wyjmujemy orkiestrację ze sklepu do `BookingFacade`, przepinamy na nią kasę (to świadomie naprawia błąd), a na końcu ukrywamy podsystemy i domykamy niezmiennik blokiem `finally`.

**Zasada:** Facade daje klientom jedną spójną operację zamiast znajomości kroków i ich kolejności. Procedura: test efektów obu klientów, Extract Method orkiestracji, Move Method do fasady, przepięcie drugiego klienta, zawężenie widoczności podsystemów. Gdy kopie orkiestracji już się rozjechały, przepięcie klienta zmienia jego zachowanie - to naprawa błędu, którą dokumentuje się osobnym testem, a nie "czysta" refaktoryzacja.

**Efekt:** Kanały sprzedaży znają tylko fasadę i własną opłatę (sklep 2.00 za miejsce, kasa zero), a kolejność cena - rezerwacja - płatność z kompensacją - mail jest w jednym miejscu. Zachowanie świadomie zmienia się dwa razy: w kroku 2 kasa zaczyna zwalniać miejsca po odmowie płatności, w kroku 3 miejsca są zwalniane także wtedy, gdy bramka rzuci wyjątkiem.

### Co widzimy

Dwie metody wyglądają prawie tak samo. Pokaż je obok siebie na rzutniku i poproś salę o znalezienie różnic, zanim cokolwiek wydzielisz:

```java
// BoxOffice.sell - kopia OnlineShop.buy bez opłaty rezerwacyjnej
Money total = prices.price(order.ticketType()).times(order.seats().size());
try {
    seats.reserve(order.screening(), order.seats());
} catch (IllegalStateException exception) {
    return "ERROR " + exception.getMessage();
}
if (!payments.charge(order.card(), total)) {
    return "ERROR payment declined";          // sklep w tym miejscu woła seats.release(...)
}
```

`Cinema` to korzeń kompozycji i stabilny punkt wejścia: składa podsystemy i oba kanały, a `state()` pokazuje miejsca, dziennik płatności i wysłane maile naraz. `S30EquivalenceTest` gra sesje, w których zachowanie ma zostać bez zmian (zakup w sklepie z opłatą, w kasie bez opłaty, miejsce zajęte przez drugi kanał, odmowa w sklepie, nieznany typ biletu przed rezerwacją, różne seanse). Odmowa w kasie celowo jest w osobnym `S30BehaviourChangeTest`.

### Krok 1: Extract Facade ze sklepu

**W IDE:** w `OnlineShop.buy` zaznacz całą orkiestrację, ⌥⌘M `book(order)`, potem Introduce Parameter (⌥⌘P) dla opłaty: `book(order, feePerSeat)`. Move Method (F6) do nowej klasy `BookingFacade` z czterema podsystemami w konstruktorze. Sklep dostaje fasadę, a `Cinema` ją tworzy.
**Po:**

```java
public String buy(TicketOrder order) {
    return booking.book(order, BOOKING_FEE);
}
```

**Uruchom:** `scripts/warsztat.sh test m5/s30` - zielone; `step1StillLeaksBecauseOnlyTheShopUsesTheFacade` pokazuje, że błąd kasy wciąż żyje.
**Co powiedzieć:** zaczynamy od klienta, którego orkiestracja jest poprawna (kompensuje odmowę płatności). To, czym różnią się kanały, przychodzi parametrem, a nie flagą `if (online)`.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s30 0 1`

### Krok 2: kasa na fasadzie - świadoma zmiana zachowania

**W IDE:** Change Signature (⌘F6) konstruktora `BoxOffice`: cztery podsystemy na `BookingFacade`. `sell` woła `booking.book(order, Money.ZERO)`, skopiowaną orkiestrację usuń. Popraw `Cinema`.
**Po:**

```java
public String sell(TicketOrder order) {
    return booking.book(order, Money.ZERO);
}
```

**Uruchom:** test równoważności zielony, a `S30BehaviourChangeTest.step2FacadeReleasesSeatsForBothChannels` pokazuje zmianę: po odmowie w kasie miejsce A1 jest wolne i sklep może je sprzedać (`OK 27.00` zamiast `ERROR seat taken A1`).
**Co powiedzieć:** usunięcie kopii usuwa także jej błąd, ale to zmiana zachowania kasy. Nazywamy ją, opisujemy testem i zatwierdzamy osobno (osobny commit "kasa zwalnia miejsca po odmowie płatności"). Test `startBoxOfficeLeaksSeatsAfterDeclinedPayment` po `jump` do kroku 2 lub 3 jest pomijany - start kasy nie ma już własnej orkiestracji.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s30 1 2`

### Krok 3: ukryte podsystemy i niezmiennik w finally

**W IDE:** usuń `public` z `SeatRegistry`, `PriceList`, `PaymentClient`, `TicketMailer`, ich metod i z konstruktora fasady (⌥⏎ na modyfikatorze albo Change Signature ⌘F6 z widocznością). W `book` otocz `charge` blokiem `try/finally`, który zwalnia miejsca, gdy płatność się nie udała.
**Po:**

```java
boolean paid = false;
try {
    paid = payments.charge(order.card(), total);
} finally {
    if (!paid) {
        seats.release(order.screening(), order.seats());
    }
}
```

**Uruchom:** test zielony; `S30SolutionTest` sprawdza, że podsystemy są niewidoczne poza pakietem, a kanały zależą tylko od fasady.
**Co powiedzieć:** zawężenie widoczności to zmiana API dla klientów spoza pakietu - robimy je po migracji, jako osobny krok. `finally` to druga zmiana zachowania: karta `TIMEOUT` (wyjątek bramki) w kroku 2 zostawiała miejsce zajęte (`beforeStep3AGatewayFailureLeakedSeats`), teraz nie (`gatewayFailureNoLongerLeavesSeatsTaken`). Fasada pilnuje niezmiennika "miejsce zajęte tylko po udanej płatności", ale nie jest transakcją: błąd wysyłki maila po płatności nadal niczego nie cofa.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s30 2 3`

### Rozwiązanie i uzasadnienie

`step3`: `BookingFacade` to jedyne wejście do podsystemu i strażnik spójności, podsystemy są pakietowo-prywatne, a `Cinema` jest jedynym miejscem poza fasadą, które je widzi. Obie zmiany zachowania (zwolnienie miejsc po odmowie w kasie i po awarii bramki) są nazwane i mają własne testy.

### Pułapki

- Przepięcie drugiego klienta jako "czysta refaktoryzacja" bez testu zmiany - ktoś mógł polegać na starym zachowaniu (np. raport zajętości kasy).
- Fasada jako kopia całego API podsystemów (God Class) zamiast jednej spójnej operacji.
- Parametr `boolean online` zamiast opłaty - fasada zaczyna znać kanały sprzedaży.
- Ukrycie podsystemów przed migracją wszystkich klientów, także testów i konfiguracji frameworka.
- Fasada przekazująca 1:1 bez żadnej reguły to Middle Man (scena s22).

### Pytanie do sali

Kasa chce sprzedawać bilety bez maila, gdy klient nie poda adresu. Parametr fasady, druga operacja fasady czy osobny klient podsystemów?

## Scena s31. Anti-Corruption Layer - licencje dystrybutora

**Temat ze slajdów:** 11.3. Obcy model w regułach domeny; 11.4. Warstwa antykorupcyjna: port, tłumacz i model domenowy; 11.5. Fasada a warstwa antykorupcyjna
**Pakiet:** `pl.training.workshop.m5.s31_anticorruptionlayer` · **Test:** `scripts/warsztat.sh test m5/s31`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `SettlementService` rozlicza się z dystrybutorem i decyduje, czy wolno grać film, myśląc w modelu dystrybutora: status `A`/`S`, daty `yyyyMMdd`, minimalna gwarancja w groszach jako tekst, udział `"45%"` albo `" 40 %"`, tytuł wielkimi literami. Wprowadzamy rekord domenowy `FilmLicense`, wydzielamy tłumacza `DistributorTranslator`, a na końcu port `FilmLicenses` z implementacją `DistributorAcl`.

**Zasada:** Anti-Corruption Layer chroni język domeny przed obcym modelem: port mówi językiem kina, a tłumacz jako jedyny zna cudze konwencje. Fasada upraszcza dostęp do podsystemu, ale mówi jego językiem; ACL tłumaczy jeden model na drugi i często składa się z fasady, adaptera i tłumacza naraz. Tłumaczenie zachłanne zmienia zachowanie dla danych błędnych - polityka wobec złego rekordu to decyzja biznesowa.

**Efekt:** Reguły (czy licencja obejmuje dzień, ile się należy) są metodami `FilmLicense` i testuje się je bez typów dystrybutora, a `null` z obcego systemu nie wychodzi poza ACL. Dla poprawnych rekordów rozliczenia są identyczne. Walidacja jest świadomie zaostrzona: zła data to `IllegalArgumentException("invalid legacy date: ...")`, a niespójna licencja (koniec przed początkiem, udział spoza 0-100) w ogóle nie powstanie.

### Co widzimy

```java
if (!record.statusCode().equals("A")) {
    return "LICENCJA ZAWIESZONA " + filmCode;
}
if (!canScreen(filmCode, day)) {
    return "LICENCJA NIEWAZNA " + filmCode + " w dniu " + day;
}
int percent = Integer.parseInt(record.share().replace("%", "").strip());
Money minimum = new Money(new BigDecimal(record.minGuaranteeCents()).movePointLeft(2));
Money due = revenue.percent(percent).max(minimum);
String title = record.titleUpper().charAt(0) + record.titleUpper().substring(1).toLowerCase(Locale.ROOT);
```

Reguła "wolno grać, gdy licencja jest aktywna i obejmuje dzień" jest zapisana jako "kod `A` i dwie daty sparsowane z `BASIC_ISO_DATE`". Każda zmiana formatu u dystrybutora rozlewa się po regułach kina. Punkt wejścia to `SettlementModule.settlementService(feed)`; `DistributorFeed` i `DistributorRecord` należą do obcego systemu i ich nie zmieniamy. `S31EquivalenceTest` sprawdza udział powyżej minimum, minimalną gwarancję, udział ze spacjami i zaokrąglenie, oba końce okresu licencji (włącznie) i dni tuż poza nimi, licencję zawieszoną i nieznany film - dla `settle` i `canScreen`.

### Krok 1: model domeny i tłumaczenie w jednym miejscu

**W IDE:** utwórz rekord `FilmLicense(filmCode, title, active, validFrom, validTo, sharePercent, minimumGuarantee)` z niezmiennikami w konstruktorze i metodami `covers(day)`, `allowsScreeningOn(day)`, `dueFor(revenue)`. W serwisie zaznacz dekodowanie pól rekordu, ⌥⌘M `toLicense(record)`, a reguły licz na licencji.
**Po:**

```java
FilmLicense license = toLicense(record);
if (!license.active()) {
    return "LICENCJA ZAWIESZONA " + filmCode;
}
if (!license.covers(day)) {
    return "LICENCJA NIEWAZNA " + filmCode + " w dniu " + day;
}
```

**Uruchom:** `scripts/warsztat.sh test m5/s31` - zielone.
**Co powiedzieć:** reguły brzmią teraz jak reguły kina, a nie jak `statusCode().equals("A")`. Zauważ, że tłumaczenie jest zachłanne: rekord zawieszony z zepsutą datą dawał w start po prostu "LICENCJA ZAWIESZONA", a teraz parsowanie dat wybuchnie wcześniej. Dla poprawnych danych z testu wynik jest ten sam - to przesunięcie trzeba nazwać.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s31 0 1`

### Krok 2: tłumacz jako osobna klasa

**W IDE:** Move Static Member (F6) dla `toLicense` do nowej klasy `DistributorTranslator`. Wydziel `title(...)` i `date(...)` (⌥⌘M), kod statusu jako stałą `ACTIVE` (⌥⌘C), a w `date` zamień `DateTimeParseException` na `IllegalArgumentException` z komunikatem granicy.
**Po:**

```java
private static LocalDate date(String legacy) {
    try {
        return LocalDate.parse(legacy, DateTimeFormatter.BASIC_ISO_DATE);
    } catch (DateTimeParseException exception) {
        throw new IllegalArgumentException("invalid legacy date: " + legacy, exception);
    }
}
```

**Uruchom:** test zielony; `S31SolutionTest.aclRejectsMalformedLegacyDataAtTheBoundary` dokumentuje nowy komunikat dla daty `2026-10-01` w formacie ISO zamiast `yyyyMMdd`.
**Co powiedzieć:** zepsuty rekord zgłasza się w języku granicy, z komunikatem, który mówi, co jest nie tak. To zmiana typu wyjątku i trzeba ją zatwierdzić. Serwis nadal jednak sam pobiera `DistributorRecord` i obsługuje jego `null`.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s31 1 2`

### Krok 3: port domenowy i warstwa antykorupcyjna

**W IDE:** utwórz interfejs `FilmLicenses` z `Optional<FilmLicense> find(String filmCode)` i klasę `DistributorAcl implements FilmLicenses`, która łączy klienta dystrybutora z tłumaczem. Change Signature (⌘F6) konstruktora serwisu: `DistributorFeed` na `FilmLicenses`. Tłumacz staje się pakietowo-prywatny, a `SettlementModule` owija klienta w ACL.
**Po:**

```java
@Override
public Optional<FilmLicense> find(String filmCode) {
    return Optional.ofNullable(feed.fetch(filmCode)).map(translator::toLicense);
}
```

**Uruchom:** test zielony; `domainServiceUsesNoLegacyType` sprawdza sygnatury serwisu refleksją, a `serviceWorksWithAnyLicenseSource` rozlicza licencję festiwalową podaną lambdą, bez żadnego rekordu dystrybutora.
**Co powiedzieć:** żaden typ dystrybutora nie przekracza ACL, a "brak licencji" to `Optional.empty()`, nie `null`. Fasada by tu nie wystarczyła - uprościłaby wywołania, ale domena dalej mówiłaby kodami `A`/`S`. ACL to też naturalny punkt dla Strangler Fig: za portem może stać stary i nowy dostawca licencji.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s31 2 3`

### Rozwiązanie i uzasadnienie

`step3`: `SettlementService` zależy tylko od `FilmLicenses` i `FilmLicense`, `DistributorAcl` jest jedyną klasą, która widzi `DistributorFeed` i `DistributorRecord`, a pakietowo-prywatny `DistributorTranslator` zna wszystkie konwencje dystrybutora. Zaostrzona walidacja jest opisana testami.

### Pułapki

- ACL bez różnicy modeli to drogi Middle Man - tu różnica jest realna (kody, grosze, daty i procenty jako tekst).
- Tłumaczenie zachłanne bez decyzji: zły rekord zawieszonej licencji zatrzymuje teraz rozliczenie. Odrzucić, pominąć z logiem czy przenieść do kwarantanny - to decyzja biznesowa.
- Przecieki modelu: `DistributorRecord` zwracany "tylko w jednym miejscu", `null` zamiast `Optional`, kody `A`/`S` w enumie domeny.
- Normalizacja tytułu (`KRAINA LODU` daje `Kraina lodu`) też jest kontraktem (paragon, raport) - nie poprawiaj jej "przy okazji" na `Kraina Lodu`.

### Pytanie do sali

Dystrybutor dodaje status `T` (licencja tylko na pokazy prasowe). Gdzie pojawi się to pojęcie i jak nazwiecie je po stronie kina?

## Scena s32. Introduce Mediator - ekran wyboru miejsc

**Temat ze slajdów:** 14.1. Komponenty połączone każdy z każdym; 14.2. Wprowadzenie Mediatora: reguły współpracy w jednym miejscu; 14.3. Obserwator czy Mediator?
**Pakiet:** `pl.training.workshop.m5.s32_mediator` · **Test:** `scripts/warsztat.sh test m5/s32`
**Czas:** ~12 min

### W skrócie

**Co robimy:** Ekran wyboru miejsc ma cztery komponenty: `SeatMap`, `Basket`, `PriceSummary` i `PayButton`, połączone "każdy z każdym" (11 referencji). Każdy po akcji użytkownika sam odświeża pozostałe, a kolejność odświeżania (suma, potem przycisk) jest powtórzona w czterech miejscach. Wprowadzamy `SelectionMediator`: najpierw dla kliknięcia miejsca, potem dla wszystkich zdarzeń ze wspólnym `refresh()`, a na końcu przycisk zgłasza tylko chęć płatności i komponenty znają wyłącznie mediatora.

**Zasada:** Mediator skupia reguły współpracy komponentów w jednym miejscu: komponent zgłasza, co się stało, a mediator decyduje, kto i w jakiej kolejności reaguje. Od Observera różni się miejscem reguł: obserwator rozgłasza zdarzenie nieznanym odbiorcom, mediator zna wszystkich uczestników i świadomie koordynuje ich reakcję. Ryzyko to God Mediator - w mediatorze zostaje koordynacja, a logika komponentów (cena, limit miejsc) zostaje w komponentach.

**Efekt:** Cztery referencje do interfejsu mediatora zamiast 11 między komponentami, a nowy komponent (np. licznik czasu rezerwacji) to zmiana w mediatorze, nie w czterech klasach. Migawka ekranu po każdej akcji jest identyczna jak w start, także dla akcji ignorowanych.

### Co widzimy

`SeatSelectionScreen` (stabilny punkt wejścia) tylko tworzy komponenty i łączy je metodami `connect(...)`. Sala ma miejsca A1-C4, B2 jest sprzedane wcześniej, rząd C to VIP (35.00, pozostałe 25.00), voucher KINO10 daje -10%, przycisk jest aktywny dla 1-4 miejsc.

```java
// SeatMap.click - mapa zna trzy inne komponenty i kolejność ich odświeżania
if (selected.remove(seat)) {
    basket.drop(seat);
} else {
    selected.add(seat);
    basket.add(seat);
}
summary.recalculate(basket.seats());
payButton.refresh(basket.seats().size(), summary.total());
```

Ta sama para "przelicz sumę, odśwież przycisk" jest w `Basket.remove`, `Basket.clear` i `PriceSummary.applyVoucher`, a `PayButton.press` sam pyta koszyk i sumę, oznacza miejsca jako sprzedane i czyści koszyk, co uruchamia kaskadę odświeżeń. `S32EquivalenceTest` porównuje migawkę `render()` po sekwencjach akcji: ekran startowy, kolejność kliknięć, ponowne kliknięcie, miejsca sprzedane i spoza sali, usuwanie z koszyka, limit 4 miejsc, voucher, płatność, czyszczenie koszyka.

### Krok 1: interfejs mediatora i pierwsze zdarzenie

**W IDE:** utwórz interfejs `SelectionMediator` z metodą `seatToggled(String seat, boolean selected)`. `SeatSelectionScreen implements SelectionMediator` (jak "dialog" w GoF). Reakcję z `SeatMap.click` wydziel (⌥⌘M) i przenieś (F6) do `seatToggled` w ekranie. `SeatMap` dostaje mediatora w konstruktorze, jej `connect(...)` znika.
**Po:**

```java
boolean nowSelected = !selected.remove(seat);
if (nowSelected) {
    selected.add(seat);
}
mediator.seatToggled(seat, nowSelected);
```

**Uruchom:** `scripts/warsztat.sh test m5/s32` - zielone.
**Co powiedzieć:** komponent mówi "co się stało", a nie "kto ma zareagować". `new SeatMap(this)` w inicjalizatorze pola przekazuje `this` przed końcem konstrukcji ekranu - bezpieczne tylko dlatego, że mediator działa dopiero po akcji użytkownika. Pozostałe komponenty są nadal połączone wprost: jeden komponent naraz.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s32 0 1`

### Krok 2: wszystkie zdarzenia i jedno refresh()

**W IDE:** dodaj do interfejsu `seatRemoved`, `basketCleared`, `voucherApplied` i `seatsPaid(List<String>)`. `Basket`, `PriceSummary` i `PayButton` dostają mediatora w konstruktorze i zgłaszają zdarzenia zamiast odświeżać sąsiadów; przycisk zachowuje jeszcze `connect(basket, summary)`. Powtórzoną parę odświeżeń wydziel w ekranie do `refresh()` (⌥⌘M, IntelliJ zaproponuje zastąpienie duplikatów).
**Po:**

```java
/** Jedno miejsce, które zna kolejność odświeżania: najpierw suma, potem przycisk. */
private void refresh() {
    summary.recalculate(basket.seats());
    payButton.refresh(basket.seats().size(), summary.total());
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** kolejność odświeżania jest zachowaniem - przycisk pokazuje kwotę, więc suma musi być przeliczona wcześniej. Teraz jest zapisana raz zamiast czterech razy. Uwaga na reentrancję: `seatsPaid` woła `basket.clear()`, które znów woła mediatora (`basketCleared`) - kaskada działa, ale jest ukryta.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s32 1 2`

### Krok 3: komponenty znają tylko mediatora

**W IDE:** Change Signature (⌘F6) `seatsPaid(List<String>)` na `String payRequested()`. Zbieranie miejsc i kwoty oraz składanie komunikatu z `PayButton.press` przenieś do mediatora. Usuń `connect` z przycisku i pusty już konstruktor ekranu.
**Po:**

```java
public String press() {
    if (!enabled) {
        return "Nieaktywny: " + label;
    }
    return mediator.payRequested();
}
```

**Uruchom:** test zielony; `S32SolutionTest.everyComponentKnowsOnlyTheMediator` sprawdza pola komponentów refleksją, a `componentReportsWhatHappenedWithoutKnowingWhoReacts` testuje komponent z atrapą mediatora.
**Co powiedzieć:** przycisk pilnuje własnej reguły (aktywny dla 1-4 miejsc) i zgłasza "użytkownik chce zapłacić", a dane zbiera mediator. Ekran jest jedynym miejscem, które zna wszystkie cztery komponenty - i to jest zarazem ryzyko: pilnuj, żeby mediator nie zaczął liczyć cen.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s32 2 3`

### Rozwiązanie i uzasadnienie

`step3`: `SeatSelectionScreen` implementuje `SelectionMediator` i jako jedyny zna wszystkie komponenty; każdy komponent zna tylko interfejs mediatora. Reguły współpracy (kto reaguje na zdarzenie i w jakiej kolejności) są w jednym miejscu, a reguły komponentów (cena VIP, voucher, limit miejsc) zostały u siebie.

### Pułapki

- God Mediator: mediator przejmuje logikę komponentów (cena VIP, limit 4 miejsc) albo obsługuje cały system zamiast jednego ekranu - dziel według spójnych grup reguł.
- Zmiana kolejności w `refresh()` (przycisk przed sumą) - przycisk pokaże starą kwotę; wychwyci to przypadek z voucherem.
- Reentrancja: mediator woła komponent, który zgłasza zdarzenie z powrotem - podwójne odświeżenie albo nieskończona pętla.
- Mediator dla dwóch komponentów i jednej zależności - zwykłe wywołanie wystarczy.
- Zdarzenie domenowe dla wielu niezależnych odbiorców (mail, SMS, punkty po opłaceniu - scena s09) to Observer, a nie mediator ekranu.

### Pytanie do sali

Dochodzi licznik "rezerwacja wygasa za 10 minut", który po upływie czasu czyści koszyk. Ile klas zmienicie w `start`, a ile w `step3`?
