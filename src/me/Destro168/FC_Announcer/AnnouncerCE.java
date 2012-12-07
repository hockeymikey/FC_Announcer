package me.Destro168.FC_Announcer;

import java.util.ArrayList;
import java.util.List;

import me.Destro168.FC_Suite_Shared.ArgParser;
import me.Destro168.FC_Suite_Shared.ColorLib;
import me.Destro168.FC_Suite_Shared.SuiteConfig;
import me.Destro168.FC_Suite_Shared.Messaging.BroadcastLib;
import me.Destro168.FC_Suite_Shared.Messaging.MessageLib;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.command.ColouredConsoleSender;
import org.bukkit.entity.Player;

public class AnnouncerCE implements CommandExecutor
{
	private FC_Announcer plugin;
	private MessageLib msgLib;
	private ColouredConsoleSender console;
	private Player player;
	
	public AnnouncerCE() { }
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args_)
    {
		//Variable Declarations
		plugin = FC_Announcer.plugin;
		boolean success = true;
		ArgParser fap = new ArgParser(args_);
		String arg0 = fap.getArg(0);
		String arg1 = fap.getArg(1);
		String arg2 = fap.getArg(2);
		
		if (sender instanceof Player)
		{
			player = (Player) sender;
			console = null;
			msgLib = new MessageLib(player);
		}
		else if (sender instanceof ColouredConsoleSender)
		{
			player = null;
			console = (ColouredConsoleSender) sender;
			msgLib = new MessageLib(console);
		}
		else
		{
			FC_Announcer.plugin.getLogger().info("Unknown command sender, returning announcer command.");
			return false;
		}
		
		//Check 1 argument commands.
		if (arg0.equalsIgnoreCase("list"))
		{
			if (arg1.equals("")) 
				commandList("groups");
			else 
				commandList(arg1);

			return true;
		}
		
		if (arg0.equalsIgnoreCase("vert"))
		{
			if (player != null)
				return FC_Announcer.sv.expandVert(player);
			else
				return msgLib.standardMessage("This command can only be executed in-game.");
		}
		
		//Check 2 argument commands.
		if (arg1.equals(""))
		{
			commandHelp();
			return true;
		}
		else
		{ //else if (args[0].equalsIgnoreCase("deletaall")) success = commandDeleteAll(args[1]);
			if (arg0.equalsIgnoreCase("off") || arg0.equalsIgnoreCase("disable")) success = commandOff(arg1);
			else if (arg0.equalsIgnoreCase("on") || arg0.equalsIgnoreCase("enable")) success = commandOn(arg1);
			else if (arg0.equalsIgnoreCase("world") || arg0.equalsIgnoreCase("worlds")) success = commandWorld(fap);
			else if (arg0.equalsIgnoreCase("set") || arg0.equalsIgnoreCase("create")) success = commandSet(fap);
			else if (arg0.equalsIgnoreCase("delete") || arg0.equalsIgnoreCase("remove")) success = commandDelete(arg1, arg2);
			else if (arg0.equalsIgnoreCase("interval") || arg0.equalsIgnoreCase("delay")) success = commandInterval(arg1, arg2);
			else if (arg0.equalsIgnoreCase("zone"))
			{
				if (player != null)
					success = commandZone(arg1, player);
				else
					return msgLib.standardMessage("This command can only be executed in-game.");
			}
			else if (arg0.equalsIgnoreCase("help")) commandHelp();
			else if (!arg0.equals("") && !arg1.equals("")) success = commandAnnounce(arg0, arg1);	//Check to see if they are using force command.
			else success = false;
		}
		
		//Give success or failure feedback
		if (success == false)
			return msgLib.errorInvalidCommand();
			
		return true;
	}

	private boolean commandAnnounce(String arg0, String arg1) 
	{
		//Variable declarations
		int intArg1 = 0;
		int intArg2 = 0;
		BroadcastLib bLib = new BroadcastLib();
		
		//We attempt to format the string to numbers.
		try
		{
			intArg1 = Integer.valueOf(arg0);
			intArg2 = Integer.valueOf(arg1);
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		
		//We check if it is created first.
		if (FC_Announcer.settingsManager.getAnnouncementGroup().getIsCreated(intArg1) == false)
			return false;
		
		
		//We make sure the line isn't null before announcing.
		if (FC_Announcer.settingsManager.getAnnouncementGroup().getLine(intArg1, intArg2) != null)
			bLib.standardBroadcast(FC_Announcer.settingsManager.getAnnouncementGroup().getLine(intArg1, intArg2));
		else
			return false;
		
		return true;
	}

	//For command /announcer off [group]
	public boolean commandOff(String group)
	{
		//Variables
		int inputGroup;
		
		try
		{
			inputGroup = Integer.valueOf(group);
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		
		//First process the command if the user entered all.
		if (group.equalsIgnoreCase("all")) 
		{
			//Cancel all current announcements.
			Bukkit.getScheduler().cancelTasks(plugin);
			
			//For all groups, iterate through as i.
			for (int i = 0; i < FC_Announcer.settingsManager.getAnnouncementGroup().getActiveAnnouncementCount(); i++)
			{	
				//Set the group to no longer recieve announcements.
				FC_Announcer.settingsManager.getAnnouncementGroup().setIsActive(i, false);
			}
		}
		else 
		{
			//If the announcement is active
			if (FC_Announcer.settingsManager.getAnnouncementGroup().getIsActive(inputGroup) == true)
			{
				//Cancel the announcement task.
				Bukkit.getServer().getScheduler().cancelTask(FC_Announcer.settingsManager.getTaskId(inputGroup));
				
				//Set the group to no longer recieve announcements. 
				FC_Announcer.settingsManager.getAnnouncementGroup().setIsActive(inputGroup, false);
			}
			else
				return false;
		}
		
		return msgLib.successCommand();
	}
	
	//For command /announcer on [group]
	public boolean commandOn(String group)
	{
		//Variables
		int inputGroup = 0;
		int newAnnouncementTaskId = 0;
		
		try
		{
			inputGroup = Integer.valueOf(group);
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		
		//First process the command if the user entered all.
		if (group.equalsIgnoreCase("all"))
		{
			//For all groups, iterate through as i.
			for (int i = 0; i < FC_Announcer.settingsManager.getAnnouncementGroup().getActiveAnnouncementCount(); i++)
			{	
				//Set the group to recieve announcements.
				FC_Announcer.settingsManager.getAnnouncementGroup().setIsActive(i, true);

				//Start all announcements for all groups.
				FC_Announcer.settingsManager.setTaskId(i, FC_Announcer.settingsManager.startAnnouncement(FC_Announcer.settingsManager.getTaskId(i), i));
			}
		}
		else
		{
			//If the announcement is inactive
			if (FC_Announcer.settingsManager.getAnnouncementGroup().getIsActive(inputGroup) == false)
			{
				//Set the group to recieve announcements.
				FC_Announcer.settingsManager.getAnnouncementGroup().setIsActive(inputGroup, true);
				
				//Start announcement.
				newAnnouncementTaskId = FC_Announcer.settingsManager.startAnnouncement(FC_Announcer.settingsManager.getTaskId(inputGroup), inputGroup);
				
				//Store task id.
				FC_Announcer.settingsManager.setTaskId(inputGroup, newAnnouncementTaskId);
			}
			else
			{
				return false;
			}
		}
		
		return msgLib.successCommand();
	}
	
	//If the command sent is /announcer set [group] [line #] [msg]
	public boolean commandSet(ArgParser args)
	{
		//Variables
		String message = "";
		int group;
		int line;
		
		//Return false if any of the following fields are null.
		if (args.getArg(1).equals(""))
			return false;
		else if (args.getArg(2).equals(""))
			return false;
		else if (args.getArg(3).equals(""))
			return false;
		
		//Attempt to store two arguments as numbers.
		try 
		{
			group = Integer.parseInt(args.getArg(1));
			line = Integer.parseInt(args.getArg(2));
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		
		//We want to form a message based on all arguments pass the first argument.
		args.setLastArg(3);
		
		//Set the message
		message = args.getFinalArg();
		
		//Set the announcement line to the one specified.
		if (FC_Announcer.settingsManager.getAnnouncementGroup().setLine(message, group, line) == true)
			msgLib.successCommand();	//Give the player feedback
		else
		{
			msgLib.standardMessage("Command failed becaues [line] or [group] was out of range.");
			return true;
		}
		
		//Reload announcements.
		FC_Announcer.settingsManager.reload();
		
		return true;
	}
	
	public boolean commandWorld(ArgParser args)
	{
		List<String> worlds = new ArrayList<String>();
		int intGroup = 0;
		
		//Attempt to convert the group to a number.
		try
		{
			intGroup = Integer.valueOf(args.getArg(1));
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		
		//Add to the worlds list all new worlds specified
		for (int i = 2; i < args.getArgs().length; i++)
		{
			if (!args.getArg(i).equals(""))
			{
				worlds.add(args.getArg(i));
			}
		}
		
		//Store the worlds list again.
		FC_Announcer.settingsManager.getAnnouncementGroup().setWorlds(intGroup, worlds);
		
		//Return success.
		return msgLib.successCommand();
	}
	
	//If the command sent is /announcer deleteall
	public boolean commandDeleteAll()
	{
		//Clear all announcement groups.
		for (int i = 0; i < FC_Announcer.settingsManager.getAnnouncementGroup().getActiveAnnouncementCount(); i++) 
			FC_Announcer.settingsManager.getAnnouncementGroup().clearAnnouncement(i);

		return true;
	}
	
	//Command: /announcer delete [group] [line #]
	public boolean commandDelete(String group, String line)
	{
		//Delete specified announcement and line
		try
		{
			FC_Announcer.settingsManager.getAnnouncementGroup().clearLine(Integer.parseInt(group), Integer.parseInt(line));
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		
		//Restart the announcements
		FC_Announcer.settingsManager.reload();
		
		//Send feedback
		return msgLib.successCommand();
	}
	
	//Command: /announcer list [group]
	public void commandList(String group)
	{
		//Variables
		String message = "";
		String line = "";
		int listCounter = 0;
		int intGroup;
		ColorLib colorlib = new ColorLib();
		SuiteConfig cm = new SuiteConfig();
		
		//If the user enters in "groups", then display all groups that are active.
		if (group.equals("groups"))
		{
			msgLib.standardHeader("Announcement Groups");
			
			//Display all announcements
			for (int i = 0; i != FC_Announcer.settingsManager.getAnnouncementGroup().getActiveAnnouncementCount(); i++) 
			{
				//Add the group number.
				message = "Group " + cm.primaryColor + "[" + cm.secondaryColor + String.valueOf(i) + cm.primaryColor + "/" + cm.secondaryColor;
				
				//Add whether active or not.
				if (FC_Announcer.settingsManager.getAnnouncementGroup().getIsActive(i) == true)
					message = message + "Enabled";
				else
					message = message + "Disabled (inactive)";
				
				//Add the interval.
				message = message + cm.primaryColor + "] - Interval [" + cm.secondaryColor + FC_Announcer.settingsManager.getAnnouncementGroup().getInterval(i) + cm.primaryColor + "]";
				
				//Add the worlds
				message = message + cm.primaryColor + " - Worlds ";
				
				for (String worldName : FC_Announcer.settingsManager.getAnnouncementGroup().getWorlds(i))
					message = message + "[" + cm.secondaryColor + worldName + cm.primaryColor + "] ";
				
				//Send the message
				msgLib.standardMessage(message);
			}
		}
		
		else
		{
			msgLib.standardHeader("Announcement Lines");
			
			//Attempt to store group.
			try
			{
				intGroup = Integer.parseInt(group);
				
				//Make sure input is in range.
				if ((intGroup > FC_Announcer.settingsManager.getAnnouncementGroup().LINE_GROUP_CAP) || (intGroup < 0))
					return;
			}
			catch (NumberFormatException e)
			{
				group = "0";
				intGroup = 0;
			}
			
			//Display all announcements
			for (int i = 0; i != FC_Announcer.settingsManager.getAnnouncementGroup().LINE_GROUP_CAP; i++) 
			{
				//Store announcement in "message" and then send it to the player.
				line = FC_Announcer.settingsManager.getAnnouncementGroup().getLine(intGroup, i);
				
				//Do a check to make sure the announcement actually has words before listing.
				if ((line != null) && !(line.equals("")) && !(line.equals("null")))
				{
					//Add all the prefixy stuff.
					message = "Line #" + cm.secondaryColor + String.valueOf(i) + cm.primaryColor + " - [" + cm.secondaryColor + "";
					
					if (FC_Announcer.settingsManager.getAnnouncementGroup().getIsActive(intGroup) == true)
						message += "On";
					else
						message += "Off";
					
					message += cm.primaryColor + "] [" + cm.secondaryColor + FC_Announcer.settingsManager.getAnnouncementGroup().getInterval(intGroup) +
							cm.primaryColor + "] - " + cm.secondaryColor + colorlib.parse(line);
					
					msgLib.standardMessage(message);
					
					listCounter++;
				}
			}
			
			//If no announcements none were listed, tell the player. This is ensure a given response if the player uses the command.
			if (listCounter == 0) {	msgLib.standardMessage("There are currently no announcements stored in this group."); }
		}
	}
	
	//Command: /announcer interval [group] [interval]
	public boolean commandInterval(String group, String interval)
	{
		//Variables
		int intGroup;
		int intInterval;
		
		//Attempt to store input as numbers
		try
		{
			intGroup = Integer.parseInt(group);
			intInterval = Integer.parseInt(interval);
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		
		//Make sure input is in range.
		if ((intGroup > FC_Announcer.settingsManager.getAnnouncementGroup().LINE_GROUP_CAP) || (intGroup < 0))
			return false;
		
		if (intInterval > 0)
		{
			FC_Announcer.settingsManager.getAnnouncementGroup().setInterval(intGroup, intInterval);
			
			//Send feedback
			msgLib.successCommand();
			
			FC_Announcer.settingsManager.reload();
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private boolean commandZone(String group, Player player)
	{
		//Variable Declarations
		int inputGroup;
		Location loc1;
		Location loc2;
		
		try
		{
			inputGroup = Integer.valueOf(group);
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		
		loc1 = FC_Announcer.sv.getBlockLoc1(player);
		loc2 = FC_Announcer.sv.getBlockLoc2(player);
		
		if (loc1 == null || loc2 == null)
			return msgLib.errorInvalidSelection();
		
		FC_Announcer.settingsManager.getAnnouncementGroup().setAnnouncementZone(inputGroup,
			(int) loc1.getX(),
			(int) loc1.getY(),
			(int) loc1.getZ(),
			(int) loc2.getX(),
			(int) loc2.getY(),
			(int) loc2.getZ()
		);
		
		return msgLib.successCommand();
	}
	
	//If the player sends /announcer help, display all commands.
	public boolean commandHelp()
	{
		//Send help messages to the player
		msgLib.standardHeader("Help for FC_Announcer!");
		msgLib.standardMessage("/announcer delete [group] [line]","Delete A Groups Line.");
		msgLib.standardMessage("/announcer list [groups, group number]:");
		msgLib.secondaryMessage("- List Groups OR Lines Of A Group");
		msgLib.standardMessage("/announcer set [group] [line] [msg1] [msg2] [...]:");
		msgLib.secondaryMessage("- Sets Announcement For Group At Specified Line.");
		msgLib.standardMessage("/announcer worlds [group] [world_name] [...]:");
		msgLib.secondaryMessage("- Set Worlds For An Announcement To Display.");
		msgLib.standardMessage("/announcer interval [group] [interval in seconds]:");
		msgLib.secondaryMessage("- Change How Often An Announcement Occurs.");
		msgLib.standardMessage("/announcer zone [group]");
		msgLib.secondaryMessage("- Use A Stick And Select Two Points To Define A Zone.");
		msgLib.standardMessage("/announcer vert","Expands Zone Selection From Sky To Bedrock.");
		msgLib.standardMessage("/announcer on [group]","Disables Group.");
		msgLib.standardMessage("/announcer off [group]","Enables Group.");
		msgLib.standardMessage("/announcer [group] [line]","Forces Announcement.");
		
		return true;
	}
}
