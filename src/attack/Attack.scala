package attack

import gamestates.NormalStage

import scala.collection.mutable.ListBuffer

/**
 * An individual attack stage. Manages bullets mainly.
 * @param damage damage
 * @param fireInterval time between the firing of bullets
 * @param bulletVelX initial velX of the bullets
 * @param bulletVelY initial velY of the bullets
 * @param bulletLoc location of the texture for the bullets
 * @param changeStage function that notifies this stage
 */
case class Attack(damage: Int, fireInterval: Long, bulletVelX: Float, bulletVelY:
Float = 0, bulletLoc: String = "bullet_large.png")(changeStage: Long => Boolean) {

  var bullets = new ListBuffer[Bullet]
  var fireTime = -1L
  var startTime = -1L
  var bulletSx = 15
  var bulletSy = 15
  var gravity = false
  /**
   * Updates the location of the bullets
   */
  def updateBullets() {
    bullets.foreach(b => {
      if(gravity) b.velY += gobjects.gravity
      b.x += b.velX
      b.y += b.velY
    })
    bullets = bullets.filterNot(b => b.x < 0 || b.x > NormalStage.map.width || b.y < 0 || b.y > NormalStage.map.height)
  }

  /**
   * Checks if an attack stage is finished
   * @return if an attack stage is finished
   */
  def finished = {
    val fin = changeStage(startTime)
    if (fin) startTime = -1L
    fin
  }

  /**
   * Does damage to the target and removes the bullet
   * @param at target
   * @param bullet bullet
   */
  def damage(at: Attackable, bullet: Bullet) {
    at.health_-=(damage)
    at.healthBar.setVisible(true)
    removeBullet(bullet)
  }

  /**
   * Removes a bullet
   * @param b bullet
   */
  def removeBullet(b: Bullet) {
    bullets -= b
  }

  /**
   * The action to perform when a bullet collides with a wall or inanimate object. Usually the bullet is removed.
   * @param bullet bullet
   */
  def collideWithWall(bullet: Bullet) {
    removeBullet(bullet)
  }

  /**
   * Fires a bullet
   * @param x x pos of the bullet
   * @param y y pos of the bullet
   * @param dirX dirX of the bullet
   * @param dirY dirY of the bullet
   */
  def fire(x: Float, y: Float, dirX: Int, dirY: Int = 1) {
    if (startTime == -1)
      startTime = gobjects.milli
    if (fireTime == -1 || gobjects.milli - fireTime > fireInterval) {
      fireTime = gobjects.milli
      addBullet(x, y, dirX, dirY)
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
  def addBullet(x: Float, y: Float, dirX: Int, dirY: Int = 1, loc: String = bulletLoc) {
    bullets += new Bullet(x, y, bulletSx, bulletSy, bulletVelX * dirX, bulletVelY * dirY, loc)
  }
}



