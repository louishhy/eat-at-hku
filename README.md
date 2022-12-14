# Eat@HKU

A HKU COMP3330 prototype application for HKU crowdsourced canteen congestion info retrieval, commenting, and menu exploration.

<p align="center">
  <img src="https://user-images.githubusercontent.com/26505854/204704927-b67c3a69-bcf0-4edf-a853-d09d4c5d782e.png" width=500/>
</p>

## Grouping
Group 11

COMP3330 2022 Sem1

Android Development: Zhu Wendi

Backend Development: Huang Haoyu

## Running instructions
### Android application frontend
```/app``` includes the Android sourcecode which can be opened using Android Studio.

### Backend
```/backend``` includes the MongoDB data and Express.JS server. 

To run the MongoDB database which is in ```/data```,  install MongoDB with a compatible version (Native: __MongoDB 6.0.2 Community__). Then, in your command line:
```
mongod --dbpath YourPath/eat-at-hku/backend/data
```

To run the Express.JS server, install node.js and run:
```
node YourPath/eat-at-hku/backend/app.js
```
