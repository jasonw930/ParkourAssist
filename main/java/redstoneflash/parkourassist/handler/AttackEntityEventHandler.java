package redstoneflash.parkourassist.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class AttackEntityEventHandler {

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void attackEntityEvent(AttackEntityEvent event) {
		//Minecraft.getMinecraft().player.sendMessage(new TextComponentString(String.format("%s hit by %s", event.getTarget().getName(), event.getEntityPlayer().getName())));
		if (Minecraft.getMinecraft().player.getName() == event.getEntityPlayer().getName() && TickEventHandler.autoTarget) {
			TickEventHandler.targetEntity = event.getTarget();
			Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Targeting " + event.getTarget().getName()));
		}
	}
	
}
