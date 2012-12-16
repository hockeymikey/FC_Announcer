package me.Destro168.FC_Announcer;

import java.util.List;

import me.Destro168.FC_Suite_Shared.ConfigManagers.FileConfigurationWrapper;

public class AnnouncementFiles
{
	public final int LINE_GROUP_CAP = 1000;
	
	private int activeAnnouncementCount;
	private final String announcerPrefix = "Announcement.";
	public FC_Announcer plugin;
	public FileConfigurationWrapper fcw;
	
	//Gets
	public int getActiveAnnouncementCount() { return activeAnnouncementCount; }
	
	public String getLine(int x, int y) { return fcw.getString(announcerPrefix + x + "." + y); }
	
	public int getInterval(int x) { return fcw.getInt(announcerPrefix + x + ".interval"); }
	public boolean getIsActive(int x) { return fcw.getBoolean(announcerPrefix + x + ".isActive"); }
	public boolean getIsCreated(int x) { try { return fcw.getBoolean(announcerPrefix + x + ".isCreated"); } catch (NullPointerException e) { return false; } }
	public boolean getPickRandomLines(int x) { return fcw.getBoolean(announcerPrefix + x + ".pickRandomLines"); }
	public List<String> getWorlds(int x) { return fcw.getStringList(announcerPrefix + x + ".worlds"); }
	public double getX1(int group) { return fcw.getInt(announcerPrefix + group + ".x1"); }
	public double getY1(int group) { return fcw.getInt(announcerPrefix + group + ".y1"); }
	public double getZ1(int group) { return fcw.getInt(announcerPrefix + group + ".z1"); }
	public double getX2(int group) { return fcw.getInt(announcerPrefix + group + ".x2"); }
	public double getY2(int group) { return fcw.getInt(announcerPrefix + group + ".y2"); }
	public double getZ2(int group) { return fcw.getInt(announcerPrefix + group + ".z2"); }
	
	//Sets
	public void setLine(int x, int y, String z) { fcw.set(announcerPrefix + x + "." + y, z); rearrangeAnnouncements(x); }
	public void setInterval(int x, int y) { fcw.set(announcerPrefix + x + ".interval", y); }
	public void setIsActive(int x, boolean y) { fcw.set(announcerPrefix + x + ".isActive", y); }
	public void setPickRandomLines(int x, boolean y) { fcw.set(announcerPrefix + x + ".pickRandomLines", y); }
	public void setIsCreated(int x, boolean y) { fcw.set(announcerPrefix + x + ".isCreated", y); }
	public void setWorlds(int x, List<String> y) { fcw.setList(announcerPrefix + x + ".worlds", y); }
	public void setX1(int x, int y) { fcw.set(announcerPrefix + x + ".x1", y); }
	public void setY1(int x, int y) { fcw.set(announcerPrefix + x + ".y1", y); }
	public void setZ1(int x, int y) { fcw.set(announcerPrefix + x + ".z1", y); }
	public void setX2(int x, int y) { fcw.set(announcerPrefix + x + ".x2", y); }
	public void setY2(int x, int y) { fcw.set(announcerPrefix + x + ".y2", y); }
	public void setZ2(int x, int y) { fcw.set(announcerPrefix + x + ".z2", y); }
	
	public void clearAnnouncement(int x) { fcw.set((announcerPrefix + x), null); }
	
	public AnnouncementFiles()
	{
		fcw = new FileConfigurationWrapper(FC_Announcer.plugin.getDataFolder().getAbsolutePath(), "config");
		
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
		
		fcw.set(newAnnounce + ".isCreated", fcw.get(old + ".isCreated"));
		fcw.set(newAnnounce + ".isActive", fcw.get(old + ".isActive"));
		fcw.set(newAnnounce + ".pickRandomLines", fcw.get(old + ".pickRandomLines"));
		fcw.set(newAnnounce + ".interval", fcw.get(old + ".interval"));
		
		fcw.set(newAnnounce + ".x1", fcw.get(old + ".x1"));
		fcw.set(newAnnounce + ".y1", fcw.get(old + ".y1"));
		fcw.set(newAnnounce + ".z1", fcw.get(old + ".z1"));
		fcw.set(newAnnounce + ".x2", fcw.get(old + ".x2"));
		fcw.set(newAnnounce + ".y2", fcw.get(old + ".y2"));
		fcw.set(newAnnounce + ".z2", fcw.get(old + ".z2"));
		
		//For all lines
		for (int i = 0; i < LINE_GROUP_CAP; i++)
		{
			line = fcw.getString(old + "." + i);
			
			//If the old announcement isn't empty or null.
			if (line != null && !isBlankString(line))
			{
				fcw.set(newAnnounce + "." + i, fcw.getString(old + "." + i));
			}
		}
		
		//Remove the whole old announcement.
		fcw.set(old, null);
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









