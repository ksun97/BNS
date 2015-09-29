package gobjects.upgrades

import attack._
import gobjects.Player

import scala.collection.mutable

object UpgradesList {
  val SIZEX = 20
  val SIZEY = 20
  val Upgrades = mutable.HashMap[(Float, Float) => BasicUpgrade, Int]()

  val heal = (x: Float, y: Float) => {
    new BasicUpgrade(x, y, SIZEX, SIZEY, "health.png") with Consumable {
      override def pickUp(): Unit = Player.health_+=(30)
    }
  }
  Upgrades += heal -> 2
  val healthUp = (x: Float, y: Float) => {
    new BasicUpgrade(x, y, SIZEX, SIZEY, "health.png") {
      override def pickUp(): Unit = {
        Player.maxHealth += 50
        Player.health_+=(50)
      }
    }
  }

  Upgrades += healthUp -> 5

  val healthOT = (x: Float, y: Float) => {
    new BasicUpgrade(x, y, SIZEX, SIZEY, "health.png") {
      val freq = 5000
      var startTime = gobjects.milli

      def longTimeEffect() = {
        if (freq + startTime > gobjects.milli) {
          startTime = gobjects.milli
          Player.health_+=(10)
        }
      }
    }
  }

  Upgrades += healthOT -> 4

  val gravityAffectedWeapon = (x: Float, y: Float) => {
    val weapon = new RangedWeapon(5, 30, 250, "health.png")
    weapon.attacks += new Attack(30,250,5,-5,"bullet_small.png")(_ => false)
    weapon.attacks.head.gravity = true
    new WeaponUpgrade(x, y, SIZEX, SIZEY, "health.png", weapon)
  }
  Upgrades += gravityAffectedWeapon -> 1

  val largeBullet = (x : Float, y : Float) => {
    val weapon = new RangedWeapon(5, 100, 250, "health.png")
    weapon.attacks += new NoWallCollideStraightAttack(100,250,3,0,"bullet_small.png")(_ => false)
    weapon.attacks.head.bulletSx = 35
    weapon.attacks.head.bulletSy = 35
    new SpecialUpgrade(x, y, SIZEX, SIZEY, "health.png", weapon)
  }
  Upgrades += largeBullet -> 3
  val rebounding = (x : Float, y : Float) => {
    val weapon = new RangedWeapon(5, 100, 250, "health.png")
    weapon.attacks += new ReboundingAttack(20,250,3,0,"bullet_small.png")(_ => false)
    weapon.attacks.head.bulletSx = 35
    weapon.attacks.head.bulletSy = 35
    new WeaponUpgrade(x, y, SIZEX, SIZEY, "health.png", weapon)
  }
  def apply = Upgrades
}
