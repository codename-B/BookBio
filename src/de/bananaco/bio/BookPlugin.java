package de.bananaco.bio;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import de.bananaco.bio.util.Book;
import de.bananaco.bio.util.CraftBookBuilder;

public class BookPlugin extends JavaPlugin implements Listener {

	Set<String> listeningLines = new HashSet<String>();
	BookConfig config = new BookConfig();

	@Override
	public void onEnable() {
		config.load(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(sender instanceof Player) {
			return this.onCommand((Player) sender, command, label, args);
		} else {
			return true;
		}
	}

	public boolean onCommand(Player player, Command command,
			String label, String[] args) {
		if(command.getName().equalsIgnoreCase("read")) {
			String name = args.length>0?args[0]:player.getName();
			if(listeningLines.contains(name)) {
				player.sendMessage(ChatColor.RED+"// Finish writing your book first");
				return true;
			} else if(!config.contains(name)) {
				player.sendMessage(ChatColor.RED+"// No book saved for "+name.toLowerCase());
				return true;
			} else {
				ItemStack inHand = player.getItemInHand();
				if(inHand != null && (inHand.getType() == Material.WRITTEN_BOOK || inHand.getType() == Material.BOOK_AND_QUILL)) {
					CraftItemStack book = new CraftItemStack(Material.WRITTEN_BOOK);
					Book b = new CraftBookBuilder().getBook(book);
					// now set the stuff on the book itself
					b.setAuthor(name.toLowerCase());
					b.setTitle("Bio");
					b.setPages(getLines(config.get(name)));
					player.setItemInHand(book);
					player.sendMessage(ChatColor.GREEN+"// You have been given the bio for "+name.toLowerCase());
					return true;
				} else {
					player.sendMessage(ChatColor.RED+"// Command can only be used with a book in your hand");
					return true;
				}
			}
		} else if(command.getName().equalsIgnoreCase("write")) {
			String name = player.getName();
			ItemStack inHand = player.getItemInHand();
			if(listeningLines.contains(name)) {
				if(inHand != null && (inHand.getType() == Material.WRITTEN_BOOK || inHand.getType() == Material.BOOK_AND_QUILL)) {
					player.sendMessage(ChatColor.GREEN+"// Book saved!");
					listeningLines.remove(name);
					// get book in hand
					Book b = new CraftBookBuilder().getBook(inHand);
					String[] lines = b.getPages();
					config.set(name, Arrays.asList(lines));
					config.save(this);
					//player.setItemInHand(null);
					player.chat("/read");
					return true;
				} else {
					player.sendMessage(ChatColor.RED+"// Command can only be used with a book in your hand");
					return true;
				}
			} else {
				if(inHand != null && (inHand.getType() == Material.WRITTEN_BOOK || inHand.getType() == Material.BOOK_AND_QUILL)) {
					if(!config.contains(name)) {
						player.sendMessage(ChatColor.GREEN+"// Write in the book to write your bio");
					} else {
						player.sendMessage(ChatColor.GREEN+"// Edit the book to continue writing your bio");
					}
					player.sendMessage(ChatColor.GREEN+"// Type "+ChatColor.WHITE+"/"+label+ChatColor.GREEN+" again to finish writing");
					listeningLines.add(name);
					// now add a book and quill
					CraftItemStack book = new CraftItemStack(Material.BOOK_AND_QUILL);
					if(config.contains(name)) {
						Book b = new CraftBookBuilder().getBook(book);
						// now set the stuff on the book itself
						b.setAuthor(name.toLowerCase());
						b.setTitle("Bio");
						b.setPages(getLines(config.get(name)));
					} else {
						// do nothing
					}
					player.setItemInHand(book);
					return true;
				} else {
					player.sendMessage(ChatColor.RED+"// Command can only be used with a book in your hand");
					return true;
				}
			}
		}
		// else return true
		return true;
	}

	public String[] getLines(List<String> lines) {
		String[] data = new String[lines.size()];
		for(int i=0; i<lines.size(); i++) {
			data[i] = lines.get(i);
		}
		return data;
	}

}
