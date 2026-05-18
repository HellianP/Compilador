package compiler.visitor;


public class nodeDeclaracaoDeVariavel {
  public nodeID ID;
  public nodeTipo tipo;


  public void visit(Visitor visitor) {
    visitor.visit_nodeDeclaracaoDeVariavel(this);
  }
}