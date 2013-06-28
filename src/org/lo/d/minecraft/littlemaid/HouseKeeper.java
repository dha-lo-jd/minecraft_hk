package org.lo.d.minecraft.littlemaid;

import java.lang.reflect.InvocationTargetException;

import net.minecraftforge.common.Configuration;

import org.lo.d.commons.configuration.ConfigurationSupport;
import org.lo.d.commons.configuration.ConfigurationSupport.IntConfig;
import org.lo.d.commons.network.KawoCommonsPacketHandler;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = "LMM_EntityMode_HouseKeeper", name = "LMM Mode HouseKeeper", version = "0.0.1")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = { HouseKeeper.HK_VILLAGE_INFO_PACKET_NAME }, packetHandler = KawoCommonsPacketHandler.class)
@ConfigurationSupport.ConfigurationMod
public class HouseKeeper {

	@IntConfig(defaultValue = 60, name = "waitMargin")
	public static int waitMargin;

	@IntConfig(defaultValue = 25, name = "teachRate")
	public static int teachRate = 25;

	@SideOnly(Side.SERVER)
	public static HouseKeeperTickHandler tickHandler;

	public static final String HK_VILLAGE_INFO_PACKET_NAME = "HKVillageInfo";

	@Mod.Init
	public void init(FMLInitializationEvent event) {
		if (event.getSide().isServer()) {
			tickHandler = new HouseKeeperTickHandler();
			TickRegistry.registerTickHandler(tickHandler, Side.SERVER);
		}
	}

	@Mod.PreInit
	public void preInit(FMLPreInitializationEvent event) throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		ConfigurationSupport.load(getClass(), event, config);
		config.save();
	}
}
