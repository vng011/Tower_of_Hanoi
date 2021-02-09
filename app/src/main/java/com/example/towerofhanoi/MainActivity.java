package com.example.towerofhanoi;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("ClickableViewAccessibility")  //For å unngå warning om at performClick må håndteres for ImageView
public class MainActivity extends AppCompatActivity {

    private Timer timer;
    private TimerTask timerTask;
    private long time = 0;
    boolean timerStarted = false;
    private TextView tvElapsedTime;
    private TextView tvMoves;

    private int totalMoves, minPossibleMoves;
    //private int moves = 0;                             //The number of moves
    private static final int NUM_DISKS = 3;            //The number of disks for a certain game

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setter onTouchListener on 3 disks(Image).Brukes til å starte "drag and drop"
        ImageView disk1 = findViewById(R.id.disk1);
        disk1.setOnTouchListener(new MyTouchListener());

        ImageView disk2 = findViewById(R.id.disk2);
        disk2.setOnTouchListener(new MyTouchListener());

        ImageView disk3 = findViewById(R.id.disk3);
        disk3.setOnTouchListener(new MyTouchListener());

        //Setter onDragListenerpå alle typer av Linear Layout(3 konteiner)
        LinearLayout left = findViewById(R.id.left);
        left.setOnDragListener(new MyDragListener());

        LinearLayout middle = findViewById(R.id.middle);
        middle.setOnDragListener(new MyDragListener());

        LinearLayout right = findViewById(R.id.right);
        right.setOnDragListener(new MyDragListener());

        // Textview to show number of moves
        tvMoves = findViewById(R.id.tvMoves);

        //Textview to show seconds
        tvElapsedTime = findViewById(R.id.tvElapsedTime);

        // Possible min moves (2^n - 1); n number of disks
        minPossibleMoves = (int) (Math.pow(2, NUM_DISKS) - 1);
    }

    //***Check
    private class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View viewToBeDragged, MotionEvent motionEvent) {
            LinearLayout owner = (LinearLayout) viewToBeDragged.getParent();
            View top = owner.getChildAt(0);

            if (viewToBeDragged == top || owner.getChildCount() == 1) {
                if(motionEvent.getAction() == motionEvent.ACTION_DOWN) {
                    //Data som kan sendes med til drag-receiver
                    ClipData data = ClipData.newPlainText("", "");

                    //lager en drag-skygge av viewet som skal dras
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(top);

                    //started Drag and Drop, alle View som implementerer OnDragListener vil motta onDrag-events
                    top.startDrag(data, shadowBuilder, viewToBeDragged, 0);

                    //Gjør bildet som dragges usynlig
                    top.setVisibility(View.INVISIBLE);
                    return true;
                }

            } /* else {
                return false;
            }*/
            return false;
        /*    //starter Drag operation
            if(motionEvent.getAction() == motionEvent.ACTION_DOWN){
                //Data som kan sendes med til drag-receiver
                ClipData data = ClipData.newPlainText("", "");

                //lager en drag-skygge av viewet som skal dras
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(viewToBeDragged);

                //started Drag and Drop, alle View som implementerer OnDragListener vil motta onDrag-events
                viewToBeDragged.startDrag(data, shadowBuilder, viewToBeDragged, 0);

                //Gjør bildet som dragges usynlig
                viewToBeDragged.setVisibility(View.INVISIBLE);
                return true;
            }else {
                return false;
            }*/
        }
    }

    //****Check
    private class MyDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View view, DragEvent event) {
            int action = event.getAction();
            boolean dragInterrupted = false;

            //Konteiner som dragged View dras til((som er en av de tre LinearLayout-ene):
            LinearLayout receiveContainer = (LinearLayout) view;

            switch(action){
                case DragEvent.ACTION_DRAG_STARTED:
                    // Skjer ingenting, skal ikke kunne dra rektanglene.
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    // Skjer ingenting, skal ikke kunne dra rektanglene.
                    break;
                case DragEvent.ACTION_DROP:
                    View topElement = null;                           //Đề bài
                    LinearLayout container = (LinearLayout) view;     //Đề bài
                    if (container.getChildCount() > 0)                //Đề bài
                        topElement = container.getChildAt(0);   //Đề bài

                    //Bildet som blir dragg
                    View draggedView = (View) event.getLocalState();

                    // Settes denne til true avbrytes drag - her kan man altså sette en betingelse for avbrutt operasjon eller ikke!
                    String draggedViewTag = draggedView.getTag().toString();
                    String draggedToContainerTag = receiveContainer.getTag().toString();
                    //dragInterrupted = draggedViewTag.equals(?) && draggedToContainerTag.equals(?);

                    if (dragInterrupted) {
                        return false;
                    } else {
                        // owner er her foreldreview (en av de fire LinearLayout-objektene) til bildet som blir dradd,
                        // fjerner bildet fra ownerview:
                        ViewGroup owner = (ViewGroup) draggedView.getParent();
                        owner.removeView(draggedView);
                        // Legger draggedView til motakkskonteiner:
                        receiveContainer.addView(draggedView);
                    }
                    draggedView.setVisibility(View.VISIBLE);
                    break;

                //DENNE GJØR AT MAN FÅR EN "TILBAKEANIMASJON"
              /*  case DragEvent.ACTION_DRAG_ENDED:
                    draggedView.setVisibility(View.VISIBLE);    //Sett view synlig igjen.
                    break;*/

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
        /*resetAlert.setNeutralButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                //do nothing
            }
        });
        resetAlert.show();
        */
    }

    //Check if the puzzle has been completed.
    /*public void complete(){
        if (moves == Math.pow(2, NUM_DISKS) - 1){
            Toast.makeText(this, "You win. The game will stop automatically", Toast.LENGTH_SHORT).show();
            stopTime(tvElapsedTime);
        } else{
            Toast.makeText(this, "You lose. Play again.", Toast.LENGTH_SHORT).show();
            stopTime(tvElapsedTime);
        }
    }*/


    //Update the move counter with the current number of moves.
    public void updateText(int moves) {
        tvMoves.setText("Antall flytt: " + moves);
    }

    public void gameOver(int moves) {
        totalMoves = moves;
        showDialog();
    }

    public void showDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Game Over");

        if (totalMoves < minPossibleMoves)
            alert.setMessage("You won, congrats!!!!");

        alert.create().show();
    }
}
