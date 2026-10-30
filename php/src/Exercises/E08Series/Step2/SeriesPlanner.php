<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E08Series\Step2;

use InvalidArgumentException;
use Training\Workshop\Exercises\E08Series\SeriesRequest;
use Training\Workshop\Shared\Money;

/**
 * Krok 2: planer składa szablon budowniczym i ustawia tylko to, co przyszło w zamówieniu.
 * Nie zna już cen formatów, domyślnej sali ani limitu wieku; sprawdza jedynie liczbę tygodni,
 * bo to reguła serii, a nie pojedynczego seansu. Kolejne tygodnie nadal powstają z kopii szablonu.
 */
final class SeriesPlanner
{
    private const string PREMIERE = '5.00';

    /** @return list<Screening> */
    public function plan(SeriesRequest $request): array
    {
        if ($request->weeks < 1) {
            throw new InvalidArgumentException('weeks must be positive');
        }
        $builder = Screening::builder()
            ->title($request->title)
            ->format($request->format)
            ->start($request->firstStart);
        if ($request->hall !== null) {
            $builder->hall($request->hall);
        }
        if ($request->basePrice !== null) {
            $builder->basePrice($request->basePrice);
        }
        if ($request->dubbing !== null) {
            $builder->dubbing($request->dubbing);
        }
        if ($request->ageLimit !== null) {
            $builder->ageLimit($request->ageLimit);
        }
        $template = $builder->build();

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
}
