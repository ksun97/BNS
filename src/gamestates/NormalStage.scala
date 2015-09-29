package gamestates

import TWLSlick.BasicTWLGameState
import de.matthiasmann.twl.{Button, Label, ProgressBar}
import gobjects.{Clock, Player, Ranged}
import main.Main.{Game, scale}
import org.newdawn.slick.particles.ParticleSystem
import org.newdawn.slick.state.StateBasedGame
import org.newdawn.slick.{GameContainer, Graphics, Input}
import util.Map


object NormalStage extends BasicTWLGameState {
  var waveNum = 1
  val Maps = Array("seasons/spring.tmx", "seasons/summer.tmx", "seasons/fall.tmx", "seasons/winter.tmx", "seasons/shop.tmx")
  var season = 0
  var delta = -1
  var score = 0
  var map = new Map(Maps(season), waveNum)
  lazy val Particles = new ParticleSystem("/res/bullet_large0.png", 100)
  Particles.setRemoveCompletedEmitters(false)
  Particles.setBlendingMode(ParticleSystem.BLEND_ADDITIVE)
  val RestartButton = new Button("Restart")
  val SPLabel = new Label
  val ScoreLabel = new Label
  val HealthBar = new ProgressBar

  override def init(container: GameContainer, game: StateBasedGame) {
    Player.init(new Ranged)
  }

  override def createRootPane() = {
    val rp = super.createRootPane()
    rp.setTheme("bns")
    RestartButton.setTheme("button")
    RestartButton.addCallback(new Runnable() {
      override def run() = {
        map = Map("/season/spring.tmx", 0)
        Player.init(new Ranged)
      }
    })
    rp.add(RestartButton)
    rp.add(HealthBar)
    rp.add(SPLabel)
    rp.add(ScoreLabel)
    map.initUI(rp)
    rp
  }

  override def layoutRootPane() = {
    RestartButton.adjustSize()
    RestartButton.setPosition((scale * (map.width - 5)).toInt, (scale * (map.height - 20)).toInt)
    HealthBar.setSize((80 * scale).toInt, (20 * scale).toInt)
    HealthBar.setPosition((scale * (map.width - 100)).toInt, (30 * scale).toInt)
    SPLabel.setPosition((scale * (map.width - 130)).toInt, (30 * scale).toInt)
    ScoreLabel.setPosition((scale * map.width/2).toInt, (30 * scale).toInt)
  }

  override def update(container: GameContainer, game: StateBasedGame, delta: Int) {
    this.delta = delta
    input(container.getInput, container, game)
    updateUI()
    if (!Player.DeathAnim.isStopped)
      Player.update(map.collision, map.enemies, container.getInput)
    map.update(container, game, delta)
    if (Particles == null) println("particles null")
    Particles.update(delta)
    nextMap()
    Clock.update(map.collision, map.enemies)
  }

  def nextMap() = {
    if (map.finished || Clock.season != season) {
      season = (season + 1) % Maps.length
      waveNum += 1
      map.destroyUI()
      val temp = map.enemies
      map = new Map(Maps(season), waveNum)
      map.enemies ++= temp
      Clock.angle = season * Clock.FullYear / 5
    }
  }

  def updateUI() {
    HealthBar.setValue(Player.health.toFloat / Player.maxHealth)
    HealthBar.setText(Player.health + "")
    SPLabel.setText("SP: " + Player.sp)
    ScoreLabel.setText("Score: " + score)
    map.updateUI()
  }

  override def render(c: GameContainer, game: StateBasedGame, g: Graphics) {
    map.render(g)
    if (!Player.DeathAnim.isStopped)
      Player.render(g)
    Particles.render()
//    Clock.render(g)
  }

  def input(in: Input, container: GameContainer, game: StateBasedGame) {
    if (in.isKeyPressed(Input.KEY_R)) {
      Player.init(new Ranged)
      Player.sp /= 2
      score = 0
    } else if(in.isKeyPressed(Input.KEY_E)) {
      waveNum = 0
      season = 0
      map = new Map(Maps(season), waveNum)
      Player.init(new Ranged)
      score = 0
      Player.sp = 0
    }
    if (in.isKeyPressed(Input.KEY_P))
      container.pause()
  }


  override def getID: Int = Game.GameId
}

