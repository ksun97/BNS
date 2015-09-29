package gobjects.enemies

import java.io.File

import attack._
import gamestates.NormalStage
import gobjects.{GObject, Player, SP}
import main.Main
import org.newdawn.slick.geom.Rectangle
import org.newdawn.slick.particles.ParticleIO
import org.newdawn.slick.{Color, Graphics}

import scala.collection.mutable.ListBuffer

class Enemy(initX: Float, initY: Float, sx: Float, sy: Float, loc: String, initHealth: Int, var damage: Int,
            var range: Float = 1000, var attackFreq: Long = 2000, offsetX: Float = 0, offsetY: Float = 0, initSx: Float =
-1, initSy: Float = -1, scale: Float = Main.scale) extends GObject(initX, initY, sx, sy, loc, offsetX,
  offsetY, initSx, initSy, scale) with Attackable with Attacker {
  target = Player
  var velX = 0f
  var velY = 0f
  var accel = .1f
  var maxVelX = 3f
  var canFly = false
  val contactApplyDuration = 2000
  val creationTime = gobjects.milli
  val rank = 4

  def score() = NormalStage.score += rank * 100

  override var maxHealth = initHealth
  override var hlth = maxHealth
  val emitter = ParticleIO.loadEmitter(new File("res/particles/blood.xml"))
  if (emitter != null) {
    emitter.setImageName("particles/blood0.png")
    emitter.setEnabled(false)
    emitter.xOffset.setMin(sx)
    emitter.xOffset.setMax(sx)
    emitter.yOffset.setMax(sy)
    emitter.yOffset.setMax(sy)
    NormalStage.Particles.addEmitter(emitter)
  }

  initAttacks()

  def spGiven = 1 //(damage * maxHealth) / Enemy.SpConstant

  override def deathAction(collision: ListBuffer[Rectangle], enemies: ListBuffer[Enemy]) {
    for (i <- 1 to spGiven) {
      val dir = if (i % 2 == 1) -1 else 1
      enemies += new SP(x, y, dir * (math.random * 6 + 2).toFloat, (math.random * -5).toFloat)
    }
  }

  /**
   * Movement based on the player's location
   */
  private def findPlayer() {
    if (withInRange) {
      move()
    }
  }

  /**
   * Movement. Override to define distinct movement, such as flying and such.
   */
  def move() = {
    if (midX + Player.sx / 2 < Player.midX) velX += accel
    else if (midX - Player.sx / 2 > Player.midX) velX -= accel
  }

  override def health_-=(that: Int) = {
    super.health_-=(that)
    emitter.setPosition(rx, ry, false)
    emitter.setEnabled(true)
    emitter.replay()
  }


  /**
   * Initialize all the attack stages
   */
  def initAttacks() {
    //    attacks += new TanAttack(damage, 500, 1, 5 / 2f)(
    //      4000 < gobjects.milli - _)
    attacks += new CircularStraightAttack(damage, 2000, 1, 1)(4000 < gobjects.milli - _)
    attacks += new NoWallCollideStraightAttack(damage, 2000, 3)(
      4000 < gobjects.milli - _)
    attacks += new TanAttack(damage, 2000, 1, 2)(4000 < gobjects.milli - _)
  }

  /**
   * Renders the enemy and its bullets
   * @param g Graphics
   */
  override def render(g: Graphics) {
    if (velX < 0) {
      renderFlipped = false
    }
    else if (velX > 0) {
      renderFlipped = true
    }
    if (velX == 0) {
      anim.stop()
      anim.setCurrentFrame(0)
    }
    else
      anim.start()
    super.render(g)
    renderBullets(g)
  }

  def gravity = gobjects.gravity

  /*
   * @param collision list of walls and other inanimate objects
   * @param enemies list of enemies ( specific to the Enemy)
   */
  override def update(collision: ListBuffer[Rectangle], enemies: ListBuffer[Enemy]) {
    super.update(collision, enemies)
    nextAttack()
    if (gOn) velY += gravity
    velX -= gobjects.friction * math.signum(velX)
    if (math.abs(velX) < .1) velX = 0
    if (velX > maxVelX) velX = maxVelX
    gOn = true
    findPlayer()
    x += velX
    if (!canFly) {
      for (c <- collision) {
        if (c.intersects(coll)) {
          x -= velX
          if (velX > 0) hitRightWall()
          else hitLeftWall()
        }
      }
    }
    y += velY
    if (!canFly) {
      for (c <- collision)
        if (c.intersects(coll)) {
          val tempSign = math.signum(velY)
          if (!canFly)
            while (c.intersects(coll)) y -= tempSign
          if (velY >= 0) hitGround()
          else hitCeiling()
        } else if (!canFly && velY >= 0 && c.getY - maxY < 1 && c.getY - maxY > 0 && x < c.getMaxX && maxX > c.getX)
          gOn = false
    }
    attack()
    updateBullets(collision, enemies)
  }

  def onContact() = {
    if (creationTime + contactApplyDuration > gobjects.milli) {
      Player.health_-=(damage)
    }
  }

  /**
   * Updates bullets :
   * 1. Checks for wall collision
   * 2. Checks for target collision
   * 3. Updates bullets
   * @param collision list of inanimate objects such as walls
   * @param enemies list of enemies
   */
  def updateBullets(collision: ListBuffer[Rectangle], enemies: ListBuffer[Enemy]) {
    wallCollide(collision)
    targetCollide(enemies, damage)
    attacks.foreach(_.updateBullets())
  }

  /**
   * Fires if within range.
   */
  def attack() = if (withInRange) fire(dir)

  /**
   * Moves to the next stage of attacks
   */
  def nextAttack() {
    if (attacks(stage).finished)
      if (attacks.size == stage + 1) {
        attacks = attacks.reverse
        stage = 0
      }
      else
        stage += 1
  }

  /**
   * Calculates if the enemy is in range
   * @return if the enemy is in range
   */
  def withInRange = math.abs(Player.x - x) < range && math.abs(Player.y - y) < range

  /**
   * The action to performed when the enemy hits the right wall. Usually affects velX.
   */
  def hitRightWall() {
    velX = 0
  }

  /**
   * The action to performed when the enemy hits the left wall. Usually affects velX.
   */
  def hitLeftWall() {
    velX = 0
  }

  /**
   * The action to performed when the enemy hits the ground. Usually affects velY.
   */
  def hitGround() {
    gOn = false
    velY = 0
  }

  /**
   * The action to performed when the enemy hits the ceiling. Usually affects velY.
   */
  def hitCeiling() {
    velY = 0
  }

  def copy = {
    new Enemy(x, y, sx, sy, loc, initHealth, damage, range, attackFreq, offsetX, offsetY)
  }
}

object Enemy {
  val SpConstant = 20 //A constant that is divided by to obtain the sp output of monsters
  //Sp is special points, points that are spent for both upgrades and special attacks

  def apply(x: Float, y: Float) = {
    new Enemy(x, y, 20, 40, "mon_sprout", 100, 10 /*offsetX = 3, offsetY = 9, initSx = 7, initSy = 31*/)
  }
}