<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents;

/** Stabilny kontrakt sceny: kanał sprzedaży, dla którego drukujemy dokumenty. */
enum Channel
{
    /** Kiosk samoobsługowy: drukarka termiczna, 32 kolumny, kod kreskowy. */
    case KIOSK;
    /** Aplikacja mobilna: dokument HTML-podobny, kod QR. */
    case MOBILE;
    /** Kasa: wydruk A4, kod kontrolny do odczytu wzrokowego. */
    case BOX_OFFICE;
}
