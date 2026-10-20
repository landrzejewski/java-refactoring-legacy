# Moduł 3. Testowanie a bezpieczeństwo - przewodnik prowadzącego po przykładach

Dokument opisuje kod modułu 3 w projekcie: co ilustruje każdy przykład, jak go pokazać na żywo i na co zwrócić uwagę grupy. Slajdy: `src/main/resources/md/slides/03-testowanie-a-bezpieczenstwo-slajdy.md`.

Wszystkie ścieżki Java są względne wobec katalogu głównego repozytorium. Kod produkcyjny: `src/main/java/pl/training/module3/`, testy: `src/test/java/pl/training/module3/`. Przykład z kodem bez testów w skali całego systemu (golden master) leży w warsztacie CineLegacy: `src/test/java/pl/training/workshop/legacy/`.

## Uruchamianie przykładów i testów

### Java

```bash
# z katalogu głównego repozytorium
mvn -q compile && java -cp target/classes pl.training.module3.Module3Examples

# testy modułu 3
mvn test -Dtest='pl.training.module3.**'

# golden master starego systemu CineLegacy
mvn -q test -Dtest=CinemaManagerGoldenMasterTest

# pełna weryfikacja z raportem pokrycia JaCoCo (target/site/jacoco/index.html)
mvn clean verify
```

Pojedynczy test, np. `mvn test -Dtest=OrderPlacementServiceTest`. Z IDE: uruchom `Module3Examples.main` albo cały katalog testów `src/test/java/pl/training/module3`.

Oczekiwane wyjście `Module3Examples`:

```text
Formatter outputs equal: true
Published event: OrderPlaced[orderId=1, total=25.00]
Placed order: PlacedOrder[orderId=1, total=25.00, authorizationId=AUTH-DEMO]
Reminder: developer@example.com renews on 2026-09-06
Break Dependencies: ALLOWED
Parallel verification: 53.97/Agreement
```

### C#

```bash
cd csharp && dotnet run --project src/Training.Module3
cd csharp && dotnet test tests/Training.Module3.Tests
```

Kod: `csharp/src/Training.Module3` (podkatalogi `BreakDependencies/Before|After` i `Incremental`), testy: `csharp/tests/Training.Module3.Tests` (te same nazwy klas testowych co w Javie).

### TypeScript

```bash
cd typescript && npm ci && npm run build && npm run module3
cd typescript && npx vitest run test/module3
```

Kod: `typescript/src/module3` (podkatalogi `breakdependencies/before|after` i `incremental`), testy: `typescript/test/module3` (np. `LegacyInvoiceFormatterCharacterization.test.ts`, `SeamedReminderService.test.ts`). W TS dodatkowo występują pomocnicze `Clock.ts` i `LocalDate.ts`, zastępujące typy JDK.

## Uwaga organizacyjna: gotowe rozwiązania w repozytorium

Pakiet `pl.training.module3` zawiera jednocześnie wersje wyjściowe i referencyjne:

| Rola | Pliki |
| --- | --- |
| wersje wyjściowe (legacy) | `LegacyInvoiceFormatter.java`, `LegacyReminderService.java`, `DeliveryFee.java` (z niepełnym testem), `breakdependencies/before/*` |
| wersje referencyjne (after) | `InvoiceFormatter.java`, `SeamedReminderService.java`, `ReminderService.java`, `breakdependencies/after/*`, `incremental/*` |
| testy referencyjne | `FormatterEquivalenceTest.java`, `SeamedReminderServiceTest.java`, `ReminderServiceTest.java`, `BreakDependenciesEquivalenceTest.java`, `incremental/*Test.java` |

Przed ćwiczeniami 1 i 3 poproś uczestników, aby nie otwierali plików referencyjnych i pracowali na własnych klasach (np. `WorkshopInvoiceFormatter`, `WorkshopReminderService`) albo na gałęzi. Nazwy własnych klas nie mogą kolidować z istniejącymi.

## Mapa przykładów

| Nr | Przykład | Sekcja slajdów | Pliki produkcyjne | Testy | Zadania |
| --- | --- | --- | --- | --- | --- |
| 1 | Obiekty zastępcze w usłudze zamówień | 2.1-2.7 (także 1.2) | `src/main/java/pl/training/module3/OrderPlacementService.java` | `src/test/java/pl/training/module3/OrderPlacementServiceTest.java` | Aktywność 2.7, Ćwiczenie 2A |
| 2 | Pokrycie linii a gałęzi | 3.1-3.7 | `src/main/java/pl/training/module3/DeliveryFee.java`, `src/main/java/pl/training/module1/DiscountPolicy.java` | `src/test/java/pl/training/module3/DeliveryFeeLineCoverageTest.java`, `src/test/java/pl/training/module1/DiscountPolicyTest.java` | Aktywność 3.7, Ćwiczenie 2B |
| 3 | Test charakteryzujący formatera | 4.2-4.4 | `src/main/java/pl/training/module3/LegacyInvoiceFormatter.java`, `src/main/java/pl/training/module3/InvoiceLine.java` | `src/test/java/pl/training/module3/LegacyInvoiceFormatterCharacterizationTest.java` | Ćwiczenie 1 |
| 4 | Golden master starego systemu | 4.5-4.6 | `src/main/java/pl/training/workshop/legacy/CinemaManager.java` | `src/test/java/pl/training/workshop/legacy/CinemaManagerGoldenMasterTest.java`, `src/test/java/pl/training/workshop/legacy/CinemaManagerScript.java` | - |
| 5 | Refaktoryzacja formatera pod ochroną testów i test różnicowy | 4.7-4.9 | `src/main/java/pl/training/module3/InvoiceFormatter.java` (after) | `src/test/java/pl/training/module3/FormatterEquivalenceTest.java` | Ćwiczenie 1 |
| 6 | Punkt wyjścia do rozrywania zależności | 4.10-4.11 (także 1.4) | `src/main/java/pl/training/module3/LegacyReminderService.java`, `src/main/java/pl/training/module3/Subscription.java` | brak (celowo) | Ćwiczenie 3 |
| 7 | Minimalny szew przejściowy (Subclass and Override Method) | 4.12 | `src/main/java/pl/training/module3/SeamedReminderService.java` | `src/test/java/pl/training/module3/SeamedReminderServiceTest.java` | Ćwiczenie 3 |
| 8 | Jawne zależności (Parameterize Constructor, Extract Interface) | 4.13, 2.6 | `src/main/java/pl/training/module3/ReminderService.java` | `src/test/java/pl/training/module3/ReminderServiceTest.java` | Ćwiczenie 3 |
| 9 | Break Dependencies | 4.14-4.16 | `src/main/java/pl/training/module3/breakdependencies/...` | `src/test/java/pl/training/module3/BreakDependenciesEquivalenceTest.java` | Aktywność 4.20 |
| 10 | Równoległa weryfikacja kalkulatora | 4.18-4.19 | `src/main/java/pl/training/module3/incremental/...` | `src/test/java/pl/training/module3/incremental/...` | Ćwiczenie 4 |
| 11 | Program demonstracyjny | przekrojowo (3.4-3.5) | `src/main/java/pl/training/module3/Module3Examples.java` | `src/test/java/pl/training/module3/Module3ExamplesTest.java` | - |

Sekcja 1 (piramida, portfel, punkt zmiany i punkt testowania) nie ma osobnego kodu. Ilustruj ją na przykładach 1, 6 i 10 (np. punkt zmiany i punkt testowania z 1.4 na przykładzie 6, portfel z 1.6 na przykładzie 1: test logiki, fake repozytorium i brakujący test adaptera). Slajdy 4.17 (scratch refactoring) i 4.16 (ruchy IDE bez testów) pokazuje się na żywo na przykładach 6 i 9 albo na scenie s02 warsztatu (`pl.training.workshop.m3.s02_breakdependencies`).

---

## Przykład 1. Obiekty zastępcze w usłudze zamówień (`pl.training.module3`)

**Sekcja teorii:** 2.1 katalog obiektów zastępczych, 2.2 weryfikacja stanu a zachowania, 2.3 przykładowa usługa, 2.4 stub/fake/spy w jednym teście, 2.5 mock i ryzyka, 2.6 nie mockuj tego, czego nie posiadasz. Nawiązanie do 1.2 (testuj stabilne zachowanie).

**Co ilustruje:** rola obiektu zastępczego wynika ze sposobu użycia w teście, a nie z techniki jego utworzenia. W jednym teście występują jednocześnie stub, fake i spy; w drugim mock i stub. Wszyscy współpracownicy są portami należącymi do aplikacji, więc zastępowanie ich nie łamie reguły "nie mockuj tego, czego nie posiadasz".

**Klasy:**

- `src/main/java/pl/training/module3/OrderPlacementService.java` - usługa `place(sku, quantity, paymentToken)`: walidacja ilości, cena z katalogu razy ilość, zaokrąglenie `HALF_UP` do 2 miejsc, obciążenie płatności, zapis `OrderDraft`, publikacja `OrderPlaced`, zwrot `PlacedOrder`. Współpracownicy są zagnieżdżonymi interfejsami: `ProductCatalog`, `PaymentGateway`, `OrderRepository`, `EventPublisher`.
- `src/test/java/pl/training/module3/OrderPlacementServiceTest.java` - klasy pomocnicze `InMemoryOrderRepository` (fake), `RecordingEventPublisher` (spy), `ExpectingPaymentGateway` (mock).

Kluczowy fragment ręcznie napisanego mocka (bez biblioteki):

```java
@Override
public String charge(String paymentToken, BigDecimal amount) {
    assertEquals(expectedToken, paymentToken);
    assertEquals(expectedAmount, amount);
    calls++;
    return authorizationId;
}

void verify() {
    assertEquals(1, calls);
}
```

**Na co zwrócić uwagę:**

- Wszystkie obiekty zastępcze są napisane ręcznie (lambdy i klasy wewnętrzne), bez Mockito. To dobrze pokazuje, że rola nie zależy od biblioteki.
- `publisherStub` to stub, a nie dummy, bo `publish` jest faktycznie wywoływane (Aktywność 2.7). Prawdziwy dummy nigdy nie byłby użyty; bezpieczny dummy rzuca wyjątek przy użyciu, żeby test nie kłamał.
- Mock także zwraca wartość (`AUTH-9`), więc role techniczne się nakładają. Klasyfikacja ma wyjaśniać intencję testu.
- Pierwszy test weryfikuje głównie stan i wynik (weryfikacja stanu), drugi - protokół płatności (weryfikacja zachowania). Weryfikacja zachowania jest tu uzasadniona, bo liczba obciążeń jest kontraktem.
- `find` istnieje tylko w `InMemoryOrderRepository`, nie w interfejsie `OrderRepository`. Nie należy dodawać metod do interfejsu produkcyjnego tylko dla testów.
- Fake w pamięci nie sprawdza SQL, mapowania `BigDecimal`, ograniczeń, transakcji ani generatora identyfikatorów (2.5). Brakujący element portfela z 1.6 to wąski test integracyjny adaptera repozytorium.
- "Nie mockuj tego, czego nie posiadasz": gdyby zamiast `PaymentGateway` test mockował bezpośrednio klienta HTTP operatora płatności, utrwalałby wyobrażenie o cudzym API. Port `PaymentGateway` jest nasz; adapter do operatora wymaga osobnego testu z prawdziwą technologią lub sandboxem.
- Przykład upraszcza transakcję płatności i zapisu. Jeśli ktoś zapyta, co gdy zapis się nie uda po obciążeniu karty: to świadome uproszczenie, nie wzorzec.
- Pułapka: nazwa `spy` w bibliotekach (np. Mockito) oznacza częściowy mock wywołujący prawdziwe metody, a nie klasyczny spy rejestrujący wywołania.

**Testy:**

| Test | Obiekty zastępcze | Co weryfikuje |
| --- | --- | --- |
| `placesOrderUsingStubFakeAndSpy` | `catalogStub` (lambda, cena 12.50), `paymentStub` (lambda, `AUTH-7`), `repositoryFake` (`InMemoryOrderRepository`), `publisherSpy` (`RecordingEventPublisher`) | wynik (`total` = 25.00, `authorizationId`), stan repozytorium przez `find`, listę opublikowanych zdarzeń |
| `verifiesPaymentProtocolUsingMock` | `catalogStub` (40.00), `paymentMock` (`ExpectingPaymentGateway`), `repositoryFake`, `publisherStub` (pusta lambda) | protokół płatności: token `TOKEN-2`, kwota 120.00, dokładnie jedno wywołanie (`verify()`) |

**Demonstracja na żywo (ok. 10 min):**

1. Pokaż slajd 2.1 (katalog) i zostaw go na ekranie obok IDE.
2. Pokaż `OrderPlacementService.java` i zapytaj: "Ile obserwowalnych efektów ma `place`?" (wynik, wyjątek dla `quantity <= 0`, obciążenie, zapis, zdarzenie).
3. Otwórz `OrderPlacementServiceTest` i przy każdej zmiennej zapytaj grupę o rolę, zanim przeczytasz nazwę na głos.
4. Uruchom `mvn test -Dtest=OrderPlacementServiceTest`.
5. Kontrolna mutacja: w `place` wywołaj `paymentGateway.charge` dwukrotnie. Pokaż, że pada tylko test z mockiem (pierwszy test ze stubem nie wykrywa podwójnego obciążenia). Przywróć kod.
6. Pytania do grupy: "Czy warto sprawdzać kolejność save i publish?" "Który z tych testów przeżyje zmianę kolejności obliczeń wewnątrz `place`?" "Czego nie wykryje `repositoryFake`?"
7. Zauważ, że test nie pokrywa ścieżki wyjątku `quantity <= 0` - dobry wstęp do rozmowy o pokryciu (przykład 2).

---

## Przykład 2. Pokrycie linii a gałęzi (`pl.training.module3`, `pl.training.module1`)

**Sekcja teorii:** 3.1 co mierzy JaCoCo, 3.2 pokrycie linii a gałęzi, 3.3 pełne linie i brakująca gałąź, 3.4-3.5 zalety, ograniczenia, progi i nadużycia, 3.6 pokrycie w legacy, Aktywność 3.7.

**Co ilustruje:** test wykonuje wszystkie linie metody, ale tylko część stron warunków. Pokrycie linii wygląda dobrze, pokrycie gałęzi ujawnia lukę. `DiscountPolicy` (z modułu 1) pokazuje to na dwóch niezależnych warunkach, `DeliveryFee` - na jednym, z ryzykiem biznesowym, którego test nie chroni.

**Klasy:**

- `src/main/java/pl/training/module3/DeliveryFee.java` - `fee(boolean premium)`: domyślnie 100, dla premium 0.
- `src/test/java/pl/training/module3/DeliveryFeeLineCoverageTest.java` - jedyny test: `premiumCustomerHasFreeDelivery` sprawdza `fee(true) == 0`.
- `src/main/java/pl/training/module1/DiscountPolicy.java` - `discountPercent(orderValue, vip)`: +10 od progu 100, +5 dla VIP (slajd 3.2).

```java
public static int fee(boolean premium) {
    int fee = 100;

    if (premium) {
        fee = 0;
    }

    return fee;
}
```

**Na co zwrócić uwagę:**

- Kod ma kształt "przypisanie domyślne + nadpisanie w `if`", dlatego linie są w 100% wykonane przy jednym wywołaniu. Przy `if/else` luka byłaby widoczna także w pokryciu linii.
- JaCoCo pokaże na linii `if (premium)` żółty znacznik "1 of 2 branches missed".
- Prywatny konstruktor nigdy nie jest wywoływany, więc JaCoCo pokaże go jako niepokrytą metodę (konstruktor jest metodą z perspektywy kodu bajtowego, 3.1). Dobry przykład na "raport wymaga interpretacji": to nie jest ryzyko.
- Żaden inny test w projekcie nie używa `DeliveryFee`, więc globalne pokrycie innych klas niczego tu nie zmienia (3.6).
- Plik testu celowo pozostaje niepełny - jest materiałem do Aktywności 3.7 i Ćwiczenia 2B. Nie dopisuj brakującego testu przed zajęciami.
- `src/test/java/pl/training/module1/DiscountPolicyTest.java` zawiera dokładnie test ze slajdu 3.2 (`combinesThresholdAndVipDiscount`), więc raport JaCoCo dla `DiscountPolicy` pokazuje 100% linii i 2 z 4 gałęzi bez żadnych przygotowań.

**Testy:** `DeliveryFeeLineCoverageTest` chroni tylko regułę darmowej dostawy dla premium. Reguła opłaty standardowej (100) nie jest chroniona. `Module3ExamplesTest` (przykład 11) podnosi pokrycie wielu klas modułu, ale nie chroni żadnej reguły.

**Demonstracja na żywo (ok. 5-7 min):**

1. Pokaż kod i test. Zapytaj: "Jakie jest pokrycie linii? A gałęzi?"
2. Uruchom `mvn clean verify`, otwórz `target/site/jacoco/index.html`, przejdź do `pl.training.module3` > `DeliveryFee` > `fee(boolean)`.
3. Pokaż kolumny Missed Instructions / Missed Branches oraz kolorowanie źródła.
4. Kontrolna mutacja na żywo: zmień `100` na `200`, uruchom testy - wszystko zielone. Przywróć kod. Pytanie: "Co nam mówiło 100% linii?"
5. Pytanie do grupy: "Gdybyśmy dopisali `DeliveryFee.fee(false);` bez asercji, co pokaże raport i co faktycznie zyskamy?"
6. Wspomnij o teście mutacyjnym (3.4-3.5): narzędzie typu PIT robiłoby automatycznie to, co właśnie zrobiliśmy ręcznie ze `100` na `200`.

---

## Przykład 3. Test charakteryzujący formatera faktury (`pl.training.module3`)

**Sekcja teorii:** 4.2 testy charakteryzujące, 4.3 kod testu, 4.4 dobór przypadków.

**Co ilustruje:** zapisanie zastanego, obserwowanego zachowania (kompletny tekst faktury) przed jakąkolwiek zmianą struktury.

**Klasy:**

- `src/main/java/pl/training/module3/InvoiceLine.java` - rekord `InvoiceLine(String sku, int quantity, BigDecimal unitPrice)`.
- `src/main/java/pl/training/module3/LegacyInvoiceFormatter.java` - jedna metoda `format(customer, lines)` łącząca normalizację klienta, obliczenia, zaokrąglenia i budowę tekstu.
- `src/test/java/pl/training/module3/LegacyInvoiceFormatterCharacterizationTest.java`.

**Na co zwrócić uwagę:**

- Nazwy testów zaczynają się od `documents` - zespół świadomie dokumentuje zastany wynik, a nie potwierdza wymaganie (4.3). Po potwierdzeniu reguł można je przemianować językiem wymagań.
- Asercja porównuje cały tekst, łącznie z pustym ostatnim elementem w `String.join` (końcowy znak nowego wiersza).
- Subtotal jest liczony z niezaokrąglonych wartości wierszy, a prezentowane są wartości zaokrąglone. Istniejące testy nie ujawniają różnicy między tymi sumami - to luka do wykrycia w Ćwiczeniu 1.
- `UNKNOWN` dla `null` może być historycznym defektem. Test nie nadaje mu statusu wymagania (4.2, 4.9).
- `toUpperCase(Locale.ROOT)` - zależność od locale jest częścią kontraktu; bez `Locale.ROOT` wynik mógłby zależeć od środowiska (np. tureckie "i").

**Testy:**

| Test | Dane | Co utrwala |
| --- | --- | --- |
| `documentsCurrentFormattingAndRounding` | klient `"  Acme  "`, BOOK 2 x 19.99, PEN 1 x 5.00 | `trim` + wielkie litery, wartości wierszy, Subtotal 44.98, Tax 10.35 (23%, `HALF_UP`), Total 55.33, końcowy `\n` |
| `documentsCurrentFallbackForMissingCustomer` | klient `null`, pusta lista | `Customer: UNKNOWN`, zera `0.00`, brak wierszy pozycji |

**Demonstracja na żywo (ok. 10 min):**

1. Pokaż `LegacyInvoiceFormatter` i poproś grupę o wypisanie reguł (bez podpowiadania). Zbierz je na tablicy.
2. Pokaż test charakteryzujący i zestaw: które reguły z tablicy są już zabezpieczone?
3. Uruchom `mvn test -Dtest=LegacyInvoiceFormatterCharacterizationTest`.
4. Kontrolna mutacja: zmień `"0.23"` na `"0.22"` albo usuń `trim()`. Pokaż porażkę i czytelność różnicy tekstu. Przywróć.
5. Pytania: "Czy `UNKNOWN` to wymaganie?" "Co zrobić, jeśli test charakteryzujący ujawni podejrzany wynik?" (nie aktualizować po cichu, tylko rozstrzygnąć: defekt czy kontrakt) "Kiedy snapshot całego tekstu przestaje być czytelny?" (4.5)

---

## Przykład 4. Golden master starego systemu (`pl.training.workshop.legacy`)

**Sekcja teorii:** 4.1 brak testów - od czego zacząć, 4.5 golden master, snapshot i approval testing, 4.6 wektor obserwowalnego zachowania.

**Co ilustruje:** jak zabezpieczyć cały stary system, który nie ma żadnych testów jednostkowych, jednym testem na najszerszej stabilnej granicy. `CinemaManagerScript` odtwarza "jeden dzień kina" (rezerwacje, płatności, zwroty, raport dzienny, rozliczenia) i zwraca pełny wektor zachowania jako tekst, a test porównuje go z zatwierdzonym plikiem.

**Klasy:**

- `src/main/java/pl/training/workshop/legacy/CinemaManager.java` - "serce" starego systemu: statyczna baza, `double` na pieniądze, maile wysyłane z logiki, statyczny zegar `CinemaManager.clock`.
- `src/test/java/pl/training/workshop/legacy/CinemaManagerScript.java` - scenariusz: czyści `LegacyDb`, ustawia sterowany zegar przez `CinemaManager.clock = () -> now[0]`, wykonuje operacje i zbiera wyniki wywołań, maile, SMS-y, operacje bramki płatności i raporty.
- `src/test/java/pl/training/workshop/legacy/CinemaManagerGoldenMasterTest.java` - jedna asercja `assertEquals(approved(), CinemaManagerScript.run())`.
- `src/test/resources/workshop/cinema-manager.approved.txt` - zatwierdzony wynik (47 linii).

**Na co zwrócić uwagę:**

- Golden master nie wymaga rozumienia reguł - wymaga determinizmu. Statyczne pole `clock` to istniejący szew, który ktoś kiedyś zostawił. Bez niego pierwszym ruchem byłoby wprowadzenie szwu na czas (slajd 4.16).
- Wektor (4.6) jest tu wprost: wynik (`book: B1 B2 ...`), błędy (`ERROR: seat taken A5`), efekty (`MAIL to=...`), stan (wolne miejsca, punkty lojalnościowe) i raporty.
- Plik oczekiwany jest wersjonowany i czytelny - różnica po zmianie jest diffem tekstu, który da się przejrzeć w code review. Aktualizacja pliku bez czytania różnicy usuwa zabezpieczenie.
- Golden master jest szeroki: powie, że coś się zmieniło, ale słabo powie, gdzie. Dlatego po wprowadzeniu szwów dokłada się węższe testy (1.7).
- Scenariusz to dane: jeśli przypadek graniczny nie występuje w "jednym dniu kina", golden master go nie chroni. Warto zapytać grupę, czego w scenariuszu brakuje (np. seansu po północy).

**Testy:** `CinemaManagerGoldenMasterTest.oneDayOfCinemaProducesApprovedOutput`.

**Demonstracja na żywo (ok. 5-8 min):**

1. Otwórz `cinema-manager.approved.txt` i przeczytaj kilka linii na głos. Zapytaj: "Które wymiary wektora zachowania tu widać?"
2. Uruchom `mvn -q test -Dtest=CinemaManagerGoldenMasterTest`.
3. Kontrolna mutacja: w `CinemaManager` zmień potrącenie przy zwrocie (np. `3.00` na `2.00`) albo treść jednego maila. Uruchom test i pokaż czytelność różnicy w IDE (porównanie oczekiwanego i rzeczywistego tekstu). Przywróć kod.
4. Pytanie do grupy: "Co by się stało, gdyby ktoś po tej zmianie skopiował nowy wynik do pliku `approved.txt` bez czytania różnicy?"
5. Zapowiedz warsztat: sceny modułu 3 (`pl.training.workshop.m3`) pokazują te same techniki w mniejszej skali.

---

## Przykład 5. Refaktoryzacja formatera pod ochroną testów i test różnicowy (`pl.training.module3`)

**Sekcja teorii:** 4.7 refaktoryzacja pod ochroną testów, 4.8 test różnicowy, 4.9 jawna zmiana zachowania.

**Co ilustruje:** wynik serii małych transformacji (after) oraz test różnicowy porównujący starą i nową implementację.

**Klasy:**

- before: `src/main/java/pl/training/module3/LegacyInvoiceFormatter.java`
- after: `src/main/java/pl/training/module3/InvoiceFormatter.java` - metody `displayedCustomer`, `calculateSubtotal`, `appendLines`, `lineTotal`, `money` oraz stała `TAX_RATE`.
- test różnicowy: `src/test/java/pl/training/module3/FormatterEquivalenceTest.java`.

```java
public String format(String customer, List<InvoiceLine> lines) {
    BigDecimal subtotal = calculateSubtotal(lines);
    BigDecimal tax = money(subtotal.multiply(TAX_RATE));
    BigDecimal total = money(subtotal.add(tax));
    StringBuilder result = new StringBuilder("INVOICE\n")
            .append("Customer: ")
            .append(displayedCustomer(customer))
            .append('\n');
    appendLines(result, lines);
    return result
            .append("Subtotal: ").append(money(subtotal)).append('\n')
            .append("Tax: ").append(tax).append('\n')
            .append("Total: ").append(total).append('\n')
            .toString();
}
```

**Na co zwrócić uwagę:**

- Zmiana semantyki ukryta w strukturze: w wersji after `lineTotal` jest wywoływane dwa razy (raz w sumie, raz przy wypisywaniu). Dla czystego obliczenia to bezpieczne; gdyby obliczenie miało efekt uboczny lub było kosztowne, byłaby to zmiana zachowania lub wydajności (wektor 4.6: czas i współpracownicy).
- Kolejność: after liczy sumy przed budową tekstu, legacy w trakcie pętli. Dla czystych obliczeń bez znaczenia - wewnętrzna kolejność czystych obliczeń zwykle nie jest kontraktem.
- `money(subtotal.add(tax))` - suma całkowita dalej jest liczona z niezaokrąglonego subtotalu i zaokrąglonego podatku, tak jak w legacy. Pokusa "uproszczenia" do sumy zaokrąglonych wartości to zmiana zachowania.
- W repozytorium `LegacyInvoiceFormatterCharacterizationTest` testuje tylko klasę legacy. Dla `InvoiceFormatter` stałych oczekiwań brak - ochrona pochodzi z testu różnicowego. Warto o to zapytać grupę: co się stanie po usunięciu klasy legacy? (test różnicowy znika, trzeba przenieść stałe oczekiwania na nową klasę, 4.8).
- Test różnicowy nie wykryje historycznego defektu obecnego w obu wersjach i potwierdza zgodność tylko dla wykonanych danych.
- Stała stawka, brak waluty i walidacji to świadome uproszczenia. Refaktoryzacja nie upoważnia do ich dodawania.

**Testy:**

- `FormatterEquivalenceTest.refactoringPreservesObservedOutput` (parametryzowany) - dla trzech zestawów danych porównuje `legacy.format(...)` z `refactored.format(...)`:
  - `"Acme"` i pusta lista,
  - `null` i jedna pozycja BOOK 1 x 10.00,
  - `"vip"` i pozycje A 3 x 0.10 oraz B 2 x 19.995 (cena wymagająca zaokrąglenia).
- `LegacyInvoiceFormatterCharacterizationTest` - stałe, przejrzane oczekiwania (dotyczą klasy legacy).

**Demonstracja na żywo (ok. 15 min):**

1. Otwórz obok siebie `LegacyInvoiceFormatter` i `InvoiceFormatter`.
2. Odtwórz na żywo 2-3 pierwsze kroki z 4.7 na kopii klasy legacy (Extract Method w IDE: `displayedCustomer`, `lineTotal`, `money`), po każdym uruchamiając test charakteryzujący na kopii i pokazując `git diff`. To jednocześnie pokaz ruchów IDE o niskim ryzyku (4.16).
3. Pokaż `FormatterEquivalenceTest` i uruchom `mvn test -Dtest=FormatterEquivalenceTest`.
4. Kontrolna mutacja w `InvoiceFormatter.calculateSubtotal`: sumuj `money(lineTotal(line))` zamiast `lineTotal(line)`. Zapytaj grupę przed uruchomieniem, czy test wykryje zmianę. Dla danych z testu (A 3 x 0.10 = 0.30, B 2 x 19.995 = 39.990) wartości zaokrąglone i dokładne sumują się tak samo, więc test pozostanie zielony. To dobry moment, by pokazać ograniczenie testu różnicowego i potrzebę przypadku z Ćwiczenia 1 (dwie pozycje po 0.005). Przywróć kod.
5. Pytania: "Który krok w 4.7 jest najbardziej ryzykowny?" "Co zrobimy, jeśli właściciel produktu każe rzucać wyjątek dla braku klienta?" (4.9: tryb funkcjonalny, osobny test, świadoma zmiana oczekiwania).

---

## Przykład 6. Punkt wyjścia do rozrywania zależności (`pl.training.module3`)

**Sekcja teorii:** 4.10 seam, punkt aktywacji i cel rozrywania, 4.11 punkt wyjścia. Nawiązanie do 1.4 (punkt zmiany i punkt testowania).

**Co ilustruje:** kod, którego nie da się deterministycznie przetestować: globalny czas `LocalDate.now(ZoneOffset.UTC)` i efekt przez `System.out.printf`.

**Klasy:**

- `src/main/java/pl/training/module3/LegacyReminderService.java`
- `src/main/java/pl/training/module3/Subscription.java` - rekord `Subscription(String email, LocalDate renewalDate)`.

**Na co zwrócić uwagę:**

- Warunek ma tylko górną granicę `today + 7`: wysyłka dla dokładnie +7 dni, dla dziś i dla dat przeszłych; brak wysyłki dla +8. Brak dolnej granicy może być defektem, ale jego korekta nie należy do refaktoryzacji.
- Wywołanie statyczne `LocalDate.now(...)` nie daje szwu obiektowego (4.10).
- Czas to problem determinizmu (pośrednie wejście, czyli miejsce na stub), `System.out` to problem obserwacji (pośrednie wyjście, czyli miejsce na spy).
- Testowanie przez zmianę zegara systemowego albo przechwytywanie `System.out` zwiększa sprzężenie testu z procesem uruchomieniowym.
- To dobry materiał na krótki scratch refactoring (4.17): 5 minut agresywnych zmian, lista znalezisk, `git checkout -- .`.

**Testy:** celowo brak. To jest sedno przykładu.

**Demonstracja na żywo (ok. 5 min):**

1. Pokaż klasę i zadaj pytania z 1.4: gdzie zmienimy kod, skąd uruchomimy zachowanie, gdzie widać wynik, które zależności przeszkadzają, jaki najmniejszy test wykryje zmianę?
2. Poproś grupę o podanie trzech przypadków granicznych (+7, +8, przeszłość) i ich zastanego wyniku.
3. Zapytaj: "Jak dziś napisać test dla +7 dni, który przejdzie jutro?"

---

## Przykład 7. Minimalny szew przejściowy (`pl.training.module3`)

**Sekcja teorii:** 4.12 minimalny szew przejściowy (Subclass and Override Method).

**Co ilustruje:** owinięcie trudnych wywołań metodami `protected` i podmiana ich w podklasie testowej. Logika produkcyjna wykonuje te same operacje.

**Klasy:**

- before: `src/main/java/pl/training/module3/LegacyReminderService.java`
- after (etap przejściowy): `src/main/java/pl/training/module3/SeamedReminderService.java` - klasa nie jest `final`, metody `protected LocalDate currentDate()` i `protected void sendMessage(String email, LocalDate renewalDate)`.
- test: `src/test/java/pl/training/module3/SeamedReminderServiceTest.java` z wewnętrzną podklasą `TestableReminderService`.

```java
private static final class TestableReminderService extends SeamedReminderService {
    private final LocalDate today;
    private final List<String> messages = new ArrayList<>();

    @Override
    protected LocalDate currentDate() { return today; }

    @Override
    protected void sendMessage(String email, LocalDate renewalDate) {
        messages.add(email + "|" + renewalDate);
    }
}
```

**Na co zwrócić uwagę:**

- W projekcie etap ma osobną nazwę klasy, żeby wersje mogły współistnieć. W realnej pracy byłaby to mała zmiana istniejącej klasy, wykonana automatem IDE (Extract Method z widocznością `protected`, 4.16).
- Punkt aktywacji: wyrażenie `new TestableReminderService(...)`. Mechanizm: dynamiczne wiązanie metod.
- Zdjęcie `final` i dodanie metod `protected` zmienia kontrakt rozszerzalności i powierzchnię API. Refaktoryzacją jest tylko wtedy, gdy typ jest wewnętrzny i wszyscy konsumenci są pod kontrolą.
- Test jest sprzężony z metodami chronionymi; hierarchia nie ma znaczenia domenowego. To rozwiązanie przejściowe i powinno być tak oznaczone.
- `currentDate()` dalej używa `ZoneOffset.UTC` w produkcji - strefa jest częścią zachowania.

**Testy (dziś = 2026-08-30):**

| Test | Data odnowienia | Oczekiwanie |
| --- | --- | --- |
| `sendsReminderForRenewalExactlySevenDaysAway` | 2026-09-06 (+7) | `true`, jedna wiadomość |
| `doesNotSendReminderMoreThanSevenDaysBeforeRenewal` | 2026-09-07 (+8) | `false`, brak wiadomości |
| `documentsCurrentBehaviorForPastRenewalDate` | 2026-08-29 (przeszłość) | `true`, jedna wiadomość (zastane zachowanie, nazwa z `documents`) |

**Demonstracja na żywo (ok. 10 min):**

1. Pokaż diff między `LegacyReminderService` a `SeamedReminderService` (np. porównanie plików w IDE). Zwróć uwagę, że logika warunku jest identyczna.
2. Pokaż `SeamedReminderServiceTest` i podklasę testową.
3. Uruchom `mvn test -Dtest=SeamedReminderServiceTest`.
4. Kontrolna mutacja: zmień `isAfter` na `!isBefore` albo `plusDays(7)` na `plusDays(6)` i pokaż, który test pada. Przywróć.
5. Pytania: "Gdzie jest punkt aktywacji?" "Jaki koszt ponieśliśmy, dodając `protected`?" "Czy to może zostać na stałe?"

---

## Przykład 8. Jawne zależności jako rozwiązanie docelowe (`pl.training.module3`)

**Sekcja teorii:** 4.13 jawne zależności, 4.15 kolejność rozrywania zależności i czego unikać. Nawiązanie do 2.6 (port we własnym języku).

**Co ilustruje:** zastąpienie dziedziczenia przekazaniem zależności przez konstruktor (Parameterize Constructor) i nazwanie efektu wyjściowego interfejsem (Extract Interface).

**Klasy:**

- before: `src/main/java/pl/training/module3/SeamedReminderService.java` (etap przejściowy)
- after: `src/main/java/pl/training/module3/ReminderService.java` - `final`, konstruktor `ReminderService(Clock clock, ReminderGateway reminderGateway)` z `Objects.requireNonNull`, zagnieżdżony interfejs `ReminderGateway { void send(String email, LocalDate renewalDate); }`.
- test: `src/test/java/pl/training/module3/ReminderServiceTest.java` z `RecordingReminderGateway` (spy) i `Clock.fixed(Instant.parse("2026-08-30T10:00:00Z"), ZoneOffset.UTC)` (fake zegara).

**Na co zwrócić uwagę:**

- Punkt aktywacji obu szwów: wyrażenie `new ReminderService(...)`; argumenty wybierają zachowanie.
- Strefa czasowa przeszła z kodu (`ZoneOffset.UTC`) do obiektu `Clock`. W produkcji trzeba przekazać zegar z tą samą strefą (np. `Clock.systemUTC()`), inaczej zmieni się zachowanie w okolicach północy. To typowa pułapka przy tej transformacji.
- `Clock.fixed` ustawia 10:00 UTC - daleko od granicy doby, więc test nie jest wrażliwy na strefę. Warto zapytać, jaki test wykryłby błędną strefę.
- `ReminderGateway` to port, który należy do nas (2.6). Reguła biznesowa nie zna technologii wysyłki. Rzeczywisty adapter wymaga osobnego, wąskiego testu integracyjnego (serializacja, uwierzytelnienie, odpowiedzi, ponowienia).
- `Module3Examples.runExplicitSeamExample` pokazuje "produkcyjne" złożenie: `Clock.fixed` i lambda drukująca na konsolę jako adapter.
- Po przeniesieniu wywołań i testów `SeamedReminderService` można usunąć osobnym, małym krokiem (4.15 punkt 5).

**Testy:** te same trzy przypadki co w przykładzie 7: `usesInjectedClockAndGateway` (+7, wysyłka), `doesNotSendReminderMoreThanSevenDaysBeforeRenewal` (+8, brak), `preservesCurrentBehaviorForPastRenewalDate` (przeszłość, wysyłka). Zmiana nazwy z `documents...` na `preserves...` sygnalizuje, że zachowanie jest świadomie utrzymane.

**Demonstracja na żywo (ok. 10 min):**

1. Pokaż `SeamedReminderService` i `ReminderService` obok siebie.
2. Pokaż `ReminderServiceTest` i porównaj z `SeamedReminderServiceTest` - test nie dziedziczy już z klasy produkcyjnej.
3. Uruchom `mvn test -Dtest='ReminderServiceTest,SeamedReminderServiceTest'`.
4. Przejdź przez 4.15 (pięć kroków) i dla każdego wskaż, który plik go realizuje.
5. Pytania: "Gdzie jest punkt aktywacji?" "Czego ten test nie sprawdzi?" "Czy każda klasa powinna dostać interfejs?" (4.15: nie).

---

## Przykład 9. Break Dependencies (`pl.training.module3.breakdependencies`)

**Sekcja teorii:** 4.14 Break Dependencies - przed i po, 4.15 kolejność i czego unikać, 4.16 bezpieczne ruchy IDE.

**Co ilustruje:** serwis sam tworzy kalendarz okien serwisowych w inicjalizatorze pola. Po zmianie zależność jest wstrzykiwana przez konstruktor jako najwęższy kontrakt `MaintenanceWindows` (interfejs funkcyjny z jedną metodą `allows`). Reguła biznesowa i walidacja nie zmieniają się, zmienia się tylko miejsce tworzenia zależności.

**Klasy:**

- before: `src/main/java/pl/training/module3/breakdependencies/before/LegacyDeploymentWindowService.java`, `src/main/java/pl/training/module3/breakdependencies/before/StandardMaintenanceWindows.java`
- after: `src/main/java/pl/training/module3/breakdependencies/after/MaintenanceWindows.java`, `src/main/java/pl/training/module3/breakdependencies/after/DeploymentWindowService.java`, `src/main/java/pl/training/module3/breakdependencies/after/StandardMaintenanceWindows.java`
- wspólne: `src/main/java/pl/training/module3/breakdependencies/DeploymentDecision.java`

Kluczowa różnica:

```java
// before
private final StandardMaintenanceWindows maintenanceWindows =
        new StandardMaintenanceWindows();

// after
public DeploymentWindowService(MaintenanceWindows maintenanceWindows) {
    this.maintenanceWindows = Objects.requireNonNull(
            maintenanceWindows, "maintenanceWindows");
}
```

**Na co zwrócić uwagę:**

- Interfejs jest `@FunctionalInterface`, więc test podstawia lambdę jako fake albo spy. Interfejs `sealed` nie nadaje się na otwarty punkt podstawienia.
- Kontrakt zawiera tylko to, czego potrzebuje klient (jedna metoda). Nie kopiujemy całego API implementacji.
- Walidacja odbywa się przed wywołaniem zależności. Po zmianie niepoprawne żądanie nadal nie dociera do kalendarza (test `invalidRequestDoesNotReachInjectedDependency`) - to element wektora zachowania (współpracownicy, 4.6).
- Konstruktor `after` dodaje `requireNonNull` dla zależności. To nowy kontrakt konstrukcji, a nie zmiana zachowania `schedule`.
- Wersja `after` nie ma konstruktora bezargumentowego, więc produkcyjne składanie przenosi się do composition root (`Module3Examples.runBreakDependenciesExample`). Alternatywą przejściową, bezpieczną także bez testów, jest konstruktor domyślny delegujący do `this(new StandardMaintenanceWindows())` - IntelliJ robi to automatem Extract Parameter z opcją "Delegate via overloading method".
- Ryzyka: przeniesienie tworzenia może zmienić czas życia, współdzielenie, inicjalizację statyczną, blokady i granicę transakcji zależności.

**Testy (`BreakDependenciesEquivalenceTest`):**

- `standardDependencyPreservesDecisionsForEveryHour` - decyzje dla godzin 0-23 identyczne w obu wersjach (test różnicowy na pełnej dziedzinie),
- `preservesMaintenanceWindowBoundaries` - granice okna 0, 5 (dozwolone), 6, 23 (poza oknem),
- `injectedSeamControlsTheDecisionAndReceivesTheArgumentsOnce` - spy z lambdą: jedno wywołanie, poprawne argumenty, wynik sterowany przez test,
- `preservesValidationFailuresAndTheirOrder` - typ i komunikat wyjątków, także `(" ", -1)` (pierwszy błąd wygrywa),
- `invalidRequestDoesNotReachInjectedDependency` - zero wywołań zależności przy błędnych danych,
- `rejectsMissingInjectedDependency` - `NullPointerException` z komunikatem `maintenanceWindows`.

**Demonstracja na żywo (ok. 10 min):**

1. Pokaż `LegacyDeploymentWindowService` i zapytaj: jak przetestować decyzję dla godziny 14 bez zmiany kalendarza?
2. Wyodrębnij interfejs z jedną metodą (Extract Interface), dodaj `implements` w implementacji produkcyjnej.
3. Zmień typ pola na interfejs, dodaj konstruktor. Skompiluj, pokaż błąd kompilacji w miejscach tworzenia serwisu (Lean on the Compiler) i przenieś składanie do `Module3Examples`.
4. Uruchom `mvn test -Dtest=BreakDependenciesEquivalenceTest`, omów test ze spy.

---

## Przykład 10. Równoległa weryfikacja kalkulatora (`pl.training.module3.incremental`)

**Sekcja teorii:** 4.18 równoległa weryfikacja, 4.19 granice trybu shadow.

**Co ilustruje:** jak zdobyć dowód zgodności nowej implementacji, gdy reguł starej nie da się w pełni spisać: stara implementacja (`LegacyPriceCalculator`, rabat w procentach 0-100) zostaje schowana za małą abstrakcją `PricingEngine` przez adapter, obok powstaje implementacja kandydująca, a `MigratingPricingEngine` centralizuje wybór ścieżki w trzech jawnie nazwanych trybach: `LEGACY`, `VERIFY`, `CANDIDATE`. Ten sam układ (abstrakcja z dwiema implementacjami) to Branch by Abstraction.

**Klasy:**

- `src/main/java/pl/training/module3/incremental/PricingEngine.java` - jedna metoda `quote(PriceRequest)`. Javadoc zapisuje warunek bezpieczeństwa trybu shadow: implementacje nie wykonują I/O i nie modyfikują zewnętrznego stanu.
- `src/main/java/pl/training/module3/incremental/PriceRequest.java`, `PriceQuote.java` - obiekty wartości. Walidacja **przed** zaokrągleniem (wartość `-0.001` nie staje się `0.00`), `HALF_EVEN`, skala 2 dla pieniędzy i 4 dla stopy rabatu.
- `src/main/java/pl/training/module3/incremental/LegacyPriceCalculator.java` - stary kod, rabat jako procent.
- `src/main/java/pl/training/module3/incremental/LegacyPricingEngineAdapter.java` - tłumaczy stopę (`0.10`) na procent (`10.00`) przez `movePointRight(2)`.
- `src/main/java/pl/training/module3/incremental/CandidatePricingEngine.java` - nowa implementacja; świadomie zaokrągla rabat przed odejmowaniem, tak jak legacy.
- `src/main/java/pl/training/module3/incremental/MigrationMode.java` - enum zamiast flagi boolean (trzy stany).
- `src/main/java/pl/training/module3/incremental/VerificationEvent.java` - zamknięta hierarchia (`sealed`): `Agreement`, `Divergence`, `CandidateFailure`.
- `src/main/java/pl/training/module3/incremental/VerificationReporter.java` - port raportowania, `ignoring()` jako wartość domyślna.
- `src/main/java/pl/training/module3/incremental/MigratingPricingEngine.java` - router migracji.

**Na co zwrócić uwagę:**

- Legacy jest autorytatywne w `VERIFY`: najpierw liczone jest legacy; jeśli ono rzuci wyjątek, kandydat w ogóle nie jest wywoływany.
- Awaria kandydata (`RuntimeException`, także zwrócenie `null`) zamienia się w zdarzenie `CandidateFailure` i nie wpływa na odpowiedź.
- Awaria reportera jest połykana (`tryToReport`) - telemetria nie może zmienić wyniku, ale też nie ma gwarancji dostarczenia zdarzenia.
- `Error` (błąd JVM, np. `AssertionError`) **nie** jest maskowany.
- Przykład jest synchroniczny: kandydat i reporter zwiększają latencję. Produkcyjny shadow wymaga izolacji, timeoutu, bulkheadu, limitu kopiowanego ruchu (4.19).
- Shadow jest bezpieczny dla czystego obliczenia. Nie wolno w ten sposób dublować płatności, wysyłki wiadomości, zapisów czy publikacji zdarzeń - to pokazuje scena s05 warsztatu (`pl.training.workshop.m3.s05_shadowlimits`).
- Zgodność dwóch implementacji nie dowodzi poprawności, bo mogą dzielić ten sam błąd. Dlatego test kontraktowy ma **niezależne** oczekiwane wartości.
- Moment zaokrąglenia: przypadek `0.01 x 1` z rabatem `0.5` daje `0.01` (rabat `0.005` zaokrąglony do `0.00` przed odejmowaniem). Zaokrąglenie dopiero wyniku końcowego dałoby `0.00`.

Serce trybu `VERIFY` (`MigratingPricingEngine.verify`):

```java
PriceQuote legacyQuote = requireQuote(legacy.quote(request), "legacy quote");
try {
    PriceQuote candidateQuote = requireQuote(
            candidate.quote(request), "candidate quote");
    VerificationEvent event = legacyQuote.equals(candidateQuote)
            ? new VerificationEvent.Agreement(request, legacyQuote)
            : new VerificationEvent.Divergence(request, legacyQuote, candidateQuote);
    tryToReport(event);
} catch (RuntimeException failure) {
    tryToReport(new VerificationEvent.CandidateFailure(request, legacyQuote,
            failure.getClass().getName(),
            Objects.toString(failure.getMessage(), "")));
}
return legacyQuote;
```

**Testy:**

- `src/test/java/pl/training/module3/incremental/PricingEngineContractTest.java` - sparametryzowany test kontraktowy uruchamiany dla `LegacyPricingEngineAdapter` i `CandidatePricingEngine`: przykłady kanoniczne (w tym połowa centa i `19.995 x 2` przy `0.12555`), determinizm i skala 2, odrzucenie `null` z komunikatem `request`.
- `src/test/java/pl/training/module3/incremental/MigratingPricingEngineTest.java` - liczba wywołań w `LEGACY` i `CANDIDATE` (nieaktywna implementacja nie jest wołana), `Agreement` i `Divergence` w `VERIFY` z wynikiem legacy, wyjątek kandydata, `null` od kandydata jako `CandidateFailure`, awaria reportera, autorytatywna awaria legacy, niemaskowanie `AssertionError`, `null` request nie dociera do żadnej implementacji, walidacja konstruktora. Testy routera używają stubów i spy (liczniki wywołań) - kolejny przykład ról z sekcji 2.
- `src/test/java/pl/training/module3/incremental/PricingValueObjectsTest.java` - normalizacja i walidacja `PriceRequest` oraz `PriceQuote`.

**Demonstracja na żywo (ok. 10-15 min):**

1. Pokaż `LegacyPriceCalculator` i zapytaj: "jaki jest kontrakt tej metody?". Zbierz odpowiedzi (jednostka rabatu, skale, moment zaokrąglenia, komunikaty wyjątków). Zauważ, że lista grupy prawie nigdy nie jest kompletna - to argument za równoległą weryfikacją.
2. Pokaż `PricingEngine` i adapter, potem `PricingEngineContractTest`. Uruchom go i zwróć uwagę, że IDE pokazuje dwa warianty każdego testu.
3. Eksperyment na żywo: w `CandidatePricingEngine` usuń `.setScale(MONEY_SCALE, ROUNDING)` przy rabacie (zaokrąglenie tylko na końcu, w `PriceQuote`). Uruchom test kontraktowy: przypadek `0.01, 1, 0.5` powinien zawieść (oczekiwane `0.01`, kandydat zwraca `0.00`). Uruchom `MigratingPricingEngineTest` - pozostaje zielony, bo testuje router na stubach, a nie kandydata. Przywróć kod.
4. Pokaż `MigratingPricingEngine` i trzy tryby. Uruchom `Module3Examples`: `Parallel verification: 53.97/Agreement`.
5. Zmień w `Module3Examples.runParallelVerificationExample` kandydata na lambdę `request -> new PriceQuote(BigDecimal.ONE)` i pokaż `Divergence` przy niezmienionej odpowiedzi `53.97`. Przywróć kod.
6. Omów 4.19: co trzeba dodać, żeby to był produkcyjny shadow, i dlaczego nie da się tak sprawdzić płatności.

---

## Przykład 11. Program demonstracyjny (`pl.training.module3`)

**Sekcja teorii:** przekrojowo - 4.8 (równość wyników formatera), 2 (obiekty zastępcze jako lambdy), 4.13 (jawne zależności), 4.14 (composition root), 4.18 (tryb `VERIFY`); jako antyprzykład testu - 3.4-3.5.

**Co ilustruje:** po jednym deterministycznym przebiegu na temat modułu oraz test, który podnosi pokrycie, nie chroniąc żadnej reguły.

**Klasy:**

- `src/main/java/pl/training/module3/Module3Examples.java` - pięć metod: `runBehaviorPreservingRefactoring`, `runTestDoubleExample`, `runExplicitSeamExample`, `runBreakDependenciesExample`, `runParallelVerificationExample`.
- `src/test/java/pl/training/module3/Module3ExamplesTest.java` - `runsAllModuleExamples` sprawdza jedynie, że `main` nie rzuca wyjątku.

**Na co zwrócić uwagę:**

- `Module3ExamplesTest` to przykład testu o słabej asercji (tylko brak wyjątku). Podnosi pokrycie wielu klas, ale nie chroni żadnej reguły - świetna ilustracja do 3.4-3.5 (nadużycia pokrycia). Warto pokazać to w trakcie omawiania raportu JaCoCo: wyłącz na chwilę wszystkie testy oprócz `Module3ExamplesTest` i porównaj procent pokrycia z siłą ochrony.
- `runBehaviorPreservingRefactoring` drukuje tylko `true/false` - to nie jest dowód, tylko pojedyncza obserwacja dla jednego zestawu danych.
- `Module3Examples` pełni też rolę composition root: tu składane są zależności wersji `after` (`new DeploymentWindowService(new StandardMaintenanceWindows())`, `new ReminderService(clock, ...)`).

**Testy:** `Module3ExamplesTest.runsAllModuleExamples` (celowo słaby).

**Demonstracja na żywo:** uruchom na początku modułu jako "mapę" tematów, a pod koniec sekcji 3 wróć do `Module3ExamplesTest` przy dyskusji o pokryciu.

---

## Proponowany plan modułu (ok. 180 min)

| Blok | Czas (orientacyjnie) | Materiał |
| --- | --- | --- |
| Wprowadzenie, `Module3Examples` jako mapa | 3 min | slajdy: Agenda; przykład 11 |
| Sekcja 1: piramida, portfel, Aktywność 1.8 | 12 min | slajdy 1.1-1.8 |
| Sekcja 2: obiekty zastępcze, Aktywność 2.7 | 13 min | przykład 1 |
| Sekcja 3: pokrycie, Aktywność 3.7 | 10 min | przykłady 2 i 11 |
| Ćwiczenie 2 (obiekty zastępcze i pokrycie) | 20 min | przykłady 1 i 2 jako omówienie |
| Sekcja 4: brak testów, charakterystyka, golden master, wektor | 12 min | przykłady 3 i 4 |
| Ćwiczenie 1 (formater) | 35 min | omówienie z przykładem 5 |
| Sekcja 4: szwy, Break Dependencies, ruchy IDE, scratch refactoring, Aktywność 4.20 | 15 min | przykłady 6 i 9 |
| Ćwiczenie 3 (szew) | 30 min | omówienie z przykładami 7 i 8 |
| Sekcja 4: równoległa weryfikacja, Ćwiczenie 4 | 25 min | przykład 10 |
| Podsumowanie, lista kontrolna, sprawdzenie wiedzy | 5 min | slajdy końcowe |

Jeśli grupa ma przejść przez warsztat CineLegacy, sceny modułu 3 (`src/main/resources/md/warsztat/przewodnik/03-testowanie-a-bezpieczenstwo.md`) mogą zastąpić demonstracje przykładów 3, 9 i 10: s01 zamiast przykładu 3, s02 zamiast przykładu 9, s04 i s05 zamiast przykładu 10.
