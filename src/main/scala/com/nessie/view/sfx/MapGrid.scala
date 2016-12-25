package com.nessie.view.sfx

import javafx.{scene => jfxs}

import com.nessie.gm.GameState
import com.nessie.model.map._
import com.nessie.model.units.CombatUnit
import com.nessie.model.units.abilities.MoveAbility
import common.rich.RichT._
import common.rich.collections.RichTraversableOnce._
import monocle.Optional
import monocle.function.Index
import rx.lang.scala.Observable
import rx.lang.scala.subjects.PublishSubject

import scala.concurrent.Promise
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.Side
import scalafx.scene.Node
import scalafx.scene.control.Button
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout._

private class MapGrid(map: BattleMap) extends NodeWrapper {
  import MapGrid._
  import NodeWrapper._
  private val mouseSubjects = PublishSubject[(MouseEvent, MapPoint)]()
  val mouseEvents: Observable[(MouseEvent, MapPoint)] = mouseSubjects
  private val cells: Map[MapPoint, BorderPane] = {
    map.points.map { case (p, o) =>
      val $ = MapGrid.createCell(o)
      GridPane.setConstraints($, p.x, p.y)
      // TODO unsubscribe on destruction
      toObserver(MouseEvent.Any, $.center.get)
          .map(new MouseEvent(_)) // piece of shit framework :\
          // TODO fproduct
          .map(e => e -> p) subscribe mouseSubjects
      $
    }.mapBy(toPoint)
  }

  for ((pd, bo) <- map.betweens) {
    val d = pd.direction
    val (wallWidth, wallHeight) = (CELL_SIDE, WALL_WIDTH)
        .mapIf(_ => d == Direction.LEFT || d == Direction.RIGHT).to(_.swap)
    borderPaneIndex.index(d).set(new Pane {
      prefWidth = wallWidth
      prefHeight = wallHeight
      style = s"-fx-background-color: ${if (bo == Wall) "black" else "grey" }"
    })(cells(pd.toPoint))
  }

  override val node = new GridPane() {
    children = cells.values
  }
  private def highlight(location: MapPoint, moveAbility: MoveAbility): Unit = {
    def color(bp: BorderPane, color: String) = bp.center.get.style = "-fx-base: " + color
    moveAbility.canBeApplied(map, location)
        .filter(_._2)
        .foreach(e => color(cells(e._1), "green"))
    color(cells(location), "blue")
  }

  private def getMove(source: MapPoint, gs: GameState): Promise[GameState] = {
    val $ = Promise[GameState]
    val menuEvents = PublishSubject[GameState]()
    val menuFactory = new ActionMenuFactory(source, gs, menuEvents)
    def createMenu(node: Node, destination: MapPoint): Unit =
      menuFactory(destination).show(node, Side.Bottom, 0, 0)
    val subscription = mouseEvents.filter(_._1.eventType == MouseEvent.MouseClicked)
        .map(e => jfx2sfx(e._1.source.asInstanceOf[jfxs.Node]) -> e._2) subscribe (e => createMenu(e._1, e._2))
    menuEvents.subscribe(e => {
      subscription.unsubscribe
      $ success e
    })
    $
  }

  def nextState(u: CombatUnit)(gs: GameState): Promise[GameState] = {
    val unitLocation = CombatUnitObject.findIn(u, gs.map).get
    highlight(unitLocation, u.moveAbility)
    getMove(unitLocation, gs)
  }
}

private object MapGrid {
  private val CELL_SIDE = 40
  private val WALL_WIDTH = 5

  private def createCell(o: BattleMapObject): BorderPane = {
    def text(o: BattleMapObject): String = o match {
      case EmptyMapObject => ""
      case CombatUnitObject(u) => u.simpleName take 2
    }
    new BorderPane() {
      center = new Button(text(o)) {
        prefHeight = CELL_SIDE
        prefWidth = CELL_SIDE
      }
    }
  }

  private val borderPaneIndex: Index[BorderPane, Direction, Node] = new Index[BorderPane, Direction, Node] {
    private implicit def toOpt[T](objectProperty: ObjectProperty[jfxs.Node]): Option[Node] =
      objectProperty.value.opt.map(NodeWrapper.jfx2sfx)
    override def index(d: Direction): Optional[BorderPane, Node] = Optional[BorderPane, Node](bp => d match {
      case Direction.UP => bp.top
      case Direction.DOWN => bp.bottom
      case Direction.LEFT => bp.left
      case Direction.RIGHT => bp.right
    })(node => bp => {
      val setter: (Node => Unit) = d match {
        case Direction.UP => bp.top_=
        case Direction.DOWN => bp.bottom_=
        case Direction.LEFT => bp.left_=
        case Direction.RIGHT => bp.right_=
      }
      setter(node)
      bp
    })
  }

  private def toPoint(e: Node): MapPoint = MapPoint(GridPane.getColumnIndex(e), GridPane.getRowIndex(e))
}
