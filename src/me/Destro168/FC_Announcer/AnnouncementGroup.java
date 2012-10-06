package me.Destro168.FC_Announcer;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementGroup extends AnnouncerFiles
{
	private int lineCount;
	
	public int getLineCount() { return lineCount; }
	
	public AnnouncementGroup() 
	{
		plugin = FC_Announcer.plugin;
		
		//Rearrange announcements.
		rearrangeAnnouncements(0);
	}
	
	public void createNewAnnouncement(int group, int line, String text)
	{
		//Variable declarations
		List<String> worlds = new ArrayList<String>();
		
		//Store compressed groups.
		group = getCompressedGroup(group);
		
		setIsCreated(group, true);
		setIsActive(group, true);
		setPickRandomLines(group, false);
		setInterval(group, 60);
		
		//Create a default world.
		worlds.add("default");
		
		//Store it.
		setWorlds(group, worlds);
		
		//Set coords to 0.
		setX1(group, 0);
		setY1(group, 0);
		setZ1(group, 0);
		setX2(group, 0);
		setY2(group, 0);
		setZ2(group, 0);

		//Store compressed lines.
		setLine(group,getCompressedLine(group,line,text),text);
	}
	
	private int getCompressedGroup(int group)
	{
		if (getIsCreated(group) == false)
		{
			for (int i = 0; i < LINE_GROUP_CAP; i++)
			{
				if (getIsCreated(i) == false)
				{
					group = i;
					i = LINE_GROUP_CAP;
				}
			}
		}
		
		return group;
	}
	
	private int getCompressedLine(int group, int line, String text)
	{
		//Store compressed lines.
		if (getLine(group,line) == null)
		{
			for (int i = 0; i < LINE_GROUP_CAP; i++)
			{
				if (getLine(group,i) == null)
				{
					line = i;
					i = LINE_GROUP_CAP;
				}
			}
		}
		else if (getLine(group,line).equals(""))
		{
			for (int i = 0; i < LINE_GROUP_CAP; i++)
			{
				if (getLine(group,i) == null)
				{
					line = i;
					i = LINE_GROUP_CAP;
				}
				else if (getLine(group,i).equals(""))
				{
					line = i;
					i = LINE_GROUP_CAP;
				}
			}
		}
		
		return line;
	}
	
	public void setAnnouncementZone(int group, int x1, int y1, int z1, int x2, int y2, int z2)
	{
		setX1(group, x1);
		setY1(group, y1);
		setZ1(group, z1);
		setX2(group, x2);
		setY2(group, y2);
		setZ2(group, z2);
	}
	
	public boolean clearLine(int group, int line)
	{
		//Attempt to clear a line
		try
		{
			//If the line has text, then...
			if (!getLine(group, line).equals(""))
			{
				//change the lines text
				setLine("", group, line);
				
				//We want to immediately re-order the announcements.
				rearrangeAnnouncements(group);
				
				//Check if the announcement group is empty.
				checkEmptyAnnouncementGroup(group);
			}
			
			//Update number of lines.
			updateLineCount(group);
			
			//Return true for success.
			return true;
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			return false;
		}
	}
	
	public void checkEmptyAnnouncementGroup(int group)
	{
		try
		{
			//If the announcement group has no more announcements, then clear the announcement group.
			if (getLine(group, 0) == null)
				clearAnnouncement(group);
		}
		catch (NullPointerException e)  { }
		catch (ArrayIndexOutOfBoundsException e) { }
	}
	
	//Sets a line both in memory and in the config.
	public boolean setLine(String text, int group, int line)
	{
		//Check group and line to make sure they are in the proper range of 1 to 1,000.
		if (group > LINE_GROUP_CAP || line > LINE_GROUP_CAP)
			return false;
		
		if (group < 0 || line < 0)
			return false;
		
		//If the group specified is created, then add a new line.
		if (getIsCreated(group) == true)
		{
			//Set the group.
			group = getCompressedGroup(group);
			line = getCompressedLine(group,line,text);
			
			//Edit the line
			setLine(group, line, text);
			
			//Check if all the announcements in the group are empty or not.
			if (getLine(group, 0).equals(""))
				clearAnnouncement(group);
		}
		//Else we want to make a new announcement group.
		else
			createNewAnnouncement(group, line, text);
		
		//Update number of lines.
		updateLineCount(group);
		
		return true;
	}
	
	//Returns a counter of nubmer of lines in an announcement group.
	public void updateLineCount(int group)
	{
		//Reset line count
		lineCount = 0;
		
		//Count number of lines.
		for (int i = 0; i < LINE_GROUP_CAP; i++)
		{
			if (getLine(group, i) != null)
				lineCount++;
			else	//Since announcements are ordered, as soon as we get a null we can break.
				break;
		}
	}
}









