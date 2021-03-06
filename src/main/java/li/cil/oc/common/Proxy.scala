package li.cil.oc.common

import cpw.mods.fml.common.event._
import cpw.mods.fml.common.network.NetworkRegistry
import cpw.mods.fml.common.registry.{TickRegistry, GameRegistry}
import cpw.mods.fml.relauncher.Side
import li.cil.oc._
import li.cil.oc.common.asm.SimpleComponentTickHandler
import li.cil.oc.server.component.Keyboard
import li.cil.oc.server.network.Network
import li.cil.oc.server.{TickHandler, driver, fs, network}
import li.cil.oc.util.WirelessNetwork
import net.minecraftforge.common.MinecraftForge

class Proxy {
  def preInit(e: FMLPreInitializationEvent): Unit = {
    Settings.load(e.getSuggestedConfigurationFile)

    Blocks.init()
    Items.init()

    api.Driver.instance = driver.Registry
    api.FileSystem.instance = fs.FileSystem
    api.Network.instance = network.Network
  }

  def init(e: FMLInitializationEvent): Unit = {
    api.Driver.add(driver.item.AbstractBusCard)
    api.Driver.add(driver.item.FileSystem)
    api.Driver.add(driver.item.GraphicsCard)
    api.Driver.add(driver.item.InternetCard)
    api.Driver.add(driver.item.Memory)
    api.Driver.add(driver.item.NetworkCard)
    api.Driver.add(driver.item.Processor)
    api.Driver.add(driver.item.RedstoneCard)
    api.Driver.add(driver.item.UpgradeCrafting)
    api.Driver.add(driver.item.UpgradeGenerator)
    api.Driver.add(driver.item.UpgradeNavigation)
    api.Driver.add(driver.item.UpgradeSign)
    api.Driver.add(driver.item.UpgradeSolarGenerator)
    api.Driver.add(driver.item.WirelessNetworkCard)

    Recipes.init()
    GameRegistry.registerCraftingHandler(CraftingHandler)
  }

  def postInit(e: FMLPostInitializationEvent): Unit = {
    // Don't allow driver registration after this point, to avoid issues.
    driver.Registry.locked = true

    TickRegistry.registerTickHandler(TickHandler, Side.SERVER)
    TickRegistry.registerTickHandler(SimpleComponentTickHandler.Instance, Side.SERVER)
    GameRegistry.registerPlayerTracker(Keyboard)
    NetworkRegistry.instance.registerConnectionHandler(ConnectionHandler)
    MinecraftForge.EVENT_BUS.register(Network)
    MinecraftForge.EVENT_BUS.register(WirelessNetwork)
  }
}