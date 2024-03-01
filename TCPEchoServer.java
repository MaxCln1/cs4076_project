import java.io.*;
import java.net.*;
import java.util.*;

public class TCPEchoServer {
    private static ServerSocket servSock;
    private static final int PORT = 6578;
    private static Map<String, List<String>> classSchedules = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("Opening the port...\n");
        try {
            servSock = new ServerSocket(PORT);
        } catch (IOException e) {
            System.out.println("Unable to attach to the port!");
            System.exit(1);
        }

        do {
            run();
        } while (true);
    }

    private static void run() {
        Socket link = null;
        try {
            link = servSock.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
            PrintWriter out = new PrintWriter(link.getOutputStream(), true);
            String requestType = in.readLine();

            switch (requestType) {
                case "Add Class":
                    handleAddClassRequest(in, out);
                    break;
                case "Remove Class":
                    handleRemoveClassRequest(in, out);
                    break;
                case "Display Schedule":
                    handleDisplayScheduleRequest(in, out);
                    break;
                default:
                    out.println("Invalid request type");
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                link.close();
            } catch (IOException e) {
                System.out.println("Unable to disconnect!");
                System.exit(1);
            }
        }
    }

    private static void handleAddClassRequest(BufferedReader in, PrintWriter out) throws IOException {
        String className = in.readLine();
        String classTime = in.readLine();

        if (checkForClash(className, classTime)) {
            out.println("Clash in scheduling. Cannot add a class.");
        } else {
            bookClass(className, classTime);
            out.println("Class is scheduled successfully.");
        }
    }

    private static boolean checkForClash(String className, String classTime) {
        List<String> schedule = classSchedules.getOrDefault(className, new ArrayList<>());
        return schedule.contains(classTime);
    }

    private static void bookClass(String className, String classTime) {
        List<String> schedule = classSchedules.computeIfAbsent(className, k -> new ArrayList<>());
        schedule.add(classTime);
    }

    private static void handleRemoveClassRequest(BufferedReader in, PrintWriter out) throws IOException {
        String className = in.readLine();
        String classTime = in.readLine();

        String freedTimeSlot = removeClass(className, classTime);

        if (freedTimeSlot != null) {
            out.println("Class is removed successfully. Time slot is now free: " + freedTimeSlot);
        } else {
            out.println("Class is not found. Unable to remove the class.");
        }
    }

    private static String removeClass(String className, String classTime) {
        List<String> schedule = classSchedules.get(className);
        if (schedule != null && schedule.remove(classTime)) {
            return classTime;
        }
        return null;
    }

    private static void handleDisplayScheduleRequest(BufferedReader in, PrintWriter out) throws IOException {
        String className = in.readLine();

        displaySchedule(className, out);
    }

    private static void displaySchedule(String className, PrintWriter out) {
        List<String> schedule = classSchedules.getOrDefault(className, new ArrayList<>());
        out.println("Class Schedule for " + className + ":");
        for (String classTime : schedule) {
            out.println(classTime);
        }
    }
}
