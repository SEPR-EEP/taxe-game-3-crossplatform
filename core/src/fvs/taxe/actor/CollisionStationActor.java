package fvs.taxe.actor;

import fvs.taxe.CustomTexture;
import gameLogic.map.IPositionable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import gameLogic.map.IPositionable;

/**This class is a type of image specifically for collisions for stations.*/
public class CollisionStationActor extends StationActor {
	/**The width of a Station's collision area in pixels.*/
	private final int width =16;
	
	/**The height of a Station's collision area in pixels.*/
	private final int height =16;


    /**Instantiation method. Uses junction_dot.png as the texture for the station.
     * @param location the location the station should be placed at.
     */
    public CollisionStationActor(IPositionable location) {
        super(location, new CustomTexture(Gdx.files.internal("junction_dot.png")));

        setSize(width, height);
        setPosition(location.getX() - width / 2, location.getY() - height / 2);
    }
}
