package snake

import scala.scalajs.js

import org.scalajs.dom
import org.scalajs.dom.ext.KeyCode

object Direction extends Enumeration {
    type Direction = Value
    val EAST, SOUTH, WEST, NORTH = Value
}

import Direction._

case class Position(x: Int, y: Int) {
    def ==(pos: Position): Boolean = {
        x == pos.x && y == pos.y
    }
}

class Snake {
    var body: List[Position] = List(
        Position(19, 12))

    private var wallHit = false

    def position(): Position = {
        val head = body.head
        Position(head.x, head.y)
    }

    def isBite(): Boolean = {
        body.tail.contains(body.head)
    }

    def isHit(): Boolean = {
        wallHit
    }

    def move(dir: Direction, food: List[Position]): Unit = {
        // Add a new head in the current direction
        val head = body.head
        val newPos = dir match {
            case NORTH => Position(head.x, head.y - 1)
            case SOUTH => Position(head.x, head.y + 1)
            case EAST  => Position(head.x + 1, head.y)
            case WEST  => Position(head.x - 1, head.y)
        }
        if (SnakeGame.isValidPosition(newPos)) {
            body = newPos :: body

            if (!food.contains(this.position)) 
                body = body.dropRight(1)
        }
        else
            wallHit = true
    }
}

object SnakeGame extends js.JSApp {
    val canvasId = "snake-game"
    val blockSize = 24
    val viewWidth = 40
    val viewHeight = 25
    val colorBackground = "white"
    val colorForeground = "black"
    var colorSnake      = "yellow"
    val colorFood       = "green"

    val rnd = scala.util.Random
    val hero = new Snake()
    var dir = EAST
    var food: List[Position] = Nil

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
        
        val update = () => {
            // Try to generate food
            if (rnd.nextInt(100) < 8) {
                food = Position(1 + rnd.nextInt(viewWidth - 2), 1 + rnd.nextInt(viewHeight - 2)) :: food
                drawFood(food.head, ctx)

                // FIXME Food generated inside Snake's body
            }

            if (hero.isBite || hero.isHit) {
                // TODO End game
                //js.timers.clearInterval() ?
                colorSnake = "red"
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
        }

        dom.window.setInterval(update, 250)

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
            ctx.fillStyle = colorSnake
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
        drawSnakeBody(hero.body.tail.head)
        drawSnakeBody(hero.body.last)
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