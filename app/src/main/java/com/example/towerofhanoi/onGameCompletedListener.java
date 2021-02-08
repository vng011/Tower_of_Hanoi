package com.example.towerofhanoi;

//Custom listener to inform GameActivity when the puzzle has been solved.
public interface onGameCompletedListener {
        void onGameComplete(boolean isOptimal, int moves, int numDisks);

}
