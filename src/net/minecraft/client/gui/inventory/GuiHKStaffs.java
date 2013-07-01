package net.minecraft.client.gui.inventory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.src.LMM_EntityLittleMaid;
import net.minecraft.src.LMM_EntityModeBase;
import net.minecraft.src.LMM_EntityMode_HouseKeeper;
import net.minecraft.src.MMM_TextureBox;
import net.minecraft.src.MMM_TextureManager;
import net.minecraft.util.StatCollector;

import org.lo.d.commons.coords.Point2D;
import org.lo.d.commons.coords.Point3DDouble;
import org.lo.d.commons.gl.SafetyGL;
import org.lo.d.commons.gui.GridHelper;
import org.lo.d.commons.gui.GuiDrawHelper;
import org.lo.d.commons.gui.ScrollHelper;
import org.lo.d.commons.renderer.Point3DRenderSupport;
import org.lo.d.minecraft.littlemaid.MaidExIcon;
import org.lo.d.minecraft.littlemaid.mode.LMMModeExIconHandler;
import org.lo.d.minecraft.littlemaid.renderer.RenderMaidExIcon;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.EXTRescaleNormal;
import org.lwjgl.opengl.GL11;

public class GuiHKStaffs extends GuiContainer {

	private static class GridItemDrawer implements GridHelper.ItemDrawer<LMM_EntityLittleMaid> {

		private final GuiHKStaffs screen;
		private final Point2D drawPoint;
		private final GuiDrawHelper drawHelper;

		private GridItemDrawer(GuiHKStaffs screen, Point2D drawPoint, GuiDrawHelper drawHelper) {
			this.screen = screen;
			this.drawPoint = drawPoint;
			this.drawHelper = drawHelper;
		}

		@Override
		public void draw(int xOffset, int yOffset, LMM_EntityLittleMaid item) {
			drawMaidSlot(drawPoint.addX(xOffset * 104).addY(yOffset * 104), item);
		}

		private void drawMaid(final Point2D drawPoint, final LMM_EntityLittleMaid maid) {
			final int x = drawPoint.getX();
			final int y = drawPoint.getY();

			boolean nameVisible = maid.func_94062_bN();
			boolean cached = RenderManager.instance.field_96451_i == maid;
			if (cached) {
				RenderManager.instance.field_96451_i = null;
			}
			maid.func_94061_f(false);
			SafetyGL.safetyGLProcess(new SafetyGL.Processor() {
				@Override
				public void process(SafetyGL safetyGL) {
					safetyGL.enable(EXTRescaleNormal.GL_RESCALE_NORMAL_EXT);
					safetyGL.enable(GL11.GL_COLOR_MATERIAL);
					safetyGL.enable(GL11.GL_DEPTH_TEST);
					safetyGL.pushMatrix();
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					int xOffset = 50;
					int yOffset = 60;
					GL11.glTranslatef(x + xOffset, y + yOffset, 50F);
					float f1 = 30F;
					GL11.glScalef(-f1, f1, f1);
					GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
					float f8 = x + xOffset - drawHelper.getMousePoint().getX();
					float f9 = y + yOffset - drawHelper.getMousePoint().getY();
					GL11.glRotatef(135F, 0.0F, 1.0F, 0.0F);
					safetyGL.enableStandardItemLighting();
					GL11.glRotatef(-135F, 0.0F, 1.0F, 0.0F);
					GL11.glRotatef(-(float) Math.atan(f9 / 30F) * 15F, 1.0F, 0.0F, 0.0F);
					//					GL11.glRotatef(10F, 1.0F, 0.0F, 0.0F);
					GL11.glRotatef(-(float) Math.atan(f8 / 80F) * 360F, 0.0F, 1.0F, 0.0F);
					GL11.glTranslatef(0.0F, maid.yOffset, 0.0F);
					RenderManager.instance.playerViewY = 180F;
					RenderManager.instance.renderEntityWithPosYaw(maid, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
					RenderHelper.disableStandardItemLighting();
					//					GL11.glDisable(GL12.GL_RESCALE_NORMAL);
					OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
				}
			});
			if (cached) {
				RenderManager.instance.field_96451_i = maid;
			}
			maid.func_94061_f(nameVisible);
		}

		private void drawMaidSlot(final Point2D drawPoint, final LMM_EntityLittleMaid maid) {
			final RenderEngine renderEngine = drawHelper.getRenderEngine();
			final int x = drawPoint.getX();
			final int y = drawPoint.getY();
			SafetyGL.safetyGLProcess(new SafetyGL.Processor() {
				@Override
				public void process(SafetyGL safetyGL) {
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					safetyGL.disable(GL11.GL_COLOR_MATERIAL);
					safetyGL.disable(GL11.GL_BLEND);
					safetyGL.disable(GL11.GL_DEPTH_TEST);
					safetyGL.disable(GL11.GL_TEXTURE_2D);
					safetyGL.disableStandardItemLighting();
					screen.drawGradientRect(x, y, x + 100, y + 100, -2130706433, -2130706433);
				}
			});

			drawMaid(drawPoint, maid);

			SafetyGL.safetyGLProcess(new SafetyGL.Processor() {
				@Override
				public void process(SafetyGL safetyGL) {
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					safetyGL.disable(GL11.GL_COLOR_MATERIAL);
					safetyGL.disable(GL11.GL_BLEND);
					safetyGL.disable(GL11.GL_DEPTH_TEST);
					safetyGL.enable(GL11.GL_TEXTURE_2D);

					if (maid.isContract()) {
						// LP/AP
						renderEngine.bindTexture("/gui/icons.png");
						GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

						boolean flag1 = (maid.hurtResistantTime / 3) % 2 == 1;
						if (maid.hurtResistantTime < 10) {
							flag1 = false;
						}
						int i1 = maid.health;
						int j1 = maid.prevHealth;

						for (int j2 = 0; j2 < 10; j2++) {
							// LP
							int k5 = 0;
							if (flag1) {
								k5 = 1;
							}
							int i6 = x + 10 + j2 * 8;
							int k3 = 88 + y;
							screen.drawTexturedModalRect(i6, k3, 16 + k5 * 9, 0, 9, 9);
							if (flag1) {
								if (j2 * 2 + 1 < j1) {
									screen.drawTexturedModalRect(i6, k3, 70, 0, 9, 9);
								}
								if (j2 * 2 + 1 == j1) {
									screen.drawTexturedModalRect(i6, k3, 79, 0, 9, 9);
								}
							}
							if (j2 * 2 + 1 < i1) {
								screen.drawTexturedModalRect(i6, k3, 52, 0, 9, 9);
							}
							if (j2 * 2 + 1 == i1) {
								screen.drawTexturedModalRect(i6, k3, 61, 0, 9, 9);
							}
						}
					}

					FontRenderer fontRenderer = RenderManager.instance.getFontRenderer();

					if (maid.isContract()) {
						fontRenderer.drawStringWithShadow(StatCollector.translateToLocal("littleMaidMob.text.Health"),
								x + 10, y + 78, 0xffffff);
					}

					//名前
					if (maid.func_94056_bM()) {
						String name = maid.func_94057_bL();
						fontRenderer.drawStringWithShadow(name, x + 10, y + 8, 0xffffff);
					}

					//モード
					fontRenderer.drawStringWithShadow(
							StatCollector.translateToLocal("littleMaidMob.mode.".concat(maid.getMaidModeString())),
							x + 10, y + 66, 0xffffff);

				}
			});
			if (maid.isContract()) {
				SafetyGL.safetyGLProcess(new SafetyGL.Processor() {

					@Override
					public void process(SafetyGL safetyGL) {
						safetyGL.pushMatrix();
						safetyGL.disable(GL11.GL_COLOR_MATERIAL);
						safetyGL.disable(GL11.GL_BLEND);
						safetyGL.disable(GL11.GL_DEPTH_TEST);
						safetyGL.enable(GL11.GL_TEXTURE_2D);
						safetyGL.disableStandardItemLighting();
						Point3DRenderSupport.glTranslatef(new Point3DDouble((double) x + 90, (double) y + 30, 0D));
						GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
						GL11.glScalef(50, 50, 1);
						double offsetY = 0;
						for (LMM_EntityModeBase entityModeBase : maid.maidEntityModeList) {
							if (entityModeBase instanceof LMMModeExIconHandler) {
								LMMModeExIconHandler iconHandler = (LMMModeExIconHandler) entityModeBase;
								offsetY += doRenderMaidIcon(iconHandler, maid, new Point3DDouble(0D, offsetY, 0D), 0,
										1, RenderManager.instance);
							}
						}
					}

					private double doRenderMaidIcon(LMMModeExIconHandler iconHandler, LMM_EntityLittleMaid plittleMaid,
							Point3DDouble renderPos, float par8, final float par9, RenderManager renderManager) {
						double offsetY = 0;
						for (MaidExIcon icon : iconHandler.getIcons(plittleMaid.getMaidModeInt())) {
							RENDER_MAID_ICON.render(icon, renderPos.addY(offsetY), par8, par9, renderManager);
							offsetY += -4.5F / 16F;
						}
						return offsetY;
					}
				});
			}
		}
	}

	private static final RenderMaidExIcon RENDER_MAID_ICON = new RenderMaidExIcon();

	private static final Point2D POINT_SCROLL_BAR_RELATIVE = new Point2D(180, 25);

	private final GuiHKTab owner;

	public final LMM_EntityLittleMaid entitylittlemaid;
	public final LMM_EntityMode_HouseKeeper mode;

	private final GridHelper<LMM_EntityLittleMaid> gridHelper;

	private final ScrollHelper scrollHelper;

	private Comparator<LMM_EntityLittleMaid> currentMaidSorter;
	private Comparator<LMM_EntityLittleMaid> prevMaidSorter;

	public GuiHKStaffs(EntityPlayer player, LMM_EntityLittleMaid entitylittlemaid, final GuiHKTab owner,
			LMM_EntityMode_HouseKeeper mode) {
		super(new Container() {
			@Override
			public boolean canInteractWith(EntityPlayer entityplayer) {
				return true;
			}
		});
		this.owner = owner;
		this.entitylittlemaid = entitylittlemaid;
		this.mode = mode;
		ySize = 207;
		gridHelper = new GridHelper<>(2, 2);
		scrollHelper = new ScrollHelper(140, "/gui/scroll_bar.png", "/gui/scroll_bar_bg.png");

		owner.addMouseInputListner(new GuiHKTab.HandleInputListner() {

			@Override
			public void handleInput() {
				if (owner.getScreen() != GuiHKStaffs.this) {
					return;
				}
				int i = Mouse.getEventDWheel();

				float unit = gridHelper.getScrollUnit();
				if (i != 0) {
					if (i < 0) {
						unit = -unit;
					}

					scrollHelper.setCurrentScrollY(scrollHelper.getCurrentScrollRate() + unit);
				}
			}
		});

	}

	@Override
	public void actionPerformed(GuiButton par1GuiButton) {
		if (par1GuiButton instanceof GuiButtonHKStaffsSorter) {
			GuiButtonHKStaffsSorter buttonHKStaffsSorter = (GuiButtonHKStaffsSorter) par1GuiButton;
			setCurrentMaidSorter(buttonHKStaffsSorter.getMaidComparator());
		}
	}

	@Override
	public void drawDefaultBackground() {
		String s = ((MMM_TextureBox) entitylittlemaid.textureBox[0]).getTextureName(MMM_TextureManager.tx_gui);
		if (s == null) {
			s = "/gui/littlemaidinventory.png";
		}
		mc.renderEngine.bindTexture(s);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int lj = guiLeft;
		int lk = guiTop;
		drawTexturedModalRect(lj, lk, 0, 0, xSize, ySize);
		super.drawDefaultBackground();
	}

	@Override
	public void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		scrollHelper.drawScrollBarBackGround(i, j, f, mc.renderEngine);
		scrollHelper.drawScrollBar(i, j, f, mc.renderEngine);
		gridHelper.drawGrid(new GridItemDrawer(this, new Point2D(guiLeft - 30, guiTop), new GuiDrawHelper(new Point2D(
				i, j), f, mc.renderEngine)));

	}

	@Override
	public void drawScreen(final int mx, final int my, float f) {
		scrollHelper.updateOnDrawScreen(mx, my);
		gridHelper.clearItems();

		List<LMM_EntityLittleMaid> maids = mode.strategyHelper.getCurrentStrategy().getMyMaids();
		maids.add(entitylittlemaid);

		Collections.sort(maids, new Comparator<LMM_EntityLittleMaid>() {
			@Override
			public int compare(LMM_EntityLittleMaid o1, LMM_EntityLittleMaid o2) {
				int compare = currentMaidSorter.compare(o1, o2);
				if (compare == 0) {
					compare = prevMaidSorter.compare(o1, o2);
				}
				if (compare == 0) {
					if (o1.isContract()) {
						if (!o2.isContract()) {
							compare = -1;
						}
					} else {
						if (o2.isContract()) {
							compare = 1;
						}
					}
				}
				return compare;
			}
		});

		gridHelper.addItems(maids);
		gridHelper.scrollTo(scrollHelper.getCurrentScrollRate());
		super.drawScreen(mx, my, f);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();
		if (mc.currentScreen == this && !owner.isInitializing) {
			mc.displayGuiScreen(owner);
		}
		scrollHelper.updateScrollRect(POINT_SCROLL_BAR_RELATIVE.addX(guiLeft).addY(guiTop));

		currentMaidSorter = new Comparator<LMM_EntityLittleMaid>() {
			@Override
			public int compare(LMM_EntityLittleMaid o1, LMM_EntityLittleMaid o2) {
				if (o1.func_94056_bM()) {
					if (o2.func_94056_bM()) {
						return o1.func_94057_bL().compareTo(o2.func_94057_bL());
					} else {
						return -1;
					}
				} else {
					if (o2.func_94056_bM()) {
						return 1;
					}
				}
				return 0;
			}
		};
		prevMaidSorter = new Comparator<LMM_EntityLittleMaid>() {
			@Override
			public int compare(LMM_EntityLittleMaid o1, LMM_EntityLittleMaid o2) {
				if (o1.isContract()) {
					if (o2.isContract()) {
						return o1.getMaidModeString(o1.getMaidModeInt()).compareTo(
								o2.getMaidModeString(o2.getMaidModeInt()));
					} else {
						return -1;
					}
				} else {
					if (o2.isContract()) {
						return 1;
					}
				}
				return 0;
			}
		};
		buttonList.add(new GuiButtonHKStaffsSorter(0, guiLeft - 50, guiTop + 4, "N", currentMaidSorter));
		buttonList.add(new GuiButtonHKStaffsSorter(0, guiLeft - 50, guiTop + 24, "J", prevMaidSorter));
		buttonList.add(new GuiButtonHKStaffsSorter(0, guiLeft - 50, guiTop + 44, "M",
				new Comparator<LMM_EntityLittleMaid>() {
					@Override
					public int compare(LMM_EntityLittleMaid o1, LMM_EntityLittleMaid o2) {
						return getValue(o2) - getValue(o1);
					}

					private int getValue(LMM_EntityLittleMaid maid) {
						if (!maid.isContract()) {
							return 0;
						}
						if (maid.isMaidWait()) {
							return 1;
						}
						if (maid.isTracer()) {
							return 2;
						}
						if (maid.isFreedom()) {
							return 3;
						}
						return 4;
					}
				}));
		buttonList.add(new GuiButtonHKStaffsSorter(0, guiLeft - 50, guiTop + 64, "D",
				new Comparator<LMM_EntityLittleMaid>() {
					@Override
					public int compare(LMM_EntityLittleMaid o1, LMM_EntityLittleMaid o2) {
						double dist1 = o1.getDistanceSqToEntity(mc.thePlayer);
						double dist2 = o2.getDistanceSqToEntity(mc.thePlayer);
						double d = dist1 - dist2;
						return (int) d;
					}
				}));
	}

	private void setCurrentMaidSorter(Comparator<LMM_EntityLittleMaid> sorter) {
		prevMaidSorter = currentMaidSorter;
		currentMaidSorter = sorter;
	}

}
