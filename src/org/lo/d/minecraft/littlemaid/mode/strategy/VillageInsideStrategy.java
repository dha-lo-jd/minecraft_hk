package org.lo.d.minecraft.littlemaid.mode.strategy;

import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.src.LMM_EntityLittleMaid;
import net.minecraft.src.LMM_EntityMode_HouseKeeper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;
import net.minecraft.world.World;

import org.lo.d.commons.coords.EntityCoordsSupport;
import org.lo.d.commons.coords.EntityPoint3DDouble;
import org.lo.d.commons.coords.EntityPoint3DInt;
import org.lo.d.commons.coords.Point2D;
import org.lo.d.commons.coords.Point2DMatrixSupport;
import org.lo.d.commons.coords.Point3D;
import org.lo.d.commons.coords.Point3DDouble;
import org.lo.d.minecraft.littlemaid.MaidExIcon;
import org.lo.d.minecraft.littlemaid.network.HKVillageInfoPacketHandler;
import org.lo.d.minecraft.littlemaid.network.HKVillageInfoPacketHandler.VillageInfo;

import com.google.common.collect.Lists;

public class VillageInsideStrategy extends VillageStrategy.Impl {
	private class ClientDelegate implements Delegate {
		private class InvalidDelegate implements Delegate {

			@Override
			public int getAdultVillagerCount() {
				return 0;
			}

			@Override
			public AxisAlignedBB getMyArea() {
				return VillageInsideStrategy.super.getMyArea();
			}

			@Override
			public List<EntityVillager> getMyVillagers() {
				return Lists.newArrayList();
			}

			@Override
			public int getVillagerCount() {
				return 0;
			}

			@Override
			public void onUpdateStrategy() {
			}

			@Override
			public boolean shouldStrategy() {
				return false;
			}

		}

		private class ValidDelegate implements Delegate {

			@Override
			public int getAdultVillagerCount() {
				return villageInfo.getAdultVillagerCount();
			}

			@Override
			public AxisAlignedBB getMyArea() {
				Point3D vc = villageInfo.getVillageCenter();
				int r = villageInfo.getVillageRadius();
				return AxisAlignedBB.getAABBPool().getAABB(vc.getX() - r, vc.getY() - 4, vc.getZ() - r, vc.getX() + r,
						vc.getY() + 4, vc.getZ() + r);
			}

			@Override
			public List<EntityVillager> getMyVillagers() {
				List<EntityVillager> list = Lists.newArrayList();
				final LMM_EntityLittleMaid maid = mode.owner;
				List<?> l = maid.worldObj
						.getEntitiesWithinAABBExcludingEntity(maid, getMyArea(), getVillagerSelector());
				for (Object o : l) {
					list.add((EntityVillager) o);
				}
				return list;
			}

			@Override
			public int getVillagerCount() {
				return villageInfo.getVillagerCount();
			}

			@Override
			public void onUpdateStrategy() {
			}

			@Override
			public boolean shouldStrategy() {
				return true;
			}

		}

		InvalidDelegate invalidDelegate = new InvalidDelegate();
		ValidDelegate validDelegate = new ValidDelegate();
		Delegate currentDelegate;

		@Override
		public int getAdultVillagerCount() {
			return currentDelegate.getAdultVillagerCount();
		}

		@Override
		public AxisAlignedBB getMyArea() {
			return currentDelegate.getMyArea();
		}

		@Override
		public List<EntityVillager> getMyVillagers() {
			return currentDelegate.getMyVillagers();
		}

		@Override
		public int getVillagerCount() {
			return currentDelegate.getVillagerCount();
		}

		@Override
		public void onUpdateStrategy() {
			currentDelegate.onUpdateStrategy();
		}

		@Override
		public boolean shouldStrategy() {
			HKVillageInfoPacketHandler.put(mode.owner, VillageInsideStrategy.this);
			if (villageInfo != null && villageInfo.isValid() && villageInfo.getVillagerCount() > 0) {
				currentDelegate = validDelegate;
			} else {
				currentDelegate = invalidDelegate;
			}
			return currentDelegate.shouldStrategy();
		}
	}

	private interface Delegate {
		public int getAdultVillagerCount();

		public AxisAlignedBB getMyArea();

		public List<EntityVillager> getMyVillagers();

		public int getVillagerCount();

		public void onUpdateStrategy();

		public boolean shouldStrategy();
	}

	private static class HKVillagerIcon extends MaidExIcon {
		private final VillageInsideStrategy strategy;

		public HKVillagerIcon(VillageInsideStrategy strategy) {
			this.strategy = strategy;
		}

		@Override
		public double getOffsetX() {
			return 4F / 16F;
		}

		@Override
		public String getText() {
			return String.valueOf(strategy.getVillagerCount());
		}

		@Override
		public int getTextColor() {
			if (strategy.getVillagerCount() > 0) {
				return 0xffffff;
			}
			return 0xff0000;
		}

		@Override
		public String getTexture() {
			return "/gui/icon_villager_azi.png";
		}
	}

	private class ServerDelegate implements Delegate {
		@Override
		public int getAdultVillagerCount() {
			int adultVillagerCount = 0;
			for (EntityVillager villager : getMyVillagers()) {
				if (villager.getGrowingAge() >= 0) {
					adultVillagerCount++;
				}
			}
			return adultVillagerCount;
		}

		@Override
		public AxisAlignedBB getMyArea() {
			if (villageObj == null) {
				return mode.owner.boundingBox;
			}
			Point3D vc = new Point3D(villageObj.getCenter());
			int r = villageObj.getVillageRadius();
			return AxisAlignedBB.getAABBPool().getAABB(vc.getX() - r, vc.getY() - 4, vc.getZ() - r, vc.getX() + r,
					vc.getY() + 4, vc.getZ() + r);
		}

		@Override
		public List<EntityVillager> getMyVillagers() {
			List<EntityVillager> list = Lists.newArrayList();
			final LMM_EntityLittleMaid maid = mode.owner;
			List<?> l = maid.worldObj.getEntitiesWithinAABBExcludingEntity(maid, getMyArea(), getVillagerSelector());
			for (Object o : l) {
				list.add((EntityVillager) o);
			}
			return list;
		}

		@Override
		public int getVillagerCount() {
			return getMyVillagers().size();
		}

		@Override
		public void onUpdateStrategy() {
			moveVillageCenter();

		}

		@Override
		public boolean shouldStrategy() {
			LMM_EntityLittleMaid maid = mode.owner;
			World w = maid.worldObj;
			Village village = w.villageCollectionObj.findNearestVillage(MathHelper.floor_double(maid.posX),
					MathHelper.floor_double(maid.posY), MathHelper.floor_double(maid.posZ), 32);
			villageObj = village;
			boolean result = villageObj != null;
			int adultVillagerCount = 0;
			List<EntityVillager> villagerList = getMyVillagers();
			for (EntityVillager villager : villagerList) {
				if (villager.getGrowingAge() >= 0) {
					adultVillagerCount++;
				}
			}
			if (result) {
				VillageInfo info = new VillageInfo(villageObj, villagerList.size(), adultVillagerCount);
				HKVillageInfoPacketHandler.sendPacket(maid, info);
				setVillageInfo(info);
			} else {
				VillageInfo info = new VillageInfo();
				HKVillageInfoPacketHandler.sendPacket(maid, info);
				setVillageInfo(info);
			}
			return result;
		}

		private Point3D getSafetyVillageCenter(LMM_EntityLittleMaid maid, Point3D center, int radius) {

			int maxHeight = center.getY();
			int minHeight = center.getY();
			for (Object o : villageObj.getVillageDoorInfoList()) {
				VillageDoorInfo door = (VillageDoorInfo) o;

				if (maxHeight < door.posY) {
					maxHeight = door.posY;
				}
				if (minHeight > door.posY) {
					minHeight = door.posY;
				}
			}

			int relativeY;
			relativeY = maxHeight - center.getY() + 1;
			//中心より上方向でいけるところを探索
			for (int l = 0; l < relativeY; l++) {
				int dY = l;
				Point3D offsetedPoint = center.addY(dY);
				if (EntityCoordsSupport.checkSafeAreaAbsolute(maid, offsetedPoint)) {
					return offsetedPoint;
				}
			}
			for (Point2D p : Point2DMatrixSupport.getNearestPointMatrix2D(radius)) {
				for (int l = 0; l < relativeY; l++) {
					int dY = l;
					Point3D offsetedPoint = center.addPoint(new Point3D(p, dY));
					if (EntityCoordsSupport.checkSafeAreaAbsolute(maid, offsetedPoint)) {
						return offsetedPoint;
					}
				}
			}

			//下に下りていく
			relativeY = center.getY() - minHeight + 1;
			for (int l = 0; l < relativeY; l++) {
				int dY = -l;
				Point3D offsetedPoint = center.addY(dY);
				if (EntityCoordsSupport.checkSafeAreaAbsolute(maid, offsetedPoint)) {
					return offsetedPoint;
				}
			}
			for (Point2D p : Point2DMatrixSupport.getNearestPointMatrix2D(radius)) {
				for (int l = 0; l < relativeY; l++) {
					int dY = -l;
					Point3D offsetedPoint = center.addPoint(new Point3D(p, dY));
					if (EntityCoordsSupport.checkSafeAreaAbsolute(maid, offsetedPoint)) {
						return offsetedPoint;
					}
				}
			}

			return null;
		}

		private void moveVillageCenter() {
			final LMM_EntityLittleMaid maid = mode.owner;
			EntityPoint3DDouble entityPoint3DDouble = new EntityPoint3DDouble(maid);

			Point3D vc = villageInfo.getVillageCenter();
			int r = villageObj.getVillageRadius();
			Point3D center = getSafetyVillageCenter(maid, vc, r);
			if (center != null) {
				double dist = entityPoint3DDouble.distanceToSq(center);
				Point3D pos = center;
				if (dist > 8 * 8) {
					Point3D oldHome = new Point3D(maid.getHomePosition());
					double homeDist = oldHome.distanceToSq(center);
					if (homeDist > 0) {
						double d = 4 / MathHelper.sqrt_double(dist);
						pos = new EntityPoint3DInt(maid);
						Point3DDouble movePos = new Point3DDouble(center).addPoint(entityPoint3DDouble.flip());
						double x = movePos.getX() * d;
						double y = movePos.getY() * d;
						double z = movePos.getZ() * d;
						movePos = entityPoint3DDouble.addPoint(new Point3DDouble(x, y, z));
						if (maid.getNavigator().noPath()) {
							maid.getNavigator().tryMoveToXYZ(movePos.getX(), movePos.getY(), movePos.getZ(),
									maid.getAIMoveSpeed());
						}
					}
				}
				maid.setHomeArea(pos.getX(), pos.getY(), pos.getZ(), maid.dimension);
			}
		}
	}

	private Village villageObj;

	private HKVillagerIcon villagerIcon;

	private VillageInfo villageInfo;

	private Delegate currentDelegate;

	public VillageInsideStrategy(LMM_EntityMode_HouseKeeper mode) {
		super(mode);
		villagerIcon = new HKVillagerIcon(this);

		LMM_EntityLittleMaid maid = mode.owner;
		if (maid == null) {
			currentDelegate = null;
			return;
		}
		World w = maid.worldObj;
		if (w == null) {
			currentDelegate = null;
			return;
		}
		if (w.isRemote) {
			currentDelegate = new ClientDelegate();
		} else {
			currentDelegate = new ServerDelegate();
		}
	}

	@Override
	public int getAdultVillagerCount() {
		return currentDelegate.getAdultVillagerCount();
	}

	@Override
	public List<MaidExIcon> getIcons() {
		List<MaidExIcon> list = Lists.newArrayList();
		list.add(villagerIcon);
		return list;
	}

	@Override
	public AxisAlignedBB getMyArea() {
		return currentDelegate.getMyArea();
	}

	@Override
	public List<EntityVillager> getMyVillagers() {
		return currentDelegate.getMyVillagers();
	}

	public VillageInfo getVillageInfo() {
		return villageInfo;
	}

	@Override
	public int getVillagerCount() {
		return currentDelegate.getVillagerCount();
	}

	@Override
	public void onUpdateStrategy() {
		super.onUpdateStrategy();
		currentDelegate.onUpdateStrategy();
	}

	public void setVillageInfo(VillageInfo villageInfo) {
		this.villageInfo = villageInfo;
	}

	@Override
	public boolean shouldStrategy() {
		return currentDelegate.shouldStrategy();
	}

	protected IEntitySelector getVillagerSelector() {
		return new IEntitySelector() {
			@Override
			public boolean isEntityApplicable(Entity entity) {
				if (entity == mode.owner) {
					return false;
				}
				if (!(entity instanceof EntityVillager)) {
					return false;
				}
				return true;
			}
		};
	}
}
