package org.lo.d.minecraft.littlemaid.mode.strategy;

import static org.lo.d.commons.gui.FontRendererConstants.Color.*;
import static org.lo.d.commons.gui.FontRendererConstants.Style.*;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.src.LMM_EntityLittleMaid;
import net.minecraft.src.LMM_EntityMode_HouseKeeper;
import net.minecraft.util.AxisAlignedBB;

import org.lo.d.minecraft.littlemaid.HouseKeeper;
import org.lo.d.minecraft.littlemaid.MaidExIcon;

import com.google.common.collect.Lists;

public class HKFreedomStrategy extends HKMaidStateStrategy.Impl {

	private StrategyUserHelper<VillageStrategy> helper;

	public HKFreedomStrategy(LMM_EntityMode_HouseKeeper mode, StrategyUserHelper<VillageStrategy> helper) {
		super(mode);
		this.helper = helper;
		addDependencyStrategy(helper);
	}

	@Override
	public List<MaidExIcon> getIcons() {
		List<MaidExIcon> list = Lists.newArrayList(super.getIcons());
		list.addAll(helper.getCurrentStrategy().getIcons());
		return list;
	}

	@Override
	public List<String> getTeachingInfo() {
		List<String> list = new ArrayList<String>();

		int villagerCount = helper.getCurrentStrategy().getVillagerCount();
		if (villagerCount == 0) {
			return list;
		}

		int adultVillagerCount = helper.getCurrentStrategy().getAdultVillagerCount();

		int maidCount = getMaidsCount();
		boolean flag = canTeach(adultVillagerCount, maidCount);

		double rate = HouseKeeper.teachRate / 100D;

		list.add("Maid: " + BOLD + maidCount);
		list.add("Villager: " + BOLD + villagerCount);
		list.add("Child: " + BOLD + (adultVillagerCount - adultVillagerCount));
		if (!flag) {
			double n = maidCount + 1 - getMaidRate(adultVillagerCount);
			n = n / rate;
			list.add(RED + "NeedToTeach: " + BOLD + (int) n);
		} else {
			int n = (int) getMaidRate(adultVillagerCount) - maidCount;
			list.add(GREEN + "CanTeach: " + BOLD + n);
		}

		return list;
	}

	@Override
	public void onUpdateStrategy() {
		super.onUpdateStrategy();
		helper.getCurrentStrategy().onUpdateStrategy();
	}

	@Override
	public boolean shouldStrategy() {
		LMM_EntityLittleMaid maid = mode.owner;
		return maid.isFreedom() && !maid.isMaidWait();
	}

	protected boolean canTeach(int adultVillagerCount, int maidCount) {
		boolean flag = false;
		if (adultVillagerCount > 0) {
			flag = (int) getMaidRate(adultVillagerCount) > maidCount;
		}
		return flag;
	}

	protected double getMaidRate(int adultVillagerCount) {
		double rate = HouseKeeper.teachRate / 100D;
		double maidRatio = adultVillagerCount * rate;
		return maidRatio;
	}

	@Override
	protected IEntitySelector getMaidSelector() {
		return new IEntitySelector() {
			@Override
			public boolean isEntityApplicable(Entity entity) {
				if (entity == mode.owner) {
					return false;
				}
				if (!(entity instanceof LMM_EntityLittleMaid)) {
					return false;
				}
				LMM_EntityLittleMaid maid = (LMM_EntityLittleMaid) entity;
				return maid.isMaidContract();
			}
		};
	}

	@Override
	protected AxisAlignedBB getMyArea() {
		return helper.getCurrentStrategy().getMyArea();
	}

}
