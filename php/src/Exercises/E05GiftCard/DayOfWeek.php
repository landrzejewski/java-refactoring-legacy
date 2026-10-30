<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E05GiftCard;

/**
 * Stabilny kontrakt sceny: dzień tygodnia seansu (odpowiednik {@code java.time.DayOfWeek},
 * przypadki nazwane jak w Javie, więc {@code ->name} drukuje to samo co {@code name()}).
 */
enum DayOfWeek
{
    case MONDAY;
    case TUESDAY;
    case WEDNESDAY;
    case THURSDAY;
    case FRIDAY;
    case SATURDAY;
    case SUNDAY;
}
