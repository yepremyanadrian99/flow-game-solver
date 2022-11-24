package am.adrianyepremyan.flowgamesolver.map.domain;

public enum FlowDirection {
    UP {
        @Override
        public FlowDirection getOpposite() {
            return DOWN;
        }
    },
    DOWN {
        @Override
        public FlowDirection getOpposite() {
            return UP;
        }
    },
    LEFT {
        @Override
        public FlowDirection getOpposite() {
            return RIGHT;
        }
    },
    RIGHT {
        @Override
        public FlowDirection getOpposite() {
            return LEFT;
        }
    };

    public abstract FlowDirection getOpposite();
}
