package am.adrianyepremyan.flowgamesolver.map.printer;

import am.adrianyepremyan.flowgamesolver.map.GameMap;
import am.adrianyepremyan.flowgamesolver.map.domain.Flow;

public class DefaultGameMapPrinter implements GameMapPrinter {

    @Override
    public void print(GameMap map) {
        print(map.getMatrix());
    }

    @Override
    public void print(Flow[][] matrix) {
        for (Flow[] rows : matrix) {
            for (Flow cell : rows) {
                System.out.print((cell == null ? "*" : cell.color()).charAt(0));
                System.out.print("  ");
            }
            System.out.println();
        }
    }
}
