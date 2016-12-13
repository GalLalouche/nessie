//package com.nessie.gm
//
//import java.awt.Color
//import javax.swing.plaf.ColorUIResource
//
//import com.nessie.map.model.BattleMap
//import com.nessie.map.{BattleMapObject, CombatUnitObject, EmptyMapObject}
//import com.nessie.units.CombatUnit
//
//import scala.swing._
//
//object SwingViewFactory {
//  private class SwingView extends View {
//    def createButton(o: BattleMapObject): Component = new Button(string(o)) {
//      val s = new Dimension(75, 75)
//      minimumSize = s
//      maximumSize = s
//      preferredSize = s
//    }
//
//    private def string(o: BattleMapObject): String = o match {
//      case EmptyMapObject => "_"
//      case CombatUnitObject(c: CombatUnit) => c.getClass.getSimpleName.take(2)
//    }
//    override def toString = "MapPanel of " + v
//    private var v: GridPanel = _
//    private var width: Int = -1
//    private val BACKGROUND_COLOR: Color = new ColorUIResource(238, 238, 238)
//    private def createGridPanel(m: BattleMap): GridPanel = new GridPanel(m.width, m.height)
//    private def generateMap(m: BattleMap): GridPanel = {
//      v = createGridPanel(m)
//      v.contents ++= m.points map {
//        case (point, o) =>
//          val b = createButton(o)
//          v.listenTo(b)
//          //          v.reactions += {
//          //            case y: ActionEvent if y.source == b => owner ! CellClicked(point)
//          //          }
//          b
//      }
//      //      unselect
//      width = m.width
//      v
//    }
//    //    private def pointToIndex(p: MapPoint): Int = p.y * width + p.x
//    //    def select(p: MapPoint) {
//    //      require(p != null)
//    //      v.contents(pointToIndex(p)).background = java.awt.Color.RED
//    //    }
//    //    def unselect = v.contents.foreach(_.background = BACKGROUND_COLOR)
//    override def updateState(state: GameState): Unit = {
//      val frame = new Frame()
//      frame.contents = generateMap(state.map)
//      frame.open()
//    }
//    override def moveUnit(u: CombatUnit)(map: BattleMap): BattleMap = ???
//  }
//  override def create(): View = new SwingView
//}
