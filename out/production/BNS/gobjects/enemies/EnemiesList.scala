package gobjects.enemies

import scala.collection.mutable.ListBuffer

/**
 * Created by Sriram on 4/17/2015.
 */
object EnemiesList {
  val Enemies = new ListBuffer[Enemy]
  Enemies += Enemy(0, 0)
  Enemies += FlyingEnemy(0, 0)
  Enemies += EBee(0, 0)
  Enemies += EDeer(0, 0)

  def apply = Enemies
}
