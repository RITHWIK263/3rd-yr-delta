public class StudentManualSave {


    public static void main(String[] args) {

        String[] names = {"Amit", "Neha", "Rahul"};
        int[] ages = {20, 21, 19};
        int[] marks = {85, 90, 78};

        int total = 0;

        for (int i = 0; i < marks.length; i++) {
            total += marks[i];
        }

        double average = total / 3.0;

        System.out.println("===== Student Records =====");
        System.out.printf("%-10s %-5s %-6s\n", "Name", "Age", "Marks");
        System.out.println("----------------------------");

        for (int i = 0; i < names.length; i++) {
            System.out.printf("%-10s %-5d %-6d\n",
                    names[i], ages[i], marks[i]);
        }

        System.out.println("----------------------------");
        System.out.printf("Average Marks: %.2f\n", average);
    }
}
