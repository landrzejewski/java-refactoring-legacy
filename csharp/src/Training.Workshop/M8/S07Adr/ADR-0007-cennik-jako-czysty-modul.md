# ADR-0007. Cennik jako czysty moduł domenowy

- **Status:** Zaakceptowana
- **Data:** 2026-03-02
- **Właściciel:** zespół Sprzedaż biletów
- **Zastępuje:** brak

## Kontekst

Cennik wycięty z `CinemaManager` ma trafić za abstrakcję i być weryfikowany w trybie shadow
(scena S03). Obecnie `TicketPricing` sam wysyła mail o rabacie grupowym i liczy w `double`.
Tryb shadow wymaga czystego obliczenia bez efektów ubocznych, a rozliczenia z dystrybutorem
nie tolerują błędów zaokrągleń.

## Decyzja

- **R1.** Przestrzeń nazw `Pricing` nie zależy od przestrzeni nazw `Notification`. Cennik zwraca wynik
  (np. informację o przyznanym rabacie), a o powiadomieniu decyduje warstwa aplikacji.
- **R2.** Przestrzeń nazw `Pricing` nie używa `double` ani `Double` do kwot - wyłącznie `Money`
  (skala 2, AwayFromZero).

## Rozważane opcje

1. Zostawić jak jest i opisać zasady w wiki - tanio, ale nikt tego nie pilnuje.
2. Wydzielić osobny projekt (assembly) dla cennika - twarda granica, ale duży koszt przy obecnym buildzie.
3. Zasady R1 i R2 jako wykonywalny test architektury w tym samym projekcie - wybrana.

## Konsekwencje

- Pozytywne: cennik można bezpiecznie liczyć w cieniu; naruszenie reguły zatrzymuje build.
- Negatywne: test czyta pliki źródłowe (reguły tekstowe, nie pełna analiza zależności);
  złamanie reguły przez refleksję albo pełną nazwę typu (bez dyrektywy `using`) trzeba wychwycić w przeglądzie.
- Do zrobienia: po wydzieleniu osobnego projektu zastąpić R1 granicą projektu i oznaczyć ten ADR
  jako zastąpiony.

## Weryfikacja

Reguły R1 i R2 są zaimplementowane w `ArchitectureRules` (ta sama scena) i sprawdzane
testem `S07SolutionTest`. Zmiana reguły wymaga nowego ADR, a nie edycji testu.
