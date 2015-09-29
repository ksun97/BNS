package gamestates

import TWLSlick.{BasicTWLGameState, RootPane}
import de.matthiasmann.twl.Button
import main.Main
import main.Main.Game
import org.newdawn.slick.state.StateBasedGame
import org.newdawn.slick.{GameContainer, Graphics}
import org.newdawn.slick


object StartScreen extends BasicTWLGameState{
  val PlayButton = new Button
  val How = new Button
  override def getID = Game.StartScreenId
  var bg : slick.Image = null
  var start: slick.Image = null
  var how : slick.Image = null

  override def init(container: GameContainer, game: StateBasedGame) {
    bg = new slick.Image("titlescreen_0.png")
    bg = bg.getScaledCopy(Main.width / bg.getWidth)
    start = new slick.Image("start_0.png").getScaledCopy(Main.scale)
    how = new slick.Image("how0.png").getScaledCopy(Main.scale)
  }

  override def update(container: GameContainer, game: StateBasedGame, delta: Int){

  }

  override def render(container: GameContainer, game: StateBasedGame, g: Graphics){
    bg.draw(0,0)
    start.draw(PlayButton.getX, PlayButton.getY)
    how.draw(How.getX, How.getY)

  }

  override protected def createRootPane(): RootPane = {
    val rp = super.createRootPane()
    rp.setTheme("bns")
    PlayButton.setTheme("button")
    PlayButton.addCallback(new Runnable() {
      override def run() = {
        Game.enterState(Game.GameId)
      }
    })
    How.setTheme("button")
    How.addCallback(new Runnable() {
      override def run() = {
        Game.enterState(Game.HelpId)
      }
    })
    rp.add(PlayButton)
    rp.add(How)
    rp
  }
  override def layoutRootPane() = {
    PlayButton.setPosition((Main.width - Main.scale * 300).toInt, (Main.height / 2 + Main.scale * 100).toInt)
    PlayButton.setSize((start.getWidth).toInt, (start.getHeight).toInt)
    How.setPosition((Main.width - Main.scale * 300).toInt, (Main.height / 2 + Main.scale * 200).toInt)
    How.setSize((how.getWidth).toInt, (how.getHeight).toInt)
  }
}
