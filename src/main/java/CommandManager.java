import java.util.Stack;

public class CommandManager {
    private Stack<Command> cmdStack = new Stack<>();
    private Stack<Command> redoStack = new Stack<>();

    public void Execute(Command cmd){
        cmd.execute();
        cmdStack.push(cmd);
        redoStack.clear();
    }
    public void Undo(){
        if (cmdStack.size() > 0){
            Command cmd = cmdStack.pop();
            cmd.undo();
            redoStack.push(cmd);
        }
    }
    public void Redo(){
        if (redoStack.size() > 0){
            Command cmd = redoStack.pop();
            cmd.execute();
            cmdStack.push(cmd);
        }
    }
}
