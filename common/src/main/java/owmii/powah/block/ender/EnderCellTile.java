package owmii.powah.block.ender;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import owmii.powah.lib.block.IInventoryHolder;
import owmii.powah.lib.block.IOwnable;
import owmii.powah.block.Tier;
import owmii.powah.block.Tiles;

public class EnderCellTile extends AbstractEnderTile<EnderCellBlock> implements IOwnable, IInventoryHolder {
    public EnderCellTile(BlockPos pos, BlockState state, Tier variant) {
        super(Tiles.ENDER_CELL.get(), pos, state, variant);
        this.inv.add(3);
    }

    public EnderCellTile(BlockPos pos, BlockState state) {
        this(pos, state, Tier.STARTER);
    }

    @Override
    public boolean isExtender() {
        return true;
    }
}
