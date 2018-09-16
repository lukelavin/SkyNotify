import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Input {
    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        System.out.print("Enter your email: ");
        String email = in.nextLine();
        System.out.println(email);

        BufferedWriter writer = new BufferedWriter(new FileWriter("emails.txt", true));
        writer.write(email + "\n");
        writer.close();
    }
}
