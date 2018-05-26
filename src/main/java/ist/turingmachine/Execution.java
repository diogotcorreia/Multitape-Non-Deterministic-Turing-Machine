package ist.turingmachine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//TODO: REMOVE SERIALIZABLE, review for loops (for each?)
public class Execution implements Serializable {
    private List<Tape> tapes;
    private List<State> states;

    public Execution(List<Tape> tapes, List<String> states) {
        this.tapes = tapes;
        createStates(states);
    }

    private void createStates(List<String> states) {
        this.states = new ArrayList<>();
        for (int i = 0; i < states.size(); i++) {
            this.states.add(new State(states.get(i)));
        }
        for (int i = 0; i < this.states.size(); i++) {
            this.states.get(i).setNextStates(this.states);
        }
    }

    public List<Tape> getTapes() {
        return tapes;
    }

    public List<State> getStates() {
        return states;
    }

    private void modifyContent(Integer tapeId, State state) {
        if (!state.getWrite().equals("*")) {
            int head = tapes.get(tapeId).getHead();
            tapes.get(tapeId).getContent().remove(head);
            tapes.get(tapeId).getContent().add(head, state.getWrite().charAt(tapeId));
        }
    }

    private void move(Integer tapeId, State state) {
        int head = tapes.get(tapeId).getHead();
        switch (state.getMove().toLowerCase()) {
            case "r":
                if (head == tapes.get(tapeId).getContent().size() - 1) {
                    tapes.get(tapeId).getContent().add('_');
                    tapes.get(tapeId).setHead(head + 1);
                    break;
                }
                tapes.get(tapeId).setHead(head + 1);
                break;
            case "l":
                if (head == 0) {
                    tapes.get(tapeId).getContent().add(0, '_');
                    break;
                }
                tapes.get(tapeId).setHead(head - 1);
                break;
        }
    }

    public void execute(State state) {
        List<State> executableNextStates;
        for (int i = 0; i < tapes.size(); i++) {
            modifyContent(i, state);
            move(i, state);
            executableNextStates = state.getExecutableNextStates(tapes);
            if (executableNextStates.size() != 0 && executableNextStates.get(0) != null) {
                tapes.get(i).setState(executableNextStates.get(0));
            }
        }
    }
}