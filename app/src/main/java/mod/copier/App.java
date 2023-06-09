/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package mod.copier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.google.common.io.Files;

public class App {
    public static File findMod(File dir) {

        // check if file is dir
        if (dir.isDirectory()) {
            // get files in dir
            File[] files = dir.listFiles();

            // if empty, return null
            if (files.length == 0) {
                return null;
            }

            // get first file
            File file = files[0];
            if (file.isDirectory()) {
                // If dir, recurse
                return findMod(file);
            } else {

                // if file, check if pak, otherwise return null
                if (file.getName().contains(".pak")) {
                    return file;
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    // Step out of folder till paks folder is found
    public static File navToPaks(File modIODir) {
        if (modIODir.getName().equals("Paks")) {
            return modIODir;
        }
        return navToPaks(modIODir.getParentFile());
    }

    public static void removeOldMods(File paksDir) {

        // Compile the regex pattern
        Pattern pattern = Pattern.compile("pakchunk9+.*\\.pak$");

        // Get all files in the directory
        File[] files = paksDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && pattern.matcher(file.getName()).matches()) {
                    file.delete();
                }

            }
        }
    }

    public static void main(String[] args) {
        // Get game path
        Scanner input = new Scanner(System.in);
        System.out.print(
                "Please enter the directory your Ready or Not install: \n (Hit enter to assume default C: steam install location)\n");
        String path = input.nextLine();
        if (path.equals("")) {
            path = "C:\\Program Files (x86)\\Steam\\steamapps\\common\\Ready Or Not";
        } //
        input.close();

        // Find mod.io folder
        Path modsPath = Paths.get(path).resolve("ReadyOrNot\\Content\\Paks\\mod.io");
        File mods = modsPath.toFile();
        mods = mods.listFiles()[0];
        mods = mods.toPath().resolve("mods").toFile();

        // Find Paks folder
        File paks = navToPaks(mods);
        System.out.println("Paks: " + paks.getAbsolutePath());
        removeOldMods(paks);

        // Get mod folders
        File[] modList = mods.listFiles();
        for (File mod : modList) {

            // check if file is dir
            if (mod.isDirectory()) {

                // find mod file(if exists)
                File modFile = findMod(mod);
                if (modFile != null) {
                    try {

                        // copy mod file to paks folder
                        System.out.println("Copying " + modFile.getName());
                        Files.copy(modFile, new File(paks.getAbsolutePath() + "\\" + modFile.getName()));
                    } catch (IOException e) {
                        System.out.println("Failed to copy " + modFile.getName());
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
