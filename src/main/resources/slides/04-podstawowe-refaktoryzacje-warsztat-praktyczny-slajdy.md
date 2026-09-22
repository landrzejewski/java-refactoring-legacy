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
# Moduł 4. Podstawowe refaktoryzacje - warsztat praktyczny
Małe transformacje, precyzyjna semantyka: od Rename do Extract Class i hermetyzacji stanu

---

## Agenda

1. Kontrakt i rytm małych transformacji
2. Rename, Extract Method, Extract Variable i Extract Constant
3. Replace Magic Numbers with Named Constants
4. Inline Variable i Inline Method
5. Move Method, Move Field i Extract Class
6. Encapsulate Field, Encapsulate Collection, Encapsulate Conditional
7. Studium przypadku w Javie 25: generator oferty wynajmu
8. Warsztat praktyczny, omówienie, sprawdzenie wiedzy i listy kontrolne

---

## Co ma pozostać niezmienione

- Refaktoryzacja zmienia strukturę kodu **bez zmiany ustalonego, obserwowalnego zachowania** - ale zakres obserwacji zależy od systemu.
- W aplikacji wdrażanej atomowo można zmienić wewnętrzną sygnaturę i jednocześnie zaktualizować wszystkich kontrolowanych klientów.
- Dla publicznej biblioteki kontrakt obejmuje dodatkowo m.in.: kompatybilność źródłową i binarną, nazwy używane przez refleksję, format serializacji, nazwy w konfiguracji i skryptach.
- Częścią kontraktu mogą być też kolejność wyjątków i efektów ubocznych, blokady, transakcje, proxy, a nawet wydajność.
- Zielony test jednostkowy **nie dowodzi** kompatybilności binarnej ani integracji z frameworkiem - test musi działać na poziomie, na którym istnieje ryzyko.

---

## Refaktoryzacja a zmiana kontraktu

- Niektóre wartościowe zmiany **nie są** czysto strukturalne: dodanie walidacji, zmiana publicznego pola na prywatne bez migracji klientów, zmiana typu wyjątku.
- Zastąpienie żywej kolekcji niemodyfikowalną migawką albo wykonanie kopii defensywnej zmienia to, co widzą klienci.
- Zmiana kolejności lub miejsca zaokrąglenia, usunięcie publicznej metody po przeniesieniu czy zmiana nazwy w formacie danych to zmiany zachowania.
- **Wniosek:** najpierw przenieś strukturę z zachowaniem starego kontraktu, a dopiero potem wykonaj świadomą zmianę zachowania z osobnym wymaganiem i testem.

---

## Pętla robocza i punkt zatrzymania

- **Pętla:** wybierz jedną obserwowalną właściwość → potwierdź, że test wykrywa kontrolowaną zmianę → jedna transformacja → kompilacja → najwęższy wiarygodny test → mały commit.
- Okresowo uruchamiaj pełny build i testy integracyjne, bo test lokalny nie widzi wszystkich ryzyk.
- IDE poprawnie przepisuje składnię i odwołania ze swojego modelu programu, ale nie zna kontraktów zewnętrznych, ciągów tekstowych ani wymagań biznesowych.
- Seria refaktoryzacji potrzebuje **konkretnego celu**: nazwania reguły, umożliwienia testu, przeniesienia odpowiedzialności, ograniczenia mutacji lub przygotowania zmiany funkcjonalnej.
- Extract i Inline zmieniają poziom pośrednictwa w przeciwnych kierunkach - właściwy poziom zależy od kontekstu.

---

## Rename - cel

Rename zmienia nazwę tak, aby opisywała **rolę w kontekście**, a nie typ techniczny ani bieżącą implementację.

| Słaba nazwa | Lepszy kierunek | Uzasadnienie |
| --- | --- | --- |
| `d` | `discount` / `deliveryDate` | znaczenie z domeny, nie z litery |
| `data` | `approvedPriceBreakdown` | określa zawartość i stan |
| `process()` | `createRentalQuote()` | operacja komunikuje rezultat |
| `manager` | konkretna odpowiedzialność | ogólna rola ukrywa powód zmiany |
| `list` | `dailyRates` | kolekcja nazwana wg zawartości |

---

## Rename - bezpieczna mechanika i granice automatyzacji

- Ustal znaczenie elementu i zasięg kontraktu nazwy, wyszukaj użycia **typowane i tekstowe**, a dla symbolu lokalnego użyj automatycznego Rename z podglądem.
- Skompiluj cały moduł i uruchom testy integracji zależne od nazwy; dla publicznego API rozważ starą metodę delegującą oznaczoną jako przestarzała.
- Nazwa żyje też poza Javą: `getDeclaredMethod`, konfiguracja frameworka, JSON/XML/CSV, SQL i ORM, szablony, metryki, dane serializowane, niezależni klienci.
- Zmiana nazwy publicznej metody może skończyć się błędem linkowania u klienta, a zmiana nazwy pola - zmianą refleksji i serializacji. Wtedy potrzebna jest **strategia migracji**, nie tylko operacja IDE.
- Trudność z nazwaniem metody często sygnalizuje kilka odpowiedzialności i potrzebę Extract Method lub Extract Class.

---

## Extract Method - cel i sygnały

- Extract Method przenosi spójny fragment do nazwanej metody, gdy nazwa wyrazi intencję lepiej niż szczegóły lub fragment jest samodzielną odpowiedzialnością.
- Sygnały: komentarz opisuje cel bloku, metoda miesza poziomy abstrakcji, ta sama wiedza powtarza się w kilku miejscach.
- Kolejne sygnały: predykat ma nazwę domenową, osobny test fragmentu istotnie zmniejszy ryzyko, a przeniesienie odpowiedzialności wymaga jej odseparowania.
- **Długość nie jest wystarczającym kryterium** - krótki predykat może zasługiwać na nazwę, a długa liniowa transformacja bywa czytelna jako całość.

---

## Extract Method - analiza przepływu danych

| Sytuacja | Typowe rozwiązanie |
| --- | --- |
| wartość tylko odczytywana | parametr lub spójny obiekt |
| wartość powstaje we fragmencie i jest używana później | wartość zwracana |
| modyfikowany jeden obiekt | jawna operacja, jeśli mutacja należy do kontraktu |
| modyfikowanych kilka wartości lokalnych | podziel fragment lub obiekt wyniku |
| używa wielu pól innego obiektu | możliwy sygnał Move Method |
| lista parametrów nadmiernie rośnie | zła granica lub brak spójnego pojęcia |

Przekazanie całego obiektu „na wszelki wypadek” ukrywa zależności, a rozbicie go na kilkanaście prymitywów zwiększa sprzężenie - zachowaj granicę semantyczną.

---

## Extract Method - mechanika i trudne przypadki

- **Mechanika:** najmniejszy spójny fragment → nazwa rezultatu → parametry i wynik → kopia bez upraszczania → wywołanie → test → dopiero potem poprawa nowej metody.
- Szczególnej analizy wymagają `return`, `break`, `continue` i etykiety przekraczające granicę fragmentu oraz modyfikacja kilku zmiennych lokalnych.
- Ryzykowne są też `try-with-resources`, zasięg `catch`/`finally`, wyjątki kontrolowane w `throws` i sekcje `synchronized`.
- Uwaga na `this`, `super`, metody przesłaniane, zmienne przechwytywane przez lambdy, generyki i przeciążenia oraz efekty ukryte w getterach.
- Wydzielenie nie może przesunąć efektu przed warunek, zmienić liczby wywołań ani wydłużyć czasu trzymania zasobu lub blokady.

---

## Extract Variable

- Extract Variable zapisuje wynik wyrażenia w nazwanej zmiennej lokalnej - rozdziela etapy obliczenia, nazywa pojęcie i ułatwia debugowanie.
- Dobra zmienna wyjaśniająca nazywa **znaczenie, nie składnię**, ma najmniejszy potrzebny zasięg i nie jest wielokrotnie nadpisywana.
- Nie powinna ukrywać istotnej mutacji ani jedynie powtarzać nazwy dobrze nazwanej metody.
- Przeniesienie wyrażenia do zmiennej może zmienić **moment ewaluacji**, a zastąpienie kilku wystąpień jedną zmienną - **liczbę ewaluacji**.

---

## Liczba i moment ewaluacji

- Ma to znaczenie, gdy wyrażenie zmienia stan, odczytuje czas lub losowość, sięga do sieci lub bazy albo inkrementuje licznik.
- Istotne jest też, gdy wyrażenie może rzucić wyjątek lub zależy od mutowalnego stanu zmienianego między użyciami.
- Zamiana dwóch wywołań `clock.instant()` na jeden lokalny `now` może być dobrą decyzją, ale **nie jest neutralna semantycznie**.
- Najpierw trzeba ustalić, czy kontrakt wymaga jednego snapshotu czasu, czy dwóch niezależnych odczytów.

---

## Extract Constant i `final`

- Extract Constant przenosi stabilną wartość do nazwanego pola (zwykle `static final`), gdy reprezentuje wspólne pojęcie, nie zależy od instancji i ma tego samego właściciela.
- Nie zamieniaj w stałą konfiguracji środowiska, danych regulacyjnych aktualizowanych bez wdrożenia ani reguły z innego kontekstu.
- `final` blokuje tylko ponowne przypisanie - `static final List` może wskazywać listę, którą da się modyfikować.
- Publiczne `static final int` lub `String` z wyrażeniem stałym to **stała czasu kompilacji**: może zostać wklejona do klienta, który do rekompilacji widzi starą wartość.
- `static final BigDecimal` nie jest stałą czasu kompilacji - referencja jest nieprzypisywalna, ale wartość nie jest wklejana do kodu klienta.

---

## Replace Magic Numbers with Named Constants

- Magiczny literał to wartość, której znaczenia nie da się wiarygodnie odczytać w miejscu użycia - problemem jest **ukryta decyzja**, nie sama liczba.
- W studium przypadku: `7` = próg długiego wynajmu, `0.10` = stawka rabatu, `8.00` = dzienne ubezpieczenie, `25.00` = dostawa, `0.23` = VAT.
- Nazwy opisują rolę (`LONG_RENTAL_DAYS`), a nie wartość (`SEVEN`); `0` i `1` w indeksowaniu czy liczniku zwykle nie wymagają stałej.
- Dwa identyczne literały nie muszą być tą samą wiedzą: 7 dni na zwrot i 7 dni progu rabatu to **osobne pojęcia**.
- Extract Constant to mechanika, Replace Magic Literal to powód - sama transformacja nie gwarantuje dobrej nazwy ani właściwego właściciela.

---

## Inline Variable

- Inline Variable zastępuje odczyty zmiennej jej inicjalizatorem, gdy nazwa nic nie wnosi, powtarza dobrze nazwaną metodę lub blokuje dalszą refaktoryzację.
- Warunki: brak ponownego przypisania, ten sam wynik w każdym miejscu, zgodna liczba i moment ewaluacji, typ nie steruje przeciążeniem, nazwa nie jest ważnym pojęciem.
- Mechanika: zastępuj **jedno użycie na raz**, kompiluj i testuj, a deklarację usuń dopiero po zniknięciu wszystkich odwołań.
- Inicjalizator wykonany raz i czytany trzy razy - po naiwnym Inline wykona się trzy razy; podobnie przy przeniesieniu do pętli lub gałęzi warunkowej.

---

## Typ docelowy i przeciążenia

- Jawny typ lokalny może wybrać **inne przeciążenie** niż `var`.
- Pusta kolekcja może brać typ z parametru metody, a po wydzieleniu jako `var` stać się kolekcją `Object`.
- Lambda i referencja do metody wymagają typu docelowego i nie mogą być samodzielnym inicjalizatorem `var`.
- Typ prymitywny i opakowanie mogą uruchomić inne przeciążenie albo unboxing.
- Kompilator wykryje część różnic, ale kod skompilowany z innym przeciążeniem nadal może zmienić zachowanie - podgląd typu i test są częścią transformacji.

---

## Inline Method

- Inline Method zastępuje wywołania ciałem metody i usuwa ją, gdy nie wnosi użytecznej nazwy, tylko deleguje bez ochrony granicy lub zaciemnia przepływ.
- Typowi kandydaci to pozostałości po wcześniejszych transformacjach i metody utrudniające inną refaktoryzację.
- Metoda domenowa o prostej implementacji może mieć wysoką wartość: `qualifiesForLongRentalDiscount()` wyjaśnia decyzję lepiej niż porównanie z liczbą.
- **Liczba linii nie przesądza o Inline Method** - decyduje, czy pośrednictwo przekazuje znaczenie.

---

## Ryzyka Inline Method

- Parametr użyty dwa razy nie może stać się dwoma wywołaniami argumentu z efektem ubocznym.
- Wywołanie polimorficzne trafia do przesłoniętej implementacji; wklejenie ciała z jednej klasy **usuwa dynamiczną dyspozycję**.
- Instancyjna metoda `synchronized` blokuje monitor odbiorcy, a statyczna - obiekt `Class`; usunięcie wywołania może usunąć blokadę.
- Adnotacje transakcyjne, autoryzacyjne i przechwytujące działają na granicy metody; referencje do metod i refleksja nie wyglądają jak zwykłe wywołania.
- Najbezpieczniejszy kandydat: **prywatny, niepolimorficzny delegat użyty w jednym miejscu** - jak między `stage2` a `stage3` studium.

---

## Move Method i Move Field - wybór właściciela

- Move Method przenosi metodę do typu, który powinien odpowiadać za jej zachowanie; Move Field przenosi stan do właściciela o właściwej odpowiedzialności i cyklu życia.
- Sygnały: metoda częściej używa danych innego obiektu, zmiana reguły wymaga edycji klasy niebędącej właścicielem pojęcia.
- Pole odczytywane i modyfikowane głównie przez inny komponent oraz dane i zachowanie tworzące spójny klaster to kolejne wskazówki.
- **Feature Envy jest sygnałem do analizy, nie nakazem** - metoda może celowo koordynować kilka obiektów na poziomie przypadku użycia.

---

## Move Method krok po kroku i ryzyka

- Zabezpiecz testem wynik, wyjątki, efekty i kolejność; sprawdź `this`, `super`, prywatne pola, metody wirtualne i zasoby.
- Utwórz metodę w klasie docelowej z najmniejszą widocznością, przenieś ciało bez zmiany algorytmu, a starą metodę zamień w **delegat**.
- Migruj wywołania pojedynczo; delegat usuń tylko, gdy nie jest częścią wymaganego kontraktu - może zostać jako fasada kompatybilności.
- Ryzyka: niekwalifikowane nazwy wiążą się z innymi polami, pominięcie przesłonięcia w podklasie, zmiana dostępu pakietowego i `protected`.
- Po przeniesieniu `synchronized` blokuje inny obiekt, a adnotacje transakcyjne mogą zostać na starym delegacie lub przestać być przechwytywane przez proxy.

---

## Move Field krok po kroku i ryzyka

- Znajdź wszystkie odczyty, zapisy i użycia refleksyjne; ustal liczność, własność i cykl życia wartości.
- Przenieś tę samą wartość lub referencję (zachowując aliasing), migruj najpierw odczyty, potem zapisy, a stare pole usuń po potwierdzeniu **jednego źródła prawdy**.
- Nie utrzymuj dwóch niezależnie zapisywalnych kopii - dual write rozjeżdża się po wyjątku, błędzie współbieżności lub pominiętym zapisie.
- Zachowaj znaczenie `final`, `volatile`, `transient`, `static`; kopia mutowalnego obiektu zmienia tożsamość, a pole statyczne - moment inicjalizacji klasy.
- Sprawdź serializację, ORM, `equals`/`hashCode`/`toString`, schemat danych i komponenty rekordu - przeniesienie komponentu rekordu zmienia jego API.

---

## Extract Class - cel i mechanika

- Extract Class tworzy klasę dla spójnego zestawu pól i operacji z **własną odpowiedzialnością i odrębnym powodem zmiany**; liczba linii to tylko sygnał.
- W studium: format dokumentu zmienia się z powodów prezentacyjnych, a reguły ceny z powodu polityki handlowej - stąd `RentalPricing`.
- Mechanika: utwórz prywatną lub pakietową klasę, dodaj jedną relację własności, przenoś pola pojedynczo, potem metody, a publiczne operacje źródła zostaw jako delegującą fasadę.
- Dopiero po ustabilizowaniu zdecyduj, czy nowy typ trafia do publicznego API.
- Extract Class to **decyzja projektowa**; Move Field i Move Method to mechanika jej realizacji.

---

## Extract Class - pytania o własność i zły wynik

- Czy nowy obiekt jest wartością, encją czy komponentem wewnętrznym? Czy ma ten sam cykl życia i czy może być współdzielony?
- Kto go tworzy i waliduje, czy potrzebuje referencji zwrotnej i czy jego tożsamość ma znaczenie dla `==`, monitorów lub cache?
- Referencja zwrotna tworzy cykl i zwiększa sprzężenie - często lepiej przekazać wymagane wartości lub wąski kontrakt.
- **Zły wynik:** dane przeniesione, ale logika nadal w źródle przez serię getterów - powstaje nawigacja bez przeniesienia odpowiedzialności.
- Jeśli nowa klasa potrzebuje większości pól starej i wzajemnej referencji, granica prawdopodobnie jest źle wybrana.

---

## Encapsulate Field

- Encapsulate Field kieruje dostęp do pola przez operacje właściciela, co pozwala kontrolować zmiany, utrzymywać inwariant i później zmienić reprezentację.
- Getter i setter nie są automatycznie dobrym modelem - operacja `renameTo` lepiej komunikuje dozwoloną zmianę niż ogólne `setName`.
- **Migracja:** dodaj trywialne akcesory na tym samym polu, bez walidacji i synchronizacji; migruj klientów pojedynczo; zwęź widoczność dopiero po migracji.
- Operację domenową zamiast ogólnego zapisu wprowadź w osobnym kroku - dodanie walidacji zmienia akceptowane dane i typy wyjątków.
- Pułapki: zapis do publicznego pola nie wywoła settera, pola wiązane są statycznie, a akcesory dynamicznie; `volatile`, blokady i adnotacje nie przechodzą same na metodę.

---

## Encapsulate Collection - cel

- Encapsulate Collection daje właścicielowi kontrolę nad członkostwem: klient czyta przez określony kontrakt, a zmienia przez `add`, `remove`, `replace` lub polecenia domenowe.
- Przed zmianą scharakteryzuj kolejność, duplikaty, dopuszczalność `null` i mutowalność elementów.
- Ważne są też tożsamość zwracanej kolekcji, widoczność późniejszych zmian właściciela i zachowanie iteratorów.
- Sprawdź alias do kolekcji przekazanej w konstruktorze i zasady współbieżności - każda z tych cech może być częścią kontraktu.

---

## Trzy różne kontrakty kolekcji

| Implementacja | Klient zmienia członkostwo | Widzi późniejsze zmiany | Kopia głęboka |
| --- | :---: | :---: | :---: |
| `Collections.unmodifiableList(internal)` | nie | tak (żywy widok) | nie |
| `List.copyOf(internal)` | nie | nie (migawka) | nie |
| `new ArrayList<>(internal)` | tak, we własnej kopii | nie | nie |

- Niemodyfikowalna kolekcja blokuje zmianę członkostwa, **nie wnętrza elementów** - mutowalny element nadal można zmienić.
- `List.copyOf` i `Map.copyOf` odrzucają `null`, a mogą zwrócić istniejący obiekt - nie opieraj kontraktu na tożsamości wyniku.

---

## Encapsulate Collection - bezpieczna mechanika

- Wykonaj kopię wejścia w konstruktorze **albo świadomie zachowaj** stary alias.
- Dodaj operacje kontrolujące członkostwo i zmigruj wszystkie zapisy klientów.
- Zastąp wynik odczytu wybranym widokiem lub migawką i dodaj test rozróżniający te semantyki.
- Wprowadzenie walidacji, zakazu `null` i nowych reguł duplikatów oddziel od samej hermetyzacji.
- Zwracanie migawki **nie zapewnia bezpieczeństwa wątkowego** całemu obiektowi - współbieżność wymaga osobnego kontraktu.

---

## Encapsulate Conditional

- Wydzielenie predykatu zastępuje pytanie o implementację pytaniem o znaczenie: `request.days() >= 7` → `qualifiesForLongRentalDiscount(request)`.
- Wariant Decompose Conditional wydziela osobno predykat, gałąź prawdziwą i fałszywą - każdy krok kompiluj i testuj oddzielnie.
- **Krótkie spięcie jest zachowaniem:** nie obliczaj wcześniej prawej strony osłony przed `null` ani niewybranej gałęzi - możesz wprowadzić wyjątek lub efekt uboczny.
- Przy `instanceof` z wzorcem zakres zmiennej zależy od przepływu - czasem trzeba zostawić dopasowanie w miejscu i wydzielić tylko resztę warunku.
- Nazwa `is`/`has`/`qualifies` nie gwarantuje czystości - jeśli predykat ma efekty, test musi chronić ich liczbę i kolejność.

---

## Studium przypadku: kontrakt generatora oferty

- Wiertnica kosztuje 39,99 za dzień, generator 120,00; wynajem ≥ 7 dni dostaje 10% rabatu **od podstawowego kosztu wynajmu**.
- Ubezpieczenie kosztuje 8,00 za dzień, dostawa jednorazowo 25,00, a VAT wynosi 23% wartości netto.
- Każda pośrednia kwota jest zaokrąglana do dwóch miejsc metodą HALF_UP; nazwa klienta jest przycinana i zamieniana na wielkie litery.
- Zapis nie zależy od domyślnego locale, a dokument kończy się znakiem nowej linii.
- Inny moment zaokrąglenia, rabat od dodatków czy brak końcowej nowej linii oznaczałyby **zmianę zachowania**; walidacja `RentalRequest` jest stabilnym warunkiem wstępnym wszystkich etapów.

---

## Etap 0: kod początkowy

```java
public String createQuote(RentalRequest r) {
    BigDecimal a = money(rates.get(r.equipmentType())
            .multiply(BigDecimal.valueOf(r.days())));
    BigDecimal d = r.days() >= 7
            ? money(a.multiply(disc)) : money(BigDecimal.ZERO);
    BigDecimal i = r.insurance()
            ? money(new BigDecimal("8.00")
                    .multiply(BigDecimal.valueOf(r.days())))
            : money(BigDecimal.ZERO);
    BigDecimal f = r.delivery() ? new BigDecimal("25.00") : money(BigDecimal.ZERO);
    BigDecimal n = money(a.subtract(d).add(i).add(f));
    BigDecimal v = money(n.multiply(new BigDecimal("0.23")));
    BigDecimal t = money(n.add(v));
    String q = "RENTAL QUOTE\n" + "Customer: " + ... + "Total: " + t.toPlainString() + "\n";
    return q;
}
```

Wynik jest poprawny, ale kod miesza reguły wyceny, formatowanie, nieczytelne nazwy i literały domenowe.

---

## Test charakterystyki przed pierwszą zmianą

- Test obejmuje **pełny dokument**, oba boki granicy rabatu (6 i 7 dni) oraz niezależne warianty ubezpieczenia i dostawy.
- Zawiera przypadki ujawniające sposób zaokrąglania VAT (1 dzień wiertnicy → VAT 9.20) i rabatu (15 dni → rabat 59.99).
- Test charakterystyki **nie ocenia**, czy zastane reguły są biznesowo pożądane - rejestruje obserwacje, aby zmiana struktury nie „poprawiła” ich przypadkiem.

```java
assertEquals("""
        RENTAL QUOTE
        Customer: ACME
        ...
        Net: 953.00
        VAT: 219.19
        Total: 1172.19
        """, service.createQuote(request));
```

---

## Etap 1: lokalne transformacje

- **Rename** nadaje znaczenie parametrowi i zmiennym, **Extract Constant** i **Replace Magic Numbers** nazywają próg rabatu, stawki dodatków i VAT.
- **Extract Variable** nazywa stawkę dzienną i liczbę dni; **Extract Method** oddziela rabat, ubezpieczenie, dostawę i dokument.
- **Encapsulate Conditional** nazywa kwalifikację do rabatu, a **Inline Variable** usuwa zmienną `q` tuż przed `return`.
- Po każdym kroku kod się kompiluje, a test charakterystyki pozostaje zielony.
- Wydzielenia zwiększyły liczbę nazw, ale **nie zmieniły kolejności obliczeń** - rabat nadal liczony od podstawy, zaokrąglenia w tych samych miejscach, format identyczny.

---

## Etap 1: wynik (fragment)

```java
private static final int LONG_RENTAL_DAYS = 7;
private static final BigDecimal VAT_RATE = new BigDecimal("0.23");

public String createQuote(RentalRequest request) {
    BigDecimal dailyRate = dailyRates.get(request.equipmentType());
    BigDecimal rentalDays = BigDecimal.valueOf(request.days());
    BigDecimal baseRentalCost = money(dailyRate.multiply(rentalDays));
    BigDecimal discount = calculateDiscount(request, baseRentalCost);
    BigDecimal insuranceCost = calculateInsuranceCost(request);
    BigDecimal deliveryCost = calculateDeliveryCost(request);
    BigDecimal netAmount = money(baseRentalCost.subtract(discount)
            .add(insuranceCost).add(deliveryCost));
    BigDecimal vat = money(netAmount.multiply(VAT_RATE));
    BigDecimal total = money(netAmount.add(vat));
    return buildDocument(request, baseRentalCost, discount, /* ... */ total);
}
```

---

## Extract Class: `RentalPricing` i `PriceBreakdown`

- Klasa etapu 1 ma dwie odpowiedzialności: **wycenę i prezentację**; reguły, stawki i zaokrąglenie trafiają do `RentalPricing`.
- Wynik obliczenia dostaje jawny typ `PriceBreakdown` - rekord z nazwanymi kwotami, którego konstruktor kompaktowy chroni format (nieujemne, skala 2).
- Sekwencja: prywatny punkt `calculatePrice` w źródle → nowy typ z implementacją i danymi → ciało źródła zamienione na delegowanie.
- Po każdym kroku usuwaj ze źródła tylko dane bez odczytów i utrzymuj **jedno źródło prawdy** dla każdej stawki i reguły.
- Konstruktor `RentalPricing` dodaje walidację nowego API (kompletność stawek, zakres rabatu) - dla poprawnych danych wynik jest ten sam, a w produkcji byłaby to osobna, świadoma decyzja.

---

## Etap 2: delegat po przeniesieniu

```java
public RentalQuoteService() { this(RentalPricing.standard()); }

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
```

- Bezargumentowy konstruktor zachowuje publiczną sygnaturę etapów 0 i 1 (także deskryptor dla skompilowanych klientów); jego usunięcie byłoby odrębną zmianą API.

---

## Etap 3: Inline Method

- Delegat `calculatePrice` nie dodaje nazwy domenowej, nie ukrywa niestabilnej implementacji i ma jedno wywołanie - jest kandydatem do Inline Method.
- Przed operacją sprawdzamy, czy nie jest punktem rozszerzenia, celem refleksji, miejscem przechwytywania przez proxy ani granicą synchronizacji - jest prywatny, niesynchronizowany i bez adnotacji.
- Po Inline: `PriceBreakdown price = pricing.calculate(request);`
- `buildDocument` **pozostaje**, bo nazywa osobną odpowiedzialność i trzyma metodę publiczną na jednym poziomie abstrakcji.
- `qualifiesForLongRentalDiscount` pozostaje w klasie wyceny, bo nazwa wyraża regułę domenową lepiej niż porównanie liczb.

---

## Test równoważności etapów

- Porównywanie nowej implementacji wyłącznie ze starą nie wystarcza - **obie mogą dzielić ten sam błąd**.
- Test parametryzowany porównuje każdy etap (0-3) z **niezależnie zapisanym, zatwierdzonym wynikiem**.
- Zestaw obejmuje oba typy sprzętu, oba boki progu rabatu, wszystkie dodatki i przypadki zaokrągleń - nie dowodzi poprawności dla wszystkich danych, ale chroni wybrane ryzyka.
- `LocaleIndependentFormattingTest` ustawia locale `ar-EG`; dlatego kod używa `String.format(Locale.ROOT, ...)` - samo `formatted(...)` mogłoby zmienić cyfry w `%d`.

```java
assertAll(
    () -> assertEquals(expectedQuote, stage0.createQuote(request)),
    () -> assertEquals(expectedQuote, stage3.createQuote(request)));
```

---

## Encapsulate Field i Collection w studium (1/2)

```java
public final class LegacyEquipmentCatalog {
    public String name;
    public final Map<EquipmentType, BigDecimal> dailyRates;
    // konstruktor przypisuje przekazaną mapę bez kopii
}
```

- `final` blokuje zmianę referencji, ale nie mutację mapy - klient może zmienić nazwę, stawkę, usunąć wpis lub modyfikować mapę wejściową.
- `AccessorBasedEquipmentCatalog` ukrywa pola po migracji klientów, ale akcesory **zachowują aliasowanie** i akceptują każdą wartość - to punkt kontrolny, nie projekt docelowy.
- W opublikowanej bibliotece usunięcie publicznego pola łamie zgodność źródłową i binarną oraz zmienia refleksję - akcesor nie zachowuje API pola.

---

## Encapsulate Field i Collection w studium (2/2)

```java
public void renameTo(String newName) { name = validName(newName); }

public void changeDailyRate(EquipmentType type, BigDecimal newRate) {
    BigDecimal normalizedRate = newRate.setScale(2, RoundingMode.HALF_UP);
    if (normalizedRate.signum() <= 0) throw new IllegalArgumentException(...);
    dailyRates.put(type, normalizedRate);
}

public Map<EquipmentType, BigDecimal> dailyRates() {
    return Map.copyOf(dailyRates);
}
```

- `EquipmentCatalog` to już **świadoma zmiana kontraktu**: kopia wejścia do `EnumMap`, normalizacja, inwarianty i niemodyfikowalne migawki.
- Test nazywa każdą właściwość (alias, migawka, walidacja nazwy, normalizacja przed inwariantem); zmiana wymaga osobnego wymagania i komunikacji do klientów.

---

## Uruchamialny przykład

- `Module4Examples` porównuje wynik etapu 0 z etapem 3 dla tego samego żądania i rzuca wyjątek, jeśli refaktoryzacja zmieniła ofertę.
- Następnie demonstruje, że migawka katalogu pobrana przed `changeDailyRate` zachowuje starą stawkę 39.99.
- Oczekiwany wynik kończy się liniami: `Quote stages equivalent: true` oraz `Catalog snapshot isolated: true`.
- Przykład pokazuje oba bieguny modułu: **równoważność** etapów refaktoryzacji i **jawną zmianę** kontraktu katalogu.

---

## Warsztat - zasady pracy

- Każde ćwiczenie zaczyna się od **zielonego testu**.
- Kolejne kroki zapisuj osobno i po każdym uruchamiaj co najmniej test najbliższy zmienianemu kodowi.
- Jeśli IDE odmawia wykonania operacji, najpierw ustal jej warunek wstępny - ręczne przepisanie kodu bez tej analizy nie rozwiązuje problemu.

---

## Ćwiczenie 1: lokalne porządkowanie metody (30 + 10 min)

**Start:** `stage0.RentalQuoteService` + `RentalQuoteServiceCharacterizationTest`

- Uruchom test, celowo zmień próg rabatu i sprawdź, czy test to wykrywa; cofnij zmianę.
- Zmień nazwy, nazwij próg rabatu, stawkę ubezpieczenia, opłatę dostawy i VAT; wydziel rabat, ubezpieczenie, dostawę, predykat i budowę dokumentu.
- Usuń zmienne, które tylko przekazują wynik dalej. **Nie** zmieniaj sygnatury `createQuote`, miejsc zaokrąglania ani nie twórz nowej klasy.
- **Akceptacja:** dokument identyczny bajt po bajcie, 6 dni bez rabatu / 7 z rabatem, VAT 9,20 dla 1 dnia wiertnicy, brak jednoliterowych nazw, spójny poziom abstrakcji.

---

## Ćwiczenie 2: Extract i Inline pod presją semantyki (25 + 10 min)

Dla każdego przypadku oceń, czy transformacja zachowuje zachowanie, i jaki test jest potrzebny:

- Inline zmiennej `now` z `clock.instant()` użytej dwa razy; Extract Variable prawego operandu `account != null && account.isActive()`.
- Argument z zapisem audytowym trafia do dwóch wywołań; lambda przekazywana do przeciążonej metody wydzielona do `var` lub jawnego typu.
- Inline jednoliniowej metody przesłanianej w podklasie oraz metody `synchronized` (instancyjnej i statycznej).
- Inline prywatnego delegata `calculatePrice`; Extract Constant dla dwóch `new BigDecimal("0.23")`.
- Następnie wykonaj Inline Method między etapami 2 i 3 i uruchom `RentalQuoteStagesEquivalenceTest`.

---

## Ćwiczenie 3: Extract Class i przeniesienie odpowiedzialności (35 + 15 min)

**Start:** `stage1.RentalQuoteService`

- Nazwij wydzielaną odpowiedzialność i klientów; wprowadź `PriceBreakdown` i prywatny punkt `calculatePrice`.
- Utwórz `RentalPricing`, przenieś metody wyceny ze stawkami i stałymi, formatowanie zostaw w `RentalQuoteService`.
- Dodaj konstruktor wstrzykujący wycenę, zachowaj bezargumentowy punkt wejścia, usuń duplikaty i dodaj testy.
- Przed startem odpowiedz: kto jest właścicielem stawek, jaki jest cykl życia wyceny, czy są monitory, przesłonięcia, adnotacje i zewnętrzni klienci?
- **Akceptacja:** jedno źródło prawdy, dokument nie zna algorytmu wyceny, wycena nie zna formatu, zależności jawne, oferty identyczne na każdym etapie.

---

## Ćwiczenie 4: kontrola mutowalnego stanu (30 + 15 min)

**Start:** `LegacyEquipmentCatalog`

- **Faza A (zachowanie kontraktu):** scharakteryzuj zapis do `name` i alias do mapy, ukryj pola, dodaj najwęższe akcesory zachowujące obserwacje, zmigruj klientów.
- **Faza B (zatwierdzona zmiana):** zdecyduj o kopii w konstruktorze, zdefiniuj reguły nazwy i stawki, zastąp setter operacjami domenowymi.
- W fazie B wybierz żywy widok lub migawkę, dodaj testy modyfikacji źródła, wyniku i właściciela, oceń `null`, mutowalne elementy i współbieżność.
- **Akceptacja:** faza A nie ukrywa zmiany zachowania, faza B ma jawny kontrakt, brak modyfikowalnego aliasu, test odróżnia migawkę od widoku.

---

## Wspólna retrospektywa

Po każdym ćwiczeniu zespół odpowiada na cztery pytania:

1. Jaka obserwowalna właściwość była chroniona?
2. Który krok był czysto mechaniczny, a który wymagał decyzji projektowej?
3. Jaki najmniejszy test dawał wiarygodny sygnał?
4. W którym momencie należało zakończyć serię transformacji?

---

## Omówienie: Ćwiczenie 1

- Kierunek odpowiada etapowi 1; zaczynamy od **Rename lokalnych symboli**, bo zmienia najmniejszy obszar i ułatwia kolejne kroki.
- Potem Extract Constant i Replace Magic Numbers (ujawniają reguły bez zmiany przepływu), Extract Variable, Extract Method i Encapsulate Conditional.
- Na końcu Extract Method dla dokumentu i Inline Variable dla `q`, która tylko poprzedzała `return`.
- Nie trzeba wydzielać każdej operacji - `netAmount`, `vat`, `total` wystarczająco opisują kroki, a nadmiar jednoliniowych metod utrudniłby czytanie.
- `ZERO_MONEY` informuje o skali wartości zerowej, a stawki zostają w mapie instancyjnej, bo przejdą razem z odpowiedzialnością za wycenę.

---

## Omówienie: Ćwiczenie 2 (1/2)

| Przypadek | Ocena | Uzasadnienie |
| --- | --- | --- |
| Inline `now` z `clock.instant()` | niebezpieczne | jeden odczyt czasu staje się dwoma |
| Wcześniejsze `account.isActive()` | niebezpieczne | znika krótkie spięcie, wyjątek dla `null` |
| Argument z efektem w 2 wywołaniach | niebezpieczne | zmienia liczbę i kolejność efektów |
| Lambda do `var` | niepoprawne | lambda wymaga typu docelowego |

---

## Omówienie: Ćwiczenie 2 (2/2)

| Przypadek | Ocena | Uzasadnienie |
| --- | --- | --- |
| Inline metody przesłanianej | zwykle niebezpieczne | traci dynamiczną dyspozycję |
| Inline `synchronized` | niebezpieczne bez blokady | ginie monitor odbiorcy / `Class` |
| Inline delegata etapu 2 | bezpieczne | jedno wywołanie, brak proxy i blokad |
| VAT jako stała | bezpieczne, jeśli ta sama reguła | centralizuj wiedzę, nie tekst |

---

## Omówienie: Ćwiczenia 3 i 4

- `RentalPricing` oblicza nazwany rozkład ceny dla poprawnego żądania; `RentalQuoteService` buduje dokument z żądania i wyniku wyceny.
- Pola stawek i rabatu poruszają się razem z metodami, które je interpretują; formatowanie i normalizacja nazwy klienta zostają po stronie prezentacji.
- `PriceBreakdown` zastępuje długą listę równoległych wartości spójnym typem wyniku, a przejściowy delegat pozwala osobno sprawdzić Move Method i Inline Method.
- W ćwiczeniu 4 **fazy A i B nie łączymy w jeden commit**: faza A migruje składnię dostępu bez zmiany aliasowania, faza B wprowadza kopię, walidację i migawki.
- Gdyby klient musiał widzieć późniejsze zmiany, właściwy byłby żywy widok (`Collections.unmodifiableMap`) - to inny kontrakt, wybierany świadomie.

---

## Sprawdzenie wiedzy (1/2)

- **Co musi pozostać niezmienione?** Ustalony zakres obserwowalnego zachowania: wyniki, wyjątki, efekty, kolejność, a czasem zgodność API.
- **Kiedy Extract Variable zmienia zachowanie?** Gdy zmienia liczbę, kolejność lub moment ewaluacji - wpływając na wyjątki, stan, czas, I/O lub przeciążenie.
- **Dlaczego publiczna stała `int`/`String` wymaga ostrożności?** Może zostać wkompilowana do klienta, który bez rekompilacji widzi starą wartość.
- **Co ginie przy Inline metody `synchronized`?** Wejście w monitor odbiorcy lub obiektu `Class`, a z nim synchronizacja i widoczność pamięci.
- **Jak bezpiecznie wykonać Move Field?** Nowy właściciel, jedno źródło prawdy dla odczytów i zapisów, migracja klientów, usunięcie starego pola na końcu.

---

## Sprawdzenie wiedzy (2/2)

- **Dlaczego prywatne pole z publicznym setterem to nie hermetyzacja?** Setter nadal pozwala na dowolną zmianę - hermetyzacja to kontrola kontraktu, nie modyfikator dostępu.
- **Widok a migawka?** Widok blokuje mutację klienta, ale pokazuje późniejsze zmiany właściciela; migawka utrwala stan z chwili utworzenia.
- **Czy `Map.copyOf` kopiuje głęboko lub daje bezpieczeństwo wątkowe?** Nie i nie - kopiuje tylko strukturę i nie definiuje synchronizacji.
- **Dlaczego porównanie ze starą implementacją nie wystarcza?** Stara wersja może mieć ten sam błąd - potrzebne są niezależne oczekiwania.
- **Czy Extract i Inline są sprzeczne?** Nie - obie zmieniają poziom pośrednictwa, a wybór zależy od tego, czy nazwa przekazuje znaczenie.

---

## Listy kontrolne (1/2)

- **Przed każdym krokiem:** znam chronione zachowanie, test pokrywa ryzyko, sprawdziłem liczbę i kolejność ewaluacji, wyjątki, efekty i publiczny/tekstowy kontrakt symbolu.
- **Extract:** nazwa wyraża intencję, wejścia i wyjścia są jawne, short-circuit i zasięg zmiennej wzorca zachowane, typ jawny zachowuje kontekst przeciążenia i lambdy.
- **Inline:** pośrednictwo nie niesie znaczenia, metoda nie jest przesłaniana ani używana przez refleksję, nie usuwam granicy proxy, transakcji ani blokady.
- **Rename:** nazwa opisuje rolę, przejrzałem użycia tekstowe, konfigurację, serializację, ORM i refleksję; dla publicznego API stara nazwa deleguje w okresie migracji.

---

## Listy kontrolne (2/2)

- **Move i Extract Class:** nowy właściciel ma odpowiedzialność i dane, cykl życia się zgadza, cały czas jest jedno źródło prawdy, brak nowych cykli zależności.
- **Encapsulate Field i Collection:** znam aliasy i drogi zapisu, nie utożsamiam settera z hermetyzacją, świadomie wybrałem widok, migawkę lub kopię.
- Uwzględniam `null`, kolejność, duplikaty, mutowalność elementów i nie przypisuję kolekcji niemodyfikowalnej bezpieczeństwa wątkowego.
- **Na koniec warsztatu:** każdy etap kompiluje się na Javie 25, testy charakterystyki, nowej odpowiedzialności i równoważności przechodzą, nie ma dwóch kopii reguły.
- Potrafię wskazać, które kroki były refaktoryzacją, a które **zmianą zachowania**.

---

## Podsumowanie - najważniejsze wnioski

- Podstawowe refaktoryzacje to małe transformacje, ale ich bezpieczeństwo zależy od **precyzyjnego rozumienia semantyki** Javy i kontraktu systemu.
- Rename może dotknąć publicznego lub tekstowego kontraktu; Extract i Inline mogą zmienić liczbę ewaluacji, przeciążenie, dynamiczną dyspozycję lub blokadę.
- Move wymaga jednego źródła prawdy i właściwego właściciela; hermetyzacja wymaga decyzji o dozwolonych operacjach, aliasowaniu i własności stanu.
- Kluczowa umiejętność to nie nazwa techniki, lecz **sekwencja kroków** z jawnym celem, odpowiednim testem i małym promieniem zmiany.
- Refaktoryzację oceniamy przez równoważność obserwacji (etapy 0-3), a nowe reguły własności i walidacji wymagają **osobnej, świadomej decyzji**.
