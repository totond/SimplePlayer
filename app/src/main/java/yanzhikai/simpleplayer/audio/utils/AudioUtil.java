package yanzhikai.simpleplayer.audio.utils;


import java.util.ArrayList;

import yanzhikai.simpleplayer.audio.AudioFileReader;
import yanzhikai.simpleplayer.audio.formats.ape.APEFileReader;
import yanzhikai.simpleplayer.audio.formats.flac.FLACFileReader;
import yanzhikai.simpleplayer.audio.formats.mp3.MP3FileReader;
import yanzhikai.simpleplayer.audio.formats.wav.WAVFileReader;

public class AudioUtil {
    private static ArrayList<AudioFileReader> readers = new ArrayList();


    static {
        readers.add(new MP3FileReader());
        readers.add(new APEFileReader());
        readers.add(new FLACFileReader());
        readers.add(new WAVFileReader());

    }

    public static AudioFileReader getAudioFileReaderByFilePath(String filePath) {
        String ext = getFileExt(filePath);
        for (AudioFileReader reader : readers) {
            if (reader.isFileSupported(ext)) {
                return reader;
            }
        }
        return null;
    }

    public static AudioFileReader getAudioFileReaderByFileExt(String fileExt) {
        fileExt = fileExt.toLowerCase();
        for (AudioFileReader reader : readers) {
            if (reader.isFileSupported(fileExt)) {
                return reader;
            }
        }
        return null;
    }


    private static String getFileExt(String filePath) {
        int pos = filePath.lastIndexOf(".");
        if (pos == -1) {
            return "";
        }
        return filePath.substring(pos + 1).toLowerCase();
    }
}
