import java.util.Scanner;
public class SavingPlanner {
    private static final int savingsPlan = 1;
    private static final int monthCalculation = 2;
    private static final int exitProgram = 3;

    public static void printMenu() {
        System.out.println("Select an option:");
        System.out.println("For Monthly Savings Plan, Enter 1");
        System.out.println("For Month calculation, Enter 2");
        System.out.println("To Exit Savings Planner, Enter 3\n");
    }

    public static void main(String[] args) {
        Scanner h = new Scanner(System.in);
        int choice;
        System.out.println("===WELCOME TO SAVINGS PLANNER===");
        String msg = "Enter your choice: ";
        do {
            printMenu();
            choice = getMenuChoice(h);
            switch (choice) {
                case savingsPlan:
                    savingsPlanner(h);
                    break;
                case monthCalculation:
                    monthCalculator(h);
                    break;
                case exitProgram:
                    System.out.println("Thank you For Choosing Savings Planner!");
                    break;
                default:
                    System.out.println("Invalid Choice Entered");
            }
        } while (choice != exitProgram);
        System.out.println("===Goodbye!===");
        h.close();
    }

    public static void savingsPlanner(Scanner h) {
        double initialAmount = getPositiveDouble(h, "Enter the current amount you are saving on a monthly basis (PKR): ");
        double targetAmount = getPositiveDouble(h, "Enter your target savings amount (PKR): ");
        int months = getPositiveInt("Enter the number of months you want to achieve this target in: ", h);

        if (months == 1) {
            if (initialAmount >= targetAmount) {
                System.out.println("Your target is already achieved in 1st Month!");
            } else {
                System.out.println("Your target cannot be achieved in 1 month!");
            }
            return;
        }
        if (months * initialAmount >= targetAmount) {
            System.out.println("You will reach your target without increasing your monthly savings");
            APTable(initialAmount, months, 0);
            return;
        }
        double difference = ((2 * targetAmount) / months - 2 * initialAmount) / (months - 1);
        System.out.println("\n--- Savings Recommendation ---");
        System.out.printf("Recommended monthly increase: PKR %.2f\n", difference);
        APTable(initialAmount, months, difference);
    }

    public static void APTable(double a, int n, double d) {
        System.out.println("Your Monthly Savings Plan");
        double sum = 0.0;
        double term = a;
        System.out.printf("%-10s %-20s %-20s\n", "Month", "Monthly Saving (PKR)", "Total Savings (PKR)");

        for (int i = 1; i <= n; i++) {
            sum += term;
            System.out.printf("%-10d %-20.2f %-20.2f\n", i, term, sum);
            term += d;
        }
        if (d > 0) {
            System.out.printf("\nYou must increase your savings by PKR %.2f every month to reach the target.\n\n", d);
        }
    }

    public static void monthCalculator(Scanner h) {

        double a = getPositiveDouble(h, "Enter your current monthly savings (PKR): ");
        double d = getPositiveDouble(h, "Enter monthly increase in saving (PKR): ");
        double target = getPositiveDouble(h, "Enter target amount (PKR): ");

        double sum = 0.0;
        double savings = a;
        int month = 0;

        if (d == 0 && a < target) {
            System.out.println("Target cannot be achieved without increasing monthly savings.");
            return;
        }
        System.out.println("Savings Predictor table");
        System.out.printf("%-10s %-20s %-20s\n", "Month", "Monthly Saving (PKR)", "Total Savings (PKR)");
        while (sum < target && month <= 600) {
            month++;
            sum += savings;
            System.out.printf("%-10d %-20.2f %-20.2f\n", month, savings, sum);
            savings += d;
        }
        if (sum >= target) {
            System.out.printf("You will reach your target in %d months.\n", month);
            System.out.printf("Your Total savings at that time: PKR %.2f\n", sum);
        } else {
            System.out.println("Target not achievable within a reasonable time frame.");
        }
    }

    public static int getMenuChoice(Scanner h) {
        while (true) {
            System.out.print("Enter your choice: ");
            if (h.hasNextInt()) {
                int choice = h.nextInt();
                if (choice >= 1 && choice <= 3)
                    return choice;
                System.out.println("Please enter 1, 2, or 3 only.");
            } else {
                System.out.println("Invalid input.");
                h.next();
            }
        }
    }

    public static int getPositiveInt(String msg, Scanner h) {
        while(true){
            System.out.print(msg);
            if (h.hasNextInt()) {
                int value = h.nextInt();
                if (value > 0) return value;
                System.out.println("Enter a positive integer.");
            } else {
                System.out.println("Invalid input.");
                h.next();
            }
        }
    }
    public static double getPositiveDouble(Scanner h, String msg) {
        while (true) {
            System.out.print(msg);
            if (h.hasNextDouble()) {
                double value = h.nextDouble();
                if (value > 0)
                    return value;
                System.out.println("Enter a positive Value");
            } else {
                System.out.println("Try again! ");
                h.next();
            }
        }
    }
}