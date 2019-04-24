package com.nessie.view.sfx

import java.io.IOException

import com.nessie.gm.{GameState, GameStateChange}
import com.nessie.model.map._
import com.nessie.model.units.CombatUnit
import com.nessie.model.units.abilities.{CanBeUsed, MoveAbility}
import com.nessie.view.sfx.MapGrid._
import com.nessie.view.sfx.RichNode._
import common.rich.RichT._
import common.rich.func.{MoreIterableInstances, MoreObservableInstances, ToMoreFunctorOps, TuplePLenses}
import javafx.{scene => jfxs}
import monocle.Optional
import monocle.function.Index
import rx.lang.scala.Observable
import rx.lang.scala.subjects.PublishSubject
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.{Pos, Side}
import scalafx.scene.Node
import scalafx.scene.control.{Button, Label}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout._

private class MapGrid(map: BattleMap, customizer: ScalaFxMapCustomizer)
    extends ToMoreFunctorOps with MoreObservableInstances with MoreIterableInstances {
  private val cells: Map[MapPoint, BorderPane] = map.objects.map {case (p, o) =>
    p -> MapGrid.createCell(o, customizer).<|(GridPane.setConstraints(_, p.x + 1, p.y + 1))
  }.toMap

  val mouseEvents: Observable[(MouseEvent, MapPoint)] =
    NodeUtils.mouseEvents(cells.mapValues(_.center.get))

  // Color walls.
  // We use black border between grid elements if there is a wall between the two map points, grey otherwise.
  for ((pd, bo) <- map.betweenObjects) {
    val d = pd.direction
    val (wallWidth, wallHeight) =
      if (d == Direction.Left || d == Direction.Right) WallWidth -> CellSide else CellSide -> WallWidth
    borderPaneIndex.index(d).set(new Pane {
      prefWidth = wallWidth
      prefHeight = wallHeight
      style = Styles.backgroundColor(if (bo == Wall) "black" else "grey")
    })(cells(pd.toPoint))
  }

  // Color cells
  for ((point, bo) <- cells) {
    val obj = map(point)
    customizer.cellColor.orElse(defaultColors).lift(obj).foreach(bo.setBaseColor)
  }

  val node: Node = {
    val yRow = createCoordinateCells("X\\Y" +: 0.until(map.width), (_, 0))
    val xRow = createCoordinateCells(0.until(map.width), i => (0, i + 1))
    new GridPane() {
      children = xRow ++ yRow ++ cells.values
    }
  }

  private def getMove(source: MapPoint, gs: GameState): PromiseZ[GameStateChange] = {
    val $ = PromiseZ[GameStateChange]()
    val menuEvents = PublishSubject[GameStateChange]()
    val menuFactory = new ActionMenuFactory(source, gs, menuEvents)
    def createMenu(node: Node, destination: MapPoint): Unit =
      menuFactory(destination).show(node, Side.Bottom, 0, 0)
    val subscription = mouseEvents
        .filter(_._1.eventType == MouseEvent.MouseClicked)
        .map(TuplePLenses.tuple2First.modify(_.source.asInstanceOf[jfxs.Node].toScalaNode))
        .subscribe((createMenu _).tupled)

    menuEvents.subscribe(e => {
      subscription.unsubscribe
      $.fulfill(e)
    })
    lastPromise = $
    $
  }

  def nextState(u: CombatUnit)(gs: GameState): PromiseZ[GameStateChange] = {
    def highlight(location: MapPoint, moveAbility: MoveAbility): Unit = {
      cells.filterKeys(CanBeUsed(moveAbility)(map, location, _))
          .values
          .foreach(_.setBaseColor("green"))
      cells(location).setBaseColor("blue")
    }
    val unitLocation = CombatUnitObject.findIn(u, gs.map).get
    highlight(unitLocation, u.moveAbility)
    getMove(unitLocation, gs)
  }

  val highlighter: Focuser[CombatUnit] = new Focuser[CombatUnit] {
    private def changeWeight(u: CombatUnit, style: String): Unit =
      CombatUnitObject.findIn(u, map).map(cells).get.center.get.setFontWeight(style)
    override def focus(u: CombatUnit) = changeWeight(u, "900")
    override def unfocus(u: CombatUnit) = changeWeight(u, "normal")
  }

  private var lastPromise: PromiseZ[_] = _
  def killLastTask(): Unit = lastPromise.opt.foreach(_.fail(new IOException("User closed the GUI")))
}

private object MapGrid {
  private val CellSide = 40
  private val WallWidth = 5

  private def defaultColors: PartialFunction[BattleMapObject, String] = {
    case FullWall => "black"
  }
  private def createCoordinateCells(seq: Seq[_], f: Int => (Int, Int)): Seq[BorderPane] =
    seq.map(_.toString).zipWithIndex.map {case (l, i) =>
      val (x, y) = f(i)
      createCoordinateCell(l, x, y)
    }
  private def createCoordinateCell(text: String, x: Int, y: Int): BorderPane = new BorderPane() {
    center = new Label(text) {
      prefHeight = CellSide
      prefWidth = CellSide
      alignment = Pos.Center
    } <| (_ setBaseColor "white")
  } <| (GridPane.setConstraints(_, x, y))

  private def createCell(o: BattleMapObject, customizer: ScalaFxMapCustomizer): BorderPane = {
    def text(o: BattleMapObject): String = customizer.text.lift(o).getOrElse(o match {
      case EmptyMapObject => ""
      case FullWall => ""
      case CombatUnitObject(u) => NodeUtils.shortName(u)
    })
    new BorderPane() {
      center = new Button(text(o)) {
        prefHeight = CellSide
        prefWidth = CellSide
      }
    }
  }

  private val borderPaneIndex: Index[BorderPane, Direction, Node] = new Index[BorderPane, Direction, Node] {
    private def toOpt[T](objectProperty: ObjectProperty[jfxs.Node]): Option[Node] =
      objectProperty.value.opt.map(_.toScalaNode)
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
}
