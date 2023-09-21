package io.stellaleaf.lightcmd;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import ru.beykerykt.lightapi.LightAPI;
import ru.beykerykt.lightapi.LightType;
import ru.beykerykt.lightapi.chunks.ChunkInfo;

public class lightCMD extends JavaPlugin {
	public void setLight(final Location location, int lightLevel, final LightType lightType) {
        if (!LightAPI.isSupported(location.getWorld(), lightType)) return;

        Block block = location.getBlock();
        
        int oldLightLevel = lightType == LightType.BLOCK ? block.getLightFromBlocks() : block.getLightFromSky();

        if (oldLightLevel == lightLevel) return;

        if (lightLevel > 0) {
            LightAPI.createLight(location, lightType, lightLevel, false);
//            if(lightTime != 0 ) {
//                getServer().getScheduler().runTaskLater(this, new Runnable() {
//                @Override
//                    public void run() {
//                    	LightAPI.deleteLight(location, lightType, false);
//                    }
//                }, lightTime * 20);
//            }
        } else {
            LightAPI.deleteLight(location, lightType, false);
        }
        for (ChunkInfo chunkInfo : LightAPI.collectChunks(location, lightType, Math.max(lightLevel, oldLightLevel))) {
            LightAPI.updateChunk(chunkInfo, lightType);
        }
    }
	
	public void onEnable() {
		getLogger().info("LightCMD is Loaded");
	}
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (args.length != 0) {
			try {
				double locX = Double.parseDouble(args[0]);
				double locY = Double.parseDouble(args[1]);
				double locZ = Double.parseDouble(args[2]);
				String worldName = args[3];
				int lightLevel = Integer.parseInt(args[4]);
				LightType lightType = LightType.BLOCK;
				if (args[5] == "SKY") {
					lightType = LightType.SKY;
				}
//				int lightTime = Integer.parseInt(args[6]);
				Location setLocation = new Location(Bukkit.getWorld(worldName),locX,locY,locZ);
				setLight(setLocation, lightLevel, lightType);
				if (lightLevel > 0) {
					sender.sendMessage("[LightCMD] Light set in X: " + args[0] + " Y: " + args[1] + " Z: " + args[2] + "in WorldName: " + args[3]);
				} else if (lightLevel == 0) {
					sender.sendMessage("[LightCMD] Light deleted in X: " + args[0] + " Y: " + args[1] + " Z: " + args[2] + "in WorldName: " + args[3]);
				}
			} catch (NumberFormatException e) {
				sender.sendMessage("[LightCMD] /light x y z worldName lightLevel BLOCK/SKY");
			}
				return true;
		}
		sender.sendMessage("[LightCMD] /light x y z worldName lightLevel BLOCK/SKY");
		return false;
	}
}
