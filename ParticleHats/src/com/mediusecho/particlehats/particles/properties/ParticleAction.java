package com.mediusecho.particlehats.particles.properties;

import java.util.HashMap;
import java.util.Map;

import com.mediusecho.particlehats.locale.Message;

public enum ParticleAction {

	EQUIP                (0, false),
	TOGGLE               (1, false),
	CLOSE                (2, false),
	DUMMY                (3, false),
	OVERRIDE             (4, false),
	CLEAR                (5, false),
	COMMAND              (6, true),
	OPEN_MENU            (7, true),
	OPEN_MENU_PERMISSION (8, true),
	PURCHASE_CONFIRM     (9, false),
	PURCHASE_DENY        (10, false),
	PURCHASE_ITEM        (11, false),
	MIMIC                (12, false),
	DEMO                 (13, true);
	
	private final int id;
	private final boolean hasData;
	private static final Map<Integer, ParticleAction> actionID = new HashMap<Integer, ParticleAction>();
	
	static
	{
		for (ParticleAction pa : values()) {
			actionID.put(pa.id, pa);
		}
	}
	
	private ParticleAction (final int id, final boolean hasData)
	{
		this.id = id;
		this.hasData = hasData;
	}
	
	/**
	 * Get this ParticleActions id
	 * @return
	 */
	public int getID () {
		return id;
	}
	
	/**
	 * Returns true if this action relies on additional data
	 * @return
	 */
	public boolean hasData () {
		return hasData;
	}
	
	/**
	 * Get the name of this ParticleAction
	 * @return The name of this action as defined in the current messages.yml file
	 */
	public String getDisplayName () 
	{
		final String key = "ACTION_" + toString() + "_NAME";
		try {
			return Message.valueOf(key).getValue();
		} catch (IllegalArgumentException e) {
			return "";
		}
	}
	
	/**
	 * Get the description of this ParticleAction
	 * @return The description of this action as defined in the current messages.yml file
	 */
	public String getDescription ()
	{
		final String key = "ACTION_" + toString() + "_DESCRIPTION";
		try {
			return Message.valueOf(key).getValue();
		} catch (IllegalArgumentException e) {
			return "";
		}
	}
	
	/**
	 * Returns the ParticleAction associated with this id
	 * @param id
	 * @return
	 */
	public static ParticleAction fromId (int id) 
	{
		if (actionID.containsKey(id)) {
			return actionID.get(id);
		}
		return EQUIP;
	}
}