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
# Moduł 2. Fundamenty refaktoryzacji

Jak zmieniać strukturę kodu legacy bezpiecznie: kontrakt, małe kroki, testy i szwy

---

## Agenda

1. Istota i cele refaktoryzacji
2. Zasady bezpiecznej refaktoryzacji
3. Znaczenie testów w procesie refaktoryzacji
4. Piramida testów i strategie testowania
5. Obiekty zastępcze: stub, spy, fake i mock
6. Pokrycie kodu: zalety, ograniczenia i nadużycia
7. Testy charakteryzujące i refaktoryzacja pod ochroną testów
8. Seams i rozrywanie zależności
9. Warsztat praktyczny, sprawdzenie wiedzy, lista kontrolna

---

## 1.1. Precyzyjna definicja refaktoryzacji

- **Refaktoryzacja** to kontrolowana zmiana wewnętrznej struktury **bez zmiany obserwowalnego zachowania**.
- Dwa znaczenia: pojedyncza transformacja oraz proces serii takich transformacji.
- Małe kroki nie oznaczają małego celu - duża zmiana projektu powstaje z wielu lokalnych kroków.
- Po każdym kroku kod się buduje, testy są zielone, a różnicę łatwo wycofać.

---

## 1.2. Co oznacza zachowanie obserwowalne

| Rodzaj | Przykład |
| --- | --- |
| wynik | zwrócona kwota, wygenerowany dokument |
| błąd | typ wyjątku, kod odpowiedzi |
| efekt uboczny | zapis rekordu, publikacja zdarzenia |
| protokół / dane | kolejność wywołań (gdy istotna), format pliku, zaokrąglenie |
| kontrakt publiczny | sygnatura API, kompatybilność binarna |
| pozafunkcjonalne | czas, pamięć - jeśli są wymaganiem |

Prywatne metody i liczba klas zwykle **nie są kontraktem** - chyba że zależy od nich refleksja, serializacja lub zewnętrzny konsument. Nazwij, **jakie obserwacje** mają pozostać niezmienione.

---

## 1.3. Refaktoryzacja a inne rodzaje zmian

| Zmiana | Refaktoryzacja? |
| --- | --- |
| zmiana nazwy prywatnej metody | zwykle tak |
| wydzielenie obliczenia do metody | tak |
| korekta błędnego zaokrąglania | nie - celowo zmienia wynik |
| dodanie pamięci podręcznej | zwykle optymalizacja |
| pełne przepisanie klasy | nie - nowa implementacja |

Klasyfikację rozstrzyga **kontrakt i konsumenci**, nie nazwa operacji w IDE: zmiana publicznej nazwy w opublikowanej bibliotece może być już migracją API.

---

## 1.4-1.5. Cele i moment refaktoryzacji

- Cel: obniżyć **koszt lub ryzyko dalszej pracy** - miejsce pod funkcję, punkt testowania, mniejsze sprzężenie.
- Nie są celami samymi w sobie: więcej klas i interfejsów, wzorzec dla wzorca, procent pokrycia.
- Najłatwiej uzasadnić inwestycję w obszar, który wkrótce będzie zmieniany.
- Kiedy: **przygotowawczo**, dla **zrozumienia**, **oportunistycznie**, **planowo** małymi seriami.
- Osobny projekt refaktoryzacyjny to wyjątek, nie domyślna odpowiedź.

---

## Aktywność 1.6: nazwij kontrakt (4 min)

Wskaż zachowanie, które może być obserwowalne:

1. zmiana typu prywatnego pola z `List` na `Set`,
2. zamiana dwóch zapisów do bazy miejscami,
3. przeniesienie klasy używanej przez refleksję,
4. zastąpienie algorytmu szybszą implementacją,
5. zmiana prywatnej nazwy bez refleksji i konfiguracji tekstowej.

**Zasada:** najpierw nazwij konsumenta, obserwację i sposób weryfikacji.

---

## 2.1-2.2. Znany stan i dwa tryby pracy

- Start: powtarzalny build, zielone testy (lub jawnie odizolowane awarie), nazwany zakres, punkt powrotu.
- Stale czerwony test nie wykryje nowej regresji - napraw go albo odizoluj z uzasadnieniem.
- **Tryb strukturalny** zachowuje obserwacje, **tryb funkcjonalny** celowo je zmienia - „dwa kapelusze” Fowlera.
- Przebieg: uprość drogę (zielone) → test nowego wymagania (czerwony) → implementacja → porządki.
- Podejrzany wynik testu charakteryzującego: najpierw rozstrzygnij, czy to defekt, czy kontrakt.

---

## 2.3. Pętla małych kroków

1. Wykonaj najmniejszą transformację (IDE lub mała zmiana ręczna).
2. Skompiluj i uruchom najszybsze testy zmienionego zachowania.
3. Obejrzyj różnicę, zapisz punkt przywracania.
4. Powtarzaj; regularnie szerszy zestaw, na końcu pełna weryfikacja.

**Dlaczego:** przy porażce głównym podejrzanym jest ostatnia, mała transformacja.

---

## 2.4. Dobierz weryfikację do ryzyka

| Zmieniany element | Przydatna weryfikacja |
| --- | --- |
| czyste obliczenie | szybkie testy przykładów i granic |
| mapowanie danych | test adaptera z rzeczywistym mapowaniem |
| format komunikatu | test kontraktu lub serializacji |
| kolejność skutków | test interakcji i transakcji |
| publiczne API | testy konsumenta i kompatybilności |

Kompilacja nie sprawdzi semantyki, a test jednostkowy - mapowania ORM.

---

## 2.5-2.7. Szum, narzędzia i moment przerwania

- Nie mieszaj refaktoryzacji z formatowaniem, aktualizacją zależności i nową funkcją.
- IDE nie widzi konfiguracji, refleksji, szablonów, serializacji ani zewnętrznych konsumentów - **nie jest dowodem** zachowania.
- Przerwij i wróć do znanego stanu, gdy: nie wiadomo, czy porażka to regresja, diff rośnie szybciej niż zrozumienie, testy są niestabilne, defekt wymaga decyzji biznesowej.

**Przerwanie kroku to mechanizm kontroli ryzyka, nie porażka.**

---

## 3.1. Test dostarcza dowodu, nie pewności

- Zielony zestaw znaczy tylko: **żaden test nie wykrył różnicy** w tym, co sprawdził.
- Nie gwarantuje pełnych wymagań, wszystkich ścieżek, danych granicznych ani czułych asercji.
- Bezpieczeństwo rośnie przez połączenie: małe kroki, przegląd diffu, kompilator, analiza statyczna, testy integracyjne, obserwacja produkcji.

---

## 3.2. Testuj stabilne zachowanie

- Test przywiązany do prywatnych metod i liczby klas może **blokować poprawną refaktoryzację**.
- Obserwuj najwęższą stabilną granicę, która wykrywa ważną zmianę.
- Weryfikacja stanu i wyniku jest zwykle odporniejsza niż weryfikacja interakcji.
- Interakcję weryfikuj, gdy jest kontraktem: jedno obciążenie płatności, publikacja po zatwierdzeniu transakcji, idempotencja.

---

## 3.3. Cechy użytecznej sieci bezpieczeństwa

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

## 3.4. Punkt zmiany i punkt testowania

Miejsce modyfikacji nie musi być najlepszym miejscem obserwacji. Na początku często zabezpieczamy szerszy przebieg, a precyzyjne testy dodajemy po poprawie struktury.

Przed pracą odpowiedz:

1. Gdzie kod zostanie zmieniony?
2. Skąd uruchomić zachowanie i gdzie widać jego efekt?
3. Które zależności blokują kontrolowane wykonanie?
4. Jaki najmniejszy test wykryje niezamierzoną zmianę?

---

## 4.1-4.2. Piramida testów to heurystyka

- Małych, szybkich testów zwykle jest dużo, szerokich i kosztownych - mniej.
- Nie wynika z niej proporcja (np. 70/20/10) ani obowiązek mockowania wszystkiego.
- Szybki, deterministyczny test szeroki może być bardzo wartościowy.
- Etykiety („jednostkowy”, „integracyjny”) są niejednoznaczne - opisuj test **przez właściwości**: zakres, interfejs, realizm zależności, szybkość, determinizm, wykrywane ryzyko, koszt.

---

## 4.3. Typowy portfel testów

| Rodzaj | Najważniejsza wartość | Ograniczenie |
| --- | --- | --- |
| logika domenowa | szybka diagnoza reguł | brak integracji |
| komponentowy | współpraca klas przez granicę | uproszczone zależności |
| wąski integracyjny | adapter, mapowanie, konfiguracja | zarządzanie realną technologią |
| kontraktowy | zgodność producenta i konsumenta | nie cały przepływ |
| systemowy / E2E | najważniejsze ścieżki całości | koszt, niestabilność |

---

## 4.4-4.5. Kolejność uruchamiania i strategia dla legacy

- Kolejność: kompilacja i szybkie testy obszaru → moduł → integracja i kontrakty → system → krytyczne E2E → testy pozafunkcjonalne.
- **Etykieta nie powinna opóźniać wartościowej informacji** - szybki test adaptera może iść w pierwszym etapie.
- W legacy nie buduj od razu idealnej piramidy - zacznij od planowanej zmiany i jej ryzyka.
- Zacznij od stabilnej, nawet szerokiej granicy; szybsze testy dodawaj po wprowadzeniu szwów.
- Defekt z testu szerokiego odtwórz na najniższym poziomie, zachowując test szeroki, jeśli chroni integrację.

---

## Aktywność 4.6: zaprojektuj portfel (5 min)

System **nalicza opłatę**, **zapisuje wynik w PostgreSQL** i **publikuje komunikat**.
Ostatnie regresje: wartość graniczna, mapowanie `BigDecimal`, niezgodny schemat komunikatu.

**Zadanie:** zaproponuj minimalny portfel. Dla każdego testu: wykrywane ryzyko, rzeczywiste zależności, czas, miejsce w procesie.

---

## 5.1. Obiekty zastępcze (test doubles)

| Rola | Zadanie | Typowa weryfikacja |
| --- | --- | --- |
| stub | zaprogramowane odpowiedzi | wynik obiektu testowanego |
| spy | rejestruje sposób użycia | odczyt zapisanych wywołań |
| fake | uproszczona działająca implementacja | stan i kontrakt |
| mock | oczekiwania interakcji | jawna weryfikacja wywołań |
| dummy | wypełnia nieużywany parametr | zwykle brak |

Rolę wyznacza **sposób użycia w teście**, nie biblioteka. Mock nie jest synonimem każdego obiektu zastępczego.

---

## 5.2. Przykładowa usługa: `OrderPlacementService`

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

Czterech współpracowników przekazanych przez konstruktor.

---

## 5.3. Stub, fake i spy w jednym teście

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

## 5.4-5.5. Mock i ryzyka obiektów zastępczych

- **Mock** płatności sprawdza protokół: właściwy token, kwota i **dokładnie jedno** wywołanie - uzasadnione, bo liczba obciążeń jest kontraktem.
- **Fake** może różnić się od adaptera produkcyjnego - uruchom wspólny test kontraktu dla obu.
- Baza w pamięci nie sprawdza zapytań, ograniczeń, transakcji ani mapowania typów.
- Rozbudowane mocki powielają implementację i utrudniają refaktoryzację.
- `spy` w bibliotece bywa częściowym mockiem - to nie klasyczna rola spy.

---

## 6.1. Co mierzy JaCoCo

- Analizuje **kod bajtowy**, nie bezpośrednio źródła.
- Raportuje pokrycie instrukcji, gałęzi, linii, metod i klas oraz złożoność cyklomatyczną.
- Obsługa wyjątków nie jest liczona jako gałąź; kod syntetyczny kompilatora bywa nieoczywisty.

---

## 6.2. Pełne linie i brakująca gałąź

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

## 6.3-6.4. Pokrycie: pytania, progi, nadużycia

- **Odpowiada:** czego testy nie wykonały, czy weszły w obszar zmiany, czy warunek ma niewykonaną stronę.
- **Nie odpowiada:** czy asercja jest właściwa, dane reprezentatywne, a system wolny od defektów.
- Próg chroni przed pogarszaniem, ale **nie istnieje uniwersalna wartość**.
- Nadużycia: testy bez asercji dla raportu, jedna średnia dla systemu, 100% jako dowód poprawności.
- **Test mutacyjny** ujawnia kod wykonany, ale chroniony słabymi asercjami.

---

## Aktywność 6.5: raport bez celu (4 min)

Otwórz raport po `mvn clean verify` i znajdź `DeliveryFee`:

1. Która gałąź nie została wykonana?
2. Jaki test ją uruchomi?
3. Jaka asercja wykryje zmianę `100` na `200`?
4. Czy wyższe globalne pokrycie z innych klas zmniejszy to ryzyko?

---

## 7.1-7.2. Testy charakteryzujące

- **Test charakteryzujący** zapisuje, jak kod działa **obecnie** - wykrywa niezamierzone zmiany, nie potwierdza wymagań.
- Zapisany wynik może zawierać defekt - test wymusza świadomą decyzję przed jego korektą.
- Przebieg: wąska granica → deterministyczne wykonanie (czas, losowość, dane) → zapis wyniku → weryfikacja oczekiwań → przypadki graniczne.
- Przed transformacją wprowadź **kontrolowaną mutację** i sprawdź, czy test ją wykrywa.
- Oddziel stabilny kontrakt od szumu: znaczniki czasu, ID techniczne, kolejność niezależnych rekordów.

---

## 7.3. Test charakteryzujący `LegacyInvoiceFormatter`

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

## 7.4. Dobór przypadków

- Granice warunków i wartości tuż po obu stronach; wartości puste, zerowe, ujemne.
- Zaokrąglenia, strefy czasowe, przejścia dat.
- Rozgałęzienia z efektami zewnętrznymi i formaty konsumowane na zewnątrz.
- Dane z produkcji i przypadki z historii awarii.

Nie utrwalaj przypadkowej implementacji: kolejność nieistotna → porównuj zbiory; ID losowe → sprawdź format.

---

## 7.5. Golden master, snapshot i approval testing

- **Golden master** - duży zapisany wynik jako historyczna wyrocznia.
- **Snapshot** - wynik porównywany w kolejnych wykonaniach; **approval testing** - świadome zatwierdzanie różnicy.
- Działa, gdy format jest stabilny, różnica czytelna, a niedeterminizm znormalizowany.
- Aktualizacja pliku oczekiwanego bez czytania różnicy **usuwa zabezpieczenie**.

---

## 7.6. Refaktoryzacja pod ochroną testów

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

## 7.7. Porównanie implementacji (test różnicowy)

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

## 7.8. Jawna zmiana zachowania

Właściciel produktu: brak klienta ma powodować wyjątek zamiast `UNKNOWN`. To **tryb funkcjonalny**:

1. Nowe wymaganie w osobnym teście - zobacz porażkę.
2. Zaimplementuj regułę.
3. Świadomie zmień sprzeczne oczekiwanie charakteryzujące.
4. Uruchom regresję i opisz zmianę kontraktu.

To nie jest refaktoryzacja - wynik został **celowo zmieniony**.

---

## 8.1-8.2. Seam, punkt aktywacji i cel rozrywania

- **Seam (szew)** - miejsce zmiany zachowania bez edycji kodu w tym miejscu; **punkt aktywacji** - miejsce wyboru wariantu.
- Sam interfejs nie jest szwem, jeśli metoda na stałe tworzy konkretną implementację.
- W Javie dominują **object seams**; `LocalDate.now(...)` nie daje szwu obiektowego.
- Rozrywamy zależności dla **separacji** (trudna, wolna zależność) lub **obserwacji** (niewidoczny efekt, np. `System.out`).
- Globalny zegar: kod działa, ale test nie kontroluje wejścia - brak determinizmu.

---

## 8.3. Punkt wyjścia: `LegacyReminderService`

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

## 8.4. Minimalny szew przejściowy (1/2)

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
    protected void sendMessage(String email, LocalDate renewalDate) {
        System.out.printf("Sent renewal reminder to %s for %s%n", email, renewalDate);
    }
}
```

**Subclass and Override Method**: w teście `TestableReminderService` nadpisuje czas i wysyłkę.

---

## 8.4. Minimalny szew przejściowy (2/2): zalety i koszty

- Testy deterministyczne: 7 dni → wysyłka, 8 dni → brak, data przeszła → wysyłka.
- **Zalety:** mała zmiana sygnatur, szybka kontrola czasu i efektu przed większą przebudową.
- **Koszty:** klasa rozszerzalna z metodami `protected` zmienia kontrakt API; test wiąże się z dziedziczeniem.
- W publicznej bibliotece wymaga analizy kompatybilności.

Szew ma **odblokować test i następny krok** - oznacz go jako przejściowy.

---

## 8.5. Jawne zależności jako rozwiązanie docelowe

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

## 8.6-8.7. Kolejność rozrywania zależności i czego unikać

1. Nazwij zachowanie, punkt uruchomienia i obserwacji.
2. Wprowadź najmniejszy szew bez zmiany logiki i zabezpiecz testami.
3. Przenieś wybór zależności do jawnego punktu aktywacji.
4. Dodaj test integracyjny rzeczywistego adaptera, jeśli niesie ryzyko.
5. Usuń konstrukcję przejściową.

**Unikaj:** zmiany widoczności tylko dla testu, interfejsu dla każdej klasy, globalnego rejestru usług, wielu warstw abstrakcji przed pierwszym testem.

---

## Ćwiczenie 1: granica refaktoryzacji (15 + 5 min)

Czy to refaktoryzacja? Jaka obserwacja, kto ją konsumuje, jaki dowód przed i po?

- **A.** Zmiana nazwy prywatnej metody `calc` na `calculateTax`.
- **B.** `double` → `BigDecimal` z powodu błędów zaokrągleń.
- **C.** Przeniesienie publicznej klasy DTO do innego pakietu.
- **D.** Wydzielenie budowy komunikatu bez zmiany tekstu.
- **E.** Pamięć podręczna dla kursu walut.

Szczegóły: zadania modułu 2, Ćwiczenie 1.

---

## Ćwiczenie 2: charakterystyka i refaktoryzacja formatera (35 + 10 min)

- Cel: scharakteryzować `LegacyInvoiceFormatter` i zrefaktoryzować go małymi krokami.
- Uzupełnij macierz przypadków (pusta lista, `null`, zaokrąglenia) i testuj po każdym kroku.
- **Nie zmieniaj** stawki, zaokrąglania ani tekstu; nie dodawaj walidacji.

Szczegóły: zadania modułu 2, Ćwiczenie 2.

---

## Ćwiczenie 3: strategia testów, obiekty zastępcze i pokrycie (25 + 10 min)

- **A:** w `OrderPlacementServiceTest` określ rolę każdego współpracownika oraz co test wykryje, a czego nie.
- **B:** w raporcie JaCoCo dla `DeliveryFee` wyjaśnij linie vs gałęzie i zaproponuj test wykrywający `100` → `200`.

Szczegóły: zadania modułu 2, Ćwiczenie 3.

---

## Ćwiczenie 4: wprowadzenie szwu (25 + 10 min)

- Cel: w `LegacyReminderService` najpierw minimalny szew, potem jawne `Clock` i `ReminderGateway`.
- Testy deterministyczne: 7 dni, 8 dni, data przeszła.
- Test nie zależy od bieżącej daty; kod domenowy nie tworzy adaptera.

Szczegóły: zadania modułu 2, Ćwiczenie 4.

---

## Kluczowe wnioski z omówienia

- Etykieta operacji nie rozstrzyga jej charakteru - decydują cel i zachowane obserwacje.
- Kolejność zaokrągleń w formaterze jest decyzją domenową - test charakteryzujący może utrwalić defekt.
- Fake repozytorium nie zastąpi wąskiego testu integracyjnego adaptera.
- Test bez asercji podnosi pokrycie, ale nie wykryje zmiany `100` → `200`.
- Odrzucanie dat przeszłych to **osobna zmiana funkcjonalna**, nie część wprowadzania szwu.

Pełne omówienie: rozwiązania modułu 2.

---

## Sprawdzenie wiedzy

1. Czym refaktoryzacja różni się od zmiany funkcjonalnej?
2. Czy zachowanie obserwowalne to tylko wartość zwracana?
3. Co naprawdę oznacza zielony zestaw testów?
4. Czym stub różni się od mocka?
5. Co pokazuje pokrycie gałęzi, czego nie pokazuje pokrycie linii?
6. Dlaczego test charakteryzujący nie dowodzi wymagania?
7. Czym różni się seam od punktu aktywacji?

---

## Lista kontrolna

- **Przed:** nazwany cel, granica, obserwacje i konsumenci; zielona baza; test charakteryzujący ryzykownego zachowania; punkt przywracania.
- **Podczas:** jedna intencja na krok; wiadomo, czy tryb jest strukturalny, czy funkcjonalny; szybkie testy po każdym kroku; diff bez szumu.
- **Na koniec:** pełny zestaw i kontrakty integracyjne sprawdzone; zmiany funkcjonalne oddzielone i opisane; rozwiązania przejściowe usunięte lub oznaczone.

---

## Podsumowanie - najważniejsze wnioski

- Refaktoryzacja zmienia strukturę, **zachowując nazwane obserwacje**; poprawki i nowe funkcje to osobne kroki.
- Testy zwiększają poziom dowodu, ale nie dają pewności; piramida to heurystyka, nie proporcja.
- Stub, spy, fake i mock to **role w konkretnym teście**.
- Pokrycie wskazuje niewykonany kod, ale nie ocenia siły asercji.
- Test charakteryzujący zapisuje zastane zachowanie, które wymaga decyzji domenowej.
- Szew ma być **minimalnym krokiem** do kontroli i obserwacji; rozwiązanie przejściowe wymaga decyzji o losie.
