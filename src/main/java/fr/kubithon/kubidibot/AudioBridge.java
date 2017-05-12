package fr.kubithon.kubidibot;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import fr.litarvan.krobot.config.ConfigProvider;
import java.io.IOException;
import java.net.Socket;
import net.dv8tion.jda.core.audio.AudioReceiveHandler;
import net.dv8tion.jda.core.audio.CombinedAudio;
import net.dv8tion.jda.core.audio.UserAudio;
import net.dv8tion.jda.core.managers.AudioManager;

@Singleton
public class AudioBridge implements AudioReceiveHandler
{
    private AudioManager audio;
    private Socket connection;
    private double volume = 1.0;

    @Inject
    private ConfigProvider config;

    public void connect() throws IOException
    {
        if (audio == null)
        {
            throw new IllegalStateException("Channel is not defined");
        }

        audio.setReceivingHandler(this);
        connection = new Socket(config.at("network.host"), config.at("network.port", int.class));
    }

    public void stop()
    {
        if (audio == null)
        {
            throw new IllegalStateException("Channel is not defined");
        }

        try
        {
            connection.close();
        }
        catch (IOException ignored)
        {
        }
        finally
        {
            connection = null;
        }
    }

    public void setAudio(AudioManager audio)
    {
        this.audio = audio;
    }

    public AudioManager getAudio()
    {
        return audio;
    }

    @Override
    public boolean canReceiveCombined()
    {
        return true;
    }

    @Override
    public boolean canReceiveUser()
    {
        return true;
    }

    @Override
    public void handleCombinedAudio(CombinedAudio combinedAudio)
    {
        send(combinedAudio.getAudioData(volume));
    }

    @Override
    public void handleUserAudio(UserAudio userAudio)
    {
        send(userAudio.getAudioData(volume));
    }

    private void send(byte[] data)
    {
        if (connection == null)
        {
            return;
        }

        try
        {
            connection.getOutputStream().write(data);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public double getVolume()
    {
        return volume;
    }

    public void setVolume(double volume)
    {
        this.volume = volume;
    }
}
