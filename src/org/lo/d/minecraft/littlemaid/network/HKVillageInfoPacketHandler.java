package org.lo.d.minecraft.littlemaid.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.WeakHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.village.Village;

import org.lo.d.commons.coords.Point3D;
import org.lo.d.commons.network.KawoCommonsPacketHandler.KawoCommonsCustomPacketHandler;
import org.lo.d.minecraft.littlemaid.HouseKeeper;
import org.lo.d.minecraft.littlemaid.mode.strategy.VillageInsideStrategy;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

@KawoCommonsCustomPacketHandler(HouseKeeper.HK_VILLAGE_INFO_PACKET_NAME)
public class HKVillageInfoPacketHandler {
	public static class VillageInfo {

		Point3D villageCenter;
		int villageRadius;

		int villagerCount;
		int adultVillagerCount;

		boolean valid = false;

		public VillageInfo() {
			villageCenter = new Point3D(0, 0, 0);
		}

		public VillageInfo(Village village, int villagerCount, int adultVillagerCount) {
			super();
			villageCenter = new Point3D(village.getCenter());
			villageRadius = village.getVillageRadius();
			this.villagerCount = villagerCount;
			this.adultVillagerCount = adultVillagerCount;
			valid = true;
		}

		private VillageInfo(NBTTagCompound nbtTag) {
			villageCenter = new Point3D(nbtTag.getInteger("center.posX"), nbtTag.getInteger("center.posY"),
					nbtTag.getInteger("center.posZ"));
			villageRadius = nbtTag.getInteger("radius");
			villagerCount = nbtTag.getInteger("count");
			adultVillagerCount = nbtTag.getInteger("adultCount");
			valid = nbtTag.getBoolean("valid");

		}

		public int getAdultVillagerCount() {
			return adultVillagerCount;
		}

		public Point3D getVillageCenter() {
			return villageCenter;
		}

		public int getVillageRadius() {
			return villageRadius;
		}

		public int getVillagerCount() {
			return villagerCount;
		}

		public boolean isValid() {
			return valid;
		}

		public void writeNBTTag(NBTTagCompound nbtTag) {
			nbtTag.setInteger("center.posX", villageCenter.getX());
			nbtTag.setInteger("center.posY", villageCenter.getY());
			nbtTag.setInteger("center.posZ", villageCenter.getZ());
			nbtTag.setInteger("radius", villageRadius);
			nbtTag.setInteger("count", villagerCount);
			nbtTag.setInteger("adultCount", adultVillagerCount);
			nbtTag.setBoolean("valid", valid);
		}
	}

	private static WeakHashMap<Entity, VillageInsideStrategy> map = new WeakHashMap<>();

	public static VillageInsideStrategy put(Entity key, VillageInsideStrategy value) {
		return map.put(key, value);
	}

	public static void sendPacket(Entity maid, VillageInfo villageInfo) {

		NBTTagCompound nbtTag = new NBTTagCompound();
		nbtTag.setInteger("entityId", maid.entityId);
		villageInfo.writeNBTTag(nbtTag);

		try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); DataOutputStream dos = new DataOutputStream(bos);) {
			NBTBase.writeNamedTag(nbtTag, dos);
			byte[] data = bos.toByteArray();
			Packet250CustomPayload packet = new Packet250CustomPayload(HouseKeeper.HK_VILLAGE_INFO_PACKET_NAME, data);
			PacketDispatcher.sendPacketToAllPlayers(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@KawoCommonsCustomPacketHandler.HandleMethod
	public void handle(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		try (ByteArrayInputStream bis = new ByteArrayInputStream(packet.data);
				DataInputStream dis = new DataInputStream(bis);) {
			NBTBase nbt = NBTBase.readNamedTag(dis);
			if (!(nbt instanceof NBTTagCompound)) {
				return;
			}
			NBTTagCompound nbtTag = (NBTTagCompound) nbt;
			int entityId = nbtTag.getInteger("entityId");
			Entity maid = ((EntityPlayer) player).worldObj.getEntityByID(entityId);
			if (maid == null) {
				return;
			}

			if (!map.containsKey(maid)) {
				return;
			}

			map.get(maid).setVillageInfo(new VillageInfo(nbtTag));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
