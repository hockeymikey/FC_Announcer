package me.Destro168.FC_Announcer;

import me.Destro168.FC_Suite_Shared.PermissionManager;

import org.bukkit.entity.Player;

public class FC_AnnouncerPermissions extends PermissionManager
{
	public FC_AnnouncerPermissions(Player player_) 
	{
		super(player_);
	}
	
	public boolean isAdmin()
	{
		if (isGlobalAdmin() == true)
			return true;
		
		if (permission.has(player, "FC_Announcer.admin"))
			return true;
		
		return false;
	}
	
	public boolean viewGroup(int x)
	{
		if (permission.playerHas(player, "FC_Announcer.view." + String.valueOf(x)))
			return true;
		
		return false;
	}
	
	public boolean ignoresGroup(int x)
	{
		if (permission.playerHas(player, "FC_Announcer.ignore." + String.valueOf(x)))
			return true;
		
		return false;
	}
	
		public boolean reloadPlugin(int x)
	{
		if (permission.playerHas(player, "FC_Announcer.reload." + String.valueOf(x)))
			return true;
		
		return false;
	}
}
