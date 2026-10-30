<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E10Showtime\Step2;

/**
 * Krok 2: sterownik sali jest jedynym miejscem, które zna wszystkie urządzenia i kolejność ich reakcji.
 * Urządzenia zgłaszają zdarzenia przez {@see HallMediator}, a sterownik decyduje, co dalej.
 * Logika urządzeń (limit głośności, przeszkoda w drzwiach, stan projektora) zostaje w urządzeniach.
 *
 * @internal podsystem ukryty za fasadą {@see Showtime} (PHP nie ma widoczności pakietowej)
 */
final class HallController implements HallMediator
{
    private const int ADS_BEFORE_FEATURE = 2;

    private readonly Lights $lights;
    private readonly Sound $sound;
    private readonly Doors $doors;
    private readonly AdPlayer $adPlayer;
    private readonly Projector $projector;
    private ?string $pendingTitle = null;
    private bool $closeWhenCleared = false;

    public function __construct(DeviceLog $log)
    {
        $this->lights = new Lights($log);
        $this->sound = new Sound($log);
        $this->doors = new Doors($log, $this);
        $this->adPlayer = new AdPlayer($log, $this);
        $this->projector = new Projector($log);
    }

    // Polecenia klientów sali.

    public function startShow(string $title): void
    {
        $this->pendingTitle = $title;
        $this->doors->close();
    }

    public function endShow(): void
    {
        $this->projector->stop();
        $this->sound->set(0);
        $this->lights->set(100);
        $this->doors->open();
    }

    public function emergency(): void
    {
        $this->lights->set(100);
        $this->doors->open();
        $this->sound->set(0);
        $this->adPlayer->stop();
        $this->projector->cut();
    }

    // Reakcje na zdarzenia urządzeń - cała kolejność w jednym miejscu.

    public function doorsClosed(): void
    {
        $this->lights->set(30);
        $this->adPlayer->start(self::ADS_BEFORE_FEATURE);
    }

    public function doorsBlocked(): void
    {
        $this->lights->set(100);
        $this->closeWhenCleared = true;
    }

    public function doorsCleared(): void
    {
        if ($this->closeWhenCleared) {
            $this->closeWhenCleared = false;
            $this->doors->close();
        }
    }

    public function adsFinished(): void
    {
        $this->lights->set(0);
        $this->sound->set(70);
        $this->projector->play($this->pendingTitle);
        $this->pendingTitle = null;
    }

    // Zdarzenia sprzętu (w teście symulowane poleceniami skryptu).

    public function adTick(): void
    {
        $this->adPlayer->tick();
    }

    public function doorObstacle(): void
    {
        $this->doors->obstacle();
    }

    public function doorCleared(): void
    {
        $this->doors->cleared();
    }

    public function volume(int $level): void
    {
        $this->sound->set($level);
    }
}
