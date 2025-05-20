package org.pendulumNeat;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class PendulumGUI extends JPanel {
  Pendulum pendulum;
  private Timer timer;
  private ArrayList<Integer> keysPressed = new ArrayList<Integer>();
  private int w, h;

  public PendulumGUI(Pendulum pendulum) {

    this.pendulum = pendulum;

    w = pendulum.w;
    h = pendulum.h;

    // Set panel size
    setPreferredSize(new Dimension(w, h));
    setBackground(Color.WHITE);
    initKeys(keysPressed);
    setFocusable(true);

    SwingUtilities.invokeLater(() -> requestFocusInWindow());

    timer = new Timer(16, a -> {
      loopUpdate();
      repaint();
    });
    timer.start();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    g.setColor(Color.BLACK);
    int xPos = (int) (w / 2 + pendulum.pos * (w / 2));
    int yPos = (int) (h / 2);
    g.drawLine(xPos, yPos, (int) (pendulum.length * Math.cos(pendulum.angle) + xPos),
        (int) (pendulum.length * Math.sin(-pendulum.angle) + yPos));

    g.setColor(Color.RED);
    g.fillOval(xPos + (int) (pendulum.length * Math.cos(pendulum.angle)) - 25,
        yPos - (int) (pendulum.length * Math.sin(pendulum.angle)) - 25, 50, 50);
  }

  public void loopUpdate() {
    getKeyPresses();
    pendulum.updatePendulum();
    System.out.println("Acc: " + pendulum.axisAcc);
    System.out.println("Vel: " + pendulum.axisVel);
    System.out.println("Pos: " + pendulum.pos);
    System.out.println("RotVel: " + pendulum.rotVel);

    System.out.println();
  }

  public void getKeyPresses() {
    if (keysPressed.indexOf(KeyEvent.VK_LEFT) != -1) {
      pendulum.axisAcc = -0.001;
    } else if (keysPressed.indexOf(KeyEvent.VK_RIGHT) != -1) {
      pendulum.axisAcc = 0.001;
    } else {
      pendulum.axisAcc = 0;
    }
  }

  public static void createAndShowGUI(Pendulum pendulum) {
    JFrame frame = new JFrame("Drawing Window");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);

    PendulumGUI panel = new PendulumGUI(pendulum);
    frame.add(panel);
    frame.pack();
    frame.setLocationRelativeTo(null); // Center the window
    frame.setVisible(true);
  }

  private void initKeys(ArrayList<Integer> keyList) {
    this.addKeyListener(new KeyListener() {

      public void keyTyped(KeyEvent e) {
      }

      public void keyReleased(KeyEvent e) {
        keyList.remove((Object) e.getKeyCode());
      }

      public void keyPressed(KeyEvent e) {
        if (!keyList.contains((Object) e.getKeyCode()))
          keyList.add(e.getKeyCode());
      }
    });

  }

  public static void main(String[] args) {
    // Needed to make anything work for some reason
    if (System.getProperty("os.name").contains("Linux")) {
      System.setProperty("sun.java2d.opengl", "True");
      System.setProperty("sun.java2d.noddraw", "True");
      System.setProperty("sun.java2d.xrender", "False");
    }
  }
}
