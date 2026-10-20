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
# Moduł 2. Prewencja
Jak krótkie pętle informacji zwrotnej, świadome decyzje, akceptacja ograniczeń i projekt aplikacji hamują degradację jakości kodu

---

## Agenda

1. Fast feedback
2. Podejmowanie decyzji i odsuwanie ich w czasie
3. Akceptacja jako sposób na radzenie sobie z problemami, których nie rozwiążesz
4. Czy można uniknąć degradacji jakości kodu?
5. Projektowanie aplikacji jako sposób na kontrolę degradacji jakości
6. Prewencja ważniejsza niż leczenie
7. Ćwiczenia, sprawdzenie wiedzy, lista kontrolna prewencji

---

## 1.1. Pętle informacji zwrotnej: od sekund do dni

| Pętla (czas) | Co wykrywa | Czego nie wykryje |
| --- | --- | --- |
| kompilator, IDE (sekundy) | typy, ostrzeżenia, nieużywane symbole | reguły biznesowe |
| szybkie testy (sekundy) | reguły, granice, zaokrąglenia | konfigurację, mapowanie |
| testy integracyjne (minuty) | adaptery, schemat, format | obciążenie |
| CI (minuty) | konflikty integracji, pełny zestaw | intencję i projekt |
| przegląd kodu (godziny - dni) | intencję, projekt, ryzyko | regresje bez testów |
| produkcja (dni - tygodnie) | realne dane i obciążenie | przyczynę w kodzie |

**Zasada:** każdy problem, który da się złapać w szybszej pętli, łap tam - wolne pętle zostaw na to, czego szybkie nie widzą.

---

## 1.2. Koszt opóźnionej informacji

- Im dłuższa pętla, tym więcej zmian między przyczyną a sygnałem - diagnoza zamienia się w śledztwo.
- Utrata kontekstu: po kilku dniach autor nie pamięta założeń, a poprawka często trafia do innej osoby.
- Wolna pętla wymusza większe partie: rzadziej uruchamiane testy, więcej zmian naraz, trudniejsze wycofanie.
- Koszt rośnie szybciej niż czas, ale gotowe mnożniki („x10 na etap”) to folklor - mierz własne dane: czas od commita do sygnału i czas naprawy.
- Przypomnienie z modułu 1: kod w godzinę, pewność w pięć dni - problemem jest koszt informacji zwrotnej, nie pisania.

---

## 1.3. Małe kroki

1. Wykonaj najmniejszą transformację (IDE lub mała zmiana ręczna).
2. Skompiluj i uruchom najszybsze testy zmienionego zachowania.
3. Obejrzyj różnicę, zapisz punkt przywracania.
4. Powtarzaj; regularnie szerszy zestaw, na końcu pełna weryfikacja.

- **Dlaczego:** przy porażce głównym podejrzanym jest ostatnia, mała transformacja.
- Mały krok to **jedna spójna intencja** z testami, nie limit linii.
- Przerwij i wróć do znanego stanu, gdy diff rośnie szybciej niż zrozumienie - przerwanie to mechanizm kontroli ryzyka, nie porażka.

---

## 1.4. Różne narzędzia, różne dowody

| Narzędzie | Główna rola | Czego samo nie dowodzi |
| --- | --- | --- |
| refaktoryzacja IDE | transformacja oparta na symbolach | zgodności klientów zewnętrznych i użyć dynamicznych |
| kompilator | typy, składnia, ostrzeżenia | poprawności reguł biznesowych |
| analiza statyczna | wzorce ryzyka | braku fałszywych wyników |
| testy | zgodność dla wykonanych scenariuszy | zachowania poza testem |
| przegląd kodu | intencja, projekt, ryzyko | braku regresji |

- IDE nie widzi refleksji, konfiguracji, szablonów, nazw serializowanych ani konsumentów spoza repozytorium.
- Bezpieczna bramka łączy **kilka niezależnych sygnałów**.

---

## 1.5. Kompilator jako najszybsza bramka

```java
private static List<String> compilerOptions(WarningPolicy warningPolicy) {
    var options = new ArrayList<>(BASE_OPTIONS); // --release 25, -proc:none, -Xlint:rawtypes
    if (warningPolicy == WarningPolicy.TREAT_WARNINGS_AS_ERRORS) {
        options.add("-Werror");
    }
    return List.copyOf(options);
}
```

- `InMemoryJavaCompiler` zwraca strukturalną diagnostykę (kod, pozycja) - ten sam surowy typ przechodzi z ostrzeżeniem albo zatrzymuje build, zależnie od polityki.
- `--release 25` ustala poziom języka, ale nie JDK uruchamiający Mavena - kontroluj go w CI.
- `-Xlint` + `-Werror` to silna bramka, ale tylko dla zespołu, który już ma zero ostrzeżeń.

---

## 1.6. Bramki w legacy: stan bazowy i ratchet

- Nagle włączone `-Werror` lub analizator zablokuje zespół historycznymi naruszeniami.
- Kolejność: **stan bazowy → brak nowych naruszeń → stopniowa redukcja**; tłumienie wąskie i uzasadnione.
- Checkstyle (konwencje), PMD (AST), SpotBugs (kod bajtowy) - **różne klasy problemów**, nie są zamienne.
- Formatowanie całego repozytorium oddziel od logiki albo stosuj ratchet dla dotykanych plików.
- Minimalna bramka: podgląd diffu → kompilacja na docelowym JDK → testy zawężone i pełne → analiza statyczna i przegląd → obserwacja wdrożenia.
- Bramka bez właściciela, która daje fałszywe alarmy, zostanie wyłączona.

---

## Aktywność 2.1: zmapuj swoje pętle (3 min)

Dla projektu, nad którym pracujesz:

1. Ile trwa każda pętla z tabeli 1.1 - od zapisania pliku do sygnału?
2. Która **najwolniejsza** pętla najczęściej wykrywa problemy?
3. Jaki jeden problem z tej pętli dałoby się złapać w szybszej?

**Zasada:** podawaj zmierzony albo szacowany czas z przykładem, nie „długo”.

---

## 2.1. Ostatni odpowiedzialny moment

- Decyzja podjęta **za wcześnie** opiera się na zgadywaniu; podjęta **za późno** - zapada sama, przez zaniechanie.
- **Ostatni odpowiedzialny moment** (Poppendieck): chwila, po której brak decyzji eliminuje ważną opcję.
- To nie „odkładaj w nieskończoność”: trzeba wiedzieć, kiedy opcja wygasa, i wykonywać pracę, która ją podtrzymuje.
- Odraczanie działa tylko w systemie podatnym na zmianę: testy, małe kroki, granice.
- Nie każda decyzja może czekać: bezpieczeństwo, zgodność, kontrakt publikowany na zewnątrz.

---

## 2.2. Decyzje odwracalne i nieodwracalne

| Drzwi dwukierunkowe (odwracalne) | Drzwi jednokierunkowe (trudne do cofnięcia) |
| --- | --- |
| nazwa i podział klas wewnętrznych | publiczne API używane przez zewnętrznych klientów |
| wybór algorytmu za stabilnym interfejsem | format danych trwałych i publikowanych zdarzeń |
| biblioteka pomocnicza za adapterem | współdzielony model danych kilku zespołów |
| pakiet, w którym leży klasa | wybór platformy, usunięcie danych |
| **decyduj szybko, eksperymentuj** | **decyduj wolniej: eksperyment, ADR, przegląd** |

- Wiele decyzji jest jednokierunkowych tylko dlatego, że brakuje testów i granic - **prewencja zamienia drzwi jednokierunkowe w dwukierunkowe**.
- Błąd symetryczny: traktowanie każdej decyzji jak nieodwracalnej paraliżuje zespół.

---

## 2.3. Utrzymywanie opcji

- Techniki: wąski interfejs na granicy niepewności, adapter dla zewnętrznej zależności, flaga z właścicielem i datą usunięcia, testy kontraktowe, modułowy podział.
- Opcja ma koszt: każda abstrakcja, flaga i adapter to kod do zrozumienia, testowania i utrzymania.
- Kupuj opcje tam, gdzie niepewność jest **realna i nazwana**, nie „na wszelki wypadek”.
- Pytanie kontrolne: jaką konkretną przyszłą zmianę ta opcja tanio umożliwi i co ją unieważni?
- Najtańsza opcja w kodzie legacy to często zwykły szybki test - pozwala odłożyć decyzję o strukturze bez ryzyka.

---

## 2.4. YAGNI: aktualna potrzeba przed przewidywaną

- **YAGNI**: nie buduj zdolności potrzebnej tylko dla przewidywanego wymagania.
- Przykłady: fabryka jednego typu, interfejs dla hipotetycznych implementacji, pluginy, nieużywane pola.
- Koszt: projekt i testy, opóźniona wartość, przebudowa, gdy potrzeba okaże się inna.
- **Nie zabrania:** refaktoryzacji, testów, CI, aktualnych wymagań bezpieczeństwa i wydajności.
- YAGNI to odraczanie decyzji o abstrakcji do ostatniego odpowiedzialnego momentu - działa, gdy kod jest podatny na zmianę.

---

## 2.5. Napięcia między zasadami

| Decyzja | DRY | KISS | YAGNI |
| --- | --- | --- | --- |
| nazwanie potwierdzonej reguły opłaty | jedno źródło wiedzy | brak synchronizacji | aktualna reguła |
| ogólny silnik hipotetycznych taryf | brak korzyści | więcej pojęć | przewidywana elastyczność |
| podobne reguły niezależnych działów | brak wspólnej wiedzy | lokalność prostsza | brak spekulacji |

**Reguła trzech** pomaga poczekać na kształt abstrakcji, ale nie definiuje DRY: dwie kopie tej samej reguły mogą wymagać natychmiastowej centralizacji.

---

## 2.6. Filtr decyzyjny przed dodaniem abstrakcji

1. Jakie aktualne wymaganie lub ryzyko ją uzasadnia?
2. Jaką wiedzę reprezentują podobne miejsca i czy zmieniają się dla tego samego właściciela?
3. Czy abstrakcja ma nazwę w języku problemu?
4. Czy upraszcza aktualny przypadek, czy tylko przyszły?
5. Ile nowych trybów i zależności wnosi i jak łatwo ją usunąć?

Odpowiedź „łatwo usunąć” oznacza drzwi dwukierunkowe - można spróbować. „Trudno usunąć” wymaga mocniejszego uzasadnienia i zapisu decyzji.

---

## Aktywność 2.2: drzwi jedno- czy dwukierunkowe? (4 min)

Sklasyfikuj decyzje i dla jednokierunkowych podaj, co uczyniłoby je odwracalnymi:

1. zmiana nazwy pakietu `pricing` na `tariffs` w aplikacji bez zewnętrznych klientów,
2. format pliku eksportu odbieranego co noc przez partnera,
3. wybór biblioteki do generowania PDF używanej w jednym adapterze,
4. usunięcie kolumny `discount_percent` z tabeli z historią zamówień,
5. wprowadzenie interfejsu `PricingRule` „bo kiedyś będą pluginy”,
6. przejście z `double` na `BigDecimal` w publicznym DTO biblioteki.

---

## 2.7. Zapis decyzji: ADR

- **ADR** dla decyzji o trwałym wpływie (granica modułu, własność danych, format kontraktu) - nie dla każdego Rename.
- Lekki rdzeń: tytuł, status, kontekst, decyzja, konsekwencje; lokalnie także rozważone opcje i weryfikacja.
- Zmiana kierunku: stary zapis oznacz jako zastąpiony, utwórz nowy - historia rozumowania zostaje.
- Przykład: `DecisionRecord` wymaga opcji i konsekwencji, renderer deterministycznie generuje Markdown.
- Regułę z ADR warto uczynić **wykonywalną** (test architektury) - build pilnuje decyzji na co dzień.
- Generator nie gwarantuje jakości decyzji - liczą się kontekst, uczciwe alternatywy i koszty.

---

## 3.1. Nie wszystko trzeba naprawić

- Celem nie jest kod bez długu, lecz **kontrola ryzyka i kosztu ewolucji**.
- **Spłacić** - usunąć konstrukcję; **ograniczyć** - testy, interfejs, monitorowanie, izolacja.
- **Unikać** - inna ścieżka zmiany; **tolerować** - małe odsetki lub obszar do wycofania.
- **Przenieść** - produkt lub usługa; **obserwować** - sygnał i termin ponownej oceny.
- **Akceptacja to nie ignorowanie:** decyzja jest jawna, ma właściciela i warunek, po którym wracamy do tematu.

---

## 3.2. Granice refaktoryzacji

- Refaktoryzacja nie naprawi platformy, która nie spełnia wymagań bezpieczeństwa lub skali.
- Nie naprawi procesu biznesowego, który zmienia się fundamentalnie, ani zależności bez wsparcia.
- Nie naprawi przyczyn organizacyjnych: presji terminów, komponentu bez właściciela, rotacji bez przekazania wiedzy.
- Nie naprawisz też kodu, którego nie kontrolujesz: biblioteki dostawcy, systemu innego zespołu, API partnera.
- Wtedy zostaje: wymienić komponent przy zachowaniu reszty, odizolować problem albo świadomie go zaakceptować.

---

## 3.3. Kwarantanna: izoluj problem za granicą

- Jeśli nie możesz naprawić modelu po drugiej stronie, **nie pozwól mu rozlać się po swoim kodzie**.
- Jedno miejsce tłumaczy obcy model, jednostki i błędy na pojęcia twojej domeny.
- Kwarantanna ma kontrakt i testy po twojej stronie; zmiana dostawcy dotyka jednego modułu.
- Wewnątrz granicy dług może zostać - świadomie, z ograniczonym promieniem.

```java
// wnętrze zna tylko własny port i własne typy
public interface CarrierQuotes {
    Optional<BigDecimal> quoteFor(Parcel parcel);
}
// adapter przewoźnika: gramy zamiast kilogramów, kwota w najmniejszych
// jednostkach, własne wyjątki SDK - wszystko tłumaczone tylko tutaj
```

Wzorzec Anti-Corruption Layer jako kierunek refaktoryzacji - moduł 5.

---

## 3.4. Świadomy rejestr długu

- Pozycja rejestru: lokalizacja, konsekwencja, decyzja (spłacić, ograniczyć, tolerować, obserwować), właściciel, sygnał i termin ponownej oceny (pełny szablon - moduł 1).
- Rozróżniaj: **zaakceptowane ryzyko** (prawdziwy problem, świadomie zostawiony), **wynik fałszywie dodatni** (narzędzie się myli), **reguła niedopasowana** (zmień konfigurację).
- Tłumienie ostrzeżenia w kodzie to też wpis rejestru: konkretna reguła, najmniejszy zakres, uzasadnienie, właściciel, termin.
- Rejestr, który tylko rośnie, jest listą życzeń - przegląd w rytmie planowania, zamykanie pozycji bez wartości.
- Stan bazowy bramki może tylko maleć.

---

## Aktywność 2.3: napraw, izoluj czy zaakceptuj? (4 min)

Dla każdego problemu wybierz reakcję i podaj warunek ponownej oceny:

1. SDK przewoźnika zwraca kwoty w groszach jako `long` i rzuca własne wyjątki.
2. Moduł raportów rocznych z wysoką złożonością zostanie wycofany za cztery miesiące.
3. Reguła rabatu VIP jest skopiowana w trzech miejscach, a w planie jest pięć zmian rabatów.
4. Kompilator zgłasza 10 ostrzeżeń `auxiliaryclass` w module, którego nikt nie zmienia.

**Zasada:** „zaakceptuj” wymaga właściciela i sygnału, inaczej to „zignoruj”.

---

## 4.1. Prawa Lehmana

- **Ciągła zmiana:** system używany w realnym świecie musi się zmieniać, inaczej staje się coraz mniej użyteczny.
- **Rosnąca złożoność:** gdy system ewoluuje, jego złożoność rośnie, chyba że wykonuje się pracę nad jej ograniczaniem.
- **Malejąca jakość:** postrzegana jakość spada, jeśli system nie jest aktywnie dostosowywany do zmieniającego się otoczenia.
- Lehman opisał je na podstawie obserwacji dużych systemów (lata 70.-90.) - to obserwacje empiryczne, nie prawa fizyki.
- Wniosek: degradacja nie jest wypadkiem ani winą jednej osoby - to **domyślny kierunek**; pytanie brzmi, ile pracy przeznaczamy na przeciwdziałanie.

---

## 4.2. Entropia i teoria rozbitych okien

- **Entropia oprogramowania:** każda zmiana bez dbałości o strukturę dokłada nieporządku; porządek wymaga stałej energii.
- **Rozbite okna** (Wilson i Kelling; w oprogramowaniu Hunt i Thomas): widoczne zaniedbanie obniża standard kolejnych zmian - „tu i tak jest bałagan”.
- Rozbite okna w kodzie: ignorowane ostrzeżenia, zakomentowany kod, stale czerwony test, `TODO` bez właściciela.
- Przeciwdziałanie: naprawiaj szybko albo jawnie oznacz (rejestr, tłumienie z terminem); **zero tolerancji dla nowych naruszeń, cierpliwość dla starych**.
- W kryminologii teoria jest dyskusyjna - w zespole traktuj ją jako heurystykę o normach, nie prawo.

---

## 4.3. Kod jest zapisem dawnych decyzji

- Rozsądna decyzja traci trafność: nowe wymagania, skala, bezpieczeństwo, granice zespołów.
- **Organizacja:** stała presja terminów, bodźce liczące tylko funkcje, komponent bez właściciela.
- **Wiedza:** rotacja szkodzi, gdy wiedza nie trafia do testów, nazw i kontraktów.
- **Technika:** brak szybkich testów, kopiowanie zamiast wspólnej reguły, odkładane aktualizacje.
- Złożoność ma tendencję do wzrostu, jeśli nie wykonuje się **jawnej pracy nad jej ograniczaniem** (Lehman).

---

## 4.4. Analiza przyczyny, nie winnego

„Programista skopiował kod” zatrzymuje analizę zbyt wcześnie. Zapytaj:

1. Dlaczego skopiowanie było najszybszą lub najbezpieczniejszą opcją?
2. Czy wspólna reguła miała właściciela i testy pozwalające ją zmienić?
3. Czy granice repozytoriów i zespołów utrudniały współdzielenie?
4. Jaka zmiana procesu zapobiegnie kolejnemu wystąpieniu?

**Czy można uniknąć degradacji?** Całkowicie - nie. Można ją spowolnić, uczynić widoczną i usuwać jej mechanizmy, a nie tylko objawy.

---

## 5.1. Dobry projekt ujawnia się podczas zmiany

- Jakość projektu widać, gdy **zmieniamy kod**: łatwo znaleźć miejsce, mały promień oddziaływania, szybka informacja zwrotna.
- Ocena zależy od kontekstu: domeny, rodzaju zmian, krytyczności błędów, zespołów, czasu życia rozwiązania.
- Zasada projektowa ma **postawić pytanie**, a nie zakończyć analizę.

| Zasada | Pytanie diagnostyczne |
| --- | --- |
| DRY | Gdzie jest autorytatywna reprezentacja wiedzy? |
| KISS | Czy jest mniej złożone poprawne rozwiązanie? |
| SOLID | Jak podział ogranicza koszt konkretnej zmiany? |
| reguła zależności | Czy polityka zależy od szczegółu technicznego? |

---

## 5.2. DRY dotyczy wiedzy, KISS - złożoności wprowadzonej

- **DRY**: jedna autorytatywna reprezentacja porcji **wiedzy**, nie zakaz podobnych linii.
- Pytanie: **czy zmiana jednej decyzji wymaga zgodnej aktualizacji kilku miejsc?**
- Wspólna metoda dla niezależnych reguł tworzy **fałszywą zależność** - dwa limity po 1000 kg to nie ta sama wiedza.
- **KISS**: najmniej złożone rozwiązanie, które **poprawnie** realizuje aktualne wymagania.
- Złożoność istotna (prawo, współbieżność, finanse) zostaje - celem jest ją nazwać i izolować; KISS celuje w złożoność **wprowadzoną**: pluginy bez rozszerzeń, silnik reguł dla dwóch przypadków.

---

## 5.3. SOLID jako narzędzie analizy zmian

| Zasada | Główna troska |
| --- | --- |
| SRP | grupowanie elementów zmieniających się dla tego samego aktora |
| OCP | ochrona stabilnej części przed wybraną osią rozszerzeń |
| LSP | zachowanie kontraktu przy podstawieniu implementacji |
| ISP | ograniczenie zależności klienta do potrzebnej roli |
| DIP | kierowanie zależności od szczegółów ku polityce |

- SRP: „jeden powód do zmiany” to odpowiedzialność wobec **aktora**, nie każda edycja pliku.
- **DIP ≠ dependency injection**: wstrzyknięcie konkretnej klasy wciąż wiąże ze szczegółem.

---

## 5.4. Błędne uproszczenia SOLID

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

## 5.5. Spójność i sprzężenie

- **Spójny** moduł da się opisać jednym zdaniem domenowym; jego elementy zmieniają się z tego samego powodu.
- **Sprzężenie**: czy zmiana jednego modułu wymusza edycję, testy lub wdrożenie drugiego?
- **Interfejs nie usuwa sprzężenia** - zastępuje zależność od implementacji zależnością od kontraktu.

| Pytanie | Sygnał problemu |
| --- | --- |
| Ile miejsc zmienić dla jednej reguły? | rozproszona wiedza, Shotgun Surgery |
| Czy typ frameworka trafia do domeny? | sprzężenie technologiczne |
| Czy wywołania wymagają ukrytej kolejności? | sprzężenie protokołu lub czasu |
| Czy moduły importują się wzajemnie? | cykl blokujący niezależną zmianę |

---

## 5.6. Reguła zależności i composition root

- **Reguła zależności**: zależności źródłowe przez granicę wskazują ku polityce, nie ku mechanizmowi.
- Przypadek użycia nie importuje `ResultSet`, encji ORM ani żądania HTTP - adapter tłumaczy te typy.
- Aplikacja definiuje port i go wywołuje; adapter go implementuje. **Sterowanie** płynie do adaptera, **zależność źródłowa** adaptera prowadzi do portu.
- **Composition root** składa graf, ale nie zawiera reguł biznesowych; ręczne konstruktory wystarczą.
- Liczy się kierunek importów, nie nazwy czterech folderów - i kierunek warto **sprawdzać automatycznie**.

---

## 5.7. Studium przypadku: punkt wyjścia

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

## 5.8. Co naprawdę jest problemem

Nie sam `switch`, lecz **niezależne decyzje w jednym miejscu** - każda kolejna zmiana dokłada się do tej samej metody.

| Obserwacja | Konsekwencja dla degradacji |
| --- | --- |
| stawka `0.08` w obu gałęziach | jedna reguła, dwie reprezentacje - przy zmianie jedna zostanie pominięta |
| polityki cenowe wewnątrz serwisu | zmiana wariantu modyfikuje koordynację |
| zapis w kolekcji serwisu | przypadek użycia zna mechanizm przechowywania |
| bezpośredni `System.out` | zmiana kanału wymaga edycji serwisu |
| ukryta kolejność zapis → powiadomienie | protokół wymaga kontraktu i testu |

---

## 5.9. Porty i przypadek użycia

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
- Kolejność `save` → `quoteCreated` to **decyzja protokołu**: błąd zapisu = brak powiadomienia.

---

## 5.10. Kierunek zależności i bilans decyzji

| Kod | Może zależeć od | Nie powinien zależeć od |
| --- | --- | --- |
| domena | Java SE, pojęcia domenowe | aplikacja, adaptery, frameworki |
| aplikacja | domena, własne porty | konkretne adaptery, konsola, baza |
| adapter | porty, domena, technologia | composition root, inne adaptery |
| composition root (`Module2Examples`) | wszystko do złożenia | reguły biznesowe |

- `FuelSurcharge` (DRY), dwie polityki (Strategy), test kontraktowy (LSP), porty (ISP, DIP), ręczny root (KISS, YAGNI).
- Więcej klas uzasadnia to, że dwa warianty i dwa efekty zewnętrzne **już istnieją** - projekt hamuje degradację, bo kolejna zmiana trafia w jedno, nazwane miejsce.

---

## 6.1. Boy Scout Rule: heurystyka, nie mandat

- **Boy Scout Rule**: dotykany kod zostaw w nieco lepszym stanie - heurystyka, nie licencja na przebudowę.
- Poprawa dotyczy kodu bieżącej zmiany, jest mała, lokalna i łatwa do weryfikacji.
- Przykłady: lepsza nazwa, małe Extract Method, usunięcie lokalnej duplikacji.
- Zmiana publicznego API, zależności, migracja danych czy reguły biznesowej **nie są** drobnym sprzątaniem.
- To prewencja w najkrótszej pętli: każde dotknięcie kodu trochę cofa entropię.

---

## 6.2. Przykład lokalnej poprawy: `ReleaseSummaryFormatter`

```java
validate(releaseId, results);
StringBuilder summary = new StringBuilder()
        .append("Release ").append(releaseId.strip()).append('\n');
int successfulDeployments = 0;
for (DeploymentResult result : results) {
    Objects.requireNonNull(result, "result");
    summary.append(formatResult(result)).append('\n');
    if (result.status() == DeploymentStatus.SUCCESS) {
        successfulDeployments++;
    }
}
return summary.append("Successful: ").append(successfulDeployments)
        .append('/').append(results.size()).toString();
```

- Przed: nazwy `s`, `n`, `r`, konkatenacja, powielony format wiersza. Po: nazwy, `validate`, `formatResult` - bez zmiany kontraktu.
- **Test różnicowy** porównuje obie wersje (także wyjątki); niezależny orakl chroni przed wspólnym błędem.

---

## 6.3. Bezpieczna sekwencja i typowe nadużycia

- Sekwencja: zielone testy → jeden mały ruch strukturalny → zawężone testy → przegląd diffu.
- Zmiana zachowania dopiero w **osobnym kroku**; gdy zakres rośnie, resztę odłóż do osobnego zadania.
- Nadużycia: nieograniczone porządki przy małym zadaniu, zmiana zachowania pod etykietą refaktoryzacji.
- Także: masowe formatowanie razem z naprawą, cudzy moduł bez komunikacji, abstrakcje „na przyszłość”.

---

## 6.4. Małe zestawy zmian - czego nie łączyć

- Dobry zestaw zmian: **jedna intencja**, powiązane testy, działające repozytorium po integracji.
- Liczba linii to przybliżenie: automatyczny rename w setkach miejsc to wciąż jedna intencja.
- „Najpierw ułatw zmianę, potem zrób łatwą zmianę”: refaktoryzacja przygotowawcza i nowa reguła to osobne commity.
- Rozdzielaj: refaktoryzację od zmiany funkcjonalnej, formatowanie od logiki, diff automatyczny od ręcznego.
- Rozdzielaj też: zmianę schematu od przełączenia odczytów, flagę od zwiększania ekspozycji.

---

## 6.5. Przegląd kodu i informacja zwrotna

- Opis odpowiada na pięć pytań: **dlaczego, co (i czego nie), jaki kontrakt, jakie dowody, jak wdrożyć i wycofać**.
- Wskaż kolejność czytania: testy kontraktowe → nowa granica → implementacje → konfiguracja.
- Komentarz dotyczy kodu i ryzyka: obserwacja, skutek, uzasadnienie, propozycja; etykiety `BLOCKER` / `SUGGESTION` / `NIT`.
- Spór o kierunek - rozmowa synchroniczna, wniosek zapisany (ADR, jeśli decyzja jest trwała).
- Przegląd to najwolniejsza pętla przed produkcją: przesuwaj do niej tylko to, czego nie złapie kompilator, test i bramka.

---

## 6.6. Prewencja ważniejsza niż leczenie

| Leczenie (po fakcie) | Prewencja (w krótkiej pętli) |
| --- | --- |
| wielka refaktoryzacja modułu po latach | Boy Scout przy każdej zmianie |
| ręczna regresja przed wydaniem | szybkie testy po każdym kroku |
| audyt architektury raz w roku | test reguły zależności w każdym buildzie |
| sprzątanie tysięcy ostrzeżeń | bramka „brak nowych naruszeń” |
| spór w przeglądzie po dwóch dniach | ADR i rozmowa przed rozpoczęciem pracy |

Leczenie bywa konieczne (moduły 3-5), ale prewencja jest tańsza, bo działa tam, gdzie informacja zwrotna jest najszybsza.

---

## Ćwiczenie 1: pętle informacji zwrotnej i bramka w legacy (15 + 5 min)

- **A:** dla opisanego zespołu zmapuj pętle, ich czas i przykłady problemów; zaproponuj trzy ruchy, które przesuną wykrywanie do szybszej pętli.
- **B:** zmierz ostrzeżenia `-Xlint:all` w repozytorium i sprawdź `InMemoryJavaCompiler` z obiema politykami.
- Zaproponuj bramkę „brak nowych naruszeń” ze stanem bazowym, który może tylko maleć.

Szczegóły: zadania modułu 2, Ćwiczenie 1.

---

## Ćwiczenie 2: decyzje teraz, później czy nigdy (15 + 10 min)

- Cel: dla sześciu sytuacji zdecydować - **teraz, w ostatnim odpowiedzialnym momencie, czy wcale**.
- Dla każdej: drzwi jedno- czy dwukierunkowe, wiedza i właściciel, sygnał ponownej oceny.
- Dla jednej decyzji jednokierunkowej napisz ADR z co najmniej dwiema opcjami.

Szczegóły: zadania modułu 2, Ćwiczenie 2.

---

## Ćwiczenie 3: projekt jako hamulec degradacji (25 + 10 min)

- Cel: zrefaktoryzować `LegacyDeliveryQuoteService` - `FuelSurcharge`, polityki, porty, adaptery, ręczny composition root.
- Po jednym wariancie, testy po każdym kroku; test kontraktowy polityk i test awarii zapisu.
- Kryteria: `17.28` / `31.32` dla 3 kg, przypadek użycia bez importu adapterów; propozycja automatycznej kontroli granicy.

Szczegóły: zadania modułu 2, Ćwiczenie 3.

---

## Ćwiczenie 4: Boy Scout i seria zmian gotowych do przeglądu (20 + 5 min)

- Cel: podzielić zmianę `ReleaseSummaryFormatter` na samodzielne zestawy zmian.
- Testy charakteryzujące → refaktoryzacja → nowa reguła raportu, każdy krok z zielonym buildem.
- Opis każdego zestawu na pięć pytań; zmiany mechaniczne oddzielone od ręcznych.

Szczegóły: zadania modułu 2, Ćwiczenie 4.

---

## Kluczowe wnioski z omówienia

- Bramka `-Werror` włączona nagle zablokuje zespół historycznymi ostrzeżeniami - najpierw stan bazowy, potem „brak nowych”.
- Wspólną opłatę paliwową scalamy od razu; dwa limity 1000 kg zostają lokalne - inna wiedza i właściciele.
- Format pliku dla partnera to drzwi jednokierunkowe - zasługuje na ADR; nazwa pakietu wewnętrznego nie.
- Przypadek użycia nie importuje adapterów; kolejność zapis → powiadomienie jest jawną decyzją z testem.
- Refaktoryzacja `ReleaseSummaryFormatter` i nowa reguła raportu to **dwa zestawy zmian** z różnymi dowodami.

Pełne omówienie: rozwiązania modułu 2.

---

## Sprawdzenie wiedzy

1. Dlaczego problem złapany w pętli kompilatora jest tańszy niż ten sam problem złapany w przeglądzie?
2. Czym jest ostatni odpowiedzialny moment i czym różni się od odkładania decyzji w nieskończoność?
3. Jak testy i granice zamieniają decyzje jednokierunkowe w dwukierunkowe?
4. Czym różni się akceptacja problemu od jego ignorowania?
5. Co mówią prawa Lehmana o degradacji jakości i czego nie mówią?
6. Dlaczego `@Autowired` nie dowodzi zastosowania DIP?
7. Czym różni się Boy Scout Rule od „sprzątania przy okazji” zmieniającego zachowanie?

---

## Lista kontrolna prewencji

- **Pętle:** zmierzony czas od zapisu do sygnału; szybkie testy obszaru; kompilator i bramka „brak nowych naruszeń” w każdym buildzie.
- **Decyzje:** nazwana niepewność i ostatni odpowiedzialny moment; decyzje jednokierunkowe z ADR; opcje z właścicielem i warunkiem usunięcia.
- **Akceptacja:** każdy zaakceptowany problem w rejestrze z właścicielem i sygnałem; obce modele w kwarantannie za granicą.
- **Projekt:** jedna reprezentacja wiedzy; polityka bez importów technologii; reguła zależności sprawdzana automatycznie.
- **Codzienność:** Boy Scout w dotykanym kodzie; jedna intencja na zestaw zmian; opis z dowodami i planem wycofania.

---

## Podsumowanie - najważniejsze wnioski

- Najtańszy problem to ten wykryty w **najkrótszej pętli** - inwestuj w kompilator, szybkie testy i bramki, zanim zainwestujesz w przegląd i testy ręczne.
- Decyzje odkładaj do **ostatniego odpowiedzialnego momentu**, ale tylko wtedy, gdy kod jest podatny na zmianę; jednokierunkowe zapisuj w ADR.
- Problemów, których nie rozwiążesz, **nie ignoruj**: zaakceptuj jawnie, odizoluj za granicą, wpisz do rejestru z właścicielem.
- Degradacja jest domyślnym kierunkiem ewolucji systemu (Lehman) - można ją spowolnić tylko stałą, jawną pracą.
- Projekt aplikacji (DRY, SOLID, reguła zależności) kieruje kolejne zmiany w nazwane miejsca i ogranicza ich promień.
- Prewencja jest ważniejsza niż leczenie: Boy Scout, małe zestawy zmian i przegląd z dowodami działają przy każdej zmianie.
