package fvs.taxe.controller;

import com.badlogic.gdx.graphics.Color;
import fvs.taxe.actor.ObstacleActor;
import fvs.taxe.actor.ParticleEffectActor;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.GameStateListener;
import gameLogic.obstacle.Obstacle;
import gameLogic.obstacle.ObstacleListener;
import gameLogic.obstacle.ObstacleType;
import gameLogic.obstacle.Rumble;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;

import Util.Tuple;
import gameLogic.resource.Train;

/**Controller for updating the game with graphics for obstacles.*/
public class ObstacleController {

	/**The context of the Game.*/
	private Context context;
	
	/**Hashmap of particle effects with a corresponding string name*/
	private HashMap<String, ParticleEffectActor> effects;
	
	/**The rumble is used to vibrate the Screen when an obstacle is placed*/
	private Rumble rumble;
	
	/**The Instantiation method sets up the particle effects and creates a listener for when an Obstacle is started so that it can update
	 * the graphics accordingly.
	 * @param context The context of the game.
	 */
	public ObstacleController(final Context context) {
		// take care of rendering of stations (only rendered on map creation, visibility changed when active)
		this.context = context;
		effects = new HashMap<String, ParticleEffectActor>();
		createParticleEffects();
		rumble = new Rumble();
		context.getGameLogic().subscribeObstacleChanged(new ObstacleListener() {
			
			@Override
			public void started(Obstacle obstacle) {
				obstacle.start();
				obstacle.getStation().setObstacle(obstacle); 
				// set the obstacle so its visible
				obstacle.getActor().setVisible(true);
				
				// shake the screen if the obstacle is an earthquake
				if (obstacle.getType() == ObstacleType.EARTHQUAKE) {
					rumble.rumble(1f, 2f);
				}
				if (obstacle.getType() == ObstacleType.BLIZZARD) {
					effects.get("Blizzard").setPosition(obstacle.getPosition().getX(), obstacle.getPosition().getY());
					effects.get("Blizzard").start(); 
				} else if (obstacle.getType() == ObstacleType.FLOOD) {
					effects.get("Flood").setPosition(obstacle.getPosition().getX()-10, obstacle.getPosition().getY() + 50);
					effects.get("Flood").start(); 
				} else if (obstacle.getType() == ObstacleType.VOLCANO) {
					effects.get("Volcano").setPosition(obstacle.getPosition().getX(), obstacle.getPosition().getY()-10);
					effects.get("Volcano").start(); 
				}
			}
			
			@Override
			public void ended(Obstacle obstacle) {
				obstacle.getActor().setVisible(false);
				obstacle.getStation().clearObstacle();
				obstacle.end();
			}
		});

		context.getGameLogic().subscribeStateChanged(new GameStateListener() {
			@Override
			public void changed(GameState state) {
				if (state == GameState.DESTROYING) {
					Train train = Game.getInstance().getTrainToDestroy();

					train.getActor().getActions().clear();
					train.getActor().setVisible(false);

					train.getPlayer().removeResource(train);

					context.getTopBarController().displayFlashMessage("Your train was hit by a natural disaster...", Color.BLACK, Color.RED, 4);

					Game.getInstance().setState(GameState.NORMAL);

				}
			}
		});
	}

	/**This method draws obstacles when the map is created. It leaves the obstacles invisible as resources that can be used at any time.*/
	public void drawObstacles(){
		// needs to only be called once, on map creation
		// adds all obstacles to the stage but makes them invisible
		ArrayList<Tuple<Obstacle, Float>> obstaclePairs = context.getGameLogic().getObstacleManager().getObstacles();
		for (Tuple<Obstacle, Float> obstaclePair: obstaclePairs) {
			renderObstacle(obstaclePair.getFirst(), false);
		}
	}

	/**This method renders an obstacle as an Actor.
	 * @param obstacle The obstacle to be rendered.
	 * @param visible The visibility of the obstacle.
	 * @return The actor produced from rendering the obstacle.
	 */
	private ObstacleActor renderObstacle(Obstacle obstacle, boolean visible) {
		// render the obstacle's actor with the visibility given
		ObstacleActor obstacleActor = new ObstacleActor(obstacle);
		obstacleActor.setVisible(visible);
		obstacle.setActor(obstacleActor);
		context.getStage().addActor(obstacleActor);
		return obstacleActor;
	}
	
	/**This method sets up particle effects for use in the game. It needs to be called once to use effects, and loads effects from the internal files*/
	private void createParticleEffects() {
		ParticleEffect snowEffect = new ParticleEffect();
		snowEffect.load(Gdx.files.internal("effects/snow.p"), Gdx.files.internal("effects"));
		ParticleEffectActor snowActor = new ParticleEffectActor(snowEffect);
		effects.put("Blizzard", snowActor);
		
		ParticleEffect floodEffect = new ParticleEffect();
		floodEffect.load(Gdx.files.internal("effects/flood.p"), Gdx.files.internal("effects"));
		ParticleEffectActor floodActor = new ParticleEffectActor(floodEffect);
		effects.put("Flood", floodActor);
		
		ParticleEffect volcanoEffect = new ParticleEffect();
		volcanoEffect.load(Gdx.files.internal("effects/volcano.p"), Gdx.files.internal("effects"));
		ParticleEffectActor volcanoActor = new ParticleEffectActor(volcanoEffect);
		effects.put("Volcano", volcanoActor);
	}
	
	/**This method draws the obstacle effects at their locations with their visibilities*/
	public void drawObstacleEffects() {
		for (ParticleEffectActor actor : effects.values()) {
			context.getStage().addActor(actor);
		}
	}

	/**@returns the Rumble used in the ObstacleController to shake the screen*/
	public Rumble getRumble() {
		return rumble;
	}
}
