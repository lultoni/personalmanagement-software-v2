package gui.elements;

import gui.GuiManager;
import gui.views.View;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Zeigt die aktuelle und vorherige/nächste Views als Breadcrumb an.
 * Der mittlere Slot ist der aktive View, fett dargestellt mit optionalen Trennstrichen.
 * Maximal 5 Einträge werden angezeigt.
 *
 * @author Elias Glauert
 * @version 1.2
 * @since 2025-07-29
 */
public class TitleBar extends JPanel {

    private final GuiManager guiManager;
    private final JComponent[] viewSlots = new JComponent[5];
    private final boolean highlightMiddle = true;

    public TitleBar(GuiManager guiManager) {
        this.guiManager = guiManager;

        setLayout(new GridLayout(1, 5));
        setOpaque(false);

        for (int i = 0; i < 5; i++) {
            if (i == 2 && highlightMiddle) {
                // mittlerer Slot mit Pipes
                JPanel middlePanel = new JPanel(new BorderLayout());
                middlePanel.setOpaque(false);

                JLabel leftPipe = new JLabel("|", SwingConstants.LEFT);
                JLabel middleLabel = new JLabel("", SwingConstants.CENTER);
                JLabel rightPipe = new JLabel("|", SwingConstants.RIGHT);

                // Konsistentes Styling
                Font font = new Font("SansSerif", Font.BOLD, 14);
                leftPipe.setFont(font);
                middleLabel.setFont(font);
                rightPipe.setFont(font);

                leftPipe.setForeground(Color.LIGHT_GRAY);
                rightPipe.setForeground(Color.LIGHT_GRAY);

                middlePanel.add(leftPipe, BorderLayout.WEST);
                middlePanel.add(middleLabel, BorderLayout.CENTER);
                middlePanel.add(rightPipe, BorderLayout.EAST);

                viewSlots[i] = middlePanel;
                add(middlePanel);
            } else {
                JLabel label = new JLabel("<html></html>", SwingConstants.CENTER);
                label.setFont(new Font("SansSerif", Font.PLAIN, 14));
                label.setForeground(Color.GRAY);
                label.setVerticalAlignment(SwingConstants.CENTER);
                viewSlots[i] = label;
                add(label);
            }
        }

        updateTitleBar();
    }

    public void updateTitleBar() {
        ArrayList<View> history = guiManager.getView_history();
        int currentIndex = guiManager.getCurrentViewIndex();

        if (history == null || history.isEmpty()) {
            System.out.println("[TitleBar] Kein Eintrag in der View-History.");
            for (JComponent comp : viewSlots) {
                if (comp instanceof JLabel label) {
                    label.setText("");
                } else if (comp instanceof JPanel panel) {
                    JLabel label = (JLabel) ((BorderLayout) panel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
                    label.setText("");
                }
            }
            return;
        }

        System.out.printf("[TitleBar] Anzeige aktualisiert. Aktueller Index: %d%n", currentIndex);

        for (int slot = 0; slot < 5; slot++) {
            int relativeIndex = slot - 2;
            int historyIndex = currentIndex + relativeIndex;

            if (historyIndex >= 0 && historyIndex < history.size()) {
                View view = history.get(historyIndex);
                String viewName = (view != null && view.getView_name() != null)
                        ? view.getView_name()
                        : "<unbenannt>";

                String htmlText = "<html><div style='text-align: center;'>" + viewName + "</div></html>";

                if (slot == 2 && highlightMiddle) {
                    JPanel panel = (JPanel) viewSlots[slot];
                    JLabel label = (JLabel) ((BorderLayout) panel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
                    label.setText(htmlText);
                    label.setForeground(Color.BLACK);
                    label.setFont(new Font("SansSerif", Font.BOLD, 14));
                } else {
                    JLabel label = (JLabel) viewSlots[slot];
                    label.setText(htmlText);
                    label.setFont(new Font("SansSerif", Font.PLAIN, 14));

                    int fade;
                    if (Math.abs(relativeIndex) == 1) {
                        fade = 97;
                    } else if (Math.abs(relativeIndex) == 2) {
                        fade = 218;
                    } else {
                        fade = 0; // unreachable
                    }
                    label.setForeground(new Color(fade, fade, fade));
                }

            } else {
                if (slot == 2 && highlightMiddle) {
                    JPanel panel = (JPanel) viewSlots[slot];
                    JLabel label = (JLabel) ((BorderLayout) panel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
                    label.setText("");
                } else {
                    JLabel label = (JLabel) viewSlots[slot];
                    label.setText("");
                }
            }
        }

        revalidate();
        repaint();
    }
}
