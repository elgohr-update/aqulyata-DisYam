package com.aqulasoft.disyam.commands;

import com.aqulasoft.disyam.audio.PlayerManager;
import com.aqulasoft.disyam.audio.YandexMusicClient;
import com.aqulasoft.disyam.models.audio.YaStationSequence;
import com.aqulasoft.disyam.models.audio.YaTrack;
import com.aqulasoft.disyam.models.bot.BotState;
import com.aqulasoft.disyam.models.bot.PlayerState;
import com.aqulasoft.disyam.models.bot.StationState;
import com.aqulasoft.disyam.service.BotStateManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.List;

import static com.aqulasoft.disyam.utils.Consts.PREFIX;

public class RecommendationCommand implements Command {

    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        BotState state = BotStateManager.getInstance().getState(event.getGuild().getIdLong());
        if (state instanceof PlayerState) {
            YaTrack track = ((PlayerState) state).getCurrentTrack();
            YaStationSequence seq = YandexMusicClient.getStationTracks(track, track.getId());
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.RED);
            event.getChannel().sendMessage(builder.build()).queue(message -> {
                StationState newState = new StationState(seq, message, event.getGuild());
                BotStateManager.getInstance().setState(event.getGuild().getIdLong(), newState, false);
                ((PlayerState) newState).updateMessage(true);
                PlayerManager playerManager = PlayerManager.getInstance();
                playerManager.loadAndPlayPlaylist(event.getChannel());
            });
        }
    }

    @Override
    public String getHelp() {
        return "Start radio by track\n" +
                "Usage: `" + PREFIX + getInvoke() + "`";
    }

    @Override
    public String getInvoke() {
        return "rec";
    }
}