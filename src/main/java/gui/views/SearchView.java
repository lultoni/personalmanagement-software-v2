package gui.views;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.*;

public class SearchView extends View {

    public SearchView() {
        setLayout(new BorderLayout());

        // Panel für obere Suchleiste
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 🔍 Suchfeld mit Platzhaltertext
        JTextField searchField = new JTextField("Suche ...");
        searchField.setForeground(Color.GRAY);
        searchField.setMaximumSize(new Dimension(320, 30));

        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Suche ...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText("Suche ...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(10));

        // 🏢 Abteilungs-Dropdown mit Platzhalter „Abteilung“
        String[] departments = {
                "Abteilung",  // Platzhalter
                "Abteilung IT",
                "Abteilung Bau",
                "Abteilung Maschinen"
        };
        JComboBox<String> departmentDropdown = new JComboBox<>(departments);
        departmentDropdown.setMaximumSize(new Dimension(180, 30));
        departmentDropdown.setSelectedIndex(0);
        departmentDropdown.setForeground(Color.GRAY);

        // Reagiere auf Fokus (zum "Leeren" bei Klick)
        departmentDropdown.addPopupMenuListener(new PopupMenuListener() {
            boolean cleared = false;

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                if (!cleared && departmentDropdown.getSelectedIndex() == 0) {
                    cleared = true;
                    departmentDropdown.setSelectedIndex(-1); // Auswahl entfernen
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                if (departmentDropdown.getSelectedIndex() == -1) {
                    departmentDropdown.setSelectedIndex(0); // zurück zu "Abteilung"
                    departmentDropdown.setForeground(Color.GRAY);
                } else {
                    departmentDropdown.setForeground(Color.BLACK);
                }
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                // nichts tun
            }
        });

        searchPanel.add(departmentDropdown);
        searchPanel.add(Box.createHorizontalStrut(10));

        // ✅ Checkbox "Nur Abteilungsleiter"
        JCheckBox headOnlyCheckBox = new JCheckBox("Nur Abteilungsleiter");
        headOnlyCheckBox.setMaximumSize(new Dimension(160, 30));
        searchPanel.add(headOnlyCheckBox);

        // Panel hinzufügen
        add(searchPanel, BorderLayout.NORTH);

        // Platzhalter für Suchergebnisse
        JLabel placeholder = new JLabel("Suchergebnisse werden hier angezeigt.");
        placeholder.setHorizontalAlignment(SwingConstants.CENTER);
        add(placeholder, BorderLayout.CENTER);
    }
}
