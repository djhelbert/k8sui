/**
 * k8sui Copyright (C) 2025 This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.k8sui;

import io.kubernetes.client.openapi.ApiException;
import javax.swing.JFrame;
import javax.swing.UIManager;
import org.k8sui.ui.MainFrame;
import org.k8sui.ui.Util;

public class App {

  private static MainFrame frame;

  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
    } catch (Exception err) {
      err.printStackTrace();
    }

    frame = new MainFrame();
    frame.setSize(1200, 800);
    frame.setResizable(true);
    frame.setVisible(true);
    frame.setIconImage(Util.getImageIcon("wheel.png").getImage());

    Util.centerComponent(frame);
  }

  public static JFrame frame() {
    return frame;
  }
}
