package mcjty.immcraft.blocks.generic.handles;

import net.minecraft.item.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class InputInterfaceHandle extends DefaultInterfaceHandle<InputInterfaceHandle> {

    private final Set<ItemStack> acceptedItems = new HashSet<>();

    @Override
    public boolean acceptAsInput(ItemStack stack) {
        return acceptedItems.isEmpty() || acceptedItems.stream().anyMatch(p -> p.isItemEqual(stack));
    }

    public InputInterfaceHandle input(ItemStack stack) {
        acceptedItems.add(stack);
        return this;
    }
}
