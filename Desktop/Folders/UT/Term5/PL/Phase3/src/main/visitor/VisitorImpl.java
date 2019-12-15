package main.visitor;
import main.ast.node.*;
import main.ast.node.Program;
import main.ast.node.declaration.*;
import main.ast.node.declaration.handler.*;
import main.ast.node.declaration.VarDeclaration;
import main.ast.node.expression.*;
import main.ast.node.expression.operators.BinaryOperator;
import main.ast.node.expression.operators.UnaryOperator;
import main.ast.node.expression.values.BooleanValue;
import main.ast.node.expression.values.IntValue;
import main.ast.node.expression.values.StringValue;
import main.ast.node.statement.*;
import java.util.*;

import main.ast.type.Type;
import main.ast.type.actorType.ActorType;
import main.ast.type.arrayType.ArrayType;
import main.ast.type.noType.NoType;
import main.ast.type.primitiveType.BooleanType;
import main.ast.type.primitiveType.IntType;
import main.ast.type.primitiveType.StringType;


public class VisitorImpl implements Visitor {
    private ArrayList<String> errorMessages = new ArrayList<String>();
    private ArrayList<String> actorNames = new ArrayList<String>();
    private ArrayList<String> knownActorNames = new ArrayList<String>();
    private ArrayList<String> actorVarNames = new ArrayList<String>();
    private ArrayList<String> msgHandlerNames = new ArrayList<String>();
    private ArrayList<String> msgHandlerArgsNames = new ArrayList<String>();
    private ArrayList<String> msgHandlerLocalVarsNames = new ArrayList<String>();
    private ArrayList<String> blocks = new ArrayList<String>();
    private ArrayList<String> varDeclarationNames = new ArrayList<String>();
    private ArrayList<String> msgHandlerArgsNames2 = new ArrayList<String>();
    private int blockCounter = 0;
    private int globalThisBlock = 0;
    private String currentActor = "";
    private String currentHandler = "";

    protected void visitStatement( Statement stat )
    {
        if( stat == null )
            return;
        else if( stat instanceof MsgHandlerCall )
            this.visit( ( MsgHandlerCall ) stat );
        else if( stat instanceof Block )
            this.visit( ( Block ) stat );
        else if( stat instanceof Conditional )
            this.visit( ( Conditional ) stat );
        else if( stat instanceof For )
            this.visit( ( For ) stat );
        else if( stat instanceof Break )
            this.visit( ( Break ) stat );
        else if( stat instanceof Continue )
            this.visit( ( Continue ) stat );
        else if( stat instanceof Print )
            this.visit( ( Print ) stat );
        else if( stat instanceof Assign )
            this.visit( ( Assign ) stat );
    }

    protected void visitExpr( Expression expr )
    {
        if( expr == null )
            return;
        else if( expr instanceof UnaryExpression )
            this.visit( ( UnaryExpression ) expr );
        else if( expr instanceof BinaryExpression )
            this.visit( ( BinaryExpression ) expr );
        else if( expr instanceof ArrayCall )
            this.visit( ( ArrayCall ) expr );
        else if( expr instanceof ActorVarAccess )
            this.visit( ( ActorVarAccess ) expr );
        else if( expr instanceof Identifier )
            this.visit( ( Identifier ) expr );
        else if( expr instanceof Self )
            this.visit( ( Self ) expr );
        else if( expr instanceof Sender )
            this.visit( ( Sender ) expr );
        else if( expr instanceof BooleanValue )
            this.visit( ( BooleanValue ) expr );
        else if( expr instanceof IntValue )
            this.visit( ( IntValue ) expr );
        else if( expr instanceof StringValue )
            this.visit( ( StringValue ) expr );
    }

    public boolean isSubtype(Type right, Type left) {
        if(left.toString() == right.toString()){
            return true;
        }
        if(right.toString() == "notype") {
            return true;
        }
        return false;
    }

    public Type returnType(String toString, Identifier id) {
        if(toString == "int")
            return new IntType();
        else if(toString == "boolean")
            return new BooleanType();
        else if(toString == "int[]")
            return new ArrayType(1);
        else if(toString == "string")
            return new StringType();
        else
            return new NoType();
    }

    @Override
    public void visit(Program program) {
        for (ActorDeclaration actor: program.getActors()) {
            currentActor = actor.getName().getName();
            actor.accept(this);
            actorNames.add(actor.getName().getName());
        }
        program.getMain().accept(this);
    }

    @Override
    public void visit(ActorDeclaration actorDeclaration) {
        if(actorDeclaration.getParentName() != null) {
            if(!(actorNames.contains(actorDeclaration.getParentName().getName()))) {
                errorMessages.add("actor " + actorDeclaration.getParentName().getName() + " is not declared");
            }
            else {
                actorDeclaration.getParentName().accept(this);
                for (String str : knownActorNames) { //add it's parent's knownActors to the lists
                    if(str.split("_", 2)[0] == actorDeclaration.getParentName().getName()) {
                        knownActorNames.add(actorDeclaration.getName().getName() +
                                "_" + str.split("_", 2)[1]);
                    }
                }
                for (String str : actorVarNames) { //add it's parent's actorVars to the lists
                    if(str.split("_", 2)[0] == actorDeclaration.getParentName().getName()) {
                        actorVarNames.add(actorDeclaration.getName().getName() +
                                "_" + str.split("_", 2)[1]);
                    }
                }
                for (String str : msgHandlerNames) { //add it's parent's msgHandlers to the lists
                    if(str.split("_", 2)[0] == actorDeclaration.getParentName().getName()) {
                        msgHandlerNames.add(actorDeclaration.getName().getName() +
                                "_" + str.split("_", 2)[1]);
                    }
                }
                for (String str : msgHandlerArgsNames) { //add it's parent's msgHandlers Args to the lists
                    if(str.split("_", 2)[0] == actorDeclaration.getParentName().getName()) {
                        msgHandlerArgsNames.add(actorDeclaration.getName().getName() +
                                "_" + str.split("_", 2)[1]);
                    }
                }
                for (String str : msgHandlerLocalVarsNames) { //add it's parent's msgHandlers localVars to the lists
                    if(str.split("_", 2)[0] == actorDeclaration.getParentName().getName()) {
                        msgHandlerLocalVarsNames.add(actorDeclaration.getName().getName() +
                                "_" + str.split("_", 2)[1]);
                    }
                }
            }
        }
        for (VarDeclaration knownActor : actorDeclaration.getKnownActors()) {
            knownActor.accept(this);
            knownActorNames.add(actorDeclaration.getName().getName() + "_" +
                    knownActor.getIdentifier().getName() + "_" + knownActor.getType().toString());
        }
        for (VarDeclaration actorVar : actorDeclaration.getActorVars()) {
            actorVar.accept(this);
            actorVarNames.add(actorDeclaration.getName().getName() + "_" +
                    actorVar.getIdentifier().getName() + "_" + actorVar.getType().toString());
        }
        if(actorDeclaration.getInitHandler() != null) { //////////////////////////////////////
            currentHandler = actorDeclaration.getInitHandler().getName().getName();
            actorDeclaration.getInitHandler().accept(this);
            msgHandlerNames.add(actorDeclaration.getName().getName() + "_" +
                    actorDeclaration.getInitHandler().getName().getName());
            currentHandler = "";
        }
        for (HandlerDeclaration msgHandler : actorDeclaration.getMsgHandlers()) {
            currentHandler = actorDeclaration.getInitHandler().getName().getName();
            msgHandler.accept(this);
            msgHandlerNames.add(actorDeclaration.getName().getName() + "_" +
                    msgHandler.getName().getName());
            currentHandler = "";
        }
    }

    @Override
    public void visit(HandlerDeclaration handlerDeclaration) {
        for (VarDeclaration arg : handlerDeclaration.getArgs()) {
            msgHandlerArgsNames.add(handlerDeclaration.getName().getName()
                    + "_" + arg.getType().toString() + "_"  + currentActor);
            msgHandlerArgsNames2.add(handlerDeclaration.getName().getName()
                    + "_" + arg.getIdentifier().getName() + "_"  + currentActor + "_" + arg.getType().toString());
            arg.accept(this);
        }
        for (VarDeclaration localVar : handlerDeclaration.getLocalVars()) {
            msgHandlerLocalVarsNames.add(handlerDeclaration.getName().getName()
                    + "_" + localVar.getIdentifier().getName() + "_" + localVar.getType().toString() + "_" + currentActor);
            localVar.accept(this);
        }
        for (Statement body : handlerDeclaration.getBody()) {
            body.accept(this);
        }
    }

    @Override
    public void visit(VarDeclaration varDeclaration) {
        varDeclarationNames.add(varDeclaration.getIdentifier().getName() + "_"
                + varDeclaration.getType().toString() + "_" + globalThisBlock);
    }

    @Override
    public void visit(Main mainActors) {
        //TODO: implement appropriate visit functionality
    }

    @Override
    public void visit(ActorInstantiation actorInstantiation) {
        //TODO: implement appropriate visit functionality
    }


    @Override
    public void visit(UnaryExpression unaryExpression) {
        unaryExpression.getOperand().accept(this);
        if(unaryExpression.getUnaryOperator() == UnaryOperator.minus || unaryExpression.getUnaryOperator() == UnaryOperator.postdec
                || unaryExpression.getUnaryOperator() == UnaryOperator.postinc || unaryExpression.getUnaryOperator() == UnaryOperator.predec
                || unaryExpression.getUnaryOperator() == UnaryOperator.preinc) {
            if(!(unaryExpression.getType().toString() == "int")) {
                errorMessages.add(unaryExpression.getLine() + "_unsupported operand type for "
                        + unaryExpression.getUnaryOperator().toString());
                unaryExpression.setType(new NoType());
            }
            else {
                unaryExpression.setType(new IntType());
            }
        }
        if(unaryExpression.getUnaryOperator() == UnaryOperator.not) {
            if(!(unaryExpression.getType().toString() == "boolean")) {
                errorMessages.add(unaryExpression.getLine() + "_unsupported operand type for "
                        + unaryExpression.getUnaryOperator().toString());
                unaryExpression.setType(new NoType());
            }
            else {
                unaryExpression.setType(new BooleanType());
            }
        }
    }

    @Override
    public void visit(BinaryExpression binaryExpression) {
        binaryExpression.getLeft().accept(this);
        binaryExpression.getRight().accept(this);
        if(binaryExpression.getBinaryOperator() == BinaryOperator.and || binaryExpression.getBinaryOperator() == BinaryOperator.and) {
            if(!(binaryExpression.getRight().getType().toString() == "boolean"
                    && binaryExpression.getLeft().getType().toString() == "boolean")) {
                errorMessages.add(binaryExpression.getLine() + "_unsupported operand type for "
                        + binaryExpression.getBinaryOperator().toString());
                binaryExpression.setType(new NoType());
            }
            else {
                binaryExpression.setType(new BooleanType());
            }
        }
        if(binaryExpression.getBinaryOperator() == BinaryOperator.mult || binaryExpression.getBinaryOperator() == BinaryOperator.div
                || binaryExpression.getBinaryOperator() == BinaryOperator.add || binaryExpression.getBinaryOperator() == BinaryOperator.sub
                || binaryExpression.getBinaryOperator() == BinaryOperator.mod || binaryExpression.getBinaryOperator() == BinaryOperator.gt
                || binaryExpression.getBinaryOperator() == BinaryOperator.lt) {
            if(!(binaryExpression.getRight().getType().toString() == "int"
                    && binaryExpression.getLeft().getType().toString() == "int")) {
                errorMessages.add(binaryExpression.getLine() + "_unsupported operand type for "
                        + binaryExpression.getBinaryOperator().toString());
                binaryExpression.setType(new NoType());
            }
            else {
                binaryExpression.setType(new IntType());
            }
        }
    }

    @Override
    public void visit(ActorVarAccess actorVarAccess) {
        //TODO: implement appropriate visit functionality
    }

    @Override
    public void visit(Identifier identifier) {
        boolean foundIt = false;
        //knownActorNames
        //actorVarNames
        //msgHandlerArgsNames
        //msgHandlerLocalVarsNames = new ArrayList<String>();
        //varDeclarationNames = new ArrayList<String>();
        for (String str : knownActorNames) {
            if(str.split("_", 3)[1] == identifier.getName() && str.split("_", 3)[0] == currentActor){
                identifier.setType(returnType(str.split("_", 3)[2], identifier));
            }
            foundIt = true;
        }
        for (String str : actorVarNames) {
            if(str.split("_", 3)[1] == identifier.getName() && str.split("_", 3)[0] == currentActor){
                identifier.setType(returnType(str.split("_", 3)[2], identifier));
            }
            foundIt = true;
        }
        for (String str : msgHandlerArgsNames2) {
            if (str.split("_", 4)[1] == identifier.getName() && str.split("_", 4)[0] == currentHandler
                    &&str.split("_", 4)[2] == currentActor) {
                identifier.setType(returnType(str.split("_", 4)[3], identifier));
            }
            foundIt = true;
        }
        for (String str : msgHandlerArgsNames2) {
            if (str.split("_", 4)[1] == identifier.getName() && str.split("_", 4)[0] == currentHandler
                    &&str.split("_", 4)[3] == currentActor) {
                identifier.setType(returnType(str.split("_", 4)[2], identifier));
            }
            foundIt = true;
        }
        if(!foundIt) {
            errorMessages.add(identifier.getLine() + "_variable " + identifier.getName() +" is not declared");
        }
    }

    @Override
    public void visit(Block block) {
        blockCounter ++;
        int thisBlock = blockCounter;
        globalThisBlock = thisBlock;
        blocks.add("Block_" + blockCounter);
        for (Statement S : block.getStatements()) {
            S.accept(this);
        }
        blocks.add("End_" + thisBlock);
        globalThisBlock --;
    }

    @Override
    public void visit(Conditional conditional) {
        if(conditional.getExpression() != null) {
            if (conditional.getExpression().getType().toString() != "boolean") {
                errorMessages.add(conditional.getExpression().getLine() + "_" + "condition type must be Boolean");
            }
            conditional.getExpression().accept(this);
        }
        if(conditional.getThenBody() != null) {
            conditional.getThenBody().accept(this);
        }
        if(conditional.getElseBody() != null) {
            conditional.getElseBody().accept(this);
        }
    }

    @Override
    public void visit(For loop) {
        if(loop.getInitialize() != null) {
            loop.getInitialize().getlValue().accept(this);
            loop.getInitialize().getlValue().accept(this);
        }
        if(loop.getUpdate() != null) {
            loop.getUpdate().getlValue().accept(this);
            loop.getUpdate().getlValue().accept(this);
        }
        if(loop.getCondition() != null) {
            if (loop.getCondition().getType().toString() != "boolean") {
                errorMessages.add(loop.getCondition().getLine() + "_condition type must be Boolean");
            }
            loop.getCondition().accept(this);
        }
    }

    @Override
    public void visit(MsgHandlerCall msgHandlerCall) {
        if(msgHandlerCall.getInstance() instanceof Self) {
            boolean foundIt = false;
            for (String str : msgHandlerNames) {
                if(str.split("_", 2)[0] == currentActor
                        && str.split("_", 2)[1] == msgHandlerCall.getMsgHandlerName().getName()) {
                    foundIt = true;
                }
            }
            if(!foundIt) {
                errorMessages.add(msgHandlerCall.getMsgHandlerName().getLine() + "_"
                        + "there is no msghandler name " + msgHandlerCall.getMsgHandlerName().getName()
                        + " in actor " + currentActor);
            }
        }
        else if(msgHandlerCall.getInstance()  instanceof Sender) {
            if (msgHandlerCall.getMsgHandlerName().getName() == "initial") {
                errorMessages.add(msgHandlerCall.getMsgHandlerName().getLine() + "_" + "no sender in initial msghandler");
            }
        }
        else if(msgHandlerCall.getInstance() instanceof Identifier){
            if(!(actorNames.contains(((Identifier) msgHandlerCall.getInstance()).getName()))) {
                errorMessages.add(msgHandlerCall.getInstance().getLine() + "_actor "
                        + ((Identifier) msgHandlerCall.getInstance()).getName() + " is not declared"); //// Instance name ??
            }
            if(!(msgHandlerNames.contains(((Identifier) msgHandlerCall.getInstance()).getName() + "_" +
                    msgHandlerCall.getMsgHandlerName().getName()))) {
                errorMessages.add(msgHandlerCall.getMsgHandlerName().getLine()
                        + "_there is no msghandler name " + msgHandlerCall.getMsgHandlerName().getName()
                        + " in actor " + ((Identifier) msgHandlerCall.getInstance()).getName());
            }
            else {
                boolean flag = false;
                for (int i = 0; i < msgHandlerArgsNames.size(); i++) {
                    String argName = msgHandlerArgsNames.get(i);
                    if(msgHandlerCall.getMsgHandlerName().getName() == argName.split("_", 2)[0]
                            && ((Identifier) msgHandlerCall.getInstance()).getName() == argName.split("_", 2)[2]) {
                        int z = 0;
                        flag = true;
                        for (Expression exp : msgHandlerCall.getArgs()) {
                            argName = msgHandlerArgsNames.get(i+z);
                            if(msgHandlerCall.getMsgHandlerName().getName() == argName.split("_", 2)[0]
                                    && ((Identifier) msgHandlerCall.getInstance()).getName() == argName.split("_", 2)[2]) {
                                exp.accept(this);
                                if (!(exp.getType().toString() == argName.split("_", 3)[1])) {
                                    errorMessages.add(exp.getLine() + "_" + "knownactors does not match with definition");
                                }
                            }
                            else {
                                break;
                            }
                            z++;
                        }
                    }
                    if(flag){
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void visit(Print print) {

    }

    @Override
    public void visit(Assign assign) {
        assign.getlValue().accept(this);
        assign.getrValue().accept(this);
        if(isSubtype(assign.getrValue().getType(), assign.getlValue().getType())) {
            errorMessages.add(assign.getLine() + "_left side of assignment must be a valid lvalue");
        }
    }

    @Override
    public void visit(BooleanValue value) {
        value.setType(new BooleanType());
    }

    @Override
    public void visit(IntValue value) {
        value.setType(new IntType());
    }

    @Override
    public void visit(StringValue value) {
        value.setType(new StringType());
    }

    @Override
    public void visit(ArrayCall arrayCall) {
        arrayCall.setType(new ArrayType(1));
    }

    @Override
    public void visit(Self self) {
        return;
    }

    @Override
    public void visit(Sender sender) {
        return;
    }

    @Override
    public void visit(Break breakLoop) {
        return;
    }

    @Override
    public void visit(Continue continueLoop) {
        return;
    }
}
