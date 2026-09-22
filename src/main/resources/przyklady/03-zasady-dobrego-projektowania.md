# Moduł 3. Zasady dobrego projektowania - przewodnik prowadzącego po przykładach

Cały moduł opiera się na jednym studium przypadku: **wycenie dostawy**. Zastany serwis `LegacyDeliveryQuoteService` (cena dla dwóch metod dostawy + zapis we własnej liście + wypisanie na konsolę) zostaje rozłożony na domenę, przypadek użycia z portami, adaptery i ręczny composition root. Kod referencyjny w pakiecie `pl.training.module3` zawiera jednocześnie wersję wyjściową (`legacy`) i docelową (`domain`, `application`, `adapter`), a program `Module3Examples` uruchamia obie i porównuje ceny.

## 1. Uruchamianie

### Java

```shell
mvn -q compile && java -cp target/classes pl.training.module3.Module3Examples
mvn test -Dtest='pl.training.module3.**'
```

Alternatywnie z IDE: klasa `Module3Examples` (metoda `main`) oraz katalog `src/test/java/pl/training/module3`. Teoria zaleca przed ćwiczeniami `mvn clean verify`.

Oczekiwane wyjście programu:

```text
Quote ready for developer@example.com: STANDARD costs 17.28
Quote ready for developer@example.com: STANDARD costs 17.28
Legacy and refactored prices equal: true
Stored quotes: 1
```

Pierwsza linia pochodzi z wersji zastanej, druga z adaptera `ConsoleQuoteNotifier` nowej wersji.

### C#

```shell
cd csharp && dotnet run --project src/Training.Module3
dotnet test
```

Kod: `csharp/src/Training.Module3` (katalogi `Legacy`, `Domain`, `Application`, `Adapter`, plik `Module3Examples.cs`), testy: `csharp/tests/Training.Module3.Tests`. Nazwy klas i testów odpowiadają wersji Java.

### TypeScript

```shell
cd typescript && npm ci && npm run build && npm run module3
npm test
```

Kod: `typescript/src/module3` (`legacy`, `domain`, `application`, `adapter`, `Module3Examples.ts`, `main.ts`), testy: `typescript/test/module3` (np. `domain/DeliveryPricePolicyContract.test.ts`).

## 2. Mapa przykładów

| Nr | Przykład | Sekcja teorii | Pliki (src/main/java/pl/training/module3/) | Testy (src/test/java/pl/training/module3/) |
| --- | --- | --- | --- | --- |
| P1 | Wersja zastana i charakterystyka | 7.1, 1.4, 4.6 | `legacy/LegacyDeliveryQuoteService.java` | `legacy/LegacyDeliveryQuoteServiceCharacterizationTest.java` |
| P2 | Model i inwarianty domeny | 7.2 | `domain/Parcel.java`, `domain/ShippingMethod.java`, `domain/DeliveryQuote.java` | pośrednio wszystkie testy (brak dedykowanego) |
| P3 | DRY: reguła opłaty paliwowej | 2.1-2.3, 7.3 | `domain/FuelSurcharge.java` | pośrednio `domain/DeliveryPriceCalculatorTest.java`, `domain/DeliveryPricePolicyContractTest.java` |
| P4 | Strategy dla wariantów ceny, OCP | 3.3, 6.3, 7.4 | `domain/DeliveryPricePolicy.java`, `domain/StandardDeliveryPricePolicy.java`, `domain/ExpressDeliveryPricePolicy.java` | `domain/DeliveryPricePolicyContractTest.java` |
| P5 | Wybór polityki i kontrola konfiguracji | 7.5, 3.6 | `domain/DeliveryPriceCalculator.java` | `domain/DeliveryPriceCalculatorTest.java` |
| P6 | Test kontraktowy i LSP | 3.4, 7.6 | `domain/DeliveryPricePolicy.java` | `domain/DeliveryPricePolicyContractTest.java` |
| P7 | Porty z perspektywy klienta (ISP, DIP) | 3.5, 3.6, 5.4, 7.7 | `application/QuoteRepository.java`, `application/QuoteNotifier.java` | `application/CreateDeliveryQuoteTest.java` |
| P8 | Przypadek użycia jako jawna orkiestracja, SRP | 3.2, 4.1, 7.8 | `application/CreateDeliveryQuote.java` | `application/CreateDeliveryQuoteTest.java` |
| P9 | Adaptery na zewnątrz granicy | 5.2, 6.4, 7.9 | `adapter/InMemoryQuoteRepository.java`, `adapter/ConsoleQuoteNotifier.java` | `adapter/InMemoryQuoteRepositoryTest.java` |
| P10 | Composition root i kierunek zależności | 5.6, 7.10, 7.11, 7.12 | `Module3Examples.java` | `Module3ExamplesTest.java` |

## 3. Przykłady szczegółowo

### P1. Wersja zastana i test charakterystyki

- **Sekcja teorii:** 7.1 (punkt wyjścia), wspierająco 1.4 (dowód wartości zmiany) i 4.6 (diagnostyka).
- **Co ilustruje:** klasę, w której jeden przebieg łączy trzy niezależne decyzje: cennik (dział finansowy), zapis (właściciel danych) i komunikat (komunikacja z klientem). Sam `switch` nie jest głównym problemem.
- **Pliki:** `src/main/java/pl/training/module3/legacy/LegacyDeliveryQuoteService.java`.
- **Na co zwrócić uwagę:**
  - stała `0.08` występuje w obu gałęziach `switch` (ta sama reguła, dwie reprezentacje),
  - zapis to prywatna `List` serwisu, powiadomienie to bezpośredni `System.out.printf`,
  - kolejność: obliczenie, zapis, powiadomienie, zwrot jest ukryta w implementacji; to protokół efektów ubocznych, który wymaga jawnej decyzji,
  - brak punktu podmiany: nie da się wiarygodnie zasymulować awarii zapisu.
- **Testy:** `LegacyDeliveryQuoteServiceCharacterizationTest`
  - `documentsStandardDeliveryPriceAndStorage` - 3 kg STANDARD daje `17.28`, a oferta trafia do `storedQuotes()`,
  - `documentsExpressDeliveryPrice` - 3 kg EXPRESS daje `31.32`.
  - Jawna luka: test nie przechwytuje treści konsoli. Nie jest to dowód, że komunikat nie jest częścią kontraktu.
- **Przebieg demonstracji:**
  1. Otwórz `LegacyDeliveryQuoteService` i poproś grupę o wypisanie „kto może zażądać zmiany w tej klasie”.
  2. Pokaż tabelę obserwacji z sekcji 7.1 dopiero po odpowiedziach grupy.
  3. Otwórz test charakterystyki, uruchom go. Przelicz na tablicy: (10 + 3 x 2) x 1.08 = 17.28, (20 + 3 x 3) x 1.08 = 31.32.
  4. Pytania: „Czego ten test nie chroni?”, „Czy treść komunikatu jest kontraktem? Kto to rozstrzyga?”, „Jaki scenariusz zmiany uzasadnia refaktoryzację?” (odwołanie do 1.4).

### P2. Model i inwarianty domeny

- **Sekcja teorii:** 7.2.
- **Co ilustruje:** rekordy utrzymujące podstawowe inwarianty w konstruktorze kanonicznym.
- **Pliki:** `domain/Parcel.java` (masa niepusta i dodatnia), `domain/ShippingMethod.java` (enum `STANDARD`, `EXPRESS`), `domain/DeliveryQuote.java`.
- **Kluczowy fragment:**

  ```java
  if (price.signum() < 0) {
      throw new IllegalArgumentException("Price must not be negative");
  }
  price = price.setScale(2, RoundingMode.UNNECESSARY);
  ```

- **Na co zwrócić uwagę:**
  - `RoundingMode.UNNECESSARY` nie zaokrągla, tylko rzuca `ArithmeticException`, gdy cena ma więcej niż dwa znaczące miejsca dziesiętne. Zaokrąglenie jest odpowiedzialnością polityki ceny, a nie wyniku.
  - walidacja e-maila sprawdza tylko niepustość (świadomie poza zakresem),
  - `BigDecimal` tworzony z tekstu, nie z `double`,
  - `record` ogranicza kod techniczny, ale nie tworzy modelu; znaczenie wynika z nazw i reguł.
  - Wersja zastana także korzysta z tych typów, więc inwarianty obowiązują obie wersje.
- **Testy:** brak dedykowanego testu rekordów; inwarianty są wykonywane pośrednio we wszystkich testach tworzących `Parcel` i `DeliveryQuote`. Warto to powiedzieć wprost jako potencjalną lukę.
- **Przebieg demonstracji:** pokaż trzy pliki, zapytaj: „Co się stanie dla `new BigDecimal("17.285")` w `DeliveryQuote`?” (wyjątek, nie zaokrąglenie), „Dlaczego nie `double`?”.

### P3. DRY: reguła opłaty paliwowej

- **Sekcja teorii:** 2.1-2.3 (DRY dotyczy wiedzy, podobieństwo nie wystarcza), 7.3.
- **Co ilustruje:** centralizację jednej potwierdzonej reguły (stawka + sposób doliczenia + zaokrąglenie `HALF_UP`) i świadome **niewydzielanie** wspólnej formuły ceny bazowej.
- **Pliki:** `domain/FuelSurcharge.java`.
- **Kluczowy fragment:**

  ```java
  if (rate.signum() < 0 || rate.compareTo(BigDecimal.ONE) > 0) {
      throw new IllegalArgumentException(
              "Fuel surcharge rate must be between zero and one");
  }
  ...
  return baseAmount.add(baseAmount.multiply(rate)).setScale(2, RoundingMode.HALF_UP);
  ```

- **Na co zwrócić uwagę:**
  - brak metody `basePrice(base, rate, weight)`: formuły STANDARD i EXPRESS są podobne tekstowo, ale to odrębne cenniki, które mogą zmieniać się niezależnie,
  - inwariant `0..1` jest decyzją modelową (0-100 procent), nie wynika z typu; jeśli biznes dopuści więcej niż 100 procent lub korektę ujemną, trzeba go zmienić,
  - brak interfejsu `Surcharge` (YAGNI, jedna reguła, brak granicy),
  - pułapka: w testach nie należy liczyć oczekiwanej ceny przez `FuelSurcharge` (2.3, niezależność wyroczni). Testy w projekcie mają wartości wpisane jawnie (`17.28`, `31.32`).
- **Testy:** brak dedykowanego testu `FuelSurcharge`; zachowanie chronią pośrednio `DeliveryPriceCalculatorTest` (konkretne ceny) i `DeliveryPricePolicyContractTest` (skala 2, nieujemność). Walidacja zakresu stawki i ujemnej kwoty bazowej nie ma testu; można to zaproponować grupie jako zadanie dodatkowe.
- **Przebieg demonstracji:** pokaż obok siebie dwie gałęzie `switch` z P1 i `FuelSurcharge`. Pytanie: „Dlaczego scalono `0.08`, a nie scalono `10.00 + weight * 2.00` z `20.00 + weight * 3.00`?”. Następnie tabela z 2.2 (limity 1000 kg, rabat 10 procent).

### P4. Strategy dla istniejących wariantów, zakres OCP

- **Sekcja teorii:** 3.3 (OCP dla wybranej osi), 6.2-6.3 (refaktoryzacja w kierunku Strategy), 7.4.
- **Co ilustruje:** dwa istniejące algorytmy za wspólnym kontraktem; każda polityka ma własne stałe `BASE_PRICE`, `PRICE_PER_KG` i współdzieli `FuelSurcharge` przez konstruktor.
- **Pliki:** `domain/DeliveryPricePolicy.java`, `domain/StandardDeliveryPricePolicy.java`, `domain/ExpressDeliveryPricePolicy.java`.
- **Na co zwrócić uwagę:**
  - kontrakt jest zapisany w Javadoc (stabilna niepusta metoda; wynik deterministyczny, nieujemny, skala 2, bez efektów ubocznych); typy tego nie wyrażają,
  - brak pluginów, refleksji, konfiguracji wyrażeń (YAGNI),
  - `ShippingMethod` pozostaje enumem: dodanie metody wymaga zmiany enuma i composition root. Zamknięta na zmianę jest logika wyboru w kalkulatorze, nie cały system. To świadomy, ograniczony zakres OCP,
  - „nie każdy `switch` łamie OCP” (3.3, 3.8): Strategy jest tu uzasadniona istniejącymi wariantami, a nie samym faktem istnienia `switch`.
- **Testy:** `DeliveryPricePolicyContractTest` (wspólny kontrakt), `DeliveryPriceCalculatorTest.delegatesToPolicySelectedByShippingMethod` (konkretne ceny obu polityk).
- **Przebieg demonstracji:** najpierw interfejs, potem `StandardDeliveryPricePolicy`, na końcu `ExpressDeliveryPricePolicy`. Opowiedz sekwencję z 6.2: zabezpiecz zachowanie, przenieś jeden wariant, uruchom testy, przenieś drugi, zatrzymaj się. Pytanie: „Co trzeba zmienić, żeby dodać SAME_DAY? Ile plików?”.

### P5. Wybór polityki i kontrola konfiguracji

- **Sekcja teorii:** 7.5, 3.6 (DIP nie wymaga interfejsu przed każdą klasą).
- **Co ilustruje:** kalkulator indeksuje strategie raz w konstruktorze (`EnumMap`), odrzuca konfigurację pustą, `null` i zduplikowaną, a następnie tworzy niemodyfikowalną kopię (`Map.copyOf`).
- **Pliki:** `domain/DeliveryPriceCalculator.java`.
- **Kluczowy fragment:**

  ```java
  DeliveryPricePolicy previous = indexedPolicies.putIfAbsent(method, policy);
  if (previous != null) {
      throw new IllegalArgumentException("Duplicate policy for method: " + method);
  }
  ...
  this.policies = Map.copyOf(indexedPolicies);
  ```

- **Na co zwrócić uwagę:**
  - brak polityki dla metody jest wykrywany dopiero przy żądaniu (`priceFor`), nie w konstruktorze; alternatywą jest wymaganie kompletu wartości enuma. Wybór zależy od tego, czy częściowa konfiguracja jest legalna,
  - `Map.copyOf` chroni przed późniejszą zmianą kolekcji wejściowej,
  - `CreateDeliveryQuote` zależy od konkretnej klasy kalkulatora, bez interfejsu: to ta sama wewnętrzna polityka i nie ma drugiej sensownej implementacji.
- **Testy:** `DeliveryPriceCalculatorTest`
  - `delegatesToPolicySelectedByShippingMethod` - parametryzowany: STANDARD `17.28`, EXPRESS `31.32` dla 3 kg,
  - `rejectsDuplicatePolicyForOneShippingMethod` - dwie polityki STANDARD dają `IllegalArgumentException`,
  - `reportsMissingPolicy` - kalkulator tylko ze STANDARD rzuca wyjątek dla EXPRESS.
- **Przebieg demonstracji:** uruchom `DeliveryPriceCalculatorTest`, potem zapytaj: „Czy częściowa konfiguracja powinna być błędem startu czy błędem żądania?”, „Dlaczego nie ma `IDeliveryPriceCalculator`?”.

### P6. Test kontraktowy i LSP

- **Sekcja teorii:** 3.4, 7.6.
- **Co ilustruje:** jeden parametryzowany test uruchamiany dla wszystkich implementacji `DeliveryPricePolicy`, sprawdzający elementy kontraktu z Javadoc.
- **Pliki:** `src/test/java/pl/training/module3/domain/DeliveryPricePolicyContractTest.java`.
- **Kluczowy fragment:**

  ```java
  assertAll(
          () -> assertEquals(expectedMethod, firstMethod),
          () -> assertEquals(firstMethod, secondMethod),
          () -> assertTrue(firstResult.signum() >= 0),
          () -> assertEquals(2, firstResult.scale()),
          () -> assertEquals(firstResult, secondResult));
  ```

- **Na co zwrócić uwagę:**
  - test dostarcza dowodów, nie formalnego dowodu LSP: jedna masa (100 kg), brak sprawdzenia maksymalnej masy, każdego ułamka zaokrąglenia, wydajności, niezmienności wejścia,
  - lista implementacji w `policies()` musi być ręcznie utrzymywana (koszt w tabeli 7.12); nowa polityka niedopisana do listy nie jest sprawdzana,
  - test kontraktowy nie zastępuje testów konkretnych cen,
  - podtyp nie może wzmacniać warunków wstępnych ani osłabiać końcowych; `UnsupportedOperationException` nie jest automatycznie naruszeniem LSP, zależy od kontraktu.
- **Testy:** `everyPolicyObeysTheSubstitutionContract` (warianty „standard policy”, „express policy”).
- **Przebieg demonstracji:** pokaż Javadoc interfejsu i test obok siebie; poproś grupę o wskazanie, które zdania Javadoc są sprawdzane, a które nie (brak efektów ubocznych, niezmienność `Parcel`). Na żywo: w kopii roboczej zmień w jednej polityce `setScale(2, ...)` na `setScale(3, ...)` albo usuń zaokrąglenie i pokaż czerwony test kontraktowy; cofnij zmianę.

### P7. Porty definiowane przez potrzeby przypadku użycia

- **Sekcja teorii:** 3.5 (ISP), 3.6 (DIP), 5.4, 7.7.
- **Co ilustruje:** dwa wąskie porty wyjściowe należące do pakietu `application`, nazwane przez potrzebę wnętrza.
- **Pliki:** `application/QuoteRepository.java` (`void save(DeliveryQuote)`), `application/QuoteNotifier.java` (`void quoteCreated(DeliveryQuote)`), oba `@FunctionalInterface`.
- **Na co zwrócić uwagę:**
  - po jednej metodzie, bo są dwie odrębne role, a nie dlatego, że „ISP wymaga jednej metody”,
  - brak dziedziczenia `CrudRepository<DeliveryQuote>`: przypadek użycia nie zależy od usuwania, stronicowania, wyszukiwania,
  - port należy do strony formułującej potrzebę; adapter importuje port, nie odwrotnie,
  - `@FunctionalInterface` pozwala w testach użyć `savedQuotes::add` jako implementacji.
- **Testy:** `CreateDeliveryQuoteTest` (implementacje portów jako lambdy i referencje do metod).
- **Przebieg demonstracji:** pokaż oba porty, zapytaj: „Kto jest właścicielem tego interfejsu?”, „Czy dodalibyśmy tu `findAll()`? Dla kogo?”.

### P8. Przypadek użycia jako jawna orkiestracja

- **Sekcja teorii:** 3.2 (SRP), 4.1 (spójność), 7.8.
- **Co ilustruje:** `CreateDeliveryQuote` koordynuje jeden cel: oblicza cenę, tworzy `DeliveryQuote`, zapisuje, powiadamia i zwraca. Nie zna wzoru ceny, mechanizmu zapisu ani formatu komunikatu. Wejście to rekord `Command`, zależności przez konstruktor (ręczne wstrzyknięcie).
- **Pliki:** `application/CreateDeliveryQuote.java`.
- **Kluczowy fragment:**

  ```java
  repository.save(quote);
  notifier.quoteCreated(quote);
  return quote;
  ```

- **Na co zwrócić uwagę:**
  - kolejność `save`, potem `quoteCreated` to decyzja protokołu wynikająca z wymagania biznesowego, nie z SOLID,
  - wyjątek przy zapisie przerywa przebieg, powiadomienie nie zostaje wysłane,
  - ustawienie kolejności nie daje atomowości: jeśli zapis się uda, a powiadomienie zawiedzie, potrzebne mogą być ponowienia, idempotencja lub transactional outbox (świadomie nieimplementowane),
  - zmiana semantyki względem wersji zastanej: w legacy nie było możliwości awarii zapisu (lista w pamięci), więc zachowanie przy błędzie jest **nową, jawnie uzgodnioną** decyzją, a nie przeniesionym zachowaniem.
- **Testy:** `CreateDeliveryQuoteTest`
  - `calculatesStoresAndNotifiesAboutQuote` - cena `17.28`, dokładnie jeden zapis i jedno powiadomienie z tym samym obiektem,
  - `doesNotNotifyWhenSavingFails` - repozytorium rzuca `IllegalStateException`, wyjątek propaguje się, lista powiadomień pozostaje pusta.
  - Lambdy nie udają bazy ani brokera; realne adaptery wymagają testów integracyjnych.
- **Przebieg demonstracji:** pokaż klasę, potem test awarii. Pytania: „Skąd wiemy, że powiadomienie po błędzie zapisu jest niedozwolone?”, „Co jeśli powiadomienie rzuci wyjątek po udanym zapisie?”, „Czy ta klasa ma jednego aktora zmiany?”.

### P9. Adaptery na zewnętrznej stronie granicy

- **Sekcja teorii:** 5.2 (kierunek zależności a przepływ sterowania), 6.4 (Adapter), 7.9.
- **Co ilustruje:** konkretne efekty wyniesione poza przypadek użycia; adaptery importują porty aplikacji, aplikacja nie importuje adapterów.
- **Pliki:** `adapter/InMemoryQuoteRepository.java` (lista w pamięci, `quotes()` zwraca `List.copyOf`), `adapter/ConsoleQuoteNotifier.java` (ten sam format komunikatu co w legacy: `"Quote ready for %s: %s costs %s%n"`).
- **Na co zwrócić uwagę:**
  - `InMemoryQuoteRepository` to działający adapter demonstracyjny, nie substytut testu trwałości: brak bezpieczeństwa wątkowego, transakcji, przetrwania restartu,
  - adapter JDBC lub JPA musiałby mapować model, błędy i transakcje,
  - format komunikatu w `ConsoleQuoteNotifier` został przeniesiony bez zmian, więc wyjście programu jest takie samo jak w legacy,
  - pusty adapter „jeden do jednego” bez ochrony żadnej decyzji nie ma wartości (6.4).
- **Testy:** `InMemoryQuoteRepositoryTest.storesQuote` - zapisana oferta jest zwracana przez `quotes()`. `ConsoleQuoteNotifier` nie ma testu (treść konsoli nie jest przechwytywana); to motywacja zadania B w ćwiczeniu 3.
- **Przebieg demonstracji:** narysuj dwie strzałki: przepływ sterowania `CreateDeliveryQuote -> QuoteNotifier -> ConsoleQuoteNotifier` oraz zależność źródłową `ConsoleQuoteNotifier -> QuoteNotifier`. Wykonaj na żywo `grep -r "import pl.training.module3.adapter" src/main/java/pl/training/module3/application src/main/java/pl/training/module3/domain` (brak wyników). Pytanie: „Czy wywołanie adaptera z przypadku użycia łamie regułę zależności?”.

### P10. Composition root, kierunek zależności, bilans zasad

- **Sekcja teorii:** 5.6, 7.10, 7.11, 7.12.
- **Co ilustruje:** `Module3Examples.main` ręcznie składa graf: `FuelSurcharge(0.08)`, dwie polityki, `DeliveryPriceCalculator`, `InMemoryQuoteRepository`, `ConsoleQuoteNotifier`, `CreateDeliveryQuote`; uruchamia też wersję zastaną i porównuje ceny.
- **Pliki:** `src/main/java/pl/training/module3/Module3Examples.java`.
- **Na co zwrócić uwagę:**
  - composition root zna wszystkie konkrety i nie zawiera reguł biznesowych,
  - ręczne konstruktory to pełnoprawny composition root; kontener DI nie tworzy poprawnego kierunku zależności samym faktem użycia (KISS, YAGNI),
  - gałąź `legacy` istnieje tylko dla demonstracji porównania, nie jest częścią architektury docelowej,
  - reguła zależności działa tylko na poziomie pakietów i importów: wszystko jest w jednym module Maven i procesie; niezależna kompilacja wymagałaby silniejszej granicy,
  - refaktoryzacja zwiększyła liczbę klas; wartością jest rozdzielenie niezależnych decyzji, nie mniejsza liczba linii. Dla programu bez drugiego wariantu i prawdziwego adaptera taki podział mógłby być nadmierny.
- **Testy:** `Module3ExamplesTest.runsAllModuleExamples` - tylko `assertDoesNotThrow`; nie sprawdza treści wyjścia (warto to wskazać jako słabą wyrocznię).
- **Przebieg demonstracji:**
  1. Uruchom program (polecenie z sekcji 1), omów cztery linie wyjścia.
  2. Pokaż diagram z 7.11 i tabelę dozwolonych zależności.
  3. Przejdź przez tabelę 7.12 (decyzja, zasada, korzyść, koszt) i poproś grupę o wskazanie kosztu każdej decyzji, zanim go odsłonisz.
  4. Pytania: „Gdzie dodalibyśmy trzecią politykę?”, „Czy ten podział byłby uzasadniony, gdyby istniała tylko jedna metoda dostawy?”.

## 4. Sugerowana kolejność prowadzenia modułu

1. Teoria 1-2 (heurystyki, DRY/KISS/YAGNI), z P1 jako materiałem do diagnozy i P3 jako przykładem DRY. Następnie Ćwiczenie 1.
2. Teoria 3-4 (SOLID, spójność, sprzężenie). Pokaz P4, P5, P6, P7, P8 w tej kolejności (od domeny do przypadku użycia). Ćwiczenie 2 (uczestnicy powtarzają drogę na kopii legacy).
3. Teoria 5 (Clean Architecture). Pokaz P9 i P10. Ćwiczenie 3.
4. Teoria 6 (wzorce jako kierunek i jako decyzja odwracalna). Ćwiczenie 4.
5. Sprawdzenie wiedzy i listy kontrolne z sekcji 11 teorii.

## 5. Ogólne pułapki do podkreślenia

- Łatwość mockowania nie dowodzi zgodności z SOLID.
- Interfejs nie usuwa sprzężenia, tylko zamienia zależność od konkretu na zależność od kontraktu.
- Zachowanie przy awarii zapisu jest nową decyzją biznesową, a nie refaktoryzacją; musi mieć właściciela i test.
- Testy w projekcie celowo mają jawne wartości oczekiwane (`17.28`, `31.32`), nie wyliczone kodem produkcyjnym.
- Ścieżka `legacy` współdzieli typy domenowe (`Parcel`, `DeliveryQuote`) z nową wersją; zmiana ich inwariantów wpływa na obie.
