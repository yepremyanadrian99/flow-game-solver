package am.adrianyepremyan.flowgamesolver.solver;

import am.adrianyepremyan.flowgamesolver.map.GameMap;
import am.adrianyepremyan.flowgamesolver.map.domain.Flow;
import java.util.function.Function;

public interface Solution extends Function<GameMap, Flow[][]> {
}
