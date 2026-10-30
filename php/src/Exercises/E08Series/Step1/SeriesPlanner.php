<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E08Series\Step1;

use InvalidArgumentException;
use Training\Workshop\Exercises\E08Series\SeriesRequest;
use Training\Workshop\Shared\Money;

/**
 * Krok 1: planer nie kopiuje już seansu pole po polu. Kolejny tydzień to {@code $template->withStart(...)},
 * premiera to {@code withSurcharge(...)} na kopii, a inna sala to {@code withHall(...)}.
 * Dopłata premierowa zostaje w pierwszym tygodniu (w start wyciekała do wszystkich).
 * Domyślna sala przenosi się do szablonu, bo kopia już jej nie uzupełnia.
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
        $surcharges = [];
        $surcharges['VIP'] = Money::of(self::VIP);
        if ($request->format === '3D') {
            $surcharges['OKULARY'] = Money::of(self::GLASSES_3D);
        }
        $dubbing = $request->dubbing ?? false;
        $ageLimit = $request->ageLimit ?? 12;
        $template = new Screening($request->title, $request->format,
            $request->hall ?? 'A',
            $request->firstStart, $base, $surcharges, !$dubbing, $dubbing, $ageLimit);

        $series = [];
        for ($week = 0; $week < $request->weeks; $week++) {
            $copy = $template->withStart($template->start()->modify('+' . $week . ' weeks'));
            if ($week === 0 && $request->premiereWeek) {
                $copy = $copy->withSurcharge('PREMIERA', Money::of(self::PREMIERE));
            }
            $series[] = $copy;
        }
        if ($request->lastWeekHall !== null) {
            $series[count($series) - 1] = $series[count($series) - 1]->withHall($request->lastWeekHall);
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
