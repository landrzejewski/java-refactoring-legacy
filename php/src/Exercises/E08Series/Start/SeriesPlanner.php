<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E08Series\Start;

use ArrayObject;
use InvalidArgumentException;
use Training\Workshop\Exercises\E08Series\SeriesRequest;
use Training\Workshop\Shared\Money;

/**
 * Start: planer serii tygodniowej. Szablon powstaje dziewięcioargumentowym konstruktorem,
 * a każdy kolejny tydzień i przeniesienie do innej sali to ręczna kopia pole po polu.
 * Kopia dzieli mapę dopłat z szablonem, więc dopłata premierowa dopisana do pierwszego
 * tygodnia "wycieka" do wszystkich kolejnych. Wartości domyślne (sala A, cena z formatu,
 * limit wieku 12) są rozsiane po planerze, kopii i samym seansie.
 */
final class SeriesPlanner
{
    private const string VIP = '10.00';
    private const string GLASSES_3D = '3.00';
    private const string PREMIERE = '5.00';

    /** @return list<Screening> */
    public function plan(SeriesRequest $request): array
    {
        if ($request->weeks < 1) {
            throw new InvalidArgumentException('weeks must be positive');
        }
        if (trim($request->title) === '') {
            throw new InvalidArgumentException('title must not be blank');
        }
        if (!in_array($request->format, ['2D', '3D', 'IMAX'], true)) {
            throw new InvalidArgumentException('unknown format: ' . $request->format);
        }
        $base = $request->basePrice ?? self::priceFor($request->format);
        if ($base->compareTo(Money::zero()) < 0) {
            throw new InvalidArgumentException('base price must not be negative');
        }
        $surcharges = new ArrayObject();
        $surcharges['VIP'] = Money::of(self::VIP);
        if ($request->format === '3D') {
            $surcharges['OKULARY'] = Money::of(self::GLASSES_3D);
        }
        $dubbing = $request->dubbing ?? false;
        $ageLimit = $request->ageLimit ?? 12;
        $template = new Screening($request->title, $request->format, $request->hall,
            $request->firstStart, $base, $surcharges, !$dubbing, $dubbing, $ageLimit);

        $series = [];
        for ($week = 0; $week < $request->weeks; $week++) {
            $copy = new Screening($template->title(), $template->format(),
                $template->hall() ?? 'A',
                $template->start()->modify('+' . $week . ' weeks'), $template->basePrice(), $template->surcharges(),
                $template->subtitles(), $template->dubbing(), $template->ageLimit());
            if ($week === 0 && $request->premiereWeek) {
                $copy->surcharges()['PREMIERA'] = Money::of(self::PREMIERE);
            }
            $series[] = $copy;
        }
        if ($request->lastWeekHall !== null) {
            $last = $series[count($series) - 1];
            $series[count($series) - 1] = new Screening($last->title(), $last->format(), $request->lastWeekHall,
                $last->start(), $last->basePrice(), $last->surcharges(), $last->subtitles(), $last->dubbing(),
                $last->ageLimit());
        }
        return $series;
    }

    private static function priceFor(string $format): Money
    {
        return match ($format) {
            '2D' => Money::of('25.00'),
            '3D' => Money::of('32.00'),
            'IMAX' => Money::of('40.00'),
            default => throw new InvalidArgumentException('unknown format: ' . $format),
        };
    }
}
