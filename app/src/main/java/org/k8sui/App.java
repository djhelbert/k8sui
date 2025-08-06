package org.k8sui;

import io.kubernetes.client.openapi.ApiException;
import org.k8sui.ui.MainFrame;
import org.k8sui.ui.Util;

import javax.swing.*;

public class App {
    private static MainFrame frame;

    public static void main(String[] args) throws ApiException {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception err) {
            err.printStackTrace();
        }

        frame = new MainFrame();
        frame.setSize(1000, 800);
        frame.setResizable(true);
        frame.setVisible(true);
        frame.setIconImage(Util.getImageIcon("wheel.png").getImage());

        Util.centerComponent(frame);
    }

    public static JFrame frame() {
        return frame;
    }
}
