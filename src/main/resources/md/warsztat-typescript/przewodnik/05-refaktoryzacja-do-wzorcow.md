# Moduł 5. Refaktoryzacja do wzorców - warsztat CineLegacy (TypeScript): przewodnik prowadzącego

Trzydzieści dwie małe sceny w domenie kina CineLegacy. Każda pokazuje jedną refaktoryzację do wzorca (albo usunięcie wzorca) ze slajdów: od pytania "czy zawsze warto?" i usuwania zbędnych wzorców, przez ryzyka zmiany kontraktu i refaktoryzacje automatyczne w IDE, po wybór między podobnymi wzorcami (Polecenie, Strategia, Stan; Strategia i Metoda szablonowa; Łańcuch zobowiązań i Dekorator), granice systemu (Adapter, obiekt graniczny, Fasada, warstwa antykorupcyjna) oraz Budowniczego, Obserwatora, Mediatora i Odwiedzającego. Polskie nazwy wzorców i refaktoryzacji są takie jak na slajdach; tytuły scen zostają w wersji angielskiej, bo tak nazywają się refaktoryzacje w katalogu Kerievsky’ego i w IDE. Każda scena ma kod wyjściowy `start` z zapachem lub pułapką i gotowe snapshoty `stepN` po każdym ruchu, więc w dowolnym momencie można przeskoczyć do kolejnego kroku. Test równoważności (vitest) pilnuje, że wzorzec wprowadzamy bez zmiany obserwowalnego zachowania, a tam, gdzie zachowanie celowo się zmienia (moment wyboru strategii, nowy niezmiennik buildera, wygaszony wariant, naprawiony błąd skopiowanej orkiestracji, zaostrzona walidacja na granicy), osobny test to dokumentuje.

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
- Refaktoryzacje w VS Code: edytor potrafi wyodrębnić zaznaczony fragment do metody, funkcji albo stałej, zmienić nazwę symbolu w całym projekcie, dopisać szkielet składowych implementowanego interfejsu i przenieść deklarację najwyższego poziomu do nowego lub istniejącego pliku. Zmiany sygnatury, bezpiecznego usuwania, wyodrębnienia interfejsu, wyodrębnienia delegata, wchłonięcia metody, zepchnięcia składowych w dół ani wyodrębnienia nadklasy nie ma - te ruchy robimy ręcznie, a listę miejsc do poprawy daje `npm run typecheck` (albo wyszukanie wszystkich odwołań do symbolu).
- Motyw przewodni: **najpierw kontrakt, potem diagram klas**. W każdej scenie zaczynamy od pytania "co jest obserwowalne?" (wyjątek, kolejność efektów, moment wyboru, format trwały, typ i moment błędu na granicy). Siatkę bezpieczeństwa z sekcji 3 slajdów ćwiczy każda scena: test równoważności przed pierwszym ruchem, test różnicowy w s10, testy struktury w s26-s32.

## Mapa scen

Sceny są ułożone według numerów, tak jak katalogi i skrypt. Kolejność pokazu, zgodną z kolejnością slajdów, podaje sekcja „Wybór scen”. Katalogi są względne wobec `typescript/src/workshop/`, testy leżą w odpowiednim katalogu pod `typescript/test/workshop/`.

| Scena | Temat ze slajdów | Kroki | Katalog | Czas |
| --- | --- | --- | --- | --- |
| s01 | 6.1. Strategia, Polecenie czy Stan? Kto wybiera wariant i kiedy; 6.2. Strategia: sygnał w kodzie; 6.3. Strategia: kroki i wynik | 3 | `m5/s01_strategy` | ~12 min |
| s02 | 16.1. Zastąpienie instrukcji warunkowej polimorfizmem; 6.8. Typowe pomyłki przy wyborze | 3 | `m5/s02_polymorphism` | ~12 min |
| s03 | 16.2. Zastąpienie kodu typu klasą | 3 | `m5/s03_typecode` | ~12 min |
| s04 | 10.1. Ukrycie klas za Fabryką; 10.3. Fabryka jako strażnik spójności | 3 | `m5/s04_encapsulatefactory` | ~10 min |
| s05 | 10.2. Wyodrębnienie klasy fabryki; 10.3. Fabryka jako strażnik spójności | 3 | `m5/s05_extractfactory` | ~10 min |
| s06 | 12.1. Ukrycie budowy Kompozytu za Budowniczym; 12.2. Budowniczy: kiedy i na co uważać | 3 | `m5/s06_builder` | ~12 min |
| s07 | 8.1. Przeniesienie dodatków do Dekoratora; 8.2. Dekorator: na co uważać | 3 | `m5/s07_decorator` | ~12 min |
| s08 | 6.4. Stan: sygnał i tabela przejść; 6.5. Stan: wynik | 3 | `m5/s08_state` | ~15 min |
| s09 | 13.1. Zastąpienie zakodowanych powiadomień Obserwatorem; 13.2. Obserwator: zasady powiadamiania muszą być jawne | 3 | `m5/s09_observer` | ~15 min |
| s10 | 16.3. Zastąpienie ukrytego drzewa Kompozytem; 3.3. Test równoważności przed zmianą i po niej | 3 | `m5/s10_implicittree` | ~15 min |
| s11 | 16.4. Kompozyt przezroczysty czy bezpieczny | 2 | `m5/s11_safecomposite` | ~8 min |
| s12 | 16.5. Jeden element lub wiele oraz wyodrębnienie Kompozytu (zastąpienie rozróżnienia „jeden lub wiele” Kompozytem) | 3 | `m5/s12_onemany` | ~10 min |
| s13 | 16.5. Jeden element lub wiele oraz wyodrębnienie Kompozytu (wyodrębnienie Kompozytu) | 2 | `m5/s13_extractcomposite` | ~8 min |
| s14 | 9.1. Ujednolicenie interfejsów Adapterem; 9.2. Co adapter naprawdę musi przetłumaczyć | 3 | `m5/s14_adapter` | ~12 min |
| s15 | 6.6. Polecenie: łańcuch warunków zamieniony na rejestr poleceń | 3 | `m5/s15_command` | ~12 min |
| s16 | 7.1. Utworzenie Metody szablonowej: wspólny szkielet, różne kroki | 2 | `m5/s16_templatemethod` | ~8 min |
| s17 | 16.7. Ograniczenie liczby instancji Singletonem; 17.4. Wchłonięcie Singletona | 3 | `m5/s17_singleton` | ~10 min |
| s18 | 16.6. Przeniesienie zbierania wyników do parametru zbierającego | 3 | `m5/s18_collectingparameter` | ~8 min |
| s19 | 15.1. Odwiedzający i macierz zmian; 15.2. Odwiedzający albo hierarchia `sealed` i `switch` | 3 | `m5/s19_visitor` | ~12 min |
| s20 | 4.2. Najpierw rodzaj zmienności, potem wzorzec; 18.1. Mapa decyzji (1/2); 18.2. Mapa decyzji (2/2); 18.3. Lista kontrolna | 3 | `m5/s20_decisionmap` | ~12 min |
| s21 | 17.5. Wzorzec jako decyzja odwracalna; 17.2. Refaktoryzacje, które usuwają wzorzec; 5.2. Kiedy wzorzec się nie opłaca | 3 | `m5/s21_reversiblepattern` | ~12 min |
| s22 | 17.3. Usunięcie pośrednika; 17.2. Refaktoryzacje, które usuwają wzorzec | 3 | `m5/s22_middleman` | ~8 min |
| s23 | 5.3. Refaktoryzacja a zmiana kontraktu; 5.4. Co trzeba zachować przy wprowadzaniu wzorca; 3.10. Bezpieczna pętla refaktoryzacji do wzorca | 3 | `m5/s23_contractchange` | ~10 min |
| s24 | 3.6. Refaktoryzacje automatyczne w IDE: co dają, a czego nie widzą; 3.7. Zmiana nazwy i wyodrębnienie metody w drodze do wzorca | 3 | `m5/s24_rename` | ~12 min |
| s25 | 3.7. Zmiana nazwy i wyodrębnienie metody w drodze do wzorca | 3 | `m5/s25_extractmethod` | ~15 min |
| s26 | 6.1. Strategia, Polecenie czy Stan? Kto wybiera wariant i kiedy; 6.7. Trzy osie w jednej klasie: każda dostaje własny mechanizm; 6.8. Typowe pomyłki przy wyborze | 3 | `m5/s26_variability` | ~15 min |
| s27 | 7.2. Gdy punkty zaczepienia obejmują dwie niezależne osie; 7.3. Zastąpienie dziedziczenia delegacją: osie jako strategie; 7.4. Strategia czy Metoda szablonowa? | 3 | `m5/s27_templatetostrategy` | ~12 min |
| s28 | 8.3. Łańcuch `if/else`, w którym kolejność jest regułą; 8.4. Łańcuch zobowiązań: ogniwo i złożenie łańcucha; 8.5. Łańcuch zobowiązań czy Dekorator? | 3 | `m5/s28_chain` | ~12 min |
| s29 | 9.3. Surowe dane z zewnątrz w kodzie domeny; 9.4. Obiekt graniczny: sprawdzone dane na granicy systemu; 9.5. Adapter czy obiekt graniczny? | 3 | `m5/s29_boundaryobject` | ~12 min |
| s30 | 11.1. Klienci sami koordynują podsystemy; 11.2. Wyodrębnienie Fasady: jedna operacja zamiast znajomości kroków | 3 | `m5/s30_facade` | ~12 min |
| s31 | 11.3. Obcy model w regułach domeny; 11.4. Warstwa antykorupcyjna: port, tłumacz i model domenowy; 11.5. Fasada a warstwa antykorupcyjna | 3 | `m5/s31_anticorruptionlayer` | ~12 min |
| s32 | 14.1. Komponenty połączone każdy z każdym; 14.2. Wprowadzenie Mediatora: reguły współpracy w jednym miejscu; 14.3. Obserwator czy Mediator? | 3 | `m5/s32_mediator` | ~12 min |

Liczby domeny (ceny formatów, zniżki, VIP, okulary, opłata online, punkty lojalnościowe, zwroty, VAT) są wspólne dla całego warsztatu. Wartości spoza wspólnej listy (program "tydzień studenta" 50%, ubezpieczenie biletu 4.00, "tani wtorek" -30%, weekend +2.00, ceny premier i maratonów, limity zatwierdzania zwrotów 100.00 i 1000.00, autoryzacja częściowa terminala do 300.00, voucher KINO10) są przykładowe i opisane w komentarzach TSDoc sceny.

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

**Sceny opcjonalne**, gdy grupa pracuje szybciej albo pyta o konkretny wzorzec: s22 i s17 (usuwanie wzorców: usunięcie pośrednika, Singleton i jego wchłonięcie), s24 (zmiana nazwy i nazwy poza kodem), s01, s08 i s15 (każdy z trzech wzorców sekcji 6 osobno, w szczegółach), s16 (klasyczna Metoda szablonowa), s07 (Dekorator), s14 (Adapter), s04 i s05 (jeden blok o Fabryce z porównaniem obu refaktoryzacji), s06 (Budowniczy), s09 (Obserwator, dobry kontrast do s32), s19 (Odwiedzający). Sceny z sekcji 16 (s02, s03, s10-s13, s18) zostawiamy do samodzielnej pracy w parach albo jako zadanie domowe; s11 i s13 mają po 2 kroki i dobrze sprawdzają się jako krótkie ćwiczenie. Na podsumowanie wróć do s20 i poproś salę o przypisanie każdej pokazanej sceny do wiersza mapy decyzji (slajdy 18.1-18.2).

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

**Zmiany:**

1. Utwórz nowy plik `DiscountPolicy.ts` z interfejsem `DiscountPolicy` i jedną metodą `discount(base: Money, ticketType: string): Money`.
2. W `TicketPricer` wyodrębnij cały łańcuch `if` z metody `price` do nowej metody `legacyDiscount` (Extract Method) z parametrami `base`, `ticketType`, `program` i wynikiem `Money`. Metoda nie używa `this`, więc oznacz ją `private static`; każda gałąź zwraca zniżkę zamiast przypisywać ją do zmiennej.
3. W `price` utwórz strategię przejściową jako literał obiektu typu `DiscountPolicy`, delegujący do `legacyDiscount`, i policz cenę przez `policy.discount(base, ticketType)`. W `TicketPricer.ts` dodaj import `import type { DiscountPolicy } from './DiscountPolicy.js';`.
4. `PriceBoard` bez zmian w kodzie (w snapshotach zmieniają się jeszcze tylko komentarze TSDoc opisujące krok - tak w całej scenie).

**Po (`DiscountPolicy.ts`, nowy plik):**

```typescript
import type { Money } from '../../../shared/Money.js';

/** Kontrakt strategii - wysokość zniżki dla ceny bazowej i typu biletu. */
export interface DiscountPolicy {
  discount(base: Money, ticketType: string): Money;
}
```

**Przed (`TicketPricer.ts`, metoda `price`):**

```typescript
price(base: Money, ticketType: string, program: string | null): Money {
  if (base.compareTo(Money.ZERO) < 0) {
    throw new IllegalArgumentError('base price must not be negative');
  }
  if (program === null) {
    throw new IllegalArgumentError('program must not be null');
  }
  let discount: Money;
  if (program === 'PREMIERE') {
    discount = Money.ZERO;
  } else if (program === 'STUDENT_WEEK' && ticketType === 'S') {
    discount = base.percent(50);
  } else if (program === 'STANDARD' || program === 'STUDENT_WEEK') {
    let percent: number;
    switch (ticketType) {
      case 'N': percent = 0; break;
      case 'S': percent = 25; break;
      case 'E': percent = 30; break;
      case 'C': percent = 40; break;
      default: throw new IllegalArgumentError(`unknown ticket type: ${ticketType}`);
    }
    discount = base.percent(percent);
  } else {
    throw new IllegalArgumentError(`unknown program: ${program}`);
  }
  return base.minus(discount);
}
```

**Po (`TicketPricer.ts`, metody `price` i `legacyDiscount`):**

```typescript
price(base: Money, ticketType: string, program: string | null): Money {
  if (base.compareTo(Money.ZERO) < 0) {
    throw new IllegalArgumentError('base price must not be negative');
  }
  if (program === null) {
    throw new IllegalArgumentError('program must not be null');
  }
  const policy: DiscountPolicy = { discount: (b, type) => TicketPricer.legacyDiscount(b, type, program) };
  return base.minus(policy.discount(base, ticketType));
}

private static legacyDiscount(base: Money, ticketType: string, program: string): Money {
  if (program === 'PREMIERE') {
    return Money.ZERO;
  } else if (program === 'STUDENT_WEEK' && ticketType === 'S') {
    return base.percent(50);
  } else if (program === 'STANDARD' || program === 'STUDENT_WEEK') {
    let percent: number;
    switch (ticketType) {
      case 'N': percent = 0; break;
      case 'S': percent = 25; break;
      case 'E': percent = 30; break;
      case 'C': percent = 40; break;
      default: throw new IllegalArgumentError(`unknown ticket type: ${ticketType}`);
    }
    return base.percent(percent);
  }
  throw new IllegalArgumentError(`unknown program: ${program}`);
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s01` - zielone.
**Co powiedzieć:** pierwszy commit jest mały i odwracalny: kontrakt strategii istnieje, ale deleguje do starego kodu. Nie zaczynamy od tworzenia wszystkich klas wzorca.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s01 0 1`

### Krok 2: przeniesienie gałęzi do strategii

**Zmiany:**

1. Utwórz klasę `StandardDiscount implements DiscountPolicy`; szkielet metody `discount` może dopisać edytor, ciało skopiuj z gałęzi STANDARD (`switch` po typie biletu). Uruchom test.
2. Tak samo utwórz `PremiereDiscount` (zwraca `Money.ZERO`, typu biletu nie sprawdza - jak w start). Test.
3. Utwórz `StudentWeekDiscount`: student dostaje 50%, pozostałych deleguje do `StandardDiscount` przekazanego w konstruktorze. Test.
4. Usuń `legacyDiscount`, a w jego miejsce dodaj `private static policyFor(program: string): DiscountPolicy` ze `switch` tworzącym strategię; `price` liczy `base.minus(TicketPricer.policyFor(program).discount(base, ticketType))` (walidacja `base` i `program === null` w `price` zostaje bez zmian). W `TicketPricer.ts` dodaj importy `PremiereDiscount`, `StandardDiscount` i `StudentWeekDiscount` z plików `./XxxDiscount.js`.

**Po (`StandardDiscount.ts`, nowy plik):**

```typescript
import { IllegalArgumentError } from '../../../../shared/errors.js';
import type { Money } from '../../../shared/Money.js';
import type { DiscountPolicy } from './DiscountPolicy.js';

/** Gałąź STANDARD przeniesiona do strategii. Bezstanowa - można ją współdzielić. */
export class StandardDiscount implements DiscountPolicy {
  discount(base: Money, ticketType: string): Money {
    let percent: number;
    switch (ticketType) {
      case 'N': percent = 0; break;
      case 'S': percent = 25; break;
      case 'E': percent = 30; break;
      case 'C': percent = 40; break;
      default: throw new IllegalArgumentError(`unknown ticket type: ${ticketType}`);
    }
    return base.percent(percent);
  }
}
```

**Po (`PremiereDiscount.ts`, nowy plik):**

```typescript
import { Money } from '../../../shared/Money.js';
import type { DiscountPolicy } from './DiscountPolicy.js';

/** Premiera - brak zniżek (typ biletu nie jest nawet sprawdzany, jak w start). */
export class PremiereDiscount implements DiscountPolicy {
  discount(_base: Money, _ticketType: string): Money {
    return Money.ZERO;
  }
}
```

**Po (`StudentWeekDiscount.ts`, nowy plik):**

```typescript
import type { Money } from '../../../shared/Money.js';
import type { DiscountPolicy } from './DiscountPolicy.js';

/** Tydzień studenta - student 50%, pozostali jak w programie standardowym. */
export class StudentWeekDiscount implements DiscountPolicy {
  constructor(private readonly fallback: DiscountPolicy) {}

  discount(base: Money, ticketType: string): Money {
    return ticketType === 'S' ? base.percent(50) : this.fallback.discount(base, ticketType);
  }
}
```

**Przed (`TicketPricer.ts`):**

```typescript
const policy: DiscountPolicy = { discount: (b, type) => TicketPricer.legacyDiscount(b, type, program) };
return base.minus(policy.discount(base, ticketType));
```

oraz cała metoda `legacyDiscount` z kroku 1.

**Po (`TicketPricer.ts`, koniec `price` i metoda `policyFor`):**

```typescript
return base.minus(TicketPricer.policyFor(program).discount(base, ticketType));
```

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

**Zmiany:**

1. Utwórz klasę `DiscountPrograms` z prywatnym konstruktorem. Przenieś do niej `policyFor` jako `static forName(program)` (wytnij z `TicketPricer` i wklej - VS Code nie przenosi metod między klasami). Instancje strategii trzymaj w stałych `private static readonly` i zwracaj te same obiekty (strategie są bezstanowe, więc można je współdzielić). Sprawdzenie `null` programu przenieś z `price` do `forName`.
2. Zmień ręcznie sygnatury (Change Signature): `TicketPricer` dostaje `DiscountPolicy` w konstruktorze (z `requireNonNull`), a `price(base, ticketType)` traci parametr programu i deleguje do `this.policy.discount(...)`.
3. Uruchom `npm run typecheck` - wskaże klienta `PriceBoard`. Usuń w nim pole z gotowym `TicketPricer`; `priceFor` składa kontekst ze strategią z `DiscountPrograms.forName(request.program)` (dodaj import `DiscountPrograms`).

**Przed (`TicketPricer.ts`):**

```typescript
export class TicketPricer {
  price(base: Money, ticketType: string, program: string | null): Money {
    if (base.compareTo(Money.ZERO) < 0) {
      throw new IllegalArgumentError('base price must not be negative');
    }
    if (program === null) {
      throw new IllegalArgumentError('program must not be null');
    }
    return base.minus(TicketPricer.policyFor(program).discount(base, ticketType));
  }

  private static policyFor(program: string): DiscountPolicy {
    switch (program) {
      case 'STANDARD': return new StandardDiscount();
      case 'STUDENT_WEEK': return new StudentWeekDiscount(new StandardDiscount());
      case 'PREMIERE': return new PremiereDiscount();
      default: throw new IllegalArgumentError(`unknown program: ${program}`);
    }
  }
}
```

**Przed (`PriceBoard.ts`):**

```typescript
export class PriceBoard {
  private readonly pricer = new TicketPricer();

  priceFor(request: PriceRequest): Money {
    return this.pricer.price(request.base, request.ticketType, request.program);
  }
}
```

**Po (`DiscountPrograms.ts`, nowy plik):**

```typescript
import { IllegalArgumentError } from '../../../../shared/errors.js';
import type { DiscountPolicy } from './DiscountPolicy.js';
import { PremiereDiscount } from './PremiereDiscount.js';
import { StandardDiscount } from './StandardDiscount.js';
import { StudentWeekDiscount } from './StudentWeekDiscount.js';

/**
 * Wybór strategii przeniesiony do korzenia kompozycji (konfiguracja kina).
 * Strategie są bezstanowe, więc współdzielimy jedne instancje.
 */
export class DiscountPrograms {
  private static readonly STANDARD: DiscountPolicy = new StandardDiscount();
  private static readonly STUDENT_WEEK: DiscountPolicy = new StudentWeekDiscount(DiscountPrograms.STANDARD);
  private static readonly PREMIERE: DiscountPolicy = new PremiereDiscount();

  private constructor() {}

  static forName(program: string | null): DiscountPolicy {
    if (program === null) {
      throw new IllegalArgumentError('program must not be null');
    }
    switch (program) {
      case 'STANDARD': return DiscountPrograms.STANDARD;
      case 'STUDENT_WEEK': return DiscountPrograms.STUDENT_WEEK;
      case 'PREMIERE': return DiscountPrograms.PREMIERE;
      default: throw new IllegalArgumentError(`unknown program: ${program}`);
    }
  }
}
```

**Po (`TicketPricer.ts`, cały plik):**

```typescript
import { IllegalArgumentError } from '../../../../shared/errors.js';
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Money } from '../../../shared/Money.js';
import type { DiscountPolicy } from './DiscountPolicy.js';

/**
 * Kontekst dostaje strategię w konstruktorze i nie zna nazw programów.
 * Uwaga: wybór w konstruktorze zamraża decyzję - zmienia moment, w którym pada błąd nieznanego programu.
 */
export class TicketPricer {
  private readonly policy: DiscountPolicy;

  constructor(policy: DiscountPolicy) {
    this.policy = requireNonNull(policy, 'policy');
  }

  price(base: Money, ticketType: string): Money {
    if (base.compareTo(Money.ZERO) < 0) {
      throw new IllegalArgumentError('base price must not be negative');
    }
    return base.minus(this.policy.discount(base, ticketType));
  }
}
```

**Po (`PriceBoard.ts`, metoda `priceFor`):**

```typescript
priceFor(request: PriceRequest): Money {
  return new TicketPricer(DiscountPrograms.forName(request.program))
    .price(request.base, request.ticketType);
}
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

Tworzenie jest w jednym miejscu: `Screening.fromRow(row: ScreeningRow)` (mapowanie wiersza z bazy). Zawsze najpierw szukamy miejsc tworzenia i deserializacji (w VS Code: wyszukaj wszystkie odwołania do konstruktora).

### Krok 1: Extract Subclass dla jednej gałęzi

**Zmiany:**

1. W `Screening` zmień konstruktor z `private` na `protected`, a pole `title` oznacz `protected readonly` - podklasa musi je widzieć.
2. Pod klasą `Screening`, w tym samym pliku, utwórz `MarathonScreening extends Screening` (Extract Subclass) z polem `films`; konstruktor woła `super(Kind.MARATHON, title, films)`. Nadpisz trzy metody z modyfikatorem `override`, wpisując je ręcznie (edytor dopisuje szkielet tylko dla metod abstrakcyjnych), i skopiuj do nich gałęzie MARATHON.
3. Plik `MarathonScreening.ts` tylko re-eksportuje klasę z `Screening.ts`.
4. W `fromRow` dla `'MARATHON'` twórz podklasę; każda gałąź zwraca gotowy obiekt (zmienna lokalna `kind` znika). W bazie gałęzie MARATHON trzech metod rzucają `IllegalStateError('handled by MarathonScreening')` - do importu z `errors.js` dopisz `IllegalStateError`.

**Przed (`Screening.ts`, konstruktor i `fromRow`):**

```typescript
private constructor(
  private readonly kind: Kind,
  private readonly title: string,
  private readonly value: number,
) {}

static fromRow(row: ScreeningRow): Screening {
  let kind: Kind;
  switch (row.kind) {
    case 'REGULAR': kind = Kind.REGULAR; break;
    case 'PREMIERE': kind = Kind.PREMIERE; break;
    case 'MARATHON': kind = Kind.MARATHON; break;
    default: throw new IllegalArgumentError(`unknown screening kind: ${row.kind}`);
  }
  return new Screening(kind, row.title, row.value);
}
```

**Przed (`Screening.ts`, metody `label` i `price`; `durationMinutes` wyżej w "Co widzimy"):**

```typescript
label(): string {
  switch (this.kind) {
    case Kind.REGULAR: return this.title;
    case Kind.PREMIERE: return `Premiera: ${this.title}`;
    case Kind.MARATHON: return `Maraton: ${this.title} (${this.value} filmy)`;
  }
}

price(): Money {
  switch (this.kind) {
    case Kind.REGULAR: return Money.of('25.00');
    case Kind.PREMIERE: return Money.of('35.00');
    case Kind.MARATHON: return Money.of('20.00').times(this.value);
  }
}
```

**Po (`Screening.ts`, konstruktor, `fromRow` i gałęzie MARATHON trzech metod):**

```typescript
protected constructor(
  private readonly kind: Kind,
  protected readonly title: string,
  private readonly value: number,
) {}

static fromRow(row: ScreeningRow): Screening {
  switch (row.kind) {
    case 'REGULAR': return new Screening(Kind.REGULAR, row.title, row.value);
    case 'PREMIERE': return new Screening(Kind.PREMIERE, row.title, row.value);
    case 'MARATHON': return new MarathonScreening(row.title, row.value);
    default: throw new IllegalArgumentError(`unknown screening kind: ${row.kind}`);
  }
}
```

W `label`, `durationMinutes` i `price` gałęzie REGULAR i PREMIERE zostają bez zmian, a gałąź MARATHON w każdej z nich to teraz:

```typescript
case Kind.MARATHON: throw new IllegalStateError('handled by MarathonScreening');
```

**Po (`Screening.ts`, klasa na końcu pliku):**

```typescript
/** Pierwsza podklasa - maraton ma własne, nazwane dane (films zamiast value). */
export class MarathonScreening extends Screening {
  constructor(title: string, private readonly films: number) {
    super(Kind.MARATHON, title, films);
  }

  override label(): string {
    return `Maraton: ${this.title} (${this.films} filmy)`;
  }

  override durationMinutes(): number {
    return this.films * 120 + (this.films - 1) * 15;
  }

  override price(): Money {
    return Money.of('20.00').times(this.films);
  }
}
```

**Po (`MarathonScreening.ts`, nowy plik):**

```typescript
// Klasa zdefiniowana w Screening.ts (unikamy cyklu importów ESM przy "extends").
export { MarathonScreening } from './Screening.js';
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s02` - zielone.
**Co powiedzieć:** jedna gałąź naraz, jeden test kontraktowy dla wszystkich rodzajów. Martwa gałąź rzuca wyjątek zamiast po cichu liczyć bzdury.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s02 0 1`

### Krok 2: pozostałe podklasy, baza abstrakcyjna

**Zmiany:**

1. Powtórz ruch z kroku 1 dla REGULAR i PREMIERE: klasy `RegularScreening` i `PremiereScreening` w tym samym pliku, obie z polem `runtime` i trzema metodami `override`; pliki `RegularScreening.ts` i `PremiereScreening.ts` re-eksportują klasy. `fromRow` tworzy podklasę dla każdego rodzaju.
2. Oznacz klasę `Screening` i jej trzy metody jako `abstract`; konstruktor bazy przyjmuje już tylko `title`. Metody `label`, `durationMinutes` i `price` wraz ze switchami znikają z bazy - zostają deklaracje `abstract`.
3. Usuń pole `kind` i enum `Kind` (ręcznie - `npm run typecheck` pokaże pozostałe użycia); konstruktor `MarathonScreening` woła teraz `super(title)`. Z importu `errors.js` usuń `IllegalStateError` (nie jest już używany).

**Przed (`Screening.ts`, baza z kroku 1):**

konstruktor, `fromRow` i trzy metody ze `switch (this.kind)` - jak w bloku Po kroku 1.

**Po (`Screening.ts`, baza i `fromRow`):**

```typescript
export abstract class Screening {
  protected constructor(protected readonly title: string) {}

  static fromRow(row: ScreeningRow): Screening {
    switch (row.kind) {
      case 'REGULAR': return new RegularScreening(row.title, row.value);
      case 'PREMIERE': return new PremiereScreening(row.title, row.value);
      case 'MARATHON': return new MarathonScreening(row.title, row.value);
      default: throw new IllegalArgumentError(`unknown screening kind: ${row.kind}`);
    }
  }

  abstract label(): string;

  abstract durationMinutes(): number;

  abstract price(): Money;
}
```

**Po (`Screening.ts`, nowe podklasy pod bazą; `MarathonScreening` jak w kroku 1, tylko konstruktor woła `super(title)`):**

```typescript
/** Zwykły seans - 20 minut reklam przed filmem. */
export class RegularScreening extends Screening {
  constructor(title: string, private readonly runtime: number) {
    super(title);
  }

  override label(): string {
    return this.title;
  }

  override durationMinutes(): number {
    return 20 + this.runtime;
  }

  override price(): Money {
    return Money.of('25.00');
  }
}

/** Premiera - 30 minut spotkania z twórcami zamiast reklam. */
export class PremiereScreening extends Screening {
  constructor(title: string, private readonly runtime: number) {
    super(title);
  }

  override label(): string {
    return `Premiera: ${this.title}`;
  }

  override durationMinutes(): number {
    return 30 + this.runtime;
  }

  override price(): Money {
    return Money.of('35.00');
  }
}
```

**Po (`RegularScreening.ts` i `PremiereScreening.ts`, nowe pliki - analogicznie do `MarathonScreening.ts`):**

```typescript
// Klasa zdefiniowana w Screening.ts (unikamy cyklu importów ESM przy "extends").
export { RegularScreening } from './Screening.js';
```

**Uruchom:** test zielony.
**Co powiedzieć:** `switch` w miejscu tworzenia jest właściwy - to jest wiedza o konstrukcji. Znikają za to switche w zachowaniu.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s02 1 2`

### Krok 3: forma TypeScript - unia dyskryminowana

**Zmiany:**

1. W `Screening.ts` zamień klasę bazową na unię `type Screening = RegularScreening | PremiereScreening | MarathonScreening` oraz stały obiekt `Screening` z funkcją `fromRow` (typ i wartość mogą dzielić nazwę, więc klienci nie zmieniają importów).
2. Wspólne składowe opisz interfejsem `ScreeningMembers` (pola `kind` i `title`, metody `label`, `durationMinutes`, `price`).
3. Każdy rodzaj zamień na niezmienną klasę `implements ScreeningMembers` z literałem `readonly kind = 'MARATHON'` (odpowiednio `'REGULAR'`, `'PREMIERE'`) i nazwanymi danymi (`runtime`, `films`); pole `value` znika.
4. Podtypy nie dziedziczą już po bazie, więc przenieś je do własnych plików - re-eksporty znikają, a ciała metod zostają jak w kroku 2 (bez `override`, bez `super`).

**Przed:** cały `Screening.ts` z kroku 2 (baza abstrakcyjna + trzy podklasy) oraz trzy pliki re-eksportów.

**Po (`Screening.ts`, cały plik):**

```typescript
import { IllegalArgumentError } from '../../../../shared/errors.js';
import type { Money } from '../../../shared/Money.js';
import type { ScreeningRow } from '../ScreeningRow.js';
import { MarathonScreening } from './MarathonScreening.js';
import { PremiereScreening } from './PremiereScreening.js';
import { RegularScreening } from './RegularScreening.js';

/** Wspólne zachowanie każdego rodzaju seansu (w Javie: metody sealed interface Screening). */
export interface ScreeningMembers {
  readonly kind: string;
  readonly title: string;

  label(): string;

  durationMinutes(): number;

  price(): Money;
}

/**
 * Forma TypeScript - unia dyskryminowana (pole kind) zamiast sealed interface i rekordów.
 * Zestaw rodzajów jest zamknięty, więc kompilator sprawdza wyczerpujące switche także u klientów.
 */
export type Screening = RegularScreening | PremiereScreening | MarathonScreening;

export const Screening = {
  fromRow(row: ScreeningRow): Screening {
    switch (row.kind) {
      case 'REGULAR': return new RegularScreening(row.title, row.value);
      case 'PREMIERE': return new PremiereScreening(row.title, row.value);
      case 'MARATHON': return new MarathonScreening(row.title, row.value);
      default: throw new IllegalArgumentError(`unknown screening kind: ${row.kind}`);
    }
  },
};
```

**Po (`RegularScreening.ts`, cały plik):**

```typescript
import { Money } from '../../../shared/Money.js';
import type { ScreeningMembers } from './Screening.js';

/** Zwykły seans jako niezmienny obiekt wartości (w Javie: rekord). */
export class RegularScreening implements ScreeningMembers {
  readonly kind = 'REGULAR';

  constructor(readonly title: string, readonly runtime: number) {}

  label(): string {
    return this.title;
  }

  durationMinutes(): number {
    return 20 + this.runtime;
  }

  price(): Money {
    return Money.of('25.00');
  }
}
```

**Po (`PremiereScreening.ts`, cały plik):**

```typescript
import { Money } from '../../../shared/Money.js';
import type { ScreeningMembers } from './Screening.js';

/** Premiera jako niezmienny obiekt wartości (w Javie: rekord). */
export class PremiereScreening implements ScreeningMembers {
  readonly kind = 'PREMIERE';

  constructor(readonly title: string, readonly runtime: number) {}

  label(): string {
    return `Premiera: ${this.title}`;
  }

  durationMinutes(): number {
    return 30 + this.runtime;
  }

  price(): Money {
    return Money.of('35.00');
  }
}
```

**Po (`MarathonScreening.ts`, cały plik):**

```typescript
import { Money } from '../../../shared/Money.js';
import type { ScreeningMembers } from './Screening.js';

/** Maraton jako niezmienny obiekt wartości (w Javie: rekord) - pole films ma jedno znaczenie. */
export class MarathonScreening implements ScreeningMembers {
  readonly kind = 'MARATHON';

  constructor(readonly title: string, readonly films: number) {}

  label(): string {
    return `Maraton: ${this.title} (${this.films} filmy)`;
  }

  durationMinutes(): number {
    return this.films * 120 + (this.films - 1) * 15;
  }

  price(): Money {
    return Money.of('20.00').times(this.films);
  }
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

**Zmiany:**

1. Utwórz klasę `Format` ze stałymi instancjami `TWO_D` (kod 1), `THREE_D` (2), `IMAX` (3): prywatny konstruktor, pola `name` (unia literałów) i `code`, metoda `values()` oraz `fromCode(code)` rzucająca ten sam wyjątek co start (`unknown format code: ...`).
2. W `describe` zamień liczbę na `Format` zaraz po odczycie; typy parametrów metod prywatnych (`label`, `basePrice`, `needsGlasses`, `toCsv`) zmień ręcznie z `number` na `Format` - kompilator wskaże resztę miejsc.
3. Switche w `label` i `basePrice` przejdź na `format.name` (wyczerpujące, bez `default`); `needsGlasses` porównuje `format === Format.THREE_D`. Usuń stałe `FORMAT_2D`, `FORMAT_3D`, `FORMAT_IMAX`. Do CSV zapisuj `format.code`. Import `IllegalArgumentError` przenosi się z `ScreeningCsv.ts` do `Format.ts`.

**Po (`Format.ts`, nowy plik):**

```typescript
import { IllegalArgumentError } from '../../../../shared/errors.js';

/**
 * Nowy typ dla kodu. Zamknięty zestaw instancji wystarcza - formatów jest mało.
 * (W Javie: enum z polem code; TypeScriptowy enum nie ma pól, więc klasa ze stałymi instancjami.)
 */
export class Format {
  static readonly TWO_D = new Format('TWO_D', 1);
  static readonly THREE_D = new Format('THREE_D', 2);
  static readonly IMAX = new Format('IMAX', 3);

  private constructor(readonly name: 'TWO_D' | 'THREE_D' | 'IMAX', readonly code: number) {}

  static values(): readonly Format[] {
    return [Format.TWO_D, Format.THREE_D, Format.IMAX];
  }

  static fromCode(code: number): Format {
    for (const format of Format.values()) {
      if (format.code === code) {
        return format;
      }
    }
    throw new IllegalArgumentError(`unknown format code: ${code}`);
  }

  toString(): string {
    return this.name;
  }
}
```

**Przed (`ScreeningCsv.ts`, cały plik bez importów):**

```typescript
export class ScreeningCsv {
  static readonly FORMAT_2D = 1;
  static readonly FORMAT_3D = 2;
  static readonly FORMAT_IMAX = 3;

  /** Wiersz "tytuł;kod" - np. "Diuna;3". */
  describe(line: string): string {
    const parts = line.split(';');
    const title = parts[0]!;
    const formatCode = parseInteger(parts[1]!.trim());
    return `${title}|${this.label(formatCode)}|${this.basePrice(formatCode).toString()}`
      + `|okulary:${this.needsGlasses(formatCode) ? 'tak' : 'nie'}`
      + `|csv=${this.toCsv(title, formatCode)}`;
  }

  private label(formatCode: number): string {
    switch (formatCode) {
      case ScreeningCsv.FORMAT_2D: return '2D';
      case ScreeningCsv.FORMAT_3D: return '3D';
      case ScreeningCsv.FORMAT_IMAX: return 'IMAX';
      default: throw new IllegalArgumentError(`unknown format code: ${formatCode}`);
    }
  }

  private basePrice(formatCode: number): Money {
    switch (formatCode) {
      case ScreeningCsv.FORMAT_2D: return Money.of('25.00');
      case ScreeningCsv.FORMAT_3D: return Money.of('32.00');
      case ScreeningCsv.FORMAT_IMAX: return Money.of('40.00');
      default: throw new IllegalArgumentError(`unknown format code: ${formatCode}`);
    }
  }

  private needsGlasses(formatCode: number): boolean {
    return formatCode === ScreeningCsv.FORMAT_3D;
  }

  private toCsv(title: string, formatCode: number): string {
    return `${title};${formatCode}`;
  }
}
```

**Po (`ScreeningCsv.ts`, cały plik):**

```typescript
import { Money } from '../../../shared/Money.js';
import { parseInteger } from '../Integers.js';
import { Format } from './Format.js';

/** Liczba zamieniana na Format na granicy (odczyt CSV); do CSV nadal trafia format.code. */
export class ScreeningCsv {
  /** Wiersz "tytuł;kod" - np. "Diuna;3". */
  describe(line: string): string {
    const parts = line.split(';');
    const title = parts[0]!;
    const format = Format.fromCode(parseInteger(parts[1]!.trim()));
    return `${title}|${this.label(format)}|${this.basePrice(format).toString()}`
      + `|okulary:${this.needsGlasses(format) ? 'tak' : 'nie'}`
      + `|csv=${this.toCsv(title, format)}`;
  }

  // switch po format.name jest wyczerpujący - kompilator pilnuje wszystkich formatów.
  private label(format: Format): string {
    switch (format.name) {
      case 'TWO_D': return '2D';
      case 'THREE_D': return '3D';
      case 'IMAX': return 'IMAX';
    }
  }

  private basePrice(format: Format): Money {
    switch (format.name) {
      case 'TWO_D': return Money.of('25.00');
      case 'THREE_D': return Money.of('32.00');
      case 'IMAX': return Money.of('40.00');
    }
  }

  private needsGlasses(format: Format): boolean {
    return format === Format.THREE_D;
  }

  private toCsv(title: string, format: Format): string {
    return `${title};${format.code}`;
  }
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s03` - zielone, łącznie z "nieznany kod" i "kod nie jest liczbą".
**Co powiedzieć:** zamknięty zestaw instancji wystarcza, bo formatów jest mało. Trwały kod to jawne pole, nigdy pozycja w `values()` ani `name`.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s03 0 1`

### Krok 2: Move Method do typu

**Zmiany:**

1. Przenieś wiedzę z metod `label`, `basePrice`, `needsGlasses` do typu `Format` jako pola `readonly label`, `basePrice`, `requiresGlasses` (Move Method, ręcznie): wartości z gałęzi `switch` trafiają do wywołań konstruktora stałych instancji, a cena jest tworzona z łańcucha w konstruktorze.
2. Usuń z `ScreeningCsv` metody prywatne `label`, `basePrice`, `needsGlasses` i `toCsv` oraz import `Money`; `describe` pyta obiekt (`format.label`, `format.basePrice`, `format.requiresGlasses`) i skleja zapis `${title};${format.code}`. Metody `values`, `fromCode` i `toString` w `Format` zostają bez zmian; w `Format.ts` dochodzi import `Money`.

**Przed:** `Format.ts` i `ScreeningCsv.ts` jak w blokach Po kroku 1.

**Po (`Format.ts`, stałe i konstruktor; reszta klasy bez zmian):**

```typescript
export class Format {
  static readonly TWO_D = new Format('TWO_D', 1, '2D', '25.00', false);
  static readonly THREE_D = new Format('THREE_D', 2, '3D', '32.00', true);
  static readonly IMAX = new Format('IMAX', 3, 'IMAX', '40.00', false);

  readonly basePrice: Money;

  private constructor(
    readonly name: 'TWO_D' | 'THREE_D' | 'IMAX',
    readonly code: number,
    readonly label: string,
    basePrice: string,
    readonly requiresGlasses: boolean,
  ) {
    this.basePrice = Money.of(basePrice);
  }
  // values(), fromCode(), toString() - bez zmian
}
```

**Po (`ScreeningCsv.ts`, cały plik):**

```typescript
import { parseInteger } from '../Integers.js';
import { Format } from './Format.js';

/** Klient pyta obiekt formatu zamiast wykonywać switch. */
export class ScreeningCsv {
  /** Wiersz "tytuł;kod" - np. "Diuna;3". */
  describe(line: string): string {
    const parts = line.split(';');
    const title = parts[0]!;
    const format = Format.fromCode(parseInteger(parts[1]!.trim()));
    return `${title}|${format.label}|${format.basePrice.toString()}`
      + `|okulary:${format.requiresGlasses ? 'tak' : 'nie'}`
      + `|csv=${title};${format.code}`;
  }
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** nowy typ przejmuje walidację, normalizację i operacje pojęcia. Nie oznacza to automatycznie hierarchii klas.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s03 1 2`

### Krok 3: mapper migracyjny na granicy trwałości

**Zmiany:**

1. Utwórz klasę `FormatCodes` z prywatnym konstruktorem i mapą `private static readonly CODES: ReadonlyMap<Format, number>`; `fromCode(code)` szuka formatu po kodzie i rzuca ten sam wyjątek co dotąd, `toCode(format)` czyta kod z mapy.
2. Usuń z `Format` pole `code` i metodę `fromCode` - typ domenowy nie zna już kodów trwałych.
3. Przełącz `ScreeningCsv` na mapper: odczyt przez `FormatCodes.fromCode(...)`, zapis przez `FormatCodes.toCode(format)`; import `Format` zamienia się na import `FormatCodes`. Reszta kodu rozmawia wyłącznie z `Format`.

**Przed (`ScreeningCsv.ts`, metoda `describe`):**

```typescript
const format = Format.fromCode(parseInteger(parts[1]!.trim()));
return `${title}|${format.label}|${format.basePrice.toString()}`
  + `|okulary:${format.requiresGlasses ? 'tak' : 'nie'}`
  + `|csv=${title};${format.code}`;
```

**Po (`FormatCodes.ts`, nowy plik):**

```typescript
import { IllegalArgumentError } from '../../../../shared/errors.js';
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Format } from './Format.js';

/**
 * Mapper migracyjny liczba <-> Format. Baza i CSV zostają przy kodzie liczbowym;
 * gdy kiedyś przejdziemy na kody tekstowe, zmieni się tylko ta klasa.
 */
export class FormatCodes {
  private static readonly CODES: ReadonlyMap<Format, number> = new Map([
    [Format.TWO_D, 1],
    [Format.THREE_D, 2],
    [Format.IMAX, 3],
  ]);

  private constructor() {}

  static fromCode(code: number): Format {
    for (const [format, formatCode] of FormatCodes.CODES) {
      if (formatCode === code) {
        return format;
      }
    }
    throw new IllegalArgumentError(`unknown format code: ${code}`);
  }

  static toCode(format: Format): number {
    return requireNonNull(FormatCodes.CODES.get(format));
  }
}
```

**Po (`Format.ts`, stałe i konstruktor po usunięciu kodu; `fromCode` i import `IllegalArgumentError` znikają, `values()` i `toString()` zostają):**

```typescript
static readonly TWO_D = new Format('TWO_D', '2D', '25.00', false);
static readonly THREE_D = new Format('THREE_D', '3D', '32.00', true);
static readonly IMAX = new Format('IMAX', 'IMAX', '40.00', false);

readonly basePrice: Money;

private constructor(
  readonly name: 'TWO_D' | 'THREE_D' | 'IMAX',
  readonly label: string,
  basePrice: string,
  readonly requiresGlasses: boolean,
) {
  this.basePrice = Money.of(basePrice);
}
```

**Po (`ScreeningCsv.ts`, metoda `describe`):**

```typescript
const format = FormatCodes.fromCode(parseInteger(parts[1]!.trim()));
return `${title}|${format.label}|${format.basePrice.toString()}`
  + `|okulary:${format.requiresGlasses ? 'tak' : 'nie'}`
  + `|csv=${title};${FormatCodes.toCode(format)}`;
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

**Zmiany:**

1. W katalogu `tickets` utwórz klasę `Tickets` z prywatnym konstruktorem i metodami tworzącymi `static standard(title, base, row)` i `static vip(title, base, row)` - obie zwracają typ `Ticket`.
2. W `BoxOffice` zastąp każde `new VipTicket(...)`/`new StandardTicket(...)` wywołaniem metody tworzącej i usuń importy klas konkretnych (ręcznie - VS Code nie ma Replace Constructor with Factory Method; listę miejsc daje wyszukanie wszystkich odwołań do konstruktora). W miejsce usuniętych importów dodaj `import { Tickets } from './tickets/Tickets.js';`. Pliki `Ticket.ts`, `StandardTicket.ts` i `VipTicket.ts` bez zmian w kodzie (w snapshotach zmieniają się tylko komentarze TSDoc - tak w całej scenie).

**Po (`tickets/Tickets.ts`, nowy plik):**

```typescript
import type { Money } from '../../../../shared/Money.js';
import { StandardTicket } from './StandardTicket.js';
import type { Ticket } from './Ticket.js';
import { VipTicket } from './VipTicket.js';

/** Metody tworzące (Creation Method) obok klas biletów - zwracają typ Ticket. */
export class Tickets {
  private constructor() {}

  static standard(title: string, base: Money, row: number): Ticket {
    return new StandardTicket(title, base, row);
  }

  static vip(title: string, base: Money, row: number): Ticket {
    return new VipTicket(title, base, row);
  }
}
```

**Przed (`BoxOffice.ts`, metody `sell` i `sellAll`):**

```typescript
sell(title: string, base: Money, row: number): Ticket {
  if (row >= 10) {
    return new VipTicket(title, base, row);
  }
  return new StandardTicket(title, base, row);
}

sellAll(title: string, base: Money, rows: readonly number[]): Ticket[] {
  const tickets: Ticket[] = [];
  for (const row of rows) {
    tickets.push(row >= 10
      ? new VipTicket(title, base, row)
      : new StandardTicket(title, base, row));
  }
  return tickets;
}
```

**Po (`BoxOffice.ts`, metody `sell` i `sellAll`):**

```typescript
sell(title: string, base: Money, row: number): Ticket {
  if (row >= 10) {
    return Tickets.vip(title, base, row);
  }
  return Tickets.standard(title, base, row);
}

sellAll(title: string, base: Money, rows: readonly number[]): Ticket[] {
  const tickets: Ticket[] = [];
  for (const row of rows) {
    tickets.push(row >= 10 ? Tickets.vip(title, base, row) : Tickets.standard(title, base, row));
  }
  return tickets;
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s04` - zielone.
**Co powiedzieć:** przekierowujemy tworzenie pojedynczo; klient nie importuje już klas konkretnych.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s04 0 1`

### Krok 2: decyzja do fabryki

**Zmiany:**

1. W `Tickets` zastąp metody `standard` i `vip` jedną metodą `static forSeat(title, base, row)` z warunkiem `row >= 10` przeniesionym z klienta (Move Method, ręcznie).
2. Liczbę 10 wyodrębnij do stałej `private static readonly VIP_FROM_ROW` (Extract Constant).
3. W `BoxOffice` metoda `sell` tylko deleguje do `Tickets.forSeat(...)`, a `sellAll` woła `this.sell(...)` w pętli.

**Przed:** `Tickets.ts` i metody `BoxOffice` jak w blokach Po kroku 1.

**Po (`tickets/Tickets.ts`, cała klasa; importy bez zmian):**

```typescript
/** Move Method - decyzja "który bilet" należy do fabryki, nie do klienta. */
export class Tickets {
  private static readonly VIP_FROM_ROW = 10;

  private constructor() {}

  static forSeat(title: string, base: Money, row: number): Ticket {
    if (row >= Tickets.VIP_FROM_ROW) {
      return new VipTicket(title, base, row);
    }
    return new StandardTicket(title, base, row);
  }
}
```

**Po (`BoxOffice.ts`, metody `sell` i `sellAll`):**

```typescript
sell(title: string, base: Money, row: number): Ticket {
  return Tickets.forSeat(title, base, row);
}

sellAll(title: string, base: Money, rows: readonly number[]): Ticket[] {
  const tickets: Ticket[] = [];
  for (const row of rows) {
    tickets.push(this.sell(title, base, row));
  }
  return tickets;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** reguła "który bilet" jest teraz w jednym miejscu - to właściwy dom dla `if` o konstrukcji.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s04 1 2`

### Krok 3: ograniczenie widoczności

**Zmiany:**

1. Przenieś deklaracje klas `StandardTicket` i `VipTicket` do `tickets/Tickets.ts`, nad fabrykę (przeniesienie deklaracji do istniejącego pliku).
2. Usuń z obu klas słowo `export` - stają się prywatne dla modułu. Pliki `StandardTicket.ts` i `VipTicket.ts` znikają (po przeniesieniu są puste).
3. Uruchom `npm run typecheck` - podkreśli każde użycie klas spoza modułu; to inwentaryzacja, którą robi za nas kompilator. W `Tickets.ts` import `Money` przestaje być `import type` (ciała klas wołają `Money.of`), a importy `StandardTicket` i `VipTicket` znikają. Pola konstruktorów przeniesionych klas zmieniają się z `private readonly` na `readonly` (jak pola rekordu). `BoxOffice` i `Ticket.ts` bez zmian.

**Przed:** pliki `tickets/StandardTicket.ts` i `tickets/VipTicket.ts` (usuwane w tym kroku) oraz fabryka z kroku 2.

**Po (`tickets/Tickets.ts`, cały plik):**

```typescript
import { Money } from '../../../../shared/Money.js';
import type { Ticket } from './Ticket.js';

// Klasy konkretne są prywatne dla modułu (brak export) - poza tym plikiem nie da się
// ich utworzyć ani zaimportować. TypeScript nie ma widoczności pakietowej z Javy, więc
// StandardTicket i VipTicket przeniesione z osobnych plików tutaj, obok fabryki.

/** Klasa prywatna modułu - zwykłe miejsce (w Javie: rekord pakietowy). */
class StandardTicket implements Ticket {
  constructor(
    readonly title: string,
    readonly base: Money,
    readonly row: number,
  ) {}

  price(): Money {
    return this.base;
  }

  describe(): string {
    return `${this.title} r${this.row} ${this.price().toString()}`;
  }
}

/** Klasa prywatna modułu, miejsce VIP: +10.00 (w Javie: rekord pakietowy). */
class VipTicket implements Ticket {
  constructor(
    readonly title: string,
    readonly base: Money,
    readonly row: number,
  ) {}

  price(): Money {
    return this.base.plus(Money.of('10.00'));
  }

  describe(): string {
    return `${this.title} r${this.row} VIP ${this.price().toString()}`;
  }
}

/** Fabryka jest jedynym publicznym wejściem do tworzenia biletów. */
export class Tickets {
  private static readonly VIP_FROM_ROW = 10;

  private constructor() {}

  static forSeat(title: string, base: Money, row: number): Ticket {
    if (row >= Tickets.VIP_FROM_ROW) {
      return new VipTicket(title, base, row);
    }
    return new StandardTicket(title, base, row);
  }
}
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

**Zmiany:**

1. W `reserve` wyodrębnij do prywatnej metody `newReservation(channel, email, seats)` (Extract Method) cały kod tworzenia: od `this.sequence++` przez walidację kanału, `fee` i `expiresAt`, po `new Reservation(...)`; oznaczanie miejsc (`seats.forEach(...)`) zostaje w `reserve`.
2. Pętlę sprawdzającą zajętość miejsc wyodrębnij tak samo do prywatnej metody `requireFree(seats)`.
3. W `reserveGroup` zastąp skopiowany kod wywołaniami `this.requireFree(seats)` i `this.newReservation('ONLINE', email, seats)`.

**Przed (`ReservationService.ts`, metody `reserve` i `reserveGroup`):**

```typescript
reserve(channel: string, email: string, seats: readonly string[]): Reservation {
  for (const seat of seats) {
    if (this.takenSeats.has(seat)) {
      throw new IllegalStateError(`seat taken: ${seat}`);
    }
  }
  this.sequence++;
  const id = `R${this.sequence}`;
  if (channel !== 'ONLINE' && channel !== 'BOX_OFFICE') {
    throw new IllegalArgumentError(`unknown channel: ${channel}`);
  }
  const fee = channel === 'ONLINE' ? Money.of('2.00').times(seats.length) : Money.ZERO;
  const expiresAt = channel === 'ONLINE'
    ? LocalDateTime.now(this.clock).plusMinutes(15)
    : null;
  seats.forEach((seat) => this.takenSeats.add(seat));
  return new Reservation(id, channel, email, Object.freeze([...seats]), fee, expiresAt);
}

reserveGroup(email: string, seats: readonly string[]): Reservation {
  if (seats.length < 10) {
    throw new IllegalArgumentError('group needs 10+ seats');
  }
  for (const seat of seats) {
    if (this.takenSeats.has(seat)) {
      throw new IllegalStateError(`seat taken: ${seat}`);
    }
  }
  this.sequence++;
  const id = `R${this.sequence}`;
  const fee = Money.of('2.00').times(seats.length);
  const expiresAt = LocalDateTime.now(this.clock).plusMinutes(15);
  seats.forEach((seat) => this.takenSeats.add(seat));
  return new Reservation(id, 'ONLINE', email, Object.freeze([...seats]), fee, expiresAt);
}
```

**Po (`ReservationService.ts`, wszystkie cztery metody; pola i konstruktor bez zmian):**

```typescript
reserve(channel: string, email: string, seats: readonly string[]): Reservation {
  this.requireFree(seats);
  const reservation = this.newReservation(channel, email, seats);
  seats.forEach((seat) => this.takenSeats.add(seat));
  return reservation;
}

reserveGroup(email: string, seats: readonly string[]): Reservation {
  if (seats.length < 10) {
    throw new IllegalArgumentError('group needs 10+ seats');
  }
  this.requireFree(seats);
  const reservation = this.newReservation('ONLINE', email, seats);
  seats.forEach((seat) => this.takenSeats.add(seat));
  return reservation;
}

private requireFree(seats: readonly string[]): void {
  for (const seat of seats) {
    if (this.takenSeats.has(seat)) {
      throw new IllegalStateError(`seat taken: ${seat}`);
    }
  }
}

private newReservation(channel: string, email: string, seats: readonly string[]): Reservation {
  this.sequence++;
  const id = `R${this.sequence}`;
  if (channel !== 'ONLINE' && channel !== 'BOX_OFFICE') {
    throw new IllegalArgumentError(`unknown channel: ${channel}`);
  }
  const fee = channel === 'ONLINE' ? Money.of('2.00').times(seats.length) : Money.ZERO;
  const expiresAt = channel === 'ONLINE'
    ? LocalDateTime.now(this.clock).plusMinutes(15)
    : null;
  return new Reservation(id, channel, email, Object.freeze([...seats]), fee, expiresAt);
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s05` - zielone.
**Co powiedzieć:** kolejność efektów zostaje: najpierw sprawdzenie miejsc, potem numer, potem walidacja kanału. Gdybyśmy przy okazji "poprawili" kolejność, test to wychwyci.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s05 0 1`

### Krok 2: Extract Class - ReservationFactory

**Zmiany:**

1. Utwórz klasę `ReservationFactory` i przenieś do niej metodę `newReservation` pod nazwą `create(channel, email, seats)` razem z polami `clock` i `sequence` (wyodrębnienie klasy robimy ręcznie - VS Code nie ma Extract Delegate).
2. W serwisie usuń pola `clock` i `sequence`; zostaje pole `factory`. Konstruktor nadal przyjmuje `Clock` i sam tworzy fabrykę, więc jego sygnatura się nie zmienia.
3. `reserve` i `reserveGroup` wołają `this.factory.create(...)` zamiast `this.newReservation(...)` - reszta ich ciał bez zmian.
4. Popraw importy serwisu: `Money`, `LocalDateTime` i `Reservation` (klasa) nie są już potrzebne - zostają `IllegalArgumentError`, `IllegalStateError`, `import type { Clock }`, `import type { Reservation }` oraz nowy `import { ReservationFactory } from './ReservationFactory.js';`.

**Przed:** metoda `newReservation` i konstruktor `constructor(private readonly clock: Clock) {}` - jak w bloku Po kroku 1.

**Po (`ReservationFactory.ts`, nowy plik):**

```typescript
import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import { type Clock, LocalDateTime } from '../../../shared/time.js';
import { Reservation } from '../Reservation.js';

/**
 * Extract Factory Class - cała wiedza o budowie rezerwacji (numeracja, opłata,
 * termin ważności) w jednej klasie. Pole sequence i zegar przeniesione razem z metodą.
 */
export class ReservationFactory {
  private sequence = 0;

  constructor(private readonly clock: Clock) {}

  create(channel: string, email: string, seats: readonly string[]): Reservation {
    this.sequence++;
    const id = `R${this.sequence}`;
    if (channel !== 'ONLINE' && channel !== 'BOX_OFFICE') {
      throw new IllegalArgumentError(`unknown channel: ${channel}`);
    }
    const fee = channel === 'ONLINE' ? Money.of('2.00').times(seats.length) : Money.ZERO;
    const expiresAt = channel === 'ONLINE'
      ? LocalDateTime.now(this.clock).plusMinutes(15)
      : null;
    return new Reservation(id, channel, email, Object.freeze([...seats]), fee, expiresAt);
  }
}
```

**Po (`ReservationService.ts`, pola i konstruktor):**

```typescript
export class ReservationService {
  private readonly factory: ReservationFactory;
  private readonly takenSeats = new Set<string>();

  constructor(clock: Clock) {
    this.factory = new ReservationFactory(clock);
  }
  // reserve i reserveGroup: this.factory.create(...) zamiast this.newReservation(...);
  // requireFree bez zmian; newReservation usunięta
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** klienci serwisu niczego nie zauważyli. Serwis ma jedną odpowiedzialność: dostępność miejsc.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s05 1 2`

### Krok 3: fabryka jako zależność

**Zmiany:**

1. Zmień parametr konstruktora serwisu na `factoryOrClock: ReservationFactory | Clock` ze sprawdzeniem `requireNonNull`.
2. Gdy argument jest fabryką (`instanceof ReservationFactory`), zapisz go w polu; gdy zegarem - utwórz fabrykę jak dotąd. Dotychczasowi klienci z zegarem działają bez zmian, a test może wstrzyknąć własną fabrykę. Dodaj import `requireNonNull` z `shared/requireNonNull.js`. Reszta serwisu i `ReservationFactory` bez zmian w kodzie.

**Przed:**

```typescript
constructor(clock: Clock) {
  this.factory = new ReservationFactory(clock);
}
```

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

**Zmiany:**

1. Utwórz klasę `ScheduleBuilder`: konstruktor z datą tworzy `DaySchedule`, a pole `current` pamięta bieżącą salę.
2. Dodaj metody: `hall(name)` tworzy `Hall`, dodaje go do dnia i ustawia jako bieżący; `screening(title, hour, minute)` dodaje seans do bieżącej sali, a bez sali rzuca `IllegalStateError('screening without hall')`. Obie zwracają `this`; `build()` zwraca `DaySchedule`.
3. Przepisz `WeekendPlanner.plan` na łańcuch wywołań buildera; seanse weekendowe dodawaj warunkowo przez `builder.screening(...)`. Importy planera: zostaje `import type { LocalDate }` i `import type { DaySchedule }`, dochodzi `ScheduleBuilder`; `LocalTime`, `Hall` i `Screening` znikają. Drzewo (`DaySchedule`, `Hall`, `Screening`) bez zmian w kodzie (tylko komentarze TSDoc).

**Po (`ScheduleBuilder.ts`, nowy plik):**

```typescript
import { IllegalStateError } from '../../../../shared/errors.js';
import { type LocalDate, LocalTime } from '../../../shared/time.js';
import { DaySchedule } from './DaySchedule.js';
import { Hall } from './Hall.js';
import { Screening } from './Screening.js';

/**
 * Klasyczny Builder (Encapsulate Composite with Builder) - pamięta bieżącą salę,
 * więc klient nie operuje węzłami ani add. Drzewo pod spodem bez zmian.
 */
export class ScheduleBuilder {
  private readonly day: DaySchedule;
  private current: Hall | null = null;

  constructor(date: LocalDate) {
    this.day = new DaySchedule(date);
  }

  hall(name: string): this {
    this.current = new Hall(name);
    this.day.add(this.current);
    return this;
  }

  screening(title: string, hour: number, minute: number): this {
    if (this.current === null) {
      throw new IllegalStateError('screening without hall');
    }
    this.current.add(new Screening(title, LocalTime.of(hour, minute)));
    return this;
  }

  build(): DaySchedule {
    return this.day;
  }
}
```

**Przed (`WeekendPlanner.ts`, metoda `plan`):**

```typescript
plan(date: LocalDate): DaySchedule {
  const dayOfWeek = date.dayOfWeek;
  const weekend = dayOfWeek === 'SATURDAY' || dayOfWeek === 'SUNDAY';
  const day = new DaySchedule(date);
  const hall1 = new Hall('Sala 1');
  hall1.add(new Screening('Diuna', LocalTime.of(18, 0)));
  hall1.add(new Screening('Diuna', LocalTime.of(21, 0)));
  day.add(hall1);
  const hall2 = new Hall('Sala 2');
  if (weekend) {
    hall2.add(new Screening('Kraina Lodu', LocalTime.of(10, 0)));
  }
  hall2.add(new Screening('Amator', LocalTime.of(17, 30)));
  day.add(hall2);
  const hall3 = new Hall('Sala 3 VIP');
  if (weekend) {
    hall3.add(new Screening('Amator', LocalTime.of(20, 0)));
  }
  day.add(hall3);
  return day;
}
```

**Po (`WeekendPlanner.ts`, metoda `plan`):**

```typescript
plan(date: LocalDate): DaySchedule {
  const dayOfWeek = date.dayOfWeek;
  const weekend = dayOfWeek === 'SATURDAY' || dayOfWeek === 'SUNDAY';
  const builder = new ScheduleBuilder(date)
    .hall('Sala 1')
    .screening('Diuna', 18, 0)
    .screening('Diuna', 21, 0)
    .hall('Sala 2');
  if (weekend) {
    builder.screening('Kraina Lodu', 10, 0);
  }
  builder.screening('Amator', 17, 30)
    .hall('Sala 3 VIP');
  if (weekend) {
    builder.screening('Amator', 20, 0);
  }
  return builder.build();
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s06` - zielone (sobota i piątek z pustą salą VIP).
**Co powiedzieć:** klient mówi "co" zbudować. Seans przed salą to teraz jawny błąd (`screening without hall`).
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s06 0 1`

### Krok 2: niemutowalne drzewo, builder jednorazowy

**Zmiany:**

1. Zamień węzły `Hall` i `DaySchedule` na niezmienne klasy: pola `readonly`, lista dzieci przekazywana w konstruktorze i kopiowana przez `Object.freeze([...])`; metody `add` znikają.
2. Builder zbiera dane w `Map<string, Screening[]>` (zachowuje kolejność wstawiania); `hall(name)` odrzuca powtórzoną nazwę (`duplicate hall: ...`), a węzły powstają dopiero w `build()`.
3. Dodaj flagę `built` i prywatne `requireNotBuilt()` wołane w każdej metodzie - drugie `build()` rzuca `IllegalStateError('builder already used')`.
4. W `Screening` zmień pola konstruktora z `private readonly` na `readonly` (builder i `DaySchedule.render` czytają je z zewnątrz). W `DaySchedule.render` zlicz seanse przez `hall.screenings.length` zamiast `hall.size()`; metody `render` poza tym bez zmian. `WeekendPlanner` bez zmian.

**Przed (`Hall.ts` i `DaySchedule.ts`, części mutowalne):**

```typescript
export class Hall {
  private readonly screenings: Screening[] = [];

  constructor(private readonly name: string) {}

  add(screening: Screening): void {
    this.screenings.push(screening);
  }

  size(): number {
    return this.screenings.length;
  }
  // render() bez zmian
}

export class DaySchedule {
  private readonly halls: Hall[] = [];

  constructor(private readonly date: LocalDate) {}

  add(hall: Hall): void {
    this.halls.push(hall);
  }
  // render() bez zmian
}
```

**Po (`Hall.ts` i `DaySchedule.ts`):**

```typescript
export class Hall {
  readonly screenings: readonly Screening[];

  constructor(readonly name: string, screenings: readonly Screening[]) {
    this.screenings = Object.freeze([...screenings]);
  }
  // render() bez zmian
}

export class DaySchedule {
  readonly halls: readonly Hall[];

  constructor(readonly date: LocalDate, halls: readonly Hall[]) {
    this.halls = Object.freeze([...halls]);
  }

  render(): string {
    let text = `${this.date.toString()} ${this.date.dayOfWeek}\n`;
    let count = 0;
    for (const hall of this.halls) {
      text += hall.render();
      count += hall.screenings.length;
    }
    return `${text}Seansow: ${count}\n`;
  }
}
```

**Po (`ScheduleBuilder.ts`, cała klasa; importy bez zmian):**

```typescript
/**
 * Builder zbiera dane, a drzewo (niezmienne obiekty) powstaje w build(). Builder jest
 * jednorazowy - drugie build() rzuca wyjątek, bo wynik nie może zależeć od późniejszych wywołań.
 */
export class ScheduleBuilder {
  // Map zachowuje kolejność wstawiania (w Javie: LinkedHashMap).
  private readonly halls = new Map<string, Screening[]>();
  private current: Screening[] | null = null;
  private built = false;

  constructor(private readonly date: LocalDate) {}

  hall(name: string): this {
    this.requireNotBuilt();
    if (this.halls.has(name)) {
      throw new IllegalStateError(`duplicate hall: ${name}`);
    }
    this.current = [];
    this.halls.set(name, this.current);
    return this;
  }

  screening(title: string, hour: number, minute: number): this {
    this.requireNotBuilt();
    if (this.current === null) {
      throw new IllegalStateError('screening without hall');
    }
    this.current.push(new Screening(title, LocalTime.of(hour, minute)));
    return this;
  }

  build(): DaySchedule {
    this.requireNotBuilt();
    this.built = true;
    const result: Hall[] = [];
    this.halls.forEach((screenings, name) => result.push(new Hall(name, screenings)));
    return new DaySchedule(this.date, result);
  }

  private requireNotBuilt(): void {
    if (this.built) {
      throw new IllegalStateError('builder already used');
    }
  }
}
```

**Uruchom:** test zielony; `S06SolutionTest` sprawdza jednorazowość i niemutowalność.
**Co powiedzieć:** gdy builder jest jedyną drogą budowy, drzewo może stać się niemutowalne. `readonly` w typie chroni tylko w czasie kompilacji, dopiero `Object.freeze` zatrzymuje `push` w runtime. Unikalność nazw sal to **nowy niezmiennik** - start pozwalał na duplikaty. Taką regułę zatwierdzamy świadomie, bo zmienia zachowanie dla błędnych danych.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s06 1 2`

### Krok 3: zagnieżdżony builder z funkcją strzałkową

**Zmiany:**

1. Dodaj w pliku buildera klasę `HallBuilder` z metodą `screening(title, hour, minute)`, dopisującą seans do listy przekazanej w konstruktorze.
2. Zmień `hall` na `hall(name, content: (hall: HallBuilder) => void)`: metoda tworzy listę seansów, woła funkcję z nowym `HallBuilder` i od razu buduje `Hall` - ukryty stan "bieżąca sala" znika.
3. Zrób konstruktor `ScheduleBuilder` prywatnym; wejściem jest statyczne `ScheduleBuilder.day(date)`. Metoda `screening` i pole `current` znikają z `ScheduleBuilder`; sale zbiera zwykła tablica `private readonly halls: Hall[] = []`, a `build()` zwraca `new DaySchedule(this.date, this.halls)`. Przepisz planera na zagnieżdżone wywołania z funkcją strzałkową dla każdej sali. Drzewo bez zmian.

**Przed:** `ScheduleBuilder.ts` i metoda `plan` jak w blokach Po kroku 2 i kroku 1.

**Po (`ScheduleBuilder.ts`, obie klasy; importy bez zmian):**

```typescript
/**
 * Builder jednej sali - dostaje go lambda przekazana do ScheduleBuilder.hall.
 * (W Javie: zagnieżdżona klasa ScheduleBuilder.HallBuilder z prywatnym konstruktorem;
 * tu klasa obok, zapisująca seanse do listy należącej do ScheduleBuilder.)
 */
export class HallBuilder {
  constructor(private readonly screenings: Screening[]) {}

  screening(title: string, hour: number, minute: number): this {
    this.screenings.push(new Screening(title, LocalTime.of(hour, minute)));
    return this;
  }
}

/**
 * Zagnieżdżony builder - sala konfigurowana lambdą, więc znika ukryty stan
 * "bieżąca sala". Lambda jest wywoływana synchronicznie, dokładnie raz.
 */
export class ScheduleBuilder {
  private readonly halls: Hall[] = [];
  private built = false;

  private constructor(private readonly date: LocalDate) {}

  static day(date: LocalDate): ScheduleBuilder {
    return new ScheduleBuilder(date);
  }

  hall(name: string, content: (hall: HallBuilder) => void): this {
    this.requireNotBuilt();
    if (this.halls.some((hall) => hall.name === name)) {
      throw new IllegalStateError(`duplicate hall: ${name}`);
    }
    const screenings: Screening[] = [];
    content(new HallBuilder(screenings));
    this.halls.push(new Hall(name, screenings));
    return this;
  }

  build(): DaySchedule {
    this.requireNotBuilt();
    this.built = true;
    return new DaySchedule(this.date, this.halls);
  }

  private requireNotBuilt(): void {
    if (this.built) {
      throw new IllegalStateError('builder already used');
    }
  }
}
```

**Po (`WeekendPlanner.ts`, metoda `plan`):**

```typescript
plan(date: LocalDate): DaySchedule {
  const dayOfWeek = date.dayOfWeek;
  const weekend = dayOfWeek === 'SATURDAY' || dayOfWeek === 'SUNDAY';
  return ScheduleBuilder.day(date)
    .hall('Sala 1', (hall) => hall
      .screening('Diuna', 18, 0)
      .screening('Diuna', 21, 0))
    .hall('Sala 2', (hall) => {
      if (weekend) {
        hall.screening('Kraina Lodu', 10, 0);
      }
      hall.screening('Amator', 17, 30);
    })
    .hall('Sala 3 VIP', (hall) => {
      if (weekend) {
        hall.screening('Amator', 20, 0);
      }
    })
    .build();
}
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

**Zmiany:**

1. Utwórz nowy plik `PricedTicket.ts` z interfejsem `PricedTicket` o metodach `price(): Money` i `description(): string` (Extract Interface robimy ręcznie).
2. Dopisz `implements PricedTicket` do klasy `Ticket` (plus `import type { PricedTicket } from './PricedTicket.js';`); reszta klasy bez zmian.
3. Zmień typ wyniku `TicketAssembler.assemble` z `Ticket` na `PricedTicket` (ten sam import) - fabryka będzie mogła zwrócić udekorowany obiekt.

**Po (`PricedTicket.ts`, nowy plik):**

```typescript
import type { Money } from '../../../shared/Money.js';

/** Wąski kontrakt wspólny dla rdzenia i przyszłych dekoratorów. */
export interface PricedTicket {
  price(): Money;

  description(): string;
}
```

**Przed (`TicketAssembler.ts`):**

```typescript
export class TicketAssembler {
  assemble(order: TicketOrder): Ticket {
    return new Ticket(order);
  }
}
```

**Po (`TicketAssembler.ts` i nagłówek `Ticket.ts`):**

```typescript
export class TicketAssembler {
  assemble(order: TicketOrder): PricedTicket {
    return new Ticket(order);
  }
}
```

```typescript
export class Ticket implements PricedTicket {
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s07` - zielone.
**Co powiedzieć:** wąski kontrakt, który spełnią i rdzeń, i dekoratory. Składanie w jednym miejscu (assembler).
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s07 0 1`

### Krok 2: pierwszy dodatek jako dekorator

**Zmiany:**

1. Utwórz klasę `Insurance implements PricedTicket` z polem `readonly inner: PricedTicket`: `price()` dodaje 4.00 do ceny środka, `description()` dopisuje " +ubezpieczenie" do opisu środka.
2. Usuń z `Ticket` pole `insurance` (deklarację i przypisanie `this.insurance = order.insurance;` w konstruktorze) oraz jego gałęzie w `price()` i `description()` (ręcznie - `npm run typecheck` pokaże resztę użyć).
3. W `TicketAssembler.assemble` owiń rdzeń dekoratorem, gdy `order.insurance` (dodaj import `Insurance`).

**Po (`Insurance.ts`, nowy plik):**

```typescript
import { Money } from '../../../shared/Money.js';
import type { PricedTicket } from './PricedTicket.js';

/**
 * Pierwszy dodatek jako dekorator. Zaczynamy od ubezpieczenia, bo w opisie jest
 * ostatnie - dekorator dopisuje się na końcu opisu obiektu, który owija.
 * (W Javie: rekord - niezmienny obiekt wartości.)
 */
export class Insurance implements PricedTicket {
  constructor(readonly inner: PricedTicket) {}

  price(): Money {
    return this.inner.price().plus(Money.of('4.00'));
  }

  description(): string {
    return `${this.inner.description()} +ubezpieczenie`;
  }
}
```

**Przed (`Ticket.ts`, metody `price` i `description`):**

```typescript
price(): Money {
  let price = this.base;
  if (this.vip) {
    price = price.plus(Money.of('10.00'));
  }
  if (this.glasses) {
    price = price.plus(Money.of('3.00'));
  }
  if (this.insurance) {
    price = price.plus(Money.of('4.00'));
  }
  return price;
}

description(): string {
  return `${this.title} ${this.format}`
    + (this.vip ? ' +VIP' : '')
    + (this.glasses ? ' +okulary 3D' : '')
    + (this.insurance ? ' +ubezpieczenie' : '');
}
```

**Po (`Ticket.ts`, metody `price` i `description`):**

```typescript
price(): Money {
  let price = this.base;
  if (this.vip) {
    price = price.plus(Money.of('10.00'));
  }
  if (this.glasses) {
    price = price.plus(Money.of('3.00'));
  }
  return price;
}

description(): string {
  return `${this.title} ${this.format}`
    + (this.vip ? ' +VIP' : '')
    + (this.glasses ? ' +okulary 3D' : '');
}
```

**Po (`TicketAssembler.ts`, metoda `assemble`):**

```typescript
assemble(order: TicketOrder): PricedTicket {
  let ticket: PricedTicket = new Ticket(order);
  if (order.insurance) {
    ticket = new Insurance(ticket);
  }
  return ticket;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** zaczynamy od ubezpieczenia, bo w opisie jest **ostatnie** - dekorator dopisuje się na końcu. Gdybyśmy zaczęli od VIP, opis zmieniłby kolejność i test "wszystkie dodatki" by to złapał. Kolejność dekoratorów jest kontraktem.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s07 1 2`

### Krok 3: pozostałe dodatki, czysty rdzeń

**Zmiany:**

1. Utwórz dekoratory `VipSeat` (+10.00, " +VIP") i `Glasses3D` (+3.00, " +okulary 3D") na wzór `Insurance`.
2. Odchudź rdzeń: `Ticket` staje się niezmienną klasą `Ticket(title, format, base)` bez flag; `price()` zwraca `base`, `description()` tylko tytuł i format.
3. W assemblerze owijaj w ustalonej kolejności: rdzeń, VIP, okulary, ubezpieczenie (dodaj importy `VipSeat` i `Glasses3D`). Decyzja "czy okulary potrzebne" (format 3D i brak własnych) zostaje w assemblerze. `Insurance` i `PricedTicket` bez zmian.

**Przed (`Ticket.ts`, pola, konstruktor i metody z kroku 2):**

```typescript
export class Ticket implements PricedTicket {
  private readonly title: string;
  private readonly format: string;
  private readonly base: Money;
  private readonly vip: boolean;
  private readonly glasses: boolean;

  constructor(order: TicketOrder) {
    this.title = order.title;
    this.format = order.format;
    this.base = order.base;
    this.vip = order.vip;
    this.glasses = order.format === '3D' && !order.ownGlasses;
  }
  // price() i description() z gałęziami vip i glasses - jak w bloku Po kroku 2
}
```

**Po (`VipSeat.ts`, nowy plik):**

```typescript
import { Money } from '../../../shared/Money.js';
import type { PricedTicket } from './PricedTicket.js';

/** Dopłata za miejsce VIP jako dekorator. */
export class VipSeat implements PricedTicket {
  constructor(readonly inner: PricedTicket) {}

  price(): Money {
    return this.inner.price().plus(Money.of('10.00'));
  }

  description(): string {
    return `${this.inner.description()} +VIP`;
  }
}
```

**Po (`Glasses3D.ts`, nowy plik):**

```typescript
import { Money } from '../../../shared/Money.js';
import type { PricedTicket } from './PricedTicket.js';

/** Okulary 3D jako dekorator; decyzja "czy potrzebne" została w fabryce. */
export class Glasses3D implements PricedTicket {
  constructor(readonly inner: PricedTicket) {}

  price(): Money {
    return this.inner.price().plus(Money.of('3.00'));
  }

  description(): string {
    return `${this.inner.description()} +okulary 3D`;
  }
}
```

**Po (`Ticket.ts`, cały plik):**

```typescript
import type { Money } from '../../../shared/Money.js';
import type { PricedTicket } from './PricedTicket.js';

/** Czysty rdzeń - tylko to, co ma każdy bilet. Bez flag. (W Javie: rekord.) */
export class Ticket implements PricedTicket {
  constructor(
    readonly title: string,
    readonly format: string,
    readonly base: Money,
  ) {}

  price(): Money {
    return this.base;
  }

  description(): string {
    return `${this.title} ${this.format}`;
  }
}
```

**Po (`TicketAssembler.ts`, metoda `assemble`):**

```typescript
assemble(order: TicketOrder): PricedTicket {
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
  return ticket;
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

**Zmiany:**

1. W pliku `Reservation.ts` dodaj prywatny dla modułu (bez `export`) interfejs `ReservationState` z jednym polem `readonly status: Status`.
2. Dodaj w `Reservation` stany jako stałe `private static readonly NEW`, `PAID`, `USED`, `EXPIRED`, `CANCELLED` - na razie literały obiektów, np. `{ status: Status.NEW }`.
3. Pole `currentStatus` (w Javie `status` - tu nazwa jest zajęta przez metodę `status()` z kontraktu `ReservationActions`) zastąp polem `state = Reservation.NEW`; warunki porównują `this.state.status`.
4. Wyodrębnij obciążenie bramki, zmianę stanu i zapis efektu do prywatnych metod `charge()`, `moveTo(next)`, `record(effect)` (Extract Method). Metoda `status()` zwraca `this.state.status`; `effects()`, konstruktor i importy bez zmian.

**Przed (start, metody `pay`, `use`, `expire`, `status`; `cancel` wyżej w "Co widzimy"):**

```typescript
private currentStatus = Status.NEW;

pay(): void {
  if (this.currentStatus !== Status.NEW) {
    throw new IllegalStateError(`cannot pay in ${this.currentStatus}`);
  }
  this.payments.charge(this.id);
  this.currentStatus = Status.PAID;
  this.effectList.push('charged');
}

use(): void {
  if (this.currentStatus !== Status.PAID) {
    throw new IllegalStateError(`cannot use in ${this.currentStatus}`);
  }
  this.currentStatus = Status.USED;
  this.effectList.push('gate opened');
}

expire(): void {
  if (this.currentStatus !== Status.NEW) {
    throw new IllegalStateError(`cannot expire in ${this.currentStatus}`);
  }
  this.currentStatus = Status.EXPIRED;
  this.effectList.push('seats released');
}

status(): Status {
  return this.currentStatus;
}
```

**Po (`Reservation.ts` - interfejs nad klasą i cała klasa poza `effects()` i konstruktorem):**

```typescript
/** Obiekt stanu - na razie zna tylko swój Status. */
interface ReservationState {
  readonly status: Status;
}

export class Reservation implements ReservationActions {
  private static readonly NEW: ReservationState = { status: Status.NEW };
  private static readonly PAID: ReservationState = { status: Status.PAID };
  // Stany końcowe - żadne przejście nie jest dozwolone.
  private static readonly USED: ReservationState = { status: Status.USED };
  private static readonly EXPIRED: ReservationState = { status: Status.EXPIRED };
  private static readonly CANCELLED: ReservationState = { status: Status.CANCELLED };

  private readonly effectList: string[] = [];
  private state = Reservation.NEW;

  constructor(private readonly id: string, private readonly payments: Payments) {}

  pay(): void {
    if (this.state.status !== Status.NEW) {
      throw new IllegalStateError(`cannot pay in ${this.state.status}`);
    }
    this.charge();
    this.moveTo(Reservation.PAID);
    this.record('charged');
  }

  use(): void {
    if (this.state.status !== Status.PAID) {
      throw new IllegalStateError(`cannot use in ${this.state.status}`);
    }
    this.moveTo(Reservation.USED);
    this.record('gate opened');
  }

  expire(): void {
    if (this.state.status !== Status.NEW) {
      throw new IllegalStateError(`cannot expire in ${this.state.status}`);
    }
    this.moveTo(Reservation.EXPIRED);
    this.record('seats released');
  }

  cancel(): void {
    if (this.state.status === Status.NEW) {
      this.moveTo(Reservation.CANCELLED);
      this.record('seats released');
    } else if (this.state.status === Status.PAID) {
      this.moveTo(Reservation.CANCELLED);
      this.record('refund');
      this.record('seats released');
    } else {
      throw new IllegalStateError(`cannot cancel in ${this.state.status}`);
    }
  }

  status(): Status {
    return this.state.status;
  }

  // effects() bez zmian

  private charge(): void {
    this.payments.charge(this.id);
  }

  private moveTo(next: ReservationState): void {
    this.state = next;
  }

  private record(effect: string): void {
    this.effectList.push(effect);
  }
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s08` - 84 testy zielone.
**Co powiedzieć:** stany końcowe USED, EXPIRED i CANCELLED zachowują się tak samo (każde przejście jest błędem) - różnią się tylko statusem.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s08 0 1`

### Krok 2: pay i use do stanów

**Zmiany:**

1. Dodaj typ `Transition = (reservation: Reservation) => void` i rozszerz interfejs stanu o przejścia `pay` i `use` typu `Transition`.
2. Napisz funkcję `state(status, allowed)`, która dla każdego przejścia wstawia domyślnie funkcję rzucającą `cannot <akcja> in <status>`, a potem nakłada dozwolone (`...allowed`).
3. Twórz stany przez `state(...)`: w `NEW` podaj `pay`, w `PAID` - `use`, przenosząc ciała gałęzi z metod kontekstu (kolejność: obciążenie, zmiana stanu, efekt). Stany końcowe to `state(Status.USED)` itd. bez przejść.
4. Metody kontekstu `pay` i `use` tylko delegują: `this.state.pay(this)`, `this.state.use(this)`. Metody `expire` i `cancel` zostają na razie bez zmian.

**Przed:** interfejs `ReservationState`, stałe stanów i metody `pay`/`use` - jak w bloku Po kroku 1.

**Po (góra pliku - typ, interfejs i funkcja `state`):**

```typescript
type Transition = (reservation: Reservation) => void;

interface ReservationState {
  readonly status: Status;
  readonly pay: Transition;
  readonly use: Transition;
}

/** Domyślne przejście rzuca wyjątek (nie jest puste!) - stan podaje tylko dozwolone przejścia. */
function state(status: Status, allowed: Partial<Omit<ReservationState, 'status'>> = {}): ReservationState {
  const invalid = (action: string): Transition => () => {
    throw new IllegalStateError(`cannot ${action} in ${status}`);
  };
  return { status, pay: invalid('pay'), use: invalid('use'), ...allowed };
}
```

**Po (stałe stanów w `Reservation`):**

```typescript
private static readonly NEW: ReservationState = state(Status.NEW, {
  pay: (reservation) => {
    reservation.charge();
    reservation.moveTo(Reservation.PAID);
    reservation.record('charged');
  },
});

private static readonly PAID: ReservationState = state(Status.PAID, {
  use: (reservation) => {
    reservation.moveTo(Reservation.USED);
    reservation.record('gate opened');
  },
});

// Stany końcowe - żadne przejście nie jest dozwolone.
private static readonly USED = state(Status.USED);
private static readonly EXPIRED = state(Status.EXPIRED);
private static readonly CANCELLED = state(Status.CANCELLED);
```

**Po (metody kontekstu):**

```typescript
pay(): void {
  this.state.pay(this);
}

use(): void {
  this.state.use(this);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** domyślne przejście **rzuca wyjątek**, nie jest puste - pusta funkcja po cichu zmieniłaby zachowanie niedozwolonych przejść. Kolejność "obciążenie - zmiana stanu - efekt" zostaje dokładnie jak w start; test awarii bramki to pilnuje.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s08 1 2`

### Krok 3: expire i cancel do stanów

**Zmiany:**

1. Dodaj do interfejsu stanu przejścia `expire` i `cancel`, a w funkcji `state` ich domyślne warianty rzucające wyjątek.
2. Przenieś ciała gałęzi do stanów: `expire` i `cancel` do `NEW` (zwolnienie miejsc), `cancel` do `PAID` (zwrot i zwolnienie) - warunek `if/else if` z `cancel` rozkłada się na dwa stany.
3. Metody kontekstu `expire` i `cancel` tylko delegują do `this.state`.

**Przed (metody kontekstu z kroku 2):**

```typescript
expire(): void {
  if (this.state.status !== Status.NEW) {
    throw new IllegalStateError(`cannot expire in ${this.state.status}`);
  }
  this.moveTo(Reservation.EXPIRED);
  this.record('seats released');
}

cancel(): void {
  if (this.state.status === Status.NEW) {
    this.moveTo(Reservation.CANCELLED);
    this.record('seats released');
  } else if (this.state.status === Status.PAID) {
    this.moveTo(Reservation.CANCELLED);
    this.record('refund');
    this.record('seats released');
  } else {
    throw new IllegalStateError(`cannot cancel in ${this.state.status}`);
  }
}
```

**Po (interfejs i funkcja `state`):**

```typescript
interface ReservationState {
  readonly status: Status;
  readonly pay: Transition;
  readonly use: Transition;
  readonly expire: Transition;
  readonly cancel: Transition;
}

function state(status: Status, allowed: Partial<Omit<ReservationState, 'status'>> = {}): ReservationState {
  const invalid = (action: string): Transition => () => {
    throw new IllegalStateError(`cannot ${action} in ${status}`);
  };
  return {
    status,
    pay: invalid('pay'),
    use: invalid('use'),
    expire: invalid('expire'),
    cancel: invalid('cancel'),
    ...allowed,
  };
}
```

**Po (stany `NEW` i `PAID`; stany końcowe bez zmian):**

```typescript
private static readonly NEW: ReservationState = state(Status.NEW, {
  pay: (reservation) => {
    reservation.charge();
    reservation.moveTo(Reservation.PAID);
    reservation.record('charged');
  },
  expire: (reservation) => {
    reservation.moveTo(Reservation.EXPIRED);
    reservation.record('seats released');
  },
  cancel: (reservation) => {
    reservation.moveTo(Reservation.CANCELLED);
    reservation.record('seats released');
  },
});

private static readonly PAID: ReservationState = state(Status.PAID, {
  use: (reservation) => {
    reservation.moveTo(Reservation.USED);
    reservation.record('gate opened');
  },
  cancel: (reservation) => {
    reservation.moveTo(Reservation.CANCELLED);
    reservation.record('refund');
    reservation.record('seats released');
  },
});
```

**Po (metody kontekstu):**

```typescript
expire(): void {
  this.state.expire(this);
}

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

**Zmiany:**

1. Utwórz niezmienną klasę zdarzenia `ReservationPaid` z polami `readonly`: `reservationId`, `email`, `phone`, `amount`.
2. Wyodrębnij trzy powiadomienia z `confirm` do prywatnej metody `notifyPaid` (Extract Method).
3. Zmień parametry `notifyPaid` na jeden `event: ReservationPaid` i czytaj dane ze zdarzenia; `confirm` tworzy zdarzenie i je przekazuje. W `PaymentService.ts` dodaj import `ReservationPaid`; `PaymentServices` bez zmian.

**Po (`ReservationPaid.ts`, nowy plik):**

```typescript
import type { Money } from '../../../shared/Money.js';

/** Zdarzenie - fakt "rezerwacja opłacona" z danymi potrzebnymi odbiorcom. */
export class ReservationPaid {
  constructor(
    readonly reservationId: string,
    readonly email: string,
    readonly phone: string,
    readonly amount: Money,
  ) {}
}
```

**Przed (`PaymentService.ts`, koniec metody `confirm`):**

```typescript
this.paidList.push(payment.reservationId);
this.mailer.send(payment.email,
  `Potwierdzenie platnosci ${payment.reservationId}: ${payment.amount.toString()}`);
this.sms.send(payment.phone, `Oplacono ${payment.reservationId}`);
this.loyalty.addPoints(payment.email, Math.trunc(payment.amount.amount.trunc().toNumber() / 10));
```

**Po (`PaymentService.ts`, koniec `confirm` i nowa metoda `notifyPaid`):**

```typescript
this.paidList.push(payment.reservationId);
this.notifyPaid(new ReservationPaid(
  payment.reservationId, payment.email, payment.phone, payment.amount));
```

```typescript
private notifyPaid(event: ReservationPaid): void {
  this.mailer.send(event.email,
    `Potwierdzenie platnosci ${event.reservationId}: ${event.amount.toString()}`);
  this.sms.send(event.phone, `Oplacono ${event.reservationId}`);
  this.loyalty.addPoints(event.email, Math.trunc(event.amount.amount.trunc().toNumber() / 10));
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s09` - zielone.
**Co powiedzieć:** zdarzenie to fakt z danymi potrzebnymi odbiorcom. Jeszcze nic nie jest dynamiczne.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s09 0 1`

### Krok 2: odbiorcy jako obserwatorzy

**Zmiany:**

1. Utwórz interfejs `PaymentListener` z metodą `onPaid(event: ReservationPaid)`.
2. Utwórz trzy klasy odbiorców: `MailConfirmation`, `SmsConfirmation`, `LoyaltyPoints` - każda dostaje swoją zależność w konstruktorze i ma w `onPaid` jedną linię przeniesioną z `notifyPaid`.
3. W konstruktorze serwisu zbuduj zamrożoną tablicę odbiorców w starej kolejności (mail, SMS, punkty); sygnatura konstruktora bez zmian.
4. `notifyPaid` to pętla po odbiorcach **bez** `try/catch`. W `PaymentService.ts` dochodzą importy `LoyaltyPoints`, `MailConfirmation`, `SmsConfirmation` i `import type { PaymentListener }`.

**Przed:** konstruktor z trzema polami `private readonly` i metoda `notifyPaid` z trzema wywołaniami - jak w bloku Po kroku 1.

**Po (`PaymentListener.ts`, nowy plik):**

```typescript
import type { ReservationPaid } from './ReservationPaid.js';

/** Kontrakt obserwatora - subject zna tylko ten interfejs. Wywołanie synchroniczne. */
export interface PaymentListener {
  onPaid(event: ReservationPaid): void;
}
```

**Po (`MailConfirmation.ts`, nowy plik):**

```typescript
import type { Mailer } from '../Mailer.js';
import type { PaymentListener } from './PaymentListener.js';
import type { ReservationPaid } from './ReservationPaid.js';

/** Obserwator - potwierdzenie mailem. */
export class MailConfirmation implements PaymentListener {
  constructor(readonly mailer: Mailer) {}

  onPaid(event: ReservationPaid): void {
    this.mailer.send(event.email,
      `Potwierdzenie platnosci ${event.reservationId}: ${event.amount.toString()}`);
  }
}
```

**Po (`SmsConfirmation.ts`, nowy plik):**

```typescript
import type { SmsGateway } from '../SmsGateway.js';
import type { PaymentListener } from './PaymentListener.js';
import type { ReservationPaid } from './ReservationPaid.js';

/** Obserwator - potwierdzenie SMS. */
export class SmsConfirmation implements PaymentListener {
  constructor(readonly sms: SmsGateway) {}

  onPaid(event: ReservationPaid): void {
    this.sms.send(event.phone, `Oplacono ${event.reservationId}`);
  }
}
```

**Po (`LoyaltyPoints.ts`, nowy plik):**

```typescript
import type { LoyaltyProgram } from '../LoyaltyProgram.js';
import type { PaymentListener } from './PaymentListener.js';
import type { ReservationPaid } from './ReservationPaid.js';

/** Obserwator - 1 punkt za każde pełne 10.00. */
export class LoyaltyPoints implements PaymentListener {
  constructor(readonly loyalty: LoyaltyProgram) {}

  onPaid(event: ReservationPaid): void {
    this.loyalty.addPoints(event.email, Math.trunc(event.amount.amount.trunc().toNumber() / 10));
  }
}
```

**Po (`PaymentService.ts`, pola, konstruktor i `notifyPaid`):**

```typescript
export class PaymentService {
  private readonly listeners: readonly PaymentListener[];
  private readonly paidList: string[] = [];

  constructor(mailer: Mailer, sms: SmsGateway, loyalty: LoyaltyProgram) {
    this.listeners = Object.freeze([
      new MailConfirmation(mailer), new SmsConfirmation(sms), new LoyaltyPoints(loyalty)]);
  }

  // confirm() bez zmian

  private notifyPaid(event: ReservationPaid): void {
    for (const listener of this.listeners) {
      listener.onPaid(event);
    }
  }

  // paid() bez zmian
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** konstruktor bez zmian, relacja wciąż "ci sami trzej odbiorcy" - to nadal czysta refaktoryzacja.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s09 1 2`

### Krok 3: subscribe i korzeń kompozycji

**Zmiany:**

1. Dodaj interfejs `Subscription` z metodą `close()` oraz prywatną dla modułu klasę `Registration` opakowującą słuchacza (osobny obiekt na każde `subscribe`).
2. Dodaj w serwisie metodę `subscribe(listener)`: tworzy rejestrację, podmienia niezmienną tablicę rejestracji na kopię z nowym wpisem i zwraca `Subscription` z idempotentnym `close()` (usuwa rejestrację po tożsamości).
3. Usuń konstruktor z trzema zależnościami (staje się bezargumentowy) i pole `listeners` z gotowymi odbiorcami; pętlę powiadomień przenieś do `confirm`.
4. W korzeniu kompozycji `PaymentServices.standard(...)` utwórz serwis i zasubskrybuj trzech odbiorców w starej kolejności (importy klas odbiorców przenoszą się z `PaymentService.ts` do `PaymentServices.ts`; w serwisie zostają `IllegalArgumentError`, `requireNonNull`, `Payment`, `PaymentListener`, `ReservationPaid` i `Subscription`).

**Przed (`PaymentService.ts`):** pola, konstruktor i `notifyPaid` - jak w bloku Po kroku 2.

**Przed (`PaymentServices.ts`, metoda `standard`):**

```typescript
static standard(mailer: Mailer, sms: SmsGateway, loyalty: LoyaltyProgram): PaymentService {
  return new PaymentService(mailer, sms, loyalty);
}
```

**Po (`Subscription.ts`, nowy plik):**

```typescript
/** Uchwyt subskrypcji - close() wyrejestrowuje, idempotentnie. */
export interface Subscription {
  close(): void;
}
```

**Po (`PaymentService.ts`, klasa `Registration` nad serwisem oraz pola, `subscribe` i `confirm`):**

```typescript
/** Osobny obiekt na każde subscribe - ten sam obiekt słuchacza zapisany dwa razy to dwie rejestracje. */
class Registration {
  constructor(readonly listener: PaymentListener) {}
}

export class PaymentService {
  private listeners: readonly Registration[] = [];
  private readonly paidList: string[] = [];

  subscribe(listener: PaymentListener): Subscription {
    const registration = new Registration(requireNonNull(listener, 'listener'));
    // kopia przy zapisie (w Javie: CopyOnWriteArrayList) - trwająca pętla widzi starą migawkę
    this.listeners = [...this.listeners, registration];
    let active = true;
    return {
      close: () => {
        if (active) {
          active = false;
          this.listeners = this.listeners.filter((r) => r !== registration);
        }
      },
    };
  }

  confirm(payment: Payment): void {
    if (payment.amount.amount.lte(0)) {
      throw new IllegalArgumentError('amount must be positive');
    }
    this.paidList.push(payment.reservationId);
    const event = new ReservationPaid(
      payment.reservationId, payment.email, payment.phone, payment.amount);
    for (const registration of this.listeners) {
      registration.listener.onPaid(event);
    }
  }

  // paid() bez zmian
}
```

**Po (`PaymentServices.ts`, metoda `standard`):**

```typescript
static standard(mailer: Mailer, sms: SmsGateway, loyalty: LoyaltyProgram): PaymentService {
  const service = new PaymentService();
  service.subscribe(new MailConfirmation(mailer));
  service.subscribe(new SmsConfirmation(sms));
  service.subscribe(new LoyaltyPoints(loyalty));
  return service;
}
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

**Zmiany:**

1. Utwórz nowe pliki: unię `type MenuItem = Product | Combo`, liść `Product` (pole `amount`, metody `price()` i `render(depth, text)`) i niemutowalny węzeł `Combo` (nazwa, zamrożona lista elementów, cena jako suma, `render` z wcięciem na poziom).
2. Utwórz `MenuMapper` z metodą `fromNested(combo: readonly unknown[])`, która tłumaczy stary format na drzewo i zachowuje komunikaty błędów starego kodu (`combo needs a name`, `product needs a price: ...`, `unsupported element: ...`).
3. `BarMenu` zostaje bez zmian - obie reprezentacje żyją obok siebie. Uruchom test różnicowy: porównuje stary kod z drzewem z mappera.

**Przed:** brak - nowe pliki `MenuItem.ts`, `Product.ts`, `Combo.ts`, `MenuMapper.ts`; `BarMenu.ts` bez zmian.

**Po (`MenuItem.ts`, nowy plik):**

```typescript
import type { Combo } from './Combo.js';
import type { Product } from './Product.js';

/**
 * Jawny Composite - produkt (liść) albo zestaw (węzeł). Zamknięty zestaw typów
 * (w Javie: sealed interface) -> unia; obie klasy mają name, price() i render(depth, text).
 * render dopisuje linie do bufora text (w Javie: StringBuilder).
 */
export type MenuItem = Product | Combo;
```

**Po (`Product.ts`, nowy plik):**

```typescript
import type { Money } from '../../../shared/Money.js';

/** Liść - produkt baru z ceną. (W Javie: rekord Product(name, price).) */
export class Product {
  constructor(
    readonly name: string,
    private readonly amount: Money,
  ) {}

  price(): Money {
    return this.amount;
  }

  render(depth: number, text: string[]): void {
    text.push(`${'  '.repeat(depth)}${this.name} ${this.amount.toString()}\n`);
  }
}
```

**Po (`Combo.ts`, nowy plik):**

```typescript
import { Money } from '../../../shared/Money.js';
import type { MenuItem } from './MenuItem.js';

/** Węzeł - zestaw, którego cena to suma elementów. Niemutowalny. */
export class Combo {
  readonly items: readonly MenuItem[];

  constructor(readonly name: string, items: readonly MenuItem[]) {
    this.items = Object.freeze([...items]);
  }

  price(): Money {
    let total = Money.ZERO;
    for (const item of this.items) {
      total = total.plus(item.price());
    }
    return total;
  }

  render(depth: number, text: string[]): void {
    text.push(`${'  '.repeat(depth)}${this.name} ${this.price().toString()}\n`);
    this.items.forEach((item) => item.render(depth + 1, text));
  }
}
```

**Po (`MenuMapper.ts`, nowy plik):**

```typescript
import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import { Combo } from './Combo.js';
import type { MenuItem } from './MenuItem.js';
import { Product } from './Product.js';

/**
 * Mapper starego formatu (zagnieżdżone listy) na Composite. Zachowuje komunikaty
 * błędów starego kodu - to też jest obserwowalne zachowanie.
 */
export class MenuMapper {
  private constructor() {}

  static fromNested(combo: readonly unknown[]): Combo {
    const name = combo[0];
    if (combo.length === 0 || typeof name !== 'string') {
      throw new IllegalArgumentError('combo needs a name');
    }
    const items: MenuItem[] = [];
    for (const element of combo.slice(1)) {
      if (typeof element === 'string') {
        items.push(MenuMapper.product(element));
      } else if (Array.isArray(element)) {
        items.push(MenuMapper.fromNested(element));
      } else {
        throw new IllegalArgumentError(`unsupported element: ${String(element)}`);
      }
    }
    return new Combo(name, items);
  }

  private static product(text: string): Product {
    if (!text.includes('=')) {
      throw new IllegalArgumentError(`product needs a price: ${text}`);
    }
    const separator = text.indexOf('=');
    return new Product(text.substring(0, separator), Money.of(text.substring(separator + 1)));
  }
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s10` - zielone, w tym `compositeFromMapperMatchesLegacyAlready`.
**Co powiedzieć:** obie reprezentacje żyją obok siebie. Mapper zachowuje komunikaty błędów starego kodu - to też obserwowalne zachowanie.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s10 0 1`

### Krok 2: price na Composite

**Zmiany:**

1. Zastąp ciało `BarMenu.price` jedną linią: `return MenuMapper.fromNested(combo).price();` (dodaj w `BarMenu.ts` import `MenuMapper`).
2. Usuń metodę `requireName` - po zmianie nikt jej nie woła (edytor wyszarza nieużywaną prywatną metodę). `renderTo` i `productPrice` zostają, bo stary `render` wciąż ich używa. Pozostałe pliki bez zmian.

**Przed:**

```typescript
price(combo: readonly unknown[]): Money {
  BarMenu.requireName(combo);
  let total = Money.ZERO;
  for (const element of combo.slice(1)) {
    if (typeof element === 'string') {
      total = total.plus(BarMenu.productPrice(element));
    } else if (Array.isArray(element)) {
      total = total.plus(this.price(element));
    } else {
      throw new IllegalArgumentError(`unsupported element: ${String(element)}`);
    }
  }
  return total;
}
```

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

**Zmiany:**

1. Zastąp ciało `BarMenu.render` delegacją do drzewa: zbuduj `Combo` mapperem i wywołaj jego `render(0, text)`.
2. Usuń starą rekurencję `renderTo` i metodę `productPrice` (ręcznie - po zmianie stają się nieużywane). W importach `BarMenu.ts` znika `IllegalArgumentError`, a `Money` staje się `import type`. Pozostałe pliki bez zmian.

**Przed:**

```typescript
render(combo: readonly unknown[]): string {
  const text: string[] = [];
  this.renderTo(combo, 0, text);
  return text.join('');
}

private renderTo(combo: readonly unknown[], depth: number, text: string[]): void {
  text.push(`${'  '.repeat(depth)}${String(combo[0])} ${this.price(combo).toString()}\n`);
  for (const element of combo.slice(1)) {
    if (typeof element === 'string') {
      text.push(`${'  '.repeat(depth + 1)}${element.substring(0, element.indexOf('='))}`
        + ` ${BarMenu.productPrice(element).toString()}\n`);
    } else if (Array.isArray(element)) {
      this.renderTo(element, depth + 1, text);
    } else {
      throw new IllegalArgumentError(`unsupported element: ${String(element)}`);
    }
  }
}
```

**Po (`BarMenu.ts`, cały plik):**

```typescript
import type { Money } from '../../../shared/Money.js';
import { MenuMapper } from './MenuMapper.js';

/**
 * render też na Composite. BarMenu tylko mapuje stary format na drzewo - format
 * trwały (zagnieżdżone listy) zmienimy osobnym krokiem, jeśli w ogóle.
 */
export class BarMenu {
  price(combo: readonly unknown[]): Money {
    return MenuMapper.fromNested(combo).price();
  }

  render(combo: readonly unknown[]): string {
    const text: string[] = [];
    MenuMapper.fromNested(combo).render(0, text);
    return text.join('');
  }
}
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

**Zmiany:**

1. Usuń `add` i `children` z `MenuComponent`; w `Combo` przestają być `override` (zepchnięcie składowych w dół robimy ręcznie - VS Code nie ma Push Members Down). `Product` nie dziedziczy już żadnego `add`.
2. `describe()` w bazie zostaje bez części o dzieciach; `Combo` nadpisuje `describe()` i rozszerza opis o dzieci.
3. Uruchom `npm run typecheck` - pokaże błędy w `ComboCatalog`; zmień typ zmiennych z `MenuComponent` na `Combo` tam, gdzie dodajemy dzieci (trzy zmienne: `combo` i `drinks` w `family()`, `combo` w `duo()`). `Product` bez zmian w kodzie - po prostu nie dziedziczy już `add`.

**Przed (`MenuComponent.ts`, cały plik):**

```typescript
import { UnsupportedOperationError } from '../../../../shared/errors.js';
import type { Money } from '../../../shared/Money.js';

/**
 * Start: Transparent Composite - add() i children() we wspólnym typie. Klient może wywołać
 * add() na liściu; kompilator milczy, błąd wychodzi dopiero w runtime.
 */
export abstract class MenuComponent {
  abstract name(): string;

  abstract price(): Money;

  add(_child: MenuComponent): void {
    throw new UnsupportedOperationError(`cannot add to ${this.name()}`);
  }

  children(): readonly MenuComponent[] {
    return Object.freeze([]);
  }

  describe(): string {
    const text = `${this.name()} ${this.price().toString()}`;
    if (this.children().length === 0) {
      return text;
    }
    return `${text} [${this.children().map((child) => child.describe()).join(', ')}]`;
  }
}
```

**Po (`MenuComponent.ts`, cały plik):**

```typescript
import type { Money } from '../../../shared/Money.js';

/**
 * Safe Composite - Push Members Down: add() i children() tylko w Combo.
 * Wspólny typ ma wyłącznie operacje sensowne dla liścia i węzła.
 */
export abstract class MenuComponent {
  abstract name(): string;

  abstract price(): Money;

  describe(): string {
    return `${this.name()} ${this.price().toString()}`;
  }
}
```

**Po (`Combo.ts` - `add` i `children` tracą `override`, dochodzi `describe`; nazwa, konstruktor i `price` bez zmian):**

```typescript
add(child: MenuComponent): void {
  this.childList.push(child);
}

children(): readonly MenuComponent[] {
  return Object.freeze([...this.childList]);
}

override describe(): string {
  if (this.childList.length === 0) {
    return super.describe();
  }
  return `${super.describe()} [${this.childList.map((child) => child.describe()).join(', ')}]`;
}
```

**Po (`ComboCatalog.ts`, typy zmiennych):**

```typescript
const combo: Combo = new Combo('Zestaw Rodzinny');
combo.add(new Product('Popcorn XL', '24.00'));
const drinks: Combo = new Combo('Napoje');
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s11` - zielone; `S11SolutionTest` pokazuje, że `Product` nie ma już `add`.
**Co powiedzieć:** błąd przeniósł się z runtime do kompilacji (vitest go nie zobaczy - widać go w `npm run typecheck` i w edytorze). Ceną jest to, że klient musi wiedzieć, czy ma w ręku węzeł.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s11 0 1`

### Krok 2: niemutowalny Composite jako unia

**Zmiany:**

1. Zamień klasę abstrakcyjną `MenuComponent` na unię `type MenuComponent = Product | Combo`.
2. Przepisz `Product` i `Combo` na niezmienne klasy bez wspólnej bazy: pole `kind` (`'product'`/`'combo'`), `name` i `price` jako pola tylko do odczytu (w `Combo` `price` to getter sumujący dzieci), dzieci podane w konstruktorze i zamrożone; dodaj wygodne `Combo.of(name, ...children)`. Metoda `add` nie istnieje nigdzie.
3. Zapisz katalog deklaratywnie - zagnieżdżone wywołania `Combo.of(...)` zamiast zmiennych i `add`; prywatne metody `family()` i `duo()` znikają, drzewa budują się wprost w gałęziach `switch` metody `find`.

**Przed:** `MenuComponent.ts` jak w bloku Po kroku 1; `Combo` i `Product` jako klasy `extends MenuComponent` (jak w start i kroku 1); w `ComboCatalog` metody `family()` i `duo()` budujące drzewa przez `add`, np.:

```typescript
private duo(): MenuComponent {
  const combo: Combo = new Combo('Zestaw Duo');
  combo.add(new Product('Popcorn L', '18.00'));
  combo.add(new Product('Cola', '9.00'));
  combo.add(new Product('Cola', '9.00'));
  return combo;
}
```

**Po (`MenuComponent.ts`, cały plik):**

```typescript
import type { Combo } from './Combo.js';
import type { Product } from './Product.js';

/**
 * Forma zamknięta (odpowiednik sealed interface z Javy 25) - unia znanych węzłów,
 * niemutowalne obiekty, brak mutacji po zbudowaniu.
 */
export type MenuComponent = Product | Combo;
```

**Po (`Product.ts`, cały plik):**

```typescript
import { Money } from '../../../shared/Money.js';

/** Liść jako niemutowalny obiekt wartości (odpowiednik rekordu). */
export class Product {
  readonly kind = 'product';
  readonly price: Money;

  constructor(readonly name: string, price: Money | string) {
    this.price = typeof price === 'string' ? Money.of(price) : price;
  }

  describe(): string {
    return `${this.name} ${this.price.toString()}`;
  }
}
```

**Po (`Combo.ts`, cały plik):**

```typescript
import { Money } from '../../../shared/Money.js';
import type { MenuComponent } from './MenuComponent.js';

/** Węzeł z dziećmi podanymi przy tworzeniu - add() nie istnieje nigdzie. */
export class Combo {
  readonly kind = 'combo';
  readonly children: readonly MenuComponent[];

  constructor(readonly name: string, children: readonly MenuComponent[]) {
    this.children = Object.freeze([...children]);
  }

  static of(name: string, ...children: MenuComponent[]): Combo {
    return new Combo(name, children);
  }

  get price(): Money {
    let total = Money.ZERO;
    for (const child of this.children) {
      total = total.plus(child.price);
    }
    return total;
  }

  describe(): string {
    const text = `${this.name} ${this.price.toString()}`;
    if (this.children.length === 0) {
      return text;
    }
    return `${text} [${this.children.map((child) => child.describe()).join(', ')}]`;
  }
}
```

**Po (`ComboCatalog.ts`, metoda `find`):**

```typescript
find(code: string): MenuComponent {
  switch (code) {
    case 'family': return Combo.of('Zestaw Rodzinny',
      new Product('Popcorn XL', '24.00'),
      Combo.of('Napoje',
        new Product('Cola', '9.00'),
        new Product('Cola', '9.00'),
        new Product('Woda', '7.00')));
    case 'duo': return Combo.of('Zestaw Duo',
      new Product('Popcorn L', '18.00'),
      new Product('Cola', '9.00'),
      new Product('Cola', '9.00'));
    case 'nachos': return new Product('Nachos', '14.00');
    default: throw new IllegalArgumentError(`unknown combo: ${code}`);
  }
}
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

**Zmiany:**

1. W `refund` wyodrębnij wyliczenie kwoty zwrotu jednego biletu do metody `ticketShare(ticket, now)` (Extract Method); nie używa `this`, więc jest `private static`, a `if/else` zamienia się na wczesne `return`.
2. W `refundAll` zastąp ciało pętli wywołaniem `ticketShare` - duplikacja reguły znika. `CancellationDesk` bez zmian.

**Przed (`RefundService.ts`, metody `refund` i `refundAll`):**

```typescript
refund(ticket: TicketData, now: LocalDateTime): Money {
  let amount: Money;
  if (!now.isBefore(ticket.showStart)) {
    amount = Money.ZERO;
  } else if (Duration.between(now, ticket.showStart).toHours() >= 24) {
    amount = ticket.price;
  } else {
    amount = ticket.price.percent(50);
  }
  return amount.minus(RefundService.FEE).max(Money.ZERO);
}

refundAll(tickets: readonly TicketData[], now: LocalDateTime): Money {
  let total = Money.ZERO;
  for (const ticket of tickets) {
    if (now.isBefore(ticket.showStart)) {
      const hours = Duration.between(now, ticket.showStart).toHours();
      total = total.plus(hours >= 24 ? ticket.price : ticket.price.percent(50));
    }
  }
  return total.minus(RefundService.FEE).max(Money.ZERO);
}
```

**Po (`RefundService.ts`, wszystkie trzy metody):**

```typescript
refund(ticket: TicketData, now: LocalDateTime): Money {
  return RefundService.ticketShare(ticket, now).minus(RefundService.FEE).max(Money.ZERO);
}

refundAll(tickets: readonly TicketData[], now: LocalDateTime): Money {
  let total = Money.ZERO;
  for (const ticket of tickets) {
    total = total.plus(RefundService.ticketShare(ticket, now));
  }
  return total.minus(RefundService.FEE).max(Money.ZERO);
}

private static ticketShare(ticket: TicketData, now: LocalDateTime): Money {
  if (!now.isBefore(ticket.showStart)) {
    return Money.ZERO;
  }
  if (Duration.between(now, ticket.showStart).toHours() >= 24) {
    return ticket.price;
  }
  return ticket.price.percent(50);
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s12` - zielone.
**Co powiedzieć:** zanim wprowadzimy wzorzec, usuwamy duplikację reguły. Test granicy 24h potwierdza, że obie wersje były równoważne.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s12 0 1`

### Krok 2: Composite i jeden kontrakt

**Zmiany:**

1. Utwórz unię `type Refundable = SingleTicket | TicketGroup` i obie klasy z metodą `refundableAmount(now)`: liść `SingleTicket` dostaje regułę przeniesioną z `ticketShare`, węzeł `TicketGroup` sumuje elementy (pusta grupa daje 0.00) i ma pomocnicze `TicketGroup.of(tickets)`.
2. Zmień nazwę starej metody `refund(ticket, now)` na `refundTicket` (zmiana nazwy symbolu poprawi wywołania) - TypeScript nie ma przeciążeń, więc nowa `refund(refundable, now)` nie może z nią współistnieć pod jedną nazwą.
3. Dodaj nową metodę `refund(refundable, now)` z potrąceniem w jednym miejscu; `refundTicket` i `refundAll` zostają jako cienkie delegacje ze znacznikiem `@deprecated` w TSDoc.
4. Przeprowadź `CancellationDesk` na nowy kontrakt - na razie wciąż wybiera `SingleTicket` albo `TicketGroup`. Metoda `ticketShare` znika z serwisu (jej reguła mieszka teraz w `SingleTicket`), a z importów serwisu znika `Duration`.

**Przed:** metody `refund`, `refundAll` i `ticketShare` serwisu - jak w bloku Po kroku 1 - oraz klient:

```typescript
refund(tickets: readonly TicketData[], now: LocalDateTime): Money {
  const [first] = tickets;
  if (tickets.length === 1 && first !== undefined) {
    return this.service.refund(first, now);
  }
  return this.service.refundAll(tickets, now);
}
```

**Po (`Refundable.ts`, nowy plik):**

```typescript
import type { SingleTicket } from './SingleTicket.js';
import type { TicketGroup } from './TicketGroup.js';

/**
 * Wspólny kontrakt "jednego" i "wielu" - kwota do zwrotu przed potrąceniem
 * (refundableAmount(now)). Zamknięta unia (odpowiednik sealed interface z Javy).
 */
export type Refundable = SingleTicket | TicketGroup;
```

**Po (`SingleTicket.ts`, nowy plik):**

```typescript
import { Money } from '../../../shared/Money.js';
import { Duration, type LocalDateTime } from '../../../shared/time.js';
import type { TicketData } from '../TicketData.js';

/** Liść - reguła zwrotu jednego biletu (przeniesiona z ticketShare). */
export class SingleTicket {
  readonly kind = 'single';

  constructor(readonly ticket: TicketData) {}

  refundableAmount(now: LocalDateTime): Money {
    if (!now.isBefore(this.ticket.showStart)) {
      return Money.ZERO;
    }
    if (Duration.between(now, this.ticket.showStart).toHours() >= 24) {
      return this.ticket.price;
    }
    return this.ticket.price.percent(50);
  }
}
```

**Po (`TicketGroup.ts`, nowy plik):**

```typescript
import { Money } from '../../../shared/Money.js';
import type { LocalDateTime } from '../../../shared/time.js';
import type { TicketData } from '../TicketData.js';
import type { Refundable } from './Refundable.js';
import { SingleTicket } from './SingleTicket.js';

/** Węzeł - suma zwrotów elementów; pusta grupa daje 0.00. */
export class TicketGroup {
  readonly kind = 'group';
  readonly items: readonly Refundable[];

  constructor(items: readonly Refundable[]) {
    this.items = Object.freeze([...items]);
  }

  static of(tickets: readonly TicketData[]): TicketGroup {
    return new TicketGroup(tickets.map((ticket) => new SingleTicket(ticket)));
  }

  refundableAmount(now: LocalDateTime): Money {
    let total = Money.ZERO;
    for (const item of this.items) {
      total = total.plus(item.refundableAmount(now));
    }
    return total;
  }
}
```

**Po (`RefundService.ts`, wszystkie metody; w importach dochodzą `Refundable`, `SingleTicket` i `TicketGroup`):**

```typescript
refund(refundable: Refundable, now: LocalDateTime): Money {
  return refundable.refundableAmount(now).minus(RefundService.FEE).max(Money.ZERO);
}

/**
 * Dawne refund(ticket, now) - TypeScript nie ma przeciążeń, więc stara metoda dostaje osobną nazwę.
 * @deprecated użyj {@link RefundService.refund} z {@link SingleTicket}.
 */
refundTicket(ticket: TicketData, now: LocalDateTime): Money {
  return this.refund(new SingleTicket(ticket), now);
}

/** @deprecated użyj {@link RefundService.refund} z {@link TicketGroup}. */
refundAll(tickets: readonly TicketData[], now: LocalDateTime): Money {
  return this.refund(TicketGroup.of(tickets), now);
}
```

**Po (`CancellationDesk.ts`, metoda `refund`; w importach dochodzą `Refundable`, `SingleTicket` i `TicketGroup`):**

```typescript
refund(tickets: readonly TicketData[], now: LocalDateTime): Money {
  const [first] = tickets;
  const refundable: Refundable = tickets.length === 1 && first !== undefined
    ? new SingleTicket(first)
    : TicketGroup.of(tickets);
  return this.service.refund(refundable, now);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** potrącenie jest w jednym miejscu i naliczane raz na wywołanie - tak jak w obu starych metodach. VS Code przekreśla wywołania metod oznaczonych `@deprecated`, więc widać, kto jeszcze nie przeszedł.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s12 1 2`

### Krok 3: usunięcie starych metod

**Zmiany:**

1. W `CancellationDesk` usuń rozróżnienie jeden/wiele - klient zawsze buduje `TicketGroup.of(tickets)`, także z jednego biletu.
2. Usuń z `RefundService` przestarzałe `refundTicket` i `refundAll` (wyszukanie wszystkich odwołań potwierdza, że nikt ich już nie woła) - w serwisie zostaje tylko `refund(refundable, now)` z kroku 2, a z jego importów znikają `TicketData`, `SingleTicket` i `TicketGroup`. W kliencie z importów znikają `Refundable` i `SingleTicket`. Pliki Composite bez zmian.

**Przed (`CancellationDesk.ts`, metoda `refund` z kroku 2):**

```typescript
refund(tickets: readonly TicketData[], now: LocalDateTime): Money {
  const [first] = tickets;
  const refundable: Refundable = tickets.length === 1 && first !== undefined
    ? new SingleTicket(first)
    : TicketGroup.of(tickets);
  return this.service.refund(refundable, now);
}
```

**Po (`CancellationDesk.ts`, metoda `refund`):**

```typescript
refund(tickets: readonly TicketData[], now: LocalDateTime): Money {
  return this.service.refund(TicketGroup.of(tickets), now);
}
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

**Zmiany:**

1. Utwórz nowy plik z `abstract class CompositeProgramItem implements ProgramItem` (Extract Superclass robimy ręcznie - VS Code go nie ma) i przenieś do niej z `Marathon` pole `childList`, metodę `add` i `children()`; `minutes()` i `describe()` zadeklaruj jako `abstract`.
2. Zmień `Marathon` na `extends CompositeProgramItem`: zostają `minutes()` i `describe()` z `override`, a do dzieci sięgają przez `this.children()`.
3. Tak samo `ShortsBlock extends CompositeProgramItem` - usuń jego kopie pola, `add` i `children()`. W obu podklasach importy `requireNonNull` i `ProgramItem` zastępuje import `CompositeProgramItem`. `Film`, `ProgramItem` i `ProgramCatalog` bez zmian.

**Przed (`Marathon.ts`, cały plik; `ShortsBlock.ts` analogicznie, tylko `minutes()` bez przerw i etykieta "Blok"):**

```typescript
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { ProgramItem } from './ProgramItem.js';

/** Kontener filmów. Obsługa dzieci jest skopiowana w ShortsBlock. */
export class Marathon implements ProgramItem {
  private readonly childList: ProgramItem[] = [];

  constructor(private readonly name: string) {}

  add(child: ProgramItem): void {
    this.childList.push(requireNonNull(child, 'child'));
  }

  children(): readonly ProgramItem[] {
    return Object.freeze([...this.childList]);
  }

  minutes(): number {
    let total = 0;
    for (const child of this.childList) {
      total += child.minutes();
    }
    return this.childList.length === 0 ? 0 : total + 15 * (this.childList.length - 1);
  }

  describe(): string {
    return `Maraton ${this.name} (${this.minutes()} min) `
      + `[${this.childList.map((child) => child.describe()).join(', ')}]`;
  }
}
```

**Po (`CompositeProgramItem.ts`, nowy plik):**

```typescript
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { ProgramItem } from './ProgramItem.js';

/** Extract Superclass - wspólna obsługa dzieci (pole, add, children) w jednym miejscu. */
export abstract class CompositeProgramItem implements ProgramItem {
  private readonly childList: ProgramItem[] = [];

  add(child: ProgramItem): void {
    this.childList.push(requireNonNull(child, 'child'));
  }

  children(): readonly ProgramItem[] {
    return Object.freeze([...this.childList]);
  }

  abstract minutes(): number;

  abstract describe(): string;
}
```

**Po (`Marathon.ts`, cały plik):**

```typescript
import { CompositeProgramItem } from './CompositeProgramItem.js';

/** Pole children, add i children() podciągnięte do CompositeProgramItem. */
export class Marathon extends CompositeProgramItem {
  constructor(private readonly name: string) {
    super();
  }

  override minutes(): number {
    const children = this.children();
    let total = 0;
    for (const child of children) {
      total += child.minutes();
    }
    return children.length === 0 ? 0 : total + 15 * (children.length - 1);
  }

  override describe(): string {
    return `Maraton ${this.name} (${this.minutes()} min) `
      + `[${this.children().map((child) => child.describe()).join(', ')}]`;
  }
}
```

**Po (`ShortsBlock.ts`, cały plik):**

```typescript
import { CompositeProgramItem } from './CompositeProgramItem.js';

/** Pole children, add i children() podciągnięte do CompositeProgramItem. */
export class ShortsBlock extends CompositeProgramItem {
  constructor(private readonly name: string) {
    super();
  }

  override minutes(): number {
    let total = 0;
    for (const child of this.children()) {
      total += child.minutes();
    }
    return total;
  }

  override describe(): string {
    return `Blok ${this.name} (${this.minutes()} min) `
      + `[${this.children().map((child) => child.describe()).join(', ')}]`;
  }
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s13` - zielone.
**Co powiedzieć:** kontrakt dzieci (brak `null`, kopia listy) jest teraz w jednym miejscu dla wszystkich kontenerów.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s13 0 1`

### Krok 2: Pull Up sumy i opisu

**Zmiany:**

1. Przenieś do bazy (ręcznie) sumę czasu dzieci jako `protected childrenMinutes()` - obie pętle liczyły to samo.
2. Przenieś `describe()` do bazy jako szablon z hookiem `protected abstract label()`; pole `name` i chroniony konstruktor z nazwą też idą do bazy.
3. W podklasach zostają tylko `minutes()` (reguła przerw) i `label()` (etykieta "Maraton"/"Blok"); konstruktory przekazują nazwę przez `super(name)`. `Film`, `ProgramItem` i `ProgramCatalog` bez zmian.

**Przed:** trzy pliki jak w blokach Po kroku 1.

**Po (`CompositeProgramItem.ts`, cały plik):**

```typescript
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { ProgramItem } from './ProgramItem.js';

/**
 * Pull Up Method - suma czasu dzieci i szkielet opisu też w nadklasie. Podklasy
 * dostarczają tylko to, czym naprawdę się różnią: etykietę i regułę przerw.
 */
export abstract class CompositeProgramItem implements ProgramItem {
  private readonly childList: ProgramItem[] = [];

  protected constructor(private readonly name: string) {}

  add(child: ProgramItem): void {
    this.childList.push(requireNonNull(child, 'child'));
  }

  children(): readonly ProgramItem[] {
    return Object.freeze([...this.childList]);
  }

  protected childrenMinutes(): number {
    let total = 0;
    for (const child of this.childList) {
      total += child.minutes();
    }
    return total;
  }

  describe(): string {
    return `${this.label()} ${this.name} (${this.minutes()} min) `
      + `[${this.childList.map((child) => child.describe()).join(', ')}]`;
  }

  abstract minutes(): number;

  protected abstract label(): string;
}
```

**Po (`Marathon.ts`, cała klasa):**

```typescript
export class Marathon extends CompositeProgramItem {
  constructor(name: string) {
    super(name);
  }

  override minutes(): number {
    const count = this.children().length;
    return count === 0 ? 0 : this.childrenMinutes() + 15 * (count - 1);
  }

  protected override label(): string {
    return 'Maraton';
  }
}
```

**Po (`ShortsBlock.ts`, cała klasa):**

```typescript
export class ShortsBlock extends CompositeProgramItem {
  constructor(name: string) {
    super(name);
  }

  override minutes(): number {
    return this.childrenMinutes();
  }

  protected override label(): string {
    return 'Blok';
  }
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

**Zmiany:**

1. Wyodrębnij gałąź XML z `pay` do prywatnej metody `payWithXml(reservationId, amount): PaymentResult` (Extract Method).
2. Tak samo gałąź REST do `payWithRest(reservationId, amount): PaymentResult` - obie metody mają identyczną sygnaturę i wynik.

**Przed (`CheckoutService.ts`, metoda `pay`; pomocnicze `attribute` bez zmian):**

```typescript
pay(provider: string, reservationId: string, amount: Money): PaymentResult {
  if (provider === 'XML') {
    const grosze = amount.amount.times(100).toNumber();
    const request = `<charge ref='${reservationId}' amount='${grosze}'/>`;
    const response = this.xml.submit(request);
    if (response.includes("status='OK'")) {
      return PaymentResult.accepted(CheckoutService.attribute(response, 'id'));
    }
    return PaymentResult.declined(CheckoutService.attribute(response, 'code'));
  } else if (provider === 'REST') {
    try {
      return PaymentResult.accepted(
        this.rest.charge(new ChargeRequest(amount.amount, 'PLN', reservationId)).transactionId);
    } catch (exception) {
      if (exception instanceof RestPayException) {
        return PaymentResult.declined(exception.code);
      }
      throw exception;
    }
  }
  throw new IllegalArgumentError(`unknown provider: ${provider}`);
}
```

**Po (`CheckoutService.ts`, metody `pay`, `payWithXml` i `payWithRest`):**

```typescript
pay(provider: string, reservationId: string, amount: Money): PaymentResult {
  if (provider === 'XML') {
    return this.payWithXml(reservationId, amount);
  } else if (provider === 'REST') {
    return this.payWithRest(reservationId, amount);
  }
  throw new IllegalArgumentError(`unknown provider: ${provider}`);
}

private payWithXml(reservationId: string, amount: Money): PaymentResult {
  const grosze = amount.amount.times(100).toNumber();
  const response = this.xml.submit(`<charge ref='${reservationId}' amount='${grosze}'/>`);
  if (response.includes("status='OK'")) {
    return PaymentResult.accepted(CheckoutService.attribute(response, 'id'));
  }
  return PaymentResult.declined(CheckoutService.attribute(response, 'code'));
}

private payWithRest(reservationId: string, amount: Money): PaymentResult {
  try {
    return PaymentResult.accepted(
      this.rest.charge(new ChargeRequest(amount.amount, 'PLN', reservationId)).transactionId);
  } catch (exception) {
    if (exception instanceof RestPayException) {
      return PaymentResult.declined(exception.code);
    }
    throw exception;
  }
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s14` - zielone.
**Co powiedzieć:** identyczna sygnatura dwóch metod to gotowy kontrakt adaptera.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s14 0 1`

### Krok 2: interfejs i dwa adaptery

**Zmiany:**

1. Utwórz interfejs `PaymentGateway` z metodą `pay(reservationId, amount): PaymentResult`.
2. Utwórz `XmlPayAdapter implements PaymentGateway`: przenieś do niego ciało `payWithXml` jako `pay` razem z pomocniczym `attribute` (przenosimy ręcznie - VS Code nie przenosi metod między klasami); bramkę dostaje w konstruktorze.
3. Tak samo `RestPayAdapter` z ciałem `payWithRest`.
4. W serwisie zmień typ pól `xml` i `rest` na `PaymentGateway`; sygnatura konstruktora bez zmian - tworzy adaptery, a `pay` deleguje do nich (`this.xml.pay(...)`, `this.rest.pay(...)`). Metody `payWithXml`, `payWithRest` i `attribute` znikają z serwisu; w importach serwisu `PaymentResult` i `RestPayClient` stają się `import type`, `ChargeRequest`/`RestPayException` znikają, dochodzą `PaymentGateway`, `RestPayAdapter`, `XmlPayAdapter`.

**Przed:** metody `pay`, `payWithXml`, `payWithRest` serwisu - jak w bloku Po kroku 1.

**Po (`PaymentGateway.ts`, nowy plik):**

```typescript
import type { Money } from '../../../shared/Money.js';
import type { PaymentResult } from '../PaymentResult.js';

/** Preferowany interfejs kina - kwota jako Money, odmowa jako wynik, nie wyjątek. */
export interface PaymentGateway {
  pay(reservationId: string, amount: Money): PaymentResult;
}
```

**Po (`XmlPayAdapter.ts`, nowy plik):**

```typescript
import type { Money } from '../../../shared/Money.js';
import { PaymentResult } from '../PaymentResult.js';
import type { XmlPayGateway } from '../XmlPayGateway.js';
import type { PaymentGateway } from './PaymentGateway.js';

/**
 * Adapter starej bramki - tłumaczy jednostki (złote -> grosze), format (XML)
 * i wynik (atrybuty status/id/code). Nie jest właścicielem bramki.
 */
export class XmlPayAdapter implements PaymentGateway {
  constructor(private readonly xml: XmlPayGateway) {}

  pay(reservationId: string, amount: Money): PaymentResult {
    const grosze = amount.amount.times(100).toNumber();
    const response = this.xml.submit(`<charge ref='${reservationId}' amount='${grosze}'/>`);
    if (response.includes("status='OK'")) {
      return PaymentResult.accepted(XmlPayAdapter.attribute(response, 'id'));
    }
    return PaymentResult.declined(XmlPayAdapter.attribute(response, 'code'));
  }

  private static attribute(xml: string, name: string): string {
    const start = xml.indexOf(`${name}='`) + name.length + 2;
    return xml.substring(start, xml.indexOf("'", start));
  }
}
```

**Po (`RestPayAdapter.ts`, nowy plik):**

```typescript
import type { Money } from '../../../shared/Money.js';
import { PaymentResult } from '../PaymentResult.js';
import { ChargeRequest, type RestPayClient, RestPayException } from '../RestPayClient.js';
import type { PaymentGateway } from './PaymentGateway.js';

/** Adapter nowej bramki - odmowa zgłaszana wyjątkiem staje się wynikiem declined. */
export class RestPayAdapter implements PaymentGateway {
  constructor(private readonly rest: RestPayClient) {}

  pay(reservationId: string, amount: Money): PaymentResult {
    try {
      return PaymentResult.accepted(
        this.rest.charge(new ChargeRequest(amount.amount, 'PLN', reservationId)).transactionId);
    } catch (exception) {
      if (exception instanceof RestPayException) {
        return PaymentResult.declined(exception.code);
      }
      throw exception;
    }
  }
}
```

**Po (`CheckoutService.ts`, cała klasa):**

```typescript
export class CheckoutService {
  private readonly xml: PaymentGateway;
  private readonly rest: PaymentGateway;

  constructor(xml: XmlPayGateway, rest: RestPayClient) {
    this.xml = new XmlPayAdapter(xml);
    this.rest = new RestPayAdapter(rest);
  }

  pay(provider: string, reservationId: string, amount: Money): PaymentResult {
    if (provider === 'XML') {
      return this.xml.pay(reservationId, amount);
    } else if (provider === 'REST') {
      return this.rest.pay(reservationId, amount);
    }
    throw new IllegalArgumentError(`unknown provider: ${provider}`);
  }
}
```

**Uruchom:** test zielony; `S14SolutionTest` sprawdza, że do starej bramki idzie `amount='4000'`.
**Co powiedzieć:** adapter tłumaczy jednostki (złote - grosze), format i sposób zgłaszania odmowy (atrybut kontra wyjątek). Nie udaje, że semantyka jest taka sama.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s14 1 2`

### Krok 3: serwis zależy tylko od interfejsu

**Zmiany:**

1. Zastąp pola `xml` i `rest` jedną mapą `private readonly gateways: ReadonlyMap<string, PaymentGateway>`.
2. Dodaj sygnaturę przeciążenia konstruktora `(gateways: ReadonlyMap<string, PaymentGateway>)` (zapisywana jako kopia `new Map(gateways)`); stara sygnatura `(xml, rest)` zostaje jako drugie przeciążenie i buduje mapę ze standardowym zestawem adapterów (implementacja rozróżnia argument przez `instanceof XmlPayGateway`).
3. W `pay` zastąp `if/else if` wyszukaniem w mapie; nieznany dostawca rzuca ten sam wyjątek co dotąd. W importach serwisu dochodzi `requireNonNull`, a `XmlPayGateway` przestaje być `import type` (konstruktor używa go w `instanceof`). Interfejs i adaptery bez zmian.

**Przed:** cała klasa `CheckoutService` - jak w bloku Po kroku 2.

**Po (`CheckoutService.ts`, cała klasa):**

```typescript
export class CheckoutService {
  private readonly gateways: ReadonlyMap<string, PaymentGateway>;

  /** Dotychczasowy konstruktor jako skrót: standardowy zestaw dwóch adapterów. */
  constructor(xml: XmlPayGateway, rest: RestPayClient);
  constructor(gateways: ReadonlyMap<string, PaymentGateway>);
  constructor(xmlOrGateways: XmlPayGateway | ReadonlyMap<string, PaymentGateway>, rest?: RestPayClient) {
    if (xmlOrGateways instanceof XmlPayGateway) {
      this.gateways = new Map<string, PaymentGateway>([
        ['XML', new XmlPayAdapter(xmlOrGateways)],
        ['REST', new RestPayAdapter(requireNonNull(rest, 'rest'))],
      ]);
    } else {
      this.gateways = new Map(xmlOrGateways);
    }
  }

  pay(provider: string, reservationId: string, amount: Money): PaymentResult {
    const gateway = this.gateways.get(provider);
    if (gateway === undefined) {
      throw new IllegalArgumentError(`unknown provider: ${provider}`);
    }
    return gateway.pay(reservationId, amount);
  }
}
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

**Zmiany:**

1. Wyodrębnij ciało gałęzi SELL do prywatnej metody `sell(args: string): string` (Extract Method).
2. Tak samo REFUND do `refund(args)` i REPORT do `report(args)` - ta sama sygnatura u wszystkich, także gdy `args` nie jest używane (parametr `_args` w `report`). Pola `PRICES`, `cash` i `tickets` bez zmian.

**Przed (`CashierConsole.ts`, metoda `handle`):**

```typescript
handle(line: string): string {
  const [name = '', ...rest] = line.trim().split(' ');
  const command = name.toUpperCase();
  const args = rest.join(' ');
  if (command === 'SELL') {
    const [count = '', ...titleParts] = args.split(' ');
    if (titleParts.length === 0 || !/^\d+$/.test(count)) {
      return 'Blad: SELL <liczba> <tytul>';
    }
    const title = titleParts.join(' ');
    const quantity = Number.parseInt(count, 10);
    const price = CashierConsole.PRICES.get(title);
    if (price === undefined) {
      return `Blad: nieznany film ${title}`;
    }
    const total = price.times(quantity);
    this.cash = this.cash.plus(total);
    this.tickets += quantity;
    return `Sprzedano ${quantity} x ${title} = ${total.toString()}`;
  } else if (command === 'REFUND') {
    const price = CashierConsole.PRICES.get(args);
    if (price === undefined) {
      return `Blad: nieznany film ${args}`;
    }
    if (this.tickets === 0) {
      return 'Blad: brak biletow do zwrotu';
    }
    this.cash = this.cash.minus(price);
    this.tickets--;
    return `Zwrot 1 x ${args} = ${price.toString()}`;
  } else if (command === 'REPORT') {
    return `Kasa: ${this.cash.toString()}, biletow: ${this.tickets}`;
  }
  return `Nieznana komenda: ${name}`;
}
```

**Po (`CashierConsole.ts`, metoda `handle` i trzy metody prywatne):**

```typescript
handle(line: string): string {
  const [name = '', ...rest] = line.trim().split(' ');
  const command = name.toUpperCase();
  const args = rest.join(' ');
  if (command === 'SELL') {
    return this.sell(args);
  } else if (command === 'REFUND') {
    return this.refund(args);
  } else if (command === 'REPORT') {
    return this.report(args);
  }
  return `Nieznana komenda: ${name}`;
}

private sell(args: string): string {
  const [count = '', ...titleParts] = args.split(' ');
  if (titleParts.length === 0 || !/^\d+$/.test(count)) {
    return 'Blad: SELL <liczba> <tytul>';
  }
  const title = titleParts.join(' ');
  const quantity = Number.parseInt(count, 10);
  const price = CashierConsole.PRICES.get(title);
  if (price === undefined) {
    return `Blad: nieznany film ${title}`;
  }
  const total = price.times(quantity);
  this.cash = this.cash.plus(total);
  this.tickets += quantity;
  return `Sprzedano ${quantity} x ${title} = ${total.toString()}`;
}

private refund(args: string): string {
  const price = CashierConsole.PRICES.get(args);
  if (price === undefined) {
    return `Blad: nieznany film ${args}`;
  }
  if (this.tickets === 0) {
    return 'Blad: brak biletow do zwrotu';
  }
  this.cash = this.cash.minus(price);
  this.tickets--;
  return `Zwrot 1 x ${args} = ${price.toString()}`;
}

private report(_args: string): string {
  return `Kasa: ${this.cash.toString()}, biletow: ${this.tickets}`;
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s15` - zielone.
**Co powiedzieć:** jednolita sygnatura przygotowuje interfejs komendy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s15 0 1`

### Krok 2: komendy jako obiekty, stan w Till

**Zmiany:**

1. Utwórz klasę `Till` i przenieś do niej stan kasy: cennik `PRICES`, pola gotówki i liczby biletów oraz metody `priceOf`, `sold`, `refunded`, `cash`, `tickets` (Extract Class, ręcznie).
2. Utwórz interfejs `ConsoleCommand` z metodą `execute(args: string, till: Till): string`.
3. Utwórz klasy `SellCommand`, `RefundCommand`, `ReportCommand` i przenieś do ich `execute` ciała metod `sell`, `refund`, `report`; na stanie operują przez `till`.
4. W konsoli zostają pole `till`, po jednym polu na komendę i dyspozytor `if` - teraz deleguje do `execute`. Prywatne metody `sell`/`refund`/`report`, pola `PRICES`, `cash`, `tickets` i import `Money` znikają z konsoli; dochodzą importy `ConsoleCommand`, trzech komend i `Till`. Zmieniamy jedną rzecz naraz.

**Przed:** metoda `handle` i trzy metody prywatne konsoli - jak w bloku Po kroku 1.

**Po (`Till.ts`, nowy plik):**

```typescript
import { Money } from '../../../shared/Money.js';

/** Stan kasy wydzielony z konsoli - komendy dostają go jako argument i same są bezstanowe. */
export class Till {
  private static readonly PRICES: ReadonlyMap<string, Money> = new Map([
    ['Diuna', Money.of('40.00')],
    ['Kraina Lodu', Money.of('32.00')],
    ['Amator', Money.of('25.00')],
  ]);

  private cashAmount = Money.ZERO;
  private ticketCount = 0;

  priceOf(title: string): Money | undefined {
    return Till.PRICES.get(title);
  }

  sold(quantity: number, total: Money): void {
    this.cashAmount = this.cashAmount.plus(total);
    this.ticketCount += quantity;
  }

  refunded(price: Money): void {
    this.cashAmount = this.cashAmount.minus(price);
    this.ticketCount--;
  }

  cash(): Money {
    return this.cashAmount;
  }

  tickets(): number {
    return this.ticketCount;
  }
}
```

**Po (`ConsoleCommand.ts`, nowy plik):**

```typescript
import type { Till } from './Till.js';

/** Kontrakt komendy - dane żądania (args) i stan (till) przychodzą jako argumenty. */
export interface ConsoleCommand {
  execute(args: string, till: Till): string;
}
```

**Po (`SellCommand.ts`, nowy plik):**

```typescript
import type { ConsoleCommand } from './ConsoleCommand.js';
import type { Till } from './Till.js';

/** Gałąź SELL jako obiekt komendy. */
export class SellCommand implements ConsoleCommand {
  execute(args: string, till: Till): string {
    const [count = '', ...titleParts] = args.split(' ');
    if (titleParts.length === 0 || !/^\d+$/.test(count)) {
      return 'Blad: SELL <liczba> <tytul>';
    }
    const title = titleParts.join(' ');
    const quantity = Number.parseInt(count, 10);
    const price = till.priceOf(title);
    if (price === undefined) {
      return `Blad: nieznany film ${title}`;
    }
    const total = price.times(quantity);
    till.sold(quantity, total);
    return `Sprzedano ${quantity} x ${title} = ${total.toString()}`;
  }
}
```

**Po (`RefundCommand.ts`, nowy plik):**

```typescript
import type { ConsoleCommand } from './ConsoleCommand.js';
import type { Till } from './Till.js';

/** Gałąź REFUND jako obiekt komendy. */
export class RefundCommand implements ConsoleCommand {
  execute(args: string, till: Till): string {
    const price = till.priceOf(args);
    if (price === undefined) {
      return `Blad: nieznany film ${args}`;
    }
    if (till.tickets() === 0) {
      return 'Blad: brak biletow do zwrotu';
    }
    till.refunded(price);
    return `Zwrot 1 x ${args} = ${price.toString()}`;
  }
}
```

**Po (`ReportCommand.ts`, nowy plik):**

```typescript
import type { ConsoleCommand } from './ConsoleCommand.js';
import type { Till } from './Till.js';

/** Gałąź REPORT jako obiekt komendy. */
export class ReportCommand implements ConsoleCommand {
  execute(_args: string, till: Till): string {
    return `Kasa: ${till.cash().toString()}, biletow: ${till.tickets()}`;
  }
}
```

**Po (`CashierConsole.ts`, cała klasa):**

```typescript
export class CashierConsole {
  private readonly till = new Till();
  private readonly sell: ConsoleCommand = new SellCommand();
  private readonly refund: ConsoleCommand = new RefundCommand();
  private readonly report: ConsoleCommand = new ReportCommand();

  handle(line: string): string {
    const [name = '', ...rest] = line.trim().split(' ');
    const command = name.toUpperCase();
    const args = rest.join(' ');
    if (command === 'SELL') {
      return this.sell.execute(args, this.till);
    } else if (command === 'REFUND') {
      return this.refund.execute(args, this.till);
    } else if (command === 'REPORT') {
      return this.report.execute(args, this.till);
    }
    return `Nieznana komenda: ${name}`;
  }
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** komendy są bezstanowe - dane żądania to argument, stan kasy to argument. Współdzielona komenda ze stanem poprzedniego żądania to błąd czekający na drugie, przeplatane wywołanie (np. przez `await`).
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s15 1 2`

### Krok 3: rejestr zamiast warunku

**Zmiany:**

1. Zastąp trzy pola komend jedną mapą `private readonly commands: ReadonlyMap<string, ConsoleCommand>`.
2. Dodaj konstruktor z parametrem `commands` o wartości domyślnej (mapa ze standardowym zestawem SELL/REFUND/REPORT); zapisuj kopię `new Map(commands)`.
3. W `handle` usuń łańcuch `if` - wyszukaj komendę po znormalizowanym kluczu `name.toUpperCase()`; brak wpisu daje dotychczasowe `Nieznana komenda: ...`. Pozostałe pliki bez zmian.

**Przed:** cała klasa `CashierConsole` - jak w bloku Po kroku 2.

**Po (`CashierConsole.ts`, cała klasa):**

```typescript
export class CashierConsole {
  private readonly till = new Till();
  private readonly commands: ReadonlyMap<string, ConsoleCommand>;

  constructor(commands: ReadonlyMap<string, ConsoleCommand> = new Map<string, ConsoleCommand>([
    ['SELL', new SellCommand()],
    ['REFUND', new RefundCommand()],
    ['REPORT', new ReportCommand()],
  ])) {
    this.commands = new Map(commands);
  }

  handle(line: string): string {
    const [name = '', ...rest] = line.trim().split(' ');
    const command = this.commands.get(name.toUpperCase());
    if (command === undefined) {
      return `Nieznana komenda: ${name}`;
    }
    return command.execute(rest.join(' '), this.till);
  }
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

**Zmiany:**

1. W `CsvSalesReport` wyodrębnij nagłówek, wiersz i stopkę do prywatnych metod `header()`, `row(sale)`, `footer(tickets, total)` (Extract Method).
2. Zrób to samo w `HtmlSalesReport` - te same nazwy i sygnatury.
3. Przepisz `render` HTML na ten sam kształt co CSV: kopia tablicy i `sort` zamiast `toSorted`, zmienne `text`, `tickets`, `total` zamiast tablicy fragmentów i `sum`/`count` - aż oba `render` będą identyczne znak w znak.

**Przed (`CsvSalesReport.ts`, metoda `render`):**

```typescript
render(sales: readonly Sale[]): string {
  const sorted = [...sales];
  sorted.sort((a, b) => a.time.compareTo(b.time));
  let csv = 'godzina;film;bilety;kwota\n';
  let tickets = 0;
  let total = Money.ZERO;
  for (const sale of sorted) {
    const title = sale.title.includes(';') ? `"${sale.title}"` : sale.title;
    csv += `${sale.time.toString()};${title};${sale.tickets};${sale.amount.toString()}\n`;
    tickets += sale.tickets;
    total = total.plus(sale.amount);
  }
  csv += `SUMA;;${tickets};${total.toString()}\n`;
  return csv;
}
```

**Przed (`HtmlSalesReport.ts`, metoda `render`):**

```typescript
render(sales: readonly Sale[]): string {
  const html: string[] = [];
  html.push('<table>\n<tr><th>Godzina</th><th>Film</th><th>Bilety</th><th>Kwota</th></tr>\n');
  let sum = Money.ZERO;
  let count = 0;
  for (const sale of sales.toSorted((a, b) => a.time.compareTo(b.time))) {
    const title = sale.title.replaceAll('&', '&amp;').replaceAll('<', '&lt;');
    html.push('<tr><td>', sale.time.toString(), '</td><td>', title,
      '</td><td>', String(sale.tickets), '</td><td>', sale.amount.toString(),
      '</td></tr>\n');
    sum = sum.plus(sale.amount);
    count += sale.tickets;
  }
  html.push('<tr><td colspan="2">Suma</td><td>', String(count), '</td><td>', sum.toString(),
    '</td></tr>\n</table>\n');
  return html.join('');
}
```

**Po (`CsvSalesReport.ts` i `HtmlSalesReport.ts`, identyczna metoda `render` w obu klasach):**

```typescript
render(sales: readonly Sale[]): string {
  const sorted = [...sales];
  sorted.sort((a, b) => a.time.compareTo(b.time));
  let text = this.header();
  let tickets = 0;
  let total = Money.ZERO;
  for (const sale of sorted) {
    text += this.row(sale);
    tickets += sale.tickets;
    total = total.plus(sale.amount);
  }
  return text + this.footer(tickets, total);
}
```

**Po (`CsvSalesReport.ts`, metody prywatne):**

```typescript
private header(): string {
  return 'godzina;film;bilety;kwota\n';
}

private row(sale: Sale): string {
  const title = sale.title.includes(';') ? `"${sale.title}"` : sale.title;
  return `${sale.time.toString()};${title};${sale.tickets};${sale.amount.toString()}\n`;
}

private footer(tickets: number, total: Money): string {
  return `SUMA;;${tickets};${total.toString()}\n`;
}
```

**Po (`HtmlSalesReport.ts`, metody prywatne):**

```typescript
private header(): string {
  return '<table>\n<tr><th>Godzina</th><th>Film</th><th>Bilety</th><th>Kwota</th></tr>\n';
}

private row(sale: Sale): string {
  const title = sale.title.replaceAll('&', '&amp;').replaceAll('<', '&lt;');
  return `<tr><td>${sale.time.toString()}</td><td>${title}</td><td>${sale.tickets}`
    + `</td><td>${sale.amount.toString()}</td></tr>\n`;
}

private footer(tickets: number, total: Money): string {
  return `<tr><td colspan="2">Suma</td><td>${tickets}</td><td>${total.toString()}</td></tr>\n`
    + '</table>\n';
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s16` - zielone.
**Co powiedzieć:** ujednolicamy sygnatury po kroku, aż `render` obu klas będzie identyczny znak w znak.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s16 0 1`

### Krok 2: Extract Superclass + Pull Up render

**Zmiany:**

1. Utwórz ręcznie `abstract class SalesReport` (Extract Superclass - VS Code go nie ma): przenieś do niej `render` z CSV, a `header`, `row` i `footer` zadeklaruj jako `protected abstract` (Pull Up Method).
2. Zmień obie klasy na `extends SalesReport`: metody formatujące dostają `protected override`, kopie `render` usuń.
3. Dodaj w konstruktorze bazy strażnika zamiast `final`: jeśli podklasa nadpisała `render`, konstruktor rzuca `IllegalStateError('render() is final')`.
4. W obu podklasach import `Money` staje się `import type`, dochodzi import `SalesReport`.

**Przed:** obie klasy z identycznym `render` i prywatnymi `header`/`row`/`footer` - jak w blokach Po kroku 1.

**Po (`SalesReport.ts`, nowy plik):**

```typescript
import { IllegalStateError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { Sale } from '../Sale.js';

/**
 * Form Template Method - Extract Superclass + Pull Up render(). Szkielet jest "final":
 * kolejność kroków i liczenie sumy należą do bazy, podklasy dostarczają tylko formatowanie.
 * TypeScript nie ma modyfikatora final, więc konstruktor bazy odrzuca podklasę nadpisującą render().
 */
export abstract class SalesReport {
  constructor() {
    if (this.render !== SalesReport.prototype.render) {
      throw new IllegalStateError('render() is final');
    }
  }

  render(sales: readonly Sale[]): string {
    const sorted = [...sales];
    sorted.sort((a, b) => a.time.compareTo(b.time));
    let text = this.header();
    let tickets = 0;
    let total = Money.ZERO;
    for (const sale of sorted) {
      text += this.row(sale);
      tickets += sale.tickets;
      total = total.plus(sale.amount);
    }
    return text + this.footer(tickets, total);
  }

  protected abstract header(): string;

  protected abstract row(sale: Sale): string;

  protected abstract footer(tickets: number, total: Money): string;
}
```

**Po (`CsvSalesReport.ts`, cały plik):**

```typescript
import type { Money } from '../../../shared/Money.js';
import type { Sale } from '../Sale.js';
import { SalesReport } from './SalesReport.js';

/** Raport CSV to już tylko trzy metody formatujące. */
export class CsvSalesReport extends SalesReport {
  protected override header(): string {
    return 'godzina;film;bilety;kwota\n';
  }

  protected override row(sale: Sale): string {
    const title = sale.title.includes(';') ? `"${sale.title}"` : sale.title;
    return `${sale.time.toString()};${title};${sale.tickets};${sale.amount.toString()}\n`;
  }

  protected override footer(tickets: number, total: Money): string {
    return `SUMA;;${tickets};${total.toString()}\n`;
  }
}
```

**Po (`HtmlSalesReport.ts`, cały plik):**

```typescript
import type { Money } from '../../../shared/Money.js';
import type { Sale } from '../Sale.js';
import { SalesReport } from './SalesReport.js';

/** Raport HTML to już tylko trzy metody formatujące. */
export class HtmlSalesReport extends SalesReport {
  protected override header(): string {
    return '<table>\n<tr><th>Godzina</th><th>Film</th><th>Bilety</th><th>Kwota</th></tr>\n';
  }

  protected override row(sale: Sale): string {
    const title = sale.title.replaceAll('&', '&amp;').replaceAll('<', '&lt;');
    return `<tr><td>${sale.time.toString()}</td><td>${title}</td><td>${sale.tickets}`
      + `</td><td>${sale.amount.toString()}</td></tr>\n`;
  }

  protected override footer(tickets: number, total: Money): string {
    return `<tr><td colspan="2">Suma</td><td>${tickets}</td><td>${total.toString()}</td></tr>\n`
      + '</table>\n';
  }
}
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

**Zmiany:**

1. W `PriceList` zrób konstruktor prywatny i dodaj `private static readonly INSTANCE = new PriceList()` oraz `static getInstance()` zwracające tę instancję.
2. Usuń licznik z pomiaru: pole `createdCount` i metodę `created()`.
3. W `TicketDesk.quote` zamień `new PriceList()` na `PriceList.getInstance()`.

**Przed (`PriceList.ts`, cały plik):**

```typescript
import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';

export class PriceList {
  private static readonly TARIFF = '2D=25.00;3D=32.00;IMAX=40.00';
  private static createdCount = 0;

  private readonly prices = new Map<string, Money>();

  constructor() {
    for (const entry of PriceList.TARIFF.split(';')) {
      const [format = '', amount = ''] = entry.split('=');
      this.prices.set(format, Money.of(amount));
    }
    PriceList.createdCount++;
  }

  static created(): number {
    return PriceList.createdCount;
  }

  basePrice(format: string): Money {
    const price = this.prices.get(format);
    if (price === undefined) {
      throw new IllegalArgumentError(`unknown format: ${format}`);
    }
    return price;
  }
}
```

**Po (`PriceList.ts`, cały plik):**

```typescript
import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';

export class PriceList {
  private static readonly TARIFF = '2D=25.00;3D=32.00;IMAX=40.00';
  private static readonly INSTANCE = new PriceList();

  private readonly prices = new Map<string, Money>();

  private constructor() {
    for (const entry of PriceList.TARIFF.split(';')) {
      const [format = '', amount = ''] = entry.split('=');
      this.prices.set(format, Money.of(amount));
    }
  }

  static getInstance(): PriceList {
    return PriceList.INSTANCE;
  }

  basePrice(format: string): Money {
    const price = this.prices.get(format);
    if (price === undefined) {
      throw new IllegalArgumentError(`unknown format: ${format}`);
    }
    return price;
  }
}
```

**Po (`TicketDesk.ts`, metoda `quote` - jedyna zmiana w pliku):**

```typescript
const price = PriceList.getInstance().basePrice(format);
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s17` - zielone.
**Co powiedzieć:** Singleton to decyzja o cyklu życia - bezpieczna tylko dlatego, że obiekt jest niemutowalny i udowodniliśmy równoważność instancji.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s17 0 1`

### Krok 2: Singleton jako zamrożona stała modułu

**Zmiany:**

1. Przenieś taryfę i parsowanie poza klasę: stała modułu `TARIFF` i funkcja `parse()` zwracająca `ReadonlyMap<string, Money>`, wywołana raz przy ewaluacji modułu (`const prices = parse()`).
2. Zamień klasę na zamrożoną stałą modułu: `export const PriceList = Object.freeze({ basePrice(format) { ... } })`.
3. W `TicketDesk.quote` zamień `PriceList.getInstance().basePrice(format)` na `PriceList.basePrice(format)`.

**Przed:** `PriceList.ts` - jak w bloku Po kroku 1.

**Po (`PriceList.ts`, cały plik):**

```typescript
import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';

const TARIFF = '2D=25.00;3D=32.00;IMAX=40.00';

function parse(): ReadonlyMap<string, Money> {
  const parsed = new Map<string, Money>();
  for (const entry of TARIFF.split(';')) {
    const [format = '', amount = ''] = entry.split('=');
    parsed.set(format, Money.of(amount));
  }
  return parsed;
}

const prices = parse();

export const PriceList = Object.freeze({
  basePrice(format: string): Money {
    const price = prices.get(format);
    if (price === undefined) {
      throw new IllegalArgumentError(`unknown format: ${format}`);
    }
    return price;
  },
});
```

**Po (`TicketDesk.ts`, metoda `quote` - jedyna zmiana w pliku):**

```typescript
const price = PriceList.basePrice(format);
```

**Uruchom:** test zielony.
**Co powiedzieć:** moduł ES daje jednokrotną inicjalizację bez leniwych sztuczek, a `Object.freeze` blokuje podmianę metody. Ale to **jedna instancja na graf modułów**, nie "jedna na proces" - dwie kopie pakietu albo worker mają własne.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s17 1 2`

### Krok 3: wstrzyknięcie zamiast globalnego dostępu

**Zmiany:**

1. Utwórz nowy plik `Tariff.ts` z interfejsem `Tariff` i jedyną metodą `basePrice(format: string): Money` (Extract Interface).
2. Oznacz stałą typem interfejsu: `export const PriceList: Tariff = Object.freeze({ ... })` - w `PriceList.ts` dochodzi `import type { Tariff } from './Tariff.js'`, reszta pliku bez zmian.
3. W `TicketDesk` dodaj pole `private readonly tariff: Tariff` i konstruktor `(tariff: Tariff = PriceList)`; `quote` woła `this.tariff.basePrice(format)`.

**Przed (`TicketDesk.ts`, cały plik bez importów):**

```typescript
export class TicketDesk {
  quote(format: string, online: boolean): Money {
    const price = PriceList.basePrice(format);
    return online ? price.plus(Money.of('2.00')) : price;
  }
}
```

**Po (`Tariff.ts`, nowy plik):**

```typescript
import type { Money } from '../../../shared/Money.js';

/** Extract Interface - klient zależy od kontraktu cennika, nie od singletona. */
export interface Tariff {
  basePrice(format: string): Money;
}
```

**Po (`TicketDesk.ts`, cały plik):**

```typescript
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Money } from '../../../shared/Money.js';
import { PriceList } from './PriceList.js';
import type { Tariff } from './Tariff.js';

export class TicketDesk {
  private readonly tariff: Tariff;

  constructor(tariff: Tariff = PriceList) {
    this.tariff = requireNonNull(tariff, 'tariff');
  }

  quote(format: string, online: boolean): Money {
    const price = this.tariff.basePrice(format);
    return online ? price.plus(Money.of('2.00')) : price;
  }
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

**Zmiany:**

1. Zmień zwracany typ `checkEmail` i `checkSeats` na `readonly string[]`: każdy fragment zwracaj jako element tablicy, bez `'; '` na końcu.
2. W `validate` zamień `let warnings = ''` na `const warnings: string[] = []`, a sklejanie `+=` na `warnings.push(...)`.
3. Wynik składaj przez `warnings.length === 0 ? 'OK' : warnings.join('; ')` - obcinanie dwóch ostatnich znaków znika.

**Przed (`ReservationValidator.ts`, cały plik):**

```typescript
import type { ReservationDraft } from '../ReservationDraft.js';

export class ReservationValidator {
  validate(draft: ReservationDraft): string {
    let warnings = '';
    warnings += this.checkEmail(draft.email);
    warnings += this.checkSeats(draft.seats);
    if (!draft.now.isBefore(draft.showStart)) {
      warnings += 'seans juz sie rozpoczal; ';
    }
    return warnings === '' ? 'OK' : warnings.substring(0, warnings.length - 2);
  }

  private checkEmail(email: string | null): string {
    if (email === null || email.trim() === '') {
      return 'brak e-maila; ';
    }
    if (!email.includes('@')) {
      return `niepoprawny e-mail: ${email}; `;
    }
    return '';
  }

  private checkSeats(seats: readonly string[]): string {
    if (seats.length === 0) {
      return 'brak miejsc; ';
    }
    let result = '';
    const seen = new Set<string>();
    const reported = new Set<string>();
    for (const seat of seats) {
      if (seen.has(seat) && !reported.has(seat)) {
        reported.add(seat);
        result += `miejsce ${seat} zdublowane; `;
      }
      seen.add(seat);
    }
    if (seats.length >= 10) {
      result += 'grupa 10+: zastosuj rabat grupowy; ';
    }
    return result;
  }
}
```

**Po (`ReservationValidator.ts`, cały plik):**

```typescript
import type { ReservationDraft } from '../ReservationDraft.js';

export class ReservationValidator {
  validate(draft: ReservationDraft): string {
    const warnings: string[] = [];
    warnings.push(...this.checkEmail(draft.email));
    warnings.push(...this.checkSeats(draft.seats));
    if (!draft.now.isBefore(draft.showStart)) {
      warnings.push('seans juz sie rozpoczal');
    }
    return warnings.length === 0 ? 'OK' : warnings.join('; ');
  }

  private checkEmail(email: string | null): readonly string[] {
    if (email === null || email.trim() === '') {
      return ['brak e-maila'];
    }
    if (!email.includes('@')) {
      return [`niepoprawny e-mail: ${email}`];
    }
    return [];
  }

  private checkSeats(seats: readonly string[]): readonly string[] {
    if (seats.length === 0) {
      return ['brak miejsc'];
    }
    const result: string[] = [];
    const seen = new Set<string>();
    const reported = new Set<string>();
    for (const seat of seats) {
      if (seen.has(seat) && !reported.has(seat)) {
        reported.add(seat);
        result.push(`miejsce ${seat} zdublowane`);
      }
      seen.add(seat);
    }
    if (seats.length >= 10) {
      result.push('grupa 10+: zastosuj rabat grupowy');
    }
    return result;
  }
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s18` - zielone.
**Co powiedzieć:** separator dokłada teraz jedno miejsce. Metody wciąż tworzą własne tablice.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s18 0 1`

### Krok 2: Collecting Parameter

**Zmiany:**

1. Zmień sygnatury `checkEmail` i `checkSeats`: dodaj parametr `warnings: string[]`, zwracany typ `void`.
2. W ich ciałach zamień każde `return [x]` na `warnings.push(x)` (w `checkSeats` po `brak miejsc` zostaje samo `return`).
3. Warunek czasu seansu wyciągnij z `validate` do nowej metody `checkShowTime(draft, warnings)` (Extract Method).
4. W `validate` wołaj metody z akumulatorem: `this.checkEmail(draft.email, warnings)` itd. - `push(...)` znika.

**Przed:** `ReservationValidator.ts` - jak w bloku Po kroku 1.

**Po (`ReservationValidator.ts`, cały plik):**

```typescript
import type { ReservationDraft } from '../ReservationDraft.js';

export class ReservationValidator {
  validate(draft: ReservationDraft): string {
    const warnings: string[] = [];
    this.checkEmail(draft.email, warnings);
    this.checkSeats(draft.seats, warnings);
    this.checkShowTime(draft, warnings);
    return warnings.length === 0 ? 'OK' : warnings.join('; ');
  }

  private checkEmail(email: string | null, warnings: string[]): void {
    if (email === null || email.trim() === '') {
      warnings.push('brak e-maila');
    } else if (!email.includes('@')) {
      warnings.push(`niepoprawny e-mail: ${email}`);
    }
  }

  private checkSeats(seats: readonly string[], warnings: string[]): void {
    if (seats.length === 0) {
      warnings.push('brak miejsc');
      return;
    }
    const seen = new Set<string>();
    const reported = new Set<string>();
    for (const seat of seats) {
      if (seen.has(seat) && !reported.has(seat)) {
        reported.add(seat);
        warnings.push(`miejsce ${seat} zdublowane`);
      }
      seen.add(seat);
    }
    if (seats.length >= 10) {
      warnings.push('grupa 10+: zastosuj rabat grupowy');
    }
  }

  private checkShowTime(draft: ReservationDraft, warnings: string[]): void {
    if (!draft.now.isBefore(draft.showStart)) {
      warnings.push('seans juz sie rozpoczal');
    }
  }
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** metody dopisują do przekazanego akumulatora. Właścicielem kolekcji jest `validate` - tworzy ją i decyduje o formacie.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s18 1 2`

### Krok 3: wąski typ parametru

**Zmiany:**

1. Utwórz nowy plik `Warnings.ts`: klasa `Warnings` z prywatną tablicą `items`, metodą `add(warning)` i `summary()` zwracającym `'OK'` albo `items.join('; ')`.
2. Zmień typ parametru `warnings` w `checkEmail`, `checkSeats` i `checkShowTime` ze `string[]` na `Warnings`, a każde `push` na `add` (`npm run typecheck` wskaże każde miejsce).
3. W `validate` twórz `new Warnings()` i zwracaj `warnings.summary()`.

**Przed (`ReservationValidator.ts`, metoda `validate`):**

```typescript
const warnings: string[] = [];
this.checkEmail(draft.email, warnings);
this.checkSeats(draft.seats, warnings);
this.checkShowTime(draft, warnings);
return warnings.length === 0 ? 'OK' : warnings.join('; ');
```

**Po (`Warnings.ts`, nowy plik):**

```typescript
/** Wąski parametr zbierający - można tylko dopisać ostrzeżenie. */
export class Warnings {
  private readonly items: string[] = [];

  add(warning: string): void {
    this.items.push(warning);
  }

  summary(): string {
    return this.items.length === 0 ? 'OK' : this.items.join('; ');
  }
}
```

**Po (`ReservationValidator.ts`, metoda `validate`; w imporcie dochodzi `import { Warnings } from './Warnings.js'`, a w `checkEmail`, `checkSeats` i `checkShowTime` tylko typ parametru i `push` na `add`):**

```typescript
validate(draft: ReservationDraft): string {
  const warnings = new Warnings();
  this.checkEmail(draft.email, warnings);
  this.checkSeats(draft.seats, warnings);
  this.checkShowTime(draft, warnings);
  return warnings.summary();
}
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

**Zmiany:**

1. Utwórz nowy plik `OrderItemVisitor.ts`: interfejs `OrderItemVisitor<R>` z metodami `visitTicket`, `visitSnack`, `visitVoucher`.
2. Do interfejsu `OrderItem` dodaj metodę `accept<R>(visitor: OrderItemVisitor<R>): R`, a w każdej klasie pozycji zaimplementuj ją wywołaniem swojej metody: `visitor.visitTicket(this)` itd.
3. Utwórz nowy plik `ReceiptLineVisitor.ts`: klasa `implements OrderItemVisitor<string>`, do której przenosisz trzy gałęzie metody `line`.
4. W `ReceiptPrinter` dodaj pole `private readonly lines = new ReceiptLineVisitor()` (plus import), w pętli zamień `this.line(item)` na `item.accept(this.lines)` i usuń metodę `line`.

**Przed (`ReceiptPrinter.ts`, metoda `line` i wiersz pętli w `print`):**

```typescript
text += `${this.line(item)}\n`;
```

```typescript
private line(item: OrderItem): string {
  if (item instanceof TicketItem) {
    return `Bilet ${item.title} ${item.format} ${item.price.toString()}`;
  } else if (item instanceof SnackItem) {
    return `${item.name} ${item.price.toString()}`;
  } else if (item instanceof VoucherItem) {
    return `Voucher ${item.code} -${item.value.toString()}`;
  }
  throw new IllegalArgumentError(`unknown item: ${String(item)}`);
}
```

**Po (`OrderItemVisitor.ts`, nowy plik):**

```typescript
import type { SnackItem } from './SnackItem.js';
import type { TicketItem } from './TicketItem.js';
import type { VoucherItem } from './VoucherItem.js';

export interface OrderItemVisitor<R> {
  visitTicket(ticket: TicketItem): R;

  visitSnack(snack: SnackItem): R;

  visitVoucher(voucher: VoucherItem): R;
}
```

**Po (`ReceiptLineVisitor.ts`, nowy plik):**

```typescript
import type { OrderItemVisitor } from './OrderItemVisitor.js';
import type { SnackItem } from './SnackItem.js';
import type { TicketItem } from './TicketItem.js';
import type { VoucherItem } from './VoucherItem.js';

export class ReceiptLineVisitor implements OrderItemVisitor<string> {
  visitTicket(ticket: TicketItem): string {
    return `Bilet ${ticket.title} ${ticket.format} ${ticket.price.toString()}`;
  }

  visitSnack(snack: SnackItem): string {
    return `${snack.name} ${snack.price.toString()}`;
  }

  visitVoucher(voucher: VoucherItem): string {
    return `Voucher ${voucher.code} -${voucher.value.toString()}`;
  }
}
```

**Po (`OrderItem.ts`, cały plik):**

```typescript
import type { OrderItemVisitor } from './OrderItemVisitor.js';

export interface OrderItem {
  accept<R>(visitor: OrderItemVisitor<R>): R;
}
```

**Po (`TicketItem.ts`, cały plik; w `SnackItem.ts` i `VoucherItem.ts` analogicznie `visitor.visitSnack(this)` / `visitor.visitVoucher(this)`):**

```typescript
import type { Money } from '../../../shared/Money.js';
import type { OrderItem } from './OrderItem.js';
import type { OrderItemVisitor } from './OrderItemVisitor.js';

export class TicketItem implements OrderItem {
  constructor(readonly title: string, readonly format: string, readonly price: Money) {}

  accept<R>(visitor: OrderItemVisitor<R>): R {
    return visitor.visitTicket(this);
  }
}
```

**Po (`ReceiptPrinter.ts`, wiersz pętli w `print`):**

```typescript
text += `${item.accept(this.lines)}\n`;
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s19` - zielone.
**Co powiedzieć:** double dispatch - element wybiera metodę odwiedzającego. Brak metody dla typu to błąd kompilacji (`npm run typecheck`).
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s19 0 1`

### Krok 2: pozostałe operacje jako Visitory

**Zmiany:**

1. Utwórz `AmountVisitor.ts` (`implements OrderItemVisitor<Money>`) z gałęziami metody `amount` i `VatVisitor.ts` z gałęziami metody `vat` (pomocnicze `vatOf` przenieś do `VatVisitor`).
2. W `ReceiptPrinter` dodaj pola `amounts` i `vats`, w pętli wołaj `item.accept(...)` i usuń metody `amount`, `vat` oraz `vatOf` - łańcuchy `instanceof` znikają razem z importami klas pozycji.

**Przed (`ReceiptPrinter.ts`, metody `amount`, `vat` i `vatOf`):**

```typescript
private amount(item: OrderItem): Money {
  if (item instanceof TicketItem) {
    return item.price;
  } else if (item instanceof SnackItem) {
    return item.price;
  } else if (item instanceof VoucherItem) {
    return Money.ZERO.minus(item.value);
  }
  throw new IllegalArgumentError(`unknown item: ${String(item)}`);
}

private vat(item: OrderItem): Money {
  if (item instanceof TicketItem) {
    return ReceiptPrinter.vatOf(item.price, 8);
  } else if (item instanceof SnackItem) {
    return ReceiptPrinter.vatOf(item.price, 23);
  } else if (item instanceof VoucherItem) {
    return Money.ZERO;
  }
  throw new IllegalArgumentError(`unknown item: ${String(item)}`);
}

/** VAT zawarty w cenie brutto: brutto * stawka / (100 + stawka). */
private static vatOf(gross: Money, rate: number): Money {
  return new Money(gross.amount.times(rate).dividedBy(100 + rate).toDecimalPlaces(2, Decimal.ROUND_HALF_UP));
}
```

**Po (`AmountVisitor.ts`, nowy plik):**

```typescript
import { Money } from '../../../shared/Money.js';
import type { OrderItemVisitor } from './OrderItemVisitor.js';
import type { SnackItem } from './SnackItem.js';
import type { TicketItem } from './TicketItem.js';
import type { VoucherItem } from './VoucherItem.js';

export class AmountVisitor implements OrderItemVisitor<Money> {
  visitTicket(ticket: TicketItem): Money {
    return ticket.price;
  }

  visitSnack(snack: SnackItem): Money {
    return snack.price;
  }

  visitVoucher(voucher: VoucherItem): Money {
    return Money.ZERO.minus(voucher.value);
  }
}
```

**Po (`VatVisitor.ts`, nowy plik):**

```typescript
import { Decimal } from 'decimal.js';

import { Money } from '../../../shared/Money.js';
import type { OrderItemVisitor } from './OrderItemVisitor.js';
import type { SnackItem } from './SnackItem.js';
import type { TicketItem } from './TicketItem.js';
import type { VoucherItem } from './VoucherItem.js';

export class VatVisitor implements OrderItemVisitor<Money> {
  visitTicket(ticket: TicketItem): Money {
    return VatVisitor.vatOf(ticket.price, 8);
  }

  visitSnack(snack: SnackItem): Money {
    return VatVisitor.vatOf(snack.price, 23);
  }

  visitVoucher(_voucher: VoucherItem): Money {
    return Money.ZERO;
  }

  private static vatOf(gross: Money, rate: number): Money {
    return new Money(gross.amount.times(rate).dividedBy(100 + rate).toDecimalPlaces(2, Decimal.ROUND_HALF_UP));
  }
}
```

**Po (`ReceiptPrinter.ts`, cały plik):**

```typescript
import { Money } from '../../../shared/Money.js';
import { AmountVisitor } from './AmountVisitor.js';
import type { OrderItem } from './OrderItem.js';
import { ReceiptLineVisitor } from './ReceiptLineVisitor.js';
import { VatVisitor } from './VatVisitor.js';

export class ReceiptPrinter {
  private readonly lines = new ReceiptLineVisitor();
  private readonly amounts = new AmountVisitor();
  private readonly vats = new VatVisitor();

  print(items: readonly OrderItem[]): string {
    let text = '';
    let total = Money.ZERO;
    let vat = Money.ZERO;
    for (const item of items) {
      text += `${item.accept(this.lines)}\n`;
      total = total.plus(item.accept(this.amounts));
      vat = vat.plus(item.accept(this.vats));
    }
    return `${text}Razem: ${total.max(Money.ZERO).toString()}\nVAT: ${vat.toString()}\n`;
  }
}
```

**Uruchom:** test zielony; `S19SolutionTest` dodaje operację "grupa VAT" jako nowy Visitor.
**Co powiedzieć:** macierz zmian: nowa operacja - nowa klasa, tanio; nowy rodzaj pozycji - zmiana interfejsu i **wszystkich** Visitorów.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s19 1 2`

### Krok 3: alternatywa TypeScript - unia dyskryminowana + switch

**Zmiany:**

1. Zamień interfejs `OrderItem` na zamkniętą unię: `export type OrderItem = TicketItem | SnackItem | VoucherItem`.
2. W każdej klasie pozycji dodaj dyskryminator `readonly kind = 'ticket'` (odpowiednio `'snack'`, `'voucher'`) i usuń `accept`.
3. Usuń pliki `OrderItemVisitor.ts`, `ReceiptLineVisitor.ts`, `AmountVisitor.ts`, `VatVisitor.ts`.
4. W `ReceiptPrinter` zapisz operacje jako statyczne metody ze `switch (item.kind)` zakończonym `default: return assertNever(item)`; w linii paragonu użyj destrukturyzacji w gałęzi (`const { title, format, price } = item`).

**Przed:** `ReceiptPrinter.ts`, `OrderItem.ts` i `TicketItem.ts` - jak w blokach Po kroków 1-2.

**Po (`OrderItem.ts`, cały plik):**

```typescript
import type { SnackItem } from './SnackItem.js';
import type { TicketItem } from './TicketItem.js';
import type { VoucherItem } from './VoucherItem.js';

export type OrderItem = TicketItem | SnackItem | VoucherItem;
```

**Po (`TicketItem.ts`, cały plik; w `SnackItem.ts` i `VoucherItem.ts` analogicznie `readonly kind = 'snack'` / `readonly kind = 'voucher'`, konstruktory bez zmian):**

```typescript
import type { Money } from '../../../shared/Money.js';

export class TicketItem {
  readonly kind = 'ticket';

  constructor(readonly title: string, readonly format: string, readonly price: Money) {}
}
```

**Po (`ReceiptPrinter.ts`, cały plik):**

```typescript
import { Decimal } from 'decimal.js';

import { assertNever } from '../../../../shared/assertNever.js';
import { Money } from '../../../shared/Money.js';
import type { OrderItem } from './OrderItem.js';

export class ReceiptPrinter {
  print(items: readonly OrderItem[]): string {
    let text = '';
    let total = Money.ZERO;
    let vat = Money.ZERO;
    for (const item of items) {
      text += `${ReceiptPrinter.line(item)}\n`;
      total = total.plus(ReceiptPrinter.amount(item));
      vat = vat.plus(ReceiptPrinter.vat(item));
    }
    return `${text}Razem: ${total.max(Money.ZERO).toString()}\nVAT: ${vat.toString()}\n`;
  }

  static line(item: OrderItem): string {
    switch (item.kind) {
      case 'ticket': {
        const { title, format, price } = item;
        return `Bilet ${title} ${format} ${price.toString()}`;
      }
      case 'snack': {
        const { name, price } = item;
        return `${name} ${price.toString()}`;
      }
      case 'voucher': {
        const { code, value } = item;
        return `Voucher ${code} -${value.toString()}`;
      }
      default: return assertNever(item);
    }
  }

  static amount(item: OrderItem): Money {
    switch (item.kind) {
      case 'ticket': return item.price;
      case 'snack': return item.price;
      case 'voucher': return Money.ZERO.minus(item.value);
      default: return assertNever(item);
    }
  }

  static vat(item: OrderItem): Money {
    switch (item.kind) {
      case 'ticket': return ReceiptPrinter.vatOf(item.price, 8);
      case 'snack': return ReceiptPrinter.vatOf(item.price, 23);
      case 'voucher': return Money.ZERO;
      default: return assertNever(item);
    }
  }

  private static vatOf(gross: Money, rate: number): Money {
    return new Money(gross.amount.times(rate).dividedBy(100 + rate).toDecimalPlaces(2, Decimal.ROUND_HALF_UP));
  }
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

**Zmiany:**

1. Zauważ, że tabela to dwie niezależne reguły: cena bazowa formatu i korekta dnia.
2. Wydziel `private static basePrice(format)` ze `switch` po formacie (`'25.00'`, `'32.00'`, `'40.00'`, nieznany format rzuca ten sam wyjątek) (Extract Method).
3. Wydziel `private static adjustForDay(day, base)` ze `switch` po dniu: wtorek `base.minus(base.percent(30))`, sobota i niedziela `base.plus(Money.of('2.00'))`, reszta `base`.
4. `price` to złożenie obu metod; zmienna `weekend` znika.

**Przed:**

```typescript
price(day: DayOfWeek, format: string): Money {
  const weekend = day === 'SATURDAY' || day === 'SUNDAY';
  if (format === '2D') {
    return Money.of(day === 'TUESDAY' ? '17.50' : weekend ? '27.00' : '25.00');
  } else if (format === '3D') {
    return Money.of(day === 'TUESDAY' ? '22.40' : weekend ? '34.00' : '32.00');
  } else if (format === 'IMAX') {
    return Money.of(day === 'TUESDAY' ? '28.00' : weekend ? '42.00' : '40.00');
  }
  throw new IllegalArgumentError(`unknown format: ${format}`);
}
```

**Po (`ShowPricing.ts`, cały plik; importy bez zmian):**

```typescript
import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { DayOfWeek } from '../../../shared/time.js';

export class ShowPricing {
  price(day: DayOfWeek, format: string): Money {
    return ShowPricing.adjustForDay(day, ShowPricing.basePrice(format));
  }

  private static basePrice(format: string): Money {
    switch (format) {
      case '2D': return Money.of('25.00');
      case '3D': return Money.of('32.00');
      case 'IMAX': return Money.of('40.00');
      default: throw new IllegalArgumentError(`unknown format: ${format}`);
    }
  }

  private static adjustForDay(day: DayOfWeek, base: Money): Money {
    switch (day) {
      case 'TUESDAY': return base.minus(base.percent(30));
      case 'SATURDAY':
      case 'SUNDAY': return base.plus(Money.of('2.00'));
      default: return base;
    }
  }
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s20` - zielone (9 cen i nieznany format).
**Co powiedzieć:** to jest moment decyzji. Ten sam `switch` może sygnalizować różne problemy - pytamy, **która oś zmienia się częściej**.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s20 0 1`

### Krok 2: ścieżka A - zmienia się reguła dnia (Strategy)

**Zmiany:**

1. Utwórz nowy plik `DayPolicy.ts`: typ funkcyjny `export type DayPolicy = (base: Money) => Money`.
2. Utwórz nowy plik `DayPolicies.ts` ze stałymi `CHEAP_TUESDAY`, `WEEKEND`, `REGULAR` (ciała gałęzi `adjustForDay`) i kalendarzem `static standard(day): DayPolicy` ze `switch` po dniu.
3. W `ShowPricing` usuń `adjustForDay`; dodaj pole `calendar: (day: DayOfWeek) => DayPolicy` i konstruktor z parametrem domyślnym - kalendarzem standardowym.
4. `price` woła `this.calendar(day)(ShowPricing.basePrice(format))`. Format zostaje prostym `switch`.

**Przed:** `ShowPricing.ts` - jak w bloku Po kroku 1.

**Po (`DayPolicy.ts`, nowy plik):**

```typescript
import type { Money } from '../../../shared/Money.js';

/** Korekta ceny za dzień jako wymienny algorytm - Strategy. */
export type DayPolicy = (base: Money) => Money;
```

**Po (`DayPolicies.ts`, nowy plik):**

```typescript
import { Money } from '../../../shared/Money.js';
import type { DayOfWeek } from '../../../shared/time.js';
import type { DayPolicy } from './DayPolicy.js';

export class DayPolicies {
  static readonly CHEAP_TUESDAY: DayPolicy = (base) => base.minus(base.percent(30));
  static readonly WEEKEND: DayPolicy = (base) => base.plus(Money.of('2.00'));
  static readonly REGULAR: DayPolicy = (base) => base;

  private constructor() {}

  static standard(day: DayOfWeek): DayPolicy {
    switch (day) {
      case 'TUESDAY': return DayPolicies.CHEAP_TUESDAY;
      case 'SATURDAY':
      case 'SUNDAY': return DayPolicies.WEEKEND;
      default: return DayPolicies.REGULAR;
    }
  }
}
```

**Po (`ShowPricing.ts`, cały plik):**

```typescript
import { IllegalArgumentError } from '../../../../shared/errors.js';
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Money } from '../../../shared/Money.js';
import type { DayOfWeek } from '../../../shared/time.js';
import { DayPolicies } from './DayPolicies.js';
import type { DayPolicy } from './DayPolicy.js';

export class ShowPricing {
  private readonly calendar: (day: DayOfWeek) => DayPolicy;

  constructor(calendar: (day: DayOfWeek) => DayPolicy = (day) => DayPolicies.standard(day)) {
    this.calendar = requireNonNull(calendar, 'calendar');
  }

  price(day: DayOfWeek, format: string): Money {
    return this.calendar(day)(ShowPricing.basePrice(format));
  }

  private static basePrice(format: string): Money {
    switch (format) {
      case '2D': return Money.of('25.00');
      case '3D': return Money.of('32.00');
      case 'IMAX': return Money.of('40.00');
      default: throw new IllegalArgumentError(`unknown format: ${format}`);
    }
  }
}
```

**Uruchom:** test zielony; `pathAMakesANewDayCampaignCheap` dodaje "środę seniora" bez dotykania formatów.
**Co powiedzieć:** wybierz tę ścieżkę, gdy marketing co miesiąc zmienia akcje dniowe, a formaty są stabilne. Zestaw polityk jest otwarty i wstrzykiwany.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s20 1 2`

### Krok 3: ścieżka B - zmienia się zestaw formatów (typ z zachowaniem)

**Zmiany:**

1. Pokaż alternatywę budowaną od kroku 1, nie od kroku 2.
2. Utwórz nowy plik `Format.ts`: klasa z prywatnym konstruktorem, stałymi instancjami `TWO_D`, `THREE_D`, `IMAX` (kod i cena bazowa), `values()` oraz `Format.of(code)` rzucającym ten sam wyjątek co dotąd.
3. Przenieś regułę dnia do metody `priceOn(day)` w `Format` - zostaje zwykłym `switch` w typie.
4. `ShowPricing.price` tylko deleguje: `Format.of(format).priceOn(day)`; metody `basePrice` i `adjustForDay` znikają. Plików ścieżki A (`DayPolicy.ts`, `DayPolicies.ts`) w tej ścieżce nie ma - snapshot `step3/` ich nie zawiera.

**Przed:** `ShowPricing.ts` - jak w bloku Po kroku 1.

**Po (`Format.ts`, nowy plik):**

```typescript
import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { DayOfWeek } from '../../../shared/time.js';

export class Format {
  static readonly TWO_D = new Format('2D', '25.00');
  static readonly THREE_D = new Format('3D', '32.00');
  static readonly IMAX = new Format('IMAX', '40.00');

  private readonly base: Money;

  private constructor(private readonly code: string, base: string) {
    this.base = Money.of(base);
  }

  static values(): readonly Format[] {
    return [Format.TWO_D, Format.THREE_D, Format.IMAX];
  }

  static of(code: string): Format {
    for (const format of Format.values()) {
      if (format.code === code) {
        return format;
      }
    }
    throw new IllegalArgumentError(`unknown format: ${code}`);
  }

  priceOn(day: DayOfWeek): Money {
    switch (day) {
      case 'TUESDAY': return this.base.minus(this.base.percent(30));
      case 'SATURDAY':
      case 'SUNDAY': return this.base.plus(Money.of('2.00'));
      default: return this.base;
    }
  }
}
```

**Po (`ShowPricing.ts`, cały plik):**

```typescript
import type { Money } from '../../../shared/Money.js';
import type { DayOfWeek } from '../../../shared/time.js';
import { Format } from './Format.js';

export class ShowPricing {
  price(day: DayOfWeek, format: string): Money {
    return Format.of(format).priceOn(day);
  }
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

**Zmiany:**

1. Utwórz nowy plik `SettlementModel.ts`: interfejs z metodą `payout(deal, week, ticketRevenue): Decimal`.
2. Gałąź `'PERCENT'` przenieś do nowej klasy `PercentageModel implements SettlementModel` (stała `MINIMUM_GUARANTEE` jako `private static readonly`).
3. Gałąź `'FESTIVAL'` przenieś do nowej klasy `FestivalFeeAdapter implements SettlementModel`: opakowuje `FestivalTariffClient` z konstruktora i konwertuje grosze (`new Decimal(cents).dividedBy(100)`).
4. W `DistributorSettlement` zastąp `switch` mapą `models` i wyszukaniem modelu; nieznany model rzuca ten sam wyjątek.

**Przed (`DistributorSettlement.ts`, cały plik):**

```typescript
import { Decimal } from 'decimal.js';

import { IllegalArgumentError } from '../../../../shared/errors.js';
import type { Deal } from '../Deal.js';
import { FestivalTariffClient } from '../FestivalTariffClient.js';

export class DistributorSettlement {
  private readonly festival = new FestivalTariffClient();

  payout(deal: Deal, week: number, ticketRevenue: Decimal): Decimal {
    switch (deal.model) {
      case 'PERCENT': {
        const percent = week === 1 ? 50 : week === 2 ? 40 : 35;
        const share = ticketRevenue.times(percent)
          .dividedBy(100).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
        return Decimal.max(share, new Decimal('500.00'));
      }
      case 'FESTIVAL': {
        const cents = this.festival.weeklyFeeInCents(deal.title, week);
        return new Decimal(cents).dividedBy(100);
      }
      default:
        throw new IllegalArgumentError(`nieznany model: ${deal.model}`);
    }
  }
}
```

**Po (`SettlementModel.ts`, nowy plik):**

```typescript
import type { Decimal } from 'decimal.js';

import type { Deal } from '../Deal.js';

/** Strategy - wspólny kontrakt modeli rozliczeń. */
export interface SettlementModel {
  payout(deal: Deal, week: number, ticketRevenue: Decimal): Decimal;
}
```

**Po (`PercentageModel.ts`, nowy plik):**

```typescript
import { Decimal } from 'decimal.js';

import type { Deal } from '../Deal.js';
import type { SettlementModel } from './SettlementModel.js';

export class PercentageModel implements SettlementModel {
  private static readonly MINIMUM_GUARANTEE = new Decimal('500.00');

  payout(_deal: Deal, week: number, ticketRevenue: Decimal): Decimal {
    const percent = week === 1 ? 50 : week === 2 ? 40 : 35;
    const share = ticketRevenue.times(percent)
      .dividedBy(100).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
    return Decimal.max(share, PercentageModel.MINIMUM_GUARANTEE);
  }
}
```

**Po (`FestivalFeeAdapter.ts`, nowy plik):**

```typescript
import { Decimal } from 'decimal.js';

import type { Deal } from '../Deal.js';
import type { FestivalTariffClient } from '../FestivalTariffClient.js';
import type { SettlementModel } from './SettlementModel.js';

export class FestivalFeeAdapter implements SettlementModel {
  constructor(private readonly client: FestivalTariffClient) {}

  payout(deal: Deal, week: number, _ticketRevenue: Decimal): Decimal {
    return new Decimal(this.client.weeklyFeeInCents(deal.title, week)).dividedBy(100);
  }
}
```

**Po (`DistributorSettlement.ts`, cały plik):**

```typescript
import type { Decimal } from 'decimal.js';

import { IllegalArgumentError } from '../../../../shared/errors.js';
import type { Deal } from '../Deal.js';
import { FestivalTariffClient } from '../FestivalTariffClient.js';
import { FestivalFeeAdapter } from './FestivalFeeAdapter.js';
import { PercentageModel } from './PercentageModel.js';
import type { SettlementModel } from './SettlementModel.js';

export class DistributorSettlement {
  private readonly models: ReadonlyMap<string, SettlementModel> = new Map<string, SettlementModel>([
    ['PERCENT', new PercentageModel()],
    ['FESTIVAL', new FestivalFeeAdapter(new FestivalTariffClient())],
  ]);

  payout(deal: Deal, week: number, ticketRevenue: Decimal): Decimal {
    const model = this.models.get(deal.model);
    if (model === undefined) {
      throw new IllegalArgumentError(`nieznany model: ${deal.model}`);
    }
    return model.payout(deal, week, ticketRevenue);
  }
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s21` - 18 testów zielonych; `step1AdapterPaysTheFestivalFeeConvertedFromCents` sprawdza 300.00 i 150.00.
**Co powiedzieć:** uzasadnienie to dwa **istniejące** warianty z różnymi właścicielami i obcy interfejs. Adapter ma realną pracę (konwersja jednostek), nie jest pustym przekazaniem 1:1.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s21 0 1`

### Krok 2: Wariant znika - usunięcie adaptera

**Zmiany:**

1. Umowy festiwalowe wygasły: usuń wpis `'FESTIVAL'` z mapy `models`.
2. Usuń plik `FestivalFeeAdapter.ts` - wcześniej upewnij się, że nikt go już nie używa (wyszukaj odwołania w kodzie).
3. Usuń niepotrzebne już importy `FestivalTariffClient` i `FestivalFeeAdapter` z `DistributorSettlement`; reszta pliku bez zmian.

**Przed:**

```typescript
private readonly models: ReadonlyMap<string, SettlementModel> = new Map<string, SettlementModel>([
  ['PERCENT', new PercentageModel()],
  ['FESTIVAL', new FestivalFeeAdapter(new FestivalTariffClient())],
]);
```

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

**Zmiany:**

1. Wklej ciało `PercentageModel.payout` w miejsce wywołania w `DistributorSettlement.payout` (Inline Method).
2. Wybór z mapy zastąp warunkiem `deal.model !== 'PERCENT'` rzucającym ten sam wyjątek; mapę `models` usuń.
3. Stałą `MINIMUM_GUARANTEE` przenieś do `DistributorSettlement` jako `private static readonly`.
4. Usuń pliki `PercentageModel.ts` i `SettlementModel.ts`.

**Przed:**

```typescript
payout(deal: Deal, week: number, ticketRevenue: Decimal): Decimal {
  const model = this.models.get(deal.model);
  if (model === undefined) {
    throw new IllegalArgumentError(`nieznany model: ${deal.model}`);
  }
  return model.payout(deal, week, ticketRevenue);
}
```

**Po (`DistributorSettlement.ts`, cały plik):**

```typescript
import { Decimal } from 'decimal.js';

import { IllegalArgumentError } from '../../../../shared/errors.js';
import type { Deal } from '../Deal.js';

export class DistributorSettlement {
  private static readonly MINIMUM_GUARANTEE = new Decimal('500.00');

  payout(deal: Deal, week: number, ticketRevenue: Decimal): Decimal {
    if (deal.model !== 'PERCENT') {
      throw new IllegalArgumentError(`nieznany model: ${deal.model}`);
    }
    const percent = week === 1 ? 50 : week === 2 ? 40 : 35;
    const share = ticketRevenue.times(percent)
      .dividedBy(100).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
    return Decimal.max(share, DistributorSettlement.MINIMUM_GUARANTEE);
  }
}
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

**Zmiany:**

1. W `SeatBadge` dodaj prywatną metodę `freeSeats(id)` i przenieś do niej `try/catch` z fasady - to jedyny klient, który polega na tłumaczeniu "brak seansu" na 0; `badge` woła `this.freeSeats(id)`.
2. W `CinemaFacade.freeSeats` zostaje zwykłe delegowanie `return this.catalog.freeSeats(id)`; import `NoSuchElementError` przenosi się z `CinemaFacade` do `SeatBadge` (`import { NoSuchElementError } from '../ScreeningCatalog.js'`; w fasadzie zostaje `import { type ScreeningCatalog } from '../ScreeningCatalog.js'`).

**Przed (`CinemaFacade.ts`, metoda `freeSeats`):**

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

**Przed (`SeatBadge.ts`, metoda `badge`):**

```typescript
badge(id: string): string {
  const free = this.cinema.freeSeats(id);
  if (free === 0) {
    return `${id}: WYPRZEDANE`;
  }
  return `${this.cinema.title(id)} (${this.cinema.format(id)}): ${free} wolnych`;
}
```

**Po (`CinemaFacade.ts`, metoda `freeSeats`):**

```typescript
freeSeats(id: string): number {
  return this.catalog.freeSeats(id);
}
```

**Po (`SeatBadge.ts`, metody `badge` i `freeSeats`):**

```typescript
badge(id: string): string {
  const free = this.freeSeats(id);
  if (free === 0) {
    return `${id}: WYPRZEDANE`;
  }
  return `${this.cinema.title(id)} (${this.cinema.format(id)}): ${free} wolnych`;
}

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

**Zmiany:**

1. W `SeatBadge` zmień typ pola i parametru konstruktora z `CinemaFacade` na `ScreeningCatalog`.
2. Zmień nazwę pola `cinema` na `catalog` we wszystkich użyciach (Rename).
3. Metody `title`, `format` i `freeSeats` katalog ma o tych samych sygnaturach, więc reszta kodu się kompiluje bez zmian.

**Przed:** `SeatBadge.ts` - jak w blokach Po kroku 1 (pole `cinema: CinemaFacade` i konstruktor jak w start).

**Po (`SeatBadge.ts`, cały plik):**

```typescript
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { NoSuchElementError, type ScreeningCatalog } from '../ScreeningCatalog.js';

export class SeatBadge {
  private readonly catalog: ScreeningCatalog;

  constructor(catalog: ScreeningCatalog) {
    this.catalog = requireNonNull(catalog, 'catalog');
  }

  badge(id: string): string {
    const free = this.freeSeats(id);
    if (free === 0) {
      return `${id}: WYPRZEDANE`;
    }
    return `${this.catalog.title(id)} (${this.catalog.format(id)}): ${free} wolnych`;
  }

  private freeSeats(id: string): number {
    try {
      return this.catalog.freeSeats(id);
    } catch (error) {
      if (error instanceof NoSuchElementError) {
        return 0;
      }
      throw error;
    }
  }
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** jeden klient naraz. `DailyBoard` wciąż używa fasady i działa.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s22 1 2`

### Krok 3: ostatni klient i usunięcie fasady

**Zmiany:**

1. Ten sam ruch w `DailyBoard`: typ konstruktora na `ScreeningCatalog`, pole `cinema` na `catalog`, a wywołanie `cinema.screenings()` zamień na `catalog.all()`.
2. Wyszukaj odwołania do `CinemaFacade` - brak użyć - i usuń plik `CinemaFacade.ts`.

**Przed (`DailyBoard.ts`, cały plik):**

```typescript
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { CinemaFacade } from './CinemaFacade.js';

export class DailyBoard {
  private readonly cinema: CinemaFacade;

  constructor(cinema: CinemaFacade) {
    this.cinema = requireNonNull(cinema, 'cinema');
  }

  render(): string {
    return this.cinema.screenings()
      .map((screening) => `${screening.id} ${screening.title} ${screening.format}`)
      .join('\n');
  }
}
```

**Po (`DailyBoard.ts`, cały plik):**

```typescript
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { ScreeningCatalog } from '../ScreeningCatalog.js';

export class DailyBoard {
  private readonly catalog: ScreeningCatalog;

  constructor(catalog: ScreeningCatalog) {
    this.catalog = requireNonNull(catalog, 'catalog');
  }

  render(): string {
    return this.catalog.all()
      .map((screening) => `${screening.id} ${screening.title} ${screening.format}`)
      .join('\n');
  }
}
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

**Zmiany:**

1. Wydziel łańcuch `if/else` wyboru udziału do metody `private static share(minutesBeforeStart): number` (Extract Method): zamień przypisania do `refund` na `return 0`, `return 1.0`, `return 0.5`.
2. W `refund` zostaje jedno wyrażenie: `let refund = ticketsPaid * RefundCalculator.share(minutesBeforeStart) - 3.00;` - reszta metody bez zmian.

**Przed (`RefundCalculator.ts`, metoda `refund`):**

```typescript
refund(ticketsPaid: number, minutesBeforeStart: number): string {
  let refund: number;
  if (minutesBeforeStart <= 0) {
    refund = 0;
  } else if (minutesBeforeStart >= 24 * 60) {
    refund = ticketsPaid;
  } else {
    refund = ticketsPaid * 0.5;
  }
  refund = refund - 3.00;
  if (refund < 0) {
    refund = 0;
  }
  refund = Math.round(refund * 100) / 100.0;
  return refund.toFixed(2);
}
```

**Po (`RefundCalculator.ts`, metody `refund` i `share`):**

```typescript
refund(ticketsPaid: number, minutesBeforeStart: number): string {
  let refund = ticketsPaid * RefundCalculator.share(minutesBeforeStart) - 3.00;
  if (refund < 0) {
    refund = 0;
  }
  refund = Math.round(refund * 100) / 100.0;
  return refund.toFixed(2);
}

private static share(minutesBeforeStart: number): number {
  if (minutesBeforeStart <= 0) {
    return 0;
  }
  if (minutesBeforeStart >= 24 * 60) {
    return 1.0;
  }
  return 0.5;
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s23` - 19 testów zielonych, łącznie z przypadkami brzegowymi (29.17 i 0.00).
**Co powiedzieć:** `x * 1.0 === x` w arytmetyce zmiennoprzecinkowej - wynik jest identyczny co do bitu. To jest refaktoryzacja.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s23 0 1`

### Krok 2: "przy okazji" number na Decimal

**Zmiany:**

1. Pokaż naiwny ruch: `share` zwraca `Decimal` (`new Decimal(0)`, `new Decimal(1)`, `new Decimal('0.5')`), dodaj stałą `FEE = new Decimal('3.00')`.
2. W `refund` licz `new Decimal(ticketsPaid).times(...).minus(RefundCalculator.FEE)` i formatuj `toFixed(2, Decimal.ROUND_HALF_UP)`.
3. "Przy okazji" skróć obcięcie do zera do guard clause `if (refund.lessThanOrEqualTo(0)) return '0';`.

**Przed:** `RefundCalculator.ts` - jak w bloku Po kroku 1.

**Po (`RefundCalculator.ts`, cały plik):**

```typescript
import { Decimal } from 'decimal.js';

export class RefundCalculator {
  private static readonly FEE = new Decimal('3.00');

  refund(ticketsPaid: number, minutesBeforeStart: number): string {
    const refund = new Decimal(ticketsPaid)
      .times(RefundCalculator.share(minutesBeforeStart))
      .minus(RefundCalculator.FEE);
    if (refund.lessThanOrEqualTo(0)) {
      return '0';
    }
    return refund.toFixed(2, Decimal.ROUND_HALF_UP);
  }

  private static share(minutesBeforeStart: number): Decimal {
    if (minutesBeforeStart <= 0) {
      return new Decimal(0);
    }
    if (minutesBeforeStart >= 24 * 60) {
      return new Decimal(1);
    }
    return new Decimal('0.5');
  }
}
```

**Uruchom:** test zwykłych zwrotów zielony, ale `byTheWayStepChangedTwoThingsAtOnce` dokumentuje dwie zmiany: 64.35 zapłacone, anulowanie 2 h przed seansem daje 29.18 zamiast 29.17, a zwrot po starcie to "0" zamiast "0.00".
**Co powiedzieć:** krok wyglądał na porządki, a zmienił dwa elementy kontraktu. Pierwszy (grosz) można uzasadnić regułą domeny, drugi (format) to czysta regresja - paragon i mail pokażą "Zwrot: 0".
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s23 1 2`

### Krok 3: świadoma decyzja

**Zmiany:**

1. Zastąp guard clause obcięciem `Decimal.max(..., 0)` przed formatowaniem - jedna ścieżka wyniku i format "0.00" wraca.
2. Różnicę groszową zatwierdź jako zmianę kontraktu: komentarz TSDoc z decyzją, nowe oczekiwanie w teście, osobny commit.

**Przed:**

```typescript
const refund = new Decimal(ticketsPaid)
  .times(RefundCalculator.share(minutesBeforeStart))
  .minus(RefundCalculator.FEE);
if (refund.lessThanOrEqualTo(0)) {
  return '0';
}
return refund.toFixed(2, Decimal.ROUND_HALF_UP);
```

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

**Zasada:** Rename nadaje nazwę opisującą rolę w kontekście, a nie typ ani implementację. Dla zmiennych lokalnych IDE robi to bezpiecznie, ale nazwa użyta w konfiguracji, wywołaniu po nazwie (`obj[name]`), JSON czy ORM jest częścią API i wymaga strategii migracji. IDE nie widzi takich użyć, więc nie mylić automatycznej zmiany nazwy z bezpieczną zmianą kontraktu.

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

**Zmiany:**

1. Zmień nazwy parametrów `calc2` (Rename): `s` -> `sales`, `flag` -> `onlineOnly`.
2. Zmień nazwy zmiennych lokalnych: `m` -> `linesByTitle`, `x` -> `sale`, `l` -> `previous` (w pętli agregującej) i `line` (w pętli wypisującej), `b` -> `csv`.
3. W funkcji pomocniczej `row`: `l` -> `line`, `v` -> `values`, `c` -> `component`.

**Przed (`SalesReport.ts`, metoda `calc2` i funkcja `row`):**

```typescript
calc2(s: readonly Sale[], flag: boolean): string {
  const m = new Map<string, Line>();
  for (const x of s) {
    if (flag && !x.online) {
      continue;
    }
    const l = m.get(x.title);
    if (l === undefined) {
      m.set(x.title, new Line(x.title, x.tickets, x.amount));
    } else {
      m.set(x.title, new Line(x.title, l.n + x.tickets, l.d.plus(x.amount)));
    }
  }
  let b = header() + '\n';
  for (const l of sortedByKey(m)) {
    b += row(l) + '\n';
  }
  return b;
}
```

```typescript
function row(l: Line): string {
  const v: string[] = [];
  for (const c of Object.values(l)) {
    v.push(String(c));
  }
  return v.join(';');
}
```

**Po (`SalesReport.ts`, metoda `calc2` i funkcja `row`):**

```typescript
calc2(sales: readonly Sale[], onlineOnly: boolean): string {
  const linesByTitle = new Map<string, Line>();
  for (const sale of sales) {
    if (onlineOnly && !sale.online) {
      continue;
    }
    const previous = linesByTitle.get(sale.title);
    if (previous === undefined) {
      linesByTitle.set(sale.title, new Line(sale.title, sale.tickets, sale.amount));
    } else {
      linesByTitle.set(sale.title, new Line(sale.title,
        previous.n + sale.tickets, previous.d.plus(sale.amount)));
    }
  }
  let csv = header() + '\n';
  for (const line of sortedByKey(linesByTitle)) {
    csv += row(line) + '\n';
  }
  return csv;
}
```

```typescript
function row(line: Line): string {
  const values: string[] = [];
  for (const component of Object.values(line)) {
    values.push(String(component));
  }
  return values.join(';');
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s24` - 14 testów zielonych.
**Co powiedzieć:** zasięg lokalny, zero użyć poza metodą, więc IDE robi to w stu procentach bezpiecznie. Od tego zaczynamy każde porządkowanie, bo tanie nazwy ułatwiają następne kroki.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s24 0 1`

### Krok 2: Rename metody z delegatem dla starej nazwy

**Zmiany:**

1. Najpierw na żywo naiwnie: zmień nazwę `calc2` na `revenueCsv` (Rename). Wszystkie wywołania w kodzie się poprawią, ale nie tekst `report.method=calc2` w konfiguracji.
2. Uruchom test: `everyStepRunsTheConfiguredJob` jest czerwony (`IllegalStateError: Zadanie raportu nie działa: brak metody calc2`) - narzędzie nie wie, że tekst w konfiguracji to nazwa metody. Cofnij zmianę.
3. Teraz poprawnie: skopiuj metodę, nazwij kopię `revenueCsv`, a ciało `calc2` zamień na delegowanie `return this.revenueCsv(sales, onlineOnly)` i oznacz w JSDoc `@deprecated`.

**Przed:** metoda `calc2` - jak w bloku Po kroku 1.

**Po (`SalesReport.ts`, metody `calc2` i `revenueCsv`; ciało `revenueCsv` to dotychczasowe ciało `calc2` bez zmian):**

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
  const linesByTitle = new Map<string, Line>();
  for (const sale of sales) {
    if (onlineOnly && !sale.online) {
      continue;
    }
    const previous = linesByTitle.get(sale.title);
    if (previous === undefined) {
      linesByTitle.set(sale.title, new Line(sale.title, sale.tickets, sale.amount));
    } else {
      linesByTitle.set(sale.title, new Line(sale.title,
        previous.n + sale.tickets, previous.d.plus(sale.amount)));
    }
  }
  let csv = header() + '\n';
  for (const line of sortedByKey(linesByTitle)) {
    csv += row(line) + '\n';
  }
  return csv;
}
```

**Uruchom:** test zielony, zadanie z konfiguracji działa. `S24RenameLimitsTest.configurationStillNamesTheOldMethod` sprawdza, że na prototypie są obie metody.
**Co powiedzieć:** konfiguracja leży na serwerach, poza naszym repozytorium, więc nie zmienimy jej w tym samym commicie. Stara nazwa zostaje jako przestarzały delegat, dopóki ktoś nie zmieni konfiguracji wszędzie. To jest strategia migracji ze slajdu. Wywołania `calc2` w kodzie są odtąd oznaczone jako przestarzałe, więc nowi klienci od razu widzą, której nazwy używać.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s24 1 2`

### Krok 3: Rename pól wiersza po odcięciu kontraktu CSV

**Zmiany:**

1. Najpierw naiwnie: zmień nazwę pola `t` w `Line` na `title`. Test pokaże nagłówek `title;n;d` zamiast `t;n;d` - plik dla dystrybutora właśnie się zmienił. Cofnij zmianę.
2. Teraz poprawnie: najpierw odetnij kontrakt - zamień `header()` oparte na `Object.keys` na jawną stałą `export const CSV_HEADER = 't;n;d'` (funkcja `header()` znika, a w `revenueCsv` wiersz `let csv = header() + '\n'` staje się `let csv = CSV_HEADER + '\n'`), a `row()` oparte na `Object.values` na jawne składanie wiersza (test zielony).
3. Dopiero potem zmień nazwy pól `t`, `n`, `d` na `title`, `tickets`, `revenue` (Rename) - razem z użyciami w `revenueCsv` (gałąź `else` agregacji) i `row`. Import `Money` w `SalesReport.ts` staje się `import type { Money }`, bo po usunięciu `header()` nikt nie tworzy `Money.ZERO`.

**Przed:**

```typescript
export class Line {
  constructor(readonly t: string, readonly n: number, readonly d: Money) {}
}

function header(): string {
  return Object.keys(new Line('', 0, Money.ZERO)).join(';');
}

function row(line: Line): string {
  const values: string[] = [];
  for (const component of Object.values(line)) {
    values.push(String(component));
  }
  return values.join(';');
}
```

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

**Po (`SalesReport.ts`, zmienione wiersze `revenueCsv`):**

```typescript
linesByTitle.set(sale.title, new Line(sale.title,
  previous.tickets + sale.tickets, previous.revenue.plus(sale.amount)));
```

```typescript
let csv = CSV_HEADER + '\n';
```

**Uruchom:** test zielony, także `S24RenameLimitsTest.reflectionTurnsJavaNamesIntoTheCsvHeader`, który pokazuje, że `Object.keys` na nowej klasie dałoby `title;tickets;revenue`.
**Co powiedzieć:** nazwa w kodzie i nazwa w kontrakcie zewnętrznym to dwie różne rzeczy. Dopóki są sklejone przez `Object.keys`, każdy Rename jest zmianą API. To samo dotyczy `JSON.stringify` - nazwy pól obiektu stają się kluczami w JSON.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s24 2 3`

### Rozwiązanie i uzasadnienie

`step3`: wszystkie nazwy mówią o roli (`revenueCsv`, `onlineOnly`, `linesByTitle`, `Line.revenue`), a dwa kontrakty zewnętrzne (nazwa metody w konfiguracji i nagłówek CSV) są zachowane jawnie: przez delegat i przez stałą. Oba testy równoważności (wywołanie z kodu i zadanie z konfiguracji) są zielone na każdym etapie.

### Pułapki

- Poleganie na tym, że automatyczny Rename "znajdzie wszystko". Działa na typach i nie zmienia tekstu w stringach, a globalne szukaj-zamień nie widzi plików poza projektem ani nazw składanych dynamicznie (`'calc' + version`).
- Inne miejsca, w których nazwa jest kontraktem: `JSON.stringify` / `JSON.parse` (klucze obiektu), kolumny ORM, szablony, nazwy pól w odpowiedziach REST, dostęp `obj[name]`, logi parsowane przez monitoring.
- Usunięcie delegatu `calc2` "bo nikt go nie woła". Wyszukiwanie odwołań w kodzie go nie znajdzie, bo woła go tylko konfiguracja przez `report[name]`.

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

**Różnica względem Javy:** IDE w Javie odmawia wydzielenia fragmentu z dwiema wartościami wyjściowymi. Narzędzia TypeScriptu nie odmawiają: wydzielona metoda zwraca wtedy obiekt `{ subtotal, vipSeats }`, a w miejscu wywołania pojawia się destrukturyzacja. Kod się kompiluje i test jest zielony, ale to dokładnie "holder" z pułapek - dwie odpowiedzialności w jednej metodzie. Kwoty to `Decimal`, w wydruku `toFixed(2)`.

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

**Zmiany:**

1. Wydziel blok pod `// cena bazowa formatu` (od `let base: Decimal;` do końca `if` z porankiem) do prywatnej metody `basePrice(order): Decimal` (Extract Method). Blok ma jedno wejście (`order`) i jedno wyjście (`base`), więc to najprostszy przypadek.
2. W `describe` zostaje `const base = this.basePrice(order);`. Usuń zbędny komentarz.

**Przed:**

```typescript
// cena bazowa formatu
let base: Decimal;
if (order.format === 'IMAX') {
  base = new Decimal('40.00');
} else if (order.format === '3D') {
  base = new Decimal('32.00');
} else {
  base = new Decimal('25.00');
}
if (order.start.hour < 12) {
  base = base.minus(new Decimal('5.00'));
}
```

**Po (w `describe` cały blok zastępuje jeden wiersz, reszta metody bez zmian; niżej nowa metoda `basePrice`):**

```typescript
const base = this.basePrice(order);
```

```typescript
private basePrice(order: Order): Decimal {
  let base: Decimal;
  if (order.format === 'IMAX') {
    base = new Decimal('40.00');
  } else if (order.format === '3D') {
    base = new Decimal('32.00');
  } else {
    base = new Decimal('25.00');
  }
  if (order.start.hour < 12) {
    base = base.minus(new Decimal('5.00'));
  }
  return base;
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s25` - 16 testów zielonych.
**Co powiedzieć:** blok z jednym wyjściem to najprostszy przypadek, IDE robi to bezpiecznie. Komentarz stał się nazwą metody.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s25 0 1`

### Krok 2: Split Loop, potem dwa razy Extract Method

**Zmiany:**

1. Pokaż najpierw propozycję automatycznej ekstrakcji pętli razem z deklaracjami `subtotal` i `vipSeats`: wyszłaby metoda zwracająca obiekt `{ subtotal, vipSeats }`. To moment dydaktyczny - narzędzie nie widzi, że to dwie odpowiedzialności. Zrezygnuj z tej propozycji.
2. Skopiuj pętlę (Split Loop): z pierwszej kopii usuń zliczanie `vipSeats`, z drugiej sumowanie `subtotal` (i zmienną `price`). Uruchom test.
3. Wydziel pierwszą pętlę do `private subtotal(order, base): Decimal` (razem z zaokrągleniem), drugą do `private vipSeats(order): number` (Extract Method).

**Przed:**

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

**Po (w `describe` pętlę i deklaracje zastępują dwa wiersze, reszta metody bez zmian; niżej nowe metody):**

```typescript
const subtotal = this.subtotal(order, base);
const vipSeats = this.vipSeats(order);
```

```typescript
private subtotal(order: Order, base: Decimal): Decimal {
  let subtotal = new Decimal(0);
  for (const row of order.rows) {
    let price = base;
    if (row >= 10) {
      price = price.plus(new Decimal('10.00'));
    }
    subtotal = subtotal.plus(price);
  }
  return subtotal.toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
}

private vipSeats(order: Order): number {
  let vipSeats = 0;
  for (const row of order.rows) {
    if (row >= 10) {
      vipSeats++;
    }
  }
  return vipSeats;
}
```

**Uruchom:** test po Split Loop i po każdej ekstrakcji.
**Co powiedzieć:** zamiast zwracać parę (obiekt z dwoma polami) rozdzielamy odpowiedzialności. Dwa przejścia po liście to koszt pomijalny wobec czytelności. Jeśli wydajność miałaby znaczenie, mierzymy, a nie zgadujemy.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s25 1 2`

### Krok 3: Extract Method dla renderowania

**Zmiany:**

1. Wydziel blok pod `// dokument` do prywatnej metody `render(order, subtotal, vipSeats): string` (Extract Method).
2. Wstaw zmienną `base` w jej jedyne użycie: `this.subtotal(order, this.basePrice(order))` (Inline Variable).

**Przed (`TicketSummary.ts`, metoda `describe`):**

```typescript
describe(order: Order): string {
  const base = this.basePrice(order);
  const subtotal = this.subtotal(order, base);
  const vipSeats = this.vipSeats(order);

  // dokument
  let text = '';
  text += 'BILETY: ' + order.title + '\n';
  text += 'Format: ' + order.format
    + ', start ' + order.start.toString() + '\n';
  text += 'Miejsc: ' + order.rows.length;
  if (vipSeats > 0) {
    text += ' (w tym VIP: ' + vipSeats + ')';
  }
  text += '\n';
  text += 'Razem: ' + subtotal.toFixed(2) + '\n';
  return text;
}
```

**Po (metoda `describe` i nowa metoda `render`):**

```typescript
describe(order: Order): string {
  const subtotal = this.subtotal(order, this.basePrice(order));
  const vipSeats = this.vipSeats(order);
  return this.render(order, subtotal, vipSeats);
}

private render(order: Order, subtotal: Decimal, vipSeats: number): string {
  let text = '';
  text += 'BILETY: ' + order.title + '\n';
  text += 'Format: ' + order.format
    + ', start ' + order.start.toString() + '\n';
  text += 'Miejsc: ' + order.rows.length;
  if (vipSeats > 0) {
    text += ' (w tym VIP: ' + vipSeats + ')';
  }
  text += '\n';
  text += 'Razem: ' + subtotal.toFixed(2) + '\n';
  return text;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** metoda publiczna czyta się jak spis treści. Obliczenia są oddzielone od prezentacji, więc przygotowaliśmy grunt pod Extract Class.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s25 2 3`

### Rozwiązanie i uzasadnienie

`step3/TicketSummary.ts`: cztery prywatne metody, każda z jednym wyjściem. Nie zmieniliśmy sygnatury publicznej ani formatu dokumentu, co potwierdza test równoważności na czterech przypadkach (w tym zamówienie puste i same miejsca VIP).

### Pułapki

- Ekstrakcja bloku, który modyfikuje zmienną lokalną używaną dalej, bez zwrócenia jej wartości. IDE tego pilnuje, ręczne kopiowanie nie.
- Zwracanie tablicy (`[subtotal, vipSeats]`) albo anonimowego obiektu tylko po to, żeby wyciągnąć dwa wyniki. Automatyczna ekstrakcja zrobi to sama, jeśli jej pozwolimy.
- Ekstrakcja fragmentu z `this` do funkcji w zasięgu modułu zamiast do metody klasy. Narzędzie zaproponuje oba warianty, a wybór zmienia miejsce i dostęp do pól.
- Nazwa metody opisująca "jak" (`loopRows`) zamiast "co" (`vipSeats`), albo pozostawiona domyślna nazwa z ekstrakcji (`newMethod`).

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

**Zmiany:**

1. Utwórz nowy plik `TicketPricing.ts`: interfejs `TicketPricing` z jedną metodą `price(formatBase: Money): Money`.
2. Utwórz nowy plik `TicketPricings.ts`: mapa kod typu - strategia (literały obiektów, wspólna funkcja `discounted(percent)`) i `static forType(code)`, które dla nieznanego kodu rzuca `unknown ticket type: X` jak start.
3. W pętli `book` zastąp wywołanie `priceOf` (wewnętrzny `switch`) wywołaniem strategii: `TicketPricings.forType(type).price(base)`; w `BoxOfficeDesk.ts` dochodzi `import { TicketPricings } from './TicketPricings.js'`.
4. Usuń `priceOf` - wcześniej upewnij się, że nie ma innych użyć (wyszukaj odwołania).

**Przed (`BoxOfficeDesk.ts`, metoda `priceOf` i pętla w `book`):**

```typescript
for (const type of args.slice(3)) {
  total = total.plus(BoxOfficeDesk.priceOf(type, base));
}
```

```typescript
private static priceOf(type: string, base: Money): Money {
  switch (type) {
    case 'N':
      return base;
    case 'S':
      return base.minus(base.percent(25));
    case 'E':
      return base.minus(base.percent(30));
    case 'C':
      return base.minus(base.percent(40));
    default:
      throw new IllegalArgumentError(`unknown ticket type: ${type}`);
  }
}
```

**Po (`TicketPricing.ts`, nowy plik):**

```typescript
import type { Money } from '../../../shared/Money.js';

/** Strategy - wymienny algorytm ceny jednego biletu. */
export interface TicketPricing {
  price(formatBase: Money): Money;
}
```

**Po (`TicketPricings.ts`, nowy plik):**

```typescript
import { IllegalArgumentError } from '../../../../shared/errors.js';
import type { Money } from '../../../shared/Money.js';
import type { TicketPricing } from './TicketPricing.js';

function discounted(percent: number): TicketPricing {
  return { price: (base) => base.minus(base.percent(percent)) };
}

export class TicketPricings {
  private static readonly BY_TYPE: ReadonlyMap<string, TicketPricing> = new Map<string, TicketPricing>([
    ['N', { price: (base: Money) => base }],
    ['S', discounted(25)],
    ['E', discounted(30)],
    ['C', discounted(40)],
  ]);

  private constructor() {}

  static forType(code: string): TicketPricing {
    const pricing = TicketPricings.BY_TYPE.get(code);
    if (pricing === undefined) {
      throw new IllegalArgumentError(`unknown ticket type: ${code}`);
    }
    return pricing;
  }
}
```

**Po (`BoxOfficeDesk.ts`, pętla w `book` - jedyna zmiana poza usunięciem `priceOf`):**

```typescript
for (const type of args.slice(3)) {
  total = total.plus(TicketPricings.forType(type).price(base));
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s26` - zielone.
**Co powiedzieć:** cenę wybiera klient na podstawie danych wejściowych, bez historii i bez stanu - to Strategy. Strategia zna tylko cenę bazową formatu, nic o rezerwacji ani o operacji kasjera. Strategie są bezstanowe, więc `forType('S')` zwraca zawsze tę samą instancję.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s26 0 1`

### Krok 2: Command dla operacji kasjera

**Zmiany:**

1. Utwórz nowy plik `BookingLedger.ts` (klasa-odbiorca) i przenieś do niego pola `statuses`, `amounts`, `till` z metodami `open`, `remove`, `status`, `pay`, `use`, `cancel`, `restore`, `deposit`, `withdraw`, `summary` (Extract Delegate, ręcznie).
2. Utwórz nowy plik `DeskCommand.ts`: interfejs `DeskCommand` z `execute(ledger)`, `undo(ledger)`, `journalEntry()` oraz eksportowaną funkcją pomocniczą `idFrom(args, operation)`.
3. Utwórz po jednej klasie na operację: `BookCommand`, `PayCommand`, `UseCommand`, `CancelCommand` - każda `implements DeskCommand`, ze statyczną fabryką `from(args)`.
4. Pulpit dostaje rejestr `ReadonlyMap<string, CommandFactory>` (`CommandFactory = (args) => DeskCommand`) i historię `DeskCommand[]` zamiast obiektów `Entry`; klasa `Entry` oraz metody `book`, `change` i `restore` znikają.
5. `undo()` zdejmuje ostatnie polecenie z historii i woła `last.undo(this.ledger)`; `report()` składa dziennik z `journalEntry()`.

**Przed (`BoxOfficeDesk.ts`, metoda `undo`; pozostałe metody i pola - kod kroku 1):**

```typescript
private undo(): string {
  const last = this.journal.pop();
  if (last === undefined) {
    throw new IllegalStateError('nothing to undo');
  }
  switch (last.operation) {
    case 'BOOK':
      this.statuses.delete(last.id);
      this.amounts.delete(last.id);
      break;
    case 'PAY':
      this.till = this.till.minus(last.moved);
      this.restore(last);
      break;
    case 'CANCEL':
      this.till = this.till.plus(last.moved);
      this.restore(last);
      break;
    default:
      this.restore(last);
  }
  return `Cofnieto: ${last.operation} ${last.id}`;
}
```

**Po (`DeskCommand.ts`, nowy plik):**

```typescript
import { IllegalArgumentError } from '../../../../shared/errors.js';
import type { BookingLedger } from './BookingLedger.js';

/** Command - żądanie kasjera jako obiekt. */
export interface DeskCommand {
  execute(ledger: BookingLedger): string;

  undo(ledger: BookingLedger): void;

  journalEntry(): string;
}

/** Wspólna walidacja poleceń "OPERACJA <id>". */
export function idFrom(args: readonly string[], operation: string): string {
  const id = args[1];
  if (id === undefined) {
    throw new IllegalArgumentError(`${operation} <id>`);
  }
  return id;
}
```

**Po (`BookingLedger.ts`, nowy plik):**

```typescript
import { IllegalArgumentError, IllegalStateError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import { Status } from '../Status.js';

/** Odbiorca poleceń - rezerwacje i kasa wydzielone z pulpitu (Extract Class). */
export class BookingLedger {
  private readonly statuses = new Map<string, Status>();
  private readonly amounts = new Map<string, Money>();
  private till = Money.ZERO;

  open(id: string, amount: Money): void {
    if (this.statuses.has(id)) {
      throw new IllegalArgumentError(`booking exists: ${id}`);
    }
    this.statuses.set(id, Status.NEW);
    this.amounts.set(id, amount);
  }

  remove(id: string): void {
    this.statuses.delete(id);
    this.amounts.delete(id);
  }

  status(id: string): Status {
    const status = this.statuses.get(id);
    if (status === undefined) {
      throw new IllegalArgumentError(`no booking ${id}`);
    }
    return status;
  }

  pay(id: string): Money {
    const status = this.status(id);
    if (status !== Status.NEW) {
      throw BookingLedger.invalid('pay', status);
    }
    const amount = this.amountOf(id);
    this.till = this.till.plus(amount);
    this.statuses.set(id, Status.PAID);
    return amount;
  }

  use(id: string): void {
    const status = this.status(id);
    if (status !== Status.PAID) {
      throw BookingLedger.invalid('use', status);
    }
    this.statuses.set(id, Status.USED);
  }

  cancel(id: string): Money {
    const status = this.status(id);
    if (status === Status.NEW) {
      this.statuses.set(id, Status.CANCELLED);
      return Money.ZERO;
    } else if (status === Status.PAID) {
      const amount = this.amountOf(id);
      this.till = this.till.minus(amount);
      this.statuses.set(id, Status.CANCELLED);
      return amount;
    }
    throw BookingLedger.invalid('cancel', status);
  }

  restore(id: string, previous: Status): void {
    this.statuses.set(id, previous);
  }

  deposit(amount: Money): void {
    this.till = this.till.plus(amount);
  }

  withdraw(amount: Money): void {
    this.till = this.till.minus(amount);
  }

  summary(): string {
    const bookings = this.statuses.size === 0 ? '-' : [...this.statuses]
      .map(([id, status]) => `${id}=${status}`)
      .join(', ');
    return `Kasa: ${this.till.toString()}; ${bookings}`;
  }

  private amountOf(id: string): Money {
    return this.amounts.get(id) ?? Money.ZERO;
  }

  private static invalid(action: string, status: Status): IllegalStateError {
    return new IllegalStateError(`cannot ${action} in ${status}`);
  }
}
```

**Po (`BookCommand.ts`, nowy plik):**

```typescript
import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { BookingLedger } from './BookingLedger.js';
import type { DeskCommand } from './DeskCommand.js';
import { TicketPricings } from './TicketPricings.js';

/** BOOK jako polecenie. Cenę liczą strategie z kroku 1, cofnięcie usuwa rezerwację. */
export class BookCommand implements DeskCommand {
  private static readonly FORMATS: ReadonlyMap<string, Money> = new Map([
    ['2D', Money.of('25.00')],
    ['3D', Money.of('32.00')],
    ['IMAX', Money.of('40.00')],
  ]);

  private readonly ticketTypes: readonly string[];

  private constructor(private readonly id: string, private readonly format: string, ticketTypes: readonly string[]) {
    this.ticketTypes = Object.freeze([...ticketTypes]);
  }

  static from(args: readonly string[]): BookCommand {
    if (args.length < 4) {
      throw new IllegalArgumentError('BOOK <id> <format> <typ>...');
    }
    return new BookCommand(args[1] ?? '', args[2] ?? '', args.slice(3));
  }

  execute(ledger: BookingLedger): string {
    const base = BookCommand.FORMATS.get(this.format);
    if (base === undefined) {
      throw new IllegalArgumentError(`unknown format: ${this.format}`);
    }
    let total = Money.ZERO;
    for (const type of this.ticketTypes) {
      total = total.plus(TicketPricings.forType(type).price(base));
    }
    ledger.open(this.id, total);
    return `${this.id} NEW ${total.toString()}`;
  }

  undo(ledger: BookingLedger): void {
    ledger.remove(this.id);
  }

  journalEntry(): string {
    return `BOOK ${this.id}`;
  }
}
```

**Po (`PayCommand.ts`, nowy plik):**

```typescript
import { Money } from '../../../shared/Money.js';
import { Status } from '../Status.js';
import type { BookingLedger } from './BookingLedger.js';
import { type DeskCommand, idFrom } from './DeskCommand.js';

/** PAY jako polecenie - pamięta poprzedni status i pobraną kwotę, żeby je odwrócić. */
export class PayCommand implements DeskCommand {
  private previous = Status.NEW;
  private paid = Money.ZERO;

  private constructor(private readonly id: string) {}

  static from(args: readonly string[]): PayCommand {
    return new PayCommand(idFrom(args, 'PAY'));
  }

  execute(ledger: BookingLedger): string {
    this.previous = ledger.status(this.id);
    this.paid = ledger.pay(this.id);
    return `${this.id} PAID ${this.paid.toString()}`;
  }

  undo(ledger: BookingLedger): void {
    ledger.withdraw(this.paid);
    ledger.restore(this.id, this.previous);
  }

  journalEntry(): string {
    return `PAY ${this.id}`;
  }
}
```

**Po (`UseCommand.ts`, nowy plik):**

```typescript
import { Status } from '../Status.js';
import type { BookingLedger } from './BookingLedger.js';
import { type DeskCommand, idFrom } from './DeskCommand.js';

/** USE jako polecenie - cofnięcie przywraca tylko poprzedni status. */
export class UseCommand implements DeskCommand {
  private previous = Status.PAID;

  private constructor(private readonly id: string) {}

  static from(args: readonly string[]): UseCommand {
    return new UseCommand(idFrom(args, 'USE'));
  }

  execute(ledger: BookingLedger): string {
    this.previous = ledger.status(this.id);
    ledger.use(this.id);
    return `${this.id} USED`;
  }

  undo(ledger: BookingLedger): void {
    ledger.restore(this.id, this.previous);
  }

  journalEntry(): string {
    return `USE ${this.id}`;
  }
}
```

**Po (`CancelCommand.ts`, nowy plik):**

```typescript
import { Money } from '../../../shared/Money.js';
import { Status } from '../Status.js';
import type { BookingLedger } from './BookingLedger.js';
import { type DeskCommand, idFrom } from './DeskCommand.js';

/** CANCEL jako polecenie. Ile oddać, decyduje odbiorca; polecenie zapamiętuje wynik. */
export class CancelCommand implements DeskCommand {
  private previous = Status.NEW;
  private refunded = Money.ZERO;

  private constructor(private readonly id: string) {}

  static from(args: readonly string[]): CancelCommand {
    return new CancelCommand(idFrom(args, 'CANCEL'));
  }

  execute(ledger: BookingLedger): string {
    this.previous = ledger.status(this.id);
    this.refunded = ledger.cancel(this.id);
    return `${this.id} CANCELLED${this.refunded.isGreaterThan(Money.ZERO) ? ` zwrot ${this.refunded.toString()}` : ''}`;
  }

  undo(ledger: BookingLedger): void {
    ledger.deposit(this.refunded);
    ledger.restore(this.id, this.previous);
  }

  journalEntry(): string {
    return `CANCEL ${this.id}`;
  }
}
```

**Po (`BoxOfficeDesk.ts`, cały plik):**

```typescript
import { IllegalArgumentError, IllegalStateError } from '../../../../shared/errors.js';
import { BookCommand } from './BookCommand.js';
import { BookingLedger } from './BookingLedger.js';
import { CancelCommand } from './CancelCommand.js';
import type { DeskCommand } from './DeskCommand.js';
import { PayCommand } from './PayCommand.js';
import { UseCommand } from './UseCommand.js';

type CommandFactory = (args: readonly string[]) => DeskCommand;

export class BoxOfficeDesk {
  private static readonly COMMANDS: ReadonlyMap<string, CommandFactory> = new Map<string, CommandFactory>([
    ['BOOK', (args) => BookCommand.from(args)],
    ['PAY', (args) => PayCommand.from(args)],
    ['USE', (args) => UseCommand.from(args)],
    ['CANCEL', (args) => CancelCommand.from(args)],
  ]);

  private readonly ledger = new BookingLedger();
  private readonly history: DeskCommand[] = [];

  handle(line: string): string {
    const args = line.trim().split(/ +/);
    const operation = (args[0] ?? '').toUpperCase();
    try {
      switch (operation) {
        case 'UNDO':
          return this.undo();
        case 'REPORT':
          return this.report();
        default:
          return this.execute(operation, args);
      }
    } catch (exception) {
      if (exception instanceof IllegalArgumentError || exception instanceof IllegalStateError) {
        return `Blad: ${exception.message}`;
      }
      throw exception;
    }
  }

  private execute(operation: string, args: readonly string[]): string {
    const factory = BoxOfficeDesk.COMMANDS.get(operation);
    if (factory === undefined) {
      return `Nieznana operacja: ${args[0] ?? ''}`;
    }
    const command = factory(args);
    const result = command.execute(this.ledger);
    this.history.push(command);
    return result;
  }

  private undo(): string {
    const last = this.history.pop();
    if (last === undefined) {
      throw new IllegalStateError('nothing to undo');
    }
    last.undo(this.ledger);
    return `Cofnieto: ${last.journalEntry()}`;
  }

  private report(): string {
    const log = this.history.length === 0 ? '-' : this.history
      .map((command) => command.journalEntry())
      .join(', ');
    return `${this.ledger.summary()}; dziennik: ${log}`;
  }
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** wykonanie i cofnięcie są obok siebie w jednej klasie, a nie w dwóch `switch`. Polecenie pamięta tylko to, czego potrzebuje do cofnięcia (poprzedni status, przesuniętą kwotę) - to stan **tego** żądania, dlatego każde wywołanie tworzy nowy obiekt przez `from(args)`, a komenda nie jest współdzieloną stałą jak w s15. Do historii trafia dopiero polecenie wykonane bez wyjątku, jak w start. UNDO i REPORT zostały w `switch` pulpitu, bo nie są operacjami na rezerwacji. Cykl życia nadal siedzi w warunkach `BookingLedger` - to zapach na krok 3.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s26 1 2`

### Krok 3: State dla cyklu życia rezerwacji

**Zmiany:**

1. Utwórz nowy plik `Booking.ts`: klasa `Booking` (kwota i pole `private state`) z nieeksportowanym interfejsem `BookingState` i funkcją `state(status, allowed)`, która dla nienadpisanych przejść rzuca `cannot <akcja> in <STATUS>`.
2. Warunki z `BookingLedger.pay/use/cancel` przenieś do obiektów stanów: NEW podaje `pay` i `cancel`, PAID podaje `use` i `cancel`, USED i CANCELLED nie podają niczego. Mapa `BY_STATUS` obsługuje `restore` (powrót do zapamiętanego stanu z pominięciem reguł przejść).
3. `BookingLedger` trzyma `Map<string, Booking>` zamiast dwóch map i tylko przesuwa pieniądze w kasie: `pay` dolicza to, co zwróci `booking.pay()`, `cancel` odejmuje zwróconą kwotę.

**Przed:** `BookingLedger.ts` - jak w bloku Po kroku 2 (m.in. `cancel` z warunkami po statusie):

```typescript
cancel(id: string): Money {
  const status = this.status(id);
  if (status === Status.NEW) {
    this.statuses.set(id, Status.CANCELLED);
    return Money.ZERO;
  } else if (status === Status.PAID) {
    const amount = this.amountOf(id);
    this.till = this.till.minus(amount);
    this.statuses.set(id, Status.CANCELLED);
    return amount;
  }
  throw BookingLedger.invalid('cancel', status);
}
```

**Po (`Booking.ts`, nowy plik):**

```typescript
import { IllegalStateError } from '../../../../shared/errors.js';
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Money } from '../../../shared/Money.js';
import { Status } from '../Status.js';

type Transition = (booking: Booking) => Money;

/** Prywatny typ stanu (w Javie: prywatny enum State w Booking) - nie jest eksportowany z modułu. */
interface BookingState {
  readonly status: Status;
  readonly pay: Transition;
  readonly use: (booking: Booking) => void;
  readonly cancel: Transition;
}

/** Domyślnie przejście rzuca wyjątek - stan podaje tylko swoje dozwolone przejścia. */
function state(status: Status, allowed: Partial<Omit<BookingState, 'status'>> = {}): BookingState {
  const invalid = (action: string) => (): never => {
    throw new IllegalStateError(`cannot ${action} in ${status}`);
  };
  return { status, pay: invalid('pay'), use: invalid('use'), cancel: invalid('cancel'), ...allowed };
}

export class Booking {
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

  private static readonly PAID: BookingState = state(Status.PAID, {
    use: (booking) => {
      booking.state = Booking.USED;
    },
    cancel: (booking) => {
      booking.state = Booking.CANCELLED;
      return booking.amount;
    },
  });

  private static readonly USED = state(Status.USED);
  private static readonly CANCELLED = state(Status.CANCELLED);

  private static readonly BY_STATUS: Readonly<Record<Status, BookingState>> = {
    [Status.NEW]: Booking.NEW,
    [Status.PAID]: Booking.PAID,
    [Status.USED]: Booking.USED,
    [Status.CANCELLED]: Booking.CANCELLED,
  };

  private readonly amount: Money;
  private state = Booking.NEW;

  constructor(amount: Money) {
    this.amount = requireNonNull(amount, 'amount');
  }

  status(): Status {
    return this.state.status;
  }

  /** Zwraca kwotę pobraną do kasy. */
  pay(): Money {
    return this.state.pay(this);
  }

  use(): void {
    this.state.use(this);
  }

  /** Zwraca kwotę oddaną klientowi (zero, gdy rezerwacja nie była opłacona). */
  cancel(): Money {
    return this.state.cancel(this);
  }

  /** Dla cofania poleceń: powrót do zapamiętanego stanu z pominięciem reguł przejść. */
  restore(status: Status): void {
    this.state = Booking.BY_STATUS[status];
  }
}
```

**Po (`BookingLedger.ts`, cały plik):**

```typescript
import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { Status } from '../Status.js';
import { Booking } from './Booking.js';

export class BookingLedger {
  private readonly bookings = new Map<string, Booking>();
  private till = Money.ZERO;

  open(id: string, amount: Money): void {
    if (this.bookings.has(id)) {
      throw new IllegalArgumentError(`booking exists: ${id}`);
    }
    this.bookings.set(id, new Booking(amount));
  }

  remove(id: string): void {
    this.bookings.delete(id);
  }

  status(id: string): Status {
    return this.booking(id).status();
  }

  pay(id: string): Money {
    const paid = this.booking(id).pay();
    this.till = this.till.plus(paid);
    return paid;
  }

  use(id: string): void {
    this.booking(id).use();
  }

  cancel(id: string): Money {
    const refunded = this.booking(id).cancel();
    this.till = this.till.minus(refunded);
    return refunded;
  }

  restore(id: string, previous: Status): void {
    this.booking(id).restore(previous);
  }

  deposit(amount: Money): void {
    this.till = this.till.plus(amount);
  }

  withdraw(amount: Money): void {
    this.till = this.till.minus(amount);
  }

  summary(): string {
    const list = this.bookings.size === 0 ? '-' : [...this.bookings]
      .map(([id, booking]) => `${id}=${booking.status()}`)
      .join(', ');
    return `Kasa: ${this.till.toString()}; ${list}`;
  }

  private booking(id: string): Booking {
    const booking = this.bookings.get(id);
    if (booking === undefined) {
      throw new IllegalArgumentError(`no booking ${id}`);
    }
    return booking;
  }
}
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

**Zmiany:**

1. Utwórz nowy plik `ReportFormat.ts`: interfejs z metodami `header`, `row`, `footer`.
2. Utwórz klasy `CsvFormat` i `TextFormat` z ciałami skopiowanymi z `DailyCsvReport` i `DailyTextReport` - najpierw porównaj je z kopiami w `Weekly*`, czy implementacje się nie rozjechały.
3. Do `ScreeningReport` dodaj pole `format` i konstruktor `protected constructor(format: ReportFormat)`; w `render` wołaj `this.format.header(...)`, `this.format.row(...)`, `this.format.footer(...)`.
4. Usuń trzy haki formatu z bazy i podklas (brak użyć potwierdzi `npm run typecheck`); podklasy wołają `super(new CsvFormat())` albo `super(new TextFormat())`.

**Przed (`DailyCsvReport.ts`, haki formatu; haki okresu `includes`/`label` bez zmian):**

```typescript
protected override header(label: string): string {
  return `# raport ${label}\nfilm;bilety;przychod\n`;
}

protected override row(title: string, tickets: number, revenue: Money): string {
  const cell = title.includes(';') ? `"${title}"` : title;
  return `${cell};${tickets};${revenue.toString()}\n`;
}

protected override footer(tickets: number, revenue: Money): string {
  return `SUMA;${tickets};${revenue.toString()}\n`;
}
```

**Po (`ReportFormat.ts`, nowy plik):**

```typescript
import type { Money } from '../../../shared/Money.js';

/** Strategy dla osi "format" - trzy haki formatu wyjęte z hierarchii. */
export interface ReportFormat {
  header(label: string): string;

  row(title: string, tickets: number, revenue: Money): string;

  footer(tickets: number, revenue: Money): string;
}
```

**Po (`CsvFormat.ts`, nowy plik):**

```typescript
import type { Money } from '../../../shared/Money.js';
import type { ReportFormat } from './ReportFormat.js';

export class CsvFormat implements ReportFormat {
  header(label: string): string {
    return `# raport ${label}\nfilm;bilety;przychod\n`;
  }

  row(title: string, tickets: number, revenue: Money): string {
    const cell = title.includes(';') ? `"${title}"` : title;
    return `${cell};${tickets};${revenue.toString()}\n`;
  }

  footer(tickets: number, revenue: Money): string {
    return `SUMA;${tickets};${revenue.toString()}\n`;
  }
}
```

**Po (`TextFormat.ts`, nowy plik):**

```typescript
import type { Money } from '../../../shared/Money.js';
import type { ReportFormat } from './ReportFormat.js';

export class TextFormat implements ReportFormat {
  header(label: string): string {
    return `RAPORT ${label.toUpperCase()}\n${'-'.repeat(37)}\n`;
  }

  row(title: string, tickets: number, revenue: Money): string {
    return `${title.padEnd(20)} ${String(tickets).padStart(5)} ${revenue.toString().padStart(10)}\n`;
  }

  footer(tickets: number, revenue: Money): string {
    return `${'-'.repeat(37)}\n${this.row('RAZEM', tickets, revenue)}`;
  }
}
```

**Po (`ScreeningReport.ts`, pole, konstruktor i `render`; klasa `Line`, funkcja `sortedKeys` i haki okresu bez zmian, w importach dochodzą `requireNonNull` i `ReportFormat`, znikają deklaracje `header`/`row`/`footer`):**

```typescript
export abstract class ScreeningReport {
  private readonly format: ReportFormat;

  protected constructor(format: ReportFormat) {
    if (this.render !== ScreeningReport.prototype.render) {
      throw new IllegalStateError('render() is final');
    }
    this.format = requireNonNull(format, 'format');
  }

  render(day: LocalDate, sales: readonly ScreeningSale[]): string {
    const byTitle = new Map<string, Line>();
    for (const sale of sales) {
      if (this.includes(day, sale.day)) {
        const line = new Line(sale.tickets, sale.revenue);
        byTitle.set(sale.title, byTitle.get(sale.title)?.plus(line) ?? line);
      }
    }
    let text = this.format.header(this.label(day));
    let total = new Line(0, Money.ZERO);
    for (const title of sortedKeys(byTitle)) {
      const line = byTitle.get(title)!;
      text += this.format.row(title, line.tickets, line.revenue);
      total = total.plus(line);
    }
    return text + this.format.footer(total.tickets, total.revenue);
  }

  /** Hak okresu: czy sprzedaż z danego dnia należy do raportu. */
  protected abstract includes(day: LocalDate, saleDay: LocalDate): boolean;

  /** Hak okresu: opis okresu w nagłówku. */
  protected abstract label(day: LocalDate): string;
}
```

**Po (`DailyCsvReport.ts`, cały plik; w `DailyTextReport` analogicznie `super(new TextFormat())` i import `TextFormat`, w `WeeklyCsvReport`/`WeeklyTextReport` ten sam ruch przy ich hakach tygodniowych):**

```typescript
import type { LocalDate } from '../../../shared/time.js';
import { CsvFormat } from './CsvFormat.js';
import { ScreeningReport } from './ScreeningReport.js';

export class DailyCsvReport extends ScreeningReport {
  constructor() {
    super(new CsvFormat());
  }

  protected override includes(day: LocalDate, saleDay: LocalDate): boolean {
    return saleDay.equals(day);
  }

  protected override label(day: LocalDate): string {
    return `dzien ${day.toString()}`;
  }
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s27` - zielone.
**Co powiedzieć:** najpierw grupujemy haki według osi, dopiero potem cokolwiek przenosimy. Hak wołamy teraz na delegacie, nie na `this` - strategia nie widzi stanu `protected` bazy i wszystko dostaje parametrami. `Reports` jest bez zmian, klienci niczego nie zauważyli.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s27 0 1`

### Krok 2: oś okresu jako strategia

**Zmiany:**

1. Ten sam ruch dla osi okresu: nowy plik `ReportPeriod.ts` (interfejs z `includes` i `label`) oraz klasy `DailyPeriod` i `WeeklyPeriod`.
2. W `WeeklyPeriod` wydziel powtórzone wyliczanie poniedziałku do prywatnej statycznej metody `monday(day)` (Extract Method).
3. Konstruktor bazy to teraz `protected constructor(period: ReportPeriod, format: ReportFormat)`; w `render` hak okresu to `this.period.includes(...)`, a etykieta to `this.period.label(day)`. Haki `includes` i `label` znikają z bazy i podklas.
4. Podklasy przekazują obie strategie, np. `super(new WeeklyPeriod(), new TextFormat())`, i nie mają już żadnych własnych metod.

**Przed:**

```typescript
export class DailyCsvReport extends ScreeningReport {
  constructor() {
    super(new CsvFormat());
  }

  protected override includes(day: LocalDate, saleDay: LocalDate): boolean {
    return saleDay.equals(day);
  }

  protected override label(day: LocalDate): string {
    return `dzien ${day.toString()}`;
  }
}
```

**Po (`ReportPeriod.ts`, nowy plik):**

```typescript
import type { LocalDate } from '../../../shared/time.js';

/** Strategy dla osi "okres" - dwa haki okresu wyjęte z hierarchii. */
export interface ReportPeriod {
  includes(day: LocalDate, saleDay: LocalDate): boolean;

  label(day: LocalDate): string;
}
```

**Po (`DailyPeriod.ts`, nowy plik):**

```typescript
import type { LocalDate } from '../../../shared/time.js';
import type { ReportPeriod } from './ReportPeriod.js';

export class DailyPeriod implements ReportPeriod {
  includes(day: LocalDate, saleDay: LocalDate): boolean {
    return saleDay.equals(day);
  }

  label(day: LocalDate): string {
    return `dzien ${day.toString()}`;
  }
}
```

**Po (`WeeklyPeriod.ts`, nowy plik):**

```typescript
import { DAYS_OF_WEEK, type LocalDate } from '../../../shared/time.js';
import type { ReportPeriod } from './ReportPeriod.js';

export class WeeklyPeriod implements ReportPeriod {
  includes(day: LocalDate, saleDay: LocalDate): boolean {
    const monday = WeeklyPeriod.monday(day);
    return !saleDay.isBefore(monday) && !saleDay.isAfter(monday.plusDays(6));
  }

  label(day: LocalDate): string {
    const monday = WeeklyPeriod.monday(day);
    return `tydzien ${monday.toString()}..${monday.plusDays(6).toString()}`;
  }

  // Odpowiednik day.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).
  private static monday(day: LocalDate): LocalDate {
    return day.plusDays(-DAYS_OF_WEEK.indexOf(day.dayOfWeek));
  }
}
```

**Po (`ScreeningReport.ts`, pola, konstruktor i `render`; deklaracje `includes`/`label` znikają, w importach dochodzi `ReportPeriod`, `Line` i `sortedKeys` bez zmian):**

```typescript
export abstract class ScreeningReport {
  private readonly period: ReportPeriod;
  private readonly format: ReportFormat;

  protected constructor(period: ReportPeriod, format: ReportFormat) {
    if (this.render !== ScreeningReport.prototype.render) {
      throw new IllegalStateError('render() is final');
    }
    this.period = requireNonNull(period, 'period');
    this.format = requireNonNull(format, 'format');
  }

  render(day: LocalDate, sales: readonly ScreeningSale[]): string {
    const byTitle = new Map<string, Line>();
    for (const sale of sales) {
      if (this.period.includes(day, sale.day)) {
        const line = new Line(sale.tickets, sale.revenue);
        byTitle.set(sale.title, byTitle.get(sale.title)?.plus(line) ?? line);
      }
    }
    let text = this.format.header(this.period.label(day));
    let total = new Line(0, Money.ZERO);
    for (const title of sortedKeys(byTitle)) {
      const line = byTitle.get(title)!;
      text += this.format.row(title, line.tickets, line.revenue);
      total = total.plus(line);
    }
    return text + this.format.footer(total.tickets, total.revenue);
  }
}
```

**Po (`WeeklyTextReport.ts`, cały plik poza importami `WeeklyPeriod`, `TextFormat` i `ScreeningReport`; trzy pozostałe podklasy analogicznie - `DailyCsvReport`: `super(new DailyPeriod(), new CsvFormat())`, `DailyTextReport`: `super(new DailyPeriod(), new TextFormat())`, `WeeklyCsvReport`: `super(new WeeklyPeriod(), new CsvFormat())`):**

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

**Zmiany:**

1. W `ScreeningReport` usuń `abstract`, konstruktor zrób publiczny, a sprawdzanie nadpisanego `render` zastąp odrzucaniem każdej podklasy: `if (new.target !== ScreeningReport) throw new IllegalStateError('ScreeningReport is final')`.
2. W `Reports` dodaj dwie mapy `PERIODS` i `FORMATS`; `of` sprawdza najpierw okres, potem format (te same komunikaty błędów) i składa `new ScreeningReport(chosenPeriod, chosenFormat)`.
3. Usuń pliki czterech podklas - przed usunięciem wyszukaj odwołania do każdej, po usunięciu uruchom `npm run typecheck`.

**Przed (`Reports.ts`, metody `of` i `unknownFormat`):**

```typescript
static of(period: string, format: string): ScreeningReport {
  switch (period) {
    case 'DAILY':
      switch (format) {
        case 'CSV':
          return new DailyCsvReport();
        case 'TEXT':
          return new DailyTextReport();
        default:
          throw Reports.unknownFormat(format);
      }
    case 'WEEKLY':
      switch (format) {
        case 'CSV':
          return new WeeklyCsvReport();
        case 'TEXT':
          return new WeeklyTextReport();
        default:
          throw Reports.unknownFormat(format);
      }
    default:
      throw new IllegalArgumentError(`unknown period: ${period}`);
  }
}

private static unknownFormat(format: string): IllegalArgumentError {
  return new IllegalArgumentError(`unknown format: ${format}`);
}
```

**Po (`ScreeningReport.ts`, nagłówek klasy i konstruktor; `render`, `Line` i `sortedKeys` bez zmian):**

```typescript
export class ScreeningReport {
  private readonly period: ReportPeriod;
  private readonly format: ReportFormat;

  constructor(period: ReportPeriod, format: ReportFormat) {
    if (new.target !== ScreeningReport) {
      throw new IllegalStateError('ScreeningReport is final');
    }
    this.period = requireNonNull(period, 'period');
    this.format = requireNonNull(format, 'format');
  }
```

**Po (`Reports.ts`, cały plik):**

```typescript
import { IllegalArgumentError } from '../../../../shared/errors.js';
import { CsvFormat } from './CsvFormat.js';
import { DailyPeriod } from './DailyPeriod.js';
import type { ReportFormat } from './ReportFormat.js';
import type { ReportPeriod } from './ReportPeriod.js';
import { ScreeningReport } from './ScreeningReport.js';
import { TextFormat } from './TextFormat.js';
import { WeeklyPeriod } from './WeeklyPeriod.js';

export class Reports {
  private static readonly PERIODS: ReadonlyMap<string, ReportPeriod> = new Map<string, ReportPeriod>([
    ['DAILY', new DailyPeriod()],
    ['WEEKLY', new WeeklyPeriod()],
  ]);

  private static readonly FORMATS: ReadonlyMap<string, ReportFormat> = new Map<string, ReportFormat>([
    ['CSV', new CsvFormat()],
    ['TEXT', new TextFormat()],
  ]);

  private constructor() {}

  static of(period: string, format: string): ScreeningReport {
    const chosenPeriod = Reports.PERIODS.get(period);
    if (chosenPeriod === undefined) {
      throw new IllegalArgumentError(`unknown period: ${period}`);
    }
    const chosenFormat = Reports.FORMATS.get(format);
    if (chosenFormat === undefined) {
      throw new IllegalArgumentError(`unknown format: ${format}`);
    }
    return new ScreeningReport(chosenPeriod, chosenFormat);
  }
}
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

**Zmiany:**

1. Utwórz nowy plik `RefundHandler.ts`: interfejs z metodą `handle(request: RefundRequest): RefundDecision | undefined` - `undefined` znaczy "pytaj dalej".
2. Pierwszą gałąź (`seans juz sie rozpoczal`) wydziel i przenieś do klasy `AfterScreeningHandler implements RefundHandler` w osobnym pliku (Extract Method, potem przeniesienie do klasy).
3. W `RefundApprover` dodaj pole `afterScreening = new AfterScreeningHandler()`; `decide` najpierw pyta ogniwo, a gdy dostanie `undefined`, przechodzi do reszty `if/else`. Walidacja kwoty zostaje przed ogniwem.

**Przed:**

```typescript
if (!request.requestedAt.isBefore(request.screeningStart)) {
  return RefundDecision.rejected('seans juz sie rozpoczal');
} else if (Duration.between(request.requestedAt, request.screeningStart).toHours() >= 24) {
  return RefundDecision.approvedBy('AUTO');
```

**Po (`RefundHandler.ts`, nowy plik):**

```typescript
import type { RefundDecision } from '../RefundDecision.js';
import type { RefundRequest } from '../RefundRequest.js';

/**
 * Ogniwo decyzji - pusta odpowiedź znaczy "to nie moja sprawa, pytaj dalej".
 * (Optional<RefundDecision> z Javy to tutaj RefundDecision | undefined.)
 */
export interface RefundHandler {
  handle(request: RefundRequest): RefundDecision | undefined;
}
```

**Po (`AfterScreeningHandler.ts`, nowy plik):**

```typescript
import { RefundDecision } from '../RefundDecision.js';
import type { RefundRequest } from '../RefundRequest.js';
import type { RefundHandler } from './RefundHandler.js';

export class AfterScreeningHandler implements RefundHandler {
  handle(request: RefundRequest): RefundDecision | undefined {
    if (!request.requestedAt.isBefore(request.screeningStart)) {
      return RefundDecision.rejected('seans juz sie rozpoczal');
    }
    return undefined;
  }
}
```

**Po (`RefundApprover.ts`, nowe pole i początek `decide`; dalsze gałęzie `else if` i importy `AfterScreeningHandler`/`RefundHandler` dochodzą, reszta bez zmian):**

```typescript
private readonly afterScreening: RefundHandler = new AfterScreeningHandler();
```

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

**Zmiany:**

1. Ten sam ruch dla pozostałych gałęzi: klasy `AutoRefundHandler`, `CashierHandler` i `ManagerHandler`.
2. Limity przekaż do konstruktorów ogniw (Introduce Parameter, ręcznie): `new CashierHandler(RefundApprover.CASHIER_LIMIT)`, `new ManagerHandler(RefundApprover.MANAGER_LIMIT)`.
3. W `RefundApprover` zastąp pole `afterScreening` tablicą `handlers` w kolejności reguł; `decide` przegląda ją po kolei i zwraca pierwszą decyzję, a po tablicy - odmowę "brak uprawnionego zatwierdzajacego". Import `Duration` znika, dochodzą importy trzech nowych ogniw.

**Przed:**

```typescript
const decision = this.afterScreening.handle(request);
if (decision !== undefined) {
  return decision;
} else if (Duration.between(request.requestedAt, request.screeningStart).toHours() >= 24) {
  return RefundDecision.approvedBy('AUTO');
} else if (!request.amount.isGreaterThan(RefundApprover.CASHIER_LIMIT)) {
  return RefundDecision.approvedBy('CASHIER');
} else if (!request.amount.isGreaterThan(RefundApprover.MANAGER_LIMIT)) {
  return RefundDecision.approvedBy('MANAGER');
}
return RefundDecision.rejected('brak uprawnionego zatwierdzajacego');
```

**Po (`AutoRefundHandler.ts`, nowy plik):**

```typescript
import { Duration } from '../../../shared/time.js';
import { RefundDecision } from '../RefundDecision.js';
import type { RefundRequest } from '../RefundRequest.js';
import type { RefundHandler } from './RefundHandler.js';

/** Co najmniej 24 h przed seansem zwrot jest automatyczny - bez limitu kwoty. */
export class AutoRefundHandler implements RefundHandler {
  handle(request: RefundRequest): RefundDecision | undefined {
    if (Duration.between(request.requestedAt, request.screeningStart).toHours() >= 24) {
      return RefundDecision.approvedBy('AUTO');
    }
    return undefined;
  }
}
```

**Po (`CashierHandler.ts`, nowy plik; `ManagerHandler.ts` analogicznie z `'MANAGER'`):**

```typescript
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { Money } from '../../../shared/Money.js';
import { RefundDecision } from '../RefundDecision.js';
import type { RefundRequest } from '../RefundRequest.js';
import type { RefundHandler } from './RefundHandler.js';

/** Poziom CASHIER zatwierdza zwroty do swojego limitu (włącznie). */
export class CashierHandler implements RefundHandler {
  private readonly limit: Money;

  constructor(limit: Money) {
    this.limit = requireNonNull(limit, 'limit');
  }

  handle(request: RefundRequest): RefundDecision | undefined {
    if (!request.amount.isGreaterThan(this.limit)) {
      return RefundDecision.approvedBy('CASHIER');
    }
    return undefined;
  }
}
```

**Po (`RefundApprover.ts`, pole `handlers` i dalszy ciąg `decide` po walidacji kwoty):**

```typescript
private readonly handlers: readonly RefundHandler[] = [
  new AfterScreeningHandler(),
  new AutoRefundHandler(),
  new CashierHandler(RefundApprover.CASHIER_LIMIT),
  new ManagerHandler(RefundApprover.MANAGER_LIMIT),
];
```

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

**Zmiany:**

1. Zmień sygnaturę `RefundHandler.handle`: zwraca `RefundDecision` zamiast `RefundDecision | undefined`; `npm run typecheck` wskaże wszystkie ogniwa do poprawy.
2. Każde ogniwo dostaje `next: RefundHandler` w konstruktorze (pole `readonly`) i woła `this.next.handle(request)`, gdy nie obsłuży żądania.
3. Odmowę z końca tablicy przenieś do nowego ogniwa końcowego `NoApproverHandler`.
4. `RefundApprover` zastępuje tablicę polem `chain` i konstruktorem z parametrem domyślnym `chain: RefundHandler = RefundApprover.standardChain()`: bez argumentu składa standardową ścieżkę, z argumentem przyjmuje dowolną głowę łańcucha.

**Przed:** `RefundApprover.ts` i ogniwa - jak w blokach Po kroków 1-2.

**Po (`RefundHandler.ts`, cały plik):**

```typescript
import type { RefundDecision } from '../RefundDecision.js';
import type { RefundRequest } from '../RefundRequest.js';

/**
 * Chain of Responsibility - ogniwo zawsze zwraca decyzję: własną (i zatrzymuje łańcuch)
 * albo następnika (przekazuje dalej). Następnika dostaje w konstruktorze.
 */
export interface RefundHandler {
  handle(request: RefundRequest): RefundDecision;
}
```

**Po (`AfterScreeningHandler.ts`, cały plik; `AutoRefundHandler.ts` analogicznie - swój warunek, potem `this.next.handle(request)`):**

```typescript
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { RefundDecision } from '../RefundDecision.js';
import type { RefundRequest } from '../RefundRequest.js';
import type { RefundHandler } from './RefundHandler.js';

export class AfterScreeningHandler implements RefundHandler {
  private readonly next: RefundHandler;

  constructor(next: RefundHandler) {
    this.next = requireNonNull(next, 'next');
  }

  handle(request: RefundRequest): RefundDecision {
    if (!request.requestedAt.isBefore(request.screeningStart)) {
      return RefundDecision.rejected('seans juz sie rozpoczal');
    }
    return this.next.handle(request);
  }
}
```

**Po (`CashierHandler.ts`, pole, konstruktor i `handle`; `ManagerHandler.ts` analogicznie z `'MANAGER'`):**

```typescript
export class CashierHandler implements RefundHandler {
  private readonly limit: Money;
  private readonly next: RefundHandler;

  constructor(limit: Money, next: RefundHandler) {
    this.limit = requireNonNull(limit, 'limit');
    this.next = requireNonNull(next, 'next');
  }

  handle(request: RefundRequest): RefundDecision {
    if (!request.amount.isGreaterThan(this.limit)) {
      return RefundDecision.approvedBy('CASHIER');
    }
    return this.next.handle(request);
  }
}
```

**Po (`NoApproverHandler.ts`, nowy plik):**

```typescript
import { RefundDecision } from '../RefundDecision.js';
import type { RefundRequest } from '../RefundRequest.js';
import type { RefundHandler } from './RefundHandler.js';

/** Jawny koniec łańcucha - żądanie nie może po cichu "spaść" bez odpowiedzi. */
export class NoApproverHandler implements RefundHandler {
  handle(_request: RefundRequest): RefundDecision {
    return RefundDecision.rejected('brak uprawnionego zatwierdzajacego');
  }
}
```

**Po (`RefundApprover.ts`, cały plik):**

```typescript
import { IllegalArgumentError } from '../../../../shared/errors.js';
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Money } from '../../../shared/Money.js';
import type { RefundDecision } from '../RefundDecision.js';
import type { RefundRequest } from '../RefundRequest.js';
import { AfterScreeningHandler } from './AfterScreeningHandler.js';
import { AutoRefundHandler } from './AutoRefundHandler.js';
import { CashierHandler } from './CashierHandler.js';
import { ManagerHandler } from './ManagerHandler.js';
import { NoApproverHandler } from './NoApproverHandler.js';
import type { RefundHandler } from './RefundHandler.js';

export class RefundApprover {
  private static readonly CASHIER_LIMIT = Money.of('100.00');
  private static readonly MANAGER_LIMIT = Money.of('1000.00');

  private readonly chain: RefundHandler;

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

  decide(request: RefundRequest): RefundDecision {
    if (!request.amount.isGreaterThan(Money.ZERO)) {
      throw new IllegalArgumentError('amount must be positive');
    }
    return this.chain.handle(request);
  }
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

**Zmiany:**

1. Utwórz nowy plik `PaymentConfirmation.ts`: klasa z polami `readonly outcome: Outcome`, `code: string`, `authorized: Money`, wyliczenie `Outcome { APPROVED, DECLINED, ERROR }` i walidacja w konstruktorze - kod niepusty, kwota nieujemna, tylko APPROVED może autoryzować pieniądze.
2. W `pay` rozbierz odpowiedź raz, zaraz po wywołaniu terminala: wydziel statyczną metodę `parse(raw)` (Extract Method), która sprawdza liczbę pól, znany status i kwotę w groszach (wyrażeniem regularnym) i buduje `PaymentConfirmation`.
3. Zamień mapę `responses` (surowy tekst) na `confirmations` z `PaymentConfirmation`; `receipt` i `totalAuthorized` czytają pola obiektu zamiast parsować.

**Przed (`PaymentService.ts`, metody `pay`, `receipt` i `totalAuthorized`; pole `private readonly responses = new Map<string, string>()`):**

```typescript
pay(bookingId: string, amount: Money): string {
  const grosze = amount.amount.times(100).toNumber();
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
  return `PAID ${parts[1]}`;
}

receipt(bookingId: string): string {
  const raw = this.responses.get(bookingId);
  if (raw === undefined) {
    return `Brak platnosci ${bookingId}`;
  }
  const parts = raw.split(';');
  if (!raw.startsWith('OK')) {
    return `Platnosc ${bookingId} nieudana: ${parts[1]}`;
  }
  return `Platnosc ${bookingId}: ${new Decimal(parts[2] ?? '').dividedBy(100).toFixed(2)} (${parts[1]})`;
}

totalAuthorized(): Money {
  let total = Money.ZERO;
  for (const raw of this.responses.values()) {
    if (raw.startsWith('OK')) {
      total = total.plus(new Money(new Decimal(raw.substring(raw.lastIndexOf(';') + 1)).dividedBy(100)));
    }
  }
  return total;
}
```

**Po (`PaymentConfirmation.ts`, nowy plik):**

```typescript
import { IllegalArgumentError } from '../../../../shared/errors.js';
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Money } from '../../../shared/Money.js';

/** Wynik autoryzacji w języku kina (w Javie: enum zagnieżdżony w rekordzie). */
export enum Outcome {
  APPROVED = 'APPROVED',
  DECLINED = 'DECLINED',
  ERROR = 'ERROR',
}

/** Boundary Object - odpowiedź terminala jako niezmienna, zwalidowana wartość w języku kina. */
export class PaymentConfirmation {
  readonly outcome: Outcome;
  readonly code: string;
  readonly authorized: Money;

  constructor(outcome: Outcome, code: string, authorized: Money) {
    this.outcome = requireNonNull(outcome, 'outcome');
    this.authorized = requireNonNull(authorized, 'authorized');
    if (code === null || code === undefined || code.trim() === '') {
      throw new IllegalArgumentError('code must not be blank');
    }
    this.code = code;
    if (authorized.compareTo(Money.ZERO) < 0) {
      throw new IllegalArgumentError('authorized amount must not be negative');
    }
    if (outcome !== Outcome.APPROVED && authorized.isGreaterThan(Money.ZERO)) {
      throw new IllegalArgumentError('only approved payment can authorize money');
    }
  }

  approved(): boolean {
    return this.outcome === Outcome.APPROVED;
  }

  /** Autoryzacja częściowa: terminal zgodził się na mniej, niż prosiliśmy. */
  isPartialFor(requested: Money): boolean {
    return this.approved() && this.authorized.compareTo(requested) < 0;
  }
}
```

**Po (`PaymentService.ts`, pole, metody `pay`, `receipt`, `totalAuthorized` i nowa metoda `parse`; w importach dochodzą `IllegalArgumentError` oraz `Outcome` i `PaymentConfirmation`):**

```typescript
private readonly confirmations = new Map<string, PaymentConfirmation>();
```

```typescript
pay(bookingId: string, amount: Money): string {
  const grosze = amount.amount.times(100).toNumber();
  const confirmation = PaymentService.parse(this.terminal.authorize(bookingId, grosze));
  this.confirmations.set(bookingId, confirmation);
  if (!confirmation.approved()) {
    return `${confirmation.outcome} ${confirmation.code}`;
  }
  if (confirmation.isPartialFor(amount)) {
    return `PARTIAL ${confirmation.code} brakuje ${amount.minus(confirmation.authorized).toString()}`;
  }
  return `PAID ${confirmation.code}`;
}

receipt(bookingId: string): string {
  const confirmation = this.confirmations.get(bookingId);
  if (confirmation === undefined) {
    return `Brak platnosci ${bookingId}`;
  }
  if (!confirmation.approved()) {
    return `Platnosc ${bookingId} nieudana: ${confirmation.code}`;
  }
  return `Platnosc ${bookingId}: ${confirmation.authorized.toString()} (${confirmation.code})`;
}

totalAuthorized(): Money {
  let total = Money.ZERO;
  for (const confirmation of this.confirmations.values()) {
    total = total.plus(confirmation.authorized);
  }
  return total;
}

private static parse(raw: string): PaymentConfirmation {
  const parts = raw.split(';');
  if (parts.length !== 3) {
    throw new IllegalArgumentError(`malformed terminal response: ${raw}`);
  }
  const [status = '', code = '', grosze = ''] = parts;
  let outcome: Outcome;
  switch (status) {
    case 'OK': outcome = Outcome.APPROVED; break;
    case 'DECLINED': outcome = Outcome.DECLINED; break;
    case 'ERROR': outcome = Outcome.ERROR; break;
    default: throw new IllegalArgumentError(`malformed terminal response: ${raw}`);
  }
  // W Javie new BigDecimal("abc") rzuca NumberFormatException (podklasa IllegalArgumentException);
  // Decimal przyjąłby "NaN" i rzuca własnym błędem, więc kwotę w groszach sprawdzamy wprost.
  if (!/^[+-]?\d+$/.test(grosze)) {
    throw new IllegalArgumentError(`malformed terminal response: ${raw}`);
  }
  return new PaymentConfirmation(outcome, code, new Money(new Decimal(grosze).dividedBy(100)));
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s29` - zielone.
**Co powiedzieć:** zamiast trzech parserów jest jeden, w miejscu wejścia danych. `totalAuthorized` nie sprawdza już `startsWith('OK')` i po prostu sumuje - niezmiennik obiektu gwarantuje zero dla odmowy. `parse` sprawdza liczbę pól, znany status i kwotę: to pierwszy moment, w którym zmienia się zachowanie dla danych błędnych (start przyjąłby `OK;;4500` i wydrukował pusty kod).
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s29 0 1`

### Krok 2: mapper na granicy

**Zmiany:**

1. Utwórz nowy plik `TerminalResponseMapper.ts` i przenieś do niego `parse` jako metodę `toConfirmation(raw)` (przenosimy ręcznie).
2. Przenieś tam też przeliczenie złotych na grosze jako `toGrosze(amount)`.
3. Serwis dostaje pole `private readonly mapper = new TerminalResponseMapper()` i woła je po obu stronach wywołania terminala.

**Przed:** `PaymentService.ts` - jak w blokach Po kroku 1 (pierwsze dwa wiersze `pay` i metoda `parse`).

**Po (`TerminalResponseMapper.ts`, nowy plik):**

```typescript
import { Decimal } from 'decimal.js';

import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import { Outcome, PaymentConfirmation } from './PaymentConfirmation.js';

/** Jedyne miejsce, które zna format terminala: grosze w obie strony i tekst "STATUS;KOD;KWOTA". */
export class TerminalResponseMapper {
  toGrosze(amount: Money): number {
    return amount.amount.times(100).toNumber();
  }

  toConfirmation(raw: string): PaymentConfirmation {
    const parts = raw.split(';');
    if (parts.length !== 3) {
      throw new IllegalArgumentError(`malformed terminal response: ${raw}`);
    }
    const [status = '', code = '', grosze = ''] = parts;
    let outcome: Outcome;
    switch (status) {
      case 'OK': outcome = Outcome.APPROVED; break;
      case 'DECLINED': outcome = Outcome.DECLINED; break;
      case 'ERROR': outcome = Outcome.ERROR; break;
      default: throw new IllegalArgumentError(`malformed terminal response: ${raw}`);
    }
    if (!/^[+-]?\d+$/.test(grosze)) {
      throw new IllegalArgumentError(`malformed terminal response: ${raw}`);
    }
    return new PaymentConfirmation(outcome, code, new Money(new Decimal(grosze).dividedBy(100)));
  }
}
```

**Po (`PaymentService.ts`, nowe pole i początek `pay`; metoda `parse` znika razem z importami `Decimal`, `IllegalArgumentError` i `Outcome`, dochodzi import `TerminalResponseMapper`, a `PaymentConfirmation` staje się `import type`; `receipt` i `totalAuthorized` bez zmian):**

```typescript
private readonly mapper = new TerminalResponseMapper();
```

```typescript
pay(bookingId: string, amount: Money): string {
  const raw = this.terminal.authorize(bookingId, this.mapper.toGrosze(amount));
  const confirmation = this.mapper.toConfirmation(raw);
  this.confirmations.set(bookingId, confirmation);
  if (!confirmation.approved()) {
    return `${confirmation.outcome} ${confirmation.code}`;
  }
  if (confirmation.isPartialFor(amount)) {
    return `PARTIAL ${confirmation.code} brakuje ${amount.minus(confirmation.authorized).toString()}`;
  }
  return `PAID ${confirmation.code}`;
}
```

**Uruchom:** test zielony; `S29SolutionTest` → `malformedResponseIsRejectedAtTheBoundary` sprawdza `OK;AUTH-R1`, `MAYBE;X;0`, `OK;;4500` i `OK;AUTH-R1;abc`.
**Co powiedzieć:** mapper to jedyne miejsce, które zna format `STATUS;KOD;KWOTA` i grosze w obie strony. Serwis nie zna już formatu, ale wciąż sam woła `LegacyTerminalClient` i składa wywołanie z mapperem.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s29 1 2`

### Krok 3: port domeny i adapter

**Zmiany:**

1. Utwórz nowy plik `PaymentTerminal.ts`: interfejs z `authorize(bookingId: string, amount: Money): PaymentConfirmation`.
2. Utwórz klasę `LegacyTerminalAdapter implements PaymentTerminal`, która łączy `LegacyTerminalClient` z mapperem - poza nią nikt nie widzi groszy ani tekstu odpowiedzi.
3. Zmień typ pola i parametru konstruktora `PaymentService` z `LegacyTerminalClient` na `PaymentTerminal`; `pay` woła `this.terminal.authorize(bookingId, amount)`, a pole `mapper` znika z serwisu (`npm run typecheck` wskaże miejsca wywołań).
4. W korzeniu kompozycji (`PaymentModule`) owiń klienta adapterem.

**Przed:** `PaymentService.ts` - jak w blokach Po kroku 2; `PaymentModule.paymentService` zwraca `new PaymentService(terminal)`.

**Po (`PaymentTerminal.ts`, nowy plik):**

```typescript
import type { Money } from '../../../shared/Money.js';
import type { PaymentConfirmation } from './PaymentConfirmation.js';

/** Port domeny - terminal płatniczy widziany oczami kina: Money na wejściu, obiekt graniczny na wyjściu. */
export interface PaymentTerminal {
  authorize(bookingId: string, amount: Money): PaymentConfirmation;
}
```

**Po (`LegacyTerminalAdapter.ts`, nowy plik):**

```typescript
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { Money } from '../../../shared/Money.js';
import type { LegacyTerminalClient } from '../LegacyTerminalClient.js';
import type { PaymentConfirmation } from './PaymentConfirmation.js';
import type { PaymentTerminal } from './PaymentTerminal.js';
import { TerminalResponseMapper } from './TerminalResponseMapper.js';

/** Adapter implementujący port domeny - poza tą klasą nikt nie widzi groszy ani tekstu "OK;AUTH;4500". */
export class LegacyTerminalAdapter implements PaymentTerminal {
  private readonly client: LegacyTerminalClient;
  private readonly mapper = new TerminalResponseMapper();

  constructor(client: LegacyTerminalClient) {
    this.client = requireNonNull(client, 'client');
  }

  authorize(bookingId: string, amount: Money): PaymentConfirmation {
    return this.mapper.toConfirmation(this.client.authorize(bookingId, this.mapper.toGrosze(amount)));
  }
}
```

**Po (`PaymentService.ts`, pola, konstruktor i początek `pay`; importy `LegacyTerminalClient` i `TerminalResponseMapper` znikają, dochodzi `import type { PaymentTerminal }`; `receipt` i `totalAuthorized` bez zmian):**

```typescript
export class PaymentService {
  private readonly terminal: PaymentTerminal;
  private readonly confirmations = new Map<string, PaymentConfirmation>();

  constructor(terminal: PaymentTerminal) {
    this.terminal = requireNonNull(terminal, 'terminal');
  }

  pay(bookingId: string, amount: Money): string {
    const confirmation = this.terminal.authorize(bookingId, amount);
    this.confirmations.set(bookingId, confirmation);
    if (!confirmation.approved()) {
      return `${confirmation.outcome} ${confirmation.code}`;
    }
    if (confirmation.isPartialFor(amount)) {
      return `PARTIAL ${confirmation.code} brakuje ${amount.minus(confirmation.authorized).toString()}`;
    }
    return `PAID ${confirmation.code}`;
  }
```

**Po (`PaymentModule.ts`, metoda `paymentService`; dochodzi import `LegacyTerminalAdapter`):**

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

**Zmiany:**

1. Wydziel całą orkiestrację z `OnlineShop.buy` do metody `book` (Extract Method), a opłatę rezerwacyjną zamień na parametr: `book(order, feePerSeat)` (Introduce Parameter).
2. Przenieś metodę (wytnij i wklej) do nowej klasy `BookingFacade` w nowym pliku, z czterema podsystemami w konstruktorze (Move Method).
3. Sklep dostaje w konstruktorze fasadę zamiast czterech podsystemów; `buy` deleguje `this.booking.book(order, OnlineShop.BOOKING_FEE)`.
4. `Cinema` tworzy fasadę i podaje ją sklepowi; kasa bez zmian.

**Przed:**

```typescript
buy(order: TicketOrder): string {
  const total = this.prices.price(order.ticketType).plus(OnlineShop.BOOKING_FEE).times(order.seats.length);
  try {
    this.seats.reserve(order.screening, order.seats);
  } catch (exception) {
    if (exception instanceof IllegalStateError) {
      return `ERROR ${exception.message}`;
    }
    throw exception;
  }
  if (!this.payments.charge(order.card, total)) {
    this.seats.release(order.screening, order.seats);
    return 'ERROR payment declined';
  }
  this.mailer.send(order.email, `Bilety ${order.screening} ${order.seats.join(',')} = ${total.toString()}`);
  return `OK ${total.toString()}`;
}
```

**Po (`BookingFacade.ts`, nowy plik):**

```typescript
import { IllegalStateError } from '../../../../shared/errors.js';
import type { Money } from '../../../shared/Money.js';
import type { TicketOrder } from '../TicketOrder.js';
import type { PaymentClient } from './PaymentClient.js';
import type { PriceList } from './PriceList.js';
import type { SeatRegistry } from './SeatRegistry.js';
import type { TicketMailer } from './TicketMailer.js';

/** Facade - orkiestracja zakupu w jednym miejscu; różnica kanałów (opłata) przychodzi parametrem. */
export class BookingFacade {
  constructor(
    private readonly seats: SeatRegistry,
    private readonly prices: PriceList,
    private readonly payments: PaymentClient,
    private readonly mailer: TicketMailer,
  ) {}

  book(order: TicketOrder, feePerSeat: Money): string {
    const total = this.prices.price(order.ticketType).plus(feePerSeat).times(order.seats.length);
    try {
      this.seats.reserve(order.screening, order.seats);
    } catch (exception) {
      if (exception instanceof IllegalStateError) {
        return `ERROR ${exception.message}`;
      }
      throw exception;
    }
    if (!this.payments.charge(order.card, total)) {
      this.seats.release(order.screening, order.seats);
      return 'ERROR payment declined';
    }
    this.mailer.send(order.email, `Bilety ${order.screening} ${order.seats.join(',')} = ${total.toString()}`);
    return `OK ${total.toString()}`;
  }
}
```

**Po (`OnlineShop.ts`, cały plik):**

```typescript
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Money } from '../../../shared/Money.js';
import type { TicketOrder } from '../TicketOrder.js';
import type { BookingFacade } from './BookingFacade.js';

export class OnlineShop {
  private static readonly BOOKING_FEE = Money.of('2.00');

  private readonly booking: BookingFacade;

  constructor(booking: BookingFacade) {
    this.booking = requireNonNull(booking, 'booking');
  }

  buy(order: TicketOrder): string {
    return this.booking.book(order, OnlineShop.BOOKING_FEE);
  }
}
```

**Po (`Cinema.ts`, zmienione pola; dochodzi import `BookingFacade`, reszta pliku bez zmian):**

```typescript
private readonly booking = new BookingFacade(this.seats, this.prices, this.payments, this.mailer);
private readonly shop = new OnlineShop(this.booking);
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s30` - zielone; `step1StillLeaksBecauseOnlyTheShopUsesTheFacade` pokazuje, że błąd kasy wciąż żyje.
**Co powiedzieć:** zaczynamy od klienta, którego orkiestracja jest poprawna (kompensuje odmowę płatności). To, czym różnią się kanały, przychodzi parametrem, a nie flagą `if (online)`.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s30 0 1`

### Krok 2: kasa na fasadzie - świadoma zmiana zachowania

**Zmiany:**

1. Zamień cztery parametry konstruktora `BoxOffice` na jeden `booking: BookingFacade` (zmiana sygnatury, ręcznie).
2. `sell` woła `this.booking.book(order, Money.ZERO)`; skopiowaną orkiestrację usuń razem z jej błędem.
3. Popraw tworzenie `BoxOffice` w `Cinema` (wskaże je `npm run typecheck`).

**Przed:**

```typescript
sell(order: TicketOrder): string {
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
    return 'ERROR payment declined';
  }
  this.mailer.send(order.email, `Bilety ${order.screening} ${order.seats.join(',')} = ${total.toString()}`);
  return `OK ${total.toString()}`;
}
```

**Po (`BoxOffice.ts`, cały plik):**

```typescript
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Money } from '../../../shared/Money.js';
import type { TicketOrder } from '../TicketOrder.js';
import type { BookingFacade } from './BookingFacade.js';

export class BoxOffice {
  private readonly booking: BookingFacade;

  constructor(booking: BookingFacade) {
    this.booking = requireNonNull(booking, 'booking');
  }

  sell(order: TicketOrder): string {
    return this.booking.book(order, Money.ZERO);
  }
}
```

**Po (`Cinema.ts`, zmienione pole):**

```typescript
private readonly desk = new BoxOffice(this.booking);
```

**Uruchom:** test równoważności zielony, a `S30BehaviourChangeTest` → `step2FacadeReleasesSeatsForBothChannels` pokazuje zmianę: po odmowie w kasie miejsce A1 jest wolne i sklep może je sprzedać (`OK 27.00` zamiast `ERROR seat taken A1`).
**Co powiedzieć:** usunięcie kopii usuwa także jej błąd, ale to zmiana zachowania kasy. Nazywamy ją, opisujemy testem i zatwierdzamy osobno (osobny commit "kasa zwalnia miejsca po odmowie płatności"). Test `startBoxOfficeLeaksSeatsAfterDeclinedPayment` po `jump` do kroku 2 lub 3 jest pomijany (vitest pokazuje go jako "skipped") - start kasy nie ma już własnej orkiestracji.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s30 1 2`

### Krok 3: ukryte podsystemy i niezmiennik w finally

**Zmiany:**

1. Przenieś klasy `SeatRegistry`, `PriceList`, `PaymentClient` i `TicketMailer` do `BookingFacade.ts` jako klasy bez `export` (treść klas bez zmian) i usuń ich pliki; razem z nimi przenieś z `Cinema.ts` pomocnik `const list = (items: readonly string[]): string => ...`. W imporcie fasady `IllegalArgumentError` dochodzi do `IllegalStateError`, a `Money` staje się importem wartości; importy czterech podsystemów znikają.
2. Konstruktor fasady zrób `private`; dodaj `static create()`, które składa podsystemy, oraz metodę `state()` z dotychczasowego `Cinema.state()`.
3. `Cinema` woła `BookingFacade.create()` i deleguje `state()` do fasady - sam nie widzi już podsystemów.
4. W `book` otocz `charge` blokiem `try/finally`, który zwalnia miejsca, gdy płatność się nie udała (odmowa albo wyjątek bramki).

**Przed (`BookingFacade.ts`, fragment `book`):**

```typescript
if (!this.payments.charge(order.card, total)) {
  this.seats.release(order.screening, order.seats);
  return 'ERROR payment declined';
}
```

**Po (`BookingFacade.ts`, klasa fasady; wyżej w tym samym pliku nieeksportowane klasy `PaymentClient`, `PriceList`, `SeatRegistry`, `TicketMailer` przeniesione bez zmian treści oraz pomocnik `list`):**

```typescript
export class BookingFacade {
  private constructor(
    private readonly seats: SeatRegistry,
    private readonly prices: PriceList,
    private readonly payments: PaymentClient,
    private readonly mailer: TicketMailer,
  ) {}

  /** Składa podsystemy w fasadę (w Javie robi to Cinema przez konstruktor pakietowy). */
  static create(): BookingFacade {
    return new BookingFacade(new SeatRegistry(), new PriceList(), new PaymentClient(), new TicketMailer());
  }

  book(order: TicketOrder, feePerSeat: Money): string {
    const total = this.prices.price(order.ticketType).plus(feePerSeat).times(order.seats.length);
    try {
      this.seats.reserve(order.screening, order.seats);
    } catch (exception) {
      if (exception instanceof IllegalStateError) {
        return `ERROR ${exception.message}`;
      }
      throw exception;
    }
    let paid = false;
    try {
      paid = this.payments.charge(order.card, total);
    } finally {
      if (!paid) {
        this.seats.release(order.screening, order.seats);
      }
    }
    if (!paid) {
      return 'ERROR payment declined';
    }
    this.mailer.send(order.email, `Bilety ${order.screening} ${order.seats.join(',')} = ${total.toString()}`);
    return `OK ${total.toString()}`;
  }

  /** Obserwowalny stan wszystkich podsystemów naraz. */
  state(): string {
    return `miejsca=${this.seats.describe()} platnosci=${list(this.payments.log())} maile=${list(this.mailer.sent())}`;
  }
}
```

**Po (`Cinema.ts`, cały plik):**

```typescript
import { BookingFacade } from './BookingFacade.js';
import { BoxOffice } from './BoxOffice.js';
import { OnlineShop } from './OnlineShop.js';

export class Cinema {
  private readonly booking = BookingFacade.create();
  private readonly shop = new OnlineShop(this.booking);
  private readonly desk = new BoxOffice(this.booking);

  onlineShop(): OnlineShop {
    return this.shop;
  }

  boxOffice(): BoxOffice {
    return this.desk;
  }

  /** Obserwowalny stan wszystkich podsystemów naraz. */
  state(): string {
    return this.booking.state();
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

**Zmiany:**

1. Utwórz nowy plik `FilmLicense.ts`: klasa z polami `readonly filmCode`, `title`, `active`, `validFrom`, `validTo`, `sharePercent`, `minimumGuarantee`, niezmiennikami w konstruktorze i metodami `covers(day)`, `allowsScreeningOn(day)`, `dueFor(revenue)`.
2. W serwisie wydziel dekodowanie pól rekordu do statycznej metody `toLicense(record)` (Extract Method), wołanej zaraz po pobraniu rekordu.
3. Reguły w `canScreen` i `settle` licz na licencji: `license.active`, `license.covers(day)`, `license.dueFor(revenue)` - kody, procenty i grosze znikają z reguł.

**Przed (`SettlementService.ts`, metody `canScreen` i `settle`):**

```typescript
canScreen(filmCode: string, day: LocalDate): boolean {
  const record = this.feed.fetch(filmCode);
  return record !== null
    && record.statusCode === 'A'
    && !day.isBefore(parseBasicIsoDate(record.validFrom))
    && !day.isAfter(parseBasicIsoDate(record.validTo));
}

settle(filmCode: string, day: LocalDate, revenue: Money): string {
  const record = this.feed.fetch(filmCode);
  if (record === null) {
    return `BRAK LICENCJI ${filmCode}`;
  }
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
  return `ROZLICZENIE ${title} ${day.toString()}: przychod ${revenue.toString()}, udzial ${percent}`
    + `%, minimum ${minimum.toString()}, dla dystrybutora ${due.toString()}`;
}
```

**Po (`FilmLicense.ts`, nowy plik):**

```typescript
import { IllegalArgumentError } from '../../../../shared/errors.js';
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Money } from '../../../shared/Money.js';
import type { LocalDate } from '../../../shared/time.js';

/** Licencja filmu w języku kina - typy domeny, niezmienniki w konstruktorze, reguły przy danych. */
export class FilmLicense {
  constructor(
    readonly filmCode: string,
    readonly title: string,
    readonly active: boolean,
    readonly validFrom: LocalDate,
    readonly validTo: LocalDate,
    readonly sharePercent: number,
    readonly minimumGuarantee: Money,
  ) {
    requireNonNull(filmCode, 'filmCode');
    requireNonNull(title, 'title');
    requireNonNull(validFrom, 'validFrom');
    requireNonNull(validTo, 'validTo');
    requireNonNull(minimumGuarantee, 'minimumGuarantee');
    if (validTo.isBefore(validFrom)) {
      throw new IllegalArgumentError('license ends before it starts');
    }
    // number to nie int z Javy: NaN i ułamki też są poza zakresem
    if (!Number.isInteger(sharePercent) || sharePercent < 0 || sharePercent > 100) {
      throw new IllegalArgumentError(`share out of range: ${sharePercent}`);
    }
    if (minimumGuarantee.compareTo(Money.ZERO) < 0) {
      throw new IllegalArgumentError('minimum guarantee must not be negative');
    }
  }

  covers(day: LocalDate): boolean {
    return !day.isBefore(this.validFrom) && !day.isAfter(this.validTo);
  }

  allowsScreeningOn(day: LocalDate): boolean {
    return this.active && this.covers(day);
  }

  /** Udział dystrybutora, ale nie mniej niż minimalna gwarancja. */
  dueFor(revenue: Money): Money {
    return revenue.percent(this.sharePercent).max(this.minimumGuarantee);
  }
}
```

**Po (`SettlementService.ts`, metody `canScreen`, `settle` i nowa `toLicense`; funkcja `parseBasicIsoDate` na końcu pliku bez zmian, w importach dochodzą `DistributorRecord` i `FilmLicense`):**

```typescript
canScreen(filmCode: string, day: LocalDate): boolean {
  const record = this.feed.fetch(filmCode);
  return record !== null && SettlementService.toLicense(record).allowsScreeningOn(day);
}

settle(filmCode: string, day: LocalDate, revenue: Money): string {
  const record = this.feed.fetch(filmCode);
  if (record === null) {
    return `BRAK LICENCJI ${filmCode}`;
  }
  const license = SettlementService.toLicense(record);
  if (!license.active) {
    return `LICENCJA ZAWIESZONA ${filmCode}`;
  }
  if (!license.covers(day)) {
    return `LICENCJA NIEWAZNA ${filmCode} w dniu ${day.toString()}`;
  }
  return `ROZLICZENIE ${license.title} ${day.toString()}: przychod ${revenue.toString()}, udzial `
    + `${license.sharePercent}%, minimum ${license.minimumGuarantee.toString()}`
    + `, dla dystrybutora ${license.dueFor(revenue).toString()}`;
}

private static toLicense(record: DistributorRecord): FilmLicense {
  const title = record.titleUpper.charAt(0) + record.titleUpper.substring(1).toLowerCase();
  return new FilmLicense(
    record.filmCode,
    title,
    record.statusCode === 'A',
    parseBasicIsoDate(record.validFrom),
    parseBasicIsoDate(record.validTo),
    Number.parseInt(record.share.replaceAll('%', '').trim(), 10),
    new Money(new Decimal(record.minGuaranteeCents).dividedBy(100)));
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s31` - zielone.
**Co powiedzieć:** reguły brzmią teraz jak reguły kina, a nie jak `record.statusCode !== 'A'`. Zauważ, że tłumaczenie jest zachłanne: rekord zawieszony z zepsutą datą dawał w start po prostu "LICENCJA ZAWIESZONA", a teraz parsowanie dat wybuchnie wcześniej. Dla poprawnych danych z testu wynik jest ten sam - to przesunięcie trzeba nazwać.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s31 0 1`

### Krok 2: tłumacz jako osobna klasa

**Zmiany:**

1. Utwórz nowy plik `DistributorTranslator.ts` i przenieś do niego `toLicense` (razem z funkcją `parseBasicIsoDate`); serwis dostaje pole `translator` i woła `this.translator.toLicense(record)`.
2. W tłumaczu wydziel pomocnicze `title(...)` i `date(...)` (Extract Method) oraz kod statusu jako stałą `ACTIVE` (Extract Constant).
3. W `date` złap błąd parsera daty i zamień go na `IllegalArgumentError` z komunikatem granicy i zachowaną przyczyną (`cause`).

**Przed:**

```typescript
private static toLicense(record: DistributorRecord): FilmLicense {
  const title = record.titleUpper.charAt(0) + record.titleUpper.substring(1).toLowerCase();
  return new FilmLicense(
    record.filmCode,
    title,
    record.statusCode === 'A',
    parseBasicIsoDate(record.validFrom),
    parseBasicIsoDate(record.validTo),
    Number.parseInt(record.share.replaceAll('%', '').trim(), 10),
    new Money(new Decimal(record.minGuaranteeCents).dividedBy(100)));
}
```

**Po (`DistributorTranslator.ts`, nowy plik):**

```typescript
import { Decimal } from 'decimal.js';

import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import { LocalDate } from '../../../shared/time.js';
import type { DistributorRecord } from '../DistributorRecord.js';
import { FilmLicense } from './FilmLicense.js';

/** Jedyne miejsce, które zna konwencje dystrybutora: kody statusu, daty "yyyyMMdd", grosze, "45%". */
export class DistributorTranslator {
  private static readonly ACTIVE = 'A';

  toLicense(record: DistributorRecord): FilmLicense {
    return new FilmLicense(
      record.filmCode,
      DistributorTranslator.title(record.titleUpper),
      record.statusCode === DistributorTranslator.ACTIVE,
      DistributorTranslator.date(record.validFrom),
      DistributorTranslator.date(record.validTo),
      Number.parseInt(record.share.replaceAll('%', '').trim(), 10),
      new Money(new Decimal(record.minGuaranteeCents).dividedBy(100)));
  }

  private static title(upper: string): string {
    return upper.charAt(0) + upper.substring(1).toLowerCase();
  }

  private static date(legacy: string): LocalDate {
    try {
      return parseBasicIsoDate(legacy);
    } catch (exception) {
      throw new IllegalArgumentError(`invalid legacy date: ${legacy}`, { cause: exception });
    }
  }
}

/** Odpowiednik LocalDate.parse(text, DateTimeFormatter.BASIC_ISO_DATE): "yyyyMMdd" -> LocalDate. */
function parseBasicIsoDate(text: string): LocalDate {
  if (!/^\d{8}$/.test(text)) {
    throw new RangeError(`Text '${text}' could not be parsed`);
  }
  const date = LocalDate.of(Number(text.slice(0, 4)), Number(text.slice(4, 6)), Number(text.slice(6)));
  if (date.toString() !== `${text.slice(0, 4)}-${text.slice(4, 6)}-${text.slice(6)}`) {
    throw new RangeError(`Text '${text}' could not be parsed: invalid date`);
  }
  return date;
}
```

**Po (`SettlementService.ts`, nowe pole i zmienione wywołania; metoda `toLicense` i funkcja `parseBasicIsoDate` znikają z tego pliku razem z importami `Decimal`, `DistributorRecord` i `FilmLicense`, a `Money` i `LocalDate` stają się `import type`):**

```typescript
private readonly translator = new DistributorTranslator();
```

```typescript
canScreen(filmCode: string, day: LocalDate): boolean {
  const record = this.feed.fetch(filmCode);
  return record !== null && this.translator.toLicense(record).allowsScreeningOn(day);
}
```

```typescript
const license = this.translator.toLicense(record);
```

**Uruchom:** test zielony; `S31SolutionTest` → `aclRejectsMalformedLegacyDataAtTheBoundary` dokumentuje nowy komunikat dla daty `2026-10-01` w formacie ISO zamiast `yyyyMMdd`.
**Co powiedzieć:** zepsuty rekord zgłasza się w języku granicy, z komunikatem, który mówi, co jest nie tak. To zmiana typu wyjątku i trzeba ją zatwierdzić. Serwis nadal jednak sam pobiera `DistributorRecord` i obsługuje jego `null`.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s31 1 2`

### Krok 3: port domenowy i warstwa antykorupcyjna

**Zmiany:**

1. Utwórz nowy plik `FilmLicenses.ts`: interfejs z `find(filmCode: string): FilmLicense | undefined`.
2. Utwórz klasę `DistributorAcl implements FilmLicenses`, która łączy klienta dystrybutora z tłumaczem i zamienia `null` z obcego systemu na `undefined`.
3. Zmień typ pola i parametru konstruktora serwisu z `DistributorFeed` na `FilmLicenses`; serwis woła `this.licenses.find(filmCode)` (`npm run typecheck` wskaże miejsca wywołań).
4. Tłumacza przenieś do `DistributorAcl.ts` jako klasę bez `export` i usuń plik `DistributorTranslator.ts`; `SettlementModule` owija klienta w ACL: `new SettlementService(new DistributorAcl(feed))`.

**Przed:** `SettlementService.ts` - jak w blokach Po kroków 1-2; `SettlementModule.settlementService` zwraca `new SettlementService(feed)`.

**Po (`FilmLicenses.ts`, nowy plik):**

```typescript
import type { FilmLicense } from './FilmLicense.js';

/** Port domeny - skąd kino bierze licencje, opisane w języku kina (brak = undefined, nie null). */
export interface FilmLicenses {
  find(filmCode: string): FilmLicense | undefined;
}
```

**Po (`DistributorAcl.ts`, nowy plik; niżej w tym samym module klasa `DistributorTranslator` bez `export` i funkcja `parseBasicIsoDate`, przeniesione z `DistributorTranslator.ts` bez zmian treści):**

```typescript
import { Decimal } from 'decimal.js';

import { IllegalArgumentError } from '../../../../shared/errors.js';
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Money } from '../../../shared/Money.js';
import { LocalDate } from '../../../shared/time.js';
import type { DistributorFeed } from '../DistributorFeed.js';
import type { DistributorRecord } from '../DistributorRecord.js';
import { FilmLicense } from './FilmLicense.js';
import type { FilmLicenses } from './FilmLicenses.js';

/** Anti-Corruption Layer - tylko ta warstwa widzi DistributorFeed i DistributorRecord. */
export class DistributorAcl implements FilmLicenses {
  private readonly feed: DistributorFeed;
  private readonly translator = new DistributorTranslator();

  constructor(feed: DistributorFeed) {
    this.feed = requireNonNull(feed, 'feed');
  }

  find(filmCode: string): FilmLicense | undefined {
    const record = this.feed.fetch(filmCode);
    return record === null ? undefined : this.translator.toLicense(record);
  }
}
```

**Po (`SettlementService.ts`, cały plik):**

```typescript
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { Money } from '../../../shared/Money.js';
import type { LocalDate } from '../../../shared/time.js';
import type { FilmLicenses } from './FilmLicenses.js';

export class SettlementService {
  private readonly licenses: FilmLicenses;

  constructor(licenses: FilmLicenses) {
    this.licenses = requireNonNull(licenses, 'licenses');
  }

  canScreen(filmCode: string, day: LocalDate): boolean {
    return this.licenses.find(filmCode)?.allowsScreeningOn(day) ?? false;
  }

  settle(filmCode: string, day: LocalDate, revenue: Money): string {
    const license = this.licenses.find(filmCode);
    if (license === undefined) {
      return `BRAK LICENCJI ${filmCode}`;
    }
    if (!license.active) {
      return `LICENCJA ZAWIESZONA ${filmCode}`;
    }
    if (!license.covers(day)) {
      return `LICENCJA NIEWAZNA ${filmCode} w dniu ${day.toString()}`;
    }
    return `ROZLICZENIE ${license.title} ${day.toString()}: przychod ${revenue.toString()}, udzial `
      + `${license.sharePercent}%, minimum ${license.minimumGuarantee.toString()}`
      + `, dla dystrybutora ${license.dueFor(revenue).toString()}`;
  }
}
```

**Po (`SettlementModule.ts`, metoda `settlementService`; dochodzi import `DistributorAcl`):**

```typescript
static settlementService(feed: DistributorFeed): SettlementService {
  return new SettlementService(new DistributorAcl(feed));
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

**Zmiany:**

1. Utwórz nowy plik `SelectionMediator.ts`: interfejs z metodą `seatToggled(seat: string, selected: boolean): void`.
2. `SeatSelectionScreen implements SelectionMediator` (jak "dialog" w GoF): reakcję z `SeatMap.click` (aktualizacja koszyka, przeliczenie sumy, odświeżenie przycisku) wydziel i przenieś do metody `seatToggled` w ekranie (Extract Method + Move Method).
3. `SeatMap` dostaje mediatora w konstruktorze (`new SeatMap(this)` w ekranie), a jej `connect(...)` i pola `basket`, `summary`, `payButton` znikają; `click` zgłasza tylko zdarzenie.

**Przed:**

```typescript
click(seat: string): void {
  if (!this.hall.has(seat) || this.sold.has(seat)) {
    return;
  }
  if (this.selected.delete(seat)) {
    this.basket.drop(seat);
  } else {
    this.selected.add(seat);
    this.basket.add(seat);
  }
  this.summary.recalculate(this.basket.seats());
  this.payButton.refresh(this.basket.seats().length, this.summary.total());
}
```

**Po (`SelectionMediator.ts`, nowy plik):**

```typescript
/** Mediator - komponent zgłasza, CO się stało, a mediator decyduje, KTO ma zareagować. */
export interface SelectionMediator {
  seatToggled(seat: string, selected: boolean): void;
}
```

**Po (`SeatMap.ts`, pola, konstruktor i `click`; metoda `connect` znika, importy `Basket`/`PayButton`/`PriceSummary` zastępują `requireNonNull` i `SelectionMediator`, pozostałe metody bez zmian):**

```typescript
export class SeatMap {
  private readonly hall = SeatMap.hall();
  private readonly sold = new Set<string>(['B2']);
  private readonly selected = new Set<string>();
  private readonly mediator: SelectionMediator;

  constructor(mediator: SelectionMediator) {
    this.mediator = requireNonNull(mediator, 'mediator');
  }

  /** Akcja użytkownika: zaznacza albo odznacza miejsce; sprzedane i spoza sali są ignorowane. */
  click(seat: string): void {
    if (!this.hall.has(seat) || this.sold.has(seat)) {
      return;
    }
    const nowSelected = !this.selected.delete(seat);
    if (nowSelected) {
      this.selected.add(seat);
    }
    this.mediator.seatToggled(seat, nowSelected);
  }
```

**Po (`SeatSelectionScreen.ts`, nagłówek klasy, pola, konstruktor i nowa metoda `seatToggled`; dochodzi `import type { SelectionMediator }`, metoda `render` bez zmian):**

```typescript
export class SeatSelectionScreen implements SelectionMediator {
  readonly seatMap = new SeatMap(this);
  readonly basket = new Basket();
  readonly priceSummary = new PriceSummary();
  readonly payButton = new PayButton();

  constructor() {
    this.basket.connect(this.seatMap, this.priceSummary, this.payButton);
    this.priceSummary.connect(this.basket, this.payButton);
    this.payButton.connect(this.seatMap, this.basket, this.priceSummary);
  }

  seatToggled(seat: string, selected: boolean): void {
    if (selected) {
      this.basket.add(seat);
    } else {
      this.basket.drop(seat);
    }
    this.priceSummary.recalculate(this.basket.seats());
    this.payButton.refresh(this.basket.seats().length, this.priceSummary.total());
  }
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s32` - zielone.
**Co powiedzieć:** komponent mówi "co się stało", a nie "kto ma zareagować". `new SeatMap(this)` w inicjalizatorze pola przekazuje `this` przed końcem konstrukcji ekranu - bezpieczne tylko dlatego, że mediator działa dopiero po akcji użytkownika. Pozostałe komponenty są nadal połączone wprost: jeden komponent naraz.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s32 0 1`

### Krok 2: wszystkie zdarzenia i jedno refresh()

**Zmiany:**

1. Dodaj do interfejsu zdarzenia `seatRemoved`, `basketCleared`, `voucherApplied` i `seatsPaid(seats: readonly string[])`, a ich obsługę w ekranie.
2. `Basket`, `PriceSummary` i `PayButton` dostają mediatora w konstruktorze i zgłaszają zdarzenia zamiast odświeżać sąsiadów; przycisk zachowuje jeszcze `connect(basket, summary)` - jedyne połączenie wprost.
3. Powtórzoną parę "przelicz sumę, odśwież przycisk" wydziel w ekranie do prywatnej metody `refresh()` (Extract Method), a pozostałe kopie zastąp jej wywołaniem - każdą ręcznie, duplikaty trzeba znaleźć samemu.

**Przed:** komponenty i ekran - kod kroku 1 (m.in. `seatToggled` jak w bloku Po kroku 1).

**Po (`SelectionMediator.ts`, cały interfejs):**

```typescript
export interface SelectionMediator {
  seatToggled(seat: string, selected: boolean): void;

  seatRemoved(seat: string): void;

  basketCleared(): void;

  voucherApplied(): void;

  seatsPaid(seats: readonly string[]): void;
}
```

**Po (`Basket.ts`, pola, konstruktor oraz `remove` i `clear`; metoda `connect` i pola `seatMap`/`summary`/`payButton` znikają, importy komponentów zastępują `requireNonNull` i `SelectionMediator`; `add`, `drop`, `seats`, `describe` bez zmian):**

```typescript
export class Basket {
  private readonly items: string[] = [];
  private readonly mediator: SelectionMediator;

  constructor(mediator: SelectionMediator) {
    this.mediator = requireNonNull(mediator, 'mediator');
  }

  /** Akcja użytkownika: usuwa miejsce z koszyka (i odznacza je na mapie). */
  remove(seat: string): void {
    const index = this.items.indexOf(seat);
    if (index < 0) {
      return;
    }
    this.items.splice(index, 1);
    this.mediator.seatRemoved(seat);
  }

  /** Akcja użytkownika: czyści koszyk. Woła ją też mediator po płatności. */
  clear(): void {
    this.items.length = 0;
    this.mediator.basketCleared();
  }
```

**Po (`PriceSummary.ts`, pola, konstruktor i `applyVoucher`; `connect` i pola `basket`/`payButton` znikają, `recalculate`, `total`, `describe` bez zmian):**

```typescript
export class PriceSummary {
  private discountPercent = 0;
  private amount = Money.ZERO;
  private readonly mediator: SelectionMediator;

  constructor(mediator: SelectionMediator) {
    this.mediator = requireNonNull(mediator, 'mediator');
  }

  /** Akcja użytkownika: voucher; nieznany kod usuwa zniżkę. */
  applyVoucher(code: string): void {
    this.discountPercent = code === 'KINO10' ? 10 : 0;
    this.mediator.voucherApplied();
  }
```

**Po (`PayButton.ts`, pola, konstruktor, `connect` i `press`; pole `seatMap` znika, `refresh` i `describe` bez zmian):**

```typescript
export class PayButton {
  private static readonly MAX_SEATS = 4;

  private enabled = false;
  private label = 'Wybierz miejsca';
  private readonly mediator: SelectionMediator;
  // uzupełniane przez connect() - jedyne połączenie wprost, które zostało
  private basket!: Basket;
  private summary!: PriceSummary;

  constructor(mediator: SelectionMediator) {
    this.mediator = requireNonNull(mediator, 'mediator');
  }

  connect(basket: Basket, summary: PriceSummary): void {
    this.basket = basket;
    this.summary = summary;
  }

  /** Akcja użytkownika: płatność za zawartość koszyka. */
  press(): string {
    if (!this.enabled) {
      return `Nieaktywny: ${this.label}`;
    }
    const paid = this.basket.seats();
    const amount = this.summary.total();
    this.mediator.seatsPaid(paid);
    return `Zaplacono ${amount.toString()} za ${paid.join(',')}`;
  }
```

**Po (`SeatSelectionScreen.ts`, pola, konstruktor i metody mediatora; `render` bez zmian):**

```typescript
export class SeatSelectionScreen implements SelectionMediator {
  readonly seatMap = new SeatMap(this);
  readonly basket = new Basket(this);
  readonly priceSummary = new PriceSummary(this);
  readonly payButton = new PayButton(this);

  constructor() {
    this.payButton.connect(this.basket, this.priceSummary);
  }

  seatToggled(seat: string, selected: boolean): void {
    if (selected) {
      this.basket.add(seat);
    } else {
      this.basket.drop(seat);
    }
    this.refresh();
  }

  seatRemoved(seat: string): void {
    this.seatMap.deselect(seat);
    this.refresh();
  }

  basketCleared(): void {
    this.seatMap.deselectAll();
    this.refresh();
  }

  voucherApplied(): void {
    this.refresh();
  }

  seatsPaid(seats: readonly string[]): void {
    this.seatMap.markSold(seats);
    this.basket.clear();
  }

  /** Jedno miejsce, które zna kolejność odświeżania: najpierw suma, potem przycisk. */
  private refresh(): void {
    this.priceSummary.recalculate(this.basket.seats());
    this.payButton.refresh(this.basket.seats().length, this.priceSummary.total());
  }
```

**Uruchom:** test zielony.
**Co powiedzieć:** kolejność odświeżania jest zachowaniem - przycisk pokazuje kwotę, więc suma musi być przeliczona wcześniej. Teraz jest zapisana raz zamiast czterech razy. Uwaga na reentrancję: `seatsPaid` woła `basket.clear()`, które znów woła mediatora (`basketCleared`) - kaskada działa, ale jest ukryta.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s32 1 2`

### Krok 3: komponenty znają tylko mediatora

**Zmiany:**

1. Zamień w interfejsie `seatsPaid(seats: readonly string[]): void` na `payRequested(): string` (zmiana sygnatury, ręcznie).
2. Zbieranie miejsc i kwoty oraz składanie komunikatu `Zaplacono ...` przenieś z `PayButton.press` do `payRequested` w mediatorze; `press` tylko sprawdza, czy jest aktywny, i deleguje.
3. Usuń `connect` z przycisku razem z polami `basket` i `summary` oraz pusty już konstruktor ekranu.

**Przed (`PayButton.ts`, metoda `press`; w ekranie `seatsPaid` jak w bloku Po kroku 2):**

```typescript
press(): string {
  if (!this.enabled) {
    return `Nieaktywny: ${this.label}`;
  }
  const paid = this.basket.seats();
  const amount = this.summary.total();
  this.mediator.seatsPaid(paid);
  return `Zaplacono ${amount.toString()} za ${paid.join(',')}`;
}
```

**Po (`SelectionMediator.ts`, zmieniona ostatnia metoda interfejsu; pozostałe bez zmian):**

```typescript
/** Zwraca komunikat dla użytkownika. */
payRequested(): string;
```

**Po (`PayButton.ts`, metoda `press`; pola `basket`/`summary`, metoda `connect` i importy `Basket`/`PriceSummary` znikają, reszta bez zmian):**

```typescript
press(): string {
  if (!this.enabled) {
    return `Nieaktywny: ${this.label}`;
  }
  return this.mediator.payRequested();
}
```

**Po (`SeatSelectionScreen.ts`, `payRequested` w miejsce `seatsPaid`; konstruktor z `connect` znika, reszta bez zmian):**

```typescript
payRequested(): string {
  const paid = this.basket.seats();
  const amount = this.priceSummary.total();
  this.seatMap.markSold(paid);
  this.basket.clear();
  return `Zaplacono ${amount.toString()} za ${paid.join(',')}`;
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
