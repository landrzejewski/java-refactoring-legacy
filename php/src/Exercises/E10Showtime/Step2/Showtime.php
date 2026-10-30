<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E10Showtime\Step2;

use Training\Workshop\Exercises\E10Showtime\ShowRequest;

/**
 * Krok 2: jedna operacja "uruchom seans" zamiast znajomości kroków i ich kolejności u każdego klienta.
 * Fasada nie koordynuje reakcji urządzeń (to robi sterownik sali) - tylko upraszcza wejście do
 * podsystemów i pilnuje, że żadnego kroku nie da się pominąć. Składa ją korzeń kompozycji.
 */
final readonly class Showtime
{
    /** @internal fasadę składa korzeń kompozycji ({@see ShowtimeRunner}); PHP nie ma konstruktora pakietowego */
    public function __construct(
        private TicketScanner $scanner,
        private HallController $hall,
        private AttendanceReport $report,
    ) {
    }

    public function start(ShowRequest $request): void
    {
        $attendance = $this->scanner->close($request->screeningId);
        $this->hall->startShow($request->title);
        $this->report->record($request->screeningId, $attendance);
    }

    public function end(string $screeningId): void
    {
        $this->hall->endShow();
        $this->report->close($screeningId);
    }
}
