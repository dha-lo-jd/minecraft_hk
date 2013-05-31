package net.minecraft.entity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.LMM_HK_GuiInventory;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSign;
import net.minecraft.item.ItemStack;
import net.minecraft.src.LMM_EntityLittleMaid;
import net.minecraft.src.LMM_GuiInventory;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.village.Village;

import org.lo.d.commons.coords.EntityCoordsSupport;
import org.lo.d.commons.coords.Point2D;
import org.lo.d.commons.coords.Point2DMatrixSupport;
import org.lo.d.commons.coords.Point3D;
import org.lo.d.minecraft.littlemaid.mode.LMM_EntityModeBaseEx;

public class LMM_EntityMode_HouseKeeper extends LMM_EntityModeBaseEx {

	public static final int mmode_HK = 0x0201;
	public static final String MODE_NAME = "HouseKeeper";

	private Village villageObj;

	public LMM_EntityMode_HouseKeeper(LMM_EntityLittleMaid pEntity) {
		super(pEntity);
	}

	@Override
	public void addEntityMode(EntityAITasks pDefaultMove, EntityAITasks pDefaultTargeting) {
		// Healer:0x0082
		EntityAITasks[] ltasks = new EntityAITasks[2];
		ltasks[0] = pDefaultMove;
		ltasks[1] = new EntityAITasks(owner.aiProfiler);

		// 索敵系
		ltasks[1].addTask(1, new EntityAIHurtByTarget(owner, true));

		owner.addMaidMode(ltasks, MODE_NAME, mmode_HK);

	}

	@Override
	public boolean changeMode(EntityPlayer pentityplayer) {
		ItemStack litemstack = owner.maidInventory.getStackInSlot(0);
		if (litemstack != null) {
			if (litemstack.getItem() instanceof ItemSign) {
				owner.setMaidMode(MODE_NAME);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean checkItemStack(ItemStack pItemStack) {
		return pItemStack.getItem() instanceof ItemFood || pItemStack.getItem() instanceof ItemPotion;
	}

	public LMM_GuiInventory getOpenGuiInventory(EntityClientPlayerMP var1, LMM_EntityLittleMaid lentity) {
		return new LMM_HK_GuiInventory(var1, lentity);
	}

	@Override
	public void init() {
		// 登録モードの名称追加
		addLocalization(MODE_NAME);
	}

	@Override
	public int priority() {
		return 7000;
	}

	@Override
	public boolean setMode(int pMode) {
		if (pMode == mmode_HK) {
			return true;
		}
		return false;
	}

	@Override
	public void updateAITick(int pMode) {
		if (pMode == mmode_HK) {
			LMM_EntityLittleMaid maid = owner;
			Village village = maid.worldObj.villageCollectionObj.findNearestVillage(MathHelper.floor_double(maid.posX),
					MathHelper.floor_double(maid.posY), MathHelper.floor_double(maid.posZ), 32);
			villageObj = village;
			if (!maid.isOpenInventory() && maid.isFreedom() && villageObj != null) {
				int villageRadius = villageObj.getVillageRadius();
				ChunkCoordinates center = villageObj.getCenter();
				if (maid.getDistance(center.posX, center.posY, center.posZ) > villageRadius / 2) {
					teleportVillageCenter(maid, new Point3D(center), villageRadius / 2);
				}
			}

			if (villageObj != null) {
				villageUpdate();
				List<EntityCreeper> entityCreepers = getInVillageEntity(EntityCreeper.class);
				for (EntityCreeper entityCreeper : entityCreepers) {
					villageObj.addOrRenewAgressor(entityCreeper);
				}
				villageIconUpdate();
			}
		}
	}

	private <E> List<E> getInVillageEntity(Class<E> entitCls) {
		List<E> l = new ArrayList<E>();
		if (villageObj == null) {
			return l;
		}
		ChunkCoordinates center = villageObj.getCenter();
		int villageRadius = villageObj.getVillageRadius();
		@SuppressWarnings("unchecked")
		List<E> aabb = owner.worldObj.getEntitiesWithinAABB(
				entitCls,
				AxisAlignedBB.getAABBPool().getAABB(center.posX - villageRadius, center.posY - 4,
						center.posZ - villageRadius, center.posX + villageRadius, center.posY + 4,
						center.posZ + villageRadius));
		return aabb;
	}

	private void teleportVillageCenter(LMM_EntityLittleMaid maid, Point3D center, int radius) {
		//中心より上方向でいけるところを探索
		for (int l = 0; l < 8; l++) {
			int dY = l;
			Point3D offsetedPoint = center.addY(dY);
			if (EntityCoordsSupport.checkSafeAreaAbsolute(maid, offsetedPoint)) {
				offsetedPoint.setEntityPosition(maid);
				maid.setHomeArea(offsetedPoint.getX(), offsetedPoint.getY(), offsetedPoint.getZ(), maid.dimension);
				return;
			}
		}
		for (Point2D p : Point2DMatrixSupport.getNearestPointMatrix2D(radius)) {
			for (int l = 0; l < 8; l++) {
				int dY = l;
				Point3D offsetedPoint = center.addPoint(new Point3D(p, dY));
				if (EntityCoordsSupport.checkSafeAreaAbsolute(maid, offsetedPoint)) {
					offsetedPoint.setEntityPosition(maid);
					maid.setHomeArea(offsetedPoint.getX(), offsetedPoint.getY(), offsetedPoint.getZ(), maid.dimension);
					return;
				}
			}
		}
		//下に下りていく
		for (int l = 0; l < 16; l++) {
			int dY = -l;
			Point3D offsetedPoint = center.addY(dY);
			if (EntityCoordsSupport.checkSafeAreaAbsolute(maid, offsetedPoint)) {
				offsetedPoint.setEntityPosition(maid);
				maid.setHomeArea(offsetedPoint.getX(), offsetedPoint.getY(), offsetedPoint.getZ(), maid.dimension);
				return;
			}
		}
		for (Point2D p : Point2DMatrixSupport.getNearestPointMatrix2D(radius)) {
			for (int l = 0; l < 16; l++) {
				int dY = -l;
				Point3D offsetedPoint = center.addPoint(new Point3D(p, dY));
				if (EntityCoordsSupport.checkSafeAreaAbsolute(maid, offsetedPoint)) {
					offsetedPoint.setEntityPosition(maid);
					maid.setHomeArea(offsetedPoint.getX(), offsetedPoint.getY(), offsetedPoint.getZ(), maid.dimension);
					return;
				}
			}
		}
	}

	private void villageIconUpdate() {
		//		if (mod_HouseKeeper.visibleIcon && maid.villageIcon == null) {
		//			EntityPosition ep = new EntityPosition(maid);
		//			maid.villageIcon = new EntityHKVillageIcon(maid.worldObj, maid);
		//			ep.setPositonAndRotationToEntity(maid.villageIcon);
		//			maid.worldObj.spawnEntityInWorld(maid.villageIcon);
		//		}
	}

	private void villageUpdate() {
		//		villagerList = getInVillageEntity(net.minecraft.src.EntityVillager.class);
		//		maid.maidList = mod_HouseKeeper.HK_MANAGER.registAndUpdateMaids(maid,
		//				getInVillageEntity(net.minecraft.src.EntityLittleMaid.class));
		//
		//		List<EntityMountDummy> cl = new ArrayList<EntityMountDummy>(
		//				maid.mountList);
		//		for (EntityMountDummy mountDummy : cl) {
		//			if (mountDummy.isDead) {
		//				maid.mountList.remove(mountDummy);
		//			}
		//		}
		//
		//		int adultVillagerCount = 0;
		//		for (EntityVillager villager : villagerList) {
		//			if (villager.getGrowingAge() >= 0) {
		//				adultVillagerCount++;
		//			}
		//		}
		//
		//		int maidCount = maid.maidList.size();
		//		boolean flag = canTeach(adultVillagerCount, maidCount);
		//
		//		for (EntityVillager villager : villagerList) {
		//			if (flag && maid.teach && -100 < villager.getGrowingAge()
		//					&& villager.getGrowingAge() < 0) {
		//				World world = villager.worldObj;
		//				EntityLittleMaid maid = new EntityLittleMaid(world);
		//				maid.setPositionAndRotation(villager.posX, villager.posY,
		//						villager.posZ, villager.rotationYaw,
		//						villager.rotationPitch);
		//				world.spawnEntityInWorld(maid);
		//				world.setEntityDead(villager);
		//			}
		//		}
	}
}
