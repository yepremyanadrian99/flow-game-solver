package am.adrianyepremyan.flowgamesolver.map.domain;

import am.adrianyepremyan.flowgamesolver.helper.Point;

public record Flow(Point point, String color, FlowDirection direction) {

    public int x() {
        return point.x();
    }

    public int y() {
        return point.y();
    }
}
