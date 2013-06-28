package org.lo.d.minecraft.littlemaid.mode.strategy;

import java.util.List;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.src.LMM_EntityMode_HouseKeeper;

import com.google.common.collect.Lists;

public class VillageOutsideStrategy extends VillageStrategy.Impl {

	public VillageOutsideStrategy(LMM_EntityMode_HouseKeeper mode) {
		super(mode);
	}

	@Override
	public int getAdultVillagerCount() {
		return 0;
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
	public boolean shouldStrategy() {
		return false;
	}
}
