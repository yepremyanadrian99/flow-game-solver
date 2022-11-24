package am.adrianyepremyan.flowgamesolver.solver;

import am.adrianyepremyan.flowgamesolver.helper.Point;
import am.adrianyepremyan.flowgamesolver.map.GameMap;
import am.adrianyepremyan.flowgamesolver.map.domain.Flow;
import am.adrianyepremyan.flowgamesolver.map.domain.FlowDirection;
import am.adrianyepremyan.flowgamesolver.map.domain.InitialFlow;
import am.adrianyepremyan.flowgamesolver.map.printer.DefaultGameMapPrinter;
import am.adrianyepremyan.flowgamesolver.map.printer.GameMapPrinter;
import java.util.List;

public class Solver {

    private static final GameMapPrinter printer = new DefaultGameMapPrinter();

    public void solve(GameMap map) {
        final var matrix = map.getMatrix();
        final var initialFlow = map.getInitialFlowList().get(0);

        if (!solveRecursively(map, matrix[initialFlow.p1().y()][initialFlow.p1().x()], map.getInitialFlowList(), 0)) {
            throw new RuntimeException("Game has no solution!");
        }
    }

    private boolean solveRecursively(GameMap map, Flow flow,
                                     List<InitialFlow> initialFlowList,
                                     int initialFlowIndex) {
        System.out.println("Solving recursively for flow: " + flow);
        printer.print(map);

        final int startX = flow.getPoint().x();
        final int startY = flow.getPoint().y();

        return solveWithDirection(map, flow, initialFlowList, initialFlowIndex, startX, startY - 1, FlowDirection.UP)
            || solveWithDirection(map, flow, initialFlowList, initialFlowIndex, startX, startY + 1, FlowDirection.DOWN)
            || solveWithDirection(map, flow, initialFlowList, initialFlowIndex, startX - 1, startY, FlowDirection.LEFT)
            ||
            solveWithDirection(map, flow, initialFlowList, initialFlowIndex, startX + 1, startY, FlowDirection.RIGHT);
    }

    private boolean solveWithDirection(GameMap map,
                                       Flow currentFlow,
                                       List<InitialFlow> initialFlowList,
                                       int initialFlowIndex,
                                       int x, int y,
                                       FlowDirection directionToGo) {
        final var matrix = map.getMatrix();
        final var initialFlow = initialFlowList.get(initialFlowIndex);

        // If the end of the initial flow is reached
        // Change the initial flow
        if (initialFlow.p2().x() == x && initialFlow.p2().y() == y) {
            System.out.println("The color's end is reached!!");
            if (initialFlowIndex == initialFlowList.size() - 1) {
                System.out.println("Game is solved!!!");
                return true;
            }
            final var nextInitialFlow = initialFlowList.get(initialFlowIndex + 1);
            final var nextFlow = matrix[nextInitialFlow.p1().y()][nextInitialFlow.p1().x()];

            return solveRecursively(map, nextFlow, initialFlowList, initialFlowIndex + 1);
        }

        if ((currentFlow.getDirection() == null || directionToGo != currentFlow.getDirection().getOpposite())
            && y >= 0 && y < matrix.length
            && x >= 0 && x < matrix[y].length) {
            // Insert the new flow with the provided direction
            final var insertedFlow = insertFlowWithDirection(matrix, x, y, currentFlow.getColor(), directionToGo);
            if (insertedFlow != null) {
                // Solve recursively with the new flow
                if (solveRecursively(map, insertedFlow, initialFlowList, initialFlowIndex)) {
                    return true;
                }
                // Revert the step
                matrix[y][x] = null;
            }
        }
        return false;
    }

    private boolean isGameSolved(GameMap map) {
        // TODO: To be implemented
//        final var matrix = map.getMatrix();
//        if (Arrays.stream(matrix).flatMap(Arrays::stream).anyMatch(Objects::isNull)) {
//            return false;
//        }
        return false;
    }

    private Flow insertFlowWithDirection(Flow[][] matrix, int x, int y,
                                         String color,
                                         FlowDirection direction) {
        // Return null to indicate that the cell is already occupied.
        if (matrix[y][x] != null) {
            return null;
        }

        return matrix[y][x] = new Flow(new Point(x, y), color, direction);
    }
}
