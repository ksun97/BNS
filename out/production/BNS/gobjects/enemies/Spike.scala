package gobjects.enemies


import gobjects.Player
import org.newdawn.slick.geom.Rectangle

import scala.collection.mutable.ListBuffer

/**
 * Created by Sriram on 4/16/2015.
 */
class Spike(x : Float, y : Float) extends Enemy(x,y, 32, 32, "blockspike.png",100, 10) {
  override def invincible = false
  override def gravity = 0
  override def update(collision: ListBuffer[Rectangle], enemies: ListBuffer[Enemy]) = {}
  override def onContact() = {
    Player.velX = if (Player.maxX - x > x - Player.x) 3f else -3f
    Player.velY = Player.jumpAccel

  }


}

class GroundSpike(x : Float, y : Float) extends Enemy(x,y, 32, 32, "groundspike.png", 100, 10, offsetY = 16) {
  override def invincible = false
  override def gravity = 0
  override def update(collision: ListBuffer[Rectangle], enemies: ListBuffer[Enemy]) = {}
  override def onContact() = {
    Player.velX = if (Player.maxX - x > x - Player.x) 3f else -3f
    Player.velY = Player.jumpAccel

  }
}
