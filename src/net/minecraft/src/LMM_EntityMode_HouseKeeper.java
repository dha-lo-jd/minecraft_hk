package net.minecraft.src;

import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.src.LMM_EntityMode_AcceptBookCommand.ModeAlias;
import net.minecraft.village.Village;

import org.lo.d.minecraft.littlemaid.LMMExtension;
import org.lo.d.minecraft.littlemaid.LittleMaidModeConfiguration;
import org.lo.d.minecraft.littlemaid.MaidExIcon;
import org.lo.d.minecraft.littlemaid.gui.GuiHKTab;
import org.lo.d.minecraft.littlemaid.mode.LMM_EntityModeBaseEx;
import org.lo.d.minecraft.littlemaid.mode.strategy.HKEscorterStrategy;
import org.lo.d.minecraft.littlemaid.mode.strategy.HKFreedomStrategy;
import org.lo.d.minecraft.littlemaid.mode.strategy.HKMaidStateStrategy;
import org.lo.d.minecraft.littlemaid.mode.strategy.StrategyUserHelper;
import org.lo.d.minecraft.littlemaid.mode.strategy.VillageInsideStrategy;
import org.lo.d.minecraft.littlemaid.mode.strategy.VillageOutsideStrategy;
import org.lo.d.minecraft.littlemaid.mode.strategy.VillageStrategy;

import com.google.common.collect.Lists;

@LittleMaidModeConfiguration
public class LMM_EntityMode_HouseKeeper extends LMM_EntityModeBaseEx {
	public static final String MODE_NAME = "HouseKeeper";

	@LittleMaidModeConfiguration.ResolveModeId(modeName = MODE_NAME)
	public static int MODE_ID = 0x0201;

	private Village villageObj;

	public final StrategyUserHelper<HKMaidStateStrategy> strategyHelper;
	public final StrategyUserHelper<VillageStrategy> villageStrategyHelper;

	public LMM_EntityMode_HouseKeeper(LMM_EntityLittleMaid pEntity) {
		super(pEntity);
		villageStrategyHelper = new StrategyUserHelper<>(new VillageOutsideStrategy(this));
		villageStrategyHelper.add(new VillageInsideStrategy(this));
		strategyHelper = new StrategyUserHelper<>(new HKEscorterStrategy(this));
		strategyHelper.add(new HKFreedomStrategy(this, villageStrategyHelper));
	}

	@Override
	public void addEntityMode(EntityAITasks pDefaultMove, EntityAITasks pDefaultTargeting) {
		EntityAITasks[] ltasks = new EntityAITasks[2];
		ltasks[0] = pDefaultMove;
		ltasks[1] = pDefaultTargeting;

		owner.addMaidMode(ltasks, MODE_NAME, MODE_ID);

	}

	@Override
	public Container getContainerInventory(final int guiId, EntityPlayer player, LMM_EntityLittleMaid maid, int maidMode) {
		return super.getContainerInventory(guiId, player, maid, maidMode);
	}

	@Override
	public List<MaidExIcon> getIcons(int maidMode) {
		if (maidMode == MODE_ID) {
			return strategyHelper.getCurrentStrategy().getIcons();
		}
		return Lists.newArrayList();
	}

	@Override
	public GuiContainer getOpenGuiInventory(final int guiId, EntityPlayer var1, LMM_EntityLittleMaid maid, int maidMode) {
		if (maidMode == MODE_ID) {
			if (guiId == LMMExtension.guiId) {
				return new GuiHKTab(var1, maid, this);
			}
		}
		return super.getOpenGuiInventory(guiId, var1, maid, maidMode);
	}

	public List<String> getTeachingInfo() {
		return strategyHelper.getCurrentStrategy().getTeachingInfo();
	}

	@Override
	public void init() {
		// 登録モードの名称追加
		addLocalization(MODE_NAME);
		LMM_EntityMode_AcceptBookCommand.add(new ModeAlias(MODE_ID, MODE_NAME, "Hk"));
	}

	@Override
	public void onUpdate(int pMode) {
		if (pMode == MODE_ID) {
			strategyHelper.updateCurrentStrategy();
			villageStrategyHelper.updateCurrentStrategy();

			strategyHelper.getCurrentStrategy().onUpdateStrategy();
		}
	}

	@Override
	public int priority() {
		return 7000;
	}

	@Override
	public boolean setMode(int pMode) {
		if (pMode == MODE_ID) {
			return true;
		}
		return false;
	}

	@Override
	public void updateAITick(int pMode) {
		if (pMode == MODE_ID) {
			//			LMM_EntityLittleMaid maid = owner;
			//			Village village = maid.worldObj.villageCollectionObj.findNearestVillage(MathHelper.floor_double(maid.posX),
			//					MathHelper.floor_double(maid.posY), MathHelper.floor_double(maid.posZ), 32);
			//			villageObj = village;
			//			if (!maid.isOpenInventory() && maid.isFreedom() && villageObj != null) {
			//				int villageRadius = villageObj.getVillageRadius();
			//				ChunkCoordinates center = villageObj.getCenter();
			//				if (maid.getDistance(center.posX, center.posY, center.posZ) > villageRadius / 2) {
			//					teleportVillageCenter(maid, new Point3D(center), villageRadius / 2);
			//				}
			//			}
			//
			//			if (villageObj != null) {
			//				villageUpdate();
			//				List<EntityCreeper> entityCreepers = getInVillageEntity(EntityCreeper.class);
			//				for (EntityCreeper entityCreeper : entityCreepers) {
			//					villageObj.addOrRenewAgressor(entityCreeper);
			//				}
			//				villageIconUpdate();
			//			}
		}
	}

}
