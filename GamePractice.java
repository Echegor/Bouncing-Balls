

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx .stage.StageStyle;
import javafx.util.Duration;
import javafx.scene.input.MouseEvent;
import java.util.Vector;
import java.util.Random;
import java.awt.GraphicsEnvironment;
import javafx.application.Platform;

public class GamePractice extends Application{

	//public static Vector<Circle> ballList = new Vector<Circle>();
	public static Vector<Ball> ballList = new Vector<Ball>();
	public static Vector<Pair> addList = new Vector<Pair>();
	public static Pane canvas;
	public static Random rand = new Random();
	public final static double GRAVITY = 1.0;
	public final static int MAXRADIUS = 50;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage primaryStage) {

		canvas = new Pane();
		double width = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getWidth();
		double height = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getHeight();
		final Scene scene = new Scene(canvas, width, height, new Color(1, 1, 1, 0));
		// primaryStage.initStyle(StageStyle.TRANSPARENT);
		//scene.setFill(null);

		primaryStage.setTitle("Game");
		primaryStage.setScene(scene);
		primaryStage.show();

						// int ballID = getBall(250,250);
						// ballList.get(ballID).getCircle().setLayoutX(250);
						// ballList.get(ballID).getCircle().setLayoutY(250);
		//canvas.setOpacity(1.0);
		//canvas.setStyle("-fx-background-color: rgba(0,0,0,0);");
		//scene.setFill(Color.TRANSPARENT);
		//primaryStage.initStyle(StageStyle.TRANSPARENT);

		// Thread t1 = new Thread(new DrawThread());
		// System.out.println("Starting DrawThread");
		// Platform.runLater(t1);

		scene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				System.out.println("Click");
				addList.add(new Pair(mouseEvent.getSceneX(),mouseEvent.getSceneY()));
		}});

		final Timeline loop = new Timeline(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
			@Override
			public synchronized void handle(final ActionEvent t) {
			checkDrawList();
			moveBalls();
			}
		}));

		loop.setCycleCount(Timeline.INDEFINITE);
		loop.play();
		
	
	}
	public static int getBall(double x, double y){
		double deltaX = rand.nextInt(10)-5;
		double deltaY = rand.nextInt(10)-5;
		double mass = rand.nextInt(MAXRADIUS)+.5;
		if(deltaX==0&&deltaY==0)
			return getBall(x,y);
		
		System.out.println("Adding Ball");
		ballList.add(new Ball(deltaX,deltaY,mass,x,y));
		return ballList.size()-1;
		
	}
	public static boolean atRightBorder(Bounds bounds, int ballID){
		return ballList.get(ballID).getCircle().getLayoutX() >= (bounds.getMaxX() - ballList.get(ballID).getCircle().getRadius());
	}
	public static boolean atLeftBorder(Bounds bounds, int ballID){
		return ballList.get(ballID).getCircle().getLayoutX() <= (bounds.getMinX() + ballList.get(ballID).getCircle().getRadius());
	}
	public static boolean atBottomBorder(Bounds bounds, int ballID){
		return ballList.get(ballID).getCircle().getLayoutY() >= (bounds.getMaxY() - ballList.get(ballID).getCircle().getRadius());
	}
	public static boolean atTopBorder(Bounds bounds, int ballID){
		return ballList.get(ballID).getCircle().getLayoutY() <= (bounds.getMinY() + ballList.get(ballID).getCircle().getRadius());
	}


	public static boolean checkCollision(int id1, int id2){
		Circle c1 = ballList.get(id1).getCircle();
		Circle c2 = ballList.get(id2).getCircle();
		double xDif = c1.getLayoutX() - c2.getLayoutX();
		double yDif = c1.getLayoutY() - c2.getLayoutY();
		double distanceSquared = xDif * xDif + yDif * yDif;
		return distanceSquared < (c1.getRadius() + c2.getRadius()) * (c1.getRadius() + c2.getRadius());
	}


	public static void resolveCollision(int ballID1,int ballID2){
		Ball c1 = ballList.get(ballID1);
		Ball c2 = ballList.get(ballID2);

		double newVelX1 = (c1.getX() * (c1.getMass() - c2.getMass()) + (2 * c2.getMass() * c2.getX())) / (c1.getMass() + c2.getMass());
		double newVelY1 = (c1.getY() * (c1.getMass() - c2.getMass()) + (2 * c2.getMass() * c2.getY())) / (c1.getMass() + c2.getMass());
		double newVelX2 = (c2.getX() * (c2.getMass() - c1.getMass()) + (2 * c1.getMass() * c1.getX())) / (c1.getMass() + c2.getMass());
		double newVelY2 = (c2.getY() * (c2.getMass() - c1.getMass()) + (2 * c1.getMass() * c1.getY())) / (c1.getMass() + c2.getMass());							

		c1.setX(newVelX1);
		c1.setY(newVelY1);
		c2.setX(newVelX2);
		c2.setY(newVelY2);


		ballList.get(ballID1).getCircle().setLayoutX(ballList.get(ballID1).getCircle().getLayoutX() + newVelX1);
		ballList.get(ballID1).getCircle().setLayoutY(ballList.get(ballID1).getCircle().getLayoutY() + newVelY1);
		ballList.get(ballID2).getCircle().setLayoutX(ballList.get(ballID2).getCircle().getLayoutX() + newVelX2);
		ballList.get(ballID2).getCircle().setLayoutY(ballList.get(ballID2).getCircle().getLayoutY() + newVelY2);
	}
	public static void doGravity(int ballID,Bounds bounds){
		Ball c1 = ballList.get(ballID);
		double speedY =c1.getY();
		if(!atBottomBorder(bounds,ballID)){
			//System.out.println("Doing Gravity");
			c1.setY(speedY+GRAVITY);
		}
			
	}
	public static void moveBalls(){
		for(int i = 0 ; i < ballList.size();i++){
			moveBall(i);
		}
	}
	public static void moveBall(int ballID){
		ballList.get(ballID).getCircle().setLayoutX(ballList.get(ballID).getCircle().getLayoutX() + ballList.get(ballID).getX());
		ballList.get(ballID).getCircle().setLayoutY(ballList.get(ballID).getCircle().getLayoutY() + ballList.get(ballID).getY());

		final Bounds bounds = canvas.getBoundsInLocal();

		if (atRightBorder(bounds,ballID) || atLeftBorder(bounds,ballID)) {
			ballList.get(ballID).setX(ballList.get(ballID).getX()*-1);
		}
		if (atBottomBorder(bounds,ballID) || atTopBorder(bounds,ballID)) {
			ballList.get(ballID).setY(ballList.get(ballID).getY()*-1);
		}
		for(int i=0;i<ballList.size();i++){
			if(i==ballID)
				continue;

			if(checkCollision(ballID,i)){
				System.out.println("Circle " + ballID +" has collided with "+i);
				resolveCollision(ballID,i);

			}
		}
	}

	public static void checkDrawList(){
		Pair temp;
		while(!addList.isEmpty()){
			System.out.println("Drawing ball.");
			temp = (Pair) addList.get(addList.size()-1);
			getBall(temp.x,temp.y);
			addList.remove(addList.size()-1);
		}
	}

	public static class Ball{
		double x,y,mass;
		Circle circle;
		public Ball(double x1, double y1,double mass,double circleX,double circleY){
			x = x1;
			y = y1;
			this.mass=mass;
			circle = addCircle(circleX,circleY,mass); 
		}
		public double getX(){
			return x;
		}
		public double getY(){
			return y;
		}
		public double getMass(){
			return mass;
		}
		public void setX(double x1){
			x = x1;
		}
		public void setY(double y1){
			y = y1;
		}
		public Circle addCircle(double x, double y,double mass){
			int r = rand.nextInt(256);
			int g = rand.nextInt(256);
			int b = rand.nextInt(256);
			Circle circle = new Circle(mass, Color.rgb(r,g,b));
			circle.relocate(x,y);

			canvas.getChildren().addAll(circle);
			return circle;
		}
		public Circle getCircle(){
			return circle;
		}
	}

	public static class Pair {
		public double x; 
		public double y;

		public Pair(double x, double y){
			this.x=x;
			this.y=y;
		}
	}
}