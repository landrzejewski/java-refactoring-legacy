namespace Training.Workshop.M3.S02BreakDependencies.Step4;

/// <summary>Najwęższy seam dla powiadomień: delegat, w teście wystarczy lambda.</summary>
public delegate void ReminderSender(string to, string subject, string body);
