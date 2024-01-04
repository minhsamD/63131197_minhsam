package com.candycrush;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class gameplay extends AppCompatActivity {

    int[] candies={
            R.drawable.brownie,
            R.drawable.cake,
            R.drawable.chocolate,
            R.drawable.cookie,
            R.drawable.cupcake,
            R.drawable.icecream
    };
    int widthOfBlock,noOfBlocks =8,widthOfScreen;
    ArrayList<ImageView> candy = new ArrayList<>();
    int candyToBeDragged,candyToBeReplaced;
    Handler mHandler;
    int interval = 100;
    //    int notCandy=R.drawable.ic_launcher_background;
    int notCandy =R.drawable.transparent;
    TextView scoreResult;
    int score =0;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);



        scoreResult = findViewById(R.id.score);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        widthOfScreen = displayMetrics.widthPixels;
        int heightOfScreen = displayMetrics.heightPixels;
        widthOfBlock = widthOfScreen/noOfBlocks;
        createBoard();
        for(final ImageView imageView: candy){
            imageView.setOnTouchListener(new OnSwipListener(this){
                @Override
                void onSwipeLeft() {
                    super.onSwipeLeft();
                    candyToBeDragged = imageView.getId();
                    candyToBeReplaced = candyToBeDragged - 1;
                    applyAnimation(candy.get(candyToBeDragged), R.anim.slide_in_left, 400); // 500 milliseconds
                    applyAnimation(candy.get(candyToBeReplaced), R.anim.slide_in_right, 400); // 500 milliseconds
                    swapCandies(candyToBeDragged,candyToBeReplaced);
                    candyInterchange();
                }

                @Override
                void onSwipeRight() {
                    super.onSwipeRight();
                    candyToBeDragged = imageView.getId();
                    candyToBeReplaced = candyToBeDragged + 1;
                    applyAnimation(candy.get(candyToBeDragged), R.anim.slide_in_right, 400); // 500 milliseconds
                    applyAnimation(candy.get(candyToBeReplaced), R.anim.slide_in_left, 400); // 500 milliseconds
                    swapCandies(candyToBeDragged,candyToBeReplaced);
                    candyInterchange();
                }

                @Override
                void onSwipeTop() {
                    super.onSwipeTop();
                    candyToBeDragged = imageView.getId();
                    candyToBeReplaced = candyToBeDragged - noOfBlocks;
                    applyAnimation(candy.get(candyToBeDragged), R.anim.slide_in_top, 400); // 500 milliseconds
                    applyAnimation(candy.get(candyToBeReplaced), R.anim.slide_in_bottom, 400); // 500 milliseconds
                    swapCandies(candyToBeDragged,candyToBeReplaced);
                    candyInterchange();
                }

                @Override
                void onSwipeBottom() {
                    super.onSwipeBottom();
                    candyToBeDragged = imageView.getId();
                    candyToBeReplaced = candyToBeDragged + noOfBlocks;
                    applyAnimation(candy.get(candyToBeDragged), R.anim.slide_in_bottom, 400); // 500 milliseconds
                    applyAnimation(candy.get(candyToBeReplaced), R.anim.slide_in_top, 400); // 500 milliseconds
                    swapCandies(candyToBeDragged,candyToBeReplaced);
                    candyInterchange();
                }
            });
        }
        mHandler = new Handler();
        startRepeat();
    }
    private void swapCandies(int position1, int position2) {
        ImageView candy1 = candy.get(position1);
        ImageView candy2 = candy.get(position2);

        // Lưu trữ thông tin của candy1
        int candy1Tag = (int) candy1.getTag();
        int candy1Resource = candy1.getDrawable().getConstantState().hashCode();

        // Gán thông tin của candy2 cho candy1
        candy1.setImageResource(candy2.getDrawable().getConstantState().hashCode());
        candy1.setTag((int) candy2.getTag());

        // Gán thông tin của candy1 (đã lưu trữ trước đó) cho candy2
        candy2.setImageResource(candy1Resource);
        candy2.setTag(candy1Tag);
    }
    private void applyAnimation(ImageView imageView, int animationResource, int duration) {
        Animation animation = AnimationUtils.loadAnimation(this, animationResource);
        animation.setDuration(duration);
        imageView.startAnimation(animation);
    }


    private void checkRowForN(int n) {
        for (int i = 0; i < 64; i++) {
            int chosenCandy = (int) candy.get(i).getTag();
            boolean isBlank = (int) candy.get(i).getTag() == notCandy;
            List<Integer> notValid = Arrays.asList(6, 7, 14, 15, 22, 23, 30, 31, 38, 39, 46, 47, 54, 55);

            if (!notValid.contains(i % noOfBlocks) && i + n - 1 < 64) { // Kiểm tra chỉ hàng ngang
                int x = i;
                boolean isMatching = true;
                for (int j = 0; j < n; j++) {
                    if ((int) candy.get(x + j).getTag() != chosenCandy || isBlank) {
                        isMatching = false;
                        break;
                    }
                }

                if (isMatching) {
                    score += 10 * n;
                    scoreResult.setText(String.valueOf(score));
//                    for (int j = 0; j < n; j++) {
//                        applyAnimation(candy.get(x + j * noOfBlocks), R.anim.slide_in_top);
//                    }
                    for (int j = 0; j < n; j++) {
                        candy.get(x + j).setImageResource(notCandy);
                        candy.get(x + j).setTag(notCandy);


                    }
                }
            }
        }

        moveDownCandies();
    }

    private void checkColForN(int n) {
        for (int i = 0; i < 47; i++) {
            int chosenCandy = (int) candy.get(i).getTag();
            boolean isBlank = (int) candy.get(i).getTag() == notCandy;
            int x = i;
            boolean isMatching = true;
            for (int j = 0; j < n; j++) {
                if ((int) candy.get(x + j * noOfBlocks).getTag() != chosenCandy || isBlank) {
                    isMatching = false;
                    break;
                }
            }

            if (isMatching) {
                score += 10 * n;
                scoreResult.setText(String.valueOf(score));
//                for (int j = 0; j < n; j++) {
//                    applyAnimation(candy.get(x + j * noOfBlocks), R.anim.slide_in_top);
//                }
                for (int j = 0; j < n; j++) {
                    candy.get(x + j * noOfBlocks).setImageResource(notCandy);
                    candy.get(x + j * noOfBlocks).setTag(notCandy);

                }
            }
        }

        moveDownCandies();
    }
    private void moveDownCandies() {
        Integer[] firstRow = {0, 1, 2, 3, 4, 5, 6, 7};
        List<Integer> list = Arrays.asList(firstRow);
        for (int i = 55; i >= 0; i--) {
            if ((int) candy.get(i + noOfBlocks).getTag() == notCandy) {
                int destinationIndex = i + noOfBlocks;
                int sourceIndex = i;

                applyAnimation(candy.get(destinationIndex), R.anim.slide_in_top, 400); // 400 milliseconds

                candy.get(destinationIndex).setImageResource((int) candy.get(sourceIndex).getTag());
                candy.get(destinationIndex).setTag(candy.get(sourceIndex).getTag());
                candy.get(sourceIndex).setImageResource(notCandy);
                candy.get(sourceIndex).setTag(notCandy);

                if (list.contains(i) && (int) candy.get(i).getTag() == notCandy) {
                    int randomColor = (int) Math.floor(Math.random() * candies.length);
                    candy.get(i).setImageResource(candies[randomColor]);
                    candy.get(i).setTag(candies[randomColor]);
                }
            }
        }
        for (int i = 0; i < 8; i++) {
            if ((int) candy.get(i).getTag() == notCandy) {
                int randomColor = (int) Math.floor(Math.random() * candies.length);
                candy.get(i).setImageResource(candies[randomColor]);
                candy.get(i).setTag(candies[randomColor]);
            }
        }
    }

    private void checkPattern(int n, boolean isRow, boolean isCross, boolean isIncomplete, boolean isHorizontal, boolean isLeftDiagonal, boolean isRightDiagonal, boolean isUpsideDown) {
        int maxI = isRow ? noOfBlocks : noOfBlocks - n + 1;
        int maxJ = isRow ? noOfBlocks - n + 1 : noOfBlocks;

        for (int i = 0; i < maxI; i++) {
            for (int j = 0; j < maxJ; j++) {
                boolean isMatching = true;
                for (int k = 0; k < n; k++) {
                    int currentIndex = isRow ? i * noOfBlocks + (j + k) : (i + k) * noOfBlocks + j;

                    if (isCross) {
                        currentIndex = isHorizontal ? i * noOfBlocks + (j + k) : (i + k) * noOfBlocks + j;
                    } else if (isLeftDiagonal) {
                        currentIndex = (i + k) * noOfBlocks + (j + k);
                    } else if (isRightDiagonal) {
                        currentIndex = (i + k) * noOfBlocks + (j - k);
                    } else if (isUpsideDown) {
                        currentIndex = (i - k) * noOfBlocks + (j + k);
                    }

                    if ((int) candy.get(currentIndex).getTag() == notCandy) {
                        isMatching = false;
                        break;
                    }
                }

                if (isMatching) {
                    score += 10 * n;
                    scoreResult.setText(String.valueOf(score));
                    for (int k = 0; k < n; k++) {
                        int currentIndex = isRow ? i * noOfBlocks + (j + k) : (i + k) * noOfBlocks + j;

                        if (isCross) {
                            currentIndex = isHorizontal ? i * noOfBlocks + (j + k) : (i + k) * noOfBlocks + j;
                        } else if (isLeftDiagonal) {
                            currentIndex = (i + k) * noOfBlocks + (j + k);
                        } else if (isRightDiagonal) {
                            currentIndex = (i + k) * noOfBlocks + (j - k);
                        } else if (isUpsideDown) {
                            currentIndex = (i - k) * noOfBlocks + (j + k);
                        }

                        candy.get(currentIndex).setImageResource(notCandy);
                        candy.get(currentIndex).setTag(notCandy);
                    }
                }
            }
        }
    }

    Runnable repeatChecker = new Runnable() {
        @Override
        public void run() {
            try{
//                checkPattern(5, false, false, false, true, false, false, false); // Kiểm tra dấu cộng bị khuyết nằm ngang
//                checkPattern(5, false, false, false, false, true, false, false); // Kiểm tra dấu cộng bị khuyết nằm dọc với góc vuông hướng về bên trái
//                checkPattern(5, false, false, false, false, false, true, false); // Kiểm tra dấu cộng bị khuyết nằm dọc với góc vuông hướng về bên phải
//                checkPattern(5, false, false, false, false, false, false, true); // Kiểm tra dấu cộng bị khuyết nằm ngược
                checkColForN(5);
                checkRowForN(5);
                checkColForN(4);
                checkRowForN(4);
                checkColForN(3);
                checkRowForN(3);
                moveDownCandies();
            }
            finally{
                mHandler.postDelayed(repeatChecker,interval);
            }
        }
    };
    void startRepeat(){
        mHandler.removeCallbacks(repeatChecker); // Huỷ bỏ lần lặp trước đó
        mHandler.postDelayed(repeatChecker, interval); // Thiết lập lặp lại sau mỗi interval
    }
    private  void candyInterchange(){
        int background =(int) candy.get(candyToBeReplaced).getTag();
        int background1 =(int) candy.get(candyToBeDragged).getTag();
        candy.get(candyToBeDragged).setImageResource(background);
        candy.get(candyToBeReplaced).setImageResource(background1);
        candy.get(candyToBeDragged).setTag(background);
        candy.get(candyToBeReplaced).setTag(background1);
    }
    private  void createBoard(){
        GridLayout gridLayout = findViewById(R.id.board);
        gridLayout.setColumnCount(noOfBlocks);
        gridLayout.setRowCount(noOfBlocks);
        gridLayout.getLayoutParams().width = widthOfScreen;
        gridLayout.getLayoutParams().height = widthOfScreen;
        for(int i = 0;i<noOfBlocks*noOfBlocks;i++){
            ImageView imageView = new ImageView(this);
            imageView.setId(i);
            imageView.setLayoutParams(new
                    android.view.ViewGroup.LayoutParams(widthOfBlock,widthOfBlock));
            imageView.setMaxHeight(widthOfBlock);
            imageView.setMaxWidth(widthOfBlock);
            int randomCandy =(int)Math.floor(Math.random()*candies.length);
            imageView.setImageResource(candies[randomCandy]);
            imageView.setTag(candies[randomCandy]);
            candy.add(imageView);
            gridLayout.addView(imageView);
        }
    }
}