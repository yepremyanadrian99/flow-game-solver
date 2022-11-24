package am.adrianyepremyan.flowgamesolver.map.domain;

import am.adrianyepremyan.flowgamesolver.helper.Point;

public record Flow(Point point, String color, FlowDirection direction) {
}
