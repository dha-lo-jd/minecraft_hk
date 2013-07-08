package org.lo.d.minecraft.littlemaid.mode.strategy;

import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.src.LMM_EntityLittleMaid;
import net.minecraft.src.LMM_EntityMode_HouseKeeper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

import com.google.common.collect.Lists;

public class HKEscorterStrategy extends HKMaidStateStrategy.Impl {

	public HKEscorterStrategy(LMM_EntityMode_HouseKeeper mode) {
		super(mode);
	}

	@Override
	public void doVillageGuard(EnderTeleportEvent event) {
	}

	@Override
	public void doVillageGuard(EntityJoinWorldEvent event) {
	}

	@Override
	public List<String> getTeachingInfo() {
		return Lists.newArrayList();
	}

	@Override
	public boolean shouldStrategy() {
		LMM_EntityLittleMaid maid = mode.owner;
		return !maid.isFreedom() && !maid.isMaidWait();
	}

	@Override
	public boolean shouldVillageGuard(EnderTeleportEvent event) {
		return false;
	}

	@Override
	public boolean shouldVillageGuard(EntityJoinWorldEvent event) {
		return false;
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
				return maid.isContract() && mode.owner.mstatMasterEntity == maid.mstatMasterEntity;
			}
		};
	}

	@Override
	protected AxisAlignedBB getMyArea() {
		return mode.owner.boundingBox.expand(16, 4, 16);
	}

}
