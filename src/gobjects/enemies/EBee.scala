package gobjects.enemies

import attack.{NoAttack, ExplodingAttack}
import gobjects.Player
import main.Main


class EBee(initX: Float, initY: Float, loc: String, initHealth: Int, scale: Float = Main.scale)
  extends FlyingEnemy(initX, initY, 29, 36,initHealth, 5, loc) {
  accel = .2f
  override val accelY = .01f
  override val rank = 1

  override def initAttacks(): Unit = {
    attacks += new NoAttack(2000 < gobjects.milli - _)
  }
  override def copy = {
    new EBee(x,y, loc, initHealth, scale)
  }
  override def onContact() = {
    super.onContact()
    velX = if (maxX - Player.x > Player.x - x) 3f else -3f
    velY = accelY
  }
}

object EBee {
  def apply(x: Float, y: Float) = {
    new EBee(x, y, "mon_bee/mon_bee.png", 40)
  }

}