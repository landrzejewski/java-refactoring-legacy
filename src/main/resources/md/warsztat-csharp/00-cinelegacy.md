# Warsztat CineLegacy (C#) - praktyczne sceny do modułów 3-8

CineLegacy to wymyślony, stary system kina. Obsługuje seanse, sale, miejsca, bilety, zniżki, rezerwacje, płatności, powiadomienia, raporty i rozliczenia z dystrybutorem. Na jego kodzie pokazujemy na żywo techniki z modułów 3-8. Każdy temat ze slajdów ma swoją krótką **scenę**: kod wyjściowy, kolejne kroki, gotowe snapshoty i testy.

Ten pakiet nie powtarza ćwiczeń z zakładek "Zadania" i "Przykłady" poszczególnych modułów. Uzupełnia je o jak najwięcej małych, uruchamialnych demonstracji na nowym kodzie.

Warsztat ma trzy wersje o tych samych scenach i krokach: [Java](../warsztat/00-cinelegacy.html), C# (ta strona) i [TypeScript](../warsztat-typescript/00-cinelegacy.html). Skrypt prowadzącego wybiera wersję opcją `--lang java|cs|ts`.

Wersja C# ma te same reguły biznesowe i oczekiwane wyniki co wersja Java. Tam, gdzie mechanizm języka jest inny, przewodnik ma w scenie akapit **Różnica względem Javy**.

## Jak zbudowana jest scena

```text
csharp/src/Training.Workshop/
    Shared/Money.cs                 wspólny typ wartości (nie refaktoryzujemy go)
    Legacy/                         stary system: CinemaManager (God Class), LegacyDb, LegacyMailer, LegacyPaymentGateway
    M6/S08State/
        IPayments.cs, Status.cs     stabilne typy kontraktu sceny (opcjonalnie)
        Start/                      kod wyjściowy - na nim pracujemy na żywo
        Step1/ Step2/ ...           gotowe snapshoty po każdym kroku przewodnika
csharp/tests/Training.Workshop.Tests/
    Legacy/CinemaManagerGoldenMasterTest
    Support/Scene                   te same przypadki dla Start i każdego kroku ([Theory] + [MemberData])
    M6/S08State/S08EquivalenceTest ...
```

- Każda scena w przewodniku zaczyna się od sekcji "W skrócie": co robimy, na czym polega zasada lub technika i jaki jest efekt. Można ją przeczytać sali przed pokazem.
- `Start` zawiera zapach albo pułapkę. Prowadzący otwiera go w Rider i wykonuje ruchy opisane w przewodniku.
- `StepN` to kompletny stan kodu po kroku N. Klasy mają te same nazwy co w `Start`, różni się tylko namespace (`...S08State.Step2` zamiast `...S08State.Start`). Ostatni krok jest rozwiązaniem wzorcowym.
- Test równoważności sceny uruchamia **te same przypadki** na `Start` i na każdym kroku. Jeśli po ruchu na żywo test jest zielony, zachowanie się nie zmieniło.
- Sceny, w których zmiana zachowania jest tematem (np. Design by Contract, zmiana kontraktu, nowa reguła biznesowa), mają osobne oczekiwania dla wariantów. Przewodnik mówi to wprost.

## Narzędzie prowadzącego: `scripts/warsztat.sh`

```bash
scripts/warsztat.sh --lang cs list            # wszystkie sceny i ich kroki
scripts/warsztat.sh --lang cs list m6         # sceny jednego modułu
scripts/warsztat.sh --lang cs test m6/s08     # testy jednej sceny (dotnet test)
scripts/warsztat.sh --lang cs test m6         # testy całego modułu
scripts/warsztat.sh --lang cs diff m6/s08 1 2 # co dokładnie zmienia krok 2 (0 = start), w kolorach
scripts/warsztat.sh --lang cs diff m6/s08 1 2 --word  # zmiany podświetlone w obrębie linii
scripts/warsztat.sh --lang cs jump m6/s08 2   # skopiuj step2 do start - przeskok, gdy brakuje czasu
scripts/warsztat.sh --lang cs next            # następny krok do start (ostatnio używana scena)
scripts/warsztat.sh --lang cs prev            # krok wstecz
scripts/warsztat.sh --lang cs status          # który krok jest teraz w start
scripts/warsztat.sh --lang cs reset m6/s08    # przywróć start z repozytorium po pokazie
scripts/warsztat.sh html            # wygeneruj strony HTML z md/warsztat (Node.js)
```

> `reset` przywraca `Start` z gita, więc pliki warsztatu muszą być w repozytorium (co najmniej `git add`). Przed zajęciami: `cd csharp && dotnet test` (wszystko zielone) i `git status` (czysty `Start`).

Opcja `--lang cs` wybiera wersję C#. Skrypt zapamiętuje ostatnio użyty język i scenę, więc `next`, `prev` i `status` działają potem bez argumentów.

Pętla pokazu dla każdego kroku:

1. Pokaż zapach w `start` (sekcja "Co widzimy" w przewodniku).
2. Wykonaj ruch w IDE, najlepiej automatyczną refaktoryzacją ze skrótem z przewodnika.
3. `scripts/warsztat.sh --lang cs test mM/sNN`, czyli zielony test.
4. Jedno zdanie komentarza ("Co powiedzieć").
5. Jeśli coś się rozjedzie albo brakuje czasu: `next` wstawia gotowy następny krok i pokazuje, co się zmieniło. Skrypt pamięta język i ostatnią scenę, więc wystarczy samo `scripts/warsztat.sh next`.

Diff jest kolorowy w terminalu (git diff). Jeśli zainstalujesz `delta` (`brew install git-delta`), skrypt użyje go automatycznie: dostaniesz podświetlanie składni C# i numery linii, co dobrze wygląda na rzutniku.

W Rider wygodnie jest uruchamiać test sceny z gutter (ikona przy klasie `SNNEquivalenceTest`) albo z okna Unit Tests i powtarzać ostatnie uruchomienie skrótem.

## Reguły biznesowe CineLegacy

Wszystkie sceny korzystają z tych samych reguł i liczb, więc wyniki w różnych modułach są spójne.

| Reguła | Wartość |
|---|---|
| Cena bazowa formatu | 2D = 25.00, 3D = 32.00, IMAX = 40.00 |
| Zniżka wg typu biletu | normalny 0%, student 25%, senior 30%, dziecko 40% |
| Seans poranny (start przed 12:00) | -5.00 od ceny biletu po zniżce |
| Miejsce VIP (rząd od progu sali, zwykle 10+) | +10.00 |
| Okulary 3D (seans 3D, klient bez własnych) | +3.00 |
| Opłata rezerwacyjna online | 2.00 za bilet, nie podlega zwrotowi |
| Grupa 10+ biletów | -10% sumy biletów (przed opłatami) |
| Program lojalnościowy | 1 punkt za każde pełne 10.00 za bilety, 100 pkt = darmowy bilet 2D |
| Statusy rezerwacji | NEW → PAID → USED, NEW → EXPIRED (15 min), NEW/PAID → CANCELLED |
| Zwrot | 24h+ przed seansem 100%, mniej 50%, po starcie 0; potrącenie 3.00 |
| Rozliczenie z dystrybutorem | tydzień 1: 50%, tydzień 2: 40%, dalej 35%; minimum 500.00 |
| VAT | bilety 8%, bar 23% |

Filmy w przykładach: "Diuna" (IMAX, wieczór), "Kraina Lodu" (3D, rano), "Amator" (2D).

## Stary system i golden master

`Legacy/CinemaManager` to "serce" starego systemu. Ma kody `int` i `string`, tablice `object?[]` jako rekordy, statyczną "bazę", maile wysyłane z logiki biznesowej, `double` na pieniądze i zagnieżdżone warunki. Z niego wycięto większość scen. Ten sam kod jest też materiałem kampanii God Class (moduł 7) oraz Branch by Abstraction i Strangler Fig (moduł 8).

`CinemaManagerGoldenMasterTest` odtwarza "jeden dzień kina" i porównuje pełny wektor zachowania z plikiem `src/test/resources/workshop/cinema-manager.approved.txt`. Wektor obejmuje wyniki wywołań, maile, SMS-y, obciążenia i zwroty na bramce płatności, raport dzienny i rozliczenia. Warto go pokazać na początku modułu 3 jako punkt odniesienia dla całego warsztatu. Plik zatwierdzonego wyniku jest wspólny dla Javy, C# i TypeScript - wszystkie trzy wersje starego systemu dają identyczny wektor zachowania.

```bash
cd csharp && dotnet test tests/Training.Workshop.Tests --filter "FullyQualifiedName~CinemaManagerGoldenMasterTest"
```

## Materiały

| Moduł | Przewodnik prowadzącego | Zadania dla uczestników | Namespace |
|---|---|---|---|
| 3. Zasady dobrego projektowania | [przewodnik](przewodnik/03-zasady-dobrego-projektowania.html) | [zadania](zadania/03-zasady-dobrego-projektowania.html) | `Training.Workshop.M3` |
| 4. Podstawowe refaktoryzacje | [przewodnik](przewodnik/04-podstawowe-refaktoryzacje.html) | [zadania](zadania/04-podstawowe-refaktoryzacje.html) | `Training.Workshop.M4` |
| 5. Refaktoryzacje hierarchii klas | [przewodnik](przewodnik/05-refaktoryzacje-hierarchii-klas.html) | [zadania](zadania/05-refaktoryzacje-hierarchii-klas.html) | `Training.Workshop.M5` |
| 6. Refaktoryzacje do wzorców projektowych | [przewodnik](przewodnik/06-refaktoryzacje-do-wzorcow-projektowych.html) | [zadania](zadania/06-refaktoryzacje-do-wzorcow-projektowych.html) | `Training.Workshop.M6` |
| 7. Zaawansowane refaktoryzacje | [przewodnik](przewodnik/07-zaawansowane-refaktoryzacje.html) | [zadania](zadania/07-zaawansowane-refaktoryzacje.html) | `Training.Workshop.M7` |
| 8. Strategie i dobre praktyki | [przewodnik](przewodnik/08-strategie-i-dobre-praktyki.html) | [zadania](zadania/08-strategie-i-dobre-praktyki.html) | `Training.Workshop.M8` |

## Macierz pokrycia tematów

Każda scena odsyła do sekcji slajdów swojego modułu. Tematy, które są wyłącznie dyskusyjne (np. kryteria decyzji, jakość w kontekście), omawiamy w przewodnikach przy pytaniach do sali.

### Moduł 3. Zasady dobrego projektowania

| Scena | Tytuł | Temat ze slajdów | Kroki |
|---|---|---|---|
| [m3/s01](przewodnik/03-zasady-dobrego-projektowania.html#scena-s01-dry-jedna-wiedza-dwie-reprezentacje) | DRY - jedna wiedza, dwie reprezentacje | 2.1 DRY dotyczy wiedzy; 2.8 Napięcia między zasadami (nazwanie potwierdzonej reguły) | 4 |
| [m3/s02](przewodnik/03-zasady-dobrego-projektowania.html#scena-s02-podobienstwo-to-nie-duplikacja) | Podobieństwo to nie duplikacja | 2.2 Podobieństwo nie wystarcza; 2.3 Tymczasowe powtórzenie kodu bywa bezpiecznym etapem | 3 |
| [m3/s03](przewodnik/03-zasady-dobrego-projektowania.html#scena-s03-falszywa-abstrakcja-z-flagami) | Fałszywa abstrakcja z flagami | 2.3 Fałszywa abstrakcja: zabezpiecz testami, przenieś kod z powrotem, wydziel tylko potwierdzoną wiedzę | 2 |
| [m3/s04](przewodnik/03-zasady-dobrego-projektowania.html#scena-s04-dry-w-testach-i-niezalezna-wyrocznia) | DRY w testach i niezależna wyrocznia | 2.3 DRY w testach: wspólne fabryki danych tak, liczenie oczekiwanej wartości algorytmem produkcyjnym nie | 2 |
| [m3/s05](przewodnik/03-zasady-dobrego-projektowania.html#scena-s05-kiss-zlozonosc-wprowadzona-kontra-istotna) | KISS - złożoność wprowadzona kontra istotna | 2.4-2.5 KISS: prostota po poprawności; ukryty przepływ przez refleksję | 2 |
| [m3/s06](przewodnik/03-zasady-dobrego-projektowania.html#scena-s06-yagni-silnik-regul-dla-dwoch-regul) | YAGNI - silnik reguł dla dwóch reguł | 2.6-2.7 YAGNI i czego nie zabrania; 2.8 Napięcia (ogólny silnik hipotetycznych taryf); 2.9 Filtr decyzyjny | 3 |
| [m3/s07](przewodnik/03-zasady-dobrego-projektowania.html#scena-s07-srp-raport-dla-dwoch-aktorow) | SRP - raport dla dwóch aktorów | 3.2 SRP: jeden aktor zmiany; 3.7 Błędne uproszczenia SOLID ("klasa robi jedną rzecz") | 3 |
| [m3/s08](przewodnik/03-zasady-dobrego-projektowania.html#scena-s08-ocp-na-wybranej-osi-formaty-seansu) | OCP na wybranej osi - formaty seansu | 3.3 OCP: zamknięcie dla wybranej osi; nie każdy `switch` narusza OCP | 3 |
| [m3/s09](przewodnik/03-zasady-dobrego-projektowania.html#scena-s09-lsp-i-test-kontraktowy) | LSP i test kontraktowy | 3.4 LSP: substytucja behawioralna; `UnsupportedOperationException` nie zawsze łamie LSP - ocena zaczyna się od kontraktu | 2 |
| [m3/s10](przewodnik/03-zasady-dobrego-projektowania.html#scena-s10-isp-z-perspektywy-klienta) | ISP z perspektywy klienta | 3.5 ISP: interfejs według ról klientów; 4.2 Interfejs nie usuwa sprzężenia | 2 |
| [m3/s11](przewodnik/03-zasady-dobrego-projektowania.html#scena-s11-dip-kierunek-zaleznosci-kontra-przeplyw-sterowania) | DIP - kierunek zależności kontra przepływ sterowania | 3.6 DIP i DIP to nie dependency injection; 5.1-5.2 Reguła zależności a przepływ sterowania | 3 |
| [m3/s12](przewodnik/03-zasady-dobrego-projektowania.html#scena-s12-clean-architecture-use-case-dane-na-granicy-composition-root) | Clean Architecture - use case, dane na granicy, composition root | 5.3 Elementy praktyczne; 5.4 Dane na granicy i composition root; 5.5-5.6 Kręgi to nie szablon; 7.8 Protokół efektów | 4 |
| [m3/s13](przewodnik/03-zasady-dobrego-projektowania.html#scena-s13-automatyczna-ochrona-granicy-i-diagnostyka-spojnosci) | Automatyczna ochrona granicy i diagnostyka spójności | 4.4 Diagnostyka spójności i sprzężenia; 4.1-4.3 Spójność, sprzężenie, koszt wydzielenia; lista kontrolna "reguła sprawdzana automatycznie" | 2 |
| [m3/s14](przewodnik/03-zasady-dobrego-projektowania.html#scena-s14-wzorzec-jako-decyzja-odwracalna) | Wzorzec jako decyzja odwracalna | 6.1-6.2 Wzorzec i refaktoryzacja w jego kierunku; 6.3 Strategy i Adapter; 6.4 Wzorzec można usunąć | 3 |
| [m3/s15](przewodnik/03-zasady-dobrego-projektowania.html#scena-s15-inwarianty-w-modelu-domeny) | Inwarianty w modelu domeny | 7.4 Model domeny i DRY (obiekty pilnują inwariantów); 4.3 Dobry moduł ukrywa decyzję | 2 |
| [m3/s16](przewodnik/03-zasady-dobrego-projektowania.html#scena-s16-sprzezenie-protokolu-ukryta-kolejnosc-wywolan) | Sprzężenie protokołu - ukryta kolejność wywołań | 4.4 "Czy wywołania wymagają ukrytej kolejności?" - sprzężenie protokołu lub czasu | 2 |

### Moduł 4. Podstawowe refaktoryzacje

| Scena | Tytuł | Temat ze slajdów | Kroki |
|---|---|---|---|
| [m4/s00](przewodnik/04-podstawowe-refaktoryzacje.html#scena-s00-test-charakterystyki-przed-pierwsza-zmiana) | Test charakterystyki przed pierwszą zmianą | Test charakterystyki przed pierwszą zmianą; Test równoważności etapów; Co ma pozostać niezmienione | 2 |
| [m4/s01](przewodnik/04-podstawowe-refaktoryzacje.html#scena-s01-rename-nazwy-ktore-zyja-poza-c) | Rename - nazwy, które żyją poza C# | Rename - cel i mechanika; Co ma pozostać niezmienione (refleksja, konfiguracja) | 3 |
| [m4/s02](przewodnik/04-podstawowe-refaktoryzacje.html#scena-s02-extract-variable-dla-zlozonego-wyrazenia-ceny) | Extract Variable dla złożonego wyrażenia ceny | Extract Variable i moment ewaluacji | 3 |
| [m4/s03](przewodnik/04-podstawowe-refaktoryzacje.html#scena-s03-stale-z-nazwa-zamiast-magicznych-liczb) | Stałe z nazwą zamiast magicznych liczb | Extract Constant i `final`; Replace Magic Numbers with Named Constants | 3 |
| [m4/s04](przewodnik/04-podstawowe-refaktoryzacje.html#scena-s04-extract-method-przeplyw-danych-i-wiele-wyjsc) | Extract Method - przepływ danych i wiele wyjść | Extract Method - cel i sygnały; Extract Method - analiza przepływu danych; Extract Method - mechanika i trudne przypadki | 3 |
| [m4/s05](przewodnik/04-podstawowe-refaktoryzacje.html#scena-s05-inline-variable-liczba-i-moment-ewaluacji) | Inline Variable - liczba i moment ewaluacji | Inline Variable i typ docelowy; Extract Variable i moment ewaluacji | 3 |
| [m4/s06](przewodnik/04-podstawowe-refaktoryzacje.html#scena-s06-inline-method-i-nadpisanie-w-podklasie) | Inline Method i nadpisanie w podklasie | Inline Method i jego ryzyka | 2 |
| [m4/s07](przewodnik/04-podstawowe-refaktoryzacje.html#scena-s07-move-method-feature-envy-i-pulapka-przeciazenia) | Move Method - Feature Envy i pułapka przeciążenia | Move Method i Move Field - wybór właściciela; Move Method krok po kroku i ryzyka | 2 |
| [m4/s08](przewodnik/04-podstawowe-refaktoryzacje.html#scena-s08-move-field-prog-vip-nalezy-do-sali) | Move Field - próg VIP należy do sali | Move Field krok po kroku i ryzyka; Move Method i Move Field - wybór właściciela | 3 |
| [m4/s09](przewodnik/04-podstawowe-refaktoryzacje.html#scena-s09-extract-class-klient-platnosc-i-klasa-worek) | Extract Class - klient, płatność i klasa-worek | Extract Class - cel, mechanika i zły wynik | 3 |
| [m4/s10](przewodnik/04-podstawowe-refaktoryzacje.html#scena-s10-encapsulate-field-status-ktory-kazdy-moze-nadpisac) | Encapsulate Field - status, który każdy może nadpisać | Encapsulate Field | 3 |
| [m4/s11](przewodnik/04-podstawowe-refaktoryzacje.html#scena-s11-encapsulate-collection-trzy-kontrakty-listy-miejsc) | Encapsulate Collection - trzy kontrakty listy miejsc | Encapsulate Collection; Trzy różne kontrakty kolekcji | 3 |
| [m4/s12](przewodnik/04-podstawowe-refaktoryzacje.html#scena-s12-encapsulate-conditional-czy-przysluguje-zwrot) | Encapsulate Conditional - czy przysługuje zwrot | Encapsulate Conditional | 3 |

### Moduł 5. Refaktoryzacje hierarchii klas

| Scena | Tytuł | Temat ze slajdów | Kroki |
|---|---|---|---|
| [m5/s01](przewodnik/05-refaktoryzacje-hierarchii-klas.html#scena-s01-pull-up-method-najpierw-ujednolicic-ciala) | Pull Up Method - najpierw ujednolicić ciała | 3.1-3.3. Pull Up Method | 3 |
| [m5/s02](przewodnik/05-refaktoryzacje-hierarchii-klas.html#scena-s02-pull-up-field-to-samo-znaczenie-typ-i-cykl-zycia) | Pull Up Field - to samo znaczenie, typ i cykl życia | 3.4-3.6. Pull Up Field i ryzyka Pull Up | 3 |
| [m5/s03](przewodnik/05-refaktoryzacje-hierarchii-klas.html#scena-s03-push-down-methodfield-i-asymetria-przesuniec) | Push Down Method/Field i asymetria przesunięć | 4.1-4.4. Push Down Method, Push Down Field i asymetria przesunięć | 3 |
| [m5/s04](przewodnik/05-refaktoryzacje-hierarchii-klas.html#scena-s04-extract-superclass-seans-i-wynajem-sali) | Extract Superclass - seans i wynajem sali | 5. Extract Superclass | 3 |
| [m5/s05](przewodnik/05-refaktoryzacje-hierarchii-klas.html#scena-s05-extract-subclass-premiera-z-gosciem) | Extract Subclass - premiera z gościem | 6. Extract Subclass | 4 |
| [m5/s06](przewodnik/05-refaktoryzacje-hierarchii-klas.html#scena-s06-extract-interface-rola-koszyka-i-metoda-domyslna) | Extract Interface - rola koszyka i metoda domyślna | 7.1-7.3. Extract Interface; 7.4. Metody domyślne | 3 |
| [m5/s07](przewodnik/05-refaktoryzacje-hierarchii-klas.html#scena-s07-collapse-hierarchy-sala-imax) | Collapse Hierarchy - sala IMAX | 8. Collapse Hierarchy | 3 |
| [m5/s08](przewodnik/05-refaktoryzacje-hierarchii-klas.html#scena-s08-replace-inheritance-with-composition-licznik-klikniec) | Replace Inheritance with Composition - licznik kliknięć | 9.1-9.4. Replace Inheritance with Composition, procedura i pułapki delegowania | 2 |
| [m5/s09](przewodnik/05-refaktoryzacje-hierarchii-klas.html#scena-s09-overriding-a-overloading-pulapka-po-extract-superclass) | Overriding a overloading - pułapka po Extract Superclass | 2.1. Overriding i dynamiczna dyspozycja; 2.2-2.3. Overloading - wybór statyczny | 2 |
| [m5/s10](przewodnik/05-refaktoryzacje-hierarchii-klas.html#scena-s10-ukrywanie-pol-i-metod-static) | Ukrywanie pól i metod static | 2.3. Pola nie są polimorficzne; 2.1. `static` jest ukrywana | 2 |
| [m5/s11](przewodnik/05-refaktoryzacje-hierarchii-klas.html#scena-s11-konstruktor-wolajacy-metode-nadpisywalna) | Konstruktor wołający metodę nadpisywalną | 2.4. Konstruktory i inicjalizacja; 3.1-3.3 (nie wołaj override z konstruktora, żeby umożliwić Pull Up) | 2 |
| [m5/s12](przewodnik/05-refaktoryzacje-hierarchii-klas.html#scena-s12-generyki-i-metody-bridge) | Generyki i metody bridge | 2.5-2.8. Sygnatury po erasure i metody bridge; 10.2-10.3. Refleksja | 2 |
| [m5/s13](przewodnik/05-refaktoryzacje-hierarchii-klas.html#scena-s13-hierarchie-sealed-i-wyczerpujacy-switch) | Hierarchie sealed i wyczerpujący switch | 2.5-2.8. `sealed`, `permits`, `MatchException` | 3 |
| [m5/s14](przewodnik/05-refaktoryzacje-hierarchii-klas.html#scena-s14-wspoldzielenie-implementacji-a-podtypowanie) | Współdzielenie implementacji a podtypowanie | 1.1-1.2. Hierarchia jest częścią zachowania; 9.1. Replace Inheritance with Composition - kiedy? | 2 |
| [m5/s15](przewodnik/05-refaktoryzacje-hierarchii-klas.html#scena-s15-zgodnosc-binarna-refleksja-i-adnotacje) | Zgodność binarna, refleksja i adnotacje | 1.3. Warstwy zgodności; 10.2-10.3. Zgodność binarna, refleksja i adnotacje | 4 |
| [m5/s16](przewodnik/05-refaktoryzacje-hierarchii-klas.html#scena-s16-serializacja-i-proxy-hierarchia-jako-format-danych) | Serializacja i proxy - hierarchia jako format danych | 10.4-10.6. Serializacja, ORM i DI | 3 |

### Moduł 6. Refaktoryzacje do wzorców projektowych

| Scena | Tytuł | Temat ze slajdów | Kroki |
|---|---|---|---|
| [m6/s01](przewodnik/06-refaktoryzacje-do-wzorcow-projektowych.html#scena-s01-replace-conditional-logic-with-strategy-polityka-znizek) | Replace Conditional Logic with Strategy - polityka zniżek | Strategy - przed i po; Strategy - intencja, sekwencja, ryzyka; Istotne mechanizmy Javy 25 | 3 |
| [m6/s02](przewodnik/06-refaktoryzacje-do-wzorcow-projektowych.html#scena-s02-replace-conditional-with-polymorphism-rodzaj-seansu) | Replace Conditional with Polymorphism - rodzaj seansu | Polimorfizm - przed i po; Polimorfizm - kiedy i pułapki | 3 |
| [m6/s03](przewodnik/06-refaktoryzacje-do-wzorcow-projektowych.html#scena-s03-replace-type-code-with-class-format-jako-typ) | Replace Type Code with Class - format jako typ | Replace Type Code with Class; Type Code - decyzje i granica trwałości | 3 |
| [m6/s04](przewodnik/06-refaktoryzacje-do-wzorcow-projektowych.html#scena-s04-encapsulate-classes-with-factory-bilety) | Encapsulate Classes with Factory - bilety | Factory - publiczna granica tworzenia; Factory - intencja, procedura, pułapki | 3 |
| [m6/s05](przewodnik/06-refaktoryzacje-do-wzorcow-projektowych.html#scena-s05-extract-factory-class-tworzenie-rezerwacji) | Extract Factory Class - tworzenie rezerwacji | Factory - intencja, procedura, pułapki (Extract Factory Class) | 3 |
| [m6/s06](przewodnik/06-refaktoryzacje-do-wzorcow-projektowych.html#scena-s06-encapsulate-composite-with-builder-repertuar-dnia) | Encapsulate Composite with Builder - repertuar dnia | Encapsulate Composite with Builder | 3 |
| [m6/s07](przewodnik/06-refaktoryzacje-do-wzorcow-projektowych.html#scena-s07-move-embellishment-to-decorator-dodatki-do-biletu) | Move Embellishment to Decorator - dodatki do biletu | Move Embellishment to Decorator; Decorator - wyjątki, migracja, przezroczystość | 3 |
| [m6/s08](przewodnik/06-refaktoryzacje-do-wzorcow-projektowych.html#scena-s08-replace-state-altering-conditionals-with-state-status-rezerwacji) | Replace State-Altering Conditionals with State - status rezerwacji | Replace State-Altering Conditionals with State; State - kontekst delegujący | 3 |
| [m6/s09](przewodnik/06-refaktoryzacje-do-wzorcow-projektowych.html#scena-s09-replace-hard-coded-notifications-with-observer-po-oplaceniu) | Replace Hard-coded Notifications with Observer - po opłaceniu | Replace Hard-coded Notifications with Observer; Observer - kontrakt i migracja | 3 |
| [m6/s10](przewodnik/06-refaktoryzacje-do-wzorcow-projektowych.html#scena-s10-replace-implicit-tree-with-composite-zestawy-baru) | Replace Implicit Tree with Composite - zestawy baru | Replace Implicit Tree with Composite; Implicit Tree - mapper, procedura, ryzyka | 3 |
| [m6/s11](przewodnik/06-refaktoryzacje-do-wzorcow-projektowych.html#scena-s11-transparent-vs-safe-composite-add-na-lisciu) | Transparent vs Safe Composite - add() na liściu | Replace One/Many Distinctions with Composite (Safe i Transparent Composite) | 2 |
| [m6/s12](przewodnik/06-refaktoryzacje-do-wzorcow-projektowych.html#scena-s12-replace-onemany-distinctions-with-composite-zwroty) | Replace One/Many Distinctions with Composite - zwroty | Replace One/Many Distinctions with Composite | 3 |
| [m6/s13](przewodnik/06-refaktoryzacje-do-wzorcow-projektowych.html#scena-s13-extract-composite-kontenery-programu) | Extract Composite - kontenery programu | Extract Composite | 2 |
| [m6/s14](przewodnik/06-refaktoryzacje-do-wzorcow-projektowych.html#scena-s14-unify-interfaces-with-adapter-dwie-bramki-platnosci) | Unify Interfaces with Adapter - dwie bramki płatności | Unify Interfaces with Adapter; Adapter - co naprawdę trzeba przetłumaczyć | 3 |
| [m6/s15](przewodnik/06-refaktoryzacje-do-wzorcow-projektowych.html#scena-s15-replace-conditional-dispatcher-with-command-konsola-kasjera) | Replace Conditional Dispatcher with Command - konsola kasjera | Replace Conditional Dispatcher with Command; Command - sekwencja i ograniczenia | 3 |
| [m6/s16](przewodnik/06-refaktoryzacje-do-wzorcow-projektowych.html#scena-s16-form-template-method-raporty-csv-i-html) | Form Template Method - raporty CSV i HTML | Apply Template Method; Template Method - sekwencja i ryzyka | 2 |
| [m6/s17](przewodnik/06-refaktoryzacje-do-wzorcow-projektowych.html#scena-s17-limit-instantiation-with-singleton-cennik) | Limit Instantiation with Singleton - cennik | Limit Instantiation with Singleton | 3 |
| [m6/s18](przewodnik/06-refaktoryzacje-do-wzorcow-projektowych.html#scena-s18-move-accumulation-to-collecting-parameter-ostrzezenia-walidacji) | Move Accumulation to Collecting Parameter - ostrzeżenia walidacji | Collecting Parameter | 3 |
| [m6/s19](przewodnik/06-refaktoryzacje-do-wzorcow-projektowych.html#scena-s19-visitor-i-macierz-zmian-pozycje-zamowienia) | Visitor i macierz zmian - pozycje zamówienia | Visitor i macierz zmian | 3 |
| [m6/s20](przewodnik/06-refaktoryzacje-do-wzorcow-projektowych.html#scena-s20-mapa-decyzji-jedna-tabela-dwie-struktury-docelowe) | Mapa decyzji - jedna tabela, dwie struktury docelowe | Najpierw rodzaj zmienności; Mapa decyzji (1/2 i 2/2); Lista kontrolna | 3 |

### Moduł 7. Zaawansowane refaktoryzacje

| Scena | Tytuł | Temat ze slajdów | Kroki |
|---|---|---|---|
| [m7/s01](przewodnik/07-zaawansowane-refaktoryzacje.html#scena-s01-break-dependencies-seam-dla-zadania-przypomnien) | Break Dependencies - seam dla zadania przypomnień | 1. Break Dependencies - intencja i ryzyka; Break Dependencies - przed i po; Java 25 w tym module (interfejs funkcyjny jako seam) | 4 |
| [m7/s02](przewodnik/07-zaawansowane-refaktoryzacje.html#scena-s02-extract-method-object-wycena-zamowienia-grupowego) | Extract Method Object - wycena zamówienia grupowego | 2. Extract Method Object - przed; Extract Method Object - po, sekwencja i ryzyka | 3 |
| [m7/s03](przewodnik/07-zaawansowane-refaktoryzacje.html#scena-s03-break-responsibilities-walidacja-wycena-powiadomienie) | Break Responsibilities - walidacja, wycena, powiadomienie | 3. Break Responsibilities - intencja i ryzyka; Break Responsibilities - po zmianie | 3 |
| [m7/s04](przewodnik/07-zaawansowane-refaktoryzacje.html#scena-s04-remove-duplication-rabat-grupowy-w-kasie-i-w-sklepie) | Remove Duplication - rabat grupowy w kasie i w sklepie | 4. Remove Duplication - duplikacja wiedzy; Remove Duplication - po zmianie | 3 |
| [m7/s05](przewodnik/07-zaawansowane-refaktoryzacje.html#scena-s05-break-method-repertuar-dnia) | Break Method - repertuar dnia | 5. Break Method - intencja; Break Method - bezpieczna sekwencja | 3 |
| [m7/s06](przewodnik/07-zaawansowane-refaktoryzacje.html#scena-s06-introduce-parameter-object-termin-seansu) | Introduce Parameter Object - termin seansu | 6. Introduce Parameter Object - data clump jako pojęcie; Parameter Object - walidacja, migracja i ryzyka | 3 |
| [m7/s07](przewodnik/07-zaawansowane-refaktoryzacje.html#scena-s07-remove-arrowhead-antipattern-bramka-rezerwacji) | Remove Arrowhead Antipattern - bramka rezerwacji | 7. Remove Arrowhead Antipattern; Remove Arrowhead - po zmianie | 3 |
| [m7/s08](przewodnik/07-zaawansowane-refaktoryzacje.html#scena-s08-introduce-design-by-contract-checks-pula-miejsc) | Introduce Design by Contract Checks - pula miejsc | 8. Introduce Design by Contract Checks; Design by Contract - jawne kontrole | 2 |
| [m7/s09](przewodnik/07-zaawansowane-refaktoryzacje.html#scena-s09-remove-double-negative-wstep-do-strefy-vip) | Remove Double Negative - wstęp do strefy VIP | 9. Remove Double Negative | 3 |
| [m7/s10](przewodnik/07-zaawansowane-refaktoryzacje.html#scena-s10-remove-boolean-method-parameters-migracja-api) | Remove Boolean Method Parameters - migracja API | 11. Remove Boolean Method Parameters; Java 25 w tym module (zgodność binarna) | 4 |
| [m7/s11](przewodnik/07-zaawansowane-refaktoryzacje.html#scena-s11-remove-middle-man-fasada-kina) | Remove Middle Man - fasada kina | 12. Remove Middle Man | 3 |
| [m7/s12](przewodnik/07-zaawansowane-refaktoryzacje.html#scena-s12-return-asap-wyszukiwanie-wolnego-miejsca) | Return ASAP - wyszukiwanie wolnego miejsca | 13. Return ASAP | 2 |
| [m7/s13](przewodnik/07-zaawansowane-refaktoryzacje.html#scena-s13-remove-god-class-kampania-na-kopii-cinemamanager) | Remove God Class - kampania na kopii CinemaManager | 10. Remove God Classes - kampania, nie pojedynczy ruch; Remove God Classes - wydzielony fragment i ryzyka; Warsztat 3 (kontekst) | 4 |
| [m7/s14](przewodnik/07-zaawansowane-refaktoryzacje.html#scena-s14-refaktoryzacja-a-zmiana-kontraktu-zwrot-na-bigdecimal) | Refaktoryzacja a zmiana kontraktu - zwrot na BigDecimal | Refaktoryzacja a zmiana kontraktu; Pętla pracy (zmiany kontraktu w osobnych krokach) | 3 |
| [m7/s15](przewodnik/07-zaawansowane-refaktoryzacje.html#scena-s15-wektor-obserwowalnego-zachowania-platnosc-za-bilety) | Wektor obserwowalnego zachowania - płatność za bilety | Wektor obserwowalnego zachowania; Lista kontrolna przeglądu (zachowanie) | 3 |

### Moduł 8. Strategie i dobre praktyki

| Scena | Tytuł | Temat ze slajdów | Kroki |
|---|---|---|---|
| [m8/s01](przewodnik/08-strategie-i-dobre-praktyki.html#scena-s01-branch-by-abstraction-na-cenniku-z-cinemamanager) | Branch by Abstraction na cenniku z CinemaManager | 1.3-1.4 Branch by Abstraction, Strangler Fig, architektura przejściowa | 4 |
| [m8/s02](przewodnik/08-strategie-i-dobre-praktyki.html#scena-s02-strangler-fig-fasada-ktora-przejmuje-sciezki) | Strangler Fig - fasada, która przejmuje ścieżki | 1.3-1.4 Branch by Abstraction, Strangler Fig, architektura przejściowa | 4 |
| [m8/s03](przewodnik/08-strategie-i-dobre-praktyki.html#scena-s03-rownolegla-weryfikacja-shadow-kalkulatora-cen) | Równoległa weryfikacja (shadow) kalkulatora cen | 1.5 Przykład: równoległa weryfikacja kalkulatora | 4 |
| [m8/s04](przewodnik/08-strategie-i-dobre-praktyki.html#scena-s04-granice-trybu-shadow-efekty-uboczne) | Granice trybu shadow - efekty uboczne | 1.6-1.7 Granice trybu shadow i antywzorce migracji | 3 |
| [m8/s05](przewodnik/08-strategie-i-dobre-praktyki.html#scena-s05-boy-scout-rule-mala-poprawa-i-jej-naduzycie) | Boy Scout Rule - mała poprawa i jej nadużycie | 2.1-2.4 Heurystyka, nie mandat; bezpieczna sekwencja i typowe nadużycia | 2 |
| [m8/s06](przewodnik/08-strategie-i-dobre-praktyki.html#scena-s06-seria-malych-zmian-gotowych-do-przegladu) | Seria małych zmian gotowych do przeglądu | 3.2 Małe zestawy zmian - czego nie łączyć; 3.3-3.4 Przegląd kodu | 3 |
| [m8/s07](przewodnik/08-strategie-i-dobre-praktyki.html#scena-s07-adr-i-wykonywalny-model-decyzji) | ADR i wykonywalny model decyzji | 4.3-4.4 Architecture Decision Record | 2 |
| [m8/s08](przewodnik/08-strategie-i-dobre-praktyki.html#scena-s08-bramka-kompilatora-nullable-i-ostrzezenia-jako-bledy) | Bramka kompilatora - nullable i ostrzeżenia jako błędy | 5.2-5.3 Refaktoryzacje IDE i kompilator Javy 25; 5.6 Bramka kompilatora | 3 |
| [m8/s09](przewodnik/08-strategie-i-dobre-praktyki.html#scena-s09-codemod-na-api-kompilatora-roslyn) | Codemod na API kompilatora Roslyn | 5.4-5.5 Analiza statyczna, formatowanie i automatyzacja (receptury) | 3 |
| [m8/s10](przewodnik/08-strategie-i-dobre-praktyki.html#scena-s10-minimalna-bramka-jakosci-jako-kod) | Minimalna bramka jakości jako kod | 5.6-5.7 Bramka kompilatora i minimalna bramka jakości | 4 |
| [m8/s11](przewodnik/08-strategie-i-dobre-praktyki.html#scena-s11-jawna-polityka-wdrozenia-etapowego) | Jawna polityka wdrożenia etapowego | 6.2 Warstwy kontroli; 6.3-6.4 Kryteria i jawna polityka wdrożenia etapowego | 3 |
| [m8/s12](przewodnik/08-strategie-i-dobre-praktyki.html#scena-s12-expand-and-contract-format-danych-rezerwacji) | Expand and contract - format danych rezerwacji | 6.5-6.6 Dane, wycofanie i zamknięcie migracji | 4 |
| [m8/s13](przewodnik/08-strategie-i-dobre-praktyki.html#scena-s13-dokumentacja-zywa-kontra-historyczna) | Dokumentacja żywa kontra historyczna | 4.1 Dokumentacja według trwałości; 4.5 Dokumentacja żywa i historyczna | 2 |
