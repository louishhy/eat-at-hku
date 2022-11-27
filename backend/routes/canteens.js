let express = require('express')
let router = express.Router()
let queryfunc = require('./canteenqueryfunc')
const EFFECTIVE_TIMESPAN_IN_MINUTES = 30
const NULL_IMAGE_URL = "images/nothing_here.png"

function null_image_substitution(doc) {
    if (doc.smallImgUrl === ""){
        doc.smallImgUrl = NULL_IMAGE_URL
    }

    if (doc.menuImgUrl === ""){
        doc.menuImgUrl = NULL_IMAGE_URL
    }

    return doc
}

// GET query functions
router.get('/get_all_canteens', async (req, res) => {
    try{
        let canteensCollection = req.db.get('canteens')
        let sortby = req.query.sortby
        
        let docs = await canteensCollection.find()
        for (let canteen of docs){
            canteen.ranking = await queryfunc.getRanking(req.db, canteen._id)
            canteen.congestionRanking = await queryfunc.getCongestionRanking(req.db, canteen._id, EFFECTIVE_TIMESPAN_IN_MINUTES)
            canteen = null_image_substitution(canteen)
        }

        if (sortby == "alphabetical"){
            docs.sort((a, b) => {
                if (a.canteenName < b.canteenName) return -1
                else return 1
            })
        }
        else if (sortby == "ranking"){
            docs.sort((a, b) => {
                if (a.ranking < b.ranking) return 1
                else return -1
            })
        }
        else if (sortby == "congestion"){
            docs.sort((a, b) => {
                if (a.congestionRanking < b.congestionRanking) return -1
                else return 1
            })
        }
        
        res.json(docs)
        
    } 
    catch (err){
        console.trace(err)
        res.status(500).send("Internal db error")
    }
})


router.get('/ranking', async (req, res) => {
    let db = req.db
    let canteenID = req.query.canteenID
    let ranking = await queryfunc.getRanking(db, canteenID)
    res.json({"ranking": ranking})
})


router.get('/comments', async (req, res) => {
    let db = req.db
    let canteenID = req.query.canteenID
    let docs = await queryfunc.getComments(db, canteenID)
    docs.sort((a, b) => {
        let a_date = Date(a.time)
        let b_date = Date(b.time)
        if (a_date < b_date) return 1
        else return -1
    })
    res.json(docs)
})


router.get('/congestion', async (req, res) => {
    let db = req.db
    let canteenID = req.query.canteenID
    let ranking = await queryfunc.getCongestionRanking(db, canteenID, EFFECTIVE_TIMESPAN_IN_MINUTES)
    res.json({"ranking": ranking})
})


router.get('/congestionReport', async (req, res) => {
    let db = req.db
    let canteenID = req.query.canteenID
    let docs = await queryfunc.getCongestionReports(db, canteenID, -1)
    res.json(docs)
})

router.get('/canteenLargeImage', async (req, res) => {
    let db = req.db
    let canteenID = req.query.canteenID
    let canteensCollection = db.get('canteens')
    let docs = await canteensCollection.find({"_id": canteenID})
    if (docs.length != 0){
        let url = docs[0].largeImgUrl
        res.json({"url": url})
    }
    else{
        res.status(400).send("Check your canteen ID")
    }
})

router.get('/canteenSmallImage', async (req, res) => {
    let db = req.db
    let canteenID = req.query.canteenID
    let canteensCollection = db.get('canteens')
    let docs = await canteensCollection.find({"_id": canteenID})
    if (docs.length != 0){
        let doc = docs[0]
        doc = null_image_substitution(doc)
        let url = doc.smallImgUrl
        res.json({"url": url})
    }
    else{
        res.status(400).send("Check your canteen ID")
    }
})


router.get('/menuImage', async (req, res) => {
    let db = req.db
    let canteenID = req.query.canteenID
    let canteensCollection = db.get('canteens')
    let docs = await canteensCollection.find({"_id": canteenID})
    if (docs.length != 0){
        let doc = docs[0]
        doc = null_image_substitution(doc)
        let url = doc.menuImgUrl
        res.json({"url": url})
    }
    else{
        res.status(400).send("Check your canteen ID")
    }
})



//POST update functions
router.post('/postcomment', async function(req, res){
    /* 
    POST syntax:
    applications/json
    {
        canteenID: str,
        username: str,
        content: str - the content of the review,
        ranking: double
    }
    */
    let commentsCollection = req.db.get('comments')
    let info = req.body
    info.time = new Date()  // Record the time

    try{
        await commentsCollection.insert(info)
    }
    catch (err){
        console.trace(err)
        res.status(500).send("Error in inserting the record")
    }
    
    res.json({"result": "succeed"})
})

router.post('/postcongestion', async function(req, res){
    /* 
    POST syntax:
    applications/json
    {
        canteenID: str,
        userID: str,
        congestionRanking: double
    }
    */
    let congestionReportsCollection = req.db.get('congestionReports')
    let info = req.body
    info.time = new Date()  // Change into date format

    try{
        await congestionReportsCollection.insert(info)
    }
    catch (err){
        console.trace(err)
        res.status(500).send("Error in inserting the record")
    }
    
    res.json({"result": "succeed"})
})

module.exports = router