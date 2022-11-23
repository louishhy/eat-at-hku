let express = require('express')
let router = express.Router()
let queryfunc = require('./canteenqueryfunc')
const EFFECTIVE_TIMESPAN_IN_MINUTES = 30

// GET query functions
router.get('/get_all_canteens', async (req, res) => {
    try{
        let canteensCollection = req.db.get('canteens')
        let sortby = req.query.sortby
        
        let docs = await canteensCollection.find()
        for (let canteen of docs){
            console.log(canteen._id)
            canteen.ranking = await queryfunc.getRanking(req.db, canteen._id)
            canteen.congestionRanking = await queryfunc.getCongestionRanking(req.db, canteen._id, EFFECTIVE_TIMESPAN_IN_MINUTES)
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


router.get('/menuImage', async (req, res) => {
    let db = req.db
    let canteenID = req.query.canteenID
    let url = await queryfunc.getMenuImage(db, canteenID)
    res.json(url)
})



//POST update functions
router.post('/postcomment', async function(req, res){
    /* 
    POST syntax:
    applications/json
    {
        canteenID: str,
        userID: str,
        content: str - the content of the review,
        ranking: int32 - ranking out of 5,
        time: str in format of "<YYYY-MM-DD>T<hh:mm:ss>+08:00", for example, "2022-11-20T09:50:00.000+08:00"
    }
    */
    let commentsCollection = req.db.get('comments')
    let info = req.body
    info.time = new Date(info.time)  // Change into date format

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
        congestionRanking: int32 - ranking out of 5,
        time: str in format of "<YYYY-MM-DD>T<hh:mm:ss>+08:00", for example, "2022-11-20T09:50:00.000+08:00"
    }
    */
    let congestionReportsCollection = req.db.get('congestionReports')
    let info = req.body
    info.time = new Date(info.time)  // Change into date format

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