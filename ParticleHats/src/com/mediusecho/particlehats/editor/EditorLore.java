package com.mediusecho.particlehats.editor;

import java.text.DecimalFormat;

import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.properties.IconDisplayMode;
import com.mediusecho.particlehats.particles.properties.ParticleAction;
import com.mediusecho.particlehats.particles.properties.ParticleLocation;
import com.mediusecho.particlehats.particles.properties.ParticleMode;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.MathUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorLore {

	private static final Core core = Core.instance;
	
	/**
	 * Applies a description to this ItemStack using ParticleLocation data
	 * @param item
	 * @param location
	 * @param description
	 */
	public static void updateLocationDescription (ItemStack item, ParticleLocation location, Message description)
	{
		if (item != null)
		{
			final int length = ParticleLocation.values().length;
			final int id = location.getID();
			
			String s = description.getValue()
					.replace("{1}", ParticleLocation.fromId(MathUtil.wrap(id - 1, length, 0)).getDisplayName())
					.replace("{2}", location.getDisplayName())
					.replace("{3}", ParticleLocation.fromId(MathUtil.wrap(id + 1, length, 0)).getDisplayName());
			ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));		
		}
	}
	
	/**
	 * Applies a description to this ItemStack using ParticleMode data
	 * @param item
	 * @param mode
	 * @param description
	 */
	public static void updateModeDescription (ItemStack item, ParticleMode mode, Message description)
	{
		if (item != null)
		{
			final int length = ParticleMode.values().length;
			final int id = mode.getID();
			
			String s = description.getValue()
					.replace("{1}", ParticleMode.fromId(MathUtil.wrap(id - 1, length, 0)).getDisplayName())
					.replace("{2}", mode.getDisplayName())
					.replace("{3}", ParticleMode.fromId(MathUtil.wrap(id + 1, length, 0)).getDisplayName())
					.replace("{4}", mode.getDescription());
			ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));		
		}
	}

	/**
	 * Applies a description to this ItemStack using IconDisplayMode data
	 * @param item
	 * @param displayMode
	 * @param description
	 */
	public static void updateDisplayModeDescription (ItemStack item, IconDisplayMode displayMode, Message description)
	{
		if (item != null)
		{
			final int length = IconDisplayMode.values().length;
			final int id = displayMode.getID();
			
			String s = description.getValue()
					.replace("{1}", IconDisplayMode.fromId(MathUtil.wrap(id - 1, length, 0)).getDisplayName())
					.replace("{2}", displayMode.getDisplayName())
					//.replace("{3}", IconDisplayMode.fromId(MathUtil.wrap(id + 1, length, 0)).getDisplayName())
					.replace("{3}", displayMode.getDescription());
			ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));	
		}
	}
	
	/**
	 * Applies a description to this ItemStack using Vector data
	 * @param item
	 * @param hat
	 * @param description
	 */
	public static void updateVectorDescription (ItemStack item, Vector vector, Message description)
	{
		if (item != null)
		{
			String s = description.getValue()
				.replace("{1}", Double.toString(vector.getX()))
				.replace("{2}", Double.toString(vector.getY()))
				.replace("{3}", Double.toString(vector.getZ()));
			ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
		}
	}
	
	/**
	 * Applies a description to this ItemStack using UpdateFrequency data (int)
	 * @param item
	 * @param updateFrequency
	 * @param description
	 */
	public static void updateFrequencyDescription (ItemStack item, int updateFrequency, Message description)
	{
		if (item != null)
		{
			String pluralSuffix = StringUtil.getParseValue(description.getValue(), "2");
			String suffix = updateFrequency > 1 && pluralSuffix != null ? pluralSuffix : "";
			String s = description.getValue()
					.replace("{1}", Integer.toString(updateFrequency))
					.replaceAll("\\{2.*\\}", suffix);
			ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
		}
	}
	
	/**
	 * Applies a description to this ItemStack using a price value
	 * @param item
	 * @param price
	 * @param description
	 */
	public static void updatePriceDescription (ItemStack item, int price, Message description)
	{
		if (item != null)
		{
			String[] priceData = StringUtil.parseValue(description.getValue(), "1");
			String c = price > 0 ?  StringUtil.escapeSpecialCharacters(SettingsManager.CURRENCY.getString()) : "";
			
			String s;
			if (priceData != null) 
			{
				s = description.getValue()
						.replace(priceData[0], price == 0 ? priceData[1] : Integer.toString(price))
						.replace("{2}", c);
			}
			
			else {
				s = description.getValue()
						.replace("{1}", Integer.toString(price))
						.replace("{2}", c);
			}
			
			ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
		}
	}
	
	/**
	 * Applies a description to this ItemStack using a duration value
	 * @param item
	 * @param duration
	 * @param description
	 */
	public static void updateDurationDescription (ItemStack item, int duration)
	{
		//"/n&8� {1} second{2=s}/n/n&3Left Click to Add 1/nRight Click to Subtract 1/n&cShift Click to Adjust by 30"
		String description = Message.EDITOR_DURATION_MENU_DESCRIPTION.getValue();
		DecimalFormat df = new DecimalFormat("#.#");
		String[] suffixInfo = StringUtil.parseValue(description, "2");
		
		double time = duration / 20D;
		if (suffixInfo != null)
		{
			String suffix = time == 1 ? "" : suffixInfo[1];
			description = description.replace(suffixInfo[0], suffix);
		}
		description = description.replace("{1}", df.format(time));
		
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(description));
	}
	
	/**
	 * Applies a description to this ItemStack using an int
	 * @param item
	 * @param speed
	 * @param description
	 */
	public static void updateIntegerDescription (ItemStack item, int value, Message description)
	{
		if (item != null)
		{
			String s = description.getValue()
					.replace("{1}", Integer.toString(value));
			ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
		}
	}
	
	/**
	 * Applies a description to this ItemStack using an int
	 * @param item
	 * @param speed
	 * @param description
	 */
	public static void updateDoubleDescription (ItemStack item, double value, Message description)
	{
		if (item != null)
		{
			String s = description.getValue()
					.replace("{1}", Double.toString(value));
			ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
		}
	}
	
	/**
	 * Applies a description to this ItemStack using Sound data
	 * @param item
	 * @param sound
	 * @param currentSound
	 * @param description
	 */
	public static void updateSoundDescription (ItemStack item, Sound sound, Sound currentSound, Message description)
	{
		String[] selectedParse = StringUtil.parseValue(description.getValue(), "1");
		if (selectedParse != null)
		{
			String selectedSuffix = (currentSound != null && currentSound.equals(sound)) ? selectedParse[1] : "";
			String s = description.getValue()
					.replace(selectedParse[0], selectedSuffix);
			ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
		}
	}
	
	/**
	 * Applies a description to this ItemStack using a ParticleAction
	 * @param item
	 * @param hat
	 * @param action
	 * @param argument
	 */
	public static void updateSpecificActionDescription (ItemStack item, Hat hat, ParticleAction action, String argument)
	{
		String description = Message.EDITOR_ACTION_OVERVIEW_MENU_ACTION_DESCRIPTION.getValue();
		String actionDescription = getParsedActionDescription(hat, action, argument);
		description = description.replace("{1}", actionDescription);
		
		String[] dataInfo = StringUtil.parseValue(description, "2");
		if (dataInfo != null) {
			description = description.replace(dataInfo[0], action.hasData() ? dataInfo[1] : "");
		}

		ItemUtil.setItemDescription(item, StringUtil.parseDescription(description));
	}
	
	/**
	 * Applies a description to this ItemStack using ParticleAction data
	 * @param item
	 * @param hat
	 */
	public static void updateGenericActionDescription (ItemStack item, Hat hat)
	{
		ParticleAction leftClickAction  = hat.getLeftClickAction();
		ParticleAction rightClickAction = hat.getRightClickAction();
		
		String leftClickDescription = getParsedActionDescription(hat, leftClickAction, hat.getLeftClickArgument());
		String rightClickDescription = getParsedActionDescription(hat, rightClickAction, hat.getRightClickArgument());
		
		//"/n&8Left Click Action:/n{1}/n/n&8Right Click Action:/n{2}/n/n&3Click to Change Actions"
		String description = Message.EDITOR_MAIN_MENU_ACTION_DESCRIPTION.getValue();
		description = description
				.replace("{1}", leftClickDescription)
				.replace("{2}", rightClickDescription);
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(description));
	}
	
	/**
	 * Translates the given action
	 * @param hat
	 * @param action
	 * @param argument
	 * @return
	 */
	private static String getParsedActionDescription (Hat hat, ParticleAction action, String argument)
	{
		switch (action)
		{
		case OPEN_MENU_PERMISSION:
		case OPEN_MENU:
		{
			String description = Message.EDITOR_ACTION_MENU_MENU_DESCRIPTION.getValue();
			String[] menuInfo = StringUtil.parseValue(description, "2");
			
			description = description.replace("{1}", action.getDisplayName());
			if (menuInfo != null)
			{
				if (argument.equals("") || !core.getDatabase().menuExists(argument)) {
					description = description.replace(menuInfo[0], menuInfo[1]);
				} else {
					description = description.replace(menuInfo[0], argument);
				}
			}
			
			return description;
		}
			
		case COMMAND:
		{
			String description = Message.EDITOR_ACTION_MENU_COMMAND_DESCRIPTION.getValue();
			String[] commandInfo = StringUtil.parseValue(description, "2");
			
			description = description.replace("{1}", action.getDisplayName());
			if (commandInfo != null)
			{
				if (argument.equals("")) {
					description = description.replace(commandInfo[0], commandInfo[1]);
				} else  {
					description = description.replace(commandInfo[0], "/" + argument);
				}
			}
			return description;
		}
			
		case DEMO:
		{
			String description = Message.EDITOR_ACTION_MENU_DEMO_DESCRIPTION.getValue();
			String[] suffixInfo = StringUtil.parseValue(description, "3");
			
			DecimalFormat df = new DecimalFormat("#.#");
			int duration = hat.getDemoDuration();
			double time = duration / 20D;
			
			description = description.replace("{1}", action.getDisplayName());
			if (suffixInfo != null)
			{
				String suffix = time == 1 ? "" : suffixInfo[1];
				description = description.replace(suffixInfo[0], suffix);
			}
			
			description = description.replace("{2}", df.format(time));
			return description;
		}
		
		default:
		{
			///n&8Current:/n&8� &7{1}
			String description = Message.EDITOR_ACTION_MENU_MISC_DESCRIPTION.getValue();
			description = description.replace("{1}", action.getDisplayName());
			return description;
		}
		}
	}
	
	public static void updateHatDescription (ItemStack item, Hat hat)
	{
		//"&7Slot &f{1}/n&7Type: &f{2}/n&7Location: &f{3}/n&7Mode: &f{4}/n&7Update: &f{5} &7tick{6=s}"
		String description = Message.EDITOR_HAT_GENERIC_DESCRIPTION.getValue();
		String s = 
				description.replace("{1}", String.valueOf(hat.getSlot()));
;	}
}