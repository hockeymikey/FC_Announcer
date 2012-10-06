package me.Destro168.FC_Announcer;

import java.util.List;
import java.util.Random;

import me.Destro168.ConfigManagers.ConfigManager;
import me.Destro168.FC_Suite_Shared.ColorLib;
import me.Destro168.FC_Suite_Shared.LocationInsideAreaCheck;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class AnnouncementManager
{
	//Variable Declarations
	private FileConfiguration config;
	private FC_Announcer plugin;
	private AnnouncementGroup ag;
	private int[] currentLine;
	private int[] tid;
	private boolean displayAnnouncementsInConsole;
	private String broadcastTag;
	private int announcementCount;
	
	//Functions
	public void setTaskId(int x, int y) { tid[x] = y; }
	
	public String getBroadcastTag() { return broadcastTag; }
	
	public AnnouncementGroup getAnnouncementGroup() { return ag; }
	public int getTaskId(int x) { return tid[x]; }
	
	public AnnouncementManager()
	{
		//Store plugin
		plugin = FC_Announcer.plugin;
		
		//Get config.
		config = plugin.getConfig();
		
		//Create the new announcement group.
		ag = new AnnouncementGroup();
		
		//Handle the configuration.
		handleConfiguration();
		
		//Get the display announcements in console variable.
		displayAnnouncementsInConsole = config.getBoolean("Setting.displayAnnouncementsInConsole");
		
		//Get config manager.
		ConfigManager cm = new ConfigManager();
		
		//Set broadcast tag.
		broadcastTag = cm.broadcastTag;
	}
	
	public void reload()
	{
		//Handle AutoAnnounce if the option is enabled.
		if (config.getBoolean("Setting.autoEnable") == true)
			ag.autoEnable();
		
		//Count active announcements.
		announcementCount = ag.countActiveAnnouncements();
		
		currentLine = new int[announcementCount];
		tid = new int[announcementCount];
		
		for (int i = 0; i < announcementCount; i++)
		{
			currentLine[i] = 0;
			tid[i] = 0;
		}
		
		//Cancel all announcements.
		Bukkit.getScheduler().cancelTasks(plugin);
		
		//Start announcing announcements.
		beginAnnouncing();
	}
	
	//Starts announcements that are stored as "enabled" in the config.
	public void beginAnnouncing()
	{
		//Get configuration file
		config = plugin.getConfig();
		
		//If there is one announcement, then announce
		if (announcementCount > -1)
		{
			//For all announcements that are enabled, create the processes and begin them for all groups.
			for (int i = 0; i < announcementCount; i++)
			{
				if (ag.getIsActive(i) == true)
				{
					tid[i] = startAnnouncement(tid[i], i);
				}
			}
		}
		else
		{
			plugin.getLogger().info("There are no announcements to display.");
		}
	}
	
	//Starts a new announcement task.
	public int startAnnouncement(int taskID, int group)
	{
		//Get configuration file
		config = plugin.getConfig();
		
		//Create a final group to be broadcast.
		final int currentGroup = group;
		
		//Cancel any past tasks.
		Bukkit.getScheduler().cancelTask(taskID);
		
		//We update the line count for the group.
		ag.updateLineCount(group);
		
		//Create the task and store the task id.
		taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
		{	
			@Override
			public void run() 
			{
				broadcastMessage(currentGroup);
			}
		}, 0, ag.getInterval(group) * 20);
		
		//returns the taskID
		return taskID;
	}
	
	public void broadcastMessage(int group) 
	{	
		//Get configuration file
		config = plugin.getConfig();
		
		//Variables
		String line;
		Location boundOne;
		Location boundTwo;
		LocationInsideAreaCheck liac;
		ColorLib colorLib = new ColorLib();
		FC_AnnouncerPermissions perms;
		Random rand = new Random();
		
		//Set our line based on random order or fixed order.
		if (ag.getPickRandomLines(group) == true)
			line = ag.getLine(group, rand.nextInt(ag.getLineCount()));	//Random order
		else
			line = ag.getLine(group, currentLine[group]);	//Fixed order.
		
		//If our line is blank, then...
		if (line == null || line.equals(""))
		{
			//Set the current group for the announcement group to 0.
			currentLine[group] = 0;
			
			//Update line
			line = ag.getLine(group, currentLine[group]);
		}
		
		if (displayAnnouncementsInConsole == true)
			plugin.getLogger().info("[" + String.valueOf(group) + "]: " + line);
		
		//If the line is equal to null still, that means no announcements, so discontinue execution.
		if (line == null)
		{
			plugin.getLogger().info("No announcements, please disable all groups and autoenable if you don't want to see this message. Or add announcements :)");
		}
		else
		{
			//Increase the current line for the group.
			currentLine[group]++;
			
			//Set colors.
			line = colorLib.parseColors(broadcastTag + line);
			
			//Message the announcement to all players.
			for (Player player: plugin.getServer().getOnlinePlayers()) 
		    {
				perms = new FC_AnnouncerPermissions(player);
				
				//Check if they have the view permission.
				if (perms.viewGroup(group) == true)
				{
					//If the player has the ignore permission, then we want to not show them it.
					if (perms.ignoresGroup(group) == false)
					{
						//Make sure that it is in proper zone.
						
						//Create two locations to store the zone bounds.
						boundOne = new Location(player.getWorld(), ag.getX1(group), ag.getY1(group), ag.getZ1(group));
						boundTwo = new Location(player.getWorld(), ag.getX2(group), ag.getY2(group), ag.getZ2(group));
						
						liac = new LocationInsideAreaCheck(player.getLocation(), boundOne, boundTwo);
						
						//If the player is inside the area, then...
						if (liac.getIsInside() == true)
						{
							List<String> worlds = ag.getWorlds(group);
							String worldName = player.getWorld().getName();
							
							//Check to make sure the player is in a valid world.
							for (int i = 0; i < worlds.size(); i++)
							{
								if (worlds.get(i).equals(worldName))
								{
									player.sendMessage(line);
									i = worlds.size();
								}
								else if (worlds.get(i).equals("default"))
								{
									player.sendMessage(line);
									i = worlds.size();
								}
							}
						}
					}
				}
		    }
		}
	}
	
	//Basically creates default settings for when the plugin first runs.
	public void handleConfiguration()
	{
		//Get configuration file
		FileConfiguration config = plugin.getConfig();
		
		//Update config files to 4.0
		if (config.getDouble("Version") < 4.0)
		{
			//Header for configuration file
			config.options().header("These are configuration variables");
			
			//Set the new version
			config.set("Version", 4.0);
			
			//Set default broadcaster tag.
			config.set("Setting.BroadcastTag", "&b[&3FC_Announcer&b]&f ");
			
			//Enable the feature automatic enable.
			config.set("Setting.autoEnable", true);
			
			//Set announcements to be displayed in the console.
			config.set("Setting.displayAnnouncementsInConsole", true);
			
			//Remove and update outdated settings if the old setting broadcastTag exists.
			if (config.getString("BroadcastTag") != null)
			{
				//Transfer settings to new settings
				config.set("Setting.BroadcastTag", config.getString("BroadcastTag"));
				config.set("Setting.autoEnable", config.getBoolean("autoEnable"));
				config.set("Setting.displayAnnouncementsInConsole", config.getBoolean("displayAnnouncementsInConsole"));
				
				//Set old settings to null
				config.set("configCreated", null);
				config.set("version", null);
				config.set("BroadcastTag", null);
				config.set("autoEnable", null);
				config.set("displayAnnouncementsInConsole", null);
				
				//Remove total lines from all announcement groups.
				for (int i = 0; i < 1000; i++)
				{
					if (config.getBoolean("Announcement." + i + ".isCreated") == true)
						config.set("Announcement." + i + ".totalLines", null);
				}
			}
			
			//Create a new announcement if there is no announcement at 0,0
			if (config.getString("Announcement." + 0 + "." + 0) == null)
				ag.createNewAnnouncement(0, 0, "&6This is the default FC_Announcer announcement! Type &e/announcer&6 for help!");
		}
		
		//Upgrade to 5.2
		if (config.getDouble("Version") < 5.2)
		{
			//Set the new version
			config.set("Version", 5.2);
			
			//Remove old broadcast tag.
			config.set("Setting.BroadcastTag", null);
		}
		
		//Save config
		plugin.saveConfig();
	}
}
