package am.adrianyepremyan.flowgamesolver.helper;

import am.adrianyepremyan.flowgamesolver.map.GameMap;
import am.adrianyepremyan.flowgamesolver.map.domain.Flow;
import am.adrianyepremyan.flowgamesolver.map.domain.FlowDirection;
import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameUtils {

    public static boolean flowsHaveNoConnectionRecursive(Flow[][] matrix,
                                                         Boolean[][] visited,
                                                         int x, int y,
                                                         Flow destination) {
        // Destination flow is reached
        if (x == destination.x() && y == destination.y()) {
            return false;
        }

        if (y >= 0 && y < matrix.length && x >= 0 && x < matrix[y].length) {
            // If the cell has already been traversed, then cancel the traversal
            if (visited[y][x] != null) {
                return true;
            }

            visited[y][x] = true;
            // If the cell is blocked with another color, then cancel the traversal
            if (matrix[y][x] != null && !matrix[y][x].color().equals(destination.color())) {
                return true;
            }

            return flowsHaveNoConnectionRecursive(matrix, visited, x, y - 1, destination)
                && flowsHaveNoConnectionRecursive(matrix, visited, x, y + 1, destination)
                && flowsHaveNoConnectionRecursive(matrix, visited, x - 1, y, destination)
                && flowsHaveNoConnectionRecursive(matrix, visited, x + 1, y, destination);
        }

        // If x and y are invalid, then there's no way for the flow to move
        return true;
    }

    public static Flow[][] copyMatrix(Flow[][] matrix) {
        final var newMatrix = new Flow[matrix.length][];
        for (int i = 0; i < matrix.length; ++i) {
            newMatrix[i] = Arrays.copyOf(matrix[i], matrix[i].length);
        }
        return newMatrix;
    }

    public static void sortInitialFlowListByShortestDistance(List<Pair<Flow, Flow>> initialFlowList) {
        initialFlowList.sort((if1, if2) -> {
            int if1Steps = Math.abs(if1.first().x() - if1.second().x()) + Math.abs(if1.first().y() - if1.second().y());
            int if2Steps = Math.abs(if2.first().x() - if2.second().x()) + Math.abs(if2.first().y() - if2.second().y());
            if (if1Steps > if2Steps) {
                return 1;
            } else if (if1Steps < if2Steps) {
                return -1;
            }
            return 0;
        });
    }

    public static Flow insertFlowWithDirection(Flow[][] matrix, int x, int y,
                                               String color,
                                               FlowDirection direction) {
        // Return null to indicate that the cell is already occupied.
        if (matrix[y][x] != null) {
            return null;
        }

        return matrix[y][x] = new Flow(new Point(x, y), color, direction);
    }

    public static boolean gameHasNoSolution(GameMap map, Flow[][] matrix) {
        for (final var initialFlowPair : map.getInitialFlowList()) {
            // This is a quick check if adjacent cells are blocked or not
            if (isCellBlocked(initialFlowPair.first(), matrix) || isCellBlocked(initialFlowPair.second(), matrix)) {
                return true;
            }
            // This is a more thorough check if there is still a path between two flows
            if (flowsHaveNoConnection(matrix, initialFlowPair.first(), initialFlowPair.second())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCellBlocked(Flow flow, Flow[][] matrix) {
        return isCellBlocked(matrix, flow.color(), flow.x(), flow.y() - 1)
            && isCellBlocked(matrix, flow.color(), flow.x(), flow.y() + 1)
            && isCellBlocked(matrix, flow.color(), flow.x() - 1, flow.y())
            && isCellBlocked(matrix, flow.color(), flow.x() + 1, flow.y());
    }

    public static boolean isCellBlocked(Flow[][] matrix, String flowColor, int x, int y) {
        if (y >= 0 && y < matrix.length && x >= 0 && x < matrix[y].length) {
            if (matrix[y][x] != null) {
                // A cell will be considered blocked for flow, if their color is not the same
                // NOTE: This doesn't cover the case of flow blocking itself
                return !matrix[y][x].color().equals(flowColor);
            }
            return false;
        }

        // If x and y are invalid, then there's no way for the flow to move
        return true;
    }

    public static boolean flowsHaveNoConnection(Flow[][] matrix, Flow start, Flow end) {
        final var visited = new Boolean[matrix.length][matrix[0].length];
        return flowsHaveNoConnectionRecursive(matrix, visited, start.x(), start.y(), end);
    }
}
