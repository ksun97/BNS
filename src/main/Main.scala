package main

import java.net.URL

import TWLSlick.TWLStateBasedGame
import gamestates.{How, StartScreen, NormalStage}
import org.newdawn.slick.{Game, AppGameContainer, GameContainer, ScalableGame}

object Main {

  var appgc = new AppGameContainer(Game)

  val width = appgc.getScreenWidth
  val height = width * 9 / 16
  val scale = width / (42f * 32)
  def unit = appgc.getWidth / 100
  appgc = new AppGameContainer(Game, width, height, false)

  def main(args: Array[String]) {
    System.setProperty("org.lwjgl.opengl.Display.enableHighDPI", "true")
        appgc.setDisplayMode(width, height, false)
//    appgc.setFullscreen(true)
    appgc.setTargetFrameRate(60)
    appgc.setVSync(true)
    appgc.start()
  }

  object Game extends TWLStateBasedGame("Eataries") {
    val GameId = 1
    val LoadingScreenId = 2
    val StartScreenId = 3
    val ShopId = 4
    val HelpId = 5

    override def initStatesList(container: GameContainer) {
      addState(StartScreen)
      addState(NormalStage)
      addState(How)
      enterState(Game.StartScreenId)
    }

    override protected def getThemeURL: URL = {
      val url = Game.getClass.getResource("/ui/BNS.xml")
      if (url == null)
        println("url to gameui not found")
      url
    }
  }
}