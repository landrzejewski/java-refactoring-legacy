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
# Moduł 6. Podsumowanie

Od zapachu do wzorca: jeden bezpieczny przepływ pracy i plan wdrożenia w zespole

---

## Agenda

1. Mapa szkolenia i jeden przepływ pracy
2. Mapa decyzji: zapach → refaktoryzacja → wzorzec → ryzyko
3. Lista kontrolna bezpiecznej refaktoryzacji
4. Antywzorce
5. Plan wdrożenia w zespole
6. Ćwiczenie końcowe, retrospektywa, pytania końcowe

Czas: ok. 20 minut podsumowania i 60-75 minut ćwiczenia końcowego na CineLegacy.

---

## 1.1. Mapa szkolenia

| Moduł | Pytanie | Narzędzia |
| --- | --- | --- |
| 1. Wprowadzenie | co chronimy? | zachowanie obserwowalne, dług, zapachy, metryki |
| 2. Prewencja | jak nie tworzyć nowego legacy? | zasady, granice, ADR, bramki, skaut |
| 3. Testowanie | jaki mamy dowód? | portfel testów, dublery, golden master, szwy |
| 4. Wzorce projektowe | jakie rozwiązania znamy? | katalog GoF, warianty funkcyjne |
| 5. Refaktoryzacja do wzorców | kiedy do, kiedy od wzorca? | osie zmienności, pary wzorców, granice |
| 6. Podsumowanie | jak to wdrożyć? | mapa decyzji, lista kontrolna, plan |

---

## 1.2. Jeden przepływ pracy

1. **Diagnoza:** zapach jako hipoteza, metryka razem z historią zmian (moduł 1).
2. **Decyzja:** czy w ogóle refaktoryzować, jaki cel, jaki najmniejszy krok (moduły 1-2).
3. **Zabezpieczenie:** wektor zachowania, charakterystyka lub golden master, kontrolna mutacja (moduł 3).
4. **Mały krok:** automat IDE, jedna intencja, kompilacja, testy, commit (moduły 3 i 5).
5. **Kierunek:** do wzorca, od wzorca albo zostaw tak, jak jest (moduły 4-5).
6. **Zamknięcie:** elementy przejściowe usunięte, decyzja zapisana (ADR), przegląd (moduł 2).

---

## 2.1. Mapa decyzji (1/3): warunki

| Zapach | Refaktoryzacja | Cel | Ryzyko |
| --- | --- | --- | --- |
| `switch` po trybie w każdym wywołaniu | Replace Conditional Logic with Strategy | Strategy | moment wyboru |
| `if (status == ...)` w każdej operacji | Replace State-Altering Conditionals with State | State | stan a efekt |
| `switch` po nazwie żądania w kilku miejscach | Replace Conditional Dispatcher with Command | Command | kompletność, `undo` |
| pole zależne od rodzaju obiektu | Replace Conditional with Polymorphism | podtypy `sealed` | tworzenie, ORM |
| długi `if/else` z kolejnością reguł | Extract Method, potem ogniwa | Chain of Responsibility | kolejność ogniw |

---

## 2.2. Mapa decyzji (2/3): typy i współpraca

| Zapach | Refaktoryzacja | Cel | Ryzyko |
| --- | --- | --- | --- |
| prymitywny kod domenowy | Replace Type Code with Class | obiekt wartości | nieznany kod |
| N × M podklas | Replace Inheritance with Delegation | Strategy | utrata typu podklasy |
| dodatki wplecione w rdzeń | Move Embellishment to Decorator | Decorator | kolejność, wyjątki |
| sztywno wpisani odbiorcy | Replace Hard-coded Notifications with Observer | Observer | błędy, wątki |
| komponenty znające się nawzajem | Move Method do koordynatora | Mediator | God Mediator |

---

## 2.3. Mapa decyzji (3/3): granice i nadmiar

| Zapach | Refaktoryzacja | Cel | Ryzyko |
| --- | --- | --- | --- |
| surowe dane z zewnątrz w domenie | Extract Class na granicy, port, adapter | Boundary Object | typ i moment błędu |
| klienci orkiestrują podsystemy | Extract Method, Move Method | Facade | God Class, atomowość |
| obcy model w języku domeny | port, tłumacz, osobny pakiet | ACL | zachłanne tłumaczenie |
| pośrednik 1:1, globalny singleton | Remove Middle Man, Inline Singleton | mniej struktury | ukryte obowiązki |
| God Class | kampania małych kroków | kilka mechanizmów | big bang |

---

## 3.1. Lista kontrolna (1/2)

- **Przed:** nazwany cel i rodzaj zmiany (refaktoryzacja, zmiana zachowania, migracja); znani konsumenci; zielona baza; punkt przywracania.
- **Sieć:** wektor zachowania (wynik, wyjątki, stan, efekty, czas, granice); charakterystyka lub golden master sprawdzone **kontrolną mutacją**; luki sieci nazwane.
- **Podczas:** jedna intencja na krok; tryb strukturalny oddzielony od funkcjonalnego; automat IDE tam, gdzie się da; kompilacja i testy po każdym ruchu.
- **Użycia dynamiczne:** refleksja, konfiguracja, serializacja, szablony, DI i klienci spoza repozytorium sprawdzone ręcznie.

---

## 3.2. Lista kontrolna (2/2)

- **Wzorzec:** problem opisany bez nazwy wzorca; wariant istnieje dziś; rozważony kierunek „od wzorca” i prosty `switch`.
- **Granice:** obcy model i surowe dane nie przechodzą do domeny; zmiana typu lub momentu błędu zatwierdzona osobno.
- **Na koniec:** stary kod i elementy przejściowe usunięte albo mają właściciela i kryterium usunięcia; pełny build zielony.
- **Zespół:** zestaw zmian z jedną intencją i opisem (zakres, ryzyko, wycofanie); decyzja zapisana w ADR, jeśli ma trwałe skutki.

---

## 4.1. Antywzorce (1/2)

| Antywzorzec | Objaw | Zamiast |
| --- | --- | --- |
| big bang | „przepiszmy to”, gałąź na miesiąc | Strangler Fig, małe zestawy zmian |
| refaktoryzacja bez sieci | „to tylko porządki” | charakterystyka, golden master |
| ślepe zatwierdzenie | nowy plik oczekiwany bez czytania różnicy | przegląd różnicy przed zatwierdzeniem |
| mieszanie trybów | refaktoryzacja i nowa reguła w jednym commicie | dwa kroki, dwa commity |

---

## 4.2. Antywzorce (2/2)

| Antywzorzec | Objaw | Zamiast |
| --- | --- | --- |
| wzorzec dla wzorca | interfejs z jedną implementacją „na przyszłość” | YAGNI, kierunek od wzorca |
| wieczny element przejściowy | adapter, flaga, forwarder bez daty końca | kryterium usunięcia i właściciel |
| metryka jako cel | 100% pokrycia bez asercji | pokrycie gałęzi, mutacje |
| zaufanie automatowi | Rename bez sprawdzenia refleksji i konfiguracji | wyszukanie użyć tekstowych |

---

## 5.1. Plan wdrożenia: prewencja, testy, małe kroki

- **Prewencja (moduł 2):** zasada skauta w dotykanych plikach, bramka „brak nowych naruszeń”, ADR dla decyzji trwałych, przegląd ukierunkowany na ryzyko.
- **Testy (moduł 3):** golden master dla najbardziej krytycznego przepływu, charakterystyka przed każdą zmianą w gorącym punkcie, kontrolna mutacja jako nawyk.
- **Małe kroki (moduł 5):** jedna intencja na zestaw zmian, automaty IDE, test równoważności przy wymianie implementacji, rozszerzenia zatwierdzane osobno.
- **Wspólny język:** nazwy zapachów, refaktoryzacji i wzorców z katalogów - w przeglądach i opisach zmian.
- **Mierzenie efektu:** czas zmiany w obszarze, liczba regresji, rozmiar zestawów zmian - nie liczba wzorców.

---

## 5.2. Pierwsze 90 dni

| Okres | Działanie | Dowód |
| --- | --- | --- |
| tydzień 1-2 | stan bazowy: build, testy, ostrzeżenia, gorące punkty (zmiany × złożoność) | lista 3 obszarów z uzasadnieniem |
| do 30 dni | golden master lub charakterystyka dla 1 obszaru; skaut; bramka bez nowych naruszeń | zielony pipeline, pierwsze małe serie |
| do 60 dni | pierwsza kampania: 2-3 refaktoryzacje do lub od wzorca, ADR | krótszy czas zmiany w obszarze |
| do 90 dni | przegląd efektów, usunięcie elementów przejściowych, zaostrzenie bramek | trend metryk, retrospektywa zespołu |

Zaczynaj tam, gdzie kod **zmienia się najczęściej**, a nie tam, gdzie jest najbrzydszy.

---

## Ćwiczenie końcowe: CineLegacy pod golden masterem (60-75 min)

- Kod: `CinemaManager` (God Class) chroniony przez `CinemaManagerGoldenMasterTest`; praca na **własnej gałęzi**.
- Etapy: diagnoza zapachów (15 min) → ocena sieci i jej luk (10 min) → plan 2-3 refaktoryzacji w małych krokach (15 min) → wykonanie jednej (20-25 min) → retrospektywa (10 min).
- Golden master zielony po każdym kroku; pliku zatwierdzonego wyniku **nie zmieniamy**.
- Luka w sieci → najpierw test charakterystyki, potem zmiana.

Szczegóły: zadania modułu 6.

---

## Retrospektywa

- Który zapach był najłatwiejszy do zobaczenia, a który najtrudniejszy do usunięcia?
- Ile mutacji przeżyło golden master i co to mówi o jego zakresie?
- Który krok okazał się większy, niż zakładał plan? Dlaczego?
- Gdzie pojawiła się pokusa zmiany zachowania „przy okazji”?
- Która decyzja zasługuje na ADR?
- Co z tego przepływu zastosujecie w pierwszym tygodniu po szkoleniu?

---

## Pytania końcowe

1. Po czym poznać, że zmiana jest refaktoryzacją, a nie zmianą zachowania?
2. Dlaczego zielony golden master nie dowodzi bezpieczeństwa każdej zmiany?
3. Jak wybrać pierwszy obszar do refaktoryzacji w dużym systemie legacy?
4. Kiedy wzorzec należy usunąć zamiast go rozbudowywać?
5. Jak rozstrzygnąć między Strategy, Command i State dla podobnego `switch`?
6. Co odróżnia Facade od Anti-Corruption Layer w kontekście migracji?
7. Jakie elementy powinien mieć opis zestawu zmian z refaktoryzacją?
8. Jak zmierzyć, czy refaktoryzacja w zespole przynosi efekt?

---

## Podsumowanie - najważniejsze wnioski

- Refaktoryzacja to zachowanie **obserwowalnego kontraktu** - wszystko inne jest zmianą, którą zatwierdzamy osobno.
- Najpierw dowód (testy, golden master, mutacja), potem ruch; najmniejszy krok wygrywa z najładniejszym diagramem.
- Wzorzec jest rezultatem refaktoryzacji, nie celem; kierunek „od wzorca” jest równie ważny.
- Granice (Adapter, Boundary Object, Facade, ACL) chronią domenę i pozwalają migrować przyrostowo.
- Zespół utrzymuje efekt dzięki prewencji, bramkom i wspólnemu językowi - nie dzięki jednorazowej akcji.
