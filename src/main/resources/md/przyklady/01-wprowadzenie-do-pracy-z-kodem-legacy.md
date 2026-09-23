# Moduł 1. Wprowadzenie do pracy z kodem legacy - przewodnik po przykładach

Materiał dla prowadzącego. Opisuje kod projektu powiązany z modułem 1, sposób jego uruchomienia i sugerowany przebieg demonstracji.

## Uruchomienie przykładów i testów

Wszystkie polecenia uruchamiamy z katalogu głównego repozytorium.

**Java**

```bash
mvn -q compile && java -cp target/classes pl.training.module1.Module1Examples
mvn test -Dtest='pl.training.module1.**'
```

Testy można też uruchomić z IDE (pakiet `pl.training.module1` w `src/test/java`). Raport pokrycia JaCoCo (przydatny przy przykładzie `DiscountPolicy`) powstaje w fazie `verify`: `mvn verify`, a następnie `target/site/jacoco/index.html`.

**C#**

```bash
cd csharp && dotnet run --project src/Training.Module1
dotnet test
```

Kod: `csharp/src/Training.Module1/`, testy: `csharp/tests/Training.Module1.Tests/`.

**TypeScript**

```bash
cd typescript && npm ci && npm run build && npm run module1
npm test
```

Kod: `typescript/src/module1/`, testy: `typescript/test/module1/`.

**Oczekiwany wynik `Module1Examples` (Java):**

```text
Saved order 9aa026a4-fc39-4af8-a008-d9b831b0ba59 with total 236.39
Sent to customer@example.com: Order total: 236.39
Receipt: Receipt[orderId=9aa026a4-fc39-4af8-a008-d9b831b0ba59, total=236.39]
Risk level: 3
Discount: 15%
Invoice line total: 90.0000
Quote line total: 90.0000
```

## Charakter kodu w module 1

Moduł 1 jest diagnostyczny. Kod projektu to **materiał do analizy, a nie rozwiązania wzorcowe**. W pakiecie nie ma wersji before/after ani etapów stage0..N: żadna klasa nie jest refaktoryzowana w tym module. Refaktoryzacja `LegacyOrderService` i podobnych konstrukcji pojawia się w kolejnych modułach. Warto to powiedzieć grupie na początku, żeby nie szukała „poprawionej wersji”.

Wszystkie klasy są celowo małe: pozwalają pokazać konkretne pojęcie (symptom, złożoność, pokrycie, duplikacja) na kodzie, który mieści się na jednym ekranie.

## Tabela mapowania

| Przykład | Sekcja teorii | Pliki (Java) | Testy | Zadania powiązane |
| --- | --- | --- | --- | --- |
| `LegacyOrderService` | 5.3 Przykład w Javie, 5.4 Procedura analizy symptomu, 8.10 Ocena przykładu, 9.1 Ćwiczenie 1 | `src/main/java/pl/training/module1/LegacyOrderService.java` | `src/test/java/pl/training/module1/LegacyOrderServiceTest.java` | Ćwiczenie 1, Aktywność 1.4 (kontekst) |
| `RiskClassifier` | 6.2 Złożoność cyklomatyczna, 6.3 Złożoność poznawcza | `src/main/java/pl/training/module1/RiskClassifier.java` | `src/test/java/pl/training/module1/RiskClassifierTest.java` | Aktywność 1.1 (kontekst metryk) |
| `DiscountPolicy` | 6.4 Pokrycie testami (linie a gałęzie) | `src/main/java/pl/training/module1/DiscountPolicy.java` | `src/test/java/pl/training/module1/DiscountPolicyTest.java` | Aktywność 1.3 |
| `SalesCalculations` | 6.5 Duplikacje | `src/main/java/pl/training/module1/SalesCalculations.java` | `src/test/java/pl/training/module1/SalesCalculationsTest.java` | Ćwiczenie 2 (`PricingService`, reguła VIP) |
| `Module1Examples` | cały moduł (uruchomienie przykładów) | `src/main/java/pl/training/module1/Module1Examples.java` | `src/test/java/pl/training/module1/Module1ExamplesTest.java` | - |

Odpowiedniki w C# i TypeScript mają te same nazwy klas i plików (`LegacyOrderService.cs` / `LegacyOrderService.ts` itd.), a testy te same nazwy z sufiksem `Test.cs` / `.test.ts`.

---

## Przykład 1. `LegacyOrderService` - symptomy w jednej metodzie

**Sekcje teorii:** 5.3, 5.4, 8.10; materiał wejściowy Ćwiczenia 1 (9.1).

**Pliki:**

- `src/main/java/pl/training/module1/LegacyOrderService.java` - klasa i powiązane typy w jednym pliku: rekordy `Order`, `OrderLine`, `Receipt`, interfejsy `OrderRepository`, `MailGateway`.
- `src/test/java/pl/training/module1/LegacyOrderServiceTest.java`

**Co ilustruje:**

Jedna metoda `placeOrder` łączy walidację, wycenę pozycji z rabatami, koszt wysyłki, podatek, zaokrąglenie, zapis i wysłanie wiadomości. Widoczne symptomy z sekcji 5.2:

- długa metoda z wieloma odpowiedzialnościami (problemy z odpowiedzialnością),
- typ klienta i kraj jako `String` (wartości domenowe jako dowolne napisy),
- `boolean express` wybierający odrębne zachowanie (argument `boolean`),
- znaczące liczby bez nazw: `0.90`, `0.95`, `10`, `39.99`, `200.00`, `14.99`, `0.23`, `0.19`,
- wariant zachowania rozproszony po `if` (kraje, typ klienta, dostawa).

**Kluczowy fragment (wysyłka i podatek):**

```java
BigDecimal shipping;
if (express) {
    shipping = new BigDecimal("39.99");
} else if (subtotal.compareTo(new BigDecimal("200.00")) >= 0) {
    shipping = BigDecimal.ZERO;
} else {
    shipping = new BigDecimal("14.99");
}

BigDecimal tax;
if ("PL".equals(destinationCountry)) {
    tax = subtotal.multiply(new BigDecimal("0.23"));
} else if ("DE".equals(destinationCountry)) {
    tax = subtotal.multiply(new BigDecimal("0.19"));
} else {
    tax = BigDecimal.ZERO;
}
```

**Na co zwrócić uwagę uczestników:**

- **Fakt a hipoteza.** „Podatek dla innych krajów wynosi zero” to fakt z kodu. „Podatek jest liczony błędnie” to hipoteza, dopóki nie znamy reguły biznesowej (teoria 10.1).
- **Kolejność operacji.** Próg darmowej wysyłki (`>= 200.00`) porównywany jest z sumą **po** rabatach. Ekspres zawsze kosztuje 39.99, niezależnie od progu. Podatek liczony jest od `subtotal`, bez wysyłki. Każde z tych zachowań może być zamierzone albo nie; to pytania domenowe, nie błędy.
- **Składanie rabatów.** Rabat VIP (0.90) i ilościowy (0.95 od 10 sztuk) są mnożone, bez pośredniego zaokrąglania; zaokrąglenie `HALF_UP` do 2 miejsc następuje dopiero na końcu.
- **Efekty uboczne.** Zapis (`repository.save`) następuje przed wysłaniem wiadomości. Awaria `MailGateway` pozostawi zapisane zamówienie, a metoda zakończy się wyjątkiem.
- **Testowalność.** Klasa **nie jest** nietestowalna: zależności są wstrzykiwane przez konstruktor jako interfejsy, metoda zwraca wynik. To ważna korekta częstej intuicji „legacy = nie da się przetestować” (teoria 8.10).
- **Pułapka dla prowadzącego:** nie pozwól grupie przejść od razu do wzorca Strategia czy enumów. Ograniczenie Ćwiczenia 1 zabrania proponowania docelowego projektu.

**Testy chroniące zachowanie:**

`LegacyOrderServiceTest.placesVipOrderAndInvokesExternalCollaborators` - test charakteryzujący przez publiczną metodę, z implementacjami zastępczymi repozytorium i bramki pocztowej (lambdy zapisujące wartości do `AtomicReference`). Sprawdza:

- sumę 236.39 dla klienta VIP, 2 sztuk po 100.00, dostawy standardowej, kraju PL (200.00 x 0.90 = 180.00; wysyłka 14.99, bo 180.00 < 200.00; podatek 180.00 x 0.23 = 41.40; razem 236.39),
- że do repozytorium zapisano tę samą kwotę,
- treść wiadomości `Order total: 236.39`.

Test pokrywa **jedną ścieżkę**. Nie chroni: przesyłki ekspresowej, darmowej wysyłki, kraju DE i „innego”, rabatu ilościowego, klienta bez VIP, wyjątku walidacji. To dobry materiał na pytanie do grupy.

**Sugerowany przebieg demonstracji (ok. 10 min, przed Ćwiczeniem 1 lub jako jego omówienie):**

1. Uruchom `Module1Examples` i pokaż pierwsze trzy linie wyniku (zapis, e-mail, paragon).
2. Otwórz `LegacyOrderService.java`. Przewiń całą metodę `placeOrder` bez komentarza i zapytaj: „Ile rzeczy robi ta metoda?”.
3. Pokaż konstruktor i interfejsy `OrderRepository`, `MailGateway` na końcu pliku. Pytanie: „Czy ta klasa jest testowalna?”.
4. Otwórz `LegacyOrderServiceTest.java`, policz z grupą 236.39 na tablicy.
5. Pytanie: „Które zachowania nie są chronione tym testem? Co się stanie, gdy dodamy `PARTNER`, Czechy i odbiór osobisty?”.
6. Pytanie kontrolne: „Które z Waszych obserwacji to fakty, a które hipotezy?”.
7. Zakończ odniesieniem do 8.10: pierwszym krokiem są testy charakteryzujące, nie przepisanie.

---

## Przykład 2. `RiskClassifier` - złożoność cyklomatyczna i poznawcza

**Sekcje teorii:** 6.2, 6.3.

**Pliki:**

- `src/main/java/pl/training/module1/RiskClassifier.java` (zawiera też rekordy `OrderSummary`, `Item`)
- `src/test/java/pl/training/module1/RiskClassifierTest.java`

**Co ilustruje:**

Metoda `riskLevel` ma cztery punkty decyzyjne: dwa `if`, pętlę `for` i `if` wewnątrz pętli. Złożoność cyklomatyczna przy prostym liczeniu: `V(G) = 4 + 1 = 5`.

**Na co zwrócić uwagę uczestników:**

- Wynik 5 **nie** oznacza 5 możliwych ścieżek wykonania. To liczba liniowo niezależnych ścieżek; pętla po dowolnej liczbie elementów daje nieograniczenie wiele ścieżek wykonania.
- Metryka nie uwzględnia zagnieżdżenia. `if` wewnątrz `for` liczy się tak samo jak płaski `if`. Tę słabość adresuje złożoność poznawcza (6.3), w której konstrukcja zagnieżdżona dostaje dodatkowy narzut za głębokość.
- Różne narzędzia mogą podać inny wynik (operatory logiczne, `switch`, wyjątki, kod bajtowy). JaCoCo liczy z kodu bajtowego wzorem `V(G) = B - D + 1`. Porównujemy tylko wyniki tego samego narzędzia w tej samej konfiguracji.
- Kod jest czytelny mimo wyniku 5: wysoka liczba sama nie jest werdyktem.

**Testy chroniące zachowanie:**

`RiskClassifierTest.countsIndependentRiskConditions` - zamówienie 1500.00, międzynarodowe, dwie pozycje (jedna krucha): oczekiwany poziom 3. Test przechodzi przez wszystkie decyzje po stronie „prawda”, a pętla wykonuje też fałszywą gałąź wewnętrznego `if` (drugi element nie jest kruchy). Brakuje przypadku kwoty dokładnie 1000.00 (warunek `> 0`, więc granica nie podnosi ryzyka) i zamówienia krajowego.

**Sugerowany przebieg demonstracji (ok. 5 min):**

1. Pokaż metodę `riskLevel` i poproś grupę o policzenie punktów decyzyjnych.
2. Zapisz na tablicy `V(G) = 1 + suma(k_i - 1)` i wynik 5.
3. Pytanie: „Ile przypadków testowych potrzeba, żeby pokryć wszystkie gałęzie? Czy 5 testów wystarczy do sprawdzenia poprawności?”.
4. Pytanie: „Czy ta metoda jest trudna do zrozumienia? Czy wynik 5 powinien uruchomić refaktoryzację?”.
5. Opcjonalnie: przepisz na tablicy wersję z trzema zagnieżdżonymi `if` o tym samym `V(G)` i porównaj odczucie czytelności (wprowadzenie do złożoności poznawczej).

---

## Przykład 3. `DiscountPolicy` - pokrycie linii a pokrycie gałęzi

**Sekcja teorii:** 6.4.

**Pliki:**

- `src/main/java/pl/training/module1/DiscountPolicy.java`
- `src/test/java/pl/training/module1/DiscountPolicyTest.java`

**Co ilustruje:**

Jeden test `discountPercent(100, true)` wykonuje każdą linię metody (100 procent pokrycia linii), ale żadnej fałszywej gałęzi obu decyzji: 2 z 4 gałęzi, czyli 50 procent pokrycia gałęzi.

**Na co zwrócić uwagę uczestników:**

- Nieprzetestowane przypadki: zamówienie poniżej 100 (brak rabatu progowego), klient bez VIP, wartość graniczna 99.
- Nawet 100 procent pokrycia gałęzi nie dowodzi poprawności: testy mogą nie mieć istotnych asercji, używać niereprezentatywnych danych, pomijać interakcje warunków. Tu rabaty są sumowane (10 + 5 = 15) jako punkty procentowe, podczas gdy w `LegacyOrderService` rabaty są mnożone. Który model jest właściwy, rozstrzyga domena, nie pokrycie.
- `Module1ExamplesTest` wywołuje tę samą metodę z tymi samymi argumentami, więc nie zmienia pokrycia gałęzi.
- JaCoCo nie liczy ścieżek obsługi wyjątków jako gałęzi; linia jest „pokryta”, gdy wykonano choć jedną jej instrukcję kodu bajtowego.

**Testy chroniące zachowanie:**

`DiscountPolicyTest.combinesThresholdAndVipDiscount` - sprawdza wynik 15 dla wartości 100 i klienta VIP (granica progu oraz oba rabaty naraz).

**Sugerowany przebieg demonstracji (ok. 5-7 min):**

1. Pokaż metodę i test obok siebie. Pytanie: „Jakie pokrycie linii da ten test? A gałęzi?”.
2. Uruchom `mvn verify` i otwórz `target/site/jacoco/index.html`, przejdź do pakietu `pl.training.module1`, klasy `DiscountPolicy`. Pokaż żółte (częściowo pokryte) linie z `if` i licznik gałęzi 2/4.
3. Pytanie: „Jaki drugi test podniesie pokrycie gałęzi do 100 procent? Czy wtedy będziemy pewni poprawności?”.
4. Połącz z antywzorcem „optymalizacja pod wynik metryki” (6.8): test bez asercji podniósłby pokrycie tak samo.

---

## Przykład 4. `SalesCalculations` - duplikacja wymagająca analizy

**Sekcja teorii:** 6.5.

**Pliki:**

- `src/main/java/pl/training/module1/SalesCalculations.java`
- `src/test/java/pl/training/module1/SalesCalculationsTest.java`

**Co ilustruje:**

Metody `invoiceLineTotal` i `quoteLineTotal` są identyczne: mnożą cenę przez ilość i dla VIP stosują 0.90. Narzędzie wykryje kopię typu „identyczne poza nazwami”.

**Na co zwrócić uwagę uczestników:**

- Kluczowe pytanie z teorii: czy faktura i oferta rzeczywiście korzystają z **jednej** reguły rabatowej? Jeżeli tak, rozproszenie grozi niespójną zmianą (jak w `PricingService` z Ćwiczenia 2). Jeżeli polityki mają ewoluować niezależnie, podobieństwo jest przejściowe, a przedwczesne połączenie stworzy błędną zależność.
- Nazwa testu `duplicatedCalculationsCurrentlyProduceTheSameResult` celowo zawiera słowo „currently”: test dokumentuje obecną zbieżność, a nie wymaganie, żeby wyniki były zawsze równe.
- Wynik ma skalę 4 (`90.0000`), bo mnożenie `BigDecimal` sumuje skale i metoda nie zaokrągla. W `LegacyOrderService` zaokrąglenie następuje na końcu (`setScale(2, HALF_UP)`). Dobre pytanie domenowe: na jakim etapie należy zaokrąglać.
- Wartość `0.90` dla VIP występuje także w `LegacyOrderService`. To trzecia kopia tej samej liczby w pakiecie; czy to ta sama reguła, wiadomo dopiero po rozmowie z właścicielem produktu.

**Testy chroniące zachowanie:**

`SalesCalculationsTest.duplicatedCalculationsCurrentlyProduceTheSameResult` - dla ceny 100.00, ilości 1 i VIP sprawdza, że faktura daje `90.0000` i oferta daje tę samą wartość.

**Sugerowany przebieg demonstracji (ok. 5 min):**

1. Pokaż obie metody. Pytanie: „Połączyć czy nie?”. Zbierz głosy.
2. Pokaż nazwę testu i zapytaj, co mówi słowo „currently”.
3. Pytanie: „Jakich informacji potrzebujecie, żeby zdecydować?” (właściciel reguły, historia współzmienności obu metod, plan zmian polityki ofert i faktur, dotychczasowe regresje).
4. Odwołaj się do odpowiedzi 7 ze Sprawdzenia wiedzy.

---

## Przykład 5. `Module1Examples` - punkt wejścia

**Pliki:**

- `src/main/java/pl/training/module1/Module1Examples.java`
- `src/test/java/pl/training/module1/Module1ExamplesTest.java`

**Co ilustruje:** uruchamia po kolei cztery przykłady (`runLegacyOrderService`, `runRiskClassifier`, `runDiscountPolicy`, `runSalesCalculations`) na tych samych danych co testy. W `runLegacyOrderService` repozytorium i bramka pocztowa są zastąpione lambdami wypisującymi na konsolę, co pokazuje istniejące punkty separacji.

**Test:** `Module1ExamplesTest.runsAllModuleExamples` sprawdza tylko, że `main` nie rzuca wyjątku (`assertDoesNotThrow`). Dobry przykład testu, który podnosi pokrycie, ale niemal niczego nie weryfikuje (brak asercji wyniku). Można go przywołać przy sekcji 6.4 i antywzorcu 6.8.

---

## Proponowana kolejność na zajęciach

| Kolejność | Teoria | Przykład | Zadanie |
| --- | --- | --- | --- |
| 1 | 1-2 (definicje, język diagnozy) | - | Aktywność 1.1 |
| 2 | 3-4 (ryzyka, dług techniczny) | - | Aktywność 1.2 |
| 3 | 5 (*code smells*) | Przykład 1 `LegacyOrderService` (krótko, bez omawiania odpowiedzi) | - |
| 4 | 6.2-6.3 | Przykład 2 `RiskClassifier` | - |
| 5 | 6.4 | Przykład 3 `DiscountPolicy` (+ JaCoCo) | - |
| 6 | 6.5-6.10 | Przykład 4 `SalesCalculations` | Aktywność 1.3 |
| 7 | 7-8 | Przykład 1 wraca przy 8.10 | Aktywność 1.4 |
| 8 | 9 (warsztat) | `LegacyOrderService.java` jako materiał | Ćwiczenie 1, 2, 3 |
| 9 | 11 | - | Sprawdzenie wiedzy |
