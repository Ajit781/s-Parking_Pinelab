/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package utilities.numberPlateScanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.vision.text.TextBlock;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data_objects.SParkingAgentModel;
import utilities.camera.GraphicOverlay;

/**
 * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
 * overlay view.
 */
public class OcrGraphic extends GraphicOverlay.Graphic {

    private static final int TEXT_COLOR = Color.WHITE;

    private static Paint sRectPaint;
    private static Paint sTextPaint;
    private final TextBlock mText;
    public String text;
    Activity context;
    Intent myIntent;
    SParkingAgentModel dataModel = SParkingAgentModel.getInstance();

    OcrGraphic(GraphicOverlay overlay, TextBlock text) {
        super(overlay);

        mText = text;

        if (sRectPaint == null) {
            sRectPaint = new Paint();
            sRectPaint.setColor(TEXT_COLOR);
            sRectPaint.setStyle(Paint.Style.STROKE);
            sRectPaint.setStrokeWidth(4.0f);
        }

        if (sTextPaint == null) {
            sTextPaint = new Paint();
            sTextPaint.setColor(TEXT_COLOR);
            sTextPaint.setTextSize(30.0f);
        }
        // Redraw the overlay, as this graphic has been added.
        postInvalidate();
    }

    public void saveContext(Activity con){
        context = con;
    }
    /**
     * Draws the text block annotations for position, size, and raw value on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        if (mText == null) {
            return;
        }

        // Draws the bounding box around the TextBlock.
        RectF rect = new RectF(mText.getBoundingBox());
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);
        canvas.drawRect(rect, sRectPaint);


        //validation setting*/
        String REGEX = "^[A-Z]{2}\\s?[0-9]{1,2}\\s?([A-Z]{1,2}|[A-Z]{2,3})\\s?[0-9]{4}$"; //regular expression
        //String REGEX = "^[A-Z]{2}[ -][0-9]{1,2}(?: [A-Z])?(?: [A-Z]*){1,2}?[0-9]{4}$"; //regular expression
        Pattern number; //a pattern of compiled regex
        Matcher matcher; //helps in matching the regex
        text = mText.getValue();

        Matcher m = Pattern.compile("[\n]").matcher(text);
        text = m.replaceAll(" ");
       /* //fixing
        Matcher m = Pattern.compile("[-][0-9]{2}[-]|[-]|[\n]").matcher(text);
        text = m.replaceAll(" ");
        m = Pattern.compile("WB|JH|CG|OD|JK|AS|BR|MH").matcher(text);
        text = m.replaceAll("");*/

        //final touch
        text = Pattern.compile("\\s").matcher(text).replaceAll(" ");
        text = text.replaceAll("( +)", " ").trim();
        //number detection

        number = Pattern.compile(REGEX);
        matcher = number.matcher(text);
        if (matcher.matches()) { //print if valid
            canvas.drawText(text, rect.centerX(), rect.bottom, sTextPaint); //draw on screen
            dataModel.scanVehicle = true;
            dataModel.scanVehicleNumber = text;
            Log.e("ScannedVehicleNumber",dataModel.scanVehicleNumber);
          /*  myIntent = new Intent(context, VehicleInfoScanActivity.class);
            //myIntent.putExtra("result", text); //Optional parameters
            context.startActivity(myIntent);*/

            Intent data = new Intent();
            String text = dataModel.scanVehicleNumber;
//---set the data to pass back---
            data.setData(Uri.parse(text));
            context.setResult(2, data);
//---close the activity---
            context.finish();


        }
    }


}
