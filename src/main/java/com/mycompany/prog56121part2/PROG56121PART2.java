package com.mycompany.prog56121part2;

// import necessary classes for input handling, file operations, and regex pattern matching
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.io.FileWriter;
import java.io.IOException;

public class PROG56121PART2 {

    static Scanner input = new Scanner(System.in);

    // ==================== Part 1: Registration/Login (unchanged) ====================

    public static boolean checkUserName(String username) {
        return username.contains("_") && username.length() <= 5;
    }

    public static boolean checkPasswordComplexity(String password) {
        String regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        return Pattern.matches(regex, password);
    }

    public static boolean checkCellPhoneNumber(String number) {
        String regex = "^\\+27\\d{9}$";
        return Pattern.matches(regex, number);
    }

    public static String registerUser(String username, String password) {
        if (!checkUserName(username)) {
            return "Username is not correctly formatted please ensure that your username contains an underscore and is no more than five characters in length.";
        }
        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted please ensure that your password contains at least eight characters, a capital letter, a number and a special character.";
        }
        return "Username and password successfully captured. User registered!";
    }

    public static boolean loginUser(String username, String password, String storedUsername, String storedPassword) {
        return username.equals(storedUsername) && password.equals(storedPassword);
    }

    public static String returnLoginStatus(boolean status) {
        if (status) {
            return "Login successful! Welcome back!";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }

    // ==================== Part 2: Sending Messages ====================

    // Running totals/storage for messages, shared across the whole program run
    static List<String> sentMessageIDs = new ArrayList<>();
    static List<String> sentMessageHashes = new ArrayList<>();
    static List<String> sentRecipients = new ArrayList<>();
    static List<String> sentMessageTexts = new ArrayList<>();

    static List<String[]> storedMessages = new ArrayList<>(); // for messages chosen "Store"

    static int messageCounter = 0; // 0-indexed count of all messages created this run

    /**
     * Generates a random 10-digit Message ID.
     */
    public static String generateMessageID() {
        Random rand = new Random();
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            id.append(rand.nextInt(10));
        }
        return id.toString();
    }

    /**
     * Ensures the Message ID is not more than ten characters.
     */
    public static boolean checkMessageID(String id) {
        return id != null && id.length() <= 10;
    }

    /**
     * Checks that the recipient cell number is correctly formatted
     * (reusing the same rule as Part 1's checkCellPhoneNumber, per the
     * brief's hint to reuse the method and tests from the last task).
     */
    public static String checkRecipientCell(String cell) {
        if (checkCellPhoneNumber(cell)) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
    }

    /**
     * Checks the message text is not more than 250 characters.
     */
    public static String checkMessageLength(String msg) {
        if (msg.length() <= 250) {
            return "Message ready to send.";
        } else {
            int over = msg.length() - 250;
            return "Message exceeds 250 characters by " + over + "; please reduce the size.";
        }
    }

    /**
     * Creates the Message Hash: first two digits of the Message ID, a
     * colon, the message number, a colon, and the first and last words
     * of the message (punctuation stripped), all in caps.
     * Example: createMessageHash("0012345678", 0, "Hi Mike, can you join
     * me for dinner tonight?") returns "00:0:HITONIGHT".
     */
    public static String createMessageHash(String id, int num, String msg) {
        String idPrefix = id.substring(0, 2);
        String[] words = msg.trim().split("\\s+");
        String first = stripPunctuation(words[0]).toUpperCase();
        String last = stripPunctuation(words[words.length - 1]).toUpperCase();
        return idPrefix + ":" + num + ":" + first + last;
    }

    private static String stripPunctuation(String word) {
        return word.replaceAll("[^a-zA-Z0-9]", "");
    }

    /**
     * Lets the user choose to send, store, or disregard the message.
     * choice: 1 = Send, 2 = Disregard, 3 = Store.
     */
    public static String sentMessage(int choice, String id, String hash, String recipient, String msg) {
        switch (choice) {
            case 1:
                sentMessageIDs.add(id);
                sentMessageHashes.add(hash);
                sentRecipients.add(recipient);
                sentMessageTexts.add(msg);
                return "Message successfully sent.";
            case 2:
                return "Press 0 to delete the message.";
            case 3:
                storedMessages.add(new String[]{id, hash, recipient, msg});
                return "Message successfully stored.";
            default:
                return "Invalid option selected.";
        }
    }

    /**
     * Returns all messages sent while the program is running, formatted
     * with Message ID, Message Hash, Recipient, and Message.
     */
    public static String printMessages() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sentMessageIDs.size(); i++) {
            sb.append("Message ID: ").append(sentMessageIDs.get(i)).append("\n")
              .append("Message Hash: ").append(sentMessageHashes.get(i)).append("\n")
              .append("Recipient: ").append(sentRecipients.get(i)).append("\n")
              .append("Message: ").append(sentMessageTexts.get(i)).append("\n")
              .append("------------------------\n");
        }
        return sb.toString();
    }

    /**
     * Returns the total number of messages successfully sent.
     */
    public static int returnTotalMessages() {
        return sentMessageIDs.size();
    }

    /**
     * Stores all "stored for later" messages to a JSON file.
     */
    public static String storeMessage() {
        try (FileWriter writer = new FileWriter("storedmessages.json")) {
            StringBuilder json = new StringBuilder();
            json.append("[\n");
            for (int i = 0; i < storedMessages.size(); i++) {
                String[] m = storedMessages.get(i);
                json.append("  {\n")
                    .append("    \"messageID\": \"").append(m[0]).append("\",\n")
                    .append("    \"messageHash\": \"").append(m[1]).append("\",\n")
                    .append("    \"recipient\": \"").append(m[2]).append("\",\n")
                    .append("    \"message\": \"").append(m[3].replace("\"", "\\\"")).append("\"\n")
                    .append("  }");
                if (i < storedMessages.size() - 1) {
                    json.append(",");
                }
                json.append("\n");
            }
            json.append("]\n");
            writer.write(json.toString());
            return "Messages successfully stored.";
        } catch (IOException e) {
            return "Failed to store messages: " + e.getMessage();
        }
    }

    // ==================== Main Method ====================

    public static void main(String[] args) {

        String storedUsername;
        String storedPassword;
        String cellPhone;

        // ---- Registration ----
        String username;
        while (true) {
            System.out.println("-----Registration-----");
            System.out.println("Enter Username (must contain '_' and max 5 chars): ");
            username = input.nextLine();
            if (checkUserName(username)) break;
            System.out.println("Username is not correctly formatted please ensure that your username contains an underscore and is no more than five characters in length.");
        }

        String password;
        while (true) {
            System.out.print("Enter Password (8+ chars, 1 capital, 1 number, 1 special char): ");
            password = input.nextLine();
            if (checkPasswordComplexity(password)) break;
            System.out.println("Password is not correctly formatted please ensure that your password contains at least eight characters, a capital letter, a number and a special character.");
        }

        String registerMessage = PROG56121PART2.registerUser(username, password);
        System.out.println(registerMessage);
        storedUsername = username;
        storedPassword = password;

        while (true) {
            System.out.print("Enter Cell Phone (+27 followed by 9 digits): ");
            cellPhone = input.nextLine();
            if (checkCellPhoneNumber(cellPhone)) {
                System.out.println("Cell phone number successfully added.");
                break;
            }
            System.out.println("cellphone number incorrectly formatted or does not contain international code.");
        }

        // ---- Login (users can only send messages once logged in) ----
        System.out.println("\nLogin");
        boolean loggedIn = false;
        while (!loggedIn) {
            System.out.print("Enter Username: ");
            String loginUsername = input.nextLine();

            System.out.print("Enter Password: ");
            String loginPassword = input.nextLine();

            loggedIn = PROG56121PART2.loginUser(loginUsername, loginPassword, storedUsername, storedPassword);
            System.out.println(PROG56121PART2.returnLoginStatus(loggedIn));
        }

        // ---- Part 2: QuickChat messaging ----
        System.out.println("\nWelcome to QuickChat.");

        System.out.print("How many messages would you like to enter? ");
        int numMessages = Integer.parseInt(input.nextLine());
        int messagesEntered = 0;

        boolean quit = false;
        while (!quit) {
            System.out.println("\nPlease choose an option:");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.print("Enter your choice: ");
            String choice = input.nextLine();

            switch (choice) {
                case "1":
                    if (messagesEntered >= numMessages) {
                        System.out.println("You have already entered your set number of messages (" + numMessages + ").");
                        break;
                    }

                    System.out.print("Enter recipient cell number (+27 followed by 9 digits): ");
                    String recipient = input.nextLine();

                    System.out.print("Enter your message: ");
                    String msg = input.nextLine();

                    String id = generateMessageID();
                    if (!checkMessageID(id)) {
                        System.out.println("Message ID error - please try again.");
                        break;
                    }
                    System.out.println("Message ID generated: " + id);

                    String cellResult = checkRecipientCell(recipient);
                    System.out.println(cellResult);
                    if (!cellResult.equals("Cell phone number successfully captured.")) {
                        break;
                    }

                    String lengthResult = checkMessageLength(msg);
                    System.out.println(lengthResult);
                    if (!lengthResult.equals("Message ready to send.")) {
                        break;
                    }

                    String hash = createMessageHash(id, messageCounter, msg);
                    messageCounter++;

                    System.out.println("\nChoose an option: 1) Send Message  2) Disregard Message  3) Store Message to send later");
                    System.out.print("Enter your choice: ");
                    int sendChoice = Integer.parseInt(input.nextLine());
                    String sendResult = sentMessage(sendChoice, id, hash, recipient, msg);
                    System.out.println(sendResult);

                    if (sendChoice == 3) {
                        System.out.println(storeMessage());
                    }

                    if (sendChoice == 1) {
                        messagesEntered++;
                        System.out.println("\n--- Message Details ---");
                        System.out.println("Message ID: " + id);
                        System.out.println("Message Hash: " + hash);
                        System.out.println("Recipient: " + recipient);
                        System.out.println("Message: " + msg);
                    }
                    break;

                case "2":
                    System.out.println("Coming Soon.");
                    break;

                case "3":
                    quit = true;
                    System.out.println("\nTotal messages sent: " + returnTotalMessages());
                    System.out.println("Goodbye!");
                    break;

                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }
}