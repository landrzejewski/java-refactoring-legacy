# Moduł 4. Podstawowe refaktoryzacje - warsztat praktyczny

## Przewodnik prowadzącego po przykładach w kodzie

## 1. Uruchamianie przykładów i testów

Wszystkie polecenia uruchamiamy z katalogu głównego repozytorium.

### Java

~~~bash
mvn -q compile && java -cp target/classes pl.training.module4.Module4Examples
mvn test -Dtest='pl.training.module4.**'
~~~

Pojedyncze klasy testowe (przydatne podczas demonstracji):

~~~bash
mvn test -Dtest='RentalQuoteServiceCharacterizationTest'
mvn test -Dtest='RentalQuoteStagesEquivalenceTest'
mvn test -Dtest='LocaleIndependentFormattingTest'
mvn test -Dtest='RentalPricingTest,PriceBreakdownTest'
mvn test -Dtest='EquipmentCatalogTest'
~~~

Alternatywnie z IDE: prawy przycisk na pakiecie `pl.training.module4` w `src/test/java` i "Run tests".

Oczekiwany wynik programu demonstracyjnego:

~~~text
RENTAL QUOTE
Customer: ACME
Equipment: GENERATOR
Days: 8
Base: 960.00
Discount: 96.00
Insurance: 64.00
Delivery: 25.00
Net: 953.00
VAT: 219.19
Total: 1172.19
Quote stages equivalent: true
Catalog snapshot isolated: true
~~~

### C#

~~~bash
cd csharp && dotnet run --project src/Training.Module4
cd csharp && dotnet test
~~~

Kod: `csharp/src/Training.Module4` (katalogi `Stage0`..`Stage3`, `Pricing`, `Encapsulation`, `Model`), testy: `csharp/tests/Training.Module4.Tests`.

### TypeScript

~~~bash
cd typescript && npm ci && npm run build && npm run module4
cd typescript && npm test
~~~

Kod: `typescript/src/module4` (katalogi `stage0`..`stage3`, `pricing`, `encapsulation`, `model`), testy: `typescript/test/module4`.

## 2. Mapa przykładów

| Nr | Przykład | Sekcja teorii | Pliki (src/main/java/pl/training/module4/...) | Testy (src/test/java/pl/training/module4/...) |
| --- | --- | --- | --- | --- |
| P1 | Model wejścia | 12.1 | `model/EquipmentType.java`, `model/RentalRequest.java` | `model/RentalRequestTest.java` |
| P2 | Etap 0: kod początkowy | 12.2, 1 | `stage0/RentalQuoteService.java` | `stage0/RentalQuoteServiceCharacterizationTest.java` |
| P3 | Etap 1: lokalne transformacje | 12.3, 2, 3, 4, 5, 6.1, 11 | `stage1/RentalQuoteService.java` | `RentalQuoteStagesEquivalenceTest.java`, `LocaleIndependentFormattingTest.java` |
| P4 | Extract Class: wycena | 12.4, 7, 8 | `pricing/PriceBreakdown.java`, `pricing/RentalPricing.java` | `pricing/RentalPricingTest.java`, `pricing/PriceBreakdownTest.java` |
| P5 | Etap 2: delegat po przeniesieniu | 12.5, 7.2 | `stage2/RentalQuoteService.java` | `RentalQuoteStagesEquivalenceTest.java`, `LocaleIndependentFormattingTest.java` |
| P6 | Etap 3: Inline Method | 12.6, 6.3, 6.4 | `stage3/RentalQuoteService.java` | `RentalQuoteStagesEquivalenceTest.java`, `LocaleIndependentFormattingTest.java` |
| P7 | Test równoważności etapów i niezależność od locale | 12.7 | wszystkie cztery `stageN/RentalQuoteService.java` | `RentalQuoteStagesEquivalenceTest.java`, `LocaleIndependentFormattingTest.java` |
| P8 | Encapsulate Field i Encapsulate Collection | 12.8, 9, 10 | `encapsulation/LegacyEquipmentCatalog.java`, `encapsulation/AccessorBasedEquipmentCatalog.java`, `encapsulation/EquipmentCatalog.java` | `encapsulation/EquipmentCatalogTest.java` |
| P9 | Uruchamialny przykład | 12.9 | `Module4Examples.java` | `Module4ExamplesTest.java` |

Powiązanie z ćwiczeniami: P2 -> P3 to rozwiązanie ćwiczenia 1, P5 -> P6 to część praktyczna ćwiczenia 2, P3 -> P4/P5/P6 to ćwiczenie 3, P8 to ćwiczenie 4. Podczas ćwiczeń nie pokazuj P3-P6 ani `AccessorBasedEquipmentCatalog` i `EquipmentCatalog` przed omówieniem.

Sugerowana kolejność na zajęciach: teoria sekcji 1-11 z krótkimi odwołaniami do kodu, potem P1 i P2, ćwiczenie 1, P3, ćwiczenie 2 (część A), P5 i P6 na żywo jako część B, ćwiczenie 3, P4 i P7, ćwiczenie 4, P8, P9 na zakończenie.

---

## P1. Model wejścia

**Sekcja teorii:** 12.1 Kontrakt szkoleniowy.

**Co ilustruje:** stabilny warunek wstępny wszystkich etapów. `RentalRequest` to rekord z konstruktorem kompaktowym: odrzuca `null` w nazwie klienta i typie sprzętu (`NullPointerException`), pustą nazwę i liczbę dni `<= 0` (`IllegalArgumentException`).

**Pliki:** `src/main/java/pl/training/module4/model/EquipmentType.java`, `src/main/java/pl/training/module4/model/RentalRequest.java`.

**Na co zwrócić uwagę:**

- Walidacja rekordu nie jest przedmiotem refaktoryzacji. Dzięki niej kolejne etapy nie muszą same obsługiwać niepoprawnych dni.
- Rekord zapamiętuje nazwę klienta w oryginalnej postaci (`" Acme "`). Normalizacja (`strip().toUpperCase(Locale.ROOT)`) jest odpowiedzialnością prezentacji dokumentu, nie modelu.
- Komponenty rekordu są częścią API (konstruktor kanoniczny, akcesory, `equals`, `hashCode`, `toString`). Warto to przypomnieć przy sekcji 7.5 (Move Field w rekordzie to szersza zmiana API).

**Testy:** `RentalRequestTest.rejectsInvalidInput` sprawdza odrzucenie pustej nazwy i zerowej liczby dni.

**Demonstracja:** pokaż rekord w 1 minutę, zapytaj grupę: "Czy dodanie tu kolejnej walidacji byłoby refaktoryzacją?" (nie, zmienia zbiór akceptowanych danych, sekcja 1.2).

---

## P2. Etap 0: kod początkowy i test charakterystyki

**Sekcja teorii:** 12.2, a także 1.3 (pętla robocza).

**Co ilustruje:** poprawnie działający, ale nieczytelny kod legacy: jednoliterowe nazwy, magiczne literały, wycena i formatowanie w jednej metodzie. Test charakterystyki rejestruje zachowanie przed pierwszą zmianą.

**Pliki:** `src/main/java/pl/training/module4/stage0/RentalQuoteService.java`, `src/test/java/pl/training/module4/stage0/RentalQuoteServiceCharacterizationTest.java`.

**Na co zwrócić uwagę:**

- `money(...)` (skala 2, HALF_UP) jest wywoływane po każdym kroku pośrednim. Miejsca zaokrąglania są częścią kontraktu.
- Rabat liczony jest wyłącznie od kosztu podstawowego `a`, nie od dodatków.
- Opłata dostawy `new BigDecimal("25.00")` nie przechodzi przez `money`, bo już ma skalę 2. Wartości zerowe to `money(BigDecimal.ZERO)`, czyli `0.00` (skala istotna dla `toPlainString`).
- Dokument jest sklejany konkatenacją. Konkatenacja `int` nie zależy od locale; to ważne dla P7.
- Stawka rabatu `disc` jest polem instancyjnym, a nie stałą. Na etapie 1 pozostaje polem, bo "podróżuje" potem razem z wyceną.

**Testy:** `RentalQuoteServiceCharacterizationTest`:

- `documentsCompleteGeneratorQuote` - pełny dokument bajt po bajcie (w tym `" Acme "` -> `ACME` i końcowy `\n`),
- `documentsDiscountBoundary` - 6 dni bez rabatu, 7 dni z rabatem 84.00,
- `documentsVatRounding` - wiertnica 1 dzień, VAT 9.20, suma 49.19,
- `distinguishesInsuranceFromDelivery` - dodatki niezależne od siebie,
- `documentsDiscountRounding` - wiertnica 15 dni, rabat 59.99 (zaokrąglenie 59.985).

**Demonstracja na żywo:**

1. Pokaż metodę `createQuote` i poproś grupę o wypisanie wszystkich decyzji domenowych ukrytych w kodzie (próg 7, 0.10, 8.00, 25.00, 0.23, zaokrąglanie).
2. Uruchom test charakterystyki (zielony).
3. Zmień `>= 7` na `> 7` i uruchom ponownie: `documentsDiscountBoundary` powinien się zaczerwienić. To jest krok 2 pętli roboczej (test wykrywa kontrolowaną zmianę). Cofnij zmianę.
4. Pytanie do grupy: "Czy test charakterystyki mówi, że te reguły są biznesowo poprawne?" (nie, rejestruje obserwacje, aby struktura nie zmieniła ich przypadkiem).

---

## P3. Etap 1: lokalne transformacje

**Sekcje teorii:** 12.3; techniki z sekcji 2 (Rename), 3 (Extract Method), 4 (Extract Variable, Extract Constant), 5 (Replace Magic Numbers), 6.1 (Inline Variable), 11 (Encapsulate Conditional).

**Co ilustruje:** seria małych transformacji w jednej klasie, bez zmiany kolejności obliczeń i miejsc zaokrąglania.

**Plik:** `src/main/java/pl/training/module4/stage1/RentalQuoteService.java` (porównuj z `stage0`).

**Mapowanie technik na kod:**

| Technika | Miejsce w etapie 1 |
| --- | --- |
| Rename | `r` -> `request`, `a` -> `baseRentalCost`, `d` -> `discount`, `i` -> `insuranceCost`, `f` -> `deliveryCost`, `n` -> `netAmount`, `v` -> `vat`, `t` -> `total`, `rates` -> `dailyRates`, `disc` -> `longRentalDiscountRate` |
| Extract Constant / Replace Magic Numbers | `LONG_RENTAL_DAYS`, `INSURANCE_DAILY_RATE`, `DELIVERY_FEE`, `VAT_RATE`, `ZERO_MONEY` |
| Extract Variable | `dailyRate`, `rentalDays` |
| Extract Method | `calculateDiscount`, `calculateInsuranceCost`, `calculateDeliveryCost`, `buildDocument` |
| Encapsulate Conditional | `qualifiesForLongRentalDiscount` |
| Inline Variable | usunięta zmienna `q` przed `return` |

Kluczowy fragment:

~~~java
private BigDecimal calculateDiscount(
        RentalRequest request,
        BigDecimal baseRentalCost) {
    if (!qualifiesForLongRentalDiscount(request)) {
        return ZERO_MONEY;
    }
    return money(baseRentalCost.multiply(longRentalDiscountRate));
}

private static boolean qualifiesForLongRentalDiscount(
        RentalRequest request) {
    return request.days() >= LONG_RENTAL_DAYS;
}
~~~

**Na co zwrócić uwagę:**

- Nazwy stałych opisują rolę (`LONG_RENTAL_DAYS`), a nie wartość (`SEVEN`), sekcja 5.1.
- `ZERO_MONEY = new BigDecimal("0.00")` zastępuje `money(BigDecimal.ZERO)`. Informuje o skali zera w dokumencie, a nie tylko optymalizuje tworzenie obiektu.
- Stawki sprzętu (`dailyRates`) i `longRentalDiscountRate` zostają polami instancyjnymi, bo w etapie 2 przeniosą się razem z wyceną.
- `buildDocument` używa `String.format(Locale.ROOT, ...)`. To celowe: `String.formatted(...)` użyłoby domyślnego locale i mogłoby zmienić cyfry w `%d` (patrz P7). To dobry przykład, że "kosmetyczna" zmiana sposobu budowania tekstu może zmienić zachowanie.
- `buildDocument` ma osiem parametrów. To sygnał (sekcja 3.2), że brakuje spójnego pojęcia; w P4 pojawi się `PriceBreakdown`.
- Nie wydzielono metod dla `netAmount`, `vat`, `total`; nazwy zmiennych wystarczają (sekcja 14.1 teorii).

**Testy:** `RentalQuoteStagesEquivalenceTest` (etap 1 jest jednym z czterech porównywanych), `LocaleIndependentFormattingTest`.

**Demonstracja na żywo:**

1. Otwórz obok siebie `stage0` i `stage1` (widok porównania w IDE).
2. Przejdź tabelę technik od góry; przy każdej pytaj: "Który test by to złapał, gdybyśmy się pomylili?".
3. Pokaż `qualifiesForLongRentalDiscount` i zapytaj: "Czy ta jednoliniowa metoda powinna zostać zinlinowana?" (nie, niesie nazwę reguły domenowej, sekcja 6.3).
4. Pytanie o `String.format(Locale.ROOT, ...)`: "Co by się stało przy `formatted`?"
5. Uruchom `RentalQuoteStagesEquivalenceTest`.

---

## P4. Extract Class: `PriceBreakdown` i `RentalPricing`

**Sekcje teorii:** 12.4; 7 (Move Method, Move Field), 8 (Extract Class).

**Co ilustruje:** wydzielenie odpowiedzialności za wycenę (stawki, progi, zaokrąglenie, kolejność kalkulacji) do własnej klasy i jawny typ wyniku.

**Pliki:** `src/main/java/pl/training/module4/pricing/PriceBreakdown.java`, `src/main/java/pl/training/module4/pricing/RentalPricing.java`.

**Na co zwrócić uwagę:**

- `RentalPricing.calculate` ma dokładnie ten sam algorytm co etap 1 (te same metody i stałe, przeniesione bez zmiany).
- Konstruktor `RentalPricing` jest nowym API i definiuje własną poprawność: kopiuje mapę do `EnumMap`, a potem `Map.copyOf`, normalizuje stawki HALF_UP do dwóch miejsc, sprawdza dodatniość po normalizacji, kompletność stawek dla każdego `EquipmentType` i zakres rabatu `[0, 1]`. Dla poprawnych danych wynik jest taki sam jak wcześniej, ale dla niepoprawnych konfiguracji zachowanie jest inne. Teoria podkreśla, że w produkcji taka walidacja to osobna, świadoma decyzja.
- `RentalPricing.standard()` zawiera jedyne źródło prawdy dla stawek domyślnych.
- `PriceBreakdown` to rekord z konstruktorem kompaktowym: odrzuca `null`, wartości ujemne i używa `setScale(2, RoundingMode.UNNECESSARY)`, więc kwota o trzech miejscach po przecinku daje `ArithmeticException`. Rekord nie zaokrągla, tylko pilnuje formatu kwot.

Fragment pokazujący kontrakt kwot:

~~~java
private static BigDecimal money(BigDecimal amount, String name) {
    Objects.requireNonNull(amount, name);
    if (amount.signum() < 0) {
        throw new IllegalArgumentException(name + " must not be negative");
    }
    return amount.setScale(2, RoundingMode.UNNECESSARY);
}
~~~

**Testy:**

- `RentalPricingTest.calculatesApprovedPriceBreakdown` - pełny rozkład dla generatora na 8 dni z dodatkami,
- `rejectsIncompleteRateConfiguration`, `rejectsNonPositiveDailyRate`, `rejectsNegativeDailyRate`, `rejectsDailyRateThatRoundsToZero` (0.004 -> 0.00), `rejectsDiscountOutsideClosedUnitInterval` (-0.01 i 1.01) - nowy kontrakt konfiguracji,
- `ownsDefensiveCopyOfDailyRates` - mutacja mapy źródłowej po konstrukcji nie wpływa na wycenę,
- `PriceBreakdownTest.rejectsAmountsOutsideItsMoneyContract` - `null` (NPE), ujemna kwota (IAE), `1.001` (ArithmeticException).

**Demonstracja na żywo:**

1. Pokaż `stage1` i zaznacz kolorem (lub komentarzem) wszystko, co dotyczy wyceny: stałe, `dailyRates`, `longRentalDiscountRate`, metody `calculate*`, `money`. Pozostaje `buildDocument`.
2. Omów sekwencję bezpiecznego przeniesienia z sekcji 12.4 (prywatny punkt `calculatePrice`, nowy typ, delegowanie, usuwanie danych bez odczytów, jedno źródło prawdy).
3. Pokaż `RentalPricing`: podkreśl, że ciało `calculate` to kod z etapu 1.
4. Pokaż konstruktor i zapytaj: "Czy ta walidacja to refaktoryzacja?" (nie, to nowy kontrakt nowego API; dla danych etapu 0 wynik ten sam).
5. Uruchom `RentalPricingTest` i `PriceBreakdownTest`.
6. Pytania o własność (sekcja 8.3): czy `RentalPricing` potrzebuje referencji zwrotnej do usługi? Kto go tworzy? Czy może być współdzielony między ofertami?

---

## P5. Etap 2: delegat po przeniesieniu

**Sekcje teorii:** 12.5, 7.2 (delegat oddziela przeniesienie od migracji klientów).

**Co ilustruje:** usługa dokumentu po Extract Class. Zostaje prywatny delegat `calculatePrice` jako celowy punkt przejściowy bezpiecznej migracji.

**Plik:** `src/main/java/pl/training/module4/stage2/RentalQuoteService.java`.

~~~java
public RentalQuoteService() {
    this(RentalPricing.standard());
}

public RentalQuoteService(RentalPricing pricing) {
    this.pricing = Objects.requireNonNull(pricing);
}

public String createQuote(RentalRequest request) {
    Objects.requireNonNull(request, "request");
    PriceBreakdown price = calculatePrice(request);
    return buildDocument(request, price);
}

private PriceBreakdown calculatePrice(RentalRequest request) {
    return pricing.calculate(request);
}
~~~

**Na co zwrócić uwagę:**

- Bezargumentowy konstruktor zachowuje publiczną sygnaturę etapów 0 i 1 (także deskryptor konstruktora dla wcześniej skompilowanego klienta, jeśli typ zastępuje poprzedni pod tą samą nazwą). Przeciążenie umożliwia jawne wstrzyknięcie konfiguracji. Usunięcie bezargumentowego konstruktora byłoby osobną zmianą API.
- `buildDocument` przyjmuje teraz dwa parametry zamiast ośmiu; `PriceBreakdown` zastąpił równoległe wartości.
- Klasa dokumentu nie zna już stawek ani algorytmu.
- `Objects.requireNonNull(request, "request")` na wejściu `createQuote`: w etapie 0 `null` też kończył się `NullPointerException` (przy `r.equipmentType()`), więc typ wyjątku się nie zmienia. Można o to zapytać grupę jako ćwiczenie z kontraktu wyjątków.

**Testy:** `RentalQuoteStagesEquivalenceTest`, `LocaleIndependentFormattingTest`.

**Demonstracja na żywo:** to jest część B ćwiczenia 2. Zapytaj grupę, jakie warunki musi spełnić `calculatePrice`, aby Inline Method był bezpieczny (prywatny, jedno wywołanie, niepolimorficzny, bez `synchronized`, bez adnotacji, nie jest celem refleksji ani proxy). Następnie przejdź do P6.

---

## P6. Etap 3: Inline Method

**Sekcje teorii:** 12.6, 6.3, 6.4.

**Co ilustruje:** usunięcie delegata, który nie niesie nazwy domenowej ani granicy.

**Plik:** `src/main/java/pl/training/module4/stage3/RentalQuoteService.java`.

Jedyna różnica względem etapu 2:

~~~java
PriceBreakdown price = pricing.calculate(request);
return buildDocument(request, price);
~~~

**Na co zwrócić uwagę:**

- Inline nie oznacza usuwania każdej jednoliniowej metody. `buildDocument` zostaje, bo nazywa osobną odpowiedzialność i utrzymuje `createQuote` na jednym poziomie abstrakcji. `qualifiesForLongRentalDiscount` zostaje w `RentalPricing`, bo wyraża regułę domenową.
- Najbezpieczniejszy kandydat do Inline Method to prywatny, niepolimorficzny delegat używany w jednym miejscu, dokładnie taki jak tutaj.
- Kontrast z ryzykami z sekcji 6.4: metoda przesłanialna (dynamiczna dyspozycja), `synchronized` (monitor odbiorcy lub obiektu `Class`), adnotacje transakcyjne i proxy, publiczna metoda (kompatybilność źródłowa i binarna).

**Testy:** `RentalQuoteStagesEquivalenceTest`, `LocaleIndependentFormattingTest`.

**Demonstracja na żywo:**

1. W kopii `stage2` (na przykład w scratch branch) ustaw kursor na wywołaniu `calculatePrice` i wykonaj Inline Method z IDE (IntelliJ: Ctrl+Alt+N / Cmd+Alt+N).
2. Pokaż podgląd; porównaj wynik z `stage3`.
3. Uruchom `RentalQuoteStagesEquivalenceTest`.
4. Pytanie: "Gdyby `calculatePrice` była `protected` i przesłonięta w podklasie, co by się stało?"

---

## P7. Test równoważności etapów i niezależność od locale

**Sekcja teorii:** 12.7.

**Co ilustruje:** jak testować serię refaktoryzacji. Każdy etap jest porównywany z niezależnie zapisanym, zatwierdzonym wynikiem, a nie tylko ze starą implementacją (dwie implementacje mogą dzielić ten sam błąd).

**Pliki testowe:** `src/test/java/pl/training/module4/RentalQuoteStagesEquivalenceTest.java`, `src/test/java/pl/training/module4/LocaleIndependentFormattingTest.java`.

**Co sprawdzają:**

- `RentalQuoteStagesEquivalenceTest.everyStageProducesApprovedQuote` - test parametryzowany (`@MethodSource("approvedQuotes")`), siedem scenariuszy: pełna oferta generatora, ubezpieczenie bez dostawy, dostawa bez ubezpieczenia, dzień przed progiem rabatu, próg rabatu, zaokrąglenie rabatu, zaokrąglenie VAT. Dla każdego scenariusza wszystkie etapy 0-3 muszą dać ten sam, zapisany w teście dokument.
- `LocaleIndependentFormattingTest.quoteFormattingDoesNotDependOnDefaultFormatLocale` - ustawia `Locale.Category.FORMAT` na `ar-EG`, sprawdza, że etap 0 daje `Days: 8\n` i że etapy 1-3 są identyczne z etapem 0. W bloku `finally` przywraca locale.

**Na co zwrócić uwagę:**

- Zestaw nie jest dowodem dla wszystkich danych, ale chroni ryzyka wybrane dla tej sekwencji: oba typy sprzętu, oba boki progu, wszystkie dodatki, zaokrąglenia.
- `String.formatted(...)` użyłoby domyślnego locale, a `%d` w `ar-EG` mogłoby dać cyfry arabsko-indyjskie w polu dni. Stąd `String.format(Locale.ROOT, ...)` w etapach 1-3.
- Test zmieniający globalne locale musi je przywrócić, inaczej może wpływać na inne testy.

**Demonstracja na żywo:**

1. W `stage3` tymczasowo zamień `String.format(Locale.ROOT, """...""", ...)` na `"""...""".formatted(...)`.
2. Uruchom `LocaleIndependentFormattingTest` i pokaż czerwony wynik (różnica w linii `Days:`).
3. Cofnij zmianę.
4. Pytanie do grupy: "Dlaczego nie wystarczy `assertEquals(stage0.createQuote(r), stage3.createQuote(r))`?"

---

## P8. Encapsulate Field i Encapsulate Collection: katalog sprzętu

**Sekcje teorii:** 12.8, 9 (Encapsulate Field), 10 (Encapsulate Collection).

**Co ilustruje:** trzy stany tej samej klasy: wyjściowy, pośredni zachowujący kontrakt oraz docelowy ze świadomą zmianą kontraktu.

**Pliki:**

- `src/main/java/pl/training/module4/encapsulation/LegacyEquipmentCatalog.java` - publiczne pole `name` i publiczne `final` pole `dailyRates` z aliasem do mapy klienta,
- `src/main/java/pl/training/module4/encapsulation/AccessorBasedEquipmentCatalog.java` - pola prywatne, `name()`, `setName(String)`, `dailyRates()` zwracające ten sam modyfikowalny obiekt (faza A),
- `src/main/java/pl/training/module4/encapsulation/EquipmentCatalog.java` - własny `EnumMap`, `renameTo`, `dailyRateFor`, `changeDailyRate`, `dailyRates()` zwracające `Map.copyOf` (faza B).

Serce stanu docelowego:

~~~java
public EquipmentCatalog(String name, Map<EquipmentType, BigDecimal> dailyRates) {
    this.name = validName(name);
    Objects.requireNonNull(dailyRates, "dailyRates");
    this.dailyRates = new EnumMap<>(EquipmentType.class);
    dailyRates.forEach(this::changeDailyRate);
}

public void changeDailyRate(EquipmentType type, BigDecimal newRate) {
    Objects.requireNonNull(type, "type");
    Objects.requireNonNull(newRate, "newRate");
    BigDecimal normalizedRate = newRate.setScale(2, RoundingMode.HALF_UP);
    if (normalizedRate.signum() <= 0) {
        throw new IllegalArgumentException("Daily rate must be positive");
    }
    dailyRates.put(type, normalizedRate);
}
~~~

**Na co zwrócić uwagę:**

- `final` na polu mapy blokuje tylko ponowne przypisanie referencji, nie mutację mapy (sekcja 4.4).
- `AccessorBasedEquipmentCatalog` nie jest projektem docelowym. Daje punkt kontrolny: zmieniła się składniowa forma dostępu, a aliasowanie i akceptowane wartości (także `" "` jako nazwa) pozostały.
- Dla opublikowanej biblioteki usunięcie publicznego pola łamie zgodność źródłową i binarną oraz zmienia wynik refleksji. Akcesor nie zachowuje automatycznie publicznego API pola.
- `EquipmentCatalog` nie jest równoważny z poprzednimi dla niepoprawnych danych: odrzuca pustą nazwę, niedodatnie stawki, `null`; kopiuje wejście; zwraca migawkę. To zmiana kontraktu, która wymaga osobnego wymagania i komunikacji.
- Walidacja stawki jest w jednym miejscu: konstruktor używa `changeDailyRate`.
- Normalizacja następuje przed sprawdzeniem inwariantu: `0.004` -> `0.00` jest odrzucane, `42.005` -> `42.01`.
- `Map.copyOf` to migawka, nie żywy widok (`Collections.unmodifiableMap`), i nie jest kopią głęboką. Tutaj wystarcza, bo `BigDecimal` jest niemutowalny. `Map.copyOf` odrzuca `null`.
- Migawka nie czyni obiektu bezpiecznym wątkowo.
- `dailyRateFor` dla brakującego typu rzuca `IllegalArgumentException` zamiast zwracać `null`.

**Testy:** `EquipmentCatalogTest`:

- `legacyCatalogSharesItsMutableMapWithTheCaller` - mutacja mapy źródłowej jest widoczna w katalogu,
- `accessorBasedCatalogPreservesAliasesDuringControlledMigration` - `assertSame(source, catalog.dailyRates())`, a `setName(" ")` nadal jest akceptowane,
- `encapsulatedCatalogOwnsRatesAndReturnsUnmodifiableSnapshots` - mutacja źródła niewidoczna, migawka nie widzi późniejszej zmiany, `put` na migawce rzuca `UnsupportedOperationException`,
- `changesNameOnlyThroughValidatedOperation` - `renameTo(" ")` rzuca `IllegalArgumentException`,
- `normalizesRateBeforeCheckingItsInvariant` - 42.005 -> 42.01, 0.004 odrzucone.

**Demonstracja na żywo:**

1. Pokaż `LegacyEquipmentCatalog` i uruchom pierwszy test. Zapytaj: "Ile dróg zapisu do stanu katalogu widzicie?" (zapis `name`, mutacja mapy przez pole, mutacja mapy źródłowej przez klienta).
2. Pokaż `AccessorBasedEquipmentCatalog` i drugi test. Podkreśl `assertSame` i akceptowaną nazwę `" "`: to nadal ten sam kontrakt.
3. Pokaż `EquipmentCatalog`. Przy `dailyRates()` zapytaj: "Widok czy migawka? Co jeśli klient potrzebuje obserwować zmiany?" Odwołaj się do tabeli trzech kontraktów z sekcji 10.2.
4. Uruchom cały `EquipmentCatalogTest`.
5. Pytanie kontrolne: "Czy `EquipmentCatalog` można nazwać refaktoryzacją `LegacyEquipmentCatalog`?" (nie w całości; faza B to zmiana kontraktu).

---

## P9. Uruchamialny przykład

**Sekcja teorii:** 12.9.

**Co ilustruje:** program sprawdzający równoważność etapu 0 i etapu 3 dla pełnej oferty generatora oraz semantykę migawki katalogu.

**Pliki:** `src/main/java/pl/training/module4/Module4Examples.java`, test `src/test/java/pl/training/module4/Module4ExamplesTest.java` (`runsAllModuleExamples` - `main` nie rzuca wyjątku).

**Na co zwrócić uwagę:**

- Jeżeli dokumenty etapu 0 i 3 się różnią, program rzuca `IllegalStateException("Refactoring changed the quote")`, więc `Module4ExamplesTest` staje się czerwony.
- Linia `Quote stages equivalent: true` jest drukowana stałym tekstem po udanym sprawdzeniu; prawdziwą weryfikacją jest wcześniejszy warunek.
- `Catalog snapshot isolated: true` pokazuje, że migawka pobrana przed `changeDailyRate` zachowała 39.99.

**Demonstracja:** uruchom `mvn -q compile && java -cp target/classes pl.training.module4.Module4Examples` na zakończenie modułu i porównaj wynik z oczekiwanym (sekcja 1 tego przewodnika). Opcjonalnie pokaż ten sam program w C# lub TS.

---

## 10. Pytania przekrojowe do grupy (na zakończenie)

- Które kroki w etapach 0-3 były czysto mechaniczne, a które wymagały decyzji projektowej (na przykład walidacja w `RentalPricing`, wybór migawki w `EquipmentCatalog`)?
- Gdzie kończy się refaktoryzacja, a zaczyna zmiana kontraktu w tym module?
- Jaki był najmniejszy test dający wiarygodny sygnał na każdym etapie?
- Dlaczego seria zatrzymała się na etapie 3, a nie rozbiła `buildDocument` na mniejsze metody?
