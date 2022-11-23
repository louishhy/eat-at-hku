let express = require('express')
let router = express.Router()

router.get('/info', function(req, res, next){
    res.send("This is the signup router.")
})

router.post('/', async function(req, res){
    let usersCollection = req.db.get('users')
    let info = req.body
    let username = info.username
    let password = info.password
    let isAdmin = info.isAdmin      // In the format of string "true" and "false"
    // Ensure the username does not exist.
    let docs = await usersCollection.find({"username": username})
    if (docs.length != 0){
        res.json({"result": "exists"})
        return
    }

    try{
        await usersCollection.insert({"username": username, "password": password, "isAdmin": isAdmin})
    }
    catch (err){
        console.trace(err)
        res.status(500).send("Error in inserting the record")
    }
    
    res.json({"result": "succeed"})
})

module.exports = router