package ca.teamdman.sfm.client.gui.crafter;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.container.CrafterContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class CrafterScreen extends ContainerScreen<CrafterContainer> implements IHasContainer<CrafterContainer> {
	private static final ResourceLocation CRAFTER_TEXTURE = new ResourceLocation(SFM.MOD_ID, "textures/gui/container/crafter.png");

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	public CrafterScreen(CrafterContainer container, PlayerInventory inv, ITextComponent name) {
		super(container, inv, name);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		//noinspection ConstantConditions
		this.minecraft.getTextureManager().bindTexture(CRAFTER_TEXTURE);
		int i = this.guiLeft;
		int j = (this.height - this.ySize) / 2;
		this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
	}

	@Override
	public CrafterContainer getContainer() {
		return container;
	}
}
