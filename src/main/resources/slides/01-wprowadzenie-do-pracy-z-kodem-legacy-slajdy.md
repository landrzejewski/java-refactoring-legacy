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
8. Warsztat diagnostyczny

---

## Czym jest kod legacy - trzy perspektywy

- **Historyczna:** kod odziedziczony po wcześniejszych zespołach, decyzjach i ograniczeniach. Sam wiek nie przesądza jednak o jakości - stary komponent może być stabilny i przetestowany, a nowy kłopotliwy.
- **Operacyjna:** kod, którego zmiana wiąże się z nieproporcjonalnie dużą niepewnością lub kosztem. Tu leży sedno problemu w codziennej pracy.
- **Biznesowa:** system nadal dostarcza wartość, ale koszt i ryzyko zmian zaczynają ograniczać realizację celów organizacji.
- Kod nieużywany, który można bezpiecznie usunąć, to kod martwy - nie wymaga modernizacji. Legacy jest istotny, bo wciąż ma użytkowników i zależności.

---

## Perspektywa operacyjna - typowe sygnały

- Brak szybkiej informacji zwrotnej po zmianie, trudne uruchomienie lokalne oraz długi lub niestabilny proces budowania.
- Niejawne zależności od danych, kolejności operacji albo środowiska sprawiają, że lokalna zmiana ma rozległe skutki.
- Zachowania nie da się odtworzyć z testów i dokumentacji, a wiedza o krytycznych regułach skupia się u pojedynczych osób.
- Definicja „kod legacy to kod bez testów” jest użyteczną heurystyką: bez sieci bezpieczeństwa trudno stwierdzić, czy system nadal działa poprawnie.
- Nie jest jednak kompletna - wolne, niestabilne lub pozbawione asercji testy też nie dają bezpieczeństwa.

---

## Legacy jest relacją, a nie etykietą

- Ten sam komponent może być neutralny w jednym kontekście i problematyczny w innym - ocena zależy od planowanych zmian.
- Stabilny moduł rozliczeniowy niezmieniany od lat może nie uzasadniać inwestycji. Gdy jednak dochodzą nowe rynki i comiesięczne zmiany podatków, brak testowalności staje się realną przeszkodą.
- Kluczowe pytanie diagnostyczne:

> Czy obecna konstrukcja systemu utrudnia zmiany, których rzeczywiście będziemy potrzebować?

- Nie zaczynamy od pytania, czy kod jest wystarczająco elegancki.

---

## Charakterystyczne właściwości

Pojedyncza właściwość nie przesądza o diagnozie - ryzyko wynika z ich kombinacji.

| Obszar | Pytanie diagnostyczne |
| --- | --- |
| Zachowanie | Skąd wiemy, co musi pozostać niezmienione? |
| Struktura | Jak daleko rozchodzi się typowa zmiana? |
| Technologia | Czy system można bezpiecznie budować, uruchamiać i aktualizować? |
| Dane | Jakie reguły są zapisane w danych, skryptach i procedurach? |
| Operacje | Jak szybko wykryjemy problem i przywrócimy usługę? |
| Wiedza | Ile osób potrafi bezpiecznie zmienić krytyczny fragment? |
| Przepływ pracy | Gdzie rzeczywiście powstaje opóźnienie? |

---

## Czego nie wolno zakładać

- Stary kod nie musi być kodem złej jakości, a monolit nie musi być legacy - mikrousługi też nie gwarantują łatwości zmian.
- Brak najnowszej wersji frameworka nie jest sam w sobie długiem technicznym, a duża liczba linii nie dowodzi nadmiernej złożoności.
- Niskie pokrycie testami nie dowodzi istnienia defektów, a wysokie pokrycie nie dowodzi poprawności.
- Obecność symptomu nie przesądza, że refaktoryzacja przyniesie dodatni zwrot z inwestycji - to zawsze wymaga osobnej oceny.

---

## Od obserwacji do decyzji - rozróżniaj pojęcia

| Pojęcie | Znaczenie | Typowa reakcja |
| --- | --- | --- |
| Defekt | zachowanie niezgodne z wymaganiem | naprawa i zabezpieczenie testem |
| *Code smell* | sygnał możliwego problemu projektowego | analiza kontekstu i kosztu zmian |
| Dług techniczny | konstrukcja zwiększająca koszt przyszłych zmian | spłacić, ograniczyć, tolerować, monitorować |
| Ryzyko | niepewne zdarzenie wpływające na cel | ocena prawdopodobieństwa i wpływu |
| Ograniczenie | warunek poza kontrolą zespołu | adapter, negocjacja, plan migracji |

Kategorie mogą się łączyć (np. sprzężenie jako symptom, dług i przyczyna defektu), ale nadal są to różne obserwacje wymagające różnych działań.

---

## Język diagnozy

- „Ten kod jest zły” nie pozwala podjąć decyzji. Przydatna diagnoza ma cztery części: **fakt, kontekst, konsekwencję i dowód**.
- **Fakt** mówi, co i gdzie zaobserwowano; **kontekst** - jaka zmiana lub cecha jakości jest istotna.
- **Konsekwencja** wskazuje powstający koszt lub ryzyko, a **dowód** - dane, które ją potwierdzają.

> Zmiana reguły rabatowej wymaga modyfikacji trzech klas i dwóch skryptów. W ostatnich sześciu miesiącach dwa z pięciu wdrożeń wymagały poprawki, bo jedna kopia reguły pozostała niezmieniona. Kolejne warianty rabatu są w planie - to uzasadnia usunięcie rozproszonej reguły.

---

## Ćwiczenie: fakt czy ocena (3 min)

Wskaż, czy stwierdzenie opisuje fakt, hipotezę, defekt, symptom czy ograniczenie (może należeć do kilku kategorii):

1. Metoda ma 142 linie i złożoność cyklomatyczną równą 24.
2. Metoda jest niemożliwa do utrzymania.
3. Wynik dla zatwierdzonego przykładu różni się od wymaganej kwoty.
4. Producent środowiska wykonawczego zakończy dostarczanie poprawek za sześć miesięcy.

Następnie przepisz stwierdzenie 2 jako diagnozę: fakt, kontekst, konsekwencja i potrzebny dowód.

---

## Koszt zmiany to więcej niż pisanie kodu

- Obejmuje odnalezienie miejsc do zmiany, odzyskanie wiedzy o zachowaniu oraz przygotowanie środowiska i danych.
- Dochodzą testy i analiza regresji, koordynacja między zespołami, a także wdrożenie, obserwacja i ewentualne wycofanie.
- Trzeba też doliczyć usuwanie skutków nieudanych zmian, które w systemach legacy bywa kosztowne.
- Jeżeli kod powstaje w godzinę, a uzyskanie pewności przed wdrożeniem trwa pięć dni, problemem jest **koszt informacji zwrotnej**, a nie tempo pisania.

---

## Kategorie ryzyka (1/2)

- **Ryzyko funkcjonalne:** zmiana może naruszyć zachowania, których zespół nie zna. Dojrzałe systemy zawierają wyjątki powstałe z realnych przypadków, często nieopisane w wymaganiach.
- Jedynym śladem tej wiedzy mogą być kod, dane, konfiguracja, logi i zachowanie produkcyjne.
- **Ryzyko operacyjne:** ręczne wdrożenia, niepowtarzalne środowiska, słaba obserwowalność i brak sprawdzonego wycofania zwiększają prawdopodobieństwo i czas trwania incydentu.
- Czysta struktura klas nie kompensuje braku kontroli operacyjnej.

---

## Kategorie ryzyka (2/2)

- **Bezpieczeństwo i zgodność:** nieaktualizowane zależności lub niewspierane środowisko mogą wymusić modernizację. Liczy się konkretna ekspozycja i model zagrożeń, a nie sam wiek komponentu.
- **Ryzyko wiedzy:** gdy tylko jedna osoba rozumie krytyczny proces, organizacja jest od niej zależna. Dokumentacja pomaga, ale nie zastąpi zdolności wielu osób do bezpiecznej zmiany.
- **Ryzyko ekonomiczne:** długi czas realizacji generuje koszt opóźnienia - utracone przychody, spóźnioną zgodność, ręczne operacje. Bywa on większy niż budżet utrzymania.

---

## Koszt nie jest równomierny

- Największą uwagę powinny otrzymać miejsca, które **jednocześnie** często się zmieniają i mają istotne znaczenie biznesowe lub operacyjne.
- Dodatkowo liczy się trudność zrozumienia i weryfikacji, szeroki promień oddziaływania oraz brak wyraźnego właściciela.
- Skomplikowany, ale stabilny parser starego formatu może wymagać jedynie monitorowania.
- Umiarkowanie skomplikowany moduł cenowy zmieniany co tydzień bywa znacznie lepszym celem inwestycji.

---

## Minimalny zestaw dowodów

- Przed większą interwencją zbierz historię zmian plików i komponentów oraz czas realizacji reprezentatywnych zmian.
- Uwzględnij incydenty, regresje i wycofane wdrożenia z danego obszaru, a także zależności uruchomieniowe i integracyjne.
- Potrzebna jest mapa odpowiedzialności zespołów, wyniki i czas testów oraz dane z analizy statycznej wraz z konfiguracją narzędzia.
- Kontekst przyszłości daje najbliższy plan zmian produktu i platformy.
- Braki danych nazywaj wprost i uzupełniaj stopniowo, zamiast zastępować je pozornie dokładnym wynikiem jednego skanera.

---

## Dług techniczny - użyteczna metafora

- Dług to celowo pozostawiony albo ujawniony z czasem kompromis dotyczący jakości technicznej, który tworzy **warunkowe zobowiązanie** na przyszłość.
- **Kapitał** to przybliżony koszt usunięcia konstrukcji i bezpiecznego zweryfikowania zmiany; **odsetki** to dodatkowy koszt ponoszony, gdy kolejne prace ją napotykają.
- Prawdopodobieństwo naliczania odsetek zależy od tego, czy obszar będzie zmieniany, a korzyścią z zaciągnięcia długu może być szybsze dostarczenie wartości lub wiedzy.
- To analogia, nie model księgowy - odsetki są warunkowe i trudne do oddzielenia od innych kosztów.
- Konieczny kompromis architektoniczny nie jest automatycznie długiem tylko dlatego, że jakaś zmiana jest kosztowna.

---

## Nie każdy problem jest długiem

| Sytuacja | Dług? |
| --- | --- |
| Funkcja nie została jeszcze zaplanowana | Zwykle nie - brak zakresu produktu |
| Błędny wynik dla aktualnego wymagania | Nie - to defekt (może być skutkiem długu) |
| Dwie usługi współdzielą schemat, zmiany wymagają wspólnego wdrożenia | Kandydat - trzeba potwierdzić koszt |
| Starsza, ale wspierana biblioteka spełniająca potrzeby | Nie na podstawie samego wieku |
| Brak testów wymusza 3 dni ręcznej regresji na wydanie | Tak - powtarzalny koszt |
| Kod niezgodny z preferencjami recenzenta | Nie - brak konsekwencji |

Zbyt szeroka etykieta „dług techniczny” utrudnia priorytetyzację i osłabia metaforę.

---

## Dwie niezależne osie klasyfikacji

- **Świadomość:** dług świadomy (kompromis podjęty celowo) vs nieświadomy (problem widoczny dopiero po zdobyciu wiedzy lub zmianie kontekstu).
- **Sposób zarządzania:** dług rozważny (istotna korzyść, ograniczone i monitorowane ryzyko) vs lekkomyślny (konsekwencje ignorowane).
- Osie można łączyć - dług może być np. nieświadomy, ale po odkryciu rozważnie zarządzany.
- Świadomy dług nie jest automatycznie dobry: potrzebuje uzasadnienia, właściciela, warunków ponownej oceny i strategii ograniczania skutków.
- Dług nieświadomy nie zawsze oznacza błąd zespołu - projektowanie to proces uczenia się, a trafna decyzja może z czasem się zdezaktualizować.

---

## Typowe obszary długu

- Kod i projekt klas oraz architektura i granice komponentów - najczęściej kojarzone z długiem, ale nie jedyne.
- Automatyzacja testów, a także dane, ich jakość i modelowanie.
- Proces budowania, wdrożenie i infrastruktura oraz zależności i platforma wykonawcza.
- Obserwowalność i obsługa operacyjna, dokumentacja decyzji i wiedza potrzebna do zmiany.
- Lista nie służy do rejestrowania każdej niedoskonałości - pomaga uniknąć redukowania długu do wyników analizy statycznej.

---

## Opis elementu długu - szablon

| Pole | Pytanie |
| --- | --- |
| Lokalizacja / Konstrukcja | Czego dotyczy i jaki element projektu powoduje przyszły koszt? |
| Przyczyna / Konsekwencja | Dlaczego powstał? Jak wpływa na koszt, czas, jakość, możliwość zmiany? |
| Dowód / Zaobserwowane odsetki | Co potwierdza konsekwencję? Jaki koszt już poniesiono? |
| Ekspozycja / Kapitał bieżący | Jakie przyszłe zmiany naliczą odsetki? Ile kosztuje usunięcie? |
| Ryzyko spłaty / Decyzja | Co może pójść źle? Spłacamy, ograniczamy, tolerujemy, obserwujemy? |
| Pewność / Właściciel i przegląd | Jak silne są dowody? Kto i kiedy ponownie oceni decyzję? |

Element długu musi być konkretny i powiązany z artefaktem, decyzją albo granicą systemu.

---

## Przykład rekordu: TD-017

| Pole | Wartość |
| --- | --- |
| Konstrukcja | trzy niezależne kopie reguły rabatu lojalnościowego |
| Przyczyna | rozwiązanie tymczasowe przed kampanią, bez wspólnego właściciela |
| Dowód / odsetki | dwie poprawki po pięciu zmianach rabatów; ok. 3 dodatkowe osobodni |
| Ekspozycja | nowy program lojalnościowy i pięć zmian rabatów w kwartale |
| Kapitał bieżący | 4-7 osobodni wraz z testami i migracją konfiguracji |
| Decyzja / pewność | usunąć duplikację przed programem; pewność średnia |

Oszacowanie to przedział, nie obietnica - obejmuje testy, wdrożenie, migrację i usunięcie starej ścieżki.

---

## Identyfikacja długu - źródła informacji

- Warsztaty z programistami, testerami, operatorami i ekspertami domenowymi ujawniają koszt znany z praktyki.
- Analiza zdarzeń (incydenty, regresje, nieudane wdrożenia) oraz historia repozytorium (częstotliwość zmian, pliki zmieniane razem, koncentracja autorstwa).
- Analiza statyczna (zależności, cykle, złożoność, duplikacje) i analiza platformy (wsparcie, podatności, odtwarzalność środowiska).
- Plan produktu pokazuje, gdzie przyszłe zmiany napotkają istniejące ograniczenia.
- Skaner nie zna wartości biznesowej, a rozmowa bywa podatna na pamięć selektywną - dopiero połączenie danych ilościowych i jakościowych daje mocną podstawę.

---

## Pomiar bez fałszywej precyzji

- Nie istnieje jedna wiarygodna liczba opisująca całkowity dług. Automatyczny „czas naprawy” zależy od reguł narzędzia i pomija koszt wiedzy, regresji, migracji i koordynacji.
- Zamiast sumy mierz pytania istotne dla decyzji: o ile dłużej trwa zmiana, jak często dotykamy obszaru, jak często zmiany powodują regresję.
- Warto też pytać o liczbę koordynowanych komponentów, czas wiarygodnej weryfikacji, horyzont wsparcia platformy i zablokowane cele biznesowe.

---

## Skala porządkowa wpływu

Można stosować skalę porządkową, jeżeli jej znaczenie jest jawne:

| Ocena | Interpretacja wpływu |
| --- | --- |
| 1 | lokalne utrudnienie bez istotnego wpływu na termin lub ryzyko |
| 2 | powtarzalna dodatkowa praca w obrębie jednego zespołu |
| 3 | zauważalne opóźnienie, ryzyko regresji albo koordynacja kilku komponentów |
| 4 | istotne ograniczenie planu rozwoju produktu lub niezawodności usługi |
| 5 | zagrożenie ciągłości, bezpieczeństwa, zgodności albo kluczowego celu biznesowego |

Wynik nie jest pomiarem fizycznym - skala porządkuje rozmowę, a każda ocena potrzebuje krótkiego uzasadnienia.

---

## Priorytetyzacja długu

- Najwyższy priorytet mają elementy, w których współwystępują wysoka konsekwencja, duże prawdopodobieństwo napotkania i rosnące odsetki.
- Musi też istnieć akceptowalny koszt interwencji i możliwość bezpiecznego ograniczenia ryzyka.

```text
Priorytet zależy od:
    wpływu konsekwencji
  + prawdopodobieństwa napotkania
  + pilności
  + kosztu opóźnienia
  - kosztu i ryzyka interwencji
```

- To model rozmowy, a nie wzór arytmetyczny - punkty mogą pomóc porównać podobne elementy, ale nie powinny automatycznie decydować.

---

## Strategie zarządzania długiem

- **Spłacić** - usunąć konstrukcję powodującą koszt; **ograniczyć** - dodać testy, interfejs, monitorowanie lub izolację.
- **Unikać** - poprowadzić zmianę inną ścieżką, jeśli nie przenosi to problemu gdzie indziej.
- **Tolerować** - świadomie pozostawić, gdy odsetki są małe lub obszar zostanie wycofany.
- **Przenieść** - zastąpić komponent produktem lub usługą (powstają nowe zależności); **obserwować** - ustalić sygnał i termin ponownej oceny.
- „Spłacenie całego długu” nie jest rozsądnym celem - celem jest kontrola ryzyka i kosztu ewolucji.

---

## Ćwiczenie: brakujące pola rekordu (3 min)

Zespół zapisał jedynie:

> „Brak testów w imporcie, naprawa zajmie trzy dni”.

- Wskaż co najmniej cztery brakujące pola z szablonu długu.
- Dla każdego określ, czy potrzebujesz danych historycznych, wiedzy domenowej, eksperymentu technicznego czy decyzji właściciela produktu.
- Nie uzupełniaj nieznanych wartości założeniami przedstawionymi jako fakty.

---

## *Code smells* jako hipotezy diagnostyczne

- *Code smell* to łatwo zauważalny sygnał, który **może** wskazywać głębszy problem projektowy - nie jest dowodem defektu ani nakazem refaktoryzacji.
- Długa metoda może łączyć kilka odpowiedzialności i utrudniać testowanie - albo przedstawiać liniowy algorytm, którego podział tylko by zaszkodził.
- Ocena zawsze zależy od kontekstu planowanych zmian.

---

## Grupy symptomów (1/2)

- **Rozmiar i obciążenie poznawcze:** długa metoda, duża klasa, długa lista parametrów, wielopoziomowe warunki, dużo stanów pośrednich.
- **Problemy z odpowiedzialnością:** klasa zmienia się z wielu niezależnych powodów, metoda intensywnie korzysta z danych innego obiektu.
- Logika domenowa trafia do kontrolerów, skryptów lub warstwy dostępu do danych, a jeden moduł zna szczegóły wielu niepowiązanych procesów.
- **Problemy ze zmianą:** jedna zmiana wymaga modyfikacji wielu odległych miejsc, a te same pliki regularnie zmieniają się razem.
- Wariant zachowania rozproszony jest po `if`/`switch`, a nowe pole przechodzi przez wiele warstw bez lokalnej odpowiedzialności.

---

## Grupy symptomów (2/2)

- **Problemy z reprezentacją:** wartości domenowe jako dowolne napisy lub liczby, argument `boolean` wybierający odrębne zachowania.
- Kilka parametrów stale występuje razem, a znaczące liczby i napisy nie mają nazw opisujących regułę.
- **Zbędność i pośrednictwo:** duplikacja wiedzy lub reguły oraz martwy kod.
- Klasa jedynie przekazuje każde wywołanie dalej, a abstrakcja istnieje bez rzeczywistej zmienności lub potrzeby.

---

## Przykład: `LegacyOrderService` (fragment)

```java
public Receipt placeOrder(Order order, String customerType,
        boolean express, String destinationCountry) {
    // ... walidacja, pętla po pozycjach:
    if ("VIP".equals(customerType)) lineValue = lineValue.multiply(new BigDecimal("0.90"));
    if (line.quantity() >= 10)     lineValue = lineValue.multiply(new BigDecimal("0.95"));
    // wysyłka
    if (express) shipping = new BigDecimal("39.99");
    else if (subtotal.compareTo(new BigDecimal("200.00")) >= 0) shipping = BigDecimal.ZERO;
    else shipping = new BigDecimal("14.99");
    // podatek
    if ("PL".equals(destinationCountry)) tax = subtotal.multiply(new BigDecimal("0.23"));
    else if ("DE".equals(destinationCountry)) tax = subtotal.multiply(new BigDecimal("0.19"));
    else tax = BigDecimal.ZERO;
    repository.save(order.id(), total);
    mailGateway.send(order.customerEmail(), "Order total: " + total);
    return new Receipt(order.id(), total);
}
```

Materiał diagnostyczny do ćwiczenia 1 - nie wzorcowa implementacja.

---

## Procedura analizy symptomu

Dla każdego zauważonego symptomu zapytaj:

1. Jaką konkretną zmianę utrudnia i jakie zachowanie może zostać naruszone?
2. Czy historia zmian potwierdza problem?
3. Czy problem jest lokalny, czy rozchodzi się po systemie?
4. Czy koszt pozostawienia przewyższa koszt i ryzyko interwencji?
5. Jaki najmniejszy krok dostarczy nowej informacji albo ograniczy ryzyko?

Procedura chroni przed mechanicznym usuwaniem symptomów tylko po to, by poprawić wynik narzędzia.

---

## Metryka nie jest celem

- Jakość ma wiele wymiarów (funkcjonalność, niezawodność, bezpieczeństwo, wydajność, utrzymywalność) - pojedyncza metryka strukturalna nie mierzy ich wszystkich.
- Metryka jest użyteczna, gdy odpowiada na konkretne pytanie - stąd schemat **Cel, Pytanie, Metryka**.
- Cel: zmniejszyć ryzyko zmian w wycenie. Pytanie: które fragmenty są często zmieniane i trudne do zweryfikowania?
- Metryki: częstotliwość zmian, złożoność, czas testów, pokrycie zmienianego kodu, liczba regresji - a potem potwierdzenie mechanizmu problemu.
- Dane zbierane bez pytania dają raporty, które wyglądają precyzyjnie, ale nie wspierają decyzji.

---

## Złożoność cyklomatyczna

- Opisuje strukturę przepływu sterowania: `V(G) = E - N + 2P`, a dla decyzji binarnych - liczba punktów decyzyjnych plus jeden.
- Odpowiada liczbie liniowo niezależnych ścieżek, więc podpowiada, ile decyzji rozważyć przy testowaniu. Nie jest liczbą wszystkich ścieżek wykonania.
- Nie mierzy trudności domeny ani czytelności i nie uwzględnia zagnieżdżenia - płaskie klauzule ochronne i głębokie zagnieżdżenie mogą dać ten sam wynik.
- Wyniki narzędzi się różnią (operatory logiczne, `switch`, wyjątki; JaCoCo liczy na kodzie bajtowym) - porównuj tylko w tym samym narzędziu i konfiguracji.

---

## Przykład: `RiskClassifier`

```java
public static int riskLevel(OrderSummary order) {
    int score = 0;
    if (order.total().compareTo(new BigDecimal("1000.00")) > 0) {
        score++;
    }
    if (order.international()) {
        score++;
    }
    for (Item item : order.items()) {
        if (item.fragile()) {
            score++;
        }
    }
    return score;
}
```

Cztery punkty decyzyjne (dwa `if`, pętla `for`, `if` w pętli), więc złożoność cyklomatyczna wynosi **5**.

---

## Złożoność cyklomatyczna - jak interpretować

- Wysoka wartość to sygnał do pytań: czy metoda realizuje kilka niezależnych reguł i czy wszystkie istotne warianty da się wiarygodnie przetestować?
- Czy złożoność wynika z problemu domenowego, czy ze struktury implementacji?
- Czy planowane zmiany dodadzą kolejne rozgałęzienia i czy decyzje można nazwać i rozdzielić bez zaciemniania algorytmu?
- Nie istnieje uniwersalny próg „błędnej” metody. Próg w narzędziu może wyzwalać przegląd, ale nie zastępuje diagnozy.

---

## Złożoność poznawcza

- Powstała jako odpowiedź na słabość złożoności cyklomatycznej: przybliża wysiłek człowieka potrzebny do zrozumienia przepływu. Definicja pochodzi z narzędzi (m.in. SonarQube), nie z normy.
- Każde przerwanie liniowego przepływu (`if`, pętla, `catch`) dodaje punkt, a zagnieżdżenie dodaje narzut równy głębokości.
- `switch` liczy się raz, sekwencja jednakowych operatorów logicznych też raz, a wczesny `return` w klauzuli ochronnej nie zwiększa wyniku.
- Dlatego spłaszczenie warunków (klauzule ochronne, wydzielenie metod) realnie obniża metrykę.
- Uzupełnia złożoność cyklomatyczną, ale jej nie zastępuje i nadal nie mierzy trudności pojęciowej domeny.

---

## Pokrycie testami

- Pokrycie mówi, które elementy programu zostały **wykonane** w danym uruchomieniu testów: instrukcje kodu bajtowego, linie, gałęzie, metody i klasy.
- `pokrycie = elementy wykonane / wszystkie mierzone elementy` - znaczenie „elementu” zależy od licznika i narzędzia.
- W JaCoCo linia jest wykonana, gdy wykonano choć jedną jej instrukcję; pokrycie gałęzi obejmuje `if` i `switch`, ale nie ścieżki wyjątków.
- Kod syntetyczny generowany przez kompilator może dawać wyniki niezgodne z intuicyjnym odczytem źródła.

---

## Pokrycie linii a pokrycie gałęzi

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

- Test wykonuje każdą linię (100% linii), ale żadnej fałszywej gałęzi - to tylko 2 z 4 gałęzi, czyli 50%.
- Nawet 100% gałęzi nie dowodzi poprawności: testy mogą nie mieć istotnych asercji, pomijać interakcje warunków albo utrwalać niezamierzone zachowanie.

---

## Pokrycie w kodzie legacy

- Pokrycie dobrze wskazuje kod, którego testy nie wykonały, ale słabo odpowiada, czy testy wykrywają ważne defekty.
- Kluczowe pytania: czy testy wykonują zachowanie, które zamierzamy zmienić, i czy obejmują obie strony zmienianych decyzji?
- Czy chronią ważne przypadki biznesowe i historyczne regresje, a wynik pochodzi z szybkich, powtarzalnych testów?
- Czy niepokryty kod jest osiągalny i istotny?
- Globalny procent może ukryć krytyczny moduł z niemal zerowym pokryciem - analizuj zakres zmiany i ryzyko, nie średnią dla repozytorium.

---

## Duplikacje

- Narzędzia wykrywają kopie identyczne, kopie ze zmienionymi nazwami, kopie z dodanymi instrukcjami oraz - najtrudniej - podobne zachowanie o innej strukturze.
- Duplikacja jest kosztowna, gdy reprezentuje **tę samą wiedzę**, która musi zmieniać się razem.
- Podobne fragmenty mogą jednak należeć do różnych reguł - ich przedwczesne połączenie tworzy błędną zależność.
- Wynik zależy od minimalnej długości fragmentu, tokenizacji, wykluczeń i mianownika - procentów z różnych narzędzi nie porównuj.
- Ważniejsze od udziału procentowego jest pytanie, czy kopie zmieniają się razem i czy niespójne aktualizacje powodują defekty.

---

## Przykład: `SalesCalculations`

```java
public static BigDecimal invoiceLineTotal(BigDecimal unitPrice, int quantity, boolean vip) {
    BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(quantity));
    if (vip) { total = total.multiply(new BigDecimal("0.90")); }
    return total;
}

public static BigDecimal quoteLineTotal(BigDecimal unitPrice, int quantity, boolean vip) {
    BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(quantity));
    if (vip) { total = total.multiply(new BigDecimal("0.90")); }
    return total;
}
```

- Przed połączeniem ustal, czy faktura i oferta rzeczywiście korzystają z jednej reguły rabatowej.
- Jeśli tak - rozproszenie grozi niespójną zmianą; jeśli polityki mają się rozwijać niezależnie - podobieństwo może być przejściowe.

---

## Sprzężenie i spójność

- **Efferent coupling (`Ce`)** - od ilu typów moduł zależy; **afferent coupling (`Ca`)** - ile typów zależy od modułu, czyli jak szeroki jest promień zmiany.
- **Niestabilność `I = Ce / (Ca + Ce)`:** blisko 0 - moduł powinien być stabilny i abstrakcyjny; blisko 1 - łatwy do zmiany.
- **Cykle zależności** między pakietami uniemożliwiają pracę w izolacji i należą do najkosztowniejszych znalezisk; **change coupling** z historii VCS ujawnia sprzężenie wiedzą.
- Spójność mierzy rodzina **LCOM** - rozłączne grupy metod i pól sugerują podział klasy; prostszym testem jest nazwanie odpowiedzialności bez „oraz”.
- Celem nie jest zero zależności - liczy się kierunek: zależność od stabilnej abstrakcji jest tania, od zmiennego konkretu droga.

---

## Łączenie metryk

| Sygnał | Interpretacja po połączeniu |
| --- | --- |
| wysoka złożoność | + częste zmiany = kosztowny obszar aktywnej pracy |
| niskie pokrycie | + planowana zmiana = większa niepewność tej zmiany |
| duplikacja | + wspólne zmiany i regresje = powielona wiedza |
| częste zmiany | + incydenty i szeroki promień = ryzyko |
| duży rozmiar | + wiele odpowiedzialności i właścicieli = trudna analiza |

- Najlepszych kandydatów wskazuje przecięcie kilku sygnałów, a nie pojedyncza wartość.
- Obserwuj trend w czasie - jednorazowy wynik może być anomalią lub skutkiem konfiguracji.

---

## Antywzorce użycia metryk

- **Optymalizacja pod wynik:** gdy liczba staje się celem, powstają np. testy bez asercji pisane dla progu pokrycia.
- **Porównywanie nieporównywalnych systemów:** inny język, profil reguł czy narzędzie zmieniają wynik - ranking zespołów po surowej złożoności nie ma podstaw.
- **Zamiana korelacji w przyczynę:** złożone komponenty bywają też większe i częściej zmieniane - obniżenie metryki nie musi usunąć defektów.
- **Uśrednianie ryzyka:** średnia pakietu może ukryć pojedynczy krytyczny fragment.
- **Bramka bez analizy zmiany:** polityka jakości powinna chronić aktywnie zmieniany kod, a nie wymuszać jeden próg dla całego systemu.

---

## Praktyczny pulpit diagnostyczny

```text
Znaczenie:          krytyczne procesy i cechy jakości
Ekspozycja na zmianę: planowane funkcje, częstotliwość zmian, pliki zmieniane razem
Tarcie:             czas realizacji, czas testów, ręczne kroki, zależności zespołów
Ryzyko:             regresje, incydenty, wycofania, podatności, koncentracja wiedzy
Struktura:          złożoność, sprzężenie, duplikacje, rozmiar, cykle
Zabezpieczenia:     pokrycie zachowania, obserwowalność, wdrożenie etapowe, wycofanie
```

- Pakiet powinien być mały i powiązany z konkretną decyzją.
- Jego zadaniem jest wskazanie miejsca do dalszej analizy, a nie uniwersalna ocena jakości.

---

## Ćwiczenie: dobór metryk (4 min)

Cel: zmniejszyć liczbę regresji podczas comiesięcznych zmian reguł cenowych. Wybierz **trzy** wskaźniki i odrzuć **dwa** najmniej przydatne:

- globalne pokrycie linii całej aplikacji,
- liczba poprawek po wydaniu przypadająca na zmianę reguł,
- pokrycie gałęzi zmienianych reguł przez testy z istotnymi asercjami,
- liczba klas w repozytorium,
- współzmienność plików zawierających reguły cenowe,
- maksymalna złożoność dowolnej metody w systemie.

Dla każdego wyboru nazwij pytanie, na które odpowiada, i jedno ograniczenie interpretacji.

---

## Kod jest zapisem dawnych decyzji

- Struktura systemu powstała w konkretnym kontekście informacji, terminów, kompetencji i celów - ocena bez tego kontekstu prowadzi do łatwych, ale mało użytecznych wniosków.
- Rozsądna decyzja może utracić trafność, bo zmieniły się wymagania, wzrosła skala albo pojawiły się nowe wymagania bezpieczeństwa i zgodności.
- Komponent mógł też przejąć więcej funkcji, zmieniły się granice zespołów, platforma straciła wsparcie lub zespół lepiej zrozumiał domenę.
- Taki dług nie świadczy o zaniedbaniu - świadczy o potrzebie ponownej oceny decyzji w aktualnym kontekście.

---

## Mechanizmy organizacyjne

- **Presja krótkoterminowa:** termin może uzasadniać kompromis, ale gdy każdy termin jest „wyjątkowy”, a koszt rozwiązań tymczasowych nie jest rejestrowany, odsetki rosną natychmiast.
- **Niewłaściwe bodźce:** gdy sukces mierzy się tylko liczbą funkcji, testowalność i obserwowalność nie mają właściciela, a koszt przesuwa się w czasie.
- **Rozproszona odpowiedzialność:** komponent współdzielony bez właściciela degraduje się przez sumę lokalnych, minimalnych zmian.
- **Utrata wiedzy:** rotacja szkodzi, gdy wiedza nie jest utrwalana w testach, nazwach, kontraktach i automatyzacji.
- **Oddzielenie projektu od utrzymania:** zespół rozliczany z pierwszego wdrożenia nie odczuwa kosztów późniejszej eksploatacji.

---

## Mechanizmy techniczne i naturalna ewolucja

- Brak szybkich testów, ręczne lub niestabilne wdrażanie oraz kopiowanie rozwiązań zamiast zrozumienia wspólnej reguły.
- Rozbudowa warunków zamiast modelowania zmienności, globalne zależności, ukryte efekty uboczne i współdzielone modele danych bez kontraktów.
- Przedwczesne abstrakcje, narastające warstwy pośrednie i aktualizacje odkładane tak długo, że przestają być przyrostowe.
- Prawa ewolucji oprogramowania opisują tendencję do wzrostu złożoności, jeśli nie wykonuje się jawnej pracy nad jej ograniczaniem.
- Regularne upraszczanie i usuwanie nieużywanego kodu to część rozwoju produktu, a nie kosmetyka na koniec.

---

## Analiza przyczyny, nie winnego

„Programista skopiował kod” zatrzymuje analizę zbyt wcześnie. Zapytaj:

1. Dlaczego skopiowanie było najszybszą lub najbezpieczniejszą opcją?
2. Czy wspólna reguła była znana, miała właściciela i czy istniały testy pozwalające ją zmienić?
3. Czy granice repozytoriów i zespołów utrudniały współdzielenie?
4. Czy harmonogram przewidywał czas na usunięcie rozwiązania tymczasowego?
5. Jaka zmiana procesu zapobiegnie kolejnemu wystąpieniu?

Refaktoryzacja usuwa objaw w kodzie - bez zmiany mechanizmu organizacyjnego problem szybko wróci.

---

## Refaktoryzacja, przepisanie, modernizacja

- **Refaktoryzacja** zmienia wewnętrzną strukturę bez zmiany obserwowalnego zachowania, aby ułatwić dalszą pracę. Kroki strukturalne i funkcjonalne powinny pozostać rozróżnialne.
- **Przepisanie** tworzy nową implementację zastępującą całość lub część rozwiązania. Nie dziedziczy automatycznie zachowań, dojrzałości operacyjnej ani wiedzy starego systemu.
- Te zachowania trzeba jawnie odzyskać, zweryfikować, przenieść albo świadomie odrzucić.
- **Modernizacja** to pojęcie szersze: refaktoryzacja, aktualizacja platformy, opakowanie interfejsem, zmiana modelu danych, wymiana komponentu, zakup, migracja lub przepisanie.

---

## To nie jest wybór binarny (1/2)

| Strategia | Kiedy może być właściwa | Główne ryzyko |
| --- | --- | --- |
| Pozostawienie i monitorowanie | obszar stabilny, odizolowany, krótki horyzont | przeoczenie zmiany kontekstu |
| Stabilizacja | brak testów, obserwowalności, powtarzalnego budowania | nie zmniejsza jeszcze kosztów strukturalnych |
| Refaktoryzacja lokalna | zachowanie ma wartość, problem da się ograniczyć | regresja bez sieci bezpieczeństwa |
| Aktualizacja platformy | problemem jest platforma, nie model biznesowy | zgodność bibliotek, danych, operacji |
| Opakowanie interfejsem | wnętrze stabilne, trzeba ograniczyć jego wpływ | utrwalenie starego modelu w kontrakcie |

---

## To nie jest wybór binarny (2/2)

| Strategia | Kiedy może być właściwa | Główne ryzyko |
| --- | --- | --- |
| Wymiana komponentu | granica i odpowiedzialność są wyraźne | ukryte integracje i semantyka danych |
| Stopniowe zastępowanie | system duży, krytyczny, musi działać | tymczasowa architektura zostaje na stałe |
| Zakup produktu | proces nie jest wyróżnikiem | uzależnienie od dostawcy, dopasowanie |
| Pełne przepisanie | mały zakres, znane zachowanie, stara baza nie wystarczy | długo bez wartości, utrata zachowań |
| Wycofanie funkcji | funkcja nie daje już wartości | nieodkryci konsumenci |

Najlepsza strategia często łączy kilka pozycji w różnych częściach systemu.

---

## Zacznij od oczekiwanego wyniku

- „Chcemy mieć nowoczesny system” nie jest mierzalnym celem i nie pozwala ocenić żadnej strategii.
- Lepsze cele: skrócić wprowadzanie nowej reguły cenowej z tygodni do dni lub instalować poprawki bezpieczeństwa w określonym terminie.
- Inne przykłady: ograniczyć okno niedostępności przy wdrożeniu, wycofać kosztowną platformę, obsłużyć nowy rynek, zmniejszyć liczbę zespołów potrzebnych do jednej zmiany.
- Cel pozwala sprawdzić, czy strategia rozwiązuje problem - migracja frameworka może zostawić ten sam model zależności i koszt koordynacji.

---

## Kryteria decyzji (1/2)

| Kryterium | Ku zmianie przyrostowej | Ku wymianie / przepisaniu |
| --- | --- | --- |
| Wartość zachowania | cenne, lecz nie w pełni opisane | w większości zbędne |
| Zakres | duży, zintegrowany, krytyczny | mały, ograniczony, stabilny kontrakt |
| Platforma | istnieje ścieżka aktualizacji | brak realnej ścieżki migracji |
| Wiedza domenowa | głównie w kodzie i danych | opisana, eksperci dostępni |

---

## Kryteria decyzji (2/2)

| Kryterium | Ku zmianie przyrostowej | Ku wymianie / przepisaniu |
| --- | --- | --- |
| Bezpieczna migracja | przełączenie jednorazowe niedopuszczalne | możliwe działanie równoległe i wycofanie |
| Ekonomia | funkcje dostarczane w trakcie poprawy | koszt pozostania > pełny koszt wymiany |
| Zdolność organizacji | jeden zespół poprawia przyrostowo | stać nas na dwa systemy do wyłączenia starego |

Tabela wspiera rozmowę, ale nie działa jak algorytm.

---

## Pełny koszt przepisania

- Porównywanie oszacowania nowej implementacji tylko z kosztem zmiany starego kodu jest błędem.
- Trzeba doliczyć odzyskanie wymagań, utrzymanie starego systemu w trakcie prac i synchronizowanie zmian w obu rozwiązaniach.
- Dochodzi migracja i uzgadnianie danych, odtworzenie integracji i niejawnych kontraktów oraz testy funkcjonalne i niefunkcjonalne.
- Potrzebne są też gotowość operacyjna, szkolenia, uruchomienie równoległe, przełączenie i plan wycofania.
- Na końcu - rzeczywiste wyłączenie starego kodu, infrastruktury i procesów. Przepisanie powinno wygrać pełnym planem, nie estetyką kodu.

---

## Przewaga małych, odwracalnych kroków

1. Ustabilizuj budowanie, testy i obserwowalność obszaru.
2. Zidentyfikuj granicę funkcji i jej aktualny kontrakt.
3. Oddziel kod wywołujący od implementacji interfejsem lub adapterem.
4. Zbuduj nową implementację jednego przypadku i porównaj wyniki lub skieruj do niej część ruchu.
5. Rozszerzaj zakres po potwierdzeniu zachowania, a na końcu usuń starą ścieżkę i elementy przejściowe.

**Strangler Fig** stopniowo przejmuje funkcje na granicach systemu, zmniejszając ryzyko przełączenia. Okres współistnienia kosztuje - każdy element przejściowy potrzebuje właściciela i warunku usunięcia.

---

## Sygnały ostrzegawcze dla pełnego przepisania

- Głównym uzasadnieniem jest niechęć do kodu lub chęć użycia nowej technologii.
- Zakres to „pełna zgodność” bez wskazania wartości potrzebnych zachowań, a jednocześnie zmieniają się technologia, architektura, proces biznesowy i UI.
- Brak strategii migracji danych, porównania wyników i wycofania; stary system nadal szybko się zmienia, a synchronizacji nie zaplanowano.
- Pierwsza wartość pojawi się dopiero po wielomiesięcznym lub wieloletnim przełączeniu i nie ma finansowanego planu wyłączenia starego rozwiązania.
- Organizacyjne przyczyny obecnych problemów pozostają bez zmian - nowy system odziedziczy te same mechanizmy.

---

## Sygnały, że sama refaktoryzacja może nie wystarczyć

- Platforma nie może spełnić obowiązkowych wymagań bezpieczeństwa lub zgodności albo wymaganej niezawodności, wydajności i skali.
- Model domenowy reprezentuje proces, który ma zostać fundamentalnie zmieniony.
- Istotna zależność utraciła wsparcie i nie ma ścieżki aktualizacji w obecnej konstrukcji.
- Koszt kompatybilności blokuje większość wartości nowego rozwiązania albo refaktoryzacja nie tworzy bezpiecznej drogi do stanu docelowego.
- Nawet wtedy pełne przepisanie nie jest jedyną opcją - można wymienić krytyczny komponent lub zdolność biznesową, zachowując resztę.

---

## Proces podjęcia decyzji

1. Nazwij problem i mierzalny wynik biznesowy; ustal krytyczne zachowania, cechy jakości i ograniczenia.
2. Zbierz dane o zmianach, ryzyku, platformie, danych i integracjach.
3. Rozważ co najmniej trzy strategie, w tym pozostawienie lub ograniczenie ryzyka.
4. Oszacuj pełny koszt przejścia, działania równoległego i wyłączenia.
5. Wykonaj ograniczony czasowo eksperyment dla najważniejszej niewiadomej i wybierz najmniejszy krok dający wartość lub wiedzę.
6. Zapisz założenia, sygnały sukcesu, warunki przerwania i termin ponownej oceny.

Decyzja jest hipotezą - nowe informacje mogą uzasadnić zmianę kierunku.

---

## Ocena przykładu `LegacyOrderService`

- Fragment nie uzasadnia przepisania: pokazuje problemy z odpowiedzialnością i reprezentacją reguł, ale zależności są interfejsami w konstruktorze, więc klasa jest testowalna.
- Brakuje danych o skali systemu, historii regresji, platformie, integracjach i planie produktu.
- **Pierwsze kroki:** testy charakteryzujące przez publiczną metodę z implementacjami zastępczymi repozytorium i bramki pocztowej.
- Następnie uzgodnić przykłady oczekiwanego zachowania (ceny, podatki, wysyłka) i pod ochroną testów oddzielić obliczenia od zapisu i wysyłki.
- Dopiero po precyzyjnych testach reguł i danych o ich zmienności - decyzja o dalszej strukturze.

---

## Ćwiczenie: wybór najmniejszego kroku (4 min)

System ma wspieraną platformę jeszcze przez dwa lata, ale krytyczny moduł zmienia się co tydzień, nie ma szybkich testów i powoduje większość regresji.

- Porównaj trzy pierwsze kroki: pełne przepisanie systemu, testy charakteryzujące moduł oraz wymianę platformy.
- Wybierz jeden krok i zapisz założenie, które dzięki niemu zweryfikujesz.
- Określ miernik wyniku i warunek przerwania.
- Pamiętaj: dostępne informacje nie wystarczają do zatwierdzenia kompletnej strategii modernizacji.

---

## Warsztat - Ćwiczenie 1: obserwacja bez pochopnej naprawy (20 min)

**Materiał:** `LegacyOrderService`. **Planowana zmiana:** obsługa Czech, typ klienta `PARTNER` i odbiór osobisty jako trzeci wariant dostawy.

1. Przeczytaj kod bez proponowania docelowego projektu.
2. Zapisz co najmniej pięć faktów i trzy hipotezy o możliwych problemach.
3. Zapisz pytania domenowe, na które kod nie daje pewnej odpowiedzi.
4. Wskaż element planowanej zmiany, przy którym symptom stanie się istotny, oraz dowody potwierdzające lub obalające hipotezę.

Arkusz: Fragment | Fakt | Hipoteza lub ryzyko | Potrzebny dowód | Planowana zmiana. **Nie proponuj jeszcze wzorców ani refaktoryzacji.**

---

## Ćwiczenie 1 - wskazówki do omówienia

| Fragment | Fakt | Hipoteza |
| --- | --- | --- |
| `customerType` | `String`, sprawdzany tylko `VIP` | `PARTNER` rozbuduje warunki i ujawni reguły łączenia rabatów |
| `destinationCountry` | stawki tylko `PL` i `DE`, reszta = 0 | zero może ukrywać nieobsługiwany kraj |
| `shipping` | `boolean express`; próg liczony po rabatach | `boolean` nie wyrazi odbioru osobistego |
| pętla po pozycjach | rabaty mnożone, bez pośredniego zaokrąglania | wymaganie może zakładać sumowanie punktów % |
| koniec `placeOrder` | zapis przed wysłaniem maila | awaria bramki zostawi zapis mimo wyjątku |

„Podatek jest liczony błędnie” to hipoteza, nie fakt - bez reguły biznesowej nie da się tego stwierdzić z kodu.

---

## Ćwiczenie 2: klasyfikacja i priorytetyzacja długu (30 min)

| Kandydat | Kluczowe dane |
| --- | --- |
| `PricingService` | reguła VIP w 3 metodach, 2 pominięte kopie, 3 regresje, 5 zmian w planie; 3-5 osobodni |
| `AnnualReportFormatter` | złożoność 54; brak zmian 22 mies.; wycofanie za 4 mies.; 2-4 osobodni |
| `OldPaymentAdapter` | koniec wsparcia za 6 mies.; niezgodny interfejs nowej płatności; 4-7 osobodni |
| `CustomerExportController` | `TODO` o niezatwierdzonym eksporcie; brak incydentów i zmian |
| `TaxRoundingRule` | wymagane `HALF_UP`, użyte `DOWN`; błąd odtworzony na fakturze |

Sklasyfikuj (fakt, symptom, dług, defekt, ograniczenie, pewność), wypełnij szablon długu, rozdziel **6 osobodni**, wskaż brakujące dane, dobierz metryki i powtórz ocenę przy założeniu, że formatter żyje 2 lata ze zmianą co miesiąc.

---

## Ćwiczenie 2 - wskazówki do omówienia

- `PricingService` to najlepiej potwierdzony dług: rozproszona reguła, udokumentowane odsetki i duża ekspozycja. `TaxRoundingRule` to **defekt**, a nie dług.
- `AnnualReportFormatter` ma najgorszą metrykę, ale krótki horyzont życia - brak dowodu na przyszły koszt. `OldPaymentAdapter` to ograniczenie i potencjalny dług do rozpoznania.

| Nakład | Działanie |
| --- | --- |
| 1 osobodzień | poprawa `TaxRoundingRule` + test regresyjny |
| 4 osobodni | charakteryzacja i konsolidacja reguły VIP |
| 1 osobodzień | rozpoznanie `OldPaymentAdapter` |

- Świadomie pomijamy formatter i niezatwierdzony eksport. Przy 24 zmianach w 2 lata formatter wymaga ponownego porównania z pozostałymi.

---

## Ćwiczenie 3: wybór strategii modernizacji (50 min)

**`ClaimsCore`:** 24/7, ok. 210 tys. linii Javy, 18 integracji, wspólna baza z raportowaniem; platforma wspierana 30 miesięcy.

- Pełna regresja trwa 11 godzin; pokrycie linii 62%, gałęzi 28%, a moduł reguł uprawnień - 14% gałęzi i większość poprawek po wydaniu.
- Przepisanie oszacowano na 18 miesięcy bez migracji danych, integracji, równoległego utrzymania i wyłączenia; zmiany regulacyjne muszą trwać.
- Moduł reguł ma jednoznaczny kontrakt, a jego wynik można liczyć równolegle bez wpływu na produkcję.

> Cel: skrócić wdrożenie nowej reguły z 15 do 3 dni i zmniejszyć liczbę poprawek po wydaniu, bez obniżenia dostępności.

---

## Ćwiczenie 3 - zadanie

Grupy 3-4 osób: 15 min analizy, 25 min jednostronicowej decyzji, 10 min prezentacji. Rekomendacja zawiera:

- co najmniej trzy rozważone strategie oraz wybraną strategię i jej związek z celem,
- najważniejsze założenia i niewiadome oraz pierwszy krok możliwy do zakończenia w ciągu czterech tygodni,
- sposób weryfikacji zgodności wyników oraz strategię wdrożenia i wycofania wdrożenia,
- warunek przerwania lub zmiany kierunku i kryterium usunięcia architektury przejściowej,
- mierniki wyniku, a nie tylko mierniki aktywności - zapisane w szablonie rekordu decyzji.

---

## Ćwiczenie 3 - wskazówki do omówienia

- Materiał nie uzasadnia pełnego przepisania: oszacowanie jest niepełne, zachowanie częściowo niejawne, a system krytyczny i nadal ewoluuje.
- **Decyzja:** przyrostowo wydzielać moduł reguł za jawnym kontraktem - najpierw szybka weryfikacja i uruchamianie równoległe, potem przełączanie kategorii spraw.
- **4 tygodnie:** kontrakt i reprezentatywne sprawy → testy charakteryzujące i porównanie historyczne → nowa implementacja jednej kategorii → uruchomienie równoległe i raport rozbieżności.
- Tryb obserwacyjny, flaga konfiguracyjna i przećwiczone wycofanie; rozbieżności o skutku finansowym blokują przełączenie.
- Mierniki wyniku (czas od zatwierdzenia reguły do wdrożenia, odsetek poprawek, dostępność), a nie aktywności (liczba przepisanych klas).

---

## Sprawdzenie wiedzy

1. Dlaczego wiek kodu nie wystarcza do określenia go jako legacy?
2. Czym różni się symptom jakości od defektu?
3. Kiedy brak testów można opisać jako element długu technicznego?
4. Co oznaczają kapitał i odsetki w metaforze długu?
5. Dlaczego 100 procent pokrycia linii nie dowodzi poprawności testów?
6. Co mierzy złożoność cyklomatyczna, a czego nie mierzy?
7. Dlaczego identyczny fragment kodu nie zawsze powinien zostać uogólniony?
8. Co daje połączenie częstotliwości zmian ze złożonością?
9. Jakie koszty pełnego przepisania są często pomijane?
10. Dlaczego decyzja o modernizacji powinna zawierać warunek ponownej oceny?

---

## Lista kontrolna oceny systemu legacy

- **Wartość i zakres:** znamy zależnych użytkowników, funkcje krytyczne, horyzont życia i oczekiwany wynik modernizacji.
- **Zachowanie:** odtwarzamy ważne przypadki i rozróżniamy zachowanie zamierzone, historyczny wyjątek i defekt.
- **Struktura i zmiana:** znamy obszary często zmieniane, współzmienność i cykle; łączymy metryki z historią i incydentami.
- **Testy i operacje:** powtarzalne budowanie, znany czas testów, pokrycie zmienianego zachowania, obserwowalność i przećwiczone wycofanie.
- **Dane, dług, strategia:** znamy konsumentów i semantykę danych; dług ma lokalizację, konsekwencję i właściciela; rozważono >2 opcje, a architektura przejściowa ma plan usunięcia.

---

## Podsumowanie - najważniejsze wnioski

- Legacy to nie synonim starego ani brzydkiego kodu - decydują wartość systemu oraz tarcie i ryzyko zmiany. Diagnoza łączy fakt, kontekst, konsekwencję i dowód.
- *Code smells* są hipotezami, a nie listą zadań; dług techniczny dotyczy przyszłego kosztu zmian, a nie każdego defektu czy starej technologii.
- Złożoność, pokrycie i duplikacje to sygnały, które nabierają znaczenia dopiero w połączeniu z planem zmian, historią i krytycznością.
- Celem nie jest usunięcie całego długu, lecz świadoma kontrola kosztów, ryzyka i dostępnych opcji.
- Refaktoryzacja i przepisanie to tylko dwie z wielu strategii - bezpieczny kierunek zaczyna się od małego kroku, który zwiększa wiedzę lub dostarcza mierzalną wartość.
