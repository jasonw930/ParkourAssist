package redstoneflash.parkourassist.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class VisualUtils {

	public static void highlightBlock(BlockPos bp, EnumParticleTypes pt) {
		Minecraft mc = Minecraft.getMinecraft();
		if (bp == null) return;
		for (float x = bp.getX(); x <= bp.getX() + 1; x += 0.5)
			for (float y = bp.getY(); y <= bp.getY() + 1; y++)
				for (float z = bp.getZ(); z <= bp.getZ() + 1; z++)
					mc.world.spawnParticle(pt, x, y, z, 0, 0, 0);
		for (double x = bp.getX(); x <= bp.getX() + 1; x++)
			for (double y = bp.getY(); y <= bp.getY() + 1; y += 0.5)
				for (double z = bp.getZ(); z <= bp.getZ() + 1; z++)
					mc.world.spawnParticle(pt, x, y, z, 0, 0, 0);
		for (double x = bp.getX(); x <= bp.getX() + 1; x++)
			for (double y = bp.getY(); y <= bp.getY() + 1; y++)
				for (double z = bp.getZ(); z <= bp.getZ() + 1; z += 0.5)
					mc.world.spawnParticle(pt, x, y, z, 0, 0, 0);
	}
	
	public static void highlightBlock(Vec3d vec, EnumParticleTypes pt) {
		if (vec == null) return;
		highlightBlock(MathUtils.getFootBlockPos(vec.x, vec.y, vec.z), pt);
	}
	
	public static void spawnParticle(EnumParticleTypes type, Vec3d pos) {
		if (pos == null) return;
		Minecraft.getMinecraft().world.spawnParticle(type, pos.x, pos.y, pos.z, 0, 0, 0);
	}
	
}
