package ist.turingmachine;

import ist.turingmachine.gui.SyntaxHelperPanel;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class TM_Gui {
    public static boolean paused = false, stepped = false, step_used = false, run_used = false, reset_used = false;
    public static JFrame frame;
    public static JPanel panelMain = new JPanel(), panelCode = new JPanel(), panelInput = new JPanel(), panelTapes = new JPanel(new BorderLayout()), controlPanel = new JPanel(), panelMother = new JPanel(), panelSaveOpen = new JPanel(), panelNonDeterministic = new JPanel(), panelProgram = new JPanel(), panelCodeInput = new JPanel();
    public static JScrollPane scrollPane, inputScrollPane, frameScroll, logScroll, outputScroll;
    public static JTextArea paneCode, paneInput, paneLog;
    public static JButton decisionButton = new JButton(">"), clear = new JButton("Clear"), run = new JButton("Run"), pause = new JButton("Pause"), step = new JButton("Step"), reset = new JButton("Set"), open = new JButton("Open"), save = new JButton("Save"), copyOutput = new JButton("Copy output");
    public static JCheckBox run_faster = new JCheckBox("Run at full speed"), choose_steps = new JCheckBox("I decide");
    public static JSplitPane splitter, splitCode;
    public static JLabel counterField = new JLabel("0", SwingConstants.CENTER), counterLabelField = new JLabel("Steps", SwingConstants.CENTER);
    public static JTextField NonDeterministicField = new JTextField();
    public static JFileChooser fileChooser = new JFileChooser();
    public static JPopupMenu popup = new JPopupMenu();
    public static JOptionPane optionPane;
    public static JMenuItem item;
    public static DefaultCaret logCaret;
    public static JTextPane paneTapesOutput = new JTextPane(), decisionSequenceDescription = new JTextPane();
    public static Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 15), monospaced = new Font(Font.MONOSPACED, Font.PLAIN, 14), syntax = new Font(Font.SANS_SERIF, Font.PLAIN, 14), NonDeterministicFont = new Font(Font.MONOSPACED, Font.PLAIN, 20);
    public static Highlighter highlighter = paneTapesOutput.getHighlighter(), highlighter_decisions = NonDeterministicField.getHighlighter();
    public static Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(UIManager.getColor( "Component.accentColor" ));
    public static Thread t = new Thread();

    public static void Prepare() {
        frame = new JFrame("Turing Machine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        paneCode = new JTextArea(10, 2);
        paneCode.setEditable(true);
        paneCode.setFont(monospaced);

        scrollPane = new JScrollPane(paneCode);
        scrollPane.getViewport().setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        panelCodeInput.setLayout(new BorderLayout());
        panelCodeInput.add(scrollPane);

        paneInput = new JTextArea(10, 2);
        paneInput.setEditable(true);
        paneInput.setFont(monospaced);
        paneInput.setText("Initial tapes here, one in each line");

        paneLog = new JTextArea(10, 2);
        paneLog.setEditable(false);
        paneLog.setFont(monospaced);
        logScroll = new JScrollPane(paneLog);
        logScroll.getViewport().setBorder(null);
        logScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        logCaret = (DefaultCaret) paneLog.getCaret();
        logCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);


        SimpleAttributeSet attribs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_CENTER);
        StyleConstants.setFontFamily(attribs, Font.MONOSPACED);
        StyleConstants.setFontSize(attribs, 25);
        paneTapesOutput.setParagraphAttributes(attribs, false);
        paneTapesOutput.setEditable(false);
        paneTapesOutput.setFocusable(false);
        outputScroll = new JScrollPane(paneTapesOutput);
        outputScroll.getViewport().setBorder(null);
        outputScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        inputScrollPane = new JScrollPane(paneInput);
        inputScrollPane.getViewport().setBorder(null);
        inputScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        inputScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        choose_steps.setVisible(false);
        decisionSequenceDescription.setText("Non-deterministic Turing machines: The button '>' is to show/hide the decision sequence and also the box 'I decide'. \nThe decision sequence is the memory of choices made in each step between possible transitions. \nWhen the 'I decide' box is checked the user chooses the next transition, otherwise the program follows a breadth-first search.");
        decisionSequenceDescription.setOpaque(false);
        decisionSequenceDescription.setFont(syntax);

        NonDeterministicField.setPreferredSize(new Dimension(0, 0));
        NonDeterministicField.setFont(NonDeterministicFont);
        NonDeterministicField.setOpaque(false);
        NonDeterministicField.setText("Decision Sequence");
        NonDeterministicField.setHorizontalAlignment(SwingConstants.CENTER);
        panelNonDeterministic.add(decisionButton);
        panelNonDeterministic.add(decisionSequenceDescription);
        panelNonDeterministic.add(NonDeterministicField);
        panelNonDeterministic.add(choose_steps);
        panelNonDeterministic.setBorder(null);

        panelSaveOpen.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelSaveOpen.add(open, Component.CENTER_ALIGNMENT);
        panelSaveOpen.add(save, Component.CENTER_ALIGNMENT);

        panelProgram.setLayout(new BoxLayout(panelProgram, BoxLayout.Y_AXIS));

        panelProgram.setBorder(BorderFactory.createTitledBorder("Program"));
        panelProgram.add(panelCodeInput);
        panelProgram.add(panelNonDeterministic, BorderLayout.CENTER);
        panelProgram.add(panelSaveOpen, BorderLayout.CENTER);
        splitCode = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitCode.setRightComponent(new SyntaxHelperPanel());
        splitCode.setLeftComponent(panelProgram);
        splitCode.getLeftComponent().setMinimumSize(new Dimension(0, 310));
        splitCode.getRightComponent().setMinimumSize(new Dimension(0, 0));
        splitCode.setDividerSize(7);

        panelCode.setLayout(new BoxLayout(panelCode, BoxLayout.Y_AXIS));
        panelCode.add(splitCode);

        counterField.setBorder(null);
        counterField.setFont(font);
        counterField.setAlignmentX(Component.CENTER_ALIGNMENT);
        counterLabelField.setBorder(null);
        counterLabelField.setAlignmentX(Component.CENTER_ALIGNMENT);
        counterLabelField.setFont(font);


        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        controlPanel.add(run, BorderLayout.CENTER);
        controlPanel.add(run_faster, BorderLayout.CENTER);
        controlPanel.add(step, BorderLayout.CENTER);
        controlPanel.add(pause, BorderLayout.CENTER);
        controlPanel.add(reset, BorderLayout.CENTER);
        controlPanel.add(copyOutput, BorderLayout.CENTER);


        panelInput.setLayout(new BoxLayout(panelInput, BoxLayout.Y_AXIS));
        panelInput.setBorder(BorderFactory.createTitledBorder("Controls"));
        panelInput.add(counterLabelField);
        panelInput.add(counterField);
        panelInput.add(controlPanel);
        panelInput.add(inputScrollPane, BorderLayout.CENTER);
        panelInput.add(logScroll, BorderLayout.CENTER);

        pause.setText("Pause/Resume");

        panelTapes.setLayout(new BoxLayout(panelTapes, BoxLayout.Y_AXIS));
        panelTapes.setBorder(BorderFactory.createTitledBorder("Tapes"));
        panelTapes.add(outputScroll);
        panelTapes.setPreferredSize(new Dimension(0, 155));
        panelTapes.add(Box.createRigidArea(new Dimension(0, 10)));

        panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.X_AXIS));
        panelMain.add(panelCode);
        panelMain.add(panelInput);
        splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitter.setRightComponent(panelMain);
        splitter.setLeftComponent(panelTapes);
        splitter.getLeftComponent().setMinimumSize(new Dimension(0, 155));
        splitter.getRightComponent().setMinimumSize(new Dimension(0, 0));
        splitter.setDividerSize(8);

        frameScroll = new JScrollPane(splitter);
        frameScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        frameScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        popup.add(item = new JMenuItem("Cut"));
        item.addActionListener(e -> {
            paneCode.cut();
        });
        popup.add(item = new JMenuItem("Copy"));
        item.addActionListener(e -> {
            paneCode.copy();
        });
        popup.add(item = new JMenuItem("Paste"));
        item.addActionListener(e -> {
            paneCode.paste();
        });
        popup.add(item = new JMenuItem("Select all"));
        item.addActionListener(e -> {
            paneCode.selectAll();
        });
        popup.setBorder(new BevelBorder(BevelBorder.RAISED));

        frame.add(frameScroll);
    }


    public static void Props() {
        decisionButton.addActionListener(e -> {
            if (!NonDeterministicField.getSize().equals(new Dimension(0, 0))) {
                decisionSequenceDescription.setVisible(true);
                NonDeterministicField.setPreferredSize(new Dimension(0, 0));
                NonDeterministicField.setSize(new Dimension(0, 0));
                choose_steps.setVisible(false);
                //frame.pack();
            } else {
                decisionSequenceDescription.setVisible(false);
                NonDeterministicField.setPreferredSize(new Dimension(400, 40));
                NonDeterministicField.setSize(new Dimension(400, 40));
                choose_steps.setVisible(true);
                //frame.pack();
            }
        });
        paneInput.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (paneInput.getText().equals("Initial tapes here, one in each line"))
                    paneInput.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });
        NonDeterministicField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (NonDeterministicField.getText().equals("Decision Sequence")) {
                    NonDeterministicField.setOpaque(true);
                    NonDeterministicField.setHorizontalAlignment(SwingConstants.LEFT);
                    NonDeterministicField.setText("");
                }
                highlighter_decisions.removeAllHighlights();
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });
        clear.addActionListener(e -> {
            highlighter_decisions.removeAllHighlights();
            NonDeterministicField.setOpaque(true);
            NonDeterministicField.setHorizontalAlignment(SwingConstants.LEFT);
            NonDeterministicField.setText("");
        });
        paneTapesOutput.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (splitter.getDividerLocation() == splitter.getMinimumDividerLocation()) {
                        splitter.setDividerLocation(1.0d);
                        splitter.getLeftComponent().setMinimumSize(new Dimension());
                        splitter.getLeftComponent().setMinimumSize(new Dimension(frame.getWidth() - 20, 155));

                    } else {
                        splitter.resetToPreferredSizes();
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        panelProgram.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (splitCode.getDividerLocation() == splitCode.getMinimumDividerLocation()) {
                        splitCode.setDividerLocation(1.0d);
                        splitCode.getLeftComponent().setMinimumSize(new Dimension());
                        splitCode.getLeftComponent().setMinimumSize(new Dimension(700, 310));

                    } else {
                        splitCode.resetToPreferredSizes();
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        panelTapes.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (splitter.getDividerLocation() == splitter.getMinimumDividerLocation()) {
                        splitter.setDividerLocation(1.0d);
                        splitter.getLeftComponent().setMinimumSize(new Dimension());
                        splitter.getLeftComponent().setMinimumSize(new Dimension(frame.getWidth() - 20, 155));

                    } else {
                        splitter.resetToPreferredSizes();
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        NonDeterministicField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                char vchar = e.getKeyChar();
                if (!Character.isDigit(vchar) || vchar == '0' || vchar == KeyEvent.VK_BACK_SPACE || vchar == KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        paneCode.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.isPopupTrigger())
                    popup.show(paneCode, e.getX(), e.getY());
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger())
                    popup.show(paneCode, e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger())
                    popup.show(paneCode, e.getX(), e.getY());
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        open.addActionListener(e -> {
            if (fileChooser.showOpenDialog(open) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    paneCode.setText("");
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        paneCode.setText(paneCode.getText() + "\n" + line);
                    }
                } catch (IOException e1) {

                }
            }
        });
        save.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(save) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    PrintWriter writer = new PrintWriter(file);
                    for (String line : paneCode.getText().split("\\n"))
                        writer.println(line);
                    writer.close();
                } catch (FileNotFoundException e1) {

                }
            }
        });
        pause.addActionListener(e -> {
            logCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
            choose_steps.setEnabled(true);
            step.setEnabled(true);
            paneLog.setFocusable(true);
            NonDeterministicField.setFocusable(true);
            if (run_used) {
                if (paused) {
                    step.setEnabled(false);
                    paneLog.setFocusable(false);
                    NonDeterministicField.setFocusable(false);
                }
                paused = !paused;
            }
        });
        run.addActionListener(e -> {
            if (reset_used) {
                paneLog.setFocusable(false);
                NonDeterministicField.setFocusable(false);
                run_used = true;
                step.setEnabled(false);
                choose_steps.setEnabled(false);
            }
            stepped = false;
            step_used = false;
            paused = false;
        });
        step.addActionListener(e -> {
            paneLog.setFocusable(true);
            NonDeterministicField.setFocusable(true);
            logCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
            choose_steps.setEnabled(true);
            paused = !paused;
            stepped = !stepped;
            step_used = true;
            run_used = false;
        });
        reset.addActionListener(e -> {
            highlighter_decisions.removeAllHighlights();
            NonDeterministicField.setOpaque(true);
            NonDeterministicField.setHorizontalAlignment(SwingConstants.LEFT);
            NonDeterministicField.setText("");
            run_used = false;
            paused = true;
            if (t.isAlive()) {
                t.interrupt();
            }
            while (t.isAlive())
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e1) {

                }
            paneLog.setFocusable(true);
            NonDeterministicField.setFocusable(true);
            choose_steps.setEnabled(true);
            highlighter_decisions.removeAllHighlights();
            counterField.setText("0");
            step_used = false;
            reset_used = true;
            t = new Thread(new TM_Run());
            t.start();
        });

        copyOutput.addActionListener(e -> {
            paneTapesOutput.selectAll();
            paneTapesOutput.copy();
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
