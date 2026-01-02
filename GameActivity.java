package com.example.snakegame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity implements SurfaceHolder.Callback{

    private List<SnakePoints> snakePointsList=new ArrayList<>();
    private Timer timer;
    private int positionX,positionY;
    private static final int pointSize=28;
    private static final int defaultTailPoint=3;
    private static final int snakeColor= Color.WHITE,foodColor=Color.RED;
    private static final int snakeMovingSpeed=800;
    private int scored=0;
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    private String movingPosition="right";
    private TextView score;
    private Canvas canvas=null;

    private Paint pointColor=null;
    private Paint foodColors=null;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_game);
        surfaceView=findViewById(R.id.surfaceView);
        score=findViewById(R.id.score);
        ImageButton left=findViewById(R.id.left_btn);
        ImageButton down=findViewById(R.id.down_btn);
        ImageButton right=findViewById(R.id.right_btn);
        ImageButton up = findViewById(R.id.up_btn);

        surfaceView.getHolder().addCallback(this);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!movingPosition.equals("right")){
                    movingPosition="left";
                }
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!movingPosition.equals("left")){
                    movingPosition="right";
                }
            }
        });
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!movingPosition.equals("down")){
                    movingPosition="up";
                }
            }
        });
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!movingPosition.equals("up")){
                    movingPosition="down";
                }
            }
        });
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        this.surfaceHolder=surfaceHolder;
        init();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }
    private void init(){
        snakePointsList.clear();;
        score.setText("Score = 0");
        scored=0;
        movingPosition="right";

        int startingPositionX=(pointSize)*defaultTailPoint;
        for(int i=0;i<defaultTailPoint;i++){
            SnakePoints snakePoints=new SnakePoints(startingPositionX,pointSize);
            snakePointsList.add(snakePoints);

            startingPositionX=startingPositionX-(pointSize*2);
        }
        addPoint();

        moveSnake();
    }
    private void addPoint(){
        int surfaceWidth=surfaceView.getWidth()- pointSize*2;
        int surfaceHeight=surfaceView.getHeight()-pointSize*2;

        int randomPointX=new Random().nextInt(surfaceWidth/pointSize);
        int randomPointY=new Random().nextInt(surfaceHeight/pointSize);

        if((randomPointX%2) != 0){
            randomPointX+=1;
        }
        if((randomPointY%2) != 0){
            randomPointY+=1;
        }
        positionX=pointSize*randomPointX+pointSize;
        positionY=pointSize*randomPointY+pointSize;
    }
    private void moveSnake(){
        timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int headPositionX=snakePointsList.get(0).getPositionX();
                int headPositionY=snakePointsList.get(0).getPositionY();

                if(headPositionX==positionX &&headPositionY==positionY){
                    growSnake();
                    addPoint();
                }

                switch(movingPosition){
                    case "right":
                        snakePointsList.get(0).setPositionX(headPositionX+(pointSize*2));
                        snakePointsList.get(0).setPositionY(headPositionY);
                        break;
                    case "left":
                        snakePointsList.get(0).setPositionX(headPositionX-(pointSize*2));
                        snakePointsList.get(0).setPositionY(headPositionY);
                        break;
                    case "up":
                        snakePointsList.get(0).setPositionX(headPositionX);
                        snakePointsList.get(0).setPositionY(headPositionY-(pointSize*2));
                        break;
                    case "down":
                        snakePointsList.get(0).setPositionX(headPositionX);
                        snakePointsList.get(0).setPositionY(headPositionY+(pointSize*2));
                        break;
                }
                if(checkGameOver(headPositionX,headPositionY)){
                    timer.purge();
                    timer.cancel();

                    Intent intent=new Intent(GameActivity.this,GameOverActivity.class);
                    intent.putExtra("SCORE",scored);
                    startActivity(intent);
                }else{
                    canvas=surfaceHolder.lockCanvas();
                    canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
                    canvas.drawCircle(snakePointsList.get(0).getPositionX(),snakePointsList.get(0).getPositionY(),pointSize,createPointColor());
                    canvas.drawCircle(positionX,positionY,pointSize,createFoodColor());

                    for(int i=1;i<snakePointsList.size();i++){
                        int tempX=snakePointsList.get(i).getPositionX();
                        int tempY=snakePointsList.get(i).getPositionY();
                        snakePointsList.get(i).setPositionX(headPositionX);
                        snakePointsList.get(i).setPositionY(headPositionY);

                        canvas.drawCircle(snakePointsList.get(i).getPositionX(),snakePointsList.get(i).getPositionY(),pointSize,createPointColor());

                        headPositionX=tempX;
                        headPositionY=tempY;
                    }

                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        },1000-snakeMovingSpeed,1000-snakeMovingSpeed);
    }
    private void growSnake(){
        SnakePoints snakePoints=new SnakePoints(0,0);
        snakePointsList.add(snakePoints);
        scored++;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                score.setText("Score = "+String.valueOf(scored));
            }
        });
    }
    private boolean checkGameOver(int headPositionX, int headPositionY){
        boolean gameOver=false;
        if(snakePointsList.get(0).getPositionX()<0 || snakePointsList.get(0).getPositionY()<0 || snakePointsList.get(0).getPositionY()>=surfaceView.getHeight() || snakePointsList.get(0).getPositionX()>=surfaceView.getWidth()){
            gameOver=true;
        }else{
            for (int i=1;i<snakePointsList.size();i++){
                if(headPositionX==snakePointsList.get(i).getPositionX()&&headPositionY==snakePointsList.get(i).getPositionY()){
                    gameOver=true;
                    break;
                }
            }
        }
        return gameOver;
    }
    private Paint createPointColor(){
        if(pointColor == null){
            pointColor=new Paint();
            pointColor.setColor(snakeColor);
            pointColor.setStyle(Paint.Style.FILL);
            pointColor.setAntiAlias(true);
            //smoothness
        }
        return pointColor;
    }
    private Paint createFoodColor(){
        if(foodColors == null){
            foodColors=new Paint();
            foodColors.setColor(foodColor);
            foodColors.setStyle(Paint.Style.FILL);
            foodColors.setAntiAlias(true);
            //smoothness
        }
        return foodColors;
    }
}
