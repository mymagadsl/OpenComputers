package li.cil.oc.server

import cpw.mods.fml.common.{TickType, ITickHandler}
import java.util
import li.cil.oc.api
import li.cil.oc.common.tileentity.TileEntity
import scala.collection.mutable

object TickHandler extends ITickHandler {
  val pendingAdds = mutable.Buffer.empty[() => Unit]

  def schedule(tileEntity: TileEntity) = pendingAdds.synchronized {
    pendingAdds += (() => if (!tileEntity.isInvalid) api.Network.joinOrCreateNetwork(tileEntity))
  }

  override def getLabel = "OpenComputers Network Initialization Ticker"

  override def ticks() = util.EnumSet.of(TickType.SERVER)

  override def tickStart(`type`: util.EnumSet[TickType], tickData: AnyRef*) {}

  override def tickEnd(`type`: util.EnumSet[TickType], tickData: AnyRef*) = pendingAdds.synchronized {
    for (callback <- pendingAdds) {
      callback()
    }
    pendingAdds.clear()
  }
}
