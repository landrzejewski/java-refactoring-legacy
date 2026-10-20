---
marp: true
theme: default
paginate: true
size: 16:9
style: |
  section { font-size: 24px; }
  section.lead h1 { font-size: 48px; }
  pre, code { font-size: 18px; }
---

<!-- _class: lead -->
# Moduł 3. Testowanie a bezpieczeństwo

Jaką sieć bezpieczeństwa potrzebuje refaktoryzacja: portfel testów, obiekty zastępcze, pokrycie i praca z kodem bez testów

---

## Agenda

1. Piramida testów
2. Test Double Patterns
3. Code Coverage - wady i zalety
4. Jak poradzić sobie bez testów?
5. Ćwiczenia, sprawdzenie wiedzy, lista kontrolna

Moduł 1 nazwał zapachy i metryki, moduł 2 - krótką pętlę informacji zwrotnej. Tu budujemy **dowód**, że struktura zmienia się bez zmiany zachowania. Testowanie przy refaktoryzacji do wzorców i wsparcie IDE w szczegółach - moduł 5.

---

## 1.1. Test dostarcza dowodu, nie pewności

- Zielony zestaw znaczy tylko: **żaden test nie wykrył różnicy** w tym, co sprawdził.
- Nie gwarantuje pełnych wymagań, wszystkich ścieżek, danych granicznych ani czułych asercji.
- Bezpieczeństwo rośnie przez połączenie: małe kroki, przegląd diffu, kompilator, analiza statyczna, testy integracyjne, obserwacja produkcji.

---

## 1.2. Testuj stabilne zachowanie

- Test przywiązany do prywatnych metod i liczby klas może **blokować poprawną refaktoryzację**.
- Obserwuj najwęższą stabilną granicę, która wykrywa ważną zmianę.
- Weryfikacja stanu i wyniku jest zwykle odporniejsza niż weryfikacja interakcji.
- Interakcję weryfikuj, gdy jest kontraktem: jedno obciążenie płatności, publikacja po zatwierdzeniu transakcji, idempotencja.

---

## 1.3. Cechy użytecznej sieci bezpieczeństwa

| Cecha | Znaczenie podczas refaktoryzacji |
| --- | --- |
| szybkość | wynik przed kolejnym krokiem |
| determinizm | porażka wskazuje zmianę, nie czas czy sieć |
| czułość | istotna zmiana zachowania powoduje porażkę |
| stabilność strukturalna | zmiana wnętrza nie psuje testów |
| diagnostyka | komunikat wskazuje źródło problemu |
| realizm | granice wysokiego ryzyka z prawdziwą technologią |

Żaden pojedynczy test nie ma wszystkich cech - potrzebny jest **portfel testów**.

---

## 1.4. Punkt zmiany i punkt testowania

Miejsce modyfikacji nie musi być najlepszym miejscem obserwacji. Na początku często zabezpieczamy szerszy przebieg, a precyzyjne testy dodajemy po poprawie struktury.

Przed pracą odpowiedz:

1. Gdzie kod zostanie zmieniony?
2. Skąd uruchomić zachowanie i gdzie widać jego efekt?
3. Które zależności blokują kontrolowane wykonanie?
4. Jaki najmniejszy test wykryje niezamierzoną zmianę?

---

## 1.5. Piramida testów to heurystyka

- Małych, szybkich testów zwykle jest dużo, szerokich i kosztownych - mniej.
- Nie wynika z niej proporcja (np. 70/20/10) ani obowiązek mockowania wszystkiego.
- Szybki, deterministyczny test szeroki może być bardzo wartościowy.
- Etykiety („jednostkowy”, „integracyjny”) są niejednoznaczne - opisuj test **przez właściwości**: zakres, interfejs, realizm zależności, szybkość, determinizm, wykrywane ryzyko, koszt.

---

## 1.6. Typowy portfel testów

| Rodzaj | Najważniejsza wartość | Ograniczenie |
| --- | --- | --- |
| logika domenowa | szybka diagnoza reguł | brak integracji |
| komponentowy | współpraca klas przez granicę | uproszczone zależności |
| wąski integracyjny | adapter, mapowanie, konfiguracja | zarządzanie realną technologią |
| kontraktowy | zgodność producenta i konsumenta | nie cały przepływ |
| systemowy / E2E | najważniejsze ścieżki całości | koszt, niestabilność |

---

## 1.7. Kolejność uruchamiania i strategia dla legacy

- Kolejność: kompilacja i szybkie testy obszaru → moduł → integracja i kontrakty → system → krytyczne E2E → testy pozafunkcjonalne.
- **Etykieta nie powinna opóźniać wartościowej informacji** - szybki test adaptera może iść w pierwszym etapie.
- W legacy nie buduj od razu idealnej piramidy - zacznij od planowanej zmiany i jej ryzyka.
- Zacznij od stabilnej, nawet szerokiej granicy; szybsze testy dodawaj po wprowadzeniu szwów.
- Defekt z testu szerokiego odtwórz na najniższym poziomie, zachowując test szeroki, jeśli chroni integrację.

---

## Aktywność 1.8: zaprojektuj portfel (5 min)

System **nalicza opłatę**, **zapisuje wynik w PostgreSQL** i **publikuje komunikat**.
Ostatnie regresje: wartość graniczna, mapowanie `BigDecimal`, niezgodny schemat komunikatu.

**Zadanie:** zaproponuj minimalny portfel. Dla każdego testu: wykrywane ryzyko, rzeczywiste zależności, czas, miejsce w procesie.

---

## 2.1. Katalog obiektów zastępczych (Meszaros)

| Rola | Intencja | Kiedy | Ryzyko |
| --- | --- | --- | --- |
| dummy | wypełnia parametr, nigdy nieużywany | sygnatura wymaga obiektu, scenariusz go nie dotyka | ukrywa zbędny parametr; po cichu zaczyna być używany |
| stub | dostarcza **pośrednie wejście** | sterujesz ścieżką: cena, błąd, czas | odpowiedzi nierealne dla prawdziwej zależności |
| spy | rejestruje **pośrednie wyjście** do późniejszej asercji | efekt jest kontraktem: zdarzenie, wiadomość | asercje na każdym wywołaniu zamiast na kontrakcie |
| mock | zawiera oczekiwania i sam je weryfikuje | protokół jest kontraktem: jedno obciążenie | nadspecyfikacja, kruchość przy refaktoryzacji |
| fake | uproszczona, **działająca** implementacja | repozytorium w pamięci, zegar, kolejka | rozjazd z adapterem produkcyjnym |

Rolę wyznacza **sposób użycia w teście**, nie biblioteka. Mock nie jest synonimem każdego obiektu zastępczego.

---

## 2.2. Weryfikacja stanu a weryfikacja zachowania

| | Weryfikacja stanu | Weryfikacja zachowania |
| --- | --- | --- |
| pytanie | co jest wynikiem i jaki jest stan po operacji? | jakie wywołania wykonał obiekt testowany? |
| typowe role | stub, fake | spy, mock |
| odporność na refaktoryzację | wysoka - wnętrze może się zmieniać | niska, jeśli sprawdza wewnętrznych współpracowników |
| uzasadnienie | domyślny wybór | interakcja jest kontraktem albo brak stanu do odczytu |

- Fowler („Mocks Aren't Stubs”): podejście klasyczne i mockistyczne - obie szkoły zgadzają się, że **asercja ma opisywać kontrakt**.
- Każda asercja na interakcji wewnętrznej to test, który trzeba przepisać przy poprawnym ruchu strukturalnym.

---

## 2.3. Przykładowa usługa: `OrderPlacementService`

```java
public PlacedOrder place(String sku, int quantity, String paymentToken) {
    if (quantity <= 0) {
        throw new IllegalArgumentException("Quantity must be positive");
    }
    BigDecimal total = catalog.priceFor(sku)
            .multiply(BigDecimal.valueOf(quantity))
            .setScale(2, RoundingMode.HALF_UP);
    String authorizationId = paymentGateway.charge(paymentToken, total);
    long orderId = repository.save(
            new OrderDraft(sku, quantity, total, authorizationId));
    eventPublisher.publish(new OrderPlaced(orderId, total));
    return new PlacedOrder(orderId, total, authorizationId);
}
```

Czterech współpracowników przekazanych przez konstruktor - każdy jest portem, który **należy do nas**.

---

## 2.4. Stub, fake i spy w jednym teście

```java
ProductCatalog catalogStub = sku -> new BigDecimal("12.50");
PaymentGateway paymentStub = (token, amount) -> "AUTH-7";
InMemoryOrderRepository repositoryFake = new InMemoryOrderRepository();
RecordingEventPublisher publisherSpy = new RecordingEventPublisher();
var service = new OrderPlacementService(
        catalogStub, paymentStub, repositoryFake, publisherSpy);

PlacedOrder result = service.place("BOOK", 2, "TOKEN-1");

assertEquals(new BigDecimal("25.00"), result.total());
assertEquals(new OrderDraft("BOOK", 2, new BigDecimal("25.00"), "AUTH-7"),
        repositoryFake.find(result.orderId()));
assertEquals(List.of(new OrderPlaced(result.orderId(), new BigDecimal("25.00"))),
        publisherSpy.publishedEvents());
```

Test weryfikuje głównie **stan i wynik** - zmiana wewnętrznej kolejności obliczeń go nie psuje.

---

## 2.5. Mock i ryzyka obiektów zastępczych

```java
public String charge(String paymentToken, BigDecimal amount) {  // paymentMock
    assertEquals(expectedToken, paymentToken);
    assertEquals(expectedAmount, amount);
    calls++;
    return authorizationId;
}
void verify() { assertEquals(1, calls); }
```

- **Mock** sprawdza protokół: token, kwota i **dokładnie jedno** wywołanie - uzasadnione, bo liczba obciążeń jest kontraktem.
- **Fake** może różnić się od adaptera produkcyjnego - uruchom wspólny test kontraktu dla obu.
- Baza w pamięci nie sprawdza zapytań, ograniczeń, transakcji ani mapowania typów.
- `spy` w bibliotece (np. Mockito) bywa częściowym mockiem - to nie klasyczna rola spy.

---

## 2.6. Nie mockuj tego, czego nie posiadasz

- Reguła Freemana i Pryce'a: nie zastępuj w testach **cudzego API** (klient HTTP, JDBC, SDK chmury, biblioteka płatności).
- Mock cudzego API utrwala **Twoje wyobrażenie** o nim: test zielony, produkcja czerwona; aktualizacja biblioteki psuje dziesiątki testów.
- Zamiast tego: własny wąski **port w języku domeny** (`PaymentGateway`, `ReminderGateway`) i adapter, który tłumaczy na cudze API.
- Port zastępuj w testach logiki; **adapter** sprawdź wąskim testem integracyjnym z prawdziwą technologią albo testem kontraktu.
- W legacy taki port powstaje przy rozrywaniu zależności (sekcja 4) - nie przed pierwszym testem.

---

## Aktywność 2.7: nazwij rolę (3 min)

W `OrderPlacementServiceTest` drugi test przekazuje `publisherStub = event -> {}`.

1. Czy to dummy, czy stub? Po czym poznać?
2. Który obiekt zastępczy w pierwszym teście wykryje **podwójne** obciążenie karty?
3. Co musiałoby się stać, żeby mock bramki płatności łamał regułę „nie mockuj tego, czego nie posiadasz”?

---

## 3.1. Co mierzy JaCoCo

- Analizuje **kod bajtowy**, nie bezpośrednio źródła.
- Raportuje pokrycie instrukcji, gałęzi, linii, metod i klas oraz złożoność cyklomatyczną.
- Obsługa wyjątków nie jest liczona jako gałąź; kod syntetyczny kompilatora bywa nieoczywisty.
- W projekcie: `mvn clean verify` → `target/site/jacoco/index.html`.

---

## 3.2. Pokrycie linii a pokrycie gałęzi (przypomnienie z modułu 1)

```java
public static int discountPercent(int orderValue, boolean vip) {
    int discount = 0;
    if (orderValue >= 100) { discount += 10; }
    if (vip) { discount += 5; }
    return discount;
}

@Test
void combinesThresholdAndVipDiscount() {
    assertEquals(15, DiscountPolicy.discountPercent(100, true));
}
```

- Pokrycie mówi, co zostało **wykonane**, nie co zostało sprawdzone.
- Tu 100% linii, ale tylko 2 z 4 gałęzi (50%).
- Nawet 100% gałęzi nie dowodzi poprawności - asercje mogą być słabe.

---

## 3.3. Pełne linie i brakująca gałąź: `DeliveryFee`

```java
public static int fee(boolean premium) {
    int fee = 100;
    if (premium) {
        fee = 0;
    }
    return fee;
}

@Test
void premiumCustomerHasFreeDelivery() {
    assertEquals(0, DeliveryFee.fee(true));
}
```

Wykonano **wszystkie linie**, ale tylko jedną gałąź - opłata bez premium nie jest sprawdzona. Lukę pokazuje pokrycie gałęzi, nie linii.

---

## 3.4-3.5. Pokrycie: zalety, ograniczenia, progi, nadużycia

- **Zaleta - odpowiada:** czego testy nie wykonały, czy weszły w obszar zmiany, czy warunek ma niewykonaną stronę.
- **Wada - nie odpowiada:** czy asercja jest właściwa, dane reprezentatywne, a system wolny od defektów.
- Próg chroni przed pogarszaniem, ale **nie istnieje uniwersalna wartość**.
- Nadużycia: testy bez asercji dla raportu, jedna średnia dla systemu, 100% jako dowód poprawności.
- **Test mutacyjny** ujawnia kod wykonany, ale chroniony słabymi asercjami.

---

## 3.6. Pokrycie w kodzie legacy

- Dobrze wskazuje kod niewykonany, słabo - czy testy wykrywają ważne defekty.
- Pytaj: czy testy wykonują **zmieniane zachowanie** i obie strony zmienianych decyzji?
- Czy chronią przypadki biznesowe i historyczne regresje, szybko i powtarzalnie?
- Globalny procent może ukryć krytyczny moduł z niemal zerowym pokryciem.
- Przed refaktoryzacją: raport dla **obszaru zmiany**, a nie dla całego systemu.

---

## Aktywność 3.7: raport bez celu (4 min)

Otwórz raport po `mvn clean verify` i znajdź `DeliveryFee`:

1. Która gałąź nie została wykonana?
2. Jaki test ją uruchomi?
3. Jaka asercja wykryje zmianę `100` na `200`?
4. Czy wyższe globalne pokrycie z innych klas zmniejszy to ryzyko?

---

## 4.1. Brak testów: od czego zacząć

| Sytuacja | Pierwszy ruch |
| --- | --- |
| kod da się uruchomić, wynik da się zapisać | test charakteryzujący lub golden master na szerokiej granicy |
| kodu nie da się uruchomić w teście | najmniejszy szew ruchem o niskim ryzyku, potem test |
| nie rozumiesz, co kod robi | scratch refactoring, notatki, `git reset` |
| jedyną wyrocznią jest produkcja | równoległa weryfikacja dla czystych obliczeń |

Algorytm Feathersa: **punkty zmiany → punkty testowania → rozerwanie zależności → testy → zmiana i refaktoryzacja**. Przed każdym ruchem nazwij wektor obserwowalnego zachowania.

---

## 4.2. Testy charakteryzujące

- **Test charakteryzujący** zapisuje, jak kod działa **obecnie** - wykrywa niezamierzone zmiany, nie potwierdza wymagań.
- Zapisany wynik może zawierać defekt - test wymusza świadomą decyzję przed jego korektą.
- Przebieg: wąska granica → deterministyczne wykonanie (czas, losowość, dane) → zapis wyniku → weryfikacja oczekiwań → przypadki graniczne.
- Przed transformacją wprowadź **kontrolowaną mutację** i sprawdź, czy test ją wykrywa.
- Oddziel stabilny kontrakt od szumu: znaczniki czasu, ID techniczne, kolejność niezależnych rekordów.

---

## 4.3. Test charakteryzujący `LegacyInvoiceFormatter`

```java
@Test
void documentsCurrentFormattingAndRounding() {
    List<InvoiceLine> lines = List.of(
            new InvoiceLine("BOOK", 2, new BigDecimal("19.99")),
            new InvoiceLine("PEN", 1, new BigDecimal("5.00")));

    String result = formatter.format("  Acme  ", lines);

    assertEquals(String.join("\n", "INVOICE", "Customer: ACME",
            "BOOK x 2 = 39.98", "PEN x 1 = 5.00", "Subtotal: 44.98",
            "Tax: 10.35", "Total: 55.33", ""), result);
}
```

- Formater łączy normalizację klienta, obliczenia, zaokrąglenia i budowę tekstu w jednej metodzie.
- Test utrwala cały tekst: `trim`, wielkie litery, podatek 23%, zaokrąglenie, końcowy `\n`.
- Nazwa `documents...` sygnalizuje zastane zachowanie, a nie wymaganie.

---

## 4.4. Dobór przypadków

- Granice warunków i wartości tuż po obu stronach; wartości puste, zerowe, ujemne.
- Zaokrąglenia, strefy czasowe, przejścia dat.
- Rozgałęzienia z efektami zewnętrznymi i formaty konsumowane na zewnątrz.
- Dane z produkcji i przypadki z historii awarii.

Nie utrwalaj przypadkowej implementacji: kolejność nieistotna → porównuj zbiory; ID losowe → sprawdź format.

---

## 4.5. Golden master, snapshot i approval testing

- **Golden master** - duży zapisany wynik jako historyczna wyrocznia.
- **Snapshot** - wynik porównywany w kolejnych wykonaniach; **approval testing** - świadome zatwierdzanie różnicy.
- Działa, gdy format jest stabilny, różnica czytelna, a niedeterminizm znormalizowany.
- Aktualizacja pliku oczekiwanego bez czytania różnicy **usuwa zabezpieczenie**.
- CineLegacy: `CinemaManagerGoldenMasterTest` odtwarza „jeden dzień kina” i porównuje wyniki, maile, płatności i raporty z `cinema-manager.approved.txt`.

---

## 4.6. Wektor obserwowalnego zachowania

| Obszar | Co sprawdzić |
| --- | --- |
| Wynik | wartość zwracana i tożsamość obiektu |
| Wyjątki | typ, komunikat, moment zgłoszenia |
| Stan | stan odbiorcy po sukcesie i po błędzie |
| Współpracownicy | kolejność i liczba wywołań, short-circuit warunków |
| Czas i aliasy | moment pobrania zależności/zegara, aliasowanie mutowalnych obiektów |
| Granice | transakcje, blokady, wątki, API, dane trwałe, serializacja |

Granica kontraktu ma być **świadomą decyzją**, nie skutkiem zbyt słabego testu.

---

## 4.7. Refaktoryzacja pod ochroną testów

```java
public String format(String customer, List<InvoiceLine> lines) {
    BigDecimal subtotal = calculateSubtotal(lines);
    BigDecimal tax = money(subtotal.multiply(TAX_RATE));
    BigDecimal total = money(subtotal.add(tax));
    StringBuilder result = new StringBuilder("INVOICE\n")
            .append("Customer: ").append(displayedCustomer(customer))
            .append('\n');
    appendLines(result, lines);
    return result
            .append("Subtotal: ").append(money(subtotal)).append('\n')
            .append("Tax: ").append(tax).append('\n')
            .append("Total: ").append(total).append('\n')
            .toString();
}
```

Sekwencja: `displayedCustomer` → `lineTotal` → `money` → `calculateSubtotal` → `appendLines` → `TAX_RATE`, **testy po każdym kroku**. Refaktoryzacja nie upoważnia do dodania walidacji czy walut.

---

## 4.8. Porównanie implementacji (test różnicowy)

```java
@ParameterizedTest
@MethodSource("representativeInvoices")
void refactoringPreservesObservedOutput(String customer, List<InvoiceLine> lines) {
    assertEquals(legacy.format(customer, lines),
                 refactored.format(customer, lines));
}
```

- Gdy stara wersja jest deterministyczna, przez ograniczony czas uruchamiamy obie na tych samych danych.
- Potwierdza zgodność tylko dla wykonanych danych i nie wykryje defektu obecnego w obu wersjach.
- Zachowaj też testy ze stałymi, przejrzanymi oczekiwaniami.

---

## 4.9. Jawna zmiana zachowania

Właściciel produktu: brak klienta ma powodować wyjątek zamiast `UNKNOWN`. To **tryb funkcjonalny**:

1. Nowe wymaganie w osobnym teście - zobacz porażkę.
2. Zaimplementuj regułę.
3. Świadomie zmień sprzeczne oczekiwanie charakteryzujące.
4. Uruchom regresję i opisz zmianę kontraktu.

To nie jest refaktoryzacja - wynik został **celowo zmieniony**.

---

## 4.10. Seam, punkt aktywacji i cel rozrywania

- **Seam (szew)** - miejsce zmiany zachowania bez edycji kodu w tym miejscu; **punkt aktywacji** - miejsce wyboru wariantu.
- Sam interfejs nie jest szwem, jeśli metoda na stałe tworzy konkretną implementację.
- W Javie dominują **object seams**; `LocalDate.now(...)` nie daje szwu obiektowego.
- Rozrywamy zależności dla **separacji** (trudna, wolna zależność) lub **obserwacji** (niewidoczny efekt, np. `System.out`).
- Globalny zegar: kod działa, ale test nie kontroluje wejścia - brak determinizmu.

---

## 4.11. Punkt wyjścia: `LegacyReminderService`

```java
public boolean sendRenewalReminder(Subscription subscription) {
    LocalDate today = LocalDate.now(ZoneOffset.UTC);
    if (subscription.renewalDate().isAfter(today.plusDays(7))) {
        return false;
    }
    System.out.printf("Sent renewal reminder to %s for %s%n",
            subscription.email(), subscription.renewalDate());
    return true;
}
```

- Tylko górna granica: wysyłka dla 7 dni, dziś i dat **przeszłych**; 8 dni - brak.
- Brak dolnej granicy może być defektem, ale jego korekta to nie refaktoryzacja.
- Czas globalny i wysyłka sprzężona z decyzją biznesową.

---

## 4.12. Minimalny szew przejściowy

```java
public class SeamedReminderService {
    public boolean sendRenewalReminder(Subscription subscription) {
        LocalDate today = currentDate();
        if (subscription.renewalDate().isAfter(today.plusDays(7))) {
            return false;
        }
        sendMessage(subscription.email(), subscription.renewalDate());
        return true;
    }
    protected LocalDate currentDate() { return LocalDate.now(ZoneOffset.UTC); }
    protected void sendMessage(String email, LocalDate renewalDate) { ... }
}
```

- **Subclass and Override Method**: `TestableReminderService` w teście nadpisuje czas i wysyłkę.
- **Koszt:** klasa rozszerzalna z metodami `protected` zmienia kontrakt API - szew oznacz jako **przejściowy**.

---

## 4.13. Jawne zależności jako rozwiązanie docelowe

```java
public final class ReminderService {
    private final Clock clock;
    private final ReminderGateway reminderGateway;
    // konstruktor z Objects.requireNonNull(...)
    public boolean sendRenewalReminder(Subscription subscription) {
        LocalDate today = LocalDate.now(clock);
        if (subscription.renewalDate().isAfter(today.plusDays(7))) {
            return false;
        }
        reminderGateway.send(subscription.email(), subscription.renewalDate());
        return true;
    }
    public interface ReminderGateway { void send(String email, LocalDate renewalDate); }
}
```

- `Clock` kontroluje czas, `ReminderGateway` nazywa efekt; test używa `Clock.fixed` i spy.
- Techniki Feathersa: **Parameterize Constructor** i **Extract Interface**.

---

## 4.14. Break Dependencies - przed i po

```java
// przed: serwis sam tworzy implementację kalendarza
private final StandardMaintenanceWindows maintenanceWindows =
        new StandardMaintenanceWindows();

// po: najwęższy kontrakt potrzebny klientowi
@FunctionalInterface
public interface MaintenanceWindows {
    boolean allows(String service, int hourUtc);
}
public DeploymentWindowService(MaintenanceWindows maintenanceWindows) {
    this.maintenanceWindows = Objects.requireNonNull(
            maintenanceWindows, "maintenanceWindows");
}
```

- Najwęższy skuteczny seam: parametr, zależność konstruktora, `Clock`, funkcja - nie interfejs przed każdą klasą.
- **Ryzyko:** zmiana czasu życia, współdzielenia, inicjalizacji, blokady lub transakcji zależności.

---

## 4.15. Kolejność rozrywania zależności i czego unikać

1. Nazwij zachowanie, punkt uruchomienia i obserwacji.
2. Wprowadź najmniejszy szew bez zmiany logiki i zabezpiecz testami.
3. Przenieś wybór zależności do jawnego punktu aktywacji.
4. Dodaj test integracyjny rzeczywistego adaptera, jeśli niesie ryzyko.
5. Usuń konstrukcję przejściową.

**Unikaj:** zmiany widoczności tylko dla testu, interfejsu dla każdej klasy, globalnego rejestru usług, wielu warstw abstrakcji przed pierwszym testem.

---

## 4.16. Bezpieczne ruchy automatami IDE, gdy brak testów

| Ruch o niskim ryzyku | Przestaje być bezpieczny, gdy |
| --- | --- |
| Rename lokalnej zmiennej, pola, prywatnej metody | nazwa żyje w refleksji, konfiguracji, serializacji |
| Extract Variable, Extract Method | wyrażenie ma efekt uboczny lub zmienia się moment ewaluacji |
| Inline Variable lokalnej | wyrażenie nie jest czyste |
| Extract Interface, Introduce Parameter z delegacją | zmienia się czas życia lub moment tworzenia zależności |
| Safe Delete | użycie jest tekstowe lub z zewnętrznego repozytorium |

- Jeden ruch, kompilacja, diff, commit; **bez ręcznych poprawek**. *Preserve Signatures*, *Lean on the Compiler*.
- Cel: dojść do pierwszego testu, a nie zrefaktoryzować całość (wsparcie IDE: moduł 5).

---

## 4.17. Scratch refactoring - refaktoryzacja na brudno

- Cel: **zrozumienie** kodu, a nie zmiana. Ograniczony czas (30-60 min), osobna gałąź.
- Agresywnie: Rename, Extract Method, przenoszenie, usuwanie martwego kodu - bez testów i bez dbałości o kompletność.
- Notuj poza kodem: reguły, ukryte wejścia (czas, konfiguracja), efekty, punkty zmiany, kandydatów na szwy, przypadki do testu charakteryzującego.
- Na koniec **wyrzuć kod**:

```bash
git switch -c scratch/reminder-job      # praca na brudno
git reset --hard && git clean -fd       # koniec: wyrzuć zmiany
git switch - && git branch -D scratch/reminder-job
```

Produkt to notatki i plan pierwszych bezpiecznych ruchów. „Skoro działa, to scalę” - kod bez dowodu.

---

## 4.18. Równoległa weryfikacja (parallel run)

```java
private PriceQuote verify(PriceRequest request) {
    PriceQuote legacyQuote = requireQuote(legacy.quote(request), "legacy quote");
    try {
        PriceQuote candidateQuote = requireQuote(candidate.quote(request), "candidate quote");
        tryToReport(legacyQuote.equals(candidateQuote)
                ? new VerificationEvent.Agreement(request, legacyQuote)
                : new VerificationEvent.Divergence(request, legacyQuote, candidateQuote));
    } catch (RuntimeException failure) {
        tryToReport(new VerificationEvent.CandidateFailure(request, legacyQuote,
                failure.getClass().getName(), Objects.toString(failure.getMessage(), "")));
    }
    return legacyQuote;   // wynik legacy pozostaje autorytatywny
}
```

- Produkcyjny ruch jako wyrocznia, gdy reguł nie da się w pełni spisać; `MigrationMode { LEGACY, VERIFY, CANDIDATE }`.
- Wspólny `PricingEngineContractTest` z **niezależnymi** oczekiwaniami dla obu implementacji.

---

## 4.19. Granice trybu shadow

- Synchroniczny kandydat może zwiększyć latencję - produkcyjny shadow wymaga izolacji, timeoutu, bulkheadu i limitu ruchu.
- Podwójne wykonanie tylko dla **czystych obliczeń**; nie powielaj płatności, wysyłek, zapisów ani zdarzeń.
- Efekty kandydata zamień w dane (plan, nagrane zamiary) i porównuj zamiast wykonywać.
- Zgodność dwóch implementacji nie dowodzi poprawności - obie mogą mieć ten sam błąd.
- Zgodność odpowiedzi API to nie dowód zgodności danych i transakcji.

---

## Aktywność 4.20: pierwsza sieć bez testów (4 min)

`ShowtimeReminderJob` sam otwiera bazę, czyta `LocalDateTime.now()` i woła statyczny mailer. Testów brak, za tydzień dochodzi przypomnienie SMS.

1. Co można zaobserwować dziś bez zmiany kodu?
2. Który ruch IDE otwiera pierwszy szew i czego nie wolno przy nim zmienić?
3. Co zapiszesz po godzinie scratch refactoringu?
4. Dlaczego równoległa weryfikacja tu nie pomoże?

---

## Ćwiczenie 1: charakterystyka i refaktoryzacja formatera (25 + 10 min)

- Cel: scharakteryzować `LegacyInvoiceFormatter` i zrefaktoryzować go małymi krokami.
- Uzupełnij macierz przypadków (pusta lista, `null`, zaokrąglenia), sprawdź czułość kontrolną mutacją.
- **Nie zmieniaj** stawki, zaokrąglania ani tekstu; nie dodawaj walidacji.

Szczegóły: zadania modułu 3, Ćwiczenie 1.

---

## Ćwiczenie 2: obiekty zastępcze i pokrycie (15 + 5 min)

- **A:** w `OrderPlacementServiceTest` nazwij rolę każdego współpracownika według katalogu, rodzaj weryfikacji oraz co test wykryje, a czego nie.
- **B:** w raporcie JaCoCo dla `DeliveryFee` wyjaśnij linie vs gałęzie i dopisz test wykrywający `100` → `200`.

Szczegóły: zadania modułu 3, Ćwiczenie 2.

---

## Ćwiczenie 3: wprowadzenie szwu (20 + 10 min)

- Cel: w `LegacyReminderService` najpierw minimalny szew, potem jawne `Clock` i `ReminderGateway`.
- Testy deterministyczne: 7 dni, 8 dni, data przeszła.
- Test nie zależy od bieżącej daty; kod domenowy nie tworzy adaptera.

Szczegóły: zadania modułu 3, Ćwiczenie 3.

---

## Ćwiczenie 4: równoległa weryfikacja i jej granice (10 + 5 min)

- Cel: zobaczyć rozbieżność w trybie `VERIFY` bez zmiany odpowiedzi i ocenić, gdzie shadow jest bezpieczny.
- Kontrolna mutacja w `CandidatePricingEngine`: który test ją wykrywa - kontraktowy czy router?
- Klasyfikacja operacji: wycena, obciążenie karty, mail, numer rezerwacji.

Szczegóły: zadania modułu 3, Ćwiczenie 4.

---

## Kluczowe wnioski z omówienia

- Kolejność zaokrągleń w formaterze jest decyzją domenową - test charakteryzujący może utrwalić defekt.
- Test różnicowy formatera przepuszcza mutację „sumuj wartości zaokrąglone” - potrzebny przypadek z dwiema pozycjami `0.005`.
- Fake repozytorium nie zastąpi wąskiego testu integracyjnego adaptera.
- Test bez asercji podnosi pokrycie, ale nie wykryje zmiany `100` → `200`.
- Odrzucanie dat przeszłych to **osobna zmiana funkcjonalna**, nie część wprowadzania szwu.
- Shadow jest bezpieczny dla wyceny, nie dla płatności i wysyłek.

Pełne omówienie: rozwiązania modułu 3.

---

## Sprawdzenie wiedzy

1. Co naprawdę oznacza zielony zestaw testów?
2. Dlaczego piramida testów nie narzuca proporcji?
3. Czym stub różni się od mocka, a spy od fake?
4. Kiedy weryfikacja zachowania jest uzasadniona?
5. Co pokazuje pokrycie gałęzi, czego nie pokazuje pokrycie linii?
6. Dlaczego test charakteryzujący nie dowodzi wymagania?
7. Czym różni się seam od punktu aktywacji?
8. Co jest produktem scratch refactoringu?
9. Kiedy tryb shadow jest niebezpieczny?

---

## Lista kontrolna

- **Przed:** nazwany punkt zmiany i punkt testowania; wektor zachowania; test charakteryzujący lub golden master ryzykownego zachowania sprawdzony mutacją; zielona baza; punkt przywracania.
- **Bez testów:** tylko ruchy IDE o niskim ryzyku, jeden ruch na commit; scratch refactoring wyrzucony; szew przejściowy oznaczony.
- **Obiekty zastępcze:** rola nazwana według użycia; interakcja weryfikowana tylko jako kontrakt; cudze API za własnym portem; fake z testem kontraktu.
- **Na koniec:** pokrycie obszaru zmiany przejrzane (gałęzie, siła asercji); zmiany funkcjonalne oddzielone; elementy przejściowe usunięte lub z kryterium usunięcia.

---

## Podsumowanie - najważniejsze wnioski

- Testy zwiększają poziom dowodu, ale nie dają pewności; piramida to heurystyka, nie proporcja.
- Dummy, stub, spy, mock i fake to **role w konkretnym teście**; domyślnie weryfikuj stan, interakcję tylko jako kontrakt.
- Pokrycie wskazuje niewykonany kod, ale nie ocenia siły asercji.
- Bez testów zaczynamy od obserwacji: test charakteryzujący, golden master, wektor zachowania.
- Szew ma być **minimalnym krokiem** do kontroli i obserwacji; automaty IDE i scratch refactoring pomagają dojść do pierwszego testu.
- Równoległa weryfikacja daje dowód z produkcji tylko dla czystych obliczeń.
