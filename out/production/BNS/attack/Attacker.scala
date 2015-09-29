package attack

import gobjects.{SP, Player, GObject}
import gobjects.enemies.Enemy
import org.newdawn.slick.Graphics
import org.newdawn.slick.geom.Rectangle

import scala.collection.mutable.ListBuffer

/**
 * Meant to be inherited only by Enemies, this trait manages attack stages, bullets and their interaction with the
 * environment
 */
trait Attacker {
  this: GObject =>
  var target: Attackable = _
  /**
   * List of attack stage
   */
  var attacks = new ListBuffer[Attack]
  var stage = 0

  /**
   * Manages collision with walls and such
   * @param collision list of walls and such
   */
  def wallCollide(collision: ListBuffer[Rectangle]) {
    for (i <- collision)
      for (attack <- attacks)
        for (bullet <- attack.bullets)
          if (bullet.coll.intersects(i))
            attack.collideWithWall(bullet)
  }

  /**
   * Manages collision with the target, including damage and removal of bullets
   * @param enemies list of enemies
   * @param damage damage done
   */
  def targetCollide(enemies: ListBuffer[Enemy], damage: Int) {
    if (Player == target) {
      val temp = new ListBuffer[Bullet]
      for (attack <- attacks)
        temp ++= attack.bullets.filter(_.coll.intersects(Player.coll))
      temp.filter(!Player.invincible && _.coll.intersects(Player.coll)).foreach(attacks(stage).damage(Player, _))
      temp.foreach(b => attacks(stage).removeBullet(b))
    }
    else {
      for (enemy <- enemies)
        enemy match {
          case sp : SP => //sp.onContact()
          case _ => attacks(stage).bullets.filter(_.coll.intersects(enemy.coll)).foreach(attacks(stage).damage(enemy, _))
        }
    }
  }

  /**
   * Fires a bullet
   * @param dir dir
   */
  def fire(dir: Int) {
    attacks(stage).fire(x + sx / 2, y + sy / 2, dir)
  }

  /**
   * Renders bullets
   * @param g Graphics
   */
  def renderBullets(g: Graphics) {
    attacks.foreach(_.bullets.foreach(_ render g))
  }

}
