package pl.training.workshop.m6.s15_command.step2;

/** Krok 2: kontrakt komendy - dane żądania (args) i stan (till) przychodzą jako argumenty. */
@FunctionalInterface
public interface ConsoleCommand {
    String execute(String args, Till till);
}
