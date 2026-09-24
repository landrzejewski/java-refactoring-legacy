/** Krok 1: bez zmian - element programu kina (film albo kontener filmów). */
export interface ProgramItem {
  minutes(): number;

  describe(): string;
}
