package me.Destro168.FC_Announcer;

import me.Destro168.FC_Announcer.AutoUpdate;
import me.Destro168.FC_Announcer.SelectionVector;

import org.bukkit.Bukkit;
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
	public static SettingsManager settingsManager;
	public static SelectionVector sv;
    public static FC_Announcer plugin;
    
    private AnnouncerCE announcerCE;
    
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
		settingsManager = new SettingsManager();
		sv = new SelectionVector();
		
		//Create listener for /announcer
		announcerCE = new AnnouncerCE();
		getCommand("Announcer").setExecutor(announcerCE);
		
		//Add listeners
		getServer().getPluginManager().registerEvents(new PlayerInteractionListener(), this);
		
		//Delay start by 1 second to give breathing room for permissions.
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable()
		{
			public void run()
			{
				//Perform a reload
				settingsManager.reload();
			}
		}, 20);
		
		try {
			new AutoUpdate(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
			ap = new FC_AnnouncerPermissions(player);
			
			//if they don't have permission return;
			if (!ap.isAdmin())
				return;
			
			//If not using a stick return.
			if (!player.getItemInHand().getType().equals(FC_Announcer.settingsManager.getZoneSelectionMaterial()))
				return;
			
			if (event.getAction() == Action.LEFT_CLICK_BLOCK)
			{
				sv.selectNewPoint(player, event.getClickedBlock().getLocation(), true);
				event.setCancelled(true);
			}
			else if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				sv.selectNewPoint(player, event.getClickedBlock().getLocation(), false);
				event.setCancelled(true);
			}
		}
	}
		
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
            Player player = (Player) sender;
            if(player.hasPermission("reload.reload"));
            reloadConfig();
            player.sendMessage(ChatColor.GREEN + "Configuration Reloaded!");
            return false;
	}
}









