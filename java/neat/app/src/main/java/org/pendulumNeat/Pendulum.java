package org.pendulumNeat;

public class Pendulum {

  public double pos;
  public double axisAcc, axisVel;
  public double angle = 6 * Math.PI / 4;
  public double rotVel;
  private final double gravityForce = 0.0005;
  public final int w = 800;
  public final int h = 600;
  public final int length = 200;
  public final double lengthNorm = (double) length / (w * 2);

  public Pendulum() {
  }

  public void updatePendulum() {
    // chatgpt physics yay

    pos += axisVel;
    if (pos < -0.66 || pos > 0.66) {
      rotVel -= (-axisVel / lengthNorm / 5) * Math.cos(angle + Math.PI / 2); // Euler step
      pos = Math.clamp(pos, -0.65999, 0.65999);
      axisVel = 0;
      axisAcc = 0;
    }

    axisVel += axisAcc;
    axisVel *= 0.97;
    rotVel *= 0.997;

    rotVel += (-gravityForce / lengthNorm) * Math.sin(angle + Math.PI / 2)
        - (axisAcc / lengthNorm) * Math.cos(angle + Math.PI / 2) / 5;
    angle += rotVel;
  }

}
