package redstoneflash.parkourassist.util;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import redstoneflash.parkourassist.handler.ParkourHandler;

public class MathUtils {

	//Finds block the input position is standing on is block is non-air full block, y up to 1 block away
	public static BlockPos getFootBlockPos(double x, double y, double z) {
		Minecraft mc = Minecraft.getMinecraft();
		double blockRad = 0.7;
		BlockPos bp = new BlockPos(Math.round(x - 0.5), (int) (Math.ceil(y) - 1), Math.round(z - 0.5));
		if (mc.world.getBlockState(bp).getBlock() != Blocks.AIR) {
			return bp;
		}
		for (double xOff = -(blockRad - 0.5); xOff <= (blockRad - 0.5); xOff += (blockRad - 0.5) * 2) {
			bp = new BlockPos(Math.round(x - 0.5 + xOff), (int) (Math.ceil(y) - 1), Math.round(z - 0.5));
			if (mc.world.getBlockState(bp).getBlock() != Blocks.AIR) {
				return bp;
			}
		}
		for (double zOff = -(blockRad - 0.5); zOff <= (blockRad - 0.5); zOff += (blockRad - 0.5) * 2) {
			bp = new BlockPos(Math.round(x - 0.5), (int) (Math.ceil(y) - 1), Math.round(z - 0.5 + zOff));
			if (mc.world.getBlockState(bp).getBlock() != Blocks.AIR) {
				return bp;
			}
		}
		for (double xOff = -(blockRad - 0.5); xOff <= (blockRad - 0.5); xOff += (blockRad - 0.5) * 2) {
			for (double zOff = -(blockRad - 0.5); zOff <= (blockRad - 0.5); zOff += (blockRad - 0.5) * 2) {
				bp = new BlockPos(Math.round(x - 0.5 + xOff), (int) (Math.ceil(y) - 1), Math.round(z - 0.5 + zOff));
				if (mc.world.getBlockState(bp).getBlock() != Blocks.AIR) {
					return bp;
				}
			}
		}
		return ParkourHandler.footBlockPos;
	}

	public static BlockPos getFootBlockPos(Vec3d vec) {
		return getFootBlockPos(vec.x, vec.y, vec.z);
	}
	
	//Checks if position is on the block, y up to 2 blocks away
	public static boolean onBlock(Vec3d vec, BlockPos bp) {
		if ((int) (Math.ceil(vec.y) - 1) != bp.getY() && (int) (Math.ceil(vec.y) - 2) != bp.getY()) {
			System.out.printf("Mismatch Y\n");
			return false;
		}
		if (Math.abs(bp.getX() + 0.5 - vec.x) > getBlockRad(bp) + 0.05) {
			System.out.printf("Mismatch X\n");
			return false;
		}
		if (Math.abs(bp.getZ() + 0.5 - vec.z) > getBlockRad(bp) + 0.05) {
			System.out.printf("Mismatch Z\n");
			return false;
		}
		return true;
	}
	
	public static double angleBetween(double x1, double y1, double x2, double y2, double x3, double y3) {
//			System.out.printf("%.4f %.4f", Math.toDegrees(Math.atan2(y1-y2, x1-x2)), Math.toDegrees(Math.atan2(y3-y2, x3-x2)));
		return (Math.toDegrees(Math.atan2(y1-y2, x1-x2) - Math.atan2(y3-y2, x3-x2)) + 900) % 360 - 180;
	}
	
	public static double distToPoint(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}
	
	public static double distToLine(double x1, double y1, double x2, double y2, double x3, double y3) {
		return ((y3 - y2) * x1 - (x3 - x2) * y1 + x3 * y2 - y3 * x2) / distToPoint(x2, y2, x3, y3);
	}
	
	public static void printBlockPos(BlockPos bp) {
		printBlockPos(bp, "");
	}
	public static void printBlockPos(BlockPos bp, String end) {
		if (bp == null) System.out.printf("null null null" + end);
		else System.out.printf("%d %d %d" + end, bp.getX(), bp.getY(), bp.getZ());
	}
	
	public static double getBlockHeight(BlockPos pos) {
		IBlockState state = Minecraft.getMinecraft().world.getBlockState(pos);
		if (state.isFullBlock()) return 1;
		if (state.getBlock() instanceof BlockSlab) return 0.5;
		if (state.getBlock().getUnlocalizedName().contains("fence")) return 1.5;
		return 0;
	}
	
	public static double getBlockRad(BlockPos pos) {
		IBlockState state = Minecraft.getMinecraft().world.getBlockState(pos);
		if (state.getBlock().getUnlocalizedName().contains("fence")) return 0.35;
		return 0.7;
	}
	
}
