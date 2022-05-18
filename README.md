# FlirOne_app

![image](https://i.ytimg.com/vi/IY-SLw2c2vU/maxresdefault.jpg)

this app was build to extract the most basic, raw data the FlirOne can provide. in order to use in Image Processing and Deep Learning in The Technion installation, under Bachelor Projects in SIPL lab.

It was use to create a dataset for semantic segmentation, to better distinguish objects like grass and trees. the grass/tree problem is a knowning one in this subject, they look alike and difficult to differentiate for must NN. and by useing the heat scall (grass usually wormer then trees) the model can easly segment between them.


the code have 2 main features. it can produce single image or a "video" like feature, that create a directory with a steady stream of images like a video. in order to keep the data as raw a pusioble.
in addition, the user have axess for all the build in filters the FilrOne SDK provide.

PC             |  human | filters
:-------------------------:|:-------------------------:|:-------------------------:
![](https://github.com/BIueMan/FlirOne_app/blob/master/imags/phone_app_pc.png)  |  ![](https://github.com/BIueMan/FlirOne_app/blob/master/imags/phone_app_man.png) | ![](https://github.com/BIueMan/FlirOne_app/blob/master/imags/phone_app_filters.png)

code:
* MainActivity - main code
* SaveImagesOnBackGround - save images on the Thread. video will not create a video file, but a directory with alot of images, that because we want to have a raw data without compression. 
* textHendler - a bad implementation of JSON files. but it was fun to build :)


As can be seen in this image, the grass are warmer then the trees (the grass is lighter then the trees in the heat image)
RGB Image            |  Raw Heat image
:-------------------------:|:-------------------------:
![](https://github.com/BIueMan/FlirOne_app/blob/master/imags/grass_rgb.png)  |  ![](https://github.com/BIueMan/FlirOne_app/blob/master/imags/grass_heat.png)
![](https://github.com/BIueMan/FlirOne_app/blob/master/imags/tree_rgb.png) | ![](https://github.com/BIueMan/FlirOne_app/blob/master/imags/tree_hear.png)
![](https://github.com/BIueMan/FlirOne_app/blob/master/imags/playhround_rgb.png) | ![](https://github.com/BIueMan/FlirOne_app/blob/master/imags/playgound_heat.png)
