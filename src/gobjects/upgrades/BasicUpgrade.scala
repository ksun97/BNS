package gobjects.upgrades

import attack.Weapon
import gobjects.{Player, GObject}
import gobjects.enemies.Enemy
import org.newdawn.slick.geom.Rectangle

import scala.collection.mutable.ListBuffer


trait Consumable

 class BasicUpgrade(x: Float, y: Float, sx: Float, sy: Float, loc: String) extends GObject(x, y, sx, sy, loc) {
  val rank : Int = 1
  override def update(collision: ListBuffer[Rectangle], enemies: ListBuffer[Enemy]): Unit = {
    if(coll.intersects(Player.coll)) {
      pickUp()
    }
  }

  def pickUp() : Unit = {}

  def drop() : Unit = {}

  def longTimeEffect(collision: ListBuffer[Rectangle], enemies: ListBuffer[Enemy]): Unit = {}

}

class WeaponUpgrade(x : Float, y : Float, sx: Float, sy : Float, loc : String, weapon : Weapon) extends BasicUpgrade(x,y,sx,sy, loc) {
  override val rank = weapon.rank
  override def pickUp() = {
    Player.weapon = weapon
  }

  override def drop() = {}

  override def longTimeEffect(collision: ListBuffer[Rectangle], enemies: ListBuffer[Enemy]) = {}
}

class SpecialUpgrade(x : Float, y : Float, sx: Float, sy : Float, loc : String, special : Weapon) extends WeaponUpgrade(x,y,sx,sy, loc, special) {
  override def pickUp() = {
    Player.special = special
  }
}