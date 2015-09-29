import gobjects.enemies.Enemy
import org.newdawn.slick.geom.Rectangle
import org.newdawn.slick.{Animation, Image}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

package object gobjects {
  val gravity = .2f
  val friction = .1f
  val platformTex = "testImg.png"
  val anims = mutable.HashMap[String, mutable.HashMap[String, Animation]]()

  def oscillate(pos1: Double, pos2: Double, speed: Double, off: Float = 0) = {
    pos1 + (math.tan(System.currentTimeMillis / 1000d * speed + off) / 2 + .5) * (pos2 - pos1)
  }

  def initAnim(loc: String, rsx: Float, rsy: Float, speed: Int, ex: String = "\\."): Animation = {
    if (anims.contains(loc)) {
      val sizes = anims(loc)
      if (!sizes.contains(rsx + " " + rsy)) {
        println("Resized " + loc + " original size: " +  sizes.keySet.head + " now: " + rsx + " " + rsy)
        sizes(rsx + " " + rsy) = changeAnimSize(sizes(sizes.keySet.head), rsx, rsy)
      }
    } else {
      anims(loc) = mutable.HashMap[String, Animation]()
      anims(loc)(rsx + " " + rsy) = initNewAnim(loc, rsx, rsy, speed, ex)
    }
    anims(loc)(rsx + " " + rsy)
  }

  def initNewAnim(loc: String, rsx: Float, rsy: Float, speed: Int, ex: String = "\\."): Animation = {
    val anim: Animation = new Animation
    var bool = true
    var locToParse = loc
    if(!loc.matches(".+\\.png"))
      locToParse = loc + "/" + loc + ".png"
    val extension = locToParse.split(ex)
    var count = 0

    while (bool) {
      try {
        anim.addFrame(new Image(extension(0) + count + "." + extension(1)).getScaledCopy(rsx.toInt, rsy.toInt), speed)
        count += 1
      } catch {
        case _: RuntimeException => {
          if(count > 0)
            println("Done loading " + loc + " Found : " + count)
          else
            System.err.println("Didn't find any " + loc)
        }
          bool = false
      }
    }
    anim
  }

  def changeAnimSize(anim: Animation, sx: Float, sy: Float) = {
    val tmpAnim = new Animation
    for (i <- 0 until anim.getFrameCount)
      tmpAnim.addFrame(anim.getImage(i).getScaledCopy(sx.toInt, sy.toInt), 250)
    tmpAnim
  }

  class MovingPlatform(x1: Float, y1: Float, x2: Float, y2: Float, sx: Float, sy: Float) extends GObject(
    x1, y1, sx, sy, platformTex) {
    var vel = 2

    override def update(collision: ListBuffer[Rectangle], enemies: ListBuffer[Enemy]) {
      x = oscillate(x1, x2, vel).toFloat
      y = oscillate(y1, y2, vel).toFloat
    }
  }

  def milli = System.currentTimeMillis
}
