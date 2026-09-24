import type { Till } from './Till.js';

/** Krok 2: kontrakt komendy - dane żądania (args) i stan (till) przychodzą jako argumenty. */
export interface ConsoleCommand {
  execute(args: string, till: Till): string;
}
