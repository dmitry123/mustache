package Common;

public class RootMachine extends StateMachine {

    private RootMachine() {
        super(null);
    }

    public static RootMachine getInstance() {
        return rootMachine;
    }

    private static RootMachine rootMachine = new RootMachine();
}
