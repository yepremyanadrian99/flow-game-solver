package am.adrianyepremyan.flowgamesolver.solver;

import static am.adrianyepremyan.flowgamesolver.helper.GameUtils.copyMatrix;
import static am.adrianyepremyan.flowgamesolver.helper.GameUtils.gameHasNoSolution;
import static am.adrianyepremyan.flowgamesolver.helper.GameUtils.insertFlowWithDirection;
import static am.adrianyepremyan.flowgamesolver.helper.GameUtils.sortInitialFlowListByShortestDistance;
import static am.adrianyepremyan.flowgamesolver.map.domain.FlowDirection.DOWN;
import static am.adrianyepremyan.flowgamesolver.map.domain.FlowDirection.LEFT;
import static am.adrianyepremyan.flowgamesolver.map.domain.FlowDirection.RIGHT;
import static am.adrianyepremyan.flowgamesolver.map.domain.FlowDirection.UP;

import am.adrianyepremyan.flowgamesolver.helper.Pair;
import am.adrianyepremyan.flowgamesolver.map.GameMap;
import am.adrianyepremyan.flowgamesolver.map.domain.Flow;
import am.adrianyepremyan.flowgamesolver.map.domain.FlowDirection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class ReactiveSolution implements Solution {

    private static final Flow[][] NIL = new Flow[0][0];

    public Flow[][] apply(GameMap map) {
        final var matrix = map.getMatrix();
        final var initialFlowList = new ArrayList<>(map.getInitialFlowList());
        sortInitialFlowListByShortestDistance(initialFlowList);
        final var initialFlow = initialFlowList.get(0);
        final int startX = initialFlow.first().x();
        final int startY = initialFlow.first().y();

        final var solvedMatrix = solveRecursively(map, matrix, matrix[startY][startX], initialFlowList, 0)
            .subscribeOn(Schedulers.boundedElastic())
            .block();

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
            .map(tuple -> Stream.of(
                    tuple.getT1(),
                    tuple.getT2(),
                    tuple.getT3(),
                    tuple.getT4()
                )
                .filter(currResult -> currResult != NIL)
                .findFirst()
                .orElse(NIL));
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
            return solveRecursively(map, matrix, nextFlow, initialFlowList, initialFlowIndex + 1);
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
                    return Mono.just(NIL);
                }
                // Solve recursively with the new flow
                return solveRecursively(map, tempMatrix, insertedFlow, initialFlowList, initialFlowIndex);
            }
        }

        return Mono.just(NIL);
    }
}
