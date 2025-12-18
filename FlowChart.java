import java.util.Scanner;

public class FlowChart {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n=== Real World Scenarios Menu ===");
            System.out.println("1. Voting Age Eligibility");
            System.out.println("2. Loan Eligibility");
            System.out.println("3. Traffic Light System");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    votingAge(sc);
                    break;
                case 2:
                    loanEligibility(sc);
                    break;
                case 3:
                    trafficLight(sc);
                    break;
                case 4:
                    System.out.println("Exiting program. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 4);

        sc.close();
    }

    public static void votingAge(Scanner sc) {
        System.out.print("Enter your age: ");
        int age = sc.nextInt();

        if (age >= 18) {
            System.out.println("You are eligible to vote.");
        } else {
            System.out.println("You are NOT eligible to vote.");
        }
    }

    public static void loanEligibility(Scanner sc) {
        System.out.print("Enter your age: ");
        int age = sc.nextInt();

        System.out.print("Enter your monthly income: ");
        double income = sc.nextDouble();

        System.out.print("Enter your credit score: ");
        int creditScore = sc.nextInt();

        if (age < 21) {
            System.out.println("Not eligible: Age must be at least 21.");
        } else if (income < 25000) { // Example threshold
            System.out.println("Not eligible: Income must be at least 25,000.");
        } else if (creditScore < 650) {
            System.out.println("Not eligible: Credit score must be at least 650.");
        } else {
            System.out.println("Congratulations! You are eligible for the loan.");
        }
    }

    public static void trafficLight(Scanner sc) {
        System.out.print("Enter the traffic light color (Red/Yellow/Green): ");
        String light = sc.next();

        if (light.equalsIgnoreCase("Red")) {
            System.out.println("Stop");
        } else if (light.equalsIgnoreCase("Yellow")) {
            System.out.println("Get Ready");
        } else if (light.equalsIgnoreCase("Green")) {
            System.out.println("Go");
        } else {
            System.out.println("Invalid color");
        }
    }
}