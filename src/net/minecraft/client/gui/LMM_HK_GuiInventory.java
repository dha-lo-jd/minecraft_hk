package net.minecraft.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.src.LMM_EntityLittleMaid;
import net.minecraft.src.LMM_GuiInventory;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

public class LMM_HK_GuiInventory extends LMM_GuiInventory {
	public static final String BOLD = "\247l";

	public static final String RED = "\2474";
	public static final String GREEN = "\247a";

	public LMM_HK_GuiInventory(EntityPlayer pPlayer, LMM_EntityLittleMaid elmaid) {
		super(pPlayer, elmaid);
	}

	@Override
	public void drawScreen(int mx, int my, float f) {
		super.drawScreen(mx, my, f);
		int i = guiLeft;
		int j = guiTop;

		if (entitylittlemaid.maidInventory.currentItem < 0) {
			return;
		}
		GL11.glPushMatrix();
		GL11.glTranslatef(i, j, 0.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		Slot slot1 = (Slot) inventorySlots.inventorySlots
				.get(entitylittlemaid.maidInventory.currentItem);
		if (isMouseOverSlot(slot1, mx, my)) {
			ItemStack itemstack = slot1.getStack();
			if (itemstack == null
					|| itemstack.itemID != Item.sign.itemID) {
				endDrawScreen();
				return;
			}
			@SuppressWarnings("unchecked")
			List<String> list = itemstack.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);
			list.addAll(getTeachingInfo());

			if (list.size() > 0) {
				int l1 = 0;

				for (int i2 = 0; i2 < list.size(); i2++) {
					int k2 = fontRenderer.getStringWidth(list.get(i2));

					if (k2 > l1) {
						l1 = k2;
					}
				}

				int j2 = (mx - i) + 12;
				int l2 = my - j - 12;
				int i3 = l1;
				int j3 = 8;

				if (list.size() > 1) {
					j3 += 2 + (list.size() - 1) * 10;
				}

				zLevel = 310F;
				itemRenderer.zLevel = 310F;
				int k3 = 0xf0100010;
				drawGradientRect(j2 - 3, l2 - 4, j2 + i3 + 3, l2 - 3, k3, k3);
				drawGradientRect(j2 - 3, l2 + j3 + 3, j2 + i3 + 3, l2 + j3 + 4,
						k3, k3);
				drawGradientRect(j2 - 3, l2 - 3, j2 + i3 + 3, l2 + j3 + 3, k3,
						k3);
				drawGradientRect(j2 - 4, l2 - 3, j2 - 3, l2 + j3 + 3, k3, k3);
				drawGradientRect(j2 + i3 + 3, l2 - 3, j2 + i3 + 4, l2 + j3 + 3,
						k3, k3);
				int l3 = 0x505000ff;
				int i4 = (l3 & 0xfefefe) >> 1 | l3 & 0xff000000;
				drawGradientRect(j2 - 3, (l2 - 3) + 1, (j2 - 3) + 1,
						(l2 + j3 + 3) - 1, l3, i4);
				drawGradientRect(j2 + i3 + 2, (l2 - 3) + 1, j2 + i3 + 3, (l2
						+ j3 + 3) - 1, l3, i4);
				drawGradientRect(j2 - 3, l2 - 3, j2 + i3 + 3, (l2 - 3) + 1, l3,
						l3);
				drawGradientRect(j2 - 3, l2 + j3 + 2, j2 + i3 + 3, l2 + j3 + 3,
						i4, i4);

				for (int j4 = 0; j4 < list.size(); j4++) {
					String s = list.get(j4);

					if (j4 == 0) {
						if (itemstack.isItemEnchanted()) {
							s = (new StringBuilder()).append("\247")
									.append(Integer.toHexString(11)).append(s)
									.toString();
						} else {
							s = (new StringBuilder()).append("\247")
									.append(Integer.toHexString(15)).append(s)
									.toString();
						}
					} else {
						s = (new StringBuilder()).append("\2477").append(s)
								.toString();
					}

					fontRenderer.drawStringWithShadow(s, j2, l2, -1);

					if (j4 == 0) {
						l2 += 2;
					}

					l2 += 10;
				}

				zLevel = 0.0F;
				itemRenderer.zLevel = 0.0F;
			}
		}
		endDrawScreen();
	}

	public List<String> getTeachingInfo() {
		List<String> list = new ArrayList<String>();

		int adultVillagerCount = 4;
		//		for (EntityVillager villager : villagerList) {
		//			if (villager.getGrowingAge() >= 0) {
		//				adultVillagerCount++;
		//			}
		//		}

		int maidCount = 1;
		//		int maidCount = maid.maidList.size();
		boolean flag = canTeach(adultVillagerCount, maidCount);

		double rate = 25 / 100D;

		list.add("Maid: " + BOLD + maidCount);
		List<String> villagerList = Lists.newArrayList("", "", "");
		list.add("Villager: " + BOLD + villagerList.size());
		list.add("Child: " + BOLD + (villagerList.size() - adultVillagerCount));
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

	private void endDrawScreen() {
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	/**
	 * Returns if the passed mouse position is over the specified slot.
	 */
	private boolean isMouseOverSlot(Slot par1Slot, int par2, int par3) {
		int i = guiLeft;
		int j = guiTop;
		par2 -= i;
		par3 -= j;
		return par2 >= par1Slot.xDisplayPosition - 1
				&& par2 < par1Slot.xDisplayPosition + 16 + 1
				&& par3 >= par1Slot.yDisplayPosition - 1
				&& par3 < par1Slot.yDisplayPosition + 16 + 1;
	}

	protected boolean canTeach(int adultVillagerCount, int maidCount) {
		boolean flag = false;
		if (adultVillagerCount > 0) {
			flag = (int) getMaidRate(adultVillagerCount) > maidCount;
		}
		return flag;
	}

	protected double getMaidRate(int adultVillagerCount) {
		double rate = 25 / 100D;
		double maidRatio = adultVillagerCount * rate;
		return maidRatio;
	}

}
