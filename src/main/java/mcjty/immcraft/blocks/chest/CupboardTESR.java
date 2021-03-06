package mcjty.immcraft.blocks.chest;


import mcjty.immcraft.ImmersiveCraft;
import mcjty.immcraft.blocks.ModBlocks;
import mcjty.immcraft.rendering.HandleTESR;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class CupboardTESR extends HandleTESR<CupboardTE> {

    private IModel lidModel;
    private IBakedModel bakedLidModel;

    public CupboardTESR() {
        super(ModBlocks.cupboardBlock);

        try {
            lidModel = ModelLoaderRegistry.getModel(new ResourceLocation(ImmersiveCraft.MODID, "block/cupboardLid.obj"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        textOffset = new Vec3(0, 0, -.2);
    }

    private IBakedModel getBakedLidModel() {
        if (bakedLidModel == null) {
            bakedLidModel = lidModel.bake(TRSRTransformation.identity(), DefaultVertexFormats.ITEM, location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
        }
        return bakedLidModel;
    }

    @Override
    protected void renderHandles(CupboardTE tileEntity) {
        if (tileEntity.isOpen() || tileEntity.getOpening() < -2) {
            super.renderHandles(tileEntity);
        }
    }

    @Override
    protected void renderExtra(CupboardTE tileEntity) {
        GlStateManager.pushMatrix();
        GL11.glTranslatef(.5f, 1, 0);
        GL11.glRotated(-tileEntity.getOpening() * 1.2, 0, 1, 0);
        if (tileEntity.isOpen() && tileEntity.getOpening() > -60) {
            tileEntity.setOpening(tileEntity.getOpening() - 3);
            if (tileEntity.getOpening() < -60) {
                tileEntity.setOpening(-60);
            }
        } else if ((!tileEntity.isOpen()) && tileEntity.getOpening() < 0) {
            tileEntity.setOpening(tileEntity.getOpening() + 3);
            if (tileEntity.getOpening() > 0) {
                tileEntity.setOpening(0);
            }
        }

        GlStateManager.translate(-tileEntity.getPos().getX(), -tileEntity.getPos().getY(), -tileEntity.getPos().getZ());
        RenderHelper.disableStandardItemLighting();
        this.bindTexture(TextureMap.locationBlocksTexture);
        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT);
        }

        World world = tileEntity.getWorld();
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getWorldRenderer().begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(world, getBakedLidModel(), world.getBlockState(tileEntity.getPos()),
                tileEntity.getPos(),
                Tessellator.getInstance().getWorldRenderer());
        tessellator.draw();

        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();

    }
}
