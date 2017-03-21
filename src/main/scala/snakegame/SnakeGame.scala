package snakegame

import scala.scalajs.js
import scalajs.js.annotation.JSExport

import org.scalajs.dom
import org.scalajs.dom.ext.KeyCode

import Direction._


@JSExport
object SnakeGame extends js.JSApp {
    val canvasId = "snake-game"
    val blockSize = 24
    val viewWidth = 40
    val viewHeight = 25
    val colorBackground = "white"
    val colorForeground = "black"
    var colorSnakeHead  = "orange"
    val colorSnake      = "yellow"
    val colorFood       = "green"

    var timer: Option[js.timers.SetIntervalHandle] = None
    val rnd = scala.util.Random
    val hero = new Snake()
    var dir = EAST
    var food: List[Position] = Nil

    @JSExport
    def main(): Unit = {
        val canvas = dom.document.getElementById(canvasId).asInstanceOf[dom.html.Canvas]
        canvas.width = viewWidth * blockSize
        canvas.height = viewHeight * blockSize
        val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

        setupUI(ctx)
        dom.window.addEventListener("keydown", (e: dom.KeyboardEvent) => {
            dir = e.keyCode match {
                case KeyCode.Up    => NORTH
                case KeyCode.Right => EAST
                case KeyCode.Down  => SOUTH
                case KeyCode.Left  => WEST
                case _             => dir
            }
        }, false)
        
        def update() = {
            // Try to generate food
            if (rnd.nextInt(100) < 8) {
                food = Position(1 + rnd.nextInt(viewWidth - 2), 1 + rnd.nextInt(viewHeight - 2)) :: food
                drawFood(food.head, ctx)

                // FIXME Food generated inside Snake's body
            }

            clearSnake(hero, ctx)
            hero.move(dir, food)
            drawSnake(hero, ctx)

            // Remove hero.position from food as it's been just eaten
            if (food.contains(hero.position)) {
                food = food.filter {
                    case p: Position if (p.x == hero.position.x && p.y == hero.position.y) => false
                    case _ => true
                }

                // TODO Increase score
            }

            if (hero.isBite || hero.isHit) // End game
                timer.foreach(js.timers.clearInterval _)
        }

        val handler: js.Function0[Any] = () => update()
        timer = Option(js.timers.setInterval(250)(update))
    }

    def clearSnake(hero: Snake, ctx: dom.CanvasRenderingContext2D) = {
        // Optimization: clear the last block only
        ctx.clearRect(hero.body.last.x * blockSize, hero.body.last.y * blockSize, blockSize, blockSize)
    }

    def clearView(ctx: dom.CanvasRenderingContext2D) = {
        ctx.fillStyle = colorBackground
        ctx.fillRect(0, 0, 639, 399)
    }

    def drawBorder(ctx: dom.CanvasRenderingContext2D) = {
        ctx.fillStyle = colorForeground
        for (i <- 0 to viewWidth) {
            ctx.fillRect(i * blockSize, 0, blockSize, blockSize)
            ctx.fillRect(i * blockSize, (viewHeight - 1) * blockSize, blockSize, blockSize)
        }
        for (i <- 0 to viewHeight) {
            ctx.fillRect(0, i * blockSize, blockSize, (i + 1) * blockSize)
            ctx.fillRect((viewWidth - 1) * blockSize, i * blockSize, blockSize, blockSize)
        }
    }

    def drawFood(pos: Position, ctx: dom.CanvasRenderingContext2D) = {
        val x = pos.x * blockSize
        val y = pos.y * blockSize
        ctx.fillStyle = colorFood
        ctx.fillRect(x, y, blockSize, blockSize)
    }

    def drawSnake(hero: Snake, ctx: dom.CanvasRenderingContext2D) = {
        def drawSnakeHead(pos: Position) = {
            val x = pos.x * blockSize
            val y = pos.y * blockSize
            ctx.fillStyle = colorSnakeHead
            ctx.fillRect(x + 1, y + 1, blockSize - 2, blockSize - 2)
            ctx.strokeRect(x + 1, y + 1, blockSize - 2, blockSize - 2)        
        }
        def drawSnakeBody(pos: Position) = {
            val x = pos.x * blockSize
            val y = pos.y * blockSize
            ctx.fillStyle = colorSnake
            ctx.fillRect(x + 1, y + 1, blockSize - 2, blockSize - 2)
            ctx.strokeRect(x + 1, y + 1, blockSize - 2, blockSize - 2)
        }

        drawSnakeHead(hero.body.head)
        if (!hero.body.tail.isEmpty) {
            drawSnakeBody(hero.body.tail.head)
            drawSnakeBody(hero.body.last)
        }
    }

    def isValidPosition(pos: Position): Boolean = {
        pos.x > 0 && pos.y > 0 && pos.x < viewWidth -1 && pos.y < viewHeight - 1
    }

    def setupUI(ctx: dom.CanvasRenderingContext2D) = {
        clearView(ctx)
        drawBorder(ctx)
        drawSnake(hero, ctx)
    }
}