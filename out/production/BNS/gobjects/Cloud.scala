package gobjects

import gobjects.enemies.Enemy
import org.newdawn.slick.geom.Rectangle

import scala.collection.mutable.ListBuffer

/**
 * Created by Sriram on 4/16/2015.
 */
class Cloud(x : Float, y : Float, sx : Float, sy : Float, loc : String) extends GObject(x,y, sx, sy,loc)  {
  override def update(collision: ListBuffer[Rectangle], enemies: ListBuffer[Enemy]): Unit = {}
}
