# Moduł 2. Prewencja - zadania dla uczestników

## Jak pracować

- Zadania dotyczą kodu z pakietu `pl.training.module2` (Java). Odpowiedniki znajdziesz w `csharp/src/Training.Module2` oraz `typescript/src/module2` (te same nazwy klas, zapisane zgodnie z konwencjami języka).
- Przed rozpoczęciem uruchom pełny build i upewnij się, że jest zielony:

  ```shell
  mvn clean verify
  ```

- Testy samego modułu: `mvn test -Dtest='pl.training.module2.**'`. Program demonstracyjny: `mvn -q compile && java -cp target/classes pl.training.module2.Module2Examples`.
- Po każdym małym kroku uruchom najszybszy test, który może wykryć błąd tego kroku, a przed zakończeniem ćwiczenia ponownie `mvn verify`.
- Nie zmieniaj jednocześnie zachowania biznesowego i struktury, chyba że zadanie wyraźnie tego wymaga.
- Każdą decyzję uzasadniaj aktualnym wymaganiem, ryzykiem albo kierunkiem zmiany. Sama nazwa zasady (YAGNI, SOLID, Boy Scout) nie jest uzasadnieniem.
- Jeżeli czegoś nie wiesz o zachowaniu kodu, zapisz to jako założenie albo pytanie. Nie zgaduj po cichu.

## Przegląd zadań

| Nr | Nazwa | Forma | Czas |
| --- | --- | --- | --- |
| Aktywność 2.1 | Zmapuj swoje pętle | indywidualnie | 3 min |
| Aktywność 2.2 | Drzwi jedno- czy dwukierunkowe? | indywidualnie lub w parach | 4 min |
| Aktywność 2.3 | Napraw, izoluj czy zaakceptuj? | indywidualnie lub w parach | 4 min |
| Ćwiczenie 1 | Pętle informacji zwrotnej i bramka w legacy | pary | 15 min + 5 min omówienia |
| Ćwiczenie 2 | Decyzje teraz, później czy nigdy | pary lub grupy | 15 min + 10 min omówienia |
| Ćwiczenie 3 | Projekt jako hamulec degradacji | pary | 25 min + 10 min omówienia |
| Ćwiczenie 4 | Boy Scout i seria zmian gotowych do przeglądu | pary | 20 min + 5 min omówienia |

### Pliki będące punktem wyjścia

| Plik | Rola |
| --- | --- |
| `src/main/java/pl/training/module2/tooling/InMemoryJavaCompiler.java` | kompilator w pamięci z jawną polityką ostrzeżeń (Ćwiczenie 1) |
| `src/main/java/pl/training/module2/documentation/DecisionRecord.java` | model ADR i jego wymagane pola (Ćwiczenie 2) |
| `src/main/java/pl/training/module2/legacy/LegacyDeliveryQuoteService.java` | zastany serwis wyceny dostawy (Ćwiczenie 3) |
| `src/test/java/pl/training/module2/legacy/LegacyDeliveryQuoteServiceCharacterizationTest.java` | testy charakterystyki wersji zastanej (Ćwiczenie 3) |
| `src/main/java/pl/training/module2/boyscout/before/ReleaseSummaryFormatter.java` | formater raportu wydania przed poprawą (Ćwiczenie 4) |

---

## Aktywność 2.1: Zmapuj swoje pętle

**Cel:** uświadomić sobie rzeczywisty czas informacji zwrotnej w codziennej pracy.

**Czas:** 3 minuty.

### Kontekst

Slajd 1.1 pokazuje pętle od sekund (kompilator, IDE, szybkie testy) do dni (przegląd kodu, produkcja). Każda pętla wykrywa inne klasy problemów.

### Polecenia

1. Dla projektu, nad którym pracujesz, zapisz czas każdej pętli z tabeli 1.1 - od zapisania pliku do sygnału.
2. Wskaż najwolniejszą pętlę, która najczęściej wykrywa problemy.
3. Podaj jeden konkretny problem z ostatnich tygodni, który dałoby się złapać w szybszej pętli.

### Kryteria akceptacji

- czasy są liczbami (zmierzonymi albo szacowanymi z przykładem), a nie określeniami „długo”, „szybko”,
- problem ma wskazaną pętlę, w której został wykryty, i pętlę, w której mógłby zostać wykryty.

### Oczekiwany produkt

Lista pętli z czasami i jeden przykład przesunięcia wykrywania.

---

## Aktywność 2.2: Drzwi jedno- czy dwukierunkowe?

**Cel:** odróżnić decyzje odwracalne od trudnych do cofnięcia i zobaczyć, co czyni decyzję odwracalną.

**Czas:** 4 minuty.

### Kontekst

Decyzje:

1. zmiana nazwy pakietu `pricing` na `tariffs` w aplikacji bez zewnętrznych klientów,
2. format pliku eksportu odbieranego co noc przez partnera,
3. wybór biblioteki do generowania PDF używanej w jednym adapterze,
4. usunięcie kolumny `discount_percent` z tabeli z historią zamówień,
5. wprowadzenie interfejsu `PricingRule`, „bo kiedyś będą pluginy”,
6. przejście z `double` na `BigDecimal` w publicznym DTO biblioteki.

### Polecenia

1. Sklasyfikuj każdą decyzję jako drzwi dwukierunkowe albo jednokierunkowe.
2. Dla każdej decyzji jednokierunkowej podaj jeden mechanizm, który uczyniłby ją bardziej odwracalną.
3. Wskaż decyzje, które zasługują na ADR.

### Kryteria akceptacji

- klasyfikacja jest uzasadniona konsumentem lub danymi, których dotyczy decyzja,
- mechanizm odwracalności jest konkretny (np. wersjonowanie formatu, okres przejściowy, kopia danych), a nie „ostrożnie”.

### Oczekiwany produkt

Tabela sześciu decyzji: klasyfikacja, uzasadnienie, mechanizm odwracalności, ADR (tak / nie).

---

## Aktywność 2.3: Napraw, izoluj czy zaakceptuj?

**Cel:** przećwiczyć świadomą reakcję na problem, którego nie trzeba albo nie da się od razu usunąć.

**Czas:** 4 minuty.

### Kontekst

Problemy:

1. SDK przewoźnika zwraca kwoty w groszach jako `long` i rzuca własne wyjątki.
2. Moduł raportów rocznych z wysoką złożonością zostanie wycofany za cztery miesiące.
3. Reguła rabatu VIP jest skopiowana w trzech miejscach, a w planie jest pięć zmian rabatów.
4. Kompilator zgłasza 10 ostrzeżeń `auxiliaryclass` w module, którego nikt nie zmienia.

### Polecenia

1. Dla każdego problemu wybierz reakcję: naprawić (spłacić), izolować (kwarantanna za granicą), zaakceptować (tolerować lub obserwować).
2. Dla każdej reakcji „izolować” i „zaakceptować” podaj właściciela i sygnał ponownej oceny.

### Kryteria akceptacji

- każda akceptacja ma właściciela i sprawdzalny sygnał,
- izolacja wskazuje, co dokładnie zostaje za granicą, a co widzi reszta kodu.

### Oczekiwany produkt

Tabela czterech problemów: reakcja, uzasadnienie, właściciel, sygnał ponownej oceny.

---

## Ćwiczenie 1: Pętle informacji zwrotnej i bramka w legacy

**Cel:** przesunąć wykrywanie problemów do szybszych pętli i zaprojektować bramkę, która nie blokuje zespołu historycznymi naruszeniami.

**Czas:** 15 minut pracy w parach (część A około 7 minut, część B około 8 minut) i 5 minut omówienia.

### Kontekst

**Część A.** Zespół utrzymuje system rezerwacji biletów. Zmierzone czasy pętli:

| Pętla | Stan obecny |
| --- | --- |
| kompilacja w IDE | 2 s; pełne `mvn compile` 40 s |
| testy | jeden zestaw, 25 minut, bo każdy test uruchamia pełny kontekst aplikacji z bazą; uruchamiany tylko w CI |
| CI | po scaleniu do gałęzi głównej, 35 minut |
| przegląd kodu | średnio 2 dni oczekiwania, typowy PR ma około 900 linii |
| wydanie | co dwa tygodnie, 3 dni ręcznej regresji |

Problemy z ostatniego miesiąca:

1. literówka w nazwie reguły cenowej w konfiguracji tekstowej, wykryta na produkcji,
2. `ClassCastException` z kolekcji o surowym typie, wykryty podczas ręcznej regresji,
3. błędne zaokrąglenie ceny biletu grupowego, zgłoszone przez klienta,
4. spór w przeglądzie o to, czy pakiet `pricing` może wysyłać maile, rozstrzygany przez trzy dni,
5. regresja w formatowaniu raportu po „drobnym sprzątaniu” w PR z nową funkcją.

**Część B.** Projekt szkoleniowy jest budowany Mavenem z `maven.compiler.release` ustawionym na `25`, bez bramki ostrzeżeń. Narzędzie do eksperymentu: `src/main/java/pl/training/module2/tooling/InMemoryJavaCompiler.java` z `WarningPolicy`.

### Polecenia

Część A:

1. Dla każdego problemu wskaż pętlę, w której został wykryty, i najszybszą pętlę, w której mógłby zostać wykryty.
2. Dla każdego problemu nazwij mechanizm, który to umożliwi (np. typ zamiast napisu, `-Xlint`, szybki test reguły, test reguły architektury, osobny zestaw zmian).
3. Zaproponuj trzy ruchy o największym wpływie na czas informacji zwrotnej. Dla każdego podaj miernik wyniku.

Część B:

1. Sprawdź, jakim JDK uruchamiany jest Maven (`java -version`, `mvn -v`) i porównaj z `maven.compiler.release` w `pom.xml`.
2. Zmierz ostrzeżenia kompilatora bez zmiany kodu: `javac --release 25 -Xlint:all -d <katalog-tymczasowy> $(find src/main/java -name '*.java')`. Zapisz liczbę i kategorie.
3. Uruchom `InMemoryJavaCompilerTest` i odczytaj, jak ten sam surowy typ zachowuje się przy `ALLOW_WARNINGS` i `TREAT_WARNINGS_AS_ERRORS`.
4. Zaproponuj regułę bramki: co blokuje od razu, co trafia do stanu bazowego, kto może zmieniać stan bazowy.

### Kryteria akceptacji

- każdy problem z części A ma wskazaną szybszą pętlę i mechanizm, a nie tylko „więcej testów”,
- trzy ruchy mają mierniki wyniku (czas od commita do sygnału, liczba problemów wykrytych po scaleniu), nie aktywności,
- pomiar z części B jest wykonany bez zmian w kodzie produkcyjnym,
- bramka nie blokuje zespołu historycznymi ostrzeżeniami, a nowe naruszenia nie powiększają stanu bazowego.

### Oczekiwany produkt

Tabela pięciu problemów z pętlami i mechanizmami, trzy ruchy z miernikami, wynik pomiaru ostrzeżeń oraz trzy zdania opisujące regułę bramki i stan bazowy.

---

## Ćwiczenie 2: Decyzje teraz, później czy nigdy

**Cel:** odróżnienie decyzji, które trzeba podjąć teraz, od tych, które warto odłożyć do ostatniego odpowiedzialnego momentu, oraz zapis decyzji trudnej do cofnięcia.

**Czas:** 15 minut pracy i 10 minut omówienia.

### Kontekst

Wszystkie sytuacje dotyczą wyceny dostawy z pakietu `pl.training.module2` (`legacy/LegacyDeliveryQuoteService.java` i jego zrefaktoryzowanej wersji w `domain`, `application`, `adapter`).

1. W politykach STANDARD i EXPRESS występuje stawka opłaty paliwowej 8 procent oraz identyczne zaokrąglenie.
2. W dwóch niezależnych kontekstach `MaximumParcelWeight` i `MaximumWarehouseLoad` wynosi dziś 1000 kg.
3. Zespół proponuje refleksyjny system pluginów, ponieważ za rok mogą pojawić się taryfy definiowane przez partnerów.
4. Produkt ma zatwierdzone wymaganie: za trzy tygodnie zostanie uruchomiona trzecia metoda dostawy z innym algorytmem.
5. Partner logistyczny będzie co noc pobierał plik z ofertami. Format trzeba uzgodnić w tym sprincie, a partner wdroży parser za kwartał i nie planuje częstych zmian.
6. Do oferty w PDF potrzebna jest biblioteka generująca dokumenty; będzie używana w jednym adapterze.

Model ADR w projekcie (`documentation/DecisionRecord.java`) wymaga: identyfikatora `ADR-NNNN`, tytułu, statusu, kontekstu, decyzji, co najmniej jednej rozważonej opcji, co najmniej jednej konsekwencji i metody weryfikacji.

### Polecenia

1. Dla każdej sytuacji zdecyduj: **teraz**, **odłożyć do ostatniego odpowiedzialnego momentu** (podaj, kiedy on wypada), **nie budować** albo **zebrać więcej informacji**.
2. Dla każdej sytuacji zapisz w tabeli:
   - czy to drzwi jedno- czy dwukierunkowe,
   - reprezentowaną wiedzę oraz właściciela i powód zmiany,
   - najprostsze poprawne rozwiązanie na dziś,
   - sygnał, po którym decyzję trzeba ponownie ocenić.
3. Wybierz jedną decyzję jednokierunkową i zapisz ją jako ADR w Markdown według pól modelu `DecisionRecord`, z co najmniej dwiema realnymi opcjami oraz konsekwencjami pozytywnymi i negatywnymi.
4. Odpowiedz: co by się stało z tym ADR, gdyby za pół roku zespół zmienił kierunek?

Szablon tabeli:

| Nr | Decyzja | Drzwi | Wiedza, właściciel, powód zmiany | Najprostsze rozwiązanie na dziś | Sygnał ponownej oceny |
| --- | --- | --- | --- | --- | --- |
| 1 |  |  |  |  |  |
| 2 |  |  |  |  |  |
| 3 |  |  |  |  |  |
| 4 |  |  |  |  |  |
| 5 |  |  |  |  |  |
| 6 |  |  |  |  |  |

### Kryteria akceptacji

- decyzja odwołuje się do wiedzy i właściciela, a nie do liczby podobnych linii,
- przyszłe wymaganie jest odróżnione od zatwierdzonego wymagania,
- dla decyzji odłożonych wskazany jest moment, w którym opcja wygasa,
- ADR zawiera kontekst konkretnej sytuacji, co najmniej dwie opcje i uczciwy koszt wybranej.

### Oczekiwany produkt

Wypełniona tabela sześciu sytuacji oraz jeden ADR w Markdown (pół strony).

---

## Ćwiczenie 3: Projekt jako hamulec degradacji

**Cel:** refaktoryzacja `LegacyDeliveryQuoteService` w taki sposób, żeby kolejne zmiany cennika, zapisu i powiadomień trafiały w osobne, nazwane miejsca, z zachowaniem cen oraz jawnego protokołu efektów.

**Czas:** 25 minut pracy i 10 minut omówienia.

### Kontekst

Punkt wyjścia:

- `src/main/java/pl/training/module2/legacy/LegacyDeliveryQuoteService.java`,
- `src/test/java/pl/training/module2/legacy/LegacyDeliveryQuoteServiceCharacterizationTest.java`,
- modele `Parcel`, `ShippingMethod`, `DeliveryQuote` w `src/main/java/pl/training/module2/domain/`.

```java
BigDecimal price = switch (method) {
    case STANDARD -> {
        BigDecimal base = new BigDecimal("10.00")
                .add(parcel.weightKg().multiply(new BigDecimal("2.00")));
        yield money(base.add(base.multiply(new BigDecimal("0.08"))));
    }
    case EXPRESS -> {
        BigDecimal base = new BigDecimal("20.00")
                .add(parcel.weightKg().multiply(new BigDecimal("3.00")));
        yield money(base.add(base.multiply(new BigDecimal("0.08"))));
    }
};
DeliveryQuote quote = new DeliveryQuote(customerEmail, method, parcel, price);
storedQuotes.add(quote);
System.out.printf("Quote ready for %s: %s costs %s%n", customerEmail, method, price);
return quote;
```

Znane zachowanie (test charakterystyki): przesyłka 3 kg kosztuje `17.28` dla `STANDARD` oraz `31.32` dla `EXPRESS`, a utworzona oferta trafia do listy zapisanych ofert. Testy nie przechwytują treści wypisywanej na konsolę.

Pracuj na kopii klasy zastanej w nowym pakiecie roboczym (np. `pl.training.module2.exercise3`) albo na gałęzi, na której przywrócisz wersję `legacy`. Gotowy podział (`domain`, `application`, `adapter`) jest w projekcie - nie zaglądaj do niego przed końcem ćwiczenia.

### Polecenia

1. Wskaż aktorów zmiany dla wyceny, zapisu i powiadomień.
2. Ustal z właścicielem procesu (w tej roli występuje prowadzący), czy po nieudanym zapisie wolno wysłać powiadomienie, i zapisz decyzję jako wymaganie.
3. Wydziel regułę opłaty paliwowej bez łączenia niezależnych cenników.
4. Wydziel kontrakt polityki cenowej i przenieś jeden wariant. Uruchom testy przed przeniesieniem drugiego wariantu.
5. Dodaj test kontraktowy uruchamiany dla każdej polityki.
6. Wydziel porty zapisu i powiadamiania z perspektywy przypadku użycia; po utworzeniu punktu podmiany dopisz test chroniący uzgodnione zachowanie przy awarii zapisu.
7. Przenieś konkretne efekty (zapis w pamięci, wypisanie na konsolę) do adapterów i złóż rozwiązanie ręcznie poza przypadkiem użycia (composition root).
8. Zaproponuj automatyczną kontrolę, która wykryje import adaptera w pakiecie aplikacji lub domeny (test architektury, skan importów w teście, moduły Maven, JPMS). Porównaj siłę gwarancji i koszt dwóch wariantów, nie implementuj wszystkich.

Po każdym kroku zanotuj, którą oś zmiany chroni konstrukcja oraz jaki koszt dodaje:

| Krok | Co zmieniono | Chroniona oś zmiany | Dodany koszt | Testy uruchomione |
| --- | --- | --- | --- | --- |
|  |  |  |  |  |

### Kryteria akceptacji

- ceny STANDARD i EXPRESS pozostają odpowiednio `17.28` oraz `31.32` dla 3 kg,
- przypadek użycia nie importuje adapterów,
- stawka opłaty paliwowej ma jedną reprezentację,
- każdy wariant przechodzi wspólny test kontraktowy,
- awaria zapisu nie prowadzi do powiadomienia (o ile taka jest uzgodniona decyzja właściciela procesu),
- `mvn verify` kończy się powodzeniem.

### Oczekiwany produkt

Zrefaktoryzowany kod z testami, dziennik kroków oraz porównanie dwóch wariantów automatycznej kontroli granicy.

---

## Ćwiczenie 4: Boy Scout i seria zmian gotowych do przeglądu

**Cel:** podzielić zmianę w kodzie legacy na samodzielne zestawy zmian o czytelnej intencji, w których mała poprawa struktury nie miesza się ze zmianą zachowania.

**Czas:** 20 minut pracy w parach i 5 minut omówienia.

### Kontekst

Zespół dostał zadanie biznesowe: raport z wydania ma dodatkowo pokazywać liczbę nieudanych wdrożeń w ostatnim wierszu (np. `Successful: 1/2, Failed: 1/2`). Osoba, która otworzyła klasę formatera, zauważyła nieczytelne nazwy zmiennych, wielokrotną konkatenację napisów i powielony format wiersza. Pierwsza wersja zmiany zawierała wszystko naraz w jednym commicie: zmianę nazw, przeformatowanie pliku, przebudowę pętli, nową regułę raportu i poprawione testy. Osoba przeglądająca odmówiła akceptacji, bo nie była w stanie ocenić, czy stare zachowanie zostało zachowane.

Dane wejściowe:

- klasa do zmiany: `src/main/java/pl/training/module2/boyscout/before/ReleaseSummaryFormatter.java`,
- model danych wejściowych (nie jest przedmiotem zmiany): `src/main/java/pl/training/module2/boyscout/DeploymentResult.java`, `src/main/java/pl/training/module2/boyscout/DeploymentStatus.java`.

Pracuj na kopii klasy w nowym pakiecie roboczym (np. `pl.training.module2.exercise4`), tak aby oryginał pozostał punktem odniesienia do porównania. Każdy krok zapisuj jako osobny commit na własnej gałęzi roboczej.

### Polecenia

1. **Odkrycie zachowania.** Zapisz obecny kontrakt `format(...)`: postać wyniku, obsługę spacji w `releaseId`, kolejność walidacji, typy wyjątków i komunikaty (w tym dla `null` na liście wyników i dla kombinacji kilku błędnych argumentów).
2. **Testy charakteryzujące.** Przygotuj testy tego kontraktu. Dla każdego zdecyduj, czy utrwala zachowanie zamierzone, czy przypadkowe.
3. **Boy Scout bez zmiany zachowania.** Wykonaj lokalne porządki (nazwy, wydzielenie walidacji i formatowania wiersza, sposób budowania napisu). Nie zmieniaj sygnatury publicznej ani walidacji.
4. **Zmiana zachowania.** W osobnym zestawie zmian zaimplementuj nową regułę ostatniego wiersza wraz z testem opisującym nowe wymaganie.
5. Jeśli formatujesz plik lub używasz automatycznej transformacji IDE, zrób to jako osobny krok mechaniczny.
6. Dla każdego zestawu zmian przygotuj opis odpowiadający na pięć pytań: dlaczego, co zmienia i czego nie zmienia, jaki kontrakt ma pozostać niezmieniony, jakie dowody potwierdzają wynik, jak wdrożyć i wycofać.
7. Wskaż kolejność czytania plików dla osoby przeglądającej.

### Kryteria akceptacji

- każdy zestaw zmian ma jedną główną intencję,
- system działa (kompilacja i testy) po każdym kroku,
- testy nie utrwalają bezkrytycznie przypadkowego zachowania,
- zmiany mechaniczne i ręczne są rozdzielone,
- opis pozwala odtworzyć sposób weryfikacji (konkretne polecenia, a nie „testy przeszły”).

### Oczekiwany produkt

Seria commitów (lub opis serii, jeśli zabraknie czasu na kod) i opisy zestawów zmian według pięciu pytań.

---

## Sprawdzenie wiedzy

1. Dlaczego problem złapany w pętli kompilatora jest tańszy niż ten sam problem złapany w przeglądzie kodu?
2. Dlaczego nagłe włączenie `-Werror` w dużym projekcie legacy jest złym pomysłem i co zrobić zamiast tego?
3. Czym jest ostatni odpowiedzialny moment i czym różni się od odkładania decyzji w nieskończoność?
4. Jak testy i granice zamieniają decyzje jednokierunkowe w dwukierunkowe?
5. Czy YAGNI zabrania pisania testów dla aktualnego zachowania?
6. Kiedy decyzja zasługuje na ADR i co robimy z ADR, gdy zmieniamy kierunek?
7. Czym różni się akceptacja problemu od jego ignorowania?
8. Na czym polega kwarantanna problemu za granicą i do jakiego wzorca prowadzi?
9. Co mówią prawa Lehmana o degradacji jakości i czego nie mówią?
10. Jak teoria rozbitych okien przekłada się na zasady pracy z ostrzeżeniami i testami?
11. Co jest przedmiotem DRY: tekst kodu czy wiedza?
12. Dlaczego `@Autowired` nie dowodzi zastosowania DIP?
13. Czy przepływ sterowania z przypadku użycia do adaptera łamie regułę zależności?
14. Czym różni się Boy Scout Rule od „sprzątania przy okazji” zmieniającego zachowanie?
15. Dlaczego refaktoryzacji nie łączy się ze zmianą zachowania w jednym zestawie zmian?
