package fr.kubithon.kubidibot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class AudioReceiver
{
    public static void main(String[] args) throws Exception
    {
        System.out.println("Connection à dede:3775");
        Socket client = new Socket("127.0.0.1", 3775);

        InputStream input = client.getInputStream();

        Player player = new Player(input);
        player.play();
    }

    /*private static final AudioFormat OUTPUT_FORMAT = new AudioFormat(48000.0f, 16, 2, true, true);
    
    public static void main(String[] args) throws Exception
    {
        System.out.println("Démarrage...");

        DataLine.Info speakerInfo = new DataLine.Info(SourceDataLine.class, OUTPUT_FORMAT);
        SourceDataLine speaker = (SourceDataLine) AudioSystem.getLine(speakerInfo);
        speaker.open(OUTPUT_FORMAT);

        Socket client = new Socket("git.ylinor.com", 3775);

        System.out.println("Connection à dede:3775");

        InputStream input = client.getInputStream();
        speaker.start();

        System.out.println("Lecture des données...");

        byte[] data = new byte[8000];

        while (!client.isClosed())
        {
            int readCount = input.read(data, 0, data.length);

            if (readCount == -1)
            {
                break;
            }

            if(readCount > 0)
            {
                speaker.write(data, 0, readCount);
            }
        }

        speaker.drain();
        speaker.close();

        System.out.println("Connexion fermée");
    }*/
}
