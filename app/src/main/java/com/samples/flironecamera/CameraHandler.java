/*******************************************************************
 * @title FLIR THERMAL SDK
 * @file CameraHandler.java
 * @Author FLIR Systems AB
 *
 * @brief Helper class that encapsulates *most* interactions with a FLIR ONE camera
 *
 * Copyright 2019:    FLIR Systems
 ********************************************************************/
package com.samples.flironecamera;

import android.graphics.Bitmap;
import android.util.Log;

import com.flir.thermalsdk.androidsdk.image.BitmapAndroid;
import com.flir.thermalsdk.image.JavaImageBuffer;
import com.flir.thermalsdk.image.ThermalImage;
import com.flir.thermalsdk.image.fusion.FusionMode;
import com.flir.thermalsdk.live.Camera;
import com.flir.thermalsdk.live.CommunicationInterface;
import com.flir.thermalsdk.live.ConnectParameters;
import com.flir.thermalsdk.live.Identity;
import com.flir.thermalsdk.live.connectivity.ConnectionStatusListener;
import com.flir.thermalsdk.live.discovery.DiscoveryEventListener;
import com.flir.thermalsdk.live.discovery.DiscoveryFactory;
import com.flir.thermalsdk.live.streaming.ThermalImageStreamListener;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Encapsulates the handling of a FLIR ONE camera or built in emulator, discovery, connecting and start receiving images.
 * All listeners are called from Thermal SDK on a non-ui thread
 * <p/>
 * Usage:
 * <pre>
 * Start discovery of FLIR FLIR ONE cameras or built in FLIR ONE cameras emulators
 * {@linkplain #startDiscovery(DiscoveryEventListener, DiscoveryStatus)}
 * Use a discovered Camera {@linkplain Identity} and connect to the Camera
 * (note that calling connect is blocking and it is mandatory to call this function from a background thread):
 * {@linkplain #connect(Identity, ConnectionStatusListener)}
 * Once connected to a camera
 * {@linkplain #startStream(StreamDataListener)}
 * </pre>
 * <p/>
 * You don't *have* to specify your application to listen or USB intents but it might be beneficial for you application,
 * we are enumerating the USB devices during the discovery process which eliminates the need to listen for USB intents.
 * See the Android documentation about USB Host mode for more information
 * <p/>
 * Please note, this is <b>NOT</b> production quality code, error handling has been kept to a minimum to keep the code as clear and concise as possible
 */
class CameraHandler {

    private static final String TAG = "CameraHandler";

    private StreamDataListener streamDataListener;

    public interface StreamDataListener {
        void images(FrameDataHolder dataHolder);
        void images(Bitmap msxBitmap, Bitmap dcBitmap);
    }

    //Discovered FLIR cameras
    LinkedList<Identity> foundCameraIdentities = new LinkedList<>();

    //A FLIR Camera
    private Camera camera;


    public interface DiscoveryStatus {
        void started();
        void stopped();
    }

    public CameraHandler() {
    }

    /**
     * Start discovery of USB and Emulators
     */
    public void startDiscovery(DiscoveryEventListener cameraDiscoveryListener, DiscoveryStatus discoveryStatus) {
        DiscoveryFactory.getInstance().scan(cameraDiscoveryListener, CommunicationInterface.EMULATOR, CommunicationInterface.USB);
        discoveryStatus.started();
    }

    /**
     * Stop discovery of USB and Emulators
     */
    public void stopDiscovery(DiscoveryStatus discoveryStatus) {
        DiscoveryFactory.getInstance().stop(CommunicationInterface.EMULATOR, CommunicationInterface.USB);
        discoveryStatus.stopped();
    }

    public void connect(Identity identity, ConnectionStatusListener connectionStatusListener) throws IOException {
        camera = new Camera();
        camera.connect(identity, connectionStatusListener, new ConnectParameters());
    }

    public void disconnect() {
        if (camera == null) {
            return;
        }
        if (camera.isGrabbing()) {
            camera.unsubscribeAllStreams();
        }
        camera.disconnect();
    }

    /**
     * Start a stream of {@link ThermalImage}s images from a FLIR ONE or emulator
     */
    public void startStream(StreamDataListener listener) {
        this.streamDataListener = listener;
        camera.subscribeStream(thermalImageStreamListener);
    }

    /**
     * Stop a stream of {@link ThermalImage}s images from a FLIR ONE or emulator
     */
    public void stopStream(ThermalImageStreamListener listener) {
        camera.unsubscribeStream(listener);
    }

    /**
     * Add a found camera to the list of known cameras
     */
    public void add(Identity identity) {
        foundCameraIdentities.add(identity);
    }

    @Nullable
    public Identity get(int i) {
        return foundCameraIdentities.get(i);
    }

    /**
     * Get a read only list of all found cameras
     */
    @Nullable
    public List<Identity> getCameraList() {
        return Collections.unmodifiableList(foundCameraIdentities);
    }

    /**
     * Clear all known network cameras
     */
    public void clear() {
        foundCameraIdentities.clear();
    }

    @Nullable
    public Identity getCppEmulator() {
        for (Identity foundCameraIdentity : foundCameraIdentities) {
            if (foundCameraIdentity.deviceId.contains("C++ Emulator")) {
                return foundCameraIdentity;
            }
        }
        return null;
    }

    @Nullable
    public Identity getFlirOneEmulator() {
        for (Identity foundCameraIdentity : foundCameraIdentities) {
            if (foundCameraIdentity.deviceId.contains("EMULATED FLIR ONE")) {
                return foundCameraIdentity;
            }
        }
        return null;
    }

    @Nullable
    public Identity getFlirOne() {
        for (Identity foundCameraIdentity : foundCameraIdentities) {
            boolean isFlirOneEmulator = foundCameraIdentity.deviceId.contains("EMULATED FLIR ONE");
            boolean isCppEmulator = foundCameraIdentity.deviceId.contains("C++ Emulator");
            if (!isFlirOneEmulator && !isCppEmulator) {
                return foundCameraIdentity;
            }
        }

        return null;
    }

    private void withImage(ThermalImageStreamListener listener, Camera.Consumer<ThermalImage> functionToRun) {
        camera.withImage(listener, functionToRun);
    }


    /**
     * Called whenever there is a new Thermal Image available, should be used in conjunction with {@link Camera.Consumer}
     */
    private final ThermalImageStreamListener thermalImageStreamListener = new ThermalImageStreamListener() {
        @Override
        public void onImageReceived() {
            //Will be called on a non-ui thread
            Log.d(TAG, "onImageReceived(), we got another ThermalImage");
            withImage(this, handleIncomingImage);
        }
    };

    /**
     * Function to process a Thermal Image and update UI
     */
    private final Camera.Consumer<ThermalImage> handleIncomingImage = new Camera.Consumer<ThermalImage>() {
        @Override
        public void accept(ThermalImage thermalImage) {
            Log.d(TAG, "accept() called with: thermalImage = [" + thermalImage.getDescription() + "]");
            //Will be called on a non-ui thread,
            // extract information on the background thread and send the specific information to the UI thread

            //Get a bitmap with the fusion mode selected
            Bitmap fusBitmap;
            {
                switch(textHendler.find_line_txt(textHendler.Data_Path, textHendler.Data_txt, textHendler.Fusion_Mode_Line)) {
                    case "Thermal Only":
                        thermalImage.getFusion().setFusionMode(FusionMode.THERMAL_ONLY);
                        break;
                    case "Visual Only":
                        thermalImage.getFusion().setFusionMode(FusionMode.VISUAL_ONLY);
                        break;
                    case "Msx":
                        thermalImage.getFusion().setFusionMode(FusionMode.MSX);
                        break;
                    case "Fusion":
                        thermalImage.getFusion().setFusionMode(FusionMode.THERMAL_FUSION);
                        break;
                    case "Blending":
                        thermalImage.getFusion().setFusionMode(FusionMode.BLENDING);
                        break;
                    case "Picture In Picture":
                        thermalImage.getFusion().setFusionMode(FusionMode.PICTURE_IN_PICTURE);
                        break;
                    case "Color Night Vision":
                        thermalImage.getFusion().setFusionMode(FusionMode.COLOR_NIGHT_VISION);
                        break;
                    default:
                        thermalImage.getFusion().setFusionMode(FusionMode.VISUAL_ONLY);
                        Log.d(TAG, "Selected mode is not defined, set default to VISUAL");
                }
                JavaImageBuffer tmp = thermalImage.getImage();
                BitmapAndroid anBitmap = BitmapAndroid.createBitmap(tmp);
                fusBitmap = anBitmap.getBitMap();
            }

            //Get a bitmap with only DC
            //Get a bitmap with the visual image, it might have different dimensions then the bitmap from THERMAL_ONLY
            Bitmap dcBitmap;
            {
                thermalImage.getFusion().setFusionMode(FusionMode.VISUAL_ONLY);
                dcBitmap = BitmapAndroid.createBitmap(thermalImage.getFusion().getPhoto()).getBitMap();
            }

            /*
            // save image on video
            Bitmap dcVideo = dcBitmap;
            Bitmap fusVideo = fusBitmap;
            {
                //get if we need to record line
                boolean doVideo;
                switch(textHendler.find_line_txt(textHendler.Data_Path, textHendler.Data_txt, textHendler.To_Record_Line))
                {
                    case "T":
                        doVideo = true;
                        break;
                    default:
                        doVideo = false;
                }
                if (doVideo)
                {
                    String dirName = textHendler.find_line_txt(textHendler.Data_Path, textHendler.Data_txt, textHendler.Record_Dir_Name);
                    String count = textHendler.find_line_txt(textHendler.Data_Path, textHendler.Data_txt, textHendler.Record_Frame_Counter);

                    // load path
                    File filepath = Environment.getExternalStorageDirectory();
                    // load sub dirs
                    File dir_dc = new File(filepath.getAbsolutePath()+"/SIPL_FlirOne/video" + dirName + "/dc");
                    File dir_fus = new File(filepath.getAbsolutePath()+"/SIPL_FlirOne/video" + dirName + "/fus");

                    // create files path
                    File file_dc = new File(dir_dc, count+".png");
                    File file_fus = new File(dir_fus, count+ ".png");

                    OutputStream outputStream = null;
                    // create stream to the path and save the image
                    try {
                        // create the stream
                        outputStream = new FileOutputStream(file_dc);
                        // save image
                        dcVideo.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

                        // for fus image
                        outputStream = new FileOutputStream(file_fus);
                        fusVideo.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                        // success saving all the image

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        // close stream
                        if(outputStream != null) {
                            try {
                                outputStream.flush();
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    int i=Integer.parseInt(count);
                    i++;
                    textHendler.update_line_txt(textHendler.Data_Path, textHendler.Data_txt, textHendler.Record_Frame_Counter, String.valueOf(i));
                }

            } */


            Log.d(TAG,"adding images to cache");
            streamDataListener.images(dcBitmap, fusBitmap);
        }
    };


}
