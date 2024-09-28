package com.dontneg.gankdetector;

import com.dontneg.gankdetector.listener.GankListener;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class GankDetector {

    public GankDetector(){}

    public static void main(String[] args){
        Dotenv config = Dotenv.configure().load();
        String token = config.get("TOKEN");
        JDABuilder builder = JDABuilder.createDefault(token)
                .setStatus(OnlineStatus.ONLINE)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.watching("out for ganking clans"));
        JDA jda = builder.build();
        jda.addEventListener(new GankListener());
    }
}
