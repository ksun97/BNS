package util

import org.newdawn.slick.particles.{ParticleEmitter, ParticleSystem}

class GParticleSystem(imageRef: String, maxParticles: Int = 100) extends ParticleSystem(imageRef, maxParticles) {
  def particles = particlesByEmitter

  def particlePool(emitter: ParticleEmitter) = {
    val temp = particlesByEmitter.get(emitter)
    temp.asInstanceOf[GParticleSystem.ParticlePoolClass.type]
  }

}

object GParticleSystem {
  val ParticlePoolClass = Class.forName("org.newdawnslick.particles.ParticleSystem#ParticlePool")

}
