package gobjects

import gobjects.enemies.Enemy
import main.Main
import org.lwjgl.opengl.GL11._
import org.newdawn.slick.Graphics
import org.newdawn.slick.geom.Rectangle

import scala.collection.mutable.ListBuffer

class GObject(initRx: Float, initRy: Float, initRSx: Float, initRSy: Float, val loc: String, var offsetX: Float = 0,
              var offsetY: Float = 0, initSx: Float = -1, initSy: Float = -1, var rot: Float = 0, var scale: Float = Main.scale) {

  val coll = new Rectangle(initRx + offsetX, initRy + offsetY, if (initSx == -1) initRSx else initSx, if (initSy == -1)
    initRSy
  else initSy)
  var rsx = initRSx * scale
  var rsy = initRSy * scale
  var rx = initRx * scale
  var ry = initRy * scale
  val anim = gobjects.initAnim(loc, rsx, rsy, 250)
  anim.start()
  var gOn = true
  var renderFlipped = false


  def dir = if (renderFlipped) 1 else -1

  def render(g: Graphics) = {
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
    //
    //    if (anim.getFrameCount < 1)
    //      g.fillRect(x, y, sx, sy)
    //    else {
    if (!renderFlipped)
      anim.draw(rx, ry, rsx, rsy)
    else
      anim.draw(rx + rsx, ry, -rsx, rsy)

  }

  def update(collision: ListBuffer[Rectangle], enemies: ListBuffer[Enemy]) = {
  }

  def sx = coll.getWidth

  def sy = coll.getHeight

  def midX = (x + sx) / 2

  def midY = (y + sy) / 2

  def midRX = (rx + rsx) / 2

  def midRY = (ry + rsy) / 2

  def sy_=(that: Float) = {
    val diff = that - sy
    coll.setHeight(that)
    rsy += diff * scale
    ry -= diff * scale
    y -= diff
  }

  def sy_+=(that: Float) = sy = sy + that

  def sy_-=(that: Float) = sy = sy - that

  def sx_=(that: Float) = {
    val diff = that - sx
    coll.setX(that)
    rsx += diff * scale
  }

  def sx_+=(that: Float) = sx = sx + that

  def sx_-=(that: Float) = sx = sx - that

  def x_+=(that: Float) = x = x + that

  def y_+=(that: Float) = y = y + that

  def x_-=(that: Float) = x = x - that

  def x = coll.getX

  def x_=(that: Float) = {
    coll.setX(that)
    rx = (that - offsetX) * scale
  }

  def y_-=(that: Float) = y = y - that

  def y = coll.getY

  def y_=(that: Float) = {
    coll.setY(that)
    ry = (that - offsetY) * scale
  }

  def maxX = coll.getMaxX

  def maxY = coll.getMaxY
}

object GObject {
  def apply(x: Float, y: Float, sx: Float, sy: Float, loc: String, rot: Float = 0) = {
    new GObject(x, y, sx, sy, loc, rot)
  }

}

