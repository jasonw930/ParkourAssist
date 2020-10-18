package redstoneflash.parkourassist.handler;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Predicate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import redstoneflash.parkourassist.proxy.ClientProxy;
import redstoneflash.parkourassist.util.MathUtils;
import redstoneflash.parkourassist.util.ParkourNode;

@Mod.EventBusSubscriber
public class KeyEventHandler {
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void keyEvent(KeyInputEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayerSP player = mc.player;
		
//		if (ClientProxy.keyTest1.isPressed()) {
//			if (player.hasNoGravity()) {
//				player.setNoGravity(false);
//			} else {
//				player.setNoGravity(true);
//				player.motionY = 0;
//			}
//		}
//		
//		if (ClientProxy.keyTest2.isPressed()) {
//			if (player.hasNoGravity()) {
//				player.motionX = 5 * Math.sin(Math.toRadians(-player.rotationYaw));
//				player.motionZ = 5 * Math.cos(Math.toRadians(-player.rotationYaw));
//			}
//		}
//		
//		if (ClientProxy.keyTest3.isPressed()) {
//			if (player.hasNoGravity()) {
//				player.motionY = 5;
//			}
//			mc.entityRenderer.getMouseOver(1.0f);
//			if (mc.objectMouseOver.entityHit != null) {
//				mc.objectMouseOver.entityHit.motionY = 2;
//				mc.objectMouseOver.entityHit.setNoGravity(true);
//				System.out.printf("%s\n", mc.objectMouseOver.entityHit.getName());
//			}
//		}
		
		if (ClientProxy.keyLocalMode.isPressed()) {
			TickEventHandler.tickFactor = 1;
			Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Local Mode"));
		}
		
		if (ClientProxy.keyServerMode.isPressed()) {
			TickEventHandler.tickFactor = 10;
			Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Server Mode"));
		}
		
		if (ClientProxy.keyTest.isPressed()) {
			System.out.println("Testing");
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), false);
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), false);
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
			ParkourHandler.execution = -1;
			
			//ParkourHandler.predictJump(player, ParkourHandler.targetBlockPos, true);
			if (player.rayTrace(100, 1.0f).typeOfHit == RayTraceResult.Type.BLOCK && player.rayTrace(100, 1.0f).getBlockPos() != null) {
				BlockPos bp = player.rayTrace(100, 1.0f).getBlockPos();				
				ParkourNode start = new ParkourNode(), end = new ParkourNode();
				ParkourNode.generateRegularNodes(start, end, MathUtils.getFootBlockPos(player.posX, player.posY, player.posZ), bp);
				start.display();
				end.display();
			}
			
			TickEventHandler.testBool = true;
			mc.gameSettings.keyBindAttack.setKeyCode(0);
		}
		
		if (ClientProxy.keyTarget.isPressed() && ParkourHandler.execution == -1) {
			if (player.rayTrace(100, 1.0f).typeOfHit == RayTraceResult.Type.BLOCK && player.rayTrace(100, 1.0f).getBlockPos() != null) {
				BlockPos bp = player.rayTrace(100, 1.0f).getBlockPos();
				if (!ParkourHandler.targetList.contains(bp)) {
					ParkourHandler.targetList.add(bp);
				} else {
					ParkourHandler.targetList.remove(bp);
				}
			}
		}
		
		if (ClientProxy.keyReset.isPressed() && ParkourHandler.execution == -1) {
			ParkourHandler.targetList.clear();
		}
		
		if (ClientProxy.keyExecute.isPressed()) {
			ParkourHandler.footBlockPos = MathUtils.getFootBlockPos(player.posX, player.posY, player.posZ);
			if (ParkourHandler.targetList.size() > 0 && ParkourHandler.footBlockPos != null) {
				ParkourHandler.targetBlockPos = ParkourHandler.targetList.get(0);
				ParkourHandler.execution = 0;
				ParkourHandler.pauseTicks = 0;
			}
		}
		
		if (ClientProxy.keyTargetEntity.isPressed()) {
			System.out.printf("Target\n");
			mc.entityRenderer.getMouseOver(1.0f);
			System.out.printf("%s\n", mc.objectMouseOver.toString());
			if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
				TickEventHandler.targetEntity = mc.objectMouseOver.entityHit;
				System.out.printf("Target Found\n");
			}
		}
		
		if (ClientProxy.keyUntargetEntity.isPressed()) {
			TickEventHandler.targetEntity = null;
			System.out.printf("Untarget\n");
		}
		
		if (ClientProxy.keyAutoTarget.isPressed()) {
			TickEventHandler.autoTarget = !TickEventHandler.autoTarget;
			player.sendMessage(new TextComponentString(TickEventHandler.autoTarget ? "Auto Target Enabled" : "Auto Target Disabled"));
			System.out.printf("Auto Target\n");
		}
		
		if (ClientProxy.keyTargetNear.isPressed()) {
			TickEventHandler.targetEntity = getNearestAttackablePlayer(player, 10, 10);
			if (TickEventHandler.targetEntity != null) Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Targeting " + TickEventHandler.targetEntity.getName()));
		}
	}
	
	public static EntityPlayer getNearestAttackablePlayer(Entity entityIn, double maxXZDistance, double maxYDistance)
    {
        return getNearestAttackablePlayer(entityIn.posX, entityIn.posY, entityIn.posZ, maxXZDistance, maxYDistance, (Function)null, (Predicate)null);
    }
	
	public static EntityPlayer getNearestAttackablePlayer(double posX, double posY, double posZ, double maxXZDistance, double maxYDistance, @Nullable Function<EntityPlayer, Double> playerToDouble, @Nullable Predicate<EntityPlayer> p_184150_12_)
    {
        double d0 = -1.0D;
        EntityPlayer entityplayer = null;

        for (int j2 = 0; j2 < Minecraft.getMinecraft().world.playerEntities.size(); ++j2)
        {
            EntityPlayer entityplayer1 = Minecraft.getMinecraft().world.playerEntities.get(j2);

            if (entityplayer1 != Minecraft.getMinecraft().player && !entityplayer1.capabilities.disableDamage && entityplayer1.isEntityAlive() && !entityplayer1.isSpectator() && (p_184150_12_ == null || p_184150_12_.apply(entityplayer1)))
            {
                double d1 = entityplayer1.getDistanceSq(posX, entityplayer1.posY, posZ);
                double d2 = maxXZDistance;

                if (entityplayer1.isSneaking())
                {
                    d2 = maxXZDistance * 0.800000011920929D;
                }

                if (entityplayer1.isInvisible())
                {
                    float f = entityplayer1.getArmorVisibility();

                    if (f < 0.1F)
                    {
                        f = 0.1F;
                    }

                    d2 *= (double)(0.7F * f);
                }

                if (playerToDouble != null)
                {
                    d2 *= ((Double)MoreObjects.firstNonNull(playerToDouble.apply(entityplayer1), Double.valueOf(1.0D))).doubleValue();
                }

                d2 = net.minecraftforge.common.ForgeHooks.getPlayerVisibilityDistance(entityplayer1, d2, maxYDistance);

                if ((maxYDistance < 0.0D || Math.abs(entityplayer1.posY - posY) < maxYDistance * maxYDistance) && (maxXZDistance < 0.0D || d1 < d2 * d2) && (d0 == -1.0D || d1 < d0))
                {
                    d0 = d1;
                    entityplayer = entityplayer1;
                }
            }
        }

        return entityplayer;
    }
	
}
