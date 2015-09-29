package attack

class NoWallCollideStraightAttack(damage: Int, interval: Long, velX: Float, velY: Float = 0, bulletLoc: String =
"bullet_large.png")(changeStage: Long => Boolean) extends StraightAttack(damage, interval, velX, velY, bulletLoc)(changeStage) {
  override def collideWithWall(bullet: Bullet) = {}
}

class StraightAttack(damage: Int, interval: Long, velX: Float, velY: Float = 0, bulletLoc: String =
"bullet_large.png")(changeStage: Long => Boolean) extends Attack(damage, interval, velX, velY, bulletLoc)(changeStage) {
  override def updateBullets() = {
    super.updateBullets()
    bullets.foreach(b => b.x += b.velX)
  }
}

class CircularStraightAttack(damage: Int, interval: Long, velX: Float, velY: Float, bulletLoc: String =
"bullet_large.png")(changeStage: Long => Boolean)
  extends NoWallCollideStraightAttack(damage, interval, velX, velY, bulletLoc)(changeStage) {

  override def addBullet(x: Float, y: Float, dirX: Int, dirY: Int, loc: String =
  "bullet_large.png") {
    for (i <- -1 to 1; j <- -1 to 1)
      if (!(i == 0 && j == 0))
        super.addBullet(x, y, i, j)
  }

  override def updateBullets() = {
    bullets.foreach(b => b.y += b.velY)
    super.updateBullets()
  }
}

class TanAttack(damage: Int, fireInterval: Long, bulletVelX: Float, bulletVelY:
Float = 0, bulletLoc: String = "bullet_large.png")(changeStage: Long => Boolean) extends Attack(damage,
  fireInterval, bulletVelX, bulletVelY, bulletLoc)(changeStage) {
  override def updateBullets() {
    super.updateBullets()
    for (i <- 0 until bullets.size) {
      val b = bullets(i)
      b.velY += gobjects.gravity
      b.x += b.velX
      b.y = gobjects.oscillate(b.inity - 40 / 2d, b.inity + 40 / 2d, bulletVelY, -i).toFloat
    }
  }

  override def collideWithWall(bullet: Bullet) {
  }
}


class ReboundingAttack(damage: Int, fireInterval: Long, bulletVelX: Float = 0, bulletVelY: Float = 0, bulletLoc:
String = "bullet_large.png")(changeStage: Long => Boolean) extends NoWallCollideStraightAttack(damage, fireInterval,
  bulletVelX, bulletVelY, bulletLoc)(changeStage) {

  override def collideWithWall(bullet: Bullet) {
    bullet.velY *= -1
    bullet.velX *= -1
    if (bullet.asInstanceOf[ReboundingBullet].tooMuchRebound)
      removeBullet(bullet)
    else
      bullet.asInstanceOf[ReboundingBullet].reboundCount += 1
  }

  /**
   * Fires a bullet
   * @param x x pos of the bullet
   * @param y y pos of the bullet
   * @param dirX dirX of the bullet
   * @param dirY dirY of the bullet
   * @param loc Locations of the texture of the bullet
   */
  override def addBullet(x: Float, y: Float, dirX: Int, dirY: Int, loc: String) {
    bullets += new ReboundingBullet(x, y, bulletSx, bulletSy, 5, bulletVelX * dirX, bulletVelY * dirY, loc)
  }
}

class TrailingAttack(damage: Int, fireInterval: Long, bulletDuration: Long, bulletLoc: String = "bullet_large.png")
                    (changeStage: Long => Boolean) extends Attack(damage, fireInterval, 0f, 0, bulletLoc)(changeStage) {
  bulletSx = 20
  bulletSy = 20

  /**
   * The action to perform when a bullet collides with a wall or inanimate object. Usually the bullet is removed.
   * @param bullet bullet
   */
  override def collideWithWall(bullet: Bullet) {}

  /**
   * Updates the location of the bullets
   */
  override def updateBullets() {
    super.updateBullets()
    bullets = bullets.filter(!_.asInstanceOf[TimedBullet].finished)
  }

  /**
   * Fires a bullet
   * @param x x pos of the bullet
   * @param y y pos of the bullet
   * @param dirX dirX of the bullet
   * @param dirY dirY of the bullet
   * @param loc Locations of the texture of the bullet
   */
  override def addBullet(x: Float, y: Float, dirX: Int, dirY: Int, loc: String) = {
    bullets += new TimedBullet(x, y, bulletSx, bulletSy, bulletVelX * dirX, bulletVelY * dirY, bulletDuration, loc)
  }
}

class BombAttack(damage: Int, interval: Long, velX: Float, velY: Float, bulletLoc: String =
"bomb_.png")(changeStage: Long => Boolean) extends Attack(damage, interval, velX, velY, bulletLoc)(changeStage) {
  bulletSx = 25
  bulletSy = 25
  val bulletDuration = 2000

  /**
   * The action to perform when a bullet collides with a wall or inanimate object. Usually the bullet is removed.
   * @param bullet bullet
   */
  override def collideWithWall(bullet: Bullet) = {
    bullet.velX = 0
    bullet.velY = 0
  }

  /**
   * Fires a bullet
   * @param x x pos of the bullet
   * @param y y pos of the bullet
   * @param dirX dirX of the bullet
   * @param dirY dirY of the bullet
   * @param loc Locations of the texture of the bullet
   */
  override def addBullet(x: Float, y: Float, dirX: Int, dirY: Int, loc: String) = {
    bullets += new TimedBullet(x, y, bulletSx, bulletSy, bulletVelX * dirX, bulletVelY * dirY, bulletDuration, loc)
  }

  override def updateBullets() {
    super.updateBullets()
    for (bullet <- bullets.filter(_.asInstanceOf[TimedBullet].finished))
      for (i <- -1 to 1; j <- -1 to 1)
        if (!(i == 0 && j == 0))
          bullets += new Bullet(bullet.x, bullet.y, 15, 15, bulletVelX * i, bulletVelY * j, "bullet_large.png")

    bullets --= bullets.filter(_.isInstanceOf[TimedBullet]).filter(_.asInstanceOf[TimedBullet].finished)

  }


}

class ExplodingAttack(damage: Int, interval: Long, velX: Float, velY: Float, bulletLoc: String =
"bullet_large.png")(changeStage: Long => Boolean) extends Attack(damage, interval, velX, velY, bulletLoc)(changeStage) {
  bulletSx = 20
  bulletSy = 20

  override def collideWithWall(bullet: Bullet) = {
    if (bullet.sx == bulletSx) {
      super.collideWithWall(bullet)
      for (i <- -1 to 1; j <- -1 to 1)
        if (!(i == 0 && j == 0)) {
          bullets += new Bullet(bullet.x, bullet.y, bulletSx / 2, bulletSy / 2, bulletVelX * i, 2 * j, bulletLoc)
        }
    }
  }

  /**
   * Fires a bullet
   * @param x x pos of the bullet
   * @param y y pos of the bullet
   * @param dirX dirX of the bullet
   * @param dirY dirY of the bullet
   * @param loc Locations of the texture of the bullet
   */
  override def addBullet(x: Float, y: Float, dirX: Int, dirY: Int, loc: String) = super.addBullet(x, y, dirX, 0, loc)

  override def updateBullets() = {
    bullets.foreach(b => {
      b.x += b.velX
      b.y += b.velY
    })
    super.updateBullets()
  }
}

class NoAttack(changeStage: Long => Boolean) extends Attack(0, 0, 0)(changeStage) {
  /**
   * Fires a bullet
   * @param x x pos of the bullet
   * @param y y pos of the bulletg
   * @param dirX dirX of the bullet
   * @param dirY dirY of the bullet
   */
  override def fire(x: Float, y: Float, dirX: Int, dirY: Int): Unit = {}
}



