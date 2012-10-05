package me.Destro168.FC_Announcer;

import java.util.HashMap;
import java.util.Map;

import me.Destro168.Messaging.MessageLib;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class FC_Announcer extends JavaPlugin 
{	
	//Variables
	public final static int minimumAnnouncementLength = 1;
	public static AnnouncementManager am;
	
    public static FC_Announcer plugin;
	
    public static Map<Player, Boolean> isSelectingFirst = new HashMap<Player, Boolean>();
    public static Map<Player, Location> block1Location = new HashMap<Player, Location>();
    public static Map<Player, Location> block2Location = new HashMap<Player, Location>();
    
    private AnnouncerCE announcerExecutor;
    
	@Override
	public void onDisable()
	{
		this.getLogger().info("Disabled Successfully");
	}
	
	@Override
	public void onEnable()
	{
		//Set the plugin.
		plugin = this;
		
		//Variable initializations
		am = new AnnouncementManager();
		
		//Create listener for /announcer
		announcerExecutor = new AnnouncerCE();
		getCommand("Announcer").setExecutor(announcerExecutor);
		
		//Add listeners
		getServer().getPluginManager().registerEvents(new PlayerInteractionListener(), this);
		
		//Delay start by 1 second to give breathing room for permissions.
		Bukkit.getScheduler().scheduleAsyncDelayedTask(this, new Runnable()
		{
			public void run()
			{
				//Perform a reload
				am.reload();
			}
		}, 20);
		
		//Log the succesful enable.
		this.getLogger().info("Enabled Successfully");
	}
	
	//Handle selections for zones.
	public class PlayerInteractionListener implements Listener
	{
		@EventHandler
		public void onPlayerInteract(PlayerInteractEvent event)
		{
			//Variable declarations
			Player player = event.getPlayer();
			FC_AnnouncerPermissions ap;
			MessageLib msgLib = new MessageLib(player);
			ap = new FC_AnnouncerPermissions(player);
			boolean isFirst = false;
			
			//Only perform event on right-clicks.
			if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
				return;
			
			//if they don't have permission return;
			if (!ap.isAdmin())
				return;
			
			//If not using a stick return.
			if (!player.getItemInHand().getType().equals(Material.STICK))
				return;
			
			//Check what they are selecting from hashmap.
			if (isSelectingFirst.containsKey(player))
				isFirst = isSelectingFirst.get(player);
			else
				isSelectingFirst.put(player, false);
			
			if (isFirst == false)
			{
				isSelectingFirst.put(player, true);
				block1Location.put(player, event.getClickedBlock().getLocation());
				msgLib.standardMessage("Successfully selected first point.");
			}
			else
			{
				isSelectingFirst.put(player, false);
				block2Location.put(player, event.getClickedBlock().getLocation());
				msgLib.standardMessage("Successfully selected second point.");
			}
		}
	}
}









