package com.nessie.view.sfx

import com.nessie.gm.GameState
import com.nessie.model.map._
import com.nessie.model.units.CombatUnit
import com.nessie.model.units.abilities.MoveAbility
import common.rich.RichT._
import common.rich.collections.RichTraversableOnce._
import common.rich.func.{MoreObservableInstances, ToMoreFunctorOps, TuplePLenses}
import javafx.{scene => jfxs}
import monocle.Optional
import monocle.function.Index
import rx.lang.scala.Observable
import rx.lang.scala.subjects.PublishSubject
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.Side
import scalafx.scene.Node
import scalafx.scene.control.Button
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout._

import scala.concurrent.Promise

private class MapGrid(map: BattleMap) extends NodeWrapper with Highlighter[CombatUnit]
    with ToMoreFunctorOps with MoreObservableInstances {
  import MapGrid._
  import NodeWrapper.{jfx2sfx, setBaseColor}

  private val cells: Map[MapPoint, BorderPane] = map.points
      .map {case (p, o) => MapGrid.createCell(o).applyAndReturn(GridPane.setConstraints(_, p.x, p.y))}
      .mapBy(toPoint)

  val mouseEvents: Observable[(MouseEvent, MapPoint)] = NodeWrapper.mouseEvents(cells.mapValues(_.center.get))

  for ((pd, bo) <- map.betweens) {
    val d = pd.direction
    val (wallWidth, wallHeight) = (CELL_SIDE, WALL_WIDTH)
        .mapIf(_ => d == Direction.Left || d == Direction.Right).to(_.swap)
    borderPaneIndex.index(d).set(new Pane {
      prefWidth = wallWidth
      prefHeight = wallHeight
      style = s"-fx-background-color: ${if (bo == Wall) "black" else "grey"}"
    })(cells(pd.toPoint))
  }

  override val node = new GridPane() {
    children = cells.values
  }
  private def highlight(location: MapPoint, moveAbility: MoveAbility): Unit = {
    moveAbility.canBeApplied(map, location)
        .filter(_._2)
        .map(cells apply _._1)
        .foreach(setBaseColor("green"))
    setBaseColor("blue")(cells(location))
  }

  private def getMove(source: MapPoint, gs: GameState): Promise[GameState] = {
    val $ = Promise[GameState]()
    val menuEvents = PublishSubject[GameState]()
    val menuFactory = new ActionMenuFactory(source, gs, menuEvents)
    def createMenu(node: Node, destination: MapPoint): Unit =
      menuFactory(destination).show(node, Side.Bottom, 0, 0)
    val subscription = mouseEvents
        .filter(_._1.eventType == MouseEvent.MouseClicked)
        .map(TuplePLenses.tuple2First.modify(e => jfx2sfx(e.source.asInstanceOf[jfxs.Node])))
        .subscribe((createMenu _).tupled)

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

  private def setFontWeight(u: CombatUnit, style: String): Unit =
    CombatUnitObject.findIn(u, map).map(cells).get.center.get.setStyle(s"-fx-font-weight: $style;")
  override def highlight(u: CombatUnit) = setFontWeight(u, "900")
  override def disableHighlighting(u: CombatUnit) = setFontWeight(u, "normal")
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
    private def toOpt[T](objectProperty: ObjectProperty[jfxs.Node]): Option[Node] =
      objectProperty.value.opt.map(NodeWrapper.jfx2sfx)
    override def index(d: Direction): Optional[BorderPane, Node] = Optional[BorderPane, Node](bp => d match {
      case Direction.Up => toOpt(bp.top)
      case Direction.Down => toOpt(bp.bottom)
      case Direction.Left => toOpt(bp.left)
      case Direction.Right => toOpt(bp.right)
    })(node => bp => {
      val setter: Node => Unit = d match {
        case Direction.Up => bp.top_=
        case Direction.Down => bp.bottom_=
        case Direction.Left => bp.left_=
        case Direction.Right => bp.right_=
      }
      setter(node)
      bp
    })
  }

  private def toPoint(e: Node): MapPoint = MapPoint(GridPane.getColumnIndex(e), GridPane.getRowIndex(e))
}
