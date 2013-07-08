package org.lo.d.minecraft.littlemaid.mode.strategy;

import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.src.LMM_EntityLittleMaid;
import net.minecraft.src.LMM_EntityMode_HouseKeeper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

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
	public void doVillageGuard(EnderTeleportEvent event) {
		helper.getCurrentStrategy().doVillageGuard(event);
	}

	@Override
	public void doVillageGuard(EntityJoinWorldEvent event) {
		helper.getCurrentStrategy().doVillageGuard(event);
	}

	@Override
	public List<MaidExIcon> getIcons() {
		List<MaidExIcon> list = Lists.newArrayList(super.getIcons());
		list.addAll(helper.getCurrentStrategy().getIcons());
		return list;
	}

	@Override
	public List<String> getTeachingInfo() {
		return helper.getCurrentStrategy().getTeachingInfo();
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

	@Override
	public boolean shouldVillageGuard(EnderTeleportEvent event) {
		return helper.getCurrentStrategy().shouldVillageGuard(event);
	}

	@Override
	public boolean shouldVillageGuard(EntityJoinWorldEvent event) {
		return helper.getCurrentStrategy().shouldVillageGuard(event);
	}

	@Override
	protected IEntitySelector getMaidSelector() {
		return helper.getCurrentStrategy().getMaidSelector();
	}

	@Override
	protected AxisAlignedBB getMyArea() {
		return helper.getCurrentStrategy().getMyArea();
	}

}
