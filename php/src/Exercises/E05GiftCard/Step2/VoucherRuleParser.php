<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E05GiftCard\Step2;

use InvalidArgumentException;
use Training\Workshop\Exercises\E05GiftCard\DayOfWeek;

/**
 * Krok 2: jedyne miejsce, które zna tekstową składnię reguły
 * ("format=X", "day IN MON,TUE", "premiere", prefiks "NOT ", łącznik " AND ").
 * Zamienia tekst na drzewo wyrażeń; ocena i opis są już sprawą drzewa.
 */
final class VoucherRuleParser
{
    private function __construct()
    {
    }

    public static function parse(?string $rule): VoucherRule
    {
        if ($rule === null || trim($rule) === '') {
            throw new InvalidArgumentException('rule must not be empty');
        }
        $result = null;
        foreach (self::split('/ AND /', $rule) as $term) {
            $parsed = self::term(trim($term));
            $result = $result === null ? $parsed : new AndRule($result, $parsed);
        }
        return $result;
    }

    /** Jak w start: "NOT " może poprzedzać tylko pojedyncze pojęcie (podwójne NOT to nieznane pojęcie). */
    private static function term(string $term): VoucherRule
    {
        if (str_starts_with($term, 'NOT ')) {
            return new Not(self::atom(trim(substr($term, 4))));
        }
        return self::atom($term);
    }

    private static function atom(string $term): VoucherRule
    {
        if (str_starts_with($term, 'format=')) {
            return new FormatIs(trim(substr($term, strlen('format='))));
        }
        if (str_starts_with($term, 'day IN ')) {
            return new DayIn(self::days(substr($term, strlen('day IN '))));
        }
        if ($term === 'premiere') {
            return new IsPremiere();
        }
        throw new InvalidArgumentException('unknown rule term: ' . $term);
    }

    /** @return list<DayOfWeek> */
    private static function days(string $list): array
    {
        $days = [];
        foreach (self::split('/\s*,\s*/', trim($list)) as $token) {
            $days[] = match ($token) {
                'MON' => DayOfWeek::MONDAY,
                'TUE' => DayOfWeek::TUESDAY,
                'WED' => DayOfWeek::WEDNESDAY,
                'THU' => DayOfWeek::THURSDAY,
                'FRI' => DayOfWeek::FRIDAY,
                'SAT' => DayOfWeek::SATURDAY,
                'SUN' => DayOfWeek::SUNDAY,
                default => throw new InvalidArgumentException('unknown day: ' . $token),
            };
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
