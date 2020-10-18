package redstoneflash.parkourassist.proxy;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
	
	public static KeyBinding keyTest1;
	public static KeyBinding keyTest2;
	public static KeyBinding keyTest3;
	
	public static KeyBinding keyLocalMode;
	public static KeyBinding keyServerMode;
	
	public static KeyBinding keyTest;
	public static KeyBinding keyTarget;
	public static KeyBinding keyReset;
	public static KeyBinding keyExecute;
	
	public static KeyBinding keyTargetEntity;
	public static KeyBinding keyUntargetEntity;
	public static KeyBinding keyAutoTarget;
	public static KeyBinding keyTargetNear;
	
    public void preInit(FMLPreInitializationEvent event) {
    	super.preInit(event);
    }

    public void init(FMLInitializationEvent event) {
    	super.init(event);
    	
    	keyTest1 = new KeyBinding("Random Test 1", Keyboard.KEY_Y, "Testing");
    	keyTest2 = new KeyBinding("Random Test 2", Keyboard.KEY_U, "Testing");
    	keyTest3 = new KeyBinding("Random Test 3", Keyboard.KEY_I, "Testing");
    	
    	keyLocalMode = new KeyBinding("Local Mode", Keyboard.KEY_J, "Minecraft Assist");
    	keyServerMode = new KeyBinding("Server Mode", Keyboard.KEY_K, "Minecraft Assist");
    	
    	keyTest = new KeyBinding("Test Key", Keyboard.KEY_RETURN, "Parkour Assist");
    	keyTarget = new KeyBinding("Target", Keyboard.KEY_P, "Parkour Assist");
    	keyReset = new KeyBinding("Reset Targets", Keyboard.KEY_L, "Parkour Assist");
    	keyExecute = new KeyBinding("Execute", Keyboard.KEY_O, "Parkour Assist");
    	
    	keyTargetEntity = new KeyBinding("Target Entity", Keyboard.KEY_LBRACKET, "Aim Assist");
    	keyUntargetEntity = new KeyBinding("Untarget Entity", Keyboard.KEY_RBRACKET, "Aim Assist");
    	keyAutoTarget = new KeyBinding("Auto Target", Keyboard.KEY_BACKSLASH, "Aim Assist");
    	keyTargetNear = new KeyBinding("Target Near", Keyboard.KEY_PERIOD, "Aim Assist");

    	ClientRegistry.registerKeyBinding(keyTest1);
    	ClientRegistry.registerKeyBinding(keyTest2);
    	ClientRegistry.registerKeyBinding(keyTest3);
    	
    	ClientRegistry.registerKeyBinding(keyLocalMode);
    	ClientRegistry.registerKeyBinding(keyServerMode);
    	
    	ClientRegistry.registerKeyBinding(keyTest);
    	ClientRegistry.registerKeyBinding(keyTarget);
    	ClientRegistry.registerKeyBinding(keyReset);
    	ClientRegistry.registerKeyBinding(keyExecute);
    	
    	ClientRegistry.registerKeyBinding(keyTargetEntity);
    	ClientRegistry.registerKeyBinding(keyUntargetEntity);
    	ClientRegistry.registerKeyBinding(keyAutoTarget);
    	ClientRegistry.registerKeyBinding(keyTargetNear);
    }
    
    public void postInit(FMLPostInitializationEvent event) {
    	super.postInit(event);
    }
    
}
