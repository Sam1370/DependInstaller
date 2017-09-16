package sam1370.dependinstaller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.plugin.java.JavaPlugin;

public class DependInstaller {
	JavaPlugin pl;
	private List<String> dependenciesInstalled = new ArrayList<String>();

	public DependInstaller(JavaPlugin pl) {
		this.pl = pl;
	}

	public void copyDependencyFromSpigot(String spigotURL) {

	}

	public void copyDependencyFromURL(String URL) {

	}

	public void copyDependencyFromFile(String filePath, String dependencyName) {
		String alreadyExistsMessage = "A version of " + dependencyName + " already exists, aborting...";
		try {
			InputStream yInputStream = getClass().getClassLoader().getResourceAsStream(filePath);
			if (yInputStream != null) {
				pl.getLogger().info(dependencyName + " file within JAR found! Filename is " + filePath + ". InputStream is " + yInputStream);
			} else {
				pl.getLogger().severe(dependencyName + " file within JAR not found! Aborting...");
				return;
			}
			File yamlerOut = new File(pl.getDataFolder().getParent() + File.separator + filePath);
			if (Bukkit.getPluginManager().getPlugin(dependencyName) == null) {
				FileUtils.copyInputStreamToFile(yInputStream, yamlerOut);
				Bukkit.getPluginManager().loadPlugin(yamlerOut);
				pl.getLogger().info("Dependency " + dependencyName + " does not exist! Copying and enabling...");
				Bukkit.getPluginManager().enablePlugin(Bukkit.getPluginManager().getPlugin(dependencyName));
				dependenciesInstalled.add(dependencyName);
			} else {
				pl.getLogger().info(alreadyExistsMessage);
				return;
			}
		} catch (FileExistsException ex) {
			pl.getLogger().info(alreadyExistsMessage);
		} catch (IOException ex) {
			pl.getLogger().severe("Failed to copy " + dependencyName + "! See the stack trace below.");
			ex.printStackTrace();
		} catch (UnknownDependencyException | InvalidPluginException | InvalidDescriptionException ex) {
			pl.getLogger().severe(dependencyName + " could not be enabled! Disabling plugin.");
			ex.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(pl);
		}
	}

	public void finalizeDependencies() {
		Object[] dependencyArray = dependenciesInstalled.toArray();
		for (int i = 0; i < dependencyArray.length; i++) {
			if (dependencyArray[i] != null) {
				String dependencyString = dependencyArray[i].toString();
				if (Bukkit.getPluginManager().getPlugin(dependencyString) == null) {
					pl.getLogger().severe(dependencyString + " could not be enabled! Disabling plugin.");
					Bukkit.getPluginManager().disablePlugin(pl);
				}
			}
		}
		pl.getLogger().info("Dependencies finalized!");
	}
}