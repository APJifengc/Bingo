package io.apjifengc.bingo.world;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 一个处理 Schematic 的类
 *
 * @author APJifengc
 */
public class SchematicManager {
    private static EditSession editSession;

    public static void buildSchematic(File file, Location location) throws IOException {
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        Clipboard clipboard;
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            clipboard = reader.read();
        }
        try {
            editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(location.getWorld()));
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
                    .ignoreAirBlocks(false)
                    .build();
            Operations.complete(operation);
            editSession.close();
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
    }

    public static void undo() {
        editSession.undo(editSession);
    }
}
