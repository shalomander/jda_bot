package ru.shalomander.jda_bot.base;

import net.dv8tion.jda.api.entities.TextChannel;

public class Message {
    protected TextChannel textChannel;
    protected StringBuilder stringBuilder;

    {
        new StringBuilder("");
    }

    public Message(TextChannel textChannel, String message) {
        this.textChannel = textChannel;
        this.stringBuilder = new StringBuilder(message);
    }

    public Message(TextChannel textChannel) {
        this.textChannel = textChannel;
        this.stringBuilder = new StringBuilder("");
    }

    public void append(String string) {
        stringBuilder.append(string);
    }

    public void send(){
       textChannel.sendMessage(stringBuilder.toString()).queue();
    }
}
