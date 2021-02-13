package com.example.towerofhanoi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("ClickableViewAccessibility")  //For å unngå warning om at performClick må håndteres for ImageView
public class MainActivity extends AppCompatActivity {

    private int moves = 0;                             //The number of moves
    private TextView tvMoves;

    private Timer timer;
    private TimerTask timerTask;
    private long time = 0;
    boolean timerStarted = false;
    private TextView tvElapsedTime;
    private Button startButton;
    private boolean running;

    private LinearLayout left;
    private LinearLayout middle;
    private LinearLayout right;

    private ImageView small_ring;
    private ImageView middle_ring;
    private ImageView large_ring;

    private androidx.appcompat.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //setter onTouchListener on 3 disks(Image).Brukes til å starte "drag and drop"
        large_ring = findViewById(R.id.large_ring);
        large_ring.setOnTouchListener(new MyTouchListener());

        middle_ring = findViewById(R.id.middle_ring);
        middle_ring.setOnTouchListener(new MyTouchListener());

        small_ring = findViewById(R.id.small_ring);
        small_ring.setOnTouchListener(new MyTouchListener());

        //Setter onDragListenerpå alle typer av Linear Layout(3 konteiner)
        left = findViewById(R.id.left);
        left.setOnDragListener(new MyDragListener());

        middle = findViewById(R.id.middle);
        middle.setOnDragListener(new MyDragListener());

        right = findViewById(R.id.right);
        right.setOnDragListener(new MyDragListener());

        //Textview to show number of moves
        tvMoves = findViewById(R.id.tvMoves);

        //Textview to show seconds
        tvElapsedTime = findViewById(R.id.tvElapsedTime);

        startButton = findViewById(R.id.btnStart);
        Toast.makeText(this, "Trykk på start for å spille", Toast.LENGTH_SHORT).show();
        movesCount();
        running = false;

    }

    //Check if the ring is on the top, otherwise it cannot be moved
    private class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View viewToBeDragged, MotionEvent motionEvent) {
           LinearLayout owner = (LinearLayout) viewToBeDragged.getParent();
           View top =  owner.getChildAt(0);

           if (running && viewToBeDragged == top || owner.getChildCount() == 1) {
            //if(motionEvent.getAction() == motionEvent.ACTION_DOWN) {
                //Data som kan sendes med til drag-receiver
                ClipData data = ClipData.newPlainText("", "");

                //lager en drag-skygge av viewet som skal dras
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(viewToBeDragged);

                //started Drag and Drop, alle View som implementerer OnDragListener vil motta onDrag-events
                viewToBeDragged.startDrag(data, shadowBuilder, viewToBeDragged, 0);

                //Gjør bildet som dragges usynlig
                viewToBeDragged.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }
    }


    //****Check
    private class MyDragListener implements View.OnDragListener {
        Drawable enterShape = getResources().getDrawable(R.drawable.shape_droptarget);
        Drawable normalShape = getResources().getDrawable(R.drawable.shape);

        @Override
        public boolean onDrag(View view, DragEvent event) {
            int action = event.getAction();
            //View draggedView = (View) event.getLocalState();

            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // Skjer ingenting, skal ikke kunne dra rektanglene.
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    // Skjer ingenting, skal ikke kunne dra rektanglene.
                    view.setBackgroundDrawable(enterShape);
                    break;

                case DragEvent.ACTION_DRAG_EXITED:
                    view.setBackgroundDrawable(normalShape);
                    break;

                case DragEvent.ACTION_DROP:

                    //Bildet som blir dradd:.
                    View draggedView = (View) event.getLocalState();
                    View topElement = null;

                    //Reassign View to ViewGroup
                    ViewGroup owner = (ViewGroup) draggedView.getParent();
                    owner.removeView(draggedView);
                    LinearLayout container = (LinearLayout) view;

                    if (container.getChildCount() > 0) {
                        topElement = container.getChildAt(0);
                        if(topElement.getWidth() > draggedView.getWidth()){
                            container.addView(draggedView, 0); //set view at top
                            draggedView.setVisibility(View.VISIBLE);
                            moves++;
                        }else{
                            owner.addView(draggedView, 0);
                            draggedView.setVisibility(View.VISIBLE);
                        }
                    } else { //(hvis det er ingen ringer i container)
                        container.addView(draggedView);
                        draggedView.setVisibility(View.VISIBLE);
                        moves++;
                    }

                    if(right.getChildCount() == 3){
                        stopTime(tvElapsedTime);
                        small_ring.setOnTouchListener(null);
                        Toast.makeText(MainActivity.this, "Du har vunnet. Klokke blir nullstilt", Toast.LENGTH_SHORT).show();
                        resetGame();
                    }
                    movesCount();
                    break;

                 //DENNE GJØR AT MAN FÅR EN "TILBAKEANIMASJON"
                 case DragEvent.ACTION_DRAG_ENDED:
                     view.setBackgroundDrawable(normalShape);   //Sett view synlig igjen.
                     break;

                 default:
                    break;
            }
            return true;
        }
    }
    public void startTimer(View view) {
        timer = new Timer();
        timerTask = new TimerTask(){
            @Override
            public void run() {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                       time++;

                        //vise klokke i skjermen
                        tvElapsedTime.setText("Sekunder: " + String.valueOf(time));
                    }
                });

            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000); //period: 10000 millisekund = 1 sekund

    }

    public void stopTime(View view)
    {
        AlertDialog.Builder resetAlert = new AlertDialog.Builder(this);
        resetAlert.setPositiveButton("Stop", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                if(timerTask != null)
                {
                    timerTask.cancel();
                    time = 0;
                    timerStarted = false;
                    tvElapsedTime.setText("Sekunder: 0");

                }
            }
        });

        resetAlert.show();
    }

    public void startStopTapped(View view)
    {
        if(timerStarted == false)
        {
            timerStarted = true;
            startTimer(tvElapsedTime);
        }
        else
        {
            timerStarted = false;
            timerTask.cancel(); //Terminate the timer thread
        }
    }

    public void startGame(View v){
        if(!running){
            startStopTapped(tvElapsedTime);
            running = true;
            startButton.setText("Reset");
        } else{
            resetGame();
        }
    }

    private void resetGame() {
        stopTime(tvElapsedTime);
        startButton.setText("Start");
        moves = 0;
        running = false;
        movesCount();
        small_ring.setOnTouchListener(null);
        resetRings();
    }

    private void resetRings() {
        left.removeAllViews();
        middle.removeAllViews();
        right.removeAllViews();
        left.addView(small_ring, 0);
        left.addView(middle_ring, 1);
        left.addView(large_ring, 2);
    }

    private void movesCount() {
        tvMoves.setText("Antall flytt: " + moves);
    }
}



