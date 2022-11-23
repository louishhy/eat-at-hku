let express = require('express')
let router = express.Router()


router.get('/info', function(req, res, next){
    res.send("This is the login router.")
})

router.post('/verification', async (req, res) => {
    let usersCollection = req.db.get('users')
    let info = req.body
    let username = info.username
    let password = info.password
    let docs = await usersCollection.find({"username": username, "password": password})
    console.log(`Matches: ${docs.length}, for username ${username} password ${password}`)
    if (docs.length != 0){
        res.json({"result": "success"})
    }
    else{
        res.json({"result": "failed"})
    }
})

module.exports = router