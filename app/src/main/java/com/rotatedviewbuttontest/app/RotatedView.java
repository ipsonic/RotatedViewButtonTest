package com.rotatedviewbuttontest.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

/**
 * Created by jipson on 6/22/14.
 */
public class RotatedView extends RelativeLayout {
    ImageButton closeButton;
    RelativeLayout layoutFrame;
    int rotation = 170;
    Point rotatedPoint;

    public RotatedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ((Activity) context).getLayoutInflater().inflate(R.layout.rotated_layout_test, this, true);

        closeButton = (ImageButton) findViewById(R.id.close_button);
        layoutFrame = (RelativeLayout) findViewById(R.id.layout_frame);


        closeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d("CloseButton", "x,y: " + motionEvent.getX() + "," + motionEvent.getY());
                Log.d("CloseButton", "raw x,y: " + motionEvent.getRawX() + "," + motionEvent.getRawY());
                return false;
            }
        });

        Animation animation = new RotateAnimation(rotation,rotation,
                layoutFrame.getLayoutParams().width/2, layoutFrame.getLayoutParams().height/2);

        animation.setFillAfter(true);
        animation.setDuration(0);
        layoutFrame.setAnimation(animation);
        animation.start();

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //set new size of bounding box:
                int[] newSize = bBoxDimension(getLayoutParams().width, getLayoutParams().height, rotation);
                Log.d("", "after animation: " + newSize[0] + " " + newSize[1]);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
                layoutParams.height = newSize[1];
                layoutParams.width = newSize[0];
                setLayoutParams(layoutParams);
                int[] closeButtonLocation = new int[2];

                Rect closeButtonRect = new Rect();
                closeButton.getGlobalVisibleRect(closeButtonRect);

                Rect layoutRect = new Rect();
                layoutFrame.getGlobalVisibleRect(layoutRect);

                Point closeButtonPoint = new Point(closeButtonRect.centerX(), closeButtonRect.centerY());

                rotatedPoint = rotate(new Matrix(), layoutRect, closeButtonPoint, rotation);
                Log.d("rotated view", "rotated point: " + rotatedPoint.x + "," + rotatedPoint.y);


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //translate the center touch
                float x = (float) rotatedPoint.x - motionEvent.getRawX();
                float y = (float) rotatedPoint.y - motionEvent.getRawY();

                x = FloatMath.sqrt(x * x);
                y = FloatMath.sqrt(y * y);

                float spacing = FloatMath.sqrt(x*x + y*y);

                if ( spacing < 10  ){
                    Log.d("delete", "delete that bitch");
                }

                Log.d("RotatedView", "x,y: " + motionEvent.getRawX() + "," + motionEvent.getRawY());
                return false;
            }
        });



    }

    public static int[] bBoxDimension(int width, int height, float radianAngle){
        double c = Math.abs(Math.cos(radianAngle));
        double s = Math.abs(Math.sin(radianAngle));
        int[] dimensions = new int[2];
        dimensions[0] = (int) (height * s) + (int) (width * c);
        dimensions[1] = (int) (height * c) + (int) (width * s);
        return dimensions;
    }

    public static int[] closeButtonPoint(int startingX, int startingY, int cx, int cy, float angle){
        int x1 = startingX - cx;
        int y1 = startingY - cy;
        int newXY[] = new int[2];
        newXY[0] = (int) (cx+ x1*Math.cos(angle) - y1*Math.sin(angle));
        newXY[1] = (int) (cy+ y1*Math.cos(angle) + x1*Math.sin(angle));
        return newXY;
    }

    public Point rotate(Matrix transform, Rect myRect, Point myPoint, float degrees)
    {
        // This is to rotate about the Rectangles center
        transform.setRotate(degrees, myRect.exactCenterX(),     myRect.exactCenterY());

        // Create new float[] to hold the rotated coordinates
        float[] pts = new float[2];

        // Initialize the array with our Coordinate
        pts[0] = myPoint.x;
        pts[1] = myPoint.y;

        // Use the Matrix to map the points
        transform.mapPoints(pts);

        // NOTE: pts will be changed by transform.mapPoints call
        // after the call, pts will hold the new cooridnates

        // Now, create a new Point from our new coordinates
        Point newPoint = new Point((int)pts[0], (int)pts[1]);

        // Return the new point
        return newPoint;
    }

}
