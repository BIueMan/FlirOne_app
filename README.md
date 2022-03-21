# FlirOne_app

README CODE WILL BE ADDED LATER


this app was build to extract the most basic, raw data the FlirOne can give as. in order to use in image prossing and deep learning in The Technion installation, under SIPL lab.

it was use to create a data for semantic segmentation, to better distinguish objects like grass and trees. the grass tree problem is a knowning one in the subject, they look alike for the NN. and by useing the heat scall (grass are wormer then trees) we can easly segment between them.


the code have 2 main Features, singal images, and a "video" that create a directory with alot of images, like a video in order to keep the data as raw a pusioble.
in addition, the user have axess for alot of filter the FilrOne SDK provide.

code:
MainActivity - main code
SaveImagesOnBackGround - save images on the Thread. video will not create a video file, but a directory with alot of images, that because we want to have a raw data without compression. 
textHendler - a bad implementation of JSON files. but it was fun to build :)
