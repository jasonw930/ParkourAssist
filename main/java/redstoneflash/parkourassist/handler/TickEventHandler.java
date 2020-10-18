package redstoneflash.parkourassist.handler;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import redstoneflash.parkourassist.util.VisualUtils;

@Mod.EventBusSubscriber
public class TickEventHandler {

	public static boolean autoTarget;
	public static Entity targetEntity;
	
	public static boolean testBool = false;
	
	public static int potionState = 0;
	public static int curItem;
	public static float curPitch;
	
	public static float tickFactor = 1;
	public static float tickCount = 0;
	
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void playerTickEvent(PlayerTickEvent event) {
		long startTime = System.currentTimeMillis();
		tickCount = (tickCount + 1) % tickFactor;
		if (tickCount > 0) return;
		//Get and Calculate Variables
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayerSP player = mc.player;
		if (mc.player == null) return;
		
		ParkourHandler.highlight(mc, player);
		ParkourHandler.tick(mc, player);
		
		//Execution State
		if (ParkourHandler.execution == -1) {
			ParkourHandler.idle(mc, player);
		}
		else if (ParkourHandler.execution == 0) {
			ParkourHandler.headRotation(mc, player);
		}
		else if (ParkourHandler.execution == 1) {
			ParkourHandler.align(mc, player);
		}
		else if (ParkourHandler.execution == 2) {
			ParkourHandler.run(mc, player);
		}
		else if (ParkourHandler.execution == 3) {
			ParkourHandler.airMovement(mc, player);
		}
		else if (ParkourHandler.execution == 4) {
			ParkourHandler.landing(mc, player);
		}
		else if (ParkourHandler.execution == 5) {
			ParkourHandler.end(mc, player);
		}
		else if (ParkourHandler.execution >= 6) {
			ParkourHandler.idle(mc, player);
			ParkourHandler.cycle(mc, player);
		}
		
		//Aim Assist
		if (targetEntity != null && potionState == 0) {
			mc.entityRenderer.getMouseOver(1.0f);
			double moveSpeed = mc.objectMouseOver.entityHit == targetEntity ? 0.02 : 0.2;
			
			double tarYaw = (float) -Math.toDegrees(Math.atan2(targetEntity.posX - player.posX, targetEntity.posZ - player.posZ)) + 720;
			while (Math.abs(tarYaw - player.rotationYaw) > 180) tarYaw -= 360;
			player.rotationYaw += (tarYaw - player.rotationYaw) * moveSpeed;
			player.rotationYaw = (player.rotationYaw + 180) % 360 - 180;
			
			double horPlaneDist = Math.sqrt(Math.pow(player.posX - targetEntity.posX, 2) + Math.pow(player.posZ - targetEntity.posZ, 2));
			double tarPitch = (float) Math.toDegrees(Math.atan2(player.posY + player.getEyeHeight() - targetEntity.posY - targetEntity.height / 2, horPlaneDist));
			player.rotationPitch += (tarPitch - player.rotationPitch) * moveSpeed;
			
			VisualUtils.spawnParticle(EnumParticleTypes.PORTAL, new Vec3d(targetEntity.posX, targetEntity.posY + targetEntity.height + 0.2, targetEntity.posZ));
			if (Math.pow(player.posX - targetEntity.posX, 2) + Math.pow(player.posY - targetEntity.posY, 2) + Math.pow(player.posZ - targetEntity.posZ, 2) > 64) {
				targetEntity = null;
			} else if (targetEntity.isDead) {
				targetEntity = null;
			}
		}
		//Potion
		if (player.getHealth() < player.getMaxHealth() * 0.3 && potionState == 0 || testBool) {
			testBool = false;
			curItem = player.inventory.currentItem;
			curPitch = player.rotationPitch;
			for (int i = 0; i < 9; i++) {
				ItemStack is = player.inventory.mainInventory.get(i);
				if (is.getItem() == Items.SPLASH_POTION && is.getTagCompound().getString("Potion").contains("healing")) {
					System.out.printf("Found Potion at %d\n", i);
					KeyBinding.onTick(mc.gameSettings.keyBindsHotbar[i].getKeyCode());
					player.rotationPitch += (90 - curPitch) / 8.0;
					potionState = 1;
					break;
				}
			}
		} else if (potionState > 0 && potionState < 8) {
			player.rotationPitch += (90 - curPitch) / 8.0;
			potionState++;
		} else if (potionState == 8) {
			KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
			potionState++;
		} else if (potionState > 8 && potionState < 12) {
			potionState++;
		} else if (potionState == 12) {
			KeyBinding.onTick(mc.gameSettings.keyBindsHotbar[curItem].getKeyCode());
			player.rotationPitch -= (90 - curPitch) / 8.0;
			potionState++;
		} else if (potionState > 12 && potionState < 20) {
			KeyBinding.onTick(mc.gameSettings.keyBindsHotbar[curItem].getKeyCode());
			player.rotationPitch -= (90 - curPitch) / 8.0;
			potionState++;
		} else if (potionState >= 20 && potionState < 30) {
			potionState++;
		} else if (potionState == 30) {
			potionState = 0;
		}
	}
	
	
}
