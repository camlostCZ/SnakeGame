package snakegame

import org.scalajs.dom

object Config {
    val canvasId = "snake-game"
    val blockSize = 24
    val viewWidth = 24
    val viewHeight = 16
    val colorBackground = "white"
    val colorForeground = "black"
    val colorSnakeHead  = "orange"
    val colorSnake      = "yellow"
    val colorFood       = "green"

    val imgPoison = dom.document.createElement("img").asInstanceOf[dom.html.Image]
    imgPoison.src = "img/poison.png"
}