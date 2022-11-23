const MS_PER_MINUTE = 60000


// Helper function to query the comments related to the canteen id.
async function getComments(database, canteenID){
    try{
        let commentsCollection = database.get('comments')
        let docs = await commentsCollection.find({'canteenID': canteenID.toString()})
        console.log(docs)
        return docs
    }
    catch(err){
        console.trace()
    }
}


async function getCongestionReports(database, canteenID, effectiveTimespanInMinutes){
    if (effectiveTimespanInMinutes <= 0){
        let docs = await congestionReportsCollection.find({"canteenID": canteenID.toString()})
        return docs
    }
    let now = new Date()
    let startTime = new Date(now - effectiveTimespanInMinutes * MS_PER_MINUTE)
    try{
        let congestionReportsCollection = database.get('congestionReports')
        let docs = await congestionReportsCollection.find({
            "canteenID": canteenID.toString(), 
            "time": {$gte: startTime, $lt: now}
        })
        return docs
    }
    catch(err){
        console.trace(err)
    }
}


async function getRanking(database, canteenID){
    let commentDocs = await getComments(database, canteenID)
    if (commentDocs.length == 0) return 0

    let totalRanking = 0
    for (let comment of commentDocs){
        totalRanking += comment.ranking
    }

    return totalRanking / (commentDocs.length)
}


async function getCongestionRanking(database, canteenID, effectiveTimespanInMinutes){
    console.log(canteenID + " " + effectiveTimespanInMinutes)
    let reports = await getCongestionReports(database, canteenID, effectiveTimespanInMinutes)
    console.log(`Reports: ${reports}`)
    if (reports.length == 0) return 0

    let totalCongestionRanking = 0
    for (let report of reports){
        totalCongestionRanking += report.congestionRanking
    }
    return totalCongestionRanking / reports.length
}


async function getMenuImage(database, canteenID){
    let menuImagesCollection = database.get('menuImages')
    let docs = await menuImagesCollection.find({"canteenID": canteenID.toString()})
    if (docs.length == 0){
        return null
    }
    else return docs[0].url
}

module.exports = { getComments, getRanking, getCongestionRanking, getCongestionReports, getMenuImage }