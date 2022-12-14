package am.adrianyepremyan.flowgamesolver.solver;

import am.adrianyepremyan.flowgamesolver.map.GameMap;
import am.adrianyepremyan.flowgamesolver.map.domain.Flow;

public class Solver {

    public Flow[][] solve(GameMap map, Solution solution) {
        return solution.apply(map);
    }
}
