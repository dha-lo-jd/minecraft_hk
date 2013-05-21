package net.minecraft.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.LMM_GuiInventory;
import net.minecraft.client.gui.LMM_HK_GuiInventory;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.LMM_ContainerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSign;
import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.mod_LMM_littleMaidMob;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.village.Village;
import scala.collection.mutable.StringBuilder;

import com.google.common.collect.Sets;

public class LMM_EntityMode_HouseKeeper extends LMM_EntityModeBase {

	private interface AllowInteractCondition {
		public boolean isAllow(LMM_EntityLittleMaid entityLittleMaid, EntityPlayer pentityplayer, ItemStack pitemstack);
	}

	private static abstract class AllowInteractRemainsContractCondition implements AllowInteractCondition {
		@Override
		public boolean isAllow(LMM_EntityLittleMaid entityLittleMaid, EntityPlayer pentityplayer, ItemStack pitemstack) {
			return entityLittleMaid.isRemainsContract() && andIsAllow(entityLittleMaid, pentityplayer, pitemstack);
		}

		protected abstract boolean andIsAllow(LMM_EntityLittleMaid entityLittleMaid, EntityPlayer pentityplayer,
				ItemStack pitemstack);

	}

	public static final int mmode_HK = 0x0201;
	public static final String MODE_NAME = "HouseKeeper";

	private Village villageObj;
	private static final int[] dirTable = new int[] {
			0, 1, -1, };
	private static final Set<Integer> allowItemIds = getAllowItemIds(new Item[] {
			Item.sugar, Item.dyePowder, Item.feather, Item.saddle, Item.gunpowder, Item.book,
	});

	private static final Set<Integer> allowStrikeItemIds = getAllowItemIds(new Item[] {
			Item.sugar, Item.cake,
	});

	private static final AllowInteractCondition[] allowInteractConditions = new AllowInteractCondition[] {
			new AllowInteractRemainsContractCondition() {
				@Override
				public boolean andIsAllow(LMM_EntityLittleMaid entityLittleMaid, EntityPlayer pentityplayer,
						ItemStack pitemstack) {
					return allowItemIds.contains(pitemstack.itemID);
				}
			},
			new AllowInteractRemainsContractCondition() {
				@Override
				public boolean andIsAllow(LMM_EntityLittleMaid entityLittleMaid, EntityPlayer pentityplayer,
						ItemStack pitemstack) {
					return (pitemstack.itemID == Item.glassBottle.itemID) && (entityLittleMaid.experienceValue >= 5);
				}
			},
			new AllowInteractRemainsContractCondition() {
				@Override
				public boolean andIsAllow(LMM_EntityLittleMaid entityLittleMaid, EntityPlayer pentityplayer,
						ItemStack pitemstack) {
					return (pitemstack.getItem() instanceof ItemPotion);
				}
			},
			new AllowInteractRemainsContractCondition() {
				@Override
				public boolean andIsAllow(LMM_EntityLittleMaid entityLittleMaid, EntityPlayer pentityplayer,
						ItemStack pitemstack) {
					return (entityLittleMaid.isFreedom() && pitemstack.itemID == Item.redstone.itemID);
				}
			},
			new AllowInteractCondition() {
				@Override
				public boolean isAllow(LMM_EntityLittleMaid entityLittleMaid, EntityPlayer pentityplayer,
						ItemStack pitemstack) {
					return !entityLittleMaid.isRemainsContract() && allowStrikeItemIds.contains(pitemstack.itemID);
				}
			},
	};

	private static Set<Integer> getAllowItemIds(Item[] items) {
		Set<Integer> set = Sets.newHashSet();

		for (Item item : items) {
			set.add(item.itemID);
		}

		return set;
	}

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

	@Override
	public LMM_GuiInventory getOpenGuiInventory(EntityClientPlayerMP var1, LMM_EntityLittleMaid lentity) {
		return new LMM_HK_GuiInventory(var1, lentity);
	}

	@Override
	public void init() {
		// 登録モードの名称追加
		addLocalization(MODE_NAME);
	}

	@Override
	public boolean interact(EntityPlayer pentityplayer, ItemStack pitemstack) {
		if (isAllowOpenGui(pentityplayer, pitemstack)) {
			owner.getNavigator().clearPathEntity();
			owner.isJumping = false;
			if (!owner.worldObj.isRemote) {
				// server
				Container lcontainer = new LMM_ContainerInventory(pentityplayer.inventory, owner.maidInventory);
				ModLoader.serverOpenWindow((EntityPlayerMP) pentityplayer, lcontainer,
						mod_LMM_littleMaidMob.containerID,
						owner.entityId, 0, 0);
			}
			//    		        	ModLoader.openGUI(par1EntityPlayer, new LMM_GuiInventory(this, par1EntityPlayer.inventory, maidInventory));
			//    				serchedChest.clear();
			return true;
		}
		return false;
	}

	@Override
	public int priority() {
		return 3300;
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
			Village village = maid.worldObj.villageCollectionObj
					.findNearestVillage(MathHelper.floor_double(maid.posX),
							MathHelper.floor_double(maid.posY),
							MathHelper.floor_double(maid.posZ), 32);
			villageObj = village;
			if (!maid.isOpenInventory() && maid.isFreedom()
					&& villageObj != null) {
				int villageRadius = villageObj.getVillageRadius();
				ChunkCoordinates center = villageObj.getCenter();
				if (maid.getDistance(center.posX, center.posY, center.posZ) > villageRadius / 2) {
					teleportVillageCenter(maid, center, villageRadius / 2);
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

	private void addLocalization(String modeName) {
		addLocalization(modeName, true, true);
	}

	private void addLocalization(String modeName, boolean addFreedom, boolean addTracer) {
		String prefix = "littleMaidMob.mode";
		addLocalization(prefix, "", modeName);
		if (addFreedom) {
			addLocalization(prefix, "F-", modeName);
		}
		if (addTracer) {
			addLocalization(prefix, "T-", modeName);
		}
	}

	private void addLocalization(String keyPrefix, String prefix, String modeName) {
		String name = prefix + modeName;
		String key = new StringBuilder(prefix).append(".").append(name).toString();
		ModLoader.addLocalization(key, "name");
	}

	private <E> List<E> getInVillageEntity(Class<E> entitCls) {
		List<E> l = new ArrayList<E>();
		if (villageObj == null) {
			return l;
		}
		ChunkCoordinates center = villageObj.getCenter();
		int villageRadius = villageObj.getVillageRadius();
		@SuppressWarnings("unchecked")
		List<E> aabb = owner.worldObj.getEntitiesWithinAABB(entitCls, AxisAlignedBB
				.getAABBPool().getAABB(center.posX - villageRadius,
						center.posY - 4, center.posZ - villageRadius,
						center.posX + villageRadius, center.posY + 4,
						center.posZ + villageRadius));
		return aabb;
	}

	private boolean isAllowOpenGui(EntityPlayer pentityplayer,
			ItemStack pitemstack) {
		for (AllowInteractCondition condition : allowInteractConditions) {
			if (!condition.isAllow(owner, pentityplayer, pitemstack)) {

			}
		}
		return true;
	}

	private void teleportVillageCenter(LMM_EntityLittleMaid maid,
			ChunkCoordinates center, int radius) {
		double posX = center.posX;
		double posY = center.posY;
		double posZ = center.posZ;
		for (int l = 0; l < 8; l++) {
			int dY = l - 4;
			if (EntityCoordsSupport.checkSafeAreaAbsolute(maid, posX,
					posY + dY, posZ)) {
				maid.setPosition(posX, posY + dY, posZ);
				maid.
						setHomeArea(
								MathHelper.floor_double(posX),
								MathHelper.floor_double(posY + dY),
								MathHelper.floor_double(posZ), maid.dimension);
				return;
			}
		}
		for (int k = 1; k < radius; k++) {
			for (int element : dirTable) {
				int dX = element * k;
				for (int element2 : dirTable) {
					int dZ = element2 * k;
					for (int l = 0; l < 8; l++) {
						int dY = l - 4;
						if (EntityCoordsSupport.checkSafeAreaAbsolute(maid,
								posX + dX, posY + dY, posZ + dZ)) {
							maid.setPosition(posX + dX, posY + dY, posZ + dZ);
							maid.setHomeArea(
									MathHelper.floor_double(posX + dX),
									MathHelper.floor_double(posY + dY),
									MathHelper.floor_double(posZ + dZ), maid.dimension);
							return;
						}
					}
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
