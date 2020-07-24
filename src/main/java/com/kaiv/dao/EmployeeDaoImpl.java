package com.kaiv.dao;

import com.sun.jna.platform.win32.Kernel32;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
public class EmployeeDaoImpl implements EmployeeDao {

    private static final Kernel32 KERNEL32 = Kernel32.INSTANCE;

    boolean isPCReachable(String computerName) {

        boolean isReachable = false;

        try {
            isReachable = InetAddress.getByName(computerName).isReachable(1500);
        } catch (IOException e) {
        }

        return isReachable;
    }

    @Override
    public String getCurrentlyLoggedUser(String computerName) {

        String loggedUserName = "loggedUserNotFound";

        if (isPCReachable(computerName)) {
            try {

                String psLoggedonPath = new File(URLDecoder.decode(getClass().getClassLoader().getResource("").getPath() + File.separatorChar + "PsLoggedon.exe", "UTF-8")).getPath();
                String loggedUserCommand = psLoggedonPath + " \\\\" + computerName + " -l";
                ArrayList<String> foundAlInfo = this.retrieveDataFromQueryWithDelay(loggedUserCommand, 3);

                if (foundAlInfo != null && !foundAlInfo.isEmpty()) {

                    for (String oneLine : foundAlInfo) {

                        if (!oneLine.contains("logged on") && !oneLine.contains("unknown time") && (oneLine.contains("LEONI\\") || oneLine.contains(":"))) {

                            if (oneLine.contains("LEONI\\")) {
                                loggedUserName = oneLine.split("LEONI\\\\")[1];
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return loggedUserName;
    }


    ArrayList<String> retrieveDataFromQueryWithDelay(String command, int delayTime) {
        ArrayList<String> dataFromQueryList = new ArrayList<>();

        try {
            String defCodepage = "cp" + Integer.toString(KERNEL32.GetConsoleOutputCP());
            Process p = Runtime.getRuntime().exec(command);
            if (!p.waitFor(delayTime, TimeUnit.SECONDS)) {
                p.destroy();
            }

            SequenceInputStream inputStream = new SequenceInputStream(p.getInputStream(), p.getErrorStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, defCodepage));

            String line = "";
            String allLine;

            while ((allLine = reader.readLine()) != null) {
                line = allLine.trim();
                if (!line.isEmpty()) {
                    dataFromQueryList.add(line);
                    if (line.contains("Uptime:") && command.contains("PsInfo")) {
                        return dataFromQueryList;
                    }
                }
            }
            if (reader != null) {
                reader.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataFromQueryList;
    }


    @Override
    public List<String> processCommand(String getCommand, String searchString) {
        List<String> outputList = new LinkedList<>();

        switch (getCommand) {

            case "getGroupsByUser":
                String commandGetGroupsByUser = "cmd /c dsquery user -samid " + searchString + " | dsget user -memberof | dsget group -samid";
                outputList = retrieveDataFromQuery(commandGetGroupsByUser);
                break;

            case "getUsersByGroup":
                String commandGetUsersByGroup = "cmd /c dsquery group -name " + searchString + " | dsget group -members -expand |dsget user -fn -ln -c -samid";
                outputList = retrieveDataFromQuery(commandGetUsersByGroup);
                break;
        }

        return outputList;
    }


    ArrayList<String> retrieveDataFromQuery(String command) {
        ArrayList<String> dataFromQueryList = new ArrayList<>();
        String defCodepage = "cp" + Integer.toString(KERNEL32.GetConsoleOutputCP());

        try {
            Process p = Runtime.getRuntime().exec(command);

            SequenceInputStream inputStream = new SequenceInputStream(p.getInputStream(), p.getErrorStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, defCodepage));

            String line = "";
            String allLine;

            while ((allLine = reader.readLine()) != null) {
                line = allLine.trim();

                if (!line.isEmpty() && !line.contains("samid") && !line.contains("type dsmod /? for help")) {
                    if (command.contains("-addmbr")) {
                        dataFromQueryList.add(line);
                    } else if (!line.contains("succeeded")) {
                        dataFromQueryList.add(line);
                    } else if (command.contains("-disabled no")) {
                        dataFromQueryList.add(line);
                    }

                    if (line.contains("Uptime:") && command.contains("PsInfo")) {
                        return dataFromQueryList;
                    }
                }
            }
            if (reader != null) {
                reader.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataFromQueryList;
    }
}