package net.minecraft.src;

import net.minecraft.entity.ai.EntityAITasks;

/**
 * @author dha_lo_jd
 * メイド長の部下ロジック…の予定
 *
 */
public class LMM_EntityMode_HKMaidVisitor extends LMM_EntityModeBase {
	public LMM_EntityMode_HKMaidVisitor(LMM_EntityLittleMaid pEntity) {
		super(pEntity);
	}

	@Override
	public void addEntityMode(EntityAITasks pDefaultMove, EntityAITasks pDefaultTargeting) {
	}

	@Override
	public void onUpdate(int pMode) {
	}

	@Override
	public int priority() {
		return 0;
	}

}
