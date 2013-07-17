package org.lo.d.minecraft.littlemaid.mode.strategy;

import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.src.LMM_EntityLittleMaid;
import net.minecraft.src.LMM_EntityMode_HouseKeeper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

import org.lo.d.minecraft.littlemaid.MaidExIcon;

import com.google.common.collect.Lists;

public interface HKMaidStateStrategy extends DependencyStrategy {//StateなのにStrategyとかあたまおかしさある
	public abstract class Impl extends DependencyStrategy.DefaultImpl implements HKMaidStateStrategy {
		private static class HKMaidsIcon extends MaidExIcon {
			private static final ResourceLocation ICON_MAIDS = new ResourceLocation("house_keeper",
					"textures/gui/icon_maids.png");
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
			public ResourceLocation getTexture() {
				return ICON_MAIDS;
			}
		}

		protected final LMM_EntityMode_HouseKeeper mode;

		private HKMaidsIcon maidsIcon;

		long lastCachedTick = -1;

		List<LMM_EntityLittleMaid> tickCachedMaids;

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
			long tick = maid.worldObj.getWorldTime();
			if (tick == lastCachedTick) {
				return Lists.newArrayList(tickCachedMaids);
			}
			List<?> l = maid.worldObj.getEntitiesWithinAABBExcludingEntity(maid, getMyArea(), getMaidSelector());
			List<LMM_EntityLittleMaid> list = Lists.newArrayList();
			for (Object o : l) {
				list.add((LMM_EntityLittleMaid) o);
			}
			lastCachedTick = tick;
			tickCachedMaids = list;
			return Lists.newArrayList(tickCachedMaids);
		}

		protected abstract IEntitySelector getMaidSelector();

		protected abstract AxisAlignedBB getMyArea();

		protected boolean isVisibleMaidsIcon() {
			return getMyMaids().size() > 0;
		}
	}

	public void doVillageGuard(EnderTeleportEvent event);

	public void doVillageGuard(EntityJoinWorldEvent event);

	public List<MaidExIcon> getIcons();

	public int getMaidsCount();

	public List<LMM_EntityLittleMaid> getMyMaids();

	public List<String> getTeachingInfo();

	public boolean shouldVillageGuard(EnderTeleportEvent event);

	public boolean shouldVillageGuard(EntityJoinWorldEvent event);
}
