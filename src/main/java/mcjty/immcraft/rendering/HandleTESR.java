package mcjty.immcraft.rendering;


import mcjty.immcraft.blocks.generic.GenericBlock;
import mcjty.immcraft.blocks.generic.GenericTE;
import mcjty.immcraft.blocks.generic.handles.IInterfaceHandle;
import mcjty.immcraft.config.GeneralConfiguration;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HandleTESR<T extends GenericTE> extends TileEntitySpecialRenderer<T> {

    protected GenericBlock block;

    protected Vec3 textOffset = new Vec3(0, 0, 0);

    public HandleTESR(GenericBlock block) {
        this.block = block;
    }

    @Override
    public void renderTileEntityAt(T tileEntity, double x, double y, double z, float partialTicks, int destroyStage) {

        IBlockState state = tileEntity.getWorld().getBlockState(tileEntity.getPos());
        if (!(state.getBlock() instanceof GenericBlock)) {
            // Safety. In some situations (like with shaders mod installed) this gets called
            // when the block is already removed
            return;
        }

        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();

        GlStateManager.translate(x + .5, y, z + .5);
        GlStateManager.disableRescaleNormal();

        BlockRenderHelper.rotateFacing(tileEntity, block.getMetaUsage());
        renderExtra(tileEntity);
        renderHandles(tileEntity);

        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    protected void renderHandles(T tileEntity) {
        double distanceSq = Minecraft.getMinecraft().thePlayer.getDistanceSq(tileEntity.getPos());
        if (distanceSq > GeneralConfiguration.maxRenderDistanceSquared) {
            return;
        }

        bindTexture(TextureMap.locationBlocksTexture);
        IInterfaceHandle selectedHandle = BlockRenderHelper.getFacingInterfaceHandle(tileEntity, block);
        BlockRenderHelper.renderInterfaceHandles(tileEntity, selectedHandle, textOffset);
    }

    protected void renderExtra(T tileEntity) {

    }
}
