package am.adrianyepremyan.flowgamesolver.map;

import am.adrianyepremyan.flowgamesolver.helper.Point;
import am.adrianyepremyan.flowgamesolver.map.domain.Flow;
import am.adrianyepremyan.flowgamesolver.map.domain.InitialFlow;
import am.adrianyepremyan.flowgamesolver.map.printer.GameMapPrinter;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class GameMap {

    private final Flow[][] matrix;
    private final List<InitialFlow> initialFlowList = new ArrayList<>();

    public GameMap(int width, int height) {
        this.matrix = new Flow[height][width];
    }

    public void addInitialFlows(String color, Point p1, Point p2) {
        final var initialPoints = new InitialFlow(color, p1, p2);
        initialFlowList.add(initialPoints);
        matrix[p1.y()][p1.x()] = new Flow(p1, color);
        matrix[p2.y()][p2.x()] = new Flow(p2, color);
    }

    public void print(GameMapPrinter printer) {
        printer.print(this);
    }
}
