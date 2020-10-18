package redstoneflash.parkourassist.handler;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import redstoneflash.parkourassist.gui.GuiHighlight;

@Mod.EventBusSubscriber
public class RenderGuiHandler {

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void postRenderGameOverlayEvent(RenderGameOverlayEvent.Post event) {
		if (event.getType() != ElementType.EXPERIENCE) return;
		new GuiHighlight(Minecraft.getMinecraft());
	}
	
}
