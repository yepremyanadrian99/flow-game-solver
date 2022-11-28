package am.adrianyepremyan.flowgamesolver.map;

import am.adrianyepremyan.flowgamesolver.helper.Pair;
import am.adrianyepremyan.flowgamesolver.helper.Point;
import am.adrianyepremyan.flowgamesolver.map.domain.Flow;
import am.adrianyepremyan.flowgamesolver.map.printer.GameMapPrinter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;

@Getter
public class GameMap {

    private final Flow[][] matrix;
    private final List<Pair<Flow, Flow>> initialFlowList = new ArrayList<>();
    private final Set<Character> colorFirstLetters = new HashSet<>();

    public GameMap(int width, int height) {
        this.matrix = new Flow[height][width];
    }

    public void addInitialFlows(String color, Point p1, Point p2) {
        if (!colorFirstLetters.add(color.charAt(0))) {
            throw new RuntimeException("The first letter of the color is already occupied");
        }

        final var flow1 = matrix[p1.y()][p1.x()] = new Flow(p1, color, null);
        final var flow2 = matrix[p2.y()][p2.x()] = new Flow(p2, color, null);
        final var pair = new Pair<>(flow1, flow2);
        initialFlowList.add(pair);
    }

    public void print(GameMapPrinter printer) {
        printer.print(this);
    }
}
