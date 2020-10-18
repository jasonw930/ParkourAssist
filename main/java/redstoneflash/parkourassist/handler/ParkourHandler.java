package redstoneflash.parkourassist.handler;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import redstoneflash.parkourassist.util.MathUtils;
import redstoneflash.parkourassist.util.VisualUtils;

public class ParkourHandler {

		//TODO Better MathUtils.getFootBlockPos to reflect block radius
	
		public static final double jumpSpeedInitial = 0.42;
		public static final double jumpMult = 0.98;
		public static final double jumpAccel = -0.08;
		public static final double sprintMult_l = 0.546;
		public static final double sprintAccel_l = 0.1274;
		public static final double sprintMult_la = 0.546;
		public static final double sprintAccel_la = 0.3274;
		public static final double sprintMult_a = 0.91;
		public static final double sprintAccel_a = 0.02548;
		
		public static double rotSpeed = 3;
		public static double alignDist = 0.04;
		
		public static ArrayList<BlockPos> targetList = new ArrayList<BlockPos>();
		public static BlockPos targetBlockPos;
		public static BlockPos footBlockPos;
		
		public static Vec3d targetDir;
		public static double targetYaw;
		public static Vec3d dist;
		public static Vec3d runPathEnd;
		public static Vec3d runPathStart;
		public static Vec3d runPathNormal;
		public static Vec3d predictLandingPos;
		
		public static int pauseTicks;
		
		public static int highlightCD;
		public static long totalTickUpdates;
		public static int execution = -1;
	
		public static void tick(Minecraft mc, EntityPlayerSP player) {
			targetDir = new Vec3d(0, 0, 0);
			if (footBlockPos != null && targetList.size() > 0) {
				targetBlockPos = targetList.get(0);
				targetDir = new Vec3d(targetBlockPos).subtract(new Vec3d(footBlockPos));
				if (Math.abs(targetDir.x) == 1) {
					targetDir = new Vec3d(0, targetDir.y, targetDir.z);
				} else if (Math.abs(targetDir.x) > 1) {
					targetDir = targetDir.addVector((MathUtils.getBlockRad(footBlockPos) + MathUtils.getBlockRad(targetBlockPos)) * (targetDir.x < 0 ? 1 : targetDir.x > 0 ? -1 : 0), 0, 0);
				}
				if (Math.abs(targetDir.z) == 1) {
					targetDir = new Vec3d(targetDir.x, targetDir.y, 0);
				} else if (Math.abs(targetDir.z) > 1){
					targetDir = targetDir.addVector(0, 0, (MathUtils.getBlockRad(footBlockPos) + MathUtils.getBlockRad(targetBlockPos)) * (targetDir.z < 0 ? 1 : targetDir.z > 0 ? -1 : 0));
				}
				targetYaw = -Math.toDegrees(Math.atan2(targetDir.x, targetDir.z)) + 720;
				while (Math.abs(targetYaw - player.rotationYaw) > 180) targetYaw -= 360;
				
				//Calculate Run Path
				dist = new Vec3d(targetBlockPos).subtract(new Vec3d(footBlockPos));
				runPathEnd = new Vec3d(footBlockPos).addVector(0.5, 1, 0.5);
				runPathStart = new Vec3d(footBlockPos).addVector(0.5, 1, 0.5);
				if (dist.x == 0) {
					runPathEnd = new Vec3d(player.posX, footBlockPos.getY()+1, footBlockPos.getZ() + 0.5 + MathUtils.getBlockRad(footBlockPos) * (dist.z > 0 ? 1 : -1));
					runPathStart = new Vec3d(player.posX, footBlockPos.getY()+1, footBlockPos.getZ() + 0.5 - MathUtils.getBlockRad(footBlockPos) * (dist.z > 0 ? 1 : -1));
//					if (false && !MathUtils.getFootBlockPos(runPathStart.addVector(-0.01,1,-0.01)).equals(footBlockPos)) {
//						runPathStart = new Vec3d(player.posX, footBlockPos.getY()+1, footBlockPos.getZ() + 0.5 - (MathUtils.getBlockRad(footBlockPos)-0.5) * (dist.z > 0 ? 1 : -1));
//					}
				} else if (dist.z == 0) {
					runPathEnd = new Vec3d(footBlockPos.getX() + 0.5 + MathUtils.getBlockRad(footBlockPos) * (dist.x > 0 ? 1 : -1), footBlockPos.getY()+1, player.posZ);
					runPathStart = new Vec3d(footBlockPos.getX() + 0.5 - MathUtils.getBlockRad(footBlockPos) * (dist.x > 0 ? 1 : -1), footBlockPos.getY()+1, player.posZ);
//					if (false && !MathUtils.getFootBlockPos(runPathStart.addVector(-0.01,1,-0.01)).equals(footBlockPos)) {
//						runPathStart = new Vec3d(footBlockPos.getX() + 0.5 - (MathUtils.getBlockRad(footBlockPos)-0.5) * (dist.x > 0 ? 1 : -1), footBlockPos.getY()+1, player.posZ);
//					}
				} else {
					runPathEnd = new Vec3d(footBlockPos.getX() + 0.5 + MathUtils.getBlockRad(footBlockPos) * (dist.x > 0 ? 1 : -1), footBlockPos.getY()+1, footBlockPos.getZ() + 0.5 + MathUtils.getBlockRad(footBlockPos) * (dist.z > 0 ? 1 : -1));
					if (Math.abs(targetDir.x) == Math.abs(targetDir.z)) {
						runPathStart = new Vec3d(footBlockPos.getX() + 0.5 - MathUtils.getBlockRad(footBlockPos) * (dist.x > 0 ? 1 : -1), footBlockPos.getY()+1, footBlockPos.getZ() + 0.5 - MathUtils.getBlockRad(footBlockPos) * (dist.z > 0 ? 1 : -1));
//						if (false && !MathUtils.getFootBlockPos(runPathStart.addVector(-0.01,1,-0.01)).equals(footBlockPos)) {
//							runPathStart = new Vec3d(footBlockPos.getX() + 0.5 - (MathUtils.getBlockRad(footBlockPos)-0.5) * (dist.x > 0 ? 1 : -1), footBlockPos.getY()+1, footBlockPos.getZ() + 0.5 - (MathUtils.getBlockRad(footBlockPos)-0.5) * (dist.z > 0 ? 1 : -1));
//						}
					} else if (Math.abs(targetDir.x) > Math.abs(targetDir.z)) {
						double x = footBlockPos.getX() + 0.5 - MathUtils.getBlockRad(footBlockPos) * (dist.x > 0 ? 1 : -1);
						double z = footBlockPos.getZ() + 0.5 + MathUtils.getBlockRad(footBlockPos) * (dist.z > 0 ? 1 : -1);
						z -= 2 * MathUtils.getBlockRad(footBlockPos) * Math.abs(targetDir.z / targetDir.x) * (dist.z > 0 ? 1 : -1);
						runPathStart = new Vec3d(x, footBlockPos.getY()+1, z);
//						if (false && !MathUtils.getFootBlockPos(runPathStart.addVector(-0.01,1,-0.01)).equals(footBlockPos)) {
//							x = footBlockPos.getX() + 0.5 - (MathUtils.getBlockRad(footBlockPos)-0.5) * (dist.x > 0 ? 1 : -1);
//							z = footBlockPos.getZ() + 0.5 + MathUtils.getBlockRad(footBlockPos) * (dist.z > 0 ? 1 : -1);
//							z -= 2 * (0.5) * Math.abs(targetDir.z / targetDir.x) * (dist.z > 0 ? 1 : -1);
//							runPathStart = new Vec3d(x, footBlockPos.getY()+1, z);
//						}
					} else {
						double x = footBlockPos.getX() + 0.5 + MathUtils.getBlockRad(footBlockPos) * (dist.x > 0 ? 1 : -1);
						double z = footBlockPos.getZ() + 0.5 - MathUtils.getBlockRad(footBlockPos) * (dist.z > 0 ? 1 : -1);
						x -= 2 * MathUtils.getBlockRad(footBlockPos) * Math.abs(targetDir.x / targetDir.z) * (dist.x > 0 ? 1 : -1);
						runPathStart = new Vec3d(x, footBlockPos.getY()+1, z);
//						if (false && !MathUtils.getFootBlockPos(runPathStart.addVector(-0.01,1,-0.01)).equals(footBlockPos)) {
//							x = footBlockPos.getX() + 0.5 + MathUtils.getBlockRad(footBlockPos) * (dist.x > 0 ? 1 : -1);
//							z = footBlockPos.getZ() + 0.5 - (MathUtils.getBlockRad(footBlockPos)-0.5) * (dist.z > 0 ? 1 : -1);
//							x -= 2 * (0.5) * Math.abs(targetDir.x / targetDir.z) * (dist.x > 0 ? 1 : -1);
//							runPathStart = new Vec3d(x, footBlockPos.getY()+1, z);
//						}
					}
				}
				runPathNormal = runPathStart.addVector(runPathStart.z - runPathEnd.z, 0, runPathEnd.x - runPathStart.x);
				
				predictLandingPos = predictJump(player, targetBlockPos, false);
			} else {
				targetDir = null;
				targetYaw = 0;
				dist = null;
				runPathEnd = null;
				runPathStart = null;
				predictLandingPos = null;
			}
			
			if (execution != -1 && player.posY - MathUtils.getBlockHeight(targetBlockPos) - targetBlockPos.getY() < -2) { 
				execution = 4;
			}
		}
		
		public static void idle(Minecraft mc, EntityPlayerSP player) {
			footBlockPos = MathUtils.getFootBlockPos(player.posX, player.posY, player.posZ);
		}
		
		public static void headRotation(Minecraft mc, EntityPlayerSP player) {
			if (Math.abs(targetYaw - player.rotationYaw) <= rotSpeed) {
				player.rotationYaw = (float) targetYaw;
			} else {
				player.rotationYaw += rotSpeed * (targetYaw > player.rotationYaw ? 1 : -1);
			}
			
			player.rotationYaw = (player.rotationYaw + 180) % 360 - 180;
			targetYaw = (targetYaw + 180) % 360 - 180;
			if (Math.abs(player.rotationYaw - targetYaw) < 0.1) {
				execution = 1;
				System.out.printf("Execution state %d\n", execution);
			}
		}
		
		public static void align(Minecraft mc, EntityPlayerSP player) {
			double dPath = MathUtils.distToLine(player.posX, player.posZ, runPathEnd.x, runPathEnd.z, runPathStart.x, runPathStart.z);
			double dNormal = MathUtils.distToLine(player.posX, player.posZ, runPathStart.x, runPathStart.z, runPathNormal.x, runPathNormal.z);
			
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
			if (dPath >= alignDist) {
				pauseTicks = 0;
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), true);
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), false);
			} else if (dPath <= -alignDist) {
				pauseTicks = 0;
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), false);
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), true);
			} else {
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), false);
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), false);
			}
			
			if (dNormal >= alignDist) {
				pauseTicks = 0;
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), true);
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
			} else if (dNormal <= -alignDist) {
				pauseTicks = 0;
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
			} else {
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
			}
			
			if (Math.abs(dPath) <= alignDist && Math.abs(dNormal) <= alignDist && player.motionX * player.motionX + player.motionZ * player.motionZ < 0.1) {
				pauseTicks++;
				if (pauseTicks > 10) {
					pauseTicks = 0;
					execution = 2;
					System.out.printf("Execution state %d\n", execution);
				}
			}
		}
		
		public static void run(Minecraft mc, EntityPlayerSP player) {
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
			
			Vec3d pPos = new Vec3d(player.posX, player.posY, player.posZ);
			Vec3d pVel = new Vec3d(player.motionX, player.motionY, player.motionZ);
			for (int i = 0; i < 0; i++) {
				double pVelX = (pVel.x + sprintAccel_l * Math.sin(Math.toRadians(-player.rotationYaw))) * sprintMult_l;
				double pVelY = 0;
				double pVelZ = (pVel.z + sprintAccel_l * Math.cos(Math.toRadians(-player.rotationYaw))) * sprintMult_l;
				pVel = new Vec3d(pVelX, pVelY, pVelZ);
				pPos = pPos.add(pVel);
			}
					
			if (MathUtils.getFootBlockPos(predictLandingPos) != null && MathUtils.getFootBlockPos(predictLandingPos).equals(targetBlockPos) && player.posY == runPathEnd.y) {
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), true);
			}
			if (!MathUtils.onBlock(pPos, footBlockPos)) {
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), true);
				System.out.printf("------------\n");
				System.out.printf("Fall Prevention Jump\n");
			}
			if (player.motionY > 0.2) {
				execution = 3;
				System.out.printf("Execution state %d\n", execution);
			}
		}
		
		public static void airMovement(Minecraft mc, EntityPlayerSP player) {
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
			if (Math.abs(player.posX - (targetBlockPos.getX() + 0.5)) < MathUtils.getBlockRad(targetBlockPos) && Math.abs(player.posZ - (targetBlockPos.getZ() + 0.5)) < MathUtils.getBlockRad(targetBlockPos) && 
					player.motionX * player.motionX + player.motionZ * player.motionZ > 0.1) {
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), true);
			}
			System.out.printf("%f %f\n", player.posY - MathUtils.getBlockHeight(targetBlockPos) - targetBlockPos.getY(), player.motionY);
			if (player.posY - MathUtils.getBlockHeight(targetBlockPos) - targetBlockPos.getY() <= 0.1 && player.motionY <= 0.1) {
				execution = 4;
				System.out.printf("Execution state %d\n", execution);
			}
		}
		
		public static void landing(Minecraft mc, EntityPlayerSP player) {
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), true);
			if (player.motionX * player.motionX + player.motionZ * player.motionZ <= 0.01) {
				execution = 5;
				System.out.printf("Execution state %d\n", execution);
			}
		}
		
		public static void end(Minecraft mc, EntityPlayerSP player) {
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
			
			targetList.remove(0);
			targetBlockPos = null;
			predictLandingPos = null;
			if (targetList.size() > 0) {
				System.out.printf("Getting next target\n");
				targetBlockPos = targetList.get(0);
				footBlockPos = MathUtils.getFootBlockPos(player.posX, player.posY, player.posZ);
				execution = 6;
				pauseTicks = 0;
			} else {
				execution = -1;
			}
			System.out.printf("Execution state %d\n", execution);
		}
		
		public static void cycle(Minecraft mc, EntityPlayer player) {
			execution++;
			if (execution >= 12) {
				execution = 0;
			}
		}
		
		public static void highlight(Minecraft mc, EntityPlayerSP player) {
			if (highlightCD < 0) {
				highlightCD = 20;
				VisualUtils.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, runPathEnd);
				VisualUtils.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, runPathStart);
				for (int i = 0; i < 8; i++)
					VisualUtils.spawnParticle(EnumParticleTypes.PORTAL, predictLandingPos);
				for (int i = 0; i < targetList.size(); i++)
					VisualUtils.highlightBlock(targetList.get(i), EnumParticleTypes.VILLAGER_HAPPY);
				VisualUtils.highlightBlock(footBlockPos, EnumParticleTypes.FLAME);
				VisualUtils.highlightBlock(predictLandingPos, EnumParticleTypes.PORTAL);
//				spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, runPathNormal);
			}
			if (highlightCD >= 0) highlightCD--;
		}
		
		public static Vec3d predictJump(EntityPlayerSP player, BlockPos endBlock, boolean display) {
			if (endBlock == null) return null;
			
			Vec3d pPos = new Vec3d(player.posX, player.posY, player.posZ);
			Vec3d pVel = new Vec3d(player.motionX, player.motionY, player.motionZ);
			BlockPos footBlockPos = MathUtils.getFootBlockPos(pPos);
			
			//If not jumping, simulate sprint ticks and 1 jump tick
			if (Math.abs(player.motionY) < 0.1) {
				for (int i = 0; i < 0; i++) {
					double pVelX = (pVel.x + sprintAccel_a * Math.sin(Math.toRadians(-player.rotationYaw))) * sprintMult_a;
					double pVelY = 0;
					double pVelZ = (pVel.z + sprintAccel_a * Math.cos(Math.toRadians(-player.rotationYaw))) * sprintMult_a;
					pVel = new Vec3d(pVelX, pVelY, pVelZ);
					pPos = pPos.add(pVel);
				}
				double pVelX = (pVel.x + sprintAccel_la * Math.sin(Math.toRadians(-player.rotationYaw))) * sprintMult_la;
				double pVelY = jumpSpeedInitial;
				double pVelZ = (pVel.z + sprintAccel_la * Math.cos(Math.toRadians(-player.rotationYaw))) * sprintMult_la;
				pVel = new Vec3d(pVelX, pVelY, pVelZ);
				pPos = pPos.add(pVel);
			}
			
			//Air ticks
			double pDiffY = pPos.y - player.posY;
			double diffY = (endBlock.getY() + MathUtils.getBlockHeight(endBlock)) - (footBlockPos.getY() + MathUtils.getBlockHeight(footBlockPos));
			int predictionLen = 0;
			while (pDiffY > diffY || pVel.y > 0) {
				double pVelX = (pVel.x + sprintAccel_a * Math.sin(Math.toRadians(-player.rotationYaw))) * sprintMult_a;
				double pVelY = (pVel.y + jumpAccel) * jumpMult;
				double pVelZ = (pVel.z + sprintAccel_a * Math.cos(Math.toRadians(-player.rotationYaw))) * sprintMult_a;
				pVel = new Vec3d(pVelX, pVelY, pVelZ);
				pPos = pPos.add(pVel);
				
				pDiffY = pPos.y - player.posY;
				diffY = (endBlock.getY() + MathUtils.getBlockHeight(endBlock)) - (footBlockPos.getY() + MathUtils.getBlockHeight(footBlockPos));
				if (display) VisualUtils.spawnParticle(EnumParticleTypes.FLAME, pPos);
			}
			
			return pPos;
		}
		
}
