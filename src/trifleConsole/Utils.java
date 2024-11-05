package trifleConsole;

import java.io.*;

/**
 * Helper methods
 */
public class Utils {

    /**
     * Read a file and convert it to a String
     * @param f The input stream to read
     * @return The content of the file as a String
     * @throws IOException If an error occurs while reading the file
     */
    public static String readFile(File f) throws IOException {
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        br.close();
        fr.close();
        return sb.toString();
    }
}
