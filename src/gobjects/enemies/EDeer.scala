package gobjects.enemies

import attack.TrailingAttack
import gobjects.Player
import main.Main


class EDeer(initX: Float, initY: Float, loc: String, initHealth: Int, initSx: Float = -1,
            initSy: Float = -1, scale: Float = Main.scale) extends Enemy(initX, initY, 56, 59, loc, initHealth, 20,
  range = 300, attackFreq = 2000, offsetX = 0, offsetY = 0) {
  lazy val duration = 7000
  accel = gobjects.friction + .1f

  override val rank = 3
  var jumpCount = 0
  val maxJump = 3

  /**
   * The action to performed when the enemy hits the ground. Usually affects velY.
   */
  override def hitGround(): Unit = {
    super.hitGround()
    jumpCount = 0
  }

  override def move(): Unit = {
    super.move()
    if (Player.y < y && jumpCount < maxJump && velY > -0.1f && Player.y < y + 100 && Player.y > y - 100) {
      velY = -4f
      jumpCount += 1
    }

  }

  override def initAttacks() {
    attacks += new TrailingAttack(damage, attackFreq, duration)(4000 < gobjects.milli - _)
  }

  override def copy = {
    new EDeer(x,y, loc, initHealth)
  }
}

object EDeer {
  def apply(x: Int, y: Int) = {
    new EDeer(x, y, "boss_deer/boss_deer_walk/boss_deer_walk.png", 50)
  }
}
