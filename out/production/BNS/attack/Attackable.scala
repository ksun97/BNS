package attack

import de.matthiasmann.twl.ProgressBar
import gobjects.GObject
import gobjects.enemies.Enemy
import org.newdawn.slick.Graphics
import org.newdawn.slick.geom.Rectangle

import scala.collection.mutable.ListBuffer

trait Attackable {
  this: GObject =>
  var maxHealth: Int
  var hlth: Int
  var gotAttackedTime = -1L

  val healthBar = new ProgressBar
  healthBar.setSize(rsx.toInt, (scale * 3).toInt)
  healthBar.setVisible(false)

  def dead = hlth <= 0

  def updateHealthBar() = {
    healthBar.setPosition(rx.toInt, ry.toInt)
    healthBar.setValue(health.toFloat / maxHealth)

  }

  def health_-=(that: Int) = {
    if (!invincible) {
      hlth -= that
      gotAttackedTime = gobjects.milli
    }
  }

  def health_+=(that: Int) = hlth += that

  def health = hlth

  def health_(that: Int) = hlth = that

  def invincible = false

  /**
   * Should be called in the update loop of an entity
   * @param g graphics object
   */
  final def renderDeath(g: Graphics) {
    if (hlth <= 0) {
      deathAction(g)
    }
  }

  final def updateDeath(collision: ListBuffer[Rectangle], enemies: ListBuffer[Enemy]) {
    if (hlth <= 0)
      deathAction(collision, enemies)
  }

  /**
   * The action that is performed upon death. For example exploding upon death. Do not use to render deathAnim!
   * @param g graphics
   */
  def deathAction(g: Graphics) = {
  }

  def deathAction(collision: ListBuffer[Rectangle], enemies: ListBuffer[Enemy]) = {}
}