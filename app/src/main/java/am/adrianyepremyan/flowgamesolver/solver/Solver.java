package am.adrianyepremyan.flowgamesolver.solver;

import static am.adrianyepremyan.flowgamesolver.map.domain.FlowDirection.DOWN;
import static am.adrianyepremyan.flowgamesolver.map.domain.FlowDirection.LEFT;
import static am.adrianyepremyan.flowgamesolver.map.domain.FlowDirection.RIGHT;
import static am.adrianyepremyan.flowgamesolver.map.domain.FlowDirection.UP;

import am.adrianyepremyan.flowgamesolver.helper.Point;
import am.adrianyepremyan.flowgamesolver.map.GameMap;
import am.adrianyepremyan.flowgamesolver.map.domain.Flow;
import am.adrianyepremyan.flowgamesolver.map.domain.FlowDirection;
import am.adrianyepremyan.flowgamesolver.map.domain.InitialFlow;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

// TODO: Implement Reactivity to avoid blocking threads.
public class Solver {

    public Flow[][] solve(GameMap map) {
        final var matrix = map.getMatrix();
        final var initialFlows = new ArrayList<>(map.getInitialFlowList());
        initialFlows.sort((if1, if2) -> {
            int if1Steps = Math.abs(if1.p1().x() - if1.p2().x()) + Math.abs(if1.p1().y() - if1.p2().y());
            int if2Steps = Math.abs(if2.p1().x() - if2.p2().x()) + Math.abs(if2.p1().y() - if2.p2().y());
            if (if1Steps > if2Steps) {
                return 1;
            } else if (if1Steps < if2Steps) {
                return -1;
            }
            return 0;
        });
        final var initialFlow = initialFlows.get(0);
        final int startX = initialFlow.p1().x();
        final int startY = initialFlow.p1().y();

        final var future = solveRecursively(matrix, matrix[startY][startX], initialFlows, 0);
        try {
            final var solvedMatrix = future.get();
            if (solvedMatrix == null) {
                throw new RuntimeException("Game has no solution!");
            }
            return solvedMatrix;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException("Unknown error handled");
        }
    }

    private CompletableFuture<Flow[][]> solveRecursively(Flow[][] matrix,
                                                         Flow flow,
                                                         List<InitialFlow> initialFlowList,
                                                         int initialFlowIndex) {
        final int startX = flow.point().x();
        final int startY = flow.point().y();

        final var upFuture =
            solveWithDirection(matrix, flow, initialFlowList, initialFlowIndex, startX, startY - 1, UP);
        final var downFuture =
            solveWithDirection(matrix, flow, initialFlowList, initialFlowIndex, startX, startY + 1, DOWN);
        final var leftFuture =
            solveWithDirection(matrix, flow, initialFlowList, initialFlowIndex, startX - 1, startY, LEFT);
        final var rightFuture =
            solveWithDirection(matrix, flow, initialFlowList, initialFlowIndex, startX + 1, startY, RIGHT);

        return CompletableFuture.allOf(upFuture, downFuture, leftFuture, rightFuture)
            .thenApplyAsync(unused -> {
                try {
                    final var upResult = upFuture.get();
                    final var downResult = downFuture.get();
                    final var leftResult = leftFuture.get();
                    final var rightResult = rightFuture.get();
                    final var result = Stream.of(upResult, downResult, leftResult, rightResult)
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(null);
                    System.out.println(Thread.currentThread().getName() + " : " + "All results complete: " + false);
                    return result;
                } catch (InterruptedException | ExecutionException e) {
                    return false;
                }
            }).thenApply(Flow[][].class::cast);
    }

    private CompletableFuture<Flow[][]> solveWithDirection(Flow[][] matrix,
                                                           Flow currentFlow,
                                                           List<InitialFlow> initialFlowList,
                                                           int initialFlowIndex,
                                                           int x, int y,
                                                           FlowDirection directionToGo) {
        final var initialFlow = initialFlowList.get(initialFlowIndex);

        // If the end of the initial flow is reached
        // Change the initial flow
        if (initialFlow.p2().x() == x && initialFlow.p2().y() == y) {
            // If the end of all initial flows is reached
            // The game is solved
            if (initialFlowIndex == initialFlowList.size() - 1) {
                return CompletableFuture.completedFuture(matrix);
            }
            final var nextInitialFlow = initialFlowList.get(initialFlowIndex + 1);
            final var nextFlow = matrix[nextInitialFlow.p1().y()][nextInitialFlow.p1().x()];

            return solveRecursively(matrix, nextFlow, initialFlowList, initialFlowIndex + 1);
        }

        if ((currentFlow.direction() == null || directionToGo != currentFlow.direction().getOpposite())
            && y >= 0 && y < matrix.length
            && x >= 0 && x < matrix[y].length) {
            // Insert the new flow with the provided direction
            final var tempMatrix = copyMatrix(matrix);
            final var insertedFlow = insertFlowWithDirection(tempMatrix, x, y, currentFlow.color(), directionToGo);
            if (insertedFlow != null) {
                // Solve recursively with the new flow
                return solveRecursively(tempMatrix, insertedFlow, initialFlowList, initialFlowIndex)
                    .thenApplyAsync(result -> {
                        if (result != null) {
                            return result;
                        }
                        // Revert the step
                        tempMatrix[y][x] = null;
                        return null;
                    });
            }
        }
        return CompletableFuture.completedFuture(null);
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

    private Flow[][] copyMatrix(Flow[][] matrix) {
        final var newMatrix = new Flow[matrix.length][];
        for (int i = 0; i < matrix.length; ++i) {
            newMatrix[i] = Arrays.copyOf(matrix[i], matrix[i].length);
        }
        return newMatrix;
    }
}
