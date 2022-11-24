package am.adrianyepremyan.flowgamesolver.map.printer;

import am.adrianyepremyan.flowgamesolver.map.GameMap;
import am.adrianyepremyan.flowgamesolver.map.domain.Flow;

public interface GameMapPrinter {

    void print(GameMap map);

    void print(Flow[][] matrix);
}
