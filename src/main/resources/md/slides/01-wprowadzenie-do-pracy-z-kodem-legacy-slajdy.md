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
# Moduł 1. Wprowadzenie do pracy z kodem legacy
Jak rozpoznać, zmierzyć i świadomie zarządzać ryzykiem zmian w odziedziczonym systemie

---

## Agenda

1. Definicja i charakterystyka kodu legacy
2. Ryzyka i koszty utrzymania systemów legacy
3. Identyfikacja, pomiar i zarządzanie długiem technicznym
4. Rozpoznawanie *code smells*
5. Metryki jakości kodu
6. Przyczyny powstawania kodu niskiej jakości
7. Kryteria wyboru między refaktoryzacją a przepisaniem
8. Warsztat diagnostyczny, sprawdzenie wiedzy, lista kontrolna

---

## 1.1. Czym jest kod legacy - trzy perspektywy

- **Historyczna:** kod odziedziczony po dawnych zespołach i decyzjach - sam wiek nie przesądza o jakości.
- **Operacyjna:** zmiana wiąże się z **nieproporcjonalną niepewnością lub kosztem** - sedno problemu.
- **Biznesowa:** system dostarcza wartość, ale koszt i ryzyko zmian ograniczają cele organizacji.
- Sygnały: brak szybkiej informacji zwrotnej, niejawne zależności, wiedza u pojedynczych osób.
- „Legacy = kod bez testów” to heurystyka - wolne lub pozbawione asercji testy też nie chronią.

---

## 1.2. Legacy jest relacją, a nie etykietą

- Ocena zależy od **planowanych zmian**: stabilny moduł bez zmian może nie uzasadniać inwestycji.
- Kluczowe pytanie: *czy konstrukcja systemu utrudnia zmiany, których rzeczywiście będziemy potrzebować?*
- Stary kod nie musi być zły, monolit nie musi być legacy, duża liczba linii nie dowodzi złożoności.
- Niskie pokrycie nie dowodzi defektów, a wysokie - poprawności.
- Kod nieużywany, który można bezpiecznie usunąć, to kod martwy - nie wymaga modernizacji.

---

## 1.3. Charakterystyczne właściwości

| Obszar | Pytanie diagnostyczne |
| --- | --- |
| Zachowanie | Skąd wiemy, co musi pozostać niezmienione? |
| Struktura | Jak daleko rozchodzi się typowa zmiana? |
| Technologia | Czy system można bezpiecznie budować, uruchamiać i aktualizować? |
| Dane | Jakie reguły są zapisane w danych, skryptach i procedurach? |
| Operacje | Jak szybko wykryjemy problem i przywrócimy usługę? |
| Wiedza | Ile osób potrafi bezpiecznie zmienić krytyczny fragment? |

Pojedyncza właściwość nie przesądza o diagnozie - ryzyko wynika z **kombinacji**.

---

## 1.4-1.5. Rozróżniaj pojęcia i mów językiem diagnozy

| Pojęcie | Znaczenie | Typowa reakcja |
| --- | --- | --- |
| Defekt | zachowanie niezgodne z wymaganiem | naprawa i test |
| *Code smell* | sygnał możliwego problemu projektowego | analiza kontekstu |
| Dług techniczny | konstrukcja zwiększająca koszt przyszłych zmian | spłacić, ograniczyć, tolerować |
| Ryzyko | niepewne zdarzenie wpływające na cel | prawdopodobieństwo i wpływ |
| Ograniczenie | warunek poza kontrolą zespołu | adapter, negocjacja, migracja |

„Ten kod jest zły” nie wspiera decyzji. Diagnoza = **fakt, kontekst, konsekwencja, dowód**.

---

## Aktywność 1.1: fakt czy ocena (3 min)

Fakt, hipoteza, defekt, symptom czy ograniczenie? (możliwe kilka kategorii)

1. Metoda ma 142 linie i złożoność cyklomatyczną równą 24.
2. Metoda jest niemożliwa do utrzymania.
3. Wynik dla zatwierdzonego przykładu różni się od wymaganej kwoty.
4. Producent środowiska zakończy dostarczanie poprawek za sześć miesięcy.

**Następnie:** przepisz stwierdzenie 2 jako diagnozę: fakt, kontekst, konsekwencja, dowód.

---

## 2.1. Koszt zmiany i jego rozkład

- Koszt to także: odnalezienie miejsc, odzyskanie wiedzy, testy, koordynacja, wdrożenie i wycofanie.
- Kod w godzinę, pewność w pięć dni - problemem jest **koszt informacji zwrotnej**.
- Koszt nie jest równomierny: priorytet mają miejsca **często zmieniane i ważne biznesowo**.
- Skomplikowany, ale stabilny parser - monitoruj; umiarkowany moduł cenowy zmieniany co tydzień - inwestuj.

---

## 2.2. Kategorie ryzyka

| Ryzyko | Istota |
| --- | --- |
| funkcjonalne | nieznane zachowania, ślad tylko w kodzie, danych i logach |
| operacyjne | ręczne wdrożenia, słaba obserwowalność, brak wycofania |
| bezpieczeństwo i zgodność | konkretna ekspozycja, nie sam wiek komponentu |
| wiedzy | tylko jedna osoba rozumie krytyczny proces |
| ekonomiczne | koszt opóźnienia bywa większy niż budżet utrzymania |

Czysta struktura klas nie kompensuje braku kontroli operacyjnej.

---

## 2.3. Minimalny zestaw dowodów

- Historia zmian plików i czas realizacji reprezentatywnych zmian.
- Incydenty, regresje, wycofane wdrożenia; zależności uruchomieniowe i integracyjne.
- Mapa odpowiedzialności, wyniki i czas testów, analiza statyczna z jej konfiguracją.
- Najbliższy plan zmian produktu i platformy.
- Braki danych **nazywaj wprost** - nie zastępuj ich pozorną dokładnością jednego skanera.

---

## 3.1. Dług techniczny - użyteczna metafora

- Kompromis jakościowy tworzący **warunkowe zobowiązanie** na przyszłość.
- **Kapitał** - koszt usunięcia i weryfikacji; **odsetki** - dodatkowy koszt, gdy kolejne prace go napotykają.
- Naliczanie odsetek zależy od tego, czy obszar będzie zmieniany.
- To analogia, nie model księgowy; konieczny kompromis architektoniczny nie jest automatycznie długiem.

---

## 3.2. Nie każdy problem jest długiem

| Sytuacja | Dług? |
| --- | --- |
| Funkcja jeszcze niezaplanowana | Zwykle nie - zakres produktu |
| Błędny wynik dla aktualnego wymagania | Nie - defekt (może być skutkiem długu) |
| Dwie usługi współdzielą schemat, wspólne wdrożenia | Kandydat - potwierdź koszt |
| Starsza, wspierana biblioteka spełniająca potrzeby | Nie na podstawie samego wieku |
| Brak testów wymusza 3 dni ręcznej regresji | Tak - powtarzalny koszt |
| Kod niezgodny z preferencjami recenzenta | Nie - brak konsekwencji |

---

## 3.3. Klasyfikacja i obszary długu

- **Świadomość:** świadomy (celowy kompromis) vs nieświadomy (widoczny po zdobyciu wiedzy).
- **Zarządzanie:** rozważny (korzyść, monitorowane ryzyko) vs lekkomyślny - osie można łączyć.
- Świadomy dług potrzebuje uzasadnienia, właściciela i warunków ponownej oceny.
- Obszary: kod i architektura, testy, dane, build i wdrożenie, platforma, obserwowalność, wiedza.

---

## 3.4. Opis elementu długu - przykład TD-017

| Pole | Wartość |
| --- | --- |
| Konstrukcja / przyczyna | trzy kopie reguły rabatu; rozwiązanie tymczasowe bez właściciela |
| Dowód / odsetki | dwie poprawki po pięciu zmianach rabatów; ok. 3 osobodni |
| Ekspozycja | nowy program lojalnościowy, pięć zmian rabatów w kwartale |
| Kapitał bieżący | 4-7 osobodni z testami i migracją konfiguracji |
| Decyzja / pewność | usunąć duplikację przed programem; pewność średnia |

Pełny szablon pól (m.in. właściciel i termin przeglądu): zadania modułu 1, Aktywność 1.2. Oszacowanie to **przedział**, nie obietnica.

---

## 3.5. Identyfikacja i pomiar bez fałszywej precyzji

- Źródła: warsztaty z zespołem, incydenty, historia repozytorium, analiza statyczna, plan produktu.
- Skaner nie zna wartości biznesowej, rozmowa bywa selektywna - **łącz dane ilościowe i jakościowe**.
- Nie istnieje jedna wiarygodna liczba całkowitego długu; „czas naprawy” z narzędzia pomija wiedzę i migrację.
- Mierz pytania decyzyjne: o ile dłużej trwa zmiana, jak często dotykamy obszaru, ile regresji.

---

## 3.6. Skala wpływu i priorytetyzacja

| Ocena | Interpretacja wpływu |
| --- | --- |
| 1 | lokalne utrudnienie bez wpływu na termin lub ryzyko |
| 2 | powtarzalna dodatkowa praca w jednym zespole |
| 3 | opóźnienie, ryzyko regresji, koordynacja kilku komponentów |
| 4 | istotne ograniczenie planu rozwoju lub niezawodności |
| 5 | zagrożenie ciągłości, bezpieczeństwa, zgodności lub celu biznesowego |

- Priorytet: wpływ + prawdopodobieństwo napotkania + pilność + koszt opóźnienia - koszt i ryzyko interwencji.
- To **model rozmowy**, nie wzór arytmetyczny; każda ocena wymaga uzasadnienia.

---

## 3.7. Strategie zarządzania długiem

- **Spłacić** - usunąć konstrukcję; **ograniczyć** - testy, interfejs, monitorowanie, izolacja.
- **Unikać** - inna ścieżka zmiany; **tolerować** - małe odsetki lub obszar do wycofania.
- **Przenieść** - produkt lub usługa; **obserwować** - sygnał i termin ponownej oceny.
- Celem nie jest „spłacenie całego długu”, lecz **kontrola ryzyka i kosztu ewolucji**.

---

## Aktywność 1.2: brakujące pola rekordu (3 min)

Zespół zapisał jedynie: *„Brak testów w imporcie, naprawa zajmie trzy dni”.*

- Wskaż co najmniej cztery brakujące pola szablonu długu.
- Dla każdego: dane historyczne, wiedza domenowa, eksperyment czy decyzja właściciela produktu?

**Zasada:** nie uzupełniaj nieznanych wartości założeniami przedstawionymi jako fakty.

---

## 4.1. *Code smells* jako hipotezy diagnostyczne

- *Code smell* to sygnał, który **może** wskazywać problem - nie dowód defektu ani nakaz refaktoryzacji.
- Długa metoda może łączyć odpowiedzialności - albo być liniowym algorytmem, którego podział zaszkodzi.

Dla każdego symptomu zapytaj:

1. Jaką konkretną zmianę utrudnia i jakie zachowanie może zostać naruszone?
2. Czy historia zmian potwierdza problem i czy rozchodzi się on po systemie?
3. Czy koszt pozostawienia przewyższa koszt i ryzyko interwencji?
4. Jaki najmniejszy krok dostarczy informacji albo ograniczy ryzyko?

---

## 4.2. Grupy symptomów

- **Rozmiar:** długa metoda, duża klasa, długa lista parametrów, głębokie zagnieżdżenie.
- **Odpowiedzialność:** wiele powodów zmiany, logika domenowa w kontrolerach lub skryptach.
- **Zmiana:** jedna zmiana w wielu odległych miejscach, wariant rozproszony po `if`/`switch`.
- **Reprezentacja:** wartości domenowe jako napisy, argument `boolean`, liczby bez nazw.
- **Zbędność:** duplikacja wiedzy, martwy kod, pośrednik bez wartości, abstrakcja bez zmienności.

---

## 4.3. Przykład: `LegacyOrderService` (fragment)

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

Materiał diagnostyczny do Ćwiczenia 1 - nie wzorcowa implementacja.

---

## 5.1. Metryka nie jest celem

- Jakość ma wiele wymiarów - pojedyncza metryka strukturalna nie mierzy ich wszystkich.
- Metryka ma odpowiadać na pytanie: schemat **Cel, Pytanie, Metryka**.
- Cel: mniej ryzyka w wycenie → pytanie: co jest często zmieniane i trudne do weryfikacji?
- Metryki: częstotliwość zmian, złożoność, czas testów, pokrycie zmienianego kodu, regresje.
- Dane bez pytania dają raporty precyzyjne z wyglądu, ale bezużyteczne dla decyzji.

---

## 5.2. Złożoność cyklomatyczna: `RiskClassifier`

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

## 5.3. Złożoność poznawcza

- Przybliża **wysiłek człowieka** potrzebny do zrozumienia przepływu (definicja z narzędzi, np. SonarQube).
- Każde przerwanie przepływu (`if`, pętla, `catch`) +1, a zagnieżdżenie dodaje narzut równy głębokości.
- `switch` i sekwencja jednakowych operatorów liczą się raz; wczesny `return` w klauzuli ochronnej nie zwiększa wyniku.
- Spłaszczenie warunków realnie ją obniża; uzupełnia złożoność cyklomatyczną, nie zastępuje jej.

---

## 5.4. Pokrycie linii a pokrycie gałęzi

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
- Tu 100% linii, ale tylko 2 z 4 gałęzi (50%); JaCoCo nie liczy ścieżek wyjątków.
- Nawet 100% gałęzi nie dowodzi poprawności - asercje mogą być słabe.

---

## 5.5. Pokrycie w kodzie legacy

- Dobrze wskazuje kod niewykonany, słabo - czy testy wykrywają ważne defekty.
- Pytaj: czy testy wykonują **zmieniane zachowanie** i obie strony zmienianych decyzji?
- Czy chronią przypadki biznesowe i historyczne regresje, szybko i powtarzalnie?
- Globalny procent może ukryć krytyczny moduł z niemal zerowym pokryciem.

---

## 5.6. Duplikacje: `SalesCalculations`

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

## 5.7. Sprzężenie i spójność

- **`Ce`** - od ilu typów moduł zależy; **`Ca`** - ile typów zależy od modułu (promień zmiany).
- **Niestabilność `I = Ce / (Ca + Ce)`:** blisko 0 - powinien być stabilny i abstrakcyjny, blisko 1 - łatwy do zmiany.
- **Cykle zależności** są kosztowne; **change coupling** z historii VCS ujawnia sprzężenie wiedzą.
- Spójność: rodzina **LCOM** lub test nazwania odpowiedzialności bez „oraz”.
- Liczy się kierunek: zależność od stabilnej abstrakcji jest tania, od zmiennego konkretu droga.

---

## 5.8. Łączenie metryk

| Sygnał | Interpretacja po połączeniu |
| --- | --- |
| wysoka złożoność | + częste zmiany = kosztowny obszar aktywnej pracy |
| niskie pokrycie | + planowana zmiana = większa niepewność zmiany |
| duplikacja | + wspólne zmiany i regresje = powielona wiedza |
| częste zmiany | + incydenty i szeroki promień = ryzyko |

- Kandydatów wskazuje **przecięcie sygnałów** i trend w czasie, nie pojedyncza wartość.
- Pulpit (znaczenie, ekspozycja, tarcie, ryzyko, struktura, zabezpieczenia) - mały, pod decyzję.

---

## 5.9. Antywzorce użycia metryk

- **Optymalizacja pod wynik** - np. testy bez asercji dla progu pokrycia.
- **Porównywanie nieporównywalnych** systemów, języków i narzędzi.
- **Korelacja jako przyczyna** - obniżenie metryki nie musi usunąć defektów.
- **Uśrednianie ryzyka** - średnia pakietu może ukryć krytyczny fragment.
- **Bramka bez analizy zmiany** - chroń aktywnie zmieniany kod, nie jeden próg dla całości.

---

## Aktywność 1.3: dobór metryk (4 min)

Cel: mniej regresji przy comiesięcznych zmianach reguł cenowych. Wybierz **trzy**, odrzuć **dwa**:

- globalne pokrycie linii całej aplikacji,
- liczba poprawek po wydaniu na zmianę reguł,
- pokrycie gałęzi zmienianych reguł przez testy z istotnymi asercjami,
- liczba klas w repozytorium,
- współzmienność plików z regułami cenowymi,
- maksymalna złożoność dowolnej metody w systemie.

**Zasada:** nazwij pytanie, na które odpowiada wskaźnik, i jedno ograniczenie interpretacji.

---

## 6.1. Kod jest zapisem dawnych decyzji

- Rozsądna decyzja traci trafność: nowe wymagania, skala, bezpieczeństwo, granice zespołów.
- **Organizacja:** stała presja terminów, bodźce liczące tylko funkcje, komponent bez właściciela.
- **Wiedza:** rotacja szkodzi, gdy wiedza nie trafia do testów, nazw i kontraktów.
- **Technika:** brak szybkich testów, kopiowanie zamiast wspólnej reguły, odkładane aktualizacje.
- Złożoność ma tendencję do wzrostu, jeśli nie wykonuje się **jawnej pracy nad jej ograniczaniem**.

---

## 6.2. Analiza przyczyny, nie winnego

„Programista skopiował kod” zatrzymuje analizę zbyt wcześnie. Zapytaj:

1. Dlaczego skopiowanie było najszybszą lub najbezpieczniejszą opcją?
2. Czy wspólna reguła miała właściciela i testy pozwalające ją zmienić?
3. Czy granice repozytoriów i zespołów utrudniały współdzielenie?
4. Jaka zmiana procesu zapobiegnie kolejnemu wystąpieniu?

Refaktoryzacja usuwa objaw - bez zmiany mechanizmu problem wróci.

---

## 7.1. Refaktoryzacja, przepisanie, modernizacja

- **Refaktoryzacja** zmienia strukturę bez zmiany obserwowalnego zachowania.
- **Przepisanie** tworzy nową implementację - nie dziedziczy automatycznie zachowań ani dojrzałości operacyjnej.
- Zachowania trzeba jawnie odzyskać, zweryfikować, przenieść albo świadomie odrzucić.
- **Modernizacja** jest szersza: platforma, interfejs, model danych, wymiana, zakup, migracja.
- Zacznij od **mierzalnego wyniku** (np. nowa reguła cenowa w dni zamiast tygodni), nie „nowoczesnego systemu”.

---

## 7.2. To nie jest wybór binarny

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

## 7.3. Kryteria decyzji

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

## 7.4. Pełny koszt przepisania

- Nie porównuj samej nowej implementacji z kosztem zmiany starego kodu.
- Dolicz: odzyskanie wymagań, utrzymanie i synchronizację starego systemu w trakcie prac.
- Migracja danych, odtworzenie integracji i niejawnych kontraktów, testy niefunkcjonalne.
- Gotowość operacyjna, uruchomienie równoległe, przełączenie, plan wycofania.
- Rzeczywiste **wyłączenie starego** kodu i infrastruktury - przepisanie wygrywa pełnym planem, nie estetyką.

---

## 7.5. Przewaga małych, odwracalnych kroków

1. Ustabilizuj budowanie, testy i obserwowalność obszaru.
2. Zidentyfikuj granicę funkcji i jej kontrakt; oddziel wywołujących adapterem.
3. Zbuduj nową implementację jednego przypadku i porównaj wyniki.
4. Rozszerzaj zakres, na końcu usuń starą ścieżkę i elementy przejściowe.

**Strangler Fig** stopniowo przejmuje funkcje na granicach systemu. Każdy element przejściowy potrzebuje właściciela i warunku usunięcia.

---

## 7.6. Sygnały ostrzegawcze i granice refaktoryzacji

- **Przepisanie ryzykowne:** motywacją jest niechęć do kodu lub nowa technologia.
- Brak migracji danych, porównania wyników i wycofania; wartość dopiero po latach.
- Przyczyny organizacyjne bez zmian - nowy system odziedziczy te same mechanizmy.
- **Refaktoryzacja może nie wystarczyć:** platforma nie spełni wymagań bezpieczeństwa lub skali, proces zmienia się fundamentalnie, zależność bez wsparcia.
- Nawet wtedy można wymienić krytyczny komponent, zachowując resztę.

---

## 7.7. Proces podjęcia decyzji

1. Nazwij problem, mierzalny wynik, krytyczne zachowania i ograniczenia.
2. Zbierz dane o zmianach, ryzyku, platformie, danych i integracjach.
3. Rozważ **co najmniej trzy strategie**, w tym pozostawienie lub ograniczenie ryzyka.
4. Oszacuj pełny koszt przejścia, działania równoległego i wyłączenia.
5. Wykonaj ograniczony czasowo eksperyment dla najważniejszej niewiadomej.
6. Zapisz założenia, sygnały sukcesu, warunki przerwania i termin ponownej oceny.

Decyzja jest **hipotezą** - nowe informacje mogą uzasadnić zmianę kierunku.

---

## Aktywność 1.4: wybór najmniejszego kroku (4 min)

Platforma wspierana jeszcze dwa lata; krytyczny moduł zmienia się co tydzień, nie ma szybkich testów i powoduje większość regresji.

- Porównaj: pełne przepisanie, testy charakteryzujące modułu, wymiana platformy.
- Wybierz jeden krok: założenie do weryfikacji, miernik wyniku, warunek przerwania.

**Pamiętaj:** te informacje nie wystarczą do zatwierdzenia kompletnej strategii modernizacji.

---

## Ćwiczenie 1: obserwacja bez pochopnej naprawy (20 min)

- Cel: w `LegacyOrderService` oddzielić fakty, hipotezy i niewiadome przed planowaną zmianą (Czechy, `PARTNER`, odbiór osobisty).
- Co najmniej pięć faktów, trzy hipotezy, pytania domenowe i potrzebne dowody.
- **Nie proponuj** wzorców ani docelowej refaktoryzacji.

Szczegóły: zadania modułu 1, Ćwiczenie 1.

---

## Ćwiczenie 2: klasyfikacja i priorytetyzacja długu (30 min)

- Cel: pokazać, że najgorszy wynik metryki nie musi oznaczać najwyższego priorytetu.
- Pięciu kandydatów: klasyfikacja, pełny rekord długu, plan na **6 osobodni** ze świadomymi rezygnacjami.
- Bez wymyślonych wartości; ponowna ocena `AnnualReportFormatter` przy zmienionej ekspozycji.

Szczegóły: zadania modułu 1, Ćwiczenie 2.

---

## Ćwiczenie 3: wybór strategii modernizacji (50 min)

- Cel: rekord decyzji dla `ClaimsCore` - skrócić wdrożenie reguły z 15 do 3 dni bez obniżenia dostępności.
- Grupy 3-4 osób; co najmniej trzy strategie, w tym inna niż refaktoryzacja i przepisanie.
- Pierwszy odwracalny krok w 4 tygodnie; **mierniki wyniku**, nie aktywności.

Szczegóły: zadania modułu 1, Ćwiczenie 3.

---

## Kluczowe wnioski z omówienia

- „Podatek jest liczony błędnie” to hipoteza - bez reguły biznesowej nie wynika z kodu.
- `LegacyOrderService` jest testowalna - pierwszy krok to testy charakteryzujące, nie przepisanie.
- `TaxRoundingRule` to **defekt**, nie dług; `PricingService` to najlepiej potwierdzony dług.
- `AnnualReportFormatter` ma najgorszą metrykę, ale krótki horyzont życia - brak dowodu przyszłego kosztu.
- `ClaimsCore`: przyrostowe wydzielanie modułu reguł, uruchomienie równoległe i raport rozbieżności.

Pełne omówienie: rozwiązania modułu 1.

---

## Sprawdzenie wiedzy

1. Dlaczego wiek kodu nie wystarcza do określenia go jako legacy?
2. Czym różni się symptom jakości od defektu?
3. Co oznaczają kapitał i odsetki w metaforze długu?
4. Dlaczego 100 procent pokrycia linii nie dowodzi poprawności testów?
5. Co mierzy złożoność cyklomatyczna, a czego nie mierzy?
6. Dlaczego identyczny fragment kodu nie zawsze powinien zostać uogólniony?
7. Jakie koszty pełnego przepisania są często pomijane?

---

## Lista kontrolna oceny systemu legacy

- **Wartość i zakres:** znani użytkownicy, funkcje krytyczne, horyzont życia, oczekiwany wynik.
- **Zachowanie:** odtwarzamy ważne przypadki; odróżniamy zamierzone zachowanie, wyjątek historyczny i defekt.
- **Struktura i zmiana:** obszary często zmieniane, współzmienność, cykle; metryki łączone z historią.
- **Testy i operacje:** powtarzalny build, czas testów, pokrycie zmienianego zachowania, przećwiczone wycofanie.
- **Dług i strategia:** dług ma lokalizację, konsekwencję i właściciela; rozważono >2 opcje, architektura przejściowa ma plan usunięcia.

---

## Podsumowanie - najważniejsze wnioski

- Legacy to nie synonim starego kodu - decydują **wartość systemu oraz tarcie i ryzyko zmiany**.
- Diagnoza łączy fakt, kontekst, konsekwencję i dowód; *code smells* są hipotezami.
- Dług techniczny dotyczy przyszłego kosztu zmian, a nie każdego defektu czy starej technologii.
- Metryki nabierają znaczenia w połączeniu z planem zmian, historią i krytycznością.
- Celem jest świadoma kontrola kosztów i ryzyka, nie usunięcie całego długu.
- Bezpieczny kierunek zaczyna się od **małego kroku**, który daje wiedzę lub mierzalną wartość.
