import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Remove {
    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter your email:");
        String email = in.nextLine();

        BufferedReader reader = new BufferedReader(new FileReader("emails.txt"));
        ArrayList<String> lines = new ArrayList<String>();
        for(Object line : reader.lines().toArray()) {
            if (!((String) line).equals(email)) {
                lines.add((String) line);
            }
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter("emails.txt"));
        for(String line : lines) {
            writer.write(line +"\n");
        }
        writer.close();
    }
}
