# Warsztat CineLegacy (TypeScript) - praktyczne sceny do modułów 2, 3 i 5

CineLegacy to wymyślony, stary system kina. Obsługuje seanse, sale, miejsca, bilety, zniżki, rezerwacje, płatności, powiadomienia, raporty i rozliczenia z dystrybutorem. Na jego kodzie pokazujemy na żywo techniki z modułów 2 (Prewencja), 3 (Testowanie a bezpieczeństwo) i 5 (Refaktoryzacja do wzorców). Każdy temat ze slajdów ma swoją krótką **scenę**: kod wyjściowy, kolejne kroki, gotowe snapshoty i testy.

Ten pakiet nie powtarza ćwiczeń z zakładek "Zadania" i "Przykłady" poszczególnych modułów. Uzupełnia je o jak najwięcej małych, uruchamialnych demonstracji na nowym kodzie.

Warsztat ma trzy wersje o tych samych scenach i krokach: [Java](../warsztat/00-cinelegacy.html), [C#](../warsztat-csharp/00-cinelegacy.html) i TypeScript (ta strona). Skrypt prowadzącego wybiera wersję opcją `--lang java|cs|ts`.

Wersja TypeScript ma te same reguły biznesowe i oczekiwane wyniki co wersja Java. Tam, gdzie mechanizm języka jest inny, przewodnik ma w scenie akapit **Różnica względem Javy**.

## Jak zbudowana jest scena

```text
typescript/src/workshop/
    shared/Money.ts, time.ts        wspólne typy (kwoty, odpowiedniki java.time) - nie refaktoryzujemy ich
    legacy/                         stary system: CinemaManager (God Class), LegacyDb, LegacyMailer, LegacyPaymentGateway
    m5/s08_state/
        Payments.ts, Status.ts      stabilne typy kontraktu sceny (opcjonalnie)
        start/                      kod wyjściowy - na nim pracujemy na żywo
        step1/ step2/ ...           gotowe snapshoty po każdym kroku przewodnika
typescript/test/workshop/
    legacy/CinemaManagerGoldenMasterTest.test.ts
    support/scene.ts                te same przypadki dla start i każdego kroku
    m5/s08_state/S08EquivalenceTest.test.ts ...
```

- Każda scena w przewodniku zaczyna się od sekcji "W skrócie": co robimy, na czym polega zasada lub technika i jaki jest efekt. Można ją przeczytać sali przed pokazem.
- `start` zawiera zapach albo pułapkę. Prowadzący otwiera go w VS Code i wykonuje ruchy opisane w przewodniku.
- `stepN` to kompletny stan kodu po kroku N. Pliki i klasy mają te same nazwy co w `start`, a kroki importują typy sceny ścieżką względną (`../Status.js`), więc krok można skopiować do `start` bez zmian. Ostatni krok jest rozwiązaniem wzorcowym.
- Test równoważności sceny uruchamia **te same przypadki** na `start` i na każdym kroku. Jeśli po ruchu na żywo test jest zielony, zachowanie się nie zmieniło.
- Sceny, w których zmiana zachowania jest tematem (np. Design by Contract, zmiana kontraktu, nowa reguła biznesowa), mają osobne oczekiwania dla wariantów. Przewodnik mówi to wprost.

## Narzędzie prowadzącego: `scripts/warsztat.sh`

```bash
scripts/warsztat.sh --lang ts list            # wszystkie sceny i ich kroki
scripts/warsztat.sh --lang ts list m5         # sceny jednego modułu
scripts/warsztat.sh --lang ts test m5/s08     # testy jednej sceny (vitest)
scripts/warsztat.sh --lang ts test m5         # testy całego modułu
scripts/warsztat.sh --lang ts diff m5/s08 1 2 # co dokładnie zmienia krok 2 (0 = start), w kolorach
scripts/warsztat.sh --lang ts diff m5/s08 1 2 --word  # zmiany podświetlone w obrębie linii
scripts/warsztat.sh --lang ts jump m5/s08 2   # skopiuj step2 do start - przeskok, gdy brakuje czasu
scripts/warsztat.sh --lang ts next            # następny krok do start (ostatnio używana scena)
scripts/warsztat.sh --lang ts prev            # krok wstecz
scripts/warsztat.sh --lang ts status          # który krok jest teraz w start
scripts/warsztat.sh --lang ts reset m5/s08    # przywróć start z repozytorium po pokazie
scripts/warsztat.sh html            # wygeneruj strony HTML wszystkich materiałów (Node.js)
```

> `reset` przywraca `start` z gita, więc pliki warsztatu muszą być w repozytorium (co najmniej `git add`). Przed zajęciami: `cd typescript && npm ci && npm run typecheck && npm test` (wszystko zielone) i `git status` (czysty `start`). Vitest nie sprawdza typów - gdy ruch w scenie opiera się na błędzie kompilacji, uruchom `npm run typecheck`.

Opcja `--lang ts` wybiera wersję TypeScript. Skrypt zapamiętuje ostatnio użyty język i scenę, więc `next`, `prev` i `status` działają potem bez argumentów.

Pętla pokazu dla każdego kroku:

1. Pokaż zapach w `start` (sekcja "Co widzimy" w przewodniku).
2. Wykonaj ruch w IDE, najlepiej automatyczną refaktoryzacją ze skrótem z przewodnika.
3. `scripts/warsztat.sh --lang ts test mM/sNN`, czyli zielony test.
4. Jedno zdanie komentarza ("Co powiedzieć").
5. Jeśli coś się rozjedzie albo brakuje czasu: `next` wstawia gotowy następny krok i pokazuje, co się zmieniło. Skrypt pamięta język i ostatnią scenę, więc wystarczy samo `scripts/warsztat.sh next`.

Diff jest kolorowy w terminalu (git diff). Jeśli zainstalujesz `delta` (`brew install git-delta`), skrypt użyje go automatycznie: dostaniesz podświetlanie składni TypeScript i numery linii, co dobrze wygląda na rzutniku.

W VS Code wygodnie jest uruchamiać test sceny z rozszerzenia Vitest (ikona przy `describe`) albo ze skryptu w terminalu obok edytora.

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

`legacy/CinemaManager` to "serce" starego systemu. Ma kody liczbowe i tekstowe, tablice `unknown[]` jako rekordy, statyczną "bazę", maile wysyłane z logiki biznesowej, `number` na pieniądze i zagnieżdżone warunki. Z niego wycięto większość scen. Ten sam kod jest też materiałem ćwiczenia końcowego (moduł 6. Podsumowanie).

`CinemaManagerGoldenMasterTest` odtwarza "jeden dzień kina" i porównuje pełny wektor zachowania z plikiem `src/test/resources/workshop/cinema-manager.approved.txt`. Wektor obejmuje wyniki wywołań, maile, SMS-y, obciążenia i zwroty na bramce płatności, raport dzienny i rozliczenia. Warto go pokazać w module 3 ("Jak poradzić sobie bez testów?") jako punkt odniesienia dla całego warsztatu. Plik zatwierdzonego wyniku jest wspólny dla Javy, C# i TypeScript - wszystkie trzy wersje starego systemu dają identyczny wektor zachowania.

```bash
cd typescript && npx vitest run test/workshop/legacy
```

## Materiały

| Moduł | Przewodnik prowadzącego | Zadania dla uczestników | Katalog |
|---|---|---|---|
| 2. Prewencja | [przewodnik](przewodnik/02-prewencja.html) | [zadania](zadania/02-prewencja.html) | `workshop/m2` |
| 3. Testowanie a bezpieczeństwo | [przewodnik](przewodnik/03-testowanie-a-bezpieczenstwo.html) | [zadania](zadania/03-testowanie-a-bezpieczenstwo.html) | `workshop/m3` |
| 5. Refaktoryzacja do wzorców | [przewodnik](przewodnik/05-refaktoryzacja-do-wzorcow.html) | [zadania](zadania/05-refaktoryzacja-do-wzorcow.html) | `workshop/m5` |

Moduły 1 (Wprowadzenie), 4 (Wzorce projektowe) i 6 (Podsumowanie) nie mają scen warsztatu: ćwiczenia tych modułów są w zakładkach "Zadania" i "Przykłady".

## Macierz pokrycia tematów

Każda scena odsyła do sekcji slajdów swojego modułu. Tematy, które są wyłącznie dyskusyjne, omawiamy w przewodnikach przy pytaniach do sali.

### Moduł 2. Prewencja

| Scena | Tytuł | Temat ze slajdów | Kroki |
|---|---|---|---|
| [m2/s01](przewodnik/02-prewencja.html#scena-s01-yagni-silnik-regul-dla-dwoch-regul) | YAGNI - silnik reguł dla dwóch reguł | 2.4 YAGNI: aktualna potrzeba przed przewidywaną; 2.5 Napięcia między zasadami (ogólny silnik hipotetycznych taryf); 2.6 Filtr decyzyjny przed dodaniem abstrakcji; 2.1 Ostatni odpowiedzialny moment | 3 |
| [m2/s02](przewodnik/02-prewencja.html#scena-s02-adr-i-wykonywalny-model-decyzji) | ADR i wykonywalny model decyzji | 2.7 Zapis decyzji: ADR; 2.2 Decyzje odwracalne i nieodwracalne | 2 |
| [m2/s03](przewodnik/02-prewencja.html#scena-s03-bramka-kompilatora-opcje-strict-i-ostrzezenia-jako-bledy) | Bramka kompilatora - opcje strict i ostrzeżenia jako błędy | 1.5 Kompilator jako najszybsza bramka; 1.6 Bramki w legacy: stan bazowy i ratchet | 3 |
| [m2/s04](przewodnik/02-prewencja.html#scena-s04-minimalna-bramka-jakosci-jako-kod) | Minimalna bramka jakości jako kod | 1.4 Różne narzędzia, różne dowody; 1.6 Bramki w legacy: stan bazowy i ratchet (minimalna bramka) | 4 |
| [m2/s05](przewodnik/02-prewencja.html#scena-s05-boy-scout-rule-mala-poprawa-i-jej-naduzycie) | Boy Scout Rule - mała poprawa i jej nadużycie | 6.1 Boy Scout Rule: heurystyka, nie mandat; 6.3 Bezpieczna sekwencja i typowe nadużycia | 2 |
| [m2/s06](przewodnik/02-prewencja.html#scena-s06-seria-malych-zmian-gotowych-do-przegladu) | Seria małych zmian gotowych do przeglądu | 6.4 Małe zestawy zmian - czego nie łączyć; 6.5 Przegląd kodu i informacja zwrotna | 3 |
| [m2/s07](przewodnik/02-prewencja.html#scena-s07-srp-raport-dla-dwoch-aktorow) | SRP - raport dla dwóch aktorów | 5.3 SOLID jako narzędzie analizy zmian (SRP: jeden aktor zmiany); 5.4 Błędne uproszczenia SOLID ("klasa robi jedną rzecz") | 3 |
| [m2/s08](przewodnik/02-prewencja.html#scena-s08-dip-kierunek-zaleznosci-kontra-przeplyw-sterowania) | DIP - kierunek zależności kontra przepływ sterowania | 5.3 SOLID jako narzędzie analizy zmian (DIP to nie dependency injection); 5.6 Reguła zależności i composition root | 3 |
| [m2/s09](przewodnik/02-prewencja.html#scena-s09-clean-architecture-use-case-dane-na-granicy-composition-root) | Clean Architecture - use case, dane na granicy, composition root | 5.6 Reguła zależności i composition root; 5.9 Porty i przypadek użycia; 5.10 Kierunek zależności i bilans decyzji | 4 |
| [m2/s10](przewodnik/02-prewencja.html#scena-s10-automatyczna-ochrona-granicy-i-diagnostyka-spojnosci) | Automatyczna ochrona granicy i diagnostyka spójności | 5.5 Spójność i sprzężenie; 5.6 Reguła zależności (sprawdzana automatycznie); lista kontrolna prewencji ("reguła zależności sprawdzana automatycznie") | 2 |

### Moduł 3. Testowanie a bezpieczeństwo

| Scena | Tytuł | Temat ze slajdów | Kroki |
|---|---|---|---|
| [m3/s01](przewodnik/03-testowanie-a-bezpieczenstwo.html#scena-s01-test-charakterystyki-przed-pierwsza-zmiana) | Test charakterystyki przed pierwszą zmianą | 4.2 Testy charakteryzujące; 4.4 Dobór przypadków; 4.8 Porównanie implementacji (równoważność etapów); 4.6 Wektor obserwowalnego zachowania | 2 |
| [m3/s02](przewodnik/03-testowanie-a-bezpieczenstwo.html#scena-s02-break-dependencies-seam-dla-zadania-przypomnien) | Break Dependencies - seam dla zadania przypomnień | 4.10 Seam, punkt aktywacji i cel rozrywania; 4.12 Minimalny szew przejściowy; 4.14 Break Dependencies - przed i po; 4.16 Bezpieczne ruchy automatami IDE, gdy brak testów | 4 |
| [m3/s03](przewodnik/03-testowanie-a-bezpieczenstwo.html#scena-s03-wektor-obserwowalnego-zachowania-platnosc-za-bilety) | Wektor obserwowalnego zachowania - płatność za bilety | 4.6 Wektor obserwowalnego zachowania; 2.2 Weryfikacja stanu a weryfikacja zachowania; 3.4-3.5 Pokrycie: zalety, ograniczenia, progi, nadużycia | 3 |
| [m3/s04](przewodnik/03-testowanie-a-bezpieczenstwo.html#scena-s04-rownolegla-weryfikacja-shadow-kalkulatora-cen) | Równoległa weryfikacja (shadow) kalkulatora cen | 4.18 Równoległa weryfikacja (parallel run) | 4 |
| [m3/s05](przewodnik/03-testowanie-a-bezpieczenstwo.html#scena-s05-granice-trybu-shadow-efekty-uboczne) | Granice trybu shadow - efekty uboczne | 4.19 Granice trybu shadow | 3 |

### Moduł 5. Refaktoryzacja do wzorców

| Scena | Tytuł | Temat ze slajdów | Kroki |
|---|---|---|---|
| [m5/s01](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s01-replace-conditional-logic-with-strategy-polityka-znizek) | Replace Conditional Logic with Strategy - polityka zniżek | 6.1. Kto wybiera wariant i kiedy?; 6.4. Strategy - intencja, sekwencja, ryzyka | 3 |
| [m5/s02](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s02-replace-conditional-with-polymorphism-rodzaj-seansu) | Replace Conditional with Polymorphism - rodzaj seansu | 17.1. Replace Conditional with Polymorphism; 6.8. Typowe pomyłki przy wyborze | 3 |
| [m5/s03](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s03-replace-type-code-with-class-format-jako-typ) | Replace Type Code with Class - format jako typ | 17.2. Replace Type Code with Class | 3 |
| [m5/s04](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s04-encapsulate-classes-with-factory-bilety) | Encapsulate Classes with Factory - bilety | 10.1. Factory - publiczna granica tworzenia; 10.2. Factory - intencja, procedura, pułapki | 3 |
| [m5/s05](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s05-extract-factory-class-tworzenie-rezerwacji) | Extract Factory Class - tworzenie rezerwacji | 10.2. Factory - intencja, procedura, pułapki (Extract Factory Class); 10.3. Fabryka jako strażnik spójności | 3 |
| [m5/s06](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s06-encapsulate-composite-with-builder-repertuar-dnia) | Encapsulate Composite with Builder - repertuar dnia | 13.1. Encapsulate Composite with Builder; 13.2. Builder - kiedy i ryzyka | 3 |
| [m5/s07](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s07-move-embellishment-to-decorator-dodatki-do-biletu) | Move Embellishment to Decorator - dodatki do biletu | 8.1. Move Embellishment to Decorator; 8.2. Decorator - wyjątki, migracja, przezroczystość | 3 |
| [m5/s08](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s08-replace-state-altering-conditionals-with-state-status-rezerwacji) | Replace State-Altering Conditionals with State - status rezerwacji | 6.5. State - tabela przejść przed hierarchią; 6.6. State - kolejność przejścia i efektów | 3 |
| [m5/s09](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s09-replace-hard-coded-notifications-with-observer-po-oplaceniu) | Replace Hard-coded Notifications with Observer - po opłaceniu | 14.1. Replace Hard-coded Notifications with Observer; 14.2. Observer - kontrakt i migracja | 3 |
| [m5/s10](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s10-replace-implicit-tree-with-composite-zestawy-baru) | Replace Implicit Tree with Composite - zestawy baru | 17.3. Replace Implicit Tree with Composite; 5.2. Test równoważności przed i po | 3 |
| [m5/s11](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s11-transparent-vs-safe-composite-add-na-lisciu) | Transparent vs Safe Composite - add() na liściu | 17.3. Replace Implicit Tree with Composite; 17.4. Jeden lub wiele i Extract Composite (Safe i Transparent Composite) | 2 |
| [m5/s12](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s12-replace-onemany-distinctions-with-composite-zwroty) | Replace One/Many Distinctions with Composite - zwroty | 17.4. Jeden lub wiele i Extract Composite (Replace One/Many Distinctions with Composite) | 3 |
| [m5/s13](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s13-extract-composite-kontenery-programu) | Extract Composite - kontenery programu | 17.4. Jeden lub wiele i Extract Composite (Extract Composite) | 2 |
| [m5/s14](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s14-unify-interfaces-with-adapter-dwie-bramki-platnosci) | Unify Interfaces with Adapter - dwie bramki płatności | 9.1. Unify Interfaces with Adapter; 9.2. Adapter - co naprawdę trzeba przetłumaczyć | 3 |
| [m5/s15](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s15-replace-conditional-dispatcher-with-command-konsola-kasjera) | Replace Conditional Dispatcher with Command - konsola kasjera | 6.7. Command - rejestr i ograniczenia | 3 |
| [m5/s16](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s16-form-template-method-raporty-csv-i-html) | Form Template Method - raporty CSV i HTML | 7.1. Apply Template Method; 7.2. Template Method - sekwencja i ryzyka | 2 |
| [m5/s17](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s17-limit-instantiation-with-singleton-cennik) | Limit Instantiation with Singleton - cennik | 17.6. Limit Instantiation with Singleton; 2.4-2.5. Inline Singleton (kierunek odwrotny) | 3 |
| [m5/s18](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s18-move-accumulation-to-collecting-parameter-ostrzezenia-walidacji) | Move Accumulation to Collecting Parameter - ostrzeżenia walidacji | 17.5. Collecting Parameter | 3 |
| [m5/s19](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s19-visitor-i-macierz-zmian-pozycje-zamowienia) | Visitor i macierz zmian - pozycje zamówienia | 16.1. Visitor i macierz zmian; 16.2. Do i od Visitora | 3 |
| [m5/s20](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s20-mapa-decyzji-jedna-tabela-dwie-struktury-docelowe) | Mapa decyzji - jedna tabela, dwie struktury docelowe | 1.2. Najpierw rodzaj zmienności; Mapa decyzji (1/3-3/3); Lista kontrolna | 3 |
| [m5/s21](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s21-wzorzec-jako-decyzja-odwracalna-rozliczenie-z-dystrybutorem) | Wzorzec jako decyzja odwracalna - rozliczenie z dystrybutorem | 2.1. Refaktoryzacja od wzorca; 2.2. Kierunki odwrotne; 1.4. Kiedy wzorzec się nie opłaca | 3 |
| [m5/s22](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s22-remove-middle-man-fasada-kina) | Remove Middle Man - fasada kina | 2.3. Remove Middle Man; 2.2. Kierunki odwrotne | 3 |
| [m5/s23](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s23-refaktoryzacja-a-zmiana-kontraktu-zwrot-na-bigdecimal) | Refaktoryzacja a zmiana kontraktu - zwrot na BigDecimal | 3.1. Refaktoryzacja a zmiana kontraktu; 3.2. Co trzeba zachować przy wprowadzaniu wzorca; 5.1. Bezpieczna pętla pracy | 3 |
| [m5/s24](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s24-rename-nazwy-ktore-zyja-poza-kodem) | Rename - nazwy, które żyją poza kodem | 4.1. Co daje IDE, a czego nie dowodzi; 4.2. Rename - mechanika i granice | 3 |
| [m5/s25](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s25-extract-method-przeplyw-danych-i-wiele-wyjsc) | Extract Method - przepływ danych i wiele wyjść | 4.3. Extract Method - analiza przepływu danych; 4.4. Extract Method - mechanika i trudne przypadki | 3 |
| [m5/s26](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s26-strategy-command-i-state-w-jednej-klasie-pulpit-kasjera) | Strategy, Command i State w jednej klasie - pulpit kasjera | 6.1. Kto wybiera wariant i kiedy?; 6.2. Punkt wyjścia: trzy osie w jednej klasie; 6.3. Po: jeden mechanizm na każdą oś; 6.8. Typowe pomyłki przy wyborze | 3 |
| [m5/s27](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s27-replace-inheritance-with-delegation-raporty-sprzedazy) | Replace Inheritance with Delegation - raporty sprzedaży | 7.3. Gdy haki niosą dwie osie; 7.4. Po: szkielet w klasie finalnej, osie jako strategie; 7.5. Replace Inheritance with Delegation; 7.6. Strategy czy Template Method? | 3 |
| [m5/s28](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s28-replace-conditional-with-chain-of-responsibility-zatwierdzanie-zwrotow) | Replace Conditional with Chain of Responsibility - zatwierdzanie zwrotów | 8.3. Łańcuch `if/else` z ukrytą kolejnością; 8.4. Chain of Responsibility - ogniwo i złożenie; 8.5. Chain - sekwencja i ryzyka; 8.6. Chain of Responsibility czy Decorator? | 3 |
| [m5/s29](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s29-introduce-boundary-object-odpowiedz-terminala-platniczego) | Introduce Boundary Object - odpowiedź terminala płatniczego | 9.3. Surowy ładunek w domenie; 9.4. Boundary Object: zwalidowany obraz na granicy; 9.5. Dane na granicy - decyzje; 9.6. Adapter czy Boundary Object? | 3 |
| [m5/s30](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s30-extract-facade-zakup-biletow-w-sklepie-i-w-kasie) | Extract Facade - zakup biletów w sklepie i w kasie | 11.1. Klient orkiestruje podsystemy; 11.2. Po: fasada jako strażnik kolejności; 11.3. Facade - procedura i ryzyka; 3.4. Skutki strukturalne wzorców | 3 |
| [m5/s31](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s31-anti-corruption-layer-licencje-dystrybutora) | Anti-Corruption Layer - licencje dystrybutora | 12.1. Obcy model w regułach domeny; 12.2. ACL: port domenowy i tłumacz; 12.3. Facade a Anti-Corruption Layer; 12.4. ACL - ryzyka i Strangler Fig | 3 |
| [m5/s32](przewodnik/05-refaktoryzacja-do-wzorcow.html#scena-s32-introduce-mediator-ekran-wyboru-miejsc) | Introduce Mediator - ekran wyboru miejsc | 15.1. Komponenty połączone każdy z każdym; 15.2. Po: reguły współpracy w jednym miejscu; 15.3. Mediator - procedura i ryzyka; 15.4. Observer czy Mediator? | 3 |
