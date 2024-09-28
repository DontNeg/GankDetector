package com.dontneg.gankdetector.listener;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class GankListener extends ListenerAdapter {
    SlashCommandInteractionEvent event;

    @SuppressWarnings({"DataFlowIssue", "SameParameterValue"})
    private String getOption(String name, boolean toLower){
        if(toLower) return event.getOption(name).getAsString().toLowerCase().replace(" ", "");
        return event.getOption(name).getAsString();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        this.event = event;
        String path = "C:/Users/iTempura/Documents/GankDetector/src/main/java/com/dontneg/gankdetector/list/";
        final Path listPath = Path.of(path + "gankerslist.txt");
        switch(event.getName().toLowerCase()){
            case "addguild" -> {
                try {
                    FileWriter writer = new FileWriter(path+"gankerslist.txt", true);
                    writer.write(getOption("guild",true).replaceAll("\\s+","") + "\n");
                    writer.close();
                } catch (IOException ignored) {}
                event.reply(getOption("guild",false) + " has been added successfully.").setEphemeral(true).queue();
                try {
                    Objects.requireNonNull(event.getJDA().getTextChannelById("1288958532099506186"))
                            .editMessageById("1289293989085843559", Files.readString(listPath, StandardCharsets.UTF_8)).queue();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case "removeguild" -> {
                try{
                    File newList = new File(path+"newlist.txt");
                    if(!newList.createNewFile()){
                        event.reply("Failed to remove guild | New file not created").setEphemeral(true).queue();
                        return;
                    }
                    BufferedWriter writer = new BufferedWriter(new FileWriter(newList, true));
                    BufferedReader reader = new BufferedReader(new FileReader(path+"gankerslist.txt"));
                    String currentLine;
                    while((currentLine = reader.readLine()) != null) {
                        if(!currentLine.equals(getOption("guild",true)))
                            writer.write(currentLine + "\n");
                    }
                    writer.close();
                    reader.close();
                    File oldList = new File(path+"gankerslist.txt");
                    if(!oldList.delete()){
                        event.reply("Failed to remove guild | Old file not deleted").setEphemeral(true).queue();
                        return;
                    }
                    boolean rename = new File(path+"newlist.txt").renameTo(new File(path+"gankerslist.txt"));
                    if(rename) event.reply(getOption("guild",false) + " has been removed successfully.").setEphemeral(true).queue();
                    try {
                        Objects.requireNonNull(event.getJDA().getTextChannelById("1288958532099506186"))
                                .editMessageById("1289293989085843559", Files.readString(listPath, StandardCharsets.UTF_8)).queue();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }catch(Exception ignored){}
            }
            case "displayguilds" -> {
                HashSet<String> guilds = new HashSet<>();
                try{
                    String currentLine;
                    BufferedReader reader = new BufferedReader(new FileReader(path+"gankerslist.txt"));
                    while((currentLine = reader.readLine()) != null) {
                        guilds.add(currentLine);
                    }
                }catch(Exception ignored){}
                StringBuilder message = new StringBuilder();
                for(String guild: guilds){
                    message.append(guild).append("\n");
                }
                event.reply(message.toString()).setEphemeral(true).queue();
            }
            case "checkguild" -> {
                try{
                    String currentLine;
                    BufferedReader reader = new BufferedReader(new FileReader(path+"gankerslist.txt"));
                    while((currentLine = reader.readLine()) != null) {
                        if(currentLine.equals(getOption("guild",true))){
                            event.reply(getOption("guild",false) + " is on the list.").setEphemeral(true).queue();
                            return;
                        }
                    }
                }catch(Exception ignored){}
                event.reply(getOption("guild",false) + " is not on the list").setEphemeral(true).queue();
            }
        }
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        OptionData addGuildDescription = new OptionData(OptionType.STRING, "guild", "Name of the guild you want to add to the list", true);
        OptionData removeGuildDescription = new OptionData(OptionType.STRING, "guild", "Name of the guild you want to remove from the list", true);
        OptionData checkGuildDescription = new OptionData(OptionType.STRING, "guild", "Name of the guild you want to check the list for", true);
        commandData.add(Commands.slash("addguild","Add guild to the known gankers list")
                .addOptions(addGuildDescription));
        commandData.add(Commands.slash("removeguild","Remove guild from the known gankers list")
                .addOptions(removeGuildDescription));
        commandData.add(Commands.slash("checkguild", "Check list for guild")
                .addOptions(checkGuildDescription));
        commandData.add(Commands.slash("displayguilds","Display all guilds currently on the list"));
        event.getJDA().updateCommands().addCommands(commandData).queue();
    }
}
