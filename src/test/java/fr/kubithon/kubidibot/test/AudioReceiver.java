package fr.kubithon.kubidibot.test;

import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import net.dv8tion.jda.core.audio.AudioReceiveHandler;

public class AudioReceiver
{
    public static void main(String[] args) throws Exception
    {
        System.out.println("Démarrage...");

        DataLine.Info speakerInfo = new DataLine.Info(SourceDataLine.class, AudioReceiveHandler.OUTPUT_FORMAT);
        SourceDataLine speaker = (SourceDataLine) AudioSystem.getLine(speakerInfo);
        speaker.open(AudioReceiveHandler.OUTPUT_FORMAT);

        ServerSocket server = new ServerSocket(3775);
        Socket client;

        System.out.println("Serveur démarré sur 0.0.0.0:3775");

        while (!server.isClosed())
        {
            System.out.println("En attente d'une connexion...");
            client = server.accept();

            System.out.println("Connexion reçue, initialization audio");

            InputStream input = client.getInputStream();
            speaker.start();

            System.out.println("Lecture des données...");

            byte[] data = new byte[8000];

            while (!client.isClosed())
            {
                int readCount = input.read(data, 0, data.length);

                if(readCount > 0)
                {
                    speaker.write(data, 0, readCount);
                }
            }

            speaker.drain();
            speaker.close();

            System.out.println("Connexion fermée");
        }
    }
}
