package gobjects.enemies

import attack.ExplodingAttack
import gobjects.Player
import main.Main

class FlyingEnemy(initX: Float, initY: Float, initSx: Float, initSy: Float, initHealth: Int, damage: Int, loc: String =
"boss_pho_idle/boss_pho_idle.png", range: Float = 1000, attackFreq: Long = 2000, offsetX: Float = 0, offsetY: Float = 0, scale: Float = Main.scale)
  extends Enemy(initX, initY, initSx, initSy, loc, initHealth, damage, range, attackFreq, offsetX, offsetY, scale = scale) {
  override def gravity = 0f
  anim.setPingPong(true)

  val accelY = .01f
  accel = .1f
  canFly = true
  override val rank = 2

  override def initAttacks() = {
    attacks += new ExplodingAttack(damage, attackFreq, 1, 2)(4000 < gobjects.milli - _)
  }

  override def hitCeiling() = {}

  override def hitGround() = {}

  override def hitLeftWall() = {}

  override def hitRightWall() = {}

  override def onContact() = {
    Player.health_-=(damage)
  }

  override def move() = {
    super.move()
    if (y - Player.y + 10 < 0)
      velY += accelY
    else if (y - Player.y - 10 > 0)
      velY -= accelY
    else
      velY = 0
  }
  override def copy = {
    new FlyingEnemy(x,y, sx, sy, initHealth, damage, loc)
  }
}

object FlyingEnemy {
  def apply(x: Float, y: Float, initHealth: Int = 100, damage: Int = 20) = {
    new FlyingEnemy(x, y, 146, 96, initHealth, damage)
  }
}