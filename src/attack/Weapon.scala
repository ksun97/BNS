package attack

import gobjects.enemies.Enemy
import gobjects.{GObject, Player}
import org.lwjgl.opengl.GL11._
import org.newdawn.slick.Graphics
import org.newdawn.slick.geom.Rectangle

import scala.collection.mutable.ListBuffer

trait Weapon {
  val damage: Int
  val rank : Int

  def update(collision: ListBuffer[Rectangle], enemies: ListBuffer[Enemy])

  def render(g: Graphics)

  def fire(dir: Int)
}

class RangedWeapon(val bulletSpeed: Float, val damage: Int, val attackInterval: Long, loc: String) extends
GObject(Player.x + Player.sx / 2, Player.y, Weapon.Sx, Weapon.Sy, loc) with Weapon with Attacker {
  override val rank = 1

  override def update(collision: ListBuffer[Rectangle], enemies: ListBuffer[Enemy]) {
    x = Player.x
    y = Player.y
    wallCollide(collision)
    targetCollide(enemies, damage)
    attacks(stage).updateBullets()
  }

  override def render(g: Graphics) {
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
    renderBullets(g)
  }
}


class MeleeWeapon(val atkSpeed: Float, val damage: Int, loc: String) extends GObject(Player.x +
  Player.sx / 2, Player.y, Weapon.Sx, Weapon.Sy, loc) with Weapon {
  override val rank = 1

  private val maxRot = 100
  var isAtking = false
  var range = 33f

  def fire(dir: Int) {
    isAtking = true
  }

  override def update(collision: ListBuffer[Rectangle], enemies: ListBuffer[Enemy]) {
    x = Player.x
    y = Player.y
    if (isAtking) {
      if (math.abs(rot) >= maxRot) {
        isAtking = false
        rot = 0
      } else {
        rot += atkSpeed * dir
      }
    }
  }

  override def render(g: Graphics) {
    g.rotate(rx + rsx, ry + rsy, rot)
    g.drawAnimation(anim, rx, ry)
    g.resetTransform()
  }
}

object Weapon {
  val Sx: Float = 16
  val Sy: Float = 32
}





