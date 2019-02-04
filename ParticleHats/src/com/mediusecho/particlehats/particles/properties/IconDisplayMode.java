package com.mediusecho.particlehats.particles.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.mediusecho.particlehats.locale.Message;

public enum IconDisplayMode {

	DISPLAY_IN_ORDER      (0),
	DISPLAY_RANDOMLY      (1);
	
	private final int id;
	private static final Map<Integer, IconDisplayMode> modeID = new HashMap<Integer, IconDisplayMode>();
	
	static
	{
		for (IconDisplayMode dm : values()) {
			modeID.put(dm.id, dm);
		}
	}
	
	private IconDisplayMode (final int id)
	{
		this.id = id;
	}
	
	public int getID () {
		return id;
	}
	
	/**
	 * Get the name of this IconDisplayMode
	 * @return The name of this mode as defined in the current messages.yml file
	 */
	public String getDisplayName () 
	{
		final String key = "DISPLAY_MODE_" + toString() + "_NAME";
		try {
			return Message.valueOf(key).getValue();
		} catch (IllegalArgumentException e) {
			return "";
		}
	}
	
	/**
	 * Get the description of this IconDisplayMode
	 * @return The description of this mode as defined in the current messages.yml file
	 */
	public String getDescription ()
	{
		final String key = "DISPLAY_MODE_" + toString() + "_DESCRIPTION";
		try {
			return Message.valueOf(key).getValue();
		} catch (IllegalArgumentException e) {
			return "";
		}
	}
	
	/**
	 * Returns the IconDisplayMode associated with this id
	 * @param id
	 * @return
	 */
	public static IconDisplayMode fromId (int id) 
	{
		for (Entry<Integer, IconDisplayMode> entry : modeID.entrySet()) {
			if (entry.getKey() != id) {
				continue;
			}
			return entry.getValue();
		}
		return DISPLAY_IN_ORDER;
	}
}