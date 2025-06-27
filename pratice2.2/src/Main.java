import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DiaryApplications diary = new DiaryApplications();

        System.out.println("Ласкаво просимо до щоденника!");
        diary.run(scanner);

        scanner.close();
    }
}