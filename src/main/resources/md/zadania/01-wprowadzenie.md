# Moduł 1. Wprowadzenie - zadania dla uczestników

## Jak pracować

- Moduł ma charakter diagnostyczny. We wszystkich zadaniach oceniasz kod, dane i scenariusze, ale **nie projektujesz jeszcze docelowego rozwiązania**, chyba że polecenie wprost o to prosi.
- Rozróżniaj konsekwentnie: **fakt** (co widać w kodzie lub danych), **hipotezę** (co może być problemem), **defekt** (niezgodność z wymaganiem), **symptom** (sygnał możliwego problemu projektowego, np. *code smell*), **ograniczenie** (warunek, którego zespół nie kontroluje).
- Diagnoza powinna mieć cztery części: fakt, kontekst, konsekwencję i dowód.
- Nie uzupełniaj brakujących danych zgadywaniem. Jeżeli czegoś nie wiesz, zapisz to jako niewiadomą i wskaż, skąd można ją pozyskać.
- Krótkie aktywności wykonuj indywidualnie lub w parach w podanym czasie. Ćwiczenia warsztatowe wykonuj w parach lub grupach zgodnie z poleceniem.
- Kod źródłowy modułu znajduje się w katalogu `src/main/java/pl/training/module1/`. Przykłady uruchomisz poleceniem `mvn -q compile && java -cp target/classes pl.training.module1.Module1Examples`, a testy poleceniem `mvn test -Dtest='pl.training.module1.**'`.
- Odpowiedniki kodu w C# i TypeScript znajdziesz w `csharp/src/Training.Module1/` oraz `typescript/src/module1/` (te same nazwy klas i plików).

## Przegląd zadań

| Nr | Nazwa | Forma | Czas |
| --- | --- | --- | --- |
| Aktywność 1.1 | Nazwij kontrakt | indywidualnie lub w parach | 4 min |
| Aktywność 1.2 | Brakujące pola rekordu | indywidualnie lub w parach | 3 min |
| Aktywność 1.3 | Wybór najmniejszego kroku | indywidualnie lub w parach | 4 min |
| Aktywność 1.4 | Fakt czy ocena | indywidualnie | 3 min |
| Aktywność 1.5 | Dobór metryk | indywidualnie lub w parach | 4 min |
| Ćwiczenie 1 | Obserwacja bez pochopnej naprawy | pary | 15 min + 5 min omówienia |
| Ćwiczenie 2 | Od zapachu do kierunku zmiany | pary | 10 min + 5 min omówienia |
| Ćwiczenie 3 | Klasyfikacja i priorytetyzacja długu | pary lub grupy | 20 min + 10 min omówienia |
| Ćwiczenie 4 | Wybór strategii modernizacji | grupy 3-4 osoby | 15 min + 5 min omówienia |

---

## Aktywność 1.1: Nazwij kontrakt

**Cel:** przećwiczyć nazywanie zachowania obserwowalnego, zanim zmiana zostanie zaklasyfikowana jako refaktoryzacja lub nie.

**Czas:** 4 minuty.

### Kontekst

Zachowanie obserwowalne to nie tylko wartość zwracana. Obejmuje również wyjątki, efekty uboczne, protokół (liczbę i kolejność wywołań), format danych, kontrakt publiczny oraz istotne właściwości pozafunkcjonalne.

Zmiany do oceny:

1. Zmiana typu prywatnego pola z `List` na `Set`.
2. Zamiana dwóch zapisów do bazy miejscami.
3. Przeniesienie klasy używanej przez mechanizm refleksji.
4. Zastąpienie algorytmu szybszą implementacją.
5. Zmiana prywatnej nazwy bez użycia refleksji i konfiguracji tekstowej.

### Polecenia

1. Dla każdej zmiany nazwij konsumenta, który może zauważyć różnicę (kod, system, człowiek, narzędzie).
2. Nazwij konkretną obserwację, która mogłaby się zmienić.
3. Wskaż sposób weryfikacji tej obserwacji.
4. Dopiero na końcu oceń, czy zmiana może być wykonana jako refaktoryzacja.

### Kryteria akceptacji

- każda zmiana ma nazwanego konsumenta i obserwację, zanim pojawi się ocena,
- sposób weryfikacji jest konkretny (rodzaj testu lub narzędzia), a nie „przetestujemy”,
- ocena dopuszcza odpowiedź „zależy od kontraktu” z nazwanym warunkiem.

### Oczekiwany produkt

Tabela pięciu zmian z kolumnami: konsument, możliwa obserwacja, weryfikacja, ocena.

---

## Aktywność 1.2: Brakujące pola rekordu

**Cel:** przećwiczenie pełnego opisu elementu długu technicznego i rozpoznanie źródeł brakujących informacji.

**Czas:** 3 minuty.

### Kontekst

Zespół zapisał w rejestrze długu jedynie:

> Brak testów w imporcie, naprawa zajmie trzy dni.

Szablon elementu długu zawiera pola:

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

### Polecenia

1. Ustal, które pola szablonu są (choćby częściowo) wypełnione przez zapis zespołu.
2. Wskaż co najmniej cztery brakujące pola.
3. Dla każdego brakującego pola określ źródło informacji: dane historyczne, wiedza domenowa, eksperyment techniczny czy decyzja właściciela produktu.
4. Nie uzupełniaj nieznanych wartości założeniami przedstawionymi jako fakty.

### Kryteria akceptacji

- co najmniej cztery pola, każde z jednym z czterech typów źródła,
- żadne pole nie zostało „wypełnione” wymyśloną wartością.

### Oczekiwany produkt

Lista co najmniej czterech brakujących pól z przypisanym źródłem informacji.

---

## Aktywność 1.3: Wybór najmniejszego kroku

**Cel:** wybór pierwszego kroku modernizacji, który zwiększa wiedzę lub ogranicza ryzyko, zamiast zatwierdzania kompletnej strategii.

**Czas:** 4 minuty.

### Kontekst

System ma wspieraną platformę jeszcze przez dwa lata, ale krytyczny moduł zmienia się co tydzień, nie ma szybkich testów i powoduje większość regresji. Dostępne informacje nie wystarczają do zatwierdzenia kompletnej strategii modernizacji.

Kandydaci na pierwszy krok:

- pełne przepisanie systemu,
- testy charakteryzujące moduł,
- wymiana platformy.

### Polecenia

1. Porównaj trzy kandydujące kroki: jaki problem rozwiązuje każdy z nich i czy ten problem jest potwierdzony w opisie.
2. Wybierz jeden krok.
3. Zapisz założenie, które dzięki niemu zweryfikujesz.
4. Zapisz miernik wyniku (nie miernik aktywności).
5. Zapisz warunek przerwania.

### Kryteria akceptacji

- wybór jest uzasadniony faktami ze scenariusza,
- miernik opisuje efekt (np. regresje, czas uzyskania informacji zwrotnej), a nie ilość pracy,
- warunek przerwania jest sprawdzalny.

### Oczekiwany produkt

Krótka notatka: wybrany krok, założenie, miernik wyniku, warunek przerwania.

---

## Aktywność 1.4: Fakt czy ocena

**Cel:** odróżnienie faktu od oceny, hipotezy, defektu, symptomu i ograniczenia oraz ćwiczenie języka diagnozy.

**Czas:** 3 minuty.

### Kontekst

Stwierdzenie „ten kod jest zły” nie pozwala podjąć decyzji. Przydatna diagnoza zawiera: fakt (co i gdzie zaobserwowano), kontekst (jaka zmiana, proces lub cecha jakości jest istotna), konsekwencję (jaki koszt, opóźnienie lub ryzyko powstaje) i dowód (jakie dane potwierdzają konsekwencję).

Stwierdzenia:

1. Metoda ma 142 linie i złożoność cyklomatyczną równą 24.
2. Metoda jest niemożliwa do utrzymania.
3. Wynik dla zatwierdzonego przykładu różni się od wymaganej kwoty.
4. Producent środowiska wykonawczego zakończy dostarczanie poprawek za sześć miesięcy.

### Polecenia

1. Dla każdego stwierdzenia wskaż, czy opisuje fakt, hipotezę, defekt, symptom czy ograniczenie. Jedno stwierdzenie może należeć do kilku kategorii.
2. Przy każdej klasyfikacji dopisz jedno zdanie uzasadnienia.
3. Przepisz stwierdzenie nr 2 jako diagnozę zawierającą fakt, kontekst, konsekwencję i potrzebny dowód. Wartości, których nie znasz, oznacz jako brakujące.

### Kryteria akceptacji

- każde stwierdzenie ma co najmniej jedną kategorię i uzasadnienie,
- diagnoza nie zawiera ocen estetycznych („brzydki”, „zły”) jako argumentu,
- dowód jest opisany jako dane do zebrania, a nie jako wymyślona liczba.

### Oczekiwany produkt

Tabela 4 stwierdzeń z kategoriami i uzasadnieniem oraz jedna diagnoza w czterech częściach.

---

## Aktywność 1.5: Dobór metryk

**Cel:** dobór metryk do konkretnego pytania diagnostycznego (schemat Cel, Pytanie, Metryka) i rozpoznanie ograniczeń ich interpretacji.

**Czas:** 4 minuty.

### Kontekst

Celem jest zmniejszenie liczby regresji podczas comiesięcznych zmian reguł cenowych. Dostępne wskaźniki:

- globalne pokrycie linii całej aplikacji,
- liczba poprawek po wydaniu przypadająca na zmianę reguł,
- pokrycie gałęzi zmienianych reguł przez testy z istotnymi asercjami,
- liczba klas w repozytorium,
- współzmienność plików zawierających reguły cenowe,
- maksymalna złożoność dowolnej metody w systemie.

### Polecenia

1. Wybierz trzy wskaźniki, które razem pomogą postawić diagnozę.
2. Odrzuć dwa najmniej przydatne.
3. Dla każdego wyboru (zarówno wybranego, jak i odrzuconego) nazwij pytanie, na które wskaźnik pomaga (albo nie pomaga) odpowiedzieć.
4. Dla każdego wybranego wskaźnika podaj co najmniej jedno ograniczenie jego interpretacji.

### Kryteria akceptacji

- wybrane wskaźniki odnoszą się do obszaru reguł cenowych, a nie całego systemu,
- przy każdym wybranym wskaźniku jest nazwane ograniczenie.

### Oczekiwany produkt

Tabela: wskaźnik, decyzja (wybrany / odrzucony), pytanie, ograniczenie.

---

## Ćwiczenie 1: Obserwacja bez pochopnej naprawy

**Cel:** oddzielenie faktów, hipotez i nieznanych elementów zachowania.

**Czas:** 15 minut pracy w parach i 5 minut omówienia.

### Kontekst

Materiał: klasa `LegacyOrderService` w pliku `src/main/java/pl/training/module1/LegacyOrderService.java`.

Klasa udostępnia metodę `placeOrder(Order order, String customerType, boolean express, String destinationCountry)`, która waliduje zamówienie, liczy sumę pozycji z rabatami, koszt wysyłki i podatek, zaokrągla wynik, zapisuje go przez `OrderRepository` i wysyła wiadomość przez `MailGateway`. Kod możesz uruchomić przez `Module1Examples` (patrz wstęp).

**Planowana zmiana:** w następnym kwartale system ma obsługiwać Czechy, typ klienta `PARTNER` oraz odbiór osobisty jako trzeci wariant dostawy.

**Ograniczenie:** nie wolno jeszcze proponować wzorca projektowego ani kompletnej refaktoryzacji. Celem jest trafna diagnoza, a nie szybkość generowania rozwiązania.

### Polecenia

1. Przeczytaj kod bez proponowania docelowego projektu.
2. Zapisz co najmniej pięć obserwowalnych faktów.
3. Zapisz co najmniej trzy hipotezy o możliwych problemach.
4. Zapisz pytania domenowe, na które kod nie daje pewnej odpowiedzi.
5. Wskaż element planowanej zmiany, przy którym dany symptom stanie się istotny.
6. Określ, jakie dowody pozwoliłyby potwierdzić lub odrzucić hipotezę.

Arkusz:

| Fragment | Fakt | Hipoteza lub ryzyko | Potrzebny dowód | Planowana zmiana |
| --- | --- | --- | --- | --- |
|  |  |  |  |  |
|  |  |  |  |  |
|  |  |  |  |  |
|  |  |  |  |  |
|  |  |  |  |  |

### Kryteria akceptacji

- co najmniej pięć faktów, każdy możliwy do wskazania w konkretnym fragmencie kodu,
- co najmniej trzy hipotezy wyraźnie oddzielone od faktów,
- lista pytań domenowych,
- dla każdej hipotezy wskazany adekwatny dowód i powiązanie z elementem planowanej zmiany,
- brak propozycji docelowej architektury.

### Oczekiwany produkt

Wypełniony arkusz (co najmniej pięć wierszy) i lista pytań domenowych.

---

## Ćwiczenie 2: Od zapachu do kierunku zmiany

**Cel:** nazwanie zapachów kodu wspólnym słownikiem katalogu, powiązanie ich z planowaną zmianą i rozpoznanie, że kierunek refaktoryzacji to hipoteza, a nie nakaz.

**Czas:** 10 minut pracy w parach i 5 minut omówienia.

### Kontekst

Materiał: `src/main/java/pl/training/module1/LegacyOrderService.java`, `src/main/java/pl/training/module1/SalesCalculations.java` i `src/main/java/pl/training/module1/RiskClassifier.java`. Planowana zmiana jest taka sama jak w Ćwiczeniu 1 (Czechy, `PARTNER`, odbiór osobisty).

Katalog zapachów ze slajdów 3.3-3.4 ma pięć grup: Bloaters, Object-Orientation Abusers, Change Preventers, Dispensables, Couplers. Tabela ze slajdu 3.6 przypisuje zapachom możliwe kierunki refaktoryzacji i wzorce docelowe (według Kerievsky'ego).

### Polecenia

1. Wskaż co najmniej pięć zapachów w trzech klasach. Każdy nazwij nazwą z katalogu i przypisz do grupy.
2. Dla każdego zapachu podaj dowód w kodzie (konkretny fragment) i zmianę, którą utrudnia. Jeśli żadna planowana zmiana go nie dotyka, napisz to wprost.
3. Dla każdego zapachu wybierz z tabeli 3.6 jeden możliwy kierunek refaktoryzacji.
4. Zdecyduj dla każdego zapachu: **teraz** (przed planowaną zmianą), **przy planowanej zmianie** albo **nie** (koszt przewyższa korzyść). Uzasadnij jednym zdaniem.
5. Wskaż co najmniej jedno miejsce, w którym metryka lub wygląd kodu sugeruje zapach, ale po analizie decyzja brzmi „nie”.

Arkusz:

| Klasa / fragment | Zapach (grupa) | Dowód w kodzie | Utrudniona zmiana | Możliwy kierunek | Decyzja i uzasadnienie |
| --- | --- | --- | --- | --- | --- |
|  |  |  |  |  |  |
|  |  |  |  |  |  |
|  |  |  |  |  |  |
|  |  |  |  |  |  |
|  |  |  |  |  |  |

### Kryteria akceptacji

- każdy zapach ma nazwę z katalogu, grupę i dowód w konkretnym fragmencie kodu,
- kierunek refaktoryzacji jest powiązany z utrudnioną zmianą, a nie z samą obecnością zapachu,
- co najmniej jedna decyzja brzmi „nie” albo „przy planowanej zmianie” z uzasadnieniem,
- brak kodu docelowego: arkusz opisuje kierunek, nie implementację.

### Oczekiwany produkt

Wypełniony arkusz co najmniej pięciu zapachów i jedno zdanie o zapachu, którego świadomie nie usuwacie.

---

## Ćwiczenie 3: Klasyfikacja i priorytetyzacja długu

**Cel:** wykazanie, że najgorszy wynik metryki nie musi oznaczać najwyższego priorytetu.

**Czas:** 20 minut pracy i 10 minut omówienia.

### Kontekst

Fikcyjny projekt napisany w Javie. Wszystkie wyniki pochodzą z tej samej wersji kodu i konfiguracji narzędzi.

| Kandydat | Dane |
| --- | --- |
| `PricingService` | ta sama reguła VIP występuje w trzech metodach, a podczas dwóch wdrożeń pominięto jedną kopię; złożoność maksymalna metody: 31; duplikacja: 22 procent; siedem zmian w ciągu pół roku; trzy regresje; pięć podobnych zmian w planie rozwoju produktu; oszacowanie interwencji: od 3 do 5 osobodni |
| `AnnualReportFormatter` | złożoność maksymalna metody: 54; brak zmian od 22 miesięcy; komponent ma zostać wycofany za cztery miesiące; oszacowanie interwencji: od 2 do 4 osobodni |
| `OldPaymentAdapter` | niski poziom złożoności i brak duplikacji; dostawca zakończy wsparcie zależności za sześć miesięcy; nowa metoda płatności korzysta z niezgodnego interfejsu; nie są znane aktywne podatności; oszacowanie migracji: od 4 do 7 osobodni |
| `CustomerExportController` | komentarz `TODO` opisuje eksport, który nigdy nie został zatwierdzony jako wymaganie; brak incydentów i planowanych zmian |
| `TaxRoundingRule` | zatwierdzone wymaganie nakazuje zaokrąglanie `HALF_UP`, implementacja używa `DOWN`, a nieprawidłową kwotę odtworzono dla faktury produkcyjnej; brak danych o przyczynie strukturalnej |

### Polecenia

1. Dla każdej pozycji wypełnij osobne kolumny: fakt, symptom, potencjalny element długu, defekt, ograniczenie środowiskowe i poziom pewności. Kategorie mogą współwystępować.
2. Dla co najmniej jednego elementu długu użyj pełnego szablonu opisu elementu długu (tabela pól z Aktywności 1.2).
3. Dysponujesz budżetem sześciu osobodni. Zaproponuj działania i uzasadnij, czego świadomie nie wykonasz.
4. Wskaż informacje, których brakuje do podjęcia decyzji.
5. Powtórz ocenę przy nowym założeniu: `AnnualReportFormatter` pozostanie aktywny przez kolejne dwa lata i będzie zmieniany co miesiąc.

Arkusz klasyfikacji:

| Kandydat | Fakt | Symptom | Potencjalny dług | Defekt | Ograniczenie | Pewność |
| --- | --- | --- | --- | --- | --- | --- |
| `PricingService` |  |  |  |  |  |  |
| `AnnualReportFormatter` |  |  |  |  |  |  |
| `OldPaymentAdapter` |  |  |  |  |  |  |
| `CustomerExportController` |  |  |  |  |  |  |
| `TaxRoundingRule` |  |  |  |  |  |  |

### Kryteria akceptacji

- suma planowanych nakładów nie przekracza sześciu osobodni,
- plan wskazuje, czego zespół świadomie nie robi, i dlaczego,
- rekord długu nie zawiera wartości wymyślonych; braki są nazwane jako braki,
- ponowna ocena `AnnualReportFormatter` odnosi się do zmienionej ekspozycji.

### Oczekiwany produkt

Arkusz klasyfikacji, co najmniej jeden pełny rekord długu, plan wykorzystania sześciu osobodni z listą świadomych rezygnacji, lista brakujących informacji, ponowna ocena po zmianie założenia.

---

## Ćwiczenie 4: Wybór strategii modernizacji

**Cel:** przygotowanie decyzji wykraczającej poza prosty wybór między refaktoryzacją a pełnym przepisaniem.

**Czas:** 15 minut w grupach liczących od trzech do czterech osób (10 minut analiza, 5 minut zapis decyzji) i 5 minut omówienia. To wersja skrócona: opisujecie pięć elementów decyzji, nie pełny plan wdrożenia.

### Kontekst

System `ClaimsCore` obsługuje rozliczanie szkód przez całą dobę. Jest aplikacją napisaną w Javie o rozmiarze około 210 tysięcy linii kodu. Współpracuje z 18 systemami, przechowuje historię kilku milionów spraw i ma wspólną bazę z dwoma procesami raportowymi.

Aktualna platforma ma zagwarantowane wsparcie przez kolejne 30 miesięcy. Proces budowania jest powtarzalny, ale wykonanie pełnego zestawu testów regresyjnych trwa 11 godzin. Pokrycie linii wynosi 62 procent, a gałęzi 28 procent. Dla najczęściej zmienianego modułu reguł uprawnień pokrycie gałęzi wynosi 14 procent. W ostatnim roku ten moduł odpowiadał za większość poprawek po wydaniu.

Dokumentacja opisuje główne procesy, ale wiele wyjątków znajduje się wyłącznie w kodzie i danych. Organizacja ma dostęp do ekspertów domenowych. W ciągu najbliższych dwóch lat planuje regularnie wprowadzać nowe warianty produktów.

Wstępnie oszacowano, że pełne przepisanie potrwa 18 miesięcy. Oszacowanie nie obejmuje migracji danych, odtworzenia wszystkich integracji, równoległego utrzymania ani wyłączenia starego systemu. Podczas tworzenia nowego rozwiązania w obecnym systemie nadal trzeba wprowadzać zmiany wynikające z regulacji.

Moduł reguł uprawnień ma kontrakt wejścia i wyjścia, który można jednoznacznie opisać. Jego wynik można obliczać równolegle w starej i nowej implementacji bez wykorzystywania go do podejmowania decyzji w środowisku produkcyjnym.

**Cel biznesowy:**

> Skrócić czas wdrożenia nowej reguły z 15 do 3 dni i zmniejszyć liczbę poprawek po wydaniu, bez obniżenia dostępności systemu.

### Polecenia

Przygotujcie rekomendację, która zawiera:

1. co najmniej trzy rozważone strategie z jednym zdaniem o koszcie i ryzyku każdej,
2. wybraną strategię i jej związek z celem,
3. pierwszy odwracalny krok możliwy do zakończenia w ciągu czterech tygodni,
4. warunek przerwania albo zmiany kierunku,
5. mierniki wyniku, nie tylko mierniki aktywności.

Szablon:

```text
Problem:

Rozważone opcje:
1.
2.
3.

Decyzja i uzasadnienie:

Pierwszy odwracalny krok (4 tygodnie):

Warunek przerwania:

Mierniki wyniku:
```

### Kryteria akceptacji

- wszystkie pięć elementów rekomendacji jest obecnych,
- wśród rozważanych opcji jest co najmniej jedna inna niż „refaktoryzacja” i „pełne przepisanie”,
- pierwszy krok mieści się w czterech tygodniach i jest odwracalny,
- mierniki wyniku są powiązane z celem biznesowym (czas wdrożenia reguły, poprawki po wydaniu, dostępność).

### Oczekiwany produkt

Rekord decyzji według szablonu (pół strony) i 2-minutowa prezentacja grupy.

---

## Sprawdzenie wiedzy

1. Czym refaktoryzacja różni się od zmiany funkcjonalnej i od przepisania?
2. Czy zachowanie obserwowalne to tylko wartość zwracana?
3. Dlaczego wiek kodu nie wystarcza do określenia go jako legacy?
4. W jakich momentach pracy refaktoryzacja jest najłatwiejsza do uzasadnienia?
5. Co oznaczają kapitał i odsetki w metaforze długu?
6. Kiedy brak testów można opisać jako element długu technicznego?
7. Czym różni się symptom jakości od defektu?
8. Do której grupy katalogu należy Shotgun Surgery i jaki kierunek refaktoryzacji podpowiada?
9. Dlaczego tabela „zapach -> wzorzec” nie rozstrzyga, czy refaktoryzować?
10. Co mierzy złożoność cyklomatyczna, a czego nie mierzy?
11. Dlaczego 100 procent pokrycia linii nie dowodzi poprawności testów?
12. Dlaczego identyczny fragment kodu nie zawsze powinien zostać uogólniony?
13. Co daje połączenie częstotliwości zmian ze złożonością?
14. Jakie koszty pełnego przepisania są często pomijane?
