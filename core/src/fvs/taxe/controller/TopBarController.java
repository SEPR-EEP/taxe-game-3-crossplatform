package fvs.taxe.controller;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import Util.ActorsManager;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import fvs.taxe.TaxeGame;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.GameStateListener;
import gameLogic.Player;
import gameLogic.obstacle.Obstacle;
import gameLogic.obstacle.ObstacleListener;
import gameLogic.obstacle.ObstacleType;

import java.util.Timer;
import java.util.TimerTask;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import gameLogic.resource.ConnectionModifier;
import gameLogic.resource.Resource;

import java.util.List;

/**Controller for the Top Bar of the GUI, changes the Top Bar.*/
public class TopBarController {
	
	/**The height of the Top Bar.*/
	public final static int CONTROLS_HEIGHT = 40;

	/**The Game Context.*/
	private Context context;

	/**The end Turn Button used for the player to End the Turn.*/
	public TextButton endTurnButton;

	/** The modifyConnection button for entering edit connection state */
	public TextButton modifyConnectionButton;

	/**The replay button.*/
	public Slider replaySpeedSlider;
	public TextButton replayButton;

	/**Label for displaying a message to the player.*/
	private Label flashMessage;
	
	/**Label for display obstacle events to the player.*/
	private Label obstacleLabel;
	
	/**Actor for the background to the Top Bar*/
	private TopBarActor topBarBackground;

	/**Instantiation method sets up a listener for Events starting to display the Event message in the Top Bar.
	 * @param context The game Context.
	 */
	public TopBarController(final Context context) {
		this.context = context;

		context.getGameLogic().subscribeObstacleChanged(new ObstacleListener() {

			@Override
			public void started(Obstacle obstacle) {
				ObstacleType type = obstacle.getType();
				Color color = null;
				switch (type) {
					case BLIZZARD:
						color = Color.WHITE;
						break;
					case FLOOD:
						color = Color.valueOf("1079c1");
						break;
					case VOLCANO:
						color = Color.valueOf("ec182c");
						break;
					case EARTHQUAKE:
						color = Color.valueOf("7a370a");
						break;
				}
				displayObstacleMessage(obstacle.getType().toString() + " in " + obstacle.getStation().getName(), color);
			}

			@Override
			public void ended(Obstacle obstacle) {
			}
		});
	}

	/**This method adds the background to the game.*/
	public void drawBackground() {
		topBarBackground = new TopBarActor();
		context.getStage().addActor(topBarBackground);
	}
	
	/**This method calls the label drawing methods*/
	public void drawLabels() {
		drawFlashLabel();
		drawObstacleLabel();
	}
	
	/**This method draws a label for a message*/
	public void drawFlashLabel() {
		flashMessage = new Label("", context.getSkin());
		flashMessage.setPosition(450, TaxeGame.HEIGHT - 24);
		flashMessage.setAlignment(0);
		context.getStage().addActor(flashMessage);
	}

	/**This method draws a label for obstacle messages*/
	public void drawObstacleLabel() {
		obstacleLabel = new Label("", context.getSkin());
		obstacleLabel.setColor(Color.BLACK);
		obstacleLabel.setPosition(10, TaxeGame.HEIGHT - 34);
		context.getStage().addActor(obstacleLabel);
	}

	/**This method displays a message of a certain color in the Top Bar.
	 * @param message The message to be displayed.
	 * @param color The color of the message to be displayed.
	 */
	public void displayFlashMessage(String message, Color color) {
		displayFlashMessage(message, color, 2f);
	}
	
	/**This method displays a message of a certain color in the Top Bar for a certain amount of time.
	 * @param message The message to be displayed.
	 * @param color The color of the message to be displayed.
	 * @param time The length of time to display the message, in seconds.
	 */
	public void displayFlashMessage(String message, Color color, float time) {
		flashMessage.setText(message);
		flashMessage.setColor(color);
		flashMessage.addAction(sequence(delay(time), fadeOut(0.25f)));
	}

	/**This method displays a message of a certain color in the Top Bar for a certain amount of time while specifiying a background Color.
	 * @param message The message to be displayed.
	 * @param backgroundColor The color of the background to display behind the message.
	 * @param textColor The color of the message to be displayed.
	 * @param time The length of time to display the message, in seconds.
	 */
	public void displayFlashMessage(String message, Color backgroundColor, Color textColor, float time) {
		topBarBackground.setObstacleColor(backgroundColor);
		topBarBackground.setControlsColor(backgroundColor);
		flashMessage.clearActions();
		flashMessage.setText(message);
		flashMessage.setColor(textColor);
		flashMessage.addAction(sequence(delay(time), fadeOut(0.25f), run(new Runnable() {
			public void run() {
				topBarBackground.setControlsColor(Color.LIGHT_GRAY);
				if (obstacleLabel.getActions().size == 0) {
					topBarBackground.setObstacleColor(Color.LIGHT_GRAY);
				}
			}
		})));
	}

	/**This method displays a message in the Top Bar with a specified background Color.
	 * @param message The message to be displayed.
	 * @param color The background color to be displayed behind the message.
	 */
	public void displayObstacleMessage(String message, Color color) {
		// display a message to the obstacle topBar label, with topBarBackground color color and given message
		// wraps automatically to correct size
		obstacleLabel.clearActions();
		obstacleLabel.setText(message);
		obstacleLabel.setColor(Color.BLACK);
		obstacleLabel.pack();
		topBarBackground.setObstacleColor(color);
		topBarBackground.setObstacleWidth(obstacleLabel.getWidth() + 20);
		obstacleLabel.addAction(sequence(delay(2f), fadeOut(0.25f), run(new Runnable() {
			public void run() {
				// run action to reset obstacle label after it has finished displaying information
				obstacleLabel.setText("");
				topBarBackground.setObstacleColor(Color.LIGHT_GRAY);
			}
		})));
	}

	/**This method adds an End Turn button to the game that captures an on click event and notifies the game when the turn is over.*/
	public void drawEndTurnButton() {
		endTurnButton = new TextButton("End Turn", context.getSkin());
		endTurnButton.setPosition(TaxeGame.WIDTH - 80.0f, TaxeGame.HEIGHT - 33.0f);
		endTurnButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if ( !Game.getInstance().replayMode ) {
					context.getGameLogic().getPlayerManager().turnOver();
				}
			}
		});

		context.getGameLogic().subscribeStateChanged(new GameStateListener() {
			@Override
			public void changed(GameState state) {
				if (state == GameState.NORMAL ) {
					endTurnButton.setVisible(true);
				} else {
					endTurnButton.setVisible(false);
				}
			}
		});

		context.getStage().addActor(endTurnButton);

		context.getGameLogic().subscribeStateChanged(new GameStateListener() {

			@Override
			public void changed(GameState state) {
				if (state == GameState.CONFIRMING) {
					Game.getInstance().getConfirmingTrain().setRoute(context.getGameLogic().getMap().createRoute(Game.getInstance().getConfirmingPositions()));

					@SuppressWarnings("unused")
					TrainMoveController move = new TrainMoveController(context, Game.getInstance().getConfirmingTrain());

				}
			}
		});

		context.getStage().addActor(endTurnButton);
	}

	/**This method adds an End Turn button to the game that captures an on click event and notifies the game when the turn is over.*/
	public void drawReplayButton() {


		replaySpeedSlider = new Slider(1.0f, 5.0f, 1.0f, false, context.getSkin());
		replaySpeedSlider.setPosition(TaxeGame.WIDTH - 500.0f, TaxeGame.HEIGHT - 33.0f);
		context.getStage().addActor(replaySpeedSlider);
		
		replaySpeedSlider.addListener(new ClickListener() {

			@Override
			public void touchUp(InputEvent event, float x, float y,
								int pointer, int button) {
				super.touchUp(event, x, y, pointer, button);
				replaySpeedSlider.setVisible(false);
			}


//			@Override
//			public void exit(InputEvent event, float x, float y, int pointer,
//					Actor toActor) {
//				replaySpeedSlider.setVisible(false);
//			}		


		});


		replayButton = new TextButton("    Loading    ", context.getSkin());
		replayButton.setPosition(TaxeGame.WIDTH - 350.0f, TaxeGame.HEIGHT - 33.0f);
		replayButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {

				if (Game.getInstance().replayMode) {
					return;
				}

				// System.out.println("Replay speed is: " + replaySpeedSlider.getValue() + "x!");
				ActorsManager.interruptAllTrains();
				ActorsManager.hideAllObstacles();

				Game.getInstance().setGameSpeed(replaySpeedSlider.getValue());
				Game.getInstance().createSnapshot();
				Game.getInstance().replaySnapshot(0);

				context.getStationController().drawConnections(Game.getInstance().getMap().getConnections(), Color.GRAY);
				context.getStationController().drawStations();
				context.getStationController().redrawTrains();
			}

			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				if ( !Game.getInstance().replayMode ) {
					replaySpeedSlider.setVisible(true);
				}
			}
		});

		context.getGameLogic().subscribeStateChanged(new GameStateListener() {
			@Override
			public void changed(GameState state) {
				if (state == GameState.NORMAL) {
					replayButton.setVisible(true);
				} else {
					if ( !Game.getInstance().replayMode ) {
						replayButton.setVisible(false);
					}
					replaySpeedSlider.setVisible(false);
				}
			}
		});

		context.getStage().addActor(replayButton);

		replaySpeedSlider.setVisible(false);
	}



	/** This method adds a Modify Connection button the game the captures an on click event and
	 * notifies the game to enter connection editing mode
	 * @author Team EEP*/
	public void drawModifyConnectionButton() {
		modifyConnectionButton = new TextButton("Mod Connection x" + (context.getGameLogic().getPlayerManager().getCurrentPlayer().getConnectionModifiers().size() + 1), context.getSkin());
		modifyConnectionButton.setPosition(TaxeGame.WIDTH - 240.0f, TaxeGame.HEIGHT - 33.0f);
		modifyConnectionButton.setVisible(false);


		modifyConnectionButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {

				Player player = context.getGameLogic().getPlayerManager().getCurrentPlayer();
				List<ConnectionModifier> connectionModifierList = player.getConnectionModifiers();

				//Only respond to click if player has a connection modifier resource
				if (!connectionModifierList.isEmpty()) {

					//Remove a connection modifier resource from the players resource list
					player.getResources().remove(player.getConnectionModifiers().get(0));

					//Set game to editing state
					context.getGameLogic().setState(GameState.EDITING);

					displayFlashMessage("Select two stations to either connect or disconnect", Color.RED);
				}

			}
		});

		context.getGameLogic().subscribeStateChanged(new GameStateListener() {
			@Override
			public void changed(GameState state) {
				//Set visible if game in normal state and play has connection modifier resources to be spent
				Player player = context.getGameLogic().getPlayerManager().getCurrentPlayer();
				if (state == GameState.NORMAL
						&& !player.getConnectionModifiers().isEmpty()) {
					modifyConnectionButton.setVisible(true);
				} else {
					modifyConnectionButton.setVisible(false);
				}
			}

		});

		context.getStage().addActor(modifyConnectionButton);


	}


}