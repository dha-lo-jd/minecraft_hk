package org.lo.d.minecraft.littlemaid.mode.strategy;

import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.src.LMM_EntityMode_HouseKeeper;
import net.minecraft.util.AxisAlignedBB;

import org.lo.d.minecraft.littlemaid.MaidExIcon;

import com.google.common.collect.Lists;

public interface VillageStrategy extends Strategy {
	public abstract class Impl extends DependencyStrategy.DefaultImpl implements VillageStrategy {
		protected final LMM_EntityMode_HouseKeeper mode;

		public Impl(LMM_EntityMode_HouseKeeper mode) {
			this.mode = mode;
		}

		@Override
		public List<MaidExIcon> getIcons() {
			return Lists.newArrayList();
		}

		@Override
		public AxisAlignedBB getMyArea() {
			return mode.owner.boundingBox.expand(16, 4, 16);
		}
	}

	public int getAdultVillagerCount();

	public List<MaidExIcon> getIcons();

	public IEntitySelector getMaidSelector();

	public AxisAlignedBB getMyArea();

	public List<EntityVillager> getMyVillagers();

	public List<String> getTeachingInfo();

	public int getVillagerCount();
}
