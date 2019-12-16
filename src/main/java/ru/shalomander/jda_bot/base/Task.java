package ru.shalomander.jda_bot.base;

import net.dv8tion.jda.api.JDA;
import ru.shalomander.jda_bot.JDABot;

import java.util.TimerTask;

public abstract class Task extends TimerTask {
    protected JDA jda;

    public Task(JDA jda) {
        this.jda = jda;
    }

    public abstract void run();
}
