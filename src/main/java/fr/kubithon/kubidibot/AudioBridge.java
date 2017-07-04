package fr.kubithon.kubidibot;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import net.dv8tion.jda.core.audio.AudioReceiveHandler;
import net.dv8tion.jda.core.audio.CombinedAudio;
import net.dv8tion.jda.core.audio.UserAudio;
import net.dv8tion.jda.core.managers.AudioManager;
import net.sourceforge.lame.lowlevel.LameEncoder;
import net.sourceforge.lame.mp3.Lame;
import net.sourceforge.lame.mp3.MPEGMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.krobot.config.ConfigProvider;

@Singleton
public class AudioBridge implements AudioReceiveHandler
{
    private static final Logger LOGGER = LogManager.getLogger("AudioBridge");

    private AudioManager audio;
    private ServerSocket server;
    private CopyOnWriteArrayList<Socket> clients;
    private Thread serverThread;
    private double volume = 1.0;

    @Inject
    private ConfigProvider config;

    public void start() throws IOException
    {
        if (audio == null)
        {
            throw new IllegalStateException("Channel is not defined");
        }

        LOGGER.info("Starting audio bridge...");

        int port = config.at("network.port", int.class);

        audio.setReceivingHandler(this);
        server = new ServerSocket(port);

        clients = new CopyOnWriteArrayList<>();
        serverThread = new Thread(() ->
        {
            while (!Thread.interrupted())
            {
                try
                {
                    Socket client = server.accept();
                    clients.add(client);

                    LOGGER.info("Connection received from " + client.getInetAddress().getHostAddress());
                }
                catch (IOException e)
                {
                    LOGGER.error("Exception while listening for clients", e);
                }
            }
        });
        serverThread.start();

        LOGGER.info("Server listening on port " + port);
    }

    public void stop()
    {
        if (audio == null)
        {
            throw new IllegalStateException("Channel is not defined");
        }

        if (server == null)
        {
            return;
        }

        LOGGER.info("Stopping server...");

        serverThread.interrupt();

        try
        {
            server.close();
        }
        catch (IOException ignored)
        {
        }
        finally
        {
            server = null;
            serverThread = null;
        }

        LOGGER.info("Server stopped");
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
        if (server == null)
        {
            return;
        }

        System.out.println("PCM : " + Arrays.toString(data));
        byte[] mp3 = encodePcmToMp3(data);
        System.out.println("MP3 : " + Arrays.toString(mp3));

        int oldSize = clients.size();
        clients.removeIf(socket -> socket.isClosed() || socket.isOutputShutdown());

        if (oldSize != clients.size())
        {
            LOGGER.info("Cleaned " + oldSize + " closed connections");
        }

        clients.forEach(c ->
        {
            try
            {
                System.out.println("CLIENT");
                c.getOutputStream().write(mp3);
            }
            catch (IOException e)
            {
                LOGGER.info("Error while sending data to a client", e);
            }
        });
    }

    private byte[] encodePcmToMp3(byte[] pcm)
    {
        LameEncoder encoder = new LameEncoder(AudioReceiveHandler.OUTPUT_FORMAT, 256, MPEGMode.STEREO, Lame.QUALITY_HIGHEST, false);

        ByteArrayOutputStream mp3 = new ByteArrayOutputStream();
        byte[] buffer = new byte[encoder.getPCMBufferSize()];

        int bytesToTransfer = Math.min(buffer.length, pcm.length);
        int bytesWritten;
        int currentPcmPosition = 0;
        while (0 < (bytesWritten = encoder.encodeBuffer(pcm, currentPcmPosition, bytesToTransfer, buffer)))
        {
            currentPcmPosition += bytesToTransfer;
            bytesToTransfer = Math.min(buffer.length, pcm.length - currentPcmPosition);

            mp3.write(buffer, 0, bytesWritten);
        }

        encoder.close();
        return mp3.toByteArray();
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
