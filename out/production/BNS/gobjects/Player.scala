package gobjects

import attack._
import gamestates.NormalStage
import gobjects.enemies.Enemy
import gobjects.upgrades.BasicUpgrade
import org.newdawn.slick.geom.Rectangle
import org.newdawn.slick.{Animation, Color, Graphics, Input}

import scala.collection.mutable.ListBuffer

/**
 * @author shimcakes in a time of dire need, not only for him but for the entire world. He had nothing. Nothing to
 *         live for and no one to stop him from not living. No food, no clothing. Not even a Player class. Yet
 *         shimcakes gave his all and all he had, he gave ( which was, if you remember, nothing much). And eventually he
 *         gave his life for this last push. Thank you shim, you will be remembered forever in our hearts( and our
 *         bitbucket) for all that you did for us( ... Sob ):
 * @author sriram
 */
object Player extends GObject(-1f, -1f, 32, 62, "char_idle/char_idle.png", 9, 16, 15, 46) with Attackable {
  val WalkAnim = initAnim("char_walk", rsx, rsy, 250)
  val JumpAnim = initAnim("char_duck.png", rsx, rsy, 250)
  val HitAnim = initAnim("char_hit.png", rsx, rsy, 500)
  val DeathAnim = initAnim("char_death", rsx, rsy, 1000)
  HitAnim.setLooping(false)
  DeathAnim.setLooping(false)
  val DuckAnim = gobjects.initAnim("char_duck.png", rsx, rsy, 250)
  var trinket: Option[BasicUpgrade] = None
  var consumable: Option[BasicUpgrade] = None

  val MaxJumpCount = 3
  val InvincibilityTime = 2000
  var hit, ducking = false
  var maxSpeed, accel, jumpAccel: Float = _
  var attack, hlth, maxHealth: Int = 0
  var fireTime: Long = _
  var weapon, special: Weapon = _
  var sp, level, jumpCount = 0
  var velX, velY = 0f

  def init(init: Race) {
    x = init.x + offsetX
    y = init.y + offsetY
    accel = init.accel
    attack = init.attack
    maxHealth = init.maxHealth
    hlth = maxHealth
    weapon = init.weapon
    special = init.special
    attack += init.weapon.damage
    maxSpeed = init.maxSpeed
    jumpAccel = init.jumpAccel
    DeathAnim.restart()
    fireTime = -1
    hit = false
    ducking = false
  }

  override def render(g: Graphics) {
    if (HitAnim.isStopped) {
      hit = false
      HitAnim.restart()
    }
    val (tempAnim, tx, ty, tsx, tsy) = whichAnim
    if (invincible && !dead)
      tempAnim.draw(tx, ty, tsx, tsy, Color.red)
    else
      tempAnim.draw(tx, ty, tsx, tsy)

    g.setColor(new Color(170, 57, 57))
    g.fill(coll)
    g.setColor(Color.white)
    weapon.render(g)
    special.render(g)
  }

  /**
   * @return the Animation to render based on the current characteristics of the Player
   */
  private def whichAnim: (Animation, Float, Float, Float, Float) = {
    velX match {
      case vel if vel < 0 =>
        renderFlipped = false
      case vel if vel > 0 =>
        renderFlipped = true
      case _ =>
    }

    if (renderFlipped) {
      if (dead) (DeathAnim, rx + rsy, ry + (rsy - rsx), -rsy, rsx)
      else if (hit) (HitAnim, rx + rsx, ry, -rsx, rsy)
      else if (ducking) (DuckAnim, rx + rsx, ry, -rsx, rsy)
      else if (velX == 0) (anim, rx + rsx, ry, -rsx, rsy)
      else (WalkAnim, rx + rsx, ry, -rsx, rsy)
    } else {
      if (dead) (DeathAnim, rx, ry + (rsy - rsx), rsy, rsx)
      else if (hit) (HitAnim, rx, ry, rsx, rsy)
      else if (ducking) (DuckAnim, rx, ry, rsx, rsy)
      else if (velX == 0) (anim, rx, ry, rsx, rsy)
      else (WalkAnim, rx, ry, rsx, rsy)
    }
  }


  def update(collision: ListBuffer[Rectangle], enemies: ListBuffer[Enemy], in: Input) {
    super.update(collision, enemies)
    if (!dead)
      input(in)

    if (gOn) velY += gobjects.gravity

    velX -= gobjects.friction * math.signum(velX)
    if (math.abs(velX) < .1) velX = 0
    gOn = true
    //x collision
    if (ducking)
      x += velX / 4
    else
      x += velX
    for (c <- collision) {
      if (c.intersects(coll)) {
        x -= velX
        velX = 0
      }
    }
    //y collision
    y += velY
    for (c <- collision) {
      if (c.intersects(coll)) {
        if (velY >= 0) {
          gOn = false
          jumpCount = 0
        }
        while (c.intersects(coll)) y -= math.signum(velY)
        velY = 0
      } else {
        if (velY >= 0 && c.getY - maxY < 1 && c.getY - maxY > 0 && x < c.getMaxX && maxX > c.getX) gOn = false
      }
    }
    //Enemy collision
    for (e <- enemies) {
      if (e.coll.intersects(coll)) {
        e match {
          case sp: SP => sp.onContact()
          case _ if invincible =>
          case _ =>
            e.onContact()
            gotAttackedTime = gobjects.milli
            velX = if (maxX - e.x > e.x - x) 3f else -3f
            velY = jumpAccel
        }
      }
    }
    for (u <- NormalStage.map.upgrades) {
      if (u.coll.intersects(coll)) {
        u.pickUp()
        NormalStage.map.upgrades.clear()
      }
    }
    for(spike <- NormalStage.map.spikes) {
      if (!invincible && spike.coll.intersects(coll)) {
        spike.onContact()
        health_-=(spike.damage)
      }
    }
    weapon.update(collision, enemies)
    special.update(collision, enemies)
    if (trinket.isDefined) trinket.get.longTimeEffect(collision, enemies)
  }

  private def input(in: Input) {
    sy = 46
    ducking = false
    if (in.isKeyDown(Input.KEY_LEFT))
      if (velX < -maxSpeed) velX = -maxSpeed else velX -= accel
    else if (in.isKeyDown(Input.KEY_RIGHT))
      if (velX > maxSpeed) velX = maxSpeed else velX += accel

    if (in.isKeyDown(Input.KEY_DOWN) && !gOn) {
      sy = 31
      ducking = true
    }
    if (in.isKeyPressed(Input.KEY_UP) && !ducking && jumpCount < MaxJumpCount) {
      jumpCount += 1
      velY = jumpAccel
    }
    if (in.isKeyDown(Input.KEY_X)) {
      weapon.fire(dir)
    }
    if (in.isKeyPressed(Input.KEY_C) && sp > 0) {
      special.fire(dir)
      sp -= 1
    }
  }

  override def invincible = gobjects.milli - gotAttackedTime < InvincibilityTime

  override def health_-=(that: Int) = {
    hlth -= that
    gotAttackedTime = gobjects.milli
    hit = true
  }
}

trait Race {
  val x = 400
  val y = 80
  val jumpAccel = -6
  val attack, maxHealth: Int
  val weapon: Weapon
  val special: Weapon
  val maxSpeed, accel: Float
}

class Ranged extends Race {
  override val attack: Int = 50
  override val weapon = new RangedWeapon(5, 10, 250, "testImg.png")
  weapon.attacks += new StraightAttack(20, 250, 3, 0, "bullet_small.png")(_ => false)
  override val special = new RangedWeapon(5, 10, 250, "testImg.png")
  special.attacks += new CircularStraightAttack(70, 250, 5, -5, "bullet_small.png")(_ => false)
  override val maxHealth: Int = 100
  override val maxSpeed: Float = 2
  override val accel: Float = .5f
}

class Melee extends Race {
  override val attack = 35
  override val weapon = new MeleeWeapon(10, 10, "testImg.png")
  override val maxHealth = 100
  override val maxSpeed = 2f
  override val accel = .5f
  override val special = new RangedWeapon(5, 10, 250, "testImg.png")
  special.attacks.clear()
  special.attacks += new CircularStraightAttack(10, 250, 2, 2, "bullet_small.png")(_ => false)
}