package dev.strongtino.soteria.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.Color;
import java.util.Arrays;

public class JDAUtil {

    public static final String EMBED_FOOTER = "License system created by strongtino";
    public static final String EMBED_ICON = "https://cdn.discordapp.com/avatars/425212072125530113/616d8c37cef6ec7b04a1e40f153486df.png";
    
    public static MessageEmbed createEmbed(Color color, String title, String... fields) {
        EmbedBuilder embed = new EmbedBuilder();
        StringBuilder builder = new StringBuilder();

        Arrays.asList(fields).forEach(field -> builder.append(field).append('\n'));

        embed.setColor(color);
        embed.setTitle(title);
        embed.appendDescription(builder.toString());
        embed.setFooter(EMBED_FOOTER, EMBED_ICON);

        return embed.build();
    }

    public static EmbedBuilder embedBuilder(Color color, String title, String... fields) {
        EmbedBuilder embed = new EmbedBuilder();
        StringBuilder builder = new StringBuilder();

        Arrays.asList(fields).forEach(field -> builder.append(field).append('\n'));

        embed.setColor(color);
        embed.setTitle(title);
        embed.appendDescription(builder.toString());
        embed.setFooter(EMBED_FOOTER, EMBED_ICON);

        return embed;
    }
}
