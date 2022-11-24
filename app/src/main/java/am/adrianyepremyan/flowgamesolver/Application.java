package am.adrianyepremyan.flowgamesolver;

import am.adrianyepremyan.flowgamesolver.helper.Point;
import am.adrianyepremyan.flowgamesolver.map.GameMap;
import am.adrianyepremyan.flowgamesolver.map.printer.DefaultGameMapPrinter;
import am.adrianyepremyan.flowgamesolver.solver.Solver;

public class Application {

    public static void main(String[] args) {
        final var map = new GameMap(5, 5);
        map.addInitialFlows("Red", new Point(0, 0), new Point(1, 4));
        map.addInitialFlows("Green", new Point(2, 0), new Point(1, 3));
        map.addInitialFlows("Blue", new Point(2, 1), new Point(2, 4));
        map.addInitialFlows("Yellow", new Point(4, 0), new Point(3, 3));
        map.addInitialFlows("Orange", new Point(4, 1), new Point(3, 4));

        final var mapPrinter = new DefaultGameMapPrinter();
        map.print(mapPrinter);

        final var solver = new Solver();
        solver.solve(map);

        System.out.println("Solution:");
        map.print(mapPrinter);
    }
}
