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

/**
 * The Audio Bridge
 *
 * <p>
 *     This is the server that redirects the audio received in a
 *     Discord voice channel to its clients.<br>
 *     The audio format is {@link AudioReceiveHandler#OUTPUT_FORMAT}.
 * </p>
 *
 * @author Litarvan
 * @version 1.0.0
 */
@Singleton
public class AudioBridge implements AudioReceiveHandler
{
    private static final Logger LOGGER = LogManager.getLogger("AudioBridge");

    private AudioManager audio; // A voice channel audio reader
    private ServerSocket server;
    private CopyOnWriteArrayList<Socket> clients;
    private Thread serverThread;
    private double volume = 1.0;

    @Inject
    private ConfigProvider config;

    /**
     * Starts the server in a separated thread, receive clients
     * and add them to the {@link #clients} list.
     *
     * @throws IOException If the server socket init threw one
     */
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

    /**
     * Stop the server and its thread
     */
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

    /**
     * Set the current channel audio manager
     */
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
        // From what i saw, this method is never called
        send(userAudio.getAudioData(volume));
    }

    /**
     * Encode the given bytes to MP3 and then send it to every clients<br>
     * Currently it looks like the MP3 encoder always returns an array full of 0
     */
    private void send(byte[] data)
    {
        if (server == null)
        {
            return;
        }

        byte[] mp3 = encodePcmToMp3(data);

        // Clean up old clients
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
                c.getOutputStream().write(mp3);
            }
            catch (IOException e)
            {
                LOGGER.info("Error while sending data to a client", e);
            }
        });
    }

    /**
     * Encode the given Discord audio bytes to MP3
     * Currently it looks like the MP3 encoder always returns an array full of 0
     */
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
