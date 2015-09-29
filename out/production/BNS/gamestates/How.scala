package gamestates

import TWLSlick.{RootPane, BasicTWLGameState}
import de.matthiasmann.twl.{Button,SimpleDialog}

import main.Main
import main.Main.Game
import org.newdawn.slick.{Graphics, GameContainer}
import org.newdawn.slick.state.StateBasedGame

/**
 * Created by Sriram on 4/17/2015.
 */
object How extends BasicTWLGameState{
  override def getID: Int = Game.HelpId
  val Back = new Button


  override def init(container: GameContainer, game: StateBasedGame) = {}

  override def update(container: GameContainer, game: StateBasedGame, delta: Int): Unit = {}

  override def render(container: GameContainer, game: StateBasedGame, g: Graphics): Unit = {
    g.drawString("Controls : \n" +
                 "Arrow keys : Movement\n" +
                  "X : Fire\n" +
                  "C : Special\n" +
                  "Collect SP to use your special attack\n" +
                  "R : Restart the season and lose all of your score and half of your SP\n" +
                  "E : Restart from the beginning and lose everything\n",
      100 * Main.scale, 100 * Main.scale)
  }

  override protected def createRootPane(): RootPane = {
    val rp = super.createRootPane()
    rp.setTheme("bns")
    Back.setTheme("button")
    Back.addCallback(new Runnable() {
      override def run() = {
        Game.enterState(Game.StartScreenId)
      }
    })
    Back.setText("Back")

    rp.add(Back)
    rp
  }

  override def layoutRootPane() = {
    Back.setPosition((100 * Main.scale).toInt, (Main.height - 100 * Main.scale).toInt)
    Back.adjustSize()
  }
}
