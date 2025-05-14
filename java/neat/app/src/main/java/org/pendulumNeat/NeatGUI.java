package org.pendulumNeat;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class NeatGUI extends JFrame implements ActionListener {
  Buttons buttonsPanel;
  ArrayList<NeuralNet> netList;
  ArrayList<Double> netScores;

  public NeatGUI() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());
    buttonsPanel = new Buttons();

    add(buttonsPanel, BorderLayout.SOUTH);

    buttonsPanel.butManual.addActionListener(this);
    buttonsPanel.butTraining.addActionListener(this);
    buttonsPanel.butShowNet.addActionListener(this);

    pack();
    setVisible(true);

    initNets(10);
  }

  public void initNets(int numNets) {
    netList = new ArrayList<>();
    for (int i = 0; i < numNets; i++) {
      netList.add(new NeuralNet(3, 1));
    }
  }

  public void activateNets() {
    for (NeuralNet n : netList) {
      n.activate();

    }

  }

  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == buttonsPanel.butManual) {
      PendulumGUI.createAndShowGUI(new Pendulum());
    }
    if (e.getSource() == buttonsPanel.butShowNet) {
      NetworkView.createAndShowGUI(netList.getFirst());
    }
  }

  public static void main(String[] args) {
    if (System.getProperty("os.name").contains("Linux")) {
      System.setProperty("sun.java2d.opengl", "True");
      System.setProperty("sun.java2d.noddraw", "True");
      System.setProperty("sun.java2d.xrender", "False");
    }
    new NeatGUI();
  }

  public class Boxes extends JPanel {
    JCheckBox boxPlayLastGen = new JCheckBox("Play best of last generation");

  }

  public class Buttons extends JPanel {
    public JButton butTraining;
    public JButton butManual;
    public JButton butShowNet;

    public Buttons() {
      butTraining = new JButton("Start Training");
      butManual = new JButton("Start Manual");
      butShowNet = new JButton("Show Network");

      add(butTraining);
      add(butManual);
      add(butShowNet);
    }
  }

}
