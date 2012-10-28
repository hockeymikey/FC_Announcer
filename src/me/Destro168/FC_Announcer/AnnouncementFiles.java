package me.Destro168.FC_Announcer;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

public class AnnouncementFiles 
{
	private int activeAnnouncementCount;
	private FileConfiguration config;
	private final String announcerPrefix = "Announcement.";
	protected FC_Announcer plugin;
	protected final int LINE_GROUP_CAP = 1000;
	
	//Gets
	protected int getActiveAnnouncementCount() { return activeAnnouncementCount; }
	protected String getLine(int x, int y) { config = plugin.getConfig(); return config.getString(announcerPrefix + x + "." + y); }
	protected int getInterval(int x) { config = plugin.getConfig(); return config.getInt(announcerPrefix + x + ".interval"); }
	protected boolean getIsActive(int x) { config = plugin.getConfig(); return config.getBoolean(announcerPrefix + x + ".isActive"); }
	protected boolean getIsCreated(int x) 
	{
		config = plugin.getConfig();
		try { return config.getBoolean(announcerPrefix + x + ".isCreated"); }
		catch (NullPointerException e) { return false; }
	}
	protected boolean getPickRandomLines(int x) { config = plugin.getConfig(); return config.getBoolean(announcerPrefix + x + ".pickRandomLines"); }
	protected List<String> getWorlds(int x) { config = plugin.getConfig(); return config.getStringList(announcerPrefix + x + ".worlds"); }
	protected double getX1(int group) { config = plugin.getConfig(); return config.getInt(announcerPrefix + group + ".x1"); }
	protected double getY1(int group) { config = plugin.getConfig(); return config.getInt(announcerPrefix + group + ".y1"); }
	protected double getZ1(int group) { config = plugin.getConfig(); return config.getInt(announcerPrefix + group + ".z1"); }
	protected double getX2(int group) { config = plugin.getConfig(); return config.getInt(announcerPrefix + group + ".x2"); }
	protected double getY2(int group) { config = plugin.getConfig(); return config.getInt(announcerPrefix + group + ".y2"); }
	protected double getZ2(int group) { config = plugin.getConfig(); return config.getInt(announcerPrefix + group + ".z2"); }
	
	//Sets
	protected void setLine(int x, int y, String z) { config = plugin.getConfig(); config.set(announcerPrefix + x + "." + y, z); plugin.saveConfig(); rearrangeAnnouncements(x); }
	protected void setInterval(int x, int y) { config = plugin.getConfig(); config.set(announcerPrefix + x + ".interval", y); plugin.saveConfig(); }
	protected void setIsActive(int x, boolean y) { config = plugin.getConfig(); config.set(announcerPrefix + x + ".isActive", y); plugin.saveConfig(); }
	protected void setPickRandomLines(int x, boolean y) { config = plugin.getConfig(); config.set(announcerPrefix + x + ".pickRandomLines", y); plugin.saveConfig(); }
	protected void setIsCreated(int x, boolean y) { config = plugin.getConfig(); config.set(announcerPrefix + x + ".isCreated", y); plugin.saveConfig(); }
	protected void setWorlds(int x, List<String> y) { config = plugin.getConfig(); config.set(announcerPrefix + x + ".worlds", y); plugin.saveConfig(); }
	protected void setX1(int x, int y) { config = plugin.getConfig(); config.set(announcerPrefix + x + ".x1", y); plugin.saveConfig(); }
	protected void setY1(int x, int y) { config = plugin.getConfig(); config.set(announcerPrefix + x + ".y1", y); plugin.saveConfig(); }
	protected void setZ1(int x, int y) { config = plugin.getConfig(); config.set(announcerPrefix + x + ".z1", y); plugin.saveConfig(); }
	protected void setX2(int x, int y) { config = plugin.getConfig(); config.set(announcerPrefix + x + ".x2", y); plugin.saveConfig(); }
	protected void setY2(int x, int y) { config = plugin.getConfig(); config.set(announcerPrefix + x + ".y2", y); plugin.saveConfig(); }
	protected void setZ2(int x, int y) { config = plugin.getConfig(); config.set(announcerPrefix + x + ".z2", y); plugin.saveConfig(); }
	
	protected void clearAnnouncement(int x) { config = plugin.getConfig(); config.set((announcerPrefix + x), null); plugin.saveConfig(); }
	
	public void announcerFiles()
	{
		//Housekeeping
		plugin = FC_Announcer.plugin;
		
		//Rearrange announcements.
		rearrangeAnnouncements(0);
	}
	
	//Move an announcement as well as all of it's line to another position
	public void moveAnnouncement(int startPosition, int newPostion)
	{
		String old = "Announcement." + startPosition;
		String newAnnounce = "Announcement." + newPostion;
		String line = "";
		
		config.set(newAnnounce + ".isCreated", config.get(old + ".isCreated"));
		config.set(newAnnounce + ".isActive", config.get(old + ".isActive"));
		config.set(newAnnounce + ".pickRandomLines", config.get(old + ".pickRandomLines"));
		config.set(newAnnounce + ".interval", config.get(old + ".interval"));
		
		config.set(newAnnounce + ".x1", config.get(old + ".x1"));
		config.set(newAnnounce + ".y1", config.get(old + ".y1"));
		config.set(newAnnounce + ".z1", config.get(old + ".z1"));
		config.set(newAnnounce + ".x2", config.get(old + ".x2"));
		config.set(newAnnounce + ".y2", config.get(old + ".y2"));
		config.set(newAnnounce + ".z2", config.get(old + ".z2"));
		
		//For all lines
		for (int i = 0; i < LINE_GROUP_CAP; i++)
		{
			line = config.getString(old + "." + i);
			
			//If the old announcement isn't empty or null.
			if (line != null && !isBlankString(line))
			{
				config.set(newAnnounce + "." + i, config.getString(old + "." + i));
			}
		}
		
		//Remove the whole old announcement.
		config.set(old, null);
	}
	
	public void rearrangeAnnouncements(int startingPoint)
	{
		//Variable Declarations
		String line = "";
		String line2 = "";
		
		//For all announcements in the config...
		for (int i = startingPoint; i < LINE_GROUP_CAP; i++)
		{
			//If the announcement was created...
			if (getIsCreated(i) == true)
			{
				//First we want to try to stack the lines down
				for (int j = 0; j < LINE_GROUP_CAP; j++)
				{
					line = getLine(i,j);
					
					//Find a good announcement line
					if (line != null && !isBlankString(line))
					{
						//For all announcement lines below that, if it's blank, then move to that.
						for (int k = 0; k < j; k++)
						{
							line2 = getLine(i,k);
							
							if (line2 == null || isBlankString(line2))
							{
								setLine(i,k,getLine(i,j));
								setLine(i,j,null);
								k = j;
							}
						}
					}
				}
				
				//If there are any empty spaces below the announcement group, shift the announcement down.
				for (int j = 0; j < i; j++)
				{
					if (getIsCreated(j) == false)
					{
						moveAnnouncement(i, j);
						j = i;
					}
				}
			}
		}
	}
	
	public int countActiveAnnouncements()
	{
		//Reset announcement count to 0.
		activeAnnouncementCount = 0;
		
		//Count how many active announcements there are.
		for (int i = 0; i < LINE_GROUP_CAP; i++)
		{
			if (getIsCreated(i) == true)
			{
				if (getIsActive(i) == true)
					activeAnnouncementCount++;
			}
		}
		
		//Return that count.
		return activeAnnouncementCount;
	}
	
	//Enables all non-empty announcements.
	public void autoEnable()
	{
		//Set all non-empty announcements to active
		for (int i = 0; i < LINE_GROUP_CAP; i++) 
		{
			if (getIsCreated(i) == true)
			{
				if (getIsActive(i) == false)
					setIsActive(i, true);
			}
		}
	}

	public Boolean isBlankString(String text)
	{
		if ((text.equals("")) || (text.equals("null")))
			return true;
		
		return false;
	}
}









