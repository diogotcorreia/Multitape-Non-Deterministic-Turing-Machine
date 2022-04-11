package ist.turingmachine.gui;

import javax.swing.*;
import java.awt.*;

public class SyntaxHelperPanel extends JPanel {

    private static final Font PANEL_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);

    public SyntaxHelperPanel() {
        final JTextArea textPane = new JTextArea();
        textPane.setEditable(false);
        textPane.setFont(PANEL_FONT);
        textPane.setText(
                """
                        Syntax inspired by Anthony Morphett @ http://morphett.info/turing/turing.html
                        \u2020  Each line should contain one tuple of the form '<current state> <current symbol> <new symbol> <direction> <new state>'.
                        \u2020  You can use any number or word for <current state> and <new state>. State labels are case-sensitive.
                        \u2020  You can use any character for <current symbol> and <new symbol>, or '_' to represent blank. Symbols are case-sensitive.
                        \u2020  <direction> should be 'l', 'r' or '*', denoting 'move left', 'move right' or 'do not move', respectively.
                        \u2020  Anything after a ';' is a comment and is ignored.
                        \u2020  '*' can be used as a wildcard in <current symbol> to match any character.
                        \u2020  The machine halts when it reaches a state 'halt', 'halt-reject' or 'halt-accept'.
                        \u2020  The sequence in Decision Sequence will be strictly followed if mentioned.

                        Copyright \u00a9 2016 Kyrylo Yefimenko""");
        textPane.setOpaque(false);

        this.setBorder(BorderFactory.createTitledBorder( "Syntax"));
        this.add(textPane);
    }
}
