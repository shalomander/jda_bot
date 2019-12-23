package ru.shalomander.jda_bot.base;

import com.google.common.base.Splitter;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Arrays;

public class Message {
    protected TextChannel textChannel;
    protected StringBuilder stringBuilder;
    protected Iterable<String> message;
    public static final int MAX_LENGTH = 2000,
            ORIGINAL_MESSAGE = 0,
            CUT_MESSAGE = 1,
            CHUNK_MESSAGE = 2;

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

    public void send(int oversizeAction) {
        chunkMessage(oversizeAction);
        message.forEach(chunk -> {
            textChannel.sendMessage(chunk).queue();
        });
    }

    public void send() {
        send(CHUNK_MESSAGE);
    }

    protected void chunkMessage(int oversizeAction) {
        switch (oversizeAction) {
            case 0:
                message = Arrays.asList(stringBuilder.toString());
                break;
            case 1:
                message = Arrays.asList(stringBuilder.toString().substring(0, MAX_LENGTH - 1));
                break;
            case 2:
                message = Splitter.fixedLength(MAX_LENGTH).split(stringBuilder.toString());
                break;
        }
    }
}
