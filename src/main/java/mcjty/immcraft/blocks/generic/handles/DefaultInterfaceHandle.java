package mcjty.immcraft.blocks.generic.handles;

import mcjty.immcraft.blocks.generic.GenericInventoryTE;
import mcjty.immcraft.blocks.generic.GenericTE;
import mcjty.immcraft.input.KeyType;
import mcjty.immcraft.varia.InventoryHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

public class DefaultInterfaceHandle<T extends DefaultInterfaceHandle> implements IInterfaceHandle {
    private int slot;
    private EnumFacing side;
    private float minX;
    private float minY;
    private float maxX;
    private float maxY;
    private Vec3d renderOffset;
    private float scale = 1.0f;

    public T slot(int slot) {
        this.slot = slot;
        return (T) this;
    }

    public T side(EnumFacing side) {
        this.side = side;
        return (T) this;
    }

    public T bounds(float minX, float minY, float maxX, float maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        return (T) this;
    }

    public T renderOffset(Vec3d renderOffset) {
        this.renderOffset = renderOffset;
        return (T) this;
    }

    public T scale(float scale) {
        this.scale = scale;
        return (T) this;
    }

    public DefaultInterfaceHandle() {
    }

    @Override
    public Vec3d getRenderOffset() {
        return renderOffset;
    }

    @Override
    public ItemStack getCurrentStack(GenericTE inventoryTE) {
        return GenericInventoryTE.castGenericInventoryTE(inventoryTE)
                .map(p -> p.getStackInSlot(slot))
                .orElse(null);
    }

    @Override
    public float getMinX() {
        return minX;
    }

    @Override
    public float getMaxX() {
        return maxX;
    }

    @Override
    public float getMinY() {
        return minY;
    }

    @Override
    public float getMaxY() {
        return maxY;
    }

    @Override
    public EnumFacing getSide() {
        return side;
    }

    @Override
    public float getScale() {
        return scale;
    }

    @Override
    public boolean acceptAsInput(ItemStack stack) {
        return false;
    }

    @Override
    public int insertInput(GenericTE te, ItemStack stack) {
        int remaining = InventoryHelper.mergeItemStackSafe(GenericInventoryTE.castGenericInventoryTE(te).get(), null, stack, slot, slot + 1, null);
        if (remaining != stack.stackSize) {
            IBlockState state = te.getWorld().getBlockState(te.getPos());
            te.getWorld().notifyBlockUpdate(te.getPos(), state, state, 3);
        }
        return remaining;
    }

    @Override
    public boolean isOutput() {
        return false;
    }

    @Override
    public boolean isCrafting() {
        return false;
    }

    @Override
    public ItemStack extractOutput(GenericTE genericTE, EntityPlayer player, int amount) {
        GenericInventoryTE te = GenericInventoryTE.castGenericInventoryTE(genericTE).get();
        ItemStack stack;
        if (amount == -1) {
            stack = te.getStackInSlot(slot);
            te.setInventorySlotContents(slot, null);
        } else {
            stack = te.decrStackSize(slot, amount);
        }
        IBlockState state = te.getWorld().getBlockState(te.getPos());
        te.getWorld().notifyBlockUpdate(te.getPos(), state, state, 3);
        return stack;
    }

    @Override
    public void onKeyPress(GenericTE genericTE, KeyType keyType, EntityPlayer player) {
    }
}
