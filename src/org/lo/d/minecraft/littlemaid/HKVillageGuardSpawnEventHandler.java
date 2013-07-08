package org.lo.d.minecraft.littlemaid;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Sets;

public class HKVillageGuardSpawnEventHandler {

	public interface Handler {

		void doHandle(EnderTeleportEvent event);

		void doHandle(EntityJoinWorldEvent event);

		World getWorld();

		boolean isLiving();

		boolean shouldHandle(EnderTeleportEvent event);

		boolean shouldHandle(EntityJoinWorldEvent event);
	}

	private static final HashMultimap<World, Handler> handlers = HashMultimap.create();

	public static void regist(Handler handler) {
		handlers.put(handler.getWorld(), handler);
	}

	@ForgeSubscribe(priority = EventPriority.NORMAL)
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		World world = event.world;
		if (world.isRemote || event.entity.addedToChunk) {
			return;
		}

		if (!handlers.containsKey(world)) {
			return;
		}

		Set<Handler> hs = Sets.newHashSet(handlers.get(world));
		for (Handler handler : hs) {
			if (!handler.isLiving()) {
				handlers.remove(world, handler);
				continue;
			}

			if (handler.shouldHandle(event)) {
				handler.doHandle(event);
			}
		}
	}

	@ForgeSubscribe(priority = EventPriority.NORMAL)
	public void onTeleportTo(EnderTeleportEvent event) {
		World world = event.entity.worldObj;
		if (world.isRemote || event.entity instanceof EntityPlayer) {
			return;
		}

		if (!handlers.containsKey(world)) {
			return;
		}

		Set<Handler> hs = Sets.newHashSet(handlers.get(world));
		for (Handler handler : hs) {
			if (!handler.isLiving()) {
				handlers.remove(world, handler);
				continue;
			}

			if (handler.shouldHandle(event)) {
				handler.doHandle(event);
			}
		}
	}
}
