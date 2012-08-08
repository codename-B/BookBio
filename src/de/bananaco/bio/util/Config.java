package de.bananaco.bio.util;



import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
/**
 * Inspired by md_5
 * 
 * An awesome super-duper-lazy Config lib!
 * Just extend it, set some (non-static) variables
 * 
 * @author codename_B
 */
public abstract class Config {

	private transient File file = null;
	private transient YamlConfiguration conf = new YamlConfiguration();
	
	/**
	 * When using this constructor, remember that
	 * config.load(); will be unavailable
	 */
	public Config() {
		// don't do anything here
	}
	
	/**
	 * This constructor stores an reference to your
	 * plugin, so you can be even more lazy!
	 */
	public Config(Plugin plugin) {
		this.file = getFile(plugin);
	}
	
	/**
	 * This constructor lets you set a file directly
	 * EVEN MORE LAZY
	 */
	public Config(File file) {
		this.file = file;
	}
	
	/**
	 * Lazy load
	 */
	public void load() {
		if(file != null) {
			try {
				onLoad(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			new InvalidConfigurationException("File cannot be null!").printStackTrace();
		}
	}
	
	/**
	 * Load a specific config
	 * @param plugin
	 */
	public void load(Plugin plugin) {
		File file = getFile(plugin);
		if(file != null) {
			try {
				onLoad(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			new InvalidConfigurationException("File cannot be null!").printStackTrace();
		}
	}
	
	/**
	 * Lazy save
	 */
	public void save() {
		if(file != null) {
			try {
				onSave(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			new InvalidConfigurationException("File cannot be null!").printStackTrace();
		}
	}
	
	/**
	 * Save a specific config
	 * @param plugin
	 */
	public void save(Plugin plugin) {
		File file = getFile(plugin);
		if(file != null) {
			try {
				onSave(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			new InvalidConfigurationException("File cannot be null!").printStackTrace();
		}
	}
	
	/**
	 * Internal method - used by both load() and load(Plugin plugin)
	 * @param plugin
	 * @throws Exception
	 */
	private void onLoad(File file) throws Exception {
		if(!file.exists()) {
			if(file.getParentFile() != null)
				file.getParentFile().mkdirs();
			file.createNewFile();
		}
		conf.load(file);
		for(Field field : getClass().getDeclaredFields()) {
			String path = field.getName().replaceAll("_", ".");
			if(doSkip(field)) {
				// don't touch it
			} else if(field.getType().getName().equals("java.util.Map")) {
				String n = field.getName();
				Map<String, Object> data = new HashMap<String, Object>();
				
				ConfigurationSection ns = conf.getConfigurationSection(n);
				if(ns != null && ns.getKeys(false) != null && ns.getKeys(false).size() > 0) {
					Set<String> keys = ns.getKeys(false);
					for(String key : keys) {
						data.put(key, ns.get(key));
					}
				}
				
				field.set(this, data);
			} else if(conf.isSet(path)) {
				field.set(this, conf.get(path));
			} else {
				conf.set(path, field.get(this));
			}
		}
		conf.save(file);
	}
	
	/**
	 * Internal method - used by both save() and save(Plugin plugin)
	 * @param plugin
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void onSave(File file) throws Exception {
		if(!file.exists()) {
			if(file.getParentFile() != null)
				file.getParentFile().mkdirs();
			file.createNewFile();
		}
		for(Field field : getClass().getDeclaredFields()) {
			String path = field.getName().replaceAll("_", ".");
			if(doSkip(field)) {
				// don't touch it
			} else if(field.getType().getName().equals("java.util.Map")) {
				String n = field.getName();
				Map<String, ?> data = (Map<String, ?>) field.get(this);
				
				ConfigurationSection ns = conf.getConfigurationSection(n);
				if(ns == null) {
					ns = conf.createSection(field.getName());
				}
				if(data.keySet().size() > 0) {
					for(String key : data.keySet()) {
						ns.set(key, data.get(key));
					}
				}
			} else {
				conf.set(path, field.get(this));
			}
		}
		conf.save(file);
	}
	
	/**
	 * A little internal method to save re-using code
	 * @param field
	 * @return skip
	 */
	private boolean doSkip(Field field) {
		return Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers());
	}
	
	private File getFile(Plugin plugin) {
		return new File(plugin.getDataFolder(), "config.yml");
	}
	
}