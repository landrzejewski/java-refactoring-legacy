namespace Training.Patterns.Behavioral.TemplateMethod;

public abstract class Processor<V, PV>
{
    // Java: public final void run() - non-virtual in C#, so subclasses cannot override the template
    public void Run()
    {
        var data = Read();
        var result = Process(data);
        Write(result);
    }

    protected abstract V Read();

    protected abstract PV Process(V data);

    protected abstract void Write(PV data);
}
