package attack

import gobjects.GObject

class Bullet(val initx: Float, val inity: Float, sx: Float, sy: Float, var velX: Float, var velY: Float, loc: String
= "bullet_large.png") extends GObject(initx, inity, sx, sy, loc)

class ReboundingBullet(initx: Float, inity: Float, sx: Float, sy: Float, val maxReboundCount: Int = 5, velX: Float,
                       velY: Float, loc: String = "bullet_large.png") extends Bullet(initx, inity, sx, sy, velX,
  velY, loc) {
  var reboundCount = 0

  def tooMuchRebound = reboundCount > maxReboundCount
}

class TimedBullet(initx: Float, inity: Float, sx: Float, sy: Float, velX: Float, velY: Float, duration: Long, loc:
String = "bullet_large.png") extends Bullet(initx, inity, sx, sy, velX, velY, loc) {
  val attackTime = gobjects.milli

  def finished = gobjects.milli - attackTime > duration
}