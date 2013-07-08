package net.minecraft.client.gui.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.src.LMM_EntityLittleMaid;
import net.minecraft.src.LMM_EntityMode_HouseKeeper;

import org.lo.d.commons.gui.ContainerTab;
import org.lo.d.minecraft.littlemaid.gui.GuiMaidExContainer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiHKTab extends GuiMaidExContainer {
	public GuiHKTab(EntityPlayer player, LMM_EntityLittleMaid maid, LMM_EntityMode_HouseKeeper mode) {
		super(new ContainerTab(), player, maid);
		GuiHKInventory lmm_GuiInventory = new GuiHKInventory(player, maid, this, mode);
		screen = lmm_GuiInventory;
		add(new MaidExTabEntry(this, lmm_GuiInventory, entitylittlemaid, "/gui/tab_maid.png"));
		add(new MaidExTabEntry(this, new GuiHKStaffs(player, maid, this, mode), entitylittlemaid, "/gui/icon_maids.png"));
	}
}
