package org.pendulumNeat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class NeuralNet {
  public ArrayList<Node> inputs;
  public ArrayList<Node> outputs;
  public ArrayList<Node> nodes;

  public NeuralNet(int inputs, int outputs) {
    this.inputs = new ArrayList<>();
    this.outputs = new ArrayList<>();
    nodes = new ArrayList<>();
    for (int i = 0; i < inputs; i++) {
      this.inputs.add(new Node(n -> Math.tanh(n.value)));
    }
    for (int i = 0; i < outputs; i++) {
      this.outputs.add(new Node(n -> Math.tanh(n.value)));
    }

    nodes.addAll(this.inputs);
    nodes.addAll(this.outputs);

    for (Node n1 : this.inputs) {
      for (Node n2 : this.outputs) {
        n1.addChild(n2);
      }
    }
    nodes = topoSort();
  }

  public void activate() {
    for (Node n : nodes) {
      n.activate();
    }
  }

  public void mutate() {
    int rand = ((int) (Math.random() * 4));
    switch (rand) {
      case 0:
        break;

      case 1:
        this.addRandomConnection();
        break;

      case 2:
        this.addRandomNode();
        break;

      case 3:
        this.changeRandomWeight();
        break;

      default:
        break;
    }
  }

  /**
   * Adds a random connection between two random nodes
   * Requires layers to be set!!!!!
   * chatgpt method lol
   */
  public void addRandomConnection() {
    ArrayList<Node> toNodes = new ArrayList<>();
    ArrayList<Node> fromNodes = new ArrayList<>();

    for (Node n : nodes) {
      if (!inputs.contains(n))
        toNodes.add(n);
      if (!outputs.contains(n))
        fromNodes.add(n);
    }

    ArrayList<Node[]> validPairs = new ArrayList<>();
    for (Node n1 : toNodes) {
      for (Node n2 : fromNodes) {
        if (n1 == n2)
          continue;
        if (n1.hasConnectionTo(n2) || n2.hasConnectionTo(n1))
          continue;
        if (n1.layer >= n2.layer)
          continue; // must be forward for acyclicity

        validPairs.add(new Node[] { n1, n2 });
      }
    }
    if (validPairs.isEmpty()) {
      System.err.println("No valid connection pairs available to add.");
      return;
    }

    Node[] pair = validPairs.get((int) (Math.random() * validPairs.size()));
    pair[0].addChild(pair[1]);
  }

  public void changeRandomWeight() {
    Node n;
    while (true) {
      n = nodes.get((int) (Math.random() * nodes.size()));
      if (!n.outConnections.isEmpty()) {
        Connection c = n.outConnections.get((int) (Math.random() * n.outConnections.size()));
        c.setWeight(Math.random() * 3);
      }
    }
  }

  public void addRandomNode() {
    int randNode = ((int) (Math.random() * nodes.size()));
    // Runs until a node with at least 1 outgoing connection is chosen
    while (nodes.get(randNode).outConnections.isEmpty())
      randNode = ((int) (Math.random() * nodes.size()));

    Node node = nodes.get(randNode);
    int randConnection = ((int) (Math.random() * node.outConnections.size()));
    Connection con = node.outConnections.get(randConnection);
    Node newNode = new Node(n -> Math.tanh(n.value));

    nodes.add(newNode);
    node.splitConnection(con, newNode);

  }

  /**
   * Requires param sortedNodes to be topologically sorted!
   */
  public static void setLayers(ArrayList<Node> sortedNodes) {
    ArrayList<Integer> parentLayers;
    for (Node n : sortedNodes) {
      parentLayers = new ArrayList<>();
      if (n.parents.isEmpty())
        n.layer = 0;
      else {
        for (Node p : n.parents)
          parentLayers.add(p.layer);
        n.layer = parentLayers.stream().max(Integer::compare).get() + 1; // ?????
      }
    }
  }

  public ArrayList<Node> topoSort() {
    ArrayList<Node> queue = new ArrayList<>();
    ArrayList<Node> sorted = new ArrayList<>();
    Map<Node, Integer> edgeTemp = new HashMap<>();
    Node node;
    System.out.println("Toposort nodes len: " + nodes.size());

    for (Node n : nodes) {
      edgeTemp.put(n, n.parents.size());
      if (n.parents.size() == 0)
        queue.add(n);
    }
    while (queue.size() != 0) {
      node = queue.remove(0);
      sorted.addLast(node);

      for (Connection c : node.outConnections) {
        edgeTemp.put(c.target, edgeTemp.get(c.target) - 1);

        if (edgeTemp.get(c.target) == 0) {
          queue.addLast(c.target);
        }
      }

    }
    System.out.println("Sorted length: " + sorted.size());
    return sorted;

  }

  public static class Node {

    public ArrayList<Connection> outConnections;
    public ArrayList<Node> parents;
    public double value;
    public int layer;
    Function<Node, Double> activationFunction;

    public Node(Function<Node, Double> activationFunction) {
      outConnections = new ArrayList<>();
      parents = new ArrayList<>();
      this.setActivationFunction(activationFunction);
    }

    public void addChild(Node child) {
      outConnections.add(new Connection(child));
      child.parents.add(this);
    }

    public void addChild(Node child, double weight) {
      outConnections.add(new Connection(child, weight));
      child.parents.add(this);
    }

    public void activate() {
      this.value = activationFunction.apply(this);
      for (Connection c : outConnections)
        c.activate(value);
    }

    /**
     * Splits a connection into two connections with node n in between
     * Make sure to add n to nodes!!!
     *
     * @param c The connection to split
     * @param n The node to place
     */
    public boolean splitConnection(Connection c, Node n) {
      if (!outConnections.remove(c))
        return false;
      else
        c.target.parents.remove(this);
      addChild(n);
      n.addChild(c.target);
      return true;
    }

    public void setActivationFunction(Function<Node, Double> function) {
      activationFunction = function;
    }

    public boolean hasConnectionTo(Node n) {
      for (Connection c : outConnections) {
        if (c.target == n)
          return true;
      }
      return false;
    }

  }

  public static class Connection {
    public Node target;
    public Double weight;

    public Connection(Node node) {
      target = node;
      weight = Math.random() * 0.25;
    }

    public Connection(Node node, double weight) {
      target = node;
      this.weight = weight;
    }

    public void activate(double value) {
      target.value += value * weight;
    }

    public void setWeight(double weight) {
      this.weight = weight;
    }
  }

}
