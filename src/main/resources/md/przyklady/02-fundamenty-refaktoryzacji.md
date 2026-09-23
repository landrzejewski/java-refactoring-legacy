# Moduł 2. Fundamenty refaktoryzacji - przewodnik prowadzącego po przykładach

Dokument opisuje kod modułu 2 w projekcie: co ilustruje każdy przykład, jak go pokazać na żywo i na co zwrócić uwagę grupy. Teoria: `src/main/resources/02-fundamenty-refaktoryzacji.md`.

## Uruchamianie przykładów i testów

### Java (wersja wiodąca)

```bash
# z katalogu głównego repozytorium
mvn -q compile && java -cp target/classes pl.training.module2.Module2Examples

# testy modułu 2
mvn test -Dtest='pl.training.module2.**'

# pełna weryfikacja z raportem pokrycia JaCoCo (target/site/jacoco/index.html)
mvn clean verify
```

Testy można też uruchamiać z IDE (katalog `src/test/java/pl/training/module2`).

Oczekiwane wyjście `Module2Examples`:

```text
Formatter outputs equal: true
Published event: OrderPlaced[orderId=1, total=25.00]
Placed order: PlacedOrder[orderId=1, total=25.00, authorizationId=AUTH-DEMO]
Reminder: developer@example.com renews on 2026-09-06
```

### C#

```bash
cd csharp && dotnet run --project src/Training.Module2
dotnet test
```

Kod: `csharp/src/Training.Module2`, testy: `csharp/tests/Training.Module2.Tests` (te same nazwy klas testowych co w Javie).

### TypeScript

```bash
cd typescript && npm ci && npm run build && npm run module2
npm test
```

Kod: `typescript/src/module2`, testy: `typescript/test/module2` (np. `LegacyInvoiceFormatterCharacterization.test.ts`, `SeamedReminderService.test.ts`). W TS dodatkowo występują pomocnicze `Clock.ts` i `LocalDate.ts`, zastępujące typy JDK.

## Uwaga organizacyjna: gotowe rozwiązania w repozytorium

Pakiet `pl.training.module2` zawiera jednocześnie wersje wyjściowe i referencyjne:

| Rola | Pliki |
| --- | --- |
| wersje wyjściowe (legacy) | `LegacyInvoiceFormatter.java`, `LegacyReminderService.java`, `DeliveryFee.java` (z niepełnym testem) |
| wersje referencyjne (after) | `InvoiceFormatter.java`, `SeamedReminderService.java`, `ReminderService.java` |
| testy referencyjne | `FormatterEquivalenceTest.java`, `SeamedReminderServiceTest.java`, `ReminderServiceTest.java` |

Przed ćwiczeniami 2 i 4 poproś uczestników, aby nie otwierali plików referencyjnych i pracowali na własnych klasach (np. `WorkshopInvoiceFormatter`, `WorkshopReminderService`) albo na gałęzi. Nazwy własnych klas nie mogą kolidować z istniejącymi.

## Mapa przykładów

| Nr | Przykład | Sekcja teorii | Pliki produkcyjne | Testy | Zadania |
| --- | --- | --- | --- | --- | --- |
| P1 | Obiekty zastępcze w usłudze zamówień | 5.2-5.5 (także 3.2) | `src/main/java/pl/training/module2/OrderPlacementService.java` | `src/test/java/pl/training/module2/OrderPlacementServiceTest.java` | Ćwiczenie 3A |
| P2 | Pełne linie, brakująca gałąź | 6.1-6.5 | `src/main/java/pl/training/module2/DeliveryFee.java` | `src/test/java/pl/training/module2/DeliveryFeeLineCoverageTest.java` | Aktywność 6.5, Ćwiczenie 3B |
| P3 | Test charakteryzujący formatera | 7.1-7.5 | `src/main/java/pl/training/module2/LegacyInvoiceFormatter.java`, `src/main/java/pl/training/module2/InvoiceLine.java` | `src/test/java/pl/training/module2/LegacyInvoiceFormatterCharacterizationTest.java` | Ćwiczenie 2 |
| P4 | Refaktoryzacja formatera pod ochroną testów i test różnicowy | 8.1-8.4 | `src/main/java/pl/training/module2/InvoiceFormatter.java` (after) | `src/test/java/pl/training/module2/FormatterEquivalenceTest.java` | Ćwiczenie 2 |
| P5 | Punkt wyjścia do rozrywania zależności | 9.1-9.3 | `src/main/java/pl/training/module2/LegacyReminderService.java`, `src/main/java/pl/training/module2/Subscription.java` | brak (celowo) | Ćwiczenie 4 |
| P6 | Minimalny szew przejściowy (Subclass and Override Method) | 9.4 | `src/main/java/pl/training/module2/SeamedReminderService.java` | `src/test/java/pl/training/module2/SeamedReminderServiceTest.java` | Ćwiczenie 4 |
| P7 | Jawne zależności (Parameterize Constructor, Extract Interface) | 9.5-9.7 | `src/main/java/pl/training/module2/ReminderService.java` | `src/test/java/pl/training/module2/ReminderServiceTest.java` | Ćwiczenie 4 |
| P8 | Program demonstracyjny | przekrojowo (8, 5, 9) | `src/main/java/pl/training/module2/Module2Examples.java` | `src/test/java/pl/training/module2/Module2ExamplesTest.java` | - |

Sekcje 1-4 teorii (definicja, zasady bezpiecznej pracy, rola testów, piramida) nie mają osobnego kodu. Ilustruj je na przykładach P3-P7 (np. pętla małych kroków z 2.3 na P4, punkt zmiany i punkt testowania z 3.4 na P5).

---

## P1. Obiekty zastępcze w usłudze zamówień

**Sekcja teorii:** 5. Obiekty zastępcze (5.2 przykładowa usługa, 5.3 stub/fake/spy w jednym teście, 5.4 weryfikacja stanu i interakcji, 5.5 ryzyka). Nawiązanie do 3.2 (testuj stabilne zachowanie).

**Co ilustruje:** rola obiektu zastępczego wynika ze sposobu użycia w teście, a nie z techniki jego utworzenia. W jednym teście występują jednocześnie stub, fake i spy; w drugim mock i stub.

**Pliki:**

- `src/main/java/pl/training/module2/OrderPlacementService.java` - usługa `place(sku, quantity, paymentToken)`: walidacja ilości, cena z katalogu razy ilość, zaokrąglenie `HALF_UP` do 2 miejsc, obciążenie płatności, zapis `OrderDraft`, publikacja `OrderPlaced`, zwrot `PlacedOrder`. Współpracownicy są zagnieżdżonymi interfejsami: `ProductCatalog`, `PaymentGateway`, `OrderRepository`, `EventPublisher`.
- `src/test/java/pl/training/module2/OrderPlacementServiceTest.java`.

**Testy i co sprawdzają:**

| Test | Obiekty zastępcze | Co weryfikuje |
| --- | --- | --- |
| `placesOrderUsingStubFakeAndSpy` | `catalogStub` (lambda, cena 12.50), `paymentStub` (lambda, `AUTH-7`), `repositoryFake` (`InMemoryOrderRepository`), `publisherSpy` (`RecordingEventPublisher`) | wynik (`total` = 25.00, `authorizationId`), stan repozytorium przez `find`, listę opublikowanych zdarzeń |
| `verifiesPaymentProtocolUsingMock` | `catalogStub` (40.00), `paymentMock` (`ExpectingPaymentGateway`), `repositoryFake`, `publisherStub` (pusta lambda) | protokół płatności: token `TOKEN-2`, kwota 120.00, dokładnie jedno wywołanie (`verify()`) |

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
- `publisherStub` to stub, a nie dummy, bo `publish` jest faktycznie wywoływane (teoria 11.3).
- Mock także zwraca wartość (`AUTH-9`), więc role techniczne się nakładają (5.4). Klasyfikacja ma wyjaśniać intencję testu.
- `find` istnieje tylko w `InMemoryOrderRepository`, nie w interfejsie `OrderRepository`. Nie należy dodawać metod do interfejsu produkcyjnego tylko dla testów.
- Fake w pamięci nie sprawdza SQL, mapowania `BigDecimal`, ograniczeń, transakcji ani generatora identyfikatorów (5.5).
- Przykład upraszcza transakcję płatności i zapisu (teoria 5.2). Jeśli ktoś zapyta, co gdy zapis się nie uda po obciążeniu karty: to świadome uproszczenie, nie wzorzec.
- Pułapka: nazwa `spy` w bibliotekach (np. Mockito) oznacza częściowy mock wywołujący prawdziwe metody, a nie klasyczny spy rejestrujący wywołania.

**Sugerowany przebieg demonstracji (ok. 10 min):**

1. Pokaż `OrderPlacementService.java` i zapytaj: "Ile obserwowalnych efektów ma `place`?" (wynik, wyjątek dla `quantity <= 0`, obciążenie, zapis, zdarzenie).
2. Otwórz `OrderPlacementServiceTest` i przy każdej zmiennej zapytaj grupę o rolę, zanim przeczytasz nazwę na głos.
3. Uruchom `mvn test -Dtest=OrderPlacementServiceTest`.
4. Kontrolna mutacja: w `place` wywołaj `paymentGateway.charge` dwukrotnie. Pokaż, że pada tylko test z mockiem (pierwszy test ze stubem nie wykrywa podwójnego obciążenia). Przywróć kod.
5. Pytania do grupy: "Czy warto sprawdzać kolejność save i publish?" "Który z tych testów przeżyje zmianę kolejności obliczeń wewnątrz `place`?" "Czego nie wykryje `repositoryFake`?"
6. Zauważ, że test nie pokrywa ścieżki wyjątku `quantity <= 0` - dobry wstęp do rozmowy o pokryciu (P2).

---

## P2. Pełne linie, brakująca gałąź (DeliveryFee)

**Sekcja teorii:** 6. Pokrycie kodu (6.1 co mierzy JaCoCo, 6.2 pełne linie i brakująca gałąź, 6.3 pytania do raportu, 6.4 progi i nadużycia, 6.5 aktywność).

**Co ilustruje:** test wykonuje wszystkie linie metody, ale tylko jedną stronę warunku. Pokrycie linii wygląda dobrze, pokrycie gałęzi ujawnia lukę.

**Pliki:**

- `src/main/java/pl/training/module2/DeliveryFee.java` - `fee(boolean premium)`: domyślnie 100, dla premium 0.
- `src/test/java/pl/training/module2/DeliveryFeeLineCoverageTest.java` - jedyny test: `premiumCustomerHasFreeDelivery` sprawdza `fee(true) == 0`.

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
- Prywatny konstruktor nigdy nie jest wywoływany, więc JaCoCo pokaże go jako niepokrytą metodę (konstruktor jest metodą z perspektywy kodu bajtowego, teoria 6.1). Dobry przykład na "raport wymaga interpretacji": to nie jest ryzyko.
- Żaden inny test w projekcie nie używa `DeliveryFee`, więc globalne pokrycie innych klas niczego tu nie zmienia.
- Plik testu celowo pozostaje niepełny - jest materiałem do Aktywności 6.5 i Ćwiczenia 3B. Nie dopisuj brakującego testu przed zajęciami.

**Testy:** `DeliveryFeeLineCoverageTest` chroni tylko regułę darmowej dostawy dla premium. Reguła opłaty standardowej (100) nie jest chroniona.

**Sugerowany przebieg demonstracji (ok. 5-7 min):**

1. Pokaż kod i test. Zapytaj: "Jakie jest pokrycie linii? A gałęzi?"
2. Uruchom `mvn clean verify`, otwórz `target/site/jacoco/index.html`, przejdź do `pl.training.module2` > `DeliveryFee` > `fee(boolean)`.
3. Pokaż kolumny Missed Instructions / Missed Branches oraz kolorowanie źródła.
4. Kontrolna mutacja na żywo: zmień `100` na `200`, uruchom testy - wszystko zielone. Przywróć kod. Pytanie: "Co nam mówiło 100% linii?"
5. Pytanie do grupy: "Gdybyśmy dopisali `DeliveryFee.fee(false);` bez asercji, co pokaże raport i co faktycznie zyskamy?"

---

## P3. Test charakteryzujący formatera faktury

**Sekcja teorii:** 7. Testy charakteryzujące (7.1 cel, 7.2 przebieg, 7.3 kod, 7.4 dobór przypadków, 7.5 golden master/snapshot/approval). Nawiązanie do 2.2 (dwa tryby pracy).

**Co ilustruje:** zapisanie zastanego, obserwowanego zachowania (kompletny tekst faktury) przed jakąkolwiek zmianą struktury.

**Pliki:**

- `src/main/java/pl/training/module2/InvoiceLine.java` - rekord `InvoiceLine(String sku, int quantity, BigDecimal unitPrice)`.
- `src/main/java/pl/training/module2/LegacyInvoiceFormatter.java` - jedna metoda `format(customer, lines)` łącząca normalizację klienta, obliczenia, zaokrąglenia i budowę tekstu.
- `src/test/java/pl/training/module2/LegacyInvoiceFormatterCharacterizationTest.java`.

**Testy i co sprawdzają:**

| Test | Dane | Co utrwala |
| --- | --- | --- |
| `documentsCurrentFormattingAndRounding` | klient `"  Acme  "`, BOOK 2 x 19.99, PEN 1 x 5.00 | `trim` + wielkie litery, wartości wierszy, Subtotal 44.98, Tax 10.35 (23%, `HALF_UP`), Total 55.33, końcowy `\n` |
| `documentsCurrentFallbackForMissingCustomer` | klient `null`, pusta lista | `Customer: UNKNOWN`, zera `0.00`, brak wierszy pozycji |

**Na co zwrócić uwagę:**

- Nazwy testów zaczynają się od `documents` - zespół świadomie dokumentuje zastany wynik, a nie potwierdza wymaganie (7.3). Po potwierdzeniu reguł można je przemianować językiem wymagań.
- Asercja porównuje cały tekst, łącznie z pustym ostatnim elementem w `String.join` (końcowy znak nowego wiersza).
- Subtotal jest liczony z niezaokrąglonych wartości wierszy, a prezentowane są wartości zaokrąglone. Istniejące testy nie ujawniają różnicy między tymi sumami - to luka do wykrycia w Ćwiczeniu 2.
- `UNKNOWN` dla `null` może być historycznym defektem. Test nie nadaje mu statusu wymagania (7.1, 8.4).
- `toUpperCase(Locale.ROOT)` - zależność od locale jest częścią kontraktu; bez `Locale.ROOT` wynik mógłby zależeć od środowiska (np. tureckie "i").

**Sugerowany przebieg demonstracji (ok. 10 min):**

1. Pokaż `LegacyInvoiceFormatter` i poproś grupę o wypisanie reguł (bez podpowiadania). Zbierz je na tablicy.
2. Pokaż test charakteryzujący i zestaw: które reguły z tablicy są już zabezpieczone?
3. Uruchom `mvn test -Dtest=LegacyInvoiceFormatterCharacterizationTest`.
4. Kontrolna mutacja: zmień `"0.23"` na `"0.22"` albo usuń `trim()`. Pokaż porażkę i czytelność różnicy tekstu. Przywróć.
5. Pytania: "Czy `UNKNOWN` to wymaganie?" "Co zrobić, jeśli test charakteryzujący ujawni podejrzany wynik?" (nie aktualizować po cichu, 2.2) "Kiedy snapshot całego tekstu przestaje być czytelny?" (7.5)

---

## P4. Refaktoryzacja formatera pod ochroną testów i test różnicowy

**Sekcja teorii:** 8. Refaktoryzacja pod ochroną testów (8.1 rozdzielenie odpowiedzialności, 8.2 porównanie implementacji, 8.3 sekwencja transformacji, 8.4 jawna zmiana zachowania). Nawiązanie do 2.3 (pętla małych kroków) i 2.5 (szum w różnicy).

**Co ilustruje:** wynik serii małych transformacji (after) oraz test różnicowy porównujący starą i nową implementację.

**Pliki:**

- before: `src/main/java/pl/training/module2/LegacyInvoiceFormatter.java`
- after: `src/main/java/pl/training/module2/InvoiceFormatter.java` - metody `displayedCustomer`, `calculateSubtotal`, `appendLines`, `lineTotal`, `money` oraz stała `TAX_RATE`.
- test różnicowy: `src/test/java/pl/training/module2/FormatterEquivalenceTest.java`.

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

**Testy i co sprawdzają:**

- `FormatterEquivalenceTest.refactoringPreservesObservedOutput` (parametryzowany) - dla trzech zestawów danych porównuje `legacy.format(...)` z `refactored.format(...)`:
  - `"Acme"` i pusta lista,
  - `null` i jedna pozycja BOOK 1 x 10.00,
  - `"vip"` i pozycje A 3 x 0.10 oraz B 2 x 19.995 (cena wymagająca zaokrąglenia).
- `LegacyInvoiceFormatterCharacterizationTest` - stałe, przejrzane oczekiwania (dotyczą klasy legacy).

**Na co zwrócić uwagę:**

- Zmiana semantyki ukryta w strukturze: w wersji after `lineTotal` jest wywoływane dwa razy (raz w sumie, raz przy wypisywaniu). Dla czystego obliczenia to bezpieczne; gdyby obliczenie miało efekt uboczny lub było kosztowne, byłaby to zmiana zachowania lub wydajności.
- Kolejność: after liczy sumy przed budową tekstu, legacy w trakcie pętli. Dla czystych obliczeń bez znaczenia (2.x: wewnętrzna kolejność czystych obliczeń zwykle nie jest kontraktem).
- `money(subtotal.add(tax))` - suma całkowita dalej jest liczona z niezaokrąglonego subtotalu i zaokrąglonego podatku, tak jak w legacy. Pokusa "uproszczenia" do sumy zaokrąglonych wartości to zmiana zachowania.
- W repozytorium `LegacyInvoiceFormatterCharacterizationTest` testuje tylko klasę legacy. Dla `InvoiceFormatter` stałych oczekiwań brak - ochrona pochodzi z testu różnicowego. Warto o to zapytać grupę: co się stanie po usunięciu klasy legacy? (test różnicowy znika, trzeba przenieść stałe oczekiwania na nową klasę, 8.2).
- Test różnicowy nie wykryje historycznego defektu obecnego w obu wersjach i potwierdza zgodność tylko dla wykonanych danych.
- Stała stawka, brak waluty i walidacji to świadome uproszczenia (8.1). Refaktoryzacja nie upoważnia do ich dodawania.

**Sugerowany przebieg demonstracji (ok. 15 min):**

1. Otwórz obok siebie `LegacyInvoiceFormatter` i `InvoiceFormatter`.
2. Odtwórz na żywo 2-3 pierwsze kroki z 8.3 na kopii klasy legacy (Extract Method w IDE: `displayedCustomer`, `lineTotal`, `money`), po każdym uruchamiając `mvn test -Dtest=LegacyInvoiceFormatterCharacterizationTest` (lub test na kopii) i pokazując `git diff`.
3. Pokaż `FormatterEquivalenceTest` i uruchom `mvn test -Dtest=FormatterEquivalenceTest`.
4. Kontrolna mutacja w `InvoiceFormatter.calculateSubtotal`: sumuj `money(lineTotal(line))` zamiast `lineTotal(line)`. Zapytaj grupę przed uruchomieniem, czy test wykryje zmianę. Dla danych z testu (A 3 x 0.10 = 0.30, B 2 x 19.995 = 39.990) wartości zaokrąglone i dokładne sumują się tak samo, więc test pozostanie zielony. To dobry moment, by pokazać ograniczenie testu różnicowego i potrzebę przypadku z Ćwiczenia 2 (np. dwie pozycje po 0.005). Przywróć kod.
5. Pytania: "Który krok w 8.3 jest najbardziej ryzykowny?" "Co zrobimy, jeśli właściciel produktu każe rzucać wyjątek dla braku klienta?" (8.4: tryb funkcjonalny, osobny test, świadoma zmiana oczekiwania).

---

## P5. Punkt wyjścia do rozrywania zależności (LegacyReminderService)

**Sekcja teorii:** 9.1 seam i punkt aktywacji, 9.2 dwa powody rozrywania zależności, 9.3 punkt wyjścia. Nawiązanie do 3.4 (punkt zmiany i punkt testowania).

**Co ilustruje:** kod, którego nie da się deterministycznie przetestować: globalny czas `LocalDate.now(ZoneOffset.UTC)` i efekt przez `System.out.printf`.

**Pliki:**

- `src/main/java/pl/training/module2/LegacyReminderService.java`
- `src/main/java/pl/training/module2/Subscription.java` - rekord `Subscription(String email, LocalDate renewalDate)`.

**Testy:** celowo brak. To jest sedno przykładu.

**Na co zwrócić uwagę:**

- Warunek ma tylko górną granicę `today + 7`: wysyłka dla dokładnie +7 dni, dla dziś i dla dat przeszłych; brak wysyłki dla +8. Brak dolnej granicy może być defektem, ale jego korekta nie należy do refaktoryzacji.
- Wywołanie statyczne `LocalDate.now(...)` nie daje szwu obiektowego (9.1).
- Czas to problem determinizmu (pośrednie wejście), `System.out` to problem obserwacji (9.2).
- Testowanie przez zmianę zegara systemowego albo przechwytywanie `System.out` zwiększa sprzężenie testu z procesem uruchomieniowym.

**Sugerowany przebieg demonstracji (ok. 5 min):**

1. Pokaż klasę i zadaj pytania z 3.4: gdzie zmienimy kod, skąd uruchomimy zachowanie, gdzie widać wynik, które zależności przeszkadzają, jaki najmniejszy test wykryje zmianę?
2. Poproś grupę o podanie trzech przypadków granicznych (+7, +8, przeszłość) i ich zastanego wyniku.
3. Zapytaj: "Jak dziś napisać test dla +7 dni, który przejdzie jutro?"

---

## P6. Minimalny szew przejściowy (SeamedReminderService)

**Sekcja teorii:** 9.4 minimalny szew przejściowy (Subclass and Override Method).

**Co ilustruje:** owinięcie trudnych wywołań metodami `protected` i podmiana ich w podklasie testowej. Logika produkcyjna wykonuje te same operacje.

**Pliki:**

- before: `src/main/java/pl/training/module2/LegacyReminderService.java`
- after (etap przejściowy): `src/main/java/pl/training/module2/SeamedReminderService.java` - klasa nie jest `final`, metody `protected LocalDate currentDate()` i `protected void sendMessage(String email, LocalDate renewalDate)`.
- test: `src/test/java/pl/training/module2/SeamedReminderServiceTest.java` z wewnętrzną podklasą `TestableReminderService`.

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

**Testy i co sprawdzają (dziś = 2026-08-30):**

| Test | Data odnowienia | Oczekiwanie |
| --- | --- | --- |
| `sendsReminderForRenewalExactlySevenDaysAway` | 2026-09-06 (+7) | `true`, jedna wiadomość |
| `doesNotSendReminderMoreThanSevenDaysBeforeRenewal` | 2026-09-07 (+8) | `false`, brak wiadomości |
| `documentsCurrentBehaviorForPastRenewalDate` | 2026-08-29 (przeszłość) | `true`, jedna wiadomość (zastane zachowanie, nazwa z `documents`) |

**Na co zwrócić uwagę:**

- W projekcie etap ma osobną nazwę klasy, żeby wersje mogły współistnieć. W realnej pracy byłaby to mała zmiana istniejącej klasy.
- Punkt aktywacji: wyrażenie `new TestableReminderService(...)`. Mechanizm: dynamiczne wiązanie metod.
- Zdjęcie `final` i dodanie metod `protected` zmienia kontrakt rozszerzalności i powierzchnię API. Refaktoryzacją jest tylko wtedy, gdy typ jest wewnętrzny i wszyscy konsumenci są pod kontrolą.
- Test jest sprzężony z metodami chronionymi; hierarchia nie ma znaczenia domenowego. To rozwiązanie przejściowe i powinno być tak oznaczone.
- `currentDate()` dalej używa `ZoneOffset.UTC` w produkcji - strefa jest częścią zachowania.

**Sugerowany przebieg demonstracji (ok. 10 min):**

1. Pokaż diff między `LegacyReminderService` a `SeamedReminderService` (np. porównanie plików w IDE). Zwróć uwagę, że logika warunku jest identyczna.
2. Pokaż `SeamedReminderServiceTest` i podklasę testową.
3. Uruchom `mvn test -Dtest=SeamedReminderServiceTest`.
4. Kontrolna mutacja: zmień `isAfter` na `!isBefore` albo `plusDays(7)` na `plusDays(6)` i pokaż, który test pada. Przywróć.
5. Pytania: "Gdzie jest punkt aktywacji?" "Jaki koszt ponieśliśmy, dodając `protected`?" "Czy to może zostać na stałe?"

---

## P7. Jawne zależności jako rozwiązanie docelowe (ReminderService)

**Sekcja teorii:** 9.5 jawne zależności, 9.6 bezpieczna kolejność rozrywania zależności, 9.7 czego unikać.

**Co ilustruje:** zastąpienie dziedziczenia przekazaniem zależności przez konstruktor (Parameterize Constructor) i nazwanie efektu wyjściowego interfejsem (Extract Interface).

**Pliki:**

- before: `src/main/java/pl/training/module2/SeamedReminderService.java` (etap przejściowy)
- after: `src/main/java/pl/training/module2/ReminderService.java` - `final`, konstruktor `ReminderService(Clock clock, ReminderGateway reminderGateway)` z `Objects.requireNonNull`, zagnieżdżony interfejs `ReminderGateway { void send(String email, LocalDate renewalDate); }`.
- test: `src/test/java/pl/training/module2/ReminderServiceTest.java` z `RecordingReminderGateway` (spy) i `Clock.fixed(Instant.parse("2026-08-30T10:00:00Z"), ZoneOffset.UTC)`.

**Testy i co sprawdzają:** te same trzy przypadki co w P6: `usesInjectedClockAndGateway` (+7, wysyłka), `doesNotSendReminderMoreThanSevenDaysBeforeRenewal` (+8, brak), `preservesCurrentBehaviorForPastRenewalDate` (przeszłość, wysyłka). Zmiana nazwy z `documents...` na `preserves...` sygnalizuje, że zachowanie jest świadomie utrzymane.

**Na co zwrócić uwagę:**

- Punkt aktywacji obu szwów: wyrażenie `new ReminderService(...)`; argumenty wybierają zachowanie.
- Strefa czasowa przeszła z kodu (`ZoneOffset.UTC`) do obiektu `Clock`. W produkcji trzeba przekazać zegar z tą samą strefą (np. `Clock.systemUTC()`), inaczej zmieni się zachowanie w okolicach północy. To typowa pułapka przy tej transformacji.
- `Clock.fixed` ustawia 10:00 UTC - daleko od granicy doby, więc test nie jest wrażliwy na strefę. Warto zapytać, jaki test wykryłby błędną strefę.
- Reguła biznesowa nie zna technologii wysyłki. Rzeczywisty adapter `ReminderGateway` wymaga osobnego, wąskiego testu integracyjnego (serializacja, uwierzytelnienie, odpowiedzi, ponowienia).
- `Module2Examples.runExplicitSeamExample` pokazuje "produkcyjne" złożenie: `Clock.fixed` i lambda drukująca na konsolę jako adapter.
- Po przeniesieniu wywołań i testów `SeamedReminderService` można usunąć osobnym, małym krokiem (9.6 punkt 8).

**Sugerowany przebieg demonstracji (ok. 10 min):**

1. Pokaż `SeamedReminderService` i `ReminderService` obok siebie.
2. Pokaż `ReminderServiceTest` i porównaj z `SeamedReminderServiceTest` - test nie dziedziczy już z klasy produkcyjnej.
3. Uruchom `mvn test -Dtest='ReminderServiceTest,SeamedReminderServiceTest'`.
4. Przejdź przez 9.6 (osiem kroków) i dla każdego wskaż, który plik go realizuje.
5. Pytania: "Gdzie jest punkt aktywacji?" "Czego ten test nie sprawdzi?" "Czy każda klasa powinna dostać interfejs?" (9.7: nie).

---

## P8. Program demonstracyjny (Module2Examples)

**Sekcja teorii:** przekrojowo - 8 (równość wyników formatera), 5 (obiekty zastępcze jako lambdy), 9.5 (jawne zależności).

**Pliki:**

- `src/main/java/pl/training/module2/Module2Examples.java` - trzy metody: `runBehaviorPreservingRefactoring`, `runTestDoubleExample`, `runExplicitSeamExample`.
- `src/test/java/pl/training/module2/Module2ExamplesTest.java` - `runsAllModuleExamples` sprawdza jedynie, że `main` nie rzuca wyjątku.

**Na co zwrócić uwagę:**

- `Module2ExamplesTest` to przykład testu o słabej asercji (tylko brak wyjątku). Podnosi pokrycie wielu klas, ale nie chroni żadnej reguły - świetna ilustracja do 6.4 (nadużycia pokrycia). Warto pokazać to w trakcie omawiania raportu JaCoCo.
- `runBehaviorPreservingRefactoring` drukuje tylko `true/false` - to nie jest dowód, tylko pojedyncza obserwacja dla jednego zestawu danych.

**Sugerowany przebieg:** uruchom na początku modułu jako "mapę" tematów, a pod koniec wróć do `Module2ExamplesTest` przy dyskusji o pokryciu.

---

## Proponowana kolejność pokazywania w czasie zajęć

1. Sekcje 1-4 (teoria, Aktywność 1.6, Ćwiczenie 1, Aktywność 4.6) - bez kodu, ewentualnie `Module2Examples` jako zapowiedź.
2. P1 (obiekty zastępcze) - przed Ćwiczeniem 3A.
3. P2 (pokrycie) - Aktywność 6.5 i Ćwiczenie 3B.
4. P3 (charakterystyka) - przed Ćwiczeniem 2.
5. P4 (refaktoryzacja i test różnicowy) - po Ćwiczeniu 2, jako omówienie.
6. P5 (legacy reminder) - przed Ćwiczeniem 4.
7. P6 i P7 - po Ćwiczeniu 4, jako omówienie.
