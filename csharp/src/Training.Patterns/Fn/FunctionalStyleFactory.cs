namespace Training.Patterns.Fn;

public class FunctionalStyleFactory
{
    internal interface IShape
    {
        void Draw();
    }

    internal class Circle : IShape
    {
        public void Draw()
        {
            Console.WriteLine("Circle");
        }
    }

    internal class Square : IShape
    {
        public void Draw()
        {
            Console.WriteLine("Square");
        }
    }

    public static void Run()
    {
        Func<IShape> circleFactory = () => new Circle();
        Func<IShape> squareFactory = () => new Square(); // Java: Square::new
        var s1 = circleFactory();
        var s2 = squareFactory();
        s1.Draw();
        s2.Draw();
    }
}
