# Moduł 1. Wprowadzenie - przewodnik prowadzącego po przykładach

Materiał dla prowadzącego. Opisuje kod projektu powiązany z modułem 1 (pakiet `pl.training.module1`), sposób jego uruchomienia i sugerowany przebieg demonstracji.

Moduł 1 jest diagnostyczny. Kod projektu to **materiał do analizy, a nie rozwiązania wzorcowe**. W pakiecie nie ma wersji before/after ani etapów: żadna klasa nie jest refaktoryzowana w tym module. Bezpieczne zmiany struktury i testy pojawiają się w kolejnych modułach (prewencja w module 2, sieć bezpieczeństwa w module 3, wzorce w modułach 4 i 5). Warto to powiedzieć grupie na początku, żeby nie szukała „poprawionej wersji”.

Wszystkie klasy są celowo małe: pozwalają pokazać konkretne pojęcie (zapach, złożoność, pokrycie, duplikacja) na kodzie, który mieści się na jednym ekranie.

## Uruchamianie przykładów i testów

Wszystkie polecenia uruchamiamy z katalogu głównego repozytorium.

### Java

```bash
mvn -q compile && java -cp target/classes pl.training.module1.Module1Examples
mvn test -Dtest='pl.training.module1.**'
```

Testy można też uruchomić z IDE (pakiet `pl.training.module1` w `src/test/java`). Raport pokrycia JaCoCo (przydatny przy przykładzie `DiscountPolicy`) powstaje w fazie `verify`: `mvn verify`, a następnie `target/site/jacoco/index.html`.

Oczekiwany wynik `Module1Examples`:

```text
Saved order 9aa026a4-fc39-4af8-a008-d9b831b0ba59 with total 236.39
Sent to customer@example.com: Order total: 236.39
Receipt: Receipt[orderId=9aa026a4-fc39-4af8-a008-d9b831b0ba59, total=236.39]
Risk level: 3
Discount: 15%
Invoice line total: 90.0000
Quote line total: 90.0000
```

### C#

```bash
cd csharp && dotnet run --project src/Training.Module1
dotnet test tests/Training.Module1.Tests
```

Kod: `csharp/src/Training.Module1/`, testy: `csharp/tests/Training.Module1.Tests/`.

### TypeScript

```bash
cd typescript && npm ci && npm run build && npm run module1
npx vitest run test/module1
```

Kod: `typescript/src/module1/`, testy: `typescript/test/module1/`.

Odpowiedniki w C# i TypeScript mają te same nazwy klas i plików (`LegacyOrderService.cs` / `LegacyOrderService.ts` itd.), a testy te same nazwy z sufiksem `Test.cs` / `.test.ts`.

## Mapa przykładów

| Przykład | Sekcja teorii | Pliki (Java) | Testy | Zadania powiązane |
| --- | --- | --- | --- | --- |
| 1. `LegacyOrderService` | 3.2 *Code smells* jako hipotezy, 3.3-3.4 Katalog zapachów, 3.5 Przykład, 3.6 Od zapachu do kierunku | `src/main/java/pl/training/module1/LegacyOrderService.java` | `src/test/java/pl/training/module1/LegacyOrderServiceTest.java` | Ćwiczenie 1, Ćwiczenie 2, Aktywność 1.3 (kontekst) |
| 2. `RiskClassifier` | 4.2 Złożoność cyklomatyczna, 4.3 Złożoność poznawcza | `src/main/java/pl/training/module1/RiskClassifier.java` | `src/test/java/pl/training/module1/RiskClassifierTest.java` | Aktywność 1.4 (kontekst metryk) |
| 3. `DiscountPolicy` | 4.4 Pokrycie linii a pokrycie gałęzi | `src/main/java/pl/training/module1/DiscountPolicy.java` | `src/test/java/pl/training/module1/DiscountPolicyTest.java` | Aktywność 1.5 |
| 4. `SalesCalculations` | 4.5 Duplikacje, 3.6 (Duplicated Code) | `src/main/java/pl/training/module1/SalesCalculations.java` | `src/test/java/pl/training/module1/SalesCalculationsTest.java` | Ćwiczenie 2, Ćwiczenie 3 (`PricingService`, reguła VIP) |
| 5. `Module1Examples` | cały moduł (uruchomienie przykładów), 4.8 Antywzorce metryk | `src/main/java/pl/training/module1/Module1Examples.java` | `src/test/java/pl/training/module1/Module1ExamplesTest.java` | - |

---

## Przykład 1. `LegacyOrderService` - zapachy w jednej metodzie (`pl.training.module1`)

**Sekcja teorii:** 3.2 *Code smells* jako hipotezy diagnostyczne, 3.3-3.4 Katalog zapachów, 3.5 Przykład `LegacyOrderService`, 3.6 Od zapachu do kierunku refaktoryzacji; materiał wejściowy Ćwiczeń 1 i 2.

**Co ilustruje:** jedna metoda `placeOrder` łączy walidację, wycenę pozycji z rabatami, koszt wysyłki, podatek, zaokrąglenie, zapis i wysłanie wiadomości. Widoczne zapachy (w słowniku katalogu ze slajdów 3.3-3.4):

- **Long Method** (Bloaters): wiele odpowiedzialności w jednej metodzie,
- **Long Parameter List** i **Primitive Obsession** (Bloaters): typ klienta i kraj jako `String`,
- argument `boolean express` wybierający odrębne zachowanie (sygnał **Switch Statements** z grupy Object-Orientation Abusers: wariant wyrażony warunkiem),
- znaczące liczby bez nazw: `0.90`, `0.95`, `10`, `39.99`, `200.00`, `14.99`, `0.23`, `0.19`,
- wariant zachowania rozproszony po `if` (kraje, typ klienta, dostawa) - kandydat na **Conditional Complexity**,
- reguła VIP `0.90` powtórzona także w `SalesCalculations` (**Duplicated Code**, Dispensables) - czy to ta sama wiedza, rozstrzyga domena.

**Klasy:** `src/main/java/pl/training/module1/LegacyOrderService.java` - klasa i powiązane typy w jednym pliku: rekordy `Order`, `OrderLine`, `Receipt`, interfejsy `OrderRepository`, `MailGateway`.

Kluczowy fragment (wysyłka i podatek):

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

**Na co zwrócić uwagę:**

- **Fakt a hipoteza.** „Podatek dla innych krajów wynosi zero” to fakt z kodu. „Podatek jest liczony błędnie” to hipoteza, dopóki nie znamy reguły biznesowej.
- **Kolejność operacji.** Próg darmowej wysyłki (`>= 200.00`) porównywany jest z sumą **po** rabatach. Ekspres zawsze kosztuje 39.99, niezależnie od progu. Podatek liczony jest od `subtotal`, bez wysyłki. Każde z tych zachowań może być zamierzone albo nie; to pytania domenowe, nie błędy.
- **Składanie rabatów.** Rabat VIP (0.90) i ilościowy (0.95 od 10 sztuk) są mnożone, bez pośredniego zaokrąglania; zaokrąglenie `HALF_UP` do 2 miejsc następuje dopiero na końcu.
- **Efekty uboczne.** Zapis (`repository.save`) następuje przed wysłaniem wiadomości. Awaria `MailGateway` pozostawi zapisane zamówienie, a metoda zakończy się wyjątkiem.
- **Testowalność.** Klasa **nie jest** nietestowalna: zależności są wstrzykiwane przez konstruktor jako interfejsy, metoda zwraca wynik. To ważna korekta częstej intuicji „legacy = nie da się przetestować”.
- **Zapach a moment decyzji.** Tabela ze slajdu 3.6 podpowiada kierunek (np. Primitive Obsession -> Replace Type Code with Class, Conditional Complexity -> Strategy), ale nie mówi, kiedy go wykonać. `customerType` jako `String` boli dopiero przy dodaniu `PARTNER`; kraj jako `String` przy Czechach. To materiał Ćwiczenia 2.
- **Pułapka dla prowadzącego:** w Ćwiczeniu 1 nie pozwól grupie przejść od razu do wzorca Strategia czy enumów. Ograniczenie ćwiczenia zabrania proponowania docelowego projektu. Kierunki pojawiają się dopiero w Ćwiczeniu 2 i tylko jako hipotezy.

**Testy:** `LegacyOrderServiceTest.placesVipOrderAndInvokesExternalCollaborators` - test charakteryzujący przez publiczną metodę, z implementacjami zastępczymi repozytorium i bramki pocztowej (lambdy zapisujące wartości do `AtomicReference`). Sprawdza:

- sumę 236.39 dla klienta VIP, 2 sztuk po 100.00, dostawy standardowej, kraju PL (200.00 x 0.90 = 180.00; wysyłka 14.99, bo 180.00 < 200.00; podatek 180.00 x 0.23 = 41.40; razem 236.39),
- że do repozytorium zapisano tę samą kwotę,
- treść wiadomości `Order total: 236.39`.

Test pokrywa **jedną ścieżkę**. Nie chroni: przesyłki ekspresowej, darmowej wysyłki, kraju DE i „innego”, rabatu ilościowego, klienta bez VIP, wyjątku walidacji. To dobry materiał na pytanie do grupy (i zapowiedź modułu 3).

**Demonstracja na żywo (ok. 10 min, przed Ćwiczeniem 1 lub jako jego omówienie):**

1. Uruchom `Module1Examples` i pokaż pierwsze trzy linie wyniku (zapis, e-mail, paragon).
2. Otwórz `LegacyOrderService.java`. Przewiń całą metodę `placeOrder` bez komentarza i zapytaj: „Ile rzeczy robi ta metoda?”.
3. Pokaż konstruktor i interfejsy `OrderRepository`, `MailGateway` na końcu pliku. Pytanie: „Czy ta klasa jest testowalna?”.
4. Otwórz `LegacyOrderServiceTest.java`, policz z grupą 236.39 na tablicy.
5. Pytanie: „Które zachowania nie są chronione tym testem? Co się stanie, gdy dodamy `PARTNER`, Czechy i odbiór osobisty?”.
6. Pytanie kontrolne: „Które z Waszych obserwacji to fakty, a które hipotezy?”.
7. Po Ćwiczeniu 1 wróć do kodu ze slajdem 3.6 obok: „Który zapach utrudnia którą z trzech planowanych zmian?”. Zakończ: pierwszym krokiem są testy charakteryzujące, nie przepisanie ani wzorzec.

---

## Przykład 2. `RiskClassifier` - złożoność cyklomatyczna i poznawcza (`pl.training.module1`)

**Sekcja teorii:** 4.2 Złożoność cyklomatyczna, 4.3 Złożoność poznawcza.

**Co ilustruje:** metoda `riskLevel` ma cztery punkty decyzyjne: dwa `if`, pętlę `for` i `if` wewnątrz pętli. Złożoność cyklomatyczna przy prostym liczeniu: `V(G) = 4 + 1 = 5`.

**Klasy:** `src/main/java/pl/training/module1/RiskClassifier.java` (zawiera też rekordy `OrderSummary`, `Item`).

**Na co zwrócić uwagę:**

- Wynik 5 **nie** oznacza 5 możliwych ścieżek wykonania. To liczba liniowo niezależnych ścieżek; pętla po dowolnej liczbie elementów daje nieograniczenie wiele ścieżek wykonania.
- Metryka nie uwzględnia zagnieżdżenia. `if` wewnątrz `for` liczy się tak samo jak płaski `if`. Tę słabość adresuje złożoność poznawcza (4.3), w której konstrukcja zagnieżdżona dostaje dodatkowy narzut za głębokość.
- Różne narzędzia mogą podać inny wynik (operatory logiczne, `switch`, wyjątki, kod bajtowy). JaCoCo liczy z kodu bajtowego wzorem `V(G) = B - D + 1`. Porównujemy tylko wyniki tego samego narzędzia w tej samej konfiguracji.
- Kod jest czytelny mimo wyniku 5: wysoka liczba sama nie jest werdyktem. W słowniku katalogu nie ma tu żadnego zapachu wartego interwencji.

**Testy:** `RiskClassifierTest.countsIndependentRiskConditions` - zamówienie 1500.00, międzynarodowe, dwie pozycje (jedna krucha): oczekiwany poziom 3. Test przechodzi przez wszystkie decyzje po stronie „prawda”, a pętla wykonuje też fałszywą gałąź wewnętrznego `if` (drugi element nie jest kruchy). Brakuje przypadku kwoty dokładnie 1000.00 (warunek `> 0`, więc granica nie podnosi ryzyka) i zamówienia krajowego.

**Demonstracja na żywo (ok. 5 min):**

1. Pokaż metodę `riskLevel` i poproś grupę o policzenie punktów decyzyjnych.
2. Zapisz na tablicy `V(G) = 1 + suma(k_i - 1)` i wynik 5.
3. Pytanie: „Ile przypadków testowych potrzeba, żeby pokryć wszystkie gałęzie? Czy 5 testów wystarczy do sprawdzenia poprawności?”.
4. Pytanie: „Czy ta metoda jest trudna do zrozumienia? Czy wynik 5 powinien uruchomić refaktoryzację?”.
5. Opcjonalnie: przepisz na tablicy wersję z trzema zagnieżdżonymi `if` o tym samym `V(G)` i porównaj odczucie czytelności (wprowadzenie do złożoności poznawczej).

---

## Przykład 3. `DiscountPolicy` - pokrycie linii a pokrycie gałęzi (`pl.training.module1`)

**Sekcja teorii:** 4.4 Pokrycie linii a pokrycie gałęzi, 4.8 Antywzorce użycia metryk.

**Co ilustruje:** jeden test `discountPercent(100, true)` wykonuje każdą linię metody (100 procent pokrycia linii), ale żadnej fałszywej gałęzi obu decyzji: 2 z 4 gałęzi, czyli 50 procent pokrycia gałęzi.

**Klasy:** `src/main/java/pl/training/module1/DiscountPolicy.java`.

**Na co zwrócić uwagę:**

- Nieprzetestowane przypadki: zamówienie poniżej 100 (brak rabatu progowego), klient bez VIP, wartość graniczna 99.
- Nawet 100 procent pokrycia gałęzi nie dowodzi poprawności: testy mogą nie mieć istotnych asercji, używać niereprezentatywnych danych, pomijać interakcje warunków. Tu rabaty są sumowane (10 + 5 = 15) jako punkty procentowe, podczas gdy w `LegacyOrderService` rabaty są mnożone. Który model jest właściwy, rozstrzyga domena, nie pokrycie.
- `Module1ExamplesTest` wywołuje tę samą metodę z tymi samymi argumentami, więc nie zmienia pokrycia gałęzi.
- JaCoCo nie liczy ścieżek obsługi wyjątków jako gałęzi; linia jest „pokryta”, gdy wykonano choć jedną jej instrukcję kodu bajtowego.
- W tym module wystarczy rozróżnienie linii i gałęzi. Szczegółowa praca z raportem, test mutacyjny i dobór portfela testów są tematem modułu 3 - nie rozwijaj ich tutaj.

**Testy:** `DiscountPolicyTest.combinesThresholdAndVipDiscount` - sprawdza wynik 15 dla wartości 100 i klienta VIP (granica progu oraz oba rabaty naraz).

**Demonstracja na żywo (ok. 5 min):**

1. Pokaż metodę i test obok siebie. Pytanie: „Jakie pokrycie linii da ten test? A gałęzi?”.
2. Uruchom `mvn verify` i otwórz `target/site/jacoco/index.html`, przejdź do pakietu `pl.training.module1`, klasy `DiscountPolicy`. Pokaż żółte (częściowo pokryte) linie z `if` i licznik gałęzi 2/4.
3. Pytanie: „Jaki drugi test podniesie pokrycie gałęzi do 100 procent? Czy wtedy będziemy pewni poprawności?”.
4. Połącz z antywzorcem „optymalizacja pod wynik metryki” (4.8): test bez asercji podniósłby pokrycie tak samo.

---

## Przykład 4. `SalesCalculations` - duplikacja wymagająca analizy (`pl.training.module1`)

**Sekcja teorii:** 4.5 Duplikacje; 3.6 (Duplicated Code jako zapach z grupy Dispensables).

**Co ilustruje:** metody `invoiceLineTotal` i `quoteLineTotal` są identyczne: mnożą cenę przez ilość i dla VIP stosują 0.90. Narzędzie wykryje kopię typu „identyczne poza nazwami”.

**Klasy:** `src/main/java/pl/training/module1/SalesCalculations.java`.

**Na co zwrócić uwagę:**

- Kluczowe pytanie: czy faktura i oferta rzeczywiście korzystają z **jednej** reguły rabatowej? Jeżeli tak, rozproszenie grozi niespójną zmianą (jak w `PricingService` z Ćwiczenia 3) i zapach Duplicated Code prowadzi do Extract Method. Jeżeli polityki mają ewoluować niezależnie, podobieństwo jest przejściowe, a przedwczesne połączenie stworzy błędną zależność.
- Nazwa testu `duplicatedCalculationsCurrentlyProduceTheSameResult` celowo zawiera słowo „currently”: test dokumentuje obecną zbieżność, a nie wymaganie, żeby wyniki były zawsze równe.
- Wynik ma skalę 4 (`90.0000`), bo mnożenie `BigDecimal` sumuje skale i metoda nie zaokrągla. W `LegacyOrderService` zaokrąglenie następuje na końcu (`setScale(2, HALF_UP)`). Dobre pytanie domenowe: na jakim etapie należy zaokrąglać.
- Wartość `0.90` dla VIP występuje także w `LegacyOrderService`. To trzecia kopia tej samej liczby w pakiecie; czy to ta sama reguła, wiadomo dopiero po rozmowie z właścicielem produktu.

**Testy:** `SalesCalculationsTest.duplicatedCalculationsCurrentlyProduceTheSameResult` - dla ceny 100.00, ilości 1 i VIP sprawdza, że faktura daje `90.0000` i oferta daje tę samą wartość.

**Demonstracja na żywo (ok. 5 min):**

1. Pokaż obie metody. Pytanie: „Połączyć czy nie?”. Zbierz głosy.
2. Pokaż nazwę testu i zapytaj, co mówi słowo „currently”.
3. Pytanie: „Jakich informacji potrzebujecie, żeby zdecydować?” (właściciel reguły, historia współzmienności obu metod, plan zmian polityki ofert i faktur, dotychczasowe regresje).
4. Odwołaj się do pytania 7 ze Sprawdzenia wiedzy.

---

## Przykład 5. `Module1Examples` - punkt wejścia (`pl.training.module1`)

**Sekcja teorii:** cały moduł; 4.8 Antywzorce użycia metryk (słaba wyrocznia testu).

**Co ilustruje:** uruchamia po kolei cztery przykłady (`runLegacyOrderService`, `runRiskClassifier`, `runDiscountPolicy`, `runSalesCalculations`) na tych samych danych co testy. W `runLegacyOrderService` repozytorium i bramka pocztowa są zastąpione lambdami wypisującymi na konsolę, co pokazuje istniejące punkty separacji.

**Klasy:** `src/main/java/pl/training/module1/Module1Examples.java`.

**Na co zwrócić uwagę:** program pokazuje, że kod legacy da się uruchomić i obserwować bez przebudowy. Kolejność wyjścia odpowiada kolejności przykładów w tym przewodniku.

**Testy:** `Module1ExamplesTest.runsAllModuleExamples` sprawdza tylko, że `main` nie rzuca wyjątku (`assertDoesNotThrow`). Dobry przykład testu, który podnosi pokrycie, ale niemal niczego nie weryfikuje (brak asercji wyniku). Można go przywołać przy slajdzie 4.4 i antywzorcu z 4.8.

**Demonstracja na żywo:** uruchom program na początku modułu jako „mapę” przykładów. Na końcu zapytaj: „Co by się stało z `Module1ExamplesTest`, gdyby suma zamówienia zmieniła się z 236.39 na 0.00?” (nic - test nadal byłby zielony).

---

## Proponowany plan modułu (~2.5 h)

| Kolejność | Teoria | Przykład | Zadanie | Czas |
| --- | --- | --- | --- | --- |
| 1 | 1. Czym jest refaktoryzacja (1.1-1.4) | - | Aktywność 1.1 | ~12 min |
| 2 | 2. Kiedy warto refaktoryzować? (2.1-2.7) | - | Aktywność 1.2 | ~12 min |
| 3 | 2.8-2.12 (strategie i decyzja) | - | Aktywność 1.3 | ~10 min |
| 4 | 3. *Code smells* (3.1-3.6) | Przykład 1 `LegacyOrderService` (krótko, bez omawiania odpowiedzi) | Aktywność 1.4, Ćwiczenie 1, Ćwiczenie 2 | ~48 min |
| 5 | 4.1-4.3 | Przykład 2 `RiskClassifier` | - | ~5 min |
| 6 | 4.4 | Przykład 3 `DiscountPolicy` (+ JaCoCo) | - | ~4 min |
| 7 | 4.5-4.8 | Przykład 4 `SalesCalculations` | Aktywność 1.5 | ~9 min |
| 8 | warsztat | materiał scenariuszowy z zadań | Ćwiczenie 3, Ćwiczenie 4 | ~50 min |
| 9 | podsumowanie | Przykład 5 `Module1Examples` | Sprawdzenie wiedzy, lista kontrolna | ~5 min |

Razem około 155 minut. Przy braku czasu skróć Ćwiczenie 4 do omówienia modelowego rekordu decyzji z rozwiązań (5 min) albo przenieś je do pracy własnej.
