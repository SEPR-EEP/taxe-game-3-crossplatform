package gameLogic.map;

	/**This class describes an abstract class of a 2 Dimensional Vector Coordinate with coordinates x and y.*/
    abstract public class IPositionable {

        public abstract int getX();

        public abstract int getY();

        public abstract void setX(int x);

        public abstract void setY(int y);

        public abstract boolean equals(Object o);
}