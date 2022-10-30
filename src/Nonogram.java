import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;

public class Nonogram {

    private State state;
    private int n;
    ArrayList<ArrayList<Integer>> row_constraints;
    ArrayList<ArrayList<Integer>> col_constraints;

    public Nonogram(State state, 
        ArrayList<ArrayList<Integer>> row_constraints, 
        ArrayList<ArrayList<Integer>> col_constraints) {
        this.state = state;
        this.n = state.getN();
        this.row_constraints = row_constraints;
        this.col_constraints = col_constraints;
    }


    public void start() {
        long tStart = System.nanoTime();
        backtrack(state);
        long tEnd = System.nanoTime();
        System.out.println("Total time: " + (tEnd - tStart)/1000000000.000000000);
    }

    private boolean backtrack(State state) {

        if (isFinished(state)) {

            System.out.println("Result Board: \n");
            state.printBoard();
            return true;
        }
        if (allAssigned(state)) {
            return false;
        }

        int[] mrvRes = MRV(state);
        for (String s : LCV(state, mrvRes)) {
            State newState = state.copy();
            newState.setIndexBoard(mrvRes[0], mrvRes[1], s);
            newState.removeIndexDomain(mrvRes[0], mrvRes[1], s);
            if (!isConsistent(newState)) {
                continue;
            }
            updateDomains(mrvRes[0], mrvRes[1], newState);
            if (backtrack(newState)) {
                return true;
            }
        }

        return false;
    }

    private void updateDomains(int i, int j, State newState) {
        int sum_row = 0;
        int sum_col = 0;
        int sumRowFill = 0;
        int sumColFill = 0;

        for (int counter = 0; counter < row_constraints.get(i).size(); counter++) {
            sum_row += row_constraints.get(i).get(counter);
        }
        for (int counter = 0; counter < col_constraints.get(j).size(); counter++) {
            sum_col += col_constraints.get(j).get(counter);
        }

        for (int row = 0; row < n; row++) {
            if (newState.getBoard().get(row).get(j).equals("F")) {
                sumColFill++;
            }
        }

        for (int col = 0; col < n; col++) {
            if (newState.getBoard().get(i).get(col).equals("F")) {
                sumRowFill++;
            }
        }

        if (sumColFill == sum_col) {
            for (int row = 0; row < n; row++){
                if (!newState.getBoard().get(row).get(j).equals("F")) {
                    newState.setIndexBoard(row, j, "X");
                    newState.removeIndexDomain(row, j,"F");
                    newState.removeIndexDomain(row, j,"X");
                }
            }
        }

        if (sumRowFill == sum_row) {
            for (int col = 0; col < n; col++){
                if (!newState.getBoard().get(i).get(col).equals("F")) {
                    newState.setIndexBoard(i, col, "X");
                    newState.removeIndexDomain(i, col,"F");
                    newState.removeIndexDomain(i, col,"X");
                }
            }
        }
    }

    private ArrayList<String> LCV (State state, int[] var) {
        return state.getDomain().get(var[0]).get(var[1]);
    } 

    private int[] MRV (State state) {
        ArrayList<ArrayList<String>> cBoard = state.getBoard();
        ArrayList<ArrayList<ArrayList<String>>> cDomain = state.getDomain();

        int min = Integer.MAX_VALUE;
        int[] result = new int[2];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (cBoard.get(i).get(j).equals("E")) {
                    int val = cDomain.get(i).get(j).size();
                    if (val < min) {
                        min = val;
                        result[0] = i;
                        result[1] = j;
                    }
                }
            }
        }
        return result;
    }

    private boolean allAssigned(State state) {
        ArrayList<ArrayList<String>> cBoard = state.getBoard();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                String s = cBoard.get(i).get(j);
                if (s.equals("E"))
                    return false;
            }
        }
        return true;
    }

    private boolean isConsistent(State state) {

        ArrayList<ArrayList<String>> cBoard = state.getBoard();
        //check row constraints
        for (int i = 0; i < n; i++) {
            int sum = 0;
            for (int x : row_constraints.get(i)) {
                sum += x;
            }
            int count_f = 0;
            int count_e = 0;
            int count_x = 0;
            for (int j = 0; j < n; j++) {
                if (cBoard.get(i).get(j).equals("F")) {
                    count_f++;
                } else if (cBoard.get(i).get(j).equals("E")) {
                    count_e++;
                } else if (cBoard.get(i).get(j).equals("X")) {
                    count_x++;
                }
            }

            if (count_x > n - sum) {
                return false;
            }
            if (count_f != sum && count_e == 0) {
                return false;
            }

            Queue<Integer> constraints = new LinkedList<>();
            constraints.addAll(row_constraints.get(i));
            int count = 0;
            boolean flag = false;
            for (int j = 0; j < n; j++) {
                if (cBoard.get(i).get(j).equals("F")) {
                    flag = true;
                    count++;
                } else if (cBoard.get(i).get(j).equals("E")) {
                    break;
                } else if (cBoard.get(i).get(j).equals("X")) {
                    if (flag) {
                        flag = false;
                        if (!constraints.isEmpty()){
                            if (count != constraints.peek()) {
                                return false;
                            }
                            constraints.remove();
                        }
                        count = 0;
                    }
                }
            }

        }

        //check col constraints

        for (int j = 0; j < n; j++) {
            int sum = 0;
            for (int x : col_constraints.get(j)) {
                sum += x;
            }
            int count_f = 0;
            int count_e = 0;
            int count_x = 0;
            for (int i = 0; i < n; i++) {
                if (cBoard.get(i).get(j).equals("F")) {
                    count_f++;
                } else if (cBoard.get(i).get(j).equals("E")) {
                    count_e++;
                } else if (cBoard.get(i).get(j).equals("X")) {
                    count_x++;
                }
            }
            if (count_x > n - sum) {
                return false;
            }
            if (count_f != sum && count_e == 0) {
                return false;
            }

            Queue<Integer> constraints = new LinkedList<>();
            constraints.addAll(col_constraints.get(j));
            int count = 0;
            boolean flag = false;
            for (int i = 0; i < n; i++) {
                if (cBoard.get(i).get(j).equals("F")) {
                    flag = true;
                    count++;
                } else if (cBoard.get(i).get(j).equals("E")) {
                    break;
                } else if (cBoard.get(i).get(j).equals("X")) {
                    if (flag) {
                        flag = false;
                        if (!constraints.isEmpty()){
                            if (count != constraints.peek()) {
                                return false;
                            }
                            constraints.remove();
                        }
                        count = 0;
                    }
                }
            }
        }
        return true;
    }

    private boolean isFinished(State state) {
        return allAssigned(state) && isConsistent(state);
    }

}
