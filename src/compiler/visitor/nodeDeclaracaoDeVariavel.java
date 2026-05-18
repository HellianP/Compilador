package compiler.visitor;

import java.util.ArrayList;

public class nodeDeclaracaoDeVariavel {
    public ArrayList<nodeID> IDs;
    public nodeTipo tipo;

    public nodeDeclaracaoDeVariavel() {
        this.IDs = new ArrayList<>();
    }

    public void visit(Visitor visitor) {
        visitor.visit_nodeDeclaracaoDeVariavel(this);
    }
}