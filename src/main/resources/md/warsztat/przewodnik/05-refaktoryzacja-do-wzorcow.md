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

**Zmiany:**

1. Powstaje nowy plik `DiscountPolicy.java`: interfejs funkcyjny z jedną metodą `Money discount(Money base, String ticketType)` (Extract Interface).
2. W `TicketPricer` wydzielamy cały łańcuch `if` do nowej prywatnej metody statycznej `legacyDiscount(Money base, String ticketType, String program)` (Extract Method); gałęzie zamiast przypisywać zmienną `discount` zwracają wynik.
3. W `price` tworzymy lambdę `DiscountPolicy policy = (b, type) -> legacyDiscount(b, type, program);` i liczymy cenę przez `policy.discount(base, ticketType)`.

**Przed:**

```java
Money discount;
if (program.equals("PREMIERE")) {
    discount = Money.ZERO;
} else if (program.equals("STUDENT_WEEK") && ticketType.equals("S")) {
    discount = base.percent(50);
} else if (program.equals("STANDARD") || program.equals("STUDENT_WEEK")) {
    int percent = switch (ticketType) { ... };
    discount = base.percent(percent);
} else {
    throw new IllegalArgumentException("unknown program: " + program);
}
return base.minus(discount);
```

**Po:**

```java
DiscountPolicy policy = (b, type) -> legacyDiscount(b, type, program);
return base.minus(policy.discount(base, ticketType));
```

**Uruchom:** `scripts/warsztat.sh test m5/s01` - zielone.
**Co powiedzieć:** pierwszy commit jest mały i odwracalny: kontrakt strategii istnieje, ale deleguje do starego kodu. Nie zaczynamy od tworzenia wszystkich klas wzorca.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s01 0 1`

### Krok 2: przeniesienie gałęzi do strategii

**Zmiany:**

1. Powstaje nowy plik `StandardDiscount.java`: klasa implementuje `DiscountPolicy`, a jej metoda `discount` dostaje ciało gałęzi STANDARD (`switch` po typie biletu). Po przeniesieniu gałęzi uruchamiamy test.
2. Powstaje nowy plik `PremiereDiscount.java`: `discount` zwraca `Money.ZERO` i celowo nie sprawdza typu biletu, tak jak start. Test.
3. Powstaje nowy plik `StudentWeekDiscount.java`: student dostaje 50%, pozostałe typy klasa deleguje do strategii `fallback` przekazanej w konstruktorze (będzie nią `StandardDiscount`). Test.
4. W `TicketPricer` metoda `legacyDiscount` znika; w jej miejsce powstaje prywatna metoda `policyFor(String program)` ze `switch`, która zwraca strategię. `price` woła `policyFor(program).discount(base, ticketType)`.

**Przed:**

```java
private static Money legacyDiscount(Money base, String ticketType, String program) {
    if (program.equals("PREMIERE")) {
        return Money.ZERO;
    } else if (program.equals("STUDENT_WEEK") && ticketType.equals("S")) {
        return base.percent(50);
    } else if (program.equals("STANDARD") || program.equals("STUDENT_WEEK")) {
        int percent = switch (ticketType) { ... };
        return base.percent(percent);
    }
    throw new IllegalArgumentException("unknown program: " + program);
}
```

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

**Zmiany:**

1. Powstaje nowy plik `DiscountPrograms.java` (Extract Class): przenosimy tam `policyFor` jako publiczną metodę statyczną `forName(String program)`. Strategie są bezstanowe, więc trzymamy je jako współdzielone stałe `STANDARD`, `STUDENT_WEEK`, `PREMIERE`. Trafia tu też walidacja `program == null`.
2. `TicketPricer` dostaje pole `DiscountPolicy policy` i konstruktor `TicketPricer(DiscountPolicy policy)`. Metoda `price` traci parametr `program` - nowa sygnatura to `price(Money base, String ticketType)` (Change Signature). Metoda `policyFor` znika z tej klasy.
3. W `PriceBoard.priceFor` składamy kontekst na nowo: strategia z `DiscountPrograms.forName(request.program())` idzie do konstruktora `TicketPricer`. Pole `pricer` znika.

**Przed:**

```java
public Money price(Money base, String ticketType, String program) {
    ...
    return base.minus(policyFor(program).discount(base, ticketType));
}

private static DiscountPolicy policyFor(String program) {
    return switch (program) {
        case "STANDARD" -> new StandardDiscount();
        case "STUDENT_WEEK" -> new StudentWeekDiscount(new StandardDiscount());
        case "PREMIERE" -> new PremiereDiscount();
        default -> throw new IllegalArgumentException("unknown program: " + program);
    };
}
```

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

**Zmiany:**

1. W `Screening` usuwamy `final` i deklarujemy klasę jako `sealed ... permits MarathonScreening`. Konstruktor przestaje być prywatny (dostęp pakietowy), dochodzi chroniony akcesor `title()`.
2. Powstaje nowy plik `MarathonScreening.java` (Extract Subclass): podklasa `extends Screening` z własnym polem `films`, nadpisuje `label()`, `durationMinutes()` i `price()` ciałami gałęzi MARATHON.
3. W `fromRow` gałąź `"MARATHON"` tworzy `new MarathonScreening(row.title(), row.value())` zamiast bazowego `Screening`.
4. W klasie bazowej gałęzie MARATHON trzech switchy stają się martwe - rzucają `IllegalStateException("handled by MarathonScreening")`.

**Przed:**

```java
public static Screening fromRow(ScreeningRow row) {
    Kind kind = switch (row.kind()) {
        case "REGULAR" -> Kind.REGULAR;
        case "PREMIERE" -> Kind.PREMIERE;
        case "MARATHON" -> Kind.MARATHON;
        default -> throw new IllegalArgumentException("unknown screening kind: " + row.kind());
    };
    return new Screening(kind, row.title(), row.value());
}
```

**Po:**

```java
case "MARATHON" -> new MarathonScreening(row.title(), row.value());
```

**Uruchom:** `scripts/warsztat.sh test m5/s02` - zielone.
**Co powiedzieć:** jedna gałąź naraz, jeden test kontraktowy dla wszystkich rodzajów. Martwa gałąź rzuca wyjątek zamiast po cichu liczyć bzdury.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s02 0 1`

### Krok 2: pozostałe podklasy, baza abstrakcyjna

**Zmiany:**

1. Powstają nowe pliki `RegularScreening.java` i `PremiereScreening.java` - ten sam ruch co dla maratonu: pole `runtime`, nadpisane trzy metody, a w `fromRow` gałęzie `"REGULAR"` i `"PREMIERE"` tworzą podklasy.
2. Metody `label()`, `durationMinutes()` i `price()` w bazie stają się `abstract`; klasa to teraz `abstract sealed class Screening permits RegularScreening, PremiereScreening, MarathonScreening`.
3. Pole `kind`, pole `value` i enum `Kind` znikają (bezpieczne usunięcie nieużywanych elementów). Konstruktor bazy przyjmuje tylko `title`, podklasy wołają `super(title)`. Jedynym `switch` zostaje `fromRow`.

**Przed:**

```java
public int durationMinutes() {
    return switch (kind) {
        case REGULAR -> 20 + value;
        case PREMIERE -> 30 + value;
        case MARATHON -> throw new IllegalStateException("handled by MarathonScreening");
    };
}
```

**Po:**

```java
public abstract sealed class Screening permits RegularScreening, PremiereScreening, MarathonScreening {
    public abstract int durationMinutes();
```

**Uruchom:** test zielony.
**Co powiedzieć:** `switch` w miejscu tworzenia jest właściwy - to jest wiedza o konstrukcji. Znikają za to switche w zachowaniu.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s02 1 2`

### Krok 3: forma Java 25 - sealed interface i rekordy

**Zmiany:**

1. Klasę bazową `Screening` zamieniamy na `sealed interface` ze statyczną metodą `fromRow` i deklaracjami `title()`, `label()`, `durationMinutes()`, `price()`.
2. Podklasy zamieniamy na rekordy: `RegularScreening(String title, int runtime)`, `PremiereScreening(String title, int runtime)`, `MarathonScreening(String title, int films)`.
3. Pola prywatne i akcesor `title()` w bazie znikają - każdy rekord ma nazwane dane (`runtime`, `films`), a komponent `title` realizuje metodę interfejsu.

**Przed:**

```java
public final class MarathonScreening extends Screening {
    private final int films;

    MarathonScreening(String title, int films) {
        super(title);
        this.films = films;
    }

    @Override
    public int durationMinutes() { return films * 120 + (films - 1) * 15; }
    ...
}
```

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

**Zmiany:**

1. Powstaje nowy plik `Format.java`: enum `Format { TWO_D(1), THREE_D(2), IMAX(3) }` z polem `code`, akcesorem `code()` i statyczną metodą `fromCode(int)`, która dla nieznanego kodu rzuca ten sam wyjątek co start.
2. W `ScreeningCsv.describe` zaraz po odczycie zamieniamy `int formatCode` na `Format format = Format.fromCode(...)`.
3. Metody prywatne `label`, `basePrice`, `needsGlasses` i `toCsv` zmieniają sygnaturę: zamiast `int` przyjmują `Format` (Change Signature). Ich switche przechodzą na stałe enuma i tracą gałąź `default`. Stałe `FORMAT_2D`, `FORMAT_3D`, `FORMAT_IMAX` znikają.
4. Do CSV nadal trafia ten sam int: `toCsv` zapisuje `format.code()`.

**Przed:**

```java
int formatCode = Integer.parseInt(parts[1].trim());
return title + "|" + label(formatCode) + "|" + basePrice(formatCode)
        + "|okulary:" + (needsGlasses(formatCode) ? "tak" : "nie")
        + "|csv=" + toCsv(title, formatCode);
```

**Po:**

```java
Format format = Format.fromCode(Integer.parseInt(parts[1].trim()));
```

**Uruchom:** `scripts/warsztat.sh test m5/s03` - zielone, łącznie z "nieznany kod" i "kod nie jest liczbą".
**Co powiedzieć:** enum wystarcza, bo zestaw jest mały i zamknięty. Trwały kod to jawne pole, nigdy `ordinal()` ani `name()`.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s03 0 1`

### Krok 2: Move Method do typu

**Zmiany:**

1. Zachowanie zależne od formatu przenosimy do enuma (Move Method): każda stała dostaje w konstruktorze `label`, `basePrice` i `requiresGlasses`, a `Format` publiczne akcesory `label()`, `basePrice()`, `requiresGlasses()`.
2. Z `ScreeningCsv` znikają metody prywatne `label`, `basePrice`, `needsGlasses` i `toCsv` - `describe` pyta obiekt formatu: `format.label()`, `format.basePrice()`, `format.requiresGlasses()`, `format.code()`.

**Przed:**

```java
private Money basePrice(Format format) {
    return switch (format) {
        case TWO_D -> Money.of("25.00");
        case THREE_D -> Money.of("32.00");
        case IMAX -> Money.of("40.00");
    };
}

private boolean needsGlasses(Format format) {
    return format == Format.THREE_D;
}
```

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

**Zmiany:**

1. Powstaje nowy plik `FormatCodes.java` (Extract Class): mapa `EnumMap<Format, Integer>` z kodami 1, 2, 3 oraz statyczne `fromCode(int)` i `toCode(Format)`. Wyjątek dla nieznanego kodu bez zmian.
2. Z enuma `Format` znikają pole `code`, akcesor `code()` i metoda `fromCode` - typ domenowy nie wie już nic o kodach trwałych.
3. `ScreeningCsv.describe` czyta przez `FormatCodes.fromCode(...)` i zapisuje przez `FormatCodes.toCode(format)`; reszta kodu rozmawia wyłącznie z `Format`.

**Przed:**

```java
public enum Format {
    TWO_D(1, "2D", "25.00", false),
    ...

    public int code() {
        return code;
    }

    public static Format fromCode(int code) {
        for (Format format : values()) {
            if (format.code == code) {
                return format;
            }
        }
        throw new IllegalArgumentException("unknown format code: " + code);
    }
}
```

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

**Zmiany:**

1. W pakiecie `tickets` powstaje nowy plik `Tickets.java`: klasa z prywatnym konstruktorem i statycznymi metodami tworzącymi `standard(title, base, row)` i `vip(title, base, row)`; obie zwracają typ `Ticket` (Replace Constructor with Factory Method).
2. W `BoxOffice` każde `new StandardTicket(...)` i `new VipTicket(...)` zastępujemy wywołaniem `Tickets.standard(...)` / `Tickets.vip(...)` - w `sell` i w `sellAll`. Importy klas konkretnych znikają z klienta.

**Przed:**

```java
public Ticket sell(String title, Money base, int row) {
    if (row >= 10) {
        return new VipTicket(title, base, row);
    }
    return new StandardTicket(title, base, row);
}
```

**Po:**

```java
return Tickets.vip(title, base, row);
```

**Uruchom:** `scripts/warsztat.sh test m5/s04` - zielone.
**Co powiedzieć:** przekierowujemy tworzenie pojedynczo; klient nie importuje już klas konkretnych.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s04 0 1`

### Krok 2: decyzja do fabryki

**Zmiany:**

1. W `Tickets` metody `standard` i `vip` zastępujemy jedną metodą `forSeat(String title, Money base, int row)`, do której przenosimy warunek `row >= 10` (Move Method). Liczbę 10 zapisujemy jako stałą `VIP_FROM_ROW`.
2. `BoxOffice.sell` woła tylko `Tickets.forSeat(title, base, row)`; `sellAll` przestaje powielać warunek i woła `sell`.

**Przed:**

```java
public Ticket sell(String title, Money base, int row) {
    if (row >= 10) {
        return Tickets.vip(title, base, row);
    }
    return Tickets.standard(title, base, row);
}
```

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

**Zmiany:**

1. `StandardTicket` i `VipTicket` tracą modyfikator `public` - stają się pakietowe, więc poza pakietem `tickets` nie da się ich utworzyć ani zaimportować. Każde pozostałe użycie spoza pakietu zgłosi kompilator - to inwentaryzacja, którą robi za nas.
2. Przy okazji obie klasy zamieniamy na rekordy pakietowe: `record StandardTicket(String title, Money base, int row) implements Ticket` i analogicznie `VipTicket`. Ręcznie pisane pola i konstruktory znikają.

**Przed:**

```java
public final class VipTicket implements Ticket {
    private final String title;
    private final Money base;
    private final int row;

    public VipTicket(String title, Money base, int row) {
        this.title = title;
        this.base = base;
        this.row = row;
    }
    ...
}
```

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

**Zmiany:**

1. W `ReservationService` wydzielamy pętlę sprawdzania zajętości miejsc do prywatnej metody `requireFree(List<String> seats)` (Extract Method).
2. Blok tworzenia rezerwacji w `reserve` - od `sequence++` do `new Reservation(...)` - wydzielamy do prywatnej metody `newReservation(String channel, String email, List<String> seats)` (Extract Method). `takenSeats.addAll(seats)` zostaje w `reserve`.
3. W `reserveGroup` usuwamy skopiowany blok: metoda woła teraz `requireFree(seats)` i `newReservation("ONLINE", email, seats)`.

**Przed:**

```java
public Reservation reserve(String channel, String email, List<String> seats) {
    for (String seat : seats) {
        if (takenSeats.contains(seat)) {
            throw new IllegalStateException("seat taken: " + seat);
        }
    }
    sequence++;
    String id = "R" + sequence;
    if (!channel.equals("ONLINE") && !channel.equals("BOX_OFFICE")) {
        throw new IllegalArgumentException("unknown channel: " + channel);
    }
    ...
    takenSeats.addAll(seats);
    return new Reservation(id, channel, email, List.copyOf(seats), fee, expiresAt);
}
```

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

**Zmiany:**

1. Powstaje nowy plik `ReservationFactory.java` (Extract Class): przenosimy do niego `newReservation` jako publiczną metodę `create(channel, email, seats)` razem z polami `clock` i `sequence`.
2. Z `ReservationService` znikają pola `clock` i `sequence`; w ich miejsce pojawia się pole `factory`. Konstruktor `ReservationService(Clock clock)` sam tworzy fabrykę, więc jego sygnatura się nie zmienia.
3. `reserve` i `reserveGroup` wołają `factory.create(...)` zamiast `newReservation(...)`.

**Przed:**

```java
private final Clock clock;
private final Set<String> takenSeats = new HashSet<>();
private int sequence;

public ReservationService(Clock clock) {
    this.clock = clock;
}
...
private Reservation newReservation(String channel, String email, List<String> seats) {
    sequence++;
    String id = "R" + sequence;
    ...
}
```

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

**Zmiany:**

1. `ReservationService` dostaje drugi konstruktor `ReservationService(ReservationFactory factory)`, który zapisuje fabrykę w polu (z `Objects.requireNonNull`).
2. Dotychczasowy konstruktor `ReservationService(Clock clock)` zamieniamy na delegację `this(new ReservationFactory(clock))` - klienci serwisu nie muszą się zmieniać.

**Przed:**

```java
public ReservationService(Clock clock) {
    this.factory = new ReservationFactory(clock);
}
```

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

**Zmiany:**

1. Powstaje nowy plik `ScheduleBuilder.java`: builder trzyma budowany `DaySchedule` i pole `current` z bieżącą salą. Metoda `hall(name)` tworzy salę, dodaje ją do dnia i zapamiętuje jako bieżącą; `screening(title, hour, minute)` dodaje seans do bieżącej sali, a bez sali rzuca `IllegalStateException("screening without hall")`; `build()` zwraca dzień. Metody zwracają `this`, więc wywołania można łączyć w łańcuch.
2. `WeekendPlanner.plan` przepisujemy na wywołania buildera: znikają zmienne `hall1`-`hall3` oraz ręczne `new Hall`, `new Screening` i `day.add(...)`. Warunki `weekend` zostają jako zwykłe `if` wokół wywołań `screening`.
3. Węzły drzewa (`DaySchedule`, `Hall`, `Screening`) na razie bez zmian.

**Przed:**

```java
DaySchedule day = new DaySchedule(date);
Hall hall1 = new Hall("Sala 1");
hall1.add(new Screening("Diuna", LocalTime.of(18, 0)));
hall1.add(new Screening("Diuna", LocalTime.of(21, 0)));
day.add(hall1);
Hall hall2 = new Hall("Sala 2");
...
```

**Po:**

```java
ScheduleBuilder builder = new ScheduleBuilder(date)
        .hall("Sala 1")
        .screening("Diuna", 18, 0)
        .screening("Diuna", 21, 0)
        .hall("Sala 2");
```

**Uruchom:** `scripts/warsztat.sh test m5/s06` - zielone (sobota i piątek z pustą salą VIP).
**Co powiedzieć:** klient mówi "co" zbudować. Seans przed salą to teraz jawny błąd (`screening without hall`).
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s06 0 1`

### Krok 2: niemutowalne drzewo, builder jednorazowy

**Zmiany:**

1. Węzły drzewa zamieniamy na rekordy: `Screening(String title, LocalTime start)`, `Hall(String name, List<Screening> screenings)`, `DaySchedule(LocalDate date, List<Hall> halls)`. Metody `add` znikają, a kompaktowe konstruktory kopiują listy przez `List.copyOf` - gotowego drzewa nie da się zmienić.
2. `ScheduleBuilder` przestaje budować drzewo na bieżąco: zbiera dane w `LinkedHashMap<String, List<Screening>>`, a rekordy tworzy dopiero w `build()`.
3. Builder staje się jednorazowy: pole `built` i pomocnicza metoda `requireNotBuilt()` sprawiają, że drugie `build()` (i każde wywołanie po nim) rzuca `IllegalStateException("builder already used")`.
4. `hall(name)` odrzuca powtórzoną nazwę sali wyjątkiem `IllegalStateException("duplicate hall: ...")` - to świadomie dodany nowy niezmiennik.

**Przed:**

```java
public final class Hall {
    private final String name;
    private final List<Screening> screenings = new ArrayList<>();

    public Hall(String name) {
        this.name = name;
    }

    public void add(Screening screening) {
        screenings.add(screening);
    }
    ...
}
```

**Po:**

```java
public record Hall(String name, List<Screening> screenings) {
    public Hall { screenings = List.copyOf(screenings); }
```

**Uruchom:** test zielony; `S06SolutionTest` sprawdza jednorazowość i niemutowalność.
**Co powiedzieć:** gdy builder jest jedyną drogą budowy, drzewo może stać się niemutowalne. Unikalność nazw sal to **nowy niezmiennik** - start pozwalał na duplikaty. Taką regułę zatwierdzamy świadomie, bo zmienia zachowanie dla błędnych danych.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s06 1 2`

### Krok 3: zagnieżdżony builder z lambdą

**Zmiany:**

1. W `ScheduleBuilder` metodę `hall(String name)` zastępujemy metodą `hall(String name, Consumer<HallBuilder> content)`: builder tworzy `HallBuilder`, wykonuje lambdę synchronicznie dokładnie raz i od razu dodaje gotową salę. Ukryty stan "bieżąca sala" znika.
2. Powstaje zagnieżdżona klasa statyczna `HallBuilder` z prywatnym konstruktorem i metodą `screening(title, hour, minute)`.
3. Konstruktor `ScheduleBuilder` staje się prywatny; wejściem jest statyczna metoda `ScheduleBuilder.day(date)`.
4. `WeekendPlanner.plan` przepisujemy na zagnieżdżone lambdy - wcięcia kodu odpowiadają poziomom drzewa.

**Przed:**

```java
public ScheduleBuilder hall(String name) {
    requireNotBuilt();
    if (halls.containsKey(name)) {
        throw new IllegalStateException("duplicate hall: " + name);
    }
    current = new ArrayList<>();
    halls.put(name, current);
    return this;
}
```

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

**Zmiany:**

1. Powstaje nowy plik `PricedTicket.java` (Extract Interface): interfejs z metodami `Money price()` i `String description()`.
2. `Ticket` deklaruje `implements PricedTicket` - ciało bez zmian, flagi na razie zostają.
3. `TicketAssembler.assemble` zmienia typ zwracany z `Ticket` na `PricedTicket`.

**Przed:**

```java
public final class TicketAssembler {
    public Ticket assemble(TicketOrder order) {
        return new Ticket(order);
    }
}
```

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

**Zmiany:**

1. Powstaje nowy plik `Insurance.java`: rekord `Insurance(PricedTicket inner) implements PricedTicket` - `price()` dodaje 4.00 do `inner.price()`, a `description()` dokleja " +ubezpieczenie" do `inner.description()`.
2. Z `Ticket` znika pole `insurance` razem z jego `if` w `price()` i członem w `description()` (bezpieczne usunięcie).
3. W `TicketAssembler.assemble` rdzeń owijamy dekoratorem, gdy `order.insurance()`.

**Przed:**

```java
public Money price() {
    Money price = base;
    if (vip) {
        price = price.plus(Money.of("10.00"));
    }
    if (glasses) {
        price = price.plus(Money.of("3.00"));
    }
    if (insurance) {
        price = price.plus(Money.of("4.00"));
    }
    return price;
}
```

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

**Zmiany:**

1. Powstają nowe pliki `VipSeat.java` i `Glasses3D.java`: dekoratory zbudowane tak samo jak `Insurance` (+10.00 i " +VIP"; +3.00 i " +okulary 3D").
2. Rdzeń `Ticket` staje się rekordem `Ticket(String title, String format, Money base)`: `price()` zwraca `base`, a `description()` to `title + " " + format`. Flagi `vip` i `glasses` oraz konstruktor przyjmujący `TicketOrder` znikają.
3. `TicketAssembler.assemble` składa cały łańcuch w ustalonej kolejności: rdzeń, potem `VipSeat`, `Glasses3D`, `Insurance`. Decyzja "czy okulary potrzebne" (format 3D i brak własnych) zostaje w assemblerze.

**Przed:**

```java
public final class Ticket implements PricedTicket {
    private final String title;
    private final String format;
    private final Money base;
    private final boolean vip;
    private final boolean glasses;
    ...
    public Money price() {
        Money price = base;
        if (vip) {
            price = price.plus(Money.of("10.00"));
        }
        if (glasses) {
            price = price.plus(Money.of("3.00"));
        }
        return price;
    }
```

**Po:**

```java
PricedTicket ticket = new Ticket(order.title(), order.format(), order.base());
if (order.vip()) {
    ticket = new VipSeat(ticket);
}
if (order.format().equals("3D") && !order.ownGlasses()) {
    ticket = new Glasses3D(ticket);
}
if (order.insurance()) {
    ticket = new Insurance(ticket);
}
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

**Zmiany:**

1. Wewnątrz `Reservation` powstaje prywatny `sealed interface ReservationState` z jedyną metodą `status()` oraz stany jako enumy: `NewState.INSTANCE`, `PaidState.INSTANCE` i wspólny `ClosedState` ze stałymi `USED`, `EXPIRED`, `CANCELLED`.
2. Pole `Status status` zastępujemy polem `ReservationState state = NewState.INSTANCE`; warunki w operacjach porównują teraz `state.status()`.
3. Powtarzalne fragmenty wydzielamy do prywatnych metod (Extract Method): `charge()` obciąża bramkę, `moveTo(next)` zmienia stan, `record(effect)` dopisuje efekt.

**Przed:**

```java
private Status status = Status.NEW;

@Override
public void pay() {
    if (status != Status.NEW) {
        throw new IllegalStateException("cannot pay in " + status);
    }
    payments.charge(id);
    status = Status.PAID;
    effects.add("charged");
}
```

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

**Zmiany:**

1. Interfejs stanu dostaje metody domyślne `pay(Reservation reservation)` i `use(Reservation reservation)`, które rzucają wyjątek przez prywatną metodę `invalid(action)` - komunikat identyczny jak w start.
2. `NewState` nadpisuje `pay`: obciążenie (`reservation.charge()`), przejście do `PaidState.INSTANCE`, efekt "charged". `PaidState` nadpisuje `use`: przejście do `ClosedState.USED`, efekt "gate opened".
3. W kontekście metody `pay()` i `use()` tylko delegują: `state.pay(this)` i `state.use(this)`; ich warunki `if` znikają.

**Przed:**

```java
@Override
public void pay() {
    if (state.status() != Status.NEW) {
        throw new IllegalStateException("cannot pay in " + state.status());
    }
    charge();
    moveTo(PaidState.INSTANCE);
    record("charged");
}
```

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

**Zmiany:**

1. Interfejs stanu dostaje domyślne metody `expire(Reservation)` i `cancel(Reservation)` rzucające `invalid(...)`.
2. `NewState` nadpisuje `expire` (przejście do `ClosedState.EXPIRED`, efekt "seats released") i `cancel` (przejście do `ClosedState.CANCELLED`, efekt "seats released"). `PaidState` nadpisuje `cancel` (CANCELLED, efekty "refund" i "seats released"). Warunek `if/else if` z kontekstu rozkłada się na dwa stany.
3. W kontekście `expire()` i `cancel()` tylko delegują: `state.expire(this)` i `state.cancel(this)`.

**Przed:**

```java
@Override
public void cancel() {
    if (state.status() == Status.NEW) {
        moveTo(ClosedState.CANCELLED);
        record("seats released");
    } else if (state.status() == Status.PAID) {
        moveTo(ClosedState.CANCELLED);
        record("refund");
        record("seats released");
    } else {
        throw new IllegalStateException("cannot cancel in " + state.status());
    }
}
```

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

**Zmiany:**

1. Powstaje nowy plik `ReservationPaid.java`: rekord zdarzenia z danymi potrzebnymi odbiorcom (`reservationId`, `email`, `phone`, `amount`).
2. Trzy powiadomienia z `confirm` wydzielamy do prywatnej metody `notifyPaid(ReservationPaid event)` (Extract Method); w jej ciele dane pochodzą ze zdarzenia (`event.email()` itd.), nie z `payment`.
3. `confirm` po zapisaniu opłaty tworzy zdarzenie i woła `notifyPaid(...)`.

**Przed:**

```java
paid.add(payment.reservationId());
mailer.send(payment.email(),
        "Potwierdzenie platnosci " + payment.reservationId() + ": " + payment.amount());
sms.send(payment.phone(), "Oplacono " + payment.reservationId());
loyalty.addPoints(payment.email(), payment.amount().amount().intValue() / 10);
```

**Po:**

```java
paid.add(payment.reservationId());
notifyPaid(new ReservationPaid(
        payment.reservationId(), payment.email(), payment.phone(), payment.amount()));
```

**Uruchom:** `scripts/warsztat.sh test m5/s09` - zielone.
**Co powiedzieć:** zdarzenie to fakt z danymi potrzebnymi odbiorcom. Jeszcze nic nie jest dynamiczne.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s09 0 1`

### Krok 2: odbiorcy jako obserwatorzy

**Zmiany:**

1. Powstaje nowy plik `PaymentListener.java`: interfejs funkcyjny z metodą `onPaid(ReservationPaid event)`.
2. Powstają trzy nowe pliki obserwatorów: rekordy `MailConfirmation(Mailer mailer)`, `SmsConfirmation(SmsGateway sms)` i `LoyaltyPoints(LoyaltyProgram loyalty)`; każdy przejmuje jedną linię z `notifyPaid`.
3. `PaymentService` zamiast pól `mailer`, `sms`, `loyalty` trzyma pole `List<PaymentListener> listeners`; konstruktor (sygnatura bez zmian) buduje `List.of(...)` z trzech obserwatorów w starej kolejności.
4. `notifyPaid` staje się pętlą po słuchaczach, celowo bez `try/catch` - fail-fast jak w start.

**Przed:**

```java
private void notifyPaid(ReservationPaid event) {
    mailer.send(event.email(),
            "Potwierdzenie platnosci " + event.reservationId() + ": " + event.amount());
    sms.send(event.phone(), "Oplacono " + event.reservationId());
    loyalty.addPoints(event.email(), event.amount().amount().intValue() / 10);
}
```

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

**Zmiany:**

1. Powstaje nowy plik `Subscription.java`: interfejs funkcyjny rozszerzający `AutoCloseable`, z `close()` bez deklarowanych wyjątków.
2. `PaymentService` dostaje metodę `subscribe(PaymentListener)`: opakowuje słuchacza w prywatną klasę `Registration`, dodaje go do listy `CopyOnWriteArrayList` i zwraca `Subscription`, którego `close()` wyrejestrowuje idempotentnie (flaga `AtomicBoolean`).
3. Konstruktor serwisu staje się bezargumentowy - importy `Mailer`, `SmsGateway`, `LoyaltyProgram` znikają z serwisu. `confirm` publikuje zdarzenie pętlą po rejestracjach; prywatna metoda `notifyPaid` znika.
4. Subskrypcje przenosimy do istniejącego `PaymentServices.standard(...)`: korzeń kompozycji tworzy serwis i rejestruje trzech odbiorców w starej kolejności.

**Przed:**

```java
public PaymentService(Mailer mailer, SmsGateway sms, LoyaltyProgram loyalty) {
    this.listeners = List.of(
            new MailConfirmation(mailer), new SmsConfirmation(sms), new LoyaltyPoints(loyalty));
}
```

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

**Zmiany:**

1. Powstają nowe pliki `MenuItem.java`, `Product.java` i `Combo.java`: `sealed interface MenuItem` z metodami `name()`, `price()` i `render(depth, text)`, liść `Product(String name, Money price)` oraz niemutowalny węzeł `Combo(String name, List<MenuItem> items)`, którego cena to suma elementów.
2. Powstaje nowy plik `MenuMapper.java`: statyczna metoda `fromNested(List<?>)` tłumaczy stary format na drzewo i zachowuje komunikaty błędów starego kodu ("combo needs a name", "product needs a price: ...", "unsupported element: ...").
3. `BarMenu` zostaje bez zmian - obie reprezentacje żyją obok siebie, porównuje je test różnicowy.

**Przed:** brak - nowe pliki `MenuItem.java`, `Product.java`, `Combo.java`, `MenuMapper.java`; stary kod nietknięty.
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

**Zmiany:**

1. Ciało `BarMenu.price` zastępujemy jedną linią: mapujemy stary format i liczymy cenę na drzewie. Rekurencyjna pętla z `instanceof` znika z tej metody.
2. Usuwamy nieużywaną już metodę `requireName` - walidację nazwy przejął mapper.
3. `render` zostaje stare i nadal działa, bo woła nowe `price`.

**Przed:**

```java
public Money price(List<?> combo) {
    requireName(combo);
    Money total = Money.ZERO;
    for (Object element : combo.subList(1, combo.size())) {
        if (element instanceof String product) {
            total = total.plus(productPrice(product));
        } else if (element instanceof List<?> nested) {
            total = total.plus(price(nested));
        } else {
            throw new IllegalArgumentException("unsupported element: " + element);
        }
    }
    return total;
}
```

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

**Zmiany:**

1. Publiczne `BarMenu.render` deleguje do drzewa: mapujemy combo i wołamy `render(0, text)` na wyniku.
2. Prywatna rekurencyjna metoda `render(List<?>, int, StringBuilder)` i metoda `productPrice` znikają (bezpieczne usunięcie) - w `BarMenu` nie ma już `instanceof` ani rzutowań.

**Przed:**

```java
private void render(List<?> combo, int depth, StringBuilder text) {
    text.append("  ".repeat(depth)).append(combo.get(0))
            .append(' ').append(price(combo)).append('\n');
    for (Object element : combo.subList(1, combo.size())) {
        if (element instanceof String product) {
            text.append("  ".repeat(depth + 1)).append(product, 0, product.indexOf('='))
                    .append(' ').append(productPrice(product)).append('\n');
        } else if (element instanceof List<?> nested) {
            render(nested, depth + 1, text);
        } else {
            throw new IllegalArgumentException("unsupported element: " + element);
        }
    }
}
```

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

**Zmiany:**

1. Metody `add(MenuComponent)` i `children()` przenosimy z `MenuComponent` w dół, do `Combo` (Push Members Down). Bazowe `add()` rzucające `UnsupportedOperationException` i `children()` zwracające pustą listę znikają - `Product` nie ma już czego odziedziczyć.
2. `describe()` w bazie opisuje odtąd tylko liścia (`name() + " " + price()`); `Combo` nadpisuje je i rozszerza opis o dzieci.
3. W `ComboCatalog` zmieniamy typ zmiennych z `MenuComponent` na `Combo` tam, gdzie dodajemy dzieci - każde pozostałe wywołanie `add` na liściu zgłosi kompilator.

**Przed:**

```java
public abstract class MenuComponent {
    public abstract String name();

    public abstract Money price();

    public void add(MenuComponent child) {
        throw new UnsupportedOperationException("cannot add to " + name());
    }

    public List<MenuComponent> children() {
        return List.of();
    }
    ...
}
```

**Po:**

```java
Combo combo = new Combo("Zestaw Rodzinny");
combo.add(new Product("Popcorn XL", "24.00"));
```

**Uruchom:** `scripts/warsztat.sh test m5/s11` - zielone; `S11SolutionTest` pokazuje, że `Product` nie ma już `add`.
**Co powiedzieć:** błąd przeniósł się z runtime do kompilacji. Ceną jest to, że klient musi wiedzieć, czy ma w ręku węzeł.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s11 0 1`

### Krok 2: niemutowalny Composite w Javie 25

**Zmiany:**

1. `MenuComponent` zamieniamy na `sealed interface ... permits Product, Combo` z metodami `name()`, `price()`, `describe()`.
2. `Product` staje się rekordem `Product(String name, Money price)` z dodatkowym konstruktorem przyjmującym cenę jako `String`.
3. `Combo` staje się rekordem `Combo(String name, List<MenuComponent> children)`: konstruktor kopiuje listę przez `List.copyOf`, dochodzi statyczna metoda `Combo.of(name, children...)`, a metoda `add` znika zupełnie.
4. `ComboCatalog` zapisuje drzewa deklaratywnie zagnieżdżonymi wywołaniami `Combo.of(...)`; prywatne metody `family()` i `duo()` znikają.

**Przed:**

```java
private MenuComponent duo() {
    Combo combo = new Combo("Zestaw Duo");
    combo.add(new Product("Popcorn L", "18.00"));
    combo.add(new Product("Cola", "9.00"));
    combo.add(new Product("Cola", "9.00"));
    return combo;
}
```

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

**Zmiany:**

1. Wyliczenie kwoty zwrotu jednego biletu z `refund` wydzielamy do prywatnej statycznej metody `ticketShare(TicketData ticket, LocalDateTime now)` (Extract Method): po starcie seansu 0, co najmniej 24h przed - pełna cena, inaczej 50%.
2. W `refundAll` ciało pętli zastępujemy wywołaniem `ticketShare` - druga, nieco inaczej zapisana kopia reguły znika.
3. Potrącenie `FEE` i `max(Money.ZERO)` zostają w obu metodach publicznych, naliczane raz na wywołanie.

**Przed:**

```java
public Money refund(TicketData ticket, LocalDateTime now) {
    Money amount;
    if (!now.isBefore(ticket.showStart())) {
        amount = Money.ZERO;
    } else if (Duration.between(now, ticket.showStart()).toHours() >= 24) {
        amount = ticket.price();
    } else {
        amount = ticket.price().percent(50);
    }
    return amount.minus(FEE).max(Money.ZERO);
}
```

**Po:**

```java
total = total.plus(ticketShare(ticket, now));
```

**Uruchom:** `scripts/warsztat.sh test m5/s12` - zielone.
**Co powiedzieć:** zanim wprowadzimy wzorzec, usuwamy duplikację reguły. Test granicy 24h potwierdza, że obie wersje były równoważne.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s12 0 1`

### Krok 2: Composite i jeden kontrakt

**Zmiany:**

1. Powstają nowe pliki `Refundable.java`, `SingleTicket.java` i `TicketGroup.java`: `sealed interface Refundable` z metodą `refundableAmount(now)`, liść - rekord `SingleTicket(TicketData ticket)` z regułą przeniesioną z `ticketShare`, węzeł - rekord `TicketGroup(List<Refundable> items)` sumujący elementy, z pomocniczą metodą `TicketGroup.of(tickets)`.
2. `RefundService` dostaje nową metodę `refund(Refundable, now)`. Stare `refund(TicketData, ...)` i `refundAll` zostają jako cienkie delegacje oznaczone `@Deprecated`; metoda `ticketShare` znika z serwisu.
3. `CancellationDesk` przechodzi na nowy kontrakt - na razie nadal wybiera `SingleTicket` albo `TicketGroup` w zależności od rozmiaru listy.

**Przed:**

```java
public Money refund(TicketData ticket, LocalDateTime now) {
    return ticketShare(ticket, now).minus(FEE).max(Money.ZERO);
}

public Money refundAll(List<TicketData> tickets, LocalDateTime now) {
    Money total = Money.ZERO;
    for (TicketData ticket : tickets) {
        total = total.plus(ticketShare(ticket, now));
    }
    return total.minus(FEE).max(Money.ZERO);
}
```

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

**Zmiany:**

1. Po migracji klientów usuwamy z `RefundService` przestarzałe metody `refund(TicketData, ...)` i `refundAll` (bezpieczne usunięcie) - zostaje tylko `refund(Refundable, now)`.
2. W `CancellationDesk` znika warunek `tickets.size() == 1` - klient zawsze buduje `TicketGroup.of(tickets)`, także z jednego biletu.

**Przed:**

```java
public Money refund(List<TicketData> tickets, LocalDateTime now) {
    Refundable refundable = tickets.size() == 1
            ? new SingleTicket(tickets.getFirst())
            : TicketGroup.of(tickets);
    return service.refund(refundable, now);
}
```

**Po:**

```java
return service.refund(TicketGroup.of(tickets), now);
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

**Zmiany:**

1. Powstaje nowy plik `CompositeProgramItem.java` (Extract Superclass): klasa abstrakcyjna implementująca `ProgramItem`, do której z `Marathon` przenosimy pole `children`, metodę `add` z kontrolą `null` i `children()` zwracające kopię listy. Obie metody są `final`.
2. `Marathon` deklaruje `extends CompositeProgramItem`; jego własne kopie pola i obu metod znikają, a `minutes()` i `describe()` korzystają z odziedziczonego `children()`.
3. `ShortsBlock` deklaruje `extends CompositeProgramItem` i również traci zduplikowaną obsługę dzieci.

**Przed:**

```java
public final class Marathon implements ProgramItem {
    private final String name;
    private final List<ProgramItem> children = new ArrayList<>();
    ...
    public void add(ProgramItem child) {
        children.add(Objects.requireNonNull(child, "child"));
    }

    public List<ProgramItem> children() {
        return List.copyOf(children);
    }
```

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

**Zmiany:**

1. Do nadklasy podciągamy to, co wspólne (Pull Up Method): pole `name` z chronionym konstruktorem, metodę `protected final childrenMinutes()` sumującą minuty dzieci oraz finalne `describe()` jako szkielet opisu z abstrakcyjnym hookiem `label()`.
2. `Marathon` zostaje z `minutes()` (suma dzieci plus 15 minut przerwy między pozycjami) i `label()` zwracającym "Maraton"; `ShortsBlock` z `minutes()` bez przerw i `label()` zwracającym "Blok".

**Przed:**

```java
public final class ShortsBlock extends CompositeProgramItem {
    private final String name;
    ...
    @Override
    public int minutes() {
        int total = 0;
        for (ProgramItem child : children()) {
            total += child.minutes();
        }
        return total;
    }

    @Override
    public String describe() {
        return "Blok " + name + " (" + minutes() + " min) "
                + children().stream().map(ProgramItem::describe).toList();
    }
}
```

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

**Zmiany:**

1. Gałąź XML wydzielamy do prywatnej metody `payWithXml(String reservationId, Money amount)` (Extract Method); zmienna `request` znika, bo złożony XML idzie wprost do `xml.submit(...)`.
2. Gałąź REST wydzielamy do prywatnej metody `payWithRest(String reservationId, Money amount)` (Extract Method).
3. Obie metody mają identyczną sygnaturę i zwracają `PaymentResult` - `pay` już tylko wybiera jedną z nich.

**Przed:**

```java
public PaymentResult pay(String provider, String reservationId, Money amount) {
    if (provider.equals("XML")) {
        long grosze = amount.amount().movePointRight(2).longValueExact();
        String request = "<charge ref='" + reservationId + "' amount='" + grosze + "'/>";
        String response = xml.submit(request);
        if (response.contains("status='OK'")) {
            return PaymentResult.accepted(attribute(response, "id"));
        }
        return PaymentResult.declined(attribute(response, "code"));
    } else if (provider.equals("REST")) {
        ...
    }
    throw new IllegalArgumentException("unknown provider: " + provider);
}
```

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

**Zmiany:**

1. Powstaje nowy plik `PaymentGateway.java`: interfejs z metodą `PaymentResult pay(String reservationId, Money amount)` - preferowany kontrakt kina.
2. Powstaje nowy plik `XmlPayAdapter.java`: klasa implementuje `PaymentGateway`, dostaje `XmlPayGateway` w konstruktorze i przejmuje ciało `payWithXml` razem z pomocniczą metodą `attribute` (Move Method).
3. Powstaje nowy plik `RestPayAdapter.java`: analogiczna implementacja dla `RestPayClient` z ciałem `payWithRest` - wyjątek odmowy staje się wynikiem `declined`.
4. W `CheckoutService` pola `xml` i `rest` zmieniają typ na `PaymentGateway`; konstruktor (sygnatura bez zmian) tworzy adaptery. Metody `payWithXml`, `payWithRest` i `attribute` znikają z serwisu.

**Przed:**

```java
private PaymentResult payWithXml(String reservationId, Money amount) {
    long grosze = amount.amount().movePointRight(2).longValueExact();
    String response = xml.submit("<charge ref='" + reservationId + "' amount='" + grosze + "'/>");
    if (response.contains("status='OK'")) {
        return PaymentResult.accepted(attribute(response, "id"));
    }
    return PaymentResult.declined(attribute(response, "code"));
}
```

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

**Zmiany:**

1. `CheckoutService` dostaje konstruktor `CheckoutService(Map<String, PaymentGateway> gateways)` z kopią `Map.copyOf`; pola `xml` i `rest` zastępuje jedno pole `gateways`.
2. Stary konstruktor `CheckoutService(XmlPayGateway, RestPayClient)` deleguje do nowego ze standardowym zestawem dwóch adapterów pod kluczami "XML" i "REST".
3. W `pay` łańcuch `if` zastępujemy wyszukaniem w mapie; dla nieznanego dostawcy rzucamy ten sam wyjątek co start.

**Przed:**

```java
public PaymentResult pay(String provider, String reservationId, Money amount) {
    if (provider.equals("XML")) {
        return xml.pay(reservationId, amount);
    } else if (provider.equals("REST")) {
        return rest.pay(reservationId, amount);
    }
    throw new IllegalArgumentException("unknown provider: " + provider);
}
```

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

**Zmiany:**

1. Ciało gałęzi SELL wydzielamy do prywatnej metody `sell(String args)` (Extract Method); tak samo REFUND do `refund(String args)` i REPORT do `report(String args)`.
2. Wszystkie trzy metody dostają tę samą sygnaturę - także `report`, które z `args` nie korzysta. `handle` już tylko wybiera metodę.

**Przed:**

```java
if (command.equals("SELL")) {
    String[] sell = args.split(" ", 2);
    if (sell.length < 2 || !sell[0].matches("\\d+")) {
        return "Blad: SELL <liczba> <tytul>";
    }
    int quantity = Integer.parseInt(sell[0]);
    Money price = PRICES.get(sell[1]);
    if (price == null) {
        return "Blad: nieznany film " + sell[1];
    }
    Money total = price.times(quantity);
    cash = cash.plus(total);
    tickets += quantity;
    return "Sprzedano " + quantity + " x " + sell[1] + " = " + total;
} else if (command.equals("REFUND")) {
    ...
```

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

**Zmiany:**

1. Powstaje nowy plik `Till.java` (Extract Class): przejmuje stan kasy - pola `cash`, `tickets` i cennik `PRICES` - oraz operacje `priceOf`, `sold`, `refunded`, `cash()`, `tickets()`.
2. Powstaje nowy plik `ConsoleCommand.java`: interfejs funkcyjny `String execute(String args, Till till)`.
3. Powstają nowe pliki `SellCommand.java`, `RefundCommand.java`, `ReportCommand.java`: każda klasa dostaje ciało jednej z metod `sell`/`refund`/`report`, a na stanie operuje przez `Till`.
4. `CashierConsole` trzyma pole `till` i trzy komendy; gałęzie `if` wołają `sell.execute(args, till)` itd. Prywatne metody `sell`, `refund`, `report` oraz stare pola stanu i cennik znikają z konsoli.

**Przed:**

```java
private Money cash = Money.ZERO;
private int tickets;
...
private String refund(String args) {
    Money price = PRICES.get(args);
    if (price == null) {
        return "Blad: nieznany film " + args;
    }
    if (tickets == 0) {
        return "Blad: brak biletow do zwrotu";
    }
    cash = cash.minus(price);
    tickets--;
    return "Zwrot 1 x " + args + " = " + price;
}
```

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

**Zmiany:**

1. `CashierConsole` trzyma pole `Map<String, ConsoleCommand> commands`: konstruktor domyślny rejestruje "SELL", "REFUND" i "REPORT", a drugi konstruktor przyjmuje mapę z zewnątrz (kopia `Map.copyOf`). Pola `sell`, `refund`, `report` znikają.
2. W `handle` łańcuch `if` zastępujemy wyszukaniem w mapie po znormalizowanym kluczu (`toUpperCase(Locale.ROOT)` zostaje); brak komendy daje ten sam komunikat "Nieznana komenda: ...".

**Przed:**

```java
if (command.equals("SELL")) {
    return sell.execute(args, till);
} else if (command.equals("REFUND")) {
    return refund.execute(args, till);
} else if (command.equals("REPORT")) {
    return report.execute(args, till);
}
return "Nieznana komenda: " + parts[0];
```

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

**Zmiany:**

1. W `CsvSalesReport` wydzielamy różnice do prywatnych metod (Extract Method): `header()`, `row(Sale sale)` i `footer(int tickets, Money total)`.
2. W `HtmlSalesReport` robimy to samo, z tymi samymi nazwami i sygnaturami; escapowanie `&` i `<` trafia do `row`.
3. `render` w HTML przepisujemy na dokładnie ten sam kształt co w CSV: kopia listy i `sort` zamiast streama, te same nazwy zmiennych (`sorted`, `text`, `tickets`, `total`). Po tym kroku `render` obu klas jest identyczny znak w znak.

**Przed:**

```java
StringBuilder html = new StringBuilder();
html.append("<table>\n<tr><th>Godzina</th><th>Film</th><th>Bilety</th><th>Kwota</th></tr>\n");
Money sum = Money.ZERO;
int count = 0;
for (Sale sale : sales.stream().sorted(Comparator.comparing(Sale::time)).toList()) {
    String title = sale.title().replace("&", "&amp;").replace("<", "&lt;");
    html.append("<tr><td>").append(sale.time()).append("</td><td>").append(title)
            .append("</td><td>").append(sale.tickets()).append("</td><td>").append(sale.amount())
            .append("</td></tr>\n");
    sum = sum.plus(sale.amount());
    count += sale.tickets();
}
```

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

**Zmiany:**

1. Powstaje nowy plik `SalesReport.java` (Extract Superclass): klasa abstrakcyjna z podciągniętym z CSV `render(List<Sale>)` (Pull Up Method) oraz abstrakcyjnymi, chronionymi `header()`, `row(Sale)` i `footer(int, Money)`.
2. `render` w bazie oznaczamy `final` - kolejność kroków i liczenie sumy należą do szkieletu.
3. `CsvSalesReport` i `HtmlSalesReport` deklarują `extends SalesReport`: obie tracą własną kopię `render`, a ich metody formatujące dostają `@Override` i widoczność `protected`.

**Przed:**

```java
public final class CsvSalesReport {
    public String render(List<Sale> sales) {
        List<Sale> sorted = new ArrayList<>(sales);
        sorted.sort(Comparator.comparing(Sale::time));
        StringBuilder text = new StringBuilder(header());
        int tickets = 0;
        Money total = Money.ZERO;
        for (Sale sale : sorted) {
            text.append(row(sale));
            tickets += sale.tickets();
            total = total.plus(sale.amount());
        }
        return text.append(footer(tickets, total)).toString();
    }
    ...
}
```

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

**Zmiany:**

1. Konstruktor `PriceList` staje się prywatny; powstaje pole `private static final PriceList INSTANCE = new PriceList()` i statyczna metoda `getInstance()`, która je zwraca.
2. Licznik pomiarowy `CREATED` (`AtomicInteger`) i metoda `created()` znikają z `PriceList`.
3. W `TicketDesk.quote` zamieniamy `new PriceList()` na `PriceList.getInstance()`.

**Przed:**

```java
public final class PriceList {
    private static final String TARIFF = "2D=25.00;3D=32.00;IMAX=40.00";
    private static final AtomicInteger CREATED = new AtomicInteger();

    private final Map<String, Money> prices = new HashMap<>();

    public PriceList() {
        for (String entry : TARIFF.split(";")) {
            String[] pair = entry.split("=");
            prices.put(pair[0], Money.of(pair[1]));
        }
        CREATED.incrementAndGet();
    }
```

**Po:**

```java
Money price = PriceList.getInstance().basePrice(format);
```

**Uruchom:** `scripts/warsztat.sh test m5/s17` - zielone.
**Co powiedzieć:** Singleton to decyzja o cyklu życia - bezpieczna tylko dlatego, że obiekt jest niemutowalny i udowodniliśmy równoważność instancji.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s17 0 1`

### Krok 2: Singleton jako enum

**Zmiany:**

1. Klasa `PriceList` staje się enumem z jedyną wartością `INSTANCE`; pole `INSTANCE`, prywatny konstruktor klasy i metoda `getInstance()` znikają.
2. Konstruktor enumu parsuje taryfę do lokalnej mapy i zapisuje ją w polu `prices` przez `Map.copyOf` - mapa jest niemodyfikowalna po zbudowaniu.
3. W `TicketDesk.quote` zamieniamy `PriceList.getInstance()` na `PriceList.INSTANCE`.

**Przed:**

```java
public final class PriceList {
    private static final String TARIFF = "2D=25.00;3D=32.00;IMAX=40.00";
    private static final PriceList INSTANCE = new PriceList();

    private final Map<String, Money> prices = new HashMap<>();

    private PriceList() {
        ...
    }

    public static PriceList getInstance() {
        return INSTANCE;
    }
```

**Po:**

```java
Money price = PriceList.INSTANCE.basePrice(format);
```

**Uruchom:** test zielony.
**Co powiedzieć:** enum daje bezpieczną publikację i odporność na serializację oraz refleksję. Ale to **jedna instancja na loader klas**, nie "jedna na JVM".
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s17 1 2`

### Krok 3: wstrzyknięcie zamiast globalnego dostępu

**Zmiany:**

1. Powstaje nowy plik `Tariff.java`: interfejs funkcyjny z metodą `Money basePrice(String format)`, wydzielony z enumu (Extract Interface).
2. `PriceList` implementuje `Tariff`; `basePrice` dostaje `@Override`, reszta enumu bez zmian.
3. `TicketDesk` dostaje pole `private final Tariff tariff` i konstruktor `TicketDesk(Tariff tariff)`; `quote` woła `tariff.basePrice(format)`.
4. Dochodzi konstruktor bezargumentowy delegujący `this(PriceList.INSTANCE)` - dotychczasowi klienci działają bez zmian.

**Przed:**

```java
public final class TicketDesk {
    public Money quote(String format, boolean online) {
        Money price = PriceList.INSTANCE.basePrice(format);
        return online ? price.plus(Money.of("2.00")) : price;
    }
}
```

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

**Zmiany:**

1. `checkEmail` i `checkSeats` zmieniają zwracany typ ze `String` na `List<String>`; fragmenty tracą separator `"; "` (`List.of("brak e-maila")` zamiast `"brak e-maila; "`), pusta lista zamiast pustego napisu.
2. W `validate` zmienna `warnings` staje się `List<String>` (`ArrayList`); sklejanie `+=` zamieniamy na `addAll`, a ostrzeżenie o rozpoczętym seansie na `warnings.add(...)`.
3. Wynik składa `String.join("; ", warnings)`; obcinanie dwóch ostatnich znaków znika.

**Przed:**

```java
public String validate(ReservationDraft draft) {
    String warnings = "";
    warnings += checkEmail(draft.email());
    warnings += checkSeats(draft.seats());
    if (!draft.now().isBefore(draft.showStart())) {
        warnings += "seans juz sie rozpoczal; ";
    }
    return warnings.isEmpty() ? "OK" : warnings.substring(0, warnings.length() - 2);
}
```

**Po:**

```java
warnings.addAll(checkEmail(draft.email()));
warnings.addAll(checkSeats(draft.seats()));
```

**Uruchom:** `scripts/warsztat.sh test m5/s18` - zielone.
**Co powiedzieć:** separator dokłada teraz jedno miejsce. Metody wciąż tworzą własne listy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s18 0 1`

### Krok 2: Collecting Parameter

**Zmiany:**

1. `checkEmail` i `checkSeats` dostają w sygnaturze dodatkowy parametr `List<String> warnings`, a zwracany typ zmienia się na `void`; każde `return List.of(x)` zamieniamy na `warnings.add(x)`.
2. W `validate` wywołania `warnings.addAll(checkEmail(...))` stają się `checkEmail(draft.email(), warnings)` - metody dopisują do przekazanego akumulatora.
3. Warunek czasu seansu wydzielamy do nowej metody `checkShowTime(ReservationDraft draft, List<String> warnings)`.

**Przed:**

```java
warnings.addAll(checkEmail(draft.email()));
warnings.addAll(checkSeats(draft.seats()));
if (!draft.now().isBefore(draft.showStart())) {
    warnings.add("seans juz sie rozpoczal");
}
...
private List<String> checkEmail(String email) {
    if (email == null || email.isBlank()) {
        return List.of("brak e-maila");
    }
    ...
}
```

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

**Zmiany:**

1. Powstaje nowy plik `Warnings.java`: klasa z prywatną listą `items` i tylko dwiema metodami - `add(String warning)` i `summary()` (zwraca `"OK"` albo `String.join("; ", items)`).
2. W `validate` i we wszystkich metodach `check...` typ parametru zbierającego zmieniamy z `List<String>` na `Warnings`.
3. Składanie wyniku w `validate` zastępuje `return warnings.summary()` - format jest w jednym miejscu, w `Warnings`.

**Przed:**

```java
public String validate(ReservationDraft draft) {
    List<String> warnings = new ArrayList<>();
    checkEmail(draft.email(), warnings);
    checkSeats(draft.seats(), warnings);
    checkShowTime(draft, warnings);
    return warnings.isEmpty() ? "OK" : String.join("; ", warnings);
}
```

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

**Zmiany:**

1. Powstaje nowy plik `OrderItemVisitor.java`: generyczny interfejs `OrderItemVisitor<R>` z metodami `visitTicket(TicketItem)`, `visitSnack(SnackItem)`, `visitVoucher(VoucherItem)`.
2. `OrderItem` przestaje być pustym znacznikiem: dostaje metodę `<R> R accept(OrderItemVisitor<R> visitor)`; rekordy `TicketItem`, `SnackItem` i `VoucherItem` implementują ją wywołaniem swojej metody `visit...` (double dispatch).
3. Powstaje nowy plik `ReceiptLineVisitor.java`: implementacja `OrderItemVisitor<String>`, do której trafiają trzy gałęzie z metody `line`.
4. Metoda `line` znika z `ReceiptPrinter`; drukarka trzyma pole `lines = new ReceiptLineVisitor()` i w pętli woła `item.accept(lines)`.

**Przed:**

```java
private String line(OrderItem item) {
    if (item instanceof TicketItem ticket) {
        return "Bilet " + ticket.title() + " " + ticket.format() + " " + ticket.price();
    } else if (item instanceof SnackItem snack) {
        return snack.name() + " " + snack.price();
    } else if (item instanceof VoucherItem voucher) {
        return "Voucher " + voucher.code() + " -" + voucher.value();
    }
    throw new IllegalArgumentException("unknown item: " + item);
}
```

**Po:**

```java
text.append(item.accept(lines)).append('\n');
```

**Uruchom:** `scripts/warsztat.sh test m5/s19` - zielone.
**Co powiedzieć:** double dispatch - element wybiera metodę odwiedzającego. Brak metody dla typu to błąd kompilacji.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s19 0 1`

### Krok 2: pozostałe operacje jako Visitory

**Zmiany:**

1. Powstaje nowy plik `AmountVisitor.java`: implementacja `OrderItemVisitor<Money>`, która przejmuje gałęzie metody `amount` (cena biletu, cena produktu, voucher jako kwota ujemna).
2. Powstaje nowy plik `VatVisitor.java`: implementacja `OrderItemVisitor<Money>`, która przejmuje gałęzie metody `vat` razem z pomocniczą metodą `vatOf`.
3. Metody `amount`, `vat` i `vatOf` znikają z `ReceiptPrinter`; drukarka trzyma pola `amounts` i `vats`, a w pętli sumuje `item.accept(amounts)` i `item.accept(vats)`. Ostatnie łańcuchy `instanceof` znikają.

**Przed:**

```java
private Money vat(OrderItem item) {
    if (item instanceof TicketItem ticket) {
        return vatOf(ticket.price(), 8);
    } else if (item instanceof SnackItem snack) {
        return vatOf(snack.price(), 23);
    } else if (item instanceof VoucherItem) {
        return Money.ZERO;
    }
    throw new IllegalArgumentException("unknown item: " + item);
}
```

**Po:**

```java
total = total.plus(item.accept(amounts));
vat = vat.plus(item.accept(vats));
```

**Uruchom:** test zielony; `S19SolutionTest` dodaje operację "grupa VAT" jako nowy Visitor.
**Co powiedzieć:** macierz zmian: nowa operacja - nowa klasa, tanio; nowy rodzaj pozycji - zmiana interfejsu i **wszystkich** Visitorów.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s19 1 2`

### Krok 3: alternatywa Java 25 - sealed + switch

**Zmiany:**

1. `OrderItem` staje się `sealed interface OrderItem permits TicketItem, SnackItem, VoucherItem` i traci metodę `accept`; rekordy tracą swoje implementacje `accept`.
2. Pliki `OrderItemVisitor.java`, `ReceiptLineVisitor.java`, `AmountVisitor.java` i `VatVisitor.java` znikają.
3. Operacje wracają do `ReceiptPrinter` jako statyczne metody `line`, `amount` i `vat` - każda to wyczerpujący `switch` po typach bez `default` (w `line` z record patterns); `vatOf` też wraca do drukarki.

**Przed:**

```java
public final class ReceiptPrinter {
    private final ReceiptLineVisitor lines = new ReceiptLineVisitor();
    private final AmountVisitor amounts = new AmountVisitor();
    private final VatVisitor vats = new VatVisitor();

    public String print(List<OrderItem> items) {
        ...
        for (OrderItem item : items) {
            text.append(item.accept(lines)).append('\n');
            total = total.plus(item.accept(amounts));
            vat = vat.plus(item.accept(vats));
        }
```

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

**Zmiany:**

1. Tabela to w rzeczywistości dwie niezależne reguły. Wydzielamy (Extract Method) prywatną statyczną metodę `basePrice(String format)`: `switch` po formacie zwraca 25.00 / 32.00 / 40.00, dla nieznanego formatu ten sam wyjątek co start.
2. Wydzielamy drugą metodę `adjustForDay(DayOfWeek day, Money base)`: `switch` po dniu - wtorek to `base.minus(base.percent(30))`, weekend `base.plus(Money.of("2.00"))`, pozostałe dni `base`. Zmienna lokalna `weekend` znika.
3. `price` staje się złożeniem obu metod.

**Przed:**

```java
public Money price(DayOfWeek day, String format) {
    boolean weekend = day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    if (format.equals("2D")) {
        return Money.of(day == DayOfWeek.TUESDAY ? "17.50" : weekend ? "27.00" : "25.00");
    } else if (format.equals("3D")) {
        return Money.of(day == DayOfWeek.TUESDAY ? "22.40" : weekend ? "34.00" : "32.00");
    } else if (format.equals("IMAX")) {
        return Money.of(day == DayOfWeek.TUESDAY ? "28.00" : weekend ? "42.00" : "40.00");
    }
    throw new IllegalArgumentException("unknown format: " + format);
}
```

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

**Zmiany:**

1. Powstaje nowy plik `DayPolicy.java`: interfejs funkcyjny z metodą `Money apply(Money base)` - korekta dnia jako wymienny algorytm (Strategy).
2. Powstaje nowy plik `DayPolicies.java`: stałe `CHEAP_TUESDAY`, `WEEKEND`, `REGULAR` jako lambdy oraz kalendarz `standard(DayOfWeek day)`, który przejmuje `switch` z `adjustForDay`.
3. `ShowPricing` dostaje pole `Function<DayOfWeek, DayPolicy> calendar` i dwa konstruktory: wstrzykujący kalendarz oraz bezargumentowy delegujący do `DayPolicies::standard`. Metoda `adjustForDay` znika, a format zostaje prostym `switch` w `basePrice`.

**Przed:**

```java
public Money price(DayOfWeek day, String format) {
    return adjustForDay(day, basePrice(format));
}
...
private static Money adjustForDay(DayOfWeek day, Money base) {
    return switch (day) {
        case TUESDAY -> base.minus(base.percent(30));
        case SATURDAY, SUNDAY -> base.plus(Money.of("2.00"));
        default -> base;
    };
}
```

**Po:**

```java
return calendar.apply(day).apply(basePrice(format));
```

**Uruchom:** test zielony; `pathAMakesANewDayCampaignCheap` dodaje "środę seniora" bez dotykania formatów.
**Co powiedzieć:** wybierz tę ścieżkę, gdy marketing co miesiąc zmienia akcje dniowe, a formaty są stabilne. Zestaw polityk jest otwarty i wstrzykiwany.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s20 1 2`

### Krok 3: ścieżka B - zmienia się zestaw formatów (typ z zachowaniem)

**Zmiany:**

1. Ścieżka B jest alternatywą budowaną od kroku 1, nie kontynuacją kroku 2 - punktem wyjścia jest `step1`, bez `DayPolicy` i `DayPolicies`.
2. Powstaje nowy plik `Format.java`: enum ze stałymi `TWO_D("2D", "25.00")`, `THREE_D("3D", "32.00")`, `IMAX("IMAX", "40.00")`, fabryką `Format.of(code)` rzucającą ten sam wyjątek `unknown format` oraz metodą `priceOn(DayOfWeek day)`, do której trafia `switch` reguły dnia.
3. `ShowPricing` kurczy się do delegacji do typu; metody `basePrice` i `adjustForDay` znikają.

**Przed:**

```java
private static Money basePrice(String format) {
    return switch (format) {
        case "2D" -> Money.of("25.00");
        case "3D" -> Money.of("32.00");
        case "IMAX" -> Money.of("40.00");
        default -> throw new IllegalArgumentException("unknown format: " + format);
    };
}
```

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

**Zmiany:**

1. Powstaje nowy plik `SettlementModel.java`: interfejs z metodą `BigDecimal payout(Deal deal, int week, BigDecimal ticketRevenue)` - wspólny kontrakt modeli rozliczeń.
2. Powstaje nowy plik `PercentageModel.java`: gałąź `"PERCENT"` przenosimy do metody `payout` tej klasy, a kwota 500.00 staje się stałą `MINIMUM_GUARANTEE`.
3. Powstaje nowy plik `FestivalFeeAdapter.java`: implementacja `SettlementModel`, która dostaje `FestivalTariffClient` w konstruktorze i przejmuje gałąź `"FESTIVAL"` razem z konwersją groszy (`BigDecimal.valueOf(cents, 2)`).
4. W `DistributorSettlement` pole `festival` i cały `switch` znikają; serwis trzyma mapę `models` i wybiera z niej model, a dla nieznanego modelu rzuca ten sam wyjątek co start.

**Przed:**

```java
public BigDecimal payout(Deal deal, int week, BigDecimal ticketRevenue) {
    switch (deal.model()) {
        case "PERCENT" -> {
            int percent = week == 1 ? 50 : week == 2 ? 40 : 35;
            BigDecimal share = ticketRevenue.multiply(BigDecimal.valueOf(percent))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            return share.max(new BigDecimal("500.00"));
        }
        case "FESTIVAL" -> {
            long cents = festival.weeklyFeeInCents(deal.title(), week);
            return BigDecimal.valueOf(cents, 2);
        }
        default -> throw new IllegalArgumentException("nieznany model: " + deal.model());
    }
}
```

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

**Zmiany:**

1. Umowy festiwalowe wygasły: wpis `"FESTIVAL"` znika z mapy `models`.
2. Plik `FestivalFeeAdapter.java` znika - przed usunięciem upewniamy się, że klasa nie ma już żadnych użyć (bezpieczne usunięcie).
3. `SettlementModel` i `PercentageModel` zostają bez zmian - Strategy z jedną implementacją.

**Przed:**

```java
private final Map<String, SettlementModel> models = Map.of(
        "PERCENT", new PercentageModel(),
        "FESTIVAL", new FestivalFeeAdapter(new FestivalTariffClient()));
```

**Po:**

```java
private final Map<String, SettlementModel> models = Map.of(
        "PERCENT", new PercentageModel());
```

**Uruchom:** test zielony; `afterTheVariantIsGoneFestivalDealsAreRejected` - to świadoma zmiana zachowania, dlatego sprawdzana osobno, a nie testem równoważności.
**Co powiedzieć:** zostaje Strategy z jedną implementacją i mapą z jednym wpisem - klasyczny sygnał nadmiaru wzorca ze slajdu 17.1.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s21 1 2`

### Krok 3: Usunięcie wzorca

**Zmiany:**

1. Ciało `PercentageModel.payout` wstawiamy w miejsce wywołania w `DistributorSettlement.payout` (Inline Method); mapa `models` znika, a na jej miejsce wchodzi warunek odrzucający model inny niż `"PERCENT"` tym samym wyjątkiem.
2. Stała `MINIMUM_GUARANTEE` przenosi się z `PercentageModel` do `DistributorSettlement`.
3. Pliki `PercentageModel.java` i `SettlementModel.java` znikają (bezpieczne usunięcie po upewnieniu się, że nie mają użyć).

**Przed:**

```java
private final Map<String, SettlementModel> models = Map.of(
        "PERCENT", new PercentageModel());

public BigDecimal payout(Deal deal, int week, BigDecimal ticketRevenue) {
    SettlementModel model = models.get(deal.model());
    if (model == null) {
        throw new IllegalArgumentException("nieznany model: " + deal.model());
    }
    return model.payout(deal, week, ticketRevenue);
}
```

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

**Zmiany:**

1. `try/catch` z `CinemaFacade.freeSeats` przenosimy do nowej prywatnej metody `freeSeats(String id)` w `SeatBadge` - jedynym kliencie, który polega na tłumaczeniu "brak seansu" na 0; `badge` woła teraz własne `freeSeats(id)`.
2. `CinemaFacade.freeSeats` staje się zwykłą delegacją `return catalog.freeSeats(id)` - pośrednik jest od teraz czystym forwarderem.

**Przed:**

```java
// CinemaFacade
public int freeSeats(String id) {
    try {
        return catalog.freeSeats(id);
    } catch (NoSuchElementException e) {
        return 0;
    }
}
```

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

**Zmiany:**

1. Konstruktor `SeatBadge` zmienia sygnaturę: zamiast `CinemaFacade` przyjmuje `ScreeningCatalog`; pole `cinema` zmienia typ i nazwę na `catalog`.
2. Wywołania `cinema.freeSeats`, `cinema.title` i `cinema.format` stają się wywołaniami na `catalog` - plakietka rozmawia bezpośrednio z katalogiem.
3. `DailyBoard` i `CinemaFacade` bez zmian - migrujemy po jednym kliencie.

**Przed:**

```java
public final class SeatBadge {
    private final CinemaFacade cinema;

    public SeatBadge(CinemaFacade cinema) {
        this.cinema = Objects.requireNonNull(cinema, "cinema");
    }
```

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

**Zmiany:**

1. Ten sam ruch w `DailyBoard`: konstruktor przyjmuje `ScreeningCatalog`, pole `cinema` staje się `catalog`, a wywołanie `cinema.screenings()` zamieniamy na `catalog.all()`.
2. Plik `CinemaFacade.java` znika - po migracji obu klientów nie ma już żadnych użyć (bezpieczne usunięcie).

**Przed:**

```java
private final CinemaFacade cinema;
...
public String render() {
    return cinema.screenings().stream()
            .map(screening -> screening.id() + " " + screening.title() + " " + screening.format())
            .collect(Collectors.joining("\n"));
}
```

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

- Wstawienie wszystkich metod pośrednika do klientów za jednym zamachem, bez sprawdzenia, że któraś tłumaczy błędy.
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

**Zmiany:**

1. Łańcuch `if/else` wyboru udziału wydzielamy (Extract Method) do prywatnej statycznej metody `share(long minutesBeforeStart)` zwracającej `double`: 0, 1.0 albo 0.5.
2. Początek `refund` zastępuje jedno wyrażenie `ticketsPaid * share(minutesBeforeStart) - 3.00`; zaokrąglenie i format zostają bez zmian.

**Przed:**

```java
double refund;
if (minutesBeforeStart <= 0) {
    refund = 0;
} else if (minutesBeforeStart >= 24 * 60) {
    refund = ticketsPaid;
} else {
    refund = ticketsPaid * 0.5;
}
refund = refund - 3.00;
```

**Po:**

```java
double refund = ticketsPaid * share(minutesBeforeStart) - 3.00;
```

**Uruchom:** `scripts/warsztat.sh test m5/s23` - wszystko zielone, łącznie z przypadkami brzegowymi (29.17 i 0.00).
**Co powiedzieć:** `x * 1.0 == x` w `double` - arytmetyka jest identyczna co do bitu. To jest refaktoryzacja.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s23 0 1`

### Krok 2: "przy okazji" double na BigDecimal

**Zmiany:**

1. Pokazujemy naiwny ruch: `share` zmienia zwracany typ na `BigDecimal` (`ZERO`, `ONE`, `new BigDecimal("0.5")`), a potrącenie staje się stałą `FEE = new BigDecimal("3.00")`.
2. `refund` liczy całość na `BigDecimal`: `valueOf`, `multiply`, `subtract(FEE)`, `setScale(2, HALF_UP)`, `max(BigDecimal.ZERO)` i zwraca `toPlainString()`.
3. `Math.round(...)` i `String.format("%.2f", ...)` znikają - to właśnie dwa zmienione "przy okazji" elementy kontraktu.

**Przed:**

```java
public String refund(double ticketsPaid, long minutesBeforeStart) {
    double refund = ticketsPaid * share(minutesBeforeStart) - 3.00;
    if (refund < 0) {
        refund = 0;
    }
    refund = Math.round(refund * 100) / 100.0;
    return String.format(Locale.ROOT, "%.2f", refund);
}
```

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

**Zmiany:**

1. W łańcuchu wywołań przestawiamy kolejność: `max(BigDecimal.ZERO)` przed `setScale(2, RoundingMode.HALF_UP)` - zero znów drukuje się jako "0.00".
2. Różnicę groszową (HALF_UP na `BigDecimal`) zatwierdzamy jako zmianę kontraktu: Javadoc z decyzją, nowe oczekiwanie w teście, osobny commit.

**Przed:**

```java
BigDecimal refund = BigDecimal.valueOf(ticketsPaid)
        .multiply(share(minutesBeforeStart))
        .subtract(FEE)
        .setScale(2, RoundingMode.HALF_UP)
        .max(BigDecimal.ZERO);
return refund.toPlainString();
```

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

**Zasada:** Rename nadaje nazwę opisującą rolę w kontekście, a nie typ ani implementację. Dla zmiennych lokalnych IDE robi to bezpiecznie, ale nazwa użyta w konfiguracji, refleksji, JSON czy ORM jest częścią API i wymaga strategii migracji. Automatyczna zmiana nazwy nie widzi takich użyć, więc nie mylić jej z bezpieczną zmianą kontraktu.

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

**Zmiany:**

1. Zmieniamy nazwy parametrów (Rename): `s` na `sales`, `flag` na `onlineOnly`.
2. Zmieniamy nazwy zmiennych lokalnych: `m` na `linesByTitle`, `x` na `sale`, `l` na `previous` (w pętli agregującej) i `line` (w pętli drukującej), `b` na `csv`, `v` na `values`, `c` na `component`.
3. Nazwa metody `calc2` i komponenty rekordu `t`, `n`, `d` na razie zostają - żyją poza Javą.

**Przed:**

```java
public String calc2(List<Sale> s, boolean flag) {
    Map<String, Line> m = new TreeMap<>();
    for (Sale x : s) {
        if (flag && !x.online()) {
            continue;
        }
        Line l = m.get(x.title());
        ...
    }
    StringBuilder b = new StringBuilder(header()).append('\n');
```

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

**Zmiany:**

1. Najpierw na żywo naiwnie: zmieniamy nazwę metody `calc2` na `revenueCsv` w całym kodzie Javy. Test `everyStepRunsTheConfiguredJob` robi się czerwony (`NoSuchMethodException: calc2`) - zmiana nazwy poprawiła wywołania w Javie, ale tekst w konfiguracji nie jest dla niej nazwą metody. Wycofujemy zmianę.
2. Teraz poprawnie: kopiujemy metodę, kopia dostaje nazwę `revenueCsv`, a ciało `calc2` zamieniamy na delegowanie `return revenueCsv(sales, onlineOnly)`.
3. `calc2` dostaje `@Deprecated` i Javadoc z powodem: nazwa żyje w `report.properties`, usunąć wolno dopiero po zmianie konfiguracji na serwerach.

**Przed:**

```java
/** Wywoływana także refleksyjnie przez ReportJob - nazwa metody jest w konfiguracji. */
public String calc2(List<Sale> sales, boolean onlineOnly) {
    Map<String, Line> linesByTitle = new TreeMap<>();
    ...
}
```

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

**Zmiany:**

1. Najpierw naiwnie: zmieniamy nazwę komponentu `t` w `Line` na `title`. Test pokaże nagłówek `title;n;d` zamiast `t;n;d` - plik dla dystrybutora właśnie się zmienił. Wycofujemy zmianę.
2. Odcinamy kontrakt od refleksji: refleksyjne `header()` zastępuje jawna stała `CSV_HEADER = "t;n;d"`, a `row(Line line)` jawnie skleja trzy pola; metoda `header()` i importy refleksji znikają. Test zielony.
3. Dopiero teraz zmieniamy nazwy komponentów rekordu (Rename): `t` na `title`, `n` na `tickets`, `d` na `revenue`.

**Przed:**

```java
public record Line(String t, int n, BigDecimal d) {
}
...
private static String header() {
    return Arrays.stream(Line.class.getRecordComponents())
            .map(RecordComponent::getName)
            .collect(Collectors.joining(";"));
}
```

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

- Poleganie na przeszukiwaniu komentarzy i tekstów przy zmianie nazwy. Czasem pomaga, ale nie widzi plików poza projektem ani nazw składanych dynamicznie (`"calc" + version`).
- Inne miejsca, w których nazwa jest kontraktem: JSON (Jackson), kolumny ORM, szablony, `Serializable`, nazwy beanów, logi parsowane przez monitoring.
- Usunięcie delegatu `calc2` "bo nikt go nie woła". Wyszukiwanie użyć go nie znajdzie, bo wołają go tylko refleksja i konfiguracja.

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

**Zmiany:**

1. Blok pod komentarzem `// cena bazowa` (od deklaracji `BigDecimal base;` do końca `if` z porankiem) wydzielamy (Extract Method) do prywatnej metody `basePrice(Order order)`.
2. Analiza przepływu danych: do bloku wchodzi tylko `order`, wychodzi tylko `base` - metoda przyjmuje `Order` i zwraca `BigDecimal`.
3. Komentarz `// cena bazowa formatu` znika - zastąpiła go nazwa metody.

**Przed:**

```java
// cena bazowa formatu
BigDecimal base;
if (order.format().equals("IMAX")) {
    base = new BigDecimal("40.00");
} else if (order.format().equals("3D")) {
    base = new BigDecimal("32.00");
} else {
    base = new BigDecimal("25.00");
}
if (order.start().getHour() < 12) {
    base = base.subtract(new BigDecimal("5.00"));
}
```

**Po:**

```java
BigDecimal base = basePrice(order);
```

**Uruchom:** `scripts/warsztat.sh test m5/s25` - 16 testów zielonych.
**Co powiedzieć:** blok z jednym wyjściem to najprostszy przypadek, IDE robi to bezpiecznie. Komentarz stał się nazwą metody.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s25 0 1`

### Krok 2: Split Loop, potem dwa razy Extract Method

**Zmiany:**

1. Próba wydzielenia całej pętli w jedną metodę kończy się odmową: fragment ma dwa wyjścia, `subtotal` i `vipSeats`. To jest moment dydaktyczny.
2. Kopiujemy pętlę (Split Loop): z pierwszej kopii usuwamy wszystko, co dotyczy `vipSeats` (zostaje zliczanie `subtotal` z `setScale` na końcu), z drugiej wszystko, co dotyczy `subtotal` (zostaje licznik `vipSeats`). Uruchamiamy test.
3. Każdą pętlę wydzielamy (Extract Method) do prywatnej metody: `subtotal(Order order, BigDecimal base)` i `vipSeats(Order order)` - każda ma już jedno wyjście.

**Przed:**

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
subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);
```

**Po:**

```java
BigDecimal subtotal = subtotal(order, base);
int vipSeats = vipSeats(order);
```

**Uruchom:** test po Split Loop i po każdej ekstrakcji.
**Co powiedzieć:** zamiast zwracać parę (record z dwoma polami) rozdzielamy odpowiedzialności. Dwa przejścia po liście to koszt pomijalny wobec czytelności. Jeśli wydajność miałaby znaczenie, mierzymy, a nie zgadujemy.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s25 1 2`

### Krok 3: Extract Method dla renderowania

**Zmiany:**

1. Blok pod komentarzem `// dokument` wydzielamy (Extract Method) do prywatnej metody `render(Order order, BigDecimal subtotal, int vipSeats)`; komentarz znika.
2. Zmienną `base` wstawiamy w miejsce jedynego użycia (Inline Variable): `subtotal(order, basePrice(order))`.
3. `describe` to już tylko trzy linie: policz sumę, policz miejsca VIP, wyrenderuj dokument.

**Przed:**

```java
BigDecimal base = basePrice(order);
BigDecimal subtotal = subtotal(order, base);
int vipSeats = vipSeats(order);

// dokument
StringBuilder text = new StringBuilder();
text.append("BILETY: ").append(order.title()).append('\n');
...
return text.toString();
```

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

**Zmiany:**

1. Powstaje nowy plik `TicketPricing.java`: interfejs funkcyjny z metodą `Money price(Money formatBase)`.
2. Powstaje nowy plik `TicketPricings.java`: mapa kod typu - strategia (lambdy, wspólna metoda pomocnicza `discounted(percent)`) oraz `forType(code)`, które dla nieznanego kodu rzuca `unknown ticket type: X` jak start.
3. W pętli `book` wewnętrzny `switch` po typie biletu zastępuje wywołanie strategii.

**Przed:**

```java
Money total = Money.ZERO;
for (int i = 3; i < args.length; i++) {
    total = total.plus(switch (args[i]) {
        case "N" -> base;
        case "S" -> base.minus(base.percent(25));
        case "E" -> base.minus(base.percent(30));
        case "C" -> base.minus(base.percent(40));
        default -> throw new IllegalArgumentException("unknown ticket type: " + args[i]);
    });
}
```

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

**Zmiany:**

1. Powstaje nowy plik `BookingLedger.java`: odbiorca poleceń, do którego przenosimy z pulpitu pola `statuses`, `amounts` i `till` (Extract Class) razem z operacjami na nich: `open`, `remove`, `status`, `pay`, `use`, `cancel`, `restore`, `deposit`, `withdraw`, `summary`.
2. Powstaje nowy plik `DeskCommand.java`: interfejs z metodami `execute(ledger)`, `undo(ledger)`, `journalEntry()` i wspólną walidacją `idFrom(args, operation)`.
3. Powstają nowe pliki `BookCommand.java`, `PayCommand.java`, `UseCommand.java`, `CancelCommand.java`: każda klasa ma fabrykę `from(args)`, wykonuje operację na odbiorcy i pamięta to, czego potrzebuje do cofnięcia (poprzedni status, przesuniętą kwotę).
4. Pulpit dostaje rejestr `Map<String, Function<String[], DeskCommand>>` i historię `List<DeskCommand>`; rekord `Entry`, metody `book` i `change` oraz `switch` po nazwie operacji w `undo()` znikają - `undo()` woła `last.undo(ledger)`. UNDO i REPORT zostają w `switch` pulpitu.

**Przed:**

```java
Entry last = journal.removeLast();
switch (last.operation()) {
    case "BOOK" -> {
        statuses.remove(last.id());
        amounts.remove(last.id());
    }
    case "PAY" -> {
        till = till.minus(last.moved());
        statuses.put(last.id(), last.previous());
    }
    case "CANCEL" -> {
        till = till.plus(last.moved());
        statuses.put(last.id(), last.previous());
    }
    default -> statuses.put(last.id(), last.previous());
}
```

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

**Zmiany:**

1. Powstaje nowy plik `Booking.java`: klasa z kwotą i polem `private State state` oraz prywatnym enumem `State`. Warunki z `BookingLedger.pay/use/cancel` przenosimy do stałych enumu: NEW nadpisuje `pay` i `cancel`, PAID nadpisuje `use` i `cancel`, USED i CANCELLED nie nadpisują niczego; metody domyślne rzucają `cannot <akcja> in <STATUS>`.
2. `Booking.restore(Status)` wraca do zapamiętanego stanu z pominięciem reguł przejść - kompensacja dla cofania poleceń.
3. `BookingLedger` zamiast map `statuses` i `amounts` trzyma `Map<String, Booking>`; jego `pay` i `cancel` już tylko przesuwają pieniądze w kasie według kwot zwróconych przez `Booking`.

**Przed:**

```java
// BookingLedger
Money cancel(String id) {
    Status status = status(id);
    if (status == Status.NEW) {
        statuses.put(id, Status.CANCELLED);
        return Money.ZERO;
    } else if (status == Status.PAID) {
        till = till.minus(amounts.get(id));
        statuses.put(id, Status.CANCELLED);
        return amounts.get(id);
    }
    throw invalid("cancel", status);
}
```

**Po:**

```java
NEW {
    @Override
    Money pay(Booking booking) {
        booking.state = PAID;
        return booking.amount;
    }

    @Override
    Money cancel(Booking booking) {
        booking.state = CANCELLED;
        return Money.ZERO;
    }
},
PAID {
    @Override
    void use(Booking booking) {
        booking.state = USED;
    }

    @Override
    Money cancel(Booking booking) {
        booking.state = CANCELLED;
        return booking.amount;
    }
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

**Zmiany:**

1. Powstaje nowy plik `ReportFormat.java`: interfejs z metodami `header(label)`, `row(title, tickets, revenue)` i `footer(tickets, revenue)` - trzy haki formatu wyjęte z hierarchii.
2. Powstają nowe pliki `CsvFormat.java` i `TextFormat.java` z ciałami haków skopiowanymi z `DailyCsvReport` i `DailyTextReport`; najpierw porównujemy je z kopiami w `Weekly*`, żeby nie zostawić rozjechanej wersji.
3. `ScreeningReport` dostaje pole `format` i konstruktor `protected ScreeningReport(ReportFormat format)`; w `render` wywołania `header(...)`, `row(...)` i `footer(...)` stają się `format.header(...)`, `format.row(...)`, `format.footer(...)`.
4. Trzy abstrakcyjne haki formatu znikają z bazy, a ich implementacje z czterech podklas; każda podklasa woła `super(new CsvFormat())` albo `super(new TextFormat())`.

**Przed:**

```java
StringBuilder text = new StringBuilder(header(label(day)));
Line total = new Line(0, Money.ZERO);
for (Map.Entry<String, Line> entry : byTitle.entrySet()) {
    text.append(row(entry.getKey(), entry.getValue().tickets(), entry.getValue().revenue()));
    total = total.plus(entry.getValue());
}
return text.append(footer(total.tickets(), total.revenue())).toString();
...
/** Hak formatu. */
protected abstract String header(String label);
```

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

**Zmiany:**

1. Ten sam ruch dla haków `includes` i `label`: powstaje nowy plik `ReportPeriod.java` - interfejs z tymi dwiema metodami.
2. Powstają nowe pliki `DailyPeriod.java` i `WeeklyPeriod.java`; powtórzone wyliczanie poniedziałku wydzielamy w `WeeklyPeriod` do prywatnej metody `monday(day)` (Extract Method).
3. Konstruktor bazy przyjmuje dwie strategie: `ScreeningReport(ReportPeriod period, ReportFormat format)`; w `render` haki okresu zamieniają się w `period.includes(...)` i `period.label(...)`.
4. Haki `includes` i `label` znikają z bazy i podklas; podklasy to już tylko konfiguracja konstruktora.

**Przed:**

```java
public final class WeeklyTextReport extends ScreeningReport {
    public WeeklyTextReport() {
        super(new TextFormat());
    }

    @Override
    protected boolean includes(LocalDate day, LocalDate saleDay) {
        LocalDate monday = day.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return !saleDay.isBefore(monday) && !saleDay.isAfter(monday.plusDays(6));
    }

    @Override
    protected String label(LocalDate day) {
        LocalDate monday = day.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return "tydzien " + monday + ".." + monday.plusDays(6);
    }
}
```

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

**Zmiany:**

1. `ScreeningReport` przestaje być abstrakcyjna: klasa staje się `final`, konstruktor publiczny, a z `render` schodzi zbędne już `final`.
2. `Reports` dostaje dwie mapy: `PERIODS` (DAILY, WEEKLY) i `FORMATS` (CSV, TEXT); `of` zamiast zagnieżdżonego `switch` sprawdza najpierw okres, potem format i składa `new ScreeningReport(chosenPeriod, chosenFormat)`.
3. Pliki czterech podklas (`DailyCsvReport`, `DailyTextReport`, `WeeklyCsvReport`, `WeeklyTextReport`) znikają (bezpieczne usunięcie po migracji `Reports`).

**Przed:**

```java
public static ScreeningReport of(String period, String format) {
    return switch (period) {
        case "DAILY" -> switch (format) {
            case "CSV" -> new DailyCsvReport();
            case "TEXT" -> new DailyTextReport();
            default -> throw unknownFormat(format);
        };
        case "WEEKLY" -> switch (format) {
            case "CSV" -> new WeeklyCsvReport();
            case "TEXT" -> new WeeklyTextReport();
            default -> throw unknownFormat(format);
        };
        default -> throw new IllegalArgumentException("unknown period: " + period);
    };
}
```

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
- Znika typ podklasy: kod z `instanceof WeeklyCsvReport`, rejestracja w DI po typie albo zapisana nazwa klasy przestają działać - poszukaj takich użyć, zanim usuniesz podklasy.
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

**Zmiany:**

1. Powstaje nowy plik `RefundHandler.java`: interfejs funkcyjny z metodą `Optional<RefundDecision> handle(RefundRequest request)` - pusty wynik znaczy "pytaj dalej".
2. Powstaje nowy plik `AfterScreeningHandler.java`: pierwsza gałąź (seans już się rozpoczął) przenosi się do jego `handle`, opakowana w `Optional.of(...)` / `Optional.empty()` (Extract Method, potem przeniesienie do nowej klasy).
3. `RefundApprover` dostaje pole `afterScreening = new AfterScreeningHandler()`; w `decide` pierwszą gałąź zastępuje pytanie ogniwa o decyzję, reszta `if/else` bez zmian. Walidacja kwoty zostaje przed ogniwem.

**Przed:**

```java
if (!request.requestedAt().isBefore(request.screeningStart())) {
    return RefundDecision.rejected("seans juz sie rozpoczal");
} else if (Duration.between(request.requestedAt(), request.screeningStart()).toHours() >= 24) {
    return RefundDecision.approvedBy("AUTO");
}
```

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

**Zmiany:**

1. Ten sam ruch dla pozostałych gałęzi: powstają nowe pliki `AutoRefundHandler.java`, `CashierHandler.java` i `ManagerHandler.java`; limity kwot trafiają do konstruktorów (Introduce Parameter): `new CashierHandler(CASHIER_LIMIT)`, `new ManagerHandler(MANAGER_LIMIT)`.
2. Pole `afterScreening` zastępuje lista `handlers = List.of(...)` z czterema ogniwami w kolejności reguł.
3. `decide` przegląda listę i zwraca pierwszą obecną decyzję, a po liście odmowę "brak uprawnionego zatwierdzajacego"; łańcuch `if/else` znika.

**Przed:**

```java
Optional<RefundDecision> decision = afterScreening.handle(request);
if (decision.isPresent()) {
    return decision.get();
} else if (Duration.between(request.requestedAt(), request.screeningStart()).toHours() >= 24) {
    return RefundDecision.approvedBy("AUTO");
} else if (!request.amount().isGreaterThan(CASHIER_LIMIT)) {
    return RefundDecision.approvedBy("CASHIER");
} else if (!request.amount().isGreaterThan(MANAGER_LIMIT)) {
    return RefundDecision.approvedBy("MANAGER");
}
return RefundDecision.rejected("brak uprawnionego zatwierdzajacego");
```

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

**Zmiany:**

1. `RefundHandler.handle` zmienia sygnaturę: zwraca `RefundDecision` zamiast `Optional<RefundDecision>`.
2. Każde ogniwo dostaje pole `final RefundHandler next` przez konstruktor i woła `next.handle(request)`, gdy samo nie obsłuży żądania.
3. Powstaje nowy plik `NoApproverHandler.java`: ogniwo końcowe, które przejmuje odmowę "brak uprawnionego zatwierdzajacego" z końca listy.
4. `RefundApprover` zamiast listy trzyma głowę łańcucha i dostaje dwa konstruktory: domyślny składa standardową ścieżkę, drugi przyjmuje dowolną głowę. Pętla w `decide` znika - zostaje walidacja kwoty i `chain.handle(request)`.

**Przed:**

```java
private final List<RefundHandler> handlers = List.of(
        new AfterScreeningHandler(),
        new AutoRefundHandler(),
        new CashierHandler(CASHIER_LIMIT),
        new ManagerHandler(MANAGER_LIMIT));
...
for (RefundHandler handler : handlers) {
    Optional<RefundDecision> decision = handler.handle(request);
    if (decision.isPresent()) {
        return decision.get();
    }
}
return RefundDecision.rejected("brak uprawnionego zatwierdzajacego");
```

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

**Zmiany:**

1. Powstaje nowy plik `PaymentConfirmation.java`: rekord `PaymentConfirmation(Outcome outcome, String code, Money authorized)` z enumem `Outcome { APPROVED, DECLINED, ERROR }`, walidacją w konstruktorze kompaktowym (kod niepusty, kwota nieujemna, tylko APPROVED może autoryzować pieniądze) i metodami `approved()` oraz `isPartialFor(requested)`.
2. W `PaymentService` powstaje prywatna statyczna metoda `parse(raw)` (Extract Method): rozbiera odpowiedź raz, zaraz po wywołaniu terminala; sprawdza liczbę pól i znany status, dla złej odpowiedzi rzuca `malformed terminal response: ...`.
3. Mapa `responses` typu `Map<String, String>` staje się mapą `confirmations` typu `Map<String, PaymentConfirmation>`; `pay`, `receipt` i `totalAuthorized` czytają pola obiektu zamiast rozbierać tekst.

**Przed:**

```java
public String pay(String bookingId, Money amount) {
    long grosze = amount.amount().movePointRight(2).longValueExact();
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
    return "PAID " + parts[1];
}
```

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

**Zmiany:**

1. Powstaje nowy plik `TerminalResponseMapper.java`: metoda `parse` przenosi się tam z serwisu jako `toConfirmation(raw)` (Move Method), a przeliczenie złotych na grosze staje się metodą `toGrosze(amount)`.
2. `PaymentService` dostaje pole `mapper = new TerminalResponseMapper()` i woła je po obu stronach wywołania terminala; prywatna metoda `parse` i zmienna `grosze` znikają z serwisu.

**Przed:**

```java
public String pay(String bookingId, Money amount) {
    long grosze = amount.amount().movePointRight(2).longValueExact();
    PaymentConfirmation confirmation = parse(terminal.authorize(bookingId, grosze));
    ...
}

private static PaymentConfirmation parse(String raw) {
    String[] parts = raw.split(";", -1);
    if (parts.length != 3) {
        throw new IllegalArgumentException("malformed terminal response: " + raw);
    }
    ...
}
```

**Po:**

```java
String raw = terminal.authorize(bookingId, mapper.toGrosze(amount));
PaymentConfirmation confirmation = mapper.toConfirmation(raw);
```

**Uruchom:** test zielony; `S29SolutionTest.malformedResponseIsRejectedAtTheBoundary` sprawdza `OK;AUTH-R1`, `MAYBE;X;0`, `OK;;4500` i `OK;AUTH-R1;abc`.
**Co powiedzieć:** mapper to jedyne miejsce, które zna format `STATUS;KOD;KWOTA` i grosze w obie strony. Serwis nie zna już formatu, ale wciąż sam woła `LegacyTerminalClient` i składa wywołanie z mapperem.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s29 1 2`

### Krok 3: port domeny i adapter

**Zmiany:**

1. Powstaje nowy plik `PaymentTerminal.java`: interfejs portu z metodą `PaymentConfirmation authorize(String bookingId, Money amount)`.
2. Powstaje nowy plik `LegacyTerminalAdapter.java`: implementacja portu, która łączy `LegacyTerminalClient` z `TerminalResponseMapper`.
3. Konstruktor `PaymentService` zmienia sygnaturę: zamiast `LegacyTerminalClient` przyjmuje `PaymentTerminal`; pole `mapper` znika z serwisu, a `pay` dostaje gotowe `PaymentConfirmation` z `terminal.authorize(bookingId, amount)`.
4. W korzeniu kompozycji `PaymentModule.paymentService` owijamy klienta adapterem.

**Przed:**

```java
public PaymentService(LegacyTerminalClient terminal) {
    this.terminal = Objects.requireNonNull(terminal, "terminal");
}

public String pay(String bookingId, Money amount) {
    String raw = terminal.authorize(bookingId, mapper.toGrosze(amount));
    PaymentConfirmation confirmation = mapper.toConfirmation(raw);
    ...
}
```

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

**Zmiany:**

1. Całą orkiestrację z `OnlineShop.buy` wydzielamy do metody `book(order)` (Extract Method), a opłatę rezerwacyjną zamieniamy na parametr (Introduce Parameter): `book(order, feePerSeat)`.
2. Powstaje nowy plik `BookingFacade.java`: metoda `book` przenosi się tam (Move Method), a fasada dostaje cztery podsystemy w konstruktorze.
3. `OnlineShop` zamiast czterech podsystemów dostaje w konstruktorze fasadę; zostaje mu tylko stała `BOOKING_FEE`. `Cinema` tworzy fasadę i podaje ją sklepowi.

**Przed:**

```java
public String buy(TicketOrder order) {
    Money total = prices.price(order.ticketType()).plus(BOOKING_FEE).times(order.seats().size());
    try {
        seats.reserve(order.screening(), order.seats());
    } catch (IllegalStateException exception) {
        return "ERROR " + exception.getMessage();
    }
    if (!payments.charge(order.card(), total)) {
        seats.release(order.screening(), order.seats());
        return "ERROR payment declined";
    }
    mailer.send(order.email(), "Bilety " + order.screening() + " " + String.join(",", order.seats()) + " = " + total);
    return "OK " + total;
}
```

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

**Zmiany:**

1. Konstruktor `BoxOffice` zmienia sygnaturę: zamiast czterech podsystemów przyjmuje `BookingFacade`.
2. `sell` woła `booking.book(order, Money.ZERO)`; skopiowana orkiestracja znika razem ze swoim błędem (brak zwolnienia miejsc po odmowie).
3. W `Cinema` kasa dostaje fasadę zamiast podsystemów.

**Przed:**

```java
public String sell(TicketOrder order) {
    Money total = prices.price(order.ticketType()).times(order.seats().size());
    try {
        seats.reserve(order.screening(), order.seats());
    } catch (IllegalStateException exception) {
        return "ERROR " + exception.getMessage();
    }
    if (!payments.charge(order.card(), total)) {
        return "ERROR payment declined";
    }
    mailer.send(order.email(), "Bilety " + order.screening() + " " + String.join(",", order.seats()) + " = " + total);
    return "OK " + total;
}
```

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

**Zmiany:**

1. Klasy `SeatRegistry`, `PriceList`, `PaymentClient` i `TicketMailer` oraz ich metody tracą modyfikator `public` i stają się pakietowe; konstruktor fasady też przestaje być publiczny - kanały sprzedaży dostają gotowy obiekt.
2. W `book` wywołanie `charge` otaczamy blokiem `try/finally`: flaga `paid`, a w `finally` zwolnienie miejsc, gdy płatność się nie udała (odmowa albo wyjątek bramki). Dopiero po bloku zwracamy `ERROR payment declined`.

**Przed:**

```java
if (!payments.charge(order.card(), total)) {
    seats.release(order.screening(), order.seats());
    return "ERROR payment declined";
}
```

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

**Zmiany:**

1. Powstaje nowy plik `FilmLicense.java`: rekord `FilmLicense(filmCode, title, active, validFrom, validTo, sharePercent, minimumGuarantee)` z niezmiennikami w konstruktorze kompaktowym (koniec nie przed początkiem, udział 0-100, gwarancja nieujemna) i metodami `covers(day)`, `allowsScreeningOn(day)`, `dueFor(revenue)`.
2. W serwisie dekodowanie pól rekordu wydzielamy (Extract Method) do prywatnej metody `toLicense(record)`: status na `boolean`, daty z `BASIC_ISO_DATE` na `LocalDate`, procent z tekstu na `int`, grosze na `Money`, normalizacja tytułu.
3. `canScreen` i `settle` liczą reguły na licencji (`allowsScreeningOn`, `covers`, `dueFor`); parsowanie znika z reguł.

**Przed:**

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

**Zmiany:**

1. Powstaje nowy plik `DistributorTranslator.java`: metoda `toLicense` przenosi się tam z serwisu (Move Method); serwis dostaje pole `translator = new DistributorTranslator()`.
2. W translatorze wydzielamy metody pomocnicze `title(upper)` i `date(legacy)` (Extract Method), a kod statusu staje się stałą `ACTIVE` (Introduce Constant).
3. W `date` łapiemy `DateTimeParseException` i rzucamy `IllegalArgumentException("invalid legacy date: ...")` z zachowaną przyczyną - błąd zgłasza się w języku granicy.

**Przed:**

```java
private static FilmLicense toLicense(DistributorRecord record) {
    String title = record.titleUpper().charAt(0) + record.titleUpper().substring(1).toLowerCase(Locale.ROOT);
    return new FilmLicense(
            record.filmCode(),
            title,
            record.statusCode().equals("A"),
            LocalDate.parse(record.validFrom(), DateTimeFormatter.BASIC_ISO_DATE),
            LocalDate.parse(record.validTo(), DateTimeFormatter.BASIC_ISO_DATE),
            Integer.parseInt(record.share().replace("%", "").strip()),
            new Money(new BigDecimal(record.minGuaranteeCents()).movePointLeft(2)));
}
```

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

**Zmiany:**

1. Powstaje nowy plik `FilmLicenses.java`: interfejs portu z metodą `Optional<FilmLicense> find(String filmCode)`.
2. Powstaje nowy plik `DistributorAcl.java`: implementacja portu łącząca `DistributorFeed` z tłumaczem; `null` z obcego systemu staje się `Optional.empty()`.
3. Konstruktor `SettlementService` zmienia sygnaturę: zamiast `DistributorFeed` przyjmuje `FilmLicenses`; pole `translator` znika z serwisu, a `canScreen` i `settle` pracują na `Optional<FilmLicense>`.
4. `DistributorTranslator` traci `public` i staje się pakietowo-prywatny, a `SettlementModule.settlementService` owija klienta w `DistributorAcl`.

**Przed:**

```java
public final class SettlementService {
    private final DistributorFeed feed;
    private final DistributorTranslator translator = new DistributorTranslator();

    public SettlementService(DistributorFeed feed) {
        this.feed = Objects.requireNonNull(feed, "feed");
    }

    public boolean canScreen(String filmCode, LocalDate day) {
        DistributorRecord record = feed.fetch(filmCode);
        return record != null && translator.toLicense(record).allowsScreeningOn(day);
    }
```

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

**Zmiany:**

1. Powstaje nowy plik `SelectionMediator.java`: interfejs z metodą `seatToggled(String seat, boolean selected)`.
2. `SeatSelectionScreen` implementuje `SelectionMediator` (jak "dialog" w GoF): reakcja na kliknięcie z `SeatMap.click` (aktualizacja koszyka, przeliczenie sumy, odświeżenie przycisku) przenosi się do metody `seatToggled` ekranu.
3. `SeatMap` dostaje mediatora w konstruktorze; jej pola `basket`, `summary`, `payButton` i metoda `connect(...)` znikają, a `click` ustala `nowSelected` i zgłasza `mediator.seatToggled(seat, nowSelected)`.
4. Pozostałe komponenty wciąż łączą się wprost przez `connect(...)` w konstruktorze ekranu - jeden komponent naraz.

**Przed:**

```java
public void click(String seat) {
    if (!hall.contains(seat) || sold.contains(seat)) {
        return;
    }
    if (selected.remove(seat)) {
        basket.drop(seat);
    } else {
        selected.add(seat);
        basket.add(seat);
    }
    summary.recalculate(basket.seats());
    payButton.refresh(basket.seats().size(), summary.total());
}
```

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

**Zmiany:**

1. Interfejs mediatora dostaje zdarzenia `seatRemoved`, `basketCleared`, `voucherApplied` i `seatsPaid(List<String>)`.
2. `Basket`, `PriceSummary` i `PayButton` dostają mediatora w konstruktorze i zgłaszają zdarzenia zamiast odświeżać sąsiadów; ich metody `connect(...)` znikają - poza przyciskiem, który zachowuje `connect(basket, summary)`, bo jeszcze sam zbiera dane do płatności.
3. W ekranie powtórzoną parę "przelicz sumę, odśwież przycisk" wydzielamy (Extract Method) do prywatnej metody `refresh()`, zastępując wszystkie duplikaty; `seatsPaid` oznacza miejsca jako sprzedane i czyści koszyk.

**Przed:**

```java
@Override
public void seatToggled(String seat, boolean selected) {
    if (selected) {
        basket.add(seat);
    } else {
        basket.drop(seat);
    }
    summary.recalculate(basket.seats());
    payButton.refresh(basket.seats().size(), summary.total());
}
```

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

**Zmiany:**

1. W interfejsie mediatora `seatsPaid(List<String>)` zmienia sygnaturę na `String payRequested()`.
2. Zbieranie miejsc i kwoty oraz składanie komunikatu "Zaplacono ..." przenosi się z `PayButton.press` do `payRequested()` w ekranie; tam też zostaje oznaczenie miejsc jako sprzedanych i czyszczenie koszyka.
3. `PayButton` traci pola `basket` i `summary` oraz metodę `connect(...)`; zostaje mu reguła aktywności (1-4 miejsca) i zgłoszenie `mediator.payRequested()`.
4. Pusty już konstruktor ekranu znika - komponenty znają wyłącznie mediatora.

**Przed:**

```java
public String press() {
    if (!enabled) {
        return "Nieaktywny: " + label;
    }
    List<String> paid = basket.seats();
    Money amount = summary.total();
    mediator.seatsPaid(paid);
    return "Zaplacono " + amount + " za " + String.join(",", paid);
}
```

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
