package snakegame

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
