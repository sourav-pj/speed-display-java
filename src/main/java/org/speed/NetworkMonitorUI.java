package org.speed;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

public class NetworkMonitorUI {
    private final JWindow overlay = new JWindow();
    private final NetworkModel model = new NetworkModel();
    private final UsageLogger logger = new UsageLogger();
    private int clickX, clickY;

    public static void main(String[] args) {
        new NetworkMonitorUI().start();
    }

    public void start() {
        logger.loadTodayStats();
        Runtime.getRuntime().addShutdownHook(new Thread(logger::saveToDisk));

        SwingUtilities.invokeLater(this::initUI);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long[] speeds = model.getCurrentSpeeds();
                logger.addUsage(speeds[0], speeds[1]);
                updateUI(speeds[0], speeds[1]);
            }
        }, 1000, 1000);
    }

    private void initUI() {
        overlay.setBackground(new Color(0,0,0,0));
        JPanel panel = createMainPanel();
        overlay.add(panel);
        overlay.pack();

        Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
        overlay.setLocation(scr.width - 150, scr.height - 100);
        overlay.setVisible(true);
    }

    private JPanel createMainPanel() {
        JPanel p = new JPanel(new GridLayout(2, 1)) {
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(120, 45));

        JLabel dLab = new JLabel("Down: 0 B/s"); dLab.setForeground(Color.GREEN);
        JLabel uLab = new JLabel("Up: 0 B/s"); uLab.setForeground(Color.CYAN);
        dLab.setName("down"); uLab.setName("up");

        p.add(dLab); p.add(uLab);
        setupMouseInteractions(p);
        return p;
    }

    private void setupMouseInteractions(JPanel p) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem stats = new JMenuItem("Statistics");
        stats.addActionListener(e -> new StatsWindow(logger).setVisible(true));

        JMenuItem reset = new JMenuItem("Reset Today");
        reset.addActionListener(e -> logger.resetToday());

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> System.exit(0));

        menu.add(stats); menu.add(reset); menu.addSeparator(); menu.add(exit);

        p.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) menu.show(e.getComponent(), e.getX(), e.getY());
                clickX = e.getX(); clickY = e.getY();
            }
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) menu.show(e.getComponent(), e.getX(), e.getY());
            }
        });
        p.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                overlay.setLocation(e.getXOnScreen() - clickX, e.getYOnScreen() - clickY);
            }
        });
    }

    private void updateUI(long down, long up) {
        SwingUtilities.invokeLater(() -> {
            JPanel p = (JPanel) overlay.getContentPane().getComponent(0);
            for (Component c : p.getComponents()) {
                if (c instanceof JLabel) {
                    if ("down".equals(c.getName())) ((JLabel)c).setText("Down: " + NetworkModel.formatSpeed(down) + "/s");
                    if ("up".equals(c.getName())) ((JLabel)c).setText("Up: " + NetworkModel.formatSpeed(up) + "/s");
                }
            }
            overlay.setAlwaysOnTop(false);
            overlay.setAlwaysOnTop(true);
            overlay.toFront();
        });
    }
}