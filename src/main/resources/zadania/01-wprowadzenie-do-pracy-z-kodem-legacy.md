# Moduł 1. Wprowadzenie do pracy z kodem legacy - zadania

## Jak pracować z tym materiałem

- Moduł ma charakter diagnostyczny. We wszystkich zadaniach oceniasz kod, dane i scenariusze, ale **nie projektujesz jeszcze docelowego rozwiązania**, chyba że polecenie wprost o to prosi.
- Rozróżniaj konsekwentnie: **fakt** (co widać w kodzie lub danych), **hipotezę** (co może być problemem), **defekt** (niezgodność z wymaganiem), **symptom** (sygnał możliwego problemu projektowego), **ograniczenie** (warunek, którego zespół nie kontroluje).
- Diagnoza powinna mieć cztery części: fakt, kontekst, konsekwencję i dowód.
- Nie uzupełniaj brakujących danych zgadywaniem. Jeżeli czegoś nie wiesz, zapisz to jako niewiadomą i wskaż, skąd można ją pozyskać.
- Krótkie aktywności wykonuj indywidualnie lub w parach w podanym czasie. Ćwiczenia warsztatowe wykonuj w parach lub grupach zgodnie z poleceniem.
- Kod źródłowy modułu znajduje się w katalogu `src/main/java/pl/training/module1/` (odpowiedniki: `csharp/src/Training.Module1/`, `typescript/src/module1/`). Przykłady uruchomisz poleceniem `mvn -q compile && java -cp target/classes pl.training.module1.Module1Examples`.

## Przegląd zadań

| Nr | Nazwa | Forma | Czas |
| --- | --- | --- | --- |
| Aktywność 1.1 | Fakt czy ocena | indywidualnie | 3 min |
| Aktywność 1.2 | Brakujące pola rekordu | indywidualnie lub w parach | 3 min |
| Aktywność 1.3 | Dobór metryk | indywidualnie lub w parach | 4 min |
| Aktywność 1.4 | Wybór najmniejszego kroku | indywidualnie lub w parach | 4 min |
| Ćwiczenie 1 | Obserwacja bez pochopnej naprawy | pary | 20 min |
| Ćwiczenie 2 | Klasyfikacja i priorytetyzacja długu | pary lub grupy | 30 min |
| Ćwiczenie 3 | Wybór strategii modernizacji | grupy 3-4 osoby | 50 min |

---

## Aktywność 1.1. Fakt czy ocena

**Cel:** odróżnienie faktu od oceny, hipotezy, defektu, symptomu i ograniczenia oraz ćwiczenie języka diagnozy.

**Czas:** 3 minuty.

**Kontekst:** stwierdzenie „ten kod jest zły” nie pozwala podjąć decyzji. Przydatna diagnoza zawiera: fakt (co i gdzie zaobserwowano), kontekst (jaka zmiana, proces lub cecha jakości jest istotna), konsekwencję (jaki koszt, opóźnienie lub ryzyko powstaje) i dowód (jakie dane potwierdzają konsekwencję).

**Stwierdzenia:**

1. Metoda ma 142 linie i złożoność cyklomatyczną równą 24.
2. Metoda jest niemożliwa do utrzymania.
3. Wynik dla zatwierdzonego przykładu różni się od wymaganej kwoty.
4. Producent środowiska wykonawczego zakończy dostarczanie poprawek za sześć miesięcy.

**Polecenia:**

1. Dla każdego stwierdzenia wskaż, czy opisuje fakt, hipotezę, defekt, symptom czy ograniczenie. Jedno stwierdzenie może należeć do kilku kategorii.
2. Przy każdej klasyfikacji dopisz jedno zdanie uzasadnienia.
3. Przepisz stwierdzenie nr 2 jako diagnozę zawierającą fakt, kontekst, konsekwencję i potrzebny dowód. Wartości, których nie znasz, oznacz jako brakujące.

**Oczekiwany produkt:** tabela 4 stwierdzeń z kategoriami i uzasadnieniem oraz jedna diagnoza w czterech częściach.

**Kryteria akceptacji:**

- każde stwierdzenie ma co najmniej jedną kategorię i uzasadnienie,
- diagnoza nie zawiera ocen estetycznych („brzydki”, „zły”) jako argumentu,
- dowód jest opisany jako dane do zebrania, a nie jako wymyślona liczba.

---

## Aktywność 1.2. Brakujące pola rekordu

**Cel:** przećwiczenie pełnego opisu elementu długu technicznego i rozpoznanie źródeł brakujących informacji.

**Czas:** 3 minuty.

**Kontekst:** zespół zapisał w rejestrze długu jedynie:

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

**Polecenia:**

1. Ustal, które pola szablonu są (choćby częściowo) wypełnione przez zapis zespołu.
2. Wskaż co najmniej cztery brakujące pola.
3. Dla każdego brakującego pola określ źródło informacji: dane historyczne, wiedza domenowa, eksperyment techniczny czy decyzja właściciela produktu.
4. Nie uzupełniaj nieznanych wartości założeniami przedstawionymi jako fakty.

**Oczekiwany produkt:** lista co najmniej czterech brakujących pól z przypisanym źródłem informacji.

**Kryteria akceptacji:**

- co najmniej cztery pola, każde z jednym z czterech typów źródła,
- żadne pole nie zostało „wypełnione” wymyśloną wartością.

---

## Aktywność 1.3. Dobór metryk

**Cel:** dobór metryk do konkretnego pytania diagnostycznego (schemat Cel, Pytanie, Metryka) i rozpoznanie ograniczeń ich interpretacji.

**Czas:** 4 minuty.

**Kontekst:** celem jest zmniejszenie liczby regresji podczas comiesięcznych zmian reguł cenowych.

**Dostępne wskaźniki:**

- globalne pokrycie linii całej aplikacji,
- liczba poprawek po wydaniu przypadająca na zmianę reguł,
- pokrycie gałęzi zmienianych reguł przez testy z istotnymi asercjami,
- liczba klas w repozytorium,
- współzmienność plików zawierających reguły cenowe,
- maksymalna złożoność dowolnej metody w systemie.

**Polecenia:**

1. Wybierz trzy wskaźniki, które razem pomogą postawić diagnozę.
2. Odrzuć dwa najmniej przydatne.
3. Dla każdego wyboru (zarówno wybranego, jak i odrzuconego) nazwij pytanie, na które wskaźnik pomaga (albo nie pomaga) odpowiedzieć.
4. Dla każdego wybranego wskaźnika podaj co najmniej jedno ograniczenie jego interpretacji.

**Oczekiwany produkt:** tabela: wskaźnik, decyzja (wybrany / odrzucony), pytanie, ograniczenie.

**Kryteria akceptacji:**

- wybrane wskaźniki odnoszą się do obszaru reguł cenowych, a nie całego systemu,
- przy każdym wybranym wskaźniku jest nazwane ograniczenie.

---

## Aktywność 1.4. Wybór najmniejszego kroku

**Cel:** wybór pierwszego kroku modernizacji, który zwiększa wiedzę lub ogranicza ryzyko, zamiast zatwierdzania kompletnej strategii.

**Czas:** 4 minuty.

**Kontekst:** system ma wspieraną platformę jeszcze przez dwa lata, ale krytyczny moduł zmienia się co tydzień, nie ma szybkich testów i powoduje większość regresji. Dostępne informacje nie wystarczają do zatwierdzenia kompletnej strategii modernizacji.

**Kandydaci na pierwszy krok:**

- pełne przepisanie systemu,
- testy charakteryzujące moduł,
- wymiana platformy.

**Polecenia:**

1. Porównaj trzy kandydujące kroki: jaki problem rozwiązuje każdy z nich i czy ten problem jest potwierdzony w opisie.
2. Wybierz jeden krok.
3. Zapisz założenie, które dzięki niemu zweryfikujesz.
4. Zapisz miernik wyniku (nie miernik aktywności).
5. Zapisz warunek przerwania.

**Oczekiwany produkt:** krótka notatka: wybrany krok, założenie, miernik wyniku, warunek przerwania.

**Kryteria akceptacji:**

- wybór jest uzasadniony faktami ze scenariusza,
- miernik opisuje efekt (np. regresje, czas uzyskania informacji zwrotnej), a nie ilość pracy,
- warunek przerwania jest sprawdzalny.

---

## Ćwiczenie 1. Obserwacja bez pochopnej naprawy

**Cel:** oddzielenie faktów, hipotez i nieznanych elementów zachowania.

**Czas:** 20 minut.

**Materiał:** klasa `LegacyOrderService`:

- `src/main/java/pl/training/module1/LegacyOrderService.java`
- odpowiedniki: `csharp/src/Training.Module1/LegacyOrderService.cs`, `typescript/src/module1/LegacyOrderService.ts`

Klasa udostępnia metodę `placeOrder(Order order, String customerType, boolean express, String destinationCountry)`, która waliduje zamówienie, liczy sumę pozycji z rabatami, koszt wysyłki i podatek, zaokrągla wynik, zapisuje go przez `OrderRepository` i wysyła wiadomość przez `MailGateway`. Kod możesz uruchomić przez `Module1Examples` (patrz wstęp).

**Planowana zmiana:** w następnym kwartale system ma obsługiwać Czechy, typ klienta `PARTNER` oraz odbiór osobisty jako trzeci wariant dostawy.

**Polecenia:**

1. Przeczytaj kod bez proponowania docelowego projektu.
2. Zapisz co najmniej pięć obserwowalnych faktów.
3. Zapisz co najmniej trzy hipotezy o możliwych problemach.
4. Zapisz pytania domenowe, na które kod nie daje pewnej odpowiedzi.
5. Wskaż element planowanej zmiany, przy którym dany symptom stanie się istotny.
6. Określ, jakie dowody pozwoliłyby potwierdzić lub odrzucić hipotezę.

**Arkusz:**

| Fragment | Fakt | Hipoteza lub ryzyko | Potrzebny dowód | Planowana zmiana |
| --- | --- | --- | --- | --- |
|  |  |  |  |  |
|  |  |  |  |  |
|  |  |  |  |  |
|  |  |  |  |  |
|  |  |  |  |  |

**Ograniczenie:** nie wolno jeszcze proponować wzorca projektowego ani kompletnej refaktoryzacji. Celem jest trafna diagnoza, a nie szybkość generowania rozwiązania.

**Kryteria akceptacji:**

- co najmniej pięć faktów, każdy możliwy do wskazania w konkretnym fragmencie kodu,
- co najmniej trzy hipotezy wyraźnie oddzielone od faktów,
- lista pytań domenowych,
- dla każdej hipotezy wskazany adekwatny dowód i powiązanie z elementem planowanej zmiany,
- brak propozycji docelowej architektury.

---

## Ćwiczenie 2. Klasyfikacja i priorytetyzacja długu

**Cel:** wykazanie, że najgorszy wynik metryki nie musi oznaczać najwyższego priorytetu.

**Czas:** 30 minut.

**Kontekst:** fikcyjny projekt napisany w Javie. Wszystkie wyniki pochodzą z tej samej wersji kodu i konfiguracji narzędzi.

| Kandydat | Dane |
| --- | --- |
| `PricingService` | ta sama reguła VIP występuje w trzech metodach, a podczas dwóch wdrożeń pominięto jedną kopię; złożoność maksymalna metody: 31; duplikacja: 22 procent; siedem zmian w ciągu pół roku; trzy regresje; pięć podobnych zmian w planie rozwoju produktu; oszacowanie interwencji: od 3 do 5 osobodni |
| `AnnualReportFormatter` | złożoność maksymalna metody: 54; brak zmian od 22 miesięcy; komponent ma zostać wycofany za cztery miesiące; oszacowanie interwencji: od 2 do 4 osobodni |
| `OldPaymentAdapter` | niski poziom złożoności i brak duplikacji; dostawca zakończy wsparcie zależności za sześć miesięcy; nowa metoda płatności korzysta z niezgodnego interfejsu; nie są znane aktywne podatności; oszacowanie migracji: od 4 do 7 osobodni |
| `CustomerExportController` | komentarz `TODO` opisuje eksport, który nigdy nie został zatwierdzony jako wymaganie; brak incydentów i planowanych zmian |
| `TaxRoundingRule` | zatwierdzone wymaganie nakazuje zaokrąglanie `HALF_UP`, implementacja używa `DOWN`, a nieprawidłową kwotę odtworzono dla faktury produkcyjnej; brak danych o przyczynie strukturalnej |

**Polecenia:**

1. Dla każdej pozycji wypełnij osobne kolumny: fakt, symptom, potencjalny element długu, defekt, ograniczenie środowiskowe i poziom pewności. Kategorie mogą współwystępować.
2. Dla elementów długu użyj pełnego szablonu opisu elementu długu (tabela pól z Aktywności 1.2): lokalizacja, konstrukcja, przyczyna, konsekwencja, dowód, zaobserwowane odsetki, ekspozycja, kapitał bieżący, ryzyko spłaty, decyzja, poziom pewności, właściciel i termin przeglądu.
3. Dysponujesz budżetem sześciu osobodni. Zaproponuj działania i uzasadnij, czego świadomie nie wykonasz.
4. Wskaż informacje, których brakuje do podjęcia decyzji.
5. Dla celu „zmniejszyć liczbę regresji w wycenie” wybierz trzy przydatne metryki diagnostyczne, odrzuć dwie inne i opisz ograniczenia interpretacji każdej decyzji.
6. Powtórz ocenę przy nowym założeniu: `AnnualReportFormatter` pozostanie aktywny przez kolejne dwa lata i będzie zmieniany co miesiąc.

**Arkusz klasyfikacji:**

| Kandydat | Fakt | Symptom | Potencjalny dług | Defekt | Ograniczenie | Pewność |
| --- | --- | --- | --- | --- | --- | --- |
| `PricingService` |  |  |  |  |  |  |
| `AnnualReportFormatter` |  |  |  |  |  |  |
| `OldPaymentAdapter` |  |  |  |  |  |  |
| `CustomerExportController` |  |  |  |  |  |  |
| `TaxRoundingRule` |  |  |  |  |  |  |

**Oczekiwany produkt:** arkusz klasyfikacji, co najmniej jeden pełny rekord długu, plan wykorzystania sześciu osobodni z listą świadomych rezygnacji, lista brakujących informacji, wybór metryk, ponowna ocena po zmianie założenia.

**Kryteria akceptacji:**

- suma planowanych nakładów nie przekracza sześciu osobodni,
- plan wskazuje, czego zespół świadomie nie robi, i dlaczego,
- rekord długu nie zawiera wartości wymyślonych; braki są nazwane jako braki,
- ponowna ocena `AnnualReportFormatter` odnosi się do zmienionej ekspozycji.

---

## Ćwiczenie 3. Wybór strategii modernizacji

**Cel:** przygotowanie decyzji wykraczającej poza prosty wybór między refaktoryzacją a pełnym przepisaniem.

**Czas:** 50 minut w grupach liczących od trzech do czterech osób: 15 minut analiza, 25 minut przygotowanie jednostronicowej decyzji, 10 minut prezentacja i pytania. Strategię wdrożenia opisz na poziomie koncepcji.

**Scenariusz:**

System `ClaimsCore` obsługuje rozliczanie szkód przez całą dobę. Jest aplikacją napisaną w Javie o rozmiarze około 210 tysięcy linii kodu. Współpracuje z 18 systemami, przechowuje historię kilku milionów spraw i ma wspólną bazę z dwoma procesami raportowymi.

Aktualna platforma ma zagwarantowane wsparcie przez kolejne 30 miesięcy. Proces budowania jest powtarzalny, ale wykonanie pełnego zestawu testów regresyjnych trwa 11 godzin. Pokrycie linii wynosi 62 procent, a gałęzi 28 procent. Dla najczęściej zmienianego modułu reguł uprawnień pokrycie gałęzi wynosi 14 procent. W ostatnim roku ten moduł odpowiadał za większość poprawek po wydaniu.

Dokumentacja opisuje główne procesy, ale wiele wyjątków znajduje się wyłącznie w kodzie i danych. Organizacja ma dostęp do ekspertów domenowych. W ciągu najbliższych dwóch lat planuje regularnie wprowadzać nowe warianty produktów.

Wstępnie oszacowano, że pełne przepisanie potrwa 18 miesięcy. Oszacowanie nie obejmuje migracji danych, odtworzenia wszystkich integracji, równoległego utrzymania ani wyłączenia starego systemu. Podczas tworzenia nowego rozwiązania w obecnym systemie nadal trzeba wprowadzać zmiany wynikające z regulacji.

Moduł reguł uprawnień ma kontrakt wejścia i wyjścia, który można jednoznacznie opisać. Jego wynik można obliczać równolegle w starej i nowej implementacji bez wykorzystywania go do podejmowania decyzji w środowisku produkcyjnym.

**Cel biznesowy:**

> Skrócić czas wdrożenia nowej reguły z 15 do 3 dni i zmniejszyć liczbę poprawek po wydaniu, bez obniżenia dostępności systemu.

**Polecenia - przygotuj rekomendację, która zawiera:**

1. co najmniej trzy rozważone strategie,
2. wybraną strategię i jej związek z celem,
3. najważniejsze założenia oraz niewiadome,
4. pierwszy krok możliwy do zakończenia w ciągu czterech tygodni,
5. sposób weryfikacji zgodności wyników,
6. strategię wdrożenia i wycofania wdrożenia,
7. warunek przerwania albo zmiany kierunku,
8. kryterium usunięcia architektury przejściowej,
9. mierniki wyniku, nie tylko mierniki aktywności.

**Szablon rekordu decyzji:**

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

**Oczekiwany produkt:** jednostronicowy rekord decyzji według szablonu i 10-minutowa prezentacja grupy.

**Kryteria akceptacji:**

- wszystkie dziewięć elementów rekomendacji jest obecnych,
- wśród rozważanych opcji jest co najmniej jedna inna niż „refaktoryzacja” i „pełne przepisanie”,
- pierwszy krok mieści się w czterech tygodniach i jest odwracalny,
- mierniki wyniku są powiązane z celem biznesowym (czas wdrożenia reguły, poprawki po wydaniu, dostępność).

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
