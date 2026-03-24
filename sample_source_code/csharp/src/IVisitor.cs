namespace Embold.Sample;

/// <summary>Visitor interface for operations on animal types.</summary>
public interface IVisitor
{
    void Visit(Dog dog);
    void Visit(Cat cat);
}
