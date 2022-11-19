let express = require('express')
let router = express.Router()


router.get('/get_all_canteens', async (req, res) => {
    try{
        let canteensCollection = req.db.get('canteens')
        let sortby = req.query.sortby
        
        let docs = await canteensCollection.find()
        if (sortby == "alphabetical"){
            docs.sort((a, b) => {
                if (a.canteenName < b.canteenName) return -1
                else return 1
            })
        }
        else if (sortby == "ranking"){
            // TODO implement ranking calculation function.
        }
        else if (sortby == "congestion"){
            // TODO implement congestion calculation function.
        }
        
        res.json(docs)
        
    } 
    catch (err){
        console.trace()
        res.status(500).send("Internal db error")
    }
})

module.exports = router