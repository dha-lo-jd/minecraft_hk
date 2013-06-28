package org.lo.d.minecraft.littlemaid.mode.strategy;

import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.src.LMM_EntityLittleMaid;
import net.minecraft.src.LMM_EntityMode_HouseKeeper;
import net.minecraft.util.AxisAlignedBB;

import org.lo.d.minecraft.littlemaid.MaidExIcon;

import com.google.common.collect.Lists;

public interface HKMaidStateStrategy extends DependencyStrategy {//StateなのにStrategyとかあたまおかしさある
	public abstract class Impl extends DependencyStrategy.DefaultImpl implements HKMaidStateStrategy {
		private static class HKMaidsIcon extends MaidExIcon {
			private final Impl strategy;

			public HKMaidsIcon(Impl strategy) {
				this.strategy = strategy;
			}

			@Override
			public double getOffsetX() {
				return 4F / 16F;
			}

			@Override
			public String getText() {
				return String.valueOf(strategy.getMaidsCount());
			}

			@Override
			public int getTextColor() {
				if (strategy.getMaidsCount() > 0) {
					return 0xffffff;
				}
				return 0xff0000;
			}

			@Override
			public String getTexture() {
				return "/gui/icon_maids.png";
			}
		}

		protected final LMM_EntityMode_HouseKeeper mode;

		private HKMaidsIcon maidsIcon;

		public Impl(LMM_EntityMode_HouseKeeper mode) {
			this.mode = mode;
			maidsIcon = new HKMaidsIcon(this);
		}

		@Override
		public List<MaidExIcon> getIcons() {
			List<MaidExIcon> list = Lists.newArrayList();
			if (isVisibleMaidsIcon()) {
				list.add(maidsIcon);
			}
			return list;
		}

		@Override
		public int getMaidsCount() {
			return getMyMaids().size();
		}

		@Override
		public List<LMM_EntityLittleMaid> getMyMaids() {
			final LMM_EntityLittleMaid maid = mode.owner;
			List<?> l = maid.worldObj.getEntitiesWithinAABBExcludingEntity(maid, getMyArea(), getMaidSelector());
			List<LMM_EntityLittleMaid> list = Lists.newArrayList();
			for (Object o : l) {
				list.add((LMM_EntityLittleMaid) o);
			}
			return list;
		}

		protected abstract IEntitySelector getMaidSelector();

		protected abstract AxisAlignedBB getMyArea();

		protected boolean isVisibleMaidsIcon() {
			return getMyMaids().size() > 0;
		}
	}

	public List<MaidExIcon> getIcons();

	public int getMaidsCount();

	public List<LMM_EntityLittleMaid> getMyMaids();

	public List<String> getTeachingInfo();
}
