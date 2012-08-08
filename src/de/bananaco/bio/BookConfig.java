package de.bananaco.bio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bananaco.bio.util.Config;

public class BookConfig extends Config {
	
	public Map<String, List<String>> bios = new HashMap<String, List<String>>();
	
	public boolean contains(String name) {
		return bios.containsKey(name.toLowerCase());
	}
	
	public void set(String name, List<String> lines) {
		bios.put(name.toLowerCase(), lines);
	}
	
	public List<String> get(String name) {
		if(!contains(name)) {
			createNew(name);
		}
		return bios.get(name.toLowerCase());
	}
	
	public void createNew(String name) {
		bios.put(name.toLowerCase(), new ArrayList<String>());
	}
	
	public void remove(String name) {
		bios.remove(name.toLowerCase());
	}

}
