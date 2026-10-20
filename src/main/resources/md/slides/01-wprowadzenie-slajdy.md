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
# Moduł 1. Wprowadzenie
Czym jest refaktoryzacja, kiedy się opłaca, jak rozpoznawać zapachy kodu i jak czytać metryki jakości

---

## Agenda

1. Czym jest refaktoryzacja
2. Kiedy warto refaktoryzować?
3. *Code smells*
4. Metryki jakości kodu
5. Ćwiczenia, sprawdzenie wiedzy, lista kontrolna

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

## 1.4. Refaktoryzacja, przepisanie, modernizacja

- **Refaktoryzacja** zmienia strukturę bez zmiany obserwowalnego zachowania.
- **Przepisanie** tworzy nową implementację - nie dziedziczy automatycznie zachowań ani dojrzałości operacyjnej; zachowania trzeba jawnie odzyskać, zweryfikować, przenieść albo świadomie odrzucić.
- **Modernizacja** jest szersza: platforma, interfejs, model danych, wymiana, zakup, migracja.
- Cel refaktoryzacji: obniżyć **koszt lub ryzyko dalszej pracy** - miejsce pod funkcję, punkt testowania, mniejsze sprzężenie.
- Nie są celami samymi w sobie: więcej klas i interfejsów, wzorzec dla wzorca, procent pokrycia.

---

## Aktywność 1.1: nazwij kontrakt (4 min)

Wskaż zachowanie, które może być obserwowalne:

1. zmiana typu prywatnego pola z `List` na `Set`,
2. zamiana dwóch zapisów do bazy miejscami,
3. przeniesienie klasy używanej przez refleksję,
4. zastąpienie algorytmu szybszą implementacją,
5. zmiana prywatnej nazwy bez refleksji i konfiguracji tekstowej.

**Zasada:** najpierw nazwij konsumenta, obserwację i sposób weryfikacji, dopiero potem oceń, czy to refaktoryzacja.

---

## 2.1. Legacy jest relacją, a nie etykietą

- **Historycznie:** kod odziedziczony po dawnych zespołach i decyzjach - sam wiek nie przesądza o jakości.
- **Operacyjnie:** zmiana wiąże się z **nieproporcjonalną niepewnością lub kosztem** - sedno problemu.
- Ocena zależy od **planowanych zmian**: stabilny moduł bez zmian może nie uzasadniać inwestycji.
- Kluczowe pytanie: *czy konstrukcja systemu utrudnia zmiany, których rzeczywiście będziemy potrzebować?*
- „Legacy = kod bez testów” to heurystyka - wolne lub pozbawione asercji testy też nie chronią.

---

## 2.2. Moment refaktoryzacji

- **Przygotowawczo:** najpierw ułatw zmianę, potem zrób łatwą zmianę - przed nową funkcją w trudnym miejscu.
- **Dla zrozumienia:** lepsza nazwa lub wydzielona metoda utrwala wiedzę zdobytą podczas czytania.
- **Oportunistycznie:** mała poprawa w dotykanym kodzie (zasada Boy Scout - moduł 2).
- **Planowo, małymi seriami:** gdy obszar jest często zmieniany i koszt każdej zmiany rośnie.
- Osobny projekt refaktoryzacyjny to wyjątek, nie domyślna odpowiedź.
- **Kiedy nie:** kod do wycofania, brak planowanych zmian, brak sieci bezpieczeństwa (najpierw testy - moduł 3).

---

## 2.3. Koszt zmiany i ryzyko

- Koszt to także: odnalezienie miejsc, odzyskanie wiedzy, testy, koordynacja, wdrożenie i wycofanie.
- Kod w godzinę, pewność w pięć dni - problemem jest **koszt informacji zwrotnej**.
- Priorytet mają miejsca **często zmieniane i ważne biznesowo**: stabilny parser - monitoruj; moduł cenowy zmieniany co tydzień - inwestuj.

| Ryzyko | Istota |
| --- | --- |
| funkcjonalne | nieznane zachowania, ślad tylko w kodzie, danych i logach |
| operacyjne | ręczne wdrożenia, słaba obserwowalność, brak wycofania |
| wiedzy | tylko jedna osoba rozumie krytyczny proces |
| ekonomiczne | koszt opóźnienia bywa większy niż budżet utrzymania |

---

## 2.4. Dług techniczny - użyteczna metafora

- Kompromis jakościowy tworzący **warunkowe zobowiązanie** na przyszłość.
- **Kapitał** - koszt usunięcia i weryfikacji; **odsetki** - dodatkowy koszt, gdy kolejne prace go napotykają.
- Naliczanie odsetek zależy od tego, czy obszar będzie zmieniany.
- To analogia, nie model księgowy; konieczny kompromis architektoniczny nie jest automatycznie długiem.

---

## 2.5. Nie każdy problem jest długiem

| Sytuacja | Dług? |
| --- | --- |
| Funkcja jeszcze niezaplanowana | Zwykle nie - zakres produktu |
| Błędny wynik dla aktualnego wymagania | Nie - defekt (może być skutkiem długu) |
| Dwie usługi współdzielą schemat, wspólne wdrożenia | Kandydat - potwierdź koszt |
| Starsza, wspierana biblioteka spełniająca potrzeby | Nie na podstawie samego wieku |
| Brak testów wymusza 3 dni ręcznej regresji | Tak - powtarzalny koszt |
| Kod niezgodny z preferencjami recenzenta | Nie - brak konsekwencji |

---

## 2.6. Klasyfikacja i opis elementu długu

- **Świadomy** (celowy kompromis) vs **nieświadomy**; **rozważny** vs **lekkomyślny** - osie można łączyć.
- Obszary: kod i architektura, testy, dane, build i wdrożenie, platforma, obserwowalność, wiedza.

| Pole | Przykład TD-017 |
| --- | --- |
| Konstrukcja / przyczyna | trzy kopie reguły rabatu; rozwiązanie tymczasowe bez właściciela |
| Dowód / odsetki | dwie poprawki po pięciu zmianach rabatów; ok. 3 osobodni |
| Ekspozycja | nowy program lojalnościowy, pięć zmian rabatów w kwartale |
| Kapitał bieżący | 4-7 osobodni z testami i migracją konfiguracji |
| Decyzja / pewność | usunąć duplikację przed programem; pewność średnia |

Pełny szablon pól: zadania modułu 1, Aktywność 1.2. Oszacowanie to **przedział**, nie obietnica.

---

## 2.7. Identyfikacja i priorytetyzacja bez fałszywej precyzji

- Źródła: warsztaty z zespołem, incydenty, historia repozytorium, analiza statyczna, plan produktu.
- Skaner nie zna wartości biznesowej, rozmowa bywa selektywna - **łącz dane ilościowe i jakościowe**.
- Nie istnieje jedna wiarygodna liczba całkowitego długu; „czas naprawy” z narzędzia pomija wiedzę i migrację.
- Priorytet: wpływ + prawdopodobieństwo napotkania + pilność + koszt opóźnienia - koszt i ryzyko interwencji.
- To **model rozmowy**, nie wzór arytmetyczny; co zrobić z długiem, którego nie spłacimy - moduł 2.

---

## Aktywność 1.2: brakujące pola rekordu (3 min)

Zespół zapisał jedynie: *„Brak testów w imporcie, naprawa zajmie trzy dni”.*

- Wskaż co najmniej cztery brakujące pola szablonu długu.
- Dla każdego: dane historyczne, wiedza domenowa, eksperyment czy decyzja właściciela produktu?

**Zasada:** nie uzupełniaj nieznanych wartości założeniami przedstawionymi jako fakty.

---

## 2.8. To nie jest wybór binarny

| Strategia | Kiedy może być właściwa | Główne ryzyko |
| --- | --- | --- |
| Pozostawienie | obszar stabilny, krótki horyzont | przeoczenie zmiany kontekstu |
| Stabilizacja | brak testów i obserwowalności | nie obniża jeszcze kosztów struktury |
| Refaktoryzacja lokalna | zachowanie ma wartość | regresja bez sieci bezpieczeństwa |
| Opakowanie interfejsem | wnętrze stabilne | utrwalenie starego modelu |
| Stopniowe zastępowanie | system duży, krytyczny | architektura przejściowa zostaje na stałe |
| Pełne przepisanie | mały zakres, znane zachowanie | utrata zachowań |

Także: aktualizacja platformy, wymiana komponentu, zakup, wycofanie funkcji - często łączone.

---

## 2.9. Kryteria decyzji

| Kryterium | Ku zmianie przyrostowej | Ku wymianie / przepisaniu |
| --- | --- | --- |
| Zachowanie | cenne, lecz nie w pełni opisane | w większości zbędne |
| Zakres | duży, zintegrowany, krytyczny | mały, stabilny kontrakt |
| Platforma | istnieje ścieżka aktualizacji | brak realnej ścieżki migracji |
| Wiedza domenowa | głównie w kodzie i danych | opisana, eksperci dostępni |
| Migracja | jednorazowe przełączenie wykluczone | działanie równoległe i wycofanie |
| Ekonomia | funkcje dostarczane w trakcie | pozostanie droższe niż wymiana |

Tabela wspiera rozmowę, ale nie działa jak algorytm.

---

## 2.10. Pełny koszt przepisania i sygnały ostrzegawcze

- Nie porównuj samej nowej implementacji z kosztem zmiany starego kodu.
- Dolicz: odzyskanie wymagań, utrzymanie starego systemu w trakcie prac, migrację danych, integracje i niejawne kontrakty, testy niefunkcjonalne, przełączenie, wycofanie i **wyłączenie starego** kodu.
- **Sygnały ostrzegawcze:** motywacją jest niechęć do kodu lub nowa technologia; brak migracji danych, porównania wyników i wycofania; wartość dopiero po latach.
- Przyczyny organizacyjne bez zmian - nowy system odziedziczy te same mechanizmy.
- Przepisanie wygrywa **pełnym planem**, nie estetyką.

---

## 2.11. Przewaga małych, odwracalnych kroków

1. Ustabilizuj budowanie, testy i obserwowalność obszaru.
2. Zidentyfikuj granicę funkcji i jej kontrakt; oddziel wywołujących adapterem.
3. Zbuduj nową implementację jednego przypadku i porównaj wyniki.
4. Rozszerzaj zakres, na końcu usuń starą ścieżkę i elementy przejściowe.

**Strangler Fig** stopniowo przejmuje funkcje na granicach systemu (szczegóły w module 5). Każdy element przejściowy potrzebuje właściciela i warunku usunięcia.

---

## 2.12. Proces podjęcia decyzji

1. Nazwij problem, mierzalny wynik, krytyczne zachowania i ograniczenia.
2. Zbierz dane o zmianach, ryzyku, platformie, danych i integracjach.
3. Rozważ **co najmniej trzy strategie**, w tym pozostawienie lub ograniczenie ryzyka.
4. Oszacuj pełny koszt przejścia, działania równoległego i wyłączenia.
5. Wykonaj ograniczony czasowo eksperyment dla najważniejszej niewiadomej.
6. Zapisz założenia, sygnały sukcesu, warunki przerwania i termin ponownej oceny.

Decyzja jest **hipotezą** - nowe informacje mogą uzasadnić zmianę kierunku.

---

## Aktywność 1.3: wybór najmniejszego kroku (4 min)

Platforma wspierana jeszcze dwa lata; krytyczny moduł zmienia się co tydzień, nie ma szybkich testów i powoduje większość regresji.

- Porównaj: pełne przepisanie, testy charakteryzujące modułu, wymiana platformy.
- Wybierz jeden krok: założenie do weryfikacji, miernik wyniku, warunek przerwania.

**Pamiętaj:** te informacje nie wystarczą do zatwierdzenia kompletnej strategii modernizacji.

---

## 3.1. Rozróżniaj pojęcia i mów językiem diagnozy

| Pojęcie | Znaczenie | Typowa reakcja |
| --- | --- | --- |
| Defekt | zachowanie niezgodne z wymaganiem | naprawa i test |
| *Code smell* | sygnał możliwego problemu projektowego | analiza kontekstu |
| Dług techniczny | konstrukcja zwiększająca koszt przyszłych zmian | spłacić, ograniczyć, tolerować |
| Ryzyko | niepewne zdarzenie wpływające na cel | prawdopodobieństwo i wpływ |
| Ograniczenie | warunek poza kontrolą zespołu | adapter, negocjacja, migracja |

„Ten kod jest zły” nie wspiera decyzji. Diagnoza = **fakt, kontekst, konsekwencja, dowód**.

---

## Aktywność 1.4: fakt czy ocena (3 min)

Fakt, hipoteza, defekt, symptom czy ograniczenie? (możliwe kilka kategorii)

1. Metoda ma 142 linie i złożoność cyklomatyczną równą 24.
2. Metoda jest niemożliwa do utrzymania.
3. Wynik dla zatwierdzonego przykładu różni się od wymaganej kwoty.
4. Producent środowiska zakończy dostarczanie poprawek za sześć miesięcy.

**Następnie:** przepisz stwierdzenie 2 jako diagnozę: fakt, kontekst, konsekwencja, dowód.

---

## 3.2. *Code smells* jako hipotezy diagnostyczne

- *Code smell* to sygnał, który **może** wskazywać problem - nie dowód defektu ani nakaz refaktoryzacji.
- Długa metoda może łączyć odpowiedzialności - albo być liniowym algorytmem, którego podział zaszkodzi.

Dla każdego symptomu zapytaj:

1. Jaką konkretną zmianę utrudnia i jakie zachowanie może zostać naruszone?
2. Czy historia zmian potwierdza problem i czy rozchodzi się on po systemie?
3. Czy koszt pozostawienia przewyższa koszt i ryzyko interwencji?
4. Jaki najmniejszy krok dostarczy informacji albo ograniczy ryzyko?

---

## 3.3. Katalog zapachów (1/2)

| Grupa | Zapachy | Co sygnalizuje |
| --- | --- | --- |
| **Bloaters** (rozrost) | Long Method, Large Class, Long Parameter List, Primitive Obsession, Data Clumps | kod urósł ponad to, co da się objąć jednym spojrzeniem |
| **Object-Orientation Abusers** (nadużycia obiektowości) | Switch Statements, Temporary Field, Refused Bequest, Alternative Classes with Different Interfaces | wariant zachowania wyrażony warunkami zamiast typem lub niespójna hierarchia |
| **Change Preventers** (blokery zmian) | Divergent Change, Shotgun Surgery, Parallel Inheritance Hierarchies | jedna zmiana dotyka wielu miejsc albo jedno miejsce zmienia się z wielu powodów |

Przykład: `placeOrder(order, customerType, express, destinationCountry)` - Long Method, Long Parameter List, Primitive Obsession (`String` jako typ klienta), argument `boolean`.

---

## 3.4. Katalog zapachów (2/2)

| Grupa | Zapachy | Co sygnalizuje |
| --- | --- | --- |
| **Dispensables** (zbędności) | Duplicated Code, Dead Code, Lazy Class, Data Class, Speculative Generality, komentarz kompensujący niejasny kod | element, którego usunięcie uprości kod bez utraty wartości |
| **Couplers** (sprzęgacze) | Feature Envy, Inappropriate Intimacy, Message Chains, Middle Man | moduły wiedzą o sobie za dużo albo pośredniczą bez wartości |

- Nazwa zapachu to wspólny słownik zespołu, nie werdykt: „Shotgun Surgery przy zmianie stawki VAT” jest konkretniejsze niż „kod jest zły”.
- Ten sam fragment może mieć zapachy z kilku grup - zacznij od tego, który utrudnia **planowaną** zmianę.

---

## 3.5. Przykład: `LegacyOrderService` (fragment)

```java
public Receipt placeOrder(Order order, String customerType,
        boolean express, String destinationCountry) {
    // ... walidacja, pętla po pozycjach:
    if ("VIP".equals(customerType)) lineValue = lineValue.multiply(new BigDecimal("0.90"));
    if (line.quantity() >= 10)     lineValue = lineValue.multiply(new BigDecimal("0.95"));
    if (express) shipping = new BigDecimal("39.99");
    else if (subtotal.compareTo(new BigDecimal("200.00")) >= 0) shipping = BigDecimal.ZERO;
    else shipping = new BigDecimal("14.99");
    if ("PL".equals(destinationCountry)) tax = subtotal.multiply(new BigDecimal("0.23"));
    else if ("DE".equals(destinationCountry)) tax = subtotal.multiply(new BigDecimal("0.19"));
    else tax = BigDecimal.ZERO;
    repository.save(order.id(), total);
    mailGateway.send(order.customerEmail(), "Order total: " + total);
    return new Receipt(order.id(), total);
}
```

Materiał diagnostyczny do Ćwiczeń 1 i 2 - nie wzorcowa implementacja.

---

## 3.6. Od zapachu do kierunku refaktoryzacji (1/2)

| Zapach | Kierunek refaktoryzacji | Możliwy wzorzec docelowy |
| --- | --- | --- |
| Switch Statements | Replace Conditional with Polymorphism, Replace Conditional Dispatcher with Command | Strategy, State, Command |
| Conditional Complexity | Replace Conditional Logic with Strategy, Move Embellishment to Decorator | Strategy, Decorator, State |
| Duplicated Code | Extract Method, Form Template Method | Template Method, Strategy |
| Combinatorial Explosion | Replace Implicit Language with Interpreter, Move Embellishment to Decorator | Interpreter, Decorator |

---

## 3.6. Od zapachu do kierunku refaktoryzacji (2/2)

| Zapach | Kierunek refaktoryzacji | Możliwy wzorzec docelowy |
| --- | --- | --- |
| Shotgun Surgery / Divergent Change | Move Method, Extract Class, Inline Class | Facade, spójne klasy |
| Primitive Obsession | Replace Type Code with Class, Replace Data Value with Object | Value Object, State |
| Indecent Exposure | Encapsulate Classes with Factory | Factory, Facade |

Na podstawie Kerievsky'ego (*Refactoring to Patterns*). Kierunek, nie nakaz: refaktoryzujemy **do**, **w kierunku** albo **od** wzorca (moduły 4 i 5).

---

## 4.1. Metryka nie jest celem

- Jakość ma wiele wymiarów - pojedyncza metryka strukturalna nie mierzy ich wszystkich.
- Metryka ma odpowiadać na pytanie: schemat **Cel, Pytanie, Metryka**.
- Cel: mniej ryzyka w wycenie → pytanie: co jest często zmieniane i trudne do weryfikacji?
- Metryki: częstotliwość zmian, złożoność, czas testów, pokrycie zmienianego kodu, regresje.
- Dane bez pytania dają raporty precyzyjne z wyglądu, ale bezużyteczne dla decyzji.

---

## 4.2. Złożoność cyklomatyczna: `RiskClassifier`

```java
public static int riskLevel(OrderSummary order) {
    int score = 0;
    if (order.total().compareTo(new BigDecimal("1000.00")) > 0) score++;
    if (order.international()) score++;
    for (Item item : order.items()) {
        if (item.fragile()) score++;
    }
    return score;
}
```

- Liczba punktów decyzyjnych + 1: tu **5** (dwa `if`, `for`, `if` w pętli).
- To liczba liniowo niezależnych ścieżek, nie wszystkich; nie mierzy czytelności ani zagnieżdżenia.
- Wyniki narzędzi się różnią - porównuj w tym samym narzędziu; brak uniwersalnego progu.

---

## 4.3. Złożoność poznawcza

- Przybliża **wysiłek człowieka** potrzebny do zrozumienia przepływu (definicja z narzędzi, np. SonarQube).
- Każde przerwanie przepływu (`if`, pętla, `catch`) +1, a zagnieżdżenie dodaje narzut równy głębokości.
- `switch` i sekwencja jednakowych operatorów liczą się raz; wczesny `return` w klauzuli ochronnej nie zwiększa wyniku.
- Spłaszczenie warunków realnie ją obniża; uzupełnia złożoność cyklomatyczną, nie zastępuje jej.

---

## 4.4. Pokrycie linii a pokrycie gałęzi

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

- Pokrycie mówi, co zostało **wykonane**, nie co zostało sprawdzone: tu 100% linii, ale 2 z 4 gałęzi.
- Globalny procent może ukryć krytyczny moduł z niemal zerowym pokryciem.
- Pokrycie, test mutacyjny i sieć bezpieczeństwa przed refaktoryzacją - szczegóły w module 3.

---

## 4.5. Duplikacje: `SalesCalculations`

```java
public static BigDecimal invoiceLineTotal(BigDecimal unitPrice, int quantity, boolean vip) {
    BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(quantity));
    if (vip) { total = total.multiply(new BigDecimal("0.90")); }
    return total;
}
// quoteLineTotal(...) - identyczna treść
```

- Duplikacja jest kosztowna, gdy reprezentuje **tę samą wiedzę**, zmieniającą się razem.
- Podobne fragmenty różnych reguł połączone przedwcześnie tworzą błędną zależność.
- Przed połączeniem ustal, czy faktura i oferta korzystają z jednej reguły rabatowej.
- Procentów z różnych narzędzi nie porównuj - ważniejsza jest współzmienność kopii.

---

## 4.6. Sprzężenie i spójność

- **`Ce`** - od ilu typów moduł zależy; **`Ca`** - ile typów zależy od modułu (promień zmiany).
- **Niestabilność `I = Ce / (Ca + Ce)`:** blisko 0 - powinien być stabilny i abstrakcyjny, blisko 1 - łatwy do zmiany.
- **Cykle zależności** są kosztowne; **change coupling** z historii VCS ujawnia sprzężenie wiedzą.
- Spójność: rodzina **LCOM** lub test nazwania odpowiedzialności bez „oraz”.
- Liczy się kierunek: zależność od stabilnej abstrakcji jest tania, od zmiennego konkretu droga.

---

## 4.7. Łączenie metryk

| Sygnał | Interpretacja po połączeniu |
| --- | --- |
| wysoka złożoność | + częste zmiany = kosztowny obszar aktywnej pracy (*hotspot*) |
| niskie pokrycie | + planowana zmiana = większa niepewność zmiany |
| duplikacja | + wspólne zmiany i regresje = powielona wiedza |
| częste zmiany | + incydenty i szeroki promień = ryzyko |

- Kandydatów wskazuje **przecięcie sygnałów** i trend w czasie, nie pojedyncza wartość.
- Pulpit (znaczenie, ekspozycja, tarcie, ryzyko, struktura, zabezpieczenia) - mały, pod decyzję.

---

## 4.8. Antywzorce użycia metryk

- **Optymalizacja pod wynik** - np. testy bez asercji dla progu pokrycia.
- **Porównywanie nieporównywalnych** systemów, języków i narzędzi.
- **Korelacja jako przyczyna** - obniżenie metryki nie musi usunąć defektów.
- **Uśrednianie ryzyka** - średnia pakietu może ukryć krytyczny fragment.
- **Bramka bez analizy zmiany** - chroń aktywnie zmieniany kod, nie jeden próg dla całości.

---

## Aktywność 1.5: dobór metryk (4 min)

Cel: mniej regresji przy comiesięcznych zmianach reguł cenowych. Wybierz **trzy**, odrzuć **dwa**:

- globalne pokrycie linii całej aplikacji,
- liczba poprawek po wydaniu na zmianę reguł,
- pokrycie gałęzi zmienianych reguł przez testy z istotnymi asercjami,
- liczba klas w repozytorium,
- współzmienność plików z regułami cenowymi,
- maksymalna złożoność dowolnej metody w systemie.

**Zasada:** nazwij pytanie, na które odpowiada wskaźnik, i jedno ograniczenie interpretacji.

---

## Ćwiczenie 1: obserwacja bez pochopnej naprawy (15 + 5 min)

- Cel: w `LegacyOrderService` oddzielić fakty, hipotezy i niewiadome przed planowaną zmianą (Czechy, `PARTNER`, odbiór osobisty).
- Co najmniej pięć faktów, trzy hipotezy, pytania domenowe i potrzebne dowody.
- **Nie proponuj** wzorców ani docelowej refaktoryzacji.

Szczegóły: zadania modułu 1, Ćwiczenie 1.

---

## Ćwiczenie 2: od zapachu do kierunku zmiany (10 + 5 min)

- Cel: nazwać zapachy w kodzie modułu 1 słownikiem katalogu i przypisać im grupę oraz możliwy kierunek refaktoryzacji.
- Dla każdego zapachu: dowód w kodzie, zmiana, którą utrudnia, decyzja **teraz / przy planowanej zmianie / nie**.
- Kierunek to hipoteza do weryfikacji, nie plan implementacji.

Szczegóły: zadania modułu 1, Ćwiczenie 2.

---

## Ćwiczenie 3: klasyfikacja i priorytetyzacja długu (20 + 10 min)

- Cel: pokazać, że najgorszy wynik metryki nie musi oznaczać najwyższego priorytetu.
- Pięciu kandydatów: klasyfikacja, pełny rekord długu, plan na **6 osobodni** ze świadomymi rezygnacjami.
- Bez wymyślonych wartości; ponowna ocena `AnnualReportFormatter` przy zmienionej ekspozycji.

Szczegóły: zadania modułu 1, Ćwiczenie 3.

---

## Ćwiczenie 4: wybór strategii modernizacji (15 + 5 min)

- Cel: skrócony rekord decyzji dla `ClaimsCore` - skrócić wdrożenie reguły z 15 do 3 dni bez obniżenia dostępności.
- Grupy 3-4 osób; co najmniej trzy strategie, w tym inna niż refaktoryzacja i przepisanie.
- Pierwszy odwracalny krok w 4 tygodnie; **mierniki wyniku**, nie aktywności.

Szczegóły: zadania modułu 1, Ćwiczenie 4.

---

## Kluczowe wnioski z omówienia

- „Podatek jest liczony błędnie” to hipoteza - bez reguły biznesowej nie wynika z kodu.
- `LegacyOrderService` jest testowalna - pierwszy krok to testy charakteryzujące, nie przepisanie.
- Zapach z katalogu wskazuje kierunek, ale o momencie decyduje planowana zmiana: `customerType` jako `String` boli dopiero przy `PARTNER`.
- `TaxRoundingRule` to **defekt**, nie dług; `PricingService` to najlepiej potwierdzony dług.
- `AnnualReportFormatter` ma najgorszą metrykę, ale krótki horyzont życia - brak dowodu przyszłego kosztu.

Pełne omówienie: rozwiązania modułu 1.

---

## Sprawdzenie wiedzy

1. Czym refaktoryzacja różni się od zmiany funkcjonalnej i od przepisania?
2. Dlaczego wiek kodu nie wystarcza do określenia go jako legacy?
3. Co oznaczają kapitał i odsetki w metaforze długu?
4. Czym różni się symptom jakości od defektu?
5. Do której grupy katalogu należy Shotgun Surgery i jaki kierunek refaktoryzacji podpowiada?
6. Co mierzy złożoność cyklomatyczna, a czego nie mierzy?
7. Dlaczego identyczny fragment kodu nie zawsze powinien zostać uogólniony?
8. Jakie koszty pełnego przepisania są często pomijane?

---

## Lista kontrolna

- **Zakres zmiany:** nazwany cel, kontrakt i konsumenci; wiadomo, czy to refaktoryzacja, zmiana zachowania czy migracja.
- **Wartość i horyzont:** znani użytkownicy, funkcje krytyczne, plan zmian w obszarze, horyzont życia.
- **Diagnoza:** fakty oddzielone od hipotez; zapachy nazwane słownikiem katalogu i powiązane z planowaną zmianą.
- **Metryki:** łączone z historią zmian i krytycznością, nie oceniane pojedynczo.
- **Dług i strategia:** dług ma lokalizację, konsekwencję i właściciela; rozważono więcej niż dwie opcje, pierwszy krok jest mały i odwracalny.

---

## Podsumowanie - najważniejsze wnioski

- Refaktoryzacja zmienia strukturę, **zachowując nazwane obserwacje**; poprawka, optymalizacja i przepisanie to inne rodzaje zmian.
- Refaktoryzować warto tam, gdzie kod jest **często zmieniany i ważny** - legacy to relacja między kodem a planowaną zmianą.
- Dług techniczny dotyczy przyszłego kosztu zmian, a nie każdego defektu czy starej technologii.
- *Code smells* są hipotezami; katalog daje wspólny słownik i podpowiada kierunek, ale nie podejmuje decyzji.
- Metryki nabierają znaczenia w połączeniu z planem zmian, historią i krytycznością.
- Bezpieczny kierunek zaczyna się od **małego kroku**, który daje wiedzę lub mierzalną wartość.
