import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static int n;
    private static ArrayList<ArrayList<Integer>> row_constraints;
    private static ArrayList<ArrayList<Integer>> col_constraints;

    public static void main(String[] args) {
        File file = new File("/media/arya/hdd6t/artificial-intelligence/csp/nonogram/inputs/input1.txt");
        Scanner scanner;
        try {
            scanner = new Scanner(file);
            n = scanner.nextInt();
            scanner.nextLine();
            row_constraints = new ArrayList<ArrayList<Integer>>(n);
            col_constraints = new ArrayList<ArrayList<Integer>>(n);

            for (int i = 0; i < n; i++) {
                String[] numbers = scanner.nextLine().split(" ");
                ArrayList<Integer> row = new ArrayList<>();
                for (String s : numbers) {
                    row.add(Integer.parseInt(s));
                }
                row_constraints.add(i, row);
            }

            for (int i = 0; i < n; i++) {
                String[] numbers = scanner.nextLine().split(" ");
                ArrayList<Integer> col = new ArrayList<>();
                for (String s : numbers) {
                    col.add(Integer.parseInt(s));
                }
                col_constraints.add(i, col);
            }

            scanner.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println(row_constraints.toString());
        System.out.println(col_constraints.toString());
        
    }
}


