package net.minecraft.client.gui.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.src.LMM_EntityLittleMaid;
import net.minecraft.src.LMM_EntityMode_HouseKeeper;
import net.minecraft.util.ResourceLocation;

import org.lo.d.commons.gui.ContainerTab;
import org.lo.d.minecraft.littlemaid.gui.GuiMaidExContainer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiHKTab extends GuiMaidExContainer {
	public static final ResourceLocation TAB_MAIDS = new ResourceLocation("house_keeper", "textures/gui/icon_maids.png");

	public GuiHKTab(EntityPlayer player, LMM_EntityLittleMaid maid, LMM_EntityMode_HouseKeeper mode) {
		super(new ContainerTab(), player, maid);
		GuiHKInventory lmm_GuiInventory = new GuiHKInventory(player, maid, this, mode);
		screen = lmm_GuiInventory;
		add(new MaidExTabEntry(this, lmm_GuiInventory, entitylittlemaid, TAB_MAID));
		add(new MaidExTabEntry(this, new GuiHKStaffs(player, maid, this, mode), entitylittlemaid, TAB_MAIDS));
	}
}
