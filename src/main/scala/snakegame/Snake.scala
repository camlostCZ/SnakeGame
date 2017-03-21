package snakegame

import Direction._

class Snake(x: Int, y: Int) {
    var body: List[Position] = List(
        Position(x, y))

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
