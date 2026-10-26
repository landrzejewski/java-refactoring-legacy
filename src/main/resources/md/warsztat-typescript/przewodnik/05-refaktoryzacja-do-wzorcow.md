# Moduł 5. Refaktoryzacja do wzorców - warsztat CineLegacy (TypeScript): przewodnik prowadzącego

Trzydzieści dwie małe sceny w domenie kina CineLegacy. Każda pokazuje jedną refaktoryzację do wzorca (albo usunięcie wzorca) ze slajdów: od pytania "czy zawsze warto?" i usuwania zbędnych wzorców, przez ryzyka zmiany kontraktu i automaty IDE, po wybór między podobnymi wzorcami (Command, Strategy, State; Strategy i Template Method; Chain of Responsibility i Decorator), granice systemu (Adapter, Boundary Object, Facade, Anti-Corruption Layer) oraz Builder, Observer, Mediator i Visitor. Każda scena ma kod wyjściowy `start` z zapachem lub pułapką i gotowe snapshoty `stepN` po każdym ruchu, więc w dowolnym momencie można przeskoczyć do kolejnego kroku. Test równoważności (vitest) pilnuje, że wzorzec wprowadzamy bez zmiany obserwowalnego zachowania, a tam, gdzie zachowanie celowo się zmienia (moment wyboru strategii, nowy niezmiennik buildera, wygaszony wariant, naprawiony błąd skopiowanej orkiestracji, zaostrzona walidacja na granicy), osobny test to dokumentuje.

Kod portu TypeScript leży w `typescript/src/workshop/m5/sNN_.../{start,step1,...}`, testy w `typescript/test/workshop/m5/sNN_.../`, a pokaz prowadzimy w VS Code. Tam, gdzie TypeScript nie ma odpowiednika mechanizmu Javy (rekordy, `sealed`, `final`, enum z polami i metodami, przeciążenia konstruktorów, widoczność pakietowa, refleksja), scena używa najbliższego idiomu TypeScriptu, a akapit **Różnica względem Javy** mówi, co i dlaczego wygląda inaczej. Wspólne dla całego modułu: rekordy Javy to klasy z polami `readonly`, `Optional<T>` to `T | undefined`, `BigDecimal` to `Decimal` z decimal.js albo `Money` z `typescript/src/workshop/shared/Money.ts`, a w oczekiwanych tekstach testów zamiast nazw wyjątków Javy stoją nazwy klas błędów portu (`IllegalArgumentError`, `IllegalStateError`). Testy struktury, które w Javie używają refleksji, w porcie sprawdzają eksporty modułów, pliki kroku, tekst źródła albo `Object.values` pól.

## Jak prowadzić pokaz

```bash
scripts/warsztat.sh --lang ts list m5              # sceny i kroki modułu 5
scripts/warsztat.sh --lang ts test m5/s08          # testy jednej sceny
scripts/warsztat.sh --lang ts test m5              # wszystkie sceny modułu (838 testów)
scripts/warsztat.sh --lang ts diff m5/s08 1 2      # co zmienia krok 2 względem kroku 1 (0 = start)
scripts/warsztat.sh --lang ts diff m5/s08 1 2 --word  # to samo, zmiany podświetlone w obrębie linii
scripts/warsztat.sh --lang ts jump m5/s08 2        # kopiuje step2 do start (przeskok, gdy brakuje czasu)
scripts/warsztat.sh --lang ts next m5/s08          # następny krok do start + podsumowanie zmian
scripts/warsztat.sh next                           # kolejny krok tej samej sceny (język i scena zapamiętane)
scripts/warsztat.sh prev                           # krok wstecz
scripts/warsztat.sh status                         # który krok jest teraz w start
scripts/warsztat.sh --lang ts reset m5/s08         # przywraca start z repozytorium
```

- Zasada: **jeden krok - jeden test - jedno zdanie komentarza**. Po każdym ruchu w VS Code uruchamiamy test sceny (skrypt albo rozszerzenie Vitest); zielony pasek jest dowodem, że wzorzec nie zmienił kontraktu.
- **Test to nie kompilacja.** Vitest uruchamia kod bez sprawdzania typów, więc tam, gdzie scena opiera się na kontroli kompilatora (wyczerpujący `switch`, klasa bez `export`, brak `add` na liściu, zmieniona sygnatura), uruchom też `npm run typecheck` w katalogu `typescript` albo patrz na podkreślenia w VS Code.
- **Krok po kroku bez numerów:** `scripts/warsztat.sh --lang ts next m5/sNN` wstawia do `start` gotowy następny krok i wypisuje, co się zmieniło. `prev` cofa o krok, `status` mówi, który krok jest teraz w `start`. Skrypt pamięta język i ostatnią scenę, więc potem wystarczy samo `next`. Ręczne zmiany w `start` zostają nadpisane - o to chodzi, gdy krok na żywo się rozjechał.
- Pracujemy w katalogu `start`. Gdy coś pójdzie nie tak, `next` (albo `jump` do właściwego kroku), a po scenie `reset`.
- Test równoważności przechodzi przez `start` i wszystkie kroki. Jeśli uczestnicy pracują równolegle, to ten sam test jest ich kryterium ukończenia.
- Testy wołają sceny przez stabilne punkty wejścia (klient w rodzaju `PriceBoard`, `CancellationDesk`, `PaymentServices`, `Reports`, `Cinema`, `PaymentModule`, `SettlementModule` albo zachowany konstruktor), dzięki czemu `jump` do dowolnego kroku przechodzi. Testy dokumentujące pułapkę samego `start` (s11, s17, s30) po `jump` są pomijane (`context.skip(...)`, vitest pokazuje je jako "skipped") - to zamierzone.
- Refaktoryzacje w VS Code: zaznaczenie i ⌃⇧R (Refactor...) daje Extract to method / Extract to function / Extract to constant, F2 to Rename Symbol, ⌘. (Quick Fix) m.in. Implement interface i Add missing member, a Move to a new file / Move to file przenosi deklaracje najwyższego poziomu. Change Signature, Safe Delete, Extract Interface, Extract Delegate, Inline Method, Push Members Down ani Extract Superclass nie ma - te ruchy robimy ręcznie, a listę miejsc do poprawy daje `npm run typecheck` (albo Find All References ⇧⌥F12).
- Motyw przewodni modułu: **najpierw kontrakt, potem diagram klas**. W każdej scenie zaczynamy od pytania "co jest obserwowalne?" (wyjątek, kolejność efektów, moment wyboru, format trwały, typ i moment błędu na granicy). Siatkę bezpieczeństwa z sekcji 3 slajdów ćwiczy każda scena: test równoważności przed pierwszym ruchem, test różnicowy w s10, testy struktury w s26-s32.

## Mapa scen

Numery scen zostają bez zmian (katalogi i skrypt). Katalogi są względne wobec `typescript/src/workshop/`, testy leżą w odpowiednim katalogu pod `typescript/test/workshop/`.

| Scena | Temat ze slajdów | Kroki | Katalog | Czas |
| --- | --- | --- | --- | --- |
| s20 | 4.2. Najpierw rodzaj zmienności, potem wzorzec; 18.1. Mapa decyzji (1/2); 18.2. Mapa decyzji (2/2); 18.3. Lista kontrolna | 3 | `m5/s20_decisionmap` | ~12 min |
| s21 | 17.5. Wzorzec jako decyzja odwracalna; 17.2. Refaktoryzacje, które usuwają wzorzec; 5.2. Kiedy wzorzec się nie opłaca | 3 | `m5/s21_reversiblepattern` | ~12 min |
| s22 | 17.3. Usunięcie pośrednika; 17.2. Refaktoryzacje, które usuwają wzorzec | 3 | `m5/s22_middleman` | ~8 min |
| s23 | 5.3. Refaktoryzacja a zmiana kontraktu; 5.4. Co trzeba zachować przy wprowadzaniu wzorca; 3.10. Bezpieczna pętla refaktoryzacji do wzorca | 3 | `m5/s23_contractchange` | ~10 min |
| s24 | 3.6. Refaktoryzacje automatyczne w IDE: co dają, a czego nie widzą; 3.7. Zmiana nazwy i wyodrębnienie metody w drodze do wzorca | 3 | `m5/s24_rename` | ~12 min |
| s25 | 3.7. Zmiana nazwy i wyodrębnienie metody w drodze do wzorca | 3 | `m5/s25_extractmethod` | ~15 min |
| s26 | 6.1. Strategia, Polecenie czy Stan? Kto wybiera wariant i kiedy; 6.7. Trzy osie w jednej klasie: każda dostaje własny mechanizm; 6.8. Typowe pomyłki przy wyborze | 3 | `m5/s26_variability` | ~15 min |
| s01 | 6.1. Strategia, Polecenie czy Stan? Kto wybiera wariant i kiedy; 6.2. Strategia: sygnał w kodzie; 6.3. Strategia: kroki i wynik | 3 | `m5/s01_strategy` | ~12 min |
| s08 | 6.4. Stan: sygnał i tabela przejść; 6.5. Stan: wynik | 3 | `m5/s08_state` | ~15 min |
| s15 | 6.6. Polecenie: łańcuch warunków zamieniony na rejestr poleceń | 3 | `m5/s15_command` | ~12 min |
| s27 | 7.2. Gdy punkty zaczepienia obejmują dwie niezależne osie; 7.3. Zastąpienie dziedziczenia delegacją: osie jako strategie; 7.4. Strategia czy Metoda szablonowa? | 3 | `m5/s27_templatetostrategy` | ~12 min |
| s16 | 7.1. Utworzenie Metody szablonowej: wspólny szkielet, różne kroki | 2 | `m5/s16_templatemethod` | ~8 min |
| s28 | 8.3. Łańcuch `if/else`, w którym kolejność jest regułą; 8.4. Łańcuch zobowiązań: ogniwo i złożenie łańcucha; 8.5. Łańcuch zobowiązań czy Dekorator? | 3 | `m5/s28_chain` | ~12 min |
| s07 | 8.1. Przeniesienie dodatków do Dekoratora; 8.2. Dekorator: na co uważać | 3 | `m5/s07_decorator` | ~12 min |
| s14 | 9.1. Ujednolicenie interfejsów Adapterem; 9.2. Co adapter naprawdę musi przetłumaczyć | 3 | `m5/s14_adapter` | ~12 min |
| s29 | 9.3. Surowe dane z zewnątrz w kodzie domeny; 9.4. Obiekt graniczny: sprawdzone dane na granicy systemu; 9.5. Adapter czy obiekt graniczny? | 3 | `m5/s29_boundaryobject` | ~12 min |
| s04 | 10.1. Ukrycie klas za Fabryką; 10.3. Fabryka jako strażnik spójności | 3 | `m5/s04_encapsulatefactory` | ~10 min |
| s05 | 10.2. Wyodrębnienie klasy fabryki; 10.3. Fabryka jako strażnik spójności | 3 | `m5/s05_extractfactory` | ~10 min |
| s30 | 11.1. Klienci sami koordynują podsystemy; 11.2. Wyodrębnienie Fasady: jedna operacja zamiast znajomości kroków | 3 | `m5/s30_facade` | ~12 min |
| s31 | 11.3. Obcy model w regułach domeny; 11.4. Warstwa antykorupcyjna: port, tłumacz i model domenowy; 11.5. Fasada a warstwa antykorupcyjna | 3 | `m5/s31_anticorruptionlayer` | ~12 min |
| s06 | 12.1. Ukrycie budowy Kompozytu za Budowniczym; 12.2. Budowniczy: kiedy i na co uważać | 3 | `m5/s06_builder` | ~12 min |
| s09 | 13.1. Zastąpienie zakodowanych powiadomień Obserwatorem; 13.2. Obserwator: zasady powiadamiania muszą być jawne | 3 | `m5/s09_observer` | ~15 min |
| s32 | 14.1. Komponenty połączone każdy z każdym; 14.2. Wprowadzenie Mediatora: reguły współpracy w jednym miejscu; 14.3. Obserwator czy Mediator? | 3 | `m5/s32_mediator` | ~12 min |
| s19 | 15.1. Odwiedzający i macierz zmian; 15.2. Odwiedzający albo hierarchia `sealed` i `switch` | 3 | `m5/s19_visitor` | ~12 min |
| s02 | 16.1. Zastąpienie instrukcji warunkowej polimorfizmem; 6.8. Typowe pomyłki przy wyborze | 3 | `m5/s02_polymorphism` | ~12 min |
| s03 | 16.2. Zastąpienie kodu typu klasą | 3 | `m5/s03_typecode` | ~12 min |
| s10 | 16.3. Zastąpienie ukrytego drzewa Kompozytem; 3.3. Test równoważności przed zmianą i po niej | 3 | `m5/s10_implicittree` | ~15 min |
| s11 | 16.4. Kompozyt przezroczysty czy bezpieczny | 2 | `m5/s11_safecomposite` | ~8 min |
| s12 | 16.5. Jeden element lub wiele oraz wyodrębnienie Kompozytu (zastąpienie rozróżnienia „jeden lub wiele” Kompozytem) | 3 | `m5/s12_onemany` | ~10 min |
| s13 | 16.5. Jeden element lub wiele oraz wyodrębnienie Kompozytu (wyodrębnienie Kompozytu) | 2 | `m5/s13_extractcomposite` | ~8 min |
| s18 | 16.6. Przeniesienie zbierania wyników do parametru zbierającego | 3 | `m5/s18_collectingparameter` | ~8 min |
| s17 | 16.7. Ograniczenie liczby instancji Singletonem; 17.4. Wchłonięcie Singletona | 3 | `m5/s17_singleton` | ~10 min |

Liczby domeny (ceny formatów, zniżki, VIP, okulary, opłata online, punkty lojalnościowe, zwroty, VAT) są wspólne dla całego warsztatu. Wartości spoza wspólnej listy (program "tydzień studenta" 50%, ubezpieczenie biletu 4.00, "tani wtorek" -30%, weekend +2.00, ceny premier i maratonów, limity zatwierdzania zwrotów 100.00 i 1000.00, autoryzacja częściowa terminala do 300.00, voucher KINO10) są przykładowe i opisane w komentarzach TSDoc sceny.

## Wybór scen na 3-dniowe szkolenie

Wszystkie sceny razem to ponad 6 h samego pokazu (około 370 min) - więcej, niż zmieści się w jednym dniu szkolenia razem ze slajdami. Na szkoleniu 3-dniowym pokazujemy **rdzeń: 11 scen, około 2 h 20 min**, po jednej scenie z kluczowych sekcji slajdów, w kolejności agendy:

1. s20 mapa decyzji (12 min) - ustawia pytanie "jaki rodzaj zmienności?" na cały moduł.
2. s21 wzorzec jako decyzja odwracalna (12 min): usunięcie wzorca, który przestał być potrzebny.
3. s23 zmiana kontraktu (10 min) - "przy okazji" zmienia dwie rzeczy naraz.
4. s25 Extract Method (15 min) - przepływ danych i moment, w którym IDE odmawia.
5. s26 pulpit kasjera (15 min) - Command, Strategy i State w jednej klasie.
6. s27 raporty sprzedaży (12 min) - Template Method z dwiema osiami → Strategy.
7. s28 zatwierdzanie zwrotów (12 min) - Chain of Responsibility a Decorator.
8. s29 odpowiedź terminala (12 min) - Adapter i Boundary Object.
9. s30 zakup w sklepie i w kasie (12 min) - Facade i świadoma naprawa błędu kopii.
10. s31 licencje dystrybutora (12 min) - Facade a ACL.
11. s32 ekran wyboru miejsc (12 min) - Mediator i ryzyko God Mediator.

**Sceny opcjonalne**, gdy grupa pracuje szybciej albo pyta o konkretny wzorzec: s22 i s17 (usuwanie wzorca: Remove Middle Man, Singleton i jego odwrotność), s24 (Rename i nazwy poza kodem), s01, s08 i s15 (każdy z trzech wzorców sekcji 6 osobno, w szczegółach), s16 (klasyczny Template Method), s07 (Decorator), s14 (Adapter), s04 i s05 (jeden blok "Factory" z porównaniem obu refaktoryzacji), s06 (Builder), s09 (Observer, dobry kontrast do s32), s19 (Visitor). Sceny z sekcji 16 "Pozostałe refaktoryzacje do wzorców" (s02, s03, s10-s13, s18) zostawiamy do samodzielnej pracy w parach albo jako zadanie domowe - s11 i s13 mają po 2 kroki i dobrze sprawdzają się jako krótkie ćwiczenie. Na podsumowanie wróć do s20 i poproś salę o przypisanie każdej pokazanej sceny do wiersza mapy decyzji.

## Scena s01. Replace Conditional Logic with Strategy - polityka zniżek

**Temat ze slajdów:** 6.1. Strategia, Polecenie czy Stan? Kto wybiera wariant i kiedy; 6.2. Strategia: sygnał w kodzie; 6.3. Strategia: kroki i wynik
**Katalog:** `typescript/src/workshop/m5/s01_strategy` · **Test:** `scripts/warsztat.sh --lang ts test m5/s01` (`S01EquivalenceTest.test.ts`, `S01SolutionTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `TicketPricer` wybiera zniżkę łańcuchem `if` po nazwie programu kina, wymieszanym z walidacją ceny. Wprowadzamy interfejs `DiscountPolicy` najpierw jako strategię przejściową, przenosimy każdy program do osobnej klasy, a wybór programu trafia do `DiscountPrograms` i konstruktora.

**Zasada:** Strategy zamyka wymienne warianty tego samego obliczenia za wspólnym interfejsem, gdy wariant wybiera się niezależnie od klasy obiektu. Wspólna walidacja zostaje w kontekście, a strategie są bezstanowe. Samo `if` nie uzasadnia wzorca - przy prostym, stabilnym warunku interfejs pogarsza czytelność.

**Efekt:** Nowy program zniżek to nowa strategia (nawet literał obiektu) i jeden wpis w `switch`, bez zmiany `TicketPricer`. Zachowanie świadomie się zmienia: wybór w konstruktorze zamraża decyzję, więc nieznany program zgłasza się teraz przed ujemną ceną.

**Różnica względem Javy:** `@FunctionalInterface DiscountPolicy` to w TypeScripcie zwykły interfejs z jedną metodą `discount`, więc strategię "w locie" zapisujemy literałem obiektu `{ discount: (b, type) => ... }`, a nie gołą lambdą. Program w `PriceRequest` jest typu `string | null` (w Javie `null` w rekordzie).

### Co widzimy

`TicketPricer.price(base, ticketType, program)` wybiera algorytm zniżki łańcuchem `if` po nazwie programu skonfigurowanego w kinie (STANDARD, STUDENT_WEEK, PREMIERE). W tej samej metodzie jest walidacja ceny i programu. Algorytm jest wybierany niezależnie od klasy obiektu - to klasyczny kandydat na Strategy.

```typescript
if (program === 'PREMIERE') {
  discount = Money.ZERO;
} else if (program === 'STUDENT_WEEK' && ticketType === 'S') {
  discount = base.percent(50);
} else if (program === 'STANDARD' || program === 'STUDENT_WEEK') {
  let percent: number;
  switch (ticketType) {
    case 'N': percent = 0; break;
    case 'S': percent = 25; break;
    ...
  }
  discount = base.percent(percent);
} else {
  throw new IllegalArgumentError(`unknown program: ${program}`);
}
```

Klientem jest `PriceBoard.priceFor(request)` - przez niego woła test. Test równoważności to **tabela decyzji**: każda gałąź, typ nieznany, program nieznany i `null`. Zwróć uwagę na przypadek "PREMIERE nie sprawdza typu" - start przyjmuje typ `X` bez błędu i refaktoryzacja musi to zachować.

### Krok 1: Extract Interface + strategia przejściowa

**W IDE:** utwórz plik `DiscountPolicy.ts` z interfejsem `DiscountPolicy` i metodą `discount(base: Money, ticketType: string): Money`. Zaznacz łańcuch `if`, ⌃⇧R > Extract to method in class 'TicketPricer', nazwa `legacyDiscount` (parametry `base`, `ticketType`, `program`, zwracane `Money`; metoda nie używa `this`, więc w porcie jest `private static`). W `price` utwórz strategię jako literał obiektu i wywołaj ją.
**Po:**

```typescript
const policy: DiscountPolicy = { discount: (b, type) => TicketPricer.legacyDiscount(b, type, program) };
return base.minus(policy.discount(base, ticketType));
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s01` - zielone.
**Co powiedzieć:** pierwszy commit jest mały i odwracalny: kontrakt strategii istnieje, ale deleguje do starego kodu. Nie zaczynamy od tworzenia wszystkich klas wzorca.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s01 0 1`

### Krok 2: przeniesienie gałęzi do strategii

**W IDE:** dla każdej gałęzi osobno: nowa klasa `implements DiscountPolicy` (⌘. na podkreślonej nazwie klasy - Implement interface 'DiscountPolicy'), ciało skopiowane z gałęzi, test. Kolejno `StandardDiscount`, `PremiereDiscount`, `StudentWeekDiscount` (ta ostatnia deleguje nie-studentów do `StandardDiscount` przekazanego w konstruktorze). Na koniec `legacyDiscount` zastąp metodą `policyFor(program)` ze `switch`.
**Po:**

```typescript
private static policyFor(program: string): DiscountPolicy {
  switch (program) {
    case 'STANDARD': return new StandardDiscount();
    case 'STUDENT_WEEK': return new StudentWeekDiscount(new StandardDiscount());
    case 'PREMIERE': return new PremiereDiscount();
    default: throw new IllegalArgumentError(`unknown program: ${program}`);
  }
}
```

**Uruchom:** test po każdej przeniesionej gałęzi.
**Co powiedzieć:** walidacja zostaje w kontekście, strategie są bezstanowe. `switch` nie zniknął - przeniósł się w jedno miejsce wyboru, a to jest w porządku.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s01 1 2`

### Krok 3: wybór strategii w korzeniu kompozycji

**W IDE:** utwórz klasę `DiscountPrograms` z prywatnym konstruktorem i przenieś do niej `policyFor` jako `static forName(program)` (VS Code nie przenosi metod między klasami - wytnij i wklej), instancje strategii jako statyczne stałe (współdzielone); sprawdzenie `null` programu przechodzi razem z wyborem. Change Signature robimy ręcznie: `TicketPricer` dostaje strategię w konstruktorze, `price(base, ticketType)` traci parametr programu, a `npm run typecheck` wskaże klienta `PriceBoard` do poprawy.
**Po:**

```typescript
return new TicketPricer(DiscountPrograms.forName(request.program))
  .price(request.base, request.ticketType);
```

**Uruchom:** test zielony; `S01SolutionTest` pokazuje nowy program zniżek jako literał obiektu (`{ discount: (base) => base.percent(50) }`) bez zmiany kontekstu.
**Co powiedzieć:** wybór w konstruktorze **zamraża decyzję** i przesuwa moment błędu: w start ujemna cena zgłaszała się przed nieznanym programem, teraz jest odwrotnie. To dokumentuje test `choosingInConstructorMovesTheUnknownProgramErrorEarlier` - taka zmiana wymaga świadomej zgody, nie jest "za darmo".
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s01 2 3`

### Rozwiązanie i uzasadnienie

`step3`: `TicketPricer` zna tylko `DiscountPolicy`, a nazwy programów zna `DiscountPrograms`. Nowy program zniżek to nowa strategia i wpis w jednym `switch`. Kontekst trzyma wspólną walidację.

### Pułapki

- Goły typ funkcyjny `(base: Money, ticketType: string) => Money` zamiast nazwanego interfejsu - działa, ale gubi nazwę pojęcia w sygnaturach i komunikatach kompilatora.
- Strategia z polami zmienianymi podczas liczenia, a potem współdzielona - w JavaScripcie nie przez wątki, ale przez wywołania przeplatane `await` albo wielokrotne użycie tej samej instancji.
- Porównywanie strategii przez `===` - każdy literał obiektu i każda funkcja strzałkowa to nowa tożsamość.
- Strategia jako zamknięta unia typów zamiast interfejsu - punkt rozszerzeń ma być otwarty, a interfejs przyjmuje także literał obiektu z testu.

### Pytanie do sali

Czy przy dwóch programach i jednym `if` Strategy nadal byłaby uzasadniona? Co musiałoby się zmieniać, żeby tak?

## Scena s02. Replace Conditional with Polymorphism - rodzaj seansu

**Temat ze slajdów:** 16.1. Zastąpienie instrukcji warunkowej polimorfizmem; 6.8. Typowe pomyłki przy wyborze
**Katalog:** `typescript/src/workshop/m5/s02_polymorphism` · **Test:** `scripts/warsztat.sh --lang ts test m5/s02` (`S02EquivalenceTest.test.ts`, `S02SolutionTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `Screening` ma pole `kind` i ten sam `switch` w trzech metodach, a pole `value` znaczy raz minuty filmu, raz liczbę filmów. Wydzielamy podtyp dla każdego rodzaju seansu po kolei, aż zostaje zamknięta unia typów z nazwanymi danymi.

**Zasada:** Replace Conditional with Polymorphism przenosi zachowanie zależne od rodzaju do podtypów, gdy selektor opisuje trwały rodzaj obiektu. Jeśli rodzaj zmienia się w czasie życia obiektu, to State, a jeśli algorytm wybiera klient - Strategy. Zaczynamy od znalezienia wszystkich miejsc tworzenia i deserializacji.

**Efekt:** Switche w zachowaniu znikają, zostaje jeden w `fromRow`, gdzie należy wiedza o konstrukcji, a dane mają jednoznaczne nazwy. Kosztem zamkniętej unii jest to, że nowy rodzaj psuje kompilację każdego wyczerpującego `switch`, także poza modułem.

**Różnica względem Javy:** TypeScript nie ma zamkniętych hierarchii klas (`sealed ... permits`), więc w krokach 1-2 baza jest zwykłą (potem abstrakcyjną) klasą, a zamknięcie daje dopiero krok 3: unia dyskryminowana `type Screening = RegularScreening | PremiereScreening | MarathonScreening` z polem `kind`. W krokach 1-2 podklasy są zdefiniowane w `Screening.ts`, a pliki `MarathonScreening.ts` itd. tylko je re-eksportują - w ESM cykl importów `Screening -> podklasa -> Screening` (przez `extends`) kończy się `ReferenceError`. Chronione akcesory `title()` z Javy to pole `protected readonly title`.

### Co widzimy

`Screening` ma pole `kind` (enum `Kind`: REGULAR, PREMIERE, MARATHON) i ten sam `switch` w trzech metodach: `label()`, `durationMinutes()`, `price()`. Pole `value` znaczy raz "minuty filmu", raz "liczba filmów". Rodzaj seansu jest **trwałą cechą obiektu** - nie zmienia się w czasie życia, więc to nie State, a klient go nie wybiera, więc to nie Strategy.

```typescript
durationMinutes(): number {
  switch (this.kind) {
    case Kind.REGULAR: return 20 + this.value;
    case Kind.PREMIERE: return 30 + this.value;
    case Kind.MARATHON: return this.value * 120 + (this.value - 1) * 15;
  }
}
```

Tworzenie jest w jednym miejscu: `Screening.fromRow(row: ScreeningRow)` (mapowanie wiersza z bazy). Zawsze najpierw szukamy miejsc tworzenia i deserializacji (w VS Code: Find All References ⇧⌥F12 na konstruktorze).

### Krok 1: Extract Subclass dla jednej gałęzi

**W IDE:** konstruktor bazy z `private` na `protected`, `title` jako `protected readonly`. Pod klasą `Screening` (w tym samym pliku) utwórz `MarathonScreening extends Screening` z polem `films`, nadpisz trzy metody z modyfikatorem `override` (⌘. na klasie - Add missing members pomaga tylko przy metodach abstrakcyjnych, tu wpisujemy je ręcznie) i skopiuj do nich gałęzie MARATHON. Plik `MarathonScreening.ts` tylko re-eksportuje klasę. W `fromRow` twórz podklasę dla `'MARATHON'`. W bazie gałęzie MARATHON rzucają `IllegalStateError`.
**Po:**

```typescript
case 'MARATHON': return new MarathonScreening(row.title, row.value);
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s02` - zielone.
**Co powiedzieć:** jedna gałąź naraz, jeden test kontraktowy dla wszystkich rodzajów. Martwa gałąź rzuca wyjątek zamiast po cichu liczyć bzdury.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s02 0 1`

### Krok 2: pozostałe podklasy, baza abstrakcyjna

**W IDE:** powtórz ruch dla REGULAR i PREMIERE. Potem klasa bazowa i jej metody jako `abstract`, usuń pole `kind` i enum `Kind` (ręcznie - `npm run typecheck` pokaże pozostałe użycia). Jedynym `switch` zostaje `fromRow`.
**Po:**

```typescript
export abstract class Screening {
  protected constructor(protected readonly title: string) {}
  ...
  abstract durationMinutes(): number;
```

**Uruchom:** test zielony.
**Co powiedzieć:** `switch` w miejscu tworzenia jest właściwy - to jest wiedza o konstrukcji. Znikają za to switche w zachowaniu.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s02 1 2`

### Krok 3: forma TypeScript - unia dyskryminowana

**W IDE:** zamień klasę bazową na unię `type Screening = RegularScreening | PremiereScreening | MarathonScreening` i obiekt `Screening` o tej samej nazwie z funkcją `fromRow` (typ i wartość mogą dzielić nazwę, więc klienci nie zmieniają importów). Każdy rodzaj dostaje literał `readonly kind = 'MARATHON'` i nazwane dane (`runtime`, `films`), a wspólne metody opisuje interfejs `ScreeningMembers`. `value` znika. Podtypy nie dziedziczą już po bazie, więc mogą wrócić do własnych plików.
**Po:**

```typescript
export class MarathonScreening implements ScreeningMembers {
  readonly kind = 'MARATHON';

  constructor(readonly title: string, readonly films: number) {}

  durationMinutes(): number {
    return this.films * 120 + (this.films - 1) * 15;
  }
```

**Uruchom:** test zielony; `S02SolutionTest` zawiera `switch (screening.kind)` klienta zakończony `default: return assertNever(screening)` - dodanie czwartego rodzaju do unii psuje kompilację (`npm run typecheck`).
**Co powiedzieć:** zamknięty zestaw rodzajów daje kontrolę kompilatora, ale też koszt: nowy rodzaj psuje kompilację każdego wyczerpującego `switch`, także poza modułem.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s02 2 3`

### Rozwiązanie i uzasadnienie

Trzy niezmienne klasy z własnymi danymi i zachowaniem, połączone w unię dyskryminowaną. Znaczenie pól jest jednoznaczne, a test kontraktowy przechodzi przez wszystkie rodzaje, łącznie z nieznanym rodzajem w danych.

### Pułapki

- Pominięcie miejsc tworzenia (ORM, `JSON.parse` z ręcznym mapowaniem, fabryka w innym module) - obiekt powstanie jako zły podtyp albo zwykły obiekt bez metod.
- Wywołanie metody nadpisywanej z konstruktora bazy - podklasa widzi niezainicjalizowane pola (w TypeScripcie pola z parametrów konstruktora podklasy są przypisywane dopiero po `super(...)`).
- Pola i metody `static` nie są polimorficzne względem instancji - "nadpisanie" stałej w podklasie nie zmienia tego, co widzi kod bazy wołający `Screening.STALA`.

### Pytanie do sali

Seans może zmienić się z "premiery" w "zwykły" po tygodniu. Czy to nadal polimorfizm, czy już State?

## Scena s03. Replace Type Code with Class - format jako typ

**Temat ze slajdów:** 16.2. Zastąpienie kodu typu klasą
**Katalog:** `typescript/src/workshop/m5/s03_typecode` · **Test:** `scripts/warsztat.sh --lang ts test m5/s03` (`S03EquivalenceTest.test.ts`, `S03SolutionTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** Format seansu jest surowym `number` z CSV, a wiedza o kodach jest rozsiana po trzech metodach. Zamieniamy liczbę na typ `Format` tuż po odczycie, przenosimy do niego etykietę, cenę i okulary, a tłumaczenie kodu trwałego zamykamy w mapperze `FormatCodes`.

**Zasada:** Replace Type Code with Class zastępuje prymitywny kod typem, który przejmuje walidację, normalizację i operacje pojęcia - co nie oznacza automatycznie hierarchii podklas. Zamknięty zestaw stałych instancji wystarcza dla małego, zamkniętego zestawu, pełniejsza klasa z fabryką jest lepsza przy aliasach i kodach zewnętrznych. Na granicy trwałości zostaje stabilny kod, nigdy pozycja na liście wartości ani `toString()`.

**Efekt:** Poza mapperem żaden kod nie operuje na liczbach 1, 2, 3, a zapis CSV jest bez zmian. Zmiana formatu trwałego (np. na kody tekstowe) dotknie tylko mappera, ale to osobna decyzja, nie część tej refaktoryzacji.

**Różnica względem Javy:** `enum` w TypeScripcie nie ma pól ani metod, więc javowy enum z polami (`Format`) to klasa z prywatnym konstruktorem, statycznymi instancjami `TWO_D`, `THREE_D`, `IMAX`, metodą `values()` i polem `name` typu unii literałów. W kroku 1 `switch (format.name)` jest wyczerpujący bez `default` (odpowiednik switcha po enumie), a akcesory `label()`, `basePrice()`, `requiresGlasses()` to pola `readonly`. `Integer.parseInt` zastępuje pomocnik sceny `parseInteger` (`Integers.ts`), który rzuca `IllegalArgumentError` z komunikatem Javy (`For input string: "x"`) - `Number.parseInt` zwróciłby `NaN` albo po cichu uciął `"3x"` do 3. Odpowiednikiem `ordinal()` jest `Format.values().indexOf(format)`, a `EnumMap` - `ReadonlyMap<Format, number>`.

### Co widzimy

Format seansu jest surowym `number` (1=2D, 2=3D, 3=IMAX) czytanym z CSV `Diuna;3`. Wiedza o kodzie jest w trzech metodach, a `needsGlasses` w ogóle nie waliduje kodu. Plik CSV musi nadal przechowywać liczbę - to jest **granica trwałości**.

```typescript
private basePrice(formatCode: number): Money {
  switch (formatCode) {
    case ScreeningCsv.FORMAT_2D: return Money.of('25.00');
    ...
    default: throw new IllegalArgumentError(`unknown format code: ${formatCode}`);
  }
}
```

### Krok 1: nowy typ i konwersja na granicy

**W IDE:** utwórz klasę `Format` ze stałymi instancjami `TWO_D` (1), `THREE_D` (2), `IMAX` (3), polem `code`, `values()` i `fromCode(code)` rzucającym ten sam wyjątek co start. W `describe` zamień `number` na `Format` zaraz po odczycie; typy parametrów metod prywatnych zmień ręcznie na `Format` (VS Code nie ma Change Signature - kompilator wskaże resztę). Switche przejdź na `format.name`. Do CSV zapisuj `format.code`.
**Po:**

```typescript
const format = Format.fromCode(parseInteger(parts[1]!.trim()));
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s03` - zielone, łącznie z "nieznany kod" i "kod nie jest liczbą".
**Co powiedzieć:** zamknięty zestaw instancji wystarcza, bo formatów jest mało. Trwały kod to jawne pole, nigdy pozycja w `values()` ani `name`.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s03 0 1`

### Krok 2: Move Method do typu

**W IDE:** przenieś `label`, `basePrice`, `needsGlasses` do typu jako pola stałych instancji (ręcznie - wartości z gałęzi `switch` trafiają do wywołań konstruktora). Klient pyta obiekt zamiast wykonywać `switch`.
**Po:**

```typescript
static readonly TWO_D = new Format('TWO_D', 1, '2D', '25.00', false);
static readonly THREE_D = new Format('THREE_D', 2, '3D', '32.00', true);
static readonly IMAX = new Format('IMAX', 3, 'IMAX', '40.00', false);
```

**Uruchom:** test zielony.
**Co powiedzieć:** nowy typ przejmuje walidację, normalizację i operacje pojęcia. Nie oznacza to automatycznie hierarchii klas.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s03 1 2`

### Krok 3: mapper migracyjny na granicy trwałości

**W IDE:** nowa klasa `FormatCodes` z `fromCode`/`toCode` (mapa `ReadonlyMap<Format, number>`), usuń `code` i `fromCode` z `Format`. `ScreeningCsv` rozmawia z mapperem, reszta kodu wyłącznie z `Format`.
**Po:**

```typescript
const format = FormatCodes.fromCode(parseInteger(parts[1]!.trim()));
... + `|csv=${title};${FormatCodes.toCode(format)}`;
```

**Uruchom:** test zielony; `S03SolutionTest` sprawdza, że pozycja IMAX w `Format.values()` to 2, a kod trwały to 3.
**Co powiedzieć:** gdy kiedyś przejdziemy na kody tekstowe ("IMAX" w bazie), zmieni się tylko mapper - może przez pewien czas czytać oba formaty. Zmiana formatu trwałego to osobna decyzja, nie część tej refaktoryzacji.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s03 2 3`

### Rozwiązanie i uzasadnienie

Typ domenowy bez wiedzy o bazie plus mapper na granicy. Zamknięty zestaw stałych instancji, bo formatów jest mało. Pełną klasę z fabryką i walidacją wartości wybralibyśmy przy aliasach, kodach zewnętrznych albo gdy lista wartości pochodzi z konfiguracji.

### Pułapki

- Zapis pozycji w `values()` (albo wartości numerycznego `enum` TypeScriptu) do bazy - dodanie stałej w środku przestawia dane.
- Zapis `toString()` - ktoś go "upiększy" i CSV przestanie się wczytywać.
- Zmiana komunikatu wyjątku przy nieznanym kodzie - to też obserwowalne zachowanie (logi, monitoring).

### Pytanie do sali

Kiedy wybralibyście pełną klasę z fabryką zamiast zamkniętego zestawu stałych dla formatu? Co w domenie kina mogłoby to wymusić?

## Scena s04. Encapsulate Classes with Factory - bilety

**Temat ze slajdów:** 10.1. Ukrycie klas za Fabryką; 10.3. Fabryka jako strażnik spójności
**Katalog:** `typescript/src/workshop/m5/s04_encapsulatefactory` · **Test:** `scripts/warsztat.sh --lang ts test m5/s04` (`S04EquivalenceTest.test.ts`, `S04SolutionTest.test.ts`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** `BoxOffice` tworzy `StandardTicket` i `VipTicket` przez `new` i sam zna regułę VIP (rząd 10+). Przekierowujemy tworzenie do metod fabryki `Tickets`, przenosimy tam regułę wyboru biletu, a na końcu odbieramy klasom konkretnym `export`.

**Zasada:** Encapsulate Classes with Factory ukrywa klasy konkretne za publiczną granicą tworzenia, gdy klient potrzebuje tylko wspólnego interfejsu. Tworzenie przekierowujemy pojedynczo, a widoczność ograniczamy dopiero na końcu. Nie mylić z Extract Factory Class, która wydziela tworzenie z klasy o innej odpowiedzialności.

**Efekt:** Publiczne zostają tylko `Ticket` i `Tickets`, więc nowy rodzaj biletu nie zmienia żadnego klienta. Ryzykiem zostają miejsca tworzenia, których kompilator nie widzi (dynamiczne `import()`, kontener DI, deserializacja), a w bibliotece publicznej potrzebny byłby etap `@deprecated`.

**Różnica względem Javy:** TypeScript nie ma widoczności pakietowej. Jedyną granicą jest moduł (plik), więc w kroku 3 `StandardTicket` i `VipTicket` przenosimy do `tickets/Tickets.ts` jako klasy bez `export` - prywatne dla modułu - a ich osobne pliki znikają. `S04SolutionTest` zamiast refleksji (`Modifier.isPublic`) sprawdza, że plików `StandardTicket.ts`/`VipTicket.ts` w `step3` nie ma, moduł fabryki eksportuje tylko `Tickets`, klasy są zadeklarowane bez `export`, a `Ticket.ts` eksportuje interfejs.

### Co widzimy

`BoxOffice` (inny katalog niż bilety) tworzy `StandardTicket` i `VipTicket` przez `new` w dwóch miejscach i zna regułę VIP (rząd 10+). Klasy biletów są eksportowane, choć klient potrzebuje tylko interfejsu `Ticket`.

```typescript
tickets.push(row >= 10
  ? new VipTicket(title, base, row)
  : new StandardTicket(title, base, row));
```

### Krok 1: metody tworzące

**W IDE:** w katalogu `tickets` utwórz klasę `Tickets` z prywatnym konstruktorem i statycznymi `standard(...)` i `vip(...)` zwracającymi `Ticket`. Zastąp każde `new` w `BoxOffice` wywołaniem metody tworzącej (VS Code nie ma Replace Constructor with Factory Method - robimy to ręcznie, Find All References ⇧⌥F12 na konstruktorze daje listę miejsc).
**Po:**

```typescript
if (row >= 10) {
  return Tickets.vip(title, base, row);
}
return Tickets.standard(title, base, row);
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s04` - zielone.
**Co powiedzieć:** przekierowujemy tworzenie pojedynczo; klient nie importuje już klas konkretnych.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s04 0 1`

### Krok 2: decyzja do fabryki

**W IDE:** warunek `row >= 10` przenieś do `Tickets.forSeat(title, base, row)` (Move Method ręcznie), liczbę 10 zaznacz i ⌃⇧R > Extract to readonly field, nazwa `VIP_FROM_ROW` (w porcie `private static readonly`). `sellAll` woła `sell`.
**Po:**

```typescript
sell(title: string, base: Money, row: number): Ticket {
  return Tickets.forSeat(title, base, row);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** reguła "który bilet" jest teraz w jednym miejscu - to właściwy dom dla `if` o konstrukcji.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s04 1 2`

### Krok 3: ograniczenie widoczności

**W IDE:** przenieś `StandardTicket` i `VipTicket` do `tickets/Tickets.ts` (⌃⇧R > Move to file na deklaracji klasy), usuń słowo `export` i skasuj puste pliki. `npm run typecheck` podkreśli każde użycie spoza modułu - to inwentaryzacja, którą robi za nas kompilator.
**Po:**

```typescript
/** Krok 3: klasa prywatna modułu, miejsce VIP: +10.00 (w Javie: rekord pakietowy). */
class VipTicket implements Ticket {
  constructor(
    readonly title: string,
    readonly base: Money,
    readonly row: number,
  ) {}
```

**Uruchom:** test zielony; `S04SolutionTest` sprawdza, że poza modułem fabryki klas konkretnych nie widać.
**Co powiedzieć:** widoczność ograniczamy na końcu, gdy wszyscy klienci już przeszli. W bibliotece publicznej potrzebny byłby etap `@deprecated` (TSDoc), zanim eksport zniknie.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s04 2 3`

### Rozwiązanie i uzasadnienie

Publiczne są tylko `Ticket` i `Tickets`. Nowy rodzaj biletu (np. miejsce dla osoby na wózku) nie zmienia żadnego klienta.

### Pułapki

- Zapomniane miejsca tworzenia: dynamiczne `import()`, kontener DI, deserializacja JSON wymagająca dostępnej klasy.
- Fabryka rosnąca w globalny rejestr wszystkiego ("ServiceLocator").
- Fabryka tworząca warianty z wyprzedzeniem - kosztowne efekty uruchamiane wcześniej niż w starej gałęzi.

### Pytanie do sali

Co się zmienia, jeśli klasy biletów są używane przez framework ORM?

## Scena s05. Extract Factory Class - tworzenie rezerwacji

**Temat ze slajdów:** 10.2. Wyodrębnienie klasy fabryki; 10.3. Fabryka jako strażnik spójności
**Katalog:** `typescript/src/workshop/m5/s05_extractfactory` · **Test:** `scripts/warsztat.sh --lang ts test m5/s05` (`S05EquivalenceTest.test.ts`, `S05SolutionTest.test.ts`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** `ReservationService` pilnuje zajętości miejsc, ale też buduje rezerwacje (numer, opłata online, termin ważności), i to w dwóch kopiach. Wyciągamy tworzenie do metody, potem do klasy `ReservationFactory`, a na końcu przekazujemy fabrykę do serwisu przez konstruktor.

**Zasada:** Extract Factory Class wydziela wiedzę o tworzeniu obiektów z klasy, która ma inną główną odpowiedzialność. Fabryka to zwykła zależność wstrzykiwana konstruktorem, a nie globalny rejestr ani zestaw metod statycznych. Kolejność efektów przy tworzeniu (tu: numer pobierany przed walidacją kanału) jest częścią kontraktu.

**Efekt:** Fabryka wie, jak powstaje rezerwacja, serwis wie, kiedy wolno ją utworzyć, i każdą da się testować osobno. "Spalanie" numeru przy błędnym kanale zostaje celowo - jego naprawa byłaby zmianą zachowania.

**Różnica względem Javy:** TypeScript nie ma przeciążonych konstruktorów, więc dwa konstruktory Javy `ReservationService(Clock)` i `ReservationService(ReservationFactory)` to w kroku 3 jeden konstruktor przyjmujący `ReservationFactory | Clock` (rozróżnienie przez `instanceof ReservationFactory`). W oczekiwaniach testu równoważności nazwy wyjątków to nazwy klas TypeScriptu (`IllegalStateError: seat taken: A1`, `IllegalArgumentError: ...`); treści komunikatów i kolejność są jak w Javie.

### Co widzimy

`ReservationService` pilnuje zajętości miejsc, ale też buduje rezerwację: numer z licznika, opłata online 2.00 za bilet, termin ważności 15 minut. Ta wiedza jest skopiowana w `reserve` i `reserveGroup`. Subtelność: numer jest pobierany **przed** walidacją kanału - błędny kanał "spala" numer.

```typescript
this.sequence++;
const id = `R${this.sequence}`;
if (channel !== 'ONLINE' && channel !== 'BOX_OFFICE') {
  throw new IllegalArgumentError(`unknown channel: ${channel}`);
}
```

Test to scenariusz kilku rezerwacji na jednym serwisie: zajęte miejsce nie zużywa numeru, nieznany kanał zużywa.

### Krok 1: Extract Method dla tworzenia

**W IDE:** w `reserve` zaznacz blok od `this.sequence++` do `new Reservation(...)`, ⌃⇧R > Extract to method in class 'ReservationService', nazwa `newReservation`. W `reserveGroup` zastąp duplikat wywołaniem `this.newReservation('ONLINE', email, seats)`. Pętlę zajętości wyciągnij tak samo do `requireFree`.
**Po:**

```typescript
this.requireFree(seats);
const reservation = this.newReservation(channel, email, seats);
seats.forEach((seat) => this.takenSeats.add(seat));
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s05` - zielone.
**Co powiedzieć:** kolejność efektów zostaje: najpierw sprawdzenie miejsc, potem numer, potem walidacja kanału. Gdybyśmy przy okazji "poprawili" kolejność, test to wychwyci.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s05 0 1`

### Krok 2: Extract Class - ReservationFactory

**W IDE:** nowa klasa `ReservationFactory` (VS Code nie ma Extract Delegate - przenosimy ręcznie): metoda `newReservation` jako `create(channel, email, seats)` razem z polami `clock` i `sequence`. Serwis tworzy fabrykę w swoim konstruktorze - sygnatura konstruktora bez zmian.
**Po:**

```typescript
constructor(clock: Clock) {
  this.factory = new ReservationFactory(clock);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** klienci serwisu niczego nie zauważyli. Serwis ma jedną odpowiedzialność: dostępność miejsc.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s05 1 2`

### Krok 3: fabryka jako zależność

**W IDE:** konstruktor przyjmuje teraz `ReservationFactory | Clock`: gdy dostaje fabrykę, używa jej, gdy zegar - tworzy fabrykę jak dotąd, żeby nie ruszać klientów.
**Po:**

```typescript
constructor(factoryOrClock: ReservationFactory | Clock) {
  requireNonNull(factoryOrClock, 'factory');
  this.factory = factoryOrClock instanceof ReservationFactory
    ? factoryOrClock
    : new ReservationFactory(factoryOrClock);
}
```

**Uruchom:** test zielony; `S05SolutionTest` testuje fabrykę osobno i pokazuje współdzieloną numerację.
**Co powiedzieć:** fabryka to zwykła zależność wstrzykiwana konstruktorem, nie globalny rejestr. Dwie usługi z jedną fabryką dzielą numerację - to decyzja korzenia kompozycji.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s05 2 3`

### Rozwiązanie i uzasadnienie

`ReservationFactory` wie, jak powstaje rezerwacja (numer, opłata, termin), `ReservationService` wie, kiedy wolno ją utworzyć. Każdą da się testować osobno.

### Pułapki

- "Naprawienie" spalania numerów w ramach refaktoryzacji - to zmiana zachowania (luki w numeracji mogą być wymagane przez księgowość albo przez nią zabronione).
- Czas pobierany przez `new Date()` albo `Date.now()` zamiast wstrzykniętego `Clock` - fabryka staje się nietestowalna.
- Fabryka ze statycznymi metodami i statycznym licznikiem (albo licznikiem na poziomie modułu) - wraca globalny stan.

### Pytanie do sali

Czy numeracja rezerwacji powinna należeć do fabryki, czy do repozytorium? Co przemawia za każdą opcją?

## Scena s06. Encapsulate Composite with Builder - repertuar dnia

**Temat ze slajdów:** 12.1. Ukrycie budowy Kompozytu za Budowniczym; 12.2. Budowniczy: kiedy i na co uważać
**Katalog:** `typescript/src/workshop/m5/s06_builder` · **Test:** `scripts/warsztat.sh --lang ts test m5/s06` (`S06EquivalenceTest.test.ts`, `S06SolutionTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `WeekendPlanner` składa drzewo dzień - sala - seans ręcznie z `new` i `add`, a węzły są mutowalne. Wprowadzamy `ScheduleBuilder`: najpierw z bieżącą salą, potem z niemutowalnymi węzłami i jednorazowym `build()`, na końcu z zagnieżdżoną funkcją strzałkową dla każdej sali.

**Zasada:** Encapsulate Composite with Builder ukrywa budowę drzewa za API, które mówi "co" zbudować, a nie "jak". Jest uzasadniony, gdy budowa jest wieloetapowa i ma własne niezmienniki (kolejność, pusta grupa, unikalność nazw). Builder jest jednorazowy, a funkcja gałęzi wykonuje się synchronicznie dokładnie raz.

**Efekt:** Wcięcia kodu planera odpowiadają poziomom repertuaru, a gotowego drzewa nie da się zmodyfikować. Unikalność nazw sal to świadomie dodany nowy niezmiennik - start pozwalał na duplikaty, więc dla błędnych danych zachowanie się zmienia.

**Różnica względem Javy:** rekordy z `List.copyOf` to klasy z polami `readonly` i tablicą zamrożoną `Object.freeze([...])`, więc test niemutowalności oczekuje `TypeError` przy `push` (w Javie `UnsupportedOperationException`). Zagnieżdżona klasa `ScheduleBuilder.HallBuilder` to osobna eksportowana klasa `HallBuilder` w tym samym pliku, której konstruktor dostaje listę seansów należącą do `ScheduleBuilder`, a `Consumer<HallBuilder>` to typ `(hall: HallBuilder) => void`. `LinkedHashMap` zastępuje `Map`, który też zachowuje kolejność wstawiania.

### Co widzimy

`WeekendPlanner.plan(date)` buduje drzewo dzień - sala - seans ręcznie: dużo `new` i `add`, a o `day.add(hall2)` łatwo zapomnieć. Kod nie przypomina kształtu repertuaru, a węzły są mutowalne.

```typescript
const hall2 = new Hall('Sala 2');
if (weekend) {
  hall2.add(new Screening('Kraina Lodu', LocalTime.of(10, 0)));
}
hall2.add(new Screening('Amator', LocalTime.of(17, 30)));
day.add(hall2);
```

### Krok 1: klasyczny Builder z bieżącym węzłem

**W IDE:** utwórz `ScheduleBuilder` z metodami `hall(name)`, `screening(title, hour, minute)` i `build()` zwracającymi `this` (poza `build`); builder pamięta bieżącą salę. Przepisz planera na wywołania buildera. Drzewo bez zmian.
**Po:**

```typescript
const builder = new ScheduleBuilder(date)
  .hall('Sala 1')
  .screening('Diuna', 18, 0)
  .screening('Diuna', 21, 0)
  .hall('Sala 2');
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s06` - zielone (sobota i piątek z pustą salą VIP).
**Co powiedzieć:** klient mówi "co" zbudować. Seans przed salą to teraz jawny błąd (`screening without hall`).
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s06 0 1`

### Krok 2: niemutowalne drzewo, builder jednorazowy

**W IDE:** węzły zamień na klasy z polami `readonly` i tablicą kopiowaną w konstruktorze przez `Object.freeze([...])`, usuń `add`. Builder zbiera dane (`Map<string, Screening[]>`) i tworzy węzły w `build()`; drugi `build()` rzuca `IllegalStateError`.
**Po:**

```typescript
export class Hall {
  readonly screenings: readonly Screening[];

  constructor(readonly name: string, screenings: readonly Screening[]) {
    this.screenings = Object.freeze([...screenings]);
  }
```

**Uruchom:** test zielony; `S06SolutionTest` sprawdza jednorazowość i niemutowalność.
**Co powiedzieć:** gdy builder jest jedyną drogą budowy, drzewo może stać się niemutowalne. `readonly` w typie chroni tylko w czasie kompilacji, dopiero `Object.freeze` zatrzymuje `push` w runtime. Unikalność nazw sal to **nowy niezmiennik** - start pozwalał na duplikaty. Taką regułę zatwierdzamy świadomie, bo zmienia zachowanie dla błędnych danych.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s06 1 2`

### Krok 3: zagnieżdżony builder z funkcją strzałkową

**W IDE:** `hall(name, content: (hall: HallBuilder) => void)` zamiast "bieżącej sali"; `HallBuilder` z `screening(...)`, a statyczne `ScheduleBuilder.day(date)` zamiast publicznego konstruktora. Przepisz planera.
**Po:**

```typescript
return ScheduleBuilder.day(date)
  .hall('Sala 1', (hall) => hall
    .screening('Diuna', 18, 0)
    .screening('Diuna', 21, 0))
  .hall('Sala 2', (hall) => { ... })
  .build();
```

**Uruchom:** test zielony.
**Co powiedzieć:** wcięcia kodu odpowiadają poziomom drzewa, a ukryty stan "bieżąca sala" zniknął. Kontrakt funkcji: synchroniczna (nie `async`), wywołana dokładnie raz.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s06 2 3`

### Rozwiązanie i uzasadnienie

Builder jest uzasadniony, bo budowa drzewa jest wieloetapowa i ma niezmienniki (kolejność sal, pusta sala dozwolona, unikalne nazwy). Wynik jest niemutowalny.

### Pułapki

- Builder wielokrotnego użytku, którego `build()` zwraca wewnętrzną tablicę - kolejne wywołania zmieniają już zwrócone drzewo.
- Funkcja zapamiętana i wywołana później (odroczenie) albo `async` - zmienia kolejność i wynik.
- Współdzielone poddrzewo (DAG) - liczy się tyle razy, ile ścieżek do niego prowadzi.

### Pytanie do sali

Czy pusta sala w repertuarze to poprawny stan, czy błąd? Kto powinien o tym zdecydować - builder czy model?

## Scena s07. Move Embellishment to Decorator - dodatki do biletu

**Temat ze slajdów:** 8.1. Przeniesienie dodatków do Dekoratora; 8.2. Dekorator: na co uważać
**Katalog:** `typescript/src/workshop/m5/s07_decorator` · **Test:** `scripts/warsztat.sh --lang ts test m5/s07` (`S07EquivalenceTest.test.ts`, `S07SolutionTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `Ticket` ma flagi `vip`, `glasses`, `insurance` i te same `if` w `price()` i `description()`. Wydzielamy interfejs `PricedTicket` i przenosimy każdy dodatek do dekoratora, zaczynając od ubezpieczenia, a łańcuch składamy w `TicketAssembler`.

**Zasada:** Move Embellishment to Decorator przenosi opcjonalny dodatek wokół rdzenia do obiektu, który ma ten sam kontrakt i deleguje do środka. Kolejność owijania jest zachowaniem, więc składa się ją w jednym miejscu. Dekorator nie jest przezroczysty dla `instanceof`, `constructor`, tożsamości i równości strukturalnej.

**Efekt:** Rdzeń biletu nie ma flag, a nowy dodatek to nowa klasa i linia w assemblerze. Kosztem jest utrata pytania o typ: kto chce wiedzieć, czy bilet jest VIP, potrzebuje osobnego API zamiast `instanceof VipSeat`.

**Różnica względem Javy:** dekoratory i rdzeń (w Javie rekordy) to klasy z polami `readonly`, bez `equals`. Asercje `assertNotEquals` z Javy (rekord a jego dekorator, dwa łańcuchy w innej kolejności) to w `S07SolutionTest` `not.toStrictEqual` - równość strukturalna z typem, najbliższa `equals` rekordów; ceny porównujemy przez `Money.equals`.

### Co widzimy

`Ticket` ma flagi `vip`, `glasses`, `insurance` i te same `if` w `price()` i `description()`. Większość biletów nie ma żadnego dodatku, a każdy nowy dodatek to kolejne pole. Opis ma ustaloną kolejność: VIP, okulary, ubezpieczenie.

```typescript
return `${this.title} ${this.format}`
  + (this.vip ? ' +VIP' : '')
  + (this.glasses ? ' +okulary 3D' : '')
  + (this.insurance ? ' +ubezpieczenie' : '');
```

### Krok 1: Extract Interface

**W IDE:** VS Code nie ma Extract Interface dla klasy - utwórz `PricedTicket.ts` z interfejsem `PricedTicket` (`price()` i `description()`) ręcznie, dopisz `implements PricedTicket` do `Ticket`. `TicketAssembler.assemble` zwraca interfejs.
**Po:**

```typescript
assemble(order: TicketOrder): PricedTicket {
  return new Ticket(order);
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s07` - zielone.
**Co powiedzieć:** wąski kontrakt, który spełnią i rdzeń, i dekoratory. Składanie w jednym miejscu (assembler).
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s07 0 1`

### Krok 2: pierwszy dodatek jako dekorator

**W IDE:** klasa `Insurance implements PricedTicket` z polem `readonly inner: PricedTicket`, dodająca 4.00 i " +ubezpieczenie". Usuń flagę z `Ticket` (ręcznie, `npm run typecheck` pokaże resztę), w assemblerze owiń, gdy `order.insurance`.
**Po:**

```typescript
let ticket: PricedTicket = new Ticket(order);
if (order.insurance) {
  ticket = new Insurance(ticket);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** zaczynamy od ubezpieczenia, bo w opisie jest **ostatnie** - dekorator dopisuje się na końcu. Gdybyśmy zaczęli od VIP, opis zmieniłby kolejność i test "wszystkie dodatki" by to złapał. Kolejność dekoratorów jest kontraktem.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s07 1 2`

### Krok 3: pozostałe dodatki, czysty rdzeń

**W IDE:** `VipSeat` i `Glasses3D` jako dekoratory, rdzeń jako niezmienna klasa `Ticket(title, format, base)`. Decyzja "czy okulary potrzebne" (3D i brak własnych) zostaje w assemblerze.
**Po:**

```typescript
let ticket: PricedTicket = new Ticket(order.title, order.format, order.base);
if (order.vip) {
  ticket = new VipSeat(ticket);
}
if (order.format === '3D' && !order.ownGlasses) {
  ticket = new Glasses3D(ticket);
}
if (order.insurance) {
  ticket = new Insurance(ticket);
}
```

**Uruchom:** test zielony; `S07SolutionTest` dokumentuje granice przezroczystości.
**Co powiedzieć:** `ticket instanceof VipSeat` zwraca `false`, gdy VIP jest owinięty ubezpieczeniem. Udekorowany bilet nie jest też strukturalnie równy rdzeniowi (`not.toStrictEqual`). Kto pyta o typ albo tożsamość, potrzebuje innego API (np. `features()`).
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s07 2 3`

### Rozwiązanie i uzasadnienie

Rdzeń bez flag, trzy dekoratory z tym samym kontraktem, łańcuch składany w jednej fabryce w ustalonej kolejności.

### Pułapki

- Własna metoda `equals` w dekoratorze ślepo delegująca do `inner` - łamie symetrię (`core.equals(vip)` różne od `vip.equals(core)`).
- Kod sprawdzający `instanceof`, `constructor.name`, używający obiektu jako klucza `Map`/`WeakMap` albo serializujący go `JSON.stringify` - dekorator nie jest przezroczysty.
- Dekorator z efektem ubocznym (audyt), którego błąd zasłania błąd rdzenia.

### Pytanie do sali

Kasa chce wydrukować "Miejsce VIP" na bilecie. Jak to zrobić, nie używając `instanceof VipSeat`?

## Scena s08. Replace State-Altering Conditionals with State - status rezerwacji

**Temat ze slajdów:** 6.4. Stan: sygnał i tabela przejść; 6.5. Stan: wynik
**Katalog:** `typescript/src/workshop/m5/s08_state` · **Test:** `scripts/warsztat.sh --lang ts test m5/s08` (`S08EquivalenceTest.test.ts`)
**Czas:** ~15 min

### W skrócie

**Co robimy:** `Reservation` w każdej operacji sprawdza i zmienia pole statusu, więc reguły przejść są rozsiane po metodach. Zaczynamy od tabeli przejść w teście, zastępujemy pole obiektem stanu i przenosimy `pay`, `use`, `expire`, `cancel` do stanów po jednej akcji.

**Zasada:** State pasuje, gdy zachowanie operacji zależy od bieżącego stanu obiektu i zmienia się razem z przejściami. Kontekst deleguje do obiektu stanu, a niedozwolone przejście domyślnie rzuca wyjątek - pusta metoda po cichu zmieniłaby zachowanie. Od Strategy różni się tym, że stan zmienia się w czasie życia obiektu, zwykle na skutek jego własnych operacji.

**Efekt:** Tabelę przejść da się przeczytać z kodu stanów, a `Reservation` tylko deleguje i przechowuje dane. Kolejność "obciążenie - zmiana stanu - efekt" zostaje jak w start, więc awaria bramki nadal zostawia rezerwację w stanie NEW.

**Różnica względem Javy:** Java buduje stany z prywatnego `sealed interface ReservationState` z metodami `default` i enumów-singletonów (`NewState.INSTANCE`, `PaidState.INSTANCE`, `ClosedState.USED/EXPIRED/CANCELLED`). TypeScript nie ma metod domyślnych w interfejsie ani enumów z zachowaniem, więc stan to niezmienny obiekt z polem `status` i przejściami jako funkcjami `(reservation: Reservation) => void`. Tworzy go funkcja `state(status, allowed)`, która wypełnia wszystkie przejścia domyślnie wyjątkiem i nadpisuje tylko dozwolone. Stany są polami `private static readonly` klasy `Reservation` - dzięki temu ich funkcje mogą wołać prywatne `charge`, `moveTo` i `record` (TypeScript nie ma widoczności pakietowej). Komunikaty są jak w Javie: `cannot pay in PAID`.

### Co widzimy

`Reservation` ma pole `currentStatus` i w każdej operacji (`pay`, `use`, `expire`, `cancel`) warunki sprawdzające i zmieniające status. Reguły przejść NEW -> PAID -> USED, NEW -> EXPIRED, NEW/PAID -> CANCELLED są rozsiane po metodach. `pay` najpierw obciąża bramkę, potem zmienia stan: gdy bramka rzuci wyjątek, rezerwacja zostaje NEW.

```typescript
cancel(): void {
  if (this.currentStatus === Status.NEW) {
    this.currentStatus = Status.CANCELLED;
    this.effectList.push('seats released');
  } else if (this.currentStatus === Status.PAID) {
    this.currentStatus = Status.CANCELLED;
    this.effectList.push('refund');
    this.effectList.push('seats released');
  } else {
    throw new IllegalStateError(`cannot cancel in ${this.currentStatus}`);
  }
}
```

**Najpierw tabela przejść.** `S08EquivalenceTest` zawiera tabelę 5 stanów x 4 akcje (20 wierszy) plus przypadek awarii bramki. Każdy wiersz sprawdza status końcowy, efekty w kolejności i komunikat wyjątku. Pokaż ją na rzutniku przed pierwszym ruchem.

### Krok 1: obiekt stanu zamiast pola status

**W IDE:** utwórz prywatny dla modułu interfejs `ReservationState` z polem `readonly status: Status` i stany jako stałe `private static readonly NEW`, `PAID`, `USED`, `EXPIRED`, `CANCELLED` (na razie literały `{ status: Status.NEW }`). Pole `currentStatus` (w Javie `status` - tu nazwa jest zajęta przez metodę `status()` z kontraktu `ReservationActions`) zastąp polem `state`; warunki porównują `this.state.status`. Efekty i zmianę stanu wyciągnij do `charge()`, `moveTo(...)`, `record(...)` (⌃⇧R > Extract to method in class 'Reservation').
**Po:**

```typescript
private state = Reservation.NEW;
...
this.charge();
this.moveTo(Reservation.PAID);
this.record('charged');
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s08` - 84 testy zielone.
**Co powiedzieć:** stany końcowe USED, EXPIRED i CANCELLED zachowują się tak samo (każde przejście jest błędem) - różnią się tylko statusem.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s08 0 1`

### Krok 2: pay i use do stanów

**W IDE:** w interfejsie stanu dodaj przejścia `pay` i `use` typu `Transition`. Napisz funkcję `state(status, allowed)`, która dla każdego przejścia wstawia domyślnie funkcję rzucającą `cannot <akcja> in <status>`, a potem nakłada dozwolone (`...allowed`). W `NEW` podaj `pay`, w `PAID` - `use`, przenosząc ciała gałęzi. Kontekst deleguje: `this.state.pay(this)`.
**Po:**

```typescript
private static readonly NEW: ReservationState = state(Status.NEW, {
  pay: (reservation) => {
    reservation.charge();
    reservation.moveTo(Reservation.PAID);
    reservation.record('charged');
  },
});
```

**Uruchom:** test zielony.
**Co powiedzieć:** domyślne przejście **rzuca wyjątek**, nie jest puste - pusta funkcja po cichu zmieniłaby zachowanie niedozwolonych przejść. Kolejność "obciążenie - zmiana stanu - efekt" zostaje dokładnie jak w start; test awarii bramki to pilnuje.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s08 1 2`

### Krok 3: expire i cancel do stanów

**W IDE:** ten sam ruch dla `expire` i `cancel`. Warunek `if/else if` w `cancel` rozkłada się na dwa stany: NEW (zwolnienie miejsc) i PAID (zwrot i zwolnienie).
**Po:**

```typescript
cancel(): void {
  this.state.cancel(this);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** tabelę przejść da się teraz przeczytać z kodu stanów - każdy stan wymienia tylko swoje dozwolone przejścia. Stany są bezstanowymi stałymi, dane zostają w `Reservation`.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s08 2 3`

### Rozwiązanie i uzasadnienie

Kontekst tylko deleguje. Każdy stan nadpisuje wyłącznie dozwolone przejścia, reszta rzuca wyjątek z tym samym komunikatem co start, a stan po błędzie się nie zmienia.

### Pułapki

- Zmiana stanu przed efektem zewnętrznym (albo odwrotnie) "przy okazji" - to inna semantyka przy awarii.
- Asynchroniczna bramka (`await payments.charge(...)`) - między sprawdzeniem stanu a przejściem może wejść `cancel`; jednowątkowość JavaScriptu nie daje atomowości przejścia przez `await`.
- Przy dwóch stanach i jednej akcji `switch` bywa czytelniejszy niż obiekty stanów.
- Kilka niezależnych osi stanu (płatność, obecność) w jednym zestawie stanów - eksplozja kombinacji.

### Pytanie do sali

Czym różni się State od Strategy, skoro oba to "obiekt, któremu delegujemy"?

## Scena s09. Replace Hard-coded Notifications with Observer - po opłaceniu

**Temat ze slajdów:** 13.1. Zastąpienie zakodowanych powiadomień Obserwatorem; 13.2. Obserwator: zasady powiadamiania muszą być jawne
**Katalog:** `typescript/src/workshop/m5/s09_observer` · **Test:** `scripts/warsztat.sh --lang ts test m5/s09` (`S09EquivalenceTest.test.ts`, `S09SolutionTest.test.ts`)
**Czas:** ~15 min

### W skrócie

**Co robimy:** `PaymentService.confirm` po zapisaniu opłaty na sztywno wysyła mail, SMS i nalicza punkty lojalnościowe. Wprowadzamy zdarzenie `ReservationPaid`, zamieniamy trzech odbiorców na implementacje `PaymentListener`, a subskrypcje przenosimy do korzenia kompozycji `PaymentServices`.

**Zasada:** Observer odwraca zależność: publikujący zna tylko interfejs odbiorcy, a odbiorcy są do niego rejestrowani z zewnątrz. Kontrakt musi być jawny - synchroniczność, kolejność, polityka błędów (tu fail-fast) i wyrejestrowanie. Dodanie nowych odbiorców to rozszerzenie zachowania, a nie część refaktoryzacji.

**Efekt:** Serwis nie importuje `Mailer`, `SmsGateway` ani `LoyaltyProgram`, a odbiorcy są wołani w kolejności subskrypcji jak w start. Zostaje ta sama semantyka błędów: awaria SMS przerywa naliczanie punktów, a jej zmiana byłaby osobną decyzją.

**Różnica względem Javy:** `Subscription extends AutoCloseable` to interfejs z `close()` (bez `Symbol.dispose`/`using` - lib ES2023 ich nie ma). `CopyOnWriteArrayList` zastępuje pole z niezmienną tablicą podmienianą przy `subscribe`/`close` (kopia przy zapisie, pętla iteruje po migawce), a `AtomicBoolean` - zwykła zmienna w domknięciu, bo JavaScript jest jednowątkowy. Prywatna zagnieżdżona klasa `Registration` to klasa prywatna modułu. Odbiorcy z testów to literały `{ onPaid: ... }`, a punkty liczy `Math.trunc(amount.trunc().toNumber() / 10)`.

### Co widzimy

Serwis powstaje w korzeniu kompozycji `PaymentServices.standard(mailer, sms, loyalty)`. `PaymentService.confirm` zapisuje opłatę, a potem na sztywno wysyła mail, SMS i nalicza punkty lojalnościowe (1 pkt za pełne 10.00). Semantyka do zachowania: **kolejność** mail - SMS - punkty i **fail-fast**: wyjątek w SMS przerywa, punkty nie są naliczone, ale opłata jest już zapisana.

```typescript
this.paidList.push(payment.reservationId);
this.mailer.send(payment.email,
  `Potwierdzenie platnosci ${payment.reservationId}: ${payment.amount.toString()}`);
this.sms.send(payment.phone, `Oplacono ${payment.reservationId}`);
this.loyalty.addPoints(payment.email, Math.trunc(payment.amount.amount.trunc().toNumber() / 10));
```

Test używa fake'ów zapisujących do wspólnego logu i sprawdza kolejność oraz awarie w mailu i SMS.

### Krok 1: zdarzenie i Extract Method

**W IDE:** utwórz niezmienną klasę `ReservationPaid` (pola `readonly`). Zaznacz trzy powiadomienia, ⌃⇧R > Extract to method in class 'PaymentService', nazwa `notifyPaid`, i zmień jej parametr na `event: ReservationPaid`.
**Po:**

```typescript
this.paidList.push(payment.reservationId);
this.notifyPaid(new ReservationPaid(
  payment.reservationId, payment.email, payment.phone, payment.amount));
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s09` - zielone.
**Co powiedzieć:** zdarzenie to fakt z danymi potrzebnymi odbiorcom. Jeszcze nic nie jest dynamiczne.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s09 0 1`

### Krok 2: odbiorcy jako obserwatorzy

**W IDE:** interfejs `PaymentListener` z `onPaid(event)` i trzy klasy: `MailConfirmation`, `SmsConfirmation`, `LoyaltyPoints` (każda z ciałem jednej linii z `notifyPaid`). Serwis tworzy zamrożoną tablicę odbiorców w konstruktorze w starej kolejności; `notifyPaid` to pętla **bez** `try/catch`.
**Po:**

```typescript
for (const listener of this.listeners) {
  listener.onPaid(event);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** konstruktor bez zmian, relacja wciąż "ci sami trzej odbiorcy" - to nadal czysta refaktoryzacja.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s09 1 2`

### Krok 3: subscribe i korzeń kompozycji

**W IDE:** `subscribe(listener)` zwracające `Subscription` (idempotentne `close()`), pole z niezmienną tablicą rejestracji podmienianą przy każdym zapisie. Konstruktor serwisu staje się bezargumentowy, pętla powiadomień trafia do `confirm`, a subskrypcje przenosimy do istniejącego `PaymentServices.standard(...)`.
**Po:**

```typescript
const service = new PaymentService();
service.subscribe(new MailConfirmation(mailer));
service.subscribe(new SmsConfirmation(sms));
service.subscribe(new LoyaltyPoints(loyalty));
```

**Uruchom:** test zielony; `S09SolutionTest` pokazuje podwójną subskrypcję, idempotentne wyrejestrowanie i nowego odbiorcę.
**Co powiedzieć:** każde `subscribe` tworzy osobny obiekt `Registration`, a `close` usuwa go po tożsamości (`r !== registration`). Gdybyśmy trzymali samego słuchacza i filtrowali po nim, zamknięcie jednej z dwóch subskrypcji tego samego słuchacza usunęłoby obie. Dodanie czwartego odbiorcy (push) to już **rozszerzenie** zachowania, zatwierdzane osobno.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s09 2 3`

### Rozwiązanie i uzasadnienie

Serwis zna tylko `PaymentListener`. Kontrakt jest jawny: synchronicznie, w kolejności subskrypcji, fail-fast, iteracja po migawce.

### Pułapki

- "Przy okazji" dodany `try/catch` wokół każdego odbiorcy - zmienia politykę błędów (punkty naliczone mimo awarii SMS).
- Asynchroniczne powiadamianie (`queueMicrotask`, `setTimeout`, odbiorca `async` bez `await`) - inna kolejność, a błąd odbiorcy ląduje jako nieobsłużone odrzucenie zamiast przerwać `confirm`.
- Odbiorca usunięty w trakcie publikacji może być jeszcze wywołany w bieżącej (migawka).
- Niezamknięta subskrypcja trzyma przy życiu graf obiektów.

### Pytanie do sali

Czy błąd wysyłki SMS powinien przerywać naliczanie punktów? Kto o tym decyduje i gdzie to zapisać?

## Scena s10. Replace Implicit Tree with Composite - zestawy baru

**Temat ze slajdów:** 16.3. Zastąpienie ukrytego drzewa Kompozytem; 3.3. Test równoważności przed zmianą i po niej
**Katalog:** `typescript/src/workshop/m5/s10_implicittree` · **Test:** `scripts/warsztat.sh --lang ts test m5/s10` (`S10EquivalenceTest.test.ts`, `S10DifferentialTest.test.ts`)
**Czas:** ~15 min

### W skrócie

**Co robimy:** Zestaw combo to zagnieżdżona tablica, w której pierwszy element jest nazwą, a `BarMenu` powtarza sprawdzanie typu elementu (`typeof`, `Array.isArray`). Budujemy obok jawny Composite (`Product`, `Combo`) z mapperem ze starego formatu, a potem przenosimy na niego `price` i `render` po jednej operacji.

**Zasada:** Replace Implicit Tree with Composite zamienia drzewo ukryte w konwencji danych na jawne typy liścia i węzła. Mapper tłumaczy stary format i zgłasza błąd dla danych, których nie da się wiernie odwzorować. Stara i nowa reprezentacja żyją obok siebie, a test różnicowy potwierdza zgodność, ale nie zastępuje niezależnych oczekiwań.

**Efekt:** `BarMenu` nie sprawdza już typów elementów, a komunikaty błędów zostają jak w start. Format trwały (zagnieżdżone tablice) się nie zmienia - jego zmiana to osobna decyzja z osobnym testem.

**Różnica względem Javy:** `List<?>` to `readonly unknown[]`, a `instanceof String`/`instanceof List` - `typeof element === 'string'` i `Array.isArray(element)`. `sealed interface MenuItem permits Product, Combo` to unia `type MenuItem = Product | Combo`. `StringBuilder` w `render(depth, text)` zastępuje bufor `string[]` łączony `join('')`, a prywatne przeciążenie `render` w `BarMenu` nazywa się `renderTo`. `Product` przechowuje cenę w polu `amount` i udostępnia metodę `price()` (pole i metoda nie mogą mieć tej samej nazwy). Test różnicowy używa klasy `JavaRandom` - wiernej kopii generatora `java.util.Random` - więc losuje dokładnie te same 500 drzew co w Javie.

### Co widzimy

Zestaw combo to zagnieżdżona tablica: pierwszy element to nazwa, kolejne to `'nazwa=cena'` albo podtablice. `BarMenu.price` i `BarMenu.render` powtarzają sprawdzanie typu elementu, a `render` dla każdego węzła liczy cenę od nowa.

```typescript
['Zestaw Duo', 'Popcorn L=18.00',
  ['Napoje', 'Cola 0.5=9.00', 'Cola 0.5=9.00'], 'Nachos=14.00']
```

Testy: `S10EquivalenceTest` (niezależne oczekiwania policzone ręcznie, także błędy) i `S10DifferentialTest` (500 losowych drzew ze stałym ziarnem - stara reprezentacja kontra nowa).

### Krok 1: Composite i mapper obok starego kodu

**W IDE:** utwórz unię `MenuItem = Product | Combo`, klasy `Product` i `Combo` oraz `MenuMapper.fromNested(combo: readonly unknown[])`. `BarMenu` bez zmian. Uruchom test różnicowy - porównuje stary kod z drzewem z mappera.
**Po:**

```typescript
for (const element of combo.slice(1)) {
  if (typeof element === 'string') {
    items.push(MenuMapper.product(element));
  } else if (Array.isArray(element)) {
    items.push(MenuMapper.fromNested(element));
  } else {
    throw new IllegalArgumentError(`unsupported element: ${String(element)}`);
  }
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s10` - zielone, w tym `compositeFromMapperMatchesLegacyAlready`.
**Co powiedzieć:** obie reprezentacje żyją obok siebie. Mapper zachowuje komunikaty błędów starego kodu - to też obserwowalne zachowanie.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s10 0 1`

### Krok 2: price na Composite

**W IDE:** ciało `BarMenu.price` zastąp `MenuMapper.fromNested(combo).price()`. Usuń nieużywane `requireName` (VS Code wyszarza nieużywaną prywatną metodę).
**Po:**

```typescript
price(combo: readonly unknown[]): Money {
  return MenuMapper.fromNested(combo).price();
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** jedna operacja naraz; `render` wciąż jest stary i działa, bo woła nowe `price`.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s10 1 2`

### Krok 3: render na Composite

**W IDE:** `render` deleguje do `Combo.render(0, text)`; starą rekurencję `renderTo` i `productPrice` usuń (ręcznie - po zmianie stają się nieużywane).
**Po:**

```typescript
const text: string[] = [];
MenuMapper.fromNested(combo).render(0, text);
return text.join('');
```

**Uruchom:** test zielony, test różnicowy `finalBarMenuMatchesLegacy` zielony.
**Co powiedzieć:** format trwały (zagnieżdżone tablice) zostaje. Jego zmiana na obiekty JSON czy tabelę w bazie to osobna decyzja z osobnym testem.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s10 2 3`

### Rozwiązanie i uzasadnienie

Jawne drzewo z typami zamiast konwencji "pierwszy element to nazwa". Test różnicowy daje szerokie pokrycie, a niezależne oczekiwania chronią przed sytuacją, w której oba warianty mylą się tak samo.

### Pułapki

- Test różnicowy jako jedyny test - jeśli stary kod ma błąd, nowy go wiernie powtórzy.
- Brak limitu głębokości dla danych niezaufanych - `RangeError: Maximum call stack size exceeded`.
- Cykl w danych (tablica zawierająca samą siebie) - nieskończona rekurencja w obu wersjach.

### Pytanie do sali

Co zrobić, gdy mapper natrafi na dane, których nie da się wiernie odwzorować w drzewie?

## Scena s11. Transparent vs Safe Composite - add() na liściu

**Temat ze slajdów:** 16.4. Kompozyt przezroczysty czy bezpieczny
**Katalog:** `typescript/src/workshop/m5/s11_safecomposite` · **Test:** `scripts/warsztat.sh --lang ts test m5/s11` (`S11EquivalenceTest.test.ts`, `S11SolutionTest.test.ts`)
**Czas:** ~8 min

### W skrócie

**Co robimy:** W Transparent Composite `add()` jest we wspólnym `MenuComponent`, więc wywołanie go na produkcie kompiluje się i wybucha dopiero w runtime. Przesuwamy `add` i `children` w dół do `Combo` (Safe Composite), a potem pokazujemy niemutowalne drzewo z obiektów wartości bez `add`.

**Zasada:** Transparent Composite trzyma zarządzanie dziećmi we wspólnym typie, więc liść musi rzucić wyjątek albo nic nie robić. Safe Composite trzyma je tylko w węźle, dzięki czemu błąd przenosi się do kompilacji, ale klient musi wiedzieć, czy ma w ręku węzeł. Gdy drzewo powstaje raz, dylemat znika, bo `add` nie jest potrzebne nigdzie.

**Efekt:** Dodanie dziecka do produktu przestaje się kompilować, a `MenuComponent` nie ma już `add` ani `children`. Kosztem jest dokładniejsze typowanie zmiennych w katalogu - asercja `as Combo` w kliencie oznaczałaby powrót do problemu.

**Różnica względem Javy:** krok 2 (w Javie `sealed interface` i rekordy) to unia `type MenuComponent = Product | Combo` z polem `kind`, a `name` i `price` są w nim polami tylko do odczytu (w `Combo` `price` to getter liczący sumę), a nie metodami jak w start i kroku 1. `Product` ma jeden konstruktor przyjmujący cenę jako `Money | string` (w Javie dwa konstruktory). `S11SolutionTest` zamiast refleksji szuka metody przez `Reflect.get(Klasa.prototype, 'add')`, a niemutowalność sprawdza `TypeError` przy `push` na zamrożonej tablicy dzieci.

### Co widzimy

Transparent Composite: `add()` i `children()` są we wspólnej klasie abstrakcyjnej `MenuComponent`, liść `Product` dziedziczy `add()`, które rzuca `UnsupportedOperationError`. Klient typuje wszystko jako `MenuComponent` i nic go nie chroni przed `nachos.add(sos)`.

```typescript
add(_child: MenuComponent): void {
  throw new UnsupportedOperationError(`cannot add to ${this.name()}`);
}
```

### Krok 1: Safe Composite - Push Members Down

**W IDE:** VS Code nie ma Push Members Down - wytnij `add` i `children` z `MenuComponent` i wklej do `Combo` (z listą dzieci jako prywatnym polem). `npm run typecheck` pokaże błędy w `ComboCatalog` - zmień typ zmiennych z `MenuComponent` na `Combo` tam, gdzie dodajemy dzieci. `describe()` w `Combo` rozszerza opis o dzieci.
**Po:**

```typescript
const combo: Combo = new Combo('Zestaw Rodzinny');
combo.add(new Product('Popcorn XL', '24.00'));
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s11` - zielone; `S11SolutionTest` pokazuje, że `Product` nie ma już `add`.
**Co powiedzieć:** błąd przeniósł się z runtime do kompilacji (vitest go nie zobaczy - widać go w `npm run typecheck` i w edytorze). Ceną jest to, że klient musi wiedzieć, czy ma w ręku węzeł.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s11 0 1`

### Krok 2: niemutowalny Composite jako unia

**W IDE:** unia `type MenuComponent = Product | Combo`, niezmienne klasy `Product` i `Combo(name, children)` z `Combo.of(...)` i polem `kind`. Katalog zapisany deklaratywnie.
**Po:**

```typescript
case 'duo': return Combo.of('Zestaw Duo',
  new Product('Popcorn L', '18.00'),
  new Product('Cola', '9.00'),
  new Product('Cola', '9.00'));
```

**Uruchom:** test zielony.
**Co powiedzieć:** gdy drzewo powstaje raz, `add()` nie jest potrzebne nigdzie - dylemat Safe/Transparent znika.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s11 1 2`

### Rozwiązanie i uzasadnienie

Safe Composite, gdy drzewo jest modyfikowane; niemutowalny Composite, gdy jest budowane raz (wtedy budowę przejmuje Builder albo fabryka - scena s06).

### Pułapki

- Transparent Composite z domyślnym pustym `add()` (bez wyjątku) - dziecko po cichu ginie.
- Asercja `component as Combo` (albo `as any`) w kliencie zamiast poprawienia typów - Safe Composite w przebraniu Transparent, a w TypeScripcie asercja nic nie sprawdza w runtime.

### Pytanie do sali

Kiedy Transparent Composite jest lepszym wyborem mimo ryzyka błędu w runtime?

## Scena s12. Replace One/Many Distinctions with Composite - zwroty

**Temat ze slajdów:** 16.5. Jeden element lub wiele oraz wyodrębnienie Kompozytu (zastąpienie rozróżnienia „jeden lub wiele” Kompozytem)
**Katalog:** `typescript/src/workshop/m5/s12_onemany` · **Test:** `scripts/warsztat.sh --lang ts test m5/s12` (`S12EquivalenceTest.test.ts`, `S12SolutionTest.test.ts`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** `RefundService` ma osobne `refund` i `refundAll` z regułą zwrotu zapisaną dwa razy, a `CancellationDesk` sam wybiera między nimi. Najpierw usuwamy duplikację reguły, potem wprowadzamy wspólny kontrakt `Refundable` z liściem i grupą, a stare metody usuwamy po etapie `@deprecated`.

**Zasada:** Replace One/Many Distinctions with Composite zastępuje dwie ścieżki API - dla jednego elementu i dla wielu - jednym kontraktem, w którym grupa też jest elementem. Nie wolno przy tym zmienić kolejności przetwarzania, wyniku dla pustej grupy ani liczby efektów naliczanych raz na wywołanie.

**Efekt:** Jest jedna metoda `refund(refundable)`, reguła biletu żyje w liściu, suma w węźle, a klient nie ma już `if`. Potrącenie 3.00 zostaje w serwisie i nadal jest naliczane raz na zwrot, a nie raz na bilet.

**Różnica względem Javy:** TypeScript nie ma przeciążeń metod, więc w kroku 2 stara metoda `refund(TicketData, now)` nie może współistnieć z nową `refund(Refundable, now)` - dostaje nazwę `refundTicket` i znacznik `@deprecated` w TSDoc (`refundAll` bez zmian nazwy). `Refundable` to unia `SingleTicket | TicketGroup` z polem `kind` (odpowiednik `sealed interface`).

### Co widzimy

`RefundService` ma `refund(ticket, now)` i `refundAll(tickets, now)`, a klient `CancellationDesk` sam wybiera jedną z nich (`if (tickets.length === 1 ...)`). Reguła zwrotu (>= 24h 100%, < 24h 50%, po starcie 0) jest napisana dwa razy, trochę inaczej. Potrącenie 3.00 jest naliczane raz na zwrot i nie schodzi poniżej 0.

```typescript
for (const ticket of tickets) {
  if (now.isBefore(ticket.showStart)) {
    const hours = Duration.between(now, ticket.showStart).toHours();
    total = total.plus(hours >= 24 ? ticket.price : ticket.price.percent(50));
  }
}
```

Test obejmuje granicę dokładnie 24h, 23h59m, bilet po starcie, listę i pustą listę.

### Krok 1: Extract Method na wspólnej regule

**W IDE:** w `refund` zaznacz wyliczenie kwoty, ⌃⇧R > Extract to method in class 'RefundService', nazwa `ticketShare(ticket, now)` (w porcie `private static`). W `refundAll` zastąp ciało pętli wywołaniem `ticketShare`.
**Po:**

```typescript
total = total.plus(RefundService.ticketShare(ticket, now));
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s12` - zielone.
**Co powiedzieć:** zanim wprowadzimy wzorzec, usuwamy duplikację reguły. Test granicy 24h potwierdza, że obie wersje były równoważne.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s12 0 1`

### Krok 2: Composite i jeden kontrakt

**W IDE:** unia `Refundable = SingleTicket | TicketGroup`, obie klasy z `refundableAmount(now)`: liść `SingleTicket` (reguła z `ticketShare`), węzeł `TicketGroup` (suma). Nowa metoda `refund(refundable, now)`; stare metody jako delegacje z `@deprecated` (stara `refund` zmienia nazwę na `refundTicket` - F2 Rename Symbol poprawi wywołania). `CancellationDesk` przechodzi na nowy kontrakt (na razie wciąż wybiera `SingleTicket` albo `TicketGroup`).
**Po:**

```typescript
refund(refundable: Refundable, now: LocalDateTime): Money {
  return refundable.refundableAmount(now).minus(RefundService.FEE).max(Money.ZERO);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** potrącenie jest w jednym miejscu i naliczane raz na wywołanie - tak jak w obu starych metodach. VS Code przekreśla wywołania metod oznaczonych `@deprecated`, więc widać, kto jeszcze nie przeszedł.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s12 1 2`

### Krok 3: usunięcie starych metod

**W IDE:** po migracji klientów usuń `refundTicket` i `refundAll` (Find All References ⇧⌥F12 potwierdza, że nikt ich nie woła). W `CancellationDesk` usuń `if` - klient zawsze buduje `TicketGroup.of(tickets)`, także z jednego biletu.
**Po:**

```typescript
return this.service.refund(TicketGroup.of(tickets), now);
```

**Uruchom:** test zielony; `S12SolutionTest` pokazuje zagnieżdżone grupy.
**Co powiedzieć:** rozróżnienie "jeden/wiele" zniknęło z API. Grupa jednego biletu daje ten sam wynik co bilet.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s12 2 3`

### Rozwiązanie i uzasadnienie

Jeden kontrakt `refund(refundable)`, reguła biletu w liściu, suma w węźle. Stare API usunięte dopiero po etapie `@deprecated`.

### Pułapki

- Potrącenie przeniesione do liścia - wtedy lista biletów płaci 3.00 za każdy bilet (zmiana zachowania).
- Zmiana kolejności przetwarzania albo liczby transakcji przy przejściu na Composite.
- Pusta grupa - trzeba jawnie ustalić wynik (tu 0.00, jak w start).

### Pytanie do sali

Co, jeśli biznes chce potrącenia za każdą rezerwację, a jedna grupa obejmuje kilka rezerwacji?

## Scena s13. Extract Composite - kontenery programu

**Temat ze slajdów:** 16.5. Jeden element lub wiele oraz wyodrębnienie Kompozytu (wyodrębnienie Kompozytu)
**Katalog:** `typescript/src/workshop/m5/s13_extractcomposite` · **Test:** `scripts/warsztat.sh --lang ts test m5/s13` (`S13EquivalenceTest.test.ts`, `S13SolutionTest.test.ts`)
**Czas:** ~8 min

### W skrócie

**Co robimy:** `Marathon` i `ShortsBlock` powielają obsługę dzieci - listę, `add`, kopię `children()`, sumę minut i opis. Wydzielamy nadklasę `CompositeProgramItem` i podciągamy do niej to, co naprawdę wspólne, a regułę czasu zostawiamy w podklasach.

**Zasada:** Extract Composite wydziela wspólną nadklasę dla kilku kontenerów, które powielają zarządzanie dziećmi. Podobne pętle mogą znaczyć co innego, więc przed podciągnięciem porównujemy kontrakty, a nie tekst. Nowe reguły, np. wykrywanie cykli, to osobna zmiana zachowania.

**Efekt:** Lista dzieci, `add` i szkielet opisu są w jednym miejscu, a podklasy mają po kilkanaście linii. `minutes()` zostaje w podklasach, bo tylko maraton dolicza przerwy - podciągnięcie go do bazy zmieniłoby wynik.

**Różnica względem Javy:** TypeScript nie ma `final`, więc `add`, `children` i `childrenMinutes` w `CompositeProgramItem` są zwykłymi metodami (w Javie `final`) - przed nadpisaniem chroni tylko konwencja i przegląd kodu. `Film` przechowuje czas w prywatnym polu `length`, a `minutes()` zostaje metodą kontraktu `ProgramItem`.

### Co widzimy

`Marathon` i `ShortsBlock` powielają obsługę dzieci: lista, `add` z kontrolą `null`/`undefined` (`requireNonNull`), `children()` jako zamrożona kopia, suma minut, opis. Różnią się tylko regułą czasu: maraton dodaje 15 minut przerwy między pozycjami.

```typescript
return this.childList.length === 0 ? 0 : total + 15 * (this.childList.length - 1);
```

### Krok 1: Extract Superclass

**W IDE:** VS Code nie ma Extract Superclass - utwórz ręcznie `abstract class CompositeProgramItem implements ProgramItem` i przenieś do niej z `Marathon` pole `childList`, `add` i `children()`, a `minutes()` i `describe()` zadeklaruj jako `abstract`. Potem `ShortsBlock extends CompositeProgramItem` i usuń jego kopie; podklasy sięgają do dzieci przez `this.children()`.
**Po:**

```typescript
export abstract class CompositeProgramItem implements ProgramItem {
  private readonly childList: ProgramItem[] = [];

  add(child: ProgramItem): void {
    this.childList.push(requireNonNull(child, 'child'));
  }
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s13` - zielone.
**Co powiedzieć:** kontrakt dzieci (brak `null`, kopia listy) jest teraz w jednym miejscu dla wszystkich kontenerów.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s13 0 1`

### Krok 2: Pull Up sumy i opisu

**W IDE:** przenieś do bazy (ręcznie) `childrenMinutes()` jako `protected` i `describe()` jako szablon z hookiem `protected abstract label()`; nazwa kontenera trafia do chronionego konstruktora bazy. Podklasy zostają z regułą czasu i etykietą.
**Po:**

```typescript
override minutes(): number {
  const count = this.children().length;
  return count === 0 ? 0 : this.childrenMinutes() + 15 * (count - 1);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** podobne pętle mogą znaczyć co innego. Tu suma jest wspólna, ale przerwy nie - dlatego `minutes()` zostaje w podklasach.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s13 1 2`

### Rozwiązanie i uzasadnienie

`CompositeProgramItem` z pełną obsługą dzieci i szkieletem opisu; `Marathon` i `ShortsBlock` po kilkanaście linii.

### Pułapki

- Podciągnięcie całego `minutes()` do bazy - maraton traci przerwy.
- Wykrywanie cykli (maraton zawierający sam siebie) to osobna zmiana zachowania, nie część Extract Composite.

### Pytanie do sali

Trzeci kontener "blok z przerwą na reklamy co drugi film" - czy nadal pasuje do tej nadklasy?

## Scena s14. Unify Interfaces with Adapter - dwie bramki płatności

**Temat ze slajdów:** 9.1. Ujednolicenie interfejsów Adapterem; 9.2. Co adapter naprawdę musi przetłumaczyć
**Katalog:** `typescript/src/workshop/m5/s14_adapter` · **Test:** `scripts/warsztat.sh --lang ts test m5/s14` (`S14EquivalenceTest.test.ts`, `S14SolutionTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `CheckoutService.pay` mówi dwoma językami: dla starej bramki składa XML w groszach, dla nowej woła REST w złotych i łapie wyjątek odmowy. Wydzielamy obie gałęzie do metod o jednej sygnaturze, przenosimy je do adapterów interfejsu `PaymentGateway`, a serwis wybiera bramkę z mapy.

**Zasada:** Unify Interfaces with Adapter tłumaczy obcy interfejs na preferowany kontrakt klienta, tak żeby klient zależał tylko od niego. Adapter tłumaczy nazwy, jednostki, format i sposób zgłaszania błędów, ale nie udaje, że semantyka jest identyczna. Jeśli wystarczy Rename Method, adapter jest zbędny.

**Efekt:** Logika serwisu zna tylko `PaymentGateway` i da się ją testować fake'iem, a trzeci dostawca to nowy adapter i wpis w mapie. Stary konstruktor zostaje jako jedyne miejsce znające biblioteki bramek - docelowo do przeniesienia do korzenia kompozycji.

**Różnica względem Javy:** zagnieżdżone typy `RestPayClient.ChargeRequest/ChargeResponse/RestPayException` to osobne eksporty modułu `RestPayClient.ts`. Grosze liczymy jako `amount.amount.times(100).toNumber()` (w Javie `movePointRight(2).longValueExact()`, które dodatkowo rzuca wyjątek przy ułamku grosza). Dwa konstruktory Javy w kroku 3 to sygnatury przeciążenia konstruktora - `(xml, rest)` i `(gateways: ReadonlyMap<string, PaymentGateway>)` - z jedną implementacją rozróżniającą argument przez `instanceof XmlPayGateway`, żeby stary sposób tworzenia działał bez zmian w klientach.

### Co widzimy

`CheckoutService.pay(provider, reservationId, amount)` mówi dwoma językami: dla starej bramki składa XML z kwotą w **groszach** i parsuje atrybuty odpowiedzi, dla nowej woła API REST w **złotych** i łapie wyjątek odmowy. "Biblioteki" bramek (`XmlPayGateway`, `RestPayClient`) są w katalogu sceny i ich nie zmieniamy.

```typescript
const grosze = amount.amount.times(100).toNumber();
const request = `<charge ref='${reservationId}' amount='${grosze}'/>`;
```

### Krok 1: Extract Method z jednolitą sygnaturą

**W IDE:** każdą gałąź ⌃⇧R > Extract to method in class 'CheckoutService': `payWithXml(reservationId, amount)` i `payWithRest(reservationId, amount)`, obie zwracają `PaymentResult`.
**Po:**

```typescript
if (provider === 'XML') {
  return this.payWithXml(reservationId, amount);
} else if (provider === 'REST') {
  return this.payWithRest(reservationId, amount);
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s14` - zielone.
**Co powiedzieć:** identyczna sygnatura dwóch metod to gotowy kontrakt adaptera.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s14 0 1`

### Krok 2: interfejs i dwa adaptery

**W IDE:** interfejs `PaymentGateway` z `pay(reservationId, amount)`. Metody `payWithXml` i `payWithRest` przenieś ręcznie (VS Code nie przenosi metod między klasami) jako `pay` do `XmlPayAdapter` (razem z pomocniczym `attribute`) i `RestPayAdapter`. Konstruktor serwisu bez zmian - tworzy adaptery.
**Po:**

```typescript
constructor(xml: XmlPayGateway, rest: RestPayClient) {
  this.xml = new XmlPayAdapter(xml);
  this.rest = new RestPayAdapter(rest);
}
```

**Uruchom:** test zielony; `S14SolutionTest` sprawdza, że do starej bramki idzie `amount='4000'`.
**Co powiedzieć:** adapter tłumaczy jednostki (złote - grosze), format i sposób zgłaszania odmowy (atrybut kontra wyjątek). Nie udaje, że semantyka jest taka sama.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s14 1 2`

### Krok 3: serwis zależy tylko od interfejsu

**W IDE:** nowa sygnatura konstruktora `(gateways: ReadonlyMap<string, PaymentGateway>)` (kopia `new Map(gateways)`); stara sygnatura `(xml, rest)` zostaje jako przeciążenie i buduje mapę ze standardowym zestawem adapterów. `if` zastąp wyszukaniem w mapie; nieznany dostawca - ten sam wyjątek.
**Po:**

```typescript
const gateway = this.gateways.get(provider);
if (gateway === undefined) {
  throw new IllegalArgumentError(`unknown provider: ${provider}`);
}
return gateway.pay(reservationId, amount);
```

**Uruchom:** test zielony.
**Co powiedzieć:** logika serwisu zna tylko `PaymentGateway` i da się ją testować fake'iem. Stary konstruktor to jedyne miejsce, które jeszcze zna biblioteki bramek - docelowo przeniesiemy go do korzenia kompozycji. Trzeci dostawca to nowy adapter i wpis w mapie.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s14 2 3`

### Rozwiązanie i uzasadnienie

Preferowany kontrakt kina (`PaymentGateway`, `PaymentResult`) i dwa adaptery. Granica 500.00 włącznie działa tak samo w obu bramkach - test to sprawdza.

### Pułapki

- Podobna sygnatura, inny kontrakt: jednostki, zaokrąglenia (`toNumber()` po cichu przepuści ułamek grosza), strefa czasowa, kodowanie znaków.
- Mapowanie wyjątku bez zachowania przyczyny (`new Error(msg, { cause })`) - trudna diagnoza w produkcji.
- Adapter zamykający zasób, którego nie jest właścicielem.
- Adapter tam, gdzie wystarczyłby Rename Method.

### Pytanie do sali

Stara bramka zwraca `status='ERROR'` przy błędnym XML. Jak to zamapować - na odmowę czy wyjątek? Kto decyduje?

## Scena s15. Replace Conditional Dispatcher with Command - konsola kasjera

**Temat ze slajdów:** 6.6. Polecenie: łańcuch warunków zamieniony na rejestr poleceń
**Katalog:** `typescript/src/workshop/m5/s15_command` · **Test:** `scripts/warsztat.sh --lang ts test m5/s15` (`S15EquivalenceTest.test.ts`, `S15SolutionTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `CashierConsole.handle` to łańcuch `if` po nazwie komendy z pełną logiką i zmianą stanu kasy w każdej gałęzi. Wydzielamy gałęzie do metod o wspólnej sygnaturze, zamieniamy je na bezstanowe komendy działające na `Till`, a łańcuch `if` zastępujemy mapą komend.

**Zasada:** Replace Conditional Dispatcher with Command zamienia każdą gałąź dyspozytora w obiekt komendy, a wybór gałęzi w wyszukanie w rejestrze. Mapa jest równoważna warunkom tylko wtedy, gdy klucze są rozłączne i zachowana jest normalizacja. Command nie daje automatycznie asynchroniczności, retry ani undo.

**Efekt:** Dodanie komendy to nowa klasa i wpis w mapie, bez zmiany `handle`. Komendy nie trzymają danych żądania, więc można je bezpiecznie współdzielić, a `sell` i `SELL` nadal działają tak samo.

**Różnica względem Javy:** `@FunctionalInterface ConsoleCommand` to interfejs z `execute(args, till)` - test dodaje komendę jako literał obiektu. Domyślny rejestr komend w kroku 3 to parametr domyślny konstruktora zamiast drugiego konstruktora. `toUpperCase(Locale.ROOT)` to zwykłe `toUpperCase()`, a `split(" ", 2)` - `split(' ')` z destrukturyzacją `[name = '', ...rest]` i `rest.join(' ')` (ten sam wynik).

### Co widzimy

`CashierConsole.handle(line)` to łańcuch `if (command === 'SELL')` z pełną logiką i modyfikacją stanu kasy w każdej gałęzi. Nazwa komendy jest normalizowana `toUpperCase()` - `sell` i `SELL` działają tak samo.

```typescript
if (command === 'SELL') {
  const [count = '', ...titleParts] = args.split(' ');
  ...
} else if (command === 'REFUND') {
  ...
} else if (command === 'REPORT') {
```

Test to sesja poleceń: sprzedaż, zwrot, raport oraz błędne polecenia, które nie zmieniają stanu.

### Krok 1: Extract Method dla gałęzi

**W IDE:** ciało każdej gałęzi ⌃⇧R > Extract to method in class 'CashierConsole': `sell(args)`, `refund(args)`, `report(args)` - ta sama sygnatura, także gdy `args` nie jest używane (`_args`).
**Po:**

```typescript
if (command === 'SELL') {
  return this.sell(args);
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s15` - zielone.
**Co powiedzieć:** jednolita sygnatura przygotowuje interfejs komendy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s15 0 1`

### Krok 2: komendy jako obiekty, stan w Till

**W IDE:** nowa klasa `Till` (pola `cash`, `tickets` i cennik - ręczne Extract Class). Interfejs `ConsoleCommand` z `execute(args, till)`, klasy `SellCommand`, `RefundCommand`, `ReportCommand` z ciałami metod. Dyspozytor `if` jeszcze zostaje.
**Po:**

```typescript
if (command === 'SELL') {
  return this.sell.execute(args, this.till);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** komendy są bezstanowe - dane żądania to argument, stan kasy to argument. Współdzielona komenda ze stanem poprzedniego żądania to błąd czekający na drugie, przeplatane wywołanie (np. przez `await`).
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s15 1 2`

### Krok 3: rejestr zamiast warunku

**W IDE:** `ReadonlyMap<string, ConsoleCommand>` jako parametr konstruktora z wartością domyślną (standardowy zestaw), `handle` wyszukuje komendę po znormalizowanym kluczu.
**Po:**

```typescript
const command = this.commands.get(name.toUpperCase());
if (command === undefined) {
  return `Nieznana komenda: ${name}`;
}
```

**Uruchom:** test zielony; `S15SolutionTest` dodaje komendę bez zmiany dyspozytora.
**Co powiedzieć:** mapa jest równoważna warunkom, bo klucze są **rozłączne** i normalizacja została zachowana. Przy nakładających się predykatach (`startsWith`) kolejność `if` byłaby częścią kontraktu i mapa by go zgubiła.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s15 2 3`

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
**Katalog:** `typescript/src/workshop/m5/s16_templatemethod` · **Test:** `scripts/warsztat.sh --lang ts test m5/s16` (`S16EquivalenceTest.test.ts`, `S16SolutionTest.test.ts`)
**Czas:** ~8 min

### W skrócie

**Co robimy:** `CsvSalesReport` i `HtmlSalesReport` mają ten sam szkielet (sortowanie, nagłówek, wiersze z sumą, stopka), ale napisany trochę inaczej. Wydzielamy różnice do metod `header`, `row`, `footer`, doprowadzamy `render` do identycznej postaci i podciągamy go do nadklasy `SalesReport` jako metodę chronioną przed nadpisaniem.

**Zasada:** Form Template Method umieszcza wspólną sekwencję kroków w metodzie bazowej, a zmienne kroki w metodach nadpisywanych przez podklasy. Stosuje się ją, gdy kolejność kroków jest stała i różnią się tylko szczegóły. Przy wielu hookach albo zmiennej kolejności lepsza jest kompozycja lub Strategy.

**Efekt:** Sortowanie i sumowanie są w jednym miejscu, a nowy format (np. Markdown) to trzy metody. Ochrona `render` pilnuje kolejności kroków, ale w opublikowanej bibliotece zablokowanie nadpisywania dotąd otwartej metody złamałoby klientów.

**Różnica względem Javy:** TypeScript nie ma `final`. `SalesReport` chroni szablon w runtime: konstruktor bazy rzuca `IllegalStateError('render() is final')`, jeśli podklasa nadpisała `render`. Test `templateMethodIsFinal` sprawdza, że `CsvSalesReport` i `HtmlSalesReport` nie mają własnego `render` (`Object.hasOwn(Klasa.prototype, 'render')`) oraz że podklasa nadpisująca `render` jest odrzucana (w Javie: `Modifier.isFinal`). `StringBuilder` to sklejanie łańcucha `text += ...`.

### Co widzimy

`CsvSalesReport` i `HtmlSalesReport` mają ten sam szkielet: kopia tablicy, sortowanie po godzinie, nagłówek, wiersze z sumowaniem, stopka. Napisane trochę inaczej (`toSorted` i tablica fragmentów kontra kopia z `sort` i sklejanie łańcucha, inne nazwy zmiennych), więc duplikacja nie rzuca się w oczy. Różnice to formatowanie i escapowanie (`;` w CSV, `&` w HTML).

### Krok 1: Extract Method na różnicach

**W IDE:** w obu klasach ⌃⇧R > Extract to method in class dla nagłówka, wiersza i stopki: `header()`, `row(sale)`, `footer(tickets, total)`. Przepisz `render` HTML na ten sam kształt co CSV (nazwy zmiennych, sortowanie).
**Po:**

```typescript
let text = this.header();
let tickets = 0;
let total = Money.ZERO;
for (const sale of sorted) {
  text += this.row(sale);
  ...
}
return text + this.footer(tickets, total);
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s16` - zielone.
**Co powiedzieć:** ujednolicamy sygnatury po kroku, aż `render` obu klas będzie identyczny znak w znak.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s16 0 1`

### Krok 2: Extract Superclass + Pull Up render

**W IDE:** VS Code nie ma Extract Superclass - utwórz ręcznie `abstract class SalesReport`, przenieś do niej `render` z CSV, a `header`, `row`, `footer` zadeklaruj jako `protected abstract`. Obie klasy `extends SalesReport`, w podklasach metody formatujące z `protected override`, kopie `render` usuń. Na koniec dodaj w konstruktorze bazy strażnika zamiast `final`.
**Po:**

```typescript
constructor() {
  if (this.render !== SalesReport.prototype.render) {
    throw new IllegalStateError('render() is final');
  }
}
...
protected abstract header(): string;
protected abstract row(sale: Sale): string;
protected abstract footer(tickets: number, total: Money): string;
```

**Uruchom:** test zielony; `S16SolutionTest` sprawdza ochronę `render` i dodaje format Markdown trzema metodami.
**Co powiedzieć:** strażnik chroni kolejność kroków - ale dopiero w runtime, przy tworzeniu obiektu, a nie w kompilacji jak `final` w Javie. W opublikowanej bibliotece zablokowanie nadpisywania metody łamie klientów, którzy już ją nadpisali.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s16 1 2`

### Rozwiązanie i uzasadnienie

Szkielet raz, w bazie; podklasy to czyste formatowanie. Pusta sprzedaż daje nagłówek i sumę 0.00 w obu formatach.

### Pułapki

- Hook opcjonalny z domyślną implementacją, która nie pasuje do wszystkich podklas.
- Wywołanie hooka z konstruktora bazy - podklasa widzi niezainicjalizowane pola (pola podklasy są przypisywane dopiero po `super()`).
- Wiele hooków i zmienna kolejność - wtedy lepsza kompozycja lub Strategy.

### Pytanie do sali

Raport PDF potrzebuje stronicowania co 30 wierszy. Czy to jeszcze Template Method?

## Scena s17. Limit Instantiation with Singleton - cennik

**Temat ze slajdów:** 16.7. Ograniczenie liczby instancji Singletonem; 17.4. Wchłonięcie Singletona
**Katalog:** `typescript/src/workshop/m5/s17_singleton` · **Test:** `scripts/warsztat.sh --lang ts test m5/s17` (`S17EquivalenceTest.test.ts`, `S17SolutionTest.test.ts`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** `TicketDesk` przy każdej wycenie tworzy nowy `PriceList` i parsuje taryfę, choć cennik jest niemutowalny. Po pomiarze ograniczamy go do jednej instancji (najpierw `getInstance()`, potem zamrożona stała modułu), a na końcu ukrywamy za interfejsem `Tariff` wstrzykiwanym do `TicketDesk`.

**Zasada:** Limit Instantiation with Singleton to decyzja o cyklu życia: jedna instancja jest bezpieczna tylko wtedy, gdy obiekt jest niemutowalny, a instancje równoważne. Stała modułu ES daje jedną instancję, ale na graf modułów, nie na proces. Odwrotny ruch, Inline Singleton, jest równie ważny, gdy globalny dostęp szkodzi testom.

**Efekt:** Cennik powstaje raz, a `TicketDesk` zależy od kontraktu i da się go przetestować z innym cennikiem bez globalnego stanu. Globalna instancja zostaje tylko w wartości domyślnej konstruktora, a dodanie do niej mutowalnego stanu byłoby najgorszym wariantem.

**Różnica względem Javy:** javowy "singleton jako enum" (`PriceList.INSTANCE`) nie ma odpowiednika w `enum` TypeScriptu, więc krok 2 to zamrożona stała modułu `export const PriceList = Object.freeze({ basePrice })`. Moduł ES jest ewaluowany raz, więc to jedna instancja - ale na graf modułów (dwie kopie pakietu w `node_modules` albo worker to osobne instancje), tak jak enum w Javie to jedna instancja na loader klas. Klient woła `PriceList.basePrice(...)`. W kroku 3 `PriceList: Tariff`, a `TicketDesk(tariff: Tariff = PriceList)` ma parametr domyślny zamiast drugiego konstruktora. Licznik `created()` to zwykłe pole statyczne (bez `AtomicInteger`), a test pomiaru szuka go przez `Reflect.get` i po `jump` jest pomijany.

### Co widzimy

`TicketDesk.quote` przy każdym wywołaniu robi `new PriceList()`, a konstruktor parsuje taryfę. Cennik jest niemutowalny, więc instancje są równoważne. `S17SolutionTest` najpierw **mierzy**: trzy wyceny to trzy utworzone cenniki.

```typescript
const price = new PriceList().basePrice(format);
```

### Krok 1: klasyczny Singleton

**W IDE:** prywatny konstruktor, `private static readonly INSTANCE = new PriceList()`, `static getInstance()`. Licznik z pomiaru usuń. Klient woła `PriceList.getInstance()`.
**Po:**

```typescript
const price = PriceList.getInstance().basePrice(format);
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s17` - zielone.
**Co powiedzieć:** Singleton to decyzja o cyklu życia - bezpieczna tylko dlatego, że obiekt jest niemutowalny i udowodniliśmy równoważność instancji.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s17 0 1`

### Krok 2: Singleton jako zamrożona stała modułu

**W IDE:** zamień klasę na stałą modułu `export const PriceList = Object.freeze({ basePrice(format) { ... } })`; taryfa parsowana raz, w funkcji `parse()` wywołanej przy ewaluacji modułu, do `ReadonlyMap`.
**Po:**

```typescript
const price = PriceList.basePrice(format);
```

**Uruchom:** test zielony.
**Co powiedzieć:** moduł ES daje jednokrotną inicjalizację bez leniwych sztuczek, a `Object.freeze` blokuje podmianę metody. Ale to **jedna instancja na graf modułów**, nie "jedna na proces" - dwie kopie pakietu albo worker mają własne.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s17 1 2`

### Krok 3: wstrzyknięcie zamiast globalnego dostępu

**W IDE:** wydziel interfejs `Tariff` z `basePrice(format)` (ręcznie - VS Code nie ma Extract Interface), oznacz stałą typem `PriceList: Tariff`; `TicketDesk` dostaje konstruktor `(tariff: Tariff = PriceList)`.
**Po:**

```typescript
constructor(tariff: Tariff = PriceList) {
  this.tariff = requireNonNull(tariff, 'tariff');
}
```

**Uruchom:** test zielony; `injectedTariffNeedsNoGlobalState` używa cennika promocyjnego jako literału obiektu `{ basePrice: () => Money.of('19.00') }`.
**Co powiedzieć:** o jednej instancji decyduje korzeń kompozycji, a klient zależy od kontraktu. Gdyby ktoś dodał do singletona `setPromo(...)`, testy zaczęłyby wpływać na siebie nawzajem (vitest uruchamia testy jednego pliku w tym samym grafie modułów) - mutowalny globalny stan to najgorszy wariant.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s17 2 3`

### Rozwiązanie i uzasadnienie

Jedna instancja cennika (stała modułu), ale ukryta za interfejsem i wstrzykiwana. Odwrotna transformacja (Inline Singleton) jest równie ważna - gdy globalny dostęp szkodzi testom, wracamy do jawnej zależności.

### Pułapki

- Singleton z mutowalnym stanem (promocje, cache) - testy zależne od kolejności.
- Leniwy singleton inicjowany asynchronicznie (`if (!instance) instance = await load()`) - dwa równoległe wywołania przed pierwszym `await` tworzą dwie instancje.
- Singleton "bo tak wygodniej" bez pomiaru kosztu tworzenia.

### Pytanie do sali

Cennik ma się zmieniać o północy bez restartu. Co wtedy z singletonem?

## Scena s18. Move Accumulation to Collecting Parameter - ostrzeżenia walidacji

**Temat ze slajdów:** 16.6. Przeniesienie zbierania wyników do parametru zbierającego
**Katalog:** `typescript/src/workshop/m5/s18_collectingparameter` · **Test:** `scripts/warsztat.sh --lang ts test m5/s18` (`S18EquivalenceTest.test.ts`)
**Czas:** ~8 min

### W skrócie

**Co robimy:** `ReservationValidator` skleja ostrzeżenia w łańcuchu, a każda metoda pomocnicza dopisuje własny separator. Zamieniamy sklejanie na tablicę, potem przekazujemy akumulator do metod `check...`, a na końcu zawężamy go do klasy `Warnings`.

**Zasada:** Collecting Parameter polega na tym, że metody dopisują wyniki do przekazanego akumulatora, zamiast zwracać fragmenty do sklejenia. Właścicielem kolekcji jest wywołujący, trzeba ustalić, czy metoda czyści, czy dopisuje, a wynik częściowy po wyjątku jest częścią kontraktu. Dobry akumulator ma wąski typ, a nie ogólną mutowalną kolekcję.

**Efekt:** Separator i format wyniku są w jednym miejscu, a metody pomocnicze mogą tylko dopisać ostrzeżenie. Treść, kolejność i separator ostrzeżeń są dokładnie takie jak w start.

**Różnica względem Javy:** `addAll` z kroku 1 to `push(...lista)`, `isBlank()` to `trim() === ''`, a `String.join` to `join('; ')`. `Set.add` w Javie zwraca `boolean`; w TypeScripcie warunek "duplikat zgłaszany raz" jest zapisany przez `has` i `add` (ta sama semantyka).

### Co widzimy

`ReservationValidator.validate` skleja ostrzeżenia w łańcuchu: każda metoda pomocnicza zwraca fragment zakończony `'; '`, a na końcu obcinamy dwa znaki. Łatwo zgubić separator.

```typescript
let warnings = '';
warnings += this.checkEmail(draft.email);
warnings += this.checkSeats(draft.seats);
...
return warnings === '' ? 'OK' : warnings.substring(0, warnings.length - 2);
```

Test sprawdza treść, kolejność i separator, w tym duplikaty miejsc zgłaszane jeden raz.

### Krok 1: tablica zamiast sklejania

**W IDE:** metody pomocnicze zwracają `readonly string[]` bez separatora, `validate` robi `push(...)` i `join('; ')`.
**Po:**

```typescript
warnings.push(...this.checkEmail(draft.email));
warnings.push(...this.checkSeats(draft.seats));
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s18` - zielone.
**Co powiedzieć:** separator dokłada teraz jedno miejsce. Metody wciąż tworzą własne tablice.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s18 0 1`

### Krok 2: Collecting Parameter

**W IDE:** zmiana sygnatury ręcznie (VS Code nie ma Change Signature): dodaj parametr `warnings: string[]`, zwracany typ `void`, zamień `return [x]` na `warnings.push(x)`. Warunek czasu seansu wyciągnij do `checkShowTime(draft, warnings)` (⌃⇧R > Extract to method in class).
**Po:**

```typescript
this.checkEmail(draft.email, warnings);
this.checkSeats(draft.seats, warnings);
this.checkShowTime(draft, warnings);
```

**Uruchom:** test zielony.
**Co powiedzieć:** metody dopisują do przekazanego akumulatora. Właścicielem kolekcji jest `validate` - tworzy ją i decyduje o formacie.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s18 1 2`

### Krok 3: wąski typ parametru

**W IDE:** klasa `Warnings` z `add` i `summary()`; zmień typ parametrów z `string[]` na `Warnings` i `push` na `add` (VS Code nie ma Type Migration - `npm run typecheck` wskaże każde miejsce).
**Po:**

```typescript
const warnings = new Warnings();
...
return warnings.summary();
```

**Uruchom:** test zielony.
**Co powiedzieć:** metoda pomocnicza może tylko dopisać - nie wyczyści ani nie przestawi cudzych ostrzeżeń.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s18 2 3`

### Rozwiązanie i uzasadnienie

Każda reguła to metoda `check...(..., warnings: Warnings)`, format wyniku w jednym miejscu, akumulator o wąskim API.

### Pułapki

- Ogólny mutowalny obiekt (`Record<string, unknown>`, `Map`) jako parametr zbierający - ukrywa, kto co zapisuje.
- Niejasne, czy metoda czyści akumulator (`warnings.length = 0`), czy dopisuje.
- Wyjątek w połowie zbierania zostawia wynik częściowy - to część kontraktu, trzeba go ustalić.

### Pytanie do sali

Czy `Warnings` powinno pozwalać na poziomy (błąd kontra ostrzeżenie)? Jak to zmieni kontrakt?

## Scena s19. Visitor i macierz zmian - pozycje zamówienia

**Temat ze slajdów:** 15.1. Odwiedzający i macierz zmian; 15.2. Odwiedzający albo hierarchia `sealed` i `switch`
**Katalog:** `typescript/src/workshop/m5/s19_visitor` · **Test:** `scripts/warsztat.sh --lang ts test m5/s19` (`S19EquivalenceTest.test.ts`, `S19SolutionTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `ReceiptPrinter` ma trzy operacje na pozycjach zamówienia, każda z łańcuchem `instanceof` zakończonym wyjątkiem w runtime. Zamieniamy je na klasyczne Visitory, a potem pokazujemy alternatywę TypeScriptu: zamkniętą unię dyskryminowaną i wyczerpujący `switch` po polu `kind` z `assertNever`.

**Zasada:** Visitor przenosi operacje na strukturze do osobnych klas, a element wybiera właściwą metodę przez double dispatch. Macierz zmian mówi, kiedy go stosować: nowa operacja jest tania, nowy rodzaj elementu wymaga zmiany wszystkich Visitorów. W TypeScripcie tę samą kontrolę kompilatora daje wyczerpujący `switch` po unii dyskryminowanej, a Visitor ma sens, gdy zestawu typów nie da się zamknąć.

**Efekt:** W kodzie nie ma `instanceof`, a pozycja bez obsługi to błąd kompilacji zamiast awarii na kasie. Obie formy są poprawne - wybór zależy od tego, czy częściej dochodzą operacje, czy rodzaje pozycji.

**Różnica względem Javy:** nazwy `visitTicket/visitSnack/visitVoucher` są jak w Javie. Krok 3 (w Javie `sealed interface` i `switch` z record patterns) to unia `TicketItem | SnackItem | VoucherItem` z polem `kind` i `switch (item.kind)`; zamiast "switcha bez `default`" jest `default: return assertNever(item)`, który **nie** wyłącza kontroli kompilatora - nowy rodzaj pozycji to błąd kompilacji, bo `item` nie zawęża się do `never`. Record patterns zastępuje destrukturyzacja w gałęzi. Pusty interfejs znacznikowy `OrderItem` w start zostaje - w TypeScripcie strukturalnie akceptuje każdy obiekt, więc problem `instanceof` widać tym wyraźniej.

### Co widzimy

Pozycje zamówienia: bilet (VAT 8%), produkt baru (VAT 23%), voucher (pomniejsza kwotę, bez VAT). `ReceiptPrinter` ma trzy operacje - linia paragonu, kwota, VAT - każda z łańcuchem `instanceof` zakończonym wyjątkiem w runtime. Nowy rodzaj pozycji kompiluje się bez błędu i wybucha na kasie.

```typescript
if (item instanceof TicketItem) {
  return ReceiptPrinter.vatOf(item.price, 8);
} else if (item instanceof SnackItem) {
  return ReceiptPrinter.vatOf(item.price, 23);
} else if (item instanceof VoucherItem) {
  return Money.ZERO;
}
throw new IllegalArgumentError(`unknown item: ${String(item)}`);
```

### Krok 1: accept + pierwszy Visitor

**W IDE:** interfejs `OrderItemVisitor<R>` z `visitTicket/visitSnack/visitVoucher`, metoda `accept<R>(visitor)` w `OrderItem` i klasach pozycji (⌘. na klasie - Implement interface 'OrderItem'). Operację "linia paragonu" przenieś do `ReceiptLineVisitor`.
**Po:**

```typescript
text += `${item.accept(this.lines)}\n`;
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s19` - zielone.
**Co powiedzieć:** double dispatch - element wybiera metodę odwiedzającego. Brak metody dla typu to błąd kompilacji (`npm run typecheck`).
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s19 0 1`

### Krok 2: pozostałe operacje jako Visitory

**W IDE:** `AmountVisitor` i `VatVisitor`, usuń łańcuchy `instanceof`.
**Po:**

```typescript
total = total.plus(item.accept(this.amounts));
vat = vat.plus(item.accept(this.vats));
```

**Uruchom:** test zielony; `S19SolutionTest` dodaje operację "grupa VAT" jako nowy Visitor.
**Co powiedzieć:** macierz zmian: nowa operacja - nowa klasa, tanio; nowy rodzaj pozycji - zmiana interfejsu i **wszystkich** Visitorów.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s19 1 2`

### Krok 3: alternatywa TypeScript - unia dyskryminowana + switch

**W IDE:** `type OrderItem = TicketItem | SnackItem | VoucherItem`, każda klasa z literałem `readonly kind = 'ticket'` itd.; usuń `accept` i Visitory, operacje jako `switch (item.kind)` zakończone `default: return assertNever(item)` (z destrukturyzacją w linii paragonu).
**Po:**

```typescript
switch (item.kind) {
  case 'ticket': return ReceiptPrinter.vatOf(item.price, 8);
  case 'snack': return ReceiptPrinter.vatOf(item.price, 23);
  case 'voucher': return Money.ZERO;
  default: return assertNever(item);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** ta sama kontrola kompilatora co w Visitorze, bez ceremonii `accept/visit`. Visitor ma sens, gdy zestawu typów nie da się zamknąć w unii (inny moduł, biblioteka, rozszerzenia) albo gdy odwiedzający ma stan i logikę przejścia struktury.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s19 2 3`

### Rozwiązanie i uzasadnienie

Dwie poprawne formy docelowe: klasyczny Visitor (`step2`) i wyczerpujący `switch` po zamkniętej unii (`step3`). Macierz zmian:

| Częsta zmiana | Naturalniejszy kierunek |
| --- | --- |
| nowe operacje, stabilne rodzaje pozycji | Visitor albo switch po unii dyskryminowanej |
| nowe rodzaje pozycji, stabilne operacje | metody polimorficzne w pozycjach |
| jedna prosta akumulacja | Collecting Parameter (scena s18) |

### Pułapki

- `default` bez `assertNever` (np. `default: return Money.ZERO`) w `switch` po unii - wyłącza kontrolę kompilatora dla nowych typów.
- VAT zaokrąglany per pozycja kontra od sumy - różne wyniki, trzeba zachować sposób ze start.
- Visitor dla hierarchii, do której ciągle dochodzą typy - każdy nowy typ dotyka wszystkich Visitorów.

### Pytanie do sali

Dochodzi pozycja "karta podarunkowa" i operacja "eksport do księgowości". Która forma przyjmie te zmiany taniej?

## Scena s20. Mapa decyzji - jedna tabela, dwie struktury docelowe

**Temat ze slajdów:** 4.2. Najpierw rodzaj zmienności, potem wzorzec; 18.1. Mapa decyzji (1/2); 18.2. Mapa decyzji (2/2); 18.3. Lista kontrolna
**Katalog:** `typescript/src/workshop/m5/s20_decisionmap` · **Test:** `scripts/warsztat.sh --lang ts test m5/s20` (`S20EquivalenceTest.test.ts`, `S20SolutionTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `ShowPricing` ma tabelę cen 3x3 wpisaną w zagnieżdżone warunki, w których splecione są format i reguła dnia. Najpierw rozplatamy dwie osie, a potem z tego samego punktu budujemy dwie alternatywy: Strategy dla reguły dnia albo typ formatu z zachowaniem.

**Zasada:** Wzorzec wybieramy według rodzaju zmienności, a nie kształtu kodu - ten sam `switch` może sygnalizować różne problemy. Pytamy, która oś zmienia się częściej, i tam budujemy punkt rozszerzenia. Gdy tego nie wiemy, zostajemy przy prostej strukturze bez nowych typów.

**Efekt:** Obie ścieżki są behawioralnie równoważne, a różnią się kosztem przyszłej zmiany: A tanio przyjmuje nowe akcje dniowe, B nowe formaty. Reguła zależna od obu osi naraz łamie założenie niezależności i wtedy tabela może być lepsza niż wzorzec.

**Różnica względem Javy:** `DayOfWeek` to unia literałów (`'MONDAY' | ... | 'SUNDAY'`) z `shared/time.ts`. `@FunctionalInterface DayPolicy` to typ funkcyjny `(base: Money) => Money`, a kalendarz - funkcja `(day) => DayPolicy`, więc wywołanie to `this.calendar(day)(base)` zamiast `calendar.apply(day).apply(base)`. `enum Format` z zachowaniem to klasa z instancjami statycznymi (`TWO_D`, `THREE_D`, `IMAX`), prywatnym konstruktorem i `values()`. Test równoważności parsuje dzień przez `DAYS_OF_WEEK` (odpowiednik `DayOfWeek.valueOf`).

### Co widzimy

`ShowPricing.price(day, format)` ma tabelę cen 3x3 wpisaną w zagnieżdżone warunki. Dwie osie zmienności - format i reguła dnia ("tani wtorek" -30%, weekend +2.00) - są splecione.

```typescript
if (format === '2D') {
  return Money.of(day === 'TUESDAY' ? '17.50' : weekend ? '27.00' : '25.00');
} else if (format === '3D') {
  return Money.of(day === 'TUESDAY' ? '22.40' : weekend ? '34.00' : '32.00');
} ...
```

Ta scena ma **dwie ścieżki**: krok 1 jest wspólny, krok 2 to ścieżka A (Strategy dla reguły dnia), krok 3 to ścieżka B (typ formatu z zachowaniem) - budowana od kroku 1, nie od kroku 2. Test równoważności sprawdza pełną tabelę dla obu ścieżek.

### Krok 1: rozplecenie osi (wspólny)

**W IDE:** zauważ, że tabela to dwie niezależne reguły. ⌃⇧R > Extract to method in class: `basePrice(format)` ze `switch` po formacie i `adjustForDay(day, base)` ze `switch` po dniu (w porcie obie `private static`). `price` = złożenie.
**Po:**

```typescript
price(day: DayOfWeek, format: string): Money {
  return ShowPricing.adjustForDay(day, ShowPricing.basePrice(format));
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s20` - zielone (9 cen i nieznany format).
**Co powiedzieć:** to jest moment decyzji. Ten sam `switch` może sygnalizować różne problemy - pytamy, **która oś zmienia się częściej**.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s20 0 1`

### Krok 2: ścieżka A - zmienia się reguła dnia (Strategy)

**W IDE:** typ `DayPolicy = (base: Money) => Money`, stałe `CHEAP_TUESDAY`, `WEEKEND`, `REGULAR` i kalendarz `DayPolicies.standard(day)`. `ShowPricing` dostaje kalendarz w konstruktorze (parametr domyślny - kalendarz standardowy). Format zostaje prostym `switch`.
**Po:**

```typescript
return this.calendar(day)(ShowPricing.basePrice(format));
```

**Uruchom:** test zielony; `pathAMakesANewDayCampaignCheap` dodaje "środę seniora" bez dotykania formatów.
**Co powiedzieć:** wybierz tę ścieżkę, gdy marketing co miesiąc zmienia akcje dniowe, a formaty są stabilne. Zestaw polityk jest otwarty i wstrzykiwany.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s20 1 2`

### Krok 3: ścieżka B - zmienia się zestaw formatów (typ z zachowaniem)

**W IDE:** pokaż alternatywę od kroku 1: klasa `Format` ze stałymi instancjami, ceną bazową i metodą `priceOn(day)`, `Format.of(code)` z tym samym wyjątkiem. `ShowPricing` deleguje. Reguła dnia zostaje zwykłym `switch` w typie.
**Po:**

```typescript
price(day: DayOfWeek, format: string): Money {
  return Format.of(format).priceOn(day);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** wybierz tę ścieżkę, gdy dochodzą formaty (4DX, ScreenX) z własnymi wyjątkami od reguł, a kalendarz jest stabilny. Porównaj `scripts/warsztat.sh --lang ts diff m5/s20 1 3` z `diff m5/s20 1 2`.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s20 1 3` (alternatywa dla kroku 2; `diff 2 3` pokazuje zamianę ścieżki)

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
**Katalog:** `typescript/src/workshop/m5/s21_reversiblepattern` · **Test:** `scripts/warsztat.sh --lang ts test m5/s21` (`S21EquivalenceTest.test.ts`, `S21FestivalVariantTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** Rozliczenie z dystrybutorem ma dwa modele w jednym `switch`, w tym festiwalowy z obcego systemu w groszach. Wprowadzamy Strategy z Adapterem, a gdy umowy festiwalowe wygasają, usuwamy wariant i cały wzorzec.

**Zasada:** Wzorzec to odpowiedź na konkretne siły (tu: dwa istniejące warianty i obcy interfejs), a nie kod do skopiowania. Strategy opłaca się dla rodziny wymiennych algorytmów, Adapter dla realnego tłumaczenia obcego modelu, a usunięcie wzorca jest równie poprawną refaktoryzacją jak jego wprowadzenie.

**Efekt:** Zachowanie świadomie się zmienia: umowy FESTIVAL są teraz odrzucane, a model procentowy liczy tak samo na każdym etapie. Zostaje prosta metoda bez interfejsu i mapy, którą w razie potrzeby rozbudujemy tymi samymi krokami w przód.

**Różnica względem Javy:** grosze to `number` (w Javie `long`), a konwersja `BigDecimal.valueOf(cents, 2)` to `new Decimal(cents).dividedBy(100)`.

### Co widzimy

Rozliczenie z dystrybutorem ma dwa istniejące modele: procentowy (tydzień 1: 50%, 2: 40%, dalej 35%, minimalna gwarancja 500.00) i festiwalowy (stawka z obcego systemu `FestivalTariffClient`, w groszach jako liczba całkowita). Oba siedzą w jednym `switch`, konwersja jednostek jest wpleciona w logikę rozliczeń.

```typescript
case 'FESTIVAL': {
  const cents = this.festival.weeklyFeeInCents(deal.title, week);
  return new Decimal(cents).dividedBy(100);
}
```

### Krok 1: Replace Conditional with Strategy (i Adapter dla obcego klienta)

**W IDE:** utwórz interfejs `SettlementModel` z metodą `payout(deal, week, ticketRevenue)`. Każdą gałąź `switch` wydziel (⌃⇧R > Extract to method in class) i przenieś ręcznie do klasy: `PercentageModel` i `FestivalFeeAdapter` (opakowuje `FestivalTariffClient`, konwertuje grosze). `DistributorSettlement` wybiera model z mapy.
**Po:**

```typescript
private readonly models: ReadonlyMap<string, SettlementModel> = new Map<string, SettlementModel>([
  ['PERCENT', new PercentageModel()],
  ['FESTIVAL', new FestivalFeeAdapter(new FestivalTariffClient())],
]);
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s21` - 18 testów zielonych; `step1AdapterPaysTheFestivalFeeConvertedFromCents` sprawdza 300.00 i 150.00.
**Co powiedzieć:** uzasadnienie to dwa **istniejące** warianty z różnymi właścicielami i obcy interfejs. Adapter ma realną pracę (konwersja jednostek), nie jest pustym przekazaniem 1:1.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s21 0 1`

### Krok 2: Wariant znika - usunięcie adaptera

**W IDE:** umowy festiwalowe wygasły. Usuń wpis `'FESTIVAL'` z mapy, potem plik `FestivalFeeAdapter.ts` (⇧⌥F12 potwierdza brak użyć) i niepotrzebny import `FestivalTariffClient`.
**Po:**

```typescript
private readonly models: ReadonlyMap<string, SettlementModel> = new Map<string, SettlementModel>([
  ['PERCENT', new PercentageModel()],
]);
```

**Uruchom:** test zielony; `afterTheVariantIsGoneFestivalDealsAreRejected` - to świadoma zmiana zachowania, dlatego sprawdzana osobno, a nie testem równoważności.
**Co powiedzieć:** zostaje Strategy z jedną implementacją i mapą z jednym wpisem - klasyczny sygnał nadmiaru wzorca ze slajdu 17.1.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s21 1 2`

### Krok 3: Usunięcie wzorca

**W IDE:** ręcznie (VS Code nie ma Inline Method): wklej ciało `PercentageModel.payout` w miejsce wywołania w `DistributorSettlement.payout`, wybór z mapy zastąp warunkiem `deal.model !== 'PERCENT'`, usuń mapę oraz pliki `PercentageModel.ts` i `SettlementModel.ts`. Stałą `MINIMUM_GUARANTEE` przenieś do `DistributorSettlement` jako `private static readonly`.
**Po:**

```typescript
payout(deal: Deal, week: number, ticketRevenue: Decimal): Decimal {
  if (deal.model !== 'PERCENT') {
    throw new IllegalArgumentError(`nieznany model: ${deal.model}`);
  }
  const percent = week === 1 ? 50 : week === 2 ? 40 : 35;
```

**Uruchom:** test zielony - model procentowy rozlicza się tak samo przed wzorcem, ze wzorcem i po jego usunięciu.
**Co powiedzieć:** usunięcie wzorca jest równie poprawną refaktoryzacją jak jego wprowadzenie. Gdy wróci drugi model, przywrócimy Strategy tymi samymi krokami w przód.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s21 2 3`

### Rozwiązanie i uzasadnienie

Wzorzec był odpowiedzią na konkretne siły (dwa warianty, obcy interfejs). Gdy siły zniknęły, zniknął też wzorzec. Test równoważności chroni oba kierunki.

### Pułapki

- Zostawienie interfejsu "bo może wróci" - to YAGNI w drugą stronę (moduł 2, scena s01).
- Adapter, który tylko przekazuje wywołania 1:1 bez tłumaczenia modelu, błędów czy jednostek.
- Dopasowanie obcego klienta na siłę do kontraktu, którego obietnic nie spełnia (np. determinizm albo synchroniczność przy wywołaniu sieciowym - w TS klient sieciowy zwróciłby `Promise`).

### Pytanie do sali

Który wzorzec w waszym kodzie przeżył powód, dla którego go wprowadzono? Co by kosztowało jego usunięcie?

## Scena s22. Remove Middle Man - fasada kina

**Temat ze slajdów:** 17.3. Usunięcie pośrednika; 17.2. Refaktoryzacje, które usuwają wzorzec
**Katalog:** `typescript/src/workshop/m5/s22_middleman` · **Test:** `scripts/warsztat.sh --lang ts test m5/s22` (`S22EquivalenceTest.test.ts`)
**Czas:** ~8 min

### W skrócie

**Co robimy:** `CinemaFacade` prawie wszystko deleguje 1:1 do `ScreeningCatalog`, ale w `freeSeats` po cichu zamienia nieznany seans na 0 wolnych miejsc. Przenosimy to tłumaczenie do `SeatBadge`, migrujemy obu klientów do katalogu po jednym i usuwamy fasadę.

**Zasada:** Remove Middle Man usuwa pośrednika, który tylko przekazuje wywołania dalej, i pozwala klientom rozmawiać bezpośrednio z właściwym obiektem - ruch odwrotny to Hide Delegate. Najpierw sprawdzamy, czy pośrednik nie robi czegoś więcej (autoryzacja, transakcja, telemetria, retry, translacja błędów), bo fasada modułu czy seam testowy mogą być wartościowe.

**Efekt:** Klienci zależą bezpośrednio od `ScreeningCatalog`, a zachowanie "nieznany seans = WYPRZEDANE" zostało zachowane w jedynym miejscu, które go potrzebuje. To dziwne zachowanie jest teraz widoczne i czeka na osobną decyzję.

**Różnica względem Javy:** `NoSuchElementException` nie ma odpowiednika we wspólnym `src/shared/errors.ts`, więc `NoSuchElementError` jest zdefiniowany i eksportowany w kontrakcie sceny `ScreeningCatalog.ts`. TypeScript nie ma `catch` po typie, więc `catch (NoSuchElementException e)` to `catch (error)` z `instanceof NoSuchElementError`, a inne błędy są rzucane dalej. `LinkedHashMap` to `Map` (też zachowuje kolejność wstawiania).

### Co widzimy

`CinemaFacade` deleguje 1:1 do `ScreeningCatalog` - prawie. `freeSeats` po cichu tłumaczy `NoSuchElementError` na 0, więc `SeatBadge` pokazuje "WYPRZEDANE" dla nieznanego seansu. Dwóch klientów: `SeatBadge` i `DailyBoard`.

```typescript
freeSeats(id: string): number {
  try {
    return this.catalog.freeSeats(id);
  } catch (error) {
    if (error instanceof NoSuchElementError) {
      return 0;
    }
    throw error;
  }
}
```

### Krok 1: pośrednik staje się czystym forwarderem

**W IDE:** przenieś `try/catch` do prywatnej metody `freeSeats` w `SeatBadge` (jedyny klient, który polega na tłumaczeniu). W fasadzie zostaje zwykłe delegowanie.
**Po:**

```typescript
// SeatBadge
private freeSeats(id: string): number {
  try {
    return this.cinema.freeSeats(id);
  } catch (error) {
    if (error instanceof NoSuchElementError) {
      return 0;
    }
    throw error;
  }
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s22` - 13 testów zielonych, przypadek "nieznany seans wyglada jak wyprzedany" nadal zielony.
**Co powiedzieć:** najpierw sprawdź, co pośrednik naprawdę robi: autoryzacja, transakcja, telemetria, retry, translacja błędów. Tu była translacja - przenosimy ją do klienta, zanim usuniemy pośrednika.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s22 0 1`

### Krok 2: pierwszy klient rozmawia z katalogiem

**W IDE:** w `SeatBadge` ręcznie zmień typ parametru konstruktora z `CinemaFacade` na `ScreeningCatalog`, a pole F2 (Rename Symbol) na `catalog`. Metody `title`, `format` i `freeSeats` katalog ma o tych samych sygnaturach, więc reszta kodu się kompiluje.
**Po:**

```typescript
constructor(catalog: ScreeningCatalog) {
  this.catalog = requireNonNull(catalog, 'catalog');
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** jeden klient naraz. `DailyBoard` wciąż używa fasady i działa.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s22 1 2`

### Krok 3: ostatni klient i usunięcie fasady

**W IDE:** ten sam ruch w `DailyBoard` (`cinema.screenings()` na `catalog.all()`), potem Find All References (⇧⌥F12) na `CinemaFacade` - brak użyć - i usunięcie pliku.
**Po:**

```typescript
return this.catalog.all()
  .map((screening) => `${screening.id} ${screening.title} ${screening.format}`)
  .join('\n');
```

**Uruchom:** test zielony.
**Co powiedzieć:** odwrotny ruch to Hide Delegate. Fasada modułu albo seam testowy mogą być wartościowe - usuwamy pośrednika, który nic nie wnosi.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s22 2 3`

### Rozwiązanie i uzasadnienie

`step3`: dwaj klienci zależą od `ScreeningCatalog`, zachowanie dla nieznanego seansu zachowane w jedynym miejscu, które go potrzebuje. Test `catalogItselfRejectsUnknownScreening` pokazuje, dlaczego krok 1 był konieczny.

### Pułapki

- Inline wszystkich metod fasady naraz (ręcznie albo wyszukaj-i-zamień `cinema.` na `catalog.`) bez sprawdzenia, że któraś tłumaczy błędy - kompilator nic nie zgłosi, bo sygnatury są identyczne.
- Usunięcie fasady, która jest granicą modułu (klienci z innych katalogów zaczną zależeć od szczegółów - w TS nie ma widoczności pakietowej, która by ich zatrzymała, co najwyżej pole `exports` w `package.json`).
- Utrwalenie dziwnego zachowania ("nieznany = wyprzedany") bez notatki - to kandydat na osobną decyzję.

### Pytanie do sali

Czy "nieznany seans = WYPRZEDANE" to błąd? Kiedy wolno go poprawić?

## Scena s23. Refaktoryzacja a zmiana kontraktu - zwrot na BigDecimal

**Temat ze slajdów:** 5.3. Refaktoryzacja a zmiana kontraktu; 5.4. Co trzeba zachować przy wprowadzaniu wzorca; 3.10. Bezpieczna pętla refaktoryzacji do wzorca
**Katalog:** `typescript/src/workshop/m5/s23_contractchange` · **Test:** `scripts/warsztat.sh --lang ts test m5/s23` (`S23ContractTest.test.ts`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** `RefundCalculator.refund` liczy zwrot na `number` (binarny zmiennoprzecinkowy, jak `double`), a kontraktem jest też format z dwoma miejscami po przecinku. Robimy jedną czystą refaktoryzację, potem pokazujemy naiwne przejście na `Decimal`, które zmienia dwie rzeczy naraz, i na koniec zostawiamy tylko uzgodnioną zmianę.

**Zasada:** Refaktoryzacja zachowuje obserwowalne zachowanie w przyjętej granicy, a kryterium brzmi: klient nie dostrzega żadnej nieuzgodnionej różnicy. Zmiana typu, zaokrąglenia, formatu czy wyjątku to zmiana kontraktu, którą robimy i zatwierdzamy osobno od ruchów strukturalnych, a nie "przy okazji".

**Efekt:** Zachowanie świadomie się zmienia w jednym punkcie: 64.35 anulowane 2 godziny przed seansem daje 29.18 zamiast 29.17, zatwierdzone w osobnym commicie. Format "0.00" zostaje, a naiwny krok 2 nie powinien trafić do repozytorium.

**Różnica względem Javy:** tytuł sceny zostaje (to temat ze slajdów), ale w porcie `BigDecimal` to `Decimal` z decimal.js. Pułapka formatu w kroku 2 musiała dostać inną postać. W Javie `BigDecimal.ZERO` drukuje się jako `"0"`, a pozostałe kwoty zachowują skalę 2; decimal.js nie przechowuje skali, więc `toString()` psułby także zwykłe zwroty. Dlatego w TS naiwny krok 2 robi "przy okazji" dwie rzeczy: przejście `number` -> `Decimal` (zaokrąglenie HALF_UP na dokładnej wartości 29.175 -> 29.18) i krótsze obcięcie do zera guard clause `if (refund.lessThanOrEqualTo(0)) return '0';` - zero wychodzi jako `"0"` zamiast `"0.00"`. Krok 3 wraca do jednej ścieżki: `Decimal.max(..., 0).toFixed(2, Decimal.ROUND_HALF_UP)`. Oczekiwania testu są identyczne jak w Javie, zmienił się tylko komunikat asercji (`"format: '0' zamiast 0.00"`).

### Co widzimy

`RefundCalculator.refund` liczy zwrot na `number` - dokładnie jak w legacy: 100%, 50% albo 0%, minus 3.00, nie poniżej zera, zaokrąglenie `Math.round(x * 100) / 100.0`, format `toFixed(2)`. Kontraktem jest także **format**: zawsze dwa miejsca po przecinku.

```typescript
refund = refund - 3.00;
if (refund < 0) {
  refund = 0;
}
refund = Math.round(refund * 100) / 100.0;
return refund.toFixed(2);
```

### Krok 1: czysta refaktoryzacja

**W IDE:** zaznacz łańcuch `if/else` wyboru udziału, ⌃⇧R > Extract to method in class, nazwa `share`, zwraca `number` (0, 1.0, 0.5) - ręcznie zamień przypisania na `return` i oznacz metodę `private static`. W `refund` jedno wyrażenie `ticketsPaid * share(minutes) - 3.00`.
**Po:**

```typescript
let refund = ticketsPaid * RefundCalculator.share(minutesBeforeStart) - 3.00;
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s23` - 19 testów zielonych, łącznie z przypadkami brzegowymi (29.17 i 0.00).
**Co powiedzieć:** `x * 1.0 === x` w arytmetyce zmiennoprzecinkowej - wynik jest identyczny co do bitu. To jest refaktoryzacja.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s23 0 1`

### Krok 2: "przy okazji" number na Decimal

**W IDE:** pokaż naiwny ruch: `share` zwraca `Decimal`, liczymy `new Decimal(ticketsPaid).times(...).minus(FEE)`, a "przy okazji" skracamy obcięcie do zera do guard clause.
**Po:**

```typescript
const refund = new Decimal(ticketsPaid)
  .times(RefundCalculator.share(minutesBeforeStart))
  .minus(RefundCalculator.FEE);
if (refund.lessThanOrEqualTo(0)) {
  return '0';
}
return refund.toFixed(2, Decimal.ROUND_HALF_UP);
```

**Uruchom:** test zwykłych zwrotów zielony, ale `byTheWayStepChangedTwoThingsAtOnce` dokumentuje dwie zmiany: 64.35 zapłacone, anulowanie 2 h przed seansem daje 29.18 zamiast 29.17, a zwrot po starcie to "0" zamiast "0.00".
**Co powiedzieć:** krok wyglądał na porządki, a zmienił dwa elementy kontraktu. Pierwszy (grosz) można uzasadnić regułą domeny, drugi (format) to czysta regresja - paragon i mail pokażą "Zwrot: 0".
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s23 1 2`

### Krok 3: świadoma decyzja

**W IDE:** zastąp guard clause obcięciem `Decimal.max(..., 0)` przed formatowaniem - jedna ścieżka wyniku i format "0.00" wraca. Różnicę groszową zatwierdzamy jako zmianę kontraktu: komentarz TSDoc z decyzją, nowe oczekiwanie w teście, osobny commit.
**Po:**

```typescript
const refund = Decimal.max(
  new Decimal(ticketsPaid)
    .times(RefundCalculator.share(minutesBeforeStart))
    .minus(RefundCalculator.FEE),
  0);
return refund.toFixed(2, Decimal.ROUND_HALF_UP);
```

**Uruchom:** test zielony: `deliberateStepChangesOnlyTheApprovedRounding`.
**Co powiedzieć:** kryterium refaktoryzacji to "klient nie dostrzega żadnej **nieuzgodnionej** różnicy". Różnicę w groszach uzgodniliśmy, różnicy w formacie nie.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s23 2 3`

### Rozwiązanie i uzasadnienie

`step3/RefundCalculator`: `Decimal` i HALF_UP zgodnie z regułą domeny, format bez zmian. Historia commitów: refaktoryzacja (krok 1), zmiana typu i reguły zaokrąglenia jako jawna zmiana kontraktu (krok 3). Krok 2 nie powinien trafić do repozytorium.

### Pułapki

- Test tylko na "okrągłych" kwotach - `number` i `Decimal` dają ten sam wynik i zmiana przechodzi niezauważona.
- `Number.prototype.toFixed` i `Math.round(x * 100) / 100` działają na rozwinięciu binarnym: 29.175 to w pamięci 29.17499..., stąd 29.17. Ta sama pułapka co `new BigDecimal(double)` w Javie, tylko ukryta w "zwykłym" formatowaniu (`(1.005).toFixed(2)` daje `"1.00"`).
- decimal.js nie przechowuje skali: `new Decimal('0.00').toString()` to `"0"`, a `equals` porównuje tylko wartość. Format kwoty musi być jawny (`toFixed(2, ...)`), inaczej zmienia się przy pierwszym zerze albo okrągłej kwocie.
- `toFixed(2)` na `Decimal` bez trybu zaokrąglenia używa globalnego `Decimal.rounding` - ktoś może go zmienić przez `Decimal.set(...)`.
- Zmiana typu wyjątku, kolejności efektów albo formatu "bo i tak ruszamy ten kod".

### Pytanie do sali

Kto zatwierdza zmianę kontraktu o 1 grosz: programista, księgowość czy klient API?

## Scena s24. Rename - nazwy, które żyją poza kodem

**Temat ze slajdów:** 3.6. Refaktoryzacje automatyczne w IDE: co dają, a czego nie widzą; 3.7. Zmiana nazwy i wyodrębnienie metody w drodze do wzorca
**Katalog:** `typescript/src/workshop/m5/s24_rename` · **Test:** `scripts/warsztat.sh --lang ts test m5/s24` (`S24EquivalenceTest.test.ts`, `S24RenameLimitsTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** W `SalesReport` nazwy `calc2`, `s`, `m`, `x` nic nie mówią, ale nazwa metody żyje w konfiguracji, a nazwy pól klasy `Line` trafiają przez `Object.keys` do nagłówka CSV. Zmieniamy nazwy od najbezpieczniejszych lokalnych do tych, które są kontraktem zewnętrznym.

**Zasada:** Rename nadaje nazwę opisującą rolę w kontekście, a nie typ ani implementację. Dla zmiennych lokalnych IDE robi to bezpiecznie, ale nazwa użyta w konfiguracji, wywołaniu po nazwie (`obj[name]`), JSON czy ORM jest częścią API i wymaga strategii migracji. IDE nie widzi takich użyć, więc nie mylić operacji F2 z bezpieczną zmianą kontraktu.

**Efekt:** Kod czyta się po nazwach ról, a oba kontrakty zostają nietknięte: stara nazwa `calc2` jako przestarzały delegat, nagłówek CSV jako jawna stała. Koszt to delegat, którego nie wolno usunąć, dopóki ktoś nie zmieni konfiguracji na serwerach.

**Różnica względem Javy:** refleksja Javy ma w TypeScript dwa odpowiedniki. `ReportJob` wywołuje metodę po nazwie z tekstu konfiguracji (`report[name].call(...)`, brak metody kończy się `IllegalStateError`). Nagłówek CSV liczy `Object.keys(...)` wiersza, a sam wiersz `Object.values(...)` - to odpowiednik `getRecordComponents()`. `Line` jest eksportowaną klasą z polami `readonly` (zamiast rekordu), a kwoty to `Money`, który w wydruku zachowuje dwa miejsca po przecinku. Adnotację `@Deprecated` zastępuje tag JSDoc `@deprecated`.

### Co widzimy

`SalesReport.calc2(s: readonly Sale[], flag: boolean)` składa CSV dla dystrybutora: `m`, `x`, `l`, `b`, a wiersz to `Line(t: string, n: number, d: Money)`. Dwie nazwy żyją poza kodem:

```typescript
// ReportJob - w produkcji plik report.properties na serwerze
static readonly CONFIG = `
report.method=calc2
report.onlineOnly=true
`;
...
// Wywołanie po nazwie z tekstu - odpowiednik refleksji (getMethod + invoke).
const method: unknown = (this.report as unknown as Record<string, unknown>)[methodName];

// SalesReport - nagłówek CSV liczony z nazw pól wiersza
function header(): string {
  return Object.keys(new Line('', 0, Money.ZERO)).join(';');
}
```

### Krok 1: Rename zmiennych lokalnych i parametrów

**W IDE:** kursor na `s`, F2, `sales`. Tak samo `flag` -> `onlineOnly`, `m` -> `linesByTitle`, `x` -> `sale`, `l` -> `previous` / `line`, `b` -> `csv`, `v` -> `values`, `c` -> `component`.
**Po:**

```typescript
calc2(sales: readonly Sale[], onlineOnly: boolean): string {
  const linesByTitle = new Map<string, Line>();
  for (const sale of sales) {
    if (onlineOnly && !sale.online) {
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s24` - 14 testów zielonych.
**Co powiedzieć:** zasięg lokalny, zero użyć poza metodą, więc IDE robi to w stu procentach bezpiecznie. Od tego zaczynamy każde porządkowanie, bo tanie nazwy ułatwiają następne kroki.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s24 0 1`

### Krok 2: Rename metody z delegatem dla starej nazwy

**W IDE:** najpierw na żywo naiwnie: F2 na `calc2`, nazwa `revenueCsv`. VS Code poprawi wszystkie wywołania w kodzie, ale nie tekst `report.method=calc2`. Uruchom test: `everyStepRunsTheConfiguredJob` jest czerwony (`IllegalStateError: Zadanie raportu nie działa: brak metody calc2`). IDE nie wie, że tekst w konfiguracji to nazwa metody. Cofnij (⌘Z). Teraz poprawnie: skopiuj metodę, zmień nazwę kopii na `revenueCsv`, a ciało `calc2` zamień na delegowanie i oznacz w JSDoc `@deprecated`.
**Po:**

```typescript
/**
 * Stara nazwa z konfiguracji report.properties (report.method=calc2).
 * Usunąć dopiero, gdy żaden serwer nie ma jej w konfiguracji.
 *
 * @deprecated użyj {@link revenueCsv}
 */
calc2(sales: readonly Sale[], onlineOnly: boolean): string {
  return this.revenueCsv(sales, onlineOnly);
}

revenueCsv(sales: readonly Sale[], onlineOnly: boolean): string {
```

**Uruchom:** test zielony, zadanie z konfiguracji działa. `S24RenameLimitsTest.configurationStillNamesTheOldMethod` sprawdza, że na prototypie są obie metody.
**Co powiedzieć:** konfiguracja leży na serwerach, poza naszym repozytorium, więc nie zmienimy jej w tym samym commicie. Stara nazwa zostaje jako przestarzały delegat, dopóki ktoś nie zmieni konfiguracji wszędzie. To jest strategia migracji ze slajdu. VS Code przekreśli wywołania `calc2` w kodzie, więc nowi klienci od razu widzą, której nazwy używać.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s24 1 2`

### Krok 3: Rename pól wiersza po odcięciu kontraktu CSV

**W IDE:** najpierw naiwnie: F2 na `t` w `Line`, `title`. Test pokaże nagłówek `title;n;d` zamiast `t;n;d`: plik dla dystrybutora właśnie się zmienił. Cofnij. Teraz poprawnie: najpierw zamień `header()` i `row()` oparte na `Object.keys`/`Object.values` na jawną stałą `CSV_HEADER = 't;n;d'` i jawne składanie wiersza (test zielony), dopiero potem F2 na `t`, `n`, `d` -> `title`, `tickets`, `revenue`.
**Po:**

```typescript
/** Nagłówek uzgodniony z dystrybutorem - kontrakt zewnętrzny, NIE nazwy pól w kodzie. */
export const CSV_HEADER = 't;n;d';

export class Line {
  constructor(readonly title: string, readonly tickets: number, readonly revenue: Money) {}
}

function row(line: Line): string {
  return line.title + ';' + line.tickets + ';' + line.revenue.toString();
}
```

**Uruchom:** test zielony, także `S24RenameLimitsTest.reflectionTurnsJavaNamesIntoTheCsvHeader`, który pokazuje, że `Object.keys` na nowej klasie dałoby `title;tickets;revenue`.
**Co powiedzieć:** nazwa w kodzie i nazwa w kontrakcie zewnętrznym to dwie różne rzeczy. Dopóki są sklejone przez `Object.keys`, każdy Rename jest zmianą API. To samo dotyczy `JSON.stringify` - nazwy pól obiektu stają się kluczami w JSON.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s24 2 3`

### Rozwiązanie i uzasadnienie

`step3`: wszystkie nazwy mówią o roli (`revenueCsv`, `onlineOnly`, `linesByTitle`, `Line.revenue`), a dwa kontrakty zewnętrzne (nazwa metody w konfiguracji i nagłówek CSV) są zachowane jawnie: przez delegat i przez stałą. Oba testy równoważności (wywołanie z kodu i zadanie z konfiguracji) są zielone na każdym etapie.

### Pułapki

- Poleganie na tym, że Rename w VS Code "znajdzie wszystko". Rename Symbol działa na typach i nie zmienia tekstu w stringach, a globalne szukaj-zamień (⇧⌘H) nie widzi plików poza projektem ani nazw składanych dynamicznie (`'calc' + version`).
- Inne miejsca, w których nazwa jest kontraktem: `JSON.stringify` / `JSON.parse` (klucze obiektu), kolumny ORM, szablony, nazwy pól w odpowiedziach REST, dostęp `obj[name]`, logi parsowane przez monitoring.
- Usunięcie delegatu `calc2` "bo nikt go nie woła". Go to References (⇧F12) go nie znajdzie, bo woła go tylko konfiguracja przez `report[name]`.

### Pytanie do sali

Jak w waszym systemie sprawdzić, czy nazwa klasy albo metody nie jest użyta w konfiguracji, bazie danych lub innym repozytorium?

## Scena s25. Extract Method - przepływ danych i wiele wyjść

**Temat ze slajdów:** 3.7. Zmiana nazwy i wyodrębnienie metody w drodze do wzorca
**Katalog:** `typescript/src/workshop/m5/s25_extractmethod` · **Test:** `scripts/warsztat.sh --lang ts test m5/s25` (`S25EquivalenceTest.test.ts`)
**Czas:** ~15 min

### W skrócie

**Co robimy:** `TicketSummary.describe` liczy cenę, sumę i liczbę miejsc VIP, a potem składa dokument, i to wszystko w jednej metodzie z komentarzami zamiast nazw. Wydzielamy bloki do metod, a pętlę z dwoma wyjściami najpierw rozdzielamy przez Split Loop.

**Zasada:** Extract Method przenosi spójny fragment do metody, gdy nazwa wyrazi intencję lepiej niż szczegóły, a sama długość nie jest powodem. Kluczowa jest analiza przepływu danych: co wchodzi jako parametr, co wychodzi jako wynik. Gdy fragment ma kilka wyjść, dzielimy go albo tworzymy obiekt wyniku z nazwą, a nie zwracamy tablicy czy anonimowego "worka" na dwie wartości.

**Efekt:** Metoda publiczna czyta się jak spis treści, a obliczenia są oddzielone od renderowania, bez zmiany sygnatury ani formatu. Kosztem są dwa przejścia po liście rzędów, pomijalne wobec czytelności.

**Różnica względem Javy:** IntelliJ odmawia wydzielenia fragmentu z dwiema wartościami wyjściowymi. VS Code nie odmawia: wydzielona metoda zwraca wtedy obiekt `{ subtotal, vipSeats }`, a w miejscu wywołania pojawia się destrukturyzacja. Kod się kompiluje i test jest zielony, ale to dokładnie "holder" z pułapek - dwie odpowiedzialności w jednej metodzie. Kwoty to `Decimal`, w wydruku `toFixed(2)`.

### Co widzimy

`TicketSummary.describe` liczy cenę bazową, sumę i liczbę miejsc VIP, a na końcu składa dokument. Komentarze `// cena bazowa formatu`, `// suma i liczba miejsc VIP`, `// dokument` to naturalne granice metod. Pętla ma jednak **dwa wyjścia**: `subtotal` i `vipSeats`.

```typescript
let subtotal = new Decimal(0);
let vipSeats = 0;
for (const row of order.rows) {
  let price = base;
  if (row >= 10) {
    price = price.plus(new Decimal('10.00'));
    vipSeats++;
  }
  subtotal = subtotal.plus(price);
}
subtotal = subtotal.toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
```

### Krok 1: Extract Method dla bloku z jednym wyjściem

**W IDE:** zaznacz blok pod `// cena bazowa formatu` (od `let base: Decimal;` do końca `if` z porankiem), ⌃⇧R, "Extract to method in class 'TicketSummary'", nazwa `basePrice`. VS Code sam wykryje jedno wejście (`order`) i jedno wyjście (`base`). Usuń zbędny komentarz.
**Po:**

```typescript
const base = this.basePrice(order);
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s25` - 16 testów zielonych.
**Co powiedzieć:** blok z jednym wyjściem to najprostszy przypadek, IDE robi to bezpiecznie. Komentarz stał się nazwą metody.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s25 0 1`

### Krok 2: Split Loop, potem dwa razy Extract Method

**W IDE:** zaznacz pętlę razem z deklaracjami `subtotal` i `vipSeats` i otwórz ⌃⇧R. Pokaż podgląd "Extract to method": VS Code zaproponuje metodę zwracającą `{ subtotal, vipSeats }`. To jest moment dydaktyczny - IDE nie widzi, że to dwie odpowiedzialności. Zamknij bez zmian (Esc). Ręcznie skopiuj pętlę (Split Loop) i z pierwszej kopii usuń `vipSeats`, a z drugiej `subtotal`. Uruchom test. Następnie na każdej pętli ⌃⇧R, "Extract to method", nazwy `subtotal` i `vipSeats`.
**Po:**

```typescript
const subtotal = this.subtotal(order, base);
const vipSeats = this.vipSeats(order);
```

**Uruchom:** test po Split Loop i po każdej ekstrakcji.
**Co powiedzieć:** zamiast zwracać parę (obiekt z dwoma polami) rozdzielamy odpowiedzialności. Dwa przejścia po liście to koszt pomijalny wobec czytelności. Jeśli wydajność miałaby znaczenie, mierzymy, a nie zgadujemy.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s25 1 2`

### Krok 3: Extract Method dla renderowania

**W IDE:** zaznacz blok `// dokument` i wykonaj ⌃⇧R, "Extract to method", nazwa `render`. Parametry: `order`, `subtotal`, `vipSeats`. Potem kursor na `base`, ⌃⇧R, "Inline variable".
**Po:**

```typescript
describe(order: Order): string {
  const subtotal = this.subtotal(order, this.basePrice(order));
  const vipSeats = this.vipSeats(order);
  return this.render(order, subtotal, vipSeats);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** metoda publiczna czyta się jak spis treści. Obliczenia są oddzielone od prezentacji, więc przygotowaliśmy grunt pod Extract Class.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s25 2 3`

### Rozwiązanie i uzasadnienie

`step3/TicketSummary.ts`: cztery prywatne metody, każda z jednym wyjściem. Nie zmieniliśmy sygnatury publicznej ani formatu dokumentu, co potwierdza test równoważności na czterech przypadkach (w tym zamówienie puste i same miejsca VIP).

### Pułapki

- Ekstrakcja bloku, który modyfikuje zmienną lokalną używaną dalej, bez zwrócenia jej wartości. IDE tego pilnuje, ręczne kopiowanie nie.
- Zwracanie tablicy (`[subtotal, vipSeats]`) albo anonimowego obiektu tylko po to, żeby wyciągnąć dwa wyniki. VS Code zrobi to sam, jeśli mu pozwolimy.
- Ekstrakcja fragmentu z `this` do funkcji w zasięgu modułu ("Extract to function in module scope") zamiast do metody klasy. VS Code zaproponuje oba warianty, a wybór zmienia miejsce i dostęp do pól.
- Nazwa metody opisująca "jak" (`loopRows`) zamiast "co" (`vipSeats`), albo domyślne `newMethod` z VS Code.

### Pytanie do sali

Kiedy zamiast Split Loop lepiej zwrócić obiekt z dwoma polami (i jak go wtedy nazwać)?

## Scena s26. Strategy, Command i State w jednej klasie - pulpit kasjera

**Temat ze slajdów:** 6.1. Strategia, Polecenie czy Stan? Kto wybiera wariant i kiedy; 6.7. Trzy osie w jednej klasie: każda dostaje własny mechanizm; 6.8. Typowe pomyłki przy wyborze
**Katalog:** `typescript/src/workshop/m5/s26_variability` · **Test:** `scripts/warsztat.sh --lang ts test m5/s26` (`S26EquivalenceTest.test.ts`, `S26SolutionTest.test.ts`)
**Czas:** ~15 min

### W skrócie

**Co robimy:** `BoxOfficeDesk.handle` obsługuje polecenia kasjera BOOK, PAY, USE, CANCEL, UNDO i REPORT jednym `switch` po nazwie operacji, w którym siedzi `switch` po statusie rezerwacji, a w rezerwacji `switch` po typie biletu. Rozplątujemy trzy osie po kolei: cenę biletu jako Strategy (`TicketPricing`), operacje kasjera jako Command z cofaniem (`DeskCommand` i odbiorca `BookingLedger`), cykl życia rezerwacji jako State (`Booking` z prywatnym typem stanu `BookingState`).

**Zasada:** O mechanizmie decyduje pytanie "kto wybiera wariant i kiedy?", a nie wygląd kodu - wszystkie trzy `switch` wyglądają tak samo. Strategy: wariant wybiera klient na podstawie danych (typ biletu), raz. Command: żądanie z zewnątrz, które trzeba zapisać w dzienniku i umieć cofnąć. State: wariant zmienia sam obiekt w wyniku własnych operacji (CANCEL w NEW zwalnia rezerwację, w PAID oddaje pieniądze). Wprowadzamy jedną oś naraz.

**Efekt:** Nowy typ biletu, nowa operacja i nowy status to zmiany w trzech rozłącznych miejscach. Odpowiedzi pulpitu, stan kasy i dziennik są identyczne w start i w każdym kroku, łącznie z komunikatami błędów, kolejnością walidacji i tym, że nieudane polecenie nie trafia do dziennika.

**Różnica względem Javy:** `@FunctionalInterface TicketPricing` to interfejs z jedną metodą `price(formatBase)`, a lambdy z mapy strategii to literały obiektów (`{ price: (base) => ... }`). Statyczna metoda interfejsu `DeskCommand.idFrom` to osobna funkcja eksportowana z `DeskCommand.ts`. Metody pakietowe Javy (`BookingLedger`, fabryki `from`, `Booking.restore`) są publiczne z komentarzem, bo TypeScript nie ma widoczności pakietowej. Prywatny enum `State` z metodami na stałych to nieeksportowany interfejs `BookingState` z obiektem na każdy stan (funkcja `state(status, allowed)` daje domyślne przejścia rzucające `IllegalStateError`) i mapą `BY_STATUS` dla `restore`. Testy struktury zamiast refleksji czytają `Object.keys` modułu, tekst źródła (`implements DeskCommand`) i `Object.values` pól.

### Co widzimy

`BoxOfficeDesk` trzyma statusy i kwoty rezerwacji w mapach, kasę (`till`) i dziennik obiektów `Entry`. Ta sama operacja CANCEL znaczy co innego w zależności od statusu, a cofanie (UNDO) to drugi `switch` po nazwie operacji zapisanej w dzienniku. Wspólny kontrakt sceny to wyliczenie `Status` (NEW, PAID, USED, CANCELLED) w `Status.ts` - wartość zapisywana w bazie.

```typescript
switch (status) {
  case Status.NEW:
    switch (operation) {
      case 'PAY':
        this.till = this.till.plus(amount);
        moved = amount;
        next = Status.PAID;
        break;
      case 'CANCEL':
        next = Status.CANCELLED;
        break;
    }
    break;
  case Status.PAID:
    switch (operation) {
      case 'USE':
        next = Status.USED;
        break;
      case 'CANCEL':
        this.till = this.till.minus(amount);
        moved = amount;
        next = Status.CANCELLED;
        break;
    }
    break;
  ...
}
```

`S26EquivalenceTest` to sesje kasjera przechodzące przez wszystkie osie naraz: ceny typów N/S/E/C, cykl życia (CANCEL w NEW i w PAID), niedozwolone przejścia, cofanie w odwrotnej kolejności, cofnięcie USE i ponowny CANCEL, błędy, które nie trafiają do dziennika. Przed pierwszym ruchem pokaż na rzutniku wiersz `"BOOK B1 4DX N"`: najpierw walidacja formatu, potem typów biletów, dopiero na końcu "booking exists" - kolejność walidacji też jest kontraktem.

### Krok 1: Strategy dla ceny biletu

**W IDE:** utwórz `TicketPricing.ts` z interfejsem `TicketPricing` (metoda `price(formatBase: Money): Money`). Utwórz `TicketPricings.ts` z mapą kod typu - strategia (literały obiektów, wspólna funkcja `discounted(percent)`) i `forType(code)`, które dla nieznanego kodu rzuca `unknown ticket type: X` jak start. W pętli `book` zastąp wywołanie `priceOf` (wewnętrzny `switch`) wywołaniem strategii, a `priceOf` usuń (Find All References ⇧⌥F12 potwierdzi brak użyć).
**Po:**

```typescript
for (const type of args.slice(3)) {
  total = total.plus(TicketPricings.forType(type).price(base));
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s26` - zielone.
**Co powiedzieć:** cenę wybiera klient na podstawie danych wejściowych, bez historii i bez stanu - to Strategy. Strategia zna tylko cenę bazową formatu, nic o rezerwacji ani o operacji kasjera. Strategie są bezstanowe, więc `forType('S')` zwraca zawsze tę samą instancję.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s26 0 1`

### Krok 2: Command dla operacji kasjera

**W IDE:** VS Code nie ma Extract Delegate, więc ręcznie: pola `statuses`, `amounts`, `till` przenieś do nowej klasy-odbiorcy `BookingLedger` (plik `BookingLedger.ts`) z metodami `open`, `remove`, `status`, `pay`, `use`, `cancel`, `restore`, `deposit`, `withdraw`, `summary`. Utwórz interfejs `DeskCommand` (`execute(ledger)`, `undo(ledger)`, `journalEntry()`) z funkcją pomocniczą `idFrom(args, operation)` i po jednej klasie na operację: `BookCommand`, `PayCommand`, `UseCommand`, `CancelCommand`, każda ze statyczną fabryką `from(args)` (⌘. → Implement interface podpowie szkielet). Pulpit dostaje rejestr `ReadonlyMap<string, CommandFactory>` (`CommandFactory = (args) => DeskCommand`) i historię `DeskCommand[]` zamiast obiektów `Entry`; `undo()` woła `last.undo(this.ledger)`.
**Po:**

```typescript
const command = factory(args);
const result = command.execute(this.ledger);
this.history.push(command);
return result;
```

**Uruchom:** test zielony.
**Co powiedzieć:** wykonanie i cofnięcie są obok siebie w jednej klasie, a nie w dwóch `switch`. Polecenie pamięta tylko to, czego potrzebuje do cofnięcia (poprzedni status, przesuniętą kwotę) - to stan **tego** żądania, dlatego każde wywołanie tworzy nowy obiekt przez `from(args)`, a komenda nie jest współdzieloną stałą jak w s15. Do historii trafia dopiero polecenie wykonane bez wyjątku, jak w start. UNDO i REPORT zostały w `switch` pulpitu, bo nie są operacjami na rezerwacji. Cykl życia nadal siedzi w warunkach `BookingLedger` - to zapach na krok 3.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s26 1 2`

### Krok 3: State dla cyklu życia rezerwacji

**W IDE:** utwórz klasę `Booking` (kwota i pole `private state`) z nieeksportowanym interfejsem `BookingState` i funkcją `state(status, allowed)`, która dla nienadpisanych przejść rzuca `cannot <akcja> in <STATUS>`. Warunki z `BookingLedger.pay/use/cancel` przenieś do obiektów stanów: NEW podaje `pay` i `cancel`, PAID podaje `use` i `cancel`, USED i CANCELLED nie podają niczego. `BookingLedger` trzyma `Map<string, Booking>` i tylko przesuwa pieniądze w kasie.
**Po:**

```typescript
private static readonly NEW: BookingState = state(Status.NEW, {
  pay: (booking) => {
    booking.state = Booking.PAID;
    return booking.amount;
  },
  cancel: (booking) => {
    booking.state = Booking.CANCELLED;
    return Money.ZERO;
  },
});
...
private static readonly USED = state(Status.USED);
private static readonly CANCELLED = state(Status.CANCELLED);
```

**Uruchom:** test zielony; `S26SolutionTest` sprawdza, że cena to bezstanowa strategia, każda operacja to polecenie z `undo`, a stan jest prywatnym typem rezerwacji (`stateLivesInsideBookingAsPrivateType`).
**Co powiedzieć:** CANCEL w NEW i w PAID to ta sama operacja z innym zachowaniem - tego nie wyrazi ani Strategy (nikt z zewnątrz nie wybiera), ani Command (polecenie jest to samo). `axesAreNotMixed` pilnuje, że stan nie zna cennika ani nazw operacji, a polecenia nie sięgają do stanu. `restore` celowo omija reguły przejść: cofnięcie to kompensacja, nie przejście biznesowe (USED -> PAID nie jest dozwolone, ale UNDO po USE musi je wykonać).
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s26 2 3`

### Rozwiązanie i uzasadnienie

`step3`: pulpit zna tylko polecenia i ich historię, cena biletu to Strategy wybierana po typie, cykl życia to State w `Booking`. Trzy osie, trzy wzorce, zero mieszania. Publiczny `Status` zostaje kontraktem trwałym, a prywatny `BookingState` jest szczegółem implementacji mapowanym przez `BY_STATUS`.

### Pułapki

- Jeden wzorzec dla wszystkich osi "bo to wszystko `switch`" - np. Strategy dla statusu: klient musiałby wiedzieć, w jakim stanie jest rezerwacja, i sam podmieniać strategię.
- Polecenie jako współdzielony singleton z polami `previous` i `paid` - drugie PAY nadpisze dane potrzebne do cofnięcia pierwszego.
- Cofanie przez publiczne przejścia (`pay()`, `cancel()`) zamiast `restore` - cofnięcie USE wymaga przejścia, którego reguły zabraniają, a cofnięcie CANCEL po zwrocie musi wpłacić kwotę z powrotem do kasy (`deposit`).
- Kombinatoryczna hierarchia (`PaidCancelCommand`) zamiast złożenia osi.
- Zapis prywatnego stanu (np. indeksu obiektu stanu) do bazy zamiast stabilnego `Status`.

### Pytanie do sali

Kierownik chce zwrotu z potrąceniem 10% dla rezerwacji opłaconej, ale tylko w weekend. Która oś się zmienia i w której klasie to dopiszecie?

## Scena s27. Replace Inheritance with Delegation - raporty sprzedaży

**Temat ze slajdów:** 7.2. Gdy punkty zaczepienia obejmują dwie niezależne osie; 7.3. Zastąpienie dziedziczenia delegacją: osie jako strategie; 7.4. Strategia czy Metoda szablonowa?
**Katalog:** `typescript/src/workshop/m5/s27_templatetostrategy` · **Test:** `scripts/warsztat.sh --lang ts test m5/s27` (`S27EquivalenceTest.test.ts`, `S27SolutionTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `ScreeningReport` to Template Method z pięcioma hakami, ale haki niosą dwie niezależne osie: okres (`includes`, `label`) i format (`header`, `row`, `footer`). Każda kombinacja jest podklasą (`DailyCsvReport`, `DailyTextReport`, `WeeklyCsvReport`, `WeeklyTextReport`), a kod okresu i formatu jest między nimi skopiowany. Wyjmujemy oś formatu do strategii `ReportFormat`, potem oś okresu do `ReportPeriod`, a na końcu zwijamy cztery podklasy w jedną finalną klasę złożoną z dwóch strategii.

**Zasada:** Template Method jest dobry, gdy szkielet jest stabilny, a zmienia się **jeden** krok. Gdy haki grupują się w niezależne osie, dziedziczenie mnoży klasy (N × M), a kompozycja je dodaje (N + M). Replace Inheritance with Delegation robimy oś po osi: pogrupuj haki, wydziel interfejs dla grupy, delegat w konstruktorze, przeniesienie implementacji, usunięcie podklas po migracji klientów.

**Efekt:** Dwa okresy i dwa formaty to cztery klasy strategii, a trzeci format to jedna klasa zamiast dwóch podklas. Kolejność kroków raportu nadal pilnuje jedna metoda `render` - "finalność" przechodzi z metody na klasę. Wynik każdego raportu i komunikaty błędów `Reports.of` są bez zmian.

**Różnica względem Javy:** TypeScript nie ma modyfikatora `final`. W `start`, `step1` i `step2` metodę `render` chroni konstruktor bazy, który rzuca `IllegalStateError('render() is final')`, gdy podklasa ją nadpisuje (jak w s16). W `step3` "finalną klasę" daje sprawdzenie `new.target !== ScreeningReport` w konstruktorze. `TreeMap` to `Map` z kluczami sortowanymi przy wypisywaniu, `String.format` to `padEnd`/`padStart`, a `previousOrSame(MONDAY)` to `day.plusDays(-DAYS_OF_WEEK.indexOf(day.dayOfWeek))`. Testy struktury nie mają refleksji: usunięcie podklas `combinationSubclassesAreGone` sprawdza po plikach kroku (`existsSync`), a "finalność" - próbą utworzenia podklasy, która kończy się `IllegalStateError`.

### Co widzimy

`Reports.of(period, format)` (stabilny punkt wejścia) wybiera zagnieżdżonym `switch` jedną z czterech podklas - iloczyn osi zapisany w kodzie. Szkielet w bazie jest poprawny, ale haki mieszają dwie przyczyny zmiany:

```typescript
render(day: LocalDate, sales: readonly ScreeningSale[]): string {
  ...
    if (this.includes(day, sale.day)) {                 // hak okresu
  ...
  let text = this.header(this.label(day));              // format(okres)
  ...
    text += this.row(title, line.tickets, line.revenue);  // hak formatu
  ...
  return text + this.footer(total.tickets, total.revenue);
}
```

`S27EquivalenceTest` sprawdza każdą kombinację: dzień CSV (z escapowaniem średnika w tytule), dzień tekst, tydzień CSV ze środy (sumy po tytule), tydzień tekst z niedzieli, następny tydzień od poniedziałku, dzień bez sprzedaży, nieznany okres, nieznany format i przypadek "najpierw okres, potem format".

### Krok 1: oś formatu jako strategia

**W IDE:** utwórz interfejs `ReportFormat` z `header`, `row`, `footer` oraz klasy `CsvFormat` i `TextFormat` z ciałami skopiowanymi z `DailyCsvReport` i `DailyTextReport` (najpierw porównaj je z kopiami w `Weekly*`: w eksploratorze Select for Compare, potem Compare with Selected). Do `ScreeningReport` dodaj pole `format` i konstruktor `protected constructor(format: ReportFormat)`, w `render` wołaj `this.format.header(...)`, `this.format.row(...)`, `this.format.footer(...)`. Usuń trzy haki formatu z bazy i podklas (Safe Delete w VS Code nie ma, brak użyć potwierdzi `npm run typecheck`), podklasy wołają `super(new CsvFormat())` albo `super(new TextFormat())`.
**Po:**

```typescript
constructor() {
  super(new CsvFormat());
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s27` - zielone.
**Co powiedzieć:** najpierw grupujemy haki według osi, dopiero potem cokolwiek przenosimy. Hak wołamy teraz na delegacie, nie na `this` - strategia nie widzi stanu `protected` bazy i wszystko dostaje parametrami. `Reports` jest bez zmian, klienci niczego nie zauważyli.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s27 0 1`

### Krok 2: oś okresu jako strategia

**W IDE:** ten sam ruch dla `includes` i `label`: interfejs `ReportPeriod`, klasy `DailyPeriod` i `WeeklyPeriod` (powtórzone wyliczanie poniedziałku wydziel ⌃⇧R → Extract to method do `monday(day)`), konstruktor bazy `protected constructor(period: ReportPeriod, format: ReportFormat)`.
**Po:**

```typescript
export class WeeklyTextReport extends ScreeningReport {
  constructor() {
    super(new WeeklyPeriod(), new TextFormat());
  }
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** baza nie ma już żadnego haka, a podklasy są tylko konfiguracją konstruktora - kandydatami do Inline Class. `render` jest wciąż chroniony przed nadpisaniem (`beforeTheCollapseTheSkeletonWasStillAProtectedTemplateMethod`), więc kolejność kroków jest chroniona tak jak w Template Method.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s27 1 2`

### Krok 3: zwinięcie podklas w jedną finalną klasę

**W IDE:** w `ScreeningReport` usuń `abstract`, konstruktor zrób publiczny, a zamiast sprawdzania nadpisanego `render` odrzucaj każdą podklasę (`new.target !== ScreeningReport`). `Reports.of` składa `new ScreeningReport(period, format)` z dwóch map `PERIODS` i `FORMATS`, sprawdzając najpierw okres, potem format. Usuń pliki czterech podklas (przed usunięciem Find All References ⇧⌥F12, po usunięciu `npm run typecheck`).
**Po:**

```typescript
const chosenPeriod = Reports.PERIODS.get(period);
if (chosenPeriod === undefined) {
  throw new IllegalArgumentError(`unknown period: ${period}`);
}
const chosenFormat = Reports.FORMATS.get(format);
if (chosenFormat === undefined) {
  throw new IllegalArgumentError(`unknown format: ${format}`);
}
return new ScreeningReport(chosenPeriod, chosenFormat);
```

**Uruchom:** test zielony; `S27SolutionTest` → `newFormatWorksWithEveryPeriodWithoutNewClasses` dodaje format Markdown jedną klasą testową i od razu używa go z oboma okresami.
**Co powiedzieć:** N + M zamiast N × M; nowa kombinacja to nowe złożenie, nie nowa klasa. Przypadek "najpierw okres, potem format" (MONTHLY i PDF daje `unknown period`) pilnuje, że zamiana zagnieżdżonego `switch` na dwie mapy nie zmieniła kolejności walidacji.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s27 2 3`

### Rozwiązanie i uzasadnienie

`step3`: jedna finalna klasa `ScreeningReport` złożona z `ReportPeriod` i `ReportFormat`, cztery małe strategie i fabryka `Reports` wybierająca osie niezależnie. Template Method był dobry, dopóki zmieniała się jedna oś; przy dwóch niezależnych osiach kompozycja pozwala też wybrać kombinację w czasie działania.

### Pułapki

- Zwinięcie podklas przed wyjęciem obu osi - zostaje klasa z warunkiem `if (weekly)` zamiast strategii.
- Znika typ podklasy: kod z `instanceof WeeklyCsvReport`, rejestracja w kontenerze DI po klasie albo zapisana nazwa klasy (`constructor.name`) przestają działać - poszukaj takich użyć przed usunięciem plików.
- Osie, które tylko wyglądają na niezależne: gdyby format tekstowy inaczej opisywał tydzień niż dzień, format zależałby od okresu i podział 2 + 2 byłby fałszywy.
- Kopie implementacji w podklasach mogły się już rozjechać (np. escapowanie tylko w jednym CSV) - porównaj je, zanim zostawisz jedną.

### Pytanie do sali

Kiedy zostawilibyście Template Method mimo dwóch osi w hakach? Co musiałoby być prawdą o tempie zmian każdej z nich?

## Scena s28. Replace Conditional with Chain of Responsibility - zatwierdzanie zwrotów

**Temat ze slajdów:** 8.3. Łańcuch `if/else`, w którym kolejność jest regułą; 8.4. Łańcuch zobowiązań: ogniwo i złożenie łańcucha; 8.5. Łańcuch zobowiązań czy Dekorator?
**Katalog:** `typescript/src/workshop/m5/s28_chain` · **Test:** `scripts/warsztat.sh --lang ts test m5/s28` (`S28EquivalenceTest.test.ts`, `S28SolutionTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `RefundApprover.decide` rozstrzyga łańcuchem `if/else`, kto zatwierdza zwrot: po rozpoczęciu seansu odmowa, co najmniej 24 h przed seansem zwrot automatyczny, do 100.00 kasjer, do 1000.00 kierownik, wyżej brak uprawnionego. Wyjmujemy pierwszą gałąź jako ogniwo `RefundHandler`, potem wszystkie gałęzie jako ogniwa w tablicy, a na końcu budujemy jawny łańcuch z następnikiem w konstruktorze i ogniwem końcowym `NoApproverHandler`.

**Zasada:** Chain of Responsibility pasuje do uporządkowanych reguł, z których obsługuje jedna. Ogniwa nie są niezależne: zwrot automatyczny nie ma limitu kwoty tylko dlatego, że stoi przed kasjerem - kolejność jest regułą biznesową i ma być widoczna w jednym miejscu. Koniec łańcucha to decyzja, nie `undefined`. Kształt przypomina Decorator, ale intencja jest inna: dekorator zawsze woła wnętrze, ogniwo tylko wtedy, gdy samo nie obsłuży żądania.

**Efekt:** Kolejność poziomów widać w jednym wyrażeniu, a inną ścieżkę (np. festiwal bez zwrotów automatycznych) składa się bez edycji `decide`. Walidacja kwoty zostaje przed łańcuchem, bo to warunek wstępny, a nie poziom zatwierdzania. Decyzje dla wszystkich granic progów są identyczne jak w start.

**Różnica względem Javy:** `Optional<RefundDecision>` z kroków 1 i 2 to `RefundDecision | undefined` (`undefined` znaczy "pytaj dalej"). Dwa konstruktory `RefundApprover` z kroku 3 to jeden konstruktor z parametrem domyślnym `chain: RefundHandler = RefundApprover.standardChain()`. Test "ogniwo zna tylko następnika" (`everyLinkKnowsOnlyItsSuccessor`) zamiast refleksji po typach pól sprawdza, że dokładnie jedno pole ogniwa wskazuje następnika.

### Co widzimy

```typescript
if (!request.requestedAt.isBefore(request.screeningStart)) {
  return RefundDecision.rejected('seans juz sie rozpoczal');
} else if (Duration.between(request.requestedAt, request.screeningStart).toHours() >= 24) {
  return RefundDecision.approvedBy('AUTO');
} else if (!request.amount.isGreaterThan(RefundApprover.CASHIER_LIMIT)) {
  return RefundDecision.approvedBy('CASHIER');
} else if (!request.amount.isGreaterThan(RefundApprover.MANAGER_LIMIT)) {
  return RefundDecision.approvedBy('MANAGER');
}
return RefundDecision.rejected('brak uprawnionego zatwierdzajacego');
```

Zmiana kolejności, nowy poziom albo inna ścieżka dla festiwalu oznacza edycję tej jednej metody. `S28EquivalenceTest` to tabela na granicach progów: dokładnie 24 h przed (AUTO) i 23 h 59 min (kasjer), 100.00 i 100.01, 1000.00 i 1000.01, 5000.00 trzy dni przed seansem (AUTO bez limitu - kolejność reguł), chwila rozpoczęcia seansu, kwota zero i kwota ujemna po seansie (walidacja przed łańcuchem).

### Krok 1: Extract Interface i pierwsze ogniwo

**W IDE:** utwórz interfejs `RefundHandler` z metodą `handle(request: RefundRequest): RefundDecision | undefined` - `undefined` znaczy "pytaj dalej". Pierwszą gałąź wydziel (⌃⇧R → Extract to method) i przenieś ręcznie do klasy `AfterScreeningHandler` w osobnym pliku. W `decide` zostaje reszta `if/else`.
**Po:**

```typescript
const decision = this.afterScreening.handle(request);
if (decision !== undefined) {
  return decision;
} else if (Duration.between(request.requestedAt, request.screeningStart).toHours() >= 24) {
  return RefundDecision.approvedBy('AUTO');
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s28` - zielone.
**Co powiedzieć:** zaczynamy od pierwszej gałęzi, bo wszystkie pozostałe zakładają, że ona odmówiła. `RefundDecision | undefined` to przejściowy kontrakt "obsłużyłem albo nie" - jeszcze bez następnika.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s28 0 1`

### Krok 2: wszystkie gałęzie jako ogniwa w tablicy

**W IDE:** ten sam ruch dla `AutoRefundHandler`, `CashierHandler` i `ManagerHandler`; limity trafiają ręcznie do konstruktorów (VS Code nie ma Introduce Parameter). `decide` przegląda tablicę `handlers` po kolei i zwraca pierwszą decyzję, a po tablicy - odmowę "brak uprawnionego zatwierdzajacego".
**Po:**

```typescript
for (const handler of this.handlers) {
  const decision = handler.handle(request);
  if (decision !== undefined) {
    return decision;
  }
}
return RefundDecision.rejected('brak uprawnionego zatwierdzajacego');
```

**Uruchom:** test zielony.
**Co powiedzieć:** kolejność reguł ma nazwy i jest w jednym miejscu - to kolejność elementów tablicy. Lista bywa wystarczająca i prostsza niż klasyczny łańcuch; jeśli nikt nie musi składać innych ścieżek, można się tu zatrzymać.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s28 1 2`

### Krok 3: jawny łańcuch z następnikiem

**W IDE:** ręcznie (VS Code nie ma Change Signature): `RefundHandler.handle` zwraca `RefundDecision` zamiast `RefundDecision | undefined`, a `npm run typecheck` wskaże wszystkie ogniwa do poprawy. Każde ogniwo dostaje `next` w konstruktorze i woła `this.next.handle(request)`, gdy nie obsłuży żądania. Odmowę z końca tablicy przenieś do `NoApproverHandler`. `RefundApprover` dostaje konstruktor z parametrem domyślnym: bez argumentu składa standardową ścieżkę, z argumentem przyjmuje dowolną głowę łańcucha.
**Po:**

```typescript
constructor(chain: RefundHandler = RefundApprover.standardChain()) {
  this.chain = requireNonNull(chain, 'chain');
}

private static standardChain(): RefundHandler {
  return new AfterScreeningHandler(
    new AutoRefundHandler(
      new CashierHandler(RefundApprover.CASHIER_LIMIT,
        new ManagerHandler(RefundApprover.MANAGER_LIMIT,
          new NoApproverHandler()))));
}
```

**Uruchom:** test zielony; `S28SolutionTest` składa ścieżkę festiwalową bez zwrotów automatycznych (`chainIsAssembledExplicitlySoAFestivalCanSkipAutoRefunds`) i sprawdza, że ogniwo, które zdecydowało, nie woła następnika (`handlerThatDecidesStopsTheChainUnlikeADecorator`).
**Co powiedzieć:** koniec łańcucha jest ogniwem, więc żądanie nie może "spaść" bez odpowiedzi. Kontrast z Decoratorem: dekorator zawsze woła wnętrze i dokłada coś do wyniku, a tu odpowiedź pochodzi od dokładnie jednego ogniwa. Pytanie kontrolne: czy po obsłużeniu ktoś dalej musi pracować? Audyt decyzji byłby więc Decoratorem wokół `RefundApprover`, a nie kolejnym ogniwem.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s28 2 3`

### Rozwiązanie i uzasadnienie

`step3`: pięć ogniw, każde zna tylko swój warunek i następnika, kolejność złożona w jednej metodzie `standardChain`, walidacja kwoty przed łańcuchem. Nowy poziom (np. dyrektor do 5000.00) to nowe ogniwo i jedna linia w złożeniu.

### Pułapki

- Przestawienie ogniw "dla porządku" (kasjer przed AUTO) - 5000.00 trzy dni przed seansem przestaje być zwrotem automatycznym; wychwyci to przypadek "AUTO nie ma limitu kwoty".
- Ogniwa z mutowalnym `next` (setter `then(...)`) współdzielone między dwoma łańcuchami - drugie złożenie zmienia pierwsze. Tu `next` jest polem `readonly` ustawianym w konstruktorze.
- Walidacja kwoty jako pierwsze ogniwo - łańcuch podany z zewnątrz mógłby ją pominąć.
- Granica `toHours() >= 24` obcina minuty; przepisanie na porównanie `Duration` łatwo zmienia wynik dla 23 h 59 min.

### Pytanie do sali

Biznes chce logować każdą decyzję o zwrocie razem z tym, kto ją podjął. Ogniwo czy dekorator - i dlaczego?

## Scena s29. Introduce Boundary Object - odpowiedź terminala płatniczego

**Temat ze slajdów:** 9.3. Surowe dane z zewnątrz w kodzie domeny; 9.4. Obiekt graniczny: sprawdzone dane na granicy systemu; 9.5. Adapter czy obiekt graniczny?
**Katalog:** `typescript/src/workshop/m5/s29_boundaryobject` · **Test:** `scripts/warsztat.sh --lang ts test m5/s29` (`S29EquivalenceTest.test.ts`, `S29SolutionTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `PaymentService` dostaje od starego terminala tekst `OK;AUTH-R1;4500` (status, kod, kwota w groszach) i rozbiera go w każdej metodzie osobno: `split`, `startsWith`, `lastIndexOf`. Wprowadzamy niezmienny obiekt graniczny `PaymentConfirmation`, wydzielamy mapper `TerminalResponseMapper`, a na końcu chowamy klienta terminala za portem `PaymentTerminal` z adapterem `LegacyTerminalAdapter`.

**Zasada:** Boundary Object to zwalidowany obraz danych z zewnątrz w języku domeny: jeśli obiekt istnieje, jest poprawny, więc domena niczego nie parsuje ani nie zgaduje. Adapter tłumaczy wywołanie (jak zawołać obcy system), Boundary Object - dane (co wpuszczamy do domeny); zwykle występują razem. Walidacja na granicy zmienia typ i moment błędu dla danych błędnych - to decyzja do zatwierdzenia, nie skutek uboczny.

**Efekt:** Format terminala zna tylko mapper, a serwis zależy wyłącznie od portu i widzi `PaymentConfirmation`. Dla poprawnych odpowiedzi wynik jest identyczny. Świadomie zmienia się zachowanie dla odpowiedzi zniekształconych: są odrzucane na granicy przez `IllegalArgumentError('malformed terminal response: ...')`, zamiast wybuchać trzy metody dalej albo drukować pusty kod na paragonie.

**Różnica względem Javy:** rekord z zagnieżdżonym enumem to klasa `PaymentConfirmation` z polami `readonly` i walidacją w konstruktorze oraz osobne `export enum Outcome` w tym samym module. Grosze to `number`. W Javie `new BigDecimal("abc")` rzuca `NumberFormatException` (podklasę `IllegalArgumentException`); `Decimal` rzuca własny `DecimalError`, więc mapper sprawdza kwotę wyrażeniem regularnym `/^[+-]?\d+$/` i sam rzuca `IllegalArgumentError` - przy okazji odrzuca też kwoty ułamkowe. Test `domainServiceSeesNeitherTheLegacyClientNorTheMapper` zamiast refleksji po sygnaturach sprawdza tekst źródła `step3/PaymentService.ts` (bez komentarzy).

### Co widzimy

`LegacyTerminalClient` (nie zmieniamy go) przyjmuje grosze i odpowiada tekstem: kwota <= 0 daje `ERROR;AMOUNT;0`, powyżej 1000.00 `DECLINED;51;0`, powyżej 300.00 autoryzację częściową do 300.00. Serwis trzyma surowe odpowiedzi w mapie i każda metoda sama je rozbiera:

```typescript
const raw = this.terminal.authorize(bookingId, grosze);
this.responses.set(bookingId, raw);
const parts = raw.split(';');
if (parts[0] !== 'OK') {
  return `${parts[0]} ${parts[1]}`;
}
const authorized = Number.parseInt(parts[2] ?? '', 10);
if (authorized < grosze) {
  return `PARTIAL ${parts[1]} brakuje ${new Decimal(grosze - authorized).dividedBy(100).toFixed(2)}`;
}
```

Punkt wejścia testu to korzeń kompozycji `PaymentModule.paymentService(terminal)`. `S29EquivalenceTest` gra sesje płatności: pełna autoryzacja, częściowa, granica 300.00 i grosz ponad, odmowa i błąd kwoty, brak płatności, ponowna płatność nadpisująca poprzednią - za każdym razem z potwierdzeniem i sumą autoryzacji.

### Krok 1: Introduce Boundary Object

**W IDE:** utwórz klasę `PaymentConfirmation` (pola `readonly outcome: Outcome`, `code: string`, `authorized: Money`) z wyliczeniem `Outcome { APPROVED, DECLINED, ERROR }` i walidacją w konstruktorze: kod niepusty, kwota nieujemna, tylko APPROVED może autoryzować pieniądze. W `pay` rozbierz odpowiedź raz, zaraz po wywołaniu terminala (⌃⇧R → Extract to method, metoda statyczna `parse(raw)`); mapa przechowuje `PaymentConfirmation`, a `receipt` i `totalAuthorized` czytają pola obiektu.
**Po:**

```typescript
const confirmation = PaymentService.parse(this.terminal.authorize(bookingId, grosze));
this.confirmations.set(bookingId, confirmation);
if (!confirmation.approved()) {
  return `${confirmation.outcome} ${confirmation.code}`;
}
if (confirmation.isPartialFor(amount)) {
  return `PARTIAL ${confirmation.code} brakuje ${amount.minus(confirmation.authorized).toString()}`;
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s29` - zielone.
**Co powiedzieć:** zamiast trzech parserów jest jeden, w miejscu wejścia danych. `totalAuthorized` nie sprawdza już `startsWith('OK')` i po prostu sumuje - niezmiennik obiektu gwarantuje zero dla odmowy. `parse` sprawdza liczbę pól, znany status i kwotę: to pierwszy moment, w którym zmienia się zachowanie dla danych błędnych (start przyjąłby `OK;;4500` i wydrukował pusty kod).
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s29 0 1`

### Krok 2: mapper na granicy

**W IDE:** przenieś ręcznie `parse` do nowej klasy `TerminalResponseMapper` jako `toConfirmation(raw)` (VS Code nie przenosi metod między klasami); przeliczenie złotych na grosze też tam (`toGrosze(amount)`). Serwis tworzy mapper i woła go po obu stronach wywołania terminala.
**Po:**

```typescript
const raw = this.terminal.authorize(bookingId, this.mapper.toGrosze(amount));
const confirmation = this.mapper.toConfirmation(raw);
```

**Uruchom:** test zielony; `S29SolutionTest` → `malformedResponseIsRejectedAtTheBoundary` sprawdza `OK;AUTH-R1`, `MAYBE;X;0`, `OK;;4500` i `OK;AUTH-R1;abc`.
**Co powiedzieć:** mapper to jedyne miejsce, które zna format `STATUS;KOD;KWOTA` i grosze w obie strony. Serwis nie zna już formatu, ale wciąż sam woła `LegacyTerminalClient` i składa wywołanie z mapperem.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s29 1 2`

### Krok 3: port domeny i adapter

**W IDE:** utwórz interfejs `PaymentTerminal` z `authorize(bookingId: string, amount: Money): PaymentConfirmation` i klasę `LegacyTerminalAdapter implements PaymentTerminal`, która łączy klienta z mapperem. Ręcznie zmień typ parametru konstruktora `PaymentService` z `LegacyTerminalClient` na `PaymentTerminal` (`npm run typecheck` wskaże miejsca wywołań). W korzeniu kompozycji owiń klienta adapterem.
**Po:**

```typescript
static paymentService(terminal: LegacyTerminalClient): PaymentService {
  return new PaymentService(new LegacyTerminalAdapter(terminal));
}
```

**Uruchom:** test zielony; `serviceWorksWithAnyTerminalBehindThePort` używa literału obiektu zamiast terminala, a `domainServiceSeesNeitherTheLegacyClientNorTheMapper` sprawdza, że kod serwisu nie wymienia klienta terminala ani mappera.
**Co powiedzieć:** granica jest kompletna: Adapter (kształt wywołania), mapper (format danych), Boundary Object (poprawna wartość w języku domeny). Serwis da się testować bez terminala i bez tekstowych odpowiedzi.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s29 2 3`

### Rozwiązanie i uzasadnienie

`step3`: `PaymentService` zna tylko `PaymentTerminal` i `PaymentConfirmation`, `LegacyTerminalAdapter` implementuje port, `TerminalResponseMapper` jako jedyny zna grosze i tekst odpowiedzi. Zmiana zachowania dla odpowiedzi zniekształconych jest nazwana i opisana testem.

### Pułapki

- Obiekt graniczny bez walidacji (zwykłe DTO albo literał obiektu zgodny z typem) - domena znowu musi sprawdzać i zgadywać.
- Zmiana typu i momentu błędu bez zapisania decyzji: w start `OK;AUTH-R1` daje w porcie `NaN` z `Number.parseInt`, porównanie `NaN < grosze` jest fałszywe, więc `pay` zgłasza `PAID AUTH-R1`, a błąd (`DecimalError`) wybucha dopiero w `receipt` albo `totalAuthorized`; `OK;;4500` daje paragon z pustym kodem.
- Adapter, który przepuszcza surowy tekst dalej "dla elastyczności" - port ma mówić językiem domeny.
- Kopiowanie każdego DTO obcego systemu bez potrzeby - osobny model ma sens, gdy granica oddziela różne semantyki lub tempo zmian.
- Przeliczanie na grosze: w Javie `longValueExact` rzuca dla ułamka grosza, a `toNumber()` w porcie po cichu zwróci ułamek. Jeśli to ma być część kontraktu, trzeba ją sprawdzić jawnie (`Number.isInteger`), a nie "upraszczać".

### Pytanie do sali

Terminal zaczyna zwracać nowy status `PENDING`. W którym miejscu kodu to zobaczycie i kto powinien zdecydować, co z nim zrobić?

## Scena s30. Extract Facade - zakup biletów w sklepie i w kasie

**Temat ze slajdów:** 11.1. Klienci sami koordynują podsystemy; 11.2. Wyodrębnienie Fasady: jedna operacja zamiast znajomości kroków
**Katalog:** `typescript/src/workshop/m5/s30_facade` · **Test:** `scripts/warsztat.sh --lang ts test m5/s30` (`S30EquivalenceTest.test.ts`, `S30BehaviourChangeTest.test.ts`, `S30SolutionTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `OnlineShop.buy` i `BoxOffice.sell` same orkiestrują cztery podsystemy: cennik, rejestr miejsc, bramkę płatności i wysyłkę maili. Kod kasy to kopia sklepu sprzed lat i ma celowo zostawiony błąd legacy: po odmowie płatności nie zwalnia miejsc. Wyjmujemy orkiestrację ze sklepu do `BookingFacade`, przepinamy na nią kasę (to świadomie naprawia błąd), a na końcu ukrywamy podsystemy i domykamy niezmiennik blokiem `finally`.

**Zasada:** Facade daje klientom jedną spójną operację zamiast znajomości kroków i ich kolejności. Procedura: test efektów obu klientów, Extract Method orkiestracji, przeniesienie metody do fasady, przepięcie drugiego klienta, zawężenie widoczności podsystemów. Gdy kopie orkiestracji już się rozjechały, przepięcie klienta zmienia jego zachowanie - to naprawa błędu, którą dokumentuje się osobnym testem, a nie "czysta" refaktoryzacja.

**Efekt:** Kanały sprzedaży znają tylko fasadę i własną opłatę (sklep 2.00 za miejsce, kasa zero), a kolejność cena - rezerwacja - płatność z kompensacją - mail jest w jednym miejscu. Zachowanie świadomie zmienia się dwa razy: w kroku 2 kasa zaczyna zwalniać miejsca po odmowie płatności, w kroku 3 miejsca są zwalniane także wtedy, gdy bramka rzuci wyjątkiem.

**Różnica względem Javy:** TypeScript nie ma widoczności pakietowej, więc w `step3` podsystemy (`SeatRegistry`, `PriceList`, `PaymentClient`, `TicketMailer`) przenosimy z osobnych plików do modułu `BookingFacade.ts` jako klasy bez `export`. Konstruktor fasady jest prywatny, a fasadę składa statyczne `BookingFacade.create()`, wołane przez korzeń kompozycji `Cinema`. `Cinema.state()` w `step3` deleguje do `BookingFacade.state()`, bo `Cinema` nie widzi już podsystemów - stąd większy diff kroku 2 do 3. Pola kanałów w `Cinema` nazywają się `shop` i `desk` (metody `onlineShop()` i `boxOffice()` zostają). Test `startBoxOfficeLeaksSeatsAfterDeclinedPayment` po `jump` pomija się przez `context.skip(...)`, a `subsystemIsHiddenBehindTheFacade` zamiast refleksji sprawdza pliki kroku, eksporty modułu i tekst źródła.

### Co widzimy

Dwie metody wyglądają prawie tak samo. Pokaż je obok siebie na rzutniku i poproś salę o znalezienie różnic, zanim cokolwiek wydzielisz:

```typescript
// BoxOffice.sell - kopia OnlineShop.buy bez opłaty rezerwacyjnej
const total = this.prices.price(order.ticketType).times(order.seats.length);
try {
  this.seats.reserve(order.screening, order.seats);
} catch (exception) {
  if (exception instanceof IllegalStateError) {
    return `ERROR ${exception.message}`;
  }
  throw exception;
}
if (!this.payments.charge(order.card, total)) {
  return 'ERROR payment declined';     // sklep w tym miejscu woła seats.release(...)
}
```

`Cinema` to korzeń kompozycji i stabilny punkt wejścia: składa podsystemy i oba kanały, a `state()` pokazuje miejsca, dziennik płatności i wysłane maile naraz. `S30EquivalenceTest` gra sesje, w których zachowanie ma zostać bez zmian (zakup w sklepie z opłatą, w kasie bez opłaty, miejsce zajęte przez drugi kanał, odmowa w sklepie, nieznany typ biletu przed rezerwacją, różne seanse). Odmowa w kasie celowo jest w osobnym `S30BehaviourChangeTest`.

### Krok 1: Extract Facade ze sklepu

**W IDE:** w `OnlineShop.buy` zaznacz całą orkiestrację, ⌃⇧R → Extract to method `book`, a opłatę zamień ręcznie na parametr: `book(order, feePerSeat)`. Metodę przenieś ręcznie (wytnij i wklej) do nowej klasy `BookingFacade` z czterema podsystemami w konstruktorze. Sklep dostaje fasadę, a `Cinema` ją tworzy.
**Po:**

```typescript
buy(order: TicketOrder): string {
  return this.booking.book(order, OnlineShop.BOOKING_FEE);
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s30` - zielone; `step1StillLeaksBecauseOnlyTheShopUsesTheFacade` pokazuje, że błąd kasy wciąż żyje.
**Co powiedzieć:** zaczynamy od klienta, którego orkiestracja jest poprawna (kompensuje odmowę płatności). To, czym różnią się kanały, przychodzi parametrem, a nie flagą `if (online)`.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s30 0 1`

### Krok 2: kasa na fasadzie - świadoma zmiana zachowania

**W IDE:** ręcznie (VS Code nie ma Change Signature) zamień cztery parametry konstruktora `BoxOffice` na jeden `booking: BookingFacade`. `sell` woła `this.booking.book(order, Money.ZERO)`, skopiowaną orkiestrację usuń. Popraw `Cinema` (wskaże ją `npm run typecheck`).
**Po:**

```typescript
sell(order: TicketOrder): string {
  return this.booking.book(order, Money.ZERO);
}
```

**Uruchom:** test równoważności zielony, a `S30BehaviourChangeTest` → `step2FacadeReleasesSeatsForBothChannels` pokazuje zmianę: po odmowie w kasie miejsce A1 jest wolne i sklep może je sprzedać (`OK 27.00` zamiast `ERROR seat taken A1`).
**Co powiedzieć:** usunięcie kopii usuwa także jej błąd, ale to zmiana zachowania kasy. Nazywamy ją, opisujemy testem i zatwierdzamy osobno (osobny commit "kasa zwalnia miejsca po odmowie płatności"). Test `startBoxOfficeLeaksSeatsAfterDeclinedPayment` po `jump` do kroku 2 lub 3 jest pomijany (vitest pokazuje go jako "skipped") - start kasy nie ma już własnej orkiestracji.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s30 1 2`

### Krok 3: ukryte podsystemy i niezmiennik w finally

**W IDE:** przenieś klasy `SeatRegistry`, `PriceList`, `PaymentClient` i `TicketMailer` do `BookingFacade.ts` bez `export` i usuń ich pliki. Konstruktor fasady zrób `private`, dodaj `static create()`, które składa podsystemy, a `Cinema` niech woła `BookingFacade.create()` i deleguje `state()` do fasady. W `book` otocz `charge` blokiem `try/finally`, który zwalnia miejsca, gdy płatność się nie udała.
**Po:**

```typescript
let paid = false;
try {
  paid = this.payments.charge(order.card, total);
} finally {
  if (!paid) {
    this.seats.release(order.screening, order.seats);
  }
}
```

**Uruchom:** test zielony; `S30SolutionTest` sprawdza, że podsystemy są niewidoczne poza modułem fasady (`subsystemIsHiddenBehindTheFacade`), a kanały zależą tylko od fasady (`salesChannelsKnowOnlyTheFacade`).
**Co powiedzieć:** zawężenie widoczności to zmiana API dla klientów spoza fasady - robimy je po migracji, jako osobny krok. `finally` to druga zmiana zachowania: karta `TIMEOUT` (wyjątek bramki) w kroku 2 zostawiała miejsce zajęte (`beforeStep3AGatewayFailureLeakedSeats`), teraz nie (`gatewayFailureNoLongerLeavesSeatsTaken`). Fasada pilnuje niezmiennika "miejsce zajęte tylko po udanej płatności", ale nie jest transakcją: błąd wysyłki maila po płatności nadal niczego nie cofa.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s30 2 3`

### Rozwiązanie i uzasadnienie

`step3`: `BookingFacade` to jedyne wejście do podsystemu i strażnik spójności, podsystemy są prywatne dla modułu fasady, a złożenie zostaje w jednym miejscu (`BookingFacade.create()` wołane przez `Cinema`). Obie zmiany zachowania (zwolnienie miejsc po odmowie w kasie i po awarii bramki) są nazwane i mają własne testy.

### Pułapki

- Przepięcie drugiego klienta jako "czysta refaktoryzacja" bez testu zmiany - ktoś mógł polegać na starym zachowaniu (np. raport zajętości kasy).
- Fasada jako kopia całego API podsystemów (God Class) zamiast jednej spójnej operacji.
- Parametr `online: boolean` zamiast opłaty - fasada zaczyna znać kanały sprzedaży.
- Ukrycie podsystemów przed migracją wszystkich klientów, także testów i konfiguracji frameworka.
- Fasada przekazująca 1:1 bez żadnej reguły to Middle Man (scena s22).

### Pytanie do sali

Kasa chce sprzedawać bilety bez maila, gdy klient nie poda adresu. Parametr fasady, druga operacja fasady czy osobny klient podsystemów?

## Scena s31. Anti-Corruption Layer - licencje dystrybutora

**Temat ze slajdów:** 11.3. Obcy model w regułach domeny; 11.4. Warstwa antykorupcyjna: port, tłumacz i model domenowy; 11.5. Fasada a warstwa antykorupcyjna
**Katalog:** `typescript/src/workshop/m5/s31_anticorruptionlayer` · **Test:** `scripts/warsztat.sh --lang ts test m5/s31` (`S31EquivalenceTest.test.ts`, `S31SolutionTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `SettlementService` rozlicza się z dystrybutorem i decyduje, czy wolno grać film, myśląc w modelu dystrybutora: status `A`/`S`, daty `yyyyMMdd`, minimalna gwarancja w groszach jako tekst, udział `"45%"` albo `" 40 %"`, tytuł wielkimi literami. Wprowadzamy klasę domenową `FilmLicense`, wydzielamy tłumacza `DistributorTranslator`, a na końcu port `FilmLicenses` z implementacją `DistributorAcl`.

**Zasada:** Anti-Corruption Layer chroni język domeny przed obcym modelem: port mówi językiem kina, a tłumacz jako jedyny zna cudze konwencje. Fasada upraszcza dostęp do podsystemu, ale mówi jego językiem; ACL tłumaczy jeden model na drugi i często składa się z fasady, adaptera i tłumacza naraz. Tłumaczenie zachłanne zmienia zachowanie dla danych błędnych - polityka wobec złego rekordu to decyzja biznesowa.

**Efekt:** Reguły (czy licencja obejmuje dzień, ile się należy) są metodami `FilmLicense` i testuje się je bez typów dystrybutora, a `null` z obcego systemu nie wychodzi poza ACL. Dla poprawnych rekordów rozliczenia są identyczne. Walidacja jest świadomie zaostrzona: zła data to `IllegalArgumentError('invalid legacy date: ...')`, a niespójna licencja (koniec przed początkiem, udział spoza 0-100) w ogóle nie powstanie.

**Różnica względem Javy:** rekord `FilmLicense` to klasa z polami `readonly` i niezmiennikami w konstruktorze. `Optional.empty()` to `undefined`, a `null` zwracany przez obce `DistributorFeed.fetch` zostaje `null` - zamienia go dopiero ACL. Zamiast `LocalDate.parse(..., BASIC_ISO_DATE)` tłumacz ma lokalną funkcję `parseBasicIsoDate` (wyrażenie regularne `^\d{8}$` i sprawdzenie poprawności daty, błąd `RangeError`), którą zamienia na `IllegalArgumentError('invalid legacy date: ...')` z `cause`. `movePointLeft(2)` to `dividedBy(100)`, `replace` to `replaceAll`. Udział to `number`, a nie `int`, więc `FilmLicense` dodatkowo sprawdza `Number.isInteger(sharePercent)`; `Number.parseInt` jest przy tym luźniejszy niż `Integer.parseInt` (z `"45.5"` zrobi 45 zamiast rzucić wyjątkiem). W `step3` tłumacz to klasa bez `export` w module `DistributorAcl.ts` (odpowiednik klasy pakietowej) - pliku `DistributorTranslator.ts` w tym kroku nie ma.

### Co widzimy

```typescript
if (record.statusCode !== 'A') {
  return `LICENCJA ZAWIESZONA ${filmCode}`;
}
if (!this.canScreen(filmCode, day)) {
  return `LICENCJA NIEWAZNA ${filmCode} w dniu ${day.toString()}`;
}
const percent = Number.parseInt(record.share.replaceAll('%', '').trim(), 10);
const minimum = new Money(new Decimal(record.minGuaranteeCents).dividedBy(100));
const due = revenue.percent(percent).max(minimum);
const title = record.titleUpper.charAt(0) + record.titleUpper.substring(1).toLowerCase();
```

Reguła "wolno grać, gdy licencja jest aktywna i obejmuje dzień" jest zapisana jako "kod `A` i dwie daty sparsowane z `yyyyMMdd`". Każda zmiana formatu u dystrybutora rozlewa się po regułach kina. Punkt wejścia to `SettlementModule.settlementService(feed)`; `DistributorFeed` i `DistributorRecord` należą do obcego systemu i ich nie zmieniamy. `S31EquivalenceTest` sprawdza udział powyżej minimum, minimalną gwarancję, udział ze spacjami i zaokrąglenie, oba końce okresu licencji (włącznie) i dni tuż poza nimi, licencję zawieszoną i nieznany film - dla `settle` i `canScreen`.

### Krok 1: model domeny i tłumaczenie w jednym miejscu

**W IDE:** utwórz klasę `FilmLicense` (pola `readonly filmCode`, `title`, `active`, `validFrom`, `validTo`, `sharePercent`, `minimumGuarantee`) z niezmiennikami w konstruktorze i metodami `covers(day)`, `allowsScreeningOn(day)`, `dueFor(revenue)`. W serwisie zaznacz dekodowanie pól rekordu, ⌃⇧R → Extract to method, metoda statyczna `toLicense(record)`, a reguły licz na licencji.
**Po:**

```typescript
const license = SettlementService.toLicense(record);
if (!license.active) {
  return `LICENCJA ZAWIESZONA ${filmCode}`;
}
if (!license.covers(day)) {
  return `LICENCJA NIEWAZNA ${filmCode} w dniu ${day.toString()}`;
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s31` - zielone.
**Co powiedzieć:** reguły brzmią teraz jak reguły kina, a nie jak `record.statusCode !== 'A'`. Zauważ, że tłumaczenie jest zachłanne: rekord zawieszony z zepsutą datą dawał w start po prostu "LICENCJA ZAWIESZONA", a teraz parsowanie dat wybuchnie wcześniej. Dla poprawnych danych z testu wynik jest ten sam - to przesunięcie trzeba nazwać.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s31 0 1`

### Krok 2: tłumacz jako osobna klasa

**W IDE:** przenieś ręcznie `toLicense` do nowej klasy `DistributorTranslator` (plik `DistributorTranslator.ts`). Wydziel `title(...)` i `date(...)` (⌃⇧R → Extract to method), kod statusu jako stałą `ACTIVE` (⌃⇧R → Extract to constant), a w `date` zamień błąd parsera daty na `IllegalArgumentError` z komunikatem granicy.
**Po:**

```typescript
private static date(legacy: string): LocalDate {
  try {
    return parseBasicIsoDate(legacy);
  } catch (exception) {
    throw new IllegalArgumentError(`invalid legacy date: ${legacy}`, { cause: exception });
  }
}
```

**Uruchom:** test zielony; `S31SolutionTest` → `aclRejectsMalformedLegacyDataAtTheBoundary` dokumentuje nowy komunikat dla daty `2026-10-01` w formacie ISO zamiast `yyyyMMdd`.
**Co powiedzieć:** zepsuty rekord zgłasza się w języku granicy, z komunikatem, który mówi, co jest nie tak. To zmiana typu wyjątku i trzeba ją zatwierdzić. Serwis nadal jednak sam pobiera `DistributorRecord` i obsługuje jego `null`.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s31 1 2`

### Krok 3: port domenowy i warstwa antykorupcyjna

**W IDE:** utwórz interfejs `FilmLicenses` z `find(filmCode: string): FilmLicense | undefined` i klasę `DistributorAcl implements FilmLicenses`, która łączy klienta dystrybutora z tłumaczem. Ręcznie zmień typ parametru konstruktora serwisu z `DistributorFeed` na `FilmLicenses` (`npm run typecheck` wskaże miejsca wywołań). Tłumacza przenieś do `DistributorAcl.ts` jako klasę bez `export` i usuń jego plik, a `SettlementModule` owija klienta w ACL.
**Po:**

```typescript
find(filmCode: string): FilmLicense | undefined {
  const record = this.feed.fetch(filmCode);
  return record === null ? undefined : this.translator.toLicense(record);
}
```

**Uruchom:** test zielony; `domainServiceUsesNoLegacyType` sprawdza importy i tekst źródła serwisu (zamiast refleksji po sygnaturach), `translatorIsAnImplementationDetailOfTheAcl` - że tłumacz nie jest eksportowany, a `serviceWorksWithAnyLicenseSource` rozlicza licencję festiwalową podaną literałem obiektu, bez żadnego rekordu dystrybutora.
**Co powiedzieć:** żaden typ dystrybutora nie przekracza ACL, a "brak licencji" to `undefined`, nie `null` z obcego systemu. Fasada by tu nie wystarczyła - uprościłaby wywołania, ale domena dalej mówiłaby kodami `A`/`S`. ACL to też naturalny punkt dla Strangler Fig: za portem może stać stary i nowy dostawca licencji.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s31 2 3`

### Rozwiązanie i uzasadnienie

`step3`: `SettlementService` zależy tylko od `FilmLicenses` i `FilmLicense`, `DistributorAcl` jest jedyną klasą, która widzi `DistributorFeed` i `DistributorRecord`, a nieeksportowany `DistributorTranslator` zna wszystkie konwencje dystrybutora. Zaostrzona walidacja jest opisana testami.

### Pułapki

- ACL bez różnicy modeli to drogi Middle Man - tu różnica jest realna (kody, grosze, daty i procenty jako tekst).
- Tłumaczenie zachłanne bez decyzji: zły rekord zawieszonej licencji zatrzymuje teraz rozliczenie. Odrzucić, pominąć z logiem czy przenieść do kwarantanny - to decyzja biznesowa.
- Przecieki modelu: `DistributorRecord` zwracany "tylko w jednym miejscu", `null` zamiast `undefined` w porcie, kody `A`/`S` w wyliczeniu domeny.
- Normalizacja tytułu (`KRAINA LODU` daje `Kraina lodu`) też jest kontraktem (paragon, raport) - nie poprawiaj jej "przy okazji" na `Kraina Lodu`.

### Pytanie do sali

Dystrybutor dodaje status `T` (licencja tylko na pokazy prasowe). Gdzie pojawi się to pojęcie i jak nazwiecie je po stronie kina?

## Scena s32. Introduce Mediator - ekran wyboru miejsc

**Temat ze slajdów:** 14.1. Komponenty połączone każdy z każdym; 14.2. Wprowadzenie Mediatora: reguły współpracy w jednym miejscu; 14.3. Obserwator czy Mediator?
**Katalog:** `typescript/src/workshop/m5/s32_mediator` · **Test:** `scripts/warsztat.sh --lang ts test m5/s32` (`S32EquivalenceTest.test.ts`, `S32SolutionTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** Ekran wyboru miejsc ma cztery komponenty: `SeatMap`, `Basket`, `PriceSummary` i `PayButton`, połączone "każdy z każdym" (11 referencji). Każdy po akcji użytkownika sam odświeża pozostałe, a kolejność odświeżania (suma, potem przycisk) jest powtórzona w czterech miejscach. Wprowadzamy `SelectionMediator`: najpierw dla kliknięcia miejsca, potem dla wszystkich zdarzeń ze wspólnym `refresh()`, a na końcu przycisk zgłasza tylko chęć płatności i komponenty znają wyłącznie mediatora.

**Zasada:** Mediator skupia reguły współpracy komponentów w jednym miejscu: komponent zgłasza, co się stało, a mediator decyduje, kto i w jakiej kolejności reaguje. Od Observera różni się miejscem reguł: obserwator rozgłasza zdarzenie nieznanym odbiorcom, mediator zna wszystkich uczestników i świadomie koordynuje ich reakcję. Ryzyko to God Mediator - w mediatorze zostaje koordynacja, a logika komponentów (cena, limit miejsc) zostaje w komponentach.

**Efekt:** Cztery referencje do interfejsu mediatora zamiast 11 między komponentami, a nowy komponent (np. licznik czasu rezerwacji) to zmiana w mediatorze, nie w czterech klasach. Migawka ekranu po każdej akcji jest identyczna jak w start, także dla akcji ignorowanych.

**Różnica względem Javy:** akcesory `seatMap()`, `basket()`, `priceSummary()`, `payButton()` to pola `readonly` ekranu (`screen.seatMap.click(...)`). Metody pakietowe Javy (np. `basket.drop`, `seatMap.deselect`) są publiczne. Pola wiązane przez `connect(...)` mają asercję `!:` (`private basket!: Basket;`) - kompilator sam wskazuje zapach "każdy zna każdego". Pola z listą miejsc i kwotą nazywają się `items` i `amount`, bo nazwy z Javy kolidują z metodami `seats()` i `total()`. `TreeSet` to `Set` sortowany przy wypisywaniu, a `List.remove(Object)` to `indexOf` i `splice`. Test `everyComponentKnowsOnlyTheMediator` zamiast refleksji po typach pól sprawdza, że jedynym importem z katalogu kroku jest `SelectionMediator`, a jedynym współpracownikiem w `Object.values(...)` komponentu jest przekazany mediator.

### Co widzimy

`SeatSelectionScreen` (stabilny punkt wejścia) tylko tworzy komponenty i łączy je metodami `connect(...)`. Sala ma miejsca A1-C4, B2 jest sprzedane wcześniej, rząd C to VIP (35.00, pozostałe 25.00), voucher KINO10 daje -10%, przycisk jest aktywny dla 1-4 miejsc.

```typescript
// SeatMap.click - mapa zna trzy inne komponenty i kolejność ich odświeżania
if (this.selected.delete(seat)) {
  this.basket.drop(seat);
} else {
  this.selected.add(seat);
  this.basket.add(seat);
}
this.summary.recalculate(this.basket.seats());
this.payButton.refresh(this.basket.seats().length, this.summary.total());
```

Ta sama para "przelicz sumę, odśwież przycisk" jest w `Basket.remove`, `Basket.clear` i `PriceSummary.applyVoucher`, a `PayButton.press` sam pyta koszyk i sumę, oznacza miejsca jako sprzedane i czyści koszyk, co uruchamia kaskadę odświeżeń. `S32EquivalenceTest` porównuje migawkę `render()` po sekwencjach akcji: ekran startowy, kolejność kliknięć, ponowne kliknięcie, miejsca sprzedane i spoza sali, usuwanie z koszyka, limit 4 miejsc, voucher, płatność, czyszczenie koszyka.

### Krok 1: interfejs mediatora i pierwsze zdarzenie

**W IDE:** utwórz interfejs `SelectionMediator` z metodą `seatToggled(seat: string, selected: boolean): void`. `SeatSelectionScreen implements SelectionMediator` (jak "dialog" w GoF). Reakcję z `SeatMap.click` wydziel (⌃⇧R → Extract to method) i przenieś ręcznie do `seatToggled` w ekranie. `SeatMap` dostaje mediatora w konstruktorze, jej `connect(...)` znika.
**Po:**

```typescript
const nowSelected = !this.selected.delete(seat);
if (nowSelected) {
  this.selected.add(seat);
}
this.mediator.seatToggled(seat, nowSelected);
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s32` - zielone.
**Co powiedzieć:** komponent mówi "co się stało", a nie "kto ma zareagować". `new SeatMap(this)` w inicjalizatorze pola przekazuje `this` przed końcem konstrukcji ekranu - bezpieczne tylko dlatego, że mediator działa dopiero po akcji użytkownika. Pozostałe komponenty są nadal połączone wprost: jeden komponent naraz.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s32 0 1`

### Krok 2: wszystkie zdarzenia i jedno refresh()

**W IDE:** dodaj do interfejsu `seatRemoved`, `basketCleared`, `voucherApplied` i `seatsPaid(seats: readonly string[])`. `Basket`, `PriceSummary` i `PayButton` dostają mediatora w konstruktorze i zgłaszają zdarzenia zamiast odświeżać sąsiadów; przycisk zachowuje jeszcze `connect(basket, summary)`. Powtórzoną parę odświeżeń wydziel w ekranie do `refresh()` (⌃⇧R → Extract to method), a pozostałe kopie zastąp wywołaniem ręcznie - VS Code nie proponuje zastąpienia duplikatów.
**Po:**

```typescript
private refresh(): void {
  this.priceSummary.recalculate(this.basket.seats());
  this.payButton.refresh(this.basket.seats().length, this.priceSummary.total());
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** kolejność odświeżania jest zachowaniem - przycisk pokazuje kwotę, więc suma musi być przeliczona wcześniej. Teraz jest zapisana raz zamiast czterech razy. Uwaga na reentrancję: `seatsPaid` woła `basket.clear()`, które znów woła mediatora (`basketCleared`) - kaskada działa, ale jest ukryta.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s32 1 2`

### Krok 3: komponenty znają tylko mediatora

**W IDE:** ręcznie (VS Code nie ma Change Signature) zamień `seatsPaid(seats: readonly string[]): void` na `payRequested(): string`. Zbieranie miejsc i kwoty oraz składanie komunikatu z `PayButton.press` przenieś do mediatora. Usuń `connect` z przycisku i pusty już konstruktor ekranu.
**Po:**

```typescript
press(): string {
  if (!this.enabled) {
    return `Nieaktywny: ${this.label}`;
  }
  return this.mediator.payRequested();
}
```

**Uruchom:** test zielony; `S32SolutionTest` → `everyComponentKnowsOnlyTheMediator` sprawdza importy i pola komponentów, a `componentReportsWhatHappenedWithoutKnowingWhoReacts` testuje komponent z atrapą mediatora.
**Co powiedzieć:** przycisk pilnuje własnej reguły (aktywny dla 1-4 miejsc) i zgłasza "użytkownik chce zapłacić", a dane zbiera mediator. Ekran jest jedynym miejscem, które zna wszystkie cztery komponenty - i to jest zarazem ryzyko: pilnuj, żeby mediator nie zaczął liczyć cen.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s32 2 3`

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
