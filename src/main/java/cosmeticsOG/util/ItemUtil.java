package cosmeticsOG.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.compatibility.CompatibleMaterial;
import cosmeticsOG.locale.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public class ItemUtil {

	public static Material getMaterial(String material, Material fallback) {

		Material originalMaterial = Material.getMaterial(material);

		return originalMaterial != null ? originalMaterial : fallback;

	}

	public static Material getMaterial(String material, String fallback) {

		Material originalMaterial = Material.getMaterial(material);

		return originalMaterial != null ? originalMaterial : Material.getMaterial(fallback);

	}

	@SuppressWarnings("deprecation")
	@NotNull
	public static ItemStack createItem(Material material, int quantity, int damage) {

		ItemStack item = new ItemStack(material, quantity);
		if (CosmeticsOG.serverVersion >= 13) {

			ItemMeta meta = item.getItemMeta();
			if (meta instanceof Damageable) {

				// Set damage for versions >= 1.13.
				((Damageable) meta).setDamage(damage);
				item.setItemMeta(meta);

			}

		}
		else {

			// Legacy method for versions < 1.13.
			item.setDurability((short) damage);

		}

		return item;

	}

	public static ItemStack createItem(Material material, int quantity, String titleString, List<TextComponent> description) {

		TextComponent title = Utils.legacySerializerAnyCase(titleString);

		ItemStack item = new ItemStack(material, quantity);
		ItemMeta itemMeta = item.getItemMeta();

		// Convert TextComponent to Component.
		itemMeta.displayName((Component) title);

		// Convert List<TextComponent> to List<Component>.
		List<Component> components = description.stream().map(text -> (Component) text).collect(Collectors.toList());
		itemMeta.lore(components);

		addItemFlags(itemMeta);
		item.setItemMeta(itemMeta);

		return item;

	}

	public static ItemStack createItem(Material material, int quantity, String title) {

		ItemStack item = new ItemStack(material, quantity);
		ItemMeta itemMeta = item.getItemMeta();

		itemMeta.displayName(Utils.legacySerializerAnyCase(title));

		addItemFlags(itemMeta);
		item.setItemMeta(itemMeta);

		return item;

	}

	public static ItemStack createItem(Material material, String displayNameString, List<Component> description) {

		TextComponent displayName = Utils.legacySerializerAnyCase(displayNameString);

		// Create the item with the material.
		ItemStack item = new ItemStack(material);
		ItemMeta itemMeta = item.getItemMeta();

		itemMeta.displayName(displayName);

		// Set the lore.
		itemMeta.lore(description);

		// Add item flags.
		addItemFlags(itemMeta);

		// Apply the meta to the item.
		item.setItemMeta(itemMeta);

		return item;

	}

	public static void setItemDescription(ItemStack item, List<? extends Component> description) {

		if (item == null || description == null) {

			return;

		}

		ItemMeta itemMeta = item.getItemMeta();
		if (itemMeta == null) {

			return;

		}

		// Process the list, handling both TextComponent and generic Component
		List<Component> components = description.stream()
				.map(component -> {

					if (component instanceof TextComponent) {

						// Process color codes for TextComponent
						return Utils.legacySerializerAnyCase(((TextComponent) component).content());

					}
					else {

						// For other Component types, simply return them as-is.
						return component;

					}

				}).collect(Collectors.toList());

		// Set the lore (description) of the item
		itemMeta.lore(components);
		item.setItemMeta(itemMeta);

	}

	public static void setItemDescription(ItemStack item, TextComponent... description) {

		if (item == null || description == null) {

			return;

		}

		// Convert TextComponent array to List<Component> for color code processing.
		List<Component> components = Arrays.stream(description)
				.map(textComponent -> Utils.legacySerializerAnyCase(textComponent.content()))
				.collect(Collectors.toList());

		ItemMeta itemMeta = item.getItemMeta();

		itemMeta.lore(components);
		item.setItemMeta(itemMeta);

	}

	// Set item description using Message object.
	public static void setItemDescription(ItemStack item, Message description) {

		setItemDescription(item, StringUtil.parseDescription(description.getValue()));

	}

	public static void setItemName(ItemStack item, String name) {

		if (item == null || name == null) {

			return;

		}

		// Get the item's metadata.
		ItemMeta meta = item.getItemMeta();
		if (meta == null) {

			return;

		}

		// Set the display name of the item using the legacy formatted string.
		meta.displayName(Utils.legacySerializerAnyCase(name));

		// Apply the modified meta back to the item.
		item.setItemMeta(meta);

	}

	public static void setItemName(ItemStack item, Message name) {

		setItemName(item, name.getValue());

	}

	public static void setNameAndDescription(ItemStack item, Component name, List<Component> description) {

		ItemMeta itemMeta = item.getItemMeta();

		itemMeta.displayName(name);
		itemMeta.lore(description);

		addItemFlags(itemMeta);
		item.setItemMeta(itemMeta);

	}

	public static void setNameAndDescription(ItemStack item, Message name, Message description) {

		setNameAndDescription(item, Component.text(name.getValue()), StringUtil.parseDescription(description.getValue()));

	}

	public static void highlightItem(ItemStack item) {

		if (CosmeticsOG.serverVersion >= 8) {

			ItemMeta itemMeta = item.getItemMeta();

			addItemFlags(itemMeta);

			// Add fake enchant for glow.
			itemMeta.addEnchant(Enchantment.ARROW_DAMAGE, 0, true);
			item.setItemMeta(itemMeta);

		}

	}

	public static void stripHighlight(ItemStack item) {

		ItemMeta itemMeta = item.getItemMeta();

		itemMeta.removeEnchant(Enchantment.ARROW_DAMAGE);
		item.setItemMeta(itemMeta);

	}

	@SuppressWarnings("deprecation")
	public static void setItemType(ItemStack item, Material material, int damage) {

		item.setType(material);
		if (CosmeticsOG.serverVersion >= 13) {

			ItemMeta meta = item.getItemMeta();
			if (meta instanceof Damageable) {

				((Damageable) meta).setDamage(damage);

				item.setItemMeta(meta);

			}

		}
		else {

			// Legacy method for versions < 1.13.
			item.setDurability((short) damage);

		}

	}

	public static void setItemType(ItemStack item, CompatibleMaterial material) {

		setItemType(item, material.getMaterial(), material.getDurability());

	}

	private static void addItemFlags(ItemMeta itemMeta) {

		try {

			itemMeta.addItemFlags(ItemFlag.values());

		}
		catch (NoClassDefFoundError ignored) {}

	}

}