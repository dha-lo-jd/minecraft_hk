package net.minecraft.client.gui.inventory;

import java.util.Comparator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.src.LMM_EntityLittleMaid;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonHKStaffsSorter extends GuiButton {

	private final Comparator<LMM_EntityLittleMaid> maidComparator;

	public GuiButtonHKStaffsSorter(int id, int x, int y, String label, Comparator<LMM_EntityLittleMaid> maidComparator) {
		super(id, x, y, 16, 20, label);
		this.maidComparator = maidComparator;
	}

	/**
	 * Draws this button to the screen.
	 */
	@Override
	public void drawButton(Minecraft par1Minecraft, int x, int y) {
		super.drawButton(par1Minecraft, x, y);
	}

	public Comparator<LMM_EntityLittleMaid> getMaidComparator() {
		return maidComparator;
	}
}