package util

import TWLSlick.RootPane
import gamestates.NormalStage._
import gobjects.SP
import gobjects.enemies._
import gobjects.upgrades.{BasicUpgrade, RandomGenerator}
import main.Main
import org.newdawn.slick.geom.Rectangle
import org.newdawn.slick.tiled.TiledMap
import org.newdawn.slick.{Game, GameContainer, Graphics, Image}

import scala.collection.mutable.ListBuffer


/*
 * Used to manage maps, enemies, and particles
 * @param loc
 */
case class Map(loc: String, waveNum: Int) {
  //  var clouds = List(new GObject())
  val map = new TiledMap(loc, "res/seasons/")
  val collision = new ListBuffer[Rectangle]
  val upgrades = new ListBuffer[BasicUpgrade]
  val spikes = new ListBuffer[Enemy]
  var enemies = initEnemies()
  var deadEnemies = ListBuffer[Enemy]()
  for (j <- 0 until map.getObjectCount(0))
    collision += new Rectangle(map.getObjectX(0, j), map.getObjectY(0, j), map.getObjectWidth(0, j), map
      .getObjectHeight(0, j))
  for (i <- 0 until map.getObjectCount(2)) {
    spikes += new Spike(map.getObjectX(2, i), map.getObjectY(2, i))
  }
  for (i <- 0 until map.getObjectCount(3)) {
    spikes += new GroundSpike(map.getObjectX(3, i), map.getObjectY(3, i))
  }


  collision += new Rectangle(0, 0, 1, height)
  collision += new Rectangle(0, 0, width, 1)
  collision += new Rectangle(0, height, width, 1)
  collision += new Rectangle(width, 0, 1, height)
  makeUpgrades()

  def initEnemies() = {
    val enemies = RandomGenerator.enemies

    for (e <- enemies) {
      e.x = 100 + RandomGenerator.random.nextInt(5)
      e.y = 200 + RandomGenerator.random.nextInt(5)
      var stuck = true
      while (stuck) {
        stuck = false
        for (c <- collision)
          if (c.intersects(e.coll)) {
            e.y -= e.sy
            stuck = true
          }
      }
    }
    enemies
  }


  def reinit(rp: RootPane) {
    deadEnemies.clear()
    destroyUI()
    enemies = initEnemies()
    initUI(rp)
  }

  def initUI(rp: RootPane) {
    enemies.foreach(e => rp.add(e.healthBar))
  }

  def updateUI() {
    enemies.foreach(_.updateHealthBar())
  }

  def destroyUI() {
    enemies.foreach(e => rootPane.removeChild(e.healthBar))
  }

  /**
   * Updates enemies and deadEnemies
   * @param c container
   * @param game game
   * @param delta delta
   */
  def update(c: GameContainer, game: Game, delta: Int) {
    deadEnemies.foreach(_.updateBullets(collision, enemies))
    enemies.foreach(_.update(collision, enemies))

    val sps = enemies.filter(e => e.dead && e.isInstanceOf[SP])
    enemies --= sps


    val newlyDead = enemies.filter(_.dead)
    for(e <- newlyDead) {
      e.updateDeath(collision,enemies)
      e.renderDeath(c.getGraphics)
      rootPane.removeChild(e.healthBar)
      Particles.removeEmitter(e.emitter)
      e.score()
    }

    deadEnemies ++= newlyDead

    enemies --= deadEnemies
    deadEnemies = deadEnemies.filter(e => e.attacks(e.stage).bullets.size != 0)
    //    var count = 0
    //    enemies.foreach(e => count += e.attacks(e.stage).bullets.size)
    //    println(count)
    upgrades.foreach(_.update(collision, enemies))
    updateVisualFx()
  }

  def updateVisualFx() {
    updateParticles()

  }

  def finished = enemies.size == 0 && upgrades.size == 0

  def updateParticles() {
    //    Particles.
  }

  def makeUpgrades(): Unit = {
    if (loc.equals("seasons/shop.tmx")) {
      val u = RandomGenerator.upgrades
      upgrades += u _1(26 * 32, 8 * 32)
      upgrades += u._2(32 * 32, 5 * 32)
      upgrades += u _3(37 * 32, 8 * 32)
    }
  }

  /**
   * Renders map, minimap and enemies
   * @param g graphics
   * @param scale scale
   */
  def render(g: Graphics, scale: Float = Main.scale) {
    renderMap(scale)
    spikes.foreach(_ render g)
    for (e <- enemies)
      e.render(g)
    deadEnemies.foreach(_.renderBullets(g))
    upgrades.foreach(_.render(g))
  }

  /**
   * Renders the map based on a scale
   * @param scale scale
   */
  def renderMap(scale: Float) {
    for (i <- 0 until map.getLayerCount; x <- 0 until cols; y <- 0 until rows) {
      val tmp = map.getTileImage(x, y, i)
      if (tmp != null)
        tmp.draw(x * scale * tileWidth, y * scale * tileWidth, scale)
    }
  }

  def tileWidth = map.getTileWidth

  def tileHeight = map.getTileHeight

  def cols = map.getWidth

  def rows = map.getHeight

  def width = tileWidth * cols

  def height = tileHeight * rows

  def rWidth = width * Main.scale

  def rHeight = height * Main.scale

}

object Map {
  val Clouds = Array(new Image("cloud/cloud0.png"), new Image("cloud/clouda0.png"), new Image("c"))
}
