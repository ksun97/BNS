package gobjects

import gamestates.NormalStage
import gobjects.enemies.Enemy
import org.newdawn.slick.{Graphics, Image}
import org.newdawn.slick.geom.Rectangle

import scala.collection.mutable.ListBuffer

object Clock extends GObject(NormalStage.map.width - 50, NormalStage.map.height - 50, 35, 35, "clock.png") {
  val Hand = new Image("hand0.png").getScaledCopy(scale)
  var angle : Float = 0
  val AngleDelta = .05f
  val FullYear  = 360
  val SeasonAmt = 5
  val Spring = 0
  val Summer = FullYear / SeasonAmt
  val Fall = 2 * FullYear / SeasonAmt
  val Winter = 3 * FullYear / SeasonAmt
  val Shop = 4 * FullYear / SeasonAmt

  override def update(collision: ListBuffer[Rectangle], enemies: ListBuffer[Enemy]): Unit = {
    if(angle >=  FullYear) angle = 0
    angle += AngleDelta
  }

  override def render(g : Graphics): Unit = {
    super.render(g)
    Hand.setRotation(angle)
    Hand.draw((NormalStage.map.width - 50 + sx/2) * scale, (NormalStage.map.height - 50)* scale)
  }

  def season = {
    if(angle < Summer)  0
    else if(angle < Fall )  1
    else if(angle < Winter)  2
    else if(angle < Shop)  3
    else if(angle < FullYear)  4
  }
}