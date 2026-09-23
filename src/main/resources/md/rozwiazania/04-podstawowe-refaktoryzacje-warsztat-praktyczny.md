# Moduł 4. Podstawowe refaktoryzacje - warsztat praktyczny

## Rozwiązania i wskazówki dla prowadzącego

Numeracja i nazwy odpowiadają plikowi zadań. Kod referencyjny znajduje się w `src/main/java/pl/training/module4`, testy w `src/test/java/pl/training/module4`.

---

## Ćwiczenie 1: lokalne porządkowanie metody

**Kod referencyjny (stan docelowy):** `src/main/java/pl/training/module4/stage1/RentalQuoteService.java`.

**Test chroniący:** `src/test/java/pl/training/module4/stage0/RentalQuoteServiceCharacterizationTest.java` (uczestnicy mogą go skopiować i przestawić na swoją klasę) oraz `RentalQuoteStagesEquivalenceTest`.

### Rozwiązanie wzorcowe

Kroki 1-2 (czułość testu): zmiana `r.days() >= 7` na `r.days() > 7` lub na `>= 8` powoduje porażkę `documentsDiscountBoundary` (7 dni przestaje dawać rabat 84.00) i `documentsCompleteGeneratorQuote` nie (8 dni nadal z rabatem). Wniosek: sama pełna oferta nie chroni granicy; potrzebne są oba boki progu.

Zalecana kolejność transformacji (z teorii, sekcja 14.1):

1. **Rename** lokalnych symboli, ponieważ zmienia najmniejszy obszar i ułatwia czytanie następnych kroków: `r` -> `request`, `a` -> `baseRentalCost`, `d` -> `discount`, `i` -> `insuranceCost`, `f` -> `deliveryCost`, `n` -> `netAmount`, `v` -> `vat`, `t` -> `total`, pola `rates` -> `dailyRates`, `disc` -> `longRentalDiscountRate`.
2. **Extract Constant oraz Replace Magic Numbers**, ponieważ ujawniają reguły bez zmiany przepływu: `LONG_RENTAL_DAYS = 7`, `INSURANCE_DAILY_RATE = 8.00`, `DELIVERY_FEE = 25.00`, `VAT_RATE = 0.23`, `ZERO_MONEY = 0.00`.
3. **Extract Variable** dla czystych wartości używanych w jednym obliczeniu: `dailyRate`, `rentalDays`.
4. **Extract Method** dla kolejnych spójnych fragmentów: `calculateDiscount`, `calculateInsuranceCost`, `calculateDeliveryCost`.
5. **Encapsulate Conditional** dla progu rabatu: `qualifiesForLongRentalDiscount(request)`.
6. **Extract Method** dla budowy dokumentu: `buildDocument(...)` z `String.format(Locale.ROOT, ...)`.
7. **Inline Variable** dla zmiennej `q`, która jedynie poprzedzała `return`.

Docelowa metoda publiczna:

~~~java
public String createQuote(RentalRequest request) {
    BigDecimal dailyRate = dailyRates.get(request.equipmentType());
    BigDecimal rentalDays = BigDecimal.valueOf(request.days());
    BigDecimal baseRentalCost = money(dailyRate.multiply(rentalDays));
    BigDecimal discount = calculateDiscount(request, baseRentalCost);
    BigDecimal insuranceCost = calculateInsuranceCost(request);
    BigDecimal deliveryCost = calculateDeliveryCost(request);
    BigDecimal netAmount = money(baseRentalCost
            .subtract(discount).add(insuranceCost).add(deliveryCost));
    BigDecimal vat = money(netAmount.multiply(VAT_RATE));
    BigDecimal total = money(netAmount.add(vat));
    return buildDocument(request, baseRentalCost, discount,
            insuranceCost, deliveryCost, netAmount, vat, total);
}
~~~

### Wskazówki do omówienia (z teorii)

- Nie ma potrzeby wydzielania każdej operacji arytmetycznej. Nazwy `netAmount`, `vat` i `total` wystarczająco opisują kolejne kroki, a nadmiar jednoliniowych metod utrudniłby czytanie algorytmu.
- Stała `ZERO_MONEY` nie jest wyłącznie optymalizacją tworzenia obiektu. Informuje o przyjętej skali wartości zerowej w dokumencie.
- Stawki sprzętu pozostają na etapie 1 w mapie instancyjnej (podobnie jak stawka rabatu), ponieważ będą przenoszone wraz z odpowiedzialnością za wycenę w ćwiczeniu 3.
- Wydzielenie metod zwiększyło liczbę nazw, ale nie zmieniło kolejności obliczeń. Rabat nadal jest liczony wyłącznie od podstawowego kosztu, zaokrąglenie zachodzi w tych samych miejscach, a dokument ma identyczny format.

### Typowe błędy uczestników

- Przeniesienie zaokrąglenia: na przykład jeden `money(...)` na końcu zamiast po każdym kroku lub zaokrąglenie rabatu dopiero w `netAmount`. Łapie to `documentsDiscountRounding` (rabat 59.99 dla 15 dni wiertnicy) i `documentsVatRounding`.
- Liczenie rabatu od sumy z dodatkami ("przy okazji" porządkowania).
- `BigDecimal.ZERO` zamiast `0.00`: `toPlainString()` daje wtedy `0` zamiast `0.00`; test pełnego dokumentu i `distinguishesInsuranceFromDelivery` to wykryją.
- Zamiana konkatenacji na `"""...""".formatted(...)` lub `String.format` bez `Locale.ROOT`. Test w domyślnym locale przejdzie, ale `LocaleIndependentFormattingTest` (locale `ar-EG`) wykaże różnicę w polu dni.
- Utrata końcowego `\n` lub zmiana wcięć w text block.
- Nazwy stałych opisujące wartość (`SEVEN`, `TWENTY_THREE_PERCENT`) zamiast roli.
- Wydzielenie `ONE`/`ZERO` dla oczywistych literałów, które nie niosą decyzji.
- Łączenie kilku transformacji w jeden krok bez uruchamiania testu po każdej.
- Zmiana sygnatury `createQuote` (na przykład rozbicie `RentalRequest` na prymitywy).

### Pytania do dyskusji

- Który krok był najmniej ryzykowny, a który wymagał decyzji (na przykład czy `8.00` i `0.10` to stałe klasy, czy konfiguracja)?
- Czy `buildDocument` z ośmioma parametrami jest dobrym stanem końcowym? Jaki sygnał daje taka lista parametrów (sekcja 3.2)?
- Czy `qualifiesForLongRentalDiscount` powinna zostać zinlinowana, bo ma jedną linię?
- Czy próg 7 dni rabatu i ewentualny siedmiodniowy termin zwrotu powinny dzielić jedną stałą?

---

## Ćwiczenie 2: Extract i Inline pod presją semantyki

**Kod referencyjny (część B):** stan wyjściowy `src/main/java/pl/training/module4/stage2/RentalQuoteService.java`, stan docelowy `src/main/java/pl/training/module4/stage3/RentalQuoteService.java`. Test: `RentalQuoteStagesEquivalenceTest`.

### Rozwiązanie wzorcowe części A (z teorii, sekcja 14.2)

| Przypadek | Ocena | Uzasadnienie | Test potrzebny przed operacją |
| --- | --- | --- | --- |
| 1. Inline zmiennej z `clock.instant()` używanej dwa razy | niebezpieczne | jedno odczytanie czasu zmieniłoby się w dwa, które mogą zwrócić różne wartości | test z kontrolowanym `Clock` zwracającym kolejne różne wartości; asercja, że obie obserwacje używają tej samej chwili |
| 2. Wcześniejsze obliczenie `account.isActive()` | niebezpieczne | znika ochrona krótkiego spięcia i dla `null` pojawia się wyjątek | test z `account == null` oczekujący braku wyjątku |
| 3. Argument z efektem ubocznym skopiowany do dwóch wywołań | niebezpieczne | zmienia liczbę i prawdopodobnie kolejność efektów | test liczący zapisy audytowe (atrapa rejestrująca liczbę i kolejność wywołań) |
| 4. Lambda wydzielona do `var` | niepoprawne lub pozbawione wymaganego kontekstu | lambda potrzebuje typu docelowego; jawny odpowiedni interfejs funkcyjny zachowuje ten kontekst | kompilacja plus test sprawdzający, które przeciążenie zostało wybrane (efekt tego przeciążenia) |
| 5. Inline metody, którą może przesłonić podklasa | zwykle niebezpieczne | wywołanie podlega dynamicznej dyspozycji, a wklejona implementacja klasy bazowej nie | test na instancji podklasy z przesłoniętą metodą |
| 6. Inline metody `synchronized` | niebezpieczne bez odtworzenia blokady | dla metody instancyjnej nie zostanie automatycznie zachowany monitor odbiorcy, a dla statycznej monitor obiektu `Class` klasy deklarującej | analiza współbieżności, ewentualnie test współbieżny; zastąpienie przez `synchronized (this)` lub `synchronized (Klasa.class)` na tym samym monitorze |
| 7. Inline prywatnego delegata etapu 2 | bezpieczne przy ustalonych założeniach | delegat ma jedno wywołanie, brak adnotacji, proxy, przesłonięcia, blokady i odwołań tekstowych | test równoważności etapów |
| 8. Wydzielenie VAT do stałej | bezpieczne, jeśli oba literały oznaczają tę samą regułę | należy centralizować wspólną wiedzę, nie jedynie równą reprezentację tekstową | test ofert z VAT; przegląd, czy oba `0.23` to ta sama stawka |

Uwaga z teorii: narzędzie IDE może zapobiec części błędów typowania. Nie potwierdzi jednak, że dwa wywołania zegara, repozytorium albo loggera są semantycznie równoważne jednemu.

> Kolumna "Test potrzebny przed operacją" została opracowana na podstawie kodu referencyjnego i treści teorii, nie pochodzi wprost z tabeli rozwiązań.

Uzupełnienia do omówienia:

- Przypadek 1: odwrotna operacja (Extract Variable z dwóch `clock.instant()` do jednego `now`) też nie jest neutralna semantycznie. Może być właściwą decyzją funkcjonalną, ale trzeba ustalić, czy kontrakt wymaga jednego snapshotu czasu (sekcja 4.2).
- Przypadek 4: po wydzieleniu z jawnym typem kompilacja może się udać z innym przeciążeniem niż wcześniej. Poprawna kompilacja nie gwarantuje zachowania (sekcja 6.2).
- Przypadek 6: poza blokadą traci się też relację widoczności pamięci (happens-before) związaną z monitorem.

### Rozwiązanie części B

Przed operacją sprawdzamy, że `calculatePrice` w etapie 2 jest prywatna, niesynchronizowana, bez adnotacji, ma jedno wywołanie i nie jest celem refleksji. Po Inline Method:

~~~java
public String createQuote(RentalRequest request) {
    Objects.requireNonNull(request, "request");

    PriceBreakdown price = pricing.calculate(request);
    return buildDocument(request, price);
}
~~~

To odpowiada dokładnie `stage3/RentalQuoteService.java`. `RentalQuoteStagesEquivalenceTest` pozostaje zielony.

### Typowe błędy uczestników

- Ocena przypadku 7 jako "zawsze bezpieczne, bo jedna linia", bez sprawdzenia warunków (proxy, adnotacje, refleksja, przesłonięcie).
- Mylenie monitora metody statycznej (`Class`) z monitorem instancji.
- Uznanie `var` za neutralne, bo "to tylko skrót zapisu".
- Traktowanie dwóch równych literałów `0.23` jako jednego pojęcia bez sprawdzenia ich znaczenia.
- Przy części B: usunięcie także `buildDocument` "dla spójności" albo inline'owanie `qualifiesForLongRentalDiscount` w `RentalPricing`.

### Pytania do dyskusji

- Po co w ogóle wprowadzono delegat `calculatePrice`, skoro za chwilę go usuwamy? (Umożliwia osobne sprawdzenie przeniesienia i późniejszego Inline Method.)
- Kiedy delegat powinien pozostać na stałe? (Gdy chroni kontrakt: fasada kompatybilności, granica proxy/transakcji, publiczne API.)
- Jak Extract i Inline mogą być jednocześnie dobrą praktyką?

---

## Ćwiczenie 3: Extract Class oraz przeniesienie odpowiedzialności

**Kod referencyjny:** `src/main/java/pl/training/module4/pricing/PriceBreakdown.java`, `src/main/java/pl/training/module4/pricing/RentalPricing.java`, stan pośredni `src/main/java/pl/training/module4/stage2/RentalQuoteService.java`, stan końcowy `src/main/java/pl/training/module4/stage3/RentalQuoteService.java`.

**Testy referencyjne:** `src/test/java/pl/training/module4/pricing/RentalPricingTest.java`, `src/test/java/pl/training/module4/pricing/PriceBreakdownTest.java`, `RentalQuoteStagesEquivalenceTest`, `LocaleIndependentFormattingTest`.

### Odpowiedzi na pytania wstępne

> Odpowiedzi na pytania wstępne opracowane na podstawie kodu referencyjnego, nie pochodzą wprost z materiału teoretycznego.

- **Właściciel stawek:** obiekt wyceny `RentalPricing`; domyślne wartości w `RentalPricing.standard()` jako jedyne źródło prawdy.
- **Czy konfiguracja jest wspólna dla wszystkich ofert:** tak, stawki i rabat nie zależą od żądania; jeden obiekt wyceny obsługuje wiele wywołań `calculate`.
- **Cykl życia obiektu wyceny:** niemutowalny obiekt tworzony raz (fabryka lub wstrzyknięcie przez konstruktor usługi), może być współdzielony, nie potrzebuje referencji zwrotnej do usługi dokumentu.
- **Monitor, przesłonięcie, dostęp pakietowy, adnotacje:** przenoszone metody są prywatne (większość statyczna), klasa jest `final`, brak `synchronized` i adnotacji frameworka; nowy pakiet `pricing` wymaga publicznego `calculate` i publicznego typu wyniku.
- **Czy stary punkt wejścia musi pozostać:** bezargumentowy konstruktor i `createQuote(RentalRequest)` zostają, bo są publicznym kontraktem etapów 0 i 1; ich usunięcie byłoby osobną zmianą API.

### Rozwiązanie wzorcowe (z teorii, sekcje 12.4 i 14.3)

Odpowiedzialności:

- `RentalPricing`: obliczyć nazwany rozkład ceny dla poprawnego żądania i skonfigurowanych reguł.
- `RentalQuoteService`: zbudować dokument oferty na podstawie żądania i wyniku wyceny.

Co się przenosi:

- pola `dailyRates` oraz `longRentalDiscountRate` poruszają się razem z metodami, które je interpretują,
- stałe dodatków, VAT, progu rabatu i funkcja `money` należą do algorytmu wyceny,
- formatowanie, normalizacja nazwy klienta i etykiety dokumentu pozostają po stronie prezentacji.

`PriceBreakdown` zastępuje długą listę równoległych wartości jednym typem wyniku. Nie jest przypadkowym pojemnikiem: jego pola reprezentują spójny rezultat wyceny, a konstruktor kompaktowy chroni format kwot (brak `null`, brak wartości ujemnych, skala 2 z `RoundingMode.UNNECESSARY`).

Sekwencja bezpiecznego przeniesienia:

1. W klasie źródłowej wprowadź prywatny punkt `calculatePrice`, który nadal korzysta z istniejącej logiki.
2. Utwórz nowy typ i przenieś do niego implementację razem z potrzebnymi danymi.
3. Zastąp ciało metody źródłowej delegowaniem do nowego właściciela.
4. Po każdym kroku usuń ze źródła tylko te dane, które nie mają już odczytów.
5. Utrzymuj jedno źródło prawdy dla każdej stawki i reguły.
6. Migruj kontrolowanych klientów, a przejściowy delegat usuń dopiero wtedy, gdy nie chroni kontraktu (to Inline Method z ćwiczenia 2).

Przejściowy delegat w etapie 2 jest celowo wprowadzony podczas Move Method. Bezargumentowy konstruktor deleguje do standardowej konfiguracji i zachowuje dotychczasowy punkt konstrukcji, a przeciążenie umożliwia jawne wstrzyknięcie `RentalPricing`:

~~~java
public RentalQuoteService() {
    this(RentalPricing.standard());
}

public RentalQuoteService(RentalPricing pricing) {
    this.pricing = Objects.requireNonNull(pricing);
}
~~~

Stan końcowy odpowiada etapowi 3.

Test jednostkowy nowego obiektu: minimum to odpowiednik `RentalPricingTest.calculatesApprovedPriceBreakdown` (generator, 8 dni, oba dodatki: 960.00 / 96.00 / 64.00 / 25.00 / 953.00 / 219.19 / 1172.19). Jeśli uczestnik dodał walidację konfiguracji, powinien mieć testy jak w `RentalPricingTest` (brakująca stawka, stawka zerowa, ujemna, zaokrąglana do zera, rabat poza `[0, 1]`, kopia defensywna mapy).

### Wskazówki do omówienia

- Konstruktor `RentalPricing` w kodzie referencyjnym dodaje walidację (kompletność stawek, dodatniość po normalizacji HALF_UP, zakres rabatu) i kopię defensywną. Dla poprawnych danych wynik jest ten sam. Zachowanie niepoprawnych konfiguracji nie było częścią publicznego kontraktu etapu 0, ale w systemie produkcyjnym dodanie takiej walidacji powinno być osobną, świadomą decyzją. Warto zapytać uczestników, czy ich wersja dodała walidację i czy to nazwali.
- Extract Class jest decyzją projektową; Move Field i Move Method są mechaniką.
- Zły wynik ekstrakcji (sekcja 8.4): dane przeniesione, ale usługa nadal liczy wszystko przez gettery `RentalPricing`; albo nowa klasa potrzebuje referencji zwrotnej do usługi.

### Typowe błędy uczestników

- Pozostawienie kopii stawek lub stałej VAT w `RentalQuoteService` (dwa źródła prawdy).
- Przeniesienie formatowania kwot (`toPlainString`, etykiety) do `RentalPricing`, czyli wiedzy o dokumencie do wyceny.
- Przeniesienie normalizacji nazwy klienta do wyceny.
- Usunięcie bezargumentowego konstruktora "bo teraz wstrzykujemy".
- `RentalPricing` tworzone wewnątrz `createQuote` przy każdym wywołaniu (niejawna zależność, inny cykl życia).
- `PriceBreakdown` jako mutowalna klasa z setterami albo jako `Map<String, BigDecimal>`.
- Zaokrąglanie w konstruktorze `PriceBreakdown` (HALF_UP zamiast UNNECESSARY) ukrywające błąd w miejscu zaokrąglenia.
- Rozbicie kroku na jeden duży commit bez stanu pośredniego z delegatem.

### Pytania do dyskusji

- Czy `PriceBreakdown` powinien być publicznym typem API od razu, czy najpierw pakietowym (sekcja 8.2, krok 7)?
- Co zmieniłoby się, gdyby stawki trzeba było aktualizować bez wdrożenia? (Wtedy nie są stałymi, tylko konfiguracją, sekcja 4.3.)
- Jak testować, aby nie porównywać wyłącznie nowej implementacji ze starą?

---

## Ćwiczenie 4: kontrola mutowalnego stanu

**Kod referencyjny:** faza A `src/main/java/pl/training/module4/encapsulation/AccessorBasedEquipmentCatalog.java`, faza B `src/main/java/pl/training/module4/encapsulation/EquipmentCatalog.java`. Test: `src/test/java/pl/training/module4/encapsulation/EquipmentCatalogTest.java`.

### Rozwiązanie wzorcowe (z teorii, sekcje 12.8 i 14.4)

Faza A i faza B nie powinny zostać połączone w jeden nieprzejrzysty commit.

**Faza A.** Prywatne pole może nadal wskazywać mapę klienta, a akcesor może nadal zwracać ten sam modyfikowalny obiekt. Takie rozwiązanie nie daje docelowej hermetyzacji, ale pozwala najpierw migrować składniową formę dostępu bez zmiany aliasowania.

~~~java
private String name;
private final Map<EquipmentType, BigDecimal> dailyRates;

public String name() { return name; }
public void setName(String newName) { name = newName; }
public Map<EquipmentType, BigDecimal> dailyRates() { return dailyRates; }
~~~

Testy charakterystyki fazy A (jak `legacyCatalogSharesItsMutableMapWithTheCaller` i `accessorBasedCatalogPreservesAliasesDuringControlledMigration`): mutacja mapy źródłowej jest widoczna w katalogu, `assertSame(source, catalog.dailyRates())`, `setName(" ")` jest akceptowane.

W zamkniętej aplikacji wdrażanej atomowo zachowanie całości pozostaje takie samo. Dla opublikowanej biblioteki usunięcie publicznego pola łamie zgodność źródłową i binarną oraz zmienia wynik refleksji.

**Faza B.** Przykład wybiera następujący kontrakt:

- konstruktor kopiuje wpisy do własnego `EnumMap`,
- wszystkie zmiany stawki przechodzą przez `changeDailyRate` (także w konstruktorze, więc walidacja jest w jednym miejscu),
- stawka jest normalizowana do dwóch miejsc metodą HALF_UP, a inwariant dodatniości jest sprawdzany po normalizacji,
- nazwa zmienia się tylko przez `renameTo` (niepusta, nie `null`),
- `dailyRateFor(type)` rzuca `IllegalArgumentException` dla brakującej stawki,
- `dailyRates()` zwraca niemodyfikowalną migawkę (`Map.copyOf`),
- późniejsze zmiany katalogu nie są widoczne w pobranej wcześniej migawce,
- `BigDecimal` jest niemutowalny, więc płytka kopia wartości jest wystarczająca.

Gdyby klient potrzebował obserwować późniejsze zmiany właściciela, właściwy byłby żywy niemodyfikowalny widok, na przykład oparty na `Collections.unmodifiableMap`. To inny kontrakt; nie należy wybierać go przypadkowo.

Testy fazy B (jak w `EquipmentCatalogTest`): mutacja źródła po konstrukcji niewidoczna (`39.99` zostaje), migawka po `changeDailyRate` nadal pokazuje `39.99`, właściciel pokazuje `42.00`, `put` na migawce rzuca `UnsupportedOperationException`, `renameTo(" ")` rzuca `IllegalArgumentException`, `42.005` -> `42.01`, `0.004` odrzucone.

Ocena punktu 6 fazy B:

- `null`: `EquipmentCatalog` odrzuca `null` nazwy, mapy, typu i stawki; `Map.copyOf` też odrzuca `null`. Jeżeli stary katalog dopuszczał `null`, to zmiana zachowania.
- Mutowalne elementy: niemodyfikowalna kolekcja blokuje zmianę członkostwa, nie wnętrza elementów. Tu elementy (`BigDecimal`, enum) są niemutowalne.
- Współbieżność: migawka nie czyni obiektu bezpiecznym wątkowo; `name` i `EnumMap` nie są synchronizowane. Potrzebny osobny kontrakt.

### Typowe błędy uczestników

- Dodanie walidacji w setterze już w fazie A i nazwanie tego refaktoryzacją.
- Zwracanie `Collections.unmodifiableMap(dailyRates)` w fazie A bez zauważenia, że to zmienia możliwość mutacji przez klienta (zmiana kontraktu).
- W fazie B przechowywanie mapy wejściowej bez kopii i zwracanie `unmodifiableMap`: klient nadal mutuje stan przez swoją referencję.
- Przekonanie, że `final` na polu mapy lub `Map.copyOf` daje głęboką niemutowalność.
- Walidacja stawki zduplikowana w konstruktorze i w `changeDailyRate` (dwa miejsca, które mogą się rozjechać).
- Sprawdzenie dodatniości przed normalizacją (0.004 przechodzi, a potem zapisuje się 0.00).
- Brak testu rozróżniającego migawkę od widoku (test tylko na `UnsupportedOperationException` nie wystarcza, bo oba rzucają ten wyjątek).
- Opieranie kontraktu na tożsamości wyniku `Map.copyOf`.

### Pytania do dyskusji

- Dlaczego `renameTo` jest lepszym kontraktem niż `setName`?
- Jak komunikować zmianę fazy B klientom biblioteki (wersja, deprecjacja, okres przejściowy)?
- Kiedy wybrać migawkę, kiedy żywy widok, a kiedy modyfikowalną kopię (tabela z sekcji 10.2)?

---

## Retrospektywa po każdym ćwiczeniu

> Rozwiązanie opracowane na podstawie kodu referencyjnego, nie pochodzi wprost z materiału teoretycznego.

Oczekiwane odpowiedzi (do naprowadzania grupy):

| Ćwiczenie | 1. Chroniona właściwość | 2. Krok mechaniczny vs decyzja | 3. Najmniejszy wiarygodny test | 4. Punkt zatrzymania |
| --- | --- | --- | --- | --- |
| 1 | pełny dokument bajt po bajcie, próg rabatu, miejsca zaokrągleń, niezależność od locale | mechaniczne: Rename, Inline `q`; decyzje: które literały to jedno pojęcie, gdzie granica metod, czy stawki to stałe | `RentalQuoteServiceCharacterizationTest` (szczególnie granica 6/7 dni i przypadki zaokrągleń) | brak jednoliterowych nazw i magicznych liczb, metody na jednym poziomie abstrakcji; nie wydzielamy każdej operacji arytmetycznej |
| 2 | liczba i kolejność ewaluacji, dynamiczna dyspozycja, monitor, typ docelowy | mechaniczne: Inline `calculatePrice`; decyzje: czy delegat chroni kontrakt | `RentalQuoteStagesEquivalenceTest` dla części B; dla części A testy z atrapą zegara, audytu, podklasą | usunięty jedyny zbędny delegat; `buildDocument` i `qualifiesForLongRentalDiscount` zostają |
| 3 | identyczne oferty na wszystkich etapach, publiczny punkt wejścia | mechaniczne: Move Method/Field, delegowanie; decyzje: granica odpowiedzialności, typ wyniku, walidacja konfiguracji | `RentalPricingTest.calculatesApprovedPriceBreakdown` plus test równoważności | jedno źródło prawdy, dokument nie zna wyceny, wycena nie zna formatu |
| 4 | faza A: aliasowanie i akceptowane wartości; faza B: nowy kontrakt własności | faza A mechaniczna; faza B to decyzja projektowa i zmiana kontraktu | testy aliasu (`assertSame`) w fazie A; test migawki po zmianie właściciela w fazie B | faza A: pola prywatne i zmigrowani klienci; faza B: zatwierdzony i przetestowany kontrakt |

Myśl przewodnia z teorii (sekcja 1.4): seria refaktoryzacji powinna mieć konkretny cel i nie trzeba stosować wszystkich dostępnych technik.

---

## Sprawdzenie wiedzy - odpowiedzi

1. **Co musi pozostać niezmienione podczas refaktoryzacji?** Ustalony zakres obserwowalnego zachowania, obejmujący odpowiednie wyniki, wyjątki, efekty, kolejność, a czasem także zgodność API i właściwości operacyjne.
2. **Dlaczego zielony test jednostkowy nie dowodzi kompatybilności publicznej biblioteki?** Nie obejmuje automatycznie wcześniej skompilowanych klientów, refleksji, serializacji, konfiguracji, frameworków ani integracji.
3. **Jakie trzy grupy zmiennych trzeba rozpoznać przed Extract Method?** Wartości wejściowe fragmentu, wartości lokalne używane tylko wewnątrz oraz wartości zmieniane wewnątrz i potrzebne później.
4. **Kiedy Extract Variable może zmienić zachowanie?** Gdy zmienia liczbę, kolejność lub moment ewaluacji, przez co wpływa na wyjątek, stan, czas, I/O albo wybór przeciążenia.
5. **Czym Replace Magic Numbers różni się od mechanicznego Extract Constant?** Extract Constant tworzy nazwę dla wystąpienia. Replace Magic Numbers wymaga ustalenia, które wystąpienia reprezentują tę samą decyzję i powinny mieć wspólnego właściciela.
6. **Dlaczego publiczna stała typu prostego lub `String` wymaga ostrożności przy zmianie wartości?** Taka publiczna wartość, jeśli spełnia warunki stałego wyrażenia, może zostać wkompilowana do kodu klienta. Zmiana biblioteki bez rekompilacji klienta może pozostawić starą wartość.
7. **Kiedy Inline Variable może zwiększyć liczbę efektów ubocznych?** Gdy inicjalizator nie jest czysty, a podstawienie wykonuje go w wielu miejscach lub w innych gałęziach.
8. **Dlaczego Inline Method metody, którą może przesłonić podklasa, może zmienić wynik?** Wywołanie metody wybiera implementację dynamicznie, natomiast wklejenie ciała klasy bazowej może ominąć implementację podtypu.
9. **Co może zostać utracone przy Inline Method metody `synchronized`?** Automatyczne wejście w monitor odbiorcy dla metody instancyjnej albo w monitor obiektu `Class` klasy deklarującej dla metody statycznej, a wraz z nim synchronizacja i relacja widoczności pamięci.
10. **Jakie kryterium jest ważniejsze przy Move Method niż liczba wywołań?** Własność odpowiedzialności, danych i inwariantów, uwzględniająca cykl życia oraz granice kontraktu.
11. **Jak bezpiecznie wykonać Move Field?** Wprowadzić nowego właściciela, skierować odczyty i zapisy do jednego źródła prawdy, migrować klientów, a stare pole usunąć dopiero po zniknięciu wszystkich użyć.
12. **Jaki sygnał uzasadnia Extract Class?** Spójna grupa danych i zachowań ma odrębną odpowiedzialność, język oraz powód zmiany.
13. **Dlaczego prywatne pole z publicznym setterem nie gwarantuje hermetyzacji?** Setter nadal może zezwalać na dowolną zmianę i pozostawiać inwarianty klientom. Hermetyzacja polega na kontroli kontraktu, nie samym modyfikatorze dostępu.
14. **Czym różni się niemodyfikowalny widok od niemodyfikowalnej migawki?** Widok blokuje mutację przez klienta, ale pokazuje późniejsze zmiany właściciela. Migawka zachowuje stan z chwili utworzenia.
15. **Czy `Map.copyOf` wykonuje kopię głęboką?** Nie. Kopiuje strukturę kolekcji, a nie wnętrze obiektów będących kluczami lub wartościami.
16. **Czy zwracanie niemodyfikowalnej mapy zapewnia bezpieczeństwo wątkowe obiektu?** Nie. Nie definiuje synchronizacji pozostałych operacji ani bezpiecznej publikacji całego stanu.
17. **Jak Extract Method może naruszyć krótkie spięcie?** Przez wcześniejsze obliczenie prawego operandu lub niewybranej gałęzi, które wcześniej mogły nie zostać wykonane.
18. **Dlaczego test nowej implementacji wyłącznie przez porównanie ze starą jest niewystarczający?** Stara implementacja może zawierać ten sam błąd. Potrzebne są niezależne oczekiwania wynikające z zatwierdzonego kontraktu.
19. **Kiedy Rename wymaga strategii migracji zamiast jednorazowej operacji IDE?** Gdy nazwa należy do publicznego API albo występuje w refleksji, danych, konfiguracji, serializacji lub u niezależnie wdrażanych klientów.
20. **Dlaczego Extract i Inline nie są sprzecznymi zaleceniami?** Obie zmieniają poziom pośrednictwa. Extract nadaje nazwę i granicę, a Inline usuwa pośrednictwo, które nie przekazuje znaczenia. Wybór zależy od kontekstu.
