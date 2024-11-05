package minmax;

import trifleConsole.boardifier.view.ConsoleColor;

import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * This class is used by the MinMax for statistics
 */
public class Tracker {
    private final HashMap<Integer, Integer> numberOfNodesPerLayout;
    private final List<Double>              weightComputationTime;

    private boolean isStatisticsApiOn = true;

    private long buildTreeStart;
    private long buildTreeEnd;

    private long findPathStart;
    private long findPathEnd;

    public Tracker(){
        this.numberOfNodesPerLayout = new HashMap<>();
        this.weightComputationTime = new ArrayList<>();
    }

    public void startTreeBuilderTimer(){
        this.buildTreeStart = System.nanoTime();
    }
    public void endTreeBuilderTimer(){
        this.buildTreeEnd = System.nanoTime();
    }

    public void startFindPathTimer(){
        this.findPathStart = System.nanoTime();
    }
    public void endFindPathTimer(){
        this.findPathEnd = System.nanoTime();
    }

    public void newNode(int layout){
        if (!numberOfNodesPerLayout.containsKey(layout)) {
            numberOfNodesPerLayout.put(layout, 1);
        } else {
            numberOfNodesPerLayout.put(
                    layout,
                    numberOfNodesPerLayout.get(layout) + 1
            );
        }
    }

    public void newWeightTime(double nanoTime){
        weightComputationTime.add(nanoTime);
    }

    public HashMap<Integer, Integer> getNumberOfNodesPerLayout() {
        return numberOfNodesPerLayout;
    }

    public List<Double> getWeightComputationTime() {
        return weightComputationTime;
    }
    public long getBuildTreeTime() {
        return buildTreeEnd - buildTreeStart;
    }
    public long getFindPathTime() {
        return findPathEnd - findPathStart;
    }

    public void reset(){
        this.numberOfNodesPerLayout.clear();
        this.weightComputationTime.clear();

        this.buildTreeStart = 0;
        this.buildTreeEnd = 0;

        this.findPathStart = 0;
        this.findPathEnd = 0;
    }

    public long getTotalWeightTime(){
        return getWeightComputationTime().stream()
                .map(n -> (long) (double) n)
                .reduce(Long::sum)
                .orElse(0L) / (long) 1e6;
    }

    public int getTotalNumberOfNodes(){
        return getNumberOfNodesPerLayout().values()
                .stream()
                .reduce(Integer::sum)
                .orElse(0);
    }

    public void displayStatistics(){
        System.out.println();
        System.out.println("MinMax statistics:");
        System.out.println("  Number of nodes:   " + getTotalNumberOfNodes());
        System.out.println("  Number of layouts: " + numberOfNodesPerLayout.size());
        System.out.println();

        System.out.println("  Time taken to build tree:       " + formatTime(getBuildTreeTime()));
        System.out.println("  Time taken to find better move: " + formatTime(getFindPathTime()));
        System.out.println("  Total time:                     " + formatTime(getBuildTreeTime() + getFindPathTime()));
        System.out.println();

        long totalWeightTime = getTotalWeightTime();

        System.out.println("  Total time to calculate all weights: " + formatTime(totalWeightTime));
        System.out.println("  Median time to calculate weight:     "
                + formatTime((totalWeightTime / getWeightComputationTime().size())));
        System.out.println();
    }

    public String getJsonData(){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"layouts\": {");
        List<Map.Entry<Integer, Integer>> entries = getNumberOfNodesPerLayout().entrySet().stream().toList();
        for (int i = 0; i < entries.size(); i++) {
            Map.Entry<Integer, Integer> entry = entries.get(i);

            sb.append('"')
                    .append(entry.getKey())
                    .append("\":")
                    .append(entry.getValue());

            if (i < entries.size() - 1) sb.append(",");
        }
        sb.append("},");

        sb.append("\"weightComputationTime\": [");
        for (int i = 0; i < weightComputationTime.size(); i++) {
            Double weight = weightComputationTime.get(i);
            sb.append(weight.toString());
            if (i < weightComputationTime.size() - 1) sb.append(",");
        }
        sb.append("]");

        sb.append(",\"buildTreeStart\": ").append(buildTreeStart);
        sb.append(",\"buildTreeEnd\": ").append(buildTreeEnd);
        sb.append(",\"findPathStart\": ").append(findPathStart);
        sb.append(",\"findPathEnd\": ").append(findPathEnd);

        sb.append('}');

        return sb.toString();
    }

    /**
     * Format the given time
     * @param nanoseconds The time in nanoseconds
     * @return The formatted time
     */
    public static String formatTime(long nanoseconds) {
        long hours = nanoseconds / (3600L * 1_000_000_000L);
        long minutes = (nanoseconds % (3600L * 1_000_000_000L)) / (60L * 1_000_000_000L);
        long seconds = (nanoseconds % (60L * 1_000_000_000L)) / (1_000_000_000L);
        long milliseconds = (nanoseconds % (1_000_000_000L)) / 1_000_000L;
        long remainingNanos = nanoseconds % 1_000_000L;

        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours).append("h ");
        }
        if (minutes > 0) {
            sb.append(minutes).append("m ");
        }
        if (seconds > 0) {
            sb.append(seconds).append("s ");
        }
        if (milliseconds > 0) {
            sb.append(milliseconds);
            if (remainingNanos > 0 || sb.isEmpty()) {
                sb.append('.').append(remainingNanos % 10000);
            }

            sb.append("ms ");
        }

        if (sb.isEmpty() && remainingNanos > 0) {
            sb.append("0.").append(remainingNanos % 10000).append("ms");
        }

        return sb.toString().trim();
    }

    public void sendStatisticsToApi(){
        if (!isStatisticsApiOn)
            return;

        System.out.println("Sending statistics to API...");
        try {
            HttpURLConnection con = getHttpURLConnection();

            switch (con.getResponseCode()) {
                case 200: {
                    System.out.println("Statistics sent");
                    break;
                }
                case 404: {
                    System.out.println(ConsoleColor.RED + "This bot is not registered on the API side. No more requests will be sent to the API during this game." + ConsoleColor.RESET);
                    isStatisticsApiOn = false;
                    break;
                }
                case 500: {
                    System.out.println(ConsoleColor.RED + "The API got an error which resulted in code 500. No more requests will be sent to the API during this game." + ConsoleColor.RESET);
                    isStatisticsApiOn = false;
                    break;
                }
                case 400: {
                    System.out.println(ConsoleColor.RED + "The API responded with a code 400, please contact the developers." + ConsoleColor.RESET);
                    isStatisticsApiOn = false;
                    break;
                }
                default: {
                    System.out.println(ConsoleColor.RED + "The API responded with the code " + con.getResponseCode() + " which wasn't expected." + ConsoleColor.RESET);
                    isStatisticsApiOn = false;
                }
            }

        }
        catch (ConnectException e) {
            System.out.println(ConsoleColor.RED + "Cannot connect to the statistics API: " + e.getMessage());
            System.out.println("Statistics will not be sent for the rest of this game" + ConsoleColor.RESET);
            isStatisticsApiOn = false;
        }
        catch(Exception e) {
            System.out.print(ConsoleColor.RED);
            System.out.println(e.getMessage());
            e.printStackTrace();

            System.out.println("Statistics will not be sent for the rest of this game" + ConsoleColor.RESET);

            isStatisticsApiOn = false;
        }
    }

    private HttpURLConnection getHttpURLConnection() throws IOException {
        URL url = new URL("http://localhost:8080/minimax");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");

        // 2s so that the game is not blocked for a little too long
        con.setConnectTimeout(2000);
        con.setDoOutput(true);

        // Write the body
        OutputStream os = con.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
        String json = getJsonData();
        osw.write(json);
        osw.flush();
        osw.close();
        os.close();

        // Connect to the socket
        con.connect();
        return con;
    }
}
