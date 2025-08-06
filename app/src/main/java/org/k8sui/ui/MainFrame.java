package org.k8sui.ui;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main Frame
 *
 * @author djhelbert
 */
public class MainFrame extends JFrame implements ActionListener {

    private final static JMenuItem exitItem = new JMenuItem("Exit");
    private final static JMenuItem aboutItem = new JMenuItem("About...");
    private final static JMenuItem licenseItem = new JMenuItem("License");
    private final static MainPanel mainPanel = new MainPanel();

    /**
     * Constructor
     */
    public MainFrame() {
        super("k8sui 1.0.0");
        init();
    }

    private void init() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        aboutItem.setIcon(Util.getImageIcon("about.png"));
        licenseItem.setIcon(Util.getImageIcon("note.png"));

        final JMenu helpMenu = new JMenu("Help");
        helpMenu.add(aboutItem);
        helpMenu.add(licenseItem);

        licenseItem.addActionListener(this);
        aboutItem.addActionListener(this);

        final JMenuBar menuBar = new JMenuBar();
        final JMenu fileMenu = new JMenu("File");

        exitItem.setIcon(Util.getImageIcon("exit.png"));

        fileMenu.add(exitItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        exitItem.addActionListener(this);

        setJMenuBar(menuBar);
        setContentPane(mainPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(exitItem)) {
            exitItemAction();
        }
        if (e.getSource().equals(licenseItem)) {
            licenseAction();
        }
        if (e.getSource().equals(aboutItem)) {
            aboutAction();
        }
    }

    /**
     * Exit
     */
    private void exitItemAction() {
        System.exit(0);
    }

    /**
     * About Action
     */
    private void aboutAction() {
        Util.showInfo(getMainComponent(), "k8sui 1.0.0 Copyright 2025", "About");
    }

    /**
     * License Action
     */
    private void licenseAction() {
        try {
            final String text = Util.getFileText("license.txt");
            final JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout(5, 5));

            final JTextArea textArea = new JTextArea(text, 25, 80);
            textArea.setEditable(false);
            textArea.setFont(new Font("courier", Font.PLAIN, 12));

            panel.setBorder(new EtchedBorder());
            panel.add(BorderLayout.CENTER, new JScrollPane(textArea));

            Util.showInfo(this, panel, "License");
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    /**
     * Get Main Component
     *
     * @return Component
     */
    public static Component getMainComponent() {
        return mainPanel;
    }
}
