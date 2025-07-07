package gui.elements;

import javax.swing.*;
import java.awt.*;

/**
 * Beinhaltet den Titel für den aktiven View.
 *
 * @author Elias Glauert
 * @version 1.0
 * @since 2025-07-07
 */
public class TitleBar extends JPanel {

    private String text;
    private JLabel main_label;

    public TitleBar() {
        setLayout(new GridLayout());
        main_label = new JLabel(text);
        add(main_label);
    }

    public void changeText(String text) {
        this.text = "Aktuelle Ansicht: " + text;
        main_label.setText(this.text);
    }

}
