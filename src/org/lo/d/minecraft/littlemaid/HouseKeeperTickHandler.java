package org.lo.d.minecraft.littlemaid;

import java.util.EnumSet;
import java.util.Set;

import net.minecraft.src.LMM_EntityLittleMaid;

import com.google.common.collect.Sets;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class HouseKeeperTickHandler implements ITickHandler {

	public interface MaidCollector extends PriorityComparable {
		Set<LMM_EntityLittleMaid> collect();
	}

	public interface MaidsWorker extends PriorityComparable {
		Set<LMM_EntityLittleMaid> work(Set<LMM_EntityLittleMaid> maids);
	}

	public interface PriorityComparable extends Comparable<PriorityComparable> {
		public Integer getPriority();
	}

	public static abstract class PriorityComparableImpl implements PriorityComparable {
		private final int priority;

		public PriorityComparableImpl(int priority) {
			this.priority = priority;
		}

		@Override
		public int compareTo(PriorityComparable o) {
			return o.getPriority().compareTo(getPriority());
		}

		@Override
		public Integer getPriority() {
			return priority;
		}

	}

	private Set<MaidCollector> collectors = Sets.newTreeSet();
	private Set<MaidsWorker> workers = Sets.newTreeSet();

	public boolean addCollector(MaidCollector collector) {
		return collectors.add(collector);
	}

	public boolean addWorker(MaidsWorker worker) {
		return workers.add(worker);
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if (type.equals(EnumSet.of(TickType.SERVER, TickType.WORLD))) {
			onTick();
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.SERVER, TickType.WORLD);
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
	}

	private void onTick() {
		Set<LMM_EntityLittleMaid> maids = Sets.newHashSet();
		for (MaidCollector collector : collectors) {
			maids.addAll(collector.collect());
		}
		for (MaidsWorker worker : workers) {
			maids.removeAll(worker.work(maids));
		}
	}

}
