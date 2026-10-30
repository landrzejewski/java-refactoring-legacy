<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E05GiftCard\Start;

use InvalidArgumentException;
use Training\Workshop\Exercises\E05GiftCard\DayOfWeek;
use Training\Workshop\Exercises\E05GiftCard\ScreeningFacts;

/**
 * Start: reguła ważności vouchera jest tekstem z konfiguracji, np.
 * "format=3D AND day IN MON,TUE AND NOT premiere". Tekst jest rozbierany dwa razy, osobno
 * w applies (czy voucher obowiązuje) i w describe (opis dla kasjera), więc każde nowe pojęcie
 * w regule (np. godzina seansu) trzeba dopisać w obu metodach, a oba rozbiory muszą się zgadzać.
 */
final class Vouchers
{
    private function __construct()
    {
    }

    public static function applies(?string $rule, ScreeningFacts $facts): bool
    {
        if ($rule === null || trim($rule) === '') {
            throw new InvalidArgumentException('rule must not be empty');
        }
        $result = true;
        foreach (self::split('/ AND /', $rule) as $term) {
            $trimmed = trim($term);
            $negated = str_starts_with($trimmed, 'NOT ');
            $body = $negated ? trim(substr($trimmed, 4)) : $trimmed;
            if (str_starts_with($body, 'format=')) {
                $value = $facts->format === trim(substr($body, strlen('format=')));
            } elseif (str_starts_with($body, 'day IN ')) {
                $value = in_array($facts->day, self::days(substr($body, strlen('day IN '))), true);
            } elseif ($body === 'premiere') {
                $value = $facts->premiere;
            } else {
                throw new InvalidArgumentException('unknown rule term: ' . $body);
            }
            $result = $result && ($negated !== $value);
        }
        return $result;
    }

    public static function describe(?string $rule): string
    {
        if ($rule === null || trim($rule) === '') {
            throw new InvalidArgumentException('rule must not be empty');
        }
        $parts = [];
        foreach (self::split('/ AND /', $rule) as $term) {
            $trimmed = trim($term);
            $negated = str_starts_with($trimmed, 'NOT ');
            $body = $negated ? trim(substr($trimmed, 4)) : $trimmed;
            if (str_starts_with($body, 'format=')) {
                $text = 'format ' . trim(substr($body, strlen('format=')));
            } elseif (str_starts_with($body, 'day IN ')) {
                $list = substr($body, strlen('day IN '));
                self::days($list);
                $text = 'dzień: ' . implode(', ', self::split('/\s*,\s*/', trim($list)));
            } elseif ($body === 'premiere') {
                $text = 'premiera';
            } else {
                throw new InvalidArgumentException('unknown rule term: ' . $body);
            }
            $parts[] = $negated ? 'nie ' . $text : $text;
        }
        return implode(' oraz ', $parts);
    }

    /** @return list<DayOfWeek> */
    private static function days(string $list): array
    {
        $days = [];
        foreach (self::split('/\s*,\s*/', trim($list)) as $token) {
            $day = match ($token) {
                'MON' => DayOfWeek::MONDAY,
                'TUE' => DayOfWeek::TUESDAY,
                'WED' => DayOfWeek::WEDNESDAY,
                'THU' => DayOfWeek::THURSDAY,
                'FRI' => DayOfWeek::FRIDAY,
                'SAT' => DayOfWeek::SATURDAY,
                'SUN' => DayOfWeek::SUNDAY,
                default => throw new InvalidArgumentException('unknown day: ' . $token),
            };
            if (!in_array($day, $days, true)) {
                $days[] = $day;
            }
        }
        return $days;
    }

    /**
     * Podział jak {@code String.split} w Javie: puste elementy na końcu są pomijane
     * ({@code explode} i {@code preg_split} je zostawiają).
     *
     * @return list<string>
     */
    private static function split(string $pattern, string $text): array
    {
        $parts = preg_split($pattern, $text);
        while ($parts !== [] && end($parts) === '') {
            array_pop($parts);
        }
        return $parts;
    }
}
