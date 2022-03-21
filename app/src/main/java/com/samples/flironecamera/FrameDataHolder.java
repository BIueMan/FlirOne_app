/*******************************************************************
 * @title FLIR THERMAL SDK
 * @file FrameDataHolder.java
 * @Author FLIR Systems AB
 *
 * @brief Container class that holds references to Bitmap images
 *
 * Copyright 2019:    FLIR Systems
 ********************************************************************/

package com.samples.flironecamera;

import android.graphics.Bitmap;

import java.util.ArrayList;

class FrameDataHolder {

    //public final Bitmap msxBitmap;
    public final Bitmap dcBitmap;
    public final Bitmap fusBitmap;

    FrameDataHolder(/*Bitmap msxBitmap,*/ Bitmap dcBitmap, Bitmap fusBitmap){
        //this.msxBitmap = msxBitmap;
        this.dcBitmap = dcBitmap;
        this.fusBitmap = fusBitmap;
    }
}

// if you adding here a mode, add it also to the "CameraHandler" save image func. in the switch case
class FusionModesList {
    final ArrayList<String> List = new ArrayList<>();

    FusionModesList() {
        List.add("Thermal Only");
        List.add("Visual Only");
        List.add("Msx");
        List.add("Thermal Fusion");
        List.add("Blending");
        List.add("Picture In Picture");
        List.add("Color Night Vision");
    }
}