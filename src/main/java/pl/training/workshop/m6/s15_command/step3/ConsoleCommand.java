package pl.training.workshop.m6.s15_command.step3;

/** Krok 3: kontrakt komendy - dane żądania (args) i stan (till) przychodzą jako argumenty. */
@FunctionalInterface
public interface ConsoleCommand {
    String execute(String args, Till till);
}
