import java.util.Scanner;

public class calculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your marks (0-100): ");
        try {
            double marks = scanner.nextDouble();

            if (marks < 0 || marks > 100) {
                System.out.println("Marks must be between 0 and 100.");
            } else {
                String grade = calculateGrade(marks);
                System.out.println("Your grade is: " + grade);
            }
        } catch (Exception e) {
            System.out.println("Please enter a valid number.");
        } finally {
            scanner.close();
        }
    }

    public static String calculateGrade(double marks) {
        if (marks >= 90) {
            return "A";
        } else if (marks >= 80) {
            return "B";
        } else if (marks >= 70) {
            return "C";
        } else if (marks >= 50) {
            return "D";
        } else {
            return "Fail";
        }
    }
}