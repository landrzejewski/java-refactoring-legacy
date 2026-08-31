# Moduł 1. Wprowadzenie do pracy z kodem legacy

## Zakres

1. Definicja i charakterystyka kodu legacy
2. Ryzyka i koszty utrzymania systemów legacy
3. Identyfikacja, pomiar i zarządzanie długiem technicznym
4. Rozpoznawanie *code smells*
5. Metryki jakości kodu
6. Przyczyny powstawania kodu niskiej jakości
7. Kryteria wyboru między refaktoryzacją a przepisaniem
8. Warsztat diagnostyczny

## 1. Czym jest kod legacy

### 1.1. Trzy perspektywy

Nie istnieje jedna definicja, która wystarcza w każdym kontekście. W praktyce warto połączyć trzy perspektywy.

#### Perspektywa historyczna

Kod został odziedziczony po wcześniejszych zespołach, decyzjach i ograniczeniach. Może wykorzystywać starsze biblioteki, konwencje lub rozwiązania architektoniczne. Sam wiek nie mówi jednak, czy kod jest bezpieczny i tani w utrzymaniu. Wieloletni komponent może być stabilny, dobrze przetestowany i rzadko zmieniany. Nowy komponent bez testów i z niejasnymi zależnościami może sprawiać znacznie większe problemy.

#### Perspektywa operacyjna

Kod legacy to kod, którego zmiana wiąże się z nieproporcjonalnie dużą niepewnością lub kosztem. Typowe sygnały to:

- brak szybkiej informacji zwrotnej po zmianie,
- trudność w uruchomieniu systemu lokalnie,
- długi lub niestabilny proces budowania,
- niejawne zależności od danych, kolejności operacji albo środowiska,
- rozległe skutki lokalnej zmiany,
- brak możliwości odtworzenia zachowania na podstawie testów i dokumentacji,
- wiedza o krytycznych regułach skupiona u pojedynczych osób.

Znana definicja operacyjna określa kod legacy jako kod bez testów. Jest celowo radykalna i użyteczna jako heurystyka: bez automatycznej sieci bezpieczeństwa trudniej stwierdzić, czy po zmianie system nadal zachowuje się zgodnie z dotychczasowymi oczekiwaniami. Nie jest to jednak definicja kompletna. Samo istnienie testów nie wystarcza, jeżeli są wolne, niestabilne, pozbawione istotnych asercji albo nie obejmują zachowań ważnych biznesowo.

#### Perspektywa biznesowa

System legacy zwykle nadal dostarcza wartość. Obsługuje procesy, dane i reguły, których organizacja nie może po prostu porzucić. Trudność polega na tym, że koszty i ryzyko kolejnych zmian zaczynają ograniczać zdolność organizacji do realizacji celów biznesowych.

Kod, który nie jest już używany i może zostać bezpiecznie usunięty, jest kodem martwym. Nie wymaga programu modernizacji. Kod legacy pozostaje istotny właśnie dlatego, że nadal ma użytkowników, zależności lub znaczenie operacyjne.

### 1.2. Legacy jest relacją, a nie etykietą

Ten sam komponent może być neutralny w jednym kontekście i problematyczny w innym. Stabilny moduł rozliczeniowy, którego zachowanie nie zmienia się od lat, może nie uzasadniać inwestycji. Jeżeli jednak organizacja planuje wejście na nowe rynki i comiesięczne zmiany reguł podatkowych, brak testowalności oraz sztywno zakodowane reguły stają się realną przeszkodą.

Ocena powinna więc odpowiadać na pytanie:

> Czy obecna konstrukcja systemu utrudnia zmiany, których rzeczywiście będziemy potrzebować?

Nie należy zaczynać od pytania, czy kod jest wystarczająco elegancki.

### 1.3. Charakterystyczne właściwości

Pojedyncza właściwość nie przesądza o diagnozie. Ryzyko zwykle wynika z ich kombinacji.

| Obszar | Przykładowe sygnały | Pytanie diagnostyczne |
| --- | --- | --- |
| Zachowanie | brak wiarygodnych testów, nieudokumentowane wyjątki, zależność od danych produkcyjnych | Skąd wiemy, co musi pozostać niezmienione? |
| Struktura | silne sprzężenie, niska spójność, cykle zależności, duże moduły | Jak daleko rozchodzi się typowa zmiana? |
| Technologia | niewspierane komponenty, trudny do odtworzenia proces budowania, brak aktualizacji | Czy system można bezpiecznie budować, uruchamiać i aktualizować? |
| Dane | niejawna semantyka kolumn, wspólna baza, korekty wykonywane ręcznie | Jakie reguły są zapisane w danych, skryptach i procedurach? |
| Operacje | ręczne wdrożenia, słaba obserwowalność, trudne wycofanie wdrożenia | Jak szybko wykryjemy problem i przywrócimy usługę? |
| Wiedza | nieaktualna dokumentacja, brak właściciela, koncentracja wiedzy | Ile osób potrafi bezpiecznie zmienić krytyczny fragment? |
| Przepływ pracy | długi czas od pomysłu do produkcji, duży zakres wydań | Gdzie rzeczywiście powstaje opóźnienie? |

### 1.4. Czego nie wolno zakładać

- Stary kod nie musi być kodem złej jakości.
- Monolit nie musi być systemem legacy, a mikrousługi nie gwarantują łatwości zmian.
- Brak najnowszej wersji frameworka nie jest sam w sobie długiem technicznym.
- Duża liczba linii nie dowodzi nadmiernej złożoności.
- Niskie pokrycie testami nie dowodzi istnienia defektów.
- Wysokie pokrycie testami nie dowodzi poprawności.
- Obecność symptomu nie przesądza, że refaktoryzacja przyniesie dodatni zwrot z inwestycji.

## 2. Od obserwacji do decyzji

W rozmowach o kodzie legacy często miesza się pojęcia, które wymagają odmiennych działań.

| Pojęcie | Znaczenie | Przykład | Typowa reakcja |
| --- | --- | --- | --- |
| Defekt | zachowanie niezgodne z wymaganiem | naliczono niewłaściwą stawkę dla obsługiwanego przypadku | naprawa i zabezpieczenie testem |
| *Code smell* | łatwo zauważalny sygnał możliwego problemu projektowego | metoda łączy obliczenia, zapis i komunikację zewnętrzną | analiza kontekstu i kosztu zmian |
| Dług techniczny | konstrukcja zwiększająca koszt lub ograniczająca możliwość przyszłych zmian | wspólny model danych wymusza modyfikacje kilku usług przy dodaniu pola | świadoma decyzja: spłacić, ograniczyć, tolerować lub monitorować |
| Ryzyko | niepewne zdarzenie wpływające na cel | aktualizacja biblioteki może zmienić serializację komunikatów | ocena prawdopodobieństwa, wpływu i sposobu reakcji |
| Ograniczenie | warunek, którego zespół obecnie nie kontroluje | kontrakt partnera wymaga starego formatu komunikatu | adapter, negocjacja kontraktu albo plan migracji |

Te kategorie mogą się łączyć. Silne sprzężenie jest symptomem. Jeżeli powoduje dodatkową pracę przy każdej zmianie, można opisać je jako element długu. Jeżeli doprowadziło do nieprawidłowego zachowania, wystąpił również defekt. Nadal są to trzy różne obserwacje.

### 2.1. Język diagnozy

Stwierdzenie „ten kod jest zły” nie pozwala podjąć decyzji. Przydatna diagnoza ma cztery części:

1. Fakt: co zaobserwowano i gdzie.
2. Kontekst: jaka zmiana, proces lub cecha jakości jest istotna.
3. Konsekwencja: jaki koszt, opóźnienie lub ryzyko powstaje.
4. Dowód: jakie dane potwierdzają konsekwencję.

Przykład:

> Zmiana reguły rabatowej wymaga modyfikacji trzech klas i dwóch skryptów. W ostatnich sześciu miesiącach dwa z pięciu wdrożeń tego obszaru wymagały poprawki, ponieważ jedna kopia reguły pozostała niezmieniona. Kolejne warianty rabatu uwzględniono w planie rozwoju produktu. To uzasadnia usunięcie rozproszonej reguły przed następną zmianą.

Opis wskazuje konkretny zakres, historię zmian i przyszły czynnik kosztu. Nie opiera się wyłącznie na preferencji projektowej.

### 2.2. Krótka aktywność: fakt czy ocena

**Czas:** 3 minuty.

Dla każdego stwierdzenia wskaż, czy opisuje fakt, hipotezę, defekt, symptom czy ograniczenie. Jedno stwierdzenie może należeć do kilku kategorii.

1. Metoda ma 142 linie i złożoność cyklomatyczną równą 24.
2. Metoda jest niemożliwa do utrzymania.
3. Wynik dla zatwierdzonego przykładu różni się od wymaganej kwoty.
4. Producent środowiska wykonawczego zakończy dostarczanie poprawek za sześć miesięcy.

Następnie przepisz drugie stwierdzenie jako diagnozę zawierającą fakt, kontekst, konsekwencję i potrzebny dowód.

## 3. Ryzyka i koszty utrzymania

### 3.1. Koszt zmiany

Koszt zmiany obejmuje więcej niż czas pisania kodu. Należy uwzględnić:

- odnalezienie miejsc wymagających modyfikacji,
- odzyskanie wiedzy o zachowaniu,
- przygotowanie środowiska i danych,
- implementację,
- testy i analizę możliwych regresji,
- koordynację między zespołami,
- wdrożenie, obserwację i ewentualne wycofanie,
- usunięcie skutków nieudanej zmiany.

Jeżeli samo napisanie kodu trwa godzinę, ale uzyskanie wystarczającej pewności przed wdrożeniem zajmuje pięć dni, problemem nie jest tempo pisania kodu, lecz koszt uzyskania informacji zwrotnej.

### 3.2. Kategorie ryzyka

#### Ryzyko funkcjonalne

Zmiana może naruszyć zachowania, których zespół nie zna. Dojrzałe systemy często zawierają wyjątki i reguły powstałe w odpowiedzi na rzeczywiste przypadki. Nie wszystkie są zapisane w wymaganiach. Kod, dane, konfiguracja, logi i zachowanie produkcyjne mogą być jedynymi dostępnymi śladami tej wiedzy.

#### Ryzyko operacyjne

Ręczne wdrożenia, niepowtarzalne środowiska, słaba obserwowalność i brak sprawdzonego sposobu wycofania wdrożenia zwiększają zarówno prawdopodobieństwo incydentu, jak i czas jego trwania. Czysta struktura klas nie kompensuje braku kontroli operacyjnej.

#### Ryzyko bezpieczeństwa i zgodności

Nieaktualizowane zależności, brak wspieranego środowiska wykonawczego albo niemożność zastosowania wymaganych mechanizmów ochrony mogą wymusić modernizację. Stary komponent nie jest automatycznie podatny, a nowy nie jest automatycznie bezpieczny. Liczy się konkretna ekspozycja, dostępne poprawki, konfiguracja oraz model zagrożeń.

#### Ryzyko wiedzy

Jeżeli tylko jedna osoba rozumie krytyczny proces, organizacja staje się od niej zależna. Dokumentacja może ograniczyć ryzyko, ale nie zastąpi zdolności do zbudowania, przetestowania i wdrożenia zmiany przez więcej niż jedną osobę.

#### Ryzyko ekonomiczne

Długi czas realizacji zmian generuje koszt opóźnienia. Obejmuje utracone przychody, opóźnioną zgodność z regulacją, dłuższe ręczne operacje i utracone możliwości eksperymentowania. Ten koszt bywa większy niż bezpośredni budżet utrzymania.

### 3.3. Koszt nie jest równomierny

Największą uwagę powinny otrzymać miejsca, które jednocześnie:

- często się zmieniają,
- mają istotne znaczenie biznesowe lub operacyjne,
- są trudne do zrozumienia i zweryfikowania,
- mają szeroki promień oddziaływania,
- nie mają wyraźnego właściciela.

Skomplikowany, ale stabilny parser starego formatu może wymagać jedynie monitorowania. Umiarkowanie skomplikowany moduł cenowy zmieniany co tydzień może być znacznie lepszym celem inwestycji.

### 3.4. Minimalny zestaw dowodów

Przed zaplanowaniem większej interwencji warto zebrać:

- historię zmian plików i komponentów,
- czas realizacji reprezentatywnych zmian,
- incydenty, regresje i wycofane wdrożenia związane z danym obszarem,
- zależności uruchomieniowe i integracyjne,
- mapę odpowiedzialności zespołów,
- wyniki testów oraz czas ich wykonania,
- dane z analizy statycznej wraz z konfiguracją narzędzia,
- najbliższy plan zmian produktu i platformy.

Nie każda organizacja ma komplet danych. Braki należy nazwać wprost i uzupełniać stopniowo, zamiast zastępować je pozornie dokładnym wynikiem jednego skanera.

## 4. Dług techniczny

### 4.1. Użyteczna metafora

Nie istnieje jedna powszechnie przyjęta granica tego pojęcia. W tym materiale dług techniczny oznacza celowo pozostawiony albo ujawniony z czasem kompromis dotyczący jakości technicznej systemu lub sposobu jego wytwarzania. Taki kompromis może przynieść krótkoterminową korzyść albo wynikać z niepełnej wiedzy, lecz tworzy warunkowe zobowiązanie: jeżeli zespół będzie rozwijał lub utrzymywał dany obszar, może ponosić dodatkowy koszt albo utracić możliwość wykonania potrzebnej zmiany.

Każda architektura upraszcza pewne zmiany kosztem innych. Taki konieczny kompromis nie staje się automatycznie długiem tylko dlatego, że jedna z możliwych zmian jest kosztowna. Diagnoza długu wymaga wskazania konkretnej słabości technicznej lub odłożonej pracy, jej konsekwencji, przyszłej ekspozycji oraz możliwego sposobu ograniczenia albo usunięcia.

Metafora finansowa pomaga rozmawiać o kompromisach:

- kapitał długu to przybliżony koszt usunięcia problematycznej konstrukcji i bezpiecznego zweryfikowania zmiany,
- odsetki to dodatkowy koszt ponoszony wtedy, gdy kolejne prace napotykają tę konstrukcję,
- prawdopodobieństwo naliczania odsetek zależy od tego, czy dany obszar będzie zmieniany,
- korzyść z zaciągnięcia długu może polegać na wcześniejszym dostarczeniu wartości albo zdobyciu wiedzy.

To analogia, a nie model księgowy. Kapitału i odsetek nie da się zwykle obliczyć z taką samą precyzją jak rat kredytu. Odsetki mogą być warunkowe, trudne do oddzielenia od innych kosztów i różne dla każdej planowanej zmiany.

### 4.2. Nie każdy problem jest długiem

Przydatna granica operacyjna brzmi następująco: kandydat na dług dotyczy elementu systemu lub procesu wytwarzania, który tworzy możliwy do uniknięcia dodatkowy koszt, ryzyko albo ograniczenie przyszłej ewolucji. Sam koszt zmiany nie wystarcza. Trzeba jeszcze wykazać techniczny mechanizm problemu i ekspozycję na przyszłe prace.

| Sytuacja | Czy to dług techniczny? | Uzasadnienie |
| --- | --- | --- |
| Funkcja nie została jeszcze zaplanowana | Zwykle nie | Jest to brak zakresu produktu, nie skutek konstrukcji technicznej. |
| System zwraca błędny wynik dla aktualnego wymagania | Nie, sam defekt nie jest długiem | Defekt może być skutkiem długu, ale wymaga osobnego zarządzania. |
| Dwie usługi współdzielą schemat i każda zmiana wymaga wspólnego wdrożenia | Kandydat na dług | Trzeba potwierdzić, że wspólne wdrożenie tworzy dodatkowy koszt lub ograniczenie, którego można uniknąć w wymaganym kierunku rozwoju. |
| Biblioteka ma starszy numer wersji, ale jest wspierana i spełnia potrzeby | Nie na podstawie samego wieku | Brak wykazanej konsekwencji. |
| Brak automatycznych testów wymusza trzy dni ręcznej regresji przy każdym wydaniu | Tak | Brak zabezpieczenia powoduje powtarzalny dodatkowy koszt. |
| Kod nie odpowiada osobistym preferencjom autora przeglądu | Nie | Preferencja bez konsekwencji nie tworzy elementu długu. |

Nie należy używać określenia „dług techniczny” jako zbiorczej nazwy każdego zadania porządkowego. Zbyt szeroka etykieta utrudnia priorytetyzację i osłabia znaczenie metafory.

### 4.3. Dwie niezależne osie klasyfikacji

Pierwsza oś opisuje świadomość kompromisu:

- dług świadomy: zespół rozumie kompromis i podejmuje go celowo,
- dług nieświadomy: problem staje się widoczny dopiero po zdobyciu wiedzy albo zmianie kontekstu.

Druga oś opisuje sposób podjęcia decyzji i zarządzania skutkami:

- dług rozważny: korzyść jest istotna, ryzyko ograniczone, a decyzja monitorowana,
- dług lekkomyślny: konsekwencje są ignorowane albo zespół nie zapewnia sposobu kontrolowania ryzyka.

Osie można łączyć. Dług może być jednocześnie świadomy i rozważny, świadomy i lekkomyślny, nieświadomy i rozważnie zarządzany po odkryciu albo nieświadomy i nadal ignorowany.

Świadomy dług nie jest automatycznie dobry. Powinien mieć uzasadnienie, właściciela, warunki ponownej oceny i strategię ograniczenia skutków. Dług nieświadomy nie zawsze oznacza błąd zespołu. Projektowanie jest procesem uczenia się, a wcześniej poprawna decyzja może utracić trafność po zmianie wymagań, skali lub otoczenia technologicznego.

### 4.4. Typowe obszary długu

- kod i projekt klas,
- architektura oraz granice komponentów,
- automatyzacja testów,
- dane, ich jakość i modelowanie,
- proces budowania, wdrożenie oraz infrastruktura,
- zależności i platforma wykonawcza,
- obserwowalność i obsługa operacyjna,
- dokumentacja decyzji i wiedza potrzebna do zmiany.

Lista nie służy do tworzenia osobnego rejestru zadań dla każdej niedoskonałości. Pomaga jedynie uniknąć redukowania długu technicznego do wyników analizy statycznej kodu.

### 4.5. Opis elementu długu

Element długu powinien być konkretny i możliwy do powiązania z artefaktem, decyzją albo granicą systemu.

Minimalny opis zawiera:

| Pole | Pytanie |
| --- | --- |
| Lokalizacja | Jakich komponentów, danych lub procesów dotyczy problem? |
| Konstrukcja | Jaki element projektu powoduje przyszły koszt? |
| Przyczyna | Dlaczego powstał lub dlaczego utracił trafność? |
| Konsekwencja | Jak wpływa na koszt, czas, jakość lub możliwość zmiany? |
| Dowód | Jakie zdarzenia lub dane potwierdzają konsekwencję? |
| Zaobserwowane odsetki | Jaki dodatkowy koszt został już poniesiony podczas zmian? |
| Ekspozycja | Jakie przyszłe zmiany i jak często mogą powodować kolejne odsetki? |
| Kapitał bieżący | Jaki jest przybliżony zakres pracy potrzebnej do usunięcia lub ograniczenia? |
| Ryzyko spłaty | Co może pójść nieprawidłowo podczas interwencji? |
| Decyzja | Spłacamy, ograniczamy, tolerujemy czy obserwujemy? |
| Poziom pewności | Jak silne są dowody i założenia użyte w ocenie? |
| Właściciel i termin przeglądu | Kto oraz kiedy ponownie oceni decyzję? |

Przykład:

| Pole | Wartość |
| --- | --- |
| Identyfikator | TD-017 |
| Lokalizacja | moduł kalkulacji ceny i dwa procesy importu |
| Konstrukcja | trzy niezależne kopie reguły rabatu lojalnościowego |
| Przyczyna | rozwiązanie tymczasowe dodane przed kampanią bez wspólnego właściciela reguły |
| Konsekwencja | każda zmiana reguły wymaga zsynchronizowania trzech wdrożeń |
| Dowód | dwie poprawki po pięciu ostatnich zmianach rabatów |
| Zaobserwowane odsetki | około trzech dodatkowych osobodni na poprawki i ponowne wdrożenia |
| Ekspozycja | nowy program lojalnościowy i pięć zmian rabatów planowanych w następnym kwartale |
| Kapitał bieżący | od 4 do 7 osobodni wraz z testami i migracją konfiguracji |
| Ryzyko spłaty | nieznane różnice semantyczne między procesami importu i ścieżką zamówienia |
| Decyzja | usunąć duplikację przed implementacją programu |
| Poziom pewności | średni, ponieważ potwierdzono historię zmian, ale nie porównano wszystkich wariantów reguły |
| Właściciel i termin przeglądu | zespół Checkout, przegląd przed planowaniem następnego kwartału |

Oszacowanie jest przedziałem, nie obietnicą. Powinno obejmować testy, wdrożenie, migrację i bezpieczne usunięcie starej ścieżki, a nie wyłącznie zmianę kodu.

### 4.6. Identyfikacja długu

Źródła informacji wzajemnie się uzupełniają:

1. Warsztaty z programistami, testerami, operatorami i ekspertami domenowymi.
2. Analiza zdarzeń: incydentów, regresji, nieudanych wdrożeń i długich zmian.
3. Historia repozytorium: częstotliwość zmian, pliki zmieniane razem, koncentracja autorstwa.
4. Analiza statyczna: zależności, cykle, złożoność, duplikacje i naruszenia reguł.
5. Analiza platformy: wsparcie, podatności, możliwość aktualizacji i odtworzenia środowiska.
6. Plan produktu: miejsca, w których przyszłe zmiany napotkają istniejące ograniczenia.

Skaner może wykryć symptom, ale nie zna wartości biznesowej ani planu zmian. Rozmowa zespołu może ujawnić koszt, ale bywa podatna na pamięć selektywną. Połączenie danych ilościowych i jakościowych daje mocniejszą podstawę decyzji.

### 4.7. Pomiar bez fałszywej precyzji

Nie istnieje jedna wiarygodna liczba opisująca całkowity dług systemu. W szczególności automatycznie wyliczony „czas naprawy” zależy od reguł i założeń narzędzia. Nie obejmuje pełnego kosztu odzyskania wiedzy, regresji, migracji, koordynacji i ryzyka operacyjnego.

Zamiast jednej sumy należy mierzyć pytania istotne dla decyzji, na przykład:

- O ile dłużej trwa zmiana po napotkaniu danego elementu długu?
- Jak często zespół dotyka tego obszaru?
- Jak często zmiany powodują regresję albo wymagają wycofania wdrożenia?
- Jak wiele komponentów i zespołów trzeba koordynować?
- Jak długo trwa wiarygodna weryfikacja?
- Czy platforma ma ograniczony horyzont wsparcia?
- Jaki cel biznesowy pozostaje zablokowany?

Można stosować skalę porządkową, jeżeli jej znaczenie jest jawne. Przykład:

| Ocena wpływu | Interpretacja |
| --- | --- |
| 1 | lokalne utrudnienie bez istotnego wpływu na termin lub ryzyko |
| 2 | powtarzalna dodatkowa praca w obrębie jednego zespołu |
| 3 | zauważalne opóźnienie, ryzyko regresji albo koordynacja kilku komponentów |
| 4 | istotne ograniczenie planu rozwoju produktu lub niezawodności usługi |
| 5 | zagrożenie ciągłości, bezpieczeństwa, zgodności albo kluczowego celu biznesowego |

Wyniku nie należy interpretować jak pomiaru fizycznego. Skala służy uporządkowaniu rozmowy, a ocena powinna zawierać krótkie uzasadnienie.

### 4.8. Priorytetyzacja

Najwyższy priorytet mają zwykle elementy, w przypadku których współwystępują:

- wysoka konsekwencja,
- duże prawdopodobieństwo napotkania w planowanych zmianach,
- rosnące lub częste odsetki,
- akceptowalny koszt interwencji,
- możliwość bezpiecznego ograniczenia ryzyka.

Prosty model rozmowy:

```text
Priorytet zależy od:
    wpływu konsekwencji
  + prawdopodobieństwa napotkania
  + pilności
  + kosztu opóźnienia
  - kosztu i ryzyka interwencji
```

Nie jest to wzór arytmetyczny. Dodawanie subiektywnych punktów może być pomocne przy porównaniu podobnych elementów, ale nie powinno automatycznie podejmować decyzji.

### 4.9. Strategie zarządzania

Z elementem długu można postąpić na kilka sposobów:

- spłacić: usunąć konstrukcję powodującą koszt,
- ograniczyć: dodać testy, interfejs, monitorowanie lub izolację zmniejszającą konsekwencje,
- unikać: poprowadzić zmianę inną ścieżką, jeżeli nie zwiększa to problemu w innym miejscu,
- tolerować: świadomie pozostawić element, gdy odsetki są małe lub obszar będzie wycofany,
- przenieść: zastąpić komponent produktem lub usługą, pamiętając, że powstają nowe zależności,
- obserwować: ustalić sygnał i termin, które uruchomią ponowną ocenę.

„Spłacenie całego długu” nie jest rozsądnym celem. System podlega zmianom, a część długu nigdy nie naliczy istotnych odsetek. Celem jest kontrolowanie ryzyka i kosztu ewolucji.

### 4.10. Krótka aktywność: brakujące pola rekordu

**Czas:** 3 minuty.

Zespół zapisał jedynie: „Brak testów w imporcie, naprawa zajmie trzy dni”. Wskaż co najmniej cztery brakujące pola z szablonu długu. Dla każdego określ, czy potrzebujesz danych historycznych, wiedzy domenowej, eksperymentu technicznego czy decyzji właściciela produktu. Nie uzupełniaj nieznanych wartości założeniami przedstawionymi jako fakty.

## 5. *Code smells* jako hipotezy diagnostyczne

### 5.1. Definicja

*Code smell* to łatwo zauważalny sygnał, który może wskazywać głębszy problem projektowy. Nie jest automatycznym dowodem defektu ani nakazem refaktoryzacji.

Przykładowo długa metoda może łączyć kilka odpowiedzialności i utrudniać testowanie. Może też przedstawiać liniowy algorytm, którego podział wymuszałby niepotrzebne przechodzenie między poziomami abstrakcji. Ocena zależy od kontekstu planowanych zmian.

### 5.2. Przydatne grupy symptomów

#### Nadmierny rozmiar i obciążenie poznawcze

- długa metoda,
- duża klasa,
- długa lista parametrów,
- złożone, wielopoziomowe warunki,
- zbyt wiele lokalnych zmiennych i stanów pośrednich.

#### Problemy z odpowiedzialnością

- klasa zmienia się z wielu niezależnych powodów,
- metoda intensywnie korzysta z danych innego obiektu,
- logika domenowa znajduje się w kontrolerach, skryptach albo warstwie dostępu do danych,
- jeden moduł zna szczegóły wielu niepowiązanych procesów.

#### Problemy ze zmianą

- jedna zmiana wymaga modyfikacji wielu odległych miejsc,
- te same pliki regularnie zmieniają się razem,
- wariant zachowania jest rozproszony po instrukcjach `if` i `switch`,
- dodanie pola przechodzi przez wiele warstw bez lokalnej odpowiedzialności.

#### Problemy z reprezentacją

- wartości domenowe są reprezentowane przez dowolne napisy lub liczby,
- argument `boolean` wybiera odrębne zachowania,
- kilka parametrów stale występuje razem,
- znaczące liczby i napisy nie mają nazw opisujących regułę.

#### Zbędność i pośrednictwo

- duplikacja wiedzy lub reguły,
- martwy kod,
- klasa jedynie przekazuje każde wywołanie dalej,
- abstrakcja istnieje bez rzeczywistej zmienności lub potrzeby.

### 5.3. Przykład w Javie

Poniższy kod jest materiałem diagnostycznym. Nie stanowi wzorcowej implementacji.

```java
package pl.training.module1;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

public final class LegacyOrderService {
    private final OrderRepository repository;
    private final MailGateway mailGateway;

    public LegacyOrderService(
            OrderRepository repository,
            MailGateway mailGateway) {
        this.repository = repository;
        this.mailGateway = mailGateway;
    }

    public Receipt placeOrder(
            Order order,
            String customerType,
            boolean express,
            String destinationCountry) {

        if (order == null || order.lines() == null || order.lines().isEmpty()) {
            throw new IllegalArgumentException("Order must contain lines");
        }

        BigDecimal subtotal = BigDecimal.ZERO;

        for (OrderLine line : order.lines()) {
            BigDecimal lineValue = line.unitPrice()
                    .multiply(BigDecimal.valueOf(line.quantity()));

            if ("VIP".equals(customerType)) {
                lineValue = lineValue.multiply(new BigDecimal("0.90"));
            }

            if (line.quantity() >= 10) {
                lineValue = lineValue.multiply(new BigDecimal("0.95"));
            }

            subtotal = subtotal.add(lineValue);
        }

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

        BigDecimal total = subtotal
                .add(shipping)
                .add(tax)
                .setScale(2, RoundingMode.HALF_UP);

        repository.save(order.id(), total);
        mailGateway.send(order.customerEmail(), "Order total: " + total);

        return new Receipt(order.id(), total);
    }
}

record Order(
        UUID id,
        String customerEmail,
        List<OrderLine> lines) {
}

record OrderLine(
        String sku,
        int quantity,
        BigDecimal unitPrice) {
}

record Receipt(UUID orderId, BigDecimal total) {
}

interface OrderRepository {
    void save(UUID orderId, BigDecimal total);
}

interface MailGateway {
    void send(String recipient, String body);
}
```

Klasa jest podstawą ćwiczenia 1. Przed przejściem do wskazówek w sekcji 10 należy samodzielnie oddzielić fakty widoczne w kodzie od hipotez wymagających wiedzy domenowej lub danych historycznych.

### 5.4. Procedura analizy symptomu

Dla każdego zauważonego symptomu należy zapytać:

1. Jaką konkretną zmianę utrudnia?
2. Jakie zachowanie może zostać naruszone?
3. Czy historia zmian potwierdza problem?
4. Czy problem jest lokalny, czy rozchodzi się po systemie?
5. Czy koszt pozostawienia przewyższa koszt i ryzyko interwencji?
6. Jaki najmniejszy krok dostarczy nowej informacji albo ograniczy ryzyko?

Taka procedura chroni przed mechanicznym usuwaniem symptomów tylko po to, aby poprawić wynik narzędzia.

## 6. Metryki jakości kodu

### 6.1. Metryka nie jest celem

Jakość oprogramowania ma wiele wymiarów, między innymi funkcjonalność, niezawodność, bezpieczeństwo, wydajność i utrzymywalność. Pojedyncza metryka strukturalna nie mierzy ich wszystkich.

Metryka jest użyteczna, gdy odpowiada na określone pytanie. Warto stosować schemat Cel, Pytanie, Metryka:

1. Cel: zmniejszyć ryzyko zmian w obszarze wyceny.
2. Pytanie: które fragmenty są często zmieniane i trudne do zweryfikowania?
3. Metryki: częstotliwość zmian, złożoność, czas testów, pokrycie zmienianego kodu, liczba regresji.
4. Interpretacja: wybrać kandydatów do analizy, a następnie potwierdzić mechanizm problemu.

Zbieranie danych bez pytania prowadzi do raportów, które wyglądają precyzyjnie, lecz nie wspierają decyzji.

### 6.2. Złożoność cyklomatyczna

Złożoność cyklomatyczna opisuje strukturę przepływu sterowania. Złożoność cyklomatyczną grafu przepływu można obliczyć ze wzoru:

```text
V(G) = E - N + 2P
```

gdzie:

- `E` oznacza liczbę krawędzi grafu,
- `N` oznacza liczbę węzłów,
- `P` oznacza liczbę spójnych składowych. Dla pojedynczej metody zwykle `P = 1`.

Dla strukturalnego przepływu równoważny zapis ma postać:

```text
V(G) = 1 + suma(k_i - 1)
```

`k_i` oznacza liczbę wyjść z danego punktu decyzyjnego. Jeżeli wszystkie decyzje są binarne, wzór upraszcza się do liczby punktów decyzyjnych powiększonej o jeden. Decyzja wielowariantowa, na przykład `switch`, może zwiększyć wynik o więcej niż jeden, zależnie od struktury przepływu i reguł narzędzia.

Wartość odpowiada liczbie liniowo niezależnych ścieżek w grafie. Może wskazywać, ile różnych decyzji trzeba rozważyć podczas testowania. Nie jest liczbą wszystkich możliwych ścieżek wykonania i nie mierzy bezpośrednio trudności domeny ani zdolności człowieka do rozumienia kodu.

Metryka nie uwzględnia stopnia zagnieżdżenia decyzji. Dwie metody o tym samym wyniku mogą znacząco różnić się czytelnością, jeżeli jedna używa głęboko zagnieżdżonych warunków, a druga płaskich warunków ochronnych.

Przykład:

```java
package pl.training.module1;

import java.math.BigDecimal;
import java.util.List;

public final class RiskClassifier {
    private RiskClassifier() {
    }

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
}

record OrderSummary(
        BigDecimal total,
        boolean international,
        List<Item> items) {
}

record Item(boolean fragile) {
}
```

Przy prostym liczeniu metoda ma cztery punkty decyzyjne: dwie instrukcje `if`, pętlę `for` oraz `if` wewnątrz pętli. Złożoność cyklomatyczna wynosi więc 5.

Wyniki narzędzi mogą się różnić. Przyczyną są między innymi odmienne reguły dotyczące operatorów logicznych, wariantów `switch`, obsługi wyjątków i kodu generowanego oraz sposób analizy kodu bajtowego. JaCoCo analizuje kod bajtowy i stosuje równoważną postać `V(G) = B - D + 1`, gdzie `B` oznacza liczbę gałęzi, a `D` liczbę punktów decyzyjnych. Nie traktuje obsługi wyjątków jako gałęzi. Porównywać należy wyniki uzyskane tym samym narzędziem, w tej samej wersji i konfiguracji.

#### Jak interpretować

Wysoka wartość jest sygnałem do pytań:

- Czy metoda realizuje kilka niezależnych reguł?
- Czy wszystkie istotne warianty można wiarygodnie przetestować?
- Czy złożoność pochodzi z problemu domenowego, czy ze struktury implementacji?
- Czy kolejne planowane zmiany dodadzą następne rozgałęzienia?
- Czy decyzje można nazwać i rozdzielić bez zaciemniania przebiegu algorytmu?

Nie istnieje uniwersalny próg, po którego przekroczeniu metoda jest automatycznie błędna. Próg skonfigurowany w narzędziu może być regułą zespołu wyzwalającą przegląd, ale nie zastępuje diagnozy.

### 6.3. Złożoność poznawcza

Złożoność poznawcza (cognitive complexity) powstała jako odpowiedź na opisaną wyżej słabość złożoności cyklomatycznej: pomija ona stopień zagnieżdżenia decyzji. Metryka próbuje przybliżyć wysiłek potrzebny człowiekowi do zrozumienia przepływu sterowania, a nie liczbę ścieżek do przetestowania. Nie jest częścią żadnej normy; jej najbardziej rozpowszechniona definicja pochodzi z narzędzi analizy statycznej, między innymi z rodziny SonarQube, i to od konkretnego narzędzia zależą szczegóły liczenia.

Typowe reguły naliczania:

- każde przerwanie liniowego przepływu, na przykład `if`, pętla, `catch` lub wyrażenie warunkowe, zwiększa wynik o jeden,
- konstrukcja zagnieżdżona w innej konstrukcji sterującej otrzymuje dodatkowy narzut równy głębokości zagnieżdżenia,
- `switch` liczony jest jako jedno przerwanie przepływu niezależnie od liczby wariantów, inaczej niż w złożoności cyklomatycznej,
- sekwencja jednorodnych operatorów logicznych liczona jest raz, a dopiero zmiana operatora, na przykład z `&&` na `||`, dodaje kolejny punkt,
- wczesne wyjście `return` w klauzuli ochronnej nie zwiększa wyniku, dzięki czemu spłaszczenie warunków realnie obniża metrykę,
- rekurencja zwiększa wynik, ponieważ wymaga śledzenia dodatkowego cyklu rozumowania.

W efekcie dwie metody o identycznej złożoności cyklomatycznej mogą mieć bardzo różną złożoność poznawczą. Metoda z czterema płaskimi warunkami ochronnymi uzyska niski wynik, a metoda z czterema warunkami zagnieżdżonymi jeden w drugim wysoki. To odróżnienie odpowiada intuicji czytelnika kodu i czyni metrykę użytecznym sygnałem do refaktoryzacji struktury metody, na przykład zastąpienia zagnieżdżonych warunków klauzulami ochronnymi lub wydzielenia metod.

#### Jak interpretować

Złożoność poznawcza uzupełnia złożoność cyklomatyczną, ale jej nie zastępuje. Do planowania testów nadal potrzebna jest informacja o liczbie niezależnych decyzji. Wynik zależy od wersji i konfiguracji narzędzia, więc porównania mają sens tylko w obrębie tego samego pomiaru. Metryka nadal nie mierzy trudności domeny: metoda o niskim wyniku może implementować regułę biznesową trudną do zrozumienia z powodów pojęciowych, nie strukturalnych.

### 6.4. Pokrycie testami

Pokrycie informuje, które elementy programu zostały wykonane podczas danego uruchomienia testów. Typowe odmiany to:

- pokrycie instrukcji kodu bajtowego,
- pokrycie linii źródłowych,
- pokrycie gałęzi decyzji,
- pokrycie metod i klas.

W JaCoCo linia jest oznaczona jako wykonana, gdy wykonano co najmniej jedną przypisaną do niej instrukcję kodu bajtowego. Powiązanie instrukcji z liniami źródłowymi wymaga informacji o numerach linii w plikach klas. Bez tabeli `LineNumberTable` narzędzie nadal mierzy instrukcje i gałęzie, ale nie może poprawnie przedstawić pokrycia linii źródłowych. Pokrycie gałęzi obejmuje konstrukcje `if` i `switch`, ale nie ścieżki obsługi wyjątków. Kod syntetyczny generowany przez kompilator może powodować wyniki, które nie odpowiadają intuicyjnemu odczytaniu źródła.

Podstawowa proporcja ma postać:

```text
pokrycie = elementy wykonane / wszystkie mierzone elementy
```

Znaczenie „elementu” zależy od rodzaju licznika i narzędzia.

#### Pokrycie linii a pokrycie gałęzi

```java
package pl.training.module1;

public final class DiscountPolicy {
    private DiscountPolicy() {
    }

    public static int discountPercent(int orderValue, boolean vip) {
        int discount = 0;

        if (orderValue >= 100) {
            discount += 10;
        }

        if (vip) {
            discount += 5;
        }

        return discount;
    }
}
```

```java
package pl.training.module1;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class DiscountPolicyTest {
    @Test
    void combinesThresholdAndVipDiscount() {
        assertEquals(15, DiscountPolicy.discountPercent(100, true));
    }
}
```

Ten test wykonuje każdą linię metody. Nie wykonuje jednak fałszywej gałęzi żadnej z dwóch decyzji. Przy typowym liczeniu obejmuje dwie z czterech gałęzi, czyli 50 procent pokrycia gałęzi.

Nawet 100 procent pokrycia gałęzi nie dowodzi poprawności. Testy mogą:

- nie zawierać istotnych asercji,
- używać niereprezentatywnych danych,
- pomijać interakcje między warunkami,
- nie sprawdzać błędów współbieżności, czasu i integracji,
- utrwalać niezamierzone zachowanie,
- być tak silnie związane z implementacją, że utrudniają zmianę.

Pokrycie dobrze wskazuje kod, którego dany zestaw testów nie wykonał. Znacznie słabiej odpowiada na pytanie, czy testy wykrywają ważne defekty.

#### Zastosowanie w kodzie legacy

Najbardziej użyteczne pytania to:

- Czy testy wykonują zachowanie, które zamierzamy zmienić?
- Czy obejmują obie strony zmienianych decyzji?
- Czy chronią ważne przypadki biznesowe i historyczne regresje?
- Czy wynik pochodzi z testów, które można szybko i powtarzalnie uruchomić?
- Czy niepokryty kod jest osiągalny i istotny?

Globalny procent może ukryć krytyczny moduł z niemal zerowym pokryciem za dużą liczbą prostych, dobrze pokrytych klas. Dlatego analizuje się zakres zmiany i ryzyko, nie tylko średnią dla repozytorium.

### 6.5. Duplikacje

Duplikacja kodu oznacza podobne fragmenty implementacji. Narzędzia mogą wykrywać różne rodzaje podobieństwa:

- kopie identyczne poza formatowaniem i komentarzami,
- kopie ze zmienionymi nazwami lub literałami,
- kopie z dodanymi albo usuniętymi instrukcjami,
- podobne zachowanie zapisane odmienną strukturą, które jest najtrudniejsze do wykrycia automatycznie.

Duplikacja staje się kosztowna, gdy reprezentuje tę samą wiedzę, która musi zmieniać się razem. Dwie podobne sekwencje mogą natomiast należeć do różnych reguł, które przypadkowo wyglądają dziś tak samo. Ich przedwczesne połączenie może stworzyć błędną zależność.

Przykład duplikacji kodu wymagającej analizy:

```java
package pl.training.module1;

import java.math.BigDecimal;

public final class SalesCalculations {
    private SalesCalculations() {
    }

    public static BigDecimal invoiceLineTotal(
            BigDecimal unitPrice,
            int quantity,
            boolean vip) {
        BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(quantity));

        if (vip) {
            total = total.multiply(new BigDecimal("0.90"));
        }

        return total;
    }

    public static BigDecimal quoteLineTotal(
            BigDecimal unitPrice,
            int quantity,
            boolean vip) {
        BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(quantity));

        if (vip) {
            total = total.multiply(new BigDecimal("0.90"));
        }

        return total;
    }
}
```

Przed połączeniem metod trzeba ustalić, czy faktura i oferta rzeczywiście korzystają z jednej reguły rabatowej. Jeżeli tak, rozproszenie tej reguły grozi niespójną zmianą. Jeżeli polityki mają rozwijać się niezależnie, podobieństwo może być przejściowe.

#### Ograniczenia pomiaru

Wynik zależy od:

- minimalnej długości fragmentu,
- sposobu tokenizacji lub analizy składni,
- wykluczeń kodu generowanego,
- języka i konfiguracji narzędzia,
- definicji mianownika użytego do obliczenia procentu.

Procentów z różnych narzędzi nie należy bezpośrednio porównywać. Ważniejsze od całkowitego udziału duplikacji jest pytanie, czy kopie często zmieniają się razem i czy niespójne aktualizacje powodują defekty.

### 6.6. Sprzężenie i spójność

Sprzężenie opisuje, jak silnie moduł zależy od innych modułów, a spójność, jak mocno elementy wewnątrz modułu należą do jednej odpowiedzialności. Oba pojęcia wracają w module trzecim jako zasady projektowe; tutaj interesuje nas ich pomiar jako sygnał diagnostyczny w systemie legacy.

Podstawowe mierzalne wskaźniki sprzężenia:

- liczba zależności wychodzących klasy lub pakietu (efferent coupling, oznaczana `Ce`): od ilu typów dany moduł zależy; wysoka wartość oznacza wiele powodów, dla których moduł może wymagać zmiany lub przestać się kompilować,
- liczba zależności przychodzących (afferent coupling, `Ca`): ile typów zależy od danego modułu; wysoka wartość oznacza szeroki promień oddziaływania każdej zmiany w tym module,
- niestabilność `I = Ce / (Ca + Ce)`: wartość bliska zeru opisuje moduł, od którego wiele zależy, a który sam zależy od niewielu, więc powinien być stabilny i abstrakcyjny; wartość bliska jedynce opisuje moduł łatwy do zmiany,
- cykle zależności między pakietami: uniemożliwiają zrozumienie, przetestowanie i wymianę modułów w izolacji, a w systemach legacy są jednym z najkosztowniejszych znalezisk,
- współzmienność (change coupling) liczona z historii systemu kontroli wersji: pliki, które regularnie zmieniają się w tych samych rewizjach, są sprzężone wiedzą, nawet jeżeli nie łączy ich żadna zależność w kodzie.

Spójność jest trudniejsza do bezpośredniego pomiaru. Rodzina metryk LCOM (lack of cohesion of methods) sprawdza, czy metody klasy operują na wspólnych polach; klasa, której metody rozpadają się na rozłączne grupy używające rozłącznych pól, jest kandydatem do podziału. Warianty LCOM różnią się definicją i skalą, więc wynik ma znaczenie tylko w ramach jednego narzędzia. Prostszym sygnałem bywa sama próba nazwania odpowiedzialności klasy: jeżeli opis wymaga spójnika „oraz”, metryka jedynie potwierdzi to, co widać w nazwach metod.

#### Jak interpretować

Celem nie jest minimalizacja sprzężenia do zera, bo moduł bez zależności nie robi niczego użytecznego. Znaczenie ma kierunek i charakter zależności: zależność od stabilnej abstrakcji jest tania, a zależność od często zmienianego konkretu droga. Wysokie `Ca` przy częstych zmianach modułu to sygnał ostrzegawczy sam w sobie. Metryki obiektowe generują też fałszywe alarmy: klasa przenosząca dane bez logiki uzyska złe wyniki LCOM, choć nie wymaga żadnej interwencji. Jak zawsze, liczba wskazuje kandydata do analizy, a nie werdykt.

### 6.7. Łączenie metryk

Najlepszych kandydatów do interwencji często wskazuje przecięcie kilku sygnałów.

| Sygnał | Samodzielna interpretacja | Interpretacja po połączeniu |
| --- | --- | --- |
| wysoka złożoność | kod ma wiele niezależnych ścieżek | wysoka złożoność i częste zmiany wskazują kosztowny obszar aktywnej pracy |
| niskie pokrycie | testy nie wykonują dużej części kodu | niskie pokrycie planowanej zmiany zwiększa niepewność tej zmiany |
| duplikacja | istnieją podobne fragmenty | kopie zmieniane razem i powodujące regresje wskazują powieloną wiedzę |
| częste zmiany | komponent aktywnie ewoluuje | częste zmiany, incydenty i szeroki promień oddziaływania wskazują ryzyko |
| duży rozmiar | komponent zawiera dużo kodu | duży rozmiar połączony z wieloma odpowiedzialnościami i właścicielami utrudnia analizę |

Warto obserwować trend w czasie. Jednorazowy wynik może być anomalią lub skutkiem konfiguracji. Trend pokazuje, czy interwencja zmniejszyła koszt i ryzyko, ale nadal wymaga interpretacji wraz z wynikami dostarczania oraz działania systemu.

### 6.8. Antywzorce użycia metryk

#### Optymalizacja pod wynik metryki

Gdy liczba staje się celem rozliczania, zespół może optymalizować wskaźnik zamiast jakości. Przykładem są testy bez wartościowych asercji pisane wyłącznie dla osiągnięcia progu pokrycia.

#### Porównywanie nieporównywalnych systemów

Inny język, generator kodu, profil reguł, domena i narzędzie mogą całkowicie zmienić wynik. Ranking zespołów według surowej złożoności lub liczby naruszeń jest pozbawiony wspólnej podstawy.

#### Zamiana korelacji w przyczynę

Złożone komponenty mogą mieć więcej defektów, ale bywają też większe, częściej zmieniane i bardziej krytyczne. Sama korelacja nie dowodzi, że obniżenie jednej metryki usunie defekty.

#### Uśrednianie ryzyka

Średnie pokrycie lub złożoność pakietu może ukryć pojedynczy fragment krytyczny. Do decyzji o zmianie potrzebny jest odpowiedni poziom szczegółowości.

#### Bramka bez analizy zmiany

Polityka jakości powinna przede wszystkim zapobiegać pogarszaniu aktywnie zmienianego kodu i chronić zachowania krytyczne. Próba natychmiastowego doprowadzenia całego systemu legacy do jednego progu często tworzy dużą kolejkę pracy bez uzasadnionej wartości.

### 6.9. Praktyczny pulpit diagnostyczny

Dla komponentu można zestawić niewielki pakiet informacji:

```text
Znaczenie:
  krytyczne procesy i cechy jakości

Ekspozycja na zmianę:
  planowane funkcje, częstotliwość zmian, pliki zmieniane razem

Tarcie:
  czas realizacji, czas testów, ręczne kroki, zależności między zespołami

Ryzyko:
  regresje, incydenty, wycofane wdrożenia, podatności, koncentracja wiedzy

Struktura:
  złożoność, sprzężenie, duplikacje, rozmiar, cykle

Zabezpieczenia:
  pokrycie istotnego zachowania, obserwowalność, wdrożenie etapowe, możliwość wycofania
```

Pakiet powinien być mały i powiązany z decyzją. Jego zadaniem jest zidentyfikowanie miejsca do dalszej analizy, a nie stworzenie uniwersalnej oceny jakości.

### 6.10. Krótka aktywność: dobór metryk

**Czas:** 4 minuty.

Celem jest zmniejszenie liczby regresji podczas comiesięcznych zmian reguł cenowych. Z poniższej listy wybierz trzy wskaźniki, które razem pomogą postawić diagnozę, i odrzuć dwa najmniej przydatne:

- globalne pokrycie linii całej aplikacji,
- liczba poprawek po wydaniu przypadająca na zmianę reguł,
- pokrycie gałęzi zmienianych reguł przez testy z istotnymi asercjami,
- liczba klas w repozytorium,
- współzmienność plików zawierających reguły cenowe,
- maksymalna złożoność dowolnej metody w systemie.

Dla każdego wyboru nazwij pytanie, na które wskaźnik pomaga odpowiedzieć, oraz co najmniej jedno ograniczenie jego interpretacji.

## 7. Dlaczego powstaje kod niskiej jakości

### 7.1. Kod jest zapisem dawnych decyzji

Obecna struktura systemu powstała w określonym kontekście informacji, terminów, kompetencji, narzędzi i celów. Ocena kodu bez tego kontekstu prowadzi do łatwych, ale mało użytecznych wniosków.

Decyzja może być rozsądna w chwili podjęcia, a następnie utracić trafność, ponieważ:

- zmieniły się wymagania i proces biznesowy,
- wzrosła skala danych lub ruchu,
- pojawiły się nowe wymagania bezpieczeństwa albo zgodności,
- komponent zaczął pełnić więcej funkcji, niż pierwotnie przewidywano,
- zmieniły się granice zespołów i odpowiedzialności,
- platforma lub zależność utraciła wsparcie,
- zespół zdobył lepsze zrozumienie domeny.

Powstały w ten sposób dług nie musi świadczyć o zaniedbaniu. Świadczy natomiast o potrzebie ponownej oceny decyzji w aktualnym kontekście.

### 7.2. Mechanizmy organizacyjne

#### Presja krótkoterminowa

Termin może uzasadniać kontrolowany kompromis. Problem pojawia się, gdy każdy termin jest traktowany jako wyjątkowy, a koszt rozwiązania tymczasowego nie jest rejestrowany ani ponownie oceniany. Zespół zaczyna płacić odsetki jeszcze podczas realizacji tego samego planu rozwoju produktu.

#### Niewłaściwe bodźce

Jeżeli sukces mierzy się wyłącznie liczbą dostarczonych funkcji, praca nad testowalnością, procesem budowania, obserwowalnością i uproszczeniem nie ma widocznego właściciela. Koszt zostaje przesunięty na późniejsze zmiany, inne zespoły albo etap eksploatacji.

#### Rozproszona odpowiedzialność

Komponent współdzielony przez wiele zespołów może nie mieć właściciela odpowiedzialnego za jego długofalową ewolucję. Każdy zespół wykonuje najmniejszą lokalną zmianę, lecz suma tych zmian pogarsza spójność rozwiązania.

#### Utrata wiedzy

Rotacja sama w sobie nie tworzy złego kodu. Problem powstaje, gdy wiedza nie jest utrwalana w testach, nazwach, kontraktach, decyzjach architektonicznych ani automatyzacji i nie jest rozpowszechniana dzięki praktykom wspólnego utrzymania.

#### Oddzielenie projektu od utrzymania

Zespół rozliczany wyłącznie z pierwszego wdrożenia może nie odczuwać kosztów późniejszej eksploatacji. Utrzymywalność staje się problemem kolejnego zespołu i kolejnego budżetu.

### 7.3. Mechanizmy techniczne

- brak szybkich testów i automatycznej weryfikacji,
- ręczne lub niestabilne budowanie oraz wdrażanie,
- kopiowanie rozwiązania zamiast zrozumienia wspólnej reguły,
- rozbudowa warunków zamiast jawnego modelowania zmienności,
- zależności globalne i ukryte efekty uboczne,
- współdzielone modele danych bez wyraźnych kontraktów,
- przedwczesne abstrakcje oparte na podobieństwie, które nie okazało się trwałe,
- dodawanie kolejnych warstw pośrednich bez usuwania przejściowych rozwiązań,
- odkładanie aktualizacji tak długo, że przestają być zmianami przyrostowymi.

### 7.4. Naturalna ewolucja systemu

Systemy odzwierciedlające rzeczywiste procesy muszą dostosowywać się do zmian otoczenia. Empiryczne prawa ewolucji oprogramowania opisują tendencję do wzrostu złożoności takich systemów, jeżeli nie wykonuje się jawnej pracy służącej jej ograniczaniu. Nie jest to matematyczne prawo obowiązujące każdy projekt, lecz użyteczna obserwacja zarządcza.

Każda lokalnie poprawna zmiana może dodać wyjątek, zależność lub kolejny wariant. Regularne upraszczanie, usuwanie nieużywanego kodu i aktualizowanie granic projektu są częścią rozwoju produktu, nie kosmetyką wykonywaną dopiero po zakończeniu „właściwej pracy”.

### 7.5. Analiza przyczyny, nie winnego

Samo stwierdzenie „programista skopiował kod” zatrzymuje analizę zbyt wcześnie. Należy zapytać:

1. Dlaczego skopiowanie było najszybszą lub najbezpieczniejszą dostępną opcją?
2. Czy wspólna reguła była znana i miała właściciela?
3. Czy istniały testy pozwalające zmienić pierwotną implementację?
4. Czy granice repozytoriów i zespołów utrudniały współdzielenie?
5. Czy harmonogram przewidywał czas na usunięcie rozwiązania tymczasowego?
6. Jaka zmiana procesu zapobiegnie kolejnemu wystąpieniu?

Refaktoryzacja usuwa objaw w kodzie. Bez zmiany mechanizmu organizacyjnego podobny problem może szybko powstać ponownie.

## 8. Refaktoryzacja czy przepisanie systemu

### 8.1. Najpierw terminologia

#### Refaktoryzacja

Refaktoryzacja zmienia wewnętrzną strukturę oprogramowania bez zmiany jego obserwowalnego zachowania. Jej celem jest ułatwienie dalszej pracy, a nie jednoczesne dodanie funkcji. W praktyce refaktoryzację często wykonuje się w ramach zmiany funkcjonalnej, ale kroki strukturalne i funkcjonalne powinny pozostać rozróżnialne.

#### Przepisanie

Przepisanie tworzy nową implementację, która ma zastąpić całość albo część dotychczasowego rozwiązania. Nowy kod nie dziedziczy automatycznie zachowań, dojrzałości operacyjnej i wiedzy zakodowanej w istniejącym systemie. Trzeba je jawnie odzyskać, zweryfikować, przenieść albo świadomie odrzucić.

#### Modernizacja

Modernizacja jest pojęciem szerszym. Może obejmować refaktoryzację, aktualizację platformy, opakowanie starego systemu interfejsem, zmianę modelu danych, wymianę komponentu, zakup produktu, migrację lub przepisanie.

### 8.2. To nie jest wybór binarny

| Strategia | Kiedy może być właściwa | Główne ryzyko |
| --- | --- | --- |
| Pozostawienie i monitorowanie | obszar jest stabilny, odizolowany i ma krótki horyzont życia | przeoczenie zmiany kontekstu |
| Stabilizacja | brakuje testów, obserwowalności, powtarzalnego procesu budowania lub możliwości wycofania wdrożenia | inwestycja poprawia bezpieczeństwo, ale jeszcze nie zmniejsza wszystkich kosztów strukturalnych |
| Refaktoryzacja lokalna | zachowanie ma wartość, a problem jest możliwy do ograniczenia | regresja przy niewystarczającej sieci bezpieczeństwa |
| Aktualizacja lub zmiana platformy | problemem jest głównie platforma, nie model biznesowy | zgodność bibliotek, danych i operacji |
| Opakowanie interfejsem | wnętrze może pozostać stabilne, a trzeba ograniczyć jego wpływ | utrwalenie starego modelu w nowym kontrakcie |
| Wymiana komponentu | granica i odpowiedzialność są dostatecznie wyraźne | ukryte integracje i semantyka danych |
| Stopniowe zastępowanie | system jest duży, krytyczny i musi działać podczas migracji | tymczasowa architektura pozostanie na stałe |
| Zakup gotowego produktu | proces nie stanowi wyróżnika, a produkt spełnia wymagania | uzależnienie od dostawcy i koszt dopasowania |
| Pełne przepisanie | zakres jest ograniczony, docelowe zachowanie znane, a stara podstawa nie może spełnić wymagań | długi okres pracy bez dostarczania wartości, utrata zachowań i ryzykowne przełączenie |
| Wycofanie funkcji | funkcja nie dostarcza już wystarczającej wartości | nieodkryci konsumenci i zależności |

Najlepsza strategia dla systemu może łączyć kilka pozycji. Można pozostawić stabilny moduł, opakować go kontraktem, wymienić jedną funkcję i refaktoryzować aktywnie rozwijany rdzeń.

### 8.3. Zacznij od oczekiwanego wyniku

„Chcemy mieć nowoczesny system” nie jest mierzalnym celem. Lepsze cele to:

- skrócić czas wprowadzania nowej reguły cenowej z tygodni do dni,
- umożliwić instalowanie poprawek bezpieczeństwa w określonym terminie,
- ograniczyć okno niedostępności podczas wdrożenia,
- wycofać kosztowną platformę i jej licencję,
- umożliwić obsługę nowego rynku,
- ograniczyć liczbę zespołów potrzebnych do zmiany jednego procesu.

Cel pozwala ocenić, czy proponowana strategia rzeczywiście rozwiązuje problem. Migracja do innego frameworka może pozostawić ten sam model zależności, proces wydawania i koszt koordynacji.

### 8.4. Kryteria decyzji

| Kryterium | Skłania ku pozostawieniu lub zmianie przyrostowej | Zwiększa zasadność wymiany albo przepisania |
| --- | --- | --- |
| Wartość obecnego zachowania | zachowanie jest cenne, lecz nie w pełni opisane | większość zachowania jest zbędna albo proces ma zostać zaprojektowany od nowa |
| Zakres | system jest duży, silnie zintegrowany i krytyczny | zakres jest mały, dobrze ograniczony i ma stabilny kontrakt |
| Platforma | istnieje wspierana ścieżka aktualizacji | obowiązkowych wymagań nie da się spełnić i nie ma realnej ścieżki migracji |
| Wiedza domenowa | wiedza znajduje się głównie w działającym kodzie i danych | zachowanie jest opisane, a eksperci i kryteria akceptacji są dostępni |
| Testowalność | można stopniowo tworzyć punkty separacji i testy | nową implementację można porównać z wiarygodnym modelem referencyjnym |
| Możliwość bezpiecznej migracji | jednorazowe przełączenie jest niedopuszczalne, więc potrzebne są małe kroki | nową implementację można uruchamiać równolegle, stopniowo przełączać i bezpiecznie wycofać |
| Ekonomia zmian | potrzebne funkcje można dostarczać podczas poprawy systemu | udokumentowany koszt pozostania przewyższa pełny koszt wymiany |
| Horyzont życia | krótki horyzont lub niewiele zmian skłaniają do pozostawienia, minimalnej stabilizacji albo wycofania | system ma długi horyzont i musi spełnić zasadniczo nowe potrzeby |
| Dane i integracje | migracja całości ma duży i słabo poznany zakres | dane oraz kontrakty można wiarygodnie zinwentaryzować i przenieść |
| Zdolność organizacji | jeden zespół może poprawiać produkt przyrostowo | organizacja potrafi finansować stary i nowy system aż do wyłączenia starego |

Tabela wspiera rozmowę, lecz nie działa jak algorytm. Znaczenie kryteriów zależy od celu i ograniczeń konkretnego systemu.

### 8.5. Pełny koszt przepisania

Porównywanie oszacowania nowej implementacji wyłącznie z kosztem zmiany starego kodu jest błędem. Należy uwzględnić:

- odzyskanie i uzgodnienie wymagań,
- utrzymanie starego systemu podczas tworzenia nowego,
- synchronizowanie zmian w obu rozwiązaniach,
- migrację, czyszczenie i uzgadnianie danych,
- odtworzenie integracji oraz niejawnych kontraktów,
- testy funkcjonalne i niefunkcjonalne,
- obserwowalność, bezpieczeństwo i gotowość operacyjną,
- szkolenia użytkowników i operatorów,
- uruchomienie równoległe, przełączenie oraz wycofanie wdrożenia,
- rzeczywiste wyłączenie starego kodu, infrastruktury i procesów.

Przepisanie może być właściwe. Powinno jednak wygrać na podstawie pełnego planu dostarczenia nowego rozwiązania i migracji, nie na podstawie porównania estetyki kodu.

### 8.6. Przewaga małych, odwracalnych kroków

Zmiana przyrostowa ogranicza liczbę założeń weryfikowanych jednocześnie. Typowy przebieg może wyglądać następująco:

1. Ustabilizować proces budowania, testy i obserwowalność obszaru.
2. Zidentyfikować granicę funkcji oraz jej aktualny kontrakt.
3. Oddzielić kod wywołujący od implementacji za pomocą interfejsu lub adaptera.
4. Zbudować nową implementację jednego przypadku.
5. Porównać wyniki lub skierować do niej ograniczoną część ruchu.
6. Rozszerzać zakres dopiero po potwierdzeniu zachowania operacyjnego.
7. Usunąć starą ścieżkę i elementy architektury przejściowej.

Podejście określane jako Strangler Fig stopniowo przejmuje funkcje starego systemu na jego granicach. Zmniejsza ryzyko jednorazowego przełączenia i pozwala wcześniej dostarczać wartość. Nie jest bezpłatne: okres współistnienia zwiększa liczbę komponentów, przepływów danych i obowiązków operacyjnych. Każdy element przejściowy powinien mieć właściciela oraz warunek usunięcia.

### 8.7. Sygnały ostrzegawcze dla pełnego przepisania

- głównym uzasadnieniem jest niechęć do kodu lub chęć użycia nowej technologii,
- zakres jest opisany jako osiągnięcie pełnej zgodności bez wskazania wartości potrzebnych zachowań,
- jednocześnie zmieniają się technologia, architektura, proces biznesowy i interfejs użytkownika,
- nie ma strategii migracji danych, porównania wyników ani wycofania wdrożenia,
- stary system nadal szybko się zmienia, ale nie zaplanowano synchronizacji,
- pierwsza wartość pojawi się dopiero po wielomiesięcznym lub wieloletnim przełączeniu,
- nie istnieje finansowany plan wyłączenia starego rozwiązania,
- organizacyjne przyczyny obecnych problemów pozostają bez zmian.

### 8.8. Sygnały, że sama refaktoryzacja może nie wystarczyć

- platforma nie może spełnić obowiązkowych wymagań bezpieczeństwa lub zgodności,
- nie można zapewnić wymaganego poziomu niezawodności, wydajności albo skali,
- model domenowy reprezentuje proces, który ma zostać fundamentalnie zmieniony,
- istotna zależność utraciła wsparcie i nie ma ścieżki aktualizacji w obecnej konstrukcji,
- koszt zachowania kompatybilności blokuje większość wartości nowego rozwiązania,
- zakres możliwej refaktoryzacji nie tworzy bezpiecznej drogi do docelowego stanu.

Nawet wtedy pełne przepisanie nie musi być jedyną opcją. Można wymienić krytyczny komponent lub zdolność biznesową, zachowując resztę systemu.

### 8.9. Proces podjęcia decyzji

1. Nazwij problem i mierzalny wynik biznesowy.
2. Ustal krytyczne zachowania, cechy jakości i ograniczenia.
3. Zbierz dane o zmianach, ryzyku, platformie, danych i integracjach.
4. Rozważ co najmniej trzy strategie, w tym pozostawienie lub ograniczenie ryzyka.
5. Oszacuj pełny koszt przejścia, działania równoległego i wyłączenia.
6. Wykonaj ograniczony czasowo eksperyment dla najważniejszej niewiadomej.
7. Wybierz najmniejszy krok, który dostarcza wartość lub wiedzę.
8. Zapisz założenia, sygnały sukcesu, warunki przerwania i termin ponownej oceny.

Decyzja jest hipotezą podlegającą weryfikacji. Nowe informacje z pierwszych kroków mogą uzasadnić zmianę kierunku.

### 8.10. Ocena przykładu `LegacyOrderService`

Sam fragment kodu nie uzasadnia pełnego przepisania systemu. Wskazuje problemy z podziałem odpowiedzialności, reprezentacją reguł i izolacją poszczególnych obliczeń, ale nie dowodzi ogólnej nietestowalności klasy. Zależności są przekazane przez konstruktor jako interfejsy, a metoda zwraca obliczoną kwotę, więc cały przebieg można testować za pomocą prostych implementacji zastępczych. Fragment nie zawiera natomiast danych o skali systemu, historii regresji, platformie, integracjach ani planie rozwoju produktu.

Rozsądny pierwszy krok to:

1. dodać przez publiczną metodę testy charakteryzujące aktualny przebieg, wykorzystując istniejące interfejsy repozytorium i bramki pocztowej jako punkty separacji,
2. uzgodnić przykłady oczekiwanego zachowania dla cen, podatków i wysyłki oraz oznaczyć zachowania jedynie odtworzone z kodu,
3. odseparować obliczenia od zapisu i wysyłania wiadomości pod ochroną tych testów,
4. dodać bardziej precyzyjne testy wyodrębnionych reguł i zebrać dane o ich planowanej zmienności,
5. dopiero wtedy zdecydować o dalszej strukturze lub wymianie części rozwiązania.

To działanie jest małe, dostarcza informacji i ogranicza ryzyko niezależnie od późniejszego kierunku modernizacji.

### 8.11. Krótka aktywność: wybór najmniejszego kroku

**Czas:** 4 minuty.

System ma wspieraną platformę jeszcze przez dwa lata, ale krytyczny moduł zmienia się co tydzień, nie ma szybkich testów i powoduje większość regresji. Porównaj trzy pierwsze kroki: pełne przepisanie systemu, testy charakteryzujące moduł oraz wymianę platformy. Wybierz jeden krok, zapisz założenie, które dzięki niemu zweryfikujesz, miernik wyniku i warunek przerwania. Dostępne informacje nie wystarczają do zatwierdzenia kompletnej strategii modernizacji.

## 9. Warsztat diagnostyczny

### 9.1. Ćwiczenie 1: obserwacja bez pochopnej naprawy

**Cel:** oddzielenie faktów, hipotez i nieznanych elementów zachowania.

**Materiał:** klasa `LegacyOrderService` z sekcji 5.3.

**Czas:** 20 minut.

**Planowana zmiana:** w następnym kwartale system ma obsługiwać Czechy, typ klienta `PARTNER` oraz odbiór osobisty jako trzeci wariant dostawy.

#### Zadanie

1. Przeczytaj kod bez proponowania docelowego projektu.
2. Zapisz co najmniej pięć obserwowalnych faktów.
3. Zapisz co najmniej trzy hipotezy o możliwych problemach.
4. Zapisz pytania domenowe, na które kod nie daje pewnej odpowiedzi.
5. Wskaż element planowanej zmiany, przy którym dany symptom stanie się istotny.
6. Określ, jakie dowody pozwoliłyby potwierdzić lub odrzucić hipotezę.

#### Arkusz

| Fragment | Fakt | Hipoteza lub ryzyko | Potrzebny dowód | Planowana zmiana |
| --- | --- | --- | --- | --- |
|  |  |  |  |  |
|  |  |  |  |  |
|  |  |  |  |  |
|  |  |  |  |  |
|  |  |  |  |  |

#### Ograniczenie

Nie wolno jeszcze proponować wzorca projektowego ani kompletnej refaktoryzacji. Celem jest trafna diagnoza, a nie szybkość generowania rozwiązania.

### 9.2. Ćwiczenie 2: klasyfikacja i priorytetyzacja długu

**Cel:** wykazanie, że najgorszy wynik metryki nie musi oznaczać najwyższego priorytetu.

**Czas:** 30 minut.

Poniżej przedstawiono fikcyjny opis projektu napisanego w Javie. Wszystkie wyniki pochodzą z tej samej wersji kodu i konfiguracji narzędzi.

| Kandydat | Dane |
| --- | --- |
| `PricingService` | ta sama reguła VIP występuje w trzech metodach, a podczas dwóch wdrożeń pominięto jedną kopię; złożoność maksymalna metody: 31; duplikacja: 22 procent; siedem zmian w ciągu pół roku; trzy regresje; pięć podobnych zmian w planie rozwoju produktu; oszacowanie interwencji: od 3 do 5 osobodni |
| `AnnualReportFormatter` | złożoność maksymalna metody: 54; brak zmian od 22 miesięcy; komponent ma zostać wycofany za cztery miesiące; oszacowanie interwencji: od 2 do 4 osobodni |
| `OldPaymentAdapter` | niski poziom złożoności i brak duplikacji; dostawca zakończy wsparcie zależności za sześć miesięcy; nowa metoda płatności korzysta z niezgodnego interfejsu; nie są znane aktywne podatności; oszacowanie migracji: od 4 do 7 osobodni |
| `CustomerExportController` | komentarz `TODO` opisuje eksport, który nigdy nie został zatwierdzony jako wymaganie; brak incydentów i planowanych zmian |
| `TaxRoundingRule` | zatwierdzone wymaganie nakazuje zaokrąglanie `HALF_UP`, implementacja używa `DOWN`, a nieprawidłową kwotę odtworzono dla faktury produkcyjnej; brak danych o przyczynie strukturalnej |

#### Zadanie

1. Dla każdej pozycji wypełnij osobne kolumny: fakt, symptom, potencjalny element długu, defekt, ograniczenie środowiskowe i poziom pewności. Kategorie mogą współwystępować.
2. Dla elementów długu użyj pełnego szablonu z sekcji 4.5: lokalizacja, konstrukcja, przyczyna, konsekwencja, dowód, zaobserwowane odsetki, ekspozycja, kapitał bieżący, ryzyko spłaty, decyzja, poziom pewności, właściciel i termin przeglądu.
3. Dysponujesz budżetem sześciu osobodni. Zaproponuj działania i uzasadnij, czego świadomie nie wykonasz.
4. Wskaż informacje, których brakuje do podjęcia decyzji.
5. Dla celu „zmniejszyć liczbę regresji w wycenie” wybierz trzy przydatne metryki diagnostyczne, odrzuć dwie inne i opisz ograniczenia interpretacji każdej decyzji.
6. Powtórz ocenę przy nowym założeniu: `AnnualReportFormatter` pozostanie aktywny przez kolejne dwa lata i będzie zmieniany co miesiąc.

### 9.3. Ćwiczenie 3: wybór strategii modernizacji

**Cel:** przygotowanie decyzji wykraczającej poza prosty wybór między refaktoryzacją a pełnym przepisaniem.

**Czas:** 50 minut w grupach liczących od trzech do czterech osób. Na analizę należy przeznaczyć 15 minut, na przygotowanie jednostronicowej decyzji 25 minut, a na prezentację i pytania 10 minut. Strategię wdrożenia należy opisać na poziomie koncepcji.

#### Scenariusz

System `ClaimsCore` obsługuje rozliczanie szkód przez całą dobę. Jest aplikacją napisaną w Javie o rozmiarze około 210 tysięcy linii kodu. Współpracuje z 18 systemami, przechowuje historię kilku milionów spraw i ma wspólną bazę z dwoma procesami raportowymi.

Aktualna platforma ma zagwarantowane wsparcie przez kolejne 30 miesięcy. Proces budowania jest powtarzalny, ale wykonanie pełnego zestawu testów regresyjnych trwa 11 godzin. Pokrycie linii wynosi 62 procent, a gałęzi 28 procent. Dla najczęściej zmienianego modułu reguł uprawnień pokrycie gałęzi wynosi 14 procent. W ostatnim roku ten moduł odpowiadał za większość poprawek po wydaniu.

Dokumentacja opisuje główne procesy, ale wiele wyjątków znajduje się wyłącznie w kodzie i danych. Organizacja ma dostęp do ekspertów domenowych. W ciągu najbliższych dwóch lat planuje regularnie wprowadzać nowe warianty produktów.

Wstępnie oszacowano, że pełne przepisanie potrwa 18 miesięcy. Oszacowanie nie obejmuje migracji danych, odtworzenia wszystkich integracji, równoległego utrzymania ani wyłączenia starego systemu. Podczas tworzenia nowego rozwiązania w obecnym systemie nadal trzeba wprowadzać zmiany wynikające z regulacji.

Moduł reguł uprawnień ma kontrakt wejścia i wyjścia, który można jednoznacznie opisać. Jego wynik można obliczać równolegle w starej i nowej implementacji bez wykorzystywania go do podejmowania decyzji w środowisku produkcyjnym.

Cel biznesowy brzmi:

> Skrócić czas wdrożenia nowej reguły z 15 do 3 dni i zmniejszyć liczbę poprawek po wydaniu, bez obniżenia dostępności systemu.

#### Zadanie

Przygotuj rekomendację, która zawiera:

1. co najmniej trzy rozważone strategie,
2. wybraną strategię i jej związek z celem,
3. najważniejsze założenia oraz niewiadome,
4. pierwszy krok możliwy do zakończenia w ciągu czterech tygodni,
5. sposób weryfikacji zgodności wyników,
6. strategię wdrożenia i wycofania wdrożenia,
7. warunek przerwania albo zmiany kierunku,
8. kryterium usunięcia architektury przejściowej,
9. mierniki wyniku, nie tylko mierniki aktywności.

#### Szablon rekordu decyzji

```text
Problem:

Oczekiwany wynik:

Ograniczenia:

Rozważone opcje:
1.
2.
3.

Decyzja:

Uzasadnienie:

Założenia i niewiadome:

Pierwszy odwracalny krok:

Sposób weryfikacji:

Plan wdrożenia i wycofania wdrożenia:

Ryzyka i reakcje:

Mierniki wyniku:

Warunki ponownej oceny:

Plan usunięcia rozwiązania przejściowego:
```

## 10. Wskazówki do omówienia warsztatu

### 10.1. Ćwiczenie 1

Poniższa tabela jest jedną z możliwych odpowiedzi. Fakty wynikają bezpośrednio z kodu. Hipotezy wymagają potwierdzenia przez wymagania, testy, dane lub obserwację systemu.

| Fragment | Fakt | Hipoteza lub ryzyko | Potrzebny dowód | Planowana zmiana |
| --- | --- | --- | --- | --- |
| warunki dotyczące `customerType` | typ klienta jest reprezentowany przez `String`, a metoda sprawdza wyłącznie wartość `VIP` | dodanie `PARTNER` może rozbudować zestaw warunków i ujawnić nieznane zasady łączenia rabatów | zatwierdzone przykłady cen dla każdego typu klienta, dane o aktualnie używanych wartościach, testy istniejących rabatów | nowy typ `PARTNER` |
| warunki dotyczące `destinationCountry` | jawne stawki istnieją tylko dla `PL` i `DE`, a każda inna wartość daje podatek równy zero | zero może oznaczać zarówno poprawną stawkę, jak i nieobsługiwany kraj ukryty jako poprawny wynik | wymagania podatkowe, kontrakt metody, lista krajów z danych produkcyjnych, zachowanie oczekiwane dla nieznanej wartości | obsługa Czech |
| obliczanie `shipping` | metoda rozróżnia tylko przesyłkę ekspresową i standardową; koszt zależy także od sumy po rabatach | parametr `boolean` nie pozwoli jednoznacznie reprezentować odbioru osobistego, a próg bezpłatnej wysyłki może nie dotyczyć tego wariantu | reguły dostawy, przykłady graniczne dla kwoty 200,00, decyzja o kolejności rabatu i naliczania wysyłki | odbiór osobisty |
| pętla po pozycjach | rabat VIP i rabat ilościowy są składane przez mnożenie, bez pośredniego zaokrąglania | rabaty mogą zgodnie z wymaganiem składać się multiplikatywnie albo sumować jako punkty procentowe; etap zaokrąglania także może być istotny | przykłady zatwierdzone przez właściciela produktu, istniejące faktury i testy charakteryzujące przypadki łączenia rabatów | typ `PARTNER` może mieć odmienną politykę rabatową |
| końcowa część `placeOrder` | jedna metoda oblicza kwotę, zapisuje wynik i wysyła wiadomość; zapis następuje przed wysłaniem | awaria bramki pocztowej może pozostawić zapisane zamówienie, mimo że metoda zakończy się wyjątkiem | kontrakt transakcyjny, sposób obsługi wyjątków przez wywołującego, logi awarii i zasady ponawiania | każda z planowanych zmian wymaga bezpiecznej regresji całego przebiegu |
| walidacja wejścia | sprawdzana jest obecność zamówienia i co najmniej jednej pozycji, ale nie są jawnie sprawdzane liczba sztuk, cena, adres ani kod kraju | niedozwolone wartości mogą docierać z wcześniejszej warstwy albo nie być kontrolowane wcale | kontrakty wejścia, walidacja w kontrolerze lub modelu, próbki błędnych żądań | nowe wartości zwiększą liczbę wariantów wejścia |

Przykładowe pytania domenowe:

- Czy brak stawki oznacza nieobsługiwany kraj, stawkę zerową czy sprzedaż zwolnioną z podatku?
- Czy awaria wiadomości powinna cofnąć zapis zamówienia, czy wysyłka ma być ponawiana niezależnie?
- Czy rabaty mają być składane multiplikatywnie, czy sumowane jako punkty procentowe, i na którym etapie należy zaokrąglać kwoty?
- Czy próg bezpłatnej wysyłki jest liczony przed rabatem czy po nim?
- Czy odbiór osobisty zawsze ma koszt zerowy?

Stwierdzenie „podatek jest liczony błędnie” nie jest faktem widocznym w kodzie. Bez reguły biznesowej pozostaje hipotezą. Wystarczająca odpowiedź na ćwiczenie zawiera co najmniej pięć podobnych rozróżnień i wskazuje dowód adekwatny do każdej hipotezy. Najlepszy pierwszy krok zwiększa wiedzę lub kontrolę nad zachowaniem. Nie musi jeszcze tworzyć docelowej architektury.

### 10.2. Ćwiczenie 2

Kategorie nie są rozłączne. Modelowa klasyfikacja może wyglądać następująco:

| Kandydat | Fakt | Symptom | Potencjalny dług | Defekt | Ograniczenie | Pewność |
| --- | --- | --- | --- | --- | --- | --- |
| `PricingService` | istnieją trzy kopie reguły; dwukrotnie pominięto jedną z nich | duplikacja i wysoka złożoność | rozproszona reguła zwiększa koszt oraz ryzyko kolejnych zmian | brak dowodu na aktualnie błędny wynik | brak | wysoka dla konstrukcji i odsetek |
| `AnnualReportFormatter` | maksymalna złożoność wynosi 54; brak zmian od 22 miesięcy; wycofanie planowane za cztery miesiące | wysoka złożoność | brak wystarczającego dowodu na istotny przyszły koszt | brak danych | krótki pozostały okres życia | wysoka dla faktów, niska dla długu wymagającego spłaty |
| `OldPaymentAdapter` | wsparcie zależności kończy się za sześć miesięcy; nowy interfejs jest niezgodny | nie wynika z podanych metryk kodu | zależność może ograniczać przyszłą zmianę, ale konstrukcja i koszt pozostawienia wymagają rozpoznania | brak danych | decyzja dostawcy i niezgodny interfejs | wysoka dla ograniczenia, średnia dla długu |
| `CustomerExportController` | komentarz opisuje niezatwierdzoną funkcję | możliwy zbędny komentarz | brak wykazanego przyszłego kosztu | brak | brak | wysoka |
| `TaxRoundingRule` | implementacja używa `DOWN` zamiast wymaganego `HALF_UP`; błąd odtworzono | brak opisanego symptomu strukturalnego | brak danych o konstrukcji powodującej przyszły koszt | tak | brak | wysoka |

Pełny rekord najlepiej potwierdzonego elementu długu:

| Pole | Wartość |
| --- | --- |
| Lokalizacja | `PricingService`, trzy metody zawierające regułę VIP |
| Konstrukcja | trzy niezależne kopie tej samej reguły |
| Przyczyna | brak danych; do sprawdzenia w historii zmian i rozmowie z właścicielem |
| Konsekwencja | każda zmiana reguły wymaga zgodnej modyfikacji trzech miejsc i zwiększa ryzyko pominięcia |
| Dowód | dwa wdrożenia z pominiętą kopią, trzy regresje w ciągu pół roku |
| Zaobserwowane odsetki | co najmniej analiza i naprawa dwóch niespójnych wdrożeń; brak danych o liczbie osobodni |
| Ekspozycja | pięć podobnych zmian w planie rozwoju produktu |
| Kapitał bieżący | od 3 do 5 osobodni według podanego oszacowania |
| Ryzyko spłaty | kopie mogą zawierać zamierzone różnice; wspólna abstrakcja może połączyć reguły rozwijane niezależnie |
| Decyzja | zabezpieczyć przykłady zachowania i usunąć potwierdzoną duplikację przed kolejną zmianą VIP |
| Poziom pewności | wysoki dla potrzeby interwencji, średni dla docelowej konstrukcji |
| Właściciel i termin przeglądu | właściciel modułu wyceny; przegląd po analizie trzech kopii, nie później niż przed następną zmianą rabatu |

`OldPaymentAdapter` należy na tym etapie zarejestrować jako ograniczenie i potencjalny element długu. Nie należy uzupełniać braków zgadywaniem. Przykładowy rekord jawnie pokazuje niepewność:

| Pole | Wartość |
| --- | --- |
| Lokalizacja | granica integracji płatniczej i używana zależność |
| Konstrukcja | zależność od interfejsu, który nie obsługuje nowej metody płatności |
| Przyczyna | ewolucja interfejsu dostawcy; wcześniejsza decyzja mogła być poprawna |
| Konsekwencja | po utracie wsparcia mogą zniknąć poprawki i możliwość dodania nowej metody; dokładny wpływ nieznany |
| Dowód | ogłoszony termin końca wsparcia i potwierdzona niezgodność interfejsu |
| Zaobserwowane odsetki | brak danych |
| Ekspozycja | planowana nowa metoda płatności i termin sześciu miesięcy |
| Kapitał bieżący | od 4 do 7 osobodni, z nieznanym zakresem testów kontraktowych i wdrożenia |
| Ryzyko spłaty | zmiana zachowania integracji, niepełna zgodność danych, wymagania certyfikacyjne dostawcy |
| Decyzja | jednodniowe rozpoznanie ograniczeń i kontraktu, następnie ponowna priorytetyzacja |
| Poziom pewności | średni |
| Właściciel i termin przeglądu | właściciel integracji płatniczej; następny przegląd po rozpoznaniu, w tym samym tygodniu |

Przykładowy podział sześciu osobodni:

| Nakład | Działanie | Oczekiwany rezultat |
| ---: | --- | --- |
| 1 osobodzień | poprawić `TaxRoundingRule` i dodać test regresyjny odtwarzający zatwierdzony przypadek | usunięty potwierdzony defekt |
| 4 osobodni | scharakteryzować trzy warianty reguły VIP, a po potwierdzeniu równoważności skonsolidować je i uruchomić testy | ograniczone odsetki w obszarze pięciu planowanych zmian |
| 1 osobodzień | wykonać ograniczone czasowo rozpoznanie `OldPaymentAdapter` | znany zakres wpływu końca wsparcia, kontraktu, certyfikacji i migracji |

Jeżeli interwencja w `PricingService` nie mieści się w czterech dniach, zespół kończy bezpieczny etap charakteryzacji i aktualizuje oszacowanie, zamiast przyspieszać zmianę kosztem weryfikacji. Świadomie nie refaktoryzuje `AnnualReportFormatter` i nie implementuje niezatwierdzonego eksportu.

Brakuje przede wszystkim kosztu i terminu biznesowego nowej metody płatności, dokładnego zakresu wsparcia dostawcy, aktywnych konsumentów adaptera, semantycznych różnic między kopiami reguły VIP, czasu obsługi regresji, krytyczności raportu rocznego oraz potwierdzenia terminu jego wycofania.

Dla celu „zmniejszyć liczbę regresji w wycenie” przydatne są:

1. liczba poprawek po wydaniu przypadająca na zmianę reguł wyceny, ponieważ bezwzględna liczba regresji rośnie także wraz z liczbą wdrożeń,
2. liczba miejsc wymagających wspólnej zmiany oraz ich współzmienność w historii, ponieważ wskazuje ryzyko pominięcia kopii, ale nie dowodzi wspólnej semantyki,
3. pokrycie gałęzi zmienionych reguł przez testy z istotnymi asercjami, ponieważ ujawnia niewykonane warianty, ale nie ocenia jakości danych i oczekiwanych wyników.

Globalne pokrycie linii należy odrzucić, ponieważ może rosnąć poza obszarem wyceny. Maksymalną złożoność całego systemu także należy odrzucić, ponieważ nie odpowiada na pytanie o przyczyny regresji w aktywnie zmienianych regułach. Oba wyniki mogą służyć innym analizom, ale nie są właściwymi wskaźnikami tego celu.

Po zmianie założenia dotyczącego `AnnualReportFormatter` wzrasta jego ekspozycja: w ciągu dwóch lat może wystąpić około 24 zmian. Nie oznacza to automatycznej potrzeby pełnej refaktoryzacji, ale uzasadnia analizę struktury metody, historii podobnych zmian i ochrony testami. Przy potwierdzonym koszcie każdej zmiany formatter powinien zostać ponownie porównany z adapterem i modułem cenowym.

### 10.3. Ćwiczenie 3

Sam przedstawiony materiał nie uzasadnia pełnego przepisania `ClaimsCore`. Oszacowanie nie obejmuje znacznej części kosztu, zachowanie jest częściowo niejawne, system jest krytyczny i musi równolegle ewoluować.

#### Rozważone strategie

1. Pozostawić obecną architekturę, skrócić zestaw testów dla modułu reguł i dodać testy charakteryzujące. Jest to mały koszt i szybka poprawa informacji zwrotnej, ale nie musi zapewnić niezależnego wdrażania ani ograniczyć sprzężenia z resztą systemu.
2. Przyrostowo wydzielać moduł reguł za jawnym kontraktem, początkowo obliczając wyniki równolegle. Strategia wykorzystuje istniejącą granicę, pozwala porównywać zachowanie i dostarczać korzyści kategoriami spraw.
3. Przepisać cały `ClaimsCore` i przełączyć system po osiągnięciu zgodności. Strategia może usunąć część ograniczeń, ale ma niepełne oszacowanie, późno dostarcza wartość i wymaga równoległego odtwarzania zmian regulacyjnych, danych oraz 18 integracji.

#### Modelowy rekord decyzji

**Problem:** najczęściej zmieniany moduł reguł ma wolną pętlę weryfikacji, niskie pokrycie gałęzi i odpowiada za większość poprawek po wydaniu. Pełny system musi zachować dostępność i nadal przyjmować zmiany regulacyjne.

**Oczekiwany wynik:** skrócenie czasu od zaakceptowania reguły do bezpiecznego wdrożenia z 15 do 3 dni oraz zmniejszenie udziału zmian wymagających poprawki po wydaniu, bez pogorszenia dostępności.

**Decyzja:** przyrostowo wydzielać moduł reguł za jawnym kontraktem. Najpierw zbudować szybką weryfikację i uruchamianie równoległe, następnie przełączać pojedyncze kategorie spraw. Nie rozpoczynać pełnego przepisania systemu.

**Założenia:** kontrakt można odseparować od zapisu do wspólnej bazy, te same wejścia można bezpiecznie przekazać obu implementacjom, a eksperci domenowi mogą rozstrzygać istotne różnice. Niewiadome obejmują kompletność danych wejściowych, deterministyczność reguł, dopuszczalny narzut uruchamiania równoległego i liczbę wyjątków zależnych od stanu wspólnej bazy.

**Pierwszy krok w ciągu czterech tygodni:** 

| Tydzień | Rezultat |
| --- | --- |
| 1 | jawny opis wejścia, wyjścia i efektów ubocznych; zestaw reprezentatywnych spraw uzgodniony z ekspertami; bazowe mierniki czasu wdrożenia i poprawek |
| 2 | szybki zestaw testów charakteryzujących kontrakt oraz automatyczne porównanie wyników dla danych historycznych |
| 3 | nowa implementacja jednej ograniczonej kategorii reguł, bez prawa do podejmowania decyzji produkcyjnych |
| 4 | uruchomienie równoległe dla kontrolowanej próbki, raport rozbieżności, wpływ na wydajność i decyzja o następnym kroku |

**Sposób weryfikacji:** obie implementacje otrzymują znormalizowane, identyfikowalne wejście. System porównuje wynik decyzji, kod uzasadnienia i istotne wartości pośrednie. Każda różnica jest przypisywana do jednej z kategorii: defekt nowej ścieżki, ujawniony defekt starej ścieżki, zamierzona zmiana reguły albo niewystarczające dane. Sama zgodność procentowa nie wystarcza bez analizy znaczenia rozbieżności.

**Wdrożenie i wycofanie:** początkowo nowa implementacja działa w trybie obserwacyjnym i nie wpływa na decyzję. Można ją wyłączyć flagą konfiguracyjną. Po spełnieniu kryteriów wybrana kategoria spraw przechodzi na nową ścieżkę z możliwością natychmiastowego skierowania jej z powrotem do starej implementacji. Zmiana nie obejmuje w pierwszym kroku migracji źródła danych ani nieodwracalnych zapisów. Zespół przed przełączeniem ćwiczy wycofanie na środowisku przedprodukcyjnym.

**Ryzyka i reakcje:** niejawne zależności od bazy ogranicza się przez rejestrowanie kompletnego wejścia; narzut wydajności kontroluje się na próbce i budżetem czasu; wzrost złożoności przejściowej ogranicza właściciel rozwiązania, termin przeglądu oraz kryteria usunięcia. Rozbieżności o skutku finansowym lub regulacyjnym blokują przełączenie danej kategorii.

**Warunek przerwania albo zmiany kierunku:** po czterech tygodniach należy zatrzymać wydzielanie i ponownie ocenić strategię, jeżeli nie da się utworzyć powtarzalnego kontraktu bez wykonywania efektów ubocznych albo nie można wiarygodnie sklasyfikować rozbieżności. Po rozpoczęciu przełączeń każda niewyjaśniona różnica o skutku finansowym, spadek dostępności lub przekroczenie uzgodnionego budżetu opóźnienia powoduje powrót danej kategorii do starej ścieżki. Dokładne progi muszą zatwierdzić właściciele biznesowi i operacyjni.

**Kryterium usunięcia rozwiązania przejściowego:** stara implementacja nie obsługuje żadnej kategorii przez dwa pełne cykle wydawnicze, wszystkie zmiany reguł są wprowadzane wyłącznie w nowym module, nie ma niewyjaśnionych rozbieżności, przećwiczono odtworzenie usługi, a właściciele danych i integracji zatwierdzili wyłączenie. Następnie usuwa się podwójne obliczanie, flagi i adaptery, zamiast pozostawiać je jako trwałą warstwę.

**Mierniki wyniku:** mediana i 85. percentyl czasu od zatwierdzenia reguły do wdrożenia, odsetek zmian reguł wymagających poprawki po wydaniu, czas uzyskania wiarygodnego wyniku testów modułu, liczba niewyjaśnionych rozbieżności na tysiąc porównań oraz dostępność i czas odpowiedzi systemu. Liczba przepisanych klas, wykonanych zadań lub procent migracji są miernikami aktywności i nie dowodzą osiągnięcia celu.

## 11. Sprawdzenie wiedzy

### Pytania

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

### Odpowiedzi

1. Istotne są koszt i ryzyko zmiany, znaczenie systemu, testowalność oraz kontekst planowanych prac. Nowy kod także może być trudny do bezpiecznej zmiany.
2. Symptom wskazuje możliwy problem projektowy. Defekt oznacza zaobserwowaną niezgodność zachowania z wymaganiem.
3. Gdy brak wiarygodnej weryfikacji zwiększa przyszły koszt albo ryzyko zmian, na przykład wymusza powtarzalną ręczną regresję.
4. Kapitał to przybliżony koszt usunięcia lub ograniczenia konstrukcji, a odsetki to dodatkowy koszt ponoszony podczas kolejnych prac, które ją napotykają.
5. Pokrycie potwierdza wykonanie kodu, ale nie jakość danych, asercji, modelu przypadków ani zdolność wykrywania ważnych defektów.
6. Opisuje liczbę liniowo niezależnych ścieżek wynikających ze struktury przepływu sterowania. Nie mierzy pełnej trudności domeny, czytelności ani wszystkich możliwych zachowań.
7. Podobne fragmenty mogą reprezentować różne reguły, które mają rozwijać się niezależnie. Wspólna abstrakcja stworzyłaby wtedy nieprawidłowe sprzężenie.
8. Pomaga znaleźć aktywne obszary, w których trudna struktura faktycznie wpływa na bieżącą pracę. Każdy z tych sygnałów osobno daje słabszą podstawę decyzji.
9. Między innymi odzyskanie wymagań, utrzymanie dwóch systemów, migracja danych, integracje, testy niefunkcjonalne, operacje, szkolenia, przełączenie, wycofanie wdrożenia i wyłączenie starego rozwiązania.
10. Decyzja opiera się na założeniach i niepełnych danych. Eksperymenty oraz zmiany kontekstu mogą uzasadnić korektę kierunku, zanim powstanie duży koszt nieodwracalny.

## 12. Lista kontrolna oceny systemu legacy

### Wartość i zakres

- [ ] Wiemy, którzy użytkownicy i procesy zależą od systemu.
- [ ] Znamy funkcje krytyczne oraz funkcje możliwe do wycofania.
- [ ] Znamy planowany horyzont życia systemu.
- [ ] Potrafimy nazwać oczekiwany wynik modernizacji.

### Zachowanie

- [ ] Znamy źródła wiedzy o aktualnym zachowaniu.
- [ ] Potrafimy odtworzyć ważne przypadki na kontrolowanych danych.
- [ ] Rozróżniamy zachowanie zamierzone, historyczny wyjątek i defekt.
- [ ] Krytyczne reguły mają właścicieli domenowych.

### Struktura i zmiana

- [ ] Znamy obszary często zmieniane.
- [ ] Wiemy, które komponenty zwykle zmieniają się razem.
- [ ] Znamy cykle i najważniejsze zależności.
- [ ] Łączymy metryki strukturalne z historią zmian i incydentami.

### Testy i informacja zwrotna

- [ ] Proces budowania jest powtarzalny.
- [ ] Znamy czas i stabilność poszczególnych zestawów testów.
- [ ] Analizujemy pokrycie zachowania objętego zmianą, nie tylko globalny procent.
- [ ] Potrafimy wykryć regresję przed pełnym wdrożeniem.

### Operacje i platforma

- [ ] Znamy status wsparcia platformy i kluczowych zależności.
- [ ] Znamy wymagania bezpieczeństwa, zgodności, wydajności i dostępności.
- [ ] System ma wystarczającą obserwowalność.
- [ ] Wdrożenie oraz jego wycofanie są opisane i przećwiczone.

### Dane i integracje

- [ ] Znamy właścicieli danych i ich źródła prawdy.
- [ ] Zinwentaryzowaliśmy konsumentów oraz niejawne integracje.
- [ ] Rozumiemy semantykę danych potrzebną do migracji.
- [ ] Potrafimy uzgodnić wyniki starej i nowej ścieżki.

### Dług techniczny

- [ ] Elementy długu mają konkretną lokalizację i konsekwencję.
- [ ] Oddzielamy wynik narzędzia od decyzji biznesowej.
- [ ] Oszacowania mają zakres, podstawę i poziom pewności.
- [ ] Każda decyzja ma właściciela oraz termin lub warunek przeglądu.

### Strategia

- [ ] Rozważyliśmy więcej niż dwie opcje.
- [ ] Uwzględniliśmy pełny koszt przejścia oraz wyłączenia starego rozwiązania.
- [ ] Pierwszy krok jest ograniczony, mierzalny i możliwie odwracalny.
- [ ] Architektura przejściowa ma plan usunięcia.
- [ ] Mierzymy wynik biznesowy i operacyjny, nie tylko ilość wykonanej pracy.

## 13. Podsumowanie

1. Legacy nie jest synonimem starego ani brzydkiego kodu. O problemie decydują wartość systemu oraz tarcie i ryzyko związane ze zmianą.
2. Diagnoza powinna łączyć fakt, kontekst, konsekwencję i dowód.
3. *Code smells* są hipotezami o problemach projektowych, nie automatyczną listą zadań.
4. Dług techniczny dotyczy przyszłego kosztu lub ograniczenia zmian. Nie obejmuje automatycznie każdego defektu, braku funkcji i starszej technologii.
5. Złożoność, pokrycie i duplikacje są sygnałami. Ich znaczenie powstaje dopiero po połączeniu z planem zmian, historią i krytycznością.
6. Celem zarządzania długiem nie jest jego całkowite usunięcie, lecz świadoma kontrola kosztów, ryzyka i dostępnych opcji.
7. Refaktoryzacja i pełne przepisanie są tylko dwiema z wielu strategii modernizacji.
8. Bezpieczny kierunek zwykle zaczyna się od małego kroku, który zwiększa wiedzę, ogranicza ryzyko lub dostarcza mierzalną wartość.
