package io.apjifengc.bingo.world;

import de.tr7zw.changeme.nbtapi.NBTFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * 一个处理 Schematic 的类
 *
 * @author APJifengc
 */
public class SchematicManager {
    private static Runnable undo;

    public static void buildSchematic(File file, Location location) throws IOException {
        NBTFile nbt = new NBTFile(file);
        var height = nbt.getShort("Height");
        var width = nbt.getShort("Width");
        var length = nbt.getShort("Length");
        var metadata = nbt.getCompound("Metadata");
        var offsetX = metadata.getInteger("WEOffsetX");
        var offsetY = metadata.getInteger("WEOffsetY");
        var offsetZ = metadata.getInteger("WEOffsetZ");
        var paletteMax = nbt.getInteger("PaletteMax");
        var palette = nbt.getCompound("Palette");
        var blocks = nbt.getByteArray("BlockData");
        BlockData[] datas = new BlockData[paletteMax];
        for (String data : palette.getKeys()) {
            datas[palette.getInteger(data)] = Bukkit.getServer().createBlockData(data);
        }
        int baseX = location.getBlockX() + offsetX;
        int baseY = location.getBlockY() + offsetY;
        int baseZ = location.getBlockZ() + offsetZ;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    new Location(location.getWorld(), baseX + x, baseY + y, baseZ + z)
                            .getBlock().setBlockData(datas[blocks[(y * length + z) * width + x]]);
                }
            }
        }
        undo = () -> {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    for (int z = 0; z < length; z++) {
                        new Location(location.getWorld(), baseX + x, baseY + y, baseZ + z)
                                .getBlock().setBlockData(Material.AIR.createBlockData());
                    }
                }
            }
        };
    }

    public static void undo() {
        undo.run();
    }
}
