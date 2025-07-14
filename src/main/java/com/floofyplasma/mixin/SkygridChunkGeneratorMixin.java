package com.floofyplasma.mixin;

import net.minecraft.block.*;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSource;
import net.minecraft.world.gen.chunk.OverworldChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(OverworldChunkGenerator.class)
public class SkygridChunkGeneratorMixin {

    @Shadow
    private World world;

    @Shadow
    private Random random;

    @Unique
    private static final int[] RANDOM_BLOCKS = new int[] {
            Block.STONE.id,
            Block.GRASS.id,
            Block.DIRT.id,
            Block.COBBLESTONE.id,
            Block.PLANKS.id,
            Block.SAPLING.id,
            Block.BEDROCK.id,
            Block.WATER.id,
            Block.LAVA.id,
            Block.SAND.id,
            Block.GRAVEL.id,
            Block.GOLD_ORE.id,
            Block.IRON_ORE.id,
            Block.COAL_ORE.id,
            Block.LOG.id,
            Block.LEAVES.id,
            Block.GLASS.id,
            Block.WOOL.id,
            Block.YELLOW_FLOWER.id,
            Block.RED_FLOWER.id,
            Block.BROWN_MUSHROOM.id,
            Block.RED_MUSHROOM.id,
            Block.GOLD_BLOCK.id,
            Block.IRON_BLOCK.id,
            Block.DOUBLE_STONE_SLAB.id,
            Block.STONE_SLAB.id,
            Block.BRICKS.id,
            Block.BOOKSHELF.id,
            Block.MOSSY_COBBLESTONE.id,
            Block.OBSIDIAN.id,
            Block.OAK_STAIRS.id,
            Block.CHEST.id,
            Block.DIAMOND_ORE.id,
            Block.DIAMOND_BLOCK.id,
            Block.CRAFTING_TABLE.id,
            Block.WHEAT.id,
            Block.FARMLAND.id,
            Block.FURNACE.id,
            Block.WOODEN_DOOR.id,
            Block.LADDER.id,
            Block.STONE_STAIRS.id,
            Block.REDSTONE_ORE.id,
            Block.ICE.id,
            Block.SNOW.id,
            Block.CACTUS.id,
            Block.CLAY.id,
            Block.REEDS.id,
            Block.JUKEBOX.id,
            Block.FENCE.id,
    };

    @Inject(method = "generate", at = @At("HEAD"), cancellable = true)
    private void generateSkygridChunk(int j, int k, byte[] bs, CallbackInfo ci) {
        int gridSpacing = 4;
        int heightLimit = 128;

        for (int x = 0; x < 16; x++) {
            if (x % gridSpacing != 0) continue;

            for (int z = 0; z < 16; z++) {
                if (z % gridSpacing != 0) continue;

                for (int y = 0; y < heightLimit; y++) {
                    if (y % gridSpacing != 0) continue;

                    int index = (x << 11) + (z << 7) + y;
                    bs[index] = (byte) Block.STONE.id;
                }
            }
        }

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < heightLimit; y++) {
                    int index = (x << 11) + (z << 7) + y;
                    if (bs[index] == 0) bs[index] = 0;
                }
            }
        }

        ci.cancel();
    }

    @Inject(method = "generateBiomes", at = @At("HEAD"), cancellable = true)
    public void generateBiomes(int i, int j, byte[] bs, CallbackInfo ci) {
        Random random = new Random(i * 341873128712L + j * 132897987541L);
        int height = 128;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < height; y++) {
                    int index = (x << 11) + (z << 7) + y;
                    if (bs[index] != 0) {
                        bs[index] = (byte) RANDOM_BLOCKS[random.nextInt(RANDOM_BLOCKS.length)];
                    }
                }
            }
        }

        ci.cancel();
    }

    @Unique
    private ItemStack pickLoot(Random random) {
        switch (random.nextInt(12)) {
            case 0: return new ItemStack(Item.IRON_INGOT.id, 1 + random.nextInt(3));
            case 1: return new ItemStack(Item.GOLD_INGOT.id, 1 + random.nextInt(2));
            case 2: return new ItemStack(Item.DIAMOND.id, 1);
            case 3: return new ItemStack(Item.BREAD.id);
            case 4: return new ItemStack(Item.APPLE.id);
            case 5: return new ItemStack(Item.BOW.id);
            case 6: return new ItemStack(Item.ARROW.id, 4 + random.nextInt(8));
            case 7: return new ItemStack(Item.IRON_PICKAXE.id);
            case 8: return new ItemStack(Item.COAL.id, 2 + random.nextInt(4));
            case 9: return new ItemStack(Item.REDSTONE.id, 2 + random.nextInt(4));
            case 10: return new ItemStack(Item.STRING.id, 1 + random.nextInt(3));
            case 11: return new ItemStack(Item.STICK.id, 1 + random.nextInt(4));
            default: return new ItemStack(Item.STICK.id);
        }
    }

    @Inject(method = "populateChunk", at = @At("HEAD"), cancellable = true)
    private void injectSkygridLoot(ChunkSource source, int chunkX, int chunkZ, CallbackInfo ci) {
        int originX = chunkX * 16;
        int originZ = chunkZ * 16;

        int chestCount = 3 + random.nextInt(3);

        for (int i = 0; i < chestCount; i++) {
            int x = originX + random.nextInt(16);
            int y = 16 + random.nextInt(96);
            int z = originZ + random.nextInt(16);

            if (world.getBlock(x, y, z) == 0) {
                world.setBlock(x, y, z, Block.CHEST.id);

                ChestBlockEntity chest = new ChestBlockEntity();
                chest.x = x;
                chest.y = y;
                chest.z = z;
                world.setBlockEntity(x, y, z, chest);

                for (int slot = 0; slot < chest.getSize(); slot++) {
                    if (random.nextFloat() < 0.35F) {
                        chest.setStack(slot, pickLoot(random));
                    }
                }
            }
        }

        ci.cancel();
    }
}
