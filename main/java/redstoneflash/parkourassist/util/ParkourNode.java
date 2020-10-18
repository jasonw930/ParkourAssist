package redstoneflash.parkourassist.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ParkourNode {

	public Vec3d start;
	public Vec3d startNormal;
	public Vec3d end;
	public Vec3d endNormal;
	
	public static void generateRegularNodes(ParkourNode startNode, ParkourNode endNode, BlockPos startBlock, BlockPos endBlock) {
		Vec3d dPos = new Vec3d(endBlock).subtract(new Vec3d(startBlock));
		if (Math.abs(dPos.x) - MathUtils.getBlockRad(startBlock) - MathUtils.getBlockRad(endBlock) > 0) {
			dPos = dPos.addVector(-(MathUtils.getBlockRad(startBlock) + MathUtils.getBlockRad(endBlock)) * (dPos.x > 0 ? 1: -1), 0, 0);
		} else {
			dPos = new Vec3d(0, dPos.y, dPos.z);
		}
		if (Math.abs(dPos.z) - MathUtils.getBlockRad(startBlock) - MathUtils.getBlockRad(endBlock) > 0) {
			dPos = dPos.addVector(0, 0, -(MathUtils.getBlockRad(startBlock) + MathUtils.getBlockRad(endBlock)) * (dPos.z > 0 ? 1: -1));
		} else {
			dPos = new Vec3d(dPos.x, dPos.y, 0);
		}
		startNode.start = new Vec3d(startBlock).addVector(0.5, 1, 0.5);
		startNode.end = new Vec3d(startBlock).addVector(0.5, 1, 0.5);
		endNode.start = new Vec3d(endBlock).addVector(0.5, 1, 0.5);
		endNode.end = new Vec3d(endBlock).addVector(0.5, 1, 0.5);
				
		if (dPos.x == 0) {
			double dX = (Math.abs(endBlock.getX() - startBlock.getX()) - MathUtils.getBlockRad(endBlock)) + MathUtils.getBlockRad(startBlock);
			dX /= 2;
			dX *= endBlock.getX() - startBlock.getX() > 0 ? 1 : -1;
			startNode.start = startNode.start.addVector(dX, 0, -MathUtils.getBlockRad(startBlock) * (dPos.z > 0 ? 1 : -1));
			startNode.end = startNode.end.addVector(dX, 0, MathUtils.getBlockRad(startBlock) * (dPos.z > 0 ? 1 : -1));
			dX = (Math.abs(endBlock.getX() - startBlock.getX()) - MathUtils.getBlockRad(startBlock)) + MathUtils.getBlockRad(endBlock);
			dX /= 2;
			dX *= startBlock.getX() - endBlock.getX() > 0 ? 1 : -1;
			endNode.start = endNode.start.addVector(dX, 0, -MathUtils.getBlockRad(endBlock) * (dPos.z > 0 ? 1 : -1));
			endNode.end = endNode.end.addVector(dX, 0, MathUtils.getBlockRad(endBlock) * (dPos.z > 0 ? 1 : -1));
		} else if (dPos.z == 0) {
			double dZ = (Math.abs(endBlock.getZ() - startBlock.getZ()) - MathUtils.getBlockRad(endBlock)) + MathUtils.getBlockRad(startBlock);
			dZ /= 2;
			dZ *= endBlock.getZ() - startBlock.getZ() > 0 ? 1 : -1;
			startNode.start = startNode.start.addVector(-MathUtils.getBlockRad(startBlock) * (dPos.x > 0 ? 1 : -1), 0, dZ);
			startNode.end = startNode.end.addVector(MathUtils.getBlockRad(startBlock) * (dPos.x > 0 ? 1 : -1), 0, dZ);
			dZ = (Math.abs(endBlock.getZ() - startBlock.getZ()) - MathUtils.getBlockRad(startBlock)) + MathUtils.getBlockRad(endBlock);
			dZ /= 2;
			dZ *= startBlock.getZ() - endBlock.getZ() > 0 ? 1 : -1;
			endNode.start = endNode.start.addVector(-MathUtils.getBlockRad(endBlock) * (dPos.x > 0 ? 1 : -1), 0, dZ);
			endNode.end = endNode.end.addVector(MathUtils.getBlockRad(endBlock) * (dPos.x > 0 ? 1 : -1), 0, dZ);
		} else {
			startNode.end = startNode.end.addVector(MathUtils.getBlockRad(startBlock) * (dPos.x > 0 ? 1 : -1), 0, MathUtils.getBlockRad(startBlock) * (dPos.z > 0 ? 1 : -1));
			endNode.start = endNode.start.addVector(-MathUtils.getBlockRad(endBlock) * (dPos.x > 0 ? 1 : -1), 0, -MathUtils.getBlockRad(endBlock) * (dPos.z > 0 ? 1 : -1));
			if (Math.abs(dPos.x) == Math.abs(dPos.z)) {
				startNode.start = startNode.start.addVector(-MathUtils.getBlockRad(startBlock) * (dPos.x > 0 ? 1 : -1), 0, -MathUtils.getBlockRad(startBlock) * (dPos.z > 0 ? 1 : -1));
				endNode.end = endNode.end.addVector(MathUtils.getBlockRad(endBlock) * (dPos.x > 0 ? 1 : -1), 0, MathUtils.getBlockRad(endBlock) * (dPos.z > 0 ? 1 : -1));
			} else if (Math.abs(dPos.x) > Math.abs(dPos.z)) {				
				double dX = -MathUtils.getBlockRad(startBlock) * (dPos.x > 0 ? 1 : -1);
				double dZ = MathUtils.getBlockRad(startBlock) * (dPos.z > 0 ? 1 : -1);
				dZ -= 2 * MathUtils.getBlockRad(startBlock) * Math.abs(dPos.z / dPos.x) * (dPos.z > 0 ? 1 : -1);
				startNode.start = startNode.start.addVector(dX, 0, dZ);
				dX = MathUtils.getBlockRad(endBlock) * (dPos.x > 0 ? 1 : -1);
				dZ = -MathUtils.getBlockRad(endBlock) * (dPos.z > 0 ? 1 : -1);
				dZ += 2 * MathUtils.getBlockRad(endBlock) * Math.abs(dPos.z / dPos.x) * (dPos.z > 0 ? 1 : -1);
				endNode.end = endNode.end.addVector(dX, 0, dZ);
			} else {
				double dX = MathUtils.getBlockRad(startBlock) * (dPos.x > 0 ? 1 : -1);
				double dZ = -MathUtils.getBlockRad(startBlock) * (dPos.z > 0 ? 1 : -1);
				dX -= 2 * MathUtils.getBlockRad(startBlock) * Math.abs(dPos.x / dPos.z) * (dPos.x > 0 ? 1 : -1);
				startNode.start = startNode.start.addVector(dX, 0, dZ);
				dX = -MathUtils.getBlockRad(endBlock) * (dPos.x > 0 ? 1 : -1);
				dZ = MathUtils.getBlockRad(endBlock) * (dPos.z > 0 ? 1 : -1);
				dX += 2 * MathUtils.getBlockRad(endBlock) * Math.abs(dPos.x / dPos.z) * (dPos.x > 0 ? 1 : -1);
				endNode.end = endNode.end.addVector(dX, 0, dZ);
			}
		}
		
		System.out.printf("%.4f %.4f\n", dPos.x, dPos.z);
		startNode.createNormals();
		endNode.createNormals();
	}
	
	public ParkourNode() {
		start = new Vec3d(0, 0, 0);
		startNormal = new Vec3d(0, 0, 0);
		end = new Vec3d(0, 0, 0);
		endNormal = new Vec3d(0, 0, 0);
	}
	
	public ParkourNode createNormals() {
		startNormal = start.addVector((start.z - end.z) / 4, 0, (end.x - start.x) / 4);
		endNormal = end.addVector((end.z - start.z) / 4, 0, (start.x - end.x) / 4);
		return this;
	}
	
	public double distToStart(Vec3d pos) {
		return MathUtils.distToLine(pos.z, pos.x, start.z, start.x, startNormal.z, startNormal.x);
	}
	
	public double distToEnd(Vec3d pos) {
		return MathUtils.distToLine(pos.z, pos.x, end.z, end.x, endNormal.z, endNormal.x);
	}
	
	public void display() {
		VisualUtils.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, start);
		VisualUtils.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, startNormal);
		VisualUtils.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, end);
		VisualUtils.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, endNormal);
	}
	
}
