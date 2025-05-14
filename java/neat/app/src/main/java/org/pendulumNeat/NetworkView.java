package org.pendulumNeat;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class NetworkView extends JPanel {

  private int w = 350;
  private int h = 200;
  private NeuralNet net;

  private ArrayList<NeuralNet.Node> sortedNodes;

  public NetworkView(NeuralNet net) {
    setPreferredSize(new Dimension(w, h));
    setBackground(Color.WHITE);
    setFocusable(true);

    this.net = net;
    sortedNodes = this.net.topoSort();
    NeuralNet.setLayers(sortedNodes);

  }

  public void updateView() {
    repaint();
  }

  @Override
  // TODO: definitely bad
  protected void paintComponent(Graphics g) {
    Map<Integer, ArrayList<NeuralNet.Node>> layerNums = new HashMap<>();
    sortedNodes = net.topoSort();
    System.out.println(sortedNodes.size());
    System.out.println(net.nodes.size());
    NeuralNet.setLayers(sortedNodes);
    Map<NeuralNet.Node, Point> nodePositions = new HashMap<>();

    // Put nodes into layers map
    for (NeuralNet.Node n : sortedNodes) {
      layerNums.putIfAbsent(n.layer, new ArrayList<NeuralNet.Node>());
      layerNums.get(n.layer).add(n);
    }

    // For every layer, put each node and its coords into nodePositions
    ArrayList<NeuralNet.Node> nodes;
    for (int l : layerNums.keySet()) {
      nodes = layerNums.get(l);
      for (int i = 0; i < nodes.size(); i++) {
        nodePositions.put(nodes.get(i),
            new Point(50 + 30 * (l + 1), (int) (h / 2 + (i - nodes.size() / 2.0) * 30.0)));
      }
    }

    // Draw things
    for (Map.Entry<NeuralNet.Node, Point> entry : nodePositions.entrySet()) {
      NeuralNet.Node n = entry.getKey();
      Point p = entry.getValue();

      g.setColor(Color.BLUE);
      for (NeuralNet.Connection c : n.outConnections) {
        g.drawLine(nodePositions.get(n).x, nodePositions.get(n).y, nodePositions.get(c.target).x,
            nodePositions.get(c.target).y);
      }

      g.setColor(Color.BLACK);
      if (net.inputs.contains(n))
        g.setColor(Color.RED);
      if (net.outputs.contains(n))
        g.setColor(Color.GREEN);
      g.fillOval(p.x - 5, p.y - 5, 10, 10);
    }
  }

  public static void createAndShowGUI(NeuralNet net) {
    JFrame frame = new JFrame("Drawing Window");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    NetworkView panel = new NetworkView(net);
    frame.add(panel);
    frame.pack();
    frame.setLocationRelativeTo(null); // Center the window
    frame.setVisible(true);

  }

}
