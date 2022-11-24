package am.adrianyepremyan.flowgamesolver.map.domain;

import am.adrianyepremyan.flowgamesolver.helper.Point;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Flow {

    private Point point;
    private String color;
    private FlowDirection direction;

    public Flow(Point point, String color) {
        this.point = point;
        this.color = color;
    }
}
