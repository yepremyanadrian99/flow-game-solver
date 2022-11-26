package am.adrianyepremyan.flowgamesolver;

import am.adrianyepremyan.flowgamesolver.helper.Point;
import am.adrianyepremyan.flowgamesolver.map.GameMap;
import am.adrianyepremyan.flowgamesolver.map.printer.DefaultGameMapPrinter;
import am.adrianyepremyan.flowgamesolver.solver.Solver;
import java.util.function.Supplier;

public class Application {

    public static void main(String[] args) {
//        solveAndPrint(Application::mapExample5x5);
//        solveAndPrint(Application::mapExample7x7);
//        solveAndPrint(Application::mapExample8x8);
        solveAndPrint(Application::mapExample10x10);
    }

    private static void solveAndPrint(Supplier<GameMap> mapSupplier) {
        final var map = mapSupplier.get();

        System.out.println("Game map is:");
        final var mapPrinter = new DefaultGameMapPrinter();
        map.print(mapPrinter);

        final var solver = new Solver();
        final var solvedMatrix = solver.solve(map);

        System.out.println("Solution:");
        mapPrinter.print(solvedMatrix);
    }

    private static GameMap mapExample5x5() {
        final var map = new GameMap(5, 5);
        map.addInitialFlows("Red", new Point(0, 0), new Point(1, 4));
        map.addInitialFlows("Green", new Point(2, 0), new Point(1, 3));
        map.addInitialFlows("Blue", new Point(2, 1), new Point(2, 4));
        map.addInitialFlows("Yellow", new Point(4, 0), new Point(3, 3));
        map.addInitialFlows("Orange", new Point(4, 1), new Point(3, 4));
        return map;
    }

    private static GameMap mapExample7x7() {
        final var map = new GameMap(7, 7);
        map.addInitialFlows("Blue", new Point(0, 1), new Point(1, 2));
        map.addInitialFlows("Yellow", new Point(1, 1), new Point(2, 3));
        map.addInitialFlows("Orange", new Point(0, 2), new Point(3, 5));
        map.addInitialFlows("Red", new Point(0, 6), new Point(6, 2));
        map.addInitialFlows("Green", new Point(6, 1), new Point(5, 5));
        map.addInitialFlows("Cyan", new Point(5, 1), new Point(5, 4));
        return map;
    }

    private static GameMap mapExample8x8() {
        final var map = new GameMap(8, 8);
        map.addInitialFlows("Kanach", new Point(6, 0), new Point(3, 5));
        map.addInitialFlows("Indigo", new Point(2, 0), new Point(7, 4));
        map.addInitialFlows("Yellow", new Point(6, 1), new Point(4, 3));
        map.addInitialFlows("Orange", new Point(3, 0), new Point(5, 0));
        map.addInitialFlows("Cyan", new Point(2, 1), new Point(5, 1));
        map.addInitialFlows("Red", new Point(2, 2), new Point(2, 5));
        map.addInitialFlows("Pink", new Point(1, 1), new Point(6, 4));
        map.addInitialFlows("Blue", new Point(3, 2), new Point(4, 4));
        return map;
    }

    private static GameMap mapExample10x10() {
        final var map = new GameMap(10, 10);
        map.addInitialFlows("Kanach", new Point(0, 0), new Point(9, 0));
        map.addInitialFlows("Indigo", new Point(0, 2), new Point(7, 8));
        map.addInitialFlows("Fioletovi", new Point(0, 4), new Point(3, 2));
        map.addInitialFlows("Yellow", new Point(0, 5), new Point(5, 2));
        map.addInitialFlows("Orange", new Point(4, 4), new Point(5, 3));
        map.addInitialFlows("Cyan", new Point(2, 3), new Point(1, 4));
        map.addInitialFlows("Gray", new Point(0, 6), new Point(4, 6));
        map.addInitialFlows("Red", new Point(1, 6), new Point(3, 6));
        map.addInitialFlows("White", new Point(1, 7), new Point(3, 7));
        map.addInitialFlows("Pink", new Point(0, 9), new Point(7, 3));
        map.addInitialFlows("Blue", new Point(4, 5), new Point(9, 9));
        return map;
    }
}
