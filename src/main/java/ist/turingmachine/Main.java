package ist.turingmachine;

import com.formdev.flatlaf.FlatDarkLaf;

public class Main {
    public static void main(String[] args)  {
        FlatDarkLaf.setup();

        TM_Gui.Prepare();
        TM_Gui.Props();
    }
}
