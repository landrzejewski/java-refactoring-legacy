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
# Moduł 3. Zasady dobrego projektowania
DRY, KISS, YAGNI, SOLID, spójność i sprzężenie, Clean Architecture oraz wzorce jako narzędzia oceny kosztu zmiany

---

## Agenda

1. Jakość projektu w kontekście zmiany
2. DRY, KISS i YAGNI
3. Zasady SOLID w praktyce
4. Wysoka spójność i niskie sprzężenie
5. Clean Architecture i pokrewne podejścia inside/outside
6. Wzorce projektowe jako kierunek refaktoryzacji
7. Studium przypadku w Javie 25: wycena dostawy
8. Warsztat praktyczny, sprawdzenie wiedzy, lista kontrolna

---

## 1.1-1.2. Dobry projekt nie jest cechą absolutną

- Jakość projektu ujawnia się **podczas zmiany**: łatwo znaleźć miejsce, mały promień oddziaływania, szybka informacja zwrotna.
- Ocena zależy od kontekstu: domeny, rodzaju zmian, krytyczności błędów, zespołów, czasu życia rozwiązania.
- Projekt to także schematy danych, formaty komunikatów, transakcje, wdrożenia, publiczne API.
- Interfejs nie naprawi niejasnej transakcji, a podział klasy nie usunie sprzężenia przez wspólną tabelę.
- Zasada projektowa ma **postawić pytanie**, a nie zakończyć analizę.

---

## 1.3. Zasady są heurystykami

| Zasada | Pytanie diagnostyczne | Ryzyko nadinterpretacji |
| --- | --- | --- |
| DRY | Gdzie jest autorytatywna reprezentacja wiedzy? | łączenie reguł, bo wyglądają podobnie |
| KISS | Czy jest mniej złożone poprawne rozwiązanie? | pomijanie przypadków i błędów |
| YAGNI | Jakie aktualne wymaganie to uzasadnia? | zaniedbanie zdrowia kodu |
| SOLID | Jak podział ogranicza koszt konkretnej zmiany? | mechaniczne klasy i interfejsy |
| Clean Arch. | Czy polityka zależy od szczegółu technicznego? | kopiowanie szablonu warstw |
| wzorzec | Jaki problem i jakie siły równoważy? | katalog wzorców bez potrzeby |

Uzasadnij zmianę **scenariuszem**: co zmieniamy, ile to dziś kosztuje, jaki nowy koszt wnosi abstrakcja, jak zmierzymy efekt.

---

## 2.1. DRY dotyczy wiedzy

- **DRY**: jedna autorytatywna reprezentacja porcji **wiedzy**, nie zakaz podobnych linii.
- Wiedza: reguła cenowa, format komunikatu, ograniczenie walidacyjne, konfiguracja, kontrakt.
- Kluczowe pytanie: **czy zmiana jednej decyzji wymaga zgodnej aktualizacji kilku miejsc?**

---

## 2.2. Podobieństwo nie wystarcza

| Sytuacja | Ocena |
| --- | --- |
| walidacja wieku i liczby produktów: `value > 0` | podobny kod, niezależna wiedza |
| stawka opłaty paliwowej w dwóch wariantach | jedna reguła - kandydat do centralizacji |
| dwa konteksty mają dziś rabat 10% | może być przypadkowe; ustal relację reguł |
| cache powiela dane źródłowe | kontrolowana duplikacja z właścicielem |
| klient API i dokumentacja z jednego kontraktu | wiele artefaktów, jedno źródło wiedzy |

Wspólna metoda dla niezależnych reguł tworzy **fałszywą zależność**.

---

## 2.3. Fałszywa abstrakcja i DRY w testach

- Fałszywą abstrakcję zabezpiecz testami, przenieś kod z powrotem do kontekstów, potem wydziel tylko potwierdzoną wiedzę.
- Tymczasowe powtórzenie kodu bywa bezpiecznym etapem rozdzielania.
- Testy mogą współdzielić fabryki danych i przygotowanie środowiska.
- Test **nie powinien liczyć** oczekiwanej wartości algorytmem produkcyjnym - traci niezależną wyrocznię.

---

## 2.4-2.5. KISS: prostota po poprawności

- **KISS**: najmniej złożone rozwiązanie, które **poprawnie** realizuje aktualne wymagania.
- Prostota to nie najmniej linii czy klas ani pominięcie błędów, bezpieczeństwa, transakcji.
- Złożoność istotna (prawo, współbieżność, finanse) zostaje - celem jest ją **nazwać i izolować**.
- KISS celuje w złożoność **wprowadzoną**: pluginy bez rozszerzeń, silnik reguł dla dwóch przypadków, ukryty przepływ przez refleksję.

---

## 2.6-2.7. YAGNI: aktualna potrzeba przed przewidywaną

- **YAGNI**: nie buduj zdolności potrzebnej tylko dla przewidywanego wymagania.
- Przykłady: fabryka jednego typu, interfejs dla hipotetycznych implementacji, pluginy, nieużywane pola.
- Koszt: projekt i testy, opóźniona wartość, przebudowa, gdy potrzeba okaże się inna.
- **Nie zabrania:** refaktoryzacji, testów, CI, aktualnych wymagań bezpieczeństwa i wydajności.
- Działa, gdy system jest podatny na zmianę - testy i małe kroki pozwalają odraczać decyzje.

---

## 2.8. Napięcia między zasadami

| Decyzja | DRY | KISS | YAGNI |
| --- | --- | --- | --- |
| nazwanie potwierdzonej reguły opłaty | jedno źródło wiedzy | brak synchronizacji | aktualna reguła |
| ogólny silnik hipotetycznych taryf | brak korzyści | więcej pojęć | przewidywana elastyczność |
| podobne reguły niezależnych działów | brak wspólnej wiedzy | lokalność prostsza | brak spekulacji |

**Reguła trzech** pomaga poczekać na kształt abstrakcji, ale nie definiuje DRY: dwie kopie tej samej reguły mogą wymagać natychmiastowej centralizacji.

---

## 2.9. Filtr decyzyjny przed dodaniem abstrakcji

1. Jakie aktualne wymaganie lub ryzyko ją uzasadnia?
2. Jaką wiedzę reprezentują podobne miejsca i czy zmieniają się dla tego samego właściciela?
3. Czy abstrakcja ma nazwę w języku problemu?
4. Czy upraszcza aktualny przypadek, czy tylko przyszły?
5. Ile nowych trybów i zależności wnosi i jak łatwo ją usunąć?

---

## 3.1. SOLID jako narzędzie analizy zmian

| Zasada | Główna troska |
| --- | --- |
| SRP | grupowanie elementów zmieniających się dla tego samego aktora |
| OCP | ochrona stabilnej części przed wybraną osią rozszerzeń |
| LSP | zachowanie kontraktu przy podstawieniu implementacji |
| ISP | ograniczenie zależności klienta do potrzebnej roli |
| DIP | kierowanie zależności od szczegółów ku polityce |

Heurystyki, nie system punktowy ani nakaz maksymalizacji polimorfizmu.

---

## 3.2. SRP: jeden aktor zmiany

- „Jeden powód do zmiany” to odpowiedzialność wobec **aktora**, nie każda edycja pliku.
- Serwis oferty łączy cenę (finanse), zapis (dane) i komunikat (obsługa klienta) - różne przyczyny zmian.
- Przypadek użycia może koordynować, ale nie powinien przejmować szczegółów polityk.
- SRP to nie jedna metoda na klasę ani limit linii.

---

## 3.3. OCP: zamknięcie dla wybranej osi

- **OCP**: rozszerzenie **wybranego** zachowania bez modyfikacji stabilnej części.
- Nie da się zamknąć systemu na wszystkie zmiany; nowa implementacja może wymagać zmiany composition root.
- Nie każdy `switch` narusza OCP - może modelować mały, celowo zamknięty zbiór.
- Polimorfizm się opłaca, gdy zbiór rośnie niezależnie, a centralne rozgałęzienia wymuszają powtarzalne zmiany.

---

## 3.4. LSP: substytucja behawioralna

- LSP dotyczy **zachowania**, nie tylko sygnatur: wejście, wynik, inwarianty, wyjątki, efekty uboczne.
- Podtyp **nie wzmacnia warunków wstępnych** i **nie osłabia warunków końcowych**.
- Kompilator nie sprawdza kontraktów domenowych ani znaczenia wyniku.
- Test kontraktowy dla wszystkich implementacji daje **dowody**, nie formalny dowód substytucyjności.
- `UnsupportedOperationException` nie zawsze łamie LSP - ocena zaczyna się od kontraktu typu bazowego.

---

## 3.5-3.6. ISP i DIP

- **ISP**: interfejs według **ról klientów**; jednometodowy port jest dobry, gdy to jedna spójna rola.
- Typowe naruszenie: `CrudRepository<T>` przekazany przypadkowi użycia, który potrzebuje jednej operacji.
- **DIP**: zależności źródłowe kierowane od szczegółów ku polityce i abstrakcjom.
- **DIP ≠ dependency injection**: wstrzyknięcie konkretnej klasy wciąż wiąże ze szczegółem; DIP działa bez kontenera.
- Port nazywa potrzebę klienta: przypadek użycia definiuje `QuoteRepository`, adapter go implementuje.

---

## 3.7. Błędne uproszczenia SOLID

| Uproszczenie | Korekta |
| --- | --- |
| SRP: klasa robi jedną rzecz | odpowiedzialność wynika z aktora i powodu zmiany |
| każdy `switch` łamie OCP | zamknięty zbiór wariantów może być poprawnym modelem |
| dziedziczenie gwarantuje LSP | potrzebna jest zgodność behawioralna |
| ISP: jedna metoda na interfejs | interfejs odpowiada spójnej roli klienta |
| DIP: konstruktor lub `@Autowired` | chodzi o kierunek zależności źródłowej |
| interfejs dla każdej klasy | abstrakcja musi chronić znaczącą granicę |

Łatwość utworzenia mocka nie dowodzi zgodności z SOLID.

---

## 4.1-4.2. Spójność i sprzężenie

- **Spójny** moduł da się opisać jednym zdaniem domenowym; jego elementy zmieniają się z tego samego powodu.
- Mała klasa nie musi być spójna, a duża może być nierozdzielnym konceptem.
- **Sprzężenie**: czy zmiana jednego modułu wymusza edycję, testy lub wdrożenie drugiego?
- Obejmuje importy, format danych, kolejność wywołań, wspólny stan, technologię, wiedzę zespołów.
- **Interfejs nie usuwa sprzężenia** - zastępuje zależność od implementacji zależnością od kontraktu.

---

## 4.3. Koszt wydzielenia i ukrywana decyzja

- Extract Class może zwiększyć spójność, ale dodaje współpracownika, nawigację i nowy kontrakt.
- Może rozdzielić dane, które muszą zmieniać się atomowo - porównuj koszt zmiany przed i po.
- Dobry moduł ukrywa **decyzję trudną lub zmienną**, a nie tylko etap przetwarzania.
- Kandydaci: polityka cenowa, sposób zapisu, format komunikatu, szczegóły protokołu.

---

## 4.4. Diagnostyka spójności i sprzężenia

| Pytanie | Sygnał problemu |
| --- | --- |
| Ile miejsc zmienić dla jednej reguły? | rozproszona wiedza, shotgun surgery |
| Czy moduł ma kilku niezależnych właścicieli? | niska spójność odpowiedzialności |
| Czy typ frameworka trafia do domeny? | sprzężenie technologiczne |
| Czy wywołania wymagają ukrytej kolejności? | sprzężenie protokołu lub czasu |
| Czy zmiana adaptera zmienia przypadek użycia? | zły kierunek zależności |
| Czy moduły importują się wzajemnie? | cykl blokujący niezależną zmianę |

---

## 5.1-5.2. Reguła zależności a przepływ sterowania

- **Reguła zależności**: zależności źródłowe przez granicę wskazują ku polityce, nie ku mechanizmowi.
- Przypadek użycia nie importuje `ResultSet`, encji ORM ani żądania HTTP - adapter tłumaczy te typy.
- Aplikacja definiuje port `QuoteRepository` i go wywołuje; adapter bazodanowy go implementuje.
- Composition root tworzy adapter i przekazuje go do przypadku użycia.
- **Sterowanie** płynie do adaptera, a **zależność źródłowa** adaptera prowadzi do portu - to DIP w praktyce.

---

## 5.3. Elementy praktyczne

| Element | Odpowiedzialność |
| --- | --- |
| domena | stabilne pojęcia, inwarianty i reguły problemu |
| przypadek użycia | orkiestracja jednego celu |
| port wyjściowy | potrzeba wnętrza wobec świata zewnętrznego |
| adapter wejściowy | mapowanie żądania na dane przypadku użycia |
| adapter wyjściowy | realizacja portu przez bazę, plik, HTTP, broker |
| composition root | wybór konkretów i składanie grafu zależności |

Port należy do **strony formułującej potrzebę** - nie odbija wszystkich możliwości bazy.

---

## 5.4. Dane na granicy i composition root

- Granicę przekraczają proste rekordy, obiekty wartości i błędy kontraktu, nie modele frameworka.
- Osobny model ma sens, gdy granica oddziela różne semantyki lub tempo zmian - nie kopiuj mechanicznie.
- Composition root składa graf, ale **nie zawiera reguł biznesowych**.
- Ręczne konstruktory wystarczą; kontener DI nie tworzy poprawnego kierunku zależności.

---

## 5.5-5.6. Kręgi to nie szablon; koszty granic

- Liczy się położenie polityki i kierunek zależności, nie nazwy czterech folderów.
- Hexagonal, Onion i Clean to rodzina inside/outside, nie dokładne synonimy.
- Clean Architecture nie wymaga mikroserwisów, DDD ani frameworka DI - działa w modularnym monolicie.
- Koszty: porty, mapowanie, więcej plików, testy kontraktowe; mały CRUD czy prototyp mogą tego nie uzasadniać.
- Adapter ogranicza propagację szczegółów, ale wymiana bazy nadal kosztuje.

---

## 6.1-6.2. Wzorzec i refaktoryzacja w jego kierunku

- Wzorzec opisuje problem, kontekst, siły i konsekwencje - nie jest kodem do skopiowania.
- Przed użyciem: znany problem, warianty, prostsza alternatywa, warunek, gdy przestanie być potrzebny.
- Kierunek: zabezpiecz zachowanie → nazwij oś zmienności → odsłoń role → najwęższy kontrakt.
- Przenoś po jednym wariancie z testami i **zatrzymaj się**, gdy problem jest rozwiązany.

---

## 6.3. Strategy i Adapter

- **Strategy**: rodzina wymiennych algorytmów o wspólnym kontrakcie.
- Czytelny `switch` bywa lepszy dla nielicznych, zamkniętych, lokalnych wariantów.
- **Adapter** tłumaczy obcy interfejs lub model na kontrakt klienta na realnej granicy.
- Wartość Adaptera: mapowanie formatów, tłumaczenie błędów, ponowienia, czas - nie puste przekazywanie 1:1.

---

## 6.4. Wzorzec można usunąć

Refaktoryzacja **od** wzorca jest równie poprawna jak **do** wzorca. Sygnały nadmiaru:

- jedna implementacja od dawna lub interfejs powtarzający API jednej klasy,
- fabryka z jednym konstruktorem, konfiguracja trudniejsza od `new`,
- większość implementacji rzuca `UnsupportedOperationException`,
- nowy wariant nadal wymaga zmian w wielu centralnych miejscach.

---

## 7.1. Studium przypadku: punkt wyjścia

```java
public DeliveryQuote createQuote(String customerEmail,
        ShippingMethod method, Parcel parcel) {
    BigDecimal price = switch (method) {
        case STANDARD -> { BigDecimal base = new BigDecimal("10.00")
                .add(parcel.weightKg().multiply(new BigDecimal("2.00")));
            yield money(base.add(base.multiply(new BigDecimal("0.08")))); }
        case EXPRESS -> { BigDecimal base = new BigDecimal("20.00")
                .add(parcel.weightKg().multiply(new BigDecimal("3.00")));
            yield money(base.add(base.multiply(new BigDecimal("0.08")))); }
    };
    DeliveryQuote quote = new DeliveryQuote(customerEmail, method, parcel, price);
    storedQuotes.add(quote);
    System.out.printf("Quote ready for %s: %s costs %s%n",
            customerEmail, method, price);
    return quote;
}
```

`LegacyDeliveryQuoteService` liczy cenę, zapisuje ofertę, wypisuje powiadomienie i zwraca wynik.

---

## 7.2. Co naprawdę jest problemem

Nie sam `switch`, lecz **niezależne decyzje w jednym miejscu**.

| Obserwacja | Konsekwencja |
| --- | --- |
| stawka `0.08` w obu gałęziach | jedna reguła, dwie reprezentacje |
| polityki cenowe wewnątrz serwisu | zmiana wariantu modyfikuje koordynację |
| zapis w kolekcji serwisu | przypadek użycia zna mechanizm przechowywania |
| bezpośredni `System.out` | zmiana kanału wymaga edycji serwisu |
| ukryta kolejność zapis → powiadomienie | protokół wymaga kontraktu i testu |

---

## 7.3. Najpierw test charakterystyki

```java
@Test
void documentsStandardDeliveryPriceAndStorage() {
    LegacyDeliveryQuoteService service = new LegacyDeliveryQuoteService();
    DeliveryQuote quote = service.createQuote("developer@example.com",
            ShippingMethod.STANDARD, new Parcel(new BigDecimal("3.00")));

    assertEquals(new BigDecimal("17.28"), quote.price());
    assertEquals(List.of(quote), service.storedQuotes());
}
// EXPRESS dla 3 kg: 31.32
```

- Pierwszy krok to nie wzorzec, lecz zapis znanego zachowania: ceny i zapisu.
- Konsola nie jest przechwycona - **jawna luka**: czy treść i kolejność powiadomień to kontrakt?

---

## 7.4. Model domeny i DRY dla opłaty

- `Parcel` i `DeliveryQuote` pilnują inwariantów: dodatnia masa, niepusty e-mail, cena ≥ 0 o skali 2.
- Powtórzoną regułę opłaty paliwowej nazwano `FuelSurcharge` - jeden właściciel.
- **Bez** wspólnej `basePrice(...)`: formuły podobne, ale to **odrębne cenniki**.

```java
public BigDecimal addTo(BigDecimal baseAmount) {
    return baseAmount.add(baseAmount.multiply(rate))
            .setScale(2, RoundingMode.HALF_UP);
}
```

---

## 7.5. Strategy dla istniejących wariantów i zakres OCP

```java
public interface DeliveryPricePolicy {
    ShippingMethod method();              // stabilna, non-null
    BigDecimal priceFor(Parcel parcel);   // deterministyczna, >= 0,
}                                         // skala 2, bez efektów ubocznych
```

- Kontrakt dokumentuje gwarancje, których typy nie wyrażają.
- Uzasadnienie: **dwa istniejące** algorytmy i wybór według metody - bez pluginów.
- `DeliveryPriceCalculator` indeksuje polityki (`EnumMap`), odrzuca puste i zduplikowane konfiguracje.
- Nowa metoda dostawy nadal wymaga zmiany enuma i composition root - zamknięta jest logika wyboru, nie cały system.
- Kalkulator bez interfejsu - **DIP nie wymaga interfejsu przed każdą klasą**.

---

## 7.6. Test kontraktowy i LSP

```java
@ParameterizedTest(name = "{0}")
@MethodSource("policies")
void everyPolicyObeysTheSubstitutionContract(String description,
        ShippingMethod expectedMethod, DeliveryPricePolicy policy) {
    Parcel parcel = new Parcel(new BigDecimal("100.00"));
    BigDecimal first = policy.priceFor(parcel);
    assertAll(
        () -> assertEquals(expectedMethod, policy.method()),
        () -> assertTrue(first.signum() >= 0),
        () -> assertEquals(2, first.scale()),
        () -> assertEquals(first, policy.priceFor(parcel)));
}
```

Każda strategia przechodzi te same sprawdzenia, ale test **nie dowodzi** LSP dla wszystkich stanów.

---

## 7.7. Porty i przypadek użycia

```java
public interface QuoteRepository { void save(DeliveryQuote quote); }
public interface QuoteNotifier { void quoteCreated(DeliveryQuote quote); }

public DeliveryQuote execute(Command command) {
    DeliveryQuote quote = new DeliveryQuote(
            command.customerEmail(), command.method(), command.parcel(),
            priceCalculator.priceFor(command.method(), command.parcel()));
    repository.save(quote);
    notifier.quoteCreated(quote);
    return quote;
}
```

- Dwie odrębne role - dwa wąskie porty; `QuoteRepository` nie dziedziczy `CrudRepository`.
- `CreateDeliveryQuote` koordynuje jedną wycenę, bez wzoru ceny, zapisu i formatowania.

---

## 7.8. Protokół efektów i adaptery

- Kolejność `save` → `quoteCreated` to **decyzja protokołu**: błąd zapisu = brak powiadomienia.
- Kolejność nie daje atomowości - w systemie rozproszonym mogą być potrzebne ponowienia lub outbox.
- Test przypadku użycia: przebieg udany i `doesNotNotifyWhenSavingFails`, porty jako lambdy.
- `InMemoryQuoteRepository` i `ConsoleQuoteNotifier` importują porty; aplikacja nie importuje `adapter`.
- Rzeczywiste adaptery (JDBC/JPA) wymagają osobnych testów integracyjnych.

---

## 7.9. Composition root i kierunek zależności

`Module3Examples` składa graf: `FuelSurcharge` → polityki → kalkulator → adaptery → `CreateDeliveryQuote`.

| Kod | Może zależeć od | Nie powinien zależeć od |
| --- | --- | --- |
| domena | Java SE, pojęcia domenowe | aplikacja, adaptery, frameworki |
| aplikacja | domena, własne porty | konkretne adaptery, konsola, baza |
| adapter | porty, domena, technologia | composition root, cykle z innymi adapterami |
| composition root | wszystko do złożenia | reguły biznesowe |

---

## 7.10. Zasady widoczne w rozwiązaniu

| Decyzja | Zasada | Korzyść | Koszt |
| --- | --- | --- | --- |
| `FuelSurcharge` | DRY, spójność | jedna stawka i zaokrąglenie | dodatkowy typ |
| dwie polityki | SRP, OCP, Strategy | lokalne warianty | kontrakt, rejestracja |
| test kontraktowy | LSP | wspólne gwarancje | lista implementacji |
| porty | ISP, DIP | tylko potrzebne role | składanie implementacji |
| adaptery | Clean Arch., Adapter | efekty na zewnątrz | mapowanie, testy integracyjne |
| ręczny root | KISS, YAGNI | widoczny graf | ręczna konfiguracja |

Więcej klas uzasadnia to, że dwa warianty i dwa efekty zewnętrzne **już istnieją**.

---

## Ćwiczenie 1: DRY, KISS i YAGNI (20 + 10 min)

- Cel: dla pięciu sytuacji zdecydować - **scalić teraz, zostawić lokalnie czy zebrać informacje?**
- Sytuacje: wspólna opłata 8%, dwa limity 1000 kg, ceny w testach, pluginy „na przyszłość”, trzecia metoda i cache.
- Dla każdej: wiedza, właściciel, wymaganie, najprostsze rozwiązanie, sygnał ponownej oceny.

Szczegóły: zadania modułu 3, Ćwiczenie 1.

---

## Ćwiczenie 2: SOLID jako seria bezpiecznych zmian (25 + 10 min)

- Cel: zrefaktoryzować `LegacyDeliveryQuoteService` - `FuelSurcharge`, polityki, porty, adaptery, ręczny root.
- Po jednym wariancie, testy po każdym kroku; test kontraktowy polityk i test awarii zapisu.
- Kryteria: `17.28` / `31.32` dla 3 kg, przypadek użycia bez importu adapterów, zielone `mvn verify`.

Szczegóły: zadania modułu 3, Ćwiczenie 2.

---

## Ćwiczenie 3: granica i reguła zależności (25 + 10 min)

- Cel: sklasyfikować zależności, dodać `BufferingQuoteNotifier` i zabezpieczyć granicę.
- Adapter podmieniany **wyłącznie w composition root**, bez frameworka DI.
- Zaproponuj kontrolę importów `adapter` (test architektury, moduły Maven, JPMS, CI) i porównaj ich siłę.

Szczegóły: zadania modułu 3, Ćwiczenie 3.

---

## Ćwiczenie 4: wzorzec jako decyzja odwracalna (20 + 10 min)

- Cel: ocenić dwa scenariusze - **SAME_DAY** z klienta przewoźnika oraz uproszczenie po zniknięciu EXPRESS.
- SAME_DAY: polityka, adapter czy oba? Gdzie konwersja jednostek, błędy, timeout?
- Nie dopasowuj klienta na siłę do kontraktu, którego obietnic nie spełnia.

Szczegóły: zadania modułu 3, Ćwiczenie 4.

---

## Kluczowe wnioski z omówienia

- Wspólną opłatę paliwową scalamy od razu; dwa limity 1000 kg zostają lokalne - inna wiedza i właściciele.
- Interfejs `Surcharge` nie jest potrzebny: brak drugiego wariantu, a mock to za mało jako powód.
- Aplikacja → `InMemoryQuoteRepository` i wyjątek JDBC w porcie są niedozwolone - adapter tłumaczy błąd.
- SAME_DAY nie powinien być `DeliveryPricePolicy`: klient sieciowy łamie obietnicę determinizmu (LSP).
- Po usunięciu EXPRESS strategię można zwinąć, ale **porty zapisu i powiadomień zostają**.

Pełne omówienie: rozwiązania modułu 3.

---

## Sprawdzenie wiedzy

1. Co jest przedmiotem DRY - tekst czy wiedza? Kiedy identycznych funkcji nie łączyć?
2. Dlaczego prostsze rozwiązanie nie zawsze ma mniej klas? Czy YAGNI zabrania testów?
3. Jak SRP definiuje powód zmiany? Czy każdy `switch` narusza OCP?
4. Jakie warunki są kluczowe dla LSP i dlaczego test kontraktowy go nie dowodzi?
5. Dlaczego `@Autowired` nie dowodzi DIP?
6. Co interfejs robi ze sprzężeniem? Gdzie należy port wyjściowy?
7. Kiedy Strategy jest gorsza od `switch` i kiedy usunąć wzorzec?

---

## Lista kontrolna

- **Abstrakcja:** nazwana wiedza, aktualne wymaganie, właściciel zmian, prostsza alternatywa, sygnał ponownej oceny.
- **SOLID:** SRP według aktorów, nazwana oś OCP, kontrakt LSP z testami, interfejs = rola klienta, importy ku polityce.
- **Granica:** oddziela znaczenie, tempo zmian lub ryzyko; brak typów frameworka wewnątrz; reguła sprawdzana automatycznie.
- **Wzorzec:** problem opisany bez nazwy wzorca, wariant istnieje dziś, wiadomo, kiedy go uprościć.
- **Na koniec:** zachowanie niezmienione, jedna reprezentacja wiedzy, brak nowych cykli, pełny build zielony.

---

## Podsumowanie - najważniejsze wnioski

- **DRY, KISS, YAGNI** równoważą centralizację wiedzy, prostotę i odraczanie spekulacji - nie liczą linii ani klas.
- **SOLID** ma wartość tylko wtedy, gdy obniża koszt konkretnej zmiany.
- **Interfejs** zastępuje jedno sprzężenie innym - sensownie, gdy kontrakt jest stabilniejszy i po właściwej stronie.
- **Clean Architecture** chroni politykę, kierując zależności do wnętrza.
- **Wzorzec** to możliwy kierunek serii refaktoryzacji: chroń zachowanie, wprowadź najwęższe rozwiązanie, bądź gotów je uprościć.
