package gobjects

import java.io.File

import attack.NoAttack
import gamestates.NormalStage
import gobjects.enemies.Enemy
import org.newdawn.slick.geom.Rectangle
import org.newdawn.slick.particles.ParticleIO

import scala.collection.mutable.ListBuffer

class SP(x: Float, y: Float, vX: Float, vY: Float, val spVal: Int = 1) extends Enemy(x, y, 8, 8, "SP.png", 1, 0, 100) {
  velX = vX
  velY = vY
  accel = 3f
  gOn = true
  override val emitter = ParticleIO.loadEmitter(new File("res/particles/particle_templete.xml"))

  emitter.length setMax 1000
  emitter.length setMin 1000
  emitter.setEnabled(true)
  NormalStage.Particles.addEmitter(emitter)

  override def onContact() {
    Player.sp += spVal
    hlth = -1
    NormalStage.Particles.removeEmitter(emitter)
  }

  override def move() {
    super.move()
    //if (Player.y < y) velY = -accel
    //if (Player.y - Player.sy > y) velY = accel
  }

  override def updateBullets(collision: ListBuffer[Rectangle], enemies: ListBuffer[Enemy]) = {}

  override def fire(dir: Int) = {}

  override def invincible = true

  override def initAttacks() = {
    attacks += new NoAttack(0 < _ - 0)
  }

  override def nextAttack() = {}

  override def deathAction(collision: ListBuffer[Rectangle], enemies: ListBuffer[Enemy]) = {}
}


