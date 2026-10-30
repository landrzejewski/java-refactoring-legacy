<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications\Start;

use InvalidArgumentException;
use Training\Workshop\Exercises\E03Notifications\Cancel;
use Training\Workshop\Exercises\E03Notifications\Move;
use Training\Workshop\Exercises\E03Notifications\Scenario;

/** Start: klient sceny - kierownik kina wykonuje kolejne zmiany repertuaru i oddaje dziennik efektow. */
final class ScreeningDesk
{
    public function run(Scenario $scenario): string
    {
        $journal = new Journal();
        $scheduler = new ScreeningScheduler(
            $journal,
            new Waitlist($journal, $scenario->waiting),
            new ChangeStats($journal),
        );
        $scheduler->register($scenario->screening);
        $id = $scenario->screening->id;
        try {
            foreach ($scenario->changes as $change) {
                match (true) {
                    $change instanceof Move => $scheduler->move($id, $change->newStart, $change->newHall, $scenario->ticketHolders),
                    $change instanceof Cancel => $scheduler->cancel($id, $change->reason, $scenario->ticketHolders),
                };
            }
        } catch (InvalidArgumentException $exception) {
            $journal->note('ERROR: ' . $exception->getMessage());
        }
        return implode("\n", $journal->lines());
    }
}
