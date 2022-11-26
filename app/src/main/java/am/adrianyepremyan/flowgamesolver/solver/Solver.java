package am.adrianyepremyan.flowgamesolver.solver;

import static am.adrianyepremyan.flowgamesolver.map.domain.FlowDirection.DOWN;
import static am.adrianyepremyan.flowgamesolver.map.domain.FlowDirection.LEFT;
import static am.adrianyepremyan.flowgamesolver.map.domain.FlowDirection.RIGHT;
import static am.adrianyepremyan.flowgamesolver.map.domain.FlowDirection.UP;

import am.adrianyepremyan.flowgamesolver.helper.Pair;
import am.adrianyepremyan.flowgamesolver.helper.Point;
import am.adrianyepremyan.flowgamesolver.map.GameMap;
import am.adrianyepremyan.flowgamesolver.map.domain.Flow;
import am.adrianyepremyan.flowgamesolver.map.domain.FlowDirection;
import am.adrianyepremyan.flowgamesolver.map.printer.DefaultGameMapPrinter;
import am.adrianyepremyan.flowgamesolver.map.printer.GameMapPrinter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class Solver {

    private static final GameMapPrinter printer = new DefaultGameMapPrinter();

    private static final Flow[][] NIL = new Flow[0][0];

    private static Scheduler scheduler;

    // TODO: Within a separate thread, print the initialFlowIndex with, e.g. 5 seconds delay.
    public Flow[][] solve(GameMap map) {
        final var matrix = map.getMatrix();
        final var initialFlowList = new ArrayList<>(map.getInitialFlowList());
        // 8x8 map takes longer to be solved with the initial sort
//        sortInitialFlowListByShortestDistance(initialFlowList);
        final var initialFlow = initialFlowList.get(0);
        final int startX = initialFlow.first().x();
        final int startY = initialFlow.first().y();

        scheduler = Schedulers.boundedElastic();
        final var solvedMatrix = solveRecursively(map, matrix, matrix[startY][startX], initialFlowList, 0)
            .subscribeOn(scheduler)
            .block();
        scheduler.dispose();

        if (solvedMatrix == NIL) {
            throw new RuntimeException("Game has no solution!");
        }

        return solvedMatrix;
    }

    private Mono<Flow[][]> solveRecursively(GameMap map,
                                            Flow[][] matrix,
                                            Flow flow,
                                            List<Pair<Flow, Flow>> initialFlowList,
                                            int initialFlowIndex) {
        final int startX = flow.point().x();
        final int startY = flow.point().y();

        return Mono.zip(
                solveWithDirection(map, matrix, flow, initialFlowList, initialFlowIndex, startX, startY - 1, UP),
                solveWithDirection(map, matrix, flow, initialFlowList, initialFlowIndex, startX, startY + 1, DOWN),
                solveWithDirection(map, matrix, flow, initialFlowList, initialFlowIndex, startX - 1, startY, LEFT),
                solveWithDirection(map, matrix, flow, initialFlowList, initialFlowIndex, startX + 1, startY, RIGHT)
            )
            .map(tuple -> {
                    final var result = Stream.of(
                            tuple.getT1(),
                            tuple.getT2(),
                            tuple.getT3(),
                            tuple.getT4()
                        )
                        .filter(currResult -> currResult != NIL)
                        .findFirst()
                        .orElse(NIL);
//                    System.out.println(Thread.currentThread().getName()
//                        + " : " + "All results complete: " + (result != NIL));
                    return result;
                }
            );
    }

    private Mono<Flow[][]> solveWithDirection(GameMap map,
                                              Flow[][] matrix,
                                              Flow currentFlow,
                                              List<Pair<Flow, Flow>> initialFlowList,
                                              int initialFlowIndex,
                                              int x, int y,
                                              FlowDirection directionToGo) {
        final var initialFlow = initialFlowList.get(initialFlowIndex);

        // If the end of the initial flow is reached
        // Change the initial flow
        if (initialFlow.second().x() == x && initialFlow.second().y() == y) {
            // If the end of all initial flows is reached
            // The game is solved
            if (initialFlowIndex == initialFlowList.size() - 1) {
                return Mono.just(matrix);
            }

            final var nextInitialFlow = initialFlowList.get(initialFlowIndex + 1);
            final var nextFlow = matrix[nextInitialFlow.first().y()][nextInitialFlow.first().x()];

            // Solve recursively for next colored flow
            return solveRecursively(map, matrix, nextFlow, initialFlowList, initialFlowIndex + 1)
                .subscribeOn(scheduler);
        }

        if ((currentFlow.direction() == null || directionToGo != currentFlow.direction().getOpposite())
            && y >= 0 && y < matrix.length
            && x >= 0 && x < matrix[y].length) {
            // Insert the new flow with the provided direction
            final var tempMatrix = copyMatrix(matrix);
            final var insertedFlow = insertFlowWithDirection(tempMatrix, x, y, currentFlow.color(), directionToGo);
            if (insertedFlow != null) {
                // Stop to backtrack if after flow insertion the game can't have any solution
                if (gameHasNoSolution(map, tempMatrix)) {
//                    System.out.println("Game can't have solution in this case for inserted flow: " + insertedFlow);
//                    printer.print(tempMatrix);
                    return Mono.just(NIL);
                }
                // Solve recursively with the new flow
                return solveRecursively(map, tempMatrix, insertedFlow, initialFlowList, initialFlowIndex)
                    .subscribeOn(scheduler);
            }
        }
        return Mono.just(NIL);
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

    private boolean gameHasNoSolution(GameMap map, Flow[][] matrix) {
        for (final var initialFlow : map.getInitialFlowList()) {
            if (isCellBlocked(initialFlow.first(), matrix) || isCellBlocked(initialFlow.second(), matrix)) {
                return true;
            }
        }
        return false;
    }

    private boolean isCellBlocked(Flow flow, Flow[][] matrix) {
        return isCellBlocked(matrix, flow.color(), flow.x(), flow.y() - 1)
            && isCellBlocked(matrix, flow.color(), flow.x(), flow.y() + 1)
            && isCellBlocked(matrix, flow.color(), flow.x() - 1, flow.y())
            && isCellBlocked(matrix, flow.color(), flow.x() + 1, flow.y());
    }

    private boolean isCellBlocked(Flow[][] matrix, String flowColor, int x, int y) {
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

    private Flow[][] copyMatrix(Flow[][] matrix) {
        final var newMatrix = new Flow[matrix.length][];
        for (int i = 0; i < matrix.length; ++i) {
            newMatrix[i] = Arrays.copyOf(matrix[i], matrix[i].length);
        }
        return newMatrix;
    }

    private void sortInitialFlowListByShortestDistance(List<Pair<Flow, Flow>> initialFlowList) {
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
}
