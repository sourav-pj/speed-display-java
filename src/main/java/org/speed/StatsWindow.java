package org.speed;

import javax.swing.*;
import java.awt.*;

public class StatsWindow extends JFrame {
    public StatsWindow(UsageLogger logger) {
        setTitle("Usage Statistics");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(350, 200);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        JPanel p = new JPanel(new GridLayout(3, 1, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        p.add(new JLabel("Date: " + logger.getCurrentDay()));
        p.add(new JLabel("Total Down: " + NetworkModel.formatSpeed(logger.getDailyRx())));
        p.add(new JLabel("Total Up: " + NetworkModel.formatSpeed(logger.getDailyTx())));

        add(p, BorderLayout.CENTER);
        JButton close = new JButton("Close");
        close.addActionListener(e -> dispose());
        add(close, BorderLayout.SOUTH);
    }
}