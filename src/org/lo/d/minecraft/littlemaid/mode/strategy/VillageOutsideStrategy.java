package org.lo.d.minecraft.littlemaid.mode.strategy;

import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.src.LMM_EntityLittleMaid;
import net.minecraft.src.LMM_EntityMode_HouseKeeper;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

import com.google.common.collect.Lists;

public class VillageOutsideStrategy extends VillageStrategy.Impl {

	public VillageOutsideStrategy(LMM_EntityMode_HouseKeeper mode) {
		super(mode);
	}

	@Override
	public void doVillageGuard(EnderTeleportEvent event) {
	}

	@Override
	public void doVillageGuard(EntityJoinWorldEvent event) {
	}

	@Override
	public int getAdultVillagerCount() {
		return 0;
	}

	@Override
	public IEntitySelector getMaidSelector() {
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
				return maid.isContract() && mode.owner.mstatMasterEntity == maid.mstatMasterEntity;
			}
		};
	}

	@Override
	public List<EntityVillager> getMyVillagers() {
		return Lists.newArrayList();
	}

	@Override
	public List<String> getTeachingInfo() {
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

	@Override
	public boolean shouldVillageGuard(EnderTeleportEvent event) {
		return false;
	}

	@Override
	public boolean shouldVillageGuard(EntityJoinWorldEvent event) {
		return false;
	}
}
